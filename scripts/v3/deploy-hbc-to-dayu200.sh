#!/usr/bin/env bash
# ============================================================================
# deploy-hbc-to-dayu200.sh — Stage 3 push driver for V3 HBC artifacts.
#
# Maps the W1-produced flat layout under westlake-deploy-ohos/v3-hbc/ into
# the device paths required by HBC's DEPLOY_SOP v4 (`scripts/DEPLOY_SOP.md`).
#
# HBC's own `deploy_to_dayu200.sh` expects the ECS build layout
# `$OUT/{adapter,aosp_lib,aosp_fwk,oh-service,boot-image,app}` plus
# `$FW_CFG`. Our pulled tree is category-based:
#   v3-hbc/lib/   <- 54 ARM32 .so (mix of aosp_lib + adapter + oh-service)
#   v3-hbc/jars/  <- 12 framework jars + 1 dex + framework-res.apk
#   v3-hbc/bcp/   <- 27 boot image files (9 segments × 3 ext)
#   v3-hbc/bin/   <- appspawn-x
#   v3-hbc/etc/   <- appspawn_x.cfg + sandbox.json + fonts.xml + icudt72l.dat
#                    + ld-musl-namespace-arm.ini + file_contexts
#   v3-hbc/app/   <- HelloWorld.apk + helloworld_resources.hap
#
# This driver executes Stage 3 push per the SOP (mount /system rw, mkdir
# device dirs, push each substage 3b/3c/3d/3e/3f, chcon labels), exactly as
# `deploy_to_dayu200.sh` would, but resolving sources from v3-hbc/ flat
# layout instead. We do NOT touch source-tree files; we treat v3-hbc/ as
# read-only.
#
# Stage 1 (device-side backup) and Stage 2 (stop foundation/render_service)
# are NOT performed here — the W2 agent runs them manually so the script
# stays restartable on already-staged boards. Stage 3.5 (reboot+verify) and
# Stage 4 (APK launch) are also separate operations.
#
# !! W9-Pattern-2 KNOWN GAP, 2026-05-16 !!
#   This script's `push_file` helper currently calls `hdc file send` directly
#   to /system/... targets, bypassing the mandatory /data/local/tmp/stage/
#   staging area required by V3-DEPLOY-SOP.md §V3 Stage 3 (HBC SOP §3).
#   Until that's reworked, do NOT run this script on a freshly-flashed
#   factory DAYU200 without reading V3-DEPLOY-SOP.md first. The
#   `scripts/v3/run-hbc-regression.sh` suite emits PASS-with-warn when it
#   detects this gap.
#
# Usage:
#   bash deploy-hbc-to-dayu200.sh [--dry-run] [--skip-libskia] [--uninstall]
#
# Env overrides:
#   HDC          path to hdc executable (default /mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe)
#   HDC_SERIAL   target board serial    (default DAYU200 dd011a4…)
#   V3_LOCAL     v3-hbc/ root           (default westlake-deploy-ohos/v3-hbc)
# ============================================================================

set -uo pipefail

HDC="${HDC:-/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe}"
HDC_SERIAL="${HDC_SERIAL:-dd011a414436314130101250040eac00}"
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
V3_LOCAL="${V3_LOCAL:-$REPO_ROOT/westlake-deploy-ohos/v3-hbc}"

DRY_RUN=0
SKIP_LIBSKIA=0
UNINSTALL=0

for arg in "$@"; do
    case "$arg" in
        --dry-run)      DRY_RUN=1 ;;
        --skip-libskia) SKIP_LIBSKIA=1 ;;
        --uninstall)    UNINSTALL=1 ;;
        -h|--help) sed -n '1,40p' "$0"; exit 0 ;;
        *) echo "unknown flag: $arg" >&2; exit 2 ;;
    esac
done

hdc() { "$HDC" -t "$HDC_SERIAL" "$@"; }
hdc_shell() { hdc shell "$*" 2>&1 | tr -d '\r'; }

run() {
    if [ "$DRY_RUN" = "1" ]; then echo "  [DRY] $*"; else "$@" || return 1; fi
}

