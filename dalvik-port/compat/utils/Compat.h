/*
 * Utils/Compat.h — compatibility defines from Android's libutils.
 */
#ifndef UTILS_COMPAT_H
#define UTILS_COMPAT_H

#include <unistd.h>
#include <sys/types.h>

/* TEMP_FAILURE_RETRY — retry syscall on EINTR */
#ifndef TEMP_FAILURE_RETRY
#define TEMP_FAILURE_RETRY(exp) ({         \
    typeof(exp) _rc;                        \
    do {                                    \
        _rc = (exp);                        \
    } while (_rc == -1 && errno == EINTR);  \
    _rc; })
#endif

#endif
