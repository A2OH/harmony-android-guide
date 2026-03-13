package android.service.dreams;
import android.content.res.Configuration;
import android.content.res.Configuration;

/**
 * Android-compatible DreamService shim. Stub — no-op implementation.
 */
public class DreamService {

    private boolean interactive = false;
    private boolean fullscreen = false;
    private boolean screenBright = true;

    // --- Lifecycle callbacks ---

    public void onAttachedToWindow() {}

    public void onDetachedFromWindow() {}

    public void onDreamingStarted() {}

    public void onDreamingStopped() {}

    // --- Configuration ---

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public boolean isInteractive() { return interactive; }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public boolean isFullscreen() { return fullscreen; }

    public void setScreenBright(boolean screenBright) {
        this.screenBright = screenBright;
    }

    public boolean isScreenBright() { return screenBright; }

    public void finish() {}
}