push_file() {
    local local_path="$1" device_path="$2"
    if [ ! -f "$local_path" ]; then
        echo "  SKIP (not found): $local_path" >&2
        return 0
    fi
    if [ "$DRY_RUN" = "1" ]; then
        echo "  [DRY] hdc file send $local_path $device_path"
        return 0
    fi
    # hdc.exe is a Windows binary; convert WSL path to Windows UNC so it
    # doesn't try to resolve a /-prefixed path relative to its cwd.
    local win_path
    if command -v wslpath >/dev/null 2>&1; then
        win_path=$(wslpath -w "$local_path")
    else
        win_path="$local_path"
    fi
    local out
    out=$(hdc file send "$win_path" "$device_path" 2>&1 | tr -d '\r')
    if echo "$out" | grep -qiE 'fail|error|\[Fail\]'; then
        echo "  FAIL: $(basename "$local_path") -> $device_path"
        echo "    $out"
        return 1
    fi
    echo "  $(basename "$local_path") -> $device_path"
}

# push every .so listed; src = $V3_LOCAL/lib
push_so_to() {
    local dst="$1"; shift
    for f in "$@"; do push_file "$V3_LOCAL/lib/$f" "$dst/$f" || return 1; done
}

# -------- preflight --------
echo "=========================================="
echo "V3 HBC -> DAYU200 deploy (W2)"
echo "  source: $V3_LOCAL"
echo "  target: $HDC_SERIAL"
echo "=========================================="

if ! [ -x "$HDC" ]; then echo "ERROR: hdc not at $HDC"; exit 2; fi
if [ "$DRY_RUN" = "0" ]; then
    if ! hdc list targets 2>&1 | tr -d '\r' | grep -q "$HDC_SERIAL"; then
        echo "ERROR: DAYU200 ($HDC_SERIAL) not connected"; exit 2
    fi
fi

# -------- uninstall mode --------
if [ "$UNINSTALL" = "1" ]; then
    echo "[UNINSTALL] Removing deployed V3 files..."
    run hdc shell "mount -o remount,rw / 2>/dev/null"
    run hdc shell "bm uninstall -n com.example.helloworld 2>/dev/null"
    run hdc shell "rm -rf /system/android"
    run hdc shell "rm -f /system/bin/appspawn-x"
    run hdc shell "rm -f /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json"
    run hdc shell "rm -f /system/lib/liboh_adapter_bridge.so /system/lib/libapk_installer.so /system/lib/libinstalls.z.so"
    run hdc shell "rm -f /system/lib/liboh_hwui_shim.so /system/lib/liboh_skia_rtti_shim.so /system/lib/liboh_android_runtime.so"
    run hdc shell "rm -f /system/lib/libandroid.so /system/lib/libc_musl.so"
    echo "[UNINSTALL] Done. Reboot device for clean state."
    exit 0
fi

# -------- Stage 3.0: mount + mkdirs --------
echo ""
echo "[3.0/3.f] mount + mkdir device target dirs..."
run hdc shell "mount -o remount,rw /" || true
run hdc shell "mount -o remount,rw /system" || true
run hdc shell "mkdir -p /system/lib/platformsdk /system/android/lib /system/android/framework/arm /system/android/etc/icu /system/android/etc"

# -------- Stage 3b: OH service .so (10) + libbms symlink --------
echo ""
echo "[3b] OH service .so to /system/lib + /system/lib/platformsdk..."
push_so_to /system/lib libwms.z.so libappms.z.so libbms.z.so libappspawn_client.z.so
if [ "$SKIP_LIBSKIA" = "0" ]; then
    push_so_to /system/lib libskia_canvaskit.z.so
else
    echo "  SKIP libskia_canvaskit.z.so (--skip-libskia)"
fi
push_so_to /system/lib/platformsdk libabilityms.z.so libscene_session.z.so libscene_session_manager.z.so librender_service_base.z.so libappexecfwk_common.z.so
# libbms.z.so symlink (per SOP 3b)
run hdc shell "rm -f /system/lib/platformsdk/libbms.z.so && ln -sf /system/lib/libbms.z.so /system/lib/platformsdk/libbms.z.so"
echo "  libbms.z.so symlink installed"
# librender_service.z.so lives in /system/lib/ per SOP comment
push_so_to /system/lib librender_service.z.so

