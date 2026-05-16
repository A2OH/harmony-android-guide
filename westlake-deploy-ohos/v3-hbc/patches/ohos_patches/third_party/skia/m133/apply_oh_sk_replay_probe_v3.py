#!/usr/bin/env python3
"""apply_oh_sk_replay_probe_v3.py — Step 3 (upgraded) post-addDrawOp probes

v2 confirmed 45 onDraw ops fully traverse: SkCanvas → Device → SurfaceDrawContext::
addDrawOp (entry=73, abandoned=0).  But fbo still red, so silent skip is
**downstream of addDrawOp** — i.e. inside GrOpsTask execution, GL render pass,
or flush dispatch.

User observation 2026-05-13: first launch shows light-translucent UI (hwui never
fired), second launch shows pure red (probe-A clear hit fbo) — confirming
clear() goes through fast path while op-replay enqueues to GrOpsTask which
might never execute.  This v3 directly tests the "task drop without flush"
hypothesis.

Probes added:
  OpsTask.cpp:
    - onExecute ENTRY → counts task executions; logs isColorNoOp + bounds
    - onExecute COLORNOOP_SKIP → catches the "isColorNoOp || boundsEmpty" silent skip
    - ~OpsTask DESTROY → counts destroy events + numOpChains at destroy time
      (smoking gun for "task destroyed before execute")

  GrGLOpsRenderPass.cpp:
    - onBegin → counts GL render pass starts (proves pass is set up)
    - onDraw → counts GL DrawArrays issuance (proves vertices reach GL)
    - onClear → counts GL clear issuance (proves the clear-red fast path)

  GrDirectContext.cpp:
    - flushAndSubmit(SkSurface*, GrSyncCpu) ENTRY → counts hwui's FlushAndSubmit
    - flush(SkSurface*, access, info) ENTRY → counts the actual flush impl

Idempotent.  Creates .bak.OH_SK_REPLAY_V3 first run.
"""
import os
import sys
import shutil

OH_ROOT = os.environ.get("OH_ROOT", "/home/HanBingChen/oh")

OPS_TASK_CPP   = os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/ops/OpsTask.cpp")
GL_RENDERPASS  = os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/gl/GrGLOpsRenderPass.cpp")
DIRECTCONTEXT  = os.path.join(OH_ROOT, "third_party/skia/m133/src/gpu/ganesh/GrDirectContext.cpp")


def probe(marker, fmt, args_expr, indent='    '):
    """Throttled probe block (5 + every 100). args_expr begins with `n,`."""
    return (
        f'{indent}{{\n'
        f'{indent}    static std::atomic<int> g_{marker}{{0}};\n'
        f'{indent}    int n = ++g_{marker};\n'
        f'{indent}    if (n <= 5 || n % 100 == 0) {{\n'
        f'{indent}        std::fprintf(stderr,\n'
        f'{indent}            "[{marker}] {fmt}\\n",\n'
        f'{indent}            {args_expr});\n'
        f'{indent}    }}\n'
        f'{indent}}}'
    )


