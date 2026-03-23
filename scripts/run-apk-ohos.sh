#!/bin/bash
# Usage: ./run-apk-ohos.sh path/to/app.apk [MainActivityClass]
#
# Unpack APK -> extract DEX -> cross-compile for ARM64 -> deploy to OHOS QEMU
#
# Examples:
#   ./run-apk-ohos.sh calculator.apk com.android.calculator2.Calculator
#   ./run-apk-ohos.sh mockdonalds.apk com.example.mockdonalds.MockDonaldsApp
#
# This script prepares everything for OHOS ARM64 QEMU deployment:
#   1. Extracts classes.dex from APK
#   2. Cross-compiles for ARM64 using dex2oat
#   3. Builds a deployment directory with all needed files
#   4. Optionally injects into userdata-art.img and boots QEMU

set -euo pipefail

# --- Configuration -----------------------------------------------------------
REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

DEX2OAT=/home/dspfac/art-universal-build/build/bin/dex2oat
OHOS_DEPLOY="$REPO_ROOT/ohos-deploy"
AOSP_SHIM="$OHOS_DEPLOY/aosp-shim.dex"

# ARM64 boot image (cross-compiled)
ARM64_BOOT_DIR="$OHOS_DEPLOY/arm64"
ARM64_BOOT_IMAGE="$ARM64_BOOT_DIR/boot.art"

# OHOS QEMU paths
OHOS_ROOT="/home/dspfac/openharmony-wsl"
QEMU_IMG="$OHOS_ROOT/userdata-art.img"
DEPLOY_TARGET="/data/a2oh"

# Boot classpath jars for the device
BCP_JARS="core-oj.jar:core-libart.jar:core-icu4j.jar"

