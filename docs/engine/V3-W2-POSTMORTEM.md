# V3-W2 — Postmortem: DAYU200 soft-brick during HBC runtime deploy

**Status:** POSTMORTEM (board offline; awaiting operator hard power-cycle).
**Date:** 2026-05-16.
**Authoring agent:** 52 (board-independent — no hdc access during writeup).
**Subject:** W2 deploy attempt by agent 49 (issue #627), checkpoint report at
[`docs/engine/V3-W2-BOOT-HBC-RUNTIME-REPORT.md`](V3-W2-BOOT-HBC-RUNTIME-REPORT.md)
(commit `f25412f8`).
**Scope:** evidence-anchored timeline + hypothesis ranking + SOP / regression
gaps + formalized recovery procedure + memory lessons.

---

## 1. Timeline — stage-by-stage

Source: `V3-W2-BOOT-HBC-RUNTIME-REPORT.md` (W2 agent's own checkpoint).
Cross-reference: `scripts/v3/deploy-hbc-to-dayu200.sh` (the driver),
`westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md` (the SOP we ostensibly
followed), `docs/engine/V3-DEPLOY-SOP.md` (the Westlake-adapted SOP).

| Stage | SOP §     | Outcome | Evidence anchor |
|------:|-----------|---------|-----------------|
| 0     | HBC §0    | **PASS** (with 1 deviation: `boot-framework.art` size 23,781,376 vs SOP guard 23,760,896 — Δ +20 KB, well above truncation floor) | W2 report §1 (line 13) |
| 1     | HBC §1    | **PASS** (13 `.orig_20260516` device-side backups verified) | W2 report §2 (line 14) |
| 2     | HBC §2    | **SKIPPED** (foundation + render_service NOT stopped; W2 agent argued the deploy targets `/system/lib/` which can be replaced without stopping consumers, "and the SOP warns against any chance of breaking hdcd") | W2 report §3 (line 15); note: HBC SOP §2 explicitly mandates the two stops |
| 3     | HBC §3b-3f| **PASS** (all 94 files + 4 symlinks pushed; 27/27 boot images MD5-verified equal local-vs-device; chcon + restorecon batched per script) | W2 report §4 (lines 16-32) + deploy script lines 146-279 |
| 3 missing | HBC §1 list | **2 SOP-mandated artifacts not pushed** (`libinstalls.z.so` + `libappexecfwk_common.z.so` absent from W1 pull `v3-hbc/lib/`) | W2 report §5 (lines 33-36); SOP §3f lines 153 + 78 |
| 3.9   | HBC §3.9  | **NOT REACHED** (post-Stage 3 `hdc shell` output went empty) | W2 report §"Blocker" (lines 37-48) |
| 3.5   | HBC §3.5  | **NOT REACHED** (`hdc target boot` issued, device did not re-enumerate) | W2 report §"Blocker" line 41 |
| 4     | HBC §4    | **NOT REACHED** | W2 report §"Verdict" line 9 |

**Verdict on MainActivity.onCreate L83 (W2 acceptance criterion):** FAIL with
checkpoint. Boot never restarted; APK never installed; lifecycle never invoked.

**Wall-clock time before board went silent:** the `/proc/uptime` recv at end
of Stage 3 returned ~44,405 s (~12 hr) — confirming the kernel was alive and
the device had not yet rebooted at the moment shell stdout silently broke.

---

## 2. Hypotheses for the soft-brick (ranked by likelihood)

Ranking criteria: (a) does evidence in W2 report + scripts directly support
the hypothesis, (b) does the hypothesis predict ALL observed symptoms (silent
shell stdout BEFORE `hdc target boot` + non-re-enumeration AFTER), and (c)
priors from `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md` §"违规事故索引"
(blood-lessons section, lines 230-234) and HBC SOP §3 chcon failure-mode
warnings (lines 104, 123, 146).

### H1 — SOP P-1 SELinux respawn storm because chcon silently no-op'd (HIGH)

**Mechanism:** Stage 3c-end and 3e-end of `deploy-hbc-to-dayu200.sh`
(lines 188-193, 246-248) batch `chcon u:object_r:system_lib_file:s0 ...`
against adapter shims and the 27 boot image files. If those `hdc shell`
invocations silently produced no output (the exact symptom observed AFTER
Stage 3), the chcon commands may have never run or never reported errors,
leaving labels at the inherited `system_file:s0`. Per HBC SOP §3c
(line 104) and §3e (line 146), the predicted failure is:

> `appspawn:s0 域 dlopen 时 flock 被拒 → ART JNI_CreateJavaVM Phase 2 SIGABRT`
> (translated: appspawn domain dlopen gets flock-denied → ART crash storm)

A respawn storm during the post-Stage-3 settling window could wedge USB
enumeration even without an explicit reboot.

**Evidence FOR:**
- Symptom timing: shell stdout went empty *during* Stage 3 (after the
  chcon batches at the end of 3c and 3e), per W2 report line 39.
- The deploy script (line 70) `tr -d '\r'`s shell output but doesn't
  verify the chcon command actually succeeded — it relies on the
  trailing `|| true` (lines 193, 216, 248, 270) suppressing failure
  rather than asserting success.
- The post-Stage-3 verification listed in HBC SOP §3.9 lines 180-186
  ("ls -lZ /system/etc/fonts.xml | grep system_fonts_file" etc.) is
  also absent from the deploy script entirely — there is no
  programmatic check that any chcon stuck.

**Evidence AGAINST:**
- Pre-reboot the device was still alive (uptime recv worked). A label
  storm typically manifests at the next service start, not at the
  current uptime instant.
- An ART respawn storm should produce hilog entries before USB drop;
  we have no hilog capture to confirm or refute.

### H2 — Windows `hdc.exe` regression independent of board state (HIGH)

**Mechanism:** The host-side hdc binary at
`/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` is a Windows executable
invoked from WSL via `wslpath` conversion (deploy script lines 88-93).
A version-skew or transport-state regression in `hdc.exe` could
manifest as `hdc shell` returning exit 0 with empty stdout while
`hdc file send/recv` continues to work (different transport channel
inside the hdc protocol). W2 agent explicitly raised this hypothesis
in the report (lines 46-47).

**Evidence FOR:**
- Bimodal failure mode: shell stdout dead, file send/recv alive — the
  signature of a per-channel hdc client bug rather than a board-side
  daemon dying.
- The W2 fallback `w2_slot` in `scripts/v3/run-hbc-regression.sh`
  (lines 449-489) was written exactly to work around this quirk
  (push probe + exec via shell + recv result), suggesting the W2
  agent had already seen it intermittently.
- `hdc target boot` reset failure: an hdc client-state bug could
  leave the USB endpoint in a half-claimed state where the device
  reboots but the client can't re-enumerate without a USB-stack
  reset.

**Evidence AGAINST:**
- A pure client bug shouldn't leave the device unreachable AFTER the
  client process exits. The post-`hdc target boot` non-re-enumeration
  is harder to explain by client-only causes.

### H3 — `appspawn-x` replacing init service tree corrupted boot (MEDIUM)

**Mechanism:** Stage 3f pushes `appspawn_x.cfg` into `/system/etc/init/`
(deploy script line 254). On next init reload (which `hdc target boot`
would trigger), init parses the new .cfg and tries to start `appspawn-x`.
If `appspawn-x` is missing its `ld-musl-namespace-arm.ini` link OR its
SELinux `file_contexts` patch hasn't taken effect (per HBC SOP §3f
line 167-169: "appspawn-x layer 1/2 prerequisites"), init enters a
fork-respawn loop on the appspawn_x service. With OH `appspawn` also
attempting to start (factory cfg), a service-tree conflict could leave
both daemons in respawn churn — both of which gate USB transport
recovery on OHOS.

**Evidence FOR:**
- The `appspawn_x.cfg` push is a one-way step; the deploy script does
  not delete the factory `appspawn.cfg` (which would create a clean
  cutover) nor stop factory `appspawn` first (Stage 2 was skipped).
- HBC SOP §2 explicitly warns: **"不停 appspawn ... 停了会断 hdc 通信链"**
  (line 51 — "do NOT stop appspawn — stopping it severs hdc"). By
  symmetry, *adding* a second appspawn (`appspawn-x`) and forcing init
  to reload could have similar transport-disrupting effects.

**Evidence AGAINST:**
- The shell-empty symptom appeared BEFORE `hdc target boot`. Init
  reload only happens on reboot. So whatever caused the silent shell
  is independent of the appspawn cutover (though the cutover could be
  what kills the device the rest of the way).

### H4 — Two SOP-mandated .so files missing triggered init crash (LOW-MEDIUM)

**Mechanism:** `libinstalls.z.so` and `libappexecfwk_common.z.so` are
in HBC's SOP §1 backup list (lines 35-36) and SOP §3f line 153 deploy
list. W1 pulled neither into `v3-hbc/lib/` (W2 report lines 33-36).
The deploy script's `push_file` (lines 76-102) silently `SKIP`s
non-existent sources and returns 0 — no FAIL.

Without `libappexecfwk_common.z.so` at `/system/lib/platformsdk/`,
the SOP's symlink at line 275 (`ln -sf /system/lib/platformsdk/libappexecfwk_common.z.so /system/android/lib/libappexecfwk_common.z.so`)
points to a non-existent target. Multiple OH services dlopen this lib
(per the OH `bundle_framework` subsystem). On next service tick, the
dlopen failures cascade.

**Evidence FOR:**
- W2 report explicitly notes these were missing (lines 33-36).
- The deploy script's silent-SKIP semantics (line 78-81: `echo "SKIP
  (not found): $local_path" >&2; return 0`) does mean missing-artifact
  failures don't propagate to the suite verdict.

**Evidence AGAINST:**
- W2 agent reported "factory copy is acceptable" for `libappexecfwk_common`
  (line 35) and the factory copy was backed up; the device-side dangling
  symlink would simply overlay a working factory lib. Not necessarily
  fatal.
- Failure would manifest at next foundation/render_service start, not
  at Stage 3 mid-push.

### H5 — HBC SOP authored for THEIR mid-bringup board state, not factory clean (MEDIUM)

**Mechanism:** HBC's `DEPLOY_SOP.md v4` (the `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md`
we followed) is dated 2026-04-21 and was authored by an HBC engineer
working on a board that had likely been deployed-to and rolled-back
many times. The SOP's `Stage 0` (line 14-19) checks only:
- `/system/android` absent (factory marker)
- `boot-framework.art` size = 23,760,896 bytes

