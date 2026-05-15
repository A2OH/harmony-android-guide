# Westlake Real McD 72-Hour Agent Prompts

Prepared: 2026-04-30 PT

Use this file to start subagents with clear ownership. Workers are not alone in
the codebase: they must not revert edits made by others, must keep changes
inside their write scope, and must adjust their work to fit accepted changes
from other workers.

Baseline before any worker starts:

- runbook: `docs/program/WESTLAKE_REAL_MCD_72H_DASHBOARD_PLAN_20260430.md`
- southbound contract: `docs/program/WESTLAKE_SOUTHBOUND_API.md`
- handoff: `docs/program/WESTLAKE_REAL_MCD_AGENT_HANDOFF_20260430.md`
- issue map: `docs/program/WESTLAKE_PLATFORM_FIRST_ISSUES.md`
- parent issue: `A2OH/westlake#575`
- baseline proof:
  `artifacts/real-mcd/20260430_164915_justflip_config_realm_args/`
- phone: `cfb7c9e3`, ADB server `localhost:5037`
- latest sparse frame:
  `dashboard-first bytes=191 views=20 texts=0 buttons=0 images=1`

## Supervisor Rules For Every Agent

- Do not claim success unless stock `com.mcdonalds.app` runs inside Westlake
  guest `dalvikvm`, not phone ART.
- Do not add a direct fake McD dashboard frame and call it success.
- Do not globally fake Realm result sizes.
- Keep Android phone proof and OHOS/musl portability separate.
- Record artifact paths, hashes, logs, screenshots, and focused grep markers.
- If blocked, return the smallest next actionable blocker with file/method
  references.

## Worker A Prompt: Realm/Storage

Issue: `PF-603`, `A2OH/westlake#576`

Write scope:

- `/home/dspfac/art-latest/patches/runtime/interpreter/interpreter_common.cc`
- a new helper module under `/home/dspfac/art-latest/patches/runtime/` only if
  the Realm state machine becomes too large for the interpreter file.

Prompt:

```text
You own the portable Realm/storage boundary for the 72-hour real-McD dashboard
push. Read the runbook, southbound contract, handoff, and issue #576 first.
Do not revert unrelated edits. Implement the smallest next slice of the Realm
state machine and keep it source-built for Android bionic and OHOS/musl.

Start with schema/property/table/query/result/row handle tracking. Required
frontier tables are class_KeyValueStore and class_BaseCart. Required predicates
are _maxAge < $0, _maxAge != $0, key = $0, and cartStatus = $0.

First deliverable:
- stable non-zero table/property/column/query/result/row handles;
- PFCUT-REALM-STATE logs showing handle lineage;
- no global positive nativeSize fake;
- unknown tables return correct empty semantics plus diagnostics.

Then implement targeted KeyValueStore rows:
- _createdOn long;
- _maxAge long;
- key string;
- value string;
- seed only language=en-US/currentAppVersion/currentAppVersionCode unless logs
  prove other keys are needed.

Prefer no active BaseCart row until proof shows the dashboard needs active
order state.

Acceptance:
- bionic runtime builds;
- OHOS runtime links or symbol-gates;
- phone proof reaches HomeDashboardActivity active with Failed requirement=0;
- row getter logs return named non-empty values for known columns or identify
  the next missing Realm API.
```

## Worker B Prompt: McD Dashboard Reverse Engineering

Issue: `PF-604`, `A2OH/westlake#577`

Write scope:

- read-only by default;
- docs updates only if assigned by supervisor.

Prompt:

```text
You own reverse engineering the real McDonald's dashboard visibility and data
preconditions. Read the runbook, handoff, issue #577, and decompiled McD tree.
Do not edit runtime or shim code.

Find the exact path that should turn home_dashboard_container 0x7f0b0ae8 from
GONE to VISIBLE:
- showHome;
- validateAndNavigate;
- navigate(HOME);
- showOrHideHomeDashboardFragment(true).

Then map HomeDashboardFragment population:
- onCreateView/onViewCreated;
- section builder methods around sections_container;
- adapters and generated binding classes;
- config/Realm/network data needed for HERO/MENU/DEALS/restaurant/cart.

Return:
- class/method names;
- resource IDs/layout names;
- required Realm rows/config keys/network responses;
- proof log markers to add;
- exact next owner: Realm, network, UI, or runtime.
```

## Worker C Prompt: Dashboard UI/Rendering/Input

Issue: `PF-606`, `A2OH/westlake#579`

Write scope:

- `/home/dspfac/android-to-openharmony-migration/shim/java/android/view/`
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/widget/`
- Material/AppCompat shim classes only when the issue requires it.

Prompt:

```text
You own making the real McD app-owned View tree measure, lay out, draw, scroll,
and receive input. Read the runbook, issue #579, and current shim code. Do not
draw a synthetic McD dashboard frame.

