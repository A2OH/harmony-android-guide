package android.widget;

/**
 * Shim: android.widget.ViewAnimator → ArkUI Swiper-like component
 *
 * Base class for frame animations that switches between child views.
 */
public class ViewAnimator extends FrameLayout {

    private int displayedChild;

    public ViewAnimator() { super(new android.content.Context());
    }

    public void setDisplayedChild(int whichChild) {
        this.displayedChild = whichChild;
    }

    public int getDisplayedChild() {
        return displayedChild;
    }

    public void showNext() {
        // no-op stub — would advance to next child
    }

    public void showPrevious() {
        // no-op stub — would go to previous child
    }

    public void setInAnimation(Object animation) {
        // no-op stub
    }

    public void setOutAnimation(Object animation) {
        // no-op stub
    }

    public void setAnimateFirstView(boolean animate) {
        // no-op stub
    }
}
