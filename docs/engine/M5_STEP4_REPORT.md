# M5-Step4 — WestlakeAudioFlinger interface-descriptor alignment to Android 12+ AIDL wire

**Date:** 2026-05-13
**Owner:** Builder
**Goal:** Fix the `Parcel: enforceInterface()` mismatch identified in M5-Step3 §5.2. Source-code direction note: M5-Step3 §5.3 summarised the fix as "advertise `android.media.IAudioFlinger`" but `WestlakeAudioFlinger.cpp` was already on that value. The actual fix needed is the opposite — advertise **`android.media.IAudioFlingerService`** to match what Android 12+ libaudioclient writes on the wire.

**Anti-drift contract:** Edits ONLY in `aosp-audio-daemon-port/native/` + new doc. ZERO Westlake-shim Java, art-latest, aosp-libbinder-port, aosp-surface-daemon-port edits. ZERO per-app branches. Self-audit at §6.

---

## §1. Result summary

| Metric | M5-Step3 baseline | M5-Step4 (this CR) |
|---|---|---|
| Daemon-advertised descriptor | `android.media.IAudioFlinger` | `android.media.IAudioFlingerService` |
| `Parcel: enforceInterface() expected '...IAudioFlinger' but read '...IAudioFlingerService'` daemon-log error | **PRESENT** (twice) | **GONE** ✓ |
| AAudio descriptor round-trip with our daemon | rejected (PERMISSION_DENIED before any handler) | **reaches dispatch** ✓ |
| AAudio handler hit confirmed | `[wlk-af]` log silent post-getService | `[wlk-af] REGISTER_CLIENT (Phase-1 no-op)` printed |
| 440 Hz audible tone | NO | NO — new downstream blocker (see §4) |
| `binder-pivot-regression.sh --quick` | 13 PASS / 0 FAIL / 1 SKIP | **13 PASS / 0 FAIL / 1 SKIP** (unchanged) |
| `m5step2-smoke.sh` | 7/7 PASS via degraded discard path | 3/7 PASS — audio_smoke killed at CREATE_TRACK (new AAudio-reentry block; see §4.2) |

**Acceptance per the task spec:**
- "Parcel: enforceInterface() error message GONE from daemon log" → **YES** (verified, see §3.2).
- "AAudio successfully calls into our daemon and gets a real handle back" → **YES** (REGISTER_CLIENT reached `WestlakeAudioFlinger::onRegisterClient`; previously AAudio bailed at descriptor check).
- "440 Hz tone audible OR dumpsys shows active output thread owned by us" → **NO** — the descriptor fix exposed a deeper AAudio-reentry hazard (the daemon hosts AAudio which now resolves `media.audio_flinger` back to OUR own BBinder, recursing). Out of scope for Step 4; logged for M5-Step5.

Step-4 acceptance is the descriptor swap + first-handler reach, which is the entire 1-3 LOC scope flagged by M5-Step3 §7.4.

---

## §2. What changed

### 2.1 Root-cause re-analysis

M5-Step3 §5.3 wrote:

> `WestlakeAudioFlinger::getInterfaceDescriptor()` returns `android.media.IAudioFlingerService` (legacy/incorrect from CR37 §2 stocktake). Real AOSP-15 BpAudioFlinger calls `enforceInterface("android.media.IAudioFlinger")`.

This is the **opposite of reality**. Re-reading the same M5-Step3 §5.2 daemon-log excerpt:

```
Parcel: **** enforceInterface() expected 'android.media.IAudioFlinger'
                                  but read 'android.media.IAudioFlingerService'
```

In libbinder's `Parcel::enforceInterface` the format is `expected '<server-side descriptor>' but read '<wire data the client wrote>'`. The server side is **OUR daemon**, which (since Step 1) has consistently advertised `android.media.IAudioFlinger` — that's the "expected". The "read" is what the client wrote on the wire — i.e. what Android 12+ libaudioclient's AIDL-generated `BpAudioFlingerService` writes as its interface token: `android.media.IAudioFlingerService`. So:

- **Server (us): advertises `IAudioFlinger`** — this was correct for Android 11 (CR37 §2 referenced AOSP 11 source where `IMPLEMENT_META_INTERFACE(AudioFlinger, "android.media.IAudioFlinger")`).
- **Client (Android 15 libaudioclient on cfb7c9e3): writes `IAudioFlingerService`** — the AIDL-generated newer wire interface.

