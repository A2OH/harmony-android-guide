#!/system/bin/sh
# ============================================================================
# Westlake — M2 servicemanager sandbox bring-up (OnePlus 6, kernel 4.9)
#
# This phone's kernel is 4.9.337 — pre-binderfs.  The standard isolated-
# binderfs sandbox isn't available.  Instead we exploit the
# CONFIG_ANDROID_BINDER_DEVICES="binder,hwbinder,vndbinder" kernel config:
# each driver name in that list creates a separate /dev/<name> character
# device with its own context manager.  We claim the /dev/vndbinder context
# by:
#   * stopping the system's vndservicemanager (ctl.stop is reversible)
#   * starting our musl-cross-compiled servicemanager pinned to /dev/vndbinder
#   * running the test against /dev/vndbinder
#   * killing our SM and restarting vndservicemanager
#
# The system's main /dev/binder is never touched, so AOSP services on the
# device continue running normally.  /dev/hwbinder is also untouched.
#
# Required: root (Magisk su), SELinux permissive (the phone is).
#
# Usage:  bash sandbox-boot.sh test     run smoke test, cleanup
#         bash sandbox-boot.sh start    leave SM running for manual probing
#         bash sandbox-boot.sh stop     teardown a leftover SM
# ============================================================================

set -u

DIR=/data/local/tmp/westlake
SM_BIN=$DIR/bin/servicemanager
SM_LIB=$DIR/lib/libbinder.so
LD_PATH=$DIR/lib
LOADER=$DIR/lib/ld-musl-aarch64.so.1
SMOKE=$DIR/bin/sm_smoke
DEV=/dev/vndbinder
PIDFILE=$DIR/sm.pid
LOGFILE=$DIR/sm.log

# CR7 (2026-05-12): source shared boot helpers (synchronous vndservicemanager
# stop). Looks beside this script in the dev tree, then on-device under
# bin-bionic/, then bin/. Inline fallback if none are present.
_self_dir="$(cd "$(dirname "$0")" 2>/dev/null && pwd)"
if [ -n "${_self_dir:-}" ] && [ -f "$_self_dir/lib-boot.sh" ]; then
    # shellcheck disable=SC1091
    . "$_self_dir/lib-boot.sh"
elif [ -f "$DIR/bin-bionic/lib-boot.sh" ]; then
    # shellcheck disable=SC1091
    . "$DIR/bin-bionic/lib-boot.sh"
elif [ -f "$DIR/bin/lib-boot.sh" ]; then
    # shellcheck disable=SC1091
    . "$DIR/bin/lib-boot.sh"
else
    echo "[sandbox] WARN: lib-boot.sh missing; using inline polling fallback" >&2
    wait_for_vndservicemanager_dead() {
        _timeout="${1:-15}"; _iters=$(( _timeout * 2 )); _i=0
        while [ "$_i" -lt "$_iters" ]; do
            [ -z "$(pidof vndservicemanager 2>/dev/null)" ] && return 0
            sleep 0.5; _i=$(( _i + 1 ))
        done
        [ -z "$(pidof vndservicemanager 2>/dev/null)" ] && return 0
        return 1
    }
    stop_vndservicemanager_synchronously() {
        _timeout="${1:-15}"
        setprop ctl.stop vndservicemanager
        wait_for_vndservicemanager_dead "$_timeout" && return 0
        _lp=$(pidof vndservicemanager 2>/dev/null)
        echo "[sandbox] WARN: vndservicemanager still alive after ${_timeout}s of ctl.stop; SIGKILL pid(s)=$_lp" >&2
        [ -n "$_lp" ] && kill -9 $_lp 2>/dev/null || true
        wait_for_vndservicemanager_dead 2 && return 0
        return 1
    }
fi

