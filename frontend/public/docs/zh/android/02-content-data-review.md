# Android 11 AOSP 代码审查：内容和数据 API

## 概要

本报告涵盖了构成 Android 应用开发数据模型骨干的内容框架、数据访问 API 和提供者契约。这些 API 包括 ContentProvider/ContentResolver 架构、基于 Intent 的消息系统、通过 SQLite 的数据库访问，以及联系人、日历和设置的系统提供者契约。

**关键统计数据：**
- **Context.java**：6,122 行，199 个 `@hide` 注解，36 个 `@SystemApi`，151 个服务常量
- **Intent.java**：11,451 行，223 个 `@hide`，85 个 `@SystemApi`，202 个 ACTION_ 常量，144 个 EXTRA_ 常量，45 个 FLAG_ 常量
- **ContentResolver.java**：4,136 行，93 个 `@hide`，5 个 `@SystemApi`
- **ContentProvider.java**：2,625 行，28 个 `@hide`，1 个 `@SystemApi`
- **Settings.java**：15,217 行，1,065 个 `@hide`，71 个 `@SystemApi`
- **ContactsContract.java**：9,747 行，55 个 `@hide`

---

## 1. Context.java —— Android 的上帝对象

**文件：** `frameworks/base/core/java/android/content/Context.java`
**行数：** 6,122
**类型：** 抽象类

### 1.1 用途和职责

Context 是 Android 应用开发中最重要的类。它提供了应用环境全局信息的接口，支持访问应用特定的资源、类和系统服务。每个 Activity、Service 和 Application 都扩展 Context（通过 ContextWrapper）。

### 1.2 关键公共 API

#### 文件模式常量（第 137-232 行）
```java
public static final int MODE_PRIVATE = 0x0000;           // 默认，仅应用访问
public static final int MODE_WORLD_READABLE = 0x0001;    // @Deprecated - N+ 上抛出 SecurityException
public static final int MODE_WORLD_WRITEABLE = 0x0002;   // @Deprecated - N+ 上抛出 SecurityException
public static final int MODE_APPEND = 0x8000;            // 追加到现有文件
public static final int MODE_MULTI_PROCESS = 0x0004;     // @Deprecated
public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 0x0008;
public static final int MODE_NO_LOCALIZED_COLLATORS = 0x0010;
```

#### 资源访问（第 508-660 行）
```java
public abstract AssetManager getAssets();
public abstract Resources getResources();
public abstract PackageManager getPackageManager();
public abstract ContentResolver getContentResolver();      // 第 527 行 - 内容提供者的网关
public abstract Looper getMainLooper();                    // 第 540 行
public Executor getMainExecutor();                        // 第 547 行 - 包装主 Looper
public abstract Context getApplicationContext();           // 第 577 行
```

#### SharedPreferences（第 898-949 行）
```java
public abstract SharedPreferences getSharedPreferences(String name, @PreferencesMode int mode);
public abstract SharedPreferences getSharedPreferences(File file, @PreferencesMode int mode);
public abstract boolean moveSharedPreferencesFrom(Context sourceContext, String name);
public abstract boolean deleteSharedPreferences(String name);
public abstract void reloadSharedPreferences();            // @hide
```

#### 文件和目录访问（第 965-1660 行）
```java
public abstract FileInputStream openFileInput(String name);
public abstract FileOutputStream openFileOutput(String name, @FileMode int mode);
public abstract File getFilesDir();
public abstract File getNoBackupFilesDir();                // 第 1118 行
public abstract File getExternalFilesDir(@Nullable String type); // 第 1206 行
public abstract File getCacheDir();                        // 第 1422 行
public abstract File getCodeCacheDir();                    // 第 1443 行
public abstract File getExternalCacheDir();                // 第 1500 行
public abstract File getDataDir();                         // 第 1054 行
```

#### 数据库访问（第 1674-1760 行）
```java
public abstract SQLiteDatabase openOrCreateDatabase(String name, @DatabaseMode int mode,
        CursorFactory factory);
public abstract SQLiteDatabase openOrCreateDatabase(String name, @DatabaseMode int mode,
        CursorFactory factory, @Nullable DatabaseErrorHandler errorHandler);
public abstract boolean deleteDatabase(String name);
public abstract File getDatabasePath(String name);
public abstract String[] databaseList();
```

#### Activity 启动（第 1828-1962 行）
```java
public abstract void startActivity(@RequiresPermission Intent intent);
public abstract void startActivity(@RequiresPermission Intent intent, @Nullable Bundle options);
public abstract void startActivities(@RequiresPermission Intent[] intents);
public abstract void startActivities(@RequiresPermission Intent[] intents, Bundle options);
```

#### 广播（第 2048-2818 行）
```java
public abstract void sendBroadcast(@RequiresPermission Intent intent);
public abstract void sendBroadcast(@RequiresPermission Intent intent, @Nullable String receiverPermission);
public abstract void sendOrderedBroadcast(@RequiresPermission Intent intent, @Nullable String receiverPermission);
public abstract void sendOrderedBroadcast(@RequiresPermission Intent intent,
        String receiverPermission, BroadcastReceiver resultReceiver,
        Handler scheduler, int initialCode, String initialData, Bundle initialExtras);
```

#### 接收器注册（第 2819-2998 行）
```java
public abstract Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter);
public abstract Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter, int flags);
public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
        @Nullable String broadcastPermission, @Nullable Handler scheduler);
public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
        @Nullable String broadcastPermission, @Nullable Handler scheduler, int flags);
public abstract void unregisterReceiver(BroadcastReceiver receiver);
```

#### 服务绑定（第 3058-3354 行）
```java
public abstract ComponentName startService(Intent service);
public abstract ComponentName startForegroundService(Intent service);  // 第 3078 行
public abstract boolean stopService(Intent service);
public abstract boolean bindService(@RequiresPermission Intent service,
        @NonNull ServiceConnection conn, @BindServiceFlags int flags);
public abstract void unbindService(@NonNull ServiceConnection conn);
```

#### 系统服务访问（第 3692-3720 行）
```java
public abstract @Nullable Object getSystemService(@ServiceName @NonNull String name);
public final <T> T getSystemService(@NonNull Class<T> serviceClass);     // 第 3700 行
public abstract @Nullable String getSystemServiceName(@NonNull Class<?> serviceClass);
```