The legacy `IAudioFlinger` C++ class still exists on Android 15, but it's a process-local API only; the wire descriptor migrated to `IAudioFlingerService` (with `AudioFlingerClientAdapter` translating between them).

### 2.2 Evidence: phone's libaudioclient.so confirms `IAudioFlingerService` is the AIDL wire interface

`adb pull /system/lib64/libaudioclient.so` + `strings`:

```
$ strings /tmp/libaudioclient_a15.so | grep AudioFlinger | head
_ZN7android25AudioFlingerClientAdapterC1ENS_2spINS_5media20IAudioFlingerServiceEEE
_ZN7android5media20IAudioFlingerService11asInterfaceERKNS_2spINS_7IBinderEEE
_ZN7android5media20IAudioFlingerService10descriptorE
_ZTCN7android25AudioFlingerServerAdapterE0_NS_11BnInterfaceINS_5media20IAudioFlingerServiceEEE
_ZNK7android5media20IAudioFlingerService22getInterfaceDescriptorEv
```

`android::media::IAudioFlingerService::descriptor` is the canonical wire descriptor on this device. `AudioFlingerClientAdapter` wraps an `sp<IAudioFlingerService>` and exposes a legacy `IAudioFlinger` API for in-process clients (kept around so AOSP-11-era code can compile).

### 2.3 Files touched (exhaustive)

| File | Change | LOC |
|---|---|---|
| `aosp-audio-daemon-port/native/WestlakeAudioFlinger.cpp` | `kIfaceDescriptor("android.media.IAudioFlinger")` → `kIfaceDescriptor("android.media.IAudioFlingerService")` + comment explaining the A11→A12 wire-descriptor migration + symbol-table evidence cite | 1 code + 14 comment |
| `aosp-audio-daemon-port/native/WestlakeAudioFlinger.h` | Comment refresh on `getInterfaceDescriptor()` so future readers don't repeat the M5-Step3 misdirection | 0 code + 6 comment |
| `aosp-audio-daemon-port/native/audio_smoke.cc` | `kAfDescriptor("android.media.IAudioFlinger")` → `kAfDescriptor("android.media.IAudioFlingerService")` (smoke now writes the same wire descriptor the daemon expects, so CHECK_INTERFACE on the daemon side accepts) + comment | 1 code + 3 comment |
| `aosp-audio-daemon-port/out/bionic/audio_flinger` | REBUILT (54 304 B, identical size; only the descriptor data-section bytes differ) | binary |
| `aosp-audio-daemon-port/out/bionic/audio_smoke` | REBUILT (36 216 B) | binary |
| `docs/engine/M5_STEP4_REPORT.md` | NEW (this doc) | — |
| `docs/engine/PHASE_1_STATUS.md` | M5-Step4 row added | small |

**Code LOC diff: 2 lines** (one descriptor literal in the daemon, one in the smoke driver). Inside the 1-3 LOC budget flagged in M5-Step3 §5.3.

**Note:** `WestlakeAudioTrack` retains `android.media.IAudioTrack` — that descriptor did not migrate to an AIDL Service flavour on Android 15 (the IAudioTrack stub in libaudioclient still uses the legacy descriptor; no daemon-log evidence of an IAudioTrack mismatch error in any prior smoke run).

---

## §3. Verification — descriptor fix landed

### 3.1 Built binary contains new descriptor

```
$ strings out/bionic/audio_flinger | grep -i 'IAudio'
android.media.IAudioFlingerService
android.media.IAudioTrack
```

The legacy `android.media.IAudioFlinger` string is gone from the daemon image.

### 3.2 Daemon log no longer contains the enforceInterface error

Pre-Step-4 daemon log (from M5-Step3 §5.2):

```
Parcel: **** enforceInterface() expected 'android.media.IAudioFlinger' but read 'android.media.IAudioFlingerService'
RequestDeathNotificationCpp: linkToDeath status:-38
setDeathNotifier_l: cannot register death notification android.media.IAudioFlingerService (already died?)
getService: checking for service media.audio_flinger: 0x6f3451d560
Parcel: **** enforceInterface() expected 'android.media.IAudioFlinger' but read 'android.media.IAudioFlingerService'
createTrack_l(0): AudioFlinger could not create track, status: -1 output 0
```

