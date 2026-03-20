package android.graphics.fonts;

public final class FontStyle {
    public static final int FONT_SLANT_ITALIC = 0;
    public static final int FONT_SLANT_UPRIGHT = 0;
    public static final int FONT_WEIGHT_BLACK = 0;
    public static final int FONT_WEIGHT_BOLD = 0;
    public static final int FONT_WEIGHT_EXTRA_BOLD = 0;
    public static final int FONT_WEIGHT_EXTRA_LIGHT = 0;
    public static final int FONT_WEIGHT_LIGHT = 0;
    public static final int FONT_WEIGHT_MAX = 0;
    public static final int FONT_WEIGHT_MEDIUM = 0;
    public static final int FONT_WEIGHT_MIN = 0;
    public static final int FONT_WEIGHT_NORMAL = 0;
    public static final int FONT_WEIGHT_SEMI_BOLD = 0;
    public static final int FONT_WEIGHT_THIN = 0;

    private int mWeight;
    private int mSlant;

    public FontStyle() { this(400, 0); }
    public FontStyle(int weight, int slant) { mWeight = weight; mSlant = slant; }

    public int getWeight() { return mWeight; }
    public int getSlant() { return mSlant; }

    /** Score for how closely this style matches target. Lower is better. */
    public int getMatchScore(FontStyle target) {
        if (target == null) return 0;
        int weightDiff = Math.abs(mWeight - target.mWeight);
        int slantDiff = (mSlant == target.mSlant) ? 0 : 1000;
        return weightDiff + slantDiff;
    }
}
