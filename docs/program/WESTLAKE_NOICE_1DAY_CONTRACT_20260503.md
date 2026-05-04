# Westlake — Noice (Pure-UI App) 1-Day Swarm Contract

**Date:** 2026-05-03 PT (forward plan for next session)
**Companion doc:** `WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md` §14.5 (pivot rationale)
**Repo:** `https://github.com/A2OH/westlake.git`
**Phone:** OnePlus 6 `cfb7c9e3` (LineageOS 22 / Android 15, rooted, SELinux permissive)
**Status:** ready-to-execute. Each `### Agent N` block is a self-contained brief.

---

## 1. Goal

Run the open-source **Noice** Android app (`com.github.ashutoshgngwr.noice`, F-Droid distribution) inside a Westlake guest `dalvikvm` on the OnePlus 6 phone, in **one focused day** (~8 hours of agent + phone time).

**Why this app:** ambient-sound app with simple architecture — material UI, sliders/lists, in-app sound assets, foreground media service. Zero auth/payment/push/Realm/Firebase/GMS dependencies. See `WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md` §14.5 for the comparison-with-McD rationale.

**Scope discipline:** "render UI + characterize the audio gap" is in scope. "Get sound playing" is **explicitly NOT** in scope for day 1 (audio bridge is its own multi-day workstream). See §6 out-of-scope list.

---

## 2. Acceptance criteria (measurable, day-1)

A successful day-1 produces an artifact directory `artifacts/noice-1day/<timestamp>_noice_day1_final/` with:

| # | Criterion | Marker / file in artifact |
|---|---|---|
| **A1** | Noice APK loaded into Westlake guest `dalvikvm` (not phone-host ART) | `westlake_subprocess_purity host_pid=N vm_pid=M direct_noice_processes=0` in `check-noice-proof.txt` |
| **A2** | Noice `MainActivity` reaches `onResume` inside guest | logcat marker `NOICE_MAIN_ACTIVITY phase=resumed` |
| **A3** | At least one main-screen view (sound card) inflated with non-zero size | `NOICE_VIEW_INFLATED type=<view-class> w=N h=N` count ≥ 5 |
| **A4** | At least one tap is dispatched to a sound-card and routed to its click handler | `NOICE_SOUND_CARD_CLICK invoked=true sound=<id>` count ≥ 1 |
| **A5** | Audio gap characterized empirically | `noice_audio_gap.txt` lists every `MediaPlayer`/`AudioTrack`/`AudioManager` JNI call observed and its return value (expected: stubs returning 0/null) |
| **A6** | Screenshot proves UI rendered | `screen.png` shows recognizable Noice main screen |
| **A7** | No fatal/SIGBUS during a 5-minute idle soak after first interaction | `noice_5min_soak_status=PASS` |

**Stretch (only if A1-A7 done early):**
- **A8**: Settings screen navigation works (tap settings icon → settings list inflates → tap any toggle → state persists in process)
- **A9**: Theme variants render (toggle dark mode if accessible without sound-emitting interactions)

**Definition of done:** A1-A7 pass AND a wrap-up doc lands at `docs/program/WESTLAKE_NOICE_DAY1_RESULTS_20260504.md` (replace date with actual run date) with screenshots and the audio-gap characterization. PR-ready.

---

## 3. Day plan (8 hours, hour-by-hour)

| Hour | Phase | Agent | Output |
|---|---|---|---|
| **0–1** | APK acquisition + provenance | Agent 1 (background) | `noice.apk` at `/data/local/tmp/westlake/noice.apk` + sha256 + provenance note |
| **0–2** | Generic gate harness fork | Agent 2 (parallel with Agent 1) | `scripts/run-noice-phone-gate.sh` + `scripts/check-noice-proof.sh` (cloned from McD versions, app-id parameterized) |
| **2–3** | First-boot attempt + framework misses inventory | Foreground (supervisor) | First artifact dir, list of `ClassNotFoundException` / `NoSuchMethodError` from logcat |
| **3–5** | Iterate on framework misses | Agent 3 + supervisor | Each miss → tiny shim addition (or `noice_framework_misses.txt` with deferred list) |
| **5–6** | UI render verification (A1-A4, A6) | Foreground | Tap-routing artifact, screenshot |
| **6–7** | Audio gap characterization (A5) | Agent 4 | `noice_audio_gap.txt` + survey of audio JNI calls invoked |
| **7–8** | 5-min soak (A7) + wrap-up doc + push | Foreground | Final artifact + `WESTLAKE_NOICE_DAY1_RESULTS_*.md` + commits pushed |

