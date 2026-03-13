package android.hardware.display;
import android.view.Display;
import android.view.Display;

/**
 * Android-compatible DisplayManager stub.
 * Manages the properties of attached displays.
 */
public class DisplayManager {

    public static final String DISPLAY_CATEGORY_PRESENTATION =
        "android.hardware.display.category.PRESENTATION";

    public static final int VIRTUAL_DISPLAY_FLAG_PUBLIC    = 1;
    public static final int VIRTUAL_DISPLAY_FLAG_SECURE    = 1 << 1;
    public static final int VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY = 1 << 3;
    public static final int VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR = 1 << 4;

    /** @param context unused in shim */
    public DisplayManager(Object context) {}

    /**
     * Gets the display with the given display ID.
     * Returns a stub Display for ID 0 (default display), null otherwise.
     */
    public Display getDisplay(int displayId) {
        if (displayId == Display.DEFAULT_DISPLAY) {
            return new Display(Display.DEFAULT_DISPLAY);
        }
        return null;
    }

    /**
     * Gets all currently attached displays.
     * Returns an array containing only the default stub display.
     */
    public Display[] getDisplays() {
        return new Display[]{ new Display(Display.DEFAULT_DISPLAY) };
    }

    /**
     * Gets displays filtered by a category flag.
     * Returns an empty array in the shim.
     */
    public Display[] getDisplays(String category) {
        return new Display[0];
    }

    /**
     * Registers a listener to be notified when display state changes.
     * No-op in the shim.
     */
    public void registerDisplayListener(DisplayListener listener, Object handler) {
        // No-op
    }

    /**
     * Unregisters a display listener.
     * No-op in the shim.
     */
    public void unregisterDisplayListener(DisplayListener listener) {
        // No-op
    }

    // -----------------------------------------------------------------------
    // DisplayListener
    // -----------------------------------------------------------------------

    public interface DisplayListener {
        void onDisplayAdded(int displayId);
        void onDisplayRemoved(int displayId);
        void onDisplayChanged(int displayId);
    }

    // -----------------------------------------------------------------------
    // Display (nested stub)
    // -----------------------------------------------------------------------

    public static final class Display {
        public static final int DEFAULT_DISPLAY = 0;
        public static final int INVALID_DISPLAY = -1;

        public static final int STATE_UNKNOWN = 0;
        public static final int STATE_OFF     = 1;
        public static final int STATE_ON      = 2;
        public static final int STATE_DOZE    = 3;

        private final int mDisplayId;

        public Display(int displayId) {
            mDisplayId = displayId;
        }

        public int    getDisplayId()  { return mDisplayId; }
        public int    getState()      { return STATE_ON; }
        public String getName()       { return "Built-in display (shim)"; }
        public int    getWidth()      { return 1080; }
        public int    getHeight()     { return 1920; }
        public float  getRefreshRate(){ return 60.0f; }
        public float  getDensity()    { return 2.0f; }

        public boolean isValid() { return mDisplayId != INVALID_DISPLAY; }

        @Override
        public String toString() {
            return "Display{id=" + mDisplayId + ", " + getWidth() + "x" + getHeight() + "}";
        }
    }
}
