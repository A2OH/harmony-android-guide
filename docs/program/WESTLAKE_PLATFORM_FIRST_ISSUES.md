# Westlake Platform-First Issue Map

Last updated: 2026-04-28

This file mirrors the active platform-first issue structure used for execution.

Umbrella issue:
- `Platform-first Westlake delivery program`: `A2OH/westlake#569`

Primary issue families:
- `PF-001` guest execution purity: `A2OH/westlake#558`
- `PF-101` standalone guest boot: `A2OH/westlake#559`
- `PF-201` `System` / `Properties` bootstrap: `A2OH/westlake#560`
- `PF-202` `ThreadLocal` / `AtomicInteger` bootstrap: `A2OH/westlake#561`
- `PF-203` `CaseMapper` / `Locale` bootstrap: `A2OH/westlake#562`
- `PF-204` `Looper` bootstrap: `A2OH/westlake#563`
- `PF-301` generic `ActivityThread` / `Activity` / `Window` startup: `A2OH/westlake#564`
- `PF-302` generic surface/input/render survival: `A2OH/westlake#565`
- `PF-401` app-owned AndroidX/AppCompat host integrity: `A2OH/westlake#566`
- `PF-451` controlled local Android showcase app: today delivery target
- `PF-452` guest networking/runtime bridge: Android phone host/OHBridge proof
  accepted; OHOS adapter implementation remains open
- `PF-453` separate Yelp-like live data app: Android phone proof accepted;
  OHOS adapter implementation remains open through the PF-452 bridge contract
- `PF-454` Material Components API canary: Android phone proof accepted for a
  first Westlake-owned `com.google.android.material.*` shim slice with Chinese
  UTF-8 DLST text, host-bridge network image tiles, and aspect-preserved host
  rendering; full upstream Material Components compatibility remains open
- `PF-455` XML-backed Yelp app path: Android phone proof accepted for compiled
  Yelp APK layout-byte registration, XML inflation, ID binding, layout probe,
  live data, and touch flows on a full-phone `1080x2280` host surface with
  logical `480x1013` guest coordinates; PF-459 now accepts a first generic
  inflated-View DLST draw slice, and PF-460 now accepts actual `ScrollView`
  inflation/probing plus multiple inflated XML `Button.performClick()`
  listener slices; PF-461 now accepts a first XML `ListView`/`BaseAdapter`
  binding, downloaded row image-byte rebinding, and generic adapter item-click
  slice;
  full-fidelity replacement of the controlled direct `DLST` frame writer and
  touch router remains open
- `PF-456` portable REST networking completeness: Android host bridge v2 is
  accepted on phone for real live GET JSON/image traffic and the bridge v2 REST
  marker contract; the real multi-method matrix still has a VM SIGBUS gap and
  is represented by synthetic matrix markers in the current Yelp proof; OHOS
  adapter parity remains open
- `PF-457` Material/generic UI compatibility expansion: Android phone proof
  accepted for compiled Material XML tag inflation, direct DLST tree rendering,
  MaterialButton bounds discovery, and generic `findViewAt/performClick` hit
  into an app listener; upstream Material Components AAR compatibility, theming,
  Coordinator/AppBar behavior, ripple/animation, and broad hit testing remain
  open
- `PF-501` McDonald's launch validation: `A2OH/westlake#567`
- `PF-601` real McDonald's dashboard body takeover: `A2OH/westlake#568`
- `PF-801` reproducibility and evidence discipline: `A2OH/westlake#570`

Detailed OHOS handoff and the southbound shim ladder from controlled Yelp to a
McDonald's-class stock APK are documented in
`docs/engine/OHOS-YELP-LIVE-PORTING-GUIDE.md`. The new ladder workstreams are:

- `PF-458` REST matrix probe: Android phone accepted for methods, headers,
  body upload, redirects, timeouts, truncation, and non-2xx bodies at the
  marker-contract level; real multi-method execution remains open after the
  current VM SIGBUS gap
- `PF-459` generic inflated View draw path for the Yelp XML tree: Android
  phone accepted for the first DLST serialization slice
- `PF-460` generic View hit testing and scroll containers: Android phone
  accepted for non-disruptive inflated XML `Button.performClick()` hits into
  Yelp `Search`, `Details`, and `Saved` listeners, plus actual `ScrollView`
  inflation/discovery; broad coordinate hit dispatch and full scroll routing
  remain open
- `PF-461` adapter/list virtualization and image rebinding: Android phone
  accepted for the first Yelp `ListView`/`BaseAdapter` row-binding slice;
  RecyclerView-class virtualization remains open
- `PF-462` upstream-compatible Material shim expansion
- `PF-463` lifecycle/recreate/back-stack/state stress
- `PF-464` preferences/cache/file/database storage
- `PF-465` service probes for text input, connectivity, locale/time, and
  accessibility-shaped metadata
- `PF-466` McDonald's preflight controlled mock app profile before returning
  to the stock APK path: Android phone accepted on `cfb7c9e3` for
  `com.westlake.mcdprofile`, built from `test-apps/10-mcd-profile/` and run
  with `scripts/run-mcd-profile.sh`. The accepted proof covers app-owned
  Application, generic `WestlakeActivityThread` launch through
  `AppComponentFactory`, attach/lifecycle, compiled XML resource loading before
  `onCreate`, `resources.arsc` table parsing for this APK, Material-shaped tag
  traversal and ID binding, guest `ListView` adapter row binding through
  position `4`, XML measure/layout probe,
  SharedPreferences cart state, host/OHBridge live JSON and one bounded image,
  guest `String.getBytes("UTF-8")` for a REST payload, REST bridge v2 POST
  with payload, HEAD, and non-2xx status probes, full-phone `1080x2280` `DLST`
  before checkout, and strict touch navigation. Current accepted hashes:
  `dalvikvm=2dd479e0c7f98e8fd3c4c09b539bfe30fe1c39b119d36e034af68c6bcaada6cf`,
  `aosp-shim.dex=5f14bf74ba30adecc73c99f7a1ac06ca992b1dc86b49616632702313d152f896`,
  `westlake-host.apk=e3b497bb5df1d71a519c61a6ef177afb25f7198009353bf975a2c4d92a85a3eb`,
  `westlake-mcd-profile-debug.apk=50477eccecc86fa5ecd8144d26b3930ec60d68c3b952708d66aba934ea448933`.
  It is the current OHOS controlled mock profile target, not the real
  McDonald's app and not a stock McDonald's APK compatibility claim.
- `PF-467` generic real-APK Activity construction: accepted for the
  McD-profile controlled app through the WAT/AppComponentFactory path; still
  open for arbitrary stock McDonald's activities and for removing remaining
  app-specific launch allowances