# -------- Stage 3c: AOSP native .so (38) + 3 adapter shims dual-path --------
echo ""
echo "[3c] AOSP native .so to /system/android/lib/..."
# AOSP native libs (38 in lib/, excluding the 11 OH-service .z.so + 5 adapter shims)
# In our flat lib/, the "AOSP" ones are those without .z.so suffix EXCEPT the
# 5 adapter shims (liboh_*, libapk_installer). Identify by file naming.
AOSP_NATIVE_SO=$(cd "$V3_LOCAL/lib" && ls *.so 2>/dev/null | grep -v '\.z\.so$' | grep -v '^liboh_' | grep -v '^libapk_installer\.so$' || true)
for so in $AOSP_NATIVE_SO; do
    push_file "$V3_LOCAL/lib/$so" "/system/android/lib/$so" || true
done

# Adapter shims dual-path (/system/android/lib/ + /system/lib/)
for shim in liboh_android_runtime.so liboh_hwui_shim.so liboh_skia_rtti_shim.so; do
    push_file "$V3_LOCAL/lib/$shim" "/system/android/lib/$shim"
    push_file "$V3_LOCAL/lib/$shim" "/system/lib/$shim"
done
# adapter bridge + apk installer + installs (single path /system/lib)
push_file "$V3_LOCAL/lib/liboh_adapter_bridge.so" /system/lib/liboh_adapter_bridge.so
push_file "$V3_LOCAL/lib/libapk_installer.so" /system/lib/libapk_installer.so
# libinstalls.z.so per SOP 3f
push_file "$V3_LOCAL/lib/libinstalls.z.so" /system/lib/libinstalls.z.so
# libsurface.z.so to /system/android/lib (if present in flat layout)
if [ -f "$V3_LOCAL/lib/libsurface.z.so" ]; then
    push_file "$V3_LOCAL/lib/libsurface.z.so" /system/lib/libsurface.z.so
fi

# Stage 3c-end chcon adapter shims dual-path (SOP P-1)
echo "  [3c-end] chcon dual-path adapter shims -> system_lib_file:s0"
run hdc shell "chcon u:object_r:system_lib_file:s0 \
    /system/android/lib/liboh_android_runtime.so /system/lib/liboh_android_runtime.so \
    /system/android/lib/liboh_hwui_shim.so /system/lib/liboh_hwui_shim.so \
    /system/android/lib/liboh_skia_rtti_shim.so /system/lib/liboh_skia_rtti_shim.so" || true

# -------- Stage 3d: framework jars + ICU + fonts.xml dual-path --------
echo ""
echo "[3d] framework jars + ICU + fonts.xml..."
JARS_DIR="$V3_LOCAL/jars"
for j in framework.jar framework-classes.dex.jar framework-res-package.jar \
         core-oj.jar core-libart.jar core-icu4j.jar okhttp.jar bouncycastle.jar apache-xml.jar \
         oh-adapter-framework.jar oh-adapter-runtime.jar adapter-mainline-stubs.jar; do
    push_file "$JARS_DIR/$j" "/system/android/framework/$j"
done
# framework-res.apk: per SOP, must be a TRUE FILE (not symlink). v3-hbc/jars/ has it pre-staged.
if [ -f "$JARS_DIR/framework-res.apk" ]; then
    push_file "$JARS_DIR/framework-res.apk" /system/android/framework/framework-res.apk
else
    # fall back to SOP's cp from framework-res-package.jar
    run hdc shell "rm -f /system/android/framework/framework-res.apk; cp /system/android/framework/framework-res-package.jar /system/android/framework/framework-res.apk; chmod 644 /system/android/framework/framework-res.apk"
fi
# ICU data
push_file "$V3_LOCAL/etc/icudt72l.dat" /system/android/etc/icu/icudt72l.dat
# fonts.xml dual-path (P-12/P-13)
push_file "$V3_LOCAL/etc/fonts.xml" /system/android/etc/fonts.xml
run hdc shell "rm -f /system/etc/fonts.xml; cp /system/android/etc/fonts.xml /system/etc/fonts.xml"
run hdc shell "chcon u:object_r:system_fonts_file:s0 /system/etc/fonts.xml /system/android/etc/fonts.xml" || true

# -------- Stage 3e: boot image (27 files, md5 verify) --------
echo ""
echo "[3e] boot image 27 files..."
BCP_DIR="$V3_LOCAL/bcp"
for seg in boot boot-core-libart boot-core-icu4j boot-okhttp boot-bouncycastle boot-apache-xml \
           boot-adapter-mainline-stubs boot-framework boot-oh-adapter-framework; do
    for ext in art oat vdex; do
        push_file "$BCP_DIR/$seg.$ext" "/system/android/framework/arm/$seg.$ext"
    done
done

