#!/usr/bin/env python3
"""apply_G214au_r4_probes.py

G2.14au r4 — In-place patch 5 hwui RT call sites to add probe traces.

Targets (AOSP 14 SkiaOpenGLPipeline frame flow, in order):
    1. CanvasContext::draw                    — RT frame main entry
    2. SkiaOpenGLPipeline::draw               — Skia pipeline draw entry
    3. RenderNodeDrawable::onDraw             — DisplayList replay entry
    4. SkiaOpenGLPipeline::swapBuffers        — pipeline-level swap (wraps EglManager)
    5. EglManager::swapBuffers                — real eglSwapBuffers call site

Each probe is a static atomic counter + fprintf(stderr) at function entry,
written under tag "G214au_HWR".  Idempotent (skip if marker already present)
and creates a per-file .bak.G214au_r4 on first run for rollback.

Project rule compliance:
  - Patches AOSP source IN-PLACE under ~/aosp/ (not via cp .bak then revert).
  - The .bak.G214au_r4 is the rollback reference only; subsequent builds use
    the in-place patched file (per CLAUDE.md "patch then keep" rule).
  - Companion entry will be added to doc/build_patch_log.html G2.14au r4.
"""
import os
import shutil
import sys

AOSP_ROOT = os.environ.get("AOSP_ROOT", "/home/HanBingChen/aosp")
HWUI = os.path.join(AOSP_ROOT, "frameworks/base/libs/hwui")

PROBES = [
    {
        "file": "renderthread/CanvasContext.cpp",
        "needle": "void CanvasContext::draw(bool solelyTextureViewUpdates) {",
        "marker": "G214au_HWR_CanvasCtxDraw",
        "trace": """
    // G2.14au r4 probe — CanvasContext::draw entry
    // marker: G214au_HWR_CanvasCtxDraw
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 30 == 0) {
            fprintf(stderr, "[G214au_HWR] CanvasContext::draw #%d solelyTextureView=%d\\n",
                    n, solelyTextureViewUpdates ? 1 : 0);
        }
    }
""",
    },
    {
        "file": "pipeline/skia/SkiaOpenGLPipeline.cpp",
        "needle": "        const std::vector<sp<RenderNode>>& renderNodes, FrameInfoVisualizer* profiler,\n        const HardwareBufferRenderParams& bufferParams) {",
        "marker": "G214au_HWR_SkiaGLDraw",
        "trace": """
    // G2.14au r4 probe — SkiaOpenGLPipeline::draw entry
    // marker: G214au_HWR_SkiaGLDraw
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 30 == 0) {
            fprintf(stderr, "[G214au_HWR] SkiaOpenGLPipeline::draw #%d opaque=%d renderNodes=%zu\\n",
                    n, opaque ? 1 : 0, renderNodes.size());
        }
    }
""",
    },
    {
        "file": "pipeline/skia/RenderNodeDrawable.cpp",
        "needle": "void RenderNodeDrawable::onDraw(SkCanvas* canvas) {",
        "marker": "G214au_HWR_RNDOnDraw",
        "trace": """
    // G2.14au r4 probe — RenderNodeDrawable::onDraw entry (DisplayList replay)
    // marker: G214au_HWR_RNDOnDraw
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 100 == 0) {
            fprintf(stderr, "[G214au_HWR] RenderNodeDrawable::onDraw #%d canvas=%p inReorder=%d\\n",
                    n, (void*)canvas, mInReorderingSection ? 1 : 0);
        }
    }
""",
    },
    {
        "file": "pipeline/skia/SkiaOpenGLPipeline.cpp",
        "needle": "bool SkiaOpenGLPipeline::swapBuffers(const Frame& frame, bool drew, const SkRect& screenDirty,",
        "marker": "G214au_HWR_SkiaGLSwap",
        "trace_after_brace": True,  # signature spans multiple lines; insert after the actual {
        "trace": """
    // G2.14au r4 probe — SkiaOpenGLPipeline::swapBuffers entry (pipeline-level)
    // marker: G214au_HWR_SkiaGLSwap
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 30 == 0) {
            fprintf(stderr, "[G214au_HWR] SkiaOpenGLPipeline::swapBuffers #%d drew=%d width=%d height=%d\\n",
                    n, drew ? 1 : 0, frame.width(), frame.height());
        }
    }
""",
    },
    {
        "file": "renderthread/EglManager.cpp",
        "needle": "bool EglManager::swapBuffers(const Frame& frame, const SkRect& screenDirty) {",
        "marker": "G214au_HWR_EglSwap",
        "trace": """
    // G2.14au r4 probe — EglManager::swapBuffers entry (real eglSwapBuffers call site)
    // marker: G214au_HWR_EglSwap
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        if (n == 1 || n % 30 == 0) {
            fprintf(stderr, "[G214au_HWR] EglManager::swapBuffers ENTRY #%d dpy=%p surface=%p\\n",
                    n, (void*)mEglDisplay, (void*)frame.mSurface);
        }
    }
""",
    },
]


def apply_probe(probe):
    path = os.path.join(HWUI, probe["file"])
    backup = path + ".bak.G214au_r4"
    label = probe["file"] + " :: " + probe["marker"]

    if not os.path.isfile(path):
        print(f"[FAIL] {label} — source not found: {path}")
        sys.exit(1)

    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    if probe["marker"] in content:
        print(f"[SKIP] {label} — already patched")
        return

    if probe["needle"] not in content:
        print(f"[FAIL] {label} — needle not found")
        print(f"       needle: {probe['needle'][:80]!r}")
        sys.exit(1)

    occurrences = content.count(probe["needle"])
    if occurrences != 1:
        print(f"[FAIL] {label} — needle appears {occurrences} times, expected exactly 1")
        sys.exit(1)

    # Take a backup on the first patching pass.
    if not os.path.exists(backup):
        shutil.copy2(path, backup)
        print(f"[BAK]  {label} -> {os.path.basename(backup)}")

    if probe.get("trace_after_brace"):
        # needle here is a multi-line signature prefix; find the next '{' on its line
        idx = content.index(probe["needle"])
        brace = content.find("{", idx + len(probe["needle"]))
        if brace < 0:
            print(f"[FAIL] {label} — no '{{' after needle")
            sys.exit(1)
        new_content = content[: brace + 1] + probe["trace"] + content[brace + 1 :]
    else:
        new_content = content.replace(
            probe["needle"], probe["needle"] + probe["trace"], 1
        )

    with open(path, "w", encoding="utf-8") as f:
        f.write(new_content)
    print(f"[OK]   {label} — probe inserted")


def main():
    print(f"Applying G2.14au r4 hwui RT probes under: {HWUI}")
    for probe in PROBES:
        apply_probe(probe)
    print("Done. Rebuild libhwui.so via build/compile_libhwui.sh + build/link_libhwui.sh")


if __name__ == "__main__":
    main()
