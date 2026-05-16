/*
 * oh_egl_surface_adapter.h
 *
 * EGL Surface adapter for hardware-accelerated rendering (Part II).
 *
 * Creates OH NativeWindow (OHNativeWindow*) from OH Surface and provides
 * EGL context management for libhwui's RenderThread.
 *
 * This follows the exact same pattern as OH RenderService's own EGL setup:
 *   rs_surface_ohos_gl.cpp: CreateNativeWindowFromSurface() + eglCreateWindowSurface()
 *
 * Key design decision:
 *   - Uses OH NativeWindow (NOT Android ANativeWindow)
 *   - OH EGL wrapper requires OHNativeWindow* (EGL_PLATFORM_OHOS_KHR)
 *   - EGL/GPU driver buffer management is handled entirely by OH NativeWindow
 *   - No need for OHGraphicBufferProducer in hardware rendering path
 *
 * Thread safety:
 *   - EGL context operations must be called from the same thread (RenderThread)
 *   - NativeWindow creation/destruction can be called from any thread
 */
#ifndef OH_EGL_SURFACE_ADAPTER_H
#define OH_EGL_SURFACE_ADAPTER_H

#include <cstdint>
#include <mutex>

// EGL headers
#include <EGL/egl.h>
#include <EGL/eglext.h>

// Forward declarations
struct NativeWindow;   // OH NativeWindow type (from native_window.h)
typedef struct NativeWindow OHNativeWindow;

namespace OHOS {
    template<typename T> class sptr;
    class Surface;
}

namespace oh_adapter {

/**
 * Per-window EGL state managed by OHEglSurfaceAdapter.
 */
struct EglWindowState {
    OHNativeWindow* nativeWindow = nullptr;   // OH NativeWindow (from CreateNativeWindowFromSurface)
    EGLSurface eglSurface = EGL_NO_SURFACE;
    int32_t width = 0;
    int32_t height = 0;
    bool active = false;
};

/**
 * OHEglSurfaceAdapter manages EGL context and surface creation for
 * hardware-accelerated rendering on OH.
 *
 * Singleton — one EGL display/context shared across all windows.
 * Each window gets its own EGLSurface backed by an OH NativeWindow.
 */
class OHEglSurfaceAdapter {
public:
    static OHEglSurfaceAdapter& getInstance();

    /**
     * Initialize EGL display and context.
     * Must be called once before any surface operations.
     *
     * Uses EGL_PLATFORM_OHOS_KHR (OH platform, not Android).
     *
     * @return true on success.
     */
    bool initialize();

    /**
     * Shutdown EGL — destroy context and display.
     */
    void shutdown();

    /**
     * Create an EGL-renderable surface from an OH Surface.
     *
     * Steps (matching RenderService rs_surface_ohos_gl.cpp:61-88):
     *   1. CreateNativeWindowFromSurface(&surface) → OHNativeWindow*
     *   2. NativeWindowHandleOpt(SET_FORMAT, RGBA_8888)
     *   3. NativeWindowHandleOpt(SET_USAGE, HW_RENDER | HW_TEXTURE | MEM_DMA)
     *   4. NativeWindowHandleOpt(SET_BUFFER_GEOMETRY, width, height)
     *   5. eglCreateWindowSurface(display, config, (EGLNativeWindowType)window)
     *
     * @param surface   OH Surface from RSSurfaceNode::GetSurface().
     * @param width     Surface width.
     * @param height    Surface height.
     * @return Opaque handle (cast of EglWindowState*), or 0 on failure.
     */
    int64_t createEglSurface(OHOS::sptr<OHOS::Surface> surface,
                             int32_t width, int32_t height);

    /**
     * Make the specified surface current for GL rendering.
     *
     * @param handle  Handle from createEglSurface().
     * @return true on success.
     */
    bool makeCurrent(int64_t handle);

    /**
     * Swap buffers (present rendered frame to RenderService).
     *
     * @param handle  Handle from createEglSurface().
     * @return true on success.
     */
    bool swapBuffers(int64_t handle);

    /**
     * Update surface geometry (on window resize).
     *
     * @param handle  Handle from createEglSurface().
     * @param width   New width.
     * @param height  New height.
     */
    void updateGeometry(int64_t handle, int32_t width, int32_t height);

    /**
     * Destroy an EGL surface and its NativeWindow.
     *
     * @param handle  Handle from createEglSurface().
     */
    void destroyEglSurface(int64_t handle);

    /**
     * Make no surface current (unbind GL context from surface).
     */
    void makeNothingCurrent();

    /**
     * Query EGL state.
     */
    bool isInitialized() const { return initialized_; }
    EGLDisplay getDisplay() const { return eglDisplay_; }
    EGLContext getContext() const { return eglContext_; }

private:
    OHEglSurfaceAdapter() = default;
    ~OHEglSurfaceAdapter();

    // Disallow copy
    OHEglSurfaceAdapter(const OHEglSurfaceAdapter&) = delete;
    OHEglSurfaceAdapter& operator=(const OHEglSurfaceAdapter&) = delete;

    // EGL function pointer for platform display
    using PFN_eglGetPlatformDisplayEXT =
        EGLDisplay (*)(EGLenum platform, void* native_display,
                       const EGLint* attrib_list);

    bool initialized_ = false;
    EGLDisplay eglDisplay_ = EGL_NO_DISPLAY;
    EGLContext eglContext_ = EGL_NO_CONTEXT;
    EGLConfig eglConfig_ = nullptr;

    std::mutex mutex_;
};

}  // namespace oh_adapter

#endif  // OH_EGL_SURFACE_ADAPTER_H
