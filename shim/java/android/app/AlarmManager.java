package android.app;

/**
 * Shim: android.app.AlarmManager -> @ohos.reminderAgentManager
 * Tier 2 -- composite mapping.
 *
 * Android AlarmManager fires PendingIntents at scheduled times.
 * OH uses reminderAgentManager which bundles alarm + notification + action.
 *
 * Bridge methods (via reflection on com.ohos.shim.bridge.OHBridge):
 *   reminderScheduleTimer(int, String, String, String, String) -> int
 *   reminderCancel(int)
 */
public class AlarmManager {
    // Alarm types
    public static final int RTC_WAKEUP = 0;
    public static final int RTC = 1;
    public static final int ELAPSED_REALTIME_WAKEUP = 2;
    public static final int ELAPSED_REALTIME = 3;

    private static Object callBridge(String methodName, Class<?>[] types, Object... args) {
        try {
            Class<?> c = Class.forName("com.ohos.shim.bridge.OHBridge");
            return c.getMethod(methodName, types).invoke(null, args);
        } catch (Throwable t) { return null; }
    }

    /**
     * Compute delay in seconds from now to the trigger time.
     * For ELAPSED_REALTIME types, triggerAtMillis is relative to boot;
     * for RTC types it is an absolute wall-clock time.  In either case
     * we convert to a positive delay (minimum 1 second).
     */
    private static int delaySeconds(int type, long triggerAtMillis) {
        long nowMillis;
        if (type == ELAPSED_REALTIME || type == ELAPSED_REALTIME_WAKEUP) {
            // Android uses SystemClock.elapsedRealtime(); approximate with nanoTime
            nowMillis = System.nanoTime() / 1000000L;
        } else {
            nowMillis = System.currentTimeMillis();
        }
        return Math.max(1, (int) ((triggerAtMillis - nowMillis) / 1000));
    }

    /**
     * Schedule a one-shot alarm via the OH reminder agent.
     * The returned reminder ID is stored on the PendingIntent so it can
     * be cancelled later.
     */
    private void scheduleOnce(int type, long triggerAtMillis, PendingIntent operation) {
        int delay = delaySeconds(type, triggerAtMillis);
        Object result = callBridge("reminderScheduleTimer",
                new Class<?>[]{ int.class, String.class, String.class, String.class, String.class },
                delay,
                operation.getTitle(),
                operation.getContent(),
                operation.getTargetAbility(),
                operation.getParamsJson());
        if (result instanceof Number) {
            operation.setReminderId(((Number) result).intValue());
        }
    }

    /**
     * Schedule an inexact alarm.  Mapped identically to setExact on OH.
     *
     * @param type            alarm type (RTC_WAKEUP etc.)
     * @param triggerAtMillis trigger time in millis (wall-clock or elapsed)
     * @param operation       PendingIntent to fire when alarm triggers
     */
    public void set(int type, long triggerAtMillis, PendingIntent operation) {
        scheduleOnce(type, triggerAtMillis, operation);
    }

    /**
     * Schedule an exact alarm.  Maps to reminderAgentManager.publishReminder().
     *
     * @param type            alarm type (RTC_WAKEUP etc.)
     * @param triggerAtMillis absolute trigger time in millis
     * @param operation       PendingIntent to fire when alarm triggers
     */
    public void setExact(int type, long triggerAtMillis, PendingIntent operation) {
        scheduleOnce(type, triggerAtMillis, operation);
    }

    /**
     * Schedule a repeating alarm.  The first firing happens at triggerAtMillis;
     * subsequent firings repeat every intervalMillis.
     *
     * OH reminder agent does not natively support intervals, so we encode the
     * interval in the params JSON so the bridge/runtime can re-schedule.
     */
    public void setRepeating(int type, long triggerAtMillis, long intervalMillis,
                             PendingIntent operation) {
        // Embed repeat interval into the params so the bridge layer can
        // re-arm the reminder when it fires.
        String origParams = operation.getParamsJson();
        String augmented;
        if (origParams == null || origParams.equals("{}")) {
            augmented = "{\"_repeatIntervalMs\":" + intervalMillis + "}";
        } else {
            // Insert the repeat field before the closing brace
            augmented = origParams.substring(0, origParams.length() - 1)
                    + ",\"_repeatIntervalMs\":" + intervalMillis + "}";
        }
        String saved = origParams;
        operation.setParamsJson(augmented);
        scheduleOnce(type, triggerAtMillis, operation);
        operation.setParamsJson(saved);
    }

    /**
     * Cancel a previously scheduled alarm.
     *
     * @param operation the PendingIntent whose associated reminder should be cancelled
     */
    public void cancel(PendingIntent operation) {
        if (operation != null && operation.getReminderId() >= 0) {
            callBridge("reminderCancel",
                    new Class<?>[]{ int.class },
                    operation.getReminderId());
            operation.setReminderId(-1);
        }
    }
}
