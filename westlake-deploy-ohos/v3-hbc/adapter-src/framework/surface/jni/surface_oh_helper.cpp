/*
 * surface_oh_helper.cpp — P13.2.b helper
 *
 * Provides createInProcessProducer() — builds an OH producer Surface backed by
 * an in-process consumer. The dmabuf round-trip works (RequestBuffer/FlushBuffer
 * succeed, virAddr is mmapped), but the queued buffers go to an in-process
 * consumer that we ignore — pixels are not visible on screen.
 *
 * This bypasses oh_surface_bridge.cpp (which would create an RSSurfaceNode that
 * eventually displays via RenderService) because that file pulls in the entire
 * RS client header chain which has broken Skia includes (skcms.h at wrong path).
 *
 * Wiring to actual display = P13.2.c (requires WindowManagerService → SceneSession
 * → RSSurfaceNode integration that's beyond P13.2.b scope).
 */
#include "iconsumer_surface.h"
#include "surface.h"
#include "ibuffer_producer.h"
#include "surface_buffer.h"
#include <android/log.h>

#define LOG_TAG "OH_SurfaceHelper"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

// Returns an OHOS::Surface* (raw pointer wrapped via OHOS::sptr internally).
// Caller stores the sptr via the helpers below to keep ref count alive.
// Returns nullptr on failure.
void* surface_oh_create_in_process_producer(const char* name) {
    auto consumer = OHOS::IConsumerSurface::Create(name ? name : "OHAdapterSurface");
    if (!consumer) {
        LOGE("create_in_process_producer: IConsumerSurface::Create failed");
        return nullptr;
    }

    // Hold the consumer alive in a static map keyed by the producer pointer,
    // because once the consumer is dropped the producer becomes useless.
    // We leak the consumer ref intentionally — Surface lifetime is tied to
    // the JNI Surface object lifetime which we don't always have visibility into.
    OHOS::sptr<OHOS::IBufferProducer> producerIface = consumer->GetProducer();
    if (!producerIface) {
        LOGE("create_in_process_producer: GetProducer failed");
        return nullptr;
    }

    OHOS::sptr<OHOS::Surface> producer = OHOS::Surface::CreateSurfaceAsProducer(producerIface);
    if (!producer) {
        LOGE("create_in_process_producer: CreateSurfaceAsProducer failed");
        return nullptr;
    }

    // Set queue size for triple buffering
    producer->SetQueueSize(3);

    // Pin both refs by leaking sptr's increment.
    // We add a ref then return the raw pointer; caller manages release via _release.
    OHOS::Surface* raw = producer.GetRefPtr();
    raw->IncStrongRef(nullptr);
    consumer->IncStrongRef(nullptr);  // pin consumer

    LOGI("create_in_process_producer: ok, name=%s", name);
    return raw;
}

void surface_oh_release_producer(void* producerRaw) {
    if (!producerRaw) return;
    auto* p = reinterpret_cast<OHOS::Surface*>(producerRaw);
    p->DecStrongRef(nullptr);
}

}  // extern "C"
