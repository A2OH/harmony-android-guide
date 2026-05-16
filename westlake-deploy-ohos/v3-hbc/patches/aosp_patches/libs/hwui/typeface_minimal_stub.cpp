/*
 * typeface_minimal_stub.cpp
 *
 * Provides JNI registration entry for android.graphics.Typeface.
 *
 * P15 update (2026-04-11): previously all native methods returned fake
 * handle 1, which would crash when jni/Paint.cpp's getAndroidTypeface()
 * dereferenced it. Now we create a REAL android::Typeface object at
 * register time (with an empty FontCollection — non-functional text but
 * non-crashing), call Typeface::setDefault() so resolveDefault(nullptr)
 * doesn't hit LOG_ALWAYS_FATAL_IF, and return this pointer as the native
 * handle from all factory methods.
 *
 * This doesn't give real text rendering (minikin stubs return empty
 * Layouts) but it does give non-crashing boot. See P10.C.full for the
 * real minikin port that would restore actual text display.
 *
 * G2.14n+ update (2026-05-02): the empty-FontFamily approach below is broken.
 * minikin::FontCollection::create(emptyVector) asserts/segfaults — the upstream
 * minikin design requires at least one valid FontFamily.  Symptom: calls to
 * Typeface natives downstream (libhwui MinikinFontSkia::populateSkFont) hit an
 * inline SkASSERT that calls sk_abort_no_print() — which is a 2-byte trap stub
 * in libskia_canvaskit at file VA 0xd13f98 (linker-folded with deleted-virtual
 * destructors at the same offset).  Result: SIGILL, fault PC ends in f98,
 * looks like a "GrRenderTarget vthunk" crash in the trace but is really
 * sk_abort_no_print() being executed.
 *
 * Fix: load HarmonyOS_Sans.ttf via SkFontMgr_New_OHOS() (libskia_canvaskit's
 * OH-specific factory), wrap into MinikinFontSkia → minikin::Font →
 * minikin::FontFamily → minikin::FontCollection so the resulting Typeface has a
 * non-empty collection.  /system/fonts/HarmonyOS_Sans.ttf is always present on
 * DAYU200 (verified 2026-05-02).
 *
 * Original broken empty-collection path is preserved as a fallback (when the
 * font file is unreadable) — explicitly logged "OH_TypefaceStub fallback"
 * in stderr so we know the broken path was hit.
 */
#include <jni.h>
#include <cstdint>
#include <cstdio>
#include <memory>
#include <vector>
#include <hwui/MinikinSkia.h>
#include <hwui/Typeface.h>
#include <minikin/Font.h>
#include <minikin/FontCollection.h>
#include <minikin/FontFamily.h>
#include "SkRefCnt.h"
#include "SkTypeface.h"

// 2026-05-02 G2.14n+: route Skia font-loading through liboh_hwui_shim.so so
// libhwui doesn't directly depend on libskia_canvaskit.z.so.  Shim function
// (defined in framework/hwui-shim/jni/oh_typeface_init.cpp) returns a freshly
// created SkTypeface plus its backing mmap.  Layout MUST stay in sync with
// the matching struct in oh_typeface_init.cpp.
struct OhFontHandle {
    SkTypeface* skTypeface;
    void*       data;
    unsigned    size;
    const char* path;
};
extern "C" int oh_create_default_skia_handle(OhFontHandle* out);

