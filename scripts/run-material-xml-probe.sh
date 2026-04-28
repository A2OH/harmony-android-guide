#!/bin/bash
# Runs the Material XML probe APK through Westlake's generic APK path and
# checks that compiled XML inflated Material shim classes receive generic touch.

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ARTIFACT_DIR="${ARTIFACT_DIR:-/mnt/c/Users/dspfa/TempWestlake}"
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
PROBE_APK_SRC="${PROBE_APK_SRC:-$REPO_ROOT/test-apps/09-material-xml-probe/build/dist/westlake-material-xml-probe-debug.apk}"
PROBE_AUTO_INSTALL="${PROBE_AUTO_INSTALL:-1}"
WAIT_SECS="${WAIT_SECS:-5}"
INTERACT="${INTERACT:-1}"

HOST_PKG="com.westlake.host"
HOST_ACTIVITY="com.westlake.host/.WestlakeActivity"
PROBE_PKG="com.westlake.materialxmlprobe"
PROBE_ACTIVITY="com.westlake.materialxmlprobe.MaterialXmlProbeActivity"
LABEL="material_xml_probe_target"

if [ -n "$ADB_HOST" ]; then
    ADB=("$ADB_BIN" -H "$ADB_HOST" -P "$ADB_PORT" -s "$ADB_SERIAL")
else
    ADB=("$ADB_BIN" -s "$ADB_SERIAL")
fi

mkdir -p "$ARTIFACT_DIR"

LOG_PATH="$ARTIFACT_DIR/${LABEL}.log"
PS_PATH="$ARTIFACT_DIR/${LABEL}.ps"
PNG_PATH="$ARTIFACT_DIR/${LABEL}.png"
TOP_PATH="$ARTIFACT_DIR/${LABEL}.top"
MARKERS_PATH="$ARTIFACT_DIR/${LABEL}.markers"
TRACE_PATH="$ARTIFACT_DIR/${LABEL}.trace"
LOG_MARKERS_PATH="$ARTIFACT_DIR/${LABEL}.logmarkers"
RUNAS_MARKER_PATH="/data/user/0/$HOST_PKG/files/vm/cutoff_canary_markers.log"
RUNAS_TRACE_PATH="/data/user/0/$HOST_PKG/files/vm/cutoff_canary_trace.log"
PUBLIC_MARKER_PATH="$PHONE_DIR/cutoff_canary_markers.log"
PUBLIC_TRACE_PATH="$PHONE_DIR/cutoff_canary_trace.log"
HOST_TOUCH_PATH_A="/sdcard/Android/data/$HOST_PKG/files/westlake_touch.dat"
HOST_TOUCH_PATH_B="/storage/emulated/0/Android/data/$HOST_PKG/files/westlake_touch.dat"

echo "=== Westlake Material XML Probe Run ==="
echo "ADB binary:  $ADB_BIN"
echo "ADB server:  ${ADB_HOST:-default}:$ADB_PORT"
echo "Device:      $ADB_SERIAL"
echo "Artifacts:   $ARTIFACT_DIR"

timeout "$ADB_TIMEOUT" "${ADB[@]}" get-state >/dev/null

if [ ! -f "$DALVIKVM_SRC" ]; then
    echo "ERROR: dalvikvm source not found: $DALVIKVM_SRC" >&2
    exit 2
fi
if [ ! -f "$AOSP_SHIM_SRC" ]; then
    echo "ERROR: aosp-shim source not found: $AOSP_SHIM_SRC" >&2
    exit 2
fi
if [ ! -f "$PROBE_APK_SRC" ]; then
    echo "ERROR: Material XML probe APK not found: $PROBE_APK_SRC" >&2
    echo "       run test-apps/09-material-xml-probe/build-apk.sh" >&2
    exit 2
fi

read_installed_probe_apk_hash() {
    remote_probe_path="$("${ADB[@]}" shell pm path "$PROBE_PKG" 2>/dev/null \
        | tr -d '\r' \
        | sed -n 's/^package://p' \
        | head -n 1 || true)"
    if [ -z "$remote_probe_path" ]; then
        remote_probe_hash=""
        return 0
    fi
    remote_probe_hash="$("${ADB[@]}" shell sha256sum "$remote_probe_path" 2>/dev/null \
        | awk '{print $1}' || true)"
}

echo "[0/5] Runtime provenance preflight..."
local_dvm_hash="$(sha256sum "$DALVIKVM_SRC" | awk '{print $1}')"
local_shim_hash="$(sha256sum "$AOSP_SHIM_SRC" | awk '{print $1}')"
local_probe_hash="$(sha256sum "$PROBE_APK_SRC" | awk '{print $1}')"
remote_dvm_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/dalvikvm" 2>/dev/null | awk '{print $1}' || true)"
remote_shim_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/aosp-shim.dex" 2>/dev/null | awk '{print $1}' || true)"
read_installed_probe_apk_hash
echo "  dalvikvm local=$local_dvm_hash phone=${remote_dvm_hash:-missing}"
echo "  aosp-shim local=$local_shim_hash phone=${remote_shim_hash:-missing}"
echo "  probe.apk local=$local_probe_hash phone=${remote_probe_hash:-missing}"

