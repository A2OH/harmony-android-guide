# Westlake Engine — Status Report

**Date:** 2026-04-27
**Status:** Platform-first cutoff canary through target `L4WATAPPREFLECT` on phone; PF-451 controlled showcase, PF-452 host/OHBridge network proof, PF-453 separate Yelp live app, PF-454 Material Components canary, PF-455 XML-backed Yelp slice, PF-456 live GET plus REST marker contract, PF-457 Material XML/generic-hit slice, PF-459 generic inflated-View draw slice, PF-460 generic XML hit/scroll probes, and PF-461 adapter/list slice accepted on phone; OHOS adapters, real multi-method REST execution, and generic UI expansion remain open

## Current Supervisor Status (2026-04-27)

PF-451, the same-day controlled local Android showcase target, has an
accepted phone proof from 2026-04-26 on `cfb7c9e3`. The delivered APK is a
self-contained Noice-style local ambient mixer with a Yelp-like Discover venue panel,
`com.westlake.showcase`, built from `test-apps/05-controlled-showcase/` and run
through `scripts/run-controlled-showcase.sh`. The accepted run proves real APK
XML bytes are wired into the standalone guest, `showcase_activity.xml` is
inflated from compiled AXML into the guest View tree, IDs/widgets bind, layout
probes complete, a visible direct frame is rendered from the XML tree, and real
phone touch packets drive guest app navigation/actions including venue load,
next venue, review, and export.

OHOS integration is now tracked in
`docs/engine/OHOS-CONTROLLED-SHOWCASE-INTEGRATION.md`. Supervisor judgement:
the controlled showcase is the right first OHOS port target because its APK,
API surface, resources, inputs, and network behavior are owned by Westlake. It
is not a generic stock Android app compatibility claim. The first OHOS proof
should use an OHOS Ability/XComponent host, DLST replay for the visible frame,
XComponent input callbacks, and the selected portable host/OHBridge HTTP
bridge from PF-452.

PF-452 is now accepted on the Android phone host path. The controlled showcase
fetches venue JSON and image bytes through the portable host/OHBridge bridge and
records `SHOWCASE_NETWORK_HOST_BRIDGE_OK`; the accepted bridge-required run has
no `SHOWCASE_NETWORK_NATIVE_GAP_OK` fallback marker. The remaining network work
for the OHOS goal is to implement the same bridge adapter in the OHOS host.

PF-453 is now accepted on the Android phone host path as a separate live-data
Yelp-like APK, not an added page inside the Noice showcase. The delivered APK is
`com.westlake.yelplive`, built from `test-apps/06-yelp-live/` and run through
`scripts/run-yelp-live.sh`. The accepted run proves WAT-owned Application and
Activity lifecycle for the separate app, compiled XML resource inflation and ID
binding, live HTTPS JSON from `dummyjson.com`, live thumbnail bytes from
`picsum.photos`, guest app logic
using the portable host/OHBridge bridge, direct `DLST` rendering, and real
phone touch packets driving category selection, filters, scroll, Details, Save,
Saved, and Search app actions.
The app is Yelp-like, but it does not call the official Yelp API; it uses
DummyJSON for a public no-key live list and Picsum remote thumbnails as the
portable network/image proof. The accepted screen is now list-first,
Material-styled, and scrollable: elevated search, selected filter/category
chips, card rows, active bottom navigation, and five restaurant rows are
visible after a swipe. Every visible row has a live thumbnail, row body taps
open details, Save/Saved works, and the screenshot gate requires a visible
high-entropy photo region so a block-only card cannot satisfy PF-453.

PF-453 latest evidence:

- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d`
- `westlake-yelp-live-debug.apk=f60f2d8b8b91592aec2e96329da9fbd44f332b535d506e624a5073e37a1122d9`
- Screenshot/log/markers/trace:
  `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.*`
- Stable accepted copy:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/yelp_live/eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d_f60f2d8b8b91592aec2e96329da9fbd44f332b535d506e624a5073e37a1122d9/`
- Visual gate:
  `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.visual`
  (`1080x2280`, `distinct_colors=1383`, `top_red_samples=3033`,
  `bottom_nav_light_samples=5199`, `bottom_nav_red_samples=26`,
  `adapter_teal_samples=697`,
  `photo_distinct_colors=568`, `photo_colored_samples=18740`,
  `photo_natural_samples=85`)
- Host log includes `Surface buffer 1080x2280 for com.westlake.yelplive`.
- Full-resolution marker:
  `YELP_FULL_RES_FRAME_OK logical=480x1013 target=1080x2280 navTop=824`.
- Accepted live markers include `YELP_APP_ON_CREATE_OK`,
  `YELP_ACTIVITY_ON_CREATE_OK`, `YELP_XML_RESOURCE_WIRE_OK`,
  `YELP_XML_INFLATE_OK`, `YELP_XML_BIND_OK`,
  `YELP_XML_LAYOUT_PROBE_OK target=480x1013 measured=480x1013`,
  `YELP_UI_BUILD_OK`, `YELP_GENERIC_VIEW_DRAW_OK`,
  `YELP_GENERIC_HIT_OK`, `YELP_GENERIC_SCROLL_OK`,
  `YELP_ADAPTER_ATTACH_OK`, `YELP_ADAPTER_NOTIFY_OK images=5`,
  `YELP_ADAPTER_IMAGE_BIND_OK position=4`,
  `YELP_GENERIC_ADAPTER_ITEM_CLICK_OK position=2`,
  `YELP_ADAPTER_ITEM_CLICK_OK position=2`,
  `YELP_VISUAL_DELTA_V4_OK surface=adapter_feed`,
  `YELP_NETWORK_BRIDGE_OK`, `YELP_LIVE_JSON_OK status=200 bytes=1605 places=8
  transport=host_bridge`, `YELP_LIVE_IMAGE_OK`,
  `YELP_LIVE_ROW_IMAGE_OK index=4`,
  `YELP_CARD_OK`, `YELP_CATEGORY_SELECT_OK`, `YELP_FILTER_TOGGLE_OK`,
  `YELP_LIST_SCROLL_OK`, `YELP_NEXT_PLACE_OK`,
  `YELP_DETAILS_OPEN_OK`, `YELP_SAVE_PLACE_OK`, `YELP_NAV_SAVED_OK`,
  `YELP_NAV_SEARCH_OK`, `YELP_DIRECT_FRAME_OK`, and
  `YELP_FULL_RES_FRAME_OK`.

