#!/usr/bin/env python3
"""apply_oh_sk_gate_probe_v4.py — pinpoint which Skia gate drops OpsTasks.

v3 result (helloworld pid 2227, sampled 1/100):
  addDrawOp        ~6800   (recording side healthy)
  DCTX_FLUSH       ~700    (flush API called)
  DCTX_FAS         ~700    (flushAndSubmit API called)
  OpsTask DESTROY  ~700    (numOpChains=0 isColorNoOp=0)  ← already empty
  OpsTask EXEC     0       (onExecute NEVER fires)
  GL_BEGIN/DRAW    0       (no GL render pass starts)

OpsTask is drained via GrDrawingManager::removeRenderTasks() → endFlush() →
deleteOps().  Why does no task make it to onExecute?  Two candidate gates in
GrDrawingManager.cpp:

  GATE A  (line 209 in flush()):
    cachePurgeNeeded = !resourceAllocator.failedInstantiation() &&
                       this->executeRenderTasks(&flushState);
    -> if failedInstantiation()==true, executeRenderTasks not called at all.

  GATE B  (line 264 inside executeRenderTasks loop):
    for (const auto& renderTask : fDAG) {
        SkASSERT(renderTask);
        if (!renderTask->isInstantiated()) {
            continue;     ← skipped here, never gets to task->execute()
        }

The fix surface for A vs B is completely different (allocator/assign vs
per-task target proxy registration), so we need to know which fires before
choosing a fix.

This script adds 3 fprintf(stderr) probes (sampled 10 + every 100) to
GrDrawingManager.cpp:

  [OH_SK_GATE_FLUSH_PRE_EXEC]
      Right before the cachePurgeNeeded assignment in flush().
      Prints failedInstantiation / fDAG.size / usingReorderedDAG.

  [OH_SK_GATE_EXEC_DAG]
      At executeRenderTasks() entry.
      Counts isInstantiated=true / =false tasks in a single pass over fDAG.

  [OH_SK_GATE_EXEC_TASK]
      Inside the execute loop, right after SkASSERT(renderTask).
      Prints task pointer + isInstantiated + isOpsTask.

Idempotent.  Marker tokens embedded in inserted code.
First run creates .bak.OH_SK_GATE_V4.
"""
import os
import sys
import shutil

OH_ROOT = os.environ.get("OH_ROOT", "/home/HanBingChen/oh")
DRAWING_MGR = os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/GrDrawingManager.cpp")


