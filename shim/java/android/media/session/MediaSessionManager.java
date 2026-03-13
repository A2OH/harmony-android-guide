package android.media.session;

import java.util.Collections;
import java.util.List;

/**
 * Android-compatible MediaSessionManager shim. Stub — no active session
 * tracking or notification listener integration.
 */
public class MediaSessionManager {

    // ---- Inner interface ────────────────────────────────────────────

    /** Object for changes to the list of active media sessions. */
    public interface OnActiveSessionsChangedListener {
        void onActiveSessionsChanged(java.util.List<Object> controllers);
    }

    // ---- Public API stubs ──────────────────────────────────────────

    /**
     * Returns the list of active media sessions.
     * Stub: always returns an empty list.
     */
    public List<?> getActiveSessions(Object notificationListener) {
        return Collections.emptyList();
    }

    /**
     * Registers a listener for changes to the active sessions list.
     * Stub: no-op.
     */
    public void addOnActiveSessionsChangedListener(Object listener, Object handler) {
        // no-op
    }

    /**
     * Unregisters a previously registered active-sessions listener.
     * Stub: no-op.
     */
    public void removeOnActiveSessionsChangedListener(Object listener) {
        // no-op
    }
}
