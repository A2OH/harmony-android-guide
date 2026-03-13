package android.os;

/**
 * Android-compatible BatteryManager shim. Returns mock/stub battery info.
 */
public class BatteryManager {
    public static final int BATTERY_STATUS_UNKNOWN = 1;
    public static final int BATTERY_STATUS_CHARGING = 2;
    public static final int BATTERY_STATUS_DISCHARGING = 3;
    public static final int BATTERY_STATUS_NOT_CHARGING = 4;
    public static final int BATTERY_STATUS_FULL = 5;

    public static final int BATTERY_HEALTH_UNKNOWN = 1;
    public static final int BATTERY_HEALTH_GOOD = 2;

    public static final int BATTERY_PROPERTY_CAPACITY = 4;
    public static final int BATTERY_PROPERTY_STATUS = 6;
    public static final int BATTERY_PROPERTY_CHARGE_COUNTER = 1;
    public static final int BATTERY_PROPERTY_CURRENT_NOW = 2;

    public int getIntProperty(int id) {
        switch (id) {
            case BATTERY_PROPERTY_CAPACITY: return 75; // mock: 75%
            case BATTERY_PROPERTY_STATUS: return BATTERY_STATUS_DISCHARGING;
            case BATTERY_PROPERTY_CHARGE_COUNTER: return 2500000; // µAh
            case BATTERY_PROPERTY_CURRENT_NOW: return -500000; // µA (negative = discharging)
            default: return Integer.MIN_VALUE;
        }
    }

    public boolean isCharging() {
        return getIntProperty(BATTERY_PROPERTY_STATUS) == BATTERY_STATUS_CHARGING;
    }

    public long computeChargeTimeRemaining() {
        return -1; // unknown
    }
}
