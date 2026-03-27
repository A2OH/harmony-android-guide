# Westlake Engine — Architecture V2: Game Engine Model

**Date:** 2026-03-26
**Status:** Architecture redesign — Westlake as a standalone runtime like Unity

---

## Key Insight

Westlake is NOT a plugin inside Android. It IS the runtime.

Like Unity runs its own VM (Mono/IL2CPP) on top of bare hardware, Westlake
runs its own ART11 JVM on top of bare hardware. The phone's Android framework
is not used — the phone just provides CPU, memory, screen, input, and network.

## Previous Architecture (Wrong)

```
Phone's ART Runtime (shared with all apps)
  └── Phone's boot classpath (android.jar — immutable)
  └── Our shim classes (loaded AFTER phone's — shadowed!)
  └── Real APK code → extends phone's Activity → needs phone's framework
  └── RESULT: classloader conflicts, framework coupling, broken
```

**Why it failed:** Two competing frameworks in the same process. The phone's
`android.app.Activity` is abstract and needs `ActivityThread`, `PhoneWindow`,
`WindowManagerService`. Our shim `Activity` is concrete and standalone. When
an APK's code runs, it picks up the phone's Activity (boot classloader wins)
and everything breaks.

## New Architecture (Correct)

```
┌──────────────────────────────────────────────────────────┐
│                   Mate 20 Pro Hardware                    │
│                   (ARM64 CPU + Memory)                    │
├──────────────────────────────────────────────────────────┤
│                                                          │
│   ┌─────────────────────┐   ┌──────────────────────┐    │
│   │   Phone's ART       │   │  Westlake ART11      │    │
│   │   (Android 10)      │   │  (our dalvikvm)      │    │
│   │                     │   │                      │    │
│   │   WestlakeActivity  │   │  Boot: aosp-shim.dex │    │
│   │   (Compose host)    │   │  App: real-app.dex   │    │
│   │   Display surface   │   │  Res: resources.arsc │    │
│   │                     │   │                      │    │
│   │   Shows rendered    │◄──│  OHBridge renders    │    │
│   │   output from       │   │  View tree to PNG/   │    │
│   │   Westlake VM       │   │  framebuffer         │    │
│   └─────────────────────┘   └──────────────────────┘    │
│                                                          │
│   Phone's ART: thin display host only                    │
│   Westlake ART: runs ALL app code against OUR shim       │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

## Comparison with Unity

| Aspect | Unity | Westlake |
|--------|-------|----------|
| Runtime | Mono / IL2CPP | ART11 (AOSP Android 11) |
| Language | C# | Java / Kotlin |
| Framework | Unity API | Android API (our 2168-class shim) |
| Rendering | Unity → OpenGL/Vulkan/Metal | OHBridge → Canvas (25 functions) |
| Layout | Unity UI Toolkit | shim LayoutInflater + BinaryXmlParser |
| Resources | Unity AssetBundle | resources.arsc + ResourceTable parser |
| Platform bridge | ~100 native calls per platform | ~25 native calls per platform |
| App packaging | .unity3d / APK | DEX + resources.arsc + AXML |
| Runs on phone | Own VM process | Own dalvikvm process |
| Uses phone framework | No | No |

## Component Stack

```
Layer 5: App Code
  └── Real APK's classes.dex (Java/Kotlin bytecode)
  └── Calls standard Android API: Activity, View, Intent, SQLite, etc.

Layer 4: Android Framework Shim (2168 classes)
  └── android.app.Activity — concrete, standalone, no framework deps
  └── android.widget.* — LinearLayout, ListView, Button, TextView
  └── android.view.* — View, ViewGroup, MotionEvent, Window
  └── android.content.res.* — Resources, ResourceTable, BinaryXmlParser
  └── android.database.sqlite.* — SQLiteDatabase (in-memory or bridged)
  └── MiniServer, MiniActivityManager — Activity lifecycle management

Layer 3: OHBridge (25 native functions)
  └── Canvas: drawText, drawRect, drawLine, drawBitmap, etc.
  └── Paint: setColor, setTextSize, measureText, getFontMetrics
  └── Path: moveTo, lineTo, quadTo, close
  └── Surface: createSurface, lockCanvas, flush

