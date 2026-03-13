package android.provider;

import android.net.Uri;

/**
 * Android-compatible Telephony provider shim.
 *
 * OH mapping: @ohos.telephony.sms / @ohos.telephony.data
 * Constants and nested-class structure mirror the real android.provider.Telephony.
 * On OH, SMS/MMS access is gated by the READ_MESSAGES permission and accessed
 * through the telephony.sms module rather than a ContentProvider URI.
 */
public final class Telephony {

    private Telephony() {}

    // ── SMS ──────────────────────────────────────────────────────────────────

    public static final class Sms {
        private Sms() {}

        public static final Uri CONTENT_URI = Uri.parse("content://sms");

        // Column names
        public static final String _ID      = "_id";
        public static final String ADDRESS  = "address";
        public static final String BODY     = "body";
        public static final String DATE     = "date";
        public static final String DATE_SENT = "date_sent";
        public static final String READ     = "read";
        public static final String SEEN     = "seen";
        public static final String TYPE     = "type";
        public static final String THREAD_ID = "thread_id";
        public static final String SUBJECT  = "subject";
        public static final String PERSON   = "person";
        public static final String PROTOCOL = "protocol";
        public static final String STATUS   = "status";
        public static final String SERVICE_CENTER = "service_center";

        // TYPE values
        public static final int MESSAGE_TYPE_ALL     = 0;
        public static final int MESSAGE_TYPE_INBOX   = 1;
        public static final int MESSAGE_TYPE_SENT    = 2;
        public static final int MESSAGE_TYPE_DRAFT   = 3;
        public static final int MESSAGE_TYPE_OUTBOX  = 4;
        public static final int MESSAGE_TYPE_FAILED  = 5;
        public static final int MESSAGE_TYPE_QUEUED  = 6;

        // ── SMS.Inbox ─────────────────────────────────────────────────────────

        public static final class Inbox {
            private Inbox() {}

            public static final Uri CONTENT_URI = Uri.parse("content://sms/inbox");
            public static final String DEFAULT_SORT_ORDER = "date DESC";
        }

        // ── SMS.Sent ──────────────────────────────────────────────────────────

        public static final class Sent {
            private Sent() {}

            public static final Uri CONTENT_URI = Uri.parse("content://sms/sent");
            public static final String DEFAULT_SORT_ORDER = "date DESC";
        }

        // ── SMS.Draft ─────────────────────────────────────────────────────────

        public static final class Draft {
            private Draft() {}

            public static final Uri CONTENT_URI = Uri.parse("content://sms/draft");
            public static final String DEFAULT_SORT_ORDER = "date DESC";
        }
    }

    // ── MMS ───────────────────────────────────────────────────────────────────

    public static final class Mms {
        private Mms() {}

        public static final Uri CONTENT_URI = Uri.parse("content://mms");

        public static final String _ID          = "_id";
        public static final String DATE         = "date";
        public static final String DATE_SENT    = "date_sent";
        public static final String MESSAGE_BOX  = "msg_box";
        public static final String READ         = "read";
        public static final String SUBJECT      = "sub";
        public static final String SUBJECT_CHARSET = "sub_cs";
        public static final String CONTENT_TYPE = "ct_t";
        public static final String MESSAGE_CLASS = "m_cls";
        public static final String MESSAGE_TYPE  = "m_type";
        public static final String TRANSACTION_ID = "tr_id";
        public static final String DELIVERY_TIME  = "d_tm";
        public static final String MESSAGE_SIZE   = "m_size";
        public static final String PRIORITY       = "pri";
        public static final String STATUS         = "st";

        // MESSAGE_BOX values
        public static final int MESSAGE_BOX_ALL      = 0;
        public static final int MESSAGE_BOX_INBOX    = 1;
        public static final int MESSAGE_BOX_SENT     = 2;
        public static final int MESSAGE_BOX_DRAFTS   = 3;
        public static final int MESSAGE_BOX_OUTBOX   = 4;
        public static final int MESSAGE_BOX_FAILED   = 5;
    }

    // ── MmsSms ────────────────────────────────────────────────────────────────

    public static final class MmsSms {
        private MmsSms() {}

        public static final Uri CONTENT_URI = Uri.parse("content://mms-sms");
        public static final Uri CONTENT_CONVERSATIONS_URI =
                Uri.parse("content://mms-sms/conversations");

        public static final String TYPE_DISCRIMINATOR_COLUMN = "transport_type";
    }

    // ── Carriers ──────────────────────────────────────────────────────────────

    public static final class Carriers {
        private Carriers() {}

        public static final Uri CONTENT_URI = Uri.parse("content://telephony/carriers");

        public static final String _ID          = "_id";
        public static final String NAME         = "name";
        public static final String APN          = "apn";
        public static final String PROXY        = "proxy";
        public static final String PORT         = "port";
        public static final String MMSPROXY     = "mmsproxy";
        public static final String MMSPORT      = "mmsport";
        public static final String SERVER       = "server";
        public static final String USER         = "user";
        public static final String PASSWORD     = "password";
        public static final String MMSC         = "mmsc";
        public static final String MCC          = "mcc";
        public static final String MNC          = "mnc";
        public static final String NUMERIC      = "numeric";
        public static final String AUTH_TYPE    = "authtype";
        public static final String TYPE         = "type";
        public static final String PROTOCOL     = "protocol";
        public static final String ROAMING_PROTOCOL = "roaming_protocol";
        public static final String CURRENT      = "current";
        public static final String CARRIER_ENABLED = "carrier_enabled";
        public static final String BEARER       = "bearer";
        public static final String MVNO_TYPE    = "mvno_type";
        public static final String MVNO_MATCH_DATA = "mvno_match_data";
        public static final String SUBSCRIPTION_ID = "sub_id";
    }
}
