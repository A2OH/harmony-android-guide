#!/bin/bash
# Quick deploy script for Westlake Engine APK
set -e
DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD=$DIR/build
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/30/public/android.jar
DX=/home/dspfac/aosp-android-11/prebuilts/build-tools/common/bin/dx
TOOLS=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/bin
ADB=/mnt/c/Users/dspfa/dev/platform-tools/adb.exe

echo "=== Building Westlake Engine APK ==="

# Compile Java
rm -rf $BUILD/classes && mkdir -p $BUILD/classes
javac -source 1.8 -target 1.8 -classpath $ANDROID_JAR -d $BUILD/classes \
  $DIR/src/com/westlake/host/WestlakeActivity.java 2>&1 | grep error && exit 1

# DEX
$DX --dex --output=$BUILD/classes.dex $BUILD/classes

# Package
$TOOLS/aapt package -f -M $DIR/AndroidManifest.xml -S $DIR/res \
  -A $BUILD/assets -I $ANDROID_JAR -F $BUILD/unsigned.apk
cd $BUILD && zip -j unsigned.apk classes.dex && zip -r unsigned.apk lib/

# Sign
jarsigner -keystore $BUILD/debug.keystore -storepass android -keypass android \
  -signedjar $BUILD/WestlakeEngine.apk $BUILD/unsigned.apk debug

echo "=== Installing ==="
$ADB install -r $BUILD/WestlakeEngine.apk
$ADB logcat -c
$ADB shell "am start -S -n com.westlake.host/.WestlakeActivity"

echo "=== Watching logcat ==="
sleep 3
$ADB logcat -d | grep -E "Westlake|MockDonalds|OHBridge|Surface|Error|FATAL" | tail -20
