package android.text.style;

import android.app.PendingIntent;

/**
 * Stub for android.text.style.EasyEditSpan.
 * Span that marks a region as easy-to-edit.
 */
public class EasyEditSpan {

    public static final String EXTRA_TEXT_CHANGED_TYPE = "android.text.style.EXTRA_TEXT_CHANGED_TYPE";
    public static final int TEXT_DELETED = 1;
    public static final int TEXT_MODIFIED = 2;

    private PendingIntent mPendingIntent;
    private boolean mDeleteEnabled = true;

    public EasyEditSpan() {}

    public EasyEditSpan(PendingIntent pendingIntent) {
        mPendingIntent = pendingIntent;
    }

    public PendingIntent getPendingIntent() { return mPendingIntent; }

    public boolean isDeleteEnabled() { return mDeleteEnabled; }
    public void setDeleteEnabled(boolean enabled) { mDeleteEnabled = enabled; }
}
