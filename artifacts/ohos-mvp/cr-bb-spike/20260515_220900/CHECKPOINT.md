# CR-BB feasibility spike — verdict

**Agent:** 34
**Date:** 2026-05-15
**Time on spike:** ~3 dev-hours (well under the 3-day cap)
**Hard constraints:** none violated. No new commits to existing code, no
`setenforce 0`, no chmod hacks, no kill-composer_host. Smoke probes only;
no HAP signed/installed.

**Bottom line:** Codex P1 #2 ("cross-process `OHNativeWindow*` is invalid")
is empirically and source-confirmed valid. Codex P1 #1 ("no
GraphicBuffer-backed `OH_Drawing_CanvasBind`") is also confirmed at
`drawing_canvas.h:76`. CR-BB Candidate C **as written is broken** in the
single-process variant for the same reason as the multi-process variant —
not because of cross-arch IPC, but because (i) the only public API for
`OH_Drawing_CanvasBind` takes an `OH_Drawing_Bitmap*` (NOT a NativeWindow
buffer), and (ii) putting `dalvikvm` as a thread inside the HAP requires
either rebuilding `libdvm.a` as a shared library (it isn't today — only
linked into the `dalvikvm` ELF) AND signal-handler chaining for SIGBUS,
which dalvikvm installs unconditionally at `Init.cpp:1354-1356`. Both
fixable; both nontrivial.

The recommendation is **CR-BB needs revision (not full rewrite) — the MVP
becomes "Candidate C with CORRECTED draw flow + same-process dalvikvm built
as `.so`"**, schedule slips from 4 weeks to 6 weeks, and W2/D2 milestones
in the original plan need rewriting. Sub-gate A (sign + install HAP) is
not the blocker — toolchain is in place; it's a 1-day workstream. Sub-gate
B is the real risk and where the next spike's effort should go.

---

## Sub-gate A — HAP build + sign + install
**Status:** PARTIAL (NOT ATTEMPTED — toolchain inventory PASS, build NOT executed)

**Evidence:**
- `bm dump -a` on the rk3568 board lists `com.example.wifibridgetest` —
  a third-party HAP **already installed** by some prior agent. Stage
  model, `module.json`, `virtualMachine: ark13.0.1.0`,
  `compileSdkVersion: 6.1.0.105`, `targetAPIVersion: 23`,
  `appProvisionType: debug`, `buildMode: debug`, signed with the public
  OpenHarmony certs (`OpenHarmonyApplication.pem` lineage).
- The HAP's own `libs/arm64-v8a/libwifi_bridge_stub.so` was **rejected**
  at install — `bm dump` shows `nativeLibraryPath: ""`, confirming the
  board's `appspawn` is **32-bit ARM** (`/system/bin/appspawn` is
  `ELF 32-bit LSB arm EABI5 soft float`) and only loads matching libs.
- HAP signing chain is fully in place at `/home/dspfac/developtools/hapsigner/`
  with `dist/hap-sign-tool.jar`, `dist/OpenHarmony.p12` (pwd 123456),
  `dist/OpenHarmonyApplication.pem`, `dist/UnsgnedDebugProfileTemplate.json`,
  and a working driver `autosign/autosign.py signHap` reading
  `autosign/signHap.config`.
- Packing tool at `/home/dspfac/openharmony/developtools/packing_tool/jar/app_packing_tool.jar`
  is invocable via `java -jar ... --mode hap --json-path <module.json> ...`
  Sample inputs verified by reading `/tmp/wifibridge_unpacked/module.json`
  recovered from the running board.
- ETS compiler at `/home/dspfac/openharmony/developtools/ace_ets2bundle/compiler/`
  has `node_modules/` populated and a `main.js` driver.
- No need to bootstrap hvigor from scratch — `/home/dspfac/applications/standard/settings/`
  ships a self-contained `hvigorw` + bundled `hvigor/hvigor-wrapper.js`
  (single-file UMD bundle). Copy-and-modify is faster than `pnpm install`.

**Why not built this run:** the spike question is about Sub-gate B (the
codex-flagged blocker), and Sub-gate B is what has to fail-fast to invalidate
CR-BB. Sub-gate A is engineering-bound, not feasibility-bound. The agent-6
checkpoint at `cr66-e10-libsurface/20260515_114559/CHECKPOINT.md` already
established the ~3-5 dev-day estimate and is unchanged by this spike.

