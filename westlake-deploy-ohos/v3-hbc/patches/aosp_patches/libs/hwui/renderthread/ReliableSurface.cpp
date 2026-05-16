// ReliableSurface.cpp -- Adapter stub version (G2.14bf, 2026-05-12).
//
// Reason for stub:
//
//   Upstream AOSP ReliableSurface wraps an ANativeWindow with reliability
//   guarantees: it registers 5 ANativeWindow_set*Interceptor hooks (cancel,
//   dequeue, queue, perform, query) and on producer-death tries to reserve
//   a fallback AHardwareBuffer.  All 5 Interceptor APIs + the 13 underlying
//   ANativeWindow / AHardwareBuffer NDK functions are AOSP-only -- OH's
//   NativeWindow API has no Interceptor concept and provides its own
//   producer-side robust error handling instead.
//
//   Compiling the upstream ReliableSurface.cpp drags in 18 AOSP NDK function
//   UND references (see compile_report.html G2.14bf entry), all of which OH
//   musl/ART reject at dlopen relocation time, blocking libhwui.so load.
//
//   This stub keeps the public class shape (so hwui's CanvasContext can hold
//   a unique_ptr ReliableSurface, call ctor/dtor/init/reserveNext) but has
//   zero NDK calls inside the bodies.  Class member field layout is dictated
//   by ReliableSurface.h which we leave upstream -- so CanvasContext.o view
//   of mNativeSurface field offsets stays consistent.
//
//   Aligns with feedback_bridge_to_oh_ndk_first.md: ReliableSurface is a
//   reliability wrapper, not an underlying capability.  OH NativeWindow
//   already provides robust error handling at the surface layer, so a no-op
//   wrapper is functionally equivalent for the adapter needs.
//
//   Every method logs to stderr with [G2.14bf stub] tag (per memory
//   feedback_debug_print_must_tag.md) so we can confirm if hwui actually
//   calls into ReliableSurface methods at runtime -- useful for later
//   deciding whether a real OH bridge is needed.

#include "renderthread/ReliableSurface.h"

#include <stdio.h>

namespace android::uirenderer::renderthread {

ReliableSurface::ReliableSurface(ANativeWindow* window) : mWindow(window) {
    fprintf(stderr, "[G2.14bf stub] ReliableSurface::ReliableSurface window=%p "
            "(no NDK Interceptor registration)\n", (void*)window);
}

ReliableSurface::~ReliableSurface() {
    fprintf(stderr, "[G2.14bf stub] ReliableSurface::~ReliableSurface window=%p\n",
            (void*)mWindow);
}

void ReliableSurface::init() {
    fprintf(stderr, "[G2.14bf stub] ReliableSurface::init no-op "
            "(5 Interceptor hooks NOT registered -- OH NativeWindow handles producer-death itself)\n");
}

int ReliableSurface::reserveNext() {
    fprintf(stderr, "[G2.14bf stub] ReliableSurface::reserveNext window=%p returning OK\n",
            (void*)mWindow);
    return OK;
}

// ---------------------------------------------------------------------------
// Static hook methods -- Never called at runtime because init does not
// register them with ANativeWindow_set*Interceptor.  Bodies present only so
// the vtable and symbol table stay complete.  If they DO fire (shouldn't),
// the [G2.14bf stub WARN] tag will reveal it in stderr.
// ---------------------------------------------------------------------------

int ReliableSurface::hook_cancelBuffer(ANativeWindow* /*window*/,
                                       ANativeWindow_cancelBufferFn /*cancelBuffer*/,
                                       void* /*data*/,
                                       ANativeWindowBuffer* /*buffer*/,
                                       int /*fenceFd*/) {
    fprintf(stderr, "[G2.14bf stub WARN] hook_cancelBuffer unexpectedly fired\n");
    return 0;
}

int ReliableSurface::hook_dequeueBuffer(ANativeWindow* /*window*/,
                                        ANativeWindow_dequeueBufferFn /*dequeueBuffer*/,
                                        void* /*data*/,
                                        ANativeWindowBuffer** /*buffer*/,
                                        int* /*fenceFd*/) {
    fprintf(stderr, "[G2.14bf stub WARN] hook_dequeueBuffer unexpectedly fired\n");
    return 0;
}

int ReliableSurface::hook_queueBuffer(ANativeWindow* /*window*/,
                                      ANativeWindow_queueBufferFn /*queueBuffer*/,
                                      void* /*data*/,
                                      ANativeWindowBuffer* /*buffer*/,
                                      int /*fenceFd*/) {
    fprintf(stderr, "[G2.14bf stub WARN] hook_queueBuffer unexpectedly fired\n");
    return 0;
}

int ReliableSurface::hook_perform(ANativeWindow* /*window*/,
                                  ANativeWindow_performFn /*perform*/,
                                  void* /*data*/,
                                  int /*operation*/,
                                  va_list /*args*/) {
    fprintf(stderr, "[G2.14bf stub WARN] hook_perform unexpectedly fired\n");
    return 0;
}

int ReliableSurface::hook_query(const ANativeWindow* /*window*/,
                                ANativeWindow_queryFn /*query*/,
                                void* /*data*/,
                                int /*what*/,
                                int* /*value*/) {
    fprintf(stderr, "[G2.14bf stub WARN] hook_query unexpectedly fired\n");
    return 0;
}

// ---------------------------------------------------------------------------
// Private helpers -- Never called from outside the class; their public
// callers are inside hook_X (which themselves are no-ops above).
// ---------------------------------------------------------------------------

bool ReliableSurface::isFallbackBuffer(const ANativeWindowBuffer* /*windowBuffer*/) const {
    return false;
}

ANativeWindowBuffer* ReliableSurface::acquireFallbackBuffer(int /*error*/) {
    fprintf(stderr, "[G2.14bf stub WARN] acquireFallbackBuffer unexpectedly fired -- returning nullptr\n");
    return nullptr;
}

void ReliableSurface::clearReservedBuffer() {
    // No reserved buffer was ever allocated in reserveNext() stub above.
}

}  // namespace android::uirenderer::renderthread
