package android.media.tv;

import java.util.ArrayList;
import java.util.List;

/**
 * Android-compatible TvInputManager shim. Stub for TV input management.
 */
public class TvInputManager {
    public static final int INPUT_STATE_CONNECTED          = 0;
    public static final int INPUT_STATE_CONNECTED_STANDBY  = 1;
    public static final int INPUT_STATE_DISCONNECTED       = 2;

    public List<TvInputInfo> getTvInputList() {
        return new ArrayList<TvInputInfo>();
    }

    public TvInputInfo getTvInputInfo(String inputId) {
        return null;
    }

    // -----------------------------------------------------------------------
    // TvInputCallback abstract inner class
    // -----------------------------------------------------------------------

    public static abstract class Object {
        public void onInputAdded(String inputId) {}
        public void onInputRemoved(String inputId) {}
        public void onInputStateChanged(String inputId, int state) {}
    }
}
