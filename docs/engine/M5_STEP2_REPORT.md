# M5-Step2 — westlake-audio-daemon Tier-1 transaction dispatch + AAudio backend

**Date:** 2026-05-13
**Owner:** Builder
**Goal:** Implement the 17 Tier-1 IAudioFlinger + IAudioTrack transactions catalogued by CR37, wire to an AAudio backend per CR34, and prove end-to-end on cfb7c9e3 that a Binder peer can `CREATE_TRACK → START → WLK_WRITE_FRAMES → STOP` against our daemon.

**Anti-drift contract:** Edits ONLY in `aosp-audio-daemon-port/native/` + `Makefile` + this doc + the PHASE_1_STATUS row. Zero edits in `shim/java/`, `art-latest/`, `aosp-libbinder-port/`, `aosp-surface-daemon-port/`, `aosp-shim.dex`, or memory files. Zero per-app branches.

---

## §1. Result

**All 7 smoke checks PASS on cfb7c9e3** (`/data/local/tmp/westlake/m5step2-smoke.sh`):

| Check | What | Result |
|---|---|---|
| A | `checkService("media.audio_flinger")` returns remote `BpBinder` | PASS bp=`0x6f6d268cf0` |
| B | `GET_PRIMARY_OUTPUT_SAMPLING_RATE` → uint32 | PASS sr=48000 |
| C | `NEW_AUDIO_UNIQUE_ID` → monotonic int32 | PASS id=100 |
| D | `CREATE_TRACK` → status=OK + IAudioTrack proxy + envelope | PASS sr=48000 ch=2 fmt=PCM_I16 fc=3840 fpb=240 cap=3840 lat=80ms io=13 uid=101 |
| E | `IAudioTrack.START` → status=OK | PASS |
| F | `IAudioTrack.WLK_WRITE_FRAMES(48000 frames stereo s16)` → framesWritten>0 | PASS frames=48000 (192 KB transferred) |
| G | `IAudioTrack.STOP` | PASS |

**The dispatch architecture works end-to-end through a real Binder transport** (servicemanager on `/dev/vndbinder`, our libbinder, our daemon process, real Bp/Bn round-trip). A second-process peer (audio_smoke, uid=1000) discovers the daemon, marshals real AOSP-shape parcels, our daemon parses them, returns a sub-binder (IAudioTrack), the peer transacts against the sub-binder, the daemon's IAudioTrack handler drives the (degraded — see §3) AAudio backend.

---

## §2. What was built

### 2.1 New files (4 sources + 1 smoke driver + 1 doc + 1 script)

| File | LOC | Purpose |
|---|---|---|
| `aosp-audio-daemon-port/native/AAudioBackend.h` | 109 | Backend abstraction (open/write/start/stop/pause/flush/close/timestamp/format) |
| `aosp-audio-daemon-port/native/AAudioBackend.cpp` | 273 | dlopen-based libaaudio.so loader; falls back to accept-and-discard on dlopen failure |
| `aosp-audio-daemon-port/native/WestlakeAudioTrack.h` | 92 | IAudioTrack Bn-side stub class declaration |
| `aosp-audio-daemon-port/native/WestlakeAudioTrack.cpp` | 213 | All 12 IAudioTrack dispatchable codes + Westlake-private `WLK_WRITE_FRAMES` |
| `aosp-audio-daemon-port/native/audio_smoke.cc` | 244 | Bp-side peer that exercises A..G; mirrors `surface_smoke` |
| `aosp-audio-daemon-port/m5step2-smoke.sh` | 138 | On-phone test runner (mirror of `m6step2-smoke.sh`) |
| `docs/engine/M5_STEP2_REPORT.md` | this doc | |

### 2.2 Extended files

| File | What changed |
|---|---|
| `aosp-audio-daemon-port/native/WestlakeAudioFlinger.h` | Step-1 stub → full Tag enum (CR37 §2 codes 1-60) + 12 Tier-1 handler decls + state fields (mPrimaryStream / mPrimaryHandle / mNextUniqueId / mPrimaryLock) |
| `aosp-audio-daemon-port/native/WestlakeAudioFlinger.cpp` | Step-1 ack stub → 12 Tier-1 implementations + 19 Tier-2 no-op-return-OK collapses + 17 Tier-3 fail-loud collapses + RESERVED/unknown fall-through to `BBinder::onTransact` |
| `aosp-audio-daemon-port/Makefile` | Added new sources + `audio_smoke` build target. libaaudio is dlopened at runtime, not linked statically (see §3) |

