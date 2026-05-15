# M3 Notes — dalvikvm + libbinder + servicemanager Integration

**Status (M3, M3-finish):** **COMPLETE.**  Java-side end-to-end test passes:
`HelloBinder.dex` runs on bionic-static dalvikvm, looks up
`westlake.test.echo` through `android.os.ServiceManager.getService`, and
receives a non-null `NativeBinderProxy{handle=...}`.  See "Resolution"
section below for the static-JNI-link solution that unblocked execution.

**Status (M3++):** **COMPLETE.**  Java `extends Binder` services register
themselves via `ServiceManager.addService`; the same-process
`IXxx.Stub.asInterface(IBinder)` returns the SAME Java object via
`queryLocalInterface`.  No Parcel JNI cluster needed.  See "M3++"
section at the bottom.

**Last updated:** 2026-05-12 (M3++)

## Path choice: A (bionic rebuild) + A2 (replace shim ServiceManager.java)

Two orthogonal decisions had to be made.

### Path A vs Path B (which libbinder runs in dalvikvm?)

**Chose Path A: rebuild libbinder/servicemanager against bionic NDK.**

The phone's dalvikvm is bionic-linked (per `reference_phone_connection.md`).
Mixing musl libbinder with bionic dalvikvm crashes on pthread/TLS conflicts.
We needed a bionic libbinder.

