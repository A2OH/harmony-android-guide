#!/bin/bash
# Runs the Material Components canary APK through Westlake's generic APK path
# and checks Material class instantiation, touch routing, and DLST rendering.

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
MATERIAL_APK_SRC="${MATERIAL_APK_SRC:-$REPO_ROOT/test-apps/07-material-yelp/build/dist/westlake-material-yelp-debug.apk}"
MATERIAL_AUTO_INSTALL="${MATERIAL_AUTO_INSTALL:-1}"
WAIT_SECS="${WAIT_SECS:-7}"
INTERACT="${INTERACT:-1}"
SUPERVISOR_HTTP_PROXY="${SUPERVISOR_HTTP_PROXY:-1}"
SUPERVISOR_HTTP_PROXY_PORT="${SUPERVISOR_HTTP_PROXY_PORT:-8766}"

HOST_PKG="com.westlake.host"
HOST_ACTIVITY="com.westlake.host/.WestlakeActivity"
MATERIAL_PKG="com.westlake.materialyelp"
MATERIAL_ACTIVITY="com.westlake.materialyelp.MaterialYelpActivity"
LABEL="material_yelp_target"

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
HOST_PROXY_PATH_A="/sdcard/Android/data/$HOST_PKG/files/westlake_http_proxy_base.txt"
HOST_PROXY_PATH_B="/storage/emulated/0/Android/data/$HOST_PKG/files/westlake_http_proxy_base.txt"
PROXY_PID=""

cleanup() {
    if [ -n "${PROXY_PID:-}" ]; then
        kill "$PROXY_PID" >/dev/null 2>&1 || true
    fi
}
trap cleanup EXIT

start_supervisor_http_proxy() {
    python3 -u - "$SUPERVISOR_HTTP_PROXY_PORT" <<'PY' &
import http.server
import socketserver
import sys
import urllib.parse
import urllib.request

port = int(sys.argv[1])

class Handler(http.server.BaseHTTPRequestHandler):
    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        if parsed.path != "/proxy":
            self.send_error(404)
            return
        query = urllib.parse.parse_qs(parsed.query)
        url = query.get("url", [""])[0]
        allowed = (
            url.startswith("https://dummyjson.com/")
            or url.startswith("https://cdn.dummyjson.com/")
            or url.startswith("https://picsum.photos/")
        )
        if not allowed:
            self.send_error(403)
            return
        try:
            req = urllib.request.Request(url, headers={"User-Agent": "Westlake-supervisor-proxy/1.0"})
            with urllib.request.urlopen(req, timeout=20) as resp:
                body = resp.read(512 * 1024)
                self.send_response(resp.status)
                self.send_header("Content-Type", resp.headers.get("Content-Type", "application/octet-stream"))
                self.send_header("Content-Length", str(len(body)))
                self.end_headers()
                self.wfile.write(body)
        except Exception as exc:
            body = str(exc).encode("utf-8", "replace")
            self.send_response(599)
            self.send_header("Content-Type", "text/plain")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)

    def log_message(self, fmt, *args):
        sys.stderr.write("proxy: " + fmt % args + "\n")

class ReusableTCPServer(socketserver.TCPServer):
    allow_reuse_address = True

with ReusableTCPServer(("127.0.0.1", port), Handler) as httpd:
    httpd.serve_forever()
PY
    PROXY_PID="$!"
    sleep 1
    if ! kill -0 "$PROXY_PID" >/dev/null 2>&1; then
        echo "ERROR: failed to start supervisor HTTP proxy on port $SUPERVISOR_HTTP_PROXY_PORT" >&2
        exit 3
    fi
    "${ADB[@]}" reverse "tcp:$SUPERVISOR_HTTP_PROXY_PORT" "tcp:$SUPERVISOR_HTTP_PROXY_PORT" >/dev/null
    "${ADB[@]}" shell mkdir -p "/sdcard/Android/data/$HOST_PKG/files" >/dev/null 2>&1 || true
    "${ADB[@]}" shell "echo http://127.0.0.1:$SUPERVISOR_HTTP_PROXY_PORT > '$HOST_PROXY_PATH_A'" >/dev/null 2>&1 || true
    "${ADB[@]}" shell "echo http://127.0.0.1:$SUPERVISOR_HTTP_PROXY_PORT > '$HOST_PROXY_PATH_B'" >/dev/null 2>&1 || true
    echo "  supervisor HTTP proxy: http://127.0.0.1:$SUPERVISOR_HTTP_PROXY_PORT via adb reverse"
}

echo "=== Westlake Material Yelp Run ==="
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
if [ ! -f "$MATERIAL_APK_SRC" ]; then
    echo "ERROR: Material Yelp APK not found: $MATERIAL_APK_SRC" >&2
    echo "       run test-apps/07-material-yelp/build-apk.sh" >&2
    exit 2
