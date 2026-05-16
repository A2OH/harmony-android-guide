// ============================================================================
// oh_skia_ahb_shim.cpp
// Real implementation of GrAHardwareBufferUtils::{GetBackendFormat,
// MakeBackendTexture, GetSkColorTypeFromBufferFormat} for libhwui.
//
// Background:
//   AOSP libhwui references these Skia-Ganesh Android-specific functions via
//   AutoBackendTextureRelease.cpp. In OH's Skia m133 build, the source files
//   (GrAHardwareBufferUtils.cpp, AHardwareBufferGL.cpp, AHardwareBufferUtils.cpp)
//   are gated off behind SK_BUILD_FOR_ANDROID, so libskia_canvaskit.z.so does
//   not export these symbols. This shim provides them by re-implementing the
//   logic using OH Skia's exported public API (GrBackendFormats::MakeGL,
//   GrBackendTextures::MakeGL, GrBackendFormats::AsGLFormatEnum) plus
//   OH EGL/GLES extensions.
//
// Scope:
//   - GetBackendFormat / GetSkColorTypeFromBufferFormat: full real mapping,
//     identical to OH Skia source AHardwareBufferGL.cpp + AHardwareBufferUtils.cpp.
//   - MakeBackendTexture: real EGL import path (eglGetNativeClientBufferANDROID
//     → eglCreateImageKHR → glGenTextures → glEGLImageTargetTexture2DOES →
//     GrBackendTextures::MakeGL). Mirrors AOSP/OH Skia's make_gl_backend_texture.
//
// Linked against libskia_canvaskit.z.so (OH Skia), libEGL.so, libGLESv3.so.
// ============================================================================

// SK_BUILD_FOR_ANDROID provided via -D on command line (match OH Skia BUILD.gn).
#define GL_GLEXT_PROTOTYPES
#define EGL_EGLEXT_PROTOTYPES

// __INTRODUCED_IN is a Bionic macro; not defined in OH musl. Define as no-op
// so <android/hardware_buffer.h> attribute annotations parse.
#ifndef __INTRODUCED_IN
#define __INTRODUCED_IN(x)
#endif
#ifndef __ANDROID_USE_LIBABI_COMPAT
#define __ANDROID_USE_LIBABI_COMPAT 0
#endif

#include <android/hardware_buffer.h>

#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES/gl.h>
#include <GLES/glext.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <atomic>
#include <cstdio>

#include "include/core/SkImageInfo.h"
#include "include/gpu/ganesh/GrBackendSurface.h"
#include "include/gpu/ganesh/GrDirectContext.h"
#include "include/gpu/ganesh/gl/GrGLBackendSurface.h"
#include "include/gpu/ganesh/gl/GrGLTypes.h"
#include "include/android/GrAHardwareBufferUtils.h"

