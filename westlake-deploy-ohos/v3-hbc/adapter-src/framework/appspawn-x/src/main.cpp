/**
 * appspawn-x main entry point.
 *
 * This is the hybrid spawner daemon that combines OH appspawn's security
 * and sandbox capabilities with Android Zygote's ART runtime preloading.
 *
 * Startup sequence:
 *   Phase 1: Initialize OH security modules (sandbox config, SELinux, AccessToken)
 *   Phase 2: Create ART VM and register JNI methods
 *   Phase 3: Preload Android framework classes and resources
 *   Phase 4: Enter event loop, accept spawn requests, fork child processes
 *
 * Each child process:
 *   - Inherits the preloaded ART VM (copy-on-write via fork)
 *   - Applies OH security restrictions (DAC, sandbox, SELinux, AccessToken)
 *   - Initializes the Android-OH adapter layer
 *   - Enters ActivityThread.main() event loop
 *
 * Usage:
 *   appspawn-x [--socket-name NAME] [--sandbox-config PATH]
 */

#include "appspawnx_runtime.h"
#include "child_main.h"
#include "spawn_server.h"

#include <cerrno>
#include <cstdlib>
#include <cstring>
#include <fcntl.h>
#include <signal.h>
#include <sys/resource.h>
#include <unistd.h>

using namespace appspawnx;

static const char* kDefaultSocketName = "AppSpawnX";
static const char* kDefaultSandboxConfig = "/system/etc/appspawn_x_sandbox.json";

// Required env values. init cfg declares these in "env", but by the time
// appspawn-x main() runs they are unset. Two independent init-side issues:
//   1. Long values (>128 B, incl. BOOTCLASSPATH/DEX2OATBOOTCLASSPATH/LD_LIBRARY_PATH):
//      OH init_service_manager.c:1023 passes srcLen+1 as strcpy_s destMax →
//      buffer overflow into the next ServiceEnv's name[] → env never reaches
//      child (see memory reference_oh_init_env_overflow.md).
//   2. Even short values (ANDROID_BOOT_IMAGE, ICU_DATA, ANDROID_ROOT, …)
//      don't arrive. Leading hypothesis: AT_SECURE env-stripping during the
//      init→appspawn SELinux domain transition (memory
//      feedback_init_service_start_layers.md, project_init_service_checkpoint.md).
//
// Bypass both via setenv() from main() before any consumer reads them:
//   - ART Runtime::Init reads BOOTCLASSPATH / ANDROID_BOOT_IMAGE
//   - ICU u_init reads ICU_DATA (called during Runtime::Start)
// B.41 (2026-04-29) note: framework.jar is no longer BUILT or DEPLOYED by
// this project (deploy/build scripts skip it).  BUT the device's existing
// /system/android/framework/framework.jar persists from prior deploys, the
// boot-framework.{art,oat,vdex} segments persist, and BCP MUST still
// reference framework.jar — most android.* classes (Activity, View, …)
// live there, and ART validates BCP against boot image segments.  Don't
// remove framework.jar from kBootClasspath unless the boot-framework.*
// segments are removed in lockstep.
static const char kBootClasspath[] =
    "/system/android/framework/core-oj.jar:"
    "/system/android/framework/core-libart.jar:"
    "/system/android/framework/core-icu4j.jar:"
    "/system/android/framework/okhttp.jar:"
    "/system/android/framework/bouncycastle.jar:"
    "/system/android/framework/apache-xml.jar:"
    // adapter-mainline-stubs.jar BEFORE framework.jar:
    //   - dex2oat's "Skipping class X from framework.jar previously found
    //     in stubs.jar" warning is BENIGN — without stubs in BCP, runtime
    //     Class.forName CANNOT find these classes.
    "/system/android/framework/adapter-mainline-stubs.jar:"
    "/system/android/framework/framework.jar:"
    "/system/android/framework/oh-adapter-framework.jar";

// Mirrors kBootClasspath order so dex2oat boot image build agrees with
// runtime ART class loader.
static const char kDex2oatBootClasspath[] =
    "/system/android/framework/core-oj.jar:"
    "/system/android/framework/core-libart.jar:"
    "/system/android/framework/core-icu4j.jar:"
    "/system/android/framework/adapter-mainline-stubs.jar:"
    "/system/android/framework/framework.jar";

