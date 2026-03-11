# Android 11 AOSP 代码审查: OS and System-Level APIs

**Review Scope:**
- `frameworks/base/core/java/android/os/` -- Threading, IPC, system utilities
- `frameworks/base/core/java/android/permission/` -- Permission management
- `frameworks/base/core/java/android/security/` -- Security and keystore APIs

---

## Table of Contents

1. [Threading Model (Handler, Looper, MessageQueue)](#1-threading-model)
2. [AsyncTask (Deprecated)](#2-asynctask)
3. [IPC / Binder Architecture](#3-ipc--binder-architecture)
4. [Bundle](#4-bundle)
5. [Build Information](#5-build-information)
6. [Environment (Filesystem Paths)](#6-environment)
7. [PowerManager and BatteryManager](#7-powermanager-and-batterymanager)
8. [SystemClock](#8-systemclock)
9. [Process Management](#9-process-management)
10. [UserManager](#10-usermanager)
11. [Permission Manager](#11-permission-manager)
12. [Security APIs](#12-security-apis)
13. [Architectural Patterns and Observations](#13-architectural-patterns)

---

## 1. Threading Model

The Android threading model is built on three tightly coupled classes that implement a single-threaded event loop pattern.

### 1.1 Looper

**File:** `frameworks/base/core/java/android/os/Looper.java` (476 lines)

**Purpose:** Runs a message loop for a thread. Each thread can have at most one Looper. The main (UI) thread always has a Looper; worker threads must explicitly create one.

**Key Public APIs:**

| Method | Line | 描述 |
|--------|------|-------------|
| `prepare()` | 103 | Initialize current thread as a looper thread |
| `loop()` | 154 | Start processing the message queue (blocks) |
| `myLooper()` | 293 | Return Looper for current thread (may be null) |
| `getMainLooper()` | 135 | Return the application's main looper |
| `quit()` | 362 | Quit immediately (may drop pending messages) |
| `quitSafely()` | 378 | Quit after processing pending due messages |
| `getThread()` | 387 | Get the Thread associated with this Looper |
| `getQueue()` | 396 | Get the MessageQueue |
| `isCurrentThread()` | 314 | Check if caller is on this looper's thread |
| `setMessageLogging(Printer)` | 327 | Enable/disable message dispatch logging |

**Architecture Details:**

- Uses `ThreadLocal<Looper>` storage (line 72) -- each thread gets its own Looper.
- The main Looper is created with `quitAllowed=false` (line 123), preventing apps from accidentally quitting the main thread's event loop.
- The `loop()` method (line 154) is an infinite `for(;;)` loop that:
  1. Calls `queue.next()` which may block via native `epoll`
  2. Dispatches via `msg.target.dispatchMessage(msg)` (line 223)
  3. Clears and verifies Binder calling identity (line 263-270) -- a security check
  4. Recycles the message after dispatch (line 272)

**Hidden APIs:**
- `setObserver(Observer)` (line 146, `@hide`) -- process-wide message dispatch observer for telemetry
- `setSlowLogThresholdMs()` (line 341, `@hide`) -- configurable slow message detection
- `prepareMainLooper()` (line 122, `@Deprecated`) -- only used by the system during app startup

**Performance Monitoring:** The loop detects slow message delivery and dispatch via configurable thresholds (lines 89-95). System property `log.looper.<uid>.<thread>.slow` can override thresholds (line 174-178).

### 1.2 Handler

**File:** `frameworks/base/core/java/android/os/Handler.java` (1001 lines)

**Purpose:** Sends and processes `Message` and `Runnable` objects on a specific thread's `MessageQueue`. The primary inter-thread communication mechanism in Android.

**Key Public APIs:**

| Method | Line | 描述 |
|--------|------|-------------|
| `Handler(Looper)` | 161 | **Recommended constructor** -- explicit Looper |
| `Handler(Looper, Callback)` | 172 | With Looper and callback |
| `createAsync(Looper)` | 275 | Create handler exempt from sync barriers |
| `post(Runnable)` | 426 | Post runnable to message queue |
| `postDelayed(Runnable, long)` | 498 | Post with delay (uptimeMillis based) |
| `postAtTime(Runnable, long)` | 448 | Post at absolute uptime |
| `sendMessage(Message)` | 634 | Send a Message object |
| `sendMessageDelayed(Message, long)` | 693 | Send with delay |
| `sendEmptyMessage(int)` | 645 | Send message with only `what` code |
| `removeCallbacks(Runnable)` | 612 | Remove pending runnable posts |
| `removeMessages(int)` | 785 | Remove pending messages by `what` code |
| `removeCallbacksAndMessages(Object)` | 814 | Remove all matching callbacks/messages |
| `hasMessages(int)` | 832 | Check for pending messages |
| `hasCallbacks(Runnable)` | 866 | Check for pending callbacks |
| `obtainMessage(...)` | 354-413 | Obtain recycled Message from pool |
| `getLooper()` | 873 | Get associated Looper |
| `handleMessage(Message)` | 91 | Override to receive messages |

**Critical Design Decision -- Default Constructor Deprecated (Android 11):**

The no-argument `Handler()` constructor is `@Deprecated` as of this version (line 127-128). The deprecation reason (lines 117-125) is that implicitly choosing a Looper can cause:
- Silent message loss if the Handler quits unexpectedly
- Crashes when created on threads without a Looper
- Race conditions about which thread the Handler is bound to

**Recommended pattern:**
```java
Handler handler = new Handler(Looper.getMainLooper());
// or
Handler handler = Handler.createAsync(Looper.getMainLooper());
```

**Message Dispatch Priority (line 97-108):**
```
1. If msg.callback != null -> run the Runnable directly
2. If Handler.Callback != null and returns true -> stop
3. Otherwise -> Handler.handleMessage(msg)
```

**Hidden APIs:**
- `runWithScissors(Runnable, long)` (line 592, `@hide`) -- synchronous cross-thread execution. The Javadoc explicitly warns "This method is prone to abuse" and notes it could cause deadlocks. Internally uses `BlockingRunnable` with `wait()`/`notifyAll()`.
- `executeOrSendMessage(Message)` (line 762, `@hide`) -- dispatch immediately if on same thread, else enqueue.
- `getMain()` (line 302, `@hide`) -- cached main thread Handler singleton.
- `Handler(boolean async)` (line 193, `@hide`) -- create handler with async message support.

**Memory Leak Detection:** `FIND_POTENTIAL_LEAKS` flag (line 72) can detect non-static inner class Handlers that hold implicit references to outer Activity instances, but it is hardcoded to `false`.

**Async Messages and Sync Barriers:** Async handlers (created via `createAsync()` or the hidden `async` constructors) set `Message.setAsynchronous(true)` on all messages (line 775-777). These messages are not blocked by synchronization barriers, which the framework uses during VSYNC-driven rendering.

### 1.3 MessageQueue

**File:** `frameworks/base/core/java/android/os/MessageQueue.java` (1045 lines)

**Purpose:** Low-level priority queue of Messages, ordered by delivery time (`when` field). Uses native `epoll` for efficient blocking.

**Key Public APIs:**

| Method | Line | 描述 |
|--------|------|-------------|
| `addIdleHandler(IdleHandler)` | 124 | Register callback for queue idle events |
| `removeIdleHandler(IdleHandler)` | 142 | Unregister idle callback |
| `isIdle()` | 107 | Check if queue has no due messages |
| `addOnFileDescriptorEventListener(...)` | 194 | Monitor file descriptor events |
| `removeOnFileDescriptorEventListener(...)` | 221 | Stop monitoring file descriptor |

**Internal Architecture:**

The `next()` method (line 319-423) is the heart of the message processing:

1. Calls `nativePollOnce(ptr, timeoutMillis)` (line 335) -- blocks in native `epoll_wait`
2. Handles **sync barriers**: messages with `target == null` (line 342-347) block all synchronous messages but allow async messages through
3. Calculates next wake-up time from message queue ordering
4. Runs **IdleHandlers** when the queue is empty or all messages are in the future (lines 398-414)
5. Flushes pending Binder commands before blocking (line 332)

**Synchronization Barriers (`@hide`, `@TestApi`):**
- `postSyncBarrier()` (line 472) -- stalls synchronous messages
- `removeSyncBarrier(int token)` (line 517) -- removes a barrier

These are used internally by the framework for VSYNC coordination. A barrier is a Message with `target == null` (line 342). When encountered, the queue skips all synchronous messages and only delivers async ones. This is how the framework ensures rendering messages get priority.

**Message Enqueueing (line 549-602):**
Messages are inserted in `when`-order (sorted linked list). The queue wakes the native poll if:
- New message is at the head (earlier than current head)
- An async message is added while a sync barrier is active

**IdleHandler Interface (line 945-954):**
```java
public static interface IdleHandler {
    boolean queueIdle(); // return true to keep, false to auto-remove
}
```
Useful for deferring work until the UI is idle. Only runs during the first idle iteration per pump cycle.

---

## 2. AsyncTask

**File:** `frameworks/base/core/java/android/os/AsyncTask.java` (812 lines)

**Purpose:** Helper for running background work and publishing results on the UI thread.

**DEPRECATED as of Android 11** (line 198). The class-level Javadoc (lines 41-46) explicitly documents the problems:
- Context leaks
- Missed callbacks on configuration changes
- Crashes on configuration changes
- Inconsistent behavior across platform versions
- Swallows exceptions from `doInBackground`

**Recommended replacements:** `java.util.concurrent.Executor`, Kotlin coroutines.

**Key APIs (all deprecated):**

| Method | Line | 描述 |
|--------|------|-------------|
| `doInBackground(Params...)` | 468 | Abstract -- runs on background thread |
| `onPreExecute()` | 478 | UI thread, before background work |
| `onPostExecute(Result)` | 498 | UI thread, after completion |
| `onProgressUpdate(Progress...)` | 514 | UI thread, progress callback |
| `publishProgress(Progress...)` | 760 | Call from background to trigger progress |
| `execute(Params...)` | 670 | Start task (serial by default) |
| `executeOnExecutor(Executor, Params...)` | 708 | Start with custom executor |
| `cancel(boolean)` | 601 | Cancel the task |
| `isCancelled()` | 564 | Check cancellation state |
| `getStatus()` | 443 | PENDING, RUNNING, or FINISHED |

**Thread Pool Architecture (lines 211-265):**

```
SERIAL_EXECUTOR (default) -- SerialExecutor with ArrayDeque
    |
    v
THREAD_POOL_EXECUTOR -- ThreadPoolExecutor(core=1, max=20, keepAlive=3s)
    |
    v (on rejection)
sBackupExecutor -- ThreadPoolExecutor(core=5, max=5, unbounded queue)
```

The `SerialExecutor` (lines 297-321) wraps each task in a `Runnable` that calls `scheduleNext()` in a `finally` block, guaranteeing serial execution even if tasks throw exceptions.

**Hidden APIs:**
- `setDefaultExecutor(Executor)` (line 357, `@hide`) -- override the default serial executor process-wide
- `AsyncTask(Handler)` / `AsyncTask(Looper)` (lines 373/382, `@hide`) -- use custom callback looper

**Notable Implementation Detail:** The worker callable (line 387-404) sets thread priority to `THREAD_PRIORITY_BACKGROUND` and calls `Binder.flushPendingCommands()` after completion, ensuring IPC resources are released.

---

## 3. IPC / Binder Architecture

### 3.1 IBinder Interface

**File:** `frameworks/base/core/java/android/os/IBinder.java` (347 lines)

**Purpose:** The core interface for Android's lightweight remote procedure call (RPC) mechanism.

**Key Interface Methods:**

| Method | Line | 描述 |
|--------|------|-------------|
| `transact(int, Parcel, Parcel, int)` | 289 | Perform a generic IPC operation |
| `queryLocalInterface(String)` | 221 | Check for local implementation |
| `getInterfaceDescriptor()` | 195 | Get canonical interface name |
| `pingBinder()` | 205 | Check if remote process is alive |
| `isBinderAlive()` | 213 | Check if hosting process exists |
| `linkToDeath(DeathRecipient, int)` | 325 | Register death notification |
| `unlinkToDeath(DeathRecipient, int)` | 346 | Unregister death notification |
| `dump(FileDescriptor, String[])` | 229 | Dump state for debugging |

**Transaction Code Ranges:**
- `FIRST_CALL_TRANSACTION` (0x00000001) to `LAST_CALL_TRANSACTION` (0x00ffffff) -- user-defined codes
- System codes: `PING_TRANSACTION`, `DUMP_TRANSACTION`, `INTERFACE_TRANSACTION`, `SHELL_COMMAND_TRANSACTION` (`@hide`)

**IPC Size Limit:** `MAX_IPC_SIZE = 64 * 1024` bytes (line 182, `@hide`). The public API `getSuggestedMaxIpcSizeBytes()` (line 188) exposes this.

**One-way Flag:** `FLAG_ONEWAY = 0x00000001` (line 170) -- caller returns immediately without waiting for result. The system guarantees ordering for multiple oneway calls to the same IBinder.

**DeathRecipient (line 298-307):** Callback interface for process death notifications. Essential for service lifecycle management. The hidden `binderDied(IBinder who)` default method (line 304) provides the dying binder reference.

**Easter Eggs:** `TWEET_TRANSACTION` (line 137) and `LIKE_TRANSACTION` (line 150) are humorous protocol codes with tongue-in-cheek Javadoc.

### 3.2 Binder

**File:** `frameworks/base/core/java/android/os/Binder.java` (1205 lines)

**Purpose:** Base class for remotable objects. The Java-side implementation of the Binder IPC mechanism.

**Key Public APIs:**

| Method | Line | 描述 |
|--------|------|-------------|
| `Binder()` | 580 | Default constructor |
| `Binder(String descriptor)` | 596 | Constructor with descriptor (for tokens) |
| `getCallingPid()` | 289 | PID of current IPC caller |
| `getCallingUid()` | 299 | UID of current IPC caller |
| `getCallingUserHandle()` | 333 | UserHandle of caller |
| `clearCallingIdentity()` | 354 | Reset caller identity (returns token) |
| `restoreCallingIdentity(long)` | 366 | Restore saved identity |
| `flushPendingCommands()` | 549 | Flush pending IPC commands to kernel |
| `attachInterface(IInterface, String)` | 617 | Associate interface with Binder |
| `onTransact(int, Parcel, Parcel, int)` | 782 | Override to handle IPC calls |
| `getCallingWorkSourceUid()` | 485 | Get work source UID (untrusted) |
| `setCallingWorkSourceUid(int)` | 473 | Set work source for attribution |

**Security-Critical Pattern -- Clearing Calling Identity:**

```java
long token = Binder.clearCallingIdentity();
try {
    // Calls here will use the local process identity,
    // not the remote caller's identity
} finally {
    Binder.restoreCallingIdentity(token);
}
```

This pattern (documented at line 337-365) is used extensively throughout system services when making calls to other services on behalf of a caller. Without it, nested permission checks would fail because they'd see the wrong caller UID.

**Hidden Convenience Methods:**
- `withCleanCallingIdentity(ThrowingRunnable)` (line 377, `@hide`) -- lambda-based version
- `withCleanCallingIdentity(ThrowingSupplier<T>)` (line 401, `@hide`) -- with return value

**Blocking Call Detection:**
- `setWarnOnBlocking(boolean)` (line 197, `@hide`) -- system process uses this to detect blocking calls to untrusted external code
- `allowBlocking(IBinder)` (line 213, `@hide`) -- override for known-safe system interfaces

**ProxyTransactListener (`@SystemApi`, line 679-706):** An observer interface for monitoring all proxy-side binder calls. Used for:
- Transaction tracing
- Work source propagation (`PropagateWorkSourceTransactListener`, line 716)

**onTransact Implementation (line 782):** The base implementation handles system transaction codes: `INTERFACE_TRANSACTION` returns the descriptor, `DUMP_TRANSACTION` calls `dump()`, `SHELL_COMMAND_TRANSACTION` delegates to `shellCommand()`.

### 3.3 Parcel

**File:** `frameworks/base/core/java/android/os/Parcel.java` (3683 lines)

**Purpose:** Container for marshalling data across IPC boundaries. High-performance serialization optimized for IPC, NOT for persistent storage.

**Key warning from Javadoc (lines 69-75):** "Parcel is **not** a general-purpose serialization mechanism... it is not appropriate to place any Parcel data in to persistent storage."

**Data Type Categories:**

1. **Primitives:** `writeInt`/`readInt`, `writeLong`/`readLong`, `writeFloat`/`readFloat`, `writeDouble`/`readDouble`, `writeString`/`readString`, `writeByte`/`readByte`
2. **Primitive Arrays:** `writeIntArray`, `createIntArray`, `writeByteArray`, etc.
3. **Parcelables:** `writeParcelable`/`readParcelable` (with class info), `writeTypedObject`/`readTypedObject` (without class info, more efficient)
4. **Bundles:** `writeBundle`/`readBundle` -- type-safe key-value maps
5. **Active Objects:** `writeStrongBinder`/`readStrongBinder` -- IBinder references, `writeFileDescriptor`/`readFileDescriptor`
6. **Untyped Containers:** `writeValue`/`readValue`, `writeList`/`readList`

**Object Pool:** `obtain()` / `recycle()` pattern for avoiding allocation overhead.

### 3.4 Parcelable Interface

**File:** `frameworks/base/core/java/android/os/Parcelable.java` (173 lines)

**Purpose:** Interface for objects that can be serialized into a Parcel for IPC transport.

**Required Components:**
1. `describeContents()` -- return `CONTENTS_FILE_DESCRIPTOR` if containing FDs
2. `writeToParcel(Parcel, int)` -- serialize to Parcel
3. Static `CREATOR` field implementing `Parcelable.Creator<T>` -- factory for deserialization

**Write Flags:**
- `PARCELABLE_WRITE_RETURN_VALUE` (0x0001) -- object is a return value, may release resources
- `PARCELABLE_ELIDE_DUPLICATES` (0x0002, `@hide`) -- parent manages duplicate data

**ClassLoaderCreator (line 160-172):** Extended `Creator` that receives a `ClassLoader`, useful for cross-process class loading.

---

## 4. Bundle

**File:** `frameworks/base/core/java/android/os/Bundle.java` (1363 lines)

**Purpose:** Type-safe mapping from String keys to various `Parcelable` values. The primary mechanism for passing structured data between components (Activities, Fragments, Services).

**Class Hierarchy:** `Bundle extends BaseBundle implements Cloneable, Parcelable`

**Key Constants:**
- `Bundle.EMPTY` (line 48) -- immutable empty Bundle singleton
- `Bundle.STRIPPED` (line 54, `@hide`) -- sentinel for stripped extras

**Internal Flags (lines 40-46):**
- `FLAG_HAS_FDS` -- contains file descriptors
- `FLAG_HAS_FDS_KNOWN` -- FD presence has been computed
- `FLAG_ALLOW_FDS` -- whether FDs are permitted

**Key Methods (inherited from BaseBundle + Bundle-specific):**
- `putString(String, String)`, `getString(String)`, `getString(String, String)`
- `putInt(String, int)`, `getInt(String)`, `getInt(String, int)`
- `putParcelable(String, Parcelable)`, `getParcelable(String)`
- `putBundle(String, Bundle)`, `getBundle(String)`
- `putBinder(String, IBinder)`, `getBinder(String)` -- for passing Binder tokens
- `deepCopy()` -- deep copy vs. shallow `Bundle(Bundle)` constructor
- `hasFileDescriptors()` -- checks if Bundle contains FDs (lazy scan)

**Lazy Unparcelling:** Bundle data from a Parcel is NOT immediately unparcelled. The raw `mParcelledData` is kept until first access, then unparcelled on demand. This is an important performance optimization for Intent extras that may never be read.

---

## 5. Build Information

**File:** `frameworks/base/core/java/android/os/Build.java` (1335 lines)

**Purpose:** Static information about the current build, extracted from system properties (`ro.build.*`, `ro.product.*`).

**Key Public Fields:**

| Field | Line | 描述 |
|-------|------|-------------|
| `DEVICE` | 60 | Device name (e.g., "walleye") |
| `MODEL` | 88 | End-user-visible model name |
| `MANUFACTURER` | 82 | Product manufacturer |
| `BRAND` | 85 | Consumer-visible brand |
| `PRODUCT` | 57 | Overall product name |
| `BOARD` | 63 | Underlying board name |
| `HARDWARE` | 106 | Hardware name from kernel |
| `BOOTLOADER` | 91 | Bootloader version |
| `DISPLAY` | 54 | Build ID for display |
| `SUPPORTED_ABIS` | 188 | Ordered list of supported ABIs |
| `SERIAL` | 126 | **DEPRECATED** -- always returns UNKNOWN |

**Build.VERSION (line 236):**

| Field | Line | 描述 |
|-------|------|-------------|
| `SDK_INT` | 288 | SDK version as integer (e.g., 30 for Android 11) |
| `RELEASE` | 251 | User-visible version string (e.g., "11") |
| `CODENAME` | 351 | "REL" for release builds, codename otherwise |
| `SECURITY_PATCH` | 269 | Security patch date string |
| `BASE_OS` | 263 | Base OS build string |
| `PREVIEW_SDK_INT` | 324 | Preview SDK revision (0 for release) |
| `INCREMENTAL` | 242 | Source control changelist/hash |

**Build.VERSION_CODES (line 389):** Enumeration of all Android API levels from `BASE` (1) through `R` (30, Android 11).

**Security-Sensitive API -- `getSerial()` (line 169):**
Requires `READ_PRIVILEGED_PHONE_STATE` permission. Starting with API 29, device identifiers are heavily restricted. The method delegates to `IDeviceIdentifiersPolicyService` via Binder IPC.

**Hidden APIs:**
- `IS_EMULATOR` (line 114, `@hide`, `@TestApi`) -- checks `ro.kernel.qemu`
- `VERSION.FIRST_SDK_INT` (line 303, `@hide`, `@TestApi`) -- SDK version that originally shipped
- `VERSION.RESOURCES_SDK_INT` (line 371, `@hide`, `@TestApi`) -- `SDK_INT + ACTIVE_CODENAMES.length`
- `VERSION.MIN_SUPPORTED_TARGET_SDK_INT` (line 380, `@hide`) -- minimum target SDK

---

## 6. Environment

**File:** `frameworks/base/core/java/android/os/Environment.java` (1434 lines)

**Purpose:** Access to standard filesystem directories and external storage state.

**Key Public APIs:**

| Method | Line | 描述 |
|--------|------|-------------|
| `getRootDirectory()` | 218 | `/system` -- read-only system partition |
| `getDataDirectory()` | 353 | `/data` -- internal data |
| `getDownloadCacheDirectory()` | 1045 | `/cache` -- download cache |
| `getExternalStorageDirectory()` | 677 | Primary external storage root |
| `getExternalStoragePublicDirectory(type)` | 962 | Public directories by type |
| `getExternalStorageState()` | 1142 | Storage mount state |
| `getExternalStorageState(File)` | 1165 | State for specific path |
| `isExternalStorageEmulated()` | ~1190 | Whether external storage is emulated |
| `isExternalStorageRemovable()` | ~1210 | Whether storage is physically removable |
| `getStorageDirectory()` | 227 | `/storage` -- mount point root |

**Public Directory Type Constants:**
- `DIRECTORY_MUSIC`, `DIRECTORY_PODCASTS`, `DIRECTORY_RINGTONES`
- `DIRECTORY_ALARMS`, `DIRECTORY_NOTIFICATIONS`
- `DIRECTORY_PICTURES`, `DIRECTORY_MOVIES`
- `DIRECTORY_DOWNLOADS`, `DIRECTORY_DCIM`, `DIRECTORY_DOCUMENTS`
- `DIRECTORY_SCREENSHOTS`, `DIRECTORY_AUDIOBOOKS`

**Storage State Constants:**
- `MEDIA_MOUNTED` -- read/write access
- `MEDIA_MOUNTED_READ_ONLY` -- read-only access
- `MEDIA_REMOVED`, `MEDIA_BAD_REMOVAL`, `MEDIA_UNMOUNTED`
- `MEDIA_CHECKING`, `MEDIA_EJECTING`, `MEDIA_UNKNOWN`

**Scoped Storage (Android 10+/11):**

Lines 94-132 define the Scoped Storage compatibility flags:
- `DEFAULT_SCOPED_STORAGE` (ChangeId 149924527) -- enabled by default for all apps
- `FORCE_ENABLE_SCOPED_STORAGE` (ChangeId 132649864) -- `@Disabled` by default, strictly enforces scoped storage

Opt-out mechanisms documented (lines 96-101):
- Target SDK < Q
- Target SDK = Q + `requestLegacyExternalStorage` manifest attribute
- Target SDK > Q + upgrading + `preserveLegacyExternalStorage`

**Hidden System APIs (`@SystemApi`):**
- `getOemDirectory()` (line 239) -- `/oem` partition
- `getOdmDirectory()` (line 251) -- `/odm` partition
- `getVendorDirectory()` (line 262) -- `/vendor` partition
- `getProductDirectory()` (line 274) -- `/product` partition
- `getSystemExtDirectory()` (line 301) -- `/system_ext` partition
- Numerous `getDataSystem*Directory()` methods for per-user system data

**UserEnvironment (line 150, `@hide`):** Inner class providing per-user external storage paths. Each user gets isolated external storage via `StorageManager.getVolumeList()`.

---

## 7. PowerManager and BatteryManager

### 7.1 PowerManager

**File:** `frameworks/base/core/java/android/os/PowerManager.java` (2634 lines)

**Purpose:** Controls device power state, wake locks, and power-related queries.

**Obtained via:** `context.getSystemService(Context.POWER_SERVICE)` (`@SystemService`)

**Wake Lock Levels:**

| Constant | Line | CPU | Screen | Keyboard |
|----------|------|-----|--------|----------|
| `PARTIAL_WAKE_LOCK` | 80 | ON | off | off |
| `SCREEN_DIM_WAKE_LOCK` | 97 | ON | dim | off |
| `SCREEN_BRIGHT_WAKE_LOCK` | 114 | ON | bright | off |
| `FULL_WAKE_LOCK` | 132 | ON | bright | bright |
| `PROXIMITY_SCREEN_OFF_WAKE_LOCK` | 153 | ON | proximity | -- |
| `DOZE_WAKE_LOCK` | 168 | -- | low power | -- |
| `DRAW_WAKE_LOCK` | 182 | -- | draw only | -- |

All screen-level wake locks are `@Deprecated` in favor of `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON`.

**Wake Lock Flags:**
- `ACQUIRE_CAUSES_WAKEUP` (line 202) -- turn screen on when acquired
- `ON_AFTER_RELEASE` (line 214) -- poke user activity timer on release

**Key Public APIs:**

| Method | Line | 描述 |
|--------|------|-------------|
| `newWakeLock(int, String)` | 1101 | Create a new WakeLock |
| `isWakeLockLevelSupported(int)` | 1418 | Check if level is supported |
| `isInteractive()` | ~1450 | Whether device is awake |
| `isDeviceIdleMode()` | ~1500 | Whether in Doze mode |
| `isPowerSaveMode()` | ~1480 | Whether battery saver is active |
| `isScreenOn()` | ~deprecated | Use `isInteractive()` instead |

**WakeLock Inner Class (line 2320):**
- `acquire()` / `acquire(long timeout)` -- acquire wake lock with optional auto-release
- `release()` / `release(int flags)` -- release wake lock
- `setReferenceCounted(boolean)` -- default true, must balance acquire/release
- `setWorkSource(WorkSource)` -- attribute power usage to another UID

**Requires Permission:** `android.permission.WAKE_LOCK` for any WakeLock usage.

**Hidden/System APIs (`@SystemApi`):**
- User activity event types: `USER_ACTIVITY_EVENT_OTHER/BUTTON/TOUCH/ACCESSIBILITY` (lines 295-316)
- Go-to-sleep reason codes: `GO_TO_SLEEP_REASON_APPLICATION/TIMEOUT/POWER_BUTTON/...` (lines 354-416)
- `GO_TO_SLEEP_FLAG_NO_DOZE` (line 446) -- skip doze state
- Various brightness constants (lines 240-285)
- `DOZE_WAKE_LOCK` and `DRAW_WAKE_LOCK` require `DEVICE_POWER` permission

### 7.2 BatteryManager

**File:** `frameworks/base/core/java/android/os/BatteryManager.java` (403 lines)

**Purpose:** Battery state information and querying properties.

**Obtained via:** `context.getSystemService(Context.BATTERY_SERVICE)` (`@SystemService`)

**Intent Extras for `ACTION_BATTERY_CHANGED`:**

| Extra | Line | Type | 描述 |
|-------|------|------|-------------|
| `EXTRA_STATUS` | 42 | int | Charging status constant |
| `EXTRA_HEALTH` | 48 | int | Battery health constant |
| `EXTRA_PRESENT` | 54 | boolean | Battery present |
| `EXTRA_LEVEL` | 61 | int | Current level (0 to EXTRA_SCALE) |
| `EXTRA_SCALE` | 75 | int | Maximum level |
| `EXTRA_PLUGGED` | 90 | int | Power source type |
| `EXTRA_VOLTAGE` | 96 | int | Current voltage |
| `EXTRA_TEMPERATURE` | 102 | int | Current temperature |
| `EXTRA_TECHNOLOGY` | 108 | String | Battery technology |

**Status Constants:** `BATTERY_STATUS_UNKNOWN/CHARGING/DISCHARGING/NOT_CHARGING/FULL` (lines 168-172)

**Health Constants:** `BATTERY_HEALTH_UNKNOWN/GOOD/OVERHEAT/DEAD/OVER_VOLTAGE/UNSPECIFIED_FAILURE/COLD` (lines 175-181)

**Plug Types:** `BATTERY_PLUGGED_AC` (1), `BATTERY_PLUGGED_USB` (2), `BATTERY_PLUGGED_WIRELESS` (4) (lines 186-190)

**Key Public APIs:**

| Method | Line | 描述 |
|--------|------|-------------|
| `isCharging()` | 285 | Whether battery level is increasing |
| `getIntProperty(int)` | 329 | Query int battery property |
| `getLongProperty(int)` | 349 | Query long battery property |
| `computeChargeTimeRemaining()` | 373 | Estimated time to full charge |

**Battery Property IDs:**
- `BATTERY_PROPERTY_CHARGE_COUNTER` (1) -- capacity in microampere-hours
- `BATTERY_PROPERTY_CURRENT_NOW` (2) -- instantaneous current in microamperes
- `BATTERY_PROPERTY_CURRENT_AVERAGE` (3) -- average current
- `BATTERY_PROPERTY_CAPACITY` (4) -- remaining percentage
- `BATTERY_PROPERTY_ENERGY_COUNTER` (5) -- remaining energy in nanowatt-hours
- `BATTERY_PROPERTY_STATUS` (6) -- charge status

**Hidden APIs:**
- `EXTRA_INVALID_CHARGER`, `EXTRA_MAX_CHARGING_CURRENT`, `EXTRA_MAX_CHARGING_VOLTAGE` (lines 117-133)
- `setChargingStateUpdateDelayMillis(int)` (line 396, `@SystemApi`) -- ML/heuristic-based charging state delay

---

## 8. SystemClock

**File:** `frameworks/base/core/java/android/os/SystemClock.java` (345 lines)

**Purpose:** Core timekeeping facilities providing multiple clock sources for different use cases.

**Three Clock Types (documented in class Javadoc, lines 36-72):**

| Clock | Method | Stops in Sleep | Adjustable | Use Case |
|-------|--------|---------------|------------|----------|
| Wall clock | `System.currentTimeMillis()` | No | Yes (user/network) | Calendar, real-world dates |
| Uptime | `uptimeMillis()` | Yes | No | Interval timing, Handler |
| Elapsed realtime | `elapsedRealtime()` | No | No | General interval timing |

**Key Public APIs:**

| Method | Line | 描述 |
|--------|------|-------------|
| `sleep(long ms)` | 124 | Like `Thread.sleep()` but ignores `InterruptedException` |
| `uptimeMillis()` | 178 | Milliseconds since boot (excludes deep sleep) |
| `elapsedRealtime()` | 201 | Milliseconds since boot (includes deep sleep) |
| `elapsedRealtimeNanos()` | 224 | Nanoseconds since boot (includes deep sleep) |
| `currentThreadTimeMillis()` | 232 | Thread CPU time in milliseconds |
| `setCurrentTimeMillis(long)` | 153 | Set wall clock (requires permission) |
| `currentGnssTimeClock()` | 322 | GNSS-synchronized Clock |

**Important:** Most Android framework timing uses `uptimeMillis()` as the time base. Handler delays, animation timing, and touch event timestamps all use this clock. Deep sleep pauses this clock, so delayed messages will execute after wake-up with accumulated delay.

**Hidden APIs:**
- `currentNetworkTimeMillis()` (line 273) -- NTP-synchronized time
- `currentTimeMicro()` (line 254) -- wall time in microseconds
- `currentThreadTimeMicro()` (line 243) -- thread CPU time in microseconds

**Implementation of `sleep()` (lines 124-145):** Unlike `Thread.sleep()`, this preserves the interrupted state by re-interrupting the thread after completing the full sleep duration. Uses a loop to handle interruptions without losing sleep time.

---

## 9. Process Management

**File:** `frameworks/base/core/java/android/os/Process.java` (1445 lines)

**Purpose:** Tools for managing OS processes, including process creation, UID/GID definitions, thread priorities, and signals.

**Well-Known UID Constants:**

| Constant | Value | Line | 描述 |
|----------|-------|------|-------------|
| `ROOT_UID` | 0 | 56 | Root user |
| `SYSTEM_UID` | 1000 | 61 | System server |
| `PHONE_UID` | 1001 | 66 | Telephony |
| `BLUETOOTH_UID` | 1002 | 141 | Bluetooth |
| `SHELL_UID` | 2000 | 71 | ADB shell |
| `WIFI_UID` | 1010 | 84 | WiFi services |
| `FIRST_APPLICATION_UID` | 10000 | 256 | First app UID |
| `LAST_APPLICATION_UID` | 19999 | 262 | Last app UID |
| `FIRST_ISOLATED_UID` | 99000 | 291 | First isolated process UID |

**Thread Priority Constants (lines 332-425):**

| Constant | Value | Line | 描述 |
|----------|-------|------|-------------|
| `THREAD_PRIORITY_DEFAULT` | 0 | 332 | Standard application |
| `THREAD_PRIORITY_LOWEST` | 19 | 347 | Lowest priority |
| `THREAD_PRIORITY_BACKGROUND` | 10 | 357 | Background threads |
| `THREAD_PRIORITY_FOREGROUND` | -2 | 368 | Interactive UI |
| `THREAD_PRIORITY_DISPLAY` | -4 | 378 | Display updates |
| `THREAD_PRIORITY_URGENT_DISPLAY` | -8 | 388 | Compositing/input |
| `THREAD_PRIORITY_AUDIO` | -16 | 406 | Audio processing |
| `THREAD_PRIORITY_URGENT_AUDIO` | -19 | 415 | Critical audio |

**Key Public APIs:**

| Method | 描述 |
|--------|-------------|
| `myPid()` | Current process PID |
| `myTid()` | Current thread TID |
| `myUid()` | Current process UID |
| `myUserHandle()` | Current user handle |
| `setThreadPriority(int)` | Set calling thread's priority |
| `setThreadPriority(int tid, int priority)` | Set specific thread's priority |
| `getThreadPriority(int tid)` | Get thread's current priority |
| `killProcess(int pid)` | Send SIGKILL to process |
| `sendSignal(int pid, int signal)` | Send signal to process |

**Signal Constants:** `SIGNAL_QUIT` (3), `SIGNAL_KILL` (9), `SIGNAL_USR1` (10) (lines 532-534)

**Hidden Process Management:**
- Zygote policy flags (lines 555-580): `ZYGOTE_POLICY_FLAG_LATENCY_SENSITIVE`, `ZYGOTE_POLICY_FLAG_BATCH_LAUNCH`, `ZYGOTE_POLICY_FLAG_SYSTEM_PROCESS`
- Thread groups (lines 467-530): `THREAD_GROUP_DEFAULT`, `THREAD_GROUP_BACKGROUND`, `THREAD_GROUP_TOP_APP`, etc.
- Scheduling policies (lines 430-455): `SCHED_OTHER`, `SCHED_FIFO`, `SCHED_RR`, `SCHED_BATCH`, `SCHED_IDLE`

---

## 10. UserManager

**File:** `frameworks/base/core/java/android/os/UserManager.java` (4370 lines)

**Purpose:** Manages users and user profiles on multi-user Android devices.

**Obtained via:** `context.getSystemService(Context.USER_SERVICE)` (`@SystemService`)

**User Type Constants (`@SystemApi`, `@hide`):**

| Constant | Line | 描述 |
|----------|------|-------------|
| `USER_TYPE_FULL_SYSTEM` | 100 | System user (pre-existing) |
| `USER_TYPE_FULL_SECONDARY` | 108 | Regular secondary user |
| `USER_TYPE_FULL_GUEST` | 115 | Guest user |
| `USER_TYPE_FULL_DEMO` | 121 | Demo user |
| `USER_TYPE_FULL_RESTRICTED` | 128 | Restricted profile |
| `USER_TYPE_PROFILE_MANAGED` | 137 | Work profile (DPC-managed) |
| `USER_TYPE_SYSTEM_HEADLESS` | 146 | Non-human system user |

**Key Public APIs (selected):**

| Method | 描述 |
|--------|-------------|
| `isUserAGoat()` | Easter egg -- checks for com.coffeestainstudios.goatsimulator |
| `getUserCount()` | Number of users on device |
| `isUserRunning(UserHandle)` | Whether user's process is started |
| `isUserUnlocked()` | Whether user storage is unlocked |
| `isSystemUser()` | Whether current user is system user |
| `isManagedProfile()` | Whether running in a work profile |
| `isGuestUser()` | Whether current user is a guest |
| `isDemoUser()` | Whether current user is demo |
| `isQuietModeEnabled(UserHandle)` | Whether quiet mode is active |
| `requestQuietModeEnabled(boolean, UserHandle)` | Toggle quiet mode |

**User Restrictions System:** A large set of `DISALLOW_*` string constants (hundreds of them) that restrict user capabilities:
- `DISALLOW_INSTALL_APPS`, `DISALLOW_UNINSTALL_APPS`
- `DISALLOW_USB_FILE_TRANSFER`, `DISALLOW_MODIFY_ACCOUNTS`
- `DISALLOW_CONFIG_WIFI`, `DISALLOW_CONFIG_BLUETOOTH`
- `DISALLOW_CAMERA`, `DISALLOW_MICROPHONE`
- `DISALLOW_OUTGOING_CALLS`, `DISALLOW_SMS`
- etc.

These are set via `DevicePolicyManager` and queried via `getUserRestrictions()` / `hasUserRestriction(String)`.

---

## 11. Permission Manager

**File:** `frameworks/base/core/java/android/permission/PermissionManager.java` (719 lines)

**Purpose:** System-level service for accessing permission capabilities. Almost entirely `@hide` / `@SystemApi`.

**Obtained via:** `context.getSystemService(Context.PERMISSION_SERVICE)` (`@SystemService`)

**The only public API:**

| Method | Line | 描述 |
|--------|------|-------------|
| `getSplitPermissions()` | 170 | Get permissions that were split across API levels |

**Split Permissions (line 152-186):**

The `SplitPermissionInfo` class (line 386) represents permissions that were split in newer API levels. For example, `ACCESS_COARSE_LOCATION` was split: apps targeting < Q automatically receive `ACCESS_BACKGROUND_LOCATION` when granted the old permission.

Key methods on `SplitPermissionInfo`:
- `getSplitPermission()` -- the original permission
- `getNewPermissions()` -- the new granular permissions
- `getTargetSdk()` -- the API level where the split occurred

**System APIs (`@SystemApi`):**

| Method | Line | Permission Required | 描述 |
|--------|------|-------------------|-------------|
| `getRuntimePermissionsVersion()` | 123 | `ADJUST_RUNTIME_PERMISSIONS_POLICY` | Get permission DB version |
| `setRuntimePermissionsVersion(int)` | 144 | `ADJUST_RUNTIME_PERMISSIONS_POLICY` | Set permission DB version |
| `getAutoRevokeExemptionRequestedPackages()` | 325 | `ADJUST_RUNTIME_PERMISSIONS_POLICY` | Auto-revoke exempt packages |
| `getAutoRevokeExemptionGrantedPackages()` | 345 | `ADJUST_RUNTIME_PERMISSIONS_POLICY` | Granted auto-revoke exemptions |
| `startOneTimePermissionSession(...)` | 475 | `MANAGE_ONE_TIME_PERMISSION_SESSIONS` | One-time permission tracking |
| `stopOneTimePermissionSession(String)` | 496 | `MANAGE_ONE_TIME_PERMISSION_SESSIONS` | Stop one-time session |
| `checkDeviceIdentifierAccess(...)` | 519 | -- | Check device ID access |

**One-Time Permissions (Android 11 Feature, lines 442-503):**

The `startOneTimePermissionSession()` method implements Android 11's one-time permission grants. Parameters control:
- `timeoutMillis` -- how long the app can be inactive before revocation
- `importanceToResetTimer` -- process importance level that resets the timer
- `importanceToKeepSessionAlive` -- importance level that extends the session

**Permission Cache (lines 605-630):**

`PermissionManager` maintains a `PropertyInvalidatedCache` for permission checks, keyed by `(permission, uid)` -- note that `pid` is included for tracking but NOT equality comparison (lines 555-602). This is an important security detail: permission checks are UID-based, not PID-based.

**Hidden Telephony Permission Grants (lines 197-312):**
Multiple methods for granting default permissions to telephony components:
- `grantDefaultPermissionsToLuiApp()`
- `grantDefaultPermissionsToEnabledImsServices()`
- `grantDefaultPermissionsToEnabledTelephonyDataServices()`
- `grantDefaultPermissionsToEnabledCarrierApps()`

All require `GRANT_RUNTIME_PERMISSIONS_TO_TELEPHONY_DEFAULTS` permission.

### Related Permission Files

**File:** `frameworks/base/core/java/android/permission/PermissionControllerService.java`
An abstract `Service` that permission controller apps must implement. Handles runtime permission UI and one-time permission timeouts.

**File:** `frameworks/base/core/java/android/permission/PermissionControllerManager.java`
Client-side interface for communicating with the PermissionControllerService.

---

## 12. Security APIs

### 12.1 NetworkSecurityPolicy

**File:** `frameworks/base/core/java/android/security/NetworkSecurityPolicy.java` (119 lines)

**Purpose:** Controls cleartext (non-TLS) network traffic policy for the process.

**Key Public APIs:**

| Method | Line | 描述 |
|--------|------|-------------|
| `getInstance()` | 45 | Get singleton policy instance |
| `isCleartextTrafficPermitted()` | 68 | Check if cleartext traffic is allowed process-wide |
| `isCleartextTrafficPermitted(String hostname)` | 78 | Check for specific hostname |

Delegates to `libcore.net.NetworkSecurityPolicy` which is configured by the Network Security Configuration XML framework.

**Hidden APIs:**
- `setCleartextTrafficPermitted(boolean)` (line 91) -- used during app initialization
- `handleTrustStorageUpdate()` (line 100) -- refresh certificate trust store
- `getApplicationConfigForPackage(Context, String)` (line 112) -- get security config for a package

### 12.2 ConfirmationPrompt

**File:** `frameworks/base/core/java/android/security/ConfirmationPrompt.java`

**Purpose:** Trusted UI confirmation dialog backed by Android Protected Confirmation (hardware-backed).

### 12.3 FileIntegrityManager

**File:** `frameworks/base/core/java/android/security/FileIntegrityManager.java`

**Purpose:** Provides access to file integrity features (fs-verity).

### 12.4 Android Keystore

**Directory:** `frameworks/base/keystore/java/android/security/keystore/`

**Key Files:**
- `AndroidKeyStoreProvider.java` -- JCA Provider for Android Keystore
- `AndroidKeyStoreSpi.java` -- `KeyStoreSpi` implementation
- `AndroidKeyStoreKeyGeneratorSpi.java` -- Symmetric key generation
- `AndroidKeyStoreKeyPairGeneratorSpi.java` -- Asymmetric key pair generation
- `KeyGenParameterSpec.java` -- Key generation parameters
- `KeyProtection.java` -- Import protection parameters
- `KeyProperties.java` -- Key algorithm/purpose/mode constants

The Android Keystore is a JCA provider that stores cryptographic keys in hardware-backed storage (TEE/StrongBox). Keys are bound to the device and can require user authentication for use.

---

## 13. Architectural Patterns and Observations

### 13.1 The Handler/Looper/MessageQueue Threading Model

This trio implements a **single-threaded event loop** pattern. Key design properties:

1. **Thread Affinity:** Each Handler is permanently bound to one Looper/thread. Messages posted to a Handler always execute on that thread.
2. **Cooperative Multitasking:** Messages are processed one at a time. Long-running handlers block the entire queue.
3. **Priority via Sync Barriers:** The framework uses sync barriers + async messages for rendering priority, not preemption.
4. **Native Integration:** `MessageQueue.next()` blocks in native `epoll_wait`, making the event loop efficient when idle.

### 13.2 Binder IPC Security Model

1. **Caller Identity:** `getCallingUid()` and `getCallingPid()` provide the identity of the IPC caller for permission checks.
2. **Identity Clearing:** `clearCallingIdentity()` / `restoreCallingIdentity()` is essential when a system service makes calls on behalf of a caller.
3. **Death Notifications:** `linkToDeath()` enables cleanup when remote processes die.
4. **Transaction Size Limit:** 64KB (`MAX_IPC_SIZE`). Large data must use `ashmem` or content providers.
5. **One-Way Semantics:** `FLAG_ONEWAY` provides fire-and-forget IPC with ordering guarantees per-IBinder.
6. **Work Source Attribution:** `setCallingWorkSourceUid()` allows battery usage attribution to the true originator.

### 13.3 Parcel/Parcelable vs. Serializable

Parcelable is Android's preferred serialization:
- **Performance:** ~10x faster than Serializable (no reflection)
- **Scope:** IPC only, not persistent storage
- **Type Safety:** Compile-time checked via CREATOR pattern
- **IBinder Support:** Can marshal live Binder references across processes

### 13.4 Hidden API Surface

A significant portion of the OS APIs are hidden from app developers:

| Class | Total Lines | Hidden/System API Density |
|-------|------------|--------------------------|
| PowerManager | 2,634 | Very High -- most constants and methods are `@hide`/`@SystemApi` |
| UserManager | 4,370 | Very High -- user types, restrictions heavily hidden |
| PermissionManager | 719 | Almost entirely `@hide`/`@SystemApi` |
| Process | 1,445 | High -- most UID constants and thread groups hidden |
| Environment | 1,434 | Medium -- many partition directories are `@SystemApi` |
| Build | 1,335 | Low -- most fields are public |
| Handler | 1,001 | Low -- `runWithScissors` and async constructors hidden |

### 13.5 Deprecation Patterns in Android 11

| API | Replacement |
|-----|------------|
| `Handler()` (no-arg) | `Handler(Looper.getMainLooper())` |
| `AsyncTask` | `java.util.concurrent`, Kotlin coroutines |
| `SCREEN_DIM_WAKE_LOCK` | `FLAG_KEEP_SCREEN_ON` |
| `SCREEN_BRIGHT_WAKE_LOCK` | `FLAG_KEEP_SCREEN_ON` |
| `FULL_WAKE_LOCK` | `FLAG_KEEP_SCREEN_ON` |
| `Build.SERIAL` | `Build.getSerial()` (with permission) |
| `Environment.getExternalStorageDirectory()` | Scoped storage via `MediaStore` / `Context.getExternalFilesDir()` |

### 13.6 Android 11 Specific Features Visible in Code

1. **One-Time Permissions** (PermissionManager, line 442) -- permissions that auto-revoke when app becomes inactive
2. **Auto-Revoke** (PermissionManager, lines 315-352) -- unused permission auto-revocation with exemption mechanism
3. **Scoped Storage enforcement** (Environment, lines 94-132) -- `DEFAULT_SCOPED_STORAGE` and `FORCE_ENABLE_SCOPED_STORAGE` change IDs
4. **Handler default constructor deprecation** -- explicit Looper requirement
5. **AsyncTask full deprecation** -- entire class marked `@Deprecated`
6. **Process.ZYGOTE_POLICY_FLAG_LATENCY_SENSITIVE** (line 564) -- USAP (Unspecialized App Process) pool support

---

*Report generated from Android 11 (API level 30) AOSP source code at `~/aosp-android-11/`.*
