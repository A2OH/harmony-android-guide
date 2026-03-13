/*
 * Linux capabilities stub — Zygote uses this for privilege dropping.
 * We don't run Zygote, so these are no-ops.
 */
#ifndef _SYS_CAPABILITY_H
#define _SYS_CAPABILITY_H

#include <stdint.h>

typedef struct __user_cap_header_struct {
    uint32_t version;
    int pid;
} *cap_user_header_t;

typedef struct __user_cap_data_struct {
    uint32_t effective;
    uint32_t permitted;
    uint32_t inheritable;
} *cap_user_data_t;

static inline int capget(cap_user_header_t h, cap_user_data_t d) {
    (void)h; (void)d; return 0;
}
static inline int capset(cap_user_header_t h, const cap_user_data_t d) {
    (void)h; (void)d; return 0;
}

#define _LINUX_CAPABILITY_VERSION_3 0x20080522
#define _LINUX_CAPABILITY_U32S_3   2

#endif
