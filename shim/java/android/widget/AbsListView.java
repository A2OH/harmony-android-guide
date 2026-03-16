package android.widget;
import android.view.ViewGroup;
import android.view.ViewGroup;

import android.view.ViewGroup;

/**
 * Shim: android.widget.AbsListView — abstract base for scrollable list-like widgets.
 *
 * Concrete subclasses are ListView and GridView.
 * Scroll-state constants and OnScrollListener are preserved for source compatibility.
 */
public class AbsListView extends AdapterView {

    // Choice mode constants
    public static final int CHOICE_MODE_NONE          = 0;
    public static final int CHOICE_MODE_SINGLE        = 1;
    public static final int CHOICE_MODE_MULTIPLE      = 2;
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;

    // Scroll state constants
    public static final int SCROLL_STATE_IDLE         = 0;
    public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
    public static final int SCROLL_STATE_FLING        = 2;

    private OnScrollListener onScrollListener;
    private int choiceMode = CHOICE_MODE_NONE;
    private boolean fastScrollEnabled = false;

    protected AbsListView() {
        super();
    }

    protected AbsListView(int arkuiNodeType) {
        super(arkuiNodeType);
    }

    // ── Scroll listener ──

    public void setOnScrollListener(OnScrollListener listener) {
        this.onScrollListener = listener;
    }

    // ── Scroll helpers ──

    public void smoothScrollToPosition(int position) {
        // No-op shim: ArkUI scroll handled natively
    }

    // ── Selector drawable resource ──

    public void setSelector(int resId) {
        // No-op shim: resource loading not supported in headless mode
    }

    // ── Fast scroll ──

    public void setFastScrollEnabled(boolean enabled) {
        this.fastScrollEnabled = enabled;
    }

    public boolean isFastScrollEnabled() { return fastScrollEnabled; }

    // ── Choice mode ──

    public void setChoiceMode(int choiceMode) {
        this.choiceMode = choiceMode;
    }

    public int getChoiceMode() { return choiceMode; }

    // ── Dispatcher helpers for subclasses ──

    protected void dispatchScrollStateChanged(int state) {
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(this, state);
        }
    }

    protected void dispatchScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (onScrollListener != null) {
            onScrollListener.onScroll(this, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    // ── Interface ──

    public interface OnScrollListener {
        void onScrollStateChanged(AbsListView view, int scrollState);
        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }
}
