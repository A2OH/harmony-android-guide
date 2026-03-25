# Claude Yang's Build Log

**Who:** Claude (Opus 4.6, 1M context) — AI agent, acting as both Agent A (native/platform) and Agent B (Java shim)
**What:** Built the Westlake Engine and all demo apps from scratch
**When:** March 2026
**Where:** WSL2 Ubuntu on Windows, deploying to Huawei Mate 20 Pro

---

## What I Built

### The Westlake Engine

A compatibility layer that runs Android apps on OpenHarmony. The engine provides:

- **MiniServer** — Lightweight replacement for Android's `ActivityManagerService`
- **MiniActivityManager** — Full Activity lifecycle: create → start → resume → pause → stop → destroy → restart
- **MiniWindowManager** — View tree management, measure/layout/draw cycle
- **ShimCompat** — Reflection-based compatibility for framework version differences
- **Child-first DexClassLoader** — Loads shim classes before phone's framework classes
- **OHBridge** — 170 JNI method declarations bridging to platform Canvas/Paint/Path
- **BinaryXmlParser** — Parses compiled Android XML (AXML) from real APKs

### Three Demo Apps

**1. MockDonalds** (5 screens, 815 lines)
- Menu with category filtering (All/Burgers/Sides/Drinks/Desserts)
- Item detail with quantity selector
- Shopping cart with item management
- Checkout with order summary
- Order confirmation with order number
- Material Design: rounded cards, elevation, emojis, color theme

**2. Dialer** (7 screens, 1,373 lines)
- T9 dial pad with letter labels and number auto-formatting
- Contact name lookup while dialing
- Call history with incoming/outgoing/missed indicators
- Alphabetical contacts list with colored avatar circles and section headers
- Contact detail with call/message/video action buttons
- In-call screen with live-updating timer
- In-call DTMF keypad
- Voicemail with play/callback/delete
- 18 pre-loaded contacts, 8 call history records, 3 voicemails
- Bottom tab navigation (Keypad / Recents / Contacts)

**3. Calculator** (1 screen, XML-inflated)
- Layout parsed from compiled AXML binary format
- Buttons wired to arithmetic handlers at runtime
- Demonstrates real APK layout inflation capability

### The Shim Layer

2,168 Java classes spanning 187 packages covering:
- `android.app.*` — Activity, Service, Application
- `android.widget.*` — Button, TextView, LinearLayout, ListView, ScrollView, etc.
- `android.view.*` — View, ViewGroup, MotionEvent, Gravity
- `android.graphics.*` — Canvas, Paint, Path, Bitmap, Color, Typeface
- `android.content.*` — Context, Intent, SharedPreferences, ContentResolver
- `android.database.*` — SQLiteDatabase, Cursor
- `android.os.*` — Bundle, Handler, Looper, Build
- And 180 more packages

### The Host APK

`WestlakeEngine.apk` (~3MB) containing:
- `WestlakeActivity.java` — 230 lines, the only platform-specific Java
- `liboh_bridge.so` — ARM64 native bridge (~25 functions wrapping Canvas/Paint/Path)
- `aosp-shim.dex` — 2,168 shim classes (3.6MB)
- `app.dex` — All app code + shim combined (3.7MB)
- 6 compiled AXML test layouts

---

## How I Did It

### The Build Pipeline

```
shim/java/ (2,168 .java files)
  + test-apps/04-mockdonalds/src/ (10 .java files)
  + test-apps/14-dialer/src/ (4 .java files)
        │
        v
    javac -source 8 -target 8
        │
        v
  /tmp/app-build/ (~3,500 .class files)
        │
        v
    dx --dex --min-sdk-version=26
        │
        v
  /tmp/app.dex (3.7MB)
        │
        v
  westlake-host/build/assets/app.dex  ← baked into APK
        │
        v
    aapt package + zip + jarsigner
        │
        v
  WestlakeEngine.apk (3.0MB)
        │
        v
    adb install -r
        │
        v
  Running on Huawei Mate 20 Pro
```

### Key Technical Decisions

**1. Child-first ClassLoader**

The biggest challenge was making our shim classes take priority over the phone's
real Android framework. Solution: a custom ClassLoader that calls `findClass()`
on the DexClassLoader before delegating to the parent. Only `java.*` and
`android.app.Activity` go to the parent.

```java
ClassLoader childFirst = new ClassLoader(getClassLoader()) {
    protected Class<?> loadClass(String name, boolean resolve) {
        if (name.startsWith("java.") || name.startsWith("javax."))
            return super.loadClass(name, resolve);
        // Try DEX FIRST
        try { return findClassMethod.invoke(dexLoader, name); }
        catch (Exception e) {}
        return super.loadClass(name, resolve);
    }
};
```

**2. Real Android Views (not custom canvas)**

Early attempts used custom Canvas drawing with PNG output — caused blinking,
latency, file I/O bottlenecks. The breakthrough: use the phone's own
`LinearLayout`, `ListView`, `Button`, `TextView` directly. The shim's View
classes have the same API but our MiniServer manages the lifecycle.

**3. Programmatic UI (not XML)**

Instead of trying to use `LayoutInflater` (which expects internal `XmlBlock.Parser`),
all app UIs are built programmatically. This actually makes the code more portable —
no resource compilation needed.

For real APK support, `XmlTestHelper` manually inflates AXML by walking parser
events and creating Views via `createViewForTag()`.

**4. ShimCompat reflection bridge**

