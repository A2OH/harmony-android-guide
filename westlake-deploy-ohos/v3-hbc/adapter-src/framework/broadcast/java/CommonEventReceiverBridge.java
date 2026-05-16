/*
 * CommonEventReceiverBridge.java
 *
 * Reverse bridge: OH CommonEvent callbacks -> Android IIntentReceiver.
 *
 * When an OH CommonEvent is received by the C++ AdapterEventSubscriber,
 * it calls back through JNI to this class, which routes the event to
 * the appropriate Android IIntentReceiver.performReceive().
 *
 * Mapping:
 *   OH EventReceiveStub.NotifyEvent(CommonEventData, ordered, sticky)
 *   -> Android IIntentReceiver.performReceive(Intent, resultCode, data, extras, ordered, sticky)
 */
package adapter.broadcast;

import android.content.IIntentReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import adapter.broadcast.BroadcastEventConverter;

import java.util.concurrent.ConcurrentHashMap;

public class CommonEventReceiverBridge {

    private static final String TAG = "OH_CEReceiverBridge";

    // subscriptionId -> IIntentReceiver (set by ActivityManagerAdapter during registerReceiver)
    private static final ConcurrentHashMap<Integer, IIntentReceiver> sReceiverMap =
            new ConcurrentHashMap<>();

    /**
     * Register a receiver mapping. Called by ActivityManagerAdapter.registerReceiverWithFeature().
     */
    public static void addReceiver(int subscriptionId, IIntentReceiver receiver) {
        sReceiverMap.put(subscriptionId, receiver);
    }

    /**
     * Remove a receiver mapping. Called by ActivityManagerAdapter.unregisterReceiver().
     */
    public static IIntentReceiver removeReceiver(int subscriptionId) {
        return sReceiverMap.remove(subscriptionId);
    }

    /**
     * Find subscription ID by IIntentReceiver binder.
     * Used by unregisterReceiver() which only has the receiver reference.
     */
    public static int findSubscriptionId(IIntentReceiver receiver) {
        if (receiver == null) return -1;
        for (var entry : sReceiverMap.entrySet()) {
            if (entry.getValue().asBinder() == receiver.asBinder()) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Called from C++ (JNI) when an OH CommonEvent is received.
     * Routes the event to the matching Android IIntentReceiver.
     *
     * This method is invoked on an OH IPC thread; the IIntentReceiver.performReceive()
     * is a oneway call that posts to the app's main thread internally.
     */
    public static void onCommonEventReceived(
            int subscriptionId,
            String ohAction,
            String extrasJson,
            String uri,
            int code,
            String data,
            boolean ordered,
            boolean sticky) {

        IIntentReceiver receiver = sReceiverMap.get(subscriptionId);
        if (receiver == null) {
            Log.w(TAG, "onCommonEventReceived: no receiver for subscriptionId=" + subscriptionId);
            return;
        }

        // Convert OH event data to Android Intent
        Intent intent = BroadcastEventConverter.ohEventToIntent(ohAction, extrasJson, uri, code);
        Bundle extras = (extrasJson != null && !extrasJson.isEmpty())
                ? BroadcastEventConverter.jsonToBundle(extrasJson) : null;

        Log.d(TAG, "onCommonEventReceived: subscriptionId=" + subscriptionId
                + ", action=" + intent.getAction()
                + ", ordered=" + ordered + ", sticky=" + sticky);

        try {
            receiver.performReceive(intent, code, data, extras, ordered, sticky,
                    0 /* sendingUser */);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to deliver event to receiver: " + e.getMessage());
            // Receiver may have died; remove it
            sReceiverMap.remove(subscriptionId);
        }
    }
}
