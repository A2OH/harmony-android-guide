/*
 * musl libc compatibility shim for Dalvik VM on OpenHarmony
 *
 * musl differs from glibc in several ways that affect Dalvik:
 * - No __register_atfork (use pthread_atfork directly)
 * - No malloc_usable_size in some builds
 * - Different pthread_internal structures
 * - No TEMP_FAILURE_RETRY macro
 * - No __THROW attribute
 */

#ifndef DALVIK_MUSL_COMPAT_H
#define DALVIK_MUSL_COMPAT_H

#ifdef __MUSL__

#include <errno.h>
#include <string.h>

/* musl doesn't define TEMP_FAILURE_RETRY — glibc extension */
#ifndef TEMP_FAILURE_RETRY
#define TEMP_FAILURE_RETRY(expression) \
    (__extension__ ({                  \
        long int __result;             \
        do {                           \
            __result = (long int)(expression); \
        } while (__result == -1L && errno == EINTR); \
        __result;                      \
    }))
#endif

/* musl doesn't define __THROW */
#ifndef __THROW
#define __THROW
#endif

/* musl strlcpy is available (we already define HAVE_STRLCPY) */

/* musl doesn't have __register_atfork — use pthread_atfork */
#ifndef __register_atfork
#include <pthread.h>
#define __register_atfork(prepare, parent, child, dso) \
    pthread_atfork(prepare, parent, child)
#endif

/* musl may lack some GNU extensions */
#ifndef _GNU_SOURCE
/* We already define _GNU_SOURCE in CXXFLAGS, but guard it */
#define _GNU_SOURCE
#endif

#endif /* __MUSL__ */
#endif /* DALVIK_MUSL_COMPAT_H */
