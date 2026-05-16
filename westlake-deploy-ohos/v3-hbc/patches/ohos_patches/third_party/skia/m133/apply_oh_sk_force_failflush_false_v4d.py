#!/usr/bin/env python3
"""apply_oh_sk_force_failflush_false_v4d.py — Option B fix.

GrOnFlushResourceProvider::failFlushTimeCallbacks() reads
GrContextOptions::fFailFlushTimeCallbacks at an offset that — due to
some still-unexplained ABI drift between hwui and libskia compile views —
contains garbage byte 0x28 (40) rather than the false hwui writes.

Production OH should never have the GPU_TEST_UTILS fail-callback
mechanism active (it is a test-only knob).  Forcing the getter to
`return false` neutralises the entire test-path and lets GrAtlasManager::
preFlush always proceed to atlas instantiation, restoring the normal
flush -> executeRenderTasks -> onExecute path.

This is a workaround, not a structural fix.  The underlying ABI drift
in GrContextOptions struct layout should be tracked down separately,
but this unblocks visual progress.

Idempotent.  Marker `OH_SK_FORCE_FFTC_FALSE_V4D`.
"""
import os
import shutil
import sys

OH_ROOT = os.environ.get("OH_ROOT", "/home/HanBingChen/oh")
PROVIDER_CPP = os.path.join(
    OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/GrOnFlushResourceProvider.cpp"
)

MARKER = "OH_SK_FORCE_FFTC_FALSE_V4D"
ANCHOR = (
    "#if defined(GPU_TEST_UTILS)\n"
    "bool GrOnFlushResourceProvider::failFlushTimeCallbacks() const {\n"
    "    return fDrawingMgr->getContext()->priv().options().fFailFlushTimeCallbacks;\n"
    "}\n"
    "#endif"
)
REPLACE = (
    "#if defined(GPU_TEST_UTILS)\n"
    "// OH_SK_FORCE_FFTC_FALSE_V4D — workaround: force return false.\n"
    "// GrContextOptions ABI drifts between hwui and libskia compile views\n"
    "// such that the byte read here is garbage (0x28) rather than the false\n"
    "// hwui's default-init / explicit set should produce.  Test-only knob,\n"
    "// production OH should never have it active anyway.  Unblocks the\n"
    "// flush -> executeRenderTasks -> onExecute path.\n"
    "bool GrOnFlushResourceProvider::failFlushTimeCallbacks() const {\n"
    "    return false;\n"
    "}\n"
    "#endif"
)


def main():
    if not os.path.exists(PROVIDER_CPP):
        print(f"ERROR: not found: {PROVIDER_CPP}", file=sys.stderr)
        return 1
    with open(PROVIDER_CPP, "r") as f:
        content = f.read()
    if MARKER in content:
        print(f"  [skip] {MARKER} already present")
        return 0
    if ANCHOR not in content:
        print(f"  [FAIL] anchor not found in {PROVIDER_CPP}", file=sys.stderr)
        return 2
    if content.count(ANCHOR) != 1:
        print(f"  [FAIL] anchor not unique ({content.count(ANCHOR)}x)", file=sys.stderr)
        return 3
    backup = PROVIDER_CPP + ".bak.OH_SK_FORCE_FFTC_FALSE_V4D"
    if not os.path.exists(backup):
        shutil.copy2(PROVIDER_CPP, backup)
        print(f"  [backup] {backup}")
    content = content.replace(ANCHOR, REPLACE, 1)
    with open(PROVIDER_CPP, "w") as f:
        f.write(content)
    print(f"  [OK] {MARKER} applied")
    print("Rebuild:")
    print("  cd /home/HanBingChen/oh/out/rk3568 && ninja -w dupbuild=warn skia_canvaskit")
    return 0


if __name__ == "__main__":
    sys.exit(main())
