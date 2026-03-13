/*
 * Process name shim.
 */
#ifndef _CUTILS_PROCESS_NAME_H
#define _CUTILS_PROCESS_NAME_H

#ifdef __cplusplus
extern "C" {
#endif

static inline const char* get_process_name(void) {
    return "dalvikvm";
}

static inline void set_process_name(const char* name) {
    (void)name;
}

#ifdef __cplusplus
}
#endif

#endif /* _CUTILS_PROCESS_NAME_H */
