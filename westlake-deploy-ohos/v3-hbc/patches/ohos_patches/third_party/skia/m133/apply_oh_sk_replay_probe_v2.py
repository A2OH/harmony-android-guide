#!/usr/bin/env python3
"""apply_oh_sk_replay_probe_v2.py — Step 1+2 deeper probes

Layered probes to find exactly which silent-skip path hwui's ops fall into,
between SkCanvas::onDrawXxx entry (where v1 already proved op arrives) and
GL framebuffer write (where G214ay C/D proves op never lands).

Layer chain probed (top to bottom):
  1. SkCanvas::onDrawRect/onDrawGlyphRunList/internalDrawPaint
       v1 probe at entry => count A
       v2 probe BEFORE topDevice()->drawXxx() => count B
       diff(A - B) = ops silently skipped by quickReject / no-layer path
  2. skgpu::ganesh::Device::drawPaint/drawRect/onDrawGlyphRunList
       v2 probe at entry => count C
       diff(B - C) = ops lost in dispatch (should be 0 unless something weird)
  3. skgpu::ganesh::SurfaceDrawContext::addDrawOp
       v2 probe at entry => count D
       diff(C - D) = ops dropped inside Device (e.g. SkPaintToGrPaint failure)
  4. If D ~= C ~= B but fbo still red => op is enqueued but never flushed
       => issue is downstream of addDrawOp (GrOpsTask / flush / GL backend)

Idempotent: skips on marker present; creates .bak.OH_SK_REPLAY_V2 first run.
"""
import os
import sys
import shutil

OH_ROOT = os.environ.get("OH_ROOT", "/home/HanBingChen/oh")

TARGETS = [
    os.path.join(OH_ROOT, "third_party/skia/m133/src/core/SkCanvas.cpp"),
    os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/Device.cpp"),
    os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/SurfaceDrawContext.cpp"),
]


def probe_block(marker, fmt, args_expr):
    """Build a self-contained probe block, throttled (5+100)."""
    return (
        '{\n'
        '            static std::atomic<int> g_' + marker + '{0};\n'
        '            int n = ++g_' + marker + ';\n'
        '            if (n <= 5 || n % 100 == 0) {\n'
        '                std::fprintf(stderr,\n'
        '                    "[' + marker + '] ' + fmt + '\\n",\n'
        '                    ' + args_expr + ');\n'
        '            }\n'
        '        }'
    )


