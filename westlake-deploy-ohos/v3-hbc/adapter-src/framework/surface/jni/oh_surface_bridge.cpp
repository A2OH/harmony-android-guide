/*
 * oh_surface_bridge.cpp
 *
 * Implements OHSurfaceBridge: binds to WMS-registered RSSurfaceNode and
 * wraps it as OHGraphicBufferProducer for Android.
 *
 * Lifecycle per window:
 *   1. createSurface()           -> fetch WMS-registered RSSurfaceNode
 *   2. getSurfaceHandle()        -> OHGraphicBufferProducer wrapping OH Surface
 *   3. notifyDrawingCompleted()  -> RSTransaction::FlushImplicitTransaction()
 *   4. updateSurfaceSize()       -> RSSurfaceNode resize
 *   5. destroySurface()          -> Release all OH resources
 *
 * Reference:
 *   OH: graphic_2d/rosen/modules/render_service_client/core/ui/rs_surface_node.h
 *   OH: graphic_surface/surface/include/surface.h
 *
 * 2026-05-08 G2.14ad: removed RSUIDirector. helloworld uses hwui+EGL direct
 * buffer push to RSSurfaceNode; the ArkUI RSUIDirector view-tree manager is
 * not needed and was injecting an RSRootNode child that polluted the surface
 * subtree (RS hilog "Generator not registered for node type 1/129").
 */
#include "oh_surface_bridge.h"
#include "oh_graphic_buffer_producer.h"
#include "pixel_format_mapper.h"
#include "oh_window_manager_client.h"  // 2026-05-06: single-source rule (§9.1 #2)

#include <android/log.h>

// OH RenderService client headers
#include "ui/rs_surface_node.h"
#include "transaction/rs_transaction.h"

#include "oh_br_trace.h"   // G2.14ac IPC trace+log macros

#define LOG_TAG "OH_SurfaceBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace oh_adapter {

OHSurfaceBridge& OHSurfaceBridge::getInstance() {
    static OHSurfaceBridge instance;
    return instance;
}

