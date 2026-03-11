# Android 11 (API 30) Public API Enumeration: Android Provider Service

Generated from `frameworks/base/api/current.txt`

## 概要

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.provider` | 215 | 212 | 1627 | 45 |
| `android.service.autofill` | 39 | 68 | 29 | 18 |
| `android.service.carrier` | 9 | 21 | 12 | 10 |
| `android.service.chooser` | 2 | 0 | 0 | 0 |
| `android.service.controls` | 5 | 8 | 65 | 5 |
| `android.service.controls.actions` | 5 | 9 | 11 | 8 |
| `android.service.controls.templates` | 7 | 17 | 18 | 7 |
| `android.service.dreams` | 1 | 42 | 2 | 1 |
| `android.service.media` | 4 | 17 | 4 | 3 |
| `android.service.notification` | 8 | 97 | 60 | 7 |
| `android.service.quickaccesswallet` | 9 | 24 | 5 | 7 |
| `android.service.quicksettings` | 2 | 26 | 7 | 1 |
| `android.service.restrictions` | 1 | 2 | 0 | 1 |
| `android.service.textservice` | 1 | 10 | 1 | 2 |
| `android.service.voice` | 15 | 92 | 28 | 6 |
| `android.service.vr` | 1 | 3 | 1 | 1 |
| `android.service.wallpaper` | 2 | 24 | 2 | 2 |
| **TOTAL** | **326** | **672** | **1872** | **124** |

---

## Package: `android.provider`

### `class final AlarmClock`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AlarmClock()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_DISMISS_ALARM = "android.intent.action.DISMISS_ALARM"` |  |
| `static final String ACTION_DISMISS_TIMER = "android.intent.action.DISMISS_TIMER"` |  |
| `static final String ACTION_SET_ALARM = "android.intent.action.SET_ALARM"` |  |
| `static final String ACTION_SET_TIMER = "android.intent.action.SET_TIMER"` |  |
| `static final String ACTION_SHOW_ALARMS = "android.intent.action.SHOW_ALARMS"` |  |
| `static final String ACTION_SHOW_TIMERS = "android.intent.action.SHOW_TIMERS"` |  |
| `static final String ACTION_SNOOZE_ALARM = "android.intent.action.SNOOZE_ALARM"` |  |
| `static final String ALARM_SEARCH_MODE_ALL = "android.all"` |  |
| `static final String ALARM_SEARCH_MODE_LABEL = "android.label"` |  |
| `static final String ALARM_SEARCH_MODE_NEXT = "android.next"` |  |
| `static final String ALARM_SEARCH_MODE_TIME = "android.time"` |  |
| `static final String EXTRA_ALARM_SEARCH_MODE = "android.intent.extra.alarm.SEARCH_MODE"` |  |
| `static final String EXTRA_ALARM_SNOOZE_DURATION = "android.intent.extra.alarm.SNOOZE_DURATION"` |  |
| `static final String EXTRA_DAYS = "android.intent.extra.alarm.DAYS"` |  |
| `static final String EXTRA_HOUR = "android.intent.extra.alarm.HOUR"` |  |
| `static final String EXTRA_IS_PM = "android.intent.extra.alarm.IS_PM"` |  |
| `static final String EXTRA_LENGTH = "android.intent.extra.alarm.LENGTH"` |  |
| `static final String EXTRA_MESSAGE = "android.intent.extra.alarm.MESSAGE"` |  |
| `static final String EXTRA_MINUTES = "android.intent.extra.alarm.MINUTES"` |  |
| `static final String EXTRA_RINGTONE = "android.intent.extra.alarm.RINGTONE"` |  |
| `static final String EXTRA_SKIP_UI = "android.intent.extra.alarm.SKIP_UI"` |  |
| `static final String EXTRA_VIBRATE = "android.intent.extra.alarm.VIBRATE"` |  |
| `static final String VALUE_RINGTONE_SILENT = "silent"` |  |

---

### `interface BaseColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String _COUNT = "_count"` |  |
| `static final String _ID = "_id"` |  |

---

### `class BlockedNumberContract`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUTHORITY = "com.android.blockednumber"` |  |
| `static final android.net.Uri AUTHORITY_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean canCurrentUserBlockNumbers(android.content.Context)` |  |

---

### `class static BlockedNumberContract.BlockedNumbers`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String COLUMN_E164_NUMBER = "e164_number"` |  |
| `static final String COLUMN_ID = "_id"` |  |
| `static final String COLUMN_ORIGINAL_NUMBER = "original_number"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/blocked_number"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/blocked_number"` |  |
| `static final android.net.Uri CONTENT_URI` |  |

---

### `class Browser`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Browser()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_APPLICATION_ID = "com.android.browser.application_id"` |  |
| `static final String EXTRA_CREATE_NEW_TAB = "create_new_tab"` |  |
| `static final String EXTRA_HEADERS = "com.android.browser.headers"` |  |
| `static final String INITIAL_ZOOM_LEVEL = "browser.initialZoomLevel"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static final void sendString(android.content.Context, String)` |  |

---

### `class final CalendarContract`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACCOUNT_TYPE_LOCAL = "LOCAL"` |  |
| `static final String ACTION_EVENT_REMINDER = "android.intent.action.EVENT_REMINDER"` |  |
| `static final String ACTION_HANDLE_CUSTOM_EVENT = "android.provider.calendar.action.HANDLE_CUSTOM_EVENT"` |  |
| `static final String ACTION_VIEW_MANAGED_PROFILE_CALENDAR_EVENT = "android.provider.calendar.action.VIEW_MANAGED_PROFILE_CALENDAR_EVENT"` |  |
| `static final String AUTHORITY = "com.android.calendar"` |  |
| `static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String EXTRA_CUSTOM_APP_URI = "customAppUri"` |  |
| `static final String EXTRA_EVENT_ALL_DAY = "allDay"` |  |
| `static final String EXTRA_EVENT_BEGIN_TIME = "beginTime"` |  |
| `static final String EXTRA_EVENT_END_TIME = "endTime"` |  |
| `static final String EXTRA_EVENT_ID = "id"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean startViewCalendarEventInManagedProfile(@NonNull android.content.Context, long, long, long, boolean, int)` |  |

---

### `class static final CalendarContract.Attendees`

- **实现：** `android.provider.BaseColumns android.provider.CalendarContract.AttendeesColumns android.provider.CalendarContract.EventsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.database.Cursor query(android.content.ContentResolver, long, String[])` |  |

---

### `interface static CalendarContract.AttendeesColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ATTENDEE_EMAIL = "attendeeEmail"` |  |
| `static final String ATTENDEE_IDENTITY = "attendeeIdentity"` |  |
| `static final String ATTENDEE_ID_NAMESPACE = "attendeeIdNamespace"` |  |
| `static final String ATTENDEE_NAME = "attendeeName"` |  |
| `static final String ATTENDEE_RELATIONSHIP = "attendeeRelationship"` |  |
| `static final String ATTENDEE_STATUS = "attendeeStatus"` |  |
| `static final int ATTENDEE_STATUS_ACCEPTED = 1` |  |
| `static final int ATTENDEE_STATUS_DECLINED = 2` |  |
| `static final int ATTENDEE_STATUS_INVITED = 3` |  |
| `static final int ATTENDEE_STATUS_NONE = 0` |  |
| `static final int ATTENDEE_STATUS_TENTATIVE = 4` |  |
| `static final String ATTENDEE_TYPE = "attendeeType"` |  |
| `static final String EVENT_ID = "event_id"` |  |
| `static final int RELATIONSHIP_ATTENDEE = 1` |  |
| `static final int RELATIONSHIP_NONE = 0` |  |
| `static final int RELATIONSHIP_ORGANIZER = 2` |  |
| `static final int RELATIONSHIP_PERFORMER = 3` |  |
| `static final int RELATIONSHIP_SPEAKER = 4` |  |
| `static final int TYPE_NONE = 0` |  |
| `static final int TYPE_OPTIONAL = 2` |  |
| `static final int TYPE_REQUIRED = 1` |  |
| `static final int TYPE_RESOURCE = 3` |  |

---

### `class static final CalendarContract.CalendarAlerts`

- **实现：** `android.provider.BaseColumns android.provider.CalendarContract.CalendarAlertsColumns android.provider.CalendarContract.CalendarColumns android.provider.CalendarContract.EventsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final android.net.Uri CONTENT_URI_BY_INSTANCE` |  |

---

### `interface static CalendarContract.CalendarAlertsColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ALARM_TIME = "alarmTime"` |  |
| `static final String BEGIN = "begin"` |  |
| `static final String CREATION_TIME = "creationTime"` |  |
| `static final String DEFAULT_SORT_ORDER = "begin ASC,title ASC"` |  |
| `static final String END = "end"` |  |
| `static final String EVENT_ID = "event_id"` |  |
| `static final String MINUTES = "minutes"` |  |
| `static final String NOTIFY_TIME = "notifyTime"` |  |
| `static final String RECEIVED_TIME = "receivedTime"` |  |
| `static final String STATE = "state"` |  |
| `static final int STATE_DISMISSED = 2` |  |
| `static final int STATE_FIRED = 1` |  |
| `static final int STATE_SCHEDULED = 0` |  |

---

### `class static final CalendarContract.CalendarCache`

- **实现：** `android.provider.CalendarContract.CalendarCacheColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String KEY_TIMEZONE_INSTANCES = "timezoneInstances"` |  |
| `static final String KEY_TIMEZONE_INSTANCES_PREVIOUS = "timezoneInstancesPrevious"` |  |
| `static final String KEY_TIMEZONE_TYPE = "timezoneType"` |  |
| `static final String TIMEZONE_TYPE_AUTO = "auto"` |  |
| `static final String TIMEZONE_TYPE_HOME = "home"` |  |
| `static final android.net.Uri URI` |  |

---

### `interface static CalendarContract.CalendarCacheColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String KEY = "key"` |  |
| `static final String VALUE = "value"` |  |

---

### `interface static CalendarContract.CalendarColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ALLOWED_ATTENDEE_TYPES = "allowedAttendeeTypes"` |  |
| `static final String ALLOWED_AVAILABILITY = "allowedAvailability"` |  |
| `static final String ALLOWED_REMINDERS = "allowedReminders"` |  |
| `static final String CALENDAR_ACCESS_LEVEL = "calendar_access_level"` |  |
| `static final String CALENDAR_COLOR = "calendar_color"` |  |
| `static final String CALENDAR_COLOR_KEY = "calendar_color_index"` |  |
| `static final String CALENDAR_DISPLAY_NAME = "calendar_displayName"` |  |
| `static final String CALENDAR_TIME_ZONE = "calendar_timezone"` |  |
| `static final int CAL_ACCESS_CONTRIBUTOR = 500` |  |
| `static final int CAL_ACCESS_EDITOR = 600` |  |
| `static final int CAL_ACCESS_FREEBUSY = 100` |  |
| `static final int CAL_ACCESS_NONE = 0` |  |
| `static final int CAL_ACCESS_OVERRIDE = 400` |  |
| `static final int CAL_ACCESS_OWNER = 700` |  |
| `static final int CAL_ACCESS_READ = 200` |  |
| `static final int CAL_ACCESS_RESPOND = 300` |  |
| `static final int CAL_ACCESS_ROOT = 800` |  |
| `static final String CAN_MODIFY_TIME_ZONE = "canModifyTimeZone"` |  |
| `static final String CAN_ORGANIZER_RESPOND = "canOrganizerRespond"` |  |
| `static final String IS_PRIMARY = "isPrimary"` |  |
| `static final String MAX_REMINDERS = "maxReminders"` |  |
| `static final String OWNER_ACCOUNT = "ownerAccount"` |  |
| `static final String SYNC_EVENTS = "sync_events"` |  |
| `static final String VISIBLE = "visible"` |  |

---

### `class static final CalendarContract.CalendarEntity`

- **实现：** `android.provider.BaseColumns android.provider.CalendarContract.CalendarColumns android.provider.CalendarContract.SyncColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.content.EntityIterator newEntityIterator(android.database.Cursor)` |  |

---

### `interface static CalendarContract.CalendarSyncColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CAL_SYNC1 = "cal_sync1"` |  |
| `static final String CAL_SYNC10 = "cal_sync10"` |  |
| `static final String CAL_SYNC2 = "cal_sync2"` |  |
| `static final String CAL_SYNC3 = "cal_sync3"` |  |
| `static final String CAL_SYNC4 = "cal_sync4"` |  |
| `static final String CAL_SYNC5 = "cal_sync5"` |  |
| `static final String CAL_SYNC6 = "cal_sync6"` |  |
| `static final String CAL_SYNC7 = "cal_sync7"` |  |
| `static final String CAL_SYNC8 = "cal_sync8"` |  |
| `static final String CAL_SYNC9 = "cal_sync9"` |  |

---

### `class static final CalendarContract.Calendars`

- **实现：** `android.provider.BaseColumns android.provider.CalendarContract.CalendarColumns android.provider.CalendarContract.SyncColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CALENDAR_LOCATION = "calendar_location"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "calendar_displayName"` |  |
| `static final String NAME = "name"` |  |

---

### `class static final CalendarContract.Colors`

- **实现：** `android.provider.CalendarContract.ColorsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |

---

### `interface static CalendarContract.ColorsColumns`

- **继承：** `android.provider.SyncStateContract.Columns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String COLOR = "color"` |  |
| `static final String COLOR_KEY = "color_index"` |  |
| `static final String COLOR_TYPE = "color_type"` |  |
| `static final int TYPE_CALENDAR = 0` |  |
| `static final int TYPE_EVENT = 1` |  |

---

### `class static final CalendarContract.EventDays`

- **实现：** `android.provider.CalendarContract.EventDaysColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.database.Cursor query(android.content.ContentResolver, int, int, String[])` |  |

---

### `interface static CalendarContract.EventDaysColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ENDDAY = "endDay"` |  |
| `static final String STARTDAY = "startDay"` |  |

---

### `class static final CalendarContract.Events`

- **实现：** `android.provider.BaseColumns android.provider.CalendarContract.CalendarColumns android.provider.CalendarContract.EventsColumns android.provider.CalendarContract.SyncColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_EXCEPTION_URI` |  |
| `static final android.net.Uri CONTENT_URI` |  |

---

### `interface static CalendarContract.EventsColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ACCESS_CONFIDENTIAL = 1` |  |
| `static final int ACCESS_DEFAULT = 0` |  |
| `static final String ACCESS_LEVEL = "accessLevel"` |  |
| `static final int ACCESS_PRIVATE = 2` |  |
| `static final int ACCESS_PUBLIC = 3` |  |
| `static final String ALL_DAY = "allDay"` |  |
| `static final String AVAILABILITY = "availability"` |  |
| `static final int AVAILABILITY_BUSY = 0` |  |
| `static final int AVAILABILITY_FREE = 1` |  |
| `static final int AVAILABILITY_TENTATIVE = 2` |  |
| `static final String CALENDAR_ID = "calendar_id"` |  |
| `static final String CAN_INVITE_OTHERS = "canInviteOthers"` |  |
| `static final String CUSTOM_APP_PACKAGE = "customAppPackage"` |  |
| `static final String CUSTOM_APP_URI = "customAppUri"` |  |
| `static final String DESCRIPTION = "description"` |  |
| `static final String DISPLAY_COLOR = "displayColor"` |  |
| `static final String DTEND = "dtend"` |  |
| `static final String DTSTART = "dtstart"` |  |
| `static final String DURATION = "duration"` |  |
| `static final String EVENT_COLOR = "eventColor"` |  |
| `static final String EVENT_COLOR_KEY = "eventColor_index"` |  |
| `static final String EVENT_END_TIMEZONE = "eventEndTimezone"` |  |
| `static final String EVENT_LOCATION = "eventLocation"` |  |
| `static final String EVENT_TIMEZONE = "eventTimezone"` |  |
| `static final String EXDATE = "exdate"` |  |
| `static final String EXRULE = "exrule"` |  |
| `static final String GUESTS_CAN_INVITE_OTHERS = "guestsCanInviteOthers"` |  |
| `static final String GUESTS_CAN_MODIFY = "guestsCanModify"` |  |
| `static final String GUESTS_CAN_SEE_GUESTS = "guestsCanSeeGuests"` |  |
| `static final String HAS_ALARM = "hasAlarm"` |  |
| `static final String HAS_ATTENDEE_DATA = "hasAttendeeData"` |  |
| `static final String HAS_EXTENDED_PROPERTIES = "hasExtendedProperties"` |  |
| `static final String IS_ORGANIZER = "isOrganizer"` |  |
| `static final String LAST_DATE = "lastDate"` |  |
| `static final String LAST_SYNCED = "lastSynced"` |  |
| `static final String ORGANIZER = "organizer"` |  |
| `static final String ORIGINAL_ALL_DAY = "originalAllDay"` |  |
| `static final String ORIGINAL_ID = "original_id"` |  |
| `static final String ORIGINAL_INSTANCE_TIME = "originalInstanceTime"` |  |
| `static final String ORIGINAL_SYNC_ID = "original_sync_id"` |  |
| `static final String RDATE = "rdate"` |  |
| `static final String RRULE = "rrule"` |  |
| `static final String SELF_ATTENDEE_STATUS = "selfAttendeeStatus"` |  |
| `static final String STATUS = "eventStatus"` |  |
| `static final int STATUS_CANCELED = 2` |  |
| `static final int STATUS_CONFIRMED = 1` |  |
| `static final int STATUS_TENTATIVE = 0` |  |
| `static final String SYNC_DATA1 = "sync_data1"` |  |
| `static final String SYNC_DATA10 = "sync_data10"` |  |
| `static final String SYNC_DATA2 = "sync_data2"` |  |
| `static final String SYNC_DATA3 = "sync_data3"` |  |
| `static final String SYNC_DATA4 = "sync_data4"` |  |
| `static final String SYNC_DATA5 = "sync_data5"` |  |
| `static final String SYNC_DATA6 = "sync_data6"` |  |
| `static final String SYNC_DATA7 = "sync_data7"` |  |
| `static final String SYNC_DATA8 = "sync_data8"` |  |
| `static final String SYNC_DATA9 = "sync_data9"` |  |
| `static final String TITLE = "title"` |  |
| `static final String UID_2445 = "uid2445"` |  |

---

### `class static final CalendarContract.EventsEntity`

- **实现：** `android.provider.BaseColumns android.provider.CalendarContract.EventsColumns android.provider.CalendarContract.SyncColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.content.EntityIterator newEntityIterator(android.database.Cursor, android.content.ContentResolver)` |  |
| `static android.content.EntityIterator newEntityIterator(android.database.Cursor, android.content.ContentProviderClient)` |  |

---

### `class static final CalendarContract.ExtendedProperties`

- **实现：** `android.provider.BaseColumns android.provider.CalendarContract.EventsColumns android.provider.CalendarContract.ExtendedPropertiesColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |

---

### `interface static CalendarContract.ExtendedPropertiesColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EVENT_ID = "event_id"` |  |
| `static final String NAME = "name"` |  |
| `static final String VALUE = "value"` |  |

---

### `class static final CalendarContract.Instances`

- **实现：** `android.provider.BaseColumns android.provider.CalendarContract.CalendarColumns android.provider.CalendarContract.EventsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String BEGIN = "begin"` |  |
| `static final android.net.Uri CONTENT_BY_DAY_URI` |  |
| `static final android.net.Uri CONTENT_SEARCH_BY_DAY_URI` |  |
| `static final android.net.Uri CONTENT_SEARCH_URI` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String END = "end"` |  |
| `static final String END_DAY = "endDay"` |  |
| `static final String END_MINUTE = "endMinute"` |  |
| `static final String EVENT_ID = "event_id"` |  |
| `static final String START_DAY = "startDay"` |  |
| `static final String START_MINUTE = "startMinute"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.database.Cursor query(android.content.ContentResolver, String[], long, long)` |  |
| `static android.database.Cursor query(android.content.ContentResolver, String[], long, long, String)` |  |

---

### `class static final CalendarContract.Reminders`

- **实现：** `android.provider.BaseColumns android.provider.CalendarContract.EventsColumns android.provider.CalendarContract.RemindersColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.database.Cursor query(android.content.ContentResolver, long, String[])` |  |

---

### `interface static CalendarContract.RemindersColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EVENT_ID = "event_id"` |  |
| `static final String METHOD = "method"` |  |
| `static final int METHOD_ALARM = 4` |  |
| `static final int METHOD_ALERT = 1` |  |
| `static final int METHOD_DEFAULT = 0` |  |
| `static final int METHOD_EMAIL = 2` |  |
| `static final int METHOD_SMS = 3` |  |
| `static final String MINUTES = "minutes"` |  |
| `static final int MINUTES_DEFAULT = -1` |  |

---

### `interface static CalendarContract.SyncColumns`

- **继承：** `android.provider.CalendarContract.CalendarSyncColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACCOUNT_NAME = "account_name"` |  |
| `static final String ACCOUNT_TYPE = "account_type"` |  |
| `static final String CAN_PARTIALLY_UPDATE = "canPartiallyUpdate"` |  |
| `static final String DELETED = "deleted"` |  |
| `static final String DIRTY = "dirty"` |  |
| `static final String MUTATORS = "mutators"` |  |
| `static final String _SYNC_ID = "_sync_id"` |  |

---

### `class static final CalendarContract.SyncState`

- **实现：** `android.provider.SyncStateContract.Columns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |

---

### `class CallLog`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CallLog()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUTHORITY = "call_log"` |  |
| `static final android.net.Uri CONTENT_URI` |  |

---

### `class static CallLog.Calls`

- **实现：** `android.provider.BaseColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CallLog.Calls()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int ANSWERED_EXTERNALLY_TYPE = 7` |  |
| `static final int BLOCKED_TYPE = 6` |  |
| `static final String BLOCK_REASON = "block_reason"` |  |
| `static final int BLOCK_REASON_BLOCKED_NUMBER = 3` |  |
| `static final int BLOCK_REASON_CALL_SCREENING_SERVICE = 1` |  |
| `static final int BLOCK_REASON_DIRECT_TO_VOICEMAIL = 2` |  |
| `static final int BLOCK_REASON_NOT_BLOCKED = 0` |  |
| `static final int BLOCK_REASON_NOT_IN_CONTACTS = 7` |  |
| `static final int BLOCK_REASON_PAY_PHONE = 6` |  |
| `static final int BLOCK_REASON_RESTRICTED_NUMBER = 5` |  |
| `static final int BLOCK_REASON_UNKNOWN_NUMBER = 4` |  |
| `static final String CACHED_FORMATTED_NUMBER = "formatted_number"` |  |
| `static final String CACHED_LOOKUP_URI = "lookup_uri"` |  |
| `static final String CACHED_MATCHED_NUMBER = "matched_number"` |  |
| `static final String CACHED_NAME = "name"` |  |
| `static final String CACHED_NORMALIZED_NUMBER = "normalized_number"` |  |
| `static final String CACHED_NUMBER_LABEL = "numberlabel"` |  |
| `static final String CACHED_NUMBER_TYPE = "numbertype"` |  |
| `static final String CACHED_PHOTO_ID = "photo_id"` |  |
| `static final String CACHED_PHOTO_URI = "photo_uri"` |  |
| `static final String CALL_SCREENING_APP_NAME = "call_screening_app_name"` |  |
| `static final String CALL_SCREENING_COMPONENT_NAME = "call_screening_component_name"` |  |
| `static final android.net.Uri CONTENT_FILTER_URI` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/calls"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/calls"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final android.net.Uri CONTENT_URI_WITH_VOICEMAIL` |  |
| `static final String COUNTRY_ISO = "countryiso"` |  |
| `static final String DATA_USAGE = "data_usage"` |  |
| `static final String DATE = "date"` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |
| `static final String DURATION = "duration"` |  |
| `static final String EXTRA_CALL_TYPE_FILTER = "android.provider.extra.CALL_TYPE_FILTER"` |  |
| `static final String FEATURES = "features"` |  |
| `static final int FEATURES_ASSISTED_DIALING_USED = 16` |  |
| `static final int FEATURES_HD_CALL = 4` |  |
| `static final int FEATURES_PULLED_EXTERNALLY = 2` |  |
| `static final int FEATURES_RTT = 32` |  |
| `static final int FEATURES_VIDEO = 1` |  |
| `static final int FEATURES_VOLTE = 64` |  |
| `static final int FEATURES_WIFI = 8` |  |
| `static final String GEOCODED_LOCATION = "geocoded_location"` |  |
| `static final int INCOMING_TYPE = 1` |  |
| `static final String IS_READ = "is_read"` |  |
| `static final String LAST_MODIFIED = "last_modified"` |  |
| `static final String LIMIT_PARAM_KEY = "limit"` |  |
| `static final int MISSED_TYPE = 3` |  |
| `static final String NEW = "new"` |  |
| `static final String NUMBER = "number"` |  |
| `static final String NUMBER_PRESENTATION = "presentation"` |  |
| `static final String OFFSET_PARAM_KEY = "offset"` |  |
| `static final int OUTGOING_TYPE = 2` |  |
| `static final String PHONE_ACCOUNT_COMPONENT_NAME = "subscription_component_name"` |  |
| `static final String PHONE_ACCOUNT_ID = "subscription_id"` |  |
| `static final String POST_DIAL_DIGITS = "post_dial_digits"` |  |
| `static final int PRESENTATION_ALLOWED = 1` |  |
| `static final int PRESENTATION_PAYPHONE = 4` |  |
| `static final int PRESENTATION_RESTRICTED = 2` |  |
| `static final int PRESENTATION_UNKNOWN = 3` |  |
| `static final int REJECTED_TYPE = 5` |  |
| `static final String TRANSCRIPTION = "transcription"` |  |
| `static final String TYPE = "type"` |  |
| `static final String VIA_NUMBER = "via_number"` |  |
| `static final int VOICEMAIL_TYPE = 4` |  |
| `static final String VOICEMAIL_URI = "voicemail_uri"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String getLastOutgoingCall(android.content.Context)` |  |

---

### `class Contacts` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.ContactMethods` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.ContactMethodsColumns android.provider.Contacts.PeopleColumns`
- **注解：** `@Deprecated`

---

### `interface static Contacts.ContactMethodsColumns` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.Extensions` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.ExtensionsColumns`
- **注解：** `@Deprecated`

---

### `interface static Contacts.ExtensionsColumns` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.GroupMembership` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.GroupsColumns`
- **注解：** `@Deprecated`

---

### `class static final Contacts.Groups` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.GroupsColumns`
- **注解：** `@Deprecated`