# ---------------------------------------------------------------------------
# Patch 1: SkCanvas.cpp — DISPATCH probes right before topDevice()->drawXxx
# ---------------------------------------------------------------------------
SKCANVAS_PATCHES = [
    # onDrawRect: insert DISPATCH probe before the topDevice() call.
    {
        "marker": "OH_SK_DISPATCH_Rect",
        "anchor": (
            '    if (layer) {\n'
            '        this->topDevice()->drawRect(r, layer->paint());\n'
            '    }\n'
            '}\n'
            '\n'
            'void SkCanvas::onDrawRegion'
        ),
        "replace": (
            '    if (layer) {\n'
            '        // OH_SK_DISPATCH probe — onDrawRect DISPATCHED\n'
            '        ' + probe_block(
                "OH_SK_DISPATCH_Rect",
                "Rect DISPATCHED #%d canvas=%p clipEmpty=%d",
                "n, (void*)this, this->isClipEmpty()?1:0",
            ) + '\n'
            '        this->topDevice()->drawRect(r, layer->paint());\n'
            '    } else {\n'
            '        // OH_SK_DISPATCH probe — onDrawRect NO_LAYER (silent skip)\n'
            '        ' + probe_block(
                "OH_SK_DISPATCH_Rect_NOLAYER",
                "Rect NO_LAYER (silent skip) #%d canvas=%p",
                "n, (void*)this",
            ) + '\n'
            '    }\n'
            '}\n'
            '\n'
            'void SkCanvas::onDrawRegion'
        ),
    },

    # onDrawGlyphRunList (the SkCanvas-level): insert REJECT + DISPATCH probes.
    {
        "marker": "OH_SK_DISPATCH_Glyph",
        "anchor": (
            'void SkCanvas::onDrawGlyphRunList(const sktext::GlyphRunList& glyphRunList, const SkPaint& paint) {\n'
            '    SkRect bounds = glyphRunList.sourceBoundsWithOrigin();\n'
            '    if (this->internalQuickReject(bounds, paint)) {\n'
            '        return;\n'
            '    }\n'
        ),
        "replace": (
            'void SkCanvas::onDrawGlyphRunList(const sktext::GlyphRunList& glyphRunList, const SkPaint& paint) {\n'
            '    SkRect bounds = glyphRunList.sourceBoundsWithOrigin();\n'
            '    if (this->internalQuickReject(bounds, paint)) {\n'
            '        // OH_SK_DISPATCH probe — onDrawGlyphRunList REJECTED (silent skip via quickReject)\n'
            '        ' + probe_block(
                "OH_SK_DISPATCH_Glyph_REJ",
                "Glyph REJECTED #%d canvas=%p bounds=[%.1f,%.1f,%.1f,%.1f]",
                "n, (void*)this, (double)bounds.fLeft, (double)bounds.fTop, (double)bounds.fRight, (double)bounds.fBottom",
            ) + '\n'
            '        return;\n'
            '    }\n'
        ),
    },
    {
        "marker": "OH_SK_DISPATCH_Glyph2",
        "anchor": (
            '    auto layer = this->aboutToDraw(paint, &bounds, PredrawFlags::kSkipMaskFilterAutoLayer);\n'
            '    if (layer) {\n'
            '        this->topDevice()->drawGlyphRunList(this, glyphRunList, layer->paint());\n'
            '    }\n'
            '}\n'
            '\n'
            'sk_sp<Slug> SkCanvas::convertBlobToSlug'
        ),
        "replace": (
            '    auto layer = this->aboutToDraw(paint, &bounds, PredrawFlags::kSkipMaskFilterAutoLayer);\n'
            '    if (layer) {\n'
            '        // OH_SK_DISPATCH probe — onDrawGlyphRunList DISPATCHED\n'
            '        ' + probe_block(
                "OH_SK_DISPATCH_Glyph_OK",
                "Glyph DISPATCHED #%d canvas=%p clipEmpty=%d",
                "n, (void*)this, this->isClipEmpty()?1:0",
            ) + '\n'
            '        this->topDevice()->drawGlyphRunList(this, glyphRunList, layer->paint());\n'
            '    } else {\n'
            '        // OH_SK_DISPATCH probe — onDrawGlyphRunList NO_LAYER (silent skip)\n'
            '        ' + probe_block(
                "OH_SK_DISPATCH_Glyph_NOLAYER",
                "Glyph NO_LAYER (silent skip) #%d canvas=%p",
                "n, (void*)this",
            ) + '\n'
            '    }\n'
            '}\n'
            '\n'
            'sk_sp<Slug> SkCanvas::convertBlobToSlug'
        ),
    },

    # internalDrawPaint: insert DISPATCH + SKIP probes.
    {
        "marker": "OH_SK_DISPATCH_Paint",
        "anchor": (
            'void SkCanvas::internalDrawPaint(const SkPaint& paint) {\n'
            '    // drawPaint does not call internalQuickReject() because computing its geometry is not free\n'
            '    // (see getLocalClipBounds(), and the two conditions below are sufficient.\n'
            '    if (paint.nothingToDraw() || this->isClipEmpty()) {\n'
            '        return;\n'
            '    }\n'
            '\n'
            '    auto layer = this->aboutToDraw(paint, nullptr, PredrawFlags::kCheckForOverwrite);\n'
            '    if (layer) {\n'
            '        this->topDevice()->drawPaint(layer->paint());\n'
            '    }\n'
            '}\n'
        ),
        "replace": (
            'void SkCanvas::internalDrawPaint(const SkPaint& paint) {\n'
            '    if (paint.nothingToDraw() || this->isClipEmpty()) {\n'
            '        // OH_SK_DISPATCH probe — internalDrawPaint REJECTED (nothingToDraw or clipEmpty)\n'
            '        ' + probe_block(
                "OH_SK_DISPATCH_Paint_REJ",
                "Paint REJECTED #%d canvas=%p nothingToDraw=%d clipEmpty=%d",
                "n, (void*)this, paint.nothingToDraw()?1:0, this->isClipEmpty()?1:0",
            ) + '\n'
            '        return;\n'
            '    }\n'
            '\n'
            '    auto layer = this->aboutToDraw(paint, nullptr, PredrawFlags::kCheckForOverwrite);\n'
            '    if (layer) {\n'
            '        // OH_SK_DISPATCH probe — internalDrawPaint DISPATCHED\n'
            '        ' + probe_block(
                "OH_SK_DISPATCH_Paint_OK",
                "Paint DISPATCHED #%d canvas=%p",
                "n, (void*)this",
            ) + '\n'
            '        this->topDevice()->drawPaint(layer->paint());\n'
            '    } else {\n'
            '        // OH_SK_DISPATCH probe — internalDrawPaint NO_LAYER (silent skip)\n'
            '        ' + probe_block(
                "OH_SK_DISPATCH_Paint_NOLAYER",
                "Paint NO_LAYER (silent skip) #%d canvas=%p",
                "n, (void*)this",
            ) + '\n'
            '    }\n'
            '}\n'
        ),
    },
]


