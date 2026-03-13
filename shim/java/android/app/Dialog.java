package android.app;

/**
 * Android-compatible Dialog shim. Base dialog class — stub, no UI rendered.
 */
public class Dialog {

    private boolean mShowing = false;
    private boolean mCancelable = true;
    private String mTitle = "";

    public Dialog() {}

    public void show() {
        mShowing = true;
        System.out.println("[Dialog] show: " + mTitle);
    }

    public void dismiss() {
        mShowing = false;
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void setTitle(CharSequence title) {
        mTitle = title != null ? title.toString() : "";
    }

    public void setTitle(String title) {
        mTitle = title != null ? title : "";
    }

    public void setContentView(int layoutResID) {
        // stub — no UI in shim layer
    }

    public void setContentView(Object view) {
        // stub — no UI in shim layer
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        // stub — no touch events in shim layer
    }

    public void cancel() {
        dismiss();
    }

    public void hide() {
        mShowing = false;
    }

    public String getTitle() {
        return mTitle;
    }
}
