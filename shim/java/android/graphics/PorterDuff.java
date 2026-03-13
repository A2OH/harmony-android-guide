package android.graphics;

/**
 * Shim: android.graphics.PorterDuff
 * OH mapping: drawing.OH_Drawing_BlendMode
 *
 * Pure Java stub — contains the Mode enum covering all standard
 * Porter-Duff compositing operators plus common blending modes.
 */
public final class PorterDuff {

    // ── Mode enum ─────────────────────────────────────────────────────────────

    public enum Mode {
        /** [0, 0] */
        CLEAR,
        /** [Sa, Sc] */
        SRC,
        /** [Da, Dc] */
        DST,
        /** [Sa + (1 - Sa)*Da, Rc = Sc + (1 - Sa)*Dc] */
        SRC_OVER,
        /** [Sa + (1 - Sa)*Da, Rc = Dc + (1 - Da)*Sc] */
        DST_OVER,
        /** [Sa * Da, Sc * Da] */
        SRC_IN,
        /** [Sa * Da, Dc * Sa] */
        DST_IN,
        /** [Sa * (1 - Da), Sc * (1 - Da)] */
        SRC_OUT,
        /** [Da * (1 - Sa), Dc * (1 - Sa)] */
        DST_OUT,
        /** [Da, Sc * Da + (1 - Sa) * Dc] */
        SRC_ATOP,
        /** [Sa, Dc * Sa + (1 - Da) * Sc] */
        DST_ATOP,
        /** [Sa + Da - 2 * Sa * Da, Sc * (1 - Da) + Dc * (1 - Sa)] */
        XOR,
        /** [Sa + Da - Sa*Da, Sc*(1-Da) + Dc*(1-Sa) + min(Sc,Dc)] */
        DARKEN,
        /** [Sa + Da - Sa*Da, Sc*(1-Da) + Dc*(1-Sa) + max(Sc,Dc)] */
        LIGHTEN,
        /** [Sa * Da, Sc * Dc] */
        MULTIPLY,
        /** [Sa + Da - Sa*Da, Sc + Dc - Sc*Dc] */
        SCREEN,
        /** Adds the two layers, clamping the result. */
        ADD,
        /** Overlays the two layers. */
        OVERLAY
    }

    // ── Private constructor — utility class ──────────────────────────────────

    private PorterDuff() {}
}
