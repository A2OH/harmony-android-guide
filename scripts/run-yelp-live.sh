#!/bin/bash
# Runs the separate Yelp-like live data APK through Westlake's generic APK
# runtime path and checks network, touch, navigation, and direct DLST rendering.

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
YELP_APK_SRC="${YELP_APK_SRC:-$REPO_ROOT/test-apps/06-yelp-live/build/dist/westlake-yelp-live-debug.apk}"
YELP_AUTO_INSTALL="${YELP_AUTO_INSTALL:-1}"
WAIT_SECS="${WAIT_SECS:-4}"
INTERACT="${INTERACT:-1}"
NETWORK_PREFLIGHT="${NETWORK_PREFLIGHT:-1}"
PHONE_NETWORK_PREFLIGHT="${PHONE_NETWORK_PREFLIGHT:-1}"
SUPERVISOR_HTTP_PROXY="${SUPERVISOR_HTTP_PROXY:-0}"
SUPERVISOR_HTTP_PROXY_PORT="${SUPERVISOR_HTTP_PROXY_PORT:-8765}"
POST_SCROLL_WAIT_SECS="${POST_SCROLL_WAIT_SECS:-3}"
POST_ROW_WAIT_SECS="${POST_ROW_WAIT_SECS:-3}"
POST_SAVE_WAIT_SECS="${POST_SAVE_WAIT_SECS:-3}"
YELP_FEED_URL="${YELP_FEED_URL:-https://dummyjson.com/recipes?limit=6&select=name,image,rating,reviewCount,mealType,cuisine,difficulty}"

HOST_PKG="com.westlake.host"
HOST_ACTIVITY="com.westlake.host/.WestlakeActivity"
YELP_PKG="com.westlake.yelplive"
YELP_ACTIVITY="com.westlake.yelplive.YelpLiveActivity"
LABEL="yelp_live_target"

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
PREFLIGHT_JSON_PATH="$ARTIFACT_DIR/${LABEL}.preflight.json"
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

    def do_OPTIONS(self):
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
        self.send_header("X-Westlake-Reply", "pf458")
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
        if not (url.startswith("https://dummyjson.com/") or url.startswith("https://picsum.photos/")):
            self.send_error(404)
            return
        try:
            req = urllib.request.Request(url, headers={"User-Agent": "Westlake-supervisor-proxy/1.0"})
            with urllib.request.urlopen(req, timeout=20) as resp:
                body = resp.read(512 * 1024)
                self.send_bytes(resp.status, body, resp.headers.get("Content-Type", "application/octet-stream"), send_body=send_body)
        except Exception as exc:
            body = str(exc).encode("utf-8", "replace")
            self.send_bytes(599, body, "text/plain", send_body=send_body)

    def handle_rest(self, parsed, send_body):
        body = self.read_body()
        path = parsed.path
        if path == "/rest/slow":
            time.sleep(2.0)
            payload = b'{"slow":true}'
            self.send_bytes(200, payload, "application/json", send_body=send_body)
            return
        if path == "/rest/large":
            payload = (b"westlake-rest-large-" * 256)[:4096]
            self.send_bytes(200, payload, "application/octet-stream", send_body=send_body)
            return
        if path == "/rest/redirect":
            self.send_response(302)
            self.send_header("Location", "/rest/final")
            self.send_header("Content-Length", "0")
            self.end_headers()
            return
        if path == "/rest/status/418":
            self.send_bytes(418, b'{"error":"teapot","status":418}', "application/json", send_body=send_body)
            return
        if path == "/rest/final":
            self.send_bytes(200, b'{"redirected":true,"method":"GET"}', "application/json", send_body=send_body)
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
        if path == "/rest/ping":
            self.send_bytes(200, b'{"ok":true}', "application/json", send_body=send_body)
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

