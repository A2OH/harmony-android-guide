# Westlake Engine — Status Report

**Date:** 2026-03-25
**Status:** Jetpack Compose + multi-app platform on Huawei Mate 20 Pro

---

## Executive Summary

The Westlake engine now runs **Jetpack Compose** natively on a real phone.
The host Activity (`WestlakeActivity`) extends `ComponentActivity` with full
AndroidX lifecycle support, built with Gradle + AGP 8.2 + Kotlin 1.9.

A Compose Material 3 app gallery serves as the home screen. Custom View-based
apps (MockDonalds, Dialer, Social Feed, Huawei Calculator) launch from the
gallery and run with full touch navigation. Real installed APKs launch via
`startActivity()`.

**Apps running on the engine:**

1. **App Gallery** — Jetpack Compose Material 3 (LazyColumn, Cards, dark theme)
2. **MockDonalds** — Full ordering app (5 screens, View-based)
3. **Dialer** — Phone dialer (7 screens, View-based)
4. **Social Feed** — Social media feed (View-based)
5. **Huawei Calculator** — Real APK DEX + XML layout
6. **Real installed APKs** — Calculator, Clock, Settings, Calendar via startActivity

The engine has been validated on three platforms:

| Platform | Runtime | FPS | Status |
|----------|---------|-----|--------|
| x86_64 host (Linux) | ART (AOT) | 60 | Stable, primary dev target |
| ARM64 on phone | dalvikvm (interpreter) | 120 | Runs natively on Mate 20 Pro |
| Huawei Mate 20 Pro | Android 10 native ART | native | Compose + View apps, 6 apps |

---

## Milestone: Jetpack Compose (2026-03-25)

Jetpack Compose renders natively on the phone via a Gradle-built host APK.

### Architecture

```
WestlakeActivity (extends ComponentActivity)
├── onCreate: setContent { ComposeGallery() }   ← Compose renders immediately
├── Engine thread: loads app.dex + aosp-shim.dex via DexClassLoader
│   └── MockDonaldsApp.main() initializes MiniServer
└── User taps app card → launchCustomApp(className, initMethod, showMethod)
    └── Reflection call → MockApp.init(ctx) + MockApp.showMenu()
        └── setContentView(viewRoot) replaces Compose with View-based UI
            └── Back → showHome() restores Compose gallery
```

### Build System

```
westlake-host-gradle/
├── build.gradle.kts          # AGP 8.2, Kotlin 1.9.10
├── app/build.gradle.kts      # Compose BOM 2023.10, Material 3
├── app/src/main/
│   ├── java/.../WestlakeActivity.kt  # ComponentActivity + Compose
│   ├── assets/
│   │   ├── aosp-shim.dex    # 2,168 shim classes
│   │   ├── app.dex           # Custom apps (MockDonalds, Dialer, etc.)
│   │   └── material_icons.ttf
│   └── jniLibs/arm64-v8a/
│       ├── liboh_bridge.so
│       └── libohbridge_android.so
└── Output: app-debug.apk (11MB)
```

### Why Gradle (not manual build)

Previous approach (manual javac + dx + aapt + jarsigner) failed for Compose:
- Compose requires Kotlin compiler plugin (not available in manual builds)
- AndroidX lifecycle/savedstate/viewmodel have complex dependency chains
- R$id resource IDs must match between classloaders
- LifecycleRegistry requires main thread + correct initialization order

Gradle solves all of this — Compose is a normal dependency, lifecycle works
natively, no reflection hacks needed.

### Platform Coupling (Updated)

**What depends on the Android phone:**

| Component | Coupling | Notes |
|-----------|----------|-------|
| `ComponentActivity` | AndroidX library | On OHOS: replace with shim |
| `setContent {}` | Compose runtime | On OHOS: Compose renders to Canvas → bridge |
| `Canvas/Paint/Path` | Android framework | On OHOS: bridge to ArkUI/Skia (~25 functions) |
| `View` classes | Android framework | On OHOS: our shim View classes |
| `DexClassLoader` | Android runtime | On OHOS: ART runtime (already works) |
| `Looper/Handler` | Android framework | On OHOS: our shim Looper |

