package android.content;

/**
 * Shim: android.content.BroadcastReceiver → @ohos.commonEventManager subscriber
 * Tier 2 — composite mapping.
 *
 * In Android, BroadcastReceivers can be registered in manifest or at runtime.
 * In OH, there are no manifest-declared receivers. All event subscription
 * is done at runtime via commonEventManager.
 *
 * This shim provides the BroadcastReceiver abstract class for apps to extend.
 * The bridge runtime maps registerReceiver() calls to commonEventManager.subscribe().
 */
public abstract class BroadcastReceiver {

    /**
     * Called when a broadcast is received. Apps override this.
     * The bridge runtime calls this when a CommonEvent is received
     * that matches the IntentFilter.
     */
    public abstract void onReceive(Object context, Object intent);

    /**
     * Called by the bridge to indicate the receiver is no longer needed.
     */
    public void onAbort() {}
}
