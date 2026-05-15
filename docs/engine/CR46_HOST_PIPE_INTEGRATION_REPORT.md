# CR46_HOST_PIPE_INTEGRATION_REPORT.md — Wire the host APK's pipe reader to the M6 daemon's DLST FIFO

**Status:** done (build clean 2026-05-13; pipe-path constant + connector landed; end-to-end smoke deferred to M7)
**Owner:** Builder (CR46)
**Companion to:** `M6_STEP4_REPORT.md` (consumer-side writer; established the wire format), `WestlakeVM.kt` §47/§61/§1746 (existing reader-side wire-format constants + replay branch), `PHASE_1_STATUS.md`
**Predecessor:** M6-Step4 done (daemon writes verified DLST frames to `/data/local/tmp/westlake/dlst.fifo`).
**Successor:** M7 (end-to-end host SurfaceView pixel-coherency smoke once M5/M6/M7/M8 daemons + scripts are running together on phone).

---

## §1  Goal

M6-Step4 confirmed the surface daemon writes **byte-for-byte** the same
display-list-frame envelope (`uint32 magic = 0x444C5354 "DLST"` +
`uint32 size` + `OP_ARGB_BITMAP=12` payload) that the Compose host's
existing `WestlakeVM.kt` pipe reader already parses (`§1746`
`OP_ARGB_BITMAP` branch + `§1864` `readPipeAndRender`). What was still
missing on the host side was a **way to feed the daemon's FIFO** into
that reader: every existing code path in `WestlakeVM.kt` populates
`pipeStream` from `ProcessBuilder.start().inputStream` (a subprocess's
stdout) or a `TailFileInputStream` (subprocess writes to a regular
file). Neither matches the named-FIFO model the daemon uses.

CR46 wires the reader-side path:
1. Hard-codes the daemon's FIFO path on the host with the same value
   the daemon's `surfaceflinger_main.cpp:kDefaultDlstPipe` falls back to
   and that `m6step4-smoke.sh` / `m6step5-smoke.sh` `mkfifo`'s.
2. Adds a `connectDaemonFifo(path)` API on the `WestlakeVM` object that
   opens the FIFO with `FileInputStream`, parks the resulting stream in
   the existing `pipeStream` field, and triggers the existing
   `startPipeReader()` path. The reader loop, the wire-format
   sync-to-magic, the `OP_ARGB_BITMAP` decode, the
   `SurfaceHolder`/`Surface`/`DisplayListFrameView` rendering branches,
   and the touch-routing side channel are all reused without change.
3. Adds a `connectDaemonFifoAsync(path)` convenience helper that does
   the FIFO open on a daemon thread (since `O_RDONLY` on a FIFO blocks
   until the writer opens, that open MUST NOT happen on the UI
   thread), plus a watcher that retries `startPipeReader()` until both
   `surfaceHolder` and `pipeStream` are present (mirrors the existing
   `PipeReaderStarter` polling pattern used by the subprocess code
   path).

The change is intentionally **a one-direction additive bridge** —
`WestlakeVM`'s legacy subprocess code path is untouched. CR46 simply
expands the set of ways the same reader can be fed; whichever caller
(legacy `start()` or the new daemon-integration path) wins, the rest
of the rendering machinery is identical.

---

## §2  What was built

### 2.1  Files touched

```
westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt
  + const WESTLAKE_DLST_FIFO = "/data/local/tmp/westlake/dlst.fifo"
  + fun connectDaemonFifo(path: String = WESTLAKE_DLST_FIFO): Boolean
  + fun connectDaemonFifoAsync(path: String = WESTLAKE_DLST_FIFO)
  Net: +~75 LOC, no removals, no edits to existing methods.

westlake-host-gradle/app/build/outputs/apk/debug/app-debug.apk
  Rebuilt — same 31_462_182 byte envelope (additive symbols deduplicate
  inside dex's string pool to a near-flat delta).
```

### 2.2  Files NOT touched (anti-drift compliance)

