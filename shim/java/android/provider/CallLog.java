package android.provider;

import android.net.Uri;

/**
 * Android-compatible CallLog shim.
 * Provides call history constants via the Calls inner class.
 */
public class CallLog {

    public static final String AUTHORITY = "call_log";

    public CallLog() {}

    public static class Calls {
        public static final Uri CONTENT_URI =
            Uri.parse("content://call_log/calls");
        public static final Uri CONTENT_FILTER_URI =
            Uri.parse("content://call_log/calls/filter");

        public static final String _ID       = "_id";
        public static final String NUMBER    = "number";
        public static final String DATE      = "date";
        public static final String DURATION  = "duration";
        public static final String TYPE      = "type";
        public static final String NEW       = "new";
        public static final String CACHED_NAME           = "name";
        public static final String CACHED_NUMBER_TYPE    = "numbertype";
        public static final String CACHED_NUMBER_LABEL   = "numberlabel";
        public static final String IS_READ   = "is_read";
        public static final String GEOCODED_LOCATION     = "geocoded_location";
        public static final String PHONE_ACCOUNT_ID      = "subscription_id";

        public static final int INCOMING_TYPE  = 1;
        public static final int OUTGOING_TYPE  = 2;
        public static final int MISSED_TYPE    = 3;
        public static final int VOICEMAIL_TYPE = 4;
        public static final int REJECTED_TYPE  = 5;
        public static final int BLOCKED_TYPE   = 6;

        public Calls() {}
    }
}