Alternatives considered:
- **Path B (use phone's stock `/system/lib64/libbinder.so`)**: rejected
  for clarity. The phone runs Android 15 LineageOS; our servicemanager is
  cross-compiled from android-16.0.0_r1 sources. While IServiceManager AIDL
  is stable across these versions, doing a clean cross-compile of OUR libbinder
  for bionic gives us full control over which kernel binder UAPI version is
  in use (we override with a newer binder.h that has BR_FROZEN_BINDER etc.)
  and ensures the IPC payload format matches exactly between libbinder and
  servicemanager.

**Implementation:** extended `aosp-libbinder-port/Makefile` with `*-bionic`
sibling targets that compile the same source against `aarch64-linux-android33`
target via NDK r25 (`/home/dspfac/android-sdk/ndk/25.2.9519653/...`).
Outputs go to `out/bionic/`:
- `out/bionic/libbinder.so` — 1.7 MB stripped
- `out/bionic/servicemanager` — 256 KB stripped
- `out/bionic/test/binder_smoke` — 70 KB
- `out/bionic/test/sm_smoke` — 71 KB
- `out/bionic/libandroid_runtime_stub.so` — 45 KB (M3 JNI bridge, see A2)

The musl variant under `out/` is **unchanged** and continues to work for
Phase 2 OHOS.

Both variants share the same:
- AIDL-generated .cpp/.h (target-independent)
- Source files (aosp-src/libbinder, deps-src/libutils_binder, libcutils, libbase)
- `-U__ANDROID__` flag (forces source through the "host" code paths so
  we don't drag in vintf/perfetto/SELinux dependencies — same trick the
  musl path uses)
- Updated kernel binder.h (newer than NDK r25's stale copy; placed in
  `bionic-overlay/include/linux/android/binder.h`)

Differences (bionic only):
- NDK r25 toolchain (`/home/dspfac/android-sdk/ndk/25.2.9519653/...`)
- API level 33 (needed for `AParcel_reset`, `AParcel_getDataSize` in NDK
  `<android/binder_parcelable_utils.h>`)
- Defines `ANDROID_FDSAN_OWNER_TYPE_{PARCEL,NATIVE_HANDLE}` to fill in
  enum values added after NDK r25 was released
- Stub log macro patched in `aosp-src/libbinder/liblog_stub/include/log/log.h`
  to bypass token-paste expansion issue with `<syslog.h>` (pulled in by
  NDK's `<android/binder_internal_logging.h>`)

### Path A1 vs Path A2 (real vs shim ServiceManager.java)

**Chose Path A2: replace shim/java/android/os/ServiceManager.java with a
new JNI wrapper.**

The milestone deliverable described both alternatives:
- **A1**: Remove shim ServiceManager.java, let AOSP's framework.jar provide
  ServiceManager.java; implement `libandroid_runtime.so` stub covering all
  the JNI methods (BinderInternal.getContextObject, JavaBBinder, BinderProxy,
  Parcel native methods, etc.).
- **A2**: Replace shim ServiceManager.java with a fresh in-process
  implementation that goes Java -> JNI -> libbinder directly.

A1 is ~1600 lines of JNI glue in just `android_util_Binder.cpp` (AOSP), plus
companion classes.  AOSP's ServiceManager.java calls `BinderInternal
.getContextObject()` (JNI) then transacts via the returned BinderProxy.
That entire stack needs to be live.

A2 is ~200 lines: a Java class with native methods that call directly
into our libbinder's C++ API (`defaultServiceManager()->getService(...)`).
No need for BinderProxy class, no need for the full Parcel JNI cluster.

**A2 is the M3 choice** — pragmatic, ~10x less code, and architecturally
maps cleanly to A1 later (the same `libandroid_runtime_stub.so` can grow
into a fuller libandroid_runtime when M4 needs per-service AIDL stubs).

**Implementation:**
- `aosp-libbinder-port/native/libandroid_runtime_stub.cc` — new file.
  JNI methods `Java_android_os_ServiceManager_native{GetService,
  ListServices, AddService, IsBinderAlive, ReleaseBinder, BinderDescriptor}`.
- `shim/java/android/os/ServiceManager.java` — replaced. Public API
  matches AOSP shape (getService / listServices / addService).  Internally
  uses `nativeGetService(name) -> long handle`, wraps in a
  `NativeBinderProxy implements IBinder` for caller-side use.
- `scripts/framework_duplicates.txt` — removed the three
  `android/os/{ServiceManager, IServiceManager, ServiceManagerNative}`
  entries so the shim no longer gets stripped from `aosp-shim.dex`.
  framework.jar's classes of the same name are shadowed.
- `shim/java/android/os/ServiceManagerNative.java` — slimmed down; kept
  as a compatibility shell that delegates `asInterface()` back through
  our new ServiceManager.

A2 limitations (M3 scope):
- `IBinder.transact()` throws UnsupportedOperationException.  Real
  per-service binder transactions aren't implemented in M3 — that's M4's
  responsibility (per-service AIDL stub support in
  `libandroid_runtime_stub`).
- `addService()` from Java creates a fresh C++ BBinder under the name but
  doesn't bind it back to a Java-side BBinder subclass.  M3's tests use
  the external `sm_smoke` binary to register services.
- `waitForService()` doesn't wait — falls through to `getService()`.

## Verification

### sm_smoke (bionic) round-trip — PASS

Run on the OnePlus 6 (kernel 4.9.337, Android 15, root via Magisk).

```
$ adb shell "su 1000 -c '...sm_smoke'"
[sm_smoke/child pid=6723] opening /dev/vndbinder
[sm-stub] WaitForProperty(servicemanager.ready=true) -> immediate true
[sm_smoke/child] addService("westlake.test.echo") -> status=0 (ok)
[sm_smoke/parent pid=6722] opening /dev/vndbinder
[sm-stub] WaitForProperty(servicemanager.ready=true) -> immediate true
[sm_smoke/parent] defaultServiceManager() OK
[sm_smoke/parent] listServices() returned 2 names:
    - manager
    - westlake.test.echo
[sm_smoke/parent] listServices(): found westlake.test.echo — ok
[sm_smoke/parent] checkService("westlake.test.echo"): non-null binder matches (remote BpBinder at 0x7a6410b320)
[sm_smoke/parent] addService("westlake.test.echo.parent") -> status=0 (ok)
[sm_smoke/parent] PASS: all checks ok. Reaping child 6723
```

Same exact PASS shape as the musl variant (M2).  Binary sizes:
- `out/bionic/libbinder.so`        1.7 MB stripped (vs musl: 803 KB —
  bionic is bigger because it embeds libc++ statically; could be cut by
  using `-stdlib=libc++` against libc++_shared.so on the device, deferred)
- `out/bionic/servicemanager`      256 KB stripped (vs musl: 223 KB)
- `out/bionic/test/sm_smoke`       71 KB

### dalvikvm + HelloBinder.dex — BLOCKED on dlopen stub

The Java-side test was prepared but cannot be exercised on this dalvikvm
build.  The root cause: **dalvikvm's `dlopen` is a stub that returns NULL.**

`/home/dspfac/art-latest/build-bionic-arm64/bin/dalvikvm` is statically
linked against bionic's `libc.a`/`libdl.a`.  Bionic's static libdl is a
stub library — its `dlopen` symbol literally compiles to:

```asm
0000000000e463fc <dlopen>:
    hint  #34        ; bti c
    mov   x0, xzr    ; return NULL
    ret
```

The existing comment in `art-latest/stubs/openjdk_stub.c` confirms this:
> "bionic's static libdl path reports 'libdl.a is a stub'"

When `Runtime_nativeLoad` (the JNI native for `System.loadLibrary()`)
runs, it calls `dlopen(path, RTLD_NOW|RTLD_GLOBAL)`, gets NULL, returns
an error string to Java, and the static initializer of our shim
ServiceManager.java throws `UnsatisfiedLinkError`.

**Reproduction:**
```
$ adb shell "su -c 'bash /data/local/tmp/westlake/bin-bionic/m3-dalvikvm-boot.sh test'"
...
[PF202N] Runtime_nativeLoad path=/data/local/tmp/westlake/lib-bionic/libandroid_runtime_stub.so
(silent failure — no JNI_OnLoad fires)
HelloBinder exits with code 10 (libLoaded=false in static{} block)
```

**Existing workarounds in the codebase** (via `art-latest/stubs/`):
- All Android system .so libraries are *baked into the dalvikvm binary*
  statically: `ohbridge_stub.c` (1700 LOC) provides JNI methods for
  OHBridge by name-matching on `path` containing "oh_bridge" inside
  `Runtime_nativeLoad`.
- Other expected libraries (icu_jni, openjdk, javacore) are short-circuited
  to "null = success" — they're statically linked and don't need real
  dlopen.
- Only one library (`librealm-jni`) is given the "stub-success" treatment
  for compat reasons.

**There is no in-band mechanism to add a NEW system library to dalvikvm
without modifying art-latest source.**

## Resolution (M3-finish, 2026-05-12)

**Chose Option 1: statically link the binder JNI surface into the
dalvikvm binary.**  Mirrors the existing OHBridge / openjdk / javacore
stub pattern — `art-latest/stubs/` is project-local stub code (not
AOSP-derived ART runtime), so extending it preserves the spirit of
"don't break the AOSP tree".

### What changed

1.  **New file `art-latest/stubs/binder_jni_stub.cc` (≈230 LOC).**
    Contains the same six JNI methods previously in
    `aosp-libbinder-port/native/libandroid_runtime_stub.cc`:

    - `Java_android_os_ServiceManager_nativeGetService`
    - `Java_android_os_ServiceManager_nativeListServices`
    - `Java_android_os_ServiceManager_nativeAddService`
    - `Java_android_os_ServiceManager_nativeIsBinderAlive`
    - `Java_android_os_ServiceManager_nativeReleaseBinder`
    - `Java_android_os_ServiceManager_nativeBinderDescriptor`

    Plus two test-harness helpers used by HelloBinder.java:
    - `Java_HelloBinder_println`
    - `Java_HelloBinder_eprintln`

    Plus `JNI_OnLoad_binder_with_cl(JavaVM*, jobject classLoader)` —
    explicitly calls `RegisterNatives` on `android.os.ServiceManager`
    and `HelloBinder` using `ClassLoader.loadClass()`.  The classloader
    argument is critical: ART's `JNIEnv::FindClass` defaults to the
    bootclasspath ClassLoader, but our target classes live in the
    `-cp` ClassLoader (aosp-shim.dex / HelloBinder.dex).

2.  **New Makefile target `libbinder_full_static-bionic` in
    `aosp-libbinder-port/Makefile`.**  Produces
    `out/bionic/libbinder_full_static.a` (2.6 MB) — a single static
    archive bundling libbinder + AIDL + libutils + libcutils + libbase
    objects.  Linked with `-Wl,--whole-archive` into dalvikvm.

3.  **Modified `art-latest/Makefile.bionic-arm64`.**  Adds the
    `binder_jni_stub.cc` compile rule (C++17, full libbinder include
    path) and threads `libbinder_full_static.a` into the static
    `link-runtime` link line.  New dalvikvm size: 27 MB (was 26 MB,
    +1 MB).

4.  **Modified `art-latest/stubs/openjdk_stub.c::Runtime_nativeLoad`.**
    Added an `android_runtime_stub` short-circuit (parallel to the
    `oh_bridge` path): instead of `dlopen` (which is a stub returning
    NULL on bionic-static), we call `JNI_OnLoad_binder_with_cl(vm,
    classLoader)` to RegisterNatives, then return null = success.

5.  **Updated `scripts/framework_duplicates.txt`.**  Removed these
    classes so they live in `aosp-shim.dex` instead of being stripped:
    - `android/os/IBinder`
    - `android/os/IInterface`
    - `android/os/Parcel`
    - `android/os/Binder`
    - `android/os/RemoteException`

    These are the minimum binder-API types needed for
    `ServiceManager.NativeBinderProxy implements IBinder` to verify and
    resolve in the absence of framework.jar on the bootclasspath.  Other
    `android/os/Parcel*` and `android/os/ParcelUuid` stay stripped (the
    shim's Parcel works with stubbed Parcelable signatures because the
    methods that mention Parcelable are never called).

### Verification

```
ADB="..."; cd /home/dspfac/android-to-openharmony-migration

# 1. JNI symbols baked into dalvikvm
$ /home/dspfac/android-sdk/ndk/25.2.9519653/.../bin/llvm-nm \
    art-latest/build-bionic-arm64/bin/dalvikvm | \
    grep Java_android_os_ServiceManager_native
0000000000dfadd8 T Java_android_os_ServiceManager_nativeAddService
0000000000dfafa0 T Java_android_os_ServiceManager_nativeBinderDescriptor
0000000000dfa860 T Java_android_os_ServiceManager_nativeGetService
0000000000dfaf2c T Java_android_os_ServiceManager_nativeIsBinderAlive
0000000000dfabec T Java_android_os_ServiceManager_nativeListServices
0000000000dfaf5c T Java_android_os_ServiceManager_nativeReleaseBinder

# 2. End-to-end Java -> JNI -> libbinder -> /dev/vndbinder -> servicemanager
$ $ADB shell "su -c 'bash /data/local/tmp/westlake/bin-bionic/m3-dalvikvm-boot.sh test'"
[m3-boot] servicemanager up
[m3-boot] sm_registrar ready
[m3-boot] dalvikvm exit code: 0
WLK-binder-jni: JNI_OnLoad_binder: android.os.ServiceManager natives: 6/6
WLK-binder-jni: JNI_OnLoad_binder: HelloBinder natives: 2/2
HelloBinder: starting M3 end-to-end test
WLK-binder-jni: ensureInit: opening /dev/vndbinder — ok
WLK-binder-jni: nativeListServices: 2 names
HelloBinder: listServices() returned 2 entries:
  [0] manager
  [1] westlake.test.echo
HelloBinder: listServices contains "manager" — SM reachable
WLK-binder-jni: nativeGetService("westlake.test.echo") -> 0x7b81614500
HelloBinder: getService("westlake.test.echo") -> NativeBinderProxy{...} (non-null)
HelloBinder: PASS
HelloBinder: exiting with code 0
```

### Notes for whoever resumes M3 cleanup or starts M4

-   The old `aosp-libbinder-port/native/libandroid_runtime_stub.cc` and
    `aosp-libbinder-port/out/bionic/libandroid_runtime_stub.so` are
    now redundant in the dalvikvm path.  Kept as reference for the M4
    migration to A1 (real Android `libandroid_runtime` shape).  They
    can be deleted once nobody depends on them — note the
    `libandroid_runtime_stub-bionic` Makefile target still builds them
    if invoked.

-   `Runtime_nativeLoad` short-circuit handles `android_runtime_stub`
    by name match.  When M4 wants more native libs baked into dalvikvm
    (e.g. `libandroid_runtime.so` from real Android), the same pattern
    can be extended.

-   Adding more JNI methods is now an additive change to
    `binder_jni_stub.cc` (then rebuild stubs + relink dalvikvm).  Each
    method must also be registered in `JNI_OnLoad_binder_with_cl`'s
    `RegisterNatives` table.

### Previous "BLOCKER" section (now historical)

The dalvikvm static-libdl-stub block listed three alternatives:
1. Static-link JNI methods into dalvikvm (recommended, **chosen**).
2. Restore-and-extend the OHBridge stub (same cost, architecturally
   muddier).
3. Build a dlopen-capable dalvikvm variant (3-5 days, big detour).

Option 1 took roughly 0.4 person-day end to end (most of that was
tracking down the FindClass-classloader issue and the
framework_duplicates.txt stripping problem; the static-link itself was
~1 hour).

## Files touched (M3 + M3-finish)

New:
- `aosp-libbinder-port/native/libandroid_runtime_stub.cc` — M3 .so version (now redundant; reference for M4 A1)
- `aosp-libbinder-port/test/HelloBinder.java`
- `aosp-libbinder-port/m3-dalvikvm-boot.sh`
- `aosp-libbinder-port/build_hello.sh`
- `aosp-libbinder-port/bionic-overlay/include/linux/android/binder.h` (copy of newer UAPI)
- `aosp-libbinder-port/patches/bionic-include-prefix.h` (documentation)
- `art-latest/stubs/binder_jni_stub.cc` — M3-finish: static JNI bridge baked into dalvikvm

Modified:
- `aosp-libbinder-port/Makefile`:
  - added `*-bionic` targets (M3)
  - added `libbinder_full_static-bionic` target → `out/bionic/libbinder_full_static.a` (M3-finish)
- `aosp-libbinder-port/build.sh` — added `bionic` subcommand
- `aosp-libbinder-port/aosp-src/libbinder/liblog_stub/include/log/log.h` —
  patched `ALOG` macro to avoid token-paste expansion bug with NDK's syslog.h
- `aosp-libbinder-port/deps-src/libbase/properties_stub.cpp` — added
  `WaitForProperty` and `WaitForPropertyCreation` stubs
- `shim/java/android/os/ServiceManager.java` — completely rewritten as
  Java -> JNI -> libbinder wrapper (M3)
- `shim/java/android/os/ServiceManagerNative.java` — slimmed down (M3)
- `scripts/framework_duplicates.txt`:
  - removed `android/os/{ServiceManager, IServiceManager, ServiceManagerNative}` (M3)
  - removed `android/os/{IBinder, IInterface, Parcel, Binder, RemoteException}` (M3-finish)
- `scripts/build-shim-dex.sh` — comment update only
- `art-latest/Makefile.bionic-arm64` (M3-finish):
  - compile `binder_jni_stub.cc` with full libbinder include path
  - link `libbinder_full_static.a` whole-archive into dalvikvm
- `art-latest/stubs/openjdk_stub.c` (M3-finish):
  - added `android_runtime_stub` short-circuit in `Runtime_nativeLoad` that
    calls `JNI_OnLoad_binder_with_cl(vm, classLoader)` to register the
    binder JNI methods

Unchanged (per the milestone's "FILES NOT TO TOUCH"):
- `aosp-libbinder-port/out/libbinder.so` (musl variant, M1)
- `aosp-libbinder-port/out/servicemanager` (musl variant, M2)
- `aosp-libbinder-port/out/bionic/libbinder.so` (bionic variant, M3)
- `aosp-libbinder-port/out/bionic/servicemanager` (bionic variant, M3)
- AOSP-derived ART runtime source under `art-latest/` (only the
  project-local `stubs/` and `Makefile.bionic-arm64` are touched, in
  the same pattern as OHBridge / openjdk / javacore stubs)
- `framework.jar` (treated as opaque; we shadow its `ServiceManager`,
  `IBinder`, `Parcel`, `IInterface`, `Binder`, `RemoteException` via
  shim-dex classloader priority — when M4 adds framework.jar to BCP,
  the shim versions will conflict and these duplicates entries must be
  re-added)

## Update (2026-05-12) — PF-arch-053: BCP placement now works

The "M3 NOTE 1" inside `run_dalvikvm()` warning that
`aosp-shim.dex` on `-Xbootclasspath:` SIGBUSes during PathClassLoader
init is **OBSOLETE**.  With the current slim shim (1.4 MB / 754 classes,
post `framework_duplicates.txt` stripping) and PF-arch-019, both
`-Xbootclasspath:...:aosp-shim.dex` and adding `framework.jar:ext.jar:
services.jar` to BCP now boot cleanly and pass HelloBinder's PASS check.

The historical SIGBUS was caused by duplicate-named native methods in
the fat shim leaving `EntryPointFromJni` at the
`kPFCutStaleNativeEntry` sentinel (`0xfffffffffffffb17`); resolved by
the slim-shim work + PF-arch-019.

`m3-dalvikvm-boot.sh` now supports `--bcp-shim` and `--bcp-framework`
flags; `aosp-libbinder-port/test/bcp-sigbus-repro.sh` is the
acceptance/regression test.  See `docs/engine/PF-arch-053-NOTES.md`.

## Recommended next action for M4

M3 is fully unblocked.  M4 needs actual binder transactions from Java to
system services (IActivityManager, IWindowManager, etc.).  Two paths:

1.  **Grow A2:** add per-service native methods to `binder_jni_stub.cc`
    for each AIDL interface
    (`Java_android_os_ServiceManager_callActivity_attachApplication`, etc.).
    Scales poorly.

2.  **Migrate to A1:** implement the BinderProxy + Parcel JNI cluster in
    `binder_jni_stub.cc`.  The payoff: Java code can use AOSP's real
    `ServiceManager.java` + AOSP's auto-generated AIDL stubs in
    `framework.jar` verbatim, no per-service shim needed.

Recommend **A1 migration as the first M4 task**.
Once BinderProxy works:
- delete `shim/java/android/os/ServiceManager.java`
- re-add `android/os/{ServiceManager*, IBinder, IInterface, Parcel,
  Binder, RemoteException}` to `framework_duplicates.txt` (so
  framework.jar's versions win)
- in `binder_jni_stub.cc`, swap the current ServiceManager natives for
  the full BinderInternal + Parcel + BinderProxy cluster
  (`android_util_Binder.cpp` in AOSP is ~1600 LOC, but most of it is
  Parcel marshaling boilerplate)
- M4a (ActivityManagerService) and friends can use AOSP AIDL stubs directly

The static-link mechanism extends cleanly: each additional JNI surface
adds entries to `JNI_OnLoad_binder_with_cl`'s `RegisterNatives` table
and links its `.o` into dalvikvm.

Estimated effort:
- M3 unblock (Option 1): 0.5 day
- A1 migration: 3-5 days
- M4a (ActivityManagerService) shells: 2 days each

## M3++ — JavaBBinder + same-process Stub.asInterface (2026-05-12)

Adds the minimum surface for AOSP-style `IXxxService.Stub.asInterface(IBinder)`
to elide marshaling and return the local Java IInterface when both ends
live in the same process.  This is the architectural pattern every M4
service will rely on.

### What changed (M3++)

1.  **New native bridge: `JavaBBinder` + `JavaBBinderHolder`.**  Sources:
    - `aosp-libbinder-port/native/JavaBBinderHolder.h` (75 LOC)
    - `aosp-libbinder-port/native/JavaBBinderHolder.cpp` (216 LOC)

    `JavaBBinder` is a `BBinder` subclass holding a JNI global ref to an
    `android.os.Binder` Java object.  `JavaBBinderHolder` is the
    `wp<JavaBBinder>` factory stored as a `jlong` in `Binder.mObject`.
    `JavaBBinder::checkSubclass(&gJavaBBinderSubclassID)` identifies
    "this BBinder is really a Java Binder"; combined with `localBinder()`
    inherited from `BBinder`, this enables the same-process round-trip
    optimization in `javaObjectForLocalIBinder()`.

    `JavaBBinder::onTransact()` returns `UNKNOWN_TRANSACTION` — M3++
    does **not** implement cross-process transactions.  Same-process
    `queryLocalInterface` elision is sufficient for M4 services.

2.  **Updated `art-latest/stubs/binder_jni_stub.cc`** (+11 native
    methods, total 18 vs M3-finish's 8):
    - `android.os.Binder.getNativeBBinderHolder` — mints a JavaBBinderHolder
    - `android.os.Binder.getNativeFinalizer` / `.nativeDestroy` — cleanup
    - `android.os.Binder.{getCallingPid, getCallingUid, clearCallingIdentity,
      restoreCallingIdentity, flushPendingCommands}` — calling-identity stubs
      that delegate to `IPCThreadState::self()` (returns process PID/UID
      for same-process calls)
    - `android.os.ServiceManager.nativeAddService` — signature changed
      from `(String, long)I` to `(String, IBinder)I`.  When the IBinder
      is an `android.os.Binder` subclass, JNI extracts the JavaBBinderHolder
      from `Binder.mObject` and registers the underlying `sp<JavaBBinder>`
      with servicemanager.
    - `android.os.ServiceManager.nativeGetLocalService` — NEW.  Looks up
      a service, then uses `IBinder::localBinder()` + the subclass-ID
      check to detect "this is a same-process JavaBBinder" and returns
      the original Java Binder object.  Returns null when the service is
      remote, or when it's a local non-Java BBinder.

3.  **Updated `shim/java/android/os/Binder.java`.**  Now matches AOSP
    shape:
    - `private final long mObject` — JavaBBinderHolder pointer, set by
      `getNativeBBinderHolder()` in the constructor
    - `private IInterface mOwner; private String mDescriptor` — set by
      `attachInterface()`, consulted by `queryLocalInterface()` for the
      Stub.asInterface optimization
    - Native method declarations match AOSP's hidden-API surface
      (getCallingPid/Uid, clearCallingIdentity, etc.).

4.  **Updated `shim/java/android/os/ServiceManager.java`.**  `getService`
    now tries `nativeGetLocalService(name)` first (returns the same Java
    object for same-process services), falling back to the M3 path of
    `nativeGetService` + `NativeBinderProxy` wrapper for remote/non-Java
    binders.  `addService(name, service)` now passes the IBinder directly
    to JNI (signature changed).

5.  **Makefile additions** (`aosp-libbinder-port/Makefile`):
    - New target `javabinder-bionic` — builds JavaBBinderHolder.cpp,
      repackages `libbinder_full_static.a` with the new .o included.
    - `libbinder_full_static.a` now depends on JavaBBinderHolder.o.

6.  **New file `aosp-libbinder-port/test/AsInterfaceTest.java`** (200 LOC):
    AOSP-style `IEcho` interface with a Stub class, EchoImpl service,
    and end-to-end test of `ServiceManager.addService` →
    `ServiceManager.getService` → `IEcho.Stub.asInterface` →
    `echo.say("hi")` round-trip.  Compiled to `out/AsInterfaceTest.dex`
    via the new `build_asinterface.sh`.

7.  **Updated `m3-dalvikvm-boot.sh`.**  Added `--test <Name>` flag for
    parameterizing which dex/main class to run.  `--test AsInterfaceTest`
    also implies `--no-registrar` since the test self-registers its
    service.

### M3++ Verification

```
$ /home/dspfac/android-sdk/ndk/25.2.9519653/.../bin/llvm-nm \
    art-latest/build-bionic-arm64/bin/dalvikvm | \
    grep -E "Java_android_os_Binder|JavaBBinder"
... 18 binder/JavaBBinder symbols present ...

$ $ADB shell "su -c 'cd /data/local/tmp/westlake && bash bin-bionic/m3-dalvikvm-boot.sh test --test AsInterfaceTest'"
[m3-boot] running dalvikvm AsInterfaceTest.dex (bcp_shim=0 bcp_framework=0)
[m3-boot] dalvikvm exit code: 0
WLK-binder-jni: JNI_OnLoad_binder: android.os.ServiceManager natives: 7/7
WLK-binder-jni: JNI_OnLoad_binder: android.os.Binder natives: 8/8
WLK-binder-jni: JNI_OnLoad_binder: AsInterfaceTest natives: 2/2
AsInterfaceTest: starting M3++ Stub.asInterface test
AsInterfaceTest: created EchoImpl: AsInterfaceTest$EchoImpl@4e203ab
AsInterfaceTest: self queryLocalInterface("westlake.IEcho") -> SAME echo object — OK
AsInterfaceTest: addService("westlake.echo", echo) OK
AsInterfaceTest: getService("westlake.echo") -> AsInterfaceTest$EchoImpl@4e203ab
AsInterfaceTest: getService returned SAME echo object — same-process optimization ACTIVE
AsInterfaceTest: Stub.asInterface returned AsInterfaceTest$EchoImpl@4e203ab
AsInterfaceTest: Stub.asInterface returned SAME echo — direct Java dispatch ACTIVE
AsInterfaceTest: say("hi") -> "hi" — OK
AsInterfaceTest: listServices() contains westlake.echo — OK
AsInterfaceTest: PASS
```

HelloBinder.dex (M3 regression) still passes — same dalvikvm binary,
no behavior change for legacy callers.

### M3++ Notes & gotchas

-   **Lib load must happen in main(), not in `<clinit>`.**  Putting
    `System.loadLibrary("android_runtime_stub")` in a Java static
    initializer triggers `JNI_OnLoad_binder` (and its classloader-aware
    Binder/ServiceManager class lookups) BEFORE dalvikvm's
    `RegisterNatives` loop runs on the main class.  On this dalvikvm
    build that leaves the main class with a corrupt method table — the
    first dispatch crashes at PC=0xfffffffffffffb17 (a poison value).
    HelloBinder.java had already adopted the deferred-load pattern;
    AsInterfaceTest follows suit.  See AsInterfaceTest.java's
    `loadLib()` comment for details.

-   **No Parcel JNI methods were needed.**  M3++'s acceptance test
    exercises `queryLocalInterface`, `attachInterface`, and direct Java
    vtable dispatch.  None of these flow through `android.os.Parcel`.
    When a future M4a/b service genuinely needs cross-process transact
    (i.e. the service is registered by a different process than the
    caller), the Parcel JNI cluster (~600 LOC in AOSP) will need
    porting — but every same-process service can skip it indefinitely.

-   **The `mObject` field is `final` in our shim.**  AOSP's Binder.java
    has it `final` too.  This means once a Binder is constructed without
    the native bridge, it can never get a JavaBBinderHolder (the field
    stays at 0).  Callers should ensure `System.loadLibrary` succeeds
    before constructing any Binder subclass.  When `mObject == 0`,
    `nativeAddService` falls back to registering an anonymous `BBinder`
    token (the M3-era behavior).

### Files touched (M3++)

New:
- `aosp-libbinder-port/native/JavaBBinderHolder.h` (75 LOC)
- `aosp-libbinder-port/native/JavaBBinderHolder.cpp` (216 LOC)
- `aosp-libbinder-port/test/AsInterfaceTest.java` (200 LOC)
- `aosp-libbinder-port/build_asinterface.sh` (60 LOC)

Modified:
- `art-latest/stubs/binder_jni_stub.cc` — added Binder natives + local-binder
  lookup (now ~540 LOC, up from ~376)
- `aosp-libbinder-port/Makefile` — added javabinder-bionic target +
  JavaBBinderHolder.o in libbinder_full_static.a
- `aosp-libbinder-port/build.sh` — added `javabinder` subcommand
- `shim/java/android/os/Binder.java` — added mObject + AOSP-shape natives
- `shim/java/android/os/ServiceManager.java` — nativeAddService signature
  changed; getService tries nativeGetLocalService first
- `aosp-libbinder-port/m3-dalvikvm-boot.sh` — `--test <Name>` flag

Unchanged (per "FILES NOT TO TOUCH"):
- `aosp-libbinder-port/out/*` (musl artifacts)
- `art-latest/Makefile.bionic-arm64` (already wires libbinder_full_static.a
  whole-archive into dalvikvm; M3++ rebuild only changes the contents of
  the archive)
- framework_duplicates.txt (no further class duplicates needed —
  Binder/IBinder/IInterface/Parcel/RemoteException already removed at
  M3-finish; ServiceManager/IServiceManager/ServiceManagerNative
  already removed at M3)

### Recommended next M4a step

The M3++ pattern works.  Next step is to migrate one real AOSP service
through this path:

1.  **Choose `IPackageManager` or `IActivityManager`** as the first M4a
    target.  PMS has a smaller interface; AMS is on the critical path
    for app startup.  Pick whichever framework.jar code wants first.

2.  **Provide the service implementation in shim/java.**  E.g.
    `shim/java/com/android/server/pm/PackageManagerService.java extends
    IPackageManager.Stub` with method bodies that return canned/stub
    data (the M4a goal is "framework.jar's bootstrap doesn't crash", not
    "PMS is functional").

3.  **Register at startup** via `ServiceManager.addService("package",
    new PackageManagerService(...))`.

4.  **Verify** `IPackageManager.Stub.asInterface(ServiceManager
    .getService("package"))` returns the same Java instance.

5.  **First framework.jar call** that uses PMS through ServiceManager
    should now succeed (returns canned data instead of crashing on a
    null binder).

Estimated effort: 0.5 day per stub service (assuming the AIDL is
already in framework.jar; we just need a Java impl + Stub registration).

The bigger M4 question — should we wait for W1-A's framework.jar
classloader fix? — depends on whether IPackageManager.Stub is actually
in framework.jar's BCP-reachable area.  M3++ doesn't load framework.jar,
so the test stays self-contained.  W1-A's work is orthogonal: it
enables loading the real framework.jar (containing AOSP-generated AIDL
stubs).  Once those load cleanly, M4a stubs just need a Java
implementation.  Recommend: M4a can start NOW with hand-written AIDL
stubs (mimicking AOSP shape) and migrate to framework.jar's AIDL stubs
when W1-A lands.  No blocking dependency.
