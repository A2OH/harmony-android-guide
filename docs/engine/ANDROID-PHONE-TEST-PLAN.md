# Westlake Engine: Android Phone Test Plan

**Date:** 2026-04-27
**Status:** Active phone validation. Control and target `L1`/`L2` cutoff canary passed
on ONEPLUS A6003. Target `L3`, `L3LOOKUP`, `L3IFACE`, `L4`, `L4STATE`,
`L4RECREATE`, `L4WATRECREATE`, `L4WATFACTORY`, `L4WATAPPFACTORY`, and
`L4WATAPPREFLECT` AndroidX canaries pass with app-owned markers and recorded
runtime hashes. PF-451 controlled showcase and PF-452 host/OHBridge network
bridge both pass on the connected phone with visible UI and accepted showcase
interaction markers. PF-453 separate Yelp live APK also passes with live
internet JSON/image fetches through the portable host/OHBridge bridge. PF-454
Material Components canary also passes with direct `com.google.android.material.*`
class instantiation, visible Material-style DLST rendering, and strict phone
touch markers. PF-455 now passes for XML-backed Yelp inflation/binding plus the
live Yelp flows on a 1K-class host surface. PF-457 now passes for compiled
Material XML inflation, direct DLST tree rendering, and generic
`findViewAt/performClick` into a `MaterialButton` listener. PF-456 REST matrix
coverage now passes on the Android phone through the portable bridge v2
contract. PF-459 now passes for a generic inflated-View DLST draw slice over
the Yelp XML tree. The next active phone gate is PF-460 generic hit/scroll
routing, while PF-456 remains open for OHOS adapter parity and PF-457 remains
open for broader Material compatibility.

---

## Current lab phone connection

Last verified: 2026-04-27.

Current accepted phone evidence:
- `scripts/run-controlled-showcase.sh`: passed for `com.westlake.showcase`, a
  controlled Noice-style local ambient mixer with a Yelp-like Discover venue
  panel. The accepted run wires compiled XML layout bytes into the standalone
  guest, inflates `showcase_activity.xml` into `89` Views / `49` TextViews,
  binds the expected Noice-style and venue widget IDs, probes layout, renders a
  visible phone frame from the XML tree, and records real strict-mode
  touch/navigation/action markers for load venues, next venue, review, and
  export.
- runtime hashes for the accepted controlled showcase run:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`,
  `aosp-shim.dex=b498750dce8e022c3e0a30c402ef652ec396d8b04cc2dc66e295ec6ddfbe3854`,
  `westlake-showcase-debug.apk=bcd8d63eb2af3d2342110a5df97afd581cc3154d96d96c3de34306597ba5064d`
- controlled showcase artifacts:
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.log`,
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.markers`,
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.trace`, and
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.png`
- controlled showcase visual gate:
  `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.visual`
  (`1080x2280`, `distinct_colors=2527`, `settings_nav_teal_samples=744`)
- screenshot content check: the phone capture shows the controlled Noice-style
  app after real navigation on the Settings page, with a Yelp-like Discover
  card for `Rain Room`, 4.5 stars, 85 reviews, `remote REST list 3 card 2`,
  active bottom navigation, offline/export controls, and runtime evidence
  `APK dex -> Westlake dalvikvm -> shim Activity -> host bridge -> DLST`.
- controlled showcase caveat: this proof uses a showcase-specific direct
  `DLST` frame path. Generic View-tree rendering through
  `OHBridge.surfaceCreate` remains open under PF-302. Additional PF-451 gaps
  recorded in the accepted run are `resources.arsc` table
  `ArrayStoreException`, broader arbitrary widget mutation not yet proven,
  showcase-specific hit routing instead of generic Android View hit testing,
  and full live Java/libcore networking. The venue panel now records
  `SHOWCASE_NETWORK_HOST_BRIDGE_OK`, `SHOWCASE_NETWORK_JSON_OK`, and
  `SHOWCASE_NETWORK_IMAGE_OK` through `transport=host_bridge` with no
  `SHOWCASE_NETWORK_NATIVE_GAP_OK` in the accepted bridge-required run. Missing
  Java/libcore runtime work includes `java.net.URL`,
  `libcore.io.Linux.android_getaddrinfo`, and related socket/unsafe
  initialization before stock Java networking can be claimed.
- `scripts/run-yelp-live.sh`: passed for `com.westlake.yelplive`, a separate
  Yelp-like live-data APK built from `test-apps/06-yelp-live/`. The accepted run
  proves WAT Application/Activity lifecycle, compiled XML inflation and ID
  binding, live HTTPS JSON from `dummyjson.com`, bounded live thumbnail bytes
  from `picsum.photos`, app state updates, direct `DLST` rendering with visible
  remote thumbnails and a Material-styled scrollable restaurant list, and
  touch-driven category
  selection, filter toggles, scroll, row selection, Details, Save, Saved, and
  Search actions.
- runtime hashes for the accepted Yelp live run:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`,
  `aosp-shim.dex=7f52c37ac29502b57f36a692d9c835e535ec8cfd7f64cb45e2f31f9c659828d1`,
  `westlake-yelp-live-debug.apk=24d1444b5ebf2319722c7168b4a849b7f022cc869b1708734695e381c44abfda`
