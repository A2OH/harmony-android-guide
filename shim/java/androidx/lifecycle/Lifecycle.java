package androidx.lifecycle;

/**
 * Stub Lifecycle class — the real one is in compose.dex.
 * This stub exists only so Activity.java can reference Lifecycle without compose.dex at compile time.
 * At runtime, the compose.dex version takes priority via child-first classloader.
 */
public abstract class Lifecycle {

    public abstract void addObserver(Object observer);
    public abstract void removeObserver(Object observer);
    public abstract State getCurrentState();

    public enum State {
        DESTROYED,
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED;

        public boolean isAtLeast(State state) {
            return compareTo(state) >= 0;
        }
    }

    public enum Event {
        ON_CREATE,
        ON_START,
        ON_RESUME,
        ON_PAUSE,
        ON_STOP,
        ON_DESTROY,
        ON_ANY;

        public static Event downFrom(State state) {
            switch (state) {
                case CREATED: return ON_DESTROY;
                case STARTED: return ON_STOP;
                case RESUMED: return ON_PAUSE;
                default: return null;
            }
        }

        public static Event upFrom(State state) {
            switch (state) {
                case INITIALIZED: return ON_CREATE;
                case CREATED: return ON_START;
                case STARTED: return ON_RESUME;
                default: return null;
            }
        }

        public State getTargetState() {
            switch (this) {
                case ON_CREATE: case ON_STOP: return State.CREATED;
                case ON_START: case ON_PAUSE: return State.STARTED;
                case ON_RESUME: return State.RESUMED;
                case ON_DESTROY: return State.DESTROYED;
                default: return State.DESTROYED;
            }
        }
    }
}
