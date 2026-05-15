# CR61 — Binder Strategy After CR60 (32-bit pivot)

**Date:** 2026-05-14
**Author:** synthesis after the CR60 32-bit dalvikvm pivot landed
**Status:** DECISION — captures binder architectural choice; M10 retargeted; OHOS-libipc explicitly rejected; SELinux plan staged
**Depends on:** `CR60_BITNESS_PIVOT_DECISION.md` (32-bit dalvikvm pivot)
**Updates:** `CR41_PHASE2_OHOS_ROADMAP.md` (M9-M13 still apply, with the corrections in §5 below)
**Does not supersede:** any landed binder code in `aosp-libbinder-port/`. Phase 1 musl + bionic builds stay green.

---

## TL;DR

**We ship our own AOSP-derived `libbinder.so` + `servicemanager` for 32-bit OHOS, talking to the kernel binder driver via `/dev/vndbinder`. We do NOT integrate with OHOS's `libipc.dylib.so` / `samgr`.** Coexistence with OHOS samgr is parallel, not federated — same isolation pattern that worked on Android against `vndservicemanager`. Effort is a `--target` triple change in the existing Makefile, not a rewrite.

---

## Why this decision is needed now

`CR60_BITNESS_PIVOT_DECISION.md` pivoted dalvikvm to 32-bit ARM but explicitly deferred the binder/IPC consequences. Without an explicit answer, the next agent will reasonably ask:

- "OHOS ships `libipc.dylib.so` and `samgr` — should we `dlopen` them instead of shipping our own libbinder?"
- "Does M10 mean rebuilding for OHOS aarch64 musl or 32-bit musl?"
- "DAYU200 is SELinux Enforcing — can a non-root dalvikvm even open `/dev/vndbinder`?"

This CR locks in those answers so the spike isn't redone every time someone reads the roadmap.

---

## Empirical state of the board (verified 2026-05-14)

```
$ hdc shell ls -la /dev/binder /dev/hwbinder /dev/vndbinder
crw-rw-rw- root root 10, 120 /dev/binder
crw-rw-rw- root root 10, 119 /dev/hwbinder
crw-rw-rw- root root 10, 118 /dev/vndbinder

$ hdc shell getenforce
Enforcing

$ hdc shell ls /system/lib/libbinder* /system/lib/libhwbinder*
ls: No such file or directory

$ hdc shell ls /system/lib/libipc* /system/lib/libsamgr*
/system/lib/libipc.dylib.so
/system/lib/libsamgr.dylib.so

$ hdc shell ls -la /system/bin/samgr /system/bin/servicemanager
-rwxr-xr-x root shell 647380 /system/bin/samgr
ls: /system/bin/servicemanager: No such file or directory
```

**What this tells us:**

1. **Kernel binder driver is live** — all three nodes exist and have world-rw DAC. The kernel side of M9 is already done by OHOS.
2. **OHOS does NOT ship `libbinder.so`** — the AOSP userspace IPC ABI is absent. Apps that link against AOSP `IBinder`/`Parcel`/`Stub.asInterface` (i.e., noice, McD, any Android APK) have no host library to call.
3. **OHOS ships its own IPC stack** — `libipc.dylib.so` + `samgr`. This is a wire-incompatible system with different `IRemoteObject` semantics, different parcel format, different service registration model.
4. **DAC is open** (`crw-rw-rw-`) but **MAC is Enforcing**. SELinux is the gate, not Linux permissions.

---

## Decision 1 — Ship our own libbinder, NOT OHOS libipc

We continue to ship the existing `aosp-libbinder-port/` artifacts (cross-compiled from AOSP source) and use them as the only IPC stack inside the Westlake process tree.

**Rationale:**

- Android APKs we host (noice, McD) call AOSP binder APIs through framework.jar / aosp-shim.dex. Re-implementing every framework binder transaction against OHOS `IRemoteObject` would be a massive substitute-everything project — fundamentally the same problem the original Phase 1 "macro-only" decision (`CR28-architect`) was meant to avoid.
- Our `libbinder` already passes 14/14 regression on cfb7c9e3 (Android phone) and handles `addService` / `getService` / `transact` / `replyToReply` / death notifications correctly. There is no engineering case for replacing a working component.
- OHOS's `libipc` would not interoperate with `framework.jar`'s service stubs (`IActivityManager.Stub`, `IWindowManager.Stub`, etc.) without us also writing per-service adapters — that's exactly the "per-app branch" failure mode the macro-shim contract forbids.

**Concrete rejection list:** we do NOT call into `libipc.dylib.so`, `libsamgr.dylib.so`, or `/system/bin/samgr` from our dalvikvm process. We do NOT register Westlake services on OHOS's samgr. We do NOT translate AOSP parcel format ↔ OHOS parcel format.

