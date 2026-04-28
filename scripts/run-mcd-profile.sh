#!/bin/bash
# Runs the controlled McD-profile APK through Westlake's generic APK path.
# This proof is intentionally McD-shaped: compiled XML, Material shim tags,
# ListView adapter rows, live menu/image network, REST POST/HEAD/status,
# prefs-backed cart state, full-phone DLST rendering, and touch navigation.

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
HOST_APK_SRC="${HOST_APK_SRC:-$REPO_ROOT/westlake-host-gradle/app/build/outputs/apk/debug/app-debug.apk}"
MCD_APK_SRC="${MCD_APK_SRC:-$REPO_ROOT/test-apps/10-mcd-profile/build/dist/westlake-mcd-profile-debug.apk}"
MCD_AUTO_INSTALL_HOST="${MCD_AUTO_INSTALL_HOST:-1}"
MCD_AUTO_INSTALL_APP="${MCD_AUTO_INSTALL_APP:-1}"
MCD_AUTO_SYNC_RUNTIME="${MCD_AUTO_SYNC_RUNTIME:-1}"
WAIT_SECS="${WAIT_SECS:-7}"
INTERACT="${INTERACT:-1}"
SUPERVISOR_HTTP_PROXY_PORT="${SUPERVISOR_HTTP_PROXY_PORT:-8767}"

HOST_PKG="com.westlake.host"
HOST_ACTIVITY="com.westlake.host/.WestlakeActivity"
MCD_PKG="com.westlake.mcdprofile"
MCD_ACTIVITY="com.westlake.mcdprofile.McdProfileActivity"
LABEL="mcd_profile_target"

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
import time
import urllib.parse
import urllib.request

port = int(sys.argv[1])

class Handler(http.server.BaseHTTPRequestHandler):
    def do_HEAD(self):
        self.handle_request(send_body=False)

    def do_GET(self):
        self.handle_request(send_body=True)

    def do_POST(self):
        self.handle_request(send_body=True)

    def do_PUT(self):
        self.handle_request(send_body=True)

    def do_PATCH(self):
        self.handle_request(send_body=True)

    def do_DELETE(self):
        self.handle_request(send_body=True)

    def handle_request(self, send_body):
        parsed = urllib.parse.urlparse(self.path)
        if parsed.path == "/proxy":
            self.handle_proxy(parsed, send_body)
            return
        if parsed.path.startswith("/rest/"):
            self.handle_rest(parsed, send_body)
            return
        self.send_error(404)

    def read_body(self):
        length = int(self.headers.get("Content-Length", "0") or "0")
        if length <= 0:
            return b""
        return self.rfile.read(length)

    def send_bytes(self, status, body, content_type="application/octet-stream", headers=None, send_body=True):
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(body)))
        self.send_header("X-Westlake-Mcd-Profile", "true")
        if headers:
            for key, value in headers.items():
                self.send_header(key, value)
        self.end_headers()
        if send_body and body:
            self.wfile.write(body)

    def handle_proxy(self, parsed, send_body):
        if self.command != "GET":
            self.send_error(405)
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
            req = urllib.request.Request(url, headers={"User-Agent": "Westlake-mcd-profile/1.0"})
            with urllib.request.urlopen(req, timeout=20) as resp:
                body = resp.read(512 * 1024)
                self.send_bytes(resp.status, body, resp.headers.get("Content-Type", "application/octet-stream"), send_body=send_body)
        except Exception as exc:
            body = str(exc).encode("utf-8", "replace")
            self.send_bytes(599, body, "text/plain", send_body=send_body)

    def handle_rest(self, parsed, send_body):
        body = self.read_body()
        path = parsed.path
        if path == "/rest/ping":
            self.send_bytes(200, b'{"ok":true}', "application/json", send_body=send_body)
            return
        if path == "/rest/status/418":
            self.send_bytes(418, b'{"error":"teapot","status":418}', "application/json", send_body=send_body)
            return
        if path == "/rest/echo":
            probe = self.headers.get("X-Westlake-Probe", "")
            escaped = body.decode("utf-8", "replace").replace("\\", "\\\\").replace('"', '\\"')
            payload = (
                '{"method":"' + self.command + '",'
                '"xWestlakeProbe":"' + probe + '",'
                '"body":"' + escaped + '",'
                '"length":' + str(len(body)) + '}'
            ).encode("utf-8")
            self.send_bytes(200, payload, "application/json", send_body=send_body)
            return
        if path == "/rest/slow":
            time.sleep(2.0)
            self.send_bytes(200, b'{"slow":true}', "application/json", send_body=send_body)
            return
        self.send_error(404)

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

