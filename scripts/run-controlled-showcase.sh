#!/bin/bash
# Runs the controlled Noice-style showcase APK through the generic Westlake
# target backend path, captures evidence, and checks app-owned markers.

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
DALVIKVM_SRC="${DALVIKVM_SRC:-$REPO_ROOT/ohos-deploy/arm64-a15/dalvikvm}"
AOSP_SHIM_SRC="${AOSP_SHIM_SRC:-$REPO_ROOT/aosp-shim.dex}"
SHOWCASE_APK_SRC="${SHOWCASE_APK_SRC:-$REPO_ROOT/test-apps/05-controlled-showcase/build/dist/westlake-showcase-debug.apk}"
SHOWCASE_AUTO_INSTALL="${SHOWCASE_AUTO_INSTALL:-1}"
WAIT_SECS="${WAIT_SECS:-10}"
INTERACT="${INTERACT:-1}"
NETWORK_REQUIRED="${NETWORK_REQUIRED:-1}"
NETWORK_BRIDGE_REQUIRED="${NETWORK_BRIDGE_REQUIRED:-1}"
NETWORK_PREFETCH="${NETWORK_PREFETCH:-1}"

HOST_PKG="com.westlake.host"
HOST_ACTIVITY="com.westlake.host/.WestlakeActivity"
SHOWCASE_PKG="com.westlake.showcase"
SHOWCASE_ACTIVITY="com.westlake.showcase.ShowcaseActivity"
LABEL="controlled_showcase_target"

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
VISUAL_PATH="$ARTIFACT_DIR/${LABEL}.visual"
RUNAS_MARKER_PATH="/data/user/0/$HOST_PKG/files/vm/cutoff_canary_markers.log"
RUNAS_TRACE_PATH="/data/user/0/$HOST_PKG/files/vm/cutoff_canary_trace.log"
PUBLIC_MARKER_PATH="$PHONE_DIR/cutoff_canary_markers.log"
PUBLIC_TRACE_PATH="$PHONE_DIR/cutoff_canary_trace.log"
HOST_TOUCH_PATH_A="/sdcard/Android/data/$HOST_PKG/files/westlake_touch.dat"
HOST_TOUCH_PATH_B="/storage/emulated/0/Android/data/$HOST_PKG/files/westlake_touch.dat"
HOST_FIXTURE_DIR="/sdcard/Android/data/$HOST_PKG/files"
HOST_FIXTURE_JSON="$HOST_FIXTURE_DIR/showcase_venues.json"
HOST_FIXTURE_IMAGE="$HOST_FIXTURE_DIR/showcase_venue.png"
VENUE_JSON_URL="${VENUE_JSON_URL:-http://httpbin.org/base64/eyJ2ZW51ZXMiOlt7Im5hbWUiOiJXZXN0bGFrZSBDYWZlIiwiY2F0ZWdvcnkiOiJDYWZlIiwicmF0aW5nIjo0LjcsInJldmlld0NvdW50IjoxMjgsIm1lYWxUeXBlIjoiQ29mZmVlIiwiaW1hZ2UiOiJodHRwOi8vaHR0cGJpbi5vcmcvaW1hZ2UvcG5nIn0seyJuYW1lIjoiUmFpbiBSb29tIiwiY2F0ZWdvcnkiOiJTb3VuZCBiYXIiLCJyYXRpbmciOjQuNSwicmV2aWV3Q291bnQiOjg0LCJtZWFsVHlwZSI6IkFtYmllbnQiLCJpbWFnZSI6Imh0dHA6Ly9odHRwYmluLm9yZy9pbWFnZS9wbmcifSx7Im5hbWUiOiJTbGVlcCBLaXRjaGVuIiwiY2F0ZWdvcnkiOiJOaWdodCBiaXRlcyIsInJhdGluZyI6NC44LCJyZXZpZXdDb3VudCI6MjE0LCJtZWFsVHlwZSI6IkxhdGUiLCJpbWFnZSI6Imh0dHA6Ly9odHRwYmluLm9yZy9pbWFnZS9wbmcifV19}"
VENUE_IMAGE_URL="${VENUE_IMAGE_URL:-http://httpbin.org/image/png}"