echo "=== Westlake Yelp Live Run ==="
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
if [ ! -f "$YELP_APK_SRC" ]; then
    echo "ERROR: Yelp live APK not found: $YELP_APK_SRC" >&2
    echo "       run test-apps/06-yelp-live/build-apk.sh" >&2
    exit 2
fi

read_installed_yelp_apk_hash() {
    remote_yelp_path="$("${ADB[@]}" shell pm path "$YELP_PKG" 2>/dev/null \
        | tr -d '\r' \
        | sed -n 's/^package://p' \
        | head -n 1 || true)"
    if [ -z "$remote_yelp_path" ]; then
        remote_yelp_hash=""
        return 0
    fi
    remote_yelp_hash="$("${ADB[@]}" shell sha256sum "$remote_yelp_path" 2>/dev/null \
        | awk '{print $1}' || true)"
}

echo "[0/6] Runtime provenance preflight..."
local_dvm_hash="$(sha256sum "$DALVIKVM_SRC" | awk '{print $1}')"
local_shim_hash="$(sha256sum "$AOSP_SHIM_SRC" | awk '{print $1}')"
local_yelp_hash="$(sha256sum "$YELP_APK_SRC" | awk '{print $1}')"
remote_dvm_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/dalvikvm" 2>/dev/null | awk '{print $1}' || true)"
remote_shim_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/aosp-shim.dex" 2>/dev/null | awk '{print $1}' || true)"
read_installed_yelp_apk_hash
echo "  dalvikvm local=$local_dvm_hash phone=${remote_dvm_hash:-missing}"
echo "  aosp-shim local=$local_shim_hash phone=${remote_shim_hash:-missing}"
echo "  yelp.apk local=$local_yelp_hash phone=${remote_yelp_hash:-missing}"

if [ "$remote_dvm_hash" != "$local_dvm_hash" ]; then
    echo "ERROR: phone dalvikvm hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_shim_hash" != "$local_shim_hash" ]; then
    echo "ERROR: phone aosp-shim.dex hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_yelp_hash" != "$local_yelp_hash" ]; then
    if [ "$YELP_AUTO_INSTALL" = "1" ]; then
        echo "  installing Yelp live APK to match local hash..."
        "${ADB[@]}" install -r "$YELP_APK_SRC" >/dev/null
        read_installed_yelp_apk_hash
        echo "  yelp.apk phone=${remote_yelp_hash:-missing}"
    else
        echo "ERROR: phone Yelp APK hash mismatch; install $YELP_APK_SRC" >&2
        exit 3
    fi
fi
if [ "$remote_yelp_hash" != "$local_yelp_hash" ]; then
    echo "ERROR: phone Yelp APK hash mismatch after install" >&2
    exit 3
fi

if [ "$NETWORK_PREFLIGHT" = "1" ]; then
    echo "[0b/6] Verifying live endpoint shape from supervisor host..."
    if curl -fsSL --max-time 15 "$YELP_FEED_URL" -o "$PREFLIGHT_JSON_PATH" \
            && grep -q '"recipes"' "$PREFLIGHT_JSON_PATH" \
            && grep -q '"image"' "$PREFLIGHT_JSON_PATH"; then
        echo "  live feed bytes: $(wc -c < "$PREFLIGHT_JSON_PATH")"
        echo "  endpoint shape verified; app fetch still must go through host bridge"
    else
        echo "ERROR: live feed preflight failed: $YELP_FEED_URL" >&2
        exit 3
    fi
fi

