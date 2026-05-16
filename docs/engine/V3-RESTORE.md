# Westlake Restore — usage and recipe book

> Companion document to `scripts/westlake-restore.sh`.  W9 Pattern 3
> (CR-FF, 2026-05-16).  Authoritative recipe for getting both the
> DAYU200 board and the local working tree back to a known-good
> baseline after a partial deploy, abandoned experiment, or
> mid-handoff state.

## TL;DR

```bash
# What does the script think the world looks like right now?
bash scripts/westlake-restore.sh --verify

# Restore the DAYU200 only (stop our daemons, clear scratch areas).
bash scripts/westlake-restore.sh --board-only

# Restore the local working tree only (clean known-noisy build caches).
bash scripts/westlake-restore.sh --tree-only

# Both at once (default).
bash scripts/westlake-restore.sh

# Show what would happen without doing it.
bash scripts/westlake-restore.sh --all --dry-run
```

## When to run it

* **Start of a new handoff.**  Run `--verify` first to see what the prior
  agent left.  Then `--all` (or specific halves) to bring the world back
  to baseline.
* **Before benchmark / regression runs.**  Stale daemons or leaked
  scratch directories can perturb timings.  `--board-only` is the
  pre-benchmark friction-free reset.
* **After a `hdc`/`adb`-related script crashes mid-flight.**  Particularly
  V3 deploys that fail between Stage 2 (services stopped) and Stage 3.9
  (integrity verified).  `--board-only` brings services back up.
* **Before pushing a commit you're unsure about.**  `--tree-only --verify`
  prints every untracked dir and tracked-file drift, so you can spot
  accidental clutter before `git add`.

## What it does — five-second mental model

The script splits into two independent halves:

| Half        | Phase | Action                                                          |
|-------------|-------|-----------------------------------------------------------------|
| Board (V3)  | A1    | Stop Westlake daemons by name (NOT by PID).                     |
|             | A1b   | Probe foundation/render_service; restart them if a prior deploy stopped them. |
|             | A2    | Scan /system for `.orig_*` backups left by HBC Stage 1 — *warn only*. Auto-restore is intentionally manual to avoid soft-bricking. |
|             | A3    | Wipe scratch areas: `/data/local/tmp/westlake/` and `/data/local/tmp/stage/`. |
|             | A4    | Read-only SELinux state report.                                 |
| Tree        | B0    | Verify we're in a git repo.                                     |
|             | B1    | Branch sanity print (NEVER auto-switches).                      |
|             | B2    | Detect sibling agents under `.claude/worktrees/` and warn.      |
|             | B3    | Remove a hard-coded `SAFE_CLEAN_DIRS` list (gradle cache, real-mcd live-check dumps, etc.). |
|             | B4    | Census untracked top-level dirs (warn-only).                    |
|             | B5    | Census tracked-file drift (warn-only).                          |

## What it does NOT do (intentional)

Listed explicitly so future agents don't try to "improve" the script and
break the contract:

* **No `git reset --hard`.**  Too much loss potential.  If you want to
  reset tracked files, do it yourself after reading the B5 census.
* **No `git clean -fdx`.**  Same reason.  Untracked files are listed in
  B4; the human/agent decides per path.
* **No bare-PID kills.**  Per HBC Tier-1 rule #1 (see V3-DEPLOY-SOP §Tier
  1).  Daemons stop by name; OHOS services by `begetctl`.
* **No /system overwrite without staging.**  Per V3-DEPLOY-SOP §V3 Stage
  3.  The script's board phase only deletes from scratch areas; it
  never writes to /system, ever.
* **No touching `.claude/worktrees/`.**  Other live agents may be working
  there.  Detected via NEVER_TOUCH_DIRS.
* **No touching `.git/`.**  Obvious.
* **No touching `westlake-deploy-ohos/v3-hbc/`.**  HBC artifacts are
  treated as read-only.

## Adding to `SAFE_CLEAN_DIRS`

The hard-coded list inside `westlake-restore.sh` is the only place a
path becomes "auto-clean-eligible."  Rules for adding to it:

1. The directory MUST be reliably regeneratable from build/test inputs
   present in git.  If clearing it would require a slow network fetch or
   user re-typing, do not add.
2. It MUST NOT contain user work or session state.  No `.gradle/caches/`
   (cleared by Gradle itself, no need); no `out/` (intentional output);
   no `artifacts/` root (contains analysis logs).
3. It MUST be repo-relative (no leading `/` or `..`).
4. Add a comment line above the new entry explaining the trade-off.

Bad adds in the past would have hit:
  * `westlake-host-gradle/.gradle/` — recovering from this is a 5-minute
    Gradle warmup; we only auto-clean the `buildOutputCleanup` subdir
    which is purely a Gradle-internal lock.
  * `westlake-deploy-ohos/v3-hbc/` — explicitly NEVER_TOUCH.

## Integration with the regression suite

`scripts/v3/run-hbc-regression.sh` invokes
`westlake-restore.sh --verify --tree-only` as one of its checks:

* `--verify` ensures zero side effects.
* `--tree-only` skips the board half (the regression suite has its own
  board-reachability probes; double-probing wastes time).
* Exit codes are mapped to suite verdicts:
  * 0 → PASS (clean tree)
  * 3 → PASS-with-warn (drift detected; the common case)
  * anything else → FAIL

This makes drift visible on every regression run without forcing the
suite to be heavyweight.

## Recipes

### Recipe 1 — "Prior agent left the DAYU200 mid-deploy."

Symptom: `hdc shell pidof foundation` returns empty, `/system/lib/`
contains files with timestamps from the past hour.

```bash
bash scripts/westlake-restore.sh --board-only --verify
# Read the .orig_* census output carefully.
# If there are .orig_* files, manually restore them — the script
# intentionally does not auto-restore.
bash scripts/westlake-restore.sh --board-only
# Then re-run the regression to confirm.
bash scripts/v3/run-hbc-regression.sh --quick
```

### Recipe 2 — "Local tree feels cluttered after a long session."

```bash
bash scripts/westlake-restore.sh --tree-only --verify
# Inspect the B4/B5 census.  If everything in B5 (drift) is expected,
# leave them; if not, `git checkout` the surprises individually.
bash scripts/westlake-restore.sh --tree-only
# B3 cleans the obvious caches.  Re-running --verify should show
# fewer warnings the second time.
```

### Recipe 3 — "Hand-off after the loaded gun — I don't know what's where."

```bash
bash scripts/westlake-restore.sh --all --verify | tee /tmp/restore-verify.log
# Take 30 seconds to read /tmp/restore-verify.log.  It tells you:
#   - whether the board is reachable
#   - whether prior daemons are still running
#   - what .orig_* backups exist on /system
#   - what untracked dirs at the top level
#   - what tracked files have drift
# Then decide: --board-only? --tree-only? --all?
```

## See also

* `scripts/westlake-restore.sh` — the script itself (~370 LOC).
* `docs/engine/V3-DEPLOY-SOP.md` — the deploy half of the discipline
  (W9 Pattern 2).
* `docs/engine/CR-FF-HBC-BORROWABLE-PATTERNS.md` — the analysis that
  motivated all three W9 patterns.
* `westlake-deploy-ohos/v3-hbc/scripts/restore_after_sync.sh` — HBC's
  original (read-only reference).  Solves a related-but-different
  problem (AOSP/OH source-tree restoration after repo sync); cited
  here only as inspiration.