static void seedRequiredEnvs() {
    // Long values (>128 B) - OH init cfg parser cannot carry these
    setenv("BOOTCLASSPATH", kBootClasspath, 1);
    setenv("DEX2OATBOOTCLASSPATH", kDex2oatBootClasspath, 1);

    // Android runtime paths - read by ART Runtime::Init / ClassLinker
    setenv("ANDROID_ROOT",        "/system/android",                       1);
    setenv("ANDROID_DATA",        "/data",                                 1);
    setenv("ANDROID_BOOT_IMAGE",  "/system/android/framework/boot.art",    1);
    setenv("ANDROID_I18N_ROOT",   "/system/android",                       1);
    setenv("ANDROID_TZDATA_ROOT", "/system/android",                       1);

    // ICU data path - read by ICU u_init() during Runtime::Start
    setenv("ICU_DATA",            "/system/android/etc/icu",               1);

    // 2026-05-01 G2.14n: -Xnojit and -Xint diagnostic envs intentionally NOT
    // seeded — both tested 2026-05-01 and crash signature unchanged, ruling out
    // JIT and AOT as corruption sources.  Set manually via env if needed.

    // 2026-05-02 G2.14r DIAGNOSTIC (path A) DISABLED: APPSPAWNX_CHECK_JNI=1
    // was found to trigger CheckSystemClass abort during boot image init —
    // legitimate boot-time JNI calls flagged as misuse by -Xcheck:jni in this
    // build of ART.  Re-enable manually (`setenv ... = 1`) when actively
    // debugging a JNI-suspect abort, but leave off for normal runs.
    // setenv("APPSPAWNX_CHECK_JNI", "1", 1);
}

// Temporary debug probe for init-service vs shell-exec discrepancy.
// LOGI/hilog may not work if hilogd SELinux rules deny; fprintf(stderr) goes
// to /dev/null under "console":0 cfg.
//
// 2026-04-22: First tried /data/local/tmp/appspawnx_dbg.log (data_local_tmp
// type — appspawn domain lacks {append}), then /data/misc/appspawnx/dbg.log
// (data_misc — also no write). Both silently failed under Enforcing.
// Final: write to /dev/kmsg, which appspawn domain is allowed per
// DZ.4.2 "dev_kmsg_file:chr_file { open write }". Read back via `dmesg`.
// The write is nonblocking, short (<128 B) and kernel-log safe.
static void dbgMark(const char* step) {
    int fd = open("/dev/kmsg", O_WRONLY | O_CLOEXEC);
    if (fd >= 0) {
        char buf[128];
        int n = snprintf(buf, sizeof(buf), "appspawn-x DBG: %s pid=%d\n", step, (int)getpid());
        if (n > 0) {
            (void)write(fd, buf, n);
        }
        close(fd);
    }
}

