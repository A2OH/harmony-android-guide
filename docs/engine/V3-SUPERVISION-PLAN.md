# V3 Supervision Plan — dispatch order + dependency DAG

**Date:** 2026-05-16
**Author:** agent 42
**Status:** AUTHORITATIVE for V3 OHOS path
**Companion:** `V3-ARCHITECTURE.md`, `V3-WORKSTREAMS.md`, `CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md`

---

## 0. TL;DR

- **Critical-path week 1:** W1 (HBC artifact pull) → W2 (smoke HBC standalone on DAYU200) → W3 (replace OhosMvpLauncher).
- **Parallelizable from day 1:** W8 (SceneBoard board config — entirely independent), W11 (audit V2 carryforward — read-only), W12 (CR61.1 disposition — most of which is done today by docs landing), W10 (memory refresh).
- **Mid-stream (week 2-3):** W4 (adapter scope diffs), W5 (mock APK), W9 (HBC pattern adoption).
- **End-state (week 3-5):** W6 (noice via V3), W7 (McD via V3), W13 (archive).
- **First-3-day swarm dispatch (§4):** W12 + W11 + W10 to one agent each; W1 to a separate agent (the rest gated on W1).

---

## 1. Dependency DAG

```
W11 (V2 carryforward audit) ──┐
                              │
W12 (CR61.1 disposition) ─────┤
                              ├─→ W10 (memory refresh — needs all docs landed)
                              │
W1 (HBC artifact pull) ─────┐ │
        │                   │ │
        ├─→ W9 (HBC patterns into Westlake tooling)
        │
        ├─→ W2 (HBC standalone on DAYU200)
        │       │
        │       ├─→ W3 (replace OhosMvpLauncher)
        │       │       │
        │       │       ├─→ W5 (mock APK)
        │       │       │       │
        │       │       │       └─→ W6 (noice)
        │       │       │               │
        │       │       │               └─→ W7 (McD)
        │       │       │
        │       │       └─→ W13 (archive V2-OHOS substrate)
        │       │
        │       └─→ W4 (adapter scope diffs) ─→ W6, W7
        │
        └─→ (W9 + W10)

W8 (SceneBoard) — ENTIRELY INDEPENDENT — runs in parallel with all of the above
```

**Critical path (longest dependency chain):** W1 → W2 → W3 → W4 → W6 → W7. Roughly 2-3 + 3-5 + 3-4 + 5-8 + 5-8 + 4-6 = **22-34 person-days** on critical path.

**Total person-days estimated:** 35-55 (across all 13 workstreams). Critical path is ~60-70% of total — so additional swarm agents help significantly but the chain itself constrains the schedule.

---

## 2. Priority ordering

Ranks each workstream `P0` (must-do, blocks everything) → `P3` (nice-to-have, low urgency).

| W | Title | Priority | Why |
|---|---|---|---|
| W12 | CR61.1 amendment landed | P0 | Required for V3 internal consistency. **Mostly done today by the doc-landing commit.** Remaining work = archive moves. |
| W10 | Memory + handoff refresh | P0 | Next agent needs V3 START HERE pointer. Without this, the swarm regresses to V2 thinking. |
| W11 | V2 carryforward audit | P0 | Without this, W3/W13 can't safely archive V2 code (might miss something needed by Android-phone path). |
| W1 | HBC artifact pull | P0 | Blocks W2-W7, W9, W13. The "go/no-go" gate for the whole V3 path. |
| W2 | HBC standalone smoke on DAYU200 | P1 | Proves W1 was complete; foundation for all app-hosting work. |
| W3 | Replace OhosMvpLauncher | P1 | Switches Westlake into HBC's launch model — the architectural pivot in code. |
| W4 | Adapter scope diffs | P1 | Required for W6/W7. Some work may surface during W5 (mock APK) and feedback into W4. |
| W5 | Mock APK validation | P1 | Generic smoke before tackling Hilt (noice) or cross-pkg (McD). Cheap insurance. |
| W6 | noice via V3 | P2 | First "real app" demo via V3. Direct comparable to V2 in-process Option 3 result on phone. |
| W7 | McD via V3 | P2 | Second real app; harder due to cross-package intents. |
| W9 | HBC Tier-1 patterns | P2 | Quality-of-life for V3 team. Can land in parallel with W2/W3. |
| W13 | Archive V2-OHOS substrate | P3 | Cleanup. Worth doing once W1-W3 are green; no rush. |
| W8 | SceneBoard board config | P2 (independent) | Required for the peer-window product goal but **not gating** the V3 stack — V3 runs single-app-foreground in DAYU200 stock config exactly as well as V2-OHOS would have. Schedule independently. |

