# V3 W2 — Recovery + Re-attempt Procedure (from HBC re-query)

**Status:** AUTHORITATIVE for W2 recovery + re-attempt. 2026-05-16.
**Authoring agent:** 53.
**Companion docs:** [`V3-W2-POSTMORTEM.md`](V3-W2-POSTMORTEM.md) (agent 52 hypothesis ranking), [`V3-W2-BOOT-HBC-RUNTIME-REPORT.md`](V3-W2-BOOT-HBC-RUNTIME-REPORT.md) (W2 checkpoint), [`V3-HBC-ARTIFACT-MANIFEST.md`](V3-HBC-ARTIFACT-MANIFEST.md), [`V3-DEPLOY-SOP.md`](V3-DEPLOY-SOP.md).

---

## Status

- Board: soft-bricked after W2 deploy; needs operator hard power-cycle.
- This doc: what HBC team's own working tree says about fresh-bringup, post-brick recovery, and the two `.so` files that were missing from W1.

---

## Findings

### 1. Fresh-board procedure — **YES, found verbatim**

HBC's `deploy/deploy_stage.sh` has a **Stage 0 preflight that defines "factory baseline"** (lines 159-203 on HBC server):

```
stage_0() {
    log "Stage 0 · 前置检查"
    check_hdc_alive
    ok "hdc alive"

    has_android=$(hdc_shell "ls -d /system/android 2>&1" | chomp)
    echo "$has_android" | grep -q "No such" || abort "/system/android already exists — not factory baseline"
    ok "/system/android absent (factory baseline)"
    ...
}
```

The single defining signal of "fresh-board / factory baseline" per HBC is
**`/system/android` does not exist**. Our W1 preflight confirmed this for
DAYU200 (the report said "clean board"). So our board WAS a fresh board at the
start of W2.

Additional Stage-0 invariants HBC checks (we did partial):
- `hdc list targets` non-empty + `hdc shell "echo alive"` returns the string `alive`.
- `boot.oat` mtime ≥ `libart.so` mtime (artifact consistency; not relevant on the device side — only checked against the local build).
- `boot.oat` ELF magic `7f454c46` (truncation guard).

There is **NO separate "first-time-bringup" doc beyond DEPLOY_SOP.md +
deploy_stage.sh Stage 0**. Fresh-board IS the canonical starting state for
the SOP. (Source: `/home/HanBingChen/adapter/deploy/DEPLOY_SOP.md` line 21
"`/system/android` 不存在（factory 态）".)

### 2. Recovery from hdc-silent symptom — **PARTIAL**

HBC's DEPLOY_SOP.md has an **explicit abort condition for our exact symptom**
(line 10):

> **中止条件**：hdc 返回 `connect-key` / 超时 / `[Fail]Not a directory` /
> `ls` 看到 `drwx` → **立即停手**，不链下一条命令。

"`hdc shell` returned exit 0 with EMPTY STDOUT" is the same family of failure
as the listed `connect-key` / timeout cases — both mean "hdc is no longer a
reliable channel". The SOP rule is: **stop, do not chain the next command, do
not issue reboot**.

W2 violated this: after the empty-stdout shell, the script issued `hdc target
boot` (Stage 3.5 reboot). That was the trigger for the soft-brick — the
device went into an unverified-state reboot, which per HBC's blood-lessons
section line 230 ("`2026-04-21 裸 PID kill`") is one of the two paths to
needing a physical reflash.