Per the macro-shim contract (`memory/feedback_macro_shim_contract.md`)
and the CR46 brief's FILES NOT TO TOUCH list:
- `shim/java/**` — CR43/CR44 active; not edited.
- `aosp-surface-daemon-port/**` — M6-Step5 active; not edited.
- `aosp-audio-daemon-port/**` — M5-Step5 active; not edited.
- `art-latest/**`, `aosp-libbinder-port/**` — substrate stable.
- `scripts/**`, `aosp-libbinder-port/test/**` — M7/M8 active; not edited.
- `aosp-shim.dex` — unchanged.
- Memory files — unchanged.
- `westlake-host-gradle/app/src/main/AndroidManifest.xml` — not edited:
  `FileInputStream` on `/data/local/tmp/westlake/dlst.fifo` is in the
  shell-writable, app-readable corner of the filesystem; the host APK
  runs as a user-installed debuggable app with `READ_EXTERNAL_STORAGE`
  already granted, and the FIFO has mode 0666 (per
  `m6step4-smoke.sh:mkfifo -m 666 …`). No new permission needed.

Zero per-app branches introduced. Zero `Unsafe` / `setAccessible`.

---

## §3  Wire-format alignment (host reader ↔ daemon writer)

The CR46 brief asked specifically whether the host reader's wire format
matches what M6-Step4 documented for the daemon writer. The answer is
**yes, exactly**, and no edits to the reader loop were required.

| Field | Daemon emits (`DlstConsumer.cpp`) | Host reads (`WestlakeVM.kt`) |
|---|---|---|
| envelope[0] uint32 LE | `kDlstMagic = 0x444C5354u` | `DLIST_MAGIC = 0x444C5354` (`§47`) |
| envelope[1] uint32 LE | `payloadLen` | `size` (`§1908`) |
| payload[0] byte | `kOpArgbBitmap = 12` | `OP_ARGB_BITMAP = 12` (`§61`, switched at `§1746`) |
| payload[1..4] float LE | `fx = 0.0f` | `bb.getFloat()` → `x` (`§1747`) |
| payload[5..8] float LE | `fy = 0.0f` | `bb.getFloat()` → `y` (`§1747`) |
| payload[9..12] int32 LE | `(int32_t)w` | `bb.getInt()` → `w` (`§1748`) |
| payload[13..16] int32 LE | `(int32_t)h` | `bb.getInt()` → `h` (`§1748`) |
| payload[17..20] int32 LE | `dataLen = w*h*4` | `bb.getInt()` → `dataLen` (`§1749`) |
| payload[21..] | stride-cropped raw RGBA8888 | `IntArray(pixelCount)` decoded `(a<<24)\|(r<<16)\|(g<<8)\|b` (`§1755-§1761`) |

Both sides also agree on endianness: daemon uses host byte-order on
ARM64 (little-endian, no explicit byteswap) and the host wraps the
buffer with `ByteOrder.LITTLE_ENDIAN`.

Both sides also agree on the magic-resync policy: if the reader sees a
size that's `<=0` or `>2 MiB`, it walks the byte stream until the next
`DLIST_MAGIC` (`§1914-§1919`). The daemon writes the envelope before
each frame so a healthy stream never needs to re-sync, but the cost is
bounded if a frame is dropped mid-write (the daemon's `EPIPE`
handling never partially writes the envelope before the payload, so
the reader's worst-case re-sync is a single 8-byte advance).

**Therefore CR46's host edit reduces to wiring the FIFO into
`pipeStream`** — the reader, the OP_ARGB_BITMAP decoder, and the
`SurfaceHolder.lockCanvas → unlockCanvasAndPost` rendering path are
correct as-is.

---

## §4  API surface added

### 4.1  `WestlakeVM.WESTLAKE_DLST_FIFO`

```kotlin
const val WESTLAKE_DLST_FIFO: String = "/data/local/tmp/westlake/dlst.fifo"
```

Hard-coded to match
`aosp-surface-daemon-port/native/surfaceflinger_main.cpp:kDefaultDlstPipe`
and `m6step{4,5}-smoke.sh`'s `mkfifo` argument. The daemon side honors
the `$WESTLAKE_DLST_PIPE` env var to override at daemon launch time;
the host side currently picks up only the compile-time constant. If a
future M7 / M8 driver needs to override the host's path too (e.g. to
exercise multi-instance setups), the new methods both accept an
explicit `path: String` argument that overrides the constant.

