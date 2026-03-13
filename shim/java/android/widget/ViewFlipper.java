package android.widget;

/**
 * Shim: android.widget.ViewFlipper → auto-flipping ViewAnimator
 *
 * Extends ViewAnimator with automatic flipping at a set interval.
 */
public class ViewFlipper extends ViewAnimator {

    private int flipInterval = 3000;
    private boolean flipping;

    public ViewFlipper() {
        super();
    }

    public void startFlipping() {
        this.flipping = true;
        // no-op stub — would start a timer to flip children
    }

    public void stopFlipping() {
        this.flipping = false;
    }

    public boolean isFlipping() {
        return flipping;
    }

    public void setFlipInterval(int milliseconds) {
        this.flipInterval = milliseconds;
    }

    public int getFlipInterval() {
        return flipInterval;
    }

    public void setAutoStart(boolean autoStart) {
        // no-op stub
    }

    public boolean isAutoStart() {
        return false;
    }
}
