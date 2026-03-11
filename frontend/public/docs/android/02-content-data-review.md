# Android 11 AOSP Code Review: Content and Data APIs

## Executive Summary

This report covers the content framework, data access APIs, and provider contracts that form the backbone of Android's data model for app development. These APIs encompass the ContentProvider/ContentResolver architecture, the Intent-based messaging system, database access through SQLite, and system provider contracts for Contacts, Calendar, and Settings.

**Key Statistics:**
- **Context.java**: 6,122 lines, 199 `@hide` annotations, 36 `@SystemApi`, 151 service constants
- **Intent.java**: 11,451 lines, 223 `@hide`, 85 `@SystemApi`, 202 ACTION_ constants, 144 EXTRA_ constants, 45 FLAG_ constants
- **ContentResolver.java**: 4,136 lines, 93 `@hide`, 5 `@SystemApi`
- **ContentProvider.java**: 2,625 lines, 28 `@hide`, 1 `@SystemApi`
- **Settings.java**: 15,217 lines, 1,065 `@hide`, 71 `@SystemApi`
- **ContactsContract.java**: 9,747 lines, 55 `@hide`

---

## 1. Context.java -- The God Object of Android

**File:** `frameworks/base/core/java/android/content/Context.java`
**Lines:** 6,122
**Type:** Abstract class

### 1.1 Purpose and Responsibility

Context is the single most important class in Android app development. It provides the interface to global information about the application environment, enabling access to application-specific resources, classes, and system services. Every Activity, Service, and Application extends Context (via ContextWrapper).

### 1.2 Key Public APIs

#### File Mode Constants (lines 137-232)
```java
public static final int MODE_PRIVATE = 0x0000;           // Default, app-only access
public static final int MODE_WORLD_READABLE = 0x0001;    // @Deprecated - SecurityException on N+
public static final int MODE_WORLD_WRITEABLE = 0x0002;   // @Deprecated - SecurityException on N+
public static final int MODE_APPEND = 0x8000;            // Append to existing file
public static final int MODE_MULTI_PROCESS = 0x0004;     // @Deprecated
public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 0x0008;
public static final int MODE_NO_LOCALIZED_COLLATORS = 0x0010;
```

#### Resource Access (lines 508-660)
```java
public abstract AssetManager getAssets();
public abstract Resources getResources();
public abstract PackageManager getPackageManager();
public abstract ContentResolver getContentResolver();      // Line 527 - Gateway to content providers
public abstract Looper getMainLooper();                    // Line 540
public Executor getMainExecutor();                        // Line 547 - Wraps main looper
public abstract Context getApplicationContext();           // Line 577
```

#### SharedPreferences (lines 898-949)
```java
public abstract SharedPreferences getSharedPreferences(String name, @PreferencesMode int mode);
public abstract SharedPreferences getSharedPreferences(File file, @PreferencesMode int mode);
public abstract boolean moveSharedPreferencesFrom(Context sourceContext, String name);
public abstract boolean deleteSharedPreferences(String name);
public abstract void reloadSharedPreferences();            // @hide
```

#### File and Directory Access (lines 965-1660)
```java
public abstract FileInputStream openFileInput(String name);
public abstract FileOutputStream openFileOutput(String name, @FileMode int mode);
public abstract File getFilesDir();
public abstract File getNoBackupFilesDir();                // Line 1118
public abstract File getExternalFilesDir(@Nullable String type); // Line 1206
public abstract File getCacheDir();                        // Line 1422
public abstract File getCodeCacheDir();                    // Line 1443
public abstract File getExternalCacheDir();                // Line 1500
public abstract File getDataDir();                         // Line 1054
```

#### Database Access (lines 1674-1760)
```java
public abstract SQLiteDatabase openOrCreateDatabase(String name, @DatabaseMode int mode,
        CursorFactory factory);
public abstract SQLiteDatabase openOrCreateDatabase(String name, @DatabaseMode int mode,
        CursorFactory factory, @Nullable DatabaseErrorHandler errorHandler);
public abstract boolean deleteDatabase(String name);
public abstract File getDatabasePath(String name);
public abstract String[] databaseList();
```

#### Activity Launch (lines 1828-1962)
```java
public abstract void startActivity(@RequiresPermission Intent intent);
public abstract void startActivity(@RequiresPermission Intent intent, @Nullable Bundle options);
public abstract void startActivities(@RequiresPermission Intent[] intents);
public abstract void startActivities(@RequiresPermission Intent[] intents, Bundle options);
```

#### Broadcast (lines 2048-2818)
```java
public abstract void sendBroadcast(@RequiresPermission Intent intent);
public abstract void sendBroadcast(@RequiresPermission Intent intent, @Nullable String receiverPermission);
public abstract void sendOrderedBroadcast(@RequiresPermission Intent intent, @Nullable String receiverPermission);
public abstract void sendOrderedBroadcast(@RequiresPermission Intent intent,
        String receiverPermission, BroadcastReceiver resultReceiver,
        Handler scheduler, int initialCode, String initialData, Bundle initialExtras);
```

#### Receiver Registration (lines 2819-2998)
```java
public abstract Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter);
public abstract Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter, int flags);
public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
        @Nullable String broadcastPermission, @Nullable Handler scheduler);
public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
        @Nullable String broadcastPermission, @Nullable Handler scheduler, int flags);
public abstract void unregisterReceiver(BroadcastReceiver receiver);
```

#### Service Binding (lines 3058-3354)
```java
public abstract ComponentName startService(Intent service);
public abstract ComponentName startForegroundService(Intent service);  // Line 3078
public abstract boolean stopService(Intent service);
public abstract boolean bindService(@RequiresPermission Intent service,
        @NonNull ServiceConnection conn, @BindServiceFlags int flags);
public abstract void unbindService(@NonNull ServiceConnection conn);
```

#### System Service Access (lines 3692-3720)
```java
public abstract @Nullable Object getSystemService(@ServiceName @NonNull String name);
public final <T> T getSystemService(@NonNull Class<T> serviceClass);     // Line 3700
public abstract @Nullable String getSystemServiceName(@NonNull Class<?> serviceClass);
```

