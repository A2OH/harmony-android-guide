package android.service.wallpaper;

/**
 * Android-compatible WallpaperService shim. Stub — no-op implementation.
 */
public class WallpaperService {

    public Engine onCreateEngine() { return null; }

    // --- Engine inner class ---

    public class Engine {

        private boolean visible = false;
        private boolean preview = false;

        public void onCreate(Object surfaceHolder) {}

        public void onDestroy() {}

        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
        }

        public void onSurfaceChanged(Object holder, int format, int width, int height) {}

        public void onSurfaceCreated(Object holder) {}

        public void onSurfaceDestroyed(Object holder) {}

        public Object getSurfaceHolder() { return null; }

        public boolean isVisible() { return visible; }

        public boolean isPreview() { return preview; }
    }
}
