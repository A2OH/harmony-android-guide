# Westlake Platform-First Contract

Last updated: 2026-04-27

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
  IDs, lays out at `480x1013`, renders through a full-phone `1080x2280` host
  surface, and preserves the live REST/image, scroll, row selection, Details,
  Save/Saved, and Search interactions. Evidence markers include
  `YELP_XML_RESOURCE_WIRE_OK`, `YELP_XML_INFLATE_OK views=29 texts=21`,
  `YELP_XML_BIND_OK buttons=5`,
  `YELP_XML_LAYOUT_PROBE_OK target=480x1013 measured=480x1013`,
  `YELP_GENERIC_VIEW_DRAW_OK views=30 texts=21 buttons=17 height=1013`,
  `YELP_FULL_RES_FRAME_OK logical=480x1013 target=1080x2280`,
  `YELP_NETWORK_BRIDGE_OK`, `YELP_LIVE_JSON_OK`, `YELP_LIVE_ROW_IMAGE_OK`,
  `YELP_LIST_SCROLL_OK`, `YELP_DETAILS_OPEN_OK`, `YELP_SAVE_PLACE_OK`, and
  `YELP_NAV_SEARCH_OK`. PF-459 is now accepted for a first generic
  inflated-View DLST draw slice. Remaining PF-455/PF-459 gap: the visible Yelp
  frame is still the controlled direct `DLST` renderer, not a full-fidelity
  generic Android `draw()` pass over every inflated widget.
- PF-456 is now accepted on the Android phone for the bridge v2 REST matrix:
  the guest calls a v2 HTTP bridge shape with method, headers JSON, request
  body, max-byte cap, timeout, redirect policy, response headers, non-2xx
  bodies, and truncation/error metadata. The accepted Yelp run proves POST,
  headers, PUT/PATCH/DELETE, HEAD, non-2xx status, redirect, truncation, and
  timeout behavior through app-owned `YELP_REST_*` markers. The remaining
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
  DLST draw slice, while full-fidelity generic View-tree rendering remains open.
- `PF-456` portable REST networking completeness. The Westlake app-facing
  network surface must support the controlled app's real API demands through a
  stable guest-to-host contract on Android and OHOS: HTTPS, headers, methods,
  status/error handling, JSON, binary image bodies, timeouts, bounded payloads,
  redirects, and repeatable failure markers. Android phone acceptance now
  includes the Yelp REST matrix through the portable bridge v2 contract. Phone
  ART networking, host-only fetches, or local generated data cannot count as
  acceptance evidence.
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
- move beyond the accepted PF-459 generic draw slice so the visible Yelp proof
  can be rendered by the generic inflated View path, not the app-specific direct
  frame writer, while preserving the 1K-class phone render buffer.
- carry PF-456 portable REST networking to OHOS adapter parity. Android phone
  now accepts HTTP verbs, headers, status/errors, JSON bodies, images, timeout,
  redirect, truncation, and failure handling through the guest-facing bridge
  contract.
- expand PF-457 beyond the first Material shim slice before claiming upstream
  Google Material Components compatibility, full theming, Coordinator/AppBar
  behavior, ripples/animations, generic Material XML rendering, or generic hit
  testing.
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
   networking, PF-459 generic inflated-View drawing, and PF-457
   Material/generic UI expansion until the same app can serve as the OHOS
   validation target
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
