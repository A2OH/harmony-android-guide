// bionic_compat/src/art_runtime_stubs.cpp
// Stubs for ART runtime external dependencies that don't exist on OH.
// These resolve link-time symbols that ART expects from Android subsystems.

#include <dlfcn.h>
#include <fcntl.h>
#include <stdarg.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/syscall.h>
#include <unistd.h>
#include <unwind.h>

extern "C" {

// ============================================================
// 0. Low-level builtins (must also be in stubs for libart dlopen)
// ============================================================

// Override abort() and catch SIGSEGV to capture crash context during ART loading
#include <setjmp.h>
#include <signal.h>
#include <ucontext.h>

static void debug_signal_handler(int sig, siginfo_t* info, void* ctx) {
    (void)ctx;
    char buf[128];
    int n = snprintf(buf, sizeof(buf), "[STUBS] Signal %d addr=%p\n", sig, info->si_addr);
    write(2, buf, n);
    // Dump maps to file
    int mfd = open("/data/local/tmp/crash_maps.txt", O_WRONLY|O_CREAT|O_TRUNC, 0666);
    if (mfd >= 0) {
        int sfd = open("/proc/self/maps", O_RDONLY);
        if (sfd >= 0) { char m[4096]; int r; while ((r = read(sfd, m, sizeof(m))) > 0) write(mfd, m, r); close(sfd); }
        close(mfd);
    }
    // Default SIGSEGV handling
    struct sigaction sa;
    sa.sa_handler = SIG_DFL;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = 0;
    sigaction(sig, &sa, NULL);
    raise(sig);
}

// Install debug signal handler during static init
__attribute__((constructor))
static void install_debug_signal_handlers(void) {
    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_sigaction = debug_signal_handler;
    sa.sa_flags = SA_SIGINFO;
    sigaction(SIGSEGV, &sa, NULL);
    sigaction(SIGBUS, &sa, NULL);
    sigaction(SIGILL, &sa, NULL);
    sigaction(SIGABRT, &sa, NULL);
}
jmp_buf g_abort_jmpbuf;
volatile int g_abort_trap_enabled = 0;

// 2026-05-02 G2.14r: backtrace dumper for SIGABRT diagnosis.
// Used in abort() override + raise(SIGABRT) override below.  When ART aborts
// via art::Runtime::Abort or any libart internal CHECK_FAIL path that bypasses
// android::base::SetAborter (HiLogAborter), this is the only way to capture
// the call site.  Output goes to stderr (which child_main redirects to a file).
struct UnwindCtx { int depth; };
static _Unwind_Reason_Code unwind_cb(struct _Unwind_Context* ctx, void* arg) {
    UnwindCtx* u = (UnwindCtx*)arg;
    uintptr_t pc = _Unwind_GetIP(ctx);
    if (pc == 0) return _URC_END_OF_STACK;
    Dl_info info = {};
    int has = dladdr((void*)pc, &info);
    fprintf(stderr, "  #%02d pc=0x%08lx %s%s%s+0x%lx\n",
            u->depth,
            (unsigned long)pc,
            has && info.dli_fname ? info.dli_fname : "?",
            has && info.dli_sname ? "(" : "",
            has && info.dli_sname ? info.dli_sname : "",
            has && info.dli_saddr ? (unsigned long)pc - (unsigned long)info.dli_saddr : 0);
    if (has && info.dli_sname) fprintf(stderr, ")\n");
    if (++u->depth > 32) return _URC_END_OF_STACK;
    return _URC_NO_REASON;
}
static void dumpBacktrace(const char* whence) {
    fprintf(stderr, "[STUBS] BACKTRACE (%s):\n", whence);
    UnwindCtx u = {0};
    _Unwind_Backtrace(unwind_cb, &u);
    fflush(stderr);
}

void abort(void) {
    fprintf(stderr, "[STUBS] abort() called!\n");
    dumpBacktrace("abort");
    fflush(stderr); fflush(stdout);
    if (g_abort_trap_enabled) {
        fprintf(stderr, "[STUBS] abort trapped, longjmp back\n");
        longjmp(g_abort_jmpbuf, 1);
    }
    raise(SIGABRT);
    syscall(1 /* SYS_exit_group */, 134);
    __builtin_unreachable();
}

// 2026-05-02 G2.14r: raise() override.  ART's Runtime::Abort or
// art_quick_throw_internal_error etc may call raise(SIGABRT) directly,
// bypassing both android::base::SetAborter and our abort() override.
// This wrapper catches the SIGABRT raise and dumps backtrace before
// forwarding to musl's real raise().
//
// Only intercept SIGABRT to avoid disturbing other raise paths.
extern "C" int __real_raise(int) __attribute__((weak));
int raise(int sig) {
    if (sig == SIGABRT) {
        fprintf(stderr, "[STUBS] raise(SIGABRT) called!\n");
        dumpBacktrace("raise(SIGABRT)");
        fflush(stderr); fflush(stdout);
    }
    // Forward to default raise via direct syscall (avoid recursion).
    // Use tgkill(pid, tid, sig) for thread-targeted signal.
    pid_t pid = getpid();
    pid_t tid = (pid_t)syscall(SYS_gettid);
    return (int)syscall(SYS_tgkill, pid, tid, sig);
}

// Use __asm__ labels to avoid clang builtin redefinition errors
void __sync_synchronize_stub(void) __asm__("__sync_synchronize");
void __sync_synchronize_stub(void) {
    // ARM32 full memory barrier. Must use inline asm: clang lowers
    // __atomic_thread_fence(SEQ_CST) on ARMv7 to a libcall to __sync_synchronize,
    // which resolves back to this stub -> infinite recursion -> stack overflow.
#if defined(__arm__)
    __asm__ __volatile__("dmb ish" ::: "memory");
#else
    __atomic_thread_fence(__ATOMIC_SEQ_CST);
#endif
}

uint8_t __sync_val_cas1_stub(volatile uint8_t* ptr, uint8_t oldval, uint8_t newval)
    __asm__("__sync_val_compare_and_swap_1");
uint8_t __sync_val_cas1_stub(volatile uint8_t* ptr, uint8_t oldval, uint8_t newval) {
    uint8_t expected = oldval;
    __atomic_compare_exchange_n(ptr, &expected, newval, 0,
                                __ATOMIC_SEQ_CST, __ATOMIC_SEQ_CST);
    return expected;
}

__attribute__((weak)) int __memcmp16(const uint16_t* s1, const uint16_t* s2, unsigned int count) {
    for (unsigned int i = 0; i < count; i++) {
        if (s1[i] != s2[i]) return (int)s1[i] - (int)s2[i];
    }
    return 0;
}

#define ADLER_MOD 65521u
__attribute__((weak)) unsigned long adler32(unsigned long adler, const unsigned char* buf, unsigned int len) {
    if (!buf) return 1;
    unsigned long s1 = adler & 0xffff;
    unsigned long s2 = (adler >> 16) & 0xffff;
    for (unsigned int i = 0; i < len; i++) {
        s1 = (s1 + buf[i]) % ADLER_MOD;
        s2 = (s2 + s1) % ADLER_MOD;
    }
    return (s2 << 16) | s1;
}

__attribute__((weak)) unsigned long adler32_combine(unsigned long a1, unsigned long a2, long len2) {
    (void)len2;
    unsigned long s1 = ((a1 & 0xffff) + (a2 & 0xffff)) % ADLER_MOD;
    unsigned long s2 = ((a1 >> 16) + (a2 >> 16) + (a2 & 0xffff)) % ADLER_MOD;
    return (s2 << 16) | s1;
}

// ============================================================
// 1. libnativeloader stubs
// ============================================================
// ART uses libnativeloader for dlopen with namespace isolation.
// On OH, we use plain dlopen.

struct NativeLoaderNamespace;

void* OpenNativeLibrary(
    void* env,
    int32_t target_sdk_version,
    const char* path,
    void* class_loader,
    const char* caller_location,
    void* library_path,
    bool* needs_native_bridge,
    char** error_msg) {
    (void)env; (void)target_sdk_version; (void)class_loader;
    (void)caller_location; (void)library_path;
    if (needs_native_bridge) *needs_native_bridge = false;
    void* handle = dlopen(path, RTLD_NOW);
    if (!handle && error_msg) {
        const char* err = dlerror();
        *error_msg = err ? strdup(err) : strdup("unknown dlopen error");
    }
    return handle;
}

bool CloseNativeLibrary(void* handle, bool needs_native_bridge, char** error_msg) {
    (void)needs_native_bridge;
    if (dlclose(handle) != 0) {
        if (error_msg) {
            const char* err = dlerror();
            *error_msg = err ? strdup(err) : strdup("dlclose failed");
        }
        return false;
    }
    return true;
}

void NativeLoaderFreeErrorMessage(char* msg) {
    free(msg);
}

struct NativeLoaderNamespace* FindNativeLoaderNamespaceByClassLoader(
    void* env, void* class_loader) {
    (void)env; (void)class_loader;
    return nullptr;
}

void* FindSymbolInNativeLoaderNamespace(
    struct NativeLoaderNamespace* ns, const char* symbol_name) {
    (void)ns;
    return dlsym(RTLD_DEFAULT, symbol_name);
}

struct NativeLoaderNamespace* CreateClassLoaderNamespace(
    void* env,
    int32_t target_sdk_version,
    void* class_loader,
    bool is_shared,
    const char* dex_path,
    const char* library_path,
    const char* permitted_path,
    const char* uses_library_list,
    char** error_msg) {
    (void)env; (void)target_sdk_version; (void)class_loader;
    (void)is_shared; (void)dex_path; (void)library_path;
    (void)permitted_path; (void)uses_library_list; (void)error_msg;
    return nullptr;
}

void InitializeNativeLoader() {}
void ResetNativeLoader() {}

// ============================================================
// 2. libnativebridge stubs
// ============================================================
// Native bridge is for running ARM apps on x86 etc. Not needed on ARM64 OH.

bool NativeBridgeAvailable() { return false; }
bool NativeBridgeInitialized() { return false; }

void* NativeBridgeLoadLibrary(const char* /*libpath*/, int /*flag*/) {
    return nullptr;
}

void* NativeBridgeGetTrampoline(void* /*handle*/, const char* /*name*/,
                                 const char* /*shorty*/, uint32_t /*len*/) {
    return nullptr;
}

bool NativeBridgeIsSupported(const char* /*libpath*/) { return false; }
bool NativeBridgeIsPathSupported(const char* /*path*/) { return false; }

const char* NativeBridgeGetError() { return "native bridge not available on OH"; }

bool NativeBridgeLinkNamespaces(void* /*src*/, void* /*dst*/,
                                 const char* /*shared_libs*/) {
    return false;
}

void* NativeBridgeLoadLibraryExt(const char* /*libpath*/, int /*flag*/,
                                  void* /*ns*/) {
    return nullptr;
}

void* NativeBridgeGetExportedNamespace(const char* /*name*/) {
    return nullptr;
}

// Additional NativeBridge symbols needed by libart.so
bool LoadNativeBridge(const char* /*nb_library*/, void* /*runtime_callbacks*/) { return false; }
bool InitializeNativeBridge(void* /*env*/, const char* /*instruction_set*/) { return false; }
bool PreInitializeNativeBridge(const char* /*app_data_dir*/, const char* /*instruction_set*/) { return false; }
void PreZygoteForkNativeBridge() {}
void UnloadNativeBridge() {}
uint32_t NativeBridgeGetVersion() { return 0; }
void* NativeBridgeGetSignalHandler(int /*signal*/) { return nullptr; }

// ============================================================
// 3. heapprofd / profiling stubs
// ============================================================
// Heap profiling daemon client - not available on OH

bool AHeapProfileEnableCallbackRegister(void (*/*callback*/)(void*, const void*, bool)) {
    return false;
}

void AHeapProfile_reportAllocation(uint32_t /*heap_id*/, uint64_t /*id*/,
                                    uint64_t /*size*/) {}
void AHeapProfile_reportFree(uint32_t /*heap_id*/, uint64_t /*id*/) {}
uint32_t AHeapProfile_registerHeap(const char* /*heap_name*/) { return 0; }

// ============================================================
// 4. libarttools stubs
// ============================================================
// Process management utilities

int GetDex2OatPid() { return -1; }

// ============================================================
// 5. Metrics / reporting stubs
// ============================================================

void android_report_metric(const char* /*name*/, int64_t /*value*/) {}

// ============================================================
// 6. selinux stubs (ART uses these for file context)
// ============================================================
// OH has its own MAC (not SELinux), these are no-ops

int selinux_android_setcontext(unsigned int /*uid*/, bool /*isSystemServer*/,
                                const char* /*seInfo*/, const char* /*pkgname*/) {
    return 0;
}

int selinux_log_callback(int /*type*/, const char* /*fmt*/, ...) {
    return 0;
}

// ============================================================
// 7. tombstone / crash reporting stubs
// ============================================================

void engrave_tombstone_ucontext(int /*pid*/, int /*tid*/, int /*signal*/,
                                 int /*si_code*/, void* /*ucontext*/) {}

void tombstoned_connect(int /*pid*/, void** /*out*/, int* /*fd*/) {}

// ============================================================
// 8. meminfo stubs
// ============================================================

int android_mallopt(int /*opcode*/, void* /*arg*/, size_t /*arg_size*/) {
    return 0;
}

// ============================================================
// 9. odrefresh / statsd stubs (Android-specific, not on OH)
// ============================================================
} // end extern "C"

