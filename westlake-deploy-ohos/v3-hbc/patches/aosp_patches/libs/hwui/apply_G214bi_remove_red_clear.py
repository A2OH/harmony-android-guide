#!/usr/bin/env python3
"""apply_G214bi_remove_red_clear.py — remove the forced red clear from
G2.14ba probe block in SkiaOpenGLPipeline.cpp draw().

G2.14ba was diagnostic only: it forced `sc->clear(0xFFFF0000u)` to test
whether the SkCanvas->framebuffer fast-clear path reached fbo 0.  Confirmed
yes (G214bb readbacks showed 0xff0000ff from #3 onwards).

Now that the actual flush -> executeRenderTasks -> onExecute path is fixed
(G214bh + v4d), real RenderNodeDrawable draws reach the framebuffer.  The
forced red clear overpaints the real UI.  Remove the clear line and its
companion fprintf, but keep the readback (G214bb) for ongoing diagnostics.

Idempotent.  Marker `G214bi_remove_red_clear`.
"""
import os
import shutil
import sys

AOSP_ROOT = os.environ.get("AOSP_ROOT", "/home/HanBingChen/aosp")
PIPELINE_CPP = os.path.join(
    AOSP_ROOT, "frameworks/base/libs/hwui/pipeline/skia/SkiaOpenGLPipeline.cpp"
)

MARKER = "G214bi_remove_red_clear"
ANCHOR = (
    "            if (sc) {\n"
    "                sc->clear(0xFFFF0000u);\n"
    '                fprintf(stderr, "[G214ba] A #%d SkCanvas->clear(red) issued\\n", n);\n'
)
REPLACE = (
    "            // G214bi_remove_red_clear — sc->clear(0xFFFF0000) removed; real\n"
    "            // RenderNodeDrawable draws now reach fb after G214bh + v4d.\n"
    "            if (sc) {\n"
)


def main():
    if not os.path.exists(PIPELINE_CPP):
        print(f"ERROR: not found: {PIPELINE_CPP}", file=sys.stderr)
        return 1
    with open(PIPELINE_CPP, "r") as f:
        content = f.read()
    if MARKER in content:
        print(f"  [skip] {MARKER} already present")
        return 0
    if ANCHOR not in content:
        print(f"  [FAIL] anchor not found in {PIPELINE_CPP}", file=sys.stderr)
        return 2
    if content.count(ANCHOR) != 1:
        print(f"  [FAIL] anchor not unique ({content.count(ANCHOR)}x)", file=sys.stderr)
        return 3
    backup = PIPELINE_CPP + ".bak.G214bi_REMOVE_RED"
    if not os.path.exists(backup):
        shutil.copy2(PIPELINE_CPP, backup)
        print(f"  [backup] {backup}")
    content = content.replace(ANCHOR, REPLACE, 1)
    with open(PIPELINE_CPP, "w") as f:
        f.write(content)
    print(f"  [OK] {MARKER} applied")
    print("Rebuild: bash ~/adapter/build/compile_libhwui.sh")
    return 0


if __name__ == "__main__":
    sys.exit(main())
