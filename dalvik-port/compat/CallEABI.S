/*
 * JNI method invocation for ARM EABI.
 * Adapted from dalvik-kitkat/vm/arch/arm/CallEABI.S
 * Removed dependency on <machine/cpu-features.h>.
 */

/* ARMv7-A always has BLX and PC interwork */
#define __ARM_HAVE_BLX
#define __ARM_HAVE_PC_INTERWORK

#ifdef __ARM_EABI__

#define DBG @

    .text
    .align  2
    .global dvmPlatformInvoke
    .type   dvmPlatformInvoke, %function

/*
 * On entry:
 *   r0  JNIEnv (can be left alone)
 *   r1  clazz (NULL for virtual method calls, non-NULL for static)
 *   r2  arg info
 *   r3  argc (number of 32-bit values in argv)
 *   [sp]     argv
 *   [sp,#4]  short signature
 *   [sp,#8]  func
 *   [sp,#12] pReturn
 */
SAVED_REG_COUNT = 6
FP_STACK_OFFSET = (SAVED_REG_COUNT-1) * 4
FP_ADJ = 4

dvmPlatformInvoke:
    .fnstart

    .save        {r6, r7, r8, r9, fp, lr}
    stmfd   sp!, {r6, r7, r8, r9, fp, lr}

    .setfp  fp, sp, #FP_STACK_OFFSET
    add     fp, sp, #FP_STACK_OFFSET

    ldr     r9, [fp, #0+FP_ADJ]         @ r9<- argv
    cmp     r1, #0                      @ calling a static method?

    @ Not static, grab the "this" pointer.
    subeq   r3, r3, #1                  @ argc--
    ldreq   r1, [r9], #4                @ r1<- *argv++

    @ Do we have arg padding flags in "argInfo"? (just need to check hi bit)
    teq     r2, #0
    bmi     .Lno_arg_info

    /*
     * "Fast" path.
     */
.Lhave_arg_info:
    and     ip, r2, #0x0f000000         @ ip<- double-words required
    mov     r6, r2, lsr #28             @ r6<- return type
    sub     sp, sp, ip, lsr #21         @ shift right 24, then left 3
    mov     r8, sp                      @ r8<- sp  (arg copy dest)

    mov     r7, r9
    movs    r2, r2, lsr #2
    addcc   r7, r7, #8                  @ skip past 2 words, for r2 and r3
    subcc   r3, r3, #2
    addcs   r7, r7, #4                  @ skip past 1 word, for r2
    subcs   r3, r3, #1

.Lfast_copy_loop:
    subs    r3, r3, #1
    bmi     .Lcopy_done

.Lfast_copy_loop2:
    movs    r2, r2, lsr #1
    ldrcc   ip, [r7], #4
    strcc   ip, [r8], #4
    bcc     .Lfast_copy_loop

    add     r8, r8, #4                  @ pad word, just advance
    b       .Lfast_copy_loop2


.Lcopy_done:
    ldmia   r9, {r2-r3}                 @ r2/r3<- argv[0]/argv[1]

    ldr     ip, [fp, #8+FP_ADJ]         @ ip<- func
    blx     ip                          @ call func

    @ Result is in r0 or r0-r1.
    cmp     r6, #0                      @ DALVIK_JNI_RETURN_VOID?
    ldrne   ip, [fp, #12+FP_ADJ]        @ pReturn
    sub     sp, fp, #FP_STACK_OFFSET    @ restore sp
    strne   r0, [ip]                    @ pReturn->j low word
    strne   r1, [ip, #4]               @ pReturn->j high word

    ldmfd   sp!, {r6, r7, r8, r9, fp, pc}


    /*
     * "Slow" path.
     */
.Lno_arg_info:
    mov     ip, r2, lsr #28             @ ip<- return type
    ldr     r6, [fp, #4+FP_ADJ]         @ r6<- short signature
    add     r6, r6, #1                  @ advance past return type
    mov     r2, #0                      @ r2<- word count, init to zero

.Lcount_loop:
    ldrb    ip, [r6], #1                @ ip<- *signature++
    cmp     ip, #0
    beq     .Lcount_done
    add     r2, r2, #1
    cmp     ip, #'D'
    cmpne   ip, #'J'
    bne     .Lcount_loop

    tst     r2, #1
    addne   r2, #1
    addeq   r2, #2
    b       .Lcount_loop
.Lcount_done:

    subs    r2, r2, #2
    movmi   r2, #0
    ble     .Lcopy_done

    sub     sp, sp, r2, asl #2
    bic     sp, #4
    mov     r7, r9
    mov     r8, sp

    ldr     r6, [fp, #4+FP_ADJ]
    add     r6, r6, #1
    add     r7, r7, #8

    ldrb    ip, [r6], #1
    cmp     ip, #'D'
    cmpne   ip, #'J'
    beq     .Lstack_copy_loop

    ldrb    ip, [r6], #1
    cmp     ip, #'D'
    cmpne   ip, #'J'
    subeq   r7, #4
    beq     .Lstack_copy_loop2

.Lstack_copy_loop:
    ldrb    ip, [r6], #1
.Lstack_copy_loop2:
    cmp     ip, #0
    beq     .Lcopy_done

    cmp     ip, #'D'
    cmpne   ip, #'J'
    beq     .Lcopy64

.Lcopy32:
    ldr     ip, [r7], #4
    str     ip, [r8], #4
    b       .Lstack_copy_loop

.Lcopy64:
    add     r8, r8, #7
    ldr     ip, [r7], #4
    bic     r8, r8, #7
    ldr     r2, [r7], #4
    str     ip, [r8], #4
    str     r2, [r8], #4
    b       .Lstack_copy_loop

    .fnend
    .size   dvmPlatformInvoke, .-dvmPlatformInvoke

#endif /* __ARM_EABI__ */
