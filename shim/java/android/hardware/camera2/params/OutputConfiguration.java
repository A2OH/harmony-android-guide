package android.hardware.camera2.params;
import android.hardware.camera2.CameraCaptureSession;
import android.view.Surface;
import android.hardware.camera2.CameraCaptureSession;
import android.view.Surface;

import android.view.Surface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Android-compatible OutputConfiguration shim.
 * Describes an output Surface target for a CameraCaptureSession.
 */
public class OutputConfiguration {

    /** Default maximum number of shared surfaces. */
    private static final int DEFAULT_MAX_SHARED_SURFACE_COUNT = 1;

    private final List<Surface> mSurfaces = new ArrayList<>();
    private int mMaxSharedSurfaceCount = DEFAULT_MAX_SHARED_SURFACE_COUNT;

    /**
     * Create an OutputConfiguration for a single surface.
     *
     * @param surface the target surface
     */
    public OutputConfiguration(Surface surface) {
        if (surface != null) {
            mSurfaces.add(surface);
        }
    }

    /**
     * Add a surface to share with this configuration.
     *
     * @param surface surface to add
     */
    public void addSurface(Surface surface) {
        if (surface != null) {
            mSurfaces.add(surface);
        }
    }

    /**
     * Remove a previously-added surface.
     *
     * @param surface surface to remove
     */
    public void removeSurface(Surface surface) {
        mSurfaces.remove(surface);
    }

    /**
     * @return unmodifiable list of surfaces in this configuration
     */
    public List<Surface> getSurfaces() {
        return Collections.unmodifiableList(mSurfaces);
    }

    /**
     * @return maximum number of surfaces that can be shared (default 1)
     */
    public int getMaxSharedSurfaceCount() {
        return mMaxSharedSurfaceCount;
    }

    @Override
    public String toString() {
        return "OutputConfiguration{surfaces=" + mSurfaces + "}";
    }
}
