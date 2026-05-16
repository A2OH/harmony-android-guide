#!/bin/bash
# ============================================================================
# restore_after_sync.sh — Single-Command Recovery Orchestrator
# ============================================================================
#
# Purpose: After a fresh `repo sync` of AOSP (~/aosp/) and/or OpenHarmony
# (~/oh/), this single script restores every modification needed to reach
# the last known-good buildable state. It is the canonical implementation
# of CLAUDE.md "一键恢复规则 (Single-Command Recovery Rule)".
#
# What it does (in order):
#   Phase A — AOSP source restoration
#     A1. Install custom product (device/adapter/oh_adapter)
#     A2. Apply aosp_build_patches/*.patch
#     A3. Apply L5 reflection injection (apply_aosp_java_patches.py)
#     A4. Apply aosp_patches/libs/hwui/*.patch
#     A5. Disable non-essential Android.bp files
#     A6. Create CTS/VTS stub files
#     A7. Deploy dex2oat_stubs/ trees
#     A8. Deploy hwui stub source files
#     A9. Apply hwui Python increment scripts (UNSEDIMENTED — see below)
#     A10. Apply aosp_patches/art/**/*.patch (native libart fixes)
#   Phase B — OH source restoration
#     B1. Apply ohos_patches/build/*.patch (build system fixes)
#     B2. (No-op on rk3568 — product config replacement not needed)
#     B3. Apply ohos_patches/ability_rt/, bundle_framework/, graphic_2d/
#     B4. Apply third_party/musl/ syscall.h.in patch
#     B6. graphic_2d rs_buffer_reclaim.cpp format fix
#     B8. bundle_framework BMS BUILD.gn — add adapter_apk_install_minimal.cpp +
#         apk_manifest_parser.cpp + cflags_cc -I AOSP headers + ldflags
#         libandroidfw + OH_ADAPTER_ANDROID define (gap 6 enabler).
#         Patch uses __AOSP_ROOT__ placeholder, sed-substituted at apply time.
#     B9. ets2abc_config.gni value collision fix (gn gen unblocker for gap 6)
#     B10. Deploy adapter_apk_install_minimal.cpp + apk_manifest_parser.{h,cpp}
#         from canonical adapter sources into oh/foundation/.../bundlemgr/src/.
#         Required by BUILD.gn from B8. Idempotent cp.
#   Phase C — Cross-cutting fixes
#     C1. Deploy skia_compat_headers/ to ECS-known location (no-op if same path)
#     (C2 removed — libarkruntime.so / libani_helpers.z.so are phony'd, not needed)
#   Phase POST — These run AFTER `gn gen` not before — invoked by build.sh:
#     POST1. musl_syscall_fix.sh
#     POST2. ninja_patches/apply_all.sh
#
# Properties (per CLAUDE.md "三性"):
#   * Self-contained — operates from clean source trees, no hidden state
#   * Idempotent     — re-runnable; each phase checks before applying
#   * Traceable      — every action logged; exit-on-error; final state file
#
# IMPORTANT — Honest disclosure (per Post-Completion Feedback Rule):
#
#   1. Phase A9 applies the consolidated aosp_patches/libs/hwui/hwui_rk3568.patch
#      (1263 lines, 47 files) via `git apply`. This patch is the sedimented
#      form of what used to be 30+ Python increment scripts (hwui_phase2_round*.py,
#      hwui_phase3_jni_round*.py, hwui_l2[1-5]_*.py, hwui_round1[23]_*.py).
#      Those scripts have been moved to build/_deprecated/hwui_increment_scripts/
#      as historical artifacts and are no longer called by any live build path.
#
#   2. All paths use $OH_PRODUCT_NAME (default rk3568) and $HOME-based
#      defaults per CLAUDE.md. apply_aosp_java_patches.py accepts
#      $AOSP_ROOT env var as of 2026-04-11 cleanup.
#
#   3. libarkruntime.so / libani_helpers.z.so are NOT needed by this project
#      (confirmed by user 2026-04-11). The ninja phony patches in
#      build/ninja_patches/ make the build graph complete without actually
#      producing these libraries. Phase C2 has been removed.
#
#   4. This script has NOT been validated end-to-end on ECS yet. First-run
#      issues are expected and must be fixed at root cause (no stubs).
#
# Usage:
#   bash restore_after_sync.sh                    # restore both AOSP & OH
#   bash restore_after_sync.sh --only-aosp        # restore only AOSP side
#   bash restore_after_sync.sh --only-oh          # restore only OH side
#   bash restore_after_sync.sh --skip-hwui-py     # skip Phase A9 (Python increments)
#   bash restore_after_sync.sh --aosp-root=PATH   # override AOSP root
#   bash restore_after_sync.sh --oh-root=PATH     # override OH root
#   bash restore_after_sync.sh --dry-run          # print actions without executing
#
# Exit codes:
#   0  — success
#   1  — fatal error (logged before exit)
#   2  — prerequisite missing (paths / source trees)
#   3  — partial success with warnings (some phase skipped)
#
# Author: Adapter project | Created 2026-04-11 per CLAUDE.md "一键恢复规则"
# ============================================================================

set -euo pipefail

# ----------------------------------------------------------------------------
# 0. Globals & defaults
# ----------------------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ADAPTER_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Authoritative defaults per CLAUDE.md (override via env or --flag)
AOSP_ROOT="${AOSP_ROOT:-$HOME/aosp}"
OH_ROOT="${OH_ROOT:-$HOME/oh}"
OH_PRODUCT="${OH_PRODUCT:-rk3568}"   # CLAUDE.md says current is rk3568 (DAYU200)
OH_OUT_DIR="${OH_OUT_DIR:-$OH_ROOT/out/$OH_PRODUCT}"

DO_AOSP=1
DO_OH=1
DO_HWUI_PY=1
DRY_RUN=0
WARNINGS=0
STATE_FILE="$ADAPTER_ROOT/out/restore_state.txt"

# Color codes (degrade gracefully on dumb terminals)
if [ -t 1 ]; then
    C_RED=$'\033[31m'; C_GREEN=$'\033[32m'; C_YELLOW=$'\033[33m'
    C_BLUE=$'\033[34m'; C_BOLD=$'\033[1m'; C_RESET=$'\033[0m'
else
    C_RED=""; C_GREEN=""; C_YELLOW=""; C_BLUE=""; C_BOLD=""; C_RESET=""
fi

log_info()  { printf '%s[INFO]%s  %s\n' "$C_BLUE"   "$C_RESET" "$*"; }
log_ok()    { printf '%s[OK]%s    %s\n' "$C_GREEN"  "$C_RESET" "$*"; }
log_warn()  { printf '%s[WARN]%s  %s\n' "$C_YELLOW" "$C_RESET" "$*" >&2; WARNINGS=$((WARNINGS+1)); }
log_error() { printf '%s[ERR]%s   %s\n' "$C_RED"    "$C_RESET" "$*" >&2; }
log_phase() { printf '\n%s========== %s ==========%s\n' "$C_BOLD" "$*" "$C_RESET"; }

run() {
    if [ "$DRY_RUN" = "1" ]; then
        printf '  [dry-run] %s\n' "$*"
    else
        eval "$@"
    fi
}

# ----------------------------------------------------------------------------
# 1. Argument parsing
# ----------------------------------------------------------------------------

for arg in "$@"; do
    case "$arg" in
        --only-aosp)        DO_OH=0 ;;
        --only-oh)          DO_AOSP=0 ;;
        --skip-hwui-py)     DO_HWUI_PY=0 ;;
        --aosp-root=*)      AOSP_ROOT="${arg#*=}" ;;
        --oh-root=*)        OH_ROOT="${arg#*=}"; OH_OUT_DIR="$OH_ROOT/out/$OH_PRODUCT" ;;
        --product=*)        OH_PRODUCT="${arg#*=}"; OH_OUT_DIR="$OH_ROOT/out/$OH_PRODUCT" ;;
        --dry-run)          DRY_RUN=1 ;;
        --help|-h)
            sed -n '/^# Usage:/,/^$/p' "$0" | sed 's/^# \?//'
            exit 0 ;;
        *)
            log_error "Unknown argument: $arg"
            exit 1 ;;
    esac
done

# ----------------------------------------------------------------------------
# 2. Banner & inconsistency warnings
# ----------------------------------------------------------------------------