It does NOT verify the broader prerequisites HBC's own internal
bringup flow likely runs first (e.g., factory `appspawn.cfg` content,
factory file_contexts version, factory `policy.31` version, the
specific OH ROM build the SOP was tested against).

Westlake adopted this SOP verbatim. Our DAYU200 may have been a
slightly different OH ROM build than the SOP's reference, in which
case the SOP's chcon/restorecon assumptions could partially fail
silently.

**Evidence FOR:**
- The +20 KB drift in `boot-framework.art` size (W2 report line 13)
  is a single data point that HBC's stack itself drifts between
  SOP-author date and W2 pull date. Other artifacts likely drift too.
- The SOP mentions "ECS build layout" (deploy script header line 9)
  vs Westlake's "flat layout" — a structural translation we hand-rolled.
- The SOP TODO list at lines 237-246 explicitly notes that
  `deploy_to_dayu200.sh` itself was missing 4 items the SOP requires.
  The maturity gap was already known.

**Evidence AGAINST:**
- HBC reportedly reaches MainActivity.onCreate L83 (per
  `project_v3_hbc_reuse_direction.md`), which means the SOP does work
  in HBC's hands — so it's not categorically broken, just possibly
  not portable without bringup gates.

### H6 — Boot image +20 KB drift caused cascade (LOW)

