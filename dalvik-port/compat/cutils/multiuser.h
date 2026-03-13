/*
 * Android multiuser shim — stub.
 */
#ifndef _CUTILS_MULTIUSER_H
#define _CUTILS_MULTIUSER_H

#include <sys/types.h>

typedef uid_t userid_t;

#define MULTIUSER_APP_PER_USER_RANGE 100000

static inline uid_t multiuser_get_uid(int userId, int appId) {
    return (uid_t)(userId * MULTIUSER_APP_PER_USER_RANGE + appId);
}

static inline int multiuser_get_user_id(uid_t uid) {
    return (int)(uid / MULTIUSER_APP_PER_USER_RANGE);
}

static inline int multiuser_get_app_id(uid_t uid) {
    return (int)(uid % MULTIUSER_APP_PER_USER_RANGE);
}

#endif /* _CUTILS_MULTIUSER_H */
