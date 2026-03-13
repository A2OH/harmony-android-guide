package android.animation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Android-compatible LayoutTransition shim.
 * Animates layout changes when views are added to or removed from a ViewGroup.
 * This shim stores configuration but performs no actual animation rendering.
 */
public class LayoutTransition {

    /** Transition type: animation run on items that are changing due to a new item appearing. */
    public static final int CHANGE_APPEARING    = 0;
    /** Transition type: animation run on items that are changing due to an item disappearing. */
    public static final int CHANGE_DISAPPEARING = 1;
    /** Transition type: animation run on the item being added. */
    public static final int APPEARING           = 2;
    /** Transition type: animation run on the item being removed. */
    public static final int DISAPPEARING        = 3;
    /** Transition type: animation run on items that are changing for any other reason. */
    public static final int CHANGING            = 4;

    private static final long DEFAULT_DURATION = 300L;

    private final Map<Integer, Animator> mAnimators = new HashMap<>();
    private final Map<Integer, Long>     mDurations  = new HashMap<>();
    private final Set<Integer> mEnabledTransitionTypes = new HashSet<>();

    /** Listener interface for transition lifecycle events. */
    public interface TransitionListener {
        void startTransition(LayoutTransition transition, Object container, Object view, int transitionType);
        void endTransition(LayoutTransition transition, Object container, Object view, int transitionType);
    }

    public LayoutTransition() {
        // Enable all transition types by default (matches Android behavior)
        mEnabledTransitionTypes.add(CHANGE_APPEARING);
        mEnabledTransitionTypes.add(CHANGE_DISAPPEARING);
        mEnabledTransitionTypes.add(APPEARING);
        mEnabledTransitionTypes.add(DISAPPEARING);
    }

    /**
     * Sets the Animator used for a given transition type.
     *
     * @param transitionType One of {@link #CHANGE_APPEARING}, {@link #CHANGE_DISAPPEARING},
     *                       {@link #APPEARING}, {@link #DISAPPEARING}, or {@link #CHANGING}.
     * @param animator       The Animator to use, or {@code null} to clear.
     */
    public void setAnimator(int transitionType, Animator animator) {
        if (animator == null) {
            mAnimators.remove(transitionType);
        } else {
            mAnimators.put(transitionType, animator);
        }
    }

    /**
     * Returns the Animator currently associated with the given transition type,
     * or {@code null} if none has been set.
     */
    public Animator getAnimator(int transitionType) {
        return mAnimators.get(transitionType);
    }

    /**
     * Sets the duration for all transition types.
     */
    public void setDuration(long duration) {
        for (int type : new int[]{CHANGE_APPEARING, CHANGE_DISAPPEARING, APPEARING, DISAPPEARING, CHANGING}) {
            mDurations.put(type, duration);
        }
    }

    /**
     * Sets the duration for a specific transition type.
     */
    public void setDuration(int transitionType, long duration) {
        mDurations.put(transitionType, duration);
    }

    /**
     * Returns the duration for the given transition type.
     */
    public long getDuration(int transitionType) {
        Long d = mDurations.get(transitionType);
        return d != null ? d : DEFAULT_DURATION;
    }

    /**
     * Enables the specified transition type so it will run when triggered.
     */
    public void enableTransitionType(int transitionType) {
        mEnabledTransitionTypes.add(transitionType);
    }

    /**
     * Disables the specified transition type so it will not run when triggered.
     */
    public void disableTransitionType(int transitionType) {
        mEnabledTransitionTypes.remove(transitionType);
    }

    /**
     * Returns whether the given transition type is currently enabled.
     */
    public boolean isTransitionTypeEnabled(int transitionType) {
        return mEnabledTransitionTypes.contains(transitionType);
    }
}
