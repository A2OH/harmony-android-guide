// SPDX-License-Identifier: Apache-2.0
//
// Westlake M3 — sm_registrar
//
// Register a service and stay alive in joinThreadPool forever.
// Used by m3-dalvikvm-boot.sh to keep "westlake.test.echo" registered
// while HelloBinder runs.
//
// Distinct from sm_smoke (which exits after verifying round-trip).
//
// Env:
//   BINDER_DEVICE   binder driver (default /dev/vndbinder)
//   SM_SERVICE_NAME service to register (default "westlake.test.echo")

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <binder/Binder.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/ProcessState.h>
#include <utils/String8.h>
#include <utils/String16.h>

using namespace android;

int main(int argc, char** argv) {
    (void)argc; (void)argv;
    const char* dev = getenv("BINDER_DEVICE");
    if (dev == nullptr || *dev == 0) dev = "/dev/vndbinder";
    const char* name = getenv("SM_SERVICE_NAME");
    if (name == nullptr || *name == 0) name = "westlake.test.echo";

    fprintf(stderr, "[sm_registrar pid=%d] opening %s, will register \"%s\"\n",
            getpid(), dev, name);

    sp<ProcessState> ps = ProcessState::initWithDriver(dev);
    if (ps == nullptr) {
        fprintf(stderr, "[sm_registrar] FAIL: initWithDriver(%s) -> null\n", dev);
        return 1;
    }
    ps->startThreadPool();

    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        fprintf(stderr, "[sm_registrar] FAIL: defaultServiceManager() -> null\n");
        return 1;
    }

    sp<IBinder> token = sp<BBinder>::make();
    String16 name16(name);
    status_t st = sm->addService(name16, token, false /*allowIsolated*/,
                                 IServiceManager::DUMP_FLAG_PRIORITY_DEFAULT);
    fprintf(stderr, "[sm_registrar] addService(\"%s\") -> status=%d (%s)\n",
            name, st, st == 0 ? "ok" : "FAIL");
    if (st != 0) return 1;

    fprintf(stderr, "[sm_registrar] READY — joining thread pool forever\n");
    // Block here.  Caller (m3-dalvikvm-boot.sh) sends SIGKILL to terminate.
    IPCThreadState::self()->joinThreadPool(true);
    return 0; // unreached
}