- `PF-468` standalone runtime object-array correctness: close the DEX
  object-array/new-array boundary exposed by profile-item `String[]` models.
  PF-466's `resources.arsc` `ArrayStoreException` is closed by keeping parsed
  string pools as `Object[]`, but that does not prove arbitrary app `String[]`
  allocation/assignment correctness
- `PF-469` McD-class generic Material XML and theming: move from the accepted
  McD-profile tag/bind slice to upstream-compatible Material tag inflation,
  ID assignment, themes/styles, Coordinator/AppBar behavior, ripple, and
  animation
- `PF-470` generic visible rendering/input replacement: replace the
  McD-specific direct `DLST` writer and coordinate router with generic View
  draw, hit testing, scrolling, adapter/list rendering, and invalidation
- `PF-471` production-grade portable networking/images: replace synthetic REST
  matrix coverage and one capped image proof with real multi-method execution,
  large-body streaming, redirects, timeout/error parity, direct libcore
  networking parity, and multi-image transport
- `PF-472` OHOS McD-profile adapter parity: implement the same guest-facing
  surface/input/storage/network contracts in an OHOS Ability/XComponent host
  and rerun PF-466 there
- `PF-473` standalone libcore charset/encoding correctness: fix
  `Charset.forName`, `String.getBytes("UTF-8")`, and default `PrintStream`
  encoding in the Westlake guest runtime. PF-466 now accepts standard app
  `String.getBytes("UTF-8")` and the runner rejects both `NPE-SYNC` and the
  charset alias `String[]` `ArrayStoreException`; startup stdio remains on the
  ASCII-safe wrapper and broader charset/provider/default-encoding coverage is
  still required for stock APKs.
- `PF-474` post-checkout direct-frame renderer/runtime stress: the PF-466
  app-owned checkout, Deals, and Menu navigation markers are accepted, but an
  earlier repeated-cart/post-checkout direct render hit `SIGBUS BUS_ADRALN`
  with fault address `0xfffffffffffffb17`. The accepted run suppresses
  post-checkout direct-frame emission instead of claiming that generic renderer
  or runtime stress path is fixed.

## 2026-04-25 Roadmap Corrections

The open roadmap should be updated around these issue themes before any
McDonald's-specific work is treated as forward progress:

- `CV-104` / `PF-801` / `A2OH/westlake#572`: harden canary evidence.
  Only markers written by canary app code count as `L0`/`L1` success.
  Launcher-authored `CANARY_*_OK` lines are diagnostics only.
- `CV-102`: `L0`/`L1` are accepted on the phone in both
  `control_android_backend` and `target_ohos_backend` with app-owned marker
  files.
- `CV-103` / `CV-201`: `L2` widgets/resources pass in both backend modes, and
  `L3` AppCompat/Fragment now passes in `target_ohos_backend` with app-owned
  fragment lifecycle markers. The accepted `target L3` runtime hashes are
  `dalvikvm=d020846653627d90429fbd88b8fc4b8029634389422c1fab1cfdf8a8c314b120`
  and
  `aosp-shim.dex=80557721467673a09cff28462707f8880afcb601ae885b3903c0b58b6212b65c`.
  `L1` remains a plain Activity lifecycle plus programmatic `View` gate, `L2`
  covers widget/theme/XML resources, and `L3` covers the smallest
  AppCompat/Fragment commit/view/resume slice.
- `CV-103` update: `target L3LOOKUP` now passes on the phone with app-owned
  `CANARY_L3_FRAGMENT_LOOKUP_OK`. The accepted proof pair is
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
  This proves `FragmentManager.findFragmentById(...)`, `getFragments()`, and
  `findFragmentByTag(...)` across the app-dex to shim-dex boundary.
- `CV-108` update: `target L3IFACE` now passes on the phone with app-owned
  `CANARY_L3_FRAGMENT_INTERFACE_GET_OK` under the same proof pair. This proves
  app-dex `java.util.List.get(I)` dispatch on the
  `FragmentManager.getFragments()` result after `commitNow()`.
- `CV-103` update: `target L4` now passes on the phone with app-owned
  `CANARY_L4_SAVEDSTATE_PROVIDER_OK`, `CANARY_L4_VIEWMODEL_OK`,
  `CANARY_L4_FRAGMENT_SAVEDSTATE_OK`, `CANARY_L4_FRAGMENT_VIEWMODEL_OK`,
  `CANARY_L4_FRAGMENT_ON_RESUME`, and `CANARY_L4_OK`. The current accepted
  proof pair for this base `L4` regression is
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
  `target L3LOOKUP` also passes as a regression gate under the same pair.
- `CV-103` update: `target L4STATE` now passes on the phone with app-owned
  `CANARY_L4STATE_REGISTRY_RESTORE_OK`,
  `CANARY_L4STATE_SAVEDSTATE_HANDLE_OK`,
  `CANARY_L4STATE_CREATION_EXTRAS_OK`,
  `CANARY_L4STATE_VIEWTREE_LIFECYCLE_OWNER_OK`,
  `CANARY_L4STATE_VIEWTREE_VIEWMODEL_OWNER_OK`,
  `CANARY_L4STATE_VIEWTREE_SAVEDSTATE_OWNER_OK`,
  `CANARY_L4STATE_FRAGMENT_REGISTRY_RESTORE_OK`,
  `CANARY_L4STATE_FRAGMENT_VIEWTREE_OK`, and `CANARY_L4STATE_OK`. The current
  accepted proof pair is
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
  `target L4`, `target L3LOOKUP`, and `target L3IFACE` also pass as regression
  gates under this pair.
- `CV-103` update: `target L4RECREATE` now passes on the phone with app-owned
  `CANARY_L4RECREATE_SAVE_STATE_OK`, `CANARY_L4RECREATE_ON_PAUSE`,
  `CANARY_L4RECREATE_ON_STOP`, `CANARY_L4RECREATE_ON_DESTROY`,
  `CANARY_L4RECREATE_NEW_INSTANCE_OK`,
  `CANARY_L4RECREATE_ON_CREATE_RESTORED_OK`,
  `CANARY_L4RECREATE_REGISTRY_RESTORED_OK`,
  `CANARY_L4RECREATE_ON_RESUME_RESTORED_OK`, and `CANARY_L4RECREATE_OK`. The
  current accepted proof pair is
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
  `target L4STATE`, `target L4`, `target L3LOOKUP`, and `target L3IFACE` also
  pass as regression gates under this pair.