**Effective dispatch priority for the swarm:**

1. P0 docs (W10, W11, W12 cleanup) — small, parallelizable, agent-friendly.
2. W1 (HBC pull) — gate for everything else; assign a dedicated agent immediately.
3. Once W1 is green (estimated +2-3 days): W2 + W4 (in parallel) + W9 (in parallel).
4. Once W2 is green (estimated +3-5 days after W1): W3.
5. Once W3 is green: W5 + W13 in parallel.
6. Once W5 + W4 are both green: W6.
7. Once W6 is green: W7.
8. W8 runs entirely in parallel from day 1 (dedicated agent if board / device profile decision lands).

---

## 3. Which Ws can run in parallel

| Phase | Parallel set |
|---|---|
| Day 1 (today) | W10, W11, W12 cleanup, W1 — all independent of each other. **W8 also parallelizable** if a separate engineer / agent owns the SceneBoard decision. |
| Day 2-3 | W1 still in flight; W10/W11/W12 wrapping. W8 continues. |
| Day 4-7 (after W1) | W2, W4, W9 — parallel. W8 continues. |
| Day 8-10 (after W2) | W3 + (W4, W9 still in flight or finishing). W8 continues. |
| Day 11-14 (after W3) | W5 + W13. W4 finishing. W8 continues. |
| Day 15-22 (after W5, W4) | W6. W13 finishing. W8 still continues. |
| Day 23-28 (after W6) | W7. W8 still continues (or has completed). |

Wall-clock: roughly **4-6 weeks** if the swarm is well-coordinated and HBC's runtime is as well-baked as CR-EE / CR-FF imply. Conservatively budget **6-8 weeks** for first noice + McD via V3 milestone.

This is comparable to CR-DD's 4-5 week Candidate C MVP estimate but with a stronger architectural foundation (real HWUI, real Skia, real framework.jar) and a clearer path to the peer-window goal (via W8 SceneBoard work which is independent).

---

## 4. Suggested first-3-day swarm dispatch

Assume swarm size ~4 agents working in parallel.

| Agent | Days 1-3 task | Deliverable |
|---|---|---|
| Agent A | W1 — HBC artifact pull (full scope) | `third_party/hbc-runtime/` tree + MANIFEST.md + PROVENANCE.md + pull script. **Critical gate.** |
| Agent B | W11 — V2 carryforward audit (read-only) | `V3-V2-CARRYFORWARD-AUDIT.md` — informs W3/W13 archive safety. ~1-2 PD, can finish day 2 then start W9 prep work. |
| Agent C | W10 — Memory + handoff refresh + W12 — CR61.1 downstream disposition | Updated `MEMORY.md`, new `project_v3_direction.md`, new `handoff_2026-05-16.md`, archive moves under `archive/v2-ohos-substrate/`. ~2 PD. |
| Agent D | W8 — SceneBoard decision investigation | `W8-SCENEBOARD-DECISION.md` — does Westlake enable SCB on DAYU200, or target a different OHOS device profile? This is research / strategic, not coding — perfect for day 1-3 of an agent who doesn't have device access yet. |

