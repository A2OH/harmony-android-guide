# aosp-audio-daemon-port

Westlake's substitute for AOSP `audioserver` — registers as the canonical
service `media.audio_flinger` with our own M2 servicemanager.  Sibling to
`aosp-surface-daemon-port/` (M6 surface daemon) and `aosp-libbinder-port/`
(M1-M3 binder substrate).

## Status

- **Step 1 (this CR — skeleton):** daemon registers with SM, `joinThreadPool`,
  stub `onTransact` that logs + acks every transaction.
- **Step 2 (next):** real `IAudioFlinger` AIDL dispatch + AAudio-backed
  output stream (per `docs/engine/M5_AUDIO_DAEMON_PLAN.md` §4.2).
- **Step 3+:** capture stub, AudioPolicy optional Tier-2, integration with
  noice-discover regression.

## Authoritative references

| Doc | Purpose |
|---|---|
| `docs/engine/M5_AUDIO_DAEMON_PLAN.md` | Full scoping (619 LOC, CR21 deliverable). Read first. |
| `docs/engine/CR34_M5_SPIKE_REPORT.md` | AAudio-backend feasibility spike (PASS 2026-05-13). |
| `docs/engine/M5_STEP1_REPORT.md` | This CR's outcome (this file's deliverable). |
| `aosp-libbinder-port/test/sm_registrar.cc` | Reference daemon shape (open binder, register, joinThreadPool). |
| `aosp-surface-daemon-port/native/surfaceflinger_main.cpp` | Sibling Step 1 daemon (M6); same pattern. |

## Directory layout

```
aosp-audio-daemon-port/
├── README.md                          (this file)
├── Makefile                           bionic-arm64 cross-compile rules
├── build.sh                           one-shot entry-point: invokes make
├── native/                            daemon sources (NEW in Step 1)
│   ├── audiopolicy_main.cpp           main(), addService, joinThreadPool
│   ├── WestlakeAudioFlinger.h         BBinder skeleton declaration
│   └── WestlakeAudioFlinger.cpp       BBinder skeleton implementation
├── out/bionic/                        build artifacts (generated)
│   └── audio_flinger                  the daemon binary
└── spike/                             CR34 feasibility spike (already merged)
    ├── spike.c                        3-phase AAudio + OpenSL ES probe
    ├── spike                          built binary (NDK r25 bionic-arm64)
    └── spike-run.log                  on-phone run transcript (PASS)
```

## Build

```bash
# Prerequisite: aosp-libbinder-port must be built first (bionic variant).
cd ../aosp-libbinder-port && bash build.sh all-bionic

# Then build this daemon.
cd ../aosp-audio-daemon-port && bash build.sh
```

Output: `out/bionic/audio_flinger` — ARM64 ELF, linked against the sibling
`libbinder.so` + `libutils_static.a` + `libcutils_static.a` + `libbase_static.a`.

## Smoke test (on-phone)

```bash
ADB="/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3"
$ADB push out/bionic/audio_flinger /data/local/tmp/westlake/bin-bionic/

# Coordinate with M6 surface-daemon smoke if it's also using SM:
$ADB shell "su -c 'pgrep -af westlake/bin-bionic/servicemanager'"

# (See docs/engine/M5_STEP1_REPORT.md §3 for the full smoke-test recipe.)
```

## Anti-drift compliance

- Zero edits outside `aosp-audio-daemon-port/` and `docs/engine/`.
- Reuses `aosp-libbinder-port/out/bionic/libbinder.so` by linking — does NOT
  modify the sibling's source.
- No per-app branches — daemon is architectural, identical surface for every
  Android app that talks to `"media.audio_flinger"`.
- No new use of `Unsafe` / `setAccessible` (native code; not applicable).