cat <<EOF
${C_BOLD}================================================================================
  Adapter Restore-After-Sync v1.0
================================================================================${C_RESET}
  ADAPTER_ROOT  = $ADAPTER_ROOT
  AOSP_ROOT     = $AOSP_ROOT     ($([ -d "$AOSP_ROOT" ] && echo "exists" || echo "${C_RED}MISSING${C_RESET}"))
  OH_ROOT       = $OH_ROOT       ($([ -d "$OH_ROOT" ] && echo "exists" || echo "${C_RED}MISSING${C_RESET}"))
  OH_PRODUCT    = $OH_PRODUCT
  OH_OUT_DIR    = $OH_OUT_DIR
  DO_AOSP       = $DO_AOSP
  DO_OH         = $DO_OH
  DO_HWUI_PY    = $DO_HWUI_PY
  DRY_RUN       = $DRY_RUN
EOF

log_phase "Pre-flight notices"
log_info  "OH_CONFIG_FILE is empty — no product config override on rk3568. If build fails on missing thirdparty deps, author rk3568_config.json and re-enable in config.sh."
log_info  "apply_aosp_java_patches.py reads \$AOSP_ROOT env var; this script passes it through."

# Placeholder patch detection (gap 0.2 instrumentation, 2026-04-11)
if [ -f "$SCRIPT_DIR/check_placeholder_patches.py" ]; then
    if ! python3 "$SCRIPT_DIR/check_placeholder_patches.py" --quiet --root "$ADAPTER_ROOT" >/dev/null 2>&1; then
        log_warn "Placeholder patches detected — those phases will be SKIPPED with warnings"
        log_warn "Run: python3 build/check_placeholder_patches.py  (to see the list)"
        log_warn "See doc/build_patch_log.html appendix A.3 for finalization instructions"
    fi
fi

WARNINGS=0  # reset counter — these are pre-flight notices, not action warnings

# Sanity check: at least one source root must exist (unless dry-run)
if [ "$DRY_RUN" = "0" ]; then
    if [ "$DO_AOSP" = "1" ] && [ ! -d "$AOSP_ROOT" ]; then
        log_error "AOSP_ROOT not found: $AOSP_ROOT"
        exit 2
    fi
    if [ "$DO_OH" = "1" ] && [ ! -d "$OH_ROOT" ]; then
        log_error "OH_ROOT not found: $OH_ROOT"
        exit 2
    fi
fi

# ============================================================================
# Phase A — AOSP source restoration
# ============================================================================

if [ "$DO_AOSP" = "1" ]; then

log_phase "Phase A — AOSP source restoration"

# ---- A1. Install custom product (device/adapter/oh_adapter) ----
log_info "A1. Install custom product device/adapter/oh_adapter"
PRODUCT_SRC="$ADAPTER_ROOT/build/device/adapter/oh_adapter"
PRODUCT_DST="$AOSP_ROOT/device/adapter/oh_adapter"
if [ -d "$PRODUCT_SRC" ]; then
    run "mkdir -p \"$PRODUCT_DST\""
    for f in AndroidProducts.mk oh_adapter.mk BoardConfig.mk; do
        if [ -f "$PRODUCT_SRC/$f" ]; then
            run "cp -v \"$PRODUCT_SRC/$f\" \"$PRODUCT_DST/\""
        fi
    done
    log_ok "A1 done"
else
    log_warn "A1 skipped — $PRODUCT_SRC not found"
fi

# ---- A2. Apply aosp_build_patches/*.patch ----
log_info "A2. Apply aosp_build_patches/*.patch via apply_patches.sh"
if [ -f "$SCRIPT_DIR/apply_patches.sh" ]; then
    run "bash \"$SCRIPT_DIR/apply_patches.sh\" --aosp-root=\"$AOSP_ROOT\" || true"
    log_ok "A2 done (existing apply_patches.sh handles internally)"
else
    log_warn "A2 skipped — apply_patches.sh not found"
fi

# ---- A2b. Apply Phase 1 (2026-04-28) libandroidfw modern-API patches ----
# These patches enable cross-compilation of AssetManager2/ApkAssets/Theme:
#   - aosp_patches/frameworks/base/libs/androidfw/include/androidfw/AssetManager2.h.patch
#       moves Theme::Entry full def into header (OH libcxx requires complete
#       type for std::vector<Entry>).
#   - aosp_patches/frameworks/base/libs/androidfw/AssetManager2.cpp.patch
#       deletes duplicate Entry definition from cpp.
#   - aosp_patches/frameworks/base/libs/androidfw/Asset.cpp.patch
#       removes 2x assert(dataMap != NULL) (IncFsFileMap value lacks operator bool).
log_info "A2b. Apply Phase 1 libandroidfw modern-API patches"
AFW_PATCHES_DIR="$ADAPTER_ROOT/aosp_patches/frameworks/base/libs/androidfw"
AFW_TARGET_DIR="$AOSP_ROOT/frameworks/base/libs/androidfw"
for patch_rel in "include/androidfw/AssetManager2.h.patch" "AssetManager2.cpp.patch" "Asset.cpp.patch"; do
    p="$AFW_PATCHES_DIR/$patch_rel"
    target_rel="${patch_rel%.patch}"
    target="$AFW_TARGET_DIR/$target_rel"
    if [ -f "$p" ] && [ -f "$target" ]; then
        # Backup once, then ensure idempotent re-apply: restore from backup, then patch
        if [ ! -f "${target}.bak" ]; then run "cp \"$target\" \"${target}.bak\""; fi
        run "cp \"${target}.bak\" \"$target\""
        if patch -p0 "$target" < "$p" >/dev/null 2>&1; then
            log_ok "A2b applied: $patch_rel"
        else
            # Older patch tools may need -l/--ignore-whitespace; retry with --forward
            if patch --forward -p0 "$target" < "$p" >/dev/null 2>&1; then
                log_ok "A2b applied (forward): $patch_rel"
            else
                log_warn "A2b apply FAILED for $patch_rel — manual inspection needed"
            fi
        fi
    elif [ ! -f "$target" ]; then
        log_warn "A2b skipped (target absent): $target"
    elif [ ! -f "$p" ]; then
        log_warn "A2b skipped (patch absent): $p"
    fi
done

# ---- A2c. Apply aosp_patches single-file patches outside libandroidfw ----
# 2026-05-09: 这 3 个 patch 历史上已应用到 AOSP 源码（有 .bak 备份 + grep marker
# 命中证据），但从未沉淀到 restore_after_sync.sh，是孤儿 — repo sync 必丢。
# 按 feedback_no_revert_patch.md 纪律一并沉淀。
#
# Patches:
#   - aosp_patches/external/icu/android_icu4j/Android.bp.patch
#       Adds java_version: "1.8" to core-repackaged-icu4j Android.bp target so
#       it compiles under partial-sync AOSP (default Java level mismatch).
#   - aosp_patches/frameworks/base/core/jni/AndroidRuntime.cpp.patch
#       DISABLE-comments out 78 register_android_os_Hidl* / HwBinder / HwParcel
#       / HwRemoteBinder JNI registrations — these classes belong to AOSP modules
#       not present in our partial sync. Without this patch, AndroidRuntime
#       startReg fails to link → libandroid_runtime.so cross-compile error.
#   - aosp_patches/frameworks/base/core/jni/android_util_Process.cpp.patch
#       Adds extern declarations for androidSetThreadPriority/GetThreadPriority.
#       libutils's AndroidThreads.h wraps these in `#if defined(__ANDROID__)`
#       and we cross-compile against OH musl sysroot without that define.
log_info "A2c. Apply aosp_patches single-file patches (icu / AndroidRuntime / Process)"
declare -a A2C_PATCHES=(
    "external/icu/android_icu4j/Android.bp.patch"
    "frameworks/base/core/jni/AndroidRuntime.cpp.patch"
    "frameworks/base/core/jni/android_util_Process.cpp.patch"
)
for patch_rel in "${A2C_PATCHES[@]}"; do
    p="$ADAPTER_ROOT/aosp_patches/$patch_rel"
    target_rel="${patch_rel%.patch}"
    target="$AOSP_ROOT/$target_rel"
    if [ ! -f "$p" ]; then
        log_warn "A2c skipped (patch absent): $p"
        continue
    fi
    if [ ! -f "$target" ]; then
        log_warn "A2c skipped (target absent): $target"
        continue
    fi
    # Backup once
    if [ ! -f "${target}.bak" ]; then run "cp \"$target\" \"${target}.bak\""; fi
    # Restore from backup then re-apply (idempotent)
    run "cp \"${target}.bak\" \"$target\""
    # Try unified-diff git apply first (handles a/b/ prefix); fall back to patch -p0
    if (cd "$AOSP_ROOT" && git apply --check "$p") 2>/dev/null; then
        run "(cd \"$AOSP_ROOT\" && git apply \"$p\")"
        log_ok "A2c applied (git apply): $patch_rel"
    elif patch -p1 -d "$AOSP_ROOT" < "$p" >/dev/null 2>&1; then
        log_ok "A2c applied (patch -p1): $patch_rel"
    elif patch -p0 "$target" < "$p" >/dev/null 2>&1; then
        log_ok "A2c applied (patch -p0): $patch_rel"
    else
        log_warn "A2c apply FAILED for $patch_rel — manual inspection needed"
    fi
