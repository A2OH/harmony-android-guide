package android.support.design.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CoordinatorLayout extends ViewGroup {

    public CoordinatorLayout(Context context) { super(context); }
    public CoordinatorLayout(Context context, AttributeSet attrs) { super(context, attrs); }
    public CoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    public static abstract class Behavior<V extends View> {
        public Behavior() {}
        public Behavior(Context context, AttributeSet attrs) {}
        public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) { return false; }
        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child,
                                           View directTargetChild, View target, int nestedScrollAxes) { return false; }
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target,
                                   int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {}
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private Behavior mBehavior;
        public LayoutParams(int width, int height) { super(width, height); }
        public LayoutParams(ViewGroup.MarginLayoutParams source) { super(source); }
        public LayoutParams(LayoutParams source) { super((ViewGroup.MarginLayoutParams) source); this.mBehavior = source.mBehavior; }
        public void setBehavior(Behavior behavior) { this.mBehavior = behavior; }
        public Behavior getBehavior() { return mBehavior; }
        public int gravity = -1;
        public int anchorId = -1;
        public int anchorGravity = -1;
    }
}
