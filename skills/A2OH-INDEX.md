# A2OH: Android to OpenHarmony Conversion Skills

## Purpose

You are an AI coding agent that converts Android (Java/Kotlin) source code to
OpenHarmony (ArkTS) source code. This index gives you the scope of the conversion
and tells you which detail skill to load for each file you process.

## Key Differences: Android vs OpenHarmony

| Aspect | Android | OpenHarmony |
|--------|---------|-------------|
| Language | Java / Kotlin | ArkTS (TypeScript superset) |
| UI paradigm | Imperative (View + XML layout) | Declarative (ArkUI `@Component` + `build()`) |
| App model | Activity / Service / Receiver / Provider | UIAbility / ExtensionAbility (Stage model) |
| Navigation | Intent + startActivity | Want + startAbility / Navigation component |
| Data | SQLite + SharedPreferences + ContentProvider | RdbStore + Preferences + DataShare |
| Async | Thread / AsyncTask / Handler | Promise / async-await / TaskPool / Worker |
| Permissions | Runtime + Manifest | Runtime + module.json5 |
| IPC | Intent / Binder / AIDL | Want / IPC / RPC |
| Build | Gradle + AndroidManifest.xml | hvigor + module.json5 + oh-package.json5 |
| Package | APK (DEX + resources) | HAP (ABC bytecode + resources) |
| Native | JNI (Java ↔ C) | NAPI (ArkTS ↔ C) |

## File Classification & Skill Selection

When you receive an Android source file, classify it and load the appropriate skill(s):

### Classification Rules

| If the file... | Load skill |
|----------------|-----------|
| extends `Activity`, `AppCompatActivity`, `FragmentActivity` | `A2OH-LIFECYCLE` + `A2OH-UI-REWRITE` |
| extends `Fragment`, `DialogFragment` | `A2OH-UI-REWRITE` + `A2OH-LIFECYCLE` |
| extends `Service`, `IntentService`, `JobService` | `A2OH-LIFECYCLE` |
| extends `BroadcastReceiver` | `A2OH-LIFECYCLE` (IPC section) |
| extends `ContentProvider` | `A2OH-DATA-LAYER` + `A2OH-LIFECYCLE` |
| extends `RecyclerView.Adapter`, `BaseAdapter`, `ArrayAdapter` | `A2OH-UI-REWRITE` |
| extends `SQLiteOpenHelper` or uses `SQLiteDatabase` | `A2OH-DATA-LAYER` |
| uses `SharedPreferences` | `A2OH-DATA-LAYER` |
| uses `HttpURLConnection`, `OkHttp`, `Retrofit`, `Volley` | `A2OH-NETWORKING` |
| uses `WebSocket`, `Socket` | `A2OH-NETWORKING` |
| uses `MediaPlayer`, `ExoPlayer`, `AudioManager` | `A2OH-MEDIA` |
| uses `Camera`, `Camera2` API | `A2OH-MEDIA` |
| uses `TelephonyManager`, `SmsManager` | `A2OH-DEVICE-API` |
| uses `SensorManager`, `LocationManager` | `A2OH-DEVICE-API` |
| uses `BluetoothAdapter`, `NfcAdapter` | `A2OH-DEVICE-API` |
| uses `NotificationManager` | `A2OH-DEVICE-API` |
| is `AndroidManifest.xml` | `A2OH-CONFIG` |
| is `build.gradle` | `A2OH-CONFIG` |
| is XML layout (`res/layout/*.xml`) | `A2OH-UI-REWRITE` |
| is a model/POJO/utility class with only Java stdlib | `A2OH-JAVA-TO-ARKTS` |
| uses `View` subclasses, custom views | `A2OH-UI-REWRITE` |

**Multiple skills can be loaded for one file.** A typical Activity needs lifecycle + UI + often data or networking.

### Conversion Workflow

```
1. READ the Android file completely
2. CLASSIFY using the table above → select skill(s) to load
3. PLAN the structural changes before writing code:
   - What OH components/modules are needed?
   - What files will be generated? (ArkTS + module.json5 entries)
   - What APIs have no OH equivalent? (flag for human review)
4. CONVERT using the loaded skill patterns
5. OUTPUT the ArkTS file(s) with all necessary imports
6. FLAG any APIs with confidence < medium for human review
```

### Confidence Levels

When converting each API call, assess your confidence:

| Confidence | Meaning | Action |
|------------|---------|--------|
| **High** | Direct equivalent exists, well-documented | Convert and move on |
| **Medium** | Similar API exists, minor adaptation needed | Convert with a `// A2OH: verify` comment |
| **Low** | No clear equivalent, paradigm shift, or uncertain | Add `// A2OH-TODO: manual review needed — [reason]` |

### Output Structure

A converted Android app produces:

```
entry/
├── src/main/
│   ├── ets/
│   │   ├── entryability/
│   │   │   └── EntryAbility.ets          ← from MainActivity.java
│   │   ├── pages/
│   │   │   ├── Index.ets                 ← from activity_main.xml + Activity UI logic
│   │   │   └── DetailPage.ets            ← from activity_detail.xml + DetailActivity
│   │   ├── components/
│   │   │   └── ListItemComponent.ets     ← from list_item.xml + Adapter
│   │   ├── model/
│   │   │   └── DataModel.ets             ← from Java model classes
│   │   ├── utils/
│   │   │   └── DbHelper.ets              ← from SQLiteOpenHelper subclass
│   │   └── common/
│   │       └── Constants.ets
│   ├── resources/                        ← from res/
│   └── module.json5                      ← from AndroidManifest.xml
└── oh-package.json5                      ← from build.gradle dependencies
```

### Available Detail Skills

| Skill File | Domain | When to Load |
|-----------|--------|-------------|
| `A2OH-LIFECYCLE` | Activity, Service, Receiver, Provider, Intent, Want | App component files |
| `A2OH-UI-REWRITE` | View→ArkUI, XML layouts, adapters, custom views | Any UI code |
| `A2OH-DATA-LAYER` | SQLite→RdbStore, SharedPreferences→Preferences, ContentProvider→DataShare | Data access code |
| `A2OH-NETWORKING` | HTTP, WebSocket, WiFi, Bluetooth connectivity | Network code |
| `A2OH-MEDIA` | MediaPlayer, Camera, Audio, Image processing | Media code |
| `A2OH-DEVICE-API` | Telephony, Sensors, Location, NFC, Notifications, Permissions | Hardware/system APIs |
| `A2OH-JAVA-TO-ARKTS` | Java syntax→ArkTS, stdlib, collections, I/O, threading | All files (reference) |
| `A2OH-CONFIG` | AndroidManifest→module.json5, Gradle→hvigor, resources | Config files |

### Scope: Android 11 (API 30) → OpenHarmony 4.1 (API 11)

- Android API surface: 34,548 callable APIs (methods + constructors) across 217 packages
- OpenHarmony API surface: 3,302 callable APIs (methods + functions + properties) across 690+ modules
- OH also has 5,098 C/NDK functions for native development
- Coverage varies by domain: Hardware/USB ~97%, Media ~92%, View/Widget ~65-70%