---

### `interface static Contacts.GroupsColumns` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.Intents` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.Intents.Insert` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.Intents.UI` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Contacts.OrganizationColumns` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.Organizations` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.OrganizationColumns`
- **注解：** `@Deprecated`

---

### `class static final Contacts.People` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.PeopleColumns android.provider.Contacts.PhonesColumns android.provider.Contacts.PresenceColumns`
- **注解：** `@Deprecated`

---

### `class static final Contacts.People.ContactMethods` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.ContactMethodsColumns android.provider.Contacts.PeopleColumns`
- **注解：** `@Deprecated`

---

### `class static Contacts.People.Extensions` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.ExtensionsColumns`
- **注解：** `@Deprecated`

---

### `class static final Contacts.People.Phones` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.PeopleColumns android.provider.Contacts.PhonesColumns`
- **注解：** `@Deprecated`

---

### `interface static Contacts.PeopleColumns` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.Phones` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.PeopleColumns android.provider.Contacts.PhonesColumns`
- **注解：** `@Deprecated`

---

### `interface static Contacts.PhonesColumns` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.Photos` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.PhotosColumns`
- **注解：** `@Deprecated`

---

### `interface static Contacts.PhotosColumns` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static Contacts.PresenceColumns` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class static final Contacts.Settings` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns android.provider.Contacts.SettingsColumns`
- **注解：** `@Deprecated`

---

### `interface static Contacts.SettingsColumns` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `class final ContactsContract`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ContactsContract()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUTHORITY = "com.android.contacts"` |  |
| `static final android.net.Uri AUTHORITY_URI` |  |
| `static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter"` |  |
| `static final String DEFERRED_SNIPPETING = "deferred_snippeting"` |  |
| `static final String DEFERRED_SNIPPETING_QUERY = "deferred_snippeting_query"` |  |
| `static final String DIRECTORY_PARAM_KEY = "directory"` |  |
| `static final String LIMIT_PARAM_KEY = "limit"` |  |
| `static final String PRIMARY_ACCOUNT_NAME = "name_for_primary_account"` |  |
| `static final String PRIMARY_ACCOUNT_TYPE = "type_for_primary_account"` |  |
| `static final String REMOVE_DUPLICATE_ENTRIES = "remove_duplicate_entries"` |  |
| `static final String STREQUENT_PHONE_ONLY = "strequent_phone_only"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean isProfileId(long)` |  |

---

### `class static final ContactsContract.AggregationExceptions`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/aggregation_exception"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/aggregation_exception"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String RAW_CONTACT_ID1 = "raw_contact_id1"` |  |
| `static final String RAW_CONTACT_ID2 = "raw_contact_id2"` |  |
| `static final String TYPE = "type"` |  |
| `static final int TYPE_AUTOMATIC = 0` |  |
| `static final int TYPE_KEEP_SEPARATE = 2` |  |
| `static final int TYPE_KEEP_TOGETHER = 1` |  |

---

### `interface static ContactsContract.BaseSyncColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SYNC1 = "sync1"` |  |
| `static final String SYNC2 = "sync2"` |  |
| `static final String SYNC3 = "sync3"` |  |
| `static final String SYNC4 = "sync4"` |  |

---

### `class static final ContactsContract.CommonDataKinds`


---

### `interface static ContactsContract.CommonDataKinds.BaseTypes`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TYPE_CUSTOM = 0` |  |

---

### `class static final ContactsContract.CommonDataKinds.Callable`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ContactsContract.CommonDataKinds.Callable()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_FILTER_URI` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final android.net.Uri ENTERPRISE_CONTENT_FILTER_URI` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |

---

### `interface static ContactsContract.CommonDataKinds.CommonColumns`

- **继承：** `android.provider.ContactsContract.CommonDataKinds.BaseTypes`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DATA = "data1"` |  |
| `static final String LABEL = "data3"` |  |
| `static final String TYPE = "data2"` |  |

---

### `class static final ContactsContract.CommonDataKinds.Contactables`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ContactsContract.CommonDataKinds.Contactables()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_FILTER_URI` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String VISIBLE_CONTACTS_ONLY = "visible_contacts_only"` |  |

---

### `class static final ContactsContract.CommonDataKinds.Email`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ADDRESS = "data1"` |  |
| `static final android.net.Uri CONTENT_FILTER_URI` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/email_v2"` |  |
| `static final android.net.Uri CONTENT_LOOKUP_URI` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/email_v2"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DISPLAY_NAME = "data4"` |  |
| `static final android.net.Uri ENTERPRISE_CONTENT_FILTER_URI` |  |
| `static final android.net.Uri ENTERPRISE_CONTENT_LOOKUP_URI` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final int TYPE_HOME = 1` |  |
| `static final int TYPE_MOBILE = 4` |  |
| `static final int TYPE_OTHER = 3` |  |
| `static final int TYPE_WORK = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence getTypeLabel(android.content.res.Resources, int, CharSequence)` |  |
| `static int getTypeLabelResource(int)` |  |

---

### `class static final ContactsContract.CommonDataKinds.Event`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact_event"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String START_DATE = "data1"` |  |
| `static final int TYPE_ANNIVERSARY = 1` |  |
| `static final int TYPE_BIRTHDAY = 3` |  |
| `static final int TYPE_OTHER = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence getTypeLabel(android.content.res.Resources, int, CharSequence)` |  |
| `static int getTypeResource(Integer)` |  |

---

### `class static final ContactsContract.CommonDataKinds.GroupMembership`

- **实现：** `android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/group_membership"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String GROUP_ROW_ID = "data1"` |  |
| `static final String GROUP_SOURCE_ID = "group_sourceid"` |  |

---

### `class static final ContactsContract.CommonDataKinds.Identity`

- **实现：** `android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/identity"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String IDENTITY = "data1"` |  |
| `static final String NAMESPACE = "data2"` |  |

---

### `class static final ContactsContract.CommonDataKinds.Im`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/im"` |  |
| `static final String CUSTOM_PROTOCOL = "data6"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String PROTOCOL = "data5"` |  |
| `static final int PROTOCOL_AIM = 0` |  |
| `static final int PROTOCOL_CUSTOM = -1` |  |
| `static final int PROTOCOL_GOOGLE_TALK = 5` |  |
| `static final int PROTOCOL_ICQ = 6` |  |
| `static final int PROTOCOL_JABBER = 7` |  |
| `static final int PROTOCOL_MSN = 1` |  |
| `static final int PROTOCOL_NETMEETING = 8` |  |
| `static final int PROTOCOL_QQ = 4` |  |
| `static final int PROTOCOL_SKYPE = 3` |  |
| `static final int PROTOCOL_YAHOO = 2` |  |
| `static final int TYPE_HOME = 1` |  |
| `static final int TYPE_OTHER = 3` |  |
| `static final int TYPE_WORK = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence getProtocolLabel(android.content.res.Resources, int, CharSequence)` |  |
| `static int getProtocolLabelResource(int)` |  |
| `static CharSequence getTypeLabel(android.content.res.Resources, int, CharSequence)` |  |
| `static int getTypeLabelResource(int)` |  |

---

### `class static final ContactsContract.CommonDataKinds.Nickname`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/nickname"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String NAME = "data1"` |  |
| `static final int TYPE_DEFAULT = 1` |  |
| `static final int TYPE_INITIALS = 5` |  |
| `static final int TYPE_MAIDEN_NAME = 3` |  |
| `static final int TYPE_OTHER_NAME = 2` |  |
| `static final int TYPE_SHORT_NAME = 4` |  |

---

### `class static final ContactsContract.CommonDataKinds.Note`

- **实现：** `android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/note"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String NOTE = "data1"` |  |

---

### `class static final ContactsContract.CommonDataKinds.Organization`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String COMPANY = "data1"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/organization"` |  |
| `static final String DEPARTMENT = "data5"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String JOB_DESCRIPTION = "data6"` |  |
| `static final String OFFICE_LOCATION = "data9"` |  |
| `static final String PHONETIC_NAME = "data8"` |  |
| `static final String PHONETIC_NAME_STYLE = "data10"` |  |
| `static final String SYMBOL = "data7"` |  |
| `static final String TITLE = "data4"` |  |
| `static final int TYPE_OTHER = 2` |  |
| `static final int TYPE_WORK = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence getTypeLabel(android.content.res.Resources, int, CharSequence)` |  |
| `static int getTypeLabelResource(int)` |  |

---

### `class static final ContactsContract.CommonDataKinds.Phone`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_FILTER_URI` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/phone_v2"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/phone_v2"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final android.net.Uri ENTERPRISE_CONTENT_FILTER_URI` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String NORMALIZED_NUMBER = "data4"` |  |
| `static final String NUMBER = "data1"` |  |
| `static final String SEARCH_DISPLAY_NAME_KEY = "search_display_name"` |  |
| `static final String SEARCH_PHONE_NUMBER_KEY = "search_phone_number"` |  |
| `static final int TYPE_ASSISTANT = 19` |  |
| `static final int TYPE_CALLBACK = 8` |  |
| `static final int TYPE_CAR = 9` |  |
| `static final int TYPE_COMPANY_MAIN = 10` |  |
| `static final int TYPE_FAX_HOME = 5` |  |
| `static final int TYPE_FAX_WORK = 4` |  |
| `static final int TYPE_HOME = 1` |  |
| `static final int TYPE_ISDN = 11` |  |
| `static final int TYPE_MAIN = 12` |  |
| `static final int TYPE_MMS = 20` |  |
| `static final int TYPE_MOBILE = 2` |  |
| `static final int TYPE_OTHER = 7` |  |
| `static final int TYPE_OTHER_FAX = 13` |  |
| `static final int TYPE_PAGER = 6` |  |
| `static final int TYPE_RADIO = 14` |  |
| `static final int TYPE_TELEX = 15` |  |
| `static final int TYPE_TTY_TDD = 16` |  |
| `static final int TYPE_WORK = 3` |  |
| `static final int TYPE_WORK_MOBILE = 17` |  |
| `static final int TYPE_WORK_PAGER = 18` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence getTypeLabel(android.content.res.Resources, int, CharSequence)` |  |
| `static int getTypeLabelResource(int)` |  |

---

### `class static final ContactsContract.CommonDataKinds.Photo`

- **实现：** `android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/photo"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String PHOTO = "data15"` |  |
| `static final String PHOTO_FILE_ID = "data14"` |  |

---

### `class static final ContactsContract.CommonDataKinds.Relation`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/relation"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String NAME = "data1"` |  |
| `static final int TYPE_ASSISTANT = 1` |  |
| `static final int TYPE_BROTHER = 2` |  |
| `static final int TYPE_CHILD = 3` |  |
| `static final int TYPE_DOMESTIC_PARTNER = 4` |  |
| `static final int TYPE_FATHER = 5` |  |
| `static final int TYPE_FRIEND = 6` |  |
| `static final int TYPE_MANAGER = 7` |  |
| `static final int TYPE_MOTHER = 8` |  |
| `static final int TYPE_PARENT = 9` |  |
| `static final int TYPE_PARTNER = 10` |  |
| `static final int TYPE_REFERRED_BY = 11` |  |
| `static final int TYPE_RELATIVE = 12` |  |
| `static final int TYPE_SISTER = 13` |  |
| `static final int TYPE_SPOUSE = 14` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence getTypeLabel(android.content.res.Resources, int, CharSequence)` |  |
| `static int getTypeLabelResource(int)` |  |

---

### `class static final ContactsContract.CommonDataKinds.SipAddress`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/sip_address"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String SIP_ADDRESS = "data1"` |  |
| `static final int TYPE_HOME = 1` |  |
| `static final int TYPE_OTHER = 3` |  |
| `static final int TYPE_WORK = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence getTypeLabel(android.content.res.Resources, int, CharSequence)` |  |
| `static int getTypeLabelResource(int)` |  |

---

### `class static final ContactsContract.CommonDataKinds.StructuredName`

- **实现：** `android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/name"` |  |
| `static final String DISPLAY_NAME = "data1"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String FAMILY_NAME = "data3"` |  |
| `static final String FULL_NAME_STYLE = "data10"` |  |
| `static final String GIVEN_NAME = "data2"` |  |
| `static final String MIDDLE_NAME = "data5"` |  |
| `static final String PHONETIC_FAMILY_NAME = "data9"` |  |
| `static final String PHONETIC_GIVEN_NAME = "data7"` |  |
| `static final String PHONETIC_MIDDLE_NAME = "data8"` |  |
| `static final String PHONETIC_NAME_STYLE = "data11"` |  |
| `static final String PREFIX = "data4"` |  |
| `static final String SUFFIX = "data6"` |  |

---

### `class static final ContactsContract.CommonDataKinds.StructuredPostal`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CITY = "data7"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/postal-address_v2"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/postal-address_v2"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String COUNTRY = "data10"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String FORMATTED_ADDRESS = "data1"` |  |
| `static final String NEIGHBORHOOD = "data6"` |  |
| `static final String POBOX = "data5"` |  |
| `static final String POSTCODE = "data9"` |  |
| `static final String REGION = "data8"` |  |
| `static final String STREET = "data4"` |  |
| `static final int TYPE_HOME = 1` |  |
| `static final int TYPE_OTHER = 3` |  |
| `static final int TYPE_WORK = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static CharSequence getTypeLabel(android.content.res.Resources, int, CharSequence)` |  |
| `static int getTypeLabelResource(int)` |  |

---

### `class static final ContactsContract.CommonDataKinds.Website`

- **实现：** `android.provider.ContactsContract.CommonDataKinds.CommonColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/website"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final int TYPE_BLOG = 2` |  |
| `static final int TYPE_FTP = 6` |  |
| `static final int TYPE_HOME = 4` |  |
| `static final int TYPE_HOMEPAGE = 1` |  |
| `static final int TYPE_OTHER = 7` |  |
| `static final int TYPE_PROFILE = 3` |  |
| `static final int TYPE_WORK = 5` |  |
| `static final String URL = "data1"` |  |

---

### `interface static ContactsContract.ContactNameColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DISPLAY_NAME_ALTERNATIVE = "display_name_alt"` |  |
| `static final String DISPLAY_NAME_PRIMARY = "display_name"` |  |
| `static final String DISPLAY_NAME_SOURCE = "display_name_source"` |  |
| `static final String PHONETIC_NAME = "phonetic_name"` |  |
| `static final String PHONETIC_NAME_STYLE = "phonetic_name_style"` |  |
| `static final String SORT_KEY_ALTERNATIVE = "sort_key_alt"` |  |
| `static final String SORT_KEY_PRIMARY = "sort_key"` |  |

---

### `interface static ContactsContract.ContactOptionsColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CUSTOM_RINGTONE = "custom_ringtone"` |  |
| `static final String PINNED = "pinned"` |  |
| `static final String SEND_TO_VOICEMAIL = "send_to_voicemail"` |  |
| `static final String STARRED = "starred"` |  |

---

### `interface static ContactsContract.ContactStatusColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTACT_CHAT_CAPABILITY = "contact_chat_capability"` |  |
| `static final String CONTACT_PRESENCE = "contact_presence"` |  |
| `static final String CONTACT_STATUS = "contact_status"` |  |
| `static final String CONTACT_STATUS_ICON = "contact_status_icon"` |  |
| `static final String CONTACT_STATUS_LABEL = "contact_status_label"` |  |
| `static final String CONTACT_STATUS_RES_PACKAGE = "contact_status_res_package"` |  |
| `static final String CONTACT_STATUS_TIMESTAMP = "contact_status_ts"` |  |

---

### `class static ContactsContract.Contacts`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.ContactNameColumns android.provider.ContactsContract.ContactOptionsColumns android.provider.ContactsContract.ContactStatusColumns android.provider.ContactsContract.ContactsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_FILTER_URI` |  |
| `static final android.net.Uri CONTENT_GROUP_URI` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact"` |  |
| `static final android.net.Uri CONTENT_LOOKUP_URI` |  |
| `static final android.net.Uri CONTENT_MULTI_VCARD_URI` |  |
| `static final android.net.Uri CONTENT_STREQUENT_FILTER_URI` |  |
| `static final android.net.Uri CONTENT_STREQUENT_URI` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String CONTENT_VCARD_TYPE = "text/x-vcard"` |  |
| `static final android.net.Uri CONTENT_VCARD_URI` |  |
| `static final android.net.Uri ENTERPRISE_CONTENT_FILTER_URI` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String QUERY_PARAMETER_VCARD_NO_PHOTO = "no_photo"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getLookupUri(android.content.ContentResolver, android.net.Uri)` |  |
| `static android.net.Uri getLookupUri(long, String)` |  |
| `static boolean isEnterpriseContactId(long)` |  |
| `static android.net.Uri lookupContact(android.content.ContentResolver, android.net.Uri)` |  |
| `static java.io.InputStream openContactPhotoInputStream(android.content.ContentResolver, android.net.Uri, boolean)` |  |
| `static java.io.InputStream openContactPhotoInputStream(android.content.ContentResolver, android.net.Uri)` |  |

---

### `class static final ContactsContract.Contacts.AggregationSuggestions`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.ContactOptionsColumns android.provider.ContactsContract.ContactStatusColumns android.provider.ContactsContract.ContactsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "suggestions"` |  |

---

### `class static final ContactsContract.Contacts.AggregationSuggestions.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ContactsContract.Contacts.AggregationSuggestions.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.provider.ContactsContract.Contacts.AggregationSuggestions.Builder addNameParameter(String)` |  |
| `android.net.Uri build()` |  |
| `android.provider.ContactsContract.Contacts.AggregationSuggestions.Builder setContactId(long)` |  |
| `android.provider.ContactsContract.Contacts.AggregationSuggestions.Builder setLimit(int)` |  |

---

### `class static final ContactsContract.Contacts.Data`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.DataColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "data"` |  |

---

### `class static final ContactsContract.Contacts.Entity`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.BaseSyncColumns android.provider.ContactsContract.ContactNameColumns android.provider.ContactsContract.ContactOptionsColumns android.provider.ContactsContract.ContactStatusColumns android.provider.ContactsContract.ContactsColumns android.provider.ContactsContract.DataColumns android.provider.ContactsContract.DataUsageStatColumns android.provider.ContactsContract.RawContactsColumns android.provider.ContactsContract.StatusColumns android.provider.ContactsContract.SyncColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "entities"` |  |
| `static final String DATA_ID = "data_id"` |  |
| `static final String RAW_CONTACT_ID = "raw_contact_id"` |  |

---

### `class static final ContactsContract.Contacts.Photo`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "photo"` |  |
| `static final String DISPLAY_PHOTO = "display_photo"` |  |
| `static final String PHOTO = "data15"` |  |
| `static final String PHOTO_FILE_ID = "data14"` |  |

---

### `interface static ContactsContract.ContactsColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTACT_LAST_UPDATED_TIMESTAMP = "contact_last_updated_timestamp"` |  |
| `static final String DISPLAY_NAME = "display_name"` |  |
| `static final String HAS_PHONE_NUMBER = "has_phone_number"` |  |
| `static final String IN_DEFAULT_DIRECTORY = "in_default_directory"` |  |
| `static final String IN_VISIBLE_GROUP = "in_visible_group"` |  |
| `static final String IS_USER_PROFILE = "is_user_profile"` |  |
| `static final String LOOKUP_KEY = "lookup"` |  |
| `static final String NAME_RAW_CONTACT_ID = "name_raw_contact_id"` |  |
| `static final String PHOTO_FILE_ID = "photo_file_id"` |  |
| `static final String PHOTO_ID = "photo_id"` |  |
| `static final String PHOTO_THUMBNAIL_URI = "photo_thumb_uri"` |  |
| `static final String PHOTO_URI = "photo_uri"` |  |

---

### `class static final ContactsContract.Data`

- **实现：** `android.provider.ContactsContract.DataColumnsWithJoins`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/data"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS"` |  |
| `static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES"` |  |
| `static final String VISIBLE_CONTACTS_ONLY = "visible_contacts_only"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContactLookupUri(android.content.ContentResolver, android.net.Uri)` |  |

---

### `interface static ContactsContract.DataColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CARRIER_PRESENCE = "carrier_presence"` |  |
| `static final int CARRIER_PRESENCE_VT_CAPABLE = 1` |  |
| `static final String DATA1 = "data1"` |  |
| `static final String DATA10 = "data10"` |  |
| `static final String DATA11 = "data11"` |  |
| `static final String DATA12 = "data12"` |  |
| `static final String DATA13 = "data13"` |  |
| `static final String DATA14 = "data14"` |  |
| `static final String DATA15 = "data15"` |  |
| `static final String DATA2 = "data2"` |  |
| `static final String DATA3 = "data3"` |  |
| `static final String DATA4 = "data4"` |  |
| `static final String DATA5 = "data5"` |  |
| `static final String DATA6 = "data6"` |  |
| `static final String DATA7 = "data7"` |  |
| `static final String DATA8 = "data8"` |  |
| `static final String DATA9 = "data9"` |  |
| `static final String DATA_VERSION = "data_version"` |  |
| `static final String IS_PRIMARY = "is_primary"` |  |
| `static final String IS_READ_ONLY = "is_read_only"` |  |
| `static final String IS_SUPER_PRIMARY = "is_super_primary"` |  |
| `static final String MIMETYPE = "mimetype"` |  |
| `static final String PREFERRED_PHONE_ACCOUNT_COMPONENT_NAME = "preferred_phone_account_component_name"` |  |
| `static final String PREFERRED_PHONE_ACCOUNT_ID = "preferred_phone_account_id"` |  |
| `static final String RAW_CONTACT_ID = "raw_contact_id"` |  |
| `static final String RES_PACKAGE = "res_package"` |  |
| `static final String SYNC1 = "data_sync1"` |  |
| `static final String SYNC2 = "data_sync2"` |  |
| `static final String SYNC3 = "data_sync3"` |  |
| `static final String SYNC4 = "data_sync4"` |  |

---

### `interface static ContactsContract.DataColumnsWithJoins`

- **继承：** `android.provider.BaseColumns android.provider.ContactsContract.ContactNameColumns android.provider.ContactsContract.ContactOptionsColumns android.provider.ContactsContract.ContactStatusColumns android.provider.ContactsContract.ContactsColumns android.provider.ContactsContract.DataColumns android.provider.ContactsContract.DataUsageStatColumns android.provider.ContactsContract.RawContactsColumns android.provider.ContactsContract.StatusColumns`

---

### `class static final ContactsContract.DataUsageFeedback` ~~DEPRECATED~~

- **注解：** `@Deprecated`

---

### `interface static ContactsContract.DataUsageStatColumns`


---

### `class static final ContactsContract.DeletedContacts`

- **实现：** `android.provider.ContactsContract.DeletedContactsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final long DAYS_KEPT_MILLISECONDS = 2592000000L` |  |

---

### `interface static ContactsContract.DeletedContactsColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTACT_DELETED_TIMESTAMP = "contact_deleted_timestamp"` |  |
| `static final String CONTACT_ID = "contact_id"` |  |

---

