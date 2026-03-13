package android.transition;

/**
 * Android-compatible TransitionSet shim.
 * Holds a collection of Transition objects. Stub -- no animation on OpenHarmony.
 */
public class TransitionSet extends Transition {

    public static final int ORDERING_TOGETHER   = 0;
    public static final int ORDERING_SEQUENTIAL = 1;

    private int mOrdering = ORDERING_TOGETHER;

    public TransitionSet() {
    }

    public TransitionSet setOrdering(int ordering) {
        mOrdering = ordering;
        return this;
    }

    public int getOrdering() {
        return mOrdering;
    }

    public TransitionSet addTransition(Object transition) {
        // No-op stub; real Android adds to internal list
        return this;
    }

    public TransitionSet removeTransition(Object transition) {
        // No-op stub
        return this;
    }

    public int getTransitionCount() {
        return 0;
    }

    public Object getTransitionAt(int index) {
        return null;
    }
}
