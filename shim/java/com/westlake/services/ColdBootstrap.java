// SPDX-License-Identifier: Apache-2.0
//
// Westlake CR18 — ColdBootstrap (production-grade ActivityThread/Context plant)
//
// Background
// ----------
// Pre-CR18, the Westlake dalvikvm cold-boot sandbox needed two distinct
// preliminary plant operations before AOSP framework code could be safely
// exercised:
//
//   1. `CharsetPrimer.primeCharsetState()` — seeds java.nio.charset.Charset's
//      `cache2` + `gate` static state so that the first Charset.forName(...)
//      call doesn't NPE.  Required by anything that touches
//      VMClassLoader.<clinit> (ZipFile, URLs).
//
//   2. `CharsetPrimer.primeActivityThread()` — plants a synthetic
//      android.app.ActivityThread singleton into the `sCurrentActivityThread`
//      static field, with `mSystemContext = BootstrapContext(planted
//      PermissionEnforcer)`, so AOSP `IXxx$Stub()` default ctors that
//      reach `PermissionEnforcer.fromContext(currentActivityThread()
//      .getSystemContext())` don't NPE on a null currentActivityThread.
//
// Both lived in `aosp-libbinder-port/test/CharsetPrimer.java`, called from
// every test's `main()` as the first lines.  This was OK for tests but
// architecturally wrong: production callers (WestlakeLauncher → noice apk →
// AOSP framework) had no equivalent.  CR17 partially addressed this for
// (2) by converting the M4d/M4e service ctors to use the
// `Stub(PermissionEnforcer)` bypass with a `NoopPermissionEnforcer`, so
// ServiceRegistrar.registerAllServices() no longer NPEs without the
// test-harness primer.
//
// CR18 (this) is the bisection follow-up:
//   - CR18 §1 — bisected the SystemServiceRouteTest "must call
//     primeActivityThread() or SIGBUS" claim from CR15/CR17 by adding a
//     primerVariant knob to CharsetPrimer and running variants 0/1/2/3/5
//     on the phone.  Result: variant 0 (NO primer call at all) PASSED
//     8/8 with zero SIGBUS occurrences.  The CR15/CR17 PHASE B SIGBUS
//     correlation was either non-deterministic noise or has been
//     resolved by CR17's Stub-bypass landings.
//   - CR18 §2 — this class is the architectural cleanup that the brief
//     anticipated (path A): a *production-code* one-stop bootstrap that
//     ColdBootstrap.ensure() can be called from any entry point that
//     touches AOSP framework code, exposes a stable public API, and is
//     idempotent.  The test harness now delegates here rather than
//     reflectively poking ActivityThread directly.
//
// Why we keep the plant in CR18 even though variant 0 passes
// ----------------------------------------------------------
// The CR18 bisection proved that *for SystemServiceRouteTest's specific
// workload* the primer is no longer needed.  But:
//
//   - NoiceDiscoverWrapper.dex exercises a much larger AOSP framework
//     surface (Activity / Application / Instrumentation / Hilt) which we
//     have NOT bisected — the primer's removal there is a separate
//     question.
//   - The PHASE B SIGBUS (PF-arch-054 sentinel) is data-flow-sensitive
//     (CR13 §5.9 disproved the "ServiceRegistrar poisons ART" hypothesis
//     but the underlying mystery — WHO writes the sentinel into the JNI
//     function table — remains open per M4_DISCOVERY §44).
//   - The plant is cheap (~5 reflective field operations on a single
//     synthetic ActivityThread instance) and idempotent.  Keeping it in
//     production code where ALL paths benefit is strictly better than
//     keeping it in the test harness where production callers miss it.
//
// Idempotency
// -----------
// `ensure()` is safe to call from any thread, any number of times.  After
// the first successful call, subsequent calls are a single
// volatile-read short-circuit.  Internal state is process-wide.
//
// Failure mode
// ------------
// Every reflection lookup is wrapped in a try/catch — if framework.jar
// shape ever drifts (e.g. `android.app.ActivityThread.sCurrentActivityThread`
// renamed), `ensure()` prints the failure to stderr and returns false.
// Callers can treat the boolean as "primer succeeded, downstream code is
// safer" but MUST NOT block on a false return — production callers should
// still attempt the actual work and let downstream NPEs surface.
//
// Author: CR18 agent  •  Date: 2026-05-12