fi

read_installed_material_apk_hash() {
    remote_material_path="$("${ADB[@]}" shell pm path "$MATERIAL_PKG" 2>/dev/null \
        | tr -d '\r' \
        | sed -n 's/^package://p' \
        | head -n 1 || true)"
    if [ -z "$remote_material_path" ]; then
        remote_material_hash=""
        return 0
    fi
    remote_material_hash="$("${ADB[@]}" shell sha256sum "$remote_material_path" 2>/dev/null \
        | awk '{print $1}' || true)"
}

echo "[0/5] Runtime provenance preflight..."
local_dvm_hash="$(sha256sum "$DALVIKVM_SRC" | awk '{print $1}')"
local_shim_hash="$(sha256sum "$AOSP_SHIM_SRC" | awk '{print $1}')"
local_material_hash="$(sha256sum "$MATERIAL_APK_SRC" | awk '{print $1}')"
remote_dvm_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/dalvikvm" 2>/dev/null | awk '{print $1}' || true)"
remote_shim_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/aosp-shim.dex" 2>/dev/null | awk '{print $1}' || true)"
read_installed_material_apk_hash
echo "  dalvikvm local=$local_dvm_hash phone=${remote_dvm_hash:-missing}"
echo "  aosp-shim local=$local_shim_hash phone=${remote_shim_hash:-missing}"
echo "  material.apk local=$local_material_hash phone=${remote_material_hash:-missing}"

if [ "$remote_dvm_hash" != "$local_dvm_hash" ]; then
    echo "ERROR: phone dalvikvm hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_shim_hash" != "$local_shim_hash" ]; then
    echo "ERROR: phone aosp-shim.dex hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_material_hash" != "$local_material_hash" ]; then
    if [ "$MATERIAL_AUTO_INSTALL" = "1" ]; then
        echo "  installing Material Yelp APK to match local hash..."
        "${ADB[@]}" install -r "$MATERIAL_APK_SRC" >/dev/null
        read_installed_material_apk_hash
        echo "  material.apk phone=${remote_material_hash:-missing}"
    else
        echo "ERROR: phone Material Yelp APK hash mismatch; install $MATERIAL_APK_SRC" >&2
        exit 3
    fi
fi
if [ "$remote_material_hash" != "$local_material_hash" ]; then
    echo "ERROR: phone Material Yelp APK hash mismatch after install" >&2
    exit 3
fi

if [ "$SUPERVISOR_HTTP_PROXY" = "1" ]; then
    echo "[0b/5] Starting supervisor HTTP proxy for Material image fetch..."
    start_supervisor_http_proxy
fi

echo "[1/5] Force-stopping old app state..."
"${ADB[@]}" shell am force-stop "$HOST_PKG" >/dev/null || true
"${ADB[@]}" shell am force-stop "$MATERIAL_PKG" >/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" mkdir -p "/data/user/0/$HOST_PKG/files/vm" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_MARKER_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$PUBLIC_MARKER_PATH" "$PUBLIC_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$HOST_TOUCH_PATH_A" "$HOST_TOUCH_PATH_B" >/dev/null 2>&1 || true
if [ "$SUPERVISOR_HTTP_PROXY" != "1" ]; then
    "${ADB[@]}" shell rm -f "$HOST_PROXY_PATH_A" "$HOST_PROXY_PATH_B" >/dev/null 2>&1 || true
fi

echo "[2/5] Clearing logcat..."
"${ADB[@]}" logcat -c

echo "[3/5] Launching Westlake host generic Material APK path..."
"${ADB[@]}" shell am start -S -W -n "$HOST_ACTIVITY" \
    --es launch "VM_APK:${MATERIAL_PKG}:${MATERIAL_ACTIVITY}:WestlakeMaterialYelp"

echo "[4/5] Waiting ${WAIT_SECS}s for settle..."
sleep "$WAIT_SECS"

