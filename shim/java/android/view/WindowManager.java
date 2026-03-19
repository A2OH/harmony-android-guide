package android.view;

public interface WindowManager extends ViewManager {
    void removeViewImmediate(View p0);

    /** Auto-generated stub. */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams() { super(0, 0); }
        public LayoutParams(int w, int h) { super(w, h); this.width = w; this.height = h; }
        public android.graphics.Rect surfaceInsets = new android.graphics.Rect();
        public int type = 0;
        public int flags = 0;
        public int format = 0;
        public int softInputMode = 0;
        public int gravity = 0;
        public float horizontalMargin = 0;
        public float verticalMargin = 0;
        public int width = 0;
        public int height = 0;
        public int x = 0;
        public int y = 0;
        public float alpha = 1.0f;
        public float dimAmount = 0f;
        public float screenBrightness = -1f;
        public float buttonBrightness = -1f;
        public int rotationAnimation = 0;
        public CharSequence title = "";
        public int privateFlags = 0;
        public int systemUiVisibility = 0;
        public static final int SOFT_INPUT_ADJUST_RESIZE = 0x10;
        public static final int SOFT_INPUT_ADJUST_PAN = 0x20;
        public static final int SOFT_INPUT_STATE_HIDDEN = 2;
        public static final int SOFT_INPUT_STATE_VISIBLE = 4;
        public static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;
        public static final int FLAG_FULLSCREEN = 0x00000400;
        public static final int FLAG_TRANSLUCENT_STATUS = 0x04000000;
        public static final int FLAG_TRANSLUCENT_NAVIGATION = 0x08000000;
        public static final int FLAG_LAYOUT_IN_SCREEN = 0x00000100;
        public static final int FLAG_NOT_FOCUSABLE = 0x00000008;
        public static final int FLAG_NOT_TOUCHABLE = 0x00000010;
        public static final int FLAG_KEEP_SCREEN_ON = 0x00000080;
        public static final int FLAG_SECURE = 0x00002000;
        public static final int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = 0x80000000;
        public static final int TYPE_APPLICATION = 2;
        public static final int TYPE_APPLICATION_PANEL = 1000;
        public static final int TYPE_STATUS_BAR = 2000;
        public static final int TYPE_TOAST = 2005;
        public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT = 0;
        public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES = 1;
        public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER = 2;
        public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS = 3;
        public int layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        public android.os.IBinder token;
        public boolean hasSystemUiListeners = false;
        public int inputFeatures = 0;
        public long userActivityTimeout = 0;
        public String packageName;
        public CharSequence accessibilityTitle = "";
        public long accessibilityIdOfAnchor = 0;
        public int windowAnimations = 0;

        public static final int FLAG_ALT_FOCUSABLE_IM = 0x00020000;
        public static final int FLAG_IGNORE_CHEEK_PRESSES = 0x00000800;
        public static final int FLAG_LAYOUT_ATTACHED_IN_DECOR = 0x40000000;
        public static final int FLAG_LAYOUT_INSET_DECOR = 0x00010000;
        public static final int FLAG_LAYOUT_NO_LIMITS = 0x00000200;
        public static final int FLAG_NOT_TOUCH_MODAL = 0x00000020;
        public static final int FLAG_SPLIT_TOUCH = 0x00800000;
        public static final int FLAG_WATCH_OUTSIDE_TOUCH = 0x00040000;
        public static final int SOFT_INPUT_STATE_UNCHANGED = 1;
        public static final int MATCH_PARENT = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        public static final int WRAP_CONTENT = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
        public static final int PRIVATE_FLAG_LAYOUT_CHILD_WINDOW_IN_PARENT_FRAME = 0x00010000;
        public static final int PRIVATE_FLAG_WILL_NOT_REPLACE_ON_RELAUNCH = 0x00008000;
        public static final int TYPE_APPLICATION_SUB_PANEL = 1002;
        public static final int TYPE_APPLICATION_ABOVE_SUB_PANEL = 1005;
        public static final int FIRST_SUB_WINDOW = 1000;
        public static final int LAST_SUB_WINDOW = 1999;

        /** Annotation stub. */
        @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
        public @interface SoftInputModeFlags {}

        public void setTitle(String title) { this.title = title; }
        public void setSurfaceInsets(View view, boolean manual, boolean preservePrevious) {}

        public LayoutParams(int type) {
            this.type = type;
        }
        public LayoutParams(int w, int h, int type, int flags, int format) {
            this.width = w;
            this.height = h;
            this.type = type;
            this.flags = flags;
            this.format = format;
        }
        public void copyFrom(LayoutParams source) {
            if (source != null) {
                this.type = source.type;
                this.flags = source.flags;
                this.format = source.format;
                this.width = source.width;
                this.height = source.height;
                this.gravity = source.gravity;
                this.x = source.x;
                this.y = source.y;
                this.alpha = source.alpha;
                this.softInputMode = source.softInputMode;
                this.title = source.title;
                this.windowAnimations = source.windowAnimations;
                this.packageName = source.packageName;
                this.privateFlags = source.privateFlags;
            }
        }
    }
}