**What is platform-independent:**

| Component | Description |
|-----------|-------------|
| All custom app code | MockDonalds, Dialer, Social Feed |
| MiniServer + MiniActivityManager | Activity lifecycle management |
| BinaryXmlParser + XmlTestHelper | XML layout inflation |
| ShimCompat | Reflection-based framework compat |
| OHBridge Java side | 170 JNI method declarations |
| Compose gallery UI | MaterialTheme, LazyColumn, Cards |

### OHOS Port Path

To run on OpenHarmony:
1. Replace `ComponentActivity` → custom Activity with shim lifecycle
2. Replace Compose's Canvas backend → ArkUI/Skia rendering
3. Keep all app code + shim layer unchanged
4. Bridge ~25 Canvas/Paint/Path functions to OHOS native APIs

---

## Architecture

### Full Pipeline (text diagram)

```
+------------------------------------------------------------------+
|                    Android Phone (Mate 20 Pro)                    |
|                                                                   |
|  +-----------------------------+   +---------------------------+  |
|  |     WestlakeActivity.java   |   |    liboh_bridge.so        |  |
|  |  (extends android.app.Act.) |   |  (~25 Canvas/Paint/Path   |  |
|  |  - child-first DexClassLdr  |   |   JNI methods bridged)    |  |
|  |  - loads app.dex at runtime |   +---------------------------+  |
|  +-------------|---------------+               |                  |
|                |                               |                  |
|  +-------------|-------------------------------|---------+        |
|  |             v            App DEX Space                |        |
|  |                                                       |        |
|  |  +----------------+   +----------------------------+  |        |
|  |  | MockDonaldsApp |   | MiniServer                 |  |        |
|  |  | (entry point)  |-->| - MiniActivityManager      |  |        |
|  |  +----------------+   | - MiniWindowManager        |  |        |
|  |                       | - Activity lifecycle        |  |        |
|  |                       +------|---------------------+  |        |
|  |                              |                        |        |
|  |                              v                        |        |
|  |  +---------------------------------------------------+|       |
|  |  |           Activity Stack                           ||       |
|  |  |  MenuActivity -> ItemDetailActivity -> CartActivity||       |
|  |  |  (real LinearLayout, ListView, Button, TextView)   ||       |
|  |  +---------------------------------------------------+|       |
|  |                              |                        |        |
|  |                              v                        |        |
|  |  +---------------------------------------------------+|       |
|  |  |           View Tree (native Android Views)         ||       |
|  |  |  LinearLayout                                      ||       |
|  |  |  +-- TextView ("MockDonalds Menu")                 ||       |
|  |  |  +-- ListView (8 menu items via BaseAdapter)       ||       |
|  |  |  +-- Button ("View Cart (2)")                      ||       |
|  |  +---------------------------------------------------+|       |
|  +-------------------------------------------------------+        |
|                              |                                    |
|                              v                                    |
|  +-----------------------------------------------------------+   |
|  |  ShimCompat (reflection-based framework compatibility)     |   |
|  |  - Bridges framework version differences via reflection    |   |
|  |  - Handles Context, Resources, PackageManager stubs        |   |
|  +-----------------------------------------------------------+   |
|                              |                                    |
|  +-----------------------------------------------------------+   |
|  |  OHBridge JNI Layer (170 registered methods)               |   |
|  |  - Canvas: drawText, drawRect, drawLine, drawBitmap, ...   |   |
|  |  - Paint: setColor, setTextSize, measureText, ...          |   |
|  |  - Path: moveTo, lineTo, quadTo, close, ...               |   |
|  |  - Surface: createSurface, lockCanvas, unlockAndPost       |   |
|  +-----------------------------------------------------------+   |
|                              |                                    |
|  +-----------------------------------------------------------+   |
|  |  Android Framework (native ART on phone)                   |   |
|  |  Canvas, Paint, FontMetrics, Path (real implementations)   |   |
|  +-----------------------------------------------------------+   |
+------------------------------------------------------------------+
```