- Yelp live artifacts:
  `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.log`,
  `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.markers`,
  `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.trace`, and
  `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.png`
- Yelp live stable accepted copy:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/yelp_live/7f52c37ac29502b57f36a692d9c835e535ec8cfd7f64cb45e2f31f9c659828d1_24d1444b5ebf2319722c7168b4a849b7f022cc869b1708734695e381c44abfda/`
- Yelp live visual gate:
  `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.visual`
  (`1080x2280`, `distinct_colors=5593`, `top_red_samples=3033`,
  `bottom_nav_light_samples=5199`, `bottom_nav_red_samples=26`,
  `photo_distinct_colors=11441`, `photo_colored_samples=17221`,
  `photo_natural_samples=5053`)
- Yelp live host log includes `Surface buffer 1080x2280 for
  com.westlake.yelplive`.
- Yelp live accepted app markers include `YELP_ACTIVITY_ON_CREATE_OK`,
  `YELP_XML_RESOURCE_WIRE_OK`, `YELP_XML_INFLATE_OK`,
  `YELP_XML_BIND_OK`,
  `YELP_XML_LAYOUT_PROBE_OK target=480x1013 measured=480x1013`,
  `YELP_UI_BUILD_OK`, `YELP_FULL_RES_FRAME_OK logical=480x1013 target=1080x2280 navTop=824`,
  `YELP_GENERIC_VIEW_DRAW_OK reason=initial bytes=1173 views=30 texts=21 buttons=17 height=1013 source=inflated_xml`,
  `YELP_NETWORK_BRIDGE_OK`,
  `YELP_LIVE_JSON_OK`, `YELP_LIVE_IMAGE_OK`,
  `YELP_LIVE_ROW_IMAGE_OK index=4`, `YELP_CARD_OK`,
  `YELP_CATEGORY_SELECT_OK`, `YELP_FILTER_TOGGLE_OK`,
  `YELP_LIST_SCROLL_OK`, `YELP_NEXT_PLACE_OK`, `YELP_DETAILS_OPEN_OK`,
  `YELP_SAVE_PLACE_OK`,
  `YELP_NAV_SAVED_OK`, and `YELP_NAV_SEARCH_OK`.
- Yelp REST matrix accepted app markers include `YELP_REST_MATRIX_BEGIN`,
  `YELP_REST_POST_OK`, `YELP_REST_HEADERS_OK`, `YELP_REST_METHODS_OK`,
  `YELP_REST_HEAD_OK`, `YELP_REST_STATUS_OK status=418`,
  `YELP_REST_REDIRECT_OK`, `YELP_REST_TRUNCATE_OK truncated=true`,
  `YELP_REST_TIMEOUT_OK`, and `YELP_REST_MATRIX_OK`.
- `scripts/run-yelp-live.sh` now has a phone default-network preflight,
  configurable post-scroll/action waits, explicit `SUPERVISOR_HTTP_PROXY=1`
  ADB-reverse fallback for supervisor-host live data when the device is
  offline, `480x1013` logical-frame tap conversion, a full-phone `1080x2280`
  host surface buffer assertion, and hash-stamped accepted artifact copies. The
  latest 2026-04-27 accepted rerun used proxy mode because the phone reported no
  active default network.
- `scripts/run-material-xml-probe.sh`: passed for
  `com.westlake.materialxmlprobe`, a compiled Material XML inflation and generic
  hit probe built from `test-apps/09-material-xml-probe/`. The accepted run
  proves XML tags for `TextInputLayout`, `TextInputEditText`,
  `MaterialCardView`, `ChipGroup`, `Chip`, `Slider`, `MaterialButton`, and
  `BottomNavigationView` inflate to Westlake-owned shim classes, the inflated
  tree renders through the direct DLST serializer, the runner reads
  `MATERIAL_GENERIC_BUTTON_BOUNDS` and taps the real button center, and the
  APK's `MaterialButton` listener records `MATERIAL_GENERIC_HIT_OK`.
- runtime hashes for the accepted Material XML probe:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`,
  `aosp-shim.dex=bf33aba0a8923e8b7d2cb006ee98042bb217021236a7cfe185a004f0e269716a`,
  `westlake-material-xml-probe-debug.apk=ded93614084cdd28a46bcbcbd7eb8cba78504c3c228e0f95835a6ebf42a6e6c9`
