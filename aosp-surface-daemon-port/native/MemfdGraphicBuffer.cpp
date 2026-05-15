// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 3): memfd-backed GraphicBuffer impl.
//
// Substitutes the AOSP gralloc allocation path with `memfd_create + ftruncate
// + F_ADD_SEALS`.  CR33 (docs/engine/CR33_M6_SPIKE_REPORT.md) validated all
// substrate primitives on cfb7c9e3 — Phase A (memfd self-test), B (gralloc
// CPU coherency reference), C (AHB cross-process), D (memfd cross-process
// via SCM_RIGHTS).  None of the four counter-factual pivots are needed.
//
// The class lives entirely in the surface daemon process.  When a per-slot
// GraphicBuffer is shipped to a client via REQUEST_BUFFER, we:
//
//   1. write the flattened header bytes (13 int32s + 4 native_handle ints
//      = 17 int32s = 68 bytes) into the parcel as inline payload, and
//   2. write the memfd fd into the parcel via Parcel::writeFileDescriptor,
//      which dups it through the binder fd table replication path the
//      kernel handles automatically — CR33 §2.5 / §2.6 confirmed end-to-end.
//
// The client side calls `GraphicBuffer::unflatten` which (a) reads the
// 13-int header + numFds/numInts, (b) calls `native_handle_create(numFds=1,
// numInts=4)` and copies fds + ints into it, (c) calls `registerBuffer` on
// the resulting handle.  Because Phase-1 callers' libgui-side gralloc
// (Android-15 on the phone) sees a synthetic handle with no allocator
// metadata, the `registerBuffer` path may NACK on real gralloc tracking —
// but the buffer's `lock(usage, &vaddr)` still works (it just mmaps the
// fd, see CR33 §5.1 skeleton).  HWUI's CPU-render path needs only this.

// _GNU_SOURCE is set at the compile-flag level (Makefile).
#include "MemfdGraphicBuffer.h"

#include <errno.h>
#include <fcntl.h>
#include <linux/memfd.h>
#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/syscall.h>
#include <unistd.h>

#include <binder/Parcel.h>

