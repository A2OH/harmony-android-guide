// bionic_compat/src/art_quick_entrypoints_arm64.S
// ARM64 quick entrypoint stubs for ART runtime on OH.
//
// In AOSP, these are in art/runtime/arch/arm64/quick_entrypoints_arm64.S
// (~4000 lines of hand-tuned assembly). For cross-compilation, we provide
// minimal stubs that call through to the C++ interpreter fallback.
//
// Strategy:
//   - Most entrypoints trap to C++ via art_quick_to_interpreter_bridge
//   - Critical path stubs (invoke, alloc) have minimal implementations
//   - All stubs are callable from compiled code via the quick ABI

#include "asm_support_arm64.S"

// ============================================================
// Method invocation stubs
// ============================================================

// art_quick_invoke_stub(ArtMethod* method, uint32_t* args, uint32_t args_size,
//                       Thread* self, JValue* result, const char* shorty)
// Called from C++ to invoke a compiled method via the quick ABI.
    .global art_quick_invoke_stub
    .type art_quick_invoke_stub, %function
    .balign 16
art_quick_invoke_stub:
    // Save callee-saves
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp

    // x0 = ArtMethod*, x1 = args, x2 = args_size, x3 = self, x4 = result, x5 = shorty
    // Load entrypoint from ArtMethod
    ldr   x9, [x0, #32]      // ART_METHOD_ENTRY_POINT_OFFSET_64
    // If entrypoint is null, return
    cbz   x9, .Linvoke_return

    // Set up self in x19 (thread register) per quick ABI
    mov   x19, x3

    // Call through entrypoint
    blr   x9

.Linvoke_return:
    // Store result if result ptr non-null
    cbz   x4, .Linvoke_done
    str   x0, [x4]
.Linvoke_done:
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_invoke_stub, .-art_quick_invoke_stub

// art_quick_invoke_static_stub - same signature, for static methods
    .global art_quick_invoke_static_stub
    .type art_quick_invoke_static_stub, %function
    .balign 16
art_quick_invoke_static_stub:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    mov   x19, x3
    ldr   x9, [x0, #32]
    cbz   x9, .Lstatic_return
    blr   x9
.Lstatic_return:
    cbz   x4, .Lstatic_done
    str   x0, [x4]
.Lstatic_done:
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_invoke_static_stub, .-art_quick_invoke_static_stub

// ============================================================
// Resolution and bridge trampolines
// ============================================================

// art_quick_resolution_trampoline - called for unresolved methods
    .global art_quick_resolution_trampoline
    .type art_quick_resolution_trampoline, %function
    .balign 16
art_quick_resolution_trampoline:
    // Fall through to C++ artQuickResolutionTrampoline
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artQuickResolutionTrampoline
    ldp   x29, x30, [sp], #16
    cbz   x0, .Lresolution_fail
    br    x0
.Lresolution_fail:
    ret
    .size art_quick_resolution_trampoline, .-art_quick_resolution_trampoline

// art_quick_imt_conflict_trampoline - interface method table collision
    .global art_quick_imt_conflict_trampoline
    .type art_quick_imt_conflict_trampoline, %function
    .balign 16
art_quick_imt_conflict_trampoline:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artInvokeInterfaceTrampoline
    ldp   x29, x30, [sp], #16
    cbz   x0, .Limt_fail
    br    x0
.Limt_fail:
    ret
    .size art_quick_imt_conflict_trampoline, .-art_quick_imt_conflict_trampoline

// art_quick_generic_jni_trampoline - generic JNI call bridge
    .global art_quick_generic_jni_trampoline
    .type art_quick_generic_jni_trampoline, %function
    .balign 16
art_quick_generic_jni_trampoline:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artQuickGenericJniTrampoline
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_generic_jni_trampoline, .-art_quick_generic_jni_trampoline

// art_quick_to_interpreter_bridge - compiled code to interpreter
    .global art_quick_to_interpreter_bridge
    .type art_quick_to_interpreter_bridge, %function
    .balign 16
art_quick_to_interpreter_bridge:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artQuickToInterpreterBridge
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_to_interpreter_bridge, .-art_quick_to_interpreter_bridge

// ============================================================
// Deoptimization
// ============================================================

    .global art_quick_deoptimize
    .type art_quick_deoptimize, %function
    .balign 16
art_quick_deoptimize:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artDeoptimize
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_deoptimize, .-art_quick_deoptimize

    .global art_quick_deoptimize_from_return
    .type art_quick_deoptimize_from_return, %function
    .balign 16
art_quick_deoptimize_from_return:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artDeoptimizeFromReturn
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_deoptimize_from_return, .-art_quick_deoptimize_from_return

// ============================================================
// Exception handling
// ============================================================

    .global art_quick_deliver_exception
    .type art_quick_deliver_exception, %function
    .balign 16
art_quick_deliver_exception:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artDeliverExceptionFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_deliver_exception, .-art_quick_deliver_exception

    .global art_quick_throw_null_pointer_exception
    .type art_quick_throw_null_pointer_exception, %function
    .balign 16
art_quick_throw_null_pointer_exception:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artThrowNullPointerExceptionFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_throw_null_pointer_exception, .-art_quick_throw_null_pointer_exception

    .global art_quick_throw_div_zero
    .type art_quick_throw_div_zero, %function
    .balign 16
art_quick_throw_div_zero:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artThrowDivZeroFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_throw_div_zero, .-art_quick_throw_div_zero

    .global art_quick_throw_array_bounds
    .type art_quick_throw_array_bounds, %function
    .balign 16
art_quick_throw_array_bounds:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artThrowArrayBoundsFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_throw_array_bounds, .-art_quick_throw_array_bounds

    .global art_quick_throw_stack_overflow
    .type art_quick_throw_stack_overflow, %function
    .balign 16
art_quick_throw_stack_overflow:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artThrowStackOverflowFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_throw_stack_overflow, .-art_quick_throw_stack_overflow

    .global art_quick_throw_string_bounds
    .type art_quick_throw_string_bounds, %function
    .balign 16
art_quick_throw_string_bounds:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artThrowStringBoundsFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_throw_string_bounds, .-art_quick_throw_string_bounds

// ============================================================
// Type checking
// ============================================================

    .global art_quick_check_instance_of
    .type art_quick_check_instance_of, %function
    .balign 16
art_quick_check_instance_of:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artInstanceOfFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_check_instance_of, .-art_quick_check_instance_of

// ============================================================
// Object allocation
// ============================================================

    .global art_quick_alloc_object_resolved
    .type art_quick_alloc_object_resolved, %function
    .balign 16
art_quick_alloc_object_resolved:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artAllocObjectFromCodeResolved
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_alloc_object_resolved, .-art_quick_alloc_object_resolved

    .global art_quick_alloc_object_initialized
    .type art_quick_alloc_object_initialized, %function
    .balign 16
art_quick_alloc_object_initialized:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artAllocObjectFromCodeInitialized
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_alloc_object_initialized, .-art_quick_alloc_object_initialized

    .global art_quick_alloc_array_resolved
    .type art_quick_alloc_array_resolved, %function
    .balign 16
art_quick_alloc_array_resolved:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artAllocArrayFromCodeResolved
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_alloc_array_resolved, .-art_quick_alloc_array_resolved

    .global art_quick_alloc_string_from_bytes
    .type art_quick_alloc_string_from_bytes, %function
    .balign 16
art_quick_alloc_string_from_bytes:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artAllocStringFromBytesFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_alloc_string_from_bytes, .-art_quick_alloc_string_from_bytes

    .global art_quick_alloc_string_from_chars
    .type art_quick_alloc_string_from_chars, %function
    .balign 16
art_quick_alloc_string_from_chars:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artAllocStringFromCharsFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_alloc_string_from_chars, .-art_quick_alloc_string_from_chars

    .global art_quick_alloc_string_from_string
    .type art_quick_alloc_string_from_string, %function
    .balign 16
art_quick_alloc_string_from_string:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artAllocStringFromStringFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_alloc_string_from_string, .-art_quick_alloc_string_from_string

// ============================================================
// Monitor (lock) operations
// ============================================================

    .global art_quick_lock_object
    .type art_quick_lock_object, %function
    .balign 16
art_quick_lock_object:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artLockObjectFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_lock_object, .-art_quick_lock_object

    .global art_quick_unlock_object
    .type art_quick_unlock_object, %function
    .balign 16
art_quick_unlock_object:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artUnlockObjectFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_unlock_object, .-art_quick_unlock_object

// ============================================================
// Field access
// ============================================================

    .global art_quick_set32_instance
    .type art_quick_set32_instance, %function
    .balign 16
art_quick_set32_instance:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artSet32InstanceFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_set32_instance, .-art_quick_set32_instance

    .global art_quick_set64_instance
    .type art_quick_set64_instance, %function
    .balign 16
art_quick_set64_instance:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artSet64InstanceFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_set64_instance, .-art_quick_set64_instance

    .global art_quick_set_obj_instance
    .type art_quick_set_obj_instance, %function
    .balign 16
art_quick_set_obj_instance:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artSetObjInstanceFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_set_obj_instance, .-art_quick_set_obj_instance

    .global art_quick_get32_instance
    .type art_quick_get32_instance, %function
    .balign 16
art_quick_get32_instance:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artGet32InstanceFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_get32_instance, .-art_quick_get32_instance

    .global art_quick_get64_instance
    .type art_quick_get64_instance, %function
    .balign 16
art_quick_get64_instance:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artGet64InstanceFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_get64_instance, .-art_quick_get64_instance

    .global art_quick_get_obj_instance
    .type art_quick_get_obj_instance, %function
    .balign 16
art_quick_get_obj_instance:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artGetObjInstanceFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_get_obj_instance, .-art_quick_get_obj_instance

    .global art_quick_set32_static
    .type art_quick_set32_static, %function
    .balign 16
art_quick_set32_static:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artSet32StaticFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_set32_static, .-art_quick_set32_static

    .global art_quick_set64_static
    .type art_quick_set64_static, %function
    .balign 16
art_quick_set64_static:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artSet64StaticFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_set64_static, .-art_quick_set64_static

    .global art_quick_set_obj_static
    .type art_quick_set_obj_static, %function
    .balign 16
art_quick_set_obj_static:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artSetObjStaticFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_set_obj_static, .-art_quick_set_obj_static

    .global art_quick_get32_static
    .type art_quick_get32_static, %function
    .balign 16
art_quick_get32_static:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artGet32StaticFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_get32_static, .-art_quick_get32_static

    .global art_quick_get64_static
    .type art_quick_get64_static, %function
    .balign 16
art_quick_get64_static:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artGet64StaticFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_get64_static, .-art_quick_get64_static

    .global art_quick_get_obj_static
    .type art_quick_get_obj_static, %function
    .balign 16
art_quick_get_obj_static:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artGetObjStaticFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_get_obj_static, .-art_quick_get_obj_static

// ============================================================
// GC read barrier (for concurrent copying GC)
// ============================================================

    .global art_quick_read_barrier_mark_reg00
    .type art_quick_read_barrier_mark_reg00, %function
    .balign 16
art_quick_read_barrier_mark_reg00:
    // x0 = reference to mark
    // Fast path: if not in GC marking phase, return immediately
    ret
    .size art_quick_read_barrier_mark_reg00, .-art_quick_read_barrier_mark_reg00

// Generate read barrier stubs for registers x1-x17
.macro READ_BARRIER_MARK_REG reg_num
    .global art_quick_read_barrier_mark_reg\reg_num
    .type art_quick_read_barrier_mark_reg\reg_num, %function
    .balign 16
art_quick_read_barrier_mark_reg\reg_num:
    ret
    .size art_quick_read_barrier_mark_reg\reg_num, .-art_quick_read_barrier_mark_reg\reg_num
.endm

READ_BARRIER_MARK_REG 01
READ_BARRIER_MARK_REG 02
READ_BARRIER_MARK_REG 03
READ_BARRIER_MARK_REG 04
READ_BARRIER_MARK_REG 05
READ_BARRIER_MARK_REG 06
READ_BARRIER_MARK_REG 07
READ_BARRIER_MARK_REG 08
READ_BARRIER_MARK_REG 09
READ_BARRIER_MARK_REG 10
READ_BARRIER_MARK_REG 11
READ_BARRIER_MARK_REG 12
READ_BARRIER_MARK_REG 13
READ_BARRIER_MARK_REG 14
READ_BARRIER_MARK_REG 15
READ_BARRIER_MARK_REG 16
READ_BARRIER_MARK_REG 17

    .global art_quick_read_barrier_slow
    .type art_quick_read_barrier_slow, %function
    .balign 16
art_quick_read_barrier_slow:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artReadBarrierSlow
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_read_barrier_slow, .-art_quick_read_barrier_slow

// ============================================================
// JNI transition
// ============================================================

    .global art_jni_dlsym_lookup_stub
    .type art_jni_dlsym_lookup_stub, %function
    .balign 16
art_jni_dlsym_lookup_stub:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artFindNativeMethod
    ldp   x29, x30, [sp], #16
    cbz   x0, .Ljni_not_found
    br    x0
.Ljni_not_found:
    ret
    .size art_jni_dlsym_lookup_stub, .-art_jni_dlsym_lookup_stub

    .global art_jni_dlsym_lookup_critical_stub
    .type art_jni_dlsym_lookup_critical_stub, %function
    .balign 16
art_jni_dlsym_lookup_critical_stub:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artFindNativeMethodRunnable
    ldp   x29, x30, [sp], #16
    cbz   x0, .Ljni_crit_not_found
    br    x0
.Ljni_crit_not_found:
    ret
    .size art_jni_dlsym_lookup_critical_stub, .-art_jni_dlsym_lookup_critical_stub

// ============================================================
// Test suspend (checkpoint) - called from compiled code
// ============================================================

    .global art_quick_test_suspend
    .type art_quick_test_suspend, %function
    .balign 16
art_quick_test_suspend:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artTestSuspendFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_test_suspend, .-art_quick_test_suspend

// ============================================================
// Initialize class
// ============================================================

    .global art_quick_initialize_static_storage
    .type art_quick_initialize_static_storage, %function
    .balign 16
art_quick_initialize_static_storage:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artInitializeStaticStorageFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_initialize_static_storage, .-art_quick_initialize_static_storage

    .global art_quick_resolve_type
    .type art_quick_resolve_type, %function
    .balign 16
art_quick_resolve_type:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artInitializeTypeFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_resolve_type, .-art_quick_resolve_type

    .global art_quick_resolve_type_and_verify_access
    .type art_quick_resolve_type_and_verify_access, %function
    .balign 16
art_quick_resolve_type_and_verify_access:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artInitializeTypeAndVerifyAccessFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_resolve_type_and_verify_access, .-art_quick_resolve_type_and_verify_access

    .global art_quick_resolve_method_handle
    .type art_quick_resolve_method_handle, %function
    .balign 16
art_quick_resolve_method_handle:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artResolveMethodHandleFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_resolve_method_handle, .-art_quick_resolve_method_handle

    .global art_quick_resolve_method_type
    .type art_quick_resolve_method_type, %function
    .balign 16
art_quick_resolve_method_type:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artResolveMethodTypeFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_resolve_method_type, .-art_quick_resolve_method_type

    .global art_quick_resolve_string
    .type art_quick_resolve_string, %function
    .balign 16
art_quick_resolve_string:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artResolveStringFromCode
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_resolve_string, .-art_quick_resolve_string

// ============================================================
// Proxy invoke handler
// ============================================================

    .global art_quick_proxy_invoke_handler
    .type art_quick_proxy_invoke_handler, %function
    .balign 16
art_quick_proxy_invoke_handler:
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artQuickProxyInvokeHandler
    ldp   x29, x30, [sp], #16
    ret
    .size art_quick_proxy_invoke_handler, .-art_quick_proxy_invoke_handler

// ============================================================
// Nterp (next-gen interpreter) entry
// ============================================================

    .global ExecuteNterpImpl
    .type ExecuteNterpImpl, %function
    .balign 16
ExecuteNterpImpl:
    // Fallback: redirect to standard interpreter
    stp   x29, x30, [sp, #-16]!
    mov   x29, sp
    bl    artQuickToInterpreterBridge
    ldp   x29, x30, [sp], #16
    ret
    .size ExecuteNterpImpl, .-ExecuteNterpImpl

    .global NterpGetShorty
    .type NterpGetShorty, %function
    .balign 16
NterpGetShorty:
    mov   x0, xzr
    ret
    .size NterpGetShorty, .-NterpGetShorty

    .global NterpGetCodeItem
    .type NterpGetCodeItem, %function
    .balign 16
NterpGetCodeItem:
    mov   x0, xzr
    ret
    .size NterpGetCodeItem, .-NterpGetCodeItem
