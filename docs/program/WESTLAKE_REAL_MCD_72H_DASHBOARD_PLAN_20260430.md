# Westlake Real McDonald's 72-Hour Dashboard Success Plan

Prepared: 2026-04-30 PT

This is the supervisor runbook for driving Westlake from the current real-McD
startup proof to a visible, interactive stock McDonald's dashboard on the
phone. It is written for autonomous swarm execution. Workers should use this
as the operating strategy, verify local facts before editing, and report proof
with artifacts rather than claims.

## Ultimate Goal

Within this 72-hour run, make the stock McDonald's APK show a real dashboard UI
on the Android phone while executing app logic inside Westlake's own
`dalvikvm` subprocess.

The target proof is not a mock app, not the McD-profile controlled app, and not
phone ART running McDonald's directly. The proof must show the real
`com.mcdonalds.app` guest route rendering materially more than the current
sparse dashboard shell.

## Current Supervisor Frontier - 2026-05-01 18:35 PT

The frontier moved from "visible real dashboard projection" to "visible,
touch-scrollable real dashboard projection".

Latest accepted interactive projection artifact:

- `artifacts/real-mcd/20260501_183146_mcd_real_dashboard_projection_scroll_probe_after_patch/`
- screen changed after ADB swipe:
  `50187c3fcfd0858ec8795f6c86533d2f1fb19f316a60d8062f582eb716c6ddea`
  -> `d63d41a034ad6e3efb38bf920ffe48d6be480f6523724f8f1b2280b4a641b8d4`;
- `MCD_DASH_TOUCH_ROUTE phase=down/move/up ... rawDispatch=skipped`;
- `MCD_DASH_SCROLL ... projectionBefore=0 projectionAfter=370 moved=true`;
- post-scroll strict frame:
  `bytes=128598 views=96 texts=2 buttons=3 images=1 rows=4
  rowImages=4 rowImageBytes=127479 overlays=0`;
- checker:
  `scripts/check-real-mcd-proof.sh
  artifacts/real-mcd/20260501_183146_mcd_real_dashboard_projection_scroll_probe_after_patch`
  returns `gate_status=PASS`.

This is still not full McD UI parity. It proves stock
`HomeDashboardActivity`, real adapter/item XML, live Westlake network, visible
row images, subprocess purity, and touch-driven projection movement. It does
not prove generic stock Android View drawing, Material/AppCompat behavior,
full RecyclerView touch dispatch, Hero/Menu child-fragment attachment, or
order/cart/login flows.

Immediate swarm work order:

1. Renderer worker: remove the projection-specific y-offset shortcut by making
   generic scroll containers and RecyclerView/list-like children shift through
   the normal View tree traversal.
2. Fragment/lifecycle worker: recover Hero/Menu/Promotion/Popular child
   fragment attachment and `onViewCreated` execution, or replace the
   child-fragment tier with a stronger stock adapter route accepted by the
   checker.
3. Input worker: add a tap proof on the visible stock projection that routes
   from coordinate hit testing into a stock listener or adapter item, not only
   the existing projection scroll path.
4. Image/UI worker: generalize real image bytes out of Glide/ImageView so the
   projection no longer needs McD row image bridges.
5. OHOS worker: keep mapping the accepted proof path to the southbound API
   boundary and mark every Android-only assumption as unportable until an OHOS
   adapter exists.

## Current Supervisor Frontier - 2026-05-01 17:45 PT

There are now two useful proof tiers:

1. `mcd_stock_dashboard_projection`: accepted for the current phone-visible
   real McD dashboard path. It proves stock `HomeDashboardActivity`, real McD
   adapter/item XML, live Westlake networking, subprocess purity, and a dense
   strict display-list frame. It does not prove full stock Android View drawing
   parity.
2. `mcd_child_fragment_section_root`: the older higher-tier proof that all
   four dashboard child-fragment section roots attached from real McD XML.
   Keep it as a regression guard while the projection path becomes more
   complete and interactive.

Latest accepted projection artifact:

- `artifacts/real-mcd/20260501_172943_mcd_real_dashboard_long_gate_fast_row_images/`
- phone: `cfb7c9e3`, ADB server `localhost:5037`;
- screenshot hash:
  `4b3eee911a8c932ed53c775befb5d4e309f2ea6aff4a8b29aff791fde04720c2`;
- guest execution: stock `com.mcdonalds.app` route inside Westlake guest
  `dalvikvm`; no direct phone-ART `com.mcdonalds.app` process;
- dashboard root:
  `HomeDashboardActivity`, `fallback=false`;
- live network:
  `network_attempt_markers=15`, `network_success_markers=9`,
  `network_error_markers=0`, `westlake_bridge=9`;
- real item XML:
  `layout_home_promotion_item` resource `0x7f0e036a` and
  `layout_home_popular_item_adapter` resource `0x7f0e0369`;
- real adapters:
  `HomePromotionAdapter count=1` and `HomePopularItemsAdapter count=3`;
- strict frame:
  `bytes=126064 views=90 texts=14 buttons=3 images=1 rows=8 rowImages=5
  rowImageBytes=331544 overlays=0`;
- updated checker:
  `scripts/check-real-mcd-proof.sh
  artifacts/real-mcd/20260501_172943_mcd_real_dashboard_long_gate_fast_row_images`
  returns `gate_status=PASS`.

Immediate swarm work order:

1. UI/image worker: make the projection visually credible by preserving and
   drawing real `ImageView`/Glide bitmap bytes for promotion and popular rows,
   then rerun screenshot/hash proof.
2. Input worker: rerun full touch/scroll/click proof on this latest real
   dashboard projection. Do not count the old McD harness navigation proof as
   proof for this projection.
3. Fragment/lifecycle worker: recover Hero/Menu child-fragment section markers
   or document why the adapter-driven root path supersedes them. Keep
   `performAttach`, `onViewCreated`, and app `FragmentManager` execution gated
   until they pass isolated phone probes.
4. Southbound/OHOS worker: map every API used by the projection path to the
   OHOS adapter boundary, especially HTTP bytes, frame publication, input, file
   reads, resource streams, and Realm/storage.
5. QA worker: maintain both checker tiers. A regression must fail if the app
   falls back to old `MCD_DASH_FALLBACK`, direct phone ART, sparse frames, or
   missing real adapter XML.

## Current Baseline

Latest accepted real-McD dashboard baseline:

- artifact:
  `artifacts/real-mcd/20260501_134422_mcd_real_promo_seed_image_bytes_probe/`
- phone: `cfb7c9e3`, ADB server `localhost:5037`;
- host: `com.westlake.host/.WestlakeActivity`;
- guest launch extra: `WESTLAKE_ART_MCD`;
- guest execution: stock `com.mcdonalds.app` inside Westlake guest
  `dalvikvm`;
