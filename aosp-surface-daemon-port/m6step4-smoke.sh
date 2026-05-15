#!/system/bin/sh
# ============================================================================
# Westlake — M6 Step 4 smoke test
#
# Builds on m6step3-smoke.sh.  Same daemon + same Step-1/2/3 regression chain
# (servicemanager + surfaceflinger registration + listServices + Tier-1
# ISurfaceComposer + IGraphicBufferProducer dequeue/request/queue), then runs
# the extended surface_smoke which now includes Step-4's check G — the DLST
# pipe consumer thread fires, mmaps the producer's memfd, and writes a
# well-formed DLST frame (magic + size + OP_ARGB_BITMAP header + raw RGBA
# bytes) into the FIFO read by an in-process reader thread, who validates the
# wire shape end-to-end (magic, opcode, dims, dataLen, first-pixel pattern).
#
# Acceptance (cumulative):
#   - daemon registers successfully (addService -> 0)              [from Step 1]
#   - SurfaceFlinger appears in listServices output                [from Step 1]
#   - surface_smoke checks A..F pass                               [from Step 2/3]
#   - surface_smoke check G PASS — DLST consumer wrote a verified  [Step 4 NEW]
#     frame to the FIFO
#
# Usage on the phone (run from /data/local/tmp/westlake/):
#   adb shell "su -c 'sh /data/local/tmp/westlake/m6step4-smoke.sh'"
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

SM_LOG=$DIR/m6step4-sm.log
SF_LOG=$DIR/m6step4-sf.log
LISTLOG=$DIR/m6step4-listservices.log
TXLOG=$DIR/m6step4-tx.log

log() { echo "[m6-step4] $*"; }

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
#    race against the test's reader thread on the very first surface.  Both
#    daemon and smoke test honor WESTLAKE_DLST_PIPE if set; defaults to
#    /data/local/tmp/westlake/dlst.fifo (matches surfaceflinger_main.cpp
#    kDefaultDlstPipe).
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
    SM_TEST_NAME=m6step4.listcheck su 1000 -c \
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

# 6. Step 2 + 3 + 4 acceptance: run surface_smoke (transaction round-trips
#    A..F + new Step-4 check G — DLST consumer thread fires).
log "running surface_smoke (Step-2..4 transaction acceptance, 7 checks)"
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
    log "PASS: all 7 transaction checks passed (Step-2 A..E + Step-3 F + Step-4 G)"
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
