# OHOS McD Profile Integration Guide

Last updated: 2026-04-28

## Purpose

`com.westlake.mcdprofile` is the current controlled McDonald's-shaped Westlake
port target. It exists to close stock-app boundaries under a known API surface
before returning to the real McDonald's APK.

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

It is not a stock APK compatibility claim. It is the first McD-class app that
should be ported to OHOS because every southbound contract is known and can be
implemented one by one.

## Accepted Android Phone Proof

Run:

```bash
WAIT_SECS=24 ./scripts/run-mcd-profile.sh
```

Accepted device: `cfb7c9e3`.

Accepted hashes:

- `dalvikvm=58ea9cb7470e0f5990f3b90b353e46c0041ddc503c7173c8417a24e82a7d1a3e`
- `aosp-shim.dex=8efeef5e8926901f301a24aee9050ce6a758a238d45a14a860664b2333eed2be`
- `westlake-host.apk=0d0f689b35dd8c7be45567fefd533ce9b12df2f08d4cf849bb128823599e83e4`
- `westlake-mcd-profile-debug.apk=f41fd4d2fd06a9d486b8f78f19e161b7a7b1b3f21acde12547574864b279ba8e`

Accepted artifacts:

- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.log`
- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.markers`
- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.trace`
- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.png`
- `/mnt/c/Users/dspfa/TempWestlake/mcd_profile_target.visual`
- `/mnt/c/Users/dspfa/TempWestlake/accepted/mcd_profile/8efeef5e8926901f301a24aee9050ce6a758a238d45a14a860664b2333eed2be_f41fd4d2fd06a9d486b8f78f19e161b7a7b1b3f21acde12547574864b279ba8e/`

Key accepted launch and XML markers:

- `MCD_PROFILE_GENERIC_ACTIVITY_FACTORY_OK class=com.westlake.mcdprofile.McdProfileActivity factory=default`
- `MCD_PROFILE_WAT_ACTIVITY_LAUNCH_OK class=com.westlake.mcdprofile.McdProfileActivity`
- `MCD_PROFILE_WAT_ACTIVITY_ONCREATE_OK class=com.westlake.mcdprofile.McdProfileActivity`
- `MCD_PROFILE_WAT_ACTIVITY_RESUME_OK class=com.westlake.mcdprofile.McdProfileActivity`
- `MCD_PROFILE_XML_RESOURCE_WIRE_OK engine=true table=false apk=true resDir=true arsc=2528 layouts=1 layoutBytes=4112`
- `MCD_PROFILE_XML_TAG_OK` for `TextInputLayout`, `TextInputEditText`,
  `ChipGroup`, `Chip`, `MaterialCardView`, `ImageView`, `MaterialButton`,
  `BottomNavigationView`, and `ListView`
- `MCD_PROFILE_XML_BIND_OK list=true ... materialViews=10`
- `MCD_PROFILE_ADAPTER_GET_VIEW_OK position=4`
- `MCD_PROFILE_XML_INFLATE_OK ... views=25 materialViews=10 source=compiled_apk_xml`

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

The OHOS port should pass the same marker set before it is treated as a real
PF-466 parity proof:

- `MCD_PROFILE_APP_ON_CREATE_OK`
- `MCD_PROFILE_ACTIVITY_ON_CREATE_OK`
- `MCD_PROFILE_GENERIC_ACTIVITY_FACTORY_OK`
- `MCD_PROFILE_WAT_ACTIVITY_LAUNCH_OK`
- `MCD_PROFILE_WAT_ACTIVITY_ONCREATE_OK`
- `MCD_PROFILE_WAT_ACTIVITY_RESUME_OK`
- `MCD_PROFILE_XML_RESOURCE_WIRE_OK layoutBytes=[nonzero]`
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
slice. OHOS parity should keep that stricter gate. This does not claim full
upstream Google Material Components XML support; it proves the controlled
McD-profile tags are wired through Westlake's XML inflation path and the app
launches through the generic WAT/AppComponentFactory path.

## Known Gaps Before Stock McDonald's

PF-466 is useful because it exposes the next real gaps:

- Activity launch is accepted for the McD-profile controlled app through the
  WAT/AppComponentFactory path, but stock McDonald's still needs this generalized
  across arbitrary activities without package-specific allowances.
- `resources.arsc` parsing is still incomplete for this APK. The accepted run
  registers layout XML bytes from extracted `res/layout` entries, while the
  table load marker remains false.
- The runtime still has an object-array/new-array correctness boundary. The
  profile app avoids `String[]` item arrays and uses scalar row fields.
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

1. Port PF-466 unchanged to OHOS and require the same marker/visual gates.
2. Generalize the accepted McD-profile WAT/AppComponentFactory launch slice to
   arbitrary stock McDonald's activities.
3. Fix standalone `resources.arsc` table parsing for the McD-profile APK.
4. Fix the object-array/new-array runtime boundary and restore array-backed
   menu models.
5. Move McD-profile rendering from the direct frame writer to generic inflated
   View draw/hit/scroll/adapter paths.
6. Expand networking/images to streamed multi-image transport and real REST
   matrix execution.
7. Swap controlled McD-profile API calls for real stock McDonald's API-surface
   shims until the stock APK can run without app code changes.