- screenshot: valid phone screenshot, `screen_sha=aa8cbaa4d0e9d9c2036c908e4c84130ab080e5304c00460a6d11818dc13ebb40`;
- latest `Failed requirement` count: `0`;
- real `HomeDashboardFragment` shell attaches;
- real dashboard `u6(List)` creates the four section placeholders;
- all four real child fragment `onCreateView` returns attach;
- Hero inflates real McDonald's
  `fragment_home_dashboard_hero_section.xml` AXML as resource `0x7f0e0282`;
- Menu guest inflates real McDonald's `home_menu_guest_user.xml` AXML as
  resource `0x7f0e0366`;
- Promotion inflates real McDonald's `fragment_promotion_section.xml` AXML as
  resource `0x7f0e030e`;
- Popular inflates real McDonald's `fragment_popular_section.xml` AXML as
  resource `0x7f0e0305`;
- Promotion now attaches the real `HomePromotionAdapter`, inflates real
  McDonald's `layout_home_promotion_item`, and creates one adapter row from a
  Westlake-seeded `Promotion` model;
- the strict-frame proof fetches and renders the row image bytes through
  `WestlakeHttp`, producing `rowImages=1 rowImageBytes=54022`;
- Popular attaches the real `HomePopularItemsAdapter` but currently has
  `itemCount=0`;
- strict frame:
  `bytes=54799 views=69 texts=6 buttons=0 images=1 rows=1 rowImages=1
  rowImageBytes=54022 overlays=0`.

Current UI theory from the dashboard explorer:

- the dashboard shell and section placeholders are now proven real;
- the next visible-density gap is no longer "make the shell visible", it is
  "make real stock adapters/images/data dense enough to look like the actual
  app, then unblock stock lifecycle/FragmentManager behavior";
- all four dashboard child section roots are real child fragment `onCreateView`
  returns and real McDonald's section-root AXML;
- the current visible-density gap is now inside the sections: item adapter XML,
  RecyclerView/adapter population, image rebinding, and app model data;
- `performAttach`, `onViewCreated`, and app FragmentManager execution remain
  unsafe and must stay gated until isolated phone proofs pass.

Critical docs:

- `docs/program/WESTLAKE_REAL_MCD_AGENT_HANDOFF_20260430.md`
- `docs/program/WESTLAKE_PLATFORM_FIRST_ISSUES.md`
- `docs/program/WESTLAKE_SOUTHBOUND_API.md`
- `docs/program/WESTLAKE_REAL_MCD_72H_ISSUE_TEMPLATES_20260430.md`
- `docs/program/WESTLAKE_REAL_MCD_72H_AGENT_PROMPTS_20260430.md`

Remote issue status:

- MCP GitHub issue creation initially failed with
  `403 Resource not accessible by integration`;
- authenticated `gh` CLI succeeded and opened the remote issues:
  `PF-602 #575`, `PF-603 #576`, `PF-604 #577`, `PF-605 #578`,
  `PF-606 #579`, `PF-607 #580`, `PF-608 #581`, `PF-609 #582`,
  `PF-610 #583`, `PF-611 #584`, `PF-612 #585`, `PF-613 #586`.

## Current Supervisor Frontier - 2026-05-01 13:35 PT

Accepted real-McD dashboard artifact:

- `artifacts/real-mcd/20260501_134422_mcd_real_promo_seed_image_bytes_probe/`

Accepted state:

- stock McDonald's APK runs inside the Westlake guest `dalvikvm` subprocess;
- no direct phone-ART `com.mcdonalds.app` process;
- network bridge is live:
  `network_attempt_markers=3`, `network_success_markers=3`,
  `network_error_markers=0`;
- real dashboard shell:
  `MCD_DASH_STOCK_VIEW_ATTACHED`;
- real dashboard `u6(List)` section placeholders:
  `MCD_DASH_U6_SEEDED`;
- real Hero AXML:
  `MCD_REAL_XML_INFLATED layout=layout_fragment_home_dashboard_hero_section
  resource=0x7f0e0282 root=RelativeLayout`;
- real Menu guest AXML:
  `MCD_REAL_XML_INFLATED layout=layout_home_menu_guest_user resource=0x7f0e0366
  root=LinearLayout`;
- real Promotion AXML:
  `MCD_REAL_XML_INFLATED layout=layout_fragment_promotion_section
  resource=0x7f0e030e root=RelativeLayout`;
- real Popular AXML:
  `MCD_REAL_XML_INFLATED layout=layout_fragment_popular_section
  resource=0x7f0e0305 root=RelativeLayout`;
- all four real child fragment views:
  `MCD_DASH_REAL_VIEW_ATTACHED section=HERO`, `MENU`, `PROMOTION`, `POPULAR`;
- fallback rejection:
  zero `MCD_DASH_SECTION_VIEW_ATTACHED`, zero dashboard fallback markers, zero
  real-view failure markers.
- PF-613 adapter row proof:
  `MCD_DASH_ADAPTER_BOOTSTRAP section=PROMOTION
  adapter=com.mcdonalds.homedashboard.adapter.HomePromotionAdapter
  itemCount=1`,
  `MCD_REAL_XML_INFLATED layout=layout_home_promotion_item
  resource=0x7f0e036a root=LinearLayout`,
  `STRICT_IMAGE_LIVE_ADAPTER recycler=2131432435 position=0 bytes=54022`,
  and strict frame `rows=1 rowImages=1 rowImageBytes=54022`.

Next workstream order:

1. PF-613: keep all four section-root XML markers as regression
   gates while replacing the strict-frame image bridge with generic
   Glide/ImageView completion, replacing the seeded promotion with real
   backend/cache data, and populating Popular.
2. PF-611: isolate `onViewCreated` / `LiveData.observe` SIGBUS for Hero.
3. PF-612: replace manual child attach with safe generic app FragmentManager
   execution.

## Current Supervisor Frontier - 2026-05-01 10:12 PT

Accepted two-step navigation/full-gate artifact:

- `artifacts/real-mcd/20260501_100855_mcd_two_step_category_navigation_clean_proof/`

Current accepted state:

- full cold-start gate still passes on the Android phone with stock McD inside
  the Westlake guest `dalvikvm` subprocess;
- current deployed `aosp-shim.dex` hash:
  `57660c18f5f4e0b9b8503f6bc39ebc21ebb8da866fca27b23e2150b5ae6155be`;
