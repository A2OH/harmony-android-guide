# M4-PRE — Minimum Context plumbing

**Date:** 2026-05-12
**Predecessor:** W2-discover (M4_DISCOVERY.md)
**Successor:** services.jar/Guava classpath fix, then M4a / M4-power dispatch
**Author:** M4-PRE agent

This document records what we built for M4-PRE, why we built it that way,
and what the next discovery iteration revealed.

---

## 1. Goal recap

W2-discover (`docs/engine/M4_DISCOVERY.md`) established that noice's
`Hilt_NoiceApplication.onCreate()` was failing at
`Context.getPackageName()` on a null `mBase`, before any Binder
transaction reached an implementation. M4-PRE's job: provide just enough
Context plumbing that `Application.attachBaseContext(westlakeContext)`
succeeds and `getPackageName()` returns the real package name.

---

## 2. Approach

Option (5a) from M4_DISCOVERY §5: a `WestlakeMinimalContext` direct
subclass of `Context`. Not the heavyweight `ContextImpl` + `LoadedApk` +
`ActivityThread` triple that AOSP uses; not the full
`ActivityThread.main()` boot-from-system_server replication.

The discover doc estimated this as "S — 1 day". Actual elapsed: ~3 hours
(see §7).

### 2.1 Class layout

```
shim/java/com/westlake/services/WestlakeContextImpl.java  (NEW — 628 LOC)
```

The `com.westlake.services` package is new; created per the
BINDER_PIVOT_DESIGN.md §3.2 layout note that Westlake-owned Java service
implementations live under `com.westlake.services.*`. M4a-e service
implementations will join this package as `WestlakeActivityManagerService`,
`WestlakePackageManagerService`, etc.

### 2.2 Why extend Context directly rather than ContextWrapper

`ContextWrapper` requires a non-null `mBase` Context at construction:

```java
public ContextWrapper(Context base) {
    attachBaseContext(base);   // throws IllegalStateException if null
}
```

There's no Context we can delegate to. Using ContextWrapper would
require ANOTHER concrete Context as the base — exactly the problem
we're solving.

Direct subclass it is. The downside: AOSP's `Context` is abstract with
~131 abstract methods on Android 11 (the SDK level we build against);
on Android 16 (runtime) it may have a few more. Every abstract method
must be implemented in `WestlakeContextImpl` or `new WestlakeContextImpl()`
throws `InstantiationError` at allocation time.

### 2.3 Why call attachBaseContext rather than attach

AOSP's `Application.attach(Context)` does:

```java
final void attach(Context context) {
    attachBaseContext(context);
    mLoadedApk = ContextImpl.getImpl(context).mPackageInfo;
}
```

The second line is a hard cast to `ContextImpl`. Our `WestlakeContextImpl`
is NOT a `ContextImpl` so that cast throws `ClassCastException`. The
`mBase` field DOES get set by line 1 before the exception propagates,
but the harness sees the exception and the wrapper code's intent is
unclear — easier to bypass `attach()` entirely.

`attachBaseContext(Context)` is `protected` on ContextWrapper but
accessible via reflection with `setAccessible(true)`. The harness calls
it directly. `Application.mLoadedApk` stays null, which is fine — the
field is `@hide` API and isn't read during Hilt DI bootstrap.

### 2.4 Compile-time vs run-time class hierarchy

Compile-time (`shim/java/android/content/Context.java`, our concrete
stub):
```
WestlakeContextImpl
   ↓ extends
android.content.Context (CONCRETE in shim, ~95 default impls)
   ↓ extends
java.lang.Object
```

Run-time (`framework.jar`'s abstract Context wins because shim's
Context is in `scripts/framework_duplicates.txt` and gets stripped from
`aosp-shim.dex`):
```
WestlakeContextImpl
   ↓ extends
android.content.Context (ABSTRACT, 131 abstract methods)
   ↓ extends
java.lang.Object
```

