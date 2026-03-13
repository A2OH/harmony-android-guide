/*
 * Minimal test: load a DEX file and run dexSwapAndVerify directly.
 * This helps isolate the crash in the dexopt child process.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <signal.h>
#include <execinfo.h>

/* Pull in DEX types */
#include "libdex/DexFile.h"

static void crash_handler(int sig) {
    void* bt[20];
    int n = backtrace(bt, 20);
    fprintf(stderr, "\n=== SIGNAL %d ===\n", sig);
    backtrace_symbols_fd(bt, n, 2);
    _exit(1);
}

int main(int argc, char* argv[]) {
    if (argc != 2) {
        fprintf(stderr, "Usage: test_swap <dex-file>\n");
        return 1;
    }

    signal(SIGSEGV, crash_handler);
    signal(SIGBUS, crash_handler);
    signal(SIGABRT, crash_handler);

    int fd = open(argv[1], O_RDONLY);
    if (fd < 0) { perror("open"); return 1; }

    struct stat st;
    fstat(fd, &st);
    fprintf(stderr, "File size: %ld\n", (long)st.st_size);

    /* Map writable copy (dexSwapAndVerify modifies in place) */
    void* map = mmap(NULL, st.st_size, PROT_READ|PROT_WRITE, MAP_PRIVATE, fd, 0);
    if (map == MAP_FAILED) { perror("mmap"); return 1; }

    fprintf(stderr, "Mapped at %p, calling dexSwapAndVerify...\n", map);
    int rc = dexSwapAndVerify((u1*)map, (int)st.st_size);
    fprintf(stderr, "dexSwapAndVerify returned %d\n", rc);

    munmap(map, st.st_size);
    return rc;
}