**Mechanism:** SOP §0 line 19 guards `boot-framework.art` at 23,760,896
bytes ("防截断复发" — "prevent truncation regression"). W2 pulled
23,781,376 bytes (+20 KB). The W2 agent argued this is not the
truncation symptom (truncation is 9 MB vs 23 MB), but a +20 KB drift
could indicate a different `dex2oat` config that requires matching
adapter framework jar versions. If the boot image's adapter-framework
class layout doesn't match the framework.jar adapter we pushed, ART
ValidateOatFile could reject the boot image on next ART process start.

**Evidence FOR:**
- HBC SOP §3e line 133 explicitly documents the
  "9段全推" (push all 9 segments) requirement — boot image segment
  versions must match per dex2oat run. Mixing versions is a known
  abort path.

**Evidence AGAINST:**
- All 27 boot image files MD5-verified equal local-vs-device, so the
  push itself was correct. The drift is in W1's pull, not in our
  Stage 3.
- ART validate-on-load failures would manifest on next ART start
  (which never happened — no reboot yet), so this can't explain the
  silent-shell symptom that appeared BEFORE reboot.

### Hypothesis ranking summary

| H | Likelihood | Pre-reboot shell-silent | Post-reboot non-enum |
|---|-----------|------------------------|-----------------------|
| H1 chcon storm | HIGH | partial (only if chcon was the silent step) | yes (label-loop wedges USB) |
| H2 hdc.exe client bug | HIGH | yes | partial (USB stack half-claim) |
| H3 init service-tree | MEDIUM | no (pre-reboot) | yes |
| H4 missing .so | LOW-MED | no | partial |
| H5 SOP portability | MEDIUM (framing) | indirect | indirect |
| H6 boot image drift | LOW | no | partial |