#### 权限检查（第 5003-5245 行）
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

### 1.3 服务名称常量（共 151 个）

选定的重要常量（从第 3722 行开始）：
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

### 1.4 隐藏/系统 API

- **`@hide` 数量：199** —— 大量仅供内部使用的方法
- **`@SystemApi` 数量：36** —— 用于特权系统应用

值得注意的隐藏 API：
- `startActivityAsUser()`（第 1842 行）—— `@SystemApi`，需要 `INTERACT_ACROSS_USERS`
- `sendBroadcast(Intent, String, Bundle)`（第 2208 行）—— `@SystemApi`，带选项发送
- `registerReceiverForAllUsers()`（第 2953 行）—— `@SystemApi`
- `startServiceAsUser()`（第 3133 行）—— `@hide`
- `getSharedPrefsFile()`（第 870 行）—— `@hide`，`@Deprecated`
- `reloadSharedPreferences()`（第 949 行）—— `@hide`

### 1.5 设计模式

Context 使用**抽象工厂**模式。它是一个抽象类，具体实现 (`ContextImpl`) 由框架提供。`ContextWrapper` 实现了**装饰器**模式，允许将一个 Context 包装在另一个中（用于 Activity、Service、Application）。

### 1.6 安全考虑

- `MODE_WORLD_READABLE` / `MODE_WORLD_WRITEABLE` 自 API 17 起废弃，在 N+ 上抛出 `SecurityException`（第 143-177 行）
- `MODE_MULTI_PROCESS` 已废弃——不适用于跨进程 SharedPreferences（第 211 行）
- 通过 `checkPermission()` / `enforcePermission()` 系列进行权限强制
- URI 权限授予（`grantUriPermission()` / `revokeUriPermission()`）用于临时访问

---

## 2. ContentProvider.java —— 结构化数据共享

**文件：** `frameworks/base/core/java/android/content/ContentProvider.java`
**行数：** 2,625
**类型：** 抽象类，实现 `ContentInterface`、`ComponentCallbacks2`

### 2.1 用途和职责

ContentProvider 是应用间共享结构化数据的标准接口。它在基于 URI 的 API 后封装数据，提供 CRUD 操作和透明的跨进程访问。

### 2.2 关键抽象方法（必须实现）

```java
public abstract boolean onCreate();                                              // 第 1213 行
public abstract @Nullable Cursor query(@NonNull Uri uri, @Nullable String[] projection,
        @Nullable String selection, @Nullable String[] selectionArgs,
        @Nullable String sortOrder);                                             // 第 1305 行
public abstract @Nullable String getType(@NonNull Uri uri);                      // 第 1503 行
public abstract @Nullable Uri insert(@NonNull Uri uri, @Nullable ContentValues values); // 第 1648 行
public abstract int delete(@NonNull Uri uri, @Nullable String selection,
        @Nullable String[] selectionArgs);                                       // 第 1723 行
public abstract int update(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable String selection, @Nullable String[] selectionArgs);           // 第 1778 行
```

### 2.3 查询演进（3 个重载）

1. **原始版本**（第 1305 行）：`query(Uri, String[], String, String[], String)` —— 基于选择字符串
2. **带 CancellationSignal**（第 1376 行）：添加取消支持
3. **基于 Bundle**（第 1464 行）：`query(Uri, String[], Bundle, CancellationSignal)` —— API 26+ 首选，支持 `QUERY_ARG_SORT_COLUMNS`、`QUERY_ARG_OFFSET`、`QUERY_ARG_LIMIT`

基于 Bundle 的变体（第 1464-1482 行）自动回退到提取 SQL 风格的参数：
```java
// 提取 QUERY_ARG_SQL_SELECTION、QUERY_ARG_SQL_SELECTION_ARGS、QUERY_ARG_SQL_SORT_ORDER
// 如果 SQL 排序不存在，还会从 QUERY_ARG_SORT_COLUMNS 构建排序子句
```

### 2.4 带 Bundle extras 的插入（Android 11 新增）

```java
public @Nullable Uri insert(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable Bundle extras);                                                // 第 1671 行
public int delete(@NonNull Uri uri, @Nullable Bundle extras);                    // 第 1755 行
public int update(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable Bundle extras);                                                // 第 1804 行
```

### 2.5 文件操作

```java
public @Nullable ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode); // 第 1875 行
public @Nullable AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode); // 第 2002 行
public @Nullable AssetFileDescriptor openTypedAssetFile(@NonNull Uri uri,
        @NonNull String mimeTypeFilter, @Nullable Bundle opts);                  // 第 2182 行
public @Nullable String[] getStreamTypes(@NonNull Uri uri, @NonNull String mimeTypeFilter); // 第 2133 行
```

### 2.6 批量操作

```java
public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values);        // 第 1692 行
public @NonNull ContentProviderResult[] applyBatch(@NonNull String authority,
        @NonNull ArrayList<ContentProviderOperation> operations);                 // 第 2410 行
```

### 2.7 调用者身份（第 951-1057 行）

```java
public final @Nullable String getCallingPackage();           // 第 951 行
public final @Nullable String getCallingAttributionTag();    // 第 971 行
public final @NonNull CallingIdentity clearCallingIdentity();// 第 1046 行
public final void restoreCallingIdentity(@NonNull CallingIdentity identity); // 第 1057 行
```

### 2.8 权限配置

```java
public final @Nullable String getReadPermission();           // 第 1114 行
public final @Nullable String getWritePermission();          // 第 1136 行
public final @Nullable PathPermission[] getPathPermissions();// 第 1158 行
```

### 2.9 隐藏/系统 API

- `checkUriPermission(Uri, int, int)`（第 1614 行）—— `@SystemApi`，检查 UID 是否对 URI 有权限
- `rejectInsert(Uri, ContentValues)`（第 1626 行）—— `@hide`，插入被拒绝时调用
- `getIContentProvider()`（第 2336 行）—— `@hide`，返回底层 IPC Binder
- `attachInfoForTesting()`（第 2346 行）—— `@hide`，测试支持

### 2.10 设计模式：传输层