# ---------------------------------------------------------------------------
# Patch 2: Device.cpp — ENTRY probes for drawPaint/drawRect/onDrawGlyphRunList
# (Device.cpp already has <atomic> at line 107; needs <cstdio>)
# ---------------------------------------------------------------------------
DEVICE_PATCHES = [
    {
        "marker": "OH_SK_DEVICE_INC",
        "anchor": '#include <atomic>',
        "replace": (
            '#include <atomic>\n'
            '// OH_SK_DEVICE — added 2026-05-13 for silent-NOOP diagnosis\n'
            '#include <cstdio>'
        ),
    },
    {
        "marker": "OH_SK_DEVICE_Paint",
        "anchor": (
            'void Device::drawPaint(const SkPaint& paint) {\n'
            '    ASSERT_SINGLE_OWNER\n'
        ),
        "replace": (
            'void Device::drawPaint(const SkPaint& paint) {\n'
            '    // OH_SK_DEVICE probe — entry\n'
            '    ' + probe_block(
                "OH_SK_DEVICE_Paint",
                "Device::drawPaint #%d device=%p",
                "n, (void*)this",
            ) + '\n'
            '    ASSERT_SINGLE_OWNER\n'
        ),
    },
    {
        "marker": "OH_SK_DEVICE_Rect",
        "anchor": (
            'void Device::drawRect(const SkRect& rect, const SkPaint& paint) {\n'
            '    ASSERT_SINGLE_OWNER\n'
        ),
        "replace": (
            'void Device::drawRect(const SkRect& rect, const SkPaint& paint) {\n'
            '    // OH_SK_DEVICE probe — entry\n'
            '    ' + probe_block(
                "OH_SK_DEVICE_Rect",
                "Device::drawRect #%d device=%p l=%.1f t=%.1f r=%.1f b=%.1f",
                "n, (void*)this, (double)rect.fLeft, (double)rect.fTop, (double)rect.fRight, (double)rect.fBottom",
            ) + '\n'
            '    ASSERT_SINGLE_OWNER\n'
        ),
    },
    {
        "marker": "OH_SK_DEVICE_Glyph",
        "anchor": (
            'void Device::onDrawGlyphRunList(SkCanvas* canvas,\n'
            '                                const sktext::GlyphRunList& glyphRunList,\n'
            '                                const SkPaint& paint) {\n'
            '    ASSERT_SINGLE_OWNER\n'
        ),
        "replace": (
            'void Device::onDrawGlyphRunList(SkCanvas* canvas,\n'
            '                                const sktext::GlyphRunList& glyphRunList,\n'
            '                                const SkPaint& paint) {\n'
            '    // OH_SK_DEVICE probe — entry\n'
            '    ' + probe_block(
                "OH_SK_DEVICE_Glyph",
                "Device::onDrawGlyphRunList #%d device=%p blob=%p",
                "n, (void*)this, (void*)glyphRunList.blob()",
            ) + '\n'
            '    ASSERT_SINGLE_OWNER\n'
        ),
    },
]