#### Permission Checking (lines 5003-5245)
```java
public abstract int checkPermission(@NonNull String permission, int pid, int uid);
public abstract int checkCallingPermission(@NonNull String permission);
public abstract int checkCallingOrSelfPermission(@NonNull String permission);
public abstract int checkSelfPermission(@NonNull String permission);
public abstract void enforcePermission(@NonNull String permission, int pid, int uid, @Nullable String message);
public abstract void enforceCallingPermission(@NonNull String permission, @Nullable String message);
public abstract int checkUriPermission(Uri uri, int pid, int uid, @Intent.AccessUriMode int modeFlags);
public abstract void grantUriPermission(String toPackage, Uri uri, @Intent.AccessUriMode int modeFlags);
public abstract void revokeUriPermission(Uri uri, @Intent.AccessUriMode int modeFlags);
```

### 1.3 Service Name Constants (151 total)

Selected important constants (starting at line 3722):
```java
public static final String POWER_SERVICE = "power";
public static final String WINDOW_SERVICE = "window";
public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
public static final String ACTIVITY_SERVICE = "activity";
public static final String ALARM_SERVICE = "alarm";
public static final String NOTIFICATION_SERVICE = "notification";
public static final String LOCATION_SERVICE = "location";
public static final String SENSOR_SERVICE = "sensor";
public static final String STORAGE_SERVICE = "storage";
public static final String CONNECTIVITY_SERVICE = "connectivity";
public static final String WIFI_SERVICE = "wifi";
public static final String AUDIO_SERVICE = "audio";
public static final String CAMERA_SERVICE = "camera";
public static final String CLIPBOARD_SERVICE = "clipboard";
public static final String INPUT_METHOD_SERVICE = "input_method";
public static final String DOWNLOAD_SERVICE = "download";
public static final String JOB_SCHEDULER_SERVICE = "jobscheduler";
public static final String TELEPHONY_SERVICE = "phone";
public static final String DISPLAY_SERVICE = "display";
public static final String BATTERY_SERVICE = "batterymanager";
```

### 1.4 Hidden/System APIs

- **`@hide` count: 199** -- Significant number of internal-only methods
- **`@SystemApi` count: 36** -- For privileged system apps

Notable hidden APIs:
- `startActivityAsUser()` (line 1842) -- `@SystemApi`, requires `INTERACT_ACROSS_USERS`
- `sendBroadcast(Intent, String, Bundle)` (line 2208) -- `@SystemApi`, sends with options
- `registerReceiverForAllUsers()` (line 2953) -- `@SystemApi`
- `startServiceAsUser()` (line 3133) -- `@hide`
- `getSharedPrefsFile()` (line 870) -- `@hide`, `@Deprecated`
- `reloadSharedPreferences()` (line 949) -- `@hide`

### 1.5 Design Pattern

Context uses the **Abstract Factory** pattern. It is an abstract class whose concrete implementation (`ContextImpl`) is provided by the framework. `ContextWrapper` implements the **Decorator** pattern, allowing wrapping of one Context with another (used by Activity, Service, Application).

### 1.6 Security Considerations

- `MODE_WORLD_READABLE` / `MODE_WORLD_WRITEABLE` deprecated since API 17, throw `SecurityException` on N+ (line 143-177)
- `MODE_MULTI_PROCESS` deprecated -- not reliable for cross-process SharedPreferences (line 211)
- Permission enforcement via `checkPermission()` / `enforcePermission()` family
- URI permission grants (`grantUriPermission()` / `revokeUriPermission()`) for temporary access

---

## 2. ContentProvider.java -- Structured Data Sharing

**File:** `frameworks/base/core/java/android/content/ContentProvider.java`
**Lines:** 2,625
**Type:** Abstract class implementing `ContentInterface`, `ComponentCallbacks2`

### 2.1 Purpose and Responsibility

ContentProvider is the standard interface for sharing structured data between applications. It encapsulates data behind a URI-based API, providing CRUD operations and cross-process access transparently.

### 2.2 Key Abstract Methods (must implement)

```java
public abstract boolean onCreate();                                              // Line 1213
public abstract @Nullable Cursor query(@NonNull Uri uri, @Nullable String[] projection,
        @Nullable String selection, @Nullable String[] selectionArgs,
        @Nullable String sortOrder);                                             // Line 1305
public abstract @Nullable String getType(@NonNull Uri uri);                      // Line 1503
public abstract @Nullable Uri insert(@NonNull Uri uri, @Nullable ContentValues values); // Line 1648
public abstract int delete(@NonNull Uri uri, @Nullable String selection,
        @Nullable String[] selectionArgs);                                       // Line 1723
public abstract int update(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable String selection, @Nullable String[] selectionArgs);           // Line 1778
```

### 2.3 Query Evolution (3 overloads)

1. **Original** (line 1305): `query(Uri, String[], String, String[], String)` -- Selection string based
2. **With CancellationSignal** (line 1376): Adds cancellation support
3. **Bundle-based** (line 1464): `query(Uri, String[], Bundle, CancellationSignal)` -- Preferred for API 26+, supports `QUERY_ARG_SORT_COLUMNS`, `QUERY_ARG_OFFSET`, `QUERY_ARG_LIMIT`

The Bundle-based variant (line 1464-1482) automatically falls back to extracting SQL-style arguments:
```java
// Extracts QUERY_ARG_SQL_SELECTION, QUERY_ARG_SQL_SELECTION_ARGS, QUERY_ARG_SQL_SORT_ORDER
// Also builds sort clause from QUERY_ARG_SORT_COLUMNS if SQL sort is absent
```

### 2.4 Insert with Bundle extras (new in Android 11)

```java
public @Nullable Uri insert(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable Bundle extras);                                                // Line 1671
public int delete(@NonNull Uri uri, @Nullable Bundle extras);                    // Line 1755
public int update(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable Bundle extras);                                                // Line 1804
```

### 2.5 File Operations

```java
public @Nullable ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode); // Line 1875
public @Nullable AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode); // Line 2002
public @Nullable AssetFileDescriptor openTypedAssetFile(@NonNull Uri uri,
        @NonNull String mimeTypeFilter, @Nullable Bundle opts);                  // Line 2182
public @Nullable String[] getStreamTypes(@NonNull Uri uri, @NonNull String mimeTypeFilter); // Line 2133
```

### 2.6 Batch Operations

```java
public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values);        // Line 1692
public @NonNull ContentProviderResult[] applyBatch(@NonNull String authority,
        @NonNull ArrayList<ContentProviderOperation> operations);                 // Line 2410
```

