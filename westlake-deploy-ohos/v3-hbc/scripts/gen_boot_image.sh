#!/bin/bash
# ============================================================================
# 重复犯错警示 (boot image 生成，调改前必读)
# ============================================================================
#
# [B-1] oh-adapter-framework.jar 单一来源（B.41 修正版，2026-04-30）
#   现象：dex2oat 用 A 版编出 boot image，设备上推的是 B 版，runtime ART
#         报 "ValidateOatFile found checksum mismatch ... oat (X) vs jar (Y)"
#         然后 SIGABRT 风暴；rollback 也救不了，要 reboot。
#   根因：早期同名 oh-adapter-framework.jar 同时存在 out/aosp_fwk/ 和
#         out/adapter/，本脚本 cp 同步有缝，编译期/部署期分别读不同副本。
#   措施：oh-adapter-framework.jar 唯一来源 out/adapter/；resolve_jar_path()
#         按 ADAPTER_JARS 白名单分流。framework.jar 仍由 AOSP Soong 编出后
#         落到 out/aosp_fwk/，作为 BCP 成员正常使用——B.41 把 framework.jar
#         也抛弃的决策已于 2026-04-30 回退（用户判定该决策错误）。
#         （memory: feedback_framework_jar_boot_image_drift.md）
#
# [B-2] art/runtime/asm_defines.h 必须不是 ARM32 版
#   现象：dex2oat64 启动后立即段错，core dump 在 art:: 命名空间常量上。
#   根因：libart cross-build for ARM32 时若把 asm_defines.h 留在 AOSP 源码树，
#         x86_64 host dex2oat64 也会包含它，常量错位。
#   措施：本脚本启动前检测；若是 ARM32 版自动备份并删掉。修改本脚本时不能
#         绕过这段 check。
#
# [B-3] silent stale .o — patch 缺 include 不会让 ninja 重 link
#   现象：改了 .cpp 重编 .so，md5 不变；strings .so | grep <patch marker> 也找不到。
#   根因：patch 加了新的 include 但 ninja 没看到 .h 依赖更新（OH 增量构建陷阱）；
#         编译跳过未变 .o，旧 .o 被 archive 进 .so，"重编"但内容没变。
#   措施：每次编完 .so 必须 strings | grep <patch marker> 验证；如果 marker
#         不在，rm 那个 .o 后强制重 link。
#         （memory: feedback_silent_stale_o_missing_include.md）
#
# [B-4] boot image 全段 vs 单段重建的取舍
#   现象：增加一个 BCP segment 后做了"全段 rebuild"，部署后 OH 系统服务 reboot
#         不起来（不止 ART/adapter，连原生 OH 服务都连锁出问题）。
#   根因：全段 rebuild 让 boot.oat 内嵌的 BCP checksum 全部变化，OH 系统服务在
#         init 期间通过 boot.oat 触发的依赖链一起重新校验，任一段不一致都崩。
#   措施：日常迭代用单 segment 增量 rebuild + reboot UI 验证；只有架构级变更才
#         全段重建，且重建后必须立即 reboot 验证桌面。
#         （memory: feedback_boot_image_full_rebuild_risk.md）
#
# [B-5] libart/dex2oat 编译宏与 boot image 强耦合
#   现象：改了 GC mode/RB barrier/layout 宏单独重编 libart，但 boot.art 还是
#         旧的 → ValidateOatFile 拒载或运行时隐式崩。
#   根因：boot.art/.oat/.vdex 是 dex2oat 编出来的，dex2oat 自己用 libart 静态
#         逻辑；libart 改了 dex2oat 也得重 build，否则 boot image 校验信息和
#         runtime libart 期望的不一致。
#   措施：libart / dex2oat / boot image 三件套必须联动重编，禁单独改其一。
#         （memory: feedback_art_bootimage_coupling.md）
#
# [B-6] 默认 JARS 数量必须与 runtime BCP 9 个 jar 完全一致
#   现象：appspawn-x Phase 2 ART VM init SIGABRT 风暴；cppcrash stack 显示
#         art::ClassLinker::CheckSystemClass → InitWithoutImage：
#         "Class mismatch for Ljava/lang/String;"。设备反复重启 appspawn-x，
#         全部停在 Phase 2 之后。
#   根因：本脚本默认 JARS 列表与 framework/appspawn-x/src/main.cpp:62
#         kBootClasspath 期望的 9 jar 顺序不一致 → dex2oat 只编出部分段
#         （比如 4 段：boot/boot-core-libart/boot-framework/boot-oh-adapter-framework），
#         其余 5 段（boot-core-icu4j / boot-okhttp / boot-bouncycastle /
#         boot-apache-xml / boot-adapter-mainline-stubs）保留旧版 → boot image
#         段间索引不齐 → ART ValidateOatFile 拒载 → fallback InitWithoutImage
#         路径下重新加载 BCP 时类定义冲突 → abort。
#   措施：默认 JARS 必须 = 9 个 jar 完整顺序（与 main.cpp:62 严格对齐）：
#         core-oj / core-libart / core-icu4j / okhttp / bouncycastle /
#         apache-xml / adapter-mainline-stubs / framework / oh-adapter-framework。
#         任一减少都会让 boot image 段不齐 → SIGABRT 风暴。
#   血训：2026-04-30 B.42 修 AppSchedulerBridge:100 NPE 时，默认 JARS 仅 3+1
#         个，全段重编后设备 appspawn-x 反复 Phase 2 SIGABRT，
#         cppcrash-XXX stack 实证 ClassLinker InitWithoutImage 路径 abort。
#         （memory: feedback_framework_jar_boot_image_drift.md）
#
# ============================================================================
# Generate ARM32 boot image (boot.art + boot.oat + boot.vdex)
# Usage: gen_boot_image.sh [--aosp-root PATH] [--output PATH] [--jars "jar1.jar jar2.jar"]
#
# Prerequisites:
#   1. dex2oat-host must be built: m dex2oat-host libsigchain-host libart-host -j16
#   2. BOOTCLASSPATH jars must exist:
#        - AOSP-built core jars (core-oj.jar, core-libart.jar, ...) -> adapter/out/aosp_fwk/
#        - oh-adapter-framework.jar                                 -> adapter/out/adapter/
#      (oh-adapter-framework.jar唯一来源 out/adapter/ 防双源漂移；framework.jar
#       由 AOSP Soong 编出，仍是 BCP 成员——B.41 抛弃 framework.jar 的决策
#       已于 2026-04-30 回退。)
#   3. art/runtime/asm_defines.h must NOT exist (or be x86_64 version)

