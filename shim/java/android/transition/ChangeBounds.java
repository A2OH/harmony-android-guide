package android.transition;

/**
 * Android-compatible ChangeBounds transition shim.
 * Animates changes in view layout bounds between scenes.
 * Stub — no animation is performed on OpenHarmony.
 */
public class ChangeBounds extends Transition {

    private boolean mResizeClip = false;

    public ChangeBounds() {
    }

    public void setResizeClip(boolean resizeClip) {
        mResizeClip = resizeClip;
    }

    public boolean getResizeClip() {
        return mResizeClip;
    }
}
