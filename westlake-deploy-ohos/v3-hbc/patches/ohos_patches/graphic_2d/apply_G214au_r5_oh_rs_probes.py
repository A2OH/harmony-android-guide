#!/usr/bin/env python3
"""apply_G214au_r5_oh_rs_probes.py

G2.14au r5 — In-place patch OH RS source to add probes tracking helloworld
buffer flow through the OH RenderService server.

Goal: confirm whether helloworld's buffer (which hwui successfully
eglSwapBuffers'd in G2.14au r4) reaches the OH RS server, whether it
gets traversed by the uni-render visitor, and whether the HWC layer
created for it carries the buffer correctly (alpha, dimensions, etc).

3 probe points (in order of buffer flow through OH RS):
    1. RSSurfaceRenderNode::UpdateBufferInfo
         librender_service_base.z.so — entry where buffer arrives at node
    2. RSUniRenderVisitor::QuickPrepareSurfaceRenderNode
         librender_service.z.so — per-surface tree traversal
    3. RSUniRenderProcessor::CreateLayer(RSSurfaceRenderNode&, ...)
         librender_service.z.so — HWC layer creation per surface

All probes filter by GetName().find("helloworld") to avoid log flooding
from other surfaces (launcher, statusbar, etc).  Uses RS_LOGI (OH's
HiLog macro) since OH RS is a system process with no stderr redirect.
Idempotent (marker-based skip).

Project rule compliance:
  - Patches OH source IN-PLACE under ~/oh/ (per CLAUDE.md "patch then keep")
  - .bak.G214au_r5 backups created on first run for rollback
  - Registered in build/restore_after_sync.sh B7 block for one-cmd restore
  - Companion entry in doc/build_patch_log.html G2.14au r5

Compile + deploy:
    ECS:    cd ~/oh && ./build.sh --product-name rk3568 --ccache \\
                --build-target render_service_base --build-target render_service
    Deploy: librender_service_base.z.so + librender_service.z.so
            via deploy/deploy_to_dayu200.sh
"""
import os
import shutil
import sys

OH_ROOT = os.environ.get("OH_ROOT", "/home/HanBingChen/oh")
RS_BASE = os.path.join(OH_ROOT, "foundation/graphic/graphic_2d/rosen/modules/render_service_base/src")
RS = os.path.join(OH_ROOT, "foundation/graphic/graphic_2d/rosen/modules/render_service/core")

