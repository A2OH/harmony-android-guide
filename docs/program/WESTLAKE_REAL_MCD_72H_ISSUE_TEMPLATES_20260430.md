# Westlake Real McD 72-Hour Issue Templates

Prepared: 2026-04-30 PT

GitHub issue creation from the MCP connector initially failed with:

```text
403 Resource not accessible by integration
```

The authenticated `gh` CLI later succeeded. Remote issues are open in
`A2OH/westlake`:

- `PF-602`: `#575`
- `PF-603`: `#576`
- `PF-604`: `#577`
- `PF-605`: `#578`
- `PF-606`: `#579`
- `PF-607`: `#580`
- `PF-608`: `#581`
- `PF-609`: `#582`
- `PF-610`: `#583`
- `PF-611`: `#584`
- `PF-612`: `#585`
- `PF-613`: `#586`

Keep the templates below as the canonical body text for edits, reposts, or
subissue recreation if a remote issue is accidentally closed or overwritten.

## PF-610: Move Remaining Real McD Dashboard Child Sections To Real AXML

Remote issue: `A2OH/westlake#583`

Labels: `westlake`, `real-mcd`, `ui`, `xml`

Update 2026-05-01 13:00 PT:

- Menu guest XML is now accepted in
  `artifacts/real-mcd/20260501_125736_mcd_real_menu_guest_xml_probe_rerun/`
  with `MCD_REAL_XML_INFLATED layout=layout_home_menu_guest_user
  resource=0x7f0e0366 root=LinearLayout`.
- Promotion XML is now accepted in
  `artifacts/real-mcd/20260501_130515_mcd_real_promotion_section_xml_probe_rerun/`
  with `MCD_REAL_XML_INFLATED layout=layout_fragment_promotion_section
  resource=0x7f0e030e root=RelativeLayout`.
- Popular XML is now accepted in
  `artifacts/real-mcd/20260501_134422_mcd_real_promo_seed_image_bytes_probe/`
  with `MCD_REAL_XML_INFLATED layout=layout_fragment_popular_section
  resource=0x7f0e0305 root=RelativeLayout`.
- PF-610 section-root XML scope is complete. Next issue should target
  promotion/popular item adapter XML, RecyclerView/adapter population, image
  rebinding, and app data/model hydration.

Body:

```markdown
Accepted frontier:
`artifacts/real-mcd/20260501_124513_mcd_real_hero_xml_manual_first_clean_pass/`.

Current proof:

- `WESTLAKE_ART_MCD` runs the real McDonald's APK inside the Westlake guest
  subprocess on phone `cfb7c9e3`.
- Real `HomeDashboardFragment` shell attaches.
- Real dashboard `u6(List)` creates placeholders.
- All four real child fragment `onCreateView` returns attach.
- Hero now inflates real McDonald's
  `fragment_home_dashboard_hero_section.xml` AXML with
  `MCD_REAL_XML_INFLATED layout=layout_fragment_home_dashboard_hero_section
  resource=0x7f0e0282 root=RelativeLayout`.

Gap:

Menu, Promotion, and Popular child sections still use builder-backed section
layouts after real child fragment creation.

Done when:

- Each section emits a section-specific `MCD_REAL_XML_INFLATED` marker from
  the real McDonald's AXML resource.
- Existing `MCD_DASH_REAL_VIEW_ATTACHED` markers remain present for HERO,
  MENU, PROMOTION, POPULAR.
- `scripts/run-real-mcd-phone-gate.sh <label>` passes on `cfb7c9e3` with zero
  `MCD_DASH_SECTION_VIEW_ATTACHED` fallback markers and zero dashboard
  fallback/real-view failure markers.
```

## PF-611: Isolate Real McD onViewCreated LiveData.observe SIGBUS

Remote issue: `A2OH/westlake#584`

Labels: `westlake`, `real-mcd`, `lifecycle`, `runtime`

Body:

