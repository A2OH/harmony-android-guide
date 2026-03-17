/*
 * Minimal Dalvik VM launcher — standalone replacement for dalvikvm.
 * Uses standard JNI invocation API (JNI_CreateJavaVM) to boot the VM
 * and invoke a class's static main(String[]) method.
 *
 * Usage: dalvikvm -cp <classpath> <classname> [args...]
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#ifndef __MUSL__
#include <execinfo.h>
#endif
#include <jni.h>

#include <ucontext.h>
static void crash_handler_sigaction(int sig, siginfo_t *info, void *ucontext) {
    ucontext_t *uc = (ucontext_t *)ucontext;
    void *fault_addr = info->si_addr;
#ifdef __x86_64__
    void *rip = (void*)uc->uc_mcontext.gregs[REG_RIP];
    fprintf(stderr, "\n=== CRASH: signal %d fault_addr=%p rip=%p ===\n", sig, fault_addr, rip);
#else
    fprintf(stderr, "\n=== CRASH: signal %d fault_addr=%p ===\n", sig, fault_addr);
#endif
#ifndef __MUSL__
    void* bt[64];
    int n = backtrace(bt, 64);
    backtrace_symbols_fd(bt, n, 2);
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
