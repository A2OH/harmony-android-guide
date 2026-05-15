#!/system/bin/sh
# ============================================================================
# W2-discover -- McdDiscoverWrapper boot script
#
# Westlake CR27 (2026-05-13): second consumer of the manifest-driven
# DiscoverWrapperBase harness.  Mirrors noice-discover.sh's structure
# exactly; only the test class name and APK target differ.  All app-
# specific configuration (package name, applicationClass, mainActivityClass,
# targetSdkVersion) comes from mcd.discover.properties at runtime.
#
# Subject under discovery:
#   App:          com.mcdonalds.app  (Real McD client, Android 15)
#   APK on phone: /data/local/tmp/westlake/com_mcdonalds_app.apk
#
# Runs on the OnePlus 6 (kernel 4.9.337, Android 15 LineageOS) as root.
# Loads framework.jar + ext.jar onto -Xbootclasspath alongside the slim
# aosp-shim.dex.
#
# IMPORTANT (inherited from noice-discover.sh): services.jar is
# INTENTIONALLY EXCLUDED from the bootclasspath.  AOSP's app-process
# launch path only puts core + framework + ext on BCP; services.jar is
# loaded exclusively by system_server.  Putting services.jar on BCP
# shadows R8-obfuscated Guava in any app bundling its own Guava and
# causes NoSuchMethodError.  See docs/engine/M4_DISCOVERY.md §12-§13.
#
# Phases driven by the Java wrapper (DiscoverWrapperBase.java):
#   PHASE A -- probe typical system services via ServiceManager.getService
#   PHASE B -- classload McD's APK
#   PHASE C -- instantiate McDMarketApplication
#   PHASE D -- Application.attachBaseContext(WestlakeContextImpl)
#   PHASE E -- Application.onCreate()
#   PHASE F -- AOSP framework Singletons
#   PHASE G -- SplashActivity launch driver
#
# Layout on the phone:
#   /data/local/tmp/westlake/
#     bin-bionic/servicemanager       # M3 bionic SM
#     lib-bionic/libbinder.so         # M3 bionic libbinder
#     dex/McdDiscoverWrapper.dex      # W2 test (this script's target)
#     com_mcdonalds_app.apk           # subject of discovery
#     mcd.discover.properties         # per-app manifest (CR27)
#     aosp-shim.dex                   # shim/java/android/os/ServiceManager etc.
#     framework.jar ext.jar           # real AOSP framework on BCP
#     dalvikvm, core-oj.jar, core-libart.jar, core-icu4j.jar, bouncycastle.jar
#
# Usage:
#   bash mcd-discover.sh
# ============================================================================

set -u

DIR=/data/local/tmp/westlake
SM_BIN=$DIR/bin-bionic/servicemanager
SM_LIB=$DIR/lib-bionic
TEST_CLASS=McdDiscoverWrapper
DEX=$DIR/dex/${TEST_CLASS}.dex
DALVIKVM=$DIR/dalvikvm
DEV=/dev/vndbinder
MANIFEST=$DIR/mcd.discover.properties

SM_PIDFILE=$DIR/mcd-discover-sm.pid
SM_LOG=$DIR/mcd-discover-sm.log
DALVIK_LOG=$DIR/mcd-discover.log

log() { echo "[mcd-discover] $*"; }

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
    require "$MANIFEST"

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
    BCP="$DIR/core-oj.jar:$DIR/core-libart.jar:$DIR/core-icu4j.jar:$DIR/bouncycastle.jar"
    BCP="$BCP:$DIR/aosp-shim.dex"
    BCP="$BCP:$DIR/framework.jar:$DIR/ext.jar"
    CP="$DEX"
    log "BCP=$BCP"
    log "CP=$CP"
    log "MANIFEST=$MANIFEST"
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
