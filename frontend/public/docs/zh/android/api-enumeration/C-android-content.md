# Android 11 (API 30) 公共 API 枚举：Android 内容

基于 `frameworks/base/api/current.txt` 生成

## 概要

| 包 | 类型数 | 方法数 | 字段数 | 构造函数数 |
|---------|------:|--------:|-------:|------:|
| `android.content` | 77 | 606 | 541 | 81 |
| `android.content.pm` | 42 | 270 | 516 | 50 |
| `android.content.res` | 14 | 106 | 99 | 10 |
| `android.content.res.loader` | 3 | 5 | 0 | 1 |
| `android.database` | 26 | 250 | 19 | 27 |
| `android.database.sqlite` | 30 | 136 | 13 | 43 |
| **总计** | **192** | **1373** | **1188** | **212** |

---

## 包： `android.content`

### `class abstract AbstractThreadedSyncAdapter`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AbstractThreadedSyncAdapter(android.content.Context, boolean)` |  |
| `AbstractThreadedSyncAdapter(android.content.Context, boolean, boolean)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.Context getContext()` |  |
| `final android.os.IBinder getSyncAdapterBinder()` |  |
| `abstract void onPerformSync(android.accounts.Account, android.os.Bundle, String, android.content.ContentProviderClient, android.content.SyncResult)` |  |
| `void onSecurityException(android.accounts.Account, android.os.Bundle, String, android.content.SyncResult)` |  |
| `void onSyncCanceled()` |  |
| `void onSyncCanceled(Thread)` |  |

---

### `class ActivityNotFoundException`

- **继承：** `java.lang.RuntimeException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityNotFoundException()` |  |
| `ActivityNotFoundException(String)` |  |

---

### `class abstract AsyncQueryHandler`

- **继承：** `android.os.Handler`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AsyncQueryHandler(android.content.ContentResolver)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final void cancelOperation(int)` |  |
| `android.os.Handler createHandler(android.os.Looper)` |  |
| `void onDeleteComplete(int, Object, int)` |  |
| `void onInsertComplete(int, Object, android.net.Uri)` |  |
| `void onQueryComplete(int, Object, android.database.Cursor)` |  |
| `void onUpdateComplete(int, Object, int)` |  |
| `final void startDelete(int, Object, android.net.Uri, String, String[])` |  |
| `final void startInsert(int, Object, android.net.Uri, android.content.ContentValues)` |  |
| `void startQuery(int, Object, android.net.Uri, String[], String, String[], String)` |  |
| `final void startUpdate(int, Object, android.net.Uri, android.content.ContentValues, String, String[])` |  |

---

### `class static final AsyncQueryHandler.WorkerArgs`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AsyncQueryHandler.WorkerArgs()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `Object cookie` |  |
| `android.os.Handler handler` |  |
| `String orderBy` |  |
| `String[] projection` |  |
| `Object result` |  |
| `String selection` |  |
| `String[] selectionArgs` |  |
| `android.net.Uri uri` |  |
| `android.content.ContentValues values` |  |

---

### `class AsyncQueryHandler.WorkerHandler`

- **继承：** `android.os.Handler`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AsyncQueryHandler.WorkerHandler(android.os.Looper)` |  |

---

### `class abstract AsyncTaskLoader<D>` ~~DEPRECATED~~

- **继承：** `android.content.Loader<D>`
- **Annotations:** `@Deprecated`

---

### `class abstract BroadcastReceiver`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `BroadcastReceiver()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final void abortBroadcast()` |  |
| `final void clearAbortBroadcast()` |  |
| `final boolean getAbortBroadcast()` |  |
| `final boolean getDebugUnregister()` |  |
| `final int getResultCode()` |  |
| `final String getResultData()` |  |
| `final android.os.Bundle getResultExtras(boolean)` |  |
| `final android.content.BroadcastReceiver.PendingResult goAsync()` |  |
| `final boolean isInitialStickyBroadcast()` |  |
| `final boolean isOrderedBroadcast()` |  |
| `abstract void onReceive(android.content.Context, android.content.Intent)` |  |
| `android.os.IBinder peekService(android.content.Context, android.content.Intent)` |  |
| `final void setDebugUnregister(boolean)` |  |
| `final void setOrderedHint(boolean)` |  |
| `final void setResult(int, String, android.os.Bundle)` |  |
| `final void setResultCode(int)` |  |
| `final void setResultData(String)` |  |
| `final void setResultExtras(android.os.Bundle)` |  |

---

### `class static BroadcastReceiver.PendingResult`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final void abortBroadcast()` |  |
| `final void clearAbortBroadcast()` |  |
| `final void finish()` |  |
| `final boolean getAbortBroadcast()` |  |
| `final int getResultCode()` |  |
| `final String getResultData()` |  |
| `final android.os.Bundle getResultExtras(boolean)` |  |
| `final void setResult(int, String, android.os.Bundle)` |  |
| `final void setResultCode(int)` |  |
| `final void setResultData(String)` |  |
| `final void setResultExtras(android.os.Bundle)` |  |

---

### `class ClipData`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ClipData(CharSequence, String[], android.content.ClipData.Item)` |  |
| `ClipData(android.content.ClipDescription, android.content.ClipData.Item)` |  |
| `ClipData(android.content.ClipData)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addItem(android.content.ClipData.Item)` |  |
| `void addItem(android.content.ContentResolver, android.content.ClipData.Item)` |  |
| `int describeContents()` |  |
| `android.content.ClipDescription getDescription()` |  |
| `android.content.ClipData.Item getItemAt(int)` |  |
| `int getItemCount()` |  |
| `static android.content.ClipData newHtmlText(CharSequence, CharSequence, String)` |  |
| `static android.content.ClipData newIntent(CharSequence, android.content.Intent)` |  |
| `static android.content.ClipData newPlainText(CharSequence, CharSequence)` |  |
| `static android.content.ClipData newRawUri(CharSequence, android.net.Uri)` |  |
| `static android.content.ClipData newUri(android.content.ContentResolver, CharSequence, android.net.Uri)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ClipData.Item`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ClipData.Item(CharSequence)` |  |
| `ClipData.Item(CharSequence, String)` |  |
| `ClipData.Item(android.content.Intent)` |  |
| `ClipData.Item(android.net.Uri)` |  |
| `ClipData.Item(CharSequence, android.content.Intent, android.net.Uri)` |  |
| `ClipData.Item(CharSequence, String, android.content.Intent, android.net.Uri)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `String coerceToHtmlText(android.content.Context)` |  |
| `CharSequence coerceToStyledText(android.content.Context)` |  |
| `CharSequence coerceToText(android.content.Context)` |  |
| `String getHtmlText()` |  |
| `android.content.Intent getIntent()` |  |
| `CharSequence getText()` |  |
| `android.net.Uri getUri()` |  |

---

### `class ClipDescription`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ClipDescription(CharSequence, String[])` |  |
| `ClipDescription(android.content.ClipDescription)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String MIMETYPE_TEXT_HTML = "text/html"` |  |
| `static final String MIMETYPE_TEXT_INTENT = "text/vnd.android.intent"` |  |
| `static final String MIMETYPE_TEXT_PLAIN = "text/plain"` |  |
| `static final String MIMETYPE_TEXT_URILIST = "text/uri-list"` |  |
| `static final String MIMETYPE_UNKNOWN = "application/octet-stream"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static boolean compareMimeTypes(String, String)` |  |
| `int describeContents()` |  |
| `String[] filterMimeTypes(String)` |  |
| `android.os.PersistableBundle getExtras()` |  |
| `CharSequence getLabel()` |  |
| `String getMimeType(int)` |  |
| `int getMimeTypeCount()` |  |
| `long getTimestamp()` |  |
| `boolean hasMimeType(String)` |  |
| `void setExtras(android.os.PersistableBundle)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ClipboardManager`

- **继承：** `android.text.ClipboardManager`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addPrimaryClipChangedListener(android.content.ClipboardManager.OnPrimaryClipChangedListener)` |  |
| `void clearPrimaryClip()` |  |
| `boolean hasPrimaryClip()` |  |
| `void removePrimaryClipChangedListener(android.content.ClipboardManager.OnPrimaryClipChangedListener)` |  |
| `void setPrimaryClip(@NonNull android.content.ClipData)` |  |

---

### `interface static ClipboardManager.OnPrimaryClipChangedListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onPrimaryClipChanged()` |  |

---

### `interface ComponentCallbacks`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onConfigurationChanged(@NonNull android.content.res.Configuration)` |  |
| `void onLowMemory()` |  |

---

### `interface ComponentCallbacks2`

- **继承：** `android.content.ComponentCallbacks`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int TRIM_MEMORY_BACKGROUND = 40` |  |
| `static final int TRIM_MEMORY_COMPLETE = 80` |  |
| `static final int TRIM_MEMORY_MODERATE = 60` |  |
| `static final int TRIM_MEMORY_RUNNING_CRITICAL = 15` |  |
| `static final int TRIM_MEMORY_RUNNING_LOW = 10` |  |
| `static final int TRIM_MEMORY_RUNNING_MODERATE = 5` |  |
| `static final int TRIM_MEMORY_UI_HIDDEN = 20` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onTrimMemory(int)` |  |

---

### `class final ComponentName`

- **实现：** `java.lang.Cloneable java.lang.Comparable<android.content.ComponentName> android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ComponentName(@NonNull String, @NonNull String)` |  |
| `ComponentName(@NonNull android.content.Context, @NonNull String)` |  |
| `ComponentName(@NonNull android.content.Context, @NonNull Class<?>)` |  |
| `ComponentName(android.os.Parcel)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.ComponentName clone()` |  |
| `int compareTo(android.content.ComponentName)` |  |
| `int describeContents()` |  |
| `String getShortClassName()` |  |
| `static android.content.ComponentName readFromParcel(android.os.Parcel)` |  |
| `String toShortString()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |
| `static void writeToParcel(android.content.ComponentName, android.os.Parcel)` |  |

---

### `class abstract ContentProvider`

- **实现：** `android.content.ComponentCallbacks2`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ContentProvider()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void attachInfo(android.content.Context, android.content.pm.ProviderInfo)` |  |
| `int bulkInsert(@NonNull android.net.Uri, @NonNull android.content.ContentValues[])` |  |
| `abstract int delete(@NonNull android.net.Uri, @Nullable String, @Nullable String[])` |  |
| `int delete(@NonNull android.net.Uri, @Nullable android.os.Bundle)` |  |
| `void dump(java.io.FileDescriptor, java.io.PrintWriter, String[])` |  |
| `boolean isTemporary()` |  |
| `void onCallingPackageChanged()` |  |
| `void onConfigurationChanged(android.content.res.Configuration)` |  |
| `abstract boolean onCreate()` |  |
| `void onLowMemory()` |  |
| `void onTrimMemory(int)` |  |
| `boolean refresh(android.net.Uri, @Nullable android.os.Bundle, @Nullable android.os.CancellationSignal)` |  |
| `final void restoreCallingIdentity(@NonNull android.content.ContentProvider.CallingIdentity)` |  |
| `final void setPathPermissions(@Nullable android.content.pm.PathPermission[])` |  |
| `final void setReadPermission(@Nullable String)` |  |
| `final void setWritePermission(@Nullable String)` |  |
| `void shutdown()` |  |
| `abstract int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable String, @Nullable String[])` |  |
| `int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable android.os.Bundle)` |  |

---

### `class final ContentProvider.CallingIdentity`


---

### `interface static ContentProvider.PipeDataWriter<T>`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void writeDataToPipe(@NonNull android.os.ParcelFileDescriptor, @NonNull android.net.Uri, @NonNull String, @Nullable android.os.Bundle, @Nullable T)` |  |

---

### `class ContentProviderClient`

- **实现：** `java.lang.AutoCloseable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int bulkInsert(@NonNull android.net.Uri, @NonNull android.content.ContentValues[]) throws android.os.RemoteException` |  |
| `void close()` |  |
| `int delete(@NonNull android.net.Uri, @Nullable String, @Nullable String[]) throws android.os.RemoteException` |  |
| `int delete(@NonNull android.net.Uri, @Nullable android.os.Bundle) throws android.os.RemoteException` |  |
| `boolean refresh(android.net.Uri, @Nullable android.os.Bundle, @Nullable android.os.CancellationSignal) throws android.os.RemoteException` |  |
| `int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable String, @Nullable String[]) throws android.os.RemoteException` |  |
| `int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable android.os.Bundle) throws android.os.RemoteException` |  |

---

### `class ContentProviderOperation`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isAssertQuery()` |  |
| `boolean isCall()` |  |
| `boolean isDelete()` |  |
| `boolean isExceptionAllowed()` |  |
| `boolean isInsert()` |  |
| `boolean isReadOperation()` |  |
| `boolean isUpdate()` |  |
| `boolean isWriteOperation()` |  |
| `boolean isYieldAllowed()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ContentProviderOperation.Builder`


---

### `class ContentProviderResult`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ContentProviderResult(@NonNull android.net.Uri)` |  |
| `ContentProviderResult(int)` |  |
| `ContentProviderResult(@NonNull android.os.Bundle)` |  |
| `ContentProviderResult(@NonNull Throwable)` |  |
| `ContentProviderResult(android.os.Parcel)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ContentQueryMap`

- **继承：** `java.util.Observable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ContentQueryMap(android.database.Cursor, String, boolean, android.os.Handler)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close()` |  |
| `java.util.Map<java.lang.String,android.content.ContentValues> getRows()` |  |
| `android.content.ContentValues getValues(String)` |  |
| `void requery()` |  |
| `void setKeepUpdated(boolean)` |  |

---

### `class abstract ContentResolver`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ContentResolver(@Nullable android.content.Context)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ANY_CURSOR_ITEM_TYPE = "vnd.android.cursor.item/*"` |  |
| `static final String CURSOR_DIR_BASE_TYPE = "vnd.android.cursor.dir"` |  |
| `static final String CURSOR_ITEM_BASE_TYPE = "vnd.android.cursor.item"` |  |
| `static final String EXTRA_HONORED_ARGS = "android.content.extra.HONORED_ARGS"` |  |
| `static final String EXTRA_REFRESH_SUPPORTED = "android.content.extra.REFRESH_SUPPORTED"` |  |
| `static final String EXTRA_SIZE = "android.content.extra.SIZE"` |  |
| `static final String EXTRA_TOTAL_COUNT = "android.content.extra.TOTAL_COUNT"` |  |
| `static final int NOTIFY_DELETE = 16` |  |
| `static final int NOTIFY_INSERT = 4` |  |
| `static final int NOTIFY_SKIP_NOTIFY_FOR_DESCENDANTS = 2` |  |
| `static final int NOTIFY_SYNC_TO_NETWORK = 1` |  |
| `static final int NOTIFY_UPDATE = 8` |  |
| `static final String QUERY_ARG_GROUP_COLUMNS = "android:query-arg-group-columns"` |  |
| `static final String QUERY_ARG_LIMIT = "android:query-arg-limit"` |  |
| `static final String QUERY_ARG_OFFSET = "android:query-arg-offset"` |  |
| `static final String QUERY_ARG_SORT_COLLATION = "android:query-arg-sort-collation"` |  |
| `static final String QUERY_ARG_SORT_COLUMNS = "android:query-arg-sort-columns"` |  |
| `static final String QUERY_ARG_SORT_DIRECTION = "android:query-arg-sort-direction"` |  |
| `static final String QUERY_ARG_SORT_LOCALE = "android:query-arg-sort-locale"` |  |
| `static final String QUERY_ARG_SQL_GROUP_BY = "android:query-arg-sql-group-by"` |  |
| `static final String QUERY_ARG_SQL_HAVING = "android:query-arg-sql-having"` |  |
| `static final String QUERY_ARG_SQL_LIMIT = "android:query-arg-sql-limit"` |  |
| `static final String QUERY_ARG_SQL_SELECTION = "android:query-arg-sql-selection"` |  |
| `static final String QUERY_ARG_SQL_SELECTION_ARGS = "android:query-arg-sql-selection-args"` |  |
| `static final String QUERY_ARG_SQL_SORT_ORDER = "android:query-arg-sql-sort-order"` |  |
| `static final int QUERY_SORT_DIRECTION_ASCENDING = 0` |  |
| `static final int QUERY_SORT_DIRECTION_DESCENDING = 1` |  |
| `static final String SCHEME_ANDROID_RESOURCE = "android.resource"` |  |
| `static final String SCHEME_CONTENT = "content"` |  |
| `static final String SCHEME_FILE = "file"` |  |
| `static final String SYNC_EXTRAS_DISCARD_LOCAL_DELETIONS = "discard_deletions"` |  |
| `static final String SYNC_EXTRAS_DO_NOT_RETRY = "do_not_retry"` |  |
| `static final String SYNC_EXTRAS_EXPEDITED = "expedited"` |  |
| `static final String SYNC_EXTRAS_IGNORE_BACKOFF = "ignore_backoff"` |  |
| `static final String SYNC_EXTRAS_IGNORE_SETTINGS = "ignore_settings"` |  |
| `static final String SYNC_EXTRAS_INITIALIZE = "initialize"` |  |
| `static final String SYNC_EXTRAS_MANUAL = "force"` |  |
| `static final String SYNC_EXTRAS_OVERRIDE_TOO_MANY_DELETIONS = "deletions_override"` |  |
| `static final String SYNC_EXTRAS_REQUIRE_CHARGING = "require_charging"` |  |
| `static final String SYNC_EXTRAS_UPLOAD = "upload"` |  |
| `static final int SYNC_OBSERVER_TYPE_ACTIVE = 4` |  |
| `static final int SYNC_OBSERVER_TYPE_PENDING = 2` |  |
| `static final int SYNC_OBSERVER_TYPE_SETTINGS = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static void addPeriodicSync(android.accounts.Account, String, android.os.Bundle, long)` |  |
| `static Object addStatusChangeListener(int, android.content.SyncStatusObserver)` |  |
| `final int bulkInsert(@NonNull @RequiresPermission.Write android.net.Uri, @NonNull android.content.ContentValues[])` |  |
| `static void cancelSync(android.accounts.Account, String)` |  |
| `static void cancelSync(android.content.SyncRequest)` |  |
| `final int delete(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable String, @Nullable String[])` |  |
| `final int delete(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable android.os.Bundle)` |  |
| `static java.util.List<android.content.SyncInfo> getCurrentSyncs()` |  |
| `static int getIsSyncable(android.accounts.Account, String)` |  |
| `static boolean getMasterSyncAutomatically()` |  |
| `static java.util.List<android.content.PeriodicSync> getPeriodicSyncs(android.accounts.Account, String)` |  |
| `static android.content.SyncAdapterType[] getSyncAdapterTypes()` |  |
| `static boolean getSyncAutomatically(android.accounts.Account, String)` |  |
| `static boolean isSyncActive(android.accounts.Account, String)` |  |
| `static boolean isSyncPending(android.accounts.Account, String)` |  |
| `void notifyChange(@NonNull android.net.Uri, @Nullable android.database.ContentObserver)` |  |
| `void notifyChange(@NonNull android.net.Uri, @Nullable android.database.ContentObserver, int)` |  |
| `void notifyChange(@NonNull java.util.Collection<android.net.Uri>, @Nullable android.database.ContentObserver, int)` |  |
| `final boolean refresh(@NonNull android.net.Uri, @Nullable android.os.Bundle, @Nullable android.os.CancellationSignal)` |  |
| `final void registerContentObserver(@NonNull android.net.Uri, boolean, @NonNull android.database.ContentObserver)` |  |
| `void releasePersistableUriPermission(@NonNull android.net.Uri, int)` |  |
| `static void removePeriodicSync(android.accounts.Account, String, android.os.Bundle)` |  |
| `static void removeStatusChangeListener(Object)` |  |
| `static void requestSync(android.accounts.Account, String, android.os.Bundle)` |  |
| `static void requestSync(android.content.SyncRequest)` |  |
| `static void setIsSyncable(android.accounts.Account, String, int)` |  |
| `static void setMasterSyncAutomatically(boolean)` |  |
| `static void setSyncAutomatically(android.accounts.Account, String, boolean)` |  |
| `void takePersistableUriPermission(@NonNull android.net.Uri, int)` |  |
| `final void unregisterContentObserver(@NonNull android.database.ContentObserver)` |  |
| `final int update(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable android.content.ContentValues, @Nullable String, @Nullable String[])` |  |
| `final int update(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable android.content.ContentValues, @Nullable android.os.Bundle)` |  |
| `static void validateSyncExtrasBundle(android.os.Bundle)` |  |

---

### `class static final ContentResolver.MimeTypeInfo`


---

### `class ContentUris`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ContentUris()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static long parseId(@NonNull android.net.Uri)` |  |

---