`Transport` 内部类（第 216 行）是处理跨进程调用的 IPC 桥接。它实现权限检查、调用包跟踪，并委托给 ContentProvider 的抽象方法。这是跨进程通信的**代理**模式。

### 2.11 线程安全

类文档明确警告（第 98-102 行）：
> 数据访问方法（如 insert 和 update）可能同时被多个线程调用，必须是线程安全的。其他方法（如 onCreate）只从应用主线程调用。

---

## 3. ContentResolver.java —— 客户端内容访问

**文件：** `frameworks/base/core/java/android/content/ContentResolver.java`
**行数：** 4,136
**类型：** 抽象类，实现 `ContentInterface`

### 3.1 用途和职责

ContentResolver 是访问 ContentProvider 的客户端 API。它处理 URI 路由、提供者获取（包括跨进程）、并为 CRUD 操作、文件访问和内容观察提供便捷方法。

### 3.2 查询参数（第 275-523 行）

API 26 引入的基于 Bundle 的查询系统：
```java
public static final String QUERY_ARG_SQL_SELECTION = "android:query-arg-sql-selection";       // 第 275 行
public static final String QUERY_ARG_SQL_SELECTION_ARGS = "android:query-arg-sql-selection-args"; // 第 293 行
public static final String QUERY_ARG_SQL_SORT_ORDER = "android:query-arg-sql-sort-order";     // 第 309 行
public static final String QUERY_ARG_SQL_GROUP_BY = "android:query-arg-sql-group-by";         // 第 321 行
public static final String QUERY_ARG_SQL_HAVING = "android:query-arg-sql-having";             // 第 331 行
public static final String QUERY_ARG_SQL_LIMIT = "android:query-arg-sql-limit";               // 第 344 行

// 结构化排序参数
public static final String QUERY_ARG_SORT_COLUMNS = "android:query-arg-sort-columns";        // 第 367 行
public static final String QUERY_ARG_SORT_DIRECTION = "android:query-arg-sort-direction";     // 第 388 行
public static final String QUERY_ARG_SORT_COLLATION = "android:query-arg-sort-collation";     // 第 414 行

// 分页
public static final String QUERY_ARG_OFFSET = "android:query-arg-offset";                    // 第 505 行
public static final String QUERY_ARG_LIMIT = "android:query-arg-limit";                      // 第 510 行
public static final String EXTRA_TOTAL_COUNT = "android.content.extra.TOTAL_COUNT";           // 第 523 行
```

### 3.3 CRUD 操作

```java
// 查询（3 个重载，全部为 final）
public final @Nullable Cursor query(Uri uri, String[] projection,
        String selection, String[] selectionArgs, String sortOrder);              // 第 1068 行
public final @Nullable Cursor query(Uri uri, String[] projection,
        String selection, String[] selectionArgs, String sortOrder,
        CancellationSignal cancellationSignal);                                  // 第 1110 行
public final @Nullable Cursor query(Uri uri, String[] projection,
        Bundle queryArgs, CancellationSignal cancellationSignal);                // 第 1154 行

// 插入
public final @Nullable Uri insert(@NonNull Uri url, @Nullable ContentValues values);         // 第 2109 行
public final @Nullable Uri insert(@NonNull Uri url, @Nullable ContentValues values,
        @Nullable Bundle extras);                                                // 第 2133 行

// 删除
public final int delete(@NonNull Uri url, @Nullable String where,
        @Nullable String[] selectionArgs);                                       // 第 2253 行
public final int delete(@NonNull Uri url, @Nullable Bundle extras);              // 第 2274 行

// 更新
public final int update(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable String where, @Nullable String[] selectionArgs);               // 第 2315 行
public final int update(@NonNull Uri uri, @Nullable ContentValues values,
        @Nullable Bundle extras);                                                // 第 2340 行
```

### 3.4 文件/流访问

```java
public final @Nullable InputStream openInputStream(@NonNull Uri uri);            // 第 1456 行
public final @Nullable OutputStream openOutputStream(@NonNull Uri uri);          // 第 1492 行
public final @Nullable OutputStream openOutputStream(@NonNull Uri uri, @NonNull String mode); // 第 1516 行
public final @Nullable ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode,
        @Nullable CancellationSignal signal);                                    // 第 1527 行
public final @Nullable ParcelFileDescriptor openFileDescriptor(@NonNull Uri uri,
        @NonNull String mode);                                                   // 第 1576 行
```

### 3.5 内容观察

```java
public final void registerContentObserver(@NonNull Uri uri,
        boolean notifyForDescendants, @NonNull ContentObserver observer);         // 第 2611 行
public final void unregisterContentObserver(@NonNull ContentObserver observer);   // 第 2640 行
public void notifyChange(@NonNull Uri uri, @Nullable ContentObserver observer);  // 第 2671 行
public void notifyChange(@NonNull Uri uri, @Nullable ContentObserver observer,
        @NotifyFlags int flags);                                                 // 第 2733 行
```

### 3.6 缩略图加载（Android 10+）

```java
public @NonNull Bitmap loadThumbnail(@NonNull Uri uri, @NonNull Size size,
        @Nullable CancellationSignal signal);                                    // 第 4021 行
```

### 3.7 数据列废弃（分区存储）

第 110-125 行揭示了 Android 11 分区存储的 `_data` 列废弃机制：
```java
public static final boolean DEPRECATE_DATA_COLUMNS = StorageManager.hasIsolatedStorage();  // @hide
public static final String DEPRECATE_DATA_PREFIX = "/mnt/content/";                        // @hide
```
这将直接文件路径替换为 `content://` URI，通过 `openFileDescriptor()` 重定向。

### 3.8 不稳定提供者恢复

查询实现（第 1167-1231 行）展示了一个关键的弹性模式：它首先尝试"不稳定"的提供者引用，如果远程进程死亡（`DeadObjectException`），则获取"稳定"引用并重试。第 1188 行的有趣注释如下：
> The remote process has died...  but we only hold an unstable reference though, so we might recover!!!  Let's try!!!!  This is exciting!!1!!1!!!!1

### 3.9 同步框架常量