### `class static final ContactsContract.Directory`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACCOUNT_NAME = "accountName"` |  |
| `static final String ACCOUNT_TYPE = "accountType"` |  |
| `static final String CALLER_PACKAGE_PARAM_KEY = "callerPackage"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact_directory"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact_directories"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final long DEFAULT = 0L` |  |
| `static final String DIRECTORY_AUTHORITY = "authority"` |  |
| `static final String DISPLAY_NAME = "displayName"` |  |
| `static final android.net.Uri ENTERPRISE_CONTENT_URI` |  |
| `static final long ENTERPRISE_DEFAULT = 1000000000L` |  |
| `static final long ENTERPRISE_LOCAL_INVISIBLE = 1000000001L` |  |
| `static final String EXPORT_SUPPORT = "exportSupport"` |  |
| `static final int EXPORT_SUPPORT_ANY_ACCOUNT = 2` |  |
| `static final int EXPORT_SUPPORT_NONE = 0` |  |
| `static final int EXPORT_SUPPORT_SAME_ACCOUNT_ONLY = 1` |  |
| `static final long LOCAL_INVISIBLE = 1L` |  |
| `static final String PACKAGE_NAME = "packageName"` |  |
| `static final String PHOTO_SUPPORT = "photoSupport"` |  |
| `static final int PHOTO_SUPPORT_FULL = 3` |  |
| `static final int PHOTO_SUPPORT_FULL_SIZE_ONLY = 2` |  |
| `static final int PHOTO_SUPPORT_NONE = 0` |  |
| `static final int PHOTO_SUPPORT_THUMBNAIL_ONLY = 1` |  |
| `static final String SHORTCUT_SUPPORT = "shortcutSupport"` |  |
| `static final int SHORTCUT_SUPPORT_DATA_ITEMS_ONLY = 1` |  |
| `static final int SHORTCUT_SUPPORT_FULL = 2` |  |
| `static final int SHORTCUT_SUPPORT_NONE = 0` |  |
| `static final String TYPE_RESOURCE_ID = "typeResourceId"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean isEnterpriseDirectoryId(long)` |  |
| `static boolean isRemoteDirectoryId(long)` |  |
| `static void notifyDirectoryChange(android.content.ContentResolver)` |  |

---

### `interface static ContactsContract.DisplayNameSources`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int EMAIL = 10` |  |
| `static final int NICKNAME = 35` |  |
| `static final int ORGANIZATION = 30` |  |
| `static final int PHONE = 20` |  |
| `static final int STRUCTURED_NAME = 40` |  |
| `static final int STRUCTURED_PHONETIC_NAME = 37` |  |
| `static final int UNDEFINED = 0` |  |

---

### `class static final ContactsContract.DisplayPhoto`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_MAX_DIMENSIONS_URI` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DISPLAY_MAX_DIM = "display_max_dim"` |  |
| `static final String THUMBNAIL_MAX_DIM = "thumbnail_max_dim"` |  |

---

### `interface static ContactsContract.FullNameStyle`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CHINESE = 3` |  |
| `static final int CJK = 2` |  |
| `static final int JAPANESE = 4` |  |
| `static final int KOREAN = 5` |  |
| `static final int UNDEFINED = 0` |  |
| `static final int WESTERN = 1` |  |

---

### `class static final ContactsContract.Groups`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.GroupsColumns android.provider.ContactsContract.SyncColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/group"` |  |
| `static final android.net.Uri CONTENT_SUMMARY_URI` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/group"` |  |
| `static final android.net.Uri CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.content.EntityIterator newEntityIterator(android.database.Cursor)` |  |

---

### `interface static ContactsContract.GroupsColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUTO_ADD = "auto_add"` |  |
| `static final String DATA_SET = "data_set"` |  |
| `static final String DELETED = "deleted"` |  |
| `static final String FAVORITES = "favorites"` |  |
| `static final String GROUP_IS_READ_ONLY = "group_is_read_only"` |  |
| `static final String GROUP_VISIBLE = "group_visible"` |  |
| `static final String NOTES = "notes"` |  |
| `static final String RES_PACKAGE = "res_package"` |  |
| `static final String SHOULD_SYNC = "should_sync"` |  |
| `static final String SUMMARY_COUNT = "summ_count"` |  |
| `static final String SUMMARY_WITH_PHONES = "summ_phones"` |  |
| `static final String SYSTEM_ID = "system_id"` |  |
| `static final String TITLE = "title"` |  |
| `static final String TITLE_RES = "title_res"` |  |

---

### `class static final ContactsContract.Intents`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ContactsContract.Intents()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_VOICE_SEND_MESSAGE_TO_CONTACTS = "android.provider.action.VOICE_SEND_MESSAGE_TO_CONTACTS"` |  |
| `static final String ATTACH_IMAGE = "com.android.contacts.action.ATTACH_IMAGE"` |  |
| `static final String CONTACTS_DATABASE_CREATED = "android.provider.Contacts.DATABASE_CREATED"` |  |
| `static final String EXTRA_CREATE_DESCRIPTION = "com.android.contacts.action.CREATE_DESCRIPTION"` |  |
| `static final String EXTRA_FORCE_CREATE = "com.android.contacts.action.FORCE_CREATE"` |  |
| `static final String EXTRA_RECIPIENT_CONTACT_CHAT_ID = "android.provider.extra.RECIPIENT_CONTACT_CHAT_ID"` |  |
| `static final String EXTRA_RECIPIENT_CONTACT_NAME = "android.provider.extra.RECIPIENT_CONTACT_NAME"` |  |
| `static final String EXTRA_RECIPIENT_CONTACT_URI = "android.provider.extra.RECIPIENT_CONTACT_URI"` |  |
| `static final String INVITE_CONTACT = "com.android.contacts.action.INVITE_CONTACT"` |  |
| `static final String METADATA_ACCOUNT_TYPE = "android.provider.account_type"` |  |
| `static final String METADATA_MIMETYPE = "android.provider.mimetype"` |  |
| `static final String SEARCH_SUGGESTION_CLICKED = "android.provider.Contacts.SEARCH_SUGGESTION_CLICKED"` |  |
| `static final String SEARCH_SUGGESTION_CREATE_CONTACT_CLICKED = "android.provider.Contacts.SEARCH_SUGGESTION_CREATE_CONTACT_CLICKED"` |  |
| `static final String SEARCH_SUGGESTION_DIAL_NUMBER_CLICKED = "android.provider.Contacts.SEARCH_SUGGESTION_DIAL_NUMBER_CLICKED"` |  |
| `static final String SHOW_OR_CREATE_CONTACT = "com.android.contacts.action.SHOW_OR_CREATE_CONTACT"` |  |

---

### `class static final ContactsContract.Intents.Insert`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ContactsContract.Intents.Insert()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION = "android.intent.action.INSERT"` |  |
| `static final String COMPANY = "company"` |  |
| `static final String DATA = "data"` |  |
| `static final String EMAIL = "email"` |  |
| `static final String EMAIL_ISPRIMARY = "email_isprimary"` |  |
| `static final String EMAIL_TYPE = "email_type"` |  |
| `static final String EXTRA_ACCOUNT = "android.provider.extra.ACCOUNT"` |  |
| `static final String EXTRA_DATA_SET = "android.provider.extra.DATA_SET"` |  |
| `static final String FULL_MODE = "full_mode"` |  |
| `static final String IM_HANDLE = "im_handle"` |  |
| `static final String IM_ISPRIMARY = "im_isprimary"` |  |
| `static final String IM_PROTOCOL = "im_protocol"` |  |
| `static final String JOB_TITLE = "job_title"` |  |
| `static final String NAME = "name"` |  |
| `static final String NOTES = "notes"` |  |
| `static final String PHONE = "phone"` |  |
| `static final String PHONETIC_NAME = "phonetic_name"` |  |
| `static final String PHONE_ISPRIMARY = "phone_isprimary"` |  |
| `static final String PHONE_TYPE = "phone_type"` |  |
| `static final String POSTAL = "postal"` |  |
| `static final String POSTAL_ISPRIMARY = "postal_isprimary"` |  |
| `static final String POSTAL_TYPE = "postal_type"` |  |
| `static final String SECONDARY_EMAIL = "secondary_email"` |  |
| `static final String SECONDARY_EMAIL_TYPE = "secondary_email_type"` |  |
| `static final String SECONDARY_PHONE = "secondary_phone"` |  |
| `static final String SECONDARY_PHONE_TYPE = "secondary_phone_type"` |  |
| `static final String TERTIARY_EMAIL = "tertiary_email"` |  |
| `static final String TERTIARY_EMAIL_TYPE = "tertiary_email_type"` |  |
| `static final String TERTIARY_PHONE = "tertiary_phone"` |  |
| `static final String TERTIARY_PHONE_TYPE = "tertiary_phone_type"` |  |

---

### `class static final ContactsContract.PhoneLookup`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.ContactNameColumns android.provider.ContactsContract.ContactOptionsColumns android.provider.ContactsContract.ContactsColumns android.provider.ContactsContract.PhoneLookupColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_FILTER_URI` |  |
| `static final android.net.Uri ENTERPRISE_CONTENT_FILTER_URI` |  |
| `static final String QUERY_PARAMETER_SIP_ADDRESS = "sip"` |  |

---

### `interface static ContactsContract.PhoneLookupColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTACT_ID = "contact_id"` |  |
| `static final String DATA_ID = "data_id"` |  |
| `static final String LABEL = "label"` |  |
| `static final String NORMALIZED_NUMBER = "normalized_number"` |  |
| `static final String NUMBER = "number"` |  |
| `static final String TYPE = "type"` |  |

---

### `interface static ContactsContract.PhoneticNameStyle`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int JAPANESE = 4` |  |
| `static final int KOREAN = 5` |  |
| `static final int PINYIN = 3` |  |
| `static final int UNDEFINED = 0` |  |

---

### `class static final ContactsContract.PinnedPositions`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ContactsContract.PinnedPositions()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DEMOTED = -1` |  |
| `static final int UNPINNED = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void pin(android.content.ContentResolver, long, int)` |  |
| `static void undemote(android.content.ContentResolver, long)` |  |

---

### `class static final ContactsContract.Presence` ~~DEPRECATED~~

- **继承：** `android.provider.ContactsContract.StatusUpdates`
- **注解：** `@Deprecated`

---

### `interface static ContactsContract.PresenceColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CUSTOM_PROTOCOL = "custom_protocol"` |  |
| `static final String DATA_ID = "presence_data_id"` |  |
| `static final String IM_ACCOUNT = "im_account"` |  |
| `static final String IM_HANDLE = "im_handle"` |  |
| `static final String PROTOCOL = "protocol"` |  |

---

### `class static final ContactsContract.Profile`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.ContactNameColumns android.provider.ContactsContract.ContactOptionsColumns android.provider.ContactsContract.ContactStatusColumns android.provider.ContactsContract.ContactsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_RAW_CONTACTS_URI` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final android.net.Uri CONTENT_VCARD_URI` |  |
| `static final long MIN_ID = 9223372034707292160L` |  |

---

### `class static final ContactsContract.ProfileSyncState`

- **实现：** `android.provider.SyncStateContract.Columns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "syncstate"` |  |
| `static final android.net.Uri CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static byte[] get(android.content.ContentProviderClient, android.accounts.Account) throws android.os.RemoteException` |  |
| `static android.util.Pair<android.net.Uri,byte[]> getWithUri(android.content.ContentProviderClient, android.accounts.Account) throws android.os.RemoteException` |  |
| `static android.content.ContentProviderOperation newSetOperation(android.accounts.Account, byte[])` |  |
| `static void set(android.content.ContentProviderClient, android.accounts.Account, byte[]) throws android.os.RemoteException` |  |

---

### `class static final ContactsContract.ProviderStatus`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/provider_status"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DATABASE_CREATION_TIMESTAMP = "database_creation_timestamp"` |  |
| `static final String STATUS = "status"` |  |
| `static final int STATUS_BUSY = 1` |  |
| `static final int STATUS_EMPTY = 2` |  |
| `static final int STATUS_NORMAL = 0` |  |

---

### `class static final ContactsContract.QuickContact`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ContactsContract.QuickContact()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_QUICK_CONTACT = "android.provider.action.QUICK_CONTACT"` |  |
| `static final String EXTRA_EXCLUDE_MIMES = "android.provider.extra.EXCLUDE_MIMES"` |  |
| `static final String EXTRA_MODE = "android.provider.extra.MODE"` |  |
| `static final String EXTRA_PRIORITIZED_MIMETYPE = "android.provider.extra.PRIORITIZED_MIMETYPE"` |  |
| `static final int MODE_LARGE = 3` |  |
| `static final int MODE_MEDIUM = 2` |  |
| `static final int MODE_SMALL = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void showQuickContact(android.content.Context, android.view.View, android.net.Uri, int, String[])` |  |
| `static void showQuickContact(android.content.Context, android.graphics.Rect, android.net.Uri, int, String[])` |  |
| `static void showQuickContact(android.content.Context, android.view.View, android.net.Uri, String[], String)` |  |
| `static void showQuickContact(android.content.Context, android.graphics.Rect, android.net.Uri, String[], String)` |  |

---

### `class static final ContactsContract.RawContacts`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.ContactNameColumns android.provider.ContactsContract.ContactOptionsColumns android.provider.ContactsContract.RawContactsColumns android.provider.ContactsContract.SyncColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AGGREGATION_MODE_DEFAULT = 0` |  |
| `static final int AGGREGATION_MODE_DISABLED = 3` |  |
| `static final int AGGREGATION_MODE_SUSPENDED = 2` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/raw_contact"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/raw_contact"` |  |
| `static final android.net.Uri CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContactLookupUri(android.content.ContentResolver, android.net.Uri)` |  |
| `static android.content.EntityIterator newEntityIterator(android.database.Cursor)` |  |

---

### `class static final ContactsContract.RawContacts.Data`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.DataColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "data"` |  |

---

### `class static final ContactsContract.RawContacts.DisplayPhoto`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "display_photo"` |  |

---

### `class static final ContactsContract.RawContacts.Entity`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.DataColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "entity"` |  |
| `static final String DATA_ID = "data_id"` |  |

---

### `interface static ContactsContract.RawContactsColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACCOUNT_TYPE_AND_DATA_SET = "account_type_and_data_set"` |  |
| `static final String AGGREGATION_MODE = "aggregation_mode"` |  |
| `static final String BACKUP_ID = "backup_id"` |  |
| `static final String CONTACT_ID = "contact_id"` |  |
| `static final String DATA_SET = "data_set"` |  |
| `static final String DELETED = "deleted"` |  |
| `static final String RAW_CONTACT_IS_READ_ONLY = "raw_contact_is_read_only"` |  |
| `static final String RAW_CONTACT_IS_USER_PROFILE = "raw_contact_is_user_profile"` |  |

---

### `class static final ContactsContract.RawContactsEntity`

- **实现：** `android.provider.BaseColumns android.provider.ContactsContract.DataColumns android.provider.ContactsContract.RawContactsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/raw_contact_entity"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DATA_ID = "data_id"` |  |
| `static final android.net.Uri PROFILE_CONTENT_URI` |  |

---

### `class static ContactsContract.SearchSnippets`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ContactsContract.SearchSnippets()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DEFERRED_SNIPPETING_KEY = "deferred_snippeting"` |  |
| `static final String SNIPPET = "snippet"` |  |

---

### `class static final ContactsContract.Settings`

- **实现：** `android.provider.ContactsContract.SettingsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/setting"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/setting"` |  |
| `static final android.net.Uri CONTENT_URI` |  |

---

### `interface static ContactsContract.SettingsColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACCOUNT_NAME = "account_name"` |  |
| `static final String ACCOUNT_TYPE = "account_type"` |  |
| `static final String ANY_UNSYNCED = "any_unsynced"` |  |
| `static final String DATA_SET = "data_set"` |  |
| `static final String SHOULD_SYNC = "should_sync"` |  |
| `static final String UNGROUPED_COUNT = "summ_count"` |  |
| `static final String UNGROUPED_VISIBLE = "ungrouped_visible"` |  |
| `static final String UNGROUPED_WITH_PHONES = "summ_phones"` |  |

---

### `interface static ContactsContract.StatusColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AVAILABLE = 5` |  |
| `static final int AWAY = 2` |  |
| `static final int CAPABILITY_HAS_CAMERA = 4` |  |
| `static final int CAPABILITY_HAS_VIDEO = 2` |  |
| `static final int CAPABILITY_HAS_VOICE = 1` |  |
| `static final String CHAT_CAPABILITY = "chat_capability"` |  |
| `static final int DO_NOT_DISTURB = 4` |  |
| `static final int IDLE = 3` |  |
| `static final int INVISIBLE = 1` |  |
| `static final int OFFLINE = 0` |  |
| `static final String PRESENCE = "mode"` |  |
| `static final String STATUS = "status"` |  |
| `static final String STATUS_ICON = "status_icon"` |  |
| `static final String STATUS_LABEL = "status_label"` |  |
| `static final String STATUS_RES_PACKAGE = "status_res_package"` |  |
| `static final String STATUS_TIMESTAMP = "status_ts"` |  |

---

### `class static ContactsContract.StatusUpdates`

- **实现：** `android.provider.ContactsContract.PresenceColumns android.provider.ContactsContract.StatusColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/status-update"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/status-update"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final android.net.Uri PROFILE_CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int getPresenceIconResourceId(int)` |  |
| `static final int getPresencePrecedence(int)` |  |

---

### `interface static ContactsContract.SyncColumns`

- **继承：** `android.provider.ContactsContract.BaseSyncColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACCOUNT_NAME = "account_name"` |  |
| `static final String ACCOUNT_TYPE = "account_type"` |  |
| `static final String DIRTY = "dirty"` |  |
| `static final String SOURCE_ID = "sourceid"` |  |
| `static final String VERSION = "version"` |  |

---

### `class static final ContactsContract.SyncState`

- **实现：** `android.provider.SyncStateContract.Columns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "syncstate"` |  |
| `static final android.net.Uri CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static byte[] get(android.content.ContentProviderClient, android.accounts.Account) throws android.os.RemoteException` |  |
| `static android.util.Pair<android.net.Uri,byte[]> getWithUri(android.content.ContentProviderClient, android.accounts.Account) throws android.os.RemoteException` |  |
| `static android.content.ContentProviderOperation newSetOperation(android.accounts.Account, byte[])` |  |
| `static void set(android.content.ContentProviderClient, android.accounts.Account, byte[]) throws android.os.RemoteException` |  |

---

### `class final DocumentsContract`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_DOCUMENT_SETTINGS = "android.provider.action.DOCUMENT_SETTINGS"` |  |
| `static final String EXTRA_ERROR = "error"` |  |
| `static final String EXTRA_EXCLUDE_SELF = "android.provider.extra.EXCLUDE_SELF"` |  |
| `static final String EXTRA_INFO = "info"` |  |
| `static final String EXTRA_INITIAL_URI = "android.provider.extra.INITIAL_URI"` |  |
| `static final String EXTRA_LOADING = "loading"` |  |
| `static final String EXTRA_ORIENTATION = "android.provider.extra.ORIENTATION"` |  |
| `static final String EXTRA_PROMPT = "android.provider.extra.PROMPT"` |  |
| `static final String METADATA_EXIF = "android:documentExif"` |  |
| `static final String METADATA_TREE_COUNT = "android:metadataTreeCount"` |  |
| `static final String METADATA_TREE_SIZE = "android:metadataTreeSize"` |  |
| `static final String METADATA_TYPES = "android:documentMetadataTypes"` |  |
| `static final String PROVIDER_INTERFACE = "android.content.action.DOCUMENTS_PROVIDER"` |  |
| `static final String QUERY_ARG_DISPLAY_NAME = "android:query-arg-display-name"` |  |
| `static final String QUERY_ARG_EXCLUDE_MEDIA = "android:query-arg-exclude-media"` |  |
| `static final String QUERY_ARG_FILE_SIZE_OVER = "android:query-arg-file-size-over"` |  |
| `static final String QUERY_ARG_LAST_MODIFIED_AFTER = "android:query-arg-last-modified-after"` |  |
| `static final String QUERY_ARG_MIME_TYPES = "android:query-arg-mime-types"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri buildChildDocumentsUri(String, String)` |  |
| `static android.net.Uri buildChildDocumentsUriUsingTree(android.net.Uri, String)` |  |
| `static android.net.Uri buildDocumentUri(String, String)` |  |
| `static android.net.Uri buildDocumentUriUsingTree(android.net.Uri, String)` |  |
| `static android.net.Uri buildRecentDocumentsUri(String, String)` |  |
| `static android.net.Uri buildRootUri(String, String)` |  |
| `static android.net.Uri buildRootsUri(String)` |  |
| `static android.net.Uri buildSearchDocumentsUri(String, String, String)` |  |
| `static android.net.Uri buildTreeDocumentUri(String, String)` |  |
| `static boolean deleteDocument(@NonNull android.content.ContentResolver, @NonNull android.net.Uri) throws java.io.FileNotFoundException` |  |
| `static void ejectRoot(@NonNull android.content.ContentResolver, @NonNull android.net.Uri)` |  |
| `static String getDocumentId(android.net.Uri)` |  |
| `static String getRootId(android.net.Uri)` |  |
| `static String getSearchDocumentsQuery(android.net.Uri)` |  |
| `static String getTreeDocumentId(android.net.Uri)` |  |
| `static boolean isChildDocument(@NonNull android.content.ContentResolver, @NonNull android.net.Uri, @NonNull android.net.Uri) throws java.io.FileNotFoundException` |  |
| `static boolean isDocumentUri(android.content.Context, @Nullable android.net.Uri)` |  |
| `static boolean isRootUri(@NonNull android.content.Context, @Nullable android.net.Uri)` |  |
| `static boolean isRootsUri(@NonNull android.content.Context, @Nullable android.net.Uri)` |  |
| `static boolean isTreeUri(android.net.Uri)` |  |
| `static boolean removeDocument(@NonNull android.content.ContentResolver, @NonNull android.net.Uri, @NonNull android.net.Uri) throws java.io.FileNotFoundException` |  |

---

### `class static final DocumentsContract.Document`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String COLUMN_DISPLAY_NAME = "_display_name"` |  |
| `static final String COLUMN_DOCUMENT_ID = "document_id"` |  |
| `static final String COLUMN_FLAGS = "flags"` |  |
| `static final String COLUMN_ICON = "icon"` |  |
| `static final String COLUMN_LAST_MODIFIED = "last_modified"` |  |
| `static final String COLUMN_MIME_TYPE = "mime_type"` |  |
| `static final String COLUMN_SIZE = "_size"` |  |
| `static final String COLUMN_SUMMARY = "summary"` |  |
| `static final int FLAG_DIR_BLOCKS_OPEN_DOCUMENT_TREE = 32768` |  |
| `static final int FLAG_DIR_PREFERS_GRID = 16` |  |
| `static final int FLAG_DIR_PREFERS_LAST_MODIFIED = 32` |  |
| `static final int FLAG_DIR_SUPPORTS_CREATE = 8` |  |
| `static final int FLAG_PARTIAL = 8192` |  |
| `static final int FLAG_SUPPORTS_COPY = 128` |  |
| `static final int FLAG_SUPPORTS_DELETE = 4` |  |
| `static final int FLAG_SUPPORTS_METADATA = 16384` |  |
| `static final int FLAG_SUPPORTS_MOVE = 256` |  |
| `static final int FLAG_SUPPORTS_REMOVE = 1024` |  |
| `static final int FLAG_SUPPORTS_RENAME = 64` |  |
| `static final int FLAG_SUPPORTS_SETTINGS = 2048` |  |
| `static final int FLAG_SUPPORTS_THUMBNAIL = 1` |  |
| `static final int FLAG_SUPPORTS_WRITE = 2` |  |
| `static final int FLAG_VIRTUAL_DOCUMENT = 512` |  |
| `static final int FLAG_WEB_LINKABLE = 4096` |  |
| `static final String MIME_TYPE_DIR = "vnd.android.document/directory"` |  |

---

### `class static final DocumentsContract.Path`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DocumentsContract.Path(@Nullable String, java.util.List<java.lang.String>)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.List<java.lang.String> getPath()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final DocumentsContract.Root`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String COLUMN_AVAILABLE_BYTES = "available_bytes"` |  |
| `static final String COLUMN_CAPACITY_BYTES = "capacity_bytes"` |  |
| `static final String COLUMN_DOCUMENT_ID = "document_id"` |  |
| `static final String COLUMN_FLAGS = "flags"` |  |
| `static final String COLUMN_ICON = "icon"` |  |
| `static final String COLUMN_MIME_TYPES = "mime_types"` |  |
| `static final String COLUMN_QUERY_ARGS = "query_args"` |  |
| `static final String COLUMN_ROOT_ID = "root_id"` |  |
| `static final String COLUMN_SUMMARY = "summary"` |  |
| `static final String COLUMN_TITLE = "title"` |  |
| `static final int FLAG_EMPTY = 64` |  |
| `static final int FLAG_LOCAL_ONLY = 2` |  |
| `static final int FLAG_SUPPORTS_CREATE = 1` |  |
| `static final int FLAG_SUPPORTS_EJECT = 32` |  |
| `static final int FLAG_SUPPORTS_IS_CHILD = 16` |  |
| `static final int FLAG_SUPPORTS_RECENTS = 4` |  |
| `static final int FLAG_SUPPORTS_SEARCH = 8` |  |
| `static final String MIME_TYPE_ITEM = "vnd.android.document/root"` |  |