#include <string>
namespace odrefresh {
bool UploadStatsIfAvailable(std::string* /*err*/) { return true; }
}

namespace art { namespace metrics {
void ReportDeviceMetrics() {}
}}

extern "C" {

// ============================================================
// 10. ARM quick entrypoints — provided by quick_entrypoints_arm.S
// ============================================================
// Real ARM32 assembly entrypoints are compiled from
// art/runtime/arch/arm/quick_entrypoints_arm.S and jni_entrypoints_arm.S
// and linked into libart.so. Weak C fallback stubs are provided below.

// All quick/jni stubs below use __attribute__((weak)) so the real
// implementations from quick_entrypoints_arm.S take priority when
// both are linked into libart.so.
#if 0 // Disabled: re-enabled below with weak attribute

// --- Hidden symbols (13, originally provided) ---
QUICK_STUB_HIDDEN(art_quick_proxy_invoke_handler)
QUICK_STUB_HIDDEN(art_quick_resolution_trampoline)
QUICK_STUB_HIDDEN(art_quick_generic_jni_trampoline)
QUICK_STUB_HIDDEN(art_quick_to_interpreter_bridge)
QUICK_STUB_HIDDEN(art_quick_imt_conflict_trampoline)
QUICK_STUB_HIDDEN(art_quick_deoptimize_from_compiled_code)
QUICK_STUB_HIDDEN(art_quick_method_entry_hook)
QUICK_STUB_HIDDEN(art_quick_method_exit_hook)
QUICK_STUB_HIDDEN(art_quick_compile_optimized)
QUICK_STUB_HIDDEN(art_quick_string_builder_append)
QUICK_STUB_HIDDEN(art_invoke_obsolete_method_stub)
QUICK_STUB_HIDDEN(art_jni_dlsym_lookup_stub)
QUICK_STUB_HIDDEN(art_jni_dlsym_lookup_critical_stub)

// --- Interpreter entrypoints ---
// Removed 2026-04-15: stubs for ExecuteNterpImpl / ExecuteNterpWithClinitImpl
// / ExecuteSwitchImplAsm and the 32KB artNterpAsmInstructionStart/End block.
// libart now ships the real ARM nterp dispatcher (nterp_arm.S generated by
// gen_mterp.py + included via cross_compile_arm32.sh's ASM_STUBS). Keeping
// these as GLOBAL stubs in libart_runtime_stubs.so caused dynamic symbol
// resolution to bind ExecuteNterpImpl to the no-op `bx lr` here instead of
// the real implementation in libart.so (which is `t` hidden), producing
// an infinite re-dispatch loop that exhausted the 128MB worker stack.
QUICK_STUB(NterpGetInstanceFieldOffset)
QUICK_STUB(NterpGetMethod)
QUICK_STUB(NterpGetStaticField)
QUICK_STUB(GetArmInfo)
QUICK_STUB(artCheckForArmSdivInstruction)
QUICK_STUB(artCheckForArmv8AInstructions)

// --- JNI entrypoints ---
QUICK_STUB(art_jni_lock_object)
QUICK_STUB(art_jni_lock_object_no_inline)
QUICK_STUB(art_jni_unlock_object)
QUICK_STUB(art_jni_unlock_object_no_inline)
QUICK_STUB(art_jni_method_start)
QUICK_STUB(art_jni_method_end)
QUICK_STUB(art_jni_method_entry_hook)
QUICK_STUB(art_jni_monitored_method_start)
QUICK_STUB(art_jni_monitored_method_end)
QUICK_STUB(art_jni_read_barrier)

// --- Exception/control flow ---
QUICK_STUB(art_quick_deliver_exception)
QUICK_STUB(art_quick_do_long_jump)
QUICK_STUB(art_quick_test_suspend)
QUICK_STUB(art_quick_implicit_suspend)
QUICK_STUB(art_quick_osr_stub)
QUICK_STUB(art_quick_invoke_stub_internal)

// --- Throw entrypoints ---
QUICK_STUB(art_quick_throw_array_bounds)
QUICK_STUB(art_quick_throw_div_zero)
QUICK_STUB(art_quick_throw_null_pointer_exception)
QUICK_STUB(art_quick_throw_null_pointer_exception_from_signal)
QUICK_STUB(art_quick_throw_stack_overflow)
QUICK_STUB(art_quick_throw_string_bounds)

// --- Type/method resolution ---
QUICK_STUB(art_quick_check_instance_of)
QUICK_STUB(art_quick_initialize_static_storage)
QUICK_STUB(art_quick_resolve_string)
QUICK_STUB(art_quick_resolve_type)
QUICK_STUB(art_quick_resolve_type_and_verify_access)
QUICK_STUB(art_quick_resolve_method_handle)
QUICK_STUB(art_quick_resolve_method_type)
QUICK_STUB(art_quick_invoke_custom)
QUICK_STUB(art_quick_invoke_polymorphic)

// --- Lock ---
QUICK_STUB(art_quick_lock_object)
QUICK_STUB(art_quick_lock_object_no_inline)
QUICK_STUB(art_quick_unlock_object)
QUICK_STUB(art_quick_unlock_object_no_inline)

// --- Invoke trampolines ---
QUICK_STUB(art_quick_invoke_direct_trampoline_with_access_check)
QUICK_STUB(art_quick_invoke_interface_trampoline_with_access_check)
QUICK_STUB(art_quick_invoke_static_trampoline_with_access_check)
QUICK_STUB(art_quick_invoke_super_trampoline_with_access_check)
QUICK_STUB(art_quick_invoke_virtual_trampoline_with_access_check)

// --- Field get/set ---
QUICK_STUB(art_quick_get_boolean_instance) QUICK_STUB(art_quick_get_boolean_static)
QUICK_STUB(art_quick_get_byte_instance) QUICK_STUB(art_quick_get_byte_static)
QUICK_STUB(art_quick_get_char_instance) QUICK_STUB(art_quick_get_char_static)
QUICK_STUB(art_quick_get_short_instance) QUICK_STUB(art_quick_get_short_static)
QUICK_STUB(art_quick_get32_instance) QUICK_STUB(art_quick_get32_static)
QUICK_STUB(art_quick_get64_instance) QUICK_STUB(art_quick_get64_static)
QUICK_STUB(art_quick_get_obj_instance) QUICK_STUB(art_quick_get_obj_static)
QUICK_STUB(art_quick_set8_instance) QUICK_STUB(art_quick_set8_static)
QUICK_STUB(art_quick_set16_instance) QUICK_STUB(art_quick_set16_static)
QUICK_STUB(art_quick_set32_instance) QUICK_STUB(art_quick_set32_static)
QUICK_STUB(art_quick_set64_instance) QUICK_STUB(art_quick_set64_static)
QUICK_STUB(art_quick_set_obj_instance) QUICK_STUB(art_quick_set_obj_static)

// --- Misc ---
QUICK_STUB(art_quick_aput_obj)
QUICK_STUB(art_quick_indexof)
QUICK_STUB(art_quick_update_inline_cache)
QUICK_STUB(art_quick_read_barrier_mark_reg00)
QUICK_STUB(art_quick_read_barrier_mark_reg01)
QUICK_STUB(art_quick_read_barrier_mark_reg02)
QUICK_STUB(art_quick_read_barrier_mark_reg03)
QUICK_STUB(art_quick_read_barrier_mark_reg04)
QUICK_STUB(art_quick_read_barrier_mark_reg05)
QUICK_STUB(art_quick_read_barrier_mark_reg06)
QUICK_STUB(art_quick_read_barrier_mark_reg07)
QUICK_STUB(art_quick_read_barrier_mark_reg08)
QUICK_STUB(art_quick_read_barrier_mark_reg09)
QUICK_STUB(art_quick_read_barrier_mark_reg10)
QUICK_STUB(art_quick_read_barrier_mark_reg11)

// --- Math ---
QUICK_STUB(art_quick_d2l) QUICK_STUB(art_quick_f2l)
QUICK_STUB(art_quick_l2f) QUICK_STUB(art_quick_mul_long)
QUICK_STUB(art_quick_shl_long) QUICK_STUB(art_quick_shr_long)
QUICK_STUB(art_quick_ushr_long) QUICK_STUB(art_quick_fmod)
QUICK_STUB(art_quick_fmodf)

// --- Allocator entrypoints (6 allocators × object/array/string variants × instrumented) ---
#define ALLOC_STUBS(allocator) \
    QUICK_STUB(art_quick_alloc_object_resolved_ ## allocator) \
    QUICK_STUB(art_quick_alloc_object_resolved_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_object_initialized_ ## allocator) \
    QUICK_STUB(art_quick_alloc_object_initialized_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_object_with_checks_ ## allocator) \
    QUICK_STUB(art_quick_alloc_object_with_checks_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved8_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved8_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved16_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved16_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved32_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved32_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved64_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved64_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_string_object_ ## allocator) \
    QUICK_STUB(art_quick_alloc_string_object_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_string_from_bytes_ ## allocator) \
    QUICK_STUB(art_quick_alloc_string_from_bytes_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_string_from_chars_ ## allocator) \
    QUICK_STUB(art_quick_alloc_string_from_chars_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_string_from_string_ ## allocator) \
    QUICK_STUB(art_quick_alloc_string_from_string_ ## allocator ## _instrumented)

ALLOC_STUBS(dlmalloc)
ALLOC_STUBS(rosalloc)
ALLOC_STUBS(bump_pointer)
ALLOC_STUBS(tlab)
ALLOC_STUBS(region)
ALLOC_STUBS(region_tlab)

#undef ALLOC_STUBS
#undef QUICK_STUB
#undef QUICK_STUB_HIDDEN
#endif // disabled block

// nterp interpreter table: REMOVED 2026-04-15.
// The previous __asm__ block defined ExecuteNterpImpl / ExecuteNterpWithClinitImpl
// / artNterpAsmInstructionStart / artNterpAsmInstructionEnd as GLOBAL `bx lr`
// placeholders plus a 32KB zero block, to satisfy CheckNterpAsmConstants() when
// nterp was excluded from the libart build. Since libart now includes the real
// nterp_arm.S (generated by gen_mterp.py, assembled via cross_compile_arm32.sh
// ASM_STUBS), these globals collided with libart's own hidden symbols: the
// dynamic loader preferred our GLOBAL `bx lr` over libart's hidden real
// implementation, producing an infinite re-dispatch loop during boot image
// initialization. Left as comment for history.

// Re-enable ALL stubs as weak symbols so .S file takes priority
#define QUICK_STUB_HIDDEN(name) \
    __attribute__((visibility("hidden"), weak)) void name() { /* weak no-op */ }
#define QUICK_STUB(name) \
    __attribute__((weak)) void name() { /* weak no-op */ }

// --- Hidden symbols ---
QUICK_STUB_HIDDEN(art_quick_proxy_invoke_handler)
QUICK_STUB_HIDDEN(art_quick_resolution_trampoline)
QUICK_STUB_HIDDEN(art_quick_generic_jni_trampoline)
QUICK_STUB_HIDDEN(art_quick_to_interpreter_bridge)
QUICK_STUB_HIDDEN(art_quick_imt_conflict_trampoline)
QUICK_STUB_HIDDEN(art_quick_deoptimize_from_compiled_code)
QUICK_STUB_HIDDEN(art_quick_method_entry_hook)
QUICK_STUB_HIDDEN(art_quick_method_exit_hook)
QUICK_STUB_HIDDEN(art_quick_compile_optimized)
QUICK_STUB_HIDDEN(art_quick_string_builder_append)
QUICK_STUB_HIDDEN(art_invoke_obsolete_method_stub)

// --- Interpreter/helper entrypoints ---
// ExecuteNterpImpl / ExecuteNterpWithClinitImpl are provided as strong labels
// at artNterpAsmInstructionStart (see asm block above); do not redefine here.
QUICK_STUB(ExecuteSwitchImplAsm)
QUICK_STUB(NterpGetInstanceFieldOffset)
QUICK_STUB(NterpGetMethod)
QUICK_STUB(NterpGetStaticField)
QUICK_STUB(GetArmInfo)
QUICK_STUB(artCheckForArmSdivInstruction)
QUICK_STUB(artCheckForArmv8AInstructions)

// --- JNI entrypoints ---
QUICK_STUB(art_jni_lock_object)
QUICK_STUB(art_jni_lock_object_no_inline)
QUICK_STUB(art_jni_unlock_object)
QUICK_STUB(art_jni_unlock_object_no_inline)
QUICK_STUB(art_jni_method_start)
QUICK_STUB(art_jni_method_end)
QUICK_STUB(art_jni_method_entry_hook)
QUICK_STUB(art_jni_monitored_method_start)
QUICK_STUB(art_jni_monitored_method_end)
QUICK_STUB(art_jni_read_barrier)

// --- Exception/control flow ---
QUICK_STUB(art_quick_deliver_exception)
QUICK_STUB(art_quick_do_long_jump)
QUICK_STUB(art_quick_test_suspend)
QUICK_STUB(art_quick_implicit_suspend)
QUICK_STUB(art_quick_osr_stub)
QUICK_STUB(art_quick_invoke_stub_internal)

// --- Throw entrypoints ---
QUICK_STUB(art_quick_throw_array_bounds)
QUICK_STUB(art_quick_throw_div_zero)
QUICK_STUB(art_quick_throw_null_pointer_exception)
QUICK_STUB(art_quick_throw_null_pointer_exception_from_signal)
QUICK_STUB(art_quick_throw_stack_overflow)
QUICK_STUB(art_quick_throw_string_bounds)

// --- Type/method resolution ---
QUICK_STUB(art_quick_check_instance_of)
QUICK_STUB(art_quick_initialize_static_storage)
QUICK_STUB(art_quick_resolve_string)
QUICK_STUB(art_quick_resolve_type)
QUICK_STUB(art_quick_resolve_type_and_verify_access)
QUICK_STUB(art_quick_resolve_method_handle)
QUICK_STUB(art_quick_resolve_method_type)
QUICK_STUB(art_quick_invoke_custom)
QUICK_STUB(art_quick_invoke_polymorphic)

// --- Lock ---
QUICK_STUB(art_quick_lock_object) QUICK_STUB(art_quick_lock_object_no_inline)
QUICK_STUB(art_quick_unlock_object) QUICK_STUB(art_quick_unlock_object_no_inline)

// --- Field get/set ---
QUICK_STUB(art_quick_get_boolean_instance) QUICK_STUB(art_quick_get_boolean_static)
QUICK_STUB(art_quick_get_byte_instance) QUICK_STUB(art_quick_get_byte_static)
QUICK_STUB(art_quick_get_char_instance) QUICK_STUB(art_quick_get_char_static)
QUICK_STUB(art_quick_get_short_instance) QUICK_STUB(art_quick_get_short_static)
QUICK_STUB(art_quick_get32_instance) QUICK_STUB(art_quick_get32_static)
QUICK_STUB(art_quick_get64_instance) QUICK_STUB(art_quick_get64_static)
QUICK_STUB(art_quick_get_obj_instance) QUICK_STUB(art_quick_get_obj_static)
QUICK_STUB(art_quick_set8_instance) QUICK_STUB(art_quick_set8_static)
QUICK_STUB(art_quick_set16_instance) QUICK_STUB(art_quick_set16_static)
QUICK_STUB(art_quick_set32_instance) QUICK_STUB(art_quick_set32_static)
QUICK_STUB(art_quick_set64_instance) QUICK_STUB(art_quick_set64_static)
QUICK_STUB(art_quick_set_obj_instance) QUICK_STUB(art_quick_set_obj_static)

// --- Invoke trampolines ---
QUICK_STUB(art_quick_invoke_direct_trampoline_with_access_check)
QUICK_STUB(art_quick_invoke_interface_trampoline_with_access_check)
QUICK_STUB(art_quick_invoke_static_trampoline_with_access_check)
QUICK_STUB(art_quick_invoke_super_trampoline_with_access_check)
QUICK_STUB(art_quick_invoke_virtual_trampoline_with_access_check)

// --- Misc + math + read barrier marks + allocators ---
QUICK_STUB(art_quick_aput_obj) QUICK_STUB(art_quick_indexof)
QUICK_STUB(art_quick_update_inline_cache)
QUICK_STUB(art_quick_read_barrier_mark_reg00) QUICK_STUB(art_quick_read_barrier_mark_reg01)
QUICK_STUB(art_quick_read_barrier_mark_reg02) QUICK_STUB(art_quick_read_barrier_mark_reg03)
QUICK_STUB(art_quick_read_barrier_mark_reg04) QUICK_STUB(art_quick_read_barrier_mark_reg05)
QUICK_STUB(art_quick_read_barrier_mark_reg06) QUICK_STUB(art_quick_read_barrier_mark_reg07)
QUICK_STUB(art_quick_read_barrier_mark_reg08) QUICK_STUB(art_quick_read_barrier_mark_reg09)
QUICK_STUB(art_quick_read_barrier_mark_reg10) QUICK_STUB(art_quick_read_barrier_mark_reg11)
QUICK_STUB(art_quick_d2l) QUICK_STUB(art_quick_f2l)
QUICK_STUB(art_quick_l2f) QUICK_STUB(art_quick_mul_long)
QUICK_STUB(art_quick_shl_long) QUICK_STUB(art_quick_shr_long)
QUICK_STUB(art_quick_ushr_long) QUICK_STUB(art_quick_fmod) QUICK_STUB(art_quick_fmodf)

#define ALLOC_STUBS(allocator) \
    QUICK_STUB(art_quick_alloc_object_resolved_ ## allocator) \
    QUICK_STUB(art_quick_alloc_object_resolved_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_object_initialized_ ## allocator) \
    QUICK_STUB(art_quick_alloc_object_initialized_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_object_with_checks_ ## allocator) \
    QUICK_STUB(art_quick_alloc_object_with_checks_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved8_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved8_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved16_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved16_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved32_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved32_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_array_resolved64_ ## allocator) \
    QUICK_STUB(art_quick_alloc_array_resolved64_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_string_object_ ## allocator) \
    QUICK_STUB(art_quick_alloc_string_object_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_string_from_bytes_ ## allocator) \
    QUICK_STUB(art_quick_alloc_string_from_bytes_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_string_from_chars_ ## allocator) \
    QUICK_STUB(art_quick_alloc_string_from_chars_ ## allocator ## _instrumented) \
    QUICK_STUB(art_quick_alloc_string_from_string_ ## allocator) \
    QUICK_STUB(art_quick_alloc_string_from_string_ ## allocator ## _instrumented)
ALLOC_STUBS(dlmalloc) ALLOC_STUBS(rosalloc) ALLOC_STUBS(bump_pointer)
ALLOC_STUBS(tlab) ALLOC_STUBS(region) ALLOC_STUBS(region_tlab)
#undef ALLOC_STUBS
#undef QUICK_STUB
#undef QUICK_STUB_HIDDEN

// ============================================================
// 11. Truly unresolved ART symbols (73) — from excluded files
//     and libartbase/libdexfile symbols not in cross-compiled versions.
//     These are C++-mangled names, provided as extern "C" trap stubs
//     to satisfy RTLD_NOW loading.
// ============================================================
// NOOP_STUB instead of TRAP_STUB — ART Runtime::Init calls many of these
#define TRAP_STUB(mangled) void mangled() { /* no-op stub */ }

// --- ProfileCompilationInfo (profile/JIT, not needed for interpreter mode) ---
TRAP_STUB(_ZN3art22ProfileCompilationInfo10AddMethodsERKNSt3__h6vectorINS_17ProfileMethodInfoENS1_9allocatorIS3_EEEENS0_13MethodHotness4FlagERKNS0_23ProfileSampleAnnotationE)
TRAP_STUB(_ZN3art22ProfileCompilationInfo11DexFileData9AddMethodENS0_13MethodHotness4FlagEj)
TRAP_STUB(_ZN3art22ProfileCompilationInfo19GetOrAddDexFileDataERKNSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEjjj)
TRAP_STUB(_ZN3art22ProfileCompilationInfo21FindOrCreateTypeIndexERKNS_7DexFileEPKc)
TRAP_STUB(_ZN3art22ProfileCompilationInfo24GetProfileDexFileBaseKeyERKNSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEE)
TRAP_STUB(_ZN3art22ProfileCompilationInfo24ProfileFilterFnAcceptAllERKNSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEj)
TRAP_STUB(_ZN3art22ProfileCompilationInfo29GetProfileDexFileAugmentedKeyERKNSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEERKNS0_23ProfileSampleAnnotationE)
TRAP_STUB(_ZN3art22ProfileCompilationInfo4LoadEibRKNSt3__h8functionIFbRKNS1_12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEjEEE)
TRAP_STUB(_ZN3art22ProfileCompilationInfo4LoadERKNSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEb)
TRAP_STUB(_ZN3art22ProfileCompilationInfo4SaveERKNSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEPy)
TRAP_STUB(_ZN3art22ProfileCompilationInfo9ClearDataEv)
TRAP_STUB(_ZN3art22ProfileCompilationInfo9MergeWithERKS0_b)
TRAP_STUB(_ZN3art22ProfileCompilationInfoC1Eb)
TRAP_STUB(_ZN3art22ProfileCompilationInfoC1EPNS_9ArenaPoolEb)
TRAP_STUB(_ZN3art22ProfileCompilationInfoD1Ev)
TRAP_STUB(_ZN3art15ProfileBootInfo4LoadEiRKNSt3__h6vectorIPKNS_7DexFileENS1_9allocatorIS5_EEEE)

// --- InstructionSetFeatures (non-ARM32 architectures, never called on ARM32) ---
TRAP_STUB(_ZN3art25X86InstructionSetFeatures10FromBitmapEjb)
TRAP_STUB(_ZN3art25X86InstructionSetFeatures11FromVariantERKNSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEPS7_b)
TRAP_STUB(_ZN3art27Arm64InstructionSetFeatures10FromBitmapEj)
TRAP_STUB(_ZN3art27Arm64InstructionSetFeatures11FromVariantERKNSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEPS7_)
TRAP_STUB(_ZN3art29Riscv64InstructionSetFeatures10FromBitmapEj)
TRAP_STUB(_ZN3art29Riscv64InstructionSetFeatures11FromVariantERKNSt3__h12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEPS7_)
// InstructionSet utilities — real implementations needed during Runtime::Init
} // end extern "C" (temporarily close for C++ code)

namespace art {
enum InstructionSet { kNone = 0, kArm = 1, kArm64 = 2, kX86 = 3, kX86_64 = 4, kRiscv64 = 5 };
const char* GetInstructionSetString(InstructionSet isa) {
    switch (isa) {
        case kArm: return "arm";
        case kArm64: return "arm64";
        case kX86: return "x86";
        case kX86_64: return "x86_64";
        case kRiscv64: return "riscv64";
        default: return "none";
    }
}
InstructionSet GetInstructionSetFromString(const char* s) {
    if (!s) return kNone;
    if (strcmp(s, "arm") == 0 || strcmp(s, "arm32") == 0) return kArm;
    if (strcmp(s, "arm64") == 0) return kArm64;
    if (strcmp(s, "x86") == 0) return kX86;
    if (strcmp(s, "x86_64") == 0) return kX86_64;
    if (strcmp(s, "riscv64") == 0) return kRiscv64;
    return kNone;
}
void GetSupportedInstructionSets(std::string* s) { if (s) *s = "arm"; }
void InstructionSetAbort(InstructionSet isa) {
    fprintf(stderr, "InstructionSetAbort: %s\n", GetInstructionSetString(isa));
}
} // namespace art

extern "C" {

// --- MarkCompact GC (excluded, uses UFFD not available on ARM32) ---
TRAP_STUB(_ZN3art2gc9collector11MarkCompact13SigbusHandlerEP9siginfo_t)
// MarkCompact::GetUffdAndMinorFault() - return {false, false} (no uffd support)
// Mangled: _ZN3art2gc9collector11MarkCompact20GetUffdAndMinorFaultEv
// Returns std::pair<bool, bool> - on ARM32 this is returned via registers r0,r1
struct BoolPair { unsigned char first; unsigned char second; };
struct BoolPair _ZN3art2gc9collector11MarkCompact20GetUffdAndMinorFaultEv(void) {
    struct BoolPair p = {0, 0};
    return p;
}
TRAP_STUB(_ZN3art2gc9collector11MarkCompactC1EPNS0_4HeapE)

// --- Metrics/statsd (excluded, Android-specific) ---
// REMOVED (real impl in metrics_common.cc now): TRAP_STUB(_ZN3art7metrics10ArtMetrics14DumpForSigQuitERNSt3__h13basic_ostreamIcNS2_11char_traitsIcEEEE)
// REMOVED (real impl in metrics_common.cc now): TRAP_STUB(_ZN3art7metrics10ArtMetrics36ReportAllMetricsAndResetValueMetricsERKNSt3__h6vectorIPNS0_14MetricsBackendENS2_9allocatorIS5_EEEE)
// REMOVED (real impl in metrics_common.cc now): TRAP_STUB(_ZN3art7metrics10ArtMetrics5ResetEv)
// REMOVED (real impl in metrics_common.cc now): TRAP_STUB(_ZN3art7metrics10ArtMetricsC1Ev)
TRAP_STUB(_ZN3art7metrics10LogBackendC1ENSt3__h10unique_ptrINS0_16MetricsFormatterENS2_14default_deleteIS4_EEEEN7android4base11LogSeverityE)
TRAP_STUB(_ZN3art7metrics11FileBackendC1ENSt3__h10unique_ptrINS0_16MetricsFormatterENS2_14default_deleteIS4_EEEERKNS2_12basic_stringIcNS2_11char_traitsIcEENS2_9allocatorIcEEEE)
TRAP_STUB(_ZN3art7metrics11SessionData13CreateDefaultEv)

// --- Misc utility ---
TRAP_STUB(_ZN3art12XzDecompressENS_8ArrayRefIKhEEPNSt3__h6vectorIhNS3_9allocatorIhEEEE)

// --- operator<< (debug printing, never critical path) ---
TRAP_STUB(_ZN3art15instrumentationlsERNSt3__h13basic_ostreamIcNS1_11char_traitsIcEEEENS0_15Instrumentation20InstrumentationLevelE)
TRAP_STUB(_ZN3art2gc5spacelsERNSt3__h13basic_ostreamIcNS2_11char_traitsIcEEEENS1_11RegionSpace10RegionTypeE)
TRAP_STUB(_ZN3art2gc5spacelsERNSt3__h13basic_ostreamIcNS2_11char_traitsIcEEEENS1_11RegionSpace11RegionStateE)
TRAP_STUB(_ZN3art2gc5spacelsERNSt3__h13basic_ostreamIcNS2_11char_traitsIcEEEENS1_17GcRetentionPolicyE)
TRAP_STUB(_ZN3art2gc5spacelsERNSt3__h13basic_ostreamIcNS2_11char_traitsIcEEEENS1_9SpaceTypeE)
TRAP_STUB(_ZN3art2gc9allocatorlsERNSt3__h13basic_ostreamIcNS2_11char_traitsIcEEEENS1_8RosAlloc11PageMapKindE)
TRAP_STUB(_ZN3art2gc9collectorlsERNSt3__h13basic_ostreamIcNS2_11char_traitsIcEEEENS1_6GcTypeE)
TRAP_STUB(_ZN3art2gclsERNSt3__h13basic_ostreamIcNS1_11char_traitsIcEEEENS0_13AllocatorTypeE)
TRAP_STUB(_ZN3art2gclsERNSt3__h13basic_ostreamIcNS1_11char_traitsIcEEEENS0_13CollectorTypeE)
TRAP_STUB(_ZN3art2gclsERNSt3__h13basic_ostreamIcNS1_11char_traitsIcEEEENS0_13WeakRootStateE)
TRAP_STUB(_ZN3art8verifierlsERNSt3__h13basic_ostreamIcNS1_11char_traitsIcEEEENS0_10MethodTypeE)
TRAP_STUB(_ZN3art8verifierlsERNSt3__h13basic_ostreamIcNS1_11char_traitsIcEEEENS0_11FailureKindE)
TRAP_STUB(_ZN3art8verifierlsERNSt3__h13basic_ostreamIcNS1_11char_traitsIcEEEENS0_11VerifyErrorE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_10InvokeTypeE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_11ClassStatusE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_11ImageHeader11ImageMethodE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_11ImageHeader11StorageModeE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_11ImageHeader13ImageSectionsE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_11Instruction6FormatE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_11ThreadStateE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_12JdwpProviderE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_12OatClassTypeE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_13SuspendReasonE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_14InstructionSetE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_15CompilationKindE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_15IndirectRefKindE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_15LinearAllocKindE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_20ReflectionSourceTypeE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_25EncodedArrayValueIterator9ValueTypeE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_8LockWord9LockStateE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_8RootTypeE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_8StubTypeE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_8VRegKindE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_9JniIdTypeE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_9LockLevelE)

// --- Static data member (not a function, needs storage) ---
// ProfileSampleAnnotation::kNone is a static const object
char _ZN3art22ProfileCompilationInfo23ProfileSampleAnnotation5kNoneE[64] = {0};

#undef TRAP_STUB

} // extern "C"

// ============================================================
// 12. C++-mangled stubs (namespace symbols required by libart.so)
// ============================================================
// These symbols are referenced via C++ mangled names. They must be
// defined outside extern "C" with proper namespace/class signatures.

#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <memory>
#include <new>
#include <optional>

// Helper: trap stub for functions that should never be called at runtime
#define TRAP_STUB_VIS __attribute__((visibility("default")))

// JNI opaque types (matching mangled names: _JNIEnv, _jobject, _jstring)
struct _JNIEnv;
struct _jobject;
struct _jstring;

// ------------------------------------------------------------
// 12a. NativeLoader (android:: namespace, C++ mangled)
// ------------------------------------------------------------
namespace android {

TRAP_STUB_VIS void* OpenNativeLibrary(
    _JNIEnv* env, int32_t target_sdk_version, const char* path,
    _jobject* class_loader, const char* caller_location,
    _jstring* library_path, bool* needs_native_bridge, char** error_msg) {
    // Mangled: _ZN7android17OpenNativeLibraryEP7_JNIEnviPKcP8_jobjectS3_P8_jstringPbPPc
    (void)env; (void)target_sdk_version; (void)class_loader;
    (void)caller_location; (void)library_path;
    if (needs_native_bridge) *needs_native_bridge = false;
    void* handle = dlopen(path, RTLD_NOW);
    if (!handle && error_msg) {
        const char* err = dlerror();
        *error_msg = err ? strdup(err) : strdup("unknown dlopen error");
    }
    return handle;
}

TRAP_STUB_VIS bool CloseNativeLibrary(void* handle, bool needs_native_bridge, char** error_msg) {
    (void)needs_native_bridge;
    if (dlclose(handle) != 0) {
        if (error_msg) {
            const char* err = dlerror();
            *error_msg = err ? strdup(err) : strdup("dlclose failed");
        }
        return false;
    }
    return true;
}

TRAP_STUB_VIS void NativeLoaderFreeErrorMessage(char* msg) { free(msg); }
TRAP_STUB_VIS void InitializeNativeLoader() {}
TRAP_STUB_VIS void ResetNativeLoader() {}

} // namespace android

// ------------------------------------------------------------
// 12b. unwindstack:: namespace (stack unwinding stubs)
// ------------------------------------------------------------
namespace unwindstack {

// Forward declarations for types used in signatures
class Maps {
public:
    __attribute__((weak)) virtual ~Maps();
};
__attribute__((weak)) Maps::~Maps() {} // weak: real dtor in libartbase.so

class LocalUpdatableMaps : public Maps {
public:
    TRAP_STUB_VIS LocalUpdatableMaps();
    TRAP_STUB_VIS bool Parse();
    TRAP_STUB_VIS bool Reparse(bool* updated);
};
LocalUpdatableMaps::LocalUpdatableMaps() {}
bool LocalUpdatableMaps::Parse() { return false; }
bool LocalUpdatableMaps::Reparse(bool*) { return false; }

enum ArchEnum : uint8_t { ARCH_UNKNOWN = 0 };

class Regs {
public:
    TRAP_STUB_VIS static ArchEnum CurrentArch();
    TRAP_STUB_VIS static Regs* CreateFromLocal();
};
ArchEnum Regs::CurrentArch() { return ARCH_UNKNOWN; }
Regs* Regs::CreateFromLocal() { return nullptr; }

class Memory {
public:
    TRAP_STUB_VIS static std::shared_ptr<Memory> CreateProcessMemoryThreadCached(pid_t pid);
};
std::shared_ptr<Memory> Memory::CreateProcessMemoryThreadCached(pid_t) { return nullptr; }

class JitDebug;
class DexFiles;

TRAP_STUB_VIS JitDebug* CreateJitDebug(ArchEnum, std::shared_ptr<Memory>&, void*) { return nullptr; }
TRAP_STUB_VIS DexFiles* CreateDexFiles(ArchEnum, std::shared_ptr<Memory>&, void*) { return nullptr; }

class Elf {
public:
    TRAP_STUB_VIS static void SetCachingEnabled(bool enabled);
};
void Elf::SetCachingEnabled(bool) {}

struct FrameData {
    uint64_t pc;
    const char* function_name;
};

class AndroidUnwinderData {
public:
    TRAP_STUB_VIS void DemangleFunctionNames();
    TRAP_STUB_VIS std::string GetErrorString();
};
void AndroidUnwinderData::DemangleFunctionNames() {}
std::string AndroidUnwinderData::GetErrorString() { return "unwinding not supported on OH"; }

class Unwinder {
public:
    virtual ~Unwinder();  // out-of-line to anchor vtable in this TU
    TRAP_STUB_VIS void SetJitDebug(JitDebug*);
    TRAP_STUB_VIS void SetDexFiles(DexFiles*);
};
Unwinder::~Unwinder() {}
void Unwinder::SetJitDebug(JitDebug*) {}
void Unwinder::SetDexFiles(DexFiles*) {}

class MapInfo {
public:
    struct ElfFields {};
    TRAP_STUB_VIS ElfFields* GetElfFields();
    TRAP_STUB_VIS std::string GetPrintableBuildID();
};
MapInfo::ElfFields* MapInfo::GetElfFields() { return nullptr; }
std::string MapInfo::GetPrintableBuildID() { return ""; }

class AndroidUnwinder {
public:
    TRAP_STUB_VIS bool Unwind(void* ucontext, AndroidUnwinderData& data);
    TRAP_STUB_VIS bool Unwind(std::optional<int> tid, AndroidUnwinderData& data);
};
bool AndroidUnwinder::Unwind(void*, AndroidUnwinderData&) { return false; }
bool AndroidUnwinder::Unwind(std::optional<int>, AndroidUnwinderData&) { return false; }

class AndroidLocalUnwinder : public AndroidUnwinder {
public:
    virtual ~AndroidLocalUnwinder();  // out-of-line to anchor vtable
};
AndroidLocalUnwinder::~AndroidLocalUnwinder() {}

} // namespace unwindstack

// ------------------------------------------------------------
// 12c. unix_file::FdFile (real implementations for dex loading)
// ------------------------------------------------------------
namespace unix_file {

class FdFile {
public:
    TRAP_STUB_VIS FdFile();
    TRAP_STUB_VIS FdFile(int fd, bool check_usage);
    TRAP_STUB_VIS FdFile(FdFile&& other);
    TRAP_STUB_VIS FdFile& operator=(FdFile&& other);
    TRAP_STUB_VIS FdFile(int fd, const std::string& path, bool check_usage);
    TRAP_STUB_VIS FdFile(int fd, const std::string& path, bool check_usage, bool read_only_mode);
    TRAP_STUB_VIS FdFile(const std::string& path, int flags, unsigned int mode, bool check_usage);
    TRAP_STUB_VIS virtual ~FdFile();

    TRAP_STUB_VIS int Fd() const;
    TRAP_STUB_VIS bool IsOpened() const;
    TRAP_STUB_VIS bool Close();
    TRAP_STUB_VIS bool Flush();
    TRAP_STUB_VIS bool ClearContent();
    TRAP_STUB_VIS bool Erase(bool unlink);
    TRAP_STUB_VIS int Release();
    TRAP_STUB_VIS bool FlushClose();
    TRAP_STUB_VIS bool FlushCloseOrErase();
    TRAP_STUB_VIS bool WriteFully(const void* data, unsigned int size);
    TRAP_STUB_VIS bool Write(const char* data, int64_t offset, int64_t length);
    TRAP_STUB_VIS int64_t Read(char* buf, int64_t offset, int64_t length) const;
    TRAP_STUB_VIS bool PwriteFully(const void* data, unsigned int size, unsigned int offset);
    TRAP_STUB_VIS bool PreadFully(void* buf, unsigned int size, unsigned int offset);
    TRAP_STUB_VIS bool SetLength(int64_t length);
    TRAP_STUB_VIS int64_t GetLength() const;
    TRAP_STUB_VIS void MarkUnchecked();
    TRAP_STUB_VIS bool Unlink();

private:
    int fd_;
    std::string path_;
    bool read_only_;
    bool check_usage_;
};

FdFile::FdFile() : fd_(-1), read_only_(false), check_usage_(false) {}

FdFile::FdFile(int fd, bool check_usage)
    : fd_(fd), read_only_(false), check_usage_(check_usage) {}

FdFile::FdFile(FdFile&& other)
    : fd_(other.fd_), path_(std::move(other.path_)),
      read_only_(other.read_only_), check_usage_(other.check_usage_) {
    other.fd_ = -1;
}

FdFile& FdFile::operator=(FdFile&& other) {
    if (this != &other) {
        if (fd_ >= 0 && !check_usage_) close(fd_);
        fd_ = other.fd_;
        path_ = std::move(other.path_);
        read_only_ = other.read_only_;
        check_usage_ = other.check_usage_;
        other.fd_ = -1;
    }
    return *this;
}

FdFile::FdFile(int fd, const std::string& path, bool check_usage)
    : fd_(fd), path_(path), read_only_(false), check_usage_(check_usage) {}

FdFile::FdFile(int fd, const std::string& path, bool check_usage, bool read_only_mode)
    : fd_(fd), path_(path), read_only_(read_only_mode), check_usage_(check_usage) {}

FdFile::FdFile(const std::string& path, int flags, unsigned int mode, bool check_usage)
    : path_(path), read_only_((flags & O_ACCMODE) == O_RDONLY), check_usage_(check_usage) {
    fd_ = open(path.c_str(), flags, mode);
}

FdFile::~FdFile() {
    if (fd_ >= 0 && !check_usage_) {
        close(fd_);
    }
}

int FdFile::Fd() const { return fd_; }
bool FdFile::IsOpened() const { return fd_ >= 0; }

bool FdFile::Close() {
    if (fd_ < 0) return true;
    int rc = close(fd_);
    fd_ = -1;
    return rc == 0;
}

bool FdFile::Flush() {
    if (fd_ < 0) return true;
    return fsync(fd_) == 0;
}

// Added 2026-04-14 - needed by libprofile.so (profile_compilation_info.cc).
bool FdFile::ClearContent() {
    if (fd_ < 0) return false;
    return ftruncate(fd_, 0) == 0;
}


bool FdFile::Erase(bool do_unlink) {
    if (fd_ >= 0) {
        ftruncate(fd_, 0);
        close(fd_);
        fd_ = -1;
    }
    if (do_unlink && !path_.empty()) {
        unlink(path_.c_str());
    }
    return true;
}

int FdFile::Release() {
    int old_fd = fd_;
    fd_ = -1;
    return old_fd;
}

bool FdFile::FlushClose() {
    if (fd_ < 0) return true;
    bool ok = Flush();
    ok = Close() && ok;
    return ok;
}

bool FdFile::FlushCloseOrErase() {
    if (!FlushClose()) {
        Erase(false);
        return false;
    }
    return true;
}

bool FdFile::WriteFully(const void* data, unsigned int size) {
    const uint8_t* p = static_cast<const uint8_t*>(data);
    unsigned int remaining = size;
    while (remaining > 0) {
        ssize_t n = write(fd_, p, remaining);
        if (n <= 0) return false;
        p += n;
        remaining -= n;
    }
    return true;
}

bool FdFile::Write(const char* data, int64_t offset, int64_t length) {
    while (length > 0) {
        ssize_t n = pwrite(fd_, data, length, offset);
        if (n <= 0) return false;
        data += n;
        offset += n;
        length -= n;
    }
    return true;
}

int64_t FdFile::Read(char* buf, int64_t offset, int64_t length) const {
    int64_t total = 0;
    while (length > 0) {
        ssize_t n = pread(fd_, buf, length, offset);
        if (n < 0) return -1;
        if (n == 0) break;
        buf += n;
        offset += n;
        length -= n;
        total += n;
    }
    return total;
}

bool FdFile::PwriteFully(const void* data, unsigned int size, unsigned int offset) {
    const uint8_t* p = static_cast<const uint8_t*>(data);
    unsigned int remaining = size;
    unsigned int off = offset;
    while (remaining > 0) {
        ssize_t n = pwrite(fd_, p, remaining, off);
        if (n <= 0) return false;
        p += n;
        remaining -= n;
        off += n;
    }
    return true;
}

bool FdFile::PreadFully(void* buf, unsigned int size, unsigned int offset) {
    uint8_t* p = static_cast<uint8_t*>(buf);
    unsigned int remaining = size;
    unsigned int off = offset;
    while (remaining > 0) {
        ssize_t n = pread(fd_, p, remaining, off);
        if (n <= 0) return false;
        p += n;
        remaining -= n;
        off += n;
    }
    return true;
}

bool FdFile::SetLength(int64_t length) {
    return ftruncate(fd_, length) == 0;
}

int64_t FdFile::GetLength() const {
    struct stat st;
    if (fstat(fd_, &st) != 0) return -1;
    return st.st_size;
}

void FdFile::MarkUnchecked() { check_usage_ = false; }

bool FdFile::Unlink() {
    if (path_.empty()) return false;
    return (::unlink(path_.c_str()) == 0);
}

} // namespace unix_file

// (12c2. android::base:: removed — now provided by libbase.so)

// ------------------------------------------------------------
// 12c6. Misc C stubs (cutils, palette, liblog)
// ------------------------------------------------------------
extern "C" {
// cutils/native_handle
typedef struct native_handle { int version; int numFds; int numInts; int data[0]; } native_handle_t;
TRAP_STUB_VIS int native_handle_close(const native_handle_t* h) {
    if (!h) return -1;
    for (int i = 0; i < h->numFds; i++) close(h->data[i]);
    return 0;
}
TRAP_STUB_VIS int native_handle_delete(native_handle_t* h) { free(h); return 0; }

// ART Palette (tracing)
TRAP_STUB_VIS int PaletteTraceBegin(const char* /*name*/) { return 0; }
TRAP_STUB_VIS int PaletteTraceEnd() { return 0; }

// liblog
TRAP_STUB_VIS void __android_log_assert(const char* /*cond*/, const char* /*tag*/,
                                         const char* fmt, ...) {
    if (fmt) {
        va_list ap; va_start(ap, fmt);
        vfprintf(stderr, fmt, ap); va_end(ap);
        fprintf(stderr, "\n");
    }
    fprintf(stderr, "[__android_log_assert: abort suppressed in dev mode]\n");
    // Don't abort during development
}
TRAP_STUB_VIS int __android_log_print(int /*prio*/, const char* /*tag*/,
                                       const char* fmt, ...) {
    va_list ap; va_start(ap, fmt);
    int r = vfprintf(stderr, fmt, ap);
    va_end(ap); fprintf(stderr, "\n");
    return r;
}
TRAP_STUB_VIS int __android_log_error_write(int /*tag*/, const char* /*subtag*/,
                                             int /*uid*/, const char* /*data*/,
                                             int /*data_len*/) { return 0; }
TRAP_STUB_VIS void __android_log_set_default_tag(const char* /*tag*/) {}
TRAP_STUB_VIS void __android_log_logd_logger(void* /*buf*/) {}
TRAP_STUB_VIS int __android_log_is_loggable(int /*prio*/, const char* /*tag*/,
                                             int default_prio) { return default_prio; }
TRAP_STUB_VIS int __android_log_is_loggable_len(int /*prio*/, const char* /*tag*/,
                                                 int /*len*/, int default_prio) { return default_prio; }
} // extern "C"

// (12c7. vixl::CodeBuffer removed — now provided by libvixl.so)


// ------------------------------------------------------------
// 12d. fmt::v7 (string formatting library)
// ------------------------------------------------------------
namespace fmt { namespace v7 {
    template<typename Char> class basic_string_view {
    public:
        const Char* data_;
        size_t size_;
    };
    class format_args {};
    namespace detail {
        TRAP_STUB_VIS std::string vformat(basic_string_view<char>, format_args) {
            return "";
        }
    } // namespace detail
}} // namespace fmt::v7

// ------------------------------------------------------------
// 12e. tinyxml2::XMLDocument
// ------------------------------------------------------------
namespace tinyxml2 {
    enum Whitespace { PRESERVE_WHITESPACE = 0, COLLAPSE_WHITESPACE = 1 };
    class XMLDocument {
    public:
        TRAP_STUB_VIS XMLDocument(bool processEntities = true, Whitespace ws = COLLAPSE_WHITESPACE);
        virtual ~XMLDocument() = default;
        char pad_[256]; // storage for member data
    };
    XMLDocument::XMLDocument(bool, Whitespace) { memset(pad_, 0, sizeof(pad_)); }
} // namespace tinyxml2

// ------------------------------------------------------------
// 12f. art:: namespace misc stubs (C++ mangled)
// ------------------------------------------------------------
extern "C" {
#define TRAP_STUB(mangled) \
    __attribute__((visibility("default"))) void mangled() { /* no-op stub */ }

// Arm64InstructionSetFeatures::IntersectWithHwcap() const
TRAP_STUB(_ZNK3art27Arm64InstructionSetFeatures18IntersectWithHwcapEv)

// art::jni::LocalReferenceTable::Get(void*) const
TRAP_STUB(_ZNK3art3jni19LocalReferenceTable3GetEPv)

// (ProfileCompilationInfo stubs removed — now provided by libart.so)

// unwindstack::CreateJitDebug / CreateDexFiles (complex template args)
TRAP_STUB(_ZN11unwindstack14CreateJitDebugENS_8ArchEnumERNSt3__h10shared_ptrINS_6MemoryEEENS1_6vectorINS1_12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEENSA_ISC_EEEE)
TRAP_STUB(_ZN11unwindstack14CreateDexFilesENS_8ArchEnumERNSt3__h10shared_ptrINS_6MemoryEEENS1_6vectorINS1_12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEENSA_ISC_EEEE)

// unwindstack::Unwinder::SetJitDebug/SetDexFiles with GlobalDebugInterface<T>*
TRAP_STUB(_ZN11unwindstack8Unwinder11SetJitDebugEPNS_20GlobalDebugInterfaceINS_3ElfEEE)
TRAP_STUB(_ZN11unwindstack8Unwinder11SetDexFilesEPNS_20GlobalDebugInterfaceINS_7DexFileEEE)

// art::metrics vtable storage (data symbols, not functions)
__attribute__((visibility("default")))
void* _ZTVN3art7metrics12XmlFormatterE[32] = {nullptr};
__attribute__((visibility("default")))
void* _ZTVN3art7metrics13TextFormatterE[32] = {nullptr};

// ============================================================
// 12g. Remaining missing symbols (exact mangled names from libart dep chain)
// ============================================================

// --- android::base logging functions ---
// debug_bridge_design.html §4.7: __android_log_set_aborter must retain the
// callback so libart's LOG(FATAL) → __android_log_assert → call_aborter()
// chain can surface the abort message before the process dies.  Previously
// stubbed as no-op, which silently swallowed every ART abort message.
static void (*g_android_log_aborter)(const char*) = nullptr;

void __android_log_set_aborter(void (*fn)(const char*)) {
    g_android_log_aborter = fn;
}

void __android_log_call_aborter(const char* msg) {
    fprintf(stderr, "android_log_call_aborter: %s\n", msg ? msg : "(null)");
    fflush(stderr);
    if (g_android_log_aborter) {
        g_android_log_aborter(msg);
        // The aborter is expected to call abort()/std::abort() itself, but if
        // it returns we still need to terminate (libart's contract is that
        // call_aborter never returns).
    }
    abort();
}

int __android_log_get_minimum_priority() { return 0; }
void __android_log_set_logger(void* fn) { (void)fn; }
void __android_log_set_minimum_priority(int p) { (void)p; }
// __android_log_message struct layout (from android/log.h)
struct __android_log_message_stub {
    int32_t struct_size;
    int32_t buffer_id;
    int32_t priority;
    const char* tag;
    const char* file;
    uint32_t line;
    const char* message;
};
void __android_log_write_log_message(void* msg) {
    struct __android_log_message_stub* m = (struct __android_log_message_stub*)msg;
    if (m && m->message) {
        fprintf(stderr, "%s %s:%u] %s\n",
                m->tag ? m->tag : "?",
                m->file ? m->file : "?",
                m->line, m->message);
    }
}

// --- ProfileCompilationInfo const methods (UND in libart.so) ---
TRAP_STUB(_ZNK3art22ProfileCompilationInfo18GetNumberOfMethodsEv)
TRAP_STUB(_ZNK3art22ProfileCompilationInfo26GetNumberOfResolvedClassesEv)
TRAP_STUB(_ZNK3art22ProfileCompilationInfo10GetClassesERKNS_7DexFileERKNS0_23ProfileSampleAnnotationE)
TRAP_STUB(_ZNK3art22ProfileCompilationInfo20GetClassesAndMethodsERKNS_7DexFileEPNSt3__h3setINS_3dex9TypeIndexENS4_4lessIS7_EENS4_9allocatorIS7_EEEEPNS5_ItNS8_ItEENSA_ItEEEESH_SH_RKNS0_23ProfileSampleAnnotationE)

// --- art:: operator<< for debug enums ---
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_10LayoutTypeE)
TRAP_STUB(_ZN3artlsERNSt3__h13basic_ostreamIcNS0_11char_traitsIcEEEENS_17DexLayoutSections11SectionTypeE)

// --- Truly missing symbols (not in any compiled .so, not in libc++) ---
// ZIP archive functions: real implementation in minizip.cpp (linked with -lz)
// vixl::CodeBuffer (code-buffer-vixl.cc compile failure)
TRAP_STUB(_ZN4vixl10CodeBuffer15EmitZeroedBytesEi)
TRAP_STUB(_ZN4vixl10CodeBuffer4GrowEj)
TRAP_STUB(_ZN4vixl10CodeBuffer5AlignEv)
TRAP_STUB(_ZN4vixl10CodeBuffer8EmitDataEPKvj)
TRAP_STUB(_ZN4vixl10CodeBufferD1Ev)
// android::CallStack (not compiled)
TRAP_STUB(_ZN7android9CallStack11deleteStackEPS0_)
TRAP_STUB(_ZN7android9CallStack16logStackInternalEPKcPKS0_19android_LogPriority)
TRAP_STUB(_ZN7android9CallStack18getCurrentInternalEi)
// Thread TLS init guard (ART internal)
TRAP_STUB(_ZTHN3art6Thread9self_tls_E)

#undef TRAP_STUB


// ZIP error code to string conversion (needed by libartbase.so via zip_archive.cc)
const char* ErrorCodeString(int err) {
    switch (err) {
        case 0:  return "Success";
        case -1: return "Iteration ended";
        case -2: return "Invalid ZIP archive";
        case -3: return "Entry not found";
        case -4: return "Invalid entry name";
        case -5: return "I/O error";
        case -6: return "Decompression error";
        case -7: return "Allocation failed";
        case -8: return "Buffer too small";
        case -9: return "Invalid handle";
        case -10: return "File operation error";
        default: return "Unknown ZIP error";
    }
}

} // extern "C"

// --- Stubs for B's libunwindstack.so dependencies ---
// DemangleNameIfNeeded: return input unchanged (no demangling support)
namespace unwindstack {
std::string DemangleNameIfNeeded(const std::string& name) { return name; }
}

// art_api::dex function pointers (libdexfile_external API, all nullptr)
namespace art_api { namespace dex {
void (*g_ADexFile_create)(void) = nullptr;
void (*g_ADexFile_findMethodAtOffset)(void) = nullptr;
void (*g_ADexFile_destroy)(void) = nullptr;
void (*g_ADexFile_Method_getCodeOffset)(void) = nullptr;
void (*g_ADexFile_Method_getQualifiedName)(void) = nullptr;
void LoadLibdexfile(void) {}
bool TryLoadLibdexfile(std::string*) { return false; }
}}

// ============================================================
// unwindstack::ThreadUnwinder stubs - ThreadUnwinder.cpp excluded from
// libunwindstack build (uses tgkill which musl doesnt expose via signal.h).
// Stub ctor + UnwindWithSignal so libunwindstack relocations resolve.
// Not on Heap-construction hot path.
// ============================================================
#include <memory>
#include <string>
#include <vector>
namespace unwindstack {
class Maps;
class Memory;
class Regs;
class ThreadUnwinder {
 public:
    ThreadUnwinder(uint32_t, Maps*, std::shared_ptr<Memory>&);
    void UnwindWithSignal(int, int,
                          std::unique_ptr<Regs>*,
                          const std::vector<std::string>*,
                          const std::vector<std::string>*);
};
ThreadUnwinder::ThreadUnwinder(uint32_t, Maps*, std::shared_ptr<Memory>&) {}
void ThreadUnwinder::UnwindWithSignal(int, int,
                                      std::unique_ptr<Regs>*,
                                      const std::vector<std::string>*,
                                      const std::vector<std::string>*) {}
}  // namespace unwindstack

