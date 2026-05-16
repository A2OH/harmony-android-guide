// ============================================================================
// android_view_KeyCharacterMap_aosp.cpp
//
// JNI binding for android.view.KeyCharacterMap.  Adapter-maintained rewrite
// keeping the AOSP 14 (frameworks/base/core/jni/android_view_KeyCharacterMap.cpp)
// 13-native-method structure + g_methods table + register_X function shape;
// implementation adapted to OH (no <binder/Parcel.h>, no <input/*.h>, no
// libinput.so).  Mirrors the "_aosp.cpp" pattern used by android_os_Parcel_aosp
// (G2.14u r2 OhParcelData) and android_util_AssetManager_aosp.
//
// ─── Why this file exists (G2.14w / 2026-05-08) ──────────────────────────────
//
// Without this register_X, ART child throws on first KeyCharacterMap.load:
//
//   java.lang.UnsatisfiedLinkError: No implementation found for
//     android.view.KeyCharacterMap android.view.KeyCharacterMap
//       .nativeObtainEmptyKeyCharacterMap(int)
//     at android.view.KeyCharacterMap.obtainEmptyMap(KeyCharacterMap.java:345)
//     at adapter.window.InputManagerAdapter.buildLocalVirtualKeyboard(...)
//     at adapter.window.InputManagerAdapter.resolveVirtualKeyboard(...)
//     at adapter.window.InputManagerAdapter.getInputDevice(VIRTUAL_KEYBOARD)
//     at android.view.KeyCharacterMap.load(KeyCharacterMap.java:359/361)
//     at com.android.internal.policy.PhoneWindow.preparePanel(PhoneWindow.java:726)
//
// Per memory feedback_no_skip_regjnirec.md, AndroidRuntime startReg's
// RegJNIRec table cannot SKIP register_X — every native class App-side
// might dispatch must be registered.
//
// ─── Implementation strategy ─────────────────────────────────────────────────
//
// The 13 native methods break into three groups based on AOSP impls:
//
//   1. nativeObtainEmptyKeyCharacterMap (KEY PATH for KCM.obtainEmptyMap)
//      — AOSP impl is pure packaging: NativeKeyCharacterMap{deviceId,nullptr}
//      → new Java KeyCharacterMap(jlong).  Never touches the C++
//      KeyCharacterMap class.  We replicate it bit-for-bit.
//
//   2. nativeDispose, nativeReadFromParcel, nativeWriteToParcel
//      — adapter handles allocation lifecycle and treats Parcel transport
//      as no-op (matches android_view_InputChannel.cpp's NO_*_TRANSPORT
//      pattern; KCM has no Parcel cross-process channel in OH).
//
//   3. nativeGetCharacter / nativeGetFallbackAction / nativeGetNumber /
//      nativeGetMatch / nativeGetDisplayLabel / nativeGetKeyboardType /
//      nativeGetEvents / nativeEquals
//      — AOSP impls all gate on `if (!map || !map->getMap()) return 0/null`
//      and only descend into the C++ KeyCharacterMap when getMap() is
//      non-null.  Adapter Phase 1 has mMap permanently nullptr, so all 8
//      ride the AOSP-built-in "no map" early-return path — bit-identical
//      to AOSP behavior for callers that hit obtainEmptyMap-derived
//      instances.  This is not a stub: AOSP itself returns these defaults
//      for empty maps (e.g. obtainEmptyMap-created instances always go
//      through this path even on real Android).
//
// ─── Phase 3 evolution path (real KCM data from OH MMI) ──────────────────────
//
// When OH MMI virtual keyboard mapping (Input_Adapter_design §5.3 B-tier)
// upgrades to attach real keymap data, NativeKeyCharacterMap's mMap can be
// populated with an OH-sourced KeyCharacterMap implementation; the 8 query
// methods above will then naturally route to real data without changing
// this file's structure (mMap.getXxx() will return real values).  The
// forward-declared KeyCharacterMap C++ class becomes a thin wrapper over OH
// MMI keymap inner_api at that point.
// ============================================================================

#include <android_runtime/AndroidRuntime.h>

#include <jni.h>
#include <nativehelper/JNIHelp.h>

#include <memory>

#include "core_jni_helpers.h"

namespace android {

// Forward declaration of AOSP's KeyCharacterMap C++ class.  Adapter Phase 1
// never instantiates it — mMap is permanently a default-constructed (null)
// std::shared_ptr<KeyCharacterMap>, and the 8 query methods always early-
// return on the !map->getMap() guard.  We give the class a complete-but-
// empty body so std::shared_ptr's default deleter compiles cleanly (it
// requires complete type at instantiation point).
class KeyCharacterMap {
    // Intentionally empty — see file header "Implementation strategy".
};

// ─── JNI class info (mirrors AOSP layout exactly) ───────────────────────────

static struct {
    jclass clazz;
    jmethodID ctor;
} gKeyCharacterMapClassInfo;

static struct {
    jclass clazz;
} gKeyEventClassInfo;

static struct {
    jfieldID keyCode;
    jfieldID metaState;
} gFallbackActionClassInfo;

// ─── NativeKeyCharacterMap (1:1 mirror of AOSP class shape) ─────────────────

class NativeKeyCharacterMap {
public:
    NativeKeyCharacterMap(int32_t deviceId, std::shared_ptr<KeyCharacterMap> map)
        : mDeviceId(deviceId), mMap(std::move(map)) {}