- dashboard/order network bridge remains live:
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`;
- screenshot hashes prove two visible transitions:
  `fe12e38e867038b3dc866fa71c867f31685f95fd14b47a59abcfdb794491b36e`
  -> `a404b1c815f6dc578e1fe382e4fb8f04c73d20a82060815f02db5611a4d7cc3c`
  -> `12f2c6078d46f2cfb613e0cde6118e684c8f6281acbd1159096c13429c2f0466`;
- accepted Start Order tap path:
  `GENERIC_HIT_CLICK target=com.mcdonalds.mcduikit.widget.McDTextView ... handled=true`;
- accepted menu/category tap path:
  `GENERIC_HIT_CLICK target=android.widget.LinearLayout leaf=android.widget.ImageView ... handled=true`;
- accepted navigation markers:
  `MCD_ORDER_NAV_OPENED source=start_order_tile_menu` and
  `MCD_CATEGORY_NAV_OPENED label=Extra_Value_Meals source=category_detail`;
- old dashboard y-band fallback and direct text mutation markers are absent;
- strict frame after category navigation:
  `bytes=124948 views=51 texts=10 buttons=3 images=2 rows=4 rowImages=4 rowImageBytes=123608 overlays=0`.

This advances the frontier from "generic click opens a visible McD menu/order
surface" to "two generic clicks navigate from dashboard to menu to category
detail." It still does not close full McD parity because the visible McD UI is
still Westlake's McD boundary harness/layout-builder surface, not the stock McD
order module rendered from generic upstream layouts end to end.

Immediate next workstreams:

- `PF-604`: identify the real order-module/dashboard XML entrypoints and begin
  replacing McD-specific builders with generic XML/resource inflation;
- `PF-606`: broaden generic hit testing to real RecyclerView/card/bottom-nav
  widgets and prove detail/add-to-bag/back flows without harness-specific
  assumptions;
- `PF-605`: replace McD-specific adapter image lookup with stock
  Glide/ImageView/BitmapDrawable evidence;
- `PF-608`: map every API used by the accepted phone proof to an OHOS adapter
  shape, especially frame, input, HTTP/image, file, and Realm storage.

## Previous Supervisor Frontier - 2026-05-01 09:57 PT

Accepted navigation/full-gate artifact:

- `artifacts/real-mcd/20260501_095501_mcd_start_order_tile_menu_navigation_proof/`

Current accepted state:

- full cold-start gate still passes on the Android phone with stock McD inside
  the Westlake guest `dalvikvm` subprocess;
- current deployed `aosp-shim.dex` hash:
  `5915d50d1f9ad46597836234047b5aa421778891641134e752f5df38926296c6`;
- dashboard/order network bridge remains live:
  `network_attempt_markers=8`, `network_success_markers=8`,
  `network_error_markers=0`;
- before/after screenshot hashes differ from the earlier text-only proof:
  `166867224281d6e7923e375e1818891a586ea9de2760817dab9556e04b336625`
  -> `027ceb04c208e28d307807c9e953a4818a2c0e7cfb177df169ff9f431d59abe5`;
- accepted tap path:
  `GENERIC_HIT_CLICK target=com.mcdonalds.mcduikit.widget.McDTextView ... handled=true`;
- accepted navigation marker:
  `MCD_ORDER_NAV_OPENED source=start_order_tile_menu`;
- old dashboard y-band fallback and direct text mutation markers are absent;
- strict frame after navigation:
  `bytes=248976 views=46 texts=7 buttons=0 images=1 rows=8 rowImages=8 rowImageBytes=247216 overlays=0`.

This advances the frontier from "generic click changes Start Order text" to
"generic click opens a visible McD menu/order surface." It still does not close
full McD parity because the menu surface is built by Westlake's McD layout
builders, not the real stock order module flow.

Immediate next workstreams:

- `PF-609`: prove a second navigation step from the order/menu surface:
  category detail, add-to-bag, checkout bar, or bottom navigation;
- `PF-606`: extend generic hit testing to clickable RecyclerView/card targets
  and keep the navigation proof independent of y-band fallbacks;
- `PF-605`: continue replacing McD-specific adapter image lookup with stock
  Glide/ImageView/BitmapDrawable evidence;
- `PF-604`: identify the real order-module entrypoint and the southbound
  dependencies that prevent using it directly.

## Current Supervisor Frontier - 2026-05-01 09:45 PT

Accepted interaction/full-gate artifact:

- `artifacts/real-mcd/20260501_094315_mcd_generic_hit_start_order_visual_delta/`

Current accepted state:

- full cold-start gate still passes on the Android phone with stock McD inside
  the Westlake guest `dalvikvm` subprocess;
- current deployed `aosp-shim.dex` hash:
  `080a4ad1ef6e0d3c9339cd166f97a841ad6df4ab792d38f69cc330fada2b13b9`;
- dashboard network bridge is live:
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`;
- strict dashboard frame after the accepted tap:
  `bytes=198707 views=33 texts=9 buttons=1 images=0 rows=7 rowImages=7 rowImageBytes=197065 overlays=0`;
- before/after screenshot hashes differ:
  `166867224281d6e7923e375e1818891a586ea9de2760817dab9556e04b336625`
  -> `3020e3b028542f16ba1240cf5274b6079d87c0b7fbd7e079a134a33741d997de`;
- the accepted tap path is now generic:
  `GENERIC_HIT_CLICK target=com.mcdonalds.mcduikit.widget.McDTextView ... handled=true`;
- the old dashboard y-band fallback did not fire in the accepted artifact.

This advances the frontier from "button-class affordance represented in strict
frame evidence" to "visible McD affordance reached through root-aware generic
hit testing and `performClick()`." It still does not close full McD parity
because the clicked listener changes text instead of opening the stock order
flow.

Immediate next workstreams:

- `PF-609`: drive one real navigation target beyond text mutation: Start Order
  order-flow route, dashboard card route, or bottom/tab route, with screenshots
  and log markers;
- `PF-606`: extend the accepted generic hit path to RecyclerView/card item
  targets and bottom navigation, and keep y-band fallback only as legacy
  diagnostic fallback;
- `PF-605`: replace McD-specific adapter image lookup with stock
  Glide/ImageView/BitmapDrawable evidence;
- `PF-604`: continue root/decor layout repair so the renderer and input mapper
  no longer need to select around a zero-size decor root.

## Current Supervisor Frontier - 2026-05-01 09:35 PT

Accepted full-gate artifact:

- `artifacts/real-mcd/20260501_093158_mcd_clickable_text_button_count_full_gate/`

Current accepted state:

- full cold-start gate passes on the Android phone with stock McD still inside
  the Westlake guest `dalvikvm` subprocess;
- current deployed `aosp-shim.dex` hash:
  `2835cbdbfc7a8fa8416a00722c7e6d1234f82868cada794c4008acdccbff1873`;