```java
public static final String SYNC_EXTRAS_EXPEDITED = "expedited";         // 第 138 行
public static final String SYNC_EXTRAS_MANUAL = "force";                // 第 176 行
public static final String SYNC_EXTRAS_UPLOAD = "upload";               // 第 183 行
public static final String SYNC_EXTRAS_INITIALIZE = "initialize";       // 第 228 行
```

---

## 4. Intent.java —— 消息骨干

**文件：** `frameworks/base/core/java/android/content/Intent.java`
**行数：** 11,451
**类型：** 实现 `Parcelable`、`Cloneable` 的类

### 4.1 用途和职责

Intent 是 Android 中的基本消息对象，用于组件间的延迟运行时绑定。它描述要执行的操作：启动 Activity、发送广播、启动 Service。该类本质上是一个被动数据结构，包含 action、data URI、category、extras、component 和 flags。

### 4.2 规模

- **202 个 ACTION_ 常量** —— 标准 action（ACTION_VIEW、ACTION_EDIT、ACTION_SEND 等）
- **144 个 EXTRA_ 常量** —— 标准 extras 键
- **45 个 FLAG_ 常量** —— 控制行为的 Intent 标志
- **223 个 @hide 注解** —— 庞大的内部 API 表面

### 4.3 关键构造函数（第 6742-6884 行）

```java
public Intent();
public Intent(Intent o);                                           // 拷贝构造函数
public Intent(String action);
public Intent(String action, Uri uri);
public Intent(Context packageContext, Class<?> cls);                // 显式 Intent
public Intent(String action, Uri uri, Context packageContext, Class<?> cls);
```

### 4.4 核心 Getter/Setter

```java
// Action
public @Nullable String getAction();                               // 第 7848 行
public @NonNull Intent setAction(@Nullable String action);         // 第 8818 行

// 数据
public @Nullable Uri getData();                                    // 第 7863 行
public @NonNull Intent setData(@Nullable Uri data);                // 第 8844 行
public @Nullable String getType();                                 // 第 7901 行
public @NonNull Intent setType(@Nullable String type);             // 第 8901 行
public @NonNull Intent setDataAndType(@Nullable Uri data, @Nullable String type); // 第 8961 行

// 组件
public @NonNull Intent setComponent(@Nullable ComponentName component); // 显式 Intent
public @NonNull Intent setClass(@NonNull Context packageContext, @NonNull Class<?> cls);
public @NonNull Intent setPackage(@Nullable String packageName);

// 类别
public @NonNull Intent addCategory(String category);               // 第 9041 行

// 标志
public @NonNull Intent addFlags(@Flags int flags);
public @NonNull Intent setFlags(@Flags int flags);

// Extras（为每种基本类型 + Parcelable + Serializable 重载）
public @NonNull Intent putExtra(String name, boolean value);       // 第 9168 行
public @NonNull Intent putExtra(String name, int value);           // 第 9260 行
public @NonNull Intent putExtra(String name, String value);        // 第 9352 行
public @NonNull Intent putExtra(String name, Parcelable value);    // 第 9398 行
// ... 还有 20+ 个 putExtra 重载

public @Nullable Bundle getExtras();                               // 第 8575 行
```

### 4.5 工厂方法

```java
public static Intent createChooser(Intent target, CharSequence title);           // 第 987 行
public static Intent createChooser(Intent target, CharSequence title, IntentSender sender); // 第 1016 行
public static Intent makeMainActivity(ComponentName mainActivity);               // 第 6912 行
public static Intent makeMainSelectorActivity(String selectorAction, String selectorCategory); // 第 6941 行
public static Intent makeRestartActivityTask(ComponentName mainActivity);        // 第 6965 行
```

### 4.6 解析

```java
public ComponentName resolveActivity(@NonNull PackageManager pm);                // 第 8720 行
public ActivityInfo resolveActivityInfo(@NonNull PackageManager pm, int flags);  // 第 8752 行
```

### 4.7 URI 解析

```java
public static Intent parseUri(String uri, @UriFlags int flags);                  // 第 7001 行
public String toUri(@UriFlags int flags);
```

### 4.8 关键 ACTION_ 常量（选摘）

```
ACTION_MAIN, ACTION_VIEW, ACTION_DEFAULT, ACTION_ATTACH_DATA, ACTION_EDIT,
ACTION_INSERT_OR_EDIT, ACTION_PICK, ACTION_CREATE_SHORTCUT, ACTION_CHOOSER,
ACTION_GET_CONTENT, ACTION_DIAL, ACTION_CALL, ACTION_SEND, ACTION_SENDTO,
ACTION_SEND_MULTIPLE, ACTION_ANSWER, ACTION_INSERT, ACTION_DELETE,
ACTION_SEARCH, ACTION_WEB_SEARCH, ACTION_BOOT_COMPLETED, ACTION_POWER_CONNECTED,
ACTION_POWER_DISCONNECTED, ACTION_SHUTDOWN, ACTION_BATTERY_LOW, ACTION_BATTERY_OKAY,
ACTION_SCREEN_ON, ACTION_SCREEN_OFF, ACTION_USER_PRESENT
```

### 4.9 关键 FLAG_ 常量（选摘）

```
FLAG_ACTIVITY_NEW_TASK, FLAG_ACTIVITY_CLEAR_TOP, FLAG_ACTIVITY_SINGLE_TOP,
FLAG_ACTIVITY_NO_HISTORY, FLAG_ACTIVITY_CLEAR_TASK, FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
FLAG_GRANT_READ_URI_PERMISSION, FLAG_GRANT_WRITE_URI_PERMISSION,
FLAG_GRANT_PERSISTABLE_URI_PERMISSION, FLAG_GRANT_PREFIX_URI_PERMISSION,
FLAG_RECEIVER_FOREGROUND, FLAG_RECEIVER_REPLACE_PENDING
```

---

## 5. IntentFilter.java —— Intent 匹配

**文件：** `frameworks/base/core/java/android/content/IntentFilter.java`
**行数：** 2,475
**类型：** 实现 `Parcelable` 的类

### 5.1 用途

IntentFilter 声明用于将 Intent 与 action、category 和数据（type、scheme、authority、path）匹配的结构化条件。用于清单声明和动态接收器注册。

### 5.2 匹配规则（来自 Javadoc，第 92-148 行）

