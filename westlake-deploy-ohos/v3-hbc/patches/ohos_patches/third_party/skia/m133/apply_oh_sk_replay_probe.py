#!/usr/bin/env python3
"""apply_oh_sk_replay_probe.py

OH_SK_REPLAY — In-place patch ~/oh/third_party/skia/m133/src/core/SkCanvas.cpp
to add probes at the entry of 5 SkCanvas::onDraw* virtuals.

Purpose: directly verify whether hwui-recorded ops actually reach OH Skia's
draw dispatch.  If hwui's RenderNodeDrawable.onDraw replay calls
canvas->drawXxx(...), the call funnels through SkCanvas::onDrawXxx — these
probes log it.

Background: P1 + record-layouts diagnostics (2026-05-13) ruled out probe
overpaint, ABI/RTTI/vtable mismatch, and shim-induced silent NOOP.  Remaining
hypothesis: ops never reach OH Skia, OR reach but die in onDraw->device->GPU
chain.  This probe distinguishes the two:

  - Many [OH_SK_REPLAY] entries → ops do reach OH Skia, dig deeper into
    SkCanvas::onDrawXxx -> SkDevice -> GPU pipeline.
  - Zero [OH_SK_REPLAY] entries (other than probe A's clear()) → hwui replay
    short-circuits before reaching OH Skia at all.

Cross-process noise: fprintf(stderr) in OH Skia (a shared .so loaded into
many processes) writes to per-process stderr.  helloworld is an appspawn-x
child whose stderr is redirected by AppSpawnX::ChildMain to
/data/service/el1/public/appspawnx/adapter_child_<pid>.stderr.  RS process's
stderr goes elsewhere (RS daemon /dev/null or hilog), so RS draw activity
will NOT pollute the helloworld stderr file.  Filter by reading only
adapter_child_<helloworld_pid>.stderr.

Idempotent: skips on marker present; creates .bak.OH_SK_REPLAY first run.
Editable build: keeps source patched after run so subsequent ninja recompiles
reuse it (per CLAUDE.md no_revert_patch rule).
"""
import os
import sys

OH_ROOT = os.environ.get("OH_ROOT", "/home/HanBingChen/oh")
TARGET = os.path.join(OH_ROOT, "third_party/skia/m133/src/core/SkCanvas.cpp")

MARKER = "OH_SK_REPLAY"


# ---------------------------------------------------------------------------
# Header-include block: add <atomic> + <cstdio> right after the file's first
# include of SkCanvas.h.  Marker on the comment line lets the apply script
# detect a prior run.
# ---------------------------------------------------------------------------
INCLUDE_ANCHOR = '#include "include/core/SkCanvas.h"\n'

INCLUDE_INSERT = '''#include "include/core/SkCanvas.h"

// OH_SK_REPLAY probe — added 2026-05-13 for helloworld silent-NOOP diagnosis.
// fprintf(stderr) per-process; appspawn-x child stderr redirects to
// /data/service/el1/public/appspawnx/adapter_child_<pid>.stderr, so RS draw
// activity is naturally filtered out.  Each probe limited to first 5 + every
// 100th call to bound log volume.
#include <atomic>
#include <cstdio>
'''


# ---------------------------------------------------------------------------
# 5 onDraw probes.  Anchor = the function-signature line through the opening
# brace; insert = anchor + a probe block before the original body.  Each
# probe uses a distinct atomic counter so the per-op count is independent.
# ---------------------------------------------------------------------------
PROBES = [
    # (anchor, op_name, fmt, args_expr)
    (
        'void SkCanvas::onDrawPaint(const SkPaint& paint) {',
        'Paint',
        '#%d canvas=%p (canvas-wide)',
        'n, (void*)this',
    ),
    (
        'void SkCanvas::onDrawRect(const SkRect& r, const SkPaint& paint) {',
        'Rect',
        '#%d canvas=%p l=%.1f t=%.1f r=%.1f b=%.1f',
        'n, (void*)this, (double)r.fLeft, (double)r.fTop, (double)r.fRight, (double)r.fBottom',
    ),
    (
        'void SkCanvas::onDrawPath(const SkPath& path, const SkPaint& paint) {',
        'Path',
        '#%d canvas=%p path-verbs=%d',
        'n, (void*)this, path.countVerbs()',
    ),
    (
        'void SkCanvas::onDrawTextBlob(const SkTextBlob* blob, SkScalar x, SkScalar y,',
        'TextBlob',
        '#%d canvas=%p blob=%p x=%.1f y=%.1f',
        'n, (void*)this, (void*)blob, (double)x, (double)y',
    ),
    (
        'void SkCanvas::onDrawImageRect2(const SkImage* image, const SkRect& src, const SkRect& dst,',
        'ImageRect',
        '#%d canvas=%p image=%p dst l=%.1f t=%.1f r=%.1f b=%.1f',
        'n, (void*)this, (void*)image, (double)dst.fLeft, (double)dst.fTop, (double)dst.fRight, (double)dst.fBottom',
    ),
]


