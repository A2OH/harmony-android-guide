/*
 * Android scheduler policy shim — no-op on non-Android.
 */
#ifndef _CUTILS_SCHED_POLICY_H
#define _CUTILS_SCHED_POLICY_H

#ifdef __cplusplus
extern "C" {
#endif

typedef enum {
    SP_DEFAULT = -1,
    SP_BACKGROUND = 0,
    SP_FOREGROUND = 1,
    SP_SYSTEM = 2,
    SP_AUDIO_APP = 3,
    SP_AUDIO_SYS = 4,
    SP_CNT,
    SP_MAX = SP_CNT - 1,
} SchedPolicy;

static inline int set_sched_policy(int tid, SchedPolicy policy) {
    (void)tid; (void)policy;
    return 0;
}

static inline int get_sched_policy(int tid, SchedPolicy* policy) {
    (void)tid;
    if (policy) *policy = SP_FOREGROUND;
    return 0;
}

#ifdef __cplusplus
}
#endif

#endif /* _CUTILS_SCHED_POLICY_H */
