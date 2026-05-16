#!/usr/bin/env python3
# ============================================================================
# apply_hwui_vulkan_manager_stub.py
#
# Blocker A.4 (2026-04-22): wrap 3 RenderThread::vulkanManager() call sites
# with #ifdef HWUI_NO_VULKAN so they become no-op under the OH adapter build
# (compile_libhwui.sh already passes -DHWUI_NO_VULKAN=1).
#
# Without this patch, libhwui.so link/dlopen reports UND:
#   _ZN7android10uirenderer12renderthread12RenderThread13vulkanManagerEv
# because the vulkanManager() definition is already under #ifndef HWUI_NO_VULKAN
# (not compiled for adapter), but 3 call sites were NOT wrapped, so the
# compiler emitted external symbol references to the missing definition.
#
# Three sites patched (all in AOSP hwui, not in Skia-vulkan-pipeline-only files
# which are excluded from compile_libhwui.sh sources):
#
#   1. frameworks/base/libs/hwui/renderthread/RenderThread.cpp
#        void RenderThread::requireVkContext() { ... } (Vulkan pipeline init)
#
#   2. frameworks/base/libs/hwui/DeferredLayerUpdater.cpp
#        createReleaseFence — Vulkan branch of GL/Vulkan fence dispatch
#
#   3. frameworks/base/libs/hwui/DeferredLayerUpdater.cpp
#        fenceWait — Vulkan branch of GL/Vulkan fence dispatch
#
# Under HWUI_NO_VULKAN, all 3 branches become runtime errors (LOG_ALWAYS_FATAL)
# or safe no-ops (err = NO_ERROR). They are unreachable at runtime because OH
# adapter only uses SkiaGL pipeline (no SkiaVulkan).
#
# Idempotent: re-running is safe (checks for marker before patching).
#
# See doc/build_patch_log.html appendix AA.8 for the full diagnostic chain.
# ============================================================================

import os
import sys

# AOSP base path — default matches restore_after_sync.sh convention.
AOSP = os.environ.get("AOSP_ROOT", os.path.expanduser("~/aosp"))

HWUI = os.path.join(AOSP, "frameworks/base/libs/hwui")

RT_CPP = os.path.join(HWUI, "renderthread/RenderThread.cpp")
DLU_CPP = os.path.join(HWUI, "DeferredLayerUpdater.cpp")
HBU_CPP = os.path.join(HWUI, "HardwareBitmapUploader.cpp")


def read(p):
    with open(p) as f:
        return f.read()


def write(p, s):
    with open(p, "w") as f:
        f.write(s)


def patch_render_thread():
    src = read(RT_CPP)

    # Idempotency check: already patched?
    if 'LOG_ALWAYS_FATAL("requireVkContext() called under HWUI_NO_VULKAN' in src:
        print("[SKIP] RenderThread.cpp::requireVkContext already patched")
        return

    old_body = '''void RenderThread::requireVkContext() {
    // the getter creates the context in the event it had been destroyed by destroyRenderingContext
    // Also check if we have a GrContext before returning fast. VulkanManager may be shared with
    // the HardwareBitmapUploader which initializes the Vk context without persisting the GrContext
    // in the rendering thread.
    if (vulkanManager().hasVkContext() && mGrContext) {
        return;
    }
    mVkManager->initialize();
    GrContextOptions options;
    initGrContextOptions(options);
    auto vkDriverVersion = mVkManager->getDriverVersion();
    cacheManager().configureContext(&options, &vkDriverVersion, sizeof(vkDriverVersion));
    sk_sp<GrDirectContext> grContext = mVkManager->createContext(options);
    LOG_ALWAYS_FATAL_IF(!grContext.get());
    setGrContext(grContext);
}'''

    new_body = '''void RenderThread::requireVkContext() {
#ifdef HWUI_NO_VULKAN
    // OH adapter build: Vulkan pipeline not built (libhwui compiled with
    // HWUI_NO_VULKAN=1, VulkanManager.cpp not in sources). All renderers
    // use SkiaGL; requireVkContext should never be reachable at runtime.
    LOG_ALWAYS_FATAL("requireVkContext() called under HWUI_NO_VULKAN build; "
                     "OH adapter only supports SkiaGL pipeline.");
#else
    // the getter creates the context in the event it had been destroyed by destroyRenderingContext
    // Also check if we have a GrContext before returning fast. VulkanManager may be shared with
    // the HardwareBitmapUploader which initializes the Vk context without persisting the GrContext
    // in the rendering thread.
    if (vulkanManager().hasVkContext() && mGrContext) {
        return;
    }
    mVkManager->initialize();
    GrContextOptions options;
    initGrContextOptions(options);
    auto vkDriverVersion = mVkManager->getDriverVersion();
    cacheManager().configureContext(&options, &vkDriverVersion, sizeof(vkDriverVersion));
    sk_sp<GrDirectContext> grContext = mVkManager->createContext(options);
    LOG_ALWAYS_FATAL_IF(!grContext.get());
    setGrContext(grContext);
#endif
}'''

    if old_body not in src:
        print("[FAIL] RenderThread.cpp::requireVkContext original body not matched "
              "(maybe already patched by a different pattern)", file=sys.stderr)
        sys.exit(1)

    write(RT_CPP, src.replace(old_body, new_body, 1))
    print("[OK] RenderThread.cpp::requireVkContext wrapped under HWUI_NO_VULKAN")


