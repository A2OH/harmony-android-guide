package android.app;

import android.content.Context;
import android.content.Intent;

/**
 * Shim: android.app.Service → OH @ohos.app.ability.ServiceExtensionAbility
 * Tier 2 — lifecycle mapping.
 *
 * OH mapping:
 *   Service.onCreate()              → ServiceExtensionAbility.onCreate()
 *   Service.onStartCommand()        → ServiceExtensionAbility.onRequest()
 *   Service.onBind()                → ServiceExtensionAbility.onConnect()
 *   Service.onUnbind()              → ServiceExtensionAbility.onDisconnect()
 *   Service.onDestroy()             → ServiceExtensionAbility.onDestroy()
 *
 * This shim is a pure Java stub — lifecycle is managed by shim-framework.jar.
 * No native OHBridge calls are needed; the OH runtime invokes lifecycle
 * methods directly through the shim framework on the OHOS target.
 *
 * App code subclasses Service and overrides lifecycle methods as usual.
 */
public abstract class Service extends Context {

    // ── onStartCommand return values ──

    /** Service is re-created if killed; intent is re-delivered. */
    public static final int START_STICKY = 1;
    /** Service is re-created if killed; intent is NOT re-delivered. */
    public static final int START_NOT_STICKY = 2;
    /** Service is re-created if killed; last intent is re-delivered. */
    public static final int START_REDELIVER_INTENT = 3;
    /** Returned when startId is no longer the most-recent start. */
    public static final int START_STICKY_COMPATIBILITY = 0;

    /** Passed as startId to indicate the command was not a result of a start request. */
    public static final int START_FLAG_REDELIVERY = 0x0001;
    public static final int START_FLAG_RETRY      = 0x0002;

    // ── Internal state ──

    private boolean stopRequested = false;
    private int lastStartId = 0;

    // ── Lifecycle callbacks — called by OH bridge / shim-framework.jar ──

    /**
     * Maps to ServiceExtensionAbility.onCreate().
     * Called once when the service is first created.
     */
    public void onCreate() {
        // Default no-op — subclasses override
    }

    /**
     * Maps to ServiceExtensionAbility.onRequest().
     * Called each time the service is started via startService().
     *
     * @return START_STICKY by default; subclasses override for other policies.
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.lastStartId = startId;
        return START_STICKY;
    }

    /**
     * Maps to ServiceExtensionAbility.onConnect().
     * Called when a client binds to the service via bindService().
     * Return null for unbound / started-only services.
     */
    public android.os.IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Maps to ServiceExtensionAbility.onDisconnect().
     * Called when all clients have unbound from the service.
     * Return true to call onRebind() when new clients bind.
     */
    public boolean onUnbind(Intent intent) {
        return false;
    }

    /** Called after onUnbind() if onUnbind() returned true and new clients bind. */
    public void onRebind(Intent intent) {}

    /**
     * Maps to ServiceExtensionAbility.onDestroy().
     * Called when the service is about to be destroyed.
     */
    public void onDestroy() {
        // Default no-op — subclasses override
    }

    // ── Self-management ──

    /**
     * Stop the service unconditionally.
     * Maps to the OH ability framework terminating the ServiceExtensionAbility.
     */
    public final void stopSelf() {
        stopRequested = true;
        onDestroy();
    }

    /**
     * Stop the service if startId is the most recent start ID.
     * Returns true if the service will be stopped.
     */
    public final boolean stopSelf(int startId) {
        if (startId == lastStartId) {
            stopSelf();
            return true;
        }
        return false;
    }

    /**
     * Stop the service if startId is the most recent start ID.
     * Legacy void overload — kept for API compatibility.
     */
    public final void stopSelfResult(int startId) {
        stopSelf(startId);
    }

    // ── Foreground service ──

    /**
     * Promote this service to the foreground, showing a persistent notification.
     * Maps to OH backgroundTaskManager.startBackgroundRunning().
     *
     * @param id           Notification ID (must be > 0)
     * @param notification The notification to display while in foreground
     */
    public final void startForeground(int id, Notification notification) {
        // TODO: map to backgroundTaskManager.startBackgroundRunning via bridge
        // The notification is posted to the status bar via NotificationManager shim
    }

    /**
     * Remove this service from the foreground, optionally removing the notification.
     */
    public final void stopForeground(boolean removeNotification) {
        // TODO: map to backgroundTaskManager.stopBackgroundRunning via bridge
    }

    /** @deprecated Use {@link #stopForeground(boolean)} */
    @Deprecated
    public final void stopForeground(int flags) {
        stopForeground((flags & 1) != 0);
    }

    // ── State query ──

    /** Returns true if stopSelf() has been called. */
    public boolean isStopRequested() {
        return stopRequested;
    }
}