### 2.7 Caller Identity (lines 951-1057)

```java
public final @Nullable String getCallingPackage();           // Line 951
public final @Nullable String getCallingAttributionTag();    // Line 971
public final @NonNull CallingIdentity clearCallingIdentity();// Line 1046
public final void restoreCallingIdentity(@NonNull CallingIdentity identity); // Line 1057
```

### 2.8 Permission Configuration

```java
public final @Nullable String getReadPermission();           // Line 1114
public final @Nullable String getWritePermission();          // Line 1136
public final @Nullable PathPermission[] getPathPermissions();// Line 1158
```

### 2.9 Hidden/System APIs

- `checkUriPermission(Uri, int, int)` (line 1614) -- `@SystemApi`, checks if a UID has permission for a URI
- `rejectInsert(Uri, ContentValues)` (line 1626) -- `@hide`, called when insert is rejected
- `getIContentProvider()` (line 2336) -- `@hide`, returns underlying IPC binder
- `attachInfoForTesting()` (line 2346) -- `@hide`, testing support

### 2.10 Design Pattern: Transport Layer

The `Transport` inner class (line 216) is the IPC bridge that handles cross-process calls. It implements permission checking, calling package tracking, and delegates to the ContentProvider's abstract methods. This is the **Proxy** pattern for cross-process communication.

### 2.11 Thread Safety

The class documentation explicitly warns (lines 98-102):
> Data access methods (such as insert and update) may be called from many threads at once, and must be thread-safe. Other methods (such as onCreate) are only called from the application main thread.

---

## 3. ContentResolver.java -- Client-Side Content Access

**File:** `frameworks/base/core/java/android/content/ContentResolver.java`
**Lines:** 4,136
**Type:** Abstract class implementing `ContentInterface`

### 3.1 Purpose and Responsibility

ContentResolver is the client-side API for accessing ContentProviders. It handles URI routing, provider acquisition (including cross-process), and provides convenience methods for CRUD operations, file access, and content observation.

### 3.2 Query Arguments (lines 275-523)

Bundle-based query system introduced in API 26:
```java
public static final String QUERY_ARG_SQL_SELECTION = "android:query-arg-sql-selection";       // Line 275
public static final String QUERY_ARG_SQL_SELECTION_ARGS = "android:query-arg-sql-selection-args"; // Line 293
public static final String QUERY_ARG_SQL_SORT_ORDER = "android:query-arg-sql-sort-order";     // Line 309
public static final String QUERY_ARG_SQL_GROUP_BY = "android:query-arg-sql-group-by";         // Line 321
public static final String QUERY_ARG_SQL_HAVING = "android:query-arg-sql-having";             // Line 331
public static final String QUERY_ARG_SQL_LIMIT = "android:query-arg-sql-limit";               // Line 344

// Structured sort arguments
public static final String QUERY_ARG_SORT_COLUMNS = "android:query-arg-sort-columns";        // Line 367
public static final String QUERY_ARG_SORT_DIRECTION = "android:query-arg-sort-direction";     // Line 388
public static final String QUERY_ARG_SORT_COLLATION = "android:query-arg-sort-collation";     // Line 414

// Paging
public static final String QUERY_ARG_OFFSET = "android:query-arg-offset";                    // Line 505
public static final String QUERY_ARG_LIMIT = "android:query-arg-limit";                      // Line 510
public static final String EXTRA_TOTAL_COUNT = "android.content.extra.TOTAL_COUNT";           // Line 523
```

### 3.3 CRUD Operations

```java
// Query (3 overloads, all final)
public final @Nullable Cursor query(Uri uri, String[] projection,
        String selection, String[] selectionArgs, String sortOrder);              // Line 1068
public final @Nullable Cursor query(Uri uri, String[] projection,
        String selection, String[] selectionArgs, String sortOrder,
        CancellationSignal cancellationSignal);                                  // Line 1110
public final @Nullable Cursor query(Uri uri, String[] projection,
        Bundle queryArgs, CancellationSignal cancellationSignal);                // Line 1154

// Insert
public final @Nullable Uri insert(@NonNull Uri url, @Nullable ContentValues values);         // Line 2109
public final @Nullable Uri insert(@NonNull Uri url, @Nullable ContentValues values,
        @Nullable Bundle extras);                                                // Line 2133

// Delete
public final int delete(@NonNull Uri url, @Nullable String where,
        @Nullable String[] selectionArgs);                                       // Line 2253
public final int delete(@NonNull Uri url, @Nullable Bundle extras);              // Line 2274

// Update
public final int update(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable String where, @Nullable String[] selectionArgs);               // Line 2315
public final int update(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable Bundle extras);                                                // Line 2340
```

### 3.4 File/Stream Access

```java
public final @Nullable InputStream openInputStream(@NonNull Uri uri);            // Line 1456
public final @Nullable OutputStream openOutputStream(@NonNull Uri uri);          // Line 1492
public final @Nullable OutputStream openOutputStream(@NonNull Uri uri, @NonNull String mode); // Line 1516
public final @Nullable ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode,
        @Nullable CancellationSignal signal);                                    // Line 1527
public final @Nullable ParcelFileDescriptor openFileDescriptor(@NonNull Uri uri,
        @NonNull String mode);                                                   // Line 1576
```

### 3.5 Content Observation

```java
public final void registerContentObserver(@NonNull Uri uri,
        boolean notifyForDescendants, @NonNull ContentObserver observer);         // Line 2611
public final void unregisterContentObserver(@NonNull ContentObserver observer);   // Line 2640
public void notifyChange(@NonNull Uri uri, @Nullable ContentObserver observer);  // Line 2671
public void notifyChange(@NonNull Uri uri, @Nullable ContentObserver observer,
        @NotifyFlags int flags);                                                 // Line 2733
```

### 3.6 Thumbnail Loading (Android 10+)

```java
public @NonNull Bitmap loadThumbnail(@NonNull Uri uri, @NonNull Size size,
        @Nullable CancellationSignal signal);                                    // Line 4021
```

### 3.7 Data Column Deprecation (Scoped Storage)

Lines 110-125 reveal the `_data` column deprecation mechanism for Android 11's Scoped Storage:
```java
public static final boolean DEPRECATE_DATA_COLUMNS = StorageManager.hasIsolatedStorage();  // @hide
public static final String DEPRECATE_DATA_PREFIX = "/mnt/content/";                        // @hide
```
This replaces direct file paths with `content://` URIs, redirected through `openFileDescriptor()`.

