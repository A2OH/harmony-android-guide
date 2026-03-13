# A2OH Shim Layer — Master Index

## Goal
Run unmodified Android APKs on OpenHarmony by providing a **Java shim layer** that implements `android.*` framework classes, internally delegating to OH native APIs via a JNI/FFI bridge.

## Architecture

```
┌──────────────────────────────────┐
│  Android APK (unchanged .dex)    │  Layer 0: App bytecode
│  calls android.* framework APIs  │
├──────────────────────────────────┤
│  Java Shim Layer                 │  Layer 1: android.* classes (Java)
│  Same signatures as AOSP         │  implements Android API surface
│  Delegates to OH via bridge      │
├──────────────────────────────────┤
│  JNI Bridge                      │  Layer 2: Java ↔ OH native glue
│  Converts Java calls → OH calls  │  Handles type marshalling
│  Manages OH lifecycle            │
├──────────────────────────────────┤
│  OH Native APIs                  │  Layer 3: @ohos.* actual platform
│  Dalvik/JVM Runtime on OHOS      │
└──────────────────────────────────┘
```

## Shim Tiers (by implementation difficulty)

### Tier 1 — Direct Mapping (score 8-10, ~7,500 APIs)
OH has a near-identical API. Shim is a thin wrapper.
- `SharedPreferences` → `@ohos.data.preferences`
- `SQLiteDatabase` → `@ohos.data.relationalStore`
- `NotificationManager` → `@ohos.notificationManager`
- `Sensor` APIs → `@ohos.sensor`
- `HttpURLConnection` → `@ohos.net.http`

### Tier 2 — Composite Mapping (score 5-7, ~14,800 APIs)
OH has equivalent functionality but different shape. Shim needs adaptation logic.
- `Intent` → `Want` (parameter mapping)
- `Activity` lifecycle → `UIAbility` lifecycle (event reordering)
- `AlarmManager` → `reminderAgentManager` (different scheduling model)
- `ContentProvider` → `DataShareExtensionAbility`

### Tier 3 — Structural Gap (score 1-4, ~34,900 APIs)
OH has no direct equivalent. Needs custom implementation or polyfill.
- `android.widget.*` / `android.view.*` → ArkUI (imperative→declarative gap)
- `android.opengl` → OH GPU APIs
- `android.renderscript` → no equivalent
- `android.accounts` → different identity model

## Priority Order
Build shims by impact (most apps use these) × feasibility (score):

1. **android.content** — Context, Intent, SharedPreferences, ContentResolver
2. **android.app** — Activity, Service, Application, NotificationManager, AlarmManager
3. **android.os** — Bundle, Handler, Looper, Build, Environment
4. **android.database** — SQLiteDatabase, Cursor, ContentValues
5. **android.net** — Uri, ConnectivityManager, HttpURLConnection
6. **android.util** — Log, SparseArray, TypedValue
7. **android.widget** — TextView, Button, EditText, ListView (hardest — UI paradigm gap)
8. **android.view** — View, ViewGroup, LayoutInflater (hardest)

## Shim Library Structure

```
shim/
├── java/                          # Layer 1: Java shim classes
│   └── android/
│       ├── app/
│       │   ├── Activity.java
│       │   ├── Service.java
│       │   ├── Application.java
│       │   ├── NotificationManager.java
│       │   ├── NotificationChannel.java
│       │   ├── AlarmManager.java
│       │   └── PendingIntent.java
│       ├── content/
│       │   ├── Context.java
│       │   ├── Intent.java
│       │   ├── SharedPreferences.java
│       │   ├── ContentProvider.java
│       │   ├── ContentResolver.java
│       │   ├── BroadcastReceiver.java
│       │   ├── ClipboardManager.java
│       │   ├── ClipData.java
│       │   └── ClipDescription.java
│       ├── os/
│       │   ├── Bundle.java
│       │   ├── Handler.java
│       │   ├── Looper.java
│       │   └── Build.java
│       ├── database/
│       │   ├── sqlite/
│       │   │   ├── SQLiteDatabase.java
│       │   │   └── SQLiteOpenHelper.java
│       │   ├── Cursor.java
│       │   └── ContentValues.java
│       ├── net/
│       │   ├── Uri.java
│       │   └── ConnectivityManager.java
│       ├── util/
│       │   ├── Log.java
│       │   ├── SparseArray.java
│       │   └── TypedValue.java
│       ├── widget/
│       │   ├── TextView.java
│       │   ├── Button.java
│       │   ├── EditText.java
│       │   ├── Toast.java
│       │   └── ... (complex — deferred)
│       └── view/
│           ├── View.java
│           ├── ViewGroup.java
│           └── LayoutInflater.java
├── bridge/                        # Layer 2: JNI bridge
│   ├── OHBridge.java             # Java side of JNI
│   ├── oh_bridge.cpp             # Native side — calls OH APIs
│   └── type_marshal.cpp          # Java↔OH type conversion
└── tests/                        # Shim unit tests
    └── ...
```

## Available Detail Skills

| Skill File | Scope |
|---|---|
| `SHIM-CONTEXT.md` | Context, Intent, Bundle, SharedPreferences, BroadcastReceiver |
| `SHIM-LIFECYCLE.md` | Activity, Service, Application → UIAbility, ServiceExtensionAbility |
| `SHIM-DATA.md` | SQLiteDatabase, Cursor, ContentValues, ContentProvider |
| `SHIM-NOTIFY.md` | NotificationManager, AlarmManager, PendingIntent |
| `SHIM-NET.md` | HttpURLConnection, ConnectivityManager, Uri |
| `SHIM-UTIL.md` | Log, Bundle, Handler, Looper, Build, SparseArray |
| `SHIM-VIEW.md` | View system shim strategy (hardest tier) |
| `SHIM-BRIDGE.md` | JNI bridge patterns, type marshalling, lifecycle management |
| `A2OH-TEST-PLAN.md` | Test strategy: headless CLI + UI mockup, mock bridge, device validation |

## Current Shim Status

- **1,968 Java shim files** — 100% clean compile (0 errors, 6 warnings)
- **2,422 .class files** generated (javac JDK 21)
- **Coverage**: 1,959 android.* + 8 dalvik.* + 1 com.ohos.* types
- **api_compat.db** tracks 4,617 Android API types total
- **Shim generator pipeline**: `generate_shims.py` → `fix_imports.py` → `fix_imports2.py` → `fix_unknown_types.py` → `fix_compile_errors.py` → `fix_all.py` → `fix_constructors.py` → `fix_final.py` → `fix_last.py`

## Scoring Reference
- DB scores are **1-10 scale**
- `mapping_type`: direct (13%), near (15%), partial (16%), composite (9%), none (46%)
- 34,548 callable APIs (methods + constructors), 22,741 constants (fields + enum_constants)