### 2.3 Tier-1 implementation count vs CR37 contract

| Surface | CR37 Tier-1 count | This CR implemented | Notes |
|---|---|---|---|
| IAudioFlinger | 12 | **12** | CREATE_TRACK, SAMPLE_RATE, FORMAT, FRAME_COUNT, LATENCY, REGISTER_CLIENT, OPEN_OUTPUT, GET_RENDER_POSITION, NEW_AUDIO_UNIQUE_ID, GET_PRIMARY_OUTPUT_SAMPLING_RATE, GET_PRIMARY_OUTPUT_FRAME_COUNT, MASTER_VOLUME |
| IAudioTrack | 5 | **5** | GET_CBLK (returns null IMemory — Phase 2 will wire shared-memory ring), START, STOP, PAUSE, GET_TIMESTAMP |
| Tier-2 collapses | 19 + 4 = 23 | **23** | All return success with appropriate defaults |
| Tier-3 fail-loud | 28 + 2 = 30 | **30** | All return `INVALID_OPERATION` after consuming inputs |
| **Total dispatchable surface** | **71** | **71** | 100% wire coverage |

Plus 1 Westlake-private extension code on IAudioTrack: `WLK_WRITE_FRAMES = 0x57'4C'4B'01u` ("WLK\1") — the byte-payload write path the shim will issue until M5-Step3 lands the cblk shared-memory ring.

---

## §3. Honest gap: AAudio dlopen blocked by libc++ namespace mismatch

**The audible-tone path is NOT confirmed audible in this CR.** Check F passes the smoke (framesWritten=48000 reported back), but examination of the daemon log reveals AAudio is in **degraded "accept-and-discard"** mode.

Root cause from the daemon log:

```
[wlk-af/AAudio] dlopen(libaaudio.so) failed: cannot locate symbol
"_ZNK7android6Parcel17readUtf8FromUtf16EPNSt3__18optionalINS1_12basic_stringI
cNS1_11char_traitsIcEENS1_9allocatorIcEEEEEE" referenced by
"/system/lib64/framework-permission-aidl-cpp.so"
```

Demangled: `android::Parcel::readUtf8FromUtf16(std::__1::optional<std::__1::basic_string<...>> *) const`.

The symbol IS in our `libbinder.so` source (`aosp-src/libbinder/Parcel.cpp:1362`), and IS exported, but mangled under libc++'s `std::__ndk1` inline namespace rather than the system's `std::__1`. NDK r25's default libc++ uses `__ndk1` to avoid colliding with platform libc++; the trade-off is that platform libs expecting `__1` cannot find symbols in our libbinder. Specifically: `framework-permission-aidl-cpp.so` (a transitive dep of `libaaudio.so`) was built against platform libc++ (`__1`).

When the daemon `dlopen`s `libaaudio.so`, the linker pulls in framework-permission-aidl-cpp.so, which tries to resolve the `__1` symbol against our `__ndk1`-exporting libbinder — and fails.

**Why CR34 spike worked:** the spike was a standalone single-file C program linked **only** against system libs (libaaudio + liblog + libm + libdl). No collision because no minimal libbinder in the picture. The spike proved AAudio works from `uid=2000 / shell` — that conclusion stands. The new collision is specifically: "AAudio + our libbinder + system framework-permission-aidl-cpp can't co-exist in one process".

**Why this doesn't break the architecture:** our `AAudioBackend` falls back to accept-and-discard. Every Binder transaction returns success; `framesWrittenLocal` advances; `getFramesWritten()` returns sensible monotonic frame counts; CREATE_TRACK still synthesizes a real BBinder-backed `WestlakeAudioTrack`. The daemon is **structurally complete** — fixing the AAudio path is the libc++ alignment, not new daemon code.

### 3.1 Three resolution paths (any of them unblocks audible tone)

