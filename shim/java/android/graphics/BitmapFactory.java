package android.graphics;
import android.content.res.Resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Shim: android.graphics.BitmapFactory
 * OH mapping: image.createImageSource / image.PixelMap
 *
 * Pure Java implementation that parses PNG/JPEG image headers to extract
 * dimensions without actually decoding pixels. Creates Bitmap objects with
 * correct width/height metadata.
 */
public class BitmapFactory {

    // ── Options ──────────────────────────────────────────────────────────────

    public static class Options {
        /** Scaling factor: 1 = full size, 2 = half size, etc. */
        public int inSampleSize = 1;

        /**
         * If true, decode returns null but sets outWidth/outHeight/outMimeType.
         */
        public boolean inJustDecodeBounds = false;

        /** Width of the decoded bitmap (populated after decode). */
        public int outWidth  = -1;

        /** Height of the decoded bitmap (populated after decode). */
        public int outHeight = -1;

        /** MIME type of the decoded image (e.g. "image/png", "image/jpeg"). */
        public String outMimeType = null;

        /** Preferred pixel format for the output bitmap. */
        public Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;

        /** Screen density; used by resource loading. */
        public int inScreenDensity;

        /** Target density for scaling. */
        public int inTargetDensity;

        /** Source density. */
        public int inDensity;
    }

    // ── Private constructor — utility class ──────────────────────────────────

    private BitmapFactory() {}

    // ── Image header info ────────────────────────────────────────────────────

    private static class ImageInfo {
        int width;
        int height;
        String mimeType;

        ImageInfo(int w, int h, String mime) {
            this.width = w;
            this.height = h;
            this.mimeType = mime;
        }
    }

    // ── PNG header parsing ───────────────────────────────────────────────────

    private static final byte[] PNG_SIGNATURE = {
        (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private static boolean isPng(byte[] data, int offset, int length) {
        if (length < 24) return false;
        for (int i = 0; i < 8; i++) {
            if (data[offset + i] != PNG_SIGNATURE[i]) return false;
        }
        return true;
    }

    private static ImageInfo parsePng(byte[] data, int offset, int length) {
        // IHDR chunk: width at offset 16, height at offset 20 (from start)
        int w = readInt32BE(data, offset + 16);
        int h = readInt32BE(data, offset + 20);
        return new ImageInfo(w, h, "image/png");
    }

    // ── JPEG header parsing ──────────────────────────────────────────────────

    private static boolean isJpeg(byte[] data, int offset, int length) {
        if (length < 2) return false;
        return (data[offset] & 0xFF) == 0xFF && (data[offset + 1] & 0xFF) == 0xD8;
    }

    private static ImageInfo parseJpeg(byte[] data, int offset, int length) {
        int pos = offset + 2; // skip SOI marker
        int end = offset + length;

        while (pos + 1 < end) {
            if ((data[pos] & 0xFF) != 0xFF) {
                pos++;
                continue;
            }
            int marker = data[pos + 1] & 0xFF;

            // Skip padding 0xFF bytes
            if (marker == 0xFF) {
                pos++;
                continue;
            }

            // SOF0 (0xC0) through SOF2 (0xC2) contain dimensions
            if (marker >= 0xC0 && marker <= 0xC2) {
                // marker + 2 bytes marker, then 2 bytes length, 1 byte precision,
                // 2 bytes height, 2 bytes width
                if (pos + 9 <= end) {
                    int h = readUint16BE(data, pos + 5);
                    int w = readUint16BE(data, pos + 7);
                    return new ImageInfo(w, h, "image/jpeg");
                }
                return null;
            }

            // For other markers, read length and skip
            if (pos + 3 < end) {
                int segLen = readUint16BE(data, pos + 2);
                pos += 2 + segLen;
            } else {
                break;
            }
        }
        return null; // no SOF marker found
    }

    // ── Byte reading helpers ─────────────────────────────────────────────────

    private static int readInt32BE(byte[] data, int off) {
        return ((data[off] & 0xFF) << 24)
             | ((data[off + 1] & 0xFF) << 16)
             | ((data[off + 2] & 0xFF) << 8)
             | (data[off + 3] & 0xFF);
    }

    private static int readUint16BE(byte[] data, int off) {
        return ((data[off] & 0xFF) << 8) | (data[off + 1] & 0xFF);
    }

    // ── Core decode logic ────────────────────────────────────────────────────

    private static Bitmap doDecode(byte[] data, int offset, int length, Options opts) {
        if (data == null || length <= 0) return null;

        // Parse image header
        ImageInfo info = null;
        if (isPng(data, offset, length)) {
            info = parsePng(data, offset, length);
        } else if (isJpeg(data, offset, length)) {
            info = parseJpeg(data, offset, length);
        }

        if (info == null) return null;

        int width = info.width;
        int height = info.height;
        String mimeType = info.mimeType;

        if (opts != null) {
            opts.outWidth = width;
            opts.outHeight = height;
            opts.outMimeType = mimeType;

            if (opts.inJustDecodeBounds) return null;

            int sample = Math.max(1, opts.inSampleSize);
            width = Math.max(1, width / sample);
            height = Math.max(1, height / sample);

            Bitmap.Config cfg = (opts.inPreferredConfig != null)
                    ? opts.inPreferredConfig : Bitmap.Config.ARGB_8888;
            return Bitmap.createBitmap(width, height, cfg);
        }

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    // ── Stream to byte array helper ──────────────────────────────────────────

    private static byte[] readAllBytes(InputStream is) {
        if (is == null) return null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    // ── Public decode API ────────────────────────────────────────────────────

    /**
     * Decode a byte array into a Bitmap, parsing PNG/JPEG headers for dimensions.
     */
    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        return decodeByteArray(data, offset, length, null);
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts) {
        if (data == null) return null;
        return doDecode(data, offset, length, opts);
    }

    /**
     * Decode an input stream into a Bitmap.
     * Reads the entire stream into a byte array, then parses the header.
     */
    public static Bitmap decodeStream(InputStream is) {
        return decodeStream(is, null, null);
    }

    public static Bitmap decodeStream(InputStream is, Rect outPadding, Options opts) {
        if (is == null) return null;
        byte[] data = readAllBytes(is);
        if (data == null) return null;
        return doDecode(data, 0, data.length, opts);
    }

    /**
     * Decode the file at the given path into a Bitmap.
     */
    public static Bitmap decodeFile(String pathName) {
        return decodeFile(pathName, null);
    }

    public static Bitmap decodeFile(String pathName, Options opts) {
        if (pathName == null) return null;
        try {
            FileInputStream fis = new FileInputStream(new File(pathName));
            try {
                return decodeStream(fis, null, opts);
            } finally {
                fis.close();
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Decode a resource into a Bitmap.
     * Resources are not available in the shim environment; returns null.
     */
    public static Bitmap decodeResource(Resources res, int id) {
        return decodeResource(res, id, null);
    }

    public static Bitmap decodeResource(Resources res, int id, Options opts) {
        return null;
    }

    public static Bitmap decodeResourceStream(Resources res, android.util.TypedValue value,
            InputStream is, Rect pad, Options opts) {
        return decodeStream(is, pad, opts);
    }
}