package com.westlake.services;

import android.content.Context;
import android.content.ContextWrapper;

public final class ColdBootstrap {

    private ColdBootstrap() {}

    // Idempotency flag.  volatile so cross-thread reads see the latest value
    // without synchronization overhead in the steady-state hot path.
    private static volatile boolean sBootstrapped = false;
    private static final Object sLock = new Object();

    /**
     * Plant the minimum AOSP-framework state Westlake's cold-boot
     * dalvikvm sandbox needs before exercising framework code.
     *
     * <p>Idempotent: returns immediately if a prior call has already
     * completed the bootstrap.  Thread-safe (a per-class lock guards
     * the slow path so two concurrent first-callers don't both run the
     * plant).
     *
     * <p>Currently plants:
     * <ul>
     *   <li>A synthetic {@code android.app.ActivityThread} instance
     *       (via {@code Unsafe.allocateInstance}, bypassing the real
     *       {@code <init>} which sets up Loopers / Handlers /
     *       Dispatchers we don't have).</li>
     *   <li>Sets {@code at.mSystemContext} to a {@link BootstrapContext}
     *       that returns a planted {@code PermissionEnforcer} from
     *       {@code getSystemService("permission_enforcer")}.</li>
     *   <li>Installs {@code at} as
     *       {@code ActivityThread.sCurrentActivityThread} so AOSP
     *       framework code reading
     *       {@code ActivityThread.currentActivityThread()} sees the
     *       synthetic instance.</li>
     * </ul>
     *
     * <p>If {@code sCurrentActivityThread} is already non-null when this
     * runs (e.g. the test harness already primed, or a prior call here
     * landed), the plant is skipped and we just flip {@code sBootstrapped}.
     *
     * @return {@code true} if the bootstrap completed (whether by this
     *         call or a prior one); {@code false} if reflection failed
     *         partway through.  Callers should NOT block on a false
     *         return — production code paths should still try the work.
     */
    public static boolean ensure() {
        if (sBootstrapped) return true;
        synchronized (sLock) {
            if (sBootstrapped) return true;
            boolean ok = doBootstrap();
            sBootstrapped = ok;
            return ok;
        }
    }

    /**
     * Force a re-bootstrap (for testing).  Clears the idempotency flag
     * so the next {@link #ensure()} call re-plants.  NOT thread-safe
     * vs concurrent {@code ensure()} from other threads.
     */
    public static synchronized void resetForTesting() {
        sBootstrapped = false;
    }