- dashboard network bridge is live:
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`;
- strict dashboard frame:
  `bytes=198705 views=33 texts=9 buttons=1 images=0 rows=7 rowImages=7 rowImageBytes=197065 overlays=0`;
- visible McD UTF-8 text is fixed (`McCafé`, not mojibake);
- all visible dashboard row images are backed by fetched live image bytes;
- prior accepted ADB tap proof still changes `Start Order` to
  `Order Started` with `MCD_DASH_ACTION start_order_text_updated`.

This advances the frontier from "visible dashboard with live image bytes and
one proven tap mutation" to "visible dashboard with live image bytes and a
button-class affordance represented in strict frame evidence." It still does
not close full McD parity because the accepted tap action is a scoped fallback,
not the stock order navigation route.

Immediate next workstreams:

- `PF-606`: replace dashboard-specific tap y-band routing with generic view
  hit testing over visible clickable `TextView`/card/control targets, while
  preserving scroll and row image proof;
- `PF-609`: prove one real navigation route beyond text mutation: Start Order
  route, card route, or bottom/tab route, with screenshots and log markers;
- `PF-605`: replace McD-specific adapter image lookup with stock
  Glide/ImageView/BitmapDrawable evidence. Bitmap byte preservation is now in
  place, but arbitrary stock image binding is not accepted yet;
- `PF-604`: continue root/decor layout repair so the renderer does not need to
  bypass a zero-size decor root.

## Current Supervisor Frontier - 2026-05-01 05:25 PT

Accepted full-gate artifact:

- `artifacts/real-mcd/20260501_092333_mcd_latest_touch_enabled_full_gate/`

Accepted interaction artifact:

- `artifacts/real-mcd/20260501_092239_mcd_visible_start_order_tap_proof/`

Current accepted state:

- full cold-start gate passes on the Android phone with stock McD still inside
  the Westlake guest `dalvikvm` subprocess;
- current deployed `aosp-shim.dex` hash:
  `6cde959352eefcb9e58dd3800766bf8bb6413c57cb02bb25fc176796d0e153f5`;
- dashboard network bridge is live:
  `network_attempt_markers=12`, `network_success_markers=10`,
  `network_error_markers=0`;
- strict dashboard frame:
  `bytes=198705 views=33 texts=9 buttons=0 images=0 rows=7 rowImages=7 rowImageBytes=197065 overlays=0`;
- visible McD UTF-8 text is fixed (`McCafé`, not mojibake);
- all visible dashboard row images are backed by fetched live image bytes;
- ADB tap proof changes `Start Order` to `Order Started` with
  `MCD_DASH_ACTION start_order_text_updated`.

This advances the frontier from "visible/scrollable dashboard" to
"visible dashboard with live image bytes and one proven tap mutation." It still
does not close full McD parity because the button action is a scoped fallback,
not the stock order navigation route.

Immediate next workstreams:

- `PF-606`: make clickable TextViews/cards count and render as buttons/cards,
  then route taps through generic hit testing instead of dashboard-specific
  y-band fallback;
- `PF-605`: replace McD-specific adapter image lookup with stock
  Glide/ImageView/BitmapDrawable evidence. Bitmap byte preservation is now in
  place, but arbitrary stock image binding is not accepted yet;
- `PF-604`: continue root/decor layout repair so the renderer does not need to
  bypass a zero-size decor root;
- `PF-609`: add a navigation proof beyond text mutation: Start Order route,
  card route, or bottom/tab route, with screenshots and log markers.

## Current Supervisor Frontier - 2026-05-01 02:35 PT

Accepted artifact:

- `artifacts/real-mcd/20260501_022708_mcd_dashboard_adb_scroll_probe/`

Current accepted state:

- gate passes on the Android phone with stock McD still running inside the
  Westlake guest `dalvikvm` subprocess;
- the selected render root is now the real
  `home_dashboard_container`, not only `sections_container`;
- dashboard RecyclerViews render through normal direct traversal:
  `STRICT_RECYCLER_DIRECT ... rowsRendered=4` and `rowsRendered=3`;
- diagnostic overlay fallback is removed from the accepted proof:
  `rows=7 overlays=0`;
- the frame shows live McD dashboard imagery, the stock "Ready to order?"
  card, the yellow "Start Order" affordance, and a "Popular" section;
- ADB swipe is accepted as a real interaction proof:
  `MCD_DASH_SCROLL offset=571 before=0 after=571 moved=true`, and the
  after-swipe screenshot hash differs from the pre-swipe screenshot.

This closes the previous "only overlay/static screen" objection. It does not
close full McD parity. The current view is still a simplified strict renderer
over the real McD View tree, with generic row drawing and incomplete image,
Material, and click/navigation fidelity.

Immediate next workstreams:

- `PF-605`: make stock image binding generic. The goal is for Glide/URL-loaded
  ImageViews to expose real BitmapDrawable/Bitmap data to the renderer without
  `mcdLiveAdapterImageBytes(...)`;
- `PF-606`: improve Material/AppCompat/card/button rendering and counters so
  visible affordances such as "Start Order" are represented as stock clickable
  UI, not just text inside a rectangle;
- `PF-604`: reduce the remaining root-layout anomaly where decor reports
  `w=0,h=0`, even though home and sections can now be selected and laid out;
- `PF-609`: extend interaction proof from scroll to tap/navigation: Start
  Order, one dashboard card, and one bottom/tab route.

## Current Supervisor Frontier - 2026-05-01 02:05 PT

Accepted artifact:

- `artifacts/real-mcd/20260501_020025_mcd_dashboard_polished_adapter_rows/`

Current accepted state:

- gate passes on the Android phone with stock McD still running inside the
  Westlake guest `dalvikvm` subprocess;
- the dashboard XML model has been corrected to match the real APK's
  full-screen `activity_home_dashboard` frame layout;
- `home_dashboard_container` is now forced visible after dashboard content is
  present (`v=0` in proof), and the intermediate placeholder is hidden when
  stock sections exist;
- adapter-backed dashboard `RecyclerView` bounds expand from the prior
  1-pixel/tiny state to useful frame bounds, and the renderer emits seven
  live network-backed McD rows;
- strict frame stats are now
  `bytes=170807 views=25 texts=8 buttons=0 images=0 rows=7 overlays=2`;
- screenshot hash is
  `aa843d7ca2a70178d7ccd41bc79892510a35cdaba76a8de1d83271d933c6df05`.

This is a real progress step, but not final dashboard success. The renderer
still needs an overlay fallback to show the rows, and the selected root is
still `sections_container` rather than a fully laid-out `home_dashboard_container`.

Immediate next workstreams:

- `PF-606`: eliminate the diagnostic overlay dependency by making normal
  `renderShowcaseView(...)` traversal render adapter-backed RecyclerViews,
  images, and text directly from the stock tree;
- `PF-604`: fix root/home layout so the visible home container has non-zero
  full-screen bounds and can be selected as the render root;
- `PF-605`: replace the McD-specific live image injection with generic
  `ImageView`/drawable/Bitmap/Glide-compatible image binding;
- `PF-609`: add ADB scroll/tap proof once direct rendering is stable, and keep
  proof capture tied to strict frame consumption.

## Current Supervisor Frontier - 2026-05-01 01:35 PT

Accepted artifact:

- `artifacts/real-mcd/20260501_022500_mcd_hardened_frame_file_live_images/`

Current accepted state:

- gate passes end-to-end on the Android phone;
- McD runs in Westlake guest `dalvikvm` as a subprocess of
  `com.westlake.host`, with no direct phone-ART McD process;
- `HomeDashboardActivity` is reached and selected by `PF-MCD-ROOT`;
- the host consumes strict dashboard frames through the new frame-file bridge;
- visible phone screenshot shows live McD adapter overlay rows with real McD
  food/category images fetched through Westlake user-space networking.

Do not overclaim this as full stock dashboard success. The current visual win is
a diagnostic overlay over the sparse dashboard shell. It proves the network,
image, frame transport, and renderer boundaries, but the next success target is
still stock dashboard child visibility and interaction.

Immediate next workstreams:

- `PF-604`: trace why `home_dashboard_container` remains `GONE` and why
  discovered dashboard RecyclerViews still collapse to tiny bounds;
- `PF-606`: replace diagnostic overlay dependency with generic stock
  RecyclerView/layout/data-binding rendering, then add hit testing and scroll;
- `PF-605`: move from McD-specific image injection toward stock
  Glide/URLConnection/Bitmap rendering proof;
- `PF-609`: keep proof capture at 100+ seconds or first-frame-triggered, since
  this app can stay on splash for over a minute before dashboard frames arrive.

## Current Supervisor Frontier - 2026-04-30 23:50 PT

The launch path has moved past the older Realm-only baseline:

- McD provider network is now bridged through Westlake user space. The native
  runtime hook builds stock `Response` objects with raw JSON bodies so stock
  Gson/listener code can continue.
- Real JSON/image network evidence exists in
  `artifacts/real-mcd/20260430_232634_mcd_bridge_menu_recycler_items/` and
  `artifacts/real-mcd/20260430_234616_mcd_live_menu_images_overlay/`.
- The strict renderer can render RecyclerView adapter preview rows when normal
  traversal misses tiny or hidden dashboard RecyclerViews. The best visible
  screenshot is
  `artifacts/real-mcd/20260430_233823_mcd_hidden_ancestor_recycler_overlay/screen.png`.

Do not overclaim this as full dashboard success:

- the visible rows still pass through Westlake's McD layout-builder adapter
  class `android.view.LayoutInflater_1`;
- the real stock dashboard HOME container remains hidden in root probes
  (`home=... v=8`);
- real RecyclerView bounds are collapsing to 1px in proof logs;
- stock Glide/OkHttp images are not generically supported;
- the host frame reader did not consume the binary/live-image display-list
  frame in the latest image proof, so proof capture itself is now a work item.

Updated top workstreams:

1. `PF-604`: dashboard visibility/layout ownership. Trace HOME navigation,
   fragment visibility setters, collapsed bounds, and relayout after adapters.
2. `PF-605`: formal HTTP/image southbound contract. Keep the McD provider hook
   but generalize toward URLConnection/OkHttp/Glide.
3. `PF-606`: renderer/proof hardening. Distinguish real view traversal from
   diagnostic adapter-preview overlays; make image frames visible and capture
   robustly.
4. `PF-609`: proof automation. Use `grep -a` for binary logs, capture process
   state immediately after first frame, and record whether `Frame: ... -> View`
   was consumed by the host.

## Non-Negotiable Acceptance Rules

- The real guest APK logic must run in Westlake `dalvikvm`; reject any proof
  using phone `app_process64`, phone `dalvikvm64`, normal installed McD
  Activity execution, or host `DexClassLoader` as the guest executor.
- Every claimed improvement must be backed by a proof artifact containing
  logs, screenshot, hashes, and focused grep markers.
- Runtime changes must remain source-built for Android bionic ARM64 and OHOS
  musl ARM64 where applicable. Android proof alone is not an OHOS claim.
- Do not add direct fake UI frames to claim dashboard success. Fallbacks may
  seed data or compatibility behavior at Android API/JNI/storage/network
  boundaries, but the visible UI must be produced by the McD app's View tree.
- Do not hide the problem with broad global noops. Any diagnostic cutout must
  be documented with the exact method, reason, and removal path.
- Do not repeat the broad `OsResults.nativeSize -> 3` style probe. Result
  sizes must be keyed by table/query/context.

## Success Definition

Minimum 72-hour success:

- stock McD reaches dashboard under Westlake guest subprocess;
- screenshot shows recognizable McD dashboard content beyond a shell;
- dashboard frame stats improve materially over baseline:
  target `bytes > 5000`, `views > 80`, `texts > 10`, `images > 2`;
- no fatal signal, `JNI DETECTED ERROR`, `UnsatisfiedLinkError`, latest
  `Failed requirement`, or uncaught Java crash in the accepted proof window;
- at least one dashboard interaction is proven through ADB touch:
  scroll, bottom/nav action, menu/deals tile, or dashboard card click;
- proof includes Android runtime hash, OHOS runtime hash or OHOS symbol-gate
  hash, shim hash, host APK hash, screen PNG, logcat, focused grep, and process
  state.

Stretch success:

- multiple dashboard sections visible, including either menu/deals/restaurant
  content or logged-in/guest-state tiles;
- images load through the app's image/network path or a documented network
  fixture boundary, not direct renderer substitution;
- temporary McD-specific cutouts are reduced, not expanded.

## Swarm Structure

The supervisor owns integration, issue routing, proof acceptance, and conflict
control. Workers should operate with disjoint write ownership.

### Supervisor

Responsibilities:

- keep the proof loop moving every 2-3 hours;
- maintain this runbook, the handoff, and the issue map;
- merge worker changes only after source build and phone proof;
- reject proofs that run on phone ART or draw mock UI;
- keep a blocker queue ranked by current phone evidence;
- call an external reviewer, including Claude Code, after each major runtime
  or shim slice.

Write ownership:

- docs, orchestration scripts, proof artifact indexing, final integration.

### Worker A: Realm/Storage Boundary

Primary owner:

- `/home/dspfac/art-latest/patches/runtime/interpreter/interpreter_common.cc`
  or a new portable Realm compatibility module if the code is split cleanly.

Mission:

- replace the current Realm zero/no-op diagnostics with a targeted portable
  table/query/result/row state machine for McD's dashboard path.

First APIs to implement or instrument:

- schema/property setup:
  `Property.nativeCreatePersistedProperty(...)`,
  `Property.nativeCreatePersistedLinkProperty(...)`,
  `Property.nativeGetColumnKey(...)`,
  `OsObjectSchemaInfo.nativeCreateRealmObjectSchema(...)`,
  `OsObjectSchemaInfo.nativeAddProperties(...)`,
  `OsObjectSchemaInfo.nativeGetProperty(...)`,
  `OsSchemaInfo.nativeCreateFromList(...)`,
  `OsSchemaInfo.nativeGetObjectSchemaInfo(...)`;
- realm/config lifecycle:
  `OsRealmConfig.nativeCreate(...)`,
  `OsRealmConfig.nativeSetSchemaConfig(...)`,
  `OsSharedRealm.nativeGetSharedRealm(...)`,
  `OsSharedRealm.nativeGetSchemaInfo(...)`,
  `OsSharedRealm.nativeHasTable(...)`,
  `OsSharedRealm.nativeBeginTransaction(...)`,
  `OsSharedRealm.nativeCommitTransaction(...)`,
  `OsSharedRealm.nativeCancelTransaction(...)`,
  `OsSharedRealm.nativeIsInTransaction(...)`,
  `OsSharedRealm.nativeIsClosed(...)`,
  `OsSharedRealm.nativeCloseSharedRealm(...)`;
- `io.realm.internal.OsSharedRealm.nativeGetTableRef(long,String)`
- `io.realm.internal.Table.nativeGetName(long)`
- `io.realm.internal.Table.nativeGetColumnKey(long,String)`
- `io.realm.internal.Table.nativeGetColumnName(long,long)`
- `io.realm.internal.Table.nativeGetColumnNames(long)`
- `io.realm.internal.Table.nativeGetColumnType(long,long)`
- `io.realm.internal.OsObjectSchemaInfo.nativeGetProperty(long,String)`
- `io.realm.internal.Property.nativeGetColumnKey(long)`
- `io.realm.internal.Table.nativeWhere(long)`
- `io.realm.internal.TableQuery.nativeRawPredicate(long,String,long[],long)`
- `io.realm.internal.TableQuery.nativeValidateQuery(long)`
- `io.realm.internal.TableQuery.nativeFind(long)`
- `io.realm.internal.TableQuery.nativeCount(long)`
- `io.realm.internal.OsResults.nativeCreateResults(long,long)`
- `io.realm.internal.OsResults.nativeSize(long)`
- `io.realm.internal.OsResults.nativeGetMode(long)`
- `io.realm.internal.OsResults.nativeGetRow(long,long)`
- `io.realm.internal.OsResults.nativeFirstRow(long)`
- `io.realm.internal.OsResults.nativeGetTable(long)`
- `io.realm.internal.Table.nativeGetRowPtr(long,long)`
- `io.realm.internal.UncheckedRow.nativeIsValid(long)`
- `io.realm.internal.UncheckedRow.nativeGetObjectKey(long)`
- `io.realm.internal.UncheckedRow.nativeGetLong(long,long)`
- `io.realm.internal.UncheckedRow.nativeGetString(long,long)`
- any additional getters reached after the first non-empty rows:
  boolean, double, timestamp/date, object link, list.

Implementation path:

1. Allocate stable pseudo handles for tables, properties, columns, queries,
   results, and rows.
2. Preserve table names for `class_KeyValueStore` and `class_BaseCart`.
3. Preserve property names and assign non-zero, stable column keys.
4. Record predicate text and bound argument references for each query.
5. Return targeted result sizes only for known table/query combinations.
6. Back rows with portable in-memory records first; move to file-backed
   storage only after dashboard data path is proven.
7. Log `PFCUT-REALM-STATE` lines for table, query, result, row, and getter
   decisions.
8. Defer Realm writes until reads are correct. Treat
   `OsObject.nativeCreateRow*`, `Table.nativeSet*`, and result delete APIs as
   a second wave unless proof shows startup/dashboard requires them.

Likely initial schema/data:

- `class_KeyValueStore`: `_createdOn long`, `_maxAge long`, `key string`,
  `value string`;
- likely seed candidates: `language=en-US`, `currentAppVersion`,
  `currentAppVersionCode`; only seed auth/session-like keys such as
  `SERVER_AUTH_TOKEN` if the proof shows that token flow is a dashboard gate;
- `class_BaseCart`: include known cart columns such as `_createdOn`, `_maxAge`,
  `cartUUID`, `cartStatus`, `storeId`, `marketId`, `languageName`,
  `orderDate`, `orderId`, `orderNumber`, `orderStatus`, `totalValue`,
  `totalDue`, `totalTax`, `totalDiscount`, and `isPaidOrder`;
- default strategy: return no active `BaseCart` row at first. Only create an
  active order/cart row if diagnostics prove the dashboard needs an active
  order card. This avoids regressing the dashboard with pseudo-cart state.

Fallbacks:

- If native Realm emulation grows too large, implement a deliberate
  Realm-JNI compatibility layer for only the observed McD table/query surface.
- If the app needs real persisted SDK config rows, seed them at the storage
  boundary, not by drawing fake UI.
- If a query cannot be decoded because bound values are opaque handles, add
  argument decoding diagnostics before guessing.
- Unknown tables should return correct empty Realm semantics plus diagnostics,
  not global fake `nativeSize` behavior.

Done evidence:

- non-zero targeted `OsResults.nativeSize(...)` for at least one dashboard
  query without pre-dashboard regression;
- row getter logs returning non-empty values;
- `nativeFind(...)` returns real row ids or `-1`, not an unconditional zero;
- `KeyValueStore` and `BaseCart` column keys are stable and non-zero;
- dashboard frame stats improve or the next blocker is captured clearly;
- Android bionic runtime builds and OHOS musl runtime links/symbol-gates.

### Worker B: McD Reverse Engineering

Primary owner:

- read-only unless explicitly assigned a small generated fixture or doc.

Mission:

- identify the exact McD data and state needed to make dashboard sections
  visible.

Exploration path:

- inspect `HomeDashboardActivity`, `HomeDashboardFragment`, dashboard helpers,
  adapters, and binding classes in the decompiled tree;
- map all conditions that keep `home=LinearLayout#0x7f0b0ae8` `GONE`;
- map data dependencies for dashboard sections, restaurants, menu, deals,
  cart, guest/login state, and feature flags;