HBC does **NOT have a recovery script for an already-soft-bricked board** —
their blood-lessons section literally says "设备需重刷" ("device needs to be
reflashed"). Their `--uninstall` rollback in `deploy_to_dayu200.sh`
(lines 379-390) only works **before** the brick:

```
$HDC shell bm uninstall -n com.example.helloworld
$HDC shell rm -rf /system/android
$HDC shell rm -f /system/bin/appspawn-x
$HDC shell rm -f /system/lib/liboh_adapter_bridge.so /system/lib/libapk_installer.so
$HDC shell rm -f /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json
```

And their `deploy_cmd.txt` "紧急回滚" block (line ~210):

```
hdc shell "for f in \$(find /system -name '*.orig_20260421' 2>/dev/null); do cp \$f \${f%.orig_20260421}; done"
hdc shell "rm -rf /system/android /system/bin/appspawn-x /system/lib/liboh_adapter_bridge.so /system/lib/libapk_installer.so /system/lib/libinstalls.z.so /system/etc/init/appspawn_x.cfg /system/etc/appspawn_x_sandbox.json"
hdc shell reboot
```

— with the explicit caveat **"单文件误推场景; 大事故走物理 recover"**
("single-file misdeploy only; major incidents go physical recover").

**Bottom line:** for a board that's already soft-bricked and hdc-silent, HBC's
own answer is hard power-cycle / physical reflash. No magic hdc-shell-side
recovery exists on the HBC tree.

### 3. Missing .so location — **BOTH FOUND**

| File                         | HBC remote source                                                                                       | Size    | Pulled |
|------------------------------|---------------------------------------------------------------------------------------------------------|---------|--------|
| `libinstalls.z.so`           | `/home/HanBingChen/oh/out/rk3568/packages/phone/system/lib/libinstalls.z.so`                            | 999 088 | YES    |
| `libappexecfwk_common.z.so`  | `/home/HanBingChen/oh/out/rk3568/packages/phone/system/lib/platformsdk/libappexecfwk_common.z.so`       |  21 468 | YES    |

Now in `/home/dspfac/android-to-openharmony-migration/westlake-deploy-ohos/v3-hbc/lib/`.

MD5:
- `libinstalls.z.so`: `bafc989ffcbd4185bcc1f6140d05f260`
- `libappexecfwk_common.z.so`: `a8c74b317410566d386d60acb8b82a45`

Both are mandatory per HBC's SOP:
- `libinstalls.z.so` → Stage 3f, /system/lib/ (644). "adapter 的 BMS install 路径助手，缺则 bm install 链路某些 ContentProvider 写文件操作 fallback 失败" (HBC comment line 415).
- `libappexecfwk_common.z.so` → Stage 3b, /system/lib/platformsdk/ (644), plus symlink `/system/android/lib/libappexecfwk_common.z.so → /system/lib/platformsdk/libappexecfwk_common.z.so` ([8/8] section). Sourced from `out/oh-service/` in HBC's current script (corrected from `out/aosp_lib/` per G2.14ai 2026-05-09); on our pull both source-trees of HBC have this file but `packages/phone/system/lib/platformsdk/` is the canonical staged copy.

W1 inventory undercounted these because the W1 puller targeted HBC's `adapter/out/aosp_lib/` and the SOP's authoritative source for these two files actually lives in `oh/out/rk3568/packages/phone/system/lib*/` (the rk3568-product staged tree).

### 4. Hidden prerequisites in HBC SOP — **YES, three**

HBC's DEPLOY_SOP.md does NOT explicitly call them "prerequisites" but the
following invariants are baked into the script logic and only triggered if
something earlier already put the board in the right state:

**P-A. Stage 2 explicitly forbids stopping appspawn** (DEPLOY_SOP.md line 38):

> 不停 appspawn（OH native appspawn 停了会断 hdc 通信链, 2026-04-21 事故场景）

This matches our exact W2 symptom (empty stdout). The HBC SOP assumes the
deploy script never touches `begetctl stop_service appspawn` — but our V3
deploy-hbc-to-dayu200.sh needs to be re-checked for any such call.

**P-B. Stage 3.0 mkdir must run BEFORE any `hdc file send` to `/system/...`**:

> 一次做完，避免 push 时目录缺失触发 hdc 造目录 quirk

The "hdc造目录 quirk" (DEPLOY_SOP.md line 232-234): if `hdc file send` targets a
path inside a directory that doesn't exist, hdc silently creates the path as a
directory tree (no `mkdir -p` involved) and then tries to write the file —
this corrupts SELinux state and bricks the board. The 2026-04-21 incident
listed at the bottom of the SOP IS this exact mechanism. The Stage 3.0 mkdir
of the four dirs is a *prerequisite*, not optional.

**P-C. Staging dir `/data/local/tmp/stage` must exist before any push**.
HBC's `deploy_stage.sh` calls `ensure_stage_dir()` at the start of every push
stage. The "全局三条" #2 rule (DEPLOY_SOP.md line 9) is **absolute**:

> 绝不 `hdc file send <src> /system/...`。一律 `send 到
> `/data/local/tmp/stage/<basename>` → `ls -la` 验是 `-rw-...` 文件
> （非 `drwx-`）→ `cp` 进 /system。

If we direct-pushed `hdc file send <local> /system/...` (instead of through
staging), and the destination filename collided with the hdc-造目录 quirk,
we may have created `drwx` directories in `/system/lib/` etc., wedging SELinux.
**This is hypothesis H1 / H2 territory** in agent 52's postmortem.

### 5. Postmortem / known-issues docs

- `/home/HanBingChen/adapter/doc/faq.html` (53 KB) — adapter project FAQ
  (could not deep-read without HTML render; not the format for a deploy-time
  reference, and our SOP queries already surfaced the relevant blood-lessons).
- `/home/HanBingChen/adapter/deploy/DEPLOY_SOP.md` "违规事故索引（血泪教训）"
  section (lines 230-234) — TWO incidents documented, both from 2026-04-21.
- HBC has NO `POSTMORTEM*`, `KNOWN-ISSUE*`, or `TROUBLESHOOT*` files. Their
  "lessons learned" mechanism is the `# [P-N] 反复犯错` comment blocks inline
  in `deploy_to_dayu200.sh` (currently P-1 through P-13 — 13 separate
  reproducible mistake categories with root cause + symptom + mitigation +
  evidence anchor + verification).

### 6. CHEN team coordination

- `/home/HanBingChen/adapter/CLAUDE.md` is HBC's project-level coordination doc
  (mentions ECS, target board, source layout, build/deploy isolation rules,
  patch workflow).
- HBC's `readme.txt` documents sync directions: HBC's local Windows tree is
  authoritative for source/patches; ECS is authoritative for build scripts &
  artifacts.
- No project-level CHEN-team-wide cross-org doc exists on HBC's tree (HBC
  works in `/home/HanBingChen/`; we work via login user `chenyue` which has
  read access — confirmed). Our V3-architecture docs are the authoritative
  Westlake-side coordination point.