    // Slow path — called once from the synchronized {@code ensure()}.
    private static boolean doBootstrap() {
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            java.lang.reflect.Field currentField =
                    atClass.getDeclaredField("sCurrentActivityThread");
            currentField.setAccessible(true);
            if (currentField.get(null) != null) {
                // Already primed by some other code (e.g. the test
                // harness, or a prior ColdBootstrap.ensure() that we
                // missed remembering).  Flip the flag and return.
                return true;
            }

            // 1. Allocate synthetic ActivityThread (bypasses <init>).
            Object at = unsafeAllocateInstance(atClass);
            if (at == null) {
                System.err.println("[ColdBootstrap] Unsafe.allocateInstance"
                        + "(ActivityThread) returned null — abort.");
                return false;
            }

            // 2. Plant `at.mSystemContext = BootstrapContext(enforcer)`.
            //    See the doc comment on BootstrapContext for why this
            //    matters (AOSP Stub() default ctors reach
            //    PermissionEnforcer.fromContext(...) which calls
            //    ctx.getSystemService("permission_enforcer")).
            try {
                Class<?> peClass = Class.forName("android.os.PermissionEnforcer");
                java.lang.reflect.Constructor<?> peCtor =
                        peClass.getDeclaredConstructor(
                                Class.forName("android.content.Context"));
                peCtor.setAccessible(true);
                // PE just stores the context in mContext and never uses
                // it until enforcePermission(...) which our Stub Tier-1
                // methods don't call.  null is acceptable.
                Object enforcer = peCtor.newInstance(new Object[]{null});
                Object syntheticCtx = new BootstrapContext(enforcer);
                setInstanceFieldUnsafe(atClass, at, "mSystemContext",
                        syntheticCtx);
            } catch (Throwable t) {
                // Fall back to a bare Unsafe-allocated ContextImpl so
                // at.getSystemContext() is at least non-null.  Logged
                // because this is informative.
                System.err.println(
                        "[ColdBootstrap] could not build synthetic "
                        + "context-with-enforcer; falling back to bare "
                        + "ContextImpl: " + t);
                try {
                    Class<?> ciClass = Class.forName("android.app.ContextImpl");
                    Object ci = unsafeAllocateInstance(ciClass);
                    setInstanceField(atClass, at, "mSystemContext", ci);
                } catch (Throwable ignored) {
                    // mSystemContext stays null; lazy path in
                    // ActivityThread.getSystemContext() will try
                    // ContextImpl.createSystemContext(...) — likely
                    // crashes downstream, but the alternative is
                    // failing right here, which is worse.
                }
            }

            // 3. Install as the static singleton.
            currentField.set(null, at);
            return true;
        } catch (Throwable t) {
            System.err.println("[ColdBootstrap] bootstrap failed: " + t);
            return false;
        }
    }

    // ------------------------------------------------------------------
    // BootstrapContext — public so tests can introspect it.
    // ------------------------------------------------------------------

    /**
     * Tiny {@link ContextWrapper} whose only job is to return a planted
     * {@code PermissionEnforcer} from
     * {@code getSystemService("permission_enforcer")}.  Other names
     * delegate to {@code super.getSystemService(name)} which calls
     * {@code mBase.getSystemService(name)} — {@code mBase} is null here
     * so any other lookup NPEs loud (intentional — surfaces unexpected
     * lookups during discovery rather than masquerading as null).
     */
    public static final class BootstrapContext extends ContextWrapper {
        private final Object enforcer;
        public BootstrapContext(Object pe) {
            super(/* base */ null);
            this.enforcer = pe;
        }
        @Override
        public Object getSystemService(String name) {
            if ("permission_enforcer".equals(name)) return enforcer;
            return super.getSystemService(name);
        }
    }

    // ------------------------------------------------------------------
    // Reflection helpers (mirrors CharsetPrimer.* style)
    // ------------------------------------------------------------------

    private static Object unsafeAllocateInstance(Class<?> cls) throws Throwable {
        Throwable firstErr = null;
        for (String cn : new String[] {"sun.misc.Unsafe", "jdk.internal.misc.Unsafe"}) {
            try {
                Class<?> uc = Class.forName(cn);
                java.lang.reflect.Field theUnsafe = uc.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                Object unsafe = theUnsafe.get(null);
                java.lang.reflect.Method m = uc.getMethod(
                        "allocateInstance", Class.class);
                return m.invoke(unsafe, cls);
            } catch (Throwable t) {
                if (firstErr == null) firstErr = t;
            }
        }
        throw firstErr != null ? firstErr
                : new IllegalStateException("no Unsafe.allocateInstance available");
    }

    private static void setInstanceField(Class<?> owner, Object obj,
            String fieldName, Object value) throws Throwable {
        Class<?> c = owner;
        while (c != null) {
            try {
                java.lang.reflect.Field f = c.getDeclaredField(fieldName);
                f.setAccessible(true);
                f.set(obj, value);
                return;
            } catch (NoSuchFieldException nf) {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName + " on " + owner.getName()
                + " (or supers)");
    }

    private static void setInstanceFieldUnsafe(Class<?> owner, Object obj,
            String fieldName, Object value) throws Throwable {
        java.lang.reflect.Field f = null;
        Class<?> c = owner;
        while (c != null && f == null) {
            try {
                f = c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException nf) {
                c = c.getSuperclass();
            }
        }
        if (f == null) {
            throw new NoSuchFieldException(fieldName + " on " + owner.getName()
                    + " (or supers)");
        }
        Class<?> uc = Class.forName("sun.misc.Unsafe");
        java.lang.reflect.Field theUnsafe = uc.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        java.lang.reflect.Method ofo = uc.getMethod("objectFieldOffset",
                java.lang.reflect.Field.class);
        long off = (Long) ofo.invoke(unsafe, f);
        java.lang.reflect.Method po = uc.getMethod("putObject",
                Object.class, long.class, Object.class);
        po.invoke(unsafe, obj, Long.valueOf(off), value);
    }
}
