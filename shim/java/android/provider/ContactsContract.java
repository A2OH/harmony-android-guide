package android.provider;
import android.net.Uri;
import android.net.Uri;
import java.net.URL;

import android.net.Uri;

/**
 * Android-compatible ContactsContract shim for OpenHarmony migration.
 * Provides constants and nested classes matching the Android Contacts Provider API.
 */
public class ContactsContract {

    public static final String AUTHORITY = "com.android.contacts";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    /** Contacts table: aggregated view of all raw contacts for a person. */
    public static class Contacts {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.contacts/contacts");
        public static final Uri CONTENT_LOOKUP_URI =
            Uri.parse("content://com.android.contacts/contacts/lookup");
        public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/contact";
        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/contact";

        public static final String _ID = "_id";
        public static final String DISPLAY_NAME = "display_name";
        public static final String DISPLAY_NAME_PRIMARY = "display_name";
        public static final String PHOTO_URI = "photo_uri";
        public static final String PHOTO_THUMBNAIL_URI = "photo_thumb_uri";
        public static final String LOOKUP_KEY = "lookup";
        public static final String HAS_PHONE_NUMBER = "has_phone_number";
        public static final String STARRED = "starred";
        public static final String IN_VISIBLE_GROUP = "in_visible_group";
        public static final String CONTACT_LAST_UPDATED_TIMESTAMP = "contact_last_updated_timestamp";

        public static Uri getLookupUri(long contactId, String lookupKey) {
            return Uri.parse("content://com.android.contacts/contacts/lookup/"
                + lookupKey + "/" + contactId);
        }
    }

    /** RawContacts table: a set of data tied to a single sync account. */
    public static class RawContacts {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.contacts/raw_contacts");
        public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/raw_contact";
        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/raw_contact";

        public static final String _ID = "_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String DELETED = "deleted";
        public static final String STARRED = "starred";
    }

    /** Data table: stores all contact detail rows (phone, email, name, etc.). */
    public static class Data {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.contacts/data");
        public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/data";

        public static final String _ID = "_id";
        public static final String RAW_CONTACT_ID = "raw_contact_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String MIMETYPE = "mimetype";

        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        public static final String DATA5 = "data5";
        public static final String DATA6 = "data6";
        public static final String DATA7 = "data7";
        public static final String DATA8 = "data8";
        public static final String DATA9 = "data9";
        public static final String DATA10 = "data10";
        public static final String DATA11 = "data11";
        public static final String DATA12 = "data12";
        public static final String DATA13 = "data13";
        public static final String DATA14 = "data14";
        public static final String DATA15 = "data15";
    }

    /** CommonDataKinds: typed accessors for well-known MIME types in the Data table. */
    public static class CommonDataKinds {

        /** Phone number data kind. */
        public static class Phone {
            public static final Uri CONTENT_URI =
                Uri.parse("content://com.android.contacts/data/phones");
            public static final Uri CONTENT_FILTER_URI =
                Uri.parse("content://com.android.contacts/data/phones/filter");
            public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/phone_v2";
            public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/phone_v2";

            public static final String MIMETYPE =
                "vnd.android.cursor.item/phone_v2";
            public static final String NUMBER = "data1";
            public static final String TYPE = "data2";
            public static final String LABEL = "data3";
            public static final String NORMALIZED_NUMBER = "data4";

            public static final int TYPE_HOME = 1;
            public static final int TYPE_MOBILE = 2;
            public static final int TYPE_WORK = 3;
            public static final int TYPE_FAX_WORK = 4;
            public static final int TYPE_FAX_HOME = 5;
            public static final int TYPE_OTHER = 7;
            public static final int TYPE_CUSTOM = 0;
        }

        /** Email address data kind. */
        public static class Email {
            public static final Uri CONTENT_URI =
                Uri.parse("content://com.android.contacts/data/emails");
            public static final Uri CONTENT_FILTER_URI =
                Uri.parse("content://com.android.contacts/data/emails/filter");
            public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/email_v2";
            public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/email_v2";

            public static final String MIMETYPE =
                "vnd.android.cursor.item/email_v2";
            public static final String ADDRESS = "data1";
            public static final String TYPE = "data2";
            public static final String LABEL = "data3";

