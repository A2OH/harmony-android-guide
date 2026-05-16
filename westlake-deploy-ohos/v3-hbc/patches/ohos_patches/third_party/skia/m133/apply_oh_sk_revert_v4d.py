#!/usr/bin/env python3
"""apply_oh_sk_revert_v4d.py — revert v4d's force-return-false workaround.

v4d (apply_oh_sk_force_failflush_false_v4d.py) made
GrOnFlushResourceProvider::failFlushTimeCallbacks() always return false
to bypass the garbage-byte ABI drift symptom while keeping the rendering
pipeline alive.

This reverter restores the original implementation (read fFailFlushTimeCallbacks
from priv().options()), so that — with the now-aligned ABI (HWUI sizeof=168
matches LIBSKIA sizeof=168, both have fFailFlushTimeCallbacks at offset 152)
and the symlink-based dual-path libhwui deployment — we can observe what
value actually shows up at the byte and decide between:

  - root cause was prior deploy bypass (helloworld loaded old non-aligned
    libhwui with sizeof=144 → libskia read past struct end → garbage 0x28)
  - root cause is default-member-initializer not firing reliably in
    GrContextOptions() {} empty body, leaving stack garbage at byte 152

Idempotent.  Marker `OH_SK_REVERT_V4D`.
"""
import os
import shutil
import sys

OH_ROOT = os.environ.get("OH_ROOT", "/home/HanBingChen/oh")
PROVIDER_CPP = os.path.join(
    OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/GrOnFlushResourceProvider.cpp"
)

MARKER = "OH_SK_REVERT_V4D"
# Match exactly what v4d inserted (with the workaround comment block + return false body).
ANCHOR = (
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
REPLACE = (
    "#if defined(GPU_TEST_UTILS)\n"
    "// OH_SK_REVERT_V4D — v4d revert.  ABI now confirmed aligned\n"
    "// (HWUI sizeof=168 with -DGPU_TEST_UTILS=1, LIBSKIA sizeof=168, both\n"
    "// have fFailFlushTimeCallbacks at offset 152).  Restoring the original\n"
    "// read so we can observe whether the field reads false (true ABI fix\n"
    "// effective) or still reads garbage (default-init or other corruption).\n"
    "bool GrOnFlushResourceProvider::failFlushTimeCallbacks() const {\n"
    "    return fDrawingMgr->getContext()->priv().options().fFailFlushTimeCallbacks;\n"
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
        print(f"  [FAIL] anchor not found (v4d may not be applied) in {PROVIDER_CPP}", file=sys.stderr)
        return 2
    if content.count(ANCHOR) != 1:
        print(f"  [FAIL] anchor not unique ({content.count(ANCHOR)}x)", file=sys.stderr)
        return 3
    backup = PROVIDER_CPP + ".bak.OH_SK_REVERT_V4D"
    if not os.path.exists(backup):
        shutil.copy2(PROVIDER_CPP, backup)
        print(f"  [backup] {backup}")
    content = content.replace(ANCHOR, REPLACE, 1)
    with open(PROVIDER_CPP, "w") as f:
        f.write(content)
    print(f"  [OK] {MARKER} applied — v4d workaround reverted")
    print("Rebuild:")
    print("  cd /home/HanBingChen/oh/out/rk3568 && ninja -w dupbuild=warn skia_canvaskit")
    return 0


if __name__ == "__main__":
    sys.exit(main())