---

### `class abstract DocumentsProvider`

- **继承：** `android.content.ContentProvider`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DocumentsProvider()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String copyDocument(String, String) throws java.io.FileNotFoundException` |  |
| `String createDocument(String, String, String) throws java.io.FileNotFoundException` |  |
| `android.content.IntentSender createWebLinkIntent(String, @Nullable android.os.Bundle) throws java.io.FileNotFoundException` |  |
| `final int delete(android.net.Uri, String, String[])` |  |
| `void deleteDocument(String) throws java.io.FileNotFoundException` |  |
| `void ejectRoot(String)` |  |
| `android.provider.DocumentsContract.Path findDocumentPath(@Nullable String, String) throws java.io.FileNotFoundException` |  |
| `String[] getDocumentStreamTypes(String, String)` |  |
| `String getDocumentType(String) throws java.io.FileNotFoundException` |  |
| `final String getType(android.net.Uri)` |  |
| `final android.net.Uri insert(android.net.Uri, android.content.ContentValues)` |  |
| `boolean isChildDocument(String, String)` |  |
| `String moveDocument(String, String, String) throws java.io.FileNotFoundException` |  |
| `final android.content.res.AssetFileDescriptor openAssetFile(android.net.Uri, String) throws java.io.FileNotFoundException` |  |
| `final android.content.res.AssetFileDescriptor openAssetFile(android.net.Uri, String, android.os.CancellationSignal) throws java.io.FileNotFoundException` |  |
| `abstract android.os.ParcelFileDescriptor openDocument(String, String, @Nullable android.os.CancellationSignal) throws java.io.FileNotFoundException` |  |
| `android.content.res.AssetFileDescriptor openDocumentThumbnail(String, android.graphics.Point, android.os.CancellationSignal) throws java.io.FileNotFoundException` |  |
| `final android.os.ParcelFileDescriptor openFile(android.net.Uri, String) throws java.io.FileNotFoundException` |  |
| `final android.os.ParcelFileDescriptor openFile(android.net.Uri, String, android.os.CancellationSignal) throws java.io.FileNotFoundException` |  |
| `final android.content.res.AssetFileDescriptor openTypedAssetFile(android.net.Uri, String, android.os.Bundle) throws java.io.FileNotFoundException` |  |
| `final android.content.res.AssetFileDescriptor openTypedAssetFile(android.net.Uri, String, android.os.Bundle, android.os.CancellationSignal) throws java.io.FileNotFoundException` |  |
| `android.content.res.AssetFileDescriptor openTypedDocument(String, String, android.os.Bundle, android.os.CancellationSignal) throws java.io.FileNotFoundException` |  |
| `final android.database.Cursor query(android.net.Uri, String[], String, String[], String)` |  |
| `final android.database.Cursor query(android.net.Uri, String[], android.os.Bundle, android.os.CancellationSignal)` |  |
| `abstract android.database.Cursor queryChildDocuments(String, String[], String) throws java.io.FileNotFoundException` |  |
| `android.database.Cursor queryChildDocuments(String, @Nullable String[], @Nullable android.os.Bundle) throws java.io.FileNotFoundException` |  |
| `abstract android.database.Cursor queryDocument(String, String[]) throws java.io.FileNotFoundException` |  |
| `android.database.Cursor queryRecentDocuments(String, String[]) throws java.io.FileNotFoundException` |  |
| `abstract android.database.Cursor queryRoots(String[]) throws java.io.FileNotFoundException` |  |
| `android.database.Cursor querySearchDocuments(String, String, String[]) throws java.io.FileNotFoundException` |  |
| `void removeDocument(String, String) throws java.io.FileNotFoundException` |  |
| `String renameDocument(String, String) throws java.io.FileNotFoundException` |  |
| `final void revokeDocumentPermission(String)` |  |
| `final int update(android.net.Uri, android.content.ContentValues, String, String[])` |  |

---

### `class final FontRequest`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FontRequest(@NonNull String, @NonNull String, @NonNull String)` |  |
| `FontRequest(@NonNull String, @NonNull String, @NonNull String, @NonNull java.util.List<java.util.List<byte[]>>)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `java.util.List<java.util.List<byte[]>> getCertificates()` |  |
| `String getProviderAuthority()` |  |
| `String getProviderPackage()` |  |
| `String getQuery()` |  |

---

### `class FontsContract`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.graphics.Typeface buildTypeface(@NonNull android.content.Context, @Nullable android.os.CancellationSignal, @NonNull android.provider.FontsContract.FontInfo[])` |  |
| `static void requestFonts(@NonNull android.content.Context, @NonNull android.provider.FontRequest, @NonNull android.os.Handler, @Nullable android.os.CancellationSignal, @NonNull android.provider.FontsContract.FontRequestCallback)` |  |

---

### `class static final FontsContract.Columns`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String FILE_ID = "file_id"` |  |
| `static final String ITALIC = "font_italic"` |  |
| `static final String RESULT_CODE = "result_code"` |  |
| `static final int RESULT_CODE_FONT_NOT_FOUND = 1` |  |
| `static final int RESULT_CODE_FONT_UNAVAILABLE = 2` |  |
| `static final int RESULT_CODE_MALFORMED_QUERY = 3` |  |
| `static final int RESULT_CODE_OK = 0` |  |
| `static final String TTC_INDEX = "font_ttc_index"` |  |
| `static final String VARIATION_SETTINGS = "font_variation_settings"` |  |
| `static final String WEIGHT = "font_weight"` |  |

---

### `class static FontsContract.FontFamilyResult`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int STATUS_OK = 0` |  |
| `static final int STATUS_REJECTED = 3` |  |
| `static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2` |  |
| `static final int STATUS_WRONG_CERTIFICATES = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getStatusCode()` |  |

---

### `class static FontsContract.FontInfo`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getResultCode()` |  |
| `boolean isItalic()` |  |

---

### `class static FontsContract.FontRequestCallback`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FontsContract.FontRequestCallback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FAIL_REASON_FONT_LOAD_ERROR = -3` |  |
| `static final int FAIL_REASON_FONT_NOT_FOUND = 1` |  |
| `static final int FAIL_REASON_FONT_UNAVAILABLE = 2` |  |
| `static final int FAIL_REASON_MALFORMED_QUERY = 3` |  |
| `static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1` |  |
| `static final int FAIL_REASON_WRONG_CERTIFICATES = -2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onTypefaceRequestFailed(int)` |  |
| `void onTypefaceRetrieved(android.graphics.Typeface)` |  |

---

### `class final LiveFolders` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns`
- **注解：** `@Deprecated`

---

### `class final MediaStore`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE"` |  |
| `static final String ACTION_IMAGE_CAPTURE_SECURE = "android.media.action.IMAGE_CAPTURE_SECURE"` |  |
| `static final String ACTION_REVIEW = "android.provider.action.REVIEW"` |  |
| `static final String ACTION_REVIEW_SECURE = "android.provider.action.REVIEW_SECURE"` |  |
| `static final String ACTION_VIDEO_CAPTURE = "android.media.action.VIDEO_CAPTURE"` |  |
| `static final String AUTHORITY = "media"` |  |
| `static final String EXTRA_BRIGHTNESS = "android.provider.extra.BRIGHTNESS"` |  |
| `static final String EXTRA_DURATION_LIMIT = "android.intent.extra.durationLimit"` |  |
| `static final String EXTRA_FINISH_ON_COMPLETION = "android.intent.extra.finishOnCompletion"` |  |
| `static final String EXTRA_FULL_SCREEN = "android.intent.extra.fullScreen"` |  |
| `static final String EXTRA_MEDIA_ALBUM = "android.intent.extra.album"` |  |
| `static final String EXTRA_MEDIA_ARTIST = "android.intent.extra.artist"` |  |
| `static final String EXTRA_MEDIA_FOCUS = "android.intent.extra.focus"` |  |
| `static final String EXTRA_MEDIA_GENRE = "android.intent.extra.genre"` |  |
| `static final String EXTRA_MEDIA_PLAYLIST = "android.intent.extra.playlist"` |  |
| `static final String EXTRA_MEDIA_RADIO_CHANNEL = "android.intent.extra.radio_channel"` |  |
| `static final String EXTRA_MEDIA_TITLE = "android.intent.extra.title"` |  |
| `static final String EXTRA_OUTPUT = "output"` |  |
| `static final String EXTRA_SCREEN_ORIENTATION = "android.intent.extra.screenOrientation"` |  |
| `static final String EXTRA_SHOW_ACTION_ICONS = "android.intent.extra.showActionIcons"` |  |
| `static final String EXTRA_SIZE_LIMIT = "android.intent.extra.sizeLimit"` |  |
| `static final String EXTRA_VIDEO_QUALITY = "android.intent.extra.videoQuality"` |  |
| `static final String INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH = "android.media.action.MEDIA_PLAY_FROM_SEARCH"` |  |
| `static final String INTENT_ACTION_MEDIA_SEARCH = "android.intent.action.MEDIA_SEARCH"` |  |
| `static final String INTENT_ACTION_STILL_IMAGE_CAMERA = "android.media.action.STILL_IMAGE_CAMERA"` |  |
| `static final String INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE = "android.media.action.STILL_IMAGE_CAMERA_SECURE"` |  |
| `static final String INTENT_ACTION_TEXT_OPEN_FROM_SEARCH = "android.media.action.TEXT_OPEN_FROM_SEARCH"` |  |
| `static final String INTENT_ACTION_VIDEO_CAMERA = "android.media.action.VIDEO_CAMERA"` |  |
| `static final String INTENT_ACTION_VIDEO_PLAY_FROM_SEARCH = "android.media.action.VIDEO_PLAY_FROM_SEARCH"` |  |
| `static final int MATCH_DEFAULT = 0` |  |
| `static final int MATCH_EXCLUDE = 2` |  |
| `static final int MATCH_INCLUDE = 1` |  |
| `static final int MATCH_ONLY = 3` |  |
| `static final String MEDIA_IGNORE_FILENAME = ".nomedia"` |  |
| `static final String MEDIA_SCANNER_VOLUME = "volume"` |  |
| `static final String META_DATA_REVIEW_GALLERY_PREWARM_SERVICE = "android.media.review_gallery_prewarm_service"` |  |
| `static final String META_DATA_STILL_IMAGE_CAMERA_PREWARM_SERVICE = "android.media.still_image_camera_preview_service"` |  |
| `static final String QUERY_ARG_MATCH_FAVORITE = "android:query-arg-match-favorite"` |  |
| `static final String QUERY_ARG_MATCH_PENDING = "android:query-arg-match-pending"` |  |
| `static final String QUERY_ARG_MATCH_TRASHED = "android:query-arg-match-trashed"` |  |
| `static final String QUERY_ARG_RELATED_URI = "android:query-arg-related-uri"` |  |
| `static final String UNKNOWN_STRING = "<unknown>"` |  |
| `static final String VOLUME_EXTERNAL = "external"` |  |
| `static final String VOLUME_EXTERNAL_PRIMARY = "external_primary"` |  |
| `static final String VOLUME_INTERNAL = "internal"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static long getGeneration(@NonNull android.content.Context, @NonNull String)` |  |
| `static android.net.Uri getMediaScannerUri()` |  |
| `static boolean getRequireOriginal(@NonNull android.net.Uri)` |  |

---

### `class static final MediaStore.Audio`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Audio()` |  |

---

### `interface static MediaStore.Audio.AlbumColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ALBUM = "album"` |  |
| `static final String ALBUM_ID = "album_id"` |  |
| `static final String ARTIST = "artist"` |  |
| `static final String ARTIST_ID = "artist_id"` |  |
| `static final String FIRST_YEAR = "minyear"` |  |
| `static final String LAST_YEAR = "maxyear"` |  |
| `static final String NUMBER_OF_SONGS = "numsongs"` |  |
| `static final String NUMBER_OF_SONGS_FOR_ARTIST = "numsongs_by_artist"` |  |

---

### `class static final MediaStore.Audio.Albums`

- **实现：** `android.provider.BaseColumns android.provider.MediaStore.Audio.AlbumColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Audio.Albums()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/albums"` |  |
| `static final String DEFAULT_SORT_ORDER = "album_key"` |  |
| `static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/album"` |  |
| `static final android.net.Uri EXTERNAL_CONTENT_URI` |  |
| `static final android.net.Uri INTERNAL_CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String)` |  |

---

### `interface static MediaStore.Audio.ArtistColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ARTIST = "artist"` |  |
| `static final String NUMBER_OF_ALBUMS = "number_of_albums"` |  |
| `static final String NUMBER_OF_TRACKS = "number_of_tracks"` |  |

---

### `class static final MediaStore.Audio.Artists`

- **实现：** `android.provider.BaseColumns android.provider.MediaStore.Audio.ArtistColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Audio.Artists()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/artists"` |  |
| `static final String DEFAULT_SORT_ORDER = "artist_key"` |  |
| `static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/artist"` |  |
| `static final android.net.Uri EXTERNAL_CONTENT_URI` |  |
| `static final android.net.Uri INTERNAL_CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String)` |  |

---

### `class static final MediaStore.Audio.Artists.Albums`

- **实现：** `android.provider.MediaStore.Audio.AlbumColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Audio.Artists.Albums()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String, long)` |  |

---

### `interface static MediaStore.Audio.AudioColumns`

- **继承：** `android.provider.MediaStore.MediaColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ALBUM_ID = "album_id"` |  |
| `static final String ARTIST_ID = "artist_id"` |  |
| `static final String BOOKMARK = "bookmark"` |  |
| `static final String GENRE = "genre"` |  |
| `static final String GENRE_ID = "genre_id"` |  |
| `static final String IS_ALARM = "is_alarm"` |  |
| `static final String IS_AUDIOBOOK = "is_audiobook"` |  |
| `static final String IS_MUSIC = "is_music"` |  |
| `static final String IS_NOTIFICATION = "is_notification"` |  |
| `static final String IS_PODCAST = "is_podcast"` |  |
| `static final String IS_RINGTONE = "is_ringtone"` |  |
| `static final String TITLE_RESOURCE_URI = "title_resource_uri"` |  |
| `static final String TRACK = "track"` |  |
| `static final String YEAR = "year"` |  |

---

### `class static final MediaStore.Audio.Genres`

- **实现：** `android.provider.BaseColumns android.provider.MediaStore.Audio.GenresColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Audio.Genres()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/genre"` |  |
| `static final String DEFAULT_SORT_ORDER = "name"` |  |
| `static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/genre"` |  |
| `static final android.net.Uri EXTERNAL_CONTENT_URI` |  |
| `static final android.net.Uri INTERNAL_CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String)` |  |
| `static android.net.Uri getContentUriForAudioId(String, int)` |  |

---

### `class static final MediaStore.Audio.Genres.Members`

- **实现：** `android.provider.MediaStore.Audio.AudioColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Audio.Genres.Members()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUDIO_ID = "audio_id"` |  |
| `static final String CONTENT_DIRECTORY = "members"` |  |
| `static final String DEFAULT_SORT_ORDER = "title_key"` |  |
| `static final String GENRE_ID = "genre_id"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String, long)` |  |

---

### `interface static MediaStore.Audio.GenresColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String NAME = "name"` |  |

---

### `class static final MediaStore.Audio.Media`

- **实现：** `android.provider.MediaStore.Audio.AudioColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Audio.Media()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/audio"` |  |
| `static final String DEFAULT_SORT_ORDER = "title_key"` |  |
| `static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/audio"` |  |
| `static final android.net.Uri EXTERNAL_CONTENT_URI` |  |
| `static final String EXTRA_MAX_BYTES = "android.provider.MediaStore.extra.MAX_BYTES"` |  |
| `static final android.net.Uri INTERNAL_CONTENT_URI` |  |
| `static final String RECORD_SOUND_ACTION = "android.provider.MediaStore.RECORD_SOUND"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String)` |  |

---

### `class static final MediaStore.Audio.Playlists`

- **实现：** `android.provider.BaseColumns android.provider.MediaStore.Audio.PlaylistsColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Audio.Playlists()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/playlist"` |  |
| `static final String DEFAULT_SORT_ORDER = "name"` |  |
| `static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/playlist"` |  |
| `static final android.net.Uri EXTERNAL_CONTENT_URI` |  |
| `static final android.net.Uri INTERNAL_CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String)` |  |

---

### `class static final MediaStore.Audio.Playlists.Members`

- **实现：** `android.provider.MediaStore.Audio.AudioColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Audio.Playlists.Members()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUDIO_ID = "audio_id"` |  |
| `static final String CONTENT_DIRECTORY = "members"` |  |
| `static final String DEFAULT_SORT_ORDER = "play_order"` |  |
| `static final String PLAYLIST_ID = "playlist_id"` |  |
| `static final String PLAY_ORDER = "play_order"` |  |
| `static final String _ID = "_id"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String, long)` |  |
| `static boolean moveItem(android.content.ContentResolver, long, int, int)` |  |

---

### `interface static MediaStore.Audio.PlaylistsColumns`

- **继承：** `android.provider.MediaStore.MediaColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DATE_ADDED = "date_added"` |  |
| `static final String DATE_MODIFIED = "date_modified"` |  |
| `static final String NAME = "name"` |  |

---

### `class static final MediaStore.Audio.Radio`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/radio"` |  |

---

### `interface static MediaStore.DownloadColumns`

- **继承：** `android.provider.MediaStore.MediaColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DOWNLOAD_URI = "download_uri"` |  |
| `static final String REFERER_URI = "referer_uri"` |  |

---

### `class static final MediaStore.Downloads`

- **实现：** `android.provider.MediaStore.DownloadColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/download"` |  |

---

### `class static final MediaStore.Files`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Files()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String)` |  |
| `static android.net.Uri getContentUri(String, long)` |  |

---

### `interface static MediaStore.Files.FileColumns`

- **继承：** `android.provider.MediaStore.MediaColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String MEDIA_TYPE = "media_type"` |  |
| `static final int MEDIA_TYPE_AUDIO = 2` |  |
| `static final int MEDIA_TYPE_DOCUMENT = 6` |  |
| `static final int MEDIA_TYPE_IMAGE = 1` |  |
| `static final int MEDIA_TYPE_NONE = 0` |  |
| `static final int MEDIA_TYPE_PLAYLIST = 4` |  |
| `static final int MEDIA_TYPE_SUBTITLE = 5` |  |
| `static final int MEDIA_TYPE_VIDEO = 3` |  |
| `static final String MIME_TYPE = "mime_type"` |  |
| `static final String PARENT = "parent"` |  |

---

### `class static final MediaStore.Images`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Images()` |  |

---

### `interface static MediaStore.Images.ImageColumns`

- **继承：** `android.provider.MediaStore.MediaColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DESCRIPTION = "description"` |  |
| `static final String EXPOSURE_TIME = "exposure_time"` |  |
| `static final String F_NUMBER = "f_number"` |  |
| `static final String ISO = "iso"` |  |
| `static final String IS_PRIVATE = "isprivate"` |  |
| `static final String SCENE_CAPTURE_TYPE = "scene_capture_type"` |  |

---

### `class static final MediaStore.Images.Media`

- **实现：** `android.provider.MediaStore.Images.ImageColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Images.Media()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/image"` |  |
| `static final String DEFAULT_SORT_ORDER = "bucket_display_name"` |  |
| `static final android.net.Uri EXTERNAL_CONTENT_URI` |  |
| `static final android.net.Uri INTERNAL_CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String)` |  |

---

### `class static MediaStore.Images.Thumbnails` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns`
- **注解：** `@Deprecated`

---

### `interface static MediaStore.MediaColumns`

- **继承：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ALBUM = "album"` |  |
| `static final String ALBUM_ARTIST = "album_artist"` |  |
| `static final String ARTIST = "artist"` |  |
| `static final String AUTHOR = "author"` |  |
| `static final String BITRATE = "bitrate"` |  |
| `static final String BUCKET_DISPLAY_NAME = "bucket_display_name"` |  |
| `static final String BUCKET_ID = "bucket_id"` |  |
| `static final String CAPTURE_FRAMERATE = "capture_framerate"` |  |
| `static final String CD_TRACK_NUMBER = "cd_track_number"` |  |
| `static final String COMPILATION = "compilation"` |  |
| `static final String COMPOSER = "composer"` |  |
| `static final String DATE_ADDED = "date_added"` |  |
| `static final String DATE_EXPIRES = "date_expires"` |  |
| `static final String DATE_MODIFIED = "date_modified"` |  |
| `static final String DATE_TAKEN = "datetaken"` |  |
| `static final String DISC_NUMBER = "disc_number"` |  |
| `static final String DISPLAY_NAME = "_display_name"` |  |
| `static final String DOCUMENT_ID = "document_id"` |  |
| `static final String DURATION = "duration"` |  |
| `static final String GENERATION_ADDED = "generation_added"` |  |
| `static final String GENERATION_MODIFIED = "generation_modified"` |  |
| `static final String GENRE = "genre"` |  |
| `static final String HEIGHT = "height"` |  |
| `static final String INSTANCE_ID = "instance_id"` |  |
| `static final String IS_DOWNLOAD = "is_download"` |  |
| `static final String IS_DRM = "is_drm"` |  |
| `static final String IS_FAVORITE = "is_favorite"` |  |
| `static final String IS_PENDING = "is_pending"` |  |
| `static final String IS_TRASHED = "is_trashed"` |  |
| `static final String MIME_TYPE = "mime_type"` |  |
| `static final String NUM_TRACKS = "num_tracks"` |  |
| `static final String ORIENTATION = "orientation"` |  |
| `static final String ORIGINAL_DOCUMENT_ID = "original_document_id"` |  |
| `static final String OWNER_PACKAGE_NAME = "owner_package_name"` |  |
| `static final String RELATIVE_PATH = "relative_path"` |  |
| `static final String RESOLUTION = "resolution"` |  |
| `static final String SIZE = "_size"` |  |
| `static final String TITLE = "title"` |  |
| `static final String VOLUME_NAME = "volume_name"` |  |
| `static final String WIDTH = "width"` |  |
| `static final String WRITER = "writer"` |  |
| `static final String XMP = "xmp"` |  |
| `static final String YEAR = "year"` |  |

---

### `class static final MediaStore.Video`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Video()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DEFAULT_SORT_ORDER = "_display_name"` |  |

---

### `class static final MediaStore.Video.Media`

- **实现：** `android.provider.MediaStore.Video.VideoColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaStore.Video.Media()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/video"` |  |
| `static final String DEFAULT_SORT_ORDER = "title"` |  |
| `static final android.net.Uri EXTERNAL_CONTENT_URI` |  |
| `static final android.net.Uri INTERNAL_CONTENT_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getContentUri(String)` |  |

---

### `class static MediaStore.Video.Thumbnails` ~~DEPRECATED~~

- **实现：** `android.provider.BaseColumns`
- **注解：** `@Deprecated`

---

### `interface static MediaStore.Video.VideoColumns`

- **继承：** `android.provider.MediaStore.MediaColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String BOOKMARK = "bookmark"` |  |
| `static final String CATEGORY = "category"` |  |
| `static final String COLOR_RANGE = "color_range"` |  |
| `static final String COLOR_STANDARD = "color_standard"` |  |
| `static final String COLOR_TRANSFER = "color_transfer"` |  |
| `static final String DESCRIPTION = "description"` |  |
| `static final String IS_PRIVATE = "isprivate"` |  |
| `static final String LANGUAGE = "language"` |  |
| `static final String TAGS = "tags"` |  |

---

### `interface OpenableColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DISPLAY_NAME = "_display_name"` |  |
| `static final String SIZE = "_size"` |  |

---

### `class SearchRecentSuggestions`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SearchRecentSuggestions(android.content.Context, String, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String[] QUERIES_PROJECTION_1LINE` |  |
| `static final String[] QUERIES_PROJECTION_2LINE` |  |
| `static final int QUERIES_PROJECTION_DATE_INDEX = 1` |  |
| `static final int QUERIES_PROJECTION_DISPLAY1_INDEX = 3` |  |
| `static final int QUERIES_PROJECTION_DISPLAY2_INDEX = 4` |  |
| `static final int QUERIES_PROJECTION_QUERY_INDEX = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void clearHistory()` |  |
| `void saveRecentQuery(String, String)` |  |
| `void truncateHistory(android.content.ContentResolver, int)` |  |

---