            public static final int TYPE_HOME = 1;
            public static final int TYPE_WORK = 2;
            public static final int TYPE_OTHER = 3;
            public static final int TYPE_MOBILE = 4;
            public static final int TYPE_CUSTOM = 0;
        }

        /** Structured name data kind. */
        public static class StructuredName {
            public static final String MIMETYPE =
                "vnd.android.cursor.item/name";
            public static final String DISPLAY_NAME = "data1";
            public static final String GIVEN_NAME = "data2";
            public static final String FAMILY_NAME = "data3";
            public static final String PREFIX = "data4";
            public static final String MIDDLE_NAME = "data5";
            public static final String SUFFIX = "data6";
            public static final String PHONETIC_GIVEN_NAME = "data7";
            public static final String PHONETIC_MIDDLE_NAME = "data8";
            public static final String PHONETIC_FAMILY_NAME = "data9";
        }

        /** Organization data kind. */
        public static class Organization {
            public static final String MIMETYPE =
                "vnd.android.cursor.item/organization";
            public static final String COMPANY = "data1";
            public static final String TYPE = "data2";
            public static final String TITLE = "data4";

            public static final int TYPE_WORK = 1;
            public static final int TYPE_OTHER = 2;
            public static final int TYPE_CUSTOM = 0;
        }

        /** Postal address data kind. */
        public static class StructuredPostal {
            public static final Uri CONTENT_URI =
                Uri.parse("content://com.android.contacts/data/postals");
            public static final String MIMETYPE =
                "vnd.android.cursor.item/postal-address_v2";
            public static final String FORMATTED_ADDRESS = "data1";
            public static final String TYPE = "data2";
            public static final String STREET = "data4";
            public static final String CITY = "data7";
            public static final String REGION = "data8";
            public static final String POSTCODE = "data9";
            public static final String COUNTRY = "data10";

            public static final int TYPE_HOME = 1;
            public static final int TYPE_WORK = 2;
            public static final int TYPE_OTHER = 3;
            public static final int TYPE_CUSTOM = 0;
        }

        /** Photo data kind. */
        public static class Photo {
            public static final String MIMETYPE =
                "vnd.android.cursor.item/photo";
            public static final String PHOTO = "data15";
        }

        /** Note data kind. */
        public static class Note {
            public static final String MIMETYPE =
                "vnd.android.cursor.item/note";
            public static final String NOTE = "data1";
        }

        /** Website data kind. */
        public static class Website {
            public static final String MIMETYPE =
                "vnd.android.cursor.item/website";
            public static final String URL = "data1";
        }

        /** Event data kind (birthday, anniversary, etc.). */
        public static class Event {
            public static final String MIMETYPE =
                "vnd.android.cursor.item/contact_event";
            public static final String START_DATE = "data1";
            public static final String TYPE = "data2";

            public static final int TYPE_ANNIVERSARY = 1;
            public static final int TYPE_OTHER = 2;
            public static final int TYPE_BIRTHDAY = 3;
            public static final int TYPE_CUSTOM = 0;
        }
    }

    /** Groups table. */
    public static class Groups {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.contacts/groups");
        public static final String _ID = "_id";
        public static final String TITLE = "title";
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
    }

    /** Intents for contacts-related actions. */
    public static class Intents {
        public static final String SEARCH_SUGGESTION_CLICKED =
            "android.provider.Contacts.SEARCH_SUGGESTION_CLICKED";

        public static class Insert {
            public static final String ACTION =
                "android.intent.action.INSERT";
            public static final String FULL_MODE = "full_mode";
            public static final String NAME = "name";
            public static final String PHONETIC_NAME = "phonetic_name";
            public static final String COMPANY = "company";
            public static final String JOB_TITLE = "job_title";
            public static final String NOTES = "notes";
            public static final String PHONE = "phone";
            public static final String PHONE_TYPE = "phone_type";
            public static final String EMAIL = "email";
            public static final String EMAIL_TYPE = "email_type";
            public static final String POSTAL = "postal";
            public static final String POSTAL_TYPE = "postal_type";
        }
    }
}
