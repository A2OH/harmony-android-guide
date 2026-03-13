package android.provider;

import android.net.Uri;

/**
 * Android-compatible ContactsContract shim.
 * Provides nested classes for Contacts, CommonDataKinds (Phone, Email), and Data
 * with CONTENT_URI and common column name constants.
 */
public class ContactsContract {

    public static final String AUTHORITY = "com.android.contacts";

    public ContactsContract() {}

    // -------------------------------------------------------------------------
    // Contacts
    // -------------------------------------------------------------------------

    public static class Contacts {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.contacts/contacts");
        public static final Uri CONTENT_LOOKUP_URI =
            Uri.parse("content://com.android.contacts/contacts/lookup");
        public static final Uri CONTENT_VCARD_URI =
            Uri.parse("content://com.android.contacts/contacts/as_vcard");

        public static final String _ID                    = "_id";
        public static final String DISPLAY_NAME           = "display_name";
        public static final String DISPLAY_NAME_PRIMARY   = "display_name";
        public static final String DISPLAY_NAME_ALTERNATIVE = "display_name_alt";
        public static final String PHONETIC_NAME          = "phonetic_name";
        public static final String HAS_PHONE_NUMBER       = "has_phone_number";
        public static final String PHOTO_ID               = "photo_id";
        public static final String PHOTO_URI              = "photo_uri";
        public static final String PHOTO_THUMBNAIL_URI    = "photo_thumb_uri";
        public static final String LOOKUP_KEY             = "lookup";
        public static final String LAST_TIME_CONTACTED    = "last_time_contacted";
        public static final String TIMES_CONTACTED        = "times_contacted";
        public static final String STARRED                = "starred";
        public static final String IN_VISIBLE_GROUP       = "in_visible_group";
        public static final String CONTACT_PRESENCE       = "contact_presence";
        public static final String SEND_TO_VOICEMAIL      = "send_to_voicemail";
        public static final String CUSTOM_RINGTONE        = "custom_ringtone";
        public static final String CONTACT_STATUS         = "contact_status";

        public Contacts() {}

        public static Uri getLookupUri(long contactId, String lookupKey) {
            return Uri.parse("content://com.android.contacts/contacts/lookup/"
                + lookupKey + "/" + contactId);
        }
    }

    // -------------------------------------------------------------------------
    // RawContacts
    // -------------------------------------------------------------------------

    public static class RawContacts {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.contacts/raw_contacts");

        public static final String _ID           = "_id";
        public static final String CONTACT_ID    = "contact_id";
        public static final String ACCOUNT_NAME  = "account_name";
        public static final String ACCOUNT_TYPE  = "account_type";
        public static final String DISPLAY_NAME  = "display_name";
        public static final String DELETED       = "deleted";
        public static final String STARRED       = "starred";

        public RawContacts() {}
    }

    // -------------------------------------------------------------------------
    // Data
    // -------------------------------------------------------------------------

    public static class Data {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.contacts/data");

        public static final String _ID               = "_id";
        public static final String MIMETYPE          = "mimetype";
        public static final String RAW_CONTACT_ID    = "raw_contact_id";
        public static final String IS_PRIMARY        = "is_primary";
        public static final String IS_SUPER_PRIMARY  = "is_super_primary";
        public static final String DATA_VERSION      = "data_version";
        public static final String DATA1             = "data1";
        public static final String DATA2             = "data2";
        public static final String DATA3             = "data3";
        public static final String DATA4             = "data4";
        public static final String DATA5             = "data5";
        public static final String DATA6             = "data6";
        public static final String DATA7             = "data7";
        public static final String DATA8             = "data8";
        public static final String DATA9             = "data9";
        public static final String DATA10            = "data10";
        public static final String DATA11            = "data11";
        public static final String DATA12            = "data12";
        public static final String DATA13            = "data13";
        public static final String DATA14            = "data14";
        public static final String DATA15            = "data15";
        public static final String SYNC1             = "sync1";
        public static final String SYNC2             = "sync2";
        public static final String SYNC3             = "sync3";
        public static final String SYNC4             = "sync4";
        public static final String CONTACT_ID        = "contact_id";
        public static final String DISPLAY_NAME      = "display_name";
        public static final String PHOTO_URI         = "photo_uri";

        public Data() {}
    }

    // -------------------------------------------------------------------------
    // CommonDataKinds
    // -------------------------------------------------------------------------

    public static class CommonDataKinds {

        public static class Phone {
            public static final Uri CONTENT_URI =
                Uri.parse("content://com.android.contacts/data/phones");
            public static final Uri CONTENT_FILTER_URI =
                Uri.parse("content://com.android.contacts/data/phones/filter");

            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/phone_v2";
            public static final String CONTENT_TYPE      = "vnd.android.cursor.dir/phone_v2";

            public static final String NUMBER            = "data1";
            public static final String TYPE              = "data2";
            public static final String LABEL             = "data3";
            public static final String NORMALIZED_NUMBER = "data4";

            public static final int TYPE_HOME      = 1;
            public static final int TYPE_MOBILE    = 2;
            public static final int TYPE_WORK      = 3;
            public static final int TYPE_FAX_WORK  = 4;
            public static final int TYPE_FAX_HOME  = 5;
            public static final int TYPE_PAGER     = 6;
            public static final int TYPE_OTHER     = 7;
            public static final int TYPE_CUSTOM    = 0;

            public Phone() {}

