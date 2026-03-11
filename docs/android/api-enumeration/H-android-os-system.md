# Android 11 (API 30) Public API Enumeration: Android Os System

Generated from `frameworks/base/api/current.txt`

## Summary

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.os` | 92 | 567 | 323 | 77 |
| `android.os.health` | 8 | 32 | 75 | 3 |
| `android.os.storage` | 4 | 22 | 13 | 2 |
| `android.os.strictmode` | 20 | 1 | 0 | 0 |
| `android.security` | 15 | 18 | 9 | 12 |
| `android.security.identity` | 20 | 6 | 8 | 25 |
| `android.security.keystore` | 14 | 42 | 36 | 26 |
| `android.system` | 10 | 115 | 535 | 9 |
| **TOTAL** | **183** | **803** | **999** | **154** |

---

## Package: `android.os`

### `class abstract AsyncTask<Params, Progress, Result>` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `enum AsyncTask.Status` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class BadParcelableException`

- **Extends:** `android.util.AndroidRuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BadParcelableException(String)` |  |
| `BadParcelableException(Exception)` |  |

---

### `class BaseBundle`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clear()` |  |
| `boolean containsKey(String)` |  |
| `boolean getBoolean(String)` |  |
| `boolean getBoolean(String, boolean)` |  |
| `double getDouble(String)` |  |
| `double getDouble(String, double)` |  |
| `int getInt(String)` |  |
| `int getInt(String, int)` |  |
| `long getLong(String)` |  |
| `long getLong(String, long)` |  |
| `String getString(@Nullable String, String)` |  |
| `boolean isEmpty()` |  |
| `java.util.Set<java.lang.String> keySet()` |  |
| `void putAll(android.os.PersistableBundle)` |  |
| `void putBoolean(@Nullable String, boolean)` |  |
| `void putBooleanArray(@Nullable String, @Nullable boolean[])` |  |
| `void putDouble(@Nullable String, double)` |  |
| `void putDoubleArray(@Nullable String, @Nullable double[])` |  |
| `void putInt(@Nullable String, int)` |  |
| `void putIntArray(@Nullable String, @Nullable int[])` |  |
| `void putLong(@Nullable String, long)` |  |
| `void putLongArray(@Nullable String, @Nullable long[])` |  |
| `void putString(@Nullable String, @Nullable String)` |  |
| `void putStringArray(@Nullable String, @Nullable String[])` |  |
| `void remove(String)` |  |
| `int size()` |  |

---

### `class BatteryManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CHARGING = "android.os.action.CHARGING"` |  |
| `static final String ACTION_DISCHARGING = "android.os.action.DISCHARGING"` |  |
| `static final int BATTERY_HEALTH_COLD = 7` |  |
| `static final int BATTERY_HEALTH_DEAD = 4` |  |
| `static final int BATTERY_HEALTH_GOOD = 2` |  |
| `static final int BATTERY_HEALTH_OVERHEAT = 3` |  |
| `static final int BATTERY_HEALTH_OVER_VOLTAGE = 5` |  |
| `static final int BATTERY_HEALTH_UNKNOWN = 1` |  |
| `static final int BATTERY_HEALTH_UNSPECIFIED_FAILURE = 6` |  |
| `static final int BATTERY_PLUGGED_AC = 1` |  |
| `static final int BATTERY_PLUGGED_USB = 2` |  |
| `static final int BATTERY_PLUGGED_WIRELESS = 4` |  |
| `static final int BATTERY_PROPERTY_CAPACITY = 4` |  |
| `static final int BATTERY_PROPERTY_CHARGE_COUNTER = 1` |  |
| `static final int BATTERY_PROPERTY_CURRENT_AVERAGE = 3` |  |
| `static final int BATTERY_PROPERTY_CURRENT_NOW = 2` |  |
| `static final int BATTERY_PROPERTY_ENERGY_COUNTER = 5` |  |
| `static final int BATTERY_PROPERTY_STATUS = 6` |  |
| `static final int BATTERY_STATUS_CHARGING = 2` |  |
| `static final int BATTERY_STATUS_DISCHARGING = 3` |  |
| `static final int BATTERY_STATUS_FULL = 5` |  |
| `static final int BATTERY_STATUS_NOT_CHARGING = 4` |  |
| `static final int BATTERY_STATUS_UNKNOWN = 1` |  |
| `static final String EXTRA_BATTERY_LOW = "battery_low"` |  |
| `static final String EXTRA_HEALTH = "health"` |  |
| `static final String EXTRA_ICON_SMALL = "icon-small"` |  |
| `static final String EXTRA_LEVEL = "level"` |  |
| `static final String EXTRA_PLUGGED = "plugged"` |  |
| `static final String EXTRA_PRESENT = "present"` |  |
| `static final String EXTRA_SCALE = "scale"` |  |
| `static final String EXTRA_STATUS = "status"` |  |
| `static final String EXTRA_TECHNOLOGY = "technology"` |  |
| `static final String EXTRA_TEMPERATURE = "temperature"` |  |
| `static final String EXTRA_VOLTAGE = "voltage"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long computeChargeTimeRemaining()` |  |
| `int getIntProperty(int)` |  |
| `long getLongProperty(int)` |  |
| `boolean isCharging()` |  |

---

### `class Binder`