def patch_deferred_layer_updater():
    src = read(DLU_CPP)
    modified = False

    # ---- Site 1: createReleaseFence ----
    if 'OH adapter: Vulkan pipeline absent; no-op — no fence to create' in src:
        print("[SKIP] DeferredLayerUpdater.cpp createReleaseFence already patched")
    else:
        old1 = '''    } else {
        int previousSlot = dlu->mCurrentSlot;
        if (previousSlot != -1) {
            dlu->mImageSlots[previousSlot].releaseQueueOwnership(
                    renderState.getRenderThread().getGrContext());
        }
        err = renderState.getRenderThread().vulkanManager().createReleaseFence(
                releaseFence, renderState.getRenderThread().getGrContext());
    }
    return err;
}

status_t DeferredLayerUpdater::fenceWait(int fence, void* handle) {'''

        new1 = '''    } else {
#ifdef HWUI_NO_VULKAN
        // OH adapter: Vulkan pipeline absent; no-op — no fence to create.
        err = NO_ERROR;
#else
        int previousSlot = dlu->mCurrentSlot;
        if (previousSlot != -1) {
            dlu->mImageSlots[previousSlot].releaseQueueOwnership(
                    renderState.getRenderThread().getGrContext());
        }
        err = renderState.getRenderThread().vulkanManager().createReleaseFence(
                releaseFence, renderState.getRenderThread().getGrContext());
#endif
    }
    return err;
}

status_t DeferredLayerUpdater::fenceWait(int fence, void* handle) {'''

        if old1 not in src:
            print("[FAIL] DeferredLayerUpdater.cpp createReleaseFence pattern not matched",
                  file=sys.stderr)
            sys.exit(2)
        src = src.replace(old1, new1, 1)
        modified = True
        print("[OK] DeferredLayerUpdater.cpp createReleaseFence wrapped")

    # ---- Site 2: fenceWait ----
    if 'OH adapter: Vulkan pipeline absent; no-op — no fence to wait on' in src:
        print("[SKIP] DeferredLayerUpdater.cpp fenceWait already patched")
    else:
        old2 = '''    } else {
        err = renderState.getRenderThread().vulkanManager().fenceWait(
                fence, renderState.getRenderThread().getGrContext());
    }
    return err;
}

void DeferredLayerUpdater::apply() {'''

        new2 = '''    } else {
#ifdef HWUI_NO_VULKAN
        // OH adapter: Vulkan pipeline absent; no-op — no fence to wait on.
        err = NO_ERROR;
#else
        err = renderState.getRenderThread().vulkanManager().fenceWait(
                fence, renderState.getRenderThread().getGrContext());
#endif
    }
    return err;
}

void DeferredLayerUpdater::apply() {'''

        if old2 not in src:
            print("[FAIL] DeferredLayerUpdater.cpp fenceWait pattern not matched",
                  file=sys.stderr)
            sys.exit(3)
        src = src.replace(old2, new2, 1)
        modified = True
        print("[OK] DeferredLayerUpdater.cpp fenceWait wrapped")

    if modified:
        write(DLU_CPP, src)


