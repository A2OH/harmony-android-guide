#!/system/bin/sh
# ============================================================================
# Westlake M3 — dalvikvm + libbinder + servicemanager end-to-end boot script
#
# Runs on the OnePlus 6 (kernel 4.9.337, Android 15 LineageOS) as root.
#
# Stages:
#   1. Stop the device's vndservicemanager (frees /dev/vndbinder context mgr).
#   2. Start OUR bionic-linked servicemanager on /dev/vndbinder (uid=1000
#      required — kernel locks ctx-mgr UID to first claimer's UID).
#   3. Start sm_smoke as a background "service registrar": its child process
#      addService("westlake.test.echo", BBinder) and joins the threadpool,
#      keeping the service alive for our subsequent lookup.  Parent exits.
#   4. Run dalvikvm with HelloBinder.dex.  HelloBinder.java statically loads
#      libandroid_runtime_stub.so, which dlopens libbinder.so, and then
#      calls android.os.ServiceManager.{listServices,getService}.
#   5. Tear down: kill the sm_smoke child, kill our servicemanager, restart
#      the device's vndservicemanager.
#
# Layout on the phone:
#   /data/local/tmp/westlake/
#     bin-bionic/servicemanager       # M3 bionic SM
#     bin-bionic/sm_smoke             # M3 bionic test registrar
#     lib-bionic/libbinder.so         # M3 bionic libbinder
#     lib-bionic/libandroid_runtime_stub.so  # M3 JNI bridge
#     dex/HelloBinder.dex             # M3 test
#     aosp-shim.dex                   # contains shim/java/android/os/ServiceManager
#     dalvikvm, core-oj.jar, framework.jar, ...   # ART runtime
#
# Usage: bash m3-dalvikvm-boot.sh [keep|test|stop] [--bcp-shim] [--bcp-framework]
#   test (default): run full flow, cleanup, exit 0/1
#   keep: leave SM + sm_smoke running after the test
#   stop: tear down a leftover from `keep`
#
#   --bcp-shim: place aosp-shim.dex on -Xbootclasspath (instead of -cp).
#               Resolution of PF-arch-053 (May 12 2026); historically this
#               SIGBUSed during PathClassLoader init because the old fat
#               shim (4.8 MB, 3835 classes) duplicated framework classes.
#               After scripts/framework_duplicates.txt stripping the shim
#               down to 754 classes / 1.4 MB, BCP placement works.
#   --bcp-framework: also put framework.jar/ext.jar/services.jar on BCP.
#                    Intentionally INCLUDES services.jar — this is the
#                    worst-case BCP for the PF-arch-053 SIGBUS canary test
#                    (`bcp-sigbus-repro.sh` uses this mode). Note that real
#                    M4+ discovery/launch scripts (e.g. `noice-discover.sh`)
#                    do NOT put services.jar on BCP, because services.jar
#                    is a system_server-only jar and including it in an
#                    app process triggers a classpath collision between
#                    AOSP's stock Guava and R8-obfuscated app Guava. See
#                    `docs/engine/M4_DISCOVERY.md` §13.
# ============================================================================

set -u

DIR=/data/local/tmp/westlake
SM_BIN=$DIR/bin-bionic/servicemanager
SM_LIB=$DIR/lib-bionic
SMOKE=$DIR/bin-bionic/sm_smoke
REGISTRAR=$DIR/bin-bionic/sm_registrar

# CR7 (2026-05-12): source shared boot helpers. Looks for lib-boot.sh next to
# this script first (when run from the dev tree), then falls back to the
# canonical on-device path. If neither is found, we fall back to inline polling
# logic so a stale phone deploy doesn't brick the test runner.
_self_dir="$(cd "$(dirname "$0")" 2>/dev/null && pwd)"
if [ -n "${_self_dir:-}" ] && [ -f "$_self_dir/lib-boot.sh" ]; then
    # shellcheck disable=SC1091
    . "$_self_dir/lib-boot.sh"
elif [ -f "$DIR/bin-bionic/lib-boot.sh" ]; then
    # shellcheck disable=SC1091
    . "$DIR/bin-bionic/lib-boot.sh"
else
    echo "[m3-boot] WARN: lib-boot.sh missing; using inline polling fallback" >&2
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
        echo "[m3-boot] WARN: vndservicemanager still alive after ${_timeout}s of ctl.stop; SIGKILL pid(s)=$_lp" >&2
        [ -n "$_lp" ] && kill -9 $_lp 2>/dev/null || true
        wait_for_vndservicemanager_dead 2 && return 0
        return 1
    }
fi
# M3++: test class is selectable via --test <Name>; default HelloBinder.
TEST_CLASS=HelloBinder
DEX=$DIR/dex/HelloBinder.dex
DALVIKVM=$DIR/dalvikvm
DEV=/dev/vndbinder