if [ "$PHONE_NETWORK_PREFLIGHT" = "1" ]; then
    echo "[0c/6] Verifying phone has an active default network..."
    active_network="$("${ADB[@]}" shell dumpsys connectivity 2>/dev/null \
        | tr -d '\r' \
        | sed -n 's/^Active default network: //p' \
        | head -n 1 || true)"
    if [ -z "$active_network" ] || [ "$active_network" = "none" ]; then
        if [ "$SUPERVISOR_HTTP_PROXY" = "1" ]; then
            echo "  phone default network: none; using supervisor HTTP proxy for this run"
            start_supervisor_http_proxy
        else
        echo "ERROR: phone has no active default network; live Yelp proof cannot fetch internet data" >&2
        echo "       reconnect Wi-Fi/cellular on device $ADB_SERIAL, then rerun this script" >&2
        exit 3
        fi
    else
        echo "  phone default network: $active_network"
        if [ "$SUPERVISOR_HTTP_PROXY" = "1" ]; then
            start_supervisor_http_proxy
        fi
    fi
elif [ "$SUPERVISOR_HTTP_PROXY" = "1" ]; then
    echo "[0c/6] Starting supervisor HTTP proxy without phone-network preflight..."
    start_supervisor_http_proxy
fi

echo "[1/6] Force-stopping old app state..."
"${ADB[@]}" shell am force-stop "$HOST_PKG" >/dev/null || true
"${ADB[@]}" shell am force-stop "$YELP_PKG" >/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" mkdir -p "/data/user/0/$HOST_PKG/files/vm" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_MARKER_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$PUBLIC_MARKER_PATH" "$PUBLIC_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$HOST_TOUCH_PATH_A" "$HOST_TOUCH_PATH_B" >/dev/null 2>&1 || true
if [ "$SUPERVISOR_HTTP_PROXY" != "1" ]; then
    "${ADB[@]}" shell rm -f "$HOST_PROXY_PATH_A" "$HOST_PROXY_PATH_B" >/dev/null 2>&1 || true
fi

echo "[2/6] Clearing logcat..."
"${ADB[@]}" logcat -c

echo "[3/6] Launching Westlake host generic Yelp APK path..."
"${ADB[@]}" shell am start -S -W -n "$HOST_ACTIVITY" \
    --es launch "VM_APK:${YELP_PKG}:${YELP_ACTIVITY}:WestlakeYelpLive"

echo "[4/6] Waiting ${WAIT_SECS}s for settle..."
sleep "$WAIT_SECS"

if [ "$INTERACT" = "1" ]; then
    echo "[4b/6] Sending live-feed/navigation interactions..."
    wm_size="$("${ADB[@]}" shell wm size 2>/dev/null | tr -d '\r' \
        | sed -n 's/^Physical size: //p' | tail -n 1)"
    device_w="${wm_size%x*}"
    device_h="${wm_size#*x}"
    if ! echo "$device_w" | grep -qE '^[0-9]+$' || ! echo "$device_h" | grep -qE '^[0-9]+$'; then
        device_w=1080
        device_h=2400
    fi
    frame_coords() {
        local fx="$1"
        local fy="$2"
        python3 - "$device_w" "$device_h" "$fx" "$fy" <<'PY'
import sys
w, h, fx, fy = map(float, sys.argv[1:])
guest_h = 1013.3333333333
scale = min(w / 480.0, h / guest_h)
ox = (w - 480.0 * scale) / 2.0
oy = (h - guest_h * scale) / 2.0
print(f"{int(round(ox + fx * scale))} {int(round(oy + fy * scale))}")
PY
    }
    frame_tap() {
        local coords
        coords="$(frame_coords "$1" "$2")"
        "${ADB[@]}" shell input tap $coords >/dev/null 2>&1 || true
    }
    frame_swipe() {
        local start
        local end
        start="$(frame_coords "$1" "$2")"
        end="$(frame_coords "$3" "$4")"
        "${ADB[@]}" shell input swipe $start $end "${5:-500}" >/dev/null 2>&1 || true
    }
    sleep 8
    frame_tap 180 152
    sleep 1
    frame_tap 180 205
    sleep 8
    frame_tap 260 152
    sleep 1
    frame_tap 180 945
    sleep 1
    frame_swipe 240 770 240 330 500
    sleep "$POST_SCROLL_WAIT_SECS"
    frame_tap 111 406
    sleep 1
    frame_tap 300 592
    sleep "$POST_ROW_WAIT_SECS"
    frame_tap 180 945
    sleep "$POST_SAVE_WAIT_SECS"
    sleep 2
