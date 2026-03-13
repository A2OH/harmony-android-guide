package android.database;

import android.net.Uri;
import android.os.Handler;

/**
 * Android-compatible ContentObserver shim. Pure Java stub.
 * Receives notifications for content URI changes.
 */
public abstract class ContentObserver {
    private final Handler mHandler;

    public ContentObserver(Handler handler) {
        mHandler = handler;
    }

    /**
     * Returns true if this observer is interested in notifications for
     * descendants of the given URI as well as the URI itself.
     */
    public boolean deliverSelfNotifications() {
        return false;
    }

    /**
     * Called when a content change is detected. Override in subclass.
     */
    public void onChange(boolean selfChange) {
        // stub — no-op
    }

    /**
     * Called when a content change is detected, with the changed URI.
     */
    public void onChange(boolean selfChange, Uri uri) {
        onChange(selfChange);
    }

    /**
     * Dispatches a change notification. Calls onChange on the handler thread
     * if a handler is set, otherwise calls directly.
     */
    public final void dispatchChange(boolean selfChange) {
        dispatchChange(selfChange, null);
    }

    public final void dispatchChange(boolean selfChange, Uri uri) {
        if (mHandler == null) {
            onChange(selfChange, uri);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onChange(selfChange, uri);
                }
            });
        }
    }

    public Handler getHandler() {
        return mHandler;
    }
}