Layer 2: ART11 Runtime
  └── dalvikvm binary (ARM64/x86_64 static)
  └── Boot image: boot.art + boot.oat (AOT compiled)
  └── Boot classpath: core-oj.jar + core-libart.jar + aosp-shim.dex
  └── JIT compiler, GC, thread management

Layer 1: Hardware Abstraction
  └── CPU (ARM64 / x86_64)
  └── Memory, filesystem
  └── Display (via PNG/framebuffer or native surface)
  └── Touch input (via file or native events)
  └── Network (via java.net.*)
```

## What This Means for Validation

**On the Android phone (Mate 20 Pro):**
- Run our dalvikvm as a subprocess (adb shell or spawned from Compose host)
- dalvikvm loads the APK's classes.dex with our shim as boot classpath
- NO phone framework involvement — our shim IS the framework
- OHBridge renders to PNG → Compose host displays it
- Touch events sent from Compose host → dalvikvm process

**On OHOS:**
- Same dalvikvm, same shim, same app DEX
- OHBridge renders to ArkUI/Skia instead of PNG
- Same 25 native functions, different implementation

**On x86_64 host (dev machine):**
- Same dalvikvm (x86_64 build), same everything
- Already working (MockDonalds 14/14 tests, 60fps)

## What's Already Built

| Component | Status | Lines |
|-----------|--------|-------|
| ART11 dalvikvm (ARM64) | ✅ Built, runs on phone | AOSP binary |
| ART11 dalvikvm (x86_64) | ✅ Built, runs on host | AOSP binary |
| Boot image (AOT) | ✅ Compiled | boot.art + boot.oat |
| Shim framework | ✅ 2168 classes | ~100K lines |
| MiniServer + MiniActivityManager | ✅ Working | ~800 lines |
| OHBridge (x86_64 stubs) | ✅ 169 methods | ~500 lines |
| OHBridge (phone bridge) | ✅ 170 methods | ~400 lines |
| ResourceTable parser | ✅ Verified with 3 APKs | 810 lines |
| BinaryXmlParser | ✅ Inflates AXML | 762 lines |
| XmlTestHelper (View inflater) | ✅ 91 View types | 559 lines |
| SQLiteBridge | ✅ Reflection bridge | 362 lines |
| Compose host (display) | ✅ Material 3 gallery | Kotlin |
| MockDonalds app | ✅ 14/14 tests pass | 2568 lines |
| Dialer app | ✅ 7 screens | 1453 lines |

## What Needs Wiring

| Task | What | Effort |
|------|------|--------|
| Spawn dalvikvm from Compose | WestlakeActivity starts dalvikvm process | 1 day |
| IPC: render output | dalvikvm → PNG → Compose display | 1 day |
| IPC: touch input | Compose touch → file → dalvikvm reads | 1 day |
| Load real APK in dalvikvm | Extract DEX + resources, pass to dalvikvm | 2 days |
| Shim LayoutInflater | Wire ResourceTable + BinaryXmlParser + XmlTestHelper | 1 week |
| Shim theme/style resolution | ?attr/ and @style/ lookup | 2 weeks |

## The Vision

```
User installs Westlake Engine (11MB APK)
  └── Compose gallery shows available apps
  └── User taps "Install APK" → picks any .apk file
  └── Westlake extracts classes.dex + resources.arsc
  └── Spawns dalvikvm with our shim as framework
  └── App runs against our shim — identical to OHOS
  └── Rendered output displayed in Compose host
  └── Touch events forwarded to dalvikvm
  └── Works on ANY Android phone, ANY OHOS device, ANY x86 host