**Top-2:** H1 (chcon respawn storm) + H2 (hdc.exe client regression).
**Most likely combination:** H2 fired first (silent shell during Stage 3
breaks our chcon batches → labels wrong → respawn storm post-reboot in
the H1 pattern). Both root causes co-occurring would explain ALL symptoms.

---

## 3. Diagnostic gaps — what we DIDN'T capture

These are the data points that, had we captured them, would let us cleanly
distinguish H1/H2/H3/H4/H5/H6:

1. **hilog snapshot at end of Stage 3.** No `hdc file recv /proc/last_kmsg`
   or `hdc shell hilog -x | head` was run after the shell went quiet. With
   that we could confirm (or refute) an ART JNI_CreateJavaVM SIGABRT storm
   (H1) or a service-respawn churn (H3).

2. **chcon result verification.** The deploy script runs `chcon ... || true`
   (line 193, 216, 248) but never reads back labels via `ls -lZ` and
   asserts. HBC SOP §3d line 121 even has the canonical pattern
   (`ls -lZ /system/etc/fonts.xml | grep system_fonts_file`); we did not
   port it. This alone would have distinguished H1 immediately.

3. **hdc client version + connection state baseline.** No `hdc version`
   capture, no `hdc list targets` round-trip before AND after every Stage,
   no comparison of stdout-byte-count for a known-good shell command at
   the start vs after each Stage. The W2 fallback w2_slot exists because
   this was already a flaky channel — should have been monitored more
   aggressively.

4. **Pre-deploy pidof / service-state snapshot.** No `pidof foundation
   render_service com.ohos.launcher hdcd appspawn` capture before Stage 3.
   Post-deploy comparison would have shown whether init was already
   churning during Stage 3 (rather than at reboot).

5. **`ls -la /system/lib/` round-trip.** Stage 3.9 in the SOP (lines 184-186)
   demands "no drwx entries in /system/android/lib/". The deploy script
   doesn't run this check at all. If hdc's 造目录 quirk fired silently
   on any of the 94 pushes, we have no record.

6. **Factory `appspawn.cfg` contents.** We never captured the existing
   `/system/etc/init/appspawn.cfg` before pushing `appspawn_x.cfg`
   alongside it. Without that diff we can't tell whether init's new
   service-tree was consistent.

7. **Backup completeness verification beyond `ls`.** Stage 1 verified the
   13 `.orig_20260516` files existed via `ls` but didn't md5sum them
   against the as-pushed files to confirm Stage 1 actually captured the
   pre-deploy state vs an already-overwritten file.

---

## 4. SOP gaps — concrete additions to V3-DEPLOY-SOP.md

These additions are derived directly from the diagnostic gaps above. Each
maps to a specific line/section that should be added to
`docs/engine/V3-DEPLOY-SOP.md`:

### G1 — Add "Stage 0.5: hdc-shell health probe" (between Stage 0 and Stage 1)

```bash
# Verify shell stdout channel is alive AND produces expected byte counts.
EXPECTED=$("$HDC" shell "echo HDC_SHELL_PROBE_v3" | tr -d '\r\n')
[ "$EXPECTED" = "HDC_SHELL_PROBE_v3" ] || {
    echo "ABORT: hdc shell stdout channel broken (got: '$EXPECTED')"
    exit 2
}
# Re-run this probe between EVERY Stage. If it ever stops returning the
# expected string, STOP the deploy and do not run any more chcon /
# restorecon commands.
```

