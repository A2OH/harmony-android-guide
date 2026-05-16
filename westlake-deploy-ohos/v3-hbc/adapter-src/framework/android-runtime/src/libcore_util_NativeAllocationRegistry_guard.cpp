/*
 * libcore_util_NativeAllocationRegistry_guard.cpp
 *
 * G2.14k DIAGNOSTIC + INFRA (2026-05-01 / 2026-05-02): override
 * libcore.util.NativeAllocationRegistry.applyFreeFunction.
 *
 * History:
 *   v1: skip when freeFunction==0 to avoid GC-time SIGSEGV at pc=0 (kept until
 *       all adapter nGetNativeFinalizer impls return real noop fn ptrs).
 *   v2: same, plus dump Java stack trace at first null encounter for diagnostics.
 *
 * 2026-05-02 audit (feedback.txt #2): user requested removal of this guard as
 * "defensive hack".  Empirically removing it BREAKS HelloWorld initChild —
 * main thread crashes with SIGSEGV pc=0 at very first Java entry.  Root cause
 * not yet pinpointed; appears the guard's FindClass("libcore/util/Native-
 * AllocationRegistry") + RegisterNatives indirectly triggers a class init or
 * sets up state that boot-framework.oat compiled code relies on.  Until that
 * dependency is identified and replaced with a clean equivalent, the guard
 * MUST stay.  TODO P1: replace with minimal class-load probe + remove this file.
 *
 * Stock AOSP impl (libcore/luni/src/main/native/libcore_util_NativeAllocationRegistry.cpp):
 *
 *     static void applyFreeFunction(JNIEnv*, jclass, jlong freeFunction, jlong ptr) {
 *         FreeFunction f = reinterpret_cast<FreeFunction>(freeFunction);
 *         f(reinterpret_cast<void*>(ptr));   // SIGSEGV at pc=0 if freeFunction == 0
 *     }
 *
 * Crash chain when freeFunction == 0:
 *   GC marks Cleaner-typed reference -> ReferenceQueueDaemon dequeues ->
 *   sun.misc.Cleaner.clean() -> NativeAllocationRegistry$CleanerThunk.run() ->
 *   applyFreeFunction(this.freeFunction, this.nativePtr) -> call to address 0 -> SEGV
 */

#include "AndroidRuntime.h"
#include <jni.h>
#include <atomic>
#include <cstdio>
#include <dlfcn.h>
#include <hilog/log.h>

