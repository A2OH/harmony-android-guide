package androidx.lifecycle.viewmodel;

public final class MutableCreationExtras extends CreationExtras {
    public MutableCreationExtras() {}

    public MutableCreationExtras(CreationExtras initialExtras) {
        if (initialExtras != null) {
            b().putAll(initialExtras.b());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T a(Key<T> key) {
        return (T) b().get(key);
    }

    public <T> void set(Key<T> key, T value) {
        b().put(key, value);
    }

    public <T> void c(Key<T> key, T value) {
        set(key, value);
    }
}
