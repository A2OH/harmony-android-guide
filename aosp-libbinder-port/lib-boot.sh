#!/system/bin/sh
# ============================================================================
# lib-boot.sh — shared boot helpers for Westlake binder-pivot test scripts.
#
# Sourced by m3-dalvikvm-boot.sh and sandbox-boot.sh.
#
# Provides:
#   wait_for_vndservicemanager_dead [timeout_s]
#       After `setprop ctl.stop vndservicemanager`, poll `pidof vndservicemanager`
#       until empty (or timeout). Returns 0 if the daemon is gone, 1 otherwise.
#       Default timeout: 15 seconds.
#
#   stop_vndservicemanager_synchronously [timeout_s]
#       1. setprop ctl.stop vndservicemanager
#       2. wait_for_vndservicemanager_dead $timeout_s
#       3. If still alive, escalate with `kill -9 $(pidof vndservicemanager)`
#          and re-poll for 2s.
#       Returns 0 if the daemon is gone, 1 otherwise.
#
# Background — CR7 (2026-05-12, ychen@futurewei.com):
#   `setprop ctl.stop X` returns immediately; init handles the stop async.
#   m3-dalvikvm-boot.sh and sandbox-boot.sh historically waited a flat `sleep 1`
#   and then bailed out if `pidof vndservicemanager` was still alive ("ERROR:
#   vndservicemanager refused to stop"). On a busy phone — especially during
#   D2's binder-pivot-regression.sh which rapidly cycles 13 tests — 1s is not
#   enough for init to actually reap vndservicemanager. The previous test's
#   `setprop ctl.start vndservicemanager` and the next test's
#   `setprop ctl.stop vndservicemanager` can also collide with init's
#   rate-limiter (which throttles too-rapid svc cycles). The net effect is
#   that ~7/8 M4 tests fail with `BINDER_SET_CONTEXT_MGR` returning EBUSY
#   because the device's vndservicemanager is still holding /dev/vndbinder's
#   context-manager slot when our SM tries to claim it.
#
#   The fix here: poll for daemon death, not blind sleep.
#
# Anti-patterns rejected:
#   - Unbounded waits (cap at the configured timeout with a clear failure)
#   - Per-app branches (these helpers are app-agnostic)
#   - Modifying noice-discover.sh's working logic (it runs ONCE per session
#     and doesn't trip the rate-limiter — we leave it alone)
# ============================================================================

# wait_for_vndservicemanager_dead [timeout_s]
#
# Polls every 0.5s for up to $timeout_s seconds. Returns 0 if the daemon has
# died, 1 if it's still alive at timeout.
wait_for_vndservicemanager_dead() {
    _timeout="${1:-15}"
    # 0.5s per iteration -> 2 iterations per second.
    _iters=$(( _timeout * 2 ))
    _i=0
    while [ "$_i" -lt "$_iters" ]; do
        if [ -z "$(pidof vndservicemanager 2>/dev/null)" ]; then
            return 0
        fi
        sleep 0.5
        _i=$(( _i + 1 ))
    done
    if [ -z "$(pidof vndservicemanager 2>/dev/null)" ]; then
        return 0
    fi
    return 1
}

# stop_vndservicemanager_synchronously [timeout_s]
#
# Issues `setprop ctl.stop vndservicemanager` and waits for it to actually die.
# If init doesn't kill it within $timeout_s (default 15), escalates with a
# direct SIGKILL on the leftover pid. Returns 0 if the daemon is gone, 1 if
# even the SIGKILL couldn't take it down (very unlikely).
stop_vndservicemanager_synchronously() {
    _timeout="${1:-15}"
    setprop ctl.stop vndservicemanager
    if wait_for_vndservicemanager_dead "$_timeout"; then
        return 0
    fi
    # init didn't deliver; possibly throttled by its rate-limiter after a
    # fresh start/stop. Escalate.
    _leftover_pids=$(pidof vndservicemanager 2>/dev/null)
    echo "[lib-boot] WARN: vndservicemanager still alive after ${_timeout}s of ctl.stop; SIGKILL pid(s)=${_leftover_pids}" >&2
    if [ -n "$_leftover_pids" ]; then
        # shellcheck disable=SC2086
        kill -9 $_leftover_pids 2>/dev/null || true
    fi
    # After SIGKILL, give 2s for the pid to clear (init re-spawn shouldn't
    # happen because ctl.stop disabled the service first).
    if wait_for_vndservicemanager_dead 2; then
        return 0
    fi
    return 1
}