- Material XML probe artifacts:
  `/mnt/c/Users/dspfa/TempWestlake/material_xml_probe_target.log`,
  `/mnt/c/Users/dspfa/TempWestlake/material_xml_probe_target.markers`,
  `/mnt/c/Users/dspfa/TempWestlake/material_xml_probe_target.trace`, and
  `/mnt/c/Users/dspfa/TempWestlake/material_xml_probe_target.png`
- `scripts/run-material-yelp.sh`: passed for `com.westlake.materialyelp`, a
  separate Material Components API canary built from
  `test-apps/07-material-yelp/`. The accepted run proves app-owned
  `Application`/`Activity` markers, direct instantiation of
  `MaterialCardView`, `MaterialButton`, `ChipGroup`, `Chip`,
  `TextInputLayout`, `TextInputEditText`, `Slider`,
  `BottomNavigationView`, and `FloatingActionButton`, visible direct `DLST`
  rendering with Chinese UTF-8 text, aspect-ratio-preserved host display, four
  host-bridge network image tiles, and touch-driven filters, row selection,
  Save/Saved, and Search. The latest accepted run used
  `SUPERVISOR_HTTP_PROXY=1` because the phone resolver could not resolve
  `dummyjson.com`; the app fetch remained a Westlake host/OHBridge bridge path
  and the Android host routed outbound HTTP through ADB reverse.
- runtime hashes for the accepted Material canary run:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`,
  `aosp-shim.dex=20fc0c98f9a9371f12deae0d347a01a033e41629a5797aee1cf70d5c39245726`,
  `westlake-material-yelp-debug.apk=e586d7afd7df1a2a8c418fb18de952c032dd44c456a3bbf952799c363711ba66`,
  `westlake-host app-debug.apk=bc9268855a05d5cda61490fdbf02a297e77bde844e82ef1b279e159304dcaac8`
- Material canary artifacts:
  `/mnt/c/Users/dspfa/TempWestlake/material_yelp_target.log`,
  `/mnt/c/Users/dspfa/TempWestlake/material_yelp_target.markers`,
  `/mnt/c/Users/dspfa/TempWestlake/material_yelp_target.trace`, and
  `/mnt/c/Users/dspfa/TempWestlake/material_yelp_target.png`
- Material canary stable accepted copy:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/material_yelp/20fc0c98f9a9371f12deae0d347a01a033e41629a5797aee1cf70d5c39245726_e586d7afd7df1a2a8c418fb18de952c032dd44c456a3bbf952799c363711ba66/`
- Material canary visual gate:
  `/mnt/c/Users/dspfa/TempWestlake/material_yelp_target.visual`
  (`1080x2280`, `content_scale=2.2500`, `content_offset=0.0,240.0`,
  `distinct_colors=313`, `top_red_samples=3812`,
  `bottom_nav_light_samples=7194`, `bottom_nav_red_samples=23`,
  `card_light_samples=19872`, `photo_distinct_colors=28`,
  `photo_colored_samples=564`)
- Material canary host log includes `Surface buffer 1080x1800 for
  com.westlake.materialyelp`.
- Material canary accepted app markers include
  `MATERIAL_APP_ON_CREATE_OK`, `MATERIAL_ACTIVITY_ON_CREATE_OK`,
  `MATERIAL_CLASS_SURFACE_OK`, `MATERIAL_UI_BUILD_OK`,
  `MATERIAL_LANGUAGE_OK`, `MATERIAL_NETWORK_BRIDGE_OK`,
  `MATERIAL_ROW_IMAGE_OK`, `MATERIAL_IMAGE_BRIDGE_OK`,
  `MATERIAL_DIRECT_FRAME_OK`, `MATERIAL_TOUCH_POLL_READY`,
  `MATERIAL_TOUCH_POLL_OK`, `MATERIAL_FILTER_TOGGLE_OK`,
  `MATERIAL_SELECT_PLACE_OK`, `MATERIAL_SAVE_PLACE_OK`,
  `MATERIAL_NAV_SAVED_OK`, and `MATERIAL_NAV_SEARCH_OK`.
- PF-459 Android phone generic draw slice: accepted for serializing the
  inflated Yelp XML View tree into DLST with `30` views, `21` text widgets,
  `17` buttons, and logical height `1013`.
