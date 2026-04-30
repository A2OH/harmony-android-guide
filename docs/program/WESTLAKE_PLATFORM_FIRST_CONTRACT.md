# Westlake Platform-First Contract

Last updated: 2026-04-30

This document is the repo-pushed version of the active Westlake delivery
contract.

## Goal

Deliver Westlake as a stable standalone Android guest platform. McDonald's is
the proving app, not the primary object of patching.

Required outcome:
- guest execution in Westlake `dalvikvm`
- stable Java/framework bootstrap
- coherent generic activity/window/looper/input/surface host contract
- app-owned AndroidX/AppCompat running on that substrate
- only then real McDonald's UI

## Supervisor Update: Stock McDonald's Return Frontier

The active contract frontier is back on the real McDonald's APK, not the
controlled mock McD profile. The host APK still runs on the phone's normal ART
only as a shell for Activity/Surface/input ownership; the guest APK is launched
as a separate Westlake `dalvikvm` subprocess. The target/OHOS path must not
fall back to `app_process64`, `dalvikvm64`, or the phone framework runtime.

Latest verified real McDonald's progress on 2026-04-30:

Newest real-McD source-built runtime proof, phone-proven at 01:15:

- Phone-proven artifacts:
  `dalvikvm=1c136763c746f8e16e06451779b6e201621eeb0ca10ccd59a6d01a53f19fd9a3`,
  `aosp-shim.dex=2baa2ab7149285f283e2537d7c2dd939f1c30cb2ecd949e6fef34b5a6ecbb6cd`,
  `westlake-host.apk=d9e505b30962bf7b837a544aa8d5826a2af37ec0ef1431d976b2f5d0fc13a213`.
- Proof artifacts:
  `artifacts/real-mcd/20260430_011506_clean_patchsystem_a15_arm64/logcat_tail.log`
  and
  `artifacts/real-mcd/20260430_011506_clean_patchsystem_a15_arm64/screen.png`
  (`1080x2280`).
- This proof is from the durable `/home/dspfac/art-latest` patch build path,
  not from hand-edited AOSP state. The clean bionic build reports
  `runtime 230 / 230`, including real A15 ARM64
  `thread_arm64.cc` and `entrypoints_init_arm64.cc`.
- The ARM64 build is now architecturally aligned for this runtime slice:
  A15 quick entrypoints assemble with A15 include precedence, A11 JNI
  entrypoints remain isolated for the current portable CFI gap, and
  `thread_cpu_stub.cc` is built as its own support object. The same assembly
  and support-stub slice also compiles under `Makefile.ohos-arm64`; that is a
  build-slice sanity check, not a full OHOS runtime proof.
- The deployed runtime passes `scripts/check-westlake-runtime-symbols.sh`.
  Strong unresolved symbols from the previous rejected deploy candidate are
  closed, including ART ARM64 entrypoint/thread CPU symbols, WebP, Android
  cmsg send, log error write, and A11 instrumentation quick-entrypoint
  leftovers.
- The accepted focused phone grep contains only the dashboard render-loop
  frame and no `pending UOE`, `ThreadGroup.uncaughtException`,
  VarHandle diagnostic marker, `NoClassDefFoundError`,
  `UnsatisfiedLinkError`, JNI fatal marker, or fatal signal.
- Acceptance boundary: this strengthens the runtime delivery baseline and
  OHOS portability track. It still does not prove production McDonald's UI,
  real dashboard data, real Realm/native persistence, or removal of existing
  `PFCUT` fallback/diagnostic paths.

Previous real-McD Java concurrency proof, phone-proven at 00:38:

- Phone-proven artifacts:
  `dalvikvm=77389791ad497b68efe7357c96f7c2ec84522fb6daff3ba82b56e584714984db`,
  `aosp-shim.dex=2baa2ab7149285f283e2537d7c2dd939f1c30cb2ecd949e6fef34b5a6ecbb6cd`,
  `westlake-host.apk=d9e505b30962bf7b837a544aa8d5826a2af37ec0ef1431d976b2f5d0fc13a213`.
- Proof artifacts:
  `artifacts/real-mcd/20260430_003805_clean_varhandle_futuretask/logcat_tail.log`
  and
  `artifacts/real-mcd/20260430_003805_clean_varhandle_futuretask/screen.png`.
- The background `FutureTask`/Rx scheduler
  `UnsupportedOperationException` is closed. The root cause was a
  `java.lang.invoke.FieldVarHandle` with `accessModesBitMask=0x0` rejecting
  `compareAndSet` for `FutureTask.runner` before reaching the already-present
  native field CAS path.
- The runtime now derives a guarded zero-mask field VarHandle fallback from
  the native `ArtField` and variable type for `FieldVarHandle` and
  `StaticFieldVarHandle`, while preserving read-only final-field behavior.
  This is a generic Java concurrency substrate fix used by `FutureTask`,
  `ScheduledThreadPoolExecutor`, and `ConcurrentLinkedQueue`.
- The accepted clean proof has no `pending UOE`,
  `ThreadGroup.uncaughtException`, `PFCUT-UOE-THROW`, VarHandle diagnostic
  marker, `DoCall-TRACE`, or `NoClassDefFoundError`; the only focused marker
  left is the dashboard render-loop frame.

Previous real-McD render-loop/service proof, phone-proven at 00:16:

- Phone-proven artifacts:
  `dalvikvm=fbce9ed66b5e023f749c6f83cf8df2b48abe97c5e91fdf2acc96675db8ba5f05`,
  `aosp-shim.dex=2baa2ab7149285f283e2537d7c2dd939f1c30cb2ecd949e6fef34b5a6ecbb6cd`,
  `westlake-host.apk=d9e505b30962bf7b837a544aa8d5826a2af37ec0ef1431d976b2f5d0fc13a213`.
- Proof artifacts:
  `artifacts/real-mcd/20260430_001605_clean_ncdfe_logging/logcat_tail.log`
  and
  `artifacts/real-mcd/20260430_001605_clean_ncdfe_logging/screen.png`.
- The render-loop `NoClassDefFoundError` exposed after the telephony/location
  fixes is closed. A temporary descriptor probe identified
  `Ljava/lang/management/ManagementFactory;`; the root cause was Westlake shim
  code in `android.os.Process` and `android.os.SystemClock` calling a Java SE
  management API that Android/OHOS should not require. Those calls now use
  `/proc/self/stat`, `System.nanoTime()`, and portable fallbacks.
- The temporary descriptor probe is disabled again in the accepted clean
  runtime so expected boot-loader misses do not bury future startup evidence.
- The proof reaches the real McDonald's dashboard activity, pumps render-loop
  messages, consumes a touch marker, and emits a dashboard render-loop frame
  without the prior `ManagementFactory` boot-class-loader miss.
- The current accepted service-surface work also includes:
  `20260429_235855_java_telephony` for Java `TelephonyManager` defaults and
  `20260430_000242_location_java_surface` for Java `LocationManager` defaults.
  This establishes the rule that guest framework services must not depend on
  unregistered `OHBridge` native calls when strict subprocess mode logs
  `guest defer registration`.

Newest real-McD native boundary proof, phone-proven at 23:50:

- Proof artifact:
  `artifacts/real-mcd/20260429_235043_realm_native_boundary/logcat_tail.log`.
- Realm now moves past the immediate `libdl.a is a stub` crash only by a
  deliberate boundary probe: `Runtime.nativeLoad` reports success for
  `librealm-jni`, and `io.realm.*` native methods default their return values
  at the interpreter boundary.
- This is useful for exploring the next Java/framework/rendering boundaries,
  but it is explicitly not PF-494 closure and not stock native-library
  compatibility. Realm persistence, real native handles, schema/query behavior,
  and full APK `.so` loading remain open.
- The over-broad McD analytics class-load fallback was also removed in
  `artifacts/real-mcd/20260429_235317_analytics_class_load/`, allowing the
  real `McDTagManagerWrapper` class to load and eliminating the earlier
  `IMcDAnalytics.trackDataWithKey` null path.

Current supervisor order from these proofs:

1. keep the source-built `art-latest` bionic/OHOS ARM64 Makefiles as the
   accepted runtime delivery path. No phone proof is accepted if
   `scripts/check-westlake-runtime-symbols.sh` fails or if the runtime hash
   cannot be reproduced from that patch system;
2. keep Java concurrency and framework service surfaces generic; do not patch
   McDonald's call sites when the blocker is a platform primitive such as
   VarHandle, FutureTask, or a framework service API;
3. keep the Java framework service surfaces portable and direct by default;
   add host/OHOS service bridges only where a real guest-facing API needs live
   data;
4. remove temporary diagnostic noise after each proof so log windows preserve
   early startup evidence;
5. replace the Realm boundary probe with either a real portable native loader
   contract or a deliberate Java storage/Realm compatibility layer;
6. continue through the remaining AndroidX/ViewModel, AppCompat, config, and
   real dashboard fragment/data gaps without calling fallback dashboard frames
   production UI.

Previous verified real McDonald's progress on 2026-04-29:

Newest real-McD native/split metadata proof, phone-proven at 23:37:

- Phone-proven artifacts:
  `dalvikvm=1011e6072a0a289deda47a379e028382e224adf7d4c6fb5f2f2af5d3daa8c467`,
  `aosp-shim.dex=9d29d310c5d1928f27a2940c75f1a9ae824cc99582969ee6e2c281944c2c527e`,
  `westlake-host.apk=3ceef0010d6533a9cdaf2842dab58f311ad2fbe99305ab8afb074e0d0bfe2f19`.
- Proof artifacts:
  `artifacts/real-mcd/20260429_233724_split_metadata/logcat_tail.log`
  and
  `artifacts/real-mcd/20260429_233724_split_metadata/screen.png`.
- The guest no longer depends on a live Android host `PackageManager` for
  split APK metadata. The host passes staged split paths as
  `westlake.apk.splitSourceDirs`, and the shim `ApplicationInfo` exposes them
  inside the Westlake subprocess.
- This advances Realm/ReLinker from `Could not find 'librealm-jni.so' ...
  only found: []` to a real native-load attempt for the versioned copied
  library:
  `Runtime_nativeLoad path=/data/local/tmp/westlake/app_lib/librealm-jni.so.10.19.0`.
- The new top blocker is no longer split metadata; it is native loader/linker
  compatibility for stock APK shared libraries:
  `UnsatisfiedLinkError: libdl.a is a stub --- use libdl.so instead`.
- Dashboard evidence remains alive after this change:
  `Dashboard active` and strict dashboard frames are still emitted.

Current supervisor order from this proof:

1. decide and implement the correct native-library contract: dynamic APK
   native loading through an OHOS-portable loader abstraction, or an explicit
   temporary Realm JNI boundary if the next milestone prioritizes UI/data
   exploration before full native library support;
2. keep the split metadata path as the accepted portable baseline for
   `ApplicationInfo.sourceDir`, `splitSourceDirs`, and `nativeLibraryDir`;
3. rerun the real-McD proof after native-loader changes and require Realm to
   move past `libdl.a` before claiming PF-493 progress;
4. continue cleaning Application config/network bootstrap and AndroidX
   factory/state behavior after Realm native init moves forward.

Newest real-McD dashboard/runtime/render proof, phone-proven at 23:29:

- The stock APK remains staged as
  `/data/local/tmp/westlake/com_mcdonalds_app.apk` and launched with
  `launch=WESTLAKE_ART_MCD`.
- Phone-proven artifacts:
  `dalvikvm=1011e6072a0a289deda47a379e028382e224adf7d4c6fb5f2f2af5d3daa8c467`,
  `aosp-shim.dex=d82381f6e44c5d0d1ba2169df63ab790b026064296b849013d18be3e66574744`,
  `westlake-host.apk=421d8e71a533360a59e6213275188342f883afccd56cb277885b083b33fe3e6b`.
- Proof artifacts:
  `artifacts/real-mcd/20260429_232926_viewmodel_helper/logcat_tail.log`
  and
  `artifacts/real-mcd/20260429_232926_viewmodel_helper/screen.png`.
- The previous `File.toPath()` / default filesystem blocker is closed by a
  portable `FileSystems.getDefault()` fallback for the Westlake guest.
- The previous null `CodingErrorAction.REPORT` charset blocker is closed for
  the current path by substituting the boot action object when encoder/decoder
  policy setup sees a null static.
- The previous `UnixNativeDispatcher.stat0`/`NativeBuffer` SIGBUS is closed
  for APK ZIP access by handling `UnixPath` stat/lstat/access directly in the
  interpreter boundary.
- The previous AndroidX Lifecycle
  `ViewModelProviderImpl_androidKt.a(factory, KClass, extras)` SIGBUS is
  closed. The helper now delegates to `Factory.create(Class, CreationExtras)`
  and the proof logs successful delegation through AndroidX, Hilt retained
  component, and Hilt view model factory paths.
- The host receives real-McD dashboard DLST frames from the Westlake guest
  subprocess again:
  `Dashboard active: com.mcdonalds.homedashboard.activity.HomeDashboardActivity`
  and `Strict dashboard frame reason=dashboard-first
  root=android.widget.LinearLayout bytes=776 views=29 texts=14 buttons=6
  images=1`, followed by a render-loop frame.
- No `SIGBUS`, `SIGSEGV`, `SIGABRT`, or `Fatal signal` marker appears in the
  accepted McDonald's proof window.

Acceptance boundary: this is still a real stock-APK Splash/HomeDashboard
lifecycle and visible Westlake render proof, not production McDonald's UI
compatibility. The visible dashboard remains generated fallback content inside
the real dashboard activity/container.

Current supervisor order from this proof:

1. close Realm/ReLinker/native init coherently. The proof attempts
   `Runtime_nativeLoad path=/data/local/tmp/westlake/app_lib/librealm-jni.so`,
   but later still logs `MissingLibraryException` for `librealm-jni.so` and
   `Call Realm.init(Context) before creating a RealmConfiguration`;
2. clean `McDMarketApplication.onCreate`, which still logs
   `Config failed to download`;
3. replace the AndroidX default-factory fallback that allocates a ViewModel
   without constructor with a generic AndroidX-compatible factory/state path;
4. replace the generated dashboard fallback with real APK-driven
   `HomeDashboardFragment`/data-binding content;
5. remove McD-specific dashboard touch/render routing by fixing generic View
   hit testing, scrolling, invalidation, adapter dispatch, and Material/AppCompat
   rendering;
6. keep every accepted step phone-proven with hashes and a screenshot/log
   artifact before calling it contract progress.

Previous real-McD dashboard/runtime/render proof, phone-proven at 22:08:

- The stock APK remains staged as
  `/data/local/tmp/westlake/com_mcdonalds_app.apk` and launched with
  `launch=WESTLAKE_ART_MCD`.
- Phone-proven artifacts:
  `dalvikvm=d9e5d37256fd52284234fd8621129f19985c7c12b754023f74b97b4c69f44a3b`,
  `aosp-shim.dex=40766a520d19e2a403383d971bf4717793115b8a459c5bd7bcb0db03ad844a65`,
  `westlake-host.apk=d1f07ca75d025b2650f449c626b29268c054b1aade391bc412f1cfd6da1c8f0d`.
- Proof artifacts:
  `artifacts/real-mcd/20260429_220819_split_native/logcat_tail.log`
  and
  `artifacts/real-mcd/20260429_220819_split_native/screen.png`
  (`1080x2280`).
- The real APK reaches real `SplashActivity`, launches real
  `com.mcdonalds.homedashboard.activity.HomeDashboardActivity`, and
  `HomeDashboardActivity.onCreate` completes under Westlake.
- The previous `MethodHandles.lookup()` caller identity blocker is closed for
  the current concurrency initialization window. The proof logs real callers
  for `FutureTask`, `ConcurrentLinkedQueue`, `AtomicBoolean`, and
  `ForkJoinPool`, and no longer logs `illegal lookupClass`.
- The previous boot `DexPathList.findLibrary()` null-array crash is closed.
  Host now copies installed sibling split APKs, extracts/passes app native
  libraries, and runtime lookup reports
  `DexPathList.findLibrary realm-jni ->
  /data/local/tmp/westlake/app_lib/librealm-jni.so`, followed by
  `Runtime_nativeLoad path=/data/local/tmp/westlake/app_lib/librealm-jni.so`.
- The host receives dashboard DLST frames from the Westlake guest subprocess:
  `Strict dashboard frame reason=dashboard-first root=android.widget.LinearLayout
  bytes=776 views=29 texts=14 buttons=6 images=1`, followed by a
  dashboard render-loop frame through the host `DisplayListFrameView`.
- The screenshot shows a full-height McDonald's dashboard frame instead of a
  black or splash-only surface.

Acceptance boundary: this is a real stock-APK Splash/HomeDashboard lifecycle
and visible Westlake render proof. It is still not stock McDonald's production
UI compatibility. The current visible menu is generated fallback content
installed into the real dashboard container; real fragment content,
production app data, Material/AppCompat fidelity, and generic hit/scroll
dispatch remain open contract work.

Current supervisor order from this proof:

1. close the libcore charset/provider/default-encoding gap exposed after real
   Realm native load by `PersistenceManager` class initialization
   (`NullPointerException: charset`);
2. clean `McDMarketApplication.onCreate` rather than relying on Activity
   survival after the Application failure;
3. replace the generated dashboard fallback with real APK-driven
   `HomeDashboardFragment`/data-binding content;
4. remove McD-specific dashboard touch routing by fixing generic View hit
   testing, scrolling, invalidation, and adapter dispatch;
5. expand generic Material/AppCompat XML/theme/rendering until the dashboard
   frame is produced by app-owned UI, not launcher scaffolding;
6. rerun the same real-McD phone proof after each change and keep artifact/hash
   discipline for `dalvikvm`, `core-oj.jar`, `aosp-shim.dex`, and screenshots.

Previous real-McD dashboard/runtime/render proof, phone-proven at 22:03:

- Phone-proven artifacts:
  `dalvikvm=d9e5d37256fd52284234fd8621129f19985c7c12b754023f74b97b4c69f44a3b`,
  `aosp-shim.dex=40766a520d19e2a403383d971bf4717793115b8a459c5bd7bcb0db03ad844a65`,
  `westlake-host.apk=0945cd73041a8522e411c934f5d9d63ab81d714fda25999018fb39f43a2b0cc1`.
