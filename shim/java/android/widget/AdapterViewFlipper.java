package android.widget;

/**
 * Shim: android.widget.AdapterViewFlipper — auto-advances through adapter views.
 *
 * Extends AdapterView. Flip interval and start/stop flipping are recorded
 * for source compatibility; actual animation is a no-op in the shim layer.
 */
public class AdapterViewFlipper extends AdapterView {

    private Object adapter;
    private boolean flipping = false;
    private int flipInterval = 3000; // default 3 s, matches Android default

    public AdapterViewFlipper(android.content.Context context) {
        super(context);
    }
    public AdapterViewFlipper(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    // ── AdapterView abstract contract ──

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItemAtPosition(int position) {
        return null;
    }

    // ── Adapter ──

    public void setAdapter(Object adapter) {
        this.adapter = adapter;
    }

    public Object getAdapter() {
        return adapter;
    }

    // ── Navigation ──

    public void showNext() {
        // No-op shim: would advance to next adapter item
    }

    public void showPrevious() {
        // No-op shim: would retreat to previous adapter item
    }

    // ── Auto-flip ──

    public void startFlipping() {
        flipping = true;
    }

    public void stopFlipping() {
        flipping = false;
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
}
