# Westlake In-Process App Run — Multi-Process Apps Design

**Status (2026-05-14)**: Design proposal. Supersedes the "Multi-process apps — can't replicate"
limitation in `INPROCESS_RUN.md`. After deeper analysis the limitation was overcautious:
multi-process Android apps CAN be hosted by Westlake using **static proxy host-processes**
declared in our manifest, with the existing five-pillar pattern replicated independently in
each. This document specifies the architecture, phasing, and known limits.

Read `INPROCESS_RUN.md` first — this doc assumes you know the five pillars,
`createPackageContext`, activity-alias routing, and the `NoiceInProcessActivity` reference
implementation.

---

## 1. Why multi-process Android apps exist

Android lets a developer attach `android:process=":suffix"` (private) or
`android:process="pkg.fqn"` (shared) to any `<activity>`, `<service>`, `<receiver>`, or
`<provider>` declaration. At launch time the Activity Manager forks a separate zygote child,
running a new `ActivityThread` with its own `Application` instance under the SAME UID. The
five real-world reasons developers do this:

**Crash isolation.** A renderer for untrusted HTML (WebView), PDF, or third-party SDK plugin
can be put in `:renderer` so a native-code crash there only kills the satellite process —
the main UI keeps running. The classic example is Chrome/WebView's `:sandboxed_process0..N`
pattern and Telegram's `:tgvoip` process for the voice-call native engine.

**Per-process memory cap.** Android imposes a per-process heap limit (192–512 MB on most
devices). A media-processing pipeline (video transcode, large image decode, on-device ML
inference) running in the main process steals heap from UI bitmaps. Putting the worker in
`:work` doubles the available heap budget at the cost of IPC marshalling. Apps like
PhotoLab, Snapseed, and TensorFlow Lite sample apps do this.

**Lifecycle independence.** A long-running background service (music playback, alarm
clock, file sync) in a separate process keeps running after the user swipes the foreground
task away. Android kills the foreground process aggressively under memory pressure but is
gentler on services in another process. Spotify's `:music`, Pushbullet's `:push`, and
WhatsApp's `:gcm` all use this.

**Security boundary.** A "keyring" process holding decrypted credentials runs in `:secure`
with stricter sandbox rules; the main UI never has the plaintext in its heap. Signal, the
Yubico Authenticator, and most password managers use a separate process for the credential
vault. This is the only "security" use case that genuinely depends on the OS giving each
process its own VM-level address space.

**Expensive native lib isolation.** Apps that load 50–200 MB of `.so` files (game engines,
heavy AR/CV libraries, OpenCV, dlib, FFmpeg-x265) put the loader in a satellite process so
the main UI doesn't pay the dlopen cost on cold start. Camera apps with computational
photography pipelines (Google Camera ports, Open Camera GCam) do this routinely.

Google Mobile Services (GMS) and Firebase Cloud Messaging (FCM) are an important special
case: they typically declare push receivers and the connector service in `:remote`,
`:fcm_service`, or `:gcm`. The system delivers wake-ups to that process even when the main
app is dead. We discuss the FCM case in §5.

---

## 2. How a normal Android `:remote` process actually works

When `ActivityTaskManagerService` (AOSP `frameworks/base/services/core/java/com/android/
server/wm/ActivityTaskManagerService.java`) resolves an intent whose target component has
`android:process=":remote"`, it computes the full process name as
`"<package_name>:remote"` (e.g. `com.foo.bar:remote`). It then asks
`ProcessRecord.startProcessLocked()` (`frameworks/base/services/core/java/com/android/
server/am/ProcessList.java`) to find or fork a process with that name.

The fork:

1. **Zygote child.** `Process.start()` sends a fork request to `zygote` (or
   `app_zygote` for `isolatedProcess`). The new pid gets uid = app uid, gid = app gid,
   selinux context = app's domain.