### `class final ContentValues`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ContentValues()` |  |
| `ContentValues(int)` |  |
| `ContentValues(android.content.ContentValues)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String TAG = "ContentValues"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void clear()` |  |
| `boolean containsKey(String)` |  |
| `int describeContents()` |  |
| `Object get(String)` |  |
| `Boolean getAsBoolean(String)` |  |
| `Byte getAsByte(String)` |  |
| `byte[] getAsByteArray(String)` |  |
| `Double getAsDouble(String)` |  |
| `Float getAsFloat(String)` |  |
| `Integer getAsInteger(String)` |  |
| `Long getAsLong(String)` |  |
| `Short getAsShort(String)` |  |
| `String getAsString(String)` |  |
| `boolean isEmpty()` |  |
| `java.util.Set<java.lang.String> keySet()` |  |
| `void put(String, String)` |  |
| `void put(String, Byte)` |  |
| `void put(String, Short)` |  |
| `void put(String, Integer)` |  |
| `void put(String, Long)` |  |
| `void put(String, Float)` |  |
| `void put(String, Double)` |  |
| `void put(String, Boolean)` |  |
| `void put(String, byte[])` |  |
| `void putAll(android.content.ContentValues)` |  |
| `void putNull(String)` |  |
| `void remove(String)` |  |
| `int size()` |  |
| `java.util.Set<java.util.Map.Entry<java.lang.String,java.lang.Object>> valueSet()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class abstract Context`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Context()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACCESSIBILITY_SERVICE = "accessibility"` |  |
| `static final String ACCOUNT_SERVICE = "account"` |  |
| `static final String ACTIVITY_SERVICE = "activity"` |  |
| `static final String ALARM_SERVICE = "alarm"` |  |
| `static final String APPWIDGET_SERVICE = "appwidget"` |  |
| `static final String APP_OPS_SERVICE = "appops"` |  |
| `static final String AUDIO_SERVICE = "audio"` |  |
| `static final String BATTERY_SERVICE = "batterymanager"` |  |
| `static final int BIND_ABOVE_CLIENT = 8` |  |
| `static final int BIND_ADJUST_WITH_ACTIVITY = 128` |  |
| `static final int BIND_ALLOW_OOM_MANAGEMENT = 16` |  |
| `static final int BIND_AUTO_CREATE = 1` |  |
| `static final int BIND_DEBUG_UNBIND = 2` |  |
| `static final int BIND_EXTERNAL_SERVICE = -2147483648` |  |
| `static final int BIND_IMPORTANT = 64` |  |
| `static final int BIND_INCLUDE_CAPABILITIES = 4096` |  |
| `static final int BIND_NOT_FOREGROUND = 4` |  |
| `static final int BIND_NOT_PERCEPTIBLE = 256` |  |
| `static final int BIND_WAIVE_PRIORITY = 32` |  |
| `static final String BIOMETRIC_SERVICE = "biometric"` |  |
| `static final String BLOB_STORE_SERVICE = "blob_store"` |  |
| `static final String BLUETOOTH_SERVICE = "bluetooth"` |  |
| `static final String CAMERA_SERVICE = "camera"` |  |
| `static final String CAPTIONING_SERVICE = "captioning"` |  |
| `static final String CARRIER_CONFIG_SERVICE = "carrier_config"` |  |
| `static final String CLIPBOARD_SERVICE = "clipboard"` |  |
| `static final String COMPANION_DEVICE_SERVICE = "companiondevice"` |  |
| `static final String CONNECTIVITY_DIAGNOSTICS_SERVICE = "connectivity_diagnostics"` |  |
| `static final String CONNECTIVITY_SERVICE = "connectivity"` |  |
| `static final String CONSUMER_IR_SERVICE = "consumer_ir"` |  |
| `static final int CONTEXT_IGNORE_SECURITY = 2` |  |
| `static final int CONTEXT_INCLUDE_CODE = 1` |  |
| `static final int CONTEXT_RESTRICTED = 4` |  |
| `static final String CROSS_PROFILE_APPS_SERVICE = "crossprofileapps"` |  |
| `static final String DEVICE_POLICY_SERVICE = "device_policy"` |  |
| `static final String DISPLAY_SERVICE = "display"` |  |
| `static final String DOWNLOAD_SERVICE = "download"` |  |
| `static final String DROPBOX_SERVICE = "dropbox"` |  |
| `static final String EUICC_SERVICE = "euicc"` |  |
| `static final String FILE_INTEGRITY_SERVICE = "file_integrity"` |  |
| `static final String FINGERPRINT_SERVICE = "fingerprint"` |  |
| `static final String HARDWARE_PROPERTIES_SERVICE = "hardware_properties"` |  |
| `static final String INPUT_METHOD_SERVICE = "input_method"` |  |
| `static final String INPUT_SERVICE = "input"` |  |
| `static final String IPSEC_SERVICE = "ipsec"` |  |
| `static final String JOB_SCHEDULER_SERVICE = "jobscheduler"` |  |
| `static final String KEYGUARD_SERVICE = "keyguard"` |  |
| `static final String LAUNCHER_APPS_SERVICE = "launcherapps"` |  |
| `static final String LAYOUT_INFLATER_SERVICE = "layout_inflater"` |  |
| `static final String LOCATION_SERVICE = "location"` |  |
| `static final String MEDIA_PROJECTION_SERVICE = "media_projection"` |  |
| `static final String MEDIA_ROUTER_SERVICE = "media_router"` |  |
| `static final String MEDIA_SESSION_SERVICE = "media_session"` |  |
| `static final String MIDI_SERVICE = "midi"` |  |
| `static final int MODE_APPEND = 32768` |  |
| `static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 8` |  |
| `static final int MODE_NO_LOCALIZED_COLLATORS = 16` |  |
| `static final int MODE_PRIVATE = 0` |  |
| `static final String NETWORK_STATS_SERVICE = "netstats"` |  |
| `static final String NFC_SERVICE = "nfc"` |  |
| `static final String NOTIFICATION_SERVICE = "notification"` |  |
| `static final String NSD_SERVICE = "servicediscovery"` |  |
| `static final String POWER_SERVICE = "power"` |  |
| `static final String PRINT_SERVICE = "print"` |  |
| `static final int RECEIVER_VISIBLE_TO_INSTANT_APPS = 1` |  |
| `static final String RESTRICTIONS_SERVICE = "restrictions"` |  |
| `static final String ROLE_SERVICE = "role"` |  |
| `static final String SEARCH_SERVICE = "search"` |  |
| `static final String SENSOR_SERVICE = "sensor"` |  |
| `static final String SHORTCUT_SERVICE = "shortcut"` |  |
| `static final String STORAGE_SERVICE = "storage"` |  |
| `static final String STORAGE_STATS_SERVICE = "storagestats"` |  |
| `static final String SYSTEM_HEALTH_SERVICE = "systemhealth"` |  |
| `static final String TELECOM_SERVICE = "telecom"` |  |
| `static final String TELEPHONY_IMS_SERVICE = "telephony_ims"` |  |
| `static final String TELEPHONY_SERVICE = "phone"` |  |
| `static final String TELEPHONY_SUBSCRIPTION_SERVICE = "telephony_subscription_service"` |  |
| `static final String TEXT_CLASSIFICATION_SERVICE = "textclassification"` |  |
| `static final String TEXT_SERVICES_MANAGER_SERVICE = "textservices"` |  |
| `static final String TV_INPUT_SERVICE = "tv_input"` |  |
| `static final String UI_MODE_SERVICE = "uimode"` |  |
| `static final String USAGE_STATS_SERVICE = "usagestats"` |  |
| `static final String USB_SERVICE = "usb"` |  |
| `static final String USER_SERVICE = "user"` |  |
| `static final String VIBRATOR_SERVICE = "vibrator"` |  |
| `static final String VPN_MANAGEMENT_SERVICE = "vpn_management"` |  |
| `static final String WALLPAPER_SERVICE = "wallpaper"` |  |
| `static final String WIFI_AWARE_SERVICE = "wifiaware"` |  |
| `static final String WIFI_P2P_SERVICE = "wifip2p"` |  |
| `static final String WIFI_RTT_RANGING_SERVICE = "wifirtt"` |  |
| `static final String WIFI_SERVICE = "wifi"` |  |
| `static final String WINDOW_SERVICE = "window"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean bindIsolatedService(@NonNull @RequiresPermission android.content.Intent, int, @NonNull String, @NonNull java.util.concurrent.Executor, @NonNull android.content.ServiceConnection)` |  |
| `abstract boolean bindService(@RequiresPermission android.content.Intent, @NonNull android.content.ServiceConnection, int)` |  |
| `boolean bindService(@NonNull @RequiresPermission android.content.Intent, int, @NonNull java.util.concurrent.Executor, @NonNull android.content.ServiceConnection)` |  |
| `abstract int checkSelfPermission(@NonNull String)` |  |
| `abstract android.content.Context createConfigurationContext(@NonNull android.content.res.Configuration)` |  |
| `abstract android.content.Context createContextForSplit(String) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `abstract android.content.Context createDeviceProtectedStorageContext()` |  |
| `abstract android.content.Context createDisplayContext(@NonNull android.view.Display)` |  |
| `abstract android.content.Context createPackageContext(String, int) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `abstract String[] databaseList()` |  |
| `abstract boolean deleteDatabase(String)` |  |
| `abstract boolean deleteFile(String)` |  |
| `abstract boolean deleteSharedPreferences(String)` |  |
| `abstract void enforceCallingOrSelfPermission(@NonNull String, @Nullable String)` |  |
| `abstract void enforceCallingOrSelfUriPermission(android.net.Uri, int, String)` |  |
| `abstract void enforceCallingPermission(@NonNull String, @Nullable String)` |  |
| `abstract void enforceCallingUriPermission(android.net.Uri, int, String)` |  |
| `abstract void enforcePermission(@NonNull String, int, int, @Nullable String)` |  |
| `abstract void enforceUriPermission(android.net.Uri, int, int, int, String)` |  |
| `abstract void enforceUriPermission(@Nullable android.net.Uri, @Nullable String, @Nullable String, int, int, int, @Nullable String)` |  |
| `abstract String[] fileList()` |  |
| `abstract android.content.Context getApplicationContext()` |  |
| `abstract android.content.pm.ApplicationInfo getApplicationInfo()` |  |
| `abstract android.content.res.AssetManager getAssets()` |  |
| `abstract java.io.File getCacheDir()` |  |
| `abstract ClassLoader getClassLoader()` |  |
| `abstract java.io.File getCodeCacheDir()` |  |
| `abstract android.content.ContentResolver getContentResolver()` |  |
| `abstract java.io.File getDataDir()` |  |
| `abstract java.io.File getDatabasePath(String)` |  |
| `abstract java.io.File getDir(String, int)` |  |
| `abstract java.io.File[] getExternalCacheDirs()` |  |
| `abstract java.io.File[] getExternalFilesDirs(String)` |  |
| `abstract java.io.File getFileStreamPath(String)` |  |
| `abstract java.io.File getFilesDir()` |  |
| `java.util.concurrent.Executor getMainExecutor()` |  |
| `abstract android.os.Looper getMainLooper()` |  |
| `abstract java.io.File getNoBackupFilesDir()` |  |
| `abstract java.io.File getObbDir()` |  |
| `abstract java.io.File[] getObbDirs()` |  |
| `abstract String getPackageCodePath()` |  |
| `abstract android.content.pm.PackageManager getPackageManager()` |  |
| `abstract String getPackageName()` |  |
| `abstract String getPackageResourcePath()` |  |
| `abstract android.content.res.Resources getResources()` |  |
| `abstract android.content.SharedPreferences getSharedPreferences(String, int)` |  |
| `abstract Object getSystemService(@NonNull String)` |  |
| `final <T> T getSystemService(@NonNull Class<T>)` |  |
| `abstract void grantUriPermission(String, android.net.Uri, int)` |  |
| `abstract boolean isDeviceProtectedStorage()` |  |
| `boolean isRestricted()` |  |
| `abstract boolean moveDatabaseFrom(android.content.Context, String)` |  |
| `abstract boolean moveSharedPreferencesFrom(android.content.Context, String)` |  |
| `abstract java.io.FileInputStream openFileInput(String) throws java.io.FileNotFoundException` |  |
| `abstract java.io.FileOutputStream openFileOutput(String, int) throws java.io.FileNotFoundException` |  |
| `abstract android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String, int, android.database.sqlite.SQLiteDatabase.CursorFactory)` |  |
| `abstract android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String, int, android.database.sqlite.SQLiteDatabase.CursorFactory, @Nullable android.database.DatabaseErrorHandler)` |  |
| `void registerComponentCallbacks(android.content.ComponentCallbacks)` |  |
| `abstract void revokeUriPermission(android.net.Uri, int)` |  |
| `abstract void revokeUriPermission(String, android.net.Uri, int)` |  |
| `abstract void sendBroadcast(@RequiresPermission android.content.Intent)` |  |
| `abstract void sendBroadcast(@RequiresPermission android.content.Intent, @Nullable String)` |  |
| `void sendBroadcastWithMultiplePermissions(@NonNull android.content.Intent, @NonNull String[])` |  |
| `abstract void sendOrderedBroadcast(@RequiresPermission android.content.Intent, @Nullable String)` |  |
| `abstract void sendOrderedBroadcast(@NonNull @RequiresPermission android.content.Intent, @Nullable String, @Nullable android.content.BroadcastReceiver, @Nullable android.os.Handler, int, @Nullable String, @Nullable android.os.Bundle)` |  |
| `void sendOrderedBroadcast(@NonNull android.content.Intent, @Nullable String, @Nullable String, @Nullable android.content.BroadcastReceiver, @Nullable android.os.Handler, int, @Nullable String, @Nullable android.os.Bundle)` |  |
| `abstract void setTheme(@StyleRes int)` |  |
| `abstract void startActivities(@RequiresPermission android.content.Intent[])` |  |
| `abstract void startActivities(@RequiresPermission android.content.Intent[], android.os.Bundle)` |  |
| `abstract void startActivity(@RequiresPermission android.content.Intent)` |  |
| `abstract void startActivity(@RequiresPermission android.content.Intent, @Nullable android.os.Bundle)` |  |
| `abstract boolean startInstrumentation(@NonNull android.content.ComponentName, @Nullable String, @Nullable android.os.Bundle)` |  |
| `abstract void startIntentSender(android.content.IntentSender, @Nullable android.content.Intent, int, int, int) throws android.content.IntentSender.SendIntentException` |  |
| `abstract void startIntentSender(android.content.IntentSender, @Nullable android.content.Intent, int, int, int, @Nullable android.os.Bundle) throws android.content.IntentSender.SendIntentException` |  |
| `abstract boolean stopService(android.content.Intent)` |  |
| `abstract void unbindService(@NonNull android.content.ServiceConnection)` |  |
| `void unregisterComponentCallbacks(android.content.ComponentCallbacks)` |  |
| `abstract void unregisterReceiver(android.content.BroadcastReceiver)` |  |
| `void updateServiceGroup(@NonNull android.content.ServiceConnection, int, int)` |  |

---

### `class ContextWrapper`

- **继承：** `android.content.Context`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ContextWrapper(android.content.Context)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void attachBaseContext(android.content.Context)` |  |
| `boolean bindService(android.content.Intent, android.content.ServiceConnection, int)` |  |
| `int checkCallingOrSelfPermission(String)` |  |
| `int checkCallingOrSelfUriPermission(android.net.Uri, int)` |  |
| `int checkCallingPermission(String)` |  |
| `int checkCallingUriPermission(android.net.Uri, int)` |  |
| `int checkPermission(String, int, int)` |  |
| `int checkSelfPermission(String)` |  |
| `int checkUriPermission(android.net.Uri, int, int, int)` |  |
| `int checkUriPermission(android.net.Uri, String, String, int, int, int)` |  |
| `android.content.Context createConfigurationContext(android.content.res.Configuration)` |  |
| `android.content.Context createContextForSplit(String) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `android.content.Context createDeviceProtectedStorageContext()` |  |
| `android.content.Context createDisplayContext(android.view.Display)` |  |
| `android.content.Context createPackageContext(String, int) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `String[] databaseList()` |  |
| `boolean deleteDatabase(String)` |  |
| `boolean deleteFile(String)` |  |
| `boolean deleteSharedPreferences(String)` |  |
| `void enforceCallingOrSelfPermission(String, String)` |  |
| `void enforceCallingOrSelfUriPermission(android.net.Uri, int, String)` |  |
| `void enforceCallingPermission(String, String)` |  |
| `void enforceCallingUriPermission(android.net.Uri, int, String)` |  |
| `void enforcePermission(String, int, int, String)` |  |
| `void enforceUriPermission(android.net.Uri, int, int, int, String)` |  |
| `void enforceUriPermission(android.net.Uri, String, String, int, int, int, String)` |  |
| `String[] fileList()` |  |
| `android.content.Context getApplicationContext()` |  |
| `android.content.pm.ApplicationInfo getApplicationInfo()` |  |
| `android.content.res.AssetManager getAssets()` |  |
| `android.content.Context getBaseContext()` |  |
| `java.io.File getCacheDir()` |  |
| `ClassLoader getClassLoader()` |  |
| `java.io.File getCodeCacheDir()` |  |
| `android.content.ContentResolver getContentResolver()` |  |
| `java.io.File getDataDir()` |  |
| `java.io.File getDatabasePath(String)` |  |
| `java.io.File getDir(String, int)` |  |
| `java.io.File getExternalCacheDir()` |  |
| `java.io.File[] getExternalCacheDirs()` |  |
| `java.io.File getExternalFilesDir(String)` |  |
| `java.io.File[] getExternalFilesDirs(String)` |  |
| `java.io.File[] getExternalMediaDirs()` |  |
| `java.io.File getFileStreamPath(String)` |  |
| `java.io.File getFilesDir()` |  |
| `android.os.Looper getMainLooper()` |  |
| `java.io.File getNoBackupFilesDir()` |  |
| `java.io.File getObbDir()` |  |
| `java.io.File[] getObbDirs()` |  |
| `String getPackageCodePath()` |  |
| `android.content.pm.PackageManager getPackageManager()` |  |
| `String getPackageName()` |  |
| `String getPackageResourcePath()` |  |
| `android.content.res.Resources getResources()` |  |
| `android.content.SharedPreferences getSharedPreferences(String, int)` |  |
| `Object getSystemService(String)` |  |
| `String getSystemServiceName(Class<?>)` |  |
| `android.content.res.Resources.Theme getTheme()` |  |
| `void grantUriPermission(String, android.net.Uri, int)` |  |
| `boolean isDeviceProtectedStorage()` |  |
| `boolean moveDatabaseFrom(android.content.Context, String)` |  |
| `boolean moveSharedPreferencesFrom(android.content.Context, String)` |  |
| `java.io.FileInputStream openFileInput(String) throws java.io.FileNotFoundException` |  |
| `java.io.FileOutputStream openFileOutput(String, int) throws java.io.FileNotFoundException` |  |
| `android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String, int, android.database.sqlite.SQLiteDatabase.CursorFactory)` |  |
| `android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String, int, android.database.sqlite.SQLiteDatabase.CursorFactory, android.database.DatabaseErrorHandler)` |  |
| `android.content.Intent registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter)` |  |
| `android.content.Intent registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter, int)` |  |
| `android.content.Intent registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter, String, android.os.Handler)` |  |
| `android.content.Intent registerReceiver(android.content.BroadcastReceiver, android.content.IntentFilter, String, android.os.Handler, int)` |  |
| `void revokeUriPermission(android.net.Uri, int)` |  |
| `void revokeUriPermission(String, android.net.Uri, int)` |  |
| `void sendBroadcast(android.content.Intent)` |  |
| `void sendBroadcast(android.content.Intent, String)` |  |
| `void sendBroadcastAsUser(android.content.Intent, android.os.UserHandle)` |  |
| `void sendBroadcastAsUser(android.content.Intent, android.os.UserHandle, String)` |  |
| `void sendOrderedBroadcast(android.content.Intent, String)` |  |
| `void sendOrderedBroadcast(android.content.Intent, String, android.content.BroadcastReceiver, android.os.Handler, int, String, android.os.Bundle)` |  |
| `void sendOrderedBroadcast(@NonNull @RequiresPermission android.content.Intent, int, @Nullable String, @Nullable String, @Nullable android.content.BroadcastReceiver, @Nullable android.os.Handler, @Nullable String, @Nullable android.os.Bundle, @Nullable android.os.Bundle)` |  |
| `void sendOrderedBroadcastAsUser(android.content.Intent, android.os.UserHandle, String, android.content.BroadcastReceiver, android.os.Handler, int, String, android.os.Bundle)` |  |
| `void setTheme(int)` |  |
| `void startActivities(android.content.Intent[])` |  |
| `void startActivities(android.content.Intent[], android.os.Bundle)` |  |
| `void startActivity(android.content.Intent)` |  |
| `void startActivity(android.content.Intent, android.os.Bundle)` |  |
| `android.content.ComponentName startForegroundService(android.content.Intent)` |  |
| `boolean startInstrumentation(android.content.ComponentName, String, android.os.Bundle)` |  |
| `void startIntentSender(android.content.IntentSender, android.content.Intent, int, int, int) throws android.content.IntentSender.SendIntentException` |  |
| `void startIntentSender(android.content.IntentSender, android.content.Intent, int, int, int, android.os.Bundle) throws android.content.IntentSender.SendIntentException` |  |
| `android.content.ComponentName startService(android.content.Intent)` |  |
| `boolean stopService(android.content.Intent)` |  |
| `void unbindService(android.content.ServiceConnection)` |  |
| `void unregisterReceiver(android.content.BroadcastReceiver)` |  |

---

### `class CursorLoader` ~~DEPRECATED~~

- **继承：** `android.content.AsyncTaskLoader<android.database.Cursor>`
- **Annotations:** `@Deprecated`

---

### `interface DialogInterface`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int BUTTON_NEGATIVE = -2` |  |
| `static final int BUTTON_NEUTRAL = -3` |  |
| `static final int BUTTON_POSITIVE = -1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void cancel()` |  |
| `void dismiss()` |  |

---

### `interface static DialogInterface.OnCancelListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onCancel(android.content.DialogInterface)` |  |

---

### `interface static DialogInterface.OnClickListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onClick(android.content.DialogInterface, int)` |  |

---

### `interface static DialogInterface.OnDismissListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onDismiss(android.content.DialogInterface)` |  |

---

### `interface static DialogInterface.OnKeyListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean onKey(android.content.DialogInterface, int, android.view.KeyEvent)` |  |

---

### `interface static DialogInterface.OnMultiChoiceClickListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onClick(android.content.DialogInterface, int, boolean)` |  |

---

### `interface static DialogInterface.OnShowListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onShow(android.content.DialogInterface)` |  |

---

### `class final Entity`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Entity(android.content.ContentValues)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addSubValue(android.net.Uri, android.content.ContentValues)` |  |
| `android.content.ContentValues getEntityValues()` |  |
| `java.util.ArrayList<android.content.Entity.NamedContentValues> getSubValues()` |  |

---

### `class static Entity.NamedContentValues`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Entity.NamedContentValues(android.net.Uri, android.content.ContentValues)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `final android.net.Uri uri` |  |
| `final android.content.ContentValues values` |  |

---

### `interface EntityIterator`

