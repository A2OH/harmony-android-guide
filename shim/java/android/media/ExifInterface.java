package android.media;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Android-compatible ExifInterface shim.
 * Provides read/write access to Exif metadata in JPEG/HEIF files.
 * This stub stores attributes in a map; file I/O is no-op.
 */
public class ExifInterface {

    // ── Orientation values ──
    public static final int ORIENTATION_UNDEFINED   = 0;
    public static final int ORIENTATION_NORMAL      = 1;
    public static final int ORIENTATION_FLIP_HORIZONTAL = 2;
    public static final int ORIENTATION_ROTATE_180  = 3;
    public static final int ORIENTATION_FLIP_VERTICAL = 4;
    public static final int ORIENTATION_TRANSPOSE   = 5;
    public static final int ORIENTATION_ROTATE_90   = 6;
    public static final int ORIENTATION_TRANSVERSE  = 7;
    public static final int ORIENTATION_ROTATE_270  = 8;

    // ── Common tag names ──
    public static final String TAG_ORIENTATION            = "Orientation";
    public static final String TAG_DATETIME               = "DateTime";
    public static final String TAG_DATETIME_ORIGINAL      = "DateTimeOriginal";
    public static final String TAG_DATETIME_DIGITIZED     = "DateTimeDigitized";
    public static final String TAG_MAKE                   = "Make";
    public static final String TAG_MODEL                  = "Model";
    public static final String TAG_SOFTWARE               = "Software";
    public static final String TAG_IMAGE_WIDTH            = "ImageWidth";
    public static final String TAG_IMAGE_LENGTH           = "ImageLength";
    public static final String TAG_GPS_LATITUDE           = "GPSLatitude";
    public static final String TAG_GPS_LONGITUDE          = "GPSLongitude";
    public static final String TAG_GPS_LATITUDE_REF       = "GPSLatitudeRef";
    public static final String TAG_GPS_LONGITUDE_REF      = "GPSLongitudeRef";
    public static final String TAG_GPS_ALTITUDE           = "GPSAltitude";
    public static final String TAG_GPS_ALTITUDE_REF       = "GPSAltitudeRef";
    public static final String TAG_GPS_TIMESTAMP          = "GPSTimeStamp";
    public static final String TAG_GPS_DATESTAMP          = "GPSDateStamp";
    public static final String TAG_FLASH                  = "Flash";
    public static final String TAG_FOCAL_LENGTH           = "FocalLength";
    public static final String TAG_EXPOSURE_TIME          = "ExposureTime";
    public static final String TAG_F_NUMBER               = "FNumber";
    public static final String TAG_ISO_SPEED_RATINGS      = "ISOSpeedRatings";
    public static final String TAG_WHITE_BALANCE          = "WhiteBalance";
    public static final String TAG_LIGHT_SOURCE           = "LightSource";
    public static final String TAG_METERING_MODE          = "MeteringMode";
    public static final String TAG_BITS_PER_SAMPLE        = "BitsPerSample";
    public static final String TAG_COMPRESSION            = "Compression";
    public static final String TAG_PHOTOMETRIC_INTERPRETATION = "PhotometricInterpretation";
    public static final String TAG_SAMPLES_PER_PIXEL      = "SamplesPerPixel";
    public static final String TAG_THUMBNAIL_IMAGE_LENGTH = "ThumbnailImageLength";
    public static final String TAG_THUMBNAIL_IMAGE_WIDTH  = "ThumbnailImageWidth";
    public static final String TAG_ARTIST                 = "Artist";
    public static final String TAG_COPYRIGHT              = "Copyright";
    public static final String TAG_EXIF_VERSION           = "ExifVersion";
    public static final String TAG_SUBJECT_DISTANCE       = "SubjectDistance";
    public static final String TAG_DIGITAL_ZOOM_RATIO     = "DigitalZoomRatio";
    public static final String TAG_CONTRAST               = "Contrast";
    public static final String TAG_SATURATION             = "Saturation";
    public static final String TAG_SHARPNESS              = "Sharpness";
    public static final String TAG_PIXEL_X_DIMENSION      = "PixelXDimension";
    public static final String TAG_PIXEL_Y_DIMENSION      = "PixelYDimension";
    public static final String TAG_SCENE_TYPE             = "SceneType";
    public static final String TAG_SCENE_CAPTURE_TYPE     = "SceneCaptureType";
    public static final String TAG_COLOR_SPACE            = "ColorSpace";
    public static final String TAG_USER_COMMENT           = "UserComment";
    public static final String TAG_SUBSEC_TIME            = "SubSecTime";
    public static final String TAG_IMAGE_UNIQUE_ID        = "ImageUniqueID";
    public static final String TAG_SENSING_METHOD         = "SensingMethod";
    public static final String TAG_FILE_SOURCE            = "FileSource";

