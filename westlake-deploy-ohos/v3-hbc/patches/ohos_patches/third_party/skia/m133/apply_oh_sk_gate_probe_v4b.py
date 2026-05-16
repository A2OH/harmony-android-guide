#!/usr/bin/env python3
"""apply_oh_sk_gate_probe_v4b.py — pin down preFlushSuccessful gate.

v4 result (helloworld pid 3474, sampled 1/100):
  DCTX_FLUSH               5   (flush(SkSurface*) called ~500 times)
  DCTX_FAS                 5   (flushAndSubmit called ~500 times)
  OpsTask DESTROY          5   (~500 OpsTasks destroyed)
  addDrawOp                7   (~700 ops recorded)
  OH_SK_GATE_FLUSH_PRE_EXEC 0  ← never reaches probe A
  OH_SK_GATE_EXEC_DAG       0  ← never reaches executeRenderTasks
  OH_SK_GATE_EXEC_TASK      0  ← never iterates fDAG
  OpsTask EXEC              0
  GL_BEGIN/DRAW             0

Combined: flush() IS entered (DCTX path → flushSurface → flushSurfaces →
flush) but exits before reaching the `if (preFlushSuccessful)` body.  The
only path that lets `removeRenderTasks` still drain OpsTasks (→ DESTROY=5)
yet bypasses the if-block is `preFlushSuccessful == false`, set by:

    bool preFlushSuccessful = true;
    for (GrOnFlushCallbackObject* onFlushCBObject : fOnFlushCBObjects) {
        preFlushSuccessful &= onFlushCBObject->preFlush(&onFlushProvider);
    }

If any registered preFlush callback returns false, every flush becomes a no-op
and all draw ops are silently dropped.

This v4b inserts a single fprintf(stderr) probe right after the for-loop, to
log preFlushSuccessful + fOnFlushCBObjects.size() per flush invocation.
Sampled (10 + every 100).

Idempotent.  Marker token `OH_SK_GATE_PREFLUSH` embedded in inserted code.
"""
import os
import sys
import shutil

OH_ROOT = os.environ.get("OH_ROOT", "/home/HanBingChen/oh")
DRAWING_MGR = os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/GrDrawingManager.cpp")


PATCHES = [
    {
        "marker": "OH_SK_GATE_PREFLUSH",
        "anchor": (
            '    bool preFlushSuccessful = true;\n'
            '    for (GrOnFlushCallbackObject* onFlushCBObject : fOnFlushCBObjects) {\n'
            '        preFlushSuccessful &= onFlushCBObject->preFlush(&onFlushProvider);\n'
            '    }\n'
            '\n'
            '    bool cachePurgeNeeded = false;'
        ),
        "replace": (
            '    bool preFlushSuccessful = true;\n'
            '    for (GrOnFlushCallbackObject* onFlushCBObject : fOnFlushCBObjects) {\n'
            '        preFlushSuccessful &= onFlushCBObject->preFlush(&onFlushProvider);\n'
            '    }\n'
            '\n'
            '    // OH_SK_GATE_PREFLUSH v4b probe — log preFlushSuccessful result\n'
            '    {\n'
            '        static std::atomic<int> g_OH_SK_GATE_PREFLUSH{0};\n'
            '        int n = ++g_OH_SK_GATE_PREFLUSH;\n'
            '        if (n <= 10 || n % 100 == 0) {\n'
            '            std::fprintf(stderr,\n'
            '                "[OH_SK_GATE_PREFLUSH] #%d preFlushSuccessful=%d fOnFlushCBObjects.size=%d\\n",\n'
            '                n, preFlushSuccessful?1:0, fOnFlushCBObjects.size());\n'
            '        }\n'
            '    }\n'
            '\n'
            '    bool cachePurgeNeeded = false;'
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
    backup = target_path + ".bak.OH_SK_GATE_V4B"
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
    print("=== apply_oh_sk_gate_probe_v4b.py ===")
    changed = patch_file(DRAWING_MGR, PATCHES)
    if changed:
        print("V4b preFlush probe inserted.")
        print("Rebuild via ninja:")
        print("  rm /home/HanBingChen/oh/out/rk3568/obj/third_party/skia/m133/src/gpu/ganesh/gpu/GrDrawingManager.o")
        print("  cd /home/HanBingChen/oh/out/rk3568 && ninja -w dupbuild=warn skia_canvaskit")
    else:
        print("No changes (already patched).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
