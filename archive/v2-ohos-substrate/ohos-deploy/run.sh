#!/bin/sh
# ART + OHOS deployment script for MockDonalds app
# Deploy contents of this directory to /data/a2oh on OHOS QEMU
#
# Architecture: ART ARM64 -> native .oat code -> OHBridge JNI -> liboh_bridge.so -> /dev/fb0

set -e

DEPLOY_DIR=/data/a2oh
export ANDROID_DATA=$DEPLOY_DIR
export ANDROID_ROOT=$DEPLOY_DIR
export BOOTCLASSPATH=$DEPLOY_DIR/core-oj.jar:$DEPLOY_DIR/core-libart.jar:$DEPLOY_DIR/core-icu4j.jar:$DEPLOY_DIR/aosp-shim.dex

# Create required directories
mkdir -p $ANDROID_DATA/dalvik-cache/arm64

cd $DEPLOY_DIR

echo "[run.sh] Starting MockDonalds on ART + OHOS"
echo "[run.sh] Boot image: boot.art ($(ls -lh boot.art | awk '{print $5}'))"
echo "[run.sh] App DEX: app.dex ($(ls -lh app.dex | awk '{print $5}'))"
echo "[run.sh] Bridge: liboh_bridge.so ($(ls -lh liboh_bridge.so | awk '{print $5}'))"

./dalvikvm \
  -Xbootclasspath:$BOOTCLASSPATH \
  -Ximage:$DEPLOY_DIR/boot.art \
  -Xverify:none \
  -Xnorelocate \
  -Djava.library.path=$DEPLOY_DIR \
  -classpath $DEPLOY_DIR/app.dex \
  com.example.mockdonalds.MockDonaldsApp \
  "$@"