installed_hash() {
    local pkg="$1"
    local path
    path="$("${ADB[@]}" shell pm path "$pkg" 2>/dev/null \
        | tr -d '\r' \
        | sed -n 's/^package://p' \
        | head -n 1 || true)"
    if [ -z "$path" ]; then
        echo ""
        return 0
    fi
    "${ADB[@]}" shell sha256sum "$path" 2>/dev/null | awk '{print $1}' || true
}

echo "=== Westlake McD Profile Run ==="
echo "ADB binary:  $ADB_BIN"
echo "ADB server:  ${ADB_HOST:-default}:$ADB_PORT"
echo "Device:      $ADB_SERIAL"
echo "Artifacts:   $ARTIFACT_DIR"

timeout "$ADB_TIMEOUT" "${ADB[@]}" get-state >/dev/null

for f in "$DALVIKVM_SRC" "$AOSP_SHIM_SRC" "$HOST_APK_SRC" "$MCD_APK_SRC"; do
    if [ ! -f "$f" ]; then
        echo "ERROR: missing required file: $f" >&2
        exit 2
    fi
done

local_dvm_hash="$(sha256sum "$DALVIKVM_SRC" | awk '{print $1}')"
local_shim_hash="$(sha256sum "$AOSP_SHIM_SRC" | awk '{print $1}')"
local_host_hash="$(sha256sum "$HOST_APK_SRC" | awk '{print $1}')"
local_mcd_hash="$(sha256sum "$MCD_APK_SRC" | awk '{print $1}')"
remote_dvm_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/dalvikvm" 2>/dev/null | awk '{print $1}' || true)"
remote_shim_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/aosp-shim.dex" 2>/dev/null | awk '{print $1}' || true)"

echo "[0/6] Runtime provenance preflight..."
echo "  dalvikvm local=$local_dvm_hash phone=${remote_dvm_hash:-missing}"
echo "  aosp-shim local=$local_shim_hash phone=${remote_shim_hash:-missing}"
echo "  host.apk local=$local_host_hash phone=$(installed_hash "$HOST_PKG")"
echo "  mcd-profile.apk local=$local_mcd_hash phone=$(installed_hash "$MCD_PKG")"

if [ "$remote_dvm_hash" != "$local_dvm_hash" ] || [ "$remote_shim_hash" != "$local_shim_hash" ]; then
    if [ "$MCD_AUTO_SYNC_RUNTIME" = "1" ]; then
        echo "  syncing phone runtime to local dalvikvm/aosp-shim..."
        ADB_BIN="$ADB_BIN" ADB_HOST="$ADB_HOST" ADB_PORT="$ADB_PORT" ADB_SERIAL="$ADB_SERIAL" \
            "$REPO_ROOT/scripts/sync-westlake-phone-runtime.sh" >/dev/null
        remote_dvm_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/dalvikvm" 2>/dev/null | awk '{print $1}' || true)"
        remote_shim_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/aosp-shim.dex" 2>/dev/null | awk '{print $1}' || true)"
    else
        echo "ERROR: phone runtime hash mismatch" >&2
        exit 3
    fi
fi

