package android.view;

/**
 * Shim: android.view.WindowInsets — immutable description of window insets.
 *
 * Describes the space along the edges of the screen occupied by system UI
 * (status bar, navigation bar, IME, etc.). All getters return 0 in this
 * no-op shim; consuming methods return {@code this}.
 */
public final class WindowInsets {

    /** An empty WindowInsets with all insets set to 0. */
    public static final WindowInsets CONSUMED = new WindowInsets(0, 0, 0, 0);

    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    public WindowInsets(int left, int top, int right, int bottom) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
    }

    public WindowInsets() {
        this(0, 0, 0, 0);
    }

    // ── Raw / system-window insets ──

    public int getSystemWindowInsetLeft()   { return left;   }
    public int getSystemWindowInsetTop()    { return top;    }
    public int getSystemWindowInsetRight()  { return right;  }
    public int getSystemWindowInsetBottom() { return bottom; }

    public boolean hasSystemWindowInsets() {
        return left != 0 || top != 0 || right != 0 || bottom != 0;
    }

    // ── Stable insets (status bar + navigation bar heights) ──

    public int getStableInsetLeft()   { return 0; }
    public int getStableInsetTop()    { return 0; }
    public int getStableInsetRight()  { return 0; }
    public int getStableInsetBottom() { return 0; }

    public boolean hasStableInsets() { return false; }

    // ── System bars (status + navigation) ──

    public int getSystemGestureInsetLeft()   { return 0; }
    public int getSystemGestureInsetTop()    { return 0; }
    public int getSystemGestureInsetRight()  { return 0; }
    public int getSystemGestureInsetBottom() { return 0; }

    // ── Display cutout ──

    /** Returns null — no display cutout in this shim environment. */
    public Object getDisplayCutout() { return null; }

    // ── IME ──

    public boolean isVisible(int typeMask) { return false; }
    public int getInsets(int typeMask)     { return 0;     }

    public boolean isConsumed() {
        return left == 0 && top == 0 && right == 0 && bottom == 0;
    }

    // ── Consume helpers ──

    /** Returns a copy with system-window insets replaced. */
    public WindowInsets replaceSystemWindowInsets(int left, int top, int right, int bottom) {
        return new WindowInsets(left, top, right, bottom);
    }

    /** Returns a fully consumed (zeroed) set of insets. */
    public WindowInsets consumeSystemWindowInsets() {
        return CONSUMED;
    }

    public WindowInsets consumeStableInsets() {
        return CONSUMED;
    }

    // ── Type masks (mirrors WindowInsets.Type) ──

    public static final class Type {
        private Type() {}
        public static int statusBars()      { return 1 << 0; }
        public static int navigationBars()  { return 1 << 1; }
        public static int captionBar()      { return 1 << 2; }
        public static int ime()             { return 1 << 3; }
        public static int systemGestures()  { return 1 << 4; }
        public static int mandatorySystemGestures() { return 1 << 5; }
        public static int tappableElement() { return 1 << 6; }
        public static int displayCutout()   { return 1 << 7; }
        public static int systemBars() {
            return statusBars() | navigationBars() | captionBar();
        }
        public static int all() { return ~0; }
    }

    // ── Builder ──

    public static final class Builder {
        private int left, top, right, bottom;

        public Builder setInsets(int typeMask, int left, int top, int right, int bottom) {
            this.left   = left;
            this.top    = top;
            this.right  = right;
            this.bottom = bottom;
            return this;
        }

        public WindowInsets build() {
            return new WindowInsets(left, top, right, bottom);
        }
    }

    @Override
    public String toString() {
        return "WindowInsets{left=" + left + ", top=" + top
                + ", right=" + right + ", bottom=" + bottom + "}";
    }
}
