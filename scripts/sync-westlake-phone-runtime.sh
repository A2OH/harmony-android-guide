#!/bin/bash
# Usage:
#   ./scripts/sync-westlake-phone-runtime.sh
#
# Sync the shared Westlake guest runtime payload that the phone host uses from
# /data/local/tmp/westlake. This keeps the real phone run aligned with the
# current local dalvikvm, core-oj.jar, core-libart.jar, core-icu4j.jar,
# bouncycastle.jar, and aosp-shim.dex.

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEFAULT_WINDOWS_ADB="/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe"
if [ -z "${ADB_BIN:-}" ] && [ -x "$DEFAULT_WINDOWS_ADB" ]; then
    ADB_BIN="$DEFAULT_WINDOWS_ADB"
else
    ADB_BIN="${ADB_BIN:-adb}"
fi
ADB_HOST="${ADB_HOST:-}"
ADB_PORT="${ADB_PORT:-5037}"
ADB_SERIAL="${ADB_SERIAL:-cfb7c9e3}"
ADB_TIMEOUT="${ADB_TIMEOUT:-30}"
PHONE_DIR="${PHONE_DIR:-/data/local/tmp/westlake}"
DALVIKVM_SRC="${DALVIKVM_SRC:-$REPO_ROOT/ohos-deploy/arm64-a15/dalvikvm}"
CORE_OJ_SRC="${CORE_OJ_SRC:-$REPO_ROOT/ohos-deploy/arm64-a15/core-oj.jar}"
CORE_LIBART_SRC="${CORE_LIBART_SRC:-$REPO_ROOT/ohos-deploy/arm64-a15/core-libart.jar}"
CORE_ICU4J_SRC="${CORE_ICU4J_SRC:-$REPO_ROOT/ohos-deploy/arm64-a15/core-icu4j.jar}"
BOUNCYCASTLE_SRC="${BOUNCYCASTLE_SRC:-$REPO_ROOT/westlake-host-gradle/app/src/main/assets/bouncycastle.jar}"
AOSP_SHIM_SRC="${AOSP_SHIM_SRC:-$REPO_ROOT/aosp-shim.dex}"

if [ -n "$ADB_HOST" ]; then
    ADB=("$ADB_BIN" -H "$ADB_HOST" -P "$ADB_PORT" -s "$ADB_SERIAL")
else
    ADB=("$ADB_BIN" -s "$ADB_SERIAL")
fi

if [ ! -f "$DALVIKVM_SRC" ]; then
    echo "ERROR: dalvikvm source not found: $DALVIKVM_SRC" >&2
    exit 1
fi

if [ ! -f "$CORE_OJ_SRC" ]; then
    echo "ERROR: core-oj source not found: $CORE_OJ_SRC" >&2
    exit 1
fi

if [ ! -f "$CORE_LIBART_SRC" ]; then
    echo "ERROR: core-libart source not found: $CORE_LIBART_SRC" >&2
    exit 1
fi

if [ ! -f "$CORE_ICU4J_SRC" ]; then
    echo "ERROR: core-icu4j source not found: $CORE_ICU4J_SRC" >&2
    exit 1
fi

if [ ! -f "$BOUNCYCASTLE_SRC" ]; then
    echo "ERROR: bouncycastle source not found: $BOUNCYCASTLE_SRC" >&2
    exit 1
fi

if [ ! -f "$AOSP_SHIM_SRC" ]; then
    echo "ERROR: aosp-shim source not found: $AOSP_SHIM_SRC" >&2
    exit 1
fi

if [ "${SKIP_SYMBOL_GATE:-0}" != "1" ]; then
    "$REPO_ROOT/scripts/check-westlake-runtime-symbols.sh" "$DALVIKVM_SRC"
fi

STAMP="$(date +%Y%m%d-%H%M%S)"

