# M5-Step1 Report — Audio daemon project skeleton

**Date:** 2026-05-13
**Owner:** Builder (this CR)
**Milestone:** M5-Step1 of M5 westlake-audio-daemon (~3-4 steps total)
**Status:** done (build clean + on-phone smoke PASS, `media.audio_flinger` listed by `IServiceManager::listServices()` from a fresh peer)

---

## 1. Goal

Stand up the directory skeleton for `aosp-audio-daemon-port/native/`, mirroring the M6
sibling (`aosp-surface-daemon-port/`) and the proven daemon shape from
`aosp-libbinder-port/test/sm_registrar.cc`. Build a binary that registers itself
with our M2 servicemanager under the canonical AOSP name `"media.audio_flinger"`,
constructs an empty `BBinder` subclass (`WestlakeAudioFlinger`), and parks in
`joinThreadPool`. No real `IAudioFlinger` AIDL logic — Step 2 will add the
dispatch table; Step 3 will hook the AAudio backend that CR34's spike already
validated.

## 2. Files created

| Path | LOC | Purpose |
|---|---|---|
| `aosp-audio-daemon-port/README.md` | ~80 | Directory overview + build/smoke recipe |
| `aosp-audio-daemon-port/Makefile` | 105 | bionic-arm64 cross-compile rules; reuses sibling `aosp-libbinder-port` libbinder.so |
| `aosp-audio-daemon-port/build.sh` | 22 | Entry-point: idempotent `make all`, prints binary size |
| `aosp-audio-daemon-port/native/audiopolicy_main.cpp` | 96 | `main()`: open /dev/vndbinder, startThreadPool, addService("media.audio_flinger", new WestlakeAudioFlinger), joinThreadPool |
| `aosp-audio-daemon-port/native/WestlakeAudioFlinger.h` | 50 | BBinder skeleton declaration; AOSP interface descriptor `"android.media.IAudioFlinger"` |
| `aosp-audio-daemon-port/native/WestlakeAudioFlinger.cpp` | 56 | Step 1 onTransact: log + ack with NO_ERROR (Step 2 will dispatch by AIDL code) |
| `aosp-audio-daemon-port/out/bionic/audio_flinger` | (37 808 bytes, stripped) | Built daemon binary, ARM64 ELF, dynamically linked against libbinder.so |
| `docs/engine/M5_STEP1_REPORT.md` | this file | Step 1 outcome |
| `docs/engine/PHASE_1_STATUS.md` | +1 row | M5-Step1 status row |

Total LOC of new daemon-owned native code: **102** (audiopolicy_main.cpp 96 + WestlakeAudioFlinger.cpp 56 + WestlakeAudioFlinger.h 50 = 202 raw, ~102 net of header/comment lines).

### 2.1 Directory layout (as of this CR)

```
aosp-audio-daemon-port/
├── README.md
├── Makefile
├── build.sh
├── native/
│   ├── audiopolicy_main.cpp           (NEW this CR)
│   ├── WestlakeAudioFlinger.h         (NEW this CR)
│   └── WestlakeAudioFlinger.cpp       (NEW this CR)
├── out/bionic/
│   ├── audio_flinger                  (NEW: 37 808 bytes stripped)
│   ├── audio_flinger.unstripped       (NEW: 53 KB)
│   └── obj/
│       ├── audiopolicy_main.o
│       └── WestlakeAudioFlinger.o
└── spike/                             (already merged via CR34)
    ├── spike.c
    ├── spike
    ├── spike-run.log
    └── build.sh
```

## 3. Build verification

```bash
$ cd /home/dspfac/android-to-openharmony-migration/aosp-audio-daemon-port
$ bash build.sh
CXX(bionic) native/audiopolicy_main.cpp
CXX(bionic) native/WestlakeAudioFlinger.cpp
════ Linking westlake-audio-daemon (audio_flinger) ════
... (link command) ...
Sizes:
-rwxr-xr-x 1 dspfac dspfac 37K May 13 16:25 .../out/bionic/audio_flinger
-rwxr-xr-x 1 dspfac dspfac 53K May 13 16:25 .../out/bionic/audio_flinger.unstripped

════ Build complete ════
-rwxr-xr-x 1 dspfac dspfac 37K May 13 16:25 .../out/bionic/audio_flinger

[build.sh] Built: out/bionic/audio_flinger (37808 bytes)

$ file out/bionic/audio_flinger
ELF 64-bit LSB pie executable, ARM aarch64, version 1 (SYSV), dynamically linked,
interpreter /system/bin/linker64, stripped
```

