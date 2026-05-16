#!/usr/bin/env python3
"""apply_oh_sk_preflush_probe_v4c.py — identify WHICH onFlushCBObject is in
fOnFlushCBObjects[0] and WHY its preFlush returns false.

After v4b confirmed fOnFlushCBObjects.size=1 + preFlushSuccessful=0, the
ABI alignment hypothesis (compile_libhwui.sh adding -DGPU_TEST_UTILS=1) and
the explicit-set hypothesis (RenderThread.cpp setting fFailFlushTimeCallbacks
=false) both failed to flip preFlushSuccessful to 1.  That rules out
GrAtlasManager via the GPU_TEST_UTILS path and points at either:

  - GrAtlasManager via some other mutation of the field
  - SmallPathAtlasMgr (same GPU_TEST_UTILS gate)
  - AtlasPathRenderer (also returns false on `fAtlasRenderTasks[i]->instantiate
    (onFlushRP)` failure — a *non* test-utils path)

This script adds entry-level fprintf(stderr) probes to all 3 preFlush impls
so we see exactly which one fires and what state it sees.

Targets:
  - src/gpu/ganesh/text/GrAtlasManager.h        marker: OH_SK_PREFLUSH_AtlasMgr
  - src/gpu/ganesh/ops/SmallPathAtlasMgr.h      marker: OH_SK_PREFLUSH_SmallPath
  - src/gpu/ganesh/ops/AtlasPathRenderer.cpp    marker: OH_SK_PREFLUSH_AtlasPath

Idempotent.  Marker token check + .bak.OH_SK_PREFLUSH_V4C on first run.
"""
import os
import sys
import shutil

OH_ROOT = os.environ.get("OH_ROOT", "/home/HanBingChen/oh")
ATLAS_MGR_H        = os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/text/GrAtlasManager.h")
SMALL_PATH_MGR_H   = os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/ops/SmallPathAtlasMgr.h")
ATLAS_PATH_REND    = os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/ops/AtlasPathRenderer.cpp")


ATLAS_MGR_PATCHES = [
    {
        "marker": "OH_SK_PREFLUSH_AtlasMgr_INC",
        "anchor": '#include "include/core/SkRefCnt.h"',
        "replace": (
            '#include "include/core/SkRefCnt.h"\n'
            '\n'
            '// OH_SK_PREFLUSH_AtlasMgr_INC — v4c probes added 2026-05-13\n'
            '#include <atomic>\n'
            '#include <cstdio>'
        ),
    },
    {
        "marker": "OH_SK_PROBE_BODY_AtlasMgr_v4c",
        "anchor": (
            "    bool preFlush(GrOnFlushResourceProvider* onFlushRP) override {\n"
            "#if defined(GPU_TEST_UTILS)\n"
            "        if (onFlushRP->failFlushTimeCallbacks()) {\n"
            "            return false;\n"
            "        }\n"
            "#endif"
        ),
        "replace": (
            "    bool preFlush(GrOnFlushResourceProvider* onFlushRP) override {\n"
            "        // OH_SK_PROBE_BODY_AtlasMgr_v4c\n"
            "        {\n"
            "            static std::atomic<int> g_OH_SK_PROBE_AtlasMgr{0};\n"
            "            int n = ++g_OH_SK_PROBE_AtlasMgr;\n"
            "            if (n <= 10 || n % 100 == 0) {\n"
            "#if defined(GPU_TEST_UTILS)\n"
            "                bool ftc = onFlushRP->failFlushTimeCallbacks();\n"
            "#else\n"
            "                bool ftc = false;\n"
            "#endif\n"
            "                std::fprintf(stderr,\n"
            "                    \"[OH_SK_PREFLUSH_AtlasMgr] #%d this=%p ftc=%d\\n\",\n"
            "                    n, (void*)this, ftc?1:0);\n"
            "            }\n"
            "        }\n"
            "#if defined(GPU_TEST_UTILS)\n"
            "        if (onFlushRP->failFlushTimeCallbacks()) {\n"
            "            return false;\n"
            "        }\n"
            "#endif"
        ),
    },
]


