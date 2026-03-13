package android.service.quicksettings;

/**
 * Android-compatible TileService shim. Stub — no-op implementation.
 */
public abstract class TileService {

    // --- Lifecycle callbacks ---

    public void onStartListening() {}

    public void onStopListening() {}

    public void onClick() {}

    public void onTileAdded() {}

    public void onTileRemoved() {}

    // --- Tile access ---

    public Tile getQsTile() { return new Tile(); }

    // --- Static helpers ---

    public static void requestListeningState(Object context, Object component) {}
}
