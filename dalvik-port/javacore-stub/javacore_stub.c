/**
 * libjavacore.so stub — JNI natives for Dalvik boot on Linux/OHOS.
 *
 * Uses standard JNI naming (Java_pkg_Class_method) so Dalvik auto-resolves.
 * Only implements methods actually called during VM boot.
 *
 * Build (x86_64 Linux):
 *   gcc -shared -fPIC -o libjavacore.so javacore_stub.c -I$JNI_INCLUDE
 *
 * Build (OHOS ARM32):
 *   $CLANG --target=arm-linux-ohos -shared -fPIC -o libjavacore.so javacore_stub.c -I$JNI_INCLUDE
 */

#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <signal.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <unistd.h>

/* Helper: set a static int field */
static void setInt(JNIEnv* env, jclass cls, const char* name, int val) {
    jfieldID f = (*env)->GetStaticFieldID(env, cls, name, "I");
    if (f) (*env)->SetStaticIntField(env, cls, f, val);
}

/* ═══ libcore.io.OsConstants ═══ */

JNIEXPORT void JNICALL Java_libcore_io_OsConstants_initConstants(JNIEnv* env, jclass cls) {
    setInt(env, cls, "EACCES", EACCES);
    setInt(env, cls, "EAGAIN", EAGAIN);
    setInt(env, cls, "EBADF", EBADF);
    setInt(env, cls, "EEXIST", EEXIST);
    setInt(env, cls, "EINTR", EINTR);
    setInt(env, cls, "EINVAL", EINVAL);
    setInt(env, cls, "EIO", EIO);
    setInt(env, cls, "EISDIR", EISDIR);
    setInt(env, cls, "ENOENT", ENOENT);
    setInt(env, cls, "ENOMEM", ENOMEM);
    setInt(env, cls, "ENOSPC", ENOSPC);
    setInt(env, cls, "ENOTDIR", ENOTDIR);
    setInt(env, cls, "EPERM", EPERM);
    setInt(env, cls, "EPIPE", EPIPE);
    setInt(env, cls, "ERANGE", ERANGE);
    setInt(env, cls, "SIGKILL", SIGKILL);
    setInt(env, cls, "SIGTERM", SIGTERM);
    setInt(env, cls, "SIGINT", SIGINT);
    setInt(env, cls, "SIGABRT", SIGABRT);
    setInt(env, cls, "O_RDONLY", O_RDONLY);
    setInt(env, cls, "O_WRONLY", O_WRONLY);
    setInt(env, cls, "O_RDWR", O_RDWR);
    setInt(env, cls, "O_CREAT", O_CREAT);
    setInt(env, cls, "O_TRUNC", O_TRUNC);
    setInt(env, cls, "O_APPEND", O_APPEND);
    setInt(env, cls, "F_GETFL", F_GETFL);
    setInt(env, cls, "F_SETFL", F_SETFL);
    setInt(env, cls, "S_IFMT", S_IFMT);
    setInt(env, cls, "S_IFDIR", S_IFDIR);
    setInt(env, cls, "S_IFREG", S_IFREG);
    setInt(env, cls, "AF_INET", 2);
    setInt(env, cls, "AF_INET6", 10);
    setInt(env, cls, "AF_UNIX", 1);
    setInt(env, cls, "SOCK_STREAM", 1);
    setInt(env, cls, "SOCK_DGRAM", 2);
    setInt(env, cls, "SEEK_SET", SEEK_SET);
    setInt(env, cls, "SEEK_CUR", SEEK_CUR);
    setInt(env, cls, "SEEK_END", SEEK_END);
}

/* ═══ libcore.icu.ICU ═══ */

JNIEXPORT jobjectArray JNICALL Java_libcore_icu_ICU_getAvailableLocalesNative(JNIEnv* env, jclass cls) {
    jclass s = (*env)->FindClass(env, "java/lang/String");
    jobjectArray a = (*env)->NewObjectArray(env, 1, s, NULL);
    (*env)->SetObjectArrayElement(env, a, 0, (*env)->NewStringUTF(env, "en_US"));
    return a;
}

JNIEXPORT void JNICALL Java_libcore_icu_ICU_setDefaultLocale(JNIEnv* env, jclass cls, jstring locale) {
}

JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getDefaultLocale(JNIEnv* env, jclass cls) {
    return (*env)->NewStringUTF(env, "en_US");
}