namespace {

// Lazy global: created on first register call
android::Typeface* gStubTypeface = nullptr;

// Try to build a real Typeface using shim-provided default SkTypeface.
// Returns nullptr if any step fails — caller falls back to empty-collection.
android::Typeface* tryBuildRealTypeface() {
    OhFontHandle h = {};
    if (oh_create_default_skia_handle(&h) != 0 || !h.skTypeface) {
        fprintf(stderr,
            "[OH_TypefaceStub] oh_create_default_skia_handle failed\n");
        return nullptr;
    }
    // Take ownership of the SkTypeface ref (refcount=1 from shim).
    sk_sp<SkTypeface> skTypeface(h.skTypeface);

    // Wrap SkTypeface in MinikinFontSkia.  Use the 7-arg ctor matching the
    // hwui/MinikinSkia.h declaration: typeface, sourceId, fontData, fontSize,
    // filePath, ttcIndex, axes.
    auto minikinFont = std::make_shared<android::MinikinFontSkia>(
            std::move(skTypeface), /*sourceId=*/0, h.data,
            /*fontSize=*/static_cast<size_t>(h.size),
            h.path, /*ttcIndex=*/0,
            std::vector<minikin::FontVariation>());

    // 2026-05-02 G2.14o systemic diagnostic: dump MinikinFontSkia bytes
    // immediately after make_shared so we can verify mTypeface field write.
    {
        const android::MinikinFontSkia* mfs = minikinFont.get();
        const uint32_t* p = reinterpret_cast<const uint32_t*>(mfs);
        fprintf(stderr,
            "[OH_TypefaceStub] MinikinFontSkia @ %p first 64 bytes:\n"
            "  +0  vtable=%08x  +4  mTypeface=%08x  +8  mSourceId=%08x  +12 mFontData=%08x\n"
            "  +16 mFontSize=%08x +20 mTtcIndex=%08x +24 (%08x)         +28 (%08x)\n"
            "  +32 (%08x)         +36 (%08x)         +40 (%08x)         +44 (%08x)\n"
            "  +48 (%08x)         +52 (%08x)         +56 (%08x)         +60 (%08x)\n",
            (void*)mfs,
            p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7],
            p[8], p[9], p[10], p[11], p[12], p[13], p[14], p[15]);
        // Now dereference mTypeface (offset 4) and dump its first 16 bytes
        if (p[1] != 0) {
            const uint32_t* tp = reinterpret_cast<const uint32_t*>(p[1]);
            fprintf(stderr,
                "[OH_TypefaceStub] mTypeface->[+0..16]: %08x %08x %08x %08x\n",
                tp[0], tp[1], tp[2], tp[3]);
        }
    }

    std::vector<std::shared_ptr<minikin::Font>> fonts;
    fonts.push_back(minikin::Font::Builder(minikinFont).build());

    // After build, dump again to see if anything changed
    {
        const android::MinikinFontSkia* mfs = minikinFont.get();
        const uint32_t* p = reinterpret_cast<const uint32_t*>(mfs);
        fprintf(stderr,
            "[OH_TypefaceStub] AFTER Font::Builder.build: MinikinFontSkia @ %p mTypeface=%08x\n",
            (void*)mfs, p[1]);
        if (p[1] != 0) {
            const uint32_t* tp = reinterpret_cast<const uint32_t*>(p[1]);
            fprintf(stderr,
                "[OH_TypefaceStub] AFTER build: mTypeface->fRefCnt=%08x\n", tp[1]);
        }
    }

    std::vector<std::shared_ptr<minikin::FontFamily>> families;
    families.push_back(minikin::FontFamily::create(std::move(fonts)));

    auto collection = minikin::FontCollection::create(std::move(families));

    auto* tf = new android::Typeface();
    tf->fFontCollection = collection;
    tf->fAPIStyle = android::Typeface::kNormal;
    tf->fBaseWeight = 400;
    tf->fStyle = minikin::FontStyle();
    fprintf(stderr,
        "[OH_TypefaceStub] real Typeface built (path=%s size=%u)\n",
        h.path, h.size);
    return tf;
}

// 2026-05-02 G2.14n+: parent vs child split (still needed even with new
// SkData/MakeFromStream path because libhwui's DT_NEEDED chain — libhitrace,
// libsurface — still spawn OS_IPC_*_<pid> worker threads in init_array,
// blocking ZygoteHooks.preFork's waitUntilAllThreadsStopped).
//
// Strategy: parent makes a minimal Typeface (no Skia/minikin work).
// Child marks itself via oh_typeface_mark_child() and then on first call
// to getOrCreateStubTypeface, the lazy ensureFontInitInChild fills in
// fFontCollection on the SAME gStubTypeface pointer (so Java handles
// created in parent's preload stay valid).
extern "C" int oh_typeface_is_child(void);

static bool g_realInitDone = false;

