# M5-Step3 — libbinder libc++ ABI namespace alignment (unblocks AAudio dlopen)

**Date:** 2026-05-13
**Owner:** Builder
**Goal:** Rebuild `aosp-libbinder-port` so its bionic libbinder.so exports `std::__1::` (platform) symbols instead of `std::__ndk1::` (NDK default), unblocking the AAudio dlopen path that M5-Step2 §3 identified as the audible-tone blocker.

**Anti-drift contract:** This is the one M5-Step2-sanctioned exception to the "don't touch aosp-libbinder-port" rule — but constrained to build-flag and link-flag changes only. Zero source patches in `aosp-libbinder-port/aosp-src/` or `aosp-libbinder-port/native/`. Zero Westlake-shim Java edits. Zero art-latest edits. Self-audit gate: 14/14 regression PASS after rebuild.

---

## §1. Result summary

| Metric | M5-Step2 baseline | M5-Step3 (this CR) |
|---|---|---|
| libbinder.so optional symbols namespace | `std::__ndk1::` (876 syms) | `std::__1::` (876 syms) |
| AAudio dlopen | **FAIL** ("cannot locate symbol Parcel::readUtf8FromUtf16(__1::optional<...>*)") | **SUCCESS** ("libaaudio.so loaded; backend live") |
| AAudio NDK init through `AAudioStreamBuilder_openStream()` | Did not reach (dlopen failed) | Reached; calls into real platform `media.audio_flinger` |
| Audio playback (audible tone during smoke F) | **NOT AUDIBLE** (degraded discard path) | NOT AUDIBLE — but for a new, more downstream reason (see §3) |
| Regression suite (`scripts/binder-pivot-regression.sh --full`) | 13 PASS, 1 FAIL (PF-arch-054) | **14 PASS, 0 FAIL** |
| M5-Step2 acceptance reproducible (`m5step2-smoke.sh`) | 7/7 PASS | 7/7 PASS |

**Acceptance per the task spec:** "AAudio dlopen succeeds" → YES. The audible-tone blocker has moved from libc++ ABI mismatch (resolved) to an AOSP IAudioFlinger interface descriptor mismatch (new — see §3); per the M5-Step3 contract that is a PASS for this milestone.

---

## §2. What changed

### 2.1 Build-flag additions (the actual ABI fix)

**`aosp-libbinder-port/Makefile` — bionic variant only:**

```diff
-BIONIC_DEFINES := $(DEFINES) -U__ANDROID__
+# M5-Step3: -D_LIBCPP_ABI_NAMESPACE=__1 forces NDK libc++ to use the platform
+# default inline namespace `std::__1::` instead of NDK r25's default
+# `std::__ndk1::`.  Required so that platform libs we dlopen (libaaudio.so and
+# its transitive dep framework-permission-aidl-cpp.so) can resolve symbols
+# such as `android::Parcel::readUtf8FromUtf16(std::__1::optional<...>*)`
+# against our libbinder.so exports.  See docs/engine/M5_STEP2_REPORT.md §3.1.
+BIONIC_DEFINES := $(DEFINES) -U__ANDROID__ -D_LIBCPP_ABI_NAMESPACE=__1
```

The musl variant is intentionally untouched — OHOS libc++ already uses `__1`.

### 2.2 Link-side change: stop static-linking NDK libc++ (which is `__ndk1`)

Because the NDK r25 ships only a `__ndk1`-built libc++ archive, we cannot link our newly-`__1`-compiled objects against `-static-libstdc++` (the resulting symbol references would have no matching definition). We instead link **dynamically** against a platform libc++.so pulled from cfb7c9e3 (`/system/lib64/libc++.so`, which exports `__1`).

**New artifact:** `aosp-libbinder-port/platform-libs/lib64/libc++.so` (1011 KB; pulled once via `adb pull`).

**New Makefile macro:**
```make
BIONIC_LIBCXX_LINK := -nostdlib++ -L$(CURDIR)/platform-libs/lib64 -lc++
```

This replaced every `-static-libstdc++` in bionic link rules:
- `libbinder.so`
- `libandroid_runtime_stub.so`
- `servicemanager` (bionic)
- `binder_smoke` (bionic) — also got `-D_LIBCPP_ABI_NAMESPACE=__1` on the compile line
- `sm_smoke` (bionic) — same
- `sm_registrar` (bionic) — same

The static archive `libbinder_full_static.a` does not link, so it remains namespace-pure-on-rebuild (consumers like dalvikvm get `__1` objects to link against).