# md5 verify
echo "  [3e-verify] md5sum check..."
MD5_FAIL=0
for seg in boot boot-core-libart boot-core-icu4j boot-okhttp boot-bouncycastle boot-apache-xml \
           boot-adapter-mainline-stubs boot-framework boot-oh-adapter-framework; do
    for ext in art oat vdex; do
        local_md5=$(md5sum "$BCP_DIR/$seg.$ext" 2>/dev/null | awk '{print $1}')
        device_md5=$(hdc_shell "md5sum /system/android/framework/arm/$seg.$ext 2>/dev/null" | awk '{print $1}')
        if [ "$local_md5" != "$device_md5" ]; then
            echo "  MD5 MISMATCH $seg.$ext: local=$local_md5 device=$device_md5"
            MD5_FAIL=$((MD5_FAIL + 1))
        fi
    done
done
if [ "$MD5_FAIL" != "0" ]; then echo "  FATAL: $MD5_FAIL boot image md5 mismatches"; exit 1; fi
echo "  all 27 boot files md5 OK"

# Stage 3e-end chcon boot image segments (SOP P-1)
echo "  [3e-end] chcon boot image -> system_lib_file:s0"
run hdc shell "for f in /system/android/framework/arm/boot*.art /system/android/framework/arm/boot*.oat /system/android/framework/arm/boot*.vdex; do case \$f in *.b40_pre|*.orig*|*.moved*|*.pre_b11*) ;; *) chcon u:object_r:system_lib_file:s0 \$f 2>/dev/null;; esac; done"

# -------- Stage 3f: appspawn-x bin + cfg + namespace ini + file_contexts --------
echo ""
echo "[3f] appspawn-x binary + configs + SELinux..."
push_file "$V3_LOCAL/bin/appspawn-x" /system/bin/appspawn-x
push_file "$V3_LOCAL/etc/appspawn_x.cfg" /system/etc/init/appspawn_x.cfg
push_file "$V3_LOCAL/etc/appspawn_x_sandbox.json" /system/etc/appspawn_x_sandbox.json
push_file "$V3_LOCAL/etc/ld-musl-namespace-arm.ini" /system/etc/ld-musl-namespace-arm.ini
push_file "$V3_LOCAL/etc/file_contexts" /system/etc/selinux/targeted/contexts/file_contexts

# -------- Stage 3f end: chmod + restorecon + symlinks --------
echo ""
echo "[3f-end] chmod + restorecon + symlinks..."
run hdc shell "chmod 755 /system/bin/appspawn-x"
run hdc shell "chmod 644 /system/lib/*.so /system/lib/platformsdk/*.z.so" || true
run hdc shell "chmod 644 /system/android/lib/*.so" || true
run hdc shell "chmod 644 /system/android/framework/*.jar" || true
run hdc shell "chmod 644 /system/android/framework/arm/*.art /system/android/framework/arm/*.oat /system/android/framework/arm/*.vdex" || true
run hdc shell "chmod 644 /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json /system/etc/ld-musl-namespace-arm.ini /system/etc/selinux/targeted/contexts/file_contexts"
# restorecon (use find -exec per SOP — toybox restorecon has no -R)
run hdc shell "restorecon /system/bin/appspawn-x"
run hdc shell "find /system/android/lib -exec restorecon {} \;" || true

# Symlinks per SOP 3f
run hdc shell "ln -sf /lib/ld-musl-arm.so.1 /system/lib/libc_musl.so"
run hdc shell "rm -f /system/android/lib/libshared_libz.z.so; ln -sf /system/lib/chipset-sdk-sp/libshared_libz.z.so /system/android/lib/libshared_libz.z.so"
run hdc shell "rm -f /system/android/lib/libappexecfwk_common.z.so; ln -sf /system/lib/platformsdk/libappexecfwk_common.z.so /system/android/lib/libappexecfwk_common.z.so"
# G2.14aa libandroid.so -> liboh_android_runtime.so dual path
run hdc shell "rm -f /system/android/lib/libandroid.so; ln -sf liboh_android_runtime.so /system/android/lib/libandroid.so"
run hdc shell "rm -f /system/lib/libandroid.so; ln -sf liboh_android_runtime.so /system/lib/libandroid.so"

echo ""
echo "=========================================="
echo "DEPLOY COMPLETE"
echo "=========================================="
echo "Next: reboot device, verify appspawn-x runs, then aa start HelloWorld."
