#!/usr/bin/env python3
"""apply_G214ay_skia_probe.py

G2.14ay — In-place patch SkiaOpenGLPipeline.cpp to add 3 probe sample points
that pinpoint where Skia replay stops writing pixels to the GL framebuffer.

After G2.14ax r2d confirmed:
  - hwui swap path IS taken (SwapHijack 5 fires)
  - EGLSurface IS correctly bound to ProducerSurface buffer (glClear-red wrote through)
  - But glReadPixels in swap-hijack returns 0 or stale color → Skia replay
    never wrote anything to the framebuffer

G2.14ay's 3 sample points distinguish B1/B2/B3 (SkSurface failure / replay
silent no-op / FlushAndSubmit failure):

Probe A — after SkSurface creation (after WrapBackendRenderTarget):
   - Log surface != nullptr, getCanvas() != nullptr, width/height
   - If surface is nullptr → B1 SkSurface creation failed (real fault)

Probe C — after SkiaPipeline::renderFrame() returns:
   - glReadPixels 4 points (tl/q/mid/br)
   - If all 0 → Skia replay never wrote to framebuffer (B3 ABI / GL state)
   - If non-zero → Skia DID write, but FlushAndSubmit or swap loses it

Probe D — after skgpu::ganesh::FlushAndSubmit():
   - glReadPixels 4 points again
   - Compare to C: same → flush is no-op; different → flush mutated buffer

Idempotent (skips on marker present); creates .bak.G214ay on first run.
"""
import os
import sys

AOSP_ROOT = os.environ.get("AOSP_ROOT", "/home/HanBingChen/aosp")
TARGET = os.path.join(AOSP_ROOT, "frameworks/base/libs/hwui/pipeline/skia/SkiaOpenGLPipeline.cpp")

MARKER = "G214ay_SkiaProbe"


# --- G2.14bd FIX: stencil 8 -> 0 to match RS / OH EGL config (no stencil request).
# blocker.txt headline hypothesis: hwui tells Skia stencil=8 but OH EGL window
# was created without EGL_STENCIL_SIZE (render_context_gl.cpp:168-169 config_attribs).
# m133 Ganesh pins clear-op to an internal RT proxy on stencil mismatch ->
# explains "raw glClear writes fbo 0 but SkCanvas::clear does not".
STENCIL_FIX_ANCHOR = """    GrBackendRenderTarget backendRT = GrBackendRenderTargets::MakeGL(frame.width(), frame.height(), 0, STENCIL_BUFFER_SIZE, fboInfo);"""

STENCIL_FIX_INSERT = """    // G2.14bd — stencil 8 -> 0 to align with RS m133 path and OH EGL config.
    // OH render_context_gl.cpp config_attribs omits EGL_STENCIL_SIZE -> attached
    // stencil bits = 0.  hwui claiming 8 causes m133 Ganesh to pin draw ops to
    // an internal RT proxy instead of fbo 0.  See doc blocker.txt headline hypothesis.
    GrBackendRenderTarget backendRT = GrBackendRenderTargets::MakeGL(frame.width(), frame.height(), 0, 0 /* was: STENCIL_BUFFER_SIZE */, fboInfo);"""


# --- Probe HEADER: file-level extern "C" declaration for g214az_dump_gl_state.
# Inserted near top of file (after #include block) so probe bodies can call
# the helper without function-body-scope extern declaration (which is C++ illegal).
HEADER_ANCHOR = """namespace android {
namespace uirenderer {
namespace skiapipeline {"""

HEADER_INSERT = """// G2.14az/G2.14bb helpers — defined in hwui_oh_abi_patch.cpp (§6/§7).
extern "C" void g214az_dump_gl_state(const char* tag, int n);
extern "C" uint32_t g214bb_raw_read_pixel(int x, int y);

namespace android {
namespace uirenderer {
namespace skiapipeline {"""


# --- Probe A: after WrapBackendRenderTarget / getBufferSkSurface ----
# Anchor: the closing brace of the if/else block plus the next line.
# We need a unique multi-line anchor.
A_ANCHOR = """        preTransform = SkMatrix::I();
    }

    SkPoint lightCenter = preTransform.mapXY(lightGeometry.center.x, lightGeometry.center.y);"""

