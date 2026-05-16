/*
 * oh_choreographer_shim.c
 *
 * Android NDK AChoreographer_* API shim, implemented as wrappers around
 * OH_NativeVSync_* API.
 *
 * Architectural note:
 *   - Android Choreographer model: One global instance per thread, post a
 *     callback that fires on the next vsync.
 *   - OH NativeVSync model: Create a named VSync object, request frame,
 *     callback fires once when next vsync arrives.
 *
 * Adaptation strategy:
 *   - AChoreographer_create() → OH_NativeVSync_Create("hwui", 4)
 *   - AChoreographer_postVsyncCallback() → OH_NativeVSync_RequestFrame()
 *   - Wrap callbacks to translate signature differences
 *
 * Build:
 *   $CC --target=arm-linux-ohos -shared -fPIC \
 *        -o liboh_choreographer_shim.so oh_choreographer_shim.c \
 *        -lnative_vsync
 */
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

/* Forward declarations of OH NativeVSync C API */
typedef struct OH_NativeVSync OH_NativeVSync;
typedef void (*OH_NativeVSync_FrameCallback)(long long timestamp, void *data);

extern OH_NativeVSync *OH_NativeVSync_Create(const char *name, unsigned int length);
extern void OH_NativeVSync_Destroy(OH_NativeVSync *nativeVsync);
extern int OH_NativeVSync_RequestFrame(OH_NativeVSync *nativeVsync,
                                       OH_NativeVSync_FrameCallback callback,
                                       void *data);
extern int OH_NativeVSync_GetPeriod(OH_NativeVSync *nativeVsync, long long *period);

/* ============================================================ */
/* Android AChoreographer types and callbacks                    */
/* ============================================================ */

/* Android Choreographer is opaque to libhwui — we wrap OH_NativeVSync */
typedef struct AChoreographer {
    OH_NativeVSync *vsync;
    char name[32];
} AChoreographer;

/* Android frame callback signatures */
typedef void (*AChoreographer_frameCallback)(long frameTimeNanos, void *data);
typedef void (*AChoreographer_frameCallback64)(int64_t frameTimeNanos, void *data);

/* AChoreographerFrameCallbackData — opaque struct for vsync callback data */
typedef struct AChoreographerFrameCallbackData {
    int64_t frameTimeNanos;
    int64_t vsyncId;
    int64_t deadlineNanos;
} AChoreographerFrameCallbackData;

typedef void (*AChoreographer_vsyncCallback)(
    const AChoreographerFrameCallbackData *callbackData, void *data);

typedef void (*AChoreographer_extendedFrameCallback)(
    int64_t vsyncId, int64_t frameTimeNanos, int64_t frameDeadline, void *data);

typedef void (*AChoreographer_refreshRateCallback)(int64_t vsyncPeriod, void *data);

/* ============================================================ */
/* Internal: callback adaptation                                 */
/* ============================================================ */

typedef struct {
    AChoreographer_vsyncCallback userCallback;
    void *userData;
    AChoreographer *choreographer;
    AChoreographerFrameCallbackData callbackData;
} VsyncCallbackContext;

/* Adapter that translates OH callback to Android callback */
static void oh_to_android_vsync_adapter(long long timestamp, void *data) {
    VsyncCallbackContext *ctx = (VsyncCallbackContext *)data;
    if (!ctx || !ctx->userCallback) return;

    ctx->callbackData.frameTimeNanos = (int64_t)timestamp;
    ctx->callbackData.vsyncId = 0; /* OH does not provide vsync ID */
    ctx->callbackData.deadlineNanos = (int64_t)timestamp + 16666666; /* +16.6ms */

    ctx->userCallback(&ctx->callbackData, ctx->userData);
    free(ctx);
}

typedef struct {
    AChoreographer_extendedFrameCallback userCallback;
    void *userData;
} ExtendedCallbackContext;

static void oh_to_android_extended_adapter(long long timestamp, void *data) {
    ExtendedCallbackContext *ctx = (ExtendedCallbackContext *)data;
    if (!ctx || !ctx->userCallback) return;

    int64_t vsyncId = 0;
    int64_t frameTimeNanos = (int64_t)timestamp;
    int64_t frameDeadline = frameTimeNanos + 16666666;

    ctx->userCallback(vsyncId, frameTimeNanos, frameDeadline, ctx->userData);
    free(ctx);
}

/* ============================================================ */
/* AChoreographer API exports                                    */
/* ============================================================ */

AChoreographer *AChoreographer_create(void) {
    AChoreographer *c = (AChoreographer *)calloc(1, sizeof(AChoreographer));
    if (!c) return NULL;
    strcpy(c->name, "hwui_vsync");
    c->vsync = OH_NativeVSync_Create(c->name, (unsigned int)strlen(c->name));
    if (!c->vsync) {
        free(c);
        return NULL;
    }
    return c;
}

