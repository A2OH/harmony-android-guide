// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 3: IGraphicBufferProducer Bn-side stub)
//
// One `WestlakeGraphicBufferProducer` per `ISurfaceComposerClient::CREATE_SURFACE`.
// Implements the 12 Tier-1 transactions enumerated in
// `M6_SURFACE_DAEMON_PLAN.md §2.4` (corresponding to AOSP-11's
// `BnGraphicBufferProducer::onTransact` enum values from
// `frameworks/native/libs/gui/IGraphicBufferProducer.cpp` §50-77).
//
// Slot model (Phase 1, M6 plan §4):
//   - 2-slot double-buffer ring.  Each slot lazily holds one
//     `MemfdGraphicBuffer` once first DEQUEUEd.
//   - States: FREE → DEQUEUED → QUEUED → FREE (consumer takes it back).
//   - REQUEST_BUFFER returns the per-slot MemfdGraphicBuffer flattened into
//     the parcel reply.
//
// Wire-format note: this is NOT a SafeInterface — IGraphicBufferProducer
// uses the older "Bp-side hand-marshals + Bn-side switches on opcode"
// pattern.  See AOSP-11 BpGraphicBufferProducer in IGraphicBufferProducer.cpp
// §79 for the Bp-side wire layout each opcode expects.
//
// Anti-drift: zero per-app branches.  Every surface gets the same 2-slot
// memfd-backed ring.

#ifndef WESTLAKE_GRAPHIC_BUFFER_PRODUCER_H
#define WESTLAKE_GRAPHIC_BUFFER_PRODUCER_H

#include <stdint.h>

#include <condition_variable>
#include <mutex>

#include <binder/Binder.h>
#include <binder/Parcel.h>
#include <utils/Errors.h>
#include <utils/RefBase.h>
#include <utils/String16.h>

#include "MemfdGraphicBuffer.h"

namespace android {

class WestlakeGraphicBufferProducer : public BBinder {
public:
    // Default canned dimensions if CREATE_SURFACE didn't carry valid ones
    // (e.g., a 0x0 placeholder).  Phase-1 panel envelope from
    // `WestlakeSurfaceComposer.cpp` § kCanned*.
    static constexpr uint32_t kDefaultWidth  = 1080;
    static constexpr uint32_t kDefaultHeight = 2280;
    static constexpr int32_t  kDefaultFormat = kHalPixelFormatRgba8888;

    WestlakeGraphicBufferProducer(uint32_t width, uint32_t height, int32_t format);
    ~WestlakeGraphicBufferProducer() override;

    status_t onTransact(uint32_t code, const Parcel& data, Parcel* reply,
                        uint32_t flags = 0) override;

    const String16& getInterfaceDescriptor() const override;

    // AOSP-11 BnGraphicBufferProducer transaction tags, verbatim from
    // frameworks/native/libs/gui/IGraphicBufferProducer.cpp §50-77.
    // FIRST_CALL_TRANSACTION = 1.
    enum Tag : uint32_t {
        REQUEST_BUFFER                = 1,
        DEQUEUE_BUFFER                = 2,
        DETACH_BUFFER                 = 3,
        DETACH_NEXT_BUFFER            = 4,
        ATTACH_BUFFER                 = 5,
        QUEUE_BUFFER                  = 6,
        CANCEL_BUFFER                 = 7,
        QUERY                         = 8,
        CONNECT                       = 9,
        DISCONNECT                    = 10,
        SET_SIDEBAND_STREAM           = 11,
        ALLOCATE_BUFFERS              = 12,
        ALLOW_ALLOCATION              = 13,
        SET_GENERATION_NUMBER         = 14,
        GET_CONSUMER_NAME             = 15,
        SET_MAX_DEQUEUED_BUFFER_COUNT = 16,
        SET_ASYNC_MODE                = 17,
        SET_SHARED_BUFFER_MODE        = 18,
        SET_AUTO_REFRESH              = 19,
        SET_DEQUEUE_TIMEOUT           = 20,
        GET_LAST_QUEUED_BUFFER        = 21,
        GET_FRAME_TIMESTAMPS          = 22,
        GET_UNIQUE_ID                 = 23,
        GET_CONSUMER_USAGE            = 24,
        SET_LEGACY_BUFFER_DROP        = 25,
        SET_AUTO_PREROTATION          = 26,
    };

    // 2-slot ring buffer.  Phase-1 sized for the most common
    // double-buffered software-render path; M6 plan §4.4 documents that
    // expanding to 3 will require an `ATTACH/DETACH`-aware path.
    static constexpr int kNumSlots = 2;

    enum SlotState : uint8_t { FREE = 0, DEQUEUED = 1, QUEUED = 2 };