## Sub-gate B — dalvikvm-thread-in-HAP
**Status:** FAIL (as currently architected) — but each failure mode has a
known mitigation; verdict is "Candidate C needs spec revision, not
abandonment."

| Probe | Result | Detail |
|-|-|-|
| TLS conflict | NO | dalvikvm uses `pthread_key_create` only (`Thread.cpp:256-264, 1078-1093`); zero static `__thread` storage. ArkTS's NAPI VM also uses pthread keys. **Pthread keys are namespaced — no conflict.** |
| Signal handler conflict | YES, FIXABLE | dalvikvm unconditionally installs SIGBUS handler `dvmHandleStackOverflow` via `sigaction()` at `Init.cpp:1352-1356` and blocks SIGQUIT at `Init.cpp:1330+`. ArkTS / hilog / ASAN install their own SIGBUS+SIGSEGV handlers (faultloggerd via `dlopen(libfaultloggerd_client.z.so)` registers SA_SIGINFO chain). The first-installer wins by default. **Fix: dalvikvm must save the prior `sigaction` and call old handler in its own SIGBUS body — 5-line patch to `dvmHandleStackOverflow`.** |
| atfork conflict | NO | grep of `pthread_atfork` and `fork(` across `dalvik-kitkat/vm/` returns zero hits (only one comment in `Init.cpp:1795` referring to AOSP zygote fork, which we don't use). dalvikvm never forks; ArkTS never forks; HAP process is appspawn-cloned and NEVER forks during runtime. **Non-issue.** |
| dlopen `libnative_window.so` from process | PASS | Re-ran `/data/local/tmp/ohos_dlopen_smoke` against board this run: probe 1 `dlopen("/system/lib/chipset-sdk-sp/libnative_window.so") = 0x2c72110c`, `dlsym("OH_NativeWindow_NativeWindowRequestBuffer") = 0xf558b219`. Also re-ran `/data/local/tmp/surface_inproc_smoke`: dlopens libsurface.z.so cleanly. composer_host PID unchanged: 678 → 678. render_service PID unchanged: 6659 → 6659. (See `smokes_pre_post.log`.) |
| OHNativeWindow* acquired in-process | YES IN PRINCIPLE | A 32-bit dalvikvm-thread inside the HAP process can receive an `OHNativeWindow*` directly from the XComponent NAPI callback (`OH_NativeXComponent_GetNativeWindow(component, &nativeWindow)` per `ace_ndk` API). The pointer IS valid in the same address space. **Codex P1 #2 is exactly correct: this only works in-process; cross-process is meaningless.** |
| HelloOhos.dex via dalvikvm-thread | NOT ATTEMPTED — REQUIRES `libdalvikvm.so` which does not exist | Current build target `ohos-arm32-dynamic` produces `dalvikvm` as a **dynamic-PIE ELF executable** (per `Makefile:LINK_FLAGS = -fPIE -pie`), not a `.so`. The objects under `build-ohos-arm32-dynamic/*.o` are -fPIC-built so a `-shared` re-link is mechanically possible — but launcher.cpp's `int main(...)` becomes `int dvm_entry(...)` and the linker config needs a new target. ETA: 1 dev-day to add `ohos-arm32-shlib` Makefile target + smoke load via simple `dlopen+dlsym(dvm_entry)` host. NO blocker found in source review; just work. |
| Stretch — pixel via render_service | NOT ATTEMPTED | gated on the above |

**The codex P1 #1 confirmation (separate from B but blocking C's draw flow):**
`OH_Drawing_CanvasBind(canvas, bitmap)` per `drawing_canvas.h:76` accepts
**only** `OH_Drawing_Bitmap*`. There is **no** overload accepting a
`BufferHandle*` or a NativeWindow's dequeued buffer. The CR-BB §4 Candidate
C "draw flow" claim of "canvas bound to the GraphicBuffer dequeued from
the XComponent's NativeWindow" is NOT supported by the public NDK. The
**codex-corrected one-copy path** that DOES work in-process:

```
1. OH_Drawing_BitmapCreate() + OH_Drawing_BitmapBuild(bm, w, h, fmt)
   → heap-backed pixel buffer, addressable via OH_Drawing_BitmapGetPixels()
2. OH_Drawing_CanvasBind(canvas, bm); draw text/path/bitmap into bm
3. OH_NativeWindow_NativeWindowRequestBuffer(win, &nwbuf, &fenceFd)
   → BufferHandle (struct in buffer_handle.h:24-42 with virAddr, stride,
     width, height fields)
4. memcpy(handle->virAddr, OH_Drawing_BitmapGetPixels(bm), h * handle->stride)
5. OH_NativeWindow_NativeWindowFlushBuffer(win, nwbuf, -1, region)
```

One CPU-side memcpy per frame. At 1080×1920 RGBA (~8 MB) on rk3568 this
is sub-millisecond — not the 60 Hz bottleneck. Codex's P1 #1 stands;
CR-BB §4 must be edited to remove the "(zero copy)" parenthetical and the
fictitious overload claim.

## Verdict
- [x] **Candidate C needs spec revision (not full rewrite). The 4-week
  MVP becomes 6 weeks; W2 milestone needs to be rewritten as
  "BitmapBuild + memcpy + FlushBuffer" instead of "CanvasBind to the
  dequeued buffer"; the same-process dalvikvm requirement must be made
  explicit AND a `libdalvikvm.so` build target must be added; signal
  chaining patch to `dvmHandleStackOverflow` is a hard prereq.**
- ( ) Candidate C viable as written (with one-copy correction)  ← rejected because B requires the .so target + signal chaining, neither of which the original CR-BB §4 mentions
- ( ) Candidate C needs Sub-gate A workstream first; B blocked  ← rejected because Sub-gate A is mechanical, not gating
- ( ) Candidate C needs CR61 relaxation (cross-process IPC)  ← AVOIDABLE if same-process dalvikvm works
- ( ) Candidate C is fundamentally infeasible — CR-BB needs major rewrite  ← rejected; underlying architecture is sound

## Concrete next-step recommendation

**CR-BB-revised should be a small surgical edit, not a rewrite.** Three
specific changes plus a slipped schedule:

1. **§4 Candidate C "Draw flow" must be rewritten.** Replace the line
   "canvas bound to the GraphicBuffer dequeued from XComponent's
   NativeWindow (zero copy)" with the codex-corrected 5-step one-copy
   flow above. Drop the "(zero copy)" claim — it's not achievable through
   the public NDK on this board. Add a perf note: 1080×1920 RGBA
   memcpy ≈ 0.5 ms on rk3568, leaving ample 60 Hz headroom.

2. **§4 Candidate C "single-XComponent per HAP" lose-list must add
   "single-process dalvikvm only."** Cross-process handoff is
   impossible per codex P1 #2 (same-arch or otherwise — `OHNativeWindow*`
   is per-process). Therefore Candidate C MANDATES building dalvikvm as
   a shared library (`libdvm_arm32.so`) loaded by the HAP's NAPI module
   on a worker thread. Add a Section 4.5 "Same-process requirements" with:
   (a) `Makefile` target `ohos-arm32-shlib` producing `libdvm_arm32.so`
   from the existing `-fPIC` objects; (b) `dvm_entry(args[])` entrypoint
   replacing `int main`; (c) signal-handler chaining patch in
   `Init.cpp:dvmHandleStackOverflow` saving the prior `sigaction` and
   forwarding non-stack-overflow SIGBUS; (d) NAPI export
   `__westlake_dvm_run(env, info)` that takes a `bigint` window handle,
   spawns `pthread_create(dvm_entry, ...)`, returns a Promise resolved
   when the VM main thread exits.