if [ "$INTERACT" = "1" ]; then
    echo "[4b/5] Sending Material component interactions..."
    wm_size="$("${ADB[@]}" shell wm size 2>/dev/null | tr -d '\r' \
        | sed -n 's/^Physical size: //p' | tail -n 1)"
    device_w="${wm_size%x*}"
    device_h="${wm_size#*x}"
    if ! echo "$device_w" | grep -qE '^[0-9]+$' || ! echo "$device_h" | grep -qE '^[0-9]+$'; then
        device_w=1080
        device_h=2400
    fi
    frame_tap() {
        local fx="$1"
        local fy="$2"
        local coords
        coords="$(python3 - "$device_w" "$device_h" "$fx" "$fy" <<'PY'
import sys
w, h, fx, fy = map(float, sys.argv[1:])
scale = min(w / 480.0, h / 800.0)
ox = (w - 480.0 * scale) / 2.0
oy = (h - 800.0 * scale) / 2.0
print(f"{int(round(ox + fx * scale))} {int(round(oy + fy * scale))}")
PY
)"
        "${ADB[@]}" shell input tap $coords >/dev/null 2>&1 || true
    }
    frame_tap 80 124
    sleep 1
    frame_tap 260 124
    sleep 1
    frame_tap 112 430
    sleep 1
    frame_tap 420 430
    sleep 1
    frame_tap 356 610
    sleep 1
    frame_tap 420 725
    sleep 1
    frame_tap 180 725
    sleep 1
fi

echo "[5/5] Capturing artifacts..."
"${ADB[@]}" logcat -d > "$LOG_PATH"
"${ADB[@]}" shell ps -A | grep -E 'westlake|dalvikvm|materialyelp' > "$PS_PATH" || true
"${ADB[@]}" shell dumpsys activity top > "$TOP_PATH"
"${ADB[@]}" exec-out screencap -p > "$PNG_PATH"
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_MARKER_PATH" > "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_MARKER_PATH" >> "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_TRACE_PATH" > "$TRACE_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_TRACE_PATH" >> "$TRACE_PATH" 2>/dev/null || true
grep -nE "MATERIAL_|WestlakeLauncher|Surface buffer|Launching VM APK|APK load error|main fatal|FATAL|SIGBUS|SIGILL" \
    "$LOG_PATH" > "$LOG_MARKERS_PATH" || true

echo ""
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
    echo "App-owned Material markers:"
    cat "$MARKERS_PATH"
else
    echo ""
    echo "App-owned Material markers: (none)"
fi

