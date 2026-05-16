/**
 * appspawn-x ART runtime wrapper.
 *
 * Manages the Android Runtime (ART) virtual machine lifecycle within
 * the appspawn-x daemon. Handles VM creation, framework class preloading
 * (analogous to Zygote), and per-child post-fork initialization.
 */

#pragma once

#include <jni.h>
#include <string>

namespace appspawnx {

class AppSpawnXRuntime {
public:
    AppSpawnXRuntime();
    ~AppSpawnXRuntime();

    // Phase 1: Create ART VM and register JNI methods
    // Returns 0 on success
    int startVm();

    // Phase 2: Call Java-side preload (classes, resources, libs, adapter)
    // Returns 0 on success
    int preload();

    // Run startVm() + preload() on a dedicated pthread with an 8MB stack.
    // OH main thread stack is ~132KB (kernel does not auto-extend), so
    // ART deep recursive init SIGSEGVs on main. This wrapper creates a
    // worker pthread sized for AOSP needs, runs both phases there, and
    // blocks the worker after success so the JNIEnv* remains valid for
    // the JavaVM's lifetime. Main thread waits on a condvar for the
    // worker's ready signal, then proceeds to the spawn loop.
    // Returns 0 if both startVm and preload succeed on the worker.
    int startVmAndPreloadOnDedicatedThread();

    // Called in child process after fork
    // For Option A: initialize OH IPC (no Android Binder)
    // For Option B: start Android Binder thread pool
    void onChildInit();

    // B.33 (2026-04-29): Zygote-style fork mediation. Without these calls,
    // ART daemon threads (HeapTaskDaemon/FinalizerDaemon/Runtime worker/
    // Signal Catcher/etc.) are present in parent at fork time but die in
    // child, leaving ART internal locks held by dead TIDs.  Subsequent
    // env->CallStaticVoidMethod in child blocks indefinitely on a futex
    // /eventfd waiting for those daemons.
    //
    // Canonical AOSP zygote sequence (Zygote.java):
    //   ZygoteHooks.preFork();        // parent: stop daemons + Runtime::PreZygoteFork
    //   pid = fork();
    //   if (pid == 0) postForkChild(0,false,false,"arm");  // child: Runtime::PostZygoteFork
    //   ZygoteHooks.postForkCommon(); // both: Daemons.startPostZygoteFork
    //
    // Returns 0 on success, non-zero on error (logs + best-effort continue).
    int zygotePreFork();
    int zygotePostForkChild();
    int zygotePostForkCommon();

    JavaVM* getJavaVM() const { return javaVm_; }
    JNIEnv* getJNIEnv() const { return env_; }

    // Cached PathClassLoader instance (global ref; null if PathClassLoader
    // path failed and FindClass fallback was used). Owned by runtime.
    jobject getPathClassLoader() const { return pathClassLoader_; }

    // Cached AppSpawnXInit class (global ref via PathClassLoader when possible;
    // null if both PathClassLoader and FindClass fallback failed).
    jclass getAppSpawnXInitClass() const { return appSpawnXInitClass_; }

    // Look up a class by binary name (e.g. "adapter.core.OHEnvironment") using
    // the cached PathClassLoader so it finds classes in oh-adapter-runtime.jar.
    // Returns a local ref (caller owns it) or null if lookup fails.
    // Falls back to env->FindClass when pathClassLoader_ is null.
    jclass loadClassViaPath(JNIEnv* env, const char* binaryName);

    // Worker pthread state (public because the pthread entry helper is
    // C linkage at file scope). Created by startVmAndPreloadOnDedicatedThread.
    struct WorkerState;
    WorkerState* workerState_;

private:
    JavaVM* javaVm_;
    JNIEnv* env_;

    // Cached Java class/method references for preload
    jclass appSpawnXInitClass_;
    jmethodID preloadMethod_;
    jmethodID initChildMethod_;

    // Global ref to the PathClassLoader that loaded oh-adapter-runtime.jar.
    // Child processes use this to find classes in the adapter jar without
    // re-triggering <clinit> (which would try to re-loadLibrary bridge.so).
    jobject pathClassLoader_;

    // Cached ClassLoader.loadClass(String) method — resolves classes via
    // pathClassLoader_ in the child without hitting bootstrap classloader.
    jmethodID classLoaderLoadClass_;

    int registerNativeMethods();
    int cacheJavaReferences();

    // B.33: cached refs for dalvik.system.ZygoteHooks (resolved lazily on
    // first zygote* call; reused across many fork iterations).
    jclass     zygoteHooksClass_;        // global ref
    jmethodID  zygotePreForkMethod_;     // ()V
    jmethodID  zygotePostForkChildMethod_;   // (IZZLjava/lang/String;)V
    jmethodID  zygotePostForkCommonMethod_;  // ()V
    int resolveZygoteHooks(JNIEnv* env);
};

} // namespace appspawnx
