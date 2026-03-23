#!/bin/bash
# Usage: ./run-apk.sh path/to/app.apk [MainActivityClass]
#
# Unpack APK -> extract DEX -> AOT compile via dex2oat -> run on ART (x86_64)
#
# Examples:
#   ./run-apk.sh calculator.apk com.android.calculator2.Calculator
#   ./run-apk.sh ../test-apps/06-real-apk/hello.apk com.example.hello.MainActivity
#   ./run-apk.sh mockdonalds.apk com.example.mockdonalds.MockDonaldsApp
#
# If no main class is given, the script lists all classes from the DEX.

set -euo pipefail

# --- Configuration -----------------------------------------------------------
REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

DEX2OAT=/home/dspfac/art-universal-build/build/bin/dex2oat
DALVIKVM=/home/dspfac/art-universal-build/build/bin/dalvikvm
ART_LIB=/home/dspfac/art-universal-build/build/lib

BOOT_IMAGE=/tmp/art-boot-out/boot.art
BCP_DIR="$REPO_ROOT/art-boot-image"
AOSP_SHIM="$REPO_ROOT/ohos-deploy/aosp-shim.dex"

# Boot classpath jars
BCP="$BCP_DIR/core-oj.jar:$BCP_DIR/core-libart.jar:$BCP_DIR/core-icu4j.jar"
if [ -f "$AOSP_SHIM" ]; then
    BCP="$BCP:$AOSP_SHIM"
fi

