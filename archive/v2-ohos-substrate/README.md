# archive/v2-ohos-substrate/

Superseded code from V2-OHOS direction (dalvik-kitkat on OHOS). Preserved
via `git mv` so blame/history remains intact.

**Authoritative planning docs:**
- `docs/engine/V3-W11-CARRYFORWARD-AUDIT.md` — wider catalog (51 components)
- `docs/engine/V3-W12-CR61.1-CODE-DISPOSITION.md` — CR61-justified subset
  with per-component reasoning
- `docs/engine/V3-ARCHITECTURE.md` §4 — V3 deletion catalog + LOC accounting
- `docs/engine/CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` §5 — migration impact

W13 execution commits (2026-05-16):
- `archive: dalvik-port OHOS substrate tree (W13)` — 87 files
- `archive: ohos-tests-gradle modules (W13)` — 33 files + settings.gradle.kts
- `archive: ohos-deploy V2-OHOS substrate artifacts (W13)` — 83 files
- `archive: V2-OHOS scripts + banner run-ohos-test.sh (W13)` — 2 files + banner

## What's archived here

| Subdirectory | Source | Why archived |
|---|---|---|
| `dalvik-port/` | `dalvik-port/` | dalvik-kitkat cross-compile pipeline (Makefile, launcher.cpp, compat/, patches/, locale-patch/, javacore-stub/, testapp/) — superseded by HBC's libart.so + framework.jar |
| `ohos-tests-gradle/` | `ohos-tests-gradle/` (7 of 8 modules) | MVP-0/1/2 + CR60-followup test APKs — superseded by HBC's real libhwui + framework.jar; `:xcomponent-test` STAYS in original location |
| `ohos-deploy/` | `ohos-deploy/` (everything except `aosp-shim.dex`) | V2 OHOS deploy tree: aosp-shim-ohos.dex, core-android-x86.jar, dalvikvm, liboh_bridge.so, boot images, arm64 segments — superseded by HBC's BCP + boot image |
| `scripts/` | `scripts/build-shim-dex-ohos.sh`, `scripts/run-apk-ohos.sh` | Build/launch wrappers for now-archived artifacts |

## What's NOT archived (stays in original location)

These remain in their original locations because the Phase-1 Android-phone
path (cfb7c9e3 OnePlus 6) still depends on them, or because they are
CR60-followup work independent of CR61's prohibition.

- `dalvik-port/ohos-sysroot/` — forensic cross-compile sysroot (referenced
  by `scripts/run-ohos-test.sh` and `dalvik-port/compat/m6-drm-daemon/build.sh`)
- `dalvik-port/ohos-sysroot-arm32/` — used by Phase-1 ArkUI ARM32 work
  (`project_arkui_arm32_tls_fix.md`)
- `ohos-deploy/aosp-shim.dex` — Phase-1 Android-side shim (despite the
  `ohos-deploy/` directory name; the OHOS variant was
  `aosp-shim-ohos.dex` which IS archived)
- `ohos-tests-gradle/xcomponent-test/` — CR60-followup in-process OHOS NDK
  API call test (HBC integration forensic)
- `scripts/run-ohos-test.sh` — REFACTORed in place with [SUPERSEDED-V3]
  banner; `status`/`push-bcp` subcommands may still be useful for forensic
  rebuild
- `aosp-libbinder-port/out/{musl,bionic}/` — Phase-1 Android-phone libbinder
  builds; powers 14/14 binder-pivot regression. CR61.1 §3.A retains for
  Android-phone path.
- `aosp-{audio,surface}-daemon-port/out/bionic/` — Phase-1 Android-phone M5
  + M6 daemons (audio AUDIBLE, surface 60 Hz vsync). OHOS-target builds
  were never produced under CR61 prohibition.
- `shim/java/` — Phase-1 Westlake-shadowed framework classes. CR61.1
  preserves; V3 OHOS path uses HBC's real `framework.jar` but Phase-1
  Android STILL builds and ships `aosp-shim.dex` from these sources.
- `WestlakeLauncher.java`, all `Westlake*Service.java` services — Phase-1
  load-bearing.
- `westlake-host-gradle/` — Phase-1 Android host APK (Option 3 in-process
  driver for noice + McD).

## To restore (un-archive) a component

```bash
# Inspect history first to confirm restoration target
git log --follow archive/v2-ohos-substrate/<path>

# Then move back
git mv archive/v2-ohos-substrate/<path> <original-path>
git commit -m "unarchive: <path> (justification)"
```

If you un-archive an `ohos-tests-gradle/` module, also uncomment its
`include(":<module>")` line in `ohos-tests-gradle/settings.gradle.kts`.

If you un-archive `dalvik-port/Makefile`, you may need to restore the
`build-ohos-*/` output dirs (currently archived alongside as untracked
build artifacts).

## Risks of restoration

1. **Conflicts with V3 HBC-runtime artifacts at `westlake-deploy-ohos/v3-hbc/`**
   — the archived V2-OHOS pieces target dalvikvm-arm32 against the
   V2-OHOS BCP layout; HBC ships its own `libart.so` + 9-segment boot
   image. Mixing the two on-device may produce undefined behavior.
2. **May reintroduce CR61 prohibition violations** (libipc / samgr
   direct linkage) — superseded by CR61.1, which permits libipc usage
   via HBC's `liboh_adapter_bridge.so` only. See
   `docs/engine/CR61_1_AMENDMENT_LIBIPC_VIA_HBC.md` §6 self-audit greps.
3. **May reintroduce dalvik-kitkat/aosp-shim-ohos.dex framework-substitute
   pattern** — V3 commits to HBC's real `framework.jar`; the V2 substrate
   class set (CR62/CR63/CR64 fixes intertwined) is no longer needed.
4. **The CR60 32-bit ARM bitness discipline still applies** — see
   `docs/engine/CR60_BITNESS_PIVOT_DECISION.md`. Pointer-sized ints in
   restored code must remain `intptr_t`/`size_t`.

## Forensic rebuild

The `dalvik-port/Makefile`'s `TARGET=ohos`/`ohos-arm32`/`ohos-arm32-dynamic`/
`ohos-arm32-shlib` branches are preserved verbatim. To rebuild for forensic
purposes:

```bash
cd archive/v2-ohos-substrate/dalvik-port
make TARGET=ohos-arm32 -j$(nproc)        # static arm32
make TARGET=ohos-arm32-dynamic -j$(nproc) # dynamic PIE arm32
make TARGET=ohos-arm32-shlib -j$(nproc)   # libdvm_arm32.so (CR-BB W0)
```

The sysroot symlinks at `dalvik-port/ohos-sysroot{,-arm32}/` (NOT archived)
are still referenced.

## Reversibility

Each archive commit in W13 is a single `git mv` group with descriptive
message. To revert a single group entirely:

```bash
git revert <commit-sha>
```

Or restore a single subtree:

```bash
git mv archive/v2-ohos-substrate/<subtree> <original-location>
```

The `git mv` preserves full file history; `git log --follow
archive/v2-ohos-substrate/dalvik-port/Makefile` shows the file's
complete development history back to its original creation.
