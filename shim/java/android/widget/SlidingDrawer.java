package android.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

@Deprecated
public class SlidingDrawer extends ViewGroup {

    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL   = 1;

    public interface OnDrawerOpenListener { void onDrawerOpened(); }
    public interface OnDrawerCloseListener { void onDrawerClosed(); }
    public interface OnDrawerScrollListener { void onScrollStarted(); void onScrollEnded(); }

    private boolean opened = false;
    private int orientation = ORIENTATION_VERTICAL;
    private OnDrawerOpenListener onDrawerOpenListener;
    private OnDrawerCloseListener onDrawerCloseListener;
    private OnDrawerScrollListener onDrawerScrollListener;

    public SlidingDrawer(Context context, AttributeSet attrs) { super(context, attrs); }
    public SlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    public boolean isOpened() { return opened; }
    public boolean isMoving() { return false; }
    public void open() { opened = true; if (onDrawerOpenListener != null) onDrawerOpenListener.onDrawerOpened(); }
    public void close() { opened = false; if (onDrawerCloseListener != null) onDrawerCloseListener.onDrawerClosed(); }
    public void animateOpen() { open(); }
    public void animateClose() { close(); }
    public void animateToggle() { if (opened) animateClose(); else animateOpen(); }
    public void toggle() { if (opened) close(); else open(); }
    public View getHandle() { return null; }
    public View getContent() { return null; }
    public void lock() {}
    public void unlock() {}
    public int getOrientation() { return orientation; }
    public void setOrientation(int orientation) { this.orientation = orientation; }
    public void setOnDrawerOpenListener(OnDrawerOpenListener listener) { this.onDrawerOpenListener = listener; }
    public void setOnDrawerCloseListener(OnDrawerCloseListener listener) { this.onDrawerCloseListener = listener; }
    public void setOnDrawerScrollListener(OnDrawerScrollListener listener) { this.onDrawerScrollListener = listener; }
}