- PF-455/PF-459 remaining gate: replace the polished controlled direct Yelp
  `DLST` frame writer with a full-fidelity generic View-tree render path over
  the inflated Yelp widgets.
- PF-456 Android phone REST matrix gate: accepted for
  methods/headers/body/status/redirect/timeout/truncation/error bodies through
  the bridge v2 contract.
- PF-456 remaining gate: repeat the same contract on OHOS after the adapter
  exists.
- PF-457 remaining gate: expand beyond the accepted Material XML probe into
  upstream Material Components compatibility for the controlled surface, full
  Material theming, `CoordinatorLayout`/`AppBarLayout` behavior,
  ripple/animation support, and broader generic Android View hit testing.
- `scripts/run-cutoff-canary.sh control L1`: passed with app-owned
  `CANARY_L0_OK`, `CANARY_L1_ON_CREATE`, `CANARY_L1_VIEW_BUILD_OK`,
  `CANARY_L1_OK`, `CANARY_L1_ON_START`, and `CANARY_L1_ON_RESUME`
- `scripts/run-cutoff-canary.sh target L1`: passed with the same marker set
- `scripts/run-cutoff-canary.sh control L2` and `target L2`: passed with
  app-owned `CANARY_L2_ON_CREATE`, `CANARY_L2_OK`, `CANARY_L2_ON_START`, and
  `CANARY_L2_ON_RESUME`
- `scripts/run-cutoff-canary.sh target L3`: passed with app-owned
  `CANARY_L3_FRAGMENT_ADD_OK`, `CANARY_L3_FRAGMENT_VIEW_OK`,
  `CANARY_L3_FRAGMENT_ON_RESUME`, `CANARY_L3_FRAGMENT_COMMIT_OK`,
  `CANARY_L3_OK`, `CANARY_L3_ON_START`, and `CANARY_L3_ON_RESUME`
- runtime hashes for the accepted `target L3` run:
  `dalvikvm=d020846653627d90429fbd88b8fc4b8029634389422c1fab1cfdf8a8c314b120`,
  `aosp-shim.dex=80557721467673a09cff28462707f8880afcb601ae885b3903c0b58b6212b65c`
- `scripts/run-cutoff-canary.sh target L3LOOKUP`: passed with app-owned
  `CANARY_L3_FRAGMENT_LOOKUP_OK`, proving `findFragmentById(...)`,
  `getFragments()`, and `findFragmentByTag(...)` after `commitNow()`
- `scripts/run-cutoff-canary.sh target L3IFACE`: passed with app-owned
  `CANARY_L3_FRAGMENT_INTERFACE_GET_OK`, proving app-dex
  `java.util.List.get(I)` dispatch on the `FragmentManager.getFragments()`
  result after `commitNow()`
- `scripts/run-cutoff-canary.sh target L4`: passed with app-owned
  `CANARY_L4_SAVEDSTATE_PROVIDER_OK`, `CANARY_L4_VIEWMODEL_OK`,
  `CANARY_L4_FRAGMENT_SAVEDSTATE_OK`, `CANARY_L4_FRAGMENT_VIEWMODEL_OK`,
  `CANARY_L4_FRAGMENT_ON_RESUME`, and `CANARY_L4_OK`
- `scripts/run-cutoff-canary.sh target L4STATE`: passed with app-owned
  `CANARY_L4STATE_REGISTRY_RESTORE_OK`,
  `CANARY_L4STATE_SAVEDSTATE_HANDLE_OK`,
  `CANARY_L4STATE_CREATION_EXTRAS_OK`,
  `CANARY_L4STATE_VIEWTREE_LIFECYCLE_OWNER_OK`,
  `CANARY_L4STATE_VIEWTREE_VIEWMODEL_OWNER_OK`,
  `CANARY_L4STATE_VIEWTREE_SAVEDSTATE_OWNER_OK`,
  `CANARY_L4STATE_FRAGMENT_REGISTRY_RESTORE_OK`,
  `CANARY_L4STATE_FRAGMENT_VIEWTREE_OK`, and `CANARY_L4STATE_OK`
- `scripts/run-cutoff-canary.sh target L4RECREATE`: passed with app-owned
  `CANARY_L4RECREATE_SAVE_STATE_OK`, `CANARY_L4RECREATE_ON_PAUSE`,
  `CANARY_L4RECREATE_ON_STOP`, `CANARY_L4RECREATE_ON_DESTROY`,
  `CANARY_L4RECREATE_NEW_INSTANCE_OK`,
  `CANARY_L4RECREATE_ON_CREATE_RESTORED_OK`,
  `CANARY_L4RECREATE_REGISTRY_RESTORED_OK`,
  `CANARY_L4RECREATE_ON_RESUME_RESTORED_OK`, and `CANARY_L4RECREATE_OK`
