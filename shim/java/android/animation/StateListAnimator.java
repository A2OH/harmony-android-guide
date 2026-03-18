package android.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Android-compatible StateListAnimator shim.
 * Associates Animator instances with view state sets. When the attached view's
 * state changes, the matching Animator is looked up and would be started.
 * This shim stores state-animator pairs but performs no actual animation scheduling.
 */
public class StateListAnimator implements Cloneable {

    private static final class StateSpec {
        final int[] specs;
        final Animator animator;

        StateSpec(int[] specs, Animator animator) {
            this.specs    = Arrays.copyOf(specs, specs.length);
            this.animator = animator;
        }
    }

    // Not declared final so that clone() can assign a fresh list without reflection.
    private List<StateSpec> mStateSpecs = new ArrayList<>();

    /** The Animator that is currently running (if any). Null in this shim. */
    private Animator mRunningAnimator;

    public StateListAnimator() {}

    public void setTarget(android.view.View view) {}

    /**
     * Associates an Animator with a set of view state attributes.
     *
     * @param specs    The state set (e.g. {@code android.R.attr.state_pressed}).
     * @param animator The Animator to run when the view enters this state.
     */
    public void addState(int[] specs, Animator animator) {
        if (specs == null) throw new IllegalArgumentException("specs must not be null");
        if (animator == null) throw new IllegalArgumentException("animator must not be null");
        mStateSpecs.add(new StateSpec(specs, animator));
    }

    /**
     * Removes all state-animator associations.
     */
    public void clearStates() {
        mStateSpecs.clear();
    }

    /**
     * Returns the Animator associated with the first matching state set,
     * or {@code null} if no state set matches.
     *
     * @param stateSet The current view state set.
     */
    public Animator getRunningAnimator() {
        return mRunningAnimator;
    }

    /**
     * Jumps the currently running animator (if any) to its end value.
     * No-op in this shim.
     */
    public void jumpToCurrentState() {
        // no-op shim
    }

    /**
     * Called when the owning view's state changes.
     * Finds the first matching state spec and stores the associated animator.
     * No actual animation is started in this shim.
     *
     * @param newState The new view state set.
     */
    public void setState(int[] newState) {
        mRunningAnimator = null;
        for (StateSpec spec : mStateSpecs) {
            if (stateSetMatches(spec.specs, newState)) {
                mRunningAnimator = spec.animator;
                break;
            }
        }
    }

    /**
     * Returns true if all states in {@code stateSpec} are present in {@code stateSet}.
     * Negative values in stateSpec indicate "must NOT be set".
     */
    private static boolean stateSetMatches(int[] stateSpec, int[] stateSet) {
        if (stateSet == null) return stateSpec.length == 0;
        for (int spec : stateSpec) {
            boolean found = false;
            if (spec > 0) {
                for (int s : stateSet) {
                    if (s == spec) { found = true; break; }
                }
                if (!found) return false;
            } else {
                int abs = -spec;
                for (int s : stateSet) {
                    if (s == abs) return false;
                }
            }
        }
        return true;
    }

    @Override
    public StateListAnimator clone() {
        try {
            StateListAnimator clone = (StateListAnimator) super.clone();
            // super.clone() shallow-copies the mStateSpecs reference; replace it with a
            // deep copy so the two instances do not share state.
            List<StateSpec> newList = new ArrayList<>(mStateSpecs.size());
            for (StateSpec spec : mStateSpecs) {
                newList.add(new StateSpec(spec.specs, spec.animator.clone()));
            }
            clone.mStateSpecs = newList;
            clone.mRunningAnimator = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("StateListAnimator must be cloneable", e);
        }
    }
}
