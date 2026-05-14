# OHOS Phase 2 — MVP Validation Workstreams

**Date:** 2026-05-14
**Hardware:** Yue-D200 / DAYU200 dev board (rk3568, Cortex-A55 ×4, aarch64 userspace, OHOS 7.0.0.18 Beta1)
**Goal:** smallest possible visible proof that Westlake can run unmodified Android APKs on OpenHarmony, before committing to the full 13-person-day Phase 2 roadmap (`CR41_PHASE2_OHOS_ROADMAP.md`).

---

## Board pre-flight findings (already validated 2026-05-14)

| Check | Result |
|---|---|
| hdc connection | ✅ `dd011a414436314130101250040eac00` via USB on Windows host |
| Kernel | Linux 6.6.101 SMP aarch64 Toybox (Apr 2026 build) |
| Userspace bitness | aarch64 (`uname -m` → `aarch64`, `ld-musl-arm.so.1` is OHOS musl-aarch64 naming) |
| CPU | 4× Cortex-A55 with FP/ASIMD/AES/CRC32/atomics/asimddp |
| Storage | `/data` 19 GB free / `/system` 857 MB free |
| **Binder devices** | ✅ `/dev/binder`, `/dev/hwbinder`, `/dev/vndbinder` all present |
| Logging | `hilog` available at `/system/bin/hilog` |
| Existing dalvik/art | ❌ none (we ship our own) |
| Native lib paths | `/system/lib/`, `/system/lib64/`, `/vendor/lib*` populated with OHOS Z-libs |
| App runtime | ArkUI / Ark VM (`/bin/ark_aot`, `/bin/ark_aptool`) — not relevant to us, OHOS uses ArkTS/JS instead of Java |

## Already verified on board (free wins)

- ✅ `dalvikvm` (5.8 MB aarch64 static binary from `dalvik-port/build-ohos-aarch64/`) **runs** on the board — `--help` output prints correctly, exit code 0
- ❌ **VM init SIGSEGVs** after `[V/dalvikvm] Using executionMode 1` — first bug to fix in MVP-0

These two together mean: we don't need to cross-compile from scratch, we just need to find one bug.

---

## Workstream A — MVP-0: dalvikvm executes "Hello OHOS"

**Goal:** prove the JVM itself runs on the board.

### Open work

