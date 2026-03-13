package android.provider;
import android.net.Uri;
import android.renderscript.Type;
import android.net.Uri;
import android.renderscript.Type;

import android.net.Uri;

/**
 * Android-compatible CalendarContract shim.
 * Provides nested classes: Calendars, Events, Instances, Reminders, Attendees.
 * Each exposes a CONTENT_URI and common column name constants.
 */
public class CalendarContract {

    public static final String AUTHORITY = "com.android.calendar";
    public static final String ACTION_VIEW              = "android.intent.action.VIEW";
    public static final String ACTION_EDIT              = "android.intent.action.EDIT";
    public static final String ACTION_INSERT            = "android.intent.action.INSERT";
    public static final String EXTRA_EVENT_BEGIN_TIME   = "beginTime";
    public static final String EXTRA_EVENT_END_TIME     = "endTime";
    public static final String EXTRA_EVENT_ALL_DAY      = "allDay";

    public CalendarContract() {}

    // -------------------------------------------------------------------------
    // Calendars
    // -------------------------------------------------------------------------

    public static class Calendars {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.calendar/calendars");

        public static final String _ID                        = "_id";
        public static final String ACCOUNT_NAME               = "account_name";
        public static final String ACCOUNT_TYPE               = "account_type";
        public static final String NAME                       = "name";
        public static final String CALENDAR_DISPLAY_NAME      = "calendar_displayName";
        public static final String CALENDAR_COLOR             = "calendar_color";
        public static final String CALENDAR_COLOR_KEY         = "calendar_color_index";
        public static final String CALENDAR_ACCESS_LEVEL      = "calendar_access_level";
        public static final String VISIBLE                    = "visible";
        public static final String SYNC_EVENTS                = "sync_events";
        public static final String CALENDAR_LOCATION          = "calendar_location";
        public static final String CALENDAR_TIME_ZONE         = "calendar_timezone";
        public static final String OWNER_ACCOUNT              = "ownerAccount";
        public static final String IS_PRIMARY                 = "isPrimary";
        public static final String CAN_ORGANIZER_RESPOND      = "canOrganizerRespond";
        public static final String CAN_MODIFY_TIME_ZONE       = "canModifyTimeZone";
        public static final String MAX_REMINDERS              = "maxReminders";
        public static final String ALLOWED_REMINDERS          = "allowedReminders";
        public static final String ALLOWED_AVAILABILITY        = "allowedAvailability";
        public static final String ALLOWED_ATTENDEE_TYPES     = "allowedAttendeeTypes";
        public static final String DELETED                    = "deleted";
        public static final String DIRTY                      = "dirty";
        public static final String MUTATORS                   = "mutators";

        // Access level constants
        public static final int CAL_ACCESS_NONE       = 0;
        public static final int CAL_ACCESS_FREE_BUSY  = 100;
        public static final int CAL_ACCESS_READ       = 200;
        public static final int CAL_ACCESS_RESPOND    = 300;
        public static final int CAL_ACCESS_OVERRIDE   = 400;
        public static final int CAL_ACCESS_CONTRIBUTOR = 500;
        public static final int CAL_ACCESS_EDITOR     = 600;
        public static final int CAL_ACCESS_OWNER      = 700;
        public static final int CAL_ACCESS_ROOT       = 800;

        public Calendars() {}
    }

    // -------------------------------------------------------------------------
    // Events
    // -------------------------------------------------------------------------

    public static class Events {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.calendar/events");
        public static final Uri CONTENT_EXCEPTION_URI =
            Uri.parse("content://com.android.calendar/exceptions");

