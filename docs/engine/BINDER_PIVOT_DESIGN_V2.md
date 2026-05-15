> SUPERSEDED 2026-05-14 by BINDER_PIVOT_ARCHITECTURE.md (merged consolidation). Kept for historical reference.

# Westlake Binder Pivot — Architectural Design V2

**Status:** Draft (2026-05-13) — CR28-architect; supersedes V1 for the in-process Java boundary
**Author:** Architect agent (read-only; no source touched)
**Supersedes:** `BINDER_PIVOT_DESIGN.md` (V1, 2026-05-12) — preserved verbatim for traceability
**Companion:** `MIGRATION_FROM_V1.md` (what to keep / remove from the current codebase)
**Related:** `PHASE_1_STATUS.md` (CR28-architect row added), `M4_DISCOVERY.md` §§34-36 + §49 (the drift this V2 corrects)

---

## 0. TL;DR for the next agent

V1 said "substitute at the Binder service boundary." That is **correct, and remains in force, for everything that crosses a service boundary** — i.e. between Java framework code and what used to be `system_server`.

V1 did **not** say what happens to AOSP framework code that runs *inside the calling process* and never crosses a Binder. The session 2026-05-12..05-13 discovered the hard way (M4-PRE12, M4-PRE13, M4-PRE14, CR23-fix, CR25-attempt) that **real AOSP `framework.jar` code, when run on `-Xbootclasspath`, expects a fully-bootstrapped `ActivityThread` / `LoadedApk` / `ContextImpl` / `Resources` / `AssetManager` / `Configuration` / `DisplayAdjustments` graph that only `system_server` builds at boot**. Plant one missing field, advance N dex bytecodes, hit the next NPE, plant another field — additive shimming, the very anti-pattern V1 §6.2 forbade, dressed up in service-substrate clothing.

V2's correction:

1. **Binder is still the substitution boundary** for cross-process service calls (M1-M4 stand; 6 services + ServiceManager + libbinder/servicemanager substrate is correct work).
2. **Inside dalvikvm**, the substitution boundary is the **`Activity.attach` cold-init seam**. Above that seam we run **Westlake-owned** lifecycle classes (`WestlakeActivity`, `WestlakeApplication`, `WestlakeResources` thin, `WestlakeContextImpl`) that the app's classes graft onto. Below that seam, AOSP `framework.jar` runs unmodified, calling our services via Binder.
3. **Generic, not per-app.** The deleted `WestlakeFragmentLifecycle` (3,087 LOC) and pre-CR14 `WestlakeView` work were per-app. The V2 substitute is *generic*: every app's `Application extends android.app.Application` and `MainActivity extends Activity` (transitively, via FragmentActivity/AppCompatActivity/ComponentActivity) plugs into the same `WestlakeApplication`/`WestlakeActivity` substrate.
4. **All M4-PRE12/13/14 field-plant code gets deleted.** The whole `AssetManager.mValue` / `Configuration.mLocaleList` / `DisplayAdjustments` cold-state plant family is the symptom of running framework.jar code that should never have run.

§3 below presents the substitution table; §4 picks where to cut; §5 says what survives; §6 says what gets removed; §7 lays out the implementation plan; §8 lists the still-open architectural questions.

---

## 1. Lessons from the drift

### 1.1 What V1 said and what we heard

V1 said *"AOSP framework.jar runs unmodified"*. The intended reading: ServiceManager.getService() and Stub.asInterface() and the marshaling parts of framework.jar run unmodified, because our libbinder + our service Stubs satisfy them.

What we actually did, starting around M4-PRE6: we put **real `framework.jar` on `-Xbootclasspath`** alongside `aosp-shim.dex`, then deployed `framework_duplicates.txt` (CR23-fix §49) to **strip the shim's `Context` / `Activity` / `ContextImpl` / `LoadedApk` / `Resources` / `AssetManager` / `Configuration` classes from aosp-shim.dex**, so the framework.jar copies win class resolution. The goal was "let AOSP's own framework classes run through to our Binder services."

The mistake: AOSP framework classes are not pure transport. `Activity.attach(...)` does ~120 lines of state setup using inputs (`mApplication`, `mResources`, `mWindow`, `mWindowManager`, `mInstrumentation`, `mUiThread`, `mMainThread`, `mActivityInfo`, `mIdent`, ...) that AOSP `ActivityThread.handleLaunchActivity` builds during process boot from `system_server`-supplied `IBinder` handles, `ActivityClientRecord`, `LoadedApk`, `CompatibilityInfoHolder`, etc. Without that bootstrap, every field is JVM-default null, and `Activity.attach`'s first reference dereferences null.

### 1.2 The drift in tabular form

| Milestone | Symptom | "Fix" applied | Architectural cost |
|---|---|---|---|
| M4-PRE12 (2026-05-12) | `AssetManager.getResourceText` → NPE on `outValue` (private final `mValue`) | Reflectively plant `mValue = new TypedValue()` and `mOffsets = new long[2]` on every synthetic AssetManager | +33 LOC reflective plant; advances ~200 dex bytecodes |
| M4-PRE13 (2026-05-12) | `Locale.toLanguageTag()` NPE because synthetic `Configuration.mLocaleList` is null | Reflectively plant `LocaleList(Locale.US)` via `Configuration.setLocales` + `mLocaleList` direct field; legacy `locale` too | +58 LOC reflective plant; advances ~100 dex bytecodes |
| M4-PRE14 (2026-05-12) | `DisplayAdjustments.getCompatibilityInfo()` NPE because synthetic `ResourcesImpl.mDisplayAdjustments` is null | Reflectively plant `new DisplayAdjustments()` (which initializes `mCompatInfo` to default) on `ResourcesImpl` | +102 LOC reflective plant; on-device verification blocked by unrelated SIGBUS |
| CR23-fix (2026-05-13) | `Activity.attach(6 args)` doesn't exist on framework's `Activity` (which has 17+ args); McD VM exits with NoSuchMethodError | Wrap 6-arg `activity.attach(...)` in `try { ... } catch (NoSuchMethodError | LinkageError)`; fall through to legacy field-set path | +95 LOC; works, but explicitly defers the 17-arg path "out of CR23-fix scope" |
| CR25-attempt (2026-05-13) | Activity → `Activity.setTheme` → `Window.setTheme(int)` on null `mWindow`; McD dashboard does not attach | (abandoned mid-investigation) — needed Window plumbing path, scope ballooning | n/a; user flagged the drift here |

### 1.3 What V1 §6.2 actually meant

V1 §6.2: *"Don't observe an NPE, add a shim, move on."* M4-PRE12/13/14 each observe an NPE on a real AOSP-framework cold-init field and respond with `setField(obj, "mFoo", new Foo())`. **That is additive shimming.** The fact that the shim is "AOSP framework field plant" instead of "Westlake framework class" doesn't change what it is — the unbounded chase of `system_server`'s ~100-field boot graph, piecemeal.

V1 §6.5: *"Don't implement service methods we haven't observed being called."* We extended this implicitly to "don't plant fields we haven't observed being read" — but the observation set grew faster than we could plant.

### 1.4 Why "plant the missing fields" is unbounded

AOSP's process bootstrap (`ActivityThread.handleBindApplication`, `LoadedApk.makeApplication`, `ContextImpl.createAppContext`, `Resources.updateConfiguration`, etc.) populates approximately:

- `ActivityThread`: ~40 instance fields, set during `attach()` (the AT one, not Activity), `handleBindApplication`, `handleSetContentCaptureOptionsCallback`, ...
- `LoadedApk`: ~25 fields, populated from `system_server`-pushed `ApplicationInfo`, `ActivityClientRecord`, etc.
- `ContextImpl`: ~30 fields, built via 12-arg internal ctor that takes a `LoadedApk` + a `Resources` + a Display, plus seven flags.
- `Resources` / `ResourcesImpl`: `mAssets`, `mDisplayAdjustments`, `mCompatibilityInfo`, `mConfiguration`, `mMetrics`, `mAccessLock`, `mLocaleAdjustments`, ...
- `Configuration`: `mLocaleList`, `locale`, `mLocale`, `screenLayout`, `densityDpi`, `screenWidthDp`, `screenHeightDp`, `smallestScreenWidthDp`, `uiMode`, `orientation`, ...
- `AssetManager`: `mValue` (private final TypedValue), `mOffsets` (private final long[2]), native handle, theme attribute cache, ...
- `DisplayAdjustments`: `mCompatInfo`, `mFixedRotationAdjustments` (post-API 30), ...
- `Activity` (during `attach`): `mMainThread`, `mApplication`, `mIntent`, `mReferrer`, `mComponent`, `mActivityInfo`, `mTitle`, `mParent`, `mEmbeddedID`, `mLastNonConfigurationInstances`, `mWindow` (a real `PhoneWindow`), `mWindowManager` (a real `WindowManagerImpl`), `mCurrentConfig`, `mWindowAdded`, ...
- `PhoneWindow` (subclass of `Window`): `mDecor` (a real `DecorView`), `mLayoutInflater`, `mContext`, `mForcedWindowFlags`, `mWindowStyle` (a `TypedArray` resolved from `R.styleable.Window`), ...

