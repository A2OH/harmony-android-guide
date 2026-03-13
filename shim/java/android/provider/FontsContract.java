package android.provider;
import android.graphics.Typeface;
import android.graphics.Typeface;
import java.net.URI;

import android.graphics.Typeface;

/**
 * Android-compatible FontsContract shim.
 *
 * OH mapping: @ohos.font (registerFont / getUIFontConfig)
 * FontsContract provides the mechanism for requesting downloadable fonts via a
 * FontProvider.  On OpenHarmony, register custom fonts with the font module and
 * reference them by family name in ArkUI styles.
 */
public final class FontsContract {

    private FontsContract() {}

    // ── Result codes ───────────────────────────────────────────────────────────

    /** The font was retrieved successfully. */
    public static final int RESULT_CODE_OK = 0;

    /** The provider was not found. */
    public static final int RESULT_CODE_PROVIDER_NOT_FOUND = 1;

    /** The provider had the wrong certificate. */
    public static final int RESULT_CODE_WRONG_CERTIFICATES = 2;

    /** The font was not found. */
    public static final int RESULT_CODE_FONT_NOT_FOUND = 3;

    /** The font is unavailable. */
    public static final int RESULT_CODE_FONT_UNAVAILABLE = 4;

    /** The query was malformed. */
    public static final int RESULT_CODE_MALFORMED_QUERY = 5;

    // ── FontInfo ───────────────────────────────────────────────────────────────

    /**
     * Metadata about a single font returned by a FontProvider.
     */
    public static final class FontInfo {
        private final String mUri;
        private final int    mTtcIndex;
        private final int    mWeight;
        private final boolean mItalic;
        private final int    mResultCode;

        public FontInfo(String uri, int ttcIndex, int weight, boolean italic, int resultCode) {
            mUri        = uri;
            mTtcIndex   = ttcIndex;
            mWeight     = weight;
            mItalic     = italic;
            mResultCode = resultCode;
        }

        /** The URI to the font file. */
        public String getUri()        { return mUri; }

        /** The TTC index for the font (0 for non-collection fonts). */
        public int getTtcIndex()      { return mTtcIndex; }

        /** The weight of this font (100–900). */
        public int getWeight()        { return mWeight; }

        /** Whether this font is italic. */
        public boolean isItalic()     { return mItalic; }

        /** The result code from the provider (see RESULT_CODE_* constants). */
        public int getResultCode()    { return mResultCode; }
    }

    // ── FontRequest stub ────────────────────────────────────────────────────────

    /**
     * Minimal FontRequest stub. Encapsulates a request for a downloadable font.
     */
    public static final class FontRequest {
        private final String mProviderAuthority;
        private final String mProviderPackage;
        private final String mQuery;

        public FontRequest(String providerAuthority, String providerPackage, String query) {
            mProviderAuthority = providerAuthority;
            mProviderPackage   = providerPackage;
            mQuery             = query;
        }

        public String getProviderAuthority() { return mProviderAuthority; }
        public String getProviderPackage()   { return mProviderPackage; }
        public String getQuery()             { return mQuery; }
    }

    // ── FontRequestCallback ────────────────────────────────────────────────────

    /**
     * Object interface for asynchronous font requests.
     */
    public abstract static class FontRequestCallback {

        /** No error; fonts were retrieved successfully. */
        public static final int FAIL_REASON_PROVIDER_NOT_FOUND    = 1;
        public static final int FAIL_REASON_WRONG_CERTIFICATES    = 2;
        public static final int FAIL_REASON_FONT_LOAD_ERROR        = 3;
        public static final int FAIL_REASON_FONT_NOT_FOUND         = 4;
        public static final int FAIL_REASON_FONT_UNAVAILABLE       = 5;
        public static final int FAIL_REASON_MALFORMED_QUERY        = 6;

        /**
         * Called when the Typeface has been retrieved successfully.
         *
         * @param typeface the retrieved Typeface (never null)
         */
        public void onTypefaceRetrieved(Typeface typeface) {}

        /**
         * Called when the font request fails.
         *
         * @param reason one of the FAIL_REASON_* constants defined in this class
         */
        public void onTypefaceRequestFailed(int reason) {}
    }

    // ── Static API ─────────────────────────────────────────────────────────────

    /**
     * Object fonts from a FontProvider.
     *
     * This is a stub implementation.  On OpenHarmony, register custom fonts
     * via {@code @ohos.font.registerFont()} and then reference the family name
     * directly in ArkUI or canvas drawing calls.
     *
     * @param context   application context (typed as Object per A2OH convention)
     * @param request   the FontRequest describing the desired font
     * @param callback  receives the Typeface or an error code asynchronously
     */
    public static void requestFonts(Object context, FontRequest request,
            FontRequestCallback callback) {
        // Stub: immediately signals failure with FONT_NOT_FOUND
        if (callback != null) {
            callback.onTypefaceRequestFailed(
                    FontRequestCallback.FAIL_REASON_FONT_NOT_FOUND);
        }
    }

    /**
     * Synchronous variant: fetch a Typeface for the given FontRequest.
     * Returns null if the font could not be loaded (stub always returns null).
     *
     * @param context application context (typed as Object per A2OH convention)
     * @param request the FontRequest describing the desired font
     * @return the Typeface, or null on failure
     */
    public static Typeface fetchFonts(Object context, FontRequest request) {
        return null; // stub
    }
}