- identify Realm model fields and generated proxy column names for
  `KeyValueStore`, `BaseCart`, and any newly observed table;
- trace image/network loaders and URLs if dashboard content requires remote
  payloads.

Output required:

- exact class/method names;
- resource IDs and layout names;
- required key/value rows or network responses;
- next API boundary causing sparse UI.

### Worker C: Dashboard UI/Rendering

Primary owner:

- `shim/java/android/view/*`
- `shim/java/android/widget/*`
- Material/AppCompat shim classes only when required.

Mission:

- make McD's real View tree measure, lay out, draw, scroll, and receive input
  once data exists.

Exploration path:

- log `setVisibility`, `addView`, adapter binding, `notifyDataSetChanged`,
  `requestLayout`, `invalidate`, and final bounds for dashboard roots;
- inspect and support layouts:
  `activity_home_dashboard.xml`, `fragment_home_dashboard.xml`,
  `home_dashboard_section.xml`, `dashboard_title_section.xml`,
  `home_deal_adapter.xml`, `home_menu_guest_user.xml`,
  `home_menu_section_item.xml`, `fragment_home_dashboard_hero_*`;
- verify TextView/ImageView/Button/ListView/ScrollView/RecyclerView-class
  behavior needed by these layouts;
- diagnose if the sparse frame is caused by data absence, visibility, adapter
  binding, resource style failure, or renderer omission.

