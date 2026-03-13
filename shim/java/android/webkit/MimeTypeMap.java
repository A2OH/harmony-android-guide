package android.webkit;

import java.util.HashMap;
import java.util.Map;

/**
 * Shim: android.webkit.MimeTypeMap
 * OH mapping: no direct OH equivalent — utility class; used at Java layer only.
 *
 * Two-way mapping between file extensions and MIME types.  A curated set of
 * common mappings is hardcoded so that code using this class works without an
 * Android runtime.  The bridge does not need to sync this class to the OH side;
 * it is used purely in Java business logic.
 */
public class MimeTypeMap {

    // ── Singleton ──

    private static final MimeTypeMap SINGLETON = new MimeTypeMap();

    /** Returns the singleton MimeTypeMap. */
    public static MimeTypeMap getSingleton() {
        return SINGLETON;
    }

    // ── Tables ──

    /** extension (lowercase, no leading dot) → MIME type */
    private static final Map<String, String> EXT_TO_MIME = new HashMap<>();
    /** MIME type (lowercase) → extension */
    private static final Map<String, String> MIME_TO_EXT = new HashMap<>();

    static {
        // Text / markup
        register("html",  "text/html");
        register("htm",   "text/html");
        register("txt",   "text/plain");
        register("css",   "text/css");
        register("csv",   "text/csv");
        register("xml",   "text/xml");
        register("js",    "application/javascript");

        // Images
        register("jpg",   "image/jpeg");
        register("jpeg",  "image/jpeg");
        register("png",   "image/png");
        register("gif",   "image/gif");
        register("webp",  "image/webp");
        register("svg",   "image/svg+xml");
        register("ico",   "image/x-icon");
        register("bmp",   "image/bmp");
        register("tiff",  "image/tiff");
        register("tif",   "image/tiff");

        // Audio
        register("mp3",   "audio/mpeg");
        register("ogg",   "audio/ogg");
        register("wav",   "audio/wav");
        register("aac",   "audio/aac");
        register("flac",  "audio/flac");
        register("m4a",   "audio/mp4");

        // Video
        register("mp4",   "video/mp4");
        register("webm",  "video/webm");
        register("ogv",   "video/ogg");
        register("avi",   "video/x-msvideo");
        register("mkv",   "video/x-matroska");
        register("3gp",   "video/3gpp");

        // Application
        register("pdf",   "application/pdf");
        register("json",  "application/json");
        register("zip",   "application/zip");
        register("gz",    "application/gzip");
        register("tar",   "application/x-tar");
        register("apk",   "application/vnd.android.package-archive");
        register("bin",   "application/octet-stream");
        register("exe",   "application/octet-stream");
        register("doc",   "application/msword");
        register("docx",  "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        register("xls",   "application/vnd.ms-excel");
        register("xlsx",  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        register("ppt",   "application/vnd.ms-powerpoint");
        register("pptx",  "application/vnd.openxmlformats-officedocument.presentationml.presentation");

        // Fonts
        register("ttf",   "font/ttf");
        register("otf",   "font/otf");
        register("woff",  "font/woff");
        register("woff2", "font/woff2");
    }

    private static void register(String ext, String mime) {
        EXT_TO_MIME.put(ext, mime);
        // Only register the first extension for a MIME type (keep canonical mapping).
        if (!MIME_TO_EXT.containsKey(mime)) {
            MIME_TO_EXT.put(mime, ext);
        }
    }

    // Package-private constructor — use getSingleton().
    MimeTypeMap() {}

    // ── Public API ──

    /**
     * Returns the MIME type for the given file extension, or null if unknown.
     * The extension must not include a leading dot.
     *
     * @param extension  file extension, e.g. {@code "png"} (case-insensitive)
     */
    public String getMimeTypeFromExtension(String extension) {
        if (extension == null) return null;
        return EXT_TO_MIME.get(extension.toLowerCase());
    }

    /**
     * Returns the file extension for the given MIME type, or null if unknown.
     *
     * @param mimeType  MIME type string, e.g. {@code "image/png"} (case-insensitive)
     */
    public String getExtensionFromMimeType(String mimeType) {
        if (mimeType == null) return null;
        return MIME_TO_EXT.get(mimeType.toLowerCase());
    }

    /**
     * Returns true if the given file extension has a known MIME type mapping.
     *
     * @param extension  file extension without leading dot (case-insensitive)
     */
    public boolean hasExtension(String extension) {
        return extension != null && EXT_TO_MIME.containsKey(extension.toLowerCase());
    }

    /**
     * Returns true if the given MIME type has a known extension mapping.
     *
     * @param mimeType  MIME type string (case-insensitive)
     */
    public boolean hasMimeType(String mimeType) {
        return mimeType != null && MIME_TO_EXT.containsKey(mimeType.toLowerCase());
    }

    /**
     * Returns the file extension from a URL by examining the path component,
     * or null if the URL has no recognisable extension.
     */
    public static String getFileExtensionFromUrl(String url) {
        if (url == null || url.isEmpty()) return null;
        // Strip query and fragment.
        int q = url.indexOf('?');
        if (q != -1) url = url.substring(0, q);
        int f = url.indexOf('#');
        if (f != -1) url = url.substring(0, f);
        int dot = url.lastIndexOf('.');
        int slash = url.lastIndexOf('/');
        if (dot > slash && dot < url.length() - 1) {
            return url.substring(dot + 1).toLowerCase();
        }
        return null;
    }
}
