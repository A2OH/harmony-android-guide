package android.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Shim: android.content.ContentValues → OH ValuesBucket (plain object)
 * Tier 1 — ContentValues is a typed key-value map, same as OH ValuesBucket.
 * Pure Java implementation with toJson() for bridge serialization.
 */
public final class ContentValues {
    private final Map<String, Object> values;

    public ContentValues() {
        this.values = new HashMap<>();
    }

    public ContentValues(int size) {
        this.values = new HashMap<>(size);
    }

    public ContentValues(ContentValues from) {
        this.values = new HashMap<>(from.values);
    }

    public void put(String key, String value) { values.put(key, value); }
    public void put(String key, Byte value) { values.put(key, value); }
    public void put(String key, Short value) { values.put(key, value); }
    public void put(String key, Integer value) { values.put(key, value); }
    public void put(String key, Long value) { values.put(key, value); }
    public void put(String key, Float value) { values.put(key, value); }
    public void put(String key, Double value) { values.put(key, value); }
    public void put(String key, Boolean value) { values.put(key, value); }
    public void put(String key, byte[] value) { values.put(key, value); }
    public void putNull(String key) { values.put(key, null); }

    public String getAsString(String key) {
        Object v = values.get(key);
        return v != null ? v.toString() : null;
    }

    public Integer getAsInteger(String key) {
        Object v = values.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof String) return Integer.parseInt((String) v);
        return null;
    }

    public Long getAsLong(String key) {
        Object v = values.get(key);
        if (v instanceof Number) return ((Number) v).longValue();
        if (v instanceof String) return Long.parseLong((String) v);
        return null;
    }

    public Float getAsFloat(String key) {
        Object v = values.get(key);
        if (v instanceof Number) return ((Number) v).floatValue();
        if (v instanceof String) return Float.parseFloat((String) v);
        return null;
    }

    public Double getAsDouble(String key) {
        Object v = values.get(key);
        if (v instanceof Number) return ((Number) v).doubleValue();
        if (v instanceof String) return Double.parseDouble((String) v);
        return null;
    }

    public Boolean getAsBoolean(String key) {
        Object v = values.get(key);
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof String) return Boolean.parseBoolean((String) v);
        if (v instanceof Number) return ((Number) v).intValue() != 0;
        return null;
    }

    public byte[] getAsByteArray(String key) {
        Object v = values.get(key);
        return v instanceof byte[] ? (byte[]) v : null;
    }

    public void putAll(ContentValues other) {
        values.putAll(other.values);
    }

    public Byte getAsByte(String key) {
        Object v = values.get(key);
        if (v instanceof Byte) return (Byte) v;
        if (v instanceof Number) return ((Number) v).byteValue();
        if (v instanceof String) {
            try { return Byte.parseByte((String) v); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    public Short getAsShort(String key) {
        Object v = values.get(key);
        if (v instanceof Short) return (Short) v;
        if (v instanceof Number) return ((Number) v).shortValue();
        if (v instanceof String) {
            try { return Short.parseShort((String) v); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    public Object get(String key) { return values.get(key); }
    public boolean containsKey(String key) { return values.containsKey(key); }
    public void remove(String key) { values.remove(key); }
    public void clear() { values.clear(); }
    public int size() { return values.size(); }
    public Set<String> keySet() { return values.keySet(); }
    public Set<Map.Entry<String, Object>> valueSet() { return values.entrySet(); }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ContentValues)) return false;
        return values.equals(((ContentValues) object).values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ContentValues[{");
        boolean first = true;
        for (Map.Entry<String, Object> e : values.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(e.getKey()).append("=").append(e.getValue());
            first = false;
        }
        sb.append("}]");
        return sb.toString();
    }

    /**
     * Serialize to JSON for passing through the JNI bridge to OH RdbStore.insert().
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : values.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":");
            Object v = e.getValue();
            if (v == null) {
                sb.append("null");
            } else if (v instanceof String) {
                sb.append("\"").append(((String) v).replace("\\", "\\\\").replace("\"", "\\\"")).append("\"");
            } else if (v instanceof Number || v instanceof Boolean) {
                sb.append(v);
            } else {
                sb.append("\"").append(v).append("\"");
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