// G2.14au probe: use fprintf(stderr) so output goes to the child stderr
// redirect file (no extra NEEDED dependency that could fail dlopen).
#define G214AU_LOGI(fmt, ...) \
    std::fprintf(stderr, "[G214au_AHB] " fmt "\n", ##__VA_ARGS__)

// Minimal GL enum constants (match AOSP Skia's GrGLDefines)
static constexpr uint32_t GR_GL_TEXTURE_2D        = 0x0DE1;
static constexpr uint32_t GR_GL_TEXTURE_EXTERNAL  = 0x8D65;
static constexpr uint32_t GR_GL_RGBA8             = 0x8058;
static constexpr uint32_t GR_GL_RGBA16F           = 0x881A;
static constexpr uint32_t GR_GL_RGB565            = 0x8D62;
static constexpr uint32_t GR_GL_RGB10_A2          = 0x8059;
static constexpr uint32_t GR_GL_RGB8              = 0x8051;
static constexpr uint32_t GR_GL_R8                = 0x8229;

namespace GrAHardwareBufferUtils {

// ---------------------------------------------------------------------------
// GetBackendFormat: map AHardwareBuffer format → GrBackendFormat (GL backend)
// Mirrors OH Skia src/gpu/ganesh/gl/AHardwareBufferGL.cpp::GetGLBackendFormat.
// ---------------------------------------------------------------------------
GrBackendFormat GetBackendFormat(GrDirectContext* dContext,
                                 AHardwareBuffer* /*hardwareBuffer*/,
                                 uint32_t bufferFormat,
                                 bool requireKnownFormat) {
    // G2.14au probe: every 10th call (cheap function, may fire per-frame).
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 10 == 0) {
            G214AU_LOGI("GetBackendFormat #%d dCtx=%p fmt=0x%x reqKnown=%d",
                        n, (void*)dContext, bufferFormat, requireKnownFormat ? 1 : 0);
        }
    }

    if (!dContext || dContext->backend() != GrBackendApi::kOpenGL) {
        return GrBackendFormat();
    }
    switch (bufferFormat) {
        case AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM:
        case AHARDWAREBUFFER_FORMAT_R8G8B8X8_UNORM:
            return GrBackendFormats::MakeGL(GR_GL_RGBA8, GR_GL_TEXTURE_EXTERNAL);
        case AHARDWAREBUFFER_FORMAT_R16G16B16A16_FLOAT:
            return GrBackendFormats::MakeGL(GR_GL_RGBA16F, GR_GL_TEXTURE_EXTERNAL);
        case AHARDWAREBUFFER_FORMAT_R5G6B5_UNORM:
            return GrBackendFormats::MakeGL(GR_GL_RGB565, GR_GL_TEXTURE_EXTERNAL);
        case AHARDWAREBUFFER_FORMAT_R10G10B10A2_UNORM:
            return GrBackendFormats::MakeGL(GR_GL_RGB10_A2, GR_GL_TEXTURE_EXTERNAL);
        case AHARDWAREBUFFER_FORMAT_R8G8B8_UNORM:
            return GrBackendFormats::MakeGL(GR_GL_RGB8, GR_GL_TEXTURE_EXTERNAL);
        case AHARDWAREBUFFER_FORMAT_R8_UNORM:
            return GrBackendFormats::MakeGL(GR_GL_R8, GR_GL_TEXTURE_EXTERNAL);
        default:
            if (requireKnownFormat) {
                return GrBackendFormat();
            }
            return GrBackendFormats::MakeGL(GR_GL_RGBA8, GR_GL_TEXTURE_EXTERNAL);
    }
}

// ---------------------------------------------------------------------------
// GetSkColorTypeFromBufferFormat
// Mirrors OH Skia src/gpu/android/AHardwareBufferUtils.cpp.
// ---------------------------------------------------------------------------
SkColorType GetSkColorTypeFromBufferFormat(uint32_t bufferFormat) {
    // G2.14au probe: every 50th call (very cheap, may fire many times).
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 50 == 0) {
            G214AU_LOGI("GetSkColorTypeFromBufferFormat #%d fmt=0x%x", n, bufferFormat);
        }
    }
    switch (bufferFormat) {
        case AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM:      return kRGBA_8888_SkColorType;
        case AHARDWAREBUFFER_FORMAT_R8G8B8X8_UNORM:      return kRGB_888x_SkColorType;
        case AHARDWAREBUFFER_FORMAT_R16G16B16A16_FLOAT:  return kRGBA_F16_SkColorType;
        case AHARDWAREBUFFER_FORMAT_R5G6B5_UNORM:        return kRGB_565_SkColorType;
        case AHARDWAREBUFFER_FORMAT_R8G8B8_UNORM:        return kRGB_888x_SkColorType;
        case AHARDWAREBUFFER_FORMAT_R10G10B10A2_UNORM:   return kRGBA_1010102_SkColorType;
        case AHARDWAREBUFFER_FORMAT_R8_UNORM:            return kAlpha_8_SkColorType;
        default:
            return kRGBA_8888_SkColorType;
    }
}

// ---------------------------------------------------------------------------
// MakeBackendTexture
// Mirrors OH Skia src/gpu/ganesh/gl/AHardwareBufferGL.cpp::make_gl_backend_texture.
// Uses OH EGL/GLES platform SDK + exported Skia GrBackendTextures::MakeGL.
// ---------------------------------------------------------------------------

// Context that deleteProc / updateProc receive. Retains the EGLImage so that
// glDeleteTextures/eglDestroyImageKHR can be invoked on RenderThread teardown.
struct OhGLTextureCtx {
    GLuint     texID;
    EGLImageKHR image;
    EGLDisplay display;
    uint32_t   target;
};

