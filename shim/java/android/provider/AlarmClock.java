package android.provider;

/**
 * Android-compatible AlarmClock provider shim.
 *
 * OH mapping: @ohos.reminderAgentManager
 * The Intent actions below correspond to implicit intents that delegate alarm
 * management to the system clock app.  On OpenHarmony, use the
 * reminderAgentManager module to set calendar/timer/alarm reminders instead.
 */
public final class AlarmClock {

    private AlarmClock() {}

    // ── Intent actions ─────────────────────────────────────────────────────────

    /** Intent action to set a new alarm. */
    public static final String ACTION_SET_ALARM =
            "android.intent.action.SET_ALARM";

    /** Intent action to dismiss an alarm that is currently ringing or snoozing. */
    public static final String ACTION_DISMISS_ALARM =
            "android.intent.action.DISMISS_ALARM";

    /** Intent action to set a countdown timer. */
    public static final String ACTION_SET_TIMER =
            "android.intent.action.SET_TIMER";

    /** Intent action to dismiss a running countdown timer. */
    public static final String ACTION_DISMISS_TIMER =
            "android.intent.action.DISMISS_TIMER";

    /** Intent action to show the list of alarms. */
    public static final String ACTION_SHOW_ALARMS =
            "android.intent.action.SHOW_ALARMS";

    /** Intent action to show the list of timers. */
    public static final String ACTION_SHOW_TIMERS =
            "android.intent.action.SHOW_TIMERS";

    // ── Intent extras ──────────────────────────────────────────────────────────

    /** int extra: hour of the alarm in 24-hour format (0–23). */
    public static final String EXTRA_HOUR = "android.intent.extra.alarm.HOUR";

    /** int extra: minutes of the alarm (0–59). */
    public static final String EXTRA_MINUTES = "android.intent.extra.alarm.MINUTES";

    /** String extra: a custom message/label for the alarm. */
    public static final String EXTRA_MESSAGE = "android.intent.extra.alarm.MESSAGE";

    /**
     * ArrayList&lt;Integer&gt; extra: days of the week on which the alarm repeats.
     * Use {@link java.util.Calendar#MONDAY} etc. as values.
     */
    public static final String EXTRA_DAYS = "android.intent.extra.alarm.DAYS";

    /**
     * String extra: URI of the ringtone to play, or
     * {@link #VALUE_RINGTONE_SILENT} for no sound.
     */
    public static final String EXTRA_RINGTONE = "android.intent.extra.alarm.RINGTONE";

    /** boolean extra: whether the alarm should vibrate. */
    public static final String EXTRA_VIBRATE = "android.intent.extra.alarm.VIBRATE";

    /**
     * boolean extra: if {@code true}, create the alarm without showing the
     * system alarm UI (requires SET_ALARM permission).
     */
    public static final String EXTRA_SKIP_UI = "android.intent.extra.alarm.SKIP_UI";

    /**
     * int extra: length of the countdown timer in seconds (used with
     * {@link #ACTION_SET_TIMER}).
     */
    public static final String EXTRA_LENGTH = "android.intent.extra.alarm.LENGTH";

    // ── Constants ──────────────────────────────────────────────────────────────

    /** Pass as {@link #EXTRA_RINGTONE} to set a silent alarm. */
    public static final String VALUE_RINGTONE_SILENT = "silent";
}