        public static final String _ID                     = "_id";
        public static final String CALENDAR_ID             = "calendar_id";
        public static final String TITLE                   = "title";
        public static final String DESCRIPTION             = "description";
        public static final String EVENT_LOCATION          = "eventLocation";
        public static final String EVENT_COLOR             = "eventColor";
        public static final String EVENT_COLOR_KEY         = "eventColor_index";
        public static final String STATUS                  = "eventStatus";
        public static final String SELF_ATTENDEE_STATUS    = "selfAttendeeStatus";
        public static final String DTSTART                 = "dtstart";
        public static final String DTEND                   = "dtend";
        public static final String EVENT_TIMEZONE          = "eventTimezone";
        public static final String EVENT_END_TIMEZONE      = "eventEndTimezone";
        public static final String DURATION                = "duration";
        public static final String ALL_DAY                 = "allDay";
        public static final String ACCESS_LEVEL            = "accessLevel";
        public static final String AVAILABILITY            = "availability";
        public static final String HAS_ALARM               = "hasAlarm";
        public static final String HAS_EXTENDED_PROPERTIES = "hasExtendedProperties";
        public static final String RRULE                   = "rrule";
        public static final String RDATE                   = "rdate";
        public static final String EXRULE                  = "exrule";
        public static final String EXDATE                  = "exdate";
        public static final String ORIGINAL_ID             = "original_id";
        public static final String ORIGINAL_SYNC_ID        = "original_sync_id";
        public static final String ORIGINAL_INSTANCE_TIME  = "originalInstanceTime";
        public static final String ORIGINAL_ALL_DAY        = "originalAllDay";
        public static final String LAST_DATE               = "lastDate";
        public static final String HAS_ATTENDEE_DATA       = "hasAttendeeData";
        public static final String GUESTS_CAN_MODIFY       = "guestsCanModify";
        public static final String GUESTS_CAN_INVITE_OTHERS = "guestsCanInviteOthers";
        public static final String GUESTS_CAN_SEE_GUESTS   = "guestsCanSeeGuests";
        public static final String ORGANIZER               = "organizer";
        public static final String IS_ORGANIZER            = "isOrganizer";
        public static final String ACCOUNT_NAME            = "account_name";
        public static final String ACCOUNT_TYPE            = "account_type";
        public static final String OWNER_ACCOUNT           = "ownerAccount";
        public static final String DELETED                 = "deleted";
        public static final String DIRTY                   = "dirty";
        public static final String MUTATORS                = "mutators";
        public static final String _SYNC_ID                = "_sync_id";
        public static final String SYNC_DATA1              = "sync_data1";
        public static final String SYNC_DATA2              = "sync_data2";
        public static final String SYNC_DATA3              = "sync_data3";
        public static final String SYNC_DATA4              = "sync_data4";
        public static final String SYNC_DATA5              = "sync_data5";
        public static final String SYNC_DATA6              = "sync_data6";
        public static final String SYNC_DATA7              = "sync_data7";
        public static final String SYNC_DATA8              = "sync_data8";
        public static final String SYNC_DATA9              = "sync_data9";
        public static final String SYNC_DATA10             = "sync_data10";

        // Status constants
        public static final int STATUS_TENTATIVE = 0;
        public static final int STATUS_CONFIRMED = 1;
        public static final int STATUS_CANCELED  = 2;

        // Access level constants
        public static final int ACCESS_DEFAULT    = 0;
        public static final int ACCESS_CONFIDENTIAL = 1;
        public static final int ACCESS_PRIVATE    = 2;
        public static final int ACCESS_PUBLIC     = 3;

        // Availability constants
        public static final int AVAILABILITY_BUSY      = 0;
        public static final int AVAILABILITY_FREE      = 1;
        public static final int AVAILABILITY_TENTATIVE = 2;

        public Events() {}
    }

    // -------------------------------------------------------------------------
    // Instances
    // -------------------------------------------------------------------------

    public static class Instances {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.calendar/instances/when");
        public static final Uri CONTENT_BY_DAY_URI =
            Uri.parse("content://com.android.calendar/instances/whenbyday");
        public static final Uri CONTENT_SEARCH_URI =
            Uri.parse("content://com.android.calendar/instances/search");
        public static final Uri CONTENT_SEARCH_BY_DAY_URI =
            Uri.parse("content://com.android.calendar/instances/searchbyday");

