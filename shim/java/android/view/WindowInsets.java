package android.view;

import android.graphics.Insets;
import android.graphics.Rect;

public final class WindowInsets {
    public WindowInsets() {}
    public WindowInsets(WindowInsets src) {}

    public boolean hasInsets() { return false; }
    public boolean isConsumed() { return false; }
    public boolean isRound() { return false; }
    public boolean isVisible(Object p0) { return false; }
    public int getSystemWindowInsetLeft() { return 0; }
    public int getSystemWindowInsetTop() { return 0; }
    public int getSystemWindowInsetRight() { return 0; }
    public int getSystemWindowInsetBottom() { return 0; }
    public boolean hasSystemWindowInsets() { return false; }
    public Rect getSystemWindowInsetsAsRect() { return new Rect(); }
    public boolean isSystemWindowInsetsConsumed() { return true; }
    public WindowInsets consumeSystemWindowInsets() { return this; }
    public int getStableInsetLeft() { return 0; }
    public int getStableInsetTop() { return 0; }
    public int getStableInsetRight() { return 0; }
    public int getStableInsetBottom() { return 0; }
    public boolean hasStableInsets() { return false; }
    public WindowInsets consumeStableInsets() { return this; }
    public DisplayCutout getDisplayCutout() { return null; }
    public WindowInsets consumeDisplayCutout() { return this; }
    public Insets getInsets(int typeMask) { return Insets.NONE; }
    public Insets getInsetsIgnoringVisibility(int typeMask) { return Insets.NONE; }
    public Object getInsetsController() { return null; }
    public static final int UNDEFINED_WINDOW_ID = -1;

    /** Auto-generated stub. */
    public static class Type {
        public Type() {}
        public static int statusBars() { return 1; }
        public static int navigationBars() { return 2; }
        public static int ime() { return 8; }
        public static int systemBars() { return 3; }
    }

    public static class Builder {
        public Builder() {}
        public Builder(WindowInsets insets) {}
        public Builder setInsets(int typeMask, Insets insets) { return this; }
        public Builder setInsetsIgnoringVisibility(int typeMask, Insets insets) { return this; }
        public Builder setVisible(int typeMask, boolean visible) { return this; }
        public WindowInsets build() { return new WindowInsets(); }
    }
}