            public static String getTypeLabel(Object res, int type, CharSequence label) {
                switch (type) {
                    case TYPE_HOME:   return "Home";
                    case TYPE_MOBILE: return "Mobile";
                    case TYPE_WORK:   return "Work";
                    default:          return label != null ? label.toString() : "Other";
                }
            }
        }

        public static class Email {
            public static final Uri CONTENT_URI =
                Uri.parse("content://com.android.contacts/data/emails");
            public static final Uri CONTENT_LOOKUP_URI =
                Uri.parse("content://com.android.contacts/data/emails/lookup");
            public static final Uri CONTENT_FILTER_URI =
                Uri.parse("content://com.android.contacts/data/emails/filter");

            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/email_v2";
            public static final String CONTENT_TYPE      = "vnd.android.cursor.dir/email_v2";

            public static final String ADDRESS   = "data1";
            public static final String TYPE      = "data2";
            public static final String LABEL     = "data3";
            public static final String DISPLAY_NAME = "data4";

            public static final int TYPE_HOME    = 1;
            public static final int TYPE_WORK    = 2;
            public static final int TYPE_OTHER   = 3;
            public static final int TYPE_MOBILE  = 4;
            public static final int TYPE_CUSTOM  = 0;

            public Email() {}

            public static String getTypeLabel(Object res, int type, CharSequence label) {
                switch (type) {
                    case TYPE_HOME:  return "Home";
                    case TYPE_WORK:  return "Work";
                    case TYPE_OTHER: return "Other";
                    default:         return label != null ? label.toString() : "Other";
                }
            }
        }

        public static class StructuredName {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/name";

            public static final String DISPLAY_NAME  = "data1";
            public static final String GIVEN_NAME    = "data2";
            public static final String FAMILY_NAME   = "data3";
            public static final String PREFIX        = "data4";
            public static final String MIDDLE_NAME   = "data5";
            public static final String SUFFIX        = "data6";
            public static final String PHONETIC_GIVEN_NAME  = "data7";
            public static final String PHONETIC_MIDDLE_NAME = "data8";
            public static final String PHONETIC_FAMILY_NAME = "data9";

            public StructuredName() {}
        }

        public static class StructuredPostal {
            public static final Uri CONTENT_URI =
                Uri.parse("content://com.android.contacts/data/postals");

            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/postal-address_v2";

            public static final String FORMATTED_ADDRESS = "data1";
            public static final String TYPE              = "data2";
            public static final String LABEL             = "data3";
            public static final String STREET            = "data4";
            public static final String POBOX             = "data5";
            public static final String NEIGHBORHOOD      = "data6";
            public static final String CITY              = "data7";
            public static final String REGION            = "data8";
            public static final String POSTCODE          = "data9";
            public static final String COUNTRY           = "data10";

            public static final int TYPE_HOME   = 1;
            public static final int TYPE_WORK   = 2;
            public static final int TYPE_OTHER  = 3;
            public static final int TYPE_CUSTOM = 0;

            public StructuredPostal() {}
        }

        public static class Organization {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/organization";

            public static final String COMPANY   = "data1";
            public static final String TYPE      = "data2";
            public static final String LABEL     = "data3";
            public static final String TITLE     = "data4";
            public static final String DEPARTMENT = "data5";

            public static final int TYPE_WORK   = 1;
            public static final int TYPE_OTHER  = 2;
            public static final int TYPE_CUSTOM = 0;

            public Organization() {}
        }

        public static class Note {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/note";
            public static final String NOTE = "data1";

            public Note() {}
        }

        public static class Photo {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/photo";
            public static final String PHOTO = "data15";
            public static final String PHOTO_FILE_ID = "data14";

            public Photo() {}
        }

        public static class Website {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/website";
            public static final String URL   = "data1";
            public static final String TYPE  = "data2";
            public static final String LABEL = "data3";

            public static final int TYPE_HOMEPAGE = 1;
            public static final int TYPE_BLOG     = 2;
            public static final int TYPE_PROFILE  = 3;
            public static final int TYPE_HOME     = 4;
            public static final int TYPE_WORK     = 5;
            public static final int TYPE_FTP      = 6;
            public static final int TYPE_OTHER    = 7;
            public static final int TYPE_CUSTOM   = 0;

            public Website() {}
        }
    }

    // -------------------------------------------------------------------------
    // Groups
    // -------------------------------------------------------------------------

    public static class Groups {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.contacts/groups");

        public static final String _ID           = "_id";
        public static final String TITLE         = "title";
        public static final String NOTES         = "notes";
        public static final String ACCOUNT_NAME  = "account_name";
        public static final String ACCOUNT_TYPE  = "account_type";
        public static final String DELETED       = "deleted";
        public static final String GROUP_VISIBLE = "group_visible";

        public Groups() {}
    }

    // -------------------------------------------------------------------------
    // PhoneLookup
    // -------------------------------------------------------------------------

    public static class PhoneLookup {
        public static final Uri CONTENT_FILTER_URI =
            Uri.parse("content://com.android.contacts/phone_lookup");

        public static final String _ID           = "_id";
        public static final String NUMBER        = "number";
        public static final String TYPE          = "type";
        public static final String LABEL         = "label";
        public static final String DISPLAY_NAME  = "display_name";
        public static final String CONTACT_ID    = "contact_id";
        public static final String LOOKUP_KEY    = "lookup_key";
        public static final String PHOTO_URI     = "photo_uri";

        public PhoneLookup() {}
    }
}
