package android.media;

import java.util.HashMap;

/**
 * Android-compatible MediaFormat shim. Backed by a HashMap.
 */
public class MediaFormat {
    public static final String KEY_MIME            = "mime";
    public static final String KEY_WIDTH           = "width";
    public static final String KEY_HEIGHT          = "height";
    public static final String KEY_BIT_RATE        = "bitrate";
    public static final String KEY_FRAME_RATE      = "frame-rate";
    public static final String KEY_SAMPLE_RATE     = "sample-rate";
    public static final String KEY_CHANNEL_COUNT   = "channel-count";
    public static final String KEY_AAC_PROFILE     = "aac-profile";
    public static final String KEY_DURATION        = "durationUs";
    public static final String KEY_MAX_INPUT_SIZE  = "max-input-size";
    public static final String KEY_COLOR_FORMAT    = "color-format";
    public static final String KEY_I_FRAME_INTERVAL = "i-frame-interval";

    private final HashMap<String, Object> mMap = new HashMap<>();

    public MediaFormat() {}

    // ---- static factory methods ----

    public static MediaFormat createVideoFormat(String mime, int width, int height) {
        MediaFormat fmt = new MediaFormat();
        fmt.setString(KEY_MIME, mime);
        fmt.setInteger(KEY_WIDTH, width);
        fmt.setInteger(KEY_HEIGHT, height);
        return fmt;
    }

    public static MediaFormat createAudioFormat(String mime, int sampleRate, int channelCount) {
        MediaFormat fmt = new MediaFormat();
        fmt.setString(KEY_MIME, mime);
        fmt.setInteger(KEY_SAMPLE_RATE, sampleRate);
        fmt.setInteger(KEY_CHANNEL_COUNT, channelCount);
        return fmt;
    }

    // ---- getters ----

    public String getString(String name) {
        Object v = mMap.get(name);
        return v instanceof String ? (String) v : null;
    }

    public String getString(String name, String defaultValue) {
        String v = getString(name);
        return v != null ? v : defaultValue;
    }

    public int getInteger(String name) {
        Object v = mMap.get(name);
        return v instanceof Integer ? (Integer) v : 0;
    }

    public int getInteger(String name, int defaultValue) {
        Object v = mMap.get(name);
        return v instanceof Integer ? (Integer) v : defaultValue;
    }

    public long getLong(String name) {
        Object v = mMap.get(name);
        if (v instanceof Long) return (Long) v;
        if (v instanceof Integer) return ((Integer) v).longValue();
        return 0L;
    }

    public long getLong(String name, long defaultValue) {
        Object v = mMap.get(name);
        if (v instanceof Long) return (Long) v;
        if (v instanceof Integer) return ((Integer) v).longValue();
        return defaultValue;
    }

    public float getFloat(String name) {
        Object v = mMap.get(name);
        return v instanceof Float ? (Float) v : 0f;
    }

    public float getFloat(String name, float defaultValue) {
        Object v = mMap.get(name);
        return v instanceof Float ? (Float) v : defaultValue;
    }

    public boolean containsKey(String name) {
        return mMap.containsKey(name);
    }

    // ---- setters ----

    public void setString(String name, String value) { mMap.put(name, value); }
    public void setInteger(String name, int value)   { mMap.put(name, value); }
    public void setLong(String name, long value)     { mMap.put(name, value); }
    public void setFloat(String name, float value)   { mMap.put(name, value); }

    @Override
    public String toString() {
        return "MediaFormat" + mMap.toString();
    }
}
