// SPDX-License-Identifier: Apache-2.0
//
// ===========================================================================
// FROZEN-SURFACE-CLASS (CR22, 2026-05-12)
//
// Per codex review #2 Tier 3: this class has reached the upper bound of its
// architectural responsibility.  At 756 LOC it is already a mini ContextImpl
// covering resources, theme, content resolver, system-service routing,
// attached Application, data dirs, permission defaults, and no-op lifecycle.
//
// DO NOT extend this class without explicit architectural review.  Specifically:
//   1. NO new app-lifecycle behavior.  Activity / Service / Receiver / Provider
//      lifecycle belongs in the AOSP framework code itself, NOT in our Context.
//   2. NO new "real impl" Context.getXxx routes.  If a new getXxx site fires
//      in discovery, the FIRST response is a stub returning a safe default
//      (null / empty / false) plus a discovery report entry.  Promoting to a
//      real impl requires a separate review.
//   3. NO per-app branches.  This class generalises across all APKs; per-app
//      configuration belongs in WestlakeDiscoverWrapper's manifest path or
//      in a per-app profile file.
//   4. NO Unsafe.allocateInstance use.  This class is constructed normally;
//      Unsafe is the pattern for AOSP framework internals (ResourcesImpl,
//      AssetManager, ActivityThread) where the natural ctor path is broken,
//      NOT for our own classes.
//
// Any new Context surface noice or McDonald's exercises should first be:
//   (a) Documented as a discovery finding in docs/engine/M4_DISCOVERY.md
//   (b) Discussed in a CR-prefixed brief naming the call site + caller
//   (c) Reviewed against the binder pivot's substitution boundary (BINDER_PIVOT_DESIGN.md §3.1)
//
// If the answer is "this is what Hilt / AppCompat / framework.jar wants",
// the fix is usually a new M4-PRE field-plant on a synthetic framework object
// (see WestlakeResources.createSafe pattern), NOT extending WestlakeContextImpl.
// ===========================================================================
//
// Westlake M4-PRE — WestlakeContextImpl
//
// Minimal Context implementation that allows the AOSP framework.jar
// Application/ContextWrapper machinery to function inside the dalvikvm
// sandbox.  Built strictly to satisfy the methods the bootstrap of a
// Hilt-DI-using AOSP Application calls during onCreate().
//
// Rationale (see docs/engine/M4_DISCOVERY.md):
//   noice's `Hilt_NoiceApplication.onCreate()` calls Dagger-generated
//   providers which call `context.getPackageName()` on the Application's
//   mBase ContextWrapper field.  Before this class existed, mBase was
//   null (Application.attach had never been called) so the first
//   getPackageName threw NPE.  This impl is the minimum work to make
//   `application.attach(westlakeContextImpl)` succeed, so getPackageName
//   returns a real value.
//
// Strategy:
//   - Extend android.content.Context directly (NOT ContextWrapper) to
//     avoid the mBase-null delegation problem.
//   - Override ALL Context abstract methods (Android 11 SDK 30 base
//     surface) since the runtime parent class is framework.jar's abstract
//     Context.  Methods we have no use for return null / 0 / false; the
//     few methods Hilt's DI bootstrap actually touches have real impls.
//   - getSystemService(String) forwards to ServiceManager.getService(name)
//     once the matching M4 service binder is registered, otherwise null.
//     This pivots us from "shim each manager class" to "look up via
//     Binder service name", matching BINDER_PIVOT_DESIGN.md §3.2.
//   - No per-app branches: packageName is a constructor arg so the class
//     generalises to any APK.
//
// Compile-time vs run-time class hierarchy:
//   Compile: extends shim's android.content.Context (concrete class, all
//            methods defaultable; this lets the shim build succeed).
//   Runtime: extends framework.jar's android.content.Context (abstract
//            with ~131 abstract methods; we MUST override every one of
//            them or `new WestlakeContextImpl(...)` would throw
//            InstantiationError).  The duplicates list in
//            scripts/framework_duplicates.txt strips the shim Context
//            from aosp-shim.dex; framework.jar's Context wins at runtime.
//
// Author: M4-PRE agent  •  Date: 2026-05-12