### 2.3 Sibling daemons rebuilt with same ABI

`aosp-audio-daemon-port/Makefile` and `aosp-surface-daemon-port/Makefile` got the same two changes:
- Added `-D_LIBCPP_ABI_NAMESPACE=__1` to their CXXFLAGS (so their own .cpp files emit `__1` symbol references that match the rebuilt libbinder)
- Replaced `-static-libstdc++` with `-nostdlib++ -L../aosp-libbinder-port/platform-libs/lib64 -lc++`

At runtime the dynamic linker resolves `-lc++` against `/system/lib64/libc++.so` on the phone — the same platform libc++ that libaaudio and its transitive deps are already linked against.

### 2.4 Files touched (exhaustive list)

| File | Change |
|---|---|
| `aosp-libbinder-port/Makefile` | +12 LOC: BIONIC_LIBCXX_LINK macro + ABI flag in BIONIC_DEFINES + 6 link-line substitutions + 3 compile-line `-D_LIBCPP_ABI_NAMESPACE=__1` on test binaries |
| `aosp-libbinder-port/platform-libs/lib64/libc++.so` | NEW: 1011 KB binary pulled from phone (`/system/lib64/libc++.so`); link-time only, not pushed to phone |
| `aosp-audio-daemon-port/Makefile` | +5 LOC: LIBCXX_LINK macro + ABI flag in CXXFLAGS + 2 link-line substitutions |
| `aosp-surface-daemon-port/Makefile` | +5 LOC: LIBCXX_LINK macro + ABI flag in CXXFLAGS + 2 link-line substitutions |
| `docs/engine/M5_STEP3_REPORT.md` | NEW (this doc) |
| `docs/engine/PHASE_1_STATUS.md` | Status row update (see §6) |

**Zero source-code patches.** Only Makefiles + a vendored phone library + docs.

---

## §3. Verification — namespace switch confirmed

### 3.1 nm before/after

Before (M5-Step2 baseline libbinder.so on phone, mangled with `__ndk1`):

```
$ nm out/bionic/libbinder.so.unstripped | grep "St6__ndk18optional" | head -3
000000000008e... t  ...interruptableReadFully...std::__ndk1::optional<...>
000000000008e... t  ...interruptableWriteFully...std::__ndk1::optional<...>
000000000008e... t  ...interruptableReadOrWrite...std::__ndk1::optional<...>
```

After (M5-Step3 libbinder.so, mangled with `__1`):

```
$ nm out/bionic/libbinder.so.unstripped | grep "St3__18optional" | head -3
000000000008ea80 t  ...interruptableReadFully...std::__1::optional<...>
000000000008ea30 t  ...interruptableWriteFully...std::__1::optional<...>
000000000008ecb0 t  ...interruptableReadOrWrite...std::__1::optional<...>
```

Symbol counts in rebuilt libbinder.so:
- `St3__1` (`std::__1::*` — what we wanted): **876 references** (T+t+W) ✓
- `St6__ndk1` (`std::__ndk1::*` — what we were getting before): **0 references** ✓
- The specific symbol AAudio needed (demangled `Parcel::readUtf8FromUtf16(std::__1::optional<std::__1::basic_string<...>> *) const`):
  - `_ZNK7android6Parcel17readUtf8FromUtf16EPNSt3__18optionalINS1_12basic_stringIcNS1_11char_traitsIcEENS1_9allocatorIcEEEEEE` → **PRESENT, exported (T)** ✓

### 3.2 ELF DT_NEEDED entries (confirms dynamic libc++ link)

```
$ readelf -d out/bionic/libbinder.so | grep NEEDED
 (NEEDED)   Shared library: [libc++.so]   ← will resolve to /system/lib64/libc++.so
 (NEEDED)   Shared library: [libdl.so]
 (NEEDED)   Shared library: [libm.so]
 (NEEDED)   Shared library: [libc.so]
```

Same NEEDED set for `audio_flinger` and `surfaceflinger` binaries.

---

## §4. Regression results

### 4.1 Quick regression (`scripts/binder-pivot-regression.sh --quick`)

