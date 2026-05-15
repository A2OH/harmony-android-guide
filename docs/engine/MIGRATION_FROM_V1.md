# Migration From V1 (Binder Pivot Design) to V2

**Status:** Companion to `BINDER_PIVOT_DESIGN_V2.md` (CR28-architect, 2026-05-13)
**Purpose:** Concrete file-by-file delta — what to keep, touch, delete in the current codebase to land V2 substrate.
**Audience:** The agent implementing V2 Steps 1-11.

This is *not* the architectural argument (that's in V2 §1-12). This is the workitem list.

---

## 1. Files to KEEP unchanged

These are V1's wins; do not touch them.

### 1.1 Binder substrate

- `aosp-libbinder-port/src/**` — entire libbinder source tree (musl + bionic)
- `aosp-libbinder-port/patches/0001-musl-clock-monotonic.patch`
- `aosp-libbinder-port/patches/0002-rpcserver-listen.patch`
- `aosp-libbinder-port/patches/0003-parcel-kheader-syst.patch`
- `aosp-libbinder-port/patches/0004-parcel-accept-any-kheader-on-recv.patch`
- `aosp-libbinder-port/cmds/servicemanager/**` — servicemanager src
- `aosp-libbinder-port/test/HelloBinder.java` — M3 smoke
- `aosp-libbinder-port/test/AsInterfaceTest.java` — M3++ smoke
- `aosp-libbinder-port/test/PowerServiceTest.java` etc. — M4 smokes
- `aosp-libbinder-port/test/SystemServiceRouteTest.java` — CR3/4/5 smoke
- `aosp-libbinder-port/test/DiscoverWrapperBase.java` — CR27 manifest harness (includes CR24 workaround at lines 438-461)
- `aosp-libbinder-port/test/NoiceDiscoverWrapper.java`, `McdDiscoverWrapper.java`
- `aosp-libbinder-port/test/noice.discover.properties`, `mcd.discover.properties`
- `aosp-libbinder-port/build.sh`, `build_hello.sh`, `build_asinterface.sh`, `build_*service_test.sh`, `build_discover.sh`, `build_mcd_discover_wrapper.sh`, `mcd-discover.sh`, `noice-discover.sh`

### 1.2 dalvikvm + native JNI clusters

- `art-latest/build-bionic-arm64/bin/dalvikvm`
- `art-latest/stubs/binder_jni_stub.cc` — M3 + M3++ ServiceManager natives
- `art-latest/stubs/messagequeue_jni_stub.cc` — M4-PRE4
- `art-latest/stubs/audiosystem_jni_stub.cc` — M5-PRE
- `art-latest/patches/PF-arch-001..055.patch` — all substrate fixes, including CR15 widen-the-guard and CR26 substrate fix for env->functions corruption
- `art-latest/stubs/openjdk_stub.c` — Runtime_nativeLoad short-circuit

**EXCEPTION**: `art-latest/stubs/assetmanager_jni_stub.cc` — see §3.4 below; **delete after Step 8 confirms no caller**.

### 1.3 Java services (M4 — 6 services + ServiceManager)

- `shim/java/com/westlake/services/WestlakeActivityManagerService.java` (672 LOC)
- `shim/java/com/westlake/services/WestlakeWindowManagerService.java` (426 LOC)
- `shim/java/com/westlake/services/WestlakePackageManagerService.java`
- `shim/java/com/westlake/services/WestlakeDisplayManagerService.java` (233 LOC)
- `shim/java/com/westlake/services/WestlakeNotificationManagerService.java` (303 LOC)
- `shim/java/com/westlake/services/WestlakeInputMethodManagerService.java` (194 LOC)
- `shim/java/com/westlake/services/WestlakePowerManagerService.java` (343 LOC)
- `shim/java/com/westlake/services/ServiceRegistrar.java` (179 LOC)
- `shim/java/com/westlake/services/ServiceMethodMissing.java` (76 LOC)
- `shim/java/com/westlake/services/SystemServiceWrapperRegistry.java` (573 LOC post-CR5)
- `shim/java/android/os/ServiceManager.java` — M3 rewrite
- companion @hide AIDL stubs under `shim/java/{android/*, com/android/internal/*}` (~70 files)

### 1.4 Local PackageManager

- `shim/java/com/westlake/services/WestlakePackageManagerStub.java` (556 LOC) — M4-PRE5 + CR19 fail-loud
- `shim/java/com/westlake/services/WestlakeContentResolver.java` (276 LOC) — M4-PRE9

### 1.5 Frozen Context

- `shim/java/com/westlake/services/WestlakeContextImpl.java` (714 LOC) — CR22 frozen surface; **don't grow** it
  - **Exception**: §2.1 below adds `setAttachedApplication(Application)` getter wiring that CR23-fix already introduced; preserve.

### 1.6 Pre-pivot generic Westlake classes (survive, expand minimally)

These predate the drift, are not per-app, and are V2's foundation for generic substrate:

- `shim/java/com/westlake/engine/WestlakeLayoutInflater.java`
- `shim/java/com/westlake/engine/WestlakeInflater.java`
- `shim/java/com/westlake/engine/WestlakeTheme.java`
- `shim/java/com/westlake/engine/WestlakeVector.java`
- `shim/java/com/westlake/engine/WestlakeNavGraph.java`
- `shim/java/com/westlake/engine/WestlakeNode.java`
- `shim/java/com/westlake/engine/WestlakeLayout.java`
- `shim/java/com/westlake/engine/WestlakeView.java`
- `shim/java/com/westlake/engine/WestlakeRenderer.java`
- `shim/java/com/westlake/engine/WestlakeStubView.java`

**Audit each** for per-app branches; excise any that remain (some predate `feedback_no_per_app_hacks.md`).

### 1.7 Existing framework_duplicates infrastructure

- `framework_duplicates.txt` (in build scripts) — V2 extends; doesn't break

---

## 2. Files to TOUCH

### 2.1 `shim/java/com/westlake/services/WestlakeContextImpl.java`

**Change:** none to the surface. Already CR22-frozen.

If Step 4 (`WestlakeResources` rewrite) changes how `getResources()` is constructed, update only the **constructor wiring**, not the public API. Frozen surface is non-negotiable.

### 2.2 `shim/java/com/westlake/services/WestlakeResources.java`

**Change:** major rewrite (V2 Step 4).

**Delete:**
- M4-PRE12: `createSyntheticAssetManager` mValue/mOffsets plant (~33 LOC)
- M4-PRE13: `plantLocaleState` helper + Configuration plant (~58 LOC)
- M4-PRE14: `plantDisplayAdjustments` helper (~102 LOC)
- `buildReflective` body — replaced by direct-from-arsc path (~50 LOC)
- All `Unsafe.allocateInstance` calls on `AssetManager`, `ResourcesImpl`, `Configuration`, `DisplayAdjustments`

**Add:**
- Embed or `new` an instance of `WestlakeResourceArscParser` (new file, §3.1)
- `getString(int)`, `getInteger(int)`, `getBoolean(int)`, `getColor(int, Theme)`, `getDimension(int)`, `getDimensionPixelSize(int)`, `getDimensionPixelOffset(int)`, `getDrawable(int, Theme)`, `getStringArray(int)`, `getIntArray(int)`, `getXml(int)`, `getLayout(int)`, `getResourceName(int)`, `getResourceEntryName(int)`, `getIdentifier(name, type, pkg)`, `obtainTypedArray(int)` — all read directly from arsc + APK assets
- `getConfiguration()` — return a `Configuration` built via public no-arg ctor with locale/density/sw set from M4d DisplayManager
- `getDisplayMetrics()` — return `DisplayMetrics` populated from M4d
- `getAssets()` — return new `WestlakeAssetManager` thin wrapper (§3.2)

**Result:** ~600 LOC net (current 347 + arsc integration), down from ~540 with plants.

### 2.3 `shim/java/android/app/WestlakeActivityThread.java`

**Change:** V2 Step 6.

**Delete:**
- `buildBaseContext(packageName, classLoader)` legacy `new Context()` fallback (lines around 241 + 1273) — fallback path becomes dead code; remove
- Tolerant try/catch around 6-arg `activity.attach(...)` at line 3017 — unreachable in V2 because our `Activity.attach` is what's called
- `ContextWrapper.class.getDeclaredField("mBase")` reflective seed at line 3063 — unnecessary; our `Activity.attach(Context, ...)` sets mBase directly

**Keep:**
- `publishApplicationToBaseContext(...)` — Hilt `Contexts.getApplication(Context)` walk still resolves through this
- `sCurrentActivityThread` seed (per CR10 + M4-PRE11)

**Modify:**
- `attachActivity(...)` now calls our `WestlakeActivity.attach(Context base, Application app, Intent intent, ComponentName component, Instrumentation instr, Object hostActivity)` directly. The reflective fallback path is gone.

### 2.4 `shim/java/com/westlake/engine/WestlakeLauncher.java`

**Change:** further slim (continuation of CR14 + CR16).

**Delete (estimated ~1,500 LOC):**
- Per-app `appClass` fallbacks in `mainImpl` (lines 4638-4661) — V2's WestlakeActivity is generic
- `prefer WAT` list (lines 4702-4707) — V2 always uses WAT path
- Per-app `performResumeActivity` (lines 4841-4862) — V2 uses generic FragmentActivity lifecycle
- `launchMcdProfileControlledActivity` (lines 4692-4698) — McD-specific, delete
- `pendingDashboardClass` McD auto-launch (lines 4898-4899) — McD-specific, delete
- Counter SharedPreferences pre-seed (lines 4043-4060) — per-app, delete

**Keep:**
- Generic launch flow: parse args → init binder → register services → bind framework → launch APK → render loop
- Native JNI declarations (down to ~5 from 14)

### 2.5 `framework_duplicates.txt` (build-script-level, in `scripts/build-shim-dex.sh` or similar)

**Add to the strip-from-shim list — these classes ship from framework.jar today and need to ship from our shim post-V2:**

- `android.app.Application` — V2 Step 3
- `android.app.Activity` — V2 Step 2
- `android.view.Window` — V2 Step 5
- `com.android.internal.policy.PhoneWindow` — V2 Step 5
- `com.android.internal.policy.DecorView` — V2 Step 5
- `android.view.WindowManagerImpl` — V2 Step 5
- `android.view.WindowManagerGlobal` — V2 Step 5

**Remove from the strip list (so framework's stays and ours doesn't fight it) — these are V2's deletions:**

- `android.content.res.AssetManager` — we no longer ship our own AssetManager
- `android.content.res.Resources` — we no longer ship a class-replacing Resources (we use `WestlakeResources` namespaced under `com.westlake.services` and route via `WestlakeContextImpl.getResources()`)
- `android.content.res.ResourcesImpl` — we don't construct one
- `android.content.res.Configuration` — we use framework's public ctor

(Note: the existing CR23-fix entries — `android.content.Context` already in strip list because we provide `WestlakeContextImpl` — stay.)

---

## 3. Files to CREATE

### 3.1 `shim/java/com/westlake/services/WestlakeResourceArscParser.java` (~400 LOC, NEW)

AAPT2-format `resources.arsc` parser. Public API:

```java
class WestlakeResourceArscParser {
  WestlakeResourceArscParser(InputStream arsc);  // ~100 ms parse for typical APK

  // Lookup by resource ID (R.string.foo = 0x7f120042)
  String getString(int id);
  String getString(int id, Locale locale);  // for multi-locale arsc
  int getInteger(int id);
  boolean getBoolean(int id);
  int getColor(int id);             // returns 0xAARRGGBB
  float getDimension(int id, DisplayMetrics m);  // applies density
  int getDimensionPixelSize(int id, DisplayMetrics m);
  String[] getStringArray(int id);
  int[] getIntArray(int id);

  // Resource path → ID
  int getIdentifier(String name, String type, String pkg);
  // ID → resource name (for getResourceName)
  String getResourceName(int id);
  String getResourceEntryName(int id);

  // Drawable lookups return a resource entry that the caller (WestlakeResources)
  // can dispatch to WestlakeVector / WestlakeInflater / raw asset bytes
  ResourceEntry getDrawableEntry(int id);
  ResourceEntry getLayoutEntry(int id);
  ResourceEntry getXmlEntry(int id);
}
```

Format reference: AOSP `frameworks/base/tools/aapt2/format/binary/BinaryResourceParser.cc` is the canonical implementation. ~1500 LOC C++; our Java port is simpler because we only read, never write, and only need flat resource entries (no overlay reconciliation).

Test: build a synthetic arsc with 10 resources, verify lookup roundtrip.

### 3.2 `shim/java/com/westlake/services/WestlakeAssetManager.java` (~150 LOC, NEW)

Thin wrapper over the APK's flat resources:
- `open(String path) → InputStream`
- `openXmlResourceParser(int cookie, String fileName) → XmlResourceParser`
- `getLocales() → String[]`
- `getDisplayMetrics() → DisplayMetrics`
- `getConfiguration() → Configuration`

Implementation: zip read against the APK's bytes (we already have APK reading infrastructure in `WestlakeLauncher.readFileBytes`).

### 3.3 `shim/java/android/app/Activity.java` (~1,500 LOC, NEW — replaces framework version via shadow)

V2 Step 2. See §3.3 in V2 design doc.

Outline:
```java
package android.app;

public class Activity extends ContextThemeWrapper
                      implements LayoutInflater.Factory2,
                                 ViewModelStoreOwner,
                                 SavedStateRegistryOwner,
                                 ComponentCallbacks2 {
  // Field declarations matching framework's Activity (for instanceof + reflective field access)
  protected Application mApplication;
  protected Intent mIntent;
  protected ComponentName mComponent;
  protected ActivityInfo mActivityInfo;
  // ... ~30 more, all JVM-default null unless V2's attach() sets them

  // V2-owned attach surface
  public final void attach(Context base, Application app, Intent intent,
                           ComponentName component, Instrumentation instr,
                           Object hostActivity) { ... }

  // The 17-arg framework signature too, so any caller that has the args can use it
  public final void attach(Context, ActivityThread, Instrumentation, IBinder, int,
                           Application, Intent, ActivityInfo, CharSequence,
                           Activity, String, NonConfigurationInstances, Configuration,
                           String, IVoiceInteractor, Window, ActivityConfigCallback,
                           IBinder) { /* delegate to 6-arg attach */ }

  // ~80 public/protected methods (V2 §3.3 step 1 table)
  public Resources getResources() { return mBase.getResources(); }
  public Object getSystemService(String name) { return mBase.getSystemService(name); }
  public View findViewById(int id) { return mContentView.findViewById(id); }
  public void setContentView(int layoutResId) { ... }
  public void setContentView(View v) { mContentView = v; }
  public Intent getIntent() { return mIntent; }
  // ... etc
}
```

Field declarations are critical for the `super.mFoo` androidx access pattern (V2 §8.1 risk).

### 3.4 `shim/java/android/app/Application.java` (~250 LOC, NEW — replaces framework version via shadow)

V2 Step 3. Similar to Activity:

```java
public class Application extends ContextWrapper implements ComponentCallbacks2 {
  private final ArrayList<ActivityLifecycleCallbacks> mActivityLifecycleCallbacks = ...;
  // Field declarations matching framework
  private LoadedApk mLoadedApk;  // stays null in V2

  public void attachBaseContext(Context base) {
    super.attachBaseContext(base);  // ContextWrapper.attachBaseContext sets mBase
  }

  public void onCreate() {}  // user's subclass overrides
  public void onTerminate() {}
  public Context getApplicationContext() { return this; }
  // ... etc
}
```

### 3.5 `shim/java/android/view/Window.java`, `com/android/internal/policy/PhoneWindow.java`, `DecorView.java`, `android/view/WindowManagerImpl.java` (~600 LOC total, NEW)

V2 Step 5. Minimal stubs. See V2 §3.5.

### 3.6 `docs/engine/WESTLAKE_ACTIVITY_API.md` (NEW, ~200 LOC)

V2 Step 1 deliverable. Tabular categorization of every Activity method:
- column 1: method signature
- column 2: framework's body summary
- column 3: V2 disposition (Implement / ServiceMethodMissing / No-op)
- column 4: which test app's discovery surfaced it (noice / McD / both / neither yet)

Drives Step 2 implementation.

---

## 4. Files to DELETE

After V2 step 7-8 confirms no caller references:

### 4.1 `art-latest/stubs/assetmanager_jni_stub.cc` (825 LOC C++)

The 56 AssetManager natives become unreachable when we stop constructing framework `AssetManager`. Audit:
```bash
grep -rn "android.content.res.AssetManager" shim/java/
grep -rn "AssetManager_" art-latest/stubs/
```
If only references are inside `assetmanager_jni_stub.cc` itself + framework_duplicates strip list, delete file + remove from `Makefile.bionic-arm64`.

### 4.2 Build artifact: `aosp-shim.dex` (auto-rebuilt)

Will shrink by ~200 LOC (M4-PRE12/13/14 deletions) minus ~2,000 LOC additions (WestlakeActivity, WestlakeApplication, Window stubs, arsc parser). Net: ~+1,800 LOC + shim dex grows to ~1.7 MB (from 1.55 MB).

### 4.3 Any `Unsafe.allocateInstance` calls on framework classes

After V2, search:
```bash
grep -n "Unsafe.allocateInstance" shim/java/com/westlake/
grep -n "allocateInstance" shim/java/com/westlake/
```
Any call on a framework class (`android.app.*`, `android.content.res.*`, `android.view.*`) is a V1-drift artifact. Audit; replace with the corresponding shadow class's public ctor.

---

## 5. Verification before declaring V2 done

### 5.1 Regression must continue passing

```bash
bash scripts/binder-pivot-regression.sh --full
```

Required:
- `binder_smoke` (M1) PASS
- `sm_smoke` musl + bionic PASS
- `HelloBinder.dex` (M3) PASS
- `AsInterfaceTest.dex` (M3++) PASS
- 6 service tests (M4a/b/c/d/e/power) PASS
- `SystemServiceRouteTest.dex` PASS
- `noice-discover.sh` reach **at least PHASE G4** (current high-water; V2 should push it to "MainActivity.onCreate completion + first Hilt @Inject Binder transaction")
- `mcd-discover.sh` reach **at least PHASE G3** (current); V2 should push it to "SplashActivity.onCreate completion + first Hilt @Inject Binder transaction"

### 5.2 New V2 acceptance signals

Add to regression script:
- `Application.onCreate() executes to completion` — Hilt-injected fields populated
- `Activity.onCreate() executes to completion` — setContentView returns
- `findViewById(R.id.someView)` returns non-null on McD SplashActivity / noice MainActivity
- Real M4a Binder transaction surfaced from inside Hilt-injected user code (e.g., `getRunningAppProcesses` call observable in our M4a's onTransact log)

### 5.3 Codex 2nd-opinion

V2 Step 11. Run:
```bash
codex exec --dangerously-bypass-approvals-and-sandbox "review the V2 substrate diff for architectural drift; specifically check for: any new Unsafe.allocateInstance on framework classes; any new try/catch wrapping framework calls; any per-app branches; any reflective field plants. Refer to docs/engine/BINDER_PIVOT_DESIGN_V2.md §10 anti-patterns."
```

V2 is done only when codex returns "no architectural concerns."

---

## 6. Risk table for migration

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| `resources.arsc` parser bugs on real McD APK (177 MB, hundreds of resources) | MEDIUM | Layout inflation fails | Test against multiple APKs (Counter, noice, McD); cross-check `aapt dump` outputs |
| WestlakeActivity field declarations miss a field androidx uses via `super.mFoo` | MEDIUM | NoSuchFieldError on first androidx Fragment lifecycle call | V2 step 1 grep androidx-fragment.jar bytecode for `getfield android/app/Activity` |
| AppCompatDelegate path drives DecorView setup that our stub doesn't handle | MEDIUM-HIGH | Theme not applied; visual glitches | V2 step 8 + targeted AppCompat audit |
| Hilt component construction expects specific Application API surface | MEDIUM | DI graph fails | V2 step 8 noice + McD discovery surfaces this |
| Discoverability of "next thing to implement" degrades when WestlakeActivity ships | LOW | Slows V2 step 8 iteration | CR2 `ServiceMethodMissing.fail` plus a new `WestlakeActivityMethodMissing.fail` companion (TODO) |
| Some app uses framework `Activity.<class init>` static state we don't replicate | LOW | App crashes at class load | Inspect noice + McD class load for static-init reflection |

---

## 7. Person-day summary

Per V2 §7:

| Step | Person-days |
|---|---|
| 1. Define WestlakeActivity API | 1.0 |
| 2. Implement WestlakeActivity | 3.0 |
| 3. Implement WestlakeApplication | 1.0 |
| 4. Implement WestlakeResources thin (+ arsc parser) | 3.0 |
| 5. Window / PhoneWindow / DecorView stubs | 1.0 |
| 6. Wire WestlakeActivityThread.attachActivity | 1.0 |
| 7. Remove obsolete plants (M4-PRE12/13/14) | 1.0 |
| 8. Regression + discover verification | 1.0 |
| 9. (M6 surface daemon, separate) | — (not in V2 scope) |
| 10. Docs migration | 0.5 |
| 11. Codex review | 0.5 |
| **Total V2 substrate** | **13.0 person-days** |

Single agent at ~5h focused/day: ~10-13 calendar days.
Two parallel agents (Java vs Resources): ~6-8 calendar days.

---

**End of migration plan.**