### `class final Settings`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Settings()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_ACCESSIBILITY_SETTINGS = "android.settings.ACCESSIBILITY_SETTINGS"` |  |
| `static final String ACTION_ADD_ACCOUNT = "android.settings.ADD_ACCOUNT_SETTINGS"` |  |
| `static final String ACTION_AIRPLANE_MODE_SETTINGS = "android.settings.AIRPLANE_MODE_SETTINGS"` |  |
| `static final String ACTION_APN_SETTINGS = "android.settings.APN_SETTINGS"` |  |
| `static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS"` |  |
| `static final String ACTION_APPLICATION_DEVELOPMENT_SETTINGS = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS"` |  |
| `static final String ACTION_APPLICATION_SETTINGS = "android.settings.APPLICATION_SETTINGS"` |  |
| `static final String ACTION_APP_NOTIFICATION_BUBBLE_SETTINGS = "android.settings.APP_NOTIFICATION_BUBBLE_SETTINGS"` |  |
| `static final String ACTION_APP_NOTIFICATION_SETTINGS = "android.settings.APP_NOTIFICATION_SETTINGS"` |  |
| `static final String ACTION_APP_SEARCH_SETTINGS = "android.settings.APP_SEARCH_SETTINGS"` |  |
| `static final String ACTION_APP_USAGE_SETTINGS = "android.settings.action.APP_USAGE_SETTINGS"` |  |
| `static final String ACTION_BATTERY_SAVER_SETTINGS = "android.settings.BATTERY_SAVER_SETTINGS"` |  |
| `static final String ACTION_BIOMETRIC_ENROLL = "android.settings.BIOMETRIC_ENROLL"` |  |
| `static final String ACTION_BLUETOOTH_SETTINGS = "android.settings.BLUETOOTH_SETTINGS"` |  |
| `static final String ACTION_CAPTIONING_SETTINGS = "android.settings.CAPTIONING_SETTINGS"` |  |
| `static final String ACTION_CAST_SETTINGS = "android.settings.CAST_SETTINGS"` |  |
| `static final String ACTION_CHANNEL_NOTIFICATION_SETTINGS = "android.settings.CHANNEL_NOTIFICATION_SETTINGS"` |  |
| `static final String ACTION_CONDITION_PROVIDER_SETTINGS = "android.settings.ACTION_CONDITION_PROVIDER_SETTINGS"` |  |
| `static final String ACTION_DATA_ROAMING_SETTINGS = "android.settings.DATA_ROAMING_SETTINGS"` |  |
| `static final String ACTION_DATA_USAGE_SETTINGS = "android.settings.DATA_USAGE_SETTINGS"` |  |
| `static final String ACTION_DATE_SETTINGS = "android.settings.DATE_SETTINGS"` |  |
| `static final String ACTION_DEVICE_INFO_SETTINGS = "android.settings.DEVICE_INFO_SETTINGS"` |  |
| `static final String ACTION_DISPLAY_SETTINGS = "android.settings.DISPLAY_SETTINGS"` |  |
| `static final String ACTION_DREAM_SETTINGS = "android.settings.DREAM_SETTINGS"` |  |
| `static final String ACTION_HARD_KEYBOARD_SETTINGS = "android.settings.HARD_KEYBOARD_SETTINGS"` |  |
| `static final String ACTION_HOME_SETTINGS = "android.settings.HOME_SETTINGS"` |  |
| `static final String ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS = "android.settings.IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS"` |  |
| `static final String ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS = "android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS"` |  |
| `static final String ACTION_INPUT_METHOD_SETTINGS = "android.settings.INPUT_METHOD_SETTINGS"` |  |
| `static final String ACTION_INPUT_METHOD_SUBTYPE_SETTINGS = "android.settings.INPUT_METHOD_SUBTYPE_SETTINGS"` |  |
| `static final String ACTION_INTERNAL_STORAGE_SETTINGS = "android.settings.INTERNAL_STORAGE_SETTINGS"` |  |
| `static final String ACTION_LOCALE_SETTINGS = "android.settings.LOCALE_SETTINGS"` |  |
| `static final String ACTION_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS"` |  |
| `static final String ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS = "android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS"` |  |
| `static final String ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION = "android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION"` |  |
| `static final String ACTION_MANAGE_APPLICATIONS_SETTINGS = "android.settings.MANAGE_APPLICATIONS_SETTINGS"` |  |
| `static final String ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION = "android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION"` |  |
| `static final String ACTION_MANAGE_DEFAULT_APPS_SETTINGS = "android.settings.MANAGE_DEFAULT_APPS_SETTINGS"` |  |
| `static final String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION"` |  |
| `static final String ACTION_MANAGE_UNKNOWN_APP_SOURCES = "android.settings.MANAGE_UNKNOWN_APP_SOURCES"` |  |
| `static final String ACTION_MANAGE_WRITE_SETTINGS = "android.settings.action.MANAGE_WRITE_SETTINGS"` |  |
| `static final String ACTION_MEMORY_CARD_SETTINGS = "android.settings.MEMORY_CARD_SETTINGS"` |  |
| `static final String ACTION_NETWORK_OPERATOR_SETTINGS = "android.settings.NETWORK_OPERATOR_SETTINGS"` |  |
| `static final String ACTION_NFCSHARING_SETTINGS = "android.settings.NFCSHARING_SETTINGS"` |  |
| `static final String ACTION_NFC_PAYMENT_SETTINGS = "android.settings.NFC_PAYMENT_SETTINGS"` |  |
| `static final String ACTION_NFC_SETTINGS = "android.settings.NFC_SETTINGS"` |  |
| `static final String ACTION_NIGHT_DISPLAY_SETTINGS = "android.settings.NIGHT_DISPLAY_SETTINGS"` |  |
| `static final String ACTION_NOTIFICATION_ASSISTANT_SETTINGS = "android.settings.NOTIFICATION_ASSISTANT_SETTINGS"` |  |
| `static final String ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS = "android.settings.NOTIFICATION_LISTENER_DETAIL_SETTINGS"` |  |
| `static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"` |  |
| `static final String ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS = "android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS"` |  |
| `static final String ACTION_PRINT_SETTINGS = "android.settings.ACTION_PRINT_SETTINGS"` |  |
| `static final String ACTION_PRIVACY_SETTINGS = "android.settings.PRIVACY_SETTINGS"` |  |
| `static final String ACTION_PROCESS_WIFI_EASY_CONNECT_URI = "android.settings.PROCESS_WIFI_EASY_CONNECT_URI"` |  |
| `static final String ACTION_QUICK_ACCESS_WALLET_SETTINGS = "android.settings.QUICK_ACCESS_WALLET_SETTINGS"` |  |
| `static final String ACTION_QUICK_LAUNCH_SETTINGS = "android.settings.QUICK_LAUNCH_SETTINGS"` |  |
| `static final String ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"` |  |
| `static final String ACTION_REQUEST_SET_AUTOFILL_SERVICE = "android.settings.REQUEST_SET_AUTOFILL_SERVICE"` |  |
| `static final String ACTION_SEARCH_SETTINGS = "android.search.action.SEARCH_SETTINGS"` |  |
| `static final String ACTION_SECURITY_SETTINGS = "android.settings.SECURITY_SETTINGS"` |  |
| `static final String ACTION_SETTINGS = "android.settings.SETTINGS"` |  |
| `static final String ACTION_SHOW_REGULATORY_INFO = "android.settings.SHOW_REGULATORY_INFO"` |  |
| `static final String ACTION_SHOW_WORK_POLICY_INFO = "android.settings.SHOW_WORK_POLICY_INFO"` |  |
| `static final String ACTION_SOUND_SETTINGS = "android.settings.SOUND_SETTINGS"` |  |
| `static final String ACTION_SYNC_SETTINGS = "android.settings.SYNC_SETTINGS"` |  |
| `static final String ACTION_USAGE_ACCESS_SETTINGS = "android.settings.USAGE_ACCESS_SETTINGS"` |  |
| `static final String ACTION_USER_DICTIONARY_SETTINGS = "android.settings.USER_DICTIONARY_SETTINGS"` |  |
| `static final String ACTION_VOICE_CONTROL_AIRPLANE_MODE = "android.settings.VOICE_CONTROL_AIRPLANE_MODE"` |  |
| `static final String ACTION_VOICE_CONTROL_BATTERY_SAVER_MODE = "android.settings.VOICE_CONTROL_BATTERY_SAVER_MODE"` |  |
| `static final String ACTION_VOICE_CONTROL_DO_NOT_DISTURB_MODE = "android.settings.VOICE_CONTROL_DO_NOT_DISTURB_MODE"` |  |
| `static final String ACTION_VOICE_INPUT_SETTINGS = "android.settings.VOICE_INPUT_SETTINGS"` |  |
| `static final String ACTION_VPN_SETTINGS = "android.settings.VPN_SETTINGS"` |  |
| `static final String ACTION_VR_LISTENER_SETTINGS = "android.settings.VR_LISTENER_SETTINGS"` |  |
| `static final String ACTION_WEBVIEW_SETTINGS = "android.settings.WEBVIEW_SETTINGS"` |  |
| `static final String ACTION_WIFI_ADD_NETWORKS = "android.settings.WIFI_ADD_NETWORKS"` |  |
| `static final String ACTION_WIFI_IP_SETTINGS = "android.settings.WIFI_IP_SETTINGS"` |  |
| `static final String ACTION_WIFI_SETTINGS = "android.settings.WIFI_SETTINGS"` |  |
| `static final String ACTION_WIRELESS_SETTINGS = "android.settings.WIRELESS_SETTINGS"` |  |
| `static final String ACTION_ZEN_MODE_PRIORITY_SETTINGS = "android.settings.ZEN_MODE_PRIORITY_SETTINGS"` |  |
| `static final int ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED = 1` |  |
| `static final int ADD_WIFI_RESULT_ALREADY_EXISTS = 2` |  |
| `static final int ADD_WIFI_RESULT_SUCCESS = 0` |  |
| `static final String AUTHORITY = "settings"` |  |
| `static final String EXTRA_ACCOUNT_TYPES = "account_types"` |  |
| `static final String EXTRA_AIRPLANE_MODE_ENABLED = "airplane_mode_enabled"` |  |
| `static final String EXTRA_APP_PACKAGE = "android.provider.extra.APP_PACKAGE"` |  |
| `static final String EXTRA_AUTHORITIES = "authorities"` |  |
| `static final String EXTRA_BATTERY_SAVER_MODE_ENABLED = "android.settings.extra.battery_saver_mode_enabled"` |  |
| `static final String EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED = "android.provider.extra.BIOMETRIC_AUTHENTICATORS_ALLOWED"` |  |
| `static final String EXTRA_CHANNEL_ID = "android.provider.extra.CHANNEL_ID"` |  |
| `static final String EXTRA_CONVERSATION_ID = "android.provider.extra.CONVERSATION_ID"` |  |
| `static final String EXTRA_DO_NOT_DISTURB_MODE_ENABLED = "android.settings.extra.do_not_disturb_mode_enabled"` |  |
| `static final String EXTRA_DO_NOT_DISTURB_MODE_MINUTES = "android.settings.extra.do_not_disturb_mode_minutes"` |  |
| `static final String EXTRA_EASY_CONNECT_ATTEMPTED_SSID = "android.provider.extra.EASY_CONNECT_ATTEMPTED_SSID"` |  |
| `static final String EXTRA_EASY_CONNECT_BAND_LIST = "android.provider.extra.EASY_CONNECT_BAND_LIST"` |  |
| `static final String EXTRA_EASY_CONNECT_CHANNEL_LIST = "android.provider.extra.EASY_CONNECT_CHANNEL_LIST"` |  |
| `static final String EXTRA_EASY_CONNECT_ERROR_CODE = "android.provider.extra.EASY_CONNECT_ERROR_CODE"` |  |
| `static final String EXTRA_INPUT_METHOD_ID = "input_method_id"` |  |
| `static final String EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME = "android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME"` |  |
| `static final String EXTRA_SUB_ID = "android.provider.extra.SUB_ID"` |  |
| `static final String EXTRA_WIFI_NETWORK_LIST = "android.provider.extra.WIFI_NETWORK_LIST"` |  |
| `static final String EXTRA_WIFI_NETWORK_RESULT_LIST = "android.provider.extra.WIFI_NETWORK_RESULT_LIST"` |  |
| `static final String INTENT_CATEGORY_USAGE_ACCESS_CONFIG = "android.intent.category.USAGE_ACCESS_CONFIG"` |  |
| `static final String METADATA_USAGE_ACCESS_REASON = "android.settings.metadata.USAGE_ACCESS_REASON"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean canDrawOverlays(android.content.Context)` |  |

---

### `class static final Settings.Global`

- **继承：** `android.provider.Settings.NameValueTable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Settings.Global()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ADB_ENABLED = "adb_enabled"` |  |
| `static final String AIRPLANE_MODE_ON = "airplane_mode_on"` |  |
| `static final String AIRPLANE_MODE_RADIOS = "airplane_mode_radios"` |  |
| `static final String ALWAYS_FINISH_ACTIVITIES = "always_finish_activities"` |  |
| `static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale"` |  |
| `static final String APPLY_RAMPING_RINGER = "apply_ramping_ringer"` |  |
| `static final String AUTO_TIME = "auto_time"` |  |
| `static final String AUTO_TIME_ZONE = "auto_time_zone"` |  |
| `static final String BLUETOOTH_ON = "bluetooth_on"` |  |
| `static final String BOOT_COUNT = "boot_count"` |  |
| `static final String CONTACT_METADATA_SYNC_ENABLED = "contact_metadata_sync_enabled"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DATA_ROAMING = "data_roaming"` |  |
| `static final String DEBUG_APP = "debug_app"` |  |
| `static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled"` |  |
| `static final String DEVICE_NAME = "device_name"` |  |
| `static final String DEVICE_PROVISIONED = "device_provisioned"` |  |
| `static final String HTTP_PROXY = "http_proxy"` |  |
| `static final String MODE_RINGER = "mode_ringer"` |  |
| `static final String NETWORK_PREFERENCE = "network_preference"` |  |
| `static final String RADIO_BLUETOOTH = "bluetooth"` |  |
| `static final String RADIO_CELL = "cell"` |  |
| `static final String RADIO_NFC = "nfc"` |  |
| `static final String RADIO_WIFI = "wifi"` |  |
| `static final String STAY_ON_WHILE_PLUGGED_IN = "stay_on_while_plugged_in"` |  |
| `static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale"` |  |
| `static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled"` |  |
| `static final String USE_GOOGLE_MAIL = "use_google_mail"` |  |
| `static final String WAIT_FOR_DEBUGGER = "wait_for_debugger"` |  |
| `static final String WIFI_DEVICE_OWNER_CONFIGS_LOCKDOWN = "wifi_device_owner_configs_lockdown"` |  |
| `static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count"` |  |
| `static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms"` |  |
| `static final String WIFI_ON = "wifi_on"` |  |
| `static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on"` |  |
| `static final String WINDOW_ANIMATION_SCALE = "window_animation_scale"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static float getFloat(android.content.ContentResolver, String, float)` |  |
| `static float getFloat(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` |  |
| `static int getInt(android.content.ContentResolver, String, int)` |  |
| `static int getInt(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` |  |
| `static long getLong(android.content.ContentResolver, String, long)` |  |
| `static long getLong(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` |  |
| `static String getString(android.content.ContentResolver, String)` |  |
| `static android.net.Uri getUriFor(String)` |  |
| `static boolean putFloat(android.content.ContentResolver, String, float)` |  |
| `static boolean putInt(android.content.ContentResolver, String, int)` |  |
| `static boolean putLong(android.content.ContentResolver, String, long)` |  |
| `static boolean putString(android.content.ContentResolver, String, String)` |  |

---

### `class static Settings.NameValueTable`

- **实现：** `android.provider.BaseColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Settings.NameValueTable()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String NAME = "name"` |  |
| `static final String VALUE = "value"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getUriFor(android.net.Uri, String)` |  |
| `static boolean putString(android.content.ContentResolver, android.net.Uri, String, String)` |  |

---

### `class static final Settings.Panel`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_INTERNET_CONNECTIVITY = "android.settings.panel.action.INTERNET_CONNECTIVITY"` |  |
| `static final String ACTION_NFC = "android.settings.panel.action.NFC"` |  |
| `static final String ACTION_VOLUME = "android.settings.panel.action.VOLUME"` |  |
| `static final String ACTION_WIFI = "android.settings.panel.action.WIFI"` |  |

---

### `class static final Settings.Secure`

- **继承：** `android.provider.Settings.NameValueTable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Settings.Secure()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACCESSIBILITY_DISPLAY_INVERSION_ENABLED = "accessibility_display_inversion_enabled"` |  |
| `static final String ACCESSIBILITY_ENABLED = "accessibility_enabled"` |  |
| `static final String ALLOWED_GEOLOCATION_ORIGINS = "allowed_geolocation_origins"` |  |
| `static final String ANDROID_ID = "android_id"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_INPUT_METHOD = "default_input_method"` |  |
| `static final String ENABLED_ACCESSIBILITY_SERVICES = "enabled_accessibility_services"` |  |
| `static final String ENABLED_INPUT_METHODS = "enabled_input_methods"` |  |
| `static final String INPUT_METHOD_SELECTOR_VISIBILITY = "input_method_selector_visibility"` |  |
| `static final int LOCATION_MODE_OFF = 0` |  |
| `static final String PARENTAL_CONTROL_ENABLED = "parental_control_enabled"` |  |
| `static final String PARENTAL_CONTROL_LAST_UPDATE = "parental_control_last_update"` |  |
| `static final String PARENTAL_CONTROL_REDIRECT_URL = "parental_control_redirect_url"` |  |
| `static final String RTT_CALLING_MODE = "rtt_calling_mode"` |  |
| `static final String SECURE_FRP_MODE = "secure_frp_mode"` |  |
| `static final String SELECTED_INPUT_METHOD_SUBTYPE = "selected_input_method_subtype"` |  |
| `static final String SETTINGS_CLASSNAME = "settings_classname"` |  |
| `static final String SKIP_FIRST_USE_HINTS = "skip_first_use_hints"` |  |
| `static final String TOUCH_EXPLORATION_ENABLED = "touch_exploration_enabled"` |  |
| `static final String TTS_DEFAULT_PITCH = "tts_default_pitch"` |  |
| `static final String TTS_DEFAULT_RATE = "tts_default_rate"` |  |
| `static final String TTS_DEFAULT_SYNTH = "tts_default_synth"` |  |
| `static final String TTS_ENABLED_PLUGINS = "tts_enabled_plugins"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static float getFloat(android.content.ContentResolver, String, float)` |  |
| `static float getFloat(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` |  |
| `static int getInt(android.content.ContentResolver, String, int)` |  |
| `static int getInt(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` |  |
| `static long getLong(android.content.ContentResolver, String, long)` |  |
| `static long getLong(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` |  |
| `static String getString(android.content.ContentResolver, String)` |  |
| `static android.net.Uri getUriFor(String)` |  |
| `static boolean putFloat(android.content.ContentResolver, String, float)` |  |
| `static boolean putInt(android.content.ContentResolver, String, int)` |  |
| `static boolean putLong(android.content.ContentResolver, String, long)` |  |
| `static boolean putString(android.content.ContentResolver, String, String)` |  |

---

### `class static Settings.SettingNotFoundException`

- **继承：** `android.util.AndroidException`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Settings.SettingNotFoundException(String)` |  |

---

### `class static final Settings.System`

- **继承：** `android.provider.Settings.NameValueTable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Settings.System()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACCELEROMETER_ROTATION = "accelerometer_rotation"` |  |
| `static final String ALARM_ALERT = "alarm_alert"` |  |
| `static final String BLUETOOTH_DISCOVERABILITY = "bluetooth_discoverability"` |  |
| `static final String BLUETOOTH_DISCOVERABILITY_TIMEOUT = "bluetooth_discoverability_timeout"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DATE_FORMAT = "date_format"` |  |
| `static final android.net.Uri DEFAULT_ALARM_ALERT_URI` |  |
| `static final android.net.Uri DEFAULT_NOTIFICATION_URI` |  |
| `static final android.net.Uri DEFAULT_RINGTONE_URI` |  |
| `static final String DTMF_TONE_TYPE_WHEN_DIALING = "dtmf_tone_type"` |  |
| `static final String DTMF_TONE_WHEN_DIALING = "dtmf_tone"` |  |
| `static final String END_BUTTON_BEHAVIOR = "end_button_behavior"` |  |
| `static final String FONT_SCALE = "font_scale"` |  |
| `static final String HAPTIC_FEEDBACK_ENABLED = "haptic_feedback_enabled"` |  |
| `static final String MODE_RINGER_STREAMS_AFFECTED = "mode_ringer_streams_affected"` |  |
| `static final String MUTE_STREAMS_AFFECTED = "mute_streams_affected"` |  |
| `static final String NOTIFICATION_SOUND = "notification_sound"` |  |
| `static final String RINGTONE = "ringtone"` |  |
| `static final String SCREEN_BRIGHTNESS = "screen_brightness"` |  |
| `static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode"` |  |
| `static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1` |  |
| `static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0` |  |
| `static final String SCREEN_OFF_TIMEOUT = "screen_off_timeout"` |  |
| `static final String SETUP_WIZARD_HAS_RUN = "setup_wizard_has_run"` |  |
| `static final String SHOW_GTALK_SERVICE_STATUS = "SHOW_GTALK_SERVICE_STATUS"` |  |
| `static final String SOUND_EFFECTS_ENABLED = "sound_effects_enabled"` |  |
| `static final String TEXT_AUTO_CAPS = "auto_caps"` |  |
| `static final String TEXT_AUTO_PUNCTUATE = "auto_punctuate"` |  |
| `static final String TEXT_AUTO_REPLACE = "auto_replace"` |  |
| `static final String TEXT_SHOW_PASSWORD = "show_password"` |  |
| `static final String TIME_12_24 = "time_12_24"` |  |
| `static final String USER_ROTATION = "user_rotation"` |  |
| `static final String VIBRATE_ON = "vibrate_on"` |  |
| `static final String VIBRATE_WHEN_RINGING = "vibrate_when_ringing"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean canWrite(android.content.Context)` |  |
| `static void getConfiguration(android.content.ContentResolver, android.content.res.Configuration)` |  |
| `static float getFloat(android.content.ContentResolver, String, float)` |  |
| `static float getFloat(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` |  |
| `static int getInt(android.content.ContentResolver, String, int)` |  |
| `static int getInt(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` |  |
| `static long getLong(android.content.ContentResolver, String, long)` |  |
| `static long getLong(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` |  |
| `static String getString(android.content.ContentResolver, String)` |  |
| `static android.net.Uri getUriFor(String)` |  |
| `static boolean putConfiguration(android.content.ContentResolver, android.content.res.Configuration)` |  |
| `static boolean putFloat(android.content.ContentResolver, String, float)` |  |
| `static boolean putInt(android.content.ContentResolver, String, int)` |  |
| `static boolean putLong(android.content.ContentResolver, String, long)` |  |
| `static boolean putString(android.content.ContentResolver, String, String)` |  |

---

### `class SettingsSlicesContract`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUTHORITY = "android.settings.slices"` |  |
| `static final android.net.Uri BASE_URI` |  |
| `static final String KEY_AIRPLANE_MODE = "airplane_mode"` |  |
| `static final String KEY_BATTERY_SAVER = "battery_saver"` |  |
| `static final String KEY_BLUETOOTH = "bluetooth"` |  |
| `static final String KEY_LOCATION = "location"` |  |
| `static final String KEY_WIFI = "wifi"` |  |
| `static final String PATH_SETTING_ACTION = "action"` |  |
| `static final String PATH_SETTING_INTENT = "intent"` |  |

---

### `class SyncStateContract`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SyncStateContract()` |  |

---

### `interface static SyncStateContract.Columns`

- **继承：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACCOUNT_NAME = "account_name"` |  |
| `static final String ACCOUNT_TYPE = "account_type"` |  |
| `static final String DATA = "data"` |  |

---

### `class static SyncStateContract.Constants`

- **实现：** `android.provider.SyncStateContract.Columns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SyncStateContract.Constants()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_DIRECTORY = "syncstate"` |  |

---

### `class static final SyncStateContract.Helpers`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SyncStateContract.Helpers()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static byte[] get(android.content.ContentProviderClient, android.net.Uri, android.accounts.Account) throws android.os.RemoteException` |  |
| `static android.util.Pair<android.net.Uri,byte[]> getWithUri(android.content.ContentProviderClient, android.net.Uri, android.accounts.Account) throws android.os.RemoteException` |  |
| `static android.net.Uri insert(android.content.ContentProviderClient, android.net.Uri, android.accounts.Account, byte[]) throws android.os.RemoteException` |  |
| `static android.content.ContentProviderOperation newSetOperation(android.net.Uri, android.accounts.Account, byte[])` |  |
| `static android.content.ContentProviderOperation newUpdateOperation(android.net.Uri, byte[])` |  |
| `static void set(android.content.ContentProviderClient, android.net.Uri, android.accounts.Account, byte[]) throws android.os.RemoteException` |  |
| `static void update(android.content.ContentProviderClient, android.net.Uri, byte[]) throws android.os.RemoteException` |  |