set -e

AOSP_ROOT=${AOSP_ROOT:-$HOME/aosp}
ADAPTER_ROOT=${ADAPTER_ROOT:-$HOME/adapter}
OUTPUT=${OUTPUT:-$ADAPTER_ROOT/out/boot-image}
HOST_OUT=$AOSP_ROOT/out/host/linux-x86
DEX2OAT=$HOST_OUT/bin/dex2oat64
# $FWK = out/aosp_fwk/ — holds AOSP-built core jars (core-oj.jar /
# core-libart.jar / core-icu4j.jar / okhttp.jar / bouncycastle.jar /
# apache-xml.jar / adapter-mainline-stubs.jar).  Pure AOSP Soong output,
# no adapter modification.
FWK=$ADAPTER_ROOT/out/aosp_fwk
# $ADAPTER_OUT = out/adapter/ — holds adapter-built jars (oh-adapter-framework.jar).
# 2026-04-30: B.41 抛弃 framework.jar 的决策已回退 — framework.jar 仍是 BCP
# 成员，由 AOSP Soong 编出，落到 $FWK (out/aosp_fwk/)。L5 reflection patches
# 仍走 framework.jar 静态修改路径。
ADAPTER_OUT=$ADAPTER_ROOT/out/adapter

# Parse args
for arg in "$@"; do
    case "$arg" in
        --aosp-root=*) AOSP_ROOT="${arg#*=}"; HOST_OUT=$AOSP_ROOT/out/host/linux-x86; DEX2OAT=$HOST_OUT/bin/dex2oat64;;
        --output=*) OUTPUT="${arg#*=}";;
        --jars=*) CUSTOM_JARS="${arg#*=}";;
    esac
done

# ============================================================
# Pre-flight checks
# ============================================================
echo "=== Boot Image Generator ==="
echo "AOSP root: $AOSP_ROOT"
echo "Output:    $OUTPUT"

# Check dex2oat exists
if [ ! -x "$DEX2OAT" ]; then
    echo "ERROR: dex2oat not found at $DEX2OAT"
    echo "Build it first: cd $AOSP_ROOT && m dex2oat-host -j16"
    exit 1
fi

# Check libsigchain
SIGCHAIN=$HOST_OUT/lib64/libsigchain.so
if [ ! -f "$SIGCHAIN" ]; then
    echo "ERROR: libsigchain.so not found. Build: m libsigchain-host -j16"
    exit 1
fi

# CRITICAL: Check asm_defines.h isolation
ASM_DEFINES=$AOSP_ROOT/art/runtime/asm_defines.h
if [ -f "$ASM_DEFINES" ]; then
    ARCH=$(head -1 "$ASM_DEFINES" | grep -o "ARM32\|x86_64\|auto" || echo "unknown")
    if [ "$ARCH" = "ARM32" ]; then
        echo "WARNING: $ASM_DEFINES is ARM32 version!"
        echo "This WILL cause dex2oat64 to crash."
        echo "Backing up and removing..."
        cp "$ASM_DEFINES" "${ASM_DEFINES}.arm32.bak"
        rm "$ASM_DEFINES"
    fi
