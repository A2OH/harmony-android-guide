package android.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.view.ViewTreeObserver — pure Java stub.
 * Provides notification of various view-tree events (layout, draw, etc.).
 */
public class ViewTreeObserver {

    private final List<OnGlobalLayoutListener> mGlobalLayoutListeners = new ArrayList<>();
    private final List<OnPreDrawListener> mPreDrawListeners = new ArrayList<>();
    private final List<OnScrollChangedListener> mScrollChangedListeners = new ArrayList<>();

    /** Package-private constructor. */
    ViewTreeObserver() {}
    ViewTreeObserver(android.content.Context context) {}

    // ── Listener registration ────────────────────────────────────────────────

    /** Register a callback to be invoked when the global layout state changes. */
    public void addOnGlobalLayoutListener(OnGlobalLayoutListener listener) {
        if (listener != null && !mGlobalLayoutListeners.contains(listener)) {
            mGlobalLayoutListeners.add(listener);
        }
    }

    /** Remove a previously installed global layout listener. */
    public void removeOnGlobalLayoutListener(OnGlobalLayoutListener victim) {
        mGlobalLayoutListeners.remove(victim);
    }

    /** Register a callback to be invoked before the view tree is drawn. */
    public void addOnPreDrawListener(OnPreDrawListener listener) {
        if (listener != null && !mPreDrawListeners.contains(listener)) {
            mPreDrawListeners.add(listener);
        }
    }

    /** Remove a previously installed pre-draw listener. */
    public void removeOnPreDrawListener(OnPreDrawListener victim) {
        mPreDrawListeners.remove(victim);
    }

    /** Register a callback invoked when the scroll position changes. */
    public void addOnScrollChangedListener(OnScrollChangedListener listener) {
        if (listener != null && !mScrollChangedListeners.contains(listener)) {
            mScrollChangedListeners.add(listener);
        }
    }

    /** Remove a previously installed scroll-changed listener. */
    public void removeOnScrollChangedListener(OnScrollChangedListener victim) {
        mScrollChangedListeners.remove(victim);
    }

    /** Return true if this observer is still valid (alive). */
    public boolean isAlive() {
        return true;
    }

    // ── Listener interfaces ──────────────────────────────────────────────────

    /** Object for global layout changes. */
    public interface OnGlobalLayoutListener {
        void onGlobalLayout();
    }

    /** Object invoked before the view tree is drawn. */
    public interface OnPreDrawListener {
        /** Return true to proceed with the draw, false to cancel. */
        boolean onPreDraw();
    }

    /** Object for scroll-position changes. */
    public interface OnScrollChangedListener {
        void onScrollChanged();
    }

    /** Auto-generated stub. */
    public static class InternalInsetsInfo {
        public InternalInsetsInfo() {}
    }

    // Methods needed for View.java
    ViewTreeObserver(boolean unused) {} // package-private constructor
    public void dispatchOnGlobalFocusChange(View oldFocus, View newFocus) {}
    public void merge(ViewTreeObserver other) {}
}
