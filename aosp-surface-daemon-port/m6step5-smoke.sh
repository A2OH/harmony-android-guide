#!/system/bin/sh
# ============================================================================
# Westlake — M6 Step 5 smoke test
#
# Builds on m6step4-smoke.sh.  Same sandbox protocol (own SM + surfaceflinger
# on /dev/vndbinder), same FIFO + DLST-pipe machinery, but the extended
# surface_smoke now runs check H: IDisplayEventConnection round-trip.
#
# Check H (NEW for Step 5):
#   1. CREATE_DISPLAY_EVENT_CONNECTION on the SurfaceFlinger binder.
#   2. STEAL_RECEIVE_CHANNEL on the connection — get the BitTube
#      (SOCK_SEQPACKET socketpair) receive fd.
#   3. SET_VSYNC_RATE(1) — subscribe to every tick.
#   4. Read 3 vsync events, validate AOSP-11 DisplayEventReceiver::Event
#      wire layout (magic 'vsyn' = 0x7673796E, displayId=0, monotonic
#      vsyncCount), AND inter-event spacing ~16.7 ms (8–35 ms tolerance for
#      a ~60Hz cadence).
#   5. SET_VSYNC_RATE(0) — pause emission.
#
# Acceptance (cumulative):
#   - daemon registers successfully (addService -> 0)              [Step 1]
#   - SurfaceFlinger appears in listServices                        [Step 1]
#   - surface_smoke checks A..F pass                                [Step 2/3]
#   - surface_smoke check G PASS (DLST pipe consumer verified)      [Step 4]
#   - surface_smoke check H PASS (IDisplayEventConnection + 60 Hz)  [Step 5 NEW]
#
# Usage on the phone (run from /data/local/tmp/westlake/):
#   adb shell "su -c 'sh /data/local/tmp/westlake/m6step5-smoke.sh'"
# ============================================================================
set -u

DIR=/data/local/tmp/westlake
BIN=$DIR/bin-bionic
LIB=$DIR/lib-bionic
DEV=/dev/vndbinder
FIFO=$DIR/dlst.fifo

SM=$BIN/servicemanager
SF=$BIN/surfaceflinger
TXSMOKE=$BIN/surface_smoke
LISTSMOKE=$BIN/sm_smoke

SM_LOG=$DIR/m6step5-sm.log
SF_LOG=$DIR/m6step5-sf.log
LISTLOG=$DIR/m6step5-listservices.log
TXLOG=$DIR/m6step5-tx.log

log() { echo "[m6-step5] $*"; }

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

# Reap stale westlake bin-bionic/* from a prior run.
pkill -9 -f westlake/bin-bionic/surfaceflinger 2>/dev/null
pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
pkill -9 -f westlake/bin-bionic/surface_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/sm_smoke 2>/dev/null
sleep 1

# 2. Pre-create the DLST FIFO so the daemon's first writer-open does not
#    race against the test's reader thread (check G).  Check H is independent
#    of the FIFO but we keep the same env-var plumbing so all 7 checks land
#    in one run.
log "ensuring FIFO at $FIFO"
rm -f "$FIFO"
mkfifo "$FIFO" || { log "ERROR: mkfifo failed"; exit 1; }
chmod 0666 "$FIFO"
ls -l "$FIFO"

# 3. Start our SM as uid=1000.
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

# 4. Start the surface daemon as uid=1000, with WESTLAKE_DLST_PIPE pointing
#    at the FIFO we just created.
log "starting westlake-surface-daemon on $DEV (WESTLAKE_DLST_PIPE=$FIFO)"
rm -f "$SF_LOG"
su 1000 -c "LD_LIBRARY_PATH=$LIB WESTLAKE_DLST_PIPE=$FIFO $SF $DEV" \
    >"$SF_LOG" 2>&1 &
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

# 5. Step 1 regression: SurfaceFlinger appears in listServices.
if [ -x "$LISTSMOKE" ]; then
    log "running sm_smoke (Step-1 regression: listServices)"
    rm -f "$LISTLOG"
    SM_TEST_NAME=m6step5.listcheck su 1000 -c \
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

# 6. Step 2 + 3 + 4 + 5 acceptance: run surface_smoke (transaction round-trips
#    A..F + Step-4 check G + new Step-5 check H — IDisplayEventConnection
#    vsync round-trip).
log "running surface_smoke (Step-2..5 transaction acceptance, 8 checks)"
rm -f "$TXLOG"
su 1000 -c \
    "LD_LIBRARY_PATH=$LIB BINDER_DEVICE=$DEV \
     WESTLAKE_DLST_PIPE=$FIFO WESTLAKE_SMOKE_CHECK_G=1 $TXSMOKE" \
    >"$TXLOG" 2>&1
TX_EXIT=$?
log "surface_smoke exit=$TX_EXIT"
log "--- surface_smoke log ---"
cat "$TXLOG"
log "---"

log "--- daemon log (post-test) ---"
cat "$SF_LOG"
log "---"

if [ "$TX_EXIT" -eq 0 ]; then
    log "PASS: all 8 transaction checks passed (Step-2 A..E + Step-3 F + Step-4 G + Step-5 H)"
    RESULT=0
else
    log "FAIL: surface_smoke exit=$TX_EXIT (see log above)"
    RESULT=1
fi

# 7. Tear down.
log "tearing down"
pkill -9 -f westlake/bin-bionic/surface_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/surfaceflinger 2>/dev/null
pkill -9 -f westlake/bin-bionic/sm_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
sleep 1
rm -f "$FIFO"
setprop ctl.start vndservicemanager
sleep 2

log "done; result=$RESULT"
exit "$RESULT"
