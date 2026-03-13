package android.app;

/**
 * Android-compatible ProgressDialog shim. Extends AlertDialog — stub, no UI rendered.
 */
public class ProgressDialog extends AlertDialog {

    public static final int STYLE_SPINNER    = 0;
    public static final int STYLE_HORIZONTAL = 1;

    private int mMax = 100;
    private int mProgress = 0;
    private int mStyle = STYLE_SPINNER;
    private boolean mIndeterminate = true;

    public ProgressDialog() {
        super();
    }

    /** Accepts an android.content.Context or any Object (ignored in shim). */
    public ProgressDialog(Object context) {
        super();
    }

    public void setProgress(int value) {
        mProgress = value;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getMax() {
        return mMax;
    }

    public void setProgressStyle(int style) {
        mStyle = style;
    }

    public int getProgressStyle() {
        return mStyle;
    }

    public void setIndeterminate(boolean indeterminate) {
        mIndeterminate = indeterminate;
    }

    public boolean isIndeterminate() {
        return mIndeterminate;
    }

    public void incrementProgressBy(int diff) {
        mProgress += diff;
        if (mProgress > mMax) mProgress = mMax;
        if (mProgress < 0) mProgress = 0;
    }

    public void setProgressNumberFormat(String format) {
        // stub — no UI in shim layer
    }

    public void setProgressPercentFormat(java.text.NumberFormat format) {
        // stub — no UI in shim layer
    }
}
