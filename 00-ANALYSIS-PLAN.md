# Android-to-OpenHarmony API Migration: Comprehensive Analysis Plan

**Goal:** Evaluate feasibility of a shim/translation layer that transparently maps Android APIs to OpenHarmony APIs, enabling Android APKs to run on OpenHarmony without modifying app source code.

**Date:** 2026-03-10

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Android APK (.apk)                       │
│  ┌─────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐   │
│  │ DEX code│ │ Resources│ │ NDK .so  │ │AndroidManifest│   │
│  └────┬────┘ └─────┬────┘ └─────┬────┘ └───────┬───────┘   │
└───────┼────────────┼────────────┼───────────────┼───────────┘
        │            │            │               │
   ┌────▼────────────▼────────────▼───────────────▼───────────┐
   │              SHIM / TRANSLATION LAYER                     │
   │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐  │
   │  │ Java API │ │ Resource │ │ NDK/JNI  │ │  Manifest  │  │
   │  │  Shim    │ │ Adapter  │ │  Bridge  │ │  Mapper    │  │
   │  └────┬─────┘ └────┬─────┘ └────┬─────┘ └─────┬──────┘  │
   └───────┼────────────┼────────────┼──────────────┼─────────┘
           │            │            │              │
   ┌───────▼────────────▼────────────▼──────────────▼─────────┐
   │                 OpenHarmony OS                             │
   │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐  │
   │  │  ArkTS   │ │ Resource │ │   NDK    │ │   Ability  │  │
   │  │  APIs    │ │  System  │ │   APIs   │ │  Framework │  │
   │  └──────────┘ └──────────┘ └──────────┘ └────────────┘  │
   └──────────────────────────────────────────────────────────┘
```

---

## Phase 1: API Domain Mapping (Similarity Analysis)

**Purpose:** Map every Android API domain to its OpenHarmony equivalent, classify coverage level.

### 1.1 — Domain-Level API Correspondence Matrix

Create a comprehensive matrix mapping Android API packages → OpenHarmony equivalents:

| Analysis Report | Android Domain | OH Domain | Priority |
|----------------|---------------|-----------|----------|
| `01-domain-mapping-app-lifecycle.md` | `android.app` (Activity, Service, BroadcastReceiver, ContentProvider) | `@ohos.app.ability` (UIAbility, ServiceExtension, DataShareExtension) | P0 |
| `02-domain-mapping-ui.md` | `android.view`, `android.widget` (View, ViewGroup, 181 widgets) | ArkUI declarative components (127 @internal components) | P0 |
| `03-domain-mapping-content.md` | `android.content` (Context, Intent, ContentResolver, SharedPreferences) | `@ohos.app.ability.Want`, `@ohos.data.preferences`, `@ohos.data.dataSharePredicates` | P0 |
| `04-domain-mapping-os-ipc.md` | `android.os` (Handler, Binder, Bundle, Parcelable) | `@ohos.rpc`, `@ohos.worker`, `@ohos.taskpool` | P0 |
| `05-domain-mapping-net.md` | `android.net`, `android.net.wifi` (ConnectivityManager, WiFi) | `@ohos.net.connection`, `@ohos.net.http`, `@ohos.wifi` | P1 |
| `06-domain-mapping-media.md` | `android.media` (MediaPlayer, AudioManager, Camera2) | `@ohos.multimedia.media`, `@ohos.multimedia.audio`, `@ohos.multimedia.camera` | P1 |
| `07-domain-mapping-telephony.md` | `android.telephony` (TelephonyManager, SmsManager) | `@ohos.telephony.call`, `@ohos.telephony.sms`, `@ohos.telephony.sim` | P1 |
| `08-domain-mapping-security.md` | `android.security`, `android.security.keystore` | `@ohos.security.huks`, `@ohos.userIAM.userAuth` | P1 |
| `09-domain-mapping-location.md` | `android.location` (LocationManager) | `@ohos.geoLocationManager` | P2 |
| `10-domain-mapping-bluetooth.md` | `android.bluetooth` (BluetoothAdapter, BLE) | `@ohos.bluetooth.ble`, `@ohos.bluetooth.connection` | P2 |
| `11-domain-mapping-nfc.md` | `android.nfc` | `@ohos.nfc.tag`, `@ohos.nfc.cardEmulation` | P2 |
| `12-domain-mapping-sensors.md` | `android.hardware` (SensorManager, Camera) | `@ohos.sensor`, `@ohos.vibrator` | P2 |
| `13-domain-mapping-storage.md` | `android.database.sqlite`, `android.provider` | `@ohos.data.relationalStore`, `@ohos.file.fs` | P1 |
| `14-domain-mapping-notifications.md` | `android.app.Notification*` | `@ohos.notificationManager` | P1 |
| `15-domain-mapping-permissions.md` | `android.permission`, `Manifest.permission` | `@ohos.abilityAccessCtrl`, `@ohos.bundle.bundleManager` | P0 |
| `16-domain-mapping-graphics.md` | `android.graphics` (Canvas, Paint, Bitmap, OpenGL) | OH NDK: `drawing_canvas.h`, `drawing_pen.h`, OpenGL ES headers | P1 |
| `17-domain-mapping-ndk-native.md` | Android NDK (libc, libm, liblog, libandroid) | OH NDK: musl libc, HiLog, NAPI, XComponent | P1 |
| `18-domain-mapping-text.md` | `android.text` (Spannable, TextUtils, Html) | ArkUI Text component, `@ohos.util` | P2 |
| `19-domain-mapping-animation.md` | `android.animation` (Animator, ValueAnimator) | ArkUI animation APIs (`animateTo`, `@AnimatableExtend`) | P1 |
| `20-domain-mapping-webkit.md` | `android.webkit` (WebView, WebSettings) | `@ohos.web.webview`, ArkWeb NDK | P1 |

### 1.2 — Per-API Method-Level Similarity Scoring

For each domain mapping, produce a method-level comparison with scores:

```
Score System:
  1.0 = Direct equivalent (same semantics, can map 1:1)
  0.8 = Near equivalent (minor parameter/behavior differences, shimmable)
  0.5 = Partial equivalent (similar concept, significant adaptation needed)
  0.3 = Indirect equivalent (achievable via composition of multiple OH APIs)
  0.0 = No equivalent (API gap — requires emulation or is impossible)
