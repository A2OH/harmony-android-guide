// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M6 surface daemon main()  (Step 1 skeleton)
//
// Mirrors aosp-libbinder-port/native pattern (servicemanager binary +
// sm_registrar test):
//   1. Open the binder driver (default /dev/vndbinder so our M2 SM picks
//      up the registration; production deployment switches to /dev/binder
//      after CR47).
//   2. Start the libbinder thread-pool.
//   3. Construct a WestlakeSurfaceComposer BBinder and call
//      defaultServiceManager()->addService("SurfaceFlinger", ...).
//   4. joinThreadPool forever.
//
// CLI:
//   westlake-surface-daemon [/dev/binder-device]   # default /dev/vndbinder
//
// Companion: docs/engine/M6_SURFACE_DAEMON_PLAN.md §1 "What M6 delivers"
//             docs/engine/M6_STEP1_REPORT.md (this CR's deliverable)

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <binder/Binder.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/ProcessState.h>
#include <utils/String16.h>
#include <utils/String8.h>

#include "WestlakeSurfaceComposer.h"

using namespace android;

static const char* kDefaultBinderDev = "/dev/vndbinder";
// AOSP-canonical service name.  framework.jar's android.view.SurfaceControl
// and libgui look up exactly this string in their global init code.
static const char* kServiceName = "SurfaceFlinger";
// M6-Step4: default DLST output pipe (created by the launcher as a FIFO
// via mkfifo before starting the daemon).  Overridable via env var.
static const char* kDefaultDlstPipe = "/data/local/tmp/westlake/dlst.fifo";

int main(int argc, char* argv[]) {
    const char* dev = (argc > 1 && argv[1] != nullptr && argv[1][0] != '\0')
                          ? argv[1]
                          : kDefaultBinderDev;

    fprintf(stderr,
            "[wlk-surface-daemon pid=%d] starting; binder=%s; will register as \"%s\"\n",
            getpid(), dev, kServiceName);

    // Same pattern as aosp-libbinder-port test/sm_registrar.cc: open binder
    // driver via initWithDriver(), then start the thread-pool BEFORE
    // addService so the SM can dispatch BR_TRANSACTIONs to us immediately.
    sp<ProcessState> ps = ProcessState::initWithDriver(dev);
    if (ps == nullptr) {
        fprintf(stderr, "[wlk-surface-daemon] FAIL: initWithDriver(%s) returned null\n", dev);
        return 1;
    }
    ps->startThreadPool();

    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        fprintf(stderr, "[wlk-surface-daemon] FAIL: defaultServiceManager() returned null\n");
        return 1;
    }
    fprintf(stderr, "[wlk-surface-daemon] defaultServiceManager() OK\n");

    sp<WestlakeSurfaceComposer> svcImpl = sp<WestlakeSurfaceComposer>::make();
    // M6-Step4: resolve the DLST pipe path once at startup.  Empty string
    // disables consumer-thread spawn (Step-3 behavior, also useful for
    // micro-benchmarks where the pipe machinery isn't relevant).
    const char* dlstEnv = getenv("WESTLAKE_DLST_PIPE");
    const char* dlstPath = (dlstEnv != nullptr) ? dlstEnv : kDefaultDlstPipe;
    svcImpl->setDlstPipePath(dlstPath);
    fprintf(stderr,
            "[wlk-surface-daemon] DLST pipe path: \"%s\" (env=%s)\n",
            dlstPath, dlstEnv ? "WESTLAKE_DLST_PIPE" : "default");
    sp<IBinder> svc = svcImpl;
    String16 name16(kServiceName);

    status_t st = sm->addService(name16, svc, false /*allowIsolated*/,
                                 IServiceManager::DUMP_FLAG_PRIORITY_DEFAULT);
    if (st != NO_ERROR) {
        fprintf(stderr,
                "[wlk-surface-daemon] FAIL: addService(\"%s\") -> %d\n",
                kServiceName, st);
        return 1;
    }
    fprintf(stderr,
            "[wlk-surface-daemon] addService(\"%s\") OK; entering joinThreadPool\n",
            kServiceName);

    // Block forever — same as sm_registrar.cc.  Real lifecycle is "kill
    // this PID when shutting Westlake down."  Step 2's real implementation
    // may set up a periodic heartbeat; for Step 1 we just block.
    IPCThreadState::self()->joinThreadPool(true);
    fprintf(stderr, "[wlk-surface-daemon] joinThreadPool returned (unexpected); exit\n");
    return 0;
}