JNIEXPORT jboolean JNICALL Java_libcore_icu_ICU_initLocaleDataNative(JNIEnv* env, jclass cls, jstring locale, jobject ld) {
    jclass c = (*env)->GetObjectClass(env, ld);
    if (!c) return JNI_FALSE;
    jfieldID f;
    f = (*env)->GetFieldID(env, c, "decimalSeparator", "C"); if(f) (*env)->SetCharField(env, ld, f, '.');
    f = (*env)->GetFieldID(env, c, "groupingSeparator", "C"); if(f) (*env)->SetCharField(env, ld, f, ',');
    f = (*env)->GetFieldID(env, c, "zeroDigit", "C"); if(f) (*env)->SetCharField(env, ld, f, '0');
    f = (*env)->GetFieldID(env, c, "percent", "C"); if(f) (*env)->SetCharField(env, ld, f, '%');
    f = (*env)->GetFieldID(env, c, "minusSign", "C"); if(f) (*env)->SetCharField(env, ld, f, '-');
    f = (*env)->GetFieldID(env, c, "patternSeparator", "C"); if(f) (*env)->SetCharField(env, ld, f, ';');
    f = (*env)->GetFieldID(env, c, "monetarySeparator", "C"); if(f) (*env)->SetCharField(env, ld, f, '.');
    f = (*env)->GetFieldID(env, c, "exponentSeparator", "Ljava/lang/String;");
    if(f) (*env)->SetObjectField(env, ld, f, (*env)->NewStringUTF(env, "E"));
    f = (*env)->GetFieldID(env, c, "infinity", "Ljava/lang/String;");
    if(f) (*env)->SetObjectField(env, ld, f, (*env)->NewStringUTF(env, "\xe2\x88\x9e"));
    f = (*env)->GetFieldID(env, c, "NaN", "Ljava/lang/String;");
    if(f) (*env)->SetObjectField(env, ld, f, (*env)->NewStringUTF(env, "NaN"));
    f = (*env)->GetFieldID(env, c, "numberPattern", "Ljava/lang/String;");
    if(f) (*env)->SetObjectField(env, ld, f, (*env)->NewStringUTF(env, "#,##0.###"));
    f = (*env)->GetFieldID(env, c, "currencyPattern", "Ljava/lang/String;");
    if(f) (*env)->SetObjectField(env, ld, f, (*env)->NewStringUTF(env, "$#,##0.00"));
    f = (*env)->GetFieldID(env, c, "percentPattern", "Ljava/lang/String;");
    if(f) (*env)->SetObjectField(env, ld, f, (*env)->NewStringUTF(env, "#,##0%"));
    f = (*env)->GetFieldID(env, c, "integerPattern", "Ljava/lang/String;");
    if(f) (*env)->SetObjectField(env, ld, f, (*env)->NewStringUTF(env, "#0"));
    f = (*env)->GetFieldID(env, c, "currencySymbol", "Ljava/lang/String;");
    if(f) (*env)->SetObjectField(env, ld, f, (*env)->NewStringUTF(env, "$"));
    f = (*env)->GetFieldID(env, c, "internationalCurrencySymbol", "Ljava/lang/String;");
    if(f) (*env)->SetObjectField(env, ld, f, (*env)->NewStringUTF(env, "USD"));
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getCurrencyCode(JNIEnv* env, jclass cls, jstring l) {
    return (*env)->NewStringUTF(env, "USD");
}

JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getISO3Country(JNIEnv* env, jclass cls, jstring l) {
    return (*env)->NewStringUTF(env, "USA");
}

JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getISO3Language(JNIEnv* env, jclass cls, jstring l) {
    return (*env)->NewStringUTF(env, "eng");
}

JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getScript(JNIEnv* env, jclass cls, jstring l) {
    return (*env)->NewStringUTF(env, "");
}

JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getBestDateTimePatternNative(JNIEnv* env, jclass cls, jstring l, jstring s) {
    return (*env)->NewStringUTF(env, "yyyy-MM-dd HH:mm:ss");
}

JNIEXPORT jobjectArray JNICALL Java_libcore_icu_ICU_getISOLanguagesNative(JNIEnv* env, jclass cls) {
    jclass s = (*env)->FindClass(env, "java/lang/String");
    jobjectArray a = (*env)->NewObjectArray(env, 1, s, NULL);
    (*env)->SetObjectArrayElement(env, a, 0, (*env)->NewStringUTF(env, "en"));
    return a;
}

JNIEXPORT jobjectArray JNICALL Java_libcore_icu_ICU_getISOCountriesNative(JNIEnv* env, jclass cls) {
    jclass s = (*env)->FindClass(env, "java/lang/String");
    jobjectArray a = (*env)->NewObjectArray(env, 1, s, NULL);
    (*env)->SetObjectArrayElement(env, a, 0, (*env)->NewStringUTF(env, "US"));
    return a;
}

/* ═══ java.io.File ═══ */

JNIEXPORT jstring JNICALL Java_java_io_File_realpath(JNIEnv* env, jclass cls, jstring path) {
    const char* p = (*env)->GetStringUTFChars(env, path, NULL);
    if (!p) return path;
    char buf[4096];
    char* r = realpath(p, buf);
    (*env)->ReleaseStringUTFChars(env, path, p);
    return r ? (*env)->NewStringUTF(env, buf) : path;
}

JNIEXPORT jboolean JNICALL Java_java_io_File_setLastModifiedImpl(JNIEnv* env, jclass cls, jstring path, jlong time) {
    return JNI_TRUE;
}

/* ═══ JNI_OnLoad — register all methods ═══ */

static int tryRegister(JNIEnv* env, const char* className, const JNINativeMethod* methods, int count) {
    jclass cls = (*env)->FindClass(env, className);
    if (!cls) {
        (*env)->ExceptionClear(env);
        return 0;
    }
    int rc = (*env)->RegisterNatives(env, cls, methods, count);
    if (rc != 0) (*env)->ExceptionClear(env);
    return rc == 0 ? 1 : 0;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) return JNI_ERR;

    /* OsConstants */
    {
        JNINativeMethod m[] = {
            {"initConstants", "()V", (void*)Java_libcore_io_OsConstants_initConstants},
        };
        tryRegister(env, "libcore/io/OsConstants", m, 1);
    }

    /* ICU */
    {
        JNINativeMethod m[] = {
            {"getAvailableLocalesNative", "()[Ljava/lang/String;", (void*)Java_libcore_icu_ICU_getAvailableLocalesNative},
            {"setDefaultLocale", "(Ljava/lang/String;)V", (void*)Java_libcore_icu_ICU_setDefaultLocale},
            {"initLocaleDataNative", "(Ljava/lang/String;Llibcore/icu/LocaleData;)Z", (void*)Java_libcore_icu_ICU_initLocaleDataNative},
            {"getCurrencyCode", "(Ljava/lang/String;)Ljava/lang/String;", (void*)Java_libcore_icu_ICU_getCurrencyCode},
            {"getISO3Country", "(Ljava/lang/String;)Ljava/lang/String;", (void*)Java_libcore_icu_ICU_getISO3Country},
            {"getISO3Language", "(Ljava/lang/String;)Ljava/lang/String;", (void*)Java_libcore_icu_ICU_getISO3Language},
            {"getScript", "(Ljava/lang/String;)Ljava/lang/String;", (void*)Java_libcore_icu_ICU_getScript},
            {"getBestDateTimePatternNative", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void*)Java_libcore_icu_ICU_getBestDateTimePatternNative},
            {"getISOLanguagesNative", "()[Ljava/lang/String;", (void*)Java_libcore_icu_ICU_getISOLanguagesNative},
            {"getISOCountriesNative", "()[Ljava/lang/String;", (void*)Java_libcore_icu_ICU_getISOCountriesNative},
        };
        tryRegister(env, "libcore/icu/ICU", m, 10);
    }

    /* File */
    {
        JNINativeMethod m[] = {
            {"realpath", "(Ljava/lang/String;)Ljava/lang/String;", (void*)Java_java_io_File_realpath},
            {"setLastModifiedImpl", "(Ljava/lang/String;J)Z", (void*)Java_java_io_File_setLastModifiedImpl},
        };
        tryRegister(env, "java/io/File", m, 2);
    }

    return JNI_VERSION_1_6;
}

/* Additional ICU methods needed by Dalvik boot */
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getIcuVersion(JNIEnv* env, jclass cls) {
    return (*env)->NewStringUTF(env, "58.2");
}
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getUnicodeVersion(JNIEnv* env, jclass cls) {
    return (*env)->NewStringUTF(env, "9.0");
}
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getCldrVersion(JNIEnv* env, jclass cls) {
    return (*env)->NewStringUTF(env, "30");
}