# ---------------------------------------------------------------------------
# Patch OpsTask.cpp
# ---------------------------------------------------------------------------
OPS_TASK_PATCHES = [
    # Add includes (first include in file)
    {
        "marker": "OH_SK_OPSTASK_INC",
        "anchor": '#include "src/gpu/ganesh/ops/OpsTask.h"',
        "replace": (
            '#include "src/gpu/ganesh/ops/OpsTask.h"\n'
            '\n'
            '// OH_SK_FLUSH probes — added 2026-05-13 for silent-NOOP diagnosis\n'
            '#include <atomic>\n'
            '#include <cstdio>'
        ),
    },
    # Destructor probe — fire when OpsTask is destroyed; log if op chains remain
    {
        "marker": "OH_SK_FLUSH_OpsTask_DESTROY",
        "anchor": (
            'OpsTask::~OpsTask() {\n'
            '    this->deleteOps();\n'
            '}'
        ),
        "replace": (
            'OpsTask::~OpsTask() {\n'
            + probe(
                "OH_SK_FLUSH_OpsTask_DESTROY",
                "OpsTask DESTROY #%d task=%p numOpChains=%d isColorNoOp=%d",
                "n, (void*)this, this->numOpChains(), this->isColorNoOp()?1:0",
                indent='    ',
            ) + '\n'
            '    this->deleteOps();\n'
            '}'
        ),
    },
    # onExecute entry probe + after-NoOp-check probe
    {
        "marker": "OH_SK_FLUSH_OpsTask_EXEC",
        "anchor": (
            'bool OpsTask::onExecute(GrOpFlushState* flushState) {\n'
            '    SkASSERT(this->numTargets() == 1);\n'
            '    GrRenderTargetProxy* proxy = this->target(0)->asRenderTargetProxy();\n'
            '    SkASSERT(proxy);\n'
            '    SK_AT_SCOPE_EXIT(proxy->clearArenas());\n'
            '\n'
            '    if (this->isColorNoOp() || fClippedContentBounds.isEmpty()) {\n'
            '        return false;\n'
            '    }\n'
        ),
        "replace": (
            'bool OpsTask::onExecute(GrOpFlushState* flushState) {\n'
            + probe(
                "OH_SK_FLUSH_OpsTask_EXEC",
                "OpsTask EXEC ENTRY #%d task=%p numOpChains=%d isColorNoOp=%d clippedBoundsEmpty=%d",
                "n, (void*)this, this->numOpChains(), this->isColorNoOp()?1:0, fClippedContentBounds.isEmpty()?1:0",
                indent='    ',
            ) + '\n'
            '    SkASSERT(this->numTargets() == 1);\n'
            '    GrRenderTargetProxy* proxy = this->target(0)->asRenderTargetProxy();\n'
            '    SkASSERT(proxy);\n'
            '    SK_AT_SCOPE_EXIT(proxy->clearArenas());\n'
            '\n'
            '    if (this->isColorNoOp() || fClippedContentBounds.isEmpty()) {\n'
            + probe(
                "OH_SK_FLUSH_OpsTask_NOOP_SKIP",
                "OpsTask NoOp/BoundsEmpty SKIP #%d task=%p (returns false)",
                "n, (void*)this",
                indent='        ',
            ) + '\n'
            '        return false;\n'
            '    }\n'
        ),
    },
]


# ---------------------------------------------------------------------------
# Patch GrGLOpsRenderPass.cpp
# ---------------------------------------------------------------------------
GL_RENDERPASS_PATCHES = [
    {
        "marker": "OH_SK_GLPASS_INC",
        "anchor": '#include "src/gpu/ganesh/gl/GrGLOpsRenderPass.h"',
        "replace": (
            '#include "src/gpu/ganesh/gl/GrGLOpsRenderPass.h"\n'
            '\n'
            '// OH_SK_FLUSH probes — added 2026-05-13 for silent-NOOP diagnosis\n'
            '#include <atomic>\n'
            '#include <cstdio>'
        ),
    },
    {
        "marker": "OH_SK_FLUSH_GL_BEGIN",
        "anchor": (
            'void GrGLOpsRenderPass::onBegin() {\n'
            '    auto glRT = static_cast<GrGLRenderTarget*>(fRenderTarget);\n'
        ),
        "replace": (
            'void GrGLOpsRenderPass::onBegin() {\n'
            + probe(
                "OH_SK_FLUSH_GL_BEGIN",
                "GLOpsRenderPass onBegin #%d pass=%p rt=%p",
                "n, (void*)this, (void*)fRenderTarget",
                indent='    ',
            ) + '\n'
            '    auto glRT = static_cast<GrGLRenderTarget*>(fRenderTarget);\n'
        ),
    },
    {
        "marker": "OH_SK_FLUSH_GL_DRAW",
        "anchor": (
            'void GrGLOpsRenderPass::onDraw(int vertexCount, int baseVertex) {\n'
            '    SkASSERT(fDidBindVertexBuffer || fGpu->glCaps().drawArraysBaseVertexIsBroken());\n'
        ),
        "replace": (
            'void GrGLOpsRenderPass::onDraw(int vertexCount, int baseVertex) {\n'
            + probe(
                "OH_SK_FLUSH_GL_DRAW",
                "GLOpsRenderPass onDraw #%d pass=%p vtxCount=%d baseVtx=%d",
                "n, (void*)this, vertexCount, baseVertex",
                indent='    ',
            ) + '\n'
            '    SkASSERT(fDidBindVertexBuffer || fGpu->glCaps().drawArraysBaseVertexIsBroken());\n'
        ),
    },
    {
        "marker": "OH_SK_FLUSH_GL_CLEAR",
        "anchor": (
            'void GrGLOpsRenderPass::onClear(const GrScissorState& scissor, std::array<float, 4> color) {\n'
            '    fGpu->clear(scissor, color, fRenderTarget, fUseMultisampleFBO, fOrigin);\n'
            '}'
        ),
        "replace": (
            'void GrGLOpsRenderPass::onClear(const GrScissorState& scissor, std::array<float, 4> color) {\n'
            + probe(
                "OH_SK_FLUSH_GL_CLEAR",
                "GLOpsRenderPass onClear #%d pass=%p rgba=[%.2f,%.2f,%.2f,%.2f]",
                "n, (void*)this, (double)color[0], (double)color[1], (double)color[2], (double)color[3]",
                indent='    ',
            ) + '\n'
            '    fGpu->clear(scissor, color, fRenderTarget, fUseMultisampleFBO, fOrigin);\n'
            '}'
        ),
    },
]