done

# ---- A3. Apply L5 reflection injection ----
log_info "A3. Apply L5 reflection injection via apply_aosp_java_patches.py"
if [ -f "$SCRIPT_DIR/apply_aosp_java_patches.py" ]; then
    run "AOSP_ROOT=\"$AOSP_ROOT\" python3 \"$SCRIPT_DIR/apply_aosp_java_patches.py\" --aosp-root=\"$AOSP_ROOT\""
    log_ok "A3 done"
else
    log_warn "A3 skipped — apply_aosp_java_patches.py not found"
fi

# ---- A3b. Install AppSpawnXInit.java into AOSP oh_adapter_framework (gap 7) ----
# The AOSP-side oh_adapter_framework/java/ tree doesn't live under our
# aosp_patches/ (it's a custom device/adapter/ dir, not a patch of existing
# AOSP sources). This script copies the authoritative AppSpawnXInit.java from
# framework/appspawn-x/ into the AOSP source tree on every restore.
log_info "A3b. Install AppSpawnXInit.java into AOSP oh_adapter_framework (gap 7)"
if [ -f "$SCRIPT_DIR/aosp_build_patches/install_app_spawn_x_init.sh" ]; then
    run "ADAPTER_ROOT=\"$ADAPTER_ROOT\" AOSP_ROOT=\"$AOSP_ROOT\" bash \"$SCRIPT_DIR/aosp_build_patches/install_app_spawn_x_init.sh\""
    log_ok "A3b done"
else
    log_warn "A3b skipped — install_app_spawn_x_init.sh not found"
fi

# ---- A3c. Sync local PackageManagerAdapter.java to AOSP oh_adapter_framework ----
# Mirrors framework/package-manager/java/PackageManagerAdapter.java into the
# AOSP-side compile target so the auto-generated stubs survive a repo sync.
log_info "A3c. Sync PackageManagerAdapter.java to AOSP oh_adapter_framework"
PMA_SRC="$ADAPTER_ROOT/framework/package-manager/java/PackageManagerAdapter.java"
PMA_DST="$AOSP_ROOT/device/adapter/oh_adapter_framework/java/adapter/packagemanager/PackageManagerAdapter.java"
if [ -f "$PMA_SRC" ] && [ -d "$(dirname "$PMA_DST")" ]; then
    run "cp \"$PMA_SRC\" \"$PMA_DST\""
    log_ok "A3c done"
else
    log_warn "A3c skipped — source or destination missing"
fi

# ---- A3c2. Cross-compile ARM32 core AOSP native stack (21 .so) ----
# Produces the libart/libbase/liblog/libdexfile/libziparchive/libvixl/libartbase/
# libartpalette-system/libunwindstack/libsigchain/libelffile/libnativehelper/
# libnativeloader/libnativebridge/libtinyxml2/libutils/libcutils/libprofile/
# liblz4/libart_runtime_stubs/libbionic_compat stack required by appspawn-x.
# Sedimented 2026-04-14: was previously invoked manually; after a clean repo
# sync this step was missing, leaving out/aosp_lib/ partially populated.
# The script itself hard-fails on any compile error and runs a smoke test
# verifying critical exports (OpenArchive/JNI_CreateJavaVM/CodeBuffer/etc).
log_info "A3c2. Cross-compile ARM32 core AOSP native stack to out/aosp_lib/"
if [ -f "$ADAPTER_ROOT/out/aosp_lib/libart.so" ] && [ -f "$ADAPTER_ROOT/out/aosp_lib/libbase.so" ] && [ -f "$ADAPTER_ROOT/out/aosp_lib/libziparchive.so" ]; then
    log_ok "A3c2 skipped — libart.so + libbase.so + libziparchive.so already present"
elif [ -f "$SCRIPT_DIR/cross_compile_arm32.sh" ]; then
    run "bash "$SCRIPT_DIR/cross_compile_arm32.sh""
    log_ok "A3c2 done"
else
    log_warn "A3c2 skipped — cross_compile_arm32.sh not found"
fi

# ---- A3d. Fetch minikin/harfbuzz_ng/freetype source trees (gap P10.C.full) ----
# The ECS AOSP is a partial repo sync; these 3 projects are filtered by
# groups="pdk*" in the master manifest and not present. fetch_minikin_deps.sh
# bypasses the permission-broken repo tool and git-clones directly from the
# Tsinghua AOSP mirror.
log_info "A3d. Fetch minikin/harfbuzz_ng/freetype AOSP source trees"
if [ -f "$SCRIPT_DIR/aosp_build_patches/fetch_minikin_deps.sh" ]; then
    run "AOSP_ROOT=\"$AOSP_ROOT\" bash \"$SCRIPT_DIR/aosp_build_patches/fetch_minikin_deps.sh\""
    log_ok "A3d done"
else
    log_warn "A3d skipped — fetch_minikin_deps.sh not found"
fi

# ---- A3e. Cross-compile minikin stack (libft2/libicuuc/libicui18n/libharfbuzz_ng/libminikin/libandroidfw) ----
# Runs after A3d. Skips libs that are already present in out/aosp_lib/ (the
# cross_compile_minikin_stack.sh script doesn't have --skip-existing yet, so
# we gate on the presence of libminikin.so — the last lib in the chain).
log_info "A3e. Cross-compile minikin stack to out/aosp_lib/"
if [ -f "$ADAPTER_ROOT/out/aosp_lib/libminikin.so" ] && [ -f "$ADAPTER_ROOT/out/aosp_lib/libandroidfw.so" ]; then
    log_ok "A3e skipped — libminikin.so and libandroidfw.so already present"
elif [ -f "$SCRIPT_DIR/cross_compile_minikin_stack.sh" ]; then
    run "bash \"$SCRIPT_DIR/cross_compile_minikin_stack.sh\""
    log_ok "A3e done"
else
    log_warn "A3e skipped — cross_compile_minikin_stack.sh not found"
fi

# ---- A4: (removed 2026-04-11) per-file hwui patches ----
# The bulk hwui_rk3568.patch (applied in A9) supersedes per-file patches.
# Any standalone .patch files under aosp_patches/libs/hwui/ other than the
# bulk should be considered legacy — deletable once superseded.

# ---- A5. Disable non-essential Android.bp files (handled by apply_patches.sh A2) ----
log_info "A5. Disable non-essential Android.bp files — handled inside apply_patches.sh A2 step"

# ---- A6. CTS/VTS stub files (handled by apply_patches.sh A2) ----
log_info "A6. CTS/VTS stub files — handled inside apply_patches.sh A2 step"

# ---- A7: (removed 2026-04-11) dex2oat_stubs deployment ----
# The custom dex2oat_stubs + build_dex2oat_host.sh + dex2oat_build/ experiment
# was a failed parallel path — never produced a working dex2oat binary. The
# real dex2oat comes from AOSP Soong at $AOSP_ROOT/out/host/linux-x86/bin/dex2oat64
# and gen_boot_image.sh already points at it. The stub tree has been deleted
# from both local and ECS to prevent accidental overwrite of real AOSP sources
# (external/libcap, external/tinyxml2 etc.) when A7 would otherwise cp over them.