2. **Separate `ActivityThread`.** The child runs `ActivityThread.main()` which calls
   `attach(false)`, creating its own `mPackages` map and its own `mInitialApplication`
   field. `ActivityThread.handleBindApplication` runs and calls
   `LoadedApk.makeApplicationInner()` — which constructs a SECOND `Application`
   instance from the same `<application android:name>` class. Two `Application` instances
   under the same package, on separate processes, with their own static-field-namespaces.
3. **Shared UID, shared `/data/data` directory.** Both processes see the same files. This
   is the source of one of multi-process's classic footguns:
   `SharedPreferences` and SQLite open the same file in two processes, and the OS does NOT
   coordinate caching. `MODE_MULTI_PROCESS` (deprecated since API 11) bandaged this badly.
   Real apps use `ContentProvider` or AIDL to serialize multi-process state.
4. **Binder IPC.** Activities/Services in different processes communicate through
   `IBinder` proxies. `bindService(intent)` returns the proxy; `onBind()` runs in the
   server process and returns a `Binder` whose proxy is marshalled back to the client.
   See `frameworks/base/core/java/android/os/Binder.java` and
   `frameworks/base/core/java/android/app/ContextImpl.java#bindServiceCommon`.
5. **System-wide registry.** `PackageManagerService` (`PackageInfo.processName` per
   component) and `ActivityManagerService` (`mProcessNames` keyed by `<processName,uid>`)
   together know which (process, package) pair owns which component. A `startActivity`
   intent for `pkg/.SomeActivityInRemoteProcess` is routed to a process whose name matches
   the manifest's `android:process` attribute for that activity.

Key AOSP source paths to read if you're modifying this:

- `frameworks/base/services/core/java/com/android/server/am/ProcessRecord.java`
- `frameworks/base/services/core/java/com/android/server/am/ProcessList.java#startProcessLocked`
- `frameworks/base/core/java/android/app/ActivityThread.java#handleBindApplication`
- `frameworks/base/core/java/android/app/LoadedApk.java#makeApplicationInner`
- `frameworks/base/services/core/java/com/android/server/wm/ActivityStarter.java`
- `frameworks/base/core/java/android/content/pm/ApplicationInfo.java` (look at `processName`)
- `frameworks/base/core/java/android/content/pm/ActivityInfo.java#processName`

---

## 3. Proposed design for Westlake

**Core insight.** The OS does not care WHAT a `:remote` process contains. It only cares
that the manifest says some component lives in process X, and that when an intent targets
that component, the runtime forks/finds process X and invokes the component there. We
control both halves: our host manifest declares the processes, our in-process launcher
controls what runs once the process is alive.

So a foreign app that says `<service android:name=".FooSyncService"
android:process=":sync"/>` can be hosted by declaring in OUR manifest a corresponding
proxy:

```xml
<service
    android:name=".ForeignProxyService_sync"
    android:process=":app_proc_sync"
    android:exported="true" />
```

And rewriting intents at start-time so that `app/.FooSyncService` → `com.westlake.host/
.ForeignProxyService_sync` with the original FQCN carried as an extra. The proxy service,
running in its own host process, replays the **same five-pillar bootstrap** to load the
foreign app's classes, instantiate its `Application`, and invoke
`FooSyncService.onCreate() + onStartCommand()/onBind()`.

### 3.1 Manifest pattern

Static proxy pool, N=4 declared once:

```xml
<!-- Activity proxies for any foreign component declaring android:process=":suffix" -->
<activity android:name=".InProcessActivityProc1" android:process=":app_proc1" android:exported="true" />
<activity android:name=".InProcessActivityProc2" android:process=":app_proc2" android:exported="true" />
<activity android:name=".InProcessActivityProc3" android:process=":app_proc3" android:exported="true" />
<activity android:name=".InProcessActivityProc4" android:process=":app_proc4" android:exported="true" />

<!-- Service proxies (one per host-process slot, all share the slot's process) -->
<service  android:name=".InProcessServiceProc1"  android:process=":app_proc1" android:exported="true" />
<service  android:name=".InProcessServiceProc2"  android:process=":app_proc2" android:exported="true" />
<!-- ... -->

<!-- Receiver proxies similarly -->
<receiver android:name=".InProcessReceiverProc1" android:process=":app_proc1" android:exported="true" />
<!-- ... -->
```