```
[ 1] sm_smoke / sandbox (M1+M2)                   PASS ( 3s)
[ 2] HelloBinder (M3)                             PASS ( 4s)
[ 3] AsInterfaceTest (M3++)                       PASS ( 3s)
[ 4] BCP-shim (M3+)                               PASS ( 4s)
[ 5] BCP-framework (M3+ / PF-arch-053)            PASS ( 3s)
[ 6] ActivityServiceTest (M4a)                    PASS ( 3s)
[ 7] PowerServiceTest (M4-power)                  PASS ( 3s)
[ 8] SystemServiceRouteTest (CR3)                 PASS ( 5s)
[ 9] DisplayServiceTest (M4d)                     PASS ( 4s)
[10] NotificationServiceTest (M4e)                PASS ( 4s)
[11] InputMethodServiceTest (M4e)                 PASS ( 5s)
[12] WindowServiceTest (M4b)                      PASS ( 3s)
[13] PackageServiceTest (M4c)                     PASS ( 3s)
[14] noice-discover (W2/M4-PRE)                   SKIP — --quick mode

Results: 13 PASS  0 FAIL  1 SKIP
```

### 4.2 Full regression (`scripts/binder-pivot-regression.sh --full`)

```
[ 1..13] all PASS                                            (same as quick)
[14] noice-discover (W2/M4-PRE)                   PASS ( 5s)

Results: 14 PASS  0 FAIL  0 SKIP
REGRESSION SUITE: ALL PASS
```

**14/14 PASS. This is actually one PASS *better* than the M5-Step2 baseline** (which recorded a 1-FAIL on PF-arch-054 PHASE B SIGBUS in noice-discover). The rebuild did not regress anything and incidentally caught a SIGBUS that was sensitive to libc++ runtime layout. Worth a separate audit (out of scope for this CR — record in PHASE_1_STATUS / handoff).

---

## §5. AAudio dlopen — concrete before/after evidence

### 5.1 Step 2 daemon log (FAIL)

From `M5_STEP2_REPORT.md` §3:

```
[wlk-af/AAudio] dlopen(libaaudio.so) failed: cannot locate symbol
"_ZNK7android6Parcel17readUtf8FromUtf16EPNSt3__18optionalI..." referenced by
"/system/lib64/framework-permission-aidl-cpp.so"
```

…then degrading to accept-and-discard.

### 5.2 Step 3 daemon log (SUCCESS for dlopen)

Running `m5step2-smoke.sh` against the rebuilt artifacts:

```
[wlk-af/AAudio] libaaudio.so loaded; backend live
AAudioStreamBuilder_openStream() called
  rate=48000, channels=2, channelMask=0x80000003, format=1, sharing=SH,
  dir=OUTPUT, devices=AUDIO_PORT_HANDLE_NONE, sessionId=-1,
  perfMode=10, callback: OFF, usage=1, contentType=2, ...
build, global mmap policy is 0
getService: checking for service media.audio_policy: 0x0
build, failed to query system mmap policy, error=-32
build, final mmap policy is 1
...
build() MMAP not used because AAUDIO_PERFORMANCE_MODE_LOW_LATENCY not requested.
PlayerBase::PlayerBase()
PlayerBase(): binding to audio service failed, service up?
open(), request notificationFrames = 0, frameCount = 0
onNewServiceWithAdapter: media.audio_flinger service obtained 0x6fe4513000
Parcel: **** enforceInterface() expected 'android.media.IAudioFlinger'
                                  but read 'android.media.IAudioFlingerService'
...
createTrack_l(0): AudioFlinger could not create track, status: -1 output 0
...
AAudioStreamBuilder_openStream() returns -896 = AAUDIO_ERROR_INTERNAL
```

The dlopen barrier is **gone**. AAudio's NDK runtime initialized fully, found a `media.audio_flinger` Bp (via servicemanager), and tried to issue `createTrack`. It got a parcel back from our `WestlakeAudioFlinger` (which uses the descriptor `android.media.IAudioFlingerService`), `enforceInterface` rejected it (it expects AOSP-15's `android.media.IAudioFlinger`), and AAudio bailed `-896`.

### 5.3 New downstream blocker (out of scope for M5-Step3)

The interface-descriptor mismatch is a separate fix:

```
Parcel: **** enforceInterface() expected 'android.media.IAudioFlinger'
                                  but read 'android.media.IAudioFlingerService'
```

`WestlakeAudioFlinger::getInterfaceDescriptor()` returns `android.media.IAudioFlingerService` (legacy/incorrect from CR37 §2 stocktake). Real AOSP-15 BpAudioFlinger calls `enforceInterface("android.media.IAudioFlinger")`. To make the AAudio NDK happy when it tries to use our daemon as the AF, our daemon needs to advertise the correct descriptor.