Fallbacks:

- If a specific widget is missing, implement the generic Android widget
  behavior for that class rather than a McD-only renderer.
- If a complex Material widget blocks visibility, implement the minimal
  Material-compatible class semantics and rendering needed by the app-owned
  View tree.
- If dashboard remains invisible after data exists, add diagnostic overlays
  only in logs/frames marked as diagnostics; do not claim those as success.

Done evidence:

- frame stats exceed success threshold;
- screenshot visibly shows real McD dashboard sections;
- ADB touch proves scroll/click reaches app listeners;
- no direct fallback UI claim.

### Worker D: Network, Images, And API Content

Primary owner:

- `shim/java/android/net/*`
- `NetworkBridge`
- host/OHBridge network adapters;
- cache/fixture code only if explicitly isolated.

Mission:

- ensure real McD app network and image needs can be satisfied through a
  portable network boundary.

Exploration path:

- log outgoing URLs, method, headers, body size, status, and response size;
- identify whether McD blocks on auth, certificates, locale, market, or device
  identity before dashboard data;
- verify image paths and decoders are not replaced by static blocks;
- determine whether menu/deals/dashboard content is local config, Realm cache,
  live REST, or both.

Fallbacks:

- If public McD endpoints require credentials or anti-abuse state, introduce a
  documented network fixture/cache boundary keyed by URL/method/headers. This
  is acceptable only if it feeds the real app network/parser/data path.
