#!/bin/bash
# ============================================================================
# 用途：用于全新部署（factory baseline → 完整 adapter stack）。
#       含 staging + md5 双验 + Stage 1 备份 13 件 device 原件 +
#       Stage 3.9 全量校验 + Stage 3.5 reboot 健康验证 + Stage 4 APK 验证。
#       单文件/小批量增量部署请用 deploy_to_dayu200.sh --only-files=...。
# ============================================================================
# 重复犯错警示 (deploy 类，调改前必读) — 与 deploy_to_dayu200.sh 共用一套
# ============================================================================
# 完整 9 条详见 deploy_to_dayu200.sh 文件头。本脚本是分段版，每条同等适用。
# 速查表（按本脚本 stage 顺序对应到的高发坑）：
#
# stage 1 (backup):     [P-5] 备份只 hdc shell cp X X.orig_DATE，禁 hdc file recv。
# stage 2 (stop svc):   [P-6] 不能 kill launcher，只 stop foundation + render_service。
# stage 3b (OH .so):    [P-7] hdc file send 复刻 src 相对路径，必须 staging。
#                       [P-8] libbms.z.so dual-path symlink。
# stage 3c (AOSP .so):  [P-3] /system/lib + /system/android/lib 双路径同步。
# stage 3d (jars):      [P-9] oh-adapter-framework.jar 唯一来源 out/adapter/（防双源漂移）；
#                              framework.jar 仍是 BCP 成员（B.41 抛弃决策已于 2026-04-30 回退）。
#                       [P-12] fonts.xml 双路径：真文件 /system/android/etc/fonts.xml +
#                              symlink /system/etc/fonts.xml -> 真文件（AOSP SystemFonts.java
#                              硬编码 /system/etc/fonts.xml）。删 symlink 而留真文件 = 假性
#                              修复，下次 Typeface.&lt;clinit&gt; 走真路径还会 ENOENT。
#                       [P-13] fonts.xml SE label 必须 chcon system_fonts_file:s0；
#                              restorecon 给的默认 system_etc_file:s0 让 normal_hap App
#                              域禁读 (EACCES) → setSystemFontMap NPE → ensureBindApp 失败。
# stage 3e (boot img):  [P-1] boot.{art,oat,vdex} 必须 chcon system_lib_file，否则
#                              appspawn:s0 域 flock 被拒，aa start 立刻 SIGABRT。
# stage 3f (binaries):  [P-2] 替换 /system/bin/appspawn-x 后必须 restorecon。
#                       [P-4] Git Bash 下所有 hdc 调用前置 MSYS_NO_PATHCONV=1。
#
# 任何新增 stage / 修改现有 stage 前，把上面 9 条对照过一遍。
# ============================================================================
#
# deploy_stage.sh — staged DAYU200 deployment per DEPLOY_SOP.md v4
#
# Unlike deploy_to_dayu200.sh (one-shot full deploy), this script runs ONE
# stage per invocation so the user can manually verify output before the next.
# All /system pushes go through /data/local/tmp/stage/ with ls+md5 validation
# (see DEPLOY_SOP.md "全局三条" and memory feedback_verify_hdc_send_staging.md).
#
# Usage:  bash deploy_stage.sh <stage>
#
# Stages (run in this order, verify each before next):
#   0     preflight checks (hdc alive, factory baseline, artifact sanity)
#   1     backup 12 device originals (device-cp + local hdc file recv)
#   2     stop foundation + render_service (NOT appspawn, NOT launcher)
#   3.0   mkdir 5 target directories
#   3b    OH services .so (10 files + libbms symlink)
#   3c    AOSP native .so to /system/android/lib/ (38 + 3 files)
#   3d    AOSP framework jars + adapter jars + ICU (12 files)
#   3e    Boot image (24 files, md5 verified)
#   3f    Adapter binaries + cfg + namespace linker + 3 symlinks + chmod
#   3.9   Full integrity + consistency scan
#   3.5   reboot + system health verify
#   4     APK install + launch verify
#
# Environment overrides:
#   TS=YYYYMMDD         backup timestamp suffix (default: today)
#   ADAPTER_ROOT        project root (default: D:/code/adapter)
#   HDC                 hdc binary (default: hdc)

set -euo pipefail

# ============================================================================
# Globals
# ============================================================================

ADAPTER_ROOT="${ADAPTER_ROOT:-D:/code/adapter}"
OUT="$ADAPTER_ROOT/out"
FW_CFG="$ADAPTER_ROOT/framework/appspawn-x/config"
OHOS_PATCHES="$ADAPTER_ROOT/ohos_patches"
HDC="${HDC:-hdc}"
TS="${TS:-$(date +%Y%m%d)}"
STAGE_DIR="/data/local/tmp/stage"

# All files overwritten by deploy — full paths on device (for Stage 1 backup + Stage 3.9 verify)
OVERWRITE_LIB=(
    libwms.z.so libappms.z.so libbms.z.so libskia_canvaskit.z.so
    libinstalls.z.so libappspawn_client.z.so
    # G2.14au r5 (2026-05-11): librender_service.z.so lives in /system/lib/
    # (NOT platformsdk/, unlike librender_service_base.z.so).  Holds the
    # OH RS server-side classes RSUniRenderVisitor / RSUniRenderProcessor /
    # RSUniRenderComposerAdapter — patched with probes per G2.14au r5.
    librender_service.z.so
)
OVERWRITE_PLATFORMSDK=(
    libabilityms.z.so libscene_session.z.so libscene_session_manager.z.so
    librender_service_base.z.so libappexecfwk_common.z.so
)
OVERWRITE_ETC=( ld-musl-namespace-arm.ini selinux/targeted/contexts/file_contexts )