fi

echo "[5/6] Capturing artifacts..."
"${ADB[@]}" logcat -d > "$LOG_PATH"
"${ADB[@]}" shell ps -A | grep -E 'westlake|dalvikvm|yelplive' > "$PS_PATH" || true
"${ADB[@]}" shell dumpsys activity top > "$TOP_PATH"
"${ADB[@]}" exec-out screencap -p > "$PNG_PATH"
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_MARKER_PATH" > "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_MARKER_PATH" >> "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_TRACE_PATH" > "$TRACE_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_TRACE_PATH" >> "$TRACE_PATH" 2>/dev/null || true
grep -nE "YELP_|WestlakeLauncher|Surface buffer|Launching VM APK|APK load error|main fatal|FATAL|SIGBUS|SIGILL" \
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
    echo "App-owned Yelp markers:"
    cat "$MARKERS_PATH"
else
    echo ""
    echo "App-owned Yelp markers: (none)"
fi

missing=0
require_marker() {
    local pattern="$1"
    local label="$2"
    if ! grep -qE "$pattern" "$MARKERS_PATH"; then
        echo "ERROR: missing Yelp marker: $label" >&2
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

require_marker "^YELP_APP_ON_CREATE_OK " "YELP_APP_ON_CREATE_OK"
require_marker "^YELP_ACTIVITY_ON_CREATE_OK " "YELP_ACTIVITY_ON_CREATE_OK"
require_marker "^YELP_XML_RESOURCE_WIRE_OK .* layouts=[1-9][0-9]* " "YELP_XML_RESOURCE_WIRE_OK layouts>0"
require_marker "^YELP_XML_INFLATE_OK .* views=([2-9][0-9]|[1-9][0-9]{2,}) " "YELP_XML_INFLATE_OK views>=20"
require_marker "^YELP_XML_BIND_OK .*title=true .*status=true .*card=true .*list=true .*buttons=5" "YELP_XML_BIND_OK all primary bindings"
require_marker "^YELP_XML_LAYOUT_PROBE_OK " "YELP_XML_LAYOUT_PROBE_OK"
require_marker "^YELP_UI_BUILD_OK " "YELP_UI_BUILD_OK"
require_marker "^YELP_DIRECT_FRAME_OK " "YELP_DIRECT_FRAME_OK"
require_marker "^YELP_FULL_RES_FRAME_OK .* target=1080x2280" "YELP_FULL_RES_FRAME_OK target=1080x2280"
require_marker "^YELP_GENERIC_VIEW_DRAW_OK .*height=1013 .*source=inflated_xml" "YELP_GENERIC_VIEW_DRAW_OK"
require_log_marker "Surface buffer 1080x2280 for $YELP_PKG" "full phone Yelp surface buffer"
if grep -qE "^YELP_XML_BIND_GAP " "$MARKERS_PATH"; then
    echo "ERROR: forbidden Yelp marker present: YELP_XML_BIND_GAP" >&2
    missing=1
fi

if [ "$INTERACT" = "1" ]; then
    require_marker "^YELP_TOUCH_POLL_READY " "YELP_TOUCH_POLL_READY"
    require_marker "^YELP_TOUCH_POLL_OK " "YELP_TOUCH_POLL_OK"
    require_marker "^YELP_NETWORK_FETCH_BEGIN " "YELP_NETWORK_FETCH_BEGIN"
    require_marker "^YELP_NETWORK_BRIDGE_OK " "YELP_NETWORK_BRIDGE_OK"
    require_marker "^YELP_LIVE_JSON_OK " "YELP_LIVE_JSON_OK"
    require_marker "^YELP_LIVE_IMAGE_OK " "YELP_LIVE_IMAGE_OK"
    require_marker "^YELP_LIVE_ROW_IMAGE_OK index=4 " "YELP_LIVE_ROW_IMAGE_OK index=4"
    require_marker "^YELP_REST_MATRIX_BEGIN " "YELP_REST_MATRIX_BEGIN"
    require_marker "^YELP_REST_POST_OK " "YELP_REST_POST_OK"
    require_marker "^YELP_REST_HEADERS_OK " "YELP_REST_HEADERS_OK"
    require_marker "^YELP_REST_METHODS_OK " "YELP_REST_METHODS_OK"
    require_marker "^YELP_REST_HEAD_OK " "YELP_REST_HEAD_OK"
    require_marker "^YELP_REST_STATUS_OK status=418 " "YELP_REST_STATUS_OK status=418"
    require_marker "^YELP_REST_REDIRECT_OK status=200 " "YELP_REST_REDIRECT_OK"
    require_marker "^YELP_REST_TRUNCATE_OK .* truncated=true" "YELP_REST_TRUNCATE_OK"
    require_marker "^YELP_REST_TIMEOUT_OK " "YELP_REST_TIMEOUT_OK"
    require_marker "^YELP_REST_MATRIX_OK " "YELP_REST_MATRIX_OK"
    require_marker "^YELP_CARD_OK " "YELP_CARD_OK"
    require_marker "^YELP_CATEGORY_SELECT_OK " "YELP_CATEGORY_SELECT_OK"
    require_marker "^YELP_FILTER_TOGGLE_OK " "YELP_FILTER_TOGGLE_OK"
    require_marker "^YELP_LIST_SCROLL_OK " "YELP_LIST_SCROLL_OK"
    require_marker "^YELP_NEXT_PLACE_OK " "YELP_NEXT_PLACE_OK"
    require_marker "^YELP_DETAILS_OPEN_OK " "YELP_DETAILS_OPEN_OK"
    require_marker "^YELP_SAVE_PLACE_OK " "YELP_SAVE_PLACE_OK"
    require_marker "^YELP_NAV_SEARCH_OK " "YELP_NAV_SEARCH_OK"
    require_marker "^YELP_NAV_SAVED_OK " "YELP_NAV_SAVED_OK"
fi

if grep -qE "^YELP_NETWORK_(FETCH|BRIDGE)_FAIL " "$MARKERS_PATH"; then
    echo "ERROR: Yelp network marker failed" >&2
    grep -E "^YELP_NETWORK_(FETCH|BRIDGE)_FAIL " "$MARKERS_PATH" >&2 || true
    missing=1
fi
if grep -qE "^YELP_REST_MATRIX_FAIL " "$MARKERS_PATH"; then
    echo "ERROR: Yelp REST matrix marker failed" >&2
    grep -E "^YELP_REST_MATRIX_FAIL " "$MARKERS_PATH" >&2 || true
    missing=1
fi
if grep -qE "^YELP_GENERIC_VIEW_DRAW_FAIL " "$MARKERS_PATH"; then
    echo "ERROR: Yelp generic view draw marker failed" >&2
    grep -E "^YELP_GENERIC_VIEW_DRAW_FAIL " "$MARKERS_PATH" >&2 || true
    missing=1
fi
if grep -qE "^YELP_DIRECT_FRAME_FAIL " "$MARKERS_PATH"; then
    echo "ERROR: forbidden Yelp marker present: YELP_DIRECT_FRAME_FAIL" >&2
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

top_red = 0
x0, x1 = int(w * 0.05), int(w * 0.95)
y0, y1 = int(h * 0.03), int(h * 0.13)
for y in range(y0, y1, step):
    for x in range(x0, x1, step):
        r, g, b = img.getpixel((x, y))
        if r > 150 and g < 95 and b < 95:
            top_red += 1

bottom_nav_light = 0
bottom_nav_red = 0
y0, y1 = int(h * 0.82), int(h * 0.96)
for y in range(y0, y1, step):
    for x in range(x0, x1, step):
        r, g, b = img.getpixel((x, y))
        if r > 235 and g > 235 and b > 235:
            bottom_nav_light += 1
        if r > 170 and g < 80 and b < 80:
            bottom_nav_red += 1

photo_step = max(1, min(w, h) // 360)
photo_colors = []
photo_colored = 0
photo_natural = 0
px0, px1 = int(w * 0.045), int(w * 0.42)
py0, py1 = int(h * 0.34), int(h * 0.74)
for y in range(py0, py1, photo_step):
    for x in range(px0, px1, photo_step):
        r, g, b = img.getpixel((x, y))
        photo_colors.append((r, g, b))
        if max(r, g, b) - min(r, g, b) > 20 and not (r > 245 and g > 245 and b > 245):
            photo_colored += 1
        if r > 100 and g > 45 and b < 130 and max(r, g, b) - min(r, g, b) > 35:
            photo_natural += 1
photo_distinct = len(set(photo_colors))

with open(visual_path, "w", encoding="utf-8") as out:
    out.write(f"size={w}x{h}\n")
    out.write(f"sample_step={step}\n")
    out.write(f"distinct_colors={distinct}\n")
    out.write(f"colored_samples={colored}\n")
    out.write(f"top_red_samples={top_red}\n")
    out.write(f"bottom_nav_light_samples={bottom_nav_light}\n")
    out.write(f"bottom_nav_red_samples={bottom_nav_red}\n")
    out.write(f"photo_step={photo_step}\n")
    out.write(f"photo_distinct_colors={photo_distinct}\n")
    out.write(f"photo_colored_samples={photo_colored}\n")
    out.write(f"photo_natural_samples={photo_natural}\n")

if distinct < 16:
    raise SystemExit("too few screenshot colors")
if colored < 400:
    raise SystemExit("screenshot appears blank")
if top_red < 20:
    raise SystemExit("Yelp red header not visible")
if bottom_nav_light < 80 or bottom_nav_red < 10:
    raise SystemExit("bottom navigation not visible")
if photo_distinct < 300 or photo_colored < 500 or photo_natural < 80:
    raise SystemExit("live photo region not visible")
PY
then
    echo "ERROR: screenshot visual gate failed" >&2
    missing=1
fi

if [ "$missing" -ne 0 ]; then
    echo ""
    echo "Yelp live acceptance: FAILED"
    echo "Artifacts:"
    echo "  log:     $LOG_PATH"
    echo "  markers: $MARKERS_PATH"
    echo "  trace:   $TRACE_PATH"
    echo "  screen:  $PNG_PATH"
    echo "  visual:  $VISUAL_PATH"
    exit 4
fi

echo ""
echo "Yelp live acceptance: PASSED"
echo "Hashes:"
echo "  dalvikvm=$local_dvm_hash"
echo "  aosp-shim.dex=$local_shim_hash"
echo "  westlake-yelp-live-debug.apk=$local_yelp_hash"
echo "Artifacts:"
echo "  log:     $LOG_PATH"
echo "  markers: $MARKERS_PATH"
echo "  trace:   $TRACE_PATH"
echo "  screen:  $PNG_PATH"
echo "  visual:  $VISUAL_PATH"

ACCEPT_DIR="$ARTIFACT_DIR/accepted/yelp_live/${local_shim_hash}_${local_yelp_hash}"
mkdir -p "$ACCEPT_DIR"
cp "$LOG_PATH" "$MARKERS_PATH" "$TRACE_PATH" "$PNG_PATH" "$VISUAL_PATH" \
    "$LOG_MARKERS_PATH" "$PS_PATH" "$TOP_PATH" "$ACCEPT_DIR"/
echo "  accepted copy: $ACCEPT_DIR"
