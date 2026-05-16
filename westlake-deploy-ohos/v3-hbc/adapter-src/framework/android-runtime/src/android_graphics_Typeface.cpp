// ============================================================================
// android_graphics_Typeface.cpp
//
// 2026-05-02 G2.14n+: Delegate Typeface JNI registration to libhwui's real
// impl (aosp_patches/libs/hwui/typeface_minimal_stub.cpp), which creates a
// real android::Typeface object with empty FontCollection and returns its
// pointer as the native handle.
//
// SIGILL root cause traced this date:
//   1. Java Typeface.<clinit> calls nativeCreate*Typeface — adapter stub
//      below returns kFakeTypefaceHandle = 1L
//   2. Java stores 1L in Typeface.native_instance
//   3. libhwui's text-rendering path (Paint → MinikinUtils → MinikinFontSkia)
//      reinterpret_cast<Typeface*>(1) → garbage MinikinFont* in
//      MinikinFontSkia::populateSkFont
//   4. SkFont::setTypeface(garbage->RefSkTypeface()) hits inline SkASSERT
//   5. SkASSERT calls sk_abort_no_print() — a 2-byte trap stub in
//      libskia_canvaskit (linker-folded with ICU/SkXMLWriter abstract
//      destructors at the same address, page off 0xf98) → SIGILL trap
//
// libhwui is already DT_NEEDED of liboh_android_runtime.so transitively, so
// dlopen here is just a refcount bump (no init-service hang risk).
//
// HISTORICAL NOTE — original B.15 design rationale (kept for future reference):
//   Earlier this file avoided dlopen("libhwui.so") because libhwui has heavy
//   GPU/OnLoad initialization that was reported unsafe before fork.  That was
//   the rationale for the broken fake-handle stubs below.  The hang concern
//   was never re-validated; with libhwui already loaded transitively by the
//   parent appspawn-x process, dlopen here is safe.  If a future change
//   re-introduces the hang, the broken stubs ARE STILL HERE in the fallback
//   path — they just register a non-functional Typeface that crashes on
//   first use.  Don't accept that fallback as "working"; fix the dlopen
//   issue instead.
// ============================================================================

#include <dlfcn.h>
#include <hilog/log.h>
#include <jni.h>

namespace android {

namespace {

// 2026-05-02: BROKEN FALLBACK PATH BELOW — kept as reference, not active.
// All natives return safe defaults — non-functional but non-crashing at
// Java-ctor time, BUT crashes downstream when libhwui dereferences the
// fake handle as (Typeface*)0x1 (see file header for full crash chain).
// Signatures match AOSP 14 frameworks/base/graphics/java/android/graphics/Typeface.java exactly.
// Return non-zero fake handle so Typeface ctor accepts (it rejects 0 with
// RuntimeException). Same handle reused across all calls — non-functional
// rendering but Java-side ctor / mStyle / sDefaultTypeface chain works.
static const jlong kFakeTypefaceHandle = 1L;

// G2.14k root cause (2026-05-01): Typeface.nativeGetReleaseFunc() must return a real
// function pointer — NOT 0. The returned long is fed to NativeAllocationRegistry as the
// freeFunction, and applyFreeFunction(0, ptr) does `((void(*)(void*))0)(ptr)` → SEGV pc=0
// inside ReferenceQueueDaemon when GC processes the Typeface's Cleaner. Stack chain:
//   GC → Reference.enqueuePending → Cleaner.clean → CleanerThunk.run →
//   NativeAllocationRegistry.applyFreeFunction(0, ptr) → null call → crash at 29s.
// Same pattern as IC_noop_free / Canvas_noop_free / B_noop_free fixes.
static void Typeface_noop_release(void* /*p*/) {
    // Intentional no-op. Real Typeface cleanup (release SkFontMgr / SkTypeface refs)
    // is unimplemented in this minimal in-runtime impl — see top-of-file note.
}

jlong       nativeCreateFromTypeface(JNIEnv*, jclass, jlong, jint) { return kFakeTypefaceHandle; }
jlong       nativeCreateFromTypefaceWithExactStyle(JNIEnv*, jclass, jlong, jint, jboolean) { return kFakeTypefaceHandle; }
jlong       nativeCreateFromTypefaceWithVariation(JNIEnv*, jclass, jlong, jobject) { return kFakeTypefaceHandle; }
jlong       nativeCreateWeightAlias(JNIEnv*, jclass, jlong, jint) { return kFakeTypefaceHandle; }
jlong       nativeCreateFromArray(JNIEnv*, jclass, jlongArray, jlong, jint, jint) { return kFakeTypefaceHandle; }
jintArray   nativeGetSupportedAxes(JNIEnv*, jclass, jlong) { return nullptr; }
void        nativeSetDefault(JNIEnv*, jclass, jlong) {}
jint        nativeGetStyle(JNIEnv*, jclass, jlong) { return 0; }
jint        nativeGetWeight(JNIEnv*, jclass, jlong) { return 400; }
jlong       nativeGetReleaseFunc(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&Typeface_noop_release);
}
void        nativeRegisterGenericFamily(JNIEnv*, jclass, jstring, jlong) {}
jint        nativeWriteTypefaces(JNIEnv*, jclass, jobject, jint, jlongArray) { return 0; }
jlongArray  nativeReadTypefaces(JNIEnv*, jclass, jobject, jint) { return nullptr; }
void        nativeForceSetStaticFinalField(JNIEnv*, jclass, jstring, jobject) {}
void        nativeAddFontCollections(JNIEnv*, jclass, jlong) {}
void        nativeWarmUpCache(JNIEnv*, jclass, jstring) {}
void        nativeRegisterLocaleList(JNIEnv*, jclass, jstring) {}

const JNINativeMethod kMethods[] = {
    {"nativeCreateFromTypeface",               "(JI)J",                                            (void*)nativeCreateFromTypeface},
    {"nativeCreateFromTypefaceWithExactStyle", "(JIZ)J",                                           (void*)nativeCreateFromTypefaceWithExactStyle},
    {"nativeCreateFromTypefaceWithVariation",  "(JLjava/util/List;)J",                             (void*)nativeCreateFromTypefaceWithVariation},
    {"nativeCreateWeightAlias",                "(JI)J",                                            (void*)nativeCreateWeightAlias},
    {"nativeCreateFromArray",                  "([JJII)J",                                         (void*)nativeCreateFromArray},
    {"nativeGetSupportedAxes",                 "(J)[I",                                            (void*)nativeGetSupportedAxes},
    {"nativeSetDefault",                       "(J)V",                                             (void*)nativeSetDefault},
    {"nativeGetStyle",                         "(J)I",                                             (void*)nativeGetStyle},
    {"nativeGetWeight",                        "(J)I",                                             (void*)nativeGetWeight},
    {"nativeGetReleaseFunc",                   "()J",                                              (void*)nativeGetReleaseFunc},
    {"nativeRegisterGenericFamily",            "(Ljava/lang/String;J)V",                           (void*)nativeRegisterGenericFamily},
    {"nativeWriteTypefaces",                   "(Ljava/nio/ByteBuffer;I[J)I",                      (void*)nativeWriteTypefaces},
    {"nativeReadTypefaces",                    "(Ljava/nio/ByteBuffer;I)[J",                       (void*)nativeReadTypefaces},
    {"nativeForceSetStaticFinalField",         "(Ljava/lang/String;Landroid/graphics/Typeface;)V", (void*)nativeForceSetStaticFinalField},
    {"nativeAddFontCollections",               "(J)V",                                             (void*)nativeAddFontCollections},
    {"nativeWarmUpCache",                      "(Ljava/lang/String;)V",                            (void*)nativeWarmUpCache},
    {"nativeRegisterLocaleList",               "(Ljava/lang/String;)V",                            (void*)nativeRegisterLocaleList},
};

}  // namespace

