// bionic_compat/src/asm_support_arm64.S
// Assembly support macros for ARM64 ART entrypoints.
// Minimal version for cross-compilation on OH.

#ifndef ART_RUNTIME_ASM_SUPPORT_ARM64_S
#define ART_RUNTIME_ASM_SUPPORT_ARM64_S

// Use .text section for all code
.text

// ARM64 calling convention:
//   x0-x7:   arguments / return values
//   x8:      indirect result register
//   x9-x15:  temporaries (caller-saved)
//   x16-x17: intra-procedure-call temporaries
//   x18:     platform register (reserved on some platforms)
//   x19:     ART thread register (self)
//   x20-x28: callee-saved
//   x29:     frame pointer
//   x30:     link register
//   sp:      stack pointer

// Thread register - x19 holds current Thread* in ART quick ABI
#define xSELF x19
#define wSELF w19

// Macro to declare a global function with proper alignment
.macro ENTRY name
    .global \name
    .type \name, %function
    .balign 16
\name:
.endm

.macro END name
    .size \name, .-\name
.endm

// Macro for saving/restoring callee-save registers
.macro SAVE_CALLEE_SAVES
    stp   x19, x20, [sp, #-16]!
    stp   x21, x22, [sp, #-16]!
    stp   x23, x24, [sp, #-16]!
    stp   x25, x26, [sp, #-16]!
    stp   x27, x28, [sp, #-16]!
    stp   x29, x30, [sp, #-16]!
.endm

.macro RESTORE_CALLEE_SAVES
    ldp   x29, x30, [sp], #16
    ldp   x27, x28, [sp], #16
    ldp   x25, x26, [sp], #16
    ldp   x23, x24, [sp], #16
    ldp   x21, x22, [sp], #16
    ldp   x19, x20, [sp], #16
.endm

#endif // ART_RUNTIME_ASM_SUPPORT_ARM64_S