PROBES = [
    {
        "abs_path": os.path.join(RS_BASE, "pipeline/rs_surface_render_node.cpp"),
        "label": "render_service_base/rs_surface_render_node.cpp :: UpdateBufferInfo",
        "marker": "G214au_RS_UpdateBufferInfo",
        "needle": "void RSSurfaceRenderNode::UpdateBufferInfo(const sptr<SurfaceBuffer>& buffer,\n    std::shared_ptr<RSSurfaceHandler::BufferOwnerCount> bufferOwnerCount, const Rect& damageRect,\n    const sptr<SyncFence>& acquireFence, const sptr<SurfaceBuffer>& preBuffer,\n    std::shared_ptr<RSSurfaceHandler::BufferOwnerCount> preBufferOwnerCount)\n{",
        "trace": """
    // G2.14au r5 probe — RSSurfaceRenderNode::UpdateBufferInfo entry
    // marker: G214au_RS_UpdateBufferInfo
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        const auto& sName = GetName();
        if (sName.find(\"helloworld\") != std::string::npos && (n == 1 || n % 30 == 0)) {
            RS_LOGI(\"[G214au_RS] UpdateBufferInfo #%{public}d name=%{public}s id=%{public}\" PRIu64
                    \" bufferPtr=%{public}p w=%{public}d h=%{public}d damage=[%{public}d,%{public}d,%{public}d,%{public}d]\",
                    n, sName.c_str(), GetId(), buffer.GetRefPtr(),
                    buffer ? buffer->GetWidth() : 0, buffer ? buffer->GetHeight() : 0,
                    damageRect.x, damageRect.y, damageRect.w, damageRect.h);
        }
    }
""",
    },
    {
        "abs_path": os.path.join(RS, "pipeline/main_thread/rs_uni_render_visitor.cpp"),
        "label": "render_service/rs_uni_render_visitor.cpp :: QuickPrepareSurfaceRenderNode",
        "marker": "G214au_RS_QuickPrep",
        "needle": "void RSUniRenderVisitor::QuickPrepareSurfaceRenderNode(RSSurfaceRenderNode& node)\n{",
        "trace": """
    // G2.14au r5 probe — RSUniRenderVisitor::QuickPrepareSurfaceRenderNode entry
    // marker: G214au_RS_QuickPrep
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        const auto& sName = node.GetName();
        if (sName.find(\"helloworld\") != std::string::npos && (n == 1 || n % 30 == 0)) {
            RS_LOGI(\"[G214au_RS] QuickPrepareSurfaceRenderNode #%{public}d name=%{public}s id=%{public}\" PRIu64
                    \" onTree=%{public}d\",
                    n, sName.c_str(), node.GetId(), node.IsOnTheTree() ? 1 : 0);
        }
    }
""",
    },
    {
        "abs_path": os.path.join(RS, "pipeline/render_thread/rs_uni_render_processor.cpp"),
        "label": "render_service/rs_uni_render_processor.cpp :: CreateLayer(SurfaceRenderNode&, ...)",
        "marker": "G214au_RS_CreateLayer",
        "needle": "void RSUniRenderProcessor::CreateLayer(RSSurfaceRenderNode& node, RSSurfaceRenderParams& params,",
        "trace_after_brace": True,
        "trace": """
    // G2.14au r5 probe — RSUniRenderProcessor::CreateLayer(RSSurfaceRenderNode&) entry
    // marker: G214au_RS_CreateLayer
    {
        static std::atomic<int> g_count{0};
        int n = ++g_count;
        const auto& sName = node.GetName();
        if (sName.find(\"helloworld\") != std::string::npos && (n == 1 || n % 30 == 0)) {
            auto buf = params.GetBuffer();
            const auto& bounds = params.GetBounds();
            RS_LOGI(\"[G214au_RS] CreateLayer #%{public}d name=%{public}s id=%{public}\" PRIu64
                    \" bufferPtr=%{public}p bounds=[%{public}f,%{public}f,%{public}f,%{public}f] alpha=%{public}f\",
                    n, sName.c_str(), node.GetId(), buf.GetRefPtr(),
                    bounds.GetLeft(), bounds.GetTop(), bounds.GetWidth(), bounds.GetHeight(),
                    params.GetAlpha());
        }
    }
""",
    },
]


def apply_probe(probe):
    path = probe["abs_path"]
    backup = path + ".bak.G214au_r5"
    label = probe["label"]

    if not os.path.isfile(path):
        print(f"[FAIL] {label} — source not found: {path}")
        sys.exit(1)

    with open(path, "r", encoding="utf-8") as f:
        content = f.read()

    if probe["marker"] in content:
        print(f"[SKIP] {label} — already patched")
        return

    if probe["needle"] not in content:
        print(f"[FAIL] {label} — needle not found")
        print(f"       needle (first 100 chars): {probe['needle'][:100]!r}")
        sys.exit(1)

    occurrences = content.count(probe["needle"])
    if occurrences != 1:
        print(f"[FAIL] {label} — needle appears {occurrences} times, expected exactly 1")
        sys.exit(1)

    if not os.path.exists(backup):
        shutil.copy2(path, backup)
        print(f"[BAK]  {label} -> {os.path.basename(backup)}")

    if probe.get("trace_after_brace"):
        idx = content.index(probe["needle"])
        brace = content.find("{", idx + len(probe["needle"]))
        if brace < 0:
            print(f"[FAIL] {label} — no '{{' after needle")
            sys.exit(1)
        new_content = content[: brace + 1] + probe["trace"] + content[brace + 1 :]
    else:
        new_content = content.replace(
            probe["needle"], probe["needle"] + probe["trace"], 1
        )

    with open(path, "w", encoding="utf-8") as f:
        f.write(new_content)
    print(f"[OK]   {label} — probe inserted")


def main():
    print(f"Applying G2.14au r5 OH RS probes under: {OH_ROOT}")
    for probe in PROBES:
        apply_probe(probe)
    print()
    print("Done.  Rebuild OH RS:")
    print("  cd ~/oh && ./build.sh --product-name rk3568 --ccache \\")
    print("      --build-target render_service_base --build-target render_service")
    print()
    print("Then deploy librender_service_base.z.so + librender_service.z.so")
    print("via deploy/deploy_to_dayu200.sh and reboot.")


if __name__ == "__main__":
    main()
