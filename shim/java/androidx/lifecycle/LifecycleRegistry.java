package androidx.lifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple LifecycleRegistry implementation for the Westlake engine.
 * Tracks lifecycle state and notifies observers.
 * The compose.dex has the full version — this is a fallback/stub.
 */
public class LifecycleRegistry extends Lifecycle {

    private State currentState = State.INITIALIZED;
    private final LifecycleOwner owner;
    private final List<Object> observers = new ArrayList<>();

    public LifecycleRegistry(LifecycleOwner owner) {
        this.owner = owner;
    }

    @Override
    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State state) {
        this.currentState = state;
    }

    /**
     * Move to the given state, dispatching events as needed.
     */
    public void handleLifecycleEvent(Event event) {
        State next = event.getTargetState();
        currentState = next;
        // Notify observers (simplified — real version is more complex)
        for (Object obs : new ArrayList<>(observers)) {
            try {
                // Try calling the observer's onStateChanged method
                obs.getClass().getMethod("onStateChanged", LifecycleOwner.class, Event.class)
                    .invoke(obs, owner, event);
            } catch (Exception e) {
                // Observer might use annotations instead
            }
        }
    }

    @Override
    public void addObserver(Object observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Object observer) {
        observers.remove(observer);
    }

    public int getObserverCount() {
        return observers.size();
    }
}