- Do not bypass app data models by writing UI frames.
- If TLS/provider is the blocker, isolate it as a Conscrypt/cert-store issue,
  not as a network-data issue.

Done evidence:

- live or fixture-backed HTTP responses logged through the app's own call path;
- at least one real image byte path reaches ImageView/decoder/rendering;
- OHOS adapter requirements recorded.

### Worker E: Runtime Stability And Java/AndroidX

Primary owner:

- `/home/dspfac/art-latest/patches/runtime/*`
- libcore/native stubs only for generic Java/runtime behavior.

Mission:

- keep the guest runtime stable under McD's Kotlin/coroutine/Rx/AndroidX load
  while avoiding broad app-specific cutouts.

Focus:

- eliminate or narrow the temporary `JustFlipBase.c(...)` event-emission
  shield after identifying the coroutine/SharedFlow root cause;
- monitor hot loops in `main-256mb`;
- keep VarHandle/Unsafe/concurrency fallbacks generic;
- ensure new native/JNI intercepts pass both bionic and OHOS builds.

Fallbacks:

- If coroutine event emission is too risky for 72-hour dashboard success, keep
  the shield but document it as temporary and prove it is not the reason the
  dashboard UI content appears.
- If a new runtime crash appears, bisect against the last accepted artifact and
  keep a rejected artifact with hashes.

Done evidence:

- no fatal crash markers;
- no unbounded CPU loop in proof window;
- source-built bionic and OHOS runtime gates pass.

### Worker F: OHOS/Southbound Contract

Primary owner:

- `docs/program/WESTLAKE_SOUTHBOUND_API.md`
- OHOS host/adapter docs and build scripts.

Mission:

- ensure every Android-phone success path has a corresponding OHOS/musl
  contract or explicit open issue.

Focus:

- Realm storage adapter;
- network/image adapter;
- surface/input adapter;
- resource/file/storage adapter;
- native loading policy for APK `.so` files.

Done evidence:

- issue map names any Android-only delegate that remains;
- OHOS runtime build or symbol gate is updated after every runtime slice;
- no claim says "portable to OHOS" solely because it worked on the Android
  phone.

### Worker G: QA/Proof Automation

Primary owner:

- proof scripts and artifacts.

Mission:

- make acceptance repeatable without relying on manual visual guesses.

Required proof loop:

1. Build shim and host.
2. Build Android bionic runtime.
3. Build OHOS musl runtime or run symbol gate when applicable.
4. Push `dalvikvm` and `aosp-shim.dex` to `/data/local/tmp/westlake`.
5. Launch host with `WESTLAKE_ART_MCD`.
6. Wait long enough for dashboard, at least 55 seconds after clean install.
7. Capture screenshot, logcat, process state, hashes.
8. Run focused grep gate.
9. Extract frame stats and compare to baseline.
10. Drive ADB touch for scroll/click once visible content appears.

Reject proof if:

- guest process is absent;
- phone ART executes McD directly;
- screenshot is black/white/sparse with no log improvement;
- fatal markers occur after the claimed improvement;
- hashes do not match staged files.

## 72-Hour Timeline

### Phase 0: First 2 Hours - Freeze Baseline And Assign Workers

Supervisor:

- keep `20260430_164915_justflip_config_realm_args` as the baseline;
- create issues for this plan and major workstreams;
- post this runbook into the handoff and issue map;
- confirm phone access and proof script path;
- make sure no long-running stale exec sessions are left open.

Workers:

- Worker A maps current Realm intercept code and adds no code until handle
  model is agreed;
- Worker B maps dashboard data/visibility preconditions;
- Worker C maps visible layout/widget requirements;
- Worker D maps network/image call-path diagnostics;
- Worker F checks every plan item against the southbound contract.

Gate:

- no code is accepted unless it targets a named blocker and has a proof plan.

### Phase 1: Hours 2-12 - Realm State Machine Diagnostics And First Rows

Primary objective:

- turn Realm from opaque no-op boundary into traceable table/query/result/row
  state.

Worker A:

- implement table/property/query/result/row handle tracking;
- add predicate and bound-argument logs;
- add stable column keys and row getter dispatch;
- return no data initially except for explicitly known rows.

Worker B:

- identify `KeyValueStore` keys and `BaseCart` fields the dashboard reads;
- identify next Realm tables if the first rows expose more queries.
- map the exact dashboard HOME transition path:
  `showHome`, `validateAndNavigate`, `navigate(HOME)`, and
  `showOrHideHomeDashboardFragment(true)`;
- map `HomeDashboardFragment.onCreateView/onViewCreated` and section builder
  calls around `sections_container` child count before/after population.

Supervisor/QA:

- run proof after diagnostics only;
- run proof after first targeted row result;
- compare frame stats and crash markers.

Fallback:

- if row data causes pre-dashboard regression, turn result sizes back to zero
  for that query only and preserve diagnostics.