**Phone resource is sequential** — only one gate can run at a time. Local agent work is parallel.

---

## 4. Agent swarm — self-contained briefs

Each block below can be pasted into a fresh `Agent` spawn. Each agent's scope is non-overlapping.

### Agent 1 — APK acquisition + analysis

```
You are doing the APK acquisition step for the Westlake noice 1-day proof
(see /home/dspfac/android-to-openharmony-migration/docs/program/
WESTLAKE_NOICE_1DAY_CONTRACT_20260503.md for the full plan).

Goal: produce `noice.apk` (a Noice ambient-sound APK) on the phone at
`/data/local/tmp/westlake/noice.apk` plus a provenance note.

Do NOT modify any code in the repo. Read-only + APK fetch + adb push.

Steps:
1. Find a Noice APK from a trustworthy source. Acceptable sources, in order:
   a. `f-droid.org` (preferred — open-source distribution, signed by F-Droid)
   b. APK already in `/home/dspfac/` or `/tmp/` if present (search first
      with `find /home/dspfac /tmp -maxdepth 4 -iname 'noice*.apk' 2>/dev/null`)
   c. `github.com/trynoice/android-app/releases` (signed by upstream)
2. The package name should be `com.github.ashutoshgngwr.noice` (legacy)
   or `com.trynoice.api.android` (new) — accept either; record which.
3. sha256 the APK; record where it came from (URL or file path) and when.
4. Push to phone at `/data/local/tmp/westlake/noice.apk` via:
     /mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 \
       -s cfb7c9e3 push <local-path> /data/local/tmp/westlake/noice.apk
5. Extract `classes.dex` (or `classes2.dex` etc. if multidex) from the APK
   to `/data/local/tmp/westlake/noice_classes.dex` so the gate harness
   can load it directly.
6. Create `/home/dspfac/android-to-openharmony-migration/artifacts/
   noice-1day/_apk_provenance.txt` with:
   - APK path / URL / source
   - sha256
   - package name (from `aapt dump badging` if `aapt` is available, else
     `unzip -p noice.apk AndroidManifest.xml | head -c 2048` — be aware
     it's binary AXML)
   - dex file count + sizes
   - acquisition timestamp

Constraints:
- Do not push UNSIGNED or random-Internet APKs. F-Droid or upstream GitHub
  releases only.
- Do not run the APK yet; just stage it.

Final report (under 250 words):
- APK path on phone, sha256, source URL, package name
- dex file count and sizes
- Provenance file path
- Any concerns (signed by whom, expected dependencies seen in manifest)
```

### Agent 2 — Generic gate harness fork

