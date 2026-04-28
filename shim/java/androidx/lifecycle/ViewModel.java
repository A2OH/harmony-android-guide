package androidx.lifecycle;

public abstract class ViewModel {
    private final java.util.Map<String, Object> mTags = new java.util.HashMap<>();
    private final java.util.Set<java.lang.AutoCloseable> mCloseables = new java.util.HashSet<>();
    private volatile boolean mCleared;

    protected void onCleared() {}

    @SuppressWarnings("unchecked")
    public <T> T setTagIfAbsent(String key, T newValue) {
        synchronized (mTags) {
            Object existing = mTags.get(key);
            if (existing != null) {
                return (T) existing;
            }
            mTags.put(key, newValue);
            if (mCleared && newValue instanceof java.lang.AutoCloseable) {
                closeQuietly((java.lang.AutoCloseable) newValue);
            }
            return newValue;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getTag(String key) {
        synchronized (mTags) {
            return (T) mTags.get(key);
        }
    }

    public java.lang.AutoCloseable getCloseable(String key) {
        Object value = getTag(key);
        return value instanceof java.lang.AutoCloseable
                ? (java.lang.AutoCloseable) value : null;
    }

    public void addCloseable(String key, java.lang.AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        java.lang.AutoCloseable existing = getCloseable(key);
        if (existing != null) {
            closeQuietly(closeable);
            return;
        }
        setTagIfAbsent(key, closeable);
    }

    public void addCloseable(java.lang.AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        synchronized (mTags) {
            if (mCleared) {
                closeQuietly(closeable);
                return;
            }
            mCloseables.add(closeable);
        }
    }

    public void clear() {
        mCleared = true;
        synchronized (mTags) {
            for (Object value : mTags.values()) {
                if (value instanceof java.lang.AutoCloseable) {
                    closeQuietly((java.lang.AutoCloseable) value);
                }
            }
            mTags.clear();
            for (java.lang.AutoCloseable closeable : mCloseables) {
                closeQuietly(closeable);
            }
            mCloseables.clear();
        }
        onCleared();
    }

    private static void closeQuietly(java.lang.AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
