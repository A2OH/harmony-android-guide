#!/usr/bin/env python3
"""apply_G214bg_options_explicit_set.py

G2.14bg — Explicit set of options.fFailFlushTimeCallbacks=false in
RenderThread::initGrContextOptions.

Background: GrContextOptions::fFailFlushTimeCallbacks (in GrContextOptions.h
inside `#if defined(GPU_TEST_UTILS)`) has a default member initializer
`= false`, and `GrContextOptions() {}` has an empty body that should still
invoke the default member initializer per C++11+.  However v4b probe
indicates fFailFlushTimeCallbacks reads `true` in libskia's
`failFlushTimeCallbacks()` even after the hwui ABI alignment fix
(GPU_TEST_UTILS=1 added to compile_libhwui.sh).

This patch explicitly assigns `false` as a 1-line defensive override.  Either:
  - It fixes the issue -> default-init path was being elided; root cause was
    a subtle ctor/copy issue.
  - It doesn't fix -> ABI alignment hypothesis is wrong; need to chase where
    `true` is being written or which onFlush callback returns false.

Idempotent.  Creates .bak.G214bg_OPTIONS on first run.
"""
import os
import shutil
import sys

AOSP_ROOT = os.environ.get("AOSP_ROOT", "/home/HanBingChen/aosp")
RENDER_THREAD_CPP = os.path.join(
    AOSP_ROOT, "frameworks/base/libs/hwui/renderthread/RenderThread.cpp"
)

MARKER = "G214bg_options_explicit_set"
ANCHOR = (
    "void RenderThread::initGrContextOptions(GrContextOptions& options) {\n"
    "    options.fPreferExternalImagesOverES3 = true;\n"
    "    options.fDisableDistanceFieldPaths = true;\n"
    "    if (android::base::GetBoolProperty(PROPERTY_REDUCE_OPS_TASK_SPLITTING, true)) {\n"
    "        options.fReduceOpsTaskSplitting = GrContextOptions::Enable::kYes;\n"
    "    } else {\n"
    "        options.fReduceOpsTaskSplitting = GrContextOptions::Enable::kNo;\n"
    "    }\n"
    "}"
)
REPLACE = (
    "void RenderThread::initGrContextOptions(GrContextOptions& options) {\n"
    "    options.fPreferExternalImagesOverES3 = true;\n"
    "    options.fDisableDistanceFieldPaths = true;\n"
    "    if (android::base::GetBoolProperty(PROPERTY_REDUCE_OPS_TASK_SPLITTING, true)) {\n"
    "        options.fReduceOpsTaskSplitting = GrContextOptions::Enable::kYes;\n"
    "    } else {\n"
    "        options.fReduceOpsTaskSplitting = GrContextOptions::Enable::kNo;\n"
    "    }\n"
    "    // G214bg_options_explicit_set — defensive: even with GPU_TEST_UTILS=1\n"
    "    // and default member initializer, libskia reads fFailFlushTimeCallbacks=true.\n"
    "    // Explicit assignment ensures hwui's view at offset 204 is zero.\n"
    "    options.fFailFlushTimeCallbacks = false;\n"
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
    backup = RENDER_THREAD_CPP + ".bak.G214bg_OPTIONS"
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
