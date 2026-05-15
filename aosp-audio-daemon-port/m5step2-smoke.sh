#!/system/bin/sh
# ============================================================================
# Westlake — M5 Step 2 smoke test
#
# Builds on m5step1-smoke.sh.  After audio_flinger registers as
# "media.audio_flinger" we run a transaction-level smoke (audio_smoke) that
# issues real IAudioFlinger / IAudioTrack transactions, parses the replies,
# and (check F) pushes a 1 s 440 Hz sine to AAudio — the device speaker
# should produce an audible tone.
#
# Acceptance:
#   - daemon registers successfully (addService -> 0)            [Step 1]
#   - media.audio_flinger appears in listServices output         [Step 1]
#   - audio_smoke passes all 7 transaction checks (A..G)         [Step 2]
#
# Usage on the phone:
#   adb push m5step2-smoke.sh /data/local/tmp/westlake/m5step2-smoke.sh
#   adb shell "su -c 'sh /data/local/tmp/westlake/m5step2-smoke.sh'"
# ============================================================================
set -u

DIR=/data/local/tmp/westlake
BIN=$DIR/bin-bionic
LIB=$DIR/lib-bionic
DEV=/dev/vndbinder

SM=$BIN/servicemanager
AF=$BIN/audio_flinger
TXSMOKE=$BIN/audio_smoke
LISTSMOKE=$BIN/sm_smoke

SM_LOG=$DIR/m5step2-sm.log
AF_LOG=$DIR/m5step2-af.log
LISTLOG=$DIR/m5step2-listservices.log
TXLOG=$DIR/m5step2-tx.log

log() { echo "[m5-step2] $*"; }

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
pkill -9 -f westlake/bin-bionic/audio_flinger 2>/dev/null
pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
pkill -9 -f westlake/bin-bionic/audio_smoke 2>/dev/null
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

# 3. Start the audio daemon as uid=1000.  CR34 spike confirmed AAudio
# accepts opens from this context (uid=1000 / u:r:shell:s0 or untrusted_app).
log "starting westlake-audio-daemon on $DEV"
rm -f "$AF_LOG"
su 1000 -c "LD_LIBRARY_PATH=$LIB $AF $DEV" >"$AF_LOG" 2>&1 &
sleep 2
if ! pgrep -f westlake/bin-bionic/audio_flinger >/dev/null; then
    log "ERROR: audio_flinger died.  log:"
    cat "$AF_LOG"
    pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
    setprop ctl.start vndservicemanager
    exit 1
fi
log "audio daemon up"
log "--- audio daemon log so far ---"
cat "$AF_LOG"
log "---"

# 4. Step 1 regression: media.audio_flinger must appear in listServices.
if [ -x "$LISTSMOKE" ]; then
    log "running sm_smoke (Step-1 regression: listServices)"
    rm -f "$LISTLOG"
    SM_TEST_NAME=m5step2.listcheck su 1000 -c \
        "LD_LIBRARY_PATH=$LIB BINDER_DEVICE=$DEV $LISTSMOKE" \
        >"$LISTLOG" 2>&1
    LIST_EXIT=$?
    if [ "$LIST_EXIT" -eq 0 ] && grep -Eq "^[[:space:]]+- media\.audio_flinger[[:space:]]*$" "$LISTLOG"; then
        log "PASS: media.audio_flinger appears in listServices (Step-1 regression OK)"
    else
        log "FAIL: media.audio_flinger regression — sm_smoke exit=$LIST_EXIT"
        cat "$LISTLOG"
    fi
else
    log "WARN: $LISTSMOKE not present, skipping Step-1 regression"
fi

# 5. Step 2 acceptance: run audio_smoke.  Check F is the audible-tone path.
log "running audio_smoke (Step-2 transaction acceptance)"
rm -f "$TXLOG"
su 1000 -c "LD_LIBRARY_PATH=$LIB BINDER_DEVICE=$DEV $TXSMOKE" >"$TXLOG" 2>&1
TX_EXIT=$?
log "audio_smoke exit=$TX_EXIT"
log "--- audio_smoke log ---"
cat "$TXLOG"
log "---"

log "--- daemon log (post-test) ---"
cat "$AF_LOG"
log "---"

if [ "$TX_EXIT" -eq 0 ]; then
    log "PASS: all 7 transaction checks passed"
    RESULT=0
else
    log "FAIL: audio_smoke exit=$TX_EXIT (see log above)"
    RESULT=1
fi

# 6. Tear down.
log "tearing down"
pkill -9 -f westlake/bin-bionic/audio_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/audio_flinger 2>/dev/null
pkill -9 -f westlake/bin-bionic/sm_smoke 2>/dev/null
pkill -9 -f westlake/bin-bionic/servicemanager 2>/dev/null
sleep 1
setprop ctl.start vndservicemanager
sleep 2

log "done; result=$RESULT"
exit "$RESULT"