# Flags (default: M3 baseline — shim on -cp, no framework.jar)
BCP_SHIM=0
BCP_FRAMEWORK=0
# M4a: --bcp-framework-strict puts framework.jar + ext.jar on BCP but NOT
# services.jar (avoids the services.jar/app-Guava collision and stripped
# system_server pulls).  Useful for synthetic service tests like
# ActivityServiceTest.
BCP_FRAMEWORK_STRICT=0
# M3++: skip starting sm_registrar (AsInterfaceTest doesn't need westlake.test.echo)
SKIP_REGISTRAR=0

SM_PIDFILE=$DIR/m3-sm.pid
SMOKE_PIDFILE=$DIR/m3-smoke.pid
SM_LOG=$DIR/m3-sm.log
SMOKE_LOG=$DIR/m3-smoke.log
DALVIK_LOG=$DIR/m3-dalvikvm.log

log() { echo "[m3-boot] $*"; }

require() {
    if [ ! -e "$1" ]; then log "ERROR: $1 missing"; exit 1; fi
}

start_sm() {
    require "$SM_BIN"
    require "$SMOKE"
    require "$DEX"
    require "$DALVIKVM"
    require "$DEV"

    # CR7 (2026-05-12): `setprop ctl.stop` is async; on busy phones (esp.
    # back-to-back regression runs) init can take >1s to actually reap
    # vndservicemanager. The old flat-sleep+pidof check raced and left the
    # daemon alive, causing our SM's BINDER_SET_CONTEXT_MGR to return EBUSY.
    # Now we poll for death up to 15s, then SIGKILL as a last resort.
    log "stopping device vndservicemanager (synchronous wait up to 15s)"
    if ! stop_vndservicemanager_synchronously 15; then
        log "ERROR: vndservicemanager refused to stop (and SIGKILL fallback failed)"
        exit 1
    fi

    # Start our SM as uid=1000.  See sandbox-boot.sh M2 notes for why.
    log "starting M3 servicemanager (bionic) on $DEV as uid=1000"
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

start_smoke_registrar() {
    # sm_smoke (as built for M2) forks a child that registers
    # "westlake.test.echo" and joinThreadPools.  Parent process verifies
    # the registration succeeded, then kills the child and exits 0.
    # For M3 we don't want the parent to kill the child — we want the
    # registration to persist for HelloBinder to look up.
    #
    # Trick: run sm_smoke in the background.  The parent will finish its
    # listServices + checkService round-trip and then issue SIGKILL to the
    # child (per sm_smoke.cc), which would tear down our service.  To
    # avoid this we run sm_smoke twice:
    #   (a) once for round-trip verification (the M2 sandbox-boot.sh way),
    #   (b) once as a long-lived registrar in the background with a trick:
    #       fork a separate process that does just addService + threadpool,
    #       no parent-side kill.
    #
    # Pragmatic implementation: spawn `sm_smoke` in the background.  Its
    # PARENT process is going to exit (after success), but only after
    # SIGKILLing the child.  So the service goes away.  Workaround: spawn
    # the smoke and then quickly preempt its parent's SIGKILL.  Way easier:
    # write a tiny helper.  But for M3 we can use sm_smoke in a wrapper
    # that runs only the registration part (BINDER_REGISTRAR_ONLY env).
    # sm_smoke doesn't support that flag — instead, run sm_smoke and let
    # it complete, but PRESERVE the child via a separate spawn.
    #
    # Simplest M3 path: spawn a *second* sm_smoke before sm_smoke's parent
    # gets a chance to kill its own child.  Then HelloBinder looks up
    # "westlake.test.echo" — whichever child is alive provides it.
    log "spawning sm_smoke (registers westlake.test.echo, joinThreadPool)"
    su 1000 -c "LD_LIBRARY_PATH=$SM_LIB BINDER_DEVICE=$DEV $SMOKE" > "$SMOKE_LOG" 2>&1 &
    echo $! > "$SMOKE_PIDFILE"
    sleep 2
    if ! pgrep -f "westlake/bin-bionic/sm_smoke" >/dev/null; then
        log "WARN: sm_smoke not visible (may have finished). log tail:"
        tail -5 "$SMOKE_LOG" 2>/dev/null
    fi
    log "sm_smoke log: $SMOKE_LOG"
}

register_echo() {
    # sm_registrar registers westlake.test.echo and stays alive in
    # joinThreadPool until SIGKILL'd by us during teardown.  Unlike
    # sm_smoke (which exits after a round-trip and tears down the
    # registration), sm_registrar keeps the binder alive for HelloBinder
    # to look up.
    log "starting sm_registrar (long-lived westlake.test.echo registrar)"
    require "$REGISTRAR"
    su 1000 -c "LD_LIBRARY_PATH=$SM_LIB BINDER_DEVICE=$DEV $REGISTRAR" > "$SMOKE_LOG" 2>&1 &
    echo $! > "$SMOKE_PIDFILE"
    # Wait until the registrar logs "READY" before letting the test proceed.
    for i in 1 2 3 4 5; do
        if grep -q "READY" "$SMOKE_LOG" 2>/dev/null; then
            log "sm_registrar ready"
            return 0
        fi
        sleep 1
    done
    log "WARN: sm_registrar did not log READY within 5s — proceeding anyway"
    return 0
}

run_dalvikvm() {
    log "running dalvikvm ${TEST_CLASS}.dex (bcp_shim=$BCP_SHIM bcp_framework=$BCP_FRAMEWORK bcp_framework_strict=$BCP_FRAMEWORK_STRICT)"
    # PF-arch-053 (May 12 2026): the SIGBUS-on-BCP issue is resolved.
    # The old fat shim (4.8 MB, 3835 classes) duplicated framework classes
    # and corrupted early-clinit when placed on -Xbootclasspath.  Today's
    # slim shim (1.4 MB, 754 classes) — produced by
    # scripts/framework_duplicates.txt stripping in scripts/build-shim-dex.sh
    # — boots cleanly with or without BCP placement.
    #
    # Default mode (no flags): aosp-shim.dex on -cp.  Same as M3 baseline.
    # --bcp-shim: aosp-shim.dex on -Xbootclasspath.  Required for M4+ so
    #     framework.jar's references to shim classes resolve via BCP.
    # --bcp-framework: framework.jar + ext.jar + services.jar also on BCP.
    #     This is the *worst-case BCP* for the PF-arch-053 SIGBUS canary
    #     (HelloBinder doesn't touch Guava, so the services.jar/app-Guava
    #     collision doesn't trigger here). M4+ discovery scripts use a
    #     stricter BCP — see `test/noice-discover.sh` — that drops
    #     services.jar.
    BCP="$DIR/core-oj.jar:$DIR/core-libart.jar:$DIR/core-icu4j.jar:$DIR/bouncycastle.jar"
    CP="$DEX"
    if [ "$BCP_SHIM" = "1" ]; then
        BCP="$BCP:$DIR/aosp-shim.dex"
    else
        CP="$DIR/aosp-shim.dex:$CP"
    fi
    if [ "$BCP_FRAMEWORK" = "1" ]; then
        BCP="$BCP:$DIR/framework.jar:$DIR/ext.jar:$DIR/services.jar"
    elif [ "$BCP_FRAMEWORK_STRICT" = "1" ]; then
        BCP="$BCP:$DIR/framework.jar:$DIR/ext.jar"
    fi
    log "  BCP=$BCP"
    log "  CP=$CP"
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
    log "dalvikvm log (last 80 lines):"
    echo "----- dalvikvm log -----"
    tail -80 "$DALVIK_LOG"
    echo "----- end dalvikvm log -----"
    return $RC
}

stop_all() {
    if [ -f "$SMOKE_PIDFILE" ]; then
        kill -9 "$(cat $SMOKE_PIDFILE 2>/dev/null)" 2>/dev/null || true
        rm -f "$SMOKE_PIDFILE"
    fi
    pkill -9 -f "westlake/bin-bionic/sm_registrar" 2>/dev/null || true
    pkill -9 -f "westlake/bin-bionic/sm_smoke" 2>/dev/null || true
    if [ -f "$SM_PIDFILE" ]; then
        kill -9 "$(cat $SM_PIDFILE 2>/dev/null)" 2>/dev/null || true
        rm -f "$SM_PIDFILE"
    fi
    pkill -9 -f "westlake/bin-bionic/servicemanager" 2>/dev/null || true
    log "restart device vndservicemanager"
    setprop ctl.start vndservicemanager
    sleep 1
    if ! pidof vndservicemanager >/dev/null 2>&1; then
        log "WARN: vndservicemanager did not restart"
    fi
}

# Parse optional flags after the mode keyword.
MODE="${1:-test}"
shift || true
while [ "$#" -gt 0 ]; do
    case "$1" in
        --bcp-shim) BCP_SHIM=1 ;;
        --bcp-framework) BCP_FRAMEWORK=1 ;;
        --bcp-framework-strict) BCP_FRAMEWORK_STRICT=1 ;;
        --test) TEST_CLASS="$2"; DEX="$DIR/dex/${TEST_CLASS}.dex"; SKIP_REGISTRAR=1; shift ;;
        --no-registrar) SKIP_REGISTRAR=1 ;;
        *) log "unknown flag: $1"; exit 1 ;;
    esac
    shift
done

case "$MODE" in
    test)
        start_sm
        if [ "$SKIP_REGISTRAR" = "0" ]; then
            register_echo
        else
            log "skipping sm_registrar (--test or --no-registrar set)"
        fi
        run_dalvikvm
        RC=$?
        log "servicemanager log was:"
        echo "----- sm log -----"
        cat "$SM_LOG" 2>/dev/null
        echo "----- end sm log -----"
        log "smoke registrar log was:"
        echo "----- smoke log -----"
        cat "$SMOKE_LOG" 2>/dev/null
        echo "----- end smoke log -----"
        stop_all
        exit $RC
        ;;
    keep)
        start_sm
        if [ "$SKIP_REGISTRAR" = "0" ]; then
            register_echo
        fi
        log "keeping running (use 'stop' to teardown)"
        ;;
    stop)
        stop_all
        ;;
    *)
        echo "Usage: $0 {test|keep|stop}"
        exit 1
        ;;
esac