```
You are forking the McD gate harness into a Noice gate harness for the
Westlake noice 1-day proof (see WESTLAKE_NOICE_1DAY_CONTRACT_20260503.md
in docs/program/).

Goal: produce `scripts/run-noice-phone-gate.sh` and
`scripts/check-noice-proof.sh` that run an arbitrary APK in Westlake
guest dalvikvm and check generic (not McD-specific) success criteria.

Steps:
1. Read `scripts/run-real-mcd-phone-gate.sh` end-to-end. Identify every
   McD-specific assumption: package name, APK filename, dex filename,
   any MCD_* env vars, any McD-specific tap coordinates.
2. Read `scripts/check-real-mcd-proof.sh` end-to-end. Identify every
   McD-specific marker check (MCD_PDP_*, MCD_DASH_*, etc.).
3. Create `scripts/run-noice-phone-gate.sh` — same shape but:
   - APK_NAME=noice.apk (or take as arg)
   - PACKAGE=com.github.ashutoshgngwr.noice (or take as arg)
   - DEX=noice_classes.dex
   - All MCD-specific env vars dropped
   - Add NOICE_* taps: a generic "first-tap" that taps near center
     (after settle), and a "second-tap" with bounded settle for any
     follow-up. Configurable via NOICE_GATE_TAPS=center|settings|toggle.
4. Create `scripts/check-noice-proof.sh` — same shape but check generic
   markers:
   - westlake_subprocess_purity (parameterized package)
   - proof_real_app_guest_dalvikvm package=$PACKAGE
   - proof_unsafe_flags_off (re-use the existing fix from check-real-mcd-proof.sh)
   - NOICE_MAIN_ACTIVITY phase=resumed (count >= 1)
   - NOICE_VIEW_INFLATED count >= 5
   - NOICE_SOUND_CARD_CLICK invoked=true count >= 1 (only when GATE_TAPS=center)
   - noice_5min_soak_status=PASS (parameterized soak duration via
     NOICE_SOAK_SECONDS, default 300)
5. Both scripts must work even if the APK is something other than noice
   — the only app-specific bit is the $PACKAGE variable.
6. The McD scripts must remain UNCHANGED — these are NEW files.

Where the markers come from: the launcher (WestlakeLauncher.java) emits
markers prefixed by app context. For noice, the launcher needs a
small addition (Agent 3 will handle if needed) to emit `NOICE_*` markers
with the specific shape above. For day 1, if `NOICE_*` markers are
absent, the checker should report A2/A3/A4 as `INCONCLUSIVE` (not FAIL)
and surface the gap.

Make ONE commit per script (2 commits total) with messages:
  Add scripts/run-noice-phone-gate.sh — generic-app gate harness
  Add scripts/check-noice-proof.sh — generic-app proof checker

Do NOT push. Surface the commit hashes and ask the supervisor before pushing.

Final report (under 300 words):
- Two file paths created with line counts
- Two commit hashes
- Any McD-specific behavior that COULDN'T be cleanly genericized (note
  for follow-up)
- Test the scripts at least syntactically: `bash -n script.sh` should
  succeed for both
```

### Agent 3 — Framework miss handler (iterative)

```
You are handling Android framework API misses surfaced by running noice
in Westlake guest. Run iteratively against artifacts produced by the
gate harness; for each miss, decide whether to (a) add a tiny shim or
(b) document as deferred.

Read these first:
- WESTLAKE_NOICE_1DAY_CONTRACT_20260503.md
- WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md §13.1 (PF-632 exemplar)

Loop:
1. Supervisor will hand you an artifact dir like
     artifacts/noice-1day/<timestamp>_noice_iter_N/
2. Grep its logcat-dump.txt for:
     ClassNotFoundException, NoSuchMethodError, NoSuchFieldError,
     UnsatisfiedLinkError, AbstractMethodError, IncompatibleClassChangeError,
     VerifyError
3. For each distinct miss:
   a. Identify the requested class/method/field and its declaring file
      in the shim (likely shim/java/android/...).
   b. If the class exists in shim but the method is missing AND adding it
      is mechanical (a constructor variant, an overload that delegates
      to existing logic, a getter that returns the expected type's
      empty value), ADD it as a single-method addition with a brief
      comment. No McD-specific or noice-specific logic.
   c. If the class is entirely absent from shim, ADD a minimal stub
      class (constructor + the requested method returning the contract
      type's null/empty/default) IFF it's a small leaf class. Document
      the stub in artifacts/noice-1day/_framework_misses.md with
      "DEFERRED: needs real implementation" if it has substance (e.g.,
      AudioTrack, MediaCodec — these belong in the audio gap).
   d. Otherwise, document as deferred.
4. Make one commit per shim addition with message:
     PF-632 (noice): add android.X.Y.method() — tiny generic stub
5. Do NOT push (supervisor batches the push).

Per-iteration report (under 200 words):
- Number of distinct misses found in this artifact
- For each: action taken (added / deferred) + 1-line rationale
- Commit hashes
- Any miss that WOULD need McD-style architectural work (flag for
  supervisor to consider scope)

Constraints:
- Do not modify check-real-mcd-proof.sh or run-real-mcd-phone-gate.sh.
- Do not modify WestlakeLauncher.java in this loop (separate Agent 5
  if needed).
- Do not invent class implementations beyond the minimum to resolve
  the miss — the goal is to unblock noice, not provide AOSP fidelity.
```