int register_android_graphics_Typeface(JNIEnv* env) {
    // Try libhwui's real registration first (typeface_minimal_stub.cpp in
    // aosp_patches creates a real Typeface w/ empty FontCollection).
    void* hwui = dlopen("libhwui.so", RTLD_NOW | RTLD_NOLOAD);
    if (!hwui) hwui = dlopen("libhwui.so", RTLD_NOW);
    if (hwui) {
        using RegFn = int (*)(JNIEnv*);
        // Try unmangled first (typeface_minimal_stub used `extern "C"`); fall
        // back to the AOSP original C++ mangled name (no `extern "C"` in
        // AOSP jni/Typeface.cpp) so we delegate to the real impl when libhwui
        // ships AOSP-original Typeface.cpp (G2.14q path-A).
        RegFn real_reg = reinterpret_cast<RegFn>(
            dlsym(hwui, "register_android_graphics_Typeface"));
        if (!real_reg) {
            real_reg = reinterpret_cast<RegFn>(
                dlsym(hwui, "_Z34register_android_graphics_TypefaceP7_JNIEnv"));
        }
        if (real_reg) {
            HiLogPrint(LOG_CORE, LOG_INFO, 0xD000F00u, "OH_Typeface",
                "Delegating to libhwui register_android_graphics_Typeface @ %{public}p",
                (void*)real_reg);
            int rc = real_reg(env);
            HiLogPrint(LOG_CORE, LOG_INFO, 0xD000F00u, "OH_Typeface",
                "libhwui Typeface register returned rc=%{public}d (exception=%{public}d)",
                rc, env->ExceptionCheck() ? 1 : 0);
            if (env->ExceptionCheck()) {
                env->ExceptionDescribe();
                env->ExceptionClear();
            }
            return rc;
        }
        HiLogPrint(LOG_CORE, LOG_WARN, 0xD000F00u, "OH_Typeface",
            "dlsym register_android_graphics_Typeface failed: %{public}s",
            dlerror() ? dlerror() : "(null)");
    } else {
        HiLogPrint(LOG_CORE, LOG_WARN, 0xD000F00u, "OH_Typeface",
            "dlopen libhwui.so failed: %{public}s",
            dlerror() ? dlerror() : "(null)");
    }
    // Fallback: register adapter's broken stubs (returns fake handle 1L —
    // will crash downstream). Better than UnsatisfiedLinkError, but logs
    // loudly so we know we're on the bad path.
    HiLogPrint(LOG_CORE, LOG_ERROR, 0xD000F00u, "OH_Typeface",
        "FALLBACK: registering adapter's broken stubs (fake handle 1L)");
    jclass clazz = env->FindClass("android/graphics/Typeface");
    if (!clazz) return -1;
    jint rc = env->RegisterNatives(clazz, kMethods,
                                    sizeof(kMethods) / sizeof(kMethods[0]));
    env->DeleteLocalRef(clazz);
    return rc == JNI_OK ? 0 : -1;
}

}  // namespace android