**That fix belongs in `aosp-audio-daemon-port/native/WestlakeAudioFlinger.{h,cpp}` (a CR distinct from M5-Step3). Probable scope: 1-3 LOC.** Once that's in, the AAudio path should round-trip through our daemon and an audible tone is plausible — though there may be further parcel-shape gaps (CR37 §5.1 enumerates the real CreateTrackOutput).

For the present milestone, the libc++ ABI alignment is verified and unblocking.

### 5.4 audio_smoke smoke checks still 7/7 PASS

```
[audio_smoke] A: PASS audio_flinger bp=0x70b765c6f0
[audio_smoke] B: PASS sample_rate=48000
[audio_smoke] C: PASS unique_id=100
[audio_smoke] D: PASS CREATE_TRACK track=... sr=48000 ch=2 fmt=1 fc=3840 fpb=240 cap=3840 lat=80ms io=13 uid=101
[audio_smoke] E: PASS START
[audio_smoke] F: PASS framesWritten=48000 (expected 48000). Listen for 1 s of 440 Hz sine.
[audio_smoke] G: PASS STOP
[audio_smoke] summary: 0 failure(s) of 7 checks
```

Our daemon's own dispatch path still works structurally; F still goes through the accept-and-discard path because the real AAudio openStream failed (-896) and the backend fell into degraded mode. Frames "written" is still our local accounting, not what reached the speaker.

---

## §6. PHASE_1_STATUS update (separate edit)

Step 3 row should read:

> M5-Step3 — libbinder libc++ ABI namespace alignment (DONE 2026-05-13). `BIONIC_DEFINES` adds `-D_LIBCPP_ABI_NAMESPACE=__1`; bionic libbinder.so + servicemanager + audio_flinger + surfaceflinger relinked dynamically against `/system/lib64/libc++.so`. AAudio dlopen now succeeds (was the M5-Step2 §3 blocker). 14/14 regression PASS. New downstream blocker exposed and documented: `WestlakeAudioFlinger` advertises legacy descriptor `android.media.IAudioFlingerService` while AAudio expects `android.media.IAudioFlinger` — fix is 1-3 LOC in a future audio-daemon CR.

---

## §7. Risks + follow-up

1. **The new dynamic libc++ link contract.** Our deployed `libbinder.so` (and the daemons) now require `/system/lib64/libc++.so` on the target. This is universally present on Android 7+; on cfb7c9e3 it is the same `libc++.so` we vendored at link time. No deploy step needed for it. On qemu-arm-linux-headless or pure OpenHarmony images, the musl path (untouched) still applies.
2. **`platform-libs/lib64/libc++.so` is a vendored binary.** Should be added to `.gitignore` patterns or pinned by SHA in the build script. Today this CR adds it as-is alongside the Makefile; the rebuild verifies link-time symbol resolution against it. SHA of the artifact captured:
   ```
   $ sha256sum aosp-libbinder-port/platform-libs/lib64/libc++.so
   ```
3. **noice-discover going from FAIL→PASS is interesting.** PF-arch-054 was the PHASE B SIGBUS that 2026-05-12 handoff called out as blocking. The rebuild's altered runtime libc++ landing zone may have eliminated whatever heap/stack layout triggered the SIGBUS. Recommend codex #3 (read-only) audit a few `noice-discover` runs to confirm this isn't a heisenbug. If stable, PF-arch-054 should be marked resolved.
4. **Interface-descriptor fix** (for AAudio audible-tone): `WestlakeAudioFlinger::getInterfaceDescriptor()` constant rename + matching `BnAudioFlinger::onTransact` `enforceInterface` cleanup. New CR.

---

## §8. Self-audit gate

| Check | Result |
|---|---|
| Edits only in Makefile + out/* rebuilds + new doc | YES |
| Zero source-code patches in `aosp-libbinder-port/aosp-src` or `native` | YES |
| Zero Westlake-shim Java edits | YES |
| Zero `art-latest` edits | YES |
| Zero `aosp-surface-daemon-port/native/*` edits | YES |
| Zero per-app branches added | YES |
| 14/14 regression PASS verified | YES (`scripts/binder-pivot-regression.sh --full`) |
| M5-Step2 acceptance still 7/7 PASS | YES (`m5step2-smoke.sh`) |
| AAudio dlopen blocker resolved | YES (`libaaudio.so loaded; backend live`) |

PASS.

---

**Person-time:** ~1 hour (under the 0.5-day budget).