### Agent 4 — Audio gap characterization

```
You are characterizing exactly what audio APIs noice tries to use and
where they land in Westlake — to produce a precise scope for the
audio-bridge work that's NOT being done day 1.

Read these first:
- WESTLAKE_NOICE_1DAY_CONTRACT_20260503.md (acceptance A5)
- WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md §13.7-§13.12 (the format
  for empirically-grounded findings)

Goal: produce
  artifacts/noice-1day/<timestamp>_noice_audio_gap/noice_audio_gap.txt
with the inventory below. Read-only investigation; no shim or runtime
changes.

Steps:
1. Pre-investigation (before phone interaction):
   a. Decompile noice's classes.dex to find calls to android.media.*
      and android.os.PowerManager.WakeLock. Tools to try: `dexdump`,
      `apktool`, `baksmali` (any one that's installed). If none
      available, grep the dex file for class string references:
        strings noice_classes.dex | grep -E 'android/media/|MediaPlayer|AudioTrack|AudioManager|AudioFocus|MediaSession|NotificationManager|WakeLock' | sort -u
2. From the gate-run logcat (artifact handed by supervisor):
   a. Grep for "InterpreterJni" lines that mention audio classes
   b. Grep for "FAST_NATIVE_METHOD" registrations that include audio
      packages
   c. Grep for any noice-side log lines mentioning audio (e.g.
      "MediaPlayer", "AudioTrack")
3. Cross-reference with the native bridge stubs at
   /home/dspfac/art-latest/stubs/ohbridge_stub.c (per the 2026-05-03
   review, the audio JNI surface is stubbed with mediaPlayerCreate /
   SetDataSource / Start / Pause / Stop returning 0/no-op). Identify
   each stubbed function noice would invoke.
4. Produce noice_audio_gap.txt with structure:
   ```
   == Noice audio API call inventory ==
   - <package.Class.method> — declared in dex, called from <noice class>
   ...
   == Westlake current state per call ==
   - <api> -> <ohbridge_stub.c:line> (stub returns 0/null) | NOT BRIDGED
   ...
   == Effort estimate to bridge ==
   - <subsystem> -> <effort range with rationale>
   ...
   == Recommended day-2+ work order ==
   - 1) ...
   - 2) ...
   ```

Final report (under 400 words):
- The text-file path
- Top 5 audio APIs noice relies on
- Realistic effort estimate to actually bridge audio (1-day, 1-week,
  multi-week). Be honest.
```

### Agent 5 (optional) — Launcher noice-context emitter

Only spawn if Agent 2's gate harness has NO source of `NOICE_*` markers (i.e., the launcher doesn't emit per-app markers).

```
You are adding a generic per-app context emitter to WestlakeLauncher.java
so the noice gate has measurable success markers (A2/A3/A4 in
WESTLAKE_NOICE_1DAY_CONTRACT_20260503.md).

Goal: emit logcat markers `NOICE_MAIN_ACTIVITY`, `NOICE_VIEW_INFLATED`,
`NOICE_SOUND_CARD_CLICK` from the launcher when it observes the
corresponding events.

Steps:
1. Read WestlakeLauncher.java end-to-end (large file ~22k lines; use
   Read with offset/limit + grep). Identify:
   a. Where the launcher receives Activity-onResume callbacks for the
      guest app (currently emits MCD_* markers — find the analog).
   b. Where View inflation is observed (likely a hook into
      LayoutInflater.inflate that emits MCD_PDP_REAL_XML_ENHANCED-style
      markers).
   c. Where tap routing emits MCD_PDP_STOCK_VIEW_CLICK.
2. Genericize the existing emitters: the markers should be parameterized
   by detected package name. If `package.startsWith("com.mcdonalds.")`
   emit `MCD_*`; if `package.startsWith("com.github.ashutoshgngwr.noice")
   || package.startsWith("com.trynoice.")` emit `NOICE_*`. Otherwise
   emit `APP_*` (generic).
3. Concretely add NOICE_MAIN_ACTIVITY, NOICE_VIEW_INFLATED,
   NOICE_SOUND_CARD_CLICK with the same shape as the McD analogs.
4. Rebuild aosp-shim.dex via scripts/build-shim-dex.sh. Confirm new dex
   contains the additions.
5. Make ONE commit with message:
     Launcher: emit per-app NOICE_*/APP_* markers (generic fork)
6. Do NOT push.

Constraints:
- Don't break the McD path. The `MCD_*` markers must still fire for
  com.mcdonalds.* — verify by re-running check-real-mcd-proof.sh on a
  recent McD artifact (no phone needed for that — the checker reads
  artifact files).
- Don't add app-detection branches outside the emitter helpers; refactor
  to a single per-app context object.

Final report (under 300 words):
- The marker emitter functions added (file:line)
- The dispatch table you used (package prefix → marker prefix)
- The new aosp-shim.dex sha256
- Commit hash
- Verification that McD checker still passes against a recent artifact
```

