# Westlake Binder Pivot — Agent Swarm Playbook

**Companion to:** `BINDER_PIVOT_DESIGN.md`, `BINDER_PIVOT_MILESTONES.md`
**Audience:** the agents executing this pivot, and any human reviewing their work.

This document codifies how multiple agents coordinate on the binder pivot. The pattern of the previous week (additive shimming, renderer-time bypasses, per-app hacks) is what we are explicitly NOT doing. Every agent must read this before claiming a milestone.

---

## 1. The Coordination Model

### 1.1 Single source of truth

- **Design decisions:** `BINDER_PIVOT_DESIGN.md`. Do not relitigate. If an agent thinks the design is wrong, raise it in their report; do not unilaterally deviate.
- **Work plan:** `BINDER_PIVOT_MILESTONES.md`. Each milestone is the unit of claim. Agents own one M-task at a time.
- **Task tracker:** the in-conversation `TaskCreate`/`TaskUpdate` system. Each M-task has a tracker entry; agents set `owner` when claiming, `status=in_progress` while working, `status=completed` only when acceptance test passes.

### 1.2 Roles

There are three roles. An agent occupies exactly one role per dispatch:

| Role | Purpose | Tool budget |
|---|---|---|
| **Builder** | Implement an M-task: write code, run tests, push artifacts | Full file edit + bash + build/deploy |
| **Validator** | Run acceptance tests and regressions; report pass/fail | Read-only file + bash + adb |
| **Architect** | Read-only audits, design docs, scoping reports | Read + WebFetch, no code edits |

The orchestrator (the parent conversation) dispatches each agent with explicit role and milestone identification.

### 1.3 Parallelism

Independent M-tasks can run in parallel. The dependency graph in `BINDER_PIVOT_MILESTONES.md` §0 shows which tasks block which. Concretely:

**Can parallelize:**
- Phase 1 cleanup (C1, C2, C4, C5) — entirely independent of foundation work
- M4 sub-milestones (M4a, M4b, M4c, M4d, M4e) — each is a separate service
- M5 and M6 — different daemons

**Must sequence:**
- M1 → M2 → M3 (libbinder, then servicemanager, then dalvikvm wiring)
- M3 → any M4 (services need the binder boot path)
- M4+M5+M6 → M7 (e2e needs the components)

Maximum useful parallelism: 5 agents (M4a, M4b, M4c, M4d, M4e simultaneously) after M3 completes, or 4 agents (C1, C2, M1, audits) at the start.

### 1.4 Communication

- **Agent → Orchestrator:** the agent's final report (tool result). This must include:
  - What changed (file paths + brief description)
  - What tests ran + their outcomes
  - What was deferred or left for follow-up
  - One paragraph of "if I were the next agent, here's what I'd watch for"
- **Agent → Agent:** never directly. The orchestrator coordinates. If agent A's output is needed by agent B, the orchestrator hands it off explicitly in B's prompt.

### 1.5 Handling disagreement

If an agent believes the design or a milestone's acceptance criteria are wrong:
1. Do not deviate. Implement to the spec.
2. Document the disagreement in the final report under a `## Concerns` section.
3. The orchestrator will decide whether to update the design or proceed.

---

## 2. Per-Agent Briefing Template

Every agent dispatch must use this template. Missing fields are bugs in the dispatch, not in the agent.

```
ROLE: <Builder | Validator | Architect>
MILESTONE: <M-id from BINDER_PIVOT_MILESTONES.md>
GOAL: <one sentence, copied from the milestone's "Goal" field>

CONTEXT (the agent has not seen the conversation):
<2-3 paragraphs explaining where Westlake is, why this milestone exists,
 what came before. Include any environmental specifics — phone serial,
 paths to existing artifacts, etc.>

DELIVERABLES:
<bulleted list, copied from the milestone>

ACCEPTANCE TEST (explicit commands the agent must run):
<shell commands the agent runs to verify acceptance; what passing looks like>

FILES TO TOUCH:
<allowlist of paths the agent may modify>

FILES NOT TO TOUCH:
<denylist; common entries: shim/java/android/view/View.java (AOSP source),
 WestlakeLauncher.java (until M4 done), framework.jar, ART patches>

ANTI-PATTERNS (do not):
- Per-app branches (no `if (className.equals("com.example.X"))`)
- Reflection on framework objects when a Stub.asInterface path exists
- Renderer-time substitution (do not call into anything from WestlakeRenderer)
- Adding fields/seeders to substitute for missing services — implement the service
- Speculative implementation (only what the acceptance test requires)

TOOLS:
- Pixel 7 Pro at adb -s cfb7c9e3 (host: /mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037)
- Build scripts: /home/dspfac/android-to-openharmony-migration/scripts/
- AOSP source (clone if needed): https://android.googlesource.com/platform/frameworks/native
- Dex tools: /home/dspfac/android-sdk/build-tools/34.0.0/dexdump
- Existing test scripts: scripts/run-noice-phone-gate.sh, scripts/check-real-mcd-proof.sh

REPORT BACK:
- Diff summary (files changed, lines added/removed)
- Acceptance test transcript (stdout + exit codes)
- Any new follow-up tasks discovered (don't auto-create; just list)
- Concerns about the design if any
- Token budget used (approx)
```

