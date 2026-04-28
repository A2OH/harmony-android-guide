#!/bin/bash
# Usage:
#   ./scripts/run-cutoff-canary.sh control L1
#   ./scripts/run-cutoff-canary.sh target L2
#
# Runs the staged cutoff canary on the phone via the configured ADB server,
# captures log/screenshot/process/activity artifacts, and prints a short summary.

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
DALVIKVM_SRC="${DALVIKVM_SRC:-/home/dspfac/art-latest/build-bionic-arm64/bin/dalvikvm}"
AOSP_SHIM_SRC="${AOSP_SHIM_SRC:-$REPO_ROOT/aosp-shim.dex}"
CANARY_APK_SRC_EXPLICIT="${CANARY_APK_SRC+x}"
CANARY_APK_SRC="${CANARY_APK_SRC:-$REPO_ROOT/test-apps/03-cutoff-canary/build/dist/cutoff-canary-debug.apk}"
CANARY_HILT_APK_SRC="${CANARY_HILT_APK_SRC:-$REPO_ROOT/test-apps/03-cutoff-canary/build/dist/cutoff-canary-hilt-debug.apk}"
CANARY_APK_AUTO_INSTALL="${CANARY_APK_AUTO_INSTALL:-1}"
HOST_PKG="com.westlake.host"
HOST_ACTIVITY="com.westlake.host/.WestlakeActivity"
CANARY_PKG="com.westlake.cutoffcanary"
CANARY_ACTIVITY="com.westlake.cutoffcanary.StageActivity"
WAIT_SECS="${WAIT_SECS:-8}"
PHONE_DIR="${PHONE_DIR:-/data/local/tmp/westlake}"

MODE="${1:-control}"
STAGE="${2:-L1}"
STAGE="${STAGE^^}"
ACCEPT_STAGE="$STAGE"
LOOKUP_PROBE=0
INTERFACE_PROBE=0
STATE_PROBE=0
RECREATE_PROBE=0
WAT_RECREATE_PROBE=0
WAT_FACTORY_PROBE=0
WAT_APP_FACTORY_PROBE=0
WAT_APP_REFLECT_PROBE=0
WAT_CORE_APP_PROBE=0
WAT_HILT_APP_PROBE=0
if [ "$STAGE" = "L3LOOKUP" ]; then
    ACCEPT_STAGE="L3"
    LOOKUP_PROBE=1
fi
if [ "$STAGE" = "L3IFACE" ]; then
    ACCEPT_STAGE="L3"
    INTERFACE_PROBE=1
fi
if [ "$STAGE" = "L4STATE" ]; then
    ACCEPT_STAGE="L4"
    STATE_PROBE=1
fi
if [ "$STAGE" = "L4RECREATE" ]; then
    ACCEPT_STAGE="L4"
    STATE_PROBE=1
    RECREATE_PROBE=1
fi
if [ "$STAGE" = "L4WATRECREATE" ]; then
    ACCEPT_STAGE="L4"
    STATE_PROBE=1
    RECREATE_PROBE=1
    WAT_RECREATE_PROBE=1
fi
if [ "$STAGE" = "L4WATFACTORY" ]; then
    ACCEPT_STAGE="L4"
    STATE_PROBE=1
    RECREATE_PROBE=1
    WAT_RECREATE_PROBE=1
    WAT_FACTORY_PROBE=1
fi
if [ "$STAGE" = "L4WATAPPFACTORY" ]; then
    ACCEPT_STAGE="L4"
    STATE_PROBE=1
    RECREATE_PROBE=1
    WAT_RECREATE_PROBE=1
    WAT_FACTORY_PROBE=1
    WAT_APP_FACTORY_PROBE=1
fi
if [ "$STAGE" = "L4WATAPPREFLECT" ]; then
    ACCEPT_STAGE="L4"
    STATE_PROBE=1
    RECREATE_PROBE=1
    WAT_RECREATE_PROBE=1
    WAT_FACTORY_PROBE=1
    WAT_APP_FACTORY_PROBE=1
    WAT_APP_REFLECT_PROBE=1
fi
if [ "$STAGE" = "L4WATCOREAPP" ]; then
    ACCEPT_STAGE="L4"
    STATE_PROBE=1
    RECREATE_PROBE=1
    WAT_RECREATE_PROBE=1
    WAT_APP_FACTORY_PROBE=1
    WAT_CORE_APP_PROBE=1
fi
if [ "$STAGE" = "L4WATHILTAPP" ]; then
    ACCEPT_STAGE="L4"
    STATE_PROBE=1
    RECREATE_PROBE=1
    WAT_RECREATE_PROBE=1
    WAT_APP_FACTORY_PROBE=1
    WAT_HILT_APP_PROBE=1
