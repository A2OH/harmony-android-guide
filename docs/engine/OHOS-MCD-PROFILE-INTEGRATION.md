# OHOS McD Profile Integration Guide

Last updated: 2026-04-28

## Purpose

`com.westlake.mcdprofile` is the current controlled mock McD-profile Westlake
port target. It is not the real McDonald's app. It exists to close stock-app
boundaries under a known API surface before returning to the real McDonald's
APK.

The app is intentionally self-contained:

- compiled APK XML: `activity_mcd_profile.xml`;
- app-owned `Application` and `Activity` classes;
- generic `WestlakeActivityThread` launch through `AppComponentFactory`;
- Material-style XML tags in the APK source, accepted through the current
  McD-profile XML traversal and ID-binding slice;
- direct-rendered five-row menu state plus guest `ListView` adapter binding;
- SharedPreferences cart state;
- host/OHBridge live JSON, image, POST, HEAD, and non-2xx REST traffic;
- full-phone `DLST` rendering and strict touch input.

It is not a stock APK compatibility claim. It is the first controlled
McD-class mock app that should be ported to OHOS because every southbound
contract is known and can be implemented one by one.

## Accepted Android Phone Proof

Run:

```bash
WAIT_SECS=24 ./scripts/run-mcd-profile.sh
```

Accepted device: `cfb7c9e3`.

Accepted hashes:

- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=7ec1a0e797b1c2459da46a827aad59eac8d418efff49e51d349f1e09b9647e21`
- `westlake-host.apk=b1e3e45d201d7ddf333bfa8e9d27c9588e5f02ca9070862876e7daf536d1e594`
- `westlake-mcd-profile-debug.apk=3c622253ab4a5fcea1ba0d3904103ac506df8b648ab10cfa1e59d74eb4987eb3`

Accepted artifacts:

- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.log`
- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.markers`
- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.trace`
- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.png`
- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.visual`
- `/mnt/c/Users/dspfa/TempWestlake/accepted/mcd_profile/7ec1a0e797b1c2459da46a827aad59eac8d418efff49e51d349f1e09b9647e21_3c622253ab4a5fcea1ba0d3904103ac506df8b648ab10cfa1e59d74eb4987eb3/`

Key accepted launch and XML markers:

- `MCD_PROFILE_GENERIC_ACTIVITY_FACTORY_OK class=com.westlake.mcdprofile.McdProfileActivity factory=default`
- `MCD_PROFILE_WAT_ACTIVITY_LAUNCH_OK class=com.westlake.mcdprofile.McdProfileActivity`
- `MCD_PROFILE_WAT_ACTIVITY_ONCREATE_OK class=com.westlake.mcdprofile.McdProfileActivity`
- `MCD_PROFILE_WAT_ACTIVITY_RESUME_OK class=com.westlake.mcdprofile.McdProfileActivity`
- `MCD_PROFILE_XML_RESOURCE_WIRE_OK engine=true table=true apk=true resDir=true arsc=2528 layouts=1 layoutBytes=4112`
- `MCD_PROFILE_XML_TAG_OK` for `TextInputLayout`, `TextInputEditText`,
  `ChipGroup`, `Chip`, `MaterialCardView`, `ImageView`, `MaterialButton`,
  `BottomNavigationView`, and `ListView`
- `MCD_PROFILE_XML_BIND_OK list=true ... materialViews=10`
- `MCD_PROFILE_ADAPTER_GET_VIEW_OK position=4`
- `MCD_PROFILE_XML_LAYOUT_PROBE_OK target=480x1013 measured=480x1013`
- `MCD_PROFILE_XML_INFLATE_OK ... views=25 materialViews=10 source=compiled_apk_xml`
- `MCD_PROFILE_REST_POST_OK status=200 bytes=100 protocol=2 transport=host_bridge`

## Call Path

The visible McD-profile screen is rendered by Westlake, not by the phone's
normal app Activity rendering path.

Current Android-host path:

