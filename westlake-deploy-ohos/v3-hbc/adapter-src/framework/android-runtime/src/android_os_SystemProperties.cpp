// ============================================================================
// android_os_SystemProperties.cpp
//
// JNI bindings for android.os.SystemProperties.  AOSP stores properties in
// bionic's __system_properties; OH has its own parameter store accessed via
// libbegetutil's GetParameter() (parameter.h).  We route every native_get*
// through GetParameter so values set via OH's `param set` (or in init.cfg)
// are visible to Android framework — including critical ones like
// ro.product.cpu.abilist32 that Build.<clinit> reads at line 312-315.
//
// Returning the caller default for unknown keys (instead of empty string)
// preserves the framework's contract: SystemProperties.get(key, default)
// callers expect default on miss, not "".
//
// Matches AOSP frameworks/base/core/jni/android_os_SystemProperties.cpp
// gMethods table (12 entries).
// ============================================================================

#include "AndroidRuntime.h"

#include <cerrno>
#include <cstdlib>
#include <cstring>
#include <jni.h>

// OH parameter store API (libbegetutil.z.so).  Forward-declared to avoid
// pulling in syspara/parameter.h's deeper dependencies.
extern "C" int GetParameter(const char* key, const char* def, char* value, unsigned int len);

namespace android {

namespace {

constexpr int kSpValueMax = 96;  // OH parameter value max length

// Hardcoded fallback values for Android-specific system properties that OH
// parameter store does not provide.  Without these, framework <clinit>
// pathways crash:
//   - android.os.Build.<clinit> reads SUPPORTED_*_ABIS via getStringList from
//     ro.product.cpu.abilist*; if absent it ends up with String[0] and then
//     line 315 (`CPU_ABI = abiList[0]`) throws ArrayIndexOutOfBoundsException
//     -> ExceptionInInitializerError -> Build$VERSION.<clinit> NCDFE -> any
//     code touching VERSION.SDK_INT NCDFE -> handleBindApplication NCDFE.
//   - android.os.Build.VERSION.<clinit> reads ro.build.version.* — most of
//     these can default to "" but SDK_INT must be a valid integer.
//
// Values are project-stable and chosen to match the AOSP 14 (API 34) we
// build the framework jars against, plus the device CPU (ARM32 on RK3568).
// Updating Android target version requires updating these in lockstep.
//
// Do NOT add unrelated app-facing keys here — that would silently mask real
// OH parameter misses for keys an app expects to be present.  Restrict to
// keys that framework <clinit>/early-startup pathways depend on.
struct AdapterPropFallback {
    const char* key;
    const char* value;
};
static const AdapterPropFallback kAdapterPropFallbacks[] = {
    // CPU ABI list — RK3568 ARM32 userspace.
    { "ro.product.cpu.abilist",       "armeabi-v7a,armeabi" },
    { "ro.product.cpu.abilist32",     "armeabi-v7a,armeabi" },
    { "ro.product.cpu.abilist64",     "" },
    { "ro.product.cpu.abi",           "armeabi-v7a" },
    { "ro.product.cpu.abi2",          "armeabi" },
    // Build version — AOSP 14 (API 34).
    { "ro.build.version.sdk",         "34" },
    { "ro.build.version.release",     "14" },
    { "ro.build.version.release_or_codename", "14" },
    { "ro.build.version.codename",    "REL" },
    { "ro.build.version.codename_or_preview", "REL" },
    { "ro.build.version.all_codenames", "REL" },
    { "ro.build.version.known_codenames", "REL" },
    { "ro.build.version.preview_sdk", "0" },
    { "ro.build.version.preview_sdk_fingerprint", "REL" },
    { "ro.build.version.release_or_preview_display", "14" },
    { "ro.product.first_api_level",   "34" },
    { "ro.build.version.incremental", "oh-adapter" },
    { "ro.build.version.security_patch", "2024-01-01" },
    { "ro.build.version.base_os",     "" },
    // Build product / model — used by Build static fields. Empty string is
    // safe but feels nicer to populate.
    { "ro.product.brand",             "OpenHarmony" },
    { "ro.product.manufacturer",      "OpenHarmony" },
    { "ro.product.model",             "DAYU200" },
    { "ro.product.device",            "rk3568" },
    { "ro.product.name",              "rk3568" },
    { "ro.build.fingerprint",         "OpenHarmony/rk3568/rk3568:14/oh-adapter:user/release-keys" },
    { "ro.build.tags",                "release-keys" },
    { "ro.build.type",                "user" },
    { "ro.build.user",                "oh-adapter" },
    { "ro.build.host",                "oh-adapter" },
    { "ro.build.id",                  "oh-adapter" },
    { "ro.build.display.id",          "oh-adapter" },
    { "ro.serialno",                  "" },
    // Hardware
    { "ro.hardware",                  "rk3568" },
    { "ro.board.platform",            "rk3568" },
    { "ro.boot.hardware",             "rk3568" },
};

static const char* lookup_adapter_fallback(const char* key) {
    for (const auto& e : kAdapterPropFallbacks) {
        if (strcmp(e.key, key) == 0) return e.value;
    }
    return nullptr;
}

// Look up `key` in OH parameter store, falling back to the adapter's hardcoded
// table for Android-specific keys OH does not provide.  Returns true if hit;
// on hit, `out` holds a heap copy that caller must `free()`.
static bool oh_get_parameter(JNIEnv* env, jstring keyJ, char** out) {
    if (keyJ == nullptr) return false;
    const char* key = env->GetStringUTFChars(keyJ, nullptr);
    if (key == nullptr) return false;
    char buf[kSpValueMax + 1] = {0};
    // Pass an empty default so we can distinguish "miss" from a real empty value.
    int rc = GetParameter(key, "", buf, kSpValueMax);
    bool hit = (rc > 0);
    const char* fallback = nullptr;
    if (!hit) fallback = lookup_adapter_fallback(key);
    env->ReleaseStringUTFChars(keyJ, key);
    if (hit) {
        *out = strdup(buf);
    } else if (fallback != nullptr) {
        *out = strdup(fallback);
    } else {
        return false;
    }
    return *out != nullptr;
}

// --- string-key variants ----------------------------------------------------

jstring JNICALL
SP_getSS(JNIEnv* env, jclass /*clazz*/, jstring keyJ, jstring defJ) {
    char* val = nullptr;
    if (oh_get_parameter(env, keyJ, &val)) {
        jstring result = env->NewStringUTF(val);
        free(val);
        return result;
    }
    if (defJ != nullptr) {
        return defJ;
    }
    return env->NewStringUTF("");
}

jint JNICALL
SP_get_int(JNIEnv* env, jclass /*clazz*/, jstring keyJ, jint defVal) {
    char* val = nullptr;
    if (oh_get_parameter(env, keyJ, &val)) {
        char* endp = nullptr;
        long parsed = strtol(val, &endp, 0);
        bool ok = (endp != val);
        free(val);
        if (ok) return static_cast<jint>(parsed);
    }
    return defVal;
}

jlong JNICALL
SP_get_long(JNIEnv* env, jclass /*clazz*/, jstring keyJ, jlong defVal) {
    char* val = nullptr;
    if (oh_get_parameter(env, keyJ, &val)) {
        char* endp = nullptr;
        long long parsed = strtoll(val, &endp, 0);
        bool ok = (endp != val);
        free(val);
        if (ok) return static_cast<jlong>(parsed);
    }
    return defVal;
}

jboolean JNICALL
SP_get_boolean(JNIEnv* env, jclass /*clazz*/, jstring keyJ, jboolean defVal) {
    char* val = nullptr;
    if (oh_get_parameter(env, keyJ, &val)) {
        // AOSP semantics: 1/y/yes/on/true → true; 0/n/no/off/false → false
        bool yes = (strcmp(val, "1") == 0 || strcasecmp(val, "y") == 0 ||
                    strcasecmp(val, "yes") == 0 || strcasecmp(val, "on") == 0 ||
                    strcasecmp(val, "true") == 0);
        bool no  = (strcmp(val, "0") == 0 || strcasecmp(val, "n") == 0 ||
                    strcasecmp(val, "no") == 0 || strcasecmp(val, "off") == 0 ||
                    strcasecmp(val, "false") == 0);
        free(val);
        if (yes) return JNI_TRUE;
        if (no)  return JNI_FALSE;
    }
    return defVal;
}

jlong JNICALL
SP_find(JNIEnv* /*env*/, jclass /*clazz*/, jstring /*keyJ*/) {
    // "find" returns an opaque handle, 0 = not found.
    return 0;
}

// --- handle-key variants (always receive handle = 0 from our find()) --------

jstring JNICALL
SP_getH(JNIEnv* env, jclass /*clazz*/, jlong /*handle*/) {
    return env->NewStringUTF("");
}

jint JNICALL
SP_get_intH(JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/, jint defVal) {
    return defVal;
}

jlong JNICALL
SP_get_longH(JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/, jlong defVal) {
    return defVal;
}

jboolean JNICALL
SP_get_booleanH(JNIEnv* /*env*/, jclass /*clazz*/, jlong /*handle*/, jboolean defVal) {
    return defVal;
}

// --- write + change-notification -------------------------------------------

void JNICALL
SP_set(JNIEnv* /*env*/, jclass /*clazz*/, jstring /*keyJ*/, jstring /*valueJ*/) {
    // OH does not expose a process-writable property store; writes are
    // silently dropped. Hello World path does not write properties.
}

void JNICALL
SP_add_change_callback(JNIEnv* /*env*/, jclass /*clazz*/) {
    // No change-notification source on OH; callers receive no callbacks.
}

void JNICALL
SP_report_sysprop_change(JNIEnv* /*env*/, jclass /*clazz*/) {
    // Nothing to report.
}

const JNINativeMethod kMethods[] = {
    { "native_get", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
      reinterpret_cast<void*>(SP_getSS) },
    { "native_get_int", "(Ljava/lang/String;I)I",
      reinterpret_cast<void*>(SP_get_int) },
    { "native_get_long", "(Ljava/lang/String;J)J",
      reinterpret_cast<void*>(SP_get_long) },
    { "native_get_boolean", "(Ljava/lang/String;Z)Z",
      reinterpret_cast<void*>(SP_get_boolean) },
    { "native_find", "(Ljava/lang/String;)J",
      reinterpret_cast<void*>(SP_find) },
    { "native_get", "(J)Ljava/lang/String;",
      reinterpret_cast<void*>(SP_getH) },
    { "native_get_int", "(JI)I",
      reinterpret_cast<void*>(SP_get_intH) },
    { "native_get_long", "(JJ)J",
      reinterpret_cast<void*>(SP_get_longH) },
    { "native_get_boolean", "(JZ)Z",
      reinterpret_cast<void*>(SP_get_booleanH) },
    { "native_set", "(Ljava/lang/String;Ljava/lang/String;)V",
      reinterpret_cast<void*>(SP_set) },
    { "native_add_change_callback", "()V",
      reinterpret_cast<void*>(SP_add_change_callback) },
    { "native_report_sysprop_change", "()V",
      reinterpret_cast<void*>(SP_report_sysprop_change) },
};

}  // namespace

int register_android_os_SystemProperties(JNIEnv* env) {
    jclass clazz = env->FindClass("android/os/SystemProperties");
    if (!clazz) return -1;
    jint rc = env->RegisterNatives(clazz, kMethods,
                                    sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