echo "=== Westlake Phone Runtime Sync ==="
echo "ADB binary:    $ADB_BIN"
echo "ADB server:    ${ADB_HOST:-default}:$ADB_PORT"
echo "Device:        $ADB_SERIAL"
echo "Phone dir:     $PHONE_DIR"
echo "dalvikvm src:  $DALVIKVM_SRC"
echo "core-oj src:   $CORE_OJ_SRC"
echo "core-libart:   $CORE_LIBART_SRC"
echo "core-icu4j:    $CORE_ICU4J_SRC"
echo "bouncycastle:  $BOUNCYCASTLE_SRC"
echo "aosp-shim src: $AOSP_SHIM_SRC"

timeout "$ADB_TIMEOUT" "${ADB[@]}" get-state >/dev/null
"${ADB[@]}" shell mkdir -p "$PHONE_DIR"

for remote_name in dalvikvm core-oj.jar core-libart.jar core-icu4j.jar bouncycastle.jar aosp-shim.dex framework.jar services.jar ext.jar framework-res.apk; do
    if "${ADB[@]}" shell "[ -f '$PHONE_DIR/$remote_name' ]" >/dev/null 2>&1; then
        "${ADB[@]}" shell cp "$PHONE_DIR/$remote_name" "$PHONE_DIR/$remote_name.bak.$STAMP"
    fi
done

"${ADB[@]}" push "$DALVIKVM_SRC" "$PHONE_DIR/dalvikvm" >/dev/null
"${ADB[@]}" push "$CORE_OJ_SRC" "$PHONE_DIR/core-oj.jar" >/dev/null
"${ADB[@]}" push "$CORE_LIBART_SRC" "$PHONE_DIR/core-libart.jar" >/dev/null
"${ADB[@]}" push "$CORE_ICU4J_SRC" "$PHONE_DIR/core-icu4j.jar" >/dev/null
"${ADB[@]}" push "$BOUNCYCASTLE_SRC" "$PHONE_DIR/bouncycastle.jar" >/dev/null
"${ADB[@]}" push "$AOSP_SHIM_SRC" "$PHONE_DIR/aosp-shim.dex" >/dev/null
# framework.jar / services.jar / ext.jar / framework-res.apk are pulled
# from a real Android device once with `adb pull /system/framework/<name>`
# and staged at FRAMEWORK_SRC. Optional: skip if not present locally so
# the script stays compatible with shim-only runs while we are bringing
# real framework.jar online.
FRAMEWORK_SRC="${FRAMEWORK_SRC:-$REPO_ROOT/ohos-deploy/arm64-a15}"
for fw_name in framework.jar services.jar ext.jar framework-res.apk; do
    src="$FRAMEWORK_SRC/$fw_name"
    if [ -f "$src" ]; then
        "${ADB[@]}" push "$src" "$PHONE_DIR/$fw_name" >/dev/null
    elif [ -f "/tmp/phone-framework/$fw_name" ]; then
        "${ADB[@]}" push "/tmp/phone-framework/$fw_name" "$PHONE_DIR/$fw_name" >/dev/null
    fi
done
"${ADB[@]}" shell chmod 0777 "$PHONE_DIR/dalvikvm" "$PHONE_DIR/core-oj.jar" "$PHONE_DIR/core-libart.jar" "$PHONE_DIR/core-icu4j.jar" "$PHONE_DIR/bouncycastle.jar" "$PHONE_DIR/aosp-shim.dex"
"${ADB[@]}" shell "chmod 0644 $PHONE_DIR/framework.jar $PHONE_DIR/services.jar $PHONE_DIR/ext.jar $PHONE_DIR/framework-res.apk 2>/dev/null || true"

echo
echo "Local hashes:"
sha256sum "$DALVIKVM_SRC" "$CORE_OJ_SRC" "$CORE_LIBART_SRC" "$CORE_ICU4J_SRC" "$BOUNCYCASTLE_SRC" "$AOSP_SHIM_SRC"

echo
echo "Phone hashes:"
"${ADB[@]}" shell sha256sum "$PHONE_DIR/dalvikvm" "$PHONE_DIR/core-oj.jar" "$PHONE_DIR/core-libart.jar" "$PHONE_DIR/core-icu4j.jar" "$PHONE_DIR/bouncycastle.jar" "$PHONE_DIR/aosp-shim.dex"