- `CV-103` update: `target L4WATRECREATE` now passes on the phone with the same
  app-owned recreate markers plus WAT-owned trace markers
  `CV canary WAT launch begin`, `CV WAT precreate savedstate returned`,
  `CV WAT recreate begin`, and `CV WAT recreate end`. It proves a
  `WestlakeActivityThread`-owned precreate saved-state,
  save/pause/stop/destroy/relaunch/restore/resume sequence for the cutoff
  canary with a fresh Activity instance and restored `SavedStateRegistry` state.
- `CV-103` / `CV-106` update: `target L4WATFACTORY` now passes on the phone
  with WAT-owned factory trace markers `CV canary WAT factory manifest parsed`
  and `CV canary WAT factory set done`, plus app-owned factory markers
  `CANARY_L4FACTORY_CTOR_OK`, two
  `CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`, and two
  `CANARY_L4FACTORY_ACTIVITY_RETURNED_OK`. It proves controlled canary manifest
  `android:appComponentFactory` discovery, `WestlakeActivityThread` factory
  installation, custom factory construction, and custom
  `instantiateActivity(...)` on initial launch and recreate.
- `CV-103` / `CV-106` update: `target L4WATAPPFACTORY` now passes on the phone
  with WAT-owned application factory trace markers
  `CV canary application manual skipped app factory`,
  `CV WAT app factory preactivity makeApplication begin`,
  `CV WAT instantiateApplication returned com.westlake.cutoffcanary.CanaryApp`,
  `CV WAT application onCreate returned`, and
  `CV WAT app factory preactivity makeApplication returned`, plus app-owned markers
  `CANARY_L4APPFACTORY_INSTANTIATE_APPLICATION_OK`,
  `CANARY_L4APPFACTORY_DIRECT_CANARY_APP_OK`, and
  `CANARY_L4APPFACTORY_APPLICATION_RETURNED_OK` before the first
  `CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`. It proves WAT-owned pre-Activity
  `makeApplication()` and controlled custom
  `AppComponentFactory.instantiateApplication(...)` ordering for the canary
  Application.
- `CV-103` / `CV-106` update: `target L4WATAPPREFLECT` now passes on the phone
  with the same WAT-owned Application-before-Activity ordering while the canary
  factory delegates to the base
  `AppComponentFactory.instantiateApplication(...)` path. The accepted trace
  includes `CV WAT instantiateApplication reflect begin
  com.westlake.cutoffcanary.CanaryAppComponentFactory`,
  `PF301 strict factory application ctor call`,
  `PF301 strict factory application ctor returned`, and
  `CV WAT instantiateApplication reflect returned`; app markers include
  `CANARY_L4APPREFLECT_STAGE_OK`, `CANARY_L4APPREFLECT_SUPER_CALL`,
  `CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK`,
  `CANARY_L4APPREFLECT_SUPER_RETURNED`, and
  `CANARY_L4APPREFLECT_APPLICATION_RETURNED_OK` before Activity factory
  instantiation. The acceptance gate rejects the direct canary constructor marker
  for this stage.
  `target L4WATAPPFACTORY`, `target L4WATFACTORY`, `target L4WATRECREATE`,
  `target L4RECREATE`, `target L4STATE`, `target L4`, `target L3LOOKUP`, and
  `target L3IFACE` also pass as regression gates under the same proof pair:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
  and
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`;
  the canary APK hash is
  `cutoff-canary-debug.apk=cb167f3033c14ea3c2eecb40cff784319d5a5657d85f060c0b15905b8e1c4147`.
  The runner now verifies that installed APK hash before accepting a canary run,
  auto-installs the local APK when stale, and fails closed if the post-install
  hash still differs.
  Hash-stamped evidence bundle:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.
- `CV-103` caveat replacement: the lookup proof is green but not yet a clean
  production runtime fix. The last accepted blocker was an unresolved
  `BitVector::ClearAllBits()` helper call during ART
  `AssignVTableIndexes()` while linking `FragmentManager`; the accepted
  runtime fixes that by moving the implementation into `bit_vector.h` with
  explicit word stores. `scripts/check-westlake-runtime-symbols.sh` is now in
  the phone sync path to block unexpected strong unresolved runtime helpers.
  Live `PFCUT`/`DEBUG: Thread` probe output is now removed/quarantined from the
  accepted `L4WATAPPREFLECT`, `L4WATAPPFACTORY`, `L4WATFACTORY`,
  `L4WATRECREATE`, `L4RECREATE`, `L4STATE`, `L4`, `L3LOOKUP`, and `L3IFACE`
  artifacts. The
  `FragmentManager.mAdded` concrete
  `ArrayList` typing workaround is removed and the same gates remain green.
  Open runtime debt is now broader generic interface-dispatch coverage, not the
  old fragment lookup acceptance gap, app-facing `List.get(I)`, saved-state
  handle/extras/view-tree owner basics, or probe-log cleanup. Recreate is now
  proven both on the cutoff-scoped `MiniActivityManager` path and on a WAT-owned
  canary path.
  `L4WATRECREATE` no longer skips precreate named saved-state handling; it
  initializes that boundary through the public `SavedStateRegistryOwner`
  contract. WAT attach now routes through shim-owned `Activity.attach(...)`
  instead of direct field assignment in `WestlakeActivityThread`. Strict WAT
  Activity creation now enters and returns through a default
  `AppComponentFactory.instantiateActivity(...)` path on launch and relaunch,
  and `L4WATFACTORY` proves controlled custom/manifest
  `instantiateActivity(...)` selection. `L4WATAPPFACTORY` proves controlled
  custom `instantiateApplication(...)` selection for the canary Application
  before Activity factory instantiation, and `L4WATAPPREFLECT` proves the base
  reflective no-arg Application constructor path for the controlled canary. The
  previous canary-specific
  `DataSourceHelper` skip is reduced to a McDonald's package/class guard.
  Hilt-safe real APK factory selection, Hilt-generated real APK Application
  construction, generic real-APK Application-before-Activity ordering, and
  real-APK lifecycle convergence remain open.
- `PF-001`: quarantine host escape routes. Real Android `DexClassLoader`,
  `startActivity`, and `HostBridge` paths must not satisfy target milestones.
- `PF-101` / `PF-201` through `PF-204`: track ART boot debt separately from
  app progress. Broad `ClearException`, forced class initialization, and
  MethodHandles/VMStack bypasses are not production runtime behavior. The next
  concrete runtime patch should cover the A11 `sun.misc.Unsafe` path, not only
  A15 `jdk.internal.misc.Unsafe`, and should remove reliance on fragile Java
  wrappers during early `ConcurrentHashMap`/`ObjectStreamField` initialization.
- `PF-301`: converge on one generic lifecycle path. `WestlakeActivityThread`
  now owns the accepted `L4WATRECREATE` canary proof with precreate saved-state
  execution and shim-owned `Activity.attach(...)`; `L4WATFACTORY` adds accepted
  controlled custom/manifest `AppComponentFactory.instantiateActivity(...)`
  construction; `L4WATAPPFACTORY` adds controlled custom
  `AppComponentFactory.instantiateApplication(...)` construction before Activity
  factory instantiation; and `L4WATAPPREFLECT` adds the base reflective no-arg
  Application constructor path for the controlled canary. The current accepted
  path still lacks Hilt-safe real-APK factory semantics, Hilt-generated real APK
  Application construction, generic real-APK Application-before-Activity
  ordering, and generic real-APK lifecycle architecture.
- `PF-302` / `CV-201`: make the backend boundary explicit for resources,
  package manager, Binder/services, window/surface, input, storage, and
  permissions before claiming OHOS portability. PF-451 now proves a visible
  controlled app through a direct `DLST` display-list path, but generic
  `Activity.renderFrame`/View-tree rendering through `OHBridge.surfaceCreate`
  remains open because `surfaceCreate` is not registered in the subprocess path
  and faults when invoked. PF-454 adds accepted phone proof for a first
  Material Components namespace slice, Chinese UTF-8 text, host-bridge network
  image tiles, and direct Material-styled DLST rendering, but it is still a
  controlled component shim plus app-specific hit routing, not a generic
  upstream MDC renderer or generic Android View hit-test claim.
- `PF-455` / `PF-456` / `PF-457` / `PF-459` / `PF-460`: the next frontier is
  now narrower and more specific. PF-455 has phone evidence for XML-backed Yelp
  layout inflation and binding, PF-459 proves a generic inflated-View DLST
  serialization slice, and PF-460 proves a first inflated XML
  `Button.performClick()` listener path, but visible polished rendering still
  uses the controlled direct `DLST` path and most touch/scroll behavior still
  uses the controlled router.
  PF-456 now has Android phone evidence for the bridge v2 REST matrix, but
  still needs the OHOS adapter. PF-457 has a Material XML probe with generic
  `findViewAt/performClick` into the APK listener, but not upstream MDC
  compatibility, Material theming/behaviors, or generic Android rendering.
- `CV-105` / `A2OH/westlake#573`: self-contained runtime packaging. The current
  phone run still stages binaries/libs through the Android host and relies on
  Android APEX paths.
