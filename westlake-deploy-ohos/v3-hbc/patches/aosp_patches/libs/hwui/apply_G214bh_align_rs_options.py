#!/usr/bin/env python3
"""apply_G214bh_align_rs_options.py — align hwui GrContextOptions construction
with OH RS pattern (rosen skia_gpu_context.cpp:101..115).

Two changes to RenderThread::initGrContextOptions:
  - REMOVE the G214bg explicit set `options.fFailFlushTimeCallbacks = false;`
    (G214bg did not fix preFlushSuccessful=0; the field reads garbage 0x28 in
    libskia regardless, suggesting hwui write offset != libskia read offset.
    RS doesn't set this field either, so revert to match RS pattern.)
  - ADD explicit set `options.clearSmallTexture = true;` under SKIA_OHOS
    (RS sets it explicitly via `options.GetIsUniRender()`; hwui depending on
    default-init was the only RS-vs-hwui asymmetry left in this struct.)

Idempotent.  Creates .bak.G214bh_ALIGN_RS on first run.
"""
import os
import shutil
import sys

AOSP_ROOT = os.environ.get("AOSP_ROOT", "/home/HanBingChen/aosp")
RENDER_THREAD_CPP = os.path.join(
    AOSP_ROOT, "frameworks/base/libs/hwui/renderthread/RenderThread.cpp"
)

MARKER = "G214bh_align_rs_options"
# Match the G214bg block exactly (including its trailing assignment).
ANCHOR = (
    "    // G214bg_options_explicit_set — defensive: even with GPU_TEST_UTILS=1\n"
    "    // and default member initializer, libskia reads fFailFlushTimeCallbacks=true.\n"
    "    // Explicit assignment ensures hwui's view at offset 204 is zero.\n"
    "    options.fFailFlushTimeCallbacks = false;\n"
    "}"
)
REPLACE = (
    "    // G214bh_align_rs_options — RS (rosen skia_gpu_context.cpp:101+) sets\n"
    "    // clearSmallTexture explicitly; hwui was relying on default-init.\n"
    "    // G214bg's fFailFlushTimeCallbacks=false reverted because the byte\n"
    "    // libskia reads (offset 204) holds 0x28 garbage regardless, so the\n"
    "    // assignment was writing to a hwui-side offset that libskia never\n"
    "    // reads from.  The real fix is libskia-side failFlushTimeCallbacks()\n"
    "    // -> return false (v4d).  Match RS layout to be safe.\n"
    "#ifdef SKIA_OHOS\n"
    "    options.clearSmallTexture = true;\n"
    "#endif\n"
    "}"
)


def main():
    if not os.path.exists(RENDER_THREAD_CPP):
        print(f"ERROR: not found: {RENDER_THREAD_CPP}", file=sys.stderr)
        return 1
    with open(RENDER_THREAD_CPP, "r") as f:
        content = f.read()
    if MARKER in content:
        print(f"  [skip] {MARKER} already present")
        return 0
    if ANCHOR not in content:
        print(f"  [FAIL] anchor not found in {RENDER_THREAD_CPP}", file=sys.stderr)
        return 2
    if content.count(ANCHOR) != 1:
        print(f"  [FAIL] anchor not unique ({content.count(ANCHOR)}x)", file=sys.stderr)
        return 3
    backup = RENDER_THREAD_CPP + ".bak.G214bh_ALIGN_RS"
    if not os.path.exists(backup):
        shutil.copy2(RENDER_THREAD_CPP, backup)
        print(f"  [backup] {backup}")
    content = content.replace(ANCHOR, REPLACE, 1)
    with open(RENDER_THREAD_CPP, "w") as f:
        f.write(content)
    print(f"  [OK] {MARKER} applied")
    print("Rebuild: bash ~/adapter/build/compile_libhwui.sh")
    return 0


if __name__ == "__main__":
    sys.exit(main())
