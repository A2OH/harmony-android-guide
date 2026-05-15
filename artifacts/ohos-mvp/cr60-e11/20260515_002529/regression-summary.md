# CR60 E11 regression summary (2026-05-15 00:25 PT)

**Subject:** flip `--arch arm32` default static → dynamic-PIE in
`scripts/run-ohos-test.sh`.

## Driver change

`resolve_arch()` updated. `--arch arm32` (and `--arch auto` on the rk3568
board, which auto-detects 32-bit userspace) now resolves to:
- board path: `/data/local/tmp/dalvikvm-arm32-dyn`
- host path:  `dalvik-port/build-ohos-arm32-dynamic/dalvikvm`

New `--arch arm32-static` explicit value keeps the legacy static binary
addressable (board `/data/local/tmp/dalvikvm-arm32`, host
`dalvik-port/build-ohos-arm32/dalvikvm`).

`cmd_hello` hardened: dalvik-cache wipe added (`HelloOhos*` +
`bcp@*` patterns) so back-to-back `--arch` switches don't trip
`Fatal error: java/lang/Object` dexopt-cache contamination — same wipe
pattern E9a applied to `cmd_hello_dlopen_real`. `cmd_trivial_activity`
already had the wipe.

## Regression matrix (all PASS on board dd011a414436314130101250040eac00)

| --arch          | subcommand           | result | evidence                                                                            |
|-----------------|----------------------|--------|-------------------------------------------------------------------------------------|
| arm32 (default) | hello                | PASS   | `artifacts/ohos-mvp/mvp0-hello/20260515_001755/`                                    |
| arm32           | trivial-activity     | PASS   | `artifacts/ohos-mvp/mvp1-trivial/20260515_001822/`                                  |
| arm32           | xcomponent-test      | PASS tier=3 | `artifacts/ohos-mvp/cr60-followup-xcomp-call/20260515_001846/`                  |
| arm32           | hello-dlopen-real    | PASS 6/6 | `artifacts/ohos-mvp/cr60-followup-e9/20260515_001904/`                            |
| arm32           | hello-drm-inprocess  | PASS 2/2 (panel red 10s) | `artifacts/ohos-mvp/cr60-followup-e9/20260515_001929-drm-inprocess/` |
| arm32-static    | hello                | PASS   | `artifacts/ohos-mvp/mvp0-hello/20260515_002035/`                                    |
| aarch64         | hello                | PASS   | `artifacts/ohos-mvp/mvp0-hello/20260515_002322/`                                    |
| aarch64         | trivial-activity     | PASS   | `artifacts/ohos-mvp/mvp1-trivial/20260515_002443/`                                  |

## Markers captured

- `westlake-dalvik on OHOS — main reached` (hello, all 3 arches)
- `OhosTrivialActivity.onCreate reached pid=14486` (arm32 dynamic)
- `OhosTrivialActivity.onCreate reached pid=14833` (aarch64)
- `xcomp-test-done highest=3` (arm32 dynamic, Tier 3 buffer map/fill/unmap)
- `hello-dlopen-real-done passed=6 failed=0` (arm32 dynamic)
- `hello-drm-inprocess nativePresent rc=0 reason=OK crtc=92 fb=161 conn=159 mode=720x1280 hold=10s` (arm32 dynamic)

## Composer_host status

- pre `hello-drm-inprocess`: pid=13326
- post `hello-drm-inprocess`: pid=14598 (auto-respawned by hdf_devmgr,
  same pattern as E9b PASS)

## Self-audit (E11)

- [x] no Unsafe.allocateInstance in any new Java code (no Java added)
- [x] no setAccessible in any new Java code (no Java added)
- [x] no per-app branches (`resolve_arch` is generic; no app-specific paths)
- [x] no new native code (only bash + markdown changed)
- [x] all three Makefile targets still build: `ohos-aarch64`, `ohos-arm32`, `ohos-arm32-dynamic`
- [x] regression matrix above covers all 5 brief-required subcommands + 2 aarch64

## Files changed

- M scripts/run-ohos-test.sh         (~50 lines: resolve_arch + arm32-static
                                       case + help text + cmd_hello cache wipe)
- M docs/engine/OHOS_MVP_WORKSTREAMS.md  (E11 row added)
- + artifacts/ohos-mvp/cr60-e11/20260515_002529/regression-summary.md  (this file)