if [ "$remote_dvm_hash" != "$local_dvm_hash" ]; then
    echo "ERROR: phone dalvikvm hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_shim_hash" != "$local_shim_hash" ]; then
    echo "ERROR: phone aosp-shim.dex hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_probe_hash" != "$local_probe_hash" ]; then
    if [ "$PROBE_AUTO_INSTALL" = "1" ]; then
        echo "  installing Material XML probe APK to match local hash..."
        "${ADB[@]}" install -r "$PROBE_APK_SRC" >/dev/null
        read_installed_probe_apk_hash
        echo "  probe.apk phone=${remote_probe_hash:-missing}"
    else
        echo "ERROR: phone Material XML probe APK hash mismatch; install $PROBE_APK_SRC" >&2
        exit 3
    fi
fi
if [ "$remote_probe_hash" != "$local_probe_hash" ]; then
    echo "ERROR: phone Material XML probe APK hash mismatch after install" >&2
    exit 3
fi

echo "[1/5] Force-stopping old app state..."
"${ADB[@]}" shell am force-stop "$HOST_PKG" >/dev/null || true
"${ADB[@]}" shell am force-stop "$PROBE_PKG" >/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" mkdir -p "/data/user/0/$HOST_PKG/files/vm" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_MARKER_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$PUBLIC_MARKER_PATH" "$PUBLIC_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$HOST_TOUCH_PATH_A" "$HOST_TOUCH_PATH_B" >/dev/null 2>&1 || true

echo "[2/5] Clearing logcat..."
"${ADB[@]}" logcat -c

echo "[3/5] Launching Westlake host generic APK path..."
"${ADB[@]}" shell am start -S -W -n "$HOST_ACTIVITY" \
    --es launch "VM_APK:${PROBE_PKG}:${PROBE_ACTIVITY}:WestlakeMaterialXmlProbe"

echo "[4/5] Waiting ${WAIT_SECS}s for settle..."
sleep "$WAIT_SECS"

if [ "$INTERACT" = "1" ]; then
    echo "[4b/5] Sending generic tap near MaterialButton..."
    guest_x=240
    guest_y=404
    PRE_BOUNDS_PATH="$ARTIFACT_DIR/${LABEL}.prebounds"
    "${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_MARKER_PATH" > "$PRE_BOUNDS_PATH" 2>/dev/null || true
    "${ADB[@]}" shell cat "$PUBLIC_MARKER_PATH" >> "$PRE_BOUNDS_PATH" 2>/dev/null || true
    bounds_line="$(grep -E "^MATERIAL_GENERIC_BUTTON_BOUNDS " "$PRE_BOUNDS_PATH" | tail -n 1 || true)"
    if [ -n "$bounds_line" ]; then
        left="$(echo "$bounds_line" | sed -n 's/.* left=\([0-9][0-9]*\).*/\1/p')"
        top="$(echo "$bounds_line" | sed -n 's/.* top=\([0-9][0-9]*\).*/\1/p')"
        right="$(echo "$bounds_line" | sed -n 's/.* right=\([0-9][0-9]*\).*/\1/p')"
        bottom="$(echo "$bounds_line" | sed -n 's/.* bottom=\([0-9][0-9]*\).*/\1/p')"
        if echo "$left $top $right $bottom" | grep -qE '^[0-9]+ [0-9]+ [0-9]+ [0-9]+$'; then
            guest_x=$(( (left + right) / 2 ))
            guest_y=$(( (top + bottom) / 2 ))
            echo "  MaterialButton guest bounds: $left,$top-$right,$bottom -> tap $guest_x,$guest_y"
        fi
    fi
    wm_size="$("${ADB[@]}" shell wm size 2>/dev/null | tr -d '\r' \
        | sed -n 's/^Physical size: //p' | tail -n 1)"
    device_w="${wm_size%x*}"
    device_h="${wm_size#*x}"
    if ! echo "$device_w" | grep -qE '^[0-9]+$' || ! echo "$device_h" | grep -qE '^[0-9]+$'; then
        device_w=1080
        device_h=2400
    fi
    coords="$(python3 - "$device_w" "$device_h" "$guest_x" "$guest_y" <<'PY'
import sys
w, h, fx, fy = map(float, sys.argv[1:])
scale = min(w / 480.0, h / 800.0)
ox = (w - 480.0 * scale) / 2.0
oy = (h - 800.0 * scale) / 2.0
print(f"{int(round(ox + fx * scale))} {int(round(oy + fy * scale))}")
PY
)"
    "${ADB[@]}" shell input tap $coords >/dev/null 2>&1 || true
    sleep 1
