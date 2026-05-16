#!/system/bin/sh
# A15 ART Runtime launcher for OHOS ARM64
# Deploy to /data/a2oh/ on the device

DEPLOY_DIR=$(dirname "$0")
if [ "$DEPLOY_DIR" = "." ]; then DEPLOY_DIR=$(pwd); fi

export ANDROID_DATA=/tmp/android-data
export ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_DATA/dalvik-cache/arm64 $ANDROID_ROOT/bin 2>/dev/null

# Boot classpath: core JARs + art-patch.jar (Utf8Writer for println)
BCP=$DEPLOY_DIR/core-oj.jar:$DEPLOY_DIR/core-libart.jar:$DEPLOY_DIR/core-icu4j.jar:$DEPLOY_DIR/art-patch.jar

# Optional: add AOSP shim DEX for Android API support
if [ -f "$DEPLOY_DIR/aosp-shim.dex" ]; then
  BCP="$BCP:$DEPLOY_DIR/aosp-shim.dex"
fi

DALVIKVM=$DEPLOY_DIR/dalvikvm
BOOT_IMAGE=$DEPLOY_DIR/boot.art
APP=${1:-$DEPLOY_DIR/app.dex}
MAIN=${2:-com.example.mockdonalds.MockDonaldsApp}

echo "=== A15 ART (OHOS ARM64) ==="
echo "Boot image: $BOOT_IMAGE"
echo "App: $APP"
echo "Main: $MAIN"

DALVIKVM_ARGS="-Xbootclasspath:$BCP"
if [ -f "$BOOT_IMAGE" ]; then
  DALVIKVM_ARGS="$DALVIKVM_ARGS -Ximage:$BOOT_IMAGE"
fi
DALVIKVM_ARGS="$DALVIKVM_ARGS -Xusejit:true"
DALVIKVM_ARGS="$DALVIKVM_ARGS -classpath $APP"

chmod +x $DALVIKVM 2>/dev/null
exec $DALVIKVM $DALVIKVM_ARGS $MAIN "$@"