```

Output format per API:
```markdown
| Android API | Score | OH Equivalent | Gap Notes |
|-------------|------:|---------------|-----------|
| Activity.onCreate(Bundle) | 0.8 | UIAbility.onCreate(Want,LaunchParam) | Bundle→Want mapping needed |
| Activity.startActivity(Intent) | 0.5 | UIAbilityContext.startAbility(Want) | Intent→Want + implicit intent resolution missing |
| View.setOnClickListener() | 0.8 | onClick() declarative handler | Imperative→declarative paradigm shift |
| SQLiteDatabase.query() | 0.8 | RdbStore.query(RdbPredicates) | SQL→RdbPredicates translation |
| BluetoothAdapter.startDiscovery() | 1.0 | connection.startBluetoothDiscovery() | Direct mapping |
| ContentResolver.query(Uri) | 0.3 | dataShare.query() | Different URI scheme, different provider model |
```

---

## Phase 2: Platform Gap Analysis

**Purpose:** Identify APIs with NO OpenHarmony equivalent — the hard problems.

### 2.1 — Structural/Paradigm Gaps

| Gap ID | Android Concept | OH Status | Impact | Mitigation Strategy |
|--------|----------------|-----------|--------|---------------------|
| GAP-01 | Implicit Intents | No equivalent | HIGH | Build intent resolution registry in shim layer |
| GAP-02 | ContentProvider (cross-app data) | DataShareExtension (different model) | HIGH | Implement ContentProvider→DataShare adapter |
| GAP-03 | Fragment lifecycle | No equivalent (ArkUI is declarative) | HIGH | Must rewrite UI — cannot shim |
| GAP-04 | BroadcastReceiver (system broadcasts) | CommonEvent (partial) | MEDIUM | Map ACTION_* → CommonEvent equivalents |
| GAP-05 | Imperative View system | ArkUI declarative only | CRITICAL | **Cannot shim** — UI must be recompiled for ArkUI |
| GAP-06 | Service (background) | ServiceExtensionAbility (limited) | HIGH | Map Android Service → OH ServiceExtension |
| GAP-07 | AccountManager | No equivalent | MEDIUM | Implement in shim layer with local storage |
| GAP-08 | Binder IPC (custom) | OH IPC/RPC (different AIDL) | HIGH | Map AIDL→IDL or use RPC shim |
| GAP-09 | AppWidget | FormExtensionAbility (different) | MEDIUM | Widget model translation |
| GAP-10 | DRM framework | No equivalent | LOW | Stub or third-party |

### 2.2 — Report: `21-gap-analysis-critical.md`
Detailed analysis of each gap with:
- Root cause (architectural difference vs missing feature)
- Feasibility of shimming (possible / hard / impossible)
- Estimated effort
- Alternative approaches

### 2.3 — Report: `22-gap-analysis-ndk.md`
NDK/native layer gaps:
- Android NDK APIs with no OH NDK equivalent
- Bionic vs musl libc differences
- JNI vs NAPI bridge differences
- Native library loading differences

---

## Phase 3: Shim Layer Architecture Design

**Purpose:** Design the actual translation layer architecture.

### 3.1 — Runtime Architecture Analysis: `23-runtime-bridge.md`

```
Key Question: How does DEX bytecode run on ArkCompiler?

