// SPDX-License-Identifier: Apache-2.0
//
// V2 Step 2: WestlakeActivity (the central macro substitute)
// =========================================================
// Shadowed via framework_duplicates.txt — wins over framework.jar's Activity.
//
// Authoritative contract: docs/engine/WESTLAKE_ACTIVITY_API.md (~324 methods,
// 87 Implement / 178 fail-loud / 14 no-op).
//
// App's MainActivity extends FragmentActivity (framework.jar / androidx)
// extends ComponentActivity (shim or androidx) extends Activity (OURS).
// No real AOSP Activity.attach cold-init machinery. No real Window/Resources/
// Theme state cascade. Lifecycle methods dispatch user code; Hilt @Inject
// and binder calls fire normally.
//
// Architectural anchors:
//   - BINDER_PIVOT_DESIGN_V2.md §3.3 decision 10-B (classpath-shadow Activity)
//   - BINDER_PIVOT_DESIGN_V2.md §3.5 (no PhoneWindow chrome / Resources cascade)
//   - WESTLAKE_ACTIVITY_API.md §2.1 (attach overloads), §3 (Implement bucket),
//     §4 (fail-loud bucket), §5 (no-op bucket), §7 (constants), §8 (fields)
//
// Anti-patterns avoided:
//   - NO per-app branches
//   - NO Unsafe.allocateInstance
//   - NO Field.setAccessible self-mutation; the only reflection use is the
//     dispatchActivityCreated callback into Application (defensive package
//     boundary, both in android.app).
//
// This class extends ContextThemeWrapper (compile-time: shim's; runtime:
// framework.jar's, since ContextThemeWrapper is in framework_duplicates.txt).
// Apps that cast Activity → ContextWrapper / Context still work.

package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toolbar;

