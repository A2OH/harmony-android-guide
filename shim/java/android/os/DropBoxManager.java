package android.os;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible DropBoxManager shim. In-memory log for debugging entries.
 */
public class DropBoxManager {

    public static class Entry implements java.io.Closeable {
        private final String mTag;
        private final long mTimeMillis;
        private final String mText;

        public Entry(String tag, long millis) {
            this(tag, millis, (String) null);
        }

        public Entry(String tag, long millis, String text) {
            mTag = tag;
            mTimeMillis = millis;
            mText = text;
        }

        public String getTag() { return mTag; }
        public long getTimeMillis() { return mTimeMillis; }
        public String getText(int maxBytes) {
            if (mText == null) return null;
            return mText.length() <= maxBytes ? mText : mText.substring(0, maxBytes);
        }

        @Override
        public void close() { /* no-op */ }
    }

    private final List<Entry> mEntries = new ArrayList<>();

    public void addText(String tag, String data) {
        mEntries.add(new Entry(tag, System.currentTimeMillis(), data));
    }

    public Entry getNextEntry(String tag, long msec) {
        for (Entry e : mEntries) {
            if (e.getTimeMillis() > msec && (tag == null || tag.equals(e.getTag()))) {
                return e;
            }
        }
        return null;
    }

    public boolean isTagEnabled(String tag) {
        return true;
    }
}