### 3.8 Unstable Provider Recovery

The query implementation (lines 1167-1231) demonstrates a key resilience pattern: it first tries an "unstable" provider reference, and if the remote process dies (`DeadObjectException`), it acquires a "stable" reference and retries. The exciting comment at line 1188 reads:
> The remote process has died...  but we only hold an unstable reference though, so we might recover!!!  Let's try!!!!  This is exciting!!1!!1!!!!1

### 3.9 Sync Framework Constants

```java
public static final String SYNC_EXTRAS_EXPEDITED = "expedited";         // Line 138
public static final String SYNC_EXTRAS_MANUAL = "force";                // Line 176
public static final String SYNC_EXTRAS_UPLOAD = "upload";               // Line 183
public static final String SYNC_EXTRAS_INITIALIZE = "initialize";       // Line 228
```

---

## 4. Intent.java -- The Messaging Backbone

**File:** `frameworks/base/core/java/android/content/Intent.java`
**Lines:** 11,451
**Type:** Class implementing `Parcelable`, `Cloneable`

### 4.1 Purpose and Responsibility

Intent is the fundamental messaging object in Android, used for late runtime binding between components. It describes an operation to perform: launching Activities, sending Broadcasts, starting Services. The class is essentially a passive data structure with an action, data URI, category, extras, component, and flags.

### 4.2 Scale

- **202 ACTION_ constants** -- Standard actions (ACTION_VIEW, ACTION_EDIT, ACTION_SEND, etc.)
- **144 EXTRA_ constants** -- Standard extras keys
- **45 FLAG_ constants** -- Intent flags controlling behavior
- **223 @hide annotations** -- Massive internal API surface

### 4.3 Key Constructors (lines 6742-6884)

```java
public Intent();
public Intent(Intent o);                                           // Copy constructor
public Intent(String action);
public Intent(String action, Uri uri);
public Intent(Context packageContext, Class<?> cls);                // Explicit intent
public Intent(String action, Uri uri, Context packageContext, Class<?> cls);
```

### 4.4 Core Getters/Setters

```java
// Action
public @Nullable String getAction();                               // Line 7848
public @NonNull Intent setAction(@Nullable String action);         // Line 8818

// Data
public @Nullable Uri getData();                                    // Line 7863
public @NonNull Intent setData(@Nullable Uri data);                // Line 8844
public @Nullable String getType();                                 // Line 7901
public @NonNull Intent setType(@Nullable String type);             // Line 8901
public @NonNull Intent setDataAndType(@Nullable Uri data, @Nullable String type); // Line 8961

// Component
public @NonNull Intent setComponent(@Nullable ComponentName component); // Explicit intent
public @NonNull Intent setClass(@NonNull Context packageContext, @NonNull Class<?> cls);
public @NonNull Intent setPackage(@Nullable String packageName);

// Categories
public @NonNull Intent addCategory(String category);               // Line 9041

// Flags
public @NonNull Intent addFlags(@Flags int flags);
public @NonNull Intent setFlags(@Flags int flags);

// Extras (overloaded for every primitive type + Parcelable + Serializable)
public @NonNull Intent putExtra(String name, boolean value);       // Line 9168
public @NonNull Intent putExtra(String name, int value);           // Line 9260
public @NonNull Intent putExtra(String name, String value);        // Line 9352
public @NonNull Intent putExtra(String name, Parcelable value);    // Line 9398
// ... and 20+ more putExtra overloads

public @Nullable Bundle getExtras();                               // Line 8575
```

### 4.5 Factory Methods

```java
public static Intent createChooser(Intent target, CharSequence title);           // Line 987
public static Intent createChooser(Intent target, CharSequence title, IntentSender sender); // Line 1016
public static Intent makeMainActivity(ComponentName mainActivity);               // Line 6912
public static Intent makeMainSelectorActivity(String selectorAction, String selectorCategory); // Line 6941
public static Intent makeRestartActivityTask(ComponentName mainActivity);        // Line 6965
```

### 4.6 Resolution

```java
public ComponentName resolveActivity(@NonNull PackageManager pm);                // Line 8720
public ActivityInfo resolveActivityInfo(@NonNull PackageManager pm, int flags);  // Line 8752
```

### 4.7 URI Parsing

```java
public static Intent parseUri(String uri, @UriFlags int flags);                  // Line 7001
public String toUri(@UriFlags int flags);
```

### 4.8 Key ACTION_ Constants (selected)

```
ACTION_MAIN, ACTION_VIEW, ACTION_DEFAULT, ACTION_ATTACH_DATA, ACTION_EDIT,
ACTION_INSERT_OR_EDIT, ACTION_PICK, ACTION_CREATE_SHORTCUT, ACTION_CHOOSER,
ACTION_GET_CONTENT, ACTION_DIAL, ACTION_CALL, ACTION_SEND, ACTION_SENDTO,
ACTION_SEND_MULTIPLE, ACTION_ANSWER, ACTION_INSERT, ACTION_DELETE,
ACTION_SEARCH, ACTION_WEB_SEARCH, ACTION_BOOT_COMPLETED, ACTION_POWER_CONNECTED,
ACTION_POWER_DISCONNECTED, ACTION_SHUTDOWN, ACTION_BATTERY_LOW, ACTION_BATTERY_OKAY,
ACTION_SCREEN_ON, ACTION_SCREEN_OFF, ACTION_USER_PRESENT
```

### 4.9 Key FLAG_ Constants (selected)

```
FLAG_ACTIVITY_NEW_TASK, FLAG_ACTIVITY_CLEAR_TOP, FLAG_ACTIVITY_SINGLE_TOP,
FLAG_ACTIVITY_NO_HISTORY, FLAG_ACTIVITY_CLEAR_TASK, FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
FLAG_GRANT_READ_URI_PERMISSION, FLAG_GRANT_WRITE_URI_PERMISSION,
FLAG_GRANT_PERSISTABLE_URI_PERMISSION, FLAG_GRANT_PREFIX_URI_PERMISSION,
FLAG_RECEIVER_FOREGROUND, FLAG_RECEIVER_REPLACE_PENDING
```

---

## 5. IntentFilter.java -- Intent Matching

