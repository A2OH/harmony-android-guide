package android.app;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.app.TaskStackBuilder
 * Tier 2 — builds a synthetic back-stack of Activities for use with notifications.
 *
 * OH mapping: OH does not have a direct equivalent; the back-stack is managed
 * by the router / navigation framework.  This shim retains the intent list so
 * that callers can retrieve the intents and construct OH navigation entries.
 */
public class TaskStackBuilder {

    private final List<Intent> mIntents = new ArrayList<>();

    private TaskStackBuilder() {}

    /**
     * Return a new TaskStackBuilder for launching a fresh task stack.
     *
     * @param context ignored in the shim (kept for API compatibility)
     */
    public static TaskStackBuilder create(Object context) {
        return new TaskStackBuilder();
    }

    /**
     * Add a new Intent to the task stack.  The most recently added Intent will be
     * at the top of the task stack.
     */
    public TaskStackBuilder addNextIntent(Intent nextIntent) {
        mIntents.add(nextIntent);
        return this;
    }

    /**
     * Add the activity parent chain as specified in its manifest parentActivityName
     * attribute.  In this shim this is a no-op; the context/class information is
     * unavailable at the shim layer.
     *
     * @param sourceActivityClass ignored in the shim
     */
    public TaskStackBuilder addParentStack(Class<?> sourceActivityClass) {
        // no-op stub — parent manifest chain not available in shim layer
        return this;
    }

    /**
     * @param sourceActivity ignored in the shim
     */
    public TaskStackBuilder addParentStack(Object sourceActivity) {
        // no-op stub
        return this;
    }

    /**
     * Return the number of intents currently in the task stack builder.
     */
    public int getIntentCount() {
        return mIntents.size();
    }

    /**
     * Return the intent at the specified index in the task stack.
     *
     * @param index 0-based index; 0 is the bottom (oldest) intent
     */
    public Intent editIntentAt(int index) {
        return mIntents.get(index);
    }

    /**
     * Obtain a PendingIntent for launching the task constructed by this builder so far.
     *
     * @param requestCode Private request code for the sender
     * @param flags       May be {@link PendingIntent#FLAG_ONE_SHOT},
     *                    {@link PendingIntent#FLAG_NO_CREATE},
     *                    {@link PendingIntent#FLAG_CANCEL_CURRENT},
     *                    {@link PendingIntent#FLAG_UPDATE_CURRENT}
     * @return PendingIntent stub wrapping requestCode; callers must configure OH reminders
     *         separately via the PendingIntent bridge fields.
     */
    public PendingIntent getPendingIntent(int requestCode, int flags) {
        return new PendingIntent(requestCode);
    }

    /**
     * Returns a copy of the intent list as an array.
     */
    public Intent[] getIntents() {
        return mIntents.toArray(new Intent[0]);
    }
}
