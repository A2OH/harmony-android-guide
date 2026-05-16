/*
 * SELinux stubs — Zygote needs these but we don't use Zygote.
 */
#ifndef SELINUX_ANDROID_H
#define SELINUX_ANDROID_H

static inline int selinux_android_setcontext(unsigned int uid, int isSystemServer,
    const char* seinfo, const char* name) {
    (void)uid; (void)isSystemServer; (void)seinfo; (void)name;
    return 0;
}

#endif