bool OHSurfaceBridge::createSurface(int32_t sessionId, const std::string& windowName,
                                     int32_t width, int32_t height, int32_t format)
{
    OH_BR_IPC_SCOPE("SurfaceBridge.createSurface",
                    "session=%{public}d name=%{public}s w=%{public}d h=%{public}d fmt=%{public}d",
                    sessionId, windowName.c_str(), width, height, format);
    std::lock_guard<std::mutex> lock(mutex_);

    // Check if session already exists
    if (sessions_.find(sessionId) != sessions_.end()) {
        LOGW("createSurface: Session %d already exists, destroying first", sessionId);
        // Clean up existing session (without holding lock — use internal helper)
        auto& existing = sessions_[sessionId];
        if (existing->producerAdapter) {
            auto* producer = static_cast<OHGraphicBufferProducer*>(existing->producerAdapter);
            producer->disconnect();
            delete producer;
        }
        sessions_.erase(sessionId);
    }

    auto session = std::make_unique<SurfaceSession>();
    session->sessionId = sessionId;
    session->width = width;
    session->height = height;
    session->format = format;

    // 2026-05-06 — Per graphics_rendering_design.html §5.1 + §9.1 三件套 #2:
    //   Single-source RSSurfaceNode rule.  The surfaceNode that WMS already
    //   registered (created in oh_window_manager_client.cpp::createSession)
    //   MUST also be the surfaceNode that hwui producer feeds buffers into.
    //   Two independent surfaceNodes (the pre-fix bug) leave RS unable to
    //   correlate WMS-side layer geometry with producer-side buffer flow,
    //   forcing RS prepare to mark the layer COMPOSITION_CLIENT (type 0)
    //   which OH HDI rejects ⇒ Apply:250 ⇒ App SIGABRT.
    //
    // Old code (commented out for reference):
    //     OHOS::Rosen::RSSurfaceNodeConfig config;
    //     config.SurfaceNodeName = windowName;
    //     auto surfaceNode = OHOS::Rosen::RSSurfaceNode::Create(config);  // ← independent surfaceNode
    //
    // New: fetch the WMS-registered surfaceNode by sessionId.
    auto surfaceNode = OHWindowManagerClient::getInstance().getRSSurfaceNode(sessionId);
    if (!surfaceNode) {
        LOGE("createSurface: WMS-registered RSSurfaceNode not found for session %d "
             "(createSession must run first)", sessionId);
        return false;
    }

    // Set initial bounds (same surfaceNode also accessed by WMS; this is a
    // client-side property update which RS will pick up via RSTransactionProxy
    // flush in notifyDrawingCompleted).
    // 2026-05-11 G2.14ap: SetBounds AND SetFrame must be paired.  ClipToFrame
    // is implicitly true on RSSurfaceNode; if Frame stays at sentinel
    // [-inf,-inf,-inf,-inf], the entire surface is clipped to an empty region
    // and hwui's buffer submission no-ops at RS (shouldPaint_=0, drawRect=0x0).
    surfaceNode->SetBoundsWidth(width);
    surfaceNode->SetBoundsHeight(height);
    surfaceNode->SetFrame(0.0f, 0.0f, static_cast<float>(width), static_cast<float>(height));

    session->surfaceNode = surfaceNode;

    // Get the producer Surface from the RSSurfaceNode
    // This is the OH ProducerSurface backed by the BufferQueue
    auto ohSurface = surfaceNode->GetSurface();
    if (!ohSurface) {
        LOGE("createSurface: Failed to get Surface from RSSurfaceNode for session %d", sessionId);
        return false;
    }

    session->ohSurface = ohSurface;

    // 2026-05-08 G2.14ad: RSUIDirector creation removed.
    // RSUIDirector is for OH ArkUI view-tree management; helloworld uses
    // hwui+EGL+ANativeWindow direct buffer push and does not need it.
    // RSUIDirector::Init() injects an RSRootNode (type 1 / type 129) child
    // into the surface subtree, which RS rejects with
    //   "Generator not registered for node type 1/129"
    // and which is unrelated to the actual buffer production path.
    // RSTransaction::FlushImplicitTransaction() in notifyDrawingCompleted()
    // is sufficient to commit producer-side property changes to RS.

    // 2026-05-12 G2.14aw probe A.3: path-A baseline uniqueId.
    // path-A (OHGraphicBufferProducer for SurfaceControl) vs path-B
    // (oh_anw_wrap for hwui) MUST resolve to the same producer uniqueId.
    // Captures ohSurface taken right here so we can prove the cross-path
    // sameness without needing access to the other module's locals.
    uint64_t pathAUniqueId = ohSurface ? ohSurface->GetUniqueId() : 0;
    sessions_[sessionId] = std::move(session);

    LOGI("createSurface[probe-pathA]: session=%d name=%s size=%dx%d surfaceNodeId=%llu "
         "ohSurface_refPtr=%p uniqueId=0x%llx",
         sessionId, windowName.c_str(), width, height,
         static_cast<unsigned long long>(surfaceNode->GetId()),
         ohSurface.GetRefPtr(),
         static_cast<unsigned long long>(pathAUniqueId));

    return true;
}

int64_t OHSurfaceBridge::getSurfaceHandle(int32_t sessionId,
                                           int32_t width, int32_t height,
                                           int32_t format)
{
    OH_BR_IPC_SCOPE("SurfaceBridge.getSurfaceHandle",
                    "session=%{public}d w=%{public}d h=%{public}d fmt=%{public}d",
                    sessionId, width, height, format);
    std::lock_guard<std::mutex> lock(mutex_);

    auto it = sessions_.find(sessionId);
    if (it == sessions_.end()) {
        LOGE("getSurfaceHandle: Session %d not found", sessionId);
        return 0;
    }

    auto& session = it->second;

    // Create OHGraphicBufferProducer if not already created
    if (session->producerAdapter == nullptr) {
        if (!session->ohSurface) {
            LOGE("getSurfaceHandle: OH Surface is null for session %d", sessionId);
            return 0;
        }

        // Set buffer queue size for triple buffering
        session->ohSurface->SetQueueSize(3);

        auto* producer = new OHGraphicBufferProducer(session->ohSurface);
        producer->connect();
        session->producerAdapter = producer;

        LOGI("getSurfaceHandle: Created OHGraphicBufferProducer for session %d", sessionId);
    }

    // Handle resize if dimensions changed
    if ((width > 0 && width != session->width) ||
        (height > 0 && height != session->height)) {
        session->width = width > 0 ? width : session->width;
        session->height = height > 0 ? height : session->height;

        if (session->surfaceNode) {
            session->surfaceNode->SetBoundsWidth(session->width);
            session->surfaceNode->SetBoundsHeight(session->height);
            // G2.14ap: pair SetFrame with SetBounds (see createSurface above).
            session->surfaceNode->SetFrame(0.0f, 0.0f,
                                          static_cast<float>(session->width),
                                          static_cast<float>(session->height));
        }
        LOGI("getSurfaceHandle: Resized session %d to %dx%d",
             sessionId, session->width, session->height);
    }

    // Return the OHGraphicBufferProducer pointer as opaque handle
    // Android side casts this to create a SurfaceControl that delegates to it
    return reinterpret_cast<int64_t>(session->producerAdapter);
}