    // ── White balance values ──
    public static final int WHITEBALANCE_AUTO   = 0;
    public static final int WHITEBALANCE_MANUAL = 1;

    // ── Light source values ──
    public static final int LIGHT_SOURCE_UNKNOWN        = 0;
    public static final int LIGHT_SOURCE_DAYLIGHT       = 1;
    public static final int LIGHT_SOURCE_FLUORESCENT    = 2;
    public static final int LIGHT_SOURCE_TUNGSTEN       = 3;
    public static final int LIGHT_SOURCE_FLASH          = 4;
    public static final int LIGHT_SOURCE_FINE_WEATHER   = 9;
    public static final int LIGHT_SOURCE_CLOUDY_WEATHER = 10;
    public static final int LIGHT_SOURCE_SHADE          = 11;
    public static final int LIGHT_SOURCE_DAYLIGHT_FLUORESCENT = 12;
    public static final int LIGHT_SOURCE_DAY_WHITE_FLUORESCENT = 13;
    public static final int LIGHT_SOURCE_COOL_WHITE_FLUORESCENT = 14;
    public static final int LIGHT_SOURCE_WHITE_FLUORESCENT = 15;

    private final String mFilename;
    private final Map<String, String> mAttributes = new HashMap<>();

    /**
     * Reads Exif attributes from the given file. In this stub the file is not
     * actually parsed; all attributes default to null until set explicitly.
     *
     * @param filename path to the JPEG/HEIF file
     */
    public ExifInterface(String filename) throws IOException {
        if (filename == null) throw new IOException("filename must not be null");
        mFilename = filename;
        System.out.println("[ExifInterface] open: " + filename);
    }

    /**
     * Reads Exif attributes from the given InputStream.
     * In this stub the stream is not actually parsed.
     *
     * @param inputStream source stream (closed by caller)
     */
    public ExifInterface(InputStream inputStream) throws IOException {
        if (inputStream == null) throw new IOException("inputStream must not be null");
        mFilename = "<stream>";
        System.out.println("[ExifInterface] open from stream");
    }

    // ── Attribute access ──

    /**
     * Returns the string value of the specified attribute, or {@code null} if not present.
     */
    public String getAttribute(String tag) {
        return mAttributes.get(tag);
    }

    /**
     * Returns the integer value of the specified attribute.
     *
     * @param tag          Exif tag name
     * @param defaultValue value returned when the tag is absent or not parseable
     */
    public int getAttributeInt(String tag, int defaultValue) {
        String val = mAttributes.get(tag);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Returns the double value of the specified attribute.
     *
     * @param tag          Exif tag name
     * @param defaultValue value returned when the tag is absent or not parseable
     */
    public double getAttributeDouble(String tag, double defaultValue) {
        String val = mAttributes.get(tag);
        if (val == null) return defaultValue;
        try {
            // Rational notation "num/den"
            if (val.contains("/")) {
                String[] parts = val.split("/", 2);
                double num = Double.parseDouble(parts[0].trim());
                double den = Double.parseDouble(parts[1].trim());
                return den == 0 ? defaultValue : num / den;
            }
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the specified attribute.
     *
     * @param tag   Exif tag name
     * @param value string value, or {@code null} to remove the attribute
     */
    public void setAttribute(String tag, String value) {
        if (value == null) {
            mAttributes.remove(tag);
        } else {
            mAttributes.put(tag, value);
        }
    }

    /**
     * Saves modified attributes back to the file.
     * In this stub the call is a no-op (file is not written).
     */
    public void saveAttributes() throws IOException {
        System.out.println("[ExifInterface] saveAttributes: " + mFilename
                + " (" + mAttributes.size() + " tags)");
    }

    /**
     * Returns whether the image has a thumbnail.
     * Always returns {@code false} in this stub.
     */
    public boolean hasThumbnail() {
        return false;
    }

    /**
     * Returns the thumbnail as a byte array, or {@code null} if none.
     */
    public byte[] getThumbnail() {
        return null;
    }

    /**
     * Returns the filename or {@code "<stream>"} when constructed from an InputStream.
     */
    public String getFilename() {
        return mFilename;
    }

    @Override
    public String toString() {
        return "ExifInterface{file=" + mFilename + ", tags=" + mAttributes.size() + "}";
    }
}
