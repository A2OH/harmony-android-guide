package android.graphics;

import android.view.View;

/**
 * Android-compatible RenderNode shim.
 * Stubs the hardware-accelerated display list node; no actual GPU rendering.
 */
public final class RenderNode {

    private final String mName;
    private float mAlpha        = 1.0f;
    private float mRotationZ    = 0.0f;
    private float mTranslationX = 0.0f;
    private float mTranslationY = 0.0f;
    private float mScaleX       = 1.0f;
    private float mScaleY       = 1.0f;
    private float mElevation    = 0.0f;
    private float mPivotX       = 0.0f;
    private float mPivotY       = 0.0f;
    private int mLeft   = 0;
    private int mTop    = 0;
    private int mRight  = 0;
    private int mBottom = 0;
    private boolean mHasDisplayList = false;

    private RenderNode(String name) {
        mName = name;
    }

    // -------------------------------------------------------------------------
    // Factory
    // -------------------------------------------------------------------------

    /**
     * Create a new RenderNode.
     *
     * @param name optional debug name for the node
     * @param owningView the View that owns this node (may be null)
     */
    public static RenderNode create(String name, View owningView) {
        return new RenderNode(name != null ? name : "");
    }

    // -------------------------------------------------------------------------
    // Position / geometry
    // -------------------------------------------------------------------------

    /**
     * Set the position of this RenderNode within its parent.
     */
    public boolean setPosition(int left, int top, int right, int bottom) {
        mLeft   = left;
        mTop    = top;
        mRight  = right;
        mBottom = bottom;
        return true;
    }

    public int getLeft()   { return mLeft; }
    public int getTop()    { return mTop; }
    public int getRight()  { return mRight; }
    public int getBottom() { return mBottom; }
    public int getWidth()  { return mRight - mLeft; }
    public int getHeight() { return mBottom - mTop; }

    // -------------------------------------------------------------------------
    // Transform properties
    // -------------------------------------------------------------------------

    public boolean setAlpha(float alpha) {
        mAlpha = alpha;
        return true;
    }
    public float getAlpha() { return mAlpha; }

    public boolean setRotationZ(float rotationZ) {
        mRotationZ = rotationZ;
        return true;
    }
    public float getRotationZ() { return mRotationZ; }

    public boolean setTranslationX(float translationX) {
        mTranslationX = translationX;
        return true;
    }
    public float getTranslationX() { return mTranslationX; }

    public boolean setTranslationY(float translationY) {
        mTranslationY = translationY;
        return true;
    }
    public float getTranslationY() { return mTranslationY; }

    public boolean setScaleX(float scaleX) {
        mScaleX = scaleX;
        return true;
    }
    public float getScaleX() { return mScaleX; }

    public boolean setScaleY(float scaleY) {
        mScaleY = scaleY;
        return true;
    }
    public float getScaleY() { return mScaleY; }

    public boolean setElevation(float elevation) {
        mElevation = elevation;
        return true;
    }
    public float getElevation() { return mElevation; }

    public boolean setPivotX(float pivotX) {
        mPivotX = pivotX;
        return true;
    }
    public float getPivotX() { return mPivotX; }

    public boolean setPivotY(float pivotY) {
        mPivotY = pivotY;
        return true;
    }
    public float getPivotY() { return mPivotY; }

    // -------------------------------------------------------------------------
    // Display list recording
    // -------------------------------------------------------------------------

    /**
     * Start recording drawing commands into this RenderNode.
     *
     * @return a Canvas to draw into (stub — returns a no-op Canvas)
     */
    public Canvas beginRecording() {
        mHasDisplayList = true;
        return new Canvas();
    }

    /**
     * Finish recording drawing commands.
     */
    public void endRecording() {
        // No-op in shim
    }

    /**
     * Returns true if this node has a recorded display list.
     */
    public boolean hasDisplayList() {
        return mHasDisplayList;
    }

    /** Discard the current display list. */
    public void discardDisplayList() {
        mHasDisplayList = false;
    }

    public String getName() { return mName; }

    @Override public String toString() {
        return "RenderNode{name=" + mName + ", alpha=" + mAlpha + "}";
    }
}
