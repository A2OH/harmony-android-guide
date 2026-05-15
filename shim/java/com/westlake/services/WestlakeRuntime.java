// SPDX-License-Identifier: Apache-2.0
//
// Westlake CR43 -- WestlakeRuntime readiness signals
//
// Global runtime readiness signals for the Westlake substrate.
//
// CR42 (docs/engine/CR42_VIEW_CTOR_AUDIT.md) surfaced the architectural rule
// that fell out of CR36 (docs/engine/CR36_FINDINGS.md):
//
//   Standalone dalvikvm CANNOT construct ANY framework `android.view.View`
//   subclass through normal ctor paths until M6's surface daemon (or the
//   M7-Step2 host APK) loads `libhwui.so` + `libandroid_runtime.so` into the
//   dalvikvm process.  The `View(Context)` ctor unconditionally calls
//   `RenderNode.create()` whose `nCreate` JNI is unresolved pre-M6, and
//   the ctor explodes with `UnsatisfiedLinkError`.
//
// CR36 fixed the symptom at one site (`Window.<init>` deferred mDecorView
// from eager `new FrameLayout(context)` to `null`, with every consumer
// already null-tolerant).  But "null forever" is only correct PRE-M6 --
// once libhwui is in the process the lazy-init pattern wants to flip
// from "always null" to "construct the real View on demand".
//
// CR42 §6 (CR-D recommendation) proposed a single global readiness flag
// so every CR36-style lazy-init can become transparently M6-aware
// without touching each site individually.  This class is that flag.
//
// Usage pattern (consumer side, illustrative; no consumer wired in this CR):
//
//   // in Window.getDecorView() after CR36 deferral:
//   if (mDecorView == null && WestlakeRuntime.areViewsReady()) {
//       try { mDecorView = new FrameLayout(getContext()); }
//       catch (Throwable t) { mDecorView = null; }
//   }
//   return mDecorView;
//
// Usage pattern (producer side, illustrative; no producer wired in this CR):
//
//   // in M6 surface daemon initializer, after libhwui + libandroid_runtime
//   // are dlopen-ed and RenderNode.nCreate is verified resolvable:
//   System.loadLibrary("hwui");
//   System.loadLibrary("android_runtime");
//   // (optional: probe RenderNode.nCreate via reflection to be paranoid)
//   WestlakeRuntime.markViewsReady();
//
// Anti-drift contract (memory/feedback_macro_shim_contract.md):
//   - architectural readiness signal, NOT a per-app branch
//   - no Unsafe, no setAccessible, no reflection
//   - frozen surface per CR22 pattern: the only fields/methods are
//     `sViewsReady`/`areViewsReady()`/`markViewsReady()`; future
//     additions (`areAudioReady`, `areBindersReady`, etc.) are
//     append-only and never break existing callers
//
// CR43 lands the flag with NO consumer and NO producer wired.  M7-Step2
// (host APK production launch, which already has libhwui) or M6-Step6
// (surface daemon dlopen of libhwui into dalvikvm) will flip the flag
// when it is natural for that milestone; the CR36 lazy-init in
// Window.java will start consulting the flag when its lazy-construct
// branch lands.  Standing up the flag in its own CR keeps that future
// wiring a one-line check rather than a coupled multi-file change.

package com.westlake.services;

/**
 * Global runtime readiness signals for the Westlake substrate.
 *
 * <p>Currently tracks:
 * <ul>
 *   <li>{@link #areViewsReady()} -- {@code true} once {@code libhwui.so} and
 *       {@code libandroid_runtime.so} are loaded into the dalvikvm process
 *       and framework {@code android.view.View} can be safely constructed.
 *       Before this flag flips, ctor-deferring code (e.g. {@code Window.mDecorView}
 *       null deferral per CR36) MUST avoid View construction or wrap it
 *       in {@code try {} catch (Throwable)}.</li>
 * </ul>
 *
 * <p>The flag is initially {@code false}.  M6 surface daemon integration
 * (or M7-Step2 production launch through a host APK that already has
 * libhwui in-process) flips it via {@link #markViewsReady()}.
 *
 * <p>This is a utility class; the constructor is private.  All state is
 * static.  {@code sViewsReady} is {@code volatile} so producer-side
 * publication is visible to all consumer threads without locking.
 *
 * <p>Frozen surface per CR22 pattern: append-only API.  Future readiness
 * signals (e.g. {@code areAudioReady()}, {@code areBindersReady()}) MAY
 * be added but the three existing entries MUST NOT change shape.
 *
 * @see <a href="../../../../docs/engine/CR42_VIEW_CTOR_AUDIT.md">CR42 audit</a>
 * @see <a href="../../../../docs/engine/CR36_FINDINGS.md">CR36 findings</a>
 * @see <a href="../../../../docs/engine/CR43_REPORT.md">CR43 report</a>
 */
public final class WestlakeRuntime {

    /**
     * Backing flag for {@link #areViewsReady()}.
     *
     * <p>{@code volatile} ensures a {@link #markViewsReady()} call on one
     * thread is observed by all subsequent {@link #areViewsReady()} reads
     * on any thread (JLS §17.4.5 happens-before).  No locking needed
     * because the flag is monotone (false -> true, never back).
     */
    private static volatile boolean sViewsReady = false;

    /**
     * Returns {@code true} if framework {@code android.view.View} subclasses
     * can be safely constructed in this process.
     *
     * <p>Pre-M6 (current default): {@code false}.  View ctors chain through
     * {@code RenderNode.<init> -> RenderNode.nCreate} which is an unresolved
     * native; constructing one explodes with {@code UnsatisfiedLinkError}.
     *
     * <p>Post-M6 / host APK: {@code true} once {@link #markViewsReady()}
     * has been called by the M6 surface daemon initializer or by the
     * host APK launcher (which loads {@code libhwui} as part of normal
     * Android process bringup).
     *
     * @return {@code true} iff View construction is currently safe.
     */
    public static boolean areViewsReady() {
        return sViewsReady;
    }

    /**
     * Flips {@link #areViewsReady()} to {@code true}.
     *
     * <p>To be called by:
     * <ul>
     *   <li>The M6 surface daemon initializer, after {@code dlopen}-ing
     *       {@code libhwui.so} and {@code libandroid_runtime.so} into
     *       the dalvikvm process and verifying {@code RenderNode.nCreate}
     *       is resolvable (e.g. via a reflective dry-run probe).</li>
     *   <li>The M7-Step2 host-APK launcher, before delegating to
     *       guest-app code.  A normal Android process already has
     *       libhwui mapped via {@code zygote}, so the call is
     *       unconditional in that path.</li>
     * </ul>
     *
     * <p>Idempotent: calling more than once is a no-op (the flag is
     * monotone false -> true).  Calling on a non-View-ready process
     * is a bug at the call site, not in this method.
     */
    public static void markViewsReady() {
        sViewsReady = true;
    }

    /** Utility class; not instantiable. */
    private WestlakeRuntime() {}
}
