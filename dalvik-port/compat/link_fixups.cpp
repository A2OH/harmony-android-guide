/*
 * Link fixups for standalone Dalvik port on 64-bit Linux.
 *
 * IMPORTANT: Compiled separately without AOSP dlmalloc include path,
 * because AOSP dlmalloc/malloc.h #defines dlmalloc_trim → malloc_trim etc.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#ifndef __MUSL__
#include <malloc.h>
#endif

/* ── bionic dlmalloc stubs ── */
extern "C" {

int dlmalloc_trim(size_t pad) {
#ifdef __MUSL__
    (void)pad;
    return 0;  /* musl has no malloc_trim */
#else
    return malloc_trim(pad);
#endif
}

void dlmalloc_inspect_all(void(*handler)(void*, void*, size_t, void*),
                          void* arg) {
    (void)handler; (void)arg;
}

void* dlmem2chunk(void* mem) {
    return mem;
}

int jniRegisterSystemMethods(void* env) {
    (void)env;
    return 0;
}

} /* extern "C" */

/* ── 64-bit u4/size_t mismatch wrapper ── */
#if __SIZEOF_POINTER__ > 4
struct StringObject;
extern "C" StringObject* _Z32dvmCreateStringFromCstrAndLengthPKcm(const char*, unsigned long);
extern "C" StringObject* _Z32dvmCreateStringFromCstrAndLengthPKcj(const char* s, unsigned int len) {
    return _Z32dvmCreateStringFromCstrAndLengthPKcm(s, (unsigned long)len);
}
#endif

/* ── mterp stubs for portable-only build ── */
struct Thread;
extern "C" void dvmMterpStdRun(Thread* self) {
    fprintf(stderr, "FATAL: dvmMterpStdRun called in portable-only build\n");
    abort();
}

extern "C" void dvmMterpStdBail(Thread* self) {
    (void)self;
    fprintf(stderr, "FATAL: dvmMterpStdBail called in portable-only build\n");
    abort();
}