start_sm() {
    if [ ! -x "$SM_BIN" ]; then
        echo "ERROR: $SM_BIN not present — push servicemanager binary first."
        exit 1
    fi
    if [ ! -e "$DEV" ]; then
        echo "ERROR: $DEV not present on this device."
        exit 1
    fi

    # 1. stop the device's vndservicemanager so the binder context is free.
    #
    # CR7 (2026-05-12): `setprop ctl.stop` is async; on busy phones (esp.
    # back-to-back regression runs) init can take >1s to actually reap
    # vndservicemanager. The old flat-sleep+pidof check raced and left the
    # daemon alive, causing our SM's BINDER_SET_CONTEXT_MGR to return EBUSY.
    # Now we poll for death up to 15s, then SIGKILL as a last resort.
    echo "[sandbox] stop vndservicemanager (synchronous wait up to 15s)"
    if ! stop_vndservicemanager_synchronously 15; then
        echo "ERROR: vndservicemanager refused to stop (and SIGKILL fallback failed)"
        exit 1
    fi

    # 2. launch our SM bound to /dev/vndbinder.
    #
    # CRITICAL: the kernel binder driver in OnePlus 6's 4.9.337 kernel locks
    # /dev/vndbinder's context-manager UID to whoever first claimed it (1000,
    # AID_SYSTEM = vndservicemanager).  Even after vndservicemanager dies the
    # check sticks: BINDER_SET_CONTEXT_MGR will return -EPERM for any UID
    # other than 1000.  Drop to AID_SYSTEM via `su 1000 -c` before starting.
    echo "[sandbox] starting westlake servicemanager on $DEV as uid=1000"
    su 1000 -c "LD_LIBRARY_PATH=$LD_PATH $LOADER $SM_BIN $DEV" >$LOGFILE 2>&1 &
    SM_PID=$!
    echo $SM_PID > $PIDFILE
    sleep 1

    if ! kill -0 $SM_PID 2>/dev/null; then
        echo "ERROR: servicemanager died on startup — log:"
        cat $LOGFILE
        setprop ctl.start vndservicemanager
        exit 1
    fi
    echo "[sandbox] servicemanager pid=$SM_PID running"
}

stop_sm() {
    if [ -f "$PIDFILE" ]; then
        SM_PID=$(cat $PIDFILE)
        echo "[sandbox] killing su wrapper pid=$SM_PID"
        kill -9 $SM_PID 2>/dev/null || true
        rm -f $PIDFILE
    fi
    # The `su 1000 -c` invocation runs servicemanager as a child of the su
    # wrapper.  Killing the wrapper above doesn't always kill the child;
    # find any leftover process by name and kill it.
    SM_LEFT=$(pgrep -f "westlake/bin/servicemanager")
    if [ -n "$SM_LEFT" ]; then
        echo "[sandbox] killing leftover servicemanager pid=$SM_LEFT"
        kill -9 $SM_LEFT 2>/dev/null || true
    fi
    echo "[sandbox] restart vndservicemanager"
    setprop ctl.start vndservicemanager
    sleep 1
}

run_smoke() {
    if [ ! -x "$SMOKE" ]; then
        echo "ERROR: $SMOKE not present — push sm_smoke binary first."
        return 1
    fi

    # Run the smoke test against our SM.  Doesn't strictly need uid=1000
    # for the test client side (the binder driver allows any uid to perform
    # transactions; only BINDER_SET_CONTEXT_MGR is restricted) but we use
    # 1000 anyway to match real Android client behavior.
    echo "[sandbox] === running sm_smoke as uid=1000 ==="
    su 1000 -c "LD_LIBRARY_PATH=$LD_PATH BINDER_DEVICE=$DEV $LOADER $SMOKE"
    RC=$?
    echo "[sandbox] === sm_smoke exit=$RC ==="
    return $RC
}

case "${1:-test}" in
    test)
        start_sm
        run_smoke
        RC=$?
        stop_sm
        echo "[sandbox] servicemanager log was:"
        echo "---"
        cat $LOGFILE 2>/dev/null
        echo "---"
        exit $RC
        ;;
    start)
        start_sm
        ;;
    stop)
        stop_sm
        ;;
    *)
        echo "Usage: $0 {test|start|stop}"
        exit 1
        ;;
esac