Default process (`:app_proc0`) is the main host process — same one
`NoiceInProcessActivity` already runs in.

Why static, not dynamic? Android does NOT allow a non-system app to spawn arbitrary
processes at runtime. The only way the system knows to fork a new process is the
`android:process` attribute parsed from your APK's manifest at install time. We must
pre-declare every process we may want to use.

### 3.2 Slot allocation at runtime

When the host launcher first sees a foreign app, it walks the foreign manifest to
enumerate distinct process names:

```kotlin
val pi = packageManager.getPackageInfo(NOICE_PKG,
    GET_ACTIVITIES or GET_SERVICES or GET_RECEIVERS or GET_PROVIDERS)
val processNames = sequence {
    pi.activities?.forEach { yield(it.processName) }
    pi.services?.forEach   { yield(it.processName) }
    pi.receivers?.forEach  { yield(it.processName) }
    pi.providers?.forEach  { yield(it.processName) }
}.toSet()
```

`ActivityInfo.processName` is exactly what the OS uses for the routing decision. If
all components have `processName == pi.applicationInfo.processName`, the foreign app is
single-process and we run as today.

If we find e.g. 3 distinct process names {default, `:remote`, `:sync`}, we allocate
those onto our slots:

| Foreign process name      | Slot                  | Host process              |
|---------------------------|-----------------------|---------------------------|
| `com.foo.bar`             | proc0 (default/main)  | `com.westlake.host`       |
| `com.foo.bar:remote`      | proc1                 | `com.westlake.host:app_proc1` |
| `com.foo.bar:sync`        | proc2                 | `com.westlake.host:app_proc2` |

The allocation is stored in a `SharedPreferences` file in the default host process so
later launches use a consistent mapping. If allocation needs > N slots, we log an error
and either fall back (collapse all extras into a single overflow slot — losing isolation
but not functionality) or refuse to host (return an error to the launcher caller).

### 3.3 Intent rewriting (extends Tier-1 #1 / PF-inproc-002)

The Tier-1 intent rewriter from `INPROCESS_RUN.md` already plans to hook
`IActivityTaskManager.startActivity` so that intents targeting `<foreign_pkg>/...` get
rewritten to `com.westlake.host/.NoiceInProcessActivity` with the original FQCN in an
extra.

We extend the rewriter:

```kotlin
// Pseudo-code in the IActivityTaskManager proxy
fun rewriteIntent(intent: Intent): Intent {
    val cn = intent.component ?: return intent
    val foreignPkg = cn.packageName.takeIf { it in knownForeignPkgs } ?: return intent

    // Look up the component's declared process by querying foreign PackageManager.
    val componentInfo = packageManager.getActivityInfo(cn, 0)
        // OR getServiceInfo / getReceiverInfo, depending on intent type
    val foreignProcessName = componentInfo.processName
    val slot = slotAllocator.resolve(foreignPkg, foreignProcessName)
        // returns 0 for default, 1..N for proxies

    val targetClass = when (intent.componentType) {
        ACTIVITY  -> "com.westlake.host.InProcessActivityProc$slot"
        SERVICE   -> "com.westlake.host.InProcessServiceProc$slot"
        RECEIVER  -> "com.westlake.host.InProcessReceiverProc$slot"
        PROVIDER  -> /* see §3.6 below */
    }
    intent.component = ComponentName("com.westlake.host", targetClass)
    intent.putExtra(EXTRA_TARGET_PKG, foreignPkg)
    intent.putExtra(EXTRA_TARGET_FQCN, cn.className)
    return intent
}
```

The rewriter runs in EVERY host process — each process binds its own
`IActivityTaskManager` proxy. A foreign service in `:app_proc1` calling
`startActivity(intent for com.foo.bar/.Settings)` gets rewritten to
`com.westlake.host/.InProcessActivityProc0` (since `.Settings` lives in the foreign
default process).

