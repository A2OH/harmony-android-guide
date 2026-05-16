#!/bin/bash
# ============================================================================
# 用途：用于部署单个或多个文件，不能用于全新部署。
#       全新部署请用 deploy_stage.sh（含 staging + md5 双验 + Stage 1 备份 +
#       Stage 3.9 全量校验 + Stage 3.5 reboot 健康验证 + Stage 4 APK 验证）。
# ============================================================================
# 重复犯错警示 (deploy 类，调改前必读)
# ============================================================================
# 以下是本项目部署阶段反复踩过的坑，每一条都让 aa start / 设备启动卡死过。
# 修改本脚本前先把对应防护是否到位过一遍。
#
# [P-1] boot image 段 SELinux label 必须是 system_lib_file
#   现象：appspawn-x 在 init service 下启动时 ART JNI_CreateJavaVM Phase 2
#         立即 SIGABRT，hilog 出 "avc denied { lock } ... tcontext=system_file"。
#         手工跑同一可执行文件却正常 — 因为 shell 域允许 system_file。
#   根因：hdc file send 把新 boot.{art,oat,vdex} 推到 /system/android/framework/arm/
#         之后，restorecon 给的默认 label = u:object_r:system_file:s0；但
#         appspawn:s0 SELinux 域只允许对 system_lib_file 做 file lock，加载
#         boot.art 时 flock() 被拒。
#   措施：本脚本 push 完 boot image 段后立即 chcon u:object_r:system_lib_file:s0
#         /system/android/framework/arm/boot*.{art,oat,vdex}（B.40 沉淀）。
#
# [P-2] 替换 /system/bin/appspawn-x 后必须 restorecon
#   现象：替换二进制后 init 标 service 为 SERVICE_ATTR_INVALID，本 boot 内
#         任何路径无法清，只能 reboot。begetctl start_service 失败。
#   根因：默认 label 跟 init cfg secon=appspawn:s0 不匹配；init 启动校验失败。
#   措施：push 完 /system/bin/appspawn-x 后 restorecon /system/bin/appspawn-x。
#         （memory: feedback_appspawnx_restorecon_invalid_flag.md）
#
# [P-3] /system/lib + /system/android/lib 双路径同步推送
#   现象：ART child fork 后 dlopen libandroid_runtime.so 报"not found"，但
#         /system/lib/ 下文件确实存在；md5 也对。
#   根因：ART child 进程 RUNPATH/LD_LIBRARY_PATH 有的从 /system/lib 找、有的
#         从 /system/android/lib 找。两条路径必须同步存在同一份 .so。
#   措施：push_so_dual() 统一两路径推送；任何新增 .so 都走它。
#         （memory: feedback_dual_lib_path_trap.md）
#
# [P-4] hdc file send 在 Git Bash 下的路径转义陷阱
#   现象：hdc file send src /system/lib 报 "fail to send file" 或推到了
#         /tmp\system\lib 这种乱七八糟的路径。
#   根因：MSYS/Git-Bash 把以 / 开头的参数当成 Windows 路径强转。
#   措施：所有 hdc 调用前置 MSYS_NO_PATHCONV=1。本脚本顶部已设。
#         （memory: reference_hdc_send_path_quirk.md）
#
# [P-5] 设备文件备份只在设备侧 cp，不本机 hdc file recv
#   现象：反复部署后旧版本被覆盖丢失，无法复现"上次 work 的版本"。
#   根因：hdc file recv 把 device 上的文件覆盖掉本机镜像，反向污染上游。
#         本机 out/ 应该是"最近一次 ECS 编译产物的镜像"，不是"设备当前版"。
#   措施：备份只用 hdc shell "cp X X.orig_DATE"；禁止 hdc file recv。
#         （memory: feedback_device_file_backup_rule.md / feedback_ecs_is_build_source.md）
#
# [P-6] 部署期禁止 kill com.ohos.launcher
#   现象：kill launcher 后 reboot 才能恢复桌面，触发 init respawn 风暴。
#   根因：launcher 是 foundation 的子进程，foundation 一停 launcher 自动下线；
#         直接 kill launcher 反而绕过了 init 期望的生命周期。
#   措施：只 stop foundation + render_service；从不 kill launcher。
#         （memory: feedback_no_kill_launcher.md）
#
# [P-7] hdc file send 的目标路径是"复刻 src 相对路径到 dst 下"
#   现象：hdc file send out/adapter/foo.so /system/lib 结果文件去了
#         /system/lib/out/adapter/foo.so，不是 /system/lib/foo.so。
#   根因：hdc file send 不是 cp，而是把 src 当成相对路径整段挂到 dst 下面。
#   措施：要么 src 用绝对路径 + dst 给具体文件名；要么先 push 到
#         /data/local/tmp/staging/<basename> 再 hdc shell cp 到 /system/...
#         （memory: feedback_verify_hdc_send_staging.md）
#
# [P-8] libbms.z.so dual-path symlink
#   现象：BMS 服务启动失败，dlopen libbms.z.so 找不到。
#   根因：/system/lib/libbms.z.so 是物理文件，但 OH 服务管理框架从
#         /system/lib/platformsdk/libbms.z.so 加载，必须 symlink。
#   措施：推 libbms.z.so 后立即 hdc shell ln -sf ../libbms.z.so
#         /system/lib/platformsdk/libbms.z.so。
#         （memory: reference_libbms_dual_path.md）
#
# [P-11] boot image segment 完整列表必须含 9 段（最易漏列 boot-adapter-mainline-stubs）
#   现象：appspawn-x Phase 2 ART VM init SIGABRT 风暴；cppcrash stack 显示
#         art::ClassLinker::CheckSystemClass → InitWithoutImage："Class mismatch for
#         Ljava/lang/String;"。三方 md5 看：oh-adapter-framework.jar / framework.jar
#         全对，但 boot-adapter-mainline-stubs.{art,oat} 设备 vs 本机 ❌ MISMATCH。
#   根因：本脚本 push_many boot-image 段列表只列 8 段（main/core-libart/core-icu4j/
#         okhttp/bouncycastle/apache-xml/framework/oh-adapter-framework），漏掉
#         B.18 起新增的 boot-adapter-mainline-stubs（BCP 第 7 段，含 149 mainline
#         APEX stubs 类）。runtime BCP 9 段，部署后设备只剩 8 段对齐 + 1 段旧版
#         残留 → ART 拒载 boot image 整段。
#   措施：本脚本 [6/8] push_many + 后续 chcon 验证循环 + deploy_stage.sh stage_3e
#         + manifest 必须列全 9 段。任何新增段必须三处同步。
#         （2026-04-30 修复，运行时 hilog 验证 appspawn-x 不再 SIGABRT 即 OK）
#
# [P-9] oh-adapter-framework.jar 单一来源原则（B.41 修正版，2026-04-30）
#   现象：deploy 后 ART SIGABRT 风暴 + "ValidateOatFile checksum mismatch"。
#   根因：out/aosp_fwk/ 与 out/adapter/ 同名 oh-adapter-framework.jar 漂移；
#         boot image 用 A 编译，设备上 push 的是 B。
#   措施：oh-adapter-framework.jar 唯一来源 out/adapter/；out/aosp_fwk/ 不再放
#         同名 jar。framework.jar 由 AOSP Soong 编出后落到 out/aosp_fwk/，本
#         脚本 [5/8] 正常推送（B.41 抛弃 framework.jar 的决策已于 2026-04-30
#         回退——它仍是 BCP 成员，含 L5 reflection patches）。
#         （memory: feedback_framework_jar_boot_image_drift.md）
#
# [P-13] /system/etc/fonts.xml SELinux label 必须是 system_fonts_file:s0 (G2.14r 2026-05-02 沉淀)
#   现象：parent appspawn-x preload OK 后, child fork → ensureBindApplication →
#         ActivityThread.handleBindApplication:6803 调 Typeface.setSystemFontMap →
#         SystemFonts.getSystemPreinstalledFontConfig 抛 EACCES (Permission denied)
#         反复出现:
#         java.io.FileNotFoundException: /system/etc/fonts.xml: open failed: EACCES
#         整链导致 mInitialApplication 不被构造 → ConfigurationController NPE on
#         Application.getResources() → child 退出 → fork-respawn loop。
#   根因：restorecon 给 /system/etc/fonts.xml 默认 label = system_etc_file:s0
#         (继承 /system/etc/ 父目录策略)，但 OH normal_hap App 域**禁读**
#         system_etc_file:s0 类标签。AOSP 路径硬编码 /system/etc/fonts.xml，App
#         必须能读 — 而 OH 给该路径的默认 SE label 不允许 normal_hap 读。
#   血训：2026-05-02 多次部署 fonts.xml 到 /system/etc/ 都被 EACCES 卡住，
#         restorecon 后看到 system_etc_file:s0 没意识到 normal_hap 读不了；
#         也试过 chcon -h u:object_r:system_file:s0 改 symlink 自身但 chcon
#         silent fail（OH SELinux 限制 relabel）；
#         也试过先 rm symlink + 真文件部署到 /system/etc/，再 restorecon ——
#         restorecon 又给回 system_etc_file:s0。
#   措施：fonts.xml 部署后必须 **chcon u:object_r:system_fonts_file:s0** 显式覆盖,
#         不能信任 restorecon 给的默认 label。system_fonts_file:s0 是 OH 给
#         /system/fonts/*.ttf 用的 label, normal_hap 域允许读, AOSP SystemFonts
#         能从 /system/etc/fonts.xml 拉到内容。
#   验证：`ls -lZ /system/etc/fonts.xml` 应显示 system_fonts_file:s0；
#         hilog 不再有 SystemFonts EACCES; child ensureBindApplication 推进过
#         setSystemFontMap 阶段。
#         (memory: G2.14r typeface chain Layer 1 fix)
#
# [P-12] fonts.xml 双路径部署 (G2.14r 2026-05-02 沉淀)
#   现象：parent appspawn-x 在 preload 阶段重复崩；hilog 显示 SystemFonts:
#         "java.io.FileNotFoundException: /system/etc/fonts.xml: open failed: ENOENT"
#         栈深至 android.graphics.fonts.SystemFonts.getSystemPreinstalledFontConfig
#         → Typeface.<clinit> → AppSpawnXInit.preInitTypefaceDefault。
#         crash 在 Java 抛 RuntimeException 后 process 整体 die。
#   根因：AOSP <code>frameworks/base/graphics/java/android/graphics/fonts/SystemFonts.java:50</code>
#         硬编码 <code>FONTS_XML = "/system/etc/fonts.xml"</code>。OH 设备 /system/etc/
#         没有该文件（OH 自己用别的字体配置机制）。当 G2.14q Path A 让 libhwui
#         走 AOSP 原版 register_android_graphics_Typeface 后，Typeface.<clinit> 真
#         路径会读 /system/etc/fonts.xml，缺则 throw FNFE。
#   血训：2026-05-02 一次会话内三次卡这个：
#         (1) Path A 部署后没意识到 fonts.xml 缺；
#         (2) 部署 fonts.xml 到 /system/android/etc/ + symlink 后，rollback 时
#             rm /system/etc/fonts.xml 把 symlink 删了，但留了 /system/android/etc/;
#         (3) 重新 path A 测时 SystemFonts 又 ENOENT，不知道 symlink 已被自己删。
#   措施：双路径部署 + symlink。
#         (a) 真文件部署到 <code>/system/android/etc/fonts.xml</code>（独立于 OH，
#             符合"Android 资源放 /system/android/" 的项目惯例）。
#         (b) 创建 symlink <code>/system/etc/fonts.xml -&gt; /system/android/etc/fonts.xml</code>
#             满足 AOSP SystemFonts.java 硬编码路径。
#         (c) restorecon 两个路径让 SELinux label 是 system_file:s0。
#         (d) fonts.xml 内容引用的字体文件位于 /system/fonts/（OH 设备已有
#             HarmonyOS_Sans*.ttf），不需另外推字体二进制。
#   验证：parent appspawn-x preload 期间 hilog 不再有 SystemFonts FileNotFoundException；
#         Typeface.<clinit> 正常完成；preInitTypefaceDefault 不抛。
#         （memory: G2.14q + G2.14r typeface chain）
#         （文件: aosp_patches/data/fonts/fonts.xml）
#
# [P-10] appspawn_x.cfg 必须用 ondemand:true 模式，禁止改 disabled:1/start-mode/critical
#   现象：AMS APPSPAWN_CLIENT.connect /dev/unix/socket/AppSpawnX 报 ECONNREFUSED
#         (errno=111) 4 次重试均失败；child fork 不发生；aa start 卡死。
#         /proc/net/unix 显示 socket 存在但 Flags 无 __SO_ACCEPTCON (即未 listen)。
#   根因：OH init `init_service_socket.c:CreateSocketForService()` 只在
#         `IsOnDemandService(service)` 为 true 时才调用 `listen()`。当 cfg 写
#         `disabled:1` + `start-mode:condition` (或 critical:[0]) 时，init 仅
#         socket()+bind() 不 listen()，AMS connect 必然 ECONNREFUSED。
#   血训：本 cfg 在 2026-04-22 → 04-29 内被改成 disabled+condition 三次（每次
#         都说"暂停 init service 工作"），每次都让 aa start 不通。每次再切
#         回 ondemand 都要追到 init 源码 listen() 这一行才确认。
#   措施：cfg 必须保持 `"ondemand": true`，且**不能**同时含 `critical`、
#         `disabled:1`、`start-mode` 三者中任何一个（OH init parser 会把
#         critical+ondemand 视为冲突直接拒绝服务）。允许的形态：
#           "ondemand": true,
#           "secon": "u:r:appspawn:s0",
#           // 不要写 critical / disabled / start-mode
#   验证：reboot 后 `cat /proc/net/unix | grep AppSpawnX` Flags 应为 00010000
#         (__SO_ACCEPTCON)；`pidof appspawn-x` 应为空（等 connect 才启动）。
#         AMS connect 后 appspawn-x 进程出现 + 进入 Phase 4 event loop。
#         （memory: project_b40_verify_socket_blocker.md）
# ============================================================================
#
# deploy_to_dayu200.sh — runs on local Windows where hdc is installed
#
# Pushes build artifacts from the canonical `out/` subdirs directly to a
# connected DAYU200 device via hdc. No intermediate staging directory.
#
# This script was rewritten on 2026-04-14 to remove the dependency on
# `out/deploy_package/` — see CLAUDE.md "废弃目录清单". Every file pushed is
# resolved against its authoritative source dir (out/adapter, out/aosp_lib,
# out/aosp_fwk, out/oh-service, out/boot-image, out/app, or
# framework/appspawn-x/config), which eliminates the "stale deploy_package"
# class of inconsistencies.
#
# Prerequisites:
#   1. Device connected and visible via `hdc list targets`
#   2. ECS built and synced to local via `bash build/pull_ecs_artifacts.sh`
#      (or individual scp commands) so `D:/code/adapter/out/...` is fresh.
#
# Usage:
#   bash deploy_to_dayu200.sh                    # full deploy
#   bash deploy_to_dayu200.sh --dry-run          # print commands only
#   bash deploy_to_dayu200.sh --skip-libskia     # skip the 30-50 MB libskia push
#   bash deploy_to_dayu200.sh --uninstall        # remove all deployed files
#
# APK install is intentionally NOT part of this script.  Deploy only writes
# system-side artifacts (boot image / native .so / jars / cfg).  Install the
# Hello World APK separately once BMS is up:
#     hdc file send out/app/HelloWorld.apk /data/local/tmp/HelloWorld.apk
#     hdc shell bm install -p /data/local/tmp/HelloWorld.apk