def make_probe_insert(anchor, op_name, fmt, args_expr):
    """Build the replacement text: original anchor line + probe block.

    Some anchors (TextBlob / ImageRect2) are split across multiple physical
    lines because the function signature wraps; we only anchor on the first
    physical line, and the patch needs to insert the probe AFTER the signature's
    opening brace.  For those, find the brace position in the file separately.
    """
    return None  # built per-anchor below in patch_one


def patch_one(content, anchor, op_name, fmt, args_expr):
    """Insert probe block right after the opening brace of the function whose
    signature starts at `anchor`.  Returns (new_content, did_change)."""
    if f"OH_SK_REPLAY_{op_name}" in content:
        print(f"  [skip] OH_SK_REPLAY_{op_name} already present")
        return content, False

    # Locate the anchor line.  For multi-line signatures the anchor is the
    # first physical line; we then walk forward to find the matching '{' that
    # opens the function body.
    idx = content.find(anchor)
    if idx < 0:
        print(f"  [FAIL] anchor not found for op {op_name}")
        return content, False
    if content.find(anchor, idx + len(anchor)) >= 0:
        print(f"  [FAIL] anchor not unique for op {op_name}")
        return content, False

    # Find the next '{' after the anchor; that's the function body open.
    brace_pos = content.find('{', idx)
    if brace_pos < 0:
        print(f"  [FAIL] could not find opening brace after anchor {op_name}")
        return content, False
    # Find the newline after that brace; insert probe on the line after.
    nl_pos = content.find('\n', brace_pos)
    if nl_pos < 0:
        print(f"  [FAIL] could not find newline after brace {op_name}")
        return content, False
    insert_at = nl_pos + 1

    probe_block = (
        f'    // OH_SK_REPLAY probe — onDraw{op_name}\n'
        f'    {{\n'
        f'        static std::atomic<int> g_OH_SK_REPLAY_{op_name}{{0}};\n'
        f'        int n = ++g_OH_SK_REPLAY_{op_name};\n'
        f'        if (n <= 5 || n % 100 == 0) {{\n'
        f'            std::fprintf(stderr,\n'
        f'                "[OH_SK_REPLAY] onDraw{op_name} {fmt}\\n",\n'
        f'                {args_expr});\n'
        f'        }}\n'
        f'    }}\n'
    )

    new_content = content[:insert_at] + probe_block + content[insert_at:]
    print(f"  [OK] inserted probe onDraw{op_name}")
    return new_content, True


def patch_include(content):
    """Add <atomic>+<cstdio> includes."""
    if "OH_SK_REPLAY probe — added" in content:
        print("  [skip] OH_SK_REPLAY include block already present")
        return content, False
    if INCLUDE_ANCHOR not in content:
        print(f"  [FAIL] include anchor not found: '{INCLUDE_ANCHOR.strip()}'")
        return content, False
    if content.count(INCLUDE_ANCHOR) > 1:
        print("  [FAIL] include anchor not unique")
        return content, False
    new_content = content.replace(INCLUDE_ANCHOR, INCLUDE_INSERT, 1)
    print("  [OK] inserted OH_SK_REPLAY include block")
    return new_content, True


def main():
    if not os.path.exists(TARGET):
        print(f"ERROR: target not found: {TARGET}", file=sys.stderr)
        return 2

    backup = TARGET + ".bak.OH_SK_REPLAY"
    if not os.path.exists(backup):
        import shutil
        shutil.copy2(TARGET, backup)
        print(f"  [backup] {backup}")

    print(f"Patching {TARGET}")

    with open(TARGET, "r") as f:
        content = f.read()

    any_change = False
    content, ch = patch_include(content)
    any_change = any_change or ch
    for anchor, op_name, fmt, args_expr in PROBES:
        content, ch = patch_one(content, anchor, op_name, fmt, args_expr)
        any_change = any_change or ch

    if any_change:
        with open(TARGET, "w") as f:
            f.write(content)
        print("Probes inserted. Re-compile libskia_canvaskit to take effect.")
    else:
        print("No changes (already patched).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