A_INSERT = """        preTransform = SkMatrix::I();
    }

    // ==================================================================
    // G2.14ay probe A — log SkSurface state right after creation.
    // G2.14az extension — also dump GL state (viewport / scissor / fb / blend).
    // G2.14ba extension — test SkCanvas clear() directly: if probe C reads
    // red, SkCanvas→framebuffer path is healthy and renderRenderNodes is
    // failing; if probe C reads 0, even direct SkCanvas clear isn't reaching
    // the framebuffer (OH Skia internal failure).
    // marker: G214ay_SkiaProbe_A
    // ==================================================================
    {
        // (extern declaration is at file top, inserted by HEADER patch)
        static std::atomic<int> g_a_count{0};
        int n = ++g_a_count;
        if (n <= 5 || n % 60 == 0) {
            SkCanvas* sc = surface ? surface->getCanvas() : nullptr;
            int w = surface ? surface->width() : -1;
            int h = surface ? surface->height() : -1;
            void* rctx = surface ? surface->recordingContext() : nullptr;
            fprintf(stderr, "[G214ay] A #%d surface=%p canvas=%p w=%d h=%d rctx=%p hwBuf=%d rn=%zu\\n",
                    n, (void*)surface.get(), (void*)sc, w, h, rctx,
                    mHardwareBuffer ? 1 : 0, renderNodes.size());
            // G2.14bc — GrDirectContext state (abandoned / backend / oomed).
            // If abandoned=true, all Skia GPU calls silently no-op — direct match
            // for "SkCanvas->clear writes nothing".
            GrDirectContext* gctx = mRenderThread.getGrContext();
            if (gctx) {
                int aban = gctx->abandoned() ? 1 : 0;
                int back = (int)gctx->backend();   // 0=Metal 1=OpenGL 2=Vulkan 3=Direct3D 4=Mock
                int oom  = gctx->oomed() ? 1 : 0;
                fprintf(stderr, "[G214bc] A #%d grCtx=%p abandoned=%d backend=%d oomed=%d\\n",
                        n, (void*)gctx, aban, back, oom);
            } else {
                fprintf(stderr, "[G214bc] A #%d grCtx=null\\n", n);
            }
            g214az_dump_gl_state("A-preRender", n);
            // G2.14ba — direct SkCanvas->clear(red) test before renderFrame.
            // SK_ColorRED = 0xFFFF0000 (ARGB).  This calls the same Skia
            // primitive that hwui's RenderNodeDrawable::onDraw eventually
            // invokes via DisplayList ops — but we hit it directly, bypassing
            // RenderNodeDrawable entirely.
            if (sc) {
                sc->clear(0xFFFF0000u);
                fprintf(stderr, "[G214ba] A #%d SkCanvas->clear(red) issued\\n", n);
                // G2.14bb — read back via TWO paths to compare:
                //   (a) SkSurface::readPixels — Skia's view of the surface
                //   (b) raw glReadPixels       — actual GL framebuffer 0
                // If Skia binds an internal FBO instead of fb 0, (a) returns red
                // (Skia sees its own write) but (b) returns 0 (real fb untouched).
                SkImageInfo info1 = SkImageInfo::Make(1, 1, kRGBA_8888_SkColorType, kUnpremul_SkAlphaType);
                uint32_t sk_px = 0;
                bool sk_ok = surface->readPixels(info1, &sk_px, 4, 360, 640);
                uint32_t gl_px = g214bb_raw_read_pixel(360, 640);
                fprintf(stderr, "[G214bb] A #%d post-SkClear sk_readPixels=0x%08x (ok=%d) raw_glReadPixels=0x%08x\\n",
                        n, sk_px, sk_ok?1:0, gl_px);
            }
        }
    }

    SkPoint lightCenter = preTransform.mapXY(lightGeometry.center.x, lightGeometry.center.y);"""


# --- Probe C + D: after renderFrame() + after FlushAndSubmit ----
# Anchor includes the renderFrame call and surrounding lines.
CD_ANCHOR = """    renderFrame(*layerUpdateQueue, dirty, renderNodes, opaque, contentDrawBounds, surface,
                preTransform);

    // Draw visual debugging features
    if (CC_UNLIKELY(Properties::showDirtyRegions ||
                    ProfileType::None != Properties::getProfileType())) {
        SkCanvas* profileCanvas = surface->getCanvas();
        SkiaProfileRenderer profileRenderer(profileCanvas, frame.width(), frame.height());
        profiler->draw(profileRenderer);
    }

    {
        ATRACE_NAME("flush commands");
        skgpu::ganesh::FlushAndSubmit(surface.get());
    }
    layerUpdateQueue->clear();"""

