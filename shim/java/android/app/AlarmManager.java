package android.app;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.app.AlarmManager → @ohos.reminderAgentManager
 * Tier 2 — composite mapping.
 *
 * Android AlarmManager fires PendingIntents at scheduled times.
 * OH uses reminderAgentManager which bundles alarm + notification + action.
 */
public class AlarmManager {
    // Alarm types
    public static final int RTC_WAKEUP = 0;
    public static final int RTC = 1;
    public static final int ELAPSED_REALTIME_WAKEUP = 2;
    public static final int ELAPSED_REALTIME = 3;

    /**
     * Schedule an exact alarm. Maps to reminderAgentManager.publishReminder().
     *
     * @param type       alarm type (RTC_WAKEUP etc.)
     * @param triggerAtMillis absolute trigger time in millis
     * @param operation  PendingIntent to fire when alarm triggers
     */
    public void setExact(int type, long triggerAtMillis, PendingIntent operation) {
        int delaySeconds = Math.max(1, (int)((triggerAtMillis - System.currentTimeMillis()) / 1000));

        OHBridge.reminderScheduleTimer(
            delaySeconds,
            operation.getTitle(),
            operation.getContent(),
            operation.getTargetAbility(),
            operation.getParamsJson()
        );
    }

    public void set(int type, long triggerAtMillis, PendingIntent operation) {
        // set() is inexact in Android, but we map it the same way
        setExact(type, triggerAtMillis, operation);
    }

    public void setRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) {
        // TODO: OH supports repeating reminders — implement
        setExact(type, triggerAtMillis, operation);
    }

    public void cancel(PendingIntent operation) {
        if (operation.getReminderId() >= 0) {
            OHBridge.reminderCancel(operation.getReminderId());
        }
    }
}
