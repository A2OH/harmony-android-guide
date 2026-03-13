package android.service.voice;

/**
 * Android-compatible VoiceInteractionSession shim. Stub — no-op implementation.
 */
public abstract class VoiceInteractionSession {

    // --- Lifecycle callbacks ---

    public void onShow(Object args, int showFlags) {}

    public void onHide() {}

    // --- Control methods ---

    public void show(Object args, int flags) {}

    public void hide() {}

    public void finish() {}

    public void setContentView(Object view) {}

    // --- Request inner class ---

    public static class Request {
        private String packageName;

        public Request() {}

        public String getCallingPackage() { return packageName; }

        public void cancel() {}

        public boolean isActive() { return false; }
    }
}