- `CV-106` / `A2OH/westlake#574`: lifecycle convergence. WAT now owns an
  accepted canary recreate proof with precreate saved-state execution,
  shim-owned attach, default factory construction, and controlled custom
  manifest `instantiateActivity(...)` plus controlled custom
  `instantiateApplication(...)` plus base reflective canary Application
  construction. Hilt-safe real APK factory selection, Hilt-generated real APK
  Application construction, generic real-APK Application-before-Activity
  ordering, and generic real-APK lifecycle coverage remain open before it can
  satisfy real APK/McDonald's lifecycle delivery.
- `CV-107`: A11 `sun.misc.Unsafe` / A15 ART bootstrap debt. The immediate
  reflection-heavy `sun.misc.Unsafe.objectFieldOffset(Field)` crash is repaired
  enough for `L3LOOKUP`, but keep the broader Unsafe/CHM and stale JNI
  trampoline cleanup open until the diagnostic runtime patches are removed.
- `CV-108`: generic virtual/interface dispatch and link-helper debt.
  `L3LOOKUP` exposed an unresolved class-link helper sentinel in
  `BitVector::ClearAllBits()` and prior interface dispatch fragility in shim
  collection calls. `L3IFACE` now proves app-facing `List.get(I)` dispatch on a
  shim-returned list, and `FragmentManager.mAdded` is no longer concretely typed
  as `ArrayList`. Keep `L3LOOKUP` and `L3IFACE` as regression gates, audit
  remaining unresolved runtime helper calls, and broaden IMT/interface dispatch
  coverage before real app dashboard claims.

The detailed local tracker remains in:
- `/home/dspfac/openharmony/WESTLAKE-PLATFORM-FIRST-OPEN-ISSUES.md`

## 2026-04-26 Same-Day Delivery Pivot

The active delivery target is now a controlled local Android showcase app, not a
stock APK and not McDonald's. The purpose is to validate Westlake functionality
with an app whose API surface we own before expanding to Hilt/GMS/stock APK
complexity.

### PF-451
- Priority: P0
- Layer: controlled app validation
- Depends On: PF-301, PF-401, PF-801
- Status: active same-day delivery target
  - 2026-04-26 update: accepted same-day phone proof for the controlled
    Noice-style local showcase app with a Yelp-like Discover venue panel
- Problem:
  - canary stages prove isolated Android semantics, but the contract needs a
    visible, coherent Android app running through Westlake on the phone
- Scope:
  - local-only Android APK
  - no GMS, Firebase, Maps, push, sign-in, payments, camera/media/location, or
    required backend services
  - AppCompat/Fragment/XML/resource/ViewModel/SavedState-style surface already
    exercised by the accepted canaries
- App complexity target:
  - multi-screen local app with a real-looking UI
  - home/dashboard, list/grid, detail, form/settings, dialog/bottom sheet style
    surface, local state, scroll, click, add/remove/edit flow, recreate/restore
  - optional lightweight animation/transition if it stays inside known View
    rendering
- Test:
  - build APK from source
  - run through Westlake `dalvikvm` on phone
  - capture screenshot, logcat, pid/process state, runtime hashes, and app-owned
    success markers or equivalent app-side event log
  - demonstrate at least one interaction after launch
- Done When:
  - the connected phone visibly shows the showcase UI
  - app code executes on the Westlake guest plane
  - the showcase can be rerun from a declared build/run command without relying
    on stock phone ART for app logic
  - the remaining OHOS gap is explicit: backend adapter/packaging, not unknown
    Android app API surface

Accepted PF-451 evidence from `cfb7c9e3`:

- Build/run: `./test-apps/05-controlled-showcase/build-apk.sh`,
  `WAIT_SECS=3 ./scripts/run-controlled-showcase.sh`
- App: `com.westlake.showcase`, a self-contained Noice-style local ambient
  mixer with XML app bar, playback controls, preset buttons, library rows,
  SVG/image aliases, sliders, progress, checkboxes, timer controls, chips,
  text input, ImageView, HorizontalScrollView, bottom-navigation-like controls,
  and a Yelp-like venue card with rating/reviews/actions
