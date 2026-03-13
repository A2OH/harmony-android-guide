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

    /**
     * Returns the {@link ColorSpace} instance identified by the specified
     * {@link Named} constant. Never returns null.
     */
    public static ColorSpace get(Named name) {
        return new Rgb(name.name(), Model.RGB);
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
