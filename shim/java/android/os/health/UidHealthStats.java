package android.os.health;

/**
 * A2OH shim: UidHealthStats - timer and measurement key constants.
 */
public final class UidHealthStats {

    private UidHealthStats() {}

    // Timer keys
    public static final int TIMER_WAKELOCKS_FULL    = 1;
    public static final int TIMER_WAKELOCKS_PARTIAL  = 2;
    public static final int TIMER_GPS_SENSOR         = 3;
    public static final int TIMER_AUDIO              = 4;
    public static final int TIMER_VIDEO              = 5;
    public static final int TIMER_SCREEN_WAKELOCK    = 6;
    public static final int TIMER_JOB               = 7;
    public static final int TIMER_SYNC              = 8;
    public static final int TIMER_MOBILE_RADIO_ACTIVE = 9;
    public static final int TIMER_WIFI_FULL_LOCK     = 10;
    public static final int TIMER_WIFI_SCAN          = 11;
    public static final int TIMER_WIFI_MULTICAST     = 12;
    public static final int TIMER_WIFI_RUNNING       = 13;
    public static final int TIMER_BLUETOOTH_SCAN     = 14;
    public static final int TIMER_PROCESS_STATE_TOP_MS            = 20;
    public static final int TIMER_PROCESS_STATE_FOREGROUND_SERVICE_MS = 21;
    public static final int TIMER_PROCESS_STATE_TOP_SLEEPING_MS   = 22;
    public static final int TIMER_PROCESS_STATE_FOREGROUND_MS     = 23;
    public static final int TIMER_PROCESS_STATE_BACKGROUND_MS     = 24;
    public static final int TIMER_PROCESS_STATE_CACHED_MS         = 25;

    // Measurement keys
    public static final int MEASUREMENT_WIFI_TX_BYTES       = 10001;
    public static final int MEASUREMENT_MOBILE_TX_BYTES     = 10002;
    public static final int MEASUREMENT_WIFI_RX_BYTES       = 10003;
    public static final int MEASUREMENT_MOBILE_RX_BYTES     = 10004;
    public static final int MEASUREMENT_WIFI_TX_PACKETS     = 10005;
    public static final int MEASUREMENT_MOBILE_TX_PACKETS   = 10006;
    public static final int MEASUREMENT_WIFI_RX_PACKETS     = 10007;
    public static final int MEASUREMENT_MOBILE_RX_PACKETS   = 10008;
    public static final int MEASUREMENT_OTHER_TX_BYTES      = 10009;
    public static final int MEASUREMENT_OTHER_RX_BYTES      = 10010;
    public static final int MEASUREMENT_BUTTON_USER_ACTIVITY_COUNT = 10011;
    public static final int MEASUREMENT_TOUCH_USER_ACTIVITY_COUNT  = 10012;
    public static final int MEASUREMENT_CPU_USER_TIME_MS    = 10013;
    public static final int MEASUREMENT_CPU_SYSTEM_TIME_MS  = 10014;
    public static final int MEASUREMENT_REALTIME_BATTERY_MS = 10015;
    public static final int MEASUREMENT_UPTIME_BATTERY_MS   = 10016;
    public static final int MEASUREMENT_REALTIME_SCREEN_OFF_BATTERY_MS = 10017;
    public static final int MEASUREMENT_UPTIME_SCREEN_OFF_BATTERY_MS   = 10018;
}
