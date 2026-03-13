package android.service.media;

import java.util.List;

/**
 * Android-compatible MediaBrowserService shim. Stub — no-op implementation.
 */
public abstract class MediaBrowserService {

    // --- Abstract methods ---

    public abstract BrowserRoot onGetRoot(String clientPackageName, int clientUid, Object rootHints);

    public abstract void onLoadChildren(String parentId, Result<List<Object>> result);

    // --- BrowserRoot inner class ---

    public static final class BrowserRoot {
        private final String rootId;
        private final Object extras;

        public BrowserRoot(String rootId, Object extras) {
            this.rootId = rootId;
            this.extras = extras;
        }

        public String getRootId() { return rootId; }

        public Object getExtras() { return extras; }
    }

    // --- Result inner class ---

    public static class Result<T> {
        private T value;
        private boolean detached = false;

        public Result() {}

        public void sendResult(T result) {
            this.value = result;
        }

        public void detach() {
            this.detached = true;
        }

        public boolean isDetached() { return detached; }
    }
}
