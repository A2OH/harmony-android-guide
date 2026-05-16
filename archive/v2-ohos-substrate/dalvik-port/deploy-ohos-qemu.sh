#!/bin/bash
# Deploy and run Dalvik Hello World on OHOS QEMU ARM32
#
# Prerequisites:
#   - OHOS QEMU booted (tools/qemu_boot.sh in ~/openharmony)
#   - HDC connected: hdc tconn <ip>:5555
#
# Usage:
#   ./deploy-ohos-qemu.sh [hdc-target]

set -e

DEPLOY="/tmp/ohos-apk/deploy"
TARGET="/data/a2oh"

HDC_TARGET="${1:-}"
hdc_cmd() {
    if [ -n "$HDC_TARGET" ]; then hdc -t "$HDC_TARGET" "$@"
    else hdc "$@"; fi
}

echo "═══ Deploying Dalvik to OHOS QEMU ═══"
echo ""

# Create dirs
hdc_cmd shell "mkdir -p $TARGET && mkdir -p /data/dalvik-cache"

# Push files
echo "Pushing dalvikvm (ARM32, 6.9MB)..."
hdc_cmd file send $DEPLOY/dalvikvm $TARGET/dalvikvm
hdc_cmd shell chmod 755 $TARGET/dalvikvm

echo "Pushing dexopt..."
hdc_cmd file send $DEPLOY/dexopt $TARGET/dexopt
hdc_cmd shell chmod 755 $TARGET/dexopt

echo "Pushing boot classpath..."
hdc_cmd file send $DEPLOY/core-kitkat-patched.jar $TARGET/core-kitkat.jar

echo "Pushing hello-ohos.dex..."
hdc_cmd file send $DEPLOY/hello-ohos.dex $TARGET/hello-ohos.dex

echo "Pushing native libs..."
hdc_cmd file send $DEPLOY/libjavacore.so $TARGET/libjavacore.so
hdc_cmd file send $DEPLOY/libnativehelper.so $TARGET/libnativehelper.so

echo ""
echo "═══ Running Hello World on Dalvik/OHOS ═══"
echo ""

hdc_cmd shell "\
    export LD_LIBRARY_PATH=$TARGET && \
    export ANDROID_DATA=/data && \
    export ANDROID_ROOT=$TARGET && \
    $TARGET/dalvikvm \
        -Xbootclasspath:$TARGET/core-kitkat.jar:$TARGET/hello-ohos.dex \
        HelloOHOS"

echo ""
echo "═══ Done ═══"
