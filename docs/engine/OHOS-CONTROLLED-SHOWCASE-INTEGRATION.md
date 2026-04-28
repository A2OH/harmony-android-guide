# Westlake Controlled Showcase OHOS Integration Guide

Last updated: 2026-04-27

Detailed Yelp live OHOS handoff:
`docs/engine/OHOS-YELP-LIVE-PORTING-GUIDE.md`.

## Supervisor Judgement

The controlled showcase app and the separate Yelp live app are in good enough
shape to be the first OHOS port targets, but not to claim generic Android app
runtime completion.

It is the right target for the OHOS path because:

- the APK is self-contained and owned by us
- the API surface is known and can be expanded deliberately
- there is no GMS, Play Services, Maps, push, auth, payments, or remote backend
  dependency
- phone evidence already proves APK dex execution through Westlake `dalvikvm`,
  compiled XML inflation, View tree construction, layout probing, visible DLST
  rendering, touch-driven navigation, and app-owned markers
- the separate Yelp live app now also proves compiled XML inflation/binding
  before the live data flows, and the Material XML probe proves Material FQN XML
  tags inflate into Westlake-owned shim classes with a generic
  `findViewAt/performClick` hit into the APK listener
- the same controlled app can force exactly the OHOS bridge seams we need:
  surface, input, files, network, logging, lifecycle, and later audio/storage

It is not yet enough for arbitrary stock Android APK compatibility because:

- the accepted controlled showcase still reads as a Noice-style app, while the
  separate PF-453 APK carries the Yelp-like local-guide proof
- rendering is currently a showcase-specific direct DLST writer, not the
  generic `Activity.renderFrame`/`OHBridge.surfaceCreate` View draw path
- Yelp/showcase hit routing is still route-specific. The Material XML probe now
  proves a narrow generic `findViewAt/performClick` path, but not broad Android
  View hit testing for arbitrary widgets.
- `resources.arsc` table parsing still has an accepted workaround for this app
- live Java/libcore networking is not green. The selected production path,
  portable host/OHBridge HTTP, is now phone-proven on Android and must be
  implemented by the OHOS host adapter next.
- the separate `com.westlake.yelplive` APK now closes the prior "Noice shell
  looks like the wrong product" concern for the live-data proof with a
  polished list-first Yelp-like UI, but it still uses controlled direct DLST
  rendering and route-specific hit handling.

The practical conclusion: port this controlled app to OHOS first, but keep the
acceptance language narrow. The delivery claim should be "a controlled
self-contained Android app runtime running on OHOS through Westlake", not
"stock Android apps run on OHOS".

## Current Proven Package

Current accepted bridge-required phone proof on `cfb7c9e3` for the controlled
showcase:

- guest VM: `dalvikvm`
- guest shim: `aosp-shim.dex`
- guest APK: `westlake-showcase-debug.apk`
- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=b498750dce8e022c3e0a30c402ef652ec396d8b04cc2dc66e295ec6ddfbe3854`
- `westlake-showcase-debug.apk=bcd8d63eb2af3d2342110a5df97afd581cc3154d96d96c3de34306597ba5064d`
- app package: `com.westlake.showcase`
- app source: `test-apps/05-controlled-showcase/`
- run script: `scripts/run-controlled-showcase.sh`
- artifacts: `/mnt/c/Users/dspfa/TempWestlake/controlled_showcase_target.*`

Accepted markers include:

- `SHOWCASE_XML_INFLATE_OK`
- `SHOWCASE_XML_BIND_OK`
- `SHOWCASE_XML_LAYOUT_PROBE_OK`
- `SHOWCASE_XML_TREE_RENDER_OK`
- `SHOWCASE_DIRECT_FRAME_OK`
- `SHOWCASE_TOUCH_POLL_OK`
- `SHOWCASE_NAV_MIXER_OK`
- `SHOWCASE_NAV_TIMER_OK`
- `SHOWCASE_NAV_SETTINGS_OK`
- `SHOWCASE_NETWORK_HOST_BRIDGE_OK`
- `SHOWCASE_NETWORK_JSON_OK`
- `SHOWCASE_NETWORK_IMAGE_OK`
- `SHOWCASE_YELP_CARD_OK`
- `SHOWCASE_VENUE_NEXT_OK`
- `SHOWCASE_VENUE_REVIEW_OK`
- `SHOWCASE_EXPORT_BUNDLE_OK`

The accepted network path is now the portable host/OHBridge bridge:
`SHOWCASE_NETWORK_HOST_BRIDGE_OK status=200 bytes=417`,
`SHOWCASE_NETWORK_JSON_OK status=200 bytes=417 venues=3 transport=host_bridge`,
`SHOWCASE_NETWORK_HOST_BRIDGE_OK status=200 bytes=8090`, and
`SHOWCASE_NETWORK_IMAGE_OK status=200 bytes=8090 bitmap=100x100 transport=host_bridge`.
`SHOWCASE_NETWORK_NATIVE_GAP_OK` remains useful as a
negative-control marker, but it must not be present in the accepted
bridge-required run.

Current accepted separate Yelp live phone proof on `cfb7c9e3`:

- guest VM: `dalvikvm`
- guest shim: `aosp-shim.dex`
- guest APK: `westlake-yelp-live-debug.apk`
- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=0a30612bb9aaf7f644309950e280905839cdd7c94cf4fd16050b8826237c9164`
- `westlake-yelp-live-debug.apk=ad17d0a0adab5edacd017fc845a42b79629c9731a9ed5866fe03d30bcc08fcf1`
- app package: `com.westlake.yelplive`
- app source: `test-apps/06-yelp-live/`
- run script: `scripts/run-yelp-live.sh`
- artifacts: `/mnt/c/Users/dspfa/TempWestlake/yelp_live_target.*`
- stable accepted copy:
  `/mnt/c/Users/dspfa/TempWestlake/accepted/yelp_live/0a30612bb9aaf7f644309950e280905839cdd7c94cf4fd16050b8826237c9164_ad17d0a0adab5edacd017fc845a42b79629c9731a9ed5866fe03d30bcc08fcf1/`
- host render buffer: full phone `1080x2280` for the Yelp-like proof, logged as
  `Surface buffer 1080x2280 for com.westlake.yelplive`
- logical guest frame: `480x1013`, accepted by
  `YELP_FULL_RES_FRAME_OK logical=480x1013 target=1080x2280`

Accepted Yelp live markers include `YELP_ACTIVITY_ON_CREATE_OK`,
`YELP_XML_RESOURCE_WIRE_OK`, `YELP_XML_INFLATE_OK`, `YELP_XML_BIND_OK`,
`YELP_XML_LAYOUT_PROBE_OK`, `YELP_UI_BUILD_OK`, `YELP_NETWORK_BRIDGE_OK`,
`YELP_LIVE_JSON_OK`,
`YELP_LIVE_IMAGE_OK`, `YELP_CARD_OK`, `YELP_CATEGORY_SELECT_OK`,
`YELP_FILTER_TOGGLE_OK`, `YELP_NEXT_PLACE_OK`, `YELP_DETAILS_OPEN_OK`,
`YELP_SAVE_PLACE_OK`, `YELP_NAV_SAVED_OK`, `YELP_NAV_SEARCH_OK`,
`YELP_DIRECT_FRAME_OK`, and `YELP_FULL_RES_FRAME_OK`.

Current accepted Material XML probe on `cfb7c9e3`:

- guest VM: `dalvikvm`
- guest shim: `aosp-shim.dex`
- guest APK: `westlake-material-xml-probe-debug.apk`
- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=bf33aba0a8923e8b7d2cb006ee98042bb217021236a7cfe185a004f0e269716a`
- `westlake-material-xml-probe-debug.apk=ded93614084cdd28a46bcbcbd7eb8cba78504c3c228e0f95835a6ebf42a6e6c9`
- app package: `com.westlake.materialxmlprobe`
- app source: `test-apps/09-material-xml-probe/`
- run script: `scripts/run-material-xml-probe.sh`
- artifacts: `/mnt/c/Users/dspfa/TempWestlake/material_xml_probe_target.*`

Accepted Material XML markers include `MATERIAL_XML_INFLATE_BEGIN`,
`MATERIAL_XML_TAG_OK` for the controlled Material shim tags,
`MATERIAL_XML_TREE_OK materialViews=9`, `MATERIAL_GENERIC_RENDER_OK`,
`MATERIAL_GENERIC_BUTTON_BOUNDS`, `MATERIAL_GENERIC_TOUCH_OK`, and
`MATERIAL_GENERIC_HIT_OK`.

PF-453 is Yelp-like but not an official Yelp API integration. The accepted
phone proof uses `dummyjson.com` and `picsum.photos` because they provide a
public no-key live feed and bounded remote image bytes for the portable bridge proof.
The Android runner also checks the screenshot for a high-entropy photo region;
the OHOS runner should keep the same visual condition and row-level list
selection/save markers so block-only or single-card rendering does not pass.

## Runtime Shape

Android phone validation currently uses Android ART only as the host launcher
and device shell. The app under test is not executed by stock phone ART as a
normal installed Android app. The intended path is:

```text
Android host Activity
  -> launches /data/local/tmp/westlake/dalvikvm
  -> loads aosp-shim.dex
  -> loads westlake-showcase-debug.apk or westlake-yelp-live-debug.apk dex/resources
  -> guest Activity + shim Android APIs
  -> DLST frame output + marker files + touch input file
```

The OHOS port replaces the Android host Activity with an OHOS Ability and
native module. The Westlake guest contract should remain the same:

```text
OHOS Stage Ability / XComponent
  -> launches or embeds Westlake dalvikvm
  -> loads aosp-shim.dex
  -> loads westlake-showcase-debug.apk or westlake-yelp-live-debug.apk dex/resources
  -> guest Activity + shim Android APIs
  -> OHOS surface/input/files/network/logging adapters