void AChoreographer_destroy(AChoreographer *choreographer) {
    if (!choreographer) return;
    if (choreographer->vsync) {
        OH_NativeVSync_Destroy(choreographer->vsync);
    }
    free(choreographer);
}

AChoreographer *AChoreographer_getInstance(void) {
    /* Lazy global instance */
    static AChoreographer *g_instance = NULL;
    if (!g_instance) {
        g_instance = AChoreographer_create();
    }
    return g_instance;
}

int AChoreographer_postVsyncCallback(AChoreographer *choreographer,
                                     AChoreographer_vsyncCallback callback,
                                     void *data) {
    if (!choreographer || !choreographer->vsync || !callback) return -1;

    VsyncCallbackContext *ctx = (VsyncCallbackContext *)calloc(1, sizeof(VsyncCallbackContext));
    if (!ctx) return -1;
    ctx->userCallback = callback;
    ctx->userData = data;
    ctx->choreographer = choreographer;

    return OH_NativeVSync_RequestFrame(choreographer->vsync,
                                       oh_to_android_vsync_adapter, ctx);
}

int AChoreographer_postExtendedFrameCallback(AChoreographer *choreographer,
                                              AChoreographer_extendedFrameCallback callback,
                                              void *data) {
    if (!choreographer || !choreographer->vsync || !callback) return -1;

    ExtendedCallbackContext *ctx = (ExtendedCallbackContext *)calloc(1, sizeof(ExtendedCallbackContext));
    if (!ctx) return -1;
    ctx->userCallback = callback;
    ctx->userData = data;

    return OH_NativeVSync_RequestFrame(choreographer->vsync,
                                       oh_to_android_extended_adapter, ctx);
}

void AChoreographer_postFrameCallback(AChoreographer *choreographer,
                                       AChoreographer_frameCallback callback, void *data) {
    /* Old API — wrap into extended */
    (void)choreographer; (void)callback; (void)data;
}

void AChoreographer_postFrameCallback64(AChoreographer *choreographer,
                                         AChoreographer_frameCallback64 callback, void *data) {
    (void)choreographer; (void)callback; (void)data;
}

int AChoreographer_registerRefreshRateCallback(AChoreographer *choreographer,
                                                AChoreographer_refreshRateCallback callback,
                                                void *data) {
    /* OH does not provide refresh rate change notifications via NativeVSync */
    (void)choreographer; (void)callback; (void)data;
    return 0;
}

int AChoreographer_unregisterRefreshRateCallback(AChoreographer *choreographer,
                                                  AChoreographer_refreshRateCallback callback,
                                                  void *data) {
    (void)choreographer; (void)callback; (void)data;
    return 0;
}

int AChoreographer_getFd(const AChoreographer *choreographer) {
    /* Android uses fd-based polling; OH NativeVSync is callback-based.
     * Returning -1 means "no fd to poll", caller must use callback path. */
    (void)choreographer;
    return -1;
}

void AChoreographer_handlePendingEvents(AChoreographer *choreographer, void *data) {
    /* OH NativeVSync auto-dispatches; nothing to do here */
    (void)choreographer; (void)data;
}

int64_t AChoreographer_getFrameInterval(const AChoreographer *choreographer) {
    if (!choreographer || !choreographer->vsync) return 16666666; /* 60Hz default */
    long long period = 0;
    OH_NativeVSync_GetPeriod((OH_NativeVSync *)choreographer->vsync, &period);
    return period > 0 ? (int64_t)period : 16666666;
}

/* ============================================================ */
/* AChoreographerFrameCallbackData accessors                     */
/* ============================================================ */

int64_t AChoreographerFrameCallbackData_getFrameTimeNanos(
    const AChoreographerFrameCallbackData *data) {
    return data ? data->frameTimeNanos : 0;
}

int64_t AChoreographerFrameCallbackData_getVsyncId(
    const AChoreographerFrameCallbackData *data) {
    return data ? data->vsyncId : 0;
}

size_t AChoreographerFrameCallbackData_getFrameTimelinesLength(
    const AChoreographerFrameCallbackData *data) {
    (void)data;
    return 1;
}

size_t AChoreographerFrameCallbackData_getPreferredFrameTimelineIndex(
    const AChoreographerFrameCallbackData *data) {
    (void)data;
    return 0;
}

int64_t AChoreographerFrameCallbackData_getFrameTimelineVsyncId(
    const AChoreographerFrameCallbackData *data, size_t index) {
    (void)index;
    return data ? data->vsyncId : 0;
}

int64_t AChoreographerFrameCallbackData_getFrameTimelineExpectedPresentationTimeNanos(
    const AChoreographerFrameCallbackData *data, size_t index) {
    (void)index;
    return data ? data->frameTimeNanos : 0;
}

int64_t AChoreographerFrameCallbackData_getFrameTimelineDeadlineNanos(
    const AChoreographerFrameCallbackData *data, size_t index) {
    (void)index;
    return data ? data->deadlineNanos : 0;
}
