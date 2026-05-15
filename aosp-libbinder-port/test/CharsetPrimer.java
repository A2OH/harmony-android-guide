// SPDX-License-Identifier: Apache-2.0
//
// Westlake CR9 -- CharsetPrimer (extracted from NoiceDiscoverWrapper, 2026-05-12)
// Westlake CR10 -- primeActivityThread() added 2026-05-12 (see M4_DISCOVERY §37).
//
// Shared helper that seeds java.nio.charset.Charset's static state and
// patches java.nio.charset.StandardCharsets reflectively, BEFORE any
// other code in the test process touches a Charset.  This is required by
// every dalvikvm-driven test that exercises framework code paths which
// reach `Charset.forName("UTF-8")`, `Charset.defaultCharset()`, or
// `StandardCharsets.UTF_8` -- for example, anything that calls
// `Class.forName(...)` on a class whose `<clinit>` transitively
// references `java.lang.VMClassLoader.<clinit>` (ZipFile, URLs, ...).
//
// CR10 also adds {@link #primeActivityThread()} which plants a synthetic
// android.app.ActivityThread singleton into the `sCurrentActivityThread`
// static field via Unsafe.allocateInstance and reflection.  This is
// required by AOSP Stub() constructors (specifically
// IDisplayManager$Stub, INotificationManager$Stub, IInputMethodManager$Stub)
// whose deprecated no-arg ctor invokes
//   `ActivityThread.currentActivityThread().getSystemContext()`
// to seed a PermissionEnforcer.  Without primer, currentActivityThread()
// returns null and the Stub ctor NPEs.  See M4_DISCOVERY §37 for context.
//
// Background (see docs/engine/M4_DISCOVERY.md §30 -- M4-PRE10):
//
//   `java.nio.charset.Charset.cache2` (private static final HashMap)
//   and `Charset.gate` (private static ThreadLocal) are null on a cold
//   dalvikvm boot.  When AOSP framework code first reaches
//   `Charset.forName("UTF-8")`, it NPEs on `synchronized(cache2)`.
//   `StandardCharsets.<clinit>` then enters ERROR state forever and
//   `StandardCharsets.UTF_8` reads back as null.  Every later access
//   throws `NullPointerException: charset` and aborts whichever clinit
//   triggered the chain (commonly `VMClassLoader.<clinit>`).
//
//   M4-PRE10 fixed this for `NoiceDiscoverWrapper` by inlining a primer.
//   CR9 (this) extracts that primer into a reusable helper so that the
//   four M4 service tests (DisplayServiceTest, NotificationServiceTest,
//   InputMethodServiceTest, SystemServiceRouteTest) -- which exhibited
//   the same `Tolerating clinit failure for Ljava/lang/VMClassLoader;:
//   NullPointerException: charset` symptom in the post-CR8 regression --
//   can share one canonical implementation rather than duplicating ~70
//   LOC apiece.  NoiceDiscoverWrapper.primeCharsetState() now delegates
//   here to avoid drift.
//
// Idempotency: this helper is safe to call multiple times.  The
// `seedStaticFieldIfNull` arm short-circuits on the already-populated
// cache2 / gate / defaultCharset fields; the unconditional
// `setStaticField` arm on `StandardCharsets.UTF_8` simply re-plants the
// (same) UTF-8 instance, which is harmless.
//
// Call site convention: invoke as the FIRST line of `main(String[])`
// in any test that hits the VMClassLoader clinit chain.  See
// DisplayServiceTest / NotificationServiceTest / InputMethodServiceTest
// / SystemServiceRouteTest / NoiceDiscoverWrapper for the canonical
// pattern.
//
// FILES NOT TO TOUCH (per the CR9 brief): shim/java/*, art-latest/*,
// the regression script, and any of the 8 currently-passing tests.

public final class CharsetPrimer {

    private CharsetPrimer() {}

