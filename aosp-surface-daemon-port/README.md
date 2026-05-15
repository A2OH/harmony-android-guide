# aosp-surface-daemon-port

Westlake-owned `westlake-surface-daemon` (registered as `"SurfaceFlinger"`
with our M2 servicemanager).  Substitutes the AOSP SurfaceFlinger Binder
contract at the libgui ↔ daemon boundary — see
[`docs/engine/M6_SURFACE_DAEMON_PLAN.md`](../docs/engine/M6_SURFACE_DAEMON_PLAN.md)
for the full scoping doc.

## Status

| Stage     | Status | Notes |
|-----------|--------|-------|
| Spike CR33 | done   | memfd-backed GraphicBuffer substitute viable on kernel 4.9.337; see [`spike/`](spike/) and [`docs/engine/CR33_M6_SPIKE_REPORT.md`](../docs/engine/CR33_M6_SPIKE_REPORT.md) |
| Step 1    | done   | Project skeleton + daemon process pattern proven; daemon registers as `"SurfaceFlinger"` on `/dev/vndbinder` + blocks on `joinThreadPool`.  No real `ISurfaceComposer` dispatch yet — Step 2 adds that. |
| Step 2    | TODO   | Wire ISurfaceComposer AIDL transaction-code dispatch using CR35's discovery output |
| Step 3+   | TODO   | See plan §7 (BufferQueueCore, GraphicBuffer-memfd, IGraphicBufferProducer, dispatcher to Compose host pipe) |

## Layout

```
aosp-surface-daemon-port/
├── README.md          — this file
├── BUILD_PLAN.md      — copy of plan structure mirroring aosp-libbinder-port/
├── Makefile           — bionic-arm64 cross-compile
├── build.sh           — entry-point wrapper around make
├── m6step1-smoke.sh   — on-phone smoke test (Step 1)
├── m6step1-smoke-run.log — captured smoke transcript (cfb7c9e3, 2026-05-13)
├── native/            — daemon source
│   ├── surfaceflinger_main.cpp        — main() w/ joinThreadPool
│   ├── WestlakeSurfaceComposer.h      — BBinder subclass declaration
│   └── WestlakeSurfaceComposer.cpp    — Step 1 stub (acks all transactions)
├── out/bionic/        — build artifacts
│   └── surfaceflinger                 — 37 KB ARM64 bionic binary
└── spike/             — CR33 spike artifacts (memfd / AHB / cross-process)
    ├── spike.cpp, spike, build.sh     — 4-phase memfd/AHB probe (PASS)
    └── spike_hwui_flags.cpp, spike_hwui_flags — usage-flag matrix probe (PASS)
```

## Quick build

```bash
cd /home/dspfac/android-to-openharmony-migration/aosp-surface-daemon-port
bash build.sh
# produces out/bionic/surfaceflinger
```

The build reuses `aosp-libbinder-port/out/bionic/libbinder.so` and the three
sibling static archives (`libutils_static.a`, `libcutils_static.a`,
`libbase_static.a`) — no libbinder rebuild is needed.  If those artifacts
are missing, `build.sh` invokes `aosp-libbinder-port/build.sh bionic` first.

## Quick smoke (Step 1)

```bash
ADB="/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3"
$ADB push out/bionic/surfaceflinger /data/local/tmp/westlake/bin-bionic/
$ADB push m6step1-smoke.sh /data/local/tmp/westlake/
$ADB shell "su -c 'sh /data/local/tmp/westlake/m6step1-smoke.sh'"
# expect: [m6-step1] PASS: SurfaceFlinger appears in listServices output
```

Smoke test runs entirely on-device (mirrors `m3-dalvikvm-boot.sh` pattern):
1. Stop device's `vndservicemanager`.
2. Spawn our `servicemanager` on `/dev/vndbinder`.
3. Spawn our `surfaceflinger` daemon → `addService("SurfaceFlinger", ...)`.
4. Spawn `sm_smoke` to listServices + checkService round-trip.
5. Tear down and restart device's `vndservicemanager`.

## Anti-drift / architectural rules

- **No per-app branches.**  Daemon is generic; same surface for every
  Android app that talks to "SurfaceFlinger".  Same rule as for the M4
  services (`shim/java/com/westlake/services/*`).
- **No edits outside this directory** (Step 1 contract; see
  [`docs/engine/M6_STEP1_REPORT.md`](../docs/engine/M6_STEP1_REPORT.md)).
- Reuses `aosp-libbinder-port/out/bionic/libbinder.so` unchanged — that
  substrate is stable.

## Related docs

- [`docs/engine/M6_SURFACE_DAEMON_PLAN.md`](../docs/engine/M6_SURFACE_DAEMON_PLAN.md) — full scoping doc, all ~52 ISurfaceComposer methods classified by tier
- [`docs/engine/M6_STEP1_REPORT.md`](../docs/engine/M6_STEP1_REPORT.md) — Step 1 trip report (this CR)
- [`docs/engine/CR33_M6_SPIKE_REPORT.md`](../docs/engine/CR33_M6_SPIKE_REPORT.md) — pre-M6 spike (memfd/AHB validation, PASS)
- [`docs/engine/BINDER_PIVOT_DESIGN.md`](../docs/engine/BINDER_PIVOT_DESIGN.md) §3.3 — architectural placement
- [`docs/engine/PHASE_1_STATUS.md`](../docs/engine/PHASE_1_STATUS.md) — phase 1 milestone tracking