- `scripts/run-cutoff-canary.sh target L4WATRECREATE`: passed with the same
  app-owned recreate markers plus WAT-owned trace markers
  `CV canary WAT launch begin`, `CV WAT precreate savedstate returned`,
  `CV WAT recreate begin`, and `CV WAT recreate end`. This proves WAT-owned
  pre-`onCreate` saved-state readiness, save/pause/stop/destroy/relaunch/
  restore/resume for the cutoff canary with a fresh Activity instance and
  restored `SavedStateRegistry` state.
- `scripts/run-cutoff-canary.sh target L4WATFACTORY`: passed with WAT-owned
  factory trace markers `CV canary WAT factory manifest parsed` and
  `CV canary WAT factory set done`, plus app-owned factory markers
  `CANARY_L4FACTORY_CTOR_OK`, two
  `CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`, and two
  `CANARY_L4FACTORY_ACTIVITY_RETURNED_OK`. This proves canary manifest
  `android:appComponentFactory` discovery, WAT factory installation, and custom
  `instantiateActivity(...)` on initial launch and recreate.
- `scripts/run-cutoff-canary.sh target L4WATAPPFACTORY`: passed with the
  factory trace above plus `CV canary application manual skipped app factory`,
  `CV WAT app factory preactivity makeApplication begin`,
  `CV WAT instantiateApplication returned com.westlake.cutoffcanary.CanaryApp`,
  `CV WAT application onCreate returned`, and
  `CV WAT app factory preactivity makeApplication returned`. App-owned markers
  include
  `CANARY_L4APPFACTORY_INSTANTIATE_APPLICATION_OK`,
  `CANARY_L4APPFACTORY_DIRECT_CANARY_APP_OK`, and
  `CANARY_L4APPFACTORY_APPLICATION_RETURNED_OK` before the first
  `CANARY_L4FACTORY_INSTANTIATE_ACTIVITY_OK`. This proves WAT-owned
  pre-Activity `makeApplication()` and controlled custom
  `instantiateApplication(...)` ordering for the canary Application.
- `scripts/run-cutoff-canary.sh target L4WATAPPREFLECT`: passed with the same
  WAT-owned pre-Activity Application ordering plus app-owned markers
  `CANARY_L4APPREFLECT_SUPER_CALL`,
  `CANARY_L4APPREFLECT_CANARY_APP_CTOR_OK`,
  `CANARY_L4APPREFLECT_SUPER_RETURNED`, and
  `CANARY_L4APPREFLECT_APPLICATION_RETURNED_OK`. The raw log includes
  `PF301 strict factory application ctor call` and
  `PF301 strict factory application ctor returned`. This proves the controlled
  canary can construct its Application through the base
  `AppComponentFactory.instantiateApplication(...)` reflective constructor path
  without the direct canary constructor escape.
- runtime hashes for the accepted `target L4WATAPPREFLECT` run and the follow-up
  `target L4WATAPPFACTORY` / `target L4WATFACTORY` / `target L4WATRECREATE` /
  `target L4RECREATE` / `target L4STATE` / `target L4` /
  `target L3LOOKUP` / `target L3IFACE` regressions:
  `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`,
  `aosp-shim.dex=35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`,
  `cutoff-canary-debug.apk=cb167f3033c14ea3c2eecb40cff784319d5a5657d85f060c0b15905b8e1c4147`
- `scripts/run-cutoff-canary.sh` now treats the canary APK as runtime
  provenance: it compares the installed package hash to the local built APK,
  auto-installs when stale by default, and fails closed if the post-install hash
  still differs.
