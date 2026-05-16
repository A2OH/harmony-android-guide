// bionic_compat/include/bionic/malloc.h
// Compatibility header: stubs for Bionic malloc extensions
#ifndef BIONIC_COMPAT_MALLOC_H
#define BIONIC_COMPAT_MALLOC_H

#include <stdbool.h>
#include <stddef.h>
#include <malloc.h>

#ifdef __cplusplus
extern "C" {
#endif

// Bionic-specific mallopt opcodes (not in musl)
#define M_SET_ZYGOTE_CHILD          1000
#define M_INIT_ZYGOTE_CHILD_PROFILING 1001
#define M_GET_MALLOC_LEAK_INFO      1002
#define M_FREE_MALLOC_LEAK_INFO     1003
#define M_PURGE                     1004
#define M_DECAY_TIME                1005
#define M_MEMTAG_STACK_IS_ON        1006

// Android-specific malloc control
bool android_mallopt(int opcode, void* arg, size_t arg_size);

// Malloc debug info (stubs)
void android_mallopt_get_caller_info(void** frames, size_t max_frames, size_t* num_frames);

#ifdef __cplusplus
}
#endif

#endif // BIONIC_COMPAT_MALLOC_H