Coverage: H2 (hdc.exe client bug). This is the single highest-value
addition — would have caught the W2 attempt at the boundary between
Stage 3c and Stage 3d.

### G2 — chcon must use `set -e` semantics, not `|| true`

`deploy-hbc-to-dayu200.sh` lines 193, 216, 248 currently end with `|| true`,
which silently swallows any failure (including silent-shell case). Replace
with assertion:

```bash
# After every chcon batch:
hdc shell "ls -lZ <path> | head -1" | grep -q "<expected_label>" || {
    echo "ABORT: chcon did not stick on <path>"
    exit 3
}
```

Coverage: H1 (silent chcon → SELinux respawn storm).

### G3 — Add "Stage 3.0a: missing-artifact assertion"

Replace `push_file`'s current silent-SKIP semantics
(`deploy-hbc-to-dayu200.sh` lines 78-81) with an explicit allowlist of
files that may legitimately be absent. Anything else missing = abort
before Stage 3 begins:

```bash
REQUIRED_LIBS="libinstalls.z.so libappexecfwk_common.z.so libwms.z.so ..."
for lib in $REQUIRED_LIBS; do
    [ -f "$V3_LOCAL/lib/$lib" ] || {
        echo "ABORT: required lib $lib missing from v3-hbc/lib/"
        exit 4
    }
done
```

Coverage: H4 (missing .so).

### G4 — Hard-gate Stage 2 (foundation/render_service stop)

