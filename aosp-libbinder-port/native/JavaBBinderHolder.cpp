// SPDX-License-Identifier: Apache-2.0
//
// Westlake M3++ — JavaBBinderHolder.cpp
//
// C++ side of the AOSP `android.os.Binder` <-> `JavaBBinder` (BBinder subclass)
// linkage.  Modeled directly on AOSP's android_util_Binder.cpp (~AOSP 11/13/16
// — the data layout has been stable for a decade), but trimmed down to only
// the surface needed for **same-process** binder use.  Specifically, we
// implement:
//
//   * `JavaBBinder` — a `BBinder` subclass that holds a JNI weak/global ref
//     back to the Java Binder it represents.  Its `localBinder()` returns
//     itself (inherited from BBinder), which is the load-bearing call that
//     `javaObjectForIBinder()` uses to detect "this remote binder is actually
//     a local Java Binder, return the same Java object that registered it".
//
//   * `JavaBBinder::onTransact()` — returns BAD_TRANSACTION.  We never wire
//     up cross-process onTransact in M3++ (no caller has needed it; same-
//     process `queryLocalInterface` elision sidesteps the entire transact
//     path).  If/when M4 needs cross-process binders, this must grow into the
//     full execTransact-into-Java path (~80 LOC in AOSP).
//
//   * `JavaBBinderHolder` — wraps a `wp<JavaBBinder>`.  Lazily promotes /
//     creates the JavaBBinder on the first `get()` call.  Stored as a `jlong`
//     in `android.os.Binder.mObject`.  Lifetime is governed by
//     `NativeAllocationRegistry`-backed Java finalizer (returned by
//     `getNativeFinalizer()` in binder_jni_stub.cc).
//
//   * `javaObjectForIBinder()` / `ibinderForJavaObject()` — the two
//     conversion helpers used at every JNI boundary that crosses the
//     binder/IBinder/Java-IBinder triangle.
//
// References:
//   - AOSP 11: frameworks/base/core/jni/android_util_Binder.cpp lines
//     336-512 (JavaBBinder / JavaBBinderHolder), 736-798 (conversion).
//   - AOSP 16 framework-native: same shape, identical class layout.
//
// The header companion is inlined here (no .h yet) — callers from
// binder_jni_stub.cc include this .cpp via the extern declarations declared
// at the bottom of this file (since we link the .o, not the .cpp).
//
// Note on classloader-aware FindClass:  this file caches a single global
// `gBinderClass` at first use of `Binder::Binder()` JNI path.  The
// classloader pointer is supplied by the caller (binder_jni_stub.cc's
// JNI_OnLoad_binder_with_cl) — that's the only place we have access to the
// app/system classloader.

#include <jni.h>

#include <atomic>
#include <mutex>

#include <binder/Binder.h>
#include <binder/IBinder.h>
#include <binder/IInterface.h>
#include <utils/RefBase.h>
#include <utils/String16.h>
#include <utils/Vector.h>

#include <android/log.h>

#include "JavaBBinderHolder.h"
// W9 (CR-FF Pattern 1, 2026-05-16) — promote previously file-local
// ScopedAttachedEnv struct to the reusable westlake::ScopedJniAttach class
// living in aosp-libbinder-port/include/westlake/scoped_jni_attach.h.  Now
// shared with daemon JNI callbacks and future V3 adapter reverse-call paths.
#include "westlake/scoped_jni_attach.h"

using namespace android;