匹配需要满足三个条件：
1. **Action** 必须匹配（过滤器中的任一 action）
2. **Category** —— Intent 中的所有 category 必须存在于过滤器中
3. **Data** —— type 和 scheme+authority+path 必须匹配

### 5.3 关键方法

```java
public final void addAction(String action);                      // 第 730 行
public final void addCategory(String category);                  // 第 1679 行
public final void addDataType(String type);                      // 第 826 行
public final void addDataScheme(String scheme);                  // 第 1035 行
public final void addDataAuthority(String host, String port);    // 第 1333 行
public final void addDataPath(String path, int type);            // 第 1421 行
public final void addDataSchemeSpecificPart(String ssp, int type); // 第 1227 行

public final void setPriority(int priority);                     // 第 515 行
public final int getPriority();                                  // 第 526 行
public final void setAutoVerify(boolean autoVerify);             // 第 566 行

// 匹配方法
public final boolean matchAction(String action);                 // 第 770 行
public final int matchData(String type, String scheme, Uri data);// 第 1579 行
public final int matchDataAuthority(Uri data);                   // 第 1515 行
```

### 5.4 优先级常量

```java
public static final int SYSTEM_HIGH_PRIORITY = 1000;             // 第 176 行 -- 仅限系统接收器
public static final int SYSTEM_LOW_PRIORITY = -1000;             // 第 186 行 -- 在应用接收器之后
```

---

## 6. BroadcastReceiver.java —— 事件处理

**文件：** `frameworks/base/core/java/android/content/BroadcastReceiver.java`
**行数：** 687
**类型：** 抽象类

### 6.1 用途

接收和处理广播 Intent 的代码基类。可以通过 `Context.registerReceiver()` 动态注册或通过清单中的 `<receiver>` 静态注册。

### 6.2 关键方法

```java
public abstract void onReceive(Context context, Intent intent);  // 第 345 行 -- 必须实现
public final PendingResult goAsync();                            // 第 383 行 -- 返回后继续处理
public IBinder peekService(Context myContext, Intent service);   // 第 403 行
```

### 6.3 有序广播支持

```java
public final void setResultCode(int code);                       // 第 432 行
public final int getResultCode();                                // 第 442 行
public final void setResultData(String data);                    // 第 461 行
public final String getResultData();                             // 第 472 行
public final void setResultExtras(Bundle extras);                // 第 493 行
public final Bundle getResultExtras(boolean makeMap);            // 第 509 行
public final void setResult(int code, String data, Bundle extras); // 第 541 行
public final void abortBroadcast();                              // 第 572 行 -- 仅限有序广播
public final boolean isOrderedBroadcast();                       // 第 591 行
```

### 6.4 PendingResult（goAsync 模式）

`PendingResult` 内部类（第 70 行）允许将广播处理卸载到后台线程。广播在调用 `PendingResult.finish()` 之前保持活动状态。关键约束：
- 前台广播 10 秒超时（第 319 行）
- 后台广播 30+ 秒（第 367 行）
- 必须始终调用 `finish()` 以防止 ANR

### 6.5 隐藏 API

- `getSendingUser()`（第 639 行）—— `@SystemApi`，返回发送广播的用户
- `setPendingResult()` / `getPendingResult()`（第 617-627 行）—— `@hide`，内部管道

---

## 7. SharedPreferences —— 键值存储

**文件：** `frameworks/base/core/java/android/content/SharedPreferences.java`
**行数：** 392
**类型：** 接口

### 7.1 用途

用于基本类型和 String/StringSet 的简单持久化键值存储。每个文件在进程内单例。**不适用于跨进程使用**（第 38 行）。

### 7.2 读取 API

```java
Map<String, ?> getAll();                                         // 第 255 行
String getString(String key, @Nullable String defValue);         // 第 270 行
Set<String> getStringSet(String key, @Nullable Set<String> defValues); // 第 289 行
int getInt(String key, int defValue);                            // 第 303 行
long getLong(String key, long defValue);                         // 第 317 行
float getFloat(String key, float defValue);                     // 第 331 行
boolean getBoolean(String key, boolean defValue);               // 第 345 行
boolean contains(String key);                                    // 第 354 行
```

### 7.3 Editor 接口（写入 API）

```java
Editor putString(String key, @Nullable String value);            // 第 93 行
Editor putStringSet(String key, @Nullable Set<String> values);   // 第 106 行
Editor putInt(String key, int value);                            // 第 118 行
Editor putLong(String key, long value);                          // 第 130 行
Editor putFloat(String key, float value);                        // 第 142 行
Editor putBoolean(String key, boolean value);                    // 第 154 行
Editor remove(String key);                                       // 第 170 行
Editor clear();                                                  // 第 184 行
boolean commit();                                                // 第 202 行 -- 同步，返回成功与否
void apply();                                                    // 第 240 行 -- 异步，无返回值
```

### 7.4 变更监听器

```java
void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);   // 第 383 行
void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener); // 第 391 行
```

### 7.5 关键设计说明

1. **监听器 GC 风险**（第 373 行）：偏好设置管理器不持有对监听器的强引用。您必须保持自己的强引用。
2. **commit() 与 apply()**（第 202-240 行）：`apply()` 立即写入内存但磁盘 I/O 是异步的。`commit()` 是同步的。如果 `apply()` 待处理时调用 `commit()`，`commit()` 会阻塞直到所有待处理的写入完成。
3. **Android R 行为变更**（第 61 行）：`OnSharedPreferenceChangeListener` 现在在 `clear()` 时以 `null` key 触发。
4. **StringSet 可变性警告**（第 275-276 行）：不得修改 `getStringSet()` 返回的 Set。

---

## 8. ClipboardManager 和 ClipData —— 剪贴板访问

### 8.1 ClipboardManager

**文件：** `frameworks/base/core/java/android/content/ClipboardManager.java`
**行数：** 253
**类型：** 扩展 `android.text.ClipboardManager` 的类，标注 `@SystemService(Context.CLIPBOARD_SERVICE)`

