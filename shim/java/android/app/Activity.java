package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

public class Activity extends Context {

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

    public Activity() {
        mWindow = new android.view.Window(this);
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
        return mComponent != null ? mComponent.getPackageName() : null;
    }

    @Override
    public Context getApplicationContext() {
        return mApplication;
    }

    /**
     * Returns the Application's Resources so that resource registrations
     * on the Application context are visible to Activities (matches real Android
     * where Activity and Application share the same ResourcesImpl).
     */
    @Override
    public android.content.res.Resources getResources() {
        if (mApplication != null) {
            return mApplication.getResources();
        }
        return super.getResources();
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

    public void renderFrame() {
        if (mSurfaceCtx == 0 || mWindow == null) return;

        android.view.View decorView = mWindow.getDecorView();
        if (decorView == null) return;

        int wSpec = android.view.View.MeasureSpec.makeMeasureSpec(mSurfaceWidth, android.view.View.MeasureSpec.EXACTLY);
        int hSpec = android.view.View.MeasureSpec.makeMeasureSpec(mSurfaceHeight, android.view.View.MeasureSpec.EXACTLY);
        decorView.measure(wSpec, hSpec);
        decorView.layout(0, 0, mSurfaceWidth, mSurfaceHeight);

        long canvasHandle = com.ohos.shim.bridge.OHBridge.surfaceGetCanvas(mSurfaceCtx);
        if (canvasHandle == 0) return;

        android.graphics.Canvas canvas = new android.graphics.Canvas(canvasHandle, mSurfaceWidth, mSurfaceHeight);
        canvas.drawColor(0xFFFFFFFF);
        decorView.draw(canvas);

        com.ohos.shim.bridge.OHBridge.surfaceFlush(mSurfaceCtx);
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
    public boolean dispatchTrackballEvent(Object p0) { return false; }
    public void dump(Object p0, Object p1, Object p2, Object p3) {}
    public boolean enterPictureInPictureMode(Object p0) { return false; }
    public android.view.View findViewById(int id) {
        return mWindow != null ? (android.view.View) mWindow.findViewById(id) : null;
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
    public Object getParent() { return null; }
    public Object getPreferences(Object p0) { return null; }
    public int getRequestedOrientation() { return 0; }
    public Object getSearchEvent() { return null; }
    public int getTaskId() { return 0; }
    public int getTitleColor() { return 0; }
    public Object getVoiceInteractor() { return null; }
    public int getVolumeControlStream() { return 0; }
    public android.view.Window getWindow() { return mWindow; }
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
    public boolean onSearchRequested(Object p0) { return false; }
    public boolean onSearchRequested() { return false; }
    public void onTitleChanged(Object p0, Object p1) {}
    public void onTopResumedActivityChanged(Object p0) {}
    public boolean onTouchEvent(Object p0) { return false; }
    public boolean onTrackballEvent(Object p0) { return false; }
    public void onTrimMemory(Object p0) {}
    public void onUserInteraction() {}
    public void onUserLeaveHint() {}
    public void onWindowAttributesChanged(Object p0) {}
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
    public boolean requestWindowFeature(Object p0) { return false; }
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
