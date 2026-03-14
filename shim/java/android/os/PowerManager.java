package android.os;

/**
 * Android-compatible PowerManager shim. Pure Java stub for power/wakelock management.
 * Headless environment: screen is always "on", power-save is always off.
 */
public class PowerManager {

    // Wake lock level constants
    public static final int PARTIAL_WAKE_LOCK = 1;
    public static final int SCREEN_DIM_WAKE_LOCK = 6;
    public static final int SCREEN_BRIGHT_WAKE_LOCK = 10;
    public static final int FULL_WAKE_LOCK = 26;
    public static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;

    // Wake lock flag constants
    public static final int ACQUIRE_CAUSES_WAKEUP = 0x10000000;
    public static final int ON_AFTER_RELEASE = 0x20000000;

    /**
     * Returns whether the device is in an interactive state (screen on).
     * Headless stub always returns true.
     */
    public boolean isInteractive() {
        return true;
    }

    /**
     * Returns whether the screen is on. Deprecated; use {@link #isInteractive()}.
     */
    @Deprecated
    public boolean isScreenOn() {
        return isInteractive();
    }

    /**
     * Returns whether the device is in power-save mode. Stub returns false.
     */
    public boolean isPowerSaveMode() {
        return false;
    }

    /**
     * Returns whether the device is in idle (Doze) mode. Stub returns false.
     */
    public boolean isDeviceIdleMode() {
        return false;
    }

    /**
     * Creates a new {@link WakeLock} with the given level/flags and tag.
     *
     * @param levelAndFlags combination of wake lock level and optional flags
     * @param tag           an identifying tag for the wake lock
     * @return a new WakeLock instance
     */
    public WakeLock newWakeLock(int levelAndFlags, String tag) {
        return new WakeLock(levelAndFlags, tag);
    }

    /**
     * Reboots the device. Stub logs the reason.
     */
    public void reboot(String reason) {
        System.out.println("[PowerManager] reboot requested: " + reason);
    }

    /**
     * Forces the device to go to sleep. Stub/no-op.
     *
     * @param time the time when the request was issued (uptimeMillis base)
     */
    public void goToSleep(long time) {
        // no-op in headless environment
    }

    /**
     * Forces the device to wake up. Stub/no-op.
     *
     * @param time the time when the request was issued (uptimeMillis base)
     */
    public void wakeUp(long time) {
        // no-op in headless environment
    }

    /**
     * A wake lock is a mechanism to indicate that your application needs the device
     * to stay on. Stub implementation tracks held state without actual power management.
     */
    public static class WakeLock {
        private final int mFlags;
        private final String mTag;
        private boolean mHeld;
        private int mRefCount;
        private boolean mRefCounted = true;

        WakeLock(int flags, String tag) {
            mFlags = flags;
            mTag = tag;
        }

        public void acquire() {
            if (mRefCounted) {
                mRefCount++;
            }
            mHeld = true;
        }

        public void acquire(long timeout) {
            // timeout ignored in shim
            acquire();
        }

        public void release() {
            release(0);
        }

        public void release(int flags) {
            if (mRefCounted) {
                if (mRefCount > 0) {
                    mRefCount--;
                }
                if (mRefCount == 0) {
                    mHeld = false;
                }
            } else {
                mHeld = false;
            }
        }

        public boolean isHeld() {
            return mHeld;
        }

        public void setReferenceCounted(boolean value) {
            mRefCounted = value;
        }

        public String toString() {
            return "WakeLock{" + mTag + " held=" + mHeld
                    + " flags=0x" + Integer.toHexString(mFlags) + "}";
        }
    }
}
