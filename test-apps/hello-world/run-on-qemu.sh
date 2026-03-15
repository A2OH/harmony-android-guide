#!/bin/bash
#
# Deploy and run Hello World on OHOS QEMU.
#
# Prerequisites:
#   - QEMU booted: tools/qemu_boot.sh (in ../openharmony/)
#   - HDC connected: hdc tconn <ip>:5555
#
# What it deploys:
#   /data/a2oh/dalvikvm          — Dalvik VM (ARM32, static)
#   /data/a2oh/core-kitkat.jar   — Boot classpath
#   /data/a2oh/hello-world.dex   — Shim classes + HelloWorldActivity + runner
#
# Usage:
#   ./run-on-qemu.sh [hdc-target]
#   ./run-on-qemu.sh 192.168.100.2:5555

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

DALVIK_VM="$PROJECT_ROOT/dalvik-port/build-ohos-arm32/dalvikvm"
BOOT_JAR="$PROJECT_ROOT/dalvik-port/core-kitkat.jar"
DEX="/tmp/hello-world.dex"

HDC_TARGET="${1:-}"

if [ ! -f "$DALVIK_VM" ]; then
    echo "ERROR: Dalvik VM not found at $DALVIK_VM"
    exit 1
fi

if [ ! -f "$DEX" ]; then
    echo "ERROR: DEX not found at $DEX"
    echo "Build it first: cd test-apps && ./hello-world/build-dex.sh"
    exit 1
fi

# HDC command wrapper
hdc_cmd() {
    if [ -n "$HDC_TARGET" ]; then
        hdc -t "$HDC_TARGET" "$@"
    else
        hdc "$@"
    fi
}

echo "═══ Deploying Hello World to OHOS QEMU ═══"

# Create deployment directory
echo "Creating /data/a2oh/..."
hdc_cmd shell mkdir -p /data/a2oh
hdc_cmd shell mkdir -p /data/dalvik-cache

# Push files
echo "Pushing dalvikvm (ARM32)..."
hdc_cmd file send "$DALVIK_VM" /data/a2oh/dalvikvm
hdc_cmd shell chmod 755 /data/a2oh/dalvikvm

echo "Pushing boot classpath..."
hdc_cmd file send "$BOOT_JAR" /data/a2oh/core-kitkat.jar

echo "Pushing hello-world.dex..."
hdc_cmd file send "$DEX" /data/a2oh/hello-world.dex

echo ""
echo "═══ Running Hello World on Dalvik/OHOS ═══"
echo ""

# Run on Dalvik
hdc_cmd shell /data/a2oh/dalvikvm \
    -Xbootclasspath:/data/a2oh/core-kitkat.jar \
    -cp /data/a2oh/hello-world.dex \
    HelloWorldRunner

echo ""
echo "═══ Done ═══"