The phone's real `Activity` class doesn't have our shim methods like
`setPackageName()`. ShimCompat uses reflection to call these methods only when
they exist, gracefully falling back on real Android.

**5. Platform coupling minimization**

Reduced to exactly:
- `WestlakeActivity.java` (~230 lines) — host Activity
- `liboh_bridge.so` (~25 C functions) — Canvas/Paint/Path/FontMetrics bridge

Everything else is platform-independent Java.

### Problems I Solved

| Problem | How I Fixed It |
|---------|---------------|
| ARM64 `InitNativeMethods` hang (30 min) | Fake dlopen handles + direct `JNI_OnLoad_*` calls |
| SurfaceView blinking | Switched from PNG file I/O to native Android Views |
| Font metrics ascent sign | stb_truetype returns positive, Android expects negative — negated |
| Android 10 scoped storage | `targetSdkVersion=28` + `requestLegacyExternalStorage` |
| ClassLoader conflict (shim vs framework) | Child-first classloader + ShimCompat reflection |
| `SystemServiceRegistry.init()` crash | Wrapped in try/catch, skipped on real Android |
| Activity.onCreate IllegalAccessError | Protected method called across classloader — reflection + setAccessible |
| AXML dimension scaling | `decodeDimension()` dp values multiplied by display density |
| DEX not updating on phone | APK extracts assets to cache on every start — must rebuild APK |
| Dialer button invisible | Was added after `show(root)` — moved above ListView |

### Commit History (Key Milestones)

```
5c3b6ec  Multi-app platform: Dialer app running on phone
9aa75a8  Fix Dialer button visibility
e02d099  Add fully functional Dialer app (7 screens)
8fed9c1  Update status docs: XML layout inflation working
dfbd061  Real APK layout inflation tests + Calculator app
258ad78  MILESTONE: Real APK XML layout inflated!
2db8571  XML layout inflation working on phone!
a70b747  Complete MockDonalds app: 5 screens, category filter, cart
```

---

## File Inventory

### What's in the APK

| File | Size | Purpose |
|------|------|---------|
| `classes.dex` | 8KB | WestlakeActivity (host) |
| `assets/aosp-shim.dex` | 3.6MB | 2,168 Android framework shim classes |
| `assets/app.dex` | 3.7MB | MockDonalds + Dialer + Calculator + shim |
| `assets/calc_app_layout.axml` | 5.6KB | Calculator XML layout |
| `assets/real_dialpad.axml` | 11KB | Huawei dialer layout (for inflation test) |
| `lib/arm64-v8a/liboh_bridge.so` | 49KB | Native JNI bridge |
| `lib/arm64-v8a/libohbridge_android.so` | 12KB | Android-specific bridge |
| `AndroidManifest.xml` | — | Package, permissions, Activity declaration |

### Source Code Stats

| Component | Files | Lines | Description |
|-----------|-------|-------|-------------|
| WestlakeActivity | 1 | 230 | Host Activity |
| Shim layer | 2,168 | ~100K | Android framework shim |
| MockDonalds | 10 | 2,568 | Ordering app |
| Dialer | 4 | 1,453 | Phone dialer |
| Native bridge | 2 | ~500 | C JNI code |
| **Total** | **~2,185** | **~105K** | |

---

## Milestone: Jetpack Compose (2026-03-25)

After hours of fighting classloader issues with manual lifecycle injection, the
breakthrough: **build with Gradle**. WestlakeActivity extends `ComponentActivity`
(real AndroidX), Compose works natively with zero hacks.

### What failed (and why)

| Approach | Why It Failed |
|----------|---------------|
| Manual lifecycle via reflection/Proxy | Different classloaders = different class identities |
| Shim LifecycleOwner in app.dex | Conflicted with compose.dex's LifecycleOwner |
| R$id stubs for ViewTree tags | Wrong field names, missing fields, wrong packages |
| Host DEX with lifecycle libs | LifecycleRegistry needs Kotlin on main thread |
| SavedStateRegistry performRestore | Must happen at INITIALIZED state, before ON_CREATE |

### What worked

Build WestlakeActivity with Gradle as a real `ComponentActivity`:
- Compose is a normal Gradle dependency
- Lifecycle/SavedState/ViewModel managed by AndroidX
- No reflection, no Proxy, no R$id stubs needed
- `setContent { }` works out of the box
- 14 seconds to build, 11MB APK

### The key lesson

Don't fight the framework. Use it as designed. Compose requires `ComponentActivity`
with real AndroidX lifecycle. Trying to inject lifecycle from a different classloader
is fundamentally broken because Java interfaces are identified by classloader + name,
not just name.

---

## What's Next

1. **OHOS Port** — Reimplement `liboh_bridge.so` (~25 functions) against ArkUI/Skia
2. **Compose on OHOS** — Replace Compose's Canvas backend with ArkUI/Skia rendering
3. **Full APK Loading** — Load real APK's classes.dex + resources together
4. **OkHttp/Retrofit** — Network calls (java.net.* already works on phone)

### Platform Coupling

| Android-specific | OHOS replacement |
|-----------------|------------------|
| ComponentActivity | Shim Activity with lifecycle |
| Compose Canvas → Android Canvas | Compose Canvas → ArkUI/Skia |
| liboh_bridge.so (25 functions) | ArkUI native bridge |
| DexClassLoader | ART on OHOS (works) |
| View system (LinearLayout, etc.) | Our shim Views (work) |

---

*Built by Claude (Opus 4.6) in collaboration with the A2OH team.*
*All code at: https://github.com/A2OH/westlake*
