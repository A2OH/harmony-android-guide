package android.app;

/**
 * Android-compatible GameManager shim. Stub — returns default/no-op values.
 */
public class GameManager {

    /** The game mode is not supported for this application. */
    public static final int GAME_MODE_UNSUPPORTED = 0;

    /** Standard game mode. */
    public static final int GAME_MODE_STANDARD = 1;

    /** Performance game mode — favours higher frame-rates / graphics fidelity. */
    public static final int GAME_MODE_PERFORMANCE = 2;

    /** Battery game mode — reduces power consumption. */
    public static final int GAME_MODE_BATTERY = 3;

    /**
     * Returns the active game mode for the current application.
     *
     * @return {@link #GAME_MODE_UNSUPPORTED} — stub implementation
     */
    public int getGameMode() {
        return GAME_MODE_UNSUPPORTED; // stub
    }

    /**
     * Sets the game state.
     *
     * @param gameState a {@code android.app.GameState} instance (typed as Object to avoid
     *                  hard dependency on the platform class)
     */
    public void setGameState(Object gameState) {
        // stub — no-op
    }
}