fi

echo "[5/5] Capturing artifacts..."
"${ADB[@]}" logcat -d > "$LOG_PATH"
"${ADB[@]}" shell ps -A | grep -E 'westlake|dalvikvm|materialxmlprobe' > "$PS_PATH" || true
"${ADB[@]}" shell dumpsys activity top > "$TOP_PATH"
"${ADB[@]}" exec-out screencap -p > "$PNG_PATH"
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_MARKER_PATH" > "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_MARKER_PATH" >> "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_TRACE_PATH" > "$TRACE_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_TRACE_PATH" >> "$TRACE_PATH" 2>/dev/null || true
grep -nE "MATERIAL_XML_|MATERIAL_GENERIC_|LAYOUT_INFLATER_|WestlakeLauncher|Launching VM APK|APK load error|main fatal|FATAL|SIGBUS|SIGILL" \
    "$LOG_PATH" > "$LOG_MARKERS_PATH" || true

echo ""
echo "Key log markers:"
cat "$LOG_MARKERS_PATH" || true

if [ -s "$MARKERS_PATH" ]; then
    echo ""
    echo "App-owned Material XML markers:"
    cat "$MARKERS_PATH"
else
    echo ""
    echo "App-owned Material XML markers: (none)"
fi

missing=0
require_marker() {
    local pattern="$1"
    local label="$2"
    if ! grep -qE "$pattern" "$MARKERS_PATH"; then
        echo "ERROR: missing Material XML marker: $label" >&2
        missing=1
    fi
}

reject_log_marker() {
    local pattern="$1"
    local label="$2"
    if grep -qE "$pattern" "$LOG_PATH"; then
        echo "ERROR: forbidden log marker present: $label" >&2
        missing=1
    fi
}

require_marker "^MATERIAL_XML_INFLATE_BEGIN " "MATERIAL_XML_INFLATE_BEGIN"
require_marker "^MATERIAL_XML_TAG_OK tag=TextInputLayout class=com\\.google\\.android\\.material\\.textfield\\.TextInputLayout" "TextInputLayout shim"
require_marker "^MATERIAL_XML_TAG_OK tag=TextInputEditText class=com\\.google\\.android\\.material\\.textfield\\.TextInputEditText" "TextInputEditText shim"
require_marker "^MATERIAL_XML_TAG_OK tag=MaterialButton class=com\\.google\\.android\\.material\\.button\\.MaterialButton" "MaterialButton shim"
require_marker "^MATERIAL_XML_TAG_OK tag=MaterialCardView class=com\\.google\\.android\\.material\\.card\\.MaterialCardView" "MaterialCardView shim"
require_marker "^MATERIAL_XML_TAG_OK tag=ChipGroup class=com\\.google\\.android\\.material\\.chip\\.ChipGroup" "ChipGroup shim"
require_marker "^MATERIAL_XML_TAG_OK tag=Chip class=com\\.google\\.android\\.material\\.chip\\.Chip" "Chip shim"
require_marker "^MATERIAL_XML_TAG_OK tag=Slider class=com\\.google\\.android\\.material\\.slider\\.Slider" "Slider shim"
require_marker "^MATERIAL_XML_TREE_OK " "MATERIAL_XML_TREE_OK"
require_marker "^MATERIAL_GENERIC_HIT_OK " "MATERIAL_GENERIC_HIT_OK"

if grep -qE "^MATERIAL_(XML|GENERIC)_.*_FAIL " "$MARKERS_PATH"; then
    echo "ERROR: Material XML failure marker present" >&2
    grep -E "^MATERIAL_(XML|GENERIC)_.*_FAIL " "$MARKERS_PATH" >&2 || true
    missing=1
fi
reject_log_marker "APK load error|FATAL EXCEPTION|SIGBUS|SIGILL" "fatal runtime/log error"

if [ "$missing" -ne 0 ]; then
    echo ""
    echo "Material XML probe acceptance: FAILED"
    echo "Artifacts:"
    echo "  log:     $LOG_PATH"
    echo "  markers: $MARKERS_PATH"
    echo "  trace:   $TRACE_PATH"
    echo "  screen:  $PNG_PATH"
    exit 4
fi

echo ""
echo "Material XML probe acceptance: PASSED"
echo "Hashes:"
echo "  dalvikvm=$local_dvm_hash"
echo "  aosp-shim.dex=$local_shim_hash"
echo "  westlake-material-xml-probe-debug.apk=$local_probe_hash"
echo "Artifacts:"
echo "  log:     $LOG_PATH"
echo "  markers: $MARKERS_PATH"
echo "  trace:   $TRACE_PATH"
echo "  screen:  $PNG_PATH"