        public static final String _ID          = "_id";
        public static final String EVENT_ID     = "event_id";
        public static final String BEGIN        = "begin";
        public static final String END          = "end";
        public static final String START_DAY    = "startDay";
        public static final String END_DAY      = "endDay";
        public static final String START_MINUTE = "startMinute";
        public static final String END_MINUTE   = "endMinute";

        public Instances() {}

        public static Uri query(Object cr, String[] projection, long begin, long end) {
            return Uri.parse("content://com.android.calendar/instances/when/"
                + begin + "/" + end);
        }

        public static Uri query(Object cr, String[] projection, long begin, long end, String searchQuery) {
            return Uri.parse("content://com.android.calendar/instances/search/"
                + begin + "/" + end);
        }
    }

    // -------------------------------------------------------------------------
    // Reminders
    // -------------------------------------------------------------------------

    public static class Reminders {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.calendar/reminders");

        public static final String _ID       = "_id";
        public static final String EVENT_ID  = "event_id";
        public static final String MINUTES   = "minutes";
        public static final String METHOD    = "method";

        // Method constants
        public static final int METHOD_DEFAULT = 0;
        public static final int METHOD_ALERT   = 1;
        public static final int METHOD_EMAIL   = 2;
        public static final int METHOD_SMS     = 3;
        public static final int METHOD_ALARM   = 4;

        public static final int MINUTES_DEFAULT = -1;

        public Reminders() {}
    }

    // -------------------------------------------------------------------------
    // Attendees
    // -------------------------------------------------------------------------

    public static class Attendees {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.calendar/attendees");

        public static final String _ID                     = "_id";
        public static final String EVENT_ID                = "event_id";
        public static final String ATTENDEE_NAME           = "attendeeName";
        public static final String ATTENDEE_EMAIL          = "attendeeEmail";
        public static final String ATTENDEE_STATUS         = "attendeeStatus";
        public static final String ATTENDEE_RELATIONSHIP   = "attendeeRelationship";
        public static final String ATTENDEE_TYPE           = "attendeeType";
        public static final String ATTENDEE_IDENTITY       = "attendeeIdentity";
        public static final String ATTENDEE_ID_NAMESPACE   = "attendeeIdNamespace";

        // Status constants
        public static final int ATTENDEE_STATUS_NONE     = 0;
        public static final int ATTENDEE_STATUS_ACCEPTED = 1;
        public static final int ATTENDEE_STATUS_DECLINED = 2;
        public static final int ATTENDEE_STATUS_INVITED  = 3;
        public static final int ATTENDEE_STATUS_TENTATIVE = 4;

        // Relationship constants
        public static final int RELATIONSHIP_NONE       = 0;
        public static final int RELATIONSHIP_ATTENDEE   = 1;
        public static final int RELATIONSHIP_ORGANIZER  = 2;
        public static final int RELATIONSHIP_SPEAKER    = 3;
        public static final int RELATIONSHIP_PERFORMER  = 4;

        // Type constants
        public static final int TYPE_NONE      = 0;
        public static final int TYPE_REQUIRED  = 1;
        public static final int TYPE_OPTIONAL  = 2;
        public static final int TYPE_RESOURCE  = 3;

        public Attendees() {}
    }

    // -------------------------------------------------------------------------
    // ExtendedProperties
    // -------------------------------------------------------------------------

    public static class ExtendedProperties {
        public static final Uri CONTENT_URI =
            Uri.parse("content://com.android.calendar/extendedproperties");

        public static final String _ID       = "_id";
        public static final String EVENT_ID  = "event_id";
        public static final String NAME      = "name";
        public static final String VALUE     = "value";

        public ExtendedProperties() {}
    }
}