---

### `class final Telephony`


---

### `interface static Telephony.BaseMmsColumns`

- **继承：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_CLASS = "ct_cls"` |  |
| `static final String CONTENT_LOCATION = "ct_l"` |  |
| `static final String CONTENT_TYPE = "ct_t"` |  |
| `static final String CREATOR = "creator"` |  |
| `static final String DATE = "date"` |  |
| `static final String DATE_SENT = "date_sent"` |  |
| `static final String DELIVERY_REPORT = "d_rpt"` |  |
| `static final String DELIVERY_TIME = "d_tm"` |  |
| `static final String EXPIRY = "exp"` |  |
| `static final String LOCKED = "locked"` |  |
| `static final String MESSAGE_BOX = "msg_box"` |  |
| `static final int MESSAGE_BOX_ALL = 0` |  |
| `static final int MESSAGE_BOX_DRAFTS = 3` |  |
| `static final int MESSAGE_BOX_FAILED = 5` |  |
| `static final int MESSAGE_BOX_INBOX = 1` |  |
| `static final int MESSAGE_BOX_OUTBOX = 4` |  |
| `static final int MESSAGE_BOX_SENT = 2` |  |
| `static final String MESSAGE_CLASS = "m_cls"` |  |
| `static final String MESSAGE_ID = "m_id"` |  |
| `static final String MESSAGE_SIZE = "m_size"` |  |
| `static final String MESSAGE_TYPE = "m_type"` |  |
| `static final String MMS_VERSION = "v"` |  |
| `static final String PRIORITY = "pri"` |  |
| `static final String READ = "read"` |  |
| `static final String READ_REPORT = "rr"` |  |
| `static final String READ_STATUS = "read_status"` |  |
| `static final String REPORT_ALLOWED = "rpt_a"` |  |
| `static final String RESPONSE_STATUS = "resp_st"` |  |
| `static final String RESPONSE_TEXT = "resp_txt"` |  |
| `static final String RETRIEVE_STATUS = "retr_st"` |  |
| `static final String RETRIEVE_TEXT = "retr_txt"` |  |
| `static final String RETRIEVE_TEXT_CHARSET = "retr_txt_cs"` |  |
| `static final String SEEN = "seen"` |  |
| `static final String STATUS = "st"` |  |
| `static final String SUBJECT = "sub"` |  |
| `static final String SUBJECT_CHARSET = "sub_cs"` |  |
| `static final String SUBSCRIPTION_ID = "sub_id"` |  |
| `static final String TEXT_ONLY = "text_only"` |  |
| `static final String THREAD_ID = "thread_id"` |  |
| `static final String TRANSACTION_ID = "tr_id"` |  |

---

### `interface static Telephony.CanonicalAddressesColumns`

- **继承：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ADDRESS = "address"` |  |

---

### `class static final Telephony.CarrierId`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CARRIER_ID = "carrier_id"` |  |
| `static final String CARRIER_NAME = "carrier_name"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String SPECIFIC_CARRIER_ID = "specific_carrier_id"` |  |
| `static final String SPECIFIC_CARRIER_ID_NAME = "specific_carrier_id_name"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getUriForSubscriptionId(int)` |  |

---

### `class static final Telephony.Carriers`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String APN = "apn"` |  |
| `static final String AUTH_TYPE = "authtype"` |  |
| `static final String CARRIER_ENABLED = "carrier_enabled"` |  |
| `static final String CARRIER_ID = "carrier_id"` |  |
| `static final String CURRENT = "current"` |  |
| `static final String DEFAULT_SORT_ORDER = "name ASC"` |  |
| `static final String MMSC = "mmsc"` |  |
| `static final String MMSPORT = "mmsport"` |  |
| `static final String MMSPROXY = "mmsproxy"` |  |
| `static final String NAME = "name"` |  |
| `static final String NETWORK_TYPE_BITMASK = "network_type_bitmask"` |  |
| `static final String PASSWORD = "password"` |  |
| `static final String PORT = "port"` |  |
| `static final String PROTOCOL = "protocol"` |  |
| `static final String PROXY = "proxy"` |  |
| `static final String ROAMING_PROTOCOL = "roaming_protocol"` |  |
| `static final String SERVER = "server"` |  |
| `static final String SUBSCRIPTION_ID = "sub_id"` |  |
| `static final String TYPE = "type"` |  |
| `static final String USER = "user"` |  |

---

### `class static final Telephony.Mms`

- **实现：** `android.provider.Telephony.BaseMmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |
| `static final android.net.Uri REPORT_REQUEST_URI` |  |
| `static final android.net.Uri REPORT_STATUS_URI` |  |

---

### `class static final Telephony.Mms.Addr`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ADDRESS = "address"` |  |
| `static final String CHARSET = "charset"` |  |
| `static final String CONTACT_ID = "contact_id"` |  |
| `static final String MSG_ID = "msg_id"` |  |
| `static final String TYPE = "type"` |  |

---

### `class static final Telephony.Mms.Draft`

- **实现：** `android.provider.Telephony.BaseMmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |

---

### `class static final Telephony.Mms.Inbox`

- **实现：** `android.provider.Telephony.BaseMmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |

---

### `class static final Telephony.Mms.Intents`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONTENT_CHANGED_ACTION = "android.intent.action.CONTENT_CHANGED"` |  |
| `static final String DELETED_CONTENTS = "deleted_contents"` |  |

---

### `class static final Telephony.Mms.Outbox`

- **实现：** `android.provider.Telephony.BaseMmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |

---

### `class static final Telephony.Mms.Part`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CHARSET = "chset"` |  |
| `static final String CONTENT_DISPOSITION = "cd"` |  |
| `static final String CONTENT_ID = "cid"` |  |
| `static final String CONTENT_LOCATION = "cl"` |  |
| `static final String CONTENT_TYPE = "ct"` |  |
| `static final String CT_START = "ctt_s"` |  |
| `static final String CT_TYPE = "ctt_t"` |  |
| `static final String FILENAME = "fn"` |  |
| `static final String MSG_ID = "mid"` |  |
| `static final String NAME = "name"` |  |
| `static final String SEQ = "seq"` |  |
| `static final String TEXT = "text"` |  |
| `static final String _DATA = "_data"` |  |

---

### `class static final Telephony.Mms.Rate`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String SENT_TIME = "sent_time"` |  |

---

### `class static final Telephony.Mms.Sent`

- **实现：** `android.provider.Telephony.BaseMmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |

---

### `class static final Telephony.MmsSms`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_CONVERSATIONS_URI` |  |
| `static final android.net.Uri CONTENT_DRAFT_URI` |  |
| `static final android.net.Uri CONTENT_FILTER_BYPHONE_URI` |  |
| `static final android.net.Uri CONTENT_LOCKED_URI` |  |
| `static final android.net.Uri CONTENT_UNDELIVERED_URI` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final int ERR_TYPE_GENERIC = 1` |  |
| `static final int ERR_TYPE_GENERIC_PERMANENT = 10` |  |
| `static final int ERR_TYPE_MMS_PROTO_PERMANENT = 12` |  |
| `static final int ERR_TYPE_MMS_PROTO_TRANSIENT = 3` |  |
| `static final int ERR_TYPE_SMS_PROTO_PERMANENT = 11` |  |
| `static final int ERR_TYPE_SMS_PROTO_TRANSIENT = 2` |  |
| `static final int ERR_TYPE_TRANSPORT_FAILURE = 4` |  |
| `static final int MMS_PROTO = 1` |  |
| `static final int NO_ERROR = 0` |  |
| `static final android.net.Uri SEARCH_URI` |  |
| `static final int SMS_PROTO = 0` |  |
| `static final String TYPE_DISCRIMINATOR_COLUMN = "transport_type"` |  |

---

### `class static final Telephony.MmsSms.PendingMessages`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DUE_TIME = "due_time"` |  |
| `static final String ERROR_CODE = "err_code"` |  |
| `static final String ERROR_TYPE = "err_type"` |  |
| `static final String LAST_TRY = "last_try"` |  |
| `static final String MSG_ID = "msg_id"` |  |
| `static final String MSG_TYPE = "msg_type"` |  |
| `static final String PROTO_TYPE = "proto_type"` |  |
| `static final String RETRY_INDEX = "retry_index"` |  |
| `static final String SUBSCRIPTION_ID = "pending_sub_id"` |  |

---

### `class static final Telephony.ServiceStateTable`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUTHORITY = "service-state"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String IS_MANUAL_NETWORK_SELECTION = "is_manual_network_selection"` |  |
| `static final String VOICE_OPERATOR_NUMERIC = "voice_operator_numeric"` |  |
| `static final String VOICE_REG_STATE = "voice_reg_state"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri getUriForSubscriptionId(int)` |  |
| `static android.net.Uri getUriForSubscriptionIdAndField(int, String)` |  |

---

### `class static final Telephony.Sms`

- **实现：** `android.provider.BaseColumns android.provider.Telephony.TextBasedSmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static String getDefaultSmsPackage(android.content.Context)` |  |

---

### `class static final Telephony.Sms.Conversations`

- **实现：** `android.provider.BaseColumns android.provider.Telephony.TextBasedSmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |
| `static final String MESSAGE_COUNT = "msg_count"` |  |
| `static final String SNIPPET = "snippet"` |  |

---

### `class static final Telephony.Sms.Draft`

- **实现：** `android.provider.BaseColumns android.provider.Telephony.TextBasedSmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |

---

### `class static final Telephony.Sms.Inbox`

- **实现：** `android.provider.BaseColumns android.provider.Telephony.TextBasedSmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |

---

### `class static final Telephony.Sms.Intents`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CHANGE_DEFAULT = "android.provider.Telephony.ACTION_CHANGE_DEFAULT"` |  |
| `static final String ACTION_DEFAULT_SMS_PACKAGE_CHANGED = "android.provider.action.DEFAULT_SMS_PACKAGE_CHANGED"` |  |
| `static final String ACTION_EXTERNAL_PROVIDER_CHANGE = "android.provider.action.EXTERNAL_PROVIDER_CHANGE"` |  |
| `static final String DATA_SMS_RECEIVED_ACTION = "android.intent.action.DATA_SMS_RECEIVED"` |  |
| `static final String EXTRA_IS_DEFAULT_SMS_APP = "android.provider.extra.IS_DEFAULT_SMS_APP"` |  |
| `static final String EXTRA_PACKAGE_NAME = "package"` |  |
| `static final int RESULT_SMS_DATABASE_ERROR = 10` |  |
| `static final int RESULT_SMS_DISPATCH_FAILURE = 6` |  |
| `static final int RESULT_SMS_DUPLICATED = 5` |  |
| `static final int RESULT_SMS_GENERIC_ERROR = 2` |  |
| `static final int RESULT_SMS_HANDLED = 1` |  |
| `static final int RESULT_SMS_INVALID_URI = 11` |  |
| `static final int RESULT_SMS_NULL_MESSAGE = 8` |  |
| `static final int RESULT_SMS_NULL_PDU = 7` |  |
| `static final int RESULT_SMS_OUT_OF_MEMORY = 3` |  |
| `static final int RESULT_SMS_RECEIVED_WHILE_ENCRYPTED = 9` |  |
| `static final int RESULT_SMS_UNSUPPORTED = 4` |  |
| `static final String SIM_FULL_ACTION = "android.provider.Telephony.SIM_FULL"` |  |
| `static final String SMS_CB_RECEIVED_ACTION = "android.provider.Telephony.SMS_CB_RECEIVED"` |  |
| `static final String SMS_DELIVER_ACTION = "android.provider.Telephony.SMS_DELIVER"` |  |
| `static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED"` |  |
| `static final String SMS_REJECTED_ACTION = "android.provider.Telephony.SMS_REJECTED"` |  |
| `static final String SMS_SERVICE_CATEGORY_PROGRAM_DATA_RECEIVED_ACTION = "android.provider.Telephony.SMS_SERVICE_CATEGORY_PROGRAM_DATA_RECEIVED"` |  |
| `static final String WAP_PUSH_DELIVER_ACTION = "android.provider.Telephony.WAP_PUSH_DELIVER"` |  |
| `static final String WAP_PUSH_RECEIVED_ACTION = "android.provider.Telephony.WAP_PUSH_RECEIVED"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.telephony.SmsMessage[] getMessagesFromIntent(android.content.Intent)` |  |

---

### `class static final Telephony.Sms.Outbox`

- **实现：** `android.provider.BaseColumns android.provider.Telephony.TextBasedSmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |

---

### `class static final Telephony.Sms.Sent`

- **实现：** `android.provider.BaseColumns android.provider.Telephony.TextBasedSmsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "date DESC"` |  |

---

### `interface static Telephony.TextBasedSmsColumns`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ADDRESS = "address"` |  |
| `static final String BODY = "body"` |  |
| `static final String CREATOR = "creator"` |  |
| `static final String DATE = "date"` |  |
| `static final String DATE_SENT = "date_sent"` |  |
| `static final String ERROR_CODE = "error_code"` |  |
| `static final String LOCKED = "locked"` |  |
| `static final int MESSAGE_TYPE_ALL = 0` |  |
| `static final int MESSAGE_TYPE_DRAFT = 3` |  |
| `static final int MESSAGE_TYPE_FAILED = 5` |  |
| `static final int MESSAGE_TYPE_INBOX = 1` |  |
| `static final int MESSAGE_TYPE_OUTBOX = 4` |  |
| `static final int MESSAGE_TYPE_QUEUED = 6` |  |
| `static final int MESSAGE_TYPE_SENT = 2` |  |
| `static final String PERSON = "person"` |  |
| `static final String PROTOCOL = "protocol"` |  |
| `static final String READ = "read"` |  |
| `static final String REPLY_PATH_PRESENT = "reply_path_present"` |  |
| `static final String SEEN = "seen"` |  |
| `static final String SERVICE_CENTER = "service_center"` |  |
| `static final String STATUS = "status"` |  |
| `static final int STATUS_COMPLETE = 0` |  |
| `static final int STATUS_FAILED = 64` |  |
| `static final int STATUS_NONE = -1` |  |
| `static final int STATUS_PENDING = 32` |  |
| `static final String SUBJECT = "subject"` |  |
| `static final String SUBSCRIPTION_ID = "sub_id"` |  |
| `static final String THREAD_ID = "thread_id"` |  |
| `static final String TYPE = "type"` |  |

---

### `class static final Telephony.Threads`

- **实现：** `android.provider.Telephony.ThreadsColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int BROADCAST_THREAD = 1` |  |
| `static final int COMMON_THREAD = 0` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final android.net.Uri OBSOLETE_THREADS_URI` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static long getOrCreateThreadId(android.content.Context, String)` |  |
| `static long getOrCreateThreadId(android.content.Context, java.util.Set<java.lang.String>)` |  |

---

### `interface static Telephony.ThreadsColumns`

- **继承：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ARCHIVED = "archived"` |  |
| `static final String DATE = "date"` |  |
| `static final String ERROR = "error"` |  |
| `static final String HAS_ATTACHMENT = "has_attachment"` |  |
| `static final String MESSAGE_COUNT = "message_count"` |  |
| `static final String READ = "read"` |  |
| `static final String RECIPIENT_IDS = "recipient_ids"` |  |
| `static final String SNIPPET = "snippet"` |  |
| `static final String SNIPPET_CHARSET = "snippet_cs"` |  |
| `static final String TYPE = "type"` |  |

---

### `class UserDictionary`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UserDictionary()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String AUTHORITY = "user_dictionary"` |  |
| `static final android.net.Uri CONTENT_URI` |  |

---

### `class static UserDictionary.Words`

- **实现：** `android.provider.BaseColumns`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UserDictionary.Words()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String APP_ID = "appid"` |  |
| `static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.userword"` |  |
| `static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.userword"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DEFAULT_SORT_ORDER = "frequency DESC"` |  |
| `static final String FREQUENCY = "frequency"` |  |
| `static final String LOCALE = "locale"` |  |
| `static final String SHORTCUT = "shortcut"` |  |
| `static final String WORD = "word"` |  |
| `static final String _ID = "_id"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static void addWord(android.content.Context, String, int, String, java.util.Locale)` |  |

---

### `class VoicemailContract`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_FETCH_VOICEMAIL = "android.intent.action.FETCH_VOICEMAIL"` |  |
| `static final String ACTION_NEW_VOICEMAIL = "android.intent.action.NEW_VOICEMAIL"` |  |
| `static final String ACTION_SYNC_VOICEMAIL = "android.provider.action.SYNC_VOICEMAIL"` |  |
| `static final String AUTHORITY = "com.android.voicemail"` |  |
| `static final String EXTRA_PHONE_ACCOUNT_HANDLE = "android.provider.extra.PHONE_ACCOUNT_HANDLE"` |  |
| `static final String EXTRA_SELF_CHANGE = "com.android.voicemail.extra.SELF_CHANGE"` |  |
| `static final String PARAM_KEY_SOURCE_PACKAGE = "source_package"` |  |

---

### `class static final VoicemailContract.Status`

- **实现：** `android.provider.BaseColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CONFIGURATION_STATE = "configuration_state"` |  |
| `static final int CONFIGURATION_STATE_CAN_BE_CONFIGURED = 2` |  |
| `static final int CONFIGURATION_STATE_CONFIGURING = 3` |  |
| `static final int CONFIGURATION_STATE_DISABLED = 5` |  |
| `static final int CONFIGURATION_STATE_FAILED = 4` |  |
| `static final int CONFIGURATION_STATE_NOT_CONFIGURED = 1` |  |
| `static final int CONFIGURATION_STATE_OK = 0` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DATA_CHANNEL_STATE = "data_channel_state"` |  |
| `static final int DATA_CHANNEL_STATE_BAD_CONFIGURATION = 3` |  |
| `static final int DATA_CHANNEL_STATE_COMMUNICATION_ERROR = 4` |  |
| `static final int DATA_CHANNEL_STATE_NO_CONNECTION = 1` |  |
| `static final int DATA_CHANNEL_STATE_NO_CONNECTION_CELLULAR_REQUIRED = 2` |  |
| `static final int DATA_CHANNEL_STATE_OK = 0` |  |
| `static final int DATA_CHANNEL_STATE_SERVER_CONNECTION_ERROR = 6` |  |
| `static final int DATA_CHANNEL_STATE_SERVER_ERROR = 5` |  |
| `static final String DIR_TYPE = "vnd.android.cursor.dir/voicemail.source.status"` |  |
| `static final String ITEM_TYPE = "vnd.android.cursor.item/voicemail.source.status"` |  |
| `static final String NOTIFICATION_CHANNEL_STATE = "notification_channel_state"` |  |
| `static final int NOTIFICATION_CHANNEL_STATE_MESSAGE_WAITING = 2` |  |
| `static final int NOTIFICATION_CHANNEL_STATE_NO_CONNECTION = 1` |  |
| `static final int NOTIFICATION_CHANNEL_STATE_OK = 0` |  |
| `static final String PHONE_ACCOUNT_COMPONENT_NAME = "phone_account_component_name"` |  |
| `static final String PHONE_ACCOUNT_ID = "phone_account_id"` |  |
| `static final String QUOTA_OCCUPIED = "quota_occupied"` |  |
| `static final String QUOTA_TOTAL = "quota_total"` |  |
| `static final int QUOTA_UNAVAILABLE = -1` |  |
| `static final String SETTINGS_URI = "settings_uri"` |  |
| `static final String SOURCE_PACKAGE = "source_package"` |  |
| `static final String SOURCE_TYPE = "source_type"` |  |
| `static final String VOICEMAIL_ACCESS_URI = "voicemail_access_uri"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri buildSourceUri(String)` |  |

---

### `class static final VoicemailContract.Voicemails`

- **实现：** `android.provider.BaseColumns android.provider.OpenableColumns`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ARCHIVED = "archived"` |  |
| `static final String BACKED_UP = "backed_up"` |  |
| `static final android.net.Uri CONTENT_URI` |  |
| `static final String DATE = "date"` |  |
| `static final String DELETED = "deleted"` |  |
| `static final String DIRTY = "dirty"` |  |
| `static final int DIRTY_RETAIN = -1` |  |
| `static final String DIR_TYPE = "vnd.android.cursor.dir/voicemails"` |  |
| `static final String DURATION = "duration"` |  |
| `static final String HAS_CONTENT = "has_content"` |  |
| `static final String IS_OMTP_VOICEMAIL = "is_omtp_voicemail"` |  |
| `static final String IS_READ = "is_read"` |  |
| `static final String ITEM_TYPE = "vnd.android.cursor.item/voicemail"` |  |
| `static final String LAST_MODIFIED = "last_modified"` |  |
| `static final String MIME_TYPE = "mime_type"` |  |
| `static final String NEW = "new"` |  |
| `static final String NUMBER = "number"` |  |
| `static final String PHONE_ACCOUNT_COMPONENT_NAME = "subscription_component_name"` |  |
| `static final String PHONE_ACCOUNT_ID = "subscription_id"` |  |
| `static final String RESTORED = "restored"` |  |
| `static final String SOURCE_DATA = "source_data"` |  |
| `static final String SOURCE_PACKAGE = "source_package"` |  |
| `static final String TRANSCRIPTION = "transcription"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static android.net.Uri buildSourceUri(String)` |  |

---

## Package: `android.service.autofill`

### `class abstract AutofillService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AutofillService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.service.autofill.AutofillService"` |  |
| `static final String SERVICE_META_DATA = "android.autofill"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `void onConnected()` |  |
| `void onDisconnected()` |  |
| `abstract void onFillRequest(@NonNull android.service.autofill.FillRequest, @NonNull android.os.CancellationSignal, @NonNull android.service.autofill.FillCallback)` |  |
| `abstract void onSaveRequest(@NonNull android.service.autofill.SaveRequest, @NonNull android.service.autofill.SaveCallback)` |  |

---

### `class final BatchUpdates`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static BatchUpdates.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BatchUpdates.Builder()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.service.autofill.BatchUpdates build()` |  |
| `android.service.autofill.BatchUpdates.Builder transformChild(int, @NonNull android.service.autofill.Transformation)` |  |
| `android.service.autofill.BatchUpdates.Builder updateTemplate(@NonNull android.widget.RemoteViews)` |  |

---

### `class final CharSequenceTransformation`

