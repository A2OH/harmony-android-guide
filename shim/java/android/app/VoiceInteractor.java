package android.app;
import java.util.concurrent.Executor;

public final class VoiceInteractor {
    public VoiceInteractor() {}

    public Object getActiveRequest(String p0) { return null; }
    public Object[] getActiveRequests() { return null; }
    public boolean isDestroyed() { return false; }
    public void notifyDirectActionsChanged() {}
    public boolean registerOnDestroyedCallback(Executor p0, Runnable p1) { return false; }
    public boolean submitRequest(Object p0) { return false; }
    public boolean submitRequest(Object p0, String p1) { return false; }
    public boolean[] supportsCommands(String[] p0) { return new boolean[0]; }
    public boolean unregisterOnDestroyedCallback(Runnable p0) { return false; }
}
