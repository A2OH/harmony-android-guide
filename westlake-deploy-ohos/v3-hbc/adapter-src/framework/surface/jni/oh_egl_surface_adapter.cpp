/*
 * oh_egl_surface_adapter.cpp
 *
 * EGL Surface adapter implementation.
 *
 * Reference: OH RenderService source code:
 *   - rs_surface_ohos_gl.cpp  (NativeWindow + EGL surface creation)
 *   - render_context_gl.cpp   (EGL display + context initialization)
 *
 * EGL Platform: EGL_PLATFORM_OHOS_KHR (0x34E0)
 *   OH's EGL wrapper (egl_wrapper_display.cpp) requires OHNativeWindow*,
 *   not Android's ANativeWindow*. This adapter uses OH's native type
 *   via CreateNativeWindowFromSurface().
 */
#include "oh_egl_surface_adapter.h"

// OH NativeWindow C API: CreateNativeWindowFromSurface, NativeWindowHandleOpt,
// DestoryNativeWindow, SET_FORMAT, SET_USAGE, SET_BUFFER_GEOMETRY. The
// unprefixed inner-API symbols live in graphic_surface/.../inner_api/surface/window.h.
// Plain <window.h> resolves to wm/window.h first; we disambiguate by going one
// directory up and using the unique relative path "surface/window.h"
// (build script adds -I.../inner_api for this).
#include "surface/window.h"
#include <surface.h>           // OHOS::Surface

#include <GLES2/gl2.h>

#include <hilog/log.h>
#include <dlfcn.h>

#undef LOG_TAG
#define LOG_TAG "OH_EglAdapter"
#undef LOG_DOMAIN
#define LOG_DOMAIN 0xD002901

#define LOGI(...) HILOG_INFO(LOG_CORE, __VA_ARGS__)
#define LOGW(...) HILOG_WARN(LOG_CORE, __VA_ARGS__)
#define LOGE(...) HILOG_ERROR(LOG_CORE, __VA_ARGS__)

// EGL_PLATFORM_OHOS_KHR — OH-specific EGL platform type
// Defined in OH's EGL wrapper; not in standard EGL headers
#ifndef EGL_PLATFORM_OHOS_KHR
#define EGL_PLATFORM_OHOS_KHR 0x34E0
#endif

// OH buffer usage flags (from surface_type.h)
#ifndef BUFFER_USAGE_CPU_READ
#define BUFFER_USAGE_CPU_READ       (1ULL << 0)
#define BUFFER_USAGE_MEM_DMA        (1ULL << 3)
#define BUFFER_USAGE_HW_RENDER      (1ULL << 8)
#define BUFFER_USAGE_HW_TEXTURE     (1ULL << 9)
#define BUFFER_USAGE_HW_COMPOSER    (1ULL << 10)
#endif

// OH pixel format
#ifndef GRAPHIC_PIXEL_FMT_RGBA_8888
#define GRAPHIC_PIXEL_FMT_RGBA_8888 12
#endif