# ---- A8. Deploy hwui stub source files ----
# 2026-04-11: the .cpp files were moved out of build/ into their semantic homes:
#   - hwui_oh_abi_patch.cpp / hwui_register_stubs.cpp / typeface_minimal_stub.cpp
#     → aosp_patches/libs/hwui/   (they augment AOSP libhwui at link time)
# 2026-05-08 G2.14ad: android_view_surface_stubs.cpp removed from this list.
# It was deactivated 2026-05-06 (#if 0 entire body) and renamed to
# .cpp.deprecated; its three Parts are now in framework/android-runtime/src/.
# This A8 phase deploys a copy into AOSP libs/hwui/ source tree for scripts
# that cd into the hwui tree and relative-include them.
log_info "A8. Deploy hwui stub .cpp files"
HWUI_TARGET_DIR="$AOSP_ROOT/frameworks/base/libs/hwui"
declare -A HWUI_STUB_SRC=(
    [typeface_minimal_stub.cpp]="$ADAPTER_ROOT/aosp_patches/libs/hwui/typeface_minimal_stub.cpp"
    [hwui_oh_abi_patch.cpp]="$ADAPTER_ROOT/aosp_patches/libs/hwui/hwui_oh_abi_patch.cpp"
    [hwui_register_stubs.cpp]="$ADAPTER_ROOT/aosp_patches/libs/hwui/hwui_register_stubs.cpp"
)
for stub in "${!HWUI_STUB_SRC[@]}"; do
    src="${HWUI_STUB_SRC[$stub]}"
    if [ -f "$src" ]; then
        if [ -d "$HWUI_TARGET_DIR" ]; then
            run "cp -v \"$src\" \"$HWUI_TARGET_DIR/$stub\""
            log_ok "A8: deployed $stub"
        else
            log_warn "A8: target dir $HWUI_TARGET_DIR not found, $stub stays at source location only"
        fi
    fi
done
# hwui_force_include.h lives inside skia_compat_headers/ (the stale top-level
# duplicate was deleted 2026-04-11). Copy real version into AOSP hwui tree.
if [ -f "$SCRIPT_DIR/skia_compat_headers/hwui_force_include.h" ] && [ -d "$HWUI_TARGET_DIR" ]; then
    run "cp -v \"$SCRIPT_DIR/skia_compat_headers/hwui_force_include.h\" \"$HWUI_TARGET_DIR/\""
fi

# ---- A9. Apply hwui rk3568 bulk patch (sedimented 2026-04-11) ----
# Supersedes the 31 Python increment scripts. The bulk diff was produced by
#   cd ~/aosp/frameworks/base && git diff libs/hwui > hwui_rk3568.patch
# after all Python scripts had been run successfully. See
# doc/technical_decision_overview.html §6 P2.
if [ "$DO_HWUI_PY" = "1" ]; then
    log_info "A9. Apply hwui bulk patch (hwui_rk3568.patch)"
    HWUI_BULK_PATCH="$ADAPTER_ROOT/aosp_patches/libs/hwui/hwui_rk3568.patch"
    if [ -f "$HWUI_BULK_PATCH" ]; then
        cd "$AOSP_ROOT/frameworks/base"
        if git apply --check "$HWUI_BULK_PATCH" 2>/dev/null; then
            run "git apply \"$HWUI_BULK_PATCH\""
            log_ok "A9: hwui bulk patch applied ($(wc -l < $HWUI_BULK_PATCH) lines, 50 files)"
        elif git apply --check --reverse "$HWUI_BULK_PATCH" 2>/dev/null; then
            log_ok "A9: hwui bulk patch already applied (reverse-check passed)"
        else
            log_warn "A9: hwui bulk patch neither applies cleanly nor is already applied — source drift detected. Check manually."
        fi
        cd "$ADAPTER_ROOT"
    else
        log_warn "A9 skipped — $HWUI_BULK_PATCH not found"
    fi

    # Ensure the 2 empty api/test-*.txt files exist (created historically but not
    # sedimented into the bulk patch since they are untracked new files)
    mkdir -p "$AOSP_ROOT/frameworks/base/libs/hwui/api"
    : > "$AOSP_ROOT/frameworks/base/libs/hwui/api/test-current.txt"
    : > "$AOSP_ROOT/frameworks/base/libs/hwui/api/test-removed.txt"
    log_info "A9: ensured empty api/test-{current,removed}.txt"

    # ---- A9b. Apply Blocker A.4 vulkanManager() call-site stub (2026-04-22) ----
    # Wraps 3 vulkanManager() call sites in RenderThread.cpp + DeferredLayerUpdater.cpp
    # with #ifdef HWUI_NO_VULKAN. Separate from the bulk patch because it is:
    #   (a) idempotent Python (safe to re-run after repo sync)
    #   (b) small focused change; easier to review / revert if Vulkan support
    #       is later restored.
    # See doc/build_patch_log.html appendix AA.8 for the diagnostic chain.
    VMGR_PATCH="$ADAPTER_ROOT/aosp_patches/libs/hwui/apply_hwui_vulkan_manager_stub.py"
    if [ -f "$VMGR_PATCH" ]; then
        run "AOSP_ROOT=$AOSP_ROOT python3 $VMGR_PATCH"
        log_ok "A9b: vulkanManager() call-site stub applied"
    else
        log_warn "A9b skipped — $VMGR_PATCH not found"
    fi

    # ---- A9c. Restore android_nio_utils.cpp (Blocker A.6, 2026-04-22) ----
    # An earlier agent wrapped the whole file in `#if 0 // OH adapter nio_utils
    # stub`, disabling AutoBufferPointer's real implementation. libhwui.so
    # dlopen reported UND _ZN7android17AutoBufferPointerC1EP7_JNIEnvP8_jobjecth.
    # Idempotent Python — safe after repo sync.
    # Paired shim change `build/skia_compat_headers/nativehelper/JNIPlatformHelp.h`
    # (adapter-side, ECS-authoritative) delegates to AOSP canonical header —
    # no separate restore step needed since it's in build/ not aosp tree.
    NIO_PATCH="$ADAPTER_ROOT/aosp_patches/libs/hwui/apply_hwui_nio_utils_unstub.py"
    if [ -f "$NIO_PATCH" ]; then
        run "AOSP_ROOT=$AOSP_ROOT python3 $NIO_PATCH"
        log_ok "A9c: android_nio_utils.cpp #if 0 wrapper removed"
    else
        log_warn "A9c skipped — $NIO_PATCH not found"
    fi

    # ---- A9d-A9f. Sedimented into compile_libhwui.sh phase 0 (G2.14bf 2026-05-12) ----
    # Historical A9d (G2.14au r4) / A9e (G2.14ay + G2.14bd) / A9f (G2.14be) Python
    # patch applications are now phase 0 of the unified compile_libhwui.sh
    # pipeline (along with the bulk hwui_rk3568.patch handled by A9 above).
    #
    # Rationale: previously these patches were applied at restore time but the
    # compile_libhwui.sh script did NOT verify or re-apply them. After repo sync
    # they got reapplied here, but anyone running compile_libhwui.sh directly
    # (without restore_after_sync.sh) got an inconsistent build state.
    #
    # Unified design: compile_libhwui.sh phase 0 does all patches idempotently
    # with explicit SKIP/APPLY logging per marker grep, so running it standalone
    # always produces the same correct state. See compile_libhwui.sh header
    # warning [C-6] and [C-9] for the full sedimentation rationale.
    log_info "A9d-A9f: hwui patches handled by compile_libhwui.sh phase 0 (not run here — call compile_libhwui.sh to build)"
else
    log_warn "A9 skipped via --skip-hwui-py"
fi


# ---- A10. Apply aosp_patches/art/**/*.patch (native libart cross-compile fixes) ----
# Each patch mirrors the AOSP source tree under aosp_patches/art/. Currently:
#   aosp_patches/art/runtime/gc/collector/mark_compact.cc.patch
#     Adds local struct uffdio_continue (Linux 5.7 UAPI) that OH kernel
#     headers are missing. Required for libart.so cross-compile.
log_info "A10. Apply aosp_patches/art/**/*.patch (native cross-compile fixes)"
ART_PATCH_ROOT="$ADAPTER_ROOT/aosp_patches/art"
if [ -d "$ART_PATCH_ROOT" ]; then
    A10_APPLIED=0
    while IFS= read -r patch; do
        [ -z "$patch" ] && continue
        cd "$AOSP_ROOT/art"
        if git apply --check "$patch" 2>/dev/null; then
            run "git apply "$patch""
            A10_APPLIED=$((A10_APPLIED + 1))
        elif git apply --check --reverse "$patch" 2>/dev/null; then
            log_info "A10: already applied: $(basename $patch)"
        else
            log_warn "A10: source drift on $(basename $patch) — check manually"
        fi
        cd "$ADAPTER_ROOT"
    done < <(find "$ART_PATCH_ROOT" -name "*.patch" | sort)
    log_ok "A10: applied $A10_APPLIED new art patches"
