#!/bin/bash
# ============================================================================
# compile_oh_adapter_runtime.sh
#
# Build oh-adapter-runtime.jar — the NON-BCP, DexClassLoader-loadable jar
# that holds frequently-changing adapter-project Java code. Decouples
# adapter Java iteration from BCP oh-adapter-framework.jar whose byte
# changes trigger boot-image rebuild.
#
# Contents (Stage 0, 2026-04-17):
#   com/android/internal/os/AppSpawnXInit.java    (preload orchestrator)
#
# Build pipeline:
#   1. javac (--release 8 for max device compatibility) → .class files
#      using framework/core-oj/core-libart turbine header jars
#   2. d8 (from AOSP host build) → classes.dex
#   3. jar → oh-adapter-runtime.jar (contains classes.dex + META-INF)
#
# Output:   out/adapter/oh-adapter-runtime.jar
# Loaded:   at runtime by appspawn-x via DexClassLoader, pointing to
#           /system/android/framework/oh-adapter-runtime.jar
# ============================================================================
set -o pipefail

ADAPTER_ROOT="${ADAPTER_ROOT:-$HOME/adapter}"
AOSP_ROOT="${AOSP_ROOT:-$HOME/aosp}"

CLEAN=0
for arg in "$@"; do
    case "$arg" in
        --clean) CLEAN=1 ;;
        *) echo "Unknown arg: $arg" >&2; exit 1 ;;
    esac
done

JDK="$AOSP_ROOT/prebuilts/jdk/jdk17/linux-x86"
JAVAC="$JDK/bin/javac"
JAR="$JDK/bin/jar"
JAVA="$JDK/bin/java"
D8="$AOSP_ROOT/out/host/linux-x86/bin/d8"

# Source files — only classes that should live in the runtime jar
# B.28 (2026-04-29): BindApplicationHelper.java 已彻底删除，永不再用。
# 真实路径走 OH IPC：aa start → AMS → AppSchedulerBridge.nativeOnScheduleLaunchAbility
#                  → IApplicationThread.scheduleTransaction → handleLaunchActivity。
SRCS=(
    "$ADAPTER_ROOT/framework/appspawn-x/java/com/android/internal/os/AppSpawnXInit.java"
    # 2026-04-30 方向 2 单类试点：AppSchedulerBridge 从 BCP 搬到此 PathClassLoader
    # jar，改它不再触发 boot image 重烘焙。需 BCP_CLASS_DIR 在 javac classpath
    # 解析它对 IntentWantConverter / LifecycleAdapter 等 BCP 必留类的引用。
    #
    # 2026-04-30 (P1 字段映射改造)：原本拆出 4 个独立 converter/factory 类
    # (OhApplicationInfoConverter / OhConfigurationConverter / OhDisplayProvider /
    # AppBindDataDefaults)，但 PathClassLoader 在跨类引用时报 ClassNotFoundException
    # 即使所有类都在同一 classes.dex（根因待查）。临时把字段映射逻辑全部内联到
    # AppSchedulerBridge 解锁 Phase 1 验证；4 个独立类文件保留作为后续修复 ClassLoader
    # 问题后的目标形态参考。
    "$ADAPTER_ROOT/framework/activity/java/AppSchedulerBridge.java"
)

OUT_DIR="$ADAPTER_ROOT/out/adapter"
OUT_JAR="$OUT_DIR/oh-adapter-runtime.jar"
BUILD_DIR="$ADAPTER_ROOT/out/adapter/runtime-build"
CLASS_DIR="$BUILD_DIR/classes"
DEX_DIR="$BUILD_DIR/dex"
LOG="$BUILD_DIR/compile.log"

# Turbine header jars for compile-only references (framework / core-oj / core-libart)
FWK_JAR="$AOSP_ROOT/out/soong/.intermediates/frameworks/base/framework-minus-apex/android_common/turbine-combined/framework-minus-apex.jar"
CORE_OJ_JAR="$AOSP_ROOT/out/soong/.intermediates/libcore/core-oj/android_common/turbine-combined/core-oj.jar"
CORE_LIBART_JAR="$AOSP_ROOT/out/soong/.intermediates/libcore/core-libart/android_common/turbine-combined/core-libart.jar"

echo "=============================================="
echo "oh-adapter-runtime.jar build (non-BCP, dex'd)"
echo "=============================================="
echo "  ADAPTER_ROOT = $ADAPTER_ROOT"
echo "  AOSP_ROOT    = $AOSP_ROOT"
echo "  OUT_JAR      = $OUT_JAR"

# Pre-flight
for f in "$JAVAC" "$JAR" "$D8" "$FWK_JAR" "$CORE_OJ_JAR" "$CORE_LIBART_JAR"; do
    if [ ! -f "$f" ] && [ ! -x "$f" ]; then
        echo "ERROR: prerequisite missing: $f" >&2
        exit 1
    fi
done
for s in "${SRCS[@]}"; do
    if [ ! -f "$s" ]; then
        echo "ERROR: source missing: $s" >&2
        exit 1
    fi
done

