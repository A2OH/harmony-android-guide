// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 3): IGraphicBufferProducer Bn impl.
//
// Phase-1 implementation of the 12 Tier-1 IGraphicBufferProducer
// transactions documented in `M6_SURFACE_DAEMON_PLAN.md §2.4`.
//
// Reply formats mirror AOSP-11 BpGraphicBufferProducer's per-opcode wire
// expectations exactly (frameworks/native/libs/gui/IGraphicBufferProducer.cpp).
//
// Slot model: 2-slot ring; per-slot `MemfdGraphicBuffer` lazily allocated
// on first DEQUEUE.  Each REQUEST_BUFFER flattens its slot's buffer into
// the reply using AOSP-11's GraphicBuffer wire format.
//
// Anti-drift: zero per-app branches; every IGBP instance behaves identically.

#include "WestlakeGraphicBufferProducer.h"

#include <stdio.h>
#include <string.h>

#include <binder/IInterface.h>
#include <binder/Parcel.h>
#include <utils/Errors.h>
#include <utils/String16.h>

namespace android {

// AOSP canonical descriptor for android.gui.IGraphicBufferProducer.
// Source: frameworks/native/libs/gui/IGraphicBufferProducer.cpp §697.
static const String16 kIgbpDescriptor("android.gui.IGraphicBufferProducer");

// AOSP `Fence::NO_FENCE` flattens to a single uint32 zero (numFds=0).
static constexpr size_t kFenceFlattenedSizeBytes = sizeof(uint32_t);
static constexpr size_t kFenceFdCount = 0;

// AOSP-11 `QueueBufferOutput` flattenable layout (see
// frameworks/native/libs/gui/QueueBufferInputOutput.cpp §111-138):
//   width (4) + height (4) + transformHint (4) + numPendingBuffers (4)
//   + nextFrameNumber (8) + bufferReplaced (1) + maxBufferCount (4)
//   + FrameEventHistoryDelta { CompositorTiming (24) + uint32 size=0 (4) }
// = 29 + 28 = 57 bytes.  No fds.
//
// CompositorTiming default = {0, 16666667, 16666667} (see
// frameworks/native/libs/gui/include/gui/FrameTimestamps.h §99).
struct __attribute__((packed)) DefaultQueueBufferOutputFlat {
    uint32_t width;
    uint32_t height;
    uint32_t transformHint;
    uint32_t numPendingBuffers;
    uint64_t nextFrameNumber;
    uint8_t  bufferReplaced;
    uint32_t maxBufferCount;
    // FrameEventHistoryDelta:
    int64_t  ct_deadline;
    int64_t  ct_interval;
    int64_t  ct_presentLatency;
    uint32_t deltaSize;
};
static_assert(sizeof(DefaultQueueBufferOutputFlat) == 57,
              "QueueBufferOutput flatten layout must be 57 bytes");

static constexpr size_t kQueueBufferOutputFlattenedSize =
        sizeof(DefaultQueueBufferOutputFlat);
static constexpr size_t kQueueBufferOutputFdCount = 0;

WestlakeGraphicBufferProducer::WestlakeGraphicBufferProducer(uint32_t width,
                                                              uint32_t height,
                                                              int32_t format)
    : mWidth(width > 0 ? width : kDefaultWidth),
      mHeight(height > 0 ? height : kDefaultHeight),
      mFormat(format != 0 ? format : kDefaultFormat) {
    fprintf(stderr,
            "[wlk-igbp] ctor %ux%u fmt=%d slots=%d\n",
            mWidth, mHeight, mFormat, kNumSlots);
}

WestlakeGraphicBufferProducer::~WestlakeGraphicBufferProducer() {
    fprintf(stderr, "[wlk-igbp] dtor (this=%p)\n", this);
}

const String16& WestlakeGraphicBufferProducer::getInterfaceDescriptor() const {
    return kIgbpDescriptor;
}

// ----------------------------------------------------------------------------
// Helpers.
// ----------------------------------------------------------------------------

int WestlakeGraphicBufferProducer::findFreeSlotLocked() {
    // Prefer FREE slots; if all are DEQUEUED/QUEUED we return -1 and the
    // caller can choose to NACK with NO_MEMORY-equivalent BUFFER_QUEUED.
    for (int i = 0; i < kNumSlots; ++i) {
        if (mSlots[i].state == FREE) return i;
    }
    return -1;
}

// ----------------------------------------------------------------------------
// M6-Step4: consumer-side hooks.
// ----------------------------------------------------------------------------

int WestlakeGraphicBufferProducer::takeQueuedSlot() {
    std::unique_lock<std::mutex> lk(mLock);
    mProducerCv.wait(lk, [this] {
        if (mWoken) return true;
        for (int i = 0; i < kNumSlots; ++i) {
            if (mSlots[i].state == QUEUED) return true;
        }
        return false;
    });
    if (mWoken) return -1;
    // Return the oldest QUEUED slot (lowest frameNumber).
    int oldestIdx = -1;
    uint64_t oldestFrame = UINT64_MAX;
    for (int i = 0; i < kNumSlots; ++i) {
        if (mSlots[i].state == QUEUED && mSlots[i].frameNumber < oldestFrame) {
            oldestIdx = i;
            oldestFrame = mSlots[i].frameNumber;
        }
    }
    return oldestIdx;
}

void WestlakeGraphicBufferProducer::releaseSlot(int idx) {
    if (idx < 0 || idx >= kNumSlots) return;
    {
        std::lock_guard<std::mutex> lk(mLock);
        if (mSlots[idx].state == QUEUED) {
            mSlots[idx].state = FREE;
        }
    }
    mConsumerCv.notify_all();
}

void WestlakeGraphicBufferProducer::wake() {
    {
        std::lock_guard<std::mutex> lk(mLock);
        mWoken = true;
    }
    mProducerCv.notify_all();
    mConsumerCv.notify_all();
}

bool WestlakeGraphicBufferProducer::snapshotSlotMemfd(
        int idx, int* outFd, uint32_t* outW, uint32_t* outH,
        uint32_t* outStride, size_t* outSize, uint64_t* outFrameNumber) const {
    if (idx < 0 || idx >= kNumSlots) return false;
    std::lock_guard<std::mutex> lk(mLock);
    const Slot& s = mSlots[idx];
    if (s.buf == nullptr) return false;
    if (outFd)          *outFd          = s.buf->memfd();
    if (outW)           *outW           = s.buf->width();
    if (outH)           *outH           = s.buf->height();
    if (outStride)      *outStride      = s.buf->stride();
    if (outSize)        *outSize        = s.buf->sizeBytes();
    if (outFrameNumber) *outFrameNumber = s.frameNumber;
    return true;
}

status_t WestlakeGraphicBufferProducer::ensureSlotAllocatedLocked(
        int slot, uint32_t width, uint32_t height, int32_t format) {
    if (slot < 0 || slot >= kNumSlots) return BAD_VALUE;
    auto& s = mSlots[slot];
    if (s.buf == nullptr) {
        // Lazy alloc on first DEQUEUE — matches AOSP BufferQueueProducer
        // behavior (a slot's GraphicBuffer materializes on first use).
        s.buf = sp<MemfdGraphicBuffer>::make(width, height, format);
    }
    return s.buf->ensureAllocated();
}

status_t WestlakeGraphicBufferProducer::writeQueueBufferOutput(Parcel* reply) const {
    // Parcel::write(Flattenable&) header.
    status_t err = reply->writeInt32(static_cast<int32_t>(kQueueBufferOutputFlattenedSize));
    if (err != NO_ERROR) return err;
    err = reply->writeInt32(static_cast<int32_t>(kQueueBufferOutputFdCount));
    if (err != NO_ERROR) return err;

    DefaultQueueBufferOutputFlat flat;
    memset(&flat, 0, sizeof(flat));
    flat.width = mWidth;
    flat.height = mHeight;
    flat.transformHint = 0;
    flat.numPendingBuffers = 0;
    flat.nextFrameNumber = 0;
    flat.bufferReplaced = 0;
    flat.maxBufferCount = kNumSlots;
    flat.ct_deadline = 0;
    flat.ct_interval = 16'666'667;       // 60 Hz, matches Step-2 canned
    flat.ct_presentLatency = 16'666'667;
    flat.deltaSize = 0;

    void* dst = reply->writeInplace(kQueueBufferOutputFlattenedSize);
    if (dst == nullptr) return NO_MEMORY;
    memcpy(dst, &flat, sizeof(flat));
    return NO_ERROR;
}

status_t WestlakeGraphicBufferProducer::writeNoFence(Parcel* reply) const {
    status_t err = reply->writeInt32(static_cast<int32_t>(kFenceFlattenedSizeBytes));
    if (err != NO_ERROR) return err;
    err = reply->writeInt32(static_cast<int32_t>(kFenceFdCount));
    if (err != NO_ERROR) return err;
    // Single uint32 = 0 (numFds=0; "no fence").
    void* dst = reply->writeInplace(kFenceFlattenedSizeBytes);
    if (dst == nullptr) return NO_MEMORY;
    uint32_t zero = 0;
    memcpy(dst, &zero, sizeof(zero));
    return NO_ERROR;
}

// ----------------------------------------------------------------------------
// Per-transaction handlers.
// ----------------------------------------------------------------------------

// REQUEST_BUFFER  (code 1):
//   in:  int32 bufferIdx
//   out: int32 nonNull, [GraphicBuffer flattenable if nonNull], int32 status
status_t WestlakeGraphicBufferProducer::onRequestBuffer(const Parcel& data, Parcel* reply) {
    int32_t bufferIdx = data.readInt32();
    fprintf(stderr, "[wlk-igbp] REQUEST_BUFFER idx=%d\n", bufferIdx);

    std::lock_guard<std::mutex> lk(mLock);

    if (bufferIdx < 0 || bufferIdx >= kNumSlots) {
        reply->writeInt32(0);                 // non-null = false
        reply->writeInt32(BAD_VALUE);
        return NO_ERROR;
    }

    status_t allocErr = ensureSlotAllocatedLocked(bufferIdx, mWidth, mHeight, mFormat);
    if (allocErr != NO_ERROR || mSlots[bufferIdx].buf == nullptr) {
        fprintf(stderr,
                "[wlk-igbp] REQUEST_BUFFER alloc failed for slot=%d err=%d\n",
                bufferIdx, allocErr);
        reply->writeInt32(0);                 // non-null = false
        reply->writeInt32(allocErr != NO_ERROR ? allocErr : NO_MEMORY);
        return NO_ERROR;
    }

    reply->writeInt32(1);                     // non-null = true
    status_t bufErr = mSlots[bufferIdx].buf->writeToParcelAsGraphicBuffer(reply);
    if (bufErr != NO_ERROR) {
        fprintf(stderr,
                "[wlk-igbp] REQUEST_BUFFER writeToParcel failed slot=%d err=%d\n",
                bufferIdx, bufErr);
        // The non-null=1 marker is already written; we still need the trailing
        // status_t.  The receiver will NACK on the buffer read; that's
        // acceptable here because the alternative is wire-protocol violation.
        reply->writeInt32(bufErr);
        return NO_ERROR;
    }
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// SET_MAX_DEQUEUED_BUFFER_COUNT (code 16):
//   in:  int32 maxDequeuedBuffers
//   out: int32 status
status_t WestlakeGraphicBufferProducer::onSetMaxDequeuedBufferCount(const Parcel& data,
                                                                     Parcel* reply) {
    int32_t maxDequeued = data.readInt32();
    fprintf(stderr,
            "[wlk-igbp] SET_MAX_DEQUEUED_BUFFER_COUNT %d (Phase-1: no-op; fixed at %d)\n",
            maxDequeued, kNumSlots);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// SET_ASYNC_MODE (code 17):
//   in:  int32 async (0/1)
//   out: int32 status
status_t WestlakeGraphicBufferProducer::onSetAsyncMode(const Parcel& data, Parcel* reply) {
    int32_t async = data.readInt32();
    fprintf(stderr, "[wlk-igbp] SET_ASYNC_MODE async=%d (no-op)\n", async);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// DEQUEUE_BUFFER (code 2):
//   in:  uint32 width, uint32 height, int32 format, uint64 usage, bool getFrameTimestamps
//   out: int32 buf, Fence(flat), uint64 bufferAge,
//        [FrameEventHistoryDelta(flat) if getFrameTimestamps], int32 status
status_t WestlakeGraphicBufferProducer::onDequeueBuffer(const Parcel& data, Parcel* reply) {
    uint32_t reqW = data.readUint32();
    uint32_t reqH = data.readUint32();
    int32_t reqFormat = data.readInt32();
    uint64_t usage = data.readUint64();
    bool getTimestamps = data.readBool();

    fprintf(stderr,
            "[wlk-igbp] DEQUEUE_BUFFER req=%ux%u fmt=%d usage=0x%llx getTs=%d\n",
            reqW, reqH, reqFormat, (unsigned long long)usage, (int)getTimestamps);

    // If the caller supplied dimensions, honor them (apps under test may
    // call setBuffersDimensions before lockCanvas).  Otherwise stick with
    // the CREATE_SURFACE canned envelope.
    uint32_t useW = reqW != 0 ? reqW : mWidth;
    uint32_t useH = reqH != 0 ? reqH : mHeight;
    int32_t useFormat = reqFormat != 0 ? reqFormat : mFormat;

    // M6-Step4: wait up to 16 ms (one Phase-1 frame interval) for a FREE
    // slot if all are in flight.  This is back-pressure against producers
    // that race ahead of the consumer thread.  Beyond 16 ms we NACK with
    // NO_MEMORY — matches AOSP BufferQueueProducer's "you must drop a frame"
    // semantics under sustained consumer stall.
    std::unique_lock<std::mutex> lk(mLock);
    int slot = findFreeSlotLocked();
    if (slot < 0) {
        mConsumerCv.wait_for(lk, std::chrono::milliseconds(16), [this] {
            if (mWoken) return true;
            for (int i = 0; i < kNumSlots; ++i) {
                if (mSlots[i].state == FREE) return true;
            }
            return false;
        });
        slot = findFreeSlotLocked();
    }
    if (slot < 0) {
        fprintf(stderr, "[wlk-igbp] DEQUEUE_BUFFER no free slot\n");
        reply->writeInt32(-1);                // buf
        writeNoFence(reply);                  // fence
        reply->writeUint64(0);                // bufferAge
        if (getTimestamps) {
            // emit an empty FrameEventHistoryDelta envelope: int32 len=28
            // (CompositorTiming + uint32 size=0), int32 fdCount=0, payload zeroes
            reply->writeInt32(28);
            reply->writeInt32(0);
            void* p = reply->writeInplace(28);
            if (p) memset(p, 0, 28);
        }
        reply->writeInt32(NO_MEMORY);
        return NO_ERROR;
    }

    // Allocate the slot's buffer if first dequeue.
    status_t allocErr = ensureSlotAllocatedLocked(slot, useW, useH, useFormat);
    if (allocErr != NO_ERROR) {
        fprintf(stderr,
                "[wlk-igbp] DEQUEUE_BUFFER slot=%d alloc FAILED err=%d\n",
                slot, allocErr);
        reply->writeInt32(-1);
        writeNoFence(reply);
        reply->writeUint64(0);
        if (getTimestamps) {
            reply->writeInt32(28); reply->writeInt32(0);
            void* p = reply->writeInplace(28); if (p) memset(p, 0, 28);
        }
        reply->writeInt32(allocErr);
        return NO_ERROR;
    }

    mSlots[slot].state = DEQUEUED;
    fprintf(stderr,
            "[wlk-igbp] DEQUEUE_BUFFER -> slot=%d memfd=%d state=DEQUEUED\n",
            slot, mSlots[slot].buf->memfd());

    // AOSP returns BUFFER_NEEDS_REALLOCATION (1) when the caller MUST
    // follow up with REQUEST_BUFFER (i.e. first-time slot use OR the slot's
    // dimensions changed).  We always return 1 on first dequeue so the
    // client knows to issue REQUEST_BUFFER and pick up the memfd.
    constexpr int32_t BUFFER_NEEDS_REALLOCATION = 1;
    reply->writeInt32(slot);                  // buf
    writeNoFence(reply);                      // fence (no fence)
    reply->writeUint64(0);                    // bufferAge
    if (getTimestamps) {
        // Empty FrameEventHistoryDelta envelope.
        reply->writeInt32(28); reply->writeInt32(0);
        void* p = reply->writeInplace(28); if (p) memset(p, 0, 28);
    }
    reply->writeInt32(BUFFER_NEEDS_REALLOCATION);
    return NO_ERROR;
}

// DETACH_BUFFER (code 3):
//   in:  int32 slot
//   out: int32 status
status_t WestlakeGraphicBufferProducer::onDetachBuffer(const Parcel& data, Parcel* reply) {
    int32_t slot = data.readInt32();
    fprintf(stderr, "[wlk-igbp] DETACH_BUFFER slot=%d (Phase-1: no-op)\n", slot);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// ATTACH_BUFFER (code 5):
//   in:  GraphicBuffer (flattenable)
//   out: int32 slot, int32 status
status_t WestlakeGraphicBufferProducer::onAttachBuffer(const Parcel& data, Parcel* reply) {
    // Phase-1: don't read the flatten payload; just NACK.  Future steps will
    // honour attach (only relevant for buffer caching across surfaces).
    (void)data;
    fprintf(stderr, "[wlk-igbp] ATTACH_BUFFER (Phase-1: NACK with -1)\n");
    reply->writeInt32(-1);                    // slot
    reply->writeInt32(INVALID_OPERATION);
    return NO_ERROR;
}

// QUEUE_BUFFER (code 6):
//   in:  int32 buf, QueueBufferInput (flattenable)
//   out: QueueBufferOutput (flattenable), int32 status
status_t WestlakeGraphicBufferProducer::onQueueBuffer(const Parcel& data, Parcel* reply) {
    int32_t buf = data.readInt32();
    // We don't decode the QueueBufferInput payload — Phase 1 just marks the
    // slot QUEUED.  M7 will pull `crop`/`scalingMode`/`fence` out and use
    // them for the DLST display pipe.
    fprintf(stderr, "[wlk-igbp] QUEUE_BUFFER slot=%d\n", buf);

    bool needWake = false;
    {
        std::lock_guard<std::mutex> lk(mLock);
        if (buf < 0 || buf >= kNumSlots) {
            writeQueueBufferOutput(reply);
            reply->writeInt32(BAD_VALUE);
            return NO_ERROR;
        }
        if (mSlots[buf].state != DEQUEUED) {
            fprintf(stderr,
                    "[wlk-igbp] QUEUE_BUFFER slot=%d state=%d not DEQUEUED — WARN\n",
                    buf, mSlots[buf].state);
        }
        mSlots[buf].state = QUEUED;
        mSlots[buf].frameNumber = mNextFrameNumber++;
        needWake = true;
        fprintf(stderr,
                "[wlk-igbp] QUEUE_BUFFER slot=%d state=QUEUED frame#=%llu (consumer signaled)\n",
                buf, (unsigned long long)mSlots[buf].frameNumber);
    }
    // Signal the per-GBP consumer thread (M6-Step4 DlstConsumer) outside
    // the lock to keep the critical section narrow.
    if (needWake) mProducerCv.notify_all();

    writeQueueBufferOutput(reply);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// CANCEL_BUFFER (code 7):
//   in:  int32 buf, Fence (flattenable)
//   out: int32 status
status_t WestlakeGraphicBufferProducer::onCancelBuffer(const Parcel& data, Parcel* reply) {
    int32_t buf = data.readInt32();
    // We don't decode the fence either.
    fprintf(stderr, "[wlk-igbp] CANCEL_BUFFER slot=%d (mark FREE)\n", buf);
    {
        std::lock_guard<std::mutex> lk(mLock);
        if (buf >= 0 && buf < kNumSlots) {
            mSlots[buf].state = FREE;
        }
    }
    mConsumerCv.notify_all();
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// QUERY (code 8):
//   in:  int32 what
//   out: int32 value, int32 status
//
// AOSP NATIVE_WINDOW_* codes used by Surface clients (see
// system/window.h):
//   NATIVE_WINDOW_WIDTH                       = 0
//   NATIVE_WINDOW_HEIGHT                      = 1
//   NATIVE_WINDOW_FORMAT                      = 2
//   NATIVE_WINDOW_MIN_UNDEQUEUED_BUFFERS      = 3
//   NATIVE_WINDOW_QUEUES_TO_WINDOW_COMPOSER   = 4
//   NATIVE_WINDOW_CONCRETE_TYPE               = 5
//   NATIVE_WINDOW_DEFAULT_WIDTH               = 6
//   NATIVE_WINDOW_DEFAULT_HEIGHT              = 7
//   NATIVE_WINDOW_TRANSFORM_HINT              = 8
//   NATIVE_WINDOW_CONSUMER_RUNNING_BEHIND     = 9
//   NATIVE_WINDOW_BUFFER_AGE                  = 13
status_t WestlakeGraphicBufferProducer::onQuery(const Parcel& data, Parcel* reply) {
    int32_t what = data.readInt32();
    int32_t value = 0;
    switch (what) {
        case 0: value = static_cast<int32_t>(mWidth); break;       // WIDTH
        case 1: value = static_cast<int32_t>(mHeight); break;      // HEIGHT
        case 2: value = mFormat; break;                            // FORMAT
        case 3: value = 1; break;                                  // MIN_UNDEQUEUED
        case 4: value = 1; break;                                  // QUEUES_TO_WINDOW_COMPOSER
        case 5: value = 1; break;                                  // CONCRETE_TYPE (Surface)
        case 6: value = static_cast<int32_t>(mWidth); break;       // DEFAULT_WIDTH
        case 7: value = static_cast<int32_t>(mHeight); break;      // DEFAULT_HEIGHT
        case 8: value = 0; break;                                  // TRANSFORM_HINT
        case 9: value = 0; break;                                  // CONSUMER_RUNNING_BEHIND
        default:
            // Unknown query — return 0 + NO_ERROR; matches AOSP behaviour
            // for queries SurfaceFlinger doesn't recognise.
            value = 0;
            break;
    }
    fprintf(stderr, "[wlk-igbp] QUERY what=%d -> value=%d\n", what, value);
    reply->writeInt32(value);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// CONNECT (code 9):
//   in:  int32 hasListener, [StrongBinder listener if hasListener],
//        int32 api, int32 producerControlledByApp
//   out: QueueBufferOutput (flattenable), int32 status
status_t WestlakeGraphicBufferProducer::onConnect(const Parcel& data, Parcel* reply) {
    int32_t hasListener = data.readInt32();
    if (hasListener == 1) {
        sp<IBinder> listener = data.readStrongBinder();
        (void)listener;
    }
    int32_t api = data.readInt32();
    int32_t producerControlledByApp = data.readInt32();
    fprintf(stderr,
            "[wlk-igbp] CONNECT api=%d producerControlledByApp=%d hasListener=%d\n",
            api, producerControlledByApp, hasListener);
    writeQueueBufferOutput(reply);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// DISCONNECT (code 10):
//   in:  int32 api, int32 mode
//   out: int32 status
status_t WestlakeGraphicBufferProducer::onDisconnect(const Parcel& data, Parcel* reply) {
    int32_t api = data.readInt32();
    int32_t mode = data.readInt32();
    fprintf(stderr, "[wlk-igbp] DISCONNECT api=%d mode=%d (Phase-1: no-op)\n", api, mode);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// SET_GENERATION_NUMBER (code 14):
//   in:  uint32 generationNumber
//   out: int32 status
status_t WestlakeGraphicBufferProducer::onSetGenerationNumber(const Parcel& data,
                                                                Parcel* reply) {
    uint32_t gen = data.readUint32();
    fprintf(stderr, "[wlk-igbp] SET_GENERATION_NUMBER %u (no-op)\n", gen);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// ----------------------------------------------------------------------------
// Top-level dispatch.
// ----------------------------------------------------------------------------

status_t WestlakeGraphicBufferProducer::onTransact(uint32_t code,
                                                    const Parcel& data,
                                                    Parcel* reply,
                                                    uint32_t flags) {
    // Every BpGraphicBufferProducer client writes the IGBP descriptor at
    // parcel position 0 (see BpGraphicBufferProducer::requestBuffer §91).
    CHECK_INTERFACE(IGraphicBufferProducer, data, reply);

    switch (code) {
        case REQUEST_BUFFER:                  return onRequestBuffer(data, reply);
        case SET_MAX_DEQUEUED_BUFFER_COUNT:   return onSetMaxDequeuedBufferCount(data, reply);
        case SET_ASYNC_MODE:                  return onSetAsyncMode(data, reply);
        case DEQUEUE_BUFFER:                  return onDequeueBuffer(data, reply);
        case DETACH_BUFFER:                   return onDetachBuffer(data, reply);
        case ATTACH_BUFFER:                   return onAttachBuffer(data, reply);
        case QUEUE_BUFFER:                    return onQueueBuffer(data, reply);
        case CANCEL_BUFFER:                   return onCancelBuffer(data, reply);
        case QUERY:                           return onQuery(data, reply);
        case CONNECT:                         return onConnect(data, reply);
        case DISCONNECT:                      return onDisconnect(data, reply);
        case SET_GENERATION_NUMBER:           return onSetGenerationNumber(data, reply);
        default:
            // Non-Tier-1: log + ack with int32(NO_ERROR) trailing status_t.
            // Most non-Tier-1 IGBP transactions return a single int32 status,
            // so the safe default is "write zero, return NO_ERROR" — matches
            // the WestlakeSurfaceComposer fall-through philosophy.  Steps
            // 4-6 will promote individual codes here as apps exercise them.
            fprintf(stderr,
                    "[wlk-igbp] onTransact code=%u flags=0x%x not in Tier-1; "
                    "ack with NO_ERROR\n",
                    code, flags);
            reply->writeInt32(NO_ERROR);
            return NO_ERROR;
    }
}

}  // namespace android
