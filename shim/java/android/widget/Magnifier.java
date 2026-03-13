package android.widget;

import android.view.View;

/**
 * Android-compatible Magnifier shim.
 *
 * Widget that can be used to zoom in on a portion of a View. Introduced in
 * Android 9 (API 28). The magnification rendering is not available in the
 * OpenHarmony shim layer; all methods are no-op stubs that allow source code
 * to compile without modification.
 *
 * The preferred way to create a Magnifier is via {@link Builder}:
 * <pre>
 *   Magnifier magnifier = new Magnifier.Builder(view)
 *       .setSize(200, 100)
 *       .setInitialZoom(1.5f)
 *       .build();
 * </pre>
 *
 * The deprecated single-argument constructor is also retained for
 * compatibility with older callers.
 */
public final class Magnifier {

    private final View mView;
    private int mWidth;
    private int mHeight;
    private float mZoom;
    private float mElevation;
    private float mCornerRadius;

    /** @deprecated Prefer {@link Builder}. */
    @Deprecated
    public Magnifier(View view) {
        this.mView = view;
        this.mWidth = 100;
        this.mHeight = 48;
        this.mZoom = 1.25f;
    }

    private Magnifier(Builder b) {
        this.mView = b.mView;
        this.mWidth = b.mWidth;
        this.mHeight = b.mHeight;
        this.mZoom = b.mZoom;
        this.mElevation = b.mElevation;
        this.mCornerRadius = b.mCornerRadius;
    }

    /**
     * Shows the magnifier at the position relative to the magnified view
     * specified by the {@code (x, y)} coordinates.
     *
     * @param x horizontal coordinate of the centre point to magnify, in view-
     *          local pixels
     * @param y vertical coordinate of the centre point to magnify, in view-
     *          local pixels
     */
    public void show(float x, float y) {
        // no-op stub — would render magnified content in an overlay window
    }

    /**
     * Shows the magnifier at the given position, with an additional source
     * offset applied to the content being magnified.
     *
     * @param x          horizontal position in view-local pixels
     * @param y          vertical position in view-local pixels
     * @param sourceX    horizontal offset of the source rectangle
     * @param sourceY    vertical offset of the source rectangle
     */
    public void show(float x, float y, float sourceX, float sourceY) {
        // no-op stub
    }

    /**
     * Dismisses the magnifier from the screen.
     */
    public void dismiss() {
        // no-op stub
    }

    /**
     * Asks the magnifier to update its content. Should be called when the
     * content underneath it changes.
     */
    public void update() {
        // no-op stub
    }

    /** Returns the width of the magnifier surface in pixels. */
    public int getWidth() {
        return mWidth;
    }

    /** Returns the height of the magnifier surface in pixels. */
    public int getHeight() {
        return mHeight;
    }

    /** Returns the zoom factor of the magnifier. */
    public float getZoom() {
        return mZoom;
    }

    /** Returns the elevation (shadow) of the magnifier in pixels. */
    public float getElevation() {
        return mElevation;
    }

    /** Returns the corner radius of the magnifier surface in pixels. */
    public float getCornerRadius() {
        return mCornerRadius;
    }

    /** Returns the view that this magnifier is attached to. */
    public View getView() {
        return mView;
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    /**
     * Builder for a {@link Magnifier} widget with custom options.
     */
    public static final class Builder {

        private final View mView;
        private int mWidth = 100;
        private int mHeight = 48;
        private float mZoom = 1.25f;
        private float mElevation = 0f;
        private float mCornerRadius = 0f;
        private int mSourceBounds = SOURCE_BOUND_MAX_VISIBLE;

        /** @see Magnifier#SOURCE_BOUND_MAX_VISIBLE */
        public static final int SOURCE_BOUND_MAX_VISIBLE = 0;
        /** @see Magnifier#SOURCE_BOUND_MAX_IN_SURFACE */
        public static final int SOURCE_BOUND_MAX_IN_SURFACE = 1;

        /**
         * Creates a builder for a magnifier attached to {@code view}.
         *
         * @param view the view to magnify; must not be {@code null}
         */
        public Builder(View view) {
            if (view == null) throw new NullPointerException("view must not be null");
            this.mView = view;
        }

        /**
         * Sets the size of the magnifier surface.
         *
         * @param width  width in pixels (must be positive)
         * @param height height in pixels (must be positive)
         */
        public Builder setSize(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            return this;
        }

        /**
         * Sets the initial zoom factor of the magnifier.
         *
         * @param zoom zoom factor; must be positive (e.g. 1.5 = 150 %)
         */
        public Builder setInitialZoom(float zoom) {
            this.mZoom = zoom;
            return this;
        }

        /**
         * Sets the elevation (drop-shadow size) of the magnifier.
         *
         * @param elevation elevation in pixels
         */
        public Builder setElevation(float elevation) {
            this.mElevation = elevation;
            return this;
        }

        /**
         * Sets the corner radius of the magnifier surface.
         *
         * @param cornerRadius radius in pixels
         */
        public Builder setCornerRadius(float cornerRadius) {
            this.mCornerRadius = cornerRadius;
            return this;
        }

        /**
         * Defines how the horizontally and vertically source bounds of the
         * magnifier are clamped.
         *
         * @param sourceBounds one of {@link #SOURCE_BOUND_MAX_VISIBLE} or
         *                     {@link #SOURCE_BOUND_MAX_IN_SURFACE}
         */
        public Builder setSourceBounds(int sourceBounds) {
            this.mSourceBounds = sourceBounds;
            return this;
        }

        /** Builds and returns the configured {@link Magnifier}. */
        public Magnifier build() {
            return new Magnifier(this);
        }
    }

    // Public constants mirrored from Builder for callers that reference them
    // on the outer class.
    public static final int SOURCE_BOUND_MAX_VISIBLE = Builder.SOURCE_BOUND_MAX_VISIBLE;
    public static final int SOURCE_BOUND_MAX_IN_SURFACE = Builder.SOURCE_BOUND_MAX_IN_SURFACE;
}
