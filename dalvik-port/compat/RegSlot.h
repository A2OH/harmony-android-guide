/*
 * 64-bit register slot fix for Dalvik portable interpreter.
 *
 * Dalvik was designed for 32-bit only. Register slots are u4 (uint32_t),
 * but on 64-bit, object references are 8 bytes. We widen each register
 * slot to pointer-width so objects fit naturally.
 *
 * Include this before any Dalvik header that uses frame pointers.
 */
#ifndef DALVIK_REGSLOT_H
#define DALVIK_REGSLOT_H

#include <stdint.h>

/* Register slot type: pointer-sized so object references fit */
#if __SIZEOF_POINTER__ > 4
typedef uintptr_t dreg_t;
#else
typedef uint32_t dreg_t;
#endif

#endif /* DALVIK_REGSLOT_H */