def patch_hardware_bitmap_uploader():
    """
    HardwareBitmapUploader.cpp::onUploadHardwareBitmap originally had:
        #ifndef HWUI_NO_VULKAN
              vkManager = getVulkanManager();
        #else
              vkManager = nullptr;
        #endif
              if (!vkManager->hasVkContext()) { vkManager->initialize(); }
              ...vkManager->createContext(...)
    The ifdef only gated which vkManager was obtained (null vs real); the
    subsequent vkManager->initialize() and createContext() calls were left
    OUTSIDE the ifdef and still generated external symbol references to
    VulkanManager::initialize / createContext, breaking libhwui link under
    HWUI_NO_VULKAN=1. Solution: move the entire Vulkan upload block into
    the ifdef, so HWUI_NO_VULKAN path skips it entirely. Upload then
    leaves uploadSucceeded=false, caller falls back to EGL upload path
    via createUploader().
    """
    src = read(HBU_CPP)

    if "// HWUI_NO_VULKAN: skip entire Vulkan upload path" in src:
        print("[SKIP] HardwareBitmapUploader.cpp already patched")
        return

    old = """#ifndef HWUI_NO_VULKAN
          renderthread::VulkanManager* vkManager = getVulkanManager();
#else
          renderthread::VulkanManager* vkManager = nullptr;
#endif
          if (!vkManager->hasVkContext()) {
              LOG_ALWAYS_FATAL_IF(mGrContext,
                                  "GrContext exists with no VulkanManager for vulkan uploads");
              vkManager->initialize();
          }

          if (!mGrContext) {
              GrContextOptions options;
              mGrContext = vkManager->createContext(options,
                      renderthread::VulkanManager::ContextType::kUploadThread);
              LOG_ALWAYS_FATAL_IF(!mGrContext, "failed to create GrContext for vulkan uploads");
              this->postIdleTimeoutCheck();
          }"""

    new = """#ifdef HWUI_NO_VULKAN
          // HWUI_NO_VULKAN: skip entire Vulkan upload path. Bitmap upload
          // via Vulkan is dead code on OH adapter (we use SkiaGL pipeline).
          // Returning without uploading leaves uploadSucceeded=false — caller
          // falls back to EGL upload path in createUploader().
          (void)mGrContext;
#else
          renderthread::VulkanManager* vkManager = getVulkanManager();
          if (!vkManager->hasVkContext()) {
              LOG_ALWAYS_FATAL_IF(mGrContext,
                                  "GrContext exists with no VulkanManager for vulkan uploads");
              vkManager->initialize();
          }

          if (!mGrContext) {
              GrContextOptions options;
              mGrContext = vkManager->createContext(options,
                      renderthread::VulkanManager::ContextType::kUploadThread);
              LOG_ALWAYS_FATAL_IF(!mGrContext, "failed to create GrContext for vulkan uploads");
              this->postIdleTimeoutCheck();
          }
#endif"""

    if old not in src:
        print("[FAIL] HardwareBitmapUploader.cpp onUploadHardwareBitmap pattern not matched",
              file=sys.stderr)
        sys.exit(5)

    write(HBU_CPP, src.replace(old, new, 1))
    print("[OK] HardwareBitmapUploader.cpp onUploadHardwareBitmap Vulkan path gated")


def main():
    for p in (RT_CPP, DLU_CPP, HBU_CPP):
        if not os.path.exists(p):
            print(f"[FAIL] source not found: {p}", file=sys.stderr)
            sys.exit(4)

    patch_render_thread()
    patch_deferred_layer_updater()
    patch_hardware_bitmap_uploader()
    print("[OK] A.4/A.5 HWUI_NO_VULKAN vulkanManager() + VulkanManager:: call-site wrapping complete")


if __name__ == "__main__":
    main()
