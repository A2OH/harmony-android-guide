/**
 * appspawn-x ART runtime implementation.
 *
 * Creates and configures the ART virtual machine, preloads Android framework
 * classes and resources for fast fork-based app spawning, and handles
 * per-child post-fork initialization including OH IPC setup.
 */

#include "appspawnx_runtime.h"
#include "spawn_msg.h"

#include <cstdlib>
#include <cstring>
#include <dlfcn.h>
#include <fcntl.h>
#include <pthread.h>
#include <setjmp.h>
#include <unistd.h>
#include <string>
#include <vector>

// JniInvocation provides the abstraction to load libart.so at runtime
#include "nativehelper/JniInvocation.h"

// Abort trap from libart_runtime_stubs.so
extern "C" volatile int g_abort_trap_enabled;
extern "C" jmp_buf g_abort_jmpbuf;

namespace appspawnx {

// ---------------------------------------------------------------------------
// JNI_CreateJavaVM function pointer type (loaded from libart.so)
// ---------------------------------------------------------------------------
using JNI_CreateJavaVM_t = jint (*)(JavaVM**, JNIEnv**, void*);

// ---------------------------------------------------------------------------
// Helper: build a VM option entry
// ---------------------------------------------------------------------------
static JavaVMOption makeOption(const char* str) {
    JavaVMOption opt;
    opt.optionString = const_cast<char*>(str);
    opt.extraInfo = nullptr;
    return opt;
}

// ---------------------------------------------------------------------------
// AppSpawnXRuntime
// ---------------------------------------------------------------------------

AppSpawnXRuntime::AppSpawnXRuntime()
    : workerState_(nullptr),
      javaVm_(nullptr),
      env_(nullptr),
      appSpawnXInitClass_(nullptr),
      preloadMethod_(nullptr),
      initChildMethod_(nullptr),
      pathClassLoader_(nullptr),
      classLoaderLoadClass_(nullptr),
      zygoteHooksClass_(nullptr),
      zygotePreForkMethod_(nullptr),
      zygotePostForkChildMethod_(nullptr),
      zygotePostForkCommonMethod_(nullptr) {
}

AppSpawnXRuntime::~AppSpawnXRuntime() {
    if (javaVm_) {
        LOGI("Destroying ART VM");
        javaVm_->DestroyJavaVM();
        javaVm_ = nullptr;
        env_ = nullptr;
    }
}

// ---------------------------------------------------------------------------
// startVm  –  create ART VM and register JNI natives
// ---------------------------------------------------------------------------
// Flush-after-log macro for debugging startup
#define LOGF(fmt, ...) do { fprintf(stderr, "[AppSpawnX][VM] " fmt "\n", ##__VA_ARGS__); fflush(stderr); } while(0)

int AppSpawnXRuntime::startVm() {
    LOGF("Starting ART VM initialization");

    // Step 1: Use JniInvocation to locate and load libart.so
    LOGF("Step 1: JniInvocation::Init (dlopen libart.so)...");
    JniInvocation jniInvocation;
    if (!jniInvocation.Init(nullptr /* default: libart.so */)) {
        LOGF("ERROR: JniInvocation::Init failed – cannot load ART runtime library");
        return -1;
    }
    LOGF("Step 1: OK - libart.so loaded");

    // Step 2: Collect VM options
    std::vector<JavaVMOption> options;

    // B.33 (2026-04-29): -Xzygote tells ART this process is the zygote.
    // Without it, art::Runtime::PreZygoteFork() (called via ZygoteHooks.preFork
    // → nativePreFork) hits a CHECK that aborts the process — observed as
    // SIGABRT signal 6 right after `[ZYG] >>> ZygoteHooks.preFork() (parent)`.
    // The flag also enables zygote-specific GC layout (separate non-moving
    // image space) so children share preloaded Java heap pages COW after fork.
    options.push_back(makeOption("-Xzygote"));

    // Previously forced interpreter mode via -Xint, but the bytecode
    // interpreter (ExecuteSwitchImplCpp) recurses once per Java method call.
    // The boot class init chain is hundreds of classes deep, which exhausted
    // the 32 MB worker stack long before the chain finished. Let ART use the
    // AOT-compiled methods shipped in boot.oat and fall back to the interpreter
    // only for methods not covered.

    // Disable sigchain — OH musl's GWP-ASan init conflicts with ART's
    // signal chain, causing infinite loop in may_init_gwp_asan.
    // 2026-04-16: removed -Xno-sig-chain; ART Runtime::Start requires sig chain. libsigchain.so is now linked and functional.

    // Boot classpath: read from environment variable set by init.rc
    const char* bootClassPath = getenv("BOOTCLASSPATH");
    std::string bootClassPathOpt;
    if (bootClassPath && bootClassPath[0] != '\0') {
        bootClassPathOpt = std::string("-Xbootclasspath:") + bootClassPath;
        options.push_back(makeOption(bootClassPathOpt.c_str()));
        LOGF("Step 2: BOOTCLASSPATH=%s", bootClassPath);
    } else {
        LOGF("Step 2: WARNING - BOOTCLASSPATH not set");
    }

    // Image location for pre-compiled boot image
    const char* bootImage = getenv("ANDROID_BOOT_IMAGE");
    std::string imageOpt;
    if (bootImage && bootImage[0] != '\0') {
        imageOpt = std::string("-Ximage:") + bootImage;
        options.push_back(makeOption(imageOpt.c_str()));
        LOGF("Step 2: Boot image=%s", bootImage);
    }

    // Enable verbose startup logging for debugging
    options.push_back(makeOption("-verbose:startup"));

    // G2.14k (2026-05-01): diagnose ReferenceQueueD SIGSEGV at ~29s. Enable
    // verbose GC + ref logging to see which GC cycle / reference type triggers
    // the null-vtable-dispatch in Daemons.ReferenceQueueDaemon. Off by default;
    // set APPSPAWNX_VERBOSE_GC=1 to enable.
    if (const char* v = getenv("APPSPAWNX_VERBOSE_GC"); v && strcmp(v, "1") == 0) {
        options.push_back(makeOption("-verbose:gc"));
        options.push_back(makeOption("-verbose:collector"));
        LOGW("ART -verbose:gc + -verbose:collector enabled via APPSPAWNX_VERBOSE_GC=1");
    }

    // 2026-04-29 B.31: -verbose:class / -verbose:verifier removed.  These were
    // B.12/B.15 NCDFE diagnostic flags.  Keeping them on permanently floods
    // hilog with class load + verify lines (~10K lines per child startup),
    // which significantly slows Java init in the child and pushes us past the
    // OH AMS 5s LIFECYCLE_HALF_TIMEOUT.  NCDFE root cause has long since been
    // closed (B.16 BCP gap → B.18 mainline stubs), so the diagnostic is no
    // longer needed.  Re-enable only for targeted regression hunts via env:
    //   if (getenv("APPSPAWNX_VERBOSE_CLASS")) options.push_back(makeOption("-verbose:class"));
    if (const char* v = getenv("APPSPAWNX_VERBOSE_CLASS"); v && strcmp(v, "1") == 0) {
        options.push_back(makeOption("-verbose:class"));
        LOGW("ART -verbose:class enabled via APPSPAWNX_VERBOSE_CLASS=1");
    }
    if (const char* v = getenv("APPSPAWNX_VERBOSE_VERIFIER"); v && strcmp(v, "1") == 0) {
        options.push_back(makeOption("-verbose:verifier"));
        LOGW("ART -verbose:verifier enabled via APPSPAWNX_VERBOSE_VERIFIER=1");
    }

    // Disable verify for faster startup in development builds
    // Remove this in production
    const char* fastDev = getenv("APPSPAWNX_FAST_DEV");
    if (fastDev && strcmp(fastDev, "1") == 0) {
        options.push_back(makeOption("-Xverify:none"));
        LOGW("DEX verification disabled (dev mode)");
    }

    // 2026-05-01 G2.14n DIAGNOSTIC: disable JIT.
    if (const char* v = getenv("APPSPAWNX_NO_JIT"); v && strcmp(v, "1") == 0) {
        options.push_back(makeOption("-Xusejit:false"));
        LOGW("ART JIT DISABLED via APPSPAWNX_NO_JIT=1");
    }
    // 2026-05-01 G2.14n DIAGNOSTIC: force interpreter mode (skip BOTH JIT and
    // AOT). If SIGILL persists with -Xint, the corrupted entry_point isn't
    // from JIT or AOT — must be a different mechanism.
    if (const char* v = getenv("APPSPAWNX_FORCE_INT"); v && strcmp(v, "1") == 0) {
        options.push_back(makeOption("-Xint"));
        LOGW("ART INTERPRETER MODE FORCED via APPSPAWNX_FORCE_INT=1");
    }

    // 2026-05-02 G2.14r DIAGNOSTIC: enable ART CheckJNI when an abort happens
    // without printable FATAL message (raise(SIGABRT) directly, no stderr dump).
    // -Xcheck:jni catches: stale local refs / wrong signatures / null this /
    // GlobalRef leaks / NewStringUTF on bad UTF-8 / ExceptionCheck violations
    // / etc., and prints a detailed FATAL message before abort. This is the
    // primary diagnostic for activityResumed-rc=0 → SIGABRT in libart.so
    // (HelloWorld real path post G2.14q+r — see project_g214r_art_sigabrt_blocker.md).
    //
    // Note: do NOT use -Xjniopts:warnonly here — we WANT abort + print, not
    // print + continue (which masks the originating call). The abort message
    // contains the offending JNI op + class/method/sig + Java-side stack.
    if (const char* v = getenv("APPSPAWNX_CHECK_JNI"); v && strcmp(v, "1") == 0) {
        options.push_back(makeOption("-Xcheck:jni"));
        LOGW("ART -Xcheck:jni ENABLED via APPSPAWNX_CHECK_JNI=1 (will abort + print on JNI misuse)");
        // 2026-05-06 — 实测踩坑（保留作记录）：曾尝试同步启用
        // -verbose:abort,jni,threads,startup 让 abort 前 print 更多诊断，结果
        // ART 死循环 1991 次重启。verbose:jni 让 ART trace 所有 JNI 调用，
        // verbose:threads 让 thread 状态变更 print，量级巨大→hilog 限流/OOM/
        // ART 内部 deadlock。**禁止启用 -verbose:* 任何子集** —— 原因：每次
        // 启用都会让 ART 在 startReg 阶段 print 海量信息，让 helloworld 没机
        // 会跑到 activityResumed。CheckJNI 单独启用是 OK 的（不影响 startup）。
        //
        //     options.push_back(makeOption("-verbose:abort,jni,threads,startup"));  // ← 禁用
    }

    // GC tuning (disabled for debugging)
    // options.push_back(makeOption("-XX:HeapGrowthLimit=256m"));

    // Step 3: Fill in JavaVMInitArgs
    JavaVMInitArgs initArgs;
    initArgs.version = JNI_VERSION_1_6;
    initArgs.nOptions = static_cast<jint>(options.size());
    initArgs.options = options.data();
    initArgs.ignoreUnrecognized = JNI_TRUE;

    LOGF("Step 3: Creating JavaVM with %d options", initArgs.nOptions);
    for (int i = 0; i < initArgs.nOptions; i++) {
        LOGF("  option[%d]: %s", i, initArgs.options[i].optionString);
    }

    // Step 4: Create the VM
    LOGF("Step 4: Calling JNI_CreateJavaVM...");
    jint rc = JNI_CreateJavaVM(&javaVm_, &env_, &initArgs);
    LOGF("Step 4: JNI_CreateJavaVM returned %d", rc);
    if (rc != JNI_OK) {
        LOGF("ERROR: JNI_CreateJavaVM failed, rc=%d", rc);
        javaVm_ = nullptr;
        env_ = nullptr;
        return -1;
    }
    LOGF("Step 4: JavaVM created successfully");

    // Step 5: Register native methods (framework JNI bindings)
    int ret = registerNativeMethods();
    if (ret != 0) {
        LOGE("registerNativeMethods failed, ret=%d", ret);
        return ret;
    }

    // Step 6: Cache Java class/method references for preload and child init
    ret = cacheJavaReferences();
    if (ret != 0) {
        LOGE("cacheJavaReferences failed, ret=%d", ret);
        return ret;
    }

    LOGI("ART VM initialization complete");
    return 0;
}

// ---------------------------------------------------------------------------
// registerNativeMethods  –  link framework JNI methods into the VM
// ---------------------------------------------------------------------------
int AppSpawnXRuntime::registerNativeMethods() {
    LOGI("Registering framework JNI native methods");

    // Progressive-replacement strategy: load liboh_android_runtime.so —
    // OH-Adapter's minimal JNI dispatcher — instead of the full AOSP
    // libandroid_runtime.so. Stage 1 registers only android.util.Log; later
    // stages add resources / Surface / Canvas / Binder as UI paths exercise
    // them. Exported entry point keeps the AOSP mangled signature
    //   android::AndroidRuntime::startReg(JNIEnv*)
    //   (_ZN7android14AndroidRuntime8startRegEP7_JNIEnv)
    // so the existing dlsym call site keeps working.
    void* libRuntime = dlopen("liboh_android_runtime.so", RTLD_NOW);
    if (!libRuntime) {
        LOGW("Cannot load liboh_android_runtime.so: %s", dlerror());
        LOGW("Framework JNI methods will not be available – "
             "only basic VM functionality");
        // Non-fatal: preload will catch missing classes
        return 0;
    }

    // The registration entry point used by AndroidRuntime
    // Signature: int register_jni_procs(const RegJNIRec array[], size_t count, JNIEnv* env)
    // We look for the exported AndroidRuntime::startReg instead
    using StartReg_t = int (*)(JNIEnv*);
    auto startReg = reinterpret_cast<StartReg_t>(
        dlsym(libRuntime, "_ZN7android14AndroidRuntime8startRegEP7_JNIEnv"));

    if (startReg) {
        int rc = startReg(env_);
        if (rc < 0) {
            LOGE("AndroidRuntime::startReg returned %d", rc);
            dlclose(libRuntime);
            return -1;
        }
        LOGI("Framework JNI methods registered via AndroidRuntime::startReg");
    } else {
        LOGW("AndroidRuntime::startReg symbol not found: %s", dlerror());
        LOGW("Attempting manual registration of essential JNI methods");

        // Manually register the minimum set of JNI methods needed for boot.
        // These are the registration functions exported by libandroid_runtime.
        struct RegEntry {
            const char* symbol;
            const char* name;
        };
        static const RegEntry essentials[] = {
            {"register_android_os_Binder",         "Binder"},
            {"register_android_os_Parcel",         "Parcel"},
            {"register_android_util_Log",          "Log"},
            {"register_android_content_res_AssetManager", "AssetManager"},
        };

        for (const auto& entry : essentials) {
            using RegFunc_t = int (*)(JNIEnv*);
            auto fn = reinterpret_cast<RegFunc_t>(dlsym(libRuntime, entry.symbol));
            if (fn) {
                int rc = fn(env_);
                if (rc < 0) {
                    LOGW("Failed to register %s JNI methods, rc=%d",
                         entry.name, rc);
                } else {
                    LOGD("Registered %s JNI methods", entry.name);
                }
            } else {
                LOGD("Symbol %s not found, skipping", entry.symbol);
            }
        }
    }

    // Keep libandroid_runtime loaded for the lifetime of the process
    // (do not dlclose – the JNI methods reference its code)
    LOGI("JNI registration complete");
    return 0;
}

// ---------------------------------------------------------------------------
// cacheJavaReferences  –  locate AppSpawnXInit class and preload/init methods
// ---------------------------------------------------------------------------
int AppSpawnXRuntime::cacheJavaReferences() {
    LOGI("Caching Java class/method references");

    // 2026-04-17 architectural shift: AppSpawnXInit no longer lives in BCP
    // oh-adapter-framework.jar. It's now in non-BCP oh-adapter-runtime.jar,
    // loaded via DexClassLoader so adapter-project Java iterations do not
    // trigger boot-image rebuild (see doc/liboh_android_runtime_design.html
    // §8.2.1a). Use DexClassLoader here; fallback to FindClass() for
    // backwards compat if the runtime jar is absent.
    static const char* kClassName = "com/android/internal/os/AppSpawnXInit";
    static const char* kRuntimeJarPath =
        "/system/android/framework/oh-adapter-runtime.jar";

    jclass localClass = nullptr;

    // --- Step 1: construct PathClassLoader(dexPath, parent=null bootstrap) via JNI ---
    // Use PathClassLoader (dalvik.system.PathClassLoader) which accepts a
    // path directly and chains to the bootstrap classloader implicitly when
    // parent=null. This avoids ClassLoader.getSystemClassLoader() which
    // hangs on our short-path ART (no ZygoteInit to initialize it).
    //
    // PathClassLoader(String dexPath, ClassLoader parent) is the 2-arg ctor.
    LOGI("Attempting to load AppSpawnXInit via PathClassLoader from %s",
         kRuntimeJarPath);
    jclass pathClClass = env_->FindClass("dalvik/system/PathClassLoader");
    if (env_->ExceptionCheck()) { env_->ExceptionClear(); }
    if (pathClClass) {
        jmethodID pathClCtor = env_->GetMethodID(
            pathClClass, "<init>",
            "(Ljava/lang/String;Ljava/lang/ClassLoader;)V");
        if (pathClCtor) {
            jstring dexPath = env_->NewStringUTF(kRuntimeJarPath);
            jobject pathCl = env_->NewObject(pathClClass, pathClCtor,
                                              dexPath, nullptr);
            if (env_->ExceptionCheck()) {
                LOGW("PathClassLoader ctor threw:");
                env_->ExceptionDescribe();
                env_->ExceptionClear();
            } else if (pathCl) {
                jclass clClass = env_->FindClass("java/lang/ClassLoader");
                if (clClass) {
                    jmethodID loadClass = env_->GetMethodID(
                        clClass, "loadClass",
                        "(Ljava/lang/String;)Ljava/lang/Class;");
                    if (loadClass) {
                        jstring binaryName = env_->NewStringUTF(
                            "com.android.internal.os.AppSpawnXInit");
                        jobject classObj = env_->CallObjectMethod(
                            pathCl, loadClass, binaryName);
                        if (env_->ExceptionCheck()) {
                            LOGW("PathClassLoader.loadClass threw:");
                            env_->ExceptionDescribe();
                            env_->ExceptionClear();
                        } else if (classObj) {
                            localClass = reinterpret_cast<jclass>(classObj);
                            LOGI("AppSpawnXInit loaded via PathClassLoader");
                            // Promote PathClassLoader + loadClass methodID to
                            // globals so child processes can reuse them after
                            // fork to resolve adapter classes (OHEnvironment,
                            // etc.) without triggering duplicate <clinit>.
                            pathClassLoader_ = env_->NewGlobalRef(pathCl);
                            classLoaderLoadClass_ = loadClass;
                            LOGI("Cached PathClassLoader + loadClass for child reuse");
                        }
                        env_->DeleteLocalRef(binaryName);
                    }
                    env_->DeleteLocalRef(clClass);
                }
            }
            if (pathCl)  env_->DeleteLocalRef(pathCl);
            if (dexPath) env_->DeleteLocalRef(dexPath);
        }
        env_->DeleteLocalRef(pathClClass);
    }
    if (env_->ExceptionCheck()) env_->ExceptionClear();

    // --- Step 2: fallback to default FindClass (if runtime jar is absent
    //             or DexClassLoader path failed) ---
    if (!localClass) {
        localClass = env_->FindClass(kClassName);
        if (env_->ExceptionCheck()) env_->ExceptionClear();
    }

    if (!localClass) {
        LOGE("Cannot find class %s (runtime jar + fallback both failed)",
             kClassName);
        LOGW("AppSpawnXInit not found – preload and initChild will be no-ops");
        appSpawnXInitClass_ = nullptr;
        preloadMethod_ = nullptr;
        initChildMethod_ = nullptr;
        return 0;
    }

    // Create a global reference so it survives across JNI frames
    appSpawnXInitClass_ = reinterpret_cast<jclass>(
        env_->NewGlobalRef(localClass));
    env_->DeleteLocalRef(localClass);

    if (!appSpawnXInitClass_) {
        LOGE("Failed to create global reference for %s", kClassName);
        return -1;
    }

    // Cache preload() – static void preload()
    preloadMethod_ = env_->GetStaticMethodID(
        appSpawnXInitClass_, "preload", "()V");
    if (!preloadMethod_) {
        LOGW("Method preload()V not found in %s", kClassName);
        if (env_->ExceptionCheck()) {
            env_->ExceptionDescribe();
            env_->ExceptionClear();
        }
    }

    // Cache initChild() – static void initChild(String procName, String targetClass, int sdkVersion)
    initChildMethod_ = env_->GetStaticMethodID(
        appSpawnXInitClass_, "initChild",
        "(Ljava/lang/String;Ljava/lang/String;I)V");
    if (!initChildMethod_) {
        LOGW("Method initChild(String,String,int)V not found in %s", kClassName);
        if (env_->ExceptionCheck()) {
            env_->ExceptionDescribe();
            env_->ExceptionClear();
        }
    }

    LOGI("Java references cached successfully");
    return 0;
}

// ---------------------------------------------------------------------------
// preload  –  invoke AppSpawnXInit.preload() to warm up the VM
// ---------------------------------------------------------------------------
int AppSpawnXRuntime::preload() {
    if (!appSpawnXInitClass_ || !preloadMethod_) {
        LOGW("Preload skipped – AppSpawnXInit class or preload method not available");
        return -1;
    }

    LOGI("Calling AppSpawnXInit.preload()...");
    env_->CallStaticVoidMethod(appSpawnXInitClass_, preloadMethod_);

    if (env_->ExceptionCheck()) {
        // Preload is defined as non-fatal: any Java-side initialization that
        // does not apply to our OH runtime (e.g. sun.security.jca.Providers,
        // missing-provider AssertionError) must not abort appspawn-x launch.
        // Log + clear + continue. The VM itself remains in a valid state
        // (ExceptionClear), and the specific gaps are tracked as "Java 待
        // 补齐点" in doc/liboh_android_runtime_design.html §8.
        LOGW("Preload raised a Java Throwable (non-fatal, continuing):");
        env_->ExceptionDescribe();
        env_->ExceptionClear();
        return 0;
    }

    LOGI("Preload completed successfully");

    // B.34.1 (2026-04-29 EOD+1): explicit RegisterNatives for
    // AppSpawnXInit.nativeHiLog (B.32 logger).  Must run AFTER preload()
    // because preload triggers OHEnvironment.<clinit> → System.loadLibrary
    // ("oh_adapter_bridge") which is the dlopen of bridge.so.  Before that,
    // dlsym(RTLD_DEFAULT, "Java_..._nativeHiLog") returns null because the
    // symbol's library is not yet in the process's search scope.
    //
    // Why classloader-scoped auto-dlsym fails: bridge.so loads via
    // OHEnvironment.<clinit> → System.loadLibrary, binding it to BCP /
    // bootstrap classloader.  AppSpawnXInit lives in oh-adapter-runtime.jar
    // (PathClassLoader, non-BCP).  ART searches native libraries owned by
    // the calling class's classloader chain — bridge.so is not in
    // PathClassLoader's chain, so AppSpawnXInit.nativeHiLog auto-resolution
    // returns UnsatisfiedLinkError, caught silently by appLog, fallback to
    // System.err which is /dev/null in init service.  Result: zero [B32-J]
    // markers in hilog despite Java init code running fine.
    //
    // RegisterNatives binds explicit method pointer to (jclass, name,
    // signature) regardless of classloader scope.
    // B.35.A (2026-04-29 EOD+2): use explicit dlopen handle for bridge.so.
    // RTLD_DEFAULT in musl does NOT include libraries loaded via dlopen
    // (such as bridge.so via System.loadLibrary).  RTLD_NOLOAD with the
    // .so name returns the existing handle if already loaded; we then
    // dlsym from that handle directly.
    if (appSpawnXInitClass_) {
        void* bridge = dlopen("liboh_adapter_bridge.so", RTLD_NOLOAD | RTLD_NOW);
        if (!bridge) {
            // Fallback: try without RTLD_NOLOAD (will increment refcount,
            // harmless since OHEnvironment.<clinit> already loaded it).
            bridge = dlopen("liboh_adapter_bridge.so", RTLD_NOW);
        }
        if (bridge) {
            void* fn = dlsym(bridge,
                "Java_com_android_internal_os_AppSpawnXInit_nativeHiLog");
            if (fn) {
                JNINativeMethod m = {
                    const_cast<char*>("nativeHiLog"),
                    const_cast<char*>("(Ljava/lang/String;Ljava/lang/String;)V"),
                    fn
                };
                jint rc = env_->RegisterNatives(appSpawnXInitClass_, &m, 1);
                if (rc == JNI_OK) {
                    LOGI("[B35.A] RegisterNatives(AppSpawnXInit.nativeHiLog) OK via dlopen handle");
                } else {
                    LOGE("[B35.A] RegisterNatives failed rc=%d", rc);
                    if (env_->ExceptionCheck()) {
                        env_->ExceptionDescribe();
                        env_->ExceptionClear();
                    }
                }
            } else {
                LOGE("[B35.A] dlsym(handle, Java_..._nativeHiLog) returned null — symbol not exported?");
            }

            // 2026-04-30 方向 2 单类试点：把 cache 好的 PathClassLoader + loadClass
            // methodID 反向注入 bridge.so，让 OH IPC native threads (system CL only,
            // 无 thread context CL) 能通过 adapter_bridge_load_class 加载已搬到
            // oh-adapter-runtime.jar 的类（如 AppSchedulerBridge）。
            if (pathClassLoader_ && classLoaderLoadClass_) {
                using SetterFn = void(*)(JavaVM*, jobject, jmethodID);
                SetterFn setter = reinterpret_cast<SetterFn>(
                    dlsym(bridge, "adapter_bridge_set_class_loader"));
                if (setter) {
                    setter(javaVm_, pathClassLoader_, classLoaderLoadClass_);
                    LOGI("[Direction2] adapter_bridge_set_class_loader injected via dlsym OK");
                } else {
                    LOGE("[Direction2] dlsym(adapter_bridge_set_class_loader) returned null — bridge.so doesn't export setter?");
                }

                // 2026-04-30 (P2-B v2): RegisterNatives for AppSchedulerBridge native
                // methods (nativeParseManifestJson + nativeGetSysProp). Same pattern
                // as B.35.A above — bridge.so loaded by BCP loader, but
                // AppSchedulerBridge lives in PathClassLoader, so JNI auto-resolution
                // fails with UnsatisfiedLinkError. Explicit registration fixes it.
                jclass clBridge = nullptr;
                if (pathClassLoader_ && classLoaderLoadClass_) {
                    jstring nm = env_->NewStringUTF("adapter.activity.AppSchedulerBridge");
                    jobject co = env_->CallObjectMethod(pathClassLoader_, classLoaderLoadClass_, nm);
                    env_->DeleteLocalRef(nm);
                    if (env_->ExceptionCheck()) {
                        LOGW("[P2-Bv2] AppSchedulerBridge loadClass threw");
                        env_->ExceptionDescribe();
                        env_->ExceptionClear();
                    } else if (co) {
                        clBridge = reinterpret_cast<jclass>(co);
                    }
                }
                // B.48 (2026-04-30 P2 §1.2.4.4): register ActivityThread native
                // stubs needed by handleBindApplication when FLAG_DEBUGGABLE is
                // set (which happens for any debug-built App, e.g. HelloWorld).
                // Without nInitZygoteChildHeapProfiling impl, line ~6856 throws
                // UnsatisfiedLinkError, breaking the entire bind path.
                jclass clActivityThread = env_->FindClass("android/app/ActivityThread");
                if (env_->ExceptionCheck()) env_->ExceptionClear();
                if (clActivityThread) {
                    void* fnHeapProf = dlsym(bridge,
                        "Java_android_app_ActivityThread_nInitZygoteChildHeapProfiling");
                    if (fnHeapProf) {
                        JNINativeMethod m = {
                            const_cast<char*>("nInitZygoteChildHeapProfiling"),
                            const_cast<char*>("()V"),
                            fnHeapProf
                        };
                        jint rc = env_->RegisterNatives(clActivityThread, &m, 1);
                        if (rc == JNI_OK) {
                            LOGI("[B48] RegisterNatives(ActivityThread.nInitZygoteChildHeapProfiling) OK");
                        } else {
                            LOGW("[B48] RegisterNatives(nInitZygoteChildHeapProfiling) rc=%d", rc);
                            if (env_->ExceptionCheck()) { env_->ExceptionDescribe(); env_->ExceptionClear(); }
                        }
                    } else {
                        LOGW("[B48] dlsym(nInitZygoteChildHeapProfiling) returned null");
                    }
                    env_->DeleteLocalRef(clActivityThread);
                }

                if (clBridge) {
                    void* fn1 = dlsym(bridge,
                        "Java_adapter_activity_AppSchedulerBridge_nativeParseManifestJson");
                    void* fn2 = dlsym(bridge,
                        "Java_adapter_activity_AppSchedulerBridge_nativeGetSysProp");
                    // G2.14i (2026-05-01): notifyForegroundDeferred main-looper callback.
                    void* fn3 = dlsym(bridge,
                        "Java_adapter_activity_AppSchedulerBridge_nativeNotifyApplicationForegrounded");
                    JNINativeMethod ms[3];
                    int n = 0;
                    if (fn1) {
                        ms[n].name = const_cast<char*>("nativeParseManifestJson");
                        ms[n].signature = const_cast<char*>("(Ljava/lang/String;)Ljava/lang/String;");
                        ms[n].fnPtr = fn1; n++;
                    }
                    if (fn2) {
                        ms[n].name = const_cast<char*>("nativeGetSysProp");
                        ms[n].signature = const_cast<char*>("(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
                        ms[n].fnPtr = fn2; n++;
                    }
                    if (fn3) {
                        ms[n].name = const_cast<char*>("nativeNotifyApplicationForegrounded");
                        ms[n].signature = const_cast<char*>("(I)V");
                        ms[n].fnPtr = fn3; n++;
                    }
                    if (n > 0) {
                        jint rc = env_->RegisterNatives(clBridge, ms, n);
                        if (rc == JNI_OK) {
                            LOGI("[P2-Bv2] RegisterNatives(AppSchedulerBridge, %d) OK", n);
                        } else {
                            LOGE("[P2-Bv2] RegisterNatives(AppSchedulerBridge) rc=%d", rc);
                            if (env_->ExceptionCheck()) { env_->ExceptionDescribe(); env_->ExceptionClear(); }
                        }
                    } else {
                        LOGW("[P2-Bv2] dlsym for AppSchedulerBridge native methods returned null");
                    }
                    env_->DeleteLocalRef(clBridge);
                } else {
                    LOGW("[P2-Bv2] AppSchedulerBridge class not found via PathClassLoader, skipping RegisterNatives");
                }
            } else {
                LOGW("[Direction2] pathClassLoader_ or classLoaderLoadClass_ is null, skipping setter inject");
            }
        } else {
            LOGE("[B35.A] dlopen(liboh_adapter_bridge.so, NOLOAD|NOW) returned null — bridge.so not loaded yet?");
        }
    }
    return 0;
}

// ---------------------------------------------------------------------------
// loadClassViaPath — find a class by binary name using the cached
// PathClassLoader (so classes in oh-adapter-runtime.jar resolve correctly
// in both parent and child processes).  Falls back to env->FindClass if
// the PathClassLoader path was not set up (e.g. jar missing at startup).
// Returns a local ref the caller owns; null + cleared exception on failure.
// ---------------------------------------------------------------------------
jclass AppSpawnXRuntime::loadClassViaPath(JNIEnv* env, const char* binaryName) {
    if (!env || !binaryName) return nullptr;

    if (pathClassLoader_ && classLoaderLoadClass_) {
        jstring jBin = env->NewStringUTF(binaryName);
        jobject classObj = env->CallObjectMethod(
            pathClassLoader_, classLoaderLoadClass_, jBin);
        if (env->ExceptionCheck()) {
            LOGW("PathClassLoader.loadClass('%s') threw:", binaryName);
            env->ExceptionDescribe();
            env->ExceptionClear();
            env->DeleteLocalRef(jBin);
            return nullptr;
        }
        env->DeleteLocalRef(jBin);
        if (classObj) {
            return reinterpret_cast<jclass>(classObj);
        }
    }

    // Fallback: convert 'a.b.C' to 'a/b/C' and use FindClass.
    std::string jniName(binaryName);
    for (char& c : jniName) {
        if (c == '.') c = '/';
    }
    jclass cls = env->FindClass(jniName.c_str());
    if (env->ExceptionCheck()) {
        LOGW("FindClass('%s') fallback threw:", jniName.c_str());
        env->ExceptionDescribe();
        env->ExceptionClear();
        return nullptr;
    }
    return cls;
}

// ---------------------------------------------------------------------------
// onChildInit  –  post-fork initialization in child process
// ---------------------------------------------------------------------------
void AppSpawnXRuntime::onChildInit() {
    LOGI("Child post-fork initialization starting");

    // Re-attach current thread to the VM if needed.
    // After fork(), only the forking thread exists in the child.
    // The JNIEnv from the parent is still valid for this thread.
    JNIEnv* childEnv = nullptr;
    jint rc = javaVm_->GetEnv(reinterpret_cast<void**>(&childEnv), JNI_VERSION_1_6);
    if (rc == JNI_EDETACHED) {
        rc = javaVm_->AttachCurrentThread(&childEnv, nullptr);
        if (rc != JNI_OK) {
            LOGE("Failed to re-attach thread to VM in child, rc=%d", rc);
            return;
        }
        env_ = childEnv;
        LOGD("Thread re-attached to VM in child process");
    } else if (rc == JNI_OK) {
        env_ = childEnv;
        LOGD("Thread already attached to VM in child process");
    } else {
        LOGE("GetEnv failed in child process, rc=%d", rc);
        return;
    }

    // Option A (default): Initialize OH IPC connection.
    // The child process communicates with OH system services via OH IPC
    // (samgr/softbus) rather than Android Binder. The adapter layer
    // (OHEnvironment.initialize) handles this setup later in initAdapterLayer().
    //
    // Option B (alternative): Start Android Binder thread pool.
    // This would be needed if the child must also serve as an Android Binder
    // server. Currently not required since all IPC goes through OH.

    LOGI("Child post-fork initialization complete (OH IPC mode)");
}

// ---------------------------------------------------------------------------
// B.33 zygote fork hooks (dalvik.system.ZygoteHooks)
// ---------------------------------------------------------------------------
//
// These hooks mediate ART state across fork().  Without them, parent fork()s
// while ART daemons hold internal locks; child inherits the lock state but
// the daemons themselves are gone (only the forking thread survives), leaving
// ART deadlocked on its own internal coordination (HeapTaskDaemon completion
// notification, Signal Catcher TLS, JIT compile cache mutex, etc.).
// Symptom we hit pre-B.33: child main thread name "<pre-initialized>" stuck
// in epoll_wait with 3 threads only (main + 2 OS_IPC binder spawns), Java
// initChild never executes, SIGFREEZE at 5s LIFECYCLE_HALF_TIMEOUT.
//
// Implementation via JNI to dalvik.system.ZygoteHooks (in core-libart.jar
// BCP).  AOSP 14 method signatures captured 2026-04-29:
//   preFork()V                                — parent before fork
//   postForkChild(IZZLjava/lang/String;)V     — child after fork
//   postForkCommon()V                          — parent and child after fork

int AppSpawnXRuntime::resolveZygoteHooks(JNIEnv* env) {
    if (zygoteHooksClass_ != nullptr) return 0;

    jclass localClass = env->FindClass("dalvik/system/ZygoteHooks");
    if (!localClass) {
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        LOGE("[ZYG] FindClass(dalvik/system/ZygoteHooks) failed");
        return -1;
    }

    zygoteHooksClass_ = static_cast<jclass>(env->NewGlobalRef(localClass));
    env->DeleteLocalRef(localClass);
    if (!zygoteHooksClass_) {
        LOGE("[ZYG] NewGlobalRef(ZygoteHooks) failed");
        return -1;
    }

    zygotePreForkMethod_ = env->GetStaticMethodID(zygoteHooksClass_, "preFork", "()V");
    zygotePostForkChildMethod_ = env->GetStaticMethodID(
        zygoteHooksClass_, "postForkChild",
        "(IZZLjava/lang/String;)V");
    zygotePostForkCommonMethod_ = env->GetStaticMethodID(
        zygoteHooksClass_, "postForkCommon", "()V");

    if (!zygotePreForkMethod_ || !zygotePostForkChildMethod_ ||
        !zygotePostForkCommonMethod_) {
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        LOGE("[ZYG] GetStaticMethodID failed: pre=%p postChild=%p postCommon=%p",
             zygotePreForkMethod_, zygotePostForkChildMethod_,
             zygotePostForkCommonMethod_);
        env->DeleteGlobalRef(zygoteHooksClass_);
        zygoteHooksClass_ = nullptr;
        return -1;
    }

    LOGI("[ZYG] ZygoteHooks resolved (class + 3 methods)");
    return 0;
}

int AppSpawnXRuntime::zygotePreFork() {
    if (!env_) {
        LOGE("[ZYG] preFork: no JNIEnv");
        return -1;
    }
    if (resolveZygoteHooks(env_) != 0) return -1;

    LOGI("[ZYG] >>> ZygoteHooks.preFork() (parent)");
    env_->CallStaticVoidMethod(zygoteHooksClass_, zygotePreForkMethod_);
    if (env_->ExceptionCheck()) {
        LOGE("[ZYG] ZygoteHooks.preFork() threw");
        env_->ExceptionDescribe();
        env_->ExceptionClear();
        return -1;
    }
    LOGI("[ZYG] <<< ZygoteHooks.preFork() OK");
    return 0;
}

int AppSpawnXRuntime::zygotePostForkChild() {
    // Child inherits parent's class + method refs (refs are heap pointers in
    // the same VM, valid post-fork).  But re-resolve defensively if the
    // parent never resolved (shouldn't happen since preFork must precede).
    if (!env_) {
        LOGE("[ZYG] postForkChild: no JNIEnv");
        return -1;
    }
    if (zygoteHooksClass_ == nullptr && resolveZygoteHooks(env_) != 0) {
        return -1;
    }

    LOGI("[ZYG] >>> ZygoteHooks.postForkChild() (child)");
    // runtimeFlags=0 (no JDWP/debug), isSystemServer=false, isChildZygote=false,
    // instructionSet="arm" (32-bit ARM target rk3568).
    jstring iset = env_->NewStringUTF("arm");
    env_->CallStaticVoidMethod(zygoteHooksClass_, zygotePostForkChildMethod_,
                               (jint)0, JNI_FALSE, JNI_FALSE, iset);
    bool threw = env_->ExceptionCheck();
    if (threw) {
        LOGE("[ZYG] ZygoteHooks.postForkChild() threw");
        env_->ExceptionDescribe();
        env_->ExceptionClear();
    }
    if (iset) env_->DeleteLocalRef(iset);
    if (threw) return -1;
    LOGI("[ZYG] <<< ZygoteHooks.postForkChild() OK");
    return 0;
}

int AppSpawnXRuntime::zygotePostForkCommon() {
    if (!env_) {
        LOGE("[ZYG] postForkCommon: no JNIEnv");
        return -1;
    }
    if (zygoteHooksClass_ == nullptr && resolveZygoteHooks(env_) != 0) {
        return -1;
    }

    LOGI("[ZYG] >>> ZygoteHooks.postForkCommon()");
    env_->CallStaticVoidMethod(zygoteHooksClass_, zygotePostForkCommonMethod_);
    if (env_->ExceptionCheck()) {
        LOGE("[ZYG] ZygoteHooks.postForkCommon() threw");
        env_->ExceptionDescribe();
        env_->ExceptionClear();
        return -1;
    }
    LOGI("[ZYG] <<< ZygoteHooks.postForkCommon() OK");
    return 0;
}

// ---------------------------------------------------------------------------
// Dedicated VM-worker pthread
// ---------------------------------------------------------------------------
struct AppSpawnXRuntime::WorkerState {
    AppSpawnXRuntime* rt;
    pthread_t tid;
    pthread_mutex_t mu;
    pthread_cond_t  cv;
    bool ready;
    int  rc;
};

static void* vmWorkerEntry(void* arg) {
    auto* ws = static_cast<AppSpawnXRuntime::WorkerState*>(arg);
    // Probe actual stack size (informational)
    pthread_attr_t attr;
    size_t stackSize = 0;
    if (pthread_getattr_np(pthread_self(), &attr) == 0) {
        pthread_attr_getstacksize(&attr, &stackSize);
        pthread_attr_destroy(&attr);
    }
    fprintf(stderr, "[AppSpawnX][VM] [VmWorker] stack=%zu bytes\n", stackSize); fflush(stderr);

    int rc = ws->rt->startVm();
    if (rc == 0) {
        rc = ws->rt->preload();
    }

    pthread_mutex_lock(&ws->mu);
    ws->rc = rc;
    ws->ready = true;
    pthread_cond_signal(&ws->cv);
    pthread_mutex_unlock(&ws->mu);

    // Keep the worker alive so the JavaVM's calling-thread JNIEnv stays valid.
    for (;;) pause();
    return nullptr;
}

int AppSpawnXRuntime::startVmAndPreloadOnDedicatedThread() {
    workerState_ = new WorkerState{this, 0, PTHREAD_MUTEX_INITIALIZER,
                                   PTHREAD_COND_INITIALIZER, false, -1};

    // Diagnostic: spawn a "SIGSEGV reclaimer" pthread that continuously
    // reinstalls SIG_DFL for SIGSEGV / SIGBUS while the VM worker is
    // initializing. Theory: libsigchain→libdfx_signalhandler installs an
    // OH fault dumper that itself faults while walking the stack through
    // libffrt, creating an unbounded signal-frame cascade that exhausts the
    // 128 MB worker stack. Forcing SIG_DFL means any genuine fault during
    // VM init terminates via kernel immediately — if EXIT=139 with a clean
    // short stack (not "Fatal signal 11 ... Fault addr = 0xedXX1ff8"), the
    // handler cascade hypothesis is confirmed.
    pthread_t reclaimer;
    pthread_create(&reclaimer, nullptr, [](void*) -> void* {
        for (int i = 0; i < 500; ++i) {
            signal(SIGSEGV, SIG_DFL);
            signal(SIGBUS, SIG_DFL);
            usleep(10000);  // 10 ms
        }
        return nullptr;
    }, nullptr);
    pthread_detach(reclaimer);
    fprintf(stderr, "[AppSpawnX][VM] [Main] SIGSEGV reclaimer pthread started (500x10ms)\n"); fflush(stderr);

    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_attr_setstacksize(&attr, 8 * 1024 * 1024)  /* 8MB = Android native default */;

    fprintf(stderr, "[AppSpawnX][VM] [Main] Creating VmWorker pthread (8 MB stack)\n"); fflush(stderr);
    int pc = pthread_create(&workerState_->tid, &attr, vmWorkerEntry, workerState_);
    pthread_attr_destroy(&attr);
    if (pc != 0) {
        LOGE("pthread_create(VmWorker) failed: %d", pc);
        return -1;
    }

    pthread_mutex_lock(&workerState_->mu);
    while (!workerState_->ready) {
        pthread_cond_wait(&workerState_->cv, &workerState_->mu);
    }
    int rc = workerState_->rc;
    pthread_mutex_unlock(&workerState_->mu);
    fprintf(stderr, "[AppSpawnX][VM] [Main] VmWorker reported rc=%d\n", rc); fflush(stderr);
    return rc;
}

} // namespace appspawnx
