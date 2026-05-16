// ============================================================================
// android_os_Binder.cpp
//
// Minimal JNI stubs for android.os.Binder + BinderInternal + BinderProxy
// to unblock framework class <clinit> chain (PhoneWindow → ServiceManager →
// Binder.getNativeBBinderHolder, etc.).
//
// AOSP Binder requires /dev/binder kernel driver which OH does not ship.
// Real cross-process IPC happens through OH samgr via liboh_adapter_bridge.so;
// these stubs only need to satisfy ART's class-init resolution. Methods that
// would actually do IPC (transactNative / linkToDeath / etc.) are intentionally
// NOT registered here — they remain unresolved and only throw when an app tries
// real cross-process Binder use, which Hello World does not.
//
// Registered minimal set:
//   Binder.getNativeBBinderHolder  ()J        → 0  (no native holder)
//   Binder.getNativeFinalizer      ()J        → 0  (no finalizer needed)
//   Binder.getCallingPid           ()I        → getpid()
//   Binder.getCallingUid           ()I        → getuid()
//   Binder.clearCallingIdentity    ()J        → 0
//   Binder.restoreCallingIdentity  (J)V       → no-op
//   Binder.flushPendingCommands    ()V        → no-op
//   Binder.markVintfStability      ()V        → no-op
//   Binder.forceDowngradeToSystemStability ()V→ no-op
//   Binder.blockUntilThreadAvailable ()V      → no-op
//   Binder.setThreadStrictModePolicy (I)V     → no-op
//   Binder.getThreadStrictModePolicy ()I      → 0
//   Binder.setCallingWorkSourceUid (I)J       → 0
//   Binder.getCallingWorkSourceUid ()I        → -1
//   Binder.clearCallingWorkSource  ()J        → 0
//   Binder.restoreCallingWorkSource (J)V      → no-op
//   Binder.hasExplicitIdentity     ()Z        → false
//   Binder.isDirectlyHandlingTransaction ()Z  → false
//   BinderInternal.getContextObject ()IBinder → null  (caller will skip if null)
//   BinderInternal.handleGc        ()V        → no-op
//   BinderProxy.getNativeFinalizer ()J        → 0
//
// All other Binder methods (transactNative / linkToDeath / extension / ...)
// remain unresolved; first call → UnsatisfiedLinkError, expected for stub
// scope. Replace with real OH-bridged impl in a future Phase when adapter
// IPC routing is wired.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>

