package androidx.lifecycle;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SavedStateHandle {
    private final Map<String, Object> values;

    public SavedStateHandle() {
        this.values = new LinkedHashMap<>();
    }

    public SavedStateHandle(Map<String, Object> initialState) {
        this();
        if (initialState != null) {
            values.putAll(initialState);
        }
    }

    public boolean b(String key) {
        return values.containsKey(key);
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public Object c(String key) {
        return values.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) values.get(key);
    }

    public Set<String> h() {
        return values.keySet();
    }

    public Set<String> keys() {
        return values.keySet();
    }

    public void k(String key, Object value) {
        values.put(key, value);
    }

    public <T> void set(String key, T value) {
        values.put(key, value);
    }
}