---

## 5. Phone protocols

### 5.1 Pre-day cleanup (do once at session start)

```bash
# Fully reset McD app state if any
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 \
  shell 'pm clear com.mcdonalds.app 2>/dev/null; pm clear com.westlake.host 2>/dev/null'

# Reboot for a clean RAM state (recommended after session 2026-05-03's
# variance issues)
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 \
  reboot
# wait ~60s for boot
until /mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 \
  -s cfb7c9e3 shell true 2>/dev/null; do sleep 5; done

# Confirm the known-good runtime is on the phone
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 \
  shell sha256sum /data/local/tmp/westlake/dalvikvm
# Expect: d7e10e47ff5ae0a8c0b103ea975f37fb2aa1ade474fac52f68ff03da95d9d872
# If different, restore from /home/dspfac/westlake-runtime-backups/
```

### 5.2 Per-iteration cleanup

Before each gate run:
```bash
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 \
  shell 'pm clear com.westlake.host'
sleep 2
```

### 5.3 Artifact directory and naming

All noice artifacts go under `artifacts/noice-1day/` (NOT `artifacts/real-mcd/`).

Naming convention: `<timestamp>_noice_<phase>_iter<N>` where phase ∈
`{first_boot, framework_iter, ui_render, audio_gap, soak, final}`.

Per-artifact contents (mirroring real-mcd):
- `check-noice-proof.txt` — checker output
- `logcat-stream.txt` — streamed logcat during gate
- `logcat-dump.txt` — full logcat dump after gate
- `screen.png` — screenshot
- `processes.txt` — `ps` output at gate end
- `unsafe_flags.txt` — flag-state capture (must report all flags missing)

### 5.4 Rollback

If anything goes wrong on the phone:
```bash
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 \
  push /home/dspfac/westlake-runtime-backups/dalvikvm.pre-pf630-d7e10e47.bak \
  /data/local/tmp/westlake/dalvikvm
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3 \
  shell 'chmod 755 /data/local/tmp/westlake/dalvikvm && \
         pm clear com.westlake.host && reboot'
```

---

## 6. Out-of-scope (DO NOT attempt in day 1)

- **Implementing the audio bridge.** Day 1 is to *characterize* the gap, not close it. A realistic audio bridge is 1-2 weeks of work (JNI for `MediaPlayer`/`AudioTrack`/`AudioFocus` to bionic libaudio on phone, or to OHOS audio for portability).
- **Foreground media service**. Service lifecycle + media notification + wake locks is a separate workstream. Day 1 is "tap a sound card, observe click handler invoked, see where audio call lands"; not "play sound continuously screen-off".
- **OHOS port of any noice subsystem.** OHOS portability is its own track (PF-608/PF-626 in McD doc). Day 1 is Android-phone only, same as today's McD baseline.
- **Continuing PF-630 attempts.** PF-630 is unblocked only by the boot-aware routing gate fix (see McD wrap-up §14.3 P0). Don't re-try the failed approaches.
- **Re-genericizing all McD-specific markers.** Agent 5 only adds NOICE_* / APP_* alongside MCD_*; it doesn't refactor the launcher to be McD-agnostic top-down.