static void ensureFontInitInChild() {
    if (g_realInitDone) return;
    if (!oh_typeface_is_child()) return;  // still in parent — defer

    // Build real Typeface and transfer fields into gStubTypeface (pointer
    // stable across fork; Java handles unaffected).
    android::Typeface* real = tryBuildRealTypeface();
    if (!real) {
        fprintf(stderr,
            "[OH_TypefaceStub] child real init: tryBuildRealTypeface failed\n");
        return;
    }
    gStubTypeface->fFontCollection = real->fFontCollection;
    gStubTypeface->fAPIStyle       = real->fAPIStyle;
    gStubTypeface->fBaseWeight     = real->fBaseWeight;
    gStubTypeface->fStyle          = real->fStyle;
    delete real;
    g_realInitDone = true;
    fprintf(stderr, "[OH_TypefaceStub] child real init complete\n");
}

android::Typeface* getOrCreateStubTypeface() {
    if (gStubTypeface) {
        // Lazy upgrade in child (no-op until ChildMain marks us as child).
        ensureFontInitInChild();
        return gStubTypeface;
    }

    // First call (in parent): minimal Typeface, no Skia/minikin work.
    // fFontCollection stays as default (null shared_ptr) — child fills it in.
    gStubTypeface = new android::Typeface();
    gStubTypeface->fAPIStyle = android::Typeface::kNormal;
    gStubTypeface->fBaseWeight = 400;
    gStubTypeface->fStyle = minikin::FontStyle();
    android::Typeface::setDefault(gStubTypeface);

    // If we're already in child, upgrade now (rare — usually first call is in parent).
    ensureFontInitInChild();
    return gStubTypeface;
}

// PREVIOUS BROKEN FALLBACK (kept commented for future-debug reference):
//   - Empty FontCollection via minikin::FontCollection::create(emptyVec)
//     asserts/segfaults — minikin requires at least one FontFamily.

#define STUB_HANDLE reinterpret_cast<jlong>(getOrCreateStubTypeface())

jlong nativeCreateFromTypeface(JNIEnv*, jclass, jlong, jint) { return STUB_HANDLE; }
jlong nativeCreateFromTypefaceWithExactStyle(JNIEnv*, jclass, jlong, jint, jboolean) { return STUB_HANDLE; }
jlong nativeCreateFromTypefaceWithVariation(JNIEnv*, jclass, jlong, jobject) { return STUB_HANDLE; }
jlong nativeCreateWeightAlias(JNIEnv*, jclass, jlong, jint) { return STUB_HANDLE; }
jlong nativeCreateFromArray(JNIEnv*, jclass, jlongArray, jlong, jint, jint) { return STUB_HANDLE; }
jint nativeGetStyle(JNIEnv*, jclass, jlong) { return 0; }
jint nativeGetWeight(JNIEnv*, jclass, jlong) { return 400; }
void nativeSetDefault(JNIEnv*, jclass, jlong) {
    // No-op: our stub typeface is already set as default at registration.
}
// G2.14k root cause (rediscovered 2026-05-02): Typeface.nativeGetReleaseFunc()
// must return a real fn ptr — NOT 0. The returned long is fed to
// NativeAllocationRegistry as the freeFunction. ART preZygoteFork triggers GC
// which runs Cleaner thunks → NAR.applyFreeFunction(0, ptr) → ((void(*)(void*))0)(ptr)
// → SIGSEGV pc=0 → parent dies SILENTLY (pc=0 is in no mapping, no backtrace).
//
// Symptom in this codebase: parent appspawn-x logs "ZygoteHooks.preFork()" then
// vanishes without faultlog. /data/log/faultlog/temp/ shows file created with
// size 0 then deleted by faultloggerd ("invalid file size 0").
//
// Same fix as adapter's android_graphics_Typeface.cpp Typeface_noop_release:
// return a real noop fn so NAR has a callable target.
//
// PREVIOUS WRONG CODE (kept for reference): jlong nativeGetReleaseFunc(...) { return 0; }
static void Typeface_noop_release(void* /*p*/) {
    // Intentional no-op. Real Typeface cleanup (release SkFontMgr / SkTypeface
    // refs / unmap font data) is unimplemented in this minimal stub.
}
jlong nativeGetReleaseFunc(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&Typeface_noop_release);
}
void nativeRegisterGenericFamily(JNIEnv*, jclass, jstring, jlong) {}
jint nativeWriteTypefaces(JNIEnv*, jclass, jobject, jlongArray) { return 0; }
jlongArray nativeReadTypefaces(JNIEnv*, jclass, jobject) { return nullptr; }
void nativeForceSetStaticFinalField(JNIEnv*, jclass, jstring, jobject) {}
// 2026-05-02 G2.14n+: aligned with adapter's android_graphics_Typeface.cpp
// kMethods table (which was previously verified to match AOSP 14 Java decl).
// Removed nativeGetFamilySize/nativeGetFamily (Java doesn't declare these in
// AOSP 14), added nativeRegisterLocaleList, fixed nativeWriteTypefaces /
// nativeReadTypefaces signatures (added the int buffer-version arg).
//
// Previous broken signatures left here as comments for reference:
//   {"nativeGetFamilySize", "(J)I", ...},                 // not in Java
//   {"nativeGetFamily", "(JI)J", ...},                    // not in Java
//   {"nativeWriteTypefaces", "(Ljava/nio/ByteBuffer;[J)I", ...},   // missing version arg
//   {"nativeReadTypefaces", "(Ljava/nio/ByteBuffer;)[J", ...},     // missing version arg
void nativeAddFontCollections(JNIEnv*, jclass, jlong) {}
void nativeWarmUpCache(JNIEnv*, jclass, jstring) {}
void nativeRegisterLocaleList(JNIEnv*, jclass, jstring) {}
jintArray nativeGetSupportedAxes(JNIEnv*, jclass, jlong) { return nullptr; }
jint nativeWriteTypefaces2(JNIEnv*, jclass, jobject, jint, jlongArray) { return 0; }
jlongArray nativeReadTypefaces2(JNIEnv*, jclass, jobject, jint) { return nullptr; }

