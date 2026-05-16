/*
 * open_memstream shim — available in glibc, may need stub on other libcs.
 */
#ifndef _CUTILS_OPEN_MEMSTREAM_H
#define _CUTILS_OPEN_MEMSTREAM_H

#include <stdio.h>

/* open_memstream is POSIX.1-2008, available on glibc and musl */
/* No shim needed on Linux — just include stdio.h */

#endif /* _CUTILS_OPEN_MEMSTREAM_H */
