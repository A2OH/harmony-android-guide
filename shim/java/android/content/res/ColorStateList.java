package android.content.res;
import android.graphics.Color;
import android.graphics.Color;

/**
 * Shim: android.content.res.ColorStateList
 * OH mapping: ResourceManager color state resources
 *
 * Pure Java stub — stores a default color and an optional state/color table.
 */
public class ColorStateList {

    // ── State ────────────────────────────────────────────────────────────────

    private final int[][] stateSpecs;
    private final int[]   colors;
    private final int     defaultColor;

    // ── Constructors ─────────────────────────────────────────────────────────

    public ColorStateList(int[][] stateSpecs, int[] colors) {
        if (stateSpecs == null || colors == null) {
            throw new IllegalArgumentException("stateSpecs and colors must not be null");
        }
        if (stateSpecs.length != colors.length) {
            throw new IllegalArgumentException("stateSpecs and colors must have same length");
        }
        this.stateSpecs = stateSpecs;
        this.colors     = colors;
        // Last entry is typically the default (empty state spec)
        this.defaultColor = colors.length > 0 ? colors[colors.length - 1] : 0xFF000000;
    }

    private ColorStateList(int color) {
        this.stateSpecs   = new int[][]{new int[0]};
        this.colors       = new int[]{color};
        this.defaultColor = color;
    }

    // ── Factory ──────────────────────────────────────────────────────────────

    /**
     * Creates a ColorStateList with a single constant color.
     */
    public static ColorStateList valueOf(int color) {
        return new ColorStateList(color);
    }

    // ── Color lookup ─────────────────────────────────────────────────────────

    /**
     * Returns the default (fallback) color.
     */
    public int getDefaultColor() {
        return defaultColor;
    }

    /**
     * Returns the color for the given state set.
     * Iterates through stateSpecs; first match wins.
     * Falls back to defaultColor if nothing matches.
     */
    public int getColorForState(int[] stateSet, int defaultColor) {
        for (int i = 0; i < stateSpecs.length; i++) {
            if (stateMatches(stateSpecs[i], stateSet)) {
                return colors[i];
            }
        }
        return defaultColor;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static boolean stateMatches(int[] stateSpec, int[] stateSet) {
        if (stateSpec == null || stateSpec.length == 0) return true; // default matches all
        if (stateSet == null) return false;
        for (int spec : stateSpec) {
            boolean wanted  = (spec > 0);
            int     attr    = wanted ? spec : -spec;
            boolean found   = false;
            for (int s : stateSet) {
                if (s == attr) { found = true; break; }
            }
            if (found != wanted) return false;
        }
        return true;
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    public boolean isStateful() {
        return stateSpecs.length > 1;
    }

    public ColorStateList withAlpha(int alpha) { return this; }

    public boolean hasFocusStateSpecified() { return false; }
    public boolean canApplyTheme() { return false; }
    public ColorStateList obtainForTheme(Resources.Theme theme) { return this; }
    public int getChangingConfigurations() { return 0; }

    @Override
    public String toString() {
        return "ColorStateList(defaultColor=0x" + Integer.toHexString(defaultColor) + ")";
    }
}
