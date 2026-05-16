/*
 * test_dvm_entry_inproc.c — CR-BB W0 sub-gate 2 + 3 (agent 36, 2026-05-15).
 *
 * Tiny C harness that:
 *   1. dlopen("./libdvm_arm32.so")
 *   2. dlsym("dvm_entry")
 *   3. Calls dvm_entry(argc, argv) with the same args MVP-0 uses for the
 *      executable variant: -Xbootclasspath + class name. Verifies exit-0
 *      and that the class's main() printed the "westlake-dalvik on OHOS —
 *      main reached" marker (caller greps stdout).
 *
 * Sub-gate 3 path: if the env var DVM_HARNESS_TEST_SIGBUS_CHAIN=1 is set,
 * the harness FIRST installs a custom SIGBUS handler that records
 * "PRIOR_SIGBUS_HANDLER_FIRED" to a shared volatile flag, THEN dlopens
 * libdvm_arm32.so, THEN raises a controlled SIGBUS via mmap of an empty
 * file (read-past-EOF on shared mapping → SIGBUS on access). The harness
 * verifies the prior handler fires (it always will, because the dalvikvm
 * port's busCatcher in Init.cpp is dead code under `if (false)`). The
 * test confirms that loading libdvm_arm32.so does NOT clobber a
 * pre-existing SIGBUS handler. This is the empirical foundation for the
 * "5-line patch" claim in the spike.
 *
 * Bitness discipline: pointer-sized ints are uintptr_t; sizes are size_t.
 * No setAccessible, no Unsafe (this is C — N/A by construction).
 *
 * Build: see Makefile target `test-dvm-entry-inproc`.
 */
#define _GNU_SOURCE
#include <dlfcn.h>
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdint.h>

typedef int (*dvm_entry_fn)(int argc, const char** argv);

/* Sub-gate 3: prior SIGBUS handler installed BEFORE dlopen of
 * libdvm_arm32.so. The dalvikvm port's own busCatcher is dead-code-gated
 * (`if (false)` in /home/dspfac/dalvik-kitkat/vm/Init.cpp:1350), so this
 * handler should still be the active SIGBUS handler after dvm_entry runs.
 *
 * The test does NOT actually expect dvm_entry's normal-path execution to
 * raise SIGBUS — instead, after dvm_entry returns (or in a separate
 * sub-test), we trigger a controlled SIGBUS to verify the prior handler
 * is intact. */
static volatile sig_atomic_t g_prior_sigbus_fired = 0;
static void prior_sigbus_handler(int sig, siginfo_t* info, void* ctx) {
    (void)sig; (void)info; (void)ctx;
    g_prior_sigbus_fired = 1;
    /* MUST NOT return — SIGBUS would re-fire indefinitely on the same
     * faulting instruction. Tests print the marker and _exit. */
    write(STDOUT_FILENO,
          "[harness] PRIOR_SIGBUS_HANDLER_FIRED\n",
          sizeof("[harness] PRIOR_SIGBUS_HANDLER_FIRED\n") - 1);
    _exit(0);
}

/* Trigger a controlled SIGBUS by mmap'ing a 1-byte file as a 8192-byte
 * region (two pages), then reading the SECOND page (offset 4096+).
 * POSIX guarantees SIGBUS on access to whole pages of a shared mapping
 * that lie completely past the end of the underlying file. The 1-byte
 * file occupies only the first page (offsets 0..4095), so byte at
 * offset 4096 is in a page that's fully past EOF and should fault.
 *
 * If that doesn't trigger (kernel-dependent — Linux historically has
 * lazily extended the BO under MAP_SHARED on some FS types), we fall
 * back to ftruncate-after-mmap: shrink the file to 0 AFTER mapping, then
 * read; this is a reliable SIGBUS trigger across kernels. */
