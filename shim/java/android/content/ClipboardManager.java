package android.content;

import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.content.ClipboardManager — pure Java, in-process mock.
 * Tier 2 — composite mapping.
 *
 * In OpenHarmony the clipboard is accessed via:
 *   @ohos.pasteboard (JS/ArkTS API, system-privilege required)
 *
 * This shim stores the primary clip in memory so that unit-test code that
 * copies/pastes within the same JVM process works without any OH runtime.
 * No OHBridge call is made; real OH integration is left for a higher-tier
 * ArkTS wrapper.
 *
 * Usage pattern on OH (for reference):
 *   import pasteboard from '@ohos.pasteboard';
 *   const pb = pasteboard.getSystemPasteboard();
 *   pb.setPasteData(pasteboard.createPlainTextData(text));
 */
public class ClipboardManager {

    /** Callback interface for primary-clip change notifications. */
    public interface OnPrimaryClipChangedListener {
        /** Called whenever the primary clip on the clipboard changes. */
        void onPrimaryClipChanged();
    }

    // ── State ─────────────────────────────────────────────────────────────────

    private ClipData mPrimaryClip;
    private final List<OnPrimaryClipChangedListener> mListeners = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────────

    /** Package-private: obtain via Context.getSystemService(CLIPBOARD_SERVICE). */
    public ClipboardManager() {
        // nothing to initialise for the in-process mock
    }

    // ── Primary-clip operations ───────────────────────────────────────────────

    /**
     * Sets the current primary clip on the clipboard.
     *
     * @param clip the ClipData to place on the clipboard; must not be null.
     */
    public void setPrimaryClip(ClipData clip) {
        if (clip == null) throw new NullPointerException("clip must not be null");
        mPrimaryClip = clip;
        notifyListeners();
    }

    /**
     * Returns the current primary clip on the clipboard, or null if there is
     * no clip or the application does not have access to the clipboard.
     */
    public ClipData getPrimaryClip() {
        return mPrimaryClip;
    }

    /**
     * Returns true if there is currently a primary clip on the clipboard.
     */
    public boolean hasPrimaryClip() {
        return mPrimaryClip != null;
    }

    /**
     * Returns the description of the current primary clip, or null if there is
     * none.
     */
    public ClipDescription getPrimaryClipDescription() {
        return mPrimaryClip != null ? mPrimaryClip.getDescription() : null;
    }

    // ── Listener management ───────────────────────────────────────────────────

    /**
     * Registers a listener that will be called whenever the primary clip
     * changes.  Duplicate registrations are ignored.
     *
     * @param listener the listener to add
     */
    public void addPrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * Removes a previously registered clip-changed listener.  No-op if the
     * listener was never registered.
     *
     * @param listener the listener to remove
     */
    public void removePrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        mListeners.remove(listener);
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private void notifyListeners() {
        // Iterate over a snapshot to avoid ConcurrentModificationException if a
        // listener removes itself during the callback.
        List<OnPrimaryClipChangedListener> snapshot = new ArrayList<>(mListeners);
        for (OnPrimaryClipChangedListener l : snapshot) {
            try {
                l.onPrimaryClipChanged();
            } catch (Exception e) {
                // Swallow; mirrors Android's behaviour of not crashing the caller.
            }
        }
    }
}