```

Do not kill or replace the phone's ART during Android validation. The phone ART
is only the control host. On OHOS, that host is naturally replaced by the
Ability/XComponent layer.

## Payload Contract

The OHOS package must include or install:

- `dalvikvm` built for the OHOS target ABI
- any required ART boot/runtime files for that `dalvikvm`
- `aosp-shim.dex`
- `westlake-showcase-debug.apk`
- `westlake-yelp-live-debug.apk` when running the separate live-data proof
- `westlake-material-xml-probe-debug.apk` when running the Material XML/hit
  proof
- extracted resource directory when required by the current loader
- launch properties file
- marker/output directory
- optional HTTP bridge spool directory

Minimum launch properties:

```properties
westlake.apk.path=/path/to/westlake-showcase-debug.apk
westlake.apk.package=com.westlake.showcase
westlake.apk.activity=com.westlake.showcase.ShowcaseActivity
westlake.apk.resdir=/path/to/extracted/res
westlake.apk.manifest=/path/to/AndroidManifest.xml
westlake.backend.mode=target_ohos_backend
westlake.marker.dir=/path/to/markers
westlake.frame.path=/path/to/frame.dlst
westlake.touch.path=/path/to/touch.dat
westlake.bridge.dir=/path/to/bridge
```

For the separate Yelp live app, change the app properties to:

```properties
westlake.apk.path=/path/to/westlake-yelp-live-debug.apk
westlake.apk.package=com.westlake.yelplive
westlake.apk.activity=com.westlake.yelplive.YelpLiveActivity
```

For the Material XML probe, change the app properties to:

```properties
westlake.apk.path=/path/to/westlake-material-xml-probe-debug.apk
westlake.apk.package=com.westlake.materialxmlprobe
westlake.apk.activity=com.westlake.materialxmlprobe.MaterialXmlProbeActivity
```

The exact file transport can change on OHOS, but the logical properties should
stay stable until the host bridge is promoted from files to direct native calls.

## OHOS API Connections

These are the OHOS seams the port should wire first. The header paths were
checked in the local OpenHarmony SDK tree under `interface/sdk_c`.

| Westlake seam | OHOS API to connect | Local SDK evidence | First acceptance |
| --- | --- | --- | --- |
| App shell and lifecycle | Stage Ability hosts a native module; native exports use N-API | `arkui/napi/native_api.h` | Ability starts/stops Westlake and preserves marker/frame dirs |
| Display host | `OH_NativeXComponent` for surface lifecycle and callbacks | `arkui/ace_engine/native/native_interface_xcomponent.h` | surface create/resize/destroy reaches Westlake backend |
| Key/touch input | XComponent touch/key callbacks | `arkui/ace_engine/native/native_interface_xcomponent.h`, `arkui/ace_engine/native/native_xcomponent_key_event.h` | real touch drives `SHOWCASE_NAV_*` and venue action markers |
| Window buffers | `OH_NativeWindow` buffer access | `graphic/graphic_2d/native_window/external_window.h` | DLST or raster output appears in the XComponent window |
| 2D drawing | Native Drawing canvas/paint/path/text APIs | `graphic/graphic_2d/native_drawing/drawing_canvas.h`, `drawing_pen.h`, `drawing_brush.h`, `drawing_font.h`, `drawing_path.h`, `drawing_bitmap.h`, `drawing_color.h` | direct DLST replay can draw text/cards/buttons on OHOS |
| Image decode | Image source and PixelMap APIs, or Westlake-owned decoder | `multimedia/image_framework/include/image_source_mdk.h`, `image_pixel_map_napi.h` | venue preview image decodes and renders |
| Network reachability/DNS/TLS | Host/OHBridge HTTP adapter using OHOS network stack; NetManager and SSL C APIs are available primitives | `network/netmanager/include/net_connection.h`, `net_connection_type.h`, `network/netssl/include/net_ssl_c.h` | `SHOWCASE_NETWORK_HOST_BRIDGE_OK` replaces native gap marker |
| Resource files | RawFile manager for packaged assets, plus app sandbox paths for copied runtime payloads | `global/resource_management/include/raw_file.h`, `raw_file_manager.h`, `raw_dir.h` | APK/shim/runtime payloads load from OHOS app-owned storage |
| Logging | HiLog | `hiviewdfx/hilog/include/hilog/log.h` | Westlake markers are mirrored to OHOS logs |
| Local persistence | App sandbox files first; RDB only if needed later | `distributeddatamgr/relational_store/include/relational_store.h` | settings/export state survives restart |
| Audio, later | OHAudio renderer when the Noice-style audio layer becomes real audio output | `multimedia/audio_framework/audio_renderer/native_audiorenderer.h` | audio playback marker plus audible output |
| Secure storage, later | HUKS or Asset APIs for credentials/tokens if future apps need them | `security/huks/include/native_huks_api.h`, `security/asset/inc/asset_api.h` | not required for controlled showcase |

Network note: NetManager exposes network state, DNS, and related primitives; it
is not the app-facing contract. The Westlake-facing contract should be a small
HTTP request API on the OHBridge boundary. The OHOS implementation can be
ArkTS-side HTTP, native network/TLS, or another approved platform HTTP client,
as long as the guest sees the same `westlakeHttpRequest` semantics and the same
markers on Android phone and OHOS. For the Yelp live app, the equivalent OHOS
markers are `YELP_NETWORK_BRIDGE_OK`, `YELP_LIVE_JSON_OK`, and
`YELP_LIVE_IMAGE_OK`.

## Recommended First OHOS Implementation

1. Package the known-good `dalvikvm`, `aosp-shim.dex`, showcase APK, extracted
   resources, and launch properties into the OHOS app sandbox.
2. Create an OHOS Stage Ability that owns an XComponent and starts Westlake.
3. Implement the display backend as DLST replay into the XComponent first.
   This is the fastest port because the phone proof already emits DLST.
4. Feed XComponent touch events into the existing Westlake touch protocol, then
   replace the file protocol with direct native event enqueue after the first
   proof is stable.
5. Implement the same host/OHBridge HTTP bridge on OHOS. The Android phone host
   implementation is accepted; the OHOS adapter must expose the same
   `westlake.bridge.dir` request/response semantics first, then can be promoted
   to direct native calls.
6. Mirror Westlake markers to HiLog and keep marker files as machine-readable
   acceptance evidence.
7. Only after the above passes, replace direct DLST replay with the generic
   `OHBridge.surfaceCreate`/View drawing path.

The Material XML probe should be carried to OHOS as an early UI-compatibility
gate after the Yelp live proof. It is smaller than the Yelp app and gives a
clear signal that compiled XML tags, Material shim class creation, DLST tree
serialization, and touch routing are all connected.

This order keeps the OHOS proof focused on platform seams and avoids blocking
on generic Android rendering completeness before we have a visible app on OHOS.

## Acceptance Gate For The OHOS Port

The first OHOS run is acceptable when the controlled showcase records:

- APK hash and shim hash used for the run
- Westlake process started by the OHOS host, not Android phone ART
- `SHOWCASE_XML_INFLATE_OK`
- `SHOWCASE_XML_LAYOUT_PROBE_OK`
- `SHOWCASE_DIRECT_FRAME_OK` or the generic replacement marker
- `SHOWCASE_TOUCH_POLL_OK` or the generic OHOS input marker
- at least three navigation/action markers from real OHOS input
- `SHOWCASE_NETWORK_HOST_BRIDGE_OK`
- `SHOWCASE_NETWORK_JSON_OK`
- `SHOWCASE_NETWORK_IMAGE_OK`
- no `SHOWCASE_NETWORK_NATIVE_GAP_OK` in the accepted live-network run
- screenshot or framebuffer proof showing the local guide UI on OHOS

The separate Yelp live app is acceptable on OHOS when it records the same hash
evidence plus `YELP_ACTIVITY_ON_CREATE_OK`, `YELP_UI_BUILD_OK`,
`YELP_NETWORK_BRIDGE_OK`, `YELP_LIVE_JSON_OK`, `YELP_LIVE_IMAGE_OK`,
`YELP_CARD_OK`, and at least three of `YELP_NEXT_PLACE_OK`,
`YELP_DETAILS_OPEN_OK`, `YELP_SAVE_PLACE_OK`, `YELP_NAV_SAVED_OK`, and
`YELP_NAV_SEARCH_OK`, with screenshot or framebuffer proof showing the Yelp-like
live-data UI.

The Material XML probe is acceptable on OHOS when it records
`MATERIAL_XML_TREE_OK`, `MATERIAL_GENERIC_RENDER_OK`,
`MATERIAL_GENERIC_TOUCH_OK`, and `MATERIAL_GENERIC_HIT_OK` with framebuffer
proof of the Material XML tree.

## Open Gaps Before Calling It "Good"

P0 gaps for the controlled OHOS port:

- Port the accepted Android host/OHBridge HTTP bridge adapter to OHOS and keep
  the same `SHOWCASE_NETWORK_HOST_BRIDGE_OK` and `YELP_NETWORK_BRIDGE_OK`
  acceptance markers.
- Build the OHOS XComponent host and DLST replay backend.
- Convert touch from Android host polling to the OHOS XComponent event path.
- Run both `com.westlake.showcase` and `com.westlake.yelplive` through the same
  OHOS host seams so the platform boundary is proven across a local XML-heavy
  app and a separate live-data app.
- Run `com.westlake.materialxmlprobe` through the same OHOS host seams to guard
  compiled Material XML inflation and generic button hit routing.

P1 gaps after first OHOS visual proof:

- Promote direct DLST replay to generic `OHBridge.surfaceCreate` rendering.
- Replace showcase/Yelp-specific hit routing with generic View hit testing.
  Keep the Material XML probe's `findViewAt/performClick` path as the first
  narrow acceptance gate, not the final generic input claim.
- Fix `resources.arsc` parsing instead of direct layout registration.
- Add list/scroll density closer to real production apps.
- Add persistence and audio output if the app remains Noice-like.

P2 gaps for broader Android app runtime claims:

- Broaden AppCompat/RecyclerView/theme/drawable coverage.
- Expand Activity stack, back navigation, and saved-state restore.
- Add permissions, content provider, service, and broadcast semantics.
- Decide whether Java/libcore networking is still needed after the bridge path
  is accepted.