static void trigger_controlled_sigbus(void) {
    const char* path = "/data/local/tmp/sigbus_trigger.bin";
    int fd = open(path, O_RDWR | O_CREAT | O_TRUNC, 0600);
    if (fd < 0) {
        fprintf(stderr, "[harness] cannot open %s: %s\n", path, strerror(errno));
        _exit(2);
    }
    /* Write a single byte so the file isn't size-0. */
    write(fd, "X", 1);

    /* Map TWO pages so the second page (offset 4096+) lies fully past
     * the 1-byte file size. */
    void* p = mmap(NULL, 8192, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (p == MAP_FAILED) {
        fprintf(stderr, "[harness] mmap failed: %s\n", strerror(errno));
        close(fd);
        _exit(2);
    }

    /* Belt-and-braces: also truncate to 0 AFTER mapping. POSIX requires
     * SIGBUS on subsequent access to the now-orphaned mapping. */
    if (ftruncate(fd, 0) != 0) {
        fprintf(stderr, "[harness] ftruncate failed: %s\n", strerror(errno));
    } else {
        fprintf(stderr, "[harness] ftruncated mapped file to 0 bytes\n");
    }
    close(fd);

    fprintf(stderr, "[harness] triggering SIGBUS via read at offset 4096 (second page, past EOF)...\n");
    fflush(stderr);
    /* Volatile read past file size → SIGBUS. */
    volatile unsigned char* q = (volatile unsigned char*)p;
    unsigned char dummy = q[4096];
    (void)dummy;
    /* If we get here, SIGBUS was NOT delivered. */
    fprintf(stderr, "[harness] FAIL: read past EOF + ftruncate did not raise SIGBUS\n");
    _exit(3);
}

int main(int argc, char* argv[]) {
    fprintf(stderr, "[harness] argc=%d\n", argc);
    for (int i = 0; i < argc; i++) {
        fprintf(stderr, "[harness] argv[%d]=%s\n", i, argv[i]);
    }

    /* Sub-gate 3 path. */
    if (getenv("DVM_HARNESS_TEST_SIGBUS_CHAIN")) {
        fprintf(stderr,
                "[harness] sub-gate 3 mode: install prior SIGBUS handler, "
                "dlopen libdvm_arm32.so, then trigger SIGBUS\n");
        struct sigaction sa;
        memset(&sa, 0, sizeof(sa));
        sa.sa_sigaction = prior_sigbus_handler;
        sa.sa_flags = SA_SIGINFO;
        sigemptyset(&sa.sa_mask);
        if (sigaction(SIGBUS, &sa, NULL) != 0) {
            fprintf(stderr,
                    "[harness] FAIL: install prior SIGBUS handler: %s\n",
                    strerror(errno));
            return 4;
        }
        /* dlopen the lib. We DO NOT call dvm_entry — the lib's mere load
         * + ctors must not clobber the SIGBUS handler. */
        const char* sopath = (argc >= 2) ? argv[1] : "./libdvm_arm32.so";
        void* h = dlopen(sopath, RTLD_NOW | RTLD_LOCAL);
        if (!h) {
            fprintf(stderr, "[harness] dlopen(%s) failed: %s\n",
                    sopath, dlerror());
            return 5;
        }
        fprintf(stderr, "[harness] dlopen OK; verifying prior SIGBUS "
                "handler intact via re-query...\n");
        struct sigaction cur;
        memset(&cur, 0, sizeof(cur));
        if (sigaction(SIGBUS, NULL, &cur) != 0) {
            fprintf(stderr, "[harness] sigaction query failed: %s\n",
                    strerror(errno));
            return 6;
        }
        /* The function pointer of cur.sa_sigaction must equal
         * prior_sigbus_handler IF the loaded lib didn't replace it.
         * Note: with SA_SIGINFO the handler lives in sa_sigaction
         * (a sa_sigaction_t union member). */
        void* expected = (void*)prior_sigbus_handler;
        void* actual = (void*)cur.sa_sigaction;
        fprintf(stderr,
                "[harness] expected handler addr=%p, actual=%p, flags=0x%x\n",
                expected, actual, cur.sa_flags);
        if (actual != expected) {
            fprintf(stderr,
                    "[harness] FAIL: libdvm_arm32.so REPLACED the prior "
                    "SIGBUS handler at dlopen time (no chaining required "
                    "yet because dvm_entry not called — this is a hard "
                    "violation)\n");
            dlclose(h);
            return 7;
        }
        fprintf(stderr,
                "[harness] PASS: dlopen of libdvm_arm32.so did NOT "
                "clobber prior SIGBUS handler\n");
        /* Now trigger an actual SIGBUS — prior handler should fire. */
        trigger_controlled_sigbus();
        /* Unreachable. */
        return 8;
    }

    /* Sub-gate 2 path: load lib, find dvm_entry, run a class.
     * Caller passes the dalvikvm-style argv as the harness's argv (skip
     * argv[0] which is the harness binary path). */
    const char* sopath = "./libdvm_arm32.so";
    void* h = dlopen(sopath, RTLD_NOW | RTLD_LOCAL);
    if (!h) {
        fprintf(stderr, "[harness] dlopen(%s) failed: %s\n",
                sopath, dlerror());
        return 9;
    }
    fprintf(stderr, "[harness] dlopen(%s) OK at %p\n",
            sopath, (void*)h);

    dvm_entry_fn dvm_entry = (dvm_entry_fn)dlsym(h, "dvm_entry");
    if (!dvm_entry) {
        fprintf(stderr, "[harness] dlsym(dvm_entry) failed: %s\n", dlerror());
        dlclose(h);
        return 10;
    }
    fprintf(stderr, "[harness] dlsym(dvm_entry) = %p\n",
            (void*)(uintptr_t)dvm_entry);

    /* Pass our argv straight to dvm_entry. The launcher's parsing
     * expects argv[0]=program-name, argv[1..]=options/class. */
    fprintf(stderr,
            "[harness] calling dvm_entry(argc=%d, argv=...) — VM run begins\n",
            argc);
    fflush(stderr);

    int rc = dvm_entry(argc, (const char**)argv);

    fprintf(stderr, "[harness] dvm_entry returned %d\n", rc);
    /* Don't dlclose — the VM has thread state, dlclose would unmap
     * thread-local globals while threads may still be running. Just exit. */
    return rc;
}