That's well over 200 fields. Each transition from "synthetic instance via `Unsafe.allocateInstance`" to "the next AOSP method actually called" surfaces a new field. M4-PRE12/13/14 covered three. There is no architectural reason to expect the next 200 to converge faster than the first three. The drift is mechanical and predictable.

### 1.5 The corrected architectural fact

**Real AOSP `framework.jar` code is going to NPE on cold-boot state. Either we plant the state (drift) or we don't run that code.**

V2 picks: **don't run that code.** Specifically: don't run `Activity.attach`, `Activity.attachBaseContext` (the framework one), `Window.<init>`, `PhoneWindow.<init>`, `Resources.updateConfiguration`, or any framework path that touches the `ActivityThread`-built bootstrap graph.

We will run, in-process and unmodified from `framework.jar`:

- `android.view.View` and subclasses (with the caveat in §3 about `mAttachInfo`)
- `android.view.ViewGroup`
- `android.widget.*` (TextView, Button, ImageView — pure-ish leaf widgets)
- `android.os.Handler`, `Looper`, `MessageQueue` (already de-risked by M4-PRE4 message queue JNI)
- `android.os.Bundle`, `android.os.Parcel` (data classes; no service deps)
- `android.util.*` (Log, TypedValue, ...)
- `android.graphics.*` data classes
- the Binder marshaling layer (`Stub` / `Stub.asInterface` / `Parcel.writeStrongBinder` / `BinderProxy`)
- the autogenerated `IXxxManager.Stub` and `IXxxManager.Stub.Proxy` classes that our Westlake services extend

We will **not** run, from `framework.jar`:

- `Activity.attach` and subclasses (`FragmentActivity`, `AppCompatActivity`, `ComponentActivity`)
- `Application.<init>` / `Application.attach` / `Application.attachBaseContext` (the framework versions; the *app's* `Application` subclass runs after our seeding)
- `ActivityThread` body (we have `WestlakeActivityThread` shim that satisfies callers needing `currentActivityThread()`)
- `LoadedApk` body (synthetic instance via `WestlakeLoadedApk`)
- `ContextImpl.createAppContext` / `createActivityContext` (replaced by `WestlakeContextImpl`)
- `Resources.updateConfiguration` body (we own a thin `WestlakeResources`)
- `PhoneWindow.<init>` and `Window.setTheme` (no Window machinery; see §3)

### 1.6 Subtraction reminder

V1 §2.4 codified subtraction. M4-PRE12/13/14 violated it. **V2 mandates the inverse check on every future change**: before adding a plant, deletion, or shim, ask "from the working baseline (`HelloBinder` + `AsInterfaceTest` + the 4 service tests + `SystemServiceRouteTest`), what is the *smallest* change that lets noice/McD reach the next acceptance event?" If the answer is "plant another framework cold-init field," reject the change and pick the V2 substitute instead.

---

## 2. Two failed approaches and their costs

### 2.1 Approach (a) — Plant every missing framework field

**Mechanism:** Run real AOSP `framework.jar` on BCP. When framework code NPEs on a null field of an `Unsafe.allocateInstance`'d object, reflectively set the field. Repeat.

**Status as of 2026-05-13:** abandoned mid-drift. Covered ~3 fields (M4-PRE12, 13, 14). Estimated >200 fields remain. Each plant costs ~50-100 LOC + a discovery cycle. Linear-in-fields cost, unbounded scope.

**Cost paid so far:**
- ~190 LOC of reflective plant code in `WestlakeResources.java` (33 + 58 + 102) — soon-to-be-deleted
- ~95 LOC of `try/catch` workarounds in `WestlakeActivityThread.java` (CR23-fix)
- 2-3 person-days of investigation that produced no architectural progress

**Why it can't converge:** framework.jar's bootstrap graph is irreducibly system_server's; the only way to satisfy it is to *be* system_server.

### 2.2 Approach (b) — Per-app shims (deleted in C1, CR14, CR16)

**Mechanism:** `WestlakeFragmentLifecycle` (3,087 LOC) reflectively drove FragmentManager for each app via per-app hooks (`MCD_SIMPLE_PRODUCT_HOLDER_ID`, `MCD_ORDER_PDP_FRAGMENT`, etc.). `DexLambdaScanner` (~600 LOC) parsed dex bytecode for Lazy<T> initializers per app. `MCD_*` constants in `FragmentTransactionImpl` and `FragmentManager`. Several `if (packageName.equals("com.mcdonalds.app"))` branches in `WestlakeLauncher`.

**Status as of 2026-05-13:** **deleted** in C1 (Cleanup 1, ~4,000 LOC), CR14 (-9,455 LOC), CR16 (-860 LOC). Cumulative `WestlakeLauncher` slim: 22,983 → 12,668 LOC (-44.9%). Per the `feedback_no_per_app_hacks.md` rule.

**Why deletion was correct:** each per-app hack encoded knowledge that the user could deploy any new APK and break. Linear-in-APKs cost.

**The trap:** approach (a) and approach (b) are both responses to the **same architectural fact** — real `framework.jar` code paths don't run cleanly without `system_server` bootstrap.

- (a) tries to bootstrap a fragment of system_server, piecemeal, unbounded
- (b) bypasses the broken framework path with reflection that knows the app's internal IDs, unbounded in app count

Neither converges. The V2 substitute (Westlake-owned Application/Activity lifecycle) is **generic** (every app's superclass chain hits it identically) and **finite** (the surface is what AOSP's `Activity` / `Application` *publicly export*, not what their bootstraps internally consume).

### 2.3 The pattern recognition

Whenever we feel ourselves writing one of these:

- `setField(syntheticObj, "mWhatever", reflectivelyConstructed)`
- `try { realFrameworkMethod(...); } catch (NoSuchMethodError | NPE) { fallback(...) }`
- `if (packageName.equals("com.foo.bar")) { specialCase(...) }`
- `if (resourceId == 0x7f0b1234) { hardcodedAnswer() }`

…we are in one of the two failed approaches. Stop and ask: which `framework.jar` code path are we trying to keep alive, and **could we replace the caller with a Westlake-owned class that doesn't go through that path?**

---

## 3. The substitution boundary (corrected)

This is the central V2 table. Each row says where the substitution lives, and the **DECIDE** rows present 2-3 trade-off options and a recommendation.

### 3.1 The big table

| # | Layer | Provider | Location | Status |
|---|---|---|---|---|
| 1 | Kernel binder (`/dev/binder`, `/dev/vndbinder`) | Linux upstream | kernel | OnePlus 6 ships it; OHOS port = Phase 2 |
| 2 | `libbinder.so` (Parcel, BBinder, BpBinder, IPCThreadState) | Westlake (AOSP source, musl/bionic rebuild) | dalvikvm process (`libbinder_full_static.a`) | **Done (M1)** |
| 3 | `servicemanager` | Westlake (AOSP source, musl/bionic rebuild) | separate process | **Done (M2)** |
| 4 | `ServiceManager.java` + `BinderProxy` JNI cluster | Westlake (rewrite of AOSP) | `shim/java/android/os/ServiceManager.java` + `art-latest/stubs/binder_jni_stub.cc` | **Done (M3 + M3++)** |
| 5 | 6 framework system services (Activity, Power, Window, Display, Notification, InputMethod) | Westlake `IXxxService.Stub` subclasses | `shim/java/com/westlake/services/Westlake*Service.java` | **Done (M4a-e + M4-power)** |
| 6 | Local `PackageManager` (Context.getPackageManager()) | Westlake `WestlakePackageManagerStub` | `shim/java/com/westlake/services/WestlakePackageManagerStub.java` | **Done (M4-PRE5, CR19 fail-loud)** |
| 7 | `Context.getSystemService("name")` routing | `WestlakeContextImpl` → `SystemServiceWrapperRegistry` → `ServiceManager.getService` → autogen `IXxx.Stub.asInterface` → our service object | **Done (CR3/CR4/CR5)** |
| 8 | `Context` class — instance for `attachBaseContext` chain | `WestlakeContextImpl` (frozen surface per CR22) | `shim/java/com/westlake/services/WestlakeContextImpl.java` (714 LOC) | **Done; surface frozen** |
| 9 | `Application` lifecycle | **DECIDE — §3.2** | | |
| 10 | `Activity` lifecycle | **DECIDE — §3.3** | | |
| 11 | `Resources` / `AssetManager` / `Configuration` / `Theme` | **DECIDE — §3.4** | | |
| 12 | `Window` / `PhoneWindow` / `DecorView` / `WindowManager` | **DECIDE — §3.5** | | |
| 13 | `View` / `ViewGroup` / measure / layout / draw | Real AOSP `framework.jar` (mostly stateless tree code) | in-process | Mostly works as-is; one caveat in §3.6 |
| 14 | Fragment / FragmentManager / FragmentTransaction | **DECIDE — §3.7** | | |
| 15 | Surface / Canvas / display output | Westlake M6 surface daemon | separate process (Phase 1: SurfaceView pipe on Android; Phase 2: XComponent on OHOS) | M6 not started |
| 16 | Touch / input events | Westlake input bridge to display daemon | dalvikvm consumer, daemon producer | Not designed yet (see §8) |
| 17 | Audio | Westlake M5 audio daemon | separate process (M5-PRE stubs landed) | M5 not started |

Rows 1-8 and 13 are settled. Row 15-17 are M5/M6 work (post-V2). Rows 9-12 and 14 are the substantive V2 design decisions; they are presented next.

### 3.2 DECIDE row 9: `Application` lifecycle

The app provides an `Application` subclass (e.g., `noice.MainApplication extends DaggerApplication extends Application`, or `com.mcdonalds.MCDApplication extends Hilt_MCDApplication extends Application`). It expects:

- `Application.<init>` runs (concretely Hilt's `Hilt_*Application.<init>` runs Dagger component-builder init)
- `Application.attachBaseContext(Context)` is called (Hilt apps override this to build the component)
- `Application.onCreate()` is called (Hilt-injected fields are now valid; user code runs)
- `getApplicationContext()` returns the Application itself
- `getBaseContext()` returns the base Context (our `WestlakeContextImpl`)

We have three options:

#### Option 9-A: Real framework `Application` superclass (current; the drift path)

Let the app's `Application` chain run with `android.app.Application` from `framework.jar` as the root. We seed the base Context, call `attach(Context)` reflectively (it's `@hide` but accessible), call `attachBaseContext`, then `onCreate`.

**Pros:**
- App's Hilt-generated `Hilt_*Application` works without modification
- App's `super.attachBaseContext(base)` chain naturally terminates in framework `ContextWrapper.attachBaseContext` (which just sets `mBase`)
- App can call `getResources()`, `getPackageName()`, `getApplicationInfo()`, etc., on `this` and they reach our base Context via `ContextWrapper` delegation

**Cons:**
- `Application.attach(Context)` body in framework.jar does:
  ```java
  attachBaseContext(context);
  mLoadedApk = ContextImpl.getImpl(context).mPackageInfo;
  ```
  The second line requires our base Context to be a real `ContextImpl` (it isn't — it's `WestlakeContextImpl`, deliberately not extending the cold-init machinery).
- If we don't call `Application.attach` and instead just call `attachBaseContext` directly, we leave `mLoadedApk` null, which most apps don't reference *but* framework code that calls `Application.getPackageManager()` does (via `mLoadedApk.getPackageManager()`).

**Cost:** ~20-40 LOC of seeding + the `mLoadedApk` plant problem.

#### Option 9-B: `WestlakeApplication` substitutes the framework `Application`

Define `class WestlakeApplication extends android.app.Application` in our shim that overrides every method the app actually calls. The app's `Application` superclass chain still terminates at framework's `Application`, but our `WestlakeApplication` is inserted *between* the app's class and framework's — by editing the inheritance chain at install time (dex rewriting).

**Pros:** complete control over `Application` surface; no framework cold-init issues.

**Cons:**
- Dex rewriting at install time is invasive; we currently don't do it.
- Hilt-generated classes use `super.attachBaseContext` / `super.onCreate` reflectively; rewriting can break this.

**Cost:** dex-rewriter infrastructure (~2-4 person-days) + ongoing fragility.

#### Option 9-C: Override framework `Application` via `framework_duplicates.txt`

Add `android.app.Application` to `framework_duplicates.txt` (CR23-fix's tool), then ship our own `android.app.Application` class inside `aosp-shim.dex` that has the same public/protected surface as framework's but a sane implementation. The app's `extends Application` resolves to our class. Hilt's `Hilt_*Application extends Application` also resolves to our class.

**Pros:**
- No dex rewriting; the existing classpath shadow mechanism does the work.
- We define exactly what `Application.attach`, `Application.attachBaseContext`, `Application.onCreate` do.
- Hilt's `super.attachBaseContext` reaches our class, which does the right thing (just set `mBase`).
- Same trick we already use for `Context`, `Activity` (per CR23-fix table) — minimal new infrastructure.

**Cons:**
- We must keep our `Application` shim's `public`/`protected` API surface in lock-step with framework's, across API levels. (Mitigated: the public API is stable since API 14.)
- App-instance fields declared in framework's Application (e.g., `mLoadedApk`) are now declared in *our* Application; framework code that reflectively reads `Application.mLoadedApk` would get our value. (Mitigated: rare reflection target.)

**Cost:** ~150-300 LOC of `Application` shim; one line in `framework_duplicates.txt`; ~0.5 person-day.

**Recommendation: Option 9-C.** Same shadow mechanism already proven for `Context`/`Activity`. Smallest delta from current state. Avoids the dex-rewriter rabbit hole.

### 3.3 DECIDE row 10: `Activity` lifecycle

The app provides an `Activity` (concretely a deep subclass: `MainActivity extends AppCompatActivity extends FragmentActivity extends ComponentActivity extends Activity`). After CR23-fix the immediate problem is `Activity.attach(...)` has the 17+-arg framework signature and we call it with 6 args; the fallback wraps it in try/catch.

The deeper problem is even if we call the 17-arg version, framework `Activity.attach` will NPE on `mApplication.getApplicationInfo()`, then on `mWindow` machinery, then on `mActivityInfo.windowConfiguration`, etc.

Three options:

#### Option 10-A: Plant the 17 arguments to real `Activity.attach`

Build real or fake versions of: `Context base`, `ActivityThread aThread`, `Instrumentation instr`, `IBinder token`, `int ident`, `Application application`, `Intent intent`, `ActivityInfo info`, `CharSequence title`, `Activity parent`, `String id`, `NonConfigurationInstances lastNonConfigurationInstances`, `Configuration config`, `String referrer`, `IVoiceInteractor voiceInteractor`, `Window window`, `ActivityConfigCallback activityConfigCallback`. (Plus signature variants per API level.)

**Pros:** Real `Activity.attach` runs; everything `Activity.getXxx()` returns is what framework decides; nothing for us to track.

**Cons:**
- 17 inputs, several of which (`Window`, `ActivityConfigCallback`, `IVoiceInteractor`) are themselves cold-init nightmares.
- This is the M4-PRE-style drift, scaled up.
- Even if we plant all 17, `Activity.attach` body does ~120 lines of setup that will hit *internal* state from `aThread` and `application` — we'd then plant fields on those too. We've already started doing this and it's the path we're abandoning.

**Cost:** unbounded; explicitly rejected by V2.

#### Option 10-B: `WestlakeActivity` shadows framework `Activity` via classpath shadow

Same trick as 9-C for `Application`. `android.app.Activity` is added to `framework_duplicates.txt`; our `aosp-shim.dex` defines `android.app.Activity` with a Westlake-controlled body.

App's `MainActivity extends AppCompatActivity extends FragmentActivity extends ComponentActivity extends Activity` resolves `Activity` to our class. AppCompatActivity / FragmentActivity / ComponentActivity stay as framework.jar's versions (they call protected `Activity` API; if our `Activity` provides correct API, they work).

Our `Activity` provides:
- `attach(...)` — accepts the 6-arg or 17-arg form; stores `mApplication`, `mBase`, `mIntent`, `mComponentName`, `mInstrumentation`, `mResources`, `mWindow=null` (see 12), `mWindowManager=null` (see 12), nothing more.
- `attachBaseContext(Context)` — sets `mBase`.
- `getApplicationContext()` — returns `mApplication`.
- `getBaseContext()` — returns `mBase`.
- `getResources()` — returns `mBase.getResources()` (our WestlakeResources).
- `getSystemService(String)` — delegates to `mBase` (which is WestlakeContextImpl, which routes through binder).
- `setContentView(int layoutId)` — inflates layoutId via WestlakeLayoutInflater into our (created here) root `FrameLayout`; sets `mContentView`.
- `setContentView(View)` — sets `mContentView = view`.
- `findViewById(int)` — searches `mContentView`.
- `getIntent()` — returns `mIntent`.
- `getMenuInflater()`, `setTheme(int)` — minimal real impl (Theme: see 11).
- `getFragmentManager()` / `getSupportFragmentManager()` — see row 14.
- `onCreate(Bundle)` etc. — empty (user's subclass overrides).
- Lifecycle drivers `performCreate`, `performStart`, `performResume`, `performPause`, `performStop`, `performDestroy` — call user's overrides plus the FragmentActivity/AppCompatActivity dispatch chains.

**Pros:**
- Generic: every app's Activity chain hits our class identically.
- Finite: surface is what `Activity`'s public/protected API is — ~200 methods, all documented, stable since API 14, no `mLoadedApk`-style internals.
- FragmentActivity / AppCompatActivity / ComponentActivity (framework.jar versions) continue to work because they only call public/protected `Activity` API.
- No cold-init field cascade.
- Same `framework_duplicates.txt` mechanism we already operate.

**Cons:**
- `Activity` is ~7,500 LOC in AOSP. We'd ship a slimmer version (~1,500 LOC); some niche methods will throw `ServiceMethodMissing` until first app hits them.
- App's `super.onCreate(bundle)` (called from MainActivity through Activity chain) reaches our Activity.onCreate which is empty — fine, matches framework.
- `instanceof Activity` checks pass (we ARE `android.app.Activity`).

**Cost:** ~1,500 LOC `WestlakeActivity` (slimmed AOSP Activity); ~1 line in `framework_duplicates.txt`; ~3-4 person-days first draft + incremental.

#### Option 10-C: Westlake-owned `WestlakeActivity` that the **app** explicitly extends

App rewrite (or per-app wrapper): `MainActivity extends WestlakeActivity` instead of `extends AppCompatActivity`. Per-APK rewrite at install or build time.

**Pros:** complete control, no shadowing trickery.

**Cons:** per-app, requires bytecode rewriting of every Activity, violates "AOSP framework.jar runs unmodified" goal.

**Cost:** infeasible for closed-source APKs.

**Recommendation: Option 10-B.** Same shadow mechanism. Generic. Finite. Already proven for the Context/Activity classes today in `framework_duplicates.txt` (CR23-fix shipped a partial version of this for `Context`; we extend the pattern to `Activity`).

The deleted pre-CR14 `WestlakeView` and `WestlakeFragmentLifecycle` work was *per-app* shadowing logic implemented via reflection. The V2 `WestlakeActivity` is **generic** shadowing implemented via a single classpath-priority swap. Same idea; different implementation; correct scope.

### 3.4 DECIDE row 11: `Resources` / `AssetManager` / `Configuration` / `Theme`

Today (post-M4-PRE12/13/14): `WestlakeResources` builds a synthetic `Resources` + `ResourcesImpl` + `AssetManager` + `Configuration` + `DisplayAdjustments` graph by `Unsafe.allocateInstance` plus reflective field plant. The plants are growing.

Three options:

#### Option 11-A: Keep planting (current; drift)

Continue M4-PRE15, M4-PRE16, ... adding plants for each new framework Resources field accessed. Already shown unbounded.

#### Option 11-B: WestlakeResources is the public `Resources` surface, no AOSP machinery

Our `WestlakeResources` (~350 LOC today, projected ~600 after V2) **extends framework `Resources` only nominally** (so `instanceof Resources` works) and **overrides every public method**:

- `getString(int)`, `getStringArray(int)`, `getIntArray(int)`, `getInteger(int)`, `getBoolean(int)`, `getColor(int, Theme)`, `getDimension(int)`, `getDimensionPixelSize(int)`, `getDimensionPixelOffset(int)`, `getDrawable(int, Theme)`, `getXml(int)`, `getLayout(int)`, `obtainTypedArray(int)`, `getResourceName(int)`, `getResourceEntryName(int)`, `getIdentifier(...)`, ... — each reads directly from a `ResourceTable` we build at Application boot from the APK's `resources.arsc`.

- `getConfiguration()` — returns a `Configuration` we construct (not the same `Configuration` framework was about to mutate; ours is read-only).
- `getDisplayMetrics()` — returns a `DisplayMetrics` we set from M4d display service.
- `getAssets()` — returns an `AssetManager` we construct (also no framework machinery; just a thin wrapper over `apk.openAsset(path)`).

No `ResourcesImpl`. No `mAssets` native handle. No `Configuration.setTo` update path. No `DisplayAdjustments`.

`Theme` (returned from `newTheme()` / `obtainTheme(int)`) is also Westlake-owned, providing `obtainStyledAttributes(AttributeSet, int[], int, int)` which is the one tricky surface — it needs to honor `?attr/` and `?android:attr/` references during layout inflation.

**Pros:**
- Finite: surface is `Resources`'s public methods (~80) + `Theme`'s public methods (~10) + `AssetManager`'s public methods (~30). All documented and stable.
- No reflective field plants on framework objects.
- M4-PRE12 / M4-PRE13 / M4-PRE14 all evaporate.
- We parse `resources.arsc` once at Application boot; lookups are O(1) hash, no native AssetManager required.

**Cons:**
- `resources.arsc` parser must be correct (it's the AAPT2 format; well-documented; ~500 LOC of pure Java).
- XML drawables (vector, layer-list) must be parsed by our code, not framework's; we already have `WestlakeVector.java`, `WestlakeInflater.java`, `WestlakeLayoutInflater.java`. They predate the drift and **survive V2**.
- `Theme.obtainStyledAttributes` is non-trivial when style stacking is involved (theme overlays, parent themes). We'd ship a baseline that handles simple cases (read style[attr], fallback to defStyleAttr, fallback to defStyleRes) and document the gap.

**Cost:** ~400 LOC `resources.arsc` parser; ~600 LOC `WestlakeResources` rewrite/expansion; ~200 LOC `WestlakeTheme` (today `WestlakeTheme.java` exists; extend); 0 LOC reflective plants. Net code: +0 to +400 LOC over current (today's M4-PRE12/13/14 plants are deleted; new ARSc parser is the addition).

#### Option 11-C: Use AAPT-time precomputed Java resource constants

For each layout/string/dimen, AAPT generates `R.java` constants (`R.string.app_name = 0x7f120042`, etc.). We could **regenerate** that mapping at boot, build a Java HashMap, and have `getString` etc. look up by int.

This is what Option 11-B does internally. They're not separate options; 11-C is just one implementation strategy inside 11-B.

**Recommendation: Option 11-B.** Removes the unbounded plant chase. Same surface every app uses. We already own `WestlakeVector` / `WestlakeInflater` / `WestlakeLayoutInflater` / `WestlakeTheme` from pre-pivot — extend them; don't reach into framework's Resources internals.

### 3.5 DECIDE row 12: `Window` / `PhoneWindow` / `DecorView` / `WindowManager`

`Activity.setContentView(int)` in real Android does: `getWindow().setContentView(int)` → `PhoneWindow.installDecor()` → `PhoneWindow.generateDecor(...)` constructs a `DecorView` which is a `FrameLayout` subclass with title bar / action bar / system insets / status bar / nav bar handling, then inflates the layout into the decor's content frame.

We don't render an OS chrome. We don't have a status bar, action bar, or system insets. The "window" is just the drawable surface area.

Three options:

#### Option 12-A: Provide `Window` / `PhoneWindow` / `DecorView` stubs

Westlake-owned classes, classpath-shadowed via `framework_duplicates.txt`:

- `Window`: empty abstract base; `getDecorView()` returns the content view; `setContentView(view)` sets it; `setTheme(int)` is a no-op.
- `PhoneWindow extends Window`: no chrome; `installDecor` just stores the inflated content view.
- `DecorView extends FrameLayout`: empty; just a `FrameLayout`.
- `WindowManager`: stub; `addView(view, params)` is the surface daemon submit.

**Pros:** straightforward; no framework cold-init.

**Cons:** AppCompatActivity does some `PhoneWindow`-style work for action bars; we'd need to provide a stub action bar (visible or no-op). Manageable.

#### Option 12-B: No Window class at all; Activity has `mContentView` directly

`WestlakeActivity.setContentView(int)` inflates directly into a `FrameLayout` it owns. `getWindow()` returns null or a minimal stub that just delegates `getDecorView()` to `mContentView`.

**Pros:** smaller surface.

**Cons:** AppCompatActivity reaches `getWindow()`. Returning null breaks it. Stub still needed.

#### Option 12-C: Real framework `PhoneWindow` (drift path)

Constructs a `DecorView` which constructs a `WindowManagerImpl` which talks to `WindowManagerService` (our M4b binder service, fine) — but `PhoneWindow.<init>` reads ~30 fields from its `Context`, expecting a real `ContextImpl`.

Same NPE cascade as Activity.attach. Same drift path. Rejected.

**Recommendation: Option 12-A.** Minimal `Window` / `PhoneWindow` / `DecorView` / `WindowManagerImpl` stubs. Action bar and title are no-ops. We already have `WestlakeStubView` and pre-CR14 stub patterns; extend the pattern. Same `framework_duplicates.txt` shadow.

### 3.6 Row 13 caveat: real `View` / `ViewGroup` from framework.jar

`android.view.View` has a `mAttachInfo` field that's populated by `ViewRootImpl` when the View is attached to a Window. Many View methods early-return or behave differently when `mAttachInfo == null`. **This is in our favor** — it's the framework's own "I'm not attached yet" sentinel. Our Westlake Activity / Window doesn't attach a `ViewRootImpl`; `mAttachInfo` stays null; View code that depends on it short-circuits cleanly.

The one wrinkle: `View.invalidate()` walks up the parent chain and eventually calls `ViewRootImpl.invalidateChild(this, dirty)`. If `mAttachInfo == null`, the upward walk stops. Our render loop polls the View tree at frame cadence rather than reacting to invalidate() — already the M6 design.

So real framework `View` / `ViewGroup` / `TextView` / `Button` / `ImageView` / `RecyclerView` (from androidx) work in-process with no plants. **Confirmed: drift was *not* in the View tree layer.**

### 3.7 DECIDE row 14: Fragment / FragmentManager / FragmentTransaction

These come from `androidx.fragment` (no longer in framework.jar since API 28 took the framework Fragment out; modern apps use androidx). They expect:

- `FragmentActivity.getSupportFragmentManager()` returns a `FragmentManager`
- `FragmentManager` calls `getActivity()`, `getApplicationContext()`, `getResources()` — all delegated to our `Activity`
- `FragmentTransaction` calls `findViewById(containerId)`, `addView(fragmentView)`, `removeView(...)` — all on our content view tree

We deleted `WestlakeFragmentLifecycle` for being a per-app reflective driver. The V2 substitute is:

**Run real `androidx.fragment.app.FragmentActivity.FragmentController` / `FragmentManagerImpl`.** These are app-classpath classes (in androidx, not framework.jar), they don't go through `ActivityThread`, and they only call API surface that our `WestlakeActivity` provides. **Provided WestlakeActivity is correct, FragmentManager works unmodified, generically.**

**This is the architectural payoff for getting WestlakeActivity right.** Once `getActivity()` returns a proper `WestlakeActivity` with `findViewById`, `getResources`, `getSystemService`, etc., the entire androidx.fragment graph works without per-app code. The 3,087 LOC of `WestlakeFragmentLifecycle` we deleted **was solving a problem that doesn't exist when WestlakeActivity is correct.**

**Recommendation: Run androidx FragmentManager unmodified, on top of WestlakeActivity.** No Westlake-Fragment substitute needed. This is the **generic** version of the work we deleted (pre-C1 per-app version).

### 3.8 Summary of §3 decisions

| Row | Layer | Decision |
|---|---|---|
| 9 | Application lifecycle | 9-C: shadow `android.app.Application` via framework_duplicates.txt |
| 10 | Activity lifecycle | 10-B: shadow `android.app.Activity` via framework_duplicates.txt; `WestlakeActivity` is the generic substrate |
| 11 | Resources / AssetManager / Configuration / Theme | 11-B: WestlakeResources owns the surface, reads `resources.arsc` directly, no framework Resources/ResourcesImpl/AssetManager machinery |
| 12 | Window / PhoneWindow / DecorView | 12-A: shadow + stub all four; no chrome, no action bar |
| 14 | Fragment / FragmentManager | androidx unmodified; works generically once 10 + 11 + 12 are correct |
| (existing) | Binder + 6 services + ServiceManager + Context.getSystemService routing | unchanged from V1 / current M3-M4 work |

---

## 4. Recommendation: where to cut

**The cut is at `android.app.Activity` (and its peer classes `Application`, `Resources`, `Window`).** Real `framework.jar` code runs **up to** the point where the app's Application/Activity constructor exits. **Westlake-owned classpath-shadowed versions of `Application`, `Activity`, `Resources`, `AssetManager`, `Configuration`, `Theme`, `Window`, `PhoneWindow`, `DecorView`, `WindowManagerImpl` take over from there.** Real `framework.jar` code resumes inside `View`, `ViewGroup`, widget classes, Handler/Looper, Binder marshaling, and the autogen `IXxxManager.Stub` proxies — all of which run cleanly in-process because they don't expect `system_server` bootstrap state.

The architectural payoff:
- **App's `Application.onCreate` body runs**, including Hilt-injected fields, which fire real Binder transactions into our M4 services. That's the V1 payoff preserved.
- **App's `Activity.onCreate` body runs**, including `setContentView(int)` which inflates real layouts via our `WestlakeLayoutInflater` (already pre-existed; not the pre-pivot per-app inflate logic — the generic one).
- **Real androidx Fragment lifecycle runs**, because our Activity satisfies the FragmentManager's contract. The 3,087 LOC of `WestlakeFragmentLifecycle` we deleted is **not replaced** — it was solving a problem we no longer create.
- **Real `View` / `RecyclerView` / `ConstraintLayout` measure/layout/draw runs**, drawing into a `Canvas` whose `Bitmap` is the surface daemon's framebuffer.

Per-app shimming becomes structurally impossible: the surface every app touches is our `WestlakeActivity` / `WestlakeApplication` / `WestlakeResources`, generic across all APKs.

---

## 5. What survives from this session's work

| Component | Status post-V2 | Notes |
|---|---|---|
| M1 — Westlake `libbinder.so` (musl + bionic) | **survives unchanged** | M1 is the right boundary. Patches 0003 (kHeader send) + 0004 (kHeader accept) stay. |
| M2 — Westlake `servicemanager` (musl + bionic) | **survives unchanged** | |
| M3 — `ServiceManager.java` + 7 ServiceManager natives + JavaBBinder | **survives unchanged** | |
| M3++ — same-process `Stub.asInterface` elision (`nativeGetLocalService`) | **survives unchanged** | The single most important V1 optimization. |
| M4a — `WestlakeActivityManagerService` | **survives unchanged** | |
| M4b — `WestlakeWindowManagerService` | **survives unchanged** | |
| M4c — `WestlakePackageManagerService` (binder side) | **survives unchanged** | |
| M4d — `WestlakeDisplayManagerService` | **survives unchanged** | |
| M4e — `WestlakeNotificationManagerService` + `WestlakeInputMethodManagerService` | **survives unchanged** | |
| M4-power — `WestlakePowerManagerService` | **survives unchanged** | |
| M4-PRE4 — MessageQueue JNI (6 natives) | **survives unchanged** | Handler/Looper/MessageQueue stay AOSP-stock; this JNI cluster is the right boundary. |
| M4-PRE5 — `WestlakePackageManagerStub` (local PM) | **survives unchanged** | Context.getPackageManager() returns it; CR19 fail-loud stands. |
| M4-PRE6 — `WestlakeContextImpl.getResources()` returns our Resources | **survives, simplified** | Returns the new thin `WestlakeResources` per §3.4. |
| M4-PRE7 — AssetManager JNI (56 natives) | **DELETED — see §6** | If `WestlakeResources` doesn't construct a framework `AssetManager`, the AssetManager native bridge is unreachable code. Keep for now; mark obsolete; delete after V2 §7 step 8 confirms unused. |
| M4-PRE8 — synthetic AssetManager via Unsafe | **DELETED** | No more `Unsafe.allocateInstance` of framework AssetManager. |
| M4-PRE9 — `WestlakeContentResolver` | **survives, audit** | Content provider surface; if any V2 app uses it, keep; if not, mark obsolete. |
| M4-PRE10 — `CharsetPrimer` (shared helper) | **survives** | Charset stub primer, dalvikvm-level concern, unrelated. |
| M4-PRE11 — `ColdBootstrap` (sCurrentActivityThread seed) | **partial survives** | Real-AOSP `ActivityThread.currentActivityThread()` is called by some framework reflection paths (e.g., `Window.setTheme` lookups). Keep seeding `sCurrentActivityThread` with our `WestlakeActivityThread`; the rest of ColdBootstrap (which preps `Looper`, `Handler`, etc.) survives. |
| **M4-PRE12 — AssetManager mValue/mOffsets plants** | **DELETED — see §6** | |
| **M4-PRE13 — Configuration LocaleList plants** | **DELETED — see §6** | |
| **M4-PRE14 — DisplayAdjustments plant** | **DELETED — see §6** | |
| CR1 — Tier-1 service correctness | **survives** | |
| CR2 — fail-loud unobserved methods (`ServiceMethodMissing.fail`) | **survives** | The pattern is exactly right for V2; the "is this method ever called" data is gold. |
| CR3/CR4/CR5 — `getSystemService` binder routing | **survives** | `SystemServiceWrapperRegistry` is the right design. |
| CR15 — widen-the-guard PF-arch-054 | **survives** | Substrate fix; orthogonal. |
| CR17 — PermissionEnforcer-bypass in services | **survives** | |
| CR19 — `WestlakePackageManagerStub` fail-loud | **survives** | |
| CR22 — frozen surface on `WestlakeContextImpl` | **survives** | Frozen surface is exactly right; V2 doesn't grow it. |
| **CR23-fix — Activity.attach try/catch + buildBaseContext** | **DELETED** in V2 §7 step 4 | We don't call real Activity.attach; the try/catch is unreachable. |
| CR24 — PF-arch-054 sentinel SIGBUS workaround (`identityHashCode + getName`) | **survives** | Belt-and-suspenders; CR26 fixed the substrate but workaround stays in DiscoverWrapperBase. |
| CR26 — PF-arch-055 (substrate fix for env->functions corruption) | **survives** | Pure substrate; V2-orthogonal. |
| CR27 — manifest-driven `DiscoverWrapperBase` | **survives** | Test harness; V2-orthogonal. |
| Pre-pivot Westlake classes: `WestlakeLayoutInflater`, `WestlakeInflater`, `WestlakeTheme`, `WestlakeVector`, `WestlakeNavGraph`, `WestlakeNode`, `WestlakeLayout`, `WestlakeView` | **survives, generalized** | These predate the drift; they're the generic per-app-agnostic substrates V2 needs. Audit for any per-app branches and excise. |
| `WestlakeRenderer` (pre-pivot) | **survives** | V2 surface output assumes this still does the view-tree → bitmap conversion. M6 daemon takes the bitmap. |
| `WestlakeLauncher` (post-CR14+CR16, 12,668 LOC) | **survives, further shrinks** | V2 step 7 cuts another ~1,500 LOC of plants/seeds that become unnecessary. |
| ColdBootstrap.java (production-side ActivityThread seed) | **survives** | Still needed for the small surface of framework reflection that reads `ActivityThread.currentActivityThread()`. |

---

## 6. What gets removed

| Component | Reason | Approx LOC |
|---|---|---|
| M4-PRE12 — `WestlakeResources.createSyntheticAssetManager` mValue/mOffsets plant | No synthetic framework AssetManager exists post-V2 | ~33 |
| M4-PRE13 — `WestlakeResources.plantLocaleState` | No synthetic framework Configuration exists post-V2 | ~58 |
| M4-PRE14 — `WestlakeResources.plantDisplayAdjustments` | No synthetic framework ResourcesImpl exists post-V2 | ~102 |
| CR23-fix Activity.attach try/catch in `WestlakeActivityThread.attachActivity` | We don't call real Activity.attach; our WestlakeActivity has its own attach | ~30 of the 95 LOC; rest is buildBaseContext which survives |
| `WestlakeResources.buildReflective` / `createSyntheticAssetManager` / synthetic ResourcesImpl path | Replaced by new direct-from-arsc path | ~200 |
| `Unsafe.allocateInstance` calls on `AssetManager`, `ResourcesImpl`, `Configuration`, `DisplayAdjustments` | None of these are instantiated post-V2 | ~50 cumulative |
| AssetManager JNI stub (56 natives) | Unreferenced after AssetManager removal | grep for caller refs; if zero, delete `assetmanager_jni_stub.cc` (~825 LOC C++) |
| `framework_duplicates.txt` entries for AssetManager/Resources/ResourcesImpl/Configuration/DisplayAdjustments | We no longer want framework's versions stripped from shim — *new* additions strip Application/Activity/Window | (table entry rewrite) |
| Pending CR25 work on `Window.setTheme(null)` | Window is stub post-V2; no NPE to hunt | 0 (work not started) |
| Anticipated M4-PRE15, M4-PRE16, ... in M4_DISCOVERY backlog | All would have been further plants on cold-init fields | (work not started; ~500 LOC avoided) |

**Net deletion:** ~480 LOC of in-tree plant/synthetic code + ~825 LOC of C++ AssetManager JNI (after caller audit) + ~500 LOC of avoided future plant work.

---

## 7. Implementation plan

Effort estimates are calendar person-days at ~5-6 hours focused work per day.

### Step 1 — Define `WestlakeActivity` API surface (1 person-day)

Read AOSP `Activity.java` public + protected method list (API 30). Categorize each into:
- **Implement** (touched by ≥1 test app — noice + McD + Counter as the seed): ~80 methods
- **`ServiceMethodMissing.fail`** (the CR2 pattern; surface as fail-loud throws when first hit): ~120 methods
- **No-op or default value** (lifecycle hooks with empty bodies in framework): ~30 methods

Output: `docs/engine/WESTLAKE_ACTIVITY_API.md` — the table.

### Step 2 — Implement `WestlakeActivity` (3 person-days)

Create `shim/java/android/app/Activity.java` (shadowed via `framework_duplicates.txt`). Body:
- `attach(...)` accepting both 6-arg and 17-arg variants (so we can drop CR23-fix's try/catch)
- field declarations matching framework's `Activity` (for `instanceof` and for any `getActivity().mFoo` reflection app code might do)
- `setContentView(int)` via `WestlakeLayoutInflater`
- `setContentView(View)`, `findViewById(int)`, `getResources()`, `getSystemService(String)`, `getApplicationContext()`, `getBaseContext()`, `getIntent()`, `getMenuInflater()`, `setTheme(int)`, `getTheme()`, `getWindow()` returning a stub `Window` (Step 5)
- `performCreate`, `performStart`, `performResume`, `performPause`, `performStop`, `performDestroy` — call user's overrides
- All "implement" methods from Step 1's table

Add `android.app.Activity` to `framework_duplicates.txt`.

### Step 3 — Implement `WestlakeApplication` (1 person-day)

Create `shim/java/android/app/Application.java` (shadowed via `framework_duplicates.txt`). Body:
- `<init>()`, `attachBaseContext(Context)`, `onCreate()`, `onTerminate()`
- `getApplicationContext()` returns `this`
- `getBaseContext()` returns `mBase`
- `getResources()` delegates to `mBase`
- All other public API delegates to `mBase`
- `registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks cb)` and friends — store callbacks; invoke from `WestlakeActivity.performCreate` etc.

### Step 4 — Implement `WestlakeResources` thin (3 person-days)

Rewrite `shim/java/com/westlake/services/WestlakeResources.java`:
- Replace `buildReflective` / `createSyntheticAssetManager` / plant logic with direct resource lookup
- Add `ResourceArscParser` (new file, ~400 LOC) reading the APK's `resources.arsc` at boot, building Java `Map<Integer, Resource>` keyed by resource ID
- Implement `getString`, `getStringArray`, `getInteger`, `getBoolean`, `getColor`, `getDimension`, `getDimensionPixelSize`, `getDimensionPixelOffset`, `getDrawable`, `getXml`, `getLayout`, `obtainTypedArray`, `getResourceName`, `getResourceEntryName`, `getIdentifier`
- `getConfiguration()` returns a Configuration constructed via its public no-arg ctor with locale/density/sw fields set (no Unsafe; no reflection)
- `getDisplayMetrics()` returns a `DisplayMetrics` populated from M4d display service
- `getAssets()` returns a thin `WestlakeAssetManager` (new class, ~150 LOC) over the APK's resource entries

### Step 5 — Implement Window / PhoneWindow / DecorView / WindowManagerImpl stubs (1 person-day)

Create `shim/java/android/view/Window.java`, `shim/java/com/android/internal/policy/PhoneWindow.java`, `shim/java/com/android/internal/policy/DecorView.java`, `shim/java/android/view/WindowManagerImpl.java` — all minimal:
- `Window`: holds `mContentView`; `setContentView(int)` inflates via WestlakeLayoutInflater; `getDecorView()` returns mContentView; `setTheme(int)` is no-op
- `PhoneWindow extends Window`: no override
- `DecorView extends FrameLayout`: empty
- `WindowManagerImpl`: stub `addView`, `removeView`, `updateViewLayout`; routes to M6 surface daemon when M6 lands

Add these to `framework_duplicates.txt`.

### Step 6 — Wire `WestlakeActivityThread.attachActivity` to the new substrate (1 person-day)

Replace the current 6-arg-then-fallback `activity.attach(...)` logic in `shim/java/android/app/WestlakeActivityThread.java:3017` with:
- Call our own `WestlakeActivity.attach(base, app, intent, component, instrumentation, instance)` (the 6-arg version we now own)
- Remove the try/catch (CR23-fix code) — unreachable, since `Activity.attach` is now our class
- Remove the `ContextWrapper.class.getDeclaredField("mBase")` reflective seed — our `Activity.attach` takes Context and sets mBase directly

### Step 7 — Remove obsolete plants (1 person-day)

Delete from `shim/java/com/westlake/services/WestlakeResources.java`:
- M4-PRE12 (`createSyntheticAssetManager` mValue/mOffsets plant)
- M4-PRE13 (`plantLocaleState` and Configuration plant)
- M4-PRE14 (`plantDisplayAdjustments`)
- `buildReflective` (replaced by Step 4 rewrite)

Audit `art-latest/stubs/assetmanager_jni_stub.cc`: if no caller in shim/java references it, delete (caller-only references come from `framework_duplicates.txt`-stripped framework AssetManager paths).

### Step 8 — Verify regression (1 person-day)

`bash scripts/binder-pivot-regression.sh --full`. Required PASS:
- HelloBinder.dex
- AsInterfaceTest.dex
- PowerServiceTest.dex, ActivityServiceTest.dex, WindowServiceTest.dex, DisplayServiceTest.dex, NotificationServiceTest.dex, InputMethodServiceTest.dex, SystemServiceRouteTest.dex
- sm_smoke (musl + bionic), binder_smoke

Re-run noice-discover + mcd-discover:
- Expect: noice reaches `MainActivity.onCreate` *completion*, hits the first Hilt @Inject method body
- Expect: McD reaches `McDMarketApplication.onCreate` completion, `SplashActivity.onCreate` completion, Hilt-injected fields populated, first dashboard binder transaction issued to our M4a (or fails fail-loud with `ServiceMethodMissing.fail` revealing the next service method to implement — *legitimate* discovery, not field-plant discovery)

### Step 9 — Render to display (the M6 prerequisite; *not in this V2 plan*) (allocated separately)

The above 8 steps land V2 substrate. M6 (surface daemon) then connects to the resulting framebuffer; that work is its own milestone, not part of this V2 transition.

### Step 10 — Document and migrate (0.5 person-day)

Update `PHASE_1_STATUS.md` "what works" section: V2 milestones M4-PRE12/13/14 marked deleted, new entries WACT (WestlakeActivity), WAPP (WestlakeApplication), WRES2 (thin WestlakeResources), WWIN (Window stub) added. Update `M4_DISCOVERY.md` discovery harness expectations.

### Step 11 — Codex 2nd-opinion review of V2 substrate (0.5 person-day)

Before declaring V2 done, run `codex exec --dangerously-bypass-approvals-and-sandbox` on the substrate diff (Steps 2-7) to verify no architectural drift. Same review cadence that surfaced V1's drift.

### Total

| Step | Person-days |
|---|---|
| 1. Define WestlakeActivity API | 1.0 |
| 2. Implement WestlakeActivity | 3.0 |
| 3. Implement WestlakeApplication | 1.0 |
| 4. Implement WestlakeResources thin | 3.0 |
| 5. Window / PhoneWindow / DecorView stubs | 1.0 |
| 6. Wire WestlakeActivityThread.attachActivity | 1.0 |
| 7. Remove obsolete plants | 1.0 |
| 8. Regression + discover verification | 1.0 |
| 9. (M6 surface daemon, separate) | — |
| 10. Docs migration | 0.5 |
| 11. Codex review | 0.5 |
| **Total V2 substrate** | **13.0 person-days** |

At ~5h/day focused work this is ~10-13 calendar days for a single agent; with two parallel agents (one on the Java substrate, one on the Resources/arsc work) probably ~6-8 calendar days.

---

## 8. Open architectural questions

### 8.1 Does WestlakeActivity inherit from framework's `android.app.Activity` (which we then shadow), or from `Object`?

If we add `android.app.Activity` to `framework_duplicates.txt`, our `shim/java/android/app/Activity.java` IS `android.app.Activity` at runtime. The app's `MainActivity extends AppCompatActivity extends FragmentActivity extends ComponentActivity extends Activity` resolves `Activity` to our class. **`instanceof android.app.Activity` checks succeed.** Method dispatch on overridden methods (e.g., `getResources()`) resolves to our class.

The question: do AppCompatActivity / FragmentActivity / ComponentActivity (which live in androidx, app classpath, NOT framework.jar) compile against framework's `Activity` *fields* (e.g., `super.mTitle`)? If yes, those field accesses miss our class. (androidx code is compiled against android-30 SDK stubs; field-access on Activity is rare; mostly method calls.)

**Risk: MEDIUM.** Mitigation: when we draft `WestlakeActivity`, copy the field declarations from framework's `Activity` (just declarations, not the cold-init logic that fills them) so any androidx code that does `super.mFoo` finds the field. Most fields stay JVM-default null; androidx-side checks tolerate.

**Followup task:** grep androidx-fragment.jar bytecode for `Field android/app/Activity.*` references; surface the list to confirm fields-needed-non-null is small.

### 8.2 What about Hilt-generated `Hilt_NoiceApplication extends Application`?

Hilt's annotation processor generates `Hilt_NoiceApplication.java` at compile time. It does:

```java
public class Hilt_NoiceApplication extends Application implements GeneratedComponentManagerHolder {
  private final SavedStateHandleHolder savedStateHandleHolder = ...;
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);   // reaches our WestlakeApplication.attachBaseContext
    hiltInternalInject();             // creates Hilt component
  }
  ...
}
```

`super.attachBaseContext(base)` reaches our `Application.attachBaseContext`, which just sets `mBase`. Then `hiltInternalInject()` runs Hilt-generated Dagger component-builder code which calls `getApplicationContext()` — reaches our `getApplicationContext()` which returns `this`. **Should work.**

The risk is at component construction: Hilt-generated `DaggerNoiceApplication_HiltComponents_SingletonC.Builder` calls `applicationContextModule(new ApplicationContextModule(getApplicationContext()))`. `ApplicationContextModule.context` is then injected wherever `@ApplicationContext Context` is requested. Our `getApplicationContext()` returns our `Application` (which `instanceof Context` is true). **Should work.**

**Risk: LOW.** Hilt's path is documented and standardized; we'll likely hit it cleanly.

### 8.3 What about apps that subclass `AppCompatActivity` and use `AppCompatDelegate`?

`AppCompatDelegate.getDelegate(this)` reads `activity.getWindow()` to install its own DecorView. Our `Window` is a stub. AppCompatDelegate's `installViewFactory(LayoutInflater)` calls `getLayoutInflater().setFactory2(this)`.

`Activity.getLayoutInflater()` returns `getWindow().getLayoutInflater()` in framework. Our `WestlakeActivity.getLayoutInflater()` returns `WestlakeLayoutInflater.from(this)`. AppCompatDelegate sets a Factory2 on it; WestlakeLayoutInflater's `inflate()` already honors set Factory2 (it's an existing class; predates pivot).

**Risk: MEDIUM.** AppCompatDelegate has ~15K LOC of compatibility shims; some paths read framework Activity fields. Will surface during V2 step 8 (noice + McD discovery).

### 8.4 How do we handle `Activity.startActivity(intent)`?

The app's `Activity.startActivity(Intent)` in framework eventually calls `ActivityTaskManager.getService().startActivity(...)` — a Binder call to system_server's ATMS. In V2, our `WestlakeActivity.startActivity(Intent)` calls `IActivityManager.startActivity(...)` (our M4a's Stub). Our M4a's implementation: queue the intent, mark it active, and resolve the target activity class via our local PackageManager.

The next question: does our M4a then *launch* the target activity? In a single-Activity app, this is rare. For multi-Activity apps (McD's SplashActivity → DashboardActivity flow), our M4a must trigger our `WestlakeActivityThread.performLaunchActivity` for the target class.

**Risk: HIGH.** Multi-activity Intent handling is a new code path. Worked around today by per-app launch logic in `WestlakeLauncher.mainImpl` (lines 4638-4861). V2 generalizes: M4a's `startActivity` calls a generic `WestlakeActivityThread.launchActivityByComponent(component)`.

### 8.5 `findViewById(int)` performance with deep view trees

WestlakeActivity.findViewById walks the content view tree. For McD's dashboard with thousands of views, this is O(n) per call; called by Hilt-injected View references at every onCreate. May be fine; may need a HashMap cache built post-`setContentView`. Profile in V2 step 8.

**Risk: LOW.** Standard performance tuning if observed.

### 8.6 Theme parent chains (`?attr/`)

WestlakeTheme.obtainStyledAttributes today does a simplified attribute lookup that doesn't fully honor theme parent chains. AppCompat themes have multi-level parent chains (`Theme.AppCompat.Light.DarkActionBar` → `Theme.AppCompat.Light` → `Theme.AppCompat` → `Theme.Material` → `Theme.Holo`).

For a correct V2, our parser must walk the parent chain. Resources.arsc encodes parents.

**Risk: MEDIUM.** Mostly correctness, not bugs that crash. Discover via subtraction: render noice, compare to native rendering, eyeball missing styles, walk back to the missing parent chain.

### 8.7 Hidden API access (`@hide` / `@SystemApi`)

Some app + library code reaches into `@hide` framework API (e.g., `ActivityManager.getRunningAppProcesses()` was `@hide` until API 26). Our `WestlakeActivity` / `WestlakeApplication` should expose `@hide` methods the wild observes — but we discover those via running, not by pre-implementing.

**Risk: LOW.** CR2 `ServiceMethodMissing.fail` pattern handles this: any `@hide` call we don't implement throws fail-loud; we add the method.

### 8.8 What survives if M5/M6 don't land in V2 timeframe?

V2's substrate work (Steps 1-7) is independent of M5 (audio) and M6 (surface). The substrate can land and be regression-validated *without* rendering anything visible. The acceptance signal is "Application.onCreate runs to completion + Activity.onCreate runs to completion + Hilt @Inject fires real Binder transactions." Visible rendering requires M6, separate milestone.

**Risk: LOW.** Decoupling is clean.

### 8.9 Highest-risk-rated open questions

- **8.4 (multi-Activity Intent handling)** — HIGH. Discover during V2 step 8. May trigger Step-12 follow-up CR.
- **8.1 (WestlakeActivity field declarations vs androidx)** — MEDIUM. Mitigatable; surface bytecode survey ASAP in Step 1.
- **8.3 (AppCompatDelegate compatibility)** — MEDIUM. Most likely place for the next "drift temptation"; preempt by carefully exposing the methods AppCompatDelegate actually calls.

---

## 9. Decision log

| Date | Decision | Rationale | Reversibility |
|---|---|---|---|
| 2026-05-13 | V2 supersedes V1 for in-process Java boundary; V1 binder substrate stays | User flagged the drift; M4-PRE12/13/14 demonstrated approach (a) is unbounded; the pre-CR14 per-app shim demonstrated approach (b) is unbounded | Reversible at any step; V1 still in tree until Step 7 deletes plants |
| 2026-05-13 | Substitute at `Activity.attach` / `Application.attach` / `Window.<init>` seam | These are the cold-init boundaries with the unbounded field cascade; below is mostly stateless framework code that works in-process | Decision can be revisited if framework field access on real Activity/Application proves a smaller cost than estimated |
| 2026-05-13 | Generic classpath-shadow (`framework_duplicates.txt`) substitution, not per-app | Per-app is what got C1/CR14/CR16 deleted; same mechanism used by CR23-fix for Context, extend for Application/Activity/Window | Reversible by removing the shadow entries; doesn't break existing binder substrate |
| 2026-05-13 | Resources surface owned by Westlake; parse resources.arsc directly | Framework's ResourcesImpl/AssetManager cold-init cascade ate M4-PRE12-14; arsc parser is finite (~400 LOC) | Reversible: 11-A (keep planting) still possible if arsc parser proves harder than estimated |
| 2026-05-13 | Real androidx Fragment lifecycle runs unmodified on WestlakeActivity | androidx is app classpath; doesn't go through ActivityThread; only depends on Activity public API which WestlakeActivity provides | Decision validated by V2 step 8 noice-discover run; if FragmentManager fails, revisit |

---

## 10. Anti-patterns (updated for V2)

V1's anti-patterns (no per-app, no additive shim, no renderer-time bypass, no Unsafe.allocateInstance on framework objects, no speculative completeness) all stand. Add:

### 10.1 No reflective field plants on framework cold-init objects

Specifically: if you find yourself writing
```java
Field f = SomeFrameworkClass.class.getDeclaredField("mSomething");
f.setAccessible(true);
f.set(instance, reflectivelyConstructed());
```
on a framework class instance that you just `Unsafe.allocateInstance`'d, **stop**. You are M4-PRE12/13/14 again. The architectural answer is: don't construct that framework instance at all; substitute the class via `framework_duplicates.txt` instead.

### 10.2 No `try { realFrameworkCold(...); } catch (Throwable) { fallback() }` patterns

If real framework code throws on cold-init, the right fix is to not call it. Wrapping in try/catch hides the architectural problem and accumulates dead branches.

The CR23-fix `try { activity.attach(6 args) } catch (NoSuchMethodError | LinkageError)` was correct for the moment (it kept McD progressing while V2 was unwritten) but is exactly the kind of catch-block that V2 deletes in Step 6.

### 10.3 No "almost framework" classes

`WestlakeContextImpl extends Context` is correct (it implements an abstract surface). `WestlakeActivity extends Activity` via classpath shadow is correct (it replaces a concrete class system-wide). But:

- Do NOT `class WestlakeActivityHelper { static void prepare(android.app.Activity a) { ... } }` that runs alongside framework Activity and partially substitutes its state. That's the deleted-with-WestlakeFragmentLifecycle pattern, in different clothes.

### 10.4 No bypassing the V2 substrate via direct framework reflection

If V2 step 2 ships `WestlakeActivity`, then `WestlakeLauncher.mainImpl` should NOT reflectively construct framework's `android.app.Activity` and operate on it. The whole point of V2 is that there is exactly one `Activity` class at runtime — ours.

---

## 11. Glossary additions

- **classpath shadow**: the technique (via `framework_duplicates.txt`) of declaring a class in `aosp-shim.dex` that has the same fully-qualified name as a class in `framework.jar`, and arranging classpath order such that our class wins resolution. The framework copy of the class is unreachable.
- **cold-init field**: a field on a framework class whose value is set by `system_server`-driven bootstrap (`ActivityThread.handleBindApplication` / `handleLaunchActivity`), not by the class's constructor. Examples: `Activity.mApplication`, `ResourcesImpl.mDisplayAdjustments`, `Configuration.mLocaleList`.
- **plant**: reflectively setting a cold-init field on an `Unsafe.allocateInstance`'d framework object instance. The M4-PRE12-14 pattern. **Banned in V2.**
- **substrate**: the layer that satisfies framework public/protected API surface from below. Binder substrate (libbinder + servicemanager + services) satisfies `Stub.asInterface`'s expectations. Java substrate (WestlakeActivity + WestlakeApplication + WestlakeResources) satisfies the app's `super.foo()` expectations.
- **substitution boundary**: the line at which Westlake-owned code replaces AOSP code. V1 placed it at Binder (correct for cross-process). V2 places it ALSO at the `Activity.attach`/`Application.attach`/`Resources` API surface inside dalvikvm (corrected for in-process).
- **drift**: the M4-PRE12-14-style pattern of advancing one cold-init field at a time, never converging. Identified and named in this V2 doc.

---

## 12. Final summary table — what V2 commits

| Question | V1 answer | V2 answer |
|---|---|---|
| Where do binder calls cross? | Westlake libbinder + servicemanager | Same (V1 stands) |
| How are framework services satisfied? | Westlake `Westlake*Service extends IXxx.Stub` registered to ServiceManager | Same (V1 stands) |
| Does AOSP framework.jar run unmodified? | "Yes" (misread; led to drift) | **Mostly**, with these classes classpath-shadowed: `Application`, `Activity`, `Resources`, `ResourcesImpl` (delete), `AssetManager` (delete), `Configuration` (delete), `Theme`, `Window`, `PhoneWindow`, `DecorView`, `WindowManagerImpl` |
| Does real `Activity.attach` run? | Implicit "yes" → led to NPE cascade | **No.** We classpath-shadow `Activity`; our `attach` is empty-ish. |
| Where do `Resources` lookups come from? | Synthetic AssetManager + planted fields → drift | Directly from `resources.arsc` parser; no framework Resources machinery |
| Per-app shims allowed? | No | Same (V1 stands) |
| Additive shimming allowed? | No | Same (V1 stands), with explicit field-plant subcase named |
| Where is the unit of discovery? | "Implement only observed Binder transactions" | Same, **plus**: "shadow only classes whose cold-init is observed-broken" |

---

## 13. Reading order for the next agent

If you're picking up V2 implementation:

1. **This doc** (you're here). 30 min.
2. **`MIGRATION_FROM_V1.md`** (companion). 15 min. Concrete file-by-file what to keep/touch/delete.
3. **`PHASE_1_STATUS.md` §1.3 + §2**. 15 min. Current state of binder substrate.
4. **`M4_DISCOVERY.md` §§34-36 + §49**. 30 min. The drift's origin so you don't repeat it.
5. **`BINDER_PIVOT_DESIGN.md` (V1) §3 + §4**. 30 min. The binder substrate design which survives.
6. **`WESTLAKE_LAUNCHER_AUDIT.md`**. 15 min. What's left of launcher to slim further.
7. **Codex 2nd-opinion** the V2 substrate diff before declaring done (per Step 11).

Total V2 onboarding: ~2.25 hours of doc reading before touching code.

---

**End of V2 design.**