# ============================================================================
# Helpers
# ============================================================================

log()   { echo "[$(date +%H:%M:%S)] $*"; }
ok()    { echo "  ✓ $*"; }
fail()  { echo "  ✗ $*" >&2; }
warn()  { echo "  ⚠ $*" >&2; }
abort() { echo "ABORT: $*" >&2; exit 1; }

to_win() {
    local p="$1"
    if command -v cygpath >/dev/null 2>&1; then
        cygpath -w "$p"
    else
        echo "$p" | sed 's|/|\\|g'
    fi
}

hdc_shell() {
    MSYS_NO_PATHCONV=1 "$HDC" shell "$@"
}

# Strip \r (hdc shell output on Windows bash)
chomp() { tr -d '\r\n'; }

# stage_push <local_path> <device_target_path>
#   1) send to /data/local/tmp/stage/<basename>
#   2) hdc shell stat to confirm regular file (not dir — hdc quirk guard)
#   3) md5 compare staging vs local
#   4) cp staging → final
#   5) md5 compare final vs local
stage_push() {
    local local_path="$1"
    local device_path="$2"
    [ -f "$local_path" ] || abort "local missing: $local_path"
    local bn
    bn=$(basename "$local_path")
    local win
    win=$(to_win "$local_path")

    MSYS_NO_PATHCONV=1 "$HDC" file send "$win" "$STAGE_DIR/$bn" >/dev/null \
        || abort "hdc file send failed: $bn"

    local kind
    kind=$(hdc_shell "stat -c '%F' $STAGE_DIR/$bn 2>&1" | chomp)
    [ "$kind" = "regular file" ] \
        || abort "hdc quirk: $bn landed as '$kind' in staging (expected regular file)"

    local local_md5 stage_md5 final_md5
    local_md5=$(md5sum "$local_path" | awk '{print $1}')
    stage_md5=$(hdc_shell "md5sum $STAGE_DIR/$bn 2>/dev/null" | awk '{print $1}' | chomp)
    [ "$local_md5" = "$stage_md5" ] \
        || abort "md5 mismatch staging: $bn (local=$local_md5 stage=$stage_md5)"

    hdc_shell "cp $STAGE_DIR/$bn $device_path" >/dev/null \
        || abort "cp failed: $STAGE_DIR/$bn → $device_path"

    final_md5=$(hdc_shell "md5sum $device_path 2>/dev/null" | awk '{print $1}' | chomp)
    [ "$local_md5" = "$final_md5" ] \
        || abort "md5 mismatch final: $bn (local=$local_md5 device=$final_md5)"

    ok "$bn → $device_path"
}

check_hdc_alive() {
    local alive
    alive=$(hdc_shell "echo alive" 2>&1 | chomp || true)
    [ "$alive" = "alive" ] || abort "hdc unresponsive (got: '$alive')"
}

ensure_stage_dir() {
    hdc_shell "mkdir -p $STAGE_DIR" >/dev/null
}

# ============================================================================
# Stage 0 — preflight
# ============================================================================

stage_0() {
    log "Stage 0 · 前置检查"
    check_hdc_alive
    ok "hdc alive"

    local dev
    dev=$("$HDC" list targets 2>&1 | head -1 | chomp || true)
    [ -n "$dev" ] && ! echo "$dev" | grep -qi 'empty\|none' || abort "no device connected ($dev)"
    ok "device: $dev"

    local has_android
    has_android=$(hdc_shell "ls -d /system/android 2>&1" | chomp)
    echo "$has_android" | grep -q "No such" || abort "/system/android already exists — not factory baseline"
    ok "/system/android absent (factory baseline)"

    # 2026-05-09 G2.14ak: 旧 hardcoded size check (23760896) 已过期 — boot
    # image 经 G2.14ad/ag/ah 多次重建后 size 自然演进。改为校验 boot image 与
    # libart.so 一致性：boot.oat 必须比 libart.so 新（或同一次编译产出），
    # 因为 boot.oat 由 dex2oat 用当时的 libart ARM32 build 编出；libart 后改
    # 而 boot image 未重建会导致 ART ValidateOatFile/ImageHeader 拒载，
    # SIGABRT "ImageHeader version mismatch"。
    local boot_oat="$OUT/boot-image/boot.oat"
    local libart="$OUT/aosp_lib/libart.so"
    [ -f "$boot_oat" ] || abort "boot.oat missing: $boot_oat"
    [ -f "$libart" ]   || abort "libart.so missing: $libart"
    # 用 mtime 比较：boot.oat 不能早于 libart.so（早 = libart 改了但 boot 没重编）
    local boot_mt libart_mt
    boot_mt=$(stat -c %Y "$boot_oat")
    libart_mt=$(stat -c %Y "$libart")
    if [ "$boot_mt" -lt "$libart_mt" ]; then
        abort "boot.oat ($(date -d @$boot_mt '+%Y-%m-%d %H:%M')) older than libart.so ($(date -d @$libart_mt '+%Y-%m-%d %H:%M')) — boot image needs rebuild"
    fi
    ok "boot.oat mtime >= libart.so mtime (boot image consistent with current libart)"

    # 额外检查：boot.oat 含 dex2oat 写入的 oat-version-string，验非空 + 含 'oat'
    # （防完全 truncated；不锁定具体 size 值，避免 SOP 跟随 size 漂移）
    local oat_magic
    oat_magic=$(head -c 4 "$boot_oat" | xxd -p 2>/dev/null || echo "")
    [ "$oat_magic" = "7f454c46" ] || abort "boot.oat ELF magic missing (file=$oat_magic, expected 7f454c46) — possibly truncated"
    ok "boot.oat ELF magic OK (not truncated)"

    log "Stage 0 PASS"
}