else
    log_info "A10 skipped — $ART_PATCH_ROOT not present"
fi

fi  # DO_AOSP

# ============================================================================
# Phase B — OH source restoration
# ============================================================================

if [ "$DO_OH" = "1" ]; then

log_phase "Phase B — OH source restoration"

# ---- B1+B2+B3. apply_oh_patches.sh handles build patches, product config,
#                and most functional patches via OH_BUILD_PATCHES /
#                OH_FULL_FILE_REPLACEMENTS / OH_DIFF_PATCHES in config.sh ----
log_info "B1+B2+B3. Run apply_oh_patches.sh (build + functional patches + product config)"
if [ -f "$SCRIPT_DIR/apply_oh_patches.sh" ]; then
    if [ -f "$OH_ROOT/.adapter_patches_applied" ]; then
        log_info "B1+B2+B3: marker exists — patches already applied, skipping"
        log_info "          Delete $OH_ROOT/.adapter_patches_applied to force re-apply (e.g. after repo sync)"
    else
        run "OH_ROOT=\"$OH_ROOT\" bash \"$SCRIPT_DIR/apply_oh_patches.sh\" --type=all"
        log_ok "B1+B2+B3 done"
    fi
else
    log_warn "apply_oh_patches.sh not found — falling back to manual patch loop"
fi

# ---- B4: (deleted 2026-04-11) musl syscall.h.in aarch64 time64 aliases ----
# The original patch targeted arch/aarch64/bits/syscall.h.in but rk3568 is
# arm32 and uses arch/arm/bits/. Patch was aarch64-only dayu210 baggage.

# ---- B5. third_party/musl/src/internal/ADLTSection.h patch (DISABLED) ----
# Arch-independent bitfield type fix. Pre-existing out/rk3568/ .so artifacts
# were produced WITHOUT this patch applied, so it's not blocking the current
# build. Re-enable if first full rk3568 build produces "Elf64_Off bit-field"
# compiler errors.
log_info "B5 skipped (disabled pending rk3568 full-build verification)"

# ---- B6. graphic_2d rs_buffer_reclaim.cpp (%u → %zu format fix) ----
log_info "B6. Apply graphic_2d rs_buffer_reclaim.cpp.patch (size_t format)"
G2D_PATCH="$ADAPTER_ROOT/ohos_patches/graphic_2d/rosen/modules/render_service_base/src/feature/buffer_reclaim/rs_buffer_reclaim.cpp.patch"
G2D_COMPONENT="$OH_ROOT/foundation/graphic/graphic_2d"
if [ -f "$G2D_PATCH" ] && [ -d "$G2D_COMPONENT/.git" ]; then
    cd "$G2D_COMPONENT"
    if git apply --check "$G2D_PATCH" 2>/dev/null; then
        run "git apply \"$G2D_PATCH\""
        log_ok "B6: applied"
    elif git apply --check --reverse "$G2D_PATCH" 2>/dev/null; then
        log_ok "B6: already applied (reverse-check passed)"
    else
        log_warn "B6: neither applies cleanly nor already applied — source drift"
    fi
    cd "$ADAPTER_ROOT"
else
    log_warn "B6 skipped — patch or component git repo not found"
fi

# ---- B6b. G2.14au r5 (2026-05-11) — OH RS RT call site probes ----
# In-place injects 3 RS_LOGI trace probes into OH render_service to track
# helloworld surface buffer flow:
#   1. RSSurfaceRenderNode::UpdateBufferInfo   (librender_service_base.z.so)
#   2. RSUniRenderVisitor::QuickPrepareSurfaceRenderNode (librender_service.z.so)
#   3. RSUniRenderProcessor::CreateLayer       (librender_service.z.so)
# All probes filter by GetName().find(helloworld) to avoid log flood.
# Idempotent (marker-based skip).
# See doc/build_patch_log.html G2.14au r5 entry for findings.
log_info B6b. Apply G2.14au r5 OH RS RT call site probes
G214AU_R5_PATCH=/ohos_patches/graphic_2d/apply_G214au_r5_oh_rs_probes.py
if [ -f  ]; then
    run OH_ROOT= python3 
    log_ok B6b: G2.14au r5 OH RS probes injected
else
    log_warn B6b skipped — not found
fi

# ---- B7. bundle_mgr_host.cpp (%llu → %lu format fix) (DISABLED) ----
# Same reasoning as B5: not part of the current successful build state.
# Additionally, the existing .patch file has non-standard prose headers
# that confuse `git apply`. If first rk3568 full-build hits the relevant
# -Werror,-Wformat, re-author as a clean git diff and re-enable here.
log_info "B7 skipped (disabled pending rk3568 full-build verification)"

# ---- B8. bundle_framework BMS BUILD.gn — gap 6 enabler ----
# Adds the adapter_apk_install_minimal.cpp + apk_manifest_parser.cpp sources,
# the OH_ADAPTER_ANDROID define, the libandroidfw ldflags, and 10 -I cflags
# pointing at AOSP headers needed by the Android AXML parser.
#
# Replaced fragile unified-diff (line-number sensitive, broke whenever OH
# upstream BUILD.gn shifted lines) with a Python semantic patcher that:
#   - finds libbms target by name
#   - inserts each modification only if not already present (idempotent)
#   - uses $AOSP_ROOT for header paths (portable across ECS / dev machines)
log_info "B8. Apply BUILD.gn semantic patch (apply_BUILD_gn.py, gap 6)"
BMS_PATCHER="$ADAPTER_ROOT/ohos_patches/bundle_framework/services/bundlemgr/apply_BUILD_gn.py"
BMS_BUILD_TARGET="$OH_ROOT/foundation/bundlemanager/bundle_framework/services/bundlemgr/BUILD.gn"
if [ -f "$BMS_PATCHER" ] && [ -f "$BMS_BUILD_TARGET" ]; then
    run "python3 \"$BMS_PATCHER\" --bms-build-gn \"$BMS_BUILD_TARGET\" --aosp-root \"$AOSP_ROOT\""
    log_ok "B8: BMS BUILD.gn semantic patch applied"
else
    log_warn "B8 skipped — patcher or target not found"
fi

# ---- B9. ets2abc_config.gni — value collision fix (gap 6 enabler) ----
# OH adapter previously zeroed out ohos_ets_kits_deps / ohos_ets_api_deps /
# ohos_ets_arkts_deps / ohos_ets_ui_plugins_deps in ets2abc_config.gni, which
# collides with the same identifiers in build/test.gni when both files are
# imported into the same scope (gn "Value collision" error). This is required
# for `gn gen` to succeed at all — without it, no ninja files get regenerated
# and gap 6 cannot proceed.
log_info "B9. Apply ets2abc_config.gni collision fix"
ETS2ABC_PATCH="$ADAPTER_ROOT/ohos_patches/build/config/components/ets_frontend/ets2abc_config.gni.collision_fix.patch"
ETS2ABC_TARGET="$OH_ROOT/build/config/components/ets_frontend/ets2abc_config.gni"
if [ -f "$ETS2ABC_PATCH" ] && [ -f "$ETS2ABC_TARGET" ]; then
    if [ ! -f "${ETS2ABC_TARGET}.before_collision_fix" ]; then
        run "cp \"$ETS2ABC_TARGET\" \"${ETS2ABC_TARGET}.before_collision_fix\""
    fi
    if run "patch --dry-run -p1 -d \"$OH_ROOT\" < \"$ETS2ABC_PATCH\" >/dev/null 2>&1"; then
        run "patch -p1 -d \"$OH_ROOT\" < \"$ETS2ABC_PATCH\""
        log_ok "B9: ets2abc_config.gni patched (collision fixed)"
    else
        log_warn "B9: already applied or context mismatch"
    fi
else
    log_warn "B9 skipped — patch or target not found"
fi