if [ "$remote_dvm_hash" != "$local_dvm_hash" ] || [ "$remote_shim_hash" != "$local_shim_hash" ]; then
    echo "ERROR: phone runtime hash mismatch after sync" >&2
    exit 3
fi

if [ "$(installed_hash "$HOST_PKG")" != "$local_host_hash" ] && [ "$MCD_AUTO_INSTALL_HOST" = "1" ]; then
    echo "  installing host APK..."
    "${ADB[@]}" install -r "$HOST_APK_SRC" >/dev/null
fi

if [ "$(installed_hash "$MCD_PKG")" != "$local_mcd_hash" ] && [ "$MCD_AUTO_INSTALL_APP" = "1" ]; then
    echo "  installing McD profile APK..."
    "${ADB[@]}" install -r "$MCD_APK_SRC" >/dev/null
fi

if [ "$(installed_hash "$HOST_PKG")" != "$local_host_hash" ]; then
    echo "ERROR: host APK hash mismatch after install" >&2
    exit 3
fi
if [ "$(installed_hash "$MCD_PKG")" != "$local_mcd_hash" ]; then
    echo "ERROR: McD profile APK hash mismatch after install" >&2
    exit 3
fi

echo "[0b/6] Starting supervisor HTTP proxy..."
start_supervisor_http_proxy

echo "[1/6] Force-stopping old app state..."
"${ADB[@]}" shell am force-stop "$HOST_PKG" >/dev/null || true
"${ADB[@]}" shell am force-stop "$MCD_PKG" >/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" mkdir -p "/data/user/0/$HOST_PKG/files/vm" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_MARKER_PATH" "$RUNAS_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$PUBLIC_MARKER_PATH" "$PUBLIC_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$HOST_TOUCH_PATH_A" "$HOST_TOUCH_PATH_B" >/dev/null 2>&1 || true

echo "[2/6] Clearing logcat..."
"${ADB[@]}" logcat -c

echo "[3/6] Launching Westlake host generic McD-profile APK path..."
"${ADB[@]}" shell am start -S -W -n "$HOST_ACTIVITY" \
    --es launch "VM_APK:${MCD_PKG}:${MCD_ACTIVITY}:Westlake McD Profile"

echo "[4/6] Waiting ${WAIT_SECS}s for settle..."
sleep "$WAIT_SECS"

if [ "$INTERACT" = "1" ]; then
    echo "[4b/6] Sending McD-profile interactions..."
    wm_size="$("${ADB[@]}" shell wm size 2>/dev/null | tr -d '\r' \
        | sed -n 's/^Physical size: //p' | tail -n 1)"
    device_w="${wm_size%x*}"
    device_h="${wm_size#*x}"
    if ! echo "$device_w" | grep -qE '^[0-9]+$' || ! echo "$device_h" | grep -qE '^[0-9]+$'; then
        device_w=1080
        device_h=2280
    fi
    frame_tap() {
        local fx="$1"
        local fy="$2"
        local coords
        coords="$(python3 - "$device_w" "$device_h" "$fx" "$fy" <<'PY'
import sys
w, h, fx, fy = map(float, sys.argv[1:])
scale = min(w / 480.0, h / 1013.3333)
ox = (w - 480.0 * scale) / 2.0
oy = (h - 1013.3333 * scale) / 2.0
print(f"{int(round(ox + fx * scale))} {int(round(oy + fy * scale))}")
PY
)"
        "${ADB[@]}" shell input tap $coords >/dev/null 2>&1 || true
    }
    frame_tap 286 322
    sleep 1
    frame_tap 420 482
    sleep 1
    frame_tap 120 404
    sleep 1
    frame_tap 120 846
    sleep 1
    frame_tap 374 846
    sleep 1
    frame_tap 420 960
    sleep 1
    frame_tap 180 960
    sleep 1
fi

