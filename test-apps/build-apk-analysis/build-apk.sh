#!/bin/bash
set -e

AAPT=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/bin/aapt
ANDROID_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/19/public/android.jar
DX_JAR=/home/dspfac/aosp-android-11/prebuilts/sdk/tools/linux/lib/dx.jar

APK_DIR="$1"
APK_NAME="$2"

echo "=== Building $APK_NAME from $APK_DIR ==="

cd "$APK_DIR"

# Clean
rm -rf gen classes obj *.apk

# Step 1: aapt - compile resources and generate R.java
mkdir -p gen obj
echo "  [aapt] Compiling resources..."
$AAPT package -f -m \
    -S res \
    -J gen \
    -M AndroidManifest.xml \
    -I "$ANDROID_JAR" \
    -F obj/resources.ap_ \
    --auto-add-overlay

echo "  [aapt] R.java generated"

# Step 2: javac - compile Java sources
mkdir -p classes
echo "  [javac] Compiling Java..."
find src gen -name "*.java" > sources.txt
javac --release 8 \
    -cp "$ANDROID_JAR" \
    -d classes \
    @sources.txt 2>&1

echo "  [javac] Classes:"
find classes -name "*.class" | sort

# Step 3: dx - create DEX
echo "  [dx] Creating DEX..."
java -jar "$DX_JAR" --dex --output=classes.dex classes/ 2>&1
echo "  [dx] DEX size: $(wc -c < classes.dex) bytes"

# Step 4: Package into APK
echo "  [pack] Creating APK..."
python3 -c "
import zipfile, os, sys

apk_name = '$APK_NAME'

with zipfile.ZipFile(apk_name, 'w', zipfile.ZIP_DEFLATED) as apk:
    apk.write('classes.dex', 'classes.dex')
    if os.path.exists('obj/resources.ap_'):
        with zipfile.ZipFile('obj/resources.ap_', 'r') as res:
            for item in res.namelist():
                data = res.read(item)
                apk.writestr(item, data)
    print('APK contents:')
    for info in apk.infolist():
        print('  {} ({} bytes)'.format(info.filename, info.file_size))

print('APK created: ' + apk_name)
"

ls -la "$APK_NAME"
echo "=== Done: $APK_NAME ==="
