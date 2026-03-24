# Agent A Tasks: OHOS Native Side — Make Pixels Appear

**Date:** 2026-03-24
**Status:** MockDonalds RUNS NATIVELY on Huawei Mate 20 Pro with real Android Views + touch navigation.

---

## Milestone: MockDonalds on Real Phone (2026-03-24)

MockDonalds runs on a Huawei Mate 20 Pro (LYA-L29, Android 10) using real
Android Views (LinearLayout, ListView, Button, TextView). Full touch navigation
works: menu -> item detail -> add to cart -> back to menu. Cart counter persists
across navigation.

**Key achievement:** Platform coupling reduced to only `liboh_bridge.so` (~25 C
functions wrapping Canvas/Paint/Path) + `WestlakeActivity.java` (~150 lines).
Everything else is platform-independent Java.

### How it works on phone

1. `WestlakeActivity` (extends `android.app.Activity`) creates a child-first `DexClassLoader`
2. The classloader loads `app.dex` containing MockDonalds + MiniServer + shim classes
3. Child-first loading ensures our shim `android.widget.*` classes take priority over framework
4. `ShimCompat` handles framework version incompatibilities via reflection
5. `OHBridge` has 170 registered JNI methods; on phone, `liboh_bridge.so` delegates to real Android Canvas/Paint/Path
6. Real Android Views render natively — no custom canvas drawing needed

---

## Milestone: MockDonalds on x86_64 ART (2026-03-23)

MockDonalds app runs the full Android Activity lifecycle on ART:

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

**Performance:** ~60 fps on x86_64 host (native ART, no emulation)

---

## Test Results Summary

| Platform | Runtime | FPS | Touch | Views | Status |
|----------|---------|-----|-------|-------|--------|
| x86_64 host | ART (AOT) | 60 | N/A | Headless draw log | 14/14 tests pass |
| ARM64 on phone | dalvikvm | 120 | Yes | Canvas rendering | Working |
| Mate 20 Pro native | Android 10 ART | Native | Yes | Real Android Views | Working |

---

## What was fixed to get here

### Phone-native milestone fixes
1. **WestlakeActivity** — Child-first DexClassLoader that loads app.dex, delegates only `java.*` and `android.app.Activity` to parent
2. **ShimCompat** — Reflection-based compatibility: handles `Context.getSystemService()`, `Resources`, `PackageManager` differences across framework versions
3. **OHBridge phone bridge** — `liboh_bridge.so` with 170 JNI methods, delegates Canvas/Paint/Path to real Android implementations (~25 methods, 4 classes)
4. **Touch pipeline** — DOWN/UP events written to file by viewer, read by dalvikvm event loop, dispatched to View tree
5. **Activity navigation** — MiniActivityManager back stack: finish() pops, startActivity() pushes, cart state persists

### x86_64 ART milestone fixes
1. **Runtime_nativeLoad** — Was no-op stub. Fixed to actually `dlopen()` + `JNI_OnLoad`
2. **registerNativesOrSkip** — Per-method registration so one bad signature doesn't block all methods
3. **ZipFile natives** — 12+ methods using mmap-based ZIP reading for ClassLoader.loadClass()
4. **UnixFileSystem natives** — File operations for java.io.File
5. **Math/StrictMath natives** — 27+ methods wrapping libc math
6. **x86_64 OHBridge stub** — Auto-generated 169 JNI stubs matching DEX signatures
7. **x86_64 boot image** — dex2oat AOT compilation (1.2s compile, <1s startup)

---

## Current architecture

### Phone-native path
```
Huawei Mate 20 Pro (Android 10)
├── WestlakeActivity.java (host, child-first DexClassLoader)
├── app.dex (MockDonalds + MiniServer + shim classes)
├── liboh_bridge.so (~25 methods → real Canvas/Paint/Path)
└── ShimCompat (reflection-based framework compat)

ClassLoader chain:
  BootClassLoader → PathClassLoader → Child-First DexClassLoader
  (app.dex classes loaded first, framework only for java.*/Activity)
```

### x86_64 fast path
```
Host x86_64
├── dalvikvm (x86_64, dynamically linked)
├── Boot image: boot.art + boot.oat (AOT compiled, 4 components)
├── Boot classpath: core-oj.jar + core-libart.jar + core-icu4j.jar + aosp-shim.dex
├── Native libraries:
│   ├── libicu_jni.so — charset converter stubs
│   ├── libjavacore.so — Linux I/O native methods
│   ├── libopenjdk.so — File I/O, Math, ZipFile, UnixFileSystem, Runtime.nativeLoad
│   └── liboh_bridge.so — 169 OHBridge JNI stubs (canvas, bitmap, surface, preferences, rdb)
└── app.dex — MockDonalds (MenuActivity, CartActivity, CheckoutActivity, etc.)
```