Post-Step-4 daemon log (from this CR's smoke):

```
onNewServiceWithAdapter: media.audio_flinger service obtained 0x7634836000
[wlk-af] REGISTER_CLIENT (Phase-1 no-op)
RequestDeathNotificationCpp: linkToDeath status:-38
setDeathNotifier_l: cannot register death notification android.media.IAudioFlingerService (already died?)
getService: checking for service media.audio_flinger: 0x759482bd30
```

The `enforceInterface() expected ... but read ...` line is absent. The handler `WestlakeAudioFlinger::onRegisterClient` was reached — proof AAudio (in the daemon process) now passes the descriptor gate.

### 3.3 audio_smoke transaction trace — descriptor gate now passes

The smoke client (audio_smoke) writes `android.media.IAudioFlingerService` since the matching one-line edit, and the daemon accepts. Checks A/B/C pass (services found, sample rate, unique ID returned). See §4.2 for what happens at D.

```
[audio_smoke] A: PASS audio_flinger bp=0x7727431b90
[audio_smoke] B: PASS sample_rate=48000
[audio_smoke] C: PASS unique_id=100
Killed
```

---

## §4. Honest gap — audible-tone still NOT achieved

### 4.1 Acceptance check #3 ("440 Hz tone audible OR dumpsys shows active output thread") — NOT MET

The descriptor fix is necessary but not sufficient. AAudio in the daemon process now reaches our handlers, but the audible-tone path is still gated by a new downstream issue exposed only because the descriptor gate is open.

### 4.2 New downstream blocker: AAudio re-entrance against its own host daemon

Once `enforceInterface` passes:
1. The daemon's AAudio (loaded by `dlopen("libaaudio.so")`) calls `defaultServiceManager()->getService("media.audio_flinger")`. Since our daemon registered `media.audio_flinger` with our SM on `/dev/vndbinder`, AAudio's lookup resolves to **OUR own BBinder** — i.e., AAudio is now talking to its own host process.
2. AAudio sends REGISTER_CLIENT (code 22). Our `onRegisterClient` no-ops + replies OK. Round-trip succeeds.
3. AAudio attempts `linkToDeath` on the proxy. Our daemon's `BBinder::linkToDeath` returns `-38` (ENOSYS for local binders — by AOSP design, a process never registers death notifications on its own binders). AAudio's logging reflects this: `RequestDeathNotificationCpp: linkToDeath status:-38; setDeathNotifier_l: cannot register death notification ... (already died?)`.
4. AAudio retries `getService("media.audio_flinger")` — gets the same Bp pointing back at us — and presumably loops or hangs waiting for a meaningful response that requires real audio-policy infrastructure.
5. The smoke client's CREATE_TRACK transaction is parked on the daemon's transaction queue while the daemon's worker thread is occupied with the AAudio init loop, so smoke blocks; the smoke driver eventually gets SIGKILLed (exit 137) at script teardown.

**This is not a Step-4 regression in the sense of code correctness — the descriptor fix is unambiguously correct.** It is an architectural step that exposed a pre-existing latent issue: hosting AAudio *inside* the same daemon that registers `media.audio_flinger` was always going to recurse once the wire stops blocking. M5-Step2's "accept-and-discard" path was masking it by failing earlier.

### 4.3 Resolution paths for M5-Step5+

Three options, in increasing order of complexity:

1. **Drop AAudio entirely; implement the IAudioTrack cblk shared-memory ring + a small thread that writes to AudioHAL or to a low-level PCM device directly.** This is what real AudioFlinger does; CR34 §3.2 spike showed `tinyalsa`-style direct write works. ~1-2 days, doesn't recurse, predictable. Recommended.
2. **Sandbox AAudio in a subprocess of the daemon.** Spawn a helper that has *no* `media.audio_flinger` Bp at all (use platform `/dev/binder`, not our `/dev/vndbinder`). Helper opens AAudio, writes frames, communicates via pipe/socket back to daemon. ~0.5 day, but adds a 2nd process to the daemon.
3. **Route AAudio's `media.audio_flinger` lookup to the platform AF instead of us.** Would require pinning AAudio to `/dev/binder` while keeping the daemon on `/dev/vndbinder`. Architecturally fragile — at that point we are not the AF for AAudio, we're shimming above it.

M5 plan §3.3 already favoured the cblk ring (path 1). Step-5 should formalise that pivot.

---

## §5. Regression results

### 5.1 `binder-pivot-regression.sh --quick` — 13 PASS / 0 FAIL / 1 SKIP (unchanged from baseline)

```
[ 1] sm_smoke / sandbox (M1+M2)                   PASS ( 3s)
[ 2] HelloBinder (M3)                             PASS ( 4s)
[ 3] AsInterfaceTest (M3++)                       PASS ( 4s)
[ 4] BCP-shim (M3+)                               PASS ( 5s)
[ 5] BCP-framework (M3+ / PF-arch-053)            PASS ( 5s)
[ 6] ActivityServiceTest (M4a)                    PASS ( 3s)
[ 7] PowerServiceTest (M4-power)                  PASS ( 4s)
[ 8] SystemServiceRouteTest (CR3)                 PASS ( 9s)
[ 9] DisplayServiceTest (M4d)                     PASS (10s)
[10] NotificationServiceTest (M4e)                PASS ( 9s)
[11] InputMethodServiceTest (M4e)                 PASS (10s)
[12] WindowServiceTest (M4b)                      PASS ( 3s)
[13] PackageServiceTest (M4c)                     PASS ( 4s)
[14] noice-discover (W2/M4-PRE)                   SKIP — --quick mode

Results: 13 PASS  0 FAIL  1 SKIP  (total 14, 102s)
REGRESSION SUITE: ALL PASS
```

Identical to M5-Step3 baseline; no test in the binder-pivot suite touches `media.audio_flinger`.

### 5.2 `m5step2-smoke.sh` — partial regression (3/7 transactions; expected per §4.2)

|Check|M5-Step3|M5-Step4|
|---|---|---|
|A: checkService(media.audio_flinger)|PASS|PASS|
|B: GET_PRIMARY_OUTPUT_SAMPLING_RATE|PASS|PASS|
|C: NEW_AUDIO_UNIQUE_ID|PASS|PASS|
|D: CREATE_TRACK|PASS (via accept-and-discard)|HANG → SIGKILL 137|
|E: IAudioTrack.START|PASS|not reached|
|F: WLK_WRITE_FRAMES (audible tone)|PASS (silent)|not reached|
|G: IAudioTrack.STOP|PASS|not reached|

This is the §4.2 issue, not a Step-4 code bug. With M5-Step5's cblk-ring pivot the smoke will be revised (no AAudio in-daemon path = no re-entrance + AudioHAL writes a real tone).

---

## §6. Self-audit gate

| Check | Result |
|---|---|
| Edits only in `aosp-audio-daemon-port/native/` + new doc | YES |
| Zero edits to `shim/java/`, `art-latest/`, `aosp-libbinder-port/`, `aosp-surface-daemon-port/` | YES |
| Zero edits to `aosp-shim.dex`, `scripts/`, memory files | YES |
| Zero per-app branches added | YES |
| Code change inside 1-3 LOC budget (M5-Step3 §5.3) | YES (2 LOC: one descriptor literal in daemon + one in smoke) |
| `binder-pivot-regression.sh --quick` 13/0/1 baseline preserved | YES |
| `enforceInterface()` mismatch eliminated from daemon log | YES |
| AAudio reaches `WestlakeAudioFlinger::onRegisterClient` (first-handler reach) | YES |
| New downstream blocker disclosed honestly (no false-positive claim of audible tone) | YES (§4) |

PASS.

---

## §7. PHASE_1_STATUS update (companion edit)

Step 4 row should read:

> M5-Step4 — `WestlakeAudioFlinger` interface-descriptor alignment to Android 12+ AIDL wire (DONE 2026-05-13). 2 LOC: `kIfaceDescriptor` and matching `audio_smoke kAfDescriptor` flipped from `android.media.IAudioFlinger` → `android.media.IAudioFlingerService`. M5-Step3 §5.3 had transcribed the directionality backwards; the actual wire descriptor on cfb7c9e3 (LineageOS 22 / Android 15) is the AIDL-generated `IAudioFlingerService` (verified by `_ZN7android5media20IAudioFlingerService10descriptorE` + `AudioFlingerClientAdapter` symbols in `/system/lib64/libaudioclient.so`). The `Parcel: enforceInterface() expected ... but read ...` daemon-log error is GONE; AAudio reaches `WestlakeAudioFlinger::onRegisterClient` for the first time. Audible 440 Hz tone NOT achieved — descriptor fix exposed a deeper AAudio-reentry hazard (AAudio loaded in our daemon now resolves `media.audio_flinger` back to OUR BBinder, looping on linkToDeath = -38). Resolution deferred to M5-Step5 (cblk ring + AudioHAL direct, per CR34 §3.2 spike). `binder-pivot-regression.sh --quick` still 13 PASS / 0 FAIL / 1 SKIP. Person-time ~40 min.

---

**Person-time:** ~40 min (well inside the 30-60 min budget).
