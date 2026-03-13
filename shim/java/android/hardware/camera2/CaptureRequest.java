package android.hardware.camera2;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shim stub for android.hardware.camera2.CaptureRequest.
 * Immutable request built via {@link Builder}.
 */
public final class CaptureRequest {

    // -----------------------------------------------------------------------
    // Key<T>
    // -----------------------------------------------------------------------

    /** Typed key identifying a single capture-request parameter. */
    public static final class Key<T> {
        private final String name;

        public Key(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // -----------------------------------------------------------------------
    // Internal state (populated by Builder)
    // -----------------------------------------------------------------------

    private final Map<Key<?>, Object> entries;

    private CaptureRequest(Map<Key<?>, Object> entries) {
        this.entries = new LinkedHashMap<>(entries);
    }

    /** Returns the value set for {@code key}, or {@code null} if not set. */
    @SuppressWarnings("unchecked")
    public <T> T get(Key<T> key) {
        return (T) entries.get(key);
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    /**
     * Mutable builder for constructing a {@link CaptureRequest}.
     */
    public static final class Builder {

        private final Map<Key<?>, Object> entries = new LinkedHashMap<>();

        /**
         * Set a capture request field to a value.  The field definitions can
         * be found in {@link CaptureRequest}.
         *
         * @param key   the {@link Key} to set
         * @param value the value to set the key to
         */
        public <T> void set(Key<T> key, T value) {
            if (key == null) {
                throw new IllegalArgumentException("key must not be null");
            }
            entries.put(key, value);
        }

        /**
         * Build a {@link CaptureRequest} from the current state of the builder.
         *
         * @return a new, immutable {@link CaptureRequest}
         */
        public CaptureRequest build() {
            return new CaptureRequest(entries);
        }
    }
}