SMALL_PATH_PATCHES = [
    {
        "marker": "OH_SK_PREFLUSH_SmallPath_INC",
        "anchor": '#include "include/core/SkTypes.h"',
        "replace": (
            '#include "include/core/SkTypes.h"\n'
            '\n'
            '// OH_SK_PREFLUSH_SmallPath_INC — v4c probes added 2026-05-13\n'
            '#include <atomic>\n'
            '#include <cstdio>'
        ),
    },
    {
        "marker": "OH_SK_PROBE_BODY_SmallPath_v4c",
        "anchor": (
            "    bool preFlush(GrOnFlushResourceProvider* onFlushRP) override {\n"
            "#if defined(GPU_TEST_UTILS)\n"
            "        if (onFlushRP->failFlushTimeCallbacks()) {\n"
            "            return false;\n"
            "        }\n"
            "#endif"
        ),
        "replace": (
            "    bool preFlush(GrOnFlushResourceProvider* onFlushRP) override {\n"
            "        // OH_SK_PROBE_BODY_SmallPath_v4c\n"
            "        {\n"
            "            static std::atomic<int> g_OH_SK_PROBE_SmallPath{0};\n"
            "            int n = ++g_OH_SK_PROBE_SmallPath;\n"
            "            if (n <= 10 || n % 100 == 0) {\n"
            "#if defined(GPU_TEST_UTILS)\n"
            "                bool ftc = onFlushRP->failFlushTimeCallbacks();\n"
            "#else\n"
            "                bool ftc = false;\n"
            "#endif\n"
            "                std::fprintf(stderr,\n"
            "                    \"[OH_SK_PREFLUSH_SmallPath] #%d this=%p ftc=%d\\n\",\n"
            "                    n, (void*)this, ftc?1:0);\n"
            "            }\n"
            "        }\n"
            "#if defined(GPU_TEST_UTILS)\n"
            "        if (onFlushRP->failFlushTimeCallbacks()) {\n"
            "            return false;\n"
            "        }\n"
            "#endif"
        ),
    },
]


ATLAS_PATH_PATCHES = [
    {
        "marker": "OH_SK_PREFLUSH_AtlasPath_INC",
        "anchor": '#include "src/gpu/ganesh/ops/AtlasPathRenderer.h"',
        "replace": (
            '#include "src/gpu/ganesh/ops/AtlasPathRenderer.h"\n'
            '\n'
            '// OH_SK_PREFLUSH_AtlasPath_INC — v4c probes added 2026-05-13\n'
            '#include <atomic>\n'
            '#include <cstdio>'
        ),
    },
    {
        "marker": "OH_SK_PROBE_BODY_AtlasPath_v4c",
        "anchor": (
            "bool AtlasPathRenderer::preFlush(GrOnFlushResourceProvider* onFlushRP) {\n"
            "    if (fAtlasRenderTasks.empty()) {"
        ),
        "replace": (
            "bool AtlasPathRenderer::preFlush(GrOnFlushResourceProvider* onFlushRP) {\n"
            "    // OH_SK_PROBE_BODY_AtlasPath_v4c — entry + state dump\n"
            "    {\n"
            "        static std::atomic<int> g_OH_SK_PROBE_AtlasPath{0};\n"
            "        int n = ++g_OH_SK_PROBE_AtlasPath;\n"
            "        if (n <= 10 || n % 100 == 0) {\n"
            "#if defined(GPU_TEST_UTILS)\n"
            "            bool ftc = onFlushRP->failFlushTimeCallbacks();\n"
            "#else\n"
            "            bool ftc = false;\n"
            "#endif\n"
            "            std::fprintf(stderr,\n"
            "                \"[OH_SK_PREFLUSH_AtlasPath] #%d this=%p ftc=%d tasks=%d\\n\",\n"
            "                n, (void*)this, ftc?1:0, (int)fAtlasRenderTasks.size());\n"
            "        }\n"
            "    }\n"
            "    if (fAtlasRenderTasks.empty()) {"
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
    backup = target_path + ".bak.OH_SK_PREFLUSH_V4C"
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
    print("=== apply_oh_sk_preflush_probe_v4c.py ===")
    a = patch_file(ATLAS_MGR_H, ATLAS_MGR_PATCHES)
    b = patch_file(SMALL_PATH_MGR_H, SMALL_PATH_PATCHES)
    c = patch_file(ATLAS_PATH_REND, ATLAS_PATH_PATCHES)
    if a or b or c:
        print("V4c per-callback probes inserted.")
        print("Note: GrAtlasManager.h and SmallPathAtlasMgr.h are headers — many .cpp")
        print("will need recompilation.  Use ninja to rebuild skia_canvaskit:")
        print("  cd /home/HanBingChen/oh/out/rk3568 && ninja -w dupbuild=warn skia_canvaskit")
    else:
        print("No changes (already patched).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
