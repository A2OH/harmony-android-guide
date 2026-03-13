package android.media;

/**
 * Shim stub for android.media.ThumbnailUtils.
 * Provides thumbnail creation utilities for video files and bitmaps.
 */
public class ThumbnailUtils {

    /**
     * Constant used to indicate the option that the source bitmap is recycled
     * after being used to extract the thumbnail.
     */
    public static final int OPTIONS_RECYCLE_INPUT = 0x2;

    private ThumbnailUtils() {}

    /**
     * Create a video thumbnail for a video. May return null if the video is corrupt
     * or the format is not supported.
     *
     * @param filePath the path of the video file
     * @param kind     one of {@link android.provider.MediaStore.Images.Thumbnails#MINI_KIND}
     *                 or {@link android.provider.MediaStore.Images.Thumbnails#MICRO_KIND}
     * @return a Bitmap (represented as Object), or null on failure
     */
    public static Object createVideoThumbnail(String filePath, int kind) {
        return null; // stub
    }

    /**
     * Creates a centered bitmap of the desired size.
     *
     * @param source the input bitmap (represented as Object)
     * @param width  the desired width of the thumbnail
     * @param height the desired height of the thumbnail
     * @return a resized/cropped bitmap (represented as Object), or null if source is null
     */
    public static Object extractThumbnail(Object source, int width, int height) {
        return extractThumbnail(source, width, height, 0);
    }

    /**
     * Creates a centered bitmap of the desired size.
     *
     * @param source  the input bitmap (represented as Object)
     * @param width   the desired width of the thumbnail
     * @param height  the desired height of the thumbnail
     * @param options zero or more options; {@link #OPTIONS_RECYCLE_INPUT} may be set
     * @return a resized/cropped bitmap (represented as Object), or null if source is null
     */
    public static Object extractThumbnail(Object source, int width, int height, int options) {
        if (source == null) {
            return null;
        }
        // stub — return the source unchanged
        return source;
    }
}