---

## Recommended W2 re-attempt procedure

### Phase A — Restore hardware to factory baseline (operator-side)

1. **Operator hard power-cycle DAYU200** (unplug barrel + USB for 10 sec, replug).
2. If board does not boot to a usable hdc state within 60 sec:
   - Operator-side **reflash** to factory image (DriverAssitant flash tool from
     Rockchip — outside this agent's reach).
3. Once hdc is responsive: `hdc shell "echo alive"` must return literal `alive`.

### Phase B — Verify factory baseline (before any push)

```bash
hdc list targets                                     # non-empty
hdc shell "echo alive"                               # returns "alive\n"
hdc shell "ls -d /system/android 2>&1"               # must say "No such file"
hdc shell "uname -a"                                 # confirm 32-bit ARM
hdc shell "getenforce"                               # confirm "Enforcing"
hdc shell "pidof hdcd"                               # non-empty (proves channel)
hdc shell "ls /system/lib/*.orig_* 2>/dev/null"      # must be empty (no prior deploy residue)
```

If ANY of these fail, **do NOT proceed**.  Either the brick wasn't cleared
or there's prior deploy residue that needs `rm /system/lib/*.orig_*` + reboot
first.

### Phase C — Update v3-hbc/lib/ with the two missing .so

Already done by this agent:
```
westlake-deploy-ohos/v3-hbc/lib/libinstalls.z.so          (999088 bytes, md5 bafc989ffcbd4185bcc1f6140d05f260)
westlake-deploy-ohos/v3-hbc/lib/libappexecfwk_common.z.so ( 21468 bytes, md5 a8c74b317410566d386d60acb8b82a45)
```

Update `scripts/v3/deploy-hbc-to-dayu200.sh` to push these in their proper
stages (libinstalls → /system/lib/, libappexecfwk_common →
/system/lib/platformsdk/ AND symlink under /system/android/lib/).

### Phase D — Tighten the deploy script per HBC's "全局三条"

Before re-attempting W2, **the V3 deploy script must enforce**:

1. **STAGING ONLY rule.** Every push to `/system/*` must be: send → stage →
   `stat -c '%F'` returns "regular file" → md5 check → cp. Reject any
   `hdc file send <local> /system/...` direct write. Model on
   `deploy_stage.sh:stage_push()` (lines 117-156 on HBC server).
2. **HEALTH PROBE between stages.** After EVERY stage, before the next one
   begins, run `check_hdc_alive` (echo alive). If empty stdout or non-"alive"
   return: **abort, do not chain**. (W2's script failed to do this between
   Stage 3 push and Stage 3.5 reboot.)
3. **drwx GUARD.** After each substage that wrote into `/system/lib/`,
   `/system/lib/platformsdk/`, `/system/android/lib/`,
   `/system/android/framework/arm/`: `ls -la <dir>` and `grep '^d'` must
   not match any of the expected file basenames. If it does, the
   hdc-造目录 quirk fired and the deploy must abort. (Reference:
   `deploy_stage.sh:stage_3b` lines 321-324 + `DEPLOY_SOP.md` line 184.)
4. **NEVER `begetctl stop_service appspawn`.** Only stop foundation +
   render_service in Stage 2 (per DEPLOY_SOP.md line 38).
5. **chcon verification.** After Stage 3c-end and 3e-end chcon batches,
   verify with `ls -lZ` and grep for the expected label. If the chcon
   silently no-op'd (the OH SELinux relabel limitation called out in
   `deploy_to_dayu200.sh` line 114), abort with a clear message — do not
   reboot into a state where appspawn:s0 will SIGABRT-storm.

### Phase E — Re-execute V3-W2 stages (with checkpoints)

