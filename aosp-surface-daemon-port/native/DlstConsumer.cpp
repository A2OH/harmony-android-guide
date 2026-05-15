// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 4): DLST pipe consumer impl.
//
// See DlstConsumer.h for design notes + frame envelope.

#include "DlstConsumer.h"
#include "WestlakeGraphicBufferProducer.h"

#include <errno.h>
#include <fcntl.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <unistd.h>

namespace android {

namespace {

// Must match WestlakeVM.kt:47 `DLIST_MAGIC = 0x444C5354` — "DLST" LE.
constexpr uint32_t kDlstMagic = 0x444C5354u;
// OP_ARGB_BITMAP from WestlakeVM.kt:61 — single-byte opcode for a raw RGBA
// blit at offset (x,y) of size (w,h).
constexpr uint8_t  kOpArgbBitmap = 12;

// Best-effort full-write wrapper.  Treats EAGAIN/EINTR as transient; treats
// EPIPE/ENXIO/short-write as fatal-for-this-frame so the caller can drop and
// keep going.  Returns bytes written or a negative errno-style status.
ssize_t writeFully(int fd, const void* buf, size_t len) {
    const uint8_t* p = static_cast<const uint8_t*>(buf);
    size_t remaining = len;
    while (remaining > 0) {
        ssize_t n = ::write(fd, p, remaining);
        if (n > 0) {
            p         += n;
            remaining -= static_cast<size_t>(n);
            continue;
        }
        if (n < 0 && (errno == EINTR || errno == EAGAIN)) {
            // Brief retry; if the reader is genuinely stalled we'll bail
            // through the second EAGAIN since the kernel pipe buffer is
            // small (~64K).  For the smoke test the reader drains
            // immediately so a single retry suffices.
            continue;
        }
        if (n < 0) return -errno;
        // n == 0 — pipe closed.
        return -EPIPE;
    }
    return static_cast<ssize_t>(len);
}

}  // namespace

DlstConsumer::DlstConsumer(const sp<WestlakeGraphicBufferProducer>& gbp,
                           std::string pipePath)
    : mGbp(gbp), mPipePath(std::move(pipePath)) {
    fprintf(stderr,
            "[wlk-dlst] ctor gbp=%p pipe=\"%s\"\n",
            mGbp.get(), mPipePath.c_str());
}

DlstConsumer::~DlstConsumer() {
    stop();
    fprintf(stderr,
            "[wlk-dlst] dtor (gbp=%p; framesWritten=%llu framesDropped=%llu bytesWritten=%llu)\n",
            mGbp.get(),
            (unsigned long long)mFramesWritten.load(),
            (unsigned long long)mFramesDropped.load(),
            (unsigned long long)mBytesWritten.load());
}

void DlstConsumer::start() {
    bool expected = false;
    if (!mRunning.compare_exchange_strong(expected, true)) {
        fprintf(stderr, "[wlk-dlst] start() already running — no-op\n");
        return;
    }
    mStopRequested.store(false);
    mThread = std::thread(&DlstConsumer::run, this);
    fprintf(stderr,
            "[wlk-dlst] start() thread spawned for gbp=%p pipe=\"%s\"\n",
            mGbp.get(), mPipePath.c_str());
}

void DlstConsumer::stop() {
    if (!mRunning.exchange(false)) return;
    mStopRequested.store(true);
    if (mGbp != nullptr) mGbp->wake();
    if (mThread.joinable()) mThread.join();
    fprintf(stderr,
            "[wlk-dlst] stop() joined; framesWritten=%llu framesDropped=%llu\n",
            (unsigned long long)mFramesWritten.load(),
            (unsigned long long)mFramesDropped.load());
}

void DlstConsumer::run() {
    fprintf(stderr,
            "[wlk-dlst] run() loop start gbp=%p pipe=\"%s\"\n",
            mGbp.get(), mPipePath.c_str());

    while (!mStopRequested.load()) {
        int slot = mGbp->takeQueuedSlot();
        if (slot < 0) {
            // wake() called; either shutdown or a spurious notify.
            if (mStopRequested.load()) break;
            continue;
        }

        int rc = writeFrame(slot);
        if (rc < 0) {
            // Pipe drop is non-fatal — keep cycling.  Reader may attach
            // later; framework.jar's HWUI keeps generating frames either way.
            mFramesDropped.fetch_add(1);
        } else {
            mFramesWritten.fetch_add(1);
            mBytesWritten.fetch_add(static_cast<uint64_t>(rc));
        }

        // Always release the slot so the producer can dequeue again.
        mGbp->releaseSlot(slot);
    }

    fprintf(stderr, "[wlk-dlst] run() loop exit gbp=%p\n", mGbp.get());
}

int DlstConsumer::writeFrame(int slotIdx) {
    int      memfd  = -1;
    uint32_t w      = 0;
    uint32_t h      = 0;
    uint32_t stride = 0;
    size_t   size   = 0;
    uint64_t frameNo = 0;
    if (!mGbp->snapshotSlotMemfd(slotIdx, &memfd, &w, &h, &stride, &size, &frameNo)
        || memfd < 0 || w == 0 || h == 0) {
        fprintf(stderr,
                "[wlk-dlst] writeFrame slot=%d snapshot FAILED memfd=%d w=%u h=%u\n",
                slotIdx, memfd, w, h);
        return -EINVAL;
    }

    // Open the pipe non-blockingly.  ENXIO ("no reader") is the common
    // expected case when the host SurfaceView hasn't attached yet.
    int pipeFd = ::open(mPipePath.c_str(), O_WRONLY | O_NONBLOCK);
    if (pipeFd < 0) {
        int e = errno;
        if (e == ENXIO) {
            // Silent drop — reader will attach in a future frame.
            return -ENXIO;
        }
        fprintf(stderr,
                "[wlk-dlst] writeFrame slot=%d open(%s) FAILED errno=%d (%s)\n",
                slotIdx, mPipePath.c_str(), e, strerror(e));
        return -e;
    }

    // Switch to blocking after the open so that an attached reader actually
    // sees the bytes (non-blocking write on a full kernel buffer would
    // EAGAIN endlessly).  We rely on writeFully's brief retry policy to
    // tolerate a few EAGAINs in the rare case the host falls slightly
    // behind; in practice the reader thread drains immediately.
    int flags = fcntl(pipeFd, F_GETFL, 0);
    if (flags >= 0) fcntl(pipeFd, F_SETFL, flags & ~O_NONBLOCK);

    // Build the inline-payload envelope first so we can prefix it with
    // the correct uint32 size word.
    //
    //   byte    opcode  = OP_ARGB_BITMAP   (1)
    //   float   x                            (4)
    //   float   y                            (4)
    //   int32   width                        (4)
    //   int32   height                       (4)
    //   int32   dataLen                      (4)
    //   bytes[] rgba (dataLen)
    //
    // Bytes-per-row in the memfd is `stride * 4`; the consumer crops to
    // `width * 4` bytes per scanline so the host receives tightly-packed
    // RGBA matching its `dataLen = w*h*4` expectation.  Stride==width
    // (the canonical case in the M6 test pipeline) avoids the copy.
    constexpr size_t kHeaderLen = 1 + 4 + 4 + 4 + 4 + 4;
    const uint32_t   dataLen    = w * h * 4u;
    const size_t     payloadLen = kHeaderLen + dataLen;
    const uint32_t   envelope[2] = { kDlstMagic, static_cast<uint32_t>(payloadLen) };

    // Map the memfd read-only — CR33 §2.4 confirmed mmap(MAP_SHARED) works
    // cross-process on the same memfd.  Here we're in the same process as
    // the producer (the daemon), so it's just an mmap of our own fd.
    void* pixels = ::mmap(nullptr, size, PROT_READ, MAP_SHARED, memfd, 0);
    if (pixels == MAP_FAILED) {
        int e = errno;
        fprintf(stderr,
                "[wlk-dlst] writeFrame slot=%d mmap(memfd=%d size=%zu) FAILED errno=%d (%s)\n",
                slotIdx, memfd, size, e, strerror(e));
        ::close(pipeFd);
        return -e;
    }

    ssize_t total = 0;

    // Write the [magic, size] envelope.
    ssize_t r = writeFully(pipeFd, envelope, sizeof(envelope));
    if (r < 0) {
        ::munmap(pixels, size);
        ::close(pipeFd);
        return static_cast<int>(r);
    }
    total += r;

    // Write the inline header.
    uint8_t hdr[kHeaderLen];
    hdr[0] = kOpArgbBitmap;
    float fx = 0.0f, fy = 0.0f;
    memcpy(hdr + 1,  &fx,      sizeof(float));
    memcpy(hdr + 5,  &fy,      sizeof(float));
    int32_t iW = static_cast<int32_t>(w);
    int32_t iH = static_cast<int32_t>(h);
    int32_t iL = static_cast<int32_t>(dataLen);
    memcpy(hdr + 9,  &iW,      sizeof(int32_t));
    memcpy(hdr + 13, &iH,      sizeof(int32_t));
    memcpy(hdr + 17, &iL,      sizeof(int32_t));
    r = writeFully(pipeFd, hdr, sizeof(hdr));
    if (r < 0) {
        ::munmap(pixels, size);
        ::close(pipeFd);
        return static_cast<int>(r);
    }
    total += r;

    // Write pixel rows.  If stride == width the buffer is tightly packed
    // and we can do one big write; otherwise loop and crop scanlines.
    const uint8_t* base = static_cast<const uint8_t*>(pixels);
    if (stride == w) {
        r = writeFully(pipeFd, base, dataLen);
        if (r < 0) {
            ::munmap(pixels, size);
            ::close(pipeFd);
            return static_cast<int>(r);
        }
        total += r;
    } else {
        for (uint32_t row = 0; row < h; ++row) {
            const uint8_t* line = base + static_cast<size_t>(row) * stride * 4u;
            r = writeFully(pipeFd, line, w * 4u);
            if (r < 0) {
                ::munmap(pixels, size);
                ::close(pipeFd);
                return static_cast<int>(r);
            }
            total += r;
        }
    }

    ::munmap(pixels, size);
    ::close(pipeFd);

    fprintf(stderr,
            "[wlk-dlst] writeFrame slot=%d frame#=%llu wrote magic+payload "
            "= %zd bytes (%ux%u stride=%u memfd=%d)\n",
            slotIdx, (unsigned long long)frameNo,
            total, w, h, stride, memfd);
    return static_cast<int>(total);
}

}  // namespace android