package com.westlake.services;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.UserHandle;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Minimum Context impl for the M4-PRE bootstrap of Hilt-DI applications
 * inside the Westlake dalvikvm sandbox.
 */
public final class WestlakeContextImpl extends Context {

    // ------------ Configuration (immutable after construction) ------------
    private final String mPackageName;
    private final ClassLoader mClassLoader;
    private final File mDataDir;
    private final File mFilesDir;
    private final File mCacheDir;
    private final File mNoBackupFilesDir;
    private final ApplicationInfo mApplicationInfo;

    // ------------ Lazy state ------------
    private volatile Resources mResources;
    private volatile Resources.Theme mTheme;
    private volatile PackageManager mPackageManager;
    private volatile ContentResolver mContentResolver;

    // ------------ Application wiring (M4-PRE11) ------------
    // The Application instance whose mBase is this Context.  Hilt's
    // dagger.hilt.android.internal.Contexts.getApplication(Context) walks
    // ctx.getApplicationContext() looking for an Application; if our impl
    // returns `this` (a Context, NOT an Application), Hilt throws
    // IllegalStateException at MainActivity.onCreate.  We expose
    // setAttachedApplication(Application) so the wrapper (which owns the
    // lifecycle: instantiate Application -> attachBaseContext(ctx) ->
    // setAttachedApplication(app) -> onCreate) can publish the
    // Application here.  See docs/engine/M4_DISCOVERY.md §32.
    private volatile Application mAttachedApplication;

    // ----------------------------------------------------------------------
    // Construction
    // ----------------------------------------------------------------------

    /**
     * Build a Westlake Context for the given package.
     *
     * @param packageName the launched APK's package name (e.g.
     *                    "com.github.ashutoshgngwr.noice").
     * @param apkPath     absolute filesystem path to the APK on disk
     *                    (becomes ApplicationInfo.sourceDir).
     * @param dataDir     absolute filesystem path to a writable per-app
     *                    data directory (becomes getDataDir(), the parent
     *                    for getFilesDir/getCacheDir).
     * @param classLoader the PathClassLoader that loaded the APK's
     *                    classes.dex (becomes getClassLoader()).
     * @param targetSdk   targetSdkVersion to advertise (e.g. 33).
     */
    public WestlakeContextImpl(String packageName, String apkPath,
            String dataDir, ClassLoader classLoader, int targetSdk) {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName must be non-empty");
        }
        mPackageName = packageName;
        mClassLoader = classLoader != null ? classLoader
                : WestlakeContextImpl.class.getClassLoader();
        mDataDir = new File(dataDir != null ? dataDir
                : "/data/local/tmp/westlake/" + packageName);
        mFilesDir = new File(mDataDir, "files");
        mCacheDir = new File(mDataDir, "cache");
        mNoBackupFilesDir = new File(mDataDir, "no_backup");
        try {
            mDataDir.mkdirs();
            mFilesDir.mkdirs();
            mCacheDir.mkdirs();
            mNoBackupFilesDir.mkdirs();
        } catch (Throwable ignored) {}

