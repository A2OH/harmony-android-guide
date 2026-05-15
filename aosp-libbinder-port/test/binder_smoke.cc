// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M1 libbinder.so smoke test
//
// Connects to the running servicemanager via /dev/binder, lists every service
// it has registered, and prints the names.
//
// Build:   make smoke   (see Makefile)
// Run on phone:
//   adb push out/libbinder.so /data/local/tmp/westlake/lib/
//   adb push out/test/binder_smoke /data/local/tmp/westlake/bin/
//   adb push <musl loader>  /data/local/tmp/westlake/lib/ld-musl-aarch64.so.1
//   adb shell "cd /data/local/tmp/westlake && \
//             LD_LIBRARY_PATH=/data/local/tmp/westlake/lib \
//             /data/local/tmp/westlake/lib/ld-musl-aarch64.so.1 \
//             /data/local/tmp/westlake/bin/binder_smoke"
//
// Expected success criteria:
//   * defaultServiceManager() returns non-null (proves /dev/binder open,
//     mmap, BC_REGISTER_LOOPER, BINDER_SET_CONTEXT_MGR all worked)
//   * getContextObject() returns non-null (proves the handle 0 protocol
//     transaction worked)
//   * raw transact to handle 0 returns status==OK (kernel binder IPC fully
//     functional through reply read)
//
// Cross-version note:  This binary is built against Android 16 AIDL.  When
// run against an older Android servicemanager (pre-16), the AIDL transaction
// codes and parcel layouts may differ enough that sm->listServices() returns
// an empty list even when the underlying kernel binder works fine.  Look at
// the raw transact output below to confirm protocol health.
//
// Optional env vars:
//   BINDER_DEVICE   path to binder driver (default /dev/binder).  Set to
//                   /dev/binder.westlake to talk to our own binderfs mount.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>

#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include <binder/IBinder.h>
#include <binder/Parcel.h>
#include <binder/ProcessState.h>
#include <utils/String8.h>
#include <utils/String16.h>
#include <utils/Vector.h>

using namespace android;

int main(int argc, char** argv) {
    (void)argc;
    (void)argv;

    // Allow tester to point at an alternate binder device.
    const char* dev = getenv("BINDER_DEVICE");
    if (dev != nullptr && dev[0] != '\0') {
        fprintf(stderr, "[binder_smoke] using BINDER_DEVICE=%s\n", dev);
        ProcessState::initWithDriver(dev);
    } else {
        fprintf(stderr, "[binder_smoke] using default driver (/dev/binder)\n");
    }

    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        fprintf(stderr, "[binder_smoke] FAIL: defaultServiceManager() returned nullptr\n");
        return 1;
    }
    fprintf(stderr, "[binder_smoke] defaultServiceManager() OK\n");

    Vector<String16> names = sm->listServices(IServiceManager::DUMP_FLAG_PRIORITY_ALL);
    fprintf(stderr, "[binder_smoke] sm->listServices() returned %zu names\n", names.size());

    for (size_t i = 0; i < names.size(); ++i) {
        printf("%s\n", String8(names[i]).c_str());
    }

    // Stretch: try to actually fetch the "activity" service proxy.
    sp<IBinder> b = sm->checkService(String16("activity"));
    if (b != nullptr) {
        fprintf(stderr, "[binder_smoke] sm->checkService(\"activity\") proxy=%p\n", b.get());
    } else {
        fprintf(stderr, "[binder_smoke] sm->checkService(\"activity\") returned nullptr\n");
    }

    // ──────────────────────────────────────────────────────────────────────
    // Low-level binder protocol probe.
    //
    // The high-level sm->listServices()/checkService() calls go through
    // Android 16's AIDL wire format.  When this binary runs against an older
    // (Android 11/12/13/14/15) servicemanager the AIDL transaction codes
    // and parcel layouts may not match, producing empty results without
    // an explicit protocol failure.
    //
    // To prove the kernel-level binder protocol (open/mmap/ioctl/transact
    // chain) is actually working, transact directly against context handle 0
    // with the legacy CHECK_SERVICE_TRANSACTION code (== 2) that has been
    // stable across all Android versions.
    // ──────────────────────────────────────────────────────────────────────
    sp<IBinder> ctxObject = ProcessState::self()->getContextObject(nullptr);
    if (ctxObject == nullptr) {
        fprintf(stderr, "[binder_smoke] FAIL: getContextObject() returned nullptr\n");
        return 1;
    }
    fprintf(stderr, "[binder_smoke] getContextObject() proxy=%p\n", ctxObject.get());

    // Phone's servicemanager AIDL transaction codes vary by Android version.
    // We probe both Android 11/12 codes (checkService=2, listServices=4) and
    // Android 16 codes (checkService=3, listServices=6) since this binary may
    // run against either generation.
    const String16 kSmDescriptor("android.os.IServiceManager");

    auto raw_check = [&](uint32_t code, const char* svcName) -> bool {
        Parcel data, reply;
        data.writeInterfaceToken(kSmDescriptor);
        data.writeUtf8AsUtf16(std::string(svcName));
        status_t st = ctxObject->transact(code, data, &reply);
        bool kernel_ok = (st == 0);
        bool obj_returned = (reply.objectsCount() > 0);
        fprintf(stderr, "[binder_smoke]   code=%u: transact=%d reply_size=%zu obj_count=%zu (%s)\n",
                code, st, reply.dataSize(), reply.objectsCount(),
                (kernel_ok && obj_returned) ? "kernel binder OK" :
                (kernel_ok ? "transact OK but no obj" : "transact failed"));
        return kernel_ok && obj_returned;
    };

    int kernel_protocol_ok = 0;
    fprintf(stderr, "[binder_smoke] probing kernel binder protocol via raw transact...\n");
    for (uint32_t code : {IBinder::FIRST_CALL_TRANSACTION + 1u,  // A11 checkService
                          IBinder::FIRST_CALL_TRANSACTION + 2u,  // A16 checkService
                          IBinder::FIRST_CALL_TRANSACTION + 3u}) {  // A16 checkService2
        if (raw_check(code, "activity")) kernel_protocol_ok++;
    }

    if (kernel_protocol_ok > 0) {
        fprintf(stderr, "[binder_smoke] PASS: kernel binder IPC verified — phone's "
                       "servicemanager returned %d binder object(s) over /dev/binder.\n",
                kernel_protocol_ok);
        fprintf(stderr, "[binder_smoke] (note: AIDL parcel deserialization above will fail on "
                       "Android-version mismatch — that is expected since this binary is built "
                       "against Android-16 AIDL and the host SM may be older.)\n");
        return 0;
    }

    fprintf(stderr, "[binder_smoke] FAIL: no transaction returned a binder object\n");
    return 1;
}