#### 关键方法
```java
public void setPrimaryClip(@NonNull ClipData clip);              // 第 101 行
public void clearPrimaryClip();                                  // 第 116 行
public @Nullable ClipData getPrimaryClip();                      // 第 132 行
public @Nullable ClipDescription getPrimaryClipDescription();    // 第 149 行
public boolean hasPrimaryClip();                                 // 第 164 行
public void addPrimaryClipChangedListener(OnPrimaryClipChangedListener what);  // 第 172 行
public void removePrimaryClipChangedListener(OnPrimaryClipChangedListener what); // 第 187 行
```

**Android 10+ 限制**（第 127-128、144-145 行）：如果应用不是默认输入法或没有输入焦点，`getPrimaryClip()` 和 `getPrimaryClipDescription()` 返回 `null`。

### 8.2 ClipData

**文件：** `frameworks/base/core/java/android/content/ClipData.java`
**行数：** 1,178
**类型：** 实现 `Parcelable` 的类

#### 工厂方法（推荐用于创建剪贴）
```java
static public ClipData newPlainText(CharSequence label, CharSequence text);      // 第 768 行
static public ClipData newHtmlText(CharSequence label, CharSequence text, String htmlText); // 第 783 行
static public ClipData newIntent(CharSequence label, Intent intent);             // 第 797 行
static public ClipData newUri(ContentResolver resolver, CharSequence label, Uri uri); // 第 813 行
static public ClipData newRawUri(CharSequence label, Uri uri);                   // 第 860 行
```

#### Item 类（第 198 行）
每个 ClipData 包含一个或多个 `Item` 对象，持有：
- `CharSequence text` —— 纯文本
- `String htmlText` —— HTML 表示
- `Intent intent` —— 一个 Intent
- `Uri uri` —— 一个 content URI

Item 的强制转换方法：
```java
public CharSequence coerceToText(Context context);               // 第 337 行
public CharSequence coerceToStyledText(Context context);         // 第 420 行
public String coerceToHtmlText(Context context);                 // 第 470 行
```

---

## 9. 数据库 API

### 9.1 Cursor 接口

**文件：** `frameworks/base/core/java/android/database/Cursor.java`
**行数：** 518
**类型：** 扩展 `Closeable` 的接口

#### 字段类型常量（第 44-57 行）
```java
static final int FIELD_TYPE_NULL = 0;
static final int FIELD_TYPE_INTEGER = 1;
static final int FIELD_TYPE_FLOAT = 2;
static final int FIELD_TYPE_STRING = 3;
static final int FIELD_TYPE_BLOB = 4;
```

#### 导航
```java
int getCount();                          // 第 64 行
int getPosition();                       // 第 75 行
boolean move(int offset);               // 第 92 行
boolean moveToPosition(int position);   // 第 104 行
boolean moveToFirst();                   // 第 113 行
boolean moveToLast();                    // 第 122 行
boolean moveToNext();                    // 第 132 行
boolean moveToPrevious();               // 第 142 行
boolean isFirst(); / isLast(); / isBeforeFirst(); / isAfterLast();
```

#### 列访问
```java
int getColumnIndex(String columnName);                    // 第 184 行
int getColumnIndexOrThrow(String columnName);             // 第 197 行
String getColumnName(int columnIndex);                    // 第 205 行
String[] getColumnNames();                                // 第 213 行
int getColumnCount();                                     // 第 219 行
```

#### 数据检索
```java
byte[] getBlob(int columnIndex);         // 第 231 行
String getString(int columnIndex);       // 第 243 行
short getShort(int columnIndex);         // 第 266 行
int getInt(int columnIndex);             // 第 279 行
long getLong(int columnIndex);           // 第 292 行
float getFloat(int columnIndex);         // 第 305 行
double getDouble(int columnIndex);       // 第 318 行
int getType(int columnIndex);            // 第 339 行
boolean isNull(int columnIndex);         // 第 347 行
```

#### 生命周期和观察
```java
void close();                                                    // 第 380 行
boolean isClosed();                                              // 第 386 行
void registerContentObserver(ContentObserver observer);          // 第 395 行
void setNotificationUri(ContentResolver cr, Uri uri);            // 第 436 行
default void setNotificationUris(ContentResolver cr, List<Uri> uris); // 第 452 行 -- 默认方法，Android 10+
void setExtras(Bundle extras);                                   // 第 493 行
Bundle getExtras();                                              // 第 504 行
```

### 9.2 SQLiteDatabase

**文件：** `frameworks/base/core/java/android/database/sqlite/SQLiteDatabase.java`
**行数：** 2,878
**类型：** 扩展 `SQLiteClosable` 的 final 类

#### 冲突解决常量（第 161-205 行）
```java
public static final int CONFLICT_ROLLBACK = 1;
public static final int CONFLICT_ABORT = 2;     // 默认
public static final int CONFLICT_FAIL = 3;
public static final int CONFLICT_IGNORE = 4;
public static final int CONFLICT_REPLACE = 5;
public static final int CONFLICT_NONE = 0;
```

#### 打开标志（第 234-282 行）
```java
public static final int OPEN_READWRITE = 0x00000000;
public static final int OPEN_READONLY = 0x00000001;
public static final int CREATE_IF_NECESSARY = 0x10000000;
public static final int NO_LOCALIZED_COLLATORS = 0x00000010;
public static final int ENABLE_WRITE_AHEAD_LOGGING = 0x20000000;
```

#### 数据库打开
```java
public static SQLiteDatabase openDatabase(String path, CursorFactory factory, int flags); // 第 734 行
public static SQLiteDatabase openDatabase(File path, OpenParams openParams);               // 第 749 行
public static SQLiteDatabase openOrCreateDatabase(String path, CursorFactory factory);     // 第 804 行
public static SQLiteDatabase create(@Nullable CursorFactory factory);                      // 第 940 行 -- 内存数据库
public static SQLiteDatabase createInMemory(OpenParams openParams);                        // 第 957 行
public static boolean deleteDatabase(File file);                                           // 第 824 行
```

#### 事务管理
```java
public void beginTransaction();                              // 第 480 行 -- 独占
public void beginTransactionNonExclusive();                  // 第 504 行 -- DEFERRED
public void beginTransactionWithListener(SQLiteTransactionListener listener); // 第 533 行
public void endTransaction();                                // 第 585 行
public void setTransactionSuccessful();                      // 第 603 行
public boolean inTransaction();                              // 第 617 行
```

