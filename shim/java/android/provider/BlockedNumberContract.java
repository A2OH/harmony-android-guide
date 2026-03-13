package android.provider;

import android.net.Uri;

/**
 * Android-compatible BlockedNumberContract shim.
 *
 * OH mapping: No direct OpenHarmony equivalent.
 * The blocked-number feature is tightly coupled to the Android Telephony stack.
 * On OpenHarmony, call blocking is managed by the system dialer and is not
 * accessible via a public ContentProvider API.
 */
public final class BlockedNumberContract {

    private BlockedNumberContract() {}

    /** The authority for the blocked-number provider. */
    public static final String AUTHORITY = "com.android.blockednumber";

    /** Base content URI for blocked-number operations. */
    public static final Uri AUTHORITY_URI =
            Uri.parse("content://" + AUTHORITY);

    // ── BlockedNumbers ────────────────────────────────────────────────────────

    /**
     * Constants and column names for the blocked_numbers table.
     */
    public static final class BlockedNumbers {
        private BlockedNumbers() {}

        /** The content URI for the blocked-numbers table. */
        public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY + "/blocked");

        /** MIME type for a directory of blocked numbers. */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/blocked_number";

        /** MIME type for a single blocked number. */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/blocked_number";

        /** Row ID column. */
        public static final String COLUMN_ID = "_id";

        /** The original phone number as entered by the user. */
        public static final String COLUMN_ORIGINAL_NUMBER = "original_number";

        /**
         * The E.164 representation of the number, or null if the number
         * could not be normalized.
         */
        public static final String COLUMN_E164_NUMBER = "e164_number";
    }

    // ── Static helpers ────────────────────────────────────────────────────────

    /**
     * Check whether a phone number is blocked.
     *
     * @param context     application context (typed as Object per A2OH convention)
     * @param phoneNumber the phone number to check
     * @return {@code true} if the number is on the blocked list; stub returns false
     */
    public static boolean isBlocked(Object context, String phoneNumber) {
        return false; // stub
    }

    /**
     * Check whether the current user is allowed to block numbers.
     * On managed-profile devices the primary user controls the blocked list.
     *
     * @param context application context (typed as Object per A2OH convention)
     * @return {@code true} if the current user can block numbers; stub returns true
     */
    public static boolean canCurrentUserBlockNumbers(Object context) {
        return true; // stub
    }

    /**
     * Remove a blocked number.
     *
     * @param context     application context (typed as Object per A2OH convention)
     * @param phoneNumber the phone number to unblock
     * @return the number of rows deleted; stub returns 0
     */
    public static int unblock(Object context, String phoneNumber) {
        return 0; // stub
    }
}