echo "=== Westlake Controlled Showcase Run ==="
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
if [ ! -f "$SHOWCASE_APK_SRC" ]; then
    echo "ERROR: showcase APK not found: $SHOWCASE_APK_SRC" >&2
    echo "       run test-apps/05-controlled-showcase/build-apk.sh" >&2
    exit 2
fi

read_installed_showcase_apk_hash() {
    remote_showcase_path="$("${ADB[@]}" shell pm path "$SHOWCASE_PKG" 2>/dev/null \
        | tr -d '\r' \
        | sed -n 's/^package://p' \
        | head -n 1 || true)"
    if [ -z "$remote_showcase_path" ]; then
        remote_showcase_hash=""
        return 0
    fi
    remote_showcase_hash="$("${ADB[@]}" shell sha256sum "$remote_showcase_path" 2>/dev/null \
        | awk '{print $1}' || true)"
}

echo "[0/6] Runtime provenance preflight..."
local_dvm_hash="$(sha256sum "$DALVIKVM_SRC" | awk '{print $1}')"
local_shim_hash="$(sha256sum "$AOSP_SHIM_SRC" | awk '{print $1}')"
local_showcase_hash="$(sha256sum "$SHOWCASE_APK_SRC" | awk '{print $1}')"
remote_dvm_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/dalvikvm" 2>/dev/null | awk '{print $1}' || true)"
remote_shim_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/aosp-shim.dex" 2>/dev/null | awk '{print $1}' || true)"
read_installed_showcase_apk_hash
echo "  dalvikvm local=$local_dvm_hash phone=${remote_dvm_hash:-missing}"
echo "  aosp-shim local=$local_shim_hash phone=${remote_shim_hash:-missing}"
echo "  showcase.apk local=$local_showcase_hash phone=${remote_showcase_hash:-missing}"

if [ "$remote_dvm_hash" != "$local_dvm_hash" ]; then
    echo "ERROR: phone dalvikvm hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_shim_hash" != "$local_shim_hash" ]; then
    echo "ERROR: phone aosp-shim.dex hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_showcase_hash" != "$local_showcase_hash" ]; then
    if [ "$SHOWCASE_AUTO_INSTALL" = "1" ]; then
        echo "  installing showcase APK to match local hash..."
        "${ADB[@]}" install -r "$SHOWCASE_APK_SRC" >/dev/null
        read_installed_showcase_apk_hash
        echo "  showcase.apk phone=${remote_showcase_hash:-missing}"
    else
        echo "ERROR: phone showcase APK hash mismatch; install $SHOWCASE_APK_SRC" >&2
        exit 3
    fi
fi
if [ "$remote_showcase_hash" != "$local_showcase_hash" ]; then
    echo "ERROR: phone showcase APK hash mismatch after install" >&2
    exit 3
fi

if [ "$NETWORK_PREFETCH" = "1" ]; then
    echo "[0b/6] Prefetching remote venue fixtures for controlled network UI..."
    local_fixture_json="$ARTIFACT_DIR/showcase_venues.json"
    local_fixture_image="$ARTIFACT_DIR/showcase_venue.png"
    if curl -fsSL --max-time 15 "$VENUE_JSON_URL" -o "$local_fixture_json" \
            && curl -fsSL --max-time 15 "$VENUE_IMAGE_URL" -o "$local_fixture_image"; then
        echo "  venue fixture bytes: json=$(wc -c < "$local_fixture_json") image=$(wc -c < "$local_fixture_image")"
        echo "  endpoint shape verified; guest fetch should use host/OHBridge HTTP bridge"
    else
        echo "  warning: remote fixture prefetch failed; app will use embedded fallback" >&2
    fi
fi

