package android.view;

/** Stub for AOSP compilation - RenderNode wraps native display list. */
public class RenderNode {
    public RenderNode(String name) {}
    public RenderNode() {}

    public void setClipToBounds(boolean clipToBounds) {}
    public void offsetTopAndBottom(int offset) {}
    public void offsetLeftAndRight(int offset) {}
    public boolean isValid() { return false; }
    public void discardDisplayList() {}
    public void setLeftTopRightBottom(int left, int top, int right, int bottom) {}
    public boolean hasDisplayList() { return false; }
    public float getAlpha() { return 1.0f; }
    public boolean setAlpha(float alpha) { return false; }
    public float getTranslationX() { return 0f; }
    public boolean setTranslationX(float translationX) { return false; }
    public float getTranslationY() { return 0f; }
    public boolean setTranslationY(float translationY) { return false; }
    public float getTranslationZ() { return 0f; }
    public boolean setTranslationZ(float translationZ) { return false; }
    public float getRotation() { return 0f; }
    public boolean setRotation(float rotation) { return false; }
    public float getRotationX() { return 0f; }
    public boolean setRotationX(float rotationX) { return false; }
    public float getRotationY() { return 0f; }
    public boolean setRotationY(float rotationY) { return false; }
    public float getScaleX() { return 1f; }
    public boolean setScaleX(float scaleX) { return false; }
    public float getScaleY() { return 1f; }
    public boolean setScaleY(float scaleY) { return false; }
    public float getPivotX() { return 0f; }
    public boolean setPivotX(float pivotX) { return false; }
    public float getPivotY() { return 0f; }
    public boolean setPivotY(float pivotY) { return false; }
    public float getElevation() { return 0f; }
    public boolean setElevation(float elevation) { return false; }
    public boolean setHasOverlappingRendering(boolean hasOverlappingRendering) { return false; }
    public boolean setOutline(android.graphics.Outline outline) { return false; }
    public boolean setClipToOutline(boolean clipToOutline) { return false; }
    public boolean setProjectBackwards(boolean projectBackwards) { return false; }
    public boolean setProjectionReceiver(boolean projectionReceiver) { return false; }
}
