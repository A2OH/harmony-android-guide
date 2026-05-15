// SPDX-License-Identifier: Apache-2.0
//
// Westlake M3 — libandroid_runtime_stub.so
//
// A minimal native bridge between dalvikvm's Java side and our libbinder.so.
// Used by the M3 "A2" path (see M3_NOTES.md): we provide a new
// shim/java/android/os/ServiceManager.java in aosp-shim.dex whose static
// methods bind to JNI methods implemented here.  Those JNI methods in turn
// call into our musl/bionic-cross-compiled libbinder.so to actually drive
// the kernel binder driver.
//
// Why not be a drop-in libandroid_runtime.so?  The real libandroid_runtime
// is ~50KLOC of JNI glue around BinderProxy/JavaBBinder/Parcel/etc.  At M3
// we just need ServiceManager.getService() and .listServices() to reach
// our servicemanager — no need for the full BinderProxy class, no need
// for Parcel marshaling.  The shim ServiceManager.java exposes getService
// as a native method; we implement it here against libbinder's C++ API.
//
// Loaded via:
//   System.loadLibrary("android_runtime_stub")   (or System.load)
// from ServiceManager.java's <clinit>.
//
// JNI surface implemented:
//   ServiceManager.nativeGetService(String name) -> long (native IBinder ptr or 0)
//   ServiceManager.nativeListServices()          -> String[]
//   ServiceManager.nativeAddService(String, long) -> int (0 = ok)
//   ServiceManager.nativeIsBinderAlive(long ptr)  -> boolean
//   ServiceManager.nativeReleaseBinder(long ptr)  -> void
//
// "long ptr" carries a sp<IBinder>* that lives until nativeReleaseBinder.
// Java callers can pass this around as an opaque token (NativeIBinder
// wrapper class).

#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <binder/Binder.h>
#include <binder/IBinder.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/ProcessState.h>

#include <utils/String8.h>
#include <utils/String16.h>
#include <utils/Vector.h>

#include <android/log.h>

using namespace android;