set -e

# Source root paths — authoritative, not deploy_package
ADAPTER_ROOT=${ADAPTER_ROOT:-D:/code/adapter}
OUT=${OUT:-$ADAPTER_ROOT/out}
FW_CFG=${FW_CFG:-$ADAPTER_ROOT/framework/appspawn-x/config}

HDC=${HDC:-hdc}

DRY_RUN=0
SKIP_LIBSKIA=0
UNINSTALL=0
SKIP_TEST_LIBS=0
ONLY_FILES=""
ONLY_FILES_BN=()

for arg in "$@"; do
    case "$arg" in
        --dry-run)        DRY_RUN=1 ;;
        --skip-libskia)   SKIP_LIBSKIA=1 ;;
        --uninstall)      UNINSTALL=1 ;;
        # --skip-test-libs: skip the 3 .so currently under boot-blocker investigation
        # (liboh_adapter_bridge.so, libapk_installer.so, libbms.z.so). Used for the
        # incremental boot-bisection: deploy everything else first, verify boot,
        # then add these 3 back one at a time. Note: libappexecfwk_common.z.so is
        # also a "test lib" but the deploy script never pushes it (OH ROM default).
        --skip-test-libs) SKIP_TEST_LIBS=1 ;;
        # --only-files=<csv>: inclusion-mode partial deploy. Each entry can be a
        # basename (e.g. "liboh_adapter_bridge.so"), a full local path, or a
        # path relative to $OUT or $ADAPTER_ROOT — basename is what matters.
        # All 8 stages still execute (so mount/mkdir/chcon/restorecon/symlink
        # side-effects are preserved per [P-1]…[P-13]) but push_file silently
        # skips any source whose basename is NOT in the list.  Boot image
        # md5-verification loop is also skipped in this mode (user owns the
        # list — the loop's auto-recovery would otherwise abort on segments
        # the user intentionally left out).
        # Memory: avoids exclusion-mode "skip a few inside full deploy" drift —
        # the safe bound is "explicitly enumerate what to push".
        --only-files=*)   ONLY_FILES="${arg#*=}" ;;
    esac
