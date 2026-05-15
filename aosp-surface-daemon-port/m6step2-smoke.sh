#!/system/bin/sh
# ============================================================================
# Westlake — M6 Step 2 smoke test
#
# Builds on m6step1-smoke.sh.  After the daemon registers as "SurfaceFlinger"
# we run a transaction-level smoke (surface_smoke) that issues real
# ISurfaceComposer transactions and parses the replies.
#
# Acceptance:
#   - daemon registers successfully (addService -> 0)        [from Step 1]
#   - SurfaceFlinger appears in listServices output          [from Step 1]
#   - surface_smoke passes all 5 transaction checks (A..E)   [NEW Step 2]
#
# Usage on the phone:
#   adb push m6step2-smoke.sh /data/local/tmp/westlake/m6step2-smoke.sh
#   adb shell "su -c 'sh /data/local/tmp/westlake/m6step2-smoke.sh'"
# ============================================================================
set -u

DIR=/data/local/tmp/westlake
BIN=$DIR/bin-bionic
LIB=$DIR/lib-bionic
DEV=/dev/vndbinder

SM=$BIN/servicemanager
SF=$BIN/surfaceflinger
TXSMOKE=$BIN/surface_smoke
LISTSMOKE=$BIN/sm_smoke

SM_LOG=$DIR/m6step2-sm.log
SF_LOG=$DIR/m6step2-sf.log
LISTLOG=$DIR/m6step2-listservices.log
TXLOG=$DIR/m6step2-tx.log

log() { echo "[m6-step2] $*"; }

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
pkill -9 -f westlake/bin-bionic/surface_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/sm_smoke 2>/dev/null
sleep 1

# 2. Start our SM as uid=1000.
log "starting our servicemanager on $DEV"
rm -f "$SM_LOG"
su 1000 -c "LD_LIBRARY_PATH=$LIB $SM $DEV" >"$SM_LOG" 2>&1 &
sleep 1
if ! pgrep -f westlake/bin-bionic/servicemanager >/dev/null; then
    log "ERROR: servicemanager died.  log:"
    cat "$SM_LOG"
    setprop ctl.start vndservicemanager
    exit 1
fi
log "servicemanager up"

# 3. Start the surface daemon as uid=1000.
log "starting westlake-surface-daemon on $DEV"
rm -f "$SF_LOG"
su 1000 -c "LD_LIBRARY_PATH=$LIB $SF $DEV" >"$SF_LOG" 2>&1 &
sleep 2
if ! pgrep -f westlake/bin-bionic/surfaceflinger >/dev/null; then
    log "ERROR: surfaceflinger died.  log:"
    cat "$SF_LOG"
    pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
    setprop ctl.start vndservicemanager
    exit 1
fi
log "surface daemon up"
log "--- surface daemon log so far ---"
cat "$SF_LOG"
log "---"

# 4. Step 1 regression: SurfaceFlinger must appear in listServices.
if [ -x "$LISTSMOKE" ]; then
    log "running sm_smoke (Step-1 regression: listServices)"
    rm -f "$LISTLOG"
    SM_TEST_NAME=m6step2.listcheck su 1000 -c \
        "LD_LIBRARY_PATH=$LIB BINDER_DEVICE=$DEV $LISTSMOKE" \
        >"$LISTLOG" 2>&1
    LIST_EXIT=$?
    if [ "$LIST_EXIT" -eq 0 ] && grep -Eq "^[[:space:]]+- SurfaceFlinger[[:space:]]*$" "$LISTLOG"; then
        log "PASS: SurfaceFlinger appears in listServices (Step-1 regression OK)"
    else
        log "FAIL: SurfaceFlinger regression — sm_smoke exit=$LIST_EXIT"
        cat "$LISTLOG"
    fi
else
    log "WARN: $LISTSMOKE not present, skipping Step-1 regression"
fi

# 5. Step 2 acceptance: run surface_smoke (transaction round-trip).
log "running surface_smoke (Step-2 transaction acceptance)"
rm -f "$TXLOG"
su 1000 -c "LD_LIBRARY_PATH=$LIB BINDER_DEVICE=$DEV $TXSMOKE" >"$TXLOG" 2>&1
TX_EXIT=$?
log "surface_smoke exit=$TX_EXIT"
log "--- surface_smoke log ---"
cat "$TXLOG"
log "---"

log "--- daemon log (post-test) ---"
cat "$SF_LOG"
log "---"

if [ "$TX_EXIT" -eq 0 ]; then
    log "PASS: all 5 transaction checks passed"
    RESULT=0
else
    log "FAIL: surface_smoke exit=$TX_EXIT (see log above)"
    RESULT=1
fi

# 6. Tear down.
log "tearing down"
pkill -9 -f westlake/bin-bionic/surface_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/surfaceflinger 2>/dev/null
pkill -9 -f westlake/bin-bionic/sm_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
sleep 1
setprop ctl.start vndservicemanager
sleep 2

log "done; result=$RESULT"
exit "$RESULT"
