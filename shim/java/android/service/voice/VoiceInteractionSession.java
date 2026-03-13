package android.service.voice;
import android.service.controls.Control;
import android.service.controls.Control;

/**
 * Android-compatible VoiceInteractionSession shim. Stub — no-op implementation.
 */
public class VoiceInteractionSession {

    // --- Lifecycle callbacks ---

    public void onShow(Object args, int showFlags) {}

    public void onHide() {}

    // --- Control methods ---

    public void show(Object args, int flags) {}

    public void hide() {}

    public void finish() {}

    public void setContentView(Object view) {}

    // --- Request inner class ---

    public static class Object {
        private String packageName;

        public Object() {}

        public String getCallingPackage() { return packageName; }

        public void cancel() {}

        public boolean isActive() { return false; }
    }
}
