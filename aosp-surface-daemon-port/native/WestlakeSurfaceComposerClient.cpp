// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 2): ISurfaceComposerClient stub.
//
// Returned to peers from ISurfaceComposer::CREATE_CONNECTION.  Implements the
// AOSP-11 SafeInterface ISurfaceComposerClient surface (Tag enum from
// frameworks/native/libs/gui/ISurfaceComposerClient.cpp §32-39).
//
// Wire-format note (SafeInterface convention, see
// aosp-libbinder-port/aosp-src/libbinder/include/binder/SafeInterface.h):
//
//   callRemote (Bp side):  writes input args, sends transact(), then
//                          readOutputs(reply, args...), then reads
//                          status_t LAST from the reply.
//
//   callLocal (Bn side):   reads input args from data, invokes method,
//                          writes outputs, then writes status_t LAST.
//
// So for CREATE_SURFACE the reply order is:
//   handle (StrongBinder), gbp (StrongBinder), transformHint (uint32), status_t (int32)
//
// We do NOT use callLocal here; we hand-code the marshalling because we don't
// have the SafeInterface template glue (no ISurfaceComposerClient interface
// declaration in this port).  The hand-coding mirrors what callLocal would
// emit for AOSP-11's exact signatures.

#include "WestlakeSurfaceComposerClient.h"
#include "DlstConsumer.h"
#include "WestlakeGraphicBufferProducer.h"

#include <stdio.h>

#include <binder/Binder.h>
#include <binder/IInterface.h>
#include <binder/Parcel.h>
#include <utils/Errors.h>
#include <utils/String16.h>
#include <utils/String8.h>

