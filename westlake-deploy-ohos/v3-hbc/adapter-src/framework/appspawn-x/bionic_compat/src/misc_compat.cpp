// bionic_compat/src/misc_compat.cpp
// Miscellaneous Bionic compatibility functions
// - Stack guard re-randomization after fork
// - android_set_abort_message

#include <unistd.h>
#include <fcntl.h>
#include <stdint.h>
#include <string.h>
#include <stdio.h>

// Stack guard (canary) re-randomization after fork
// Bionic does this automatically; musl does not
extern uintptr_t __stack_chk_guard;

extern "C" void android_reset_stack_guards(void) {
    uintptr_t guard;
    int fd = open("/dev/urandom", O_RDONLY | O_CLOEXEC);
    if (fd >= 0) {
        ssize_t n = read(fd, &guard, sizeof(guard));
        close(fd);
        if (n == sizeof(guard)) {
            __stack_chk_guard = guard;
            return;
        }
    }
    // Fallback: use a constant (less secure but won't crash)
    __stack_chk_guard = 0x00000aff0a0d0000UL;
}

// android_set_abort_message: used by ART to set crash message before abort()
// On OH, just log it via stderr
static char g_abort_message[1024] = {0};

extern "C" void android_set_abort_message(const char* msg) {
    if (msg) {
        strncpy(g_abort_message, msg, sizeof(g_abort_message) - 1);
        fprintf(stderr, "android_set_abort_message: %s\n", msg);
    }
}

// android_get_abort_message: used by crash handler
extern "C" const char* android_get_abort_message(void) {
    return g_abort_message[0] ? g_abort_message : nullptr;
}


// ZIP error code to string (needed by libartbase.so, via libbionic_compat.so NEEDED)
const char* ErrorCodeString(int err) {
    switch (err) {
        case 0:  return "Success";
        case -1: return "Iteration ended";
        case -2: return "Invalid ZIP archive";
        case -3: return "Entry not found";
        case -4: return "Invalid entry name";
        case -5: return "I/O error";
        case -6: return "Decompression error";
        case -7: return "Allocation failed";
        case -8: return "Buffer too small";
        case -9: return "Invalid handle";
        case -10: return "File operation error";
        default: return "Unknown ZIP error";
    }
}

// ---------------------------------------------------------------------------
// android_dlwarning — Bionic linker API used by AOSP libandroid_runtime to
// report deprecated library usage. On OH/musl we have no warning source;
// implement as a no-op that does not invoke the callback. This matches the
// documented contract: "calls f(obj, msg) for each deprecated lib"; with
// zero deprecated libs reported, f is never invoked.
// ---------------------------------------------------------------------------
extern "C" void android_dlwarning(void* /*obj*/,
                                   void (* /*f*/)(void* /*obj*/, const char* /*msg*/)) {
    // nothing to report
}