CD_INSERT = """    renderFrame(*layerUpdateQueue, dirty, renderNodes, opaque, contentDrawBounds, surface,
                preTransform);

    // ==================================================================
    // G2.14ay probe C — read pixel value via SkSurface::readPixels.
    // SkSurface API handles GPU stall + GL fb readback internally.
    // If Skia replay actually wrote pixels, readPixels returns non-zero.
    // marker: G214ay_SkiaProbe_C
    // ==================================================================
    {
        static std::atomic<int> g_c_count{0};
        int n = ++g_c_count;
        if (surface && (n <= 5 || n % 60 == 0)) {
            SkImageInfo info1 = SkImageInfo::Make(1, 1, kRGBA_8888_SkColorType, kUnpremul_SkAlphaType);
            uint32_t tl = 0, q = 0, mid = 0, br = 0;
            bool ok_tl  = surface->readPixels(info1, &tl,  4, 0,   0);
            bool ok_q   = surface->readPixels(info1, &q,   4, 180, 320);
            bool ok_mid = surface->readPixels(info1, &mid, 4, 360, 640);
            bool ok_br  = surface->readPixels(info1, &br,  4, 719, 1279);
            fprintf(stderr, "[G214ay] C #%d post-renderFrame tl=0x%08x q=0x%08x mid=0x%08x br=0x%08x ok=%d%d%d%d\\n",
                    n, tl, q, mid, br, ok_tl?1:0, ok_q?1:0, ok_mid?1:0, ok_br?1:0);
        }
    }

    // Draw visual debugging features
    if (CC_UNLIKELY(Properties::showDirtyRegions ||
                    ProfileType::None != Properties::getProfileType())) {
        SkCanvas* profileCanvas = surface->getCanvas();
        SkiaProfileRenderer profileRenderer(profileCanvas, frame.width(), frame.height());
        profiler->draw(profileRenderer);
    }

    {
        ATRACE_NAME("flush commands");
        skgpu::ganesh::FlushAndSubmit(surface.get());
    }

    // ==================================================================
    // G2.14ay probe D — SkSurface::readPixels after FlushAndSubmit.
    // Compare to C: flush may have mutated framebuffer state.
    // marker: G214ay_SkiaProbe_D
    // ==================================================================
    {
        static std::atomic<int> g_d_count{0};
        int n = ++g_d_count;
        if (surface && (n <= 5 || n % 60 == 0)) {
            SkImageInfo info1 = SkImageInfo::Make(1, 1, kRGBA_8888_SkColorType, kUnpremul_SkAlphaType);
            uint32_t tl = 0, q = 0, mid = 0, br = 0;
            bool ok_tl  = surface->readPixels(info1, &tl,  4, 0,   0);
            bool ok_q   = surface->readPixels(info1, &q,   4, 180, 320);
            bool ok_mid = surface->readPixels(info1, &mid, 4, 360, 640);
            bool ok_br  = surface->readPixels(info1, &br,  4, 719, 1279);
            fprintf(stderr, "[G214ay] D #%d post-FlushAndSubmit tl=0x%08x q=0x%08x mid=0x%08x br=0x%08x ok=%d%d%d%d\\n",
                    n, tl, q, mid, br, ok_tl?1:0, ok_q?1:0, ok_mid?1:0, ok_br?1:0);
        }
    }

    layerUpdateQueue->clear();"""


def patch(target, anchor, insert, marker_substr):
    with open(target, "r") as f:
        content = f.read()
    if marker_substr in content:
        print(f"  [skip] {marker_substr} already present")
        return False
    count = content.count(anchor)
    if count != 1:
        print(f"  [FAIL] anchor not unique (occurrences={count}) for marker {marker_substr}")
        return False
    content = content.replace(anchor, insert, 1)
    with open(target, "w") as f:
        f.write(content)
    print(f"  [OK] inserted probe {marker_substr}")
    return True


def main():
    if not os.path.exists(TARGET):
        print(f"ERROR: target not found: {TARGET}", file=sys.stderr)
        return 2

    backup = TARGET + ".bak.G214ay"
    if not os.path.exists(backup):
        import shutil
        shutil.copy2(TARGET, backup)
        print(f"  [backup] {backup}")

    print(f"Patching {TARGET}")
    ok_h = patch(TARGET, HEADER_ANCHOR, HEADER_INSERT, "G2.14az helper")
    ok_s = patch(TARGET, STENCIL_FIX_ANCHOR, STENCIL_FIX_INSERT, "G2.14bd")  # stencil fix
    ok_a = patch(TARGET, A_ANCHOR, A_INSERT, "G214ay_SkiaProbe_A")
    ok_cd = patch(TARGET, CD_ANCHOR, CD_INSERT, "G214ay_SkiaProbe_C")  # one shot inserts C+D

    if ok_h or ok_s or ok_a or ok_cd:
        print("Probes inserted. Re-compile libhwui to take effect.")
    else:
        print("No changes (already patched or anchors moved).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
