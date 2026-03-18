package android.util;

public abstract class Property<T, V> {
    private final String mName;
    private final Class<V> mType;

    public Property(Class<V> type, String name) {
        mName = name;
        mType = type;
    }

    public abstract V get(T object);

    public String getName() { return mName; }
    public Class<V> getType() { return mType; }
    public boolean isReadOnly() { return false; }

    public void set(T object, V value) {}

    public static <T, V> Property<T, V> of(Class<T> hostType, Class<V> valueType, String name) {
        return null;
    }
}