End-of-day-3 expected state: W1 80%+ complete, W10/W11/W12 done, W8 decision drafted. Agent A continues W1 finalization on day 4; agents B/C pick up W4 / W9 in parallel on day 4 once W1 fully lands; agent D either continues W8 or rotates onto W2 if device access is available.

---

## 5. Acceptance gates (when can each W close?)

Each W has its own acceptance criteria in `V3-WORKSTREAMS.md` §<N>. The supervision-level "gates" below capture *cross-W* validation needs:

| Gate | Triggered when | Validates |
|---|---|---|
| **G1: V3 docs landed** | W10 + W12 docs side complete | Memory points at V3; CR61.1 amendment readable; handoff exists |
| **G2: HBC artifacts ingested** | W1 acceptance all green | Westlake has all binary + source needed to attempt boot |
| **G3: HBC standalone runs** | W2 acceptance all green | Pure HBC stack reaches MainActivity.onCreate on our DAYU200 — proves W1 wasn't missing anything load-bearing |
| **G4: Westlake launches HBC-runtime apps** | W3 acceptance + W5 acceptance | V3 stack hosts a generic Android app end-to-end; OhosMvpLauncher gone |
| **G5: Real-app parity with V2-on-phone** | W6 acceptance | noice via V3 reaches the same UI surface as V2 in-process Option 3 reached on Android phone |
| **G6: Hard app via V3** | W7 acceptance | McD via V3 reaches Wi-Fry offline screen with cross-pkg intent rewriting working |
| **G7: V2-OHOS substrate archived** | W13 acceptance | Default build doesn't touch V2-OHOS code; Android-phone V2 still 14/14 PASS |
| **G8: Peer-window UX** | W8 acceptance | Two V3-hosted Android apps visible simultaneously (the product goal) |

G1-G7 are V3-internal gates. G8 is the Westlake product gate; it's intentionally decoupled from V3 because solving it is a board-config problem, not a runtime-substrate problem.

---

## 6. Reporting cadence

Per existing project discipline (memory + handoff system):

- **Daily:** swarm-status thread updated by each in-flight agent with their W and current sub-step
- **Per-W completion:** the completing agent produces an `artifacts/v3/w<N>-<short>/CHECKPOINT.md` matching the W's acceptance criteria, and runs the W's self-audit gate
- **Per-G gate completion:** a brief `docs/engine/V3-GATE-<G>.md` summary doc with screenshots / hilog cite / 14/14 status as applicable
- **Weekly handoff:** the supervising agent (or rotating role) writes `handoff_2026-05-<DD>.md` consolidating state, updating `MEMORY.md` "START HERE" pointer

If a W produces unexpected blockers (e.g., HBC artifact has missing symbol that breaks on our DAYU200), the agent reports back rather than improvising. Per CR-EE §11.7: "blame adapter first" RCA discipline — suspect Westlake adapter customization → cross-compile flags → HBC adapter → AOSP → OHOS, in that order. Never editor-edit HBC source.

---

## 7. Risks of the V3 pivot

Listed in agent 42's honest assessment for the supervision plan:

1. **HBC's HelloWorld result is a single-Activity APK with no cross-pkg intent, no Hilt, no Fragment nav. Westlake-side apps (noice, McD) exercise dramatically more of the framework surface than HBC has validated.** Adapter scope diffs (W4) is the workstream where this risk materializes. Mitigation: W5 mock APK validates generic Android first; W6 (noice) is gated on W4 + W5 finishing.

2. **HBC's `liboh_android_runtime.so` register_* stubs (13/15 stubs per CR-EE §12-item-3).** Many native callbacks return safe defaults rather than doing real work. Apps relying on those will silently misbehave. V3 must validate every register_* HBC stubs that our hosted apps hit — likely surfaces during W6/W7 as "feature X doesn't work, why."