#### CRUD 操作
```java
public Cursor query(boolean distinct, String table, String[] columns,
        String selection, String[] selectionArgs, String groupBy,
        String having, String orderBy, String limit);                // 第 1260 行
public Cursor query(String table, String[] columns, String selection,
        String[] selectionArgs, String groupBy, String having, String orderBy); // 第 1427 行
public Cursor rawQuery(String sql, String[] selectionArgs);          // 第 1483 行

public long insert(String table, String nullColumnHack, ContentValues values);           // 第 1568 行
public long insertOrThrow(String table, String nullColumnHack, ContentValues values);    // 第 1594 行
public long insertWithOnConflict(String table, String nullColumnHack,
        ContentValues initialValues, int conflictAlgorithm);                             // 第 1667 行
public long replace(String table, String nullColumnHack, ContentValues initialValues);   // 第 1615 行

public int delete(String table, String whereClause, String[] whereArgs);                 // 第 1723 行
public int update(String table, ContentValues values, String whereClause, String[] whereArgs); // 第 1751 行
public int updateWithOnConflict(String table, ContentValues values,
        String whereClause, String[] whereArgs, int conflictAlgorithm);                  // 第 1769 行

public void execSQL(String sql);                                     // 第 1840 行
public void execSQL(String sql, Object[] bindArgs);                  // 第 1893 行
```

#### 自定义函数（Android 11 新增）
```java
public void setCustomScalarFunction(String functionName, UnaryOperator<String> function); // 第 985 行
public void setCustomAggregateFunction(String functionName, BinaryOperator<String> function); // 第 1031 行
```

#### 配置
```java
public int getVersion();                                     // 第 1092 行
public void setVersion(int version);                         // 第 1101 行
public long getMaximumSize();                                // 第 1110 行
public long setMaximumSize(long numBytes);                   // 第 1122 行
public void setLocale(Locale locale);                        // 第 2017 行
public boolean isReadOnly();                                 // 第 1953 行
public boolean isOpen();                                     // 第 1980 行
public String getPath();                                     // 第 2001 行
public boolean enableWriteAheadLogging();                    // WAL 模式
public void disableWriteAheadLogging();
public boolean isWriteAheadLoggingEnabled();
```

### 9.3 SQLiteOpenHelper

**文件：** `frameworks/base/core/java/android/database/sqlite/SQLiteOpenHelper.java`
**行数：** 558
**类型：** 实现 `AutoCloseable` 的抽象类

#### 构造函数
```java
public SQLiteOpenHelper(Context context, String name, CursorFactory factory, int version);  // 第 80 行
public SQLiteOpenHelper(Context context, String name, CursorFactory factory, int version,
        DatabaseErrorHandler errorHandler);                                                  // 第 102 行
public SQLiteOpenHelper(Context context, String name, int version, OpenParams openParams);  // 第 123 行
```

#### 生命周期方法（必须/应该重写）
```java
public abstract void onCreate(SQLiteDatabase db);            // 第 499 行 -- 必须重写
public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion); // 第 521 行 -- 必须重写
public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion); // 第 539 行 -- 默认抛出异常
public void onConfigure(SQLiteDatabase db);                  // 第 477 行 -- 在创建/升级之前
public void onOpen(SQLiteDatabase db);                       // 第 557 行 -- 在所有操作之后
public void onBeforeDelete(SQLiteDatabase db);               // 第 490 行 -- @hide，在删除过时数据库之前
```

#### 数据库访问
```java
public SQLiteDatabase getWritableDatabase();                 // 第 314 行
public SQLiteDatabase getReadableDatabase();                 // 第 338 行
public synchronized void close();                            // 第 451 行
public String getDatabaseName();                             // 第 177 行
```

#### 配置
```java
public void setWriteAheadLoggingEnabled(boolean enabled);    // 第 192 行
public void setLookasideConfig(int slotSize, int slotCount); // 第 227 行
```

#### 关键设计细节（getDatabaseLocked，第 344-441 行）

内部 `getDatabaseLocked()` 方法实现版本管理生命周期：
1. 尝试打开现有数据库
2. 调用 `onConfigure(db)`
3. 检查版本：
   - 版本 0（新数据库）：调用 `onCreate(db)`
   - 版本 < 当前版本：调用 `onUpgrade(db, oldVersion, newVersion)`
   - 版本 > 当前版本：调用 `onDowngrade(db, oldVersion, newVersion)`（默认抛出异常）
4. 设置版本并提交事务
5. 调用 `onOpen(db)`

所有版本变更操作都包装在事务中（第 408 行）。

---

## 10. 提供者契约

### 10.1 Settings.java

**文件：** `frameworks/base/core/java/android/provider/Settings.java`
**行数：** 15,217
**类型：** final 类

#### 结构

三个主要内部类代表不同的命名空间：

1. **`Settings.System`**（第 3042 行）—— 面向用户的系统设置（铃声、屏幕亮度等）
2. **`Settings.Secure`**（第 5229 行）—— 安全设置，需要 `WRITE_SECURE_SETTINGS` 权限
3. **`Settings.Global`**（未在摘录中显示）—— 设备范围设置（飞行模式、数据漫游等）

每个内部类扩展 `NameValueTable` 并提供：
```java
public static String getString(ContentResolver resolver, String name);
public static boolean putString(ContentResolver resolver, String name, String value);
public static int getInt(ContentResolver cr, String name, int def);
public static boolean putInt(ContentResolver cr, String name, int value);
public static long getLong(ContentResolver cr, String name, long def);
public static float getFloat(ContentResolver cr, String name, float def);
public static Uri getUriFor(String name);
```

#### @hide 统计

- **1,065 个 @hide 注解** —— 绝大多数 Settings 常量是隐藏的
- **71 个 @SystemApi 注解** —— 许多需要系统级权限

#### 设置 UI 的 Intent Action（第 107-143 行）
```java
public static final String ACTION_SETTINGS = "android.settings.SETTINGS";
public static final String ACTION_APN_SETTINGS = "android.settings.APN_SETTINGS";
public static final String ACTION_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS";
// ... 更多
```

### 10.2 ContactsContract.java

