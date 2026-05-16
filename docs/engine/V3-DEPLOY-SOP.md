# Westlake Deployment Standard Operating Procedure (V3-DEPLOY-SOP)

> Project-level authoritative deploy protocol.  Borrowed and adapted from
> sibling HBC's `DEPLOY_SOP.md v4` (2026-04-21).  Every `hdc` / `adb` push
> that lands binaries on a real device MUST follow this SOP.  Authored
> 2026-05-16 as part of W9 (CR-FF Pattern 2).

## Why this document exists

Westlake has been informal about device-side staging.  We've burned cycles
on `hdc` "造目录 quirks" (write to non-existent /system path creates a
directory, not a file), SELinux label drift after `hdc file send` (kernel
assigns the parent-dir's default label, not the policy label), and forgotten
`restorecon` runs.  HBC sedimented its lessons into a 5-stage SOP; this
document is Westlake's adaptation, covering BOTH:

  * **V2 Phase 1 Android path** — `adb push` of `dalvikvm` runtime,
    `aosp-shim.dex`, `framework.jar`, app APK, etc. to
    `/data/local/tmp/westlake/` on a rooted OnePlus 6 / generic Android
    target.  Lower risk because we never touch `/system`.
  * **V3 Phase 2 OHOS path** — `hdc file send` of `appspawn-x`, OH
    service `.so` overlays, framework jars, boot image, namespace linker,
    SELinux file_contexts to `/system/` on a DAYU200.  Higher risk:
    bricking `/system` requires a full reflash via burn tool.

The Tier-1 protocol below applies to both; the Tier-2 stage 3 SOP applies
only to V3.

---

## Tier 1 — Three global laws (apply to ALL Westlake deploys)

These three rules originated in HBC's SOP §"全局三条" and are non-negotiable
for any Westlake deploy script as well:

1. **No bare-PID kill / no launcher touch.**  Never run `kill <numeric_pid>`
   against an OH foundation/launcher/render PID — those values rotate as
   `hdcd` recycles them and you may end up killing `hdcd` itself, severing
   your only access to the board.  Use `begetctl stop_service <name>` on
   OHOS or `am force-stop <package>` on Android.  Never `killall com.ohos.launcher`
   on DAYU200 — once `foundation` is stopped it exits on its own.

2. **Pushing to /system MUST go through `/data/local/tmp/stage/`.**  Never
   call `hdc file send <src> /system/...` directly.  This trips the
   `hdc` "造目录 quirk": if the target path doesn't already exist as a
   file, the daemon creates a *directory* of that name and silently
   succeeds.  The mandatory pattern is:

   ```bash
   # On host:
   hdc file send "$src" "/data/local/tmp/stage/$basename"
   # On device:
   hdc shell "ls -la /data/local/tmp/stage/$basename"  # MUST be -rw-... not drwx
   hdc shell "cp /data/local/tmp/stage/$basename /system/<dst>"
   ```

   `ls -la` returning `drwx-` is the abort signal — stop immediately
   and investigate.  The same pattern applies to `adb push` on Android,
   though `/system` is read-only on most Android builds so we generally
   avoid it entirely (see V2 path below).

3. **Abort conditions — stop on first sign of trouble.**  Any of these
   means **STOP, do not chain the next command**:
     * `hdc` / `adb` returns `connect-key` or `[Fail] device offline`
     * any `hdc file send` times out
     * `[Fail] Not a directory` from cp/mv
     * `ls -la` of a freshly-pushed file shows `drwx-` (it's a directory)
     * `pidof hdcd` returns empty after a `begetctl stop_service` call

---

## V2 — Phase 1 Android path (/data/local/tmp/)

Used by:
  * `scripts/run-noice-westlake.sh`
  * `scripts/run-mcd-westlake.sh`
  * `scripts/sync-westlake-phone-runtime.sh`
  * any phone-side `adb` deploy of the V2 binder-pivot substrate

### V2 Stage 0 — Pre-flight

```bash
"$ADB" devices                                       # device listed?
"$ADB" shell "echo alive"                            # device responsive?
"$ADB" shell "su -c 'id'"                            # root available?  (V2 needs su)
"$ADB" shell "ls /data/local/tmp/westlake/" 2>/dev/null  # confirm layout
```

If `su -c id` fails the V2 path is impossible on this board — V2 binder-pivot
runs need root for `setenforce 0` (SELinux) and for `chmod 04755 dalvikvm`.

### V2 Stage 1 — Push to /data/local/tmp/ (not /system)

The V2 protocol is intentionally `/system`-free.  All runtime binaries go to
`/data/local/tmp/westlake/`.  `adb push` directly to that path is safe
because:
  * the kernel default label is `app_data_file:s0`, which is
    `dalvikvm`-accessible
  * `adb push` does NOT exhibit hdc's 造目录 quirk for `/data/local/tmp/`
    (the directory always exists, never auto-created with wrong label)
  * `/data/local/tmp/` is writable without root

```bash
"$ADB" push "$LOCAL/aosp-shim.dex"        "/data/local/tmp/westlake/aosp-shim.dex"
"$ADB" push "$LOCAL/framework.jar"        "/data/local/tmp/westlake/framework.jar"
"$ADB" push "$LOCAL/app.apk"              "/data/local/tmp/westlake/app.apk"
# ... etc.
"$ADB" shell "ls -la /data/local/tmp/westlake/" | head -20   # verify -rw- on each
```

Some sub-paths (e.g. helper launchers) do need root to land in a
non-`/data/local/tmp/` location — those use the V2 staging variant:

```bash
"$ADB" push "$LOCAL/.m7-launch-daemons.sh" "/data/local/tmp/.m7-launch-daemons.sh"
"$ADB" shell "su -c 'cp /data/local/tmp/.m7-launch-daemons.sh /data/local/tmp/westlake/'"
```

This pattern (push-then-su-cp) is already in `run-noice-westlake.sh` and is
the V2 analog of HBC's Stage-3 staging discipline.

### V2 Stage 2 — Verify

```bash
"$ADB" shell "ls -la /data/local/tmp/westlake/"                 # listing
"$ADB" shell "md5sum /data/local/tmp/westlake/aosp-shim.dex"     # vs local md5sum
"$ADB" shell "test -x /data/local/tmp/westlake/dalvikvm && echo ok"
```

If `md5sum` mismatches, `adb push` was truncated — re-push, do not proceed.

### V2 Stage 3 — Launch verification

```bash
bash scripts/binder-pivot-regression.sh --quick
# Expect: 13 PASS, 1 SKIP (noice-discover in --quick mode), 0 FAIL.
# (The classic 14/14 regression — sm_smoke through PackageServiceTest.)
```

If `--quick` ever shows a FAIL, do not proceed to `--full`.

---

## V3 — Phase 2 OHOS path (/system/, DAYU200)

Used by:
  * `scripts/v3/deploy-hbc-to-dayu200.sh` (W1 deploy driver)
  * `scripts/v3/aa-launch.sh`
  * any future `westlake-restore.sh --board-only` invocation

### V3 Stage 0 — Pre-flight

```bash
"$HDC" -t "$HDC_SERIAL" list targets                 # device visible?
"$HDC" -t "$HDC_SERIAL" shell "echo alive"           # responsive?
"$HDC" -t "$HDC_SERIAL" shell "ls /system/android"   # factory state -> "No such file or directory"
"$HDC" -t "$HDC_SERIAL" shell "ls /system/bin/appspawn-x"  # already-deployed state
```

Pick a clear start state: factory or already-deployed.  Do not start
pushing on top of a partial deploy without first running the un-overwrite
half of `westlake-restore.sh --board-only`.

### V3 Stage 1 — Device-side backup (HBC §Stage 1)

Per HBC's SOP, 13 device-side files MUST be backed up before any Stage 3
push lands.  Always make backups on the device itself (not via
`hdc file recv`) — `cp X X.orig_${TS}` is faster and the backup never leaves
the device:

```bash
TS=$(date +%Y%m%d)
"$HDC" shell "mount -o remount,rw /"
# 13 files — see HBC DEPLOY_SOP.md §Stage 1 for the exact list.
```

### V3 Stage 2 — Stop services (HBC §Stage 2)

Only stop these two:
```bash
"$HDC" shell "begetctl stop_service foundation"
"$HDC" shell "begetctl stop_service render_service"
```

**Never** stop `appspawn` (it owns the hdc transport — stop it and the
device goes offline).  **Never** kill `com.ohos.launcher` (it exits naturally
once `foundation` is down).

### V3 Stage 3 — Push (staging discipline mandatory)

Every push to `/system/...` MUST use the staging template, never direct.
The mandatory pattern:

```bash
push_file_to_system() {
    local src="$1" dst="$2"
    local base
    base=$(basename "$dst")
    "$HDC" file send "$src" "/data/local/tmp/stage/$base"
    "$HDC" shell "ls -la /data/local/tmp/stage/$base" | grep -q '^-' || {
        echo "ABORT: staging area shows a directory, not a file"
        exit 1
    }
    "$HDC" shell "cp /data/local/tmp/stage/$base $dst"
}
```

**KNOWN GAP, 2026-05-16:** `scripts/v3/deploy-hbc-to-dayu200.sh` currently
calls `hdc file send` directly to `/system/...` targets (its `push_file`
function bypasses the staging area).  This script's `push_file`
implementation needs to be reworked to use the staging template above.
Tracked separately; do not run this script on a freshly-flashed factory
DAYU200 until that gap is closed.  When manually deploying, override
each `push_file` call with the function above.

Stage 3 substages (3b…3f) and `chcon` / `restorecon` rules are identical to
HBC's SOP — see `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md` §3 for
the canonical list.  Do not duplicate that 90-line table here; defer.

### V3 Stage 3.9 — Integrity and SELinux verification

After all substages complete:

```bash
# 1. Existence + size check
"$HDC" shell "ls -la /system/lib/ /system/android/lib/ /system/android/framework/arm/ | head"

# 2. md5 round-trip — local file MUST match device file
for f in <list>; do
    md5sum local/$f
    "$HDC" shell "md5sum /system/.../$f"
done

# 3. No accidental directory entries (the 造目录 quirk)
"$HDC" shell "ls /system/android/lib/ /system/android/framework/arm/ | grep '^d'"
# Expected output: empty.  Any drwx- entry = stop, investigate.

# 4. SELinux label gates
"$HDC" shell "ls -lZ /system/etc/fonts.xml | grep system_fonts_file"
"$HDC" shell "ls -lZ /system/bin/appspawn-x | grep appspawn_exec"
```

Any failure here = STOP.  Do not reboot.

### V3 Stage 3.5 — Reboot + health

```bash
"$HDC" shell "sync; reboot"
# Poll: hdc shell "echo alive" until response
"$HDC" shell "pidof foundation render_service com.ohos.launcher hdcd"
# All four must be non-empty.
"$HDC" shell "hilog | grep -i 'BMS.*ready'" | head
```

### V3 Stage 4 — APK verification

```bash
"$HDC" file send "out/app/HelloWorld.apk" "/data/local/tmp/HelloWorld.apk"
"$HDC" shell "bm install -p /data/local/tmp/HelloWorld.apk"
"$HDC" shell "bm dump -n com.example.helloworld"   # should print label
"$HDC" shell "aa start -a com.example.helloworld.MainActivity -b com.example.helloworld"
"$HDC" shell "pidof com.example.helloworld"        # process running?
```

Or use `scripts/v3/aa-launch.sh` as a wrapper — it calls into this SOP
implicitly.

---

## Script compliance — which scripts use this SOP?

| Script                                         | Path covered | SOP-compliant? |
|------------------------------------------------|--------------|---------------:|
| `scripts/run-noice-westlake.sh`                | V2 Android   | YES — uses /data/local/tmp/ only, no /system writes |
| `scripts/run-mcd-westlake.sh`                  | V2 Android   | YES — same pattern as noice |
| `scripts/sync-westlake-phone-runtime.sh`       | V2 Android   | YES — /data/local/tmp/westlake/ only |
| `scripts/v3/deploy-hbc-to-dayu200.sh`          | V3 OHOS      | **PARTIAL** — pushes direct to /system, no staging area (TODO 2026-05-16) |
| `scripts/v3/aa-launch.sh`                      | V3 OHOS      | YES — only invokes `aa start`, no /system writes |
| `scripts/v3/run-hbc-regression.sh`             | V3 OHOS      | YES (read-only — never pushes) |
| `scripts/westlake-restore.sh --board-only`     | V3 OHOS      | YES — see W9 Pattern 3 |

`scripts/v3/run-hbc-regression.sh` performs a lightweight SOP-compliance
probe as part of its artifact-discovery phase: it greps `deploy-hbc-to-dayu200.sh`
for any `hdc file send` line that targets `/system/...` directly and emits
a PASS-with-warn (exit 77) if found.  This drift-detection complements the
WARN line above; the regression itself does NOT abort on it.

---

## Reference

* HBC `DEPLOY_SOP.md` v4 — the original SOP this document is adapted from.
  Read-only reference; do not edit in place.  Path:
  `westlake-deploy-ohos/v3-hbc/scripts/DEPLOY_SOP.md`.
* `docs/engine/CR-FF-HBC-BORROWABLE-PATTERNS.md` — Westlake's analysis of
  which HBC patterns to borrow.  Pattern 2 = this SOP.
* `docs/engine/V3-RESTORE.md` — companion document for the restore-time
  half of the discipline (W9 Pattern 3).