# --- Argument parsing ---------------------------------------------------------
if [ $# -lt 1 ]; then
    echo "Usage: $0 <path/to/app.apk> [MainActivityClass]"
    echo ""
    echo "  Extracts DEX from APK, cross-compiles for ARM64,"
    echo "  and prepares for OHOS QEMU deployment."
    echo ""
    echo "Options (via environment):"
    echo "  DEPLOY=1       Inject into QEMU image and boot"
    echo "  QEMU_BOOT=1    Also start QEMU after injection"
    echo "  INSTRUCTION_SET=arm64  (default: arm64)"
    exit 1
fi

APK="$(realpath "$1")"
MAIN_CLASS="${2:-}"
INSTRUCTION_SET="${INSTRUCTION_SET:-arm64}"

if [ ! -f "$APK" ]; then
    echo "ERROR: APK not found: $APK"
    exit 1
fi

APK_NAME="$(basename "$APK" .apk)"
WORKDIR="/tmp/apk-ohos/$APK_NAME"
STAGING="$WORKDIR/staging"
mkdir -p "$WORKDIR" "$STAGING"

echo "=== Westlake APK Runner (OHOS ARM64) ==="
echo "APK:       $APK"
echo "Target:    $INSTRUCTION_SET"
echo "Work dir:  $WORKDIR"

# --- Step 1: Extract DEX from APK --------------------------------------------
echo ""
echo "[1/6] Extracting DEX from APK..."

python3 -c "
import zipfile, sys
z = zipfile.ZipFile('$APK')
dex_files = [n for n in z.namelist() if n.startswith('classes') and n.endswith('.dex')]
if not dex_files:
    print('ERROR: No classes*.dex found in APK')
    sys.exit(1)
for f in dex_files:
    z.extract(f, '$WORKDIR')
    print('  Extracted: ' + f + ' (' + str(z.getinfo(f).file_size) + ' bytes)')
print('Total DEX files: ' + str(len(dex_files)))
"

PRIMARY_DEX="$WORKDIR/classes.dex"
if [ ! -f "$PRIMARY_DEX" ]; then
    echo "ERROR: classes.dex not found after extraction"
    exit 1
fi

# --- Step 2: Analyze classes --------------------------------------------------
echo ""
echo "[2/6] Analyzing DEX..."

DEXDUMP=""
for candidate in \
    /home/dspfac/art-universal-build/build/bin/dexdump \
    /home/dspfac/aosp-android-11/out/host/linux-x86/bin/dexdump \
    "$(which dexdump 2>/dev/null || true)"; do
    if [ -n "$candidate" ] && [ -x "$candidate" ]; then
        DEXDUMP="$candidate"
        break
    fi
done

if [ -z "$MAIN_CLASS" ]; then
    if [ -n "$DEXDUMP" ]; then
        echo "No main class specified. Classes found in DEX:"
        "$DEXDUMP" -f "$PRIMARY_DEX" 2>/dev/null \
            | grep "Class descriptor" \
            | sed "s/.*'L//; s/;'//; s|/|.|g" \
            | sort | head -50
    else
        echo "No main class specified and dexdump not available."
    fi
    echo ""
    echo "Re-run with: $0 $APK <MainClass>"
    exit 0
else
    echo "  Main class: $MAIN_CLASS"
fi

# --- Step 3: Cross-compile for ARM64 with dex2oat ----------------------------
echo ""
echo "[3/6] Cross-compiling DEX for $INSTRUCTION_SET..."

OAT_FILE="$WORKDIR/app.oat"

# dex2oat requires ANDROID_ROOT
export ANDROID_ROOT=/tmp/android-root
export ANDROID_DATA=/tmp/android-data
mkdir -p "$ANDROID_ROOT/bin" "$ANDROID_DATA/dalvik-cache/$INSTRUCTION_SET"

DEX_ARGS="--dex-file=$PRIMARY_DEX"
for extra_dex in "$WORKDIR"/classes[0-9]*.dex; do
    [ -f "$extra_dex" ] && [ "$extra_dex" != "$PRIMARY_DEX" ] && \
        DEX_ARGS="$DEX_ARGS --dex-file=$extra_dex"
done

if [ -f "$DEX2OAT" ]; then
    set +e
    "$DEX2OAT" \
        $DEX_ARGS \
        --oat-file="$OAT_FILE" \
        --android-root="$ANDROID_ROOT" \
        --instruction-set="$INSTRUCTION_SET" \
        --compiler-filter=speed \
        --runtime-arg -Xverify:none \
        ${ARM64_BOOT_IMAGE:+--boot-image=$ARM64_BOOT_IMAGE} \
        -j4 \
        2>&1 | tee "$WORKDIR/dex2oat.log"
    DEX2OAT_RC=${PIPESTATUS[0]}
    set -e

    if [ $DEX2OAT_RC -eq 0 ] && [ -f "$OAT_FILE" ]; then
        echo "  Cross-compiled: $OAT_FILE ($(stat -c%s "$OAT_FILE") bytes)"
    else
        echo "  WARNING: dex2oat cross-compile failed (rc=$DEX2OAT_RC)"
        echo "  App will run interpreted on device. Log: $WORKDIR/dex2oat.log"
    fi
else
    echo "  WARNING: dex2oat not found at $DEX2OAT"
    echo "  App will run interpreted on device."
fi

# --- Step 4: Stage deployment directory ---------------------------------------
echo ""
echo "[4/6] Staging deployment directory..."

# Copy core jars from ohos-deploy
for f in core-oj.jar core-libart.jar core-icu4j.jar; do
    if [ -f "$OHOS_DEPLOY/$f" ]; then
        cp "$OHOS_DEPLOY/$f" "$STAGING/"
    elif [ -f "$REPO_ROOT/art-boot-image/$f" ]; then
        cp "$REPO_ROOT/art-boot-image/$f" "$STAGING/"
    fi
done

# Copy boot image (ARM64)
if [ -d "$ARM64_BOOT_DIR" ]; then
    cp "$ARM64_BOOT_DIR"/boot*.art "$ARM64_BOOT_DIR"/boot*.oat "$ARM64_BOOT_DIR"/boot*.vdex \
       "$STAGING/" 2>/dev/null || true
fi

# Copy AOSP shim
[ -f "$AOSP_SHIM" ] && cp "$AOSP_SHIM" "$STAGING/"

# Copy app DEX
cp "$PRIMARY_DEX" "$STAGING/app.dex"
for extra_dex in "$WORKDIR"/classes[0-9]*.dex; do
    [ -f "$extra_dex" ] && [ "$extra_dex" != "$PRIMARY_DEX" ] && \
        cp "$extra_dex" "$STAGING/"
done

# Copy AOT output if available
if [ -f "$OAT_FILE" ]; then
    mkdir -p "$STAGING/oat/$INSTRUCTION_SET"
    cp "$OAT_FILE" "$STAGING/oat/$INSTRUCTION_SET/app.oat"
fi

# Copy dalvikvm and bridge library if available in ohos-deploy
[ -f "$OHOS_DEPLOY/dalvikvm" ] && cp "$OHOS_DEPLOY/dalvikvm" "$STAGING/"
[ -f "$OHOS_DEPLOY/liboh_bridge.so" ] && cp "$OHOS_DEPLOY/liboh_bridge.so" "$STAGING/"

# Generate on-device run script
cat > "$STAGING/run.sh" << RUNEOF
#!/bin/sh
# Auto-generated run script for $APK_NAME
# Deploy this directory to $DEPLOY_TARGET on OHOS device

set -e
DEPLOY_DIR=$DEPLOY_TARGET
export ANDROID_DATA=\$DEPLOY_DIR
export ANDROID_ROOT=\$DEPLOY_DIR
export BOOTCLASSPATH=\$DEPLOY_DIR/core-oj.jar:\$DEPLOY_DIR/core-libart.jar:\$DEPLOY_DIR/core-icu4j.jar:\$DEPLOY_DIR/aosp-shim.dex

mkdir -p \$ANDROID_DATA/dalvik-cache/$INSTRUCTION_SET

cd \$DEPLOY_DIR

echo "[run.sh] Starting $APK_NAME on ART + OHOS"
echo "[run.sh] Main class: $MAIN_CLASS"

./dalvikvm \\
  -Xbootclasspath:\$BOOTCLASSPATH \\
  -Ximage:\$DEPLOY_DIR/boot.art \\
  -Xverify:none \\
  -Xnorelocate \\
  -Djava.library.path=\$DEPLOY_DIR \\
  -classpath \$DEPLOY_DIR/app.dex \\
  $MAIN_CLASS \\
  "\$@"
RUNEOF
chmod +x "$STAGING/run.sh"

echo "  Staged $(ls "$STAGING" | wc -l) files to: $STAGING/"
ls -lh "$STAGING/"

# --- Step 5: Optionally inject into QEMU image -------------------------------
echo ""
echo "[5/6] QEMU image injection..."

if [ "${DEPLOY:-0}" = "1" ] && [ -f "$QEMU_IMG" ]; then
    echo "  Injecting into $QEMU_IMG at $DEPLOY_TARGET..."

    # Use debugfs to write files into ext4 image
    DEBUGFS_CMDS="$WORKDIR/debugfs.cmds"
    echo "mkdir $DEPLOY_TARGET" > "$DEBUGFS_CMDS"

    for f in "$STAGING"/*; do
        fname="$(basename "$f")"
        if [ -f "$f" ]; then
            echo "write $f $DEPLOY_TARGET/$fname" >> "$DEBUGFS_CMDS"
        fi
    done

    # Handle oat subdirectory
    if [ -d "$STAGING/oat" ]; then
        echo "mkdir $DEPLOY_TARGET/oat" >> "$DEBUGFS_CMDS"
        echo "mkdir $DEPLOY_TARGET/oat/$INSTRUCTION_SET" >> "$DEBUGFS_CMDS"
        for f in "$STAGING/oat/$INSTRUCTION_SET"/*; do
            [ -f "$f" ] && echo "write $f $DEPLOY_TARGET/oat/$INSTRUCTION_SET/$(basename "$f")" >> "$DEBUGFS_CMDS"
        done
    fi

    debugfs -w -f "$DEBUGFS_CMDS" "$QEMU_IMG" 2>&1 || {
        echo "  WARNING: debugfs injection failed. Install e2fsprogs or use manual deployment."
        echo "  Manual: copy $STAGING/* to device at $DEPLOY_TARGET/"
    }
    echo "  Injection complete."
else
    if [ "${DEPLOY:-0}" = "1" ]; then
        echo "  WARNING: QEMU image not found at $QEMU_IMG"
    else
        echo "  Skipped (set DEPLOY=1 to inject into QEMU image)"
    fi
    echo ""
    echo "  Manual deployment:"
    echo "    scp -r $STAGING/* device:$DEPLOY_TARGET/"
    echo "    ssh device 'sh $DEPLOY_TARGET/run.sh'"
fi

# --- Step 6: Optionally boot QEMU --------------------------------------------
echo ""
echo "[6/6] QEMU boot..."

if [ "${QEMU_BOOT:-0}" = "1" ] && [ -d "$OHOS_ROOT" ]; then
    echo "  Booting OHOS QEMU..."
    if [ -f "$OHOS_ROOT/run-qemu.sh" ]; then
        exec "$OHOS_ROOT/run-qemu.sh"
    elif [ -f "$OHOS_ROOT/start-qemu.sh" ]; then
        exec "$OHOS_ROOT/start-qemu.sh"
    else
        echo "  WARNING: No QEMU boot script found in $OHOS_ROOT"
        echo "  Boot QEMU manually, then run: sh $DEPLOY_TARGET/run.sh"
    fi
else
    echo "  Skipped (set QEMU_BOOT=1 to auto-boot QEMU)"
fi

echo ""
echo "=== Done ==="
echo "Staging dir: $STAGING/"
echo "On-device:   sh $DEPLOY_TARGET/run.sh"