const JNINativeMethod gMethods[] = {
    {"nativeCreateFromTypeface", "(JI)J", (void*)nativeCreateFromTypeface},
    {"nativeCreateFromTypefaceWithExactStyle", "(JIZ)J", (void*)nativeCreateFromTypefaceWithExactStyle},
    {"nativeCreateFromTypefaceWithVariation", "(JLjava/util/List;)J", (void*)nativeCreateFromTypefaceWithVariation},
    {"nativeCreateWeightAlias", "(JI)J", (void*)nativeCreateWeightAlias},
    {"nativeCreateFromArray", "([JJII)J", (void*)nativeCreateFromArray},
    {"nativeGetSupportedAxes", "(J)[I", (void*)nativeGetSupportedAxes},
    {"nativeSetDefault", "(J)V", (void*)nativeSetDefault},
    {"nativeGetStyle", "(J)I", (void*)nativeGetStyle},
    {"nativeGetWeight", "(J)I", (void*)nativeGetWeight},
    {"nativeGetReleaseFunc", "()J", (void*)nativeGetReleaseFunc},
    {"nativeRegisterGenericFamily", "(Ljava/lang/String;J)V", (void*)nativeRegisterGenericFamily},
    {"nativeWriteTypefaces", "(Ljava/nio/ByteBuffer;I[J)I", (void*)nativeWriteTypefaces2},
    {"nativeReadTypefaces", "(Ljava/nio/ByteBuffer;I)[J", (void*)nativeReadTypefaces2},
    {"nativeForceSetStaticFinalField", "(Ljava/lang/String;Landroid/graphics/Typeface;)V", (void*)nativeForceSetStaticFinalField},
    {"nativeAddFontCollections", "(J)V", (void*)nativeAddFontCollections},
    {"nativeWarmUpCache", "(Ljava/lang/String;)V", (void*)nativeWarmUpCache},
    {"nativeRegisterLocaleList", "(Ljava/lang/String;)V", (void*)nativeRegisterLocaleList},
};

}  // namespace

extern "C" int register_android_graphics_Typeface(JNIEnv* env) {
    // Pre-create the stub typeface + set as default so any later resolveDefault
    // calls won't fatal.
    (void)getOrCreateStubTypeface();

    jclass clazz = env->FindClass("android/graphics/Typeface");
    if (clazz == nullptr) return -1;
    return env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(JNINativeMethod));
}