PATCHES = [
    # ----- header includes -----
    {
        "marker": "OH_SK_GATE_INC",
        "anchor": '#include "src/gpu/ganesh/GrDrawingManager.h"',
        "replace": (
            '#include "src/gpu/ganesh/GrDrawingManager.h"\n'
            '\n'
            '// OH_SK_GATE_INC — v4 probes added 2026-05-13 to pinpoint OpsTask drop gate\n'
            '#include <atomic>\n'
            '#include <cstdio>'
        ),
    },
    # ----- probe A: pre-executeRenderTasks gate (in flush) -----
    {
        "marker": "OH_SK_GATE_FLUSH_PRE_EXEC",
        "anchor": (
            '        cachePurgeNeeded = !resourceAllocator.failedInstantiation() &&\n'
            '                           this->executeRenderTasks(&flushState);'
        ),
        "replace": (
            '        // OH_SK_GATE_FLUSH_PRE_EXEC v4 probe — capture gate A inputs\n'
            '        {\n'
            '            static std::atomic<int> g_OH_SK_GATE_FLUSH_PRE_EXEC{0};\n'
            '            int n = ++g_OH_SK_GATE_FLUSH_PRE_EXEC;\n'
            '            if (n <= 10 || n % 100 == 0) {\n'
            '                std::fprintf(stderr,\n'
            '                    "[OH_SK_GATE_FLUSH_PRE_EXEC] #%d failedInstantiation=%d fDAG.size=%d usingReorderedDAG=%d\\n",\n'
            '                    n, resourceAllocator.failedInstantiation()?1:0, fDAG.size(), usingReorderedDAG?1:0);\n'
            '            }\n'
            '        }\n'
            '        cachePurgeNeeded = !resourceAllocator.failedInstantiation() &&\n'
            '                           this->executeRenderTasks(&flushState);'
        ),
    },
    # ----- probe B: executeRenderTasks entry, bucket counts over fDAG -----
    {
        "marker": "OH_SK_GATE_EXEC_DAG",
        "anchor": 'bool anyRenderTasksExecuted = false;',
        "replace": (
            'bool anyRenderTasksExecuted = false;\n'
            '\n'
            '    // OH_SK_GATE_EXEC_DAG v4 probe — bucket isInstantiated over fDAG at entry\n'
            '    {\n'
            '        static std::atomic<int> g_OH_SK_GATE_EXEC_DAG{0};\n'
            '        int n = ++g_OH_SK_GATE_EXEC_DAG;\n'
            '        if (n <= 10 || n % 100 == 0) {\n'
            '            int inst_t = 0, inst_f = 0, null_t = 0;\n'
            '            for (const auto& rt : fDAG) {\n'
            '                if (!rt) { ++null_t; continue; }\n'
            '                if (rt->isInstantiated()) ++inst_t; else ++inst_f;\n'
            '            }\n'
            '            std::fprintf(stderr,\n'
            '                "[OH_SK_GATE_EXEC_DAG] #%d fDAG.size=%d inst_true=%d inst_false=%d null=%d\\n",\n'
            '                n, fDAG.size(), inst_t, inst_f, null_t);\n'
            '        }\n'
            '    }'
        ),
    },
    # ----- probe C: per-task in execute loop -----
    {
        "marker": "OH_SK_GATE_EXEC_TASK",
        "anchor": (
            '    for (const auto& renderTask : fDAG) {\n'
            '        SkASSERT(renderTask);\n'
            '        if (!renderTask->isInstantiated()) {\n'
            '            continue;\n'
            '        }'
        ),
        "replace": (
            '    for (const auto& renderTask : fDAG) {\n'
            '        SkASSERT(renderTask);\n'
            '        // OH_SK_GATE_EXEC_TASK v4 probe — per-task gate B observation\n'
            '        {\n'
            '            static std::atomic<int> g_OH_SK_GATE_EXEC_TASK{0};\n'
            '            int n = ++g_OH_SK_GATE_EXEC_TASK;\n'
            '            if (n <= 10 || n % 100 == 0) {\n'
            '                bool inst = renderTask->isInstantiated();\n'
            '                bool isOps = renderTask->asOpsTask() != nullptr;\n'
            '                std::fprintf(stderr,\n'
            '                    "[OH_SK_GATE_EXEC_TASK] #%d task=%p isInstantiated=%d isOpsTask=%d\\n",\n'
            '                    n, (void*)renderTask.get(), inst?1:0, isOps?1:0);\n'
            '            }\n'
            '        }\n'
            '        if (!renderTask->isInstantiated()) {\n'
            '            continue;\n'
            '        }'
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
    backup = target_path + ".bak.OH_SK_GATE_V4"
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
    print("=== apply_oh_sk_gate_probe_v4.py ===")
    changed = patch_file(DRAWING_MGR, PATCHES)
    if changed:
        print("V4 gate probes inserted into GrDrawingManager.cpp.")
        print("Re-compile libskia_canvaskit.z.so for the probes to take effect:")
        print("  cd ~/adapter/build/skia_rebuild")
        print("  rm /home/HanBingChen/oh/out/rk3568/obj/third_party/skia/m133/src/gpu/ganesh/GrDrawingManager.o 2>/dev/null")
        print("  python3 build_targeted.py GrDrawingManager.o")
        print("  bash link.sh")
    else:
        print("No changes (already patched).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
