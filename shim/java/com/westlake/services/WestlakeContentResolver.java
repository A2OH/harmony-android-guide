// SPDX-License-Identifier: Apache-2.0
//
// Westlake M4-PRE9 -- WestlakeContentResolver
//
// Minimum-surface ContentResolver subclass for the Westlake dalvikvm
// sandbox.  Required because (per CR4's noice-discover-postCR4.log)
// `PhoneWindow.<init>(Context)` calls
//   Settings.Global.getInt(ctx.getContentResolver(), name, defaultValue)
// which in turn dispatches:
//   1. cr.getUserId()                          -- NPE if cr == null
//   2. NameValueCache.getStringForUser(cr, ...)
//        -> ContentProviderHolder.getProvider(cr)
//             -> cr.acquireProvider(authority)  -- abstract in framework.jar
//        -> IContentProvider.call(...)          -- NPE if provider == null
//
// Pre-CR5/M4-PRE9 our `WestlakeContextImpl.getContentResolver()` returned
// null so step 1 NPE'd immediately.  This class makes getContentResolver()
// return a real ContentResolver subclass whose getUserId()=0 and whose
// acquireProvider*() returns a no-op `IContentProvider` Proxy.  The Proxy
// makes `IContentProvider.call(...)` return null (default for object
// returns); NameValueCache.getStringForUser interprets null as
// "value not found in DB" and falls through to the cursor-based fallback
// path which is wrapped in an `<any>` catch that returns null on any
// exception.  Settings.Global.getString thus returns null,
// Settings.Global.getInt returns the caller-supplied default --
// exactly the behaviour PhoneWindow.<init> tolerates (e.g.
// haptic_feedback_intensity = default = 0).
//
// Compile-time vs runtime hierarchy:
//   Compile-time: extends shim's android.content.ContentResolver
//                 (concrete; ctor (Context) sets mContext, all methods
//                  defaulted to null/0/false).
//   Runtime:      extends framework.jar's android.content.ContentResolver
//                 (PUBLIC ABSTRACT; declares 5 abstract protected/public
//                  methods we MUST override or `new WestlakeContentResolver`
//                  would throw InstantiationError).
//   The duplicates list in scripts/framework_duplicates.txt strips the
//   shim ContentResolver from aosp-shim.dex; framework.jar's real
//   ContentResolver wins at runtime.
//
// Five abstract methods overridden:
//   protected IContentProvider acquireProvider(Context, String)
//   protected IContentProvider acquireUnstableProvider(Context, String)
//   public    boolean          releaseProvider(IContentProvider)
//   public    boolean          releaseUnstableProvider(IContentProvider)
//   public    void             unstableProviderDied(IContentProvider)
//
// Plus the non-abstract `getUserId()` is overridden to return USER_SYSTEM
// without dereferencing the inherited `mContext` field (which a
// framework.jar getUserId() implementation does via
// `mContext.getUserId()`; that path would itself be problematic in the
// sandbox).
//
// IContentProvider stub via java.lang.reflect.Proxy:
//   IContentProvider has ~30 abstract methods (`call`, `query`, `insert`,
//   `delete`, `update`, etc.) all declared `throws RemoteException`.  A
//   static implementation would mean 30+ no-op bodies.  Instead we
//   instantiate a Proxy whose InvocationHandler returns null/0/false
//   based on the declared return type.  This is robust against
//   framework.jar IContentProvider surface changes (Android 14/15/16
//   adds new transactions periodically); we accept the (small) ProxyClass
//   gen cost once per process and never need to update the surface map.
//
// No per-app branches: nothing in this class branches on package name.
// Settings.Global.getInt's default fallback is the only consumer we
// observed; we expect a future discovery hit to upgrade this to a real
// (in-memory or backed) ContentProvider when an actual app calls
// `cr.query(SettingsContract.URI, ...)` and demands persistent data.
//
// Author: M4-PRE9 agent (2026-05-12).

