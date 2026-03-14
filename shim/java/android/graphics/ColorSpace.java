package android.graphics;

/**
 * Shim: android.graphics.ColorSpace
 * OH mapping: effectKit color-management (partial); most color-space conversions
 * are handled natively in OH's rendering pipeline.
 *
 * Abstract base class for all color spaces. Provides the Named enum, the Model
 * enum, and a concrete Rgb inner subclass sufficient to satisfy API signatures
 * without pulling in the full ART implementation.
 */
public abstract class ColorSpace {
    public ColorSpace() {}

    // -------------------------------------------------------------------------
    // Named — well-known color space identifiers
    // -------------------------------------------------------------------------

    /** Identifier for a well-known color space. */
    public enum Named {
        SRGB,
        LINEAR_SRGB,
        EXTENDED_SRGB,
        LINEAR_EXTENDED_SRGB,
        BT709,
        BT2020,
        DCI_P3,
        DISPLAY_P3,
        NTSC_1953,
        SMPTE_C,
        ADOBE_RGB,
        PRO_PHOTO_RGB,
        ACES,
        ACESCG,
        CIE_XYZ,
        CIE_LAB,
        OKLAB
    }

    // -------------------------------------------------------------------------
    // Model — describes how components relate to color channels
    // -------------------------------------------------------------------------

    /** Describes the color model of a color space. */
    public enum Model {
        /** Red, Green, Blue. */
        RGB(3),
        /** Cyan, Magenta, Yellow, Key (Black). */
        CMYK(4),
        /** Luminance, a*, b* (or u*, v*). */
        LAB(3),
        /** X, Y, Z. */
        XYZ(3);

        private final int mComponentCount;

        Model(int componentCount) {
            mComponentCount = componentCount;
        }

        /** Returns the number of components for this model. */
        public int getComponentCount() {
            return mComponentCount;
        }
    }

    // -------------------------------------------------------------------------
    // Static factory
    // -------------------------------------------------------------------------

    // Cache of singleton ColorSpace instances keyed by Named ordinal.
    private static final ColorSpace[] sNamedColorSpaces =
            new ColorSpace[Named.values().length];

    /**
     * Returns the {@link ColorSpace} instance identified by the specified
     * {@link Named} constant. Never returns null.
     *
     * The returned color space has the AOSP-canonical display name and the
     * correct {@link Model} for that color space.
     */
    public static ColorSpace get(Named name) {
        int ordinal = name.ordinal();
        ColorSpace cs = sNamedColorSpaces[ordinal];
        if (cs == null) {
            String displayName;
            Model model;
            switch (name) {
                case SRGB:
                    displayName = "sRGB IEC61966-2.1";
                    model = Model.RGB;
                    break;
                case LINEAR_SRGB:
                    displayName = "sRGB IEC61966-2.1 (Linear)";
                    model = Model.RGB;
                    break;
                case EXTENDED_SRGB:
                    displayName = "scRGB-nl IEC 61966-2-2:2003";
                    model = Model.RGB;
                    break;
                case LINEAR_EXTENDED_SRGB:
                    displayName = "scRGB IEC 61966-2-2:2003";
                    model = Model.RGB;
                    break;
                case BT709:
                    displayName = "Rec. ITU-R BT.709-5";
                    model = Model.RGB;
                    break;
                case BT2020:
                    displayName = "Rec. ITU-R BT.2020-1";
                    model = Model.RGB;
                    break;
                case DCI_P3:
                    displayName = "SMPTE RP 431-2-2007 DCI (P3)";
                    model = Model.RGB;
                    break;
                case DISPLAY_P3:
                    displayName = "Display P3";
                    model = Model.RGB;
                    break;
                case NTSC_1953:
                    displayName = "NTSC (1953)";
                    model = Model.RGB;
                    break;
                case SMPTE_C:
                    displayName = "SMPTE-C RGB";
                    model = Model.RGB;
                    break;
                case ADOBE_RGB:
                    displayName = "Adobe RGB (1998)";
                    model = Model.RGB;
                    break;
                case PRO_PHOTO_RGB:
                    displayName = "ROMM RGB ISO 22028-2:2013";
                    model = Model.RGB;
                    break;
                case ACES:
                    displayName = "SMPTE ST 2065-1:2012 ACES";
                    model = Model.RGB;
                    break;
                case ACESCG:
                    displayName = "Academy S-2014-004 ACEScg";
                    model = Model.RGB;
                    break;
                case CIE_XYZ:
                    displayName = "Generic XYZ";
                    model = Model.XYZ;
                    break;
                case CIE_LAB:
                    displayName = "Generic L*a*b*";
                    model = Model.LAB;
                    break;
                case OKLAB:
                    displayName = "Oklab";
                    model = Model.LAB;
                    break;
                default:
                    displayName = name.name();
                    model = Model.RGB;
                    break;
            }
            cs = new Rgb(displayName, model);
            sNamedColorSpaces[ordinal] = cs;
        }
        return cs;
    }

    // -------------------------------------------------------------------------
    // Abstract interface
    // -------------------------------------------------------------------------

    /** Returns the name of this color space. */
    public String getName() { return null; }

    /** Returns the color model of this color space. */
    public Model getModel() { return null; }

    /** Returns the number of components of this color space's color model. */
    public int getComponentCount() { return 0; }

    /** Returns whether this color space is the sRGB color space or equivalent. */
    public boolean isSrgb() { return false; }

    /** Returns whether this color space has a wide color gamut. */
    public boolean isWideGamut() { return false; }

    @Override
    public String toString() {
        return getName() + " (id=0, model=" + getModel() + ")";
    }

    // -------------------------------------------------------------------------
    // Rgb subclass
    // -------------------------------------------------------------------------

    /**
     * A color space based on the RGB color model.
     */
    public static final class Rgb extends ColorSpace {

        private final String mName;
        private final Model  mModel;
        private final int    mComponentCount;

        /**
         * Construct an Rgb color space with explicit parameters.
         * All arguments are stored verbatim; no validation occurs in this shim.
         */
        public Rgb(String name, Model model) {
            this(name, model, model.getComponentCount());
        }

        public Rgb(String name, Model model, int componentCount) {
            mName           = name;
            mModel          = model;
            mComponentCount = componentCount;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public Model getModel() {
            return mModel;
        }

        @Override
        public int getComponentCount() {
            return mComponentCount;
        }
    }
}