    struct Slot {
        sp<MemfdGraphicBuffer> buf;
        SlotState state{FREE};
        uint64_t frameNumber{0};   // monotonic; incremented on each QUEUE
    };

    // M6-Step4: consumer-side hooks.  A DlstConsumer instance polls these.
    //
    //   takeQueuedSlot(): blocks until at least one slot is QUEUED OR `wake`
    //                     is called.  Returns the slot index (>=0) or -1 on
    //                     wake/shutdown.  The slot remains in QUEUED state
    //                     until releaseSlot() is called — so the consumer
    //                     can mmap the memfd race-free.
    //
    //   releaseSlot(idx): transitions the named slot QUEUED → FREE and
    //                     notifies any DEQUEUE waiters (back-pressure path).
    //
    //   wake():           unblocks any takeQueuedSlot() callers so the
    //                     consumer thread can exit cleanly on shutdown.
    //
    //   snapshotSlotMemfd(): atomic snapshot of (memfd, w, h, stride, size)
    //                        for the named slot.  Returns true on success.
    //                        Consumer uses this to mmap without holding mLock
    //                        during the syscall.
    int      takeQueuedSlot();
    void     releaseSlot(int idx);
    void     wake();
    bool     snapshotSlotMemfd(int idx, int* outFd, uint32_t* outW,
                               uint32_t* outH, uint32_t* outStride,
                               size_t* outSize, uint64_t* outFrameNumber) const;

    uint32_t width()  const { return mWidth;  }
    uint32_t height() const { return mHeight; }
    int32_t  format() const { return mFormat; }

private:
    // Per-transaction handlers — each verifies the interface header and
    // writes a well-formed reply matching the AOSP-11 Bp-side reader.
    status_t onRequestBuffer(const Parcel& data, Parcel* reply);
    status_t onSetMaxDequeuedBufferCount(const Parcel& data, Parcel* reply);
    status_t onSetAsyncMode(const Parcel& data, Parcel* reply);
    status_t onDequeueBuffer(const Parcel& data, Parcel* reply);
    status_t onDetachBuffer(const Parcel& data, Parcel* reply);
    status_t onAttachBuffer(const Parcel& data, Parcel* reply);
    status_t onQueueBuffer(const Parcel& data, Parcel* reply);
    status_t onCancelBuffer(const Parcel& data, Parcel* reply);
    status_t onQuery(const Parcel& data, Parcel* reply);
    status_t onConnect(const Parcel& data, Parcel* reply);
    status_t onDisconnect(const Parcel& data, Parcel* reply);
    status_t onSetGenerationNumber(const Parcel& data, Parcel* reply);

    // Slot ring + helpers.
    int findFreeSlotLocked();
    status_t ensureSlotAllocatedLocked(int slot, uint32_t width, uint32_t height,
                                       int32_t format);

    // Write a default-constructed `QueueBufferOutput` flattenable into the
    // reply.  Used by CONNECT / QUEUE_BUFFER replies.  Default contents:
    //   width, height, transformHint=0, numPendingBuffers=0,
    //   nextFrameNumber=0, bufferReplaced=false, maxBufferCount=kNumSlots,
    //   frameTimestamps = empty FrameEventHistoryDelta (size 0, default
    //   CompositorTiming {deadline=0, interval=16666667, presentLatency=16666667}).
    status_t writeQueueBufferOutput(Parcel* reply) const;

    // Write a `Fence` flattenable representing NO_FENCE (numFds=0).
    status_t writeNoFence(Parcel* reply) const;

    // Cached canned dimensions captured from the CREATE_SURFACE call.
    uint32_t mWidth;
    uint32_t mHeight;
    int32_t  mFormat;

    // M6-Step4: protect mSlots and observable state.  Step-3's per-method
    // handlers ran without locking because the daemon's thread-pool tends to
    // serialize per-binder transactions, but with a dedicated consumer thread
    // (and Phase-2 multi-threaded apps), we need explicit mutual exclusion.
    mutable std::mutex mLock;
    // Producer signals this when a slot transitions to QUEUED or on wake().
    // Consumer waits on it in takeQueuedSlot().
    std::condition_variable mProducerCv;
    // Producer waits on this when no slot is FREE (DEQUEUE back-pressure).
    // Consumer signals it after a release.
    std::condition_variable mConsumerCv;
    // Monotonic frame counter; incremented on every QUEUE_BUFFER.
    uint64_t mNextFrameNumber{1};
    // Set by wake(); takeQueuedSlot() returns -1 promptly while this is true.
    bool mWoken{false};

    Slot mSlots[kNumSlots];
};

}  // namespace android

#endif  // WESTLAKE_GRAPHIC_BUFFER_PRODUCER_H
