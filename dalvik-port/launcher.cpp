/*
 * Minimal Dalvik VM launcher — standalone replacement for dalvikvm.
 * Uses standard JNI invocation API (JNI_CreateJavaVM) to boot the VM
 * and invoke a class's static main(String[]) method.
 *
 * Usage: dalvikvm -cp <classpath> <classname> [args...]
 *
 * Optional env: DVM_PRELOAD_LIB=<absolute path>
 *   When set (CR60 follow-up, 2026-05-14), the launcher calls
 *   dvmLoadNativeCode(path, NULL, ...) right after VM creation,
 *   BEFORE invoking main(). This is necessary because the bundled
 *   core-kitkat.jar's Runtime.load / loadLibrary / System.loadLibrary
 *   are stubs (return-void) — they don't reach the VM's nativeLoad
 *   path. Loading via the C-side function call associates the lib
 *   with the boot class loader (null), matching the classLoader of
 *   BCP-loaded test classes, so the JNI auto-discovery in
 *   dvmResolveNativeMethod / findMethodInLib succeeds when the test
 *   first calls a native method.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#ifndef __MUSL__
#include <execinfo.h>
#endif
#include <jni.h>

/* Forward-declare dvmLoadNativeCode without pulling in Object.h /
 * Native.h (which would require the full dalvik internal header
 * chain). Signature matches /home/dspfac/dalvik-kitkat/vm/Native.h
 * line 65. The Object* classLoader pointer is passed as NULL → boot
 * class loader; any non-null value would need a real ClassLoader
 * object handle from JNI which we don't currently need. This symbol
 * is a C++ symbol (no extern "C" in Native.h), so we declare it
 * without extern "C" too — the mangling matches. */
typedef struct Object Object;
bool dvmLoadNativeCode(const char* fileName, Object* classLoader,
                       char** detail);

#include <ucontext.h>
static void crash_handler_sigaction(int sig, siginfo_t *info, void *ucontext) {
    ucontext_t *uc = (ucontext_t *)ucontext;
    void *fault_addr = info->si_addr;
#ifdef __x86_64__
    void *rip = (void*)uc->uc_mcontext.gregs[REG_RIP];
    fprintf(stderr, "\n=== CRASH: signal %d fault_addr=%p rip=%p ===\n", sig, fault_addr, rip);
#elif defined(__arm__)
    /* ARM EABI/musl: mcontext has arm_pc / arm_lr fields for the PC and
     * return address at fault. Printing both helps disambiguate native
     * call sites from interpreter-internal crashes when investigating
     * dynamic-PIE launch issues. */
    void *pc = (void*)uc->uc_mcontext.arm_pc;
    void *lr = (void*)uc->uc_mcontext.arm_lr;
    fprintf(stderr, "\n=== CRASH: signal %d fault_addr=%p pc=%p lr=%p ===\n",
            sig, fault_addr, pc, lr);
#else
    fprintf(stderr, "\n=== CRASH: signal %d fault_addr=%p ===\n", sig, fault_addr);
#endif
#ifndef __MUSL__
    void* bt[64];
    int n = backtrace(bt, 64);
    backtrace_symbols_fd(bt, n, 2);
#else
    /* musl has no backtrace_symbols_fd; dump /proc/self/maps so the
     * caller can map fault_addr/pc/lr to a specific mmap'd region.
     * Helps debug dynamic-PIE launch issues where every run gets a
     * different ASLR slide. */
    fputs("--- /proc/self/maps ---\n", stderr);
    FILE *mf = fopen("/proc/self/maps", "r");
    if (mf) {
        char line[512];
        while (fgets(line, sizeof(line), mf)) {
            fputs(line, stderr);
        }
        fclose(mf);
    }
    fputs("--- end maps ---\n", stderr);
#endif
    _exit(128 + sig);
}