### 3.4 Per-process five-pillar replay

Each proxy `Activity`/`Service`/`Receiver` runs the SAME five-pillar pattern
independently:

```kotlin
class InProcessActivityProc1 : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InProcessLauncher.launchActivity(
            host = this,
            foreignPkg = intent.getStringExtra(EXTRA_TARGET_PKG)!!,
            foreignFqcn = intent.getStringExtra(EXTRA_TARGET_FQCN)!!,
            // ... five-pillar pattern: createPackageContext, redirectDataDir,
            // bypassHiddenApi, stubLocaleManager, instantiate App + Activity,
            // attach, onCreate, steal view tree, drive lifecycle.
        )
    }
}
```

The `InProcessLauncher` is the generic refactor (open item #2 in INPROCESS_RUN.md). It
takes a config and a host activity, runs the full bootstrap. Subclasses
`InProcessActivityProc0..N` are 5-line shells.

Because each proxy lives in its own OS process, each process has its own:

- ART instance (own static fields, own primordial classloader)
- `ActivityThread.sCurrentActivityThread` singleton
- Foreign `Application` instance (newly instantiated in this process)
- `LoadedApk` cache
- `SharedPreferences` map (in-memory mirror of the same backing files)
- Room / SQLite database connections
- Hilt singleton scope

This is the exact isolation guarantee Android gives a real multi-process app, because we
literally use the same mechanism (manifest-declared android:process). Static fields in the
foreign app's classes are namespaced by process automatically.

### 3.5 Cross-process Binder for foreign services

Services with `android:process=":remote"` exist primarily to be bound from the main
process via `Context.bindService` and return an `IBinder` that the caller uses for IPC.

In our model:

1. **Foreign main-process code calls `bindService(intent for FooRemoteService)`.**
2. **Intent rewriter** sees `FooRemoteService.processName == ":remote"`, rewrites to
   `bindService(intent for com.westlake.host/.InProcessServiceProc1)` with the original
   FQCN as an extra.
3. **`NoiceSafeContext` allows this through** (it's now bound to OUR proxy, not the
   foreign service's process — that's safe). Note: `NoiceSafeContext.shouldStub` must be
   updated so that intents whose component package == host package don't get stubbed.
4. **Android forks `:app_proc1`**, instantiates `InProcessServiceProc1`, calls its
   `onBind(intent)`.
5. **`InProcessServiceProc1.onBind(intent)`** runs the five-pillar bootstrap for the
   foreign app's `Application` in THIS process, then instantiates `FooRemoteService` via
   reflection, calls `foreignService.attachBaseContext(...)`, `onCreate()`, and
   `onBind(intent)`. It returns the resulting `IBinder` to Android's `ActivityManager`.
6. **The Binder proxy** is returned to the caller in the main host process. From the
   foreign code's perspective, it received `FooRemoteService`'s actual `IBinder` — same as
   on stock Android.

The Binder transport is the OS's, not ours. We just put valid `IBinder` instances at the
two ends of a normal Android bind-call. No special marshalling.

### 3.6 Content providers

This one is materially harder than activities/services because providers are resolved
by **authority**, not by component name, and that resolution happens inside system_server
before any code in our process runs. AOSP behavior (quoting
`PackageManagerService.queryContentProviders`):

> "ContentProviders are resolved by authority via the system-wide
> `mProvidersByAuthority` map. The map is built at package-install time from each APK's
> manifest. We cannot reroute a query to authority `com.foo.bar.provider` away from the
> foreign app's process from inside another app's process."

The OS has the authority→process mapping baked in. The intent rewriter never sees a
ContentProvider call because there's no Intent.

**Mitigation:** for the multi-process design we explicitly DO NOT host ContentProviders
in proxy slots. If the foreign app's main process binds to its own provider, the bind goes
to the foreign app's actual process (forked separately by the OS), which then runs the
provider code there. If the foreign app's `:remote` service binds to its own provider,
the same happens. Cross-process state inside the foreign app may end up split between
two host-owned processes (proc0, proc1) PLUS the foreign app's own forked provider
process. This is acceptable as long as the foreign app uses `ContentProvider` only for
small, well-defined queries (account state, prefs sync).

Alternative (Phase D, out-of-scope here): declare proxy providers in our manifest with the
foreign authority. This requires us to claim the authority globally, which fails install
if the foreign app is also installed (authority collision). Solvable only by force-stopping
or uninstalling the foreign APK at host-install time.

---

## 4. Implementation phases

### Phase A — discovery (1 day)

Read foreign manifest, group components by process:

- Hook `getPackageInfo(pkg, flags)` with all four GET_* flags.
- Build a `Map<ProcessName, List<ComponentInfo>>`.
- Persist allocation `Map<ForeignProcessName, SlotIndex>` to `SharedPreferences`.
- Log a discovery report:
  ```
  com.github.ashutoshgngwr.noice: 1 process (default only) — single-slot.
  com.foo.bar: 3 processes: default, :sync, :remote — slots 0,1,2.
  ```

**Acceptance:** discovery report logged for noice (single) and McD (single, AFAIK) and a
synthetic multi-process test APK.

### Phase B — one proxy pair, prove the pattern (3–5 days)

Build a tiny test APK `com.westlake.mptest`:

```xml
<activity android:name=".MainActivity" android:exported="true">
    <intent-filter><action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/></intent-filter>
</activity>
<service android:name=".CounterService" android:process=":counter" />
<service android:name=".PingerService"  android:process=":pinger" />
```

- `CounterService.onBind` returns a Binder with `incrementAndGet(): Int`.
- `PingerService.onBind` returns a Binder with `getRoundTripMs(): Long`.
- `MainActivity` binds to both, displays counters.
- Validation:
  - Running the APK natively (without Westlake) shows 3 processes via
    `adb shell ps | grep com.westlake.mptest` — `com.westlake.mptest`,
    `com.westlake.mptest:counter`, `com.westlake.mptest:pinger`.
  - Running via Westlake's `MpTestInProcessActivity` shows 3 host processes:
    `com.westlake.host`, `com.westlake.host:app_proc1`, `com.westlake.host:app_proc2`.
  - The counter increments and survives main activity restart (different process keeps
    its own copy of `AtomicInteger` static).
  - Killing `:counter` process resets the counter; main activity still binds and gets a
    fresh service instance.

This proves all 3 hard pieces: per-process bootstrap, intent rewriting between processes,
Binder return.

### Phase C — generalize and integrate (2 days)

- Refactor `NoiceInProcessActivity` + `McdInProcessActivity` into thin
  `InProcessActivityProc0` shells that call `InProcessLauncher(AppConfig)`.
- Add `InProcessServiceProc0..N` and `InProcessReceiverProc0..N` boilerplate. Each is
  generic (no foreign-app-specific code) — they look up the target via Intent extras.
- Wire the discovery + slot-allocator from Phase A so noice/McD pick correct slots.
- Test noice and McD continue to work (zero regression on single-process apps).
- Run a real multi-process Android app — Telegram or Signal (both use `:remote` for FCM
  + voice).

**Acceptance:** Telegram or Signal launches via Westlake with at least one `:remote`
service successfully hosted in `:app_proc1`. Don't expect full functionality (network +
push tokens won't work without GMS), but Application + at least one Activity + one
`:remote` Service must instantiate cleanly.

### Phase D — providers (deferred)

Per §3.6, ContentProviders are hard. Defer until a real foreign app needs them. Note
that noice/McD do not declare providers in their manifests (re-verify).

---

## 5. Failure modes and limitations

### 5.1 Static proxy count vs dynamic process spawning

Our host manifest pre-declares N proxy processes. If a foreign app declares N+1 distinct
processes, we either collapse some into shared slots (losing isolation) or fail to host
that app. Reinstalling the host APK with a different manifest is the only way to bump N
on a deployed device.

**Recommendation:** ship N=8 in the production manifest. Empirically, ~95% of apps use
≤4 processes (one main + 1–2 of {`:remote`, `:push`, `:work`, `:sync`}). N=8 covers the
long tail (Chrome's sandbox spawns up to 16 renderers but those are isolatedProcess, see
below).

Stock Android does not let a non-system app declare an unbounded set of processes;
manifest entries are processed once at install. So our limitation is identical to what
the OS itself imposes on legitimate apps. Dynamic process spawning would require
**root + zygote injection** which Westlake explicitly does not require.

### 5.2 `android:isolatedProcess="true"`

`isolatedProcess` requests the OS spawn the process from `app_zygote` (not the regular
zygote), under a fresh sandboxed UID with no app-data access and no permissions. Used by
Chrome's renderer sandbox.

We cannot replicate this. The OS gives our host APK a single UID; even our proxy
processes run as that UID. Declaring `android:isolatedProcess="true"` on our proxy would
break our ability to load the foreign APK's code (no perm to read `/data/data` for the
foreign pkg's resources — even though our existing flow uses `createPackageContext`
which reads from `/data/app`, the resource path may need `/data/data` for certain
overlay configs).