# ---- B10. Deploy adapter_apk_install_minimal.cpp + apk_manifest_parser.{h,cpp} ----
# These files MUST live inside oh/foundation/.../bundlemgr/src/ at gn compile
# time because gn forbids cross-component header includes. The canonical
# sources live at:
#   - $ADAPTER_ROOT/framework/package-manager/jni/apk_manifest_parser.{h,cpp}
#   - $ADAPTER_ROOT/ohos_patches/bundle_framework/services/bundlemgr/src/adapter_apk_install_minimal.cpp
# We `cp` them into the OH source tree on every restore.
log_info "B10. Deploy apk_manifest_parser + adapter_apk_install_minimal into bundlemgr/src/"
BMS_SRC_DIR="$OH_ROOT/foundation/bundlemanager/bundle_framework/services/bundlemgr/src"
APK_PARSER_H_SRC="$ADAPTER_ROOT/framework/package-manager/jni/apk_manifest_parser.h"
APK_PARSER_CPP_SRC="$ADAPTER_ROOT/framework/package-manager/jni/apk_manifest_parser.cpp"
APK_INSTALL_MIN_SRC="$ADAPTER_ROOT/ohos_patches/bundle_framework/services/bundlemgr/src/adapter_apk_install_minimal.cpp"
if [ -d "$BMS_SRC_DIR" ]; then
    if [ -f "$APK_PARSER_H_SRC" ] && [ -f "$APK_PARSER_CPP_SRC" ] && [ -f "$APK_INSTALL_MIN_SRC" ]; then
        run "cp \"$APK_PARSER_H_SRC\" \"$BMS_SRC_DIR/apk_manifest_parser.h\""
        run "cp \"$APK_PARSER_CPP_SRC\" \"$BMS_SRC_DIR/apk_manifest_parser.cpp\""
        run "cp \"$APK_INSTALL_MIN_SRC\" \"$BMS_SRC_DIR/adapter_apk_install_minimal.cpp\""
        log_ok "B10: 3 files deployed into $BMS_SRC_DIR"
    else
        log_warn "B10 skipped — one or more canonical sources missing:"
        [ ! -f "$APK_PARSER_H_SRC" ] && log_warn "    $APK_PARSER_H_SRC"
        [ ! -f "$APK_PARSER_CPP_SRC" ] && log_warn "    $APK_PARSER_CPP_SRC"
        [ ! -f "$APK_INSTALL_MIN_SRC" ] && log_warn "    $APK_INSTALL_MIN_SRC"
    fi
else
    log_warn "B10 skipped — $BMS_SRC_DIR not found"
fi

# ---- B11. Apply .apk install dispatch + register chain (layered, idempotent) ----
# Three patches, in order:
#   B11.1 apply_bms_apk_dispatch_processinstall.py — layer 1: .apk suffix
#         detection in BaseBundleInstaller::ProcessBundleInstall, dispatches
#         to libapk_installer.so (skip standard HAP sig / parse pipeline).
#   B11.2 apply_bms_apk_register_with_manifest.py  — layer 2 (v3 2026-05-01,
#         broadcast-fields fix 2026-05-09):
#         builds minimum-viable InnerBundleInfo + registers via BundleDataMgr,
#         routes all fs writes (CreateBundleDir/Mkdir/CopyFile) via
#         InstalldClient IPC to installs domain, synthesizes resources HAP via
#         ExtractFiles(APK_RESOURCES_HAP), CRUCIALLY calls SaveInnerBundleInfo
#         (otherwise bundle vanishes on reboot — R.3.8).
#         v3 (G2.14h, 2026-05-01) — calls AccessTokenKit::AllocHapToken before
#         AddInnerBundleInfo, writes the returned tokenID to all three places:
#           appInfo.accessTokenId / accessTokenIdEx (top-level applicationInfo),
#           userInfo.accessTokenId / accessTokenIdEx (per-userId InnerBundleUserInfo
#             for both userId 0 and 100),
#           innerBundleInfo.SetAccessTokenIdEx(allocTokenIdEx, uid_user)
#         (official setter). Without v3, AppMS::StartProcess sends accessTokenIdEx=0
#         in TLV → child SetSelfTokenID skipped → AMS::AttachAbilityThread
#         JudgeSelfCalled rejects with rc=2097177 CHECK_PERMISSION_FAILED.
#         A+ broadcast-fields fix (2026-05-09) — sets installRes.bundleType =
#         BundleType::APP_ANDROID and installRes.accessTokenId = real value in
#         the COMMON_EVENT_PACKAGE_ADDED broadcast, otherwise launcher consumes
#         hardcoded zeros from the event JSON and silent-skips view refresh
#         (icon only appears after reboot's full re-scan).
#   B11.3 apply_installd_apk_resources_hap.py      — layer 3: adds
#         ExtractFileType::APK_RESOURCES_HAP enum + installd dispatcher that
#         dlopens libapk_installer.so and calls oh_adapter_build_resources_hap
#         (must run in installs domain for SELinux write perm on
#         data_app_el1_file).
# Skip layer 2 if adapter_apk_install_minimal.cpp's fallback is in use.
log_info "B11. Apply .apk install dispatch + register + installd-side resources HAP chain"
BMS_SRC_CPP="$OH_ROOT/foundation/bundlemanager/bundle_framework/services/bundlemgr/src/base_bundle_installer.cpp"

for patch_script in \
    apply_bms_apk_dispatch_processinstall.py \
    apply_bms_apk_register_with_manifest.py \
    apply_installd_apk_resources_hap.py
do
    PATCH_PATH="$ADAPTER_ROOT/ohos_patches/bundle_framework/services/bundlemgr/$patch_script"
    if [ -f "$PATCH_PATH" ]; then
        if [ "$patch_script" = "apply_installd_apk_resources_hap.py" ]; then
            # No --src arg; script uses hard-coded default OH tree root (overridable via --oh-root)
            run "python3 \"$PATCH_PATH\" --oh-root \"$OH_ROOT\""
        elif [ -f "$BMS_SRC_CPP" ]; then
            run "python3 \"$PATCH_PATH\" --src \"$BMS_SRC_CPP\""
        else
            log_warn "B11.$patch_script skipped — target $BMS_SRC_CPP not found"
            continue
        fi
        log_ok "B11: $patch_script applied"
    else
        log_warn "B11.$patch_script skipped — $PATCH_PATH not found"
    fi
done

# ---- B11.4. Apply AppMS token-fallback patch (G2.14h #2 of 3) ----
# 2026-05-09: previously orphaned (patch existed since 2026-05-01 but never
# wired into restore chain → repo sync would erase it; libappms.z.so 4/20
# version on device lacked the fix for ~30 days, blocking helloworld
# AttachAbilityThread → MoveToForeground → onResume).
#
# Why this patch is needed:
#   OH AppMS::StartProcess line 4815 unconditionally OVERRIDES
#   bundleInfo.applicationInfo.accessTokenId/Ex with AccessTokenKit::
#   GetHapTokenIDEx() return. For OH-native HAPs that's the source of truth.
#   For adapter-installed APKs (BMS v3 path with AllocHapToken), GetHapTokenIDEx
#   returns 0 → AppMS sends accessTokenIdEx=0 in TLV → child appspawn-x
#   skips SetSelfTokenID → child inherits parent's appspawn-x token →
#   AMS::AttachAbilityThread JudgeSelfCalled FAIL ([ABMS12737] not self) →
#   ProcessForegroundAbility never starts → Activity.onResume never fires →
#   LIFECYCLE_HALF_TIMEOUT → AMS killApp.
#
# The patch: when GetHapTokenIDEx returns 0, PRESERVE the bundleInfo's
# accessTokenId (already set by BMS v3 AllocHapToken) instead of overwriting
# with 0. Lets child get a real token, AttachAbilityThread succeeds, the full
# AppMS::MoveToForeground → ScheduleAbilityTransaction(FOREGROUND_NEW=5) →
# Activity.onResume chain runs.
#
# Source: ohos_patches/ability_rt/services/appmgr/src/apply_appms_token_fallback.py
# Target: foundation/ability/ability_runtime/services/appmgr/src/app_mgr_service_inner.cpp
log_info "B11.4. Apply AppMS token-fallback patch (G2.14h)"
APPMS_PATCH="$ADAPTER_ROOT/ohos_patches/ability_rt/services/appmgr/src/apply_appms_token_fallback.py"
APPMS_SRC_CPP="$OH_ROOT/foundation/ability/ability_runtime/services/appmgr/src/app_mgr_service_inner.cpp"
if [ -f "$APPMS_PATCH" ]; then
    if [ -f "$APPMS_SRC_CPP" ]; then
        run "python3 \"$APPMS_PATCH\" --src \"$APPMS_SRC_CPP\""
        log_ok "B11.4: apply_appms_token_fallback.py applied"
    else
        log_warn "B11.4 skipped — target $APPMS_SRC_CPP not found"
    fi
