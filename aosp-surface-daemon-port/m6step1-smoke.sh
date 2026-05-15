#!/system/bin/sh
# ============================================================================
# Westlake — M6 Step 1 smoke test
#
# Runs entirely on the phone (push this script, then `adb shell sh
# /data/local/tmp/westlake/m6step1-smoke.sh`).  Mirrors the m3-dalvikvm-boot.sh
# pattern:
#   1. Stop the device's vndservicemanager (frees /dev/vndbinder context mgr).
#   2. Start our bionic-linked servicemanager on /dev/vndbinder.
#   3. Start our surfaceflinger daemon.
#   4. Start sm_smoke to listServices and verify "SurfaceFlinger" appears.
#   5. Tear down everything, restart device vndservicemanager.
#
# Acceptance:
#   - daemon registers successfully (addService -> 0)
#   - sm_smoke's listServices output contains "SurfaceFlinger"
# ============================================================================
set -u

DIR=/data/local/tmp/westlake
BIN=$DIR/bin-bionic
LIB=$DIR/lib-bionic
DEV=/dev/vndbinder

SM=$BIN/servicemanager
SF=$BIN/surfaceflinger
SMOKE=$BIN/sm_smoke

SM_LOG=$DIR/m6step1-sm.log
SF_LOG=$DIR/m6step1-sf.log
SMOKE_LOG=$DIR/m6step1-smoke.log

log() { echo "[m6-step1] $*"; }

# 1. Stop vndservicemanager.  Poll up to 15s.
log "stopping vndservicemanager"
setprop ctl.stop vndservicemanager
i=0
while [ "$i" -lt 30 ]; do
    if ! pidof vndservicemanager >/dev/null 2>&1; then break; fi
    sleep 0.5
    i=$((i+1))
done
if pidof vndservicemanager >/dev/null 2>&1; then
    log "ERROR: vndservicemanager still alive after 15s; force-kill"
    kill -9 "$(pidof vndservicemanager)" 2>/dev/null
    sleep 1
fi

# Also reap any stale westlake bin-bionic/* from a prior run.
pkill -9 -f westlake/bin-bionic/surfaceflinger 2>/dev/null
pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
pkill -9 -f westlake/bin-bionic/sm_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/sm_registrar 2>/dev/null
sleep 1

# 2. Start our SM as uid=1000.
log "starting our servicemanager on $DEV"
rm -f "$SM_LOG"
su 1000 -c "LD_LIBRARY_PATH=$LIB $SM $DEV" >"$SM_LOG" 2>&1 &
SM_PID=$!
sleep 1
if ! pgrep -f westlake/bin-bionic/servicemanager >/dev/null; then
    log "ERROR: servicemanager died.  log:"
    cat "$SM_LOG"
    setprop ctl.start vndservicemanager
    exit 1
fi
log "servicemanager up (pid via shell=$SM_PID)"

# 3. Start the surface daemon as uid=1000.
log "starting westlake-surface-daemon on $DEV"
rm -f "$SF_LOG"
su 1000 -c "LD_LIBRARY_PATH=$LIB $SF $DEV" >"$SF_LOG" 2>&1 &
SF_PID=$!
sleep 2
if ! pgrep -f westlake/bin-bionic/surfaceflinger >/dev/null; then
    log "ERROR: surfaceflinger died.  log:"
    cat "$SF_LOG"
    pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
    setprop ctl.start vndservicemanager
    exit 1
fi
log "surface daemon up (pid via shell=$SF_PID)"
log "--- surface daemon log so far ---"
cat "$SF_LOG"
log "---"

# 4. Run sm_smoke; its listServices should now include "SurfaceFlinger".
# Use a one-shot registration with a custom name so the listServices output
# includes BOTH the surface service AND sm_smoke's own.
log "running sm_smoke (verifies listServices)"
rm -f "$SMOKE_LOG"
SM_TEST_NAME=m6step1.smoke su 1000 -c "LD_LIBRARY_PATH=$LIB BINDER_DEVICE=$DEV $SMOKE" >"$SMOKE_LOG" 2>&1
SMOKE_EXIT=$?
log "sm_smoke exit=$SMOKE_EXIT"
log "--- sm_smoke log ---"
cat "$SMOKE_LOG"
log "---"

# Acceptance: SurfaceFlinger present in listServices.
if grep -Eq "^[[:space:]]+- SurfaceFlinger[[:space:]]*$" "$SMOKE_LOG"; then
    log "PASS: SurfaceFlinger appears in listServices output"
    RESULT=0
else
    log "FAIL: SurfaceFlinger NOT found in listServices output"
    RESULT=1
fi

# 5. Tear down.
log "tearing down"
pkill -9 -f westlake/bin-bionic/surfaceflinger 2>/dev/null
pkill -9 -f westlake/bin-bionic/sm_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
sleep 1
setprop ctl.start vndservicemanager
sleep 2

log "done; result=$RESULT"
exit "$RESULT"