---

## 3. Anti-Patterns (Codified from the past week's mistakes)

### 3.1 Additive shimming

**Symptom:** observe NPE → add a class to satisfy the missing reference → observe next NPE → repeat.

**Why it's wrong:** the shim landscape grows linearly with app coverage. Each new app needs ~5–10 new shims. After 10 apps, the shim layer is unmaintainable.

**Right reflex:** when an NPE happens, identify which service that path should reach. Make the service work. Do not class-shim the immediate caller.

**Concrete:** the recent task #97 (enum seeding in `Fragment.mMaxState`) is the anti-pattern. The right fix would have been "FragmentManager should populate mMaxState via its standard flow"; the wrong fix (that was applied) was "shim the enum to enum.values()[0]." Don't do the wrong fix.

### 3.2 Renderer-time substitution

**Symptom:** `WestlakeRenderer` calls into anything other than primitive draw operations on the View tree.

**Why it's wrong:** the renderer is a passive consumer. If the View tree it receives is wrong, the bug is upstream. Substituting at render time creates a parallel reality (e.g., the View tree the framework built vs. the View tree the renderer "fixes up").

**Right reflex:** if the renderer encounters a malformed input, log it and abort the frame. Investigate upstream.

### 3.3 Per-app hardcoded shortcuts

**Symptom:** `if (className.equals("com.foo.Bar"))`, `if (resourceId == 0x7f0b1234)`, `if (packageName.startsWith("com.mcdonalds"))`.

**Why it's wrong:** every per-app branch is technical debt that future apps will re-trigger or work around. Documented violation: `feedback_no_per_app_hacks.md`.

**Right reflex:** if app X needs behavior Y, ask: "what generic Android API does X use to request Y?" Implement that API correctly. Never branch on app identity.

### 3.4 Reflection on framework objects

**Symptom:** `Unsafe.allocateInstance(Fragment.class)`, `someField.setAccessible(true)`, manual field seeding loops.

**Why it's wrong:** framework objects have invariants their constructors and standard paths uphold. Reflection bypasses those invariants and creates corrupted state. Recent example: 73 fields seeded by reflection in `WestlakeFragmentLifecycle` because Unsafe-allocated Fragments had null collections.

**Right reflex:** instantiate framework objects through their public constructors or factory methods. If a public path is missing, implement the missing service or factory — don't reflect around it.

**Exception:** reflection on *app-defined* extension points (e.g., manifest-declared Activity class names) is fine. That's the framework's own pattern.

### 3.5 Speculative completeness

**Symptom:** an agent implements every method of `IActivityManager.Stub` "to be safe."

**Why it's wrong:** unverified code is buggy code. Speculative implementations drift from AOSP semantics and accumulate dead code.

**Right reflex:** implement only what an observed trace demands. The acceptance test for each M-task tells you the minimum surface; don't expand it.

### 3.6 Skipping the acceptance test

**Symptom:** an agent reports "milestone completed" without running the test.

**Why it's wrong:** undermines the entire validation premise. The pivot only works if every claim is verified.

**Right reflex:** if the acceptance test can't be run (environment issue, blocker), report `status=in_progress` with the blocker explained — never `status=completed`.

---

## 4. Tool Inventory

### 4.1 Phone access

```bash
# ADB invocation (Windows host, WSL)
ADB="/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3"

# Verify connectivity before doing real work
$ADB shell echo OK
```

### 4.2 Source paths

```bash
# Westlake codebase
cd /home/dspfac/android-to-openharmony-migration

# Existing build output (look here for current artifacts)
ls out/

# AOSP source — clone on first use into a separate tree
mkdir -p ~/aosp-sources && cd ~/aosp-sources
# (Use specific paths from https://android.googlesource.com/platform/ —
#  do NOT do `repo sync` for the whole thing; just clone what you need)
```

