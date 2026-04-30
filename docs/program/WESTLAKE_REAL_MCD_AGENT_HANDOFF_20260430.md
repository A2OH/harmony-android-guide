# Westlake Real McD Agent Handoff - 2026-04-30

This handoff is for a new agent continuing the Westlake contract work. Treat it
as the current supervisor baseline, but still verify with commands and phone
proofs before claiming progress.

## Mission

Deliver Westlake as a complete self-contained Android app runtime that can be
ported to OHOS. The real McDonald's APK is the proving app.

Do not optimize for a McDonald's-only demo. Use McDonald's to expose missing
generic Android runtime, framework, appcompat/material, native-loader, storage,
network, rendering, input, and OHOS southbound contracts.

The user cares about:

- real stock APK execution, not a mock app;
- Westlake guest execution, not phone ART app execution;
- durable source/build changes, not one-off edited binaries;
- phone-proven evidence with hashes, logs, screenshots, and clear gaps;
- continuing supervision with minimal stops.

## Current Architecture Rule

The host Android APK may run on the phone's normal ART only as a shell for
Activity, Surface, input, and subprocess ownership.

The guest APK must run in Westlake's own `dalvikvm` subprocess.

Do not accept a proof if guest app logic is executed through:

- phone `app_process64`;
- phone `dalvikvm64`;
- host `DexClassLoader` as the real guest executor;
- normal installed McDonald's app Activity execution outside Westlake.

Current launch path:

- host: `com.westlake.host/.WestlakeActivity`
- launch extra: `WESTLAKE_ART_MCD`
- guest APK staged at: `/data/local/tmp/westlake/com_mcdonalds_app.apk`
- guest runtime staged under: `/data/local/tmp/westlake/`
- rendering path: guest writes DLST frames through stdout; host replays on
  `DisplayListFrameView`

## Repositories

Runtime/build repo:

- `/home/dspfac/art-latest`
- remote: `https://github.com/A2OH/art-latest.git`
- branch: `main`
- latest pushed commit: `defde26 Stabilize ARM64 runtime patch build`

Migration/host/shim/docs/artifacts repo:

- `/home/dspfac/android-to-openharmony-migration`
- remote: `https://github.com/A2OH/westlake.git`
- branch: `main`
- latest pushed commit before this handoff: `6ad7baee Record source-built real McD runtime proof`

Local tracker outside git:

- `/home/dspfac/openharmony/WESTLAKE-PLATFORM-FIRST-OPEN-ISSUES.md`
- This directory is not a git repo in this workspace. Repo-backed status is in
  `android-to-openharmony-migration/docs/`.

## Phone And ADB

Connected phone:

- serial: `cfb7c9e3`
- model observed earlier: `ONEPLUS A6003`
- ADB binary: `/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe`
- ADB server: default localhost port `5037`

Use:

```bash
ADB=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe
SERIAL=cfb7c9e3
"$ADB" -s "$SERIAL" devices
```

The environment has repeatedly warned about too many open unified exec
processes. Avoid long-lived shells. If a command returns a session id, drain it
with `write_stdin` until it exits. Do not leave builds, pushes, or grep jobs
open.

## Latest Accepted Proof

Latest accepted real-McD proof:

- artifact dir:
  `/home/dspfac/android-to-openharmony-migration/artifacts/real-mcd/20260430_092445_portable_tz_ohos_symbol_gate/`
- latest pointer:
  `/home/dspfac/android-to-openharmony-migration/artifacts/real-mcd/latest_patchsystem_a15_arm64.txt`
- host launch result: `Status: ok`, cold start, `TotalTime: 1057`
- screenshot: valid `1080x2280` PNG
- focused proof grep contains only:
  `Strict dashboard frame reason=dashboard-renderLoop root=android.widget.LinearLayout bytes=776 views=29 texts=14 buttons=6 images=1`

Accepted hashes:

- `dalvikvm=b337d96877ef42732f7db50ad9d98751f6d65c84f2ce6c88ae94d2a5af014869`
- `aosp-shim.dex=2baa2ab7149285f283e2537d7c2dd939f1c30cb2ecd949e6fef34b5a6ecbb6cd`
- `westlake-host.apk=d9e505b30962bf7b837a544aa8d5826a2af37ec0ef1431d976b2f5d0fc13a213`
- phone `core-oj.jar=e19236b056ec6257c751d070f758e682dc1c62ba0cb042fde93d3eec09d647c2`
- phone `core-libart.jar=017aae7e18a501836b11bbb663bd3d4d26f2686820ea3309197998b1c09b35d7`
- phone `core-icu4j.jar=25015e123850920b90c8f9b0f9a75204781f44d71d5f66643a977e4d07f66f8c`

Artifact hashes:

- `logcat_tail.log=d372be078d3bde7d5e0553d24cdefe828035f73f2c291f6856e7d35121a0a6fe`
- `screen.png=b43af4421a83177b465b7b5f78a03f24548cf4fed1fea0593d6bd360cba9c9c2`

Focused gate is clean for:

- `pending UOE`
- `ThreadGroup.uncaughtException`
- VarHandle diagnostic marker
- `DoCall-TRACE`
- `NoClassDefFoundError`
- `UnsatisfiedLinkError`
- JNI fatal marker
- `Fatal signal`
- `SIGBUS`

Important boundary: this is runtime/build/dashboard-survival proof. It is not
production McDonald's UI compatibility.

## What Was Just Fixed

The previous deploy candidate failed the runtime symbol gate because strong
symbols were unresolved. This has been closed.

In `/home/dspfac/art-latest`:

- `patches/runtime/runtime.cc` now routes `TimeZone.getDefault()` through a
  portable resolver using `WESTLAKE_TIMEZONE_ID`/`TZ` and libc offset data,
  instead of the old hard-coded/noisy `PFCUT-TZ` UTC object path.
- `patches/runtime/entrypoints/quick/quick_trampoline_entrypoints.cc` is now
  repo-backed and compiled by all current Makefiles so the quick timezone
  bridge is durable for bionic and OHOS builds. Timezone diagnostics are gated
  behind `WESTLAKE_TRACE_TZ`; the latest phone proof has `tz markers: 0`.
- `Makefile.ohos-arm64` now links `framework_native_stubs.o`, and
  `stubs/thread_cpu_stub.cc` covers the OHOS libc++ `std::__h` mangled
  file-descriptor helper signatures plus the static `_DYNAMIC` placeholder.
  Full OHOS `link-runtime` now builds `dalvikvm=c5bd8135af2cfd86d052b96d4438a565bc73f80625fd10e25f3305540dc491de`
  and passes `scripts/check-westlake-runtime-symbols.sh`.
- `patches/runtime/var_handles.cc` now carries the zero-mask
  `FieldVarHandle`/`StaticFieldVarHandle` fallback fix.
- `Makefile`, `Makefile.bionic-arm64`, and `Makefile.ohos-arm64` exclude the
  upstream `runtime/var_handles.cc` and compile the patched copy.
- `Makefile.bionic-arm64` and `Makefile.ohos-arm64` no longer exclude real A15
  `runtime/arch/arm64/thread_arm64.cc`.
- Same for real A15 `runtime/arch/arm64/entrypoints_init_arm64.cc`.
- A15 `quick_entrypoints_arm64.S` is used again, but with A15 include
  precedence so it does not accidentally include A11 `arch/quick_alloc_entrypoints.S`.
- A11 `jni_entrypoints_arm64.S` remains isolated because A15 JNI assembly still
  has portable-toolchain CFI issues.
- `thread_cpu_stub.cc` is now built as its own support object. It is no longer
  accidentally produced from `metrics_stubs.cc`.
- `thread_cpu_stub.cc` supplies support stubs for WebP, Android cmsg send/recv,
  log error write, `GetShorty`, `create_disassembler`, and related static
  build gaps.

Verification already run:

- clean bionic build: `runtime 230 / 230`
- bionic runtime symbol gate: passed
- `nm -u` on deployed runtime shows only weak loader/HWASAN hooks
- phone sync hashes match
- real-McD phone proof captured
- OHOS sanity slice:
  `make -f Makefile.ohos-arm64 asm metrics-stubs -j4`
  compiles A15 quick entrypoints and `thread_cpu_stub.cc`

OHOS full link/run is not proven yet.

## Rebuild And Deploy Commands

Bionic runtime rebuild:

```bash
cd /home/dspfac/art-latest
make -f Makefile.bionic-arm64 clean
make -f Makefile.bionic-arm64 link-runtime -j4
/home/dspfac/android-to-openharmony-migration/scripts/check-westlake-runtime-symbols.sh \
  /home/dspfac/art-latest/build-bionic-arm64/bin/dalvikvm
```

Deploy to phone:

```bash
ROOT=/home/dspfac/android-to-openharmony-migration
cp /home/dspfac/art-latest/build-bionic-arm64/bin/dalvikvm \
  "$ROOT/ohos-deploy/arm64-a15/dalvikvm"
cd "$ROOT"
./scripts/sync-westlake-phone-runtime.sh
```

Run real McD proof:

```bash
ADB=/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe
SERIAL=cfb7c9e3
ROOT=/home/dspfac/android-to-openharmony-migration
OUT="$ROOT/artifacts/real-mcd/$(date +%Y%m%d_%H%M%S)_next_real_mcd"
mkdir -p "$OUT"

"$ADB" -s "$SERIAL" install -r \
  "$ROOT/westlake-host-gradle/app/build/outputs/apk/debug/app-debug.apk" \
  > "$OUT/install.txt"
sha256sum \
  "$ROOT/westlake-host-gradle/app/build/outputs/apk/debug/app-debug.apk" \
  "$ROOT/aosp-shim.dex" \
  "$ROOT/ohos-deploy/arm64-a15/dalvikvm" \
  > "$OUT/hashes.txt"
"$ADB" -s "$SERIAL" shell sha256sum \
  /data/local/tmp/westlake/dalvikvm \
  /data/local/tmp/westlake/aosp-shim.dex \
  /data/local/tmp/westlake/core-oj.jar \
  /data/local/tmp/westlake/core-libart.jar \
  /data/local/tmp/westlake/core-icu4j.jar \
  > "$OUT/phone_hashes.txt"
"$ADB" -s "$SERIAL" logcat -c
"$ADB" -s "$SERIAL" shell am start -S -W \
  -n com.westlake.host/.WestlakeActivity \
  --es launch WESTLAKE_ART_MCD \
  > "$OUT/am_start.txt"
sleep 30
"$ADB" -s "$SERIAL" logcat -d > "$OUT/logcat_tail.log"
"$ADB" -s "$SERIAL" exec-out screencap -p > "$OUT/screen.png"
grep -E "PFCUT-VARHANDLE|PFCUT-UOE-THROW|Strict dashboard frame|ThreadGroup\\.uncaughtException|pending UOE|UnsupportedOperationException|NoClassDefFoundError|Config failed|DoCall-TRACE|SIGBUS|Fatal signal|ExceptionInInitializerError|UnsatisfiedLinkError|JNI DETECTED ERROR|FATAL EXCEPTION" \
  "$OUT/logcat_tail.log" > "$OUT/proof_grep.txt" || true
sha256sum "$OUT/logcat_tail.log" "$OUT/screen.png" > "$OUT/artifact_hashes.txt"
```

## Current Gap Estimate

Rough supervisor estimate:

- Westlake Android-side runtime baseline: `60-65%`
- real McDonald's visible production UI: `30-40%`
- OHOS portability: `35-45%`
- complete stock APK runtime: still `50%+` remaining

This is not because the architecture is wrong. The latest proof strengthens
the architecture. The remaining work is the real Android platform surface that
stock apps expect.

## Top Workstreams

### 1. PF-494: Native Library Loading And Realm

