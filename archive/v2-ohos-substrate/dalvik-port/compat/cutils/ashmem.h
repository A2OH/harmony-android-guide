/*
 * Ashmem (Android shared memory) shim.
 * Replace with standard POSIX shm_open / mmap.
 */
#ifndef _CUTILS_ASHMEM_H
#define _CUTILS_ASHMEM_H

#include <sys/mman.h>
#include <sys/syscall.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Create anonymous shared memory region — use memfd_create on Linux 3.17+ */
static inline int ashmem_create_region(const char* name, size_t size) {
#ifdef __linux__
    /* Try memfd_create first (Linux 3.17+) */
#ifdef __NR_memfd_create
    int fd = syscall(__NR_memfd_create, name ? name : "dalvik-ashmem", 1 /* MFD_CLOEXEC */);
#else
    int fd = syscall(385, name ? name : "dalvik-ashmem", 1 /* MFD_CLOEXEC */); /* ARM32 */
#endif
    if (fd >= 0) {
        if (ftruncate(fd, size) < 0) {
            close(fd);
            return -1;
        }
        return fd;
    }
#endif
    /* Fallback: use /dev/shm or tmpfile */
    char path[256];
    snprintf(path, sizeof(path), "/dev/shm/dalvik-%s-XXXXXX", name ? name : "anon");
    fd = mkstemp(path);
    if (fd < 0) {
        /* Last resort: tmpfile-based */
        FILE* f = tmpfile();
        if (!f) return -1;
        fd = fileno(f);
        /* Don't fclose — caller owns fd */
    } else {
        unlink(path);
    }
    if (ftruncate(fd, size) < 0) {
        close(fd);
        return -1;
    }
    return fd;
}

static inline int ashmem_set_prot_region(int fd, int prot) {
    (void)fd; (void)prot;
    return 0; /* no-op on non-Android */
}

static inline int ashmem_pin_region(int fd, size_t offset, size_t len) {
    (void)fd; (void)offset; (void)len;
    return 0; /* ASHMEM_NOT_PURGED */
}

static inline int ashmem_unpin_region(int fd, size_t offset, size_t len) {
    (void)fd; (void)offset; (void)len;
    return 0;
}

static inline int ashmem_get_size_region(int fd) {
    off_t size = lseek(fd, 0, SEEK_END);
    lseek(fd, 0, SEEK_SET);
    return (int)size;
}

#ifdef __cplusplus
}
#endif

#endif /* _CUTILS_ASHMEM_H */