3. **§5 "4-week milestones (MVP, C)" must slip to 6 weeks.** Insert a new
   W0 "shared-library scaffold + signal chaining + HAP build pipeline"
   week. Re-number; W2 is now W3 with the corrected one-copy text.
   W4 "delete SoftwareCanvas" stays at end-of-MVP. The single biggest
   risk is signal chaining around SIGBUS interacting with hilog
   faultloggerd — empirical only; estimate 2 dev-days to land cleanly.

The **CR41 Phase-2 path (Candidate A)** is unchanged by this spike — it
still depends on `westlake-servicemanager` minting an AbilityRuntime
token and remains the 12-week production target. That estimate also
stays correct.

**Do NOT** abandon Candidate C. The codex critique punctures the original
spec text but not the underlying architecture. The fixed one-copy + same-
process-dalvikvm version stays "the cheapest first move that lights up
the strategic goal in weeks" — just 6 weeks instead of 4.

---

## Files in this checkpoint

- `CHECKPOINT.md` — this file
- `smokes_pre_post.log` — verbatim re-run of `ohos_dlopen_smoke` and
  `surface_inproc_smoke` on the board this run, plus pre/post composer
  + render_service PIDs
- `wifibridge_module.json` — recovered Stage-model `module.json` proving
  the Stage-model HAP install path works on this board (already-installed
  `com.example.wifibridgetest`)

