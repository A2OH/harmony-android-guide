#!/system/bin/sh
# ============================================================================
# W2-discover — NoiceDiscoverWrapper boot script
#
# Runs on the OnePlus 6 (kernel 4.9.337, Android 15 LineageOS) as root.
# Loads framework.jar + ext.jar onto -Xbootclasspath alongside the slim
# aosp-shim.dex, then drives NoiceDiscoverWrapper.main() which probes ~70
# system services, attempts to classload noice's classes, instantiate
# NoiceApplication, and invoke onCreate().
#
# IMPORTANT (M4-PRE2, 2026-05-12): services.jar is INTENTIONALLY EXCLUDED
# from the bootclasspath. AOSP's real app-process launch path (zygote ->
# app_process) only puts core + framework + ext on BCP; services.jar is
# loaded exclusively by system_server. Putting services.jar on BCP here
# caused a classpath collision between AOSP's stock Guava (inside
# services.jar!classes3.dex) and noice's R8-obfuscated bundled Guava,
# manifesting as `NoSuchMethodError: ImmutableMap.h(Lz6/b;)`. See
# docs/engine/M4_DISCOVERY.md §12-§13.
#
# Phases driven by the Java wrapper (NoiceDiscoverWrapper.java):
#   PHASE A — probe typical system services via ServiceManager.getService
#   PHASE B — classload noice's APK
#   PHASE C — instantiate NoiceApplication
#   PHASE D — Application.attach()
#   PHASE E — Application.onCreate()  (Hilt wakes up here)
#
# All output goes to stdout/stderr and is captured into:
#   /data/local/tmp/westlake/noice-discover.log
#
# Layout on the phone:
#   /data/local/tmp/westlake/
#     bin-bionic/servicemanager       # M3 bionic SM
#     bin-bionic/sm_registrar         # (NOT used in W2-discover; reserved)
#     lib-bionic/libbinder.so         # M3 bionic libbinder
#     dex/NoiceDiscoverWrapper.dex    # W2 test
#     com_github_ashutoshgngwr_noice.apk  # subject of discovery
#     aosp-shim.dex                   # shim/java/android/os/ServiceManager etc.
#     framework.jar ext.jar           # real AOSP framework on BCP
#     (services.jar exists in $DIR but is NOT on BCP — see banner above)
#     dalvikvm, core-oj.jar, core-libart.jar, core-icu4j.jar, bouncycastle.jar
#
# Usage:
#   bash noice-discover.sh
# ============================================================================

set -u

DIR=/data/local/tmp/westlake
SM_BIN=$DIR/bin-bionic/servicemanager
SM_LIB=$DIR/lib-bionic
TEST_CLASS=NoiceDiscoverWrapper
DEX=$DIR/dex/${TEST_CLASS}.dex
DALVIKVM=$DIR/dalvikvm
DEV=/dev/vndbinder

SM_PIDFILE=$DIR/noice-discover-sm.pid
SM_LOG=$DIR/noice-discover-sm.log
DALVIK_LOG=$DIR/noice-discover.log

log() { echo "[noice-discover] $*"; }

require() {
    if [ ! -e "$1" ]; then log "ERROR: $1 missing"; exit 1; fi
}

start_sm() {
    require "$SM_BIN"
    require "$DEX"
    require "$DALVIKVM"
    require "$DEV"
    require "$DIR/framework.jar"
    require "$DIR/aosp-shim.dex"

    log "stopping device vndservicemanager"
    setprop ctl.stop vndservicemanager
    sleep 1
    if pidof vndservicemanager >/dev/null 2>&1; then
        log "ERROR: vndservicemanager refused to stop"
        exit 1
    fi
    log "starting W2-discover servicemanager (bionic) on $DEV as uid=1000"
    su 1000 -c "LD_LIBRARY_PATH=$SM_LIB $SM_BIN $DEV" > "$SM_LOG" 2>&1 &
    echo $! > "$SM_PIDFILE"
    sleep 1
    if ! pgrep -f "westlake/bin-bionic/servicemanager" >/dev/null; then
        log "ERROR: servicemanager died on startup. log:"
        cat "$SM_LOG"
        setprop ctl.start vndservicemanager
        rm -f "$SM_PIDFILE"
        exit 1
    fi
    log "servicemanager up; log: $SM_LOG"
}

run_wrapper() {
    # BCP includes framework.jar + ext.jar plus aosp-shim.dex (PF-arch-053
    # resolution lets us put the slim shim on BCP since it no longer
    # duplicates framework classes).
    #
    # services.jar is INTENTIONALLY OMITTED — see banner at top of file.
    # AOSP only loads services.jar in system_server; app processes do NOT
    # see it on BCP. Including it here shadows noice's R8-obfuscated Guava
    # with stock Guava and produces NoSuchMethodError on ImmutableMap.h().
    BCP="$DIR/core-oj.jar:$DIR/core-libart.jar:$DIR/core-icu4j.jar:$DIR/bouncycastle.jar"
    BCP="$BCP:$DIR/aosp-shim.dex"
    BCP="$BCP:$DIR/framework.jar:$DIR/ext.jar"
    CP="$DEX"
    log "BCP=$BCP"
    log "CP=$CP"
    log "running dalvikvm $TEST_CLASS"
    su 1000 -c "
        cd $DIR
        BINDER_DEVICE=$DEV \
        LD_LIBRARY_PATH=$SM_LIB \
        $DALVIKVM \
            -Xbootclasspath:$BCP \
            -Xverify:none \
            -Xnorelocate \
            -Djava.library.path=$SM_LIB \
            -cp $CP \
            $TEST_CLASS
    " > "$DALVIK_LOG" 2>&1
    RC=$?
    log "dalvikvm exit code: $RC"
    return $RC
}

stop_all() {
    if [ -f "$SM_PIDFILE" ]; then
        kill -9 "$(cat $SM_PIDFILE 2>/dev/null)" 2>/dev/null || true
        rm -f "$SM_PIDFILE"
    fi
    pkill -9 -f "westlake/bin-bionic/servicemanager" 2>/dev/null || true
    log "restart device vndservicemanager"
    setprop ctl.start vndservicemanager
    sleep 1
}

start_sm
run_wrapper
RC=$?
log "============================== END OF RUN =============================="
log "dalvikvm log tail (200 lines):"
echo "----- begin dalvikvm log -----"
tail -200 "$DALVIK_LOG"
echo "----- end dalvikvm log -----"
log "servicemanager log:"
echo "----- begin sm log -----"
cat "$SM_LOG" 2>/dev/null
echo "----- end sm log -----"
stop_all
exit $RC