namespace android {

namespace {

// Real free function for NativeAllocationRegistry. Each call to
// getNativeBBinderHolder returns a fresh malloc'd 8-byte chunk so each Binder
// instance has its own unique nativePtr (NativeAllocationRegistry would
// otherwise confuse them as a single allocation). The finalizer must therefore
// free the same pointer when GC reclaims the Java Binder. AOSP's real
// JavaBBinderHolder is heavier (refcount + IBinder backref); for stub purposes
// 8 bytes is enough to be a valid heap object.
extern "C" void B_noop_free(void* ptr) {
    if (ptr) free(ptr);
}

// ---- Binder.* (@CriticalNative) --------------------------------------------
jlong B_getNativeBBinderHolder()                          {
    // Java's Binder.<init> calls registerNativeAllocation(this, this nativePtr)
    // which throws IllegalArgumentException("nativePtr is null") when 0.
    // Allocate a tiny placeholder so each Binder gets a unique non-zero ptr.
    // Real Binder transactions are not supported in stub mode (no /dev/binder),
    // so this storage is only used for NativeAllocationRegistry bookkeeping.
    void* p = malloc(8);
    if (p) memset(p, 0, 8);
    return reinterpret_cast<jlong>(p);
}
jlong B_getNativeFinalizer()                              {
    return reinterpret_cast<jlong>(&B_noop_free);
}
jint  B_getCallingPid()                                   { return static_cast<jint>(getpid()); }
jint  B_getCallingUid()                                   { return static_cast<jint>(getuid()); }
jboolean B_isDirectlyHandlingTransaction()                { return JNI_FALSE; }
jboolean B_hasExplicitIdentity()                          { return JNI_FALSE; }
jlong B_clearCallingIdentity()                            { return 0; }
void  B_restoreCallingIdentity(jlong /*token*/)           { }
void  B_setThreadStrictModePolicy(jint /*p*/)             { }
jint  B_getThreadStrictModePolicy()                       { return 0; }
jlong B_setCallingWorkSourceUid(jint /*uid*/)             { return 0; }
jint  B_getCallingWorkSourceUid()                         { return -1; }
jlong B_clearCallingWorkSource()                          { return 0; }
void  B_restoreCallingWorkSource(jlong /*token*/)         { }

// ---- Binder.* (regular JNI) ------------------------------------------------
void B_flushPendingCommands(JNIEnv*, jclass)              { }
void B_markVintfStability(JNIEnv*, jclass)                { }
void B_forceDowngradeToSystemStability(JNIEnv*, jclass)   { }
void B_blockUntilThreadAvailable(JNIEnv*, jclass)         { }

// ---- BinderInternal.* ------------------------------------------------------
jobject BI_getContextObject(JNIEnv*, jclass)              { return nullptr; }
void    BI_handleGc(JNIEnv*, jclass)                      { }

// ---- BinderProxy.* ---------------------------------------------------------
jlong   BP_getNativeFinalizer()                           {
    return reinterpret_cast<jlong>(&B_noop_free);
}

const JNINativeMethod kBinderMethods[] = {
    { "getCallingPid",              "()I",  reinterpret_cast<void*>(B_getCallingPid) },
    { "getCallingUid",              "()I",  reinterpret_cast<void*>(B_getCallingUid) },
    { "isDirectlyHandlingTransaction", "()Z", reinterpret_cast<void*>(B_isDirectlyHandlingTransaction) },
    { "hasExplicitIdentity",        "()Z",  reinterpret_cast<void*>(B_hasExplicitIdentity) },
    { "clearCallingIdentity",       "()J",  reinterpret_cast<void*>(B_clearCallingIdentity) },
    { "restoreCallingIdentity",     "(J)V", reinterpret_cast<void*>(B_restoreCallingIdentity) },
    { "setThreadStrictModePolicy",  "(I)V", reinterpret_cast<void*>(B_setThreadStrictModePolicy) },
    { "getThreadStrictModePolicy",  "()I",  reinterpret_cast<void*>(B_getThreadStrictModePolicy) },
    { "setCallingWorkSourceUid",    "(I)J", reinterpret_cast<void*>(B_setCallingWorkSourceUid) },
    { "getCallingWorkSourceUid",    "()I",  reinterpret_cast<void*>(B_getCallingWorkSourceUid) },
    { "clearCallingWorkSource",     "()J",  reinterpret_cast<void*>(B_clearCallingWorkSource) },
    { "restoreCallingWorkSource",   "(J)V", reinterpret_cast<void*>(B_restoreCallingWorkSource) },
    { "flushPendingCommands",       "()V",  reinterpret_cast<void*>(B_flushPendingCommands) },
    { "markVintfStability",         "()V",  reinterpret_cast<void*>(B_markVintfStability) },
    { "forceDowngradeToSystemStability", "()V", reinterpret_cast<void*>(B_forceDowngradeToSystemStability) },
    { "blockUntilThreadAvailable",  "()V",  reinterpret_cast<void*>(B_blockUntilThreadAvailable) },
    { "getNativeBBinderHolder",     "()J",  reinterpret_cast<void*>(B_getNativeBBinderHolder) },
    { "getNativeFinalizer",         "()J",  reinterpret_cast<void*>(B_getNativeFinalizer) },
};

const JNINativeMethod kBinderInternalMethods[] = {
    { "getContextObject", "()Landroid/os/IBinder;", reinterpret_cast<void*>(BI_getContextObject) },
    { "handleGc",         "()V",                    reinterpret_cast<void*>(BI_handleGc) },
};

const JNINativeMethod kBinderProxyMethods[] = {
    { "getNativeFinalizer", "()J", reinterpret_cast<void*>(BP_getNativeFinalizer) },
};

}  // namespace

int register_android_os_Binder(JNIEnv* env) {
    // Binder
    jclass bClass = env->FindClass("android/os/Binder");
    if (!bClass) return -1;
    jint rc = env->RegisterNatives(bClass, kBinderMethods,
                                    sizeof(kBinderMethods) / sizeof(kBinderMethods[0]));
    env->DeleteLocalRef(bClass);
    if (rc != JNI_OK) return -1;

    // BinderInternal — present in com.android.internal.os, optional class.
    jclass biClass = env->FindClass("com/android/internal/os/BinderInternal");
    if (biClass) {
        env->RegisterNatives(biClass, kBinderInternalMethods,
                             sizeof(kBinderInternalMethods) / sizeof(kBinderInternalMethods[0]));
        env->DeleteLocalRef(biClass);
    } else if (env->ExceptionCheck()) {
        env->ExceptionClear();
    }

    // BinderProxy — inner class of Binder.
    jclass bpClass = env->FindClass("android/os/BinderProxy");
    if (bpClass) {
        env->RegisterNatives(bpClass, kBinderProxyMethods,
                             sizeof(kBinderProxyMethods) / sizeof(kBinderProxyMethods[0]));
        env->DeleteLocalRef(bpClass);
    } else if (env->ExceptionCheck()) {
        env->ExceptionClear();
    }
    return 0;
}

}  // namespace android