PF-455 is accepted for the XML-backed Yelp slice, PF-459 is accepted for a
first generic inflated-View draw slice, PF-460 is accepted for generic XML
button hits plus ScrollView discovery, and PF-461 is accepted for the first
adapter/list slice. The app inflates `yelp_live_activity.xml` from compiled APK
layout bytes, binds the expected guest View IDs plus a real `ListView`, and the
launcher serializes the inflated tree into a DLST frame with
`YELP_GENERIC_VIEW_DRAW_OK views=27 texts=17 buttons=13 images=0 lists=1
listRows=5 listImages=5 height=1013`, plus
`YELP_GENERIC_LIST_DRAW_OK rows=5 images=5` and
`YELP_GENERIC_VISIBLE_LIST_OK rows=5 images=5`. The direct renderer now also
paints an explicit XML
ListView/BaseAdapter adapter-feed ribbon, and the visual gate rejects screenshots
without that teal region. The polished visible Yelp surface remains the
controlled direct `DLST` renderer, so the remaining rendering gap is
full-fidelity generic View drawing replacing the Yelp-specific frame writer.
The accepted generic list proof is still controlled: it overlays Yelp row state
for the visible list proof, and raw Bitmap-backed `ImageView` decode remains a
SIGBUS gap.

PF-456 implementation status: the Android host bridge now exposes a v2 request
shape for method, headers JSON, request body, max-byte cap, timeout,
redirect-follow flag, response headers, non-2xx bodies, truncation, and
structured errors. The latest Yelp run proves real live GET JSON/image traffic
through the bridge and records `YELP_REST_MATRIX_OK` plus POST, headers,
PUT/PATCH/DELETE, HEAD, non-2xx status, redirect, truncation, and timeout
marker-contract coverage. Caveat: the multi-method REST matrix markers are
currently synthetic (`YELP_REST_MATRIX_SYNTHETIC_OK`,
`YELP_REST_TIMEOUT_SYNTHETIC_OK`) because the real matrix path still hits a VM
SIGBUS. Remaining PF-456 closure is real matrix execution and the OHOS adapter
repeating the same guest-facing bridge contract, not more Android phone GET
proof.

PF-457 now has a separate accepted Material XML probe,
`com.westlake.materialxmlprobe`, built from
`test-apps/09-material-xml-probe/` and run with
`scripts/run-material-xml-probe.sh`.

PF-457 evidence:

- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=bf33aba0a8923e8b7d2cb006ee98042bb217021236a7cfe185a004f0e269716a`
- `westlake-material-xml-probe-debug.apk=ded93614084cdd28a46bcbcbd7eb8cba78504c3c228e0f95835a6ebf42a6e6c9`
- Screenshot/log/markers/trace:
  `/mnt/c/Users/dspfa/TempWestlake/material_xml_probe_target.*`
- Accepted markers include `MATERIAL_XML_APP_OK`,
  `MATERIAL_XML_INFLATE_BEGIN`, `MATERIAL_XML_TAG_OK` for
  `TextInputLayout`, `TextInputEditText`, `MaterialCardView`, `ChipGroup`,
  `Chip`, `Slider`, `MaterialButton`, and `BottomNavigationView`,
  `MATERIAL_XML_TREE_OK materialViews=9`,
  `MATERIAL_GENERIC_BUTTON_BOUNDS left=0 top=708 right=480 bottom=836`,
  `MATERIAL_GENERIC_RENDER_OK`, `MATERIAL_GENERIC_TOUCH_OK direct=true`, and
  `MATERIAL_GENERIC_HIT_OK`.

PF-457 caveat: this is compiled Material XML inflation plus direct DLST tree
serialization and generic `findViewAt/performClick` for the probe. It still
does not claim full upstream Material Components AAR compatibility, Material
theming, Coordinator/AppBar behavior, ripple/animation, or generic Android
rendering for arbitrary APKs.

Harness note: the latest 2026-04-27 Yelp rerun used
`SUPERVISOR_HTTP_PROXY=1` because the phone had no active default network. The
app still fetched through the Westlake host/OHBridge bridge; the Android host
routed outbound HTTP through ADB reverse. The runner now fails before app launch
when the phone has no default network unless explicit proxy mode is enabled, and
successful runs copy artifacts into a hash-stamped accepted directory.

PF-454 is now accepted on the Android phone host path as a separate Material
Components API canary, `com.westlake.materialyelp`, built from
`test-apps/07-material-yelp/` and run through `scripts/run-material-yelp.sh`.
This is not a claim of full upstream Google Material Components AAR
compatibility. It proves a first Westlake-owned Material namespace slice:
guest code directly instantiates `com.google.android.material.*` classes
including `MaterialCardView`, `MaterialButton`, `ChipGroup`, `Chip`,
`TextInputLayout`, `TextInputEditText`, `Slider`, `BottomNavigationView`, and
`FloatingActionButton`; the custom `Application` and `Activity` lifecycle
markers are app-owned; the Material view tree is counted; direct `DLST`
rendering produces a visible Chinese Yelp-like Material screen with UTF-8 text
and host-bridge network image tiles; the host preserves the 480x800 guest frame
aspect ratio instead of vertically stretching text; and strict phone touch
packets drive filters, row selection, Save/Saved, and Search. The latest run
used `SUPERVISOR_HTTP_PROXY=1` because the phone DNS resolver could not resolve
`dummyjson.com`; the guest app still fetched bytes through the Westlake
host/OHBridge bridge, with the Android host routed through ADB reverse.

PF-454 evidence:

- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=20fc0c98f9a9371f12deae0d347a01a033e41629a5797aee1cf70d5c39245726`
- `westlake-material-yelp-debug.apk=e586d7afd7df1a2a8c418fb18de952c032dd44c456a3bbf952799c363711ba66`
- `westlake-host app-debug.apk=bc9268855a05d5cda61490fdbf02a297e77bde844e82ef1b279e159304dcaac8`
- Screenshot/log/markers/trace:
  `/mnt/c/Users/dspfa/TempWestlake/material_yelp_target.*`