# --- Argument parsing ---------------------------------------------------------
if [ $# -lt 1 ]; then
    echo "Usage: $0 <path/to/app.apk> [MainActivityClass]"
    echo ""
    echo "  APK file is unpacked, DEX is AOT-compiled with dex2oat,"
    echo "  then launched on the ART dalvikvm (x86_64)."
    echo ""
    echo "  If no main class is given, lists all classes in the DEX."
    exit 1
fi

APK="$(realpath "$1")"
MAIN_CLASS="${2:-}"

if [ ! -f "$APK" ]; then
    echo "ERROR: APK not found: $APK"
    exit 1
fi

APK_NAME="$(basename "$APK" .apk)"
WORKDIR="/tmp/apk-run/$APK_NAME"
mkdir -p "$WORKDIR"

echo "=== Westlake APK Runner (x86_64) ==="
echo "APK:       $APK"
echo "Work dir:  $WORKDIR"

# --- Step 1: Extract DEX from APK --------------------------------------------
echo ""
echo "[1/5] Extracting DEX from APK..."

# APKs can contain classes.dex, classes2.dex, etc.
python3 -c "
import zipfile, sys, os
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

# --- Step 2: Analyze classes (list or verify main class) ----------------------
echo ""
echo "[2/5] Analyzing DEX classes..."

# Try dexdump if available
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

if [ -n "$DEXDUMP" ]; then
    CLASS_LIST=$("$DEXDUMP" -f "$PRIMARY_DEX" 2>/dev/null \
        | grep "Class descriptor" \
        | sed "s/.*'L//; s/;'//; s|/|.|g" \
        | sort || true)
else
    # Fallback: use Python to read DEX header for class names
    CLASS_LIST=$(python3 -c "
import struct
with open('$PRIMARY_DEX', 'rb') as f:
    data = f.read()
# Minimal DEX parsing - just report file size
print('(dexdump not available - cannot list classes)')
print('DEX size: ' + str(len(data)) + ' bytes')
" 2>/dev/null || echo "(could not parse DEX)")
fi

if [ -z "$MAIN_CLASS" ]; then
    echo "No main class specified. Classes found in DEX:"
    echo "$CLASS_LIST" | head -50
    if [ "$(echo "$CLASS_LIST" | wc -l)" -gt 50 ]; then
        echo "  ... (truncated, $(echo "$CLASS_LIST" | wc -l) total)"
    fi
    echo ""
    echo "Re-run with: $0 $APK <MainClass>"
    exit 0
else
    echo "  Main class: $MAIN_CLASS"
    # Check if class exists in DEX (best effort)
    if [ -n "$DEXDUMP" ]; then
        if echo "$CLASS_LIST" | grep -qF "$MAIN_CLASS"; then
            echo "  (confirmed in DEX)"
        else
            echo "  WARNING: $MAIN_CLASS not found in DEX class list"
            echo "  (proceeding anyway - may be in secondary DEX or inner class)"
        fi
    fi
fi

# --- Step 3: AOT compile with dex2oat -----------------------------------------
echo ""
echo "[3/5] AOT compiling DEX with dex2oat (x86_64)..."

# dex2oat requires ANDROID_ROOT and ANDROID_DATA
export ANDROID_DATA=/tmp/android-data
export ANDROID_ROOT=/tmp/android-root
mkdir -p "$ANDROID_DATA/dalvik-cache/x86_64"
mkdir -p "$ANDROID_ROOT/bin"

# Collect all DEX files for multi-dex APKs
DEX_ARGS="--dex-file=$PRIMARY_DEX"
for extra_dex in "$WORKDIR"/classes[0-9]*.dex; do
    [ -f "$extra_dex" ] && [ "$extra_dex" != "$PRIMARY_DEX" ] && \
        DEX_ARGS="$DEX_ARGS --dex-file=$extra_dex"
done

OAT_FILE="$WORKDIR/app.oat"

if [ -f "$DEX2OAT" ]; then
    # Build dex2oat boot classpath args
    DEX2OAT_BCP=""
    for jar in "$BCP_DIR"/core-oj.jar "$BCP_DIR"/core-libart.jar "$BCP_DIR"/core-icu4j.jar; do
        [ -f "$jar" ] && DEX2OAT_BCP="${DEX2OAT_BCP:+$DEX2OAT_BCP:}$jar"
    done

    set +e
    "$DEX2OAT" \
        $DEX_ARGS \
        --oat-file="$OAT_FILE" \
        --android-root="$ANDROID_ROOT" \
        --instruction-set=x86_64 \
        --compiler-filter=speed \
        --runtime-arg -Xverify:none \
        ${BOOT_IMAGE:+--boot-image=$BOOT_IMAGE} \
        -j4 \
        2>&1 | tee "$WORKDIR/dex2oat.log"
    DEX2OAT_RC=${PIPESTATUS[0]}
    set -e

    if [ $DEX2OAT_RC -eq 0 ] && [ -f "$OAT_FILE" ]; then
        echo "  AOT compiled: $OAT_FILE ($(stat -c%s "$OAT_FILE") bytes)"

        # Place .oat where ART expects it (dalvik-cache structure)
        mkdir -p "$WORKDIR/oat/x86_64"
        cp "$OAT_FILE" "$WORKDIR/oat/x86_64/classes.oat"
        echo "  Installed to: $WORKDIR/oat/x86_64/classes.oat"
    else
        echo "  WARNING: dex2oat failed (rc=$DEX2OAT_RC), will run interpreted"
        echo "  Log: $WORKDIR/dex2oat.log"
    fi
else
    echo "  WARNING: dex2oat not found at $DEX2OAT, will run interpreted"
fi

# --- Step 4: Set up ART environment -------------------------------------------
echo ""
echo "[4/5] Setting up ART environment..."

export LD_LIBRARY_PATH="$ART_LIB"

echo "  ANDROID_DATA=$ANDROID_DATA"
echo "  ANDROID_ROOT=$ANDROID_ROOT"
echo "  LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
echo "  Boot image: $BOOT_IMAGE"
echo "  Boot classpath: $BCP"

# --- Step 5: Launch on ART dalvikvm -------------------------------------------
echo ""
echo "[5/5] Launching $MAIN_CLASS on ART dalvikvm..."
echo "----------------------------------------------------------------------"

if [ ! -f "$DALVIKVM" ]; then
    echo "ERROR: dalvikvm not found at $DALVIKVM"
    echo "Build it first: cd art-universal-build && make"
    exit 1
fi

DALVIKVM_ARGS=(
    -Xbootclasspath:"$BCP"
    -Xverify:none
    -Xnorelocate
)

# Add boot image if it exists
if [ -f "$BOOT_IMAGE" ]; then
    DALVIKVM_ARGS+=(-Ximage:"$BOOT_IMAGE")
fi

# Add library path for JNI (OHBridge etc.)
if [ -d "$REPO_ROOT/ohos-deploy" ]; then
    DALVIKVM_ARGS+=(-Djava.library.path="$REPO_ROOT/ohos-deploy")
fi

exec "$DALVIKVM" \
    "${DALVIKVM_ARGS[@]}" \
    -classpath "$PRIMARY_DEX" \
    "$MAIN_CLASS" \
    "${@:3}"