namespace android {
namespace {

typedef void (*FreeFunction)(void*);

static std::atomic<int> gNullDumpRemaining{3};
static std::atomic<int> gNullSkipTotal{0};

void dumpJavaStack(JNIEnv* env, const char* reason) {
    if (env == nullptr) return;
    jclass throwableCls = env->FindClass("java/lang/Throwable");
    if (throwableCls == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        return;
    }
    jmethodID ctor = env->GetMethodID(throwableCls, "<init>", "(Ljava/lang/String;)V");
    if (ctor == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        env->DeleteLocalRef(throwableCls);
        return;
    }
    jstring msg = env->NewStringUTF(reason);
    jobject t = env->NewObject(throwableCls, ctor, msg);
    env->DeleteLocalRef(msg);
    if (t == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        env->DeleteLocalRef(throwableCls);
        return;
    }
    jmethodID printStackTrace = env->GetMethodID(throwableCls, "printStackTrace", "()V");
    if (printStackTrace != nullptr) {
        env->CallVoidMethod(t, printStackTrace);
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
    }
    env->DeleteLocalRef(t);
    env->DeleteLocalRef(throwableCls);
}

void NAR_applyFreeFunction_guarded(JNIEnv* env, jclass,
                                    jlong freeFunction, jlong ptr) {
    if (freeFunction == 0) {
        int total = gNullSkipTotal.fetch_add(1) + 1;
        int dumpsLeft = gNullDumpRemaining.fetch_sub(1);
        HiLogPrint(LOG_CORE, LOG_WARN, 0xD000F00u, "OH_NARGuard",
                   "[G2.14k] applyFreeFunction(0, ptr=0x%{public}llx) [skip #%{public}d]",
                   static_cast<unsigned long long>(ptr), total);
        if (dumpsLeft > 0) {
            HiLogPrint(LOG_CORE, LOG_WARN, 0xD000F00u, "OH_NARGuard",
                       "[G2.14k] Java stack trace for first null encounter:");
            dumpJavaStack(env, "NAR null freeFunction - find me");
        }
        return;
    }
    auto fn = reinterpret_cast<FreeFunction>(static_cast<uintptr_t>(freeFunction));
    void* nativePtr = reinterpret_cast<void*>(static_cast<uintptr_t>(ptr));
    fn(nativePtr);
}

const JNINativeMethod kMethods[] = {
    { "applyFreeFunction", "(JJ)V",
      reinterpret_cast<void*>(NAR_applyFreeFunction_guarded) },
};

}  // namespace

int register_libcore_util_NativeAllocationRegistry_guard(JNIEnv* env) {
    HiLogPrint(LOG_CORE, LOG_INFO, 0xD000F00u, "OH_NARGuard", "register entry");

    // PRIMARY (G2.14r 2026-05-02): adapter never calls
    // System.loadLibrary("javacore"), so libjavacore.so's JNI_OnLoad never
    // runs and the canonical register_libcore_util_NativeAllocationRegistry
    // never binds applyFreeFunction.  That's the actual root cause of the
    // "removing guard => SIGSEGV pc=0" symptom — applyFreeFunction was simply
    // not bound.  Try the canonical libjavacore register first; it correctly
    // binds applyFreeFunction (and the rest of NAR's natives) without our
    // skip-on-null override.  Fall back to the defensive adapter guard only
    // if libjavacore is unavailable.
    void* libjc = dlopen("libjavacore.so", RTLD_NOW | RTLD_NOLOAD);
    if (libjc == nullptr) {
        libjc = dlopen("libjavacore.so", RTLD_NOW);
    }
    if (libjc != nullptr) {
        using RegFn = void (*)(JNIEnv*);
        RegFn real_reg = reinterpret_cast<RegFn>(dlsym(libjc,
            "_Z46register_libcore_util_NativeAllocationRegistryP7_JNIEnv"));
        if (real_reg != nullptr) {
            real_reg(env);
            if (env->ExceptionCheck()) {
                env->ExceptionDescribe();
                env->ExceptionClear();
                HiLogPrint(LOG_CORE, LOG_WARN, 0xD000F00u, "OH_NARGuard",
                    "libjavacore register threw exception; falling back to adapter guard");
            } else {
                HiLogPrint(LOG_CORE, LOG_INFO, 0xD000F00u, "OH_NARGuard",
                    "libjavacore canonical register OK — adapter guard SKIPPED (root cause repaired)");
                return 0;
            }
        } else {
            HiLogPrint(LOG_CORE, LOG_WARN, 0xD000F00u, "OH_NARGuard",
                "libjavacore loaded but register symbol missing; falling back");
        }
    } else {
        HiLogPrint(LOG_CORE, LOG_WARN, 0xD000F00u, "OH_NARGuard",
            "libjavacore.so dlopen failed (%{public}s); using adapter guard fallback",
            dlerror() != nullptr ? dlerror() : "(null)");
    }

    // FALLBACK: adapter's defensive null-skip guard (only reached if
    // libjavacore real register is unavailable / errored).
    jclass cls = env->FindClass("libcore/util/NativeAllocationRegistry");
    if (cls == nullptr) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        HiLogPrint(LOG_CORE, LOG_WARN, 0xD000F00u, "OH_NARGuard",
                   "FindClass FAILED - guard NOT installed");
        return -1;
    }
    jint rc = env->RegisterNatives(cls, kMethods,
                                    sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(cls);
    if (rc != JNI_OK) {
        if (env->ExceptionCheck()) env->ExceptionClear();
        HiLogPrint(LOG_CORE, LOG_WARN, 0xD000F00u, "OH_NARGuard",
                   "RegisterNatives FAILED rc=%{public}d", rc);
        return rc;
    }
    HiLogPrint(LOG_CORE, LOG_INFO, 0xD000F00u, "OH_NARGuard",
               "[G2.14k] applyFreeFunction null-guard INSTALLED (fallback path)");
    return 0;
}

}  // namespace android
