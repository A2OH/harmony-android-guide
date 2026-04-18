package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

public class Activity extends Context implements android.view.Window.Callback {

    /* ── Framework-managed state ── */
    Intent mIntent;
    ComponentName mComponent;
    Application mApplication;
    boolean mFinished;
    boolean mDestroyed;
    boolean mStarted;
    boolean mResumed;
    int mResultCode = RESULT_CANCELED;
    Intent mResultData;
    String mTitle;
    android.view.Window mWindow;
    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;

    public Activity() {
        mWindow = new android.view.Window(this);
        mWindow.setCallback(this);
    }

    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        Application app = getApplication();
        if (app != null) app.registerActivityLifecycleCallbacks(callback);
    }
    public void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        Application app = getApplication();
        if (app != null) app.unregisterActivityLifecycleCallbacks(callback);
    }

    public static final int DEFAULT_KEYS_DIALER = 1;
    public static final int DEFAULT_KEYS_DISABLE = 0;
    public static final int DEFAULT_KEYS_SEARCH_GLOBAL = 4;
    public static final int DEFAULT_KEYS_SEARCH_LOCAL = 3;
    public static final int DEFAULT_KEYS_SHORTCUT = 2;
    public static final int FOCUSED_STATE_SET = 0;
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_FIRST_USER = 1;
    public static final int RESULT_OK = -1;

    /* ── Lifecycle (proper Bundle signatures) ── */

    protected void onCreate(Bundle savedInstanceState) {}

    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        onCreate(savedInstanceState);
    }

    protected void onStart() {}
    protected void onResume() {}
    protected void onPause() {}
    protected void onStop() {}
    protected void onDestroy() {}
    protected void onRestart() {}

    protected void onPostCreate(Bundle savedInstanceState) {}
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        onPostCreate(savedInstanceState);
    }

    protected void onPostResume() {}

    protected void onSaveInstanceState(Bundle outState) {}
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {}
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        onRestoreInstanceState(savedInstanceState);
    }

    protected void onNewIntent(Intent intent) {}
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {}

    /* ── Core getters/setters ── */

    public Intent getIntent() {
        if (mIntent == null) mIntent = new Intent();
        return mIntent;
    }
    public void setIntent(Intent newIntent) { mIntent = newIntent; }
    public Application getApplication() { return mApplication; }
    public ComponentName getComponentName() { return mComponent; }
    public CharSequence getTitle() { return mTitle; }
    public void setTitle(int resId) { setTitle(getResources() != null ? getResources().getString(resId) : ""); }
    public void setTitle(CharSequence title) { mTitle = title != null ? title.toString() : null; }

    public void finish() {
        if (mFinished) return;
        mFinished = true;
        MiniServer.get().getActivityManager().finishActivity(this);
    }
    public boolean isFinishing() { return mFinished; }
    public boolean isDestroyed() { return mDestroyed; }

    public void setResult(int resultCode) {
        mResultCode = resultCode;
        mResultData = null;
    }
    public void setResult(int resultCode, Intent data) {
        mResultCode = resultCode;
        mResultData = data;
    }

    public void startActivity(Intent intent) {
        startActivityForResult(intent, -1, null);
    }
    public void startActivity(Intent intent, Bundle options) {
        startActivityForResult(intent, -1, options);
    }
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        MiniServer.get().getActivityManager().startActivity(this, intent, requestCode);
    }

    @Override
    public String getPackageName() {
        if (mComponent != null) {
            String pkg = mComponent.getPackageName();
            if (pkg != null && !pkg.isEmpty()) return pkg;
        }
        if (mApplication != null) {
            String appPkg = mApplication.getPackageName();
            if (appPkg != null && !appPkg.isEmpty()) return appPkg;
        }
        try {
            MiniServer server = MiniServer.get();
            if (server != null) {
                String serverPkg = server.getPackageName();
                if (serverPkg != null && !serverPkg.isEmpty()) return serverPkg;
            }
        } catch (Throwable ignored) {
        }
        String propPkg = System.getProperty("westlake.apk.package");
        if (propPkg != null && !propPkg.isEmpty()) return propPkg;
        if (HostBridge.hasHost()) {
            String hostPkg = HostBridge.getHostPackageName();
            if (hostPkg != null && !hostPkg.isEmpty()) return hostPkg;
        }
        return "";
    }

    // ── HostBridge-delegating overrides ────────────────────────────────────
    // All host-dependent calls go through HostBridge to avoid ClassLoader
    // type mismatches between shim classes and phone framework classes.

    @Override
    public android.content.pm.PackageManager getPackageManager() {
        // Return the shim's PM (which delegates to host's real PM internally via HostBridge)
        return super.getPackageManager();
    }

    @Override
    public android.content.res.Resources getResources() {
        // Use Application's Resources if available (same as real Android)
        if (mApplication != null) {
            return mApplication.getResources();
        }
        // Fallback: try to get host's Resources via HostBridge
        // We can't return the host's Resources directly (different CL),
        // but we can set up the shim Resources with host's display metrics etc.
        return super.getResources();
    }

    @Override
    public android.content.res.Resources.Theme getTheme() {
        // Try to proxy theme resolution from host
        if (HostBridge.hasHost()) {
            // For now return a shim Theme -- real theme attributes are resolved
            // via obtainStyledAttributes which delegates to HostBridge
            return super.getTheme();
        }
        return super.getTheme();
    }

    @Override
    public Object getSystemService(String name) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            return new android.view.LayoutInflater(this);
        }
        // Delegate to host's real system services for real functionality
        if (HostBridge.hasHost()) {
            Object service = HostBridge.getHostSystemService(name);
            if (service != null) return service;
        }
        return super.getSystemService(name);
    }

    @Override
    public Context getApplicationContext() {
        if (mApplication != null) return mApplication;
        // Don't delegate to host here -- the APK expects a Context from
        // the shim's CL, not the phone's Context
        return super.getApplicationContext();
    }

    @Override
    public android.content.res.TypedArray obtainStyledAttributes(android.util.AttributeSet set, int[] attrs) {
        if (HostBridge.hasHost()) {
            return HostBridge.host_obtainStyledAttributes(attrs);
        }
        return super.obtainStyledAttributes(set, attrs);
    }

    @Override
    public android.content.res.TypedArray obtainStyledAttributes(android.util.AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        if (HostBridge.hasHost()) {
            return HostBridge.host_obtainStyledAttributes(attrs);
        }
        return super.obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public android.content.res.TypedArray obtainStyledAttributes(int resId, int[] attrs) {
        if (HostBridge.hasHost()) {
            return HostBridge.host_obtainStyledAttributes(resId, attrs);
        }
        return super.obtainStyledAttributes(resId, attrs);
    }

    @Override
    public android.content.res.TypedArray obtainStyledAttributes(int[] attrs) {
        if (HostBridge.hasHost()) {
            return HostBridge.host_obtainStyledAttributes(attrs);
        }
        return super.obtainStyledAttributes(attrs);
    }

    @Override
    public android.content.pm.ApplicationInfo getApplicationInfo() {
        // Try to get real app info for the APK's package
        if (HostBridge.hasHost() && mComponent != null) {
            try {
                return getPackageManager().getApplicationInfo(
                    mComponent.getPackageName(), 0);
            } catch (android.content.pm.PackageManager.NameNotFoundException e) {
                // fall through
            }
        }
        return super.getApplicationInfo();
    }

    @Override
    public ClassLoader getClassLoader() {
        // Return the child-first ClassLoader that loaded this Activity.
        // This is critical: the APK's code must resolve android.* classes
        // to the shim's versions, not the phone's boot CL versions.
        return getClass().getClassLoader();
    }

    /* ── Surface rendering ── */
    private long mSurfaceCtx;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public void onSurfaceCreated(long xcomponentHandle, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mSurfaceCtx = com.ohos.shim.bridge.OHBridge.surfaceCreate(xcomponentHandle, width, height);
    }

    public void onSurfaceDestroyed() {
        if (mSurfaceCtx != 0) {
            com.ohos.shim.bridge.OHBridge.surfaceDestroy(mSurfaceCtx);
            mSurfaceCtx = 0;
        }
    }

    private boolean mLayoutDone = false;
    private android.view.View mLastDecorView = null;

    public void renderFrame() {
        if (mSurfaceCtx == 0 || mWindow == null) return;

        android.view.View decorView = mWindow.getDecorView();
        if (decorView == null) return;

        // Re-layout when content changed
        if (!mLayoutDone || decorView != mLastDecorView) {
            try {
                DefaultTheme.applyToViewTree(decorView);
            } catch (Throwable ignored) {
            }
            int wSpec = android.view.View.MeasureSpec.makeMeasureSpec(mSurfaceWidth, android.view.View.MeasureSpec.EXACTLY);
            int hSpec = android.view.View.MeasureSpec.makeMeasureSpec(mSurfaceHeight, android.view.View.MeasureSpec.EXACTLY);
            decorView.measure(wSpec, hSpec);
            decorView.layout(0, 0, mSurfaceWidth, mSurfaceHeight);
            // Force BottomNavigationView to bottom of screen after layout
            try {
                fixBottomNav(decorView, mSurfaceWidth, mSurfaceHeight);
            } catch (Throwable ignored) {
            }
            mLayoutDone = true;
            mLastDecorView = decorView;
        }

        long canvasHandle = com.ohos.shim.bridge.OHBridge.surfaceGetCanvas(mSurfaceCtx);
        if (canvasHandle == 0) return;

        android.graphics.Canvas canvas = new android.graphics.Canvas(canvasHandle, mSurfaceWidth, mSurfaceHeight);
        canvas.drawColor(DefaultTheme.COLOR_BG);

        // Draw splash background image if available (sent as OP_IMAGE for host decoding)
        byte[] splashImg = com.westlake.engine.WestlakeLauncher.splashImageData;
        if (splashImg != null) {
            com.ohos.shim.bridge.OHBridge.canvasDrawImage(canvasHandle, splashImg, 0, 0, mSurfaceWidth, mSurfaceHeight);
        }

        int scrollY = decorView.getScrollY();
        if (scrollY != 0) {
            canvas.save();
            canvas.translate(0, -scrollY);
        }
        decorView.draw(canvas);
        if (scrollY != 0) {
            canvas.restore();
        }

        // (test pattern removed — pipeline verified working)

        com.ohos.shim.bridge.OHBridge.surfaceFlush(mSurfaceCtx);
    }

    /** Force re-layout on next renderFrame */
    public void invalidateLayout() { mLayoutDone = false; }

    /** Force BottomNavigationView to bottom of screen after layout pass */
    private void fixBottomNav(android.view.View root, int screenW, int screenH) {
        if (root == null) {
            return;
        }
        String simpleName = null;
        try {
            simpleName = root.getClass().getSimpleName();
        } catch (Throwable ignored) {
        }
        if (simpleName != null && simpleName.contains("BottomNavigationView")) {
            int navH = root.getMeasuredHeight();
            if (navH < 50) navH = 112;
            root.layout(0, screenH - navH, screenW, screenH);
            return;
        }
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                fixBottomNav(vg.getChildAt(i), screenW, screenH);
            }
        }
    }

    private void dumpViewTree(android.view.View v, String indent, int depth) {
        if (depth > 8) return;
        String name = v.getClass().getSimpleName();
        int id = v.getId();
        String idStr = (id != android.view.View.NO_ID) ? "0x" + Integer.toHexString(id) : "-";
        System.out.println("[ViewTree] " + indent + name + "(id=" + idStr
            + ") bounds=[" + v.getLeft() + "," + v.getTop() + "," + v.getRight() + "," + v.getBottom()
            + "] measured=" + v.getMeasuredWidth() + "x" + v.getMeasuredHeight()
            + " vis=" + v.getVisibility());
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                dumpViewTree(vg.getChildAt(i), indent + "  ", depth + 1);
            }
        }
    }

    /**
     * Render the View tree to an external Android Canvas (in-process mode).
     * Used when running inside the host app's process with real Skia rendering.
     */
    public void renderFrameTo(android.graphics.Canvas canvas, int width, int height) {
        if (mWindow == null) return;
        android.view.View decorView = mWindow.getDecorView();
        if (decorView == null) return;

        if (!mLayoutDone || decorView != mLastDecorView) {
            try {
                DefaultTheme.applyToViewTree(decorView);
            } catch (Throwable ignored) {
            }
            int wSpec = android.view.View.MeasureSpec.makeMeasureSpec(width, android.view.View.MeasureSpec.EXACTLY);
            int hSpec = android.view.View.MeasureSpec.makeMeasureSpec(height, android.view.View.MeasureSpec.EXACTLY);
            decorView.measure(wSpec, hSpec);
            decorView.layout(0, 0, width, height);
            mLayoutDone = true;
            mLastDecorView = decorView;
        }

        canvas.drawColor(DefaultTheme.COLOR_BG);
        int scrollY = decorView.getScrollY();
        if (scrollY != 0) {
            canvas.save();
            canvas.translate(0, -scrollY);
        }
        decorView.draw(canvas);
        if (scrollY != 0) {
            canvas.restore();
        }
    }

    /* ── Input dispatch ── */

    public boolean dispatchTouchEvent(android.view.MotionEvent event) {
        if (mWindow != null) {
            android.view.View decor = mWindow.getDecorView();
            if (decor != null) {
                return decor.dispatchTouchEvent(event);
            }
        }
        return false;
    }

    public boolean dispatchKeyEvent(android.view.KeyEvent event) {
        if (mWindow != null) {
            android.view.View decor = mWindow.getDecorView();
            if (decor != null && decor.dispatchKeyEvent(event)) {
                return true;
            }
        }
        // BACK key fallback — fire onBackPressed on ACTION_UP
        if (event.getAction() == android.view.KeyEvent.ACTION_UP
                && event.getKeyCode() == android.view.KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }

    /* ── Fragment support ── */

    public FragmentManager getFragmentManager() {
        if (mFragmentManager == null) {
            mFragmentManager = new FragmentManager();
            mFragmentManager.setHost(this);
        }
        return mFragmentManager;
    }

    /* ── Remaining stubs ── */

    public void addContentView(Object p0, Object p1) {}
    public void closeContextMenu() {}
    public void closeOptionsMenu() {}
    public Object createPendingResult(Object p0, Object p1, Object p2) { return null; }
    public void dismissKeyboardShortcutsHelper() {}
    public boolean dispatchGenericMotionEvent(Object p0) { return false; }
    public boolean dispatchKeyEvent(Object p0) {
        if (p0 instanceof android.view.KeyEvent) return dispatchKeyEvent((android.view.KeyEvent) p0);
        return false;
    }
    public boolean dispatchKeyShortcutEvent(Object p0) { return false; }
    public boolean dispatchPopulateAccessibilityEvent(Object p0) { return false; }
    public boolean dispatchTouchEvent(Object p0) {
        if (p0 instanceof android.view.MotionEvent) return dispatchTouchEvent((android.view.MotionEvent) p0);
        return false;
    }
    public boolean dispatchTrackballEvent(android.view.MotionEvent event) { return false; }
    public boolean dispatchTrackballEvent(Object p0) { return false; }
    public void dump(Object p0, Object p1, Object p2, Object p3) {}
    public boolean enterPictureInPictureMode(Object p0) { return false; }
    public android.view.View findViewById(int id) {
        android.view.View v = mWindow != null ? (android.view.View) mWindow.findViewById(id) : null;
        // Return null for missing views — WestlakeInstrumentation catches NPE/ClassCastException
        return v;
    }
    public Object findViewById(Object p0) {
        if (p0 instanceof Integer) return findViewById(((Integer) p0).intValue());
        return null;
    }
    public void finishActivity(Object p0) {}
    public void finishAffinity() {}
    public void finishAfterTransition() {}
    public void finishAndRemoveTask() {}
    public int getChangingConfigurations() { return 0; }
    public Object getContentScene() { return null; }
    public Object getContentTransitionManager() { return null; }
    public int getMaxNumPictureInPictureActions() { return 0; }
    public Object getMediaController() { return null; }
    public ActionBar getActionBar() {
        if (mActionBar == null) {
            mActionBar = new ActionBar();
        }
        return mActionBar;
    }
    public Object getParent() { return null; }
    public android.content.SharedPreferences getPreferences(int mode) {
        return getSharedPreferences(getClass().getSimpleName() + "_preferences", mode);
    }
    public Object getPreferences(Object p0) {
        int mode = 0;
        if (p0 instanceof Integer) mode = (Integer) p0;
        return getPreferences(mode);
    }
    public int getRequestedOrientation() { return 0; }
    public Object getSearchEvent() { return null; }
    public int getTaskId() { return 0; }
    public int getTitleColor() { return 0; }
    public Object getVoiceInteractor() { return null; }
    public int getVolumeControlStream() { return 0; }
    public android.view.Window getWindow() { return mWindow; }
    public android.view.LayoutInflater getLayoutInflater() {
        return mWindow != null ? mWindow.getLayoutInflater() : android.view.LayoutInflater.from(this);
    }
    public Object getWindowManager() {
        return getSystemService(Context.WINDOW_SERVICE);
    }
    public boolean hasWindowFocus() { return false; }
    public void invalidateOptionsMenu() {}
    public boolean isActivityTransitionRunning() { return false; }
    public boolean isChangingConfigurations() { return false; }
    public boolean isChild() { return false; }
    public boolean isImmersive() { return false; }
    public boolean isInMultiWindowMode() { return false; }
    public boolean isInPictureInPictureMode() { return false; }
    public boolean isLocalVoiceInteractionSupported() { return false; }
    public boolean isTaskRoot() { return false; }
    public boolean isVoiceInteraction() { return false; }
    public boolean isVoiceInteractionRoot() { return false; }
    public boolean moveTaskToBack(Object p0) { return false; }
    public boolean navigateUpTo(Object p0) { return false; }
    public void onActivityReenter(Object p0, Object p1) {}
    public void onAttachedToWindow() {}
    public void onBackPressed() {
        finish();
    }
    public void onChildTitleChanged(Object p0, Object p1) {}
    public void onConfigurationChanged(Object p0) {}
    public void onContentChanged() {}
    public boolean onContextItemSelected(Object p0) { return false; }
    public void onContextMenuClosed(Object p0) {}
    public void onCreateContextMenu(Object p0, Object p1, Object p2) {}
    public void onCreateNavigateUpTaskStack(Object p0) {}
    public boolean onCreateOptionsMenu(Object p0) { return false; }
    public android.view.View onCreatePanelView(int featureId) { return null; }
    public boolean onCreatePanelMenu(int featureId, android.view.Menu menu) { return false; }
    public boolean onPreparePanel(int featureId, android.view.View view, android.view.Menu menu) { return false; }
    public boolean onMenuOpened(int featureId, android.view.Menu menu) { return false; }
    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) { return false; }
    public void onPanelClosed(int featureId, android.view.Menu menu) {}
    public boolean onCreatePanelMenu(Object p0, Object p1) { return false; }
    public void onDetachedFromWindow() {}
    public void onEnterAnimationComplete() {}
    public boolean onGenericMotionEvent(Object p0) { return false; }
    public void onGetDirectActions(Object p0, Object p1) {}
    public boolean onKeyDown(Object p0, Object p1) { return false; }
    public boolean onKeyLongPress(Object p0, Object p1) { return false; }
    public boolean onKeyMultiple(Object p0, Object p1, Object p2) { return false; }
    public boolean onKeyShortcut(Object p0, Object p1) { return false; }
    public boolean onKeyUp(Object p0, Object p1) { return false; }
    public void onLocalVoiceInteractionStarted() {}
    public void onLocalVoiceInteractionStopped() {}
    public void onLowMemory() {}
    public boolean onMenuItemSelected(Object p0, Object p1) { return false; }
    public boolean onMenuOpened(Object p0, Object p1) { return false; }
    public void onMultiWindowModeChanged(Object p0, Object p1) {}
    public boolean onNavigateUp() { return false; }
    public boolean onOptionsItemSelected(Object p0) { return false; }
    public void onOptionsMenuClosed(Object p0) {}
    public void onPanelClosed(Object p0, Object p1) {}
    public void onPerformDirectAction(Object p0, Object p1, Object p2, Object p3) {}
    public void onPictureInPictureModeChanged(Object p0, Object p1) {}
    public boolean onPictureInPictureRequested() { return false; }
    public void onPrepareNavigateUpTaskStack(Object p0) {}
    public boolean onPrepareOptionsMenu(Object p0) { return false; }
    public boolean onPreparePanel(Object p0, Object p1, Object p2) { return false; }
    public void onProvideAssistContent(Object p0) {}
    public void onProvideAssistData(Object p0) {}
    public Object onProvideReferrer() { return null; }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {}
    public void onRequestPermissionsResult(Object p0, Object p1, Object p2) {}
    public Object onRetainNonConfigurationInstance() { return null; }
    public Object getLastNonConfigurationInstance() { return null; }
    public boolean onSearchRequested(Object p0) { return false; }
    public boolean onSearchRequested() { return false; }
    public void onTitleChanged(Object p0, Object p1) {}
    public void onTopResumedActivityChanged(Object p0) {}
    public boolean onTouchEvent(Object p0) { return false; }
    public boolean onTrackballEvent(Object p0) { return false; }
    public void onTrimMemory(Object p0) {}
    public void onUserInteraction() {}
    public void onUserLeaveHint() {}
    public void onWindowAttributesChanged(android.view.WindowManager.LayoutParams attrs) {}
    public void onWindowAttributesChanged(Object p0) {}
    public void onWindowFocusChanged(boolean hasFocus) {}
    public void onWindowFocusChanged(Object p0) {}
    public void openContextMenu(Object p0) {}
    public void openOptionsMenu() {}
    public void overridePendingTransition(Object p0, Object p1) {}
    public void postponeEnterTransition() {}
    public void recreate() {}
    public void registerActivityLifecycleCallbacks(Object p0) {}
    public void registerForContextMenu(Object p0) {}
    public boolean releaseInstance() { return false; }
    public void reportFullyDrawn() {}
    public Object requestDragAndDropPermissions(Object p0) { return null; }
    public void requestPermissions(String[] permissions, int requestCode) {
        // Auto-grant all permissions for the engine
        int[] grantResults = new int[permissions.length];
        java.util.Arrays.fill(grantResults, android.content.pm.PackageManager.PERMISSION_GRANTED);
        onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void requestPermissions(Object p0, Object p1) {}
    public void requestShowKeyboardShortcuts() {}
    public boolean requestWindowFeature(int featureId) {
        return mWindow != null ? mWindow.requestFeature(featureId) : false;
    }
    public boolean requestWindowFeature(Object p0) {
        if (p0 instanceof Integer) return requestWindowFeature(((Integer) p0).intValue());
        return false;
    }
    public void runOnUiThread(Runnable action) {
        action.run(); // Synchronous in shim — no separate UI thread
    }
    public void runOnUiThread(Object p0) {
        if (p0 instanceof Runnable) runOnUiThread((Runnable) p0);
    }
    public void setActionBar(Object p0) {}
    public void setContentTransitionManager(Object p0) {}
    public void setContentView(android.view.View view) {
        if (mWindow != null) mWindow.setContentView(view);
        mLayoutDone = false; // force re-layout
    }
    public void setContentView(int layoutResID) {
        if (mWindow != null) mWindow.setContentView(layoutResID);
    }
    public void setContentView(Object p0) {
        if (p0 instanceof android.view.View) setContentView((android.view.View) p0);
        else if (p0 instanceof Integer) setContentView(((Integer) p0).intValue());
    }
    public void setContentView(Object p0, Object p1) {
        if (p0 instanceof android.view.View) {
            if (mWindow != null) mWindow.setContentView((android.view.View) p0, p1);
        }
    }
    public void setDefaultKeyMode(Object p0) {}
    public void setEnterSharedElementCallback(Object p0) {}
    public void setExitSharedElementCallback(Object p0) {}
    public void setFeatureDrawable(Object p0, Object p1) {}
    public void setFeatureDrawableAlpha(Object p0, Object p1) {}
    public void setFeatureDrawableResource(Object p0, Object p1) {}
    public void setFeatureDrawableUri(Object p0, Object p1) {}
    public void setFinishOnTouchOutside(Object p0) {}
    public void setImmersive(Object p0) {}
    public void setInheritShowWhenLocked(Object p0) {}
    public void setLocusContext(Object p0, Object p1) {}
    public void setMediaController(Object p0) {}
    public void setPictureInPictureParams(Object p0) {}
    public void setRequestedOrientation(Object p0) {}
    public void setShowWhenLocked(Object p0) {}
    public void setTaskDescription(Object p0) {}
    public boolean setTranslucent(Object p0) { return false; }
    public void setTurnScreenOn(Object p0) {}
    public void setVisible(Object p0) {}
    public void setVolumeControlStream(int streamType) {}
    public void setVolumeControlStream(Object p0) {}
    public void setVrModeEnabled(Object p0, Object p1) {}
    public boolean shouldShowRequestPermissionRationale(Object p0) { return false; }
    public boolean shouldUpRecreateTask(Object p0) { return false; }
    public boolean showAssist(Object p0) { return false; }
    public void showLockTaskEscapeMessage() {}
    public boolean startActivityIfNeeded(Object p0, Object p1) { return false; }
    public boolean startActivityIfNeeded(Object p0, Object p1, Object p2) { return false; }
    public void startIntentSenderForResult(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {}
    public void startIntentSenderForResult(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {}
    public void startLocalVoiceInteraction(Object p0) {}
    public void startLockTask() {}
    public boolean startNextMatchingActivity(Object p0) { return false; }
    public boolean startNextMatchingActivity(Object p0, Object p1) { return false; }
    public void startPostponedEnterTransition() {}
    public void startSearch(Object p0, Object p1, Object p2, Object p3) {}
    public void stopLocalVoiceInteraction() {}
    public void stopLockTask() {}
    public void takeKeyEvents(Object p0) {}
    public void triggerSearch(Object p0, Object p1) {}
    public void unregisterActivityLifecycleCallbacks(Object p0) {}
    public void unregisterForContextMenu(Object p0) {}
}