# ---------------------------------------------------------------------------
# Patch GrDirectContext.cpp (already has <atomic>, only need <cstdio>)
# ---------------------------------------------------------------------------
DCTX_PATCHES = [
    {
        "marker": "OH_SK_DCTX_INC",
        "anchor": '#include <atomic>',
        "replace": (
            '#include <atomic>\n'
            '// OH_SK_FLUSH probes — added 2026-05-13\n'
            '#include <cstdio>'
        ),
    },
    {
        "marker": "OH_SK_FLUSH_DCTX_FAS",
        "anchor": (
            'void GrDirectContext::flushAndSubmit(SkSurface* surface, GrSyncCpu sync) {\n'
            '    this->flush(surface, SkSurfaces::BackendSurfaceAccess::kNoAccess, GrFlushInfo());\n'
            '    this->submit(sync);\n'
            '}'
        ),
        "replace": (
            'void GrDirectContext::flushAndSubmit(SkSurface* surface, GrSyncCpu sync) {\n'
            + probe(
                "OH_SK_FLUSH_DCTX_FAS",
                "DCtx::flushAndSubmit #%d ctx=%p surface=%p sync=%d",
                "n, (void*)this, (void*)surface, (int)sync",
                indent='    ',
            ) + '\n'
            '    this->flush(surface, SkSurfaces::BackendSurfaceAccess::kNoAccess, GrFlushInfo());\n'
            '    this->submit(sync);\n'
            '}'
        ),
    },
    {
        "marker": "OH_SK_FLUSH_DCTX_FLUSH",
        "anchor": (
            'GrSemaphoresSubmitted GrDirectContext::flush(SkSurface* surface,\n'
            '                                             SkSurfaces::BackendSurfaceAccess access,\n'
            '                                             const GrFlushInfo& info) {\n'
            '    if (!surface) {\n'
            '        return GrSemaphoresSubmitted::kNo;\n'
            '    }\n'
        ),
        "replace": (
            'GrSemaphoresSubmitted GrDirectContext::flush(SkSurface* surface,\n'
            '                                             SkSurfaces::BackendSurfaceAccess access,\n'
            '                                             const GrFlushInfo& info) {\n'
            + probe(
                "OH_SK_FLUSH_DCTX_FLUSH",
                "DCtx::flush(surface) #%d ctx=%p surface=%p",
                "n, (void*)this, (void*)surface",
                indent='    ',
            ) + '\n'
            '    if (!surface) {\n'
            '        return GrSemaphoresSubmitted::kNo;\n'
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
    backup = target_path + ".bak.OH_SK_REPLAY_V3"
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
    print("=== apply_oh_sk_replay_probe_v3.py ===")
    a = patch_file(OPS_TASK_CPP, OPS_TASK_PATCHES)
    b = patch_file(GL_RENDERPASS, GL_RENDERPASS_PATCHES)
    c = patch_file(DIRECTCONTEXT, DCTX_PATCHES)
    if a or b or c:
        print("V3 probes inserted. Re-compile libskia_canvaskit to take effect.")
    else:
        print("No changes (already patched).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