mkdir -p "$BUILD_DIR" "$OUT_DIR"
# 2026-04-30 方向 2：always-clean — 防搬迁过的类在 CLASS_DIR 残留。
rm -rf "$CLASS_DIR" "$DEX_DIR"
mkdir -p "$CLASS_DIR" "$DEX_DIR"

# ----------------------------------------------------------------------------
# 1. javac
# ----------------------------------------------------------------------------
echo ""
echo "[1/3] javac ${#SRCS[@]} sources..."
# 2026-04-30 方向 2：runtime jar 类引用 BCP 必留类（IntentWantConverter 等）。
# BCP jar 是 dex-only (javac 读不了)，用它的中间 CLASS_DIR (含 .class 字节码)
# 作 compile-time classpath。运行时 PathClassLoader 父级 BCP 解析这些 import。
# 前置：必须先跑 compile_oh_adapter_framework.sh 让 BCP_CLASS_DIR 是当前 BCP
# jar 对应的 .class 字节码。
BCP_CLASS_DIR="$ADAPTER_ROOT/out/adapter/classes"
if [ ! -d "$BCP_CLASS_DIR" ] || [ -z "$(ls -A "$BCP_CLASS_DIR" 2>/dev/null)" ]; then
    echo "ERROR: BCP_CLASS_DIR ($BCP_CLASS_DIR) is empty/missing."
    echo "       Run compile_oh_adapter_framework.sh first."
    exit 1
fi
CP="$FWK_JAR:$CORE_OJ_JAR:$CORE_LIBART_JAR:$BCP_CLASS_DIR"
if ! "$JAVAC" \
        -d "$CLASS_DIR" \
        --release 17 \
        -classpath "$CP" \
        -encoding UTF-8 \
        -Xmaxerrs 30 \
        "${SRCS[@]}" \
        > "$LOG" 2>&1; then
    echo "  FAIL — see $LOG"
    head -30 "$LOG"
    exit 2
fi
NUM_CLASSES=$(find "$CLASS_DIR" -name '*.class' | wc -l)
echo "  OK: $NUM_CLASSES .class files"

# ----------------------------------------------------------------------------
# 2. d8 → classes.dex
# ----------------------------------------------------------------------------
echo ""
echo "[2/3] d8 → classes.dex..."
# d8 wants JAVA_HOME pointed at a modern JDK (17+) — reuse AOSP jdk17
export JAVA_HOME="$JDK"
export PATH="$JDK/bin:$PATH"

# Collect all .class files
CLASS_FILES=$(find "$CLASS_DIR" -name '*.class')
if [ -z "$CLASS_FILES" ]; then
    echo "  FAIL: no .class files to dex"
    exit 3
fi

# Use --classpath for boot classpath references so d8 can verify
if ! "$D8" \
        --release \
        --output "$DEX_DIR" \
        --lib "$CORE_OJ_JAR" \
        --lib "$CORE_LIBART_JAR" \
        --lib "$FWK_JAR" \
        $CLASS_FILES \
        >> "$LOG" 2>&1; then
    echo "  FAIL — see $LOG"
    tail -30 "$LOG"
    exit 3
fi
if [ ! -f "$DEX_DIR/classes.dex" ]; then
    echo "  FAIL: d8 did not produce classes.dex"
    tail -30 "$LOG"
    exit 3
fi
DEX_SIZE=$(stat -c%s "$DEX_DIR/classes.dex")
echo "  OK: classes.dex = $DEX_SIZE bytes"

# ----------------------------------------------------------------------------
# 3. Package as jar
# ----------------------------------------------------------------------------
echo ""
echo "[3/3] Packaging jar..."
rm -f "$OUT_JAR"
(cd "$DEX_DIR" && "$JAR" cf "$OUT_JAR" classes.dex)
if [ ! -f "$OUT_JAR" ]; then
    echo "ERROR: jar packaging failed" >&2
    exit 4
fi
JAR_SIZE=$(stat -c%s "$OUT_JAR")
echo "  OK: $OUT_JAR ($JAR_SIZE bytes)"

# ----------------------------------------------------------------------------
# Post-flight
# ----------------------------------------------------------------------------
echo ""
echo "Post-flight:"
if unzip -l "$OUT_JAR" 2>/dev/null | grep -q 'classes\.dex'; then
    echo "  OK: classes.dex present"
else
    echo "  FAIL: classes.dex missing from jar"
    exit 5
fi
# Confirm at least AppSpawnXInit ended up in the dex
if "$D8" --help >/dev/null 2>&1 && command -v strings >/dev/null; then
    if strings "$DEX_DIR/classes.dex" | grep -q 'AppSpawnXInit'; then
        echo "  OK: AppSpawnXInit symbol present in classes.dex"
    else
        echo "  WARN: AppSpawnXInit symbol not found in classes.dex strings"
    fi
fi

echo ""
echo "=============================================="
echo "DONE — oh-adapter-runtime.jar built"
echo "  $OUT_JAR ($JAR_SIZE bytes)"
echo ""
echo "Deploy: hdc file send $OUT_JAR /system/android/framework/oh-adapter-runtime.jar"
echo "Load:   via DexClassLoader in appspawn-x native"
echo "=============================================="
