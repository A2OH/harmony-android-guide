@ oh_sync_builtins.S
@
@ A.9 - ARM32 __sync_* legacy builtins (pure asm to sidestep clang's
@ builtin-redeclaration diagnostic). Referenced by 11 libhwui .o files for
@ __sync_synchronize, and 1 (.o jni_Paint.o) for __sync_val_compare_and_swap_1.
@
@ OH toolchain ships arm-linux-ohos libclang_rt.builtins.a but its
@ sync_synchronize.S.o member has no symbols (debug-only stub) and
@ sync_val_compare_and_swap_1 is absent entirely. These hand-rolled
@ implementations follow the ARMv7-A reference manual:
@   __sync_synchronize:           dmb ish  (inner-shareable memory barrier)
@   __sync_val_compare_and_swap_1: byte CAS via ldrexb / strexb loop
@
@ Returns old value of *ptr (regardless of whether the swap happened).

    .syntax unified
    .arch armv7-a
    .text

    .global __sync_synchronize
    .type   __sync_synchronize, %function
__sync_synchronize:
    dmb ish
    bx lr
    .size   __sync_synchronize, .-__sync_synchronize


    @ uint8_t __sync_val_compare_and_swap_1(volatile uint8_t *ptr,
    @                                       uint8_t oldval,
    @                                       uint8_t newval)
    @ r0 = ptr, r1 = oldval, r2 = newval
    .global __sync_val_compare_and_swap_1
    .type   __sync_val_compare_and_swap_1, %function
__sync_val_compare_and_swap_1:
    dmb     ish
    uxtb    r1, r1                @ zero-extend old byte to 32b for cmp
1:  ldrexb  r3, [r0]
    cmp     r3, r1
    bne     2f
    strexb  ip, r2, [r0]
    cmp     ip, #0
    bne     1b
2:  dmb     ish
    uxtb    r0, r3                @ return old byte in r0
    bx      lr
    .size   __sync_val_compare_and_swap_1, .-__sync_val_compare_and_swap_1

    .section .note.GNU-stack,"",%progbits