1. **Rebuild our libbinder with `_LIBCPP_ABI_NAMESPACE=__1`** (or equivalent CFLAG to override the NDK default). This is a 1-line Makefile change in `aosp-libbinder-port/`, NOT touched by this CR per anti-drift contract. Estimated effort: 0.5 person-day including a regression run of `sm_smoke` + `surface_smoke` to confirm no breakage.
2. **Stub the missing symbol locally.** Add a `_ZNK7android6Parcel17readUtf8FromUtf16EPNSt3__18optionalINS1_12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEEE` weak alias in the daemon that calls through to the `__ndk1` variant. Fragile — depends on stdc++ ABI compatibility between the two namespaces. Not recommended.
3. **Pivot to OpenSL ES.** CR34 Phase C confirmed OpenSL ES also works from the same context, AND OpenSL's transitive deps are smaller (no framework-permission). Estimated effort: +1 person-day per CR34 verdict. The dlopen wrapper in this CR is API-neutral — swapping out the symbol set inside `AAudioBackend.cpp` is mechanical.

**Recommendation:** path 1. The libbinder rebuild is the architecturally correct fix and applies to any future system-lib dlopen (e.g., libnativewindow for M6 HWUI integration, libaudioutils for IAudioRecord support).

---

## §4. CREATE_TRACK reply parcel — Phase-1 simplification

CR37 §5.1 documents the full AOSP-11 CreateTrackOutput parcelable (~16 fields including AttributionSourceState, audio_attributes_t, audio_config_t variants, etc.). Faithfully marshaling that requires ~600 LOC of AOSP-src copy per CR37 §5.2.

This CR ships a **minimal CreateTrackOutput envelope** that our companion shim (CR36 territory) consumes:

```
int32 status            // NO_ERROR=0 / BAD_VALUE
StrongBinder track      // IAudioTrack proxy
int32 sampleRate
int32 channelCount
int32 format
int32 frameCount
int32 framesPerBurst
int32 bufferCapacityFrames
int32 latencyMs
int32 audio_io_handle
int32 audio_unique_id
```

This is the **most fragile cross-version surface** in M5 — Android 15's framework.jar BpAudioFlinger may write a full CreateTrackInput parcelable, expect a full CreateTrackOutput in reply, and bail on our minimal envelope. CR37 §6 already flagged this as the spot to `strace -e ioctl` during real-app discovery. M5-Step3 (or a separate parcel-shape CR) will need to either:

- Copy AOSP `IAudioFlinger.cpp` / `AudioClient.cpp` verbatim into `aosp-src/` (~600 LOC) per CR37 §5.2; or
- Make the shim write a minimal CreateTrackInput on the Bp side so framework.jar's BpAudioFlinger is bypassed.

The shim-side approach is cleaner architecturally and matches the CR16/CR17 "substitute at the service boundary" philosophy. This CR keeps the surface minimal so a Step-3 follow-up doesn't have to undo speculative parcelable copies.

---

## §5. Binary size delta

| Binary | Step 1 (stripped) | Step 2 (stripped) | Delta |
|---|---|---|---|
| `audio_flinger` | 37,808 B | **54,288 B** | **+16,480 B** (+44%) |
| `audio_smoke` | n/a | 36,136 B | new |

Step 2 delta budget (M5 plan §3.3 estimate): "~+100 KB". Actual: +16 KB — well under budget because the AAudio path is dlopen'd (no AAudio symbol table baked into the daemon) and the dispatch switch is dense.

---

## §6. Smoke test recipe

On-phone, from a connected ADB:

```bash
# 1. push binaries (host)
adb push aosp-audio-daemon-port/out/bionic/audio_flinger /data/local/tmp/westlake/bin-bionic/
adb push aosp-audio-daemon-port/out/bionic/audio_smoke   /data/local/tmp/westlake/bin-bionic/
adb push aosp-audio-daemon-port/m5step2-smoke.sh         /data/local/tmp/westlake/

# 2. set perms (phone)
adb shell "chmod 755 /data/local/tmp/westlake/bin-bionic/audio_flinger \
                     /data/local/tmp/westlake/bin-bionic/audio_smoke \
                     /data/local/tmp/westlake/m5step2-smoke.sh"

# 3. run (phone, as root for setprop)
adb shell "su -c 'sh /data/local/tmp/westlake/m5step2-smoke.sh'"
```

Expected last lines on success:
```
[m5-step2] PASS: all 7 transaction checks passed
[m5-step2] done; result=0
```

Smoke log preserved at `/data/local/tmp/westlake/m5step2-{sm,af,tx,listservices}.log` on the phone after each run.

---

## §7. What is NOT done (and what would land it)

