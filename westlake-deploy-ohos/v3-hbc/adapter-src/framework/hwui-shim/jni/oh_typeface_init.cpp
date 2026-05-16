// ============================================================================
// oh_typeface_init.cpp — provide a default SkTypeface for libhwui's
// typeface_minimal_stub.cpp without forcing libhwui to directly DT_NEEDED
// libskia_canvaskit.z.so.
//
// 2026-05-02 G2.14n+ rewrite: route via SkData::MakeFromFileName +
// SkTypeface::MakeFromStream (NOT via SkFontMgr_New_OHOS).  Reason:
// SkFontMgr_New_OHOS internally talks to OH services through libipc_core,
// spawning OS_IPC_*_<pid> worker threads.  When invoked in appspawn-x's
// PARENT process, those threads are not daemon threads, so
// ZygoteHooks.preFork()'s waitUntilAllThreadsStopped() hangs forever
// (verified 2026-05-02 — parent State R, two OS_IPC threads in
// binder_ioctl, AMS times out at 4.5s).
//
// SkData::MakeFromFileName opens the file via fopen/mmap (libc, no IPC).
// SkTypeface::MakeFromStream uses pure FreeType code (no service calls).
// Both safe in parent.
//
// PREVIOUS BROKEN PATH (kept for reference):
//   sk_sp<SkFontMgr> mgr = SkFontMgr_New_OHOS();   // <-- spawned IPC threads
//   sk_sp<SkTypeface> tf = mgr->makeFromFile(kPath, 0);
// ============================================================================
#include <atomic>
#include <cstdio>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>

#include "core/SkData.h"
#include "core/SkRefCnt.h"
#include "core/SkStream.h"
#include "core/SkTypeface.h"

// G2.14au probe: use fprintf(stderr) so the child redirect (handled by
// appspawn-x ChildMain — writes to /data/service/el1/public/appspawnx/
// adapter_child_<pid>.stderr) captures the output. Avoids adding any
// NEEDED dependency that the dynamic linker might fail to resolve at
// dlopen time (which silently drops the whole .so).
#define G214AU_LOGI(fmt, ...) \
    std::fprintf(stderr, "[G214au_TF] " fmt "\n", ##__VA_ARGS__)

// 2026-05-02 G2.14n+: parent vs child distinguisher.
// Even with the SkFontMgr-free path below, keep a child marker so future
// shim functions that DO need IPC can guard themselves.  Set by
// oh_typeface_mark_child() invoked from appspawn-x's ChildMain::run().
static bool g_isChild = false;
extern "C" void oh_typeface_mark_child(void) { g_isChild = true; }
extern "C" int  oh_typeface_is_child(void)   { return g_isChild ? 1 : 0; }

// Returned to libhwui via oh_create_default_skia_handle.  Layout MUST stay
// in sync with the typedef in typeface_minimal_stub.cpp.
struct OhFontHandle {
    SkTypeface* skTypeface;  // refcount = 1, ownership transfers to caller
    void*       data;        // pointer to font data; caller must NOT free
    unsigned    size;
    const char* path;        // static literal, no free needed
};

extern "C" int oh_create_default_skia_handle(OhFontHandle* out) {
    // G2.14au probe: log every call (typeface init should fire <=2x per process).
    static std::atomic<int> g_count{0};
    int n = ++g_count;
    G214AU_LOGI("oh_create_default_skia_handle #%d isChild=%d out=%p",
                n, g_isChild ? 1 : 0, (void*)out);

    if (!out) return -1;
    *out = OhFontHandle{};

    // 2026-05-02 G2.14n+: even though THIS function uses SkData/MakeFromStream
    // (no FontMgr, no IPC), libhwui's load — pulled in by the act of calling
    // register_android_graphics_Typeface — pulls libhitrace/libsurface/etc.
    // whose init_array side-effects spawn OS_IPC_*_<pid> threads in parent.
    // Those threads block ZygoteHooks.preFork()'s waitUntilAllThreadsStopped.
    //
    // So we still gate this on g_isChild (set by ChildMain via
    // oh_typeface_mark_child).  Parent gets a minimal Typeface; child fills
    // in the real one lazily.
    if (!g_isChild) {
        std::fprintf(stderr,
            "[oh_typeface_init] refused: not in child process yet\n");
        return -1;
    }

    // Default path: HarmonyOS_Sans.ttf (always present on DAYU200).
    static const char* kPath = "/system/fonts/HarmonyOS_Sans.ttf";

    // SkData::MakeFromFileName uses fopen/mmap internally (libc, no IPC, no
    // service calls).  Safe in both parent and child.
    sk_sp<SkData> fontData = SkData::MakeFromFileName(kPath);
    if (!fontData) {
        std::fprintf(stderr,
            "[oh_typeface_init] SkData::MakeFromFileName(%s) returned null\n",
            kPath);
        return -1;
    }

    // Wrap in an SkMemoryStream.  SkTypeface::MakeFromStream takes a
    // unique_ptr<SkStreamAsset>; SkMemoryStream IS-A SkStreamAsset.
    auto stream = std::make_unique<SkMemoryStream>(fontData);

    // SkTypeface::MakeFromStream uses FreeType to parse the font tables —
    // no FontMgr, no system services, no threads.
    sk_sp<SkTypeface> tf = SkTypeface::MakeFromStream(std::move(stream), 0);
    if (!tf) {
        std::fprintf(stderr,
            "[oh_typeface_init] SkTypeface::MakeFromStream(%s) returned null\n",
            kPath);
        return -1;
    }

    // 2026-05-02 G2.14o systemic diagnostic: dump SkTypeface object bytes
    // immediately after MakeFromStream so we can verify refcount initial state.
    // SkRefCntBase has fRefCnt at offset +4 after vtable.
    {
        SkTypeface* raw = tf.get();
        const uint32_t* p = reinterpret_cast<const uint32_t*>(raw);
        std::fprintf(stderr,
            "[oh_typeface_init] SkTypeface @ %p first 32 bytes:\n"
            "  +0  vtable=%08x  +4  fRefCnt=%08x  +8  fWeakCnt=%08x  +12 (%08x)\n"
            "  +16 (%08x)        +20 (%08x)         +24 (%08x)        +28 (%08x)\n",
            (void*)raw, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
    }

    // Keep SkData alive for the life of the SkTypeface.  The stream we built
    // already retains a ref via fontData; SkTypeface holds the stream; so as
    // long as SkTypeface lives, fontData lives.  We expose data + size via
    // OhFontHandle so MinikinFontSkia's ctor can record them.
    //
    // NOTE: data here is the memory-mapped region inside SkData (read-only),
    // which lives for the life of the sk_sp.  Holding a ref via fontData ptr
    // is what keeps it valid; we don't transfer ownership through OhFontHandle.
    out->skTypeface = tf.release();   // ownership → caller (refcount = 1)
    out->data       = const_cast<void*>(fontData->data());
    out->size       = static_cast<unsigned>(fontData->size());
    out->path       = kPath;

    // Leak fontData ref intentionally: SkTypeface holds the stream which
    // holds fontData, so we don't need our own ref past this point.  (The
    // raw pointer in out->data stays valid as long as SkTypeface is alive,
    // which is for the lifetime of gStubTypeface — process lifetime.)
    fontData.release();

    std::fprintf(stderr,
        "[oh_typeface_init] built SkTypeface from %s (%u bytes), no FontMgr/IPC\n",
        kPath, out->size);
    return 0;
}