fi
if [[ "$STAGE" == L4WATPRECREATE* ]]; then
    ACCEPT_STAGE="L4"
fi

if [ "$STAGE" = "L4WATHILTAPP" ] && [ -z "$CANARY_APK_SRC_EXPLICIT" ]; then
    CANARY_APK_SRC="$CANARY_HILT_APK_SRC"
fi

case "$MODE" in
    control)
        LAUNCH_VALUE="VM_APK_CONTROL:${CANARY_PKG}:${CANARY_ACTIVITY}:Canary${STAGE}Control:${STAGE}"
        LABEL="cutoff_canary_control_${STAGE,,}"
        ;;
    target)
        LAUNCH_VALUE="VM_APK_TARGET:${CANARY_PKG}:${CANARY_ACTIVITY}:Canary${STAGE}Target:${STAGE}"
        LABEL="cutoff_canary_target_${STAGE,,}"
        ;;
    *)
        echo "ERROR: mode must be 'control' or 'target'" >&2
        exit 1
        ;;
esac

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

echo "=== Westlake Cutoff Canary Run ==="
echo "Mode:        $MODE"
echo "Stage:       $STAGE"
echo "ADB binary:  $ADB_BIN"
echo "ADB server:  ${ADB_HOST:-default}:$ADB_PORT"
echo "Device:      $ADB_SERIAL"
echo "Artifacts:   $ARTIFACT_DIR"

timeout "$ADB_TIMEOUT" "${ADB[@]}" get-state >/dev/null

read_installed_canary_apk_hash() {
    remote_canary_path="$("${ADB[@]}" shell pm path "$CANARY_PKG" 2>/dev/null \
        | tr -d '\r' \
        | sed -n 's/^package://p' \
        | head -n 1 || true)"
    if [ -z "$remote_canary_path" ]; then
        remote_canary_hash=""
        return 0
    fi
    remote_canary_hash="$("${ADB[@]}" shell sha256sum "$remote_canary_path" 2>/dev/null \
        | awk '{print $1}' || true)"
}

echo "[0/6] Runtime provenance preflight..."
if [ ! -f "$DALVIKVM_SRC" ]; then
    echo "ERROR: dalvikvm source not found: $DALVIKVM_SRC" >&2
    exit 2
fi
if [ ! -f "$AOSP_SHIM_SRC" ]; then
    echo "ERROR: aosp-shim source not found: $AOSP_SHIM_SRC" >&2
    exit 2
fi
if [ ! -f "$CANARY_APK_SRC" ]; then
    echo "ERROR: cutoff canary APK not found: $CANARY_APK_SRC" >&2
    echo "       run test-apps/03-cutoff-canary/build-apk.sh" >&2
    exit 2
fi

local_dvm_hash="$(sha256sum "$DALVIKVM_SRC" | awk '{print $1}')"
local_shim_hash="$(sha256sum "$AOSP_SHIM_SRC" | awk '{print $1}')"
local_canary_hash="$(sha256sum "$CANARY_APK_SRC" | awk '{print $1}')"
remote_dvm_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/dalvikvm" 2>/dev/null | awk '{print $1}' || true)"
remote_shim_hash="$("${ADB[@]}" shell sha256sum "$PHONE_DIR/aosp-shim.dex" 2>/dev/null | awk '{print $1}' || true)"
read_installed_canary_apk_hash
echo "  dalvikvm local=$local_dvm_hash phone=${remote_dvm_hash:-missing}"
echo "  aosp-shim local=$local_shim_hash phone=${remote_shim_hash:-missing}"
echo "  cutoff-canary.apk local=$local_canary_hash phone=${remote_canary_hash:-missing}"

if [ "$remote_dvm_hash" != "$local_dvm_hash" ]; then
    echo "ERROR: phone dalvikvm hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_shim_hash" != "$local_shim_hash" ]; then
    echo "ERROR: phone aosp-shim.dex hash mismatch; run scripts/sync-westlake-phone-runtime.sh" >&2
    exit 3
fi
if [ "$remote_canary_hash" != "$local_canary_hash" ]; then
    if [ "$CANARY_APK_AUTO_INSTALL" = "1" ]; then
        echo "  installing cutoff canary APK to match local hash..."
        "${ADB[@]}" install -r "$CANARY_APK_SRC" >/dev/null
        read_installed_canary_apk_hash
        echo "  cutoff-canary.apk phone=${remote_canary_hash:-missing}"
    else
        echo "ERROR: phone cutoff canary APK hash mismatch; install $CANARY_APK_SRC" >&2
        exit 3
    fi
