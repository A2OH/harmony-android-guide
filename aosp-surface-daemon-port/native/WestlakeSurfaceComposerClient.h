// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon (Step 2)
//
// WestlakeSurfaceComposerClient is the BBinder we hand back to peers that
// invoke ISurfaceComposer::CREATE_CONNECTION (transaction code 0x02 on the
// "SurfaceFlinger" service).  It implements (Phase-1, stubbed) the AOSP-11
// ISurfaceComposerClient surface from
// frameworks/native/libs/gui/include/gui/ISurfaceComposerClient.h and
// frameworks/native/libs/gui/ISurfaceComposerClient.cpp (the SafeInterface
// Tag::CREATE_SURFACE, CLEAR_LAYER_FRAME_STATS, etc.).
//
// Phase-1 every per-Surface allocation completes successfully but returns
// synthetic IBinder handles and a null IGraphicBufferProducer — sufficient
// for app discovery to proceed past `new SurfaceComposerClient()` and into
// per-Window constructor code.  M6-Step3 will wire a real
// IGraphicBufferProducer backed by the memfd-substitute GraphicBuffer
// (M6 plan §3.3 GraphicBuffer-memfd.cpp).
//
// Anti-drift: NO per-app branches.  Every peer that calls CREATE_CONNECTION
// gets an instance with the exact same behaviour.

#ifndef WESTLAKE_SURFACE_COMPOSER_CLIENT_H
#define WESTLAKE_SURFACE_COMPOSER_CLIENT_H

#include <memory>
#include <mutex>
#include <string>
#include <vector>

#include <binder/Binder.h>
#include <binder/Parcel.h>
#include <utils/Errors.h>
#include <utils/String16.h>

namespace android {

class DlstConsumer;
class WestlakeGraphicBufferProducer;

class WestlakeSurfaceComposerClient : public BBinder {
public:
    WestlakeSurfaceComposerClient();
    ~WestlakeSurfaceComposerClient() override;

    status_t onTransact(uint32_t code, const Parcel& data, Parcel* reply,
                        uint32_t flags = 0) override;

    const String16& getInterfaceDescriptor() const override;

    // AOSP-11 ISurfaceComposerClient SafeInterface tag enum, verbatim:
    //   frameworks/native/libs/gui/ISurfaceComposerClient.cpp §32-39
    //
    // FIRST_CALL_TRANSACTION = 1; values are dense + sequential.
    enum Tag : uint32_t {
        CREATE_SURFACE              = 1,  // 0x01
        CREATE_WITH_SURFACE_PARENT  = 2,  // 0x02
        CLEAR_LAYER_FRAME_STATS     = 3,  // 0x03
        GET_LAYER_FRAME_STATS       = 4,  // 0x04
        MIRROR_SURFACE              = 5,  // 0x05
    };

    // M6-Step4: install the DLST pipe path used for every subsequent
    // CREATE_SURFACE.  Called once at startup by `surfaceflinger_main` after
    // resolving `$WESTLAKE_DLST_PIPE`.  Empty path disables consumer-thread
    // spawn (returns to Step-3 behavior — useful in unit tests where the
    // DLST machinery isn't relevant).
    void setDlstPipePath(std::string path) { mDlstPipePath = std::move(path); }
    const std::string& dlstPipePath() const { return mDlstPipePath; }

private:
    status_t onCreateSurface(const Parcel& data, Parcel* reply);
    status_t onCreateWithSurfaceParent(const Parcel& data, Parcel* reply);
    status_t onClearLayerFrameStats(const Parcel& data, Parcel* reply);
    status_t onGetLayerFrameStats(const Parcel& data, Parcel* reply);
    status_t onMirrorSurface(const Parcel& data, Parcel* reply);

    // Helper: construct + return a new (producer, consumer) pair for a
    // CREATE_SURFACE / CREATE_WITH_SURFACE_PARENT reply.
    sp<WestlakeGraphicBufferProducer> spawnProducerAndConsumer(
            uint32_t w, uint32_t h, int32_t format);

    std::string mDlstPipePath;
    // Strong refs to per-surface consumers — keeps the consumer alive even
    // if the binder reply's outgoing reference is the only handle the
    // client retains.  Cleared at client destruction.
    mutable std::mutex mConsumersLock;
    std::vector<std::unique_ptr<DlstConsumer>> mConsumers;
};

}  // namespace android

#endif  // WESTLAKE_SURFACE_COMPOSER_CLIENT_H
