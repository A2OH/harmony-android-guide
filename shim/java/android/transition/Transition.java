package android.transition;

import java.util.ArrayList;

/**
 * Android-compatible Transition shim. Abstract base for all transitions.
 * No-op on OpenHarmony; structural stub for compile compatibility.
 */
public abstract class Transition {

    private long mDuration = -1;
    private long mStartDelay = -1;
    private Object mInterpolator; // android.animation.TimeInterpolator — kept as Object to avoid hard dep
    private final ArrayList<Object> mTargets = new ArrayList<>();
    private final ArrayList<TransitionListener> mListeners = new ArrayList<>();

    // ── Target management ──

    public Transition addTarget(android.view.View target) {
        if (target != null) mTargets.add(target);
        return this;
    }

    public Transition addTarget(int targetId) {
        mTargets.add(targetId);
        return this;
    }

    public Transition addTarget(String targetName) {
        if (targetName != null) mTargets.add(targetName);
        return this;
    }

    public Transition addTarget(Class targetType) {
        if (targetType != null) mTargets.add(targetType);
        return this;
    }

    public Transition removeTarget(android.view.View target) {
        mTargets.remove(target);
        return this;
    }

    public Transition removeTarget(int targetId) {
        mTargets.remove(Integer.valueOf(targetId));
        return this;
    }

    // ── Timing ──

    public Transition setDuration(long duration) {
        mDuration = duration;
        return this;
    }

    public long getDuration() {
        return mDuration;
    }

    public Transition setStartDelay(long startDelay) {
        mStartDelay = startDelay;
        return this;
    }

    public long getStartDelay() {
        return mStartDelay;
    }

    public Transition setInterpolator(Object interpolator) {
        mInterpolator = interpolator;
        return this;
    }

    public Object getInterpolator() {
        return mInterpolator;
    }

    // ── Listener management ──

    public Transition addListener(TransitionListener listener) {
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
        return this;
    }

    public Transition removeListener(TransitionListener listener) {
        mListeners.remove(listener);
        return this;
    }

    // ── TransitionListener interface ──

    public interface TransitionListener {
        void onTransitionStart(Transition transition);
        void onTransitionEnd(Transition transition);
        void onTransitionCancel(Transition transition);
        void onTransitionPause(Transition transition);
        void onTransitionResume(Transition transition);
    }
}