```markdown
Accepted frontier keeps `westlake.mcd.child.onviewcreated=false`.

Diagnostic failure:

- Artifact: `artifacts/real-mcd/20260501_122703_mcd_child_onviewcreated_probe/`.
- Probe called real child fragment `onViewCreated` and SIGBUSed inside
  `LiveData.observe`.

Gap:

Real child fragment `onCreateView` is accepted, but stock child lifecycle after
view creation is not safe yet.

Done when:

- A focused probe identifies the exact missing LiveData/Lifecycle/observer
  contract.
- Enabling a narrow `onViewCreated` slice no longer SIGBUSes for Hero.
- The accepted Hero real-AXML gate still passes and records a new explicit
  marker for safe `onViewCreated` progress.
- Do not enable global `onViewCreated` by default until the phone gate passes.
```

## PF-612: Replace McD Dashboard Manual Child Attach With Safe Generic FragmentManager Execution

Remote issue: `A2OH/westlake#585`

Labels: `westlake`, `real-mcd`, `fragmentmanager`, `androidx`

Body:

```markdown
Accepted frontier:
`artifacts/real-mcd/20260501_124513_mcd_real_hero_xml_manual_first_clean_pass/`.

Current accepted path:

- Westlake seeds host/activity state.
- It skips child `performAttach`.
- It invokes real child fragment create-view path directly.
- Dashboard replace probing is opt-in behind
  `westlake.mcd.dashboard.replace.probe`.

Gap:

This proves real McD dashboard child views and first Hero real AXML, but it is
not stock FragmentManager compatibility. Prior probes showed unsafe behavior
around app FragmentManager execution / `h0(true)` and child `performAttach`.

Done when:

- App-owned FragmentManager/BackStackRecord replace/commit creates visible
  content in section containers without SIGBUS.
- The proof no longer relies on direct child view attachment from
  `WestlakeLauncher`.
- Existing dashboard proof still passes: Westlake subprocess purity, real
  dashboard shell, real `u6` placeholders, all four real child sections, Hero
  real AXML, network bridge success, and no fallback section markers.
```

## PF-613: Populate Real McD Dashboard Section Item Adapters And Image/Data Rows

Remote issue: `A2OH/westlake#586`

Labels: `westlake`, `real-mcd`, `adapter`, `ui`

Body:

```markdown
Accepted prerequisite:

- `artifacts/real-mcd/20260501_134422_mcd_real_promo_seed_image_bytes_probe/`
- All four real dashboard child section roots now inflate from real McDonald's
  AXML.
- Promotion now attaches the real `HomePromotionAdapter`, inflates
  `layout_home_promotion_item`, and creates one row.
- Strict-frame rendering now fetches the row image through `WestlakeHttp` and
  records `STRICT_IMAGE_LIVE_ADAPTER recycler=2131432435 position=0 bytes=54022`.
- All four real child fragment view markers remain present.
- Zero section fallback markers.

Current gap:

The real-adapter frame is structurally stronger but not yet stock-complete:
`bytes=54799 views=69 texts=6 buttons=0 images=1 rows=1 rowImageBytes=54022`.
The Promotion row is driven by the real app adapter and item XML, but the
current model is Westlake-seeded, the accepted image proof is a strict-frame
image-byte bridge rather than stock Glide/ImageView completion, and Popular's
real adapter attaches with `itemCount=0`.

Scope:

- Promotion item XML: `home_promotion_item.xml` /
  `home_promotion_item_updated.xml`.
- Popular item XML: `home_popular_item_adapter.xml`.
- RecyclerView adapter creation/binding, item count, row layout params,
  text/image binding.
- App data/model hydration for promotion/popular rows without direct UI
  substitution.
- Generic Glide/ImageView row image binding that produces nonzero image bytes
  without the strict-frame fallback.
- Preserve real section-root XML markers as regression gates.

Done when:

- Gate still proves Westlake subprocess purity, real dashboard shell, real
  `u6` placeholders, all four real child fragment views, and all four real
  section-root XML markers.
- At least one promotion or popular item row is created through the app
  adapter/item XML path.
- Strict frame regains nonzero row/image-byte evidence without
  `MCD_DASH_SECTION_VIEW_ATTACHED` fallback markers.
- Follow-up artifacts document whether missing data comes from network,
  Realm/cache, ViewModel/LiveData, or RecyclerView adapter semantics.
```