Build was clean (only two `-Wunused-command-line-argument` warnings for `-fuse-ld=lld`
during compile phase — exactly the same warnings the sibling M6 build emits, harmless).
Two source files compile and link successfully against the prebuilt
`aosp-libbinder-port/out/bionic/libbinder.so` + the three static archives
(libutils, libcutils, libbase).

## 4. On-phone smoke test

Phone: OnePlus 6 `cfb7c9e3` (Android 15 LineageOS 22, kernel 4.9.337).

### 4.1 Procedure

1. `setprop ctl.stop vndservicemanager` + poll-for-death (up to 10 s) + SIGKILL
   fallback (mirrors `aosp-libbinder-port/lib-boot.sh`'s
   `stop_vndservicemanager_synchronously` from CR7).
2. Start our `westlake/bin-bionic/servicemanager` on `/dev/vndbinder` as uid=1000
   (via `setsid -- su 1000 -c "exec ..."`).
3. Start our `westlake/bin-bionic/audio_flinger` on `/dev/vndbinder` as uid=1000.
4. Run `sm_smoke` with `BINDER_DEVICE=/dev/vndbinder SM_TEST_NAME=m5step1.smoke`:
   it forks, child does `addService("m5step1.smoke")`, parent does
   `defaultServiceManager()` + `listServices()` + `checkService("m5step1.smoke")`.
5. Verify `media.audio_flinger` is present in the listServices output.
6. Cleanup: kill audio_flinger + SM by PID, restart stock vndservicemanager.

Script materialized as `/data/local/tmp/m5step1_smoke.sh` on the phone to avoid the
`pkill -f westlake/bin-bionic/...` self-kill trap (the outer shell's own argv would
otherwise match the pattern when the kill is invoked from inside it). PID-based
cleanup used instead.

### 4.2 Result

**PASS.**

`m5step1-af.log` (audio_flinger stderr):
```
[wlk-audio-daemon pid=540] starting; binder=/dev/vndbinder; will register as "media.audio_flinger"
[sm-stub] WaitForProperty(servicemanager.ready=true) -> immediate true
[wlk-audio-daemon] defaultServiceManager() OK
[wlk-af] WestlakeAudioFlinger constructed
[wlk-audio-daemon] addService("media.audio_flinger") OK; entering joinThreadPool
```

`m5step1-smoke.log` (sm_smoke output, fresh peer process):
```
[sm_smoke/parent] listServices() returned 3 names:
    - m5step1.smoke
    - manager
    - media.audio_flinger          ← our daemon's registration appears here
[sm_smoke/parent] PASS: all checks ok.
```

The smoke-runner's own verification line:
```
[smoke] VERIFY PASS: media.audio_flinger is in SM listServices
```

What this proves:
1. The daemon links cleanly against the bionic-built `libbinder.so` (no missing
   symbols, no `dlopen` failures at runtime — process reached `main`).
2. `ProcessState::initWithDriver("/dev/vndbinder")` succeeds — our libbinder
   negotiates with the binder driver and gets its half of `BC_REGISTER_LOOPER` /
   `BR_NOOP` sequence right.
3. `defaultServiceManager()` returns non-null — our SM (uid=system on
   /dev/vndbinder) responds to the implicit "ask for handle 0" lookup.
4. `addService("media.audio_flinger", ...)` round-trips successfully through
   the kernel binder driver — the AIDL marshaling for `IServiceManager::addService`
   is symmetric between our daemon's libbinder and our SM's libbinder (this was
   never in doubt; both link the same source — just nice to confirm).