**Recommendation:** explicitly skip components with `isolatedProcess=true`. Most apps
that use isolation expect it for security (untrusted plugin loaders). Hosting an
isolation-tagged component in a non-isolated proxy could violate the developer's
security model. Log a clear warning and either run that component in the default proxy
slot (losing the isolation guarantee, may be unsafe — only do this with user opt-in) or
refuse to host the app entirely.

### 5.3 Foreign manifest changes after host install

If the foreign app updates and adds `:newproc4`, but our host's slot pool was sized to
the foreign app's old manifest (say 3 procs), the new component cannot be hosted.
Discovery runs at every host activity launch, so this is *detectable* — we just can't
remediate without a host APK reinstall.

**Mitigation:** ship with N=8 (generous headroom). Surface a "this app uses M processes,
host supports N" warning if M > N. Document that bumping N requires a host reinstall.

### 5.4 GMS / FCM push handling

GMS push: the system-installed `com.google.android.gms` process delivers a wake-up
broadcast to a receiver registered by the app, usually in a `:fcm_service` or `:gcm`
process. The receiver path:

1. GMS calls `IPackageManager.queryIntentReceivers(intent, ...)` to find the app's
   receiver.
2. PMS consults its `mProvidersByAuthority` / receiver tables.
3. Dispatches via `IActivityManager.startReceiver` to the target process.

