/*
 * Minimal FFI (Foreign Function Interface) header.
 * The generic arch uses libffi for JNI native method calls.
 * This provides the type declarations needed for compilation.
 * A real libffi must be linked at runtime.
 */
#ifndef FFI_H
#define FFI_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef enum {
    FFI_OK = 0,
    FFI_BAD_TYPEDEF,
    FFI_BAD_ABI
} ffi_status;

typedef enum {
#if defined(__x86_64__) || defined(_M_X64)
    FFI_FIRST_ABI = 1,
    FFI_UNIX64 = 2,
    FFI_WIN64 = 3,
    FFI_DEFAULT_ABI = FFI_UNIX64
#elif defined(__aarch64__)
    FFI_FIRST_ABI = 0,
    FFI_SYSV = 1,
    FFI_DEFAULT_ABI = FFI_SYSV
#elif defined(__i386__)
    FFI_FIRST_ABI = 0,
    FFI_SYSV = 1,
    FFI_DEFAULT_ABI = FFI_SYSV
#else
    FFI_DEFAULT_ABI = 2   /* common default */
#endif
} ffi_abi;

typedef struct _ffi_type {
    size_t size;
    unsigned short alignment;
    unsigned short type;
    struct _ffi_type **elements;
} ffi_type;

typedef struct {
    ffi_abi abi;
    unsigned nargs;
    ffi_type **arg_types;
    ffi_type *rtype;
    unsigned bytes;
    unsigned flags;
} ffi_cif;

/* Predefined types */
extern ffi_type ffi_type_void;
extern ffi_type ffi_type_uint8;
extern ffi_type ffi_type_sint8;
extern ffi_type ffi_type_uint16;
extern ffi_type ffi_type_sint16;
extern ffi_type ffi_type_uint32;
extern ffi_type ffi_type_sint32;
extern ffi_type ffi_type_uint64;
extern ffi_type ffi_type_sint64;
extern ffi_type ffi_type_float;
extern ffi_type ffi_type_double;
extern ffi_type ffi_type_pointer;

/* Core functions */
ffi_status ffi_prep_cif(ffi_cif *cif, ffi_abi abi, unsigned int nargs,
                         ffi_type *rtype, ffi_type **atypes);
void ffi_call(ffi_cif *cif, void (*fn)(void), void *rvalue, void **avalue);

/* FFI_FN macro — casts function pointer */
#define FFI_FN(f) ((void (*)(void))(f))

#ifdef __cplusplus
}
#endif

#endif /* FFI_H */