### ClassLoader Hierarchy

```
BootClassLoader (Android framework)
  |
  +-- PathClassLoader (WestlakeActivity — host app)
        |
        +-- Child-First DexClassLoader (app.dex)
              |
              +-- MockDonalds classes
              +-- MiniServer, MiniActivityManager
              +-- ShimCompat, OHBridge
              +-- Shim classes (android.widget.*, android.view.*)
              |
              (Parent delegation: only for java.*, android.app.Activity)
```

The child-first classloader is critical: it ensures that our shim `android.widget.ListView`
(which works with `MiniActivityManager`) is loaded instead of the framework's real one,
while still delegating `android.app.Activity` to the real framework so
`WestlakeActivity` can extend it.

---

## What's Tested

### Components

| Component | Description | Status |
|-----------|-------------|--------|
| MiniServer | Lightweight app server replacing Android's `ActivityManagerService` | Tested |
| MiniActivityManager | Activity lifecycle: create, start, resume, pause, stop, destroy, restart | Tested |
| MiniWindowManager | View tree management, measure/layout/draw cycle | Tested |
| OHBridge JNI | 170 registered JNI methods for Canvas/Paint/Path/Surface/Prefs/RDB | Tested |
| Child-first DexClassLoader | Loads app.dex, shim classes override framework classes | Tested |
| ShimCompat | Reflection-based compatibility for framework version differences | Tested |
| Native Views | LinearLayout, ListView, Button, TextView, FrameLayout, RelativeLayout | Tested |
| Touch input | DOWN/UP events routed to View tree, click handlers fire | Tested |
| Activity navigation | Menu -> ItemDetail -> Cart -> Checkout, with back stack | Tested |
| Cart persistence | Cart counter persists across Activity navigation | Tested |
| Intent + extras | String, int, double, boolean extras passed between Activities | Tested |
| BaseAdapter + ListView | Dynamic list population, view recycling | Tested |
| Multi-app hosting | Multiple apps in single DEX, app switching via buttons | Tested |
| GradientDrawable | Rounded rects, circles, strokes for Material Design UI | Tested |
| Alphabetical sections | Section headers in ListView (contacts A-Z) | Tested |
| Call timer | Live-updating timer on in-call screen via background thread | Tested |

### Apps Running on Engine

| App | Screens | Features |
|-----|---------|----------|
| MockDonalds | 5 | Menu with categories, item detail, cart, checkout, order confirmation |
| Dialer | 7 | T9 keypad, call history (incoming/outgoing/missed), contacts with avatars, contact detail, add contact, in-call with timer, in-call DTMF keypad, voicemail |
| Calculator | 1 | XML-inflated layout from AXML, functional arithmetic |

### End-to-End Flow (Phone)

1. WestlakeActivity launches, creates child-first DexClassLoader for `app.dex`
2. MockDonaldsApp.main() runs, initializes MiniServer
3. MiniActivityManager starts MenuActivity (8 menu items in ListView)
4. User taps menu item -> ItemDetailActivity shows item details
5. User taps "Add to Cart" -> CartActivity shows cart contents
6. User taps "Back" -> returns to MenuActivity, cart counter updated
7. Full touch navigation cycle verified on physical device

---

## Platform Coupling

The Westlake engine is designed for minimal platform coupling. Only two components
are platform-specific:

### Platform-Specific (changes per target)

| Component | Size | Description |
|-----------|------|-------------|
| `liboh_bridge.so` | ~25 methods | JNI bridge to platform Canvas/Paint/Path |
| `WestlakeActivity.java` | ~150 lines | Host Activity with child-first classloader |

### liboh_bridge.so API Surface

The native bridge depends on exactly 4 Android classes with ~25 methods total:

```
android.graphics.Canvas
  - drawText(String, float, float, Paint)
  - drawRect(float, float, float, float, Paint)
  - drawLine(float, float, float, float, Paint)
  - drawBitmap(Bitmap, float, float, Paint)
  - drawCircle(float, float, float, Paint)
  - drawRoundRect(RectF, float, float, Paint)
  - clipRect(float, float, float, float)
  - save(), restore(), translate(float, float)
  - getWidth(), getHeight()

android.graphics.Paint
  - setColor(int), setTextSize(float), setStyle(Style)
  - setAntiAlias(boolean), setStrokeWidth(float)
  - measureText(String)
  - getFontMetrics(FontMetrics)

android.graphics.Paint.FontMetrics
  - ascent, descent, top, bottom (float fields)

android.graphics.Path
  - moveTo(float, float), lineTo(float, float)
  - quadTo(float, float, float, float)
  - close(), reset()
```

This is the **only** native code that needs porting per platform. On OHOS, these
25 methods would call ArkUI/Skia equivalents. On any other platform with a 2D
canvas API (e.g., SDL, Cairo, HTML5 Canvas), the same 25 methods are all that
need reimplementation.

### Platform-Independent (stays the same everywhere)

| Component | Description |
|-----------|-------------|
| MiniServer | App server, Activity lifecycle management |
| MiniActivityManager | Activity stack, lifecycle state machine |
| MiniWindowManager | View tree measure/layout/draw |
| All View classes | LinearLayout, ListView, Button, TextView, etc. |
| ShimCompat | Reflection-based compatibility layer |
| OHBridge Java side | 170 JNI method declarations (auto-generated) |
| MockDonalds app | All application code |
| Intent, Bundle, extras | Inter-Activity communication |
| BaseAdapter, ArrayAdapter | List data binding |
| Canvas/Paint/Path Java API | Drawing API (Java side unchanged) |

In total, **~150 lines of Java + ~25 C functions** are platform-specific.
Everything else (thousands of lines of Java) is platform-independent and runs
unchanged on any target.

---

## Test Results

### x86_64 Host (ART, AOT-compiled boot image)

```
[MockDonaldsApp] Starting on OHOS + ART ...
[OHBridge x86] 169 methods
[MockDonaldsApp] OHBridge native: LOADED
[MockDonaldsApp] arkuiInit() = 0
[MockDonaldsApp] MiniServer initialized
[D] MiniActivityManager: startActivity: com.example.mockdonalds.MenuActivity
[D] MiniActivityManager:   performCreate: com.example.mockdonalds.MenuActivity
[D] MiniActivityManager:   performStart: com.example.mockdonalds.MenuActivity
[D] MiniActivityManager:   performResume: com.example.mockdonalds.MenuActivity
[MockDonaldsApp] MenuActivity launched: com.example.mockdonalds.MenuActivity
[MockDonaldsApp] Creating surface 480x800
[MockDonaldsApp] Initial frame rendered
[MockDonaldsApp] Entering event loop...
[MockDonaldsApp] Frame 600 activity=MenuActivity
```

- **Performance:** ~60 fps sustained
- **Startup:** <1 second (AOT boot image)
- **All 14 MockDonalds tests pass**

### ARM64 dalvikvm on Phone

```
[MockDonaldsApp] Starting via dalvikvm ...
[OHBridge arm64] 170 methods registered
[D] MiniActivityManager: startActivity: MenuActivity
[D] MiniActivityManager:   performCreate, performStart, performResume
[MockDonaldsApp] MenuActivity launched
[MockDonaldsApp] Frame rendered at 120fps
```

- **Performance:** ~120 fps (interpreter, lightweight draw)
- **Runtime:** dalvikvm (KitKat portable interpreter, 64-bit patched)

### Huawei Mate 20 Pro (Native Android 10)

- **Device:** Huawei Mate 20 Pro (LYA-L29)
- **OS:** Android 10 (EMUI)
- **Runtime:** Native ART
- **Views:** Real Android LinearLayout, ListView, Button, TextView
- **Touch:** Full touch navigation working
- **Navigation flow:**
  1. Menu screen (8 items in ListView) -- tap item
  2. Item detail screen (name, price, description) -- tap "Add to Cart"
  3. Cart screen (items list, total price) -- tap "Back"
  4. Menu screen (cart counter updated in button text)
