# Westlake Yelp Live OHOS Porting Guide

Last updated: 2026-04-27

This document is the handoff contract for agents porting the controlled
Yelp-like Android APK proof to OHOS.

## Current Status

The controlled Yelp-like app is a good OHOS port target. It is not yet proof
that arbitrary stock Android APKs or upstream Google Material Components run on
Westlake.

Accepted Android phone proof:

- device: `cfb7c9e3`
- phone resolution: `1080x2280`
- host package/activity: `com.westlake.host/.WestlakeActivity`
- guest app package: `com.westlake.yelplive`
- guest app source: `test-apps/06-yelp-live/`
- Android runner: `scripts/run-yelp-live.sh`
- runtime dir: `/data/local/tmp/westlake`
- artifacts: `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.*`
- accepted copy:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/yelp_live/eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d_f60f2d8b8b91592aec2e96329da9fbd44f332b535d506e624a5073e37a1122d9/`

Accepted hashes:

```text
dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e
aosp-shim.dex=eab847a8ef6108a6c24118ad9349a2aebb74e5e7f837edfc4cb5d0f92a30535d
westlake-yelp-live-debug.apk=f60f2d8b8b91592aec2e96329da9fbd44f332b535d506e624a5073e37a1122d9
```

Required acceptance markers:

```text
YELP_XML_RESOURCE_WIRE_OK
YELP_XML_INFLATE_OK root=android.widget.ScrollView views=30 texts=21
YELP_XML_BIND_OK title=true status=true card=true list=true buttons=5
YELP_XML_LAYOUT_PROBE_OK target=480x1013 measured=480x1013 bounds=0,0,480,1013
YELP_UI_BUILD_OK surface=xml tabs=4 network=host_bridge views=30 texts=21
YELP_FULL_RES_FRAME_OK logical=480x1013 target=1080x2280 navTop=824
YELP_GENERIC_VIEW_DRAW_OK views=27 texts=17 buttons=13 images=0 lists=1 listRows=5 listImages=5 height=1013 source=inflated_xml
YELP_GENERIC_LIST_DRAW_OK rows=5 images=5 source=inflated_xml
YELP_GENERIC_VISIBLE_LIST_OK rows=5 images=5 surface=direct_composite source=inflated_xml
YELP_GENERIC_HIT_OK clicked=true target=android.widget.Button text=Search source=inflated_xml
YELP_GENERIC_HIT_OK clicked=true target=android.widget.Button text=Details source=inflated_xml
YELP_GENERIC_HIT_OK clicked=true target=android.widget.Button text=Saved source=inflated_xml
YELP_GENERIC_SCROLL_OK container=android.widget.ScrollView source=inflated_xml
YELP_ADAPTER_ATTACH_OK class=android.widget.ListView adapter=...VenueAdapter
YELP_ADAPTER_BIND_PROBE_OK rows=5
YELP_ADAPTER_NOTIFY_OK images=5
YELP_ADAPTER_IMAGE_REBIND_OK index=4
YELP_ADAPTER_IMAGE_BIND_OK position=4 bitmap=false imageView=true
YELP_GENERIC_ADAPTER_ITEM_CLICK_OK clicked=true position=2 source=inflated_xml
YELP_ADAPTER_ITEM_CLICK_OK position=2
YELP_VISUAL_DELTA_V4_OK surface=adapter_feed adapterBadge=true visibleImages=5
YELP_NETWORK_BRIDGE_OK
YELP_LIVE_JSON_OK
YELP_LIVE_IMAGE_OK
YELP_LIVE_ROW_IMAGE_OK index=4
YELP_LIST_SCROLL_OK
YELP_DETAILS_OPEN_OK
YELP_SAVE_PLACE_OK
YELP_NAV_SEARCH_OK
YELP_REST_MATRIX_OK
YELP_REST_POST_OK
YELP_REST_HEADERS_OK
YELP_REST_METHODS_OK
YELP_REST_HEAD_OK
YELP_REST_STATUS_OK status=418
YELP_REST_REDIRECT_OK
YELP_REST_TRUNCATE_OK
YELP_REST_TIMEOUT_OK
YELP_REST_MATRIX_SYNTHETIC_OK
YELP_REST_TIMEOUT_SYNTHETIC_OK
```

The host log must include:

```text
Surface buffer 1080x2280 for com.westlake.yelplive
```

## Android Reproduction

Build the runtime shim:

```bash
./scripts/build-shim-dex.sh
```

Build the controlled APK:

```bash
./test-apps/06-yelp-live/build-apk.sh
```

Build and install the Android host when `WestlakeVM.kt` changed:

```bash
(cd westlake-host-gradle && ./gradlew :app:assembleDebug)
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 install -r \
  westlake-host-gradle/app/build/outputs/apk/debug/app-debug.apk
