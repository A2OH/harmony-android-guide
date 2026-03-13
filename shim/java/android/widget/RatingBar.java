package android.widget;

import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.RatingBar — star-rating widget, extends ProgressBar.
 *
 * ArkUI does not have a native RatingBar node; this shim extends ProgressBar
 * (ARKUI_NODE_PROGRESS) and keeps the rating/numStars state in Java.
 * setRating() updates the underlying progress value proportionally.
 */
public class RatingBar extends ProgressBar {

    private int numStars  = 5;
    private float rating  = 0f;
    private float stepSize = 0.5f;
    private boolean isIndicator = false;
    private OnRatingBarChangeListener onRatingBarChangeListener;

    public RatingBar() {
        super();
        setMax(numStars * 2); // stepSize 0.5 → multiply by 2 for integer progress
    }

    // ── Star count ──

    public void setNumStars(int numStars) {
        this.numStars = numStars;
        setMax((int) (numStars / stepSize));
    }

    public int getNumStars() { return numStars; }

    // ── Rating ──

    public void setRating(float rating) {
        this.rating = Math.max(0f, Math.min(rating, numStars));
        setProgress((int) (this.rating / stepSize));
        if (onRatingBarChangeListener != null) {
            onRatingBarChangeListener.onRatingChanged(this, this.rating, false);
        }
    }

    public float getRating() { return rating; }

    // ── Step size ──

    public void setStepSize(float stepSize) {
        this.stepSize = stepSize > 0 ? stepSize : 0.5f;
        setMax((int) (numStars / this.stepSize));
    }

    public float getStepSize() { return stepSize; }

    // ── Indicator mode (display only, no interaction) ──

    public void setIsIndicator(boolean isIndicator) {
        this.isIndicator = isIndicator;
    }

    public boolean isIndicator() { return isIndicator; }

    // ── Listener ──

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener listener) {
        this.onRatingBarChangeListener = listener;
    }

    public OnRatingBarChangeListener getOnRatingBarChangeListener() {
        return onRatingBarChangeListener;
    }

    // ── Interface ──

    public interface OnRatingBarChangeListener {
        void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser);
    }
}