else
    log_warn "B11.4 skipped — $APPMS_PATCH not found"
fi

# ---- B12. Complete-file OH source replacements (2026-04-22) ----
# Snapshot approach (instead of apply_*.py string replacement):
# authoritative modified copies live under ohos_patches/<mirror-path>/ and
# get cp'd over OH source every restore. Simpler to review + no anchor
# fragility. Entries below are mirrored to the same relative path under OH.
#
# Rationale (feedback_sedimentation_both_sides.md): after an apply_*.py runs
# once, the modified OH source is the source of truth. Keeping only the
# script means repo loses the *result* of the modification — a full OH
# source drift can silently break the anchor match. Snapshot makes the
# modification reviewable as plain diff.
log_info "B12. Complete-file OH source replacements (cp from ohos_patches mirror)"
declare -a FULL_FILE_PATCHES=(
    # adapter relative path under ohos_patches/  (same as OH source relative path)
    "base/startup/appspawn/interfaces/innerkits/include/appspawn.h"
    "base/startup/appspawn/modules/module_engine/include/appspawn_msg.h"
    "base/startup/appspawn/interfaces/innerkits/client/appspawn_client.h"
    "base/startup/appspawn/interfaces/innerkits/client/appspawn_client.c"
    "foundation/ability/ability_runtime/services/appmgr/src/app_spawn_client.cpp"
    "foundation/bundlemanager/bundle_framework/common/BUILD.gn"
    "foundation/bundlemanager/bundle_framework/common/utils/src/bundle_file_util.cpp"
    "foundation/bundlemanager/bundle_framework/services/bundlemgr/BUILD.gn"
    "foundation/bundlemanager/bundle_framework/services/bundlemgr/include/ipc/extract_param.h"
    "foundation/bundlemanager/bundle_framework/services/bundlemgr/include/installd/installd_operator.h"
    "foundation/bundlemanager/bundle_framework/services/bundlemgr/src/base_bundle_installer.cpp"
    "foundation/bundlemanager/bundle_framework/services/bundlemgr/src/bundle_data_mgr.cpp"
    "foundation/bundlemanager/bundle_framework/services/bundlemgr/src/bundle_installer.cpp"
    "foundation/bundlemanager/bundle_framework/services/bundlemgr/src/bundle_stream_installer_host_impl.cpp"
    # 2026-05-09: bundle_util.cpp 含 apply_bms_apk_gates patch 的 .apk 后缀放行
    # 修改（"allow .apk extension"，line 103）。该 patch 历史上被应用到 OH 源码但
    # snapshot 缺失 — repo sync 必丢，导致 BMS suffix gate 拒 .apk 文件 → APK
    # 安装链断。沉淀为 B12 snapshot 与其他 4 个 BMS dispatch patch (file_util /
    # dispatch_installapp / dispatch_vector / iconpath_fallback) 一致管理。
    "foundation/bundlemanager/bundle_framework/services/bundlemgr/src/bundle_util.cpp"
    # 2026-05-09 audit 沉淀：以下 4 个 snapshot 历史上是孤儿（patch marker 在 OH
    # 源码 + ohos_patches 镜像有但未沉淀到 B12），repo sync 必丢。
    # mission*: ohos_patches/ability_rt/... 历史路径错位，已 mv 到正确 foundation/...
    # 路径与 OH 源码对齐。init_service.h 含 adapter project marker（appspawn-x init
    # service header 修改）。
    "foundation/ability/ability_runtime/services/abilitymgr/include/mission/mission.h"
    "foundation/ability/ability_runtime/services/abilitymgr/src/mission/mission.cpp"
    "foundation/ability/ability_runtime/services/abilitymgr/src/mission/mission_list_manager_patch.cpp"
    "base/startup/init/services/init/include/init_service.h"
    "foundation/bundlemanager/bundle_framework/services/bundlemgr/src/installd/installd_operator.cpp"
    "third_party/musl/config/ld-musl-namespace-arm.ini"
    "vendor/hihope/rk3568/config.json"
)
for rel in "${FULL_FILE_PATCHES[@]}"; do
    SRC="$ADAPTER_ROOT/ohos_patches/$rel"
    DST="$OH_ROOT/$rel"
    if [ -f "$SRC" ] && [ -d "$(dirname "$DST")" ]; then
        if [ ! -f "${DST}.adapter_orig" ] && [ -f "$DST" ]; then
            run "cp \"$DST\" \"${DST}.adapter_orig\""
        fi
        run "cp \"$SRC\" \"$DST\""
        log_ok "B12: $rel"
    else
        log_warn "B12 skipped ($rel) — src=$SRC dst_parent=$(dirname "$DST")"
    fi
done

# ---- B13. Unified-diff patches against OH source ----
# For small adapter changes we prefer unified diff (*.patch) over full-file
# snapshots (B12), because diffs survive OH source drift and document intent.
# Patch file's --- / +++ labels carry the OH source relative path; apply with
# patch -p0 from OH_ROOT. --forward + silent makes it idempotent (re-run OK).
log_info "B13. Apply adapter unified-diff patches"
declare -a PATCH_FILES=(
    "selinux_adapter/file_contexts.patch"
)
for patch_rel in "${PATCH_FILES[@]}"; do
    PATCH_FILE="$ADAPTER_ROOT/ohos_patches/$patch_rel"
    if [ ! -f "$PATCH_FILE" ]; then
        log_warn "B13 skipped: $patch_rel not found"
        continue
    fi
    # Back up target file (best-effort; the patch itself tells us the target via --- label)
    target_rel=$(grep -m1 '^---' "$PATCH_FILE" | awk '{print $2}')
    if [ -n "$target_rel" ] && [ -f "$OH_ROOT/$target_rel" ] && [ ! -f "$OH_ROOT/${target_rel}.adapter_orig" ]; then
        run "cp \"$OH_ROOT/$target_rel\" \"$OH_ROOT/${target_rel}.adapter_orig\""
    fi
    # Apply with --forward (skip if already applied) + --silent
    (cd "$OH_ROOT" && patch -p0 --forward --silent < "$PATCH_FILE") 2>&1 | grep -vE 'already applied|^$' || true
    log_ok "B13: $patch_rel"
done

fi  # DO_OH

# ============================================================================
# Phase C — Cross-cutting fixes
# ============================================================================

log_phase "Phase C — Cross-cutting fixes"

# ---- C1. skia_compat_headers/ already lives at $SCRIPT_DIR/skia_compat_headers ----
log_info "C1. skia_compat_headers/ — staying at $SCRIPT_DIR/skia_compat_headers (referenced by hwui compile scripts via -I)"
if [ ! -d "$SCRIPT_DIR/skia_compat_headers" ]; then
    log_warn "C1: skia_compat_headers/ not found — hwui compile will fail"
fi

# ---- C1.1 (2026-05-02 G2.14p): assert NO stub minikin/ directory ----
# Real libminikin.so is deployed at /system/android/lib/libminikin.so and
# headers at $AOSP/frameworks/minikin/include/. The 18-file stub minikin/
# subtree under skia_compat_headers/ was a historical placeholder that
# silently shadowed real minikin via INC-order accidents — caused multiple
# vtable + class-layout crashes (G2.14n: SIGILL @ populateSkFont).
# Removing it eliminates an entire class of "stub leaks into real-API code"
# bugs. The 4 hwui compile scripts (compile_libhwui*.sh, compile_hwui_*.sh)
# now use real $AOSP/frameworks/minikin/include directly.
if [ -d "$SCRIPT_DIR/skia_compat_headers/minikin" ]; then
    log_warn "C1.1: stub skia_compat_headers/minikin/ exists — should have been deleted (G2.14p)"
    log_info "C1.1: see memory project_g214p_systemic_cleanup for context"
fi

# C2 removed 2026-04-11: this project does not need libarkruntime.so or
# libani_helpers.z.so at runtime. The ninja phony patches in
# build/ninja_patches/patch_{irtoc,arkruntime,ani_helpers}.sh make the
# build graph complete without actually producing these .so files.

