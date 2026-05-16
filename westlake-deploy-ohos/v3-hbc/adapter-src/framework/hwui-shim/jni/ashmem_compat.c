// ashmem_compat.c
//
// Provides ashmem_get_size_region(), the only ashmem_* symbol that the
// current libhwui .o set references as undefined (2026-04-12 readelf
// scan across 147 .o files). Android's original lives in
// system/core/libcutils/ashmem-dev.cpp and is shipped in libcutils.so;
// the AOSP cross-compiled libcutils.so in out/aosp_lib/ does not
// include the ashmem module because OH has no /dev/ashmem driver.
//
// Android API contract:
//   int ashmem_get_size_region(int fd);
//     Returns the size of the ashmem region associated with fd, or -1
//     on error. Implemented on Android via an ashmem ioctl; OH has no
//     equivalent driver.
//
// Strategy: fall back to lseek(fd, 0, SEEK_END) to obtain the size.
//   - If fd refers to a dmabuf, memfd, or any other seekable file
//     descriptor (which is what an "ashmem-like" region looks like on
//     OH), lseek gives us the correct byte length.
//   - If fd is not seekable (pipe, socket, etc.), lseek returns -1 and
//     we propagate that failure through to the caller. In Android the
//     ioctl would also fail in that scenario, so this matches the
//     existing error contract.
//   - We save and restore the file position so the caller's subsequent
//     reads/writes see the fd exactly where it was before this call.
//
// This path is only exercised by libhwui code that receives an ashmem
// file descriptor from the Java side (e.g. JavaAshmemAllocator-backed
// Bitmap creation). Hello World never hits it; however, providing a
// reasonable real implementation instead of returning -1 unconditionally
// avoids silently failing any future code path that does.

#include <sys/types.h>
#include <unistd.h>
#include <errno.h>

int ashmem_get_size_region(int fd) {
    off_t saved = lseek(fd, 0, SEEK_CUR);
    if (saved < 0) {
        return -1;  // fd not seekable -> cannot determine size
    }
    off_t end = lseek(fd, 0, SEEK_END);
    if (end < 0) {
        int saved_errno = errno;
        lseek(fd, saved, SEEK_SET);  // best-effort restore
        errno = saved_errno;
        return -1;
    }
    lseek(fd, saved, SEEK_SET);  // restore original position
    return (int)end;
}
