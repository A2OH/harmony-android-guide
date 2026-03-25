package androidx.lifecycle;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public abstract class ViewModel {
    private final Map<String, Object> tags = new HashMap<>();
    private volatile boolean cleared = false;

    protected void onCleared() {}

    final void clear() {
        cleared = true;
        onCleared();
        // Close any closeable tags
        for (Object val : tags.values()) {
            if (val instanceof Closeable) {
                try { ((Closeable) val).close(); } catch (Exception e) {}
            }
        }
    }

    public <T> T getTag(String key) {
        return (T) tags.get(key);
    }

    public <T> void setTagIfAbsent(String key, T value) {
        if (!tags.containsKey(key)) {
            tags.put(key, value);
        }
    }
}
