/*
 * Android filesystem utils shim.
 */
#ifndef _CUTILS_FS_H
#define _CUTILS_FS_H

#include <sys/stat.h>
#include <sys/types.h>
#include <errno.h>
#include <string.h>

#ifdef __cplusplus
extern "C" {
#endif

static inline int fs_prepare_dir(const char* path, mode_t mode, uid_t uid, gid_t gid) {
    (void)uid; (void)gid;
    if (mkdir(path, mode) != 0 && errno != EEXIST) {
        return -1;
    }
    return 0;
}

#ifdef __cplusplus
}
#endif

#endif /* _CUTILS_FS_H */
