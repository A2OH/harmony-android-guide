// sync_wait_compat.c
// Real implementation of sync_wait() using standard POSIX poll().
// This is identical to AOSP's system/core/libsync/sync.c approach.
// OH also has OH_NativeFence_Wait() but sync_wait's contract is
// poll-on-fd which works on any valid fence fd regardless of OS.
#include <poll.h>
#include <errno.h>

int sync_wait(int fd, int timeout) {
    struct pollfd fds;
    int ret;
    if (fd < 0) { errno = EINVAL; return -1; }
    fds.fd = fd;
    fds.events = POLLIN;
    do {
        ret = poll(&fds, 1, timeout);
        if (ret > 0) {
            if (fds.revents & (POLLERR | POLLNVAL)) { errno = EINVAL; return -1; }
            return 0;
        }
        if (ret == 0) { errno = ETIME; return -1; }
    } while (ret == -1 && (errno == EINTR || errno == EAGAIN));
    return -1;
}