- Proof artifacts:
  `artifacts/real-mcd/20260429_220309_native_lib/logcat_tail.log`
  and
  `artifacts/real-mcd/20260429_220309_native_lib/screen.png`.
- This proof closed the `DexPathList.findLibrary()` null-array crash and
  exposed the need for full split/native payload staging.

Previous real-McD dashboard/runtime/render proof, phone-proven at 18:52:

- Phone-proven artifacts:
  `dalvikvm=0b7acbe35837c357ded4e3413f3c64057efd256b9e31854221112b346f14b17f`,
  `core-oj.jar=e19236b056ec6257c751d070f758e682dc1c62ba0cb042fde93d3eec09d647c2`,
  `aosp-shim.dex=2284d0f553ffb9eae3e1f7cc4d1afccf3fcb0875876ddedae3bec3cc9d76d1e1`.
- Proof artifacts:
  `artifacts/real-mcd/20260429_185146/logcat_tail_20260429_185146.log`
  and
  `artifacts/real-mcd/20260429_185146/real_mcd_screen_20260429_185146.png`.
- This proof closed the earlier `OHBridge.isNetworkAvailable()` native SIGBUS
  for the captured path by routing `ConnectivityManager` through
  `NetworkBridge`.

Previous real-McD dashboard/render/input proof, phone-proven at 15:50:

- The stock APK remains staged as
  `/data/local/tmp/westlake/com_mcdonalds_app.apk` and launched with
  `launch=WESTLAKE_ART_MCD`.
- Phone-proven artifacts:
  `dalvikvm=a1d54866a5b1e70ede0a0919ccaeca63b0a3deeae4972ab23d54e31412089bd8`,
  `core-oj.jar=e19236b056ec6257c751d070f758e682dc1c62ba0cb042fde93d3eec09d647c2`,
  `aosp-shim.dex=f62561aa3dbec74269b98d9aa46ba1925dc204148d6ee9d875d77d818d243282`,
  `westlake-host.apk=63dda5e62c61387004df15e7fb0f4a2ff43bbd3f3e3b7536c53eacbc495094bb`.
- Proof artifacts:
  `artifacts/real-mcd/20260429_155050/real_mcd_touch_routed_20260429_155050.log`
  and
  `artifacts/real-mcd/20260429_155050/real_mcd_touch_routed_20260429_155050.png`.
- The real APK reaches `SplashActivity`, launches real
  `com.mcdonalds.homedashboard.activity.HomeDashboardActivity`, wires app APK
  resources, enters dashboard `onCreate`, and then installs a
  Westlake-rendered dashboard fallback after the current dashboard runtime gap.
- The host receives dashboard DLST frames with
  `bytes=1736 views=48 texts=30 buttons=8 images=3`.
- ADB touch is proven through Westlake into guest state:
  the captured screenshot shows `2 items in bag | Added Big Mac Combo` after
  touch-driven frames.

Acceptance boundary: this is a real stock-APK lifecycle/resource/render/input
frontier, not stock McDonald's UI compatibility. The visible UI is still a
Westlake fallback scaffold and the current touch proof uses dashboard-specific
coordinate routing. Generic layout, hit testing, Material rendering, and real
fragment lifecycle remain contract work.

Next supervisor order from that previous proof, partly superseded by the 18:52
run:

1. add/phone-prove `dalvik.system.VMRuntime.getSdkVersion()I` if it returns;
   the 18:52 proof no longer shows this blocker in the captured window;
2. remove the McD-specific dashboard input router by fixing generic View hit
   testing and invalidation;
3. replace strict-mode fragment lifecycle skips with generic AndroidX
   FragmentManager/Fragment attach;
4. render the real dashboard XML/View tree through generic Material/AppCompat
   support;
5. rerun this same real-McD phone proof after each change and keep the
   artifact/hash discipline.

Newest real-McD runtime frontier, phone-proven at 14:25:

- `core-oj.jar` is now a required Westlake guest runtime artifact, not an
  optional deploy side file. Every real-McD replay must push/hash `dalvikvm`,
  `core-oj.jar`, and `aosp-shim.dex`.
- Phone-proven artifacts:
  `dalvikvm=d7bb5761ea16d56ff41ce49a6499627748054d3af8413bb44e1615ec9dd2f8d2`,
  `core-oj.jar=8c344b1ac41bdbb4403763a5b061a8313056a010752835273cf90d79dd561d44`,
  `aosp-shim.dex=9d7ffa3a60c37b21fc1bed01f1cb9f52de8e720b4c454d9d096eb255ef5c5bf4`.
- Latest phone-proven log:
  `/mnt/c/Users/dspfa/TempWestlake/real_mcd/real_mcd_20260429_142531.log`.
- Latest phone-proven screenshot:
  `/mnt/c/Users/dspfa/TempWestlake/real_mcd/real_mcd_20260429_142531.png`
  (`1080x2280`, mostly blank/black frame).
- The run clears the previous Realm `UnixFileSystem.list(File)` SIGBUS,
  `AtomicInteger.getAndIncrement`, `AtomicReference.getAndSet`,
  `Unsafe.getUnsafe`, and `AtomicLong.compareAndSet` blockers.
- The real APK now reaches SplashActivity construction, AndroidX
  ActivityResult request-code generation, Hilt listener setup, and NewRelic
  trace construction under Westlake.
- Current hard blockers are generic standalone runtime/shim contracts:
  `System.getProperty(...)` can still dereference null `System.props`, and the
  NewRelic no-op cutout returns null for `Util.getRandom()`.
- Local pending `core-oj.jar`
  `4b152c62e7746ca93df19c6e25fe744c86fe29b1dbff45d9fc24a9675d855c45`
  adds guarded `System.getProperty` null-protection, but is not phone-proven
  yet because WSL/Windows ADB interop is currently failing with
  `UtilAcceptVsock: accept4 failed 110`.
- Local pending runtime candidate
  `/home/dspfac/art-latest/build-bionic-arm64/bin/dalvikvm`
  (`b193e5f3ff3ba564f58319fe3b81cca3ead7c605450e7c05e68e06d14d7151cd`,
  symbol gate passed) lets `NewRelic Util.getRandom()` execute instead of
  returning null. It is not accepted until it passes the same real-McD phone
  proof.

Earlier 13:08 dashboard-fragment branch, retained as lifecycle/rendering
evidence:

- `com.mcdonalds.app` is staged as
  `/data/local/tmp/westlake/com_mcdonalds_app.apk` and launched with
  `launch=WESTLAKE_ART_MCD`;
- Westlake reaches `McDMarketApplication.onCreate`;
- Westlake reaches
  `com.mcdonalds.mcdcoreapp.common.activity.SplashActivity.onCreate`;
- the guest schedules
  `com.mcdonalds.homedashboard.activity.HomeDashboardActivity`;
- Westlake enters `HomeDashboardActivity.onCreate`;
- Westlake programmatically instantiates `HomeDashboardFragment`, bypasses the
  unsafe app FragmentManager `commitNow()` and `performAttach()` calls in
  strict mode, then reaches `performCreate()`, `performCreateView()`, attaches
  a `ScrollView`, runs `performActivityCreated()`, and logs
  `Programmatic HomeDashboardFragment attached`.

The current blocker is still before first real dashboard paint. The
`d7bb5761...` runtime plus `a9b115...` shim phone proof no longer emits the
earlier `ApplicationNotificationBinding` missing-tag marker and no longer logs
a fatal signal in the captured window. It also moves past the Material
`BottomNavigationView` self-cast `ClassCastException` by making Material
boot/shim-owned on this path. The next `HomeDashboardActivity.onCreate`
blocker is `NullPointerException` on `RestaurantModuleInteractor.s()`, so the
frontier has moved to app dependency/Hilt state. The visible screenshot is a
valid `1080x2280` mostly blank Westlake frame, not the real McDonald's
dashboard.

Latest accepted real-McD proof:

- `dalvikvm=d7bb5761ea16d56ff41ce49a6499627748054d3af8413bb44e1615ec9dd2f8d2`
- `aosp-shim.dex=a9b115a81dba519991d20aa3e48e52f701abec43b71dd652cf07c933856bf40e`
- `westlake-host.apk=f080e20e9e96a6965be74a7ed38ea4369de38200b71054ffdeca6949b5b5d3a3`
- log:
  `/mnt/c/Users/dspfa/TempWestlake/real_mcd/real_mcd_material_policy_20260429_130813.log`
  (`57eb43e76d70f277dad79527e496a27699fba537a6a7f25753e108d8ba90ebbf`)
- screenshot:
  `/mnt/c/Users/dspfa/TempWestlake/real_mcd/real_mcd_material_policy_20260429_130813.png`
  (`a4ae352c727c2c8f182275b68beb48543c258ffa4eb6652ed006ea7d103d3bd3`)

Rejected runtime candidate:

- `dalvikvm=7523774ecfdabeec733718326a3f74e87ce51aa080b28237a741f253be0efadb`
  regressed before Splash/Dashboard and must not replace the accepted
  `c90d15a...` runtime.