Options:
A) ART-on-OH: Port ART runtime to run on OpenHarmony (heavy)
B) DEX→ABC transpiler: Convert DEX bytecode to Ark bytecode (.abc)
C) Java→ArkTS source transpiler: Convert Java/Kotlin source to ArkTS
D) Hybrid: Native shim + recompiled UI layer
```

Analysis for each option:
- Technical feasibility
- Performance implications
- Maintenance burden
- Android version compatibility

### 3.2 — API Shim Layers: `24-shim-layer-design.md`

For each mappable domain, design the shim:

```
Layer 1: android.jar stub (compile-time)
  - Provide android.* class stubs so APK compiles
  - Forward calls to Layer 2

Layer 2: Runtime bridge (Java/ArkTS interop)
  - Android API → OH API translation
  - State management (Activity lifecycle → UIAbility lifecycle)
  - Event model translation

Layer 3: Native bridge
  - Android NDK → OH NDK function mapping
  - JNI → NAPI bridge
  - Shared library loading shim (dlopen mapping)
```

### 3.3 — FFI Bridge Design: `25-ffi-bridge-design.md`

```
Android JNI                    OpenHarmony NAPI
┌──────────────┐              ┌──────────────┐
│ JNIEnv*      │    bridge    │ napi_env     │
│ FindClass    │ ──────────── │ napi_get_*   │
│ GetMethodID  │              │ napi_call_*  │
│ CallVoidMethod│             │ napi_create_*│
│ NewStringUTF │              │              │
└──────────────┘              └──────────────┘

Key challenges:
- JNI uses Java class/method reflection → NAPI uses module exports
- JNI has direct memory access → NAPI is value-based
- Thread model differences (JNI AttachCurrentThread vs NAPI thread-safe functions)
```

---

## Phase 4: UI Translation Strategy

**Purpose:** Solve the hardest problem — Android's imperative View system vs ArkUI's declarative model.

### 4.1 — `26-ui-translation-strategy.md`

```
CRITICAL FINDING: Direct View→ArkUI shimming is NOT feasible.

Android: Imperative (new Button(), addView(), setOnClickListener())
ArkUI:   Declarative (struct + build() + @State)

