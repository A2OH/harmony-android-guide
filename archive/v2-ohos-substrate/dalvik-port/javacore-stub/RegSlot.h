#ifndef DALVIK_REGSLOT_H
#define DALVIK_REGSLOT_H
#include <stdint.h>
/* Register slot type: pointer-sized so object references fit on 64-bit.
 * Dalvik was designed for 32-bit, but our standalone port uses native
 * 64-bit pointers for object references. */
#if __SIZEOF_POINTER__ > 4
typedef uintptr_t dreg_t;
#else
typedef uint32_t dreg_t;
#endif
#endif
