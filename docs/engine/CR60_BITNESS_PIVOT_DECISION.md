# CR60 — Bitness Pivot Decision (32-bit dalvikvm on DAYU200)

**Date:** 2026-05-14
**Author:** strategic review (outside reviewer + on-board empirical verification)
**Status:** DECISION — spike approved, 3-5 day bound
**Supersedes:** the implicit pre-CR60 assumption that aarch64 was the right dalvikvm target on DAYU200
**Does not supersede:** any landed code (M6 daemon, DRM/KMS direct, V2 substrate). All preserved.

---

## TL;DR

We pivot dalvikvm to 32-bit ARM on DAYU200 because **the board's userspace is 32-bit only**. The current 64-bit dalvikvm cannot `dlopen` any OHOS native library (XComponent, AudioRenderer, network) because they're all 32-bit, so it talks to them through cross-arch IPC (M6 daemon + AF_UNIX). Matching bitness eliminates the daemon for the production path. Switching back to 64-bit later — if a 64-bit OHOS ROM ships — is ~2-4 days of revalidation, not a rewrite, because everything except the dalvikvm binary itself is bitness-neutral.

---

## Empirical evidence on the board

```
$ hdc shell uname -m              → aarch64       (kernel)
$ hdc shell getconf LONG_BIT      → 32            (userspace)
$ hdc shell ls /system/lib64      → No such file or directory
$ hdc shell ls /vendor/lib64      → No such file or directory
$ hdc shell file /system/bin/sh   → 32-bit LSB arm, EABI5, dyn (/lib/ld-musl-arm.so.1)
$ hdc shell file render_service   → 32-bit LSB arm, EABI5, dyn (/lib/ld-musl-arm.so.1)
```

Not a single 64-bit user-space binary on this DAYU200 ROM. Kernel can execute 64-bit binaries (our aarch64 dalvikvm runs), but no OHOS library has a 64-bit variant to load.

---

## What this invalidates and what it preserves

### Invalidates (assumption-only, not code)

- The pre-flight finding "Userspace bitness: aarch64" in `OHOS_MVP_WORKSTREAMS.md` (corrected in this commit).
- The path of least resistance from "MVP-2 visible pixel" → "noice on OHOS." That path implicitly required calling OHOS XComponent/Audio APIs in-process or building a cross-arch bridge per service.

### Preserves (all landed code stays)

| Artifact | Why kept |
|---|---|
| aarch64 dalvikvm at `dalvik-port/build-ohos-aarch64/` | Still primary target for Android phones (OnePlus 6 etc.) and any future 64-bit OHOS ROM |
| M6 daemon (`c32a219e`) | Production path for 64-bit dalvikvm; fallback for boards without `dlopen`-able OHOS libs |
| M6DrmClient + POSIX bridge (`204a8fa0`) | Same — still the right answer when bitness doesn't match |
| DRM/KMS direct (`44686464`) | Still useful as a non-compositor fallback regardless of bitness |
| V2 substrate (`shim/java/android/app/Westlake*.java`) | Pure Java, bitness-neutral |
| BCP dex (`aosp-shim-ohos.dex`, `core-android-x86.jar`, `direct-print-stream.jar`) | dex is arch-neutral |
| Hilt CR59 fix | Pure Java, bitness-neutral |
| `OhosMvpLauncher`, gradle modules, test activities | Java + dex, bitness-neutral |

So the pivot is **additive**, not destructive. We're adding a 32-bit target alongside the existing 64-bit one, not deleting either.

---

## What 32-bit dalvikvm enables

```
[dalvikvm 32-bit] ── dlopen libace_napi.z.so       ──→ XComponent NAPI in-process
                  ── dlopen libnative_window.so    ──→ OH_NativeWindow_RequestBuffer in-process
                  ── dlopen libaudio_renderer.z.so ──→ Audio in-process
                  ── dlopen libnetwork_*.z.so      ──→ Network APIs in-process
```

No daemon. No memfd. No AF_UNIX. No cross-arch binder. The dalvikvm process is just another OHOS process, calling OHOS APIs the way an OHOS app would.

This is the same architectural pattern Android uses on real Android phones — `app_process` is the same bitness as the framework libs it loads.

---

## Cost of the pivot