### 4.2  `WestlakeVM.connectDaemonFifo(path = WESTLAKE_DLST_FIFO): Boolean`

Synchronous (caller-thread) FIFO connect:
1. Returns `false` early if `pipeStream != null` (idempotent — never
   clobbers an in-flight subprocess stdout or another daemon FIFO).
2. Calls `FileInputStream(path)` — **this blocks** until the daemon
   opens its end for write. Linux FIFO semantics.
3. On success: assigns `pipeStream = fis` and, if a surface is already
   attached, kicks off `startPipeReader()`.
4. On exception: logs at `Log.e` level and returns `false`.

Intended caller: an M7 / M8 integration driver that wants direct
control over which thread the FIFO open runs on (for predictable
test-harness lifecycles).

### 4.3  `WestlakeVM.connectDaemonFifoAsync(path = WESTLAKE_DLST_FIFO)`

Asynchronous (daemon-thread) FIFO connect:
1. Spawns `DaemonFifoConnect` thread to call `connectDaemonFifo(path)`
   (so the blocking `O_RDONLY` open never stalls the UI thread).
2. Spawns `DaemonFifoPipeReaderStarter` thread that polls every 200 ms
   for `surfaceHolder != null && pipeStream != null`, then triggers
   `startPipeReader()`. Mirrors the same retry loop used by the
   existing subprocess `start()` path (`§1477-§1485`).

This is the recommended caller for Compose-host integration: the
SurfaceView creation callback can fire before the FIFO open completes,
or vice versa; either ordering works because the watcher waits for
both.

---

## §5  Build verification

```
$ cd westlake-host-gradle && ./gradlew assembleDebug 2>&1 | tail -10
> Task :app:compileDebugKotlin
> Task :app:compileDebugJavaWithJavac NO-SOURCE
> Task :app:dexBuilderDebug
> Task :app:mergeProjectDexDebug
> Task :app:packageDebug
> Task :app:assembleDebug
BUILD SUCCESSFUL in 28s
33 actionable tasks: 6 executed, 27 up-to-date
```

5 pre-existing `kotlinc` warnings (all 4-line LOC-pre-existing in
`WestlakeVM.kt:977/1307/1313/1594/2280`); CR46 adds zero new warnings.

APK output: `westlake-host-gradle/app/build/outputs/apk/debug/app-debug.apk`
(31_462_182 B). DEX-string-pool verification confirms the new symbols
made it in:

```
$ unzip -p app-debug.apk classes4.dex | strings | grep -E \
    "connectDaemonFifo|dlst\.fifo|WESTLAKE_DLST_FIFO"
/data/local/tmp/westlake/dlst.fifo
WESTLAKE_DLST_FIFO
connectDaemonFifo
connectDaemonFifo$default
connectDaemonFifo(
connectDaemonFifo: open OK, fifo=
$i$a$-apply-WestlakeVM$connectDaemonFifoAsync$2
$i$a$-apply-WestlakeVM$connectDaemonFifoAsync$4
$this$connectDaemonFifoAsync_u24lambda_u245
$this$connectDaemonFifoAsync_u24lambda_u247
```

`connectDaemonFifo$default` is Kotlin's compiler-generated overload
for the default-argument variant; `connectDaemonFifoAsync` shows up in
the lambdas for the two daemon-thread spawns. All expected.

---

## §6  End-to-end smoke status

The CR46 brief explicitly notes "this test may NOT fully work yet (M5 /
M6 still in flight + need full integration via M7/M8 scripts)" and
asks for **mechanical** verification only. The mechanical state is:

| Component | State |
|---|---|
| M6-Step4 daemon writes DLST frames to FIFO | confirmed (smoke PASS on cfb7c9e3 2026-05-13, `m6step4-smoke-run.log` line 298 `[surface_smoke] G: PASS magic=0x444c5354 …`) |
| M6-Step5 daemon (composer / vsync / TX_STATE / autostart) | in flight per concurrent agent — daemon binary not yet a single-shot service |
| Daemon FIFO path | `/data/local/tmp/westlake/dlst.fifo` (env-var overridable) |
| Host APK constant matches daemon path | yes (`WESTLAKE_DLST_FIFO`) |
| Host wire-format parser matches daemon emit | yes (no edits needed — see §3) |
| Host has connector API | yes (`connectDaemonFifo` / `connectDaemonFifoAsync`) |
| Host caller wires the new API into Compose | **not yet** — left for M7 |