static void oh_delete_gl_texture(void* context) {
    if (!context) return;
    OhGLTextureCtx* ctx = static_cast<OhGLTextureCtx*>(context);
    if (ctx->texID) {
        GLuint id = ctx->texID;
        glDeleteTextures(1, &id);
    }
    if (ctx->image != EGL_NO_IMAGE_KHR && ctx->display != EGL_NO_DISPLAY) {
        eglDestroyImageKHR(ctx->display, ctx->image);
    }
    delete ctx;
}

static void oh_update_gl_texture(void* context, GrDirectContext* dContext) {
    if (!context) return;
    OhGLTextureCtx* ctx = static_cast<OhGLTextureCtx*>(context);
    glBindTexture(ctx->target, ctx->texID);
    glEGLImageTargetTexture2DOES(ctx->target, ctx->image);
}

GrBackendTexture MakeBackendTexture(GrDirectContext* dContext,
                                    AHardwareBuffer* hardwareBuffer,
                                    int width, int height,
                                    DeleteImageProc* deleteProc,
                                    UpdateImageProc* updateProc,
                                    TexImageCtx* imageCtx,
                                    bool isProtectedContent,
                                    const GrBackendFormat& backendFormat,
                                    bool isRenderable,
                                    bool /*fromAndroidWindow*/) {
    // G2.14au probe: every call — this is the GPU backing-texture allocator,
    // should fire once per frame (RT) if hwui Ganesh GL pipeline is alive.
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        G214AU_LOGI("MakeBackendTexture #%d dCtx=%p hb=%p w=%d h=%d renderable=%d",
                    n, (void*)dContext, (void*)hardwareBuffer, width, height,
                    isRenderable ? 1 : 0);
    }

    if (!dContext || dContext->backend() != GrBackendApi::kOpenGL || !hardwareBuffer) {
        return GrBackendTexture();
    }

    // Clear stale GL errors
    while (glGetError() != GL_NO_ERROR) {}

    EGLClientBuffer clientBuffer = eglGetNativeClientBufferANDROID(hardwareBuffer);
    if (!clientBuffer) {
        return GrBackendTexture();
    }

    EGLint attribs[] = {
        EGL_IMAGE_PRESERVED_KHR, EGL_TRUE,
        isProtectedContent ? EGL_PROTECTED_CONTENT_EXT : EGL_NONE,
        isProtectedContent ? EGL_TRUE : EGL_NONE,
        EGL_NONE
    };
    EGLDisplay display = eglGetCurrentDisplay();
    if (display == EGL_NO_DISPLAY) {
        return GrBackendTexture();
    }

    EGLImageKHR image = eglCreateImageKHR(display, EGL_NO_CONTEXT,
                                          EGL_NATIVE_BUFFER_ANDROID,
                                          clientBuffer, attribs);
    if (image == EGL_NO_IMAGE_KHR) {
        return GrBackendTexture();
    }

    GLuint texID = 0;
    glGenTextures(1, &texID);
    if (!texID) {
        eglDestroyImageKHR(display, image);
        return GrBackendTexture();
    }

    uint32_t target = isRenderable ? GR_GL_TEXTURE_2D : GR_GL_TEXTURE_EXTERNAL;
    glBindTexture(target, texID);
    if (glGetError() != GL_NO_ERROR) {
        glDeleteTextures(1, &texID);
        eglDestroyImageKHR(display, image);
        return GrBackendTexture();
    }

    glEGLImageTargetTexture2DOES(target, image);
    if (glGetError() != GL_NO_ERROR) {
        glDeleteTextures(1, &texID);
        eglDestroyImageKHR(display, image);
        return GrBackendTexture();
    }

    GrGLTextureInfo textureInfo{};
    textureInfo.fID     = texID;
    textureInfo.fTarget = target;
    textureInfo.fFormat = GrBackendFormats::AsGLFormatEnum(backendFormat);

    // Allocate context for caller-managed cleanup callbacks
    OhGLTextureCtx* ctx = new OhGLTextureCtx{texID, image, display, target};
    if (deleteProc) *deleteProc = oh_delete_gl_texture;
    if (updateProc) *updateProc = oh_update_gl_texture;
    if (imageCtx)   *imageCtx   = ctx;

    return GrBackendTextures::MakeGL(width, height, skgpu::Mipmapped::kNo, textureInfo);
}

}  // namespace GrAHardwareBufferUtils