---

## Decision 2 — Talk to the kernel via `/dev/vndbinder`

Three kernel nodes exist (`/dev/binder`, `/dev/hwbinder`, `/dev/vndbinder`). Pick `/dev/vndbinder` for the same reasons it worked on Android in Phase 1:

- `/dev/binder` may have OHOS-system processes attached to it (verify mid-spike — if empty, switch to it for closer Android parity).
- `/dev/hwbinder` is for HIDL / HAL, not the IPC paths Android apps use.
- `/dev/vndbinder` is the "vendor binder" — historically a separate context, less contention, fewer policy collisions.

Our `servicemanager` binary attaches to `/dev/vndbinder` (see `aosp-libbinder-port/servicemanager.cpp`). This is the same node Phase 1 used on cfb7c9e3.

---

## Decision 3 — Run our own servicemanager in parallel with OHOS samgr

OHOS `samgr` registers OHOS system abilities (display, audio, network, accessibility) on its own channel. We do not federate with it. We launch our own `servicemanager` (from `aosp-libbinder-port/out/servicemanager`) which binds to `/dev/vndbinder` and serves only OUR Westlake services (`WestlakeActivityManagerService`, `WestlakeWindowManagerService`, etc.).

**Coexistence model:**

```
DAYU200 process tree
├── samgr                          ← OHOS, on /dev/samgr_inner (or similar)
│   └── serves: display_ability, audio_ability, ...
├── westlake-servicemanager        ← OURS, on /dev/vndbinder
│   └── serves: activity, window, package, display, notification, input_method
└── dalvikvm (32-bit)
    └── talks to: westlake-servicemanager via our libbinder.so
    └── separately: dlopen libnative_window.so, libaudio_renderer.z.so for in-process OHOS APIs
```

The two service-manager universes don't see each other. AOSP framework code asking for `ActivityManagerService` reaches OUR implementation via OUR libbinder via `/dev/vndbinder`. OHOS code asking for `display_ability` reaches OHOS's implementation via libipc.

This is **exactly** the pattern that worked on Android in Phase 1 where Android's `vndservicemanager` was running alongside ours on different binder contexts.

---

## Decision 4 — SELinux policy plan for `/dev/vndbinder` from non-root

DAYU200 is `Enforcing`. DAC on `/dev/vndbinder` is world-rw, so the kernel won't reject our `open()` for permission reasons — but SELinux MAC may.

**Probe sequence (do this before M10 rebuild):**

1. `hdc shell "ls -Z /dev/vndbinder"` — capture the binder node's SELinux label (likely `u:object_r:binder_device:s0` or OHOS equivalent).
2. `hdc shell "id -Z"` — confirm our process domain. From a non-root `hdc shell` it'll be `u:r:hdcd:s0` (the hdcd shell context).
3. From a dalvikvm test process spawned by hdcd, try `open("/dev/vndbinder", O_RDWR)`. If it works, we're done — SELinux already permits the transition.
4. If `EACCES`, capture the audit log: `hdc shell "dmesg | grep avc | tail -20"`. Diagnose whether the deny is on `binder_device:open` / `binder_device:read` / `binder_device:ioctl`.

**Three escalation tiers (least intrusive first):**

