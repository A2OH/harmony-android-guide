package android.view.accessibility;

import java.util.Locale;

/**
 * Android-compatible CaptioningManager shim. Stub — returns default/no-op values.
 */
public class CaptioningManager {

    // -----------------------------------------------------------------------
    // CaptionStyle inner class
    // -----------------------------------------------------------------------
    public static final class CaptionStyle {
        public static final int EDGE_TYPE_NONE        = 0;
        public static final int EDGE_TYPE_OUTLINE     = 1;
        public static final int EDGE_TYPE_DROP_SHADOW = 2;
        public static final int EDGE_TYPE_RAISED      = 3;
        public static final int EDGE_TYPE_DEPRESSED   = 4;

        public static final CaptionStyle DEFAULT = new CaptionStyle(
                0xFFFFFFFF, 0x80000000, EDGE_TYPE_NONE, 0xFF000000, 0x00000000);

        public final int foregroundColor;
        public final int backgroundColor;
        public final int edgeType;
        public final int edgeColor;
        public final int windowColor;

        public CaptionStyle(int foregroundColor, int backgroundColor,
                            int edgeType, int edgeColor, int windowColor) {
            this.foregroundColor = foregroundColor;
            this.backgroundColor = backgroundColor;
            this.edgeType        = edgeType;
            this.edgeColor       = edgeColor;
            this.windowColor     = windowColor;
        }
    }

    // -----------------------------------------------------------------------
    // CaptioningChangeListener inner class
    // -----------------------------------------------------------------------
    public abstract static class CaptioningChangeListener {
        public void onEnabledChanged(boolean enabled) {}
        public void onFontScaleChanged(float fontScale) {}
        public void onLocaleChanged(Locale locale) {}
        public void onUserStyleChanged(CaptionStyle userStyle) {}
    }

    // -----------------------------------------------------------------------
    // CaptioningManager instance methods
    // -----------------------------------------------------------------------
    public boolean isCaptioningEnabled() {
        return false; // stub
    }

    public float getFontScale() {
        return 1.0f; // stub
    }

    public Locale getLocale() {
        return Locale.getDefault(); // stub
    }

    public CaptionStyle getUserStyle() {
        return CaptionStyle.DEFAULT; // stub
    }

    public void addCaptioningChangeListener(CaptioningChangeListener listener) {
        // stub — no-op
    }

    public void removeCaptioningChangeListener(CaptioningChangeListener listener) {
        // stub — no-op
    }
}