- `dalvikvm=d7bb5761ea16d56ff41ce49a6499627748054d3af8413bb44e1615ec9dd2f8d2`
  is accepted as a phone-proven one-byte Material policy derivative of
  baseline `c90d15a...`; clean source rebuild/reproducibility remains open.

Current supervisor order:

1. phone-prove the pending `System.getProperty` core patch once ADB is
   reachable again;
2. fix the NewRelic `Util.getRandom()` cutout so no-op telemetry methods keep
   their non-null return contracts;
3. convert the guarded `core-oj.jar` byte patches into durable source-level
   libcore/runtime fixes where possible;
4. close the `RestaurantModuleInteractor.s()` null dependency/state gap in
   `HomeDashboardActivity.onCreate`;
5. source-reproduce the accepted boot-owned Material class policy instead of
   relying on the current one-byte runtime derivative;
6. replace the strict-mode McD fragment lifecycle skips with a generic
   app-AndroidX compatible attach/transaction path;
7. make the attached `HomeDashboardFragment` produce visible real dashboard
   content through generic View rendering/layout;
8. isolate and fix the next `SIGBUS` if the next real dashboard step exposes a
   runtime dispatch fault;
9. rerun the real McDonald's APK after each runtime/shim change and accept only
   evidence that advances the real dashboard path;
10. keep the southbound contracts portable for OHOS.

Primary live docs:

- `docs/engine/WESTLAKE-STATUS.md`
- `docs/program/WESTLAKE_PLATFORM_FIRST_ISSUES.md`
- `docs/engine/REAL-APK-STATUS.md`

## Today Delivery Goal: Controlled Mock McD Profile Boundary

For 2026-04-28, the active delivery target is now PF-466: a controlled
mock McD-profile APK that exercises the next set of stock-app boundaries
before returning to the real McDonald's APK. This is not the real McDonald's
app and is not a stock APK compatibility claim. It is the current best OHOS
port target because it is self-contained, has a known app/API surface, and
runs through the same Westlake guest runtime path that the stock APK must
eventually use.

PF-466 is accepted on the connected phone `cfb7c9e3`. The delivered APK is
`com.westlake.mcdprofile`, built from `test-apps/10-mcd-profile/` and run with
`scripts/run-mcd-profile.sh`. The accepted run proves:

- app-owned `Application.onCreate()`;
- generic `WestlakeActivityThread` launch through `AppComponentFactory`,
  shim `Activity.attach(...)`, and `onCreate`/`onStart`/`onResume` dispatch
  inside Westlake, with the older controlled allocator rejected by the runner;
- compiled APK XML resource loading and inflation from
  `resources.arsc` and `activity_mcd_profile.xml` into a guest
  `LinearLayout` root;
- McD-profile XML tag traversal and ID binding for the app's Material-shaped
  tags, `ImageView`, `ListView`, cart `TextView`, checkout `MaterialButton`,
  and `BottomNavigationView`, with no `XML_TAG_WARN` markers;
- guest `ListView` adapter row binding through position `4`;
- controlled generic XML input/list sidecar: touch-file packets are converted
  to `MotionEvent`s and dispatched into the inflated View tree, checkout can
  be reached through the generic `MaterialButton` click path, and the XML
  `ListView` can invoke the app `AdapterView.performItemClick()` listener
  from a real laid-out ListView coordinate with `fallback=false`;
- XML layout measurement/layout probe at `480x1013` before direct-frame
  rendering;
- SharedPreferences-backed cart state;
- live JSON fetch through the portable host/OHBridge HTTP bridge;
- one bounded live image fetch through the same bridge;
- guest libcore `String.getBytes("UTF-8")` through the Westlake runtime for
  a REST payload;
- REST bridge v2 POST with a real payload, HEAD, and non-2xx status coverage
  through the supervisor proxy;
- full-phone `1080x2280` `DLST` frames for launch/network/menu/cart,
  repeated-cart, and post-checkout navigation states, with strict touch-file
  input through category, row select, second cart add, checkout, Deals
  navigation, and Menu navigation markers.

Accepted PF-466 hashes:

- `dalvikvm=2dd479e0c7f98e8fd3c4c09b539bfe30fe1c39b119d36e034af68c6bcaada6cf`
- `aosp-shim.dex=d548351815ba5d8a700b7dd48089d652ec43623b032383738d036ae30740949d`
- `westlake-host.apk=d6d8e81a801bb799a815039abc0b296416c723a11f2c31547077ddb87cad7c68`
- `westlake-mcd-profile-debug.apk=50477eccecc86fa5ecd8144d26b3930ec60d68c3b952708d66aba934ea448933`

Accepted PF-466 artifacts:

- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.*`
- `/mnt/c/Users/dspfa/TempWestlake/accepted/mcd_profile/d548351815ba5d8a700b7dd48089d652ec43623b032383738d036ae30740949d_50477eccecc86fa5ecd8144d26b3930ec60d68c3b952708d66aba934ea448933/`

Key accepted PF-466 markers:

- `MCD_PROFILE_GENERIC_ACTIVITY_FACTORY_OK class=com.westlake.mcdprofile.McdProfileActivity factory=default`
- `MCD_PROFILE_WAT_ACTIVITY_LAUNCH_OK class=com.westlake.mcdprofile.McdProfileActivity`
- `MCD_PROFILE_WAT_ACTIVITY_ONCREATE_OK class=com.westlake.mcdprofile.McdProfileActivity`
- `MCD_PROFILE_WAT_ACTIVITY_RESUME_OK class=com.westlake.mcdprofile.McdProfileActivity`
- `MCD_PROFILE_XML_RESOURCE_WIRE_OK engine=true table=true apk=true resDir=true arsc=2528 layouts=1 layoutBytes=4112`
- `MCD_PROFILE_XML_BIND_OK list=true ... materialViews=10`
- `MCD_PROFILE_ADAPTER_GET_VIEW_OK position=4`
- `MCD_PROFILE_GENERIC_LIST_BOUNDS_OK ... top=568 ... bottom=709 ... children=1 first=4 count=5 adapter=android.widget.ListView`
- `MCD_PROFILE_GENERIC_TOUCH_OK ... action=touch_up ... adapter=true adapterClick=true position=4`
- `MCD_PROFILE_GENERIC_LIST_HIT_OK ... position=4 ... clicked=true fallback=false adapter=android.widget.ListView`
- `MCD_PROFILE_ADAPTER_ITEM_CLICK_OK position=4 id=4 count=5`
- `MCD_PROFILE_GENERIC_CLICK_OK ... target=com.google.android.material.button.MaterialButton`
- `MCD_PROFILE_XML_LAYOUT_PROBE_OK target=480x1013 measured=480x1013`
- `MCD_PROFILE_XML_INFLATE_OK ... views=25 materialViews=10 source=compiled_apk_xml`
- `MCD_PROFILE_CHARSET_UTF8_OK bytes=24`
- `MCD_PROFILE_REST_POST_OK status=200 bytes=100 protocol=2 transport=host_bridge`
- `MCD_PROFILE_REST_HEAD_OK status=200 bytes=0`
- `MCD_PROFILE_REST_MATRIX_OK post=200 head=200 status=418 transport=host_bridge`
- `MCD_PROFILE_CART_ADD_OK count=2 totalCents=1178 ...`
- `MCD_PROFILE_CHECKOUT_OK count=2 totalCents=1178 storage=true`
- `MCD_PROFILE_DIRECT_FRAME_OK reason=checkout_touch_up ... checkedOut=true`
- `MCD_PROFILE_NAV_DEALS_OK network=1`
- `MCD_PROFILE_NAV_MENU_OK tab=menu`

PF-466 does not close stock McDonald's APK readiness. It is our controlled
mock app boundary test. The gap list that must be closed next is:

- generalize the accepted McD-profile WAT/AppComponentFactory launch slice to
  arbitrary stock McDonald's activities and remove the remaining app-specific
  launch allowances;
- fix the standalone runtime object-array/new-array boundary that forced the
  profile app away from `String[]` item models; the PF-466 resource-table
  `String[]` failure is closed by storing parsed pools as `Object[]`, but the
  broader DEX/runtime array boundary is not proven closed;
- broaden standalone libcore charset/encoding correctness. PF-466 now accepts
  normal app `String.getBytes("UTF-8")`; startup stdio still uses the
  ASCII-safe wrapper and broader `Charset` provider/default-encoding behavior
  must be proven before a stock APK claim;
- make Material XML inflation generic enough for upstream Google Material
  Components tags, IDs, theming, Coordinator/AppBar behaviors, ripple, and
  animation;
- replace the McD-specific direct `DLST` frame writer and coordinate router
  with generic View draw, hit testing, scrolling, and adapter rendering.
  PF-475 proves a controlled sidecar for generic `MotionEvent` dispatch,
  `MaterialButton` click, and real-coordinate `ListView` item-click invocation
  without the former adapter fallback; broad visible generic rendering and pure
  `AdapterView` touch-dispatch item click remain open;
- keep the PF-474 controlled direct-frame fix under stress: repeated-cart and
  post-checkout frames are now accepted by coalescing touch-driven dirty
  invalidation into the touch frame instead of emitting a redundant immediate
  dirty frame;
- expand image/network transport from one capped image proof and proxy-backed
  POST/HEAD/status probes to multi-image, large-body, streamed responses,
  redirects, timeouts, and direct libcore networking parity;
- implement the same host/OHBridge southbound contracts in the OHOS host:
  Ability/XComponent surface, input file or callback bridge, app data
  directory, HTTP bridge, and `DLST` replay.

OHOS integration guide:
`docs/engine/OHOS-MCD-PROFILE-INTEGRATION.md`.

## Today Delivery Goal: Controlled Android Showcase

For 2026-04-26, delivery is re-scoped to a controlled local Android showcase
app that runs through Westlake first, then becomes the OHOS portability proof.
This app is intentionally not a stock APK stress test.

OHOS integration guide:
`docs/engine/OHOS-CONTROLLED-SHOWCASE-INTEGRATION.md`. The controlled showcase
is accepted as the first OHOS port target because Westlake owns its app surface
and can force known platform seams. It does not claim arbitrary stock Android
APK compatibility.

Same-day acceptance update: PF-451 is accepted on the connected phone
`cfb7c9e3`, and PF-452 is accepted for the Android phone host/OHBridge network
path. The delivered app is `com.westlake.showcase`, a self-contained
Noice-style local ambient mixer with a Yelp-like Discover venue panel, built
from `test-apps/05-controlled-showcase/` and run with
`scripts/run-controlled-showcase.sh`. The accepted evidence proves the owned
APK XML is inflated from compiled AXML into a real guest View tree
(`89` views / `49` text widgets), binds expected IDs, completes a layout probe,
renders a visible phone frame from the XML tree, and records real strict-mode
touch, navigation, venue load, next venue, review, and export markers.

Follow-up same-day acceptance update: PF-453 is accepted on the same phone as a
separate Yelp-like live-data APK, not a page inside the Noice showcase. The
delivered app is `com.westlake.yelplive`, built from
`test-apps/06-yelp-live/` and run with `scripts/run-yelp-live.sh`. It proves a
controlled app can use Westlake guest code to fetch live HTTPS JSON and image
bytes through the portable host/OHBridge HTTP bridge, update app state, render
through the direct `DLST` path, and respond to touch-driven scroll, Details,
Save/Saved, and Search flows. This is not the official Yelp API; it is a
Yelp-like live-data proof using a public no-key feed plus bounded live remote
thumbnails. The current visual gate requires a real photo region, not only
colored blocks, and the accepted UI shows a Material-styled scrollable
restaurant list with elevated search, selected filter/category chips, card
rows, a white bottom navigation bar, five visible live-thumbnail rows after a
swipe, row details, and save state.

Additional same-day acceptance update: PF-454 is accepted on the same phone as
a separate Material Components API canary. The delivered app is
`com.westlake.materialyelp`, built from `test-apps/07-material-yelp/` and run
with `scripts/run-material-yelp.sh`. It directly instantiates a first
Westlake-owned `com.google.android.material.*` shim slice including
`MaterialCardView`, `MaterialButton`, `ChipGroup`, `Chip`,
`TextInputLayout`, `TextInputEditText`, `Slider`, `BottomNavigationView`, and
`FloatingActionButton`; it records app-owned `Application` and `Activity`
markers; it renders a visible Chinese Material/Yelp-style screen through direct
`DLST` with UTF-8 text, preserved host frame aspect ratio, and four
host-bridge network image tiles; and strict phone touch packets drive filters,
row selection, Save/Saved, and Search. This is not a full upstream Google
Material Components AAR compatibility claim. The latest run used
`SUPERVISOR_HTTP_PROXY=1` because the phone DNS resolver could not resolve
`dummyjson.com`; the guest still fetched through the Westlake host/OHBridge
bridge, with outbound HTTP routed through ADB reverse by the Android host.

Supervisor update, 2026-04-27:

- PF-455 now has a phone-accepted XML-backed Yelp slice on `cfb7c9e3`. The
  latest accepted `scripts/run-yelp-live.sh` run proves
  `com.westlake.yelplive` loads compiled layout bytes from the APK, inflates
  `yelp_live_activity.xml`, binds the expected title/status/card/list/button
  IDs plus a real `android.widget.ListView`, lays out at `480x1013`, renders
  through a full-phone `1080x2280` host surface, and preserves the live
  REST/image, scroll, row selection, Details, Save/Saved, and Search
  interactions. Evidence markers include `YELP_XML_RESOURCE_WIRE_OK`,
  `YELP_XML_INFLATE_OK views=30 texts=21`,
  `YELP_XML_BIND_OK buttons=5`,
  `YELP_XML_LAYOUT_PROBE_OK target=480x1013 measured=480x1013`,
  `YELP_GENERIC_VIEW_DRAW_OK views=27 texts=17 buttons=13 images=0 lists=1
  listRows=5 listImages=5 height=1013`,
  `YELP_GENERIC_LIST_DRAW_OK rows=5 images=5`,
  `YELP_GENERIC_VISIBLE_LIST_OK rows=5 images=5`,
  `YELP_FULL_RES_FRAME_OK logical=480x1013 target=1080x2280`,
  `YELP_NETWORK_BRIDGE_OK`, `YELP_LIVE_JSON_OK`, `YELP_LIVE_ROW_IMAGE_OK`,
  `YELP_LIST_SCROLL_OK`, `YELP_DETAILS_OPEN_OK`, `YELP_SAVE_PLACE_OK`, and
  `YELP_NAV_SEARCH_OK`. PF-459 is accepted for a first generic inflated-View
  DLST draw slice. PF-460 now accepts a wider non-disruptive generic XML slice:
  `<ScrollView>` inflates as `android.widget.ScrollView`, `Search`, `Details`,
  and `Saved` XML `Button.performClick()` listeners fire, and the inflated
  `ScrollView` is discovered by the generic scroll probe. PF-461 now accepts a
  first adapter/list virtualization slice: the XML tree contains a
  `ListView`, a guest `BaseAdapter` binds five visible rows, five live image
  rows rebind to `ImageView` row slots from downloaded byte payloads, and
  `ListView.performItemClick()` reaches the APK's row listener. Evidence
  includes
  `YELP_GENERIC_HIT_OK` for `text=Search`, `text=Details`, and `text=Saved`,
  `YELP_GENERIC_SCROLL_OK container=android.widget.ScrollView
  source=inflated_xml`, `YELP_ADAPTER_ATTACH_OK class=android.widget.ListView`,
  `YELP_ADAPTER_NOTIFY_OK images=5`, `YELP_ADAPTER_IMAGE_BIND_OK position=4`,
  `YELP_GENERIC_ADAPTER_ITEM_CLICK_OK position=2`, and
  `YELP_ADAPTER_ITEM_CLICK_OK position=2`. A follow-up phone-visible gate also
  records `YELP_VISUAL_DELTA_V4_OK surface=adapter_feed` and
  `adapter_teal_samples=697`, so this slice is visible on the screenshot as an
  XML ListView/BaseAdapter adapter-feed ribbon instead of only hidden markers.
  Remaining PF-455/PF-459/PF-460/PF-461 gap: the visible Yelp frame is still
  the controlled direct `DLST` renderer, not a full-fidelity generic Android
  `draw()` pass over every inflated widget; the generic list renderer is still
  a controlled Yelp overlay from activity row state; raw
  `Bitmap.createFromImageData` / Bitmap-backed `ImageView` decode remains a
  crash gap; and broad coordinate hit dispatch / full visible scroll routing is
  still open.
- PF-456 is now accepted on the Android phone for the bridge v2 REST contract:
  the guest calls a v2 HTTP bridge shape with method, headers JSON, request
  body, max-byte cap, timeout, redirect policy, response headers, non-2xx
  bodies, and truncation/error metadata. The accepted Yelp run proves real live
  GET JSON/image traffic through the host bridge and records the bridge v2 REST
  marker contract for POST, headers, PUT/PATCH/DELETE, HEAD, non-2xx status,
  redirect, truncation, and timeout. Current caveat: the multi-method REST
  matrix is temporarily synthetic after a VM SIGBUS in the real matrix path, so
  stock-style `HttpURLConnection` / libcore networking and the real matrix must
  still be closed before this becomes a broad networking claim. The remaining
  PF-456 closure is OHOS adapter parity under the same guest-facing contract.
- PF-457 now has a phone-accepted Material XML probe slice. The delivered app
  `com.westlake.materialxmlprobe`, built from
  `test-apps/09-material-xml-probe/` and run with
  `scripts/run-material-xml-probe.sh`, proves compiled XML inflation of Material
  FQN tags into Westlake-owned shim classes, direct DLST tree rendering of that
  inflated tree, and generic `findViewAt(...).performClick()` hit routing into
  the APK's `MaterialButton` listener. Evidence markers include
  `MATERIAL_XML_TAG_OK` for `TextInputLayout`, `TextInputEditText`,
  `MaterialCardView`, `ChipGroup`, `Chip`, `Slider`, `MaterialButton`, and
  `BottomNavigationView`, plus `MATERIAL_GENERIC_RENDER_OK`,
  `MATERIAL_GENERIC_BUTTON_BOUNDS`, `MATERIAL_GENERIC_TOUCH_OK`, and
  `MATERIAL_GENERIC_HIT_OK`. This does not close upstream Google Material
  Components AAR compatibility, full Material theming, Coordinator/AppBar
  behavior, ripple/animation, or broad generic Android hit testing.

Supervisor handoff update: the separate OHOS Yelp live porting guide is now the
source of truth for agents attempting the first controlled app port to OHOS:
`docs/engine/OHOS-YELP-LIVE-PORTING-GUIDE.md`. It also defines the next
southbound shim ladder, from REST matrix and generic View draw/hit testing to
adapter/list virtualization, Material compatibility, lifecycle/storage/service
probes, and finally a McDonald's preflight profile before returning to the
stock McDonald's APK.

Accepted PF-451 hashes:
- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=b498750dce8e022c3e0a30c402ef652ec396d8b04cc2dc66e295ec6ddfbe3854`
- `westlake-showcase-debug.apk=bcd8d63eb2af3d2342110a5df97afd581cc3154d96d96c3de34306597ba5064d`
- Artifacts: `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.*`
- Screenshot acceptance: `controlled_showcase_target.png` shows the controlled
  Noice-style UI after real navigation to Settings on the real phone, including
  a Yelp-like Discover card for `Rain Room`, 4.5 stars, 85 reviews, and the
  host/OHBridge network marker path.
