package android.support.v4.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

public class ViewPager extends ViewGroup {

    public static final int SCROLL_STATE_IDLE     = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    private PagerAdapter mAdapter;
    private int mCurrentItem = 0;
    private int mOffscreenPageLimit = 1;
    private OnPageChangeListener mOnPageChangeListener;
    private final List<OnPageChangeListener> mOnPageChangeListeners = new ArrayList<>();

    public ViewPager(Context context) { super(context); }
    public ViewPager(Context context, AttributeSet attrs) { super(context, attrs); }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    public void setAdapter(PagerAdapter adapter) { this.mAdapter = adapter; if (adapter != null) mCurrentItem = 0; }
    public PagerAdapter getAdapter() { return mAdapter; }
    public void setCurrentItem(int item) { setCurrentItem(item, true); }
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (mAdapter == null) return;
        int count = mAdapter.getCount();
        if (count == 0) return;
        item = Math.max(0, Math.min(item, count - 1));
        if (item == mCurrentItem) return;
        mCurrentItem = item;
        dispatchOnPageSelected(item);
    }
    public int getCurrentItem() { return mCurrentItem; }
    public void setOffscreenPageLimit(int limit) { if (limit < 1) limit = 1; this.mOffscreenPageLimit = limit; }
    public int getOffscreenPageLimit() { return mOffscreenPageLimit; }
    @Deprecated
    public void setOnPageChangeListener(OnPageChangeListener listener) { this.mOnPageChangeListener = listener; }
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (listener != null && !mOnPageChangeListeners.contains(listener)) mOnPageChangeListeners.add(listener);
    }
    public void removeOnPageChangeListener(OnPageChangeListener listener) { mOnPageChangeListeners.remove(listener); }
    public void clearOnPageChangeListeners() { mOnPageChangeListeners.clear(); }

    private void dispatchOnPageSelected(int position) {
        if (mOnPageChangeListener != null) mOnPageChangeListener.onPageSelected(position);
        for (OnPageChangeListener l : mOnPageChangeListeners) l.onPageSelected(position);
    }

    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
        void onPageSelected(int position);
        void onPageScrollStateChanged(int state);
    }
    public static class SimpleOnPageChangeListener implements OnPageChangeListener {
        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override public void onPageSelected(int position) {}
        @Override public void onPageScrollStateChanged(int state) {}
    }
}
