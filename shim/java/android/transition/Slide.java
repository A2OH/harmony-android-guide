package android.transition;

import android.view.Gravity;

/**
 * Android-compatible Slide transition shim.
 * Stub — no animation is performed on OpenHarmony.
 */
public class Slide extends Transition {

    private int mSlideEdge;

    public Slide() {
        this(Gravity.BOTTOM);
    }

    public Slide(int slideEdge) {
        mSlideEdge = slideEdge;
    }

    public void setSlideEdge(int slideEdge) {
        mSlideEdge = slideEdge;
    }

    public int getSlideEdge() {
        return mSlideEdge;
    }
}
