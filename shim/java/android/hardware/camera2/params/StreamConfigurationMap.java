package android.hardware.camera2.params;

import android.graphics.Point;

/**
 * Android-compatible StreamConfigurationMap shim. Stub implementation.
 */
public class StreamConfigurationMap {

    public StreamConfigurationMap() {}

    /**
     * Get the image format output formats in this stream configuration.
     * @return array of output formats (empty stub)
     */
    public int[] getOutputFormats() {
        return new int[0];
    }

    /**
     * Get the supported output sizes for the given format.
     * @param format image format
     * @return array of sizes as Point(width,height), or null if unsupported
     */
    public Point[] getOutputSizes(int format) {
        return null;
    }

    /**
     * Get the minimum frame duration for the given format and size.
     * @param format image format
     * @param size   output size as Point(width,height)
     * @return duration in nanoseconds (0 stub)
     */
    public long getOutputMinFrameDuration(int format, Point size) {
        return 0L;
    }

    /**
     * Get the stall duration for the given format and size.
     * @param format image format
     * @param size   output size as Point(width,height)
     * @return stall duration in nanoseconds (0 stub)
     */
    public long getOutputStallDuration(int format, Point size) {
        return 0L;
    }

    /**
     * Check whether a given format is supported for output.
     * @param format image format
     * @return false (stub)
     */
    public boolean isOutputSupportedFor(int format) {
        return false;
    }
}
