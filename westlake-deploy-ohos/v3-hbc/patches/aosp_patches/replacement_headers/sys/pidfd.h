/*
 * aosp_patches/replacement_headers/sys/pidfd.h
 *
 * Minimal real replacement for bionic's <sys/pidfd.h> — OH musl sysroot
 * does not ship this header. Declares pidfd_open() as a direct syscall
 * wrapper using SYS_pidfd_open (434 on all Linux arches).
 *
 * NOT a stub — this is a real syscall invocation, same as the libc wrapper.
 * The kernel provides the functionality; we just provide the userspace
 * prototype + inline wrapper.
 */
#pragma once

#include <fcntl.h>
#include <sys/syscall.h>
#include <unistd.h>

#ifndef SYS_pidfd_open
#define SYS_pidfd_open 434
#endif

#ifdef __cplusplus
extern "C" {
#endif

static inline int pidfd_open(pid_t pid, unsigned int flags) {
    return (int)syscall(SYS_pidfd_open, pid, flags);
}

#ifdef __cplusplus
}
#endif
