package android.app.usage;

/**
 * Android-compatible UsageEvents stub for the A2OH shim layer.
 * Provides usage event history for apps and system interactions.
 */
public final class UsageEvents {

    /**
     * An event representing a state change for a component.
     */
    public static final class Event {
        public static final int NONE = 0;
        public static final int ACTIVITY_RESUMED = 1;
        public static final int ACTIVITY_PAUSED = 2;
        public static final int ACTIVITY_STOPPED = 23;
        public static final int CONFIGURATION_CHANGE = 5;
        public static final int SCREEN_INTERACTIVE = 15;
        public static final int SCREEN_NON_INTERACTIVE = 16;

        private int mEventType;
        private String mPackageName;
        private String mClassName;
        private long mTimeStamp;

        public Event() {
            mEventType = NONE;
        }

        /** Returns the event type. */
        public int getEventType() { return mEventType; }

        /** Returns the package name of the source of this event. */
        public String getPackageName() { return mPackageName; }

        /** Returns the class name of the source of this event, may be null. */
        public String getClassName() { return mClassName; }

        /** Returns the time stamp for this event, in milliseconds since epoch. */
        public long getTimeStamp() { return mTimeStamp; }
    }

    /** Stub: no events are available. */
    public boolean hasNextEvent() {
        return false;
    }

    /** Stub: no events are available; always returns false. */
    public boolean getNextEvent(Event eventOut) {
        return false;
    }
}