- **Implements:** `android.os.IBinder`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Binder()` |  |
| `Binder(@Nullable String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void attachInterface(@Nullable android.os.IInterface, @Nullable String)` |  |
| `static final long clearCallingIdentity()` |  |
| `static final long clearCallingWorkSource()` |  |
| `void dump(@NonNull java.io.FileDescriptor, @Nullable String[])` |  |
| `void dump(@NonNull java.io.FileDescriptor, @NonNull java.io.PrintWriter, @Nullable String[])` |  |
| `void dumpAsync(@NonNull java.io.FileDescriptor, @Nullable String[])` |  |
| `static final void flushPendingCommands()` |  |
| `static final int getCallingPid()` |  |
| `static final int getCallingUid()` |  |
| `static final int getCallingUidOrThrow()` |  |
| `static final int getCallingWorkSourceUid()` |  |
| `boolean isBinderAlive()` |  |
| `static final void joinThreadPool()` |  |
| `void linkToDeath(@NonNull android.os.IBinder.DeathRecipient, int)` |  |
| `boolean onTransact(int, @NonNull android.os.Parcel, @Nullable android.os.Parcel, int) throws android.os.RemoteException` |  |
| `boolean pingBinder()` |  |
| `static final void restoreCallingIdentity(long)` |  |
| `static final void restoreCallingWorkSource(long)` |  |
| `static final long setCallingWorkSourceUid(int)` |  |
| `final boolean transact(int, @NonNull android.os.Parcel, @Nullable android.os.Parcel, int) throws android.os.RemoteException` |  |
| `boolean unlinkToDeath(@NonNull android.os.IBinder.DeathRecipient, int)` |  |

---

### `class Build`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Build()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String BOARD` |  |
| `static final String BOOTLOADER` |  |
| `static final String BRAND` |  |
| `static final String DEVICE` |  |
| `static final String DISPLAY` |  |
| `static final String FINGERPRINT` |  |
| `static final String HARDWARE` |  |
| `static final String HOST` |  |
| `static final String ID` |  |
| `static final String MANUFACTURER` |  |
| `static final String MODEL` |  |
| `static final String PRODUCT` |  |
| `static final String[] SUPPORTED_32_BIT_ABIS` |  |
| `static final String[] SUPPORTED_64_BIT_ABIS` |  |
| `static final String[] SUPPORTED_ABIS` |  |
| `static final String TAGS` |  |
| `static final long TIME` |  |
| `static final String TYPE` |  |
| `static final String UNKNOWN = "unknown"` |  |
| `static final String USER` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String getRadioVersion()` |  |

---

### `class static Build.Partition`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String PARTITION_NAME_SYSTEM = "system"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getBuildTimeMillis()` |  |

---

### `class static Build.VERSION`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Build.VERSION()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String BASE_OS` |  |
| `static final String CODENAME` |  |
| `static final String INCREMENTAL` |  |
| `static final int PREVIEW_SDK_INT` |  |
| `static final String RELEASE` |  |
| `static final int SDK_INT` |  |
| `static final String SECURITY_PATCH` |  |

---

### `class static Build.VERSION_CODES`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Build.VERSION_CODES()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BASE = 1` |  |
| `static final int BASE_1_1 = 2` |  |
| `static final int CUPCAKE = 3` |  |
| `static final int CUR_DEVELOPMENT = 10000` |  |
| `static final int DONUT = 4` |  |
| `static final int ECLAIR = 5` |  |
| `static final int ECLAIR_0_1 = 6` |  |
| `static final int ECLAIR_MR1 = 7` |  |
| `static final int FROYO = 8` |  |
| `static final int GINGERBREAD = 9` |  |
| `static final int GINGERBREAD_MR1 = 10` |  |
| `static final int HONEYCOMB = 11` |  |
| `static final int HONEYCOMB_MR1 = 12` |  |
| `static final int HONEYCOMB_MR2 = 13` |  |
| `static final int ICE_CREAM_SANDWICH = 14` |  |
| `static final int ICE_CREAM_SANDWICH_MR1 = 15` |  |
| `static final int JELLY_BEAN = 16` |  |
| `static final int JELLY_BEAN_MR1 = 17` |  |
| `static final int JELLY_BEAN_MR2 = 18` |  |
| `static final int KITKAT = 19` |  |
| `static final int KITKAT_WATCH = 20` |  |
| `static final int LOLLIPOP = 21` |  |
| `static final int LOLLIPOP_MR1 = 22` |  |
| `static final int M = 23` |  |
| `static final int N = 24` |  |
| `static final int N_MR1 = 25` |  |
| `static final int O = 26` |  |
| `static final int O_MR1 = 27` |  |
| `static final int P = 28` |  |
| `static final int Q = 29` |  |
| `static final int R = 30` |  |

---

### `class final Bundle`

- **Extends:** `android.os.BaseBundle`
- **Implements:** `java.lang.Cloneable android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Bundle()` |  |
| `Bundle(ClassLoader)` |  |
| `Bundle(int)` |  |
| `Bundle(android.os.Bundle)` |  |
| `Bundle(android.os.PersistableBundle)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.os.Bundle EMPTY` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `android.os.Bundle deepCopy()` |  |
| `int describeContents()` |  |
| `byte getByte(String)` |  |
| `Byte getByte(String, byte)` |  |
| `char getChar(String)` |  |
| `char getChar(String, char)` |  |
| `CharSequence getCharSequence(@Nullable String, CharSequence)` |  |
| `ClassLoader getClassLoader()` |  |
| `float getFloat(String)` |  |
| `float getFloat(String, float)` |  |
| `short getShort(String)` |  |
| `short getShort(String, short)` |  |
| `boolean hasFileDescriptors()` |  |
| `void putAll(android.os.Bundle)` |  |
| `void putBinder(@Nullable String, @Nullable android.os.IBinder)` |  |
| `void putBundle(@Nullable String, @Nullable android.os.Bundle)` |  |
| `void putByte(@Nullable String, byte)` |  |
| `void putByteArray(@Nullable String, @Nullable byte[])` |  |
| `void putChar(@Nullable String, char)` |  |
| `void putCharArray(@Nullable String, @Nullable char[])` |  |
| `void putCharSequence(@Nullable String, @Nullable CharSequence)` |  |
| `void putCharSequenceArray(@Nullable String, @Nullable CharSequence[])` |  |
| `void putCharSequenceArrayList(@Nullable String, @Nullable java.util.ArrayList<java.lang.CharSequence>)` |  |
| `void putFloat(@Nullable String, float)` |  |
| `void putFloatArray(@Nullable String, @Nullable float[])` |  |
| `void putIntegerArrayList(@Nullable String, @Nullable java.util.ArrayList<java.lang.Integer>)` |  |
| `void putParcelable(@Nullable String, @Nullable android.os.Parcelable)` |  |
| `void putParcelableArray(@Nullable String, @Nullable android.os.Parcelable[])` |  |
| `void putParcelableArrayList(@Nullable String, @Nullable java.util.ArrayList<? extends android.os.Parcelable>)` |  |
| `void putSerializable(@Nullable String, @Nullable java.io.Serializable)` |  |
| `void putShort(@Nullable String, short)` |  |
| `void putShortArray(@Nullable String, @Nullable short[])` |  |
| `void putSize(@Nullable String, @Nullable android.util.Size)` |  |
| `void putSizeF(@Nullable String, @Nullable android.util.SizeF)` |  |
| `void putSparseParcelableArray(@Nullable String, @Nullable android.util.SparseArray<? extends android.os.Parcelable>)` |  |
| `void putStringArrayList(@Nullable String, @Nullable java.util.ArrayList<java.lang.String>)` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void setClassLoader(ClassLoader)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final CancellationSignal`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CancellationSignal()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `boolean isCanceled()` |  |
| `void setOnCancelListener(android.os.CancellationSignal.OnCancelListener)` |  |
| `void throwIfCanceled()` |  |

---

### `interface static CancellationSignal.OnCancelListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCancel()` |  |

---

### `class ConditionVariable`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConditionVariable()` |  |
| `ConditionVariable(boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void block()` |  |
| `boolean block(long)` |  |
| `void close()` |  |
| `void open()` |  |

---

### `class abstract CountDownTimer`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CountDownTimer(long, long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void cancel()` |  |
| `abstract void onFinish()` |  |
| `abstract void onTick(long)` |  |
| `final android.os.CountDownTimer start()` |  |

---

### `class final CpuUsageInfo`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `long getActive()` |  |
| `long getTotal()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class DeadObjectException`

- **Extends:** `android.os.RemoteException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DeadObjectException()` |  |
| `DeadObjectException(String)` |  |

---

### `class DeadSystemException`

- **Extends:** `android.os.DeadObjectException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DeadSystemException()` |  |

---

### `class final Debug`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int SHOW_CLASSLOADER = 2` |  |
| `static final int SHOW_FULL_DETAIL = 1` |  |
| `static final int SHOW_INITIALIZED = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void attachJvmtiAgent(@NonNull String, @Nullable String, @Nullable ClassLoader) throws java.io.IOException` |  |
| `static void dumpHprofData(String) throws java.io.IOException` |  |
| `static boolean dumpService(String, java.io.FileDescriptor, String[])` |  |
| `static void enableEmulatorTraceOutput()` |  |
| `static int getBinderDeathObjectCount()` |  |
| `static int getBinderLocalObjectCount()` |  |
| `static int getBinderProxyObjectCount()` |  |
| `static int getBinderReceivedTransactions()` |  |
| `static int getBinderSentTransactions()` |  |
| `static int getLoadedClassCount()` |  |
| `static void getMemoryInfo(android.os.Debug.MemoryInfo)` |  |
| `static long getNativeHeapAllocatedSize()` |  |
| `static long getNativeHeapFreeSize()` |  |
| `static long getNativeHeapSize()` |  |
| `static long getPss()` |  |
| `static String getRuntimeStat(String)` |  |
| `static java.util.Map<java.lang.String,java.lang.String> getRuntimeStats()` |  |
| `static boolean isDebuggerConnected()` |  |
| `static void printLoadedClasses(int)` |  |
| `static void startMethodTracing()` |  |
| `static void startMethodTracing(String)` |  |
| `static void startMethodTracing(String, int)` |  |
| `static void startMethodTracing(String, int, int)` |  |
| `static void startMethodTracingSampling(String, int, int)` |  |
| `static void startNativeTracing()` |  |
| `static void stopMethodTracing()` |  |
| `static void stopNativeTracing()` |  |
| `static long threadCpuTimeNanos()` |  |
| `static void waitForDebugger()` |  |
| `static boolean waitingForDebugger()` |  |

---

### `class static Debug.InstructionCount` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static Debug.MemoryInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Debug.MemoryInfo()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int dalvikPrivateDirty` |  |
| `int dalvikPss` |  |
| `int dalvikSharedDirty` |  |
| `int nativePrivateDirty` |  |
| `int nativePss` |  |
| `int nativeSharedDirty` |  |
| `int otherPrivateDirty` |  |
| `int otherPss` |  |
| `int otherSharedDirty` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getMemoryStat(String)` |  |
| `java.util.Map<java.lang.String,java.lang.String> getMemoryStats()` |  |
| `int getTotalPrivateClean()` |  |
| `int getTotalPrivateDirty()` |  |
| `int getTotalPss()` |  |
| `int getTotalSharedClean()` |  |
| `int getTotalSharedDirty()` |  |
| `int getTotalSwappablePss()` |  |
| `void readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class DropBoxManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DropBoxManager()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_DROPBOX_ENTRY_ADDED = "android.intent.action.DROPBOX_ENTRY_ADDED"` |  |
| `static final String EXTRA_DROPPED_COUNT = "android.os.extra.DROPPED_COUNT"` |  |
| `static final String EXTRA_TAG = "tag"` |  |
| `static final String EXTRA_TIME = "time"` |  |
| `static final int IS_EMPTY = 1` |  |
| `static final int IS_GZIPPED = 4` |  |
| `static final int IS_TEXT = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addData(String, byte[], int)` |  |
| `void addFile(String, java.io.File, int) throws java.io.IOException` |  |
| `void addText(String, String)` |  |
| `boolean isTagEnabled(String)` |  |

---

### `class static DropBoxManager.Entry`

- **Implements:** `java.io.Closeable android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DropBoxManager.Entry(String, long)` |  |
| `DropBoxManager.Entry(String, long, String)` |  |
| `DropBoxManager.Entry(String, long, byte[], int)` |  |
| `DropBoxManager.Entry(String, long, android.os.ParcelFileDescriptor, int)` |  |
| `DropBoxManager.Entry(String, long, java.io.File, int) throws java.io.IOException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int describeContents()` |  |
| `int getFlags()` |  |
| `java.io.InputStream getInputStream() throws java.io.IOException` |  |
| `String getTag()` |  |
| `String getText(int)` |  |
| `long getTimeMillis()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class Environment`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Environment()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static String DIRECTORY_ALARMS` |  |
| `static String DIRECTORY_AUDIOBOOKS` |  |
| `static String DIRECTORY_DCIM` |  |
| `static String DIRECTORY_DOCUMENTS` |  |
| `static String DIRECTORY_DOWNLOADS` |  |
| `static String DIRECTORY_MOVIES` |  |
| `static String DIRECTORY_MUSIC` |  |
| `static String DIRECTORY_NOTIFICATIONS` |  |
| `static String DIRECTORY_PICTURES` |  |
| `static String DIRECTORY_PODCASTS` |  |
| `static String DIRECTORY_RINGTONES` |  |
| `static String DIRECTORY_SCREENSHOTS` |  |
| `static final String MEDIA_BAD_REMOVAL = "bad_removal"` |  |
| `static final String MEDIA_CHECKING = "checking"` |  |
| `static final String MEDIA_EJECTING = "ejecting"` |  |
| `static final String MEDIA_MOUNTED = "mounted"` |  |
| `static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro"` |  |
| `static final String MEDIA_NOFS = "nofs"` |  |
| `static final String MEDIA_REMOVED = "removed"` |  |
| `static final String MEDIA_SHARED = "shared"` |  |
| `static final String MEDIA_UNKNOWN = "unknown"` |  |
| `static final String MEDIA_UNMOUNTABLE = "unmountable"` |  |
| `static final String MEDIA_UNMOUNTED = "unmounted"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.io.File getDataDirectory()` |  |
| `static java.io.File getDownloadCacheDirectory()` |  |
| `static String getExternalStorageState()` |  |
| `static String getExternalStorageState(java.io.File)` |  |
| `static boolean isExternalStorageEmulated()` |  |
| `static boolean isExternalStorageEmulated(@NonNull java.io.File)` |  |
| `static boolean isExternalStorageLegacy()` |  |
| `static boolean isExternalStorageLegacy(@NonNull java.io.File)` |  |
| `static boolean isExternalStorageManager()` |  |
| `static boolean isExternalStorageManager(@NonNull java.io.File)` |  |
| `static boolean isExternalStorageRemovable()` |  |
| `static boolean isExternalStorageRemovable(@NonNull java.io.File)` |  |

---

### `class abstract FileObserver`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileObserver(@NonNull java.io.File)` |  |
| `FileObserver(@NonNull java.util.List<java.io.File>)` |  |
| `FileObserver(@NonNull java.io.File, int)` |  |
| `FileObserver(@NonNull java.util.List<java.io.File>, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACCESS = 1` |  |
| `static final int ALL_EVENTS = 4095` |  |
| `static final int ATTRIB = 4` |  |
| `static final int CLOSE_NOWRITE = 16` |  |
| `static final int CLOSE_WRITE = 8` |  |
| `static final int CREATE = 256` |  |
| `static final int DELETE = 512` |  |
| `static final int DELETE_SELF = 1024` |  |
| `static final int MODIFY = 2` |  |
| `static final int MOVED_FROM = 64` |  |
| `static final int MOVED_TO = 128` |  |
| `static final int MOVE_SELF = 2048` |  |
| `static final int OPEN = 32` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize()` |  |
| `abstract void onEvent(int, @Nullable String)` |  |
| `void startWatching()` |  |
| `void stopWatching()` |  |

---

### `class FileUriExposedException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FileUriExposedException(String)` |  |

---

### `class final FileUtils`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static long copy(@NonNull java.io.InputStream, @NonNull java.io.OutputStream) throws java.io.IOException` |  |
| `static long copy(@NonNull java.io.InputStream, @NonNull java.io.OutputStream, @Nullable android.os.CancellationSignal, @Nullable java.util.concurrent.Executor, @Nullable android.os.FileUtils.ProgressListener) throws java.io.IOException` |  |
| `static long copy(@NonNull java.io.FileDescriptor, @NonNull java.io.FileDescriptor) throws java.io.IOException` |  |
| `static long copy(@NonNull java.io.FileDescriptor, @NonNull java.io.FileDescriptor, @Nullable android.os.CancellationSignal, @Nullable java.util.concurrent.Executor, @Nullable android.os.FileUtils.ProgressListener) throws java.io.IOException` |  |

---

### `interface static FileUtils.ProgressListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onProgress(long)` |  |

---

### `class Handler`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Handler(@NonNull android.os.Looper)` |  |
| `Handler(@NonNull android.os.Looper, @Nullable android.os.Handler.Callback)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void dispatchMessage(@NonNull android.os.Message)` |  |
| `final void dump(@NonNull android.util.Printer, @NonNull String)` |  |
| `void handleMessage(@NonNull android.os.Message)` |  |
| `final boolean hasCallbacks(@NonNull Runnable)` |  |
| `final boolean hasMessages(int)` |  |
| `final boolean hasMessages(int, @Nullable Object)` |  |
| `final boolean post(@NonNull Runnable)` |  |
| `final boolean postAtFrontOfQueue(@NonNull Runnable)` |  |
| `final boolean postAtTime(@NonNull Runnable, long)` |  |
| `final boolean postAtTime(@NonNull Runnable, @Nullable Object, long)` |  |
| `final boolean postDelayed(@NonNull Runnable, long)` |  |
| `final boolean postDelayed(@NonNull Runnable, @Nullable Object, long)` |  |
| `final void removeCallbacks(@NonNull Runnable)` |  |
| `final void removeCallbacks(@NonNull Runnable, @Nullable Object)` |  |
| `final void removeCallbacksAndMessages(@Nullable Object)` |  |
| `final void removeMessages(int)` |  |
| `final void removeMessages(int, @Nullable Object)` |  |
| `final boolean sendEmptyMessage(int)` |  |
| `final boolean sendEmptyMessageAtTime(int, long)` |  |
| `final boolean sendEmptyMessageDelayed(int, long)` |  |
| `final boolean sendMessage(@NonNull android.os.Message)` |  |
| `final boolean sendMessageAtFrontOfQueue(@NonNull android.os.Message)` |  |
| `boolean sendMessageAtTime(@NonNull android.os.Message, long)` |  |
| `final boolean sendMessageDelayed(@NonNull android.os.Message, long)` |  |

---

### `interface static Handler.Callback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean handleMessage(@NonNull android.os.Message)` |  |

---

### `class HandlerThread`

- **Extends:** `java.lang.Thread`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `HandlerThread(String)` |  |
| `HandlerThread(String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.os.Looper getLooper()` |  |
| `int getThreadId()` |  |
| `void onLooperPrepared()` |  |
| `boolean quit()` |  |
| `boolean quitSafely()` |  |

---

### `class HardwarePropertiesManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DEVICE_TEMPERATURE_BATTERY = 2` |  |
| `static final int DEVICE_TEMPERATURE_CPU = 0` |  |
| `static final int DEVICE_TEMPERATURE_GPU = 1` |  |
| `static final int DEVICE_TEMPERATURE_SKIN = 3` |  |
| `static final int TEMPERATURE_CURRENT = 0` |  |
| `static final int TEMPERATURE_SHUTDOWN = 2` |  |
| `static final int TEMPERATURE_THROTTLING = 1` |  |
| `static final int TEMPERATURE_THROTTLING_BELOW_VR_MIN = 3` |  |
| `static final float UNDEFINED_TEMPERATURE = -3.4028235E38f` |  |

---

### `interface IBinder`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DUMP_TRANSACTION = 1598311760` |  |
| `static final int FIRST_CALL_TRANSACTION = 1` |  |
| `static final int FLAG_ONEWAY = 1` |  |
| `static final int INTERFACE_TRANSACTION = 1598968902` |  |
| `static final int LAST_CALL_TRANSACTION = 16777215` |  |
| `static final int LIKE_TRANSACTION = 1598835019` |  |
| `static final int PING_TRANSACTION = 1599098439` |  |
| `static final int TWEET_TRANSACTION = 1599362900` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void dump(@NonNull java.io.FileDescriptor, @Nullable String[]) throws android.os.RemoteException` |  |
| `void dumpAsync(@NonNull java.io.FileDescriptor, @Nullable String[]) throws android.os.RemoteException` |  |
| `static int getSuggestedMaxIpcSizeBytes()` |  |
| `boolean isBinderAlive()` |  |
| `void linkToDeath(@NonNull android.os.IBinder.DeathRecipient, int) throws android.os.RemoteException` |  |
| `boolean pingBinder()` |  |
| `boolean transact(int, @NonNull android.os.Parcel, @Nullable android.os.Parcel, int) throws android.os.RemoteException` |  |
| `boolean unlinkToDeath(@NonNull android.os.IBinder.DeathRecipient, int)` |  |

---

### `interface static IBinder.DeathRecipient`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void binderDied()` |  |

---

### `interface IInterface`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.os.IBinder asBinder()` |  |

---

### `class LimitExceededException`

- **Extends:** `java.lang.IllegalStateException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LimitExceededException()` |  |
| `LimitExceededException(@NonNull String)` |  |

---

### `class final LocaleList`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LocaleList(@NonNull java.util.Locale...)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.Locale get(int)` |  |
| `boolean isEmpty()` |  |
| `static boolean isPseudoLocale(@Nullable android.icu.util.ULocale)` |  |
| `static void setDefault(@NonNull @Size(min=1) android.os.LocaleList)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final Looper`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void dump(@NonNull android.util.Printer, @NonNull String)` |  |
| `static android.os.Looper getMainLooper()` |  |
| `boolean isCurrentThread()` |  |
| `static void loop()` |  |
| `static void prepare()` |  |
| `void quit()` |  |
| `void quitSafely()` |  |
| `void setMessageLogging(@Nullable android.util.Printer)` |  |

---

### `class MemoryFile`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MemoryFile(String, int) throws java.io.IOException` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `java.io.InputStream getInputStream()` |  |
| `java.io.OutputStream getOutputStream()` |  |
| `int length()` |  |
| `int readBytes(byte[], int, int, int) throws java.io.IOException` |  |
| `void writeBytes(byte[], int, int, int) throws java.io.IOException` |  |

---

### `class final Message`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Message()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int arg1` |  |
| `int arg2` |  |
| `Object obj` |  |
| `android.os.Messenger replyTo` |  |
| `int sendingUid` |  |
| `int what` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void copyFrom(android.os.Message)` |  |
| `int describeContents()` |  |
| `Runnable getCallback()` |  |
| `android.os.Bundle getData()` |  |
| `android.os.Handler getTarget()` |  |
| `long getWhen()` |  |
| `boolean isAsynchronous()` |  |
| `static android.os.Message obtain()` |  |
| `static android.os.Message obtain(android.os.Message)` |  |
| `static android.os.Message obtain(android.os.Handler)` |  |
| `static android.os.Message obtain(android.os.Handler, Runnable)` |  |
| `static android.os.Message obtain(android.os.Handler, int)` |  |
| `static android.os.Message obtain(android.os.Handler, int, Object)` |  |
| `static android.os.Message obtain(android.os.Handler, int, int, int)` |  |
| `static android.os.Message obtain(android.os.Handler, int, int, int, Object)` |  |
| `android.os.Bundle peekData()` |  |
| `void recycle()` |  |
| `void sendToTarget()` |  |
| `void setAsynchronous(boolean)` |  |
| `void setData(android.os.Bundle)` |  |
| `void setTarget(android.os.Handler)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final MessageQueue`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addIdleHandler(@NonNull android.os.MessageQueue.IdleHandler)` |  |
| `void addOnFileDescriptorEventListener(@NonNull java.io.FileDescriptor, int, @NonNull android.os.MessageQueue.OnFileDescriptorEventListener)` |  |
| `boolean isIdle()` |  |
| `void removeIdleHandler(@NonNull android.os.MessageQueue.IdleHandler)` |  |
| `void removeOnFileDescriptorEventListener(@NonNull java.io.FileDescriptor)` |  |

---

### `interface static MessageQueue.IdleHandler`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean queueIdle()` |  |

---

### `interface static MessageQueue.OnFileDescriptorEventListener`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int EVENT_ERROR = 4` |  |
| `static final int EVENT_INPUT = 1` |  |
| `static final int EVENT_OUTPUT = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int onFileDescriptorEvents(@NonNull java.io.FileDescriptor, int)` |  |

---

### `class final Messenger`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Messenger(android.os.Handler)` |  |
| `Messenger(android.os.IBinder)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.os.IBinder getBinder()` |  |
| `static android.os.Messenger readMessengerOrNullFromParcel(android.os.Parcel)` |  |
| `void send(android.os.Message) throws android.os.RemoteException` |  |
| `static void writeMessengerOrNullToParcel(android.os.Messenger, android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class NetworkOnMainThreadException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NetworkOnMainThreadException()` |  |

---

### `class OperationCanceledException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OperationCanceledException()` |  |
| `OperationCanceledException(String)` |  |

---

### `class final Parcel`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.os.Parcelable.Creator<java.lang.String> STRING_CREATOR` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void appendFrom(android.os.Parcel, int, int)` |  |
| `int dataAvail()` |  |
| `int dataCapacity()` |  |
| `int dataPosition()` |  |
| `int dataSize()` |  |
| `void enforceInterface(String)` |  |
| `boolean hasFileDescriptors()` |  |
| `byte[] marshall()` |  |
| `void readBinderArray(@NonNull android.os.IBinder[])` |  |
| `void readBinderList(@NonNull java.util.List<android.os.IBinder>)` |  |
| `boolean readBoolean()` |  |
| `void readBooleanArray(@NonNull boolean[])` |  |
| `byte readByte()` |  |
| `void readByteArray(@NonNull byte[])` |  |
| `void readCharArray(@NonNull char[])` |  |
| `double readDouble()` |  |
| `void readDoubleArray(@NonNull double[])` |  |
| `void readException()` |  |
| `void readException(int, String)` |  |
| `android.os.ParcelFileDescriptor readFileDescriptor()` |  |
| `float readFloat()` |  |
| `void readFloatArray(@NonNull float[])` |  |
| `int readInt()` |  |
| `void readIntArray(@NonNull int[])` |  |
| `void readList(@NonNull java.util.List, @Nullable ClassLoader)` |  |
| `long readLong()` |  |
| `void readLongArray(@NonNull long[])` |  |
| `void readMap(@NonNull java.util.Map, @Nullable ClassLoader)` |  |
| `void readStringArray(@NonNull String[])` |  |
| `void readStringList(@NonNull java.util.List<java.lang.String>)` |  |
| `android.os.IBinder readStrongBinder()` |  |
| `<T> void readTypedArray(@NonNull T[], @NonNull android.os.Parcelable.Creator<T>)` |  |
| `<T> void readTypedList(@NonNull java.util.List<T>, @NonNull android.os.Parcelable.Creator<T>)` |  |
| `void recycle()` |  |
| `void setDataCapacity(int)` |  |
| `void setDataPosition(int)` |  |
| `void setDataSize(int)` |  |
| `void unmarshall(@NonNull byte[], int, int)` |  |
| `void writeArray(@Nullable Object[])` |  |
| `void writeBinderArray(@Nullable android.os.IBinder[])` |  |
| `void writeBinderList(@Nullable java.util.List<android.os.IBinder>)` |  |
| `void writeBoolean(boolean)` |  |
| `void writeBooleanArray(@Nullable boolean[])` |  |
| `void writeBundle(@Nullable android.os.Bundle)` |  |
| `void writeByte(byte)` |  |
| `void writeByteArray(@Nullable byte[])` |  |
| `void writeByteArray(@Nullable byte[], int, int)` |  |
| `void writeCharArray(@Nullable char[])` |  |
| `void writeDouble(double)` |  |
| `void writeDoubleArray(@Nullable double[])` |  |
| `void writeException(@NonNull Exception)` |  |
| `void writeFileDescriptor(@NonNull java.io.FileDescriptor)` |  |
| `void writeFloat(float)` |  |
| `void writeFloatArray(@Nullable float[])` |  |
| `void writeInt(int)` |  |
| `void writeIntArray(@Nullable int[])` |  |
| `void writeInterfaceToken(String)` |  |
| `void writeList(@Nullable java.util.List)` |  |
| `void writeLong(long)` |  |
| `void writeLongArray(@Nullable long[])` |  |
| `void writeMap(@Nullable java.util.Map)` |  |
| `void writeNoException()` |  |
| `void writeParcelable(@Nullable android.os.Parcelable, int)` |  |
| `<T extends android.os.Parcelable> void writeParcelableArray(@Nullable T[], int)` |  |
| `void writeParcelableCreator(@NonNull android.os.Parcelable)` |  |
| `<T extends android.os.Parcelable> void writeParcelableList(@Nullable java.util.List<T>, int)` |  |
| `void writePersistableBundle(@Nullable android.os.PersistableBundle)` |  |
| `void writeSerializable(@Nullable java.io.Serializable)` |  |
| `void writeSize(@NonNull android.util.Size)` |  |
| `void writeSizeF(@NonNull android.util.SizeF)` |  |
| `<T> void writeSparseArray(@Nullable android.util.SparseArray<T>)` |  |
| `void writeSparseBooleanArray(@Nullable android.util.SparseBooleanArray)` |  |
| `void writeString(@Nullable String)` |  |
| `void writeStringArray(@Nullable String[])` |  |
| `void writeStringList(@Nullable java.util.List<java.lang.String>)` |  |
| `void writeStrongBinder(android.os.IBinder)` |  |
| `void writeStrongInterface(android.os.IInterface)` |  |
| `<T extends android.os.Parcelable> void writeTypedArray(@Nullable T[], int)` |  |
| `<T extends android.os.Parcelable> void writeTypedArrayMap(@Nullable android.util.ArrayMap<java.lang.String,T>, int)` |  |
| `<T extends android.os.Parcelable> void writeTypedList(@Nullable java.util.List<T>)` |  |
| `<T extends android.os.Parcelable> void writeTypedObject(@Nullable T, int)` |  |
| `<T extends android.os.Parcelable> void writeTypedSparseArray(@Nullable android.util.SparseArray<T>, int)` |  |
| `void writeValue(@Nullable Object)` |  |

---

### `class ParcelFileDescriptor`

- **Implements:** `java.io.Closeable android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ParcelFileDescriptor(android.os.ParcelFileDescriptor)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MODE_APPEND = 33554432` |  |
| `static final int MODE_CREATE = 134217728` |  |
| `static final int MODE_READ_ONLY = 268435456` |  |
| `static final int MODE_READ_WRITE = 805306368` |  |
| `static final int MODE_TRUNCATE = 67108864` |  |
| `static final int MODE_WRITE_ONLY = 536870912` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.os.ParcelFileDescriptor adoptFd(int)` |  |
| `boolean canDetectErrors()` |  |
| `void checkError() throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `void closeWithError(String) throws java.io.IOException` |  |
| `static android.os.ParcelFileDescriptor[] createPipe() throws java.io.IOException` |  |
| `static android.os.ParcelFileDescriptor[] createReliablePipe() throws java.io.IOException` |  |
| `static android.os.ParcelFileDescriptor[] createReliableSocketPair() throws java.io.IOException` |  |
| `static android.os.ParcelFileDescriptor[] createSocketPair() throws java.io.IOException` |  |
| `int describeContents()` |  |
| `int detachFd()` |  |
| `static android.os.ParcelFileDescriptor dup(java.io.FileDescriptor) throws java.io.IOException` |  |
| `android.os.ParcelFileDescriptor dup() throws java.io.IOException` |  |
| `static android.os.ParcelFileDescriptor fromDatagramSocket(java.net.DatagramSocket)` |  |
| `static android.os.ParcelFileDescriptor fromFd(int) throws java.io.IOException` |  |
| `static android.os.ParcelFileDescriptor fromSocket(java.net.Socket)` |  |
| `int getFd()` |  |
| `java.io.FileDescriptor getFileDescriptor()` |  |
| `long getStatSize()` |  |
| `static android.os.ParcelFileDescriptor open(java.io.File, int) throws java.io.FileNotFoundException` |  |
| `static android.os.ParcelFileDescriptor open(java.io.File, int, android.os.Handler, android.os.ParcelFileDescriptor.OnCloseListener) throws java.io.IOException` |  |
| `static int parseMode(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static ParcelFileDescriptor.AutoCloseInputStream`

- **Extends:** `java.io.FileInputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ParcelFileDescriptor.AutoCloseInputStream(android.os.ParcelFileDescriptor)` |  |

---

### `class static ParcelFileDescriptor.AutoCloseOutputStream`

- **Extends:** `java.io.FileOutputStream`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ParcelFileDescriptor.AutoCloseOutputStream(android.os.ParcelFileDescriptor)` |  |

---

### `class static ParcelFileDescriptor.FileDescriptorDetachedException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ParcelFileDescriptor.FileDescriptorDetachedException()` |  |

---

### `interface static ParcelFileDescriptor.OnCloseListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onClose(java.io.IOException)` |  |

---

### `class ParcelFormatException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ParcelFormatException()` |  |
| `ParcelFormatException(String)` |  |

---

### `class final ParcelUuid`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ParcelUuid(java.util.UUID)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static android.os.ParcelUuid fromString(String)` |  |
| `java.util.UUID getUuid()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface Parcelable`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CONTENTS_FILE_DESCRIPTOR = 1` |  |
| `static final int PARCELABLE_WRITE_RETURN_VALUE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface static Parcelable.ClassLoaderCreator<T>`

- **Extends:** `android.os.Parcelable.Creator<T>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T createFromParcel(android.os.Parcel, ClassLoader)` |  |

---

### `interface static Parcelable.Creator<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T createFromParcel(android.os.Parcel)` |  |
| `T[] newArray(int)` |  |

---

### `class PatternMatcher`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PatternMatcher(String, int)` |  |
| `PatternMatcher(android.os.Parcel)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PATTERN_ADVANCED_GLOB = 3` |  |
| `static final int PATTERN_LITERAL = 0` |  |
| `static final int PATTERN_PREFIX = 1` |  |
| `static final int PATTERN_SIMPLE_GLOB = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `final String getPath()` |  |
| `final int getType()` |  |
| `boolean match(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final PersistableBundle`

- **Extends:** `android.os.BaseBundle`
- **Implements:** `java.lang.Cloneable android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PersistableBundle()` |  |
| `PersistableBundle(int)` |  |
| `PersistableBundle(android.os.PersistableBundle)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.os.PersistableBundle EMPTY` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `android.os.PersistableBundle deepCopy()` |  |
| `int describeContents()` |  |
| `void putPersistableBundle(@Nullable String, @Nullable android.os.PersistableBundle)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |
| `void writeToStream(@NonNull java.io.OutputStream) throws java.io.IOException` |  |

---

### `class final PowerManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACQUIRE_CAUSES_WAKEUP = 268435456` |  |
| `static final String ACTION_DEVICE_IDLE_MODE_CHANGED = "android.os.action.DEVICE_IDLE_MODE_CHANGED"` |  |
| `static final String ACTION_POWER_SAVE_MODE_CHANGED = "android.os.action.POWER_SAVE_MODE_CHANGED"` |  |
| `static final int LOCATION_MODE_ALL_DISABLED_WHEN_SCREEN_OFF = 2` |  |
| `static final int LOCATION_MODE_FOREGROUND_ONLY = 3` |  |
| `static final int LOCATION_MODE_GPS_DISABLED_WHEN_SCREEN_OFF = 1` |  |
| `static final int LOCATION_MODE_NO_CHANGE = 0` |  |
| `static final int LOCATION_MODE_THROTTLE_REQUESTS_WHEN_SCREEN_OFF = 4` |  |
| `static final int ON_AFTER_RELEASE = 536870912` |  |
| `static final int PARTIAL_WAKE_LOCK = 1` |  |
| `static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32` |  |
| `static final int RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY = 1` |  |
| `static final int THERMAL_STATUS_CRITICAL = 4` |  |
| `static final int THERMAL_STATUS_EMERGENCY = 5` |  |
| `static final int THERMAL_STATUS_LIGHT = 1` |  |
| `static final int THERMAL_STATUS_MODERATE = 2` |  |
| `static final int THERMAL_STATUS_NONE = 0` |  |
| `static final int THERMAL_STATUS_SEVERE = 3` |  |
| `static final int THERMAL_STATUS_SHUTDOWN = 6` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addThermalStatusListener(@NonNull android.os.PowerManager.OnThermalStatusChangedListener)` |  |
| `void addThermalStatusListener(@NonNull java.util.concurrent.Executor, @NonNull android.os.PowerManager.OnThermalStatusChangedListener)` |  |
| `int getCurrentThermalStatus()` |  |
| `int getLocationPowerSaveMode()` |  |
| `float getThermalHeadroom(@IntRange(from=0, to=60) int)` |  |
| `boolean isDeviceIdleMode()` |  |
| `boolean isIgnoringBatteryOptimizations(String)` |  |
| `boolean isInteractive()` |  |
| `boolean isPowerSaveMode()` |  |
| `boolean isRebootingUserspaceSupported()` |  |
| `boolean isSustainedPerformanceModeSupported()` |  |
| `boolean isWakeLockLevelSupported(int)` |  |
| `android.os.PowerManager.WakeLock newWakeLock(int, String)` |  |
| `void removeThermalStatusListener(@NonNull android.os.PowerManager.OnThermalStatusChangedListener)` |  |

---

### `interface static PowerManager.OnThermalStatusChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onThermalStatusChanged(int)` |  |

---

### `class final PowerManager.WakeLock`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void acquire()` |  |
| `void acquire(long)` |  |
| `boolean isHeld()` |  |
| `void release()` |  |
| `void release(int)` |  |
| `void setReferenceCounted(boolean)` |  |
| `void setWorkSource(android.os.WorkSource)` |  |

---

### `class Process`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Process()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BLUETOOTH_UID = 1002` |  |
| `static final int FIRST_APPLICATION_UID = 10000` |  |
| `static final int INVALID_UID = -1` |  |
| `static final int LAST_APPLICATION_UID = 19999` |  |
| `static final int PHONE_UID = 1001` |  |
| `static final int ROOT_UID = 0` |  |
| `static final int SHELL_UID = 2000` |  |
| `static final int SIGNAL_KILL = 9` |  |
| `static final int SIGNAL_QUIT = 3` |  |
| `static final int SIGNAL_USR1 = 10` |  |
| `static final int SYSTEM_UID = 1000` |  |
| `static final int THREAD_PRIORITY_AUDIO = -16` |  |
| `static final int THREAD_PRIORITY_BACKGROUND = 10` |  |
| `static final int THREAD_PRIORITY_DEFAULT = 0` |  |
| `static final int THREAD_PRIORITY_DISPLAY = -4` |  |
| `static final int THREAD_PRIORITY_FOREGROUND = -2` |  |
| `static final int THREAD_PRIORITY_LESS_FAVORABLE = 1` |  |
| `static final int THREAD_PRIORITY_LOWEST = 19` |  |
| `static final int THREAD_PRIORITY_MORE_FAVORABLE = -1` |  |
| `static final int THREAD_PRIORITY_URGENT_AUDIO = -19` |  |
| `static final int THREAD_PRIORITY_URGENT_DISPLAY = -8` |  |
| `static final int THREAD_PRIORITY_VIDEO = -10` |  |
| `static final int WIFI_UID = 1010` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static final long getElapsedCpuTime()` |  |
| `static final int[] getExclusiveCores()` |  |
| `static final int getGidForName(String)` |  |
| `static final long getStartElapsedRealtime()` |  |
| `static final long getStartUptimeMillis()` |  |
| `static final int getThreadPriority(int) throws java.lang.IllegalArgumentException` |  |
| `static final int getUidForName(String)` |  |
| `static final boolean is64Bit()` |  |
| `static boolean isApplicationUid(int)` |  |
| `static final boolean isIsolated()` |  |
| `static final void killProcess(int)` |  |
| `static final int myPid()` |  |
| `static final int myTid()` |  |
| `static final int myUid()` |  |
| `static android.os.UserHandle myUserHandle()` |  |
| `static final void sendSignal(int, int)` |  |
| `static final void setThreadPriority(int, int) throws java.lang.IllegalArgumentException, java.lang.SecurityException` |  |
| `static final void setThreadPriority(int) throws java.lang.IllegalArgumentException, java.lang.SecurityException` |  |

---

### `class abstract ProxyFileDescriptorCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ProxyFileDescriptorCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onFsync() throws android.system.ErrnoException` |  |
| `long onGetSize() throws android.system.ErrnoException` |  |
| `int onRead(long, int, byte[]) throws android.system.ErrnoException` |  |
| `abstract void onRelease()` |  |
| `int onWrite(long, int, byte[]) throws android.system.ErrnoException` |  |

---

### `class RecoverySystem`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void rebootWipeCache(android.content.Context) throws java.io.IOException` |  |
| `static void rebootWipeUserData(android.content.Context) throws java.io.IOException` |  |
| `static void verifyPackage(java.io.File, android.os.RecoverySystem.ProgressListener, java.io.File) throws java.security.GeneralSecurityException, java.io.IOException` |  |

---

### `interface static RecoverySystem.ProgressListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onProgress(int)` |  |

---

### `class RemoteCallbackList<E`

- **Extends:** `android.os.IInterface>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RemoteCallbackList()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int beginBroadcast()` |  |
| `void finishBroadcast()` |  |
| `Object getBroadcastCookie(int)` |  |
| `E getBroadcastItem(int)` |  |
| `Object getRegisteredCallbackCookie(int)` |  |
| `int getRegisteredCallbackCount()` |  |
| `E getRegisteredCallbackItem(int)` |  |
| `void kill()` |  |
| `void onCallbackDied(E)` |  |
| `void onCallbackDied(E, Object)` |  |
| `boolean register(E)` |  |
| `boolean register(E, Object)` |  |
| `boolean unregister(E)` |  |

---

### `class RemoteException`

- **Extends:** `android.util.AndroidException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RemoteException()` |  |
| `RemoteException(String)` |  |

---

### `class ResultReceiver`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ResultReceiver(android.os.Handler)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void onReceiveResult(int, android.os.Bundle)` |  |
| `void send(int, android.os.Bundle)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final SharedMemory`

- **Implements:** `java.io.Closeable android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `int describeContents()` |  |
| `int getSize()` |  |
| `boolean setProtect(int)` |  |
| `static void unmap(@NonNull java.nio.ByteBuffer)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class StatFs`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StatFs(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getAvailableBlocksLong()` |  |
| `long getAvailableBytes()` |  |
| `long getBlockCountLong()` |  |
| `long getBlockSizeLong()` |  |
| `long getFreeBlocksLong()` |  |
| `long getFreeBytes()` |  |
| `long getTotalBytes()` |  |
| `void restat(String)` |  |

---

### `class final StrictMode`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.os.StrictMode.ThreadPolicy allowThreadDiskReads()` |  |
| `static android.os.StrictMode.ThreadPolicy allowThreadDiskWrites()` |  |
| `static void enableDefaults()` |  |
| `static android.os.StrictMode.ThreadPolicy getThreadPolicy()` |  |
| `static android.os.StrictMode.VmPolicy getVmPolicy()` |  |
| `static void noteSlowCall(String)` |  |
| `static void setThreadPolicy(android.os.StrictMode.ThreadPolicy)` |  |
| `static void setVmPolicy(android.os.StrictMode.VmPolicy)` |  |

---

### `interface static StrictMode.OnThreadViolationListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onThreadViolation(android.os.strictmode.Violation)` |  |

---

### `interface static StrictMode.OnVmViolationListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onVmViolation(android.os.strictmode.Violation)` |  |

---

### `class static final StrictMode.ThreadPolicy`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.os.StrictMode.ThreadPolicy LAX` |  |

---

### `class static final StrictMode.ThreadPolicy.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StrictMode.ThreadPolicy.Builder()` |  |
| `StrictMode.ThreadPolicy.Builder(android.os.StrictMode.ThreadPolicy)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.os.StrictMode.ThreadPolicy build()` |  |

---

### `class static final StrictMode.VmPolicy`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.os.StrictMode.VmPolicy LAX` |  |

---

### `class static final StrictMode.VmPolicy.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StrictMode.VmPolicy.Builder()` |  |
| `StrictMode.VmPolicy.Builder(android.os.StrictMode.VmPolicy)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.os.StrictMode.VmPolicy build()` |  |

---

### `class final SystemClock`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static long currentThreadTimeMillis()` |  |
| `static long elapsedRealtime()` |  |
| `static long elapsedRealtimeNanos()` |  |
| `static boolean setCurrentTimeMillis(long)` |  |
| `static void sleep(long)` |  |
| `static long uptimeMillis()` |  |

---

### `class TestLooperManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void execute(android.os.Message)` |  |
| `android.os.MessageQueue getMessageQueue()` |  |
| `boolean hasMessages(android.os.Handler, Object, int)` |  |
| `boolean hasMessages(android.os.Handler, Object, Runnable)` |  |
| `android.os.Message next()` |  |
| `void recycle(android.os.Message)` |  |
| `void release()` |  |

---

### `class abstract TokenWatcher`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TokenWatcher(android.os.Handler, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void acquire(android.os.IBinder, String)` |  |
| `abstract void acquired()` |  |
| `void cleanup(android.os.IBinder, boolean)` |  |
| `void dump()` |  |
| `void dump(java.io.PrintWriter)` |  |
| `boolean isAcquired()` |  |
| `void release(android.os.IBinder)` |  |
| `abstract void released()` |  |

---

### `class final Trace`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void beginAsyncSection(@NonNull String, int)` |  |
| `static void beginSection(@NonNull String)` |  |
| `static void endAsyncSection(@NonNull String, int)` |  |
| `static void endSection()` |  |
| `static boolean isEnabled()` |  |
| `static void setCounter(@NonNull String, long)` |  |

---

### `class TransactionTooLargeException`

- **Extends:** `android.os.RemoteException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TransactionTooLargeException()` |  |
| `TransactionTooLargeException(String)` |  |

---

### `class final UserHandle`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UserHandle(android.os.Parcel)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static android.os.UserHandle getUserHandleForUid(int)` |  |
| `static android.os.UserHandle readFromParcel(android.os.Parcel)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |
| `static void writeToParcel(android.os.UserHandle, android.os.Parcel)` |  |

---

### `class UserManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ALLOW_PARENT_PROFILE_APP_LINKING = "allow_parent_profile_app_linking"` |  |
| `static final String DISALLOW_ADD_USER = "no_add_user"` |  |
| `static final String DISALLOW_ADJUST_VOLUME = "no_adjust_volume"` |  |
| `static final String DISALLOW_AIRPLANE_MODE = "no_airplane_mode"` |  |
| `static final String DISALLOW_AMBIENT_DISPLAY = "no_ambient_display"` |  |
| `static final String DISALLOW_APPS_CONTROL = "no_control_apps"` |  |
| `static final String DISALLOW_AUTOFILL = "no_autofill"` |  |
| `static final String DISALLOW_BLUETOOTH = "no_bluetooth"` |  |
| `static final String DISALLOW_BLUETOOTH_SHARING = "no_bluetooth_sharing"` |  |
| `static final String DISALLOW_CONFIG_BLUETOOTH = "no_config_bluetooth"` |  |
| `static final String DISALLOW_CONFIG_BRIGHTNESS = "no_config_brightness"` |  |
| `static final String DISALLOW_CONFIG_CELL_BROADCASTS = "no_config_cell_broadcasts"` |  |
| `static final String DISALLOW_CONFIG_CREDENTIALS = "no_config_credentials"` |  |
| `static final String DISALLOW_CONFIG_DATE_TIME = "no_config_date_time"` |  |
| `static final String DISALLOW_CONFIG_LOCALE = "no_config_locale"` |  |
| `static final String DISALLOW_CONFIG_LOCATION = "no_config_location"` |  |
| `static final String DISALLOW_CONFIG_MOBILE_NETWORKS = "no_config_mobile_networks"` |  |
| `static final String DISALLOW_CONFIG_PRIVATE_DNS = "disallow_config_private_dns"` |  |
| `static final String DISALLOW_CONFIG_SCREEN_TIMEOUT = "no_config_screen_timeout"` |  |
| `static final String DISALLOW_CONFIG_TETHERING = "no_config_tethering"` |  |
| `static final String DISALLOW_CONFIG_VPN = "no_config_vpn"` |  |
| `static final String DISALLOW_CONFIG_WIFI = "no_config_wifi"` |  |
| `static final String DISALLOW_CONTENT_CAPTURE = "no_content_capture"` |  |
| `static final String DISALLOW_CONTENT_SUGGESTIONS = "no_content_suggestions"` |  |
| `static final String DISALLOW_CREATE_WINDOWS = "no_create_windows"` |  |
| `static final String DISALLOW_CROSS_PROFILE_COPY_PASTE = "no_cross_profile_copy_paste"` |  |
| `static final String DISALLOW_DATA_ROAMING = "no_data_roaming"` |  |
| `static final String DISALLOW_DEBUGGING_FEATURES = "no_debugging_features"` |  |
| `static final String DISALLOW_FACTORY_RESET = "no_factory_reset"` |  |
| `static final String DISALLOW_FUN = "no_fun"` |  |
| `static final String DISALLOW_INSTALL_APPS = "no_install_apps"` |  |
| `static final String DISALLOW_INSTALL_UNKNOWN_SOURCES = "no_install_unknown_sources"` |  |
| `static final String DISALLOW_INSTALL_UNKNOWN_SOURCES_GLOBALLY = "no_install_unknown_sources_globally"` |  |
| `static final String DISALLOW_MODIFY_ACCOUNTS = "no_modify_accounts"` |  |
| `static final String DISALLOW_MOUNT_PHYSICAL_MEDIA = "no_physical_media"` |  |
| `static final String DISALLOW_NETWORK_RESET = "no_network_reset"` |  |
| `static final String DISALLOW_OUTGOING_BEAM = "no_outgoing_beam"` |  |
| `static final String DISALLOW_OUTGOING_CALLS = "no_outgoing_calls"` |  |
| `static final String DISALLOW_PRINTING = "no_printing"` |  |
| `static final String DISALLOW_REMOVE_USER = "no_remove_user"` |  |
| `static final String DISALLOW_SAFE_BOOT = "no_safe_boot"` |  |
| `static final String DISALLOW_SET_USER_ICON = "no_set_user_icon"` |  |
| `static final String DISALLOW_SET_WALLPAPER = "no_set_wallpaper"` |  |
| `static final String DISALLOW_SHARE_INTO_MANAGED_PROFILE = "no_sharing_into_profile"` |  |
| `static final String DISALLOW_SHARE_LOCATION = "no_share_location"` |  |
| `static final String DISALLOW_SMS = "no_sms"` |  |
| `static final String DISALLOW_SYSTEM_ERROR_DIALOGS = "no_system_error_dialogs"` |  |
| `static final String DISALLOW_UNIFIED_PASSWORD = "no_unified_password"` |  |
| `static final String DISALLOW_UNINSTALL_APPS = "no_uninstall_apps"` |  |
| `static final String DISALLOW_UNMUTE_MICROPHONE = "no_unmute_microphone"` |  |
| `static final String DISALLOW_USB_FILE_TRANSFER = "no_usb_file_transfer"` |  |
| `static final String DISALLOW_USER_SWITCH = "no_user_switch"` |  |
| `static final String ENSURE_VERIFY_APPS = "ensure_verify_apps"` |  |
| `static final String KEY_RESTRICTIONS_PENDING = "restrictions_pending"` |  |
| `static final int QUIET_MODE_DISABLE_ONLY_IF_CREDENTIAL_NOT_REQUIRED = 1` |  |
| `static final int USER_CREATION_FAILED_NOT_PERMITTED = 1` |  |
| `static final int USER_CREATION_FAILED_NO_MORE_USERS = 2` |  |
| `static final int USER_OPERATION_ERROR_CURRENT_USER = 4` |  |
| `static final int USER_OPERATION_ERROR_LOW_STORAGE = 5` |  |
| `static final int USER_OPERATION_ERROR_MANAGED_PROFILE = 2` |  |
| `static final int USER_OPERATION_ERROR_MAX_RUNNING_USERS = 3` |  |
| `static final int USER_OPERATION_ERROR_MAX_USERS = 6` |  |
| `static final int USER_OPERATION_ERROR_UNKNOWN = 1` |  |
| `static final int USER_OPERATION_SUCCESS = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.content.Intent createUserCreationIntent(@Nullable String, @Nullable String, @Nullable String, @Nullable android.os.PersistableBundle)` |  |
| `long getSerialNumberForUser(android.os.UserHandle)` |  |
| `long getUserCreationTime(android.os.UserHandle)` |  |
| `android.os.UserHandle getUserForSerialNumber(long)` |  |
| `java.util.List<android.os.UserHandle> getUserProfiles()` |  |
| `android.os.Bundle getUserRestrictions()` |  |
| `boolean hasUserRestriction(String)` |  |
| `boolean isDemoUser()` |  |
| `boolean isManagedProfile()` |  |
| `boolean isQuietModeEnabled(android.os.UserHandle)` |  |
| `boolean isSystemUser()` |  |
| `boolean isUserAGoat()` |  |
| `boolean isUserUnlocked()` |  |
| `boolean requestQuietModeEnabled(boolean, @NonNull android.os.UserHandle, int)` |  |
| `static boolean supportsMultipleUsers()` |  |

---

### `class static UserManager.UserOperationException`

- **Extends:** `java.lang.RuntimeException`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getUserOperationResult()` |  |

---

### `class final VibrationAttributes`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FLAG_BYPASS_INTERRUPTION_POLICY = 1` |  |
| `static final int USAGE_ALARM = 17` |  |
| `static final int USAGE_CLASS_ALARM = 1` |  |
| `static final int USAGE_CLASS_FEEDBACK = 2` |  |
| `static final int USAGE_CLASS_MASK = 15` |  |
| `static final int USAGE_CLASS_UNKNOWN = 0` |  |
| `static final int USAGE_COMMUNICATION_REQUEST = 65` |  |
| `static final int USAGE_HARDWARE_FEEDBACK = 50` |  |
| `static final int USAGE_NOTIFICATION = 49` |  |
| `static final int USAGE_PHYSICAL_EMULATION = 34` |  |
| `static final int USAGE_RINGTONE = 33` |  |
| `static final int USAGE_TOUCH = 18` |  |
| `static final int USAGE_UNKNOWN = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getFlags()` |  |
| `int getUsage()` |  |
| `int getUsageClass()` |  |
| `boolean isFlagSet(int)` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final VibrationAttributes.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `VibrationAttributes.Builder()` |  |
| `VibrationAttributes.Builder(@Nullable android.os.VibrationAttributes)` |  |

---

### `class abstract VibrationEffect`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DEFAULT_AMPLITUDE = -1` |  |
| `static final int EFFECT_CLICK = 0` |  |
| `static final int EFFECT_DOUBLE_CLICK = 1` |  |
| `static final int EFFECT_HEAVY_CLICK = 5` |  |
| `static final int EFFECT_TICK = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.os.VibrationEffect createOneShot(long, int)` |  |
| `static android.os.VibrationEffect createWaveform(long[], int)` |  |
| `static android.os.VibrationEffect createWaveform(long[], int[], int)` |  |
| `int describeContents()` |  |

---

### `class static final VibrationEffect.Composition`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PRIMITIVE_CLICK = 1` |  |
| `static final int PRIMITIVE_QUICK_FALL = 6` |  |
| `static final int PRIMITIVE_QUICK_RISE = 4` |  |
| `static final int PRIMITIVE_SLOW_RISE = 5` |  |
| `static final int PRIMITIVE_TICK = 7` |  |

---

### `class abstract Vibrator`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int VIBRATION_EFFECT_SUPPORT_NO = 2` |  |
| `static final int VIBRATION_EFFECT_SUPPORT_UNKNOWN = 0` |  |
| `static final int VIBRATION_EFFECT_SUPPORT_YES = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int areAllEffectsSupported(@NonNull int...)` |  |
| `final boolean areAllPrimitivesSupported(@NonNull int...)` |  |
| `abstract boolean hasAmplitudeControl()` |  |
| `abstract boolean hasVibrator()` |  |

---

### `class WorkSource`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WorkSource()` |  |
| `WorkSource(android.os.WorkSource)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean add(android.os.WorkSource)` |  |
| `void clear()` |  |
| `int describeContents()` |  |
| `boolean diff(android.os.WorkSource)` |  |
| `boolean remove(android.os.WorkSource)` |  |
| `void set(android.os.WorkSource)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.os.health`

### `class HealthStats`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getDataType()` |  |
| `long getMeasurement(int)` |  |
| `int getMeasurementKeyAt(int)` |  |
| `int getMeasurementKeyCount()` |  |
| `java.util.Map<java.lang.String,java.lang.Long> getMeasurements(int)` |  |
| `int getMeasurementsKeyAt(int)` |  |
| `int getMeasurementsKeyCount()` |  |
| `java.util.Map<java.lang.String,android.os.health.HealthStats> getStats(int)` |  |
| `int getStatsKeyAt(int)` |  |
| `int getStatsKeyCount()` |  |
| `android.os.health.TimerStat getTimer(int)` |  |
| `int getTimerCount(int)` |  |
| `int getTimerKeyAt(int)` |  |
| `int getTimerKeyCount()` |  |
| `long getTimerTime(int)` |  |
| `java.util.Map<java.lang.String,android.os.health.TimerStat> getTimers(int)` |  |
| `int getTimersKeyAt(int)` |  |
| `int getTimersKeyCount()` |  |
| `boolean hasMeasurement(int)` |  |
| `boolean hasMeasurements(int)` |  |
| `boolean hasStats(int)` |  |
| `boolean hasTimer(int)` |  |
| `boolean hasTimers(int)` |  |

---

### `class final PackageHealthStats`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MEASUREMENTS_WAKEUP_ALARMS_COUNT = 40002` |  |
| `static final int STATS_SERVICES = 40001` |  |

---

### `class final PidHealthStats`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MEASUREMENT_WAKE_NESTING_COUNT = 20001` |  |
| `static final int MEASUREMENT_WAKE_START_MS = 20003` |  |
| `static final int MEASUREMENT_WAKE_SUM_MS = 20002` |  |

---

### `class final ProcessHealthStats`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MEASUREMENT_ANR_COUNT = 30005` |  |
| `static final int MEASUREMENT_CRASHES_COUNT = 30004` |  |
| `static final int MEASUREMENT_FOREGROUND_MS = 30006` |  |
| `static final int MEASUREMENT_STARTS_COUNT = 30003` |  |
| `static final int MEASUREMENT_SYSTEM_TIME_MS = 30002` |  |
| `static final int MEASUREMENT_USER_TIME_MS = 30001` |  |

---

### `class final ServiceHealthStats`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MEASUREMENT_LAUNCH_COUNT = 50002` |  |
| `static final int MEASUREMENT_START_SERVICE_COUNT = 50001` |  |

---

### `class SystemHealthManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.os.health.HealthStats takeMyUidSnapshot()` |  |
| `android.os.health.HealthStats takeUidSnapshot(int)` |  |
| `android.os.health.HealthStats[] takeUidSnapshots(int[])` |  |

---

### `class final TimerStat`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TimerStat()` |  |
| `TimerStat(int, long)` |  |
| `TimerStat(android.os.Parcel)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCount()` |  |
| `long getTime()` |  |
| `void setCount(int)` |  |
| `void setTime(long)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final UidHealthStats`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MEASUREMENT_BLUETOOTH_IDLE_MS = 10020` |  |
| `static final int MEASUREMENT_BLUETOOTH_POWER_MAMS = 10023` |  |
| `static final int MEASUREMENT_BLUETOOTH_RX_BYTES = 10052` |  |
| `static final int MEASUREMENT_BLUETOOTH_RX_MS = 10021` |  |
| `static final int MEASUREMENT_BLUETOOTH_RX_PACKETS = 10058` |  |
| `static final int MEASUREMENT_BLUETOOTH_TX_BYTES = 10053` |  |
| `static final int MEASUREMENT_BLUETOOTH_TX_MS = 10022` |  |
| `static final int MEASUREMENT_BLUETOOTH_TX_PACKETS = 10059` |  |
| `static final int MEASUREMENT_BUTTON_USER_ACTIVITY_COUNT = 10046` |  |
| `static final int MEASUREMENT_MOBILE_IDLE_MS = 10024` |  |
| `static final int MEASUREMENT_MOBILE_POWER_MAMS = 10027` |  |
| `static final int MEASUREMENT_MOBILE_RX_BYTES = 10048` |  |
| `static final int MEASUREMENT_MOBILE_RX_MS = 10025` |  |
| `static final int MEASUREMENT_MOBILE_RX_PACKETS = 10054` |  |
| `static final int MEASUREMENT_MOBILE_TX_BYTES = 10049` |  |
| `static final int MEASUREMENT_MOBILE_TX_MS = 10026` |  |
| `static final int MEASUREMENT_MOBILE_TX_PACKETS = 10055` |  |
| `static final int MEASUREMENT_OTHER_USER_ACTIVITY_COUNT = 10045` |  |
| `static final int MEASUREMENT_REALTIME_BATTERY_MS = 10001` |  |
| `static final int MEASUREMENT_REALTIME_SCREEN_OFF_BATTERY_MS = 10003` |  |
| `static final int MEASUREMENT_SYSTEM_CPU_TIME_MS = 10063` |  |
| `static final int MEASUREMENT_TOUCH_USER_ACTIVITY_COUNT = 10047` |  |
| `static final int MEASUREMENT_UPTIME_BATTERY_MS = 10002` |  |
| `static final int MEASUREMENT_UPTIME_SCREEN_OFF_BATTERY_MS = 10004` |  |
| `static final int MEASUREMENT_USER_CPU_TIME_MS = 10062` |  |
| `static final int MEASUREMENT_WIFI_FULL_LOCK_MS = 10029` |  |
| `static final int MEASUREMENT_WIFI_IDLE_MS = 10016` |  |
| `static final int MEASUREMENT_WIFI_MULTICAST_MS = 10031` |  |
| `static final int MEASUREMENT_WIFI_POWER_MAMS = 10019` |  |
| `static final int MEASUREMENT_WIFI_RUNNING_MS = 10028` |  |
| `static final int MEASUREMENT_WIFI_RX_BYTES = 10050` |  |
| `static final int MEASUREMENT_WIFI_RX_MS = 10017` |  |
| `static final int MEASUREMENT_WIFI_RX_PACKETS = 10056` |  |
| `static final int MEASUREMENT_WIFI_TX_BYTES = 10051` |  |
| `static final int MEASUREMENT_WIFI_TX_MS = 10018` |  |
| `static final int MEASUREMENT_WIFI_TX_PACKETS = 10057` |  |
| `static final int STATS_PACKAGES = 10015` |  |
| `static final int STATS_PIDS = 10013` |  |
| `static final int STATS_PROCESSES = 10014` |  |
| `static final int TIMERS_JOBS = 10010` |  |
| `static final int TIMERS_SENSORS = 10012` |  |
| `static final int TIMERS_SYNCS = 10009` |  |
| `static final int TIMERS_WAKELOCKS_DRAW = 10008` |  |
| `static final int TIMERS_WAKELOCKS_FULL = 10005` |  |
| `static final int TIMERS_WAKELOCKS_PARTIAL = 10006` |  |
| `static final int TIMERS_WAKELOCKS_WINDOW = 10007` |  |
| `static final int TIMER_AUDIO = 10032` |  |
| `static final int TIMER_BLUETOOTH_SCAN = 10037` |  |
| `static final int TIMER_CAMERA = 10035` |  |
| `static final int TIMER_FLASHLIGHT = 10034` |  |
| `static final int TIMER_FOREGROUND_ACTIVITY = 10036` |  |
| `static final int TIMER_GPS_SENSOR = 10011` |  |
| `static final int TIMER_MOBILE_RADIO_ACTIVE = 10061` |  |
| `static final int TIMER_PROCESS_STATE_BACKGROUND_MS = 10042` |  |
| `static final int TIMER_PROCESS_STATE_CACHED_MS = 10043` |  |
| `static final int TIMER_PROCESS_STATE_FOREGROUND_MS = 10041` |  |
| `static final int TIMER_PROCESS_STATE_FOREGROUND_SERVICE_MS = 10039` |  |
| `static final int TIMER_PROCESS_STATE_TOP_MS = 10038` |  |
| `static final int TIMER_PROCESS_STATE_TOP_SLEEPING_MS = 10040` |  |
| `static final int TIMER_VIBRATOR = 10044` |  |
| `static final int TIMER_VIDEO = 10033` |  |
| `static final int TIMER_WIFI_SCAN = 10030` |  |

---

## Package: `android.os.storage`

### `class abstract OnObbStateChangeListener`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OnObbStateChangeListener()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ERROR_ALREADY_MOUNTED = 24` |  |
| `static final int ERROR_COULD_NOT_MOUNT = 21` |  |
| `static final int ERROR_COULD_NOT_UNMOUNT = 22` |  |
| `static final int ERROR_INTERNAL = 20` |  |
| `static final int ERROR_NOT_MOUNTED = 23` |  |
| `static final int ERROR_PERMISSION_DENIED = 25` |  |
| `static final int MOUNTED = 1` |  |
| `static final int UNMOUNTED = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onObbStateChange(String, int)` |  |

---

### `class StorageManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_MANAGE_STORAGE = "android.os.storage.action.MANAGE_STORAGE"` |  |
| `static final String EXTRA_REQUESTED_BYTES = "android.os.storage.extra.REQUESTED_BYTES"` |  |
| `static final String EXTRA_UUID = "android.os.storage.extra.UUID"` |  |
| `static final java.util.UUID UUID_DEFAULT` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getMountedObbPath(String)` |  |
| `boolean isAllocationSupported(@NonNull java.io.FileDescriptor)` |  |
| `boolean isCacheBehaviorGroup(java.io.File) throws java.io.IOException` |  |
| `boolean isCacheBehaviorTombstone(java.io.File) throws java.io.IOException` |  |
| `boolean isCheckpointSupported()` |  |
| `boolean isEncrypted(java.io.File)` |  |
| `boolean isObbMounted(String)` |  |
| `boolean mountObb(String, String, android.os.storage.OnObbStateChangeListener)` |  |
| `void registerStorageVolumeCallback(@NonNull java.util.concurrent.Executor, @NonNull android.os.storage.StorageManager.StorageVolumeCallback)` |  |
| `void setCacheBehaviorGroup(java.io.File, boolean) throws java.io.IOException` |  |
| `void setCacheBehaviorTombstone(java.io.File, boolean) throws java.io.IOException` |  |
| `boolean unmountObb(String, boolean, android.os.storage.OnObbStateChangeListener)` |  |
| `void unregisterStorageVolumeCallback(@NonNull android.os.storage.StorageManager.StorageVolumeCallback)` |  |

---

### `class static StorageManager.StorageVolumeCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StorageManager.StorageVolumeCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onStateChanged(@NonNull android.os.storage.StorageVolume)` |  |

---

### `class final StorageVolume`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_STORAGE_VOLUME = "android.os.storage.extra.STORAGE_VOLUME"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getDescription(android.content.Context)` |  |
| `String getState()` |  |
| `boolean isEmulated()` |  |
| `boolean isPrimary()` |  |
| `boolean isRemovable()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.os.strictmode`

### `class final CleartextNetworkViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final ContentUriWithoutPermissionViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final CredentialProtectedWhileLockedViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final CustomViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final DiskReadViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final DiskWriteViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final FileUriExposedViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final ImplicitDirectBootViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class InstanceCountViolation`

- **Extends:** `android.os.strictmode.Violation`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getNumberOfInstances()` |  |

---

### `class final IntentReceiverLeakedViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final LeakedClosableViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final NetworkViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final NonSdkApiUsedViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final ResourceMismatchViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final ServiceConnectionLeakedViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final SqliteObjectLeakedViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final UnbufferedIoViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class final UntaggedSocketViolation`

- **Extends:** `android.os.strictmode.Violation`

---

### `class abstract Violation`

- **Extends:** `java.lang.Throwable`

---

### `class final WebViewMethodCalledOnWrongThreadViolation`

- **Extends:** `android.os.strictmode.Violation`

---

## Package: `android.security`

### `class final AttestedKeyPair`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AttestedKeyPair(@Nullable java.security.KeyPair, @NonNull java.util.List<java.security.cert.Certificate>)` |  |

---

### `class ConfirmationAlreadyPresentingException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConfirmationAlreadyPresentingException()` |  |
| `ConfirmationAlreadyPresentingException(String)` |  |

---

### `class abstract ConfirmationCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConfirmationCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCanceled()` |  |
| `void onConfirmed(@NonNull byte[])` |  |
| `void onDismissed()` |  |
| `void onError(Throwable)` |  |

---

### `class ConfirmationNotAvailableException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConfirmationNotAvailableException()` |  |
| `ConfirmationNotAvailableException(String)` |  |

---

### `class ConfirmationPrompt`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancelPrompt()` |  |
| `static boolean isSupported(android.content.Context)` |  |
| `void presentPrompt(@NonNull java.util.concurrent.Executor, @NonNull android.security.ConfirmationCallback) throws android.security.ConfirmationAlreadyPresentingException, android.security.ConfirmationNotAvailableException` |  |

---

### `class static final ConfirmationPrompt.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConfirmationPrompt.Builder(android.content.Context)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.security.ConfirmationPrompt build()` |  |
| `android.security.ConfirmationPrompt.Builder setExtraData(byte[])` |  |
| `android.security.ConfirmationPrompt.Builder setPromptText(CharSequence)` |  |

---

### `class final FileIntegrityManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isApkVeritySupported()` |  |

---

### `class final KeyChain`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyChain()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_KEYCHAIN_CHANGED = "android.security.action.KEYCHAIN_CHANGED"` |  |
| `static final String ACTION_KEY_ACCESS_CHANGED = "android.security.action.KEY_ACCESS_CHANGED"` |  |
| `static final String ACTION_TRUST_STORE_CHANGED = "android.security.action.TRUST_STORE_CHANGED"` |  |
| `static final String EXTRA_CERTIFICATE = "CERT"` |  |
| `static final String EXTRA_KEY_ACCESSIBLE = "android.security.extra.KEY_ACCESSIBLE"` |  |
| `static final String EXTRA_KEY_ALIAS = "android.security.extra.KEY_ALIAS"` |  |
| `static final String EXTRA_NAME = "name"` |  |
| `static final String EXTRA_PKCS12 = "PKCS12"` |  |
| `static final String KEY_ALIAS_SELECTION_DENIED = "android:alias-selection-denied"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void choosePrivateKeyAlias(@NonNull android.app.Activity, @NonNull android.security.KeyChainAliasCallback, @Nullable String[], @Nullable java.security.Principal[], @Nullable String, int, @Nullable String)` |  |
| `static void choosePrivateKeyAlias(@NonNull android.app.Activity, @NonNull android.security.KeyChainAliasCallback, @Nullable String[], @Nullable java.security.Principal[], @Nullable android.net.Uri, @Nullable String)` |  |
| `static boolean isKeyAlgorithmSupported(@NonNull String)` |  |

---

### `interface KeyChainAliasCallback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void alias(@Nullable String)` |  |

---

### `class KeyChainException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyChainException()` |  |
| `KeyChainException(String)` |  |
| `KeyChainException(String, Throwable)` |  |
| `KeyChainException(Throwable)` |  |

---

### `class final KeyPairGeneratorSpec` ~~DEPRECATED~~

- **Implements:** `java.security.spec.AlgorithmParameterSpec`
- **Annotations:** `@Deprecated`

---

### `class static final KeyPairGeneratorSpec.Builder` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final KeyStoreParameter` ~~DEPRECATED~~

- **Implements:** `java.security.KeyStore.ProtectionParameter`
- **Annotations:** `@Deprecated`

---

### `class static final KeyStoreParameter.Builder` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class NetworkSecurityPolicy`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.security.NetworkSecurityPolicy getInstance()` |  |
| `boolean isCleartextTrafficPermitted()` |  |
| `boolean isCleartextTrafficPermitted(String)` |  |

---

## Package: `android.security.identity`

### `class AccessControlProfile`


---

### `class static final AccessControlProfile.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessControlProfile.Builder(@NonNull android.security.identity.AccessControlProfileId)` |  |

---

### `class AccessControlProfileId`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessControlProfileId(int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getId()` |  |

---

### `class AlreadyPersonalizedException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AlreadyPersonalizedException(@NonNull String)` |  |
| `AlreadyPersonalizedException(@NonNull String, @NonNull Throwable)` |  |

---

### `class CipherSuiteNotSupportedException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CipherSuiteNotSupportedException(@NonNull String)` |  |
| `CipherSuiteNotSupportedException(@NonNull String, @NonNull Throwable)` |  |

---

### `class DocTypeNotSupportedException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DocTypeNotSupportedException(@NonNull String)` |  |
| `DocTypeNotSupportedException(@NonNull String, @NonNull Throwable)` |  |

---

### `class EphemeralPublicKeyNotFoundException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EphemeralPublicKeyNotFoundException(@NonNull String)` |  |
| `EphemeralPublicKeyNotFoundException(@NonNull String, @NonNull Throwable)` |  |

---

### `class abstract IdentityCredential`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void setAllowUsingExhaustedKeys(boolean)` |  |
| `abstract void setAvailableAuthenticationKeys(int, int)` |  |
| `abstract void setReaderEphemeralPublicKey(@NonNull java.security.PublicKey) throws java.security.InvalidKeyException` |  |
| `abstract void storeStaticAuthenticationData(@NonNull java.security.cert.X509Certificate, @NonNull byte[]) throws android.security.identity.UnknownAuthenticationKeyException` |  |

---

### `class IdentityCredentialException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IdentityCredentialException(@NonNull String)` |  |
| `IdentityCredentialException(@NonNull String, @NonNull Throwable)` |  |

---

### `class abstract IdentityCredentialStore`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CIPHERSUITE_ECDHE_HKDF_ECDSA_WITH_AES_256_GCM_SHA256 = 1` |  |

---

### `class InvalidReaderSignatureException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidReaderSignatureException(@NonNull String)` |  |
| `InvalidReaderSignatureException(@NonNull String, @NonNull Throwable)` |  |

---

### `class InvalidRequestMessageException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InvalidRequestMessageException(@NonNull String)` |  |
| `InvalidRequestMessageException(@NonNull String, @NonNull Throwable)` |  |

---

### `class MessageDecryptionException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MessageDecryptionException(@NonNull String)` |  |
| `MessageDecryptionException(@NonNull String, @NonNull Throwable)` |  |

---

### `class NoAuthenticationKeyAvailableException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NoAuthenticationKeyAvailableException(@NonNull String)` |  |
| `NoAuthenticationKeyAvailableException(@NonNull String, @NonNull Throwable)` |  |

---

### `class PersonalizationData`


---

### `class static final PersonalizationData.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PersonalizationData.Builder()` |  |

---

### `class abstract ResultData`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int STATUS_NOT_IN_REQUEST_MESSAGE = 3` |  |
| `static final int STATUS_NOT_REQUESTED = 2` |  |
| `static final int STATUS_NO_ACCESS_CONTROL_PROFILES = 6` |  |
| `static final int STATUS_NO_SUCH_ENTRY = 1` |  |
| `static final int STATUS_OK = 0` |  |
| `static final int STATUS_READER_AUTHENTICATION_FAILED = 5` |  |
| `static final int STATUS_USER_AUTHENTICATION_FAILED = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract int getStatus(@NonNull String, @NonNull String)` |  |

---

### `class SessionTranscriptMismatchException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SessionTranscriptMismatchException(@NonNull String)` |  |
| `SessionTranscriptMismatchException(@NonNull String, @NonNull Throwable)` |  |

---

### `class UnknownAuthenticationKeyException`

- **Extends:** `android.security.identity.IdentityCredentialException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnknownAuthenticationKeyException(@NonNull String)` |  |
| `UnknownAuthenticationKeyException(@NonNull String, @NonNull Throwable)` |  |

---

### `class abstract WritableIdentityCredential`


---

## Package: `android.security.keystore`

### `class KeyExpiredException`

- **Extends:** `java.security.InvalidKeyException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyExpiredException()` |  |
| `KeyExpiredException(String)` |  |
| `KeyExpiredException(String, Throwable)` |  |

---

### `class final KeyGenParameterSpec`

- **Implements:** `java.security.spec.AlgorithmParameterSpec`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte[] getAttestationChallenge()` |  |
| `int getKeySize()` |  |
| `int getPurposes()` |  |
| `int getUserAuthenticationType()` |  |
| `int getUserAuthenticationValidityDurationSeconds()` |  |
| `boolean isInvalidatedByBiometricEnrollment()` |  |
| `boolean isRandomizedEncryptionRequired()` |  |
| `boolean isStrongBoxBacked()` |  |
| `boolean isUnlockedDeviceRequired()` |  |
| `boolean isUserAuthenticationRequired()` |  |
| `boolean isUserAuthenticationValidWhileOnBody()` |  |
| `boolean isUserConfirmationRequired()` |  |
| `boolean isUserPresenceRequired()` |  |

---

### `class static final KeyGenParameterSpec.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyGenParameterSpec.Builder(@NonNull String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.security.keystore.KeyGenParameterSpec.Builder setAlgorithmParameterSpec(@NonNull java.security.spec.AlgorithmParameterSpec)` |  |

---

### `class KeyInfo`

- **Implements:** `java.security.spec.KeySpec`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getKeySize()` |  |
| `String getKeystoreAlias()` |  |
| `int getOrigin()` |  |
| `int getPurposes()` |  |
| `int getUserAuthenticationType()` |  |
| `int getUserAuthenticationValidityDurationSeconds()` |  |
| `boolean isInsideSecureHardware()` |  |
| `boolean isInvalidatedByBiometricEnrollment()` |  |
| `boolean isTrustedUserPresenceRequired()` |  |
| `boolean isUserAuthenticationRequired()` |  |
| `boolean isUserAuthenticationRequirementEnforcedBySecureHardware()` |  |
| `boolean isUserAuthenticationValidWhileOnBody()` |  |
| `boolean isUserConfirmationRequired()` |  |

---

### `class KeyNotYetValidException`

- **Extends:** `java.security.InvalidKeyException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyNotYetValidException()` |  |
| `KeyNotYetValidException(String)` |  |
| `KeyNotYetValidException(String, Throwable)` |  |

---

### `class KeyPermanentlyInvalidatedException`

- **Extends:** `java.security.InvalidKeyException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyPermanentlyInvalidatedException()` |  |
| `KeyPermanentlyInvalidatedException(String)` |  |
| `KeyPermanentlyInvalidatedException(String, Throwable)` |  |

---

### `class abstract KeyProperties`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AUTH_BIOMETRIC_STRONG = 2` |  |
| `static final int AUTH_DEVICE_CREDENTIAL = 1` |  |
| `static final String BLOCK_MODE_CBC = "CBC"` |  |
| `static final String BLOCK_MODE_CTR = "CTR"` |  |
| `static final String BLOCK_MODE_ECB = "ECB"` |  |
| `static final String BLOCK_MODE_GCM = "GCM"` |  |
| `static final String DIGEST_MD5 = "MD5"` |  |
| `static final String DIGEST_NONE = "NONE"` |  |
| `static final String DIGEST_SHA1 = "SHA-1"` |  |
| `static final String DIGEST_SHA224 = "SHA-224"` |  |
| `static final String DIGEST_SHA256 = "SHA-256"` |  |
| `static final String DIGEST_SHA384 = "SHA-384"` |  |
| `static final String DIGEST_SHA512 = "SHA-512"` |  |
| `static final String ENCRYPTION_PADDING_NONE = "NoPadding"` |  |
| `static final String ENCRYPTION_PADDING_PKCS7 = "PKCS7Padding"` |  |
| `static final String ENCRYPTION_PADDING_RSA_OAEP = "OAEPPadding"` |  |
| `static final String ENCRYPTION_PADDING_RSA_PKCS1 = "PKCS1Padding"` |  |
| `static final String KEY_ALGORITHM_AES = "AES"` |  |
| `static final String KEY_ALGORITHM_EC = "EC"` |  |
| `static final String KEY_ALGORITHM_HMAC_SHA1 = "HmacSHA1"` |  |
| `static final String KEY_ALGORITHM_HMAC_SHA224 = "HmacSHA224"` |  |
| `static final String KEY_ALGORITHM_HMAC_SHA256 = "HmacSHA256"` |  |
| `static final String KEY_ALGORITHM_HMAC_SHA384 = "HmacSHA384"` |  |
| `static final String KEY_ALGORITHM_HMAC_SHA512 = "HmacSHA512"` |  |
| `static final String KEY_ALGORITHM_RSA = "RSA"` |  |
| `static final int ORIGIN_GENERATED = 1` |  |
| `static final int ORIGIN_IMPORTED = 2` |  |
| `static final int ORIGIN_SECURELY_IMPORTED = 8` |  |
| `static final int ORIGIN_UNKNOWN = 4` |  |
| `static final int PURPOSE_DECRYPT = 2` |  |
| `static final int PURPOSE_ENCRYPT = 1` |  |
| `static final int PURPOSE_SIGN = 4` |  |
| `static final int PURPOSE_VERIFY = 8` |  |
| `static final int PURPOSE_WRAP_KEY = 32` |  |
| `static final String SIGNATURE_PADDING_RSA_PKCS1 = "PKCS1"` |  |
| `static final String SIGNATURE_PADDING_RSA_PSS = "PSS"` |  |

---

### `class final KeyProtection`

- **Implements:** `java.security.KeyStore.ProtectionParameter`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getPurposes()` |  |
| `int getUserAuthenticationType()` |  |
| `int getUserAuthenticationValidityDurationSeconds()` |  |
| `boolean isDigestsSpecified()` |  |
| `boolean isInvalidatedByBiometricEnrollment()` |  |
| `boolean isRandomizedEncryptionRequired()` |  |
| `boolean isUnlockedDeviceRequired()` |  |
| `boolean isUserAuthenticationRequired()` |  |
| `boolean isUserAuthenticationValidWhileOnBody()` |  |
| `boolean isUserConfirmationRequired()` |  |
| `boolean isUserPresenceRequired()` |  |

---

### `class static final KeyProtection.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `KeyProtection.Builder(int)` |  |

---

### `class SecureKeyImportUnavailableException`

- **Extends:** `java.security.ProviderException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SecureKeyImportUnavailableException()` |  |
| `SecureKeyImportUnavailableException(String)` |  |
| `SecureKeyImportUnavailableException(String, Throwable)` |  |
| `SecureKeyImportUnavailableException(Throwable)` |  |

---

### `class StrongBoxUnavailableException`

- **Extends:** `java.security.ProviderException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StrongBoxUnavailableException()` |  |
| `StrongBoxUnavailableException(String)` |  |
| `StrongBoxUnavailableException(String, Throwable)` |  |
| `StrongBoxUnavailableException(Throwable)` |  |

---

### `class UserNotAuthenticatedException`

- **Extends:** `java.security.InvalidKeyException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UserNotAuthenticatedException()` |  |
| `UserNotAuthenticatedException(String)` |  |
| `UserNotAuthenticatedException(String, Throwable)` |  |

---

### `class UserPresenceUnavailableException`

- **Extends:** `java.security.InvalidKeyException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UserPresenceUnavailableException()` |  |
| `UserPresenceUnavailableException(String)` |  |
| `UserPresenceUnavailableException(String, Throwable)` |  |

---

### `class WrappedKeyEntry`

- **Implements:** `java.security.KeyStore.Entry`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WrappedKeyEntry(byte[], String, String, java.security.spec.AlgorithmParameterSpec)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.security.spec.AlgorithmParameterSpec getAlgorithmParameterSpec()` |  |
| `String getTransformation()` |  |
| `byte[] getWrappedKeyBytes()` |  |
| `String getWrappingKeyAlias()` |  |

---

## Package: `android.system`

### `class final ErrnoException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ErrnoException(String, int)` |  |
| `ErrnoException(String, int, Throwable)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final int errno` |  |

---

### `class Int64Ref`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Int64Ref(long)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `long value` |  |

---

### `class final Os`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.io.FileDescriptor accept(java.io.FileDescriptor, java.net.InetSocketAddress) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static boolean access(String, int) throws android.system.ErrnoException` |  |
| `static void bind(java.io.FileDescriptor, java.net.InetAddress, int) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static void bind(@NonNull java.io.FileDescriptor, @NonNull java.net.SocketAddress) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static void chmod(String, int) throws android.system.ErrnoException` |  |
| `static void chown(String, int, int) throws android.system.ErrnoException` |  |
| `static void close(java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static void connect(java.io.FileDescriptor, java.net.InetAddress, int) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static void connect(@NonNull java.io.FileDescriptor, @NonNull java.net.SocketAddress) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static java.io.FileDescriptor dup(java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static java.io.FileDescriptor dup2(java.io.FileDescriptor, int) throws android.system.ErrnoException` |  |
| `static String[] environ()` |  |
| `static void execv(String, String[]) throws android.system.ErrnoException` |  |
| `static void execve(String, String[], String[]) throws android.system.ErrnoException` |  |
| `static void fchmod(java.io.FileDescriptor, int) throws android.system.ErrnoException` |  |
| `static void fchown(java.io.FileDescriptor, int, int) throws android.system.ErrnoException` |  |
| `static int fcntlInt(@NonNull java.io.FileDescriptor, int, int) throws android.system.ErrnoException` |  |
| `static void fdatasync(java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static android.system.StructStat fstat(java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static android.system.StructStatVfs fstatvfs(java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static void fsync(java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static void ftruncate(java.io.FileDescriptor, long) throws android.system.ErrnoException` |  |
| `static String gai_strerror(int)` |  |
| `static int getegid()` |  |
| `static String getenv(String)` |  |
| `static int geteuid()` |  |
| `static int getgid()` |  |
| `static java.net.SocketAddress getpeername(java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static int getpid()` |  |
| `static int getppid()` |  |
| `static java.net.SocketAddress getsockname(java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static int gettid()` |  |
| `static int getuid()` |  |
| `static byte[] getxattr(String, String) throws android.system.ErrnoException` |  |
| `static String if_indextoname(int)` |  |
| `static int if_nametoindex(String)` |  |
| `static java.net.InetAddress inet_pton(int, String)` |  |
| `static boolean isatty(java.io.FileDescriptor)` |  |
| `static void kill(int, int) throws android.system.ErrnoException` |  |
| `static void lchown(String, int, int) throws android.system.ErrnoException` |  |
| `static void link(String, String) throws android.system.ErrnoException` |  |
| `static void listen(java.io.FileDescriptor, int) throws android.system.ErrnoException` |  |
| `static String[] listxattr(String) throws android.system.ErrnoException` |  |
| `static long lseek(java.io.FileDescriptor, long, int) throws android.system.ErrnoException` |  |
| `static android.system.StructStat lstat(String) throws android.system.ErrnoException` |  |
| `static void mincore(long, long, byte[]) throws android.system.ErrnoException` |  |
| `static void mkdir(String, int) throws android.system.ErrnoException` |  |
| `static void mkfifo(String, int) throws android.system.ErrnoException` |  |
| `static void mlock(long, long) throws android.system.ErrnoException` |  |
| `static long mmap(long, long, int, int, java.io.FileDescriptor, long) throws android.system.ErrnoException` |  |
| `static void msync(long, long, int) throws android.system.ErrnoException` |  |
| `static void munlock(long, long) throws android.system.ErrnoException` |  |
| `static void munmap(long, long) throws android.system.ErrnoException` |  |
| `static java.io.FileDescriptor open(String, int, int) throws android.system.ErrnoException` |  |
| `static java.io.FileDescriptor[] pipe() throws android.system.ErrnoException` |  |
| `static int poll(android.system.StructPollfd[], int) throws android.system.ErrnoException` |  |
| `static void posix_fallocate(java.io.FileDescriptor, long, long) throws android.system.ErrnoException` |  |
| `static int prctl(int, long, long, long, long) throws android.system.ErrnoException` |  |
| `static int pread(java.io.FileDescriptor, java.nio.ByteBuffer, long) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |
| `static int pread(java.io.FileDescriptor, byte[], int, int, long) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |
| `static int pwrite(java.io.FileDescriptor, java.nio.ByteBuffer, long) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |
| `static int pwrite(java.io.FileDescriptor, byte[], int, int, long) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |
| `static int read(java.io.FileDescriptor, java.nio.ByteBuffer) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |
| `static int read(java.io.FileDescriptor, byte[], int, int) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |
| `static String readlink(String) throws android.system.ErrnoException` |  |
| `static int readv(java.io.FileDescriptor, Object[], int[], int[]) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |
| `static int recvfrom(java.io.FileDescriptor, java.nio.ByteBuffer, int, java.net.InetSocketAddress) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static int recvfrom(java.io.FileDescriptor, byte[], int, int, int, java.net.InetSocketAddress) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static void remove(String) throws android.system.ErrnoException` |  |
| `static void removexattr(String, String) throws android.system.ErrnoException` |  |
| `static void rename(String, String) throws android.system.ErrnoException` |  |
| `static long sendfile(java.io.FileDescriptor, java.io.FileDescriptor, android.system.Int64Ref, long) throws android.system.ErrnoException` |  |
| `static int sendto(java.io.FileDescriptor, java.nio.ByteBuffer, int, java.net.InetAddress, int) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static int sendto(java.io.FileDescriptor, byte[], int, int, int, java.net.InetAddress, int) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static int sendto(@NonNull java.io.FileDescriptor, @NonNull byte[], int, int, int, @Nullable java.net.SocketAddress) throws android.system.ErrnoException, java.net.SocketException` |  |
| `static void setenv(String, String, boolean) throws android.system.ErrnoException` |  |
| `static int setsid() throws android.system.ErrnoException` |  |
| `static void setsockoptInt(java.io.FileDescriptor, int, int, int) throws android.system.ErrnoException` |  |
| `static void setsockoptTimeval(@NonNull java.io.FileDescriptor, int, int, @NonNull android.system.StructTimeval) throws android.system.ErrnoException` |  |
| `static void setxattr(String, String, byte[], int) throws android.system.ErrnoException` |  |
| `static void shutdown(java.io.FileDescriptor, int) throws android.system.ErrnoException` |  |
| `static java.io.FileDescriptor socket(int, int, int) throws android.system.ErrnoException` |  |
| `static void socketpair(int, int, int, java.io.FileDescriptor, java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static android.system.StructStat stat(String) throws android.system.ErrnoException` |  |
| `static android.system.StructStatVfs statvfs(String) throws android.system.ErrnoException` |  |
| `static String strerror(int)` |  |
| `static String strsignal(int)` |  |
| `static void symlink(String, String) throws android.system.ErrnoException` |  |
| `static long sysconf(int)` |  |
| `static void tcdrain(java.io.FileDescriptor) throws android.system.ErrnoException` |  |
| `static void tcsendbreak(java.io.FileDescriptor, int) throws android.system.ErrnoException` |  |
| `static int umask(int)` |  |
| `static android.system.StructUtsname uname()` |  |
| `static void unsetenv(String) throws android.system.ErrnoException` |  |
| `static int write(java.io.FileDescriptor, java.nio.ByteBuffer) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |
| `static int write(java.io.FileDescriptor, byte[], int, int) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |
| `static int writev(java.io.FileDescriptor, Object[], int[], int[]) throws android.system.ErrnoException, java.io.InterruptedIOException` |  |

---

### `class final OsConstants`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AF_INET` |  |
| `static final int AF_INET6` |  |
| `static final int AF_NETLINK` |  |
| `static final int AF_PACKET` |  |
| `static final int AF_UNIX` |  |
| `static final int AF_UNSPEC` |  |
| `static final int AI_ADDRCONFIG` |  |
| `static final int AI_ALL` |  |
| `static final int AI_CANONNAME` |  |
| `static final int AI_NUMERICHOST` |  |
| `static final int AI_NUMERICSERV` |  |
| `static final int AI_PASSIVE` |  |
| `static final int AI_V4MAPPED` |  |
| `static final int ARPHRD_ETHER` |  |
| `static final int CAP_AUDIT_CONTROL` |  |
| `static final int CAP_AUDIT_WRITE` |  |
| `static final int CAP_BLOCK_SUSPEND` |  |
| `static final int CAP_CHOWN` |  |
| `static final int CAP_DAC_OVERRIDE` |  |
| `static final int CAP_DAC_READ_SEARCH` |  |
| `static final int CAP_FOWNER` |  |
| `static final int CAP_FSETID` |  |
| `static final int CAP_IPC_LOCK` |  |
| `static final int CAP_IPC_OWNER` |  |
| `static final int CAP_KILL` |  |
| `static final int CAP_LAST_CAP` |  |
| `static final int CAP_LEASE` |  |
| `static final int CAP_LINUX_IMMUTABLE` |  |
| `static final int CAP_MAC_ADMIN` |  |
| `static final int CAP_MAC_OVERRIDE` |  |
| `static final int CAP_MKNOD` |  |
| `static final int CAP_NET_ADMIN` |  |
| `static final int CAP_NET_BIND_SERVICE` |  |
| `static final int CAP_NET_BROADCAST` |  |
| `static final int CAP_NET_RAW` |  |
| `static final int CAP_SETFCAP` |  |
| `static final int CAP_SETGID` |  |
| `static final int CAP_SETPCAP` |  |
| `static final int CAP_SETUID` |  |
| `static final int CAP_SYSLOG` |  |
| `static final int CAP_SYS_ADMIN` |  |
| `static final int CAP_SYS_BOOT` |  |
| `static final int CAP_SYS_CHROOT` |  |
| `static final int CAP_SYS_MODULE` |  |
| `static final int CAP_SYS_NICE` |  |
| `static final int CAP_SYS_PACCT` |  |
| `static final int CAP_SYS_PTRACE` |  |
| `static final int CAP_SYS_RAWIO` |  |
| `static final int CAP_SYS_RESOURCE` |  |
| `static final int CAP_SYS_TIME` |  |
| `static final int CAP_SYS_TTY_CONFIG` |  |
| `static final int CAP_WAKE_ALARM` |  |
| `static final int E2BIG` |  |
| `static final int EACCES` |  |
| `static final int EADDRINUSE` |  |
| `static final int EADDRNOTAVAIL` |  |
| `static final int EAFNOSUPPORT` |  |
| `static final int EAGAIN` |  |
| `static final int EAI_AGAIN` |  |
| `static final int EAI_BADFLAGS` |  |
| `static final int EAI_FAIL` |  |
| `static final int EAI_FAMILY` |  |
| `static final int EAI_MEMORY` |  |
| `static final int EAI_NODATA` |  |
| `static final int EAI_NONAME` |  |
| `static final int EAI_OVERFLOW` |  |
| `static final int EAI_SERVICE` |  |
| `static final int EAI_SOCKTYPE` |  |
| `static final int EAI_SYSTEM` |  |
| `static final int EALREADY` |  |
| `static final int EBADF` |  |
| `static final int EBADMSG` |  |
| `static final int EBUSY` |  |
| `static final int ECANCELED` |  |
| `static final int ECHILD` |  |
| `static final int ECONNABORTED` |  |
| `static final int ECONNREFUSED` |  |
| `static final int ECONNRESET` |  |
| `static final int EDEADLK` |  |
| `static final int EDESTADDRREQ` |  |
| `static final int EDOM` |  |
| `static final int EDQUOT` |  |
| `static final int EEXIST` |  |
| `static final int EFAULT` |  |
| `static final int EFBIG` |  |
| `static final int EHOSTUNREACH` |  |
| `static final int EIDRM` |  |
| `static final int EILSEQ` |  |
| `static final int EINPROGRESS` |  |
| `static final int EINTR` |  |
| `static final int EINVAL` |  |
| `static final int EIO` |  |
| `static final int EISCONN` |  |
| `static final int EISDIR` |  |
| `static final int ELOOP` |  |
| `static final int EMFILE` |  |
| `static final int EMLINK` |  |
| `static final int EMSGSIZE` |  |
| `static final int EMULTIHOP` |  |
| `static final int ENAMETOOLONG` |  |
| `static final int ENETDOWN` |  |
| `static final int ENETRESET` |  |
| `static final int ENETUNREACH` |  |
| `static final int ENFILE` |  |
| `static final int ENOBUFS` |  |
| `static final int ENODATA` |  |
| `static final int ENODEV` |  |
| `static final int ENOENT` |  |
| `static final int ENOEXEC` |  |
| `static final int ENOLCK` |  |
| `static final int ENOLINK` |  |
| `static final int ENOMEM` |  |
| `static final int ENOMSG` |  |
| `static final int ENOPROTOOPT` |  |
| `static final int ENOSPC` |  |
| `static final int ENOSR` |  |
| `static final int ENOSTR` |  |
| `static final int ENOSYS` |  |
| `static final int ENOTCONN` |  |
| `static final int ENOTDIR` |  |
| `static final int ENOTEMPTY` |  |
| `static final int ENOTSOCK` |  |
| `static final int ENOTSUP` |  |
| `static final int ENOTTY` |  |
| `static final int ENXIO` |  |
| `static final int EOPNOTSUPP` |  |
| `static final int EOVERFLOW` |  |
| `static final int EPERM` |  |
| `static final int EPIPE` |  |
| `static final int EPROTO` |  |
| `static final int EPROTONOSUPPORT` |  |
| `static final int EPROTOTYPE` |  |
| `static final int ERANGE` |  |
| `static final int EROFS` |  |
| `static final int ESPIPE` |  |
| `static final int ESRCH` |  |
| `static final int ESTALE` |  |
| `static final int ETH_P_ALL` |  |
| `static final int ETH_P_ARP` |  |
| `static final int ETH_P_IP` |  |
| `static final int ETH_P_IPV6` |  |
| `static final int ETIME` |  |
| `static final int ETIMEDOUT` |  |
| `static final int ETXTBSY` |  |
| `static final int EXDEV` |  |
| `static final int EXIT_FAILURE` |  |
| `static final int EXIT_SUCCESS` |  |
| `static final int FD_CLOEXEC` |  |
| `static final int FIONREAD` |  |
| `static final int F_DUPFD` |  |
| `static final int F_DUPFD_CLOEXEC` |  |
| `static final int F_GETFD` |  |
| `static final int F_GETFL` |  |
| `static final int F_GETLK` |  |
| `static final int F_GETLK64` |  |
| `static final int F_GETOWN` |  |
| `static final int F_OK` |  |
| `static final int F_RDLCK` |  |
| `static final int F_SETFD` |  |
| `static final int F_SETFL` |  |
| `static final int F_SETLK` |  |
| `static final int F_SETLK64` |  |
| `static final int F_SETLKW` |  |
| `static final int F_SETLKW64` |  |
| `static final int F_SETOWN` |  |
| `static final int F_UNLCK` |  |
| `static final int F_WRLCK` |  |
| `static final int ICMP6_ECHO_REPLY` |  |
| `static final int ICMP6_ECHO_REQUEST` |  |
| `static final int ICMP_ECHO` |  |
| `static final int ICMP_ECHOREPLY` |  |
| `static final int IFA_F_DADFAILED` |  |
| `static final int IFA_F_DEPRECATED` |  |
| `static final int IFA_F_HOMEADDRESS` |  |
| `static final int IFA_F_NODAD` |  |
| `static final int IFA_F_OPTIMISTIC` |  |
| `static final int IFA_F_PERMANENT` |  |
| `static final int IFA_F_SECONDARY` |  |
| `static final int IFA_F_TEMPORARY` |  |
| `static final int IFA_F_TENTATIVE` |  |
| `static final int IFF_ALLMULTI` |  |
| `static final int IFF_AUTOMEDIA` |  |
| `static final int IFF_BROADCAST` |  |
| `static final int IFF_DEBUG` |  |
| `static final int IFF_DYNAMIC` |  |
| `static final int IFF_LOOPBACK` |  |
| `static final int IFF_MASTER` |  |
| `static final int IFF_MULTICAST` |  |
| `static final int IFF_NOARP` |  |
| `static final int IFF_NOTRAILERS` |  |
| `static final int IFF_POINTOPOINT` |  |
| `static final int IFF_PORTSEL` |  |
| `static final int IFF_PROMISC` |  |
| `static final int IFF_RUNNING` |  |
| `static final int IFF_SLAVE` |  |
| `static final int IFF_UP` |  |
| `static final int IPPROTO_ICMP` |  |
| `static final int IPPROTO_ICMPV6` |  |
| `static final int IPPROTO_IP` |  |
| `static final int IPPROTO_IPV6` |  |
| `static final int IPPROTO_RAW` |  |
| `static final int IPPROTO_TCP` |  |
| `static final int IPPROTO_UDP` |  |
| `static final int IPV6_CHECKSUM` |  |
| `static final int IPV6_MULTICAST_HOPS` |  |
| `static final int IPV6_MULTICAST_IF` |  |
| `static final int IPV6_MULTICAST_LOOP` |  |
| `static final int IPV6_RECVDSTOPTS` |  |
| `static final int IPV6_RECVHOPLIMIT` |  |
| `static final int IPV6_RECVHOPOPTS` |  |
| `static final int IPV6_RECVPKTINFO` |  |
| `static final int IPV6_RECVRTHDR` |  |
| `static final int IPV6_RECVTCLASS` |  |
| `static final int IPV6_TCLASS` |  |
| `static final int IPV6_UNICAST_HOPS` |  |
| `static final int IPV6_V6ONLY` |  |
| `static final int IP_MULTICAST_IF` |  |
| `static final int IP_MULTICAST_LOOP` |  |
| `static final int IP_MULTICAST_TTL` |  |
| `static final int IP_TOS` |  |
| `static final int IP_TTL` |  |
| `static final int MAP_ANONYMOUS` |  |
| `static final int MAP_FIXED` |  |
| `static final int MAP_PRIVATE` |  |
| `static final int MAP_SHARED` |  |
| `static final int MCAST_BLOCK_SOURCE` |  |
| `static final int MCAST_JOIN_GROUP` |  |
| `static final int MCAST_JOIN_SOURCE_GROUP` |  |
| `static final int MCAST_LEAVE_GROUP` |  |
| `static final int MCAST_LEAVE_SOURCE_GROUP` |  |
| `static final int MCAST_UNBLOCK_SOURCE` |  |
| `static final int MCL_CURRENT` |  |
| `static final int MCL_FUTURE` |  |
| `static final int MFD_CLOEXEC` |  |
| `static final int MSG_CTRUNC` |  |
| `static final int MSG_DONTROUTE` |  |
| `static final int MSG_EOR` |  |
| `static final int MSG_OOB` |  |
| `static final int MSG_PEEK` |  |
| `static final int MSG_TRUNC` |  |
| `static final int MSG_WAITALL` |  |
| `static final int MS_ASYNC` |  |
| `static final int MS_INVALIDATE` |  |
| `static final int MS_SYNC` |  |
| `static final int NETLINK_INET_DIAG` |  |
| `static final int NETLINK_NETFILTER` |  |
| `static final int NETLINK_ROUTE` |  |
| `static final int NI_DGRAM` |  |
| `static final int NI_NAMEREQD` |  |
| `static final int NI_NOFQDN` |  |
| `static final int NI_NUMERICHOST` |  |
| `static final int NI_NUMERICSERV` |  |
| `static final int O_ACCMODE` |  |
| `static final int O_APPEND` |  |
| `static final int O_CLOEXEC` |  |
| `static final int O_CREAT` |  |
| `static final int O_DSYNC` |  |
| `static final int O_EXCL` |  |
| `static final int O_NOCTTY` |  |
| `static final int O_NOFOLLOW` |  |
| `static final int O_NONBLOCK` |  |
| `static final int O_RDONLY` |  |
| `static final int O_RDWR` |  |
| `static final int O_SYNC` |  |
| `static final int O_TRUNC` |  |
| `static final int O_WRONLY` |  |
| `static final int POLLERR` |  |
| `static final int POLLHUP` |  |
| `static final int POLLIN` |  |
| `static final int POLLNVAL` |  |
| `static final int POLLOUT` |  |
| `static final int POLLPRI` |  |
| `static final int POLLRDBAND` |  |
| `static final int POLLRDNORM` |  |
| `static final int POLLWRBAND` |  |
| `static final int POLLWRNORM` |  |
| `static final int PROT_EXEC` |  |
| `static final int PROT_NONE` |  |
| `static final int PROT_READ` |  |
| `static final int PROT_WRITE` |  |
| `static final int PR_GET_DUMPABLE` |  |
| `static final int PR_SET_DUMPABLE` |  |
| `static final int PR_SET_NO_NEW_PRIVS` |  |
| `static final int RTMGRP_NEIGH` |  |
| `static final int RT_SCOPE_HOST` |  |
| `static final int RT_SCOPE_LINK` |  |
| `static final int RT_SCOPE_NOWHERE` |  |
| `static final int RT_SCOPE_SITE` |  |
| `static final int RT_SCOPE_UNIVERSE` |  |
| `static final int R_OK` |  |
| `static final int SEEK_CUR` |  |
| `static final int SEEK_END` |  |
| `static final int SEEK_SET` |  |
| `static final int SHUT_RD` |  |
| `static final int SHUT_RDWR` |  |
| `static final int SHUT_WR` |  |
| `static final int SIGABRT` |  |
| `static final int SIGALRM` |  |
| `static final int SIGBUS` |  |
| `static final int SIGCHLD` |  |
| `static final int SIGCONT` |  |
| `static final int SIGFPE` |  |
| `static final int SIGHUP` |  |
| `static final int SIGILL` |  |
| `static final int SIGINT` |  |
| `static final int SIGIO` |  |
| `static final int SIGKILL` |  |
| `static final int SIGPIPE` |  |
| `static final int SIGPROF` |  |
| `static final int SIGPWR` |  |
| `static final int SIGQUIT` |  |
| `static final int SIGRTMAX` |  |
| `static final int SIGRTMIN` |  |
| `static final int SIGSEGV` |  |
| `static final int SIGSTKFLT` |  |
| `static final int SIGSTOP` |  |
| `static final int SIGSYS` |  |
| `static final int SIGTERM` |  |
| `static final int SIGTRAP` |  |
| `static final int SIGTSTP` |  |
| `static final int SIGTTIN` |  |
| `static final int SIGTTOU` |  |
| `static final int SIGURG` |  |
| `static final int SIGUSR1` |  |
| `static final int SIGUSR2` |  |
| `static final int SIGVTALRM` |  |
| `static final int SIGWINCH` |  |
| `static final int SIGXCPU` |  |
| `static final int SIGXFSZ` |  |
| `static final int SIOCGIFADDR` |  |
| `static final int SIOCGIFBRDADDR` |  |
| `static final int SIOCGIFDSTADDR` |  |
| `static final int SIOCGIFNETMASK` |  |
| `static final int SOCK_CLOEXEC` |  |
| `static final int SOCK_DGRAM` |  |
| `static final int SOCK_NONBLOCK` |  |
| `static final int SOCK_RAW` |  |
| `static final int SOCK_SEQPACKET` |  |
| `static final int SOCK_STREAM` |  |
| `static final int SOL_SOCKET` |  |
| `static final int SO_BINDTODEVICE` |  |
| `static final int SO_BROADCAST` |  |
| `static final int SO_DEBUG` |  |
| `static final int SO_DONTROUTE` |  |
| `static final int SO_ERROR` |  |
| `static final int SO_KEEPALIVE` |  |
| `static final int SO_LINGER` |  |
| `static final int SO_OOBINLINE` |  |
| `static final int SO_PASSCRED` |  |
| `static final int SO_PEERCRED` |  |
| `static final int SO_RCVBUF` |  |
| `static final int SO_RCVLOWAT` |  |
| `static final int SO_RCVTIMEO` |  |
| `static final int SO_REUSEADDR` |  |
| `static final int SO_SNDBUF` |  |
| `static final int SO_SNDLOWAT` |  |
| `static final int SO_SNDTIMEO` |  |
| `static final int SO_TYPE` |  |
| `static final int STDERR_FILENO` |  |
| `static final int STDIN_FILENO` |  |
| `static final int STDOUT_FILENO` |  |
| `static final int ST_MANDLOCK` |  |
| `static final int ST_NOATIME` |  |
| `static final int ST_NODEV` |  |
| `static final int ST_NODIRATIME` |  |
| `static final int ST_NOEXEC` |  |
| `static final int ST_NOSUID` |  |
| `static final int ST_RDONLY` |  |
| `static final int ST_RELATIME` |  |
| `static final int ST_SYNCHRONOUS` |  |
| `static final int S_IFBLK` |  |
| `static final int S_IFCHR` |  |
| `static final int S_IFDIR` |  |
| `static final int S_IFIFO` |  |
| `static final int S_IFLNK` |  |
| `static final int S_IFMT` |  |
| `static final int S_IFREG` |  |
| `static final int S_IFSOCK` |  |
| `static final int S_IRGRP` |  |
| `static final int S_IROTH` |  |
| `static final int S_IRUSR` |  |
| `static final int S_IRWXG` |  |
| `static final int S_IRWXO` |  |
| `static final int S_IRWXU` |  |
| `static final int S_ISGID` |  |
| `static final int S_ISUID` |  |
| `static final int S_ISVTX` |  |
| `static final int S_IWGRP` |  |
| `static final int S_IWOTH` |  |
| `static final int S_IWUSR` |  |
| `static final int S_IXGRP` |  |
| `static final int S_IXOTH` |  |
| `static final int S_IXUSR` |  |
| `static final int TCP_NODELAY` |  |
| `static final int TCP_USER_TIMEOUT` |  |
| `static final int WCONTINUED` |  |
| `static final int WEXITED` |  |
| `static final int WNOHANG` |  |
| `static final int WNOWAIT` |  |
| `static final int WSTOPPED` |  |
| `static final int WUNTRACED` |  |
| `static final int W_OK` |  |
| `static final int X_OK` |  |
| `static final int _SC_2_CHAR_TERM` |  |
| `static final int _SC_2_C_BIND` |  |
| `static final int _SC_2_C_DEV` |  |
| `static final int _SC_2_C_VERSION` |  |
| `static final int _SC_2_FORT_DEV` |  |
| `static final int _SC_2_FORT_RUN` |  |
| `static final int _SC_2_LOCALEDEF` |  |
| `static final int _SC_2_SW_DEV` |  |
| `static final int _SC_2_UPE` |  |
| `static final int _SC_2_VERSION` |  |
| `static final int _SC_AIO_LISTIO_MAX` |  |
| `static final int _SC_AIO_MAX` |  |
| `static final int _SC_AIO_PRIO_DELTA_MAX` |  |
| `static final int _SC_ARG_MAX` |  |
| `static final int _SC_ASYNCHRONOUS_IO` |  |
| `static final int _SC_ATEXIT_MAX` |  |
| `static final int _SC_AVPHYS_PAGES` |  |
| `static final int _SC_BC_BASE_MAX` |  |
| `static final int _SC_BC_DIM_MAX` |  |
| `static final int _SC_BC_SCALE_MAX` |  |
| `static final int _SC_BC_STRING_MAX` |  |
| `static final int _SC_CHILD_MAX` |  |
| `static final int _SC_CLK_TCK` |  |
| `static final int _SC_COLL_WEIGHTS_MAX` |  |
| `static final int _SC_DELAYTIMER_MAX` |  |
| `static final int _SC_EXPR_NEST_MAX` |  |
| `static final int _SC_FSYNC` |  |
| `static final int _SC_GETGR_R_SIZE_MAX` |  |
| `static final int _SC_GETPW_R_SIZE_MAX` |  |
| `static final int _SC_IOV_MAX` |  |
| `static final int _SC_JOB_CONTROL` |  |
| `static final int _SC_LINE_MAX` |  |
| `static final int _SC_LOGIN_NAME_MAX` |  |
| `static final int _SC_MAPPED_FILES` |  |
| `static final int _SC_MEMLOCK` |  |
| `static final int _SC_MEMLOCK_RANGE` |  |
| `static final int _SC_MEMORY_PROTECTION` |  |
| `static final int _SC_MESSAGE_PASSING` |  |
| `static final int _SC_MQ_OPEN_MAX` |  |
| `static final int _SC_MQ_PRIO_MAX` |  |
| `static final int _SC_NGROUPS_MAX` |  |
| `static final int _SC_NPROCESSORS_CONF` |  |
| `static final int _SC_NPROCESSORS_ONLN` |  |
| `static final int _SC_OPEN_MAX` |  |
| `static final int _SC_PAGESIZE` |  |
| `static final int _SC_PAGE_SIZE` |  |
| `static final int _SC_PASS_MAX` |  |
| `static final int _SC_PHYS_PAGES` |  |
| `static final int _SC_PRIORITIZED_IO` |  |
| `static final int _SC_PRIORITY_SCHEDULING` |  |
| `static final int _SC_REALTIME_SIGNALS` |  |
| `static final int _SC_RE_DUP_MAX` |  |
| `static final int _SC_RTSIG_MAX` |  |
| `static final int _SC_SAVED_IDS` |  |
| `static final int _SC_SEMAPHORES` |  |
| `static final int _SC_SEM_NSEMS_MAX` |  |
| `static final int _SC_SEM_VALUE_MAX` |  |
| `static final int _SC_SHARED_MEMORY_OBJECTS` |  |
| `static final int _SC_SIGQUEUE_MAX` |  |
| `static final int _SC_STREAM_MAX` |  |
| `static final int _SC_SYNCHRONIZED_IO` |  |
| `static final int _SC_THREADS` |  |
| `static final int _SC_THREAD_ATTR_STACKADDR` |  |
| `static final int _SC_THREAD_ATTR_STACKSIZE` |  |
| `static final int _SC_THREAD_DESTRUCTOR_ITERATIONS` |  |
| `static final int _SC_THREAD_KEYS_MAX` |  |
| `static final int _SC_THREAD_PRIORITY_SCHEDULING` |  |
| `static final int _SC_THREAD_PRIO_INHERIT` |  |
| `static final int _SC_THREAD_PRIO_PROTECT` |  |
| `static final int _SC_THREAD_SAFE_FUNCTIONS` |  |
| `static final int _SC_THREAD_STACK_MIN` |  |
| `static final int _SC_THREAD_THREADS_MAX` |  |
| `static final int _SC_TIMERS` |  |
| `static final int _SC_TIMER_MAX` |  |
| `static final int _SC_TTY_NAME_MAX` |  |
| `static final int _SC_TZNAME_MAX` |  |
| `static final int _SC_VERSION` |  |
| `static final int _SC_XBS5_ILP32_OFF32` |  |
| `static final int _SC_XBS5_ILP32_OFFBIG` |  |
| `static final int _SC_XBS5_LP64_OFF64` |  |
| `static final int _SC_XBS5_LPBIG_OFFBIG` |  |
| `static final int _SC_XOPEN_CRYPT` |  |
| `static final int _SC_XOPEN_ENH_I18N` |  |
| `static final int _SC_XOPEN_LEGACY` |  |
| `static final int _SC_XOPEN_REALTIME` |  |
| `static final int _SC_XOPEN_REALTIME_THREADS` |  |
| `static final int _SC_XOPEN_SHM` |  |
| `static final int _SC_XOPEN_UNIX` |  |
| `static final int _SC_XOPEN_VERSION` |  |
| `static final int _SC_XOPEN_XCU_VERSION` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static boolean S_ISBLK(int)` |  |
| `static boolean S_ISCHR(int)` |  |
| `static boolean S_ISDIR(int)` |  |
| `static boolean S_ISFIFO(int)` |  |
| `static boolean S_ISLNK(int)` |  |
| `static boolean S_ISREG(int)` |  |
| `static boolean S_ISSOCK(int)` |  |
| `static boolean WCOREDUMP(int)` |  |
| `static int WEXITSTATUS(int)` |  |
| `static boolean WIFEXITED(int)` |  |
| `static boolean WIFSIGNALED(int)` |  |
| `static boolean WIFSTOPPED(int)` |  |
| `static int WSTOPSIG(int)` |  |
| `static int WTERMSIG(int)` |  |
| `static String errnoName(int)` |  |
| `static String gaiName(int)` |  |

---

### `class final StructPollfd`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StructPollfd()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `short events` |  |
| `java.io.FileDescriptor fd` |  |
| `short revents` |  |
| `Object userData` |  |

---

### `class final StructStat`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StructStat(long, long, int, long, int, int, long, long, long, long, long, long, long)` |  |
| `StructStat(long, long, int, long, int, int, long, long, android.system.StructTimespec, android.system.StructTimespec, android.system.StructTimespec, long, long)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final android.system.StructTimespec st_atim` |  |
| `final long st_atime` |  |
| `final long st_blksize` |  |
| `final long st_blocks` |  |
| `final android.system.StructTimespec st_ctim` |  |
| `final long st_ctime` |  |
| `final long st_dev` |  |
| `final int st_gid` |  |
| `final long st_ino` |  |
| `final int st_mode` |  |
| `final android.system.StructTimespec st_mtim` |  |
| `final long st_mtime` |  |
| `final long st_nlink` |  |
| `final long st_rdev` |  |
| `final long st_size` |  |
| `final int st_uid` |  |

---

### `class final StructStatVfs`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StructStatVfs(long, long, long, long, long, long, long, long, long, long, long)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final long f_bavail` |  |
| `final long f_bfree` |  |
| `final long f_blocks` |  |
| `final long f_bsize` |  |
| `final long f_favail` |  |
| `final long f_ffree` |  |
| `final long f_files` |  |
| `final long f_flag` |  |
| `final long f_frsize` |  |
| `final long f_fsid` |  |
| `final long f_namemax` |  |

---

### `class final StructTimespec`

- **Implements:** `java.lang.Comparable<android.system.StructTimespec>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StructTimespec(long, long)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final long tv_nsec` |  |
| `final long tv_sec` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(android.system.StructTimespec)` |  |

---

### `class final StructTimeval`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final long tv_sec` |  |
| `final long tv_usec` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long toMillis()` |  |

---

### `class final StructUtsname`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StructUtsname(String, String, String, String, String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final String machine` |  |
| `final String nodename` |  |
| `final String release` |  |
| `final String sysname` |  |
| `final String version` |  |

---