missing=0
require_marker() {
    local pattern="$1"
    local label="$2"
    if ! grep -qE "$pattern" "$MARKERS_PATH"; then
        echo "ERROR: missing Material marker: $label" >&2
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

require_log_marker() {
    local pattern="$1"
    local label="$2"
    if ! grep -qE "$pattern" "$LOG_PATH"; then
        echo "ERROR: missing log marker: $label" >&2
        missing=1
    fi
}

require_marker "^MATERIAL_APP_ON_CREATE_OK " "MATERIAL_APP_ON_CREATE_OK"
require_marker "^MATERIAL_ACTIVITY_ON_CREATE_OK " "MATERIAL_ACTIVITY_ON_CREATE_OK"
require_marker "^MATERIAL_CLASS_SURFACE_OK .*MaterialCardView" "MATERIAL_CLASS_SURFACE_OK"
require_marker "^MATERIAL_UI_BUILD_OK " "MATERIAL_UI_BUILD_OK"
require_marker "^MATERIAL_LANGUAGE_OK .*locale=zh-Hans" "MATERIAL_LANGUAGE_OK"
require_marker "^MATERIAL_DIRECT_FRAME_OK " "MATERIAL_DIRECT_FRAME_OK"
require_marker "^MATERIAL_IMAGE_BRIDGE_OK .*transport=host_bridge" "MATERIAL_IMAGE_BRIDGE_OK"
require_marker "^MATERIAL_ROW_IMAGE_OK .*index=0 .*transport=host_bridge" "MATERIAL_ROW_IMAGE_OK index=0"
require_log_marker "Surface buffer 1080x1800 for $MATERIAL_PKG" "1K Material Yelp surface buffer"

if [ "$INTERACT" = "1" ]; then
    require_marker "^MATERIAL_TOUCH_POLL_READY " "MATERIAL_TOUCH_POLL_READY"
    require_marker "^MATERIAL_TOUCH_POLL_OK " "MATERIAL_TOUCH_POLL_OK"
    require_marker "^MATERIAL_FILTER_TOGGLE_OK " "MATERIAL_FILTER_TOGGLE_OK"
    require_marker "^MATERIAL_SELECT_PLACE_OK " "MATERIAL_SELECT_PLACE_OK"
    require_marker "^MATERIAL_SAVE_PLACE_OK " "MATERIAL_SAVE_PLACE_OK"
    require_marker "^MATERIAL_NAV_SAVED_OK " "MATERIAL_NAV_SAVED_OK"
    require_marker "^MATERIAL_NAV_SEARCH_OK " "MATERIAL_NAV_SEARCH_OK"
fi

if grep -qE "^MATERIAL_.*_FAIL " "$MARKERS_PATH"; then
    echo "ERROR: Material failure marker present" >&2
    grep -E "^MATERIAL_.*_FAIL " "$MARKERS_PATH" >&2 || true
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

scale = min(w / 480.0, h / 800.0)
ox = (w - 480.0 * scale) / 2.0
oy = (h - 800.0 * scale) / 2.0

def frame_bounds(l, t, r, b):
    return (
        max(0, int(ox + l * scale)),
        max(0, int(oy + t * scale)),
        min(w - 1, int(ox + r * scale)),
        min(h - 1, int(oy + b * scale)),
    )

def in_box(x, y, box):
    l, t, r, b = box
    return l <= x <= r and t <= y <= b

top_box = frame_bounds(0, 0, 480, 104)
bottom_box = frame_bounds(0, 686, 480, 800)
cards_box = frame_bounds(16, 238, 464, 640)
photo_box = frame_bounds(30, 252, 112, 318)

step = max(1, int(min(w, h) // 160))
distinct = set()
colored = 0
top_red = 0
bottom_light = 0
bottom_red = 0
card_light = 0
photo_distinct = set()
photo_colored = 0
for y in range(0, h, step):
    for x in range(0, w, step):
        r, g, b = img.getpixel((x, y))
        distinct.add((r, g, b))
        if max(r, g, b) - min(r, g, b) > 20 and not (r > 245 and g > 245 and b > 245):
            colored += 1
        if in_box(x, y, top_box) and r > 150 and g < 95 and b < 95:
            top_red += 1
        if in_box(x, y, bottom_box):
            if r > 235 and g > 235 and b > 235:
                bottom_light += 1
            if r > 170 and g < 90 and b < 90:
                bottom_red += 1
        if in_box(x, y, cards_box) and r > 240 and g > 235 and b > 235:
            card_light += 1
        if in_box(x, y, photo_box):
            photo_distinct.add((r, g, b))
            if max(r, g, b) - min(r, g, b) > 18:
                photo_colored += 1

with open(visual_path, "w", encoding="utf-8") as out:
    out.write(f"size={w}x{h}\n")
    out.write(f"sample_step={step}\n")
    out.write(f"content_scale={scale:.4f}\n")
    out.write(f"content_offset={ox:.1f},{oy:.1f}\n")
    out.write(f"distinct_colors={len(distinct)}\n")
    out.write(f"colored_samples={colored}\n")
    out.write(f"top_red_samples={top_red}\n")
    out.write(f"bottom_nav_light_samples={bottom_light}\n")
    out.write(f"bottom_nav_red_samples={bottom_red}\n")
    out.write(f"card_light_samples={card_light}\n")
    out.write(f"photo_distinct_colors={len(photo_distinct)}\n")
    out.write(f"photo_colored_samples={photo_colored}\n")

if len(distinct) < 32:
    raise SystemExit("too few screenshot colors")
if colored < 800:
    raise SystemExit("screenshot appears blank")
if top_red < 20:
    raise SystemExit("red Material header not visible")
if bottom_light < 80 or bottom_red < 8:
    raise SystemExit("bottom navigation not visible")
if card_light < 300:
    raise SystemExit("Material cards not visible")
if len(photo_distinct) < 24 or photo_colored < 80:
    raise SystemExit("network images not visible in result cards")
PY
then
    echo "ERROR: screenshot visual gate failed" >&2
    missing=1
fi

if [ "$missing" -ne 0 ]; then
    echo ""
    echo "Material Yelp acceptance: FAILED"
    echo "Artifacts:"
    echo "  log:     $LOG_PATH"
    echo "  markers: $MARKERS_PATH"
    echo "  trace:   $TRACE_PATH"
    echo "  screen:  $PNG_PATH"
    echo "  visual:  $VISUAL_PATH"
    exit 4
fi

echo ""
echo "Material Yelp acceptance: PASSED"
echo "Hashes:"
echo "  dalvikvm=$local_dvm_hash"
echo "  aosp-shim.dex=$local_shim_hash"
echo "  westlake-material-yelp-debug.apk=$local_material_hash"
echo "Artifacts:"
echo "  log:     $LOG_PATH"
echo "  markers: $MARKERS_PATH"
echo "  trace:   $TRACE_PATH"
echo "  screen:  $PNG_PATH"
echo "  visual:  $VISUAL_PATH"

ACCEPT_DIR="$ARTIFACT_DIR/accepted/material_yelp/${local_shim_hash}_${local_material_hash}"
mkdir -p "$ACCEPT_DIR"
cp "$LOG_PATH" "$MARKERS_PATH" "$TRACE_PATH" "$PNG_PATH" "$VISUAL_PATH" \
    "$LOG_MARKERS_PATH" "$PS_PATH" "$TOP_PATH" "$ACCEPT_DIR"/
echo "  accepted copy: $ACCEPT_DIR"