5. `WestlakeAudioFlinger` constructor ran (so the `BBinder` vtable is well-formed
   and the `kIfaceDescriptor` String16 was constructed correctly).
6. **A fresh, unrelated process (`sm_smoke`)** can list services through our SM
   and **see `media.audio_flinger`** — this is the load-bearing assertion for
   "the daemon will be discoverable by `ServiceManager.getService("media.audio_flinger")`
   from inside framework.jar later in Step 3 / Step 4."

### 4.3 Cleanup hygiene

Per anti-drift: smoke-runner script lives at `/data/local/tmp/m5step1_smoke.sh` only
(NOT in the repo; treated as ephemeral). Stock vndservicemanager restarted at end of
each run. State drift across runs from earlier iterations cleaned up by killing
leftover stale westlake SM PIDs (one race surfaced during development: `setsid`
backgrounded a SM that survived a `pkill -f westlake/bin-bionic` because the pkill
target string also matched the outer shell's argv → exit 137 on the outer shell →
backgrounded SM kept running; fixed by switching to PID-based cleanup).

## 5. Anti-drift compliance audit

Per the CR self-audit gate:

```bash
$ git status   # (NB: not a git repo at root — manual file-level audit below)
```

Files touched:
- `aosp-audio-daemon-port/README.md`           NEW
- `aosp-audio-daemon-port/Makefile`            NEW
- `aosp-audio-daemon-port/build.sh`            NEW
- `aosp-audio-daemon-port/native/audiopolicy_main.cpp`     NEW
- `aosp-audio-daemon-port/native/WestlakeAudioFlinger.h`   NEW
- `aosp-audio-daemon-port/native/WestlakeAudioFlinger.cpp` NEW
- `aosp-audio-daemon-port/out/bionic/audio_flinger`        BUILT
- `docs/engine/M5_STEP1_REPORT.md`             NEW (this file)
- `docs/engine/PHASE_1_STATUS.md`              +1 row

Files NOT touched (per task contract):
- `shim/java/**` (CR32 stable / CR36 in flight)
- `art-latest/**`
- `aosp-libbinder-port/**` (linked-against, not edited)
- `aosp-surface-daemon-port/**` (M6-Step1 sibling, separate agent)
- `aosp-shim.dex`
- `scripts/**`
- `memory/**`

Per-app branches: **zero** — daemon is architectural. Identical surface for every
Android app that talks to `"media.audio_flinger"`.

New `Unsafe` / `setAccessible` usage: **n/a** (this is native C++ code).

## 6. Next-step blockers and recommendations

### 6.1 Blockers — none for Step 2

Step 2 (real `IAudioFlinger` AIDL dispatch) is unblocked. The daemon binary
already round-trips Binder transactions to the SM and parks in `joinThreadPool`;
all that changes in Step 2 is the body of `WestlakeAudioFlinger::onTransact()`,
which currently logs+acks every code.

### 6.2 Recommended pre-Step-2 research CR

The M5 plan §2 derives Android-11 transaction codes from
`frameworks/av/media/libaudioclient/IAudioFlinger.cpp:34-95` (the `enum`). But
the actual peer is **framework.jar** (Android-16 era), and Android-11 vs.
Android-16 may have re-numbered transactions if `IAudioFlinger` was migrated
from header-defined to AIDL-generated.

Recommended: spin a small **CR (research-only, parallel to Step 2 build)** to
diff the Android-11 enum against:
- Android-16 `IAudioFlinger.aidl` (if it exists at
  `/home/dspfac/aosp-android-16/frameworks/av/media/libaudioclient/aidl/` —
  needs verification),
- The on-device `framework.jar`'s reflected `BnAudioFlinger.TRANSACTION_*`
  constants (mirroring the M4-services discovery pattern in
  `docs/engine/M4_DISCOVERY.md` §7).

If the numbers match, Step 2 uses the Android-11 enum directly. If they differ,
Step 2 uses the framework.jar-reflected values. This research CR is ~1-2 hours
and avoids a guess in Step 2.

The M5 plan §2.1 already calls this out:

> **Note:** Android 11 uses the *header-defined* IAudioFlinger pattern (no `.aidl`
> file — the Bp/Bn classes are hand-written in `IAudioFlinger.cpp`). Android 16
> may have migrated to AIDL-generated stubs; the Builder agent should verify
> by checking `frameworks/av/media/libaudioclient/aidl/` against the
> framework-jar version we ship.

This is the **only** decision point ahead of Step 2; not a blocker for Step 1.

### 6.3 Step 2 scope preview (for the next CR)

Step 2 should:
1. Settle the transaction-code source-of-truth question (see §6.2).
2. Add a `switch(code)` dispatch table to `WestlakeAudioFlinger::onTransact`.
3. Implement Tier-1 methods per M5 plan §2.2:
   - `OPEN_OUTPUT` (delegate to AAudio backend → returns synthetic
     `audio_io_handle_t`)
   - `CREATE_TRACK` (allocate cblk shmem, spawn per-track pump thread → write
     into AAudio stream)
   - `START`, `STOP`, `FLUSH`, `PAUSE` (route to backend.startStream etc.)
   - `REGISTER_CLIENT` (store IAudioFlingerClient ref; never call back unless death)
   - `SAMPLE_RATE`, `FORMAT`, `FRAME_COUNT`, `LATENCY` (return per-handle stored
     values from the open negotiation)
4. All other transaction codes → `failLoud("media.audio_flinger", "XXX", code)`
   per the CR2 pattern (already established by M4-services).

Estimated Step 2 effort: ~2-3 person-days (matches M5 plan §9.1 line-item:
"§2 Bn/Bp implementations + Tier-1 dispatch = 2-3 days").

### 6.4 Step 3+ scope preview (FYI, NOT part of Step 2)

- Step 3: hook AAudio backend (`AudioBackend.h` + `AudioBackend_aaudio.cpp` per
  M5 plan §4.2, ~150 LOC of NDK AAudio wrapper). The CR34 spike already proved
  the API path works; this just productionizes it.
- Step 4: cblk shmem ring-buffer (M5 plan §5) — pump-thread reading from
  AudioTrack's shared `audio_track_cblk_t` and writing into AAudio.
- Step 5: integration test (`audio_binder_smoke` per M5 plan §7) and
  noice-discover regression integration.

## 7. Person-time spent on this CR

**~80 minutes** (well inside the 3-4-hour budget):

| Activity | Time |
|---|---|
| Read M5 plan, M6-Step1 sibling source (the canonical reference shape), CR34 spike, AOSP IAudioFlinger.cpp for the interface descriptor | 15 min |
| Write `audiopolicy_main.cpp`, `WestlakeAudioFlinger.{h,cpp}`, `Makefile`, `build.sh`, `README.md` | 25 min |
| Build (`bash build.sh`) — succeeded first try | 1 min |
| On-phone smoke iterations (vndsm-EBUSY race debugging, `pkill -f` self-kill trap, final clean PASS via script-file approach mirroring M6-Step1) | 30 min |
| Write this report + PHASE_1_STATUS row | 10 min |

## 8. Summary line for PHASE_1_STATUS.md

> M5-Step1 — audio_flinger daemon project skeleton | done (build clean + on-phone
> smoke PASS, media.audio_flinger registered with our M2 SM and listed by
> sm_smoke listServices from a fresh peer process) | 2026-05-13 | Builder | Mirrored
> M6-Step1 / sm_registrar pattern: 102-LOC daemon (audiopolicy_main.cpp + WestlakeAudioFlinger.{h,cpp})
> + 105-LOC Makefile + 80-LOC README, 37 808-byte ARM64 binary linked against
> aosp-libbinder-port/out/bionic/libbinder.so; on-phone PASS via /data/local/tmp/m5step1_smoke.sh
> mirroring CR33/M6-Step1 recipe. Next: Step 2 (real IAudioFlinger AIDL dispatch
> table + AAudio backend hookup), preceded by optional ~1-2 hr CR to settle
> Android-11-enum vs. Android-16-AIDL transaction-code source-of-truth.