import com.android.internal.policy.PhoneWindow;
import com.westlake.services.ServiceMethodMissing;
import com.westlake.services.SystemServiceWrapperRegistry;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * V2 WestlakeActivity (classpath-shadow). See file header.
 *
 * Extends ContextThemeWrapper so theme/getResources() dispatch routes
 * through our mBase (a WestlakeContextImpl). super(null) is fine because
 * mBase is seeded by {@link #attachBaseContext(Context)} during
 * {@link #attach(Context, Application, Intent, ComponentName, android.view.Window, Instrumentation)}.
 */
public class Activity extends android.view.ContextThemeWrapper {

    private static final String FAIL_TAG = "Activity";

    // ─── Public/protected fields (§8). Visibility chosen to match
    //     framework Activity so app code reading via super.mFoo works. ───
    /** Set during attach. */
    protected Application mApplication;
    /** Set during attach / setIntent. */
    protected Intent mIntent;
    /** Set during attach. */
    protected ComponentName mComponent;
    /** Set during attach. */
    protected Instrumentation mInstrumentation;
    /** Set during attach (may be null in 6-arg path). */
    protected ActivityInfo mActivityInfo;
    /** Set during attach / setTitle. */
    protected CharSequence mTitle = "";
    /** Set during setTitleColor (default Color.BLACK). */
    protected int mTitleColor = 0xFF000000;
    /** V2 §3.5: PhoneWindow is a stub. */
    protected Window mWindow;
    /** V2 §3.5: WindowManager is null (no chrome). */
    protected WindowManager mWindowManager;
    /** Synthetic IBinder for M4a routing. */
    protected IBinder mToken;
    /** Stable assist token (synthetic). */
    protected IBinder mAssistToken;
    /** Task identity for M4a. */
    protected int mIdent;
    /** finish() set this. */
    protected boolean mFinished;
    /** performDestroy() sets this. */
    protected boolean mDestroyed;
    /** performStart()/performStop() toggle. */
    protected boolean mStarted;
    /** performResume()/performPause() toggle. Load-bearing for Compose. */
    protected boolean mResumed;
    /** Super-call sentinel; lifecycle bodies set true before returning. */
    protected boolean mCalled;
    /** Root inflated view (V2-specific name, not framework). */
    protected View mContentView;
    /** Lazy LayoutInflater bound to this Context. */
    protected LayoutInflater mLayoutInflater;
    /** Lazy MenuInflater bound to this Context. */
    protected MenuInflater mMenuInflater;
    /** ActivityLifecycleCallbacks list (per-instance). */
    protected ArrayList<Object> mLifecycleCallbacks;
    /** Lazy theme from WestlakeResources.newTheme(); current theme resid. */
    protected int mThemeResource;
    /** Parent Activity (always null in V2 — no nested ActivityGroup). */
    protected Activity mParent;
    /** Result code (Activity.setResult) — final method in framework. */
    protected int mResultCode = RESULT_CANCELED;
    /** Result data (Activity.setResult). */
    protected Intent mResultData;
    /** Requested orientation (set by setRequestedOrientation). */
    protected int mRequestedOrientation = -1;
    /** Default key mode (setDefaultKeyMode). */
    protected int mDefaultKeyMode = DEFAULT_KEYS_DISABLE;
    /** CR32: setVisible(boolean) bookkeeping; sandbox has no decor to toggle. */
    protected boolean mVisibleFromClient = true;

    // ─── Constants (§7) ────────────────────────────────────────────────
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_OK = -1;
    public static final int RESULT_FIRST_USER = 1;
    public static final int DONT_FINISH_TASK_WITH_ACTIVITY = 0;
    public static final int FINISH_TASK_WITH_ROOT_ACTIVITY = 1;
    public static final int FINISH_TASK_WITH_ACTIVITY = 2;
    public static final int DEFAULT_KEYS_DIALER = 1;
    public static final int DEFAULT_KEYS_DISABLE = 0;
    public static final int DEFAULT_KEYS_SEARCH_GLOBAL = 4;
    public static final int DEFAULT_KEYS_SEARCH_LOCAL = 3;
    public static final int DEFAULT_KEYS_SHORTCUT = 2;
    /** AOSP places this as protected — keep it for source compat. */
    protected static final int[] FOCUSED_STATE_SET = { 0x0101009C /* android.R.attr.state_focused */ };

    // ─── Constructor ───────────────────────────────────────────────────
    // Note: compile-time ContextThemeWrapper has only a no-arg ctor; runtime
    // framework.jar's ContextThemeWrapper provides the rich(er) (Context)
    // ctor. Either way, mBase is seeded later via attachBaseContext().
    public Activity() {
        super();
    }

    // ─── attach (§2.1; both overloads, single attachInternal) ──────────

    /**
     * 6-arg overload — the existing WestlakeActivityThread.attachActivity call
     * site (CR23-fix). Public so cross-package callers (com.westlake.engine,
     * android.app's WestlakeActivityThread) can invoke us. Promoted from
     * package-private per WESTLAKE_ACTIVITY_API.md surprise #1.
     */
    public final void attach(
            Context base,
            Application application,
            Intent intent,
            ComponentName component,
            Window window,
            Instrumentation instrumentation) {
        attachInternal(base, application, intent, component, instrumentation, null);
        // 6-arg form: caller may pass an explicit window; honor it if non-null.
        if (window != null) {
            mWindow = window;
        }
    }

    /**
     * Single private substrate. Records ONLY the V2-live fields per
     * WESTLAKE_ACTIVITY_API.md §2.1. We do NOT touch mUiThread, mMainThread,
     * mFragments, mLoadedApk, mLastNonConfigurationInstances, mVoiceInteractor,
     * mCurrentConfig — those are framework cold-init grab-bag fields.
     */
    private void attachInternal(Context base, Application application, Intent intent,
            ComponentName component, Instrumentation instrumentation, ActivityInfo info) {
        // §12 Q3: route mBase through ContextWrapper.attachBaseContext.
        if (base != null) {
            try {
                attachBaseContext(base);
            } catch (Throwable ignored) {
                // Pre-attached states may throw IllegalStateException; that's fine.
                // We'll force-set mBase below.
            }
            // CR58 (2026-05-14): Hilt-generated subclasses (e.g. Hilt_MainActivity)
            // override attachBaseContext to WRAP newBase in a Hilt context (observed
            // as obfuscated "i.f" in noice). That wrapper's mBase chain to our
            // WestlakeContextImpl is correct, but downstream code paths sometimes
            // copy contexts whose mBase isn't attached. Force-set mBase back to our
            // WestlakeContextImpl so Activity.getApplicationContext() always walks
            // through WestlakeContextImpl.mAttachedApplication.
            //
            // This is a write to OUR Activity's own ContextWrapper field — not
            // plant-on-framework-class (the field is on the framework ContextWrapper
            // SUPERCLASS but the field-instance is on OUR Activity object).
            try {
                Class<?> cw = Class.forName("android.content.ContextWrapper");
                java.lang.reflect.Field mBaseField = cw.getDeclaredField("mBase");
                mBaseField.setAccessible(true);
                Object current = mBaseField.get(this);
                boolean currentIsWestlake = current != null
                    && current.getClass().getName().startsWith("com.westlake.");
                if (current == null || (!currentIsWestlake && base != null
                        && base.getClass().getName().startsWith("com.westlake."))) {
                    mBaseField.set(this, base);
                }
            } catch (Throwable ignored) {
                // Best-effort; if the field is sealed in a future Android release
                // we'll see the failure mode and revisit.
            }
        }
        mApplication = application;
        mIntent = intent;
        mComponent = component != null ? component
                : (intent != null ? intent.getComponent() : null);
        mInstrumentation = instrumentation;
        mActivityInfo = info;
        mTitle = mTitle != null ? mTitle : "";
        // V2 §3.5: no PhoneWindow chrome. Initialize a stub so getWindow()
        // returns non-null (some AppCompat paths null-check it).
        if (mWindow == null) {
            try {
                mWindow = new PhoneWindow(this);
            } catch (Throwable t) {
                android.util.Log.w("WLK-CR36",
                        "PhoneWindow(this) ctor threw -- mWindow stays null", t);
                mWindow = null;
            }
        }
        // CR31-A: install a no-op Window.Callback so `window.getCallback()` is
        // never null. AppCompatDelegate.attachBaseContext (e.k0.q in noice's
        // R8-minified bytecode) calls getCallback() and wraps it; null causes
        // NPE during ComponentActivity.onCreate. AOSP's Activity implements
        // Window.Callback itself; our shim doesn't (~20 method surface), so we
        // install a minimal stub that returns false / no-ops for everything.
        if (mWindow != null && mWindow.getCallback() == null) {
            try {
                mWindow.setCallback(new android.view.Window.Callback() {
                    @Override public boolean dispatchKeyEvent(android.view.KeyEvent event) { return false; }
                    @Override public boolean dispatchTouchEvent(android.view.MotionEvent event) { return false; }
                    @Override public boolean dispatchTrackballEvent(android.view.MotionEvent event) { return false; }
                    @Override public void onContentChanged() {}
                    @Override public void onWindowFocusChanged(boolean hasFocus) {}
                    @Override public void onAttachedToWindow() {}
                    @Override public void onDetachedFromWindow() {}
                    @Override public void onWindowAttributesChanged(android.view.WindowManager.LayoutParams attrs) {}
                    @Override public boolean onMenuOpened(int featureId, android.view.Menu menu) { return false; }
                    @Override public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) { return false; }
                    @Override public void onPanelClosed(int featureId, android.view.Menu menu) {}
                    @Override public boolean onSearchRequested() { return false; }
                    @Override public android.view.View onCreatePanelView(int featureId) { return null; }
                    @Override public boolean onCreatePanelMenu(int featureId, android.view.Menu menu) { return false; }
                    @Override public boolean onPreparePanel(int featureId, android.view.View view, android.view.Menu menu) { return false; }
                });
            } catch (Throwable ignored) {}
        }
        // CR31-A: also eagerly resolve WindowManager so getWindow() consumers
        // that grab mWindowManager directly don't NPE either.
        if (mWindowManager == null) {
            try {
                Object svc = getSystemService(WINDOW_SERVICE);
                if (svc instanceof WindowManager) {
                    mWindowManager = (WindowManager) svc;
                }
            } catch (Throwable ignored) {}
        }
        // CR55 (2026-05-13): force LifecycleRegistry's internal observer-map
        // lazy-init by adding+removing a no-op observer. AndroidX
        // ComponentActivity.<init> creates mLifecycleRegistry but its internal
        // FastSafeIterableMap is lazy-allocated on first addObserver(). Without
        // priming, ComponentActivity.onCreate -> registerForActivityResult ->
        // Lifecycle.addObserver causes "Attempt to read from null array" NPE
        // (observed PHASE G4 of discovery harness for both noice + McD).
        // Adding an observer is public API; not framework-state planting.
        try {
            // getLifecycle() is on framework Activity (returns LifecycleOwner's lifecycle
            // via ComponentActivity); use reflection so we don't compile-depend on it.
            java.lang.reflect.Method getLifecycle = null;
            for (java.lang.reflect.Method m : getClass().getMethods()) {
                if ("getLifecycle".equals(m.getName()) && m.getParameterCount() == 0) {
                    getLifecycle = m;
                    break;
                }
            }
            if (getLifecycle != null) {
                Object lc = getLifecycle.invoke(this);
                if (lc != null) {
                    Class<?> observerIface = null;
                    try {
                        observerIface = Class.forName(
                                "androidx.lifecycle.LifecycleObserver",
                                false, getClass().getClassLoader());
                    } catch (Throwable ignored) {}
                    if (observerIface != null) {
                        Object noop = java.lang.reflect.Proxy.newProxyInstance(
                                observerIface.getClassLoader(),
                                new Class<?>[] { observerIface },
                                new java.lang.reflect.InvocationHandler() {
                                    @Override public Object invoke(Object p, java.lang.reflect.Method mm, Object[] a) {
                                        return null;
                                    }
                                });
                        for (java.lang.reflect.Method lm : lc.getClass().getMethods()) {
                            if (!"addObserver".equals(lm.getName())) continue;
                            if (lm.getParameterCount() != 1) continue;
                            if (!lm.getParameterTypes()[0].isAssignableFrom(observerIface)) continue;
                            try {
                                lm.invoke(lc, noop);
                                // Successful add — registry's internal map is now initialized.
                                break;
                            } catch (Throwable ignored) {}
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
            // CR55 is best-effort; if it fails the user's onCreate will hit
            // the same NPE the original discovery probe caught.
        }
    }

    // ─── attachBaseContext (lifecycle §3 row 7) ────────────────────────
    // Public visibility matches the shim's ContextWrapper.attachBaseContext.
    // (Real framework declares it protected, but the override-relaxation rule
    // means an Activity making it public is legal in both build and runtime.)
    @Override
    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    // ─── Core getters (§3 Implement bucket) ────────────────────────────

    public Intent getIntent() {
        return mIntent;
    }

    public void setIntent(Intent newIntent) {
        mIntent = newIntent;
    }

    public final Application getApplication() {
        return mApplication;
    }

    public final boolean isChild() {
        return mParent != null;
    }

    public final Activity getParent() {
        return mParent;
    }

    public WindowManager getWindowManager() {
        if (mWindowManager == null) {
            Object svc = getSystemService(WINDOW_SERVICE);
            if (svc instanceof WindowManager) {
                mWindowManager = (WindowManager) svc;
            }
        }
        return mWindowManager;
    }

    public Window getWindow() {
        return mWindow;
    }

    /**
     * Walk mContentView for focused subview; null fallback.
     * AOSP surprise #9: InputMethodManager.showSoftInput(...) reads this.
     */
    public View getCurrentFocus() {
        if (mContentView == null) return null;
        return mContentView.findFocus();
    }

    // Final per framework — apps depend on it.
    public final IBinder getActivityToken() {
        return mToken;
    }

    // Final per framework — return synthetic stable IBinder per §12 Q4.
    public final IBinder getAssistToken() {
        if (mAssistToken == null) {
            mAssistToken = new android.os.Binder();
        }
        return mAssistToken;
    }

    // ─── Lifecycle hooks (§3 row 10-28: user-overridable, mCalled=true) ─

    protected void onCreate(Bundle savedInstanceState) {
        mCalled = true;
    }

    /** API 21+ variant; delegate to 1-arg (§2.11). */
    protected void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        onCreate(savedInstanceState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        onRestoreInstanceState(savedInstanceState);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
    }

    protected void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        onPostCreate(savedInstanceState);
    }

    protected void onStart() {
        mCalled = true;
    }

    protected void onRestart() {
        mCalled = true;
    }

    public void onStateNotSaved() {
    }

    protected void onResume() {
        mCalled = true;
    }

    protected void onPostResume() {
        mCalled = true;
    }

    public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
    }

    protected void onNewIntent(Intent intent) {
    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    protected void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        onSaveInstanceState(outState);
    }

    protected void onPause() {
        mCalled = true;
    }

    protected void onUserLeaveHint() {
    }

    protected void onStop() {
        mCalled = true;
    }

    protected void onDestroy() {
        mCalled = true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        mCalled = true;
    }

    public void onLowMemory() {
        mCalled = true;
    }

    public void onTrimMemory(int level) {
        mCalled = true;
    }

    // ─── Lifecycle drivers (§2.12, §3 row 87 — performXxx family) ──────
    // Each calls the corresponding onXxx + dispatches to Application's
    // ActivityLifecycleCallbacks. Same-package call (android.app) — direct
    // invocation of Application's package-private dispatchActivityXxx hooks.

    final void performCreate(Bundle icicle) {
        performCreate(icicle, null);
    }

    final void performCreate(Bundle icicle, PersistableBundle persistentState) {
        if (mApplication != null) {
            mApplication.dispatchActivityPreCreated(this, icicle);
        }
        mCalled = false;
        if (persistentState != null) {
            onCreate(icicle, persistentState);
        } else {
            onCreate(icicle);
        }
        if (mApplication != null) {
            mApplication.dispatchActivityCreated(this, icicle);
            mApplication.dispatchActivityPostCreated(this, icicle);
        }
    }

    final void performNewIntent(Intent intent) {
        onNewIntent(intent);
    }

    final void performStart(String reason) {
        if (mApplication != null) {
            mApplication.dispatchActivityPreStarted(this);
        }
        mCalled = false;
        onStart();
        mStarted = true;
        if (mApplication != null) {
            mApplication.dispatchActivityStarted(this);
            mApplication.dispatchActivityPostStarted(this);
        }
    }

    final void performRestart(boolean start, String reason) {
        if (mStarted) {
            // not in stopped state — ignore restart
            return;
        }
        mCalled = false;
        onRestart();
        if (start) {
            performStart(reason);
        }
    }

    final void performResume(boolean followedByPause, String reason) {
        if (mApplication != null) {
            mApplication.dispatchActivityPreResumed(this);
        }
        mCalled = false;
        onResume();
        mResumed = true;
        onPostResume();
        if (mApplication != null) {
            mApplication.dispatchActivityResumed(this);
            mApplication.dispatchActivityPostResumed(this);
        }
    }

    final void performPause() {
        if (mApplication != null) {
            mApplication.dispatchActivityPrePaused(this);
        }
        mCalled = false;
        onPause();
        mResumed = false;
        if (mApplication != null) {
            mApplication.dispatchActivityPaused(this);
            mApplication.dispatchActivityPostPaused(this);
        }
    }

    final void performUserLeaving() {
        onUserLeaveHint();
    }

    final void performStop(boolean preserveWindow, String reason) {
        if (mApplication != null) {
            mApplication.dispatchActivityPreStopped(this);
        }
        mCalled = false;
        onStop();
        mStarted = false;
        if (mApplication != null) {
            mApplication.dispatchActivityStopped(this);
            mApplication.dispatchActivityPostStopped(this);
        }
    }

    final void performDestroy() {
        if (mApplication != null) {
            mApplication.dispatchActivityPreDestroyed(this);
        }
        mCalled = false;
        onDestroy();
        mDestroyed = true;
        if (mApplication != null) {
            mApplication.dispatchActivityDestroyed(this);
            mApplication.dispatchActivityPostDestroyed(this);
        }
    }

    final void performSaveInstanceState(Bundle outState) {
        onSaveInstanceState(outState);
        if (mApplication != null) {
            mApplication.dispatchActivitySaveInstanceState(this, outState);
        }
    }

    final void performSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        onSaveInstanceState(outState, outPersistentState);
        if (mApplication != null) {
            mApplication.dispatchActivitySaveInstanceState(this, outState);
        }
    }

    final void performRestoreInstanceState(Bundle savedInstanceState) {
        onRestoreInstanceState(savedInstanceState);
    }

    final void performRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        onRestoreInstanceState(savedInstanceState, persistentState);
    }

    // ─── ActivityLifecycleCallbacks register/unregister (§3 row 8/9) ───

    public void registerActivityLifecycleCallbacks(
            Application.ActivityLifecycleCallbacks callback) {
        if (mApplication != null) {
            mApplication.registerActivityLifecycleCallbacks(callback);
        }
        if (mLifecycleCallbacks == null) {
            mLifecycleCallbacks = new ArrayList<Object>();
        }
        mLifecycleCallbacks.add(callback);
    }

    public void unregisterActivityLifecycleCallbacks(
            Application.ActivityLifecycleCallbacks callback) {
        if (mApplication != null) {
            mApplication.unregisterActivityLifecycleCallbacks(callback);
        }
        if (mLifecycleCallbacks != null) {
            mLifecycleCallbacks.remove(callback);
        }
    }

    // ─── findViewById / requireViewById (§3 row 32/33) ─────────────────

    public <T extends View> T findViewById(int id) {
        if (mContentView == null) return null;
        @SuppressWarnings("unchecked")
        T v = (T) mContentView.findViewById(id);
        return v;
    }

    public final <T extends View> T requireViewById(int id) {
        T v = findViewById(id);
        if (v == null) {
            throw new IllegalArgumentException(
                    "ID does not reference a View inside this Activity");
        }
        return v;
    }

    // ─── getActionBar (§3 row 34): AppCompat owns its own. ─────────────

    public ActionBar getActionBar() {
        return null;
    }

    // ─── setContentView family (§3 row 35-38) ──────────────────────────

    public void setContentView(int layoutResID) {
        try {
            LayoutInflater inflater = getLayoutInflater();
            if (inflater != null) {
                mContentView = inflater.inflate(layoutResID, null);
            }
        } catch (Throwable t) {
            // Inflate failures are common in headless contexts; surface as a
            // discovery signal but don't crash the launch.
        }
    }

    public void setContentView(View view) {
        mContentView = view;
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        // V2 §3.5: no real window, ignore params.
        mContentView = view;
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (mContentView instanceof ViewGroup) {
            ((ViewGroup) mContentView).addView(view, params);
        }
    }

    // ─── User-override hooks: empty bodies (§3 row 39-58) ──────────────

    public void onBackPressed() {
        finish();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void onUserInteraction() {
    }

    public void onContentChanged() {
    }

    public void onWindowFocusChanged(boolean hasFocus) {
    }

    public void onAttachedToWindow() {
    }

    public void onDetachedFromWindow() {
    }

    public boolean hasWindowFocus() {
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void onOptionsMenuClosed(Menu menu) {
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    }

    public void registerForContextMenu(View view) {
    }

    public void unregisterForContextMenu(View view) {
    }

    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }

    public void onContextMenuClosed(Menu menu) {
    }

    public boolean onSearchRequested(SearchEvent searchEvent) {
        return onSearchRequested();
    }

    public boolean onSearchRequested() {
        return false;
    }

    public void onAttachFragment(Fragment fragment) {
    }

    // ─── LayoutInflater / MenuInflater (§3 row 60/61) ──────────────────

    public LayoutInflater getLayoutInflater() {
        if (mLayoutInflater == null) {
            try {
                mLayoutInflater = LayoutInflater.from(this);
            } catch (Throwable t) {
                Object svc = getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (svc instanceof LayoutInflater) {
                    mLayoutInflater = (LayoutInflater) svc;
                }
            }
        }
        return mLayoutInflater;
    }

    /**
     * §2.10 + surprise #10: AppCompatDelegate.installMenuInflater hits this.
     * Return a thin MenuInflater bound to this Context. The MenuInflater
     * class itself is in framework.jar — we use the real one with our
     * Context as the inflate base.
     */
    public MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new MenuInflater(this);
        }
        return mMenuInflater;
    }

    // ─── setTheme / getTheme (§3 row 62) ───────────────────────────────

    @Override
    public void setTheme(int resid) {
        mThemeResource = resid;
        super.setTheme(resid);
    }

    // ─── Lifecycle accessors (§3 row 65-67) ────────────────────────────

    public final boolean isFinishing() {
        return mFinished;
    }

    public final boolean isDestroyed() {
        return mDestroyed;
    }

    public boolean isChangingConfigurations() {
        return false;
    }

    /** §10 surprise #8: final and load-bearing for Compose. */
    public final boolean isResumed() {
        return mResumed;
    }

    // ─── finish (§3 row 68-70) ─────────────────────────────────────────

    public void finish() {
        mFinished = true;
        // TODO Step 8: route through M4a finishActivity(token) binder.
    }

    public void finishAffinity() {
        finish();
    }

    public void finishAfterTransition() {
        finish();
    }

    // ─── Activity result (lifecycle hook; user-overridable) ────────────

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    // ─── Identity / metadata (§3 row 72-80) ────────────────────────────

    public int getTaskId() {
        return mIdent;
    }

    public boolean isTaskRoot() {
        return true;
    }

    public String getLocalClassName() {
        if (mComponent != null) {
            String cls = mComponent.getClassName();
            String pkg = mComponent.getPackageName();
            if (cls != null && pkg != null && cls.startsWith(pkg + ".")) {
                return cls.substring(pkg.length() + 1);
            }
            return cls;
        }
        return getClass().getName();
    }

    public ComponentName getComponentName() {
        return mComponent;
    }

    public SharedPreferences getPreferences(int mode) {
        return getSharedPreferences(getLocalClassName(), mode);
    }

    @Override
    public Object getSystemService(String name) {
        // Delegate to the wrapped Context (mBase = WestlakeContextImpl)
        // via super.getSystemService — that path already routes through
        // SystemServiceWrapperRegistry for the M4 binder-backed managers.
        Object svc = super.getSystemService(name);
        if (svc != null) return svc;
        // Process-local fall-throughs (LayoutInflater is contextual).
        return SystemServiceWrapperRegistry.wrapProcessLocal(name, this);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        onTitleChanged(title, mTitleColor);
    }

    public void setTitle(int titleId) {
        try {
            setTitle(getText(titleId));
        } catch (Throwable t) {
            // Resource lookup may fail in V2's stub Resources path; keep silent.
        }
    }

    public final CharSequence getTitle() {
        return mTitle;
    }

    public final int getTitleColor() {
        return mTitleColor;
    }

    // ─── runOnUiThread (§3 row 82, surprise #3 final) ──────────────────

    public final void runOnUiThread(Runnable action) {
        if (action == null) return;
        Looper main = Looper.getMainLooper();
        if (main != null && Thread.currentThread() == main.getThread()) {
            action.run();
            return;
        }
        if (main != null) {
            new Handler(main).post(action);
        } else {
            // No main looper bound; run synchronously rather than drop.
            action.run();
        }
    }

    // ─── getApplicationContext (§2.2) ──────────────────────────────────

    @Override
    public Context getApplicationContext() {
        if (mApplication != null) return mApplication;
        return super.getApplicationContext();
    }

    // ─── startActivity (§3 row 63/64): minimal Implement, route via super.
    //     Multi-Activity is V2-Step12 (§2.8); for now we accept the call. ──

    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        // V2 §8.4 first-render apps are single-Activity; this fires only on
        // splash→main and similar. Delegate to Context.startActivity which
        // routes through the framework / binder. The result is a discovery
        // boundary if M4a doesn't handle it yet.
        super.startActivity(intent, options);
    }

    // ─── onTitleChanged: user-override hook (called from setTitle) ──────

    protected void onTitleChanged(CharSequence title, int color) {
    }

    // ─── No-op safe-constant returns (§5 final 14) ──────────────────────

    public boolean isImmersive() {
        return false;
    }

    public boolean isVoiceInteraction() {
        return false;
    }

    public boolean isVoiceInteractionRoot() {
        return false;
    }

    public boolean isLocalVoiceInteractionSupported() {
        return false;
    }

    public boolean isInMultiWindowMode() {
        return false;
    }

    public boolean isInPictureInPictureMode() {
        return false;
    }

    public int getMaxNumPictureInPictureActions() {
        return 0;
    }

    public boolean isActivityTransitionRunning() {
        return false;
    }

    public boolean canStartActivityForResult() {
        return true;
    }

    public Uri onProvideReferrer() {
        return null;
    }

    public Uri getReferrer() {
        return null;
    }

    public String getCallingPackage() {
        return null;
    }

    public ComponentName getCallingActivity() {
        return null;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    // ─── fail-loud bucket (§4, 178 methods) ────────────────────────────
    // Each throws ServiceMethodMissing.fail("Activity", "<name>"). The throw
    // IS the discovery signal: first real-world call promotes to Implement.

    // §4.1 Autofill
    public void requestPermissions(String[] permissions, int requestCode) { throw ServiceMethodMissing.fail(FAIL_TAG, "requestPermissions"); }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { throw ServiceMethodMissing.fail(FAIL_TAG, "onRequestPermissionsResult"); }
    public boolean shouldShowRequestPermissionRationale(String permission) { throw ServiceMethodMissing.fail(FAIL_TAG, "shouldShowRequestPermissionRationale"); }

    // §4.2 Picture-in-picture
    // CR32: AOSP defaults are "left deliberately empty" / delegate to the no-arg overload.
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        // AOSP: empty + delegate to overload.
        onPictureInPictureModeChanged(isInPictureInPictureMode);
    }
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) { /* AOSP: empty */ }
    public boolean enterPictureInPictureMode() { throw ServiceMethodMissing.fail(FAIL_TAG, "enterPictureInPictureMode"); }
    public boolean enterPictureInPictureMode(android.app.PictureInPictureParams params) { throw ServiceMethodMissing.fail(FAIL_TAG, "enterPictureInPictureMode"); }
    public void setPictureInPictureParams(android.app.PictureInPictureParams params) { throw ServiceMethodMissing.fail(FAIL_TAG, "setPictureInPictureParams"); }
    public void onPictureInPictureRequested() { throw ServiceMethodMissing.fail(FAIL_TAG, "onPictureInPictureRequested"); }

    // §4.3 Multi-window
    // CR32: AOSP defaults are "left deliberately empty" / delegate to the no-arg overload.
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        // AOSP: empty + delegate to overload.
        onMultiWindowModeChanged(isInMultiWindowMode);
    }
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) { /* AOSP: empty */ }

    // §4.4 Voice / assist
    public android.app.VoiceInteractor getVoiceInteractor() { throw ServiceMethodMissing.fail(FAIL_TAG, "getVoiceInteractor"); }
    public void startLocalVoiceInteraction(Bundle privateOptions) { throw ServiceMethodMissing.fail(FAIL_TAG, "startLocalVoiceInteraction"); }
    public void onLocalVoiceInteractionStarted() { throw ServiceMethodMissing.fail(FAIL_TAG, "onLocalVoiceInteractionStarted"); }
    public void onLocalVoiceInteractionStopped() { throw ServiceMethodMissing.fail(FAIL_TAG, "onLocalVoiceInteractionStopped"); }
    public void stopLocalVoiceInteraction() { throw ServiceMethodMissing.fail(FAIL_TAG, "stopLocalVoiceInteraction"); }
    // CR32: AOSP defaults.
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) { return false; }
    public CharSequence onCreateDescription() { return null; }
    public void onProvideAssistData(Bundle data) { /* AOSP: empty */ }
    public void onProvideAssistContent(android.app.assist.AssistContent outContent) { /* AOSP: empty */ }
    public void showAssist(Bundle args) { throw ServiceMethodMissing.fail(FAIL_TAG, "showAssist"); }

    // §4.5 Direct actions / keyboard shortcuts
    public void onGetDirectActions(android.os.CancellationSignal cancellationSignal, java.util.function.Consumer<List<android.app.DirectAction>> callback) { throw ServiceMethodMissing.fail(FAIL_TAG, "onGetDirectActions"); }
    public void onPerformDirectAction(String actionId, Bundle arguments, android.os.CancellationSignal cancellationSignal, java.util.function.Consumer<Bundle> resultListener) { throw ServiceMethodMissing.fail(FAIL_TAG, "onPerformDirectAction"); }
    public final void requestShowKeyboardShortcuts() { throw ServiceMethodMissing.fail(FAIL_TAG, "requestShowKeyboardShortcuts"); }
    public final void dismissKeyboardShortcutsHelper() { throw ServiceMethodMissing.fail(FAIL_TAG, "dismissKeyboardShortcutsHelper"); }
    public void onProvideKeyboardShortcuts(List<android.view.KeyboardShortcutGroup> data, Menu menu, int deviceId) { throw ServiceMethodMissing.fail(FAIL_TAG, "onProvideKeyboardShortcuts"); }

    // §4.7 startActivityForResult / startActivities / startIntentSender family
    public void startActivityForResult(Intent intent, int requestCode) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivityForResult"); }
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivityForResult"); }
    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) { throw ServiceMethodMissing.fail(FAIL_TAG, "startIntentSenderForResult"); }
    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) { throw ServiceMethodMissing.fail(FAIL_TAG, "startIntentSenderForResult"); }
    public void startActivities(Intent[] intents) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivities"); }
    public void startActivities(Intent[] intents, Bundle options) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivities"); }
    @Override public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) { throw ServiceMethodMissing.fail(FAIL_TAG, "startIntentSender"); }
    @Override public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) { throw ServiceMethodMissing.fail(FAIL_TAG, "startIntentSender"); }
    public boolean startActivityIfNeeded(Intent intent, int requestCode) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivityIfNeeded"); }
    public boolean startActivityIfNeeded(Intent intent, int requestCode, Bundle options) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivityIfNeeded"); }
    public boolean startNextMatchingActivity(Intent intent) { throw ServiceMethodMissing.fail(FAIL_TAG, "startNextMatchingActivity"); }
    public boolean startNextMatchingActivity(Intent intent, Bundle options) { throw ServiceMethodMissing.fail(FAIL_TAG, "startNextMatchingActivity"); }
    public void startActivityFromChild(Activity child, Intent intent, int requestCode) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivityFromChild"); }
    public void startActivityFromChild(Activity child, Intent intent, int requestCode, Bundle options) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivityFromChild"); }
    public void startActivityFromFragment(android.app.Fragment fragment, Intent intent, int requestCode) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivityFromFragment"); }
    public void startActivityFromFragment(android.app.Fragment fragment, Intent intent, int requestCode, Bundle options) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActivityFromFragment"); }
    public void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) { throw ServiceMethodMissing.fail(FAIL_TAG, "startIntentSenderFromChild"); }
    public void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) { throw ServiceMethodMissing.fail(FAIL_TAG, "startIntentSenderFromChild"); }
    public void overridePendingTransition(int enterAnim, int exitAnim) { throw ServiceMethodMissing.fail(FAIL_TAG, "overridePendingTransition"); }

    // §4.8 Result / pending intent
    public final void setResult(int resultCode) {
        // FINAL: must Implement (per WESTLAKE_ACTIVITY_API.md §10 surprise #4).
        // Store result; actual delivery is V2-Step12.
        mResultCode = resultCode;
        mResultData = null;
    }
    public final void setResult(int resultCode, Intent data) {
        mResultCode = resultCode;
        mResultData = data;
    }
    /** CR32: AOSP toggles mVisibleFromClient/decor; sandbox has no decor, so visibility flag only. */
    public void setVisible(boolean visible) { mVisibleFromClient = visible; }
    /** CR32: AOSP default is empty. */
    public void onActivityReenter(int resultCode, Intent data) { /* AOSP: empty */ }
    public PendingIntent createPendingResult(int requestCode, Intent data, int flags) { throw ServiceMethodMissing.fail(FAIL_TAG, "createPendingResult"); }
    public void finishActivity(int requestCode) { throw ServiceMethodMissing.fail(FAIL_TAG, "finishActivity"); }
    public void finishFromChild(Activity child) { throw ServiceMethodMissing.fail(FAIL_TAG, "finishFromChild"); }
    public void finishActivityFromChild(Activity child, int requestCode) { throw ServiceMethodMissing.fail(FAIL_TAG, "finishActivityFromChild"); }
    public void finishAndRemoveTask() { throw ServiceMethodMissing.fail(FAIL_TAG, "finishAndRemoveTask"); }
    public boolean releaseInstance() { throw ServiceMethodMissing.fail(FAIL_TAG, "releaseInstance"); }

    // §4.9 Task / orientation / display
    public void setRequestedOrientation(int requestedOrientation) {
        mRequestedOrientation = requestedOrientation;
    }
    public int getRequestedOrientation() {
        return mRequestedOrientation;
    }
    public boolean moveTaskToBack(boolean nonRoot) { throw ServiceMethodMissing.fail(FAIL_TAG, "moveTaskToBack"); }
    /** CR32: setTaskDescription is a binder call to ATM that affects the recents tile.
     *  Sandbox has no recents UI, so the descriptor is silently dropped. */
    public void setTaskDescription(Object taskDescription) { /* no-op (no recents UI) */ }
    public void recreate() { throw ServiceMethodMissing.fail(FAIL_TAG, "recreate"); }
    /** CR32: AOSP binder call to set a locus context for shortcut tracking; sandbox has no shortcut surface. */
    public void setLocusContext(android.content.LocusId locusId, Bundle bundle) { /* no-op */ }
    /** CR32: lifecycle hook called by ATM when display changes; sandbox stays on display 0. */
    public void onMovedToDisplay(int displayId, Configuration config) { /* AOSP: empty */ }
    /** CR32: backing field is always 0 in sandbox (no recreate flow yet). */
    public int getChangingConfigurations() { return 0; }
    /**
     * V2 / CR30-A: cold-start has no retained configuration instance — return null.
     * AndroidX {@code ComponentActivity.ensureViewModelStore()} calls this on every
     * Activity construction; on cold start there is no previous config to retain,
     * so {@code null} is the correct Android semantics (matches AOSP default when
     * {@code mLastNonConfigurationInstances == null}).
     */
    public Object getLastNonConfigurationInstance() { return null; }

    /**
     * V2 / CR30-A: default no-op — AOSP {@code Activity.onRetainNonConfigurationInstance()}
     * returns {@code null} by default; subclasses override to retain state across
     * configuration changes. Westlake never rotates, so the default is always correct.
     */
    public Object onRetainNonConfigurationInstance() { return null; }

    // §4.10 Cursor management (deprecated)
    public final Cursor managedQuery(Uri uri, String[] projection, String selection, String sortOrder) { throw ServiceMethodMissing.fail(FAIL_TAG, "managedQuery"); }
    public final Cursor managedQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { throw ServiceMethodMissing.fail(FAIL_TAG, "managedQuery"); }
    public void startManagingCursor(Cursor c) { throw ServiceMethodMissing.fail(FAIL_TAG, "startManagingCursor"); }
    public void stopManagingCursor(Cursor c) { throw ServiceMethodMissing.fail(FAIL_TAG, "stopManagingCursor"); }
    /** CR32: persistent task flag is a binder call to ATM; sandbox is non-persistent. */
    public void setPersistent(boolean isPersistent) { /* no-op */ }

    // §4.11 Action bar / toolbar / action mode
    /** CR32: sandbox has no decor / action bar; toolbar is silently dropped. */
    public void setActionBar(Toolbar toolbar) { /* no-op (no action bar in sandbox) */ }
    public ActionMode startActionMode(ActionMode.Callback callback) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActionMode"); }
    public ActionMode startActionMode(ActionMode.Callback callback, int type) { throw ServiceMethodMissing.fail(FAIL_TAG, "startActionMode"); }
    /** CR32: AOSP returns null when no action bar is initialized (sandbox: never). */
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) { return null; }
    /** CR32: AOSP returns null when no action bar is initialized. */
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) { return null; }
    /** CR32: AOSP body is empty (callback hook). */
    public void onActionModeStarted(ActionMode mode) { /* AOSP: empty */ }
    /** CR32: AOSP body is empty (callback hook). */
    public void onActionModeFinished(ActionMode mode) { /* AOSP: empty */ }

    // §4.12 Transition
    /** CR32: sandbox has no transition manager. */
    public TransitionManager getContentTransitionManager() { return null; }
    /** CR32: no transition pipeline -- silently drop. */
    public void setContentTransitionManager(TransitionManager tm) { /* no-op */ }
    /** CR32: no scenes in sandbox. */
    public Scene getContentScene() { return null; }
    /** CR32: no shared-element transitions in sandbox. */
    public void setEnterSharedElementCallback(SharedElementCallback callback) { /* no-op */ }
    public void setExitSharedElementCallback(SharedElementCallback callback) { /* no-op */ }
    /** CR32: no enter-transition postpone surface; AOSP just toggles a flag. */
    public void postponeEnterTransition() { /* no-op */ }
    public void startPostponedEnterTransition() { /* no-op */ }
    /** CR32: dispatch hook -- AOSP calls onEnterAnimationComplete on the decor; sandbox no-ops. */
    public void dispatchEnterAnimationComplete() { /* no-op */ }
    /** CR32: AOSP body is empty (callback hook). */
    public void onEnterAnimationComplete() { /* AOSP: empty */ }

    // §4.13 Dialogs (deprecated)
    /** CR32: AOSP deprecated default returns null. */
    protected Dialog onCreateDialog(int id) { return null; }
    /** CR32: AOSP default delegates to deprecated 1-arg overload; sandbox: null. */
    protected Dialog onCreateDialog(int id, Bundle args) { return null; }
    /** CR32: AOSP default is empty. */
    protected void onPrepareDialog(int id, Dialog dialog) { /* AOSP: empty */ }
    /** CR32: AOSP default is empty. */
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) { /* AOSP: empty */ }
    public final void showDialog(int id) { throw ServiceMethodMissing.fail(FAIL_TAG, "showDialog"); }
    public final boolean showDialog(int id, Bundle args) { throw ServiceMethodMissing.fail(FAIL_TAG, "showDialog"); }
    public final void dismissDialog(int id) { throw ServiceMethodMissing.fail(FAIL_TAG, "dismissDialog"); }
    public final void removeDialog(int id) { throw ServiceMethodMissing.fail(FAIL_TAG, "removeDialog"); }

    // §4.14 Search
    public final SearchEvent getSearchEvent() { throw ServiceMethodMissing.fail(FAIL_TAG, "getSearchEvent"); }
    public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) { throw ServiceMethodMissing.fail(FAIL_TAG, "startSearch"); }
    public void triggerSearch(String query, Bundle appSearchData) { throw ServiceMethodMissing.fail(FAIL_TAG, "triggerSearch"); }
    /** CR32: binder call to ATM that adjusts key handling; sandbox has no input pipeline. */
    public void takeKeyEvents(boolean get) { /* no-op */ }
    /** CR32: binder call to ATM that affects tap-outside dismissal; sandbox: no dialog dismissal. */
    public void setFinishOnTouchOutside(boolean finish) { /* no-op */ }
    public final void setDefaultKeyMode(int mode) {
        mDefaultKeyMode = mode;
    }

    // §4.15 Window feature flags
    public final boolean requestWindowFeature(int featureId) { throw ServiceMethodMissing.fail(FAIL_TAG, "requestWindowFeature"); }
    public final void setFeatureDrawableResource(int featureId, int resId) { throw ServiceMethodMissing.fail(FAIL_TAG, "setFeatureDrawableResource"); }
    public final void setFeatureDrawableUri(int featureId, Uri uri) { throw ServiceMethodMissing.fail(FAIL_TAG, "setFeatureDrawableUri"); }
    public final void setFeatureDrawable(int featureId, Drawable drawable) { throw ServiceMethodMissing.fail(FAIL_TAG, "setFeatureDrawable"); }
    public final void setFeatureDrawableAlpha(int featureId, int alpha) { throw ServiceMethodMissing.fail(FAIL_TAG, "setFeatureDrawableAlpha"); }
    /**
     * CR31-B: AOSP default body. Applies the requested style on the Activity's
     * current Theme. Standard Android lifecycle hook — no per-app behavior.
     * Fixes McD's setTheme cascade (Activity.setTheme -> ContextThemeWrapper.setTheme
     * -> ContextThemeWrapper.initializeTheme -> onApplyThemeResource) which was
     * fail-loud UOE before.
     */
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        if (theme != null) {
            theme.applyStyle(resid, true);
        }
    }

    // §4.16 Progress bar (deprecated)
    public final void setProgressBarVisibility(boolean visible) { throw ServiceMethodMissing.fail(FAIL_TAG, "setProgressBarVisibility"); }
    public final void setProgressBarIndeterminateVisibility(boolean visible) { throw ServiceMethodMissing.fail(FAIL_TAG, "setProgressBarIndeterminateVisibility"); }
    public final void setProgressBarIndeterminate(boolean indeterminate) { throw ServiceMethodMissing.fail(FAIL_TAG, "setProgressBarIndeterminate"); }
    public final void setProgress(int progress) { throw ServiceMethodMissing.fail(FAIL_TAG, "setProgress"); }
    public final void setSecondaryProgress(int secondaryProgress) { throw ServiceMethodMissing.fail(FAIL_TAG, "setSecondaryProgress"); }

    // §4.17 Volume / media controller
    public final void setVolumeControlStream(int streamType) { throw ServiceMethodMissing.fail(FAIL_TAG, "setVolumeControlStream"); }
    public final int getVolumeControlStream() { throw ServiceMethodMissing.fail(FAIL_TAG, "getVolumeControlStream"); }
    public final void setMediaController(android.media.session.MediaController controller) { throw ServiceMethodMissing.fail(FAIL_TAG, "setMediaController"); }
    public final android.media.session.MediaController getMediaController() { throw ServiceMethodMissing.fail(FAIL_TAG, "getMediaController"); }

    // §4.18 Inflation / dump
    /** CR32: AOSP default returns null (no fragment-tag inflation in plain Activity). */
    public View onCreateView(String name, Context context, AttributeSet attrs) { return null; }
    /** CR32: AOSP default delegates to 3-arg overload for non-fragment tags; sandbox: null. */
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) { return null; }
    /** CR32: AOSP dump prints state; sandbox writes a short marker so adb dumpsys doesn't NPE. */
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        if (writer != null) {
            writer.print(prefix);
            writer.print("WestlakeActivity ");
            writer.println(getClass().getName());
        }
    }

    // §4.19 Immersive / translucent / VR
    /** CR32: binder call to ATM that toggles immersive flag. Sandbox: silently drop. */
    public void setImmersive(boolean i) { /* no-op */ }
    /** CR32: AOSP binder call to ATM; in sandbox the activity is always considered translucent-capable. */
    public boolean setTranslucent(boolean translucent) { return true; }
    /** CR32: binder call to ATM; no opacity state in sandbox. */
    public void convertFromTranslucent() { /* no-op */ }
    public void onNewActivityOptions(ActivityOptions options) { throw ServiceMethodMissing.fail(FAIL_TAG, "onNewActivityOptions"); }
    /** CR32: AOSP binder call; deprecated; sandbox returns false (not visible behind). */
    public boolean requestVisibleBehind(boolean visible) { return false; }
    /** CR32: lifecycle hook for deprecated visible-behind; AOSP empty. */
    public void onVisibleBehindCanceled() { /* AOSP: empty */ }
    /** CR32: deprecated state probe; sandbox: false. */
    public boolean isBackgroundVisibleBehind() { return false; }
    /** CR32: lifecycle hook; AOSP empty. */
    public void onBackgroundVisibleBehindChanged(boolean visible) { /* AOSP: empty */ }
    /** CR32: binder call to ATM toggling VR mode; sandbox has no VR pipeline. */
    public void setVrModeEnabled(boolean enabled, ComponentName requestedComponent) throws PackageManager.NameNotFoundException { /* no-op */ }

    // §4.20 Navigate up
    /** CR32: AOSP default returns false when no parent activity intent. Sandbox has no parent metadata. */
    public boolean onNavigateUp() { return false; }
    /** CR32: AOSP delegates to onNavigateUp on the parent; sandbox: just delegate to our own. */
    public boolean onNavigateUpFromChild(Activity child) { return onNavigateUp(); }
    /** CR32: AOSP calls builder.addParentStack(this); sandbox has no task stack. */
    public void onCreateNavigateUpTaskStack(TaskStackBuilder builder) { /* no-op */ }
    /** CR32: AOSP default is empty (subclasses customize). */
    public void onPrepareNavigateUpTaskStack(TaskStackBuilder builder) { /* AOSP: empty */ }
    /** CR32: AOSP probes PackageManager for taskAffinity; sandbox has no affinity surface. */
    public boolean shouldUpRecreateTask(Intent targetIntent) { return false; }
    public boolean navigateUpTo(Intent upIntent) { throw ServiceMethodMissing.fail(FAIL_TAG, "navigateUpTo"); }
    public boolean navigateUpToFromChild(Activity child, Intent upIntent) { throw ServiceMethodMissing.fail(FAIL_TAG, "navigateUpToFromChild"); }
    public Intent getParentActivityIntent() { throw ServiceMethodMissing.fail(FAIL_TAG, "getParentActivityIntent"); }

    // §4.21 Drag-and-drop
    public android.view.DragAndDropPermissions requestDragAndDropPermissions(DragEvent event) { throw ServiceMethodMissing.fail(FAIL_TAG, "requestDragAndDropPermissions"); }

    // §4.23 Lock task / show-when-locked
    public void startLockTask() { throw ServiceMethodMissing.fail(FAIL_TAG, "startLockTask"); }
    public void stopLockTask() { throw ServiceMethodMissing.fail(FAIL_TAG, "stopLockTask"); }
    public void showLockTaskEscapeMessage() { throw ServiceMethodMissing.fail(FAIL_TAG, "showLockTaskEscapeMessage"); }
    /** CR32: binder call; sandbox has no lockscreen. */
    public void setShowWhenLocked(boolean showWhenLocked) { /* no-op */ }
    public void setInheritShowWhenLocked(boolean inheritShowWhenLocked) { /* no-op */ }
    public void setTurnScreenOn(boolean turnScreenOn) { /* no-op */ }

    // §4.24 Decor caption / remote animations — RemoteAnimationDefinition is hidden.
    /** CR32: sandbox has no decor caption. */
    public boolean isOverlayWithDecorCaptionEnabled() { return false; }
    public void setOverlayWithDecorCaptionEnabled(boolean enabled) { /* no-op */ }

    // §4.25 Misc lifecycle
    /** CR32: AOSP binder call to ATM marking the activity as "fully drawn" for cold-start metrics.
     *  Sandbox has no ATM cold-start tracking, so this is a no-op. */
    public void reportFullyDrawn() { /* no-op */ }
    /** CR32: LoaderManager is deprecated (use androidx). Sandbox returns null; callers fall back to androidx LoaderManager. */
    public android.app.LoaderManager getLoaderManager() { return null; }
    /** CR32: AOSP closes the options panel via mWindow.closePanel; sandbox has no panel. */
    public void closeOptionsMenu() { /* no-op */ }
    public void openOptionsMenu() { /* no-op */ }
    public void openContextMenu(View view) { /* no-op (sandbox: no context menu) */ }
    public void closeContextMenu() { /* no-op */ }
    /** CR32: AOSP invalidates the panel menu so it rebuilds on next show; sandbox has no panel. */
    public void invalidateOptionsMenu() { /* no-op */ }
    /** CR32: AOSP passes updated LayoutParams to WindowManager.updateViewLayout; sandbox has no decor. */
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) { /* no-op */ }
    /** CR32: AOSP finishes the activity when its window is dismissed; sandbox lifecycle is fully app-driven. */
    public void onWindowDismissed(boolean finishTask, boolean suppressWindowTransition) { /* no-op */ }
    public void setTitleColor(int textColor) {
        mTitleColor = textColor;
    }
    /** CR32: AOSP default is empty (subclass hook only fired when child activities exist). */
    protected void onChildTitleChanged(Activity childActivity, CharSequence title) { /* AOSP: empty */ }

    // §4.26 Key handling / input
    // CR32: AOSP defaults all return false (no consumption) for non-BACK keys. Sandbox has no
    // active key dispatch pipeline yet, so returning false is the safe Android contract.
    public boolean onKeyDown(int keyCode, KeyEvent event) { return false; }
    public boolean onKeyLongPress(int keyCode, KeyEvent event) { return false; }
    public boolean onKeyUp(int keyCode, KeyEvent event) { return false; }
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) { return false; }
    public boolean onKeyShortcut(int keyCode, KeyEvent event) { return false; }
    public boolean dispatchKeyShortcutEvent(KeyEvent event) { return false; }
    public boolean onTrackballEvent(MotionEvent event) { return false; }
    public boolean onGenericMotionEvent(MotionEvent event) { return false; }
    public boolean dispatchTrackballEvent(MotionEvent ev) { return false; }
    public boolean dispatchGenericMotionEvent(MotionEvent ev) { return false; }

    // §4.27 Panel / menu
    // CR32: AOSP defaults follow these semantics (see Activity.java 4192-4324 in AOSP-11):
    //   onCreatePanelView: returns null (no custom panel view).
    //   onCreatePanelMenu: returns onCreateOptionsMenu (true). Sandbox: false to suppress menu.
    //   onPreparePanel:    returns onPrepareOptionsMenu (true). Sandbox: true (no menu state).
    //   onMenuOpened:      returns true (proceed). Sandbox: same.
    //   onMenuItemSelected: returns false (allow default dispatch).
    //   onPanelClosed:     empty.
    public View onCreatePanelView(int featureId) { return null; }
    public boolean onCreatePanelMenu(int featureId, Menu menu) { return false; }
    public boolean onPreparePanel(int featureId, View view, Menu menu) { return true; }
    public boolean onMenuOpened(int featureId, Menu menu) { return true; }
    public boolean onMenuItemSelected(int featureId, MenuItem item) { return false; }
    public void onPanelClosed(int featureId, Menu menu) { /* AOSP: dispatch close hook; sandbox no-op */ }

    // ───────────────────────────────────────────────────────────────────
    // V1-LEGACY API SURFACE — temporary scaffolding
    // ───────────────────────────────────────────────────────────────────
    // V1 callers (MiniActivityManager, WestlakeLauncher, Fragment) still
    // depend on these helper methods. They are NOT part of the V2 Activity
    // API contract (per WESTLAKE_ACTIVITY_API.md) and will be removed when
    // V2-Step6 rewires WestlakeActivityThread and Step 8 cleans up the
    // remaining V1 fallback callers. Keeping these as thin pass-throughs
    // / no-ops keeps the build green during the V2 step ladder.

    /** V1 legacy: surface lifecycle hook. V2 replaces with M6 surface daemon. */
    public void onSurfaceCreated(long xcomponentHandle, int width, int height) {
        // V2: no in-process surface; M6 surface daemon owns the buffer.
    }

    /** V1 legacy: surface lifecycle hook. */
    public void onSurfaceDestroyed() {
    }

    /** V1 legacy: surface migration between Activities. V2 routes via M6. */
    public boolean adoptSurfaceFrom(Activity other) {
        return false;
    }

    /** V1 legacy: per-frame draw. V2 has no in-process render loop. */
    public void renderFrame() {
    }

    /** V1 legacy: render to an externally-provided Canvas (in-process). */
    public void renderFrameTo(Canvas canvas, int width, int height) {
    }

    /** V1 legacy: force re-layout on next renderFrame. V2: caller can drop. */
    public void invalidateLayout() {
    }

    /**
     * V1 legacy: platform FragmentManager accessor (deprecated in framework
     * API 28+). Per WESTLAKE_ACTIVITY_API.md §3 row 30, returns null until
     * a real app hits it (fail-loud-on-first-call discovery boundary). For
     * V1-compat with Fragment.java and MiniActivityManager.java the return
     * type stays {@code FragmentManager} (shim's platform class) and the
     * value is null; the caller paths already null-check.
     */
    public FragmentManager getFragmentManager() {
        return null;
    }
}