#define WLK_TAG "WLK-jbbh"
#define WLK_LOGI(fmt, ...) \
    do { \
        __android_log_print(ANDROID_LOG_INFO, WLK_TAG, fmt, ##__VA_ARGS__); \
        fprintf(stderr, "[" WLK_TAG "] " fmt "\n", ##__VA_ARGS__); \
    } while (0)
#define WLK_LOGE(fmt, ...) \
    do { \
        __android_log_print(ANDROID_LOG_ERROR, WLK_TAG, fmt, ##__VA_ARGS__); \
        fprintf(stderr, "[" WLK_TAG " err] " fmt "\n", ##__VA_ARGS__); \
    } while (0)

// ---------------------------------------------------------------------------
// Subclass-ID — used by `BBinder::checkSubclass()` to identify "this BBinder
// is a JavaBBinder".  AOSP uses the address of `gBinderOffsets` (their cached
// Binder.class jclass/method ID block); we use a dedicated static byte so we
// don't have to maintain that struct.  Externally visible (extern "C") so
// binder_jni_stub.cc can pass the same pointer to `checkSubclass()`.
extern "C" {
__attribute__((visibility("default")))
const char gJavaBBinderSubclassID = 'J';
}

// ---------------------------------------------------------------------------
// JavaBBinder — BBinder subclass holding a Java global ref to android.os.Binder

static JavaVM* g_vm_for_jbinder = nullptr;

// CR1-fix (2026-04-…): codex Tier 1 MEDIUM-3 — binder-pool threads reach this
// file unattached to the JVM.  GetEnv returns JNI_EDETACHED and naive code
// used to AttachCurrentThread without ever detaching, leaking a JVM thread
// reference and tripping ART CheckJNI "thread exited without
// DetachCurrentThread" SIGABRT.
//
// W9 (2026-05-16): the file-local ScopedAttachedEnv struct + getEnvOrAttach()
// helper + leak-by-design javavm_to_jnienv() trio has been replaced by the
// reusable westlake::ScopedJniAttach class in
// aosp-libbinder-port/include/westlake/scoped_jni_attach.h.  All call sites
// in this file now construct a ScopedJniAttach directly; the leaky
// javavm_to_jnienv() helper is intentionally gone — any new code path must
// use the RAII class.

namespace android {

class JavaBBinder : public BBinder {
public:
    JavaBBinder(JNIEnv* env, jobject javaBinder)
        : mVM(nullptr), mObject(nullptr) {
        if (env != nullptr) {
            env->GetJavaVM(&mVM);
            if (g_vm_for_jbinder == nullptr) g_vm_for_jbinder = mVM;
            mObject = env->NewGlobalRef(javaBinder);
        }
        WLK_LOGI("JavaBBinder ctor: this=%p javaBinder=%p", this, javaBinder);
    }

    bool checkSubclass(const void* subclassID) const override {
        return subclassID == &gJavaBBinderSubclassID;
    }

    // Returns the JNI global ref to the Java Binder this BBinder wraps.
    // Callers must not delete the ref — it's owned by this JavaBBinder.
    jobject object() const { return mObject; }

    JavaVM* vm() const { return mVM; }

protected:
    // CR1-fix: codex Tier 1 MEDIUM-3 -- the dtor may run on any thread the
    // sp<> ref-count drops to zero on, which can be a binder threadpool
    // thread NOT attached to the JVM.  W9 (2026-05-16): use the shared
    // westlake::ScopedJniAttach class — DetachCurrentThread is automatic if
    // (and only if) this scope did the attach.
    virtual ~JavaBBinder() {
        WLK_LOGI("JavaBBinder dtor: this=%p", this);
        if (mObject != nullptr && mVM != nullptr) {
            westlake::ScopedJniAttach attach(mVM, "WLK-JavaBBinder-dtor");
            if (attach.valid()) attach.env()->DeleteGlobalRef(mObject);
        }
    }

    // Cross-process transact path: NOT IMPLEMENTED for M3++.
    // Same-process callers use `localBinder()` + `queryLocalInterface()` to
    // elide marshalling entirely; this method is only hit by the kernel
    // binder driver when a true cross-process transaction arrives.
    status_t onTransact(uint32_t code, const Parcel& /*data*/,
                        Parcel* /*reply*/, uint32_t /*flags*/) override {
        WLK_LOGE("JavaBBinder::onTransact(code=%u) — cross-process not "
                 "implemented in M3++; returning UNKNOWN_TRANSACTION", code);
        return UNKNOWN_TRANSACTION;
    }

    // CR1-fix: codex Tier 1 MEDIUM-3 -- same Attach/Detach concern as the
    // dtor.  getInterfaceDescriptor() is virtual and may be invoked from a
    // binder thread (or from servicemanager-side logging).  W9 (2026-05-16):
    // shared westlake::ScopedJniAttach ensures balanced detach.
    const String16& getInterfaceDescriptor() const override {
        std::call_once(mDescriptorOnce, [this] {
            westlake::ScopedJniAttach attach(mVM, "WLK-JavaBBinder-descr");
            JNIEnv* env = attach.env();
            if (env == nullptr || mObject == nullptr) return;
            jclass cls = env->GetObjectClass(mObject);
            if (cls == nullptr) { env->ExceptionClear(); return; }
            jmethodID mid = env->GetMethodID(cls, "getInterfaceDescriptor",
                                              "()Ljava/lang/String;");
            env->DeleteLocalRef(cls);
            if (mid == nullptr) { env->ExceptionClear(); return; }
            jstring js = (jstring) env->CallObjectMethod(mObject, mid);
            if (env->ExceptionCheck()) { env->ExceptionClear(); return; }
            if (js == nullptr) return;
            const char* cs = env->GetStringUTFChars(js, nullptr);
            if (cs != nullptr) {
                mDescriptor = String16(cs);
                env->ReleaseStringUTFChars(js, cs);
            }
            env->DeleteLocalRef(js);
        });
        return mDescriptor;
    }

private:
    JavaVM* mVM;
    jobject mObject;  // Global ref to android.os.Binder

    mutable std::once_flag mDescriptorOnce;
    mutable String16 mDescriptor;
};

// ---------------------------------------------------------------------------
// JavaBBinderHolder — lazy factory + weak ref to JavaBBinder.

JavaBBinderHolder::JavaBBinderHolder() {
    WLK_LOGI("JavaBBinderHolder ctor: this=%p", this);
}

JavaBBinderHolder::~JavaBBinderHolder() {
    WLK_LOGI("JavaBBinderHolder dtor: this=%p", this);
}

sp<JavaBBinder> JavaBBinderHolder::get(JNIEnv* env, jobject javaBinder) {
    std::lock_guard<std::mutex> lock(mLock);
    sp<JavaBBinder> b = mBinder.promote();
    if (b == nullptr) {
        b = new JavaBBinder(env, javaBinder);
        mBinder = b;
    }
    return b;
}

sp<JavaBBinder> JavaBBinderHolder::getExisting() {
    std::lock_guard<std::mutex> lock(mLock);
    return mBinder.promote();
}

// ---------------------------------------------------------------------------
// Conversion helpers — used by binder_jni_stub.cc

// Convert a sp<IBinder> (typically the result of `sm->checkService(...)`) to
// a Java IBinder.  If the IBinder is actually a same-process JavaBBinder
// (its `localBinder()->checkSubclass()` recognizes our ID), we return the
// ORIGINAL Java Binder object that registered it — preserving identity for
// the AOSP `Stub.asInterface()` optimization.  Otherwise the caller is
// responsible for wrapping it in a NativeBinderProxy.
//
// Returns:  Java jobject (Binder subclass)  if local-in-process
//            nullptr                          otherwise (caller wraps)
jobject javaObjectForLocalIBinder(JNIEnv* env, const sp<IBinder>& val) {
    if (val == nullptr) return nullptr;
    BBinder* local = val->localBinder();
    if (local == nullptr) return nullptr;
    if (!local->checkSubclass(&gJavaBBinderSubclassID)) {
        // Local C++ BBinder but not a JavaBBinder (e.g. servicemanager's own
        // "manager" service handle, or a raw `sp<BBinder>::make()` token).
        return nullptr;
    }
    JavaBBinder* jb = static_cast<JavaBBinder*>(local);
    return jb->object();  // already a global ref; caller must NOT delete
}

// Extract the sp<JavaBBinder> backing a Java android.os.Binder object.
// Returns null if the Java object has no associated JavaBBinderHolder
// (e.g. it's a NativeBinderProxy or some other IBinder shim).
sp<IBinder> ibinderForJavaBinder(JNIEnv* env, jobject javaBinder,
                                  jfieldID mObjectField) {
    if (javaBinder == nullptr) return nullptr;
    if (mObjectField == nullptr) return nullptr;
    jlong handle = env->GetLongField(javaBinder, mObjectField);
    if (handle == 0) return nullptr;
    JavaBBinderHolder* holder = reinterpret_cast<JavaBBinderHolder*>(handle);
    return holder->get(env, javaBinder);
}

}  // namespace android

// ---------------------------------------------------------------------------
// C-callable factory / finalizer.  Used by binder_jni_stub.cc's
// `Java_android_os_Binder_getNativeBBinderHolder` and `getNativeFinalizer`.

extern "C" {

JNIEXPORT jlong JNICALL westlake_create_javabinder_holder() {
    return reinterpret_cast<jlong>(new android::JavaBBinderHolder());
}

JNIEXPORT void JNICALL westlake_destroy_javabinder_holder(jlong handle) {
    if (handle == 0) return;
    delete reinterpret_cast<android::JavaBBinderHolder*>(handle);
}

}  // extern "C"