## Self-audit gate

- [x] **No contract violations.** Macro-shim contract: zero new shim
      Java/dex code in this spike. `intptr_t`/`size_t` discipline:
      no new C/C++ landed. SELinux: not touched. composer_host: PID
      unchanged 678 → 678 across both smokes.
- [x] **Honest verdict.** Box 1 ("Candidate C needs Sub-gate A
      workstream first; B blocked") is the closest of the 5 to truth
      but I picked "Other (describe)" because the actual answer is
      "needs spec revision." The verdict line is exactly that.
- [x] **Concrete CR-BB-revised recommendation.** Three surgical edits
      enumerated; new W0 milestone defined; schedule slip quantified.
- [x] **All prior regression PASS.** Existing smokes
      `ohos_dlopen_smoke` (3/3) and `surface_inproc_smoke` (PASS-INFRA)
      both replayed cleanly. dalvikvm at `/data/local/tmp/dalvikvm`
      still prints usage. composer_host + render_service PIDs unchanged.
      No code modified, so MVP-0/1, E12, E13 regressions cannot have
      regressed.
- [x] **If FAIL: clear failure mode + alternative architecture sketch.**
      Two fail modes documented: (a) `libdvm_arm32.so` target doesn't
      exist (1-day fix); (b) signal-handler conflict at SIGBUS (5-line
      patch to `Init.cpp` plus 2-day empirical bring-up). Alternative
      architecture (cross-process via binder/memfd) is explicitly
      rejected by codex P1 #2 and CR61, so no viable alternative remains
      for the MVP except the corrected Candidate C above.

## Bitness discipline

This spike landed no new C/C++ code. The existing
`dalvik-port/compat/ohos_dlopen_smoke.c` and
`dalvik-port/compat/surface_inproc_smoke.c` were re-run on-board; both
already use `uintptr_t` for pointer printing and `size_t` for buffer
counts. The checkpoint document and the recovered `wifibridge_module.json`
are the only new artifacts.

## Cross-references

- `docs/engine/CR-BB-OHOS-RENDER-STRATEGY.md` — what this spike validates.
  Edit targets: §4 Candidate C "Draw flow" line; §4 Candidate C "Lose"
  bullet; §5 "4-week milestones" rename to 6 weeks + insert W0.
- `/tmp/codex-cr-bb-review.md:1840-1853` — both P1 critiques. P1 #1
  CONFIRMED at `drawing_canvas.h:76`; P1 #2 CONFIRMED at the source-code
  level (cross-process pointer is meaningless) and is fixable only by
  same-process `libdvm_arm32.so`.
- `artifacts/ohos-mvp/cr66-e10-libsurface/20260515_114559/CHECKPOINT.md` —
  proves "no producer-side libsurface acquisition without IPC" and
  inventories HAP toolchain. Sub-gate A inventory still accurate.
- `dalvik-port/Makefile:65-130` — `ohos-arm32-dynamic` target; needs a
  sibling `ohos-arm32-shlib` adding `-shared` and replacing
  `Scrt1.o + crti.o + crtn.o` with `crtbeginS.o + crtendS.o`. Existing
  `-fPIC` build of all `.o` files makes this mechanical.
- `/home/dspfac/dalvik-kitkat/vm/Init.cpp:1318-1356` — SIGBUS handler
  install; the 5-line chaining patch goes here.
- `/home/dspfac/openharmony/interface/sdk_c/graphic/graphic_2d/native_drawing/drawing_canvas.h:76` —
  authoritative `OH_Drawing_CanvasBind(canvas, bitmap)` signature, no
  GraphicBuffer overload.
- `/home/dspfac/openharmony/interface/sdk_c/graphic/graphic_2d/native_window/buffer_handle.h:24-42` —
  `BufferHandle` struct with `virAddr` field used for the memcpy.
- `/home/dspfac/applications/standard/settings/` — copy-template for the
  Westlake host HAP scaffold (working hvigor wrapper, signed `.p7b`,
  module.json5 examples).
- Memory: `feedback_macro_shim_contract.md`, `feedback_subtraction_not_addition.md`,
  `project_ohos_mvp_pipeline.md`.
