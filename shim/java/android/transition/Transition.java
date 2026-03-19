package android.transition;

public class Transition {
    public Transition() {}

    public static final int MATCH_ID = 0;
    public static final int MATCH_INSTANCE = 0;
    public static final int MATCH_ITEM_ID = 0;
    public static final int MATCH_NAME = 0;

    /** Inner interface for transition lifecycle callbacks. */
    public interface TransitionListener {
        void onTransitionStart(Transition transition);
        void onTransitionEnd(Transition transition);
        void onTransitionCancel(Transition transition);
        void onTransitionPause(Transition transition);
        void onTransitionResume(Transition transition);
    }

    /** Inner abstract class for epicenter callbacks. */
    public static abstract class EpicenterCallback {
        public abstract android.graphics.Rect onGetEpicenter(Transition transition);
    }

    public Transition addListener(TransitionListener listener) { return this; }
    public Transition removeListener(TransitionListener listener) { return this; }
    public Object addTarget(Object p0) { return null; }
    public boolean canRemoveViews() { return false; }
    public void captureEndValues(Object p0) {}
    public void captureStartValues(Object p0) {}
    public Transition clone() {
        try {
            return (Transition) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    public Object createAnimator(Object p0, Object p1, Object p2) { return null; }
    public Object excludeChildren(Object p0, Object p1) { return null; }
    public Object excludeTarget(Object p0, Object p1) { return null; }
    public long getDuration() { return 0L; }
    public Object getEpicenter() { return null; }
    public EpicenterCallback getEpicenterCallback() { return null; }
    public Object getInterpolator() { return null; }
    public Object getName() { return null; }
    public Object getPathMotion() { return null; }
    public Object getPropagation() { return null; }
    public long getStartDelay() { return 0L; }
    public Object getTargetIds() { return null; }
    public Object getTargetNames() { return null; }
    public Object getTargetTypes() { return null; }
    public Object getTargets() { return null; }
    public Object getTransitionProperties() { return null; }
    public Object getTransitionValues(Object p0, Object p1) { return null; }
    public boolean isTransitionRequired(Object p0, Object p1) { return false; }
    public Object removeTarget(Object p0) { return null; }
    public Transition setDuration(long duration) { return this; }
    public void setEpicenterCallback(EpicenterCallback cb) {}
    public Object setInterpolator(Object p0) { return null; }
    public void setMatchOrder(Object p0) {}
    public void setPathMotion(Object p0) {}
    public void setPropagation(Object p0) {}
    public Object setStartDelay(Object p0) { return null; }
    public Object onGetEpicenter(Object p0) { return null; }
}
