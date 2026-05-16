// bionic_compat/src/malloc_compat.cpp
// Stubs for Bionic-specific malloc extensions
// musl uses a different malloc implementation (no jemalloc, no malloc_trim)

#include <bionic/malloc.h>
#include <stdbool.h>
#include <stddef.h>

bool android_mallopt(int opcode, void* arg, size_t arg_size) {
    (void)arg;
    (void)arg_size;
    switch (opcode) {
        case M_SET_ZYGOTE_CHILD:
        case M_INIT_ZYGOTE_CHILD_PROFILING:
        case M_DECAY_TIME:
        case M_MEMTAG_STACK_IS_ON:
        case M_PURGE:
            // Safe no-ops: musl does not support these operations
            return true;
        case M_GET_MALLOC_LEAK_INFO:
        case M_FREE_MALLOC_LEAK_INFO:
            return false;
        default:
            return false;
    }
}

void android_mallopt_get_caller_info(void** frames, size_t max_frames, size_t* num_frames) {
    (void)frames;
    (void)max_frames;
    if (num_frames) *num_frames = 0;
}