fi

# ----------------------------------------------------------------------------
# Per-jar source resolution.  Each jar is resolved to either $FWK
# (AOSP-built core jars including framework.jar) or $ADAPTER_OUT
# (adapter-built jars: oh-adapter-framework.jar).
# 2026-04-30: framework.jar 重新进默认 BCP（B.41 抛弃决策回退）。
# ----------------------------------------------------------------------------

# Jars that live in $ADAPTER_OUT (adapter-built artifacts).  All other jars
# resolve under $FWK.
# 2026-04-30 G2.8: adapter-mainline-stubs.jar added — built by
# compile_mainline_stubs.sh into $ADAPTER_OUT, never copied to $FWK.
# Pre-G2.8 had a manual cp $ADAPTER_OUT/adapter-mainline-stubs.jar $FWK/
# step which was easy to forget (G2.6 dual-source trap).
ADAPTER_JARS="oh-adapter-framework.jar adapter-mainline-stubs.jar"

resolve_jar_path() {
    local jar="$1"
    case " $ADAPTER_JARS " in
        *" $jar "*) echo "$ADAPTER_OUT/$jar" ;;
        *) echo "$FWK/$jar" ;;
    esac
}

# Default jars
# IMPORTANT: oh-adapter-framework.jar MUST be on the boot classpath because the
# L5 reflection patches in framework.jar (Class.forName("adapter.core.OHEnvironment"))
# resolve against the boot image. Without this jar in the boot image, every L5
# patch site silently falls back to stock AOSP behavior. See gap 0.1.
if [ -z "$CUSTOM_JARS" ]; then
    # 2026-04-30: BCP runtime 顺序必须与 framework/appspawn-x/src/main.cpp:62 kBootClasspath
    # 完全一致，否则 ART ValidateOatFile 拒载 boot image，fallback InitWithoutImage 然后
    # 在 ClassLinker::CheckSystemClass 报 "Class mismatch for Ljava/lang/String;" abort。
    # 之前 B.41 回退漏掉了 5 个 jar (core-icu4j/okhttp/bouncycastle/apache-xml/adapter-mainline-stubs)
    # 导致 boot image 段不齐，aa start helloworld 时 appspawn-x SIGABRT 风暴。
    JARS="core-oj.jar core-libart.jar core-icu4j.jar okhttp.jar bouncycastle.jar apache-xml.jar adapter-mainline-stubs.jar framework.jar"
    if [ -f "$ADAPTER_OUT/oh-adapter-framework.jar" ]; then
        JARS="$JARS oh-adapter-framework.jar"
    else
        echo "WARNING: oh-adapter-framework.jar not found in $ADAPTER_OUT/"
        echo "         L5 reflection chain will FAIL on device. See gap 0.1."
    fi
else
    JARS=$CUSTOM_JARS
fi

# Check jars exist (per-jar source resolution)
DEX_ARGS=""
LOC_ARGS=""
for jar in $JARS; do
    src=$(resolve_jar_path "$jar")
    if [ ! -f "$src" ]; then
        echo "ERROR: $src not found"
        exit 1
    fi
    DEX_ARGS="$DEX_ARGS --dex-file=$src"
    LOC_ARGS="$LOC_ARGS --dex-location=/system/android/framework/$jar"
done

# ============================================================
# Generate boot image
# ============================================================
mkdir -p "$OUTPUT"

export ANDROID_ROOT=$HOST_OUT
export ANDROID_DATA=/tmp/dex2oat_data_$$
mkdir -p $ANDROID_DATA/dalvik-cache/arm

echo ""
echo "Generating ARM32 boot image..."
echo "  Input jars: $JARS"
echo "  Output: $OUTPUT/boot.{art,oat,vdex}"

LD_LIBRARY_PATH=$HOST_OUT/lib64 \
LD_PRELOAD=$SIGCHAIN \
$DEX2OAT \
    --android-root=$HOST_OUT \
    --instruction-set=arm \
    $DEX_ARGS \
    $LOC_ARGS \
    --oat-file=$OUTPUT/boot.oat \
    --image=$OUTPUT/boot.art \
    --base=0x70000000 \
    --runtime-arg -Xms64m \
    --runtime-arg -Xmx512m \
    --compiler-filter=speed

# Cleanup
rm -rf $ANDROID_DATA

# Verify output
echo ""
echo "=== Results ==="
ls -lh $OUTPUT/boot.*
echo ""
file $OUTPUT/boot.oat
echo ""
echo "Done! Deploy to device:"
echo "  hdc shell mkdir -p /system/android/framework/arm"
echo "  hdc file send $OUTPUT/boot.art /system/android/framework/arm/"
echo "  hdc file send $OUTPUT/boot.oat /system/android/framework/arm/"
echo "  hdc file send $OUTPUT/boot.vdex /system/android/framework/arm/"