| Step | Cost | Notes |
|---|---|---|
| E1. Rebuild dalvikvm-arm32 with current source | ~½ day | Artifacts already exist at `dalvik-port/build-ohos-arm32/` (stale by 2 months). `dalvik-port/build-ohos.sh` already supports both arches. |
| E2. Port 4-layer aarch64 SIGSEGV fix to ARM32 | ~½-1 day | 3 of 4 layers are arch-agnostic. The `u4` cast widening fix is N/A on 32-bit (pointers are already 32-bit). |
| E3. Validate MVP-0 (`HelloOhos.dex`) on 32-bit | ~½ day | Same BCP, same dex. Only the JNI compat layer needs ARM32 build. |
| E4. Validate MVP-1 (`OhosTrivialActivity`) on 32-bit | ~½ day | Pure Java path; should "just work" if MVP-0 does. |
| E5. Wire XComponent in-process | ~1-2 days | `System.loadLibrary` + NAPI bridge. Far simpler than the current daemon route. |
| E6. Bitness-as-parameter discipline | ~½ day | `--arch` flag in driver, auto-detect via board's `getconf LONG_BIT`, both arches in CI. |

**Total: 3-5 days, hard bound.** If MVP-0 doesn't run on 32-bit dalvikvm by day 5, the spike is killed and we resume the 64-bit + M6 daemon path. Either outcome is a useful answer.

---

## Cost of reverse-pivot (if 64-bit OHOS ROM ships on a later board)

Switching back to 64-bit is essentially **free** if we keep both build targets alive:

| Step | Cost |
|---|---|
| Build dalvikvm-aarch64 | Already built. Zero. |
| Validate MVP-0 on 64-bit ROM | ½-1 day |
| Validate MVP-1 + in-process XComponent on 64-bit | 1-2 days |
| Flip default in `scripts/run-ohos-test.sh` | trivial |

**Total: 2-4 days of validation, no rewrite.**

The reason this is cheap: every layer above the dalvikvm binary is bitness-neutral (dex, Java, daemon C code that compiles either way). The 32-bit pivot is **not a one-way door**.

---

## Discipline rules going forward

These rules make the bitness pivot reversible and prevent us from accidentally creating a one-way door:

1. **All native code uses `intptr_t` / `uintptr_t` / `size_t` for pointers and pointer-sized integers.** No `(int)pointer`, no `(long)pointer`. The aarch64 `u4` cast bug we hit earlier is the canonical anti-example.
2. **Build both arches in CI.** Cheap (~30 sec each); catches regressions instantly.
3. **No `#ifdef __aarch64__` or `__arm__` branches in shim Java or JNI bridge** unless absolutely necessary. Prefer runtime-detected behavior.
4. **`scripts/run-ohos-test.sh` detects board bitness** via `hdc shell getconf LONG_BIT` and picks the matching binary automatically. Override with `--arch arm32` / `--arch aarch64` for explicit testing.
5. **Macro-shim contract still applies** unchanged on the Java side. JNI/native is exempt from the contract (existing rule), so adding the in-process XComponent JNI shim does not violate.

---

## Open questions deferred to the spike

- Does the ARM32 musl differ from aarch64 musl in any syscall surface our code uses? (Probably no, but verify.)
- Does the `core-android-x86.jar` BCP have any aarch64-only assumptions? (dex is arch-neutral but verify by running.)
- Does Hilt CR59 fix re-validate cleanly on 32-bit? (Pure Java + reflection; should be free.)
- Does `libcore_bridge.cpp` have any `(int)pointer` casts that bitrotted to aarch64-only? (Audit during E1.)
- Does the existing `dalvik-port/ohos-sysroot-arm32/usr/lib/libc_static_fixed.a` work for our dynamic-libs-in-process model, or do we need a fresh dynamic-linkage sysroot variant? (Phase 1 ARM32 dalvikvm was static; for in-process `dlopen` we need a dynamic binary.)

---

## Decision authority

Strategic question raised by user; outside reviewer agent flagged 32-bit pivot as the single most important correction; user empirically verified board bitness via `hdc shell`; this CR captures the decision. Spike is bounded at 3-5 days with explicit go/no-go at day 5.

---

## Cross-references

- `docs/engine/OHOS_MVP_WORKSTREAMS.md` — Workstream E (this CR's implementation plan)
- `docs/engine/CR41_PHASE2_OHOS_ROADMAP.md` — M11/M12 (cross-arch work that becomes optional if E succeeds)
- `docs/engine/CR59_REPORT.md` — Hilt unblock that preceded this decision
- Memory: `project_ohos_mvp_pipeline.md` — current MVP state
- Memory: `feedback_macro_shim_contract.md` — unchanged; still applies to Java
- Memory: `feedback_bitness_as_parameter.md` — new feedback memory capturing the discipline rules