The deliberate non-decision in CR46 is to **NOT** also edit
`WestlakeActivity.kt` or `WestlakeVMScreen` to call
`connectDaemonFifoAsync()` at activity start. The reason is that the
M7 brief is in flight in a parallel agent and owns the orchestration
question (which Compose screen mode triggers daemon-FIFO vs.
subprocess-stdout; which SurfaceView size + scaling matches the
daemon's surface dimensions; how to coordinate with the daemon's
SystemServer-style autostart). CR46 lays the host-side primitive
that M7 will call; calling it now would land a per-app activity
branch that violates the macro-shim contract's "no per-app hacks"
rule (every Westlake-managed APK uses the same subprocess pipe path
today — switching one and not the others would be a per-app
branch).

Once M7 is ready, the integration is a one-liner from the M7 driver:

```kotlin
WestlakeVM.connectDaemonFifoAsync()
```

…called any time after the host APK has come up far enough to know
the daemon is running. The watcher thread handles SurfaceView
attach ordering.

---

## §7  Anti-drift compliance & person-time

**Files touched (within sanctioned scope per CR46 brief):**
- `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt`
  (+~75 LOC, no removals).
- `westlake-host-gradle/app/build/outputs/apk/debug/app-debug.apk`
  (rebuilt).
- `docs/engine/CR46_HOST_PIPE_INTEGRATION_REPORT.md` (this file, NEW).
- `docs/engine/PHASE_1_STATUS.md` (CR46 row).

**Files NOT touched** (per brief): `shim/java/**`, `art-latest/**`,
`aosp-libbinder-port/**`, `aosp-audio-daemon-port/**`,
`aosp-surface-daemon-port/**`, `aosp-shim.dex`, `scripts/**`,
`aosp-libbinder-port/test/**`, memory files,
`westlake-host-gradle/app/src/main/AndroidManifest.xml`.

**Zero per-app branches** — `connectDaemonFifo` is a generic
substitute for the subprocess `pipeStream` plumbing; the same call
works for any guest the daemon emits to.

**Zero `Unsafe` / `setAccessible`** — pure Kotlin stdlib
(`java.io.FileInputStream`).

**Person-time**: ~50 min (well inside the 1-2 hour budget).
Breakdown: ~20 min reading `WestlakeVM.kt` pipe-reader internals +
`DlstConsumer.cpp` writer + `M6_STEP4_REPORT.md` wire-format spec;
~10 min designing the additive API (declined to ship a per-app
activity edit per anti-drift contract); ~10 min coding + recompile
verification; ~10 min report.

---

## §8  Cross-references

- `aosp-surface-daemon-port/native/DlstConsumer.cpp:31-53` —
  `writeFully` retry policy.
- `aosp-surface-daemon-port/native/DlstConsumer.cpp:129-264` —
  `writeFrame` envelope + header + payload write order (matches §3
  table above exactly).
- `aosp-surface-daemon-port/native/surfaceflinger_main.cpp` (search
  for `kDefaultDlstPipe`) — default path string source.
- `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt:47`
  — `DLIST_MAGIC` constant.
- `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt:61`
  — `OP_ARGB_BITMAP` constant.
- `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt:1746-1768`
  — host-side OP_ARGB_BITMAP decoder.
- `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt:1860-2013`
  — host-side `readPipeAndRender` / `readPipeAndRenderLocked`.
- `westlake-host-gradle/app/src/main/java/com/westlake/host/WestlakeVM.kt`
  (CR46-added) — `WESTLAKE_DLST_FIFO`, `connectDaemonFifo`,
  `connectDaemonFifoAsync`.
- `docs/engine/M6_STEP4_REPORT.md` §3.3 — wire format spec.
- `docs/engine/M6_SURFACE_DAEMON_PLAN.md` §4.2 — Phase-1 DLST pipe
  backend.
- `memory/feedback_macro_shim_contract.md` — anti-drift constraints
  observed by §2.2 + §7.