fi
if [ "$remote_canary_hash" != "$local_canary_hash" ]; then
    echo "ERROR: phone cutoff canary APK hash mismatch after install" >&2
    exit 3
fi

echo "[1/6] Force-stopping old app state..."
"${ADB[@]}" shell am force-stop "$HOST_PKG" >/dev/null || true
"${ADB[@]}" shell am force-stop "$CANARY_PKG" >/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" mkdir -p "/data/user/0/$HOST_PKG/files/vm" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_MARKER_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell run-as "$HOST_PKG" rm -f "$RUNAS_TRACE_PATH" >/dev/null 2>&1 || true
"${ADB[@]}" shell rm -f "$PUBLIC_MARKER_PATH" "$PUBLIC_TRACE_PATH" >/dev/null 2>&1 || true

echo "[2/6] Clearing logcat..."
"${ADB[@]}" logcat -c

echo "[3/6] Launching Westlake host..."
"${ADB[@]}" shell am start -S -W -n "$HOST_ACTIVITY" --es launch "$LAUNCH_VALUE"

echo "[4/6] Waiting ${WAIT_SECS}s for settle..."
sleep "$WAIT_SECS"

echo "[5/6] Capturing artifacts..."
"${ADB[@]}" logcat -d > "$LOG_PATH"
"${ADB[@]}" shell ps -A | grep -E 'westlake|dalvikvm|cutoffcanary' > "$PS_PATH" || true
"${ADB[@]}" shell dumpsys activity top > "$TOP_PATH"
"${ADB[@]}" exec-out screencap -p > "$PNG_PATH"
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_MARKER_PATH" > "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_MARKER_PATH" >> "$MARKERS_PATH" 2>/dev/null || true
"${ADB[@]}" shell run-as "$HOST_PKG" cat "$RUNAS_TRACE_PATH" > "$TRACE_PATH" 2>/dev/null || true
"${ADB[@]}" shell cat "$PUBLIC_TRACE_PATH" >> "$TRACE_PATH" 2>/dev/null || true
grep -nE "CANARY_|CV |control canary headless|activity-state" "$LOG_PATH" > "$LOG_MARKERS_PATH" || true

echo "[6/6] Summary..."
echo "Processes:"
if [ -s "$PS_PATH" ]; then
    cat "$PS_PATH"
else
    echo "(no matching processes)"
fi

echo ""
echo "Key log markers:"
grep -nE \
    "Auto-launching|Launching VM APK|CANARY_|CV |No implementation found|UnsatisfiedLinkError|SIGBUS|SIGILL|Exception in thread|Displayed com.westlake" \
    "$LOG_PATH" || true

if [ -s "$MARKERS_PATH" ]; then
    echo ""
    echo "App-owned canary markers:"
    cat "$MARKERS_PATH"
else
    echo ""
    echo "App-owned canary markers: (none)"
fi

if [ -s "$TRACE_PATH" ]; then
    echo ""
    echo "Lifecycle trace:"
    cat "$TRACE_PATH"
fi

echo ""
missing=0
require_marker() {
    local pattern="$1"
    local label="$2"
    if ! grep -qE "$pattern" "$MARKERS_PATH"; then
        echo "ERROR: missing app-owned marker: $label" >&2
        missing=1
    fi
}

require_trace_marker() {
    local pattern="$1"
    local label="$2"
    if ! grep -qE "$pattern" "$TRACE_PATH"; then
        echo "ERROR: missing lifecycle trace marker: $label" >&2
        missing=1
    fi
}

reject_trace_marker() {
    local pattern="$1"
    local label="$2"
    if grep -qE "$pattern" "$TRACE_PATH"; then
        echo "ERROR: forbidden lifecycle trace marker present: $label" >&2
        missing=1
    fi
}