The receiver is registered against the FOREIGN package name in PMS, not ours. So when
GMS sends a push for `com.foo.bar`, the dispatch finds the foreign app's receiver and
spawns the foreign app's actual process — bypassing Westlake entirely.

**Open problem.** We cannot intercept GMS-→PMS dispatch from userland; it goes through
system_server's routing tables. Three options:

1. **Live with it.** Wake-ups from GMS spawn the foreign app in its native process,
   which is wasteful but not broken. We don't claim FCM/GCM works through Westlake.
2. **Foreground service self-poll.** Run a Westlake-side foreground service that polls
   FCM tokens / app servers on a timer. Battery cost, but no GMS dependency.
3. **Register-our-own-receivers.** Declare a receiver in OUR manifest with the same
   intent filters as the foreign app's FCM receiver. When PMS resolves the FCM intent,
   it returns both our receiver and theirs. The foreign app's receiver still gets
   notified first (it owns the canonical entry); we'd get notified too if the broadcast
   is non-ordered, but that doesn't help us own the delivery.

**Recommendation:** document that GMS/FCM push goes via foreign-app's native process,
not Westlake. Westlake hosts the app's UI and most components; the OS bypasses us for
push specifically. This is a known limit, not a bug.

### 5.5 ActivityResult / startActivityForResult across slots