```

Sync the phone runtime:

```bash
./scripts/sync-westlake-phone-runtime.sh
```

Run acceptance. The current phone has no active default network, so use the
supervisor HTTP proxy:

```bash
SUPERVISOR_HTTP_PROXY=1 \
WAIT_SECS=4 \
POST_SCROLL_WAIT_SECS=3 \
POST_ROW_WAIT_SECS=3 \
POST_SAVE_WAIT_SECS=3 \
./scripts/run-yelp-live.sh
```

## Runtime Call Path

The controlled app does go through APK resource wiring and XML inflation.

```text
Android WestlakeActivity
  -> WestlakeVMApkScreen(config=com.westlake.yelplive)
  -> SurfaceView fixed buffer 1080x2280
  -> /data/local/tmp/westlake/dalvikvm
  -> com.westlake.engine.WestlakeLauncher
  -> loads installed westlake-yelp-live-debug.apk
  -> wires APK path, resources.arsc, and res/layout/yelp_live_activity.xml
  -> guest YelpLiveActivity.onCreate()
  -> LayoutInflater inflates yelp_live_activity.xml
  -> app binds title/status/card/list/button IDs
  -> app performs layout probe at 480x1013
  -> guest state changes from touch and network callbacks
  -> Westlake direct DLST frame writer emits full-height logical frame
  -> host replays DLST onto the 1080x2280 SurfaceView
```

Touch is file-backed during Android validation:

```text
host pointer input
  -> maps physical 1080x2280 into logical 480x1013
  -> writes westlake_touch.dat
  -> guest polling loop reads action/x/y/seq
  -> routeYelpLiveDirectTouch(...)
  -> app methods: category, filters, list scroll, details, save, nav
```

PF-460 adds narrow generic XML listener and scroll-container proof inside that
same run:

```text
direct-frame Search tap at logical x=175 y=972
  -> guest loop recognizes the tap is already targeting Search
  -> traverses the inflated XML decor tree for Button text "Search"
  -> invokes Button.performClick()
  -> YelpLiveActivity yelp_search listener calls navigateSearch()
  -> records YELP_GENERIC_HIT_OK ... target=android.widget.Button
     text=Search source=inflated_xml

direct-frame row/details/save-equivalent taps
  -> invoke inflated XML Button.performClick() for "Details" and "Saved"
  -> records matching YELP_GENERIC_HIT_OK markers

direct-frame swipe
  -> finds the inflated android.widget.ScrollView
  -> calls scrollBy(...)
  -> records YELP_GENERIC_SCROLL_OK
```

This is intentionally non-disruptive: it proves the inflated XML listener path
without stealing category/network/list interactions from the existing accepted
Yelp flow.

Network is host-bridge backed:

```text
YelpLiveActivity fetch
  -> WestlakeLauncher.bridgeHttpGetBytes(...)
  -> host bridge spool request
  -> Android host URLConnection or supervisor proxy
  -> response body/meta files
  -> guest parses JSON and image bytes

YelpLiveActivity REST matrix
  -> WestlakeLauncher.bridgeHttpRequest(...)
  -> POST/PUT/PATCH/DELETE/HEAD/GET requests with headers/body/caps/timeouts
  -> host bridge spool request
  -> Android host URLConnection or supervisor proxy
  -> response status/body/headers/final URL/truncation/error metadata
  -> guest records YELP_REST_* acceptance markers
```

Current networking caveat: the accepted Android run proves real live GET JSON
and five real image GETs through the host bridge. The multi-method REST matrix
markers are currently synthetic bridge-contract markers because the real matrix
path still hits a VM SIGBUS. OHOS agents should implement the bridge seam, but
they must not treat the synthetic matrix as proof that stock
`HttpURLConnection`, raw sockets, DNS, or all REST methods are complete.

## OHOS Porting Contract

On OHOS, replace the Android host shell while preserving the guest contract.

```text
OHOS Stage Ability / XComponent
  -> launches or embeds Westlake dalvikvm
  -> supplies aosp-shim.dex and westlake-yelp-live-debug.apk
  -> creates a 1080x2280-equivalent surface or reports the real surface size
  -> maps input into logical 480x1013 coordinates
  -> replays DLST onto the OHOS drawing/window APIs
  -> implements the host HTTP bridge using OHOS network APIs
  -> exposes marker, trace, and screenshot artifacts
