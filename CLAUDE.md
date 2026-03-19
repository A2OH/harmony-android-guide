# Westlake -- Build & Development Guide

## What This Project Is

This project runs **unmodified Android APKs on OpenHarmony** using:
1. **Dalvik VM** -- KitKat-era VM ported to 64-bit (x86_64, OHOS aarch64, OHOS ARM32)
2. **AOSP Framework** -- 140,000+ lines of unmodified AOSP code across 94+ files (View, ViewGroup, TextView, LinearLayout, ListView, GridView, Spinner, ScrollView, ProgressBar, SearchView, Toolbar, etc.)
3. **Stub Layer** -- ~200 minimal stub files for system service dependencies (ViewRootImpl, AccessibilityManager, Editor deps, etc.)
4. **OHBridge** -- JNI bridge (169 methods) routing Android API calls to OHOS native APIs
5. **MiniServer** -- Lightweight replacement for Android SystemServer (6 managers, ~2000 lines)

The engine approach: 99% of Android API calls stay inside the VM as pure Java. Only ~15 HAL-level boundaries cross to native code via JNI.

## Key Skills for Building This Project

### 1. Option B AOSP Integration

Copy ENTIRE AOSP .java files unmodified. Never cherry-pick methods or modify AOSP code.

- Source: `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/`
- Target: `shim/java/android/` (same package path)
- Currently: 140,000+ lines across 94+ AOSP files (View, ViewGroup, TextView, AbsListView, ListView, GridView, Spinner, etc.)
- When AOSP code references missing classes, create stub files that compile but return defaults
- Use `scripts/aosp-stub-gen.py` to auto-generate stubs from compile errors
- Fix compile errors in TEST code or STUB code, NEVER in AOSP code

### 2. Closed-Loop Visual Debugging

Render an Activity to pixels without any hardware or ArkUI:

```
Activity.onCreate() -> setContentView(layout)
  -> View.measure() -> View.layout() -> View.draw(Canvas)
    -> Canvas records draw log (mock OHBridge)
      -> PixelRenderer reads draw log -> Java2D -> PNG file
```

- Run: `java -cp build FrameDumper` (outputs /tmp/mockdonalds-menu.png etc.)
- Read the PNG to identify layout bugs
- Fix shim/stub code -> re-render -> verify visually
- No hardware or ArkUI needed at any point

### 3. Auto-Stub Generator

```bash
python3 scripts/aosp-stub-gen.py
```

Iteratively:
1. Compiles AOSP file with all shim code
2. Parses javac error output
3. Generates minimal stub classes/methods for missing dependencies
4. Repeats until compilation succeeds

### 4. DEX Building

For KitKat Dalvik compatibility, target DEX format 035:

```bash
# Option A: javac --release 8 + dx (simplest)
javac -d classes --release 8 *.java
java -jar dx.jar --dex --output=classes.dex classes/

# Option B: javac --release 11 + d8 (needed for AOSP TextView completeOnTimeout)
javac -d classes --release 11 *.java
java -jar r8-8.5.35.jar --min-api 19 --output . classes/
```

Constraints:
- No lambdas in shim code (KitKat Dalvik cannot handle invokedynamic)
- No String.format, String.split, Pattern.compile (KitKat natives missing)
- Use SimpleFormatter, splitByChar helpers instead

### 5. Test Infrastructure

```bash
# Primary test command -- compiles everything, runs on host JVM
cd test-apps && ./run-local-tests.sh headless

# Individual suites
./run-local-tests.sh ui           # View tree, measure/layout/draw
./run-local-tests.sh mockdonalds  # End-to-end restaurant app
./run-local-tests.sh realapk      # APK unpack, AXML parse, Activity launch
./run-local-tests.sh all          # All of the above
```

Key files:
- `test-apps/run-local-tests.sh` -- compiles all shim + test code, runs headless
- `test-apps/mock/` -- Mock OHBridge that records draw log, no native code needed
- `test-apps/02-headless-cli/src/HeadlessTest.java` -- 2,470+ self-validation checks
- `test-apps/03-ui-mockup/src/UITestApp.java` -- 53 UI rendering checks
- `test-apps/04-mockdonalds/` -- 14 end-to-end checks
- `test-apps/11-frame-dump/` -- PixelRenderer for PNG screenshot generation

### 6. Real APK Analysis

```bash
# Extract type references from an APK
dexdump -f app.apk | grep "Class descriptor"

# Categorize: app classes / AndroidX / android.* / java.*
# Cross-reference against shim layer for gap report
```

