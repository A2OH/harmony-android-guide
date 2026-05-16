/*
 * Zero-dependency init startup probe.
 * Link only against libc (musl). No DT_NEEDED to libart/libandroid_runtime.
 * If this prints via console:2 redirect, init env itself is fine.
 * If it SEGVs without output, the problem is NOT in our libs — it's init/SELinux/cap.
 */
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>

int main(int argc, char** argv) {
    const char msg[] = "[TEST_MIN] enter-main\n";
    write(2, msg, sizeof(msg) - 1);
    fprintf(stderr, "[TEST_MIN] pid=%d uid=%d gid=%d argc=%d\n",
            getpid(), getuid(), getgid(), argc);
    fflush(stderr);

    for (int i = 0; i < argc; i++) {
        fprintf(stderr, "[TEST_MIN] argv[%d]=%s\n", i, argv[i]);
    }
    fflush(stderr);

    char line[256];
    int fd = open("/proc/self/status", O_RDONLY);
    if (fd >= 0) {
        int n = read(fd, line, sizeof(line) - 1);
        if (n > 0) {
            line[n] = 0;
            fprintf(stderr, "[TEST_MIN] /proc/self/status first bytes:\n%s\n", line);
        }
        close(fd);
    }
    fflush(stderr);

    fprintf(stderr, "[TEST_MIN] exiting-normal\n");
    fflush(stderr);
    return 0;
}