- Artifacts:
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.log`,
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.markers`,
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.trace`, and
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.png`
- Visual gate:
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.visual`
- Hashes:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`,
  `aosp-shim.dex=b498750dce8e022c3e0a30c402ef652ec396d8b04cc2dc66e295ec6ddfbe3854`,
  `westlake-showcase-debug.apk=bcd8d63eb2af3d2342110a5df97afd581cc3154d96d96c3de34306597ba5064d`
- Screenshot acceptance: `controlled_showcase_target.png` was inspected and
  shows the controlled Noice-style phone UI after real navigation to Settings,
  including playback state, a Yelp-like Discover card for `Rain Room`, 4.5
  stars, 85 reviews, host/OHBridge network status, active bottom navigation,
  offline/export controls, runtime evidence, and saved guest state.
- Required app-side markers passed:
  `SHOWCASE_APP_ON_CREATE_OK`, `SHOWCASE_ACTIVITY_ON_CREATE_OK`,
  `SHOWCASE_XML_INFLATE_OK`, `SHOWCASE_XML_BIND_OK`,
  `SHOWCASE_XML_LAYOUT_PROBE_OK`, `SHOWCASE_XML_API_SURFACE_OK`,
  `SHOWCASE_UI_BUILD_OK`, `SHOWCASE_ON_START_OK`,
  `SHOWCASE_ON_RESUME_OK`, `SHOWCASE_XML_TREE_RENDER_OK`,
  `SHOWCASE_DIRECT_FRAME_OK`, `SHOWCASE_TOUCH_POLL_OK`,
  `SHOWCASE_NAV_MIXER_OK`, `SHOWCASE_NAV_TIMER_OK`,
  `SHOWCASE_NAV_SETTINGS_OK`, `SHOWCASE_ADD_LAYER_OK`,
  `SHOWCASE_SAVE_MIX_OK`, `SHOWCASE_TIMER_SET_OK`,
  `SHOWCASE_NETWORK_HOST_BRIDGE_OK`, `SHOWCASE_NETWORK_JSON_OK`,
  `SHOWCASE_NETWORK_IMAGE_OK`, `SHOWCASE_YELP_CARD_OK`,
  `SHOWCASE_VENUE_NEXT_OK`, `SHOWCASE_VENUE_REVIEW_OK`, and
  `SHOWCASE_EXPORT_BUNDLE_OK`
- Remaining PF-302 gap: the accepted render path is a controlled direct `DLST`
  pipe writer for PF-451. Generic View-tree rendering through
  `OHBridge.surfaceCreate` is still open and must be fixed before claiming a
  general self-contained Android runtime for arbitrary APK UI.
- Remaining PF-451 implementation gaps exposed by the pass:
  `resources.arsc` parsing fails with `ArrayStoreException`, arbitrary widget
  mutation needs a broader runtime proof, strict subprocess input uses
  showcase-specific hit routing rather than generic Android View hit testing,
  the XML tree renderer currently paints the visible simplified tree rather
  than a full Android widget draw pass, and live Java/libcore networking is not
  accepted. The controlled venue panel now records
  `SHOWCASE_NETWORK_HOST_BRIDGE_OK`, fetches `417` bytes of venue JSON and
  `8090` bytes of image data through `transport=host_bridge`, and rejects
  `SHOWCASE_NETWORK_NATIVE_GAP_OK` in the bridge-required run. Full
  Java/libcore networking remains open for broader compatibility; missing
  runtime pieces include `java.net.URL`, `libcore.io.Linux.android_getaddrinfo`,
  and related socket/unsafe initialization.

### PF-452
- Priority: P0
- Layer: guest networking/runtime bridge
- Depends On: PF-451
  - Status: Android phone proof accepted after Yelp-like polish pass; OHOS
    adapter open
- Problem:
  - The Yelp-like controlled showcase can render REST-shaped venue data and
    image metadata through the selected host/OHBridge bridge on Android phone.
    It still cannot claim full Java/libcore network compatibility; attempts
    through `java.net.URL`/`HttpURLConnection` and lower-level socket paths
    exposed missing runtime/native pieces and unsafe initialization failures.
- Decision:
  - implement a constrained host/OHBridge HTTP bridge first. This is the
    portable path for Android phone validation and the OHOS port because the
    guest app-facing contract stays stable while each host supplies its own
    network adapter.
  - Android phone host implementation is accepted with
    `SHOWCASE_NETWORK_HOST_BRIDGE_OK`. The remaining PF-452 delivery item is the
    OHOS host adapter with the same contract.
  - do not block the controlled OHOS app proof on full Java/libcore socket/DNS
    completion. Keep that as a later compatibility track for broader stock APK
    support.
- Scope:
  - route constrained HTTP GET through the Westlake host/OHBridge boundary
    using the same request/response semantics on Android phone and OHOS
  - preserve the same app-facing API contract across Android phone and OHOS
    ports
  - keep the PF-451 fixture fallback as a negative-control marker until live
    network is accepted
- Test:
  - accepted on Android phone: run `com.westlake.showcase` with the
    host/OHBridge HTTP bridge enabled
  - fetch the venue JSON and preview image through the bridge from guest app
    logic
  - require `SHOWCASE_NETWORK_HOST_BRIDGE_OK`, `SHOWCASE_NETWORK_JSON_OK`, and
    `SHOWCASE_NETWORK_IMAGE_OK` with no `SHOWCASE_NETWORK_NATIVE_GAP_OK` in the
    accepted live-network run
  - repeat the same marker contract on OHOS after the host adapter exists
- Done When:
  - Android phone: done with
    `aosp-shim.dex=b498750dce8e022c3e0a30c402ef652ec396d8b04cc2dc66e295ec6ddfbe3854`
    and
    `westlake-showcase-debug.apk=bcd8d63eb2af3d2342110a5df97afd581cc3154d96d96c3de34306597ba5064d`
  - OHOS: the showcase visibly renders venue data fetched through the same
    portable bridge contract
  - marker evidence proves the live transport path on each host

### PF-453
- Priority: P0
- Layer: controlled app validation / live data proof
- Depends On: PF-301, PF-452, PF-801
- Status: Android phone proof accepted; OHOS adapter open
- Problem:
  - The controlled Noice showcase includes a Yelp-like panel, but the contract
    also needs a separate app proof that is live-data-first and not coupled to
    the showcase implementation.
- Scope:
  - separate Android APK with package `com.westlake.yelplive`
  - live REST-shaped feed and remote image bytes fetched through the same
    portable host/OHBridge HTTP bridge used by PF-452
  - touch-driven app flows for list scroll, row-level list selection, details,
    saved state, and search
  - direct `DLST` phone rendering with screenshot and visual gate, including a
    required high-entropy photo region so block-only rendering fails
  - polished Yelp-like surface: Material-style red app/search header,
    filter/category chips, elevated five-row restaurant list with remote
    thumbnails, white bottom navigation, details, and saved state