int main(int argc, char* argv[]) {
    struct sigaction sa;
    sa.sa_sigaction = crash_handler_sigaction;
    sa.sa_flags = SA_SIGINFO;
    sigemptyset(&sa.sa_mask);
    sigaction(SIGSEGV, &sa, NULL);
    sigaction(SIGABRT, &sa, NULL);
    const char* classpath = NULL;
    const char* className = NULL;
    int classArgStart = 0;

    /* Parse our arguments */
    for (int i = 1; i < argc; i++) {
        if ((strcmp(argv[i], "-cp") == 0 || strcmp(argv[i], "-classpath") == 0) && i + 1 < argc) {
            classpath = argv[++i];
        } else if (argv[i][0] != '-') {
            className = argv[i];
            classArgStart = i + 1;
            break;
        }
        /* other -X options will be passed through to the VM */
    }

    if (className == NULL) {
        fprintf(stderr, "Usage: dalvikvm [-cp <classpath>] <class> [args...]\n");
        return 1;
    }

    /* Build JNI option list */
    int nOpts = 0;
    JavaVMOption opts[64];

    /* Boot class path — for core library classes */
    char bootcp[4096];
    const char* envBootCp = getenv("BOOTCLASSPATH");
    if (envBootCp) {
        snprintf(bootcp, sizeof(bootcp), "-Xbootclasspath:%s", envBootCp);
        opts[nOpts++].optionString = bootcp;
    }

    /* User classpath */
    char cpOpt[4096];
    if (classpath) {
        snprintf(cpOpt, sizeof(cpOpt), "-Djava.class.path=%s", classpath);
        opts[nOpts++].optionString = cpOpt;
    }

    /* Pass through any -X flags from command line */
    for (int i = 1; i < argc; i++) {
        if (argv[i][0] == '-' && strcmp(argv[i], "-cp") != 0 && strcmp(argv[i], "-classpath") != 0) {
            opts[nOpts++].optionString = argv[i];
        } else if (strcmp(argv[i], "-cp") == 0 || strcmp(argv[i], "-classpath") == 0) {
            i++; /* skip next arg */
        } else {
            break; /* hit classname */
        }
    }

    /* Heap defaults */
    opts[nOpts++].optionString = (char*)"-Xms4m";
    opts[nOpts++].optionString = (char*)"-Xmx64m";

    JavaVMInitArgs vmArgs;
    vmArgs.version = JNI_VERSION_1_4;
    vmArgs.nOptions = nOpts;
    vmArgs.options = opts;
    vmArgs.ignoreUnrecognized = JNI_TRUE;

    JavaVM* vm = NULL;
    JNIEnv* env = NULL;

    fprintf(stderr, "dalvikvm: creating VM...\n");
    jint rc = JNI_CreateJavaVM(&vm, &env, &vmArgs);
    if (rc != JNI_OK) {
        fprintf(stderr, "dalvikvm: JNI_CreateJavaVM failed (%d)\n", rc);
        return 1;
    }

    /* CR60 follow-up (2026-05-14): preload native lib(s) via the
     * VM's own dvmLoadNativeCode → dlopen path. The bundled
     * core-kitkat.jar stubs out Runtime.load / loadLibrary, so any
     * Java-side System.loadLibrary call silently returns without
     * doing the dlopen. We work around that by reading
     * $DVM_PRELOAD_LIB (colon-separated absolute paths) and calling
     * dvmLoadNativeCode here, BEFORE the user's main(). Each lib is
     * associated with classLoader=NULL (boot CL); the test class
     * is itself BCP-loaded so findMethodInLib's CL match succeeds. */
    {
        const char* preload = getenv("DVM_PRELOAD_LIB");
        if (preload && *preload) {
            fprintf(stderr, "dalvikvm: DVM_PRELOAD_LIB=%s\n", preload);
            /* Walk a colon-separated list in-place using a mutable
             * copy (strdup since getenv yields a const-ish buffer
             * that's unsafe to modify on some libc impls). */
            char* dup = strdup(preload);
            if (dup) {
                char* save = dup;
                char* tok;
                while ((tok = strsep(&save, ":")) != NULL) {
                    if (!*tok) continue;
                    char* detail = NULL;
                    fprintf(stderr, "dalvikvm: preload dvmLoadNativeCode(%s)\n", tok);
                    bool ok = dvmLoadNativeCode(tok, NULL, &detail);
                    if (!ok) {
                        fprintf(stderr, "dalvikvm: preload FAILED %s : %s\n",
                                tok, detail ? detail : "(no detail)");
                    } else {
                        fprintf(stderr, "dalvikvm: preload OK %s\n", tok);
                    }
                    free(detail);
                }
                free(dup);
            }
        }
    }

    /* Convert class name from com.foo.Bar to com/foo/Bar */
    char classJni[512];
    strncpy(classJni, className, sizeof(classJni) - 1);
    classJni[sizeof(classJni) - 1] = '\0';
    for (char* p = classJni; *p; p++) {
        if (*p == '.') *p = '/';
    }

    jclass cls = env->FindClass(classJni);
    if (cls == NULL) {
        fprintf(stderr, "dalvikvm: class '%s' not found\n", className);
        env->ExceptionDescribe();
        vm->DestroyJavaVM();
        return 1;
    }

    jmethodID mainMethod = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V");
    if (mainMethod == NULL) {
        fprintf(stderr, "dalvikvm: main method not found in '%s'\n", className);
        env->ExceptionClear();
        _exit(1);
    }

    /* Build String[] for main() args */
    int appArgc = argc - classArgStart;
    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray mainArgs = env->NewObjectArray(appArgc, stringClass, NULL);
    for (int i = 0; i < appArgc; i++) {
        jstring s = env->NewStringUTF(argv[classArgStart + i]);
        env->SetObjectArrayElement(mainArgs, i, s);
    }

    fprintf(stderr, "dalvikvm: invoking %s.main()\n", className);
    env->CallStaticVoidMethod(cls, mainMethod, mainArgs);

    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
    }

    vm->DestroyJavaVM();
    return 0;
}