### 7. Running on Dalvik VM

```bash
cd dalvik-port
export ANDROID_DATA=/tmp/android-data ANDROID_ROOT=/tmp/android-root
mkdir -p $ANDROID_DATA/dalvik-cache $ANDROID_ROOT/bin

# Run with AOSP shim DEX on bootclasspath
./build/dalvikvm -Xverify:none -Xdexopt:none \
  -Xbootclasspath:$(pwd)/core-android-x86.jar:/path/to/aosp-shim.dex \
  -classpath /path/to/app.dex \
  com.example.app.MainActivity
```

## Important Constraints

- **No lambdas** in shim code (KitKat Dalvik)
- **No String.format / String.split / Pattern.compile** (KitKat natives missing)
- **No Co-Authored-By** in git commits
- **Option B = copy ENTIRE AOSP files unchanged**, stub missing deps, NEVER cherry-pick
- **Never modify AOSP code** -- fix tests or stubs instead
- **Match AOSP method signatures exactly** -- apps depend on them
- **Don't break existing tests** -- always run full suite before pushing

## Repository Layout

```
A2OH/westlake           -- Integration (this repo)
A2OH/dalvik-universal    -- Dalvik VM source + 64-bit port
A2OH/openharmony-wsl     -- OHOS build on WSL2/QEMU
```

### This Repo Structure

```
westlake/
├── shim/java/android/          # 2,056 Java shim files (126,625 lines)
│   ├── app/                    # Activity, Fragment, MiniServer, Service
│   ├── content/                # Intent, ContentProvider, SharedPreferences
│   ├── database/               # SQLite, Cursor, MatrixCursor
│   ├── graphics/               # Canvas, Paint, Bitmap, Path, Color
│   ├── net/                    # Uri, ConnectivityManager
│   ├── os/                     # Bundle, Handler, Looper, Parcel
│   ├── view/                   # View (30K lines AOSP), ViewGroup (9K)
│   ├── widget/                 # TextView (13K), Button, LinearLayout, ...
│   └── ...                     # 137 android.* packages total
├── shim/java/com/ohos/shim/    # OHBridge JNI (169 native methods)
├── test-apps/
│   ├── 02-headless-cli/        # Headless test harness (2,470 checks)
│   ├── 03-ui-mockup/           # UI rendering tests (53 checks)
│   ├── 04-mockdonalds/         # End-to-end restaurant app (14 checks)
│   ├── 06-real-apk/            # APK loading pipeline
│   ├── 11-frame-dump/          # Pixel rendering + PNG output
│   ├── mock/                   # Mock OHBridge (JVM testing, no device)
│   └── run-local-tests.sh      # Test runner
├── dalvik-port/                # Dalvik VM (x86_64, ARM32, aarch64)
├── database/api_compat.db      # 57,289 APIs, tier classification, OH mappings
├── scripts/                    # Stub generator, issue creator, orchestration
└── skills/                     # 8 conversion skill files (A2OH-*)
```

## Common Tasks

| Task | How |
|------|-----|
| Run tests | `cd test-apps && ./run-local-tests.sh headless` |
| Add a new AOSP class | Copy from AOSP source, stub deps, compile, test |
| Fix test regression | Update TEST code or STUB code, never AOSP code |
| Build DEX for Dalvik | `javac --release 8` then `dx --dex` (or `d8` for release 11) |
| Render PNG screenshot | `java -cp build FrameDumper` -> `/tmp/*.png` |
| Analyze an APK | `dexdump` -> categorize types -> gap report |
| Add AOSP class to engine | Copy from AOSP, stub deps, compile, test (Option B) |

## Project Key Paths

| What | Path |
|------|------|
| Shim Java sources | `shim/java/android/` |
| OHBridge (real) | `shim/java/com/ohos/shim/bridge/OHBridge.java` |
| OHBridge (mock for testing) | `test-apps/mock/com/ohos/shim/bridge/OHBridge.java` |
| Headless test harness | `test-apps/02-headless-cli/src/HeadlessTest.java` |
| UI test harness | `test-apps/03-ui-mockup/src/UITestApp.java` |
| Test runner script | `test-apps/run-local-tests.sh` |
| API compatibility database | `database/api_compat.db` |
| Dalvik VM (x86_64) | `dalvik-port/build/dalvikvm` |
| Dalvik VM (OHOS ARM32) | `dalvik-port/build-ohos-arm32/dalvikvm` |
| AOSP source (local) | `/home/dspfac/aosp-android-11/frameworks/base/core/java/android/` |
