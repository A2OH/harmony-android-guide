package androidx.lifecycle;

public abstract class Lifecycle {
    public enum State {
        DESTROYED, INITIALIZED, CREATED, STARTED, RESUMED;

        public boolean isAtLeast(State state) {
            return compareTo(state) >= 0;
        }
    }

    public enum Event {
        ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY, ON_ANY;

        public State getTargetState() {
            switch (this) {
                case ON_CREATE:
                case ON_STOP:
                    return State.CREATED;
                case ON_START:
                case ON_PAUSE:
                    return State.STARTED;
                case ON_RESUME:
                    return State.RESUMED;
                case ON_DESTROY:
                    return State.DESTROYED;
                case ON_ANY:
                default:
                    return State.INITIALIZED;
            }
        }

        public static Event upTo(State state) {
            switch (state) {
                case CREATED:
                    return ON_CREATE;
                case STARTED:
                    return ON_START;
                case RESUMED:
                    return ON_RESUME;
                default:
                    return null;
            }
        }

        public static Event upFrom(State state) {
            switch (state) {
                case INITIALIZED:
                    return ON_CREATE;
                case CREATED:
                    return ON_START;
                case STARTED:
                    return ON_RESUME;
                default:
                    return null;
            }
        }

        public static Event downFrom(State state) {
            switch (state) {
                case RESUMED:
                    return ON_PAUSE;
                case STARTED:
                    return ON_STOP;
                case CREATED:
                    return ON_DESTROY;
                default:
                    return null;
            }
        }

        public static Event downTo(State state) {
            switch (state) {
                case DESTROYED:
                    return ON_DESTROY;
                case CREATED:
                    return ON_STOP;
                case STARTED:
                    return ON_PAUSE;
                default:
                    return null;
            }
        }
    }
    public abstract void addObserver(LifecycleObserver observer);
    public abstract void removeObserver(LifecycleObserver observer);
    public abstract State getCurrentState();
    // Obfuscated aliases (R8/ProGuard renames in McDonald's DEX)
    public void c(LifecycleObserver observer) { addObserver(observer); }
    public void a(LifecycleObserver observer) { addObserver(observer); }
    public void b(LifecycleObserver observer) { removeObserver(observer); }
    public State d() { return getCurrentState(); }
    public void g(LifecycleObserver observer) { removeObserver(observer); }
}