First instrument:
- Fragment transactions: class, container id, resolved container, view class,
  child count;
- LayoutInflater: layout id/name/path for fragment_home_dashboard,
  home_dashboard_section, hero/menu/deals layouts;
- View visibility, requestLayout, invalidate, addView, adapter bind, and
  final bounds;
- strict frame text/button/image counts plus first visible text snippets.

Then implement generic missing behavior for:
- RecyclerView/NestedScrollView/FragmentContainerView mapping;
- TextView/ImageView/Button/ListView/ScrollView basics;
- Material/AppCompat class ownership and minimal visual semantics;
- post-add relayout and invalidation.

Acceptance:
- frame stats improve over baseline;
- screenshot shows app-owned McD dashboard sections;
- ADB touch proves scroll or click reaches app listener.
```

## Worker D Prompt: Network/Image/Content

Issue: `PF-605`, `A2OH/westlake#578`

Write scope:

- `/home/dspfac/android-to-openharmony-migration/shim/java/android/net/`
- `NetworkBridge` and host/OHBridge network adapter code.

Prompt:

```text
You own the real McD network, image, and content transport boundary. Read the
runbook, southbound contract, and issue #578. Do not bypass app models with
direct UI frames.

Instrument outgoing requests:
- method;
- URL;
- headers;
- body size;
- status;
- response size;
- timeout/error;
- image byte decode/render path.

Decide whether dashboard content is local config, Realm cache, live REST, or
both. If live endpoints require auth/anti-abuse state, propose a URL-keyed
fixture/cache fallback that feeds the real app network/parser/model path.

Acceptance:
- proof logs show app-originated network/image requests;
- at least one response feeds an app model or image decoder;
- OHOS network adapter requirements are documented.
```

## Worker E Prompt: Runtime Stability

Issue: `PF-607`, `A2OH/westlake#580`

Write scope:

- `/home/dspfac/art-latest/patches/runtime/`
- generic libcore/native stubs only when needed for Java/runtime behavior.

Prompt:

```text
You own runtime stability for the real-McD dashboard push. Read the runbook,
handoff, and issue #580. Do not broaden app-specific noops.

Focus:
- diagnose or narrow the temporary JustFlipBase.c(JustFlipFlagEvent) shield;
- inspect coroutine/SharedFlow event behavior;
- monitor main-256mb CPU hot loops;
- keep Unsafe/VarHandle/concurrency behavior generic;
- ensure every runtime change builds for Android bionic and OHOS/musl.

Acceptance:
- no fatal/JNI/ULE markers in accepted proof;
- CPU does not spin indefinitely;
- cutouts are narrowed, explained, or replaced by generic behavior;
- symbol gates pass.
```

## Worker F Prompt: OHOS/Southbound Parity

Issue: `PF-608`, `A2OH/westlake#581`

Write scope:

- `docs/program/WESTLAKE_SOUTHBOUND_API.md`
- OHOS adapter docs/scripts only when assigned.

Prompt:

```text
You own OHOS/musl portability discipline for the real-McD dashboard push. Read
the southbound contract, runbook, and issue #581.

For every Android-phone success path, record:
- guest API;
- Android semantics;
- Westlake boundary;
- Android proof adapter;
- OHOS adapter path;
- failure behavior;
- evidence.

Focus first on Realm/storage, network/images, surface/input, filesystem/NIO,
package/build/locale/time/device services, and native loading policy.

Acceptance:
- no Android-only delegate is counted as OHOS portability;
- arbitrary APK .so loading remains PF-494 debt unless explicitly solved;
- southbound doc is current after each accepted slice.
```

## Worker G Prompt: QA/Proof Automation

Issue: `PF-609`, `A2OH/westlake#582`

Write scope:

- proof scripts;
- artifact indexing;
- docs evidence summaries.

Prompt:

```text
You own proof automation for the 72-hour real-McD dashboard push. Read issue
#582 and the runbook. Do not change runtime/shim behavior unless assigned.

Build a repeatable proof loop:
1. build shim and host;
2. build Android bionic runtime;
3. build or symbol-gate OHOS/musl runtime when runtime code changes;
4. push dalvikvm and aosp-shim.dex to /data/local/tmp/westlake;
5. launch com.westlake.host/.WestlakeActivity with WESTLAKE_ART_MCD;
6. wait at least 55 seconds after clean install;
7. capture screenshot, logcat, process state, hashes;
8. run focused grep gate;
9. extract frame stats and compare to baseline;
10. drive ADB touch for scroll/click once content is visible.

Reject proof if:
- guest process is absent;
- phone ART executes McD directly;
- screenshot is black/white/sparse without log improvement;
- fatal markers occur;
- hashes do not match staged files.
```