3. **HBC's adapter is moving target.** HBC is actively iterating (we audited 2026-05-15). Their build_patch_log.html has "ongoing, hundreds of entries" per CR-EE App. B. If we pull artifacts once at W1 and freeze, we miss their fixes. If we re-pull continuously, we may break our integration. **Mitigation:** W1 manifests HBC version; W4 documents diffs that need upstreaming; periodic re-pull cadence to be defined in W9's RCA-discipline doc. Suggested: re-pull on a fixed cadence (monthly?) or on demand when HBC fixes a bug we hit.

4. **W4 "shadow class" pattern (PathClassLoader-loaded `oh-adapter-runtime-westlake.jar`) is a class of work we don't have hands-on experience with on HBC's actual stack.** The dual-classloader pattern works in HBC's tests; we'll only learn its sharp edges by exercising it ourselves. Mitigation: W4 acceptance includes "at least one shadow class actually loads and gets invoked." Bias toward consume-as-is dispositions; only shadow when blocking.

5. **DAYU200 hardware bottleneck.** Per MEMORY.md, rk3568 is the only OHOS hardware Westlake has access to. SceneBoard off + single-foreground constraint applies equally to V3 as it did to V2-OHOS. **Mitigation:** W8 is independent and can run in parallel; if it identifies a different device profile as the right target, W6/W7 can be re-validated there.

6. **HBC's deploy is 94 files + 4 symlinks per push.** Iteration velocity is slow if we have to deploy that frequently. **Mitigation:** W9 ports HBC's restore_after_sync.sh + DEPLOY_SOP.md (incremental deploy + verify) into our tooling. CR-FF Pattern 2 (dual-classloader adapter in non-BCP jar) means Westlake adapter iteration costs 0 boot-image rebuild — a major velocity win we inherit.

7. **The "this is a pivot from a pivot" risk.** Westlake just yesterday committed to V2-OHOS direction (`project_v2_ohos_direction.md` 2026-05-15 morning), and is now flipping to V3 (2026-05-16). That's two strategic pivots in two days. **Mitigation:** Both pivots are well-justified (V2→V3 driven by CR-EE / CR-FF empirical evidence; V1→V2 driven by additive-shim audit). The `feedback_additive_shim_vs_architectural_pivot.md` rule explicitly encourages this when the previous direction's premise is falsified by new evidence. CR-DD itself proposed HYBRID for similar reasons. V3 is the resolution that earlier CRs were converging toward; not a new direction at the third level of recursion.

8. **The pivot abandons substantial V2-OHOS investment.** Per V3-ARCHITECTURE §4, ~70K+ LOC of substrate work + ~16K LOC of daemon work is archived (not deleted, but no longer the production path). Some of that was educational; some was throwaway per the additive-shim feedback. **Mitigation:** V3 explicitly preserves the V2 *learnings* (5-pillar pattern, CR59 lifecycle drive insight, macro-shim contract, CR60 bitness discipline) — only the *artifacts* are archived. The conceptual learnings carry forward as cited in V3-ARCHITECTURE.md §5.2.

9. **HBC isn't ours.** We coordinate via reading their work tree; we don't have engineering authority over their backlog. If they pivot away from this stack themselves, we inherit the orphan-fork problem. **Mitigation:** W9's RCA discipline + W4's upstream-request mechanism keep us in sync. Worst case: we end up maintaining HBC's artifacts ourselves, which is still less work than V2-OHOS would have been because the engineering is already done.

10. **APK transparency may not survive our app-hosting engine integration.** HBC enforces zero `import adapter.*` in APK source; we add a Westlake engine layer above. If our engine requires APK-side cooperation (manifest aliases, etc.), we violate the invariant. **Mitigation:** V3-ARCHITECTURE §3 footnote on "per-app diff = 4 constants + manifest aliases" preserves the V2 contract — APKs unmodified, manifest aliases handled in our packaging tooling. If a future app forces us to break this, that's a CR.