Per HBC SOP, do NOT use `deploy_to_dayu200.sh --one-shot`. Use the
stage-by-stage `deploy_stage.sh` model:

| Stage | Command                                | Checkpoint before next stage                       |
|------:|----------------------------------------|----------------------------------------------------|
| 0     | `deploy_stage.sh 0` (preflight)        | "Stage 0 PASS" line + `echo alive`                 |
| 1     | `deploy_stage.sh 1` (13 .orig backups) | Verify `ls /system/lib*/*.orig_${TS}` count == 13 |
| 2     | `deploy_stage.sh 2` (stop 2 services)  | `pidof foundation; pidof render_service; pidof hdcd` returns blank/blank/non-blank |
| 3.0   | `deploy_stage.sh 3.0` (mkdir 4)        | 4 directories stat "directory"                     |
| 3b    | `deploy_stage.sh 3b` (10 OH .so)       | All 10 md5 match + libbms symlink present + no drwx |
| 3c    | `deploy_stage.sh 3c` (38+3 AOSP)       | 41 files md5 match + chcon labels verified + no drwx |
| 3d    | `deploy_stage.sh 3d` (12 jars + ICU)   | All files md5 match                                |
| 3e    | `deploy_stage.sh 3e` (27 boot segs)    | All 27 md5 match + boot.{art,oat,vdex} labels = system_lib_file |
| 3f    | `deploy_stage.sh 3f` (adapter + cfg)   | restorecon ran + chcon verified                    |
| 3.9   | `deploy_stage.sh 3.9` (full verify)    | Manifest 94 files all PASS                         |
| 3.5   | `deploy_stage.sh 3.5` (reboot)         | Poll `echo alive`; verify pidof foundation/render_service/launcher/hdcd all non-empty |
| 4     | `deploy_stage.sh 4` (APK install)      | `pidof com.example.helloworld` non-empty           |

**Operator handoff point:** at any abort, communicate clearly to the operator
"hdc is unhealthy, please power-cycle and re-run from Phase B".

---

## What to add to V3-DEPLOY-SOP.md

Concrete additions to `docs/engine/V3-DEPLOY-SOP.md`:

1. **New §0.1 "Pre-flight invariants (mandatory)"** — copy the 6 commands
   from Phase B above (factory baseline check + hdcd alive + no orig residue).
2. **New §0.2 "Abort conditions (verbatim from HBC SOP §全局三条)"** —
   explicit list: empty stdout / connect-key / timeout / [Fail]Not a
   directory / drwx in ls. **Hard rule: any abort condition → do NOT issue
   reboot.**
3. **New §2.1 "Stage 2 do-not-stop list"** — appspawn (any variant), launcher
   (any variant), hdcd. Cite the 2026-04-21 hdc-failure incident.
4. **Replace §3 "push" with a `stage_push()` requirement** — direct-write to
   `/system/*` is FORBIDDEN. Mandatory staging + stat + md5 + cp.
5. **New §3.0 "mkdir-before-push rule"** — list the 4 mandatory pre-created
   directories. Cite the 2026-04-21 hdc-造目录 incident.
6. **New §3.X "post-stage health probe"** — between every stage, `echo alive`
   must return literal `alive`. Specify the abort path.
7. **New §3.Y "chcon verification rule"** — after every chcon batch,
   `ls -lZ` + grep for the expected SE label. If silently no-op'd, abort.
8. **New §X "Recovery"** — operator-side hard power-cycle is the ONLY path
   for a soft-bricked board. Document `--uninstall` for pre-brick rollback
   and `find /system -name '*.orig_${TS}' -exec cp` for single-file
   misdeploy. Acknowledge "大事故走物理 recover" — HBC has no shell-side
   recovery for a wedged hdc channel.
9. **Update §"missing libs"** — both `libinstalls.z.so` and
   `libappexecfwk_common.z.so` are now in `v3-hbc/lib/`. Push paths +
   chmod 644 + symlink (libappexecfwk_common only).

---

## Hard constraints honored

- READ-ONLY on HBC server (all `ssh` calls were `cat`/`ls`/`grep`/`find`;
  no edits, no `rm`, no `cp`, no `mkdir`).
- Two `.so` files pulled to local `v3-hbc/lib/` via `scp` (under same-team
  CR-EE license).
- No git push; commit-only locally.

## Self-audit

- [x] All 6 query categories executed.
- [x] V3-W2-RECOVERY-PROCEDURE.md written.
- [x] `libinstalls.z.so` + `libappexecfwk_common.z.so` pulled and verified
      (md5 + size).
- [x] No remote modifications.
- [x] Honest "found nothing for X" where applicable (e.g., no
      already-bricked-board recovery exists on HBC tree).