#define TAG "WLK-ar-stub"
#define LOGI(fmt, ...) \
    do { \
        __android_log_print(ANDROID_LOG_INFO, TAG, fmt, ##__VA_ARGS__); \
        fprintf(stderr, "[" TAG "] " fmt "\n", ##__VA_ARGS__); \
    } while (0)
#define LOGE(fmt, ...) \
    do { \
        __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##__VA_ARGS__); \
        fprintf(stderr, "[" TAG " err] " fmt "\n", ##__VA_ARGS__); \
    } while (0)

namespace {

// Convert a heap sp<IBinder>* (allocated for Java to hold) to a jlong handle
// and back.  We allocate the sp on the C++ heap so it survives until the
// matching nativeReleaseBinder call.
struct BinderHandle {
    sp<IBinder> binder;
};

jlong toHandle(sp<IBinder> binder) {
    if (binder == nullptr) return 0;
    BinderHandle* h = new BinderHandle{std::move(binder)};
    return reinterpret_cast<jlong>(h);
}

BinderHandle* fromHandle(jlong h) {
    return reinterpret_cast<BinderHandle*>(h);
}

// One-time init.  Pulls the binder device override from BINDER_DEVICE env
// var (matches sm_smoke / sandbox-boot.sh) so we can run on /dev/vndbinder
// in our sandbox.
bool gInitDone = false;

bool ensureInit() {
    if (gInitDone) return true;
    const char* dev = getenv("BINDER_DEVICE");
    if (dev == nullptr || *dev == 0) dev = "/dev/vndbinder";
    LOGI("ensureInit: opening %s", dev);
    sp<ProcessState> ps = ProcessState::initWithDriver(dev);
    if (ps == nullptr) {
        LOGE("ensureInit: ProcessState::initWithDriver(%s) returned null", dev);
        return false;
    }
    ps->startThreadPool();
    gInitDone = true;
    LOGI("ensureInit: ok");
    return true;
}

// Helper: jstring -> String16
String16 jstringToString16(JNIEnv* env, jstring js) {
    if (js == nullptr) return String16();
    const char* cs = env->GetStringUTFChars(js, nullptr);
    if (cs == nullptr) return String16();
    String16 result(cs);
    env->ReleaseStringUTFChars(js, cs);
    return result;
}

}  // namespace

// ===================================================================
// JNI implementations
// ===================================================================

extern "C" JNIEXPORT jlong JNICALL
Java_android_os_ServiceManager_nativeGetService(JNIEnv* env, jclass /*klass*/,
                                                 jstring jName) {
    if (!ensureInit()) return 0;
    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        LOGE("nativeGetService: defaultServiceManager() returned null");
        return 0;
    }
    String16 name = jstringToString16(env, jName);
    sp<IBinder> b = sm->checkService(name);
    LOGI("nativeGetService(\"%s\") -> %p",
         String8(name).c_str(), b.get());
    return toHandle(b);
}

extern "C" JNIEXPORT jobjectArray JNICALL
Java_android_os_ServiceManager_nativeListServices(JNIEnv* env, jclass /*klass*/) {
    if (!ensureInit()) return nullptr;
    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        LOGE("nativeListServices: defaultServiceManager() returned null");
        return nullptr;
    }
    Vector<String16> names = sm->listServices(IServiceManager::DUMP_FLAG_PRIORITY_ALL);
    LOGI("nativeListServices: %zu names", names.size());
    jclass stringCls = env->FindClass("java/lang/String");
    if (stringCls == nullptr) return nullptr;
    jobjectArray arr = env->NewObjectArray(static_cast<jsize>(names.size()),
                                            stringCls, nullptr);
    if (arr == nullptr) return nullptr;
    for (size_t i = 0; i < names.size(); ++i) {
        String8 n8(names[i]);
        jstring js = env->NewStringUTF(n8.c_str());
        env->SetObjectArrayElement(arr, static_cast<jsize>(i), js);
        env->DeleteLocalRef(js);
    }
    return arr;
}

extern "C" JNIEXPORT jint JNICALL
Java_android_os_ServiceManager_nativeAddService(JNIEnv* env, jclass /*klass*/,
                                                jstring jName, jlong /*binderHandle*/) {
    if (!ensureInit()) return -1;
    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) return -1;
    String16 name = jstringToString16(env, jName);
    // For M3 we don't yet support binding a Java-side BBinder back to C++.
    // The shim ServiceManager.addService() path in HelloBinder isn't
    // exercised — the test uses an external sm_smoke to register the service.
    // We still log so any future caller sees what would happen.
    sp<IBinder> token = sp<BBinder>::make();
    status_t st = sm->addService(name, token, false /*allowIsolated*/,
                                 IServiceManager::DUMP_FLAG_PRIORITY_DEFAULT);
    LOGI("nativeAddService(\"%s\") -> %d", String8(name).c_str(), st);
    return static_cast<jint>(st);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_android_os_ServiceManager_nativeIsBinderAlive(JNIEnv* /*env*/, jclass /*klass*/,
                                                    jlong handle) {
    BinderHandle* h = fromHandle(handle);
    if (h == nullptr) return JNI_FALSE;
    return h->binder->isBinderAlive() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL
Java_android_os_ServiceManager_nativeReleaseBinder(JNIEnv* /*env*/, jclass /*klass*/,
                                                    jlong handle) {
    BinderHandle* h = fromHandle(handle);
    if (h != nullptr) delete h;
}

extern "C" JNIEXPORT jstring JNICALL
Java_android_os_ServiceManager_nativeBinderDescriptor(JNIEnv* env, jclass /*klass*/,
                                                       jlong handle) {
    BinderHandle* h = fromHandle(handle);
    if (h == nullptr) return nullptr;
    String8 desc(h->binder->getInterfaceDescriptor());
    return env->NewStringUTF(desc.c_str());
}

// ---------------------------------------------------------------------------
// HelloBinder helpers: System.out.println in this standalone dalvikvm raises
// NPE because Charset isn't initialized.  Provide a tiny native print method
// so the test harness can emit progress without depending on the Java I/O
// stack.  Wired via JNI under the HelloBinder class name.
// ---------------------------------------------------------------------------

extern "C" JNIEXPORT void JNICALL
Java_HelloBinder_println(JNIEnv* env, jclass /*klass*/, jstring js) {
    if (js == nullptr) {
        fprintf(stdout, "(null)\n");
        fflush(stdout);
        return;
    }
    const char* cs = env->GetStringUTFChars(js, nullptr);
    if (cs == nullptr) return;
    fprintf(stdout, "%s\n", cs);
    fflush(stdout);
    env->ReleaseStringUTFChars(js, cs);
}

extern "C" JNIEXPORT void JNICALL
Java_HelloBinder_eprintln(JNIEnv* env, jclass /*klass*/, jstring js) {
    if (js == nullptr) {
        fprintf(stderr, "(null)\n");
        return;
    }
    const char* cs = env->GetStringUTFChars(js, nullptr);
    if (cs == nullptr) return;
    fprintf(stderr, "%s\n", cs);
    env->ReleaseStringUTFChars(js, cs);
}

// Entry point in case dalvikvm uses System.loadLibrary().  Doesn't do much;
// our methods are auto-discovered via the standard JNI name mangling above.
extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved) {
    fprintf(stderr, "[ar-stub] JNI_OnLoad ENTERED vm=%p\n", vm);
    fflush(stderr);
    LOGI("JNI_OnLoad");
    fprintf(stderr, "[ar-stub] JNI_OnLoad RETURNING JNI_VERSION_1_6\n");
    fflush(stderr);
    return JNI_VERSION_1_6;
}

// Library constructor — runs at dlopen time, before JNI_OnLoad.
__attribute__((constructor))
static void libandroid_runtime_stub_ctor() {
    fprintf(stderr, "[ar-stub] ctor: library loaded into process\n");
    fflush(stderr);
}
