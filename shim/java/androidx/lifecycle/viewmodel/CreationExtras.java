package androidx.lifecycle.viewmodel;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CreationExtras {
    private final Map<Key<?>, Object> extras = new LinkedHashMap<>();

    public abstract <T> T a(Key<T> key);

    public final Map<Key<?>, Object> b() {
        return extras;
    }

    public interface Key<T> {}

    public static final class Empty extends CreationExtras {
        public static final Empty c = new Empty();

        private Empty() {}

        @Override
        public <T> T a(Key<T> key) {
            return null;
        }
    }
}
