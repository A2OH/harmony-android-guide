// ============================================================================
// AndroidRuntime.cpp
//
// OH-Adapter's replacement of frameworks/base/core/jni/AndroidRuntime.cpp's
// startReg() dispatch. Kept intentionally small: each entry is a register_*
// function that has already been implemented in this project.
//
// Adding a new JNI module:
//   1. Write src/android_<area>_<Class>.cpp with a `register_android_*` fn.
//   2. Declare it in include/AndroidRuntime.h.
//   3. Add a line to `kRegJNI[]` below.
//   4. Rebuild liboh_android_runtime.so.
// ============================================================================

#include "AndroidRuntime.h"

#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <dlfcn.h>
#include <hilog/log.h>

namespace android {

// Cached JavaVM so ApkAssets.cpp's AndroidRuntime::getJNIEnv() works.
static JavaVM* g_vm = nullptr;

void AndroidRuntime::setJavaVM(JavaVM* vm) { g_vm = vm; }

JNIEnv* AndroidRuntime::getJNIEnv() {
    if (g_vm == nullptr) return nullptr;
    JNIEnv* env = nullptr;
    jint rc = g_vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if (rc == JNI_OK && env != nullptr) return env;
    // Attach if needed (not used in HelloWorld bring-up; defensive)
    if (rc == JNI_EDETACHED) {
        JavaVMAttachArgs args = { JNI_VERSION_1_6, "AndroidRuntime", nullptr };
        if (g_vm->AttachCurrentThread(&env, &args) == JNI_OK) return env;
    }
    return nullptr;
}

// G2.4 (2026-04-30): graphics JNI compat shim — last-wins overrides for
// methods whose libhwui impl aborts (fid==null) or whose AOSP libandroid_runtime
// impl is missing (we don't cross-compile that .so).  Spec: doc/graphics_jni_inventory.html §4.1.
extern int register_android_graphics_compat_shim(JNIEnv* env);

// G2.14k (2026-05-01) + 2026-05-02 audit: NAR applyFreeFunction guard.
// User flagged for removal as "defensive hack" but empirical removal breaks
// initChild Java entry on main thread (SIGSEGV pc=0).  TODO P1: identify the
// implicit class-init dependency this provides, then replace.  See header
// comment in libcore_util_NativeAllocationRegistry_guard.cpp.
extern int register_libcore_util_NativeAllocationRegistry_guard(JNIEnv* env);

// G2.14n (2026-05-01): PropertyValuesHolder JNI cache for animation framework.
// View.setContentView path triggers Animator/PropertyValuesHolder static init
// which calls nGetFloatMethod / nGetIntMethod via JNI.
extern int register_android_animation_PropertyValuesHolder(JNIEnv* env);

// B.15: register_android_graphics_Typeface — minimal in-runtime impl.
// Earlier dlopen("libhwui.so") attempt hung (libhwui has heavy GPU init not
// safe in init service ctx).  Compiled directly into liboh_android_runtime.so.
extern int register_android_graphics_Typeface(JNIEnv* env);

// G2.14u (2026-05-07): AOSP-ported android_os_Parcel.cpp.  Provides ~30
// Parcel native methods needed by SurfaceControl / Bundle / Intent / Binder
// IPC paths that ViewRootImpl.relayoutWindow follows on first frame.
// Source: frameworks/base/core/jni/android_os_Parcel.cpp (918 lines), pulled
// in via SRCS_AOSP (compile_oh_android_runtime.sh).
extern int register_android_os_Parcel(JNIEnv* env);

// G2.14w (2026-05-08): adapter-rewritten android_view_KeyCharacterMap.cpp.
// Provides 13 native methods for android.view.KeyCharacterMap.  KCM.load
// fallback path (KCM.obtainEmptyMap → nativeObtainEmptyKeyCharacterMap)
// previously hit UnsatisfiedLinkError because no register_X covered it,
// blocking PhoneWindow.preparePanel after InputManagerAdapter VIRTUAL_KEYBOARD
// double mapping landed.  Source pattern: frameworks/base/core/jni/
// android_view_KeyCharacterMap.cpp (AOSP 14, 284 lines), adapter-adapted to
// strip <binder/Parcel.h> + <input/*.h> deps; mMap permanently nullptr in
// Phase 1 (Phase 3 will populate from OH MMI keymap data).
extern int register_android_view_KeyCharacterMap(JNIEnv* env);

// B.20.r7: register_android_os_GraphicsEnvironment — 11 natives, all no-op
// safe defaults. setupGraphicsSupport line 6680 calls isDebuggable() then
// (within setup) calls layer/driver/ANGLE configuration natives.  AOSP
// registers these in libandroid_servers.so (system_server-side); on OH the
// child process needs them resolved in liboh_android_runtime.so.
extern int register_android_os_GraphicsEnvironment(JNIEnv* env);

// Phase 2 r27 (2026-04-28): all graphics register_X functions
// (Paint/Canvas/RenderNode/HardwareRenderer/Matrix/Path/...) come from real
// cross-compiled libhwui.so via dlopen+dlsym in startReg below.  Stub
// register_android_graphics_Canvas / HardwareRenderer / RenderNode / Paint
// are retired (no longer compiled into liboh_android_runtime).

struct RegJNIRec {
    const char* name;
    int (*proc)(JNIEnv*);
};

// Dispatch table. Grows as we port more AOSP register_* functions.
static const RegJNIRec kRegJNI[] = {
    { "register_android_util_Log",            register_android_util_Log },
    { "register_android_util_EventLog",       register_android_util_EventLog },
    { "register_android_app_Activity",        register_android_app_Activity },
    { "register_android_os_SystemProperties", register_android_os_SystemProperties },
    { "register_android_os_Trace",            register_android_os_Trace },
    { "register_android_os_Process",          register_android_os_Process },
    { "register_android_os_SystemClock",      register_android_os_SystemClock },
    { "register_android_os_Binder",           register_android_os_Binder },
    // 2026-05-07 G2.14u: AOSP-ported android_os_Parcel.cpp providing
    // ~30 Parcel native methods (nativeCreate / nativeWriteToParcel /
    // nativeMarshall / etc).  HelloWorld TextView path → ViewRootImpl
    // .performTraversals → relayoutWindow → adapter WindowSessionAdapter
    // .relayout → SurfaceControl.<init> → Parcel.obtain → Parcel.<init>
    // → Parcel.nativeCreate UnsatisfiedLinkError before this entry.
    { "register_android_os_Parcel",           register_android_os_Parcel },
    { "register_android_view_SurfaceControl", register_android_view_SurfaceControl },
    { "register_android_view_SurfaceSession", register_android_view_SurfaceSession },
    { "register_android_view_DisplayEventReceiver", register_android_view_DisplayEventReceiver },
    { "register_android_view_InputChannel",   register_android_view_InputChannel },
    { "register_android_view_KeyCharacterMap", register_android_view_KeyCharacterMap },
    { "register_android_view_InputEventReceiver", register_android_view_InputEventReceiver },
    { "register_android_content_AssetManager", register_android_content_AssetManager },
    { "register_android_os_MessageQueue",     register_android_os_MessageQueue },
    { "register_android_content_res_ApkAssets", register_android_content_res_ApkAssets },
    { "register_android_content_StringBlock", register_android_content_StringBlock },
    { "register_android_content_XmlBlock",    register_android_content_XmlBlock },
    { "register_com_android_internal_os_ClassLoaderFactory", register_com_android_internal_os_ClassLoaderFactory },
    { "register_com_android_internal_util_VirtualRefBasePtr", register_com_android_internal_util_VirtualRefBasePtr },
    { "register_android_graphics_Typeface",   register_android_graphics_Typeface },
    { "register_android_os_GraphicsEnvironment", register_android_os_GraphicsEnvironment },
    // 2026-05-02 audit: kept; see libcore_util_NativeAllocationRegistry_guard.cpp
    // header.  TODO P1: identify implicit dep then replace + remove.
    { "register_libcore_util_NativeAllocationRegistry_guard",
      register_libcore_util_NativeAllocationRegistry_guard },
    { "register_android_animation_PropertyValuesHolder",
      register_android_animation_PropertyValuesHolder },
    // Phase 2 (r27): graphics natives provided by real libhwui.so via dlopen
    // block in startReg below — see kHwuiRegFns table.
};

static constexpr size_t kRegJNICount = sizeof(kRegJNI) / sizeof(kRegJNI[0]);

// 2026-05-01 G2.14n: hook RegisterNatives to dump every (class, method, sig, fnPtr)
// registration. Goal: identify which Java method's ArtMethod entry_point is being
// set to a corrupted address that lands in lib .bss when later invoked via
// art_quick_invoke_stub_internal blx r12 → SIGILL.
namespace {
using OrigRegisterNatives = jint (*)(JNIEnv*, jclass, const JNINativeMethod*, jint);
static OrigRegisterNatives g_orig_register_natives = nullptr;

static jint hooked_RegisterNatives(JNIEnv* env, jclass clazz,
                                    const JNINativeMethod* methods, jint nMethods) {
    // Resolve class name for logging. Use stack buffer to avoid heap allocation
    // here (we re-enter JNI carefully — no recursion into RegisterNatives).
    char className[256] = "?";
    if (clazz) {
        // Use original FindClass/GetMethodID/CallObjectMethod/GetStringUTFChars
        // through env->functions, but those are unaffected by our hook (only
        // RegisterNatives is patched).
        jclass cls = env->GetObjectClass(clazz);  // returns Class.class
        if (cls) {
            jmethodID mGetName = env->GetMethodID(cls, "getName", "()Ljava/lang/String;");
            if (mGetName) {
                jstring nameStr = (jstring)env->CallObjectMethod(clazz, mGetName);
                if (nameStr) {
                    const char* utf = env->GetStringUTFChars(nameStr, nullptr);
                    if (utf) {
                        snprintf(className, sizeof(className), "%s", utf);
                        env->ReleaseStringUTFChars(nameStr, utf);
                    }
                    env->DeleteLocalRef(nameStr);
                }
            }
            env->DeleteLocalRef(cls);
        }
        if (env->ExceptionCheck()) env->ExceptionClear();
    }
    for (jint i = 0; i < nMethods; i++) {
        // 2026-05-02 G2.14n+: enhanced flagging for suspicious fnPtr.
        // SIGILL crash signature: PC ends in f98 (ARM) or f99 (Thumb bit set)
        // and falls inside libskia_canvaskit.z.so .text. Flag both criteria.
        uintptr_t fp = (uintptr_t)methods[i].fnPtr;
        uintptr_t pageOff = fp & 0xFFFu;
        bool offHit = (pageOff == 0xf98u) || (pageOff == 0xf99u);
        bool libHit = false;
        const char* libName = "?";
        Dl_info info;
        if (methods[i].fnPtr && dladdr(methods[i].fnPtr, &info) && info.dli_fname) {
            libName = info.dli_fname;
            if (strstr(info.dli_fname, "libskia_canvaskit") != nullptr) libHit = true;
        }
        const char* tag = (offHit || libHit) ? "OH_RegHook_BAD" : "OH_RegHook";
        HiLogPrint(LOG_CORE, LOG_INFO, 0xD000F00u, tag,
            "%{public}s%{public}s::%{public}s%{public}s -> fn=%{public}p (lib=%{public}s pageOff=0x%{public}x)",
            (offHit || libHit) ? "[!!! SUSPECT] " : "",
            className,
            methods[i].name ? methods[i].name : "?",
            methods[i].signature ? methods[i].signature : "?",
            methods[i].fnPtr, libName, pageOff);
    }
    return g_orig_register_natives(env, clazz, methods, nMethods);
}

static void install_register_natives_hook(JNIEnv* env) {
    if (g_orig_register_natives) return;  // already installed
    // env->functions is `const struct JNINativeInterface*`. We allocate a copy
    // we can patch, then point env->functions at the copy.
    static struct JNINativeInterface patched;
    const struct JNINativeInterface* orig =
        *(const struct JNINativeInterface**)env;
    memcpy(&patched, orig, sizeof(patched));
    g_orig_register_natives = orig->RegisterNatives;
    patched.RegisterNatives = hooked_RegisterNatives;
    // Cast away const and overwrite the JNIEnv's functions pointer.
    *(const struct JNINativeInterface**)env = &patched;
    HiLogPrint(LOG_CORE, LOG_INFO, 0xD000F00u, "OH_RegHook",
        "RegisterNatives hook INSTALLED orig=%{public}p hook=%{public}p",
        (void*)g_orig_register_natives, (void*)hooked_RegisterNatives);
}
}  // namespace

int AndroidRuntime::startReg(JNIEnv* env) {
    fprintf(stderr, "[liboh_android_runtime] startReg entering (%zu modules)\n",
            kRegJNICount);

    // Cache JavaVM so AOSP-ported sources (ApkAssets.cpp et al.) can call
    // AndroidRuntime::getJNIEnv() from background threads.
    JavaVM* vm = nullptr;
    if (env->GetJavaVM(&vm) == JNI_OK) {
        setJavaVM(vm);
        fprintf(stderr, "[liboh_android_runtime] cached JavaVM=%p\n", (void*)vm);
    }

    // 2026-05-02 G2.14n+: install RegisterNatives audit hook unconditionally
    // for this debug iteration. Hook flags fnPtrs whose page offset is f98/f99
    // OR whose containing lib is libskia_canvaskit (per SIGILL signature).
    // Tag: OH_RegHook (normal) / OH_RegHook_BAD (suspicious).
    install_register_natives_hook(env);

    // 2026-05-02 G2.14r: bootstrap libjavacore.so by invoking its JNI_OnLoad
    // ONCE before our own kRegJNI loop runs.  Adapter never calls
    // System.loadLibrary("javacore") on the Java side, so absent this hook
    // libjavacore's 12 register_libcore_* / register_sun_misc_Unsafe /
    // register_java_lang_invoke_* / register_android_system_OsConstants
    // never run, leaving the corresponding native methods unbound.  ART can
    // intrinsify some (sun.misc.Unsafe atomics) but not e.g.
    // NativeAllocationRegistry.applyFreeFunction — first GC cycle calls it
    // and SEGV pc=0 follows (G2.14r root cause).  Calling JNI_OnLoad here
    // restores the canonical AOSP init flow once for the parent process;
    // child forks inherit the registrations.
    {
        void* libjc = dlopen("libjavacore.so", RTLD_NOW);
        if (libjc != nullptr) {
            using OnLoadFn = jint (*)(JavaVM*, void*);
            OnLoadFn onload = reinterpret_cast<OnLoadFn>(
                dlsym(libjc, "JNI_OnLoad"));
            if (onload != nullptr && vm != nullptr) {
                jint rc = onload(vm, nullptr);
                fprintf(stderr,
                    "[liboh_android_runtime] libjavacore JNI_OnLoad rc=0x%x %s\n",
                    rc, (rc == JNI_VERSION_1_6) ? "(OK)" : "(unexpected)");
                if (env->ExceptionCheck()) {
                    fprintf(stderr,
                        "[liboh_android_runtime] libjavacore JNI_OnLoad raised exception:\n");
                    env->ExceptionDescribe();
                    env->ExceptionClear();
                }
            } else {
                fprintf(stderr,
                    "[liboh_android_runtime] libjavacore JNI_OnLoad symbol missing or vm=null\n");
            }
        } else {
            fprintf(stderr,
                "[liboh_android_runtime] libjavacore.so dlopen FAILED: %s\n",
                dlerror() ? dlerror() : "(null)");
        }
    }

    // Bound a local-ref frame generously; each register_* may create a handful
    // of class / method references that won't be released until frame pop.
    if (env->PushLocalFrame(200) < 0) {
        fprintf(stderr, "[liboh_android_runtime] PushLocalFrame failed\n");
        return -1;
    }

    for (size_t i = 0; i < kRegJNICount; ++i) {
        int rc = kRegJNI[i].proc(env);
        if (rc < 0) {
            fprintf(stderr, "[liboh_android_runtime] %s failed (rc=%d)\n",
                    kRegJNI[i].name, rc);
            env->PopLocalFrame(nullptr);
            return -1;
        }
        fprintf(stderr, "[liboh_android_runtime]   ok %s\n", kRegJNI[i].name);
    }

    env->PopLocalFrame(nullptr);

    // ============================================================
    // Phase 2 (r27) — dlopen real libhwui.so + invoke its 27 register_X
    // functions to register Paint/Canvas/RenderNode/HardwareRenderer/...
    // graphics natives with REAL Skia-backed implementations.  No stubs.
    //
    // libhwui.so is OH-cross-built (DT_NEEDED: liboh_hwui_shim.so /
    // liboh_skia_rtti_shim.so / libskia_canvaskit.z.so / libEGL.so
    // / libGLESv3.so / libutils.so etc., all available on device).
    // Earlier r15 diag confirmed libhwui dlopen alone succeeds in JVM ctx.
    // ============================================================
    if (env->PushLocalFrame(50) >= 0) {
        const char* kHwuiPaths[] = {
            "/system/android/lib/libhwui.so",
            "libhwui.so",
        };
        void* hwui = nullptr;
        for (const char* p : kHwuiPaths) {
            hwui = dlopen(p, RTLD_NOW | RTLD_GLOBAL);
            if (hwui) {
                fprintf(stderr, "[liboh_android_runtime] dlopen %s OK\n", p);
                break;
            } else {
                fprintf(stderr, "[liboh_android_runtime] dlopen %s FAIL: %s\n",
                        p, dlerror());
            }
        }
        if (hwui) {
            // 45 register_X mangled symbol names extracted from libhwui.so
            // dynsym table (`llvm-readelf --dyn-syms ... | grep register_android`).
            // Mangled names use Itanium C++ ABI:
            //   - `_Z<len>register_android_*P7_JNIEnv` for global-namespace fns
            //   - `_ZN7android<len>register_android_*EP7_JNIEnv` for android:: fns
            using RegFn = int (*)(JNIEnv*);
            struct HwuiReg { const char* name; const char* sym; };
            static const HwuiReg kHwuiRegFns[] = {
                // 2026-05-07 G2.14s: Graphics is registered below, AFTER ColorSpace,
                // matching AOSP frameworks/base/libs/hwui/apex/jni_runtime.cpp:104-109
                // canonical order (Canvas, ColorSpace, Graphics, Bitmap, ...).
                // History blame for the prior SKIP: see the larger comment at the
                // ColorSpace → Graphics block below.
                {"BitmapFactory",                "_Z39register_android_graphics_BitmapFactoryP7_JNIEnv"},
                {"Matrix",                       "_ZN7android32register_android_graphics_MatrixEP7_JNIEnv"},
                {"BitmapRegionDecoder",          "_Z45register_android_graphics_BitmapRegionDecoderP7_JNIEnv"},
                {"Interpolator",                 "_Z38register_android_graphics_InterpolatorP7_JNIEnv"},
                {"CreateJavaOutputStreamAdaptor","_Z55register_android_graphics_CreateJavaOutputStreamAdaptorP7_JNIEnv"},
                {"PathMeasure",                  "_ZN7android37register_android_graphics_PathMeasureEP7_JNIEnv"},
                {"GraphicsStatsService",         "_Z46register_android_graphics_GraphicsStatsServiceP7_JNIEnv"},
                {"Picture",                      "_ZN7android33register_android_graphics_PictureEP7_JNIEnv"},
                {"ColorFilter",                  "_ZN7android37register_android_graphics_ColorFilterEP7_JNIEnv"},
                {"Camera",                       "_Z32register_android_graphics_CameraP7_JNIEnv"},
                {"Gainmap",                      "_ZN7android33register_android_graphics_GainmapEP7_JNIEnv"},
                {"Region",                       "_ZN7android32register_android_graphics_RegionEP7_JNIEnv"},
                {"Paint",                        "_ZN7android31register_android_graphics_PaintEP7_JNIEnv"},
                {"DisplayListCanvas",            "_ZN7android39register_android_view_DisplayListCanvasEP7_JNIEnv"},
                {"ByteBufferStreamAdaptor",      "_Z49register_android_graphics_ByteBufferStreamAdaptorP7_JNIEnv"},
                {"Movie",                        "_Z31register_android_graphics_MovieP7_JNIEnv"},
                {"Mesh",                         "_ZN7android30register_android_graphics_MeshEP7_JNIEnv"},
                {"ThreadedRenderer",             "_ZN7android38register_android_view_ThreadedRendererEP7_JNIEnv"},
                {"PathIterator",                 "_ZN7android38register_android_graphics_PathIteratorEP7_JNIEnv"},
                // 2026-04-30 G2.4 (graphics_jni_inventory §3.3): ColorSpace
                // must register BEFORE ImageDecoder/Bitmap/HardwareBufferRenderer.
                // ImageDecoder's register_X caches a jfieldID for ColorSpace
                // static field (e.g. SRGB).  If ColorSpace class isn't loaded
                // when ImageDecoder.cpp does GetStaticFieldID, the cached fid
                // stays null → first nGetColorSpace call CheckJNI-aborts with
                // "JNI DETECTED ERROR IN APPLICATION: fid == null".
                // (Was previously after ImageDecoder; moved up here.)
                {"ColorSpace",                   "_ZN7android36register_android_graphics_ColorSpaceEP7_JNIEnv"},
                // 2026-05-07 G2.14s: Graphics MUST be registered here — after ColorSpace
                // (which Graphics's register fn reads via gColorSpace_Named_class for
                // SRGB/EXTENDED_SRGB/etc fields) and before ANY Paint/Bitmap/Region/
                // Canvas native gets first invoked at runtime.
                //
                // register_android_graphics_Graphics initializes the file-static
                // globals gFontMetricsInt_class / gFontMetrics_class / gRect_class /
                // gRectF_class / gPoint_class / gPointF_class / gBitmapConfig_class /
                // gCanvas_class / gPicture_class / gRegion_class / gByte_class /
                // gVMRuntime_class / gColorSpace_class / gColorSpaceRGB_class /
                // gTransferParameters_class plus their associated fieldIDs in
                // libhwui's own translation units.  Paint.cpp's nGetFontMetricsInt
                // does `IsInstanceOf(metrics, gFontMetricsInt_class)` and SetIntField
                // on gFontMetricsInt_top/ascent/descent/bottom/leading — without this
                // call those globals stay NULL → JNI DETECTED ERROR aborts in CheckJNI.
                //
                // History blame: 2026-05-06 SKIPPED on the false-attribution theory
                // that adding Graphics required a boot image rebuild, which itself
                // had failed with `Class mismatch String objectSize 467 vs 459`
                // dex2oat ABI mismatch.  That ABI mismatch is an INDEPENDENT issue
                // (ART build flags drift vs device libart) and has nothing to do
                // with whether libhwui's register_X is invoked at startReg time.
                // Verification (2026-05-07):
                //   nm -D libhwui.so | grep register_android_graphics_Graphics
                //   → 000dedcd T _Z34register_android_graphics_GraphicsP7_JNIEnv
                // The symbol IS exported; dlsym at startReg succeeds; the call
                // initializes Graphics's LOCAL g_* globals (LOCAL is fine — same .so
                // internal access, see jni_Graphics.o objdump showing 'b' BSS for
                // _ZL18gFontMetrics_class etc).  Paint native then sees init'd globals.
                {"Graphics",                     "_Z34register_android_graphics_GraphicsP7_JNIEnv"},
                {"AnimatedImageDrawable",        "_Z56register_android_graphics_drawable_AnimatedImageDrawableP7_JNIEnv"},
                {"PathParser",                   "_ZN7android32register_android_util_PathParserEP7_JNIEnv"},
                {"TextureLayer",                 "_ZN7android38register_android_graphics_TextureLayerEP7_JNIEnv"},
                {"AnimatedVectorDrawable",       "_ZN7android57register_android_graphics_drawable_AnimatedVectorDrawableEP7_JNIEnv"},
                {"NativeInterpolatorFactory",    "_ZN7android61register_android_graphics_animation_NativeInterpolatorFactoryEP7_JNIEnv"},
                {"ImageDecoder",                 "_Z38register_android_graphics_ImageDecoderP7_JNIEnv"},
                {"RenderNode",                   "_ZN7android32register_android_view_RenderNodeEP7_JNIEnv"},
                {"DrawFilter",                   "_ZN7android36register_android_graphics_DrawFilterEP7_JNIEnv"},
                {"RenderEffect",                 "_Z38register_android_graphics_RenderEffectP7_JNIEnv"},
                {"NinePatch",                    "_Z35register_android_graphics_NinePatchP7_JNIEnv"},
                {"Canvas",                       "_ZN7android32register_android_graphics_CanvasEP7_JNIEnv"},
                {"HardwareBufferRenderer",       "_ZN7android48register_android_graphics_HardwareBufferRendererEP7_JNIEnv"},
                {"Bitmap",                       "_Z32register_android_graphics_BitmapP7_JNIEnv"},
                {"HardwareRendererObserver",     "_ZN7android50register_android_graphics_HardwareRendererObserverEP7_JNIEnv"},
                {"Path",                         "_ZN7android30register_android_graphics_PathEP7_JNIEnv"},
                {"Shader",                       "_Z32register_android_graphics_ShaderP7_JNIEnv"},
                {"VectorDrawable",               "_ZN7android49register_android_graphics_drawable_VectorDrawableEP7_JNIEnv"},
                {"MaskFilter",                   "_Z36register_android_graphics_MaskFilterP7_JNIEnv"},
                {"PathEffect",                   "_Z36register_android_graphics_PathEffectP7_JNIEnv"},
                {"RenderNodeAnimator",           "_ZN7android54register_android_graphics_animation_RenderNodeAnimatorEP7_JNIEnv"},
                {"CanvasProperty",               "_ZN7android40register_android_graphics_CanvasPropertyEP7_JNIEnv"},
                {"YuvImage",                     "_Z34register_android_graphics_YuvImageP7_JNIEnv"},
                {"FontFamily",                   "_ZN7android36register_android_graphics_FontFamilyEP7_JNIEnv"},
                {"MeshSpecification",            "_ZN7android43register_android_graphics_MeshSpecificationEP7_JNIEnv"},
                // 2026-05-02 G2.14r: NEW font API (android.graphics.fonts.*)
                // — required by AOSP SystemFonts.buildSystemFallback chain.
                // Without these, Font$Builder.nInitBuilder() throws
                // UnsatisfiedLinkError → setSystemFontMap NPE → handleBindApplication
                // fails → mInitialApplication = null → ConfigurationController NPE.
                // libhwui exports them via fonts/Font.cpp + fonts/FontFamily.cpp
                // (compiled into libhwui.so in G2.14q Path A).
                {"fonts.Font",                   "_ZN7android36register_android_graphics_fonts_FontEP7_JNIEnv"},
                {"fonts.FontFamily",             "_ZN7android42register_android_graphics_fonts_FontFamilyEP7_JNIEnv"},
                // 2026-05-07 G2.14t: text/* register fns required by AOSP
                // apex/jni_runtime.cpp:149-152.  Order kept identical to AOSP
                // (MeasuredText → LineBreaker → TextShaper → GraphemeBreak).
                //
                // Without these registered, HelloWorld TextView.onMeasure path
                // → StaticLayout.generate → LineBreaker$Builder.build →
                // LineBreaker.<clinit>:450 → nGetReleaseFunc() throws
                // UnsatisfiedLinkError (No implementation found for ...) →
                // ART runtime exception → AMS schedulerDied → kill -9 child.
                //
                // Build dependency: build/compile_libhwui_jni.sh must compile
                // frameworks/base/libs/hwui/jni/text/*.cpp (4 files); the main
                // loop in that script globs jni/*.cpp (top level only) so
                // jni/text/ subdir was previously missed.  G2.14t patched both
                // sides simultaneously.
                // mangled name lengths verified against actual nm -D libhwui.so output:
                //   MeasuredText = 43 chars, LineBreaker = 42, TextShaper = 41, GraphemeBreak = 44
                {"text.MeasuredText",            "_ZN7android43register_android_graphics_text_MeasuredTextEP7_JNIEnv"},
                {"text.LineBreaker",             "_ZN7android42register_android_graphics_text_LineBreakerEP7_JNIEnv"},
                {"text.TextShaper",              "_ZN7android41register_android_graphics_text_TextShaperEP7_JNIEnv"},
                {"text.GraphemeBreak",           "_ZN7android44register_android_graphics_text_GraphemeBreakEP7_JNIEnv"},
                // 2026-05-01 G2.14n: Typeface real-impl from libhwui DEFERRED
                // — libhwui register_android_graphics_Typeface aborts during
                // startReg (SIGABRT in parent appspawn-x).  Likely needs
                // init_FontUtils / GraphicsJNI prior init that we haven't wired.
                // Keep stub for now; revisit after addressing init path.
            };
            int hwui_ok = 0, hwui_fail = 0;
            for (const auto& r : kHwuiRegFns) {
                RegFn fn = reinterpret_cast<RegFn>(dlsym(hwui, r.sym));
                if (!fn) {
                    fprintf(stderr, "[liboh_android_runtime]   dlsym %s FAIL: %s\n",
                            r.name, dlerror());
                    hwui_fail++;
                    continue;
                }
                int rc = fn(env);
                if (env->ExceptionCheck()) {
                    env->ExceptionDescribe();
                    env->ExceptionClear();
                    hwui_fail++;
                    continue;
                }
                if (rc == 0) {
                    fprintf(stderr, "[liboh_android_runtime]   libhwui:register_%s OK\n", r.name);
                    hwui_ok++;
                } else {
                    fprintf(stderr, "[liboh_android_runtime]   libhwui:register_%s rc=%d\n", r.name, rc);
                    hwui_fail++;
                }
            }
            fprintf(stderr, "[liboh_android_runtime] libhwui register: %d ok / %d fail\n",
                    hwui_ok, hwui_fail);
        } else {
            fprintf(stderr, "[liboh_android_runtime] WARN: libhwui not loaded — graphics natives unbound\n");
        }

        // G2.4 (2026-04-30): apply graphics JNI compat shim — last-wins
        // RegisterNatives that override libhwui impls known to abort
        // (e.g., ImageDecoder.nGetColorSpace fid==null) and fill in
        // framework JNI methods we don't have a real libandroid_runtime
        // for (Surface / BLASTBufferQueue / DisplayEventReceiver).
        // MUST run AFTER libhwui's register loop above so our overrides win.
        register_android_graphics_compat_shim(env);

        env->PopLocalFrame(nullptr);
    }

    fprintf(stderr, "[liboh_android_runtime] startReg exiting (ok)\n");
    return 0;
}

}  // namespace android