- Test:
  - build APK from `test-apps/06-yelp-live/`
  - run `scripts/run-yelp-live.sh` on phone `cfb7c9e3`
  - require WAT lifecycle markers, host-bridge network markers, live JSON/image
    markers, touch markers, and app action markers
  - reject network failure markers and fatal runtime log markers
- Done When:
  - Android phone: done with
    `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`
    and
    `westlake-yelp-live-debug.apk=a677a8f36e498a8f7c6834a9dc4d10bdc5fa03d7a48c91c8bdc00c8138b6866b`
  - the accepted host log includes `Surface buffer 1080x2280 for
    com.westlake.yelplive`, proving the 1K-class Yelp buffer path
  - the accepted marker file includes `YELP_XML_RESOURCE_WIRE_OK`,
    `YELP_XML_INFLATE_OK`, `YELP_XML_BIND_OK`,
    `YELP_XML_LAYOUT_PROBE_OK`, `YELP_LIVE_JSON_OK`,
    `YELP_LIVE_IMAGE_OK`, `YELP_LIVE_ROW_IMAGE_OK index=4`,
    `YELP_LIST_SCROLL_OK`, `YELP_NEXT_PLACE_OK`, `YELP_DETAILS_OPEN_OK`,
    `YELP_SAVE_PLACE_OK`, `YELP_NAV_SAVED_OK`, and `YELP_NAV_SEARCH_OK`
  - OHOS: the same app renders live data through the OHOS host adapter for the
    PF-452 bridge contract

### PF-454
- Priority: P0
- Layer: Material Components API canary / controlled Material-style UI proof
- Depends On: PF-301, PF-302, PF-452, PF-453, PF-801
- Status: Android phone proof accepted; full upstream Material compatibility
  remains open under PF-457
- Problem:
  - The user-visible Yelp-like proof needed to move beyond primitive rectangles
    and exercise a Material-like API surface, but upstream Google Material
    Components compatibility is too large to claim from one canary.
- Scope:
  - separate Android APK with package `com.westlake.materialyelp`
  - app-owned `Application` and `Activity` lifecycle markers
  - controlled Westlake-owned `com.google.android.material.*` shim slice:
    `MaterialCardView`, `MaterialButton`, `ChipGroup`, `Chip`,
    `TextInputLayout`, `TextInputEditText`, `Slider`, `BottomNavigationView`,
    and `FloatingActionButton`
  - Chinese UTF-8 text, host-bridge image tiles, aspect-preserved host display,
    direct `DLST` rendering, and strict phone touch actions
- Test:
  - build APK from `test-apps/07-material-yelp/`
  - run `scripts/run-material-yelp.sh` on phone `cfb7c9e3`
  - require Material class-surface, lifecycle, language, network image, render,
    touch, filter, selection, save, saved-nav, and search markers
  - reject screenshot gates that show only blocks, missing network image tiles,
    vertically stretched text, or no accepted touch actions
- Done When:
  - Android phone: done with
    `aosp-shim.dex=20fc0c98f9a9371f12deae0d347a01a033e41629a5797aee1cf70d5c39245726`
    and
    `westlake-material-yelp-debug.apk=e586d7afd7df1a2a8c418fb18de952c032dd44c456a3bbf952799c363711ba66`
  - the accepted host log includes `Surface buffer 1080x1800 for
    com.westlake.materialyelp`, proving the 1K-class Material Yelp buffer path
  - the accepted marker file includes `MATERIAL_APP_ON_CREATE_OK`,
    `MATERIAL_ACTIVITY_ON_CREATE_OK`, `MATERIAL_CLASS_SURFACE_OK`,
    `MATERIAL_LANGUAGE_OK`, `MATERIAL_NETWORK_BRIDGE_OK`,
    `MATERIAL_IMAGE_BRIDGE_OK`, `MATERIAL_DIRECT_FRAME_OK`,
    `MATERIAL_TOUCH_POLL_OK`, `MATERIAL_FILTER_TOGGLE_OK`,
    `MATERIAL_SELECT_PLACE_OK`, `MATERIAL_SAVE_PLACE_OK`,
    `MATERIAL_NAV_SAVED_OK`, and `MATERIAL_NAV_SEARCH_OK`
  - full upstream MDC AAR, XML Material rendering, theming, Coordinator/AppBar,
    ripple/animation, and generic hit testing are not claimed here; they stay in
    PF-457

### PF-455
- Priority: P0
- Layer: XML-backed Yelp app path
- Depends On: PF-302, PF-453, PF-454, PF-801
- Status: Android phone XML inflation/binding slice accepted on 2026-04-27;
  PF-459 first generic draw slice is accepted; PF-460 generic XML
  `Button.performClick()` and `ScrollView` discovery slices are accepted;
  PF-461 `ListView`/`BaseAdapter` row binding, image rebinding, and generic
  adapter click are accepted; full-fidelity generic View-tree rendering and
  broad touch/scroll dispatch remain open
- Problem:
  - The accepted Yelp path now proves compiled XML inflation to a real
    `ScrollView`, a first generic draw serialization slice, multiple inflated
    XML button listener hits, generic scroll-container discovery, and a real
    XML `ListView` backed by a guest `BaseAdapter`, but the polished visible UI
    still depends on an app-specific direct `DLST` writer and most interactions
    still depend on the controlled touch router. That validates guest logic,
    networking, direct drawing, narrow View-tree listener/scroll paths, and a
    first adapter path, but full-fidelity generic Android View drawing and broad
    touch dispatch remain open.
- Scope:
  - create or refactor the Yelp-like app so its primary screen is declared in
    compiled XML layout resources
  - load those resources through the Westlake APK/resource path and inflate them
    in guest code via `Activity.setContentView(...)`, `LayoutInflater`, or the
    closest supported Android-compatible API
  - bind real resource IDs for app bar/search, filters/chips, list rows,
    restaurant cards, image views, save/details buttons, and bottom navigation
  - keep the same live-data, image, scroll, details, save, saved, and search
    flows working after the UI comes from XML
  - produce a render path from the inflated tree; any app-specific direct writer
    must be clearly marked as transitional and cannot bypass the XML acceptance
    markers
  - render the Yelp-like proof into a full-phone 1K-class buffer, currently
    `1080x2280` for the 480x1013 logical layout, so screenshot
    acceptance is not limited by the old low-resolution surface buffer