**文件：** `frameworks/base/core/java/android/provider/ContactsContract.java`
**行数：** 9,747
**类型：** final 类

#### 三层数据模型（文档第 68-119 行）

1. **Data** 表 —— 单个数据项（电话、邮箱等）。开放的数据类型集合。
2. **RawContacts** 表 —— 来自单个账户的描述一个人的数据集
3. **Contacts** 表 —— 同一个人的一个或多个 RawContacts 的聚合视图

#### 关键常量
```java
public static final String AUTHORITY = "com.android.contacts";                   // 第 124 行
public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);     // 第 126 行
public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";     // 第 147 行
```

#### 内部类（选摘）
- `ContactsContract.Contacts` —— 聚合联系人
- `ContactsContract.RawContacts` —— 每账户联系人
- `ContactsContract.Data` —— 单个数据行
- `ContactsContract.CommonDataKinds.Phone` —— 电话号码
- `ContactsContract.CommonDataKinds.Email` —— 邮箱地址
- `ContactsContract.Groups` —— 联系人分组
- `ContactsContract.PhoneLookup` —— 快速来电 ID 查询
- `ContactsContract.QuickContact` —— 快速联系人徽章

#### 隐藏 API：列前缀
```java
public static final String HIDDEN_COLUMN_PREFIX = "x_";                          // 第 134 行 -- @hide @TestApi
```

### 10.3 CalendarContract.java

**文件：** `frameworks/base/core/java/android/provider/CalendarContract.java`
**行数：** 2,855
**类型：** final 类

#### 数据模型（文档第 51-97 行）

表：
- **Calendars** —— 日历元数据（名称、颜色、同步信息）
- **Events** —— 事件详情（标题、地点、开始/结束时间）
- **Instances** —— 事件的计算出现次数（处理重复事件）
- **Attendees** —— 事件参与者
- **Reminders** —— 提醒/通知设置
- **ExtendedProperties** —— 同步适配器的不透明数据

#### 关键常量
```java
public static final String ACTION_EVENT_REMINDER = "android.intent.action.EVENT_REMINDER"; // 第 108 行
public static final String ACTION_HANDLE_CUSTOM_EVENT =
        "android.provider.calendar.action.HANDLE_CUSTOM_EVENT";                  // 第 135 行
public static final String ACTION_VIEW_MANAGED_PROFILE_CALENDAR_EVENT =
        "android.provider.calendar.action.VIEW_MANAGED_PROFILE_CALENDAR_EVENT";  // 第 142 行
```

### 10.4 MediaStore

**注意：** 在此 AOSP 检出中未在预期位置（`frameworks/base/core/java/android/provider/MediaStore.java`）找到 `MediaStore.java`。它可能在 Android 11 源代码树重构中被移至其他模块（可能 `frameworks/base/core/java/android/provider/` 被拆分或 MediaStore 被移至 Mainline 模块）。

---

## 11. ContentObserver —— 响应式数据更新

**文件：** `frameworks/base/core/java/android/database/ContentObserver.java`
**行数：**（辅助类）

ContentObserver 为内容提供者启用响应式编程模式：

```java
// 注册（在 ContentResolver 中）
contentResolver.registerContentObserver(uri, notifyForDescendants, observer);

// 通知（在 ContentProvider 中）
getContext().getContentResolver().notifyChange(uri, null);

// 接收（在 ContentObserver 子类中）
public void onChange(boolean selfChange, Uri uri) { ... }
```

这是应用于 content URI 的**观察者**模式，使 UI 组件（通过 CursorLoader 或 LiveData）在底层数据变更时自动刷新。

---

## 12. 设计模式总结

| 模式 | 使用位置 | 描述 |
|---------|-----------|-------------|
| **抽象工厂** | `Context` | 抽象类，`ContextImpl` 提供具体实现 |
| **装饰器** | `ContextWrapper` | 包装 Context，用于 Activity/Service/Application |
| **代理** | `ContentProvider.Transport` | ContentProvider 的跨进程 IPC 桥接 |
| **观察者** | `ContentObserver`、`ContentResolver.notifyChange()` | 响应式数据变更通知 |
| **构建器** | `ContentValues`、`Intent` | 用于构建数据/消息的流式 API |
| **策略** | `SQLiteDatabase` 冲突解决 | 可插拔的冲突处理（ROLLBACK、ABORT 等） |
| **模板方法** | `SQLiteOpenHelper` | onCreate/onUpgrade/onOpen 生命周期 |
| **适配器** | `CursorWrapper` | 用修改后的行为包装 Cursor |
| **单例** | `SharedPreferences` | 每个进程中每个偏好文件一个实例 |
| **命令** | `ContentProviderOperation` | 封装的批量操作 |

---

## 13. 安全注解总结

| 注解 | 典型用途 | 数量（跨审查文件） |
|-----------|--------------|-------------------------------|
| `@RequiresPermission` | 需要运行时/清单权限的方法 | 在 Context、Intent 中广泛存在 |
| `@SystemApi` | 仅限系统应用的 API | 跨文件共 198+ |
| `@TestApi` | 仅限测试的 API | 多处 |
| `@hide` | 不在 SDK 中的内部 API | 跨文件共 1,663+ |
| `@UnsupportedAppUsage` | 应用使用但不应使用的灰名单 API | 在内部字段中非常常见 |
| `@Deprecated` | 过时的 API | MODE_WORLD_*、MODE_MULTI_PROCESS、getText/setText |

### 关键权限要求

- `INTERACT_ACROSS_USERS` / `INTERACT_ACROSS_USERS_FULL` —— ContentProvider 中的跨用户操作（第 19 行）
- `WRITE_SECURE_SETTINGS` —— 修改 Settings.Secure 值
- `WRITE_SETTINGS` —— 修改 Settings.System 值
- `READ_CONTACTS` / `WRITE_CONTACTS` —— ContactsContract 操作
- `READ_CALENDAR` / `WRITE_CALENDAR` —— CalendarContract 操作
- URI 权限（FLAG_GRANT_READ_URI_PERMISSION、FLAG_GRANT_WRITE_URI_PERMISSION）—— 临时访问授予

---

## 14. 关键文件参考

| 文件 | 路径 | 行数 |
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