Gate:

- accepted only if dashboard still reaches active state and either frame stats
  improve or the next precise data/UI blocker is identified.

### Phase 2: Hours 12-24 - Dashboard Data Activation

Primary objective:

- feed enough real-shaped state into McD's app model to make dashboard
  sections visible.

Worker A:

- add minimal portable backing records for needed Realm rows;
- support additional Realm getters encountered by proof;
- keep all records table/query scoped.

Worker B:

- deliver a dashboard precondition matrix:
  feature flags, config keys, market, restaurant, menu/deal/cart state,
  guest/login state, network vs cache.

Worker C:

- add visibility/layout diagnostics and close obvious widget omissions.
- log fragment transactions by class, container id, resolved container, view
  class, and child count;
- log layout inflation by resource id/name/path for
  `fragment_home_dashboard`, `home_dashboard_section`, hero/menu/deals
  layouts;
- ensure `RecyclerView`, `NestedScrollView`, `FragmentContainerView`,
  Material/AppCompat widgets, and data binding tags do not collapse into blank
  placeholders.

Worker D:

- add URL/image diagnostics and fixture decision if live endpoints block.

Gate:

- target first visible dashboard content beyond shell:
  `texts > 0` and `bytes > 1000`.

### Phase 3: Hours 24-40 - UI Fidelity, Scroll, And Image Path

Primary objective:

- convert first content into a convincing dashboard screen and prove input.

Worker C:

- implement missing generic widget rendering/layout for dashboard layouts;
- ensure TextView sizing, ImageView bounds, ScrollView/ListView/Recycler-like
  behavior, and Material-shaped controls do not collapse;
- wire generic hit testing where the dashboard needs it.

Worker D:

- ensure at least one image byte path reaches the rendered UI;
- decide live network vs fixture cache based on logs and credentials reality.

Worker G:

- add ADB touch proof for scroll/click and screenshot-after-touch.

Fallback:

- if full dashboard data is blocked by auth, use a URL-keyed fixture/cache
  response at the network boundary, clearly labeled. This is a fallback for
  app data transport, not a mock UI.

Gate:

- screenshot visibly shows dashboard content;
- frame stats target `bytes > 5000`, `views > 80`, `texts > 10`,
  `images > 2` or a justified near-miss with a clear next blocker.

### Phase 4: Hours 40-56 - Remove Risky Cutouts And Stabilize

Primary objective:

- reduce McD-specific hacks while keeping dashboard success.

Worker E:

- diagnose whether `JustFlipBase.c(...)` shield can be replaced by a generic
  coroutine/SharedFlow compatibility fix;
- inspect CPU hot loops and render loop churn;
- ensure any native/JNI changes pass OHOS build/symbol gate.

Worker A:

- move Realm state machine helpers out of ad hoc branches if the code is now
  large enough to justify a module.

Worker F:

- update southbound doc with any new exposed APIs and OHOS adapter gaps.

Gate:

- dashboard remains visible after cleanup;
- no accepted proof depends on a newly added broad no-op.

### Phase 5: Hours 56-72 - Acceptance Proof And Handoff

Primary objective:

- produce the final 72-hour acceptance artifact and next-stage handoff.

Supervisor/QA:

- clean-ish rebuild of shim/host/runtime;
- Android phone proof with fresh install, 55+ second dashboard wait, screenshot,
  interaction proof, focused grep, hashes, process state;
- OHOS runtime link/symbol gate from same source;
- update handoff, issue map, and southbound doc;
- document remaining gaps honestly.

Final acceptance artifact must include:

- `hashes.txt`;
- `phone_hashes.txt`;
- `logcat.txt` and focused grep;
- `screen.png`;
- `screen_after_touch.png` when input is proven;
- `am_start.txt`;
- `processes.txt`;
- summary of frame stats versus baseline.

## Decision Tree

Use this sequence when the app still renders sparse UI:

1. Did the app crash or throw before dashboard active?
   - yes: fix runtime/framework blocker first.
   - no: continue.
2. Is dashboard root present but `GONE`?
   - yes: trace feature/config/data predicates and visibility setters.
3. Are Realm results zero or row values empty?
   - yes: continue Realm state machine work.
4. Are app data models populated but views still absent?
   - yes: inspect HOME navigation, fragment transactions, adapters, binding,
     lifecycle, and layout/widget gaps.
5. Are views present but not visible in frame stats?
   - yes: fix renderer/layout/text/image serialization.
6. Are images missing while text appears?
   - yes: fix network/image decode/render path.
7. Is content visible but not touchable?
   - yes: fix generic hit testing, scroll dispatch, and listener invocation.

## Fallback Strategy Ladder

Fallbacks are allowed only if they move through real Android-facing contracts.

Accepted fallback order:

1. Portable Realm JNI/storage compatibility for observed app tables.
2. Portable network fixture/cache keyed by real URL/method/header contract.
3. Minimal generic widget/Material compatibility for classes used by the app.
4. Temporary method cutout with exact owner/method/reason/removal criteria.

Rejected fallback order:

1. Direct hardcoded McD dashboard frame as success.
2. Running the installed McD app through phone ART.
3. Globally positive Realm result sizes.
4. Claiming OHOS portability from Android-only bionic `.so` loading.
5. Suppressing all errors without exposing the next boundary.

## Issue Workstreams

The 72-hour plan should be mirrored into GitHub/local issues as these
workstreams:

- `PF-602`: 72-hour real McD dashboard success parent.
- `PF-603`: portable Realm table/query/result/row state machine.
- `PF-604`: McD dashboard visibility, databinding, and View-tree density.
- `PF-605`: real McD network/image/content transport boundary.
- `PF-606`: dashboard UI rendering, Material/AppCompat widgets, scroll/input.
- `PF-607`: runtime stability, coroutine/event, CPU, and cutout cleanup.
- `PF-608`: OHOS/musl southbound parity for McD-critical APIs.
- `PF-609`: proof automation and acceptance evidence.

Each issue should link this document and include:

- owner role;
- write scope;
- first commands or files to inspect;
- acceptance evidence;
- fallback boundary.

## Supervisor Cadence

Every 2-3 hours:

- collect worker status;
- run or schedule a phone proof if any runtime/shim behavior changed;
- update the blocker ranking;
- choose one next patch, not many unrelated patches;
- reject stale or duplicate work;
- update docs with accepted artifacts.

Every major patch:

- build Android bionic runtime or shim/host as applicable;
- build or symbol-gate OHOS runtime for runtime changes;
- push to phone and prove;
- ask Claude Code or another reviewer to challenge the direction before
  continuing if the patch changes runtime semantics or broad framework
  behavior.

End of each day:

- produce a short supervisor note:
  accepted artifacts, closed gaps, new top blocker, rejected attempts,
  next-worker ownership.