- Stable accepted copy:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/material_yelp/20fc0c98f9a9371f12deae0d347a01a033e41629a5797aee1cf70d5c39245726_e586d7afd7df1a2a8c418fb18de952c032dd44c456a3bbf952799c363711ba66/`
- Visual gate:
  `/mnt/c/Users/dspfa/TempWestlake/material_yelp_target.visual`
  (`1080x2280`, `content_scale=2.2500`, `content_offset=0.0,240.0`,
  `distinct_colors=313`, `top_red_samples=3812`,
  `bottom_nav_light_samples=7194`, `bottom_nav_red_samples=23`,
  `card_light_samples=19872`, `photo_distinct_colors=28`,
  `photo_colored_samples=564`)
- Host log includes `Surface buffer 1080x1800 for com.westlake.materialyelp`.
- Accepted Material markers include `MATERIAL_APP_ON_CREATE_OK`,
  `MATERIAL_ACTIVITY_ON_CREATE_OK`, `MATERIAL_CLASS_SURFACE_OK count=9`,
  `MATERIAL_UI_BUILD_OK`, `MATERIAL_LANGUAGE_OK locale=zh-Hans`,
  `MATERIAL_NETWORK_BRIDGE_OK`, `MATERIAL_ROW_IMAGE_OK index=0`,
  `MATERIAL_IMAGE_BRIDGE_OK count=4 transport=host_bridge`,
  `MATERIAL_DIRECT_FRAME_OK images=4`, `MATERIAL_TOUCH_POLL_READY`,
  `MATERIAL_TOUCH_POLL_OK`,
  `MATERIAL_FILTER_TOGGLE_OK`, `MATERIAL_SELECT_PLACE_OK`,
  `MATERIAL_SAVE_PLACE_OK`, `MATERIAL_NAV_SAVED_OK`, and
	  `MATERIAL_NAV_SEARCH_OK`.

Current contract workstreams after the PF-461 phone run:

- `PF-455`: accepted for XML inflation/binding/layout proof plus live Yelp
  flows on a full-phone `1080x2280` host surface.
- `PF-459`: accepted for a generic inflated-View DLST draw slice over the Yelp
  XML tree; open for full-fidelity replacement of the Yelp-specific renderer.
- `PF-460`: accepted for generic inflated XML `Button.performClick()` hits and
  ScrollView discovery; open for broad coordinate hit dispatch and visible
  scroll routing.
- `PF-461`: accepted for XML `ListView`/`BaseAdapter` row binding, five image
  byte rebinds, and generic adapter item click; open for RecyclerView-class
  virtualization, raw Bitmap/ImageView decode, and OHOS parity.
- `PF-456`: Android phone live GET path and REST marker contract accepted
  through bridge v2; open for real multi-method matrix execution and OHOS
  adapter parity.
- `PF-457`: accepted for Material XML inflation and probe hit routing; open for
  upstream MDC compatibility, theming/behaviors, ripple/animation, and broad
  generic rendering/hit testing.

Accepted PF-451 markers include `SHOWCASE_XML_INFLATE_OK root=android.widget.LinearLayout views=89 texts=49`,
`SHOWCASE_XML_BIND_OK`, `SHOWCASE_XML_LAYOUT_PROBE_OK`,
`SHOWCASE_XML_API_SURFACE_OK`, `SHOWCASE_UI_BUILD_OK`,
`SHOWCASE_XML_TREE_RENDER_OK`, `SHOWCASE_DIRECT_FRAME_OK`,
`SHOWCASE_TOUCH_POLL_OK`, `SHOWCASE_NAV_MIXER_OK`, `SHOWCASE_NAV_TIMER_OK`,
`SHOWCASE_NAV_SETTINGS_OK`, `SHOWCASE_ADD_LAYER_OK`, `SHOWCASE_TIMER_SET_OK`,
`SHOWCASE_NETWORK_HOST_BRIDGE_OK`, `SHOWCASE_NETWORK_JSON_OK status=200 bytes=417 venues=3 transport=host_bridge`,
`SHOWCASE_NETWORK_IMAGE_OK status=200 bytes=8090 bitmap=100x100 transport=host_bridge`, `SHOWCASE_YELP_CARD_OK`,
`SHOWCASE_VENUE_NEXT_OK`, `SHOWCASE_VENUE_REVIEW_OK`, and
`SHOWCASE_EXPORT_BUNDLE_OK`.

PF-451 evidence:

- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=b498750dce8e022c3e0a30c402ef652ec396d8b04cc2dc66e295ec6ddfbe3854`
- `westlake-showcase-debug.apk=bcd8d63eb2af3d2342110a5df97afd581cc3154d96d96c3de34306597ba5064d`
- Screenshot/log/markers/trace:
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.*`
- Visual gate:
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.visual`
  (`1080x2280`, `distinct_colors=2527`, `settings_nav_teal_samples=744`)
- The accepted phone screenshot shows the Noice-style app after real
  navigation on the Settings page, with a Yelp-like Discover card for
  `Rain Room`, 4.5 stars, 85 reviews, host-bridge network status, active bottom
  navigation, offline/export buttons, and host/OHBridge network markers.

This is a valid same-day showcase delivery, but not yet a generic Android app
runtime completion claim. Current gaps exposed by the Noice-style XML proof:

- `resources.arsc` table parsing still logs `ArrayStoreException`; the layout
  bytes are currently registered directly for the showcase.
- Dynamic app state now updates through controlled page/action handlers, but
  broader arbitrary widget mutation remains a follow-up runtime proof.
- Strict subprocess input now polls the host touch file and drives the showcase;
  the remaining gap is that hit routing is still showcase-specific, not generic
  Android View hit testing.
- The passing PF-451 render path uses a showcase-specific direct `DLST` frame
  writer in `WestlakeLauncher`; generic `Activity.renderFrame`/View-tree
  rendering through `OHBridge.surfaceCreate` remains open under PF-302.
- Live Java/libcore networking is still not accepted. The accepted network
  plane is now the constrained host/OHBridge HTTP adapter, which returns venue
  JSON and image bytes to guest app logic without using the embedded fixture.
  The exposed libcore pieces, including `java.net.URL`/URL parsing support,
  `libcore.io.Linux.android_getaddrinfo`, and related socket/unsafe
  initialization, remain a later compatibility track for broader stock APK
  support.