1. **OHOS-MVP-001 — Debug dalvikvm VM init SIGSEGV on aarch64 OHOS** (BLOCKER)
   - Repro: `./dalvikvm -cp HelloOhos.dex HelloOhos` → SIGSEGV after `[V/dalvikvm] Using executionMode 1`
   - Likely causes:
     - Bootstrap classloader can't load `boot.oat` / `boot.art` (we have them in `/home/dspfac/.claude/projects/-home-dspfac-openharmony/`, need to push)
     - Missing libnativehelper / libcrypto co-deps
     - OHOS musl differs from Android bionic for `sysconf(_SC_NPROCESSORS_ONLN)` or similar
     - TLS layout difference (Phase 1 had a `__init_tls` patch for ARM32 — `ohos-sysroot-arm32/usr/lib/libc_static_fixed.a` — may need aarch64 equivalent)
   - Approach: enable `-verbose:jni,class,gc`, capture last log line before crash, look for missing files via `strace` (board likely doesn't have strace; use `LD_DEBUG=files` if dynamic) OR add `printf`-style breadcrumbs to dalvikvm source.

2. **OHOS-MVP-002 — Boot classpath staging on board**
   - Push `boot-aosp-shim.{art,oat,vdex}`, `boot-core-icu4j.{art,oat,vdex}`, core-libart.jar to board (`/data/local/tmp/westlake/bcp/`)
   - Pass `-Xbootclasspath` correctly
   - Verify VM picks them up (`-verbose:class` should show class loading from BCP)

3. **OHOS-MVP-003 — HelloOhos test harness**
   - Build script: compile Java → dx/d8 → bundle as `.dex`
   - Wrapper: `scripts/run-ohos-hello.sh` that takes a class name + dex path + pushes + runs
   - Expected output: `westlake-dalvik on OHOS — main reached` to stdout

**Success criterion:** `hdc shell "/data/local/tmp/westlake/dalvikvm -cp HelloOhos.dex HelloOhos"` prints the marker string and exits 0.

**Estimated effort:** 1-2 days (most of it in OHOS-MVP-001 SIGSEGV debug).

---

## Workstream B — MVP-1: Trivial APK loads + Activity.onCreate runs ✅ PASS 2026-05-14

**Goal:** prove a synthetic Android Activity executes through the V2 substrate on OHOS.

**Result:** PASS. `OhosTrivialActivity.onCreate reached pid=4954` printed from the board (dalvikvm stdout). Driver `scripts/run-ohos-test.sh trivial-activity` returns 0 end-to-end. See `artifacts/ohos-mvp/mvp1-trivial/20260514_135137/`.

### Landed (post-MVP-0 fixes — what actually unblocked MVP-1)

4. **OHOS-MVP-005 — Stage V2 substrate BCP on board** ✅
   - Pushed `aosp-shim-ohos.dex` (4.9 MB, dex.035), `core-android-x86.jar` (1.2 MB, dex.035), `direct-print-stream.jar` to `/data/local/tmp/westlake/bcp/`.
   - Did **NOT** ship `framework.jar` (Android-phone's is dex.039, unloadable by dalvik-kitkat). Instead we rebuilt the AOSP shim WITHOUT `scripts/framework_duplicates.txt` stripping — produces a non-slim shim that carries `ContextThemeWrapper`, `Bundle`, `Process`, etc. directly.
   - Did **NOT** ship `core-kitkat.jar` (missing `java.util.concurrent.CopyOnWriteArrayList` etc.). Used the richer `dalvik-port/core-android-x86.jar`.

5. **OHOS-MVP-006 — Trivial Activity** ✅
   - `ohos-tests-gradle/trivial-activity` — single `MainActivity extends Activity` that logs marker via both `Log.i` AND `System.out.println` (Log alone doesn't reach stdout on standalone OHOS, so the test app belt-and-suspenders).
   - Re-dexed via `d8 --min-api 13` to land at dex.035 (the default dex.037 is unloadable).

6. **OHOS-MVP-007 — Minimal OhosMvpLauncher** ✅
   - New module: `ohos-tests-gradle/launcher/` (110 LOC).
   - Path: `Class.forName(...) → newInstance() → new Instrumentation().callActivityOnCreate(activity, null) → callActivityOnDestroy(activity) → exit(0)`.
   - **ZERO Unsafe / setAccessible / per-app branches** — fully compliant with the macro-shim contract. Uses only public API methods on classes we own (Activity, Instrumentation, Bundle).
   - Replaces the heavy `NoiceProductionLauncher` for MVP-1; same path will scale to apps that load by APK once `DexClassLoader` is verified on board.

7. **OHOS-MVP-004 — Cross-compile aosp-libbinder for OHOS aarch64 musl** (deferred)
   - Not required for MVP-1 (Activity.onCreate doesn't touch binder for this minimal app). Deferred to MVP-2/MVP-3.

**Success criterion:** ✅ `MainActivity.onCreate` ran; `OhosTrivialActivity.onCreate reached pid=4954` printed.

**Actual effort:** ~1 hour after MVP-0 PASS (the four-layer fix above; no dalvikvm internals work needed).

### Reproducer

```bash
cd /home/dspfac/android-to-openharmony-migration
bash scripts/run-ohos-test.sh trivial-activity
# Look for: "MVP-1 PASS: marker found"
# and:      "marker line: OhosTrivialActivity.onCreate reached pid=..."
```

---

## Workstream C — MVP-2: Visible UI on OHOS display

**Goal:** paint a red square (or any visible content) on the DAYU200's display from a Westlake-hosted APK.

### Open work

8. **OHOS-MVP-008 — Port M6 surface daemon's consumer side to OHOS**
   - Phase 1 M6 daemon writes frames to a `memfd` via DLST pipe
   - Phase 2 consumer: OHOS XComponent / native window instead of Android SurfaceView
   - Aligns with CR41 `M12` roadmap entry

9. **OHOS-MVP-009 — Trivial setContentView(View) drawing red**
   - APK extends MVP-1's trivial activity
   - View subclass with `onDraw(c) { c.drawColor(Color.RED) }`
   - Verify pixels reach display

10. **OHOS-MVP-010 — Memory-mapped framebuffer route (fallback)**
    - If OHOS XComponent integration is hard, fallback: write directly to `/dev/fb0` (we did this in Phase 1 OHOS full-integration work — see `project_full_ohos_integration.md`)
    - Won't be production-grade but proves visual pipeline

**Success criterion:** screenshot of DAYU200 display showing red square rendered from APK via Westlake.

**Estimated effort:** 2-3 days after MVP-1.

---

## Workstream D — Infrastructure (supports A/B/C in parallel)

11. **OHOS-MVP-011 — Build script `scripts/run-ohos-test.sh`**
    - One-stop: `./run-ohos-test.sh HelloOhos`
    - Compiles, dexes, pushes, runs, captures logs
    - Mirrors Phase 1's `scripts/run-noice-westlake.sh`

12. **OHOS-MVP-012 — Captured-evidence dir `artifacts/ohos-mvp/`**
    - Screenshot per milestone
    - logcat capture per milestone
    - hardware setup notes

---

## Open GitHub issues (created with this workstream doc)

Issues will be opened as `PF-ohos-mvp-001..012` with the work items above, labeled `enhancement` + `mvp` + `phase2`.

## Order of attack

```
OHOS-MVP-001 (debug SIGSEGV)    ← critical blocker, MVP-0
       │
       ▼
OHOS-MVP-002 + 003 (BCP + harness)   ← finishes MVP-0
       │
       ▼
OHOS-MVP-004 + 005 (libbinder + V2 substrate on board)
       │
       ▼
OHOS-MVP-006 + 007 (trivial APK + launcher)   ← finishes MVP-1
       │
       ▼
OHOS-MVP-008 + 009 + 010 (display)   ← finishes MVP-2
```

Workstream D (infrastructure) runs continuously alongside.

## Honest scope statement

This document scopes ~5 days of focused work to land MVP-0/1/2. The full Phase 2 roadmap (real noice or McD on OHOS) is the existing `CR41_PHASE2_OHOS_ROADMAP.md` — about 13 person-days on top of the MVP. The MVP exists to:

1. **De-risk the cross-compile + runtime story** before committing to full porting
2. **Generate concrete artifacts** (screenshots, logs) for stakeholder buy-in
3. **Identify the actual hard problems** vs. estimated hard problems

If MVP-0 succeeds cheaply (e.g., the SIGSEGV is a simple TLS fix), MVP-1+2 may proceed faster than estimated. If MVP-0 requires significant dalvikvm internals work, that itself is the signal to reconsider Phase 2 architecture.

## Reproducer (current state)

```bash
# From WSL on Windows host (board connected via USB):
HDC=/mnt/c/Users/dspfa/Dev/ohos-tools/hdc.exe

# Verify board is reachable
$HDC list targets        # should show: dd011a414436314130101250040eac00

# dalvikvm is already on board after our preflight
$HDC shell "/data/local/tmp/dalvikvm"          # ⇒ usage message (exit 0) — VM binary works
$HDC shell "/data/local/tmp/dalvikvm -cp HelloOhos.dex HelloOhos"  # ⇒ SIGSEGV after "Using executionMode 1"
```

## Key file pointers

- Dalvikvm aarch64 binary: `dalvik-port/build-ohos-aarch64/dalvikvm`
- Dalvikvm source: `/home/dspfac/dalvik-kitkat/` + patches at `dalvik-port/patches/`
- Cross-toolchain: `dalvik-port/ohos-sysroot-arm32/` (need aarch64 variant — see OHOS-MVP-004)
- V2 substrate: `shim/java/android/app/Westlake{Activity,Application,ActivityThread}.java`
- Compiled shim dex: `aosp-shim.dex` (1.3 MB after slimming)
- Phase 1 launcher pattern: `aosp-libbinder-port/test/NoiceProductionLauncher.java`
- Phase 1 binder regression: `scripts/binder-pivot-regression.sh` (14/14 PASS on Android phone)
