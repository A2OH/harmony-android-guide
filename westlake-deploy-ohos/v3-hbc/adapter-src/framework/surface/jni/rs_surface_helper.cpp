/*
 * rs_surface_helper.cpp — P13.2.c "Option B" minimal RS integration.
 *
 * Exposes a C ABI that creates an RSSurfaceNode wired to the real
 * RenderService, returning its producer OH::Surface* to the caller.
 *
 * This file is compiled *inside* ~/oh/out/rk3568 using the exact ninja
 * cflags/include_dirs of render_service_client_src (extracted by the
 * companion compile script), so the full RS header chain resolves.
 *
 * The returned OH::Surface* can be handed to OHGraphicBufferProducer —
 * same path already used by surface_oh_helper.cpp, except the consumer
 * is RenderService instead of an in-process IConsumerSurface.
 */

#include "ui/rs_surface_node.h"
#include "surface.h"

#include <android/log.h>
#include <mutex>
#include <unordered_map>

#define LOG_TAG "OH_RSHelper"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace {
std::mutex g_mu;
// Keep RSSurfaceNode shared_ptr alive as long as the caller holds the
// OH::Surface*. Key: Surface*, Value: RSSurfaceNode shared_ptr.
std::unordered_map<void*, std::shared_ptr<OHOS::Rosen::RSSurfaceNode>> g_nodes;
}  // namespace

extern "C" {

void* rs_surface_helper_create_display_surface(const char* name) {
    OHOS::Rosen::RSSurfaceNodeConfig config;
    config.SurfaceNodeName = (name && *name) ? name : "OHAdapterRSSurface";
    // isWindow=false → create an "app surface" node usable in-process without
    // going through the window manager. Still wires to RenderService.
    auto node = OHOS::Rosen::RSSurfaceNode::Create(config, /*isWindow=*/false);
    if (!node) {
        LOGE("RSSurfaceNode::Create returned null");
        return nullptr;
    }

    OHOS::sptr<OHOS::Surface> producer = node->GetSurface();
    if (!producer) {
        LOGE("RSSurfaceNode::GetSurface returned null");
        return nullptr;
    }

    OHOS::Surface* raw = producer.GetRefPtr();
    raw->IncStrongRef(nullptr);

    {
        std::lock_guard<std::mutex> lk(g_mu);
        g_nodes[raw] = node;
    }

    LOGI("rs_surface_helper: created RSSurfaceNode='%s', surface=%p", config.SurfaceNodeName.c_str(), raw);
    return raw;
}

void rs_surface_helper_release(void* producerRaw) {
    if (!producerRaw) return;
    std::shared_ptr<OHOS::Rosen::RSSurfaceNode> dropped;
    {
        std::lock_guard<std::mutex> lk(g_mu);
        auto it = g_nodes.find(producerRaw);
        if (it != g_nodes.end()) {
            dropped = std::move(it->second);
            g_nodes.erase(it);
        }
    }
    auto* p = reinterpret_cast<OHOS::Surface*>(producerRaw);
    p->DecStrongRef(nullptr);
}

}  // extern "C"
