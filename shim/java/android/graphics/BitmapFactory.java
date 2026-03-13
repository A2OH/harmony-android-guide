package android.graphics;
import android.content.res.Resources;
import android.util.Config;
import android.content.res.Resources;
import android.util.Config;

import java.io.InputStream;

/**
 * Shim: android.graphics.BitmapFactory
 * OH mapping: image.createImageSource / image.PixelMap
 *
 * Pure Java stub — decode methods return synthetic Bitmap stubs without
 * reading actual pixel data.  inJustDecodeBounds mode returns null and sets
 * outWidth/outHeight to -1 (real dimensions are unavailable in the shim).
 */
public class BitmapFactory {

    // ── Options ──────────────────────────────────────────────────────────────

    public static class Options {
        /** Scaling factor: 1 = full size, 2 = half size, etc. */
        public int inSampleSize = 1;

        /**
         * If true, decode returns null but sets outWidth/outHeight.
         * In this shim, out-values remain -1 (no actual decoding).
         */
        public boolean inJustDecodeBounds = false;

        /** Width of the decoded bitmap (populated after decode, -1 in shim). */
        public int outWidth  = -1;

        /** Height of the decoded bitmap (populated after decode, -1 in shim). */
        public int outHeight = -1;

        /** Preferred pixel format for the output bitmap. */
        public Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;
    }

    // ── Private constructor — utility class ──────────────────────────────────

    private BitmapFactory() {}

    // ── Decode helpers ───────────────────────────────────────────────────────

    private static Bitmap decodeStub(Options opts, int width, int height) {
        if (opts != null) {
            opts.outWidth  = opts.inJustDecodeBounds ? -1 : width;
            opts.outHeight = opts.inJustDecodeBounds ? -1 : height;
            if (opts.inJustDecodeBounds) return null;
            int sample = Math.max(1, opts.inSampleSize);
            Bitmap.Config cfg = (opts.inPreferredConfig != null)
                    ? opts.inPreferredConfig : Bitmap.Config.ARGB_8888;
            return Bitmap.createBitmap(
                    Math.max(1, width  / sample),
                    Math.max(1, height / sample),
                    cfg);
        }
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    // ── Public decode API ────────────────────────────────────────────────────

    /**
     * Decode the file at the given path into a Bitmap.
     * This shim returns a 1×1 stub; real decoding requires the OH bridge.
     */
    public static Bitmap decodeFile(String pathName) {
        return decodeFile(pathName, null);
    }

    public static Bitmap decodeFile(String pathName, Options opts) {
        if (pathName == null) return null;
        return decodeStub(opts, 1, 1);
    }

    /**
     * Decode an input stream into a Bitmap.
     * Stream is not actually consumed in this shim.
     */
    public static Bitmap decodeStream(InputStream is) {
        return decodeStream(is, null, null);
    }

    public static Bitmap decodeStream(InputStream is, Rect outPadding, Options opts) {
        if (is == null) return null;
        return decodeStub(opts, 1, 1);
    }

    /**
     * Decode a byte array into a Bitmap.
     */
    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        return decodeByteArray(data, offset, length, null);
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts) {
        if (data == null) return null;
        return decodeStub(opts, 1, 1);
    }

    /**
     * Decode a resource into a Bitmap.
     * Resources are not available in the shim environment; returns a 1×1 stub.
     */
    public static Bitmap decodeResource(android.content.res.Resources res, int id) {
        return decodeResource(res, id, null);
    }

    public static Bitmap decodeResource(android.content.res.Resources res, int id, Options opts) {
        return decodeStub(opts, 1, 1);
    }
}