# ---- C3. Build liboh_skia_rtti_shim.so (2026-04-12) ----
# This small shim (~12 KB) provides _ZTI*/_ZTS* typeinfo symbols for the 9
# Skia base classes that libhwui inherits from. Replaces the previous
# approach of rebuilding all of Skia with -frtti + SK_API_TYPE. The shim
# is a hard dependency of link_libhwui.sh; the link script exits with a
# clear error message if the .so isn't present, so this step must run
# before any libhwui link attempt.
#
# Idempotent: the compile script always recompiles, but total cost is
# < 1 second (one .cpp + one ld.lld invocation). If we need to skip-if-
# fresh, add an mtime check here.
#
# Sources that must be present after a Local→ECS sync:
#   framework/surface/jni/skia_rtti_shim/skia_rtti_shim.cpp
#   framework/surface/jni/skia_rtti_shim/skia_class_list.inc
#   framework/surface/jni/skia_rtti_shim/skia_rtti_shim.ver
#   build/compile_skia_rtti_shim.sh
log_info "C3. Build liboh_skia_rtti_shim.so (Skia typeinfo shim)"
SKIA_RTTI_SHIM_SRC="$ADAPTER_ROOT/framework/surface/jni/skia_rtti_shim"
SKIA_RTTI_BUILD="$SCRIPT_DIR/compile_skia_rtti_shim.sh"
SKIA_RTTI_OUT="$ADAPTER_ROOT/out/skia-rtti-shim/liboh_skia_rtti_shim.so"
if [ ! -d "$SKIA_RTTI_SHIM_SRC" ]; then
    log_warn "C3 skipped — shim source dir missing: $SKIA_RTTI_SHIM_SRC"
elif [ ! -f "$SKIA_RTTI_BUILD" ]; then
    log_warn "C3 skipped — build script missing: $SKIA_RTTI_BUILD"
else
    for f in skia_rtti_shim.cpp skia_class_list.inc skia_rtti_shim.ver; do
        if [ ! -f "$SKIA_RTTI_SHIM_SRC/$f" ]; then
            log_warn "C3: missing $SKIA_RTTI_SHIM_SRC/$f — shim build may fail"
        fi
    done
    run "bash \"$SKIA_RTTI_BUILD\""
    if [ -f "$SKIA_RTTI_OUT" ]; then
        SHIM_SIZE=$(stat -c%s "$SKIA_RTTI_OUT" 2>/dev/null)
        log_ok "C3 done: $SKIA_RTTI_OUT ($SHIM_SIZE bytes)"
    else
        log_error "C3 FAIL: $SKIA_RTTI_OUT not produced — check compile_skia_rtti_shim.sh output"
        exit 1
    fi
fi

# ---- C4. Verify 2026-04-17 Phase 2/3 additions (libcxx array fix, AHB shim,
#         liboh_android_runtime source tree) -----------------------------------
# These files live entirely under $ADAPTER_ROOT and are NOT affected by
# repo sync; this phase is a sanity check, not a restoration step. If any
# file is missing, either the adapter tree was pruned or a future refactor
# moved it — investigate rather than silently continuing.
log_info "C4. Verify 2026-04-17 Phase 2/3 assets"
C4_MISSING=0
C4_FILES=(
    # Phase 2 — libcxx std::array<T,0>::data() cherry-pick fix
    "$ADAPTER_ROOT/framework/appspawn-x/bionic_compat/include/libcxx_array_aosp/array"
    # Phase 3 — liblog __ANDROID__-gated supplement
    "$ADAPTER_ROOT/framework/appspawn-x/bionic_compat/src/liblog_android_supplement.cpp"
    # Phase 3 — Skia AHB shim (GrAHardwareBufferUtils real impl)
    "$ADAPTER_ROOT/framework/hwui-shim/jni/oh_skia_ahb_shim.cpp"
    # Phase 3 — liboh_android_runtime (progressive libandroid_runtime replacement)
    "$ADAPTER_ROOT/framework/android-runtime/include/AndroidRuntime.h"
    "$ADAPTER_ROOT/framework/android-runtime/src/AndroidRuntime.cpp"
    "$ADAPTER_ROOT/framework/android-runtime/src/android_util_Log.cpp"
    "$ADAPTER_ROOT/framework/android-runtime/src/android_os_SystemProperties.cpp"
    "$ADAPTER_ROOT/build/compile_oh_android_runtime.sh"
)
for f in "${C4_FILES[@]}"; do
    if [ ! -f "$f" ]; then
        log_warn "C4: missing $f"
        C4_MISSING=$((C4_MISSING + 1))
    fi
done
# Verify cross_compile_arm32.sh has the libcxx_array_aosp -isystem flag and
# bionic_compat srcs include liblog_android_supplement.cpp.
if ! grep -q "libcxx_array_aosp" "$ADAPTER_ROOT/build/cross_compile_arm32.sh" 2>/dev/null; then
    log_warn "C4: cross_compile_arm32.sh missing '-isystem libcxx_array_aosp' flag"
    C4_MISSING=$((C4_MISSING + 1))
fi
if ! grep -q "liblog_android_supplement" "$ADAPTER_ROOT/build/cross_compile_arm32.sh" 2>/dev/null; then
    log_warn "C4: cross_compile_arm32.sh missing liblog_android_supplement.cpp in bionic_compat srcs"
    C4_MISSING=$((C4_MISSING + 1))
fi
if ! grep -q "oh_skia_ahb_shim" "$ADAPTER_ROOT/build/compile_hwui_shims.sh" 2>/dev/null; then
    log_warn "C4: compile_hwui_shims.sh missing oh_skia_ahb_shim step"
    C4_MISSING=$((C4_MISSING + 1))
fi
if [ "$C4_MISSING" -eq 0 ]; then
    log_ok "C4 done: all 2026-04-17 Phase 2/3 assets present"
else
    log_warn "C4: $C4_MISSING asset(s) missing — build will fail until restored"
fi

# ============================================================================
# Final state file
# ============================================================================

log_phase "Final"

mkdir -p "$(dirname "$STATE_FILE")"
{
    echo "Adapter restore-after-sync state"
    echo "Date:        $(date -Iseconds)"
    echo "AOSP_ROOT:   $AOSP_ROOT"
    echo "OH_ROOT:     $OH_ROOT"
    echo "OH_PRODUCT:  $OH_PRODUCT"
    echo "DO_AOSP:     $DO_AOSP"
    echo "DO_OH:       $DO_OH"
    echo "DO_HWUI_PY:  $DO_HWUI_PY"
    echo "DRY_RUN:     $DRY_RUN"
    echo "WARNINGS:    $WARNINGS"
} > "$STATE_FILE"

if [ "$WARNINGS" -gt 0 ]; then
    log_warn "Restore completed with $WARNINGS warning(s). Review the output above before building."
    log_warn "State file: $STATE_FILE"
    exit 3
else
    log_ok "Restore completed cleanly. State file: $STATE_FILE"
    log_ok "Next step: run your build pipeline."
    log_ok "Recommended order after restore (2026-04-17):"
    log_ok "  1. bash build/cross_compile_arm32.sh           # libart, libbionic_compat, liblog, etc."
    log_ok "  2. bash build/compile_skia_rtti_shim.sh        # liboh_skia_rtti_shim.so"
    log_ok "  3. bash build/compile_hwui_shims.sh            # liboh_hwui_shim.so (incl. GrAHB shim)"
    log_ok "  4. bash build/compile_oh_android_runtime.sh    # liboh_android_runtime.so (Phase 3+)"
    log_ok "  5. bash build/compile_appspawnx.sh             # appspawn-x"
    log_ok "Reminder: post-GN-gen patches (musl_syscall_fix.sh + ninja_patches/apply_all.sh) must be invoked from the build script AFTER 'gn gen' and BEFORE 'ninja'."
    exit 0
fi

# ============================================================
# Restore ARM32 asm_defines.h from AOSP generated version
# ============================================================
echo '>>> Restoring ARM32 asm_defines.h...'
ASM_SRC="$OH_ROOT/art/runtime/asm_defines.h.generated"
ASM_DST="$ADAPTER_ROOT/framework/appspawn-x/bionic_compat/include/art/asm_defines.h"
if [ -f "$ASM_SRC" ]; then
    cp "$ASM_SRC" "$ASM_DST"
    echo "  Copied from $ASM_SRC (ARM32, POINTER_SIZE=4)"
else
    echo "  WARNING: $ASM_SRC not found, asm_defines.h NOT restored"
fi
