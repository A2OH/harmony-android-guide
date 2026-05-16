// bionic_compat/include/android/fdsan.h
// Compatibility header: stubs for Android fdsan (file descriptor sanitizer)
#ifndef BIONIC_COMPAT_FDSAN_H
#define BIONIC_COMPAT_FDSAN_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

enum android_fdsan_error_level {
    ANDROID_FDSAN_ERROR_LEVEL_DISABLED = 0,
    ANDROID_FDSAN_ERROR_LEVEL_WARN_ONCE = 1,
    ANDROID_FDSAN_ERROR_LEVEL_WARN_ALWAYS = 2,
    ANDROID_FDSAN_ERROR_LEVEL_FATAL = 3,
};

enum android_fdsan_error_level android_fdsan_get_error_level(void);
enum android_fdsan_error_level android_fdsan_set_error_level(
    enum android_fdsan_error_level new_level);
void android_fdsan_exchange_owner_tag(int fd, uint64_t expected_tag, uint64_t new_tag);
uint64_t android_fdsan_get_owner_tag(int fd);
int android_fdsan_close_with_tag(int fd, uint64_t tag);

#ifdef __cplusplus
}
#endif

#endif // BIONIC_COMPAT_FDSAN_H
