package android.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.app.Activity → OH UIAbility + page lifecycle
 * Tier 2 — composite mapping.
 *
 * In OH, Activity is split into:
 * - UIAbility (app-level lifecycle: onCreate, onForeground, onBackground, onDestroy)
 * - @Entry @Component page (UI rendering via build())
 *
 * This shim merges both into a single Activity class that Android apps expect.
 * The OH runtime bridge manages the mapping between Activity lifecycle callbacks
 * and the underlying UIAbility + page events.
 */
public class Activity extends Context {
    private Intent intent;
    private boolean isFinishing = false;

    // ── Lifecycle callbacks (called by the OH bridge runtime) ──

    protected void onCreate(Bundle savedInstanceState) {
        // Default no-op — apps override this
    }

    protected void onStart() {}
    protected void onResume() {}
    protected void onPause() {}
    protected void onStop() {}

    protected void onDestroy() {}

    protected void onRestart() {
        onStart();
    }

    protected void onNewIntent(Intent intent) {
        this.intent = intent;
    }

    protected void onSaveInstanceState(Bundle outState) {}
    protected void onRestoreInstanceState(Bundle savedInstanceState) {}

    // ── Activity result ──

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {}

    public void startActivityForResult(Intent intent, int requestCode) {
        // TODO: implement via OH ability result callback
        startActivity(intent);
    }

    public static final int RESULT_OK = -1;
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_FIRST_USER = 1;

    public void setResult(int resultCode) {}
    public void setResult(int resultCode, Intent data) {}

    // ── View / Layout ──

    /**
     * In AOSP this inflates XML and sets the view hierarchy.
     * In the shim, this signals the bridge to load the corresponding
     * ArkUI page. The mapping from R.layout.xxx to page name is
     * maintained by the bridge runtime.
     */
    public void setContentView(int layoutResID) {
        // Bridge handles this — maps layoutResID to an ArkUI page
        // The actual UI rendering is done by the View shim layer
    }

    public void setContentView(android.view.View view) {
        // Direct view setting — bridge renders via ArkUI canvas
    }

    /**
     * findViewById replacement.
     * In the shim, views are registered by ID when the layout is inflated.
     * Returns the View shim object with the matching ID.
     */
    @SuppressWarnings("unchecked")
    public <T extends android.view.View> T findViewById(int id) {
        // TODO: implement via ViewRegistry managed by layout inflater shim
        return null;
    }

    // ── Intent ──

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent newIntent) {
        this.intent = newIntent;
    }

    // ── Navigation ──

    public void finish() {
        isFinishing = true;
        OHBridge.terminateSelf();
    }

    public boolean isFinishing() {
        return isFinishing;
    }

    public void finishAffinity() {
        finish();
    }

    // ── System services shortcut ──

    public Object getSystemService(Class<?> serviceClass) {
        // Map class → service name
        if (serviceClass == NotificationManager.class) {
            return getSystemService(NOTIFICATION_SERVICE);
        }
        if (serviceClass == AlarmManager.class) {
            return getSystemService(ALARM_SERVICE);
        }
        return null;
    }

    // ── Toast shortcut ──

    public void runOnUiThread(Runnable action) {
        // In OH, all UI runs on the main thread already
        // TODO: use OH taskpool or main thread handler if needed
        action.run();
    }

    // ── Window features (stubs) ──

    public void requestWindowFeature(int featureId) {}
    public Object getWindow() { return null; }

    // ── Title ──

    public void setTitle(CharSequence title) {}
    public void setTitle(int titleId) {}

    // ── Permissions ──

    public void requestPermissions(String[] permissions, int requestCode) {
        // TODO: map to abilityAccessCtrl.requestPermissionsFromUser via bridge
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {}

    public int checkSelfPermission(String permission) {
        // TODO: map to atManager.checkAccessToken via bridge
        return 0; // PERMISSION_GRANTED
    }
}