- Visual gate acceptance: `controlled_showcase_target.visual` records screenshot
  dimensions, color diversity, and Settings navigation-highlight samples
  (`1080x2280`, `distinct_colors=2527`, `settings_nav_teal_samples=744`).

Accepted PF-453 hashes:
- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=7f52c37ac29502b57f36a692d9c835e535ec8cfd7f64cb45e2f31f9c659828d1`
- `westlake-yelp-live-debug.apk=24d1444b5ebf2319722c7168b4a849b7f022cc869b1708734695e381c44abfda`
- Artifacts: `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.*`
- Stable accepted copy:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/yelp_live/7f52c37ac29502b57f36a692d9c835e535ec8cfd7f64cb45e2f31f9c659828d1_24d1444b5ebf2319722c7168b4a849b7f022cc869b1708734695e381c44abfda/`
- Screenshot acceptance: `yelp_live_target.png` shows the Yelp-like live app
  after accepted network and navigation actions, with a red Yelp-style header,
  loaded live list data, five host-rendered remote thumbnails after scroll,
  save controls, bottom navigation, and a host log marker proving
  `Surface buffer 1080x2280 for com.westlake.yelplive`.
- Visual gate acceptance: `yelp_live_target.visual` records
  `1080x2280`, `distinct_colors=5593`, `top_red_samples=3033`,
  `bottom_nav_light_samples=5199`, `bottom_nav_red_samples=26`,
  `photo_distinct_colors=11441`, `photo_colored_samples=17221`, and
  `photo_natural_samples=5053`.

Accepted current PF-455/PF-456/PF-459/PF-460/PF-461 Yelp hashes:
- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`
- `westlake-yelp-live-debug.apk=a677a8f36e498a8f7c6834a9dc4d10bdc5fa03d7a48c91c8bdc00c8138b6866b`
- Stable accepted copy:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/yelp_live/eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d_a677a8f36e498a8f7c6834a9dc4d10bdc5fa03d7a48c91c8bdc00c8138b6866b/`
- Added PF-460 evidence: `YELP_XML_INFLATE_OK root=android.widget.ScrollView`,
  `YELP_GENERIC_HIT_OK` with `text=Search`, `text=Details`, and `text=Saved`,
  and `YELP_GENERIC_SCROLL_OK container=android.widget.ScrollView
  source=inflated_xml`.
- Added PF-461 evidence: `YELP_ADAPTER_ATTACH_OK
  class=android.widget.ListView`, `YELP_ADAPTER_BIND_PROBE_OK rows=5`,
  `YELP_ADAPTER_NOTIFY_OK images=5`, `YELP_ADAPTER_IMAGE_REBIND_OK index=4`,
  `YELP_ADAPTER_IMAGE_BIND_OK position=4 bitmap=false imageView=true`,
  `YELP_GENERIC_ADAPTER_ITEM_CLICK_OK position=2`, and
  `YELP_ADAPTER_ITEM_CLICK_OK position=2`, while the same run retains
  `YELP_REST_MATRIX_OK`, `YELP_LIVE_ROW_IMAGE_OK index=4`,
  `YELP_LIST_SCROLL_OK`, `YELP_DETAILS_OPEN_OK`, `YELP_SAVE_PLACE_OK`, and
  `YELP_NAV_SEARCH_OK`.
- Added visible PF-461 delta evidence: `YELP_VISUAL_DELTA_V4_OK
  surface=adapter_feed adapterBadge=true visibleImages=5 rows=5
  materialRibbon=true`, with `adapter_teal_samples=697` in
  `yelp_live_target.visual`.

Accepted PF-454 hashes:
- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=20fc0c98f9a9371f12deae0d347a01a033e41629a5797aee1cf70d5c39245726`
- `westlake-material-yelp-debug.apk=e586d7afd7df1a2a8c418fb18de952c032dd44c456a3bbf952799c363711ba66`
- `westlake-host app-debug.apk=bc9268855a05d5cda61490fdbf02a297e77bde844e82ef1b279e159304dcaac8`
- Artifacts: `/mnt/c/Users/dspfa/TempWestlake/material_yelp_target.*`
- Stable accepted copy:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/material_yelp/20fc0c98f9a9371f12deae0d347a01a033e41629a5797aee1cf70d5c39245726_e586d7afd7df1a2a8c418fb18de952c032dd44c456a3bbf952799c363711ba66/`
- Screenshot acceptance: `material_yelp_target.png` shows a Chinese
  Material/Yelp-style search screen with red app bar, elevated search, chips,
  slider, four network image tiles, result cards, save affordances, FAB, bottom
  navigation, black letterbox bands from aspect-ratio-preserved rendering, and a
  host log marker proving `Surface buffer 1080x1800 for
  com.westlake.materialyelp`.
- Visual gate acceptance: `material_yelp_target.visual` records
  `1080x2280`, `content_scale=2.2500`, `content_offset=0.0,240.0`,
  `distinct_colors=313`, `top_red_samples=3812`,
  `bottom_nav_light_samples=7194`, `bottom_nav_red_samples=23`,
  `card_light_samples=19872`, `photo_distinct_colors=28`, and
  `photo_colored_samples=564`.