### 4.3 Build invocations

```bash
# Build shim DEX
bash /home/dspfac/android-to-openharmony-migration/scripts/build-shim-dex.sh

# Push current artifacts to phone
bash /home/dspfac/android-to-openharmony-migration/scripts/sync-westlake-phone-runtime.sh

# Smoke-test McDonald's regression
bash /home/dspfac/android-to-openharmony-migration/scripts/check-real-mcd-proof.sh

# Smoke-test noice
bash /home/dspfac/android-to-openharmony-migration/scripts/run-noice-phone-gate.sh
```

### 4.4 Dex inspection

```bash
# Decode an APK's bytecode
/home/dspfac/android-sdk/build-tools/34.0.0/dexdump -d <file.dex> > /tmp/dump.txt
grep -nA 5 'methodName' /tmp/dump.txt
```

### 4.5 Logcat utilities

```bash
# Capture and search
$ADB logcat -d > /tmp/lc.txt
grep -nE 'WestlakeVM|MCD_APP_PROOF|PF-arch' /tmp/lc.txt

# Live tail filtered to our process
$ADB logcat | grep -E 'WestlakeVM:|WestlakeFM|MCD_'
```

### 4.6 Sandbox setup (for M1–M3 testing)

```bash
# Pre-check userns availability
$ADB shell cat /proc/sys/kernel/unprivileged_userns_clone
# 1 = available; 0 = need root

# Pre-check binderfs availability
$ADB shell ls /sys/kernel/config/binder 2>&1
# Look for: 'devices/' subdirectory available

# (Sandbox boot script will be created in M3)
```

---

## 5. Verification Checklist (run before reporting "completed")

For every milestone:

- [ ] Code compiles (build script exits 0)
- [ ] Code passes any linters or formatters specified in the milestone
- [ ] Acceptance test commands run and produce expected output
- [ ] McDonald's regression still passes (run `check-real-mcd-proof.sh`)
- [ ] No new per-app branches introduced (grep for app/package strings in changed files)
- [ ] No new reflection on framework objects (grep `Unsafe\.allocateInstance\|setAccessible(true)`)
- [ ] No new `WestlakeFragmentLifecycle` or `WestlakeRenderer`-side dispatch (these are forbidden post-C1)
- [ ] Final report includes: changed files, test transcript, follow-up items, token usage
- [ ] Tracker task updated: `status=completed`, owner set

If any checkbox is unclear or fails, leave the task `in_progress` and report blocker.

---

## 6. Escalation Path

If during an M-task an agent discovers:

- **A bug in another M-task's deliverable** that blocks progress → report in final message; do not "fix in passing" (creates ownership confusion). Orchestrator dispatches a follow-up agent to the owning task.
- **A design assumption that turns out wrong** → halt work, report in `## Concerns`, leave task `in_progress`. Orchestrator updates design.
- **An OHOS-team-required dependency** (e.g., binder.ko inclusion) → tag the task with `blocked-by-platform`, report which decision is needed.

---

## 7. Phase 1 Kickoff Recipe (for the first agent batch)

The recommended first parallel batch (3 agents) is:

```
Agent A: C1 (Builder) — Remove WestlakeFragmentLifecycle bypass + DexLambdaScanner.
         Safe pure deletion, validates that the existing FragmentManager flow compiles
         and McDonald's still runs.

Agent B: C2 (Builder) — Remove MCD constants from FragmentTransactionImpl.java and
         FragmentManager.java. Leaves WestlakeLauncher/MiniActivityManager MCD
         branches alone (those need M4 first).

Agent C: M1 prep (Architect) — Locate AOSP libbinder source (~/aosp-sources), produce
         a build-scaffold proposal: which files, which musl patches, expected
         output. NO code yet — just a buildable plan in
         aosp-libbinder-port/BUILD_PLAN.md.
```

After A+B+C complete:
- A's deletion is verified by C1's acceptance test.
- B's MCD removal is verified by McD regression still passing.
- C's plan is reviewed; M1 is then dispatched as Builder agent with the plan.

This decouples "is the engine still alive without the bypass?" (A) from "can we even build the binder port?" (C). If A fails, we know the engine couldn't survive without the bypass and the design needs revisiting. If C reveals a 4-week musl port effort, we know the timeline upfront.

---

## 8. Living Document

This playbook is updated whenever:
- A new anti-pattern is discovered (add to §3)
- A new tool is needed (add to §4)
- Milestone dependencies shift (update §1.3 and the milestones doc)

Updates require commit to the tree. Do not let this drift from reality.
