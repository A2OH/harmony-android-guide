package android.graphics;

/**
 * Shim: android.graphics.PorterDuffXfermode
 * OH mapping: drawing.OH_Drawing_BlendMode
 *
 * Extends Xfermode; stores a PorterDuff.Mode.
 * Pure Java stub — no actual compositing performed.
 */
public class PorterDuffXfermode extends Xfermode {

    private final PorterDuff.Mode mode;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * @param mode the Porter-Duff compositing mode to apply
     */
    public PorterDuffXfermode(PorterDuff.Mode mode) {
        this.mode = (mode != null) ? mode : PorterDuff.Mode.SRC_OVER;
    }

    // ── Accessor ─────────────────────────────────────────────────────────────

    public PorterDuff.Mode getMode() { return mode; }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "PorterDuffXfermode(" + mode + ")";
    }
}
