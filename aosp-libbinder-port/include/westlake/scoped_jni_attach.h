// SPDX-License-Identifier: Apache-2.0
//
// Westlake — include/westlake/scoped_jni_attach.h
//
// RAII helper that attaches the current native thread to the JVM in a
// constructor and detaches in the destructor — but only when the scope
// itself performed the attach.  An inner scope nested on an already-attached
// thread (e.g. main thread, or an outer scope further up the stack) returns
// JNI_OK from GetEnv and skips both attach and detach.  The outermost scope
// owns the lifetime.
//
// Purpose / why we need this
// ---------------------------------------------------------------------------
//
// On Android (and Android-on-OHOS via the Westlake substrate) native binder
// threads start *unattached* to the JVM.  Code paths reached on those threads
// — JavaBBinder callbacks, IPCThreadState::joinThreadPool() handlers, future
// adapter reverse-callbacks — must AttachCurrentThread before touching any
// JNIEnv*.  If they then forget to DetachCurrentThread when the thread exits,
// ART CheckJNI fatal-aborts with:
//
//     "thread exited without DetachCurrentThread"
//
// surfacing as a cppcrash on a thread named "<pre-initialize>" + SIGABRT,
// typically shortly after activityResumed rc=0.  Westlake's CR1-fix landed a
// local ScopedAttachedEnv struct inside JavaBBinderHolder.cpp; this header
// promotes that to a proper, reusable RAII class so the rest of the native
// substrate (audio daemon, surface daemon, future V3 adapter callbacks)
// inherits the same discipline.
//
// Inspiration
// ---------------------------------------------------------------------------
//
// Modeled after the sibling project's adapter pattern — ScopedJniAttach in
// westlake-deploy-ohos/v3-hbc/adapter-src/framework/activity/jni/
// ability_scheduler_adapter.cpp (read-only reference).  This is a clean
// Westlake re-implementation with our naming conventions, our existing
// log macros, and a slightly different ergonomics (operator-> on env(),
// explicit `valid()` check) tuned for our `[NPE]`-frame grep harness.
//
// Cited as the prior art chain:  AOSP libnativehelper's
// android::ScopedLocalRef / ScopedUtfChars style → HBC adapter's
// ScopedJniAttach (33 call sites; 2026-05-06 RAII refactor) → this file.
//
// Usage
// ---------------------------------------------------------------------------
//
//     #include "westlake/scoped_jni_attach.h"
//
//     void some_callback_on_unknown_thread(JavaVM* vm) {
//         westlake::ScopedJniAttach attach(vm, "WLK-mycallback");
//         JNIEnv* env = attach.env();
//         if (!attach.valid()) {
//             // GetEnv returned JNI_EVERSION or AttachCurrentThread failed —
//             // bail out without crashing.
//             return;
//         }
//         // ... use env ...
//         // attach goes out of scope — DetachCurrentThread is called
//         // automatically if (and only if) this scope did the attach.
//     }
//
// Nesting is safe:
//
//     void outer(JavaVM* vm) {
//         westlake::ScopedJniAttach a(vm);   // attaches
//         inner(vm);                          // inner scope sees JNI_OK
//                                             // and skips attach+detach
//     }                                       // outer detaches on exit
//
// Constraints
// ---------------------------------------------------------------------------
//
//   * Header-only.  No .cpp companion needed — entire body is inline.
//   * No dependency beyond <jni.h>.  Specifically NOT a dependency on
//     android/log.h — we leave logging to the caller so this can be reused
//     in audio/surface daemon contexts that don't link liblog.
//   * Non-copyable, non-movable.  Lifetime is strictly scope-bound.
//   * Thread name (second ctor arg) is optional; defaults to "WLK-attach".
//     Passing a stable name per call site improves perfetto/strace
//     readability when these threads do appear in profiles.
//
// Self-audit (macro-shim contract — see feedback_macro_shim_contract.md):
//   * No Unsafe.allocateInstance / setAccessible — pure C++ JNI primitives.
//   * No per-app branches — applies to every native thread the substrate
//     reaches, regardless of which APK is hosted.
//   * No new methods on WestlakeContextImpl — this is a native header.
//
// History
// ---------------------------------------------------------------------------
//   2026-05-16  W9 (CR-FF Pattern 1) — promoted from JavaBBinderHolder.cpp
//               local struct + helper function pair into a reusable class.

#pragma once

#include <jni.h>

namespace westlake {

class ScopedJniAttach {
public:
    // Construct + attempt to obtain a JNIEnv* for the current thread.
    //
    //   vm          The cached JavaVM* (typically captured in JNI_OnLoad).
    //               Passing nullptr is tolerated — env() will return nullptr
    //               and valid() will return false.
    //   threadName  Optional name used when AttachCurrentThread synthesizes
    //               a fresh JNI thread.  Defaults to "WLK-attach".  Pass a
    //               call-site-specific name (e.g. "WLK-AudioCb") for better
    //               profiler / strace readability.
    explicit ScopedJniAttach(JavaVM* vm, const char* threadName = "WLK-attach")
        : vm_(vm), env_(nullptr), needsDetach_(false) {
        if (vm_ == nullptr) {
            return;
        }
        jint status = vm_->GetEnv(reinterpret_cast<void**>(&env_),
                                  JNI_VERSION_1_6);
        if (status == JNI_OK) {
            // Already attached — main thread, or an outer ScopedJniAttach.
            // Leave needsDetach_ false so destructor is a no-op.
            return;
        }
        if (status == JNI_EDETACHED) {
            JavaVMAttachArgs args;
            args.version = JNI_VERSION_1_6;
            args.name = const_cast<char*>(threadName != nullptr
                                          ? threadName : "WLK-attach");
            args.group = nullptr;
            if (vm_->AttachCurrentThread(&env_, &args) == JNI_OK) {
                needsDetach_ = true;
            } else {
                env_ = nullptr;
            }
        } else {
            // JNI_EVERSION or other error — env_ stays nullptr; caller can
            // detect via valid().
            env_ = nullptr;
        }
    }

    ~ScopedJniAttach() {
        if (needsDetach_ && vm_ != nullptr) {
            vm_->DetachCurrentThread();
        }
    }

    // Non-copyable, non-movable — RAII scopes are strictly bound to the
    // textual block that declared them.
    ScopedJniAttach(const ScopedJniAttach&) = delete;
    ScopedJniAttach& operator=(const ScopedJniAttach&) = delete;
    ScopedJniAttach(ScopedJniAttach&&) = delete;
    ScopedJniAttach& operator=(ScopedJniAttach&&) = delete;

    // The JNIEnv* for the current thread, or nullptr if attach failed or
    // the supplied JavaVM* was null.  Callers MUST gate JNI use on valid().
    JNIEnv* env() const { return env_; }

    // True iff env() can be safely dereferenced.
    bool valid() const { return env_ != nullptr; }

    // True iff this scope (rather than an outer scope or the main thread)
    // is the one that called AttachCurrentThread.  Diagnostic-only.
    bool ownedAttach() const { return needsDetach_; }

private:
    JavaVM* vm_;
    JNIEnv* env_;
    bool    needsDetach_;
};

}  // namespace westlake