Phone validation on `cfb7c9e3` now has target `L4WATRECREATE`,
`L4WATFACTORY`, `L4WATAPPFACTORY`, and `L4WATAPPREFLECT` passing with
app-owned markers and WAT-owned lifecycle trace markers. The WAT path now runs
pre-`onCreate` saved-state readiness through the public
`SavedStateRegistryOwner` contract on both initial launch and relaunch; the old
`CV WAT canary precreate savedstate skipped` quarantine is absent from the
accepted trace. WAT attach for the cutoff canary now routes through the shim
`Activity.attach(...)` method instead of direct field assignment in
`WestlakeActivityThread`; the accepted log includes
`PF301 strict WAT attachActivity shim attach begin` and
`PF301 strict WAT attachActivity shim attach done`. Strict WAT Activity
construction now enters and returns through the default
`AppComponentFactory.instantiateActivity(...)` path on both initial launch and
relaunch, with `PF301 strict Instr default factory path returned` in the raw
log. The new `L4WATFACTORY` gate also parses the canary manifest
`android:appComponentFactory`, installs the custom factory into WAT, and routes
both initial launch and recreate through
`CanaryAppComponentFactory.instantiateActivity(...)`. The new
`L4WATAPPFACTORY` gate skips launcher-created `CanaryApp`, forces WAT
pre-Activity `makeApplication()`, enters/returns the controlled canary
`CanaryAppComponentFactory.instantiateApplication(...)` path, calls
`Application.onCreate()`, and only then instantiates the Activity through the
custom factory. The new `L4WATAPPREFLECT` gate then proves the same ordering
through the base `AppComponentFactory.instantiateApplication(...)` reflective
constructor path: the accepted log has `PF301 strict factory application ctor
call` and `PF301 strict factory application ctor returned`, and the app marker
`CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK` proves the constructor ran.
`target L4WATAPPFACTORY`, `target L4WATFACTORY`, `target L4WATRECREATE`,
`target L4RECREATE`, `target L4STATE`, `target L4`, `target L3LOOKUP`, and
`target L3IFACE` still pass as regression gates under the same runtime payload.
The latest accepted proof pair is:

- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`
- `cutoff-canary-debug.apk=cb167f3033c14ea3c2eecb40cff784319d5a5657d85f060c0b15905b8e1c4147`

The canary runner now verifies that installed APK hash before accepting a run;
it auto-installs the local APK when stale and fails closed if the phone hash
still differs.

Hash-stamped evidence bundle:
`/mnt/c/Users/dspfa/TempWestlake/accepted/35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`.

`L3LOOKUP` proves `FragmentManager.findFragmentById(...)`,
`getFragments()`, and `findFragmentByTag(...)` across app-dex to shim-dex after
`commitNow()`. The last blocker was not fragment lookup itself: ART class
linking reached `AssignVTableIndexes()` and branched through the unresolved
`BitVector::ClearAllBits()` sentinel. The accepted runtime makes the helper
available from `bit_vector.h` with explicit word stores.

`L3IFACE` now adds a focused app-dex interface dispatch proof:
`java.util.List<Fragment>.get(0)` on the list returned by shim
`FragmentManager.getFragments()` returns the committed Fragment under the same
runtime hash pair.

`L4` proves the next AndroidX boundary: `ComponentActivity` and committed
Fragment owners both expose `SavedStateRegistry`, registered saved-state
providers can be read back, and `ViewModelProvider` retains the same ViewModel
instance through `ViewModelStore` for both Activity and Fragment owners.
Artifacts:
`/mnt/c/Users/dspfa/TempWestlake/cutoff_canary_target_l4.*`.

`L4STATE` extends that boundary below true Activity recreation. It proves
`SavedStateRegistry` save/restore/one-shot consume, public `SavedStateHandle`
`contains`/`get`/`keys`/`set`, `CreationExtras` delivery through the two-argument
`ViewModelProvider.Factory.create(...)`, and Activity/Fragment
`ViewTreeLifecycleOwner`, `ViewTreeViewModelStoreOwner`, and
`ViewTreeSavedStateRegistryOwner` propagation. Artifacts:
`/mnt/c/Users/dspfa/TempWestlake/cutoff_canary_target_l4state.*`.

`L4RECREATE` extends that state proof through a real `Activity.recreate()` call
on the accepted cutoff path. It proves app-owned save-state, pause, stop,
destroy, a fresh Activity instance, restored direct state, restored
`SavedStateRegistry` provider state, and resumed recreated Activity markers.
Artifacts:
`/mnt/c/Users/dspfa/TempWestlake/cutoff_canary_target_l4recreate.*`.

`L4WATRECREATE` moves that recreate proof onto `WestlakeActivityThread` for the
cutoff canary. It proves a WAT-owned launch/recreate sequence with saved state,
pause, stop, destroy, relaunch, restore, resume, a fresh Activity instance, and
restored `SavedStateRegistry` state. The accepted trace includes
`CV canary WAT launch begin`, `CV WAT precreate savedstate returned`,
`CV WAT recreate begin`, and `CV WAT recreate end`. Artifacts:
`/mnt/c/Users/dspfa/TempWestlake/cutoff_canary_target_l4watrecreate.*`.

`L4WATFACTORY` adds manifest factory selection to that WAT path. It proves
manifest `android:appComponentFactory` discovery for the cutoff canary, factory
installation through `WestlakeActivityThread`, custom factory construction, and
two custom `instantiateActivity(...)` calls across launch and recreate. The
accepted trace includes `CV canary WAT factory manifest parsed
com.westlake.cutoffcanary.CanaryAppComponentFactory` and
`CV canary WAT factory set done`; app markers include
`CANARY_L4FACTORY_CTOR_OK`, two
`CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`, and two
`CANARY_L4FACTORY_ACTIVITY_RETURNED_OK`. Artifacts:
`/mnt/c/Users/dspfa/TempWestlake/cutoff_canary_target_l4watfactory.*`.

`L4WATAPPFACTORY` adds controlled Application factory construction to the WAT
path. It proves the launcher-created canary Application is skipped,
`WestlakeActivityThread` calls `makeApplication()` before Activity
instantiation, the custom canary factory enters and returns
`instantiateApplication(...)`, and WAT then calls `Application.onCreate()`. The
accepted trace includes
`CV canary application manual skipped app factory`,
`CV WAT app factory preactivity makeApplication begin`,
`CV WAT instantiateApplication returned com.westlake.cutoffcanary.CanaryApp`,
`CV WAT application onCreate returned`, and
`CV WAT app factory preactivity makeApplication returned`; app markers include
`CANARY_L4APPFACTORY_INSTANTIATE_APPLICATION_OK`,
`CANARY_L4APPFACTORY_DIRECT_CANARY_APP_OK`, and
`CANARY_L4APPFACTORY_APPLICATION_RETURNED_OK` before
`CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`. Artifacts:
`/mnt/c/Users/dspfa/TempWestlake/cutoff_canary_target_l4watappfactory.*`.

`L4WATAPPREFLECT` adds the reflective constructor proof above
`L4WATAPPFACTORY`. It routes the canary factory through
`super.instantiateApplication(...)`, avoids the previously crashing raw
`cl.loadClass(...)` first step by using the same safe class resolution strategy
as Activity construction, and proves the Application constructor ran before
returning. App markers include `CANARY_L4APPREFLECT_SUPER_CALL`,
`CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK`,
`CANARY_L4APPREFLECT_SUPER_RETURNED`, and
`CANARY_L4APPREFLECT_APPLICATION_RETURNED_OK`; the raw log includes
`PF301 strict factory application ctor call` and
`PF301 strict factory application ctor returned`. Artifacts:
`/mnt/c/Users/dspfa/TempWestlake/cutoff_canary_target_l4watappreflect.*`.

This is a lifecycle convergence gate, not final generic McDonald's readiness:
the accepted WAT canary path now uses shim-owned `Activity.attach(...)` and
precreate named saved-state is no longer skipped. It also uses a default
`AppComponentFactory.instantiateActivity(...)` path rather than the prior
strict constructor bypass, and the controlled canary now proves custom/manifest
`instantiateActivity(...)` plus controlled custom `instantiateApplication(...)`
selection with pre-Activity Application ordering. `L4WATAPPREFLECT` proves the
base reflective Application constructor path for the controlled canary. It is
still cutoff-scoped, though: Hilt-generated real APK factory behavior, generic
real-APK Application-before-Activity ordering, and generic real APK
factory/lifecycle convergence remain open.
The previous canary-specific `DataSourceHelper` skip is now a package/class
guard that only attempts the McDonald's bootstrap for McDonald's launches.

The phone sync path now runs
`scripts/check-westlake-runtime-symbols.sh` before pushing `dalvikvm`; the gate
passes for the accepted runtime and blocks unexpected strong unresolved helper
symbols. The latest cleanup build also removes/quarantines the live `PFCUT`
invoke/link/type probe output: accepted `target L4WATAPPREFLECT`,
`target L4WATAPPFACTORY`, `target L4WATFACTORY`, `target L4WATRECREATE`,
`target L4RECREATE`, `target L4STATE`, `target L4`, `target L3LOOKUP`, and
`target L3IFACE` artifacts scan clean for
`PFCUT` and `DEBUG: Thread`. The
`FragmentManager.mAdded` concrete `ArrayList` typing workaround is removed in
the accepted shim; remaining runtime debt is broader generic interface-dispatch
coverage beyond this fragment collection slice, making WAT lifecycle/factory
selection generic for real APK launch, proving Hilt-safe real APK Application
construction, and self-contained runtime packaging.

---

## Executive Summary

The Westlake engine now runs **Jetpack Compose** natively on a real phone.
The host Activity (`WestlakeActivity`) extends `ComponentActivity` with full
AndroidX lifecycle support, built with Gradle + AGP 8.2 + Kotlin 1.9.

A Compose Material 3 app gallery serves as the home screen. Custom View-based
apps (MockDonalds, Dialer, Social Feed, Huawei Calculator) launch from the
gallery and run with full touch navigation. Real installed APKs launch via
`startActivity()`.

**Apps running on the engine:**

1. **App Gallery** — Jetpack Compose Material 3 (LazyColumn, Cards, dark theme)
2. **MockDonalds** — Full ordering app (5 screens, View-based)
3. **Dialer** — Phone dialer (7 screens, View-based)
4. **Social Feed** — Social media feed (View-based)
5. **Huawei Calculator** — Real APK DEX + XML layout
6. **Real installed APKs** — Calculator, Clock, Settings, Calendar via startActivity

The engine has been validated on three platforms:

| Platform | Runtime | FPS | Status |
|----------|---------|-----|--------|
| x86_64 host (Linux) | ART (AOT) | 60 | Stable, primary dev target |
| ARM64 on phone | dalvikvm (interpreter) | 120 | Runs natively on Mate 20 Pro |
| Huawei Mate 20 Pro | Android 10 native ART | native | Compose + View apps, 6 apps |

---

## Milestone: Jetpack Compose (2026-03-25)

Jetpack Compose renders natively on the phone via a Gradle-built host APK.

### Architecture

```
WestlakeActivity (extends ComponentActivity)
├── onCreate: setContent { ComposeGallery() }   ← Compose renders immediately
├── Engine thread: loads app.dex + aosp-shim.dex via DexClassLoader
│   └── MockDonaldsApp.main() initializes MiniServer
└── User taps app card → launchCustomApp(className, initMethod, showMethod)
    └── Reflection call → MockApp.init(ctx) + MockApp.showMenu()
        └── setContentView(viewRoot) replaces Compose with View-based UI
            └── Back → showHome() restores Compose gallery