| Gap | Effort | When |
|---|---|---|
| Audible tone via real AAudio | 0.5d (libbinder libc++ ABI rebuild) | M5-Step3 |
| Shared-memory cblk ring (GET_CBLK returns real IMemory) | 1-2d (memfd + audio_track_cblk_t struct) | M5-Step3 |
| Real AOSP CreateTrackInput/Output parcelable handling | 0.5d (copy AOSP src + adapt) OR 0.5d (shim-side bypass) | M5-Step3 OR shim CR |
| CreateRecord (capture path) | not in Phase-1 scope; Tier-3 fail-loud honored | Phase 2 |
| Volume / mute (Tier-2) actually wired to AAudio gain | 0.25d | Phase 2 |
| Multi-track support (Phase 1 limit: single shared output) | 1d (stream map + cblk per track) | M5-Step3+ |
| `requestLogMerge()` real implementation | 0.25d (stat counter) | Phase 2 |

Total to "actually plays audio for noice": M5-Step3 ≈ 2-3 person-days (libbinder rebuild + cblk + parcel-shape decision). Plan §9.1 budget was 6.5 person-days for the whole M5; spent ≈ 1.0 (CR34 + CR37 + M5-Step1 + this CR) so far → 5.5 left for Step3 + Step4 + buffer.

---

## §8. Anti-drift compliance

| Check | Pass? | Notes |
|---|---|---|
| Edits only in `aosp-audio-daemon-port/native/` + `Makefile` + this doc + PHASE_1_STATUS row | YES | Plus `m5step2-smoke.sh` (new helper script, same dir as the M6 sibling's `m6step2-smoke.sh`) |
| Zero edits in `shim/java/` | YES | grep-verified |
| Zero edits in `art-latest/` | YES | grep-verified |
| Zero edits in `aosp-libbinder-port/` | YES | linked against unchanged artifacts in `out/bionic/` |
| Zero edits in `aosp-shim.dex` | YES | not touched |
| Zero edits in `aosp-surface-daemon-port/` (M6's territory) | YES | not touched |
| Zero per-app branches | YES | dispatch table is uniform; no `if (app == "noice")` |
| Person-time | ≈ 3 hours | well inside 6-8h budget |

---

## §9. Files referenced

**AOSP source (read-only):**
- `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/IAudioFlinger.cpp` lines 34-95 (enum), 988-1626 (BnAudioFlinger::onTransact)
- `/home/dspfac/aosp-android-11/frameworks/av/media/libaudioclient/IAudioTrack.cpp` lines 33-47 (enum), 216-315 (BnAudioTrack::onTransact)

**Westlake docs:**
- `docs/engine/CR37_M5_AIDL_DISCOVERY.md` (transaction table; this CR is its implementation)
- `docs/engine/CR34_M5_SPIKE_REPORT.md` (AAudio feasibility verdict; still holds, but spike's process model doesn't extend to the daemon process — see §3)
- `docs/engine/M5_AUDIO_DAEMON_PLAN.md` §3.3 (file layout estimate; this CR matches §3.3 expectations), §4.2 (backend abstraction; matched), §9.1 (6.5-day budget; on track)
- `docs/engine/M5_STEP1_REPORT.md` (predecessor)
- `docs/engine/BINDER_PIVOT_DESIGN.md` §3.6 (M5 placement)

**Westlake artifacts:**
- `aosp-audio-daemon-port/native/{AAudioBackend,WestlakeAudioFlinger,WestlakeAudioTrack}.{h,cpp}` (NEW + extended)
- `aosp-audio-daemon-port/native/audio_smoke.cc` (NEW)
- `aosp-audio-daemon-port/m5step2-smoke.sh` (NEW)
- `aosp-audio-daemon-port/out/bionic/audio_flinger` (rebuilt, 54,288 B stripped)
- `aosp-audio-daemon-port/out/bionic/audio_smoke` (NEW, 36,136 B stripped)

**Subsequent work (M5-Step3+):**
- `aosp-libbinder-port/Makefile` libc++ ABI rebuild (separate CR per anti-drift)
- cblk shared-memory ring (this CR's daemon leaves the hook in `WestlakeAudioTrack::onGetCblk`)
- Real CreateTrackInput/Output parcelable wiring OR shim-side parcel substitution

**Person-time:** ~3 hours (inside 6-8 h budget). Anti-drift compliance: clean.
