package android.support.v4.widget;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class DrawerLayout extends ViewGroup {

    public static final int LOCK_MODE_UNLOCKED      = 0;
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_LOCKED_OPEN   = 2;
    public static final int STATE_IDLE    = 0;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;

    private boolean mDrawerOpen = false;
    private int mLockMode = LOCK_MODE_UNLOCKED;
    private DrawerListener mDrawerListener;

    public DrawerLayout(Context context) { super(context); }
    public DrawerLayout(Context context, AttributeSet attrs) { super(context, attrs); }
    public DrawerLayout(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {}

    public void openDrawer(View drawerView) { mDrawerOpen = true; if (mDrawerListener != null) mDrawerListener.onDrawerOpened(drawerView); }
    public void openDrawer(int gravity) { openDrawer((View) null); }
    public void closeDrawer(View drawerView) { mDrawerOpen = false; if (mDrawerListener != null) mDrawerListener.onDrawerClosed(drawerView); }
    public void closeDrawer(int gravity) { closeDrawer((View) null); }
    public void closeDrawers() { closeDrawer((View) null); }
    public boolean isDrawerOpen(View drawer) { return mDrawerOpen; }
    public boolean isDrawerOpen(int drawerGravity) { return mDrawerOpen; }
    public boolean isDrawerVisible(View drawer) { return mDrawerOpen; }
    public boolean isDrawerVisible(int drawerGravity) { return mDrawerOpen; }
    public void setDrawerLockMode(int lockMode) { this.mLockMode = lockMode; }
    public void setDrawerLockMode(int lockMode, int edgeGravity) { this.mLockMode = lockMode; }
    public void setDrawerLockMode(int lockMode, View drawerView) { this.mLockMode = lockMode; }
    public int getDrawerLockMode(int edgeGravity) { return mLockMode; }
    public int getDrawerLockMode(View drawerView) { return mLockMode; }
    public void setDrawerListener(DrawerListener listener) { this.mDrawerListener = listener; }
    public void addDrawerListener(DrawerListener listener) { this.mDrawerListener = listener; }
    public void removeDrawerListener(DrawerListener listener) { if (mDrawerListener == listener) mDrawerListener = null; }
    public void setStatusBarBackgroundColor(int color) {}
    public void setStatusBarBackground(Object drawable) {}
    public void setScrimColor(int color) {}
    public void setDrawerTitle(int edgeGravity, CharSequence title) {}
    public CharSequence getDrawerTitle(int edgeGravity) { return null; }

    public interface DrawerListener {
        void onDrawerSlide(View drawerView, float slideOffset);
        void onDrawerOpened(View drawerView);
        void onDrawerClosed(View drawerView);
        void onDrawerStateChanged(int newState);
    }
    public static class SimpleDrawerListener implements DrawerListener {
        @Override public void onDrawerSlide(View drawerView, float slideOffset) {}
        @Override public void onDrawerOpened(View drawerView) {}
        @Override public void onDrawerClosed(View drawerView) {}
        @Override public void onDrawerStateChanged(int newState) {}
    }
}