echo "[5/6] Capturing artifacts..."
"${ADB[@]}" logcat -d > "$LOG_PATH"
"${ADB[@]}" shell ps -A | grep -E 'westlake|dalvikvm|mcdprofile' > "$PS_PATH" || true
"${ADB[@]}" shell dumpsys activity top > "$TOP_PATH"
"${ADB[@]}" exec-out screencap -p > "$PNG_PATH"
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_MARKER_PATH" > "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_MARKER_PATH" >> "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_TRACE_PATH" > "$TRACE_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_TRACE_PATH" >> "$TRACE_PATH" 2>/dev/null || true
grep -nE "MCD_PROFILE_|WestlakeLauncher|Surface buffer|Launching VM APK|APK load error|main fatal|FATAL|SIGBUS|SIGILL" \
    "$LOG_PATH" > "$LOG_MARKERS_PATH" || true

echo ""
echo "Key log markers:"
cat "$LOG_MARKERS_PATH" || true

echo ""
echo "App-owned McD-profile markers:"
if [ -s "$MARKERS_PATH" ]; then
    cat "$MARKERS_PATH"
else
    echo "(none)"
fi

missing=0
require_marker() {
    local pattern="$1"
    local label="$2"
    if ! grep -qE "$pattern" "$MARKERS_PATH"; then
        echo "ERROR: missing McD-profile marker: $label" >&2
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

reject_log_marker() {
    local pattern="$1"
    local label="$2"
    if grep -qE "$pattern" "$LOG_PATH"; then
        echo "ERROR: forbidden log marker present: $label" >&2
        missing=1
    fi
}

require_marker "^MCD_PROFILE_APP_ON_CREATE_OK " "APP_ON_CREATE"
require_marker "^MCD_PROFILE_ACTIVITY_ON_CREATE_OK " "ACTIVITY_ON_CREATE"
require_marker "^MCD_PROFILE_GENERIC_ACTIVITY_FACTORY_OK " "GENERIC_ACTIVITY_FACTORY_OK"
require_marker "^MCD_PROFILE_WAT_ACTIVITY_LAUNCH_OK " "WAT_ACTIVITY_LAUNCH_OK"
require_marker "^MCD_PROFILE_WAT_ACTIVITY_ONCREATE_OK " "WAT_ACTIVITY_ONCREATE_OK"
require_marker "^MCD_PROFILE_WAT_ACTIVITY_RESUME_OK " "WAT_ACTIVITY_RESUME_OK"
require_marker "^MCD_PROFILE_XML_RESOURCE_WIRE_OK .*table=true .*layoutBytes=[1-9][0-9]*" "XML_RESOURCE_WIRE_OK table"
require_marker "^MCD_PROFILE_XML_TAG_OK tag=TextInputLayout " "XML TextInputLayout"
require_marker "^MCD_PROFILE_XML_TAG_OK tag=MaterialCardView " "XML MaterialCardView"
require_marker "^MCD_PROFILE_XML_TAG_OK tag=ListView " "XML ListView"
require_marker "^MCD_PROFILE_XML_INFLATE_OK .*views=[1-9][0-9]+ .*materialViews=[1-9][0-9]* .*source=compiled_apk_xml" "XML_INFLATE_OK"
require_marker "^MCD_PROFILE_XML_BIND_OK .*list=true .*materialViews=[1-9][0-9]*" "XML_BIND_OK"
require_marker "^MCD_PROFILE_XML_LAYOUT_PROBE_OK " "XML_LAYOUT_PROBE_OK"
require_marker "^MCD_PROFILE_ADAPTER_GET_VIEW_OK .*position=4" "ADAPTER_GET_VIEW position=4"
require_marker "^MCD_PROFILE_STORAGE_PREFS_OK " "STORAGE_PREFS_OK"
require_marker "^MCD_PROFILE_LIVE_JSON_OK .*transport=host_bridge" "LIVE_JSON_OK"
require_marker "^MCD_PROFILE_ROW_IMAGE_OK .*index=0 .*transport=host_bridge" "ROW_IMAGE_OK index=0"
require_marker "^MCD_PROFILE_IMAGE_BRIDGE_OK .*transport=host_bridge" "IMAGE_BRIDGE_OK"
require_marker "^MCD_PROFILE_REST_POST_OK .*protocol=2" "REST_POST_OK"
require_marker "^MCD_PROFILE_REST_HEAD_OK " "REST_HEAD_OK"
require_marker "^MCD_PROFILE_REST_MATRIX_OK " "REST_MATRIX_OK"
require_marker "^MCD_PROFILE_DIRECT_FRAME_OK .*xml=true .*materialViews=[1-9][0-9]* .*rows=5" "DIRECT_FRAME_OK"
require_marker "^MCD_PROFILE_FULL_RES_FRAME_OK .*target=1080x2280" "FULL_RES_FRAME_OK"
require_marker "^MCD_PROFILE_READY_FOR_OHOS_PORT_OK " "READY_FOR_OHOS_PORT_OK"
require_log_marker "Surface buffer 1080x2280 for $MCD_PKG" "1K/full-phone McD-profile surface buffer"

if [ "$INTERACT" = "1" ]; then
    require_marker "^MCD_PROFILE_TOUCH_POLL_READY " "TOUCH_POLL_READY"
    require_marker "^MCD_PROFILE_TOUCH_POLL_OK " "TOUCH_POLL_OK"
    require_marker "^MCD_PROFILE_CATEGORY_OK " "CATEGORY_OK"
    require_marker "^MCD_PROFILE_SELECT_ITEM_OK " "SELECT_ITEM_OK"
    require_marker "^MCD_PROFILE_CART_ADD_OK " "CART_ADD_OK"
    require_marker "^MCD_PROFILE_CHECKOUT_OK " "CHECKOUT_OK"
    require_marker "^MCD_PROFILE_NAV_DEALS_OK " "NAV_DEALS_OK"
    require_marker "^MCD_PROFILE_NAV_MENU_OK " "NAV_MENU_OK"
fi

if grep -qE "^MCD_PROFILE_.*_FAIL " "$MARKERS_PATH"; then
    echo "ERROR: McD-profile failure marker present" >&2
    grep -E "^MCD_PROFILE_.*_FAIL " "$MARKERS_PATH" >&2 || true
    missing=1
fi
if grep -qE "^MCD_PROFILE_XML_TAG_WARN " "$MARKERS_PATH"; then
    echo "ERROR: McD-profile XML warning marker present" >&2
    grep -E "^MCD_PROFILE_XML_TAG_WARN " "$MARKERS_PATH" >&2 || true
    missing=1
fi
if grep -qE "^MCD_PROFILE_CONTROLLED_" "$MARKERS_PATH"; then
    echo "ERROR: McD-profile controlled-launch marker present; expected generic WAT launch" >&2
    grep -E "^MCD_PROFILE_CONTROLLED_" "$MARKERS_PATH" >&2 || true
    missing=1
fi
reject_log_marker "APK load error|FATAL EXCEPTION|SIGBUS|SIGILL" "fatal runtime/log error"
reject_log_marker "standalone ResourceTable parse failed" "standalone ResourceTable parse failure"
reject_log_marker "NPE-SYNC" "standalone synchronized null/NPE"

if ! python3 - "$PNG_PATH" "$VISUAL_PATH" <<'PY'
import sys
from PIL import Image

png_path, visual_path = sys.argv[1], sys.argv[2]
img = Image.open(png_path).convert("RGB")
w, h = img.size
scale = min(w / 480.0, h / 1013.3333)
ox = (w - 480.0 * scale) / 2.0
oy = (h - 1013.3333 * scale) / 2.0

def box(l, t, r, b):
    return (
        max(0, int(ox + l * scale)),
        max(0, int(oy + t * scale)),
        min(w - 1, int(ox + r * scale)),
        min(h - 1, int(oy + b * scale)),
    )

def inside(x, y, b):
    l, t, r, bot = b
    return l <= x <= r and t <= y <= bot

header = box(0, 0, 480, 118)
hero = box(16, 132, 464, 274)
rows = box(16, 378, 464, 786)
cart = box(16, 810, 464, 898)
nav = box(0, 920, 480, 1013)

step = max(1, int(min(w, h) // 180))
distinct = set()
red = yellow = row_light = cart_dark = nav_light = colored = 0
for y in range(0, h, step):
    for x in range(0, w, step):
        r, g, b = img.getpixel((x, y))
        distinct.add((r, g, b))
        if max(r, g, b) - min(r, g, b) > 22 and not (r > 245 and g > 245 and b > 245):
            colored += 1
        if inside(x, y, header) and r > 170 and g < 80 and b < 70:
            red += 1
        if (inside(x, y, header) or inside(x, y, hero) or inside(x, y, rows)) and r > 215 and g > 145 and b < 45:
            yellow += 1
        if inside(x, y, rows) and r > 238 and g > 230 and b > 210:
            row_light += 1
        if inside(x, y, cart) and r < 80 and g < 80 and b < 80:
            cart_dark += 1
        if inside(x, y, nav) and r > 238 and g > 238 and b > 238:
            nav_light += 1

with open(visual_path, "w", encoding="utf-8") as out:
    out.write(f"size={w}x{h}\n")
    out.write(f"scale={scale:.4f}\n")
    out.write(f"distinct_colors={len(distinct)}\n")
    out.write(f"colored_samples={colored}\n")
    out.write(f"red_header_samples={red}\n")
    out.write(f"yellow_samples={yellow}\n")
    out.write(f"row_light_samples={row_light}\n")
    out.write(f"cart_dark_samples={cart_dark}\n")
    out.write(f"nav_light_samples={nav_light}\n")

if len(distinct) < 28:
    raise SystemExit("too few colors")
if colored < 800:
    raise SystemExit("screenshot appears blank")
if red < 40:
    raise SystemExit("McD red header missing")
if yellow < 30:
    raise SystemExit("McD yellow accents/images missing")
if row_light < 250:
    raise SystemExit("menu rows missing")
if cart_dark < 60:
    raise SystemExit("cart bar missing")
if nav_light < 80:
    raise SystemExit("bottom nav missing")
PY
then
    echo "ERROR: screenshot visual gate failed" >&2
    missing=1
fi

if [ "$missing" -ne 0 ]; then
    echo ""
    echo "McD-profile acceptance: FAILED"
    echo "Artifacts:"
    echo "  log:     $LOG_PATH"
    echo "  markers: $MARKERS_PATH"
    echo "  trace:   $TRACE_PATH"
    echo "  screen:  $PNG_PATH"
    echo "  visual:  $VISUAL_PATH"
    exit 4
fi

echo ""
echo "McD-profile acceptance: PASSED"
echo "Hashes:"
echo "  dalvikvm=$local_dvm_hash"
echo "  aosp-shim.dex=$local_shim_hash"
echo "  westlake-host.apk=$local_host_hash"
echo "  westlake-mcd-profile-debug.apk=$local_mcd_hash"
echo "Artifacts:"
echo "  log:     $LOG_PATH"
echo "  markers: $MARKERS_PATH"
echo "  trace:   $TRACE_PATH"
echo "  screen:  $PNG_PATH"
echo "  visual:  $VISUAL_PATH"

ACCEPT_DIR="$ARTIFACT_DIR/accepted/mcd_profile/${local_shim_hash}_${local_mcd_hash}"
mkdir -p "$ACCEPT_DIR"
cp "$LOG_PATH" "$MARKERS_PATH" "$TRACE_PATH" "$PNG_PATH" "$VISUAL_PATH" \
    "$LOG_MARKERS_PATH" "$PS_PATH" "$TOP_PATH" "$ACCEPT_DIR"/
echo "  accepted copy: $ACCEPT_DIR"