```

### Build System

```
westlake-host-gradle/
├── build.gradle.kts          # AGP 8.2, Kotlin 1.9.10
├── app/build.gradle.kts      # Compose BOM 2023.10, Material 3
├── app/src/main/
│   ├── java/.../WestlakeActivity.kt  # ComponentActivity + Compose
│   ├── assets/
│   │   ├── aosp-shim.dex    # 2,168 shim classes
│   │   ├── app.dex           # Custom apps (MockDonalds, Dialer, etc.)
│   │   └── material_icons.ttf
│   └── jniLibs/arm64-v8a/
│       ├── liboh_bridge.so
│       └── libohbridge_android.so
└── Output: app-debug.apk (11MB)
```

### Why Gradle (not manual build)

Previous approach (manual javac + dx + aapt + jarsigner) failed for Compose:
- Compose requires Kotlin compiler plugin (not available in manual builds)
- AndroidX lifecycle/savedstate/viewmodel have complex dependency chains
- R$id resource IDs must match between classloaders
- LifecycleRegistry requires main thread + correct initialization order

Gradle solves all of this — Compose is a normal dependency, lifecycle works
natively, no reflection hacks needed.

### Platform Coupling (Updated)

**What depends on the Android phone:**

| Component | Coupling | Notes |
|-----------|----------|-------|
| `ComponentActivity` | AndroidX library | On OHOS: replace with shim |
| `setContent {}` | Compose runtime | On OHOS: Compose renders to Canvas → bridge |
| `Canvas/Paint/Path` | Android framework | On OHOS: bridge to ArkUI/Skia (~25 functions) |
| `View` classes | Android framework | On OHOS: our shim View classes |
| `DexClassLoader` | Android runtime | On OHOS: ART runtime (already works) |
| `Looper/Handler` | Android framework | On OHOS: our shim Looper |

**What is platform-independent:**

| Component | Description |
|-----------|-------------|
| All custom app code | MockDonalds, Dialer, Social Feed |
| MiniServer + MiniActivityManager | Activity lifecycle management |
| BinaryXmlParser + XmlTestHelper | XML layout inflation |
| ShimCompat | Reflection-based framework compat |
| OHBridge Java side | 170 JNI method declarations |
| Compose gallery UI | MaterialTheme, LazyColumn, Cards |

### Milestone: Real APK Resource Loading (2026-03-25)

Real published APK UI rendered inside Westlake from resources.arsc + binary XML:

```
APK ZIP → resources.arsc → ResourceTable → getString/getColor/getDimension
APK ZIP → res/layout/counter.xml → AXML parser → View tree → display
```

**Tested with:**
- **Simple Counter** (F-Droid, 737KB): 59 strings, 110 integers, counter.xml inflated
- **Noice** (F-Droid, 5.1MB): 1640 strings, 2200 integers, 100+ layouts parsed
- **Huawei Calculator** (system, 1MB): 272 strings, 1113 integers, layouts resolved

**Pipeline components:**
| Component | What it does | Lines |
|-----------|-------------|-------|
| ResourceTable.java | Parses resources.arsc binary format | 601 |
| ResourceTableParser.java | Convenience wrapper | 209 |
| ApkResourceLoader.java | Extracts resources from APK ZIP | 112 |
| ApkViewRunner.kt | AXML parser + View creation in Kotlin | 400+ |
| BinaryXmlParser.java | Compiled XML parser (AXML) | 762 |

**What works:**
- String resolution: `R.string.app_name` → "Simple Counter"
- Color resolution: `R.color.bg_dark` → #FFF1F1F1
- Layout file resolution: `R.layout.counter` → "res/layout-xlarge-land-v4/counter.xml"
- Binary XML inflation: AXML → RelativeLayout + Button + TextView
- Resource references: `@0x7f0c0026` → "string/plus" → "+"
- Functional buttons with click handlers from layout structure

**Full production stack verified on phone:**
| Feature | Library | Status |
|---------|---------|--------|
| Compose UI | Material 3 + Foundation | ✅ Native |
| Navigation | navigation-compose 2.7.5 | ✅ Multi-screen |
| REST API | Retrofit 2.9 + OkHttp 4.12 | ✅ Dog images fetched |
| Image loading | Coil 2.5 | ✅ Async display |
| State management | ViewModel + Compose State | ✅ Counter persists |
| JSON parsing | Gson 2.10 | ✅ API responses |
| Coroutines | kotlinx-coroutines 1.7.3 | ✅ Async operations |
| SQLite | Bridge to phone's real SQLite | ✅ Reflection bridge |
| Network | java.net.HttpURLConnection | ✅ Works natively |
| resources.arsc | ResourceTable parser | ✅ 1640+ strings from real APK |
| Binary XML | AXML parser in Kotlin | ✅ Layout inflated |

### Remaining Gaps for Production APKs

| Gap | What's needed | Effort |
|-----|--------------|--------|
| Generic WAT lifecycle | Broaden accepted WAT launch/recreate beyond cutoff canary to real APK lifecycle semantics | High |
| AppComponentFactory/Hilt | Broaden accepted canary custom activity/application factory paths to Hilt-safe real APK factories and AOSP-correct ordering | High |
| Self-contained runtime | Package dalvikvm, shim dex, libs, APKs, and bootstrap without Android host staging | High |
| OHOS backend closure | Services/package/window/input/storage/permissions mapped below Android semantics | High |
| Full AXML → View inflation | Handle all View types + all attributes | 2 weeks |
| Resource ID resolver | Map R.string.xxx int IDs to values at runtime | 1 week |
| Multi-Activity | Read AndroidManifest, manage Activity stack | 2 weeks |
| Fragments | Broaden from cutoff FragmentManager proof to real app fragment state/back stack | 2 weeks |
| RecyclerView | Most production lists use this | 1 week |
| Runtime permissions | requestPermissions flow | 1 week |

### OHOS Port Path

To run on OpenHarmony:
1. Package `dalvikvm`, `aosp-shim.dex`, the controlled showcase APK, extracted
   resources, and launch properties into an OHOS app-owned runtime directory.
2. Start Westlake from an OHOS Stage Ability/XComponent host instead of the
   Android phone host Activity.
3. Replay the current DLST frame stream into the XComponent first, then promote
   to the generic `OHBridge.surfaceCreate`/View draw path after visible OHOS
   proof is stable.
4. Route XComponent touch/key callbacks into the Westlake input protocol, then
   replace the file protocol with a direct native event queue.
5. Implement the PF-452 host/OHBridge HTTP adapter so the venue JSON/image path
   records `SHOWCASE_NETWORK_HOST_BRIDGE_OK` without
   `SHOWCASE_NETWORK_NATIVE_GAP_OK`.

Detailed API mapping and acceptance gates:
`docs/engine/OHOS-CONTROLLED-SHOWCASE-INTEGRATION.md`.

---

## Architecture

### Full Pipeline (text diagram)

```
+------------------------------------------------------------------+
|                    Android Phone (Mate 20 Pro)                    |
|                                                                   |
|  +-----------------------------+   +---------------------------+  |
|  |     WestlakeActivity.java   |   |    liboh_bridge.so        |  |
|  |  (extends android.app.Act.) |   |  (~25 Canvas/Paint/Path   |  |
|  |  - child-first DexClassLdr  |   |   JNI methods bridged)    |  |
|  |  - loads app.dex at runtime |   +---------------------------+  |
|  +-------------|---------------+               |                  |
|                |                               |                  |
|  +-------------|-------------------------------|---------+        |
|  |             v            App DEX Space                |        |
|  |                                                       |        |
|  |  +----------------+   +----------------------------+  |        |
|  |  | MockDonaldsApp |   | MiniServer                 |  |        |
|  |  | (entry point)  |-->| - MiniActivityManager      |  |        |
|  |  +----------------+   | - MiniWindowManager        |  |        |
|  |                       | - Activity lifecycle        |  |        |
|  |                       +------|---------------------+  |        |
|  |                              |                        |        |
|  |                              v                        |        |
|  |  +---------------------------------------------------+|       |
|  |  |           Activity Stack                           ||       |
|  |  |  MenuActivity -> ItemDetailActivity -> CartActivity||       |
|  |  |  (real LinearLayout, ListView, Button, TextView)   ||       |
|  |  +---------------------------------------------------+|       |
|  |                              |                        |        |
|  |                              v                        |        |
|  |  +---------------------------------------------------+|       |
|  |  |           View Tree (native Android Views)         ||       |
|  |  |  LinearLayout                                      ||       |
|  |  |  +-- TextView ("MockDonalds Menu")                 ||       |
|  |  |  +-- ListView (8 menu items via BaseAdapter)       ||       |
|  |  |  +-- Button ("View Cart (2)")                      ||       |
|  |  +---------------------------------------------------+|       |
|  +-------------------------------------------------------+        |
|                              |                                    |
|                              v                                    |
|  +-----------------------------------------------------------+   |
|  |  ShimCompat (reflection-based framework compatibility)     |   |
|  |  - Bridges framework version differences via reflection    |   |
|  |  - Handles Context, Resources, PackageManager stubs        |   |
|  +-----------------------------------------------------------+   |
|                              |                                    |
|  +-----------------------------------------------------------+   |
|  |  OHBridge JNI Layer (170 registered methods)               |   |
|  |  - Canvas: drawText, drawRect, drawLine, drawBitmap, ...   |   |
|  |  - Paint: setColor, setTextSize, measureText, ...          |   |
|  |  - Path: moveTo, lineTo, quadTo, close, ...               |   |
|  |  - Surface: createSurface, lockCanvas, unlockAndPost       |   |
|  +-----------------------------------------------------------+   |
|                              |                                    |
|  +-----------------------------------------------------------+   |
|  |  Android Framework (native ART on phone)                   |   |
|  |  Canvas, Paint, FontMetrics, Path (real implementations)   |   |
|  +-----------------------------------------------------------+   |
+------------------------------------------------------------------+
```

### ClassLoader Hierarchy

```
BootClassLoader (Android framework)
  |
  +-- PathClassLoader (WestlakeActivity — host app)
        |
        +-- Child-First DexClassLoader (app.dex)
              |
              +-- MockDonalds classes
              +-- MiniServer, MiniActivityManager
              +-- ShimCompat, OHBridge
              +-- Shim classes (android.widget.*, android.view.*)
              |
              (Parent delegation: only for java.*, android.app.Activity)
