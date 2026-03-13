package android.transition;

import java.util.ArrayList;

/**
 * Android-compatible TransitionSet shim.
 * Holds a collection of Transition objects. Stub — no animation on OpenHarmony.
 */
public class TransitionSet extends Transition {

    public static final int ORDERING_TOGETHER    = 0;
    public static final int ORDERING_SEQUENTIAL  = 1;

    private int mOrdering = ORDERING_TOGETHER;
    private final ArrayList<Transition> mTransitions = new ArrayList<>();

    public TransitionSet() {
    }

    public TransitionSet setOrdering(int ordering) {
        mOrdering = ordering;
        return this;
    }

    public int getOrdering() {
        return mOrdering;
    }

    public TransitionSet addTransition(Transition transition) {
        if (transition != null) {
            mTransitions.add(transition);
        }
        return this;
    }

    public TransitionSet removeTransition(Transition transition) {
        mTransitions.remove(transition);
        return this;
    }

    public int getTransitionCount() {
        return mTransitions.size();
    }

    public Transition getTransitionAt(int index) {
        if (index < 0 || index >= mTransitions.size()) return null;
        return mTransitions.get(index);
    }
}