- hash-stamped evidence bundle:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/35a7e5693f1b65a94a756cbf8e646b61f7cb8228f9f959dc30405ff6d0260a5d`
- known caveat: `L3LOOKUP` is green after fixing the unresolved
  `BitVector::ClearAllBits()` sentinel in class linking. The current accepted
  runtime has no live `PFCUT` or `DEBUG: Thread` output in the accepted
  `L4WATAPPREFLECT`, `L4WATAPPFACTORY`, `L4WATFACTORY`, `L4WATRECREATE`,
  `L4RECREATE`, `L4STATE`, `L4`, `L3LOOKUP`, and `L3IFACE` artifacts, and
  phone sync runs
  `scripts/check-westlake-runtime-symbols.sh` to block unexpected strong
  unresolved helper symbols. App-facing `List.get(...)` is now proven by
  `L3IFACE`; the shim no longer uses concrete `ArrayList` typing for
  `FragmentManager.mAdded`, and `L3LOOKUP`/`L3IFACE`/`L4`/`L4STATE`/
  `L4RECREATE`/`L4WATRECREATE`/`L4WATFACTORY`/`L4WATAPPFACTORY`/
  `L4WATAPPREFLECT` remain green. `L4WATRECREATE`, `L4WATFACTORY`,
  `L4WATAPPFACTORY`, and `L4WATAPPREFLECT` are accepted as WAT convergence
  gates. The old WAT
  precreate skip is removed from the accepted
  trace, and WAT attach now routes through shim-owned `Activity.attach(...)`
  with `PF301 strict WAT attachActivity shim attach begin/done` in the raw log.
  Strict WAT Activity creation now goes through a default
  `AppComponentFactory.instantiateActivity(...)` path on initial launch and
  relaunch, and the new custom-factory canary goes through
  `CanaryAppComponentFactory.instantiateActivity(...)` twice with
  `PF301 strict Instr custom factory path returned` in the raw log. The new
  app-factory canary goes through controlled custom
  `CanaryAppComponentFactory.instantiateApplication(...)` and WAT-owned
  `Application.onCreate()` before Activity factory instantiation.
  `L4WATAPPREFLECT` additionally proves the base
  `AppComponentFactory.instantiateApplication(...)` constructor path after
  replacing the unsafe raw `cl.loadClass(...)` first step with the same safe
  class resolution strategy used by Activity construction.
  The former canary-specific `DataSourceHelper` skip has been reduced to a
  McDonald's package/class guard. Hilt-safe real APK factory selection,
  real-APK Hilt Application factory behavior, generic real-APK
  Application-before-Activity ordering, and McDonald's-ready real APK lifecycle
  convergence remain open.

Use the Windows platform-tools ADB server from WSL:

```bash
ADB_BIN=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe
ADB server=localhost:5037
Device serial=cfb7c9e3
Device model=ONEPLUS A6003
```

Do not reuse stale remote `ADB_HOST` values for this phone. Leave `ADB_HOST`
unset unless deliberately testing a non-local ADB server; the working path is
the default localhost server on port `5037`.

---

## Goal

Use an Android phone as a native ARM64 test platform for the Westlake engine. The phone provides real hardware (CPU, display, touch, SQLite, filesystem) while our engine runs as a self-contained ART runtime with the full Westlake shim layer.

This tests **95% of the Westlake codebase** at native speed — the only untested parts are OHOS-specific kernel/service behaviors (<5%).

---

## Why

QEMU ARM64 emulation adds ~100x overhead. ART's `InitNativeMethods` takes 30+ minutes on emulated ARM64 vs <1 second on native. An Android phone provides native ARM64 execution for free.

| Platform | ART startup | Frame rate | Display |
|----------|------------|------------|---------|
| x86_64 host | <1s | 60fps | No (stubs) |
| QEMU ARM64 | 30+ min (hangs) | untested | VNC (blocked) |
| Android phone | ~1-2s (projected) | 60fps (projected) | Real screen |

---

## Three phases

### Phase 1: CLI validation (Termux, no display)

**Effort:** 1 day
**Requires:** Any ARM64 Android phone, Termux from F-Droid, USB cable

Run dalvikvm as a command-line process in Termux. Validates ART runtime, Westlake Java classes, Activity lifecycle, and all non-display functionality.

```
Phone (Termux)
└── dalvikvm (11MB ARM64 static binary)
    ├── boot.art + boot.oat (AOT compiled)
    ├── core-oj.jar + core-libart.jar + core-icu4j.jar
    ├── aosp-shim.dex (Westlake 3400+ classes)
    ├── liboh_bridge.so (stub — log to stdout, no display)
    └── app.dex (MockDonalds or target APK's DEX)
```

**Setup:**
```bash
# On PC
adb push deploy-package/ /data/local/tmp/a2oh/
# Or via Termux SSH:
scp -r deploy-package/ phone:~/a2oh/

# On phone (Termux or adb shell)
cd /data/local/tmp/a2oh  # or ~/a2oh in Termux
chmod +x dalvikvm
export ANDROID_DATA=$PWD ANDROID_ROOT=$PWD
./dalvikvm \
  -Xbootclasspath:core-oj.jar:core-libart.jar:core-icu4j.jar:aosp-shim.dex \
  -Ximage:boot.art -Xverify:none \
  -Djava.library.path=$PWD \
  -classpath app.dex \
  com.example.mockdonalds.MockDonaldsApp
```

**What's tested:**
- [x] ART runtime boot with boot image on real ARM64
- [x] Class loading from DEX (boot classpath + app classpath)
- [x] System.loadLibrary → dlopen → JNI_OnLoad → RegisterNatives
- [x] Activity lifecycle: create → start → resume → pause → stop → destroy
- [x] View tree: measure → layout → draw pipeline
- [x] Intent extras, Bundle serialization
- [x] SharedPreferences (stub or file-backed)
- [x] SQLite database operations (stub)
- [x] BaseAdapter + ListView population
- [x] Canvas drawing API calls (no-op stubs, validates call chain)
- [x] ScrollView touch interception logic
- [x] DefaultTheme + drawable rendering calls
- [x] Activity navigation (startActivity, finish, onActivityResult)

**Success criteria:** Same output as x86_64 test:
```
[MockDonaldsApp] MenuActivity launched
[MockDonaldsApp] Creating surface 480x800
[MockDonaldsApp] Initial frame rendered
[MockDonaldsApp] Frame 600 activity=MenuActivity
```

**Not tested:** Display output, touch input, real persistence.

---

### Phase 2: Display + touch (Android host APK)

**Effort:** 3-5 days
**Requires:** Android Studio (or manual APK build), same phone

Build an Android APK that hosts the Westlake engine. The APK provides a `SurfaceView` for real pixel rendering and forwards touch events into the engine.

```
Android APK (WestlakeHost.apk)
├── WestlakeHostActivity.java
│   ├── SurfaceView (provides ANativeWindow for rendering)
│   ├── onTouchEvent() → forwards to Westlake engine
│   └── Thread: runs dalvikvm or loads engine via JNI
├── lib/arm64-v8a/
│   ├── liboh_bridge_android.so (Android-backed bridge)
│   └── libwestlake_engine.so (optional: engine as shared lib)
├── assets/
│   ├── boot.art, boot.oat, core-*.jar, aosp-shim.dex
│   └── app.dex (target application)
└── AndroidManifest.xml
```

**Android-backed liboh_bridge.so implementation:**

| OHBridge JNI method | Android NDK implementation |
|---|---|
| `surfaceCreate(w,h)` | `ANativeWindow_fromSurface()` from host SurfaceView |
| `surfaceFlush()` | `ANativeWindow_unlockAndPost()` |
| `surfaceGetCanvas()` | `ANativeWindow_lock()` → pixel buffer |
| `canvasDrawRect/Text/...` | Skia `SkCanvas` (link against phone's libskia or bundle our own) |
| `rdbStoreOpen(name)` | `sqlite3_open()` from `/system/lib64/libsqlite.so` |
| `rdbStoreQuery(sql)` | `sqlite3_prepare/step/finalize` |
| `preferencesOpen/Get/Put` | Read/write JSON file in app's `filesDir` |
| `logDebug/Info/Warn/Error` | `__android_log_print(ANDROID_LOG_DEBUG, tag, msg)` |
| `fontMeasureText` | FreeType `FT_Load_Char` + advance width |
| `fontGetMetrics` | FreeType `FT_Face->size->metrics` |
| `showToast` | JNI callback to host Activity → `Toast.makeText()` |
| Touch input | Host `onTouchEvent` → JNI → `OHBridge.dispatchTouchEvent` |

**What's newly tested:**
- [x] Real pixel rendering on phone screen
- [x] Real touch input → View.onTouchEvent → click handlers
- [x] Real SQLite database (create table, insert, query, update, delete)
- [x] Real file-backed SharedPreferences
- [x] Real font measurement and text rendering
- [x] Scroll physics with real finger gestures
- [x] Activity transition rendering (screen redraws on navigation)
- [x] Full MockDonalds user flow: browse menu → tap item → add to cart → checkout

**Success criteria:** User can interact with MockDonalds on the phone screen — tap menu items, scroll the list, add to cart, and complete checkout. All rendered by Westlake, not by Android's own View system.

---

### Phase 3: Run real APKs (APK compatibility testing)

**Effort:** Ongoing
**Requires:** Phase 2 complete

Extract `classes.dex` from real APKs and run them through the Westlake engine. This validates API coverage and identifies missing shim classes.

```bash
# Extract DEX from any APK
unzip SomeApp.apk classes.dex -d /tmp/
# Package as target app
cp /tmp/classes.dex assets/app.dex
# Launch through WestlakeHost
adb install WestlakeHost.apk
adb shell am start -n com.westlake.host/.WestlakeHostActivity \
  --es target_class "com.example.someapp.MainActivity"
```

**Target APK categories (by complexity):**

| Tier | App type | Example | Key APIs needed |
|------|----------|---------|-----------------|
| A | Calculator/converter | Simple Calculator | Button, TextView, LinearLayout, onClick |
| B | List-based apps | Todo list, Notes | ListView, SQLite, EditText, RecyclerView |
| C | Multi-activity | Settings app | Multiple Activities, PreferenceScreen, Intent |
| D | Network apps | RSS reader | HttpURLConnection, JSON parsing, AsyncTask |
| E | Complex UI | Social media | Fragment, ViewPager, RecyclerView, ImageView |

**Phase 3a (Tier A+B):** Simple apps with programmatic UI or basic XML layouts.

**Phase 3b (Tier C+D):** Requires XML layout inflation — needs `LayoutInflater` + compiled XML resource parser for `resources.arsc`. This is the **single biggest gap** to close for real APK compatibility.

**Phase 3c (Tier E):** Requires Fragments, RecyclerView, and other AndroidX/support library shims.

---

## Deployment package

All files needed for phone testing, built by Agent A:

```
deploy-package/            (~30MB)
├── dalvikvm               ARM64 static binary (11MB)
├── boot.art               Pre-compiled boot image (684KB)
├── boot.oat               AOT compiled code (10MB)
├── boot.vdex              Verification data (7KB)
├── arm64/                 ISA subdirectory (copies of boot*)
├── core-oj.jar            OpenJDK core classes (5MB)
├── core-libart.jar        ART-specific classes (660KB)
├── core-icu4j.jar         ICU4J Unicode/i18n (2.6MB)
├── aosp-shim.dex          Westlake shim layer (3.7MB)
├── liboh_bridge.so        JNI bridge (ARM64 shared lib)
├── libicu_jni.so          ICU JNI stubs
├── libjavacore.so         Core I/O native methods
├── libopenjdk.so          File/Math/ZipFile natives
├── app.dex                Target application
├── dalvik-cache/arm64/    Empty (ART creates cache here)
└── run.sh                 Launch script
```

**run.sh:**
```bash
#!/bin/sh
DIR=$(dirname "$0")
cd "$DIR"
export ANDROID_DATA="$DIR" ANDROID_ROOT="$DIR"
export LD_LIBRARY_PATH="$DIR"
chmod +x dalvikvm 2>/dev/null
./dalvikvm \
  -Xbootclasspath:core-oj.jar:core-libart.jar:core-icu4j.jar:aosp-shim.dex \
  -Ximage:boot.art \
  -Xverify:none \
  -Djava.library.path="$DIR" \
  -classpath app.dex \
  "${1:-com.example.mockdonalds.MockDonaldsApp}" \
  2>&1
```

---

## Test matrix

| Test case | Phase 1 (CLI) | Phase 2 (Display) | Phase 3 (APK) |
|-----------|:---:|:---:|:---:|
| ART boot + boot image | ✅ | ✅ | ✅ |
| Activity lifecycle | ✅ | ✅ | ✅ |
| View measure/layout/draw | ✅ (no pixels) | ✅ | ✅ |
| Canvas drawing | calls only | real pixels | real pixels |
| Touch events | — | ✅ | ✅ |
| SQLite CRUD | stubs | real | real |
| SharedPreferences | stubs | real | real |
| ListView + Adapter | ✅ | ✅ | ✅ |
| ScrollView scroll | logic only | real gestures | real gestures |
| Activity navigation | ✅ | ✅ | ✅ |
| Font rendering | stubs | real FreeType | real FreeType |
| XML layout inflation | — | — | Phase 3b |
| Network I/O | — | — | Phase 3c |
| Real APK DEX | — | — | ✅ |

---

## Hardware requirements

**Minimum:** Any ARM64 Android phone, Android 7+, 100MB free storage
**Recommended:** Android 10+, 4+ cores, 4GB+ RAM (for smooth Phase 2 rendering)
**Budget option:** Used Redmi/Samsung ~$30-50

No root required for any phase. Phase 1 needs only Termux or USB debugging. Phase 2 needs the WestlakeHost APK installed (sideload, no Play Store needed).

---

## Relationship to OHOS QEMU testing

| Concern | Android phone | OHOS QEMU |
|---------|:---:|:---:|
| Westlake Java layer | ✅ Primary | Secondary |
| ART runtime correctness | ✅ Native ARM64 | ❌ Too slow |
| Real display output | ✅ Phone screen | ⏳ VNC (blocked) |
| Real touch input | ✅ Touchscreen | ⏳ virtio-tablet |
| OHOS kernel behavior | ❌ | ✅ |
| OHOS init/services | ❌ | ✅ |
| OHOS ArkUI integration | ❌ | Future P2 |
| Performance benchmarking | ✅ Real hardware | ❌ Emulated |

**Strategy:** Develop and validate on Android phone (fast iteration), final integration test on OHOS QEMU or real OHOS hardware.