namespace oh_adapter {

// ============================================================
// Singleton
// ============================================================

OHEglSurfaceAdapter& OHEglSurfaceAdapter::getInstance() {
    static OHEglSurfaceAdapter instance;
    return instance;
}

OHEglSurfaceAdapter::~OHEglSurfaceAdapter() {
    if (initialized_) {
        shutdown();
    }
}

// ============================================================
// EGL Initialization
// ============================================================

bool OHEglSurfaceAdapter::initialize() {
    std::lock_guard<std::mutex> lock(mutex_);

    if (initialized_) {
        LOGI("Already initialized");
        return true;
    }

    // 1. Get EGL display using OH platform type
    //    Reference: render_context_gl.cpp:149
    PFN_eglGetPlatformDisplayEXT eglGetPlatformDisplayEXT = nullptr;
    eglGetPlatformDisplayEXT = reinterpret_cast<PFN_eglGetPlatformDisplayEXT>(
        eglGetProcAddress("eglGetPlatformDisplayEXT"));

    if (eglGetPlatformDisplayEXT) {
        eglDisplay_ = eglGetPlatformDisplayEXT(EGL_PLATFORM_OHOS_KHR,
                                                EGL_DEFAULT_DISPLAY, nullptr);
        LOGI("Using EGL_PLATFORM_OHOS_KHR platform display");
    }

    if (eglDisplay_ == EGL_NO_DISPLAY) {
        // Fallback to default display
        eglDisplay_ = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        LOGI("Fallback to eglGetDisplay(EGL_DEFAULT_DISPLAY)");
    }

    if (eglDisplay_ == EGL_NO_DISPLAY) {
        LOGE("Failed to get EGL display");
        return false;
    }

    // 2. Initialize EGL
    EGLint major = 0, minor = 0;
    if (!eglInitialize(eglDisplay_, &major, &minor)) {
        LOGE("eglInitialize failed: 0x%{public}x", eglGetError());
        return false;
    }
    LOGI("EGL initialized: %{public}d.%{public}d", major, minor);

    // 3. Bind OpenGL ES API
    eglBindAPI(EGL_OPENGL_ES_API);

    // 4. Choose config — RGBA8888, OpenGL ES 3.0
    //    Reference: render_context_gl.cpp:107-135
    EGLint configAttribs[] = {
        EGL_SURFACE_TYPE,    EGL_WINDOW_BIT,
        EGL_RED_SIZE,        8,
        EGL_GREEN_SIZE,      8,
        EGL_BLUE_SIZE,       8,
        EGL_ALPHA_SIZE,      8,
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT_KHR,
        EGL_NONE
    };

    EGLint numConfigs = 0;
    if (!eglChooseConfig(eglDisplay_, configAttribs, &eglConfig_, 1, &numConfigs) ||
        numConfigs == 0) {
        // Fallback to ES 2.0
        configAttribs[11] = EGL_OPENGL_ES2_BIT;
        if (!eglChooseConfig(eglDisplay_, configAttribs, &eglConfig_, 1, &numConfigs) ||
            numConfigs == 0) {
            LOGE("eglChooseConfig failed: no suitable config");
            return false;
        }
        LOGW("Using OpenGL ES 2.0 (ES 3.0 not available)");
    }

    // 5. Create EGL context — OpenGL ES 2 client version
    //    (EGL_CONTEXT_CLIENT_VERSION=2 supports both ES 2.0 and 3.0 contexts)
    //    Reference: render_context_gl.cpp:137-145
    EGLint contextAttribs[] = {
        EGL_CONTEXT_CLIENT_VERSION, 2,
        EGL_NONE
    };
    eglContext_ = eglCreateContext(eglDisplay_, eglConfig_,
                                   EGL_NO_CONTEXT, contextAttribs);
    if (eglContext_ == EGL_NO_CONTEXT) {
        LOGE("eglCreateContext failed: 0x%{public}x", eglGetError());
        return false;
    }

    initialized_ = true;
    LOGI("EGL context created successfully");

    // Log GL info
    makeNothingCurrent();

    return true;
}

void OHEglSurfaceAdapter::shutdown() {
    std::lock_guard<std::mutex> lock(mutex_);

    if (!initialized_) return;

    eglMakeCurrent(eglDisplay_, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);

    if (eglContext_ != EGL_NO_CONTEXT) {
        eglDestroyContext(eglDisplay_, eglContext_);
        eglContext_ = EGL_NO_CONTEXT;
    }

    eglTerminate(eglDisplay_);
    eglDisplay_ = EGL_NO_DISPLAY;

    initialized_ = false;
    LOGI("EGL shutdown complete");
}

// ============================================================
// Surface Creation
// ============================================================

int64_t OHEglSurfaceAdapter::createEglSurface(OHOS::sptr<OHOS::Surface> surface,
                                               int32_t width, int32_t height) {
    std::lock_guard<std::mutex> lock(mutex_);

    if (!initialized_) {
        LOGE("createEglSurface: not initialized");
        return 0;
    }

    if (!surface) {
        LOGE("createEglSurface: null surface");
        return 0;
    }

    auto* state = new EglWindowState();
    state->width = width;
    state->height = height;

    // 1. Create OH NativeWindow from Surface
    //    Reference: rs_surface_ohos_gl.cpp:61
    //    CreateNativeWindowFromSurface expects void* pointing to sptr<Surface>
    state->nativeWindow = CreateNativeWindowFromSurface(
        reinterpret_cast<void*>(&surface));

    if (!state->nativeWindow) {
        LOGE("CreateNativeWindowFromSurface failed");
        delete state;
        return 0;
    }

    // 2. Configure buffer properties
    //    Reference: rs_surface_ohos_gl.cpp:74-88
    NativeWindowHandleOpt(state->nativeWindow, SET_FORMAT,
                          GRAPHIC_PIXEL_FMT_RGBA_8888);

    uint64_t usage = BUFFER_USAGE_HW_RENDER | BUFFER_USAGE_HW_TEXTURE |
                     BUFFER_USAGE_HW_COMPOSER | BUFFER_USAGE_MEM_DMA;
    NativeWindowHandleOpt(state->nativeWindow, SET_USAGE, usage);

    NativeWindowHandleOpt(state->nativeWindow, SET_BUFFER_GEOMETRY, width, height);

    // 3. Create EGL surface
    //    Reference: rs_surface_ohos_gl.cpp:63
    state->eglSurface = eglCreateWindowSurface(
        eglDisplay_, eglConfig_,
        reinterpret_cast<EGLNativeWindowType>(state->nativeWindow),
        nullptr);

    if (state->eglSurface == EGL_NO_SURFACE) {
        LOGE("eglCreateWindowSurface failed: 0x%{public}x", eglGetError());
        DestoryNativeWindow(state->nativeWindow);
        delete state;
        return 0;
    }

    state->active = true;
    LOGI("EGL surface created: %{public}dx%{public}d, surface=%{public}p",
         width, height, state->eglSurface);

    return reinterpret_cast<int64_t>(state);
}

// ============================================================
// Surface Operations
// ============================================================

bool OHEglSurfaceAdapter::makeCurrent(int64_t handle) {
    if (!handle || !initialized_) return false;

    auto* state = reinterpret_cast<EglWindowState*>(handle);
    if (!state->active || state->eglSurface == EGL_NO_SURFACE) {
        LOGE("makeCurrent: invalid surface state");
        return false;
    }

    if (!eglMakeCurrent(eglDisplay_, state->eglSurface,
                        state->eglSurface, eglContext_)) {
        LOGE("eglMakeCurrent failed: 0x%{public}x", eglGetError());
        return false;
    }
    return true;
}

bool OHEglSurfaceAdapter::swapBuffers(int64_t handle) {
    if (!handle || !initialized_) return false;

    auto* state = reinterpret_cast<EglWindowState*>(handle);
    if (!state->active || state->eglSurface == EGL_NO_SURFACE) {
        LOGE("swapBuffers: invalid surface state");
        return false;
    }

    if (!eglSwapBuffers(eglDisplay_, state->eglSurface)) {
        EGLint err = eglGetError();
        if (err == EGL_BAD_SURFACE) {
            LOGW("swapBuffers: EGL_BAD_SURFACE (surface may have been destroyed)");
            state->active = false;
        } else {
            LOGE("eglSwapBuffers failed: 0x%{public}x", err);
        }
        return false;
    }
    return true;
}

void OHEglSurfaceAdapter::updateGeometry(int64_t handle,
                                          int32_t width, int32_t height) {
    if (!handle) return;

    auto* state = reinterpret_cast<EglWindowState*>(handle);
    if (!state->nativeWindow) return;

    NativeWindowHandleOpt(state->nativeWindow, SET_BUFFER_GEOMETRY, width, height);
    state->width = width;
    state->height = height;

    LOGI("updateGeometry: %{public}dx%{public}d", width, height);
}

void OHEglSurfaceAdapter::destroyEglSurface(int64_t handle) {
    std::lock_guard<std::mutex> lock(mutex_);

    if (!handle) return;

    auto* state = reinterpret_cast<EglWindowState*>(handle);

    // Unbind if current
    eglMakeCurrent(eglDisplay_, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);

    if (state->eglSurface != EGL_NO_SURFACE) {
        eglDestroySurface(eglDisplay_, state->eglSurface);
        state->eglSurface = EGL_NO_SURFACE;
    }

    if (state->nativeWindow) {
        DestoryNativeWindow(state->nativeWindow);
        state->nativeWindow = nullptr;
    }

    state->active = false;
    LOGI("EGL surface destroyed");

    delete state;
}

void OHEglSurfaceAdapter::makeNothingCurrent() {
    if (!initialized_) return;
    eglMakeCurrent(eglDisplay_, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
}

}  // namespace oh_adapter