Accepted PF-457 Material XML probe hashes:
- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=bf33aba0a8923e8b7d2cb006ee98042bb217021236a7cfe185a004f0e269716a`
- `westlake-material-xml-probe-debug.apk=ded93614084cdd28a46bcbcbd7eb8cba78504c3c228e0f95835a6ebf42a6e6c9`
- Artifacts: `/mnt/c/Users/dspfa/TempWestlake/material_xml_probe_target.*`
- Screenshot acceptance: `material_xml_probe_target.png` shows the inflated
  Material XML tree rendered through the Westlake DLST pipe on the phone.

Harness note: the runner now checks the phone default network before launch,
supports explicit `SUPERVISOR_HTTP_PROXY=1` for ADB-reverse supervisor network
testing when the device is offline, and copies passed artifacts to a
hash-stamped accepted directory.

PF-451 does not close the generic surface/runtime work. The accepted showcase
rendering path is a controlled direct `DLST` frame writer; generic
`Activity.renderFrame`/View-tree rendering through `OHBridge.surfaceCreate`
remains open under PF-302 because `surfaceCreate` is not registered in the
subprocess path and faults when invoked.
It also leaves explicit PF-451 follow-up work: `resources.arsc` table parsing
still fails with `ArrayStoreException`, arbitrary widget mutation needs a
broader runtime proof, and strict subprocess live input is still
showcase-specific hit routing rather than generic Android View hit testing.
The Yelp-like network panel now proves the selected portable network path:
guest app logic requests venue JSON and image bytes through the host/OHBridge
HTTP bridge and records `SHOWCASE_NETWORK_HOST_BRIDGE_OK`,
`SHOWCASE_NETWORK_JSON_OK`, and `SHOWCASE_NETWORK_IMAGE_OK` with
`transport=host_bridge`. `SHOWCASE_NETWORK_NATIVE_GAP_OK` is no longer accepted
in the bridge-required run. Full Java/libcore networking remains a later
compatibility track; missing runtime pieces still include `java.net.URL`,
`libcore.io.Linux.android_getaddrinfo`, and related socket/unsafe
initialization.

Required same-day outcome:
- build one Android APK whose API surface we own
- run it through Westlake `dalvikvm` on the connected phone
- show visible app UI on the phone, backed by screenshot/log/pid evidence
- keep app logic on the Westlake guest plane, not stock phone ART
- prove at least one interactive flow with app-owned markers or equivalent
  app-side evidence

Allowed app surface for the first showcase:
- plain `Activity`, AppCompat-style Activity, Fragments, XML resources, themes
- programmatic and XML views: text, buttons, lists, forms, cards, toolbar-like
  header, dialogs, simple tabs or bottom-navigation-like controls
- local-only state: in-memory model, simple saved state, ViewModel/SavedState,
  local preferences or small local persistence if already supported
- UI behavior: scrolling, selection, detail screen, cart/settings-style flow,
  recreate/restore, basic animations or transitions where they do not require
  unsupported render services

Excluded from the same-day app:
- Google Play Services/GMS, Firebase, Maps, push notifications, sign-in SDKs
- required backend services, auth, payments, live network API correctness
- camera/media/location/Bluetooth/telephony/service-provider depth
- Compose-first UI, WebView-heavy UI, or native SDK integrations unless isolated
  as optional later probes

PF-453 deliberately uses a public no-key live endpoint as a network transport
proof. That is not a product/backend dependency claim; acceptance is scoped to
the portable host/OHBridge HTTP bridge and app-side handling of live JSON/image
bytes.

PF-454 deliberately uses a controlled first-slice Material shim. It is a real
guest import/instantiation proof for the Material namespace Westlake currently
owns, not a guarantee that arbitrary upstream Material Components apps will run
without expanding the shim surface.

New contract workstreams added on 2026-04-27:

- `PF-455` XML-backed Yelp app path. The Yelp-like app must move from a
  programmatic/direct-render canary into a real APK layout path: compiled XML
  resources are loaded, `Activity.setContentView(...)` or equivalent inflation
  creates the guest View tree, IDs bind from resources, list/search/filter/card
  state is driven from that tree, and the same phone interactions still pass on
  a 1K-class render surface. Programmatic fallback may stay as a diagnostic, but
  it cannot satisfy this workstream. Status: XML inflation/binding and live
  flows are now phone-accepted; PF-459 accepts a first generic inflated-View
  DLST draw slice; PF-460 accepts multiple inflated XML `Button.performClick()`
  listener slices plus actual `ScrollView` inflation/probing; PF-461 accepts a
  first XML `ListView`/`BaseAdapter` row binding and image rebinding slice.
  Full-fidelity generic View-tree rendering, broad generic touch/scroll
  dispatch, and RecyclerView-class virtualization remain open.
- `PF-456` portable REST networking completeness. The Westlake app-facing
  network surface must support the controlled app's real API demands through a
  stable guest-to-host contract on Android and OHOS: HTTPS, headers, methods,
  status/error handling, JSON, binary image bodies, timeouts, bounded payloads,
  redirects, and repeatable failure markers. Android phone acceptance now
  includes real Yelp live GET JSON/image traffic plus the Yelp bridge v2 REST
  marker contract. The current multi-method matrix markers are synthetic
  placeholders for a real matrix path that still SIGBUSes, so phone ART
  networking, host-only fetches, local generated data, or synthetic REST
  markers cannot count as final broad networking evidence.
- `PF-457` Material and generic UI compatibility expansion. The accepted
  PF-454 canary and PF-457 Material XML probe are still controlled slices. The
  open workstream covers full upstream Google Material Components AAR
  compatibility, full Material theming, `CoordinatorLayout`/`AppBarLayout`
  behavior, ripple and animation systems, and broader generic Android View hit
  testing/rendering. Until this passes, Westlake can claim controlled
  Material-style and Material XML proofs, not broad Material app compatibility.

Complexity target: the showcase can be richer than a canary and should look like
a real local app, but it must stay inside the known green Westlake surface. It
should demonstrate breadth of UI and app behavior, not every Android API.

## 2026-04-25 Code Review Judgement

Architecture direction remains valid, but the implementation must be tightened
before it can converge on the goal.

Keep as the main architecture:
- standalone Westlake `dalvikvm` owns guest APK execution
- Android control backend and OHOS target backend remain explicit modes
- the canary ladder is the primary acceptance vehicle
- OHOS integration happens below a stable Android-semantics boundary
- the controlled showcase OHOS port uses the documented Ability/XComponent,
  DLST/display, input, file, logging, and host/OHBridge HTTP seams

Do not count as delivery evidence:
- host-side APK execution through phone ART or `HostBridge`
- launcher-authored `CANARY_*_OK` markers
- Activity object construction without app-owned lifecycle markers
- McDonald's-specific no-op, DI, dashboard, or splash surgery in the generic path
- broad ART survival patches that only mark failed classes initialized

Current accepted frontier:
- Westlake now passes cutoff canary `L0`/`L1`/`L2` on the phone in both
  `control_android_backend` and `target_ohos_backend`
- Westlake now passes cutoff canary `L3` in `target_ohos_backend` on the
  phone. `L3` proves app-owned `AppCompatActivity` inheritance, AndroidX
  `FragmentTransaction.add(...)`, `commitNow()`, fragment view creation,
  fragment `onStart`/`onResume`, and activity `onStart`/`onResume`.
- accepted evidence is the marker file written by canary APK code:
  `CANARY_L0_OK`, `CANARY_L1_ON_CREATE`, `CANARY_L1_OK`,
  `CANARY_L1_ON_START`, `CANARY_L1_ON_RESUME`, `CANARY_L2_ON_CREATE`,
  `CANARY_L2_OK`, `CANARY_L2_ON_START`, `CANARY_L2_ON_RESUME`,
  `CANARY_L3_FRAGMENT_ADD_OK`, `CANARY_L3_FRAGMENT_VIEW_OK`,
  `CANARY_L3_FRAGMENT_ON_RESUME`, `CANARY_L3_FRAGMENT_COMMIT_OK`,
  `CANARY_L3_OK`, `CANARY_L3_ON_START`, and `CANARY_L3_ON_RESUME`
- validated runtime hashes for the accepted `target L3` run:
  `dalvikvm=d020846653627d90429fbd88b8fc4b8029634389422c1fab1cfdf8a8c314b120`,
  `aosp-shim.dex=80557721467673a09cff28462707f8880afcb601ae885b3903c0b58b6212b65c`
- validated runtime hashes for the accepted `target L3LOOKUP` run:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`,
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`
- `L1` is intentionally a plain `Activity` plus programmatic `View`
  content boundary; `L2` covers widget/theme/XML resource inflation; `L3`
  covers the smallest AndroidX/AppCompat fragment lifecycle slice
- `L3LOOKUP` additionally proves `FragmentManager.findFragmentById(...)`,
  `getFragments()`, and `findFragmentByTag(...)` across the app-dex to shim-dex
  boundary with app-owned `CANARY_L3_FRAGMENT_LOOKUP_OK`
- `L3IFACE` additionally proves app-dex `java.util.List.get(I)` dispatch on
  `FragmentManager.getFragments()` with app-owned
  `CANARY_L3_FRAGMENT_INTERFACE_GET_OK`
- Westlake now passes cutoff canary `L4` in `target_ohos_backend` on the phone.
  `L4` proves Activity and Fragment `SavedStateRegistry` owner surfaces,
  saved-state provider registration/readback, and retained `ViewModelProvider`
  instances through `ViewModelStore`.
- Westlake now passes cutoff canary `L4STATE` in `target_ohos_backend` on the
  phone. `L4STATE` proves `SavedStateRegistry` save/restore/one-shot consume,
  public `SavedStateHandle` accessors, `CreationExtras` delivery through
  `ViewModelProvider.Factory.create(Class, CreationExtras)`, and Activity plus
  Fragment ViewTree lifecycle/viewmodel/savedstate owner propagation.
- Westlake now passes cutoff canary `L4RECREATE` in `target_ohos_backend` on
  the phone. `L4RECREATE` proves `Activity.recreate()` on the current cutoff
  `MiniActivityManager` path: app-owned save-state, pause, stop, destroy, fresh
  Activity instance, restored direct state, restored `SavedStateRegistry`
  provider state, and resumed recreated Activity.
- Westlake now passes cutoff canary `L4WATRECREATE` in `target_ohos_backend` on
  the phone. `L4WATRECREATE` proves a `WestlakeActivityThread`-owned
  launch/recreate sequence for the cutoff canary: save, pause, stop, destroy,
  relaunch, restore, resume, fresh Activity instance, and restored
  `SavedStateRegistry` state. The accepted trace includes
  `CV canary WAT launch begin`, `CV WAT precreate savedstate returned`,
  `CV WAT recreate begin`, and `CV WAT recreate end`.
- Westlake now passes cutoff canary `L4WATFACTORY` in `target_ohos_backend` on
  the phone. `L4WATFACTORY` proves canary manifest
  `android:appComponentFactory` discovery, factory installation through
  `WestlakeActivityThread`, custom factory construction, and custom
  `instantiateActivity(...)` on initial launch and recreate. The accepted trace
  includes `CV canary WAT factory manifest parsed
  com.westlake.cutoffcanary.CanaryAppComponentFactory` and
  `CV canary WAT factory set done`; app markers include
  `CANARY_L4FACTORY_CTOR_OK`, two
  `CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`, and two
  `CANARY_L4FACTORY_ACTIVITY_RETURNED_OK`.
- Westlake now passes cutoff canary `L4WATAPPFACTORY` in `target_ohos_backend`
  on the phone. `L4WATAPPFACTORY` proves launcher-created `CanaryApp` is
  skipped, WAT calls pre-Activity `makeApplication()`, the controlled canary
  factory enters and returns `instantiateApplication(...)`, and WAT calls
  `Application.onCreate()`. The accepted trace includes
  `CV canary application manual skipped app factory`,
  `CV WAT app factory preactivity makeApplication begin`,
  `CV WAT instantiateApplication returned com.westlake.cutoffcanary.CanaryApp`,
  `CV WAT application onCreate returned`, and
  `CV WAT app factory preactivity makeApplication returned`; app markers include
  `CANARY_L4APPFACTORY_INSTANTIATE_APPLICATION_OK`,
  `CANARY_L4APPFACTORY_DIRECT_CANARY_APP_OK`, and
  `CANARY_L4APPFACTORY_APPLICATION_RETURNED_OK` before
  `CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`.
- Westlake now passes cutoff canary `L4WATAPPREFLECT` in `target_ohos_backend`
  on the phone. `L4WATAPPREFLECT` keeps the same WAT-owned
  Application-before-Activity ordering, but the canary factory delegates to the
  base `AppComponentFactory.instantiateApplication(...)` path instead of
  directly constructing `CanaryApp`. The accepted trace includes
  `CV WAT instantiateApplication reflect begin
  com.westlake.cutoffcanary.CanaryAppComponentFactory`,
  `PF301 strict factory application ctor call`,
  `PF301 strict factory application ctor returned`, and
  `CV WAT instantiateApplication reflect returned`; app markers include
  `CANARY_L4APPREFLECT_STAGE_OK`, `CANARY_L4APPREFLECT_SUPER_CALL`,
  `CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK`,
  `CANARY_L4APPREFLECT_SUPER_RETURNED`, and
  `CANARY_L4APPREFLECT_APPLICATION_RETURNED_OK` before the first Activity
  factory marker, with the direct canary constructor marker rejected.
- validated runtime hashes for the accepted `target L4WATAPPREFLECT` run and
  follow-up `target L4WATAPPFACTORY` / `target L4WATFACTORY` /
  `target L4WATRECREATE` / `target L4RECREATE` / `target L4STATE` /
  `target L4` / `target L3LOOKUP` / `target L3IFACE` regressions:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`,
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`,
  `cutoff-canary-debug.apk=cb167f3033c14ea3c2eecb40cff784319d5a5657d85f060c0b15905b8e1c4147`
- the canary runner now verifies the installed canary APK hash as part of
  runtime provenance, auto-installs the local APK when stale, and fails closed
  if the phone hash still differs.
- hash-stamped evidence bundle:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`
- caveat: the lookup proof is not yet the final production runtime shape. The
  latest accepted fix removed the active `AssignVTableIndexes()` sentinel by
  making `BitVector::ClearAllBits()` available from `bit_vector.h`. The latest
  accepted cleanup also removes/quarantines live `PFCUT` probe output from the
  `L4WATAPPREFLECT`, `L4WATAPPFACTORY`, `L4WATFACTORY`, `L4WATRECREATE`,
  `L4RECREATE`, `L4STATE`, `L4`, `L3LOOKUP`, and `L3IFACE` phone artifacts.
  `L4WATRECREATE`, `L4WATFACTORY`, `L4WATAPPFACTORY`, and
  `L4WATAPPREFLECT` are still convergence gates, not final
  generic McDonald's readiness: the WAT canary
  path now performs precreate named saved-state through
  `SavedStateRegistryOwner`, routes attach through shim-owned
  `Activity.attach(...)`, constructs the Activity through a default
  `AppComponentFactory.instantiateActivity(...)` path, and proves controlled
  custom/manifest factory `instantiateActivity(...)` on initial launch and
  relaunch. It also proves controlled custom
  `AppComponentFactory.instantiateApplication(...)` for the canary Application,
  first using a direct canary constructor path and now using the base reflective
  no-arg constructor path before Activity factory instantiation.
  The previous canary-specific `DataSourceHelper` skip is reduced to a
  McDonald's package/class guard. The runtime still must be audited for other
  unresolved helper symbols, broader interface dispatch coverage, Hilt-safe real
  APK factory semantics, Hilt-generated real APK Application construction,
  generic real-APK Application-before-Activity ordering, generic real-APK
  lifecycle convergence, and self-contained packaging before dashboard claims.