W2 agent skipped Stage 2 with a plausible-sounding reason ("the SOP warns
against breaking hdcd"). The SOP's actual position (HBC §2 lines 44-54)
is that ONLY appspawn must not be stopped; foundation/render_service
SHOULD be stopped to release mmap'd .so handles. Make the script enforce:

```bash
[ "$1" = "--skip-stage2" ] || begetctl_or_abort foundation render_service
```

Default: stop them. Opt-out flag required to skip. Coverage: H3
(service-tree consistency at next reload).

### G5 — Add Stage 3.9 verification block as a script step

Per HBC SOP §3.9 lines 180-186, the post-Stage-3 verification is
non-optional. Currently `deploy-hbc-to-dayu200.sh` ends at Stage 3f and
does not run integrity or "drwx" sentinel checks. Append:

```bash
echo "[3.9] integrity + drwx sentinel check..."
hdc shell "ls /system/android/lib/ /system/android/framework/arm/ | grep '^d'" \
    | wc -l | tr -d '\n' | grep -q "^0$" || {
    echo "ABORT: directory entry found where file expected (hdc 造目录 quirk)"
    exit 5
}
# + the md5 round-trip for all 94 files (boot image already done in 3e)
```

Coverage: H6 (validate boot image consumability) + hdc 造目录 quirk
that lines 230-234 of the HBC SOP explicitly call out as a prior incident.

### G6 — Mandate `hdc.exe` version pinning + warning if WSL

Add to Stage 0:

```bash
HDC_VERSION=$("$HDC" version 2>&1 | head -1)
echo "[0] hdc version: $HDC_VERSION"
# Pin a known-good version range here. If the actual hdc.exe at /mnt/c/...
# has drifted, abort.
case "$HDC_VERSION" in
    *Ver:1.3.0c*|*Ver:1.3.0d*) ;;  # known-good
    *) echo "WARN: hdc version $HDC_VERSION not in known-good list" ;;
esac
```

Coverage: H2 (hdc.exe client regression). Pinned version is the
mitigation.

### G7 — Add Stage 0 + Stage 3.9 entries to `run-hbc-regression.sh`

The regression suite currently has w2_slot but no separate slot probing
"the deploy script's pre-flight gates would pass." Add:

```bash
check_hdc_shell_stdout_alive()  # G1 — channel-health probe
check_deploy_chcon_assertions() # G2 — static-lint deploy script
check_required_lib_inventory()  # G3 — local v3-hbc/lib/ has full set
```

These probe the SOP-compliance of the deploy script itself BEFORE any
agent runs it.

---

## 5. Recovery procedure (for when board returns)

Formalized from W2 report §"Restoration plan" (lines 50-58). To be invoked
ONLY after the operator hard power-cycles DAYU200 and `hdc list targets`
shows the device.

### R0 — Confirm device alive

```bash
"$HDC" list targets | grep -q "$HDC_SERIAL" || {
    echo "Device still offline. STOP. Re-attempt hard power-cycle."; exit 1; }
"$HDC" shell "echo alive" | tr -d '\r' | grep -q "alive" || {
    echo "Shell channel still broken. Try hdc kill + restart."; exit 1; }
"$HDC" shell "uname -a"   # capture for log
"$HDC" shell "uptime"     # confirm reboot occurred (uptime < pre-deploy)
```

If R0 passes, proceed. If shell stdout is still broken but file send/recv
works, capture diagnostics via push-probe-recv pattern (the same pattern
the w2_slot fallback uses), then STOP for human review before attempting
restore.

### R1 — Snapshot current state (READ-ONLY)

```bash
"$HDC" shell "ls /system/android" 2>&1 | tee /tmp/post_brick_system_android.txt
"$HDC" shell "ls /system/lib/*.orig_20260516" 2>&1 | tee /tmp/post_brick_backups.txt
"$HDC" shell "pidof foundation render_service com.ohos.launcher hdcd appspawn appspawn-x"
"$HDC" shell "hilog -x | tail -200" > /tmp/post_brick_hilog.txt 2>&1
```

The hilog dump is critical for confirming which hypothesis fired. Even if
the device is currently healthy (post-reboot it may have settled), the
last-boot hilog should show the respawn storm signature.

### R2 — Restore from device-side backups

Per W2 report §"Restoration plan" step 1 (line 54), with the addition of
explicit MD5 verification of each restore:

```bash
"$HDC" shell "mount -o remount,rw /"

# /system/lib/  (6 files)
for f in libwms.z.so libappms.z.so libbms.z.so libskia_canvaskit.z.so \
         libinstalls.z.so libappspawn_client.z.so; do
    "$HDC" shell "[ -f /system/lib/${f}.orig_20260516 ] && \
        cp /system/lib/${f}.orig_20260516 /system/lib/${f}"
done

# /system/lib/platformsdk/  (5 files)
for f in libabilityms.z.so libscene_session.z.so libscene_session_manager.z.so \
         librender_service_base.z.so libappexecfwk_common.z.so; do
    "$HDC" shell "[ -f /system/lib/platformsdk/${f}.orig_20260516 ] && \
        cp /system/lib/platformsdk/${f}.orig_20260516 /system/lib/platformsdk/${f}"
done

# /system/etc/  (1 file)
"$HDC" shell "[ -f /system/etc/ld-musl-namespace-arm.ini.orig_20260516 ] && \
    cp /system/etc/ld-musl-namespace-arm.ini.orig_20260516 /system/etc/ld-musl-namespace-arm.ini"

# /system/etc/selinux/targeted/contexts/  (1 file)
"$HDC" shell "[ -f /system/etc/selinux/targeted/contexts/file_contexts.orig_20260516 ] && \
    cp /system/etc/selinux/targeted/contexts/file_contexts.orig_20260516 \
       /system/etc/selinux/targeted/contexts/file_contexts"
```

13 of 13 files restored = OH factory state recovered for the backed-up
subset.

### R3 — Remove deploy artifacts

```bash
"$HDC" shell "rm -rf /system/android"             # all V3 newly-created paths
"$HDC" shell "rm -f /system/bin/appspawn-x"
"$HDC" shell "rm -f /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json"
"$HDC" shell "rm -f /system/lib/liboh_*.so /system/lib/libapk_installer.so"
# Symlinks to remove (these point at things now gone):
"$HDC" shell "rm -f /system/lib/libc_musl.so /system/lib/libandroid.so"
```

Note: `deploy-hbc-to-dayu200.sh --uninstall` mode does most of this
(lines 124-137), but it assumes `hdc shell` works. If R0 confirmed shell
is healthy, `--uninstall` is the preferred path.

### R4 — Restorecon + reboot + verify

```bash
"$HDC" shell "restorecon /system/lib/* /system/lib/platformsdk/* \
              /system/etc/ld-musl-namespace-arm.ini \
              /system/etc/selinux/targeted/contexts/file_contexts"
"$HDC" shell "sync; reboot"
sleep 60   # board takes ~30-60s to come up cleanly
"$HDC" shell "echo alive" | grep -q alive
"$HDC" shell "pidof foundation render_service com.ohos.launcher hdcd"
# All four must be non-empty == factory-state recovered.
```

### R5 — If recovery fails (any step in R0-R4)

Physical recovery via DAYU200 USB burn tool. Separate procedure;
out of scope for this postmortem. The 13 backups + bootloader/recovery
partition integrity (W2 report §"bootloader/recovery partitions are
intact" line 58) mean physical recovery should be straightforward.

---

## 6. Lessons (memory feedback candidates)

Three lessons rise above the W2 specifics into reusable rules. The lead
lesson is enshrined as `feedback_soft_brick_w2_2026-05-16.md` (W10
deliverable, co-authored by this agent). The other two are commentary
that should land in existing memory files via amendment:

### L1 (primary) — Channel-health probe between every Stage

If a deploy script's transport channel can fail silently (return exit 0
with empty stdout), **probe channel health before and after every Stage**.
Do not assume that "the last command returned exit 0" implies "the next
command will be heard." Especially when the transport is a Windows
binary invoked from WSL with `wslpath` translation — that's two layers
of error-swallowing potential.

→ Encoded as `feedback_soft_brick_w2_2026-05-16.md`.

### L2 (amendment to `feedback_subtraction_not_addition.md`)

When following a borrowed SOP, **stage the SOP itself** — run it on a
clean board in increments (one component at a time + verify) before
running the full SOP-in-one-shot. The W2 attempt was the
"full SOP in one shot" form; the addition to `feedback_subtraction_not_addition.md`
is that subtraction discipline applies not only to debugging an
existing broken state but also to **adopting** a new deploy procedure.

→ Mentioned in `feedback_soft_brick_w2_2026-05-16.md` as
"how-to-apply rule 4."

### L3 (amendment to `project_v3_hbc_reuse_direction.md`)

A borrowed SOP is **scoped to the author's mid-bringup board state**,
not to a clean / factory board. When inheriting an SOP from another
team's stack, add a fresh-bringup gate that walks through the
prerequisites the original SOP took as given. The HBC SOP took as given
that HBC's specific OH ROM build was running, that HBC's bringup-time
chcon labels were already correct, etc. For Westlake to run the SOP on
our DAYU200 fresh, those prerequisites need to be either verified or
re-installed first.

→ Encoded as a STATUS line in `project_v3_hbc_reuse_direction.md`
(W10 deliverable, co-authored by this agent).

---

## 7. Status + ownership

- **Postmortem author:** agent 52 (board-independent, ~2-3h time-box).
- **W2 attempt author:** agent 49 (issue #627; checkpoint commit
  `f25412f8`).
- **Board status:** OFFLINE awaiting operator hard power-cycle. No
  remote intervention possible (DAYU200 has no remote reset).
- **Downstream gates blocked:** W3 (appspawn-x integration), W4 (adapter
  customization), W5 (mock APK), W6 (noice), W7 (McD). All require W2
  PASS first.
- **Next agent action when board returns:** execute §5 R0-R4 in order;
  do NOT re-attempt deploy until §4 G1+G2 are landed into the script.

## 8. Cross-references

- `docs/engine/V3-W2-BOOT-HBC-RUNTIME-REPORT.md` — W2 agent's
  checkpoint (the source of truth for what was attempted)
- `scripts/v3/deploy-hbc-to-dayu200.sh` — the deploy driver
  (target of §4 G1-G6 amendments)
- `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md` — HBC's SOP v4
  (read-only reference; do not modify in place)
- `docs/engine/V3-DEPLOY-SOP.md` — Westlake-adapted SOP (target of
  §4 amendments)
- `scripts/v3/run-hbc-regression.sh` §w2_slot — the regression slot
  (already filled by W2 agent at lines 428-504; §4 G7 adds new slots)
- `docs/engine/V3-RESTORE.md` — companion document for restore-time
  discipline (relevant when §5 recovery runs)
- `feedback_soft_brick_w2_2026-05-16.md` (memory) — the L1 lesson
- `feedback_additive_shim_vs_architectural_pivot.md` (memory) —
  reminder: a single failed deploy is NOT pivot-evidence; this is
  normal W-level engineering rework, not an architectural signal
- `feedback_two_pivots_in_two_days.md` (memory) — same theme; W2
  failure does NOT warrant V3-pivot consideration