namespace android {

// AOSP-11 ISurfaceComposerClient descriptor — the canonical value that
// surface_smoke's BpSurfaceComposerClient writes to the wire.
static const String16 kClientIfaceDescriptor("android.ui.ISurfaceComposerClient");
// CR35 §6.3 / §6.6 / §7 §D-B: A15 AIDL-generated form (descriptor namespace
// migrated to `android.gui.*` per CR35 §6.6's nm-symbol-table evidence).
static const String16 kClientA15IfaceDescriptor("android.gui.ISurfaceComposerClient");

WestlakeSurfaceComposerClient::WestlakeSurfaceComposerClient() {
    fprintf(stderr, "[wlk-sf] SurfaceComposerClient constructed (this=%p)\n", this);
}

WestlakeSurfaceComposerClient::~WestlakeSurfaceComposerClient() {
    // Stop and drop every per-surface consumer.  Each consumer's stop()
    // wakes its bound producer so the consumer thread can join cleanly.
    {
        std::lock_guard<std::mutex> lk(mConsumersLock);
        for (auto& c : mConsumers) {
            if (c) c->stop();
        }
        mConsumers.clear();
    }
    fprintf(stderr, "[wlk-sf] SurfaceComposerClient destructed (this=%p)\n", this);
}

sp<WestlakeGraphicBufferProducer>
WestlakeSurfaceComposerClient::spawnProducerAndConsumer(
        uint32_t w, uint32_t h, int32_t format) {
    sp<WestlakeGraphicBufferProducer> gbp =
            sp<WestlakeGraphicBufferProducer>::make(w, h, format);
    if (!mDlstPipePath.empty()) {
        auto consumer = std::make_unique<DlstConsumer>(gbp, mDlstPipePath);
        consumer->start();
        std::lock_guard<std::mutex> lk(mConsumersLock);
        mConsumers.push_back(std::move(consumer));
        fprintf(stderr,
                "[wlk-sf] spawned DlstConsumer #%zu for gbp=%p pipe=\"%s\"\n",
                mConsumers.size(), gbp.get(), mDlstPipePath.c_str());
    } else {
        fprintf(stderr,
                "[wlk-sf] DLST pipe path empty — no consumer spawned for gbp=%p "
                "(Step-3 behavior; smoke must call setDlstPipePath first)\n",
                gbp.get());
    }
    return gbp;
}

const String16& WestlakeSurfaceComposerClient::getInterfaceDescriptor() const {
    return kClientIfaceDescriptor;
}

// CREATE_SURFACE (tag 1) — the per-Window surface allocation call.
//
// AOSP-11 signature (from ISurfaceComposerClient.h):
//   status_t createSurface(const String8& name, uint32_t w, uint32_t h,
//                          PixelFormat format, uint32_t flags,
//                          const sp<IBinder>& parent, LayerMetadata metadata,
//                          sp<IBinder>* handle,
//                          sp<IGraphicBufferProducer>* gbp,
//                          uint32_t* outTransformHint);
//
// Input parcel layout (SafeInterface auto-marshalling): name, w, h, format,
// flags, parent, metadata.  We read what we care about and skip the rest.
//
// Output parcel layout: handle, gbp, transformHint, status_t.
status_t WestlakeSurfaceComposerClient::onCreateSurface(const Parcel& data, Parcel* reply) {
    String8 name = data.readString8();
    uint32_t w = data.readUint32();
    uint32_t h = data.readUint32();
    int32_t format = data.readInt32();
    uint32_t flags = data.readUint32();
    sp<IBinder> parent = data.readStrongBinder();
    // LayerMetadata is the trailing input — we don't decode it.

    fprintf(stderr,
            "[wlk-sf] CREATE_SURFACE name=\"%s\" %ux%u fmt=%d flags=0x%x parent=%p\n",
            name.c_str(), w, h, format, flags, parent.get());

    // M6-Step3: hand back a real BnGraphicBufferProducer backed by the
    // memfd-substitute GraphicBuffer (M6 plan §3.3; CR33 spike retired the
    // risk row).  Per-Surface 2-slot ring; lazy memfd allocation on first
    // DEQUEUE.  See WestlakeGraphicBufferProducer + MemfdGraphicBuffer.
    // M6-Step4: also spawn a DlstConsumer that pulls QUEUED slots and
    // streams pixels to the Compose host SurfaceView via the DLST pipe.
    sp<IBinder> handle = new BBinder();
    sp<WestlakeGraphicBufferProducer> gbpImpl = spawnProducerAndConsumer(w, h, format);
    sp<IBinder> gbp = gbpImpl;
    uint32_t transformHint = 0;

    // SafeInterface reply order: outputs first, status_t LAST.
    reply->writeStrongBinder(handle);
    reply->writeStrongBinder(gbp);
    reply->writeUint32(transformHint);
    reply->writeInt32(NO_ERROR);  // status_t
    return NO_ERROR;
}

// CREATE_WITH_SURFACE_PARENT (tag 2) — variant where parent is an
// IGraphicBufferProducer instead of an IBinder layer handle.  Same reply
// layout as CREATE_SURFACE.  Phase-1: identical canned response.
status_t WestlakeSurfaceComposerClient::onCreateWithSurfaceParent(const Parcel& data,
                                                                   Parcel* reply) {
    String8 name = data.readString8();
    uint32_t w = data.readUint32();
    uint32_t h = data.readUint32();
    int32_t format = data.readInt32();
    uint32_t flags = data.readUint32();
    sp<IBinder> parent = data.readStrongBinder();
    // LayerMetadata trailing — skipped.

    fprintf(stderr,
            "[wlk-sf] CREATE_WITH_SURFACE_PARENT name=\"%s\" %ux%u fmt=%d flags=0x%x parent=%p\n",
            name.c_str(), w, h, format, flags, parent.get());

    sp<IBinder> handle = new BBinder();
    sp<WestlakeGraphicBufferProducer> gbpImpl = spawnProducerAndConsumer(w, h, format);
    sp<IBinder> gbp = gbpImpl;
    uint32_t transformHint = 0;

    reply->writeStrongBinder(handle);
    reply->writeStrongBinder(gbp);
    reply->writeUint32(transformHint);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// CLEAR_LAYER_FRAME_STATS (tag 3) — no outputs, just status_t.
// AOSP-11: status_t clearLayerFrameStats(const sp<IBinder>& handle) const;
status_t WestlakeSurfaceComposerClient::onClearLayerFrameStats(const Parcel& data,
                                                                Parcel* reply) {
    sp<IBinder> handle = data.readStrongBinder();
    fprintf(stderr, "[wlk-sf] CLEAR_LAYER_FRAME_STATS handle=%p (no-op)\n", handle.get());
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// GET_LAYER_FRAME_STATS (tag 4):
//   status_t getLayerFrameStats(const sp<IBinder>& handle, FrameStats* outStats) const;
// outStats is a parcelable; Phase-1 emits zeroed envelope (the SafeInterface
// marshalling auto-writes the parcelable via Parcelable::writeToParcel).
status_t WestlakeSurfaceComposerClient::onGetLayerFrameStats(const Parcel& data,
                                                              Parcel* reply) {
    sp<IBinder> handle = data.readStrongBinder();
    fprintf(stderr, "[wlk-sf] GET_LAYER_FRAME_STATS handle=%p (Phase-1: empty)\n",
            handle.get());
    // FrameStats parcel format: int64 refreshPeriodNano, then int32 N then 3*N int64s.
    // Emit zero counts.
    reply->writeInt64(0);   // refreshPeriodNano
    reply->writeInt32(0);   // count of desired frame timestamps
    reply->writeInt32(0);   // count of actual presented timestamps
    reply->writeInt32(0);   // count of frame ready timestamps
    reply->writeInt32(NO_ERROR);  // status_t LAST
    return NO_ERROR;
}

// MIRROR_SURFACE (tag 5):
//   status_t mirrorSurface(const sp<IBinder>& mirrorFromHandle, sp<IBinder>* outHandle);
status_t WestlakeSurfaceComposerClient::onMirrorSurface(const Parcel& data, Parcel* reply) {
    sp<IBinder> from = data.readStrongBinder();
    fprintf(stderr, "[wlk-sf] MIRROR_SURFACE from=%p\n", from.get());
    sp<IBinder> handle = new BBinder();
    reply->writeStrongBinder(handle);
    reply->writeInt32(NO_ERROR);
    return NO_ERROR;
}

// ---------------------------------------------------------------------------
// Top-level dispatch — mirror AOSP-11 BnSurfaceComposerClient::onTransact §103.
// ---------------------------------------------------------------------------

status_t WestlakeSurfaceComposerClient::onTransact(uint32_t code,
                                                    const Parcel& data,
                                                    Parcel* reply,
                                                    uint32_t flags) {
    // SafeInterface BnSurfaceComposerClient first range-checks, then dispatches.
    // We do the same: codes outside the Tag range pass through to BBinder
    // default (which handles INTERFACE_TRANSACTION etc.).
    if (code < CREATE_SURFACE || code > MIRROR_SURFACE) {
        fprintf(stderr,
                "[wlk-sf] Client onTransact code=%u flags=0x%x (out-of-range; "
                "deferring to BBinder default)\n",
                code, flags);
        return BBinder::onTransact(code, data, reply, flags);
    }

    // CR35 §7 §D-B: descriptor-tolerant header consumption.  Accept both the
    // AOSP-11 form ("android.ui.ISurfaceComposerClient") and the A15 AIDL
    // form ("android.gui.ISurfaceComposerClient" per CR35 §6.6's nm evidence).
    // We replicate Parcel::enforceInterface's header reads (StrictMode,
    // WorkSource, kHeader, descriptor) so handlers see the same post-header
    // parcel position.  Pattern is symmetric to CR11's libbinder
    // receive-tolerance.
    (void)data.readInt32();  // StrictModePolicy
    (void)data.readInt32();  // WorkSource
    (void)data.readInt32();  // kHeader
    size_t descLen = 0;
    const char16_t* desc = data.readString16Inplace(&descLen);
    if (desc == nullptr) {
        fprintf(stderr,
                "[wlk-sf] Client onTransact: null descriptor — REJECT\n");
        return PERMISSION_DENIED;
    }
    const bool isAosp11 =
            descLen == kClientIfaceDescriptor.size() &&
            memcmp(desc, kClientIfaceDescriptor.c_str(),
                   descLen * sizeof(char16_t)) == 0;
    const bool isA15 =
            descLen == kClientA15IfaceDescriptor.size() &&
            memcmp(desc, kClientA15IfaceDescriptor.c_str(),
                   descLen * sizeof(char16_t)) == 0;
    if (!isAosp11 && !isA15) {
        fprintf(stderr,
                "[wlk-sf] Client onTransact: unrecognized descriptor \"%s\" — "
                "REJECT (expected \"%s\" or \"%s\")\n",
                String8(desc, descLen).c_str(),
                String8(kClientIfaceDescriptor).c_str(),
                String8(kClientA15IfaceDescriptor).c_str());
        return PERMISSION_DENIED;
    }
    if (isA15) {
        fprintf(stderr,
                "[wlk-sf] CR35 §D-B: A15 ISurfaceComposerClient descriptor "
                "observed — A15 client TX code=%u; CR35 §6.3 shows the per-tag "
                "code numbering is mostly stable (createSurface, "
                "clearLayerFrameStats, getLayerFrameStats, mirrorSurface), "
                "with mirrorDisplay + getSchedulingPolicy as net-new — the "
                "in-Tag-range switch below covers the overlap.\n",
                code);
    }

    switch (static_cast<Tag>(code)) {
        case CREATE_SURFACE:
            return onCreateSurface(data, reply);
        case CREATE_WITH_SURFACE_PARENT:
            return onCreateWithSurfaceParent(data, reply);
        case CLEAR_LAYER_FRAME_STATS:
            return onClearLayerFrameStats(data, reply);
        case GET_LAYER_FRAME_STATS:
            return onGetLayerFrameStats(data, reply);
        case MIRROR_SURFACE:
            return onMirrorSurface(data, reply);
    }
    // Unreachable — switch is exhaustive on Tag and we already range-checked.
    return UNKNOWN_TRANSACTION;
}

}  // namespace android