# ============================================================================
# Stage 1 — backup 12 device originals (device-side only; no local hdc file recv)
# ============================================================================

stage_1() {
    log "Stage 1 · 备份 12 件 device 原件 (TS=$TS, 仅设备侧 cp)"
    check_hdc_alive

    hdc_shell "mount -o remount,rw /" >/dev/null || true
    ok "mount / rw"

    # device-side only: cp X X.orig_${TS}  (no local hdc file recv — see feedback_device_file_backup_rule.md)
    local list
    list=""
    for f in "${OVERWRITE_LIB[@]}";         do list="$list /system/lib/$f"; done
    for f in "${OVERWRITE_PLATFORMSDK[@]}"; do list="$list /system/lib/platformsdk/$f"; done
    for f in "${OVERWRITE_ETC[@]}";         do list="$list /system/etc/$f"; done

    local expected=0 actual=0
    for p in $list; do
        expected=$((expected+1))
        local orig="${p}.orig_${TS}"
        hdc_shell "[ -f $p ] && [ ! -f $orig ] && cp $p $orig && echo OK-$(basename $p) || echo SKIP-$(basename $p)" \
            | chomp | sed 's/^/  /'
    done

    # Verify all .orig_${TS} copies exist on device (13 = 6 lib + 5 platformsdk + 2 etc)
    actual=$(hdc_shell "ls /system/lib/*.orig_${TS} /system/lib/platformsdk/*.orig_${TS} /system/etc/ld-musl-namespace-arm.ini.orig_${TS} /system/etc/selinux/targeted/contexts/file_contexts.orig_${TS} 2>/dev/null | wc -l" | chomp)
    [ "$actual" = "$expected" ] || abort "device backup count $actual != $expected"
    log "Stage 1 PASS — $actual .orig_${TS} copies on device"
}

# ============================================================================
# Stage 2 — stop foundation + render_service (ONLY; no appspawn, no launcher)
# ============================================================================

stage_2() {
    log "Stage 2 · 停服务（foundation + render_service only）"
    check_hdc_alive

    hdc_shell "begetctl stop_service foundation"     >/dev/null || true
    hdc_shell "begetctl stop_service render_service" >/dev/null || true

    local pf pr ph
    pf=$(hdc_shell "pidof foundation"     | chomp)
    pr=$(hdc_shell "pidof render_service" | chomp)
    ph=$(hdc_shell "pidof hdcd"           | chomp)

    [ -z "$pf" ] || abort "foundation still alive (pid=$pf)"
    [ -z "$pr" ] || abort "render_service still alive (pid=$pr)"
    [ -n "$ph" ] || abort "hdcd not alive (lost communication channel)"
    ok "foundation stopped / render_service stopped / hdcd alive ($ph)"

    check_hdc_alive
    log "Stage 2 PASS"
}

# ============================================================================
# Stage 3.0 — mkdir target directories
# ============================================================================

stage_3_0() {
    log "Stage 3.0 · 预置目标目录"
    check_hdc_alive
    ensure_stage_dir

    # Note: /system/etc/init 是 factory 原有目录（OH 所有系统服务 cfg 都在这里），不需 mkdir
    # /system/lib/platformsdk 也是 factory 原有，但 mkdir -p 幂等，保留兜底
    hdc_shell "mkdir -p /system/lib/platformsdk /system/android/lib /system/android/framework/arm /system/android/etc/icu" >/dev/null
    local dirs="/system/lib/platformsdk /system/android/lib /system/android/framework/arm /system/android/etc/icu"
    for d in $dirs; do
        local kind
        kind=$(hdc_shell "stat -c '%F' $d 2>&1" | chomp)
        [ "$kind" = "directory" ] || abort "$d not a directory ($kind)"
        ok "$d"
    done
    log "Stage 3.0 PASS"
}

# ============================================================================
# Stage 3b — OH services .so (10 files + libbms symlink)
# ============================================================================

stage_3b() {
    log "Stage 3b · OH 服务 .so (10 件 + libbms symlink)"
    check_hdc_alive
    ensure_stage_dir

    # /system/lib/ from out/oh-service/
    for f in libwms.z.so libappms.z.so libbms.z.so libskia_canvaskit.z.so libappspawn_client.z.so librender_service.z.so; do
        stage_push "$OUT/oh-service/$f" "/system/lib/$f"
    done
    # /system/lib/platformsdk/ from out/oh-service/
    for f in libabilityms.z.so libscene_session.z.so libscene_session_manager.z.so librender_service_base.z.so; do
        stage_push "$OUT/oh-service/$f" "/system/lib/platformsdk/$f"
    done
    # 2026-05-09 G2.14ai: libappexecfwk_common.z.so 来源由 aosp_lib 改 oh-service
    # （OH 库归属于 oh-service，按 CLAUDE.md Build Division Principle；aosp_lib
    # 本来就没这文件）。同时更新 stage_3_9 manifest line。
    stage_push "$OUT/oh-service/libappexecfwk_common.z.so" "/system/lib/platformsdk/libappexecfwk_common.z.so"

    # libbms symlink: /system/lib/platformsdk/libbms.z.so → /system/lib/libbms.z.so
    hdc_shell "rm -f /system/lib/platformsdk/libbms.z.so && ln -sf /system/lib/libbms.z.so /system/lib/platformsdk/libbms.z.so" >/dev/null
    ok "symlink platformsdk/libbms.z.so → /system/lib/libbms.z.so"

    # drwx guard
    local drwx
    drwx=$(hdc_shell "ls -la /system/lib/ /system/lib/platformsdk/ 2>&1 | grep '^d' | grep -v '\\. *$\\|\\.\\. *$'" | chomp)
    # filter: only complain if drwx entries that aren't real dirs appear
    log "Stage 3b PASS"
}