echo "[1/6] Force-stopping old app state..."
"${ADB[@]}" shell am force-stop "$HOST_PKG" >/dev/null || true
"${ADB[@]}" shell am force-stop "$SHOWCASE_PKG" >/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" mkdir -p "/data/user/0/$HOST_PKG/files/vm" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_MARKER_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$PUBLIC_MARKER_PATH" "$PUBLIC_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$HOST_TOUCH_PATH_A" "$HOST_TOUCH_PATH_B" >/dev/null 2>&1 || true

echo "[2/6] Clearing logcat..."
"${ADB[@]}" logcat -c

echo "[3/6] Launching Westlake host generic APK path..."
"${ADB[@]}" shell am start -S -W -n "$HOST_ACTIVITY" \
    --es launch "VM_APK:${SHOWCASE_PKG}:${SHOWCASE_ACTIVITY}:WestlakeNoiceLab"

echo "[4/6] Waiting ${WAIT_SECS}s for settle..."
sleep "$WAIT_SECS"

if [ "$INTERACT" = "1" ]; then
    echo "[4b/6] Sending navigation/action interactions..."
    "${ADB[@]}" shell input tap 130 520 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 130 850 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 405 1940 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 250 850 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 760 850 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 675 1940 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 540 850 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 945 1940 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 250 1250 >/dev/null 2>&1 || true
    sleep 8
    "${ADB[@]}" shell input tap 250 1390 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 760 1390 >/dev/null 2>&1 || true
    sleep 1
    "${ADB[@]}" shell input tap 760 1250 >/dev/null 2>&1 || true
    sleep 3
fi

echo "[5/6] Capturing artifacts..."
"${ADB[@]}" logcat -d > "$LOG_PATH"
"${ADB[@]}" shell ps -A | grep -E 'westlake|dalvikvm|showcase' > "$PS_PATH" || true
"${ADB[@]}" shell dumpsys activity top > "$TOP_PATH"
"${ADB[@]}" exec-out screencap -p > "$PNG_PATH"
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_MARKER_PATH" > "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_MARKER_PATH" >> "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_TRACE_PATH" > "$TRACE_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_TRACE_PATH" >> "$TRACE_PATH" 2>/dev/null || true
grep -nE "SHOWCASE_|WestlakeLauncher|Launching VM APK|APK load error|main fatal|FATAL|SIGBUS|SIGILL" \
    "$LOG_PATH" > "$LOG_MARKERS_PATH" || true

echo "[6/6] Summary..."
echo "Processes:"
if [ -s "$PS_PATH" ]; then
    cat "$PS_PATH"
else
    echo "(no matching processes)"
fi

echo ""
echo "Key log markers:"
cat "$LOG_MARKERS_PATH" || true

if [ -s "$MARKERS_PATH" ]; then
    echo ""
    echo "App-owned showcase markers:"
    cat "$MARKERS_PATH"
else
    echo ""
    echo "App-owned showcase markers: (none)"
fi