## PF-602: 72-Hour Real McDonald's Dashboard Success Parent

Labels: `westlake`, `real-mcd`, `program`

Body:

```markdown
Parent workstream for the 2026-04-30 72-hour push to move stock
`com.mcdonalds.app` from the current sparse dashboard shell to a visible,
interactive dashboard while executing inside Westlake guest `dalvikvm`.

Runbook:
`docs/program/WESTLAKE_REAL_MCD_72H_DASHBOARD_PLAN_20260430.md`

Southbound contract:
`docs/program/WESTLAKE_SOUTHBOUND_API.md`

Current handoff:
`docs/program/WESTLAKE_REAL_MCD_AGENT_HANDOFF_20260430.md`

Baseline proof:

- `artifacts/real-mcd/20260430_164915_justflip_config_realm_args/`
- `HomeDashboardActivity.onCreate` returns
- latest `Failed requirement` count is `0`
- sparse frame remains:
  `dashboard-first bytes=191 views=20 texts=0 buttons=0 images=1`
- active frontier: Realm `class_KeyValueStore` and `class_BaseCart`
  table/query/result/row semantics

Acceptance:

- stock McD APK logic runs inside Westlake guest `dalvikvm`, not phone ART
- dashboard screenshot visibly improves beyond sparse shell
- target frame stats: `bytes > 5000`, `views > 80`, `texts > 10`,
  `images > 2`
- at least one ADB-driven dashboard scroll/click proof
- no fatal signal, JNI fatal, ULE, or latest `Failed requirement` in proof
  window
- Android bionic proof plus OHOS/musl runtime link or symbol gate from same
  source
- every new southbound exposure has an OHOS adapter path or explicit open gap
```

## PF-603: Portable Realm Table/Query/Result/Row State Machine

Labels: `westlake`, `real-mcd`, `realm`, `southbound`

Body:

```markdown
Implement the targeted portable Realm/storage boundary required by the stock
McDonald's dashboard path.

Baseline:

- latest proof:
  `artifacts/real-mcd/20260430_164915_justflip_config_realm_args/`
- observed tables: `class_KeyValueStore`, `class_BaseCart`
- observed predicates: `_maxAge < $0`, `_maxAge != $0`, `key = $0`,
  `cartStatus = $0`
- current gap: table/query/result/row handles and getters are mostly
  diagnostic no-op, zero, or empty.

Write scope:

- runtime Realm/native boundary code, currently around
  `/home/dspfac/art-latest/patches/runtime/interpreter/interpreter_common.cc`,
  or a clean helper module called from there.

Required APIs:

- `Property.nativeCreatePersistedProperty`
- `Property.nativeCreatePersistedLinkProperty`
- `OsObjectSchemaInfo.nativeCreateRealmObjectSchema`
- `OsObjectSchemaInfo.nativeAddProperties`
- `OsSchemaInfo.nativeCreateFromList`
- `OsSchemaInfo.nativeGetObjectSchemaInfo`
- `OsRealmConfig.nativeCreate`
- `OsRealmConfig.nativeSetSchemaConfig`
- `OsSharedRealm.nativeGetSharedRealm`
- `OsSharedRealm.nativeGetSchemaInfo`
- `OsSharedRealm.nativeHasTable`
- `OsSharedRealm.nativeBeginTransaction`
- `OsSharedRealm.nativeCommitTransaction`
- `OsSharedRealm.nativeCancelTransaction`
- `OsSharedRealm.nativeIsInTransaction`
- `OsSharedRealm.nativeIsClosed`
- `OsSharedRealm.nativeCloseSharedRealm`
- `OsSharedRealm.nativeGetTableRef(long,String)`
- `OsObjectSchemaInfo.nativeGetProperty(long,String)`
- `Property.nativeGetColumnKey(long)`
- `Table.nativeGetName`
- `Table.nativeGetColumnKey`
- `Table.nativeGetColumnName`
- `Table.nativeGetColumnNames`
- `Table.nativeGetColumnType`
- `Table.nativeWhere(long)`
- `TableQuery.nativeRawPredicate(long,String,long[],long)`
- `TableQuery.nativeValidateQuery`
- `TableQuery.nativeFind`
- `TableQuery.nativeCount`
- `OsResults.nativeCreateResults(long,long)`
- `OsResults.nativeSize(long)`
- `OsResults.nativeGetMode`
- `OsResults.nativeGetRow`
- `OsResults.nativeFirstRow`
- `OsResults.nativeGetTable`
- `Table.nativeGetRowPtr(long,long)`
- `UncheckedRow.nativeIsValid`
- `UncheckedRow.nativeGetObjectKey`
- `UncheckedRow.nativeGetLong(long,long)`
- `UncheckedRow.nativeGetString(long,long)`

Initial data strategy:

- `class_KeyValueStore`: `_createdOn long`, `_maxAge long`, `key string`,
  `value string`
- likely seed candidates: `language=en-US`, `currentAppVersion`,
  `currentAppVersionCode`
- `class_BaseCart`: define full cart columns, but prefer no active row until
  proof shows the dashboard needs active-order state

Acceptance:

- stable pseudo handles for table/property/column/query/result/row
- targeted result sizes, never global positive cardinality
- row getters return non-empty values only for known table/query/column context
- `nativeFind` returns real row ids or `-1`
- `KeyValueStore` and `BaseCart` column keys are stable and non-zero
- dashboard reaches active state and either frame stats improve or next blocker
  is precisely logged
- Android bionic runtime builds and OHOS/musl runtime links or symbol-gates
```

## PF-604: McD Dashboard Visibility, Databinding, And View-Tree Density

Labels: `westlake`, `real-mcd`, `ui`, `reverse-engineering`

Body:

```markdown
Map and close the app-level conditions keeping the real McDonald's dashboard
body sparse or hidden.

Baseline:

- `home=LinearLayout#0x7f0b0ae8` has been observed as `GONE`
- sparse dashboard frame:
  `bytes=191 views=20 texts=0 buttons=0 images=1`

Exploration:

- inspect `HomeDashboardActivity`, `HomeDashboardFragment`, dashboard helpers,
  adapters, ViewModels, generated binding classes, and layouts
- map feature flag/config/data predicates that control home sections
- map data dependencies for guest state, restaurant, menu, deals, cart, and
  image sections

Acceptance:

- exact class/method/resource IDs identified for the visibility gate
- required Realm rows, config keys, or network responses documented
- the next implementation blocker is assigned to Realm, network, UI, or
  runtime with proof lines
```

## PF-605: Real McD Network/Image/Content Transport Boundary

Labels: `westlake`, `real-mcd`, `network`, `images`, `southbound`

Body:

```markdown
Make the real McDonald's dashboard content transport explicit and portable.

Scope:

- outgoing HTTP(S) method, URL, headers, body size, status, response size
- image byte downloads and decoders
- market/config/content APIs
- live endpoint versus URL-keyed fixture/cache fallback when auth blocks

Rules:

- fallback fixtures must feed the app's own network/parser/model path
- no direct UI frame substitution
- TLS/provider/cert-store blockers must be tracked as security/network
  contracts, not hidden by empty data

Acceptance:

- proof logs show app-originated network/image requests through Westlake
- at least one response path feeds app models or image decoding
- OHOS network adapter requirements documented
```

## PF-606: Dashboard UI Rendering, Material/AppCompat Widgets, Scroll/Input

Labels: `westlake`, `real-mcd`, `ui`, `material`, `input`

Body:

```markdown
Make the real McDonald's dashboard View tree measure, lay out, draw, scroll,
and receive input through generic Westlake widget/rendering behavior.

Scope:

- `activity_home_dashboard.xml`
- `fragment_home_dashboard.xml`
- `home_dashboard_section.xml`
- `dashboard_title_section.xml`
- `home_deal_adapter.xml`
- `home_menu_guest_user.xml`
- `home_menu_section_item.xml`
- `fragment_home_dashboard_hero_*`
- TextView/ImageView/Button/ListView/ScrollView/Recycler-like behavior
- Material/AppCompat class ownership and minimal visual semantics

Acceptance:

- frame stats materially improve over sparse baseline
- screenshot visibly shows real app dashboard sections
- ADB touch proves scroll or click into app listeners
- no McD-specific direct frame is counted as success
```

## PF-607: Runtime Stability, Coroutine/Event, CPU, And Cutout Cleanup

Labels: `westlake`, `real-mcd`, `runtime`, `stability`

Body:

```markdown
Keep the runtime stable under McD's Kotlin/coroutine/Rx/AndroidX load and
reduce temporary cutouts.

Scope:

- temporary `JustFlipBase.c(JustFlipFlagEvent)` event-emission shield
- coroutine/SharedFlow event path
- `main-256mb` CPU hot loops
- VarHandle/Unsafe/concurrency regressions
- JNI/native intercepts and OHOS dual-build behavior

Acceptance:

- no fatal crash markers in accepted proof
- CPU does not spin indefinitely in the proof window
- temporary cutouts are narrowed, explained, or replaced by generic behavior
- Android bionic and OHOS/musl runtime gates pass after runtime changes
```

## PF-608: OHOS/Musl Southbound Parity For McD-Critical APIs

Labels: `westlake`, `real-mcd`, `ohos`, `southbound`

Body:

```markdown
Ensure every Android-phone McD success path has a real OHOS/musl portability
story.

Critical API families:

- Realm/storage
- networking/images
- surface/render/input
- filesystem/NIO/storage
- package/build/locale/time/device services
- native loading and APK `.so` containment policy

Acceptance:

- each accepted Android-phone slice names guest API, semantics, Westlake
  boundary, Android adapter, OHOS adapter, failure behavior, and proof
- Android-only delegates remain open issues and are not counted as OHOS
  portability
- OHOS runtime link/symbol gate is recorded for runtime changes
```

## PF-609: Proof Automation And Acceptance Evidence

Labels: `westlake`, `real-mcd`, `qa`, `evidence`

Body:

```markdown
Make the 72-hour dashboard push repeatable and evidence-driven.

Proof loop:

1. build shim and host
2. build Android bionic runtime
3. build or symbol-gate OHOS/musl runtime when runtime code changes
4. push `dalvikvm` and `aosp-shim.dex` to `/data/local/tmp/westlake`
5. launch `com.westlake.host/.WestlakeActivity` with `WESTLAKE_ART_MCD`
6. wait at least 55 seconds after clean install
7. capture screenshot, logcat, process state, hashes
8. run focused grep gate
9. extract dashboard frame stats and compare to baseline
10. drive ADB touch for scroll/click once content is visible

Acceptance artifact:

- `hashes.txt`
- `phone_hashes.txt`
- `logcat.txt`
- focused grep
- `screen.png`
- `screen_after_touch.png` when input is proven
- `am_start.txt`
- `processes.txt`
- frame stats summary

Reject proof if guest process is absent, phone ART executes McD directly,
screenshot is black/white/sparse without log improvement, fatal markers occur,
or hashes do not match staged files.
```