- **Cart counter:** Persists across all navigation transitions

---

## Milestone: XML Layout Inflation (2026-03-24)

Binary XML (AXML) layout inflation from real APKs is now working on the phone.
The engine can parse compiled Android XML layouts and create real Android Views
from the parsed events, bypassing the phone's `LayoutInflater` (which expects
internal `XmlBlock.Parser`).

### Components

| Component | Description |
|-----------|-------------|
| `BinaryXmlParser` | Parses compiled AXML format (string pool, resource IDs, XML tree) |
| `XmlTestHelper.inflateFromParser()` | Walks XML events, creates Views via `createViewForTag()` |
| `XmlTestHelper.applyAttributes()` | Handles layout_width/height/weight, text, textSize, textColor, orientation, padding, background, gravity |
| `XmlTestHelper.loadCalculatorApp()` | Loads separate DEX + inflates XML layout + wires button handlers |

### Tested APK Layouts

| APK | Result |
|-----|--------|
| Custom Calculator (our DEX + XML) | Fully functional: layout inflated, buttons wired, arithmetic works |
| Huawei Contacts Dialer (dialpad_huawei.xml) | Root LinearLayout inflated, custom Huawei View classes skipped |

### Dimension Handling

AXML `decodeDimension()` returns dp values labeled as "px". The inflater
multiplies by display density (e.g., 3.0x on Mate 20 Pro) for correct sizing.

---

## Known Issues

| Issue | Severity | Description | Workaround |
|-------|----------|-------------|------------|
| No SQLite | Medium | SQLiteDatabase not available on phone path | In-memory data structures, no persistence across app restarts |
| No SharedPreferences | Medium | SharedPreferences not implemented for phone path | Cart state held in memory, lost on app kill |
| Custom View classes in APKs | Medium | Third-party APKs use custom View subclasses not in our shim | Must load APK's classes.dex alongside layout XML |
| No resources.arsc on phone | Low | Resource string lookup not wired | Hardcoded strings in Java code |
| No Bitmap loading | Low | BitmapFactory.decodeResource() not implemented | Text-only UI, no images |
| No animation | Low | View animation framework not implemented | Static transitions between Activities |

### Path Forward

1. **OHOS port** — Reimplement `liboh_bridge.so` (~25 functions) against ArkUI/Skia on OpenHarmony
2. **Full APK loading** — Load APK's classes.dex + XML layouts + resources.arsc together
3. **SQLite** — Port `android.database.sqlite` via JNI to native SQLite library
4. **SharedPreferences** — Implement file-backed XML storage

---

## File Locations

| What | Path |
|------|------|
| WestlakeActivity | `westlake-host/src/com/westlake/host/WestlakeActivity.java` |
| liboh_bridge.so (phone) | `westlake-host/jni/ohbridge_native.c` |
| ShimCompat | `shim/java/android/app/ShimCompat.java` |
| MiniServer | `shim/java/android/app/MiniServer.java` |
| MiniActivityManager | `shim/java/android/app/MiniActivityManager.java` |
| MockDonalds app | `test-apps/04-mockdonalds/src/com/example/mockdonalds/` |
| MockApp (Material UI) | `test-apps/04-mockdonalds/src/com/example/mockdonalds/MockApp.java` |
| XmlTestHelper | `test-apps/04-mockdonalds/src/com/example/mockdonalds/XmlTestHelper.java` |
| BinaryXmlParser | `shim/java/android/content/res/BinaryXmlParser.java` |
| OHBridge Java | `shim/java/com/ohos/shim/bridge/OHBridge.java` |
| x86_64 dalvikvm | `art-universal-build/build/bin/dalvikvm` |
| x86_64 OHBridge stub | `art-universal-build/stubs/ohbridge_stub.c` |
| Software renderer | `art-universal-build/stubs/ohbridge_render.c` |
| ARM64 dalvikvm | `art-universal-build/build-ohos-arm64/bin/dalvikvm` |
