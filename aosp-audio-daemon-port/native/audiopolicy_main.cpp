// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M5 audio daemon main()  (Step 1 skeleton)
//
// Mirrors the M6 sibling (`aosp-surface-daemon-port/native/surfaceflinger_main.cpp`)
// and the aosp-libbinder-port test/sm_registrar pattern:
//
//   1. Open the binder driver (default /dev/vndbinder so our M2 SM picks up
//      the registration; production deployment switches to /dev/binder
//      after CR47).
//   2. Start the libbinder thread-pool.
//   3. Construct a WestlakeAudioFlinger BBinder and call
//      defaultServiceManager()->addService("media.audio_flinger", ...).
//   4. joinThreadPool forever.
//
// In Step 1 the daemon is a transaction-acking stub.  Step 2 will replace
// onTransact with the real IAudioFlinger AIDL dispatch table and hook the
// AAudio backend that CR34 already validated.
//
// CLI:
//   audio_flinger [/dev/binder-device]   # default /dev/vndbinder
//
// Companion: docs/engine/M5_AUDIO_DAEMON_PLAN.md §3.5 process model
//             docs/engine/M5_STEP1_REPORT.md (this CR's deliverable)
//             docs/engine/CR34_M5_SPIKE_REPORT.md (AAudio backend feasibility)

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

#include "WestlakeAudioFlinger.h"

using namespace android;

static const char* kDefaultBinderDev = "/dev/vndbinder";
// AOSP-canonical service name.  framework.jar's android.media.AudioSystem
// and libaudioclient's AudioSystem::get_audio_flinger() look up exactly
// this string.  See frameworks/av/media/libaudioclient/AudioSystem.cpp
// gAudioFlingerServiceName = "media.audio_flinger".
static const char* kServiceName = "media.audio_flinger";

int main(int argc, char* argv[]) {
    const char* dev = (argc > 1 && argv[1] != nullptr && argv[1][0] != '\0')
                          ? argv[1]
                          : kDefaultBinderDev;

    fprintf(stderr,
            "[wlk-audio-daemon pid=%d] starting; binder=%s; will register as \"%s\"\n",
            getpid(), dev, kServiceName);

    // Same pattern as sm_registrar.cc / surfaceflinger_main.cpp: open binder
    // driver via initWithDriver(), then start the thread-pool BEFORE
    // addService so the SM can dispatch BR_TRANSACTIONs to us immediately.
    sp<ProcessState> ps = ProcessState::initWithDriver(dev);
    if (ps == nullptr) {
        fprintf(stderr, "[wlk-audio-daemon] FAIL: initWithDriver(%s) returned null\n", dev);
        return 1;
    }
    ps->startThreadPool();

    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        fprintf(stderr, "[wlk-audio-daemon] FAIL: defaultServiceManager() returned null\n");
        return 1;
    }
    fprintf(stderr, "[wlk-audio-daemon] defaultServiceManager() OK\n");

    sp<IBinder> svc = sp<WestlakeAudioFlinger>::make();
    String16 name16(kServiceName);

    status_t st = sm->addService(name16, svc, false /*allowIsolated*/,
                                 IServiceManager::DUMP_FLAG_PRIORITY_DEFAULT);
    if (st != NO_ERROR) {
        fprintf(stderr,
                "[wlk-audio-daemon] FAIL: addService(\"%s\") -> %d\n",
                kServiceName, st);
        return 1;
    }
    fprintf(stderr,
            "[wlk-audio-daemon] addService(\"%s\") OK; entering joinThreadPool\n",
            kServiceName);

    // Block forever — same as sm_registrar.cc / surfaceflinger_main.cpp.
    // Lifecycle: caller (sandbox-boot.sh / westlake-launch.sh) sends SIGKILL
    // to terminate.  Step 2's real implementation may add periodic backend
    // heartbeats; Step 1 just blocks.
    IPCThreadState::self()->joinThreadPool(true);
    fprintf(stderr, "[wlk-audio-daemon] joinThreadPool returned (unexpected); exit\n");
    return 0;
}
