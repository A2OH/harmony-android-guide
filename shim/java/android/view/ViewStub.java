package android.view;

/**
 * Shim: android.view.ViewStub — lazy-inflating placeholder view.
 *
 * A ViewStub is an invisible, zero-size View that can be used to lazily
 * inflate layout resources at runtime. In this no-op shim the inflate
 * operation does nothing and returns null. The stub itself behaves like
 * a minimally-visible View (GONE by default, matching real Android behaviour).
 */
public class ViewStub extends View {

    /** Callback notified when the stub has been successfully inflated. */
    public interface OnInflateListener {
        void onInflate(ViewStub stub, View inflated);
    }

    private int layoutResource = 0;
    private int inflatedId     = -1; // View.NO_ID
    private OnInflateListener onInflateListener;

    public ViewStub() {
        super();
        setVisibility(View.GONE);
    }

    public ViewStub(int layoutResource) {
        this();
        this.layoutResource = layoutResource;
    }

    // ── Layout resource ──

    public int getLayoutResource()              { return layoutResource; }
    public void setLayoutResource(int layoutResource) {
        this.layoutResource = layoutResource;
    }

    // ── Inflated view id ──

    public int  getInflatedId()               { return inflatedId; }
    public void setInflatedId(int inflatedId) { this.inflatedId = inflatedId; }

    // ── Listener ──

    public OnInflateListener getOnInflateListener() {
        return onInflateListener;
    }

    public void setOnInflateListener(OnInflateListener listener) {
        this.onInflateListener = listener;
    }

    // ── Inflate ──

    /**
     * Inflate the layout resource associated with this stub.
     *
     * In this shim no real inflation occurs. Returns {@code null} and notifies
     * the {@link OnInflateListener} (if set) with a {@code null} inflated view
     * so that callers that perform null-checks behave correctly.
     *
     * @return null (no-op shim)
     */
    public View inflate() {
        if (onInflateListener != null) {
            onInflateListener.onInflate(this, null);
        }
        return null;
    }

    // ViewStub is always GONE and has no measured size
    @Override
    public int getVisibility() {
        return View.GONE;
    }
}