Viable approaches:
A) Source-level transpiler: Convert Android XML layouts + Java view code → ArkUI components
B) Canvas-based renderer: Render Android Views onto an ArkUI Canvas/XComponent
C) WebView bridge: Run Android UI in a WebView-based container
D) Custom render engine: Port Android's View rendering to OH's graphics layer
```

### 4.2 — `27-view-component-mapping.md`

Map each Android widget to its ArkUI equivalent:

| Android Widget | ArkUI Component | Mapping Quality | Notes |
|---------------|----------------|:---------------:|-------|
| TextView | Text | 0.9 | Rich text differences |
| EditText | TextInput/TextArea | 0.8 | IME handling differs |
| Button | Button | 0.9 | Style mapping needed |
| ImageView | Image | 0.9 | Drawable→Resource |
| RecyclerView | List/LazyForEach | 0.7 | Adapter→LazyForEach |
| LinearLayout | Row/Column | 0.9 | Direct mapping |
| RelativeLayout | RelativeContainer | 0.8 | Anchor rules differ |
| FrameLayout | Stack | 0.9 | Direct mapping |
| ScrollView | Scroll | 0.9 | Direct mapping |
| WebView | Web | 0.8 | Different JS bridge |
| ViewPager | Swiper/Tabs | 0.8 | Adapter pattern differs |
| ConstraintLayout | RelativeContainer | 0.6 | Constraint system differs |
| DrawerLayout | SideBarContainer | 0.7 | Gesture handling differs |
| ToolBar/ActionBar | Navigation/Toolbar | 0.7 | Different API surface |
| AlertDialog | AlertDialog/CustomDialog | 0.8 | Builder pattern → struct |
| Toast | promptAction.showToast | 0.9 | Direct mapping |
| ProgressBar | Progress | 0.9 | Direct mapping |
| SeekBar | Slider | 0.9 | Direct mapping |
| Switch | Toggle | 0.9 | Direct mapping |
| CheckBox | Checkbox | 0.9 | Direct mapping |
| RadioButton | Radio | 0.9 | Direct mapping |
| Spinner | Select | 0.8 | Adapter → options array |
| ListView | List | 0.7 | Adapter pattern → ForEach |

---

## Phase 5: Resource & Manifest Translation

### 5.1 — `28-resource-translation.md`

| Android Resource | OH Equivalent | Translation |
|-----------------|---------------|-------------|
| `res/layout/*.xml` | ArkUI `build()` method | XML→declarative transpilation |
| `res/values/strings.xml` | `resources/base/element/string.json` | Key-value format conversion |
| `res/drawable/*` | `resources/base/media/*` | File copy + reference update |
| `res/mipmap/*` | `resources/base/media/*` | Icon mapping |
| `res/values/colors.xml` | `resources/base/element/color.json` | Format conversion |
| `res/values/dimens.xml` | `resources/base/element/float.json` | Unit conversion (dp→vp) |
| `res/values/styles.xml` | `@Styles` decorator / Theme | Manual mapping |
| `res/anim/*.xml` | ArkUI animation APIs | Must rewrite |
| `res/menu/*.xml` | No direct equivalent | Must rewrite |
| `AndroidManifest.xml` | `module.json5` | Structural translation |

### 5.2 — `29-manifest-translation.md`

```
AndroidManifest.xml          →    module.json5
─────────────────                 ────────────
<application>                     "module": {
  <activity>                        "abilities": [{
    intent-filter                     "skills": [{
      action MAIN                       "actions": ["action.system.home"]
      category LAUNCHER                 "entities": ["entity.system.home"]
  <service>                         }]
  <receiver>                      "extensionAbilities": [{
  <provider>                        "type": "dataShare"
<uses-permission>                 "requestPermissions": [{
<uses-feature>                    "deviceTypes": [...]
```

---

## Phase 6: Feasibility Scoring & Recommendation

### 6.1 — `30-feasibility-matrix.md`

Per-domain feasibility score:

| Domain | API Count | Mappable | Gaps | Shim Feasibility | Effort |
|--------|----------:|:--------:|:----:|:----------------:|--------|
| App Lifecycle | 1,116 methods | 60% | 40% | MEDIUM | High |
| UI/Widgets | 4,063 methods | 10% | 90% | **NOT FEASIBLE** (must rewrite) | Very High |
| Content/Data | 1,373 methods | 50% | 50% | MEDIUM | High |
| OS/IPC | 803 methods | 40% | 60% | HARD | Very High |
| Networking | 853 methods | 70% | 30% | HIGH | Medium |
| Media | 1,713 methods | 60% | 40% | MEDIUM | High |
| Telephony | 1,025 methods | 50% | 50% | MEDIUM | High |
| Security | 348+ methods | 40% | 60% | HARD | High |
| Graphics NDK | 2,744 methods | 80% | 20% | HIGH (OpenGL shared) | Medium |
| Storage/DB | ~200 methods | 70% | 30% | HIGH | Medium |

### 6.2 — `31-recommended-approach.md`

Final recommendation with:
- Recommended architecture (likely Option D: Hybrid)
- Phase 1 scope (which APIs to shim first)
- Estimated LOC for shim layer
- Risk assessment
- Comparison with existing solutions (e.g., HarmonyOS's Android compatibility layer)

---

## Execution Plan

### Analysis Reports to Generate (31 total):

```
Phase 1: Domain Mapping (20 reports)           → 20 parallel agents
Phase 2: Gap Analysis (2 reports)               → 2 parallel agents
Phase 3: Shim Architecture (3 reports)          → 3 parallel agents
Phase 4: UI Translation (2 reports)             → 2 parallel agents
Phase 5: Resource/Manifest (2 reports)          → 2 parallel agents
Phase 6: Feasibility & Recommendation (2 reports) → 2 parallel agents (after all above)
```

### Input Data:
- Android 11 API enumeration: `~/aosp-android-11/CODE_REVIEW_REPORTS/` (524KB + 4.1MB API enum)
- OpenHarmony API enumeration: `~/openharmony-review/` (2.9MB reviews + API enum)
- Android 11 source: `~/aosp-android-11/` (178GB)
- OpenHarmony source: `~/openharmony/`

### Output:
All reports written to: `~/android-to-openharmony-migration/`