        mApplicationInfo = new ApplicationInfo();
        mApplicationInfo.packageName = packageName;
        mApplicationInfo.processName = packageName;
        mApplicationInfo.sourceDir = apkPath;
        mApplicationInfo.publicSourceDir = apkPath;
        mApplicationInfo.dataDir = mDataDir.getAbsolutePath();
        mApplicationInfo.targetSdkVersion = targetSdk;
        mApplicationInfo.uid = android.os.Process.myUid();
    }

    // ----------------------------------------------------------------------
    // Methods Hilt DI bootstrap actually calls (real impls)
    // ----------------------------------------------------------------------

    public String getPackageName() {
        return mPackageName;
    }

    /** Final on framework.jar; kept here for clarity / shim-compile compat. */
    public String getOpPackageName() {
        return mPackageName;
    }

    public String getBasePackageName() {
        return mPackageName;
    }

    public ApplicationInfo getApplicationInfo() {
        return mApplicationInfo;
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    public Context getApplicationContext() {
        // M4-PRE11 (2026-05-12): Hilt's
        // dagger.hilt.android.internal.Contexts.getApplication(Context)
        // walks getApplicationContext() looking for an `Application`
        // instance.  When the wrapper has published the attached
        // Application via setAttachedApplication(...), return it so the
        // Hilt walk succeeds.  Otherwise return `this` (matches pre-M4-PRE11
        // behaviour and keeps the chain valid for callers that don't require
        // Application).
        Application app = mAttachedApplication;
        if (app != null) return app;
        return this;
    }

    /**
     * Publish the Application instance whose `mBase` is this Context.
     *
     * <p>NoiceDiscoverWrapper (and any future launcher) MUST call this
     * after {@code Application.attachBaseContext(thisContext)} and BEFORE
     * the Application's {@code onCreate()} runs — Hilt's @{code Contexts
     * .getApplication(Context)} walks the chain during DI bootstrap and
     * throws {@link IllegalStateException} if no {@link Application} is
     * reachable.
     *
     * @param app the {@link Application} that holds this Context as its
     *            base; may be {@code null} to clear the wiring.
     */
    public void setAttachedApplication(Application app) {
        mAttachedApplication = app;
    }

    /**
     * @return the Application previously published via {@link
     * #setAttachedApplication(Application)}, or {@code null} if none.
     */
    public Application getAttachedApplication() {
        return mAttachedApplication;
    }

    public Resources getResources() {
        Resources r = mResources;
        if (r != null) return r;
        synchronized (this) {
            if (mResources == null) {
                // CR29-3 (2026-05-13): McD's PHASE G4 (ContextThemeWrapper
                // .initializeTheme → getResources NPE) showed createSafe()
                // returning null because (a) no -Dwestlake.apk.path was set
                // by the discover wrappers and (b) `new WestlakeResources()`
                // itself threw LinkageError at runtime — the V2-Step4 class
                // had @Override of getAssets() and newTheme() against the
                // shim Resources (non-final at compile-time), but at runtime
                // the framework Resources class wins via classpath shadow
                // and both methods are final there. WestlakeResources.java
                // CR29-3 strips those two @Overrides; this method then prefers
                // empty() (guaranteed-fast, non-null Resources) so AppCompat's
                // getConfiguration / initializeTheme don't NPE.
                //
                // Order:
                //   1. WestlakeResources.empty() — fast, guaranteed-non-null
                //      zero-arsc instance. Provides Configuration +
                //      DisplayMetrics defaults so AppCompat's
                //      getConfiguration / initializeTheme don't NPE.
                //   2. createForApkPath(...) DELIBERATELY NOT FIRST. The McD
                //      resources.arsc is 3.3 MB and full parsing under
                //      dalvikvm's broken Charset fallback path takes minutes
                //      (verified in cr29-3 mcd-discover trace). A future CR
                //      can promote arsc-backed Resources on-demand once the
                //      cold-boot critical path is past.
                //   3. createSafe() — legacy entry point for engine-driven
                //      paths that set -Dwestlake.apk.path.
                //   4. Resources.getSystem() — final fallback (usually null
                //      under dalvikvm due to ResourcesImpl.<clinit> failure).
                //
                // Silent try/catch: System.err.println in this path NPEs
                // (Charset.newEncoder returns null pre-bootstrap; verified
                // in noice-discover-postM4PRE6.log).
                try {
                    mResources = WestlakeResources.empty();
                } catch (Throwable ignored) {}
                if (mResources == null) {
                    try {
                        mResources = WestlakeResources.createSafe();
                    } catch (Throwable ignored) {}
                }
                if (mResources == null) {
                    try {
                        mResources = Resources.getSystem();
                    } catch (Throwable ignored) {}
                }
            }
            return mResources;
        }
    }

    public AssetManager getAssets() {
        Resources r = getResources();
        return r != null ? r.getAssets() : null;
    }

    public Resources.Theme getTheme() {
        Resources.Theme t = mTheme;
        if (t != null) return t;
        synchronized (this) {
            if (mTheme == null) {
                try {
                    Resources r = getResources();
                    if (r != null) mTheme = r.newTheme();
                } catch (Throwable ignored) {}
            }
            return mTheme;
        }
    }

    public void setTheme(int resid) {
        // No-op; theme picked at getTheme() time from getResources().newTheme()
    }

    public Looper getMainLooper() {
        Looper l = Looper.getMainLooper();
        if (l != null) return l;
        // Last-resort: prepare the main looper for this thread.
        try { Looper.prepareMainLooper(); } catch (Throwable ignored) {}
        return Looper.getMainLooper();
    }

    public Object getSystemService(String name) {
        if (name == null) return null;

        // ------------------------------------------------------------------
        // Single chokepoint: SystemServiceWrapperRegistry handles BOTH
        //   - process-local services (CR4 -- layout_inflater, etc.) that
        //     have no IBinder backing in AOSP and are constructed per-Context
        //   - binder-backed services (CR3 -- activity, power, ...) that
        //     route through ServiceManager.getService + reflective Manager
        //     wrap
        // The registry decides which path to take per service name; this
        // method does NOT duplicate that decision.  See
        // SystemServiceWrapperRegistry.getSystemService and
        // SystemServiceWrapperRegistry.wrapProcessLocal for the actual
        // routing tables.
        //
        // CR3 (2026-05-12): codex Tier 3 §3 flagged this method as
        // architecturally drifted -- the doc comment promised this routing,
        // but the implementation returned null for everything.  CR3 made
        // the binder route real; CR4 (2026-05-12 evening) added the
        // layout_inflater process-local case.  M4_DISCOVERY.md §21+§23
        // track which Context.* SERVICE constants are currently routed.
        // ------------------------------------------------------------------
        return SystemServiceWrapperRegistry.getSystemService(name, this);
    }

    public String getSystemServiceName(Class<?> serviceClass) {
        // Inverse of getSystemService(Class<T>).  Required by ContextImpl;
        // returning null is fine (callers fall back to getSystemService(String)).
        return null;
    }

    public File getDataDir() {
        return mDataDir;
    }

    public File getFilesDir() {
        return mFilesDir;
    }

    public File getCacheDir() {
        return mCacheDir;
    }

    public File getCodeCacheDir() {
        File f = new File(mDataDir, "code_cache");
        try { f.mkdirs(); } catch (Throwable ignored) {}
        return f;
    }

    public File getNoBackupFilesDir() {
        return mNoBackupFilesDir;
    }

    public File getDir(String name, int mode) {
        File f = new File(mDataDir, "app_" + name);
        try { f.mkdirs(); } catch (Throwable ignored) {}
        return f;
    }

    public File getFileStreamPath(String name) {
        return new File(mFilesDir, name);
    }

    public File getSharedPreferencesPath(String name) {
        File shp = new File(mDataDir, "shared_prefs");
        try { shp.mkdirs(); } catch (Throwable ignored) {}
        return new File(shp, name + ".xml");
    }

    public String[] fileList() {
        String[] arr = mFilesDir.list();
        return arr != null ? arr : new String[0];
    }

    public boolean isDeviceProtectedStorage() { return false; }

    public boolean isCredentialProtectedStorage() { return false; }

    public boolean canLoadUnsafeResources() { return false; }

    public int getDisplayId() { return 0; /* Display.DEFAULT_DISPLAY */ }

    public void updateDisplay(int displayId) { /* no-op */ }

    // ----------------------------------------------------------------------
    // Methods we explicitly don't implement (return safe defaults)
    // ----------------------------------------------------------------------

    public PackageManager getPackageManager() {
        // M4-PRE5 (2026-05-12): noice's Hilt-generated attachBaseContext
        // calls pm.getServiceInfo(ComponentName, int) before any binder
        // transaction is even possible.  Return a local WestlakePackageManagerStub
        // that resolves the host app's components synthetically.  This is
        // NOT a binder-backed implementation -- it's a Java-only stub that
        // satisfies the abstract PackageManager surface; the binder
        // IPackageManager is M4c's separate work.
        PackageManager pm = mPackageManager;
        if (pm != null) return pm;
        synchronized (this) {
            if (mPackageManager == null) {
                try {
                    mPackageManager = new WestlakePackageManagerStub(
                            mPackageName,
                            mApplicationInfo.sourceDir,
                            mApplicationInfo);
                } catch (Throwable t) {
                    // Best-effort: failure to build the stub means we fall
                    // back to null (the M4-PRE4 behaviour).
                    try {
                        System.err.println(
                                "[WLK-ctx] PackageManager init failed: " + t);
                    } catch (Throwable ignored) {}
                }
            }
            return mPackageManager;
        }
    }

    public ContentResolver getContentResolver() {
        // M4-PRE9 (2026-05-12): pre-M4-PRE9 we returned null here, which
        // worked for Hilt's deferred provider lookups but NPE'd inside
        // PhoneWindow.<init>(Context) when it calls
        //   Settings.Global.getInt(getContentResolver(), name, default)
        // -> Settings.Global.getString(cr, name)
        // -> cr.getUserId()        // <-- NPE on null cr.
        // (See CR4 report; noice-discover-postCR4.log).
        //
        // Return a lazily-built WestlakeContentResolver that:
        //   1. Overrides getUserId() to return USER_SYSTEM (0).
        //   2. Returns a no-op IContentProvider Proxy from acquireProvider.
        // The Proxy makes provider.call(...) return null, NameValueCache
        // interprets that as "value not in DB" and falls through to its
        // cursor fallback (wrapped in an <any> catch returning null on
        // any exception).  Settings.Global.getInt thus returns the
        // caller-supplied default value -- exactly what PhoneWindow
        // tolerates here (e.g. haptic_feedback_intensity defaults are
        // fine).  See WestlakeContentResolver class doc and
        // docs/engine/M4_DISCOVERY.md §25.
        ContentResolver cr = mContentResolver;
        if (cr != null) return cr;
        synchronized (this) {
            if (mContentResolver == null) {
                try {
                    mContentResolver = new WestlakeContentResolver(this);
                } catch (Throwable t) {
                    // Construction failed (e.g. abstract-method mismatch
                    // post framework.jar refresh).  Log so discovery can
                    // see the cause; fall back to the legacy null which
                    // restores the pre-M4-PRE9 NPE.
                    try {
                        System.err.println(
                                "[WLK-ctx] ContentResolver init failed: " + t);
                    } catch (Throwable ignored) {}
                }
            }
            return mContentResolver;
        }
    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        // CR29-2 (2026-05-13): McD Application.onCreate asserts the
        // return value is non-null (V2-Probe G3 FAIL).  Back the SP by
        // a Properties file under our per-app sandbox; see
        // WestlakeSharedPreferences for the storage / encoding contract.
        // `mode` is intentionally ignored — single-process sandbox, so
        // MODE_MULTI_PROCESS / MODE_WORLD_READABLE / etc. carry no
        // semantics here.
        String safeName = (name == null || name.isEmpty()) ? "default" : name;
        return new WestlakeSharedPreferences(getSharedPreferencesPath(safeName));
    }

    public SharedPreferences getSharedPreferences(File file, int mode) {
        // API 24+ overload (Context#getSharedPreferences(File, int)).
        // Caller supplies the backing file directly; pass through.
        return new WestlakeSharedPreferences(file);
    }

    public boolean moveSharedPreferencesFrom(Context sourceContext, String name) {
        return false;
    }

    public boolean deleteSharedPreferences(String name) {
        return false;
    }

    public void reloadSharedPreferences() { /* no-op */ }

    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return new FileInputStream(new File(mFilesDir, name));
    }

    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        return new FileOutputStream(new File(mFilesDir, name), (mode & MODE_APPEND) != 0);
    }

    public boolean deleteFile(String name) {
        return new File(mFilesDir, name).delete();
    }

    public File getExternalFilesDir(String type) { return null; }

    public File[] getExternalFilesDirs(String type) { return new File[0]; }

    public File getObbDir() { return null; }

    public File[] getObbDirs() { return new File[0]; }

    public File getExternalCacheDir() { return null; }

    public File getPreloadsFileCache() { return null; }

    public File[] getExternalCacheDirs() { return new File[0]; }

    public File[] getExternalMediaDirs() { return new File[0]; }

    public String getPackageResourcePath() {
        return mApplicationInfo.sourceDir;
    }

    public String getPackageCodePath() {
        return mApplicationInfo.sourceDir;
    }

    // ----------------------------------------------------------------------
    // Databases — unsupported
    // ----------------------------------------------------------------------

    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
            SQLiteDatabase.CursorFactory factory) {
        return null;
    }

    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
            SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return null;
    }

    public boolean moveDatabaseFrom(Context sourceContext, String name) { return false; }

    public boolean deleteDatabase(String name) { return false; }

    public File getDatabasePath(String name) {
        File db = new File(mDataDir, "databases");
        try { db.mkdirs(); } catch (Throwable ignored) {}
        return new File(db, name);
    }

    public String[] databaseList() {
        File db = new File(mDataDir, "databases");
        String[] arr = db.list();
        return arr != null ? arr : new String[0];
    }

    // ----------------------------------------------------------------------
    // Wallpapers — completely unused
    // ----------------------------------------------------------------------

    public Drawable getWallpaper() { return null; }
    public Drawable peekWallpaper() { return null; }
    public int getWallpaperDesiredMinimumWidth() { return 0; }
    public int getWallpaperDesiredMinimumHeight() { return 0; }
    public void setWallpaper(Bitmap bitmap) throws IOException { /* no-op */ }
    public void setWallpaper(InputStream data) throws IOException { /* no-op */ }
    public void clearWallpaper() throws IOException { /* no-op */ }

    // ----------------------------------------------------------------------
    // Activity / broadcast / service starts — defer to M4-A
    // ----------------------------------------------------------------------

    public void startActivity(Intent intent) { /* defer to M4-A */ }
    public void startActivity(Intent intent, Bundle options) { /* defer */ }
    public void startActivities(Intent[] intents) { /* defer */ }
    public void startActivities(Intent[] intents, Bundle options) { /* defer */ }

    public void startIntentSender(IntentSender intent, Intent fillInIntent,
            int flagsMask, int flagsValues, int extraFlags) { /* defer */ }

    public void startIntentSender(IntentSender intent, Intent fillInIntent,
            int flagsMask, int flagsValues, int extraFlags, Bundle options) { /* defer */ }

    public void sendBroadcast(Intent intent) { /* defer */ }
    public void sendBroadcast(Intent intent, String receiverPermission) { /* defer */ }
    public void sendBroadcastAsUserMultiplePermissions(Intent intent, UserHandle user, String[] receiverPermissions) { /* defer */ }
    public void sendBroadcast(Intent intent, String receiverPermission, Bundle options) { /* defer */ }
    public void sendBroadcast(Intent intent, String receiverPermission, int appOp) { /* defer */ }

    public void sendOrderedBroadcast(Intent intent, String receiverPermission) { /* defer */ }

    public void sendOrderedBroadcast(Intent intent, String receiverPermission,
            BroadcastReceiver resultReceiver, Handler scheduler, int initialCode,
            String initialData, Bundle initialExtras) { /* defer */ }

    public void sendOrderedBroadcast(Intent intent, String receiverPermission,
            Bundle options, BroadcastReceiver resultReceiver, Handler scheduler,
            int initialCode, String initialData, Bundle initialExtras) { /* defer */ }

    public void sendOrderedBroadcast(Intent intent, String receiverPermission,
            int appOp, BroadcastReceiver resultReceiver, Handler scheduler,
            int initialCode, String initialData, Bundle initialExtras) { /* defer */ }

    public void sendBroadcastAsUser(Intent intent, UserHandle user) { /* defer */ }
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) { /* defer */ }
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, int appOp) { /* defer */ }
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, Bundle options) { /* defer */ }

    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user,
            String receiverPermission, BroadcastReceiver resultReceiver,
            Handler scheduler, int initialCode, String initialData, Bundle initialExtras) { /* defer */ }

    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user,
            String receiverPermission, int appOp, BroadcastReceiver resultReceiver,
            Handler scheduler, int initialCode, String initialData, Bundle initialExtras) { /* defer */ }

    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user,
            String receiverPermission, int appOp, Bundle options,
            BroadcastReceiver resultReceiver, Handler scheduler, int initialCode,
            String initialData, Bundle initialExtras) { /* defer */ }

    public void sendStickyBroadcast(Intent intent) { /* defer */ }
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) { /* defer */ }
    public void removeStickyBroadcast(Intent intent) { /* defer */ }
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) { /* defer */ }
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user, Bundle options) { /* defer */ }
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) { /* defer */ }
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) { /* defer */ }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return null;
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        return null;
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
            String broadcastPermission, Handler scheduler) {
        return null;
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
            String broadcastPermission, Handler scheduler, int flags) {
        return null;
    }

    public Intent registerReceiverAsUser(BroadcastReceiver receiver, UserHandle user,
            IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return null;
    }

    public void unregisterReceiver(BroadcastReceiver receiver) { /* no-op */ }

    public ComponentName startService(Intent service) { return null; }
    public ComponentName startForegroundService(Intent service) { return null; }
    public ComponentName startForegroundServiceAsUser(Intent service, UserHandle user) { return null; }
    public boolean stopService(Intent service) { return false; }
    public ComponentName startServiceAsUser(Intent service, UserHandle user) { return null; }
    public boolean stopServiceAsUser(Intent service, UserHandle user) { return false; }

    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return false;
    }

    public void unbindService(ServiceConnection conn) { /* no-op */ }

    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        return false;
    }

    // ----------------------------------------------------------------------
    // Permissions — allow everything (sandbox runs as su / system uid)
    // ----------------------------------------------------------------------

    public int checkPermission(String permission, int pid, int uid) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public int checkPermission(String permission, int pid, int uid, IBinder callerToken) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public int checkCallingPermission(String permission) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public int checkCallingOrSelfPermission(String permission) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public int checkSelfPermission(String permission) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public void enforcePermission(String permission, int pid, int uid, String message) { /* allow */ }
    public void enforceCallingPermission(String permission, String message) { /* allow */ }
    public void enforceCallingOrSelfPermission(String permission, String message) { /* allow */ }

    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) { /* no-op */ }
    public void revokeUriPermission(Uri uri, int modeFlags) { /* no-op */ }
    public void revokeUriPermission(String toPackage, Uri uri, int modeFlags) { /* no-op */ }

    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags, IBinder callerToken) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public int checkUriPermission(Uri uri, String readPermission, String writePermission,
            int pid, int uid, int modeFlags) {
        return PackageManager.PERMISSION_GRANTED;
    }

    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) { /* allow */ }
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) { /* allow */ }
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) { /* allow */ }
    public void enforceUriPermission(Uri uri, String readPermission, String writePermission,
            int pid, int uid, int modeFlags, String message) { /* allow */ }

    // ----------------------------------------------------------------------
    // Context derivation — return `this` (we are the only Context we know)
    // ----------------------------------------------------------------------

    // NOTE: framework.jar Context declares these `throws NameNotFoundException`,
    // but the shim's compile-time Context omits the throws clause.  We match
    // the shim's signature here; at runtime the JVM resolves these methods by
    // name+args ignoring `throws` (Java throws is compile-time only).
    public Context createPackageContext(String packageName, int flags) {
        return this;
    }

    public Context createApplicationContext(ApplicationInfo application, int flags) {
        return this;
    }

    public Context createContextForSplit(String splitName) {
        return this;
    }

    public Context createConfigurationContext(Configuration overrideConfiguration) {
        return this;
    }

    public Context createDisplayContext(Display display) {
        return this;
    }

    public Context createDeviceProtectedStorageContext() {
        return this;
    }

    public Context createCredentialProtectedStorageContext() {
        return this;
    }

    public android.view.DisplayAdjustments getDisplayAdjustments(int displayId) {
        return null;
    }
}