---

## 7. Risks and mitigations

| Risk | Likelihood | Mitigation |
|---|---|---|
| Noice APK won't load due to dex format incompatibility (target SDK > runtime SDK) | LOW-MED | Acquire APK with target SDK ≤ 30 (Westlake guest is dalvikvm-class). If only SDK 33+ available, document as a finding and try anyway |
| Noice has multidex (`classes.dex` + `classes2.dex`) and the gate harness only loads one | MED | Agent 2 must handle multidex (push all classesN.dex; concatenate to a classpath the launcher accepts) |
| Noice depends on `androidx.media3` or other AndroidX libs not yet shimmed | MED | Each miss → Agent 3 iteration. Budget ~5 iterations for day 1 |
| Audio JNI calls trigger SIGSEGV in stub C code | LOW | The `ohbridge_stub.c` returns 0 for unimplemented functions; should be safe. If SIGSEGV: add explicit guard for the called function |
| Noice triggers a Realm-like JNI finalizer SIGBUS we haven't seen | LOW | Noice doesn't use Realm; should be safe. If hit: roll back, document, stop |
| Phone state drifts mid-day (same instability as 2026-05-03 PT) | MED | Reboot + re-baseline between phases. Don't let one bad gate cascade |
| The day blows past 8 hours | MED-HIGH | Day-1 acceptance is A1-A7 only. If after 8h only A1-A4 are met, that's still a meaningful first proof — write it up as partial-day result and pause |

---

## 8. Wrap-up criteria (end of day 1)

By session close, deliver:

1. **Final artifact** at `artifacts/noice-1day/<timestamp>_noice_day1_final/` with all files per §5.3
2. **Wrap-up doc** at `docs/program/WESTLAKE_NOICE_DAY1_RESULTS_<date>.md` containing:
   - Hour-by-hour what-actually-happened log
   - Each acceptance criterion: PASS / FAIL / INCONCLUSIVE with evidence
   - Audio gap characterization (Agent 4's deliverable embedded or referenced)
   - Framework misses encountered + how each was handled
   - Per-component status: APK loaded? UI rendered? Click routed? Audio gap measured?
   - Day 2+ recommendation (continue noice with audio bridge? pivot to another simple app? return to McD PF-630?)
3. **All commits pushed to `origin/main`** (one push at end of day, not per-commit). Commit chain:
   - Agent 2: 2 commits (gate scripts)
   - Agent 3: N commits (one per shim addition)
   - Agent 5 (optional): 1 commit (launcher emitter)
   - Supervisor: 1 commit (results doc)
   - **DO NOT** push if any commit involves cleared-data-on-phone or destructive ops
4. **Phone state recorded:** runtime hash unchanged from `d7e10e47…`; backup retained; probe files cleaned
5. **art-latest:** untouched (this contract does NOT involve runtime work)

Optional stretch: file noice-equivalents of PF-* GitHub issues if specific blockers surfaced (e.g. "PF-noice-001: MediaPlayer JNI needs bionic libaudio bridge").

---

## 9. References

- **McD wrap-up + roadmap:** `docs/program/WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md` §14
- **Platform-first contract:** `docs/program/WESTLAKE_PLATFORM_FIRST_CONTRACT.md`
- **Existing handoff (McD-side):** `docs/program/WESTLAKE_REAL_MCD_HANDOFF_20260503.md`
- **Phone & ADB details:** McD handoff §"Phone + ADB"
- **Westlake host package on phone:** `com.westlake.host`
- **McD gate harness reference:** `scripts/run-real-mcd-phone-gate.sh`, `scripts/check-real-mcd-proof.sh`
- **Proof-checker `no_unsafe` fix (must inherit):** commit `98719db2`

---

## 10. Sign-off (this contract)

This contract is approved for execution as-written by the next agent or human supervisor. Modifications are welcome; if a phase plan slips, drop the stretch criteria (A8/A9) before dropping anything from A1-A7.

The 1-day budget is a forcing function for scope discipline, not a deadline. If real progress requires day 2, write up day 1 honestly and continue — don't pad.