void OHSurfaceBridge::notifyDrawingCompleted(int32_t sessionId) {
    OH_BR_IPC_SCOPE("SurfaceBridge.notifyDrawingCompleted", "session=%{public}d", sessionId);
    std::lock_guard<std::mutex> lock(mutex_);

    auto it = sessions_.find(sessionId);
    if (it == sessions_.end()) {
        LOGW("notifyDrawingCompleted: Session %d not found", sessionId);
        return;
    }

    // 2026-05-08 G2.14ad: RSUIDirector::SendMessages() removed (see createSurface).
    // RSTransaction::FlushImplicitTransaction() commits all pending RSSurfaceNode
    // property changes (bounds, alpha, visibility) to RS via
    // RSIClientToRenderConnection::CommitTransaction(), which is exactly what
    // hwui's buffer push lifecycle requires.
    OHOS::Rosen::RSTransaction::FlushImplicitTransaction();
}

void OHSurfaceBridge::updateSurfaceSize(int32_t sessionId, int32_t width, int32_t height) {
    OH_BR_IPC_SCOPE("SurfaceBridge.updateSurfaceSize",
                    "session=%{public}d w=%{public}d h=%{public}d", sessionId, width, height);
    std::lock_guard<std::mutex> lock(mutex_);

    auto it = sessions_.find(sessionId);
    if (it == sessions_.end()) {
        LOGW("updateSurfaceSize: Session %d not found", sessionId);
        return;
    }

    auto& session = it->second;
    session->width = width;
    session->height = height;

    if (session->surfaceNode) {
        session->surfaceNode->SetBoundsWidth(width);
        session->surfaceNode->SetBoundsHeight(height);
        // G2.14ap: pair SetFrame with SetBounds (see createSurface above).
        session->surfaceNode->SetFrame(0.0f, 0.0f,
                                      static_cast<float>(width),
                                      static_cast<float>(height));
        LOGI("updateSurfaceSize: session=%d, size=%dx%d", sessionId, width, height);
    }
}

void OHSurfaceBridge::destroySurface(int32_t sessionId) {
    OH_BR_IPC_SCOPE("SurfaceBridge.destroySurface", "session=%{public}d", sessionId);
    std::lock_guard<std::mutex> lock(mutex_);

    auto it = sessions_.find(sessionId);
    if (it == sessions_.end()) {
        LOGW("destroySurface: Session %d not found", sessionId);
        return;
    }

    auto& session = it->second;

    // Disconnect and delete the OHGraphicBufferProducer
    if (session->producerAdapter) {
        auto* producer = static_cast<OHGraphicBufferProducer*>(session->producerAdapter);
        producer->disconnect();
        delete producer;
        session->producerAdapter = nullptr;
    }

    // 2026-05-08 G2.14ad: RSUIDirector::Destroy() removed (see createSurface).

    // Release OH Surface and RSSurfaceNode
    session->ohSurface = nullptr;
    session->surfaceNode = nullptr;

    sessions_.erase(it);

    LOGI("destroySurface: session=%d destroyed", sessionId);
}

std::shared_ptr<OHOS::Rosen::RSSurfaceNode>
OHSurfaceBridge::getSurfaceNode(int32_t sessionId) {
    std::lock_guard<std::mutex> lock(mutex_);

    auto it = sessions_.find(sessionId);
    if (it == sessions_.end()) {
        return nullptr;
    }
    return it->second->surfaceNode;
}

}  // namespace oh_adapter