done

# Parse --only-files CSV into basename array (for filtering inside push_file).
if [ -n "$ONLY_FILES" ]; then
    IFS=',' read -ra _ONLY_RAW <<< "$ONLY_FILES"
    for _e in "${_ONLY_RAW[@]}"; do
        # trim whitespace
        _e=$(echo "$_e" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
        [ -z "$_e" ] && continue
        ONLY_FILES_BN+=("$(basename "$_e")")
    done
    echo "[ONLY-FILES] inclusion mode: ${#ONLY_FILES_BN[@]} basenames"
    for _bn in "${ONLY_FILES_BN[@]}"; do echo "    + $_bn"; done
fi

run() {
    if [ $DRY_RUN -eq 1 ]; then
        echo "  [DRY] $*"
    else
        MSYS_NO_PATHCONV=1 "$@" || { echo "  FAIL: $*"; return 1; }
    fi
}

to_win() {
    local p="$1"
    if command -v cygpath >/dev/null 2>&1; then
        cygpath -w "$p"
    else
        echo "$p"
    fi
}

push_file() {
    local local_path="$1"
    local device_path="$2"
    if [ ! -f "$local_path" ]; then
        echo "  SKIP (not found): $local_path"
        return 0
    fi
    # --only-files filter: silently skip if basename not in the inclusion list.
    # We rely on basename matching so the same flag value works whether the
    # user passes "boot.art", "out/boot-image/boot.art", or an absolute path.
    if [ ${#ONLY_FILES_BN[@]} -gt 0 ]; then
        local _bn=$(basename "$local_path")
        local _match=0
        for _x in "${ONLY_FILES_BN[@]}"; do
            if [ "$_x" = "$_bn" ]; then _match=1; break; fi
        done
        if [ $_match -eq 0 ]; then
            return 0
        fi
    fi
    local win_path=$(to_win "$local_path")
    if [ $DRY_RUN -eq 1 ]; then
        echo "  [DRY] hdc file send $win_path $device_path"
    else
        local out
        out=$(MSYS_NO_PATHCONV=1 $HDC file send "$win_path" "$device_path" 2>&1)
        if echo "$out" | grep -qi 'fail\|error'; then
            echo "  FAIL: $(basename $local_path) — $out"
            return 1
        else
            echo "  $(basename $local_path) → $device_path"
        fi
    fi
}

# push_many "<SRC_DIR>" "<DST_DIR>" <file1> <file2> ...
push_many() {
    local src="$1"; local dst="$2"; shift 2
    for name in "$@"; do
        push_file "$src/$name" "$dst/$name"
    done
}

# push_pattern "<SRC_DIR>" "<DST_DIR>" "<shell-glob>" [exclude1 exclude2 ...]
# Pushes every file in src that matches glob; basename preserved on dst.
# Trailing names are excluded (basename match).
push_pattern() {
    local src="$1"; local dst="$2"; local pat="$3"; shift 3
    local any=0
    for f in "$src"/$pat; do
        [ -f "$f" ] || continue
        local bn=$(basename "$f")
        local skip=0
        for ex in "$@"; do
            if [ "$bn" = "$ex" ]; then skip=1; break; fi
        done
        if [ $skip -eq 1 ]; then
            echo "  EXCLUDE $bn (deployed elsewhere)"
            continue
        fi
        push_file "$f" "$dst/$bn"
        any=1
    done
    [ $any -eq 0 ] && echo "  (nothing matched $src/$pat)"
    return 0
}

# ==========================================================================
# Pre-flight
# ==========================================================================

echo "=========================================="
echo "OH-Adapter → DAYU200 deployment"
echo "  sources:"
echo "    $OUT/adapter"
echo "    $OUT/aosp_lib"
echo "    $OUT/aosp_fwk"
echo "    $OUT/oh-service"
echo "    $OUT/boot-image"
echo "    $OUT/app"
echo "    $FW_CFG"
echo "=========================================="

if ! command -v "$HDC" >/dev/null 2>&1; then
    echo "ERROR: hdc not found on PATH. Install OpenHarmony hdc tool."
    exit 1
fi

if [ $DRY_RUN -eq 0 ]; then
    DEV=$($HDC list targets 2>&1 | head -1 || true)
    if [ -z "$DEV" ] || echo "$DEV" | grep -qi 'empty\|none\|no.*device'; then
        echo "ERROR: No DAYU200 device detected"
        echo "  hdc list targets output: $DEV"
        exit 1
    fi
    echo "Device: $DEV"
fi

# ==========================================================================
# Uninstall mode
# ==========================================================================
if [ $UNINSTALL -eq 1 ]; then
    echo ""
    echo "[UNINSTALL] Removing deployed files..."
    run $HDC shell bm uninstall -n com.example.helloworld
    run $HDC shell rm -rf /system/android
    run $HDC shell rm -f /system/bin/appspawn-x
    run $HDC shell rm -f /system/lib/liboh_adapter_bridge.so /system/lib/libapk_installer.so
    run $HDC shell rm -f /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json
    echo "[UNINSTALL] Done. (OH service patches NOT removed — these are upgrade-in-place)"
    echo "[UNINSTALL] Reboot device to fully clean up: hdc shell reboot"
    exit 0
fi

# ==========================================================================
# Make /system writable + create target dirs
# ==========================================================================
echo ""
echo "[1/8] Mounting /system as read-write + creating target dirs..."
run $HDC shell mount -o remount,rw / || true
run $HDC shell mount -o remount,rw /system || true
run $HDC shell mkdir -p /system/lib/platformsdk
run $HDC shell mkdir -p /system/android/lib
run $HDC shell mkdir -p /system/android/framework/arm
run $HDC shell mkdir -p /system/android/etc/icu
# /system/etc/init is a factory OH dir (holds all system service .cfg files); do not mkdir

# ==========================================================================
# 2. Adapter binaries (appspawn-x + JNI bridges)
# ==========================================================================
echo ""
echo "[2/8] Adapter binaries (from out/adapter/)..."
push_file "$OUT/adapter/appspawn-x" /system/bin/appspawn-x
if [ $SKIP_TEST_LIBS -eq 1 ]; then
    echo "  SKIP liboh_adapter_bridge.so + libapk_installer.so (--skip-test-libs)"
else
    # 2026-05-09 G2.14aj: libinstalls.z.so 加入（之前缺，per DEPLOY_SOP.md 文末）。
    # libinstalls 是 adapter 的 BMS install 路径助手，缺则 bm install 链路某些
    # ContentProvider 写文件操作 fallback 失败。
    push_many "$OUT/adapter" /system/lib \
        liboh_adapter_bridge.so \
        libapk_installer.so \
        libinstalls.z.so
fi

# ==========================================================================
# 3. OH service patches (from out/oh-service/)
# ==========================================================================
echo ""
echo "[3/8] OH service patches (from out/oh-service/)..."
# Top-level lib — match OH native layout (libbms, libskia_canvaskit live here on V7)
if [ $SKIP_TEST_LIBS -eq 1 ]; then
    push_many "$OUT/oh-service" /system/lib \
        libwms.z.so \
        libappms.z.so \
        libappspawn_client.z.so
    echo "  SKIP libbms.z.so (--skip-test-libs)"
else
    # 2026-05-09 G2.14aj: libappspawn_client.z.so 加入（之前缺，per DEPLOY_SOP.md
    # 文末）。该 .so 是 AppMS 与 appspawn-x 通信的 client，含 4/30 sepolicy +
    # namespace patch；不推则 AppMS 用 ROM 旧版 → token TLV 序列化路径可能错位。
    push_many "$OUT/oh-service" /system/lib \
        libwms.z.so \
        libappms.z.so \
        libbms.z.so \
        libappspawn_client.z.so
fi
# libbms.z.so dual-path: OH 7.0.0.18 ships TWO physical copies
# (/system/lib/libbms.z.so + /system/lib/platformsdk/libbms.z.so).
# foundation linker resolves /system/lib/ FIRST, so platformsdk version is
# silently shadowed when only platformsdk is updated. Single-physical-file
# rule: keep /system/lib/ as authoritative + symlink platformsdk.
# Memory: reference_libbms_dual_path.md
if [ $SKIP_TEST_LIBS -eq 0 ]; then
    run $HDC shell "rm -f /system/lib/platformsdk/libbms.z.so && ln -sf /system/lib/libbms.z.so /system/lib/platformsdk/libbms.z.so"
    echo "  libbms.z.so -> /system/lib/libbms.z.so (symlink in platformsdk/)"
fi
# platformsdk — OH native layout for these four
push_many "$OUT/oh-service" /system/lib/platformsdk \
    libabilityms.z.so \
    libscene_session_manager.z.so \
    libscene_session.z.so \
    librender_service_base.z.so

# /system/lib/ — librender_service.z.so is the OH RS server-side library
# (uni-render visitor, processor, composer adapter). G2.14au r5 adds
# probes here.  Different from librender_service_base.z.so which lives in
# platformsdk/ above.  Device path is /system/lib/librender_service.z.so
# (per device ls; NOT platformsdk).
push_many "$OUT/oh-service" /system/lib \
    librender_service.z.so

# libskia_canvaskit.z.so (22 MB on V7, lives in /system/lib/); toggle with --skip-libskia
if [ $SKIP_LIBSKIA -eq 1 ]; then
    echo "  SKIP libskia_canvaskit.z.so (--skip-libskia)"
else
    push_file "$OUT/oh-service/libskia_canvaskit.z.so" /system/lib/libskia_canvaskit.z.so
fi

# ==========================================================================
# 4. AOSP Native libs (from out/aosp_lib/ + adapter shims)
# ==========================================================================
echo ""
echo "[4/8] AOSP Native libs (from out/aosp_lib/ + adapter shims)..."
# All cross-compiled .so from aosp_lib (authoritative source for libart.so,
# libart_runtime_stubs.so, libbionic_compat.so, libartbase/palette, libbase,
# liblog, libcutils, libutils, libnativehelper, libsigchain, libdexfile,
# libvixl, liblz4, libziparchive, libandroid_runtime, libhwui, libandroidfw,
# libft2, libharfbuzz_ng, libicu*, libminikin, etc.)
# Exclude OH-named libs (.z.so suffix) that incidentally landed in aosp_lib —
# they belong in /system/lib/platformsdk/ (OH canonical location). Any Android
# namespace process needing them gets a symlink in section [8/8].
# 2026-05-09 G2.14ai: libappexecfwk_common.z.so is an OH library (built by
# OH bundle_framework subsystem), not AOSP. Source corrected from out/aosp_lib/
# to out/oh-service/ — aosp_lib never had this file (push_file silently
# SKIP'd before this fix), so the prior behavior was an accidental no-op
# letting the OH ROM-bundled version stay in place. push_pattern's EXCLUDE
# entry kept here in form of an empty default — no aosp_lib *.so currently
# matches OH .z.so naming, so EXCLUDE list is now empty.
push_pattern "$OUT/aosp_lib" /system/android/lib "*.so"
# Push OH-named lib from oh-service (its proper home per Build Division Principle)
# to canonical /system/lib/platformsdk/. android-lib symlink created in [8/8].
push_file "$OUT/oh-service/libappexecfwk_common.z.so" /system/lib/platformsdk/libappexecfwk_common.z.so
# liboh_hwui_shim.so lives in out/adapter/ (built by compile_hwui_shims.sh,
# not cross_compile_arm32.sh).
# 2026-05-09 G2.14aj: dual-path 推到 /system/android/lib/ + /system/lib/，与 [P-3]
# 一致；之前只推 /system/android/lib/，OH systemscence linker 解析顺序让
# /system/lib/ 旧版 shadow 新版（同 liboh_android_runtime.so dual-path 原因）。
push_file "$OUT/adapter/liboh_hwui_shim.so" /system/android/lib/liboh_hwui_shim.so
push_file "$OUT/adapter/liboh_hwui_shim.so" /system/lib/liboh_hwui_shim.so
# 2026-05-09 G2.14aj: liboh_skia_rtti_shim.so 加入（之前缺，per DEPLOY_SOP.md 文末）。
# 它是 libhwui 的 DT_NEEDED；漏推则 libhwui 直接 dlopen 失败 → UI 全挂。
# Dual-path 同 liboh_hwui_shim 原因。
push_file "$OUT/adapter/liboh_skia_rtti_shim.so" /system/android/lib/liboh_skia_rtti_shim.so
push_file "$OUT/adapter/liboh_skia_rtti_shim.so" /system/lib/liboh_skia_rtti_shim.so
# liboh_android_runtime.so — adapter's progressive replacement for libandroid_runtime
# (Stage 3 framework preload calls registerNatives from this). Lives in out/adapter/.
# Dual-path required (memory: reference_liboh_android_runtime_dual_path.md):
# OH systemscence linker namespace resolves /system/lib/ BEFORE /system/android/lib/,
# so a stale /system/lib/ copy silently shadows new /system/android/lib/ updates.
# Push the same file to BOTH paths and chcon both to system_lib_file.
push_file "$OUT/adapter/liboh_android_runtime.so" /system/android/lib/liboh_android_runtime.so
push_file "$OUT/adapter/liboh_android_runtime.so" /system/lib/liboh_android_runtime.so
# 2026-05-09 G2.14aj: chcon 范围扩展到所有 dual-path adapter shims（liboh_hwui_shim
# + liboh_skia_rtti_shim + liboh_android_runtime），避免 [P-1] flock 被拒。
run $HDC shell chcon u:object_r:system_lib_file:s0 \
    /system/android/lib/liboh_android_runtime.so /system/lib/liboh_android_runtime.so \
    /system/android/lib/liboh_hwui_shim.so /system/lib/liboh_hwui_shim.so \
    /system/android/lib/liboh_skia_rtti_shim.so /system/lib/liboh_skia_rtti_shim.so \
    2>/dev/null || true

# ==========================================================================
# 5. AOSP Framework jars (from out/aosp_fwk/ + out/adapter/)
# ==========================================================================
echo ""
echo "[5/8] AOSP Framework jars (from out/aosp_fwk/ + out/adapter/)..."
# 2026-04-30: B.41 抛弃 framework.jar 的决策已回退 — framework.jar 重新作为 BCP
# 成员部署到 /system/android/framework/，从 out/aosp_fwk/ 推送（AOSP-built，含
# L5 reflection patches）。oh-adapter-framework.jar 仍然唯一来源 out/adapter/
# （B.41 这一半决策保留，避免双源漂移）。
# AOSP-built core jars (no adapter modification) — out/aosp_fwk/
push_many "$OUT/aosp_fwk" /system/android/framework \
    framework.jar \
    framework-classes.dex.jar \
    framework-res-package.jar \
    core-oj.jar \
    core-libart.jar \
    core-icu4j.jar \
    okhttp.jar \
    bouncycastle.jar \
    apache-xml.jar
# framework-res.apk: AppSpawnXInit.java:696 + android_content_res_ApkAssets.cpp:68
#硬编码读 /system/android/framework/framework-res.apk（带 .apk 后缀）。设备上这个
# 文件实际由 framework-res-package.jar 提供（同 md5 / 同内容、仅后缀不同）。
#
# 重要：必须 cp 真文件 — 不能用 symlink。ApkAssets.nativeLoad → libandroidfw
# ZipArchive open() 内部对 symlink 失败（OH 的 ZipArchive 实现里某处 fstat /
# realpath 链对 symlink 处理与真文件不一致，2026-05-09 实测 InitSysAM
# loadFromPath 用 symlink 抛 IOException）。改成真文件 cp 后 InitSysAM
# post-init sSystem apkPaths=[framework-res.apk] OK。
# 缺这文件会让 InitSysAM loadFromPath 抛 IOException → sSystemApkAssets 仍
# length=0 → AssetManager2::FindEntryInternal NULL deref SEGV at fault addr
# 0x86c（命中 helloworld <pre-initialize> 主线程）。
# 实证：B.25/B.26 修法假设此路径存在；2026-05-09 helloworld pid 2088 SIGSEGV 真因。
run $HDC shell "rm -f /system/android/framework/framework-res.apk; cp /system/android/framework/framework-res-package.jar /system/android/framework/framework-res.apk; chmod 644 /system/android/framework/framework-res.apk"
# oh-adapter-framework.jar lives in out/adapter/ (built separately)
push_file "$OUT/adapter/oh-adapter-framework.jar" /system/android/framework/oh-adapter-framework.jar
# oh-adapter-runtime.jar — non-BCP adapter jar loaded via PathClassLoader
# (contains com.android.internal.os.AppSpawnXInit); Stage 3 framework preload entry.
push_file "$OUT/adapter/oh-adapter-runtime.jar" /system/android/framework/oh-adapter-runtime.jar
# adapter-mainline-stubs.jar — G2.8 (2026-04-30): single canonical source
# is $OUT/adapter/, NOT $OUT/aosp_fwk/.  Pre-G2.8 the script omitted this
# jar entirely; gen_boot_image.sh manually cp'd it to aosp_fwk before
# reading.  Now deployed alongside other adapter jars.
push_file "$OUT/adapter/adapter-mainline-stubs.jar" /system/android/framework/adapter-mainline-stubs.jar

# ==========================================================================
# 6. Boot image (from out/boot-image/)
# ==========================================================================
echo ""
echo "[6/8] Boot image (from out/boot-image/)..."
push_many "$OUT/boot-image" /system/android/framework/arm \
    boot.art boot.oat boot.vdex \
    boot-core-libart.art boot-core-libart.oat boot-core-libart.vdex \
    boot-core-icu4j.art boot-core-icu4j.oat boot-core-icu4j.vdex \
    boot-okhttp.art boot-okhttp.oat boot-okhttp.vdex \
    boot-bouncycastle.art boot-bouncycastle.oat boot-bouncycastle.vdex \
    boot-apache-xml.art boot-apache-xml.oat boot-apache-xml.vdex \
    boot-adapter-mainline-stubs.art boot-adapter-mainline-stubs.oat boot-adapter-mainline-stubs.vdex \
    boot-framework.art boot-framework.oat boot-framework.vdex \
    boot-oh-adapter-framework.art boot-oh-adapter-framework.oat boot-oh-adapter-framework.vdex

# Boot image MD5 verification (truncation prevention).
# 2026-04-21 incident: boot-framework.art got truncated 23.7MB → 9.2MB silently
# (suspected hdc race or disk pressure). libart Image header validation rejects
# the truncated file → "Image file truncated: 9209442 vs. 23576124" → ART crash.
# Symptom is identical to feedback_art_bootimage_coupling.md (compile flag
# mismatch) so easy to misdiagnose. Always verify md5 after boot image push.
if [ ${#ONLY_FILES_BN[@]} -gt 0 ]; then
    echo "  Verifying boot image md5 (only-files mode — verifying listed segments only)..."
else
    echo "  Verifying boot image md5 (truncation guard)..."
fi
for bf in boot.art boot.oat boot.vdex \
          boot-core-libart.art boot-core-libart.oat boot-core-libart.vdex \
          boot-core-icu4j.art boot-core-icu4j.oat boot-core-icu4j.vdex \
          boot-okhttp.art boot-okhttp.oat boot-okhttp.vdex \
          boot-bouncycastle.art boot-bouncycastle.oat boot-bouncycastle.vdex \
          boot-apache-xml.art boot-apache-xml.oat boot-apache-xml.vdex \
          boot-adapter-mainline-stubs.art boot-adapter-mainline-stubs.oat boot-adapter-mainline-stubs.vdex \
          boot-framework.art boot-framework.oat boot-framework.vdex \
          boot-oh-adapter-framework.art boot-oh-adapter-framework.oat boot-oh-adapter-framework.vdex; do
    # In only-files mode, skip segments the user did NOT list — re-push retry
    # would silently no-op (push_file filters them out) and the loop would
    # then abort with FATAL on a file the user explicitly excluded.
    if [ ${#ONLY_FILES_BN[@]} -gt 0 ]; then
        _seg_match=0
        for _x in "${ONLY_FILES_BN[@]}"; do
            if [ "$_x" = "$bf" ]; then _seg_match=1; break; fi
        done
        if [ $_seg_match -eq 0 ]; then
            continue
        fi
    fi
    local_md5=$(md5sum "$OUT/boot-image/$bf" 2>/dev/null | awk '{print $1}')
    device_md5=$($HDC shell "md5sum /system/android/framework/arm/$bf 2>/dev/null" | awk '{print $1}' | tr -d '\r\n')
    if [ "$local_md5" != "$device_md5" ]; then
        echo "  ❌ MD5 MISMATCH $bf: local=$local_md5 device=$device_md5"
        echo "  Re-pushing $bf..."
        push_file "$OUT/boot-image/$bf" "/system/android/framework/arm/$bf"
        device_md5_retry=$($HDC shell "md5sum /system/android/framework/arm/$bf 2>/dev/null" | awk '{print $1}' | tr -d '\r\n')
        if [ "$local_md5" != "$device_md5_retry" ]; then
            echo "  ❌ FATAL: $bf md5 still mismatch after retry; aborting"
            exit 1
        fi
        echo "  ✓ $bf re-push verified"
    fi
done
if [ ${#ONLY_FILES_BN[@]} -eq 0 ]; then
    echo "  All 27 boot image files md5-verified (9 segments × 3 ext)"
fi

# ==========================================================================
# 6.5. ICU data (from out/aosp_fwk/icu/ — required by Runtime::InitNativeMethods
#      u_init() at Phase 2; env ICU_DATA=/system/android/etc/icu from cfg)
# ==========================================================================
echo ""
echo "[6.5/8] ICU data (from out/aosp_fwk/icu/)..."
push_file "$OUT/aosp_fwk/icu/icudt72l.dat" /system/android/etc/icu/icudt72l.dat

# ==========================================================================
# 6.6. fonts.xml (G2.14r 2026-05-02 sediment) — required by AOSP
#      SystemFonts.java when libhwui's real Typeface init runs (post-G2.14q).
#      ⚠ [P-12] 反复犯错: 不能只删 /system/etc/fonts.xml symlink 而留真文件;
#      下次 Typeface.<clinit> 还会 ENOENT。详见本文件顶部 [P-12]。
#      AOSP SystemFonts.java:50 hardcodes FONTS_XML="/system/etc/fonts.xml";
#      adapter authoritative path = /system/android/etc/fonts.xml + symlink
#      /system/etc/fonts.xml -> /system/android/etc/fonts.xml.
# ==========================================================================
FONTS_XML_SRC="$ADAPTER_ROOT/aosp_patches/data/fonts/fonts.xml"
if [ -f "$FONTS_XML_SRC" ]; then
    echo ""
    echo "[6.6/8] fonts.xml (Android-side, references OH /system/fonts/*.ttf)..."
    push_file "$FONTS_XML_SRC" /system/android/etc/fonts.xml
    # 真文件 dual-path: /system/android/etc/ + /system/etc/ 都放真文件 (不用 symlink,
    # symlink 自身的 SELinux relabel 在 OH 上不可靠)。
    run $HDC shell "rm -f /system/etc/fonts.xml; cp /system/android/etc/fonts.xml /system/etc/fonts.xml"
    # ⚠ [P-13] 反复犯错: 必须 chcon system_fonts_file:s0 (不能依赖 restorecon)
    #   restorecon 会按 file_contexts 给 /system/etc/fonts.xml 默认 label =
    #   system_etc_file:s0, 但 OH normal_hap App 域禁读 → setSystemFontMap NPE。
    #   system_fonts_file:s0 是 OH /system/fonts/*.ttf 的 label, normal_hap 可读。
    run $HDC shell "chcon u:object_r:system_fonts_file:s0 /system/etc/fonts.xml /system/android/etc/fonts.xml"
    echo "  /system/etc/fonts.xml + /system/android/etc/fonts.xml (chcon system_fonts_file:s0)"
else
    echo "[6.6/8] SKIP fonts.xml (source not found at $FONTS_XML_SRC)"
fi

# ==========================================================================
# 7. appspawn-x init config (from framework/appspawn-x/config/)
# ==========================================================================
echo ""
echo "[7/8] appspawn-x init configs + SELinux file_contexts..."
# ⚠ 反复犯错 [P-10]: appspawn_x.cfg 必须保持 "ondemand": true (无 critical / disabled / start-mode)
#   — OH init 只在 ondemand 模式下 listen() AppSpawnX socket; 否则 AMS connect ECONNREFUSED。
#   验证: reboot 后 `cat /proc/net/unix | grep AppSpawnX` Flags 应为 00010000(__SO_ACCEPTCON)。
#   详见本文件顶部 [P-10] 注释 + project_b40_verify_socket_blocker.md。
push_file "$FW_CFG/appspawn_x.cfg" /system/etc/init/appspawn_x.cfg
push_file "$FW_CFG/appspawn_x_sandbox.json" /system/etc/appspawn_x_sandbox.json
# 2026-05-09 G2.14aj: ld-musl-namespace-arm.ini 加入（之前缺，per DEPLOY_SOP.md
# 文末 + [P-3] dual-path 关联）。该 ini 是 OH musl linker 的 namespace 配置，
# 默认 namespace lib.paths 不含 /system/android/lib/ → appspawn-x dlopen libart
# 等 AOSP native lib 必败 → appspawn-x 起不来（layer 2 namespace 前置）。
# 来源：ohos_patches/third_party/musl/config/ld-musl-namespace-arm.ini（adapter
# 维护，OH ROM 自带版本不含 /system/android/lib/）。
push_file "$ADAPTER_ROOT/ohos_patches/third_party/musl/config/ld-musl-namespace-arm.ini" \
    /system/etc/ld-musl-namespace-arm.ini
# SELinux file_contexts — 给 /system/bin/appspawn-x 打 appspawn_exec label
# 不推则 init domain transition 失败, execv EACCES (errno 13).
# 本机来源是 ECS 编译产物 (factory 618 行 + adapter 3 行 = 621 行); adapter diff
# 在 ohos_patches/selinux_adapter/file_contexts.patch.
push_file "$OUT/oh-service/file_contexts" /system/etc/selinux/targeted/contexts/file_contexts

# ==========================================================================
# 8. Permissions + device-side symlink
# ==========================================================================
echo ""
echo "[8/8] Setting permissions + device-side symlinks..."
run $HDC shell chmod 755 /system/bin/appspawn-x || true
# 2026-05-09 G2.14aj: chmod batch 扩展 — 加入新 6 件文件 (libinstalls / libappspawn_client /
# liboh_hwui_shim 第二副本 / liboh_skia_rtti_shim 双副本) 及 ld-musl-namespace-arm.ini。
run $HDC shell chmod 644 /system/lib/libapk_installer.so /system/lib/liboh_adapter_bridge.so /system/lib/libinstalls.z.so /system/lib/libappms.z.so /system/lib/libwms.z.so /system/lib/libbms.z.so /system/lib/libskia_canvaskit.z.so /system/lib/libappspawn_client.z.so /system/lib/liboh_hwui_shim.so /system/lib/liboh_skia_rtti_shim.so || true
run $HDC shell chmod 644 /system/lib/platformsdk/libabilityms.z.so /system/lib/platformsdk/librender_service_base.z.so /system/lib/platformsdk/libscene_session.z.so /system/lib/platformsdk/libscene_session_manager.z.so || true
run $HDC shell "chmod 644 /system/android/lib/*.so" || true
run $HDC shell chmod 644 /system/etc/ld-musl-namespace-arm.ini /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json || true
# REMOVED 2026-04-22: 原 chcon -R /system/android/lib hook 在"设备已运行状态"下
# 会 relabel 正被 render_service 等进程 mmap 的文件，触发 SELinux 再评估 →
# render_service SIGSEGV → init 级联 SIGKILL foundation/allocator_host/composer_host
# → 整个系统 SA 死光。正确做法：依赖 OH file_contexts patch
# (ohos_patches/appspawn/apply_appspawnx_sepolicy_and_namespace.py 里的
# /system/android/lib(/.*)? → system_lib_file 正则)，走 selinux_adapter 重编流程。
# 若首次 deploy 缺这个 label、appspawn-x init service SIGSEGV，reboot 后会在
# file_contexts 生效后自动正确，无需 deploy 时 chcon。
run $HDC shell "chmod 644 /system/android/framework/*.jar" || true
run $HDC shell "chmod 644 /system/android/framework/arm/*.art /system/android/framework/arm/*.oat /system/android/framework/arm/*.vdex" || true
run $HDC shell chmod 644 /system/etc/selinux/targeted/contexts/file_contexts || true

# restorecon: 按新 file_contexts 规则给 push 过的文件打 SELinux label
# 必须在 file_contexts push 之后（前一步已推）才执行, 否则读不到 adapter 3 条新规则
# 注意: OH toybox restorecon 不支持 -R, 用 find -exec 替代 (覆盖 dir + 文件 + symlink)
run $HDC shell restorecon /system/bin/appspawn-x || true
run $HDC shell "find /system/android/lib -exec restorecon {} \;" || true
echo "  restorecon /system/bin/appspawn-x + find /system/android/lib -exec restorecon"

# B.40 (2026-04-29): boot image segments under /system/android/framework/arm/
# MUST be labeled u:object_r:system_lib_file:s0 (not the default system_file
# that restorecon assigns). The appspawn:s0 SELinux domain only allows
# `file lock` on system_lib_file targets — without this, ART JNI_CreateJavaVM
# in init-spawned appspawn-x aborts in Phase 2 (boot.art flock denied,
# permissive=0). Manual `appspawn-x` from shell:s0/su:s0 works because shell
# has broader policy; init-service mode strictly requires the label.
#
# Symptom of missing this step: appspawn-x parent SIGABRT respawn storm
# (~700ms per pid) immediately after "Phase 2: Initializing Android Runtime
# (ART VM)..." log line. dmesg shows
#   avc: denied { lock } for path="/system/android/framework/arm/boot.art"
#   tcontext=u:object_r:system_file:s0 permissive=0
#
# Hard fix: explicit chcon to system_lib_file. Don't rely on restorecon —
# OH file_contexts has no rule for /system/android/framework/arm/* and so
# assigns the default system_file label.
run $HDC shell "for f in /system/android/framework/arm/boot*.art /system/android/framework/arm/boot*.oat /system/android/framework/arm/boot*.vdex; do case \$f in *.b40_pre|*.orig*|*.moved*|*.pre_b11*) ;; *) chcon u:object_r:system_lib_file:s0 \$f 2>/dev/null;; esac; done" || true
echo "  chcon system_lib_file on boot image segments"

# libsigchain.so dlopen() needs "libc.so" (after the binary patch); map to musl loader
run $HDC shell ln -sf /lib/ld-musl-arm.so.1 /system/lib/libc_musl.so || true
echo "  libc_musl.so -> /lib/ld-musl-arm.so.1"
# libziparchive.so / libprofile.so have NEEDED libshared_libz.z.so (OH zlib SONAME).
# OH keeps it at /system/lib/chipset-sdk-sp/ which is not in the android/lib
# LD_LIBRARY_PATH. Symlink so musl loader finds it. Sedimented 2026-04-14
# after Phase 1 verification failed with "inflate: symbol not found" runtime
# errors on libziparchive.
# OH device's ln -sf does NOT atomically replace if target is a regular file
# (busybox/toybox on this build silently keeps the existing file). Always rm -f
# before ln -sf to guarantee the symlink takes effect.
run $HDC shell "rm -f /system/android/lib/libshared_libz.z.so; ln -sf /system/lib/chipset-sdk-sp/libshared_libz.z.so /system/android/lib/libshared_libz.z.so" || true
echo "  libshared_libz.z.so -> /system/lib/chipset-sdk-sp/libshared_libz.z.so"
# libappexecfwk_common.z.so — single physical copy in /system/lib/platformsdk/;
# Android namespace processes reach it via this symlink. Avoids version drift.
run $HDC shell "rm -f /system/android/lib/libappexecfwk_common.z.so; ln -sf /system/lib/platformsdk/libappexecfwk_common.z.so /system/android/lib/libappexecfwk_common.z.so" || true
echo "  libappexecfwk_common.z.so -> /system/lib/platformsdk/libappexecfwk_common.z.so"

# G2.14aa (2026-05-08): hwui RenderThread.cpp::ASurfaceControlFunctions ctor
# 在 dlopen("libandroid.so", RTLD_NOW | RTLD_NODELETE) 之后 dlsym 13 个 NDK
# 符号 (ASurfaceControl_create / ASurfaceTransaction_create / ...) 并
# LOG_ALWAYS_FATAL_IF。这些符号的真桥实现已编入 liboh_android_runtime.so
# (framework/android-runtime/src/android_view_SurfaceControl.cpp)。device 上
# libandroid.so 必须 symlink 到 liboh_android_runtime.so，否则 hwui 的
# RenderProxy → CanvasContext → ASurfaceControlFunctions 路径 SIGABRT
# "Failed to find required symbol ASurfaceControl_create"。
# 实证：2026-05-09 helloworld pid 2373 child stderr: SIGABRT signal 6 SI_TKILL
# in <pre-initialize> main thread, 调用栈在 hwui RenderThread init。
run $HDC shell "rm -f /system/android/lib/libandroid.so; ln -sf liboh_android_runtime.so /system/android/lib/libandroid.so" || true
run $HDC shell "rm -f /system/lib/libandroid.so; ln -sf liboh_android_runtime.so /system/lib/libandroid.so" || true
echo "  libandroid.so -> liboh_android_runtime.so (G2.14aa NDK symbol provider, dual path)"


# ==========================================================================
# Done
# ==========================================================================
echo ""
echo "=========================================="
echo "Deployment complete!"
echo "=========================================="
echo ""
echo "Next steps:"
echo "  1. Reboot device:                hdc shell reboot"
echo "  2. (Optional) Install APK:       hdc file send out/app/HelloWorld.apk /data/local/tmp/HelloWorld.apk \\"
echo "                                     && hdc shell bm install -p /data/local/tmp/HelloWorld.apk"
echo "  3. Launch HelloWorld:             hdc shell aa start -a com.example.helloworld.MainActivity -b com.example.helloworld"
echo "  4. Watch logs:                    hdc shell hilog | grep -E 'OH_AMAdapter|OH_WMAdapter|HelloWorld'"
echo ""
echo "  Uninstall:  bash deploy_to_dayu200.sh --uninstall"