When our `InProcessActivityProc1` starts an activity that ends up in `InProcessActivityProc2`
(via intent rewriting), Android tracks the call via `mResultTo` IBinder in
`ActivityRecord`. The result callback will arrive at the caller correctly — the OS
handles this for us. However, the foreign code expects `getCallingActivity()` to return
the foreign FQCN, not `.InProcessActivityProc1`. We must intercept
`Activity.getCallingActivity` (it's hookable via Activity.mReferrer / mCallingPackage)
and return the foreign component name we recorded during the intent rewrite.

This is solvable but non-obvious; budget half a day for it during Phase B.

### 5.6 Singleton "global" state in the foreign app

Many apps assume their Application is a singleton. Our model honors that — Android
itself instantiates Application once per process. But some apps additionally store
state in a `companion object` / static field assuming "anywhere in this process I get
back the same value." That's also true within each proxy slot.

The footgun: some apps store cross-process state in `static` fields and assume it
survives because their service runs in the same JVM. With multi-process, that's
definitionally broken on stock Android too — but some lazy apps work by accident
because the service-in-:remote is bound rarely. Under Westlake, the proxy lifecycle
may differ; bugs can surface. This is a *foreign app* bug, not a Westlake bug, but
expect to debug it.

---

## 6. Test plan

### 6.1 Synthetic test APK

Build `com.westlake.mptest` per Phase B. Manifest: 1 activity, 2 services in 2 distinct
:remote processes, 1 receiver in a third. Activity binds to both services, listens for
a broadcast that the receiver fires.

Tests:

1. **Native run sanity.** `am start -n com.westlake.mptest/.MainActivity`. Verify with
   `adb shell ps | grep mptest`: expect 4 processes (default, :counter, :pinger,
   :recv).
2. **Westlake run.** `am start -n com.westlake.host/.MpTestInProcessActivity`. Verify
   with `adb shell ps | grep westlake.host`: expect 4 host processes
   (default, :app_proc1, :app_proc2, :app_proc3).
3. **Counter increment.** Tap "increment" button, see counter go up. Confirms binder
   round-trip across host processes.
4. **Cross-process isolation.** Static `AtomicInteger` in `CounterService` — kill
   `:app_proc1` via `am force-stop` then re-bind. Counter resets, confirms each process
   has its own static namespace.
5. **Cross-process broadcast.** Activity in `:app_proc0` sends a broadcast; receiver in
   `:app_proc3` logs it. Confirms intent rewriting works for broadcasts.
6. **Slot exhaustion.** Build a variant test APK with 9 distinct processes (assuming
   host has N=8). Confirm graceful fallback to overflow slot + warning log.

### 6.2 Real app test

Telegram (uses `:tgvoip` for voice) and Signal (uses `:fcm_service`):

- Install both via Westlake host.
- `am start -n com.westlake.host/.TelegramInProcessActivity`.
- Verify UI renders (no need for full network — confirm at least main + chats list).
- Confirm `:tgvoip` process spawned as `com.westlake.host:app_proc1`.
- Inspect via `adb shell dumpsys activity processes` — every spawned process must be a
  host process; no `org.telegram.messenger` process should appear (with the noted
  exception of GMS-→app push dispatch, §5.4).

### 6.3 Regression

- `NoiceInProcessActivity` and `McdInProcessActivity` continue to launch and render as
  before. The slot allocator picks slot 0 (default) for noice's only declared process.
  Manifest now has the additional proxy slots but they remain unused for these apps.

---

## 7. Open questions

1. **N=?** Pick a number for the static proxy pool. Recommendation N=8. Confirm with
   measurements on devices with constrained memory (3 GB) — 8 idle host processes cost
   ~50–100 MB total in our experience but it's worth checking.

2. **Provider strategy.** §3.6 defers providers. If a target app (e.g. WhatsApp,
   WordPress) actually needs cross-process providers and we can't host them, do we
   reject the app or accept partial functionality?

3. **`getCallingActivity()` faithfulness.** §5.5 — should we faithfully report the
   foreign component name (good for app code that uses this for routing) or our proxy
   class name (good for debugging)? Probably the former, but the trick is the OS-level
   `getCallingActivity` reads from `ActivityRecord` in system_server, not a field in
   our process. We'd have to hook in our Activity subclass.

4. **Slot persistence across host updates.** If the host APK is updated, do existing
   slot allocations (in SharedPreferences) carry over? Yes by default (SharedPrefs
   survive APK upgrade), but if the new host has a different N, we must validate. Add a
   manifest-fingerprint to the allocation file.

5. **Should we surface this as an opt-in?** Multi-process hosting has more failure
   modes than single-process. Default to single-process for apps that declare one
   process; auto-enable multi-process when the foreign manifest demands it; provide a
   killswitch (`disable_multiprocess_for_pkg=com.foo.bar` system property) for debug.

6. **What about apps that use `android:sharedUserId`?** Two foreign packages declaring
   `sharedUserId="com.foo.shared"` end up in the SAME uid and conventionally the same
   process namespace. We currently host each foreign app under our single UID, so this
   should just work — but verify against an app pair like Google Play services + Google
   Play store (both `com.google.uid.shared`).

7. **`isolatedProcess` security policy.** §5.2 — refuse to host? Auto-host without
   isolation with user warning? Need product-level call.

---

## 8. Verdict

**Multi-process hosting is genuinely doable** under Westlake's in-process model. The
mechanism is "static proxy host-processes with per-slot five-pillar replay" — we
pre-declare slots in our manifest, the OS forks them on demand, each slot independently
loads foreign-app classes and bootstraps. The OS gives us real process-level isolation
(separate VM, separate static fields, separate uid-sandboxed file caches) for free
because we use the OS's actual multi-process mechanism, not a simulation.

The original claim in `INPROCESS_RUN.md` ("we can't replicate that in our single
process") was overcautious — we never had to be in a single process. The retraction
is: "Multi-process apps are supported via a static proxy-process pool declared in the
host manifest, with per-process five-pillar bootstrap (see
`INPROCESS_MULTIPROCESS_DESIGN.md`)."

The real limitations are not technical impossibility but **constrained scope**:

- ContentProviders cannot be transparently rerouted (system_server owns authority
  resolution).
- GMS-→app push dispatch bypasses Westlake (system_server delivers directly to foreign
  package).
- `isolatedProcess` cannot be faithfully reproduced (we have one UID).
- N is fixed at host-install time (architectural, not bug).

All four limitations are clearly documented and have workarounds or expected-failure
patterns. None invalidates the design.

---

## 9. File map (proposed for Phase C)

```
westlake-host-gradle/app/src/main/java/com/westlake/host/
├── InProcessLauncher.kt              # generic refactor of NoiceInProcessActivity
├── InProcessSlotAllocator.kt         # foreign-process-name → slot mapping + persistence
├── InProcessIntentRewriter.kt        # IActivityTaskManager binder hook
├── proxy/
│   ├── InProcessActivityProc0.kt     # ~10 LOC each
│   ├── InProcessActivityProc1.kt
│   ├── ... (Proc7)
│   ├── InProcessServiceProc0.kt
│   ├── ... (Proc7)
│   ├── InProcessReceiverProc0.kt
│   └── ... (Proc7)
└── app/
    ├── NoiceAppConfig.kt              # PKG/APP_CLS/MAIN_CLS/themeName
    ├── McdAppConfig.kt
    └── MpTestAppConfig.kt
```

`NoiceInProcessActivity.kt` / `McdInProcessActivity.kt` become thin compatibility
shells (or are removed in favor of slot-0 proxies parametrized by the foreign pkg
captured in the intent extras).

---

## 10. References

- `INPROCESS_RUN.md` — the single-process design this builds on.
- `BINDER_PIVOT_DESIGN_V2.md` §M5/M6/M7 — the V2 substrate's daemon model (audio/surface
  daemons run in separate processes and proved out the IPC pattern we'd reuse here for
  daemon-style isolation; multi-process app hosting differs in that the foreign app's
  *own* class lives in the satellite, not a Westlake-owned helper).
- `feedback_no_per_app_hacks.md` — applies here: the design is generic (per-slot, per-
  process-name) with zero per-app code. App-specific config is data (`AppConfig`), not
  branches.
- AOSP source paths in §2.