**File:** `frameworks/base/core/java/android/content/IntentFilter.java`
**Lines:** 2,475
**Type:** Class implementing `Parcelable`

### 5.1 Purpose

IntentFilter declares structured criteria for matching Intents against actions, categories, and data (type, scheme, authority, path). Used in manifest declarations and dynamic receiver registration.

### 5.2 Match Rules (from Javadoc, lines 92-148)

Three conditions must hold for a match:
1. **Action** must match (any of the filter's actions)
2. **Category** -- ALL categories in the Intent must be present in the filter
3. **Data** -- type and scheme+authority+path must match

### 5.3 Key Methods

```java
public final void addAction(String action);                      // Line 730
public final void addCategory(String category);                  // Line 1679
public final void addDataType(String type);                      // Line 826
public final void addDataScheme(String scheme);                  // Line 1035
public final void addDataAuthority(String host, String port);    // Line 1333
public final void addDataPath(String path, int type);            // Line 1421
public final void addDataSchemeSpecificPart(String ssp, int type); // Line 1227

public final void setPriority(int priority);                     // Line 515
public final int getPriority();                                  // Line 526
public final void setAutoVerify(boolean autoVerify);             // Line 566

// Match methods
public final boolean matchAction(String action);                 // Line 770
public final int matchData(String type, String scheme, Uri data);// Line 1579
public final int matchDataAuthority(Uri data);                   // Line 1515
```

### 5.4 Priority Constants

```java
public static final int SYSTEM_HIGH_PRIORITY = 1000;             // Line 176 -- System receivers only
public static final int SYSTEM_LOW_PRIORITY = -1000;             // Line 186 -- After app receivers
```

---

## 6. BroadcastReceiver.java -- Event Handling

**File:** `frameworks/base/core/java/android/content/BroadcastReceiver.java`
**Lines:** 687
**Type:** Abstract class

### 6.1 Purpose

Base class for code that receives and handles broadcast intents. Can be registered dynamically via `Context.registerReceiver()` or statically via `<receiver>` in the manifest.

### 6.2 Key Methods

```java
public abstract void onReceive(Context context, Intent intent);  // Line 345 -- MUST implement
public final PendingResult goAsync();                            // Line 383 -- Continue after return
public IBinder peekService(Context myContext, Intent service);   // Line 403
```

### 6.3 Ordered Broadcast Support

```java
public final void setResultCode(int code);                       // Line 432
public final int getResultCode();                                // Line 442
public final void setResultData(String data);                    // Line 461
public final String getResultData();                             // Line 472
public final void setResultExtras(Bundle extras);                // Line 493
public final Bundle getResultExtras(boolean makeMap);            // Line 509
public final void setResult(int code, String data, Bundle extras); // Line 541
public final void abortBroadcast();                              // Line 572 -- Ordered only
public final boolean isOrderedBroadcast();                       // Line 591
```

### 6.4 PendingResult (goAsync pattern)

The `PendingResult` inner class (line 70) allows offloading broadcast processing to a background thread. The broadcast is kept alive until `PendingResult.finish()` is called. Critical constraints:
- 10-second timeout for foreground broadcasts (line 319)
- 30+ seconds for background broadcasts (line 367)
- Must always call `finish()` to prevent ANR

### 6.5 Hidden APIs

- `getSendingUser()` (line 639) -- `@SystemApi`, returns the user that sent the broadcast
- `setPendingResult()` / `getPendingResult()` (lines 617-627) -- `@hide`, internal plumbing

---

## 7. SharedPreferences -- Key-Value Storage

**File:** `frameworks/base/core/java/android/content/SharedPreferences.java`
**Lines:** 392
**Type:** Interface

### 7.1 Purpose

Simple persistent key-value storage for primitives and String/StringSet. Single instance per file within a process. **Not designed for cross-process use** (line 38).

### 7.2 Reading API

```java
Map<String, ?> getAll();                                         // Line 255
String getString(String key, @Nullable String defValue);         // Line 270
Set<String> getStringSet(String key, @Nullable Set<String> defValues); // Line 289
int getInt(String key, int defValue);                            // Line 303
long getLong(String key, long defValue);                         // Line 317
float getFloat(String key, float defValue);                     // Line 331
boolean getBoolean(String key, boolean defValue);               // Line 345
boolean contains(String key);                                    // Line 354
```

### 7.3 Editor Interface (write API)

```java
Editor putString(String key, @Nullable String value);            // Line 93
Editor putStringSet(String key, @Nullable Set<String> values);   // Line 106
Editor putInt(String key, int value);                            // Line 118
Editor putLong(String key, long value);                          // Line 130
Editor putFloat(String key, float value);                        // Line 142
Editor putBoolean(String key, boolean value);                    // Line 154
Editor remove(String key);                                       // Line 170
Editor clear();                                                  // Line 184
boolean commit();                                                // Line 202 -- Synchronous, returns success
void apply();                                                    // Line 240 -- Async, no return value
```

### 7.4 Change Listener

```java
void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);   // Line 383
void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener); // Line 391
```

### 7.5 Critical Design Notes

1. **Listener GC risk** (line 373): The preference manager does NOT hold a strong reference to listeners. You must keep your own strong reference.
2. **commit() vs apply()** (lines 202-240): `apply()` writes to memory immediately but disk I/O is async. `commit()` is synchronous. If `apply()` is pending when `commit()` is called, `commit()` blocks until all pending writes complete.
3. **Android R behavior change** (line 61): `OnSharedPreferenceChangeListener` now fires on `clear()` with a `null` key.
4. **StringSet mutability warning** (line 275-276): Must NOT modify the Set returned by `getStringSet()`.

---

## 8. ClipboardManager and ClipData -- Clipboard Access

### 8.1 ClipboardManager

**File:** `frameworks/base/core/java/android/content/ClipboardManager.java`
**Lines:** 253
**Type:** Class extending `android.text.ClipboardManager`, annotated `@SystemService(Context.CLIPBOARD_SERVICE)`

#### Key Methods
```java
public void setPrimaryClip(@NonNull ClipData clip);              // Line 101
public void clearPrimaryClip();                                  // Line 116
public @Nullable ClipData getPrimaryClip();                      // Line 132
public @Nullable ClipDescription getPrimaryClipDescription();    // Line 149
public boolean hasPrimaryClip();                                 // Line 164
public void addPrimaryClipChangedListener(OnPrimaryClipChangedListener what);  // Line 172
public void removePrimaryClipChangedListener(OnPrimaryClipChangedListener what); // Line 187
```

**Android 10+ restriction** (lines 127-128, 144-145): `getPrimaryClip()` and `getPrimaryClipDescription()` return `null` if the app is not the default IME or does not have input focus.

### 8.2 ClipData

**File:** `frameworks/base/core/java/android/content/ClipData.java`
**Lines:** 1,178
**Type:** Class implementing `Parcelable`

#### Factory Methods (recommended for creating clips)
```java
static public ClipData newPlainText(CharSequence label, CharSequence text);      // Line 768
static public ClipData newHtmlText(CharSequence label, CharSequence text, String htmlText); // Line 783
static public ClipData newIntent(CharSequence label, Intent intent);             // Line 797
static public ClipData newUri(ContentResolver resolver, CharSequence label, Uri uri); // Line 813
static public ClipData newRawUri(CharSequence label, Uri uri);                   // Line 860
```

#### Item Class (line 198)
Each ClipData contains one or more `Item` objects holding:
- `CharSequence text` -- Plain text
- `String htmlText` -- HTML representation
- `Intent intent` -- An Intent
- `Uri uri` -- A content URI

Coercion methods on Item:
```java
public CharSequence coerceToText(Context context);               // Line 337
public CharSequence coerceToStyledText(Context context);         // Line 420
public String coerceToHtmlText(Context context);                 // Line 470
```

---

## 9. Database APIs

### 9.1 Cursor Interface

**File:** `frameworks/base/core/java/android/database/Cursor.java`
**Lines:** 518
**Type:** Interface extending `Closeable`

#### Field Type Constants (lines 44-57)
```java
static final int FIELD_TYPE_NULL = 0;
static final int FIELD_TYPE_INTEGER = 1;
static final int FIELD_TYPE_FLOAT = 2;
static final int FIELD_TYPE_STRING = 3;
static final int FIELD_TYPE_BLOB = 4;
```

#### Navigation
```java
int getCount();                          // Line 64
int getPosition();                       // Line 75
boolean move(int offset);               // Line 92
boolean moveToPosition(int position);   // Line 104
boolean moveToFirst();                   // Line 113
boolean moveToLast();                    // Line 122
boolean moveToNext();                    // Line 132
boolean moveToPrevious();               // Line 142
boolean isFirst(); / isLast(); / isBeforeFirst(); / isAfterLast();
```

#### Column Access
```java
int getColumnIndex(String columnName);                    // Line 184
int getColumnIndexOrThrow(String columnName);             // Line 197
String getColumnName(int columnIndex);                    // Line 205
String[] getColumnNames();                                // Line 213
int getColumnCount();                                     // Line 219
```

#### Data Retrieval
```java
byte[] getBlob(int columnIndex);         // Line 231
String getString(int columnIndex);       // Line 243
short getShort(int columnIndex);         // Line 266
int getInt(int columnIndex);             // Line 279
long getLong(int columnIndex);           // Line 292
float getFloat(int columnIndex);         // Line 305
double getDouble(int columnIndex);       // Line 318
int getType(int columnIndex);            // Line 339
boolean isNull(int columnIndex);         // Line 347
```

#### Lifecycle and Observation
```java
void close();                                                    // Line 380
boolean isClosed();                                              // Line 386
void registerContentObserver(ContentObserver observer);          // Line 395
void setNotificationUri(ContentResolver cr, Uri uri);            // Line 436
default void setNotificationUris(ContentResolver cr, List<Uri> uris); // Line 452 -- Default method, Android 10+
void setExtras(Bundle extras);                                   // Line 493
Bundle getExtras();                                              // Line 504
```

### 9.2 SQLiteDatabase

**File:** `frameworks/base/core/java/android/database/sqlite/SQLiteDatabase.java`
**Lines:** 2,878
**Type:** Final class extending `SQLiteClosable`

#### Conflict Resolution Constants (lines 161-205)
```java
public static final int CONFLICT_ROLLBACK = 1;
public static final int CONFLICT_ABORT = 2;     // Default
public static final int CONFLICT_FAIL = 3;
public static final int CONFLICT_IGNORE = 4;
public static final int CONFLICT_REPLACE = 5;
public static final int CONFLICT_NONE = 0;
```

#### Open Flags (lines 234-282)
```java
public static final int OPEN_READWRITE = 0x00000000;
public static final int OPEN_READONLY = 0x00000001;
public static final int CREATE_IF_NECESSARY = 0x10000000;
public static final int NO_LOCALIZED_COLLATORS = 0x00000010;
public static final int ENABLE_WRITE_AHEAD_LOGGING = 0x20000000;
```

#### Database Opening
```java
public static SQLiteDatabase openDatabase(String path, CursorFactory factory, int flags); // Line 734
public static SQLiteDatabase openDatabase(File path, OpenParams openParams);               // Line 749
public static SQLiteDatabase openOrCreateDatabase(String path, CursorFactory factory);     // Line 804
public static SQLiteDatabase create(@Nullable CursorFactory factory);                      // Line 940 -- In-memory
public static SQLiteDatabase createInMemory(OpenParams openParams);                        // Line 957
public static boolean deleteDatabase(File file);                                           // Line 824
```

#### Transaction Management
```java
public void beginTransaction();                              // Line 480 -- Exclusive
public void beginTransactionNonExclusive();                  // Line 504 -- DEFERRED
public void beginTransactionWithListener(SQLiteTransactionListener listener); // Line 533
public void endTransaction();                                // Line 585
public void setTransactionSuccessful();                      // Line 603
public boolean inTransaction();                              // Line 617
```

#### CRUD Operations
```java
public Cursor query(boolean distinct, String table, String[] columns,
        String selection, String[] selectionArgs, String groupBy,
        String having, String orderBy, String limit);                // Line 1260
public Cursor query(String table, String[] columns, String selection,
        String[] selectionArgs, String groupBy, String having, String orderBy); // Line 1427
public Cursor rawQuery(String sql, String[] selectionArgs);          // Line 1483

public long insert(String table, String nullColumnHack, ContentValues values);           // Line 1568
public long insertOrThrow(String table, String nullColumnHack, ContentValues values);    // Line 1594
public long insertWithOnConflict(String table, String nullColumnHack,
        ContentValues initialValues, int conflictAlgorithm);                             // Line 1667
public long replace(String table, String nullColumnHack, ContentValues initialValues);   // Line 1615

public int delete(String table, String whereClause, String[] whereArgs);                 // Line 1723
public int update(String table, ContentValues values, String whereClause, String[] whereArgs); // Line 1751
public int updateWithOnConflict(String table, ContentValues values,
        String whereClause, String[] whereArgs, int conflictAlgorithm);                  // Line 1769

public void execSQL(String sql);                                     // Line 1840
public void execSQL(String sql, Object[] bindArgs);                  // Line 1893
```

#### Custom Functions (Android 11 new)
```java
public void setCustomScalarFunction(String functionName, UnaryOperator<String> function); // Line 985
public void setCustomAggregateFunction(String functionName, BinaryOperator<String> function); // Line 1031
```

#### Configuration
```java
public int getVersion();                                     // Line 1092
public void setVersion(int version);                         // Line 1101
public long getMaximumSize();                                // Line 1110
public long setMaximumSize(long numBytes);                   // Line 1122
public void setLocale(Locale locale);                        // Line 2017
public boolean isReadOnly();                                 // Line 1953
public boolean isOpen();                                     // Line 1980
public String getPath();                                     // Line 2001
public boolean enableWriteAheadLogging();                    // WAL mode
public void disableWriteAheadLogging();
public boolean isWriteAheadLoggingEnabled();
```

### 9.3 SQLiteOpenHelper

**File:** `frameworks/base/core/java/android/database/sqlite/SQLiteOpenHelper.java`
**Lines:** 558
**Type:** Abstract class implementing `AutoCloseable`

#### Constructors
```java
public SQLiteOpenHelper(Context context, String name, CursorFactory factory, int version);  // Line 80
public SQLiteOpenHelper(Context context, String name, CursorFactory factory, int version,
        DatabaseErrorHandler errorHandler);                                                  // Line 102
public SQLiteOpenHelper(Context context, String name, int version, OpenParams openParams);  // Line 123
```

#### Lifecycle Methods (must/should override)
```java
public abstract void onCreate(SQLiteDatabase db);            // Line 499 -- MUST override
public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion); // Line 521 -- MUST override
public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion); // Line 539 -- Throws by default
public void onConfigure(SQLiteDatabase db);                  // Line 477 -- Before create/upgrade
public void onOpen(SQLiteDatabase db);                       // Line 557 -- After everything
public void onBeforeDelete(SQLiteDatabase db);               // Line 490 -- @hide, before obsolete DB deletion
```

#### Database Access
```java
public SQLiteDatabase getWritableDatabase();                 // Line 314
public SQLiteDatabase getReadableDatabase();                 // Line 338
public synchronized void close();                            // Line 451
public String getDatabaseName();                             // Line 177
```

#### Configuration
```java
public void setWriteAheadLoggingEnabled(boolean enabled);    // Line 192
public void setLookasideConfig(int slotSize, int slotCount); // Line 227
```

#### Key Design Details (getDatabaseLocked, lines 344-441)

The internal `getDatabaseLocked()` method implements the version management lifecycle:
1. Try to open existing database
2. Call `onConfigure(db)`
3. Check version:
   - Version 0 (new database): call `onCreate(db)`
   - Version < current: call `onUpgrade(db, oldVersion, newVersion)`
   - Version > current: call `onDowngrade(db, oldVersion, newVersion)` (throws by default)
4. Set version and commit transaction
5. Call `onOpen(db)`

All version change operations are wrapped in a transaction (line 408).

---

## 10. Provider Contracts

### 10.1 Settings.java

**File:** `frameworks/base/core/java/android/provider/Settings.java`
**Lines:** 15,217
**Type:** Final class

#### Structure

Three main inner classes representing distinct namespaces:

1. **`Settings.System`** (line 3042) -- User-facing system settings (ringtone, screen brightness, etc.)
2. **`Settings.Secure`** (line 5229) -- Secure settings requiring `WRITE_SECURE_SETTINGS` permission
3. **`Settings.Global`** (not shown in excerpt) -- Device-wide settings (airplane mode, data roaming, etc.)

Each inner class extends `NameValueTable` and provides:
```java
public static String getString(ContentResolver resolver, String name);
public static boolean putString(ContentResolver resolver, String name, String value);
public static int getInt(ContentResolver cr, String name, int def);
public static boolean putInt(ContentResolver cr, String name, int value);
public static long getLong(ContentResolver cr, String name, long def);
public static float getFloat(ContentResolver cr, String name, float def);
public static Uri getUriFor(String name);
```

#### @hide Statistics

- **1,065 @hide annotations** -- The vast majority of Settings constants are hidden
- **71 @SystemApi annotations** -- Many require system-level privileges

#### Intent Actions for Settings UI (lines 107-143)
```java
public static final String ACTION_SETTINGS = "android.settings.SETTINGS";
public static final String ACTION_APN_SETTINGS = "android.settings.APN_SETTINGS";
public static final String ACTION_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS";
// ... many more
```

### 10.2 ContactsContract.java

**File:** `frameworks/base/core/java/android/provider/ContactsContract.java`
**Lines:** 9,747
**Type:** Final class

#### Three-Tier Data Model (documented lines 68-119)

1. **Data** table -- Individual data items (phone, email, etc.). Open-ended set of data kinds.
2. **RawContacts** table -- Set of data describing a person from a single account
3. **Contacts** table -- Aggregated view of one or more RawContacts for the same person

#### Key Constants
```java
public static final String AUTHORITY = "com.android.contacts";                   // Line 124
public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);     // Line 126
public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";     // Line 147
```

#### Inner Classes (selected)
- `ContactsContract.Contacts` -- Aggregated contacts
- `ContactsContract.RawContacts` -- Per-account contacts
- `ContactsContract.Data` -- Individual data rows
- `ContactsContract.CommonDataKinds.Phone` -- Phone numbers
- `ContactsContract.CommonDataKinds.Email` -- Email addresses
- `ContactsContract.Groups` -- Contact groups
- `ContactsContract.PhoneLookup` -- Quick caller ID lookup
- `ContactsContract.QuickContact` -- Quick contact badge

#### Hidden API: Column Prefix
```java
public static final String HIDDEN_COLUMN_PREFIX = "x_";                          // Line 134 -- @hide @TestApi
```

### 10.3 CalendarContract.java

**File:** `frameworks/base/core/java/android/provider/CalendarContract.java`
**Lines:** 2,855
**Type:** Final class

#### Data Model (documented lines 51-97)

Tables:
- **Calendars** -- Calendar metadata (name, color, sync info)
- **Events** -- Event details (title, location, start/end time)
- **Instances** -- Computed occurrences of events (handles recurring events)
- **Attendees** -- Event guests
- **Reminders** -- Alert/notification settings
- **ExtendedProperties** -- Sync adapter opaque data

#### Key Constants
```java
public static final String ACTION_EVENT_REMINDER = "android.intent.action.EVENT_REMINDER"; // Line 108
public static final String ACTION_HANDLE_CUSTOM_EVENT =
        "android.provider.calendar.action.HANDLE_CUSTOM_EVENT";                  // Line 135
public static final String ACTION_VIEW_MANAGED_PROFILE_CALENDAR_EVENT =
        "android.provider.calendar.action.VIEW_MANAGED_PROFILE_CALENDAR_EVENT";  // Line 142
```

### 10.4 MediaStore

**Note:** `MediaStore.java` was not found in this AOSP checkout at the expected location (`frameworks/base/core/java/android/provider/MediaStore.java`). It may have been moved to a different module in the Android 11 source tree restructuring (likely `frameworks/base/core/java/android/provider/` was split or MediaStore was moved to a Mainline module).

---

## 11. ContentObserver -- Reactive Data Updates

**File:** `frameworks/base/core/java/android/database/ContentObserver.java`
**Lines:** (supporting class)

ContentObserver enables reactive programming patterns with content providers:

```java
// Registration (in ContentResolver)
contentResolver.registerContentObserver(uri, notifyForDescendants, observer);

// Notification (in ContentProvider)
getContext().getContentResolver().notifyChange(uri, null);

// Reception (in ContentObserver subclass)
public void onChange(boolean selfChange, Uri uri) { ... }
```

This is the **Observer** pattern applied to content URIs, enabling UI components (via CursorLoader or LiveData) to automatically refresh when underlying data changes.

---

## 12. Design Patterns Summary

| Pattern | Where Used | Description |
|---------|-----------|-------------|
| **Abstract Factory** | `Context` | Abstract class with `ContextImpl` providing concrete implementation |
| **Decorator** | `ContextWrapper` | Wraps Context, used by Activity/Service/Application |
| **Proxy** | `ContentProvider.Transport` | Cross-process IPC bridge for ContentProvider |
| **Observer** | `ContentObserver`, `ContentResolver.notifyChange()` | Reactive data change notifications |
| **Builder** | `ContentValues`, `Intent` | Fluent API for constructing data/messages |
| **Strategy** | `SQLiteDatabase` conflict resolution | Pluggable conflict handling (ROLLBACK, ABORT, etc.) |
| **Template Method** | `SQLiteOpenHelper` | onCreate/onUpgrade/onOpen lifecycle |
| **Adapter** | `CursorWrapper` | Wraps Cursor with modified behavior |
| **Singleton** | `SharedPreferences` | One instance per preferences file per process |
| **Command** | `ContentProviderOperation` | Encapsulated batch operations |

---

## 13. Security Annotations Summary

| Annotation | Typical Usage | Count (across reviewed files) |
|-----------|--------------|-------------------------------|
| `@RequiresPermission` | Methods needing runtime/manifest permissions | Widespread in Context, Intent |
| `@SystemApi` | System-app-only APIs | 198+ total across files |
| `@TestApi` | Testing-only APIs | Various |
| `@hide` | Internal APIs not in SDK | 1,663+ total across files |
| `@UnsupportedAppUsage` | Greylist APIs that apps use but shouldn't | Very common in internal fields |
| `@Deprecated` | Obsolete APIs | MODE_WORLD_*, MODE_MULTI_PROCESS, getText/setText |

### Key Permission Requirements

- `INTERACT_ACROSS_USERS` / `INTERACT_ACROSS_USERS_FULL` -- Cross-user operations in ContentProvider (line 19)
- `WRITE_SECURE_SETTINGS` -- Modifying Settings.Secure values
- `WRITE_SETTINGS` -- Modifying Settings.System values
- `READ_CONTACTS` / `WRITE_CONTACTS` -- ContactsContract operations
- `READ_CALENDAR` / `WRITE_CALENDAR` -- CalendarContract operations
- URI permissions (FLAG_GRANT_READ_URI_PERMISSION, FLAG_GRANT_WRITE_URI_PERMISSION) -- Temporary access grants

---

## 14. Key Files Reference

| File | Path | Lines |
|------|------|-------|
| Context.java | `frameworks/base/core/java/android/content/Context.java` | 6,122 |
| Intent.java | `frameworks/base/core/java/android/content/Intent.java` | 11,451 |
| ContentProvider.java | `frameworks/base/core/java/android/content/ContentProvider.java` | 2,625 |
| ContentResolver.java | `frameworks/base/core/java/android/content/ContentResolver.java` | 4,136 |
| IntentFilter.java | `frameworks/base/core/java/android/content/IntentFilter.java` | 2,475 |
| BroadcastReceiver.java | `frameworks/base/core/java/android/content/BroadcastReceiver.java` | 687 |
| SharedPreferences.java | `frameworks/base/core/java/android/content/SharedPreferences.java` | 392 |
| ClipboardManager.java | `frameworks/base/core/java/android/content/ClipboardManager.java` | 253 |
| ClipData.java | `frameworks/base/core/java/android/content/ClipData.java` | 1,178 |
| Settings.java | `frameworks/base/core/java/android/provider/Settings.java` | 15,217 |
| ContactsContract.java | `frameworks/base/core/java/android/provider/ContactsContract.java` | 9,747 |
| CalendarContract.java | `frameworks/base/core/java/android/provider/CalendarContract.java` | 2,855 |
| Cursor.java | `frameworks/base/core/java/android/database/Cursor.java` | 518 |
| SQLiteDatabase.java | `frameworks/base/core/java/android/database/sqlite/SQLiteDatabase.java` | 2,878 |
| SQLiteOpenHelper.java | `frameworks/base/core/java/android/database/sqlite/SQLiteOpenHelper.java` | 558 |
