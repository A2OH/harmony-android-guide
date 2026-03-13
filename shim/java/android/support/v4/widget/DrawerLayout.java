package android.support.v4.widget;

import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.support.v4.widget.DrawerLayout
 *
 * DrawerLayout acts as a top-level container that allows for interactive
 * "drawer" views to be pulled out from one or both vertical edges of the
 * window.  All gesture handling and animation is stubbed — only the API
 * surface and state are modelled.
 */
public class DrawerLayout extends ViewGroup {

    // ── Lock mode constants ──

    /** The drawer is unlocked. */
    public static final int LOCK_MODE_UNLOCKED      = 0;
    /** The drawer is locked closed; ignore all interaction with the drawer. */
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    /** The drawer is locked open; ignore all interaction with the drawer. */
    public static final int LOCK_MODE_LOCKED_OPEN   = 2;

    // ── Drawer state constants ──

    public static final int STATE_IDLE    = 0;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;

    // ── Gravity constants for drawer gravity ──

    private static final int GRAVITY_LEFT  = 3;  // android.view.Gravity.LEFT
    private static final int GRAVITY_RIGHT = 5;  // android.view.Gravity.RIGHT
    private static final int GRAVITY_START = 8388611; // Gravity.START

    private boolean mDrawerOpen = false;
    private int mLockMode = LOCK_MODE_UNLOCKED;
    private DrawerListener mDrawerListener;
    private DrawerListener mSystemDrawerListener;

    public DrawerLayout() {
        super();
    }

    public DrawerLayout(Object context) {
        super();
    }

    public DrawerLayout(Object context, Object attrs) {
        super();
    }

    public DrawerLayout(Object context, Object attrs, int defStyle) {
        super();
    }

    // ── Open / close ──

    /**
     * Open the drawer view by animating it into view.
     *
     * @param drawerView Drawer view to open
     */
    public void openDrawer(View drawerView) {
        mDrawerOpen = true;
        if (mDrawerListener != null) mDrawerListener.onDrawerOpened(drawerView);
        if (mSystemDrawerListener != null) mSystemDrawerListener.onDrawerOpened(drawerView);
    }

    /**
     * Open the drawer identified by the given gravity constant.
     *
     * @param gravity Gravity.LEFT, Gravity.RIGHT, Gravity.START, or Gravity.END
     */
    public void openDrawer(int gravity) {
        openDrawer(null);
    }

    /**
     * Close the drawer view by animating it out of view.
     *
     * @param drawerView Drawer view to close
     */
    public void closeDrawer(View drawerView) {
        mDrawerOpen = false;
        if (mDrawerListener != null) mDrawerListener.onDrawerClosed(drawerView);
        if (mSystemDrawerListener != null) mSystemDrawerListener.onDrawerClosed(drawerView);
    }

    /**
     * Close the drawer identified by the given gravity constant.
     *
     * @param gravity Gravity.LEFT, Gravity.RIGHT, Gravity.START, or Gravity.END
     */
    public void closeDrawer(int gravity) {
        closeDrawer((View) null);
    }

    /** Close all currently open drawers. */
    public void closeDrawers() {
        closeDrawer((View) null);
    }

    // ── State queries ──

    /**
     * Check if the given drawer view is currently in an open state.
     *
     * @param drawer the drawer view to check
     * @return {@code true} if the drawer view is open
     */
    public boolean isDrawerOpen(View drawer) {
        return mDrawerOpen;
    }

    /**
     * Check if the drawer identified by the given gravity is currently open.
     *
     * @param drawerGravity the gravity of the drawer to check
     * @return {@code true} if the identified drawer is open
     */
    public boolean isDrawerOpen(int drawerGravity) {
        return mDrawerOpen;
    }

    /**
     * Check if a drawer view is currently visible (partially or fully open).
     *
     * @param drawer the drawer view to check
     * @return {@code true} if the drawer is visible
     */
    public boolean isDrawerVisible(View drawer) {
        return mDrawerOpen;
    }

    public boolean isDrawerVisible(int drawerGravity) {
        return mDrawerOpen;
    }

    // ── Lock mode ──

    public void setDrawerLockMode(int lockMode) {
        this.mLockMode = lockMode;
    }

    public void setDrawerLockMode(int lockMode, int edgeGravity) {
        this.mLockMode = lockMode;
    }

    public void setDrawerLockMode(int lockMode, View drawerView) {
        this.mLockMode = lockMode;
    }

    public int getDrawerLockMode(int edgeGravity) {
        return mLockMode;
    }

    public int getDrawerLockMode(View drawerView) {
        return mLockMode;
    }

    // ── Listener ──

    /**
     * Set a listener to be notified of drawer events.
     *
     * @param listener listener to notify of drawer events
     */
    public void setDrawerListener(DrawerListener listener) {
        this.mDrawerListener = listener;
    }

    public void addDrawerListener(DrawerListener listener) {
        this.mDrawerListener = listener;
    }

    public void removeDrawerListener(DrawerListener listener) {
        if (mDrawerListener == listener) {
            mDrawerListener = null;
        }
    }

    // ── Status bar / scrim ──

    public void setStatusBarBackgroundColor(int color) {
        // Stub
    }

    public void setStatusBarBackground(Object drawable) {
        // Stub
    }

    public void setScrimColor(int color) {
        // Stub
    }

    // ── Title ──

    public void setDrawerTitle(int edgeGravity, CharSequence title) {
        // Stub
    }

    public CharSequence getDrawerTitle(int edgeGravity) {
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DrawerListener interface
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Listener for monitoring events about drawers.
     */
    public interface DrawerListener {

        /**
         * Called when a drawer's position changes.
         *
         * @param drawerView  the child view that was moved
         * @param slideOffset the new offset of this drawer within its range, from 0-1
         */
        void onDrawerSlide(View drawerView, float slideOffset);

        /**
         * Called when a drawer has settled in a completely open state.
         *
         * @param drawerView the drawer view that is now open
         */
        void onDrawerOpened(View drawerView);

        /**
         * Called when a drawer has settled in a completely closed state.
         *
         * @param drawerView the drawer view that is now closed
         */
        void onDrawerClosed(View drawerView);

        /**
         * Called when the drawer motion state changes.
         *
         * @param newState the new drawer motion state: {@link #STATE_IDLE},
         *                 {@link #STATE_DRAGGING}, or {@link #STATE_SETTLING}
         */
        void onDrawerStateChanged(int newState);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SimpleDrawerListener (convenience no-op base)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Stub implementation of DrawerListener with all no-op methods.
     * Extend this to implement only the callbacks you need.
     */
    public static class SimpleDrawerListener implements DrawerListener {
        @Override public void onDrawerSlide(View drawerView, float slideOffset) {}
        @Override public void onDrawerOpened(View drawerView) {}
        @Override public void onDrawerClosed(View drawerView) {}
        @Override public void onDrawerStateChanged(int newState) {}
    }
}