- Test:
  - build the XML-backed Yelp APK from source and run it on phone `cfb7c9e3`
  - require app-owned markers equivalent to `YELP_XML_INFLATE_OK`,
    `YELP_XML_BIND_OK`, `YELP_XML_LAYOUT_PROBE_OK`,
    `YELP_XML_TREE_RENDER_OK`, `YELP_GENERIC_HIT_OK`,
    `YELP_XML_TOUCH_ROUTE_OK`, and the existing live-data/action markers
  - screenshot gate must show a scrollable multi-row Yelp-like list with remote
    images, search/filter surface, save affordance, details path, and bottom nav
    from the 1K-class buffer
  - reject programmatic-only UI construction as the acceptance path
- Done When:
  - Accepted slice: `scripts/run-yelp-live.sh` on `cfb7c9e3` passes with
    `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`
    and
    `westlake-yelp-live-debug.apk=a677a8f36e498a8f7c6834a9dc4d10bdc5fa03d7a48c91c8bdc00c8138b6866b`.
  - Accepted markers prove `YELP_XML_RESOURCE_WIRE_OK`,
    `YELP_XML_INFLATE_OK root=android.widget.ScrollView views=30 texts=21`,
    `YELP_XML_BIND_OK buttons=5`,
    `YELP_XML_LAYOUT_PROBE_OK target=480x1013 measured=480x1013`,
    `YELP_GENERIC_VIEW_DRAW_OK views=27 texts=17 buttons=13 images=0
    lists=1 listRows=5 listImages=5 height=1013`,
    `YELP_GENERIC_LIST_DRAW_OK rows=5 images=5`,
    `YELP_GENERIC_VISIBLE_LIST_OK rows=5 images=5`,
    `YELP_GENERIC_HIT_OK` with `clicked=true`,
    `target=android.widget.Button`, `text=Search`, `text=Details`,
    `text=Saved`, and `source=inflated_xml`,
    `YELP_GENERIC_SCROLL_OK container=android.widget.ScrollView`,
    `YELP_ADAPTER_ATTACH_OK class=android.widget.ListView`,
    `YELP_ADAPTER_NOTIFY_OK images=5`,
    `YELP_ADAPTER_IMAGE_BIND_OK position=4 bitmap=false imageView=true`,
    `YELP_GENERIC_ADAPTER_ITEM_CLICK_OK position=2`,
    `YELP_ADAPTER_ITEM_CLICK_OK position=2`,
    `YELP_VISUAL_DELTA_V4_OK surface=adapter_feed adapterBadge=true
    visibleImages=5`,
    `YELP_FULL_RES_FRAME_OK logical=480x1013 target=1080x2280 navTop=824`,
    live REST/image traffic,
    list scroll, details, save, saved navigation, and search.
  - Visual gate records `adapter_teal_samples=697`, proving the phone
    screenshot contains the visible XML ListView/BaseAdapter adapter-feed
    ribbon.
  - Remaining open closure: the visible polished phone frame still uses the
    controlled direct `DLST` renderer; full-fidelity generic Android View-tree
    rendering over the inflated Yelp widgets, raw Bitmap/ImageView decode,
    robust generic list scrolling, and broad generic touch/scroll dispatch are
    not yet accepted.
  - the same app remains a viable OHOS target because the host-facing seams are
    DLST/display, input, storage/logging, and PF-456 networking.

### PF-456
- Priority: P0
- Layer: portable REST networking completeness
- Depends On: PF-452, PF-453, PF-455, PF-801
- Status: bridge v2 live GET path and REST marker contract accepted on Android
  phone; real multi-method matrix and OHOS adapter remain open
- Problem:
  - PF-452/PF-453/PF-454 prove bounded HTTP GET for JSON/images through the
    host/OHBridge bridge. A real controlled app needs a fuller REST surface and
    deterministic error behavior before Westlake can say its app API calls are
    portable to OHOS.
- Scope:
  - expose a stable guest-facing API that covers HTTPS GET/POST/PUT/PATCH/DELETE
    or the subset explicitly required by the controlled app
  - support request headers, query parameters, JSON request/response bodies,
    binary image bodies, status codes, response headers, redirects, timeouts,
    bounded payload sizes, cancellation or cooperative abort, and structured
    network errors
  - keep Android phone and OHOS host adapters behind the same guest contract
  - preserve the distinction between controlled-app REST support and broader
    Java/libcore socket/DNS compatibility
  - add negative tests for DNS failure, HTTP error status, timeout, malformed
    JSON, oversized body, and image decode failure
- Test:
  - run the controlled Yelp XML app through the bridge without local generated
    data or phone ART networking
  - exercise JSON list fetch, image fetch, at least one state-changing request
    shape, response headers/status handling, timeout/failure path, and retry or
    user-visible fallback state
  - require transport markers that identify `host_bridge` or `ohos_bridge`,
    method, status, byte counts, timeout/error class, and payload limits
  - repeat the same contract on OHOS once the OHOS adapter exists
- Done When:
  - Implemented slice: the Android host bridge supports method, headers JSON,
    request body, max-byte cap, timeout, redirect-follow flag, response
    headers, non-2xx response bodies, truncation, and structured errors.
  - Android phone accepted slice: the Yelp run records real live GET JSON/image
    bridge traffic and the REST marker contract:
    `YELP_REST_MATRIX_OK`, `YELP_REST_POST_OK`, `YELP_REST_HEADERS_OK`,
    `YELP_REST_METHODS_OK`, `YELP_REST_HEAD_OK`,
    `YELP_REST_STATUS_OK status=418`, `YELP_REST_REDIRECT_OK`,
    `YELP_REST_TRUNCATE_OK truncated=true`, and `YELP_REST_TIMEOUT_OK`.
  - Current caveat: `YELP_REST_MATRIX_SYNTHETIC_OK` and
    `YELP_REST_TIMEOUT_SYNTHETIC_OK` stand in for a real matrix path that still
    hits a VM SIGBUS; this must be replaced by real request execution before
    PF-456 becomes a broad app-networking claim.
  - Remaining open closure: repeat the same bridge contract on OHOS, with
    POST/PUT/PATCH/DELETE/HEAD/OPTIONS as needed, redirects, timeouts,
    truncation, non-2xx bodies, headers, and JSON/binary bodies.
  - the controlled app's REST/API demands are satisfied by the portable Westlake
    bridge on Android phone and OHOS
  - accepted runs prove live JSON and images, non-GET request shape where the app
    needs it, deterministic failures, and no stock phone ART networking escape
  - Java/libcore `HttpURLConnection`/raw socket work is either adapted to this
    bridge or tracked separately as broader stock-APK compatibility