---

## Platform coupling analysis

**Platform-specific (must change per target):**
- `liboh_bridge.so`: ~25 C functions wrapping Canvas, Paint, FontMetrics, Path
- `WestlakeActivity.java`: ~150 lines, host Activity with child-first classloader

**Platform-independent (unchanged across targets):**
- MiniServer, MiniActivityManager, MiniWindowManager
- All View classes (LinearLayout, ListView, Button, TextView, etc.)
- ShimCompat, OHBridge Java side (170 methods)
- All application code (MockDonalds)
- Intent/Bundle/extras, BaseAdapter, Canvas/Paint Java API

**Total platform-specific surface: ~150 lines Java + ~25 C functions**

---

## Known issues

| Issue | Severity | Notes |
|-------|----------|-------|
| No SQLite on phone path | Medium | In-memory data only |
| No SharedPreferences | Medium | Cart state in memory only |
| No XML layout inflation | Medium | Programmatic layouts only |
| No Bitmap loading | Low | Text-only UI |
| No animation | Low | Static transitions |

---

## Remaining work

### P0: OHOS port
- Reimplement `liboh_bridge.so` (~25 functions) against ArkUI/Skia on OpenHarmony
- Port `WestlakeActivity` equivalent for OHOS app entry point
- Everything else stays the same

### P1: Feature gaps
- SQLite via JNI to native SQLite library
- SharedPreferences with file-backed XML storage
- XML layout inflation (AXML parser exists)
- Bitmap loading via Skia/stb_image

### P2: Polish
- View animations
- Resources.getString() from resources.arsc
- Multi-window support

---

## File locations

| What | Path |
|------|------|
| WestlakeActivity | `android-to-openharmony-migration/phone/WestlakeActivity.java` |
| liboh_bridge.so (phone) | `art-universal-build/stubs/oh_bridge_phone.c` |
| ShimCompat | `android-to-openharmony-migration/shim/java/.../ShimCompat.java` |
| MiniServer | `android-to-openharmony-migration/shim/java/.../MiniServer.java` |
| x86_64 dalvikvm | `art-universal-build/build/bin/dalvikvm` |
| x86_64 OHBridge stub | `/tmp/oh_bridge_x86_auto.c` (auto-generated) |
| openjdk stub | `art-universal-build/stubs/openjdk_stub.c` |
| javacore stub | `art-universal-build/stubs/javacore_stub.c` |
| ARM64 dalvikvm | `art-universal-build/build-ohos-arm64/bin/dalvikvm` |
| Test data dir (x86_64) | `/tmp/a2ohd/` |
| Westlake status doc | `docs/engine/WESTLAKE-STATUS.md` |
| Westlake status (CN) | `docs/engine/WESTLAKE-STATUS_CN.md` |

## Run x86_64 test
```bash
ART=/home/dspfac/art-universal-build/build
LD_LIBRARY_PATH=$ART/bin:$ART/lib:/tmp/a2ohd \
ANDROID_DATA=/tmp/a2ohd ANDROID_ROOT=/tmp/a2ohd \
$ART/bin/dalvikvm \
  -Xbootclasspath:/tmp/a2ohd/core-oj.jar:/tmp/a2ohd/core-libart.jar:/tmp/a2ohd/core-icu4j.jar:/tmp/a2ohd/aosp-shim.dex \
  -Ximage:/tmp/a2ohd/boot.art -Xverify:none \
  -Djava.library.path=/tmp/a2ohd:$ART/lib \
  -classpath /tmp/a2ohd/app.dex \
  com.example.mockdonalds.MockDonaldsApp
```

---

## Success criteria

1. ~~artFindNativeMethod finds OHBridge methods via dlsym~~ Done
2. ~~MockDonalds MenuActivity launches and calls View.draw()~~ Done
3. ~~MockDonalds runs on real phone with touch navigation~~ Done
4. ~~Cart counter persists across Activity navigation~~ Done
5. OHOS port: reimplement liboh_bridge.so for ArkUI/Skia -- Next
6. SQLite + SharedPreferences -- P1