namespace android {

namespace {

// Some bionic versions expose memfd_create as a libc symbol; we drop to the
// syscall for portability across Android 9/10/11/15 ABI.
int memfd_create_syscall(const char* name, unsigned int flags) {
#ifdef __NR_memfd_create
    return static_cast<int>(syscall(__NR_memfd_create, name, flags));
#else
    errno = ENOSYS;
    return -1;
#endif
}

// Phase-1: every supported format is 4 bytes-per-pixel (RGBA8888, RGBX8888,
// BGRA8888).  Apps that request anything else still get 4 bpp — we expand
// here once we add YUV support in Phase 2.
inline size_t bytesPerPixelFor(int32_t /*format*/) {
    return 4;
}

}  // namespace

MemfdGraphicBuffer::MemfdGraphicBuffer(uint32_t width, uint32_t height, int32_t format)
    : mWidth(width), mHeight(height), mFormat(format),
      mStride(pickStride(width)),
      mSizeBytes(static_cast<size_t>(pickStride(width)) * height * bytesPerPixelFor(format)),
      mFd(-1) {
    fprintf(stderr,
            "[wlk-mgb] ctor %ux%u fmt=%d stride=%u bytes=%zu (lazy; no memfd yet)\n",
            mWidth, mHeight, mFormat, mStride, mSizeBytes);
}

MemfdGraphicBuffer::~MemfdGraphicBuffer() {
    if (mFd >= 0) {
        fprintf(stderr, "[wlk-mgb] dtor closing memfd=%d\n", mFd);
        close(mFd);
        mFd = -1;
    }
}

status_t MemfdGraphicBuffer::ensureAllocated() {
    if (mFd >= 0) return NO_ERROR;

    int fd = memfd_create_syscall("westlake-gbuf", MFD_CLOEXEC | MFD_ALLOW_SEALING);
    if (fd < 0) {
        int e = errno;
        fprintf(stderr,
                "[wlk-mgb] memfd_create FAILED errno=%d (%s)\n", e, strerror(e));
        return -e;
    }
    if (ftruncate(fd, static_cast<off_t>(mSizeBytes)) != 0) {
        int e = errno;
        fprintf(stderr,
                "[wlk-mgb] ftruncate(%zu) FAILED errno=%d (%s)\n",
                mSizeBytes, e, strerror(e));
        close(fd);
        return -e;
    }
    // CR33 §2.3 confirmed F_ADD_SEALS works on this kernel.  Non-fatal if
    // it fails on a hypothetical older kernel — the buffer is still
    // usable, just not seal-protected against resize.
    if (fcntl(fd, F_ADD_SEALS, F_SEAL_SHRINK | F_SEAL_GROW) != 0) {
        fprintf(stderr,
                "[wlk-mgb] F_ADD_SEALS WARN errno=%d (%s) — non-fatal\n",
                errno, strerror(errno));
    }

    mFd = fd;
    fprintf(stderr,
            "[wlk-mgb] allocated memfd=%d size=%zu (%ux%u stride=%u fmt=%d)\n",
            mFd, mSizeBytes, mWidth, mHeight, mStride, mFormat);
    return NO_ERROR;
}

void MemfdGraphicBuffer::writeFlatPayload(int32_t* out) const {
    // Mirror frameworks/native/libs/ui/GraphicBuffer.cpp::flatten §364.
    memset(out, 0, kFlattenedSizeBytes);

    constexpr int32_t kMagicGB01 = 'GB01';
    out[0]  = kMagicGB01;
    out[1]  = static_cast<int32_t>(mWidth);
    out[2]  = static_cast<int32_t>(mHeight);
    out[3]  = static_cast<int32_t>(mStride);
    out[4]  = mFormat;
    out[5]  = 1;                       // layerCount
    out[6]  = 0;                       // usage low32 — Phase 1: no gralloc flags
    out[7]  = static_cast<int32_t>(id() >> 32);
    out[8]  = static_cast<int32_t>(id() & 0xFFFFFFFFull);
    out[9]  = 0;                       // generationNumber
    out[10] = static_cast<int32_t>(kNumFds);
    out[11] = static_cast<int32_t>(kNumInts);
    out[12] = 0;                       // usage high32 — Phase 1

    // native_handle ints (4): width, height, stride, format.  M6 plan §5.2
    // schema; the client side will see these in handle->data[numFds..].
    out[13] = static_cast<int32_t>(mWidth);
    out[14] = static_cast<int32_t>(mHeight);
    out[15] = static_cast<int32_t>(mStride);
    out[16] = mFormat;
}

status_t MemfdGraphicBuffer::writeToParcelAsGraphicBuffer(Parcel* reply) const {
    if (mFd < 0) {
        fprintf(stderr,
                "[wlk-mgb] writeToParcelAsGraphicBuffer called with no memfd "
                "— call ensureAllocated() first\n");
        return INVALID_OPERATION;
    }

    // Parcel::write(Flattenable&) wire format (see
    // aosp-libbinder-port/aosp-src/libbinder/Parcel.cpp §1777):
    //   int32 len     = flattened byte count
    //   int32 fdCount = number of fds
    //   `len` bytes of flat payload (writeInplace)
    //   `fdCount` file descriptors (writeDupFileDescriptor)
    status_t err = reply->writeInt32(static_cast<int32_t>(kFlattenedSizeBytes));
    if (err != NO_ERROR) return err;
    err = reply->writeInt32(static_cast<int32_t>(kFlattenedFdCount));
    if (err != NO_ERROR) return err;

    void* dst = reply->writeInplace(kFlattenedSizeBytes);
    if (dst == nullptr) return NO_MEMORY;
    writeFlatPayload(reinterpret_cast<int32_t*>(dst));

    // writeDupFileDescriptor dups the fd through the binder driver's
    // fd-table replication path (BR_TRANSACTION_FD / sec_ctx etc.) — same
    // mechanism CR33 Phase C/D validated for cross-process buffer handoff.
    err = reply->writeDupFileDescriptor(mFd);
    if (err != NO_ERROR) {
        fprintf(stderr,
                "[wlk-mgb] writeDupFileDescriptor(memfd=%d) FAILED err=%d\n",
                mFd, err);
        return err;
    }
    return NO_ERROR;
}

}  // namespace android