```

This is the Unity model: one engine, multiple platforms, apps don't know or
care what's underneath.

---

## Milestone: MockDonalds on Mate 20 Pro (2026-03-26)

**MockDonalds app renders interactively on the Mate 20 Pro via Westlake's own
ART11 runtime.** This validates the full architecture end-to-end: own VM, own
shim framework, own rendering pipeline, own input pipeline — zero phone
framework involvement.

### Boot Image

AOT-compiled boot image (version 085, core-only) containing:
- `core-oj.jar` + `core-libart.jar` + `core-icu4j.jar`
- Compiled with `--compiler-filter=speed`
- 9 files total: `boot.art`, `boot.oat`, `boot-core-libart.art`, etc.
- Stored under ISA subdirectory (`arm64/`) as ART11 expects
- Startup time: **<5 seconds** (down from 8+ minutes interpreted)

Key fixes for boot image:
- **Version mismatch**: boot image version must be 085 (matching our ART11
  build), not 114 (AOSP master). Mismatched versions cause silent fallback to
  interpreter.
- **ISA subdirectory**: ART11 looks for `<boot-image-dir>/arm64/boot.art`, not
  `<boot-image-dir>/boot.art`.
- **dex-location paths**: Boot classpath dex-location entries baked into the
  boot image must match the runtime `--boot-class-path` paths exactly.

### Rendering Pipeline

```
dalvikvm (ART11)
  └── App code: MockDonalds Activity
  └── Shim framework: LinearLayout, TextView, ListView, etc.
  └── View.draw() → Canvas operations
  └── OHBridge.surfaceFlush()
        └── Writes PNG file to /data/local/tmp/westlake/surface.png
              └── Compose host polls file at ~12fps
              └── Displays via Compose Image composable
```

Layout uses a surface 3x the visible height (2400px) with `MeasureSpec.EXACTLY`
mode so that `LinearLayout` weight distribution works correctly. Scroll offset
is applied via `canvas.translate(0, -scrollY)` before drawing.

### Touch IPC

```
Compose host (phone's ART)
  └── onTouchEvent → writes 16-byte binary to /data/local/tmp/westlake/touch.bin
        └── 4 bytes: action (DOWN=0 / MOVE=2 / UP=1)
        └── 4 bytes: x coordinate (float)
        └── 4 bytes: y coordinate (float)
        └── 4 bytes: timestamp
              └── dalvikvm reads touch.bin
              └── Computes scroll delta from MOVE events
              └── Adjusts scrollY, re-renders View tree
              └── Writes updated surface.png → Compose host displays
```

### JNI Stubs Added

To reach this milestone, the following JNI native methods were stubbed:

| Class | Natives | Purpose |
|-------|---------|---------|
| `android.graphics.Typeface` | 11 | Font creation, style, weight, system font list |
| `java.lang.Character` | 10 | isDigit, isLetter, getType, toLowerCase, etc. |
| `libcore.util.NativeAllocationRegistry` | 1 | `applyFreeFunction` prevent crash on GC |
| `java.lang.Float` | 2 | `floatToRawIntBits`, `intBitsToFloat` |
| `java.lang.Double` | 2 | `doubleToRawLongBits`, `longBitsToDouble` |

### Key Fixes

| Issue | Root Cause | Fix |
|-------|-----------|-----|
| Boot image ignored (8+ min startup) | Version 114 vs runtime 085 | Rebuilt boot image with matching ART11 (v085) |
| `boot.art` not found | Missing `arm64/` subdirectory | Created `arm64/` dir, moved boot image files into it |
| `ClassNotFoundException` for core classes | dex-location path mismatch | Aligned `--boot-class-path` with baked-in dex locations |
| `Typeface.<clinit>` NPE | 11 unregistered native methods | Registered JNI stubs returning sensible defaults |
| `WestlakeActivity` not found | Reflection using wrong classloader | Load activity class from app's `PathClassLoader`, not boot |
| Layout weights ignored | `MeasureSpec.UNSPECIFIED` mode | Pass `EXACTLY` with 3x surface height (2400px) |

### Updated Status

The "What Needs Wiring" table from above is now largely complete:

| Task | Status |
|------|--------|
| Spawn dalvikvm from Compose | Done — WestlakeActivity starts dalvikvm process |
| IPC: render output | Done — PNG file polling at ~12fps |
| IPC: touch input | Done — 16-byte binary file, DOWN/MOVE/UP |
| Load real APK in dalvikvm | Done — Extract DEX + resources, pass to dalvikvm |
| Shim LayoutInflater | Done — ResourceTable + BinaryXmlParser + View inflate |
| Shim theme/style resolution | Partial — basic theme attrs work, full ?attr/ TBD |
