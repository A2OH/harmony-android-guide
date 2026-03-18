package android.support.v4.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class SwipeRefreshLayout extends ViewGroup {

    private boolean mRefreshing = false;
    private OnRefreshListener mOnRefreshListener;

    public SwipeRefreshLayout(Context context) { super(context); }
    public SwipeRefreshLayout(Context context, AttributeSet attrs) { super(context, attrs); }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    public void setOnRefreshListener(OnRefreshListener listener) { this.mOnRefreshListener = listener; }
    public void setRefreshing(boolean refreshing) { this.mRefreshing = refreshing; }
    public boolean isRefreshing() { return mRefreshing; }
    public void setColorSchemeResources(int... colorResIds) {}
    public void setColorSchemeColors(int... colors) {}
    public void setProgressBackgroundColorSchemeResource(int colorRes) {}
    public void setProgressBackgroundColorSchemeColor(int color) {}
    public void setNestedScrollingEnabled(boolean enabled) {}

    public interface OnRefreshListener { void onRefresh(); }
}