reject_marker() {
    local pattern="$1"
    local label="$2"
    if grep -qE "$pattern" "$MARKERS_PATH"; then
        echo "ERROR: forbidden app-owned marker present: $label" >&2
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

require_marker_count_at_least() {
    local pattern="$1"
    local min_count="$2"
    local label="$3"
    local count
    count="$(grep -cE "$pattern" "$MARKERS_PATH" 2>/dev/null || true)"
    if [ "${count:-0}" -lt "$min_count" ]; then
        echo "ERROR: missing app-owned marker count: $label expected>=$min_count got=${count:-0}" >&2
        missing=1
    fi
}

require_marker_order() {
    local first_pattern="$1"
    local second_pattern="$2"
    local label="$3"
    local first_line
    local second_line
    first_line="$(grep -nE "$first_pattern" "$MARKERS_PATH" 2>/dev/null \
        | head -n 1 \
        | cut -d: -f1 || true)"
    second_line="$(grep -nE "$second_pattern" "$MARKERS_PATH" 2>/dev/null \
        | head -n 1 \
        | cut -d: -f1 || true)"
    if [ -z "$first_line" ] || [ -z "$second_line" ] \
        || [ "$first_line" -ge "$second_line" ]; then
        echo "ERROR: marker order failed: $label first=${first_line:-missing} second=${second_line:-missing}" >&2
        missing=1
    fi
}

require_marker "^CANARY_L0_OK " "CANARY_L0_OK"
if [ "$ACCEPT_STAGE" != "L0" ]; then
    require_marker "^CANARY_${ACCEPT_STAGE}_ON_CREATE " "CANARY_${ACCEPT_STAGE}_ON_CREATE"
    if [ "$ACCEPT_STAGE" = "L1" ]; then
        require_marker "^CANARY_L1_VIEW_BUILD_OK " "CANARY_L1_VIEW_BUILD_OK"
    fi
    if [ "$ACCEPT_STAGE" = "L3" ]; then
        require_marker "^CANARY_L3_SUPER_ON_CREATE_OK " "CANARY_L3_SUPER_ON_CREATE_OK"
        require_marker "^CANARY_L3_CONTAINER_OK " "CANARY_L3_CONTAINER_OK"
        require_marker "^CANARY_L3_FRAGMENT_MANAGER_OK " "CANARY_L3_FRAGMENT_MANAGER_OK"
        require_marker "^CANARY_L3_FRAGMENT_TX_OK " "CANARY_L3_FRAGMENT_TX_OK"
        require_marker "^CANARY_L3_FRAGMENT_ADD_OK " "CANARY_L3_FRAGMENT_ADD_OK"
        require_marker "^CANARY_L3_FRAGMENT_COMMIT_OK " "CANARY_L3_FRAGMENT_COMMIT_OK"
        if [ "$LOOKUP_PROBE" -ne 0 ]; then
            require_marker "^CANARY_L3_FRAGMENT_LOOKUP_OK " "CANARY_L3_FRAGMENT_LOOKUP_OK"
        fi
        if [ "$INTERFACE_PROBE" -ne 0 ]; then
            require_marker "^CANARY_L3_FRAGMENT_INTERFACE_GET_OK " "CANARY_L3_FRAGMENT_INTERFACE_GET_OK"
        fi
        require_marker "^CANARY_L3_FRAGMENT_VIEW_OK " "CANARY_L3_FRAGMENT_VIEW_OK"
        require_marker "^CANARY_L3_FRAGMENT_ON_RESUME " "CANARY_L3_FRAGMENT_ON_RESUME"
    fi
    if [ "$ACCEPT_STAGE" = "L4" ]; then
        require_marker "^CANARY_L4_SUPER_ON_CREATE_OK " "CANARY_L4_SUPER_ON_CREATE_OK"
        require_marker "^CANARY_L4_SAVEDSTATE_OWNER_OK " "CANARY_L4_SAVEDSTATE_OWNER_OK"
        require_marker "^CANARY_L4_SAVEDSTATE_PROVIDER_OK " "CANARY_L4_SAVEDSTATE_PROVIDER_OK"
        require_marker "^CANARY_L4_VIEWMODEL_OWNER_OK " "CANARY_L4_VIEWMODEL_OWNER_OK"
        require_marker "^CANARY_L4_VIEWMODEL_OK " "CANARY_L4_VIEWMODEL_OK"
        require_marker "^CANARY_L4_CONTAINER_OK " "CANARY_L4_CONTAINER_OK"
        require_marker "^CANARY_L4_FRAGMENT_MANAGER_OK " "CANARY_L4_FRAGMENT_MANAGER_OK"
        require_marker "^CANARY_L4_FRAGMENT_TX_OK " "CANARY_L4_FRAGMENT_TX_OK"
        require_marker "^CANARY_L4_FRAGMENT_ADD_OK " "CANARY_L4_FRAGMENT_ADD_OK"
        require_marker "^CANARY_L4_FRAGMENT_COMMIT_OK " "CANARY_L4_FRAGMENT_COMMIT_OK"
        require_marker "^CANARY_L4_FRAGMENT_LOOKUP_OK " "CANARY_L4_FRAGMENT_LOOKUP_OK"
        require_marker "^CANARY_L4_FRAGMENT_VIEW_OK " "CANARY_L4_FRAGMENT_VIEW_OK"
        require_marker "^CANARY_L4_FRAGMENT_SAVEDSTATE_OK " "CANARY_L4_FRAGMENT_SAVEDSTATE_OK"
        require_marker "^CANARY_L4_FRAGMENT_VIEWMODEL_OK " "CANARY_L4_FRAGMENT_VIEWMODEL_OK"
        require_marker "^CANARY_L4_FRAGMENT_ON_RESUME " "CANARY_L4_FRAGMENT_ON_RESUME"
        if [ "$STATE_PROBE" -ne 0 ]; then
            require_marker "^CANARY_L4STATE_REGISTRY_RESTORE_OK " "CANARY_L4STATE_REGISTRY_RESTORE_OK"
            require_marker "^CANARY_L4STATE_SAVEDSTATE_HANDLE_OK " "CANARY_L4STATE_SAVEDSTATE_HANDLE_OK"
            require_marker "^CANARY_L4STATE_CREATION_EXTRAS_OK " "CANARY_L4STATE_CREATION_EXTRAS_OK"
            require_marker "^CANARY_L4STATE_VIEWTREE_LIFECYCLE_OWNER_OK " "CANARY_L4STATE_VIEWTREE_LIFECYCLE_OWNER_OK"
            require_marker "^CANARY_L4STATE_VIEWTREE_VIEWMODEL_OWNER_OK " "CANARY_L4STATE_VIEWTREE_VIEWMODEL_OWNER_OK"
            require_marker "^CANARY_L4STATE_VIEWTREE_SAVEDSTATE_OWNER_OK " "CANARY_L4STATE_VIEWTREE_SAVEDSTATE_OWNER_OK"
            require_marker "^CANARY_L4STATE_FRAGMENT_REGISTRY_RESTORE_OK " "CANARY_L4STATE_FRAGMENT_REGISTRY_RESTORE_OK"
            require_marker "^CANARY_L4STATE_FRAGMENT_VIEWTREE_OK " "CANARY_L4STATE_FRAGMENT_VIEWTREE_OK"
            require_marker "^CANARY_L4STATE_OK " "CANARY_L4STATE_OK"
        fi
        if [ "$RECREATE_PROBE" -ne 0 ]; then
            require_marker "^CANARY_L4RECREATE_SAVE_STATE_OK " "CANARY_L4RECREATE_SAVE_STATE_OK"
            require_marker "^CANARY_L4RECREATE_ON_PAUSE " "CANARY_L4RECREATE_ON_PAUSE"
            require_marker "^CANARY_L4RECREATE_ON_STOP " "CANARY_L4RECREATE_ON_STOP"
            require_marker "^CANARY_L4RECREATE_ON_DESTROY " "CANARY_L4RECREATE_ON_DESTROY"
            require_marker "^CANARY_L4RECREATE_NEW_INSTANCE_OK " "CANARY_L4RECREATE_NEW_INSTANCE_OK"
            require_marker "^CANARY_L4RECREATE_ON_CREATE_RESTORED_OK " "CANARY_L4RECREATE_ON_CREATE_RESTORED_OK"
            require_marker "^CANARY_L4RECREATE_REGISTRY_RESTORED_OK " "CANARY_L4RECREATE_REGISTRY_RESTORED_OK"
            require_marker "^CANARY_L4RECREATE_ON_RESUME_RESTORED_OK " "CANARY_L4RECREATE_ON_RESUME_RESTORED_OK"
            require_marker "^CANARY_L4RECREATE_OK " "CANARY_L4RECREATE_OK"
        fi
        if [ "$WAT_RECREATE_PROBE" -ne 0 ]; then
            require_trace_marker "^CV canary WAT launch begin" "CV canary WAT launch begin"
            require_trace_marker "^CV WAT recreate begin" "CV WAT recreate begin"
            require_trace_marker "^CV WAT recreate end" "CV WAT recreate end"
        fi
        if [ "$WAT_FACTORY_PROBE" -ne 0 ]; then
            require_trace_marker "^CV canary WAT factory manifest parsed" \
                "CV canary WAT factory manifest parsed"
            require_trace_marker "^CV canary WAT factory set done" \
                "CV canary WAT factory set done"
            require_marker "^CANARY_L4FACTORY_CTOR_OK " "CANARY_L4FACTORY_CTOR_OK"
            require_marker_count_at_least "^CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK " 2 \
                "CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK"
            require_marker_count_at_least "^CANARY_L4FACTORY_ACTIVITY_RETURNED_OK " 2 \
                "CANARY_L4FACTORY_ACTIVITY_RETURNED_OK"
        fi
        if [ "$WAT_APP_FACTORY_PROBE" -ne 0 ]; then
            require_trace_marker "^CV canary application manual skipped app factory" \
                "CV canary application manual skipped app factory"
            require_trace_marker "^CV canary WAT app factory application parsed" \
                "CV canary WAT app factory application parsed"
            require_trace_marker "^CV canary WAT app factory force set done" \
                "CV canary WAT app factory force set done"
            require_trace_marker "^CV WAT app factory preactivity makeApplication begin" \
                "CV WAT app factory preactivity makeApplication begin"
            require_trace_marker "^CV WAT app factory preactivity makeApplication returned" \
                "CV WAT app factory preactivity makeApplication returned"
            if [ "$WAT_HILT_APP_PROBE" -ne 0 ]; then
                require_trace_marker "^CV canary WAT factory manifest parsed androidx\\.core\\.app\\.CoreComponentFactory" \
                    "CV canary WAT factory manifest parsed CoreComponentFactory"
                require_trace_marker "^CV canary WAT factory set done" \
                    "CV canary WAT factory set done"
                require_trace_marker "^CV canary WAT app factory application parsed com\\.westlake\\.cutoffcanary\\.CanaryHiltApp" \
                    "CV canary WAT app factory application parsed CanaryHiltApp"
                require_trace_marker "^CV WAT makeApplication begin com\\.westlake\\.cutoffcanary\\.CanaryHiltApp" \
                    "CV WAT makeApplication begin CanaryHiltApp"
                require_trace_marker "^CV WAT instantiateApplication begin com\\.westlake\\.cutoffcanary\\.CanaryHiltApp" \
                    "CV WAT instantiateApplication begin CanaryHiltApp"
                require_trace_marker "^CV WAT instantiateApplication reflect begin androidx\\.core\\.app\\.CoreComponentFactory" \
                    "CV WAT instantiateApplication reflect begin CoreComponentFactory"
                require_trace_marker "^CV WAT instantiateApplication reflect returned" \
                    "CV WAT instantiateApplication reflect returned"
                require_trace_marker "^CV WAT instantiateApplication returned com\\.westlake\\.cutoffcanary\\.CanaryHiltApp" \
                    "CV WAT instantiateApplication returned CanaryHiltApp"
                require_trace_marker "^CV ComponentActivity context begin" \
                    "CV ComponentActivity context begin"
                require_trace_marker "^CV ComponentActivity context end" \
                    "CV ComponentActivity context end"
            elif [ "$WAT_CORE_APP_PROBE" -ne 0 ]; then
                require_trace_marker "^CV canary WAT core factory selected androidx\\.core\\.app\\.CoreComponentFactory" \
                    "CV canary WAT core factory selected"
                require_trace_marker "^CV canary WAT factory set done" \
                    "CV canary WAT factory set done"
                require_trace_marker "^CV WAT makeApplication begin com\\.westlake\\.cutoffcanary\\.CanaryCoreWrappedApp" \
                    "CV WAT makeApplication begin CanaryCoreWrappedApp"
                require_trace_marker "^CV WAT instantiateApplication begin com\\.westlake\\.cutoffcanary\\.CanaryCoreWrappedApp" \
                    "CV WAT instantiateApplication begin CanaryCoreWrappedApp"
                require_trace_marker "^CV WAT instantiateApplication reflect begin androidx\\.core\\.app\\.CoreComponentFactory" \
                    "CV WAT instantiateApplication reflect begin CoreComponentFactory"
                require_trace_marker "^CV WAT instantiateApplication reflect returned" \
                    "CV WAT instantiateApplication reflect returned"
                require_trace_marker "^CV WAT instantiateApplication returned com\\.westlake\\.cutoffcanary\\.CanaryCoreWrapperApp" \
                    "CV WAT instantiateApplication returned CanaryCoreWrapperApp"
            else
                require_trace_marker "^CV WAT makeApplication begin com\\.westlake\\.cutoffcanary\\.CanaryApp" \
                    "CV WAT makeApplication begin CanaryApp"
                require_trace_marker "^CV WAT instantiateApplication begin com\\.westlake\\.cutoffcanary\\.CanaryApp" \
                    "CV WAT instantiateApplication begin CanaryApp"
                require_trace_marker "^CV WAT instantiateApplication returned com\\.westlake\\.cutoffcanary\\.CanaryApp" \
                    "CV WAT instantiateApplication returned CanaryApp"
            fi
            require_trace_marker "^CV WAT application onCreate begin" \
                "CV WAT application onCreate begin"
            require_trace_marker "^CV WAT application onCreate returned" \
                "CV WAT application onCreate returned"
            if [ "$WAT_HILT_APP_PROBE" -ne 0 ]; then
                require_marker "^CANARY_L4HILTAPP_APP_CTOR_OK " \
                    "CANARY_L4HILTAPP_APP_CTOR_OK"
                require_marker "^CANARY_L4HILTAPP_COMPONENT_MANAGER_OK " \
                    "CANARY_L4HILTAPP_COMPONENT_MANAGER_OK"
                require_marker "^CANARY_L4HILTAPP_GENERATED_COMPONENT_OK " \
                    "CANARY_L4HILTAPP_GENERATED_COMPONENT_OK"
                require_marker "^CANARY_L4HILTAPP_APP_INJECT_OK " \
                    "CANARY_L4HILTAPP_APP_INJECT_OK"
                require_marker "^CANARY_L4HILTAPP_APP_ON_CREATE_OK " \
                    "CANARY_L4HILTAPP_APP_ON_CREATE_OK"
                require_marker "^CANARY_L4HILTAPP_ACTIVITY_CTOR_OK " \
                    "CANARY_L4HILTAPP_ACTIVITY_CTOR_OK"
                require_marker "^CANARY_L4HILTAPP_CONTEXT_AVAILABLE_OK " \
                    "CANARY_L4HILTAPP_CONTEXT_AVAILABLE_OK"
                require_marker "^CANARY_L4HILTAPP_ACTIVITY_COMPONENT_MANAGER_OK " \
                    "CANARY_L4HILTAPP_ACTIVITY_COMPONENT_MANAGER_OK"
                require_marker "^CANARY_L4HILTAPP_ACTIVITY_GENERATED_COMPONENT_OK " \
                    "CANARY_L4HILTAPP_ACTIVITY_GENERATED_COMPONENT_OK"
                require_marker "^CANARY_L4HILTAPP_ACTIVITY_INJECT_OK " \
                    "CANARY_L4HILTAPP_ACTIVITY_INJECT_OK"
                require_marker_order "^CANARY_L4HILTAPP_APP_CTOR_OK " \
                    "^CANARY_L4HILTAPP_APP_INJECT_OK " \
                    "Hilt app ctor before app inject"
                require_marker_order "^CANARY_L4HILTAPP_APP_INJECT_OK " \
                    "^CANARY_L4HILTAPP_APP_ON_CREATE_OK " \
                    "Hilt app inject before app onCreate"
                require_marker_order "^CANARY_L4HILTAPP_APP_ON_CREATE_OK " \
                    "^CANARY_L4HILTAPP_ACTIVITY_CTOR_OK " \
                    "Hilt app onCreate before Activity ctor"
                require_marker_order "^CANARY_L4HILTAPP_CONTEXT_AVAILABLE_OK " \
                    "^CANARY_L4HILTAPP_ACTIVITY_INJECT_OK " \
                    "Hilt context available before Activity inject"
                require_marker_order "^CANARY_L4HILTAPP_ACTIVITY_INJECT_OK " \
                    "^CANARY_L4_ON_CREATE " \
                    "Hilt Activity inject before Activity onCreate"
                reject_marker "^CANARY_L4APPFACTORY_" \
                    "custom canary AppComponentFactory marker"
                reject_trace_marker "^CV WAT instantiateApplication failed" \
                    "WAT instantiateApplication failed"
            elif [ "$WAT_CORE_APP_PROBE" -ne 0 ]; then
                require_marker "^CANARY_L4COREAPP_WRAPPED_CTOR_OK " \
                    "CANARY_L4COREAPP_WRAPPED_CTOR_OK"
                require_marker "^CANARY_L4COREAPP_GET_WRAPPER_OK " \
                    "CANARY_L4COREAPP_GET_WRAPPER_OK"
                require_marker "^CANARY_L4COREAPP_WRAPPER_CTOR_OK " \
                    "CANARY_L4COREAPP_WRAPPER_CTOR_OK"
                require_marker "^CANARY_L4COREAPP_WRAPPER_RETURNED_OK " \
                    "CANARY_L4COREAPP_WRAPPER_RETURNED_OK"
                require_marker "^CANARY_L4COREAPP_WRAPPER_ON_CREATE_OK " \
                    "CANARY_L4COREAPP_WRAPPER_ON_CREATE_OK"
                require_marker "^CANARY_L4COREAPP_WRAPPER_ON_CREATE_RETURNED_OK " \
                    "CANARY_L4COREAPP_WRAPPER_ON_CREATE_RETURNED_OK"
                require_marker_order "^CANARY_L4COREAPP_WRAPPED_CTOR_OK " \
                    "^CANARY_L4COREAPP_GET_WRAPPER_OK " \
                    "Core wrapped app ctor before getWrapper"
                require_marker_order "^CANARY_L4COREAPP_GET_WRAPPER_OK " \
                    "^CANARY_L4COREAPP_WRAPPER_CTOR_OK " \
                    "Core getWrapper before wrapper ctor"
                require_marker_order "^CANARY_L4COREAPP_WRAPPER_CTOR_OK " \
                    "^CANARY_L4COREAPP_WRAPPER_RETURNED_OK " \
                    "Core wrapper ctor before wrapper returned"
                require_marker_order "^CANARY_L4COREAPP_WRAPPER_RETURNED_OK " \
                    "^CANARY_L4COREAPP_WRAPPER_ON_CREATE_OK " \
                    "Core wrapper returned before wrapper onCreate"
                require_marker_order "^CANARY_L4COREAPP_WRAPPER_ON_CREATE_RETURNED_OK " \
                    "^CANARY_L4_ON_CREATE " \
                    "Core wrapper onCreate before Activity onCreate"
                reject_marker "^CANARY_L4COREAPP_WRAPPED_ON_CREATE_ERR " \
                    "wrapped Application onCreate"
                reject_marker "^CANARY_L4APPFACTORY_" \
                    "custom canary AppComponentFactory marker"
            else
                require_marker "^CANARY_L4APPFACTORY_INSTANTIATE_APPLICATION_OK " \
                    "CANARY_L4APPFACTORY_INSTANTIATE_APPLICATION_OK"
                require_marker "^CANARY_L4APPFACTORY_APPLICATION_RETURNED_OK " \
                    "CANARY_L4APPFACTORY_APPLICATION_RETURNED_OK"
                require_marker_order "^CANARY_L4APPFACTORY_INSTANTIATE_APPLICATION_OK " \
                    "^CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK " \
                    "Application factory before Activity factory"
            fi
            if [ "$WAT_APP_REFLECT_PROBE" -ne 0 ]; then
                require_marker "^CANARY_L4APPREFLECT_STAGE_OK " \
                    "CANARY_L4APPREFLECT_STAGE_OK"
                require_marker "^CANARY_L4APPREFLECT_SUPER_CALL " \
                    "CANARY_L4APPREFLECT_SUPER_CALL"
                require_marker "^CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK " \
                    "CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK"
                require_marker "^CANARY_L4APPREFLECT_SUPER_RETURNED " \
                    "CANARY_L4APPREFLECT_SUPER_RETURNED"
                require_marker "^CANARY_L4APPREFLECT_APPLICATION_RETURNED_OK " \
                    "CANARY_L4APPREFLECT_APPLICATION_RETURNED_OK"
                require_marker_order "^CANARY_L4APPREFLECT_SUPER_CALL " \
                    "^CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK " \
                    "super instantiateApplication before CanaryApp ctor"
                require_marker_order "^CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK " \
                    "^CANARY_L4APPREFLECT_SUPER_RETURNED " \
                    "CanaryApp ctor before super returned"
                reject_marker "^CANARY_L4APPFACTORY_DIRECT_CANARY_APP_OK " \
                    "direct CanaryApp constructor marker"
            else
                if [ "$WAT_CORE_APP_PROBE" -eq 0 ] && [ "$WAT_HILT_APP_PROBE" -eq 0 ]; then
                    require_marker "^CANARY_L4APPFACTORY_DIRECT_CANARY_APP_OK " \
                        "CANARY_L4APPFACTORY_DIRECT_CANARY_APP_OK"
                fi
            fi
            reject_trace_marker "^CV canary application onCreate begin" \
                "launcher-created CanaryApp onCreate"
            reject_log_marker "PF301 strict WAT impl step5 MiniServer app nonnull" \
                "MiniServer Application reuse"
        fi
    fi
    require_marker "^CANARY_${ACCEPT_STAGE}_OK " "CANARY_${ACCEPT_STAGE}_OK"
    require_marker "^CANARY_${ACCEPT_STAGE}_ON_START " "CANARY_${ACCEPT_STAGE}_ON_START"
    require_marker "^CANARY_${ACCEPT_STAGE}_ON_RESUME " "CANARY_${ACCEPT_STAGE}_ON_RESUME"
fi

if [ "$missing" -ne 0 ]; then
    echo ""
    echo "Canary acceptance: FAILED (app-owned lifecycle/view markers missing)"
    echo "Diagnostic log markers: $LOG_MARKERS_PATH"
    echo ""
    echo "Artifacts:"
    printf "  %s\n" "$LOG_PATH" "$PS_PATH" "$TOP_PATH" "$PNG_PATH" "$MARKERS_PATH" "$TRACE_PATH" "$LOG_MARKERS_PATH"
    exit 4
fi

echo "Canary acceptance: PASSED ($MODE $STAGE; app-owned lifecycle/view markers present; dalvikvm=$local_dvm_hash; aosp-shim=$local_shim_hash; canary-apk=$local_canary_hash)"

echo ""
echo "Artifacts:"
printf "  %s\n" "$LOG_PATH" "$PS_PATH" "$TOP_PATH" "$PNG_PATH" "$MARKERS_PATH" "$TRACE_PATH" "$LOG_MARKERS_PATH"