Current state:

- split APK metadata and native library staging have been proven earlier;
- Realm/ReLinker can find staged native payloads;
- static `dalvikvm` cannot truly load real APK native libraries depending on
  Android linker/system libraries;
- the current Realm behavior is a boundary probe, not real persistence.

Do not claim stock native compatibility until real `.so` loading, lifetime,
JNI registration, and dependencies are coherent under the portable contract.

Acceptable directions:

- real OHOS-portable native-loader abstraction;
- deliberately scoped Java Realm/storage compatibility layer;
- staged intermediate proof that is clearly labeled as boundary exploration.

Unacceptable:

- silent success from `Runtime.nativeLoad` for arbitrary native APK libraries;
- McDonald's-only native shortcuts without a generic contract.

### 2. PF-499: Remove Or Productize PFCUT Paths

The focused fatal/UOE gate is clean. The latest proof also productizes and
quarantines the former timezone PFCUT path. Broader logs still contain known
cutouts:

- ICU `ULocale`;
- currency;
- atomics/Unsafe forced paths;
- proxy repair;
- McD logging/perf no-ops.

Convert these to generic portable implementations or documented bridge
contracts. Then remove noisy diagnostics from accepted proof windows.

### 3. Production Config/Data Bootstrap

Do not treat dashboard Activity survival as production app init.

Need proof that McDonald's config/data/bootstrap path succeeds or is replaced
by a clearly generic app/network/storage contract. The latest focused proof did
not show the old `Config failed to download` marker, but no successful
production config/data path has been proven.

### 4. Real Dashboard UI

Current visible dashboard is still fallback/scaffolded Westlake content.

Need real app-owned dashboard Fragment/View/Material/AppCompat/data-binding
content. This means replacing direct fallback frame generation with generic
inflated View rendering, invalidation, layout, scrolling, adapter/list,
material styles, and input dispatch.

### 5. OHOS Full Runtime Proof

Current OHOS status:

- A15 quick entrypoint assembly slice compiles;
- `thread_cpu_stub.cc` support object compiles;
- full OHOS static runtime link and symbol gate now pass;
- OHOS hosted runtime execution is not accepted yet.

Next OHOS gate should be:

- run under an OHOS Ability/XComponent host or nearest available OHOS runner;
- prove same guest-facing contracts used by Android phone proof.

## Do Not Waste Time On

- Mock McD/Yelp polish as proof of real McD progress. Those are useful canaries
  only.
- Phone ART execution of the McDonald's APK.
- Landing-page UI work.
- Claiming Material Components compatibility from a small shim slice.
- Treating fallback dashboard frames as real McDonald's UI.
- Reverting broad dirty worktree changes. There are many unrelated generated
  files and previous edits in both repos.
- Leaving command sessions open. Process pressure is already high.

## Current Docs To Read First

Repo-backed current docs:

- `docs/engine/REAL-APK-STATUS.md`
- `docs/engine/WESTLAKE-STATUS.md`
- `docs/program/WESTLAKE_PLATFORM_FIRST_CONTRACT.md`
- `docs/program/WESTLAKE_PLATFORM_FIRST_ISSUES.md`

Do not trust old sections without checking dates and artifacts. The current
accepted frontier is the `20260430_011506_clean_patchsystem_a15_arm64` proof.

## Evidence Acceptance Standard

For any next claim, require:

- local runtime hash;
- phone runtime hash;
- host APK hash;
- `aosp-shim.dex` hash;
- boot classpath jar hashes if touched;
- symbol gate result for runtime;
- `am start` output;
- logcat;
- screenshot;
- focused grep;
- clear statement of what is proven and what is not.

If a proof still uses a fallback or cutout, label it as such.

## Optional Claude Usage

The user said Claude Code may be used via:

```bash
claude --dangerously-skip-permissions
```

Use it only as a sidecar reviewer or pair-programming helper, and close the
process. Previous process pressure was high, so do not spawn Claude while a
build, push, or phone proof session is still open.