package com.westlake.services;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Westlake M4-PRE9 minimum-surface ContentResolver.
 *
 * <p>Overrides {@link #getUserId()} to return USER_SYSTEM (0) without
 * dereferencing the inherited {@code mContext}, and supplies a no-op
 * {@link IContentProvider} Proxy from {@link #acquireProvider} so
 * {@code Settings.Global.getString(cr, name)} falls through to its
 * cursor-based fallback (which the framework wraps in an {@code <any>}
 * catch that returns null on any exception).  Result:
 * {@code Settings.Global.getInt(cr, name, default)} returns the
 * caller-supplied default value, which is exactly what
 * {@code PhoneWindow.<init>} expects when the underlying settings DB
 * is unreachable.
 */
public final class WestlakeContentResolver extends ContentResolver {

    /** USER_SYSTEM; the only user this sandbox knows about. */
    private static final int USER_SYSTEM = 0;

    /**
     * Cached no-op {@link IContentProvider} Proxy returned by
     * {@link #acquireProvider} and friends.  Lazy-initialised so that
     * Proxy class generation happens at most once per process.
     */
    private static volatile IContentProvider sNoopProvider;

    /**
     * Build a ContentResolver bound to the given Context.  The Context
     * argument is forwarded to {@code ContentResolver(Context)} so the
     * inherited {@code mContext} field is set (some non-overridden
     * methods on framework.jar's CR side -- e.g. {@code getAttributionSource} --
     * read it).  Caller passes the WestlakeContextImpl that owns this CR.
     */
    public WestlakeContentResolver(Context context) {
        super(context);
    }

    // ----------------------------------------------------------------------
    // getUserId -- the Tier-1 method PhoneWindow.<init> ultimately needs.
    // ----------------------------------------------------------------------
    //
    // framework.jar's ContentResolver.getUserId() reads `mContext.getUserId()`,
    // which on a WestlakeContextImpl would route to Context.getUserId() --
    // not implemented in our WestlakeContextImpl, so it would propagate
    // back as the same NPE shape we are trying to fix.  Override the
    // method directly to return USER_SYSTEM without touching mContext.
    //
    // This is the *immediate* reason this class exists; the Settings.Global
    // getString call dispatches cr.getUserId() before doing anything else.

    @Override
    public int getUserId() {
        return USER_SYSTEM;
    }

    // ----------------------------------------------------------------------
    // Abstract provider methods (5 total) -- return the no-op Proxy.
    // ----------------------------------------------------------------------
    //
    // Settings.Global -> NameValueCache.getStringForUser path:
    //   1. mProviderHolder.getProvider(cr)
    //      -> cr.acquireProvider(authority)   -- THIS METHOD CHAIN
    //      Returns null in a naive impl -> NPE later on IContentProvider.call.
    //   2. provider.call(AttributionSource, "GET_global", name, null, Bundle)
    //      -> Proxy returns null              -- triggers fallback
    //   3. Fallback: cursor-based query -> wrapped in <any> catch ->
    //      returns null on any failure.
    //   4. Settings.Global.getString returns null -> getInt returns default.
    //
    // We return the SAME Proxy for the stable and unstable variants.
    // The distinction (stable vs unstable) is about death recipient
    // semantics in real binder transports; in our same-process sandbox
    // it has no observable difference.

    @Override
    protected IContentProvider acquireProvider(Context context, String name) {
        return getNoopProvider();
    }

    @Override
    protected IContentProvider acquireUnstableProvider(Context context, String name) {
        return getNoopProvider();
    }

    @Override
    public boolean releaseProvider(IContentProvider icp) {
        // Always return true.  AOSP's ApplicationContentResolver returns
        // true on successful unref; callers tolerate either bool.  We
        // never had a real ref to release.
        return true;
    }

    @Override
    public boolean releaseUnstableProvider(IContentProvider icp) {
        return true;
    }

    @Override
    public void unstableProviderDied(IContentProvider icp) {
        // No-op.  Our Proxy provider never "dies" in any meaningful
        // sense.  Real implementations notify listeners and clean up
        // proxy state; we have neither.
    }

    // ----------------------------------------------------------------------
    // No-op IContentProvider Proxy
    // ----------------------------------------------------------------------
    //
    // IContentProvider's ~30 methods all declare `throws RemoteException`.
    // We never actually need their results, but we DO need the receiver
    // to be non-null so the framework's `invoke-interface` instructions
    // succeed.  A dynamic Proxy is the lightest-touch way to materialise
    // a concrete instance.
    //
    // Return type defaults:
    //   - Object (interface, class, array): null
    //   - boolean: false
    //   - int/short/byte/long/char/float/double: 0
    //   - void: returns null which JVM-side discards
    //   These match AOSP NULL-binder dispatch defaults.

    private static IContentProvider getNoopProvider() {
        IContentProvider p = sNoopProvider;
        if (p != null) return p;
        synchronized (WestlakeContentResolver.class) {
            if (sNoopProvider == null) {
                try {
                    sNoopProvider = (IContentProvider) Proxy.newProxyInstance(
                            IContentProvider.class.getClassLoader(),
                            new Class<?>[]{IContentProvider.class},
                            new NoopProviderHandler());
                } catch (Throwable t) {
                    // Proxy generation failed; nothing useful to fall
                    // back to.  Returning null preserves the
                    // pre-M4-PRE9 NPE shape, but with a clear
                    // exception trace.
                    try {
                        System.err.println(
                                "[M4-PRE9] IContentProvider Proxy build failed: " + t);
                    } catch (Throwable ignored) {}
                }
            }
            return sNoopProvider;
        }
    }

    /**
     * InvocationHandler that returns null/0/false from every method
     * invocation; this is the entire surface of our stub
     * {@code IContentProvider}.  See class-doc above for the
     * Settings.Global fall-through logic that consumes the null returns.
     */
    private static final class NoopProviderHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            // Tiny diagnostic so discovery surfaces calls we may want
            // to upgrade to a real impl later (e.g. a real Settings
            // store if a future app reads SettingsContract directly).
            // Only log when *.call(...) (and friends commonly observed
            // by Settings.Global) actually hit; otherwise the chatter
            // is too high.
            try {
                String n = method.getName();
                if ("call".equals(n) || "query".equals(n)) {
                    System.err.println("[M4-PRE9-cr] IContentProvider."
                            + n + " returning null (no provider)");
                }
            } catch (Throwable ignored) {}

            Class<?> rt = method.getReturnType();
            if (rt == boolean.class) return Boolean.FALSE;
            if (rt == int.class) return Integer.valueOf(0);
            if (rt == long.class) return Long.valueOf(0L);
            if (rt == short.class) return Short.valueOf((short) 0);
            if (rt == byte.class) return Byte.valueOf((byte) 0);
            if (rt == char.class) return Character.valueOf((char) 0);
            if (rt == float.class) return Float.valueOf(0f);
            if (rt == double.class) return Double.valueOf(0d);
            if (rt == void.class) return null;
            // Object / array / interface / class: null
            return null;
        }
    }

    // ----------------------------------------------------------------------
    // Diagnostics
    // ----------------------------------------------------------------------

    @Override
    public String toString() {
        return "WestlakeContentResolver{userId=0, noopProvider="
                + (sNoopProvider != null) + "}";
    }
}