int main(int argc, char* argv[]) {
    dbgMark("M0_main_entry");
    // Must run before ART Runtime::Init reads BOOTCLASSPATH / ANDROID_BOOT_IMAGE
    // and before ICU u_init reads ICU_DATA.
    seedRequiredEnvs();
    dbgMark("M1_after_seedRequiredEnvs");

    // Ensure 16MB stack for ART ClassLinker::Init (DEX parsing is deeply recursive)
    struct rlimit rl;
    rl.rlim_cur = 16 * 1024 * 1024;
    rl.rlim_max = 16 * 1024 * 1024;
    setrlimit(RLIMIT_STACK, &rl);

    dbgMark("M2_before_LOGI_banner");
    LOGI("========================================");
    LOGI("appspawn-x hybrid spawner starting");
    LOGI("  pid=%d  uid=%d  gid=%d", getpid(), getuid(), getgid());
    LOGI("========================================");
    dbgMark("M3_after_LOGI_banner");

    // Parse command line arguments
    const char* socketName = kDefaultSocketName;
    const char* sandboxConfig = kDefaultSandboxConfig;
    for (int i = 1; i < argc; i++) {
        if (strcmp(argv[i], "--socket-name") == 0 && i + 1 < argc) {
            socketName = argv[++i];
        } else if (strcmp(argv[i], "--sandbox-config") == 0 && i + 1 < argc) {
            sandboxConfig = argv[++i];
        } else if (strcmp(argv[i], "--help") == 0 || strcmp(argv[i], "-h") == 0) {
            fprintf(stdout,
                    "Usage: %s [OPTIONS]\n"
                    "\n"
                    "Options:\n"
                    "  --socket-name NAME       Unix socket name (default: %s)\n"
                    "  --sandbox-config PATH    Sandbox config JSON (default: %s)\n"
                    "  --help, -h               Show this help\n",
                    argv[0], kDefaultSocketName, kDefaultSandboxConfig);
            return 0;
        } else {
            LOGW("Unknown argument: %s", argv[i]);
        }
    }

    dbgMark("M4_after_argv_parse");
    // ============ Phase 1: OH Security Init ============
    fprintf(stderr, "[MAIN] >>> Phase 1 START <<<\n"); fflush(stderr);
    LOGI("Phase 1: Initializing OH security modules...");
    SpawnServer server(socketName);
    dbgMark("M5_after_SpawnServer_ctor");

    int ret = server.initSecurity(sandboxConfig);
    dbgMark("M6_after_initSecurity");
    fprintf(stderr, "[MAIN] initSecurity ret=%d\n", ret); fflush(stderr);
    if (ret != 0) {
        LOGE("Failed to initialize security, ret=%d", ret);
        return 1;
    }

    ret = server.createListenSocket();
    dbgMark("M7_after_createListenSocket");
    fprintf(stderr, "[MAIN] createListenSocket ret=%d\n", ret); fflush(stderr);
    if (ret != 0) {
        LOGE("Failed to create listen socket, ret=%d", ret);
        return 1;
    }

    fprintf(stderr, "[MAIN] >>> Phase 1 DONE <<<\n"); fflush(stderr);
    // ============ Phase 2: Android Runtime Init ============
    fprintf(stderr, "[MAIN] >>> Phase 2 START <<<\n"); fflush(stderr);
    LOGI("Phase 2: Initializing Android Runtime (ART VM)...");
    // DEBUG: pause here to allow /proc/<pid>/maps capture before VM init crashes.
    // Enabled only when APPSPAWNX_DEBUG_SLEEP=<seconds> is set.
    const char* dbgSleep = getenv("APPSPAWNX_DEBUG_SLEEP");
    if (dbgSleep && dbgSleep[0]) {
        int sec = atoi(dbgSleep);
        fprintf(stderr, "[MAIN] DEBUG sleep %ds (pid=%d)\n", sec, getpid()); fflush(stderr);
        sleep(sec);
    }
    dbgMark("M8_before_AppSpawnXRuntime_ctor");
    AppSpawnXRuntime runtime;
    dbgMark("M9_after_AppSpawnXRuntime_ctor");

    // CMS GC mode: try running ART VM on MAIN thread directly.
    fprintf(stderr, "[MAIN] starting ART VM on MAIN thread (CMS)\n"); fflush(stderr);
    dbgMark("M10_before_startVm");
    ret = runtime.startVm();
    dbgMark("M11_after_startVm");
    if (ret == 0) {
        dbgMark("M12_before_preload");
        ret = runtime.preload();
        dbgMark("M13_after_preload");
    }
    goto phase2_done_main;
    ret = runtime.startVmAndPreloadOnDedicatedThread();  // unreachable fallback
    phase2_done_main:;
    if (ret != 0) {
        LOGE("Failed to start ART VM on worker pthread, ret=%d", ret);
        return 1;
    }
    LOGI("ART VM started + framework preloaded on worker pthread");

    // ============ Phase 4: Enter Event Loop ============
    LOGI("Phase 4: Ready to accept spawn requests");
    LOGI("Listening on /dev/unix/socket/%s", socketName);

    // Set up the fork-based spawn handler
    server.setSpawnHandler([&](const SpawnMsg& msg) -> int {
        LOGI("Spawn request: proc=%s bundle=%s uid=%d flags=0x%llx",
             msg.procName.c_str(), msg.bundleName.c_str(), msg.uid,
             static_cast<unsigned long long>(msg.flags));

        if (msg.hasFlag(StartFlags::COLD_START)) {
            LOGI("Cold start requested for %s", msg.procName.c_str());
        }

        if (msg.hasFlag(StartFlags::DEBUGGABLE)) {
            LOGI("Debuggable flag set for %s", msg.procName.c_str());
        }

        // B.33 (2026-04-29): canonical Zygote fork mediation around fork().
        // Without this, ART daemons (HeapTaskDaemon/Finalizer/Signal Catcher/
        // Runtime worker x4) are alive in parent at fork time but vanish in
        // child, leaving ART internal locks held by dead TIDs.  Child main
        // thread then blocks indefinitely in epoll_wait inside CallStatic-
        // VoidMethod (pre-Java thread attach), Java initChild never runs.
        runtime.zygotePreFork();

        pid_t pid = fork();
        if (pid < 0) {
            LOGE("fork() failed: %s", strerror(errno));
            // Best-effort: restart parent daemons even on fork failure.
            runtime.zygotePostForkCommon();
            return -1;
        }

        if (pid == 0) {
            // ---- Child process ----
            // Close the listening socket (child doesn't accept connections)
            server.closeSocket();

            // B.33 (2026-04-29): postForkChild + postForkCommon are called
            // from INSIDE ChildMain::run, AFTER OH child specialization
            // (DAC + sandbox + SELinux setcon + AccessToken).  Calling
            // them here (before specialization) breaks setcon: ART daemon
            // threads spawned by Daemons.startPostZygoteFork are alive
            // before setcon runs, making the process multi-threaded; the
            // kernel then refuses /proc/self/attr/current write with EPERM
            // (libhap_restorecon -SELINUX_SET_CONTEXT_ERROR rc=-7).
            //
            // Canonical AOSP order (Zygote.java + nativeForkAndSpecialize):
            //   fork → drop caps + setresuid + setSchedPolicy + SELinux
            //   setcon (single-threaded child) → ZygoteHooks.postForkChild
            //   (now safe to start daemons because specialization done).

            // Enter child main – this function does not return
            ChildMain::run(msg, &runtime);

            // Should never reach here
            _exit(1);
        }

        // ---- Parent process ----
        // B.33: parent also restarts its daemons (Daemons.startPostZygoteFork).
        runtime.zygotePostForkCommon();

        LOGI("Spawned child pid=%d for %s (uid=%d)",
             pid, msg.procName.c_str(), msg.uid);
        return pid;
    });

    // Enter the event loop (blocks forever)
    server.run();

    // Should never reach here
    LOGE("Event loop exited unexpectedly");
    return 1;
}