- **实现：** `android.os.Parcelable android.service.autofill.Transformation`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static CharSequenceTransformation.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CharSequenceTransformation.Builder(@NonNull android.view.autofill.AutofillId, @NonNull java.util.regex.Pattern, @NonNull String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.service.autofill.CharSequenceTransformation.Builder addField(@NonNull android.view.autofill.AutofillId, @NonNull java.util.regex.Pattern, @NonNull String)` |  |
| `android.service.autofill.CharSequenceTransformation build()` |  |

---

### `class final CustomDescription`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static CustomDescription.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CustomDescription.Builder(@NonNull android.widget.RemoteViews)` |  |

---

### `class final Dataset`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final Dataset.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Dataset.Builder(@NonNull android.widget.RemoteViews)` |  |
| `Dataset.Builder()` |  |

---

### `class final DateTransformation`

- **实现：** `android.os.Parcelable android.service.autofill.Transformation`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DateTransformation(@NonNull android.view.autofill.AutofillId, @NonNull android.icu.text.DateFormat)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final DateValueSanitizer`

- **实现：** `android.os.Parcelable android.service.autofill.Sanitizer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DateValueSanitizer(@NonNull android.icu.text.DateFormat)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final FieldClassification`


---

### `class static final FieldClassification.Match`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float getScore()` |  |

---

### `class final FillCallback`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onFailure(@Nullable CharSequence)` |  |
| `void onSuccess(@Nullable android.service.autofill.FillResponse)` |  |

---

### `class final FillContext`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getRequestId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final FillEventHistory`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final FillEventHistory.Event`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TYPE_AUTHENTICATION_SELECTED = 2` |  |
| `static final int TYPE_CONTEXT_COMMITTED = 4` |  |
| `static final int TYPE_DATASETS_SHOWN = 5` |  |
| `static final int TYPE_DATASET_AUTHENTICATION_SELECTED = 1` |  |
| `static final int TYPE_DATASET_SELECTED = 0` |  |
| `static final int TYPE_SAVE_SHOWN = 3` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getType()` |  |

---

### `class final FillRequest`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FLAG_COMPATIBILITY_MODE_REQUEST = 2` |  |
| `static final int FLAG_MANUAL_REQUEST = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getFlags()` |  |
| `int getId()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final FillResponse`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FLAG_DISABLE_ACTIVITY_ONLY = 2` |  |
| `static final int FLAG_TRACK_CONTEXT_COMMITED = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final FillResponse.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FillResponse.Builder()` |  |

---

### `class final ImageTransformation`

- **实现：** `android.os.Parcelable android.service.autofill.Transformation`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ImageTransformation.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ImageTransformation.Builder(@NonNull android.view.autofill.AutofillId, @NonNull java.util.regex.Pattern, @DrawableRes int, @NonNull CharSequence)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.service.autofill.ImageTransformation.Builder addOption(@NonNull java.util.regex.Pattern, @DrawableRes int, @NonNull CharSequence)` |  |
| `android.service.autofill.ImageTransformation build()` |  |

---

### `class final InlinePresentation`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `InlinePresentation(@NonNull android.app.slice.Slice, @NonNull android.widget.inline.InlinePresentationSpec, boolean)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isPinned()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final LuhnChecksumValidator`

- **实现：** `android.os.Parcelable android.service.autofill.Validator`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `LuhnChecksumValidator(@NonNull android.view.autofill.AutofillId...)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface OnClickAction`


---

### `class final RegexValidator`

- **实现：** `android.os.Parcelable android.service.autofill.Validator`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RegexValidator(@NonNull android.view.autofill.AutofillId, @NonNull java.util.regex.Pattern)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface Sanitizer`


---

### `class final SaveCallback`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onFailure(CharSequence)` |  |
| `void onSuccess()` |  |
| `void onSuccess(@NonNull android.content.IntentSender)` |  |

---

### `class final SaveInfo`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FLAG_DELAY_SAVE = 4` |  |
| `static final int FLAG_DONT_SAVE_ON_FINISH = 2` |  |
| `static final int FLAG_SAVE_ON_ALL_VIEWS_INVISIBLE = 1` |  |
| `static final int NEGATIVE_BUTTON_STYLE_CANCEL = 0` |  |
| `static final int NEGATIVE_BUTTON_STYLE_NEVER = 2` |  |
| `static final int NEGATIVE_BUTTON_STYLE_REJECT = 1` |  |
| `static final int POSITIVE_BUTTON_STYLE_CONTINUE = 1` |  |
| `static final int POSITIVE_BUTTON_STYLE_SAVE = 0` |  |
| `static final int SAVE_DATA_TYPE_ADDRESS = 2` |  |
| `static final int SAVE_DATA_TYPE_CREDIT_CARD = 4` |  |
| `static final int SAVE_DATA_TYPE_DEBIT_CARD = 32` |  |
| `static final int SAVE_DATA_TYPE_EMAIL_ADDRESS = 16` |  |
| `static final int SAVE_DATA_TYPE_GENERIC = 0` |  |
| `static final int SAVE_DATA_TYPE_GENERIC_CARD = 128` |  |
| `static final int SAVE_DATA_TYPE_PASSWORD = 1` |  |
| `static final int SAVE_DATA_TYPE_PAYMENT_CARD = 64` |  |
| `static final int SAVE_DATA_TYPE_USERNAME = 8` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final SaveInfo.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SaveInfo.Builder(int, @NonNull android.view.autofill.AutofillId[])` |  |
| `SaveInfo.Builder(int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.service.autofill.SaveInfo build()` |  |

---

### `class final SaveRequest`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final TextValueSanitizer`

- **实现：** `android.os.Parcelable android.service.autofill.Sanitizer`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TextValueSanitizer(@NonNull java.util.regex.Pattern, @NonNull String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface Transformation`


---

### `class final UserData`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getId()` |  |
| `static int getMaxCategoryCount()` |  |
| `static int getMaxFieldClassificationIdsSize()` |  |
| `static int getMaxUserDataSize()` |  |
| `static int getMaxValueLength()` |  |
| `static int getMinValueLength()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final UserData.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `UserData.Builder(@NonNull String, @NonNull String, @NonNull String)` |  |

---

### `interface Validator`


---

### `class final Validators`


---

### `class final VisibilitySetterAction`

- **实现：** `android.service.autofill.OnClickAction android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final VisibilitySetterAction.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VisibilitySetterAction.Builder(@IdRes int, int)` |  |

---

## Package: `android.service.carrier`

### `class CarrierIdentifier`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CarrierIdentifier(String, String, @Nullable String, @Nullable String, @Nullable String, @Nullable String)` |  |
| `CarrierIdentifier(@NonNull String, @NonNull String, @Nullable String, @Nullable String, @Nullable String, @Nullable String, int, int)` |  |
| `CarrierIdentifier(byte[], @Nullable String, @Nullable String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCarrierId()` |  |
| `String getMcc()` |  |
| `String getMnc()` |  |
| `int getSpecificCarrierId()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class CarrierMessagingClientService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CarrierMessagingClientService()` |  |

---

### `class abstract CarrierMessagingService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CarrierMessagingService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int DOWNLOAD_STATUS_ERROR = 2` |  |
| `static final int DOWNLOAD_STATUS_OK = 0` |  |
| `static final int DOWNLOAD_STATUS_RETRY_ON_CARRIER_NETWORK = 1` |  |
| `static final int RECEIVE_OPTIONS_DEFAULT = 0` |  |
| `static final int RECEIVE_OPTIONS_DROP = 1` |  |
| `static final int RECEIVE_OPTIONS_SKIP_NOTIFY_WHEN_CREDENTIAL_PROTECTED_STORAGE_UNAVAILABLE = 2` |  |
| `static final int SEND_FLAG_REQUEST_DELIVERY_STATUS = 1` |  |
| `static final int SEND_STATUS_ERROR = 2` |  |
| `static final int SEND_STATUS_OK = 0` |  |
| `static final int SEND_STATUS_RETRY_ON_CARRIER_NETWORK = 1` |  |
| `static final String SERVICE_INTERFACE = "android.service.carrier.CarrierMessagingService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onDownloadMms(@NonNull android.net.Uri, int, @NonNull android.net.Uri, @NonNull android.service.carrier.CarrierMessagingService.ResultCallback<java.lang.Integer>)` |  |
| `void onReceiveTextSms(@NonNull android.service.carrier.MessagePdu, @NonNull String, int, int, @NonNull android.service.carrier.CarrierMessagingService.ResultCallback<java.lang.Integer>)` |  |
| `void onSendDataSms(@NonNull byte[], int, @NonNull String, int, int, @NonNull android.service.carrier.CarrierMessagingService.ResultCallback<android.service.carrier.CarrierMessagingService.SendSmsResult>)` |  |
| `void onSendMms(@NonNull android.net.Uri, int, @Nullable android.net.Uri, @NonNull android.service.carrier.CarrierMessagingService.ResultCallback<android.service.carrier.CarrierMessagingService.SendMmsResult>)` |  |
| `void onSendMultipartTextSms(@NonNull java.util.List<java.lang.String>, int, @NonNull String, int, @NonNull android.service.carrier.CarrierMessagingService.ResultCallback<android.service.carrier.CarrierMessagingService.SendMultipartSmsResult>)` |  |
| `void onSendTextSms(@NonNull String, int, @NonNull String, int, @NonNull android.service.carrier.CarrierMessagingService.ResultCallback<android.service.carrier.CarrierMessagingService.SendSmsResult>)` |  |

---

### `interface static CarrierMessagingService.ResultCallback<T>`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onReceiveResult(@NonNull T) throws android.os.RemoteException` |  |

---

### `class static final CarrierMessagingService.SendMmsResult`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CarrierMessagingService.SendMmsResult(int, @Nullable byte[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getSendStatus()` |  |

---

### `class static final CarrierMessagingService.SendMultipartSmsResult`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CarrierMessagingService.SendMultipartSmsResult(int, @Nullable int[])` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getSendStatus()` |  |

---

### `class static final CarrierMessagingService.SendSmsResult`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CarrierMessagingService.SendSmsResult(int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getMessageRef()` |  |
| `int getSendStatus()` |  |

---

### `class abstract CarrierService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CarrierService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String CARRIER_SERVICE_INTERFACE = "android.service.carrier.CarrierService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void notifyCarrierNetworkChange(boolean)` |  |
| `abstract android.os.PersistableBundle onLoadConfig(android.service.carrier.CarrierIdentifier)` |  |

---

### `class final MessagePdu`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MessagePdu(@NonNull java.util.List<byte[]>)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.service.chooser`

### `class final ChooserTarget` ~~DEPRECATED~~

- **实现：** `android.os.Parcelable`
- **注解：** `@Deprecated`

---

### `class abstract ChooserTargetService` ~~DEPRECATED~~

- **继承：** `android.app.Service`
- **注解：** `@Deprecated`

---

## Package: `android.service.controls`

### `class final Control`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int STATUS_DISABLED = 4` |  |
| `static final int STATUS_ERROR = 3` |  |
| `static final int STATUS_NOT_FOUND = 2` |  |
| `static final int STATUS_OK = 1` |  |
| `static final int STATUS_UNKNOWN = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDeviceType()` |  |
| `int getStatus()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final Control.StatefulBuilder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Control.StatefulBuilder(@NonNull String, @NonNull android.app.PendingIntent)` |  |
| `Control.StatefulBuilder(@NonNull android.service.controls.Control)` |  |

---

### `class static final Control.StatelessBuilder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Control.StatelessBuilder(@NonNull String, @NonNull android.app.PendingIntent)` |  |
| `Control.StatelessBuilder(@NonNull android.service.controls.Control)` |  |

---

### `class abstract ControlsProviderService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ControlsProviderService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_CONTROLS = "android.service.controls.ControlsProviderService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final boolean onUnbind(@NonNull android.content.Intent)` |  |
| `abstract void performControlAction(@NonNull String, @NonNull android.service.controls.actions.ControlAction, @NonNull java.util.function.Consumer<java.lang.Integer>)` |  |
| `static void requestAddControl(@NonNull android.content.Context, @NonNull android.content.ComponentName, @NonNull android.service.controls.Control)` |  |

---

### `class DeviceTypes`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TYPE_AC_HEATER = 1` |  |
| `static final int TYPE_AC_UNIT = 2` |  |
| `static final int TYPE_AIR_FRESHENER = 3` |  |
| `static final int TYPE_AIR_PURIFIER = 4` |  |
| `static final int TYPE_AWNING = 33` |  |
| `static final int TYPE_BLINDS = 34` |  |
| `static final int TYPE_CAMERA = 50` |  |
| `static final int TYPE_CLOSET = 35` |  |
| `static final int TYPE_COFFEE_MAKER = 5` |  |
| `static final int TYPE_CURTAIN = 36` |  |
| `static final int TYPE_DEHUMIDIFIER = 6` |  |
| `static final int TYPE_DISHWASHER = 24` |  |
| `static final int TYPE_DISPLAY = 7` |  |
| `static final int TYPE_DOOR = 37` |  |
| `static final int TYPE_DOORBELL = 51` |  |
| `static final int TYPE_DRAWER = 38` |  |
| `static final int TYPE_DRYER = 25` |  |
| `static final int TYPE_FAN = 8` |  |
| `static final int TYPE_GARAGE = 39` |  |
| `static final int TYPE_GATE = 40` |  |
| `static final int TYPE_GENERIC_ARM_DISARM = -5` |  |
| `static final int TYPE_GENERIC_LOCK_UNLOCK = -4` |  |
| `static final int TYPE_GENERIC_ON_OFF = -1` |  |
| `static final int TYPE_GENERIC_OPEN_CLOSE = -3` |  |
| `static final int TYPE_GENERIC_START_STOP = -2` |  |
| `static final int TYPE_GENERIC_TEMP_SETTING = -6` |  |
| `static final int TYPE_GENERIC_VIEWSTREAM = -7` |  |
| `static final int TYPE_HEATER = 47` |  |
| `static final int TYPE_HOOD = 10` |  |
| `static final int TYPE_HUMIDIFIER = 11` |  |
| `static final int TYPE_KETTLE = 12` |  |
| `static final int TYPE_LIGHT = 13` |  |
| `static final int TYPE_LOCK = 45` |  |
| `static final int TYPE_MICROWAVE = 14` |  |
| `static final int TYPE_MOP = 26` |  |
| `static final int TYPE_MOWER = 27` |  |
| `static final int TYPE_MULTICOOKER = 28` |  |
| `static final int TYPE_OUTLET = 15` |  |
| `static final int TYPE_PERGOLA = 41` |  |
| `static final int TYPE_RADIATOR = 16` |  |
| `static final int TYPE_REFRIGERATOR = 48` |  |
| `static final int TYPE_REMOTE_CONTROL = 17` |  |
| `static final int TYPE_ROUTINE = 52` |  |
| `static final int TYPE_SECURITY_SYSTEM = 46` |  |
| `static final int TYPE_SET_TOP = 18` |  |
| `static final int TYPE_SHOWER = 29` |  |
| `static final int TYPE_SHUTTER = 42` |  |
| `static final int TYPE_SPRINKLER = 30` |  |
| `static final int TYPE_STANDMIXER = 19` |  |
| `static final int TYPE_STYLER = 20` |  |
| `static final int TYPE_SWITCH = 21` |  |
| `static final int TYPE_THERMOSTAT = 49` |  |
| `static final int TYPE_TV = 22` |  |
| `static final int TYPE_UNKNOWN = 0` |  |
| `static final int TYPE_VACUUM = 32` |  |
| `static final int TYPE_VALVE = 44` |  |
| `static final int TYPE_WASHER = 31` |  |
| `static final int TYPE_WATER_HEATER = 23` |  |
| `static final int TYPE_WINDOW = 43` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static boolean validDeviceType(int)` |  |

---

## Package: `android.service.controls.actions`

### `class final BooleanAction`

- **继承：** `android.service.controls.actions.ControlAction`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `BooleanAction(@NonNull String, boolean)` |  |
| `BooleanAction(@NonNull String, boolean, @Nullable String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getActionType()` |  |
| `boolean getNewState()` |  |

---

### `class final CommandAction`

- **继承：** `android.service.controls.actions.ControlAction`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CommandAction(@NonNull String, @Nullable String)` |  |
| `CommandAction(@NonNull String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getActionType()` |  |

---

### `class abstract ControlAction`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int RESPONSE_CHALLENGE_ACK = 3` |  |
| `static final int RESPONSE_CHALLENGE_PASSPHRASE = 5` |  |
| `static final int RESPONSE_CHALLENGE_PIN = 4` |  |
| `static final int RESPONSE_FAIL = 2` |  |
| `static final int RESPONSE_OK = 1` |  |
| `static final int RESPONSE_UNKNOWN = 0` |  |
| `static final int TYPE_BOOLEAN = 1` |  |
| `static final int TYPE_COMMAND = 5` |  |
| `static final int TYPE_ERROR = -1` |  |
| `static final int TYPE_FLOAT = 2` |  |
| `static final int TYPE_MODE = 4` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract int getActionType()` |  |
| `static final boolean isValidResponse(int)` |  |

---

### `class final FloatAction`

- **继承：** `android.service.controls.actions.ControlAction`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `FloatAction(@NonNull String, float)` |  |
| `FloatAction(@NonNull String, float, @Nullable String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getActionType()` |  |
| `float getNewValue()` |  |

---

### `class final ModeAction`

- **继承：** `android.service.controls.actions.ControlAction`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ModeAction(@NonNull String, int, @Nullable String)` |  |
| `ModeAction(@NonNull String, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getActionType()` |  |
| `int getNewMode()` |  |

---

## Package: `android.service.controls.templates`

### `class final ControlButton`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ControlButton(boolean, @NonNull CharSequence)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isChecked()` |  |

---

### `class abstract ControlTemplate`


#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TYPE_ERROR = -1` |  |
| `static final int TYPE_NO_TEMPLATE = 0` |  |
| `static final int TYPE_RANGE = 2` |  |
| `static final int TYPE_STATELESS = 8` |  |
| `static final int TYPE_TEMPERATURE = 7` |  |
| `static final int TYPE_TOGGLE = 1` |  |
| `static final int TYPE_TOGGLE_RANGE = 6` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract int getTemplateType()` |  |

---

### `class final RangeTemplate`

- **继承：** `android.service.controls.templates.ControlTemplate`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RangeTemplate(@NonNull String, float, float, float, float, @Nullable CharSequence)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `float getCurrentValue()` |  |
| `float getMaxValue()` |  |
| `float getMinValue()` |  |
| `float getStepValue()` |  |
| `int getTemplateType()` |  |

---

### `class final StatelessTemplate`

- **继承：** `android.service.controls.templates.ControlTemplate`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StatelessTemplate(@NonNull String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getTemplateType()` |  |

---

### `class final TemperatureControlTemplate`

- **继承：** `android.service.controls.templates.ControlTemplate`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TemperatureControlTemplate(@NonNull String, @NonNull android.service.controls.templates.ControlTemplate, int, int, int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FLAG_MODE_COOL = 8` |  |
| `static final int FLAG_MODE_ECO = 32` |  |
| `static final int FLAG_MODE_HEAT = 4` |  |
| `static final int FLAG_MODE_HEAT_COOL = 16` |  |
| `static final int FLAG_MODE_OFF = 2` |  |
| `static final int MODE_COOL = 3` |  |
| `static final int MODE_ECO = 5` |  |
| `static final int MODE_HEAT = 2` |  |
| `static final int MODE_HEAT_COOL = 4` |  |
| `static final int MODE_OFF = 1` |  |
| `static final int MODE_UNKNOWN = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getCurrentActiveMode()` |  |
| `int getCurrentMode()` |  |
| `int getModes()` |  |
| `int getTemplateType()` |  |

---

### `class final ToggleRangeTemplate`

- **继承：** `android.service.controls.templates.ControlTemplate`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ToggleRangeTemplate(@NonNull String, @NonNull android.service.controls.templates.ControlButton, @NonNull android.service.controls.templates.RangeTemplate)` |  |
| `ToggleRangeTemplate(@NonNull String, boolean, @NonNull CharSequence, @NonNull android.service.controls.templates.RangeTemplate)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getTemplateType()` |  |
| `boolean isChecked()` |  |

---

### `class final ToggleTemplate`

- **继承：** `android.service.controls.templates.ControlTemplate`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ToggleTemplate(@NonNull String, @NonNull android.service.controls.templates.ControlButton)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getTemplateType()` |  |
| `boolean isChecked()` |  |

---

## Package: `android.service.dreams`

### `class DreamService`

- **继承：** `android.app.Service`
- **实现：** `android.view.Window.Callback`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `DreamService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String DREAM_META_DATA = "android.service.dream"` |  |
| `static final String SERVICE_INTERFACE = "android.service.dreams.DreamService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void addContentView(android.view.View, android.view.ViewGroup.LayoutParams)` |  |
| `boolean dispatchGenericMotionEvent(android.view.MotionEvent)` |  |
| `boolean dispatchKeyEvent(android.view.KeyEvent)` |  |
| `boolean dispatchKeyShortcutEvent(android.view.KeyEvent)` |  |
| `boolean dispatchPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent)` |  |
| `boolean dispatchTouchEvent(android.view.MotionEvent)` |  |
| `boolean dispatchTrackballEvent(android.view.MotionEvent)` |  |
| `<T extends android.view.View> T findViewById(@IdRes int)` |  |
| `final void finish()` |  |
| `android.view.Window getWindow()` |  |
| `android.view.WindowManager getWindowManager()` |  |
| `boolean isFullscreen()` |  |
| `boolean isInteractive()` |  |
| `boolean isScreenBright()` |  |
| `void onActionModeFinished(android.view.ActionMode)` |  |
| `void onActionModeStarted(android.view.ActionMode)` |  |
| `void onAttachedToWindow()` |  |
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `void onContentChanged()` |  |
| `boolean onCreatePanelMenu(int, android.view.Menu)` |  |
| `android.view.View onCreatePanelView(int)` |  |
| `void onDetachedFromWindow()` |  |
| `void onDreamingStarted()` |  |
| `void onDreamingStopped()` |  |
| `boolean onMenuItemSelected(int, android.view.MenuItem)` |  |
| `boolean onMenuOpened(int, android.view.Menu)` |  |
| `void onPanelClosed(int, android.view.Menu)` |  |
| `boolean onPreparePanel(int, android.view.View, android.view.Menu)` |  |
| `boolean onSearchRequested(android.view.SearchEvent)` |  |
| `boolean onSearchRequested()` |  |
| `void onWakeUp()` |  |
| `void onWindowAttributesChanged(android.view.WindowManager.LayoutParams)` |  |
| `void onWindowFocusChanged(boolean)` |  |
| `android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback)` |  |
| `android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback, int)` |  |
| `void setContentView(@LayoutRes int)` |  |
| `void setContentView(android.view.View)` |  |
| `void setContentView(android.view.View, android.view.ViewGroup.LayoutParams)` |  |
| `void setFullscreen(boolean)` |  |
| `void setInteractive(boolean)` |  |
| `void setScreenBright(boolean)` |  |
| `final void wakeUp()` |  |

---

## Package: `android.service.media`

### `class abstract CameraPrewarmService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `CameraPrewarmService()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract void onCooldown(boolean)` |  |
| `abstract void onPrewarm()` |  |

---

### `class abstract MediaBrowserService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaBrowserService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.media.browse.MediaBrowserService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void dump(java.io.FileDescriptor, java.io.PrintWriter, String[])` |  |
| `final android.os.Bundle getBrowserRootHints()` |  |
| `final android.media.session.MediaSessionManager.RemoteUserInfo getCurrentBrowserInfo()` |  |
| `void notifyChildrenChanged(@NonNull String)` |  |
| `void notifyChildrenChanged(@NonNull String, @NonNull android.os.Bundle)` |  |
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract void onLoadChildren(@NonNull String, @NonNull android.service.media.MediaBrowserService.Result<java.util.List<android.media.browse.MediaBrowser.MediaItem>>)` |  |
| `void onLoadChildren(@NonNull String, @NonNull android.service.media.MediaBrowserService.Result<java.util.List<android.media.browse.MediaBrowser.MediaItem>>, @NonNull android.os.Bundle)` |  |
| `void onLoadItem(String, android.service.media.MediaBrowserService.Result<android.media.browse.MediaBrowser.MediaItem>)` |  |
| `void setSessionToken(android.media.session.MediaSession.Token)` |  |

---

### `class static final MediaBrowserService.BrowserRoot`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `MediaBrowserService.BrowserRoot(@NonNull String, @Nullable android.os.Bundle)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_OFFLINE = "android.service.media.extra.OFFLINE"` |  |
| `static final String EXTRA_RECENT = "android.service.media.extra.RECENT"` |  |
| `static final String EXTRA_SUGGESTED = "android.service.media.extra.SUGGESTED"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.os.Bundle getExtras()` |  |
| `String getRootId()` |  |

---

### `class MediaBrowserService.Result<T>`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void detach()` |  |
| `void sendResult(T)` |  |

---

## Package: `android.service.notification`

### `class final Condition`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `Condition(android.net.Uri, String, int)` |  |
| `Condition(android.net.Uri, String, String, String, int, int, int)` |  |
| `Condition(android.os.Parcel)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int FLAG_RELEVANT_ALWAYS = 2` |  |
| `static final int FLAG_RELEVANT_NOW = 1` |  |
| `static final String SCHEME = "condition"` |  |
| `static final int STATE_ERROR = 3` |  |
| `static final int STATE_FALSE = 0` |  |
| `static final int STATE_TRUE = 1` |  |
| `static final int STATE_UNKNOWN = 2` |  |
| `final int flags` |  |
| `final int icon` |  |
| `final android.net.Uri id` |  |
| `final String line1` |  |
| `final String line2` |  |
| `final int state` |  |
| `final String summary` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.service.notification.Condition copy()` |  |
| `int describeContents()` |  |
| `static boolean isValidId(android.net.Uri, String)` |  |
| `static android.net.Uri.Builder newId(android.content.Context)` |  |
| `static String relevanceToString(int)` |  |
| `static String stateToString(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract ConditionProviderService` ~~DEPRECATED~~

- **继承：** `android.app.Service`
- **注解：** `@Deprecated`

---

### `class abstract NotificationListenerService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NotificationListenerService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int HINT_HOST_DISABLE_CALL_EFFECTS = 4` |  |
| `static final int HINT_HOST_DISABLE_EFFECTS = 1` |  |
| `static final int HINT_HOST_DISABLE_NOTIFICATION_EFFECTS = 2` |  |
| `static final int INTERRUPTION_FILTER_ALARMS = 4` |  |
| `static final int INTERRUPTION_FILTER_ALL = 1` |  |
| `static final int INTERRUPTION_FILTER_NONE = 3` |  |
| `static final int INTERRUPTION_FILTER_PRIORITY = 2` |  |
| `static final int INTERRUPTION_FILTER_UNKNOWN = 0` |  |
| `static final int NOTIFICATION_CHANNEL_OR_GROUP_ADDED = 1` |  |
| `static final int NOTIFICATION_CHANNEL_OR_GROUP_DELETED = 3` |  |
| `static final int NOTIFICATION_CHANNEL_OR_GROUP_UPDATED = 2` |  |
| `static final int REASON_APP_CANCEL = 8` |  |
| `static final int REASON_APP_CANCEL_ALL = 9` |  |
| `static final int REASON_CANCEL = 2` |  |
| `static final int REASON_CANCEL_ALL = 3` |  |
| `static final int REASON_CHANNEL_BANNED = 17` |  |
| `static final int REASON_CLICK = 1` |  |
| `static final int REASON_ERROR = 4` |  |
| `static final int REASON_GROUP_OPTIMIZATION = 13` |  |
| `static final int REASON_GROUP_SUMMARY_CANCELED = 12` |  |
| `static final int REASON_LISTENER_CANCEL = 10` |  |
| `static final int REASON_LISTENER_CANCEL_ALL = 11` |  |
| `static final int REASON_PACKAGE_BANNED = 7` |  |
| `static final int REASON_PACKAGE_CHANGED = 5` |  |
| `static final int REASON_PACKAGE_SUSPENDED = 14` |  |
| `static final int REASON_PROFILE_TURNED_OFF = 15` |  |
| `static final int REASON_SNOOZED = 18` |  |
| `static final int REASON_TIMEOUT = 19` |  |
| `static final int REASON_UNAUTOBUNDLED = 16` |  |
| `static final int REASON_USER_STOPPED = 6` |  |
| `static final String SERVICE_INTERFACE = "android.service.notification.NotificationListenerService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final void cancelAllNotifications()` |  |
| `final void cancelNotification(String)` |  |
| `final void cancelNotifications(String[])` |  |
| `final void clearRequestedListenerHints()` |  |
| `android.service.notification.StatusBarNotification[] getActiveNotifications()` |  |
| `android.service.notification.StatusBarNotification[] getActiveNotifications(String[])` |  |
| `final int getCurrentInterruptionFilter()` |  |
| `final int getCurrentListenerHints()` |  |
| `android.service.notification.NotificationListenerService.RankingMap getCurrentRanking()` |  |
| `final java.util.List<android.app.NotificationChannelGroup> getNotificationChannelGroups(@NonNull String, @NonNull android.os.UserHandle)` |  |
| `final java.util.List<android.app.NotificationChannel> getNotificationChannels(@NonNull String, @NonNull android.os.UserHandle)` |  |
| `final android.service.notification.StatusBarNotification[] getSnoozedNotifications()` |  |
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `void onInterruptionFilterChanged(int)` |  |
| `void onListenerConnected()` |  |
| `void onListenerDisconnected()` |  |
| `void onListenerHintsChanged(int)` |  |
| `void onNotificationChannelGroupModified(String, android.os.UserHandle, android.app.NotificationChannelGroup, int)` |  |
| `void onNotificationChannelModified(String, android.os.UserHandle, android.app.NotificationChannel, int)` |  |
| `void onNotificationPosted(android.service.notification.StatusBarNotification)` |  |
| `void onNotificationPosted(android.service.notification.StatusBarNotification, android.service.notification.NotificationListenerService.RankingMap)` |  |
| `void onNotificationRankingUpdate(android.service.notification.NotificationListenerService.RankingMap)` |  |
| `void onNotificationRemoved(android.service.notification.StatusBarNotification)` |  |
| `void onNotificationRemoved(android.service.notification.StatusBarNotification, android.service.notification.NotificationListenerService.RankingMap)` |  |
| `void onNotificationRemoved(android.service.notification.StatusBarNotification, android.service.notification.NotificationListenerService.RankingMap, int)` |  |
| `void onSilentStatusBarIconsVisibilityChanged(boolean)` |  |
| `final void requestInterruptionFilter(int)` |  |
| `final void requestListenerHints(int)` |  |
| `static void requestRebind(android.content.ComponentName)` |  |
| `final void requestUnbind()` |  |
| `final void setNotificationsShown(String[])` |  |
| `final void snoozeNotification(String, long)` |  |
| `final void updateNotificationChannel(@NonNull String, @NonNull android.os.UserHandle, @NonNull android.app.NotificationChannel)` |  |

---

### `class static NotificationListenerService.Ranking`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `NotificationListenerService.Ranking()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int USER_SENTIMENT_NEGATIVE = -1` |  |
| `static final int USER_SENTIMENT_NEUTRAL = 0` |  |
| `static final int USER_SENTIMENT_POSITIVE = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean canBubble()` |  |
| `boolean canShowBadge()` |  |
| `android.app.NotificationChannel getChannel()` |  |
| `int getImportance()` |  |
| `CharSequence getImportanceExplanation()` |  |
| `String getKey()` |  |
| `long getLastAudiblyAlertedMillis()` |  |
| `String getOverrideGroupKey()` |  |
| `int getRank()` |  |
| `int getSuppressedVisualEffects()` |  |
| `int getUserSentiment()` |  |
| `boolean isAmbient()` |  |
| `boolean isSuspended()` |  |
| `boolean matchesInterruptionFilter()` |  |

---

### `class static NotificationListenerService.RankingMap`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String[] getOrderedKeys()` |  |
| `boolean getRanking(String, android.service.notification.NotificationListenerService.Ranking)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class StatusBarNotification`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `StatusBarNotification(android.os.Parcel)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.service.notification.StatusBarNotification clone()` |  |
| `int describeContents()` |  |
| `String getGroupKey()` |  |
| `int getId()` |  |
| `String getKey()` |  |
| `android.app.Notification getNotification()` |  |
| `String getOverrideGroupKey()` |  |
| `String getPackageName()` |  |
| `long getPostTime()` |  |
| `String getTag()` |  |
| `int getUid()` |  |
| `android.os.UserHandle getUser()` |  |
| `boolean isAppGroup()` |  |
| `boolean isClearable()` |  |
| `boolean isGroup()` |  |
| `boolean isOngoing()` |  |
| `void setOverrideGroupKey(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final ZenPolicy`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int CONVERSATION_SENDERS_ANYONE = 1` |  |
| `static final int CONVERSATION_SENDERS_IMPORTANT = 2` |  |
| `static final int CONVERSATION_SENDERS_NONE = 3` |  |
| `static final int CONVERSATION_SENDERS_UNSET = 0` |  |
| `static final int PEOPLE_TYPE_ANYONE = 1` |  |
| `static final int PEOPLE_TYPE_CONTACTS = 2` |  |
| `static final int PEOPLE_TYPE_NONE = 4` |  |
| `static final int PEOPLE_TYPE_STARRED = 3` |  |
| `static final int PEOPLE_TYPE_UNSET = 0` |  |
| `static final int STATE_ALLOW = 1` |  |
| `static final int STATE_DISALLOW = 2` |  |
| `static final int STATE_UNSET = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getPriorityCallSenders()` |  |
| `int getPriorityCategoryAlarms()` |  |
| `int getPriorityCategoryCalls()` |  |
| `int getPriorityCategoryConversations()` |  |
| `int getPriorityCategoryEvents()` |  |
| `int getPriorityCategoryMedia()` |  |
| `int getPriorityCategoryMessages()` |  |
| `int getPriorityCategoryReminders()` |  |
| `int getPriorityCategoryRepeatCallers()` |  |
| `int getPriorityCategorySystem()` |  |
| `int getPriorityConversationSenders()` |  |
| `int getPriorityMessageSenders()` |  |
| `int getVisualEffectAmbient()` |  |
| `int getVisualEffectBadge()` |  |
| `int getVisualEffectFullScreenIntent()` |  |
| `int getVisualEffectLights()` |  |
| `int getVisualEffectNotificationList()` |  |
| `int getVisualEffectPeek()` |  |
| `int getVisualEffectStatusBar()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final ZenPolicy.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `ZenPolicy.Builder()` |  |

---

## Package: `android.service.quickaccesswallet`

### `interface GetWalletCardsCallback`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onFailure(@NonNull android.service.quickaccesswallet.GetWalletCardsError)` |  |
| `void onSuccess(@NonNull android.service.quickaccesswallet.GetWalletCardsResponse)` |  |

---

### `class final GetWalletCardsError`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GetWalletCardsError(@Nullable android.graphics.drawable.Icon, @Nullable CharSequence)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final GetWalletCardsRequest`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GetWalletCardsRequest(int, int, int, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCardHeightPx()` |  |
| `int getCardWidthPx()` |  |
| `int getIconSizePx()` |  |
| `int getMaxCards()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final GetWalletCardsResponse`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `GetWalletCardsResponse(@NonNull java.util.List<android.service.quickaccesswallet.WalletCard>, int)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSelectedIndex()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class abstract QuickAccessWalletService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `QuickAccessWalletService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_VIEW_WALLET = "android.service.quickaccesswallet.action.VIEW_WALLET"` |  |
| `static final String ACTION_VIEW_WALLET_SETTINGS = "android.service.quickaccesswallet.action.VIEW_WALLET_SETTINGS"` |  |
| `static final String SERVICE_INTERFACE = "android.service.quickaccesswallet.QuickAccessWalletService"` |  |
| `static final String SERVICE_META_DATA = "android.quickaccesswallet"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract void onWalletCardSelected(@NonNull android.service.quickaccesswallet.SelectWalletCardRequest)` |  |
| `abstract void onWalletCardsRequested(@NonNull android.service.quickaccesswallet.GetWalletCardsRequest, @NonNull android.service.quickaccesswallet.GetWalletCardsCallback)` |  |
| `abstract void onWalletDismissed()` |  |
| `final void sendWalletServiceEvent(@NonNull android.service.quickaccesswallet.WalletServiceEvent)` |  |

---

### `class final SelectWalletCardRequest`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SelectWalletCardRequest(@NonNull String)` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final WalletCard`

- **实现：** `android.os.Parcelable`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final WalletCard.Builder`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WalletCard.Builder(@NonNull String, @NonNull android.graphics.drawable.Icon, @NonNull CharSequence, @NonNull android.app.PendingIntent)` |  |

---

### `class final WalletServiceEvent`

- **实现：** `android.os.Parcelable`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WalletServiceEvent(int)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TYPE_NFC_PAYMENT_STARTED = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getEventType()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

## Package: `android.service.quicksettings`

### `class final Tile`

- **实现：** `android.os.Parcelable`

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int STATE_ACTIVE = 2` |  |
| `static final int STATE_INACTIVE = 1` |  |
| `static final int STATE_UNAVAILABLE = 0` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `CharSequence getContentDescription()` |  |
| `android.graphics.drawable.Icon getIcon()` |  |
| `CharSequence getLabel()` |  |
| `int getState()` |  |
| `void setContentDescription(CharSequence)` |  |
| `void setIcon(android.graphics.drawable.Icon)` |  |
| `void setLabel(CharSequence)` |  |
| `void setState(int)` |  |
| `void setStateDescription(@Nullable CharSequence)` |  |
| `void setSubtitle(@Nullable CharSequence)` |  |
| `void updateTile()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class TileService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `TileService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String ACTION_QS_TILE = "android.service.quicksettings.action.QS_TILE"` |  |
| `static final String ACTION_QS_TILE_PREFERENCES = "android.service.quicksettings.action.QS_TILE_PREFERENCES"` |  |
| `static final String META_DATA_ACTIVE_TILE = "android.service.quicksettings.ACTIVE_TILE"` |  |
| `static final String META_DATA_TOGGLEABLE_TILE = "android.service.quicksettings.TOGGLEABLE_TILE"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final android.service.quicksettings.Tile getQsTile()` |  |
| `final boolean isLocked()` |  |
| `final boolean isSecure()` |  |
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `void onClick()` |  |
| `void onStartListening()` |  |
| `void onStopListening()` |  |
| `void onTileAdded()` |  |
| `void onTileRemoved()` |  |
| `static final void requestListeningState(android.content.Context, android.content.ComponentName)` |  |
| `final void showDialog(android.app.Dialog)` |  |
| `final void startActivityAndCollapse(android.content.Intent)` |  |
| `final void unlockAndRun(Runnable)` |  |

---

## Package: `android.service.restrictions`

### `class abstract RestrictionsReceiver`

- **继承：** `android.content.BroadcastReceiver`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `RestrictionsReceiver()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void onReceive(android.content.Context, android.content.Intent)` |  |
| `abstract void onRequestPermission(android.content.Context, String, String, String, android.os.PersistableBundle)` |  |

---

## Package: `android.service.textservice`

### `class abstract SpellCheckerService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `SpellCheckerService()` |  |
| `SpellCheckerService.Session()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.service.textservice.SpellCheckerService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `abstract android.service.textservice.SpellCheckerService.Session createSession()` |  |
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `android.os.Bundle getBundle()` |  |
| `String getLocale()` |  |
| `void onCancel()` |  |
| `void onClose()` |  |
| `abstract void onCreate()` |  |
| `android.view.textservice.SentenceSuggestionsInfo[] onGetSentenceSuggestionsMultiple(android.view.textservice.TextInfo[], int)` |  |
| `abstract android.view.textservice.SuggestionsInfo onGetSuggestions(android.view.textservice.TextInfo, int)` |  |
| `android.view.textservice.SuggestionsInfo[] onGetSuggestionsMultiple(android.view.textservice.TextInfo[], int, boolean)` |  |

---

## Package: `android.service.voice`

### `class AlwaysOnHotwordDetector`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `AlwaysOnHotwordDetector.Callback()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int AUDIO_CAPABILITY_ECHO_CANCELLATION = 1` |  |
| `static final int AUDIO_CAPABILITY_NOISE_SUPPRESSION = 2` |  |
| `static final int MODEL_PARAM_THRESHOLD_FACTOR = 0` |  |
| `static final int RECOGNITION_FLAG_ALLOW_MULTIPLE_TRIGGERS = 2` |  |
| `static final int RECOGNITION_FLAG_CAPTURE_TRIGGER_AUDIO = 1` |  |
| `static final int RECOGNITION_FLAG_ENABLE_AUDIO_ECHO_CANCELLATION = 4` |  |
| `static final int RECOGNITION_FLAG_ENABLE_AUDIO_NOISE_SUPPRESSION = 8` |  |
| `static final int RECOGNITION_MODE_USER_IDENTIFICATION = 2` |  |
| `static final int RECOGNITION_MODE_VOICE_TRIGGER = 1` |  |
| `static final int STATE_HARDWARE_UNAVAILABLE = -2` |  |
| `static final int STATE_KEYPHRASE_ENROLLED = 2` |  |
| `static final int STATE_KEYPHRASE_UNENROLLED = 1` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.content.Intent createEnrollIntent()` |  |
| `android.content.Intent createReEnrollIntent()` |  |
| `android.content.Intent createUnEnrollIntent()` |  |
| `int getParameter(int)` |  |
| `int getSupportedAudioCapabilities()` |  |
| `int getSupportedRecognitionModes()` |  |
| `int setParameter(int, int)` |  |
| `boolean startRecognition(int)` |  |
| `boolean stopRecognition()` |  |
| `abstract void onAvailabilityChanged(int)` |  |
| `abstract void onDetected(@NonNull android.service.voice.AlwaysOnHotwordDetector.EventPayload)` |  |
| `abstract void onError()` |  |
| `abstract void onRecognitionPaused()` |  |
| `abstract void onRecognitionResumed()` |  |

---

### `class static AlwaysOnHotwordDetector.EventPayload`


---

### `class static final AlwaysOnHotwordDetector.ModelParamRange`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `int getEnd()` |  |
| `int getStart()` |  |

---

### `class VoiceInteractionService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VoiceInteractionService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.service.voice.VoiceInteractionService"` |  |
| `static final String SERVICE_META_DATA = "android.voice_interaction"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final android.service.voice.AlwaysOnHotwordDetector createAlwaysOnHotwordDetector(String, java.util.Locale, android.service.voice.AlwaysOnHotwordDetector.Callback)` |  |
| `int getDisabledShowContext()` |  |
| `static boolean isActiveService(android.content.Context, android.content.ComponentName)` |  |
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `void onLaunchVoiceAssistFromKeyguard()` |  |
| `void onReady()` |  |
| `void onShutdown()` |  |
| `void setDisabledShowContext(int)` |  |
| `final void setUiHints(@NonNull android.os.Bundle)` |  |
| `void showSession(android.os.Bundle, int)` |  |

---

### `class VoiceInteractionSession`

- **实现：** `android.content.ComponentCallbacks2 android.view.KeyEvent.Callback`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VoiceInteractionSession(android.content.Context)` |  |
| `VoiceInteractionSession(android.content.Context, android.os.Handler)` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int SHOW_SOURCE_ACTIVITY = 16` |  |
| `static final int SHOW_SOURCE_APPLICATION = 8` |  |
| `static final int SHOW_SOURCE_ASSIST_GESTURE = 4` |  |
| `static final int SHOW_SOURCE_AUTOMOTIVE_SYSTEM_UI = 128` |  |
| `static final int SHOW_SOURCE_NOTIFICATION = 64` |  |
| `static final int SHOW_SOURCE_PUSH_TO_TALK = 32` |  |
| `static final int SHOW_WITH_ASSIST = 1` |  |
| `static final int SHOW_WITH_SCREENSHOT = 2` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void closeSystemDialogs()` |  |
| `void dump(String, java.io.FileDescriptor, java.io.PrintWriter, String[])` |  |
| `void finish()` |  |
| `android.content.Context getContext()` |  |
| `int getDisabledShowContext()` |  |
| `android.view.LayoutInflater getLayoutInflater()` |  |
| `int getUserDisabledShowContext()` |  |
| `android.app.Dialog getWindow()` |  |
| `void hide()` |  |
| `void onAssistStructureFailure(Throwable)` |  |
| `void onBackPressed()` |  |
| `void onCancelRequest(android.service.voice.VoiceInteractionSession.Request)` |  |
| `void onCloseSystemDialogs()` |  |
| `void onComputeInsets(android.service.voice.VoiceInteractionSession.Insets)` |  |
| `void onConfigurationChanged(android.content.res.Configuration)` |  |
| `void onCreate()` |  |
| `android.view.View onCreateContentView()` |  |
| `void onDestroy()` |  |
| `void onDirectActionsInvalidated(@NonNull android.service.voice.VoiceInteractionSession.ActivityId)` |  |
| `boolean[] onGetSupportedCommands(String[])` |  |
| `void onHandleAssist(@NonNull android.service.voice.VoiceInteractionSession.AssistState)` |  |
| `void onHandleScreenshot(@Nullable android.graphics.Bitmap)` |  |
| `void onHide()` |  |
| `boolean onKeyDown(int, android.view.KeyEvent)` |  |
| `boolean onKeyLongPress(int, android.view.KeyEvent)` |  |
| `boolean onKeyMultiple(int, int, android.view.KeyEvent)` |  |
| `boolean onKeyUp(int, android.view.KeyEvent)` |  |
| `void onLockscreenShown()` |  |
| `void onLowMemory()` |  |
| `void onPrepareShow(android.os.Bundle, int)` |  |
| `void onRequestAbortVoice(android.service.voice.VoiceInteractionSession.AbortVoiceRequest)` |  |
| `void onRequestCommand(android.service.voice.VoiceInteractionSession.CommandRequest)` |  |
| `void onRequestCompleteVoice(android.service.voice.VoiceInteractionSession.CompleteVoiceRequest)` |  |
| `void onRequestConfirmation(android.service.voice.VoiceInteractionSession.ConfirmationRequest)` |  |
| `void onRequestPickOption(android.service.voice.VoiceInteractionSession.PickOptionRequest)` |  |
| `void onShow(android.os.Bundle, int)` |  |
| `void onTaskFinished(android.content.Intent, int)` |  |
| `void onTaskStarted(android.content.Intent, int)` |  |
| `void onTrimMemory(int)` |  |
| `final void performDirectAction(@NonNull android.app.DirectAction, @Nullable android.os.Bundle, @Nullable android.os.CancellationSignal, @NonNull java.util.concurrent.Executor, @NonNull java.util.function.Consumer<android.os.Bundle>)` |  |
| `final void requestDirectActions(@NonNull android.service.voice.VoiceInteractionSession.ActivityId, @Nullable android.os.CancellationSignal, @NonNull java.util.concurrent.Executor, @NonNull java.util.function.Consumer<java.util.List<android.app.DirectAction>>)` |  |
| `void setContentView(android.view.View)` |  |
| `void setDisabledShowContext(int)` |  |
| `void setKeepAwake(boolean)` |  |
| `void setTheme(int)` |  |
| `void setUiEnabled(boolean)` |  |
| `void show(android.os.Bundle, int)` |  |
| `void startAssistantActivity(android.content.Intent)` |  |
| `void startVoiceActivity(android.content.Intent)` |  |

---

### `class static final VoiceInteractionSession.AbortVoiceRequest`

- **继承：** `android.service.voice.VoiceInteractionSession.Request`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void sendAbortResult(android.os.Bundle)` |  |

---

### `class static VoiceInteractionSession.ActivityId`


---

### `class static final VoiceInteractionSession.AssistState`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `boolean isFocused()` |  |

---

### `class static final VoiceInteractionSession.CommandRequest`

- **继承：** `android.service.voice.VoiceInteractionSession.Request`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `String getCommand()` |  |
| `void sendIntermediateResult(android.os.Bundle)` |  |
| `void sendResult(android.os.Bundle)` |  |

---

### `class static final VoiceInteractionSession.CompleteVoiceRequest`

- **继承：** `android.service.voice.VoiceInteractionSession.Request`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void sendCompleteResult(android.os.Bundle)` |  |

---

### `class static final VoiceInteractionSession.ConfirmationRequest`

- **继承：** `android.service.voice.VoiceInteractionSession.Request`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void sendConfirmationResult(boolean, android.os.Bundle)` |  |

---

### `class static final VoiceInteractionSession.Insets`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VoiceInteractionSession.Insets()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final int TOUCHABLE_INSETS_CONTENT = 1` |  |
| `static final int TOUCHABLE_INSETS_FRAME = 0` |  |
| `static final int TOUCHABLE_INSETS_REGION = 3` |  |
| `final android.graphics.Rect contentInsets` |  |
| `int touchableInsets` |  |
| `final android.graphics.Region touchableRegion` |  |

---

### `class static final VoiceInteractionSession.PickOptionRequest`

- **继承：** `android.service.voice.VoiceInteractionSession.Request`

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.app.VoiceInteractor.PickOptionRequest.Option[] getOptions()` |  |
| `void sendIntermediatePickOptionResult(android.app.VoiceInteractor.PickOptionRequest.Option[], android.os.Bundle)` |  |
| `void sendPickOptionResult(android.app.VoiceInteractor.PickOptionRequest.Option[], android.os.Bundle)` |  |

---

### `class static VoiceInteractionSession.Request`


#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `String getCallingPackage()` |  |
| `int getCallingUid()` |  |
| `android.os.Bundle getExtras()` |  |
| `boolean isActive()` |  |

---

### `class abstract VoiceInteractionSessionService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VoiceInteractionSessionService()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract android.service.voice.VoiceInteractionSession onNewSession(android.os.Bundle)` |  |

---

## Package: `android.service.vr`

### `class abstract VrListenerService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `VrListenerService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.service.vr.VrListenerService"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `static final boolean isVrModePackageEnabled(@NonNull android.content.Context, @NonNull android.content.ComponentName)` |  |
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `void onCurrentVrActivityChanged(android.content.ComponentName)` |  |

---

## Package: `android.service.wallpaper`

### `class abstract WallpaperService`

- **继承：** `android.app.Service`

#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WallpaperService()` |  |

#### Fields / Constants

| 签名 | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.service.wallpaper.WallpaperService"` |  |
| `static final String SERVICE_META_DATA = "android.service.wallpaper"` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract android.service.wallpaper.WallpaperService.Engine onCreateEngine()` |  |

---

### `class WallpaperService.Engine`


#### Constructors

| 签名 | Deprecated |
|-----------|:----------:|
| `WallpaperService.Engine()` |  |

#### Methods

| 签名 | Deprecated |
|-----------|:----------:|
| `void dump(String, java.io.FileDescriptor, java.io.PrintWriter, String[])` |  |
| `int getDesiredMinimumHeight()` |  |
| `int getDesiredMinimumWidth()` |  |
| `android.view.SurfaceHolder getSurfaceHolder()` |  |
| `boolean isPreview()` |  |
| `boolean isVisible()` |  |
| `void notifyColorsChanged()` |  |
| `void onApplyWindowInsets(android.view.WindowInsets)` |  |
| `android.os.Bundle onCommand(String, int, int, int, android.os.Bundle, boolean)` |  |
| `void onCreate(android.view.SurfaceHolder)` |  |
| `void onDesiredSizeChanged(int, int)` |  |
| `void onDestroy()` |  |
| `void onOffsetsChanged(float, float, float, float, int, int)` |  |
| `void onSurfaceChanged(android.view.SurfaceHolder, int, int, int)` |  |
| `void onSurfaceCreated(android.view.SurfaceHolder)` |  |
| `void onSurfaceDestroyed(android.view.SurfaceHolder)` |  |
| `void onSurfaceRedrawNeeded(android.view.SurfaceHolder)` |  |
| `void onTouchEvent(android.view.MotionEvent)` |  |
| `void onVisibilityChanged(boolean)` |  |
| `void onZoomChanged(@FloatRange(from=0.0f, to=1.0f) float)` |  |
| `void setOffsetNotificationsEnabled(boolean)` |  |
| `void setTouchEventsEnabled(boolean)` |  |

---