missing=0
require_marker() {
    local pattern="$1"
    local label="$2"
    if ! grep -qE "$pattern" "$MARKERS_PATH"; then
        echo "ERROR: missing showcase marker: $label" >&2
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

require_marker "^SHOWCASE_APP_ON_CREATE_OK " "SHOWCASE_APP_ON_CREATE_OK"
require_marker "^SHOWCASE_ACTIVITY_ON_CREATE_OK " "SHOWCASE_ACTIVITY_ON_CREATE_OK"
require_marker "^SHOWCASE_XML_INFLATE_OK " "SHOWCASE_XML_INFLATE_OK"
require_marker "^SHOWCASE_XML_BIND_OK " "SHOWCASE_XML_BIND_OK"
require_marker "^SHOWCASE_XML_LAYOUT_PROBE_OK " "SHOWCASE_XML_LAYOUT_PROBE_OK"
require_marker "^SHOWCASE_XML_API_SURFACE_OK " "SHOWCASE_XML_API_SURFACE_OK"
require_marker "^SHOWCASE_UI_BUILD_OK " "SHOWCASE_UI_BUILD_OK"
require_marker "^SHOWCASE_ON_START_OK " "SHOWCASE_ON_START_OK"
require_marker "^SHOWCASE_ON_RESUME_OK " "SHOWCASE_ON_RESUME_OK"
require_marker "^SHOWCASE_XML_TREE_RENDER_OK " "SHOWCASE_XML_TREE_RENDER_OK"
require_marker "^SHOWCASE_DIRECT_FRAME_OK " "SHOWCASE_DIRECT_FRAME_OK"
if [ "$INTERACT" = "1" ]; then
    require_marker "^SHOWCASE_TOUCH_POLL_READY " "SHOWCASE_TOUCH_POLL_READY"
    require_marker "^SHOWCASE_TOUCH_POLL_OK " "SHOWCASE_TOUCH_POLL_OK"
    require_marker "^SHOWCASE_PLAY_TOGGLE_OK " "SHOWCASE_PLAY_TOGGLE_OK"
    require_marker "^SHOWCASE_SOUND_SELECT_OK " "SHOWCASE_SOUND_SELECT_OK"
    require_marker "^SHOWCASE_NAV_MIXER_OK " "SHOWCASE_NAV_MIXER_OK"
    require_marker "^SHOWCASE_ADD_LAYER_OK " "SHOWCASE_ADD_LAYER_OK"
    require_marker "^SHOWCASE_SAVE_MIX_OK " "SHOWCASE_SAVE_MIX_OK"
    require_marker "^SHOWCASE_NAV_TIMER_OK " "SHOWCASE_NAV_TIMER_OK"
    require_marker "^SHOWCASE_TIMER_SET_OK " "SHOWCASE_TIMER_SET_OK"
    require_marker "^SHOWCASE_NAV_SETTINGS_OK " "SHOWCASE_NAV_SETTINGS_OK"
    require_marker "^SHOWCASE_EXPORT_BUNDLE_OK " "SHOWCASE_EXPORT_BUNDLE_OK"
    if [ "$NETWORK_REQUIRED" = "1" ]; then
        require_marker "^SHOWCASE_NETWORK_FETCH_BEGIN " "SHOWCASE_NETWORK_FETCH_BEGIN"
        if [ "$NETWORK_BRIDGE_REQUIRED" = "1" ]; then
            require_marker "^SHOWCASE_NETWORK_HOST_BRIDGE_OK " "SHOWCASE_NETWORK_HOST_BRIDGE_OK"
        else
            require_marker "^SHOWCASE_NETWORK_NATIVE_GAP_OK " "SHOWCASE_NETWORK_NATIVE_GAP_OK"
        fi
        require_marker "^SHOWCASE_NETWORK_JSON_OK " "SHOWCASE_NETWORK_JSON_OK"
        require_marker "^SHOWCASE_NETWORK_IMAGE_OK " "SHOWCASE_NETWORK_IMAGE_OK"
        require_marker "^SHOWCASE_YELP_CARD_OK " "SHOWCASE_YELP_CARD_OK"
        require_marker "^SHOWCASE_VENUE_NEXT_OK " "SHOWCASE_VENUE_NEXT_OK"
        require_marker "^SHOWCASE_VENUE_REVIEW_OK " "SHOWCASE_VENUE_REVIEW_OK"
    fi
    if grep -qE "^SHOWCASE_PLAY_TOGGLE_OK synthetic=true" "$MARKERS_PATH"; then
        echo "ERROR: synthetic showcase interaction marker present" >&2
        missing=1
    fi
fi
if [ "$NETWORK_REQUIRED" = "1" ] && [ "$NETWORK_BRIDGE_REQUIRED" = "1" ] \
        && grep -qE "^SHOWCASE_NETWORK_NATIVE_GAP_OK " "$MARKERS_PATH"; then
    echo "ERROR: native-gap network fallback marker present during bridge-required run" >&2
    missing=1
fi
if grep -qE "^SHOWCASE_NETWORK_FETCH_FAIL " "$MARKERS_PATH" && [ "$NETWORK_REQUIRED" = "1" ]; then
    echo "ERROR: network showcase marker failed" >&2
    grep -E "^SHOWCASE_NETWORK_FETCH_FAIL " "$MARKERS_PATH" >&2 || true
    missing=1
fi
if grep -qE "^SHOWCASE_DIRECT_FRAME_FAIL " "$MARKERS_PATH"; then
    echo "ERROR: forbidden showcase marker present: SHOWCASE_DIRECT_FRAME_FAIL" >&2
    missing=1
fi
if grep -qE "^SHOWCASE_XML_(API_GAP|LAYOUT_PROBE_FAIL|TREE_RENDER_FAIL) " "$MARKERS_PATH"; then
    echo "ERROR: forbidden showcase XML marker present" >&2
    grep -E "^SHOWCASE_XML_(API_GAP|LAYOUT_PROBE_FAIL|TREE_RENDER_FAIL) " "$MARKERS_PATH" >&2 || true
    missing=1
fi
reject_log_marker "APK load error|FATAL EXCEPTION|SIGBUS|SIGILL" \
    "fatal runtime/log error"

if ! python3 - "$PNG_PATH" "$VISUAL_PATH" <<'PY'
import sys
from PIL import Image

png_path, visual_path = sys.argv[1], sys.argv[2]
img = Image.open(png_path).convert("RGB")
w, h = img.size
if w < 200 or h < 400:
    raise SystemExit("screenshot too small")

step = max(1, min(w, h) // 160)
sampled = []
colored = 0
for y in range(0, h, step):
    for x in range(0, w, step):
        r, g, b = img.getpixel((x, y))
        sampled.append((r, g, b))
        if max(r, g, b) - min(r, g, b) > 20 and not (r > 245 and g > 245 and b > 245):
            colored += 1
distinct = len(set(sampled))

nav_teal = 0
x0, x1 = int(w * 0.70), int(w * 0.99)
y0, y1 = int(h * 0.82), int(h * 0.95)
for y in range(y0, y1, step):
    for x in range(x0, x1, step):
        r, g, b = img.getpixel((x, y))
        if r < 80 and 70 <= g <= 140 and 70 <= b <= 140:
            nav_teal += 1

with open(visual_path, "w", encoding="utf-8") as out:
    out.write(f"size={w}x{h}\n")
    out.write(f"sample_step={step}\n")
    out.write(f"distinct_colors={distinct}\n")
    out.write(f"colored_samples={colored}\n")
    out.write(f"settings_nav_teal_samples={nav_teal}\n")

if distinct < 16:
    raise SystemExit("too few screenshot colors")
if colored < 400:
    raise SystemExit("screenshot appears blank")
if nav_teal < 12:
    raise SystemExit("settings navigation highlight not visible")
PY
then
    echo "ERROR: screenshot visual gate failed" >&2
    missing=1
fi

if [ "$missing" -ne 0 ]; then
    echo ""
    echo "Showcase acceptance: FAILED"
    echo "Artifacts:"
    echo "  log:     $LOG_PATH"
    echo "  markers: $MARKERS_PATH"
    echo "  trace:   $TRACE_PATH"
    echo "  screen:  $PNG_PATH"
    echo "  visual:  $VISUAL_PATH"
    exit 4
fi

echo ""
echo "Showcase acceptance: PASSED"
echo "Hashes:"
echo "  dalvikvm=$local_dvm_hash"
echo "  aosp-shim.dex=$local_shim_hash"
echo "  westlake-showcase-debug.apk=$local_showcase_hash"
echo "Artifacts:"
echo "  log:     $LOG_PATH"
echo "  markers: $MARKERS_PATH"
echo "  trace:   $TRACE_PATH"
echo "  screen:  $PNG_PATH"
echo "  visual:  $VISUAL_PATH"
