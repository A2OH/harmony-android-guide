# V3-REGRESSION

How to run, interpret, and extend `scripts/v3/run-hbc-regression.sh` — the
V3 (HBC-runtime / Phase 2 OHOS) analog of the Phase 1 V2 master regression
script `scripts/binder-pivot-regression.sh`.

Authored as the day-1 deliverable to close W11 audit risk **R2**
("V3 has no regression suite analog to `binder-pivot-regression.sh`. Don't
wait until W7 to discover we have no consolidated test entry point.").
See `docs/engine/V3-W11-CARRYFORWARD-AUDIT.md` §R2.

---

## TL;DR

```bash
# Local-only artifact discovery (no DAYU200 needed). ~5s.
bash scripts/v3/run-hbc-regression.sh --quick --no-board

# Artifact discovery + DAYU200 smoke probes. ~30s.
bash scripts/v3/run-hbc-regression.sh --quick

# Everything, including every W-slot. Currently mostly SKIPs. ~1-3min.
bash scripts/v3/run-hbc-regression.sh --full
```

Exit code `0` = OK to land your change. Exit code `1` = something a slot
owner declared mandatory regressed. `2` = setup error.

## What the suite tests

The suite runs in three sections:

### Section 1 — local V3 artifact discovery

Verifies the locally-staged V3 substrate exists at
`westlake-deploy-ohos/v3-hbc/`. These are the files an agent pulls down
with W1 ("HBC artifact inventory + pull") and pushes to DAYU200 with W3
("appspawn-x integration"). The script does **not** push them — it only
asserts they're present on the host so subsequent stages have something
to send.

Checks:

* `v3-hbc/lib/` populated (at least 10 native libs).
* `v3-hbc/jars/` populated (at least 5 boot jars).
* `v3-hbc/bcp/boot.art` present (anchor file of the boot image).
* `v3-hbc/bin/appspawn-x` present (the executable that boots HBC).
* `docs/engine/V3-ARCHITECTURE.md` present.
* `docs/engine/CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` present.
* `docs/engine/V3-REGRESSION.md` (this file) present.

### Section 2 — DAYU200 smoke probes (READ-ONLY)

Probes the board's state but **never writes**. Skips cleanly when run
without board access (`--no-board`).

Probes:

* `hdc list targets` shows the configured serial.
* `getconf LONG_BIT` returns `32` (per CR60 bitness pivot — DAYU200
  userspace is 32-bit even though the kernel is aarch64).
* `getenforce` returns `Enforcing`.
* `ls $BOARD_DIR/v3-hbc/` returns the deployed V3 tree (PASS-with-warn
  if W3 hasn't pushed yet).

### Section 3 — workstream slots

One function per workstream that owns an end-to-end test:

| Slot | Workstream | Recommended fill-in |
| ---- | ---------- | ------------------- |
| `w2_slot` | W2 — Boot HBC runtime standalone | Launch `appspawn-x` on board; assert HBC boots without SIGSEGV. |
| `w3_slot` | W3 — `appspawn-x` integration | Deploy `v3-hbc/` to board; assert fork + dlopen of `libart_runtime_stubs.so`. |
| `w5_slot` | W5 — Mock APK validation | Push trivial-activity APK; assert MainActivity.onCreate marker in hilog. |
| `w6_slot` | W6 — noice on V3 | Push noice APK; assert Welcome screen renders. |
| `w7_slot` | W7 — McD on V3 | Push McD APK; assert SplashActivity reached. |

On day 1 each slot returns 99 (SKIP) and prints
`W<N> owner not yet implemented`. The suite verdict is still PASS so the
script is safe to wire into CI / pre-push hooks from day 1.

---

## Verdict vocabulary

V3 makes "PASS-with-warn" a first-class outcome — important because most
V3 artifacts are expected-but-not-yet-present on day 1.

| Verdict | Counts as | Meaning |
| ------- | --------- | ------- |
| **PASS** | PASS | Test ran and succeeded. |
| **PASS-with-warn** | PASS (with warning) | An expected-future artifact is missing, or a board probe found a known-pending state (e.g. `v3-hbc/` not yet deployed). Surfaced in summary so reviewers see what's still pending. |
| **SKIP** | neither | Slot owner has not implemented the test yet. The day-1 default for every W-slot. |
| **FAIL** | FAIL | HARD failure. Suite exits non-zero. |

The summary line is tab-separated to match the V2 format:

```
Results:    7 PASS    3 WARN    0 FAIL    5 SKIP    total=12    elapsed=11s
```

Verdict aggregation:

* Any FAIL → suite exits 1.
* No FAIL, any WARN → suite exits 0, banner says `PASS-with-warn`.
* No FAIL, no WARN → suite exits 0, banner says `ALL PASS`.

---

## How a W-owner adds a real test

Each W-slot is a single bash function near the bottom of
`scripts/v3/run-hbc-regression.sh`. To replace a stub with a real test:

1. Find the function `w<N>_slot()` (e.g. `w5_slot` for W5 mock-APK).
2. Replace the body with your test command. Print one line of
   human-readable detail to stdout.
3. Return code mapping:
   * `0` → PASS
   * `77` → PASS-with-warn (an expected-future artifact is still pending,
     but what you do verify is OK)
   * `99` → SKIP (e.g. dependency artifact absent in a way the slot
     considers tolerable)
   * any other non-zero → FAIL
4. If your test needs shared helpers, add them to `scripts/v3/lib/`
   (create the directory) and `source` them from this file. Don't bloat
   the main script with utilities.
5. Re-run the suite to confirm your slot now reports PASS.

### Required-vs-expected artifact helpers

When your slot needs to assert a file exists:

```bash
# Use when your workstream has landed the artifact. Missing => FAIL.
check_required_artifact "label" "$path" [min-file-count]

# Use when a future workstream will produce it. Missing => PASS-with-warn.
check_expected_artifact "label" "$path" [min-file-count]
```

Promote an `expected` check to `required` once your workstream actually
lands the artifact — that's how the suite gradually tightens.

---

## Hard rules

These are non-negotiable. Future maintainers, please uphold:

1. **READ-ONLY on the board.** No `hdc file send`. No
   `hdc shell rm/mv/touch`. Push scripts live elsewhere
   (`scripts/run-ohos-test.sh push-bcp`, future `scripts/v3/push.sh`).
   This script only probes.
2. **Idempotent.** Re-running back-to-back must give the same verdict
   modulo board uptime drift. No side effects.
3. **Self-contained.** Don't depend on artifacts that aren't either
   declared expected (and checked with `check_expected_artifact`) or
   built by another regression run earlier in the pipeline.
4. **Never break V1/V2.** Do not edit `scripts/binder-pivot-regression.sh`,
   `scripts/run-ohos-test.sh`, or any V1/V2 helper. Phase 1 V2
   Android-phone regression must still be 14/14 PASS per
   V3-WORKSTREAMS.md §W12 acceptance.
5. **Failure modes are explicit.** A test cannot silently pass because
   an artifact is missing — it must either FAIL (declared mandatory) or
   PASS-with-warn (declared expected-future).

---

## Environment overrides

| Variable | Default | Purpose |
| -------- | ------- | ------- |
| `HDC` | `/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe` | hdc binary |
| `HDC_SERIAL` | `dd011a414436314130101250040eac00` | DAYU200 serial |
| `BOARD_DIR` | `/data/local/tmp/westlake` | on-device deploy root |
| `REPO_ROOT` | auto-detected | repo root |
| `V3_LOCAL_DIR` | `$REPO_ROOT/westlake-deploy-ohos/v3-hbc` | local v3 tree |

---

## Acceptance check for this scaffold (day 1)

Run on the current tree, with DAYU200 unreachable:

```bash
bash scripts/v3/run-hbc-regression.sh --quick --no-board
```

Expected: exit 0, banner `V3 REGRESSION SUITE: PASS-with-warn` or
`ALL PASS`. Section 1 PASSes (artifacts exist locally), Section 2 SKIPs
(no board), Section 3 skipped (`--quick`).

Then on a host with DAYU200 reachable:

```bash
bash scripts/v3/run-hbc-regression.sh --full
```

Expected: exit 0, Section 1 PASS, Section 2 mostly PASS (last probe
PASS-with-warn until W3 deploys `v3-hbc/`), Section 3 all SKIP.

As workstreams land, each W-slot flips from SKIP → PASS. At G7 (V2-OHOS
substrate archived per V3-WORKSTREAMS.md §W13), the V3 suite is the
single "did I break anything?" entry point — analogous to today's V2
master script.

---

## References

* `docs/engine/V3-W11-CARRYFORWARD-AUDIT.md` §R2 — origin of this scaffold.
* `docs/engine/V3-WORKSTREAMS.md` §W2/W3/W5/W6/W7 — slot owners.
* `docs/engine/V3-ARCHITECTURE.md` — what the HBC runtime is.
* `docs/engine/CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` — libipc-via-HBC
  amendment that defines the runtime boundary the slots test.
* `scripts/binder-pivot-regression.sh` — the V2 master script this
  scaffold mirrors (Phase 1 Android-phone, 14 tests at HEAD).
* `scripts/run-ohos-test.sh` — V2-OHOS hdc helpers; same hdc/serial
  conventions used here.