    /**
     * Seed java.nio.charset.Charset's static state so that
     * Charset.forName(...) / Charset.defaultCharset() / direct
     * StandardCharsets.UTF_8 access in framework code work in our
     * cold-boot dalvikvm.
     *
     * Conceptually mirrors {@code WestlakeLauncher.primeCharsetState()}
     * but extended in two key ways (matching the M4-PRE10 inline version
     * in NoiceDiscoverWrapper):
     *
     *   (1) Order: seed {@code cache2} + {@code gate} on Charset FIRST,
     *       BEFORE touching {@link java.nio.charset.StandardCharsets}.
     *       The {@code StandardCharsets} class has a {@code <clinit>}
     *       that calls {@code Charset.forName("UTF-8")} eagerly; if
     *       {@code Charset.cache2} is null when that runs, the forName
     *       NPEs on {@code synchronized(cache2)}, the {@code <clinit>}
     *       fails, and the class enters ERROR state forever (the JVM
     *       remembers the class init failure).
     *
     *   (2) Patch BOTH {@code Charset.defaultCharset} AND
     *       {@code StandardCharsets.UTF_8} (etc.) reflectively with a
     *       freshly-built UTF-8 charset, in case {@code StandardCharsets}
     *       had already been ERROR-marked before we got control.  We use
     *       UNCONDITIONAL set (not seedIfNull) for the StandardCharsets
     *       fields because a failed clinit may leave them readable as
     *       null via reflection without seedIfNull noticing.
     */
    public static void primeCharsetState() {
        Class<?> charsetCls;
        try {
            charsetCls = Class.forName("java.nio.charset.Charset");
        } catch (Throwable t) {
            // Charset itself unavailable -- nothing we can do; the
            // downstream caller will fail later with a more useful
            // error.
            return;
        }

        // STEP 1: seed cache2 + gate.  These MUST be set before any
        // Charset.forName call, because forName synchronizes on cache2.
        // cache2 is `private static final HashMap` in libcore -- final,
        // but Field.set() still works on Android pre-JDK-12 reflection
        // model (which is what dalvikvm uses).
        seedStaticFieldIfNull(charsetCls, "cache2", new java.util.HashMap<String, Object>());
        seedStaticFieldIfNull(charsetCls, "gate", new java.lang.ThreadLocal<Object>());

        // STEP 2: now build a real UTF-8 Charset via Charset.forName.
        // With cache2 + gate populated this should succeed.
        Object utf8 = null;
        try {
            utf8 = charsetCls.getMethod("forName", String.class).invoke(null, "UTF-8");
        } catch (Throwable t) {
            // forName still failing -- give up gracefully.
            return;
        }
        if (utf8 == null) return;

        // STEP 3: plant the UTF-8 charset on Charset.defaultCharset.
        seedStaticFieldIfNull(charsetCls, "defaultCharset", utf8);

        // STEP 4: also patch StandardCharsets.UTF_8 (+ siblings) in
        // case StandardCharsets's <clinit> already ran and ERROR-marked.
        // We try to set the field even if it's already non-null because
        // a NoClassDefFoundError chain may leave the field readable as
        // null via reflection without setStaticFieldIfNull noticing.
        try {
            Class<?> scCls = Class.forName("java.nio.charset.StandardCharsets");
            // Note: StandardCharsets has US_ASCII, ISO_8859_1, UTF_8,
            // UTF_16BE, UTF_16LE, UTF_16 as public static final.  We
            // patch UTF_8 (the one UriCodec / StreamEncoder use); the
            // others fall back to Charset.forName per their original
            // <clinit> logic if the parent class init partially ran.
            setStaticField(scCls, "UTF_8", utf8);
            // Try to populate the rest too -- best effort.
            try {
                Object asc = charsetCls.getMethod("forName", String.class)
                        .invoke(null, "US-ASCII");
                if (asc != null) setStaticField(scCls, "US_ASCII", asc);
            } catch (Throwable ignored) {}
            try {
                Object iso = charsetCls.getMethod("forName", String.class)
                        .invoke(null, "ISO-8859-1");
                if (iso != null) setStaticField(scCls, "ISO_8859_1", iso);
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {
            // StandardCharsets unavailable -- defaultCharset alone is
            // enough for the UriCodec decoder path that triggered PRE10.
        }
    }

    /**
     * CR10 (2026-05-12): plant a synthetic
     * {@code android.app.ActivityThread} singleton into the
     * {@code sCurrentActivityThread} static field so that
     * {@code ActivityThread.currentActivityThread()} returns non-null
     * and {@code ActivityThread.getSystemContext()} returns a non-null
     * (synthetic) {@code ContextImpl} when AOSP Stub() ctors call into
     * those methods to seed a PermissionEnforcer.
     *
     * <p><b>CR17 (2026-05-12) — partially superseded.</b>  The three
     * services that originally required this primer
     * (WestlakeDisplayManagerService, WestlakeNotificationManagerService,
     * WestlakeInputMethodManagerService) have been converted to use the
     * {@code Stub(PermissionEnforcer)} bypass with a
     * {@code NoopPermissionEnforcer} (same pattern as M4a/M4b/M4c/M4-power).
     * As a result the three single-service tests
     * (DisplayServiceTest, NotificationServiceTest, InputMethodServiceTest)
     * have dropped their {@code primeActivityThread()} call -- the
     * services construct cleanly from production callers like
     * {@code ServiceRegistrar.registerAllServices()} that have no
     * test-harness priming.
     *
     * <p><b>CR18 (2026-05-12) — fully superseded for SystemServiceRouteTest.</b>
     * The CR18 bisection (see diagnostics/CR18_primer_sigbus_bisection.md)
     * built five variants of {@code primeActivityThread()} controlled by a
     * bitmask of plant operations and ran each variant 4-8 times on the
     * phone.  <b>Variant 0 (NO primer at all)</b> PASSED 8/8 for
     * SystemServiceRouteTest, proving the "primer required to avoid
     * SIGBUS" claim from CR15/CR17 was either non-deterministic noise or
     * has been resolved by intervening CR17 Stub-bypass landings.
     *
     * <p>The plant logic itself has moved to a production class:
     * {@link com.westlake.services.ColdBootstrap}, callable from any
     * entry point (WestlakeLauncher, ServiceRegistrar, test harness).
     * This method now delegates to {@code ColdBootstrap.ensure()} for
     * consistency and falls back to {@link #primeActivityThreadVariant(int)}
     * only if ColdBootstrap is not on the bootclasspath.
     *
     * <p>NoiceDiscoverWrapper still calls {@code primeActivityThread()};
     * its bisection is left for a future CR (NoiceDiscoverWrapper exercises
     * a much larger AOSP framework surface and may still benefit from the
     * warming).
     *
     * <p>See M4_DISCOVERY §48 (CR18) and §46 (CR17).
     *
     * <p>Pattern mirrors M4-PRE6 (ResourcesImpl) and M4-PRE8
     * (AssetManager): {@code Unsafe.allocateInstance} the framework
     * class -- bypasses {@code <init>} (whose body is dangerous in our
     * cold-boot sandbox) and {@code <clinit>} (already tolerated by
     * ART with static fields at JVM defaults).  Then reflectively
     * populate the minimum fields that downstream methods actually
     * read.  For ActivityThread that minimum is:
     * <ul>
     *   <li>{@code sCurrentActivityThread} (static) -- read by
     *       {@code currentActivityThread()}.</li>
     *   <li>{@code mSystemContext} (instance) -- read by
     *       {@code getSystemContext()} and returned directly when
     *       non-null (otherwise the method synchronises and lazily
     *       creates a real {@code ContextImpl} via
     *       {@code ContextImpl.createSystemContext(this)} which
     *       cascades into resources/asset-manager natives we have not
     *       stubbed).  Planting a synthetic {@code ContextImpl} avoids
     *       the cascade.</li>
     * </ul>
     *
     * <p>The synthetic {@code ContextImpl} is also
     * Unsafe-allocate'd; all of its fields are JVM-default null/0.
     * If a caller actually invokes a method on it (rather than just
     * passing it as a {@code Context} argument like
     * {@code PermissionEnforcer.fromContext(...)} does), the call will
     * fail loud -- which is the desired discovery behaviour.
     *
     * <p>Idempotent: if {@code sCurrentActivityThread} is already
     * non-null (e.g. this method has been called before, or some
     * other code installed an instance), this method is a no-op.
     *
     * <p>Best-effort: any reflection failure is caught and logged to
     * stderr with no exception propagation -- callers should treat
     * the primer as "fire and forget" and let downstream NPE surface
     * the next bottleneck if the primer can't do its job.
     */
    public static void primeActivityThread() {
        // CR18 (2026-05-12): delegate to com.westlake.services.ColdBootstrap.
        // The plant logic moved to production code (shim/java/com/westlake/services/
        // ColdBootstrap.java) so production callsites (WestlakeLauncher,
        // ServiceRegistrar) can request the same warming as test harnesses.
        // ColdBootstrap.ensure() is idempotent + thread-safe.
        //
        // This direct path remains available for tests that haven't
        // converted to ColdBootstrap.ensure() yet (NoiceDiscoverWrapper).
        // It is functionally equivalent to ColdBootstrap.ensure() and
        // delegates by reflection so this test source stays free of
        // shim/* compile-time dependencies.
        //
        // Bisection finding (CR18): variant 0 (NO primer at all) PASSED
        // 8/8 for SystemServiceRouteTest.  The "primer required to avoid
        // SIGBUS" claim from CR15/CR17 was either non-deterministic noise
        // or has been resolved by intervening CR17 Stub-bypass landings.
        // primeActivityThread() is now conservative warming, NOT a
        // strict prerequisite.  See diagnostics/CR18_primer_sigbus_bisection.md.
        try {
            Class<?> cb = Class.forName("com.westlake.services.ColdBootstrap");
            cb.getDeclaredMethod("ensure").invoke(null);
        } catch (Throwable t) {
            // Fall back to the legacy direct plant if ColdBootstrap is
            // not on the bootclasspath (e.g. an older aosp-shim.dex).
            System.err.println(
                    "[CharsetPrimer.primeActivityThread] ColdBootstrap.ensure() "
                    + "unavailable; falling back to direct primeActivityThreadVariant. "
                    + t);
            primeActivityThreadVariant(VARIANT_FLAG_INSTALL | VARIANT_FLAG_ENFORCER_CTX);
        }
    }

    /**
     * CR18 (2026-05-12): bit-flag bisection knobs for {@link #primeActivityThread()}.
     *
     * <ul>
     *   <li>{@link #VARIANT_FLAG_INSTALL} -- install the synthetic ActivityThread
     *       instance as {@code sCurrentActivityThread} (static).  Without this
     *       bit, {@code ActivityThread.currentActivityThread()} continues to
     *       read null and the synthetic instance is throwaway.</li>
     *   <li>{@link #VARIANT_FLAG_ENFORCER_CTX} -- plant
     *       {@code at.mSystemContext = BootstrapContext(planted PermissionEnforcer)}.
     *       Without this bit, {@code at.mSystemContext} stays null (unless
     *       {@link #VARIANT_FLAG_BARE_CTX} is set, in which case it gets a
     *       vanilla {@code Unsafe.allocateInstance}-built {@code ContextImpl}).</li>
     *   <li>{@link #VARIANT_FLAG_BARE_CTX} -- plant
     *       {@code at.mSystemContext = Unsafe.allocateInstance(ContextImpl.class)}.
     *       Only takes effect if {@link #VARIANT_FLAG_ENFORCER_CTX} is NOT set;
     *       the enforcer path is preferred when both are requested.</li>
     * </ul>
     */
    public static final int VARIANT_FLAG_INSTALL = 0x1;
    public static final int VARIANT_FLAG_ENFORCER_CTX = 0x2;
    public static final int VARIANT_FLAG_BARE_CTX = 0x4;

    /**
     * CR18 (2026-05-12): bisection-friendly variant of {@link #primeActivityThread()}.
     * The default ({@code primeActivityThread()}) calls this with
     * {@code VARIANT_FLAG_INSTALL | VARIANT_FLAG_ENFORCER_CTX}.  Test harnesses
     * doing PHASE B SIGBUS bisection pass narrower flag sets to identify which
     * plant is load-bearing for suppressing the
     * {@code kPFCutStaleNativeEntry} sentinel from ending up in a JNI
     * function-pointer slot.
     *
     * @param flags any combination of {@link #VARIANT_FLAG_INSTALL},
     *              {@link #VARIANT_FLAG_ENFORCER_CTX}, and
     *              {@link #VARIANT_FLAG_BARE_CTX}.  flags=0 is a no-op.
     */
    public static void primeActivityThreadVariant(int flags) {
        // CR18 (2026-05-12): one-line variant trace -- helpful when bisecting
        // a failing test; harmless when not.  ColdBootstrap-driven callers
        // get this for free because they reach this code only as the
        // fallback path when ColdBootstrap is missing.
        System.err.println("[CharsetPrimer.primeActivityThreadVariant] flags=0x"
                + Integer.toHexString(flags)
                + " (install=" + ((flags & VARIANT_FLAG_INSTALL) != 0)
                + " enforcerCtx=" + ((flags & VARIANT_FLAG_ENFORCER_CTX) != 0)
                + " bareCtx=" + ((flags & VARIANT_FLAG_BARE_CTX) != 0) + ")");
        if (flags == 0) {
            // Explicit no-op variant.
            return;
        }
        try {
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            java.lang.reflect.Field currentField =
                    atClass.getDeclaredField("sCurrentActivityThread");
            currentField.setAccessible(true);
            if (currentField.get(null) != null) {
                // Already primed -- nothing to do.
                return;
            }

            // 1. Allocate a synthetic ActivityThread via Unsafe.  Bypasses
            //    both <init> (which sets up handlers, looper, dispatchers we
            //    don't have) and any further <clinit> work (ART already
            //    tolerated the original clinit per the M4-PRE6 / M4-PRE8
            //    precedent, so static fields are at JVM defaults).
            Object at = unsafeAllocateInstance(atClass);

            // 2. Build a synthetic mSystemContext that does the ONE thing
            //    needed by every Stub() default ctor on Android 16:
            //    `getSystemService("permission_enforcer")` must return a
            //    non-null PermissionEnforcer.
            //
            //    Why: Android 16's `IXxx$Stub()` deprecated default ctor
            //    expands to:
            //      this(PermissionEnforcer.fromContext(
            //              ActivityThread.currentActivityThread()
            //                  .getSystemContext()))
            //    and `PermissionEnforcer.fromContext(ctx)` is literally
            //      return (PermissionEnforcer)
            //          ctx.getSystemService("permission_enforcer");
            //    If that returns null, the Stub(PermissionEnforcer)
            //    overload throws IllegalArgumentException("enforcer
            //    cannot be null").  (Disassembly: see M4_DISCOVERY §37
            //    where the bytecode for IDeviceIdleController$Stub.<init>
            //    is documented.)
            //
            //    A plain Unsafe-allocated `ContextImpl` (as M4-PRE6 /
            //    M4-PRE8 do for ResourcesImpl / AssetManager) is NOT
            //    enough here: ContextImpl.getSystemService routes through
            //    `SystemServiceRegistry` whose <clinit> has been
            //    ERROR-marked in our cold dalvikvm (see the
            //    "Tolerating clinit failure for SystemServiceRegistry"
            //    log line); subsequent static calls just return null
            //    rather than throwing, so getSystemService("...") ->
            //    null -> IAE.
            //
            //    Fix: build a *real* Context subclass that overrides
            //    getSystemService(String) to return a planted
            //    PermissionEnforcer for "permission_enforcer", null for
            //    everything else.  We use ContextWrapper (which is a
            //    concrete public Context subclass with all methods
            //    delegating to mBase) and pass `null` for the base so
            //    callers that ask for anything other than the enforcer
            //    NPE loud -- which is the desired discovery behaviour.
            //
            //    The PermissionEnforcer itself is constructed via its
            //    public `<init>(Context)` ctor (no natives, no
            //    ActivityManager.getService chain), passing the
            //    wrapper as mContext.
            // CR18: ENFORCER_CTX bit governs the synthetic
            // BootstrapContext-with-PermissionEnforcer plant.  BARE_CTX bit
            // governs the vanilla ContextImpl fallback (mutually exclusive in
            // practice: ENFORCER_CTX wins when both set).
            if ((flags & VARIANT_FLAG_ENFORCER_CTX) != 0) {
                try {
                    // (a) Build the PermissionEnforcer via its public ctor.
                    Class<?> peClass = Class.forName("android.os.PermissionEnforcer");
                    java.lang.reflect.Constructor<?> peCtor =
                            peClass.getDeclaredConstructor(
                                    Class.forName("android.content.Context"));
                    peCtor.setAccessible(true);
                    // We need a Context to pass to the PE ctor; the PE just
                    // stores it in mContext and never invokes anything on it
                    // until enforcePermission(...) is called -- which our
                    // Stub Tier-1 methods never do.  Pass null: the PE field
                    // accepts null fine; we just need a non-null PE.
                    Object enforcer = peCtor.newInstance(new Object[]{null});

                    // (b) Build the synthetic Context that returns this PE
                    //     from getSystemService("permission_enforcer").
                    Object syntheticCtx = new BootstrapContext(enforcer);

                    // (c) Plant it on at.mSystemContext.  Field type is
                    //     `ContextImpl` -- but `Field.set` on Android does
                    //     not enforce subtype assignability for object
                    //     fields, so a non-ContextImpl Context goes in
                    //     cleanly.  Downstream readers cast to Context
                    //     (via fromContext's signature), which our class
                    //     IS-A, so the dispatch works.  (We use
                    //     setInstanceFieldUnsafe to be belt-and-braces:
                    //     putObject bypasses any Field.set type check the
                    //     runtime might apply.)
                    setInstanceFieldUnsafe(atClass, at, "mSystemContext",
                            syntheticCtx);
                } catch (Throwable t) {
                    // Fall back to a vanilla synthetic ContextImpl so at
                    // least getSystemContext() is non-null -- some Stub
                    // ctors may simply not invoke fromContext.  Logged
                    // because the failure-mode is informative.
                    System.err.println(
                            "[CharsetPrimer.primeActivityThreadVariant] could not "
                            + "build synthetic context-with-enforcer; "
                            + "falling back to bare ContextImpl: " + t);
                    try {
                        Class<?> ciClass = Class.forName("android.app.ContextImpl");
                        Object ci = unsafeAllocateInstance(ciClass);
                        setInstanceField(atClass, at, "mSystemContext", ci);
                    } catch (Throwable ignored) {
                        // mSystemContext stays null -- getSystemContext() will
                        // try its lazy path.
                    }
                }
            } else if ((flags & VARIANT_FLAG_BARE_CTX) != 0) {
                // CR18 variant: vanilla ContextImpl, NO planted PermissionEnforcer.
                try {
                    Class<?> ciClass = Class.forName("android.app.ContextImpl");
                    Object ci = unsafeAllocateInstance(ciClass);
                    setInstanceField(atClass, at, "mSystemContext", ci);
                } catch (Throwable t) {
                    System.err.println(
                            "[CharsetPrimer.primeActivityThreadVariant] could not "
                            + "build bare ContextImpl: " + t);
                }
            } // else: mSystemContext stays null.

            // 3. Install our synthetic instance as the static singleton.
            //    From here on ActivityThread.currentActivityThread() will
            //    return our instance, and Stub() ctors can call
            //    getSystemContext() without NPE'ing on a null this.
            //
            // CR18: INSTALL bit governs this install.  Without it, the
            // synthetic ActivityThread is throwaway -- exercises the
            // allocation + clinit-tolerate path but does NOT publish it.
            if ((flags & VARIANT_FLAG_INSTALL) != 0) {
                currentField.set(null, at);
            }
        } catch (Throwable t) {
            // Print rather than swallow so failures are visible in test
            // logs.  We do NOT propagate -- the caller is expected to
            // proceed and let downstream code surface its own NPEs if the
            // primer couldn't do its job.
            System.err.println("[CharsetPrimer.primeActivityThreadVariant] " + t);
        }
    }

    /**
     * Tiny ContextWrapper subclass whose only job is to return a
     * planted PermissionEnforcer for getSystemService("permission_enforcer").
     * Other names fall through to {@link android.content.ContextWrapper#getSystemService(String)}
     * which calls {@code mBase.getSystemService(name)} -- mBase is null here
     * so any other lookup NPEs loud (desired for discovery).
     */
    private static final class BootstrapContext extends android.content.ContextWrapper {
        private final Object enforcer;
        BootstrapContext(Object pe) {
            super(/* base */ null);
            this.enforcer = pe;
        }
        @Override
        public Object getSystemService(String name) {
            if ("permission_enforcer".equals(name)) return enforcer;
            // Fall back to super (which NPEs on null mBase); deliberately
            // loud so any unexpected getSystemService call surfaces in
            // discovery rather than masquerading as "returned null".
            return super.getSystemService(name);
        }
    }

    /**
     * Plant {@code obj.<fieldName>} = {@code value} via
     * {@code sun.misc.Unsafe.putObject}, bypassing any subtype check
     * that {@code Field.set} might apply at runtime.  Useful when the
     * field's declared type (e.g. {@code ContextImpl}) is narrower
     * than the runtime type we want to plant (e.g. a custom
     * {@code Context} subclass).
     */
    static void setInstanceFieldUnsafe(Class<?> owner, Object obj,
            String fieldName, Object value) throws Throwable {
        // Walk supers to find the field declaration.
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
        // Get sun.misc.Unsafe.
        Class<?> uc = Class.forName("sun.misc.Unsafe");
        java.lang.reflect.Field theUnsafe = uc.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Object unsafe = theUnsafe.get(null);
        // offsetof + putObject -- no type check.
        java.lang.reflect.Method ofo = uc.getMethod("objectFieldOffset",
                java.lang.reflect.Field.class);
        long off = (Long) ofo.invoke(unsafe, f);
        java.lang.reflect.Method po = uc.getMethod("putObject",
                Object.class, long.class, Object.class);
        po.invoke(unsafe, obj, Long.valueOf(off), value);
    }

    /**
     * Set instance field {@code obj.<fieldName>} = {@code value},
     * walking up the class hierarchy if the field isn't declared on
     * {@code owner} itself.  Pattern mirrors
     * {@code WestlakeResources.setField}.
     */
    static void setInstanceField(Class<?> owner, Object obj, String fieldName, Object value)
            throws Throwable {
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

    /**
     * Reflectively call {@code sun.misc.Unsafe.allocateInstance(cls)}
     * to allocate an instance bypassing {@code <init>}.  Mirrors
     * {@code WestlakeResources.unsafeAllocateInstance}.
     */
    static Object unsafeAllocateInstance(Class<?> cls) throws Throwable {
        Throwable firstErr = null;
        for (String cn : new String[] {"sun.misc.Unsafe", "jdk.internal.misc.Unsafe"}) {
            try {
                Class<?> uc = Class.forName(cn);
                java.lang.reflect.Field theUnsafe = uc.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                Object unsafe = theUnsafe.get(null);
                java.lang.reflect.Method m = uc.getMethod("allocateInstance", Class.class);
                return m.invoke(unsafe, cls);
            } catch (Throwable t) {
                if (firstErr == null) firstErr = t;
            }
        }
        throw firstErr != null ? firstErr
                : new IllegalStateException("no Unsafe.allocateInstance available");
    }

    /**
     * Set {@code owner.<fieldName>} to {@code value} only if the field
     * currently reads as null.  No-op on any reflection failure (field
     * may not exist in this framework.jar version).
     */
    static void seedStaticFieldIfNull(Class<?> owner, String fieldName, Object value) {
        try {
            java.lang.reflect.Field f = owner.getDeclaredField(fieldName);
            f.setAccessible(true);
            if (f.get(null) == null) {
                f.set(null, value);
            }
        } catch (Throwable ignored) {
            // Field may not exist in this framework.jar version (e.g.
            // cache2 renamed or removed in some OEM forks) -- non-fatal.
        }
    }

    /**
     * Set {@code owner.<fieldName>} to {@code value} unconditionally.
     * No-op on any reflection failure.  Used for the StandardCharsets
     * fields where the field may already be null-from-failed-clinit (in
     * which case seedIfNull would silently no-op when we actually want
     * to overwrite).
     */
    static void setStaticField(Class<?> owner, String fieldName, Object value) {
        try {
            java.lang.reflect.Field f = owner.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(null, value);
        } catch (Throwable ignored) {
            // Field may not exist -- non-fatal.
        }
    }
}