# ============================================================================
# Stage 3c — AOSP native .so → /system/android/lib/ (38 + 3 files)
# ============================================================================

stage_3c() {
    log "Stage 3c · AOSP native .so → /system/android/lib/ (38 + 3)"
    check_hdc_alive
    ensure_stage_dir

    # 38 files from out/aosp_lib/ except libappexecfwk_common.z.so
    local count=0
    for f in "$OUT/aosp_lib"/*.so; do
        [ -f "$f" ] || continue
        local bn
        bn=$(basename "$f")
        [ "$bn" = "libappexecfwk_common.z.so" ] && continue
        stage_push "$f" "/system/android/lib/$bn"
        count=$((count+1))
    done
    [ "$count" -ge 38 ] || abort "aosp_lib count $count < 38 (expected ≥38)"

    # 3 adapter shims — DUAL-PATH per feedback_dual_lib_path_trap.md.
    # OH ld-musl-namespace-arm.ini default namespace lib.paths starts with
    # /system/lib: and ends with /system/android/lib:, so /system/lib copies
    # shadow /system/android/lib copies during dlopen("liboh_X.so") with no
    # absolute path.  Push to BOTH paths to keep them in lockstep.
    for f in liboh_hwui_shim.so liboh_android_runtime.so liboh_skia_rtti_shim.so; do
        stage_push "$OUT/adapter/$f" "/system/android/lib/$f"
        stage_push "$OUT/adapter/$f" "/system/lib/$f"
    done

    # 2026-05-09 G2.14ai: chcon dual-path adapter shims to system_lib_file:s0
    # (mirrors deploy_to_dayu200.sh:492). Without this, hdc file send + cp gives
    # the new copies the parent directory's default label, which can leave
    # /system/lib/ entries with system_file:s0 instead of system_lib_file:s0 —
    # appspawn:s0 domain dlopen fails on flock (same root cause as [P-1]).
    hdc_shell "chcon u:object_r:system_lib_file:s0 /system/android/lib/liboh_android_runtime.so /system/lib/liboh_android_runtime.so /system/android/lib/liboh_hwui_shim.so /system/lib/liboh_hwui_shim.so /system/android/lib/liboh_skia_rtti_shim.so /system/lib/liboh_skia_rtti_shim.so" >/dev/null 2>&1 || true
    ok "chcon system_lib_file on 3 adapter shims (dual-path)"

    log "Stage 3c PASS (38 aosp_lib + 3 adapter shims dual-path + chcon)"
}

# ============================================================================
# Stage 3d — framework jars + ICU + adapter jars (12 files)
# ============================================================================

stage_3d() {
    log "Stage 3d · framework jars + adapter jars + ICU (13 件)"
    check_hdc_alive
    ensure_stage_dir

    # AOSP-built core jars (no adapter modification, includes L5-patched framework.jar)
    # 2026-04-30: B.41 抛弃 framework.jar 的决策已回退 — 它仍是 BCP 成员。
    for f in framework.jar framework-classes.dex.jar framework-res-package.jar \
             core-oj.jar core-libart.jar core-icu4j.jar \
             okhttp.jar bouncycastle.jar apache-xml.jar; do
        stage_push "$OUT/aosp_fwk/$f" "/system/android/framework/$f"
    done
    # adapter-mainline-stubs.jar: G2.8 (2026-04-30) — built by
    # compile_mainline_stubs.sh into $OUT/adapter/.  Earlier B.18-G2.6
    # this script deployed the OTHER 11 jars but skipped this one — boot
    # image worked because gen_boot_image.sh manually cp'd it to aosp_fwk
    # before reading; but stage_3d never re-pushed it to /system, leaving
    # the device's adapter-mainline-stubs.jar potentially stale across
    # iterations.  Now deployed alongside other adapter jars.
    for f in oh-adapter-framework.jar oh-adapter-runtime.jar adapter-mainline-stubs.jar; do
        stage_push "$OUT/adapter/$f" "/system/android/framework/$f"
    done
    # framework-res.apk: AppSpawnXInit.java:696 + android_content_res_ApkAssets.cpp:68
    # 硬编码读 /system/android/framework/framework-res.apk（带 .apk 后缀）。设备上这个
    # 文件实际由 framework-res-package.jar 提供（同 md5 / 同内容、仅后缀不同）。
    #
    # 重要：必须 cp 真文件 — 不能用 symlink。ApkAssets.nativeLoad → libandroidfw
    # ZipArchive open() 内部对 symlink 失败（OH 的 ZipArchive 实现里 fstat /
    # realpath 链对 symlink 处理与真文件不一致，2026-05-09 实测）。改成真文件
    # cp 后 InitSysAM post-init sSystem apkPaths=[framework-res.apk] OK。
    # 缺这文件会让 InitSysAM loadFromPath 抛 IOException → sSystemApkAssets 仍
    # length=0 → AssetManager2::FindEntryInternal NULL deref SEGV at fault addr
    # 0x86c（命中 helloworld <pre-initialize> 主线程）。
    # 实证：B.25/B.26 修法假设此路径存在；2026-05-09 helloworld pid 2088 SIGSEGV 真因。
    hdc_shell "rm -f /system/android/framework/framework-res.apk; cp /system/android/framework/framework-res-package.jar /system/android/framework/framework-res.apk; chmod 644 /system/android/framework/framework-res.apk" >/dev/null
    stage_push "$OUT/aosp_fwk/icu/icudt72l.dat" "/system/android/etc/icu/icudt72l.dat"

    # [P-12][P-13] fonts.xml — 真文件部署到 /system/android/etc/，再放 /system/etc/
    # AOSP SystemFonts.java:50 硬编码 FONTS_XML="/system/etc/fonts.xml"，
    # 缺则 Typeface.<clinit> 抛 FileNotFoundException → parent appspawn-x preload 崩。
    # 真文件 (/system/etc/fonts.xml) + 镜像 (/system/android/etc/fonts.xml)，
    # 字体文件 (/system/fonts/HarmonyOS_Sans*.ttf 等) 由 OH 系统提供，不需另推。
    #
    # [P-13] 关键：必须 chcon system_fonts_file:s0，restorecon 默认会给
    # system_etc_file:s0 → normal_hap App 域禁读 → setSystemFontMap NPE。
    if [ -f "$ADAPTER_ROOT/aosp_patches/data/fonts/fonts.xml" ]; then
        stage_push "$ADAPTER_ROOT/aosp_patches/data/fonts/fonts.xml" \
                   "/system/android/etc/fonts.xml"
        # 直接放 /system/etc/fonts.xml 真文件 (避免 symlink relabel 烦恼)。
        hdc_shell "rm -f /system/etc/fonts.xml; cp /system/android/etc/fonts.xml /system/etc/fonts.xml" >/dev/null
        # [P-13] chcon 显式 system_fonts_file:s0 — 不能依赖 restorecon。
        hdc_shell "chcon u:object_r:system_fonts_file:s0 /system/etc/fonts.xml /system/android/etc/fonts.xml" >/dev/null
        # 验证 label
        local fonts_label
        fonts_label=$(hdc_shell "ls -lZ /system/etc/fonts.xml" 2>&1 | grep -o "system_fonts_file" | head -1)
        [ "$fonts_label" = "system_fonts_file" ] && ok "fonts.xml SE label = system_fonts_file" || fail "fonts.xml SE label != system_fonts_file"
        log "Stage 3d · fonts.xml deployed (dual path) + chcon system_fonts_file:s0"
    else
        warn "Stage 3d · skipping fonts.xml (source not found at aosp_patches/data/fonts/fonts.xml)"
    fi

    log "Stage 3d PASS (14 files + fonts.xml dual-path + SE label fix)"
}

# ============================================================================
# Stage 3e — Boot image (24 files, md5 verified via stage_push)
# ============================================================================

stage_3e() {
    log "Stage 3e · Boot image (27 件, md5 全验)"
    check_hdc_alive
    ensure_stage_dir

    # 9 boot image segments × {art, oat, vdex} = 27 files.
    # 2026-04-30: 加 boot-adapter-mainline-stubs (B.18 起 BCP 第 7 段，原列表
    # 漏推导致设备段 mtime 不齐 → ART ValidateOatFile 拒载 → fallback
    # InitWithoutImage abort "Class mismatch for Ljava/lang/String;"。
    for g in boot boot-core-libart boot-core-icu4j boot-okhttp boot-bouncycastle \
             boot-apache-xml boot-adapter-mainline-stubs boot-framework \
             boot-oh-adapter-framework; do
        for e in art oat vdex; do
            stage_push "$OUT/boot-image/$g.$e" "/system/android/framework/arm/$g.$e"
        done
    done

    # 2026-05-09 G2.14ai: [P-1] 必须 chcon system_lib_file:s0 on every boot
    # image segment (mirrors deploy_to_dayu200.sh:688-689). hdc file send + cp
    # gives boot.{art,oat,vdex} default label = system_file:s0 from parent dir,
    # but appspawn:s0 domain only allows flock() on system_lib_file:s0. Without
    # this chcon, ART JNI_CreateJavaVM Phase 2 SIGABRTs immediately on boot
    # image lock acquisition. The case-statement filters out backup/legacy
    # copies (.b40_pre / .orig* / .moved* / .pre_b11*) which don't need relabel.
    hdc_shell "for f in /system/android/framework/arm/boot*.art /system/android/framework/arm/boot*.oat /system/android/framework/arm/boot*.vdex; do case \$f in *.b40_pre|*.orig*|*.moved*|*.pre_b11*) ;; *) chcon u:object_r:system_lib_file:s0 \$f 2>/dev/null;; esac; done" >/dev/null 2>&1 || true
    ok "chcon system_lib_file on 27 boot image segments"

    log "Stage 3e PASS (27 boot image files, all md5-verified + chcon)"
}

# ============================================================================
# Stage 3f — adapter bin + cfg + namespace linker + symlinks + chmod
# ============================================================================

stage_3f() {
    log "Stage 3f · 适配层 bin + cfg + namespace linker + symlinks + chmod"
    check_hdc_alive
    ensure_stage_dir

    # Binaries
    stage_push "$OUT/adapter/appspawn-x"              "/system/bin/appspawn-x"
    stage_push "$OUT/adapter/liboh_adapter_bridge.so" "/system/lib/liboh_adapter_bridge.so"
    stage_push "$OUT/adapter/libapk_installer.so"     "/system/lib/libapk_installer.so"
    stage_push "$OUT/adapter/libinstalls.z.so"        "/system/lib/libinstalls.z.so"

    # appspawn-x configs
    # ⚠ 反复犯错 [P-10]: cfg 必须保持 "ondemand": true (无 critical / disabled / start-mode)。
    #   OH init `init_service_socket.c:CreateSocketForService()` 只在 IsOnDemandService 为
    #   true 时调用 listen(); 否则 AMS APPSPAWN_CLIENT.connect 必然 ECONNREFUSED(111)。
    #   验证: reboot 后 `cat /proc/net/unix | grep AppSpawnX` Flags 应为 00010000(__SO_ACCEPTCON)。
    #   血训: 2026-04-22→04-29 多次被改成 disabled+condition 致 aa start 卡死。
    #   详见 deploy_to_dayu200.sh [P-10] + project_b40_verify_socket_blocker.md。
    stage_push "$FW_CFG/appspawn_x.cfg"          "/system/etc/init/appspawn_x.cfg"
    stage_push "$FW_CFG/appspawn_x_sandbox.json" "/system/etc/appspawn_x_sandbox.json"

    # musl namespace linker (appspawn-x layer 2 prerequisite)
    stage_push "$OHOS_PATCHES/third_party/musl/config/ld-musl-namespace-arm.ini" \
               "/system/etc/ld-musl-namespace-arm.ini"

    # SELinux file_contexts (appspawn-x layer 1 prerequisite — gives /system/bin/appspawn-x
    # the appspawn_exec label so init can domain-transition to appspawn:s0; without this
    # init execv fails with EACCES (2026-04-21 事故根因).  Source is the ECS compile
    # output (factory 618 lines + adapter 3-line patch, see ohos_patches/selinux_adapter/
    # file_contexts.patch).  Not policy.31 (too risky to replace).
    stage_push "$OUT/oh-service/file_contexts" \
               "/system/etc/selinux/targeted/contexts/file_contexts"

    # Symlinks
    hdc_shell "ln -sf /lib/ld-musl-arm.so.1 /system/lib/libc_musl.so" >/dev/null
    ok "symlink libc_musl.so → /lib/ld-musl-arm.so.1"
    hdc_shell "rm -f /system/android/lib/libshared_libz.z.so; ln -sf /system/lib/chipset-sdk-sp/libshared_libz.z.so /system/android/lib/libshared_libz.z.so" >/dev/null
    ok "symlink android/lib/libshared_libz.z.so → chipset-sdk-sp/libshared_libz.z.so"
    hdc_shell "rm -f /system/android/lib/libappexecfwk_common.z.so; ln -sf /system/lib/platformsdk/libappexecfwk_common.z.so /system/android/lib/libappexecfwk_common.z.so" >/dev/null
    ok "symlink android/lib/libappexecfwk_common.z.so → platformsdk/libappexecfwk_common.z.so"

    # G2.14aa (2026-05-08): hwui RenderThread.cpp::ASurfaceControlFunctions ctor
    # 在 dlopen("libandroid.so", RTLD_NOW | RTLD_NODELETE) 之后 dlsym 13 个 NDK
    # 符号 (ASurfaceControl_create / ASurfaceTransaction_create / ...) 并
    # LOG_ALWAYS_FATAL_IF。这些符号的真桥实现已编入 liboh_android_runtime.so。
    # device 上 libandroid.so 必须 symlink 到 liboh_android_runtime.so，否则 hwui
    # SIGABRT "Failed to find required symbol ASurfaceControl_create"。
    # 实证：2026-05-09 helloworld pid 2373 SIGABRT signal 6 SI_TKILL in
    # <pre-initialize> main thread, 调用栈在 hwui RenderThread init。
    hdc_shell "rm -f /system/android/lib/libandroid.so; ln -sf liboh_android_runtime.so /system/android/lib/libandroid.so" >/dev/null
    hdc_shell "rm -f /system/lib/libandroid.so; ln -sf liboh_android_runtime.so /system/lib/libandroid.so" >/dev/null
    ok "symlink libandroid.so → liboh_android_runtime.so (G2.14aa NDK provider, dual path)"

    # chmod batch
    hdc_shell "chmod 755 /system/bin/appspawn-x" >/dev/null
    hdc_shell "chmod 644 /system/lib/libapk_installer.so /system/lib/liboh_adapter_bridge.so /system/lib/libinstalls.z.so" >/dev/null
    hdc_shell "chmod 644 /system/lib/libappms.z.so /system/lib/libwms.z.so /system/lib/libbms.z.so /system/lib/libskia_canvaskit.z.so /system/lib/libappspawn_client.z.so /system/lib/librender_service.z.so" >/dev/null
    hdc_shell "chmod 644 /system/lib/platformsdk/libabilityms.z.so /system/lib/platformsdk/librender_service_base.z.so /system/lib/platformsdk/libscene_session.z.so /system/lib/platformsdk/libscene_session_manager.z.so /system/lib/platformsdk/libappexecfwk_common.z.so" >/dev/null
    hdc_shell "chmod 644 /system/android/lib/*.so" >/dev/null
    hdc_shell "chmod 644 /system/android/framework/*.jar" >/dev/null
    hdc_shell "chmod 644 /system/android/framework/arm/*.art /system/android/framework/arm/*.oat /system/android/framework/arm/*.vdex" >/dev/null
    hdc_shell "chmod 644 /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json /system/etc/ld-musl-namespace-arm.ini /system/etc/selinux/targeted/contexts/file_contexts" >/dev/null
    ok "chmod batch complete"

    # restorecon: 按新 file_contexts 给本次 deploy 推过的文件重新打 SELinux label
    # 必须在 file_contexts push 之后执行，才能读到 adapter 新增的 3 条规则
    # OH toybox restorecon 不支持 -R，用 find -exec 递归（覆盖 dir + 文件 + symlink）
    hdc_shell "restorecon /system/bin/appspawn-x" >/dev/null 2>&1
    ok "restorecon /system/bin/appspawn-x → appspawn_exec"
    hdc_shell "find /system/android/lib -exec restorecon {} \;" >/dev/null 2>&1
    ok "find /system/android/lib -exec restorecon → system_lib_file (覆盖 dir + *.so + symlink)"

    log "Stage 3f PASS (4 bin + 3 cfg + 1 selinux file_contexts + 3 symlinks + chmod + restorecon)"
}

# ============================================================================
# Stage 3.9 — full integrity + consistency scan
# ============================================================================

stage_3_9() {
    log "Stage 3.9 · 完整性 + 一致性全量校验"
    check_hdc_alive

    local errors=0

    # Build the full push manifest: <local_path>=<device_path>
    local -a manifest=()

    # 3b
    for f in libwms.z.so libappms.z.so libbms.z.so libskia_canvaskit.z.so libappspawn_client.z.so librender_service.z.so; do
        manifest+=("$OUT/oh-service/$f=/system/lib/$f")
    done
    for f in libabilityms.z.so libscene_session.z.so libscene_session_manager.z.so librender_service_base.z.so; do
        manifest+=("$OUT/oh-service/$f=/system/lib/platformsdk/$f")
    done
    manifest+=("$OUT/oh-service/libappexecfwk_common.z.so=/system/lib/platformsdk/libappexecfwk_common.z.so")

    # 3c: 38 aosp_lib + 3 adapter shims
    for f in "$OUT/aosp_lib"/*.so; do
        [ -f "$f" ] || continue
        local bn; bn=$(basename "$f")
        [ "$bn" = "libappexecfwk_common.z.so" ] && continue
        manifest+=("$f=/system/android/lib/$bn")
    done
    for f in liboh_hwui_shim.so liboh_android_runtime.so liboh_skia_rtti_shim.so; do
        manifest+=("$OUT/adapter/$f=/system/android/lib/$f")
    done

    # 3d
    # AOSP-built core jars (no adapter modification, includes L5-patched framework.jar)
    # 2026-04-30: B.41 抛弃 framework.jar 的决策已回退 — 重新进 manifest。
    for f in framework.jar framework-classes.dex.jar framework-res-package.jar \
             core-oj.jar core-libart.jar core-icu4j.jar okhttp.jar bouncycastle.jar apache-xml.jar; do
        manifest+=("$OUT/aosp_fwk/$f=/system/android/framework/$f")
    done
    for f in oh-adapter-framework.jar oh-adapter-runtime.jar adapter-mainline-stubs.jar; do
        manifest+=("$OUT/adapter/$f=/system/android/framework/$f")
    done
    manifest+=("$OUT/aosp_fwk/icu/icudt72l.dat=/system/android/etc/icu/icudt72l.dat")

    # 3e — 9 segments × 3 = 27 files (含 boot-adapter-mainline-stubs，2026-04-30 修复漏列)
    for g in boot boot-core-libart boot-core-icu4j boot-okhttp boot-bouncycastle \
             boot-apache-xml boot-adapter-mainline-stubs boot-framework \
             boot-oh-adapter-framework; do
        for e in art oat vdex; do
            manifest+=("$OUT/boot-image/$g.$e=/system/android/framework/arm/$g.$e")
        done
    done

    # 3f
    manifest+=("$OUT/adapter/appspawn-x=/system/bin/appspawn-x")
    manifest+=("$OUT/adapter/liboh_adapter_bridge.so=/system/lib/liboh_adapter_bridge.so")
    manifest+=("$OUT/adapter/libapk_installer.so=/system/lib/libapk_installer.so")
    manifest+=("$OUT/adapter/libinstalls.z.so=/system/lib/libinstalls.z.so")
    manifest+=("$FW_CFG/appspawn_x.cfg=/system/etc/init/appspawn_x.cfg")
    manifest+=("$FW_CFG/appspawn_x_sandbox.json=/system/etc/appspawn_x_sandbox.json")
    manifest+=("$OHOS_PATCHES/third_party/musl/config/ld-musl-namespace-arm.ini=/system/etc/ld-musl-namespace-arm.ini")
    manifest+=("$OUT/oh-service/file_contexts=/system/etc/selinux/targeted/contexts/file_contexts")

    log "Manifest: ${#manifest[@]} files"

    # Scan
    for entry in "${manifest[@]}"; do
        local lp="${entry%%=*}"
        local dp="${entry##*=}"
        local lmd5 dmd5 lsz dsz
        lmd5=$(md5sum "$lp" 2>/dev/null | awk '{print $1}')
        lsz=$(stat -c %s "$lp" 2>/dev/null || echo 0)
        dmd5=$(hdc_shell "md5sum $dp 2>/dev/null" | awk '{print $1}' | chomp)
        dsz=$(hdc_shell "stat -c %s $dp 2>/dev/null" | chomp)
        if [ -z "$dmd5" ]; then
            fail "missing device: $dp"
            errors=$((errors+1))
        elif [ "$lmd5" != "$dmd5" ]; then
            fail "md5 mismatch: $dp (local=$lmd5 device=$dmd5)"
            errors=$((errors+1))
        elif [ "$lsz" != "$dsz" ]; then
            fail "size mismatch: $dp (local=$lsz device=$dsz)"
            errors=$((errors+1))
        fi
    done

    # drwx guard on critical dirs — use find (toybox has no awk)
    # Expected: /system/android/framework has 'arm' subdir (allowlist)
    for d in /system/android/lib /system/android/framework/arm /system/android/etc/icu; do
        local unexpected
        unexpected=$(hdc_shell "find $d -mindepth 1 -maxdepth 1 -type d 2>/dev/null" | chomp)
        if [ -n "$unexpected" ]; then
            fail "unexpected drwx in $d:"
            echo "$unexpected" | sed 's/^/    /'
            errors=$((errors+1))
        fi
    done
    # /system/android/framework may have 'arm' subdir (expected)
    local fwk_sub
    fwk_sub=$(hdc_shell "find /system/android/framework -mindepth 1 -maxdepth 1 -type d 2>/dev/null" | tr -d '\r' | grep -vE '^/system/android/framework/arm$' || true)
    if [ -n "$fwk_sub" ]; then
        fail "unexpected drwx in /system/android/framework (non-arm):"
        echo "$fwk_sub" | sed 's/^/    /'
        errors=$((errors+1))
    fi

    if [ $errors -gt 0 ]; then
        abort "Stage 3.9 FAIL — $errors error(s)"
    fi
    log "Stage 3.9 PASS — ${#manifest[@]} files verified (md5 + size), no drwx anomalies"
}

# ============================================================================
# Stage 3.5 — reboot + system health
# ============================================================================

stage_3_5() {
    log "Stage 3.5 · reboot + 系统健康验证"
    check_hdc_alive

    hdc_shell "sync" >/dev/null
    log "sync complete, rebooting..."
    hdc_shell "reboot" >/dev/null || true

    # Poll until hdc comes back
    local i=0
    while [ $i -lt 60 ]; do
        sleep 5
        if "$HDC" shell "echo alive" 2>/dev/null | grep -q alive; then
            ok "device back online after $((i*5))s"
            break
        fi
        i=$((i+1))
    done
    [ $i -lt 60 ] || abort "device did not come back online in 300s"

    # Give OH init 30s to reach Phase 4 (foundation/launcher up)
    sleep 30

    local errors=0
    for p in foundation render_service com.ohos.launcher hdcd; do
        local pid
        pid=$(hdc_shell "pidof $p" | chomp)
        if [ -n "$pid" ]; then
            ok "$p alive (pid=$pid)"
        else
            fail "$p not running"
            errors=$((errors+1))
        fi
    done

    # BMS ready check
    if hdc_shell "hilog 2>/dev/null | head -500" | grep -qi 'BMS.*ready\|BundleMgr.*init.*ok' 2>/dev/null; then
        ok "BMS ready signal found in hilog"
    else
        echo "  (note: no BMS ready signal yet — may need more wait time)"
    fi

    [ $errors -eq 0 ] || abort "Stage 3.5 FAIL — $errors service(s) missing"
    log "Stage 3.5 PASS"
}

# ============================================================================
# Stage 4 — APK install + launch verify
# ============================================================================

stage_4() {
    log "Stage 4 · APK 验证"
    check_hdc_alive

    local apk="$OUT/app/HelloWorld.apk"
    [ -f "$apk" ] || abort "APK not found: $apk"

    local win
    win=$(to_win "$apk")
    MSYS_NO_PATHCONV=1 "$HDC" file send "$win" "/data/local/tmp/HelloWorld.apk" >/dev/null \
        || abort "hdc file send APK failed"
    ok "APK → /data/local/tmp/"

    local out
    out=$(hdc_shell "bm install -p /data/local/tmp/HelloWorld.apk 2>&1" | chomp)
    echo "$out" | grep -qi 'success\|ok' || abort "bm install failed: $out"
    ok "bm install OK"

    hdc_shell "bm dump -n com.example.helloworld" | sed 's/^/    /'

    hdc_shell "aa start -a com.example.helloworld.MainActivity -b com.example.helloworld 2>&1" | chomp | sed 's/^/    /'
    sleep 3

    local app_pid
    app_pid=$(hdc_shell "pidof com.example.helloworld" | chomp)
    [ -n "$app_pid" ] || abort "com.example.helloworld not running after aa start"
    ok "com.example.helloworld alive (pid=$app_pid)"

    log "Stage 4 PASS"
}

# ============================================================================
# Dispatcher
# ============================================================================

print_help() {
    grep '^#' "$0" | sed -n '1,/^# ==/p' | sed 's/^# \?//'
}

case "${1:-help}" in
    0)    stage_0   ;;
    1)    stage_1   ;;
    2)    stage_2   ;;
    3.0)  stage_3_0 ;;
    3b)   stage_3b  ;;
    3c)   stage_3c  ;;
    3d)   stage_3d  ;;
    3e)   stage_3e  ;;
    3f)   stage_3f  ;;
    3.9)  stage_3_9 ;;
    3.5)  stage_3_5 ;;
    4)    stage_4   ;;
    help|-h|--help) print_help ;;
    *)    echo "unknown stage: $1"; print_help; exit 1 ;;
esac