```

The child-first classloader is critical: it ensures that our shim `android.widget.ListView`
(which works with `MiniActivityManager`) is loaded instead of the framework's real one,
while still delegating `android.app.Activity` to the real framework so
`WestlakeActivity` can extend it.

---

## What's Tested

### Components

| Component | Description | Status |
|-----------|-------------|--------|
| MiniServer | Lightweight app server replacing Android's `ActivityManagerService` | Tested |
| MiniActivityManager | Activity lifecycle: create, start, resume, pause, stop, destroy, restart | Tested |
| MiniWindowManager | View tree management, measure/layout/draw cycle | Tested |
| OHBridge JNI | 170 registered JNI methods for Canvas/Paint/Path/Surface/Prefs/RDB | Tested |
| Child-first DexClassLoader | Loads app.dex, shim classes override framework classes | Tested |
| ShimCompat | Reflection-based compatibility for framework version differences | Tested |
| Native Views | LinearLayout, ListView, Button, TextView, FrameLayout, RelativeLayout | Tested |
| Touch input | DOWN/UP events routed to View tree, click handlers fire | Tested |
| Activity navigation | Menu -> ItemDetail -> Cart -> Checkout, with back stack | Tested |
| Cart persistence | Cart counter persists across Activity navigation | Tested |
| Intent + extras | String, int, double, boolean extras passed between Activities | Tested |
| BaseAdapter + ListView | Dynamic list population, view recycling | Tested |
| Multi-app hosting | Multiple apps in single DEX, app switching via buttons | Tested |
| GradientDrawable | Rounded rects, circles, strokes for Material Design UI | Tested |
| Alphabetical sections | Section headers in ListView (contacts A-Z) | Tested |
| Call timer | Live-updating timer on in-call screen via background thread | Tested |

### Apps Running on Engine

| App | Screens | Features |
|-----|---------|----------|
| MockDonalds | 5 | Menu with categories, item detail, cart, checkout, order confirmation |
| Dialer | 7 | T9 keypad, call history (incoming/outgoing/missed), contacts with avatars, contact detail, add contact, in-call with timer, in-call DTMF keypad, voicemail |
| Calculator | 1 | XML-inflated layout from AXML, functional arithmetic |

### End-to-End Flow (Phone)

1. WestlakeActivity launches, creates child-first DexClassLoader for `app.dex`
2. MockDonaldsApp.main() runs, initializes MiniServer
3. MiniActivityManager starts MenuActivity (8 menu items in ListView)
4. User taps menu item -> ItemDetailActivity shows item details
5. User taps "Add to Cart" -> CartActivity shows cart contents
6. User taps "Back" -> returns to MenuActivity, cart counter updated
7. Full touch navigation cycle verified on physical device

---

## Platform Coupling

The Westlake engine is designed for minimal platform coupling. Only two components
are platform-specific:

### Platform-Specific (changes per target)

| Component | Size | Description |
|-----------|------|-------------|
| `liboh_bridge.so` | ~25 methods | JNI bridge to platform Canvas/Paint/Path |
| `WestlakeActivity.java` | ~150 lines | Host Activity with child-first classloader |

### liboh_bridge.so API Surface

The native bridge depends on exactly 4 Android classes with ~25 methods total:

```
android.graphics.Canvas
  - drawText(String, float, float, Paint)
  - drawRect(float, float, float, float, Paint)
  - drawLine(float, float, float, float, Paint)
  - drawBitmap(Bitmap, float, float, Paint)
  - drawCircle(float, float, float, Paint)
  - drawRoundRect(RectF, float, float, Paint)
  - clipRect(float, float, float, float)
  - save(), restore(), translate(float, float)
  - getWidth(), getHeight()

