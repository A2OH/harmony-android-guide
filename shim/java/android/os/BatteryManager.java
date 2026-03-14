package android.os;

/**
 * Android-compatible BatteryManager shim. Returns mock/stub battery info.
 * Pure Java, no OHBridge dependency. Mock values simulate a fully charged,
 * unplugged device.
 */
public class BatteryManager {

    // Battery status constants
    public static final int BATTERY_STATUS_UNKNOWN = 1;
    public static final int BATTERY_STATUS_CHARGING = 2;
    public static final int BATTERY_STATUS_DISCHARGING = 3;
    public static final int BATTERY_STATUS_NOT_CHARGING = 4;
    public static final int BATTERY_STATUS_FULL = 5;

    // Battery health constants
    public static final int BATTERY_HEALTH_UNKNOWN = 1;
    public static final int BATTERY_HEALTH_GOOD = 2;
    public static final int BATTERY_HEALTH_OVERHEAT = 3;
    public static final int BATTERY_HEALTH_DEAD = 4;
    public static final int BATTERY_HEALTH_OVER_VOLTAGE = 5;
    public static final int BATTERY_HEALTH_UNSPECIFIED_FAILURE = 6;
    public static final int BATTERY_HEALTH_COLD = 7;

    // Battery plugged constants
    public static final int BATTERY_PLUGGED_AC = 1;
    public static final int BATTERY_PLUGGED_USB = 2;
    public static final int BATTERY_PLUGGED_WIRELESS = 4;

    // Battery property constants
    public static final int BATTERY_PROPERTY_CHARGE_COUNTER = 1;
    public static final int BATTERY_PROPERTY_CURRENT_NOW = 2;
    public static final int BATTERY_PROPERTY_CURRENT_AVERAGE = 3;
    public static final int BATTERY_PROPERTY_CAPACITY = 4;
    public static final int BATTERY_PROPERTY_STATUS = 6;

    // Intent extra key constants
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_HEALTH = "health";
    public static final String EXTRA_PRESENT = "present";
    public static final String EXTRA_LEVEL = "level";
    public static final String EXTRA_SCALE = "scale";
    public static final String EXTRA_PLUGGED = "plugged";
    public static final String EXTRA_VOLTAGE = "voltage";
    public static final String EXTRA_TEMPERATURE = "temperature";
    public static final String EXTRA_TECHNOLOGY = "technology";

    /**
     * Returns the requested battery property as an int.
     * Mock values: capacity=100, status=FULL, charge_counter=2500000 uAh,
     * current_now=0 uA (not charging/discharging), current_average=0 uA.
     */
    public int getIntProperty(int id) {
        switch (id) {
            case BATTERY_PROPERTY_CAPACITY:
                return 75; // mock: 75%
            case BATTERY_PROPERTY_STATUS:
                return BATTERY_STATUS_FULL;
            case BATTERY_PROPERTY_CHARGE_COUNTER:
                return 2500000; // uAh
            case BATTERY_PROPERTY_CURRENT_NOW:
                return 0; // uA, 0 = full/idle
            case BATTERY_PROPERTY_CURRENT_AVERAGE:
                return 0; // uA
            default:
                return Integer.MIN_VALUE;
        }
    }

    /**
     * Returns the requested battery property as a long.
     * Delegates to getIntProperty for known properties.
     */
    public long getLongProperty(int id) {
        switch (id) {
            case BATTERY_PROPERTY_CHARGE_COUNTER:
                return 2500000L; // uAh
            case BATTERY_PROPERTY_CURRENT_NOW:
                return 0L;
            case BATTERY_PROPERTY_CURRENT_AVERAGE:
                return 0L;
            case BATTERY_PROPERTY_CAPACITY:
                return 100L;
            case BATTERY_PROPERTY_STATUS:
                return (long) BATTERY_STATUS_FULL;
            default:
                return Long.MIN_VALUE;
        }
    }

    /**
     * Returns whether the device is currently charging. Mock: false (full).
     */
    public boolean isCharging() {
        return false;
    }

    /**
     * Returns the estimated time remaining to fully charge, in microseconds.
     * Returns -1 if unknown or not charging.
     */
    public long computeChargeTimeRemaining() {
        return -1L;
    }
}