```text
WestlakeActivity
  -> launch extra VM_APK:com.westlake.mcdprofile:com.westlake.mcdprofile.McdProfileActivity
  -> WestlakeVM stages target APK and aosp-shim.dex into the host private VM dir
  -> WestlakeVM starts /data/local/tmp/westlake/dalvikvm as a subprocess
  -> dalvikvm loads aosp-shim.dex + target APK classes
  -> WestlakeActivityThread creates McdProfileApp and McdProfileActivity
     through AppComponentFactory
  -> WestlakeLauncher wires extracted res/layout XML bytes before onCreate
  -> Activity.westlakeAttach / westlakePerformCreate / Start / Resume
  -> McdProfileActivity inflates compiled APK XML and binds Material-shaped/ListView tags
  -> WestlakeLauncher writes DLST display-list frames to stdout
  -> WestlakeVM replays DLST into the host SurfaceView buffer
```

The Android phone's ART is still used by the host APK itself. The guest
McD-profile app UI and logic are not launched as a normal phone Activity UI;
they execute inside the Westlake `dalvikvm` subprocess and present frames
through the `DLST` surface pipe.

Input path:

```text
phone touch
  -> Westlake host SurfaceView touch listener
  -> westlake_touch.dat
  -> WestlakeLauncher strict touch poll loop
  -> McD-profile action methods
  -> new DLST frame
```

Network path:

```text
McdProfileActivity guest logic
  -> WestlakeLauncher / host bridge request helpers
  -> host/OHBridge HTTP bridge
  -> supervisor proxy or public HTTPS endpoint
  -> response bytes back to guest state
  -> rendered into DLST frame
```

## OHOS Host Contracts To Implement

An OHOS port should keep the guest-facing contract unchanged and replace only
the host-side plumbing.

| Android host duty | OHOS replacement |
| --- | --- |
| `WestlakeActivity` owns the screen | Ability with an `XComponent` or equivalent native drawing surface |
| `SurfaceView` buffer receives `DLST` replay | XComponent/native surface receives `DLST` replay |
| host touch listener writes `westlake_touch.dat` | XComponent input callback writes the same 16-byte touch packet, or provides an equivalent bridge with the same guest-visible format |
| app private VM dir stores staged APK/DEX/logs | OHOS app data directory with the same file layout expected by `WestlakeVM`/launcher |
| `/data/local/tmp/westlake/dalvikvm` subprocess | packaged or deployed Westlake `dalvikvm` binary for OHOS |
| Android host HTTP bridge / ADB reverse proxy | OHOS host HTTP bridge with the same request/response schema |
| logcat plus copied marker files | OHOS hilog/filesystem marker collection with the same marker gates |

## Acceptance Markers

The OHOS port should pass the same marker set before it is treated as a
PF-466 parity proof for the controlled mock app:

- `MCD_PROFILE_APP_ON_CREATE_OK`
- `MCD_PROFILE_ACTIVITY_ON_CREATE_OK`
- `MCD_PROFILE_GENERIC_ACTIVITY_FACTORY_OK`
- `MCD_PROFILE_WAT_ACTIVITY_LAUNCH_OK`
- `MCD_PROFILE_WAT_ACTIVITY_ONCREATE_OK`
- `MCD_PROFILE_WAT_ACTIVITY_RESUME_OK`
- `MCD_PROFILE_XML_RESOURCE_WIRE_OK table=true layoutBytes=[nonzero]`
- `MCD_PROFILE_XML_TAG_OK tag=TextInputLayout`
- `MCD_PROFILE_XML_TAG_OK tag=MaterialCardView`
- `MCD_PROFILE_XML_TAG_OK tag=ListView`
- `MCD_PROFILE_XML_INFLATE_OK views=[nonzero] materialViews=[nonzero] source=compiled_apk_xml`
- `MCD_PROFILE_XML_BIND_OK list=true materialViews=[nonzero]`
- `MCD_PROFILE_XML_LAYOUT_PROBE_OK`
- `MCD_PROFILE_ADAPTER_GET_VIEW_OK position=4`
- `MCD_PROFILE_STORAGE_PREFS_OK`
- `MCD_PROFILE_LIVE_JSON_OK transport=host_bridge`
- `MCD_PROFILE_ROW_IMAGE_OK index=0 transport=host_bridge`
- `MCD_PROFILE_IMAGE_BRIDGE_OK transport=host_bridge`
- `MCD_PROFILE_REST_POST_OK protocol=2`
- `MCD_PROFILE_REST_HEAD_OK`
- `MCD_PROFILE_REST_MATRIX_OK`
- `MCD_PROFILE_DIRECT_FRAME_OK xml=true materialViews=[nonzero] rows=5`
- `MCD_PROFILE_FULL_RES_FRAME_OK target=1080x2280`
- `MCD_PROFILE_TOUCH_POLL_READY`
- `MCD_PROFILE_TOUCH_POLL_OK`
- `MCD_PROFILE_CATEGORY_OK`
- `MCD_PROFILE_SELECT_ITEM_OK`
- `MCD_PROFILE_CART_ADD_OK`
- `MCD_PROFILE_CHECKOUT_OK`
- `MCD_PROFILE_NAV_DEALS_OK`
- `MCD_PROFILE_NAV_MENU_OK`
- `MCD_PROFILE_READY_FOR_OHOS_PORT_OK`