# ---------------------------------------------------------------------------
# Patch 3: SurfaceDrawContext.cpp — add includes + ENTRY probe in addDrawOp
# ---------------------------------------------------------------------------
SDC_PATCHES = [
    {
        "marker": "OH_SK_ADDOP_INC",
        "anchor": '#include "src/gpu/ganesh/SurfaceDrawContext.h"',
        "replace": (
            '#include "src/gpu/ganesh/SurfaceDrawContext.h"\n'
            '\n'
            '// OH_SK_ADDOP — added 2026-05-13 for silent-NOOP diagnosis\n'
            '#include <atomic>\n'
            '#include <cstdio>'
        ),
    },
    {
        "marker": "OH_SK_ADDOP_Entry",
        "anchor": (
            'void SurfaceDrawContext::addDrawOp(const GrClip* clip,\n'
            '                                   GrOp::Owner op,\n'
            '                                   const std::function<WillAddOpFn>& willAddFn) {\n'
            '    ASSERT_SINGLE_OWNER\n'
            '    if (fContext->abandoned()) {\n'
            '        return;\n'
            '    }\n'
        ),
        "replace": (
            'void SurfaceDrawContext::addDrawOp(const GrClip* clip,\n'
            '                                   GrOp::Owner op,\n'
            '                                   const std::function<WillAddOpFn>& willAddFn) {\n'
            '    // OH_SK_ADDOP probe — entry (op reached the bottom-level enqueue)\n'
            '    ' + probe_block(
                "OH_SK_ADDOP_Entry",
                "addDrawOp ENTRY #%d sdc=%p op=%p clip=%p",
                "n, (void*)this, (void*)op.get(), (void*)clip",
            ) + '\n'
            '    ASSERT_SINGLE_OWNER\n'
            '    if (fContext->abandoned()) {\n'
            '        // OH_SK_ADDOP probe — abandoned (silent skip)\n'
            '        ' + probe_block(
                "OH_SK_ADDOP_Abandoned",
                "addDrawOp ABANDONED #%d sdc=%p",
                "n, (void*)this",
            ) + '\n'
            '        return;\n'
            '    }\n'
        ),
    },
]


def patch_one(content, anchor, replace, marker):
    if marker in content:
        print(f"  [skip] {marker} already present")
        return content, False
    if anchor not in content:
        print(f"  [FAIL] anchor not found for {marker}")
        return content, False
    if content.count(anchor) != 1:
        print(f"  [FAIL] anchor not unique ({content.count(anchor)}x) for {marker}")
        return content, False
    content = content.replace(anchor, replace, 1)
    print(f"  [OK] {marker}")
    return content, True


def patch_file(target_path, patches):
    if not os.path.exists(target_path):
        print(f"ERROR: target not found: {target_path}", file=sys.stderr)
        return False
    backup = target_path + ".bak.OH_SK_REPLAY_V2"
    if not os.path.exists(backup):
        shutil.copy2(target_path, backup)
        print(f"  [backup] {backup}")
    print(f"Patching {target_path}")
    with open(target_path, "r") as f:
        content = f.read()
    any_change = False
    for p in patches:
        content, ch = patch_one(content, p["anchor"], p["replace"], p["marker"])
        any_change = any_change or ch
    if any_change:
        with open(target_path, "w") as f:
            f.write(content)
    return any_change


def main():
    print("=== apply_oh_sk_replay_probe_v2.py ===")
    a = patch_file(TARGETS[0], SKCANVAS_PATCHES)
    b = patch_file(TARGETS[1], DEVICE_PATCHES)
    c = patch_file(TARGETS[2], SDC_PATCHES)
    if a or b or c:
        print("V2 probes inserted. Re-compile libskia_canvaskit to take effect.")
    else:
        print("No changes (already patched).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
