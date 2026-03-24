#!/bin/bash
# Build WestlakeViewer APK without Android Studio
# Requires: aapt, javac, d8 (or dx), apksigner, android.jar

set -e
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

ANDROID_SDK=/home/dspfac/aosp-android-11/prebuilts/sdk
TOOLS=$ANDROID_SDK/tools/linux/bin
ANDROID_JAR=$ANDROID_SDK/30/public/android.jar
BUILD=$DIR/build
KEYSTORE=$BUILD/debug.keystore

# Check tools
for tool in $TOOLS/aapt $TOOLS/d8 $TOOLS/apksigner; do
    if [ ! -f "$tool" ]; then echo "Missing: $tool"; exit 1; fi
done
if [ ! -f "$ANDROID_JAR" ]; then
    # Try alternative paths
    ANDROID_JAR=$ANDROID_SDK/29/public/android.jar
    if [ ! -f "$ANDROID_JAR" ]; then
        echo "No android.jar found"; exit 1
    fi
fi

echo "=== Building WestlakeViewer APK ==="
rm -rf $BUILD
mkdir -p $BUILD/classes $BUILD/gen

# 1. Generate R.java (no resources needed for this simple app)
echo "  [aapt] package"
$TOOLS/aapt package -f -m \
    -S $DIR/res \
    -J $BUILD/gen \
    -M $DIR/AndroidManifest.xml \
    -I $ANDROID_JAR 2>/dev/null || true

# 2. Compile Java
echo "  [javac] ViewerActivity.java"
find $DIR/src -name "*.java" > $BUILD/sources.txt
find $BUILD/gen -name "*.java" >> $BUILD/sources.txt 2>/dev/null
javac -source 1.8 -target 1.8 \
    -classpath $ANDROID_JAR \
    -d $BUILD/classes \
    @$BUILD/sources.txt 2>&1

# 3. DEX
echo "  [d8] classes.dex"
find $BUILD/classes -name "*.class" > $BUILD/classes.txt
$TOOLS/d8 --output $BUILD @$BUILD/classes.txt --lib $ANDROID_JAR 2>&1 || \
    $ANDROID_SDK/../build-tools/common/bin/dx --dex --output=$BUILD/classes.dex $BUILD/classes

# 4. Package APK
echo "  [aapt] create APK"
$TOOLS/aapt package -f \
    -M $DIR/AndroidManifest.xml \
    -I $ANDROID_JAR \
    -F $BUILD/unsigned.apk 2>/dev/null

# Add DEX
cd $BUILD
cp classes.dex $BUILD/ 2>/dev/null || true
zip -j $BUILD/unsigned.apk $BUILD/classes.dex

# 5. Sign APK
echo "  [keytool] debug keystore"
if [ ! -f $KEYSTORE ]; then
    keytool -genkeypair -v \
        -keystore $KEYSTORE -storepass android -keypass android \
        -alias debug -keyalg RSA -keysize 2048 -validity 10000 \
        -dname "CN=Debug,O=Westlake" 2>/dev/null
fi

echo "  [apksigner] sign"
$TOOLS/apksigner sign \
    --ks $KEYSTORE --ks-pass pass:android --key-pass pass:android \
    --out $BUILD/WestlakeViewer.apk \
    $BUILD/unsigned.apk 2>&1

echo ""
echo "=== Built: $BUILD/WestlakeViewer.apk ==="
ls -la $BUILD/WestlakeViewer.apk