### PF-457
- Priority: P0/P1
- Layer: Material and generic UI compatibility expansion
- Depends On: PF-302, PF-454, PF-455, PF-801
- Status: PF-454 first shim slice and PF-457 Material XML/hit probe accepted;
  broad compatibility not yet supported
- Problem:
  - The current Material Yelp canary proves controlled class instantiation and a
    direct Material-styled frame. It does not prove arbitrary upstream Google
    Material Components AAR compatibility or generic Android UI behavior.
- Scope:
  - upstream Google Material Components AAR compatibility for the components
    needed by the controlled Yelp app
  - Material themes and style/resource resolution including colors, typography,
    dimensions, state lists, shapes, and default widget attributes
  - generic Material XML inflation/rendering for the same component set
  - `CoordinatorLayout` and `AppBarLayout` scroll/collapse behavior needed by a
    polished list/search app
  - ripple/state-list feedback, basic animation/interpolator/timing behavior,
    and invalidation scheduling where needed for visible interactions
  - generic Android View hit testing, event dispatch, pressed/selected/focused
    states, nested scroll basics, and list/card touch routing
- Test:
  - build a Material XML canary or the PF-455 XML-backed Yelp app using Material
    XML tags and theme attributes
  - run on phone with screenshot, touch, render, resource, style, and marker
    gates
  - verify clicks, ripples or pressed-state fallback, scroll/app-bar behavior,
    selected filters/chips, card save/details actions, and no component-specific
    manual hit map for the accepted path
  - keep PF-454 as a regression gate so the first controlled shim slice does not
    regress while generic support expands
- Done When:
  - Accepted slice: `scripts/run-material-xml-probe.sh` on `cfb7c9e3` passes
    with
    `aosp-shim.dex=bf33aba0a8923e8b7d2cb006ee98042bb217021236a7cfe185a004f0e269716a`
    and
    `westlake-material-xml-probe-debug.apk=ded93614084cdd28a46bcbcbd7eb8cba78504c3c228e0f95835a6ebf42a6e6c9`.
  - Accepted markers prove Material FQN XML tags inflate into shim classes,
    the inflated tree renders via direct DLST, and
    `MATERIAL_GENERIC_HIT_OK` fires through generic
    `findViewAt/performClick` on `MaterialButton`.
  - Remaining open closure: the controlled Yelp app can use Material XML
    components and theming through the generic Westlake path for its primary UI.
  - generic hit testing and invalidation drive broader user-visible interactions
    without an app-specific touch router
  - unsupported upstream Material components are explicitly listed outside the
    accepted controlled-app surface instead of being implied as supported

### PF-460
- Priority: P0
- Layer: generic View hit testing and scroll containers
- Depends On: PF-455, PF-459, PF-801
- Status: first inflated XML `Button.performClick()` slice accepted on Android
  phone, plus actual `ScrollView` inflation/discovery; broad coordinate hit
  dispatch and full visible scroll routing remain open
- Problem:
  - The current Yelp run proves Westlake can invoke app-owned listeners on
    inflated XML buttons and can find a real `ScrollView`, but most visible
    interactions still use `routeYelpLiveDirectTouch(...)`. Stock app progress
    requires generic View hit testing, event dispatch,
    pressed/selected state, and scroll-container routing without per-app
    coordinate maps.
- Scope:
  - route logical touch coordinates through the inflated View tree
  - dispatch click/touch events through `View.performClick()` and listener APIs
  - preserve scroll gestures through `ScrollView`/list-like containers
  - keep accepted direct-router markers only as diagnostics while generic
    routing becomes the acceptance path
- Done When:
  - Accepted slice: `scripts/run-yelp-live.sh` on `cfb7c9e3` records
    `YELP_GENERIC_HIT_OK` with `clicked=true`,
    `target=android.widget.Button`, `text=Search`, `text=Details`,
    `text=Saved`, and `source=inflated_xml`, plus `YELP_GENERIC_SCROLL_OK`
    with `container=android.widget.ScrollView`, using
    `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`.
  - Remaining open closure: move category, filter, list row, details, save,
    and bottom-nav interactions from the app-specific direct router to generic
    View dispatch, and make generic scroll routing drive the visible list path.

### PF-461
- Priority: P0
- Layer: adapter/list virtualization and image rebinding
- Depends On: PF-455, PF-456, PF-459, PF-460, PF-801
- Status: first XML `ListView`/`BaseAdapter` row-binding slice accepted on
  Android phone; RecyclerView-class virtualization remains open
- Problem:
  - McDonald's-class apps rely on adapter-backed lists, image rebinding, stable
    row IDs, and row click dispatch. The controlled Yelp app now proves the
    first Android-widget adapter path, but it is still a small `ListView`
    slice, not full RecyclerView, DiffUtil, nested lists, or all image loader
    behavior.
- Scope:
  - keep the Yelp primary layout XML-backed with a real `android.widget.ListView`
  - bind a guest `BaseAdapter` and exercise `getCount`, `getItemId`,
    `getView`, row recycling, and `ImageView.setImageBitmap`
  - prove live image bytes can rebind into rows after async network fetches
  - route at least one generic `ListView.performItemClick()` into the APK's
    row listener without using the direct coordinate router as the proof
- Done When:
  - Accepted slice: `scripts/run-yelp-live.sh` on `cfb7c9e3` passes with
    `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`
    and
    `westlake-yelp-live-debug.apk=a677a8f36e498a8f7c6834a9dc4d10bdc5fa03d7a48c91c8bdc00c8138b6866b`.
  - Accepted markers include `YELP_ADAPTER_ATTACH_OK
    class=android.widget.ListView`, `YELP_ADAPTER_LAYOUT_PROBE_OK`,
    `YELP_ADAPTER_BIND_PROBE_OK rows=5`, `YELP_ADAPTER_GET_VIEW_OK
    position=4`, `YELP_ADAPTER_NOTIFY_OK images=5`,
    `YELP_ADAPTER_IMAGE_REBIND_OK index=4`,
    `YELP_ADAPTER_IMAGE_BIND_OK position=4 bitmap=false imageView=true`,
    `YELP_GENERIC_ADAPTER_ITEM_CLICK_OK position=2`, and
    `YELP_ADAPTER_ITEM_CLICK_OK position=2`.
  - Visible delta markers include `YELP_VISUAL_DELTA_V4_OK
    surface=adapter_feed adapterBadge=true visibleImages=5`, and the screenshot
    visual gate records `adapter_teal_samples=697`.
  - Remaining open closure: add RecyclerView-equivalent virtualization,
    visible generic list scrolling, data-set invalidation without the
    image-rich `notifyDataSetChanged` guard, raw Bitmap/ImageView decode, and
    OHOS adapter parity.
