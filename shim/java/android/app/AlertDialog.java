package android.app;

/**
 * Android-compatible AlertDialog shim. Extends Dialog; stub — no UI rendered.
 */
public class AlertDialog extends Dialog {

    private String mMessage = "";

    /** Private constructor — use Builder to create instances. */
    protected AlertDialog() {
        super();
    }

    protected AlertDialog(Object context) {
        super();
    }

    protected AlertDialog(Object context, int themeResId) {
        super();
    }

    public void setMessage(CharSequence message) {
        mMessage = message != null ? message.toString() : "";
    }

    public String getMessage() {
        return mMessage;
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static class Builder {

        private String mTitle = "";
        private String mMessage = "";
        private String mPositiveLabel = "";
        private String mNegativeLabel = "";
        private String mNeutralLabel = "";
        private OnClickListener mPositiveListener;
        private OnClickListener mNegativeListener;
        private OnClickListener mNeutralListener;
        private CharSequence[] mItems;
        private OnClickListener mItemsListener;
        private boolean mCancelable = true;

        public Builder() {}

        /** Accepts an android.content.Context or any Object (ignored in shim). */
        public Builder(Object context) {}

        public Builder setTitle(CharSequence title) {
            mTitle = title != null ? title.toString() : "";
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title != null ? title : "";
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mMessage = message != null ? message.toString() : "";
            return this;
        }

        public Builder setMessage(String message) {
            mMessage = message != null ? message : "";
            return this;
        }

        public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            mPositiveLabel = text != null ? text.toString() : "";
            mPositiveListener = listener;
            return this;
        }

        public Builder setPositiveButton(String text, OnClickListener listener) {
            mPositiveLabel = text != null ? text : "";
            mPositiveListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            mNegativeLabel = text != null ? text.toString() : "";
            mNegativeListener = listener;
            return this;
        }

        public Builder setNegativeButton(String text, OnClickListener listener) {
            mNegativeLabel = text != null ? text : "";
            mNegativeListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            mNeutralLabel = text != null ? text.toString() : "";
            mNeutralListener = listener;
            return this;
        }

        public Builder setItems(CharSequence[] items, OnClickListener listener) {
            mItems = items;
            mItemsListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public AlertDialog create() {
            AlertDialog d = new AlertDialog();
            d.setTitle(mTitle);
            d.setMessage(mMessage);
            d.setCancelable(mCancelable);
            return d;
        }

        public AlertDialog show() {
            AlertDialog d = create();
            d.show();
            return d;
        }
    }

    // -------------------------------------------------------------------------
    // Listener interface (mirrors android.content.DialogInterface.OnClickListener)
    // -------------------------------------------------------------------------

    public interface OnClickListener {
        void onClick(AlertDialog dialog, int which);
    }

    public static final int BUTTON_POSITIVE = -1;
    public static final int BUTTON_NEGATIVE = -2;
    public static final int BUTTON_NEUTRAL  = -3;
}