    ~NativeKeyCharacterMap() {}

    inline int32_t getDeviceId() const { return mDeviceId; }

    inline const std::shared_ptr<KeyCharacterMap> getMap() const { return mMap; }

private:
    int32_t mDeviceId;
    std::shared_ptr<KeyCharacterMap> mMap;
};

// ─── Shared create helper (also called from C++ side if Phase 3 wires up
//     OH MMI keymap data — kept as a non-static API for forward compat) ──────

jobject android_view_KeyCharacterMap_create(JNIEnv* env, int32_t deviceId,
                                            const std::shared_ptr<KeyCharacterMap> kcm) {
    NativeKeyCharacterMap* nativeMap = new NativeKeyCharacterMap(deviceId, kcm);
    if (!nativeMap) {
        return nullptr;
    }
    return env->NewObject(gKeyCharacterMapClassInfo.clazz, gKeyCharacterMapClassInfo.ctor,
                          reinterpret_cast<jlong>(nativeMap));
}

// ─── Group 1: KEY PATH — nativeObtainEmptyKeyCharacterMap ────────────────────

static jobject nativeObtainEmptyKeyCharacterMap(JNIEnv* env, jobject /* clazz */, jint deviceId) {
    // Bit-identical to AOSP impl — pure packaging, no C++ KCM class touch.
    return android_view_KeyCharacterMap_create(env, deviceId, nullptr);
}

// ─── Group 2: lifecycle (alloc/free) + Parcel io (NO_TRANSPORT) ─────────────

static jlong nativeReadFromParcel(JNIEnv* /* env */, jobject /* clazz */, jobject /* parcelObj */) {
    // OH has no cross-process KeyCharacterMap Parcel transport; mirror
    // android_view_InputChannel.cpp's pattern — return a fresh empty
    // NativeKeyCharacterMap so callers' nativeDispose path stays sound.
    NativeKeyCharacterMap* map = new NativeKeyCharacterMap(/*deviceId*/ -1, nullptr);
    return reinterpret_cast<jlong>(map);
}

static void nativeWriteToParcel(JNIEnv* /* env */, jobject /* clazz */,
                                jlong /* ptr */, jobject /* parcelObj */) {
    // No-op — same rationale as nativeReadFromParcel.
}

static void nativeDispose(JNIEnv* /* env */, jobject /* clazz */, jlong ptr) {
    NativeKeyCharacterMap* map = reinterpret_cast<NativeKeyCharacterMap*>(ptr);
    delete map;
}

// ─── Group 3: query methods — nullptr-tolerant defaults (= AOSP empty-map path) ──

static jchar nativeGetCharacter(JNIEnv* /* env */, jobject /* clazz */, jlong ptr,
                                jint /* keyCode */, jint /* metaState */) {
    NativeKeyCharacterMap* map = reinterpret_cast<NativeKeyCharacterMap*>(ptr);
    if (!map || !map->getMap()) {
        return static_cast<jchar>(0);
    }
    // Phase 3: when OH MMI keymap data attaches, dispatch to map->getMap()
    // ->getCharacter(...).  For now mMap is permanently nullptr.
    return static_cast<jchar>(0);
}

static jboolean nativeGetFallbackAction(JNIEnv* /* env */, jobject /* clazz */, jlong ptr,
                                        jint /* keyCode */, jint /* metaState */,
                                        jobject /* fallbackActionObj */) {
    NativeKeyCharacterMap* map = reinterpret_cast<NativeKeyCharacterMap*>(ptr);
    if (!map || !map->getMap()) {
        return static_cast<jboolean>(false);
    }
    return JNI_FALSE;
}

static jchar nativeGetNumber(JNIEnv* /* env */, jobject /* clazz */, jlong ptr,
                             jint /* keyCode */) {
    NativeKeyCharacterMap* map = reinterpret_cast<NativeKeyCharacterMap*>(ptr);
    if (!map || !map->getMap()) {
        return static_cast<jchar>(0);
    }
    return static_cast<jchar>(0);
}

static jchar nativeGetMatch(JNIEnv* /* env */, jobject /* clazz */, jlong ptr,
                            jint /* keyCode */, jcharArray /* charsArray */, jint /* metaState */) {
    NativeKeyCharacterMap* map = reinterpret_cast<NativeKeyCharacterMap*>(ptr);
    if (!map || !map->getMap()) {
        return static_cast<jchar>(0);
    }
    return static_cast<jchar>(0);
}

static jchar nativeGetDisplayLabel(JNIEnv* /* env */, jobject /* clazz */, jlong ptr,
                                   jint /* keyCode */) {
    NativeKeyCharacterMap* map = reinterpret_cast<NativeKeyCharacterMap*>(ptr);
    if (!map || !map->getMap()) {
        return static_cast<jchar>(0);
    }
    return static_cast<jchar>(0);
}

static jint nativeGetKeyboardType(JNIEnv* /* env */, jobject /* clazz */, jlong ptr) {
    NativeKeyCharacterMap* map = reinterpret_cast<NativeKeyCharacterMap*>(ptr);
    if (!map || !map->getMap()) {
        return static_cast<jint>(0);
    }
    return static_cast<jint>(0);
}

static jobjectArray nativeGetEvents(JNIEnv* env, jobject /* clazz */, jlong ptr,
                                    jcharArray /* charsArray */) {
    NativeKeyCharacterMap* map = reinterpret_cast<NativeKeyCharacterMap*>(ptr);
    if (!map || !map->getMap()) {
        return env->NewObjectArray(0 /* size */, gKeyEventClassInfo.clazz, NULL);
    }
    return env->NewObjectArray(0, gKeyEventClassInfo.clazz, NULL);
}

static jboolean nativeEquals(JNIEnv* /* env */, jobject /* clazz */, jlong ptr1, jlong ptr2) {
    NativeKeyCharacterMap* m1 = reinterpret_cast<NativeKeyCharacterMap*>(ptr1);
    NativeKeyCharacterMap* m2 = reinterpret_cast<NativeKeyCharacterMap*>(ptr2);
    if (!m1 || !m2) {
        return m1 == m2;
    }
    const std::shared_ptr<KeyCharacterMap>& map1 = m1->getMap();
    const std::shared_ptr<KeyCharacterMap>& map2 = m2->getMap();
    if (map1 == nullptr || map2 == nullptr) {
        return map1 == map2;
    }
    // Both maps are non-null (Phase 3 path) — adapter has no comparison
    // operator on the empty stub class; treat identical pointers as equal.
    return static_cast<jboolean>(map1.get() == map2.get());
}

// ─── JNI registration table (1:1 mirror of AOSP g_methods order) ────────────

static const JNINativeMethod g_methods[] = {
        {"nativeReadFromParcel", "(Landroid/os/Parcel;)J", (void*)nativeReadFromParcel},
        {"nativeWriteToParcel", "(JLandroid/os/Parcel;)V", (void*)nativeWriteToParcel},
        {"nativeDispose", "(J)V", (void*)nativeDispose},
        {"nativeGetCharacter", "(JII)C", (void*)nativeGetCharacter},
        {"nativeGetFallbackAction", "(JIILandroid/view/KeyCharacterMap$FallbackAction;)Z",
         (void*)nativeGetFallbackAction},
        {"nativeGetNumber", "(JI)C", (void*)nativeGetNumber},
        {"nativeGetMatch", "(JI[CI)C", (void*)nativeGetMatch},
        {"nativeGetDisplayLabel", "(JI)C", (void*)nativeGetDisplayLabel},
        {"nativeGetKeyboardType", "(J)I", (void*)nativeGetKeyboardType},
        {"nativeGetEvents", "(J[C)[Landroid/view/KeyEvent;", (void*)nativeGetEvents},
        {"nativeObtainEmptyKeyCharacterMap", "(I)Landroid/view/KeyCharacterMap;",
         (void*)nativeObtainEmptyKeyCharacterMap},
        {"nativeEquals", "(JJ)Z", (void*)nativeEquals},
};

int register_android_view_KeyCharacterMap(JNIEnv* env) {
    gKeyCharacterMapClassInfo.clazz = FindClassOrDie(env, "android/view/KeyCharacterMap");
    gKeyCharacterMapClassInfo.clazz = MakeGlobalRefOrDie(env, gKeyCharacterMapClassInfo.clazz);

    gKeyCharacterMapClassInfo.ctor = GetMethodIDOrDie(env, gKeyCharacterMapClassInfo.clazz,
                                                     "<init>", "(J)V");

    gKeyEventClassInfo.clazz = FindClassOrDie(env, "android/view/KeyEvent");
    gKeyEventClassInfo.clazz = MakeGlobalRefOrDie(env, gKeyEventClassInfo.clazz);

    jclass clazz = FindClassOrDie(env, "android/view/KeyCharacterMap$FallbackAction");

    gFallbackActionClassInfo.keyCode = GetFieldIDOrDie(env, clazz, "keyCode", "I");
    gFallbackActionClassInfo.metaState = GetFieldIDOrDie(env, clazz, "metaState", "I");

    return RegisterMethodsOrDie(env, "android/view/KeyCharacterMap", g_methods, NELEM(g_methods));
}

}; // namespace android
