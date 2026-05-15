// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 3: memfd-backed GraphicBuffer substitute)
//
// `MemfdGraphicBuffer` is a daemon-side container for a single buffer slot
// owned by a `WestlakeGraphicBufferProducer`.  It mirrors just enough of
// `android::GraphicBuffer`'s state to:
//
//   1. Lazily allocate a memfd of `stride * height * 4` bytes (RGBA8888) the
//      first time a slot is DEQUEUEd.  Memfd allocation is what CR33's
//      spike validated as cross-process viable on the OnePlus 6 / kernel
//      4.9.337 / LineageOS 22 substrate (see docs/engine/CR33_M6_SPIKE_REPORT.md).
//
//   2. Flatten itself into a Parcel reply matching AOSP-11's
//      `GraphicBuffer::flatten` wire format (the 13-int header + numFds +
//      numInts schema; see frameworks/native/libs/ui/GraphicBuffer.cpp §364).
//      The receiving side calls `Parcel::read(GraphicBuffer&)` which goes
//      through `GraphicBuffer::unflatten` and reconstructs the buffer.
//
// We do NOT depend on `<ui/GraphicBuffer.h>` here — the surface daemon links
// only against libbinder + libutils/libcutils, NOT libui.  Instead we
// hand-emit the flattened bytes the same way AOSP's GraphicBuffer.cpp does,
// then write the memfd fd into the Parcel as a Binder-marshaled file
// descriptor via `Parcel::write(Flattenable&)`'s standard path (length /
// fdCount header followed by inline bytes and dup'd fds).
//
// Companion docs:
//   - docs/engine/CR33_M6_SPIKE_REPORT.md  §5 (memfd skeleton)
//   - docs/engine/M6_SURFACE_DAEMON_PLAN.md §5 (substitution strategy)
//   - docs/engine/M6_STEP3_REPORT.md         (this CR)
//
// Anti-drift: zero per-app branches.  All apps get the same memfd-backed
// substrate from the daemon.

#ifndef WESTLAKE_MEMFD_GRAPHIC_BUFFER_H
#define WESTLAKE_MEMFD_GRAPHIC_BUFFER_H

#include <stddef.h>
#include <stdint.h>

#include <binder/Parcel.h>
#include <utils/Errors.h>
#include <utils/RefBase.h>

namespace android {

// Phase-1 supports only the most common Android client-pixel-format: RGBA8888.
// AOSP `HAL_PIXEL_FORMAT_RGBA_8888 = 1` (see
// system/core/include/system/graphics-base-v1.0.h).  Apps that request other
// formats fall through to the same byte count (bpp=4) for Phase 1.
constexpr int32_t kHalPixelFormatRgba8888 = 1;

// AOSP-11 stride convention: gralloc picks `align(width, 64)`.  CR33 spike
// §2.4 confirmed sdm845's gralloc gives 1088 for a 1080-wide allocation —
// matching this convention exactly.  HWUI / libgui's stride-handling code
// already reads stride out of the GraphicBuffer wrapper, so as long as we
// pick the same convention the renderer is happy.
inline uint32_t pickStride(uint32_t width) {
    return (width + 63u) & ~63u;
}

class MemfdGraphicBuffer : public RefBase {
public:
    // Lazy-alloc constructor: records dimensions only; the memfd is allocated
    // on `ensureAllocated()`.
    MemfdGraphicBuffer(uint32_t width, uint32_t height, int32_t format);

    ~MemfdGraphicBuffer() override;

    // Allocate the memfd + ftruncate + F_ADD_SEALS if not already done.
    // Idempotent; returns NO_ERROR on success or an errno-style negative.
    status_t ensureAllocated();

    // Returns the memfd fd (>= 0) once `ensureAllocated()` has succeeded.
    // -1 otherwise.
    int memfd() const { return mFd; }

    uint32_t width()  const { return mWidth;  }
    uint32_t height() const { return mHeight; }
    uint32_t stride() const { return mStride; }
    int32_t  format() const { return mFormat; }
    size_t   sizeBytes() const { return mSizeBytes; }

    // Stable per-process id used in the flatten header `mId` field.  AOSP
    // uses a GraphicBufferAllocator-side counter; we use the memfd value
    // for determinism (fd uniqueness is per-process, sufficient for the
    // identity-only role mId plays in libgui's BufferQueueProducer).
    uint64_t id() const {
        return static_cast<uint64_t>(mFd >= 0 ? static_cast<uint32_t>(mFd) : 0u);
    }

    // Wire-format helpers — mirror AOSP `Flattenable` protocol so we can
    // emit a parcel payload that the receiver's
    // `Parcel::read(GraphicBuffer&)` => `GraphicBuffer::unflatten` consumes.
    //
    // Layout (see frameworks/native/libs/ui/GraphicBuffer.cpp §364):
    //   buf[0]  = 'GB01' magic (new 64-bit-usage format)
    //   buf[1]  = width
    //   buf[2]  = height
    //   buf[3]  = stride
    //   buf[4]  = format
    //   buf[5]  = layerCount        (1 — single layer, no layered RGBA)
    //   buf[6]  = usage low32       (0 — Phase-1 has no gralloc usage flags)
    //   buf[7]  = (mId >> 32)
    //   buf[8]  = (mId & 0xFFFFFFFF)
    //   buf[9]  = mGenerationNumber (0 — Phase 1)
    //   buf[10] = numFds           (1 — our memfd)
    //   buf[11] = numInts          (4 — width, height, stride, format —
    //                                see M6 plan §5.2 native_handle schema)
    //   buf[12] = usage high32     (0)
    //   buf[13..16] = native_handle.data ints (width, height, stride, format)
    //
    // Total = 13 + 4 = 17 int32 words = 68 bytes inline payload, plus 1 fd.
    static constexpr size_t kFlattenWordCount = 13;
    static constexpr size_t kNumInts = 4;
    static constexpr size_t kNumFds  = 1;
    static constexpr size_t kFlattenedSizeBytes =
            (kFlattenWordCount + kNumInts) * sizeof(int32_t);
    static constexpr size_t kFlattenedFdCount = kNumFds;

    // Write the flat byte payload (without the Parcel `len`/`fdCount` header
    // and without the fd itself — caller handles those via Parcel
    // helpers).  `out` must have at least kFlattenedSizeBytes addressable.
    void writeFlatPayload(int32_t* out) const;

    // Compose the full Parcel write of this buffer in the format
    // BpGraphicBufferProducer expects after a REQUEST_BUFFER:
    //   writeInt32(1)                  // "non-null"
    //   write(GraphicBuffer&)          // standard Flattenable parcel:
    //     int32 len  = kFlattenedSizeBytes
    //     int32 nFds = 1
    //     17 int32s of payload (writeInplace)
    //     1 file descriptor (writeDupFileDescriptor)
    //
    // Returns NO_ERROR or the underlying Parcel error.
    status_t writeToParcelAsGraphicBuffer(Parcel* reply) const;

private:
    uint32_t mWidth;
    uint32_t mHeight;
    int32_t  mFormat;
    uint32_t mStride;     // pickStride(width)
    size_t   mSizeBytes;  // stride * height * bytes-per-pixel
    int      mFd;         // memfd; -1 until ensureAllocated() succeeds
};

}  // namespace android

#endif  // WESTLAKE_MEMFD_GRAPHIC_BUFFER_H
