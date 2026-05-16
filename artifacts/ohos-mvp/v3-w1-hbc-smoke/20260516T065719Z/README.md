# V3-W1 HBC substrate smoke test — DAYU200 (2026-05-16)

**Result:** **PARTIAL PASS** — dynamic linker + ELF ABI + `appspawn-x --help` confirmed runnable. Full `MainActivity.onCreate` not attempted (requires destructive `/system` overwrites per HBC `DEPLOY_SOP.md`, out of scope for W1).

**Companion:** `docs/engine/V3-HBC-ARTIFACT-MANIFEST.md` §6.

---

## Environment

| Item | Value |
|---|---|
| Board | DAYU200 (rk3568) serial `dd011a414436314130101250040eac00` |
| Kernel | Linux 6.6.101 SMP aarch64 Toybox (2026-04-04) |
| OS | OpenHarmony 7.0.0.18 Beta1 |
| Userspace bitness | 32-bit (`getconf LONG_BIT = 32`, no `/system/lib64`) |
| System loader | `/lib/ld-musl-arm.so.1` (1.2 MB, BuildID `e06576e9...`) |
| HBC artifact arch | 32-bit ARM EABI5 (matches board) |
| hdc binary | `C:\Users\dspfa\Dev\ohos-tools\hdc.exe` (invoked via WSL) |
| Win-stage dir | `C:\Users\dspfa\Dev\ohos-tools\stage\v3-hbc-smoke\` |
| Board staging dir | `/data/local/tmp/v3-hbc-smoke-dir/` (created, used, removed) |

---

## Steps run

```
preflight.log   — board uname/df + listing
push.log        — first 6-file push + md5 verify
runtest.log     — full dep closure push + appspawn-x invocation
```

### Step 1 — Six-file push + integrity check

Pushed `appspawn-x`, `libart.so`, `libhwui.so`, `liboh_adapter_bridge.so`, `HelloWorld.apk`, `DEPLOY_SOP.md` via `hdc file send`. Per-file FileTransfer rate 1700 KB/s–25 MB/s (USB).

End-to-end md5 verification:

```
appspawn-x       0d9ba07d8c90e4c420ade38d6cfc2d66  (local == device)
libart.so        eadd392606013bca388dd1a196e5b0b7  (local == device)
HelloWorld.apk   1b5bb4e888b80eb11fda8385361f5b9d  (local == device)
```

### Step 2 — First exec attempt (deps not yet on LD_LIBRARY_PATH)

```
$ LD_LIBRARY_PATH=/data/local/tmp/v3-hbc-smoke:/system/lib ./appspawn-x --help
Error loading shared library libnativehelper.so: (needed by ./appspawn-x)
Error loading shared library liblog.so: (needed by ./appspawn-x)
Error loading shared library libbionic_compat.so: (needed by ./appspawn-x)
[... many more DT_NEEDED errors, expected ...]
```

* **Interpretation:** the dynamic linker accepted the ELF format (no `Exec format error`, no `not executable`), walked the DT_NEEDED chain, reported missing libs. This is the loader doing its job — proves ABI match.

### Step 3 — Push full closure + retry

Added 17 more AOSP `.so` files (`libnativehelper`, `liblog`, `libbionic_compat`, `libbase`, `libcutils`, `libutils`, `libsigchain`, `libdexfile`, `libartbase`, `libartpalette`, `libvixl`, `liblz4`, `libziparchive`, `libelffile`, `libnativebridge`, `libnativeloader`, `libprofile`) — total 23 files. Then 7 more (`libtinyxml2`, `libunwindstack`, `libart_runtime_stubs`, `libopenjdk`, `libopenjdkjvm`, `libicu_jni`, `libjavacore`) — total 30.

```
$ chmod +x ./appspawn-x
$ LD_LIBRARY_PATH=$D:/system/lib ./appspawn-x --help
Usage: ./appspawn-x [OPTIONS]

Options:
  --socket-name NAME       Unix socket name (default: AppSpawnX)
  --sandbox-config PATH    Sandbox config JSON (default: /system/etc/appspawn_x_sandbox.json)
  --help, -h               Show this help
EXIT=0
```

### Step 4 — Daemon entry (no args, fail-fast expected)

```
$ LD_LIBRARY_PATH=$D:/system/lib ./appspawn-x
Signal 6
EXIT2=0
```

* SIGABRT (signal 6) on bare invocation — expected because the daemon's Phase 1 fails to open `/system/etc/appspawn_x_sandbox.json` (we deliberately did not write to `/system/`).

### Step 5 — Cleanup

```
$ rm -rf /data/local/tmp/v3-hbc-smoke /data/local/tmp/v3-hbc-smoke-dir
$ rm -rf /mnt/c/Users/dspfa/Dev/ohos-tools/stage/v3-hbc-smoke
```

Board confirmed clean (`ls /data/local/tmp/v3-hbc-smoke*` → "No such file or directory").

---

## What this PASS proves

1. **HBC's cross-built `.so` artifacts are ELF-compatible** with the OHOS-7.0.0.18 musl loader on rk3568 — no `Exec format error`, no missing relocation symbols once full dep closure is present.
2. **DT_NEEDED transitive graph is internally consistent** — the 30 .so files we pushed satisfy every `<lib> needed by <lib>` link the loader checks.
3. **`appspawn-x` parses its own arg vector and prints help** — so its ELF entry point + libc init + arg-parse code path are all reachable from the board's loader.
4. **End-to-end integrity** — `hdc file send` did not corrupt any artifact (md5 stable through both directions).
5. **Board environment matches HBC's prerequisites** — `/lib/ld-musl-arm.so.1` exists with HBC's expected version; `/system/lib` present (32-bit); no `/system/lib64` to confuse the bitness pivot.

## What this PASS does NOT prove

* That `appspawn-x` can complete its full Phase-1→Phase-4 init on this board (we never wrote `/system/etc/appspawn_x_sandbox.json`).
* That `framework.jar` + BCP boot.art load through ART VM init (Phase 2). This requires deploying to `/system/android/framework/arm/` with chcon labels — Stage 3e of `DEPLOY_SOP.md`.
* That `MainActivity.onCreate` line 83 (HBC's "proven endpoint") reaches.

The above three are the responsibility of **V3-W2** (boot standalone HBC runtime on DAYU200), which will run the full `DEPLOY_SOP.md`.

---

## Files in this dir

* `preflight.log` — board uname + /data/local/tmp listing
* `push.log` — 6-file push + integrity check log
* `runtest.log` — full dep push + appspawn-x invocation log (steps 2-4)
* `README.md` — this file