- **继承：** `java.util.Iterator<android.content.Entity>`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close()` |  |
| `void reset()` |  |

---

### `class Intent`

- **实现：** `java.lang.Cloneable android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Intent()` |  |
| `Intent(android.content.Intent)` |  |
| `Intent(String)` |  |
| `Intent(String, android.net.Uri)` |  |
| `Intent(android.content.Context, Class<?>)` |  |
| `Intent(String, android.net.Uri, android.content.Context, Class<?>)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_AIRPLANE_MODE_CHANGED = "android.intent.action.AIRPLANE_MODE"` |  |
| `static final String ACTION_ALL_APPS = "android.intent.action.ALL_APPS"` |  |
| `static final String ACTION_ANSWER = "android.intent.action.ANSWER"` |  |
| `static final String ACTION_APPLICATION_PREFERENCES = "android.intent.action.APPLICATION_PREFERENCES"` |  |
| `static final String ACTION_APPLICATION_RESTRICTIONS_CHANGED = "android.intent.action.APPLICATION_RESTRICTIONS_CHANGED"` |  |
| `static final String ACTION_APP_ERROR = "android.intent.action.APP_ERROR"` |  |
| `static final String ACTION_ASSIST = "android.intent.action.ASSIST"` |  |
| `static final String ACTION_ATTACH_DATA = "android.intent.action.ATTACH_DATA"` |  |
| `static final String ACTION_AUTO_REVOKE_PERMISSIONS = "android.intent.action.AUTO_REVOKE_PERMISSIONS"` |  |
| `static final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED"` |  |
| `static final String ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW"` |  |
| `static final String ACTION_BATTERY_OKAY = "android.intent.action.BATTERY_OKAY"` |  |
| `static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"` |  |
| `static final String ACTION_BUG_REPORT = "android.intent.action.BUG_REPORT"` |  |
| `static final String ACTION_CALL = "android.intent.action.CALL"` |  |
| `static final String ACTION_CALL_BUTTON = "android.intent.action.CALL_BUTTON"` |  |
| `static final String ACTION_CAMERA_BUTTON = "android.intent.action.CAMERA_BUTTON"` |  |
| `static final String ACTION_CARRIER_SETUP = "android.intent.action.CARRIER_SETUP"` |  |
| `static final String ACTION_CHOOSER = "android.intent.action.CHOOSER"` |  |
| `static final String ACTION_CLOSE_SYSTEM_DIALOGS = "android.intent.action.CLOSE_SYSTEM_DIALOGS"` |  |
| `static final String ACTION_CONFIGURATION_CHANGED = "android.intent.action.CONFIGURATION_CHANGED"` |  |
| `static final String ACTION_CREATE_DOCUMENT = "android.intent.action.CREATE_DOCUMENT"` |  |
| `static final String ACTION_CREATE_REMINDER = "android.intent.action.CREATE_REMINDER"` |  |
| `static final String ACTION_CREATE_SHORTCUT = "android.intent.action.CREATE_SHORTCUT"` |  |
| `static final String ACTION_DATE_CHANGED = "android.intent.action.DATE_CHANGED"` |  |
| `static final String ACTION_DEFAULT = "android.intent.action.VIEW"` |  |
| `static final String ACTION_DEFINE = "android.intent.action.DEFINE"` |  |
| `static final String ACTION_DELETE = "android.intent.action.DELETE"` |  |
| `static final String ACTION_DIAL = "android.intent.action.DIAL"` |  |
| `static final String ACTION_DOCK_EVENT = "android.intent.action.DOCK_EVENT"` |  |
| `static final String ACTION_DREAMING_STARTED = "android.intent.action.DREAMING_STARTED"` |  |
| `static final String ACTION_DREAMING_STOPPED = "android.intent.action.DREAMING_STOPPED"` |  |
| `static final String ACTION_EDIT = "android.intent.action.EDIT"` |  |
| `static final String ACTION_EXTERNAL_APPLICATIONS_AVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE"` |  |
| `static final String ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE"` |  |
| `static final String ACTION_FACTORY_TEST = "android.intent.action.FACTORY_TEST"` |  |
| `static final String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"` |  |
| `static final String ACTION_GET_RESTRICTION_ENTRIES = "android.intent.action.GET_RESTRICTION_ENTRIES"` |  |
| `static final String ACTION_GTALK_SERVICE_CONNECTED = "android.intent.action.GTALK_CONNECTED"` |  |
| `static final String ACTION_GTALK_SERVICE_DISCONNECTED = "android.intent.action.GTALK_DISCONNECTED"` |  |
| `static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG"` |  |
| `static final String ACTION_INPUT_METHOD_CHANGED = "android.intent.action.INPUT_METHOD_CHANGED"` |  |
| `static final String ACTION_INSERT = "android.intent.action.INSERT"` |  |
| `static final String ACTION_INSERT_OR_EDIT = "android.intent.action.INSERT_OR_EDIT"` |  |
| `static final String ACTION_INSTALL_FAILURE = "android.intent.action.INSTALL_FAILURE"` |  |
| `static final String ACTION_LOCALE_CHANGED = "android.intent.action.LOCALE_CHANGED"` |  |
| `static final String ACTION_LOCKED_BOOT_COMPLETED = "android.intent.action.LOCKED_BOOT_COMPLETED"` |  |
| `static final String ACTION_MAIN = "android.intent.action.MAIN"` |  |
| `static final String ACTION_MANAGED_PROFILE_ADDED = "android.intent.action.MANAGED_PROFILE_ADDED"` |  |
| `static final String ACTION_MANAGED_PROFILE_AVAILABLE = "android.intent.action.MANAGED_PROFILE_AVAILABLE"` |  |
| `static final String ACTION_MANAGED_PROFILE_REMOVED = "android.intent.action.MANAGED_PROFILE_REMOVED"` |  |
| `static final String ACTION_MANAGED_PROFILE_UNAVAILABLE = "android.intent.action.MANAGED_PROFILE_UNAVAILABLE"` |  |
| `static final String ACTION_MANAGED_PROFILE_UNLOCKED = "android.intent.action.MANAGED_PROFILE_UNLOCKED"` |  |
| `static final String ACTION_MANAGE_NETWORK_USAGE = "android.intent.action.MANAGE_NETWORK_USAGE"` |  |
| `static final String ACTION_MANAGE_PACKAGE_STORAGE = "android.intent.action.MANAGE_PACKAGE_STORAGE"` |  |
| `static final String ACTION_MEDIA_BAD_REMOVAL = "android.intent.action.MEDIA_BAD_REMOVAL"` |  |
| `static final String ACTION_MEDIA_BUTTON = "android.intent.action.MEDIA_BUTTON"` |  |
| `static final String ACTION_MEDIA_CHECKING = "android.intent.action.MEDIA_CHECKING"` |  |
| `static final String ACTION_MEDIA_EJECT = "android.intent.action.MEDIA_EJECT"` |  |
| `static final String ACTION_MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED"` |  |
| `static final String ACTION_MEDIA_NOFS = "android.intent.action.MEDIA_NOFS"` |  |
| `static final String ACTION_MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED"` |  |
| `static final String ACTION_MEDIA_SCANNER_FINISHED = "android.intent.action.MEDIA_SCANNER_FINISHED"` |  |
| `static final String ACTION_MEDIA_SCANNER_STARTED = "android.intent.action.MEDIA_SCANNER_STARTED"` |  |
| `static final String ACTION_MEDIA_SHARED = "android.intent.action.MEDIA_SHARED"` |  |
| `static final String ACTION_MEDIA_UNMOUNTABLE = "android.intent.action.MEDIA_UNMOUNTABLE"` |  |
| `static final String ACTION_MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED"` |  |
| `static final String ACTION_MY_PACKAGE_REPLACED = "android.intent.action.MY_PACKAGE_REPLACED"` |  |
| `static final String ACTION_MY_PACKAGE_SUSPENDED = "android.intent.action.MY_PACKAGE_SUSPENDED"` |  |
| `static final String ACTION_MY_PACKAGE_UNSUSPENDED = "android.intent.action.MY_PACKAGE_UNSUSPENDED"` |  |
| `static final String ACTION_OPEN_DOCUMENT = "android.intent.action.OPEN_DOCUMENT"` |  |
| `static final String ACTION_OPEN_DOCUMENT_TREE = "android.intent.action.OPEN_DOCUMENT_TREE"` |  |
| `static final String ACTION_PACKAGES_SUSPENDED = "android.intent.action.PACKAGES_SUSPENDED"` |  |
| `static final String ACTION_PACKAGES_UNSUSPENDED = "android.intent.action.PACKAGES_UNSUSPENDED"` |  |
| `static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED"` |  |
| `static final String ACTION_PACKAGE_CHANGED = "android.intent.action.PACKAGE_CHANGED"` |  |
| `static final String ACTION_PACKAGE_DATA_CLEARED = "android.intent.action.PACKAGE_DATA_CLEARED"` |  |
| `static final String ACTION_PACKAGE_FIRST_LAUNCH = "android.intent.action.PACKAGE_FIRST_LAUNCH"` |  |
| `static final String ACTION_PACKAGE_FULLY_REMOVED = "android.intent.action.PACKAGE_FULLY_REMOVED"` |  |
| `static final String ACTION_PACKAGE_NEEDS_VERIFICATION = "android.intent.action.PACKAGE_NEEDS_VERIFICATION"` |  |
| `static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED"` |  |
| `static final String ACTION_PACKAGE_REPLACED = "android.intent.action.PACKAGE_REPLACED"` |  |
| `static final String ACTION_PACKAGE_RESTARTED = "android.intent.action.PACKAGE_RESTARTED"` |  |
| `static final String ACTION_PACKAGE_VERIFIED = "android.intent.action.PACKAGE_VERIFIED"` |  |
| `static final String ACTION_PASTE = "android.intent.action.PASTE"` |  |
| `static final String ACTION_PICK = "android.intent.action.PICK"` |  |
| `static final String ACTION_PICK_ACTIVITY = "android.intent.action.PICK_ACTIVITY"` |  |
| `static final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED"` |  |
| `static final String ACTION_POWER_DISCONNECTED = "android.intent.action.ACTION_POWER_DISCONNECTED"` |  |
| `static final String ACTION_POWER_USAGE_SUMMARY = "android.intent.action.POWER_USAGE_SUMMARY"` |  |
| `static final String ACTION_PROCESS_TEXT = "android.intent.action.PROCESS_TEXT"` |  |
| `static final String ACTION_PROVIDER_CHANGED = "android.intent.action.PROVIDER_CHANGED"` |  |
| `static final String ACTION_QUICK_CLOCK = "android.intent.action.QUICK_CLOCK"` |  |
| `static final String ACTION_QUICK_VIEW = "android.intent.action.QUICK_VIEW"` |  |
| `static final String ACTION_REBOOT = "android.intent.action.REBOOT"` |  |
| `static final String ACTION_RUN = "android.intent.action.RUN"` |  |
| `static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF"` |  |
| `static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON"` |  |
| `static final String ACTION_SEARCH = "android.intent.action.SEARCH"` |  |
| `static final String ACTION_SEARCH_LONG_PRESS = "android.intent.action.SEARCH_LONG_PRESS"` |  |
| `static final String ACTION_SEND = "android.intent.action.SEND"` |  |
| `static final String ACTION_SENDTO = "android.intent.action.SENDTO"` |  |
| `static final String ACTION_SEND_MULTIPLE = "android.intent.action.SEND_MULTIPLE"` |  |
| `static final String ACTION_SET_WALLPAPER = "android.intent.action.SET_WALLPAPER"` |  |
| `static final String ACTION_SHOW_APP_INFO = "android.intent.action.SHOW_APP_INFO"` |  |
| `static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN"` |  |
| `static final String ACTION_SYNC = "android.intent.action.SYNC"` |  |
| `static final String ACTION_SYSTEM_TUTORIAL = "android.intent.action.SYSTEM_TUTORIAL"` |  |
| `static final String ACTION_TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED"` |  |
| `static final String ACTION_TIME_CHANGED = "android.intent.action.TIME_SET"` |  |
| `static final String ACTION_TIME_TICK = "android.intent.action.TIME_TICK"` |  |
| `static final String ACTION_TRANSLATE = "android.intent.action.TRANSLATE"` |  |
| `static final String ACTION_UID_REMOVED = "android.intent.action.UID_REMOVED"` |  |
| `static final String ACTION_USER_BACKGROUND = "android.intent.action.USER_BACKGROUND"` |  |
| `static final String ACTION_USER_FOREGROUND = "android.intent.action.USER_FOREGROUND"` |  |
| `static final String ACTION_USER_INITIALIZE = "android.intent.action.USER_INITIALIZE"` |  |
| `static final String ACTION_USER_PRESENT = "android.intent.action.USER_PRESENT"` |  |
| `static final String ACTION_USER_UNLOCKED = "android.intent.action.USER_UNLOCKED"` |  |
| `static final String ACTION_VIEW = "android.intent.action.VIEW"` |  |
| `static final String ACTION_VIEW_LOCUS = "android.intent.action.VIEW_LOCUS"` |  |
| `static final String ACTION_VOICE_COMMAND = "android.intent.action.VOICE_COMMAND"` |  |
| `static final String ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH"` |  |
| `static final String CATEGORY_ACCESSIBILITY_SHORTCUT_TARGET = "android.intent.category.ACCESSIBILITY_SHORTCUT_TARGET"` |  |
| `static final String CATEGORY_ALTERNATIVE = "android.intent.category.ALTERNATIVE"` |  |
| `static final String CATEGORY_APP_BROWSER = "android.intent.category.APP_BROWSER"` |  |
| `static final String CATEGORY_APP_CALCULATOR = "android.intent.category.APP_CALCULATOR"` |  |
| `static final String CATEGORY_APP_CALENDAR = "android.intent.category.APP_CALENDAR"` |  |
| `static final String CATEGORY_APP_CONTACTS = "android.intent.category.APP_CONTACTS"` |  |
| `static final String CATEGORY_APP_EMAIL = "android.intent.category.APP_EMAIL"` |  |
| `static final String CATEGORY_APP_FILES = "android.intent.category.APP_FILES"` |  |
| `static final String CATEGORY_APP_GALLERY = "android.intent.category.APP_GALLERY"` |  |
| `static final String CATEGORY_APP_MAPS = "android.intent.category.APP_MAPS"` |  |
| `static final String CATEGORY_APP_MARKET = "android.intent.category.APP_MARKET"` |  |
| `static final String CATEGORY_APP_MESSAGING = "android.intent.category.APP_MESSAGING"` |  |
| `static final String CATEGORY_APP_MUSIC = "android.intent.category.APP_MUSIC"` |  |
| `static final String CATEGORY_BROWSABLE = "android.intent.category.BROWSABLE"` |  |
| `static final String CATEGORY_CAR_DOCK = "android.intent.category.CAR_DOCK"` |  |
| `static final String CATEGORY_CAR_MODE = "android.intent.category.CAR_MODE"` |  |
| `static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT"` |  |
| `static final String CATEGORY_DESK_DOCK = "android.intent.category.DESK_DOCK"` |  |
| `static final String CATEGORY_DEVELOPMENT_PREFERENCE = "android.intent.category.DEVELOPMENT_PREFERENCE"` |  |
| `static final String CATEGORY_EMBED = "android.intent.category.EMBED"` |  |
| `static final String CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST = "android.intent.category.FRAMEWORK_INSTRUMENTATION_TEST"` |  |
| `static final String CATEGORY_HE_DESK_DOCK = "android.intent.category.HE_DESK_DOCK"` |  |
| `static final String CATEGORY_HOME = "android.intent.category.HOME"` |  |
| `static final String CATEGORY_INFO = "android.intent.category.INFO"` |  |
| `static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER"` |  |
| `static final String CATEGORY_LEANBACK_LAUNCHER = "android.intent.category.LEANBACK_LAUNCHER"` |  |
| `static final String CATEGORY_LE_DESK_DOCK = "android.intent.category.LE_DESK_DOCK"` |  |
| `static final String CATEGORY_MONKEY = "android.intent.category.MONKEY"` |  |
| `static final String CATEGORY_OPENABLE = "android.intent.category.OPENABLE"` |  |
| `static final String CATEGORY_PREFERENCE = "android.intent.category.PREFERENCE"` |  |
| `static final String CATEGORY_SAMPLE_CODE = "android.intent.category.SAMPLE_CODE"` |  |
| `static final String CATEGORY_SECONDARY_HOME = "android.intent.category.SECONDARY_HOME"` |  |
| `static final String CATEGORY_SELECTED_ALTERNATIVE = "android.intent.category.SELECTED_ALTERNATIVE"` |  |
| `static final String CATEGORY_TAB = "android.intent.category.TAB"` |  |
| `static final String CATEGORY_TEST = "android.intent.category.TEST"` |  |
| `static final String CATEGORY_TYPED_OPENABLE = "android.intent.category.TYPED_OPENABLE"` |  |
| `static final String CATEGORY_UNIT_TEST = "android.intent.category.UNIT_TEST"` |  |
| `static final String CATEGORY_VOICE = "android.intent.category.VOICE"` |  |
| `static final String CATEGORY_VR_HOME = "android.intent.category.VR_HOME"` |  |
| `static final String EXTRA_ALARM_COUNT = "android.intent.extra.ALARM_COUNT"` |  |
| `static final String EXTRA_ALLOW_MULTIPLE = "android.intent.extra.ALLOW_MULTIPLE"` |  |
| `static final String EXTRA_ALTERNATE_INTENTS = "android.intent.extra.ALTERNATE_INTENTS"` |  |
| `static final String EXTRA_ASSIST_CONTEXT = "android.intent.extra.ASSIST_CONTEXT"` |  |
| `static final String EXTRA_ASSIST_INPUT_DEVICE_ID = "android.intent.extra.ASSIST_INPUT_DEVICE_ID"` |  |
| `static final String EXTRA_ASSIST_INPUT_HINT_KEYBOARD = "android.intent.extra.ASSIST_INPUT_HINT_KEYBOARD"` |  |
| `static final String EXTRA_ASSIST_PACKAGE = "android.intent.extra.ASSIST_PACKAGE"` |  |
| `static final String EXTRA_ASSIST_UID = "android.intent.extra.ASSIST_UID"` |  |
| `static final String EXTRA_AUTO_LAUNCH_SINGLE_CHOICE = "android.intent.extra.AUTO_LAUNCH_SINGLE_CHOICE"` |  |
| `static final String EXTRA_BCC = "android.intent.extra.BCC"` |  |
| `static final String EXTRA_BUG_REPORT = "android.intent.extra.BUG_REPORT"` |  |
| `static final String EXTRA_CC = "android.intent.extra.CC"` |  |
| `static final String EXTRA_CHANGED_COMPONENT_NAME_LIST = "android.intent.extra.changed_component_name_list"` |  |
| `static final String EXTRA_CHANGED_PACKAGE_LIST = "android.intent.extra.changed_package_list"` |  |
| `static final String EXTRA_CHANGED_UID_LIST = "android.intent.extra.changed_uid_list"` |  |
| `static final String EXTRA_CHOOSER_REFINEMENT_INTENT_SENDER = "android.intent.extra.CHOOSER_REFINEMENT_INTENT_SENDER"` |  |
| `static final String EXTRA_CHOOSER_TARGETS = "android.intent.extra.CHOOSER_TARGETS"` |  |
| `static final String EXTRA_CHOSEN_COMPONENT = "android.intent.extra.CHOSEN_COMPONENT"` |  |
| `static final String EXTRA_CHOSEN_COMPONENT_INTENT_SENDER = "android.intent.extra.CHOSEN_COMPONENT_INTENT_SENDER"` |  |
| `static final String EXTRA_COMPONENT_NAME = "android.intent.extra.COMPONENT_NAME"` |  |
| `static final String EXTRA_CONTENT_ANNOTATIONS = "android.intent.extra.CONTENT_ANNOTATIONS"` |  |
| `static final String EXTRA_CONTENT_QUERY = "android.intent.extra.CONTENT_QUERY"` |  |
| `static final String EXTRA_DATA_REMOVED = "android.intent.extra.DATA_REMOVED"` |  |
| `static final String EXTRA_DOCK_STATE = "android.intent.extra.DOCK_STATE"` |  |
| `static final int EXTRA_DOCK_STATE_CAR = 2` |  |
| `static final int EXTRA_DOCK_STATE_DESK = 1` |  |
| `static final int EXTRA_DOCK_STATE_HE_DESK = 4` |  |
| `static final int EXTRA_DOCK_STATE_LE_DESK = 3` |  |
| `static final int EXTRA_DOCK_STATE_UNDOCKED = 0` |  |
| `static final String EXTRA_DONT_KILL_APP = "android.intent.extra.DONT_KILL_APP"` |  |
| `static final String EXTRA_DURATION_MILLIS = "android.intent.extra.DURATION_MILLIS"` |  |
| `static final String EXTRA_EMAIL = "android.intent.extra.EMAIL"` |  |
| `static final String EXTRA_EXCLUDE_COMPONENTS = "android.intent.extra.EXCLUDE_COMPONENTS"` |  |
| `static final String EXTRA_FROM_STORAGE = "android.intent.extra.FROM_STORAGE"` |  |
| `static final String EXTRA_HTML_TEXT = "android.intent.extra.HTML_TEXT"` |  |
| `static final String EXTRA_INDEX = "android.intent.extra.INDEX"` |  |
| `static final String EXTRA_INITIAL_INTENTS = "android.intent.extra.INITIAL_INTENTS"` |  |
| `static final String EXTRA_INSTALLER_PACKAGE_NAME = "android.intent.extra.INSTALLER_PACKAGE_NAME"` |  |
| `static final String EXTRA_INTENT = "android.intent.extra.INTENT"` |  |
| `static final String EXTRA_KEY_EVENT = "android.intent.extra.KEY_EVENT"` |  |
| `static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY"` |  |
| `static final String EXTRA_LOCUS_ID = "android.intent.extra.LOCUS_ID"` |  |
| `static final String EXTRA_MIME_TYPES = "android.intent.extra.MIME_TYPES"` |  |
| `static final String EXTRA_NOT_UNKNOWN_SOURCE = "android.intent.extra.NOT_UNKNOWN_SOURCE"` |  |
| `static final String EXTRA_ORIGINATING_URI = "android.intent.extra.ORIGINATING_URI"` |  |
| `static final String EXTRA_PACKAGE_NAME = "android.intent.extra.PACKAGE_NAME"` |  |
| `static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER"` |  |
| `static final String EXTRA_PROCESS_TEXT = "android.intent.extra.PROCESS_TEXT"` |  |
| `static final String EXTRA_PROCESS_TEXT_READONLY = "android.intent.extra.PROCESS_TEXT_READONLY"` |  |
| `static final String EXTRA_QUICK_VIEW_FEATURES = "android.intent.extra.QUICK_VIEW_FEATURES"` |  |
| `static final String EXTRA_QUIET_MODE = "android.intent.extra.QUIET_MODE"` |  |
| `static final String EXTRA_REFERRER = "android.intent.extra.REFERRER"` |  |
| `static final String EXTRA_REFERRER_NAME = "android.intent.extra.REFERRER_NAME"` |  |
| `static final String EXTRA_REMOTE_INTENT_TOKEN = "android.intent.extra.remote_intent_token"` |  |
| `static final String EXTRA_REPLACEMENT_EXTRAS = "android.intent.extra.REPLACEMENT_EXTRAS"` |  |
| `static final String EXTRA_REPLACING = "android.intent.extra.REPLACING"` |  |
| `static final String EXTRA_RESTRICTIONS_BUNDLE = "android.intent.extra.restrictions_bundle"` |  |
| `static final String EXTRA_RESTRICTIONS_INTENT = "android.intent.extra.restrictions_intent"` |  |
| `static final String EXTRA_RESTRICTIONS_LIST = "android.intent.extra.restrictions_list"` |  |
| `static final String EXTRA_RESULT_RECEIVER = "android.intent.extra.RESULT_RECEIVER"` |  |
| `static final String EXTRA_RETURN_RESULT = "android.intent.extra.RETURN_RESULT"` |  |
| `static final String EXTRA_SHORTCUT_ID = "android.intent.extra.shortcut.ID"` |  |
| `static final String EXTRA_SHUTDOWN_USERSPACE_ONLY = "android.intent.extra.SHUTDOWN_USERSPACE_ONLY"` |  |
| `static final String EXTRA_SPLIT_NAME = "android.intent.extra.SPLIT_NAME"` |  |
| `static final String EXTRA_STREAM = "android.intent.extra.STREAM"` |  |
| `static final String EXTRA_SUBJECT = "android.intent.extra.SUBJECT"` |  |
| `static final String EXTRA_SUSPENDED_PACKAGE_EXTRAS = "android.intent.extra.SUSPENDED_PACKAGE_EXTRAS"` |  |
| `static final String EXTRA_TEMPLATE = "android.intent.extra.TEMPLATE"` |  |
| `static final String EXTRA_TEXT = "android.intent.extra.TEXT"` |  |
| `static final String EXTRA_TIME = "android.intent.extra.TIME"` |  |
| `static final String EXTRA_TIMEZONE = "time-zone"` |  |
| `static final String EXTRA_TITLE = "android.intent.extra.TITLE"` |  |
| `static final String EXTRA_UID = "android.intent.extra.UID"` |  |
| `static final String EXTRA_USER = "android.intent.extra.USER"` |  |
| `static final int FILL_IN_ACTION = 1` |  |
| `static final int FILL_IN_CATEGORIES = 4` |  |
| `static final int FILL_IN_CLIP_DATA = 128` |  |
| `static final int FILL_IN_COMPONENT = 8` |  |
| `static final int FILL_IN_DATA = 2` |  |
| `static final int FILL_IN_IDENTIFIER = 256` |  |
| `static final int FILL_IN_PACKAGE = 16` |  |
| `static final int FILL_IN_SELECTOR = 64` |  |
| `static final int FILL_IN_SOURCE_BOUNDS = 32` |  |
| `static final int FLAG_ACTIVITY_BROUGHT_TO_FRONT = 4194304` |  |
| `static final int FLAG_ACTIVITY_CLEAR_TASK = 32768` |  |
| `static final int FLAG_ACTIVITY_CLEAR_TOP = 67108864` |  |
| `static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 8388608` |  |
| `static final int FLAG_ACTIVITY_FORWARD_RESULT = 33554432` |  |
| `static final int FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY = 1048576` |  |
| `static final int FLAG_ACTIVITY_LAUNCH_ADJACENT = 4096` |  |
| `static final int FLAG_ACTIVITY_MATCH_EXTERNAL = 2048` |  |
| `static final int FLAG_ACTIVITY_MULTIPLE_TASK = 134217728` |  |
| `static final int FLAG_ACTIVITY_NEW_DOCUMENT = 524288` |  |
| `static final int FLAG_ACTIVITY_NEW_TASK = 268435456` |  |
| `static final int FLAG_ACTIVITY_NO_ANIMATION = 65536` |  |
| `static final int FLAG_ACTIVITY_NO_HISTORY = 1073741824` |  |
| `static final int FLAG_ACTIVITY_NO_USER_ACTION = 262144` |  |
| `static final int FLAG_ACTIVITY_PREVIOUS_IS_TOP = 16777216` |  |
| `static final int FLAG_ACTIVITY_REORDER_TO_FRONT = 131072` |  |
| `static final int FLAG_ACTIVITY_REQUIRE_DEFAULT = 512` |  |
| `static final int FLAG_ACTIVITY_REQUIRE_NON_BROWSER = 1024` |  |
| `static final int FLAG_ACTIVITY_RESET_TASK_IF_NEEDED = 2097152` |  |
| `static final int FLAG_ACTIVITY_RETAIN_IN_RECENTS = 8192` |  |
| `static final int FLAG_ACTIVITY_SINGLE_TOP = 536870912` |  |
| `static final int FLAG_ACTIVITY_TASK_ON_HOME = 16384` |  |
| `static final int FLAG_DEBUG_LOG_RESOLUTION = 8` |  |
| `static final int FLAG_DIRECT_BOOT_AUTO = 256` |  |
| `static final int FLAG_EXCLUDE_STOPPED_PACKAGES = 16` |  |
| `static final int FLAG_FROM_BACKGROUND = 4` |  |
| `static final int FLAG_GRANT_PERSISTABLE_URI_PERMISSION = 64` |  |
| `static final int FLAG_GRANT_PREFIX_URI_PERMISSION = 128` |  |
| `static final int FLAG_GRANT_READ_URI_PERMISSION = 1` |  |
| `static final int FLAG_GRANT_WRITE_URI_PERMISSION = 2` |  |
| `static final int FLAG_INCLUDE_STOPPED_PACKAGES = 32` |  |
| `static final int FLAG_RECEIVER_FOREGROUND = 268435456` |  |
| `static final int FLAG_RECEIVER_NO_ABORT = 134217728` |  |
| `static final int FLAG_RECEIVER_REGISTERED_ONLY = 1073741824` |  |
| `static final int FLAG_RECEIVER_REPLACE_PENDING = 536870912` |  |
| `static final int FLAG_RECEIVER_VISIBLE_TO_INSTANT_APPS = 2097152` |  |
| `static final String METADATA_DOCK_HOME = "android.dock_home"` |  |
| `static final int URI_ALLOW_UNSAFE = 4` |  |
| `static final int URI_ANDROID_APP_SCHEME = 2` |  |
| `static final int URI_INTENT_SCHEME = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `Object clone()` |  |
| `static android.content.Intent createChooser(android.content.Intent, CharSequence)` |  |
| `static android.content.Intent createChooser(android.content.Intent, CharSequence, android.content.IntentSender)` |  |
| `int describeContents()` |  |
| `int fillIn(@NonNull android.content.Intent, int)` |  |
| `boolean filterEquals(android.content.Intent)` |  |
| `int filterHashCode()` |  |
| `boolean getBooleanExtra(String, boolean)` |  |
| `byte getByteExtra(String, byte)` |  |
| `java.util.Set<java.lang.String> getCategories()` |  |
| `char getCharExtra(String, char)` |  |
| `double getDoubleExtra(String, double)` |  |
| `int getFlags()` |  |
| `float getFloatExtra(String, float)` |  |
| `int getIntExtra(String, int)` |  |
| `static android.content.Intent getIntentOld(String) throws java.net.URISyntaxException` |  |
| `long getLongExtra(String, long)` |  |
| `short getShortExtra(String, short)` |  |
| `boolean hasCategory(String)` |  |
| `boolean hasExtra(String)` |  |
| `boolean hasFileDescriptors()` |  |
| `static android.content.Intent makeMainActivity(android.content.ComponentName)` |  |
| `static android.content.Intent makeMainSelectorActivity(String, String)` |  |
| `static android.content.Intent makeRestartActivityTask(android.content.ComponentName)` |  |
| `static android.content.Intent parseUri(String, int) throws java.net.URISyntaxException` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void removeCategory(String)` |  |
| `void removeExtra(String)` |  |
| `void removeFlags(int)` |  |
| `android.content.ComponentName resolveActivity(@NonNull android.content.pm.PackageManager)` |  |
| `android.content.pm.ActivityInfo resolveActivityInfo(@NonNull android.content.pm.PackageManager, int)` |  |
| `void setClipData(@Nullable android.content.ClipData)` |  |
| `void setExtrasClassLoader(@Nullable ClassLoader)` |  |
| `void setSelector(@Nullable android.content.Intent)` |  |
| `void setSourceBounds(@Nullable android.graphics.Rect)` |  |
| `String toUri(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final Intent.FilterComparison`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Intent.FilterComparison(android.content.Intent)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.Intent getIntent()` |  |

---

### `class static Intent.ShortcutIconResource`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Intent.ShortcutIconResource()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `String packageName` |  |
| `String resourceName` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `static android.content.Intent.ShortcutIconResource fromContext(android.content.Context, @AnyRes int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class IntentFilter`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `IntentFilter()` |  |
| `IntentFilter(String)` |  |
| `IntentFilter(String, String) throws android.content.IntentFilter.MalformedMimeTypeException` |  |
| `IntentFilter(android.content.IntentFilter)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int MATCH_ADJUSTMENT_MASK = 65535` |  |
| `static final int MATCH_ADJUSTMENT_NORMAL = 32768` |  |
| `static final int MATCH_CATEGORY_EMPTY = 1048576` |  |
| `static final int MATCH_CATEGORY_HOST = 3145728` |  |
| `static final int MATCH_CATEGORY_MASK = 268369920` |  |
| `static final int MATCH_CATEGORY_PATH = 5242880` |  |
| `static final int MATCH_CATEGORY_PORT = 4194304` |  |
| `static final int MATCH_CATEGORY_SCHEME = 2097152` |  |
| `static final int MATCH_CATEGORY_SCHEME_SPECIFIC_PART = 5767168` |  |
| `static final int MATCH_CATEGORY_TYPE = 6291456` |  |
| `static final int NO_MATCH_ACTION = -3` |  |
| `static final int NO_MATCH_CATEGORY = -4` |  |
| `static final int NO_MATCH_DATA = -2` |  |
| `static final int NO_MATCH_TYPE = -1` |  |
| `static final int SYSTEM_HIGH_PRIORITY = 1000` |  |
| `static final int SYSTEM_LOW_PRIORITY = -1000` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final java.util.Iterator<java.lang.String> actionsIterator()` |  |
| `final void addAction(String)` |  |
| `final void addCategory(String)` |  |
| `final void addDataAuthority(String, String)` |  |
| `final void addDataPath(String, int)` |  |
| `final void addDataScheme(String)` |  |
| `final void addDataSchemeSpecificPart(String, int)` |  |
| `final void addDataType(String) throws android.content.IntentFilter.MalformedMimeTypeException` |  |
| `final java.util.Iterator<android.content.IntentFilter.AuthorityEntry> authoritiesIterator()` |  |
| `final java.util.Iterator<java.lang.String> categoriesIterator()` |  |
| `final int countActions()` |  |
| `final int countCategories()` |  |
| `final int countDataAuthorities()` |  |
| `final int countDataPaths()` |  |
| `final int countDataSchemeSpecificParts()` |  |
| `final int countDataSchemes()` |  |
| `final int countDataTypes()` |  |
| `static android.content.IntentFilter create(String, String)` |  |
| `final int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `final String getAction(int)` |  |
| `final String getCategory(int)` |  |
| `final android.content.IntentFilter.AuthorityEntry getDataAuthority(int)` |  |
| `final android.os.PatternMatcher getDataPath(int)` |  |
| `final String getDataScheme(int)` |  |
| `final android.os.PatternMatcher getDataSchemeSpecificPart(int)` |  |
| `final String getDataType(int)` |  |
| `final int getPriority()` |  |
| `final boolean hasAction(String)` |  |
| `final boolean hasCategory(String)` |  |
| `final boolean hasDataAuthority(android.net.Uri)` |  |
| `final boolean hasDataPath(String)` |  |
| `final boolean hasDataScheme(String)` |  |
| `final boolean hasDataSchemeSpecificPart(String)` |  |
| `final boolean hasDataType(String)` |  |
| `final int match(android.content.ContentResolver, android.content.Intent, boolean, String)` |  |
| `final int match(String, String, String, android.net.Uri, java.util.Set<java.lang.String>, String)` |  |
| `final boolean matchAction(String)` |  |
| `final String matchCategories(java.util.Set<java.lang.String>)` |  |
| `final int matchData(String, String, android.net.Uri)` |  |
| `final int matchDataAuthority(android.net.Uri)` |  |
| `final java.util.Iterator<android.os.PatternMatcher> pathsIterator()` |  |
| `void readFromXml(org.xmlpull.v1.XmlPullParser) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `final java.util.Iterator<android.os.PatternMatcher> schemeSpecificPartsIterator()` |  |
| `final java.util.Iterator<java.lang.String> schemesIterator()` |  |
| `final void setPriority(int)` |  |
| `final java.util.Iterator<java.lang.String> typesIterator()` |  |
| `final void writeToParcel(android.os.Parcel, int)` |  |
| `void writeToXml(org.xmlpull.v1.XmlSerializer) throws java.io.IOException` |  |

---

### `class static final IntentFilter.AuthorityEntry`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `IntentFilter.AuthorityEntry(String, String)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `String getHost()` |  |
| `int getPort()` |  |
| `int match(android.net.Uri)` |  |

---

### `class static IntentFilter.MalformedMimeTypeException`

- **继承：** `android.util.AndroidException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `IntentFilter.MalformedMimeTypeException()` |  |
| `IntentFilter.MalformedMimeTypeException(String)` |  |

---

### `class IntentSender`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getCreatorPackage()` |  |
| `int getCreatorUid()` |  |
| `android.os.UserHandle getCreatorUserHandle()` |  |
| `static android.content.IntentSender readIntentSenderOrNullFromParcel(android.os.Parcel)` |  |
| `void sendIntent(android.content.Context, int, android.content.Intent, android.content.IntentSender.OnFinished, android.os.Handler) throws android.content.IntentSender.SendIntentException` |  |
| `void sendIntent(android.content.Context, int, android.content.Intent, android.content.IntentSender.OnFinished, android.os.Handler, String) throws android.content.IntentSender.SendIntentException` |  |
| `static void writeIntentSenderOrNullToParcel(android.content.IntentSender, android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface static IntentSender.OnFinished`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onSendFinished(android.content.IntentSender, android.content.Intent, int, String, android.os.Bundle)` |  |

---

### `class static IntentSender.SendIntentException`

- **继承：** `android.util.AndroidException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `IntentSender.SendIntentException()` |  |
| `IntentSender.SendIntentException(String)` |  |
| `IntentSender.SendIntentException(Exception)` |  |

---

### `class Loader<D>` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final Loader.ForceLoadContentObserver` ~~DEPRECATED~~

- **继承：** `android.database.ContentObserver`
- **Annotations:** `@Deprecated`

---

### `interface static Loader.OnLoadCanceledListener<D>` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static Loader.OnLoadCompleteListener<D>` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final LocusId`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `LocusId(@NonNull String)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class MutableContextWrapper`

- **继承：** `android.content.ContextWrapper`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `MutableContextWrapper(android.content.Context)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void setBaseContext(android.content.Context)` |  |

---

### `class OperationApplicationException`

- **继承：** `java.lang.Exception`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `OperationApplicationException()` |  |
| `OperationApplicationException(String)` |  |
| `OperationApplicationException(String, Throwable)` |  |
| `OperationApplicationException(Throwable)` |  |
| `OperationApplicationException(int)` |  |
| `OperationApplicationException(String, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getNumSuccessfulYieldPoints()` |  |

---

### `class PeriodicSync`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PeriodicSync(android.accounts.Account, String, android.os.Bundle, long)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `final android.accounts.Account account` |  |
| `final String authority` |  |
| `final android.os.Bundle extras` |  |
| `final long period` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class QuickViewConstants`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String FEATURE_DELETE = "android:delete"` |  |
| `static final String FEATURE_DOWNLOAD = "android:download"` |  |
| `static final String FEATURE_EDIT = "android:edit"` |  |
| `static final String FEATURE_PRINT = "android:print"` |  |
| `static final String FEATURE_SEND = "android:send"` |  |
| `static final String FEATURE_VIEW = "android:view"` |  |

---

### `class ReceiverCallNotAllowedException`

- **继承：** `android.util.AndroidRuntimeException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ReceiverCallNotAllowedException(String)` |  |

---

### `class RestrictionEntry`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `RestrictionEntry(int, String)` |  |
| `RestrictionEntry(String, String)` |  |
| `RestrictionEntry(String, boolean)` |  |
| `RestrictionEntry(String, String[])` |  |
| `RestrictionEntry(String, int)` |  |
| `RestrictionEntry(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int TYPE_BOOLEAN = 1` |  |
| `static final int TYPE_BUNDLE = 7` |  |
| `static final int TYPE_BUNDLE_ARRAY = 8` |  |
| `static final int TYPE_CHOICE = 2` |  |
| `static final int TYPE_INTEGER = 5` |  |
| `static final int TYPE_MULTI_SELECT = 4` |  |
| `static final int TYPE_NULL = 0` |  |
| `static final int TYPE_STRING = 6` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static android.content.RestrictionEntry createBundleArrayEntry(String, android.content.RestrictionEntry[])` |  |
| `static android.content.RestrictionEntry createBundleEntry(String, android.content.RestrictionEntry[])` |  |
| `int describeContents()` |  |
| `String[] getAllSelectedStrings()` |  |
| `String[] getChoiceEntries()` |  |
| `String[] getChoiceValues()` |  |
| `String getDescription()` |  |
| `int getIntValue()` |  |
| `String getKey()` |  |
| `android.content.RestrictionEntry[] getRestrictions()` |  |
| `boolean getSelectedState()` |  |
| `String getSelectedString()` |  |
| `String getTitle()` |  |
| `int getType()` |  |
| `void setAllSelectedStrings(String[])` |  |
| `void setChoiceEntries(String[])` |  |
| `void setChoiceEntries(android.content.Context, @ArrayRes int)` |  |
| `void setChoiceValues(String[])` |  |
| `void setChoiceValues(android.content.Context, @ArrayRes int)` |  |
| `void setDescription(String)` |  |
| `void setIntValue(int)` |  |
| `void setRestrictions(android.content.RestrictionEntry[])` |  |
| `void setSelectedState(boolean)` |  |
| `void setSelectedString(String)` |  |
| `void setTitle(String)` |  |
| `void setType(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class RestrictionsManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_PERMISSION_RESPONSE_RECEIVED = "android.content.action.PERMISSION_RESPONSE_RECEIVED"` |  |
| `static final String ACTION_REQUEST_LOCAL_APPROVAL = "android.content.action.REQUEST_LOCAL_APPROVAL"` |  |
| `static final String ACTION_REQUEST_PERMISSION = "android.content.action.REQUEST_PERMISSION"` |  |
| `static final String EXTRA_PACKAGE_NAME = "android.content.extra.PACKAGE_NAME"` |  |
| `static final String EXTRA_REQUEST_BUNDLE = "android.content.extra.REQUEST_BUNDLE"` |  |
| `static final String EXTRA_REQUEST_ID = "android.content.extra.REQUEST_ID"` |  |
| `static final String EXTRA_REQUEST_TYPE = "android.content.extra.REQUEST_TYPE"` |  |
| `static final String EXTRA_RESPONSE_BUNDLE = "android.content.extra.RESPONSE_BUNDLE"` |  |
| `static final String META_DATA_APP_RESTRICTIONS = "android.content.APP_RESTRICTIONS"` |  |
| `static final String REQUEST_KEY_APPROVE_LABEL = "android.request.approve_label"` |  |
| `static final String REQUEST_KEY_DATA = "android.request.data"` |  |
| `static final String REQUEST_KEY_DENY_LABEL = "android.request.deny_label"` |  |
| `static final String REQUEST_KEY_ICON = "android.request.icon"` |  |
| `static final String REQUEST_KEY_ID = "android.request.id"` |  |
| `static final String REQUEST_KEY_MESSAGE = "android.request.mesg"` |  |
| `static final String REQUEST_KEY_NEW_REQUEST = "android.request.new_request"` |  |
| `static final String REQUEST_KEY_TITLE = "android.request.title"` |  |
| `static final String REQUEST_TYPE_APPROVAL = "android.request.type.approval"` |  |
| `static final String RESPONSE_KEY_ERROR_CODE = "android.response.errorcode"` |  |
| `static final String RESPONSE_KEY_MESSAGE = "android.response.msg"` |  |
| `static final String RESPONSE_KEY_RESPONSE_TIMESTAMP = "android.response.timestamp"` |  |
| `static final String RESPONSE_KEY_RESULT = "android.response.result"` |  |
| `static final int RESULT_APPROVED = 1` |  |
| `static final int RESULT_DENIED = 2` |  |
| `static final int RESULT_ERROR = 5` |  |
| `static final int RESULT_ERROR_BAD_REQUEST = 1` |  |
| `static final int RESULT_ERROR_INTERNAL = 3` |  |
| `static final int RESULT_ERROR_NETWORK = 2` |  |
| `static final int RESULT_NO_RESPONSE = 3` |  |
| `static final int RESULT_UNKNOWN_REQUEST = 4` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static android.os.Bundle convertRestrictionsToBundle(java.util.List<android.content.RestrictionEntry>)` |  |
| `android.content.Intent createLocalApprovalIntent()` |  |
| `android.os.Bundle getApplicationRestrictions()` |  |
| `java.util.List<android.content.RestrictionEntry> getManifestRestrictions(String)` |  |
| `boolean hasRestrictionsProvider()` |  |
| `void notifyPermissionResponse(String, android.os.PersistableBundle)` |  |
| `void requestPermission(String, String, android.os.PersistableBundle)` |  |

---

### `class SearchRecentSuggestionsProvider`

- **继承：** `android.content.ContentProvider`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SearchRecentSuggestionsProvider()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int DATABASE_MODE_2LINES = 2` |  |
| `static final int DATABASE_MODE_QUERIES = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int delete(android.net.Uri, String, String[])` |  |
| `String getType(android.net.Uri)` |  |
| `android.net.Uri insert(android.net.Uri, android.content.ContentValues)` |  |
| `boolean onCreate()` |  |
| `android.database.Cursor query(android.net.Uri, String[], String, String[], String)` |  |
| `void setupSuggestions(String, int)` |  |
| `int update(android.net.Uri, android.content.ContentValues, String, String[])` |  |

---

### `interface ServiceConnection`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `default void onBindingDied(android.content.ComponentName)` |  |
| `default void onNullBinding(android.content.ComponentName)` |  |
| `void onServiceConnected(android.content.ComponentName, android.os.IBinder)` |  |
| `void onServiceDisconnected(android.content.ComponentName)` |  |

---

### `interface SharedPreferences`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean contains(String)` |  |
| `android.content.SharedPreferences.Editor edit()` |  |
| `java.util.Map<java.lang.String,?> getAll()` |  |
| `boolean getBoolean(String, boolean)` |  |
| `float getFloat(String, float)` |  |
| `int getInt(String, int)` |  |
| `long getLong(String, long)` |  |
| `void registerOnSharedPreferenceChangeListener(android.content.SharedPreferences.OnSharedPreferenceChangeListener)` |  |
| `void unregisterOnSharedPreferenceChangeListener(android.content.SharedPreferences.OnSharedPreferenceChangeListener)` |  |

---

### `interface static SharedPreferences.Editor`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void apply()` |  |
| `android.content.SharedPreferences.Editor clear()` |  |
| `boolean commit()` |  |
| `android.content.SharedPreferences.Editor putBoolean(String, boolean)` |  |
| `android.content.SharedPreferences.Editor putFloat(String, float)` |  |
| `android.content.SharedPreferences.Editor putInt(String, int)` |  |
| `android.content.SharedPreferences.Editor putLong(String, long)` |  |
| `android.content.SharedPreferences.Editor putString(String, @Nullable String)` |  |
| `android.content.SharedPreferences.Editor putStringSet(String, @Nullable java.util.Set<java.lang.String>)` |  |
| `android.content.SharedPreferences.Editor remove(String)` |  |

---

### `interface static SharedPreferences.OnSharedPreferenceChangeListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onSharedPreferenceChanged(android.content.SharedPreferences, String)` |  |

---

### `class SyncAdapterType`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SyncAdapterType(String, String, boolean, boolean)` |  |
| `SyncAdapterType(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `final String accountType` |  |
| `final String authority` |  |
| `final boolean isKey` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean allowParallelSyncs()` |  |
| `int describeContents()` |  |
| `String getSettingsActivity()` |  |
| `boolean isAlwaysSyncable()` |  |
| `boolean isUserVisible()` |  |
| `static android.content.SyncAdapterType newKey(String, String)` |  |
| `boolean supportsUploading()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SyncContext`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.os.IBinder getSyncContextBinder()` |  |
| `void onFinished(android.content.SyncResult)` |  |

---

### `class SyncInfo`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `final android.accounts.Account account` |  |
| `final String authority` |  |
| `final long startTime` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SyncRequest`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static SyncRequest.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SyncRequest.Builder()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.SyncRequest build()` |  |
| `android.content.SyncRequest.Builder setDisallowMetered(boolean)` |  |
| `android.content.SyncRequest.Builder setExpedited(boolean)` |  |
| `android.content.SyncRequest.Builder setExtras(android.os.Bundle)` |  |
| `android.content.SyncRequest.Builder setIgnoreBackoff(boolean)` |  |
| `android.content.SyncRequest.Builder setIgnoreSettings(boolean)` |  |
| `android.content.SyncRequest.Builder setManual(boolean)` |  |
| `android.content.SyncRequest.Builder setNoRetry(boolean)` |  |
| `android.content.SyncRequest.Builder setRequiresCharging(boolean)` |  |
| `android.content.SyncRequest.Builder setSyncAdapter(android.accounts.Account, String)` |  |
| `android.content.SyncRequest.Builder syncOnce()` |  |
| `android.content.SyncRequest.Builder syncPeriodic(long, long)` |  |

---

### `class final SyncResult`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SyncResult()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final android.content.SyncResult ALREADY_IN_PROGRESS` |  |
| `boolean databaseError` |  |
| `long delayUntil` |  |
| `boolean fullSyncRequested` |  |
| `boolean moreRecordsToGet` |  |
| `boolean partialSyncUnavailable` |  |
| `final android.content.SyncStats stats` |  |
| `final boolean syncAlreadyInProgress` |  |
| `boolean tooManyDeletions` |  |
| `boolean tooManyRetries` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void clear()` |  |
| `int describeContents()` |  |
| `boolean hasError()` |  |
| `boolean hasHardError()` |  |
| `boolean hasSoftError()` |  |
| `boolean madeSomeProgress()` |  |
| `String toDebugString()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class SyncStats`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SyncStats()` |  |
| `SyncStats(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `long numAuthExceptions` |  |
| `long numConflictDetectedExceptions` |  |
| `long numDeletes` |  |
| `long numEntries` |  |
| `long numInserts` |  |
| `long numIoExceptions` |  |
| `long numParseExceptions` |  |
| `long numSkippedEntries` |  |
| `long numUpdates` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void clear()` |  |
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface SyncStatusObserver`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onStatusChanged(int)` |  |

---

### `class UriMatcher`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `UriMatcher(int)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int NO_MATCH = -1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addURI(String, String, int)` |  |
| `int match(android.net.Uri)` |  |

---

### `class final UriPermission`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final long INVALID_TIME = -9223372036854775808L` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getPersistedTime()` |  |
| `android.net.Uri getUri()` |  |
| `boolean isReadPermission()` |  |
| `boolean isWritePermission()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## 包： `android.content.pm`

### `class ActivityInfo`

- **继承：** `android.content.pm.ComponentInfo`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityInfo()` |  |
| `ActivityInfo(android.content.pm.ActivityInfo)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int COLOR_MODE_DEFAULT = 0` |  |
| `static final int COLOR_MODE_HDR = 2` |  |
| `static final int COLOR_MODE_WIDE_COLOR_GAMUT = 1` |  |
| `static final int CONFIG_COLOR_MODE = 16384` |  |
| `static final int CONFIG_DENSITY = 4096` |  |
| `static final int CONFIG_FONT_SCALE = 1073741824` |  |
| `static final int CONFIG_KEYBOARD = 16` |  |
| `static final int CONFIG_KEYBOARD_HIDDEN = 32` |  |
| `static final int CONFIG_LAYOUT_DIRECTION = 8192` |  |
| `static final int CONFIG_LOCALE = 4` |  |
| `static final int CONFIG_MCC = 1` |  |
| `static final int CONFIG_MNC = 2` |  |
| `static final int CONFIG_NAVIGATION = 64` |  |
| `static final int CONFIG_ORIENTATION = 128` |  |
| `static final int CONFIG_SCREEN_LAYOUT = 256` |  |
| `static final int CONFIG_SCREEN_SIZE = 1024` |  |
| `static final int CONFIG_SMALLEST_SCREEN_SIZE = 2048` |  |
| `static final int CONFIG_TOUCHSCREEN = 8` |  |
| `static final int CONFIG_UI_MODE = 512` |  |
| `static final int DOCUMENT_LAUNCH_ALWAYS = 2` |  |
| `static final int DOCUMENT_LAUNCH_INTO_EXISTING = 1` |  |
| `static final int DOCUMENT_LAUNCH_NEVER = 3` |  |
| `static final int DOCUMENT_LAUNCH_NONE = 0` |  |
| `static final int FLAG_ALLOW_TASK_REPARENTING = 64` |  |
| `static final int FLAG_ALWAYS_RETAIN_TASK_STATE = 8` |  |
| `static final int FLAG_AUTO_REMOVE_FROM_RECENTS = 8192` |  |
| `static final int FLAG_CLEAR_TASK_ON_LAUNCH = 4` |  |
| `static final int FLAG_ENABLE_VR_MODE = 32768` |  |
| `static final int FLAG_EXCLUDE_FROM_RECENTS = 32` |  |
| `static final int FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS = 256` |  |
| `static final int FLAG_FINISH_ON_TASK_LAUNCH = 2` |  |
| `static final int FLAG_HARDWARE_ACCELERATED = 512` |  |
| `static final int FLAG_IMMERSIVE = 2048` |  |
| `static final int FLAG_MULTIPROCESS = 1` |  |
| `static final int FLAG_NO_HISTORY = 128` |  |
| `static final int FLAG_PREFER_MINIMAL_POST_PROCESSING = 33554432` |  |
| `static final int FLAG_RELINQUISH_TASK_IDENTITY = 4096` |  |
| `static final int FLAG_RESUME_WHILE_PAUSING = 16384` |  |
| `static final int FLAG_SINGLE_USER = 1073741824` |  |
| `static final int FLAG_STATE_NOT_NEEDED = 16` |  |
| `static final int LAUNCH_MULTIPLE = 0` |  |
| `static final int LAUNCH_SINGLE_INSTANCE = 3` |  |
| `static final int LAUNCH_SINGLE_TASK = 2` |  |
| `static final int LAUNCH_SINGLE_TOP = 1` |  |
| `static final int PERSIST_ACROSS_REBOOTS = 2` |  |
| `static final int PERSIST_NEVER = 1` |  |
| `static final int PERSIST_ROOT_ONLY = 0` |  |
| `static final int SCREEN_ORIENTATION_BEHIND = 3` |  |
| `static final int SCREEN_ORIENTATION_FULL_SENSOR = 10` |  |
| `static final int SCREEN_ORIENTATION_FULL_USER = 13` |  |
| `static final int SCREEN_ORIENTATION_LANDSCAPE = 0` |  |
| `static final int SCREEN_ORIENTATION_LOCKED = 14` |  |
| `static final int SCREEN_ORIENTATION_NOSENSOR = 5` |  |
| `static final int SCREEN_ORIENTATION_PORTRAIT = 1` |  |
| `static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8` |  |
| `static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9` |  |
| `static final int SCREEN_ORIENTATION_SENSOR = 4` |  |
| `static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6` |  |
| `static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7` |  |
| `static final int SCREEN_ORIENTATION_UNSPECIFIED = -1` |  |
| `static final int SCREEN_ORIENTATION_USER = 2` |  |
| `static final int SCREEN_ORIENTATION_USER_LANDSCAPE = 11` |  |
| `static final int SCREEN_ORIENTATION_USER_PORTRAIT = 12` |  |
| `static final int UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW = 1` |  |
| `int colorMode` |  |
| `int configChanges` |  |
| `int documentLaunchMode` |  |
| `int flags` |  |
| `int launchMode` |  |
| `int maxRecents` |  |
| `String parentActivityName` |  |
| `String permission` |  |
| `int persistableMode` |  |
| `int screenOrientation` |  |
| `int softInputMode` |  |
| `String targetActivity` |  |
| `String taskAffinity` |  |
| `int theme` |  |
| `int uiOptions` |  |
| `android.content.pm.ActivityInfo.WindowLayout windowLayout` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `final int getThemeResource()` |  |

---

### `class static final ActivityInfo.WindowLayout`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ActivityInfo.WindowLayout(int, float, int, float, int, int, int)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `final int gravity` |  |
| `final int height` |  |
| `final float heightFraction` |  |
| `final int minHeight` |  |
| `final int minWidth` |  |
| `final int width` |  |
| `final float widthFraction` |  |

---

### `class ApplicationInfo`

- **继承：** `android.content.pm.PackageItemInfo`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ApplicationInfo()` |  |
| `ApplicationInfo(android.content.pm.ApplicationInfo)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int CATEGORY_AUDIO = 1` |  |
| `static final int CATEGORY_GAME = 0` |  |
| `static final int CATEGORY_IMAGE = 3` |  |
| `static final int CATEGORY_MAPS = 6` |  |
| `static final int CATEGORY_NEWS = 5` |  |
| `static final int CATEGORY_PRODUCTIVITY = 7` |  |
| `static final int CATEGORY_SOCIAL = 4` |  |
| `static final int CATEGORY_UNDEFINED = -1` |  |
| `static final int CATEGORY_VIDEO = 2` |  |
| `static final int FLAG_ALLOW_BACKUP = 32768` |  |
| `static final int FLAG_ALLOW_CLEAR_USER_DATA = 64` |  |
| `static final int FLAG_ALLOW_TASK_REPARENTING = 32` |  |
| `static final int FLAG_DEBUGGABLE = 2` |  |
| `static final int FLAG_EXTERNAL_STORAGE = 262144` |  |
| `static final int FLAG_EXTRACT_NATIVE_LIBS = 268435456` |  |
| `static final int FLAG_FACTORY_TEST = 16` |  |
| `static final int FLAG_FULL_BACKUP_ONLY = 67108864` |  |
| `static final int FLAG_HARDWARE_ACCELERATED = 536870912` |  |
| `static final int FLAG_HAS_CODE = 4` |  |
| `static final int FLAG_INSTALLED = 8388608` |  |
| `static final int FLAG_IS_DATA_ONLY = 16777216` |  |
| `static final int FLAG_KILL_AFTER_RESTORE = 65536` |  |
| `static final int FLAG_LARGE_HEAP = 1048576` |  |
| `static final int FLAG_MULTIARCH = -2147483648` |  |
| `static final int FLAG_PERSISTENT = 8` |  |
| `static final int FLAG_RESIZEABLE_FOR_SCREENS = 4096` |  |
| `static final int FLAG_RESTORE_ANY_VERSION = 131072` |  |
| `static final int FLAG_STOPPED = 2097152` |  |
| `static final int FLAG_SUPPORTS_LARGE_SCREENS = 2048` |  |
| `static final int FLAG_SUPPORTS_NORMAL_SCREENS = 1024` |  |
| `static final int FLAG_SUPPORTS_RTL = 4194304` |  |
| `static final int FLAG_SUPPORTS_SMALL_SCREENS = 512` |  |
| `static final int FLAG_SUPPORTS_XLARGE_SCREENS = 524288` |  |
| `static final int FLAG_SUSPENDED = 1073741824` |  |
| `static final int FLAG_SYSTEM = 1` |  |
| `static final int FLAG_TEST_ONLY = 256` |  |
| `static final int FLAG_UPDATED_SYSTEM_APP = 128` |  |
| `static final int FLAG_USES_CLEARTEXT_TRAFFIC = 134217728` |  |
| `static final int FLAG_VM_SAFE_MODE = 16384` |  |
| `static final int GWP_ASAN_ALWAYS = 1` |  |
| `static final int GWP_ASAN_DEFAULT = -1` |  |
| `static final int GWP_ASAN_NEVER = 0` |  |
| `String appComponentFactory` |  |
| `String backupAgentName` |  |
| `int category` |  |
| `String className` |  |
| `int compatibleWidthLimitDp` |  |
| `String dataDir` |  |
| `int descriptionRes` |  |
| `String deviceProtectedDataDir` |  |
| `boolean enabled` |  |
| `int flags` |  |
| `int largestWidthLimitDp` |  |
| `String manageSpaceActivityName` |  |
| `int minSdkVersion` |  |
| `String nativeLibraryDir` |  |
| `String permission` |  |
| `String processName` |  |
| `String publicSourceDir` |  |
| `int requiresSmallestWidthDp` |  |
| `String[] sharedLibraryFiles` |  |
| `String sourceDir` |  |
| `String[] splitNames` |  |
| `String[] splitPublicSourceDirs` |  |
| `String[] splitSourceDirs` |  |
| `java.util.UUID storageUuid` |  |
| `int targetSdkVersion` |  |
| `String taskAffinity` |  |
| `int theme` |  |
| `int uiOptions` |  |
| `int uid` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `static CharSequence getCategoryTitle(android.content.Context, int)` |  |
| `int getGwpAsanMode()` |  |
| `boolean isProfileableByShell()` |  |
| `boolean isResourceOverlay()` |  |
| `boolean isVirtualPreload()` |  |
| `CharSequence loadDescription(android.content.pm.PackageManager)` |  |

---

### `class static ApplicationInfo.DisplayNameComparator`

- **实现：** `java.util.Comparator<android.content.pm.ApplicationInfo>`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ApplicationInfo.DisplayNameComparator(android.content.pm.PackageManager)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final int compare(android.content.pm.ApplicationInfo, android.content.pm.ApplicationInfo)` |  |

---

### `class final ChangedPackages`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ChangedPackages(int, @NonNull java.util.List<java.lang.String>)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getSequenceNumber()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ComponentInfo`

- **继承：** `android.content.pm.PackageItemInfo`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ComponentInfo()` |  |
| `ComponentInfo(android.content.pm.ComponentInfo)` |  |
| `ComponentInfo(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.pm.ApplicationInfo applicationInfo` |  |
| `int descriptionRes` |  |
| `boolean directBootAware` |  |
| `boolean enabled` |  |
| `boolean exported` |  |
| `String processName` |  |
| `String splitName` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final int getBannerResource()` |  |
| `final int getIconResource()` |  |
| `final int getLogoResource()` |  |
| `boolean isEnabled()` |  |

---

### `class ConfigurationInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ConfigurationInfo()` |  |
| `ConfigurationInfo(android.content.pm.ConfigurationInfo)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int GL_ES_VERSION_UNDEFINED = 0` |  |
| `static final int INPUT_FEATURE_FIVE_WAY_NAV = 2` |  |
| `static final int INPUT_FEATURE_HARD_KEYBOARD = 1` |  |
| `int reqGlEsVersion` |  |
| `int reqInputFeatures` |  |
| `int reqKeyboardType` |  |
| `int reqNavigation` |  |
| `int reqTouchScreen` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getGlEsVersion()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class CrossProfileApps`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_CAN_INTERACT_ACROSS_PROFILES_CHANGED = "android.content.pm.action.CAN_INTERACT_ACROSS_PROFILES_CHANGED"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean canInteractAcrossProfiles()` |  |
| `boolean canRequestInteractAcrossProfiles()` |  |
| `void startMainActivity(@NonNull android.content.ComponentName, @NonNull android.os.UserHandle)` |  |

---

### `class final FeatureGroupInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `FeatureGroupInfo()` |  |
| `FeatureGroupInfo(android.content.pm.FeatureGroupInfo)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.pm.FeatureInfo[] features` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class FeatureInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `FeatureInfo()` |  |
| `FeatureInfo(android.content.pm.FeatureInfo)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_REQUIRED = 1` |  |
| `static final int GL_ES_VERSION_UNDEFINED = 0` |  |
| `int flags` |  |
| `String name` |  |
| `int reqGlEsVersion` |  |
| `int version` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getGlEsVersion()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final InstallSourceInfo`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class InstrumentationInfo`

- **继承：** `android.content.pm.PackageItemInfo`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `InstrumentationInfo()` |  |
| `InstrumentationInfo(android.content.pm.InstrumentationInfo)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `String dataDir` |  |
| `boolean functionalTest` |  |
| `boolean handleProfiling` |  |
| `String publicSourceDir` |  |
| `String sourceDir` |  |
| `String[] splitNames` |  |
| `String[] splitPublicSourceDirs` |  |
| `String[] splitSourceDirs` |  |
| `String targetPackage` |  |
| `String targetProcesses` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |

---

### `class LabeledIntent`

- **继承：** `android.content.Intent`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `LabeledIntent(android.content.Intent, String, int, int)` |  |
| `LabeledIntent(android.content.Intent, String, CharSequence, int)` |  |
| `LabeledIntent(String, int, int)` |  |
| `LabeledIntent(String, CharSequence, int)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int getIconResource()` |  |
| `int getLabelResource()` |  |
| `CharSequence getNonLocalizedLabel()` |  |
| `String getSourcePackage()` |  |
| `android.graphics.drawable.Drawable loadIcon(android.content.pm.PackageManager)` |  |
| `CharSequence loadLabel(android.content.pm.PackageManager)` |  |

---

### `class LauncherActivityInfo`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.pm.ApplicationInfo getApplicationInfo()` |  |
| `android.graphics.drawable.Drawable getBadgedIcon(int)` |  |
| `android.content.ComponentName getComponentName()` |  |
| `long getFirstInstallTime()` |  |
| `android.graphics.drawable.Drawable getIcon(int)` |  |
| `CharSequence getLabel()` |  |
| `String getName()` |  |
| `android.os.UserHandle getUser()` |  |

---

### `class LauncherApps`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `LauncherApps.Callback()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_CONFIRM_PIN_APPWIDGET = "android.content.pm.action.CONFIRM_PIN_APPWIDGET"` |  |
| `static final String ACTION_CONFIRM_PIN_SHORTCUT = "android.content.pm.action.CONFIRM_PIN_SHORTCUT"` |  |
| `static final String EXTRA_PIN_ITEM_REQUEST = "android.content.pm.extra.PIN_ITEM_REQUEST"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `java.util.List<android.content.pm.LauncherActivityInfo> getActivityList(String, android.os.UserHandle)` |  |
| `android.content.pm.ApplicationInfo getApplicationInfo(@NonNull String, int, @NonNull android.os.UserHandle) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `android.content.pm.LauncherApps.PinItemRequest getPinItemRequest(android.content.Intent)` |  |
| `java.util.List<android.os.UserHandle> getProfiles()` |  |
| `android.graphics.drawable.Drawable getShortcutBadgedIconDrawable(android.content.pm.ShortcutInfo, int)` |  |
| `java.util.List<android.content.pm.LauncherActivityInfo> getShortcutConfigActivityList(@Nullable String, @NonNull android.os.UserHandle)` |  |
| `android.graphics.drawable.Drawable getShortcutIconDrawable(@NonNull android.content.pm.ShortcutInfo, int)` |  |
| `boolean hasShortcutHostPermission()` |  |
| `boolean isActivityEnabled(android.content.ComponentName, android.os.UserHandle)` |  |
| `boolean isPackageEnabled(String, android.os.UserHandle)` |  |
| `void pinShortcuts(@NonNull String, @NonNull java.util.List<java.lang.String>, @NonNull android.os.UserHandle)` |  |
| `void registerCallback(android.content.pm.LauncherApps.Callback)` |  |
| `void registerCallback(android.content.pm.LauncherApps.Callback, android.os.Handler)` |  |
| `void registerPackageInstallerSessionCallback(@NonNull java.util.concurrent.Executor, @NonNull android.content.pm.PackageInstaller.SessionCallback)` |  |
| `android.content.pm.LauncherActivityInfo resolveActivity(android.content.Intent, android.os.UserHandle)` |  |
| `boolean shouldHideFromSuggestions(@NonNull String, @NonNull android.os.UserHandle)` |  |
| `void startAppDetailsActivity(android.content.ComponentName, android.os.UserHandle, android.graphics.Rect, android.os.Bundle)` |  |
| `void startMainActivity(android.content.ComponentName, android.os.UserHandle, android.graphics.Rect, android.os.Bundle)` |  |
| `void startPackageInstallerSessionDetailsActivity(@NonNull android.content.pm.PackageInstaller.SessionInfo, @Nullable android.graphics.Rect, @Nullable android.os.Bundle)` |  |
| `void startShortcut(@NonNull String, @NonNull String, @Nullable android.graphics.Rect, @Nullable android.os.Bundle, @NonNull android.os.UserHandle)` |  |
| `void startShortcut(@NonNull android.content.pm.ShortcutInfo, @Nullable android.graphics.Rect, @Nullable android.os.Bundle)` |  |
| `void unregisterCallback(android.content.pm.LauncherApps.Callback)` |  |
| `void unregisterPackageInstallerSessionCallback(@NonNull android.content.pm.PackageInstaller.SessionCallback)` |  |
| `abstract void onPackageAdded(String, android.os.UserHandle)` |  |
| `abstract void onPackageChanged(String, android.os.UserHandle)` |  |
| `abstract void onPackageRemoved(String, android.os.UserHandle)` |  |
| `abstract void onPackagesAvailable(String[], android.os.UserHandle, boolean)` |  |
| `void onPackagesSuspended(String[], android.os.UserHandle)` |  |
| `abstract void onPackagesUnavailable(String[], android.os.UserHandle, boolean)` |  |
| `void onPackagesUnsuspended(String[], android.os.UserHandle)` |  |
| `void onShortcutsChanged(@NonNull String, @NonNull java.util.List<android.content.pm.ShortcutInfo>, @NonNull android.os.UserHandle)` |  |

---

### `class static final LauncherApps.PinItemRequest`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int REQUEST_TYPE_APPWIDGET = 2` |  |
| `static final int REQUEST_TYPE_SHORTCUT = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean accept(@Nullable android.os.Bundle)` |  |
| `boolean accept()` |  |
| `int describeContents()` |  |
| `int getRequestType()` |  |
| `boolean isValid()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static LauncherApps.ShortcutQuery`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `LauncherApps.ShortcutQuery()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_GET_KEY_FIELDS_ONLY = 4` |  |
| `static final int FLAG_MATCH_CACHED = 16` |  |
| `static final int FLAG_MATCH_DYNAMIC = 1` |  |
| `static final int FLAG_MATCH_MANIFEST = 8` |  |
| `static final int FLAG_MATCH_PINNED = 2` |  |
| `static final int FLAG_MATCH_PINNED_BY_ANY_LAUNCHER = 1024` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.pm.LauncherApps.ShortcutQuery setActivity(@Nullable android.content.ComponentName)` |  |
| `android.content.pm.LauncherApps.ShortcutQuery setChangedSince(long)` |  |
| `android.content.pm.LauncherApps.ShortcutQuery setPackage(@Nullable String)` |  |
| `android.content.pm.LauncherApps.ShortcutQuery setQueryFlags(int)` |  |
| `android.content.pm.LauncherApps.ShortcutQuery setShortcutIds(@Nullable java.util.List<java.lang.String>)` |  |

---

### `class final ModuleInfo`

- **实现：** `android.os.Parcelable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean isHidden()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class PackageInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PackageInfo()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int INSTALL_LOCATION_AUTO = 0` |  |
| `static final int INSTALL_LOCATION_INTERNAL_ONLY = 1` |  |
| `static final int INSTALL_LOCATION_PREFER_EXTERNAL = 2` |  |
| `static final int REQUESTED_PERMISSION_GRANTED = 2` |  |
| `android.content.pm.ActivityInfo[] activities` |  |
| `android.content.pm.ApplicationInfo applicationInfo` |  |
| `int baseRevisionCode` |  |
| `android.content.pm.ConfigurationInfo[] configPreferences` |  |
| `android.content.pm.FeatureGroupInfo[] featureGroups` |  |
| `long firstInstallTime` |  |
| `int[] gids` |  |
| `int installLocation` |  |
| `android.content.pm.InstrumentationInfo[] instrumentation` |  |
| `boolean isApex` |  |
| `long lastUpdateTime` |  |
| `String packageName` |  |
| `android.content.pm.PermissionInfo[] permissions` |  |
| `android.content.pm.ProviderInfo[] providers` |  |
| `android.content.pm.ActivityInfo[] receivers` |  |
| `android.content.pm.FeatureInfo[] reqFeatures` |  |
| `String[] requestedPermissions` |  |
| `int[] requestedPermissionsFlags` |  |
| `android.content.pm.ServiceInfo[] services` |  |
| `String sharedUserId` |  |
| `int sharedUserLabel` |  |
| `android.content.pm.SigningInfo signingInfo` |  |
| `String[] splitNames` |  |
| `int[] splitRevisionCodes` |  |
| `String versionName` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getLongVersionCode()` |  |
| `void setLongVersionCode(long)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class PackageInstaller`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final String ACTION_SESSION_COMMITTED = "android.content.pm.action.SESSION_COMMITTED"` |  |
| `static final String ACTION_SESSION_DETAILS = "android.content.pm.action.SESSION_DETAILS"` |  |
| `static final String ACTION_SESSION_UPDATED = "android.content.pm.action.SESSION_UPDATED"` |  |
| `static final String EXTRA_OTHER_PACKAGE_NAME = "android.content.pm.extra.OTHER_PACKAGE_NAME"` |  |
| `static final String EXTRA_PACKAGE_NAME = "android.content.pm.extra.PACKAGE_NAME"` |  |
| `static final String EXTRA_SESSION = "android.content.pm.extra.SESSION"` |  |
| `static final String EXTRA_SESSION_ID = "android.content.pm.extra.SESSION_ID"` |  |
| `static final String EXTRA_STATUS = "android.content.pm.extra.STATUS"` |  |
| `static final String EXTRA_STATUS_MESSAGE = "android.content.pm.extra.STATUS_MESSAGE"` |  |
| `static final String EXTRA_STORAGE_PATH = "android.content.pm.extra.STORAGE_PATH"` |  |
| `static final int STATUS_FAILURE = 1` |  |
| `static final int STATUS_FAILURE_ABORTED = 3` |  |
| `static final int STATUS_FAILURE_BLOCKED = 2` |  |
| `static final int STATUS_FAILURE_CONFLICT = 5` |  |
| `static final int STATUS_FAILURE_INCOMPATIBLE = 7` |  |
| `static final int STATUS_FAILURE_INVALID = 4` |  |
| `static final int STATUS_FAILURE_STORAGE = 6` |  |
| `static final int STATUS_PENDING_USER_ACTION = -1` |  |
| `static final int STATUS_SUCCESS = 0` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void abandonSession(int)` |  |
| `int createSession(@NonNull android.content.pm.PackageInstaller.SessionParams) throws java.io.IOException` |  |
| `void registerSessionCallback(@NonNull android.content.pm.PackageInstaller.SessionCallback)` |  |
| `void registerSessionCallback(@NonNull android.content.pm.PackageInstaller.SessionCallback, @NonNull android.os.Handler)` |  |
| `void unregisterSessionCallback(@NonNull android.content.pm.PackageInstaller.SessionCallback)` |  |
| `void updateSessionAppIcon(int, @Nullable android.graphics.Bitmap)` |  |
| `void updateSessionAppLabel(int, @Nullable CharSequence)` |  |

---

### `class static PackageInstaller.Session`

- **实现：** `java.io.Closeable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PackageInstaller.SessionCallback()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void abandon()` |  |
| `void addChildSessionId(int)` |  |
| `void close()` |  |
| `void commit(@NonNull android.content.IntentSender)` |  |
| `void fsync(@NonNull java.io.OutputStream) throws java.io.IOException` |  |
| `int getParentSessionId()` |  |
| `boolean isMultiPackage()` |  |
| `boolean isStaged()` |  |
| `void removeChildSessionId(int)` |  |
| `void removeSplit(@NonNull String) throws java.io.IOException` |  |
| `void setStagingProgress(float)` |  |
| `void transfer(@NonNull String) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `abstract void onActiveChanged(int, boolean)` |  |
| `abstract void onBadgingChanged(int)` |  |
| `abstract void onCreated(int)` |  |
| `abstract void onFinished(int, boolean)` |  |
| `abstract void onProgressChanged(int, float)` |  |

---

### `class static PackageInstaller.SessionInfo`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final android.os.Parcelable.Creator<android.content.pm.PackageInstaller.SessionInfo> CREATOR` |  |
| `static final int INVALID_ID = -1` |  |
| `static final int STAGED_SESSION_ACTIVATION_FAILED = 2` |  |
| `static final int STAGED_SESSION_NO_ERROR = 0` |  |
| `static final int STAGED_SESSION_UNKNOWN = 3` |  |
| `static final int STAGED_SESSION_VERIFICATION_FAILED = 1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getCreatedMillis()` |  |
| `int getInstallLocation()` |  |
| `int getInstallReason()` |  |
| `int getMode()` |  |
| `int getOriginatingUid()` |  |
| `int getParentSessionId()` |  |
| `float getProgress()` |  |
| `int getSessionId()` |  |
| `long getSize()` |  |
| `int getStagedSessionErrorCode()` |  |
| `long getUpdatedMillis()` |  |
| `boolean hasParentSessionId()` |  |
| `boolean isActive()` |  |
| `boolean isCommitted()` |  |
| `boolean isMultiPackage()` |  |
| `boolean isSealed()` |  |
| `boolean isStaged()` |  |
| `boolean isStagedSessionActive()` |  |
| `boolean isStagedSessionApplied()` |  |
| `boolean isStagedSessionFailed()` |  |
| `boolean isStagedSessionReady()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static PackageInstaller.SessionParams`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PackageInstaller.SessionParams(int)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final android.os.Parcelable.Creator<android.content.pm.PackageInstaller.SessionParams> CREATOR` |  |
| `static final int MODE_FULL_INSTALL = 1` |  |
| `static final int MODE_INHERIT_EXISTING = 2` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void setAppIcon(@Nullable android.graphics.Bitmap)` |  |
| `void setAppLabel(@Nullable CharSequence)` |  |
| `void setAppPackageName(@Nullable String)` |  |
| `void setAutoRevokePermissionsMode(boolean)` |  |
| `void setInstallLocation(int)` |  |
| `void setInstallReason(int)` |  |
| `void setMultiPackage()` |  |
| `void setOriginatingUid(int)` |  |
| `void setOriginatingUri(@Nullable android.net.Uri)` |  |
| `void setReferrerUri(@Nullable android.net.Uri)` |  |
| `void setSize(long)` |  |
| `void setWhitelistedRestrictedPermissions(@Nullable java.util.Set<java.lang.String>)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class PackageItemInfo`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PackageItemInfo()` |  |
| `PackageItemInfo(android.content.pm.PackageItemInfo)` |  |
| `PackageItemInfo(android.os.Parcel)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `int banner` |  |
| `int icon` |  |
| `int labelRes` |  |
| `int logo` |  |
| `android.os.Bundle metaData` |  |
| `String name` |  |
| `CharSequence nonLocalizedLabel` |  |
| `String packageName` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void dumpBack(android.util.Printer, String)` |  |
| `void dumpFront(android.util.Printer, String)` |  |
| `android.graphics.drawable.Drawable loadBanner(android.content.pm.PackageManager)` |  |
| `android.graphics.drawable.Drawable loadIcon(android.content.pm.PackageManager)` |  |
| `android.graphics.drawable.Drawable loadLogo(android.content.pm.PackageManager)` |  |
| `android.graphics.drawable.Drawable loadUnbadgedIcon(android.content.pm.PackageManager)` |  |
| `android.content.res.XmlResourceParser loadXmlMetaData(android.content.pm.PackageManager, String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static PackageItemInfo.DisplayNameComparator`

- **实现：** `java.util.Comparator<android.content.pm.PackageItemInfo>`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PackageItemInfo.DisplayNameComparator(android.content.pm.PackageManager)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final int compare(android.content.pm.PackageItemInfo, android.content.pm.PackageItemInfo)` |  |

---

### `class abstract PackageManager`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PackageManager()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int CERT_INPUT_RAW_X509 = 0` |  |
| `static final int CERT_INPUT_SHA256 = 1` |  |
| `static final int COMPONENT_ENABLED_STATE_DEFAULT = 0` |  |
| `static final int COMPONENT_ENABLED_STATE_DISABLED = 2` |  |
| `static final int COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED = 4` |  |
| `static final int COMPONENT_ENABLED_STATE_DISABLED_USER = 3` |  |
| `static final int COMPONENT_ENABLED_STATE_ENABLED = 1` |  |
| `static final int DONT_KILL_APP = 1` |  |
| `static final String EXTRA_VERIFICATION_ID = "android.content.pm.extra.VERIFICATION_ID"` |  |
| `static final String EXTRA_VERIFICATION_RESULT = "android.content.pm.extra.VERIFICATION_RESULT"` |  |
| `static final String FEATURE_ACTIVITIES_ON_SECONDARY_DISPLAYS = "android.software.activities_on_secondary_displays"` |  |
| `static final String FEATURE_APP_WIDGETS = "android.software.app_widgets"` |  |
| `static final String FEATURE_AUDIO_LOW_LATENCY = "android.hardware.audio.low_latency"` |  |
| `static final String FEATURE_AUDIO_OUTPUT = "android.hardware.audio.output"` |  |
| `static final String FEATURE_AUDIO_PRO = "android.hardware.audio.pro"` |  |
| `static final String FEATURE_AUTOFILL = "android.software.autofill"` |  |
| `static final String FEATURE_AUTOMOTIVE = "android.hardware.type.automotive"` |  |
| `static final String FEATURE_BACKUP = "android.software.backup"` |  |
| `static final String FEATURE_BLUETOOTH = "android.hardware.bluetooth"` |  |
| `static final String FEATURE_BLUETOOTH_LE = "android.hardware.bluetooth_le"` |  |
| `static final String FEATURE_CAMERA = "android.hardware.camera"` |  |
| `static final String FEATURE_CAMERA_ANY = "android.hardware.camera.any"` |  |
| `static final String FEATURE_CAMERA_AR = "android.hardware.camera.ar"` |  |
| `static final String FEATURE_CAMERA_AUTOFOCUS = "android.hardware.camera.autofocus"` |  |
| `static final String FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING = "android.hardware.camera.capability.manual_post_processing"` |  |
| `static final String FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR = "android.hardware.camera.capability.manual_sensor"` |  |
| `static final String FEATURE_CAMERA_CAPABILITY_RAW = "android.hardware.camera.capability.raw"` |  |
| `static final String FEATURE_CAMERA_CONCURRENT = "android.hardware.camera.concurrent"` |  |
| `static final String FEATURE_CAMERA_EXTERNAL = "android.hardware.camera.external"` |  |
| `static final String FEATURE_CAMERA_FLASH = "android.hardware.camera.flash"` |  |
| `static final String FEATURE_CAMERA_FRONT = "android.hardware.camera.front"` |  |
| `static final String FEATURE_CAMERA_LEVEL_FULL = "android.hardware.camera.level.full"` |  |
| `static final String FEATURE_CANT_SAVE_STATE = "android.software.cant_save_state"` |  |
| `static final String FEATURE_COMPANION_DEVICE_SETUP = "android.software.companion_device_setup"` |  |
| `static final String FEATURE_CONNECTION_SERVICE = "android.software.connectionservice"` |  |
| `static final String FEATURE_CONSUMER_IR = "android.hardware.consumerir"` |  |
| `static final String FEATURE_CONTROLS = "android.software.controls"` |  |
| `static final String FEATURE_DEVICE_ADMIN = "android.software.device_admin"` |  |
| `static final String FEATURE_EMBEDDED = "android.hardware.type.embedded"` |  |
| `static final String FEATURE_ETHERNET = "android.hardware.ethernet"` |  |
| `static final String FEATURE_FACE = "android.hardware.biometrics.face"` |  |
| `static final String FEATURE_FAKETOUCH = "android.hardware.faketouch"` |  |
| `static final String FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT = "android.hardware.faketouch.multitouch.distinct"` |  |
| `static final String FEATURE_FAKETOUCH_MULTITOUCH_JAZZHAND = "android.hardware.faketouch.multitouch.jazzhand"` |  |
| `static final String FEATURE_FINGERPRINT = "android.hardware.fingerprint"` |  |
| `static final String FEATURE_FREEFORM_WINDOW_MANAGEMENT = "android.software.freeform_window_management"` |  |
| `static final String FEATURE_GAMEPAD = "android.hardware.gamepad"` |  |
| `static final String FEATURE_HIFI_SENSORS = "android.hardware.sensor.hifi_sensors"` |  |
| `static final String FEATURE_HOME_SCREEN = "android.software.home_screen"` |  |
| `static final String FEATURE_INPUT_METHODS = "android.software.input_methods"` |  |
| `static final String FEATURE_IPSEC_TUNNELS = "android.software.ipsec_tunnels"` |  |
| `static final String FEATURE_IRIS = "android.hardware.biometrics.iris"` |  |
| `static final String FEATURE_LEANBACK = "android.software.leanback"` |  |
| `static final String FEATURE_LEANBACK_ONLY = "android.software.leanback_only"` |  |
| `static final String FEATURE_LIVE_TV = "android.software.live_tv"` |  |
| `static final String FEATURE_LIVE_WALLPAPER = "android.software.live_wallpaper"` |  |
| `static final String FEATURE_LOCATION = "android.hardware.location"` |  |
| `static final String FEATURE_LOCATION_GPS = "android.hardware.location.gps"` |  |
| `static final String FEATURE_LOCATION_NETWORK = "android.hardware.location.network"` |  |
| `static final String FEATURE_MANAGED_USERS = "android.software.managed_users"` |  |
| `static final String FEATURE_MICROPHONE = "android.hardware.microphone"` |  |
| `static final String FEATURE_MIDI = "android.software.midi"` |  |
| `static final String FEATURE_NFC = "android.hardware.nfc"` |  |
| `static final String FEATURE_NFC_BEAM = "android.sofware.nfc.beam"` |  |
| `static final String FEATURE_NFC_HOST_CARD_EMULATION = "android.hardware.nfc.hce"` |  |
| `static final String FEATURE_NFC_HOST_CARD_EMULATION_NFCF = "android.hardware.nfc.hcef"` |  |
| `static final String FEATURE_NFC_OFF_HOST_CARD_EMULATION_ESE = "android.hardware.nfc.ese"` |  |
| `static final String FEATURE_NFC_OFF_HOST_CARD_EMULATION_UICC = "android.hardware.nfc.uicc"` |  |
| `static final String FEATURE_OPENGLES_EXTENSION_PACK = "android.hardware.opengles.aep"` |  |
| `static final String FEATURE_PC = "android.hardware.type.pc"` |  |
| `static final String FEATURE_PICTURE_IN_PICTURE = "android.software.picture_in_picture"` |  |
| `static final String FEATURE_PRINTING = "android.software.print"` |  |
| `static final String FEATURE_RAM_LOW = "android.hardware.ram.low"` |  |
| `static final String FEATURE_RAM_NORMAL = "android.hardware.ram.normal"` |  |
| `static final String FEATURE_SCREEN_LANDSCAPE = "android.hardware.screen.landscape"` |  |
| `static final String FEATURE_SCREEN_PORTRAIT = "android.hardware.screen.portrait"` |  |
| `static final String FEATURE_SECURELY_REMOVES_USERS = "android.software.securely_removes_users"` |  |
| `static final String FEATURE_SECURE_LOCK_SCREEN = "android.software.secure_lock_screen"` |  |
| `static final String FEATURE_SENSOR_ACCELEROMETER = "android.hardware.sensor.accelerometer"` |  |
| `static final String FEATURE_SENSOR_AMBIENT_TEMPERATURE = "android.hardware.sensor.ambient_temperature"` |  |
| `static final String FEATURE_SENSOR_BAROMETER = "android.hardware.sensor.barometer"` |  |
| `static final String FEATURE_SENSOR_COMPASS = "android.hardware.sensor.compass"` |  |
| `static final String FEATURE_SENSOR_GYROSCOPE = "android.hardware.sensor.gyroscope"` |  |
| `static final String FEATURE_SENSOR_HEART_RATE = "android.hardware.sensor.heartrate"` |  |
| `static final String FEATURE_SENSOR_HEART_RATE_ECG = "android.hardware.sensor.heartrate.ecg"` |  |
| `static final String FEATURE_SENSOR_HINGE_ANGLE = "android.hardware.sensor.hinge_angle"` |  |
| `static final String FEATURE_SENSOR_LIGHT = "android.hardware.sensor.light"` |  |
| `static final String FEATURE_SENSOR_PROXIMITY = "android.hardware.sensor.proximity"` |  |
| `static final String FEATURE_SENSOR_RELATIVE_HUMIDITY = "android.hardware.sensor.relative_humidity"` |  |
| `static final String FEATURE_SENSOR_STEP_COUNTER = "android.hardware.sensor.stepcounter"` |  |
| `static final String FEATURE_SENSOR_STEP_DETECTOR = "android.hardware.sensor.stepdetector"` |  |
| `static final String FEATURE_SE_OMAPI_ESE = "android.hardware.se.omapi.ese"` |  |
| `static final String FEATURE_SE_OMAPI_SD = "android.hardware.se.omapi.sd"` |  |
| `static final String FEATURE_SE_OMAPI_UICC = "android.hardware.se.omapi.uicc"` |  |
| `static final String FEATURE_SIP = "android.software.sip"` |  |
| `static final String FEATURE_SIP_VOIP = "android.software.sip.voip"` |  |
| `static final String FEATURE_STRONGBOX_KEYSTORE = "android.hardware.strongbox_keystore"` |  |
| `static final String FEATURE_TELEPHONY = "android.hardware.telephony"` |  |
| `static final String FEATURE_TELEPHONY_CDMA = "android.hardware.telephony.cdma"` |  |
| `static final String FEATURE_TELEPHONY_EUICC = "android.hardware.telephony.euicc"` |  |
| `static final String FEATURE_TELEPHONY_GSM = "android.hardware.telephony.gsm"` |  |
| `static final String FEATURE_TELEPHONY_IMS = "android.hardware.telephony.ims"` |  |
| `static final String FEATURE_TELEPHONY_MBMS = "android.hardware.telephony.mbms"` |  |
| `static final String FEATURE_TOUCHSCREEN = "android.hardware.touchscreen"` |  |
| `static final String FEATURE_TOUCHSCREEN_MULTITOUCH = "android.hardware.touchscreen.multitouch"` |  |
| `static final String FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT = "android.hardware.touchscreen.multitouch.distinct"` |  |
| `static final String FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND = "android.hardware.touchscreen.multitouch.jazzhand"` |  |
| `static final String FEATURE_USB_ACCESSORY = "android.hardware.usb.accessory"` |  |
| `static final String FEATURE_USB_HOST = "android.hardware.usb.host"` |  |
| `static final String FEATURE_VERIFIED_BOOT = "android.software.verified_boot"` |  |
| `static final String FEATURE_VR_HEADTRACKING = "android.hardware.vr.headtracking"` |  |
| `static final String FEATURE_VR_MODE_HIGH_PERFORMANCE = "android.hardware.vr.high_performance"` |  |
| `static final String FEATURE_VULKAN_DEQP_LEVEL = "android.software.vulkan.deqp.level"` |  |
| `static final String FEATURE_VULKAN_HARDWARE_COMPUTE = "android.hardware.vulkan.compute"` |  |
| `static final String FEATURE_VULKAN_HARDWARE_LEVEL = "android.hardware.vulkan.level"` |  |
| `static final String FEATURE_VULKAN_HARDWARE_VERSION = "android.hardware.vulkan.version"` |  |
| `static final String FEATURE_WATCH = "android.hardware.type.watch"` |  |
| `static final String FEATURE_WEBVIEW = "android.software.webview"` |  |
| `static final String FEATURE_WIFI = "android.hardware.wifi"` |  |
| `static final String FEATURE_WIFI_AWARE = "android.hardware.wifi.aware"` |  |
| `static final String FEATURE_WIFI_DIRECT = "android.hardware.wifi.direct"` |  |
| `static final String FEATURE_WIFI_PASSPOINT = "android.hardware.wifi.passpoint"` |  |
| `static final String FEATURE_WIFI_RTT = "android.hardware.wifi.rtt"` |  |
| `static final int FLAG_PERMISSION_WHITELIST_INSTALLER = 2` |  |
| `static final int FLAG_PERMISSION_WHITELIST_SYSTEM = 1` |  |
| `static final int FLAG_PERMISSION_WHITELIST_UPGRADE = 4` |  |
| `static final int GET_ACTIVITIES = 1` |  |
| `static final int GET_CONFIGURATIONS = 16384` |  |
| `static final int GET_GIDS = 256` |  |
| `static final int GET_INSTRUMENTATION = 16` |  |
| `static final int GET_INTENT_FILTERS = 32` |  |
| `static final int GET_META_DATA = 128` |  |
| `static final int GET_PERMISSIONS = 4096` |  |
| `static final int GET_PROVIDERS = 8` |  |
| `static final int GET_RECEIVERS = 2` |  |
| `static final int GET_RESOLVED_FILTER = 64` |  |
| `static final int GET_SERVICES = 4` |  |
| `static final int GET_SHARED_LIBRARY_FILES = 1024` |  |
| `static final int GET_SIGNING_CERTIFICATES = 134217728` |  |
| `static final int GET_URI_PERMISSION_PATTERNS = 2048` |  |
| `static final int INSTALL_REASON_DEVICE_RESTORE = 2` |  |
| `static final int INSTALL_REASON_DEVICE_SETUP = 3` |  |
| `static final int INSTALL_REASON_POLICY = 1` |  |
| `static final int INSTALL_REASON_UNKNOWN = 0` |  |
| `static final int INSTALL_REASON_USER = 4` |  |
| `static final int MATCH_ALL = 131072` |  |
| `static final int MATCH_APEX = 1073741824` |  |
| `static final int MATCH_DEFAULT_ONLY = 65536` |  |
| `static final int MATCH_DIRECT_BOOT_AUTO = 268435456` |  |
| `static final int MATCH_DIRECT_BOOT_AWARE = 524288` |  |
| `static final int MATCH_DIRECT_BOOT_UNAWARE = 262144` |  |
| `static final int MATCH_DISABLED_COMPONENTS = 512` |  |
| `static final int MATCH_DISABLED_UNTIL_USED_COMPONENTS = 32768` |  |
| `static final int MATCH_SYSTEM_ONLY = 1048576` |  |
| `static final int MATCH_UNINSTALLED_PACKAGES = 8192` |  |
| `static final long MAXIMUM_VERIFICATION_TIMEOUT = 3600000L` |  |
| `static final int PERMISSION_DENIED = -1` |  |
| `static final int PERMISSION_GRANTED = 0` |  |
| `static final int SIGNATURE_FIRST_NOT_SIGNED = -1` |  |
| `static final int SIGNATURE_MATCH = 0` |  |
| `static final int SIGNATURE_NEITHER_SIGNED = 1` |  |
| `static final int SIGNATURE_NO_MATCH = -3` |  |
| `static final int SIGNATURE_SECOND_NOT_SIGNED = -2` |  |
| `static final int SIGNATURE_UNKNOWN_PACKAGE = -4` |  |
| `static final int SYNCHRONOUS = 2` |  |
| `static final int VERIFICATION_ALLOW = 1` |  |
| `static final int VERIFICATION_REJECT = -1` |  |
| `static final int VERSION_CODE_HIGHEST = -1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `abstract boolean addPermission(@NonNull android.content.pm.PermissionInfo)` |  |
| `abstract boolean addPermissionAsync(@NonNull android.content.pm.PermissionInfo)` |  |
| `abstract boolean canRequestPackageInstalls()` |  |
| `abstract String[] canonicalToCurrentPackageNames(@NonNull String[])` |  |
| `abstract void clearInstantAppCookie()` |  |
| `abstract String[] currentToCanonicalPackageNames(@NonNull String[])` |  |
| `abstract void extendVerificationTimeout(int, int, long)` |  |
| `abstract int getApplicationEnabledSetting(@NonNull String)` |  |
| `abstract int getComponentEnabledSetting(@NonNull android.content.ComponentName)` |  |
| `abstract int getInstantAppCookieMaxBytes()` |  |
| `abstract int[] getPackageGids(@NonNull String) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `abstract int[] getPackageGids(@NonNull String, int) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `abstract android.content.pm.PackageInfo getPackageInfo(@NonNull String, int) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `abstract android.content.pm.PackageInfo getPackageInfo(@NonNull android.content.pm.VersionedPackage, int) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `abstract int getPackageUid(@NonNull String, int) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `abstract android.content.pm.PermissionInfo getPermissionInfo(@NonNull String, int) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `boolean getSyntheticAppDetailsActivityEnabled(@NonNull String)` |  |
| `boolean hasSigningCertificate(@NonNull String, @NonNull byte[], int)` |  |
| `boolean hasSigningCertificate(int, @NonNull byte[], int)` |  |
| `abstract boolean hasSystemFeature(@NonNull String)` |  |
| `abstract boolean hasSystemFeature(@NonNull String, int)` |  |
| `boolean isAutoRevokeWhitelisted()` |  |
| `boolean isDefaultApplicationIcon(@NonNull android.graphics.drawable.Drawable)` |  |
| `boolean isDeviceUpgrading()` |  |
| `abstract boolean isInstantApp()` |  |
| `abstract boolean isInstantApp(@NonNull String)` |  |
| `boolean isPackageSuspended(@NonNull String) throws android.content.pm.PackageManager.NameNotFoundException` |  |
| `boolean isPackageSuspended()` |  |
| `abstract boolean isSafeMode()` |  |
| `abstract void removePermission(@NonNull String)` |  |
| `abstract void setApplicationCategoryHint(@NonNull String, int)` |  |
| `abstract void setInstallerPackageName(@NonNull String, @Nullable String)` |  |
| `void setMimeGroup(@NonNull String, @NonNull java.util.Set<java.lang.String>)` |  |
| `abstract void updateInstantAppCookie(@Nullable byte[])` |  |
| `abstract void verifyPendingInstall(int, int)` |  |

---

### `class static PackageManager.NameNotFoundException`

- **继承：** `android.util.AndroidException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PackageManager.NameNotFoundException()` |  |
| `PackageManager.NameNotFoundException(String)` |  |

---

### `class PackageStats` ~~DEPRECATED~~

- **实现：** `android.os.Parcelable`
- **Annotations:** `@Deprecated`

---

### `class PathPermission`

- **继承：** `android.os.PatternMatcher`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `PathPermission(String, int, String, String)` |  |
| `PathPermission(android.os.Parcel)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `String getReadPermission()` |  |
| `String getWritePermission()` |  |

---

### `class PermissionGroupInfo`

- **继承：** `android.content.pm.PackageItemInfo`
- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_PERSONAL_INFO = 1` |  |
| `int flags` |  |
| `int priority` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |

---

### `class PermissionInfo`

- **继承：** `android.content.pm.PackageItemInfo`
- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_COSTS_MONEY = 1` |  |
| `static final int FLAG_HARD_RESTRICTED = 4` |  |
| `static final int FLAG_IMMUTABLY_RESTRICTED = 16` |  |
| `static final int FLAG_INSTALLED = 1073741824` |  |
| `static final int FLAG_SOFT_RESTRICTED = 8` |  |
| `static final int PROTECTION_DANGEROUS = 1` |  |
| `static final int PROTECTION_FLAG_APPOP = 64` |  |
| `static final int PROTECTION_FLAG_DEVELOPMENT = 32` |  |
| `static final int PROTECTION_FLAG_INSTALLER = 256` |  |
| `static final int PROTECTION_FLAG_INSTANT = 4096` |  |
| `static final int PROTECTION_FLAG_PRE23 = 128` |  |
| `static final int PROTECTION_FLAG_PREINSTALLED = 1024` |  |
| `static final int PROTECTION_FLAG_PRIVILEGED = 16` |  |
| `static final int PROTECTION_FLAG_RUNTIME_ONLY = 8192` |  |
| `static final int PROTECTION_FLAG_SETUP = 2048` |  |
| `static final int PROTECTION_FLAG_VERIFIER = 512` |  |
| `static final int PROTECTION_NORMAL = 0` |  |
| `static final int PROTECTION_SIGNATURE = 2` |  |
| `int flags` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getProtection()` |  |
| `int getProtectionFlags()` |  |

---

### `class final ProviderInfo`

- **继承：** `android.content.pm.ComponentInfo`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ProviderInfo()` |  |
| `ProviderInfo(android.content.pm.ProviderInfo)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_SINGLE_USER = 1073741824` |  |
| `String authority` |  |
| `int flags` |  |
| `boolean forceUriPermissions` |  |
| `boolean grantUriPermissions` |  |
| `int initOrder` |  |
| `boolean multiprocess` |  |
| `android.content.pm.PathPermission[] pathPermissions` |  |
| `String readPermission` |  |
| `android.os.PatternMatcher[] uriPermissionPatterns` |  |
| `String writePermission` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |

---

### `class ResolveInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ResolveInfo()` |  |
| `ResolveInfo(android.content.pm.ResolveInfo)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.content.pm.ActivityInfo activityInfo` |  |
| `android.content.IntentFilter filter` |  |
| `int icon` |  |
| `boolean isDefault` |  |
| `boolean isInstantAppAvailable` |  |
| `int labelRes` |  |
| `int match` |  |
| `CharSequence nonLocalizedLabel` |  |
| `int preferredOrder` |  |
| `int priority` |  |
| `android.content.pm.ProviderInfo providerInfo` |  |
| `String resolvePackageName` |  |
| `android.content.pm.ServiceInfo serviceInfo` |  |
| `int specificIndex` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `final int getIconResource()` |  |
| `boolean isCrossProfileIntentForwarderActivity()` |  |
| `android.graphics.drawable.Drawable loadIcon(android.content.pm.PackageManager)` |  |
| `CharSequence loadLabel(android.content.pm.PackageManager)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ResolveInfo.DisplayNameComparator`

- **实现：** `java.util.Comparator<android.content.pm.ResolveInfo>`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ResolveInfo.DisplayNameComparator(android.content.pm.PackageManager)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `final int compare(android.content.pm.ResolveInfo, android.content.pm.ResolveInfo)` |  |

---

### `class ServiceInfo`

- **继承：** `android.content.pm.ComponentInfo`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ServiceInfo()` |  |
| `ServiceInfo(android.content.pm.ServiceInfo)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_EXTERNAL_SERVICE = 4` |  |
| `static final int FLAG_ISOLATED_PROCESS = 2` |  |
| `static final int FLAG_SINGLE_USER = 1073741824` |  |
| `static final int FLAG_STOP_WITH_TASK = 1` |  |
| `static final int FLAG_USE_APP_ZYGOTE = 8` |  |
| `static final int FOREGROUND_SERVICE_TYPE_CAMERA = 64` |  |
| `static final int FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE = 16` |  |
| `static final int FOREGROUND_SERVICE_TYPE_DATA_SYNC = 1` |  |
| `static final int FOREGROUND_SERVICE_TYPE_LOCATION = 8` |  |
| `static final int FOREGROUND_SERVICE_TYPE_MANIFEST = -1` |  |
| `static final int FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK = 2` |  |
| `static final int FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION = 32` |  |
| `static final int FOREGROUND_SERVICE_TYPE_MICROPHONE = 128` |  |
| `static final int FOREGROUND_SERVICE_TYPE_NONE = 0` |  |
| `static final int FOREGROUND_SERVICE_TYPE_PHONE_CALL = 4` |  |
| `int flags` |  |
| `String permission` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void dump(android.util.Printer, String)` |  |
| `int getForegroundServiceType()` |  |

---

### `class final SharedLibraryInfo`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int TYPE_BUILTIN = 0` |  |
| `static final int TYPE_DYNAMIC = 1` |  |
| `static final int TYPE_STATIC = 2` |  |
| `static final int VERSION_UNDEFINED = -1` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getName()` |  |
| `int getType()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final ShortcutInfo`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int DISABLED_REASON_APP_CHANGED = 2` |  |
| `static final int DISABLED_REASON_BACKUP_NOT_SUPPORTED = 101` |  |
| `static final int DISABLED_REASON_BY_APP = 1` |  |
| `static final int DISABLED_REASON_NOT_DISABLED = 0` |  |
| `static final int DISABLED_REASON_OTHER_RESTORE_ISSUE = 103` |  |
| `static final int DISABLED_REASON_SIGNATURE_MISMATCH = 102` |  |
| `static final int DISABLED_REASON_UNKNOWN = 3` |  |
| `static final int DISABLED_REASON_VERSION_LOWER = 100` |  |
| `static final String SHORTCUT_CATEGORY_CONVERSATION = "android.shortcut.conversation"` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDisabledReason()` |  |
| `long getLastChangedTimestamp()` |  |
| `int getRank()` |  |
| `android.os.UserHandle getUserHandle()` |  |
| `boolean hasKeyFieldsOnly()` |  |
| `boolean isCached()` |  |
| `boolean isDeclaredInManifest()` |  |
| `boolean isDynamic()` |  |
| `boolean isEnabled()` |  |
| `boolean isImmutable()` |  |
| `boolean isPinned()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ShortcutInfo.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ShortcutInfo.Builder(android.content.Context, String)` |  |

---

### `class ShortcutManager`


#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FLAG_MATCH_CACHED = 8` |  |
| `static final int FLAG_MATCH_DYNAMIC = 2` |  |
| `static final int FLAG_MATCH_MANIFEST = 1` |  |
| `static final int FLAG_MATCH_PINNED = 4` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean addDynamicShortcuts(@NonNull java.util.List<android.content.pm.ShortcutInfo>)` |  |
| `android.content.Intent createShortcutResultIntent(@NonNull android.content.pm.ShortcutInfo)` |  |
| `void disableShortcuts(@NonNull java.util.List<java.lang.String>)` |  |
| `void disableShortcuts(@NonNull java.util.List<java.lang.String>, CharSequence)` |  |
| `void enableShortcuts(@NonNull java.util.List<java.lang.String>)` |  |
| `int getIconMaxHeight()` |  |
| `int getIconMaxWidth()` |  |
| `int getMaxShortcutCountPerActivity()` |  |
| `boolean isRateLimitingActive()` |  |
| `boolean isRequestPinShortcutSupported()` |  |
| `void pushDynamicShortcut(@NonNull android.content.pm.ShortcutInfo)` |  |
| `void removeAllDynamicShortcuts()` |  |
| `void removeDynamicShortcuts(@NonNull java.util.List<java.lang.String>)` |  |
| `void removeLongLivedShortcuts(@NonNull java.util.List<java.lang.String>)` |  |
| `void reportShortcutUsed(String)` |  |
| `boolean requestPinShortcut(@NonNull android.content.pm.ShortcutInfo, @Nullable android.content.IntentSender)` |  |
| `boolean setDynamicShortcuts(@NonNull java.util.List<android.content.pm.ShortcutInfo>)` |  |
| `boolean updateShortcuts(@NonNull java.util.List<android.content.pm.ShortcutInfo>)` |  |

---

### `class Signature`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Signature(byte[])` |  |
| `Signature(String)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `byte[] toByteArray()` |  |
| `char[] toChars()` |  |
| `char[] toChars(char[], int[])` |  |
| `String toCharsString()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final SigningInfo`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SigningInfo()` |  |
| `SigningInfo(android.content.pm.SigningInfo)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.content.pm.Signature[] getApkContentsSigners()` |  |
| `android.content.pm.Signature[] getSigningCertificateHistory()` |  |
| `boolean hasMultipleSigners()` |  |
| `boolean hasPastSigningCertificates()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final VersionedPackage`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `VersionedPackage(@NonNull String, int)` |  |
| `VersionedPackage(@NonNull String, long)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getLongVersionCode()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## 包： `android.content.res`

### `class AssetFileDescriptor`

- **实现：** `java.io.Closeable android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AssetFileDescriptor(android.os.ParcelFileDescriptor, long, long)` |  |
| `AssetFileDescriptor(android.os.ParcelFileDescriptor, long, long, android.os.Bundle)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final long UNKNOWN_LENGTH = -1L` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `java.io.FileInputStream createInputStream() throws java.io.IOException` |  |
| `java.io.FileOutputStream createOutputStream() throws java.io.IOException` |  |
| `int describeContents()` |  |
| `long getDeclaredLength()` |  |
| `android.os.Bundle getExtras()` |  |
| `java.io.FileDescriptor getFileDescriptor()` |  |
| `long getLength()` |  |
| `android.os.ParcelFileDescriptor getParcelFileDescriptor()` |  |
| `long getStartOffset()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static AssetFileDescriptor.AutoCloseInputStream`

- **继承：** `android.os.ParcelFileDescriptor.AutoCloseInputStream`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AssetFileDescriptor.AutoCloseInputStream(android.content.res.AssetFileDescriptor) throws java.io.IOException` |  |

---

### `class static AssetFileDescriptor.AutoCloseOutputStream`

- **继承：** `android.os.ParcelFileDescriptor.AutoCloseOutputStream`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AssetFileDescriptor.AutoCloseOutputStream(android.content.res.AssetFileDescriptor) throws java.io.IOException` |  |

---

### `class final AssetManager`

- **实现：** `java.lang.AutoCloseable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int ACCESS_BUFFER = 3` |  |
| `static final int ACCESS_RANDOM = 1` |  |
| `static final int ACCESS_STREAMING = 2` |  |
| `static final int ACCESS_UNKNOWN = 0` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close()` |  |
| `String[] getLocales()` |  |

---

### `class final AssetManager.AssetInputStream`

- **继承：** `java.io.InputStream`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int read() throws java.io.IOException` |  |

---

### `class ColorStateList`

- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ColorStateList(int[][], @ColorInt int[])` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getChangingConfigurations()` |  |
| `int getColorForState(@Nullable int[], int)` |  |
| `boolean isOpaque()` |  |
| `boolean isStateful()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final Configuration`

- **实现：** `java.lang.Comparable<android.content.res.Configuration> android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Configuration()` |  |
| `Configuration(android.content.res.Configuration)` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int COLOR_MODE_HDR_MASK = 12` |  |
| `static final int COLOR_MODE_HDR_NO = 4` |  |
| `static final int COLOR_MODE_HDR_SHIFT = 2` |  |
| `static final int COLOR_MODE_HDR_UNDEFINED = 0` |  |
| `static final int COLOR_MODE_HDR_YES = 8` |  |
| `static final int COLOR_MODE_UNDEFINED = 0` |  |
| `static final int COLOR_MODE_WIDE_COLOR_GAMUT_MASK = 3` |  |
| `static final int COLOR_MODE_WIDE_COLOR_GAMUT_NO = 1` |  |
| `static final int COLOR_MODE_WIDE_COLOR_GAMUT_UNDEFINED = 0` |  |
| `static final int COLOR_MODE_WIDE_COLOR_GAMUT_YES = 2` |  |
| `static final int DENSITY_DPI_UNDEFINED = 0` |  |
| `static final int HARDKEYBOARDHIDDEN_NO = 1` |  |
| `static final int HARDKEYBOARDHIDDEN_UNDEFINED = 0` |  |
| `static final int HARDKEYBOARDHIDDEN_YES = 2` |  |
| `static final int KEYBOARDHIDDEN_NO = 1` |  |
| `static final int KEYBOARDHIDDEN_UNDEFINED = 0` |  |
| `static final int KEYBOARDHIDDEN_YES = 2` |  |
| `static final int KEYBOARD_12KEY = 3` |  |
| `static final int KEYBOARD_NOKEYS = 1` |  |
| `static final int KEYBOARD_QWERTY = 2` |  |
| `static final int KEYBOARD_UNDEFINED = 0` |  |
| `static final int MNC_ZERO = 65535` |  |
| `static final int NAVIGATIONHIDDEN_NO = 1` |  |
| `static final int NAVIGATIONHIDDEN_UNDEFINED = 0` |  |
| `static final int NAVIGATIONHIDDEN_YES = 2` |  |
| `static final int NAVIGATION_DPAD = 2` |  |
| `static final int NAVIGATION_NONAV = 1` |  |
| `static final int NAVIGATION_TRACKBALL = 3` |  |
| `static final int NAVIGATION_UNDEFINED = 0` |  |
| `static final int NAVIGATION_WHEEL = 4` |  |
| `static final int ORIENTATION_LANDSCAPE = 2` |  |
| `static final int ORIENTATION_PORTRAIT = 1` |  |
| `static final int ORIENTATION_UNDEFINED = 0` |  |
| `static final int SCREENLAYOUT_LAYOUTDIR_LTR = 64` |  |
| `static final int SCREENLAYOUT_LAYOUTDIR_MASK = 192` |  |
| `static final int SCREENLAYOUT_LAYOUTDIR_RTL = 128` |  |
| `static final int SCREENLAYOUT_LAYOUTDIR_SHIFT = 6` |  |
| `static final int SCREENLAYOUT_LAYOUTDIR_UNDEFINED = 0` |  |
| `static final int SCREENLAYOUT_LONG_MASK = 48` |  |
| `static final int SCREENLAYOUT_LONG_NO = 16` |  |
| `static final int SCREENLAYOUT_LONG_UNDEFINED = 0` |  |
| `static final int SCREENLAYOUT_LONG_YES = 32` |  |
| `static final int SCREENLAYOUT_ROUND_MASK = 768` |  |
| `static final int SCREENLAYOUT_ROUND_NO = 256` |  |
| `static final int SCREENLAYOUT_ROUND_UNDEFINED = 0` |  |
| `static final int SCREENLAYOUT_ROUND_YES = 512` |  |
| `static final int SCREENLAYOUT_SIZE_LARGE = 3` |  |
| `static final int SCREENLAYOUT_SIZE_MASK = 15` |  |
| `static final int SCREENLAYOUT_SIZE_NORMAL = 2` |  |
| `static final int SCREENLAYOUT_SIZE_SMALL = 1` |  |
| `static final int SCREENLAYOUT_SIZE_UNDEFINED = 0` |  |
| `static final int SCREENLAYOUT_SIZE_XLARGE = 4` |  |
| `static final int SCREENLAYOUT_UNDEFINED = 0` |  |
| `static final int SCREEN_HEIGHT_DP_UNDEFINED = 0` |  |
| `static final int SCREEN_WIDTH_DP_UNDEFINED = 0` |  |
| `static final int SMALLEST_SCREEN_WIDTH_DP_UNDEFINED = 0` |  |
| `static final int TOUCHSCREEN_FINGER = 3` |  |
| `static final int TOUCHSCREEN_NOTOUCH = 1` |  |
| `static final int TOUCHSCREEN_UNDEFINED = 0` |  |
| `static final int UI_MODE_NIGHT_MASK = 48` |  |
| `static final int UI_MODE_NIGHT_NO = 16` |  |
| `static final int UI_MODE_NIGHT_UNDEFINED = 0` |  |
| `static final int UI_MODE_NIGHT_YES = 32` |  |
| `static final int UI_MODE_TYPE_APPLIANCE = 5` |  |
| `static final int UI_MODE_TYPE_CAR = 3` |  |
| `static final int UI_MODE_TYPE_DESK = 2` |  |
| `static final int UI_MODE_TYPE_MASK = 15` |  |
| `static final int UI_MODE_TYPE_NORMAL = 1` |  |
| `static final int UI_MODE_TYPE_TELEVISION = 4` |  |
| `static final int UI_MODE_TYPE_UNDEFINED = 0` |  |
| `static final int UI_MODE_TYPE_VR_HEADSET = 7` |  |
| `static final int UI_MODE_TYPE_WATCH = 6` |  |
| `int colorMode` |  |
| `int densityDpi` |  |
| `float fontScale` |  |
| `int hardKeyboardHidden` |  |
| `int keyboard` |  |
| `int keyboardHidden` |  |
| `int mcc` |  |
| `int mnc` |  |
| `int navigation` |  |
| `int navigationHidden` |  |
| `int orientation` |  |
| `int screenHeightDp` |  |
| `int screenLayout` |  |
| `int screenWidthDp` |  |
| `int smallestScreenWidthDp` |  |
| `int touchscreen` |  |
| `int uiMode` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int compareTo(android.content.res.Configuration)` |  |
| `int describeContents()` |  |
| `int diff(android.content.res.Configuration)` |  |
| `boolean equals(android.content.res.Configuration)` |  |
| `int getLayoutDirection()` |  |
| `boolean isLayoutSizeAtLeast(int)` |  |
| `boolean isNightModeActive()` |  |
| `boolean isScreenHdr()` |  |
| `boolean isScreenRound()` |  |
| `boolean isScreenWideColorGamut()` |  |
| `static boolean needNewResources(int, int)` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void setLayoutDirection(java.util.Locale)` |  |
| `void setLocale(@Nullable java.util.Locale)` |  |
| `void setLocales(@Nullable android.os.LocaleList)` |  |
| `void setTo(android.content.res.Configuration)` |  |
| `void setToDefaults()` |  |
| `int updateFrom(@NonNull android.content.res.Configuration)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ObbInfo`

- **实现：** `android.os.Parcelable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int OBB_OVERLAY = 1` |  |
| `String filename` |  |
| `int flags` |  |
| `String packageName` |  |
| `int version` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class ObbScanner`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static android.content.res.ObbInfo getObbInfo(String) throws java.io.IOException` |  |

---

### `class Resources`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addLoaders(@NonNull android.content.res.loader.ResourcesLoader...)` |  |
| `final void finishPreloading()` |  |
| `final void flushLayoutCache()` |  |
| `final android.content.res.AssetManager getAssets()` |  |
| `boolean getBoolean(@BoolRes int) throws android.content.res.Resources.NotFoundException` |  |
| `android.content.res.Configuration getConfiguration()` |  |
| `float getDimension(@DimenRes int) throws android.content.res.Resources.NotFoundException` |  |
| `int getDimensionPixelOffset(@DimenRes int) throws android.content.res.Resources.NotFoundException` |  |
| `int getDimensionPixelSize(@DimenRes int) throws android.content.res.Resources.NotFoundException` |  |
| `android.util.DisplayMetrics getDisplayMetrics()` |  |
| `android.graphics.drawable.Drawable getDrawable(@DrawableRes int, @Nullable android.content.res.Resources.Theme) throws android.content.res.Resources.NotFoundException` |  |
| `float getFloat(@DimenRes int)` |  |
| `float getFraction(@FractionRes int, int, int)` |  |
| `int getIdentifier(String, String, String)` |  |
| `int getInteger(@IntegerRes int) throws android.content.res.Resources.NotFoundException` |  |
| `String getResourceEntryName(@AnyRes int) throws android.content.res.Resources.NotFoundException` |  |
| `String getResourceName(@AnyRes int) throws android.content.res.Resources.NotFoundException` |  |
| `String getResourcePackageName(@AnyRes int) throws android.content.res.Resources.NotFoundException` |  |
| `String getResourceTypeName(@AnyRes int) throws android.content.res.Resources.NotFoundException` |  |
| `static android.content.res.Resources getSystem()` |  |
| `CharSequence getText(@StringRes int, CharSequence)` |  |
| `void getValue(@AnyRes int, android.util.TypedValue, boolean) throws android.content.res.Resources.NotFoundException` |  |
| `void getValue(String, android.util.TypedValue, boolean) throws android.content.res.Resources.NotFoundException` |  |
| `void getValueForDensity(@AnyRes int, int, android.util.TypedValue, boolean) throws android.content.res.Resources.NotFoundException` |  |
| `final android.content.res.Resources.Theme newTheme()` |  |
| `android.content.res.TypedArray obtainAttributes(android.util.AttributeSet, @StyleableRes int[])` |  |
| `android.content.res.AssetFileDescriptor openRawResourceFd(@RawRes int) throws android.content.res.Resources.NotFoundException` |  |
| `void parseBundleExtra(String, android.util.AttributeSet, android.os.Bundle) throws org.xmlpull.v1.XmlPullParserException` |  |
| `void parseBundleExtras(android.content.res.XmlResourceParser, android.os.Bundle) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException` |  |
| `void removeLoaders(@NonNull android.content.res.loader.ResourcesLoader...)` |  |

---

### `class static Resources.NotFoundException`

- **继承：** `java.lang.RuntimeException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Resources.NotFoundException()` |  |
| `Resources.NotFoundException(String)` |  |
| `Resources.NotFoundException(String, Exception)` |  |

---

### `class final Resources.Theme`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void applyStyle(int, boolean)` |  |
| `void dump(int, String, String)` |  |
| `int getChangingConfigurations()` |  |
| `android.graphics.drawable.Drawable getDrawable(@DrawableRes int) throws android.content.res.Resources.NotFoundException` |  |
| `android.content.res.Resources getResources()` |  |
| `void rebase()` |  |
| `boolean resolveAttribute(int, android.util.TypedValue, boolean)` |  |
| `void setTo(android.content.res.Resources.Theme)` |  |

---

### `class TypedArray`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean getBoolean(@StyleableRes int, boolean)` |  |
| `int getChangingConfigurations()` |  |
| `float getDimension(@StyleableRes int, float)` |  |
| `int getDimensionPixelOffset(@StyleableRes int, int)` |  |
| `int getDimensionPixelSize(@StyleableRes int, int)` |  |
| `float getFloat(@StyleableRes int, float)` |  |
| `float getFraction(@StyleableRes int, int, int, float)` |  |
| `int getIndex(int)` |  |
| `int getIndexCount()` |  |
| `int getInt(@StyleableRes int, int)` |  |
| `int getInteger(@StyleableRes int, int)` |  |
| `int getLayoutDimension(@StyleableRes int, String)` |  |
| `int getLayoutDimension(@StyleableRes int, int)` |  |
| `String getNonResourceString(@StyleableRes int)` |  |
| `String getPositionDescription()` |  |
| `android.content.res.Resources getResources()` |  |
| `CharSequence getText(@StyleableRes int)` |  |
| `CharSequence[] getTextArray(@StyleableRes int)` |  |
| `int getType(@StyleableRes int)` |  |
| `boolean getValue(@StyleableRes int, android.util.TypedValue)` |  |
| `boolean hasValue(@StyleableRes int)` |  |
| `boolean hasValueOrEmpty(@StyleableRes int)` |  |
| `int length()` |  |
| `android.util.TypedValue peekValue(@StyleableRes int)` |  |
| `void recycle()` |  |

---

### `interface XmlResourceParser`

- **继承：** `org.xmlpull.v1.XmlPullParser android.util.AttributeSet java.lang.AutoCloseable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close()` |  |

---

## 包： `android.content.res.loader`

### `interface AssetsProvider`


---

### `class ResourcesLoader`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ResourcesLoader()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addProvider(@NonNull android.content.res.loader.ResourcesProvider)` |  |
| `void clearProviders()` |  |
| `void removeProvider(@NonNull android.content.res.loader.ResourcesProvider)` |  |
| `void setProviders(@NonNull java.util.List<android.content.res.loader.ResourcesProvider>)` |  |

---

### `class ResourcesProvider`

- **实现：** `java.lang.AutoCloseable java.io.Closeable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close()` |  |

---

## 包： `android.database`

### `class abstract AbstractCursor`

- **实现：** `android.database.CrossProcessCursor`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AbstractCursor()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void checkPosition()` |  |
| `void close()` |  |
| `void copyStringToBuffer(int, android.database.CharArrayBuffer)` |  |
| `void deactivate()` |  |
| `void fillWindow(int, android.database.CursorWindow)` |  |
| `void finalize()` |  |
| `byte[] getBlob(int)` |  |
| `int getColumnCount()` |  |
| `int getColumnIndex(String)` |  |
| `int getColumnIndexOrThrow(String)` |  |
| `String getColumnName(int)` |  |
| `android.os.Bundle getExtras()` |  |
| `android.net.Uri getNotificationUri()` |  |
| `final int getPosition()` |  |
| `int getType(int)` |  |
| `boolean getWantsAllOnMoveCalls()` |  |
| `android.database.CursorWindow getWindow()` |  |
| `final boolean isAfterLast()` |  |
| `final boolean isBeforeFirst()` |  |
| `boolean isClosed()` |  |
| `final boolean isFirst()` |  |
| `final boolean isLast()` |  |
| `final boolean move(int)` |  |
| `final boolean moveToFirst()` |  |
| `final boolean moveToLast()` |  |
| `final boolean moveToNext()` |  |
| `final boolean moveToPosition(int)` |  |
| `final boolean moveToPrevious()` |  |
| `void onChange(boolean)` |  |
| `boolean onMove(int, int)` |  |
| `void registerContentObserver(android.database.ContentObserver)` |  |
| `void registerDataSetObserver(android.database.DataSetObserver)` |  |
| `boolean requery()` |  |
| `android.os.Bundle respond(android.os.Bundle)` |  |
| `void setExtras(android.os.Bundle)` |  |
| `void setNotificationUri(android.content.ContentResolver, android.net.Uri)` |  |
| `void unregisterContentObserver(android.database.ContentObserver)` |  |
| `void unregisterDataSetObserver(android.database.DataSetObserver)` |  |

---

### `class static AbstractCursor.SelfContentObserver`

- **继承：** `android.database.ContentObserver`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AbstractCursor.SelfContentObserver(android.database.AbstractCursor)` |  |

---

### `class abstract AbstractWindowedCursor`

- **继承：** `android.database.AbstractCursor`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `AbstractWindowedCursor()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.database.CursorWindow mWindow` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `double getDouble(int)` |  |
| `float getFloat(int)` |  |
| `int getInt(int)` |  |
| `long getLong(int)` |  |
| `short getShort(int)` |  |
| `String getString(int)` |  |
| `boolean hasWindow()` |  |
| `boolean isNull(int)` |  |
| `void setWindow(android.database.CursorWindow)` |  |

---

### `class final CharArrayBuffer`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `CharArrayBuffer(int)` |  |
| `CharArrayBuffer(char[])` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `char[] data` |  |
| `int sizeCopied` |  |

---

### `class ContentObservable`

- **继承：** `android.database.Observable<android.database.ContentObserver>`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ContentObservable()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void dispatchChange(boolean, android.net.Uri)` |  |
| `void registerObserver(android.database.ContentObserver)` |  |

---

### `class abstract ContentObserver`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `ContentObserver(android.os.Handler)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean deliverSelfNotifications()` |  |
| `final void dispatchChange(boolean, @Nullable android.net.Uri)` |  |
| `final void dispatchChange(boolean, @Nullable android.net.Uri, int)` |  |
| `final void dispatchChange(boolean, @NonNull java.util.Collection<android.net.Uri>, int)` |  |
| `void onChange(boolean)` |  |
| `void onChange(boolean, @Nullable android.net.Uri)` |  |
| `void onChange(boolean, @Nullable android.net.Uri, int)` |  |
| `void onChange(boolean, @NonNull java.util.Collection<android.net.Uri>, int)` |  |

---

### `interface CrossProcessCursor`

- **继承：** `android.database.Cursor`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void fillWindow(int, android.database.CursorWindow)` |  |
| `android.database.CursorWindow getWindow()` |  |
| `boolean onMove(int, int)` |  |

---

### `class CrossProcessCursorWrapper`

- **继承：** `android.database.CursorWrapper`
- **实现：** `android.database.CrossProcessCursor`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `CrossProcessCursorWrapper(android.database.Cursor)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void fillWindow(int, android.database.CursorWindow)` |  |
| `android.database.CursorWindow getWindow()` |  |
| `boolean onMove(int, int)` |  |

---

### `interface Cursor`

- **继承：** `java.io.Closeable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int FIELD_TYPE_BLOB = 4` |  |
| `static final int FIELD_TYPE_FLOAT = 2` |  |
| `static final int FIELD_TYPE_INTEGER = 1` |  |
| `static final int FIELD_TYPE_NULL = 0` |  |
| `static final int FIELD_TYPE_STRING = 3` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close()` |  |
| `void copyStringToBuffer(int, android.database.CharArrayBuffer)` |  |
| `byte[] getBlob(int)` |  |
| `int getColumnCount()` |  |
| `int getColumnIndex(String)` |  |
| `int getColumnIndexOrThrow(String) throws java.lang.IllegalArgumentException` |  |
| `String getColumnName(int)` |  |
| `String[] getColumnNames()` |  |
| `int getCount()` |  |
| `double getDouble(int)` |  |
| `android.os.Bundle getExtras()` |  |
| `float getFloat(int)` |  |
| `int getInt(int)` |  |
| `long getLong(int)` |  |
| `android.net.Uri getNotificationUri()` |  |
| `int getPosition()` |  |
| `short getShort(int)` |  |
| `String getString(int)` |  |
| `int getType(int)` |  |
| `boolean getWantsAllOnMoveCalls()` |  |
| `boolean isAfterLast()` |  |
| `boolean isBeforeFirst()` |  |
| `boolean isClosed()` |  |
| `boolean isFirst()` |  |
| `boolean isLast()` |  |
| `boolean isNull(int)` |  |
| `boolean move(int)` |  |
| `boolean moveToFirst()` |  |
| `boolean moveToLast()` |  |
| `boolean moveToNext()` |  |
| `boolean moveToPosition(int)` |  |
| `boolean moveToPrevious()` |  |
| `void registerContentObserver(android.database.ContentObserver)` |  |
| `void registerDataSetObserver(android.database.DataSetObserver)` |  |
| `android.os.Bundle respond(android.os.Bundle)` |  |
| `void setExtras(android.os.Bundle)` |  |
| `void setNotificationUri(android.content.ContentResolver, android.net.Uri)` |  |
| `default void setNotificationUris(@NonNull android.content.ContentResolver, @NonNull java.util.List<android.net.Uri>)` |  |
| `void unregisterContentObserver(android.database.ContentObserver)` |  |
| `void unregisterDataSetObserver(android.database.DataSetObserver)` |  |

---

### `class CursorIndexOutOfBoundsException`

- **继承：** `java.lang.IndexOutOfBoundsException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `CursorIndexOutOfBoundsException(int, int)` |  |
| `CursorIndexOutOfBoundsException(String)` |  |

---

### `class final CursorJoiner`

- **实现：** `java.lang.Iterable<android.database.CursorJoiner.Result> java.util.Iterator<android.database.CursorJoiner.Result>`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `CursorJoiner(android.database.Cursor, String[], android.database.Cursor, String[])` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean hasNext()` |  |
| `java.util.Iterator<android.database.CursorJoiner.Result> iterator()` |  |
| `android.database.CursorJoiner.Result next()` |  |

---

### `enum CursorJoiner.Result`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.database.CursorJoiner.Result BOTH` |  |
| `static final android.database.CursorJoiner.Result LEFT` |  |
| `static final android.database.CursorJoiner.Result RIGHT` |  |

---

### `class CursorWindow`

- **继承：** `android.database.sqlite.SQLiteClosable`
- **实现：** `android.os.Parcelable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `CursorWindow(String)` |  |
| `CursorWindow(String, long)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `boolean allocRow()` |  |
| `void clear()` |  |
| `void copyStringToBuffer(int, int, android.database.CharArrayBuffer)` |  |
| `int describeContents()` |  |
| `void freeLastRow()` |  |
| `byte[] getBlob(int, int)` |  |
| `double getDouble(int, int)` |  |
| `float getFloat(int, int)` |  |
| `int getInt(int, int)` |  |
| `long getLong(int, int)` |  |
| `int getNumRows()` |  |
| `short getShort(int, int)` |  |
| `int getStartPosition()` |  |
| `String getString(int, int)` |  |
| `int getType(int, int)` |  |
| `static android.database.CursorWindow newFromParcel(android.os.Parcel)` |  |
| `void onAllReferencesReleased()` |  |
| `boolean putBlob(byte[], int, int)` |  |
| `boolean putDouble(double, int, int)` |  |
| `boolean putLong(long, int, int)` |  |
| `boolean putNull(int, int)` |  |
| `boolean putString(String, int, int)` |  |
| `boolean setNumColumns(int)` |  |
| `void setStartPosition(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class CursorWrapper`

- **实现：** `android.database.Cursor`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `CursorWrapper(android.database.Cursor)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close()` |  |
| `void copyStringToBuffer(int, android.database.CharArrayBuffer)` |  |
| `byte[] getBlob(int)` |  |
| `int getColumnCount()` |  |
| `int getColumnIndex(String)` |  |
| `int getColumnIndexOrThrow(String) throws java.lang.IllegalArgumentException` |  |
| `String getColumnName(int)` |  |
| `String[] getColumnNames()` |  |
| `int getCount()` |  |
| `double getDouble(int)` |  |
| `android.os.Bundle getExtras()` |  |
| `float getFloat(int)` |  |
| `int getInt(int)` |  |
| `long getLong(int)` |  |
| `android.net.Uri getNotificationUri()` |  |
| `int getPosition()` |  |
| `short getShort(int)` |  |
| `String getString(int)` |  |
| `int getType(int)` |  |
| `boolean getWantsAllOnMoveCalls()` |  |
| `android.database.Cursor getWrappedCursor()` |  |
| `boolean isAfterLast()` |  |
| `boolean isBeforeFirst()` |  |
| `boolean isClosed()` |  |
| `boolean isFirst()` |  |
| `boolean isLast()` |  |
| `boolean isNull(int)` |  |
| `boolean move(int)` |  |
| `boolean moveToFirst()` |  |
| `boolean moveToLast()` |  |
| `boolean moveToNext()` |  |
| `boolean moveToPosition(int)` |  |
| `boolean moveToPrevious()` |  |
| `void registerContentObserver(android.database.ContentObserver)` |  |
| `void registerDataSetObserver(android.database.DataSetObserver)` |  |
| `android.os.Bundle respond(android.os.Bundle)` |  |
| `void setExtras(android.os.Bundle)` |  |
| `void setNotificationUri(android.content.ContentResolver, android.net.Uri)` |  |
| `void unregisterContentObserver(android.database.ContentObserver)` |  |
| `void unregisterDataSetObserver(android.database.DataSetObserver)` |  |

---

### `class DataSetObservable`

- **继承：** `android.database.Observable<android.database.DataSetObserver>`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DataSetObservable()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void notifyChanged()` |  |
| `void notifyInvalidated()` |  |

---

### `class abstract DataSetObserver`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DataSetObserver()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onChanged()` |  |
| `void onInvalidated()` |  |

---

### `interface DatabaseErrorHandler`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onCorruption(android.database.sqlite.SQLiteDatabase)` |  |

---

### `class DatabaseUtils`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DatabaseUtils()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int STATEMENT_ABORT = 6` |  |
| `static final int STATEMENT_ATTACH = 3` |  |
| `static final int STATEMENT_BEGIN = 4` |  |
| `static final int STATEMENT_COMMIT = 5` |  |
| `static final int STATEMENT_DDL = 8` |  |
| `static final int STATEMENT_OTHER = 99` |  |
| `static final int STATEMENT_PRAGMA = 7` |  |
| `static final int STATEMENT_SELECT = 1` |  |
| `static final int STATEMENT_UNPREPARED = 9` |  |
| `static final int STATEMENT_UPDATE = 2` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static void appendEscapedSQLString(StringBuilder, String)` |  |
| `static String[] appendSelectionArgs(String[], String[])` |  |
| `static final void appendValueToSql(StringBuilder, Object)` |  |
| `static void bindObjectToProgram(android.database.sqlite.SQLiteProgram, int, Object)` |  |
| `static android.os.ParcelFileDescriptor blobFileDescriptorForQuery(android.database.sqlite.SQLiteDatabase, String, String[])` |  |
| `static android.os.ParcelFileDescriptor blobFileDescriptorForQuery(android.database.sqlite.SQLiteStatement, String[])` |  |
| `static String concatenateWhere(String, String)` |  |
| `static void createDbFromSqlStatements(android.content.Context, String, int, String)` |  |
| `static void cursorDoubleToContentValues(android.database.Cursor, String, android.content.ContentValues, String)` |  |
| `static void cursorDoubleToContentValuesIfPresent(android.database.Cursor, android.content.ContentValues, String)` |  |
| `static void cursorDoubleToCursorValues(android.database.Cursor, String, android.content.ContentValues)` |  |
| `static void cursorFloatToContentValuesIfPresent(android.database.Cursor, android.content.ContentValues, String)` |  |
| `static void cursorIntToContentValues(android.database.Cursor, String, android.content.ContentValues)` |  |
| `static void cursorIntToContentValues(android.database.Cursor, String, android.content.ContentValues, String)` |  |
| `static void cursorIntToContentValuesIfPresent(android.database.Cursor, android.content.ContentValues, String)` |  |
| `static void cursorLongToContentValues(android.database.Cursor, String, android.content.ContentValues)` |  |
| `static void cursorLongToContentValues(android.database.Cursor, String, android.content.ContentValues, String)` |  |
| `static void cursorLongToContentValuesIfPresent(android.database.Cursor, android.content.ContentValues, String)` |  |
| `static void cursorRowToContentValues(android.database.Cursor, android.content.ContentValues)` |  |
| `static void cursorShortToContentValuesIfPresent(android.database.Cursor, android.content.ContentValues, String)` |  |
| `static void cursorStringToContentValues(android.database.Cursor, String, android.content.ContentValues)` |  |
| `static void cursorStringToContentValues(android.database.Cursor, String, android.content.ContentValues, String)` |  |
| `static void cursorStringToContentValuesIfPresent(android.database.Cursor, android.content.ContentValues, String)` |  |
| `static void cursorStringToInsertHelper(android.database.Cursor, String, android.database.DatabaseUtils.InsertHelper, int)` |  |
| `static void dumpCurrentRow(android.database.Cursor)` |  |
| `static void dumpCurrentRow(android.database.Cursor, java.io.PrintStream)` |  |
| `static void dumpCurrentRow(android.database.Cursor, StringBuilder)` |  |
| `static String dumpCurrentRowToString(android.database.Cursor)` |  |
| `static void dumpCursor(android.database.Cursor)` |  |
| `static void dumpCursor(android.database.Cursor, java.io.PrintStream)` |  |
| `static void dumpCursor(android.database.Cursor, StringBuilder)` |  |
| `static String dumpCursorToString(android.database.Cursor)` |  |
| `static String getCollationKey(String)` |  |
| `static String getHexCollationKey(String)` |  |
| `static int getSqlStatementType(String)` |  |
| `static long longForQuery(android.database.sqlite.SQLiteDatabase, String, String[])` |  |
| `static long longForQuery(android.database.sqlite.SQLiteStatement, String[])` |  |
| `static long queryNumEntries(android.database.sqlite.SQLiteDatabase, String)` |  |
| `static long queryNumEntries(android.database.sqlite.SQLiteDatabase, String, String)` |  |
| `static long queryNumEntries(android.database.sqlite.SQLiteDatabase, String, String, String[])` |  |
| `static final void readExceptionFromParcel(android.os.Parcel)` |  |
| `static void readExceptionWithFileNotFoundExceptionFromParcel(android.os.Parcel) throws java.io.FileNotFoundException` |  |
| `static void readExceptionWithOperationApplicationExceptionFromParcel(android.os.Parcel) throws android.content.OperationApplicationException` |  |
| `static String sqlEscapeString(String)` |  |
| `static String stringForQuery(android.database.sqlite.SQLiteDatabase, String, String[])` |  |
| `static String stringForQuery(android.database.sqlite.SQLiteStatement, String[])` |  |
| `static final void writeExceptionToParcel(android.os.Parcel, Exception)` |  |

---

### `class static DatabaseUtils.InsertHelper` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final DefaultDatabaseErrorHandler`

- **实现：** `android.database.DatabaseErrorHandler`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `DefaultDatabaseErrorHandler()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onCorruption(android.database.sqlite.SQLiteDatabase)` |  |

---

### `class MatrixCursor`

- **继承：** `android.database.AbstractCursor`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `MatrixCursor(String[], int)` |  |
| `MatrixCursor(String[])` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void addRow(Object[])` |  |
| `void addRow(Iterable<?>)` |  |
| `String[] getColumnNames()` |  |
| `int getCount()` |  |
| `double getDouble(int)` |  |
| `float getFloat(int)` |  |
| `int getInt(int)` |  |
| `long getLong(int)` |  |
| `short getShort(int)` |  |
| `String getString(int)` |  |
| `boolean isNull(int)` |  |
| `android.database.MatrixCursor.RowBuilder newRow()` |  |

---

### `class MatrixCursor.RowBuilder`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.database.MatrixCursor.RowBuilder add(Object)` |  |
| `android.database.MatrixCursor.RowBuilder add(String, Object)` |  |

---

### `class MergeCursor`

- **继承：** `android.database.AbstractCursor`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `MergeCursor(android.database.Cursor[])` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `String[] getColumnNames()` |  |
| `int getCount()` |  |
| `double getDouble(int)` |  |
| `float getFloat(int)` |  |
| `int getInt(int)` |  |
| `long getLong(int)` |  |
| `short getShort(int)` |  |
| `String getString(int)` |  |
| `boolean isNull(int)` |  |

---

### `class abstract Observable<T>`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `Observable()` |  |

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `final java.util.ArrayList<T> mObservers` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void registerObserver(T)` |  |
| `void unregisterAll()` |  |
| `void unregisterObserver(T)` |  |

---

### `class SQLException`

- **继承：** `java.lang.RuntimeException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLException()` |  |
| `SQLException(String)` |  |
| `SQLException(String, Throwable)` |  |

---

### `class StaleDataException`

- **继承：** `java.lang.RuntimeException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `StaleDataException()` |  |
| `StaleDataException(String)` |  |

---

## 包： `android.database.sqlite`

### `class SQLiteAbortException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteAbortException()` |  |
| `SQLiteAbortException(String)` |  |

---

### `class SQLiteAccessPermException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteAccessPermException()` |  |
| `SQLiteAccessPermException(String)` |  |

---

### `class SQLiteBindOrColumnIndexOutOfRangeException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteBindOrColumnIndexOutOfRangeException()` |  |
| `SQLiteBindOrColumnIndexOutOfRangeException(String)` |  |

---

### `class SQLiteBlobTooBigException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteBlobTooBigException()` |  |
| `SQLiteBlobTooBigException(String)` |  |

---

### `class SQLiteCantOpenDatabaseException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteCantOpenDatabaseException()` |  |
| `SQLiteCantOpenDatabaseException(String)` |  |

---

### `class abstract SQLiteClosable`

- **实现：** `java.io.Closeable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteClosable()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void acquireReference()` |  |
| `void close()` |  |
| `abstract void onAllReferencesReleased()` |  |
| `void releaseReference()` |  |

---

### `class SQLiteConstraintException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteConstraintException()` |  |
| `SQLiteConstraintException(String)` |  |

---

### `class SQLiteCursor`

- **继承：** `android.database.AbstractWindowedCursor`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteCursor(android.database.sqlite.SQLiteCursorDriver, String, android.database.sqlite.SQLiteQuery)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `String[] getColumnNames()` |  |
| `int getCount()` |  |
| `android.database.sqlite.SQLiteDatabase getDatabase()` |  |
| `void setFillWindowForwardOnly(boolean)` |  |
| `void setSelectionArguments(String[])` |  |

---

### `interface SQLiteCursorDriver`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void cursorClosed()` |  |
| `void cursorDeactivated()` |  |
| `void cursorRequeried(android.database.Cursor)` |  |
| `android.database.Cursor query(android.database.sqlite.SQLiteDatabase.CursorFactory, String[])` |  |
| `void setBindArguments(String[])` |  |

---

### `class final SQLiteDatabase`

- **继承：** `android.database.sqlite.SQLiteClosable`

#### 字段 / 常量

| 签名 | 已弃用 |
|-----------|:----------:|
| `static final int CONFLICT_ABORT = 2` |  |
| `static final int CONFLICT_FAIL = 3` |  |
| `static final int CONFLICT_IGNORE = 4` |  |
| `static final int CONFLICT_NONE = 0` |  |
| `static final int CONFLICT_REPLACE = 5` |  |
| `static final int CONFLICT_ROLLBACK = 1` |  |
| `static final int CREATE_IF_NECESSARY = 268435456` |  |
| `static final int ENABLE_WRITE_AHEAD_LOGGING = 536870912` |  |
| `static final int MAX_SQL_CACHE_SIZE = 100` |  |
| `static final int NO_LOCALIZED_COLLATORS = 16` |  |
| `static final int OPEN_READONLY = 1` |  |
| `static final int OPEN_READWRITE = 0` |  |
| `static final int SQLITE_MAX_LIKE_PATTERN_LENGTH = 50000` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void beginTransaction()` |  |
| `void beginTransactionNonExclusive()` |  |
| `void beginTransactionWithListener(android.database.sqlite.SQLiteTransactionListener)` |  |
| `void beginTransactionWithListenerNonExclusive(android.database.sqlite.SQLiteTransactionListener)` |  |
| `android.database.sqlite.SQLiteStatement compileStatement(String) throws android.database.SQLException` |  |
| `int delete(String, String, String[])` |  |
| `static boolean deleteDatabase(@NonNull java.io.File)` |  |
| `void disableWriteAheadLogging()` |  |
| `boolean enableWriteAheadLogging()` |  |
| `void endTransaction()` |  |
| `void execPerConnectionSQL(@NonNull String, @Nullable Object[]) throws android.database.SQLException` |  |
| `void execSQL(String) throws android.database.SQLException` |  |
| `void execSQL(String, Object[]) throws android.database.SQLException` |  |
| `static String findEditTable(String)` |  |
| `java.util.List<android.util.Pair<java.lang.String,java.lang.String>> getAttachedDbs()` |  |
| `long getMaximumSize()` |  |
| `long getPageSize()` |  |
| `String getPath()` |  |
| `int getVersion()` |  |
| `boolean inTransaction()` |  |
| `long insert(String, String, android.content.ContentValues)` |  |
| `long insertOrThrow(String, String, android.content.ContentValues) throws android.database.SQLException` |  |
| `long insertWithOnConflict(String, String, android.content.ContentValues, int)` |  |
| `boolean isDatabaseIntegrityOk()` |  |
| `boolean isDbLockedByCurrentThread()` |  |
| `boolean isOpen()` |  |
| `boolean isReadOnly()` |  |
| `boolean isWriteAheadLoggingEnabled()` |  |
| `boolean needUpgrade(int)` |  |
| `void onAllReferencesReleased()` |  |
| `static android.database.sqlite.SQLiteDatabase openDatabase(@NonNull String, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory, int)` |  |
| `static android.database.sqlite.SQLiteDatabase openDatabase(@NonNull java.io.File, @NonNull android.database.sqlite.SQLiteDatabase.OpenParams)` |  |
| `static android.database.sqlite.SQLiteDatabase openDatabase(@NonNull String, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory, int, @Nullable android.database.DatabaseErrorHandler)` |  |
| `static android.database.sqlite.SQLiteDatabase openOrCreateDatabase(@NonNull java.io.File, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory)` |  |
| `static android.database.sqlite.SQLiteDatabase openOrCreateDatabase(@NonNull String, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory)` |  |
| `static android.database.sqlite.SQLiteDatabase openOrCreateDatabase(@NonNull String, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory, @Nullable android.database.DatabaseErrorHandler)` |  |
| `android.database.Cursor query(boolean, String, String[], String, String[], String, String, String, String)` |  |
| `android.database.Cursor query(boolean, String, String[], String, String[], String, String, String, String, android.os.CancellationSignal)` |  |
| `android.database.Cursor query(String, String[], String, String[], String, String, String)` |  |
| `android.database.Cursor query(String, String[], String, String[], String, String, String, String)` |  |
| `android.database.Cursor queryWithFactory(android.database.sqlite.SQLiteDatabase.CursorFactory, boolean, String, String[], String, String[], String, String, String, String)` |  |
| `android.database.Cursor queryWithFactory(android.database.sqlite.SQLiteDatabase.CursorFactory, boolean, String, String[], String, String[], String, String, String, String, android.os.CancellationSignal)` |  |
| `android.database.Cursor rawQuery(String, String[])` |  |
| `android.database.Cursor rawQuery(String, String[], android.os.CancellationSignal)` |  |
| `android.database.Cursor rawQueryWithFactory(android.database.sqlite.SQLiteDatabase.CursorFactory, String, String[], String)` |  |
| `android.database.Cursor rawQueryWithFactory(android.database.sqlite.SQLiteDatabase.CursorFactory, String, String[], String, android.os.CancellationSignal)` |  |
| `static int releaseMemory()` |  |
| `long replace(String, String, android.content.ContentValues)` |  |
| `long replaceOrThrow(String, String, android.content.ContentValues) throws android.database.SQLException` |  |
| `void setCustomAggregateFunction(@NonNull String, @NonNull java.util.function.BinaryOperator<java.lang.String>) throws android.database.sqlite.SQLiteException` |  |
| `void setCustomScalarFunction(@NonNull String, @NonNull java.util.function.UnaryOperator<java.lang.String>) throws android.database.sqlite.SQLiteException` |  |
| `void setForeignKeyConstraintsEnabled(boolean)` |  |
| `void setLocale(java.util.Locale)` |  |
| `void setMaxSqlCacheSize(int)` |  |
| `long setMaximumSize(long)` |  |
| `void setPageSize(long)` |  |
| `void setTransactionSuccessful()` |  |
| `void setVersion(int)` |  |
| `int update(String, android.content.ContentValues, String, String[])` |  |
| `int updateWithOnConflict(String, android.content.ContentValues, String, String[], int)` |  |
| `void validateSql(@NonNull String, @Nullable android.os.CancellationSignal)` |  |
| `boolean yieldIfContendedSafely()` |  |
| `boolean yieldIfContendedSafely(long)` |  |

---

### `interface static SQLiteDatabase.CursorFactory`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.database.Cursor newCursor(android.database.sqlite.SQLiteDatabase, android.database.sqlite.SQLiteCursorDriver, String, android.database.sqlite.SQLiteQuery)` |  |

---

### `class static final SQLiteDatabase.OpenParams`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `long getIdleConnectionTimeout()` |  |
| `int getOpenFlags()` |  |

---

### `class static final SQLiteDatabase.OpenParams.Builder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteDatabase.OpenParams.Builder()` |  |
| `SQLiteDatabase.OpenParams.Builder(android.database.sqlite.SQLiteDatabase.OpenParams)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `android.database.sqlite.SQLiteDatabase.OpenParams.Builder setLookasideConfig(@IntRange(from=0) int, @IntRange(from=0) int)` |  |

---

### `class SQLiteDatabaseCorruptException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteDatabaseCorruptException()` |  |
| `SQLiteDatabaseCorruptException(String)` |  |

---

### `class SQLiteDatabaseLockedException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteDatabaseLockedException()` |  |
| `SQLiteDatabaseLockedException(String)` |  |

---

### `class SQLiteDatatypeMismatchException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteDatatypeMismatchException()` |  |
| `SQLiteDatatypeMismatchException(String)` |  |

---

### `class SQLiteDiskIOException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteDiskIOException()` |  |
| `SQLiteDiskIOException(String)` |  |

---

### `class SQLiteDoneException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteDoneException()` |  |
| `SQLiteDoneException(String)` |  |

---

### `class SQLiteException`

- **继承：** `android.database.SQLException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteException()` |  |
| `SQLiteException(String)` |  |
| `SQLiteException(String, Throwable)` |  |

---

### `class SQLiteFullException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteFullException()` |  |
| `SQLiteFullException(String)` |  |

---

### `class SQLiteMisuseException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteMisuseException()` |  |
| `SQLiteMisuseException(String)` |  |

---

### `class abstract SQLiteOpenHelper`

- **实现：** `java.lang.AutoCloseable`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteOpenHelper(@Nullable android.content.Context, @Nullable String, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory, int)` |  |
| `SQLiteOpenHelper(@Nullable android.content.Context, @Nullable String, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory, int, @Nullable android.database.DatabaseErrorHandler)` |  |
| `SQLiteOpenHelper(@Nullable android.content.Context, @Nullable String, int, @NonNull android.database.sqlite.SQLiteDatabase.OpenParams)` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void close()` |  |
| `String getDatabaseName()` |  |
| `android.database.sqlite.SQLiteDatabase getReadableDatabase()` |  |
| `android.database.sqlite.SQLiteDatabase getWritableDatabase()` |  |
| `void onConfigure(android.database.sqlite.SQLiteDatabase)` |  |
| `abstract void onCreate(android.database.sqlite.SQLiteDatabase)` |  |
| `void onDowngrade(android.database.sqlite.SQLiteDatabase, int, int)` |  |
| `void onOpen(android.database.sqlite.SQLiteDatabase)` |  |
| `abstract void onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)` |  |
| `void setLookasideConfig(@IntRange(from=0) int, @IntRange(from=0) int)` |  |
| `void setOpenParams(@NonNull android.database.sqlite.SQLiteDatabase.OpenParams)` |  |
| `void setWriteAheadLoggingEnabled(boolean)` |  |

---

### `class SQLiteOutOfMemoryException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteOutOfMemoryException()` |  |
| `SQLiteOutOfMemoryException(String)` |  |

---

### `class abstract SQLiteProgram`

- **继承：** `android.database.sqlite.SQLiteClosable`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void bindAllArgsAsStrings(String[])` |  |
| `void bindBlob(int, byte[])` |  |
| `void bindDouble(int, double)` |  |
| `void bindLong(int, long)` |  |
| `void bindNull(int)` |  |
| `void bindString(int, String)` |  |
| `void clearBindings()` |  |
| `void onAllReferencesReleased()` |  |

---

### `class final SQLiteQuery`

- **继承：** `android.database.sqlite.SQLiteProgram`

---

### `class SQLiteQueryBuilder`


#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteQueryBuilder()` |  |

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `static void appendColumns(StringBuilder, String[])` |  |
| `void appendWhere(@NonNull CharSequence)` |  |
| `void appendWhereEscapeString(@NonNull String)` |  |
| `void appendWhereStandalone(@NonNull CharSequence)` |  |
| `String buildQuery(String[], String, String, String, String, String)` |  |
| `static String buildQueryString(boolean, String, String[], String, String, String, String, String)` |  |
| `String buildUnionQuery(String[], String, String)` |  |
| `String buildUnionSubQuery(String, String[], java.util.Set<java.lang.String>, int, String, String, String, String)` |  |
| `int delete(@NonNull android.database.sqlite.SQLiteDatabase, @Nullable String, @Nullable String[])` |  |
| `long insert(@NonNull android.database.sqlite.SQLiteDatabase, @NonNull android.content.ContentValues)` |  |
| `boolean isDistinct()` |  |
| `boolean isStrict()` |  |
| `boolean isStrictColumns()` |  |
| `boolean isStrictGrammar()` |  |
| `android.database.Cursor query(android.database.sqlite.SQLiteDatabase, String[], String, String[], String, String, String)` |  |
| `android.database.Cursor query(android.database.sqlite.SQLiteDatabase, String[], String, String[], String, String, String, String)` |  |
| `android.database.Cursor query(android.database.sqlite.SQLiteDatabase, String[], String, String[], String, String, String, String, android.os.CancellationSignal)` |  |
| `void setCursorFactory(@Nullable android.database.sqlite.SQLiteDatabase.CursorFactory)` |  |
| `void setDistinct(boolean)` |  |
| `void setProjectionGreylist(@Nullable java.util.Collection<java.util.regex.Pattern>)` |  |
| `void setProjectionMap(@Nullable java.util.Map<java.lang.String,java.lang.String>)` |  |
| `void setStrict(boolean)` |  |
| `void setStrictColumns(boolean)` |  |
| `void setStrictGrammar(boolean)` |  |
| `void setTables(@Nullable String)` |  |
| `int update(@NonNull android.database.sqlite.SQLiteDatabase, @NonNull android.content.ContentValues, @Nullable String, @Nullable String[])` |  |

---

### `class SQLiteReadOnlyDatabaseException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteReadOnlyDatabaseException()` |  |
| `SQLiteReadOnlyDatabaseException(String)` |  |

---

### `class final SQLiteStatement`

- **继承：** `android.database.sqlite.SQLiteProgram`

#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void execute()` |  |
| `long executeInsert()` |  |
| `int executeUpdateDelete()` |  |
| `android.os.ParcelFileDescriptor simpleQueryForBlobFileDescriptor()` |  |
| `long simpleQueryForLong()` |  |
| `String simpleQueryForString()` |  |

---

### `class SQLiteTableLockedException`

- **继承：** `android.database.sqlite.SQLiteException`

#### 构造函数

| 签名 | 已弃用 |
|-----------|:----------:|
| `SQLiteTableLockedException()` |  |
| `SQLiteTableLockedException(String)` |  |

---

### `interface SQLiteTransactionListener`


#### 方法

| 签名 | 已弃用 |
|-----------|:----------:|
| `void onBegin()` |  |
| `void onCommit()` |  |
| `void onRollback()` |  |

---

