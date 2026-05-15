// SPDX-License-Identifier: Apache-2.0
//
// Westlake — M2 servicemanager round-trip smoke test
//
// Sandbox protocol (run on the OnePlus 6, kernel 4.9, pre-binderfs):
//   1. caller (sandbox-boot.sh) has already done:
//        setprop ctl.stop vndservicemanager
//        ./servicemanager /dev/vndbinder &   (background, our SM owns ctx)
//   2. this test:
//        forks a child that opens /dev/vndbinder, calls
//        sm->addService("westlake.test.echo", new BBinder()), then
//        joinThreadPool so the binder driver can dispatch incoming
//        BR_TRANSACTIONs to our object;
//        in the parent: opens /dev/vndbinder, lists services (expects to
//        see "westlake.test.echo" + "manager"), calls getService for the
//        echo service, verifies the returned proxy is non-null and is a
//        remote BpBinder (handle != 0).
//   3. parent kills the child PID and returns 0/1.
//
// All errors are visible — we never swallow.
//
// Env:
//   BINDER_DEVICE   binder driver to use (default /dev/vndbinder)
//   SM_TEST_NAME    service name (default "westlake.test.echo")

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>

#include <binder/Binder.h>
#include <binder/BpBinder.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/ProcessState.h>
#include <utils/String8.h>
#include <utils/String16.h>
#include <utils/Vector.h>

using namespace android;

static const char* dev_for_run() {
    const char* d = getenv("BINDER_DEVICE");
    return (d && *d) ? d : "/dev/vndbinder";
}

static String16 service_name_for_run() {
    const char* n = getenv("SM_TEST_NAME");
    return String16((n && *n) ? n : "westlake.test.echo");
}

// Child: register "westlake.test.echo" and become a thread-pool worker so
// the SM can route incoming transactions.
//
// Note: we do NOT call defaultServiceManager() from the parent process and
// then fork — that would share an unsafe IPCThreadState across the fork.
// Instead the child does its own initWithDriver and joinThreadPool from
// scratch.
static int child_main(int parent_ready_fd) {
    const char* dev = dev_for_run();
    fprintf(stderr, "[sm_smoke/child pid=%d] opening %s\n", getpid(), dev);

    sp<ProcessState> ps = ProcessState::initWithDriver(dev);
    if (ps == nullptr) {
        fprintf(stderr, "[sm_smoke/child] FAIL: initWithDriver(%s) returned null\n", dev);
        return 1;
    }
    ps->startThreadPool();

    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        fprintf(stderr, "[sm_smoke/child] FAIL: defaultServiceManager() null\n");
        return 1;
    }

    sp<IBinder> token = sp<BBinder>::make();
    String16 name = service_name_for_run();
    status_t st = sm->addService(name, token, false /*allowIsolated*/,
                                 IServiceManager::DUMP_FLAG_PRIORITY_DEFAULT);
    fprintf(stderr, "[sm_smoke/child] addService(\"%s\") -> status=%d (%s)\n",
            String8(name).c_str(), st, st == 0 ? "ok" : "FAIL");
    if (st != 0) {
        return 1;
    }

    // Tell parent we're ready, then wait for incoming transactions forever.
    char ready = 'r';
    if (write(parent_ready_fd, &ready, 1) != 1) {
        fprintf(stderr, "[sm_smoke/child] WARN: write ready byte failed: %s\n",
                strerror(errno));
    }
    close(parent_ready_fd);

    fprintf(stderr, "[sm_smoke/child] joining thread pool — will be killed by parent\n");
    IPCThreadState::self()->joinThreadPool(true);
    return 0; // unreached
}