- **Tier A — Use existing rules.** OHOS may already permit `hdcd` or `shell` domain to `binder_device:{open read write ioctl}`. If so, no policy change needed.
- **Tier B — Run dalvikvm as a domain that has access.** Spawn via OHOS init service config so the process gets a binder-allowed domain (e.g., `u:r:samgr:s0` — though sharing samgr's label is risky; better is a Westlake-dedicated domain).
- **Tier C — Add a policy module.** `sepolicy/westlake.te` adds a domain `westlake_app` with full binder access. Loaded via `restorecon` + `load_policy` on board. Requires either root or a vendor-policy boot path.

**This CR explicitly forbids `setenforce 0` as a "fix"** — it disables MAC for the entire board, defeats the security model, and gives a false signal about production viability. If SELinux turns out to be the hard blocker, we **escalate to documented policy work, not workarounds.**

---

## Decision 5 — M9/M10/M11/M12 retargeting matrix

| Milestone | Original (Phase 2 roadmap) | Post-CR60 |
|---|---|---|
| M9 — binder.ko for OHOS kernel | "Build / port binder.ko" | **DONE by OHOS itself.** All 3 nodes present, kernel driver live. Trivially verifiable. |
| M10 — libbinder + servicemanager for OHOS sysroot | Cross-compile for `aarch64-linux-ohos-musl` | **Cross-compile for `arm-linux-ohos-musl`** (32-bit). Same Makefile, new `--target` triple, sysroot already exists at `dalvik-port/ohos-sysroot-arm32/`. |
| M11 — Audio daemon swap backend → OHOS AudioRenderer | Out-of-process daemon swap | **In-process** `System.loadLibrary("libaudio_renderer.z.so")` from 32-bit dalvikvm. Daemon process disappears from production path. M5 daemon kept as fallback. |
| M12 — Surface daemon swap backend → OHOS XComponent | Out-of-process daemon swap | **In-process** `System.loadLibrary("libnative_window.so")` + XComponent NAPI. Daemon disappears from production. M6 daemon kept as fallback. Smoke test already PASS today (`ohos_dlopen_smoke.c` resolved `OH_NativeWindow_NativeWindowRequestBuffer`). |
| M13 — noice on OHOS phone | (terminal milestone) | Unchanged in spirit, scoped to the new architecture. |

**The M11/M12 in-process path requires dynamic-PIE dalvikvm** (currently the 32-bit binary is static — see CR60 spike report). The dynamic retarget is the next ~½-day gate after CR60.

---

## Implementation plan (concrete)

### M10-arm32 — Cross-compile libbinder for 32-bit OHOS musl

1. In `aosp-libbinder-port/Makefile`, add a `musl-arm32` target paralleling existing `musl` (aarch64) and `bionic` (Android NDK).
2. Toolchain: same OHOS Clang 15 already used, with `--target=arm-linux-ohos --march=armv7-a -mfloat-abi=soft` (or `softfp` depending on what the OHOS sysroot expects — verify against `dalvik-port/ohos-sysroot-arm32` build flags).
3. Sysroot: `dalvik-port/ohos-sysroot-arm32/`.
4. Output: `aosp-libbinder-port/out/arm32/{libbinder.so, servicemanager, libutils_static.a, libcutils_static.a, libbase_static.a}`.
5. Smoke test: `HelloBinder.dex` running under dalvikvm-arm32 → calls our libbinder → reaches `/dev/vndbinder` → registers a test service → retrieves it back. Identical surface to existing M3 regression.

**Estimated effort: 1-2 person-days.** Cross-compile path is well-trodden — the only unknown is which OHOS arm32 ABI flag set works (`soft` vs `softfp` vs `hard`); pick the one matching `dalvik-port/ohos-sysroot-arm32/usr/lib/` libs.

### M10-arm32 — SELinux probe

Done in parallel with M10 build. Three escalation tiers as in Decision 4 §SELinux. Likely outcome on a dev board: `hdcd` shell already has `binder_device` access, no policy change needed. Worst case: a 50-line `westlake.te` policy module.

### M11-inprocess / M12-inprocess — Defer

These belong to a separate CR once M10-arm32 lands and the dynamic-PIE dalvikvm spike (next CR60 follow-up) is green. Not in scope here.

---

## What this CR does NOT decide

- **Whether to use atomic KMS** (DRM/KMS path) instead of XComponent for compositor. That's M12 territory and depends on XComponent in-process performance.
- **Whether HDI services** (the OHOS HAL framework, accessed via `/dev/hwbinder`) need any of our IPC. They don't — apps don't speak HDI.
- **What apps not running on Hilt** look like on this substrate. That's an app-validation question, not a binder strategy question.

---

## Self-audit checklist (to run when M10-arm32 lands)

- [ ] `libbinder.so` produced for arm-linux-ohos-musl; symbols match Phase-1 aarch64 musl variant
- [ ] `servicemanager` produced for same; binds to `/dev/vndbinder` cleanly on board
- [ ] `HelloBinder.dex` smoke test PASS under dalvikvm-arm32 on board
- [ ] SELinux probe complete; documented in `artifacts/ohos-mvp/m10-arm32/selinux-probe.md`
- [ ] No `setenforce 0` in any script
- [ ] No `dlopen("libipc.dylib.so")` anywhere in the Westlake codebase
- [ ] aarch64 build still passes (no parallel regression)

---

## Cross-references

- `docs/engine/CR60_BITNESS_PIVOT_DECISION.md` — the bitness pivot this CR follows up
- `docs/engine/OHOS_MVP_WORKSTREAMS.md` — Workstream E (32-bit dalvikvm)
- `docs/engine/CR41_PHASE2_OHOS_ROADMAP.md` — original M9-M13 plan (updated by Decision 5 matrix above)
- `docs/engine/BINDER_PIVOT_DESIGN_V2.md` — V2 substrate design (bitness-neutral; unchanged)
- `docs/engine/BINDER_PIVOT_MILESTONES.md` — milestone definitions (M9-M13 commentary updated)
- `aosp-libbinder-port/README.md` — current build state; needs `musl-arm32` target added
- Memory: `feedback_bitness_as_parameter.md` — discipline rules (also apply here)
- Memory: `project_binder_pivot.md` — to be refreshed by next memory-maintenance pass after M10-arm32 lands