```

Minimum OHOS adapter seams:

| Westlake seam | OHOS side |
| --- | --- |
| Ability lifecycle | Start/stop Westlake VM, allocate runtime dirs, collect markers |
| Surface | `OH_NativeXComponent` and `OH_NativeWindow` |
| Drawing | Native Drawing canvas/brush/pen/font/path/bitmap APIs or a compatible raster layer |
| Touch/key | XComponent touch/key callbacks mapped to logical guest coordinates |
| File bridge | Marker, trace, frame, touch, and HTTP spool paths |
| HTTP bridge | GET/POST/etc. over OHOS network stack with status/body/header metadata |
| Image bytes | Decode remote images to drawable pixels or pass compressed image bytes to the DLST image op |
| Logging | Mirror `YELP_*` markers and host surface logs to a pullable artifact dir |

The first OHOS acceptance should require the same markers as Android plus an
OHOS host marker proving the XComponent surface size. Do not accept a run with
black bars, block-only cards, a single result row, missing `index=4` row image,
or missing scroll/details/save markers.

## Material / UI Reality

The current visible Yelp UI is not generic Android widget drawing and not full
Google Material Components. It is a controlled direct DLST renderer with
Material-like styling.

Accepted:

- compiled XML resource wiring
- XML inflation into shim views
- ID binding and layout probing
- generic inflated-View DLST serialization slice over the Yelp XML tree
- actual `ScrollView` tag inflation
- generic inflated XML `Button.performClick()` listener slices for Search,
  Details, and Saved
- generic `ScrollView` discovery/probe marker
- XML `ListView` backed by a guest `BaseAdapter`
- five live row-image byte rebinds into `ImageView` row slots
- generic `ListView.performItemClick()` into the APK row listener
- phone-visible adapter-feed ribbon with screenshot gate
  `adapter_teal_samples=697`
- full phone-height DLST rendering
- touch-driven app state
- live host-bridge JSON and images

Not accepted yet:

- full-fidelity generic `View.draw(Canvas)` replacement for the visible Yelp
  frame
- broad generic coordinate hit dispatch and visible scroll-container routing
- raw `Bitmap.createFromImageData` / Bitmap-backed `ImageView` decode without
  SIGBUS
- real multi-method REST matrix execution instead of synthetic bridge-contract
  markers
- upstream Material Components AAR compatibility
- Material theming and shape/ripple/animation fidelity
- `CoordinatorLayout` / AppBar / nested scroll behavior
- broad generic Android hit testing

## Enhancement Plan Toward McDonald's-Class APKs

The controlled Yelp app should become a ladder of southbound shim requirements.
Each step adds one stock-app-shaped capability and a phone/OHOS acceptance
marker before moving to the next.

1. REST matrix probe
   - Android phone status: accepted for real live GET JSON/image traffic and
     the Yelp bridge v2 marker contract.
   - Proven surface: real GET JSON and image byte downloads through the bridge;
     POST/PUT/PATCH/DELETE/HEAD, headers, body upload, non-2xx body, redirect,
     timeout, truncation, response headers, and structured error metadata are
     represented by the current marker contract.
   - Required markers: `YELP_REST_POST_OK`, `YELP_REST_HEADERS_OK`,
     `YELP_REST_METHODS_OK`, `YELP_REST_HEAD_OK`, `YELP_REST_STATUS_OK`,
     `YELP_REST_REDIRECT_OK`, `YELP_REST_TIMEOUT_OK`,
     `YELP_REST_TRUNCATE_OK`, and `YELP_REST_MATRIX_OK`.
   - Remaining work: replace synthetic matrix markers with real multi-method
     request execution, then implement the same host adapter contract on OHOS
     and repeat this marker set with `transport=ohos_bridge` or equivalent host
     evidence.
   - McDonald's relevance: menu/config/auth bootstrap APIs.

2. Generic widget render slice
   - Android phone status: accepted first slice for serializing the inflated
     `yelp_live_activity.xml` tree into DLST with `27` traversed views,
     `17` text widgets, `13` buttons, `1` list, `5` list rows, `5` live
     list-image byte slots, and logical height `1013`.
   - Required marker: `YELP_GENERIC_VIEW_DRAW_OK`.
   - Remaining work: make this the full-fidelity visible renderer, including
     image/background fidelity and replacement of the Yelp-specific frame
     writer.
   - McDonald's relevance: stock layouts must paint without per-app renderers.

3. Generic hit testing and scroll containers
   - Android phone status: non-disruptive inflated XML
     `Button.performClick()` listener slices accepted for Search, Details, and
     Saved, plus real `ScrollView` inflation and `YELP_GENERIC_SCROLL_OK`;
     broad routing remains open.
   - Replace route-specific touch handling with View tree hit testing,
     `performClick()`, scroll gestures, and pressed state.
   - Required markers: `YELP_GENERIC_HIT_OK`, `YELP_GENERIC_SCROLL_OK`.
   - McDonald's relevance: dashboard buttons, carousels, tabs, and RecyclerViews.

4. Adapter/list virtualization
   - Android phone status: accepted first `ListView`/`BaseAdapter` slice with
     five visible rows, stable item IDs, five live row-image byte rebinds, and
     a generic adapter item click into the APK listener.
   - Required markers: `YELP_ADAPTER_ATTACH_OK`,
     `YELP_ADAPTER_BIND_PROBE_OK`, `YELP_ADAPTER_NOTIFY_OK images=5`,
     `YELP_ADAPTER_IMAGE_BIND_OK position=4`,
     `YELP_GENERIC_ADAPTER_ITEM_CLICK_OK`, `YELP_ADAPTER_ITEM_CLICK_OK`, and
     `YELP_VISUAL_DELTA_V4_OK`.
   - Remaining work: RecyclerView-equivalent virtualization, raw Bitmap-backed
     row images, robust visible generic list scrolling, and OHOS adapter
     parity.
   - McDonald's relevance: menu grids, offers lists, order history.

5. Material compatibility ladder
   - Move from handcrafted Material-like DLST to a small upstream-compatible
     Material shim surface: `MaterialButton`, `MaterialCardView`, chips,
     text fields, bottom nav, shape drawables, and ripple fallback.
   - Required markers: `YELP_MATERIAL_XML_DRAW_OK`,
     `YELP_MATERIAL_STATE_OK`.
   - McDonald's relevance: modern app UI stacks often depend on Material and
     AppCompat contracts.

6. Lifecycle and state stress
   - Add recreate, saved-state restore, ViewModel, back stack, orientation-like
     resize, and process restart simulations.
   - Required markers: `YELP_RECREATE_RESTORE_OK`,
     `YELP_BACKSTACK_OK`.
   - McDonald's relevance: real dashboard startup survives lifecycle churn.

7. Storage and cache APIs
   - Add image disk cache, preferences, small database or file-backed saved
     restaurants, and cache eviction.
   - Required markers: `YELP_PREFS_OK`, `YELP_CACHE_RESTORE_OK`.
   - McDonald's relevance: config, session, menu cache, and user state.

8. Media and device service probes
   - Add vibration/haptic fallback, accessibility labels, clipboard/text input,
     locale, time zone, and network connectivity callbacks.
   - Required markers: `YELP_INPUT_TEXT_OK`, `YELP_CONNECTIVITY_CALLBACK_OK`.
   - McDonald's relevance: stock APK service lookups must not crash.

9. Web/image/render edge cases
   - Add SVG/vector drawable, nine-patch-like stretch, alpha layers, clipping,
     text ellipsize, custom fonts, and large images.
   - Required markers: `YELP_VECTOR_OK`, `YELP_TEXT_ELLIPSIZE_OK`,
     `YELP_LARGE_IMAGE_OK`.
   - McDonald's relevance: splash/dashboard assets and branded UI.

10. McDonald's preflight bridge
    - Reuse the controlled app to mimic McDonald's launch contracts:
      AppComponentFactory/Application-before-Activity, Hilt-safe construction,
      AppCompat/Fragment lifecycle, network config fetch, list/dashboard draw,
      and touch navigation.
    - Required marker: `YELP_MCD_PREFLIGHT_OK`.
    - Only after this marker should work move back to the stock McDonald's APK
      path as the primary validation target.

## Ready / Not Ready Decision

Ready for OHOS port attempt:

- controlled APK packaging
- resource and XML inflation path
- full-height phone rendering contract
- live network bridge contract
- touch/navigation/save/list proof
- XML `ListView`/`BaseAdapter` row and image-rebind proof
- marker-driven acceptance

Not ready for final OHOS delivery claim:

- OHOS surface/input/network adapters are not implemented in-tree
- generic widget drawing is not complete
- generic touch/scroll routing is only accepted for limited XML button listeners
  and a controlled `ScrollView` scroll probe
- raw Bitmap/ImageView decode and real multi-method REST execution still have
  known gaps
- RecyclerView-class list virtualization is not complete
- full Material Components are not complete
- stock McDonald's APK still needs the southbound shim ladder above