At compile time, `extends Context` requires no abstract-method
implementations (shim's Context is concrete). At run time, the JVM checks
that our class has implementations matching every framework.jar abstract
method signature by name+args; if any are missing, `new
WestlakeContextImpl(...)` throws `InstantiationError`. So even though
javac says we're done, we MUST write every abstract method implementation
into our class explicitly. We do.

(The class file was compiled with no `@Override` annotations on the
methods that aren't in shim's Context — those would have been compile
errors. The runtime JVM treats methods structurally; absence of `@Override`
is irrelevant for runtime override behaviour.)

### 2.5 Method implementations: real, default, defer

| Category | Count | What they return | Notes |
|---|---|---|---|
| Real impls | ~12 | meaningful values | `getPackageName`, `getOpPackageName`, `getBasePackageName`, `getApplicationInfo`, `getClassLoader`, `getApplicationContext`, `getResources`, `getAssets`, `getTheme`, `getMainLooper`, `getDataDir`/`getFilesDir`/`getCacheDir`/`getCodeCacheDir`/`getNoBackupFilesDir`/`getDir`/`getFileStreamPath`/`getSharedPreferencesPath`, `getDisplayId` |
| File I/O | ~6 | usable | `openFileInput`, `openFileOutput`, `deleteFile`, `fileList`, `databaseList`, `getDatabasePath` |
| Permissions | ~12 | `PERMISSION_GRANTED` / no-op | sandbox runs as su/system uid; effectively granted |
| Defer | ~30 | null/false/no-op | Activity/Service/Broadcast/StartService — wait for M4a IActivityManager |
| Unsupported | ~70 | null/false/no-op/empty | Wallpapers, SQL databases, content resolvers, external storage. Surface as discover failures if hit. |

`getSystemService(String)` currently returns `null` for every name. The
plan is to switch this to `ServiceManager.getService(name)` + a manager
factory once M4a-e binder services come online, but doing it now would
return `IBinder` instead of the expected `Manager` class — better to wait
until M4a's `IActivityManager.Stub` and a `WestlakeActivityManager`
wrapper exist together.

---

## 3. Verification

### 3.1 Re-run command

```bash
ADB="/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3"
cd /home/dspfac/android-to-openharmony-migration

bash scripts/build-shim-dex.sh                            # rebuilds aosp-shim.dex with WestlakeContextImpl
bash aosp-libbinder-port/build_discover.sh                # rebuilds NoiceDiscoverWrapper.dex

$ADB push aosp-shim.dex /data/local/tmp/westlake/
$ADB push aosp-libbinder-port/out/NoiceDiscoverWrapper.dex /data/local/tmp/westlake/dex/

$ADB shell "su -c 'bash /data/local/tmp/westlake/bin-bionic/noice-discover.sh'" \
    2>&1 | tee /tmp/noice-discover-postM4PRE.log
```

### 3.2 Phase outcomes (2026-05-12 14:58:39 UTC, run 1)

```
PHASE A: 0/70 services resolved
PHASE B: 5/7 classes loadable                       (unchanged — Hilt_* not present post-R8 obfuscation)
PHASE C: PASSED — NoiceApplication instantiated
PHASE D: PASSED (attachBaseContext with WestlakeContextImpl)
   built WestlakeContextImpl: com.westlake.services.WestlakeContextImpl@8db5f6a
   post-attach Application.getPackageName() -> com.github.ashutoshgngwr.noice
PHASE E: FAILED at NoSuchMethodError on Guava ImmutableMap
PHASE F: bus error during ActivityThread.<clinit> (unrelated; was Bus-erroring pre-M4-PRE too)
```

### 3.3 New failure

```
java.lang.NoSuchMethodError:
  No InvokeType(0) method h(Lz6/b;)Lcom/google/common/collect/ImmutableMap;
  in class Lcom/google/common/collect/ImmutableMap;
  (declaration of 'com.google.common.collect.ImmutableMap' appears in
   /data/local/tmp/westlake/services.jar!classes3.dex)
```

This is a **classpath collision** between services.jar's Guava and
noice's bundled Guava (which has R8-obfuscated method names like `h()`).
Not a Context plumbing problem. Not a binder problem. Diagnosed in
`M4_DISCOVERY.md §12.2`.

---

## 4. The next step

The recommended next action is to remove `services.jar` from the
`noice-discover.sh` BCP. In real Android, app processes don't load
services.jar — only system_server does. Our discovery sandbox put it on
BCP defensively (to keep framework Singletons happy), but it leaks
internal libraries that conflict with the app's own.

Estimated effort: 1-2 hours including a re-run.

If removing services.jar surfaces a different framework issue (most
likely: a Singleton that was finding system_server internals there can't
anymore), the right fix is to investigate per-Singleton, not blanket
re-add services.jar.

---

## 5. Anti-patterns avoided

* No per-app branches in `WestlakeContextImpl`. packageName, apkPath,
  dataDir, targetSdk are constructor args.
* No comprehensive Context impl beyond what the JVM requires for
  instantiation. Most methods are null/0/false stubs.
* No speculative ApplicationInfo population. Only `packageName`,
  `processName`, `sourceDir`, `publicSourceDir`, `dataDir`,
  `targetSdkVersion`, `uid` are set.
* Did not pull in AOSP's `ContextImpl` (option 5b) — that path would
  have required partial M4c IPackageManager which is the wrong layering
  for a prerequisite.
* Did not drive `ActivityThread.main()` (option 5c) — would have been a
  4-5 day epic; out of scope for M4-PRE.

---

## 6. Files touched

```
NEW:    shim/java/com/westlake/services/WestlakeContextImpl.java     628 LOC
EDIT:   aosp-libbinder-port/test/NoiceDiscoverWrapper.java           +37 / -13 LOC
NEW:    aosp-libbinder-port/M4_PRE_NOTES.md                          (this file)
EDIT:   docs/engine/M4_DISCOVERY.md                                  +110 LOC (§12)
```

No changes to `art-latest/`, `aosp-libbinder-port/native/`,
`aosp-libbinder-port/out/`, `shim/java/android/`, or `shim/java/androidx/`.

---

## 7. Time spent

| Phase | Time | Activity |
|---|---|---|
| Read M4_DISCOVERY §5 + BINDER_PIVOT_DESIGN §3.2 + Phase 1 status | 25 min | Confirmed option (5a) was the recommended path |
| Investigate Context/ContextWrapper/Application internals (AOSP source) | 30 min | Established `attach()` does ContextImpl cast → use `attachBaseContext()` instead |
| Audit shim Context concrete methods vs framework.jar abstract methods | 20 min | Decided to extend Context directly, override every abstract; document the compile/runtime hierarchy mismatch |
| Write `WestlakeContextImpl.java` | 60 min | 628 LOC, structured by category |
| Build/iteration loop (@Override annotation cleanup, throws clause fix, sed pass) | 30 min | Compile cleanly against shim's signatures while still being a runtime-valid Context impl |
| Wire up NoiceDiscoverWrapper buildProxyContext + attachBaseContext path | 20 min | Reflective ctor invocation; reflective attachBaseContext call |
| Build, push, run, capture output | 10 min | First-try success on PHASE D pass |
| Document M4_DISCOVERY §12 + M4_PRE_NOTES.md | 30 min | This file + the §12 append |
| **Total** | **~3h** | within the M4_DISCOVERY 1-day estimate |

---

## 8. What changed re: future agents

Future M4a-e agents do NOT need to think about Context plumbing. The
contract for their Java service classes is:

```java
public class WestlakeActivityManagerService extends IActivityManager.Stub {
    // implement onTransact / public methods
    // NO Context concerns; this class is constructed at engine boot and
    // registered via ServiceManager.addService("activity", this).
}
```

The `getSystemService(String)` switch in WestlakeContextImpl will route
"activity" to a binder lookup once the M4a service is online (TODO in
M4-PRE2, after the services.jar/Guava fix).
</content>
</invoke>
