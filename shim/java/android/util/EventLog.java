package android.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Android-compatible EventLog shim. In-memory event logging stub.
 */
public class EventLog {

    public static class Event {
        private final int mTag;
        private final long mTimeNanos;
        private final Object mData;
        private final int mProcessId;
        private final int mThreadId;

        public Event(int tag, Object data) {
            mTag = tag;
            mData = data;
            mTimeNanos = System.nanoTime();
            mProcessId = 0;
            mThreadId = (int) Thread.currentThread().getId();
        }

        public int getTag() { return mTag; }
        public long getTimeNanos() { return mTimeNanos; }
        public Object getData() { return mData; }
        public int getProcessId() { return mProcessId; }
        public int getThreadId() { return mThreadId; }
    }

    private static final ArrayList<Event> sEvents = new ArrayList<>();

    public static int writeEvent(int tag, int value) {
        sEvents.add(new Event(tag, value));
        return 0;
    }

    public static int writeEvent(int tag, long value) {
        sEvents.add(new Event(tag, value));
        return 0;
    }

    public static int writeEvent(int tag, float value) {
        sEvents.add(new Event(tag, value));
        return 0;
    }

    public static int writeEvent(int tag, String str) {
        sEvents.add(new Event(tag, str));
        return 0;
    }

    public static int writeEvent(int tag, Object... list) {
        sEvents.add(new Event(tag, list));
        return 0;
    }

    public static void readEvents(int[] tags, Collection<Event> output) {
        for (Event e : sEvents) {
            for (int tag : tags) {
                if (e.getTag() == tag) {
                    output.add(e);
                    break;
                }
            }
        }
    }
}
