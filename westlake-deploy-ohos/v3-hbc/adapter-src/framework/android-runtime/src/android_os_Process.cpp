// ============================================================================
// android_os_Process.cpp
//
// JNI bindings for android.os.Process. Mirrors AOSP
// frameworks/base/core/jni/android_util_Process.cpp, minimal subset Hello
// World startup actually invokes.
//
// Current set:
//   Process.setArgV0Native(String)   — uses prctl(PR_SET_NAME) for
//                                       visible process name in ps / top.
//
// AOSP's real setArgV0 also overwrites argv[0] by reallocating the main
// thread's stack base. We omit that (Hello World doesn't need it; ps
// shows PR_SET_NAME value which is what matters for debugging). Add
// argv[0] rewrite only if a concrete caller needs it.
//
// Additional Process natives (setUid, setGid, getPids, killProcess, etc.)
// are not registered here yet — add incrementally as new UnsatisfiedLinkErrors
// surface during child launch.
// ============================================================================

#include "AndroidRuntime.h"

#include <jni.h>
#include <string.h>
#include <sys/prctl.h>
#include <sys/resource.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <signal.h>
#include <unistd.h>
#include <errno.h>

namespace android {

namespace {

void JNICALL
Process_setArgV0Native(JNIEnv* env, jclass /*clazz*/, jstring name) {
    if (name == nullptr) {
        return;
    }
    const char* utf = env->GetStringUTFChars(name, nullptr);
    if (!utf) {
        return;
    }
    // PR_SET_NAME takes at most 15 bytes + NUL. ps/top show this value as
    // comm. AOSP's String8/argv0 patching for /proc/self/cmdline is skipped
    // for adapter — not on the Hello World critical path.
    char buf[16] = {0};
    strncpy(buf, utf, sizeof(buf) - 1);
    prctl(PR_SET_NAME, reinterpret_cast<unsigned long>(buf), 0, 0, 0);
    env->ReleaseStringUTFChars(name, utf);
}

// B.37 (2026-04-29): sendSignal JNI binding.  When OH AMS times out the app
// and asks ActivityThread.H to handle EXIT_APPLICATION, AOSP calls
// Process.killProcess(pid) → Process.sendSignal(pid, SIGNAL_KILL).  Without
// it registered, child throws UnsatisfiedLinkError and the timeout cascade
// is opaque.  Real impl uses kill(2) which Linux musl exposes directly;
// matches AOSP semantics of frameworks/base/core/jni/android_util_Process.cpp.
//
// IMPORTANT: only register methods we KNOW exist on android.os.Process for
// our boot-image-matching framework.jar.  RegisterNatives fails with -1
// (whole batch) if any one method-name lookup misses.  Don't speculate —
// add new entries only after the corresponding UnsatisfiedLinkError surfaces.
void JNICALL
Process_sendSignal(JNIEnv* /*env*/, jclass /*clazz*/, jint pid, jint sig) {
    if (pid > 0) {
        kill(static_cast<pid_t>(pid), sig);
    }
}

// 2026-05-11 G2.14at — single-arg setThreadPriority(int).  AOSP impl:
// setpriority(PRIO_PROCESS, gettid(), priority).  Used by HandlerThread.run()
// on activity teardown; missing this caused helloworld FATAL UnsatisfiedLinkError
// → System.exit on onDestroy.
void JNICALL
Process_setThreadPriority1(JNIEnv* env, jclass /*clazz*/, jint priority) {
    pid_t tid = static_cast<pid_t>(syscall(SYS_gettid));
    if (setpriority(PRIO_PROCESS, tid, priority) != 0) {
        // ignore errors — adapter doesn't enforce thread-priority semantics
        // beyond best-effort; failure here is non-fatal for our purposes.
    }
}

// Two-arg setThreadPriority(int tid, int priority).
void JNICALL
Process_setThreadPriority2(JNIEnv* env, jclass /*clazz*/, jint tid, jint priority) {
    if (tid <= 0) return;
    setpriority(PRIO_PROCESS, static_cast<id_t>(tid), priority);
}

const JNINativeMethod kProcessMethods[] = {
    { "setArgV0Native",
      "(Ljava/lang/String;)V",
      reinterpret_cast<void*>(Process_setArgV0Native) },
    { "sendSignal",
      "(II)V",
      reinterpret_cast<void*>(Process_sendSignal) },
    { "setThreadPriority",
      "(I)V",
      reinterpret_cast<void*>(Process_setThreadPriority1) },
    { "setThreadPriority",
      "(II)V",
      reinterpret_cast<void*>(Process_setThreadPriority2) },
};

}  // namespace

int register_android_os_Process(JNIEnv* env) {
    jclass clazz = env->FindClass("android/os/Process");
    if (!clazz) {
        return -1;
    }
    jint rc = env->RegisterNatives(clazz, kProcessMethods,
                                    sizeof(kProcessMethods) / sizeof(kProcessMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
