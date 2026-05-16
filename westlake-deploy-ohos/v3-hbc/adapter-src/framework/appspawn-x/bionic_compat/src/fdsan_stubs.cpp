#include <unistd.h>
// bionic_compat/src/fdsan_stubs.cpp
// Stubs for Android fdsan (file descriptor sanitizer)
// OH does not have fdsan; all functions are no-ops

#include <android/fdsan.h>

enum android_fdsan_error_level android_fdsan_get_error_level(void) {
    return ANDROID_FDSAN_ERROR_LEVEL_DISABLED;
}

enum android_fdsan_error_level android_fdsan_set_error_level(
        enum android_fdsan_error_level new_level) {
    (void)new_level;
    return ANDROID_FDSAN_ERROR_LEVEL_DISABLED;
}

void android_fdsan_exchange_owner_tag(int fd, uint64_t expected_tag, uint64_t new_tag) {
    (void)fd;
    (void)expected_tag;
    (void)new_tag;
}

uint64_t android_fdsan_get_owner_tag(int fd) {
    (void)fd;
    return 0;
}

int android_fdsan_close_with_tag(int fd, uint64_t tag) {
    (void)tag;
    return close(fd);
}
