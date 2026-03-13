package android.graphics.drawable;

import android.graphics.Canvas;

/**
 * Android-compatible DrawableContainer shim. Abstract drawable that manages a
 * collection of child drawables, selecting one at a time.
 */
public abstract class DrawableContainer extends Drawable {

    private DrawableContainerState mDrawableContainerState;
    private int mCurrentIndex = -1;
    private Drawable mCurrentDrawable;

    /** Abstract state that subclasses extend to hold their child drawable data. */
    public abstract static class DrawableContainerState {
        private final java.util.List<Drawable> mDrawables = new java.util.ArrayList<>();

        public DrawableContainerState() {}

        public int addChild(Drawable dr) {
            mDrawables.add(dr);
            return mDrawables.size() - 1;
        }

        public int getChildCount() {
            return mDrawables.size();
        }

        public Drawable getChild(int index) {
            if (index < 0 || index >= mDrawables.size()) return null;
            return mDrawables.get(index);
        }

        public Drawable[] getChildren() {
            return mDrawables.toArray(new Drawable[0]);
        }
    }

    protected void setConstantState(DrawableContainerState state) {
        mDrawableContainerState = state;
    }

    public DrawableContainerState getDrawableContainerState() {
        return mDrawableContainerState;
    }

    /**
     * Select the drawable at the given index. Returns true if selection changed.
     */
    public boolean selectDrawable(int index) {
        if (index == mCurrentIndex) return false;

        DrawableContainerState state = mDrawableContainerState;
        if (state == null) return false;

        Drawable dr = state.getChild(index);
        mCurrentDrawable = dr;
        mCurrentIndex = index;
        invalidateSelf();
        return true;
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public Drawable getCurrent() {
        return mCurrentDrawable;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mCurrentDrawable != null) mCurrentDrawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mCurrentDrawable != null) mCurrentDrawable.setAlpha(alpha);
    }

    @Override
    public int getAlpha() { return 255; }

    @Override
    public int getOpacity() { return -3; /* PixelFormat.TRANSLUCENT */ }

    @Override
    public int getIntrinsicWidth() {
        return mCurrentDrawable != null ? mCurrentDrawable.getIntrinsicWidth() : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return mCurrentDrawable != null ? mCurrentDrawable.getIntrinsicHeight() : -1;
    }
}
