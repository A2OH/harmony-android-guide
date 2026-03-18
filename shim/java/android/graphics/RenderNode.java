package android.graphics;
import android.view.Display;
import android.view.View;
import android.view.Display;
import android.view.View;
import java.util.Set;

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
    public static RenderNode create(String name, Object owningView) {
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
    public RecordingCanvas beginRecording() {
        mHasDisplayList = true;
        return new RecordingCanvas();
    }

    public RecordingCanvas beginRecording(int width, int height) {
        mHasDisplayList = true;
        return new RecordingCanvas();
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

    // --- Additional methods needed for View.java compilation ---

    public static final int USAGE_BACKGROUND = 1;

    private int mAmbientShadowColor = 0xFF000000;
    private int mSpotShadowColor = 0xFF000000;
    private float mCameraDistance = 0f;
    private float mRotationX = 0f;
    private float mRotationY = 0f;
    private float mTranslationZ = 0f;
    private boolean mClipToOutline = false;
    private boolean mForceDarkAllowed = true;
    private boolean mPivotExplicitlySet = false;
    private long mUniqueId = 0;

    public boolean addPositionUpdateListener(PositionUpdateListener l) { return true; }
    public boolean removePositionUpdateListener(PositionUpdateListener l) { return true; }
    public int getAmbientShadowColor() { return mAmbientShadowColor; }
    public boolean setAmbientShadowColor(int color) { mAmbientShadowColor = color; return true; }
    public int getSpotShadowColor() { return mSpotShadowColor; }
    public boolean setSpotShadowColor(int color) { mSpotShadowColor = color; return true; }
    public Matrix getAnimationMatrix() { return null; }
    public boolean setAnimationMatrix(Matrix matrix) { return true; }
    public float getCameraDistance() { return mCameraDistance; }
    public boolean setCameraDistance(float distance) { mCameraDistance = distance; return true; }
    public boolean getClipToOutline() { return mClipToOutline; }
    public boolean setClipToOutline(boolean clip) { mClipToOutline = clip; return true; }
    public boolean getInverseMatrix(Matrix inverse) { return true; }
    public boolean getMatrix(Matrix outMatrix) { return true; }
    public float getRotationX() { return mRotationX; }
    public boolean setRotationX(float rotationX) { mRotationX = rotationX; return true; }
    public float getRotationY() { return mRotationY; }
    public boolean setRotationY(float rotationY) { mRotationY = rotationY; return true; }
    public float getTranslationZ() { return mTranslationZ; }
    public boolean setTranslationZ(float translationZ) { mTranslationZ = translationZ; return true; }
    public long getUniqueId() { return mUniqueId; }
    public boolean hasIdentityMatrix() { return true; }
    public boolean hasShadow() { return mElevation > 0; }
    public boolean isForceDarkAllowed() { return mForceDarkAllowed; }
    public boolean setForceDarkAllowed(boolean allow) { mForceDarkAllowed = allow; return true; }
    public boolean isPivotExplicitlySet() { return mPivotExplicitlySet; }
    public boolean offsetLeftAndRight(int offset) { mLeft += offset; mRight += offset; return true; }
    public boolean offsetTopAndBottom(int offset) { mTop += offset; mBottom += offset; return true; }
    public boolean resetPivot() { mPivotExplicitlySet = false; return true; }
    public boolean setClipRect(Rect rect) { return true; }
    public boolean setLayerPaint(Paint paint) { return true; }
    public boolean setLayerType(int layerType) { return true; }
    public boolean setLeft(int left) { mLeft = left; return true; }
    public boolean setTop(int top) { mTop = top; return true; }
    public boolean setRight(int right) { mRight = right; return true; }
    public boolean setBottom(int bottom) { mBottom = bottom; return true; }
    public boolean setLeftTopRightBottom(int left, int top, int right, int bottom) {
        mLeft = left; mTop = top; mRight = right; mBottom = bottom; return true;
    }
    public boolean setOutline(Outline outline) { return true; }
    public boolean setRevealClip(boolean shouldClip, float x, float y, float radius) { return true; }
    public boolean setClipToBounds(boolean clipToBounds) { return true; }
    public boolean setHasOverlappingRendering(boolean hasOverlappingRendering) { return true; }
    public boolean setProjectionReceiver(boolean shouldReceiveProjection) { return true; }
    public boolean setStaticMatrix(Object matrix) { return true; }
    public boolean setProjectBackwards(boolean shouldProject) { return true; }
    public boolean setUsageHint(int usageHint) { return true; }

    /** Auto-generated stub. */
    public static interface PositionUpdateListener {
        void positionChanged(long frameNumber, int left, int top, int right, int bottom);
        void positionLost(long frameNumber);
    }
}