- the implementation is still a prototype harness, not yet a self-contained
  Android runtime package

## Active Milestones

- `P0` Guest execution purity
- `P1` Standalone guest boot
- `P2` Core Java/framework bootstrap
- `P3` Generic app host contract
- `P4` AndroidX/AppCompat host contract
- `P4.5` Controlled local Android showcase app on phone
- `P5` McDonald's real launch
- `P6` McDonald's real dashboard body
- `P7` Stable interaction

## Immediate Critical Path

Current accepted recovery branch shows:
- guest reaches `WestlakeLauncher.main()`
- old pre-`main` argv/classloader crash is cleared
- canary `Application.onCreate()` and `StageActivity` `onCreate`/`onStart`/
  `onResume` now write durable app-owned markers in both backend modes

Current active blockers:
- carry the accepted controlled showcase and Yelp live apps to OHOS host
  adapters with the same DLST, input, file, marker, and network contracts.
- move beyond the accepted PF-459 generic draw slice and PF-460 generic
  listener/ScrollView slices so the visible Yelp proof can be rendered and
  driven by the generic inflated View path, not the app-specific direct frame
  writer and touch router, while preserving the 1K-class phone render buffer.
- carry PF-456 portable REST networking to OHOS adapter parity. Android phone
  now accepts HTTP verbs, headers, status/errors, JSON bodies, images, timeout,
  redirect, truncation, and failure handling through the guest-facing bridge
  contract.
- expand PF-457 beyond the first Material shim slice before claiming upstream
  Google Material Components compatibility, full theming, Coordinator/AppBar
  behavior, ripples/animations, generic Material XML rendering, or broad
  generic hit testing.
- keep `L4WATHILTAPP` open as a real-APK/Hilt boundary, but do not let it block
  the controlled no-GMS showcase app unless the showcase chooses to use Hilt.
- A11 core-oj on A15 ART bootstrap debt: `sun.misc.Unsafe` wrappers,
  `ConcurrentHashMap`/`ObjectStreamField` initialization, stale JNI trampoline
  risk, and tolerated `System`/`Locale` clinit failures must be repaired or
  isolated
- productionize generic virtual/interface dispatch and standalone helper-symbol
  hygiene; `L3LOOKUP` now passes after the `BitVector::ClearAllBits()` fix, and
  `L3IFACE` proves app-facing `List.get(I)` dispatch. The
  `FragmentManager.mAdded` concrete `ArrayList` typing workaround is removed;
  broader interface-dispatch coverage remains open. A `dalvikvm`
  unresolved-symbol gate now runs during phone runtime sync.
- general fragment state APIs beyond the `L3LOOKUP` lookup slice
- validation hardening so launcher diagnostics and stale staged canary detection
  cannot masquerade as app success
- removal or quarantine of host fallback and McDonald's-specific runtime hacks
- replacement of ART tolerance patches with coherent boot/core-library behavior
- self-contained runtime packaging independent of Android host staging

So the current execution order is:
1. lock `L0`/`L1`/`L2`/`L3`/`L3LOOKUP`/`L3IFACE`/`L4`/`L4STATE`/`L4RECREATE`/
   `L4WATRECREATE`/`L4WATFACTORY`/`L4WATAPPFACTORY`/`L4WATAPPREFLECT`
   as regression gates with app-owned marker-only acceptance and exact runtime
   hashes
2. keep the unresolved-symbol gate in the runtime sync path and replace
   concrete-collection containment with a production virtual/interface dispatch
   fix in ART class linking / dispatch cache code
3. finish the remaining A11 `sun.misc.Unsafe`/stale-JNI cleanup exposed by
   reflection-heavy startup paths
4. build and run the controlled local Android showcase app on the phone with
   visible UI, input, screenshot, log, pid, and guest-plane evidence
5. converge that showcase on one generic lifecycle path owned by
   `WestlakeActivityThread`
6. package the showcase plus runtime as a self-contained Westlake bundle
7. promote the Yelp-like app through PF-455 XML inflation, PF-456 portable REST
   networking, PF-459 generic inflated-View drawing, PF-460 generic hit/scroll
   routing, PF-461 adapter/list virtualization, and PF-457 Material/generic UI
   expansion until the same app can serve as the OHOS validation target
8. move that bundle to the OHOS backend and replace remaining Android-host
   staging assumptions
9. after the controlled app is repeatable, broaden Hilt/real-APK lifecycle gates
10. only then resume McDonald's launch and UI work

## Delivery Rule

Platform-first means:
- no more counting fallback UI polish as progress
- no more broad launcher-side AndroidX replay as the primary strategy
- no more app-specific surgery before the platform gates above are green
- no more accepting constructor bypass, host fallback, or diagnostic markers as
  proof of app lifecycle correctness

Program execution rule:
- the critical path stays local
- sidecar analysis and audits are delegated to the agent swarm
- issue order follows dependency order
- internal restore/classify/bookkeeping loops do not become user-facing stop
  points

The detailed local tracker remains in:
- `/home/dspfac/openharmony/WESTLAKE-PLATFORM-FIRST-DELIVERY-CONTRACT.md`
- `/home/dspfac/openharmony/WESTLAKE-PLATFORM-FIRST-OPEN-ISSUES.md`