android.graphics.Paint
  - setColor(int), setTextSize(float), setStyle(Style)
  - setAntiAlias(boolean), setStrokeWidth(float)
  - measureText(String)
  - getFontMetrics(FontMetrics)

android.graphics.Paint.FontMetrics
  - ascent, descent, top, bottom (float fields)

android.graphics.Path
  - moveTo(float, float), lineTo(float, float)
  - quadTo(float, float, float, float)
  - close(), reset()
```

This is the **only** native code that needs porting per platform. On OHOS, these
25 methods would call ArkUI/Skia equivalents. On any other platform with a 2D
canvas API (e.g., SDL, Cairo, HTML5 Canvas), the same 25 methods are all that
need reimplementation.

### Platform-Independent (stays the same everywhere)

| Component | Description |
|-----------|-------------|
| MiniServer | App server, Activity lifecycle management |
| MiniActivityManager | Activity stack, lifecycle state machine |
| MiniWindowManager | View tree measure/layout/draw |
| All View classes | LinearLayout, ListView, Button, TextView, etc. |
| ShimCompat | Reflection-based compatibility layer |
| OHBridge Java side | 170 JNI method declarations (auto-generated) |
| MockDonalds app | All application code |
| Intent, Bundle, extras | Inter-Activity communication |
| BaseAdapter, ArrayAdapter | List data binding |
| Canvas/Paint/Path Java API | Drawing API (Java side unchanged) |

In total, **~150 lines of Java + ~25 C functions** are platform-specific.
Everything else (thousands of lines of Java) is platform-independent and runs
unchanged on any target.

---

## Test Results

### x86_64 Host (ART, AOT-compiled boot image)

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

- **Performance:** ~60 fps sustained
- **Startup:** <1 second (AOT boot image)
- **All 14 MockDonalds tests pass**

### ARM64 dalvikvm on Phone

```
[MockDonaldsApp] Starting via dalvikvm ...
[OHBridge arm64] 170 methods registered
[D] MiniActivityManager: startActivity: MenuActivity
[D] MiniActivityManager:   performCreate, performStart, performResume
[MockDonaldsApp] MenuActivity launched
[MockDonaldsApp] Frame rendered at 120fps
```

- **Performance:** ~120 fps (interpreter, lightweight draw)
- **Runtime:** dalvikvm (KitKat portable interpreter, 64-bit patched)

### Huawei Mate 20 Pro (Native Android 10)

- **Device:** Huawei Mate 20 Pro (LYA-L29)
- **OS:** Android 10 (EMUI)
- **Runtime:** Native ART
- **Views:** Real Android LinearLayout, ListView, Button, TextView
- **Touch:** Full touch navigation working
- **Navigation flow:**
  1. Menu screen (8 items in ListView) -- tap item
  2. Item detail screen (name, price, description) -- tap "Add to Cart"
  3. Cart screen (items list, total price) -- tap "Back"
  4. Menu screen (cart counter updated in button text)
- **Cart counter:** Persists across all navigation transitions

---

## Milestone: XML Layout Inflation (2026-03-24)

Binary XML (AXML) layout inflation from real APKs is now working on the phone.
The engine can parse compiled Android XML layouts and create real Android Views
from the parsed events, bypassing the phone's `LayoutInflater` (which expects
internal `XmlBlock.Parser`).

### Components

| Component | Description |
|-----------|-------------|
| `BinaryXmlParser` | Parses compiled AXML format (string pool, resource IDs, XML tree) |
| `XmlTestHelper.inflateFromParser()` | Walks XML events, creates Views via `createViewForTag()` |
| `XmlTestHelper.applyAttributes()` | Handles layout_width/height/weight, text, textSize, textColor, orientation, padding, background, gravity |
| `XmlTestHelper.loadCalculatorApp()` | Loads separate DEX + inflates XML layout + wires button handlers |

### Tested APK Layouts

| APK | Result |
|-----|--------|
| Custom Calculator (our DEX + XML) | Fully functional: layout inflated, buttons wired, arithmetic works |
| Huawei Contacts Dialer (dialpad_huawei.xml) | Root LinearLayout inflated, custom Huawei View classes skipped |

### Dimension Handling

AXML `decodeDimension()` returns dp values labeled as "px". The inflater
multiplies by display density (e.g., 3.0x on Mate 20 Pro) for correct sizing.

---

## Known Issues

| Issue | Severity | Description | Workaround |
|-------|----------|-------------|------------|
| No SQLite | Medium | SQLiteDatabase not available on phone path | In-memory data structures, no persistence across app restarts |
| No SharedPreferences | Medium | SharedPreferences not implemented for phone path | Cart state held in memory, lost on app kill |
| Custom View classes in APKs | Medium | Third-party APKs use custom View subclasses not in our shim | Must load APK's classes.dex alongside layout XML |
| No resources.arsc on phone | Low | Resource string lookup not wired | Hardcoded strings in Java code |
| No Bitmap loading | Low | BitmapFactory.decodeResource() not implemented | Text-only UI, no images |
| No animation | Low | View animation framework not implemented | Static transitions between Activities |

### Path Forward

1. **OHOS port** — Reimplement `liboh_bridge.so` (~25 functions) against ArkUI/Skia on OpenHarmony
2. **Full APK loading** — Load APK's classes.dex + XML layouts + resources.arsc together
3. **SQLite** — Port `android.database.sqlite` via JNI to native SQLite library
4. **SharedPreferences** — Implement file-backed XML storage

---

## File Locations

| What | Path |
|------|------|
| WestlakeActivity | `westlake-host/src/com/westlake/host/WestlakeActivity.java` |
| liboh_bridge.so (phone) | `westlake-host/jni/ohbridge_native.c` |
| ShimCompat | `shim/java/android/app/ShimCompat.java` |
| MiniServer | `shim/java/android/app/MiniServer.java` |
| MiniActivityManager | `shim/java/android/app/MiniActivityManager.java` |
| MockDonalds app | `test-apps/04-mockdonalds/src/com/example/mockdonalds/` |
| MockApp (Material UI) | `test-apps/04-mockdonalds/src/com/example/mockdonalds/MockApp.java` |
| XmlTestHelper | `test-apps/04-mockdonalds/src/com/example/mockdonalds/XmlTestHelper.java` |
| BinaryXmlParser | `shim/java/android/content/res/BinaryXmlParser.java` |
| OHBridge Java | `shim/java/com/ohos/shim/bridge/OHBridge.java` |
| x86_64 dalvikvm | `art-universal-build/build/bin/dalvikvm` |
| x86_64 OHBridge stub | `art-universal-build/stubs/ohbridge_stub.c` |
| Software renderer | `art-universal-build/stubs/ohbridge_render.c` |
| ARM64 dalvikvm | `art-universal-build/build-ohos-arm64/bin/dalvikvm` |