The visual gate should remain strict: nonblank full-phone output, red McD
header, yellow accents, menu rows, cart bar, and bottom navigation must be
visible in the screenshot.

The Android-phone runner now rejects any `MCD_PROFILE_XML_TAG_WARN` marker and
any `MCD_PROFILE_CONTROLLED_*` launch marker for this controlled McD-profile
slice. It also rejects `NPE-SYNC`. OHOS parity should keep those stricter
gates. This does not claim full upstream Google Material Components XML
support or real McDonald's APK support; it proves the controlled mock
McD-profile tags are wired through Westlake's XML inflation path and the app
launches through the generic WAT/AppComponentFactory path.

## Known Gaps Before Stock McDonald's

PF-466 is useful because it exposes the next real gaps:

- Activity launch is accepted for the McD-profile controlled app through the
  WAT/AppComponentFactory path, but stock McDonald's still needs this generalized
  across arbitrary activities without package-specific allowances.
- `resources.arsc` parsing is now accepted for this controlled mock APK, but
  it is not yet proven across arbitrary stock APK resource tables.
- The runtime still has an object-array/new-array correctness boundary beyond
  the fixed resource-table string-pool case. The profile app avoids `String[]`
  item arrays and uses scalar row fields.
- Libcore charset/encoding remains incomplete. PF-466 avoids the current
  `Charset.forName("UTF-8")` failure with a local UTF-8 encoder and an
  ASCII-safe stdio wrapper, but stock APKs need normal charset behavior.
- Material XML is not upstream-complete. Full Material Components AAR
  compatibility, themes, Coordinator/AppBar behaviors, ripple, animation, and
  generic Material rendering remain open.
- Rendering and touch are still McD-profile controlled. The visible frame comes
  from a McD-specific `DLST` writer and coordinate router in
  `WestlakeLauncher`, not a full generic Android `View.draw()` and hit-test
  pipeline.
- Networking proves the portable bridge shape, not full libcore networking.
  Real multi-method matrix execution, large streamed images, redirects,
  timeout parity, and many concurrent image requests remain open.

## Next Closure Order

1. Port PF-466 unchanged to OHOS as the controlled mock app and require the
   same marker/visual gates.
2. Generalize the accepted McD-profile WAT/AppComponentFactory launch slice to
   arbitrary stock McDonald's activities.
3. Generalize `resources.arsc` table parsing beyond the controlled mock APK.
4. Fix the object-array/new-array runtime boundary and restore array-backed
   menu models.
5. Fix libcore charset/encoding behavior instead of relying on PF-466 local
   workarounds.
6. Move McD-profile rendering from the direct frame writer to generic inflated
   View draw/hit/scroll/adapter paths.
7. Expand networking/images to streamed multi-image transport and real REST
   matrix execution.
8. Swap controlled McD-profile API calls for real stock McDonald's API-surface
   shims until the stock APK can run without app code changes.