static int parent_main(pid_t child_pid, int child_ready_fd) {
    // Wait for child's "ready" byte (with a 5-second timeout via poll).
    fprintf(stderr, "[sm_smoke/parent] waiting for child ready signal...\n");
    fd_set rfds;
    FD_ZERO(&rfds);
    FD_SET(child_ready_fd, &rfds);
    struct timeval tv = {5, 0};
    int sret = select(child_ready_fd + 1, &rfds, nullptr, nullptr, &tv);
    if (sret <= 0) {
        fprintf(stderr, "[sm_smoke/parent] FAIL: child did not signal ready within 5s\n");
        kill(child_pid, SIGKILL);
        waitpid(child_pid, nullptr, 0);
        return 1;
    }
    char dummy;
    (void)read(child_ready_fd, &dummy, 1);
    close(child_ready_fd);

    const char* dev = dev_for_run();
    fprintf(stderr, "[sm_smoke/parent pid=%d] opening %s\n", getpid(), dev);

    sp<ProcessState> ps = ProcessState::initWithDriver(dev);
    if (ps == nullptr) {
        fprintf(stderr, "[sm_smoke/parent] FAIL: initWithDriver(%s) returned null\n", dev);
        kill(child_pid, SIGKILL);
        return 1;
    }

    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        fprintf(stderr, "[sm_smoke/parent] FAIL: defaultServiceManager() null\n");
        kill(child_pid, SIGKILL);
        return 1;
    }
    fprintf(stderr, "[sm_smoke/parent] defaultServiceManager() OK\n");

    String16 name = service_name_for_run();

    // 1. listServices — child's service must appear.
    Vector<String16> names = sm->listServices(IServiceManager::DUMP_FLAG_PRIORITY_ALL);
    fprintf(stderr, "[sm_smoke/parent] listServices() returned %zu names:\n", names.size());
    bool found_in_list = false;
    for (size_t i = 0; i < names.size(); ++i) {
        String8 n8(names[i]);
        fprintf(stderr, "    - %s\n", n8.c_str());
        if (names[i] == name) found_in_list = true;
    }
    if (!found_in_list) {
        fprintf(stderr, "[sm_smoke/parent] FAIL: \"%s\" not in listServices output\n",
                String8(name).c_str());
        kill(child_pid, SIGKILL);
        waitpid(child_pid, nullptr, 0);
        return 1;
    }
    fprintf(stderr, "[sm_smoke/parent] listServices(): found %s — ok\n",
            String8(name).c_str());

    // 2. checkService — proxy must be non-null and must be remote.
    // (checkService instead of getService because the child has already
    // registered, so we don't need the lazy-start polling that getService
    // performs.)
    sp<IBinder> proxy = sm->checkService(name);
    if (proxy == nullptr) {
        fprintf(stderr, "[sm_smoke/parent] FAIL: checkService(\"%s\") returned null\n",
                String8(name).c_str());
        kill(child_pid, SIGKILL);
        waitpid(child_pid, nullptr, 0);
        return 1;
    }
    BpBinder* bp = proxy->remoteBinder();
    if (bp == nullptr) {
        fprintf(stderr, "[sm_smoke/parent] FAIL: checkService(\"%s\") returned non-remote "
                       "binder (likely a local stub fluke)\n", String8(name).c_str());
        kill(child_pid, SIGKILL);
        waitpid(child_pid, nullptr, 0);
        return 1;
    }
    fprintf(stderr,
            "[sm_smoke/parent] checkService(\"%s\"): non-null binder matches "
            "(remote BpBinder at %p)\n",
            String8(name).c_str(), bp);

    // 3. addService from parent — register an extra name to prove a fresh
    // addService works through our SM.
    String16 echo2(String8(name) + String8(".parent"));
    sp<IBinder> tok2 = sp<BBinder>::make();
    status_t st = sm->addService(echo2, tok2, false /*allowIsolated*/,
                                 IServiceManager::DUMP_FLAG_PRIORITY_DEFAULT);
    fprintf(stderr, "[sm_smoke/parent] addService(\"%s\") -> status=%d (%s)\n",
            String8(echo2).c_str(), st, st == 0 ? "ok" : "FAIL");
    if (st != 0) {
        kill(child_pid, SIGKILL);
        waitpid(child_pid, nullptr, 0);
        return 1;
    }

    // 4. Clean up — kill the child.
    fprintf(stderr, "[sm_smoke/parent] PASS: all checks ok. Reaping child %d\n",
            child_pid);
    kill(child_pid, SIGKILL);
    waitpid(child_pid, nullptr, 0);
    return 0;
}

int main(int argc, char** argv) {
    (void)argc;
    (void)argv;

    int fds[2];
    if (pipe(fds) != 0) {
        fprintf(stderr, "[sm_smoke] FAIL: pipe() %s\n", strerror(errno));
        return 1;
    }

    pid_t pid = fork();
    if (pid < 0) {
        fprintf(stderr, "[sm_smoke] FAIL: fork() %s\n", strerror(errno));
        return 1;
    }
    if (pid == 0) {
        close(fds[0]);
        return child_main(fds[1]);
    }
    close(fds[1]);
    return parent_main(pid, fds[0]);
}
