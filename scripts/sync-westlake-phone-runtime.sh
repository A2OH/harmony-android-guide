#!/bin/bash
# Usage:
#   ./scripts/sync-westlake-phone-runtime.sh
#
# Sync the shared Westlake guest runtime payload that the phone host uses from
# /data/local/tmp/westlake. This keeps the real phone run aligned with the
# current local dalvikvm and aosp-shim.dex.

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
DALVIKVM_SRC="${DALVIKVM_SRC:-/home/dspfac/art-latest/build-bionic-arm64/bin/dalvikvm}"
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
echo "aosp-shim src: $AOSP_SHIM_SRC"

timeout "$ADB_TIMEOUT" "${ADB[@]}" get-state >/dev/null
"${ADB[@]}" shell mkdir -p "$PHONE_DIR"

for remote_name in dalvikvm aosp-shim.dex; do
    if "${ADB[@]}" shell "[ -f '$PHONE_DIR/$remote_name' ]" >/dev/null 2>&1; then
        "${ADB[@]}" shell cp "$PHONE_DIR/$remote_name" "$PHONE_DIR/$remote_name.bak.$STAMP"
    fi
done

"${ADB[@]}" push "$DALVIKVM_SRC" "$PHONE_DIR/dalvikvm" >/dev/null
"${ADB[@]}" push "$AOSP_SHIM_SRC" "$PHONE_DIR/aosp-shim.dex" >/dev/null
"${ADB[@]}" shell chmod 0777 "$PHONE_DIR/dalvikvm" "$PHONE_DIR/aosp-shim.dex"

echo
echo "Local hashes:"
sha256sum "$DALVIKVM_SRC" "$AOSP_SHIM_SRC"

echo
echo "Phone hashes:"
"${ADB[@]}" shell sha256sum "$PHONE_DIR/dalvikvm" "$PHONE_DIR/aosp-shim.dex"
