# Westlake — PF-noice 5-Day Delivery Plan (D1 = instrumentation)

**Date:** 2026-05-04 PT
**Author:** design agent (read-only deep-design phase, no phone, no runtime, no commits)
**Companion docs:**
- `docs/program/WESTLAKE_NOICE_DAY1_RESULTS_20260503.md` (empirical record + open hypothesis)
- `docs/program/WESTLAKE_FULL_MCD_GAP_REPORT_20260503.md` §13.7-§13.12 (PF-630 substrate-fix arc)
- `docs/program/WESTLAKE_NOICE_1DAY_CONTRACT_20260503.md` (acceptance shape)
**Phone:** OnePlus 6 `cfb7c9e3` (LineageOS 22 / Android 15, rooted, SELinux permissive)
**Runtime baseline at start:** `fda3db92031c43752e54f5034ab6556a46aa7312d326634656720f32a9782508` (gate-restored, post-§13.12)
**Shim baseline at start:** `d31fa0809f163b720a59af48c6ce43fd5a60b516f0d1abd283084a61ee0b9551`
**Note:** This document IS the D1 deliverable. Implementation is for the next agent (D1 instrumentation patch + iterative D2-D5 work).

---

## 1. Scope, acceptance, day plan

### 1.1 Why this plan exists

Day-2 part-2 of the noice 1-day proof (§"Day-2 update part 2" in the results doc) eliminated the PF-630 SIGBUS at sentinel `0xfffffffffffffb17` and got noice `MainActivity.performResume` to log DONE inside the Westlake guest. **No noice UI paints.** Four iterations attacked the symptom (recovery branches + bypass disable) and all failed because the cause is upstream: a boot-class clinit cascade that survives both the PF-630 boot-aware gate AND complete bypass disable, and that destroys static state in `ICUBinary`, `CoderResult`, `Providers`, `Crypto`, `Charset`, `LanguageTag`, `Date`, `DefaultProxySelector`, `Build`, plus the `View` class itself (per `setId NPE` at v3 programmatic). Empirically:

```
Tolerating clinit failure for Landroid/icu/impl/ICUBinary;       ArrayStoreException
Tolerating clinit failure for Ljava/nio/charset/CoderResult;     ArrayStoreException
Tolerating clinit failure for Lsun/security/jca/Providers;       ArrayStoreException
Tolerating clinit failure for Landroid/os/Build;                 ArrayStoreException
Tolerating clinit failure for Lsun/util/locale/LanguageTag;      ArrayStoreException
Tolerating clinit failure for Ljava/util/Date;                   ArrayStoreException
performCreate error: ArrayStoreException: String cannot be stored in array of type String[]
View.setId NPE on programmatic LinearLayout
```

The exception text `java.lang.String cannot be stored in an array of type java.lang.String[]` is paradoxical — both element class and value class are `String`. This is the canonical signature of a `String[]` whose `mirror::Class*` slot has been overwritten with a class whose own component-type pointer points to `[Ljava/lang/String;` instead of `Ljava/lang/String;`. Some boot-time write is corrupting array-class metadata or backing storage.

The hypothesis we will test in D1: **a boot-time `Unsafe`-array write goes through a path that bypasses element-type checks and lands the wrong `mirror::Class*` (or wrong-typed reference) into a String-typed slot before the gate flips.** Candidates that do NOT survive empirical disable (already eliminated):

- The PFCut Unsafe-array bypass alone (gate-disable test in iter 4 still cascades). Therefore the bad write is happening through SOMETHING ELSE.

Possibilities not yet ruled out (D1 instrumentation must distinguish them):

1. The dalvikvm.cc / runtime.cc `[RT] Set ConcurrentHashMap ABASE=12 ASHIFT=2` patcher — if these values are wrong for this build's reference layout, every CHM-backed boot map is misaligned. **For aarch64 OHOS dalvikvm with compressed object refs (`kHeapReferenceSize=4`), `ABASE=12` and `ASHIFT=2` are correct** (header is 12 bytes, scale is 4, log2(4)=2). For uncompressed refs (`kHeapReferenceSize=8`), `ABASE` would be 16 and `ASHIFT=3`. The runtime.cc patcher computes both at runtime from `mirror::Array::DataOffset(sizeof(mirror::HeapReference<mirror::Object>))` so the values match the build's actual layout — **this is unlikely to be wrong**, but we add a verification log line to confirm.
2. Some other `PFCut*` mechanism — `PFCutPf625*` widening which §13.7 already attributed earlier ASE cascades to. The PF-630 boot-aware gate fixed THE specific routing path through `PFCutObjectArrayIndexFromOffset`, but `PFCutPf625EntryLooksInvalid` widening (`interpreter_common.cc:107-116`) STILL flags any 4096-or-below or high-half pointer as invalid and forces the JNI dispatch through `PFCutUnsafe[Get|Set]ObjectArraySlot` — which the gate flips off but only via the array-index path. The interpreter-bridge sites at `interpreter_common.cc:9481, 10840, 11199` are NOT gated.
3. `obj->CasFieldObject<>` direct mirror-template path (called from `Unsafe_compareAndSwapObject` line 234 *after* the `PFCutObjectArrayIndexFromOffset` gate) — this CAS uses `MemberOffset(offset)` where `offset` may be an array-element offset, not a field offset. Stock dalvikvm typed-array semantics don't apply.
4. Reflection-driven set-static via the launcher's `WestlakeLauncher.prepareCoroutineRuntimeForWestlake` / `seedCoroutine*` — these reflect on internal kotlinx/coroutines fields and may write through `Field.setAccessible` + `Field.set` of String into typed slots whose declaring class wasn't fully initialized.
5. `class_linker.cc` resolved-method patching at line 2386 (`RegisterNatives stable path`) — if this bypasses DEX flag checks while still using array-write paths, it could corrupt method-array entries.

D1 is the **read-the-tape** day: instrument every `Unsafe`-backed array-write between VM start and `performResume DONE`, dump source method, array class, value class, length, index, assignability, thread, monotonic timestamp. Look at the trace, read it, isolate the bad write, then jump to D2 to fix it.

### 1.2 Acceptance for the 5-day cycle

The D5 endgate is **noice's actual UI painted on phone, with a 5-min soak no-SIGBUS, and McD's bounded green not regressed**. Per-day acceptance below.

### 1.3 Five-day plan summary

| Day | Goal | Primary deliverable | Acceptance |
|---|---|---|---|
| **D1** | Instrument substrate; localize root cause | runtime.cc + Unsafe.cc patches that emit per-Unsafe-array-write trace; an analysis doc identifying the offending write | Trace contains ≥1 line where `array.componentType.IsAssignableFrom(value.class) == false`, with source method named, BEFORE first `Tolerating clinit failure for Landroid/icu/impl/ICUBinary` |
| **D2** | Fix root cause | Either runtime patch (e.g. type-check the offending path), shim patch (e.g. defer the corrupting reflection until after gate flip), OR Java-side static seeding (e.g. preload `String[].class` before any reflection). | Same trace under `WESTLAKE_PFCUT_TRACE=1` shows zero non-assignable writes during boot |
| **D3** | Noice MainActivity paints view tree end-to-end | Updated noice gate run + screen.png with recognizable noice UI | A2/A3/A6 from contract: ≥5 views inflated, screenshot shows noice |
| **D4** | McD bounded gate doesn't regress + 5-min soak | Real-mcd bounded artifact + noice 5-min soak | bounded-mcd PASS + soak no-SIGBUS |
| **D5** | Audio-gap stub (parallel workstream) + final wrap-up doc | `ohbridge_stub.c` adds 50 LOC AudioFocus + NotificationManager stubs; results doc; ALL commits pushed in one batch | Noice foreground-service-init reaches AudioTrack-construct gate |

---

## 2. PF-noice issue ledger

### PF-noice-001 — Localize boot-class ArrayStoreException cascade root cause (D1)

- **Scope**: Add per-Unsafe-array-write instrumentation in the runtime so every write between VM start and `performResume DONE` is logged with source-method, array class, value class, index, length, assignability check. Add a stop-signal (JNI from Java when `MainActivity.onResume` completes) so trace cap is bounded but full. Add an ABASE/ASHIFT verification log line. Run noice once with the instrumented runtime; eyeball the trace; identify the first non-assignable write and the calling Java method.
- **Acceptance**:
    1. New trace file `/sdcard/westlake_pfcut_trace.txt` (or `stderr` redirected) contains at least one line of shape `[PFCUT-TRACE] thread=<name> ts=<monotonic_us> caller=<javaMethod> array.class=<...> array.componentType=<...> array.length=<N> index=<I> value.class=<V> assignable=<0|1>`.
    2. Trace is bounded ≤200 lines per thread (thread-local counter).
    3. Trace stops emitting once `MainActivity.onResume` calls a new Java→JNI `nativePfcutTraceStop()` hook.
    4. ABASE/ASHIFT verification log line `[PFCUT-VERIFY] ABASE=<N> ASHIFT=<M> stock_aarch64_compressed_expected=ABASE=12 ASHIFT=2 ABASE=<N> match=<0|1>` printed once at runtime startup before the first ASE.
    5. The trace contains ≥1 line where `assignable=0`. The Java caller frame in that line is the first hypothesis-target for D2.
- **Effort**: 4-6h (instrumentation patch + build + sync + 1 noice gate run + read tape).
- **Dependencies**: none.
- **Risk**: instrumentation timing perturbs the cascade so it doesn't reproduce; mitigation = trace-to-file (durable) + skip stderr fflush per-line for a 2nd run if needed.

### PF-noice-002 — Fix root cause in runtime/shim/Java seed (D2)

- **Scope**: Read the D1 trace, classify the bad write, and apply ONE of the following (in order of cheapness): (a) Java-side preload — call `Class.forName("[Ljava.lang.String;")` and pin `String[].class` from `WestlakeLauncher` before any reflection; (b) defer the offending Unsafe-or-reflection write until after the gate flips; (c) runtime patch — type-check the offending path. Do NOT widen any predicate; we already know widening cascades.
- **Acceptance**:
    1. After patch + rebuild + sync, instrumented trace shows zero `assignable=0` lines during boot phase.
    2. `Tolerating clinit failure for L<each ICU/Charset/Provider/Crypto/Build/LanguageTag/Date/DefaultProxySelector>` count = 0 in the noice boot logcat.
    3. McD bounded baseline still passes (PF-noice-004 also).
- **Effort**: 4-12h (depends on which of a/b/c — a is fastest).
- **Dependencies**: PF-noice-001.
- **Risk**: trace points to multiple bad writes corrupting different arrays; need to apply multiple sub-fixes. Mitigation = if D2 finds N>1 root causes, split into PF-noice-002a..N and budget D2-D3 for them.

### PF-noice-003 — Noice MainActivity paints view tree end-to-end (D3)

- **Scope**: With PF-noice-002 landed, run a noice gate. Verify A2 (`onResume` reaches), A3 (≥5 views inflated), A6 (screenshot). If still fails, identify residual blocker (likely Hilt or `lateinit subscriptionBillingProvider`) and either shim it or fall back to `MiniActivityManager.tryRecoverContent`'s programmatic-fallback path (now that View class is no longer poisoned).
- **Acceptance**:
    1. `NOICE_VIEW_INFLATED count >= 5` in `check-noice-proof.txt`.
    2. `screen.png` SHA differs from host-Compose-home baseline `c8b3f5...` (or whatever the stored host hash is — see `screen_hash.txt` baseline at `artifacts/noice-1day/20260503_232600_*` host hash).
    3. Visual inspection of `screen.png` shows recognizable noice UI elements (tabs / sound cards / loading indicator).
- **Effort**: 4-8h.
- **Dependencies**: PF-noice-002.
- **Risk**: View tree inflates but RecyclerView remains empty (data never populated because Hilt can't inject `subscriptionBillingProvider`). Mitigation = day-3 acceptance accepts a static screen with header bar + nav as "rendered"; full RecyclerView is D4-stretch.

### PF-noice-004 — McD bounded gate doesn't regress (D4)

- **Scope**: Run `scripts/run-real-mcd-phone-gate.sh` with the new runtime + shim. Compare to baseline `20260502_175722_mcd_48h_network_pf621_bounded_final/`.
- **Acceptance**:
    1. `gate_status=PASS` (sub-gate FAILs are OK if they FAIL'd in baseline too — diff against baseline rather than absolute).
    2. `westlake_subprocess_purity ... direct_mcd_processes=0`.
    3. No new SIGBUS lines in logcat-dump.txt.
- **Effort**: 1-2h.
- **Dependencies**: PF-noice-002.
- **Risk**: D2 fix accidentally tightens a path that McD's brittle Application.onCreate relied on. Mitigation = revert and re-design D2 fix; budget D4 to be fix-iterate, not just verify.

### PF-noice-005 — 5-min soak with real UI rendered (D4-D5)

- **Scope**: Noice gate with `NOICE_SOAK_SECONDS=300` set and one tap dispatched via `NOICE_GATE_TAPS=center`. Watch for SIGBUS / kill / VM-exit.
- **Acceptance**:
    1. `noice_5min_soak_status=PASS` in checker.
    2. Logcat-dump.txt has no `signal 7 (SIGBUS)`, no `Fatal signal`, no `VM process exited`.
    3. `vm_pid` still alive at 5min.
- **Effort**: 1h (just run the soak).
- **Dependencies**: PF-noice-003.
- **Risk**: SIGBUS reappears at minute 4 due to a different clinit triggered by user interaction. Mitigation = capture state for PF-noice-008 follow-on.

### PF-noice-006 — Audio-gap stub: AudioFocus + NotificationManager (D5, parallel)

- **Scope**: Per Agent 4's `_noice_audio_gap.txt` 1-day-stub recommendation, add ~50 LOC to `art-latest/stubs/ohbridge_stub.c` for `android.media.AudioManager.requestAudioFocus`, `abandonAudioFocus`, `android.app.NotificationManager.{getNotificationChannel,createNotificationChannel,notify,cancel}` returning sane defaults so noice's foreground media service `onCreate` can return without throwing.
- **Acceptance**:
    1. After service start, logcat shows `am S` enter for `com.github.ashutoshgngwr.noice.MediaPlaybackService` (or its real class name from the manifest) with no immediate exception.
    2. `AudioTrack` constructor reached (logcat marker added by Agent 4 work).
    3. **Sound silence is OK** — getting actual playback is multi-week per the gap doc.
- **Effort**: 4-6h (mostly research finding the right stub return shapes).
- **Dependencies**: PF-noice-003 (without UI, foreground service won't start).
- **Risk**: noice short-circuits if focus request returns AUDIOFOCUS_REQUEST_FAILED; need to return GRANTED. Mitigation = read noice's source to confirm.

### PF-noice-007 — McD's Application.onCreate brittleness (likely same fix as PF-noice-002) (D4-D5)

- **Scope**: §"Day-2 update" identified that McD's bounded gate has the **same** ArrayStoreException cascade as noice — the difference was that McD's narrow Application.onCreate path tolerates the cascade only when an early dependency is satisfied that noice doesn't satisfy. Confirm PF-noice-002's fix also makes the McD ASE cascade go quiet.
- **Acceptance**:
    1. `Tolerating clinit failure` count in McD bounded artifact = 0.
    2. Sub-gate `pdp_add_cart_gate_status` count goes from FAIL to PASS in some bounded runs (this is a tap-timing thing per §13.10, but elimination of the ASE cascade should make it more deterministic).
- **Effort**: 0-2h (just verification, fix is shared with PF-noice-002).
- **Dependencies**: PF-noice-002.
- **Risk**: McD has additional cascades not seen in noice; budget D5 for those.

### PF-noice-008 — Generic launcher (host) — `WESTLAKE_ART_GENERIC` token (deferred to post-D5)

- **Scope**: Per the original day-1 results doc Headline #2, the host APK is McD-hardcoded at `WestlakeActivity.kt:158, 413, 609`. Add a single `WESTLAKE_ART_GENERIC` branch that reads `package`/`activity`/`displayName` from intent extras. Bump and rebuild the host APK.
- **Acceptance**:
    1. `WESTLAKE_ART_GENERIC` with `package=com.github.ashutoshgngwr.noice activity=...MainActivity` runs successfully.
    2. `WESTLAKE_ART_GENERIC` with a third app (e.g. simple-counter) also runs.
- **Effort**: 4h.
- **Dependencies**: nominal (PF-noice-003 makes any new app reusable).
- **Risk**: low (small Kotlin edit + Gradle rebuild).

### PF-noice-009 — Trace harness as permanent debugging facility (deferred)

- **Scope**: Promote the D1 instrumentation patch from "one-shot debug build" to a permanent env-flag-gated facility (`WESTLAKE_PFCUT_TRACE=1`). Future investigations of similar cascades can flip the flag rather than rebuild.
- **Acceptance**: env flag gates the trace; flag-off has zero overhead (the `g_pfcut_trace_active` atomic check is one-relaxed-load); flag-on emits the trace.
- **Effort**: 1h after PF-noice-002.
- **Dependencies**: PF-noice-001 implementation kept opt-in.
- **Risk**: none.

---

## 3. D1 instrumentation patch — exact file:line edits

### 3.1 Architecture overview

Three call sites need instrumentation to capture every Unsafe-array write between VM start and `MainActivity.onResume` completion:

1. **`Unsafe_compareAndSwapObject` (sun_misc and jdk_internal)** — top of function, BEFORE the `PFCutObjectArrayIndexFromOffset` gate so we observe ALL writes including those that bypass would skip.
2. **`PFCutUnsafeSetObjectArraySlot` (sun_misc and jdk_internal)** — inside the function so we capture the actual write whether it's invoked from putObject / putObjectVolatile / putOrderedObject / compareAndSetReference.
3. **`PFCutUnsafeGetObjectArraySlot`** — instrument reads too, since reads of corrupted slots cascade.

We also need:

4. **An ABASE/ASHIFT verification log line** added once at `runtime.cc:2988`.
5. **A JNI-callable `nativePfcutTraceStop()`** hook registered in `dalvikvm.cc:2074-2092` and triggered from Java when MainActivity onResume completes.
6. **A global atomic flag `g_pfcut_trace_active`** in `runtime.cc` flipped TRUE at runtime start (after the gate-flip log line at line 3227) and flipped FALSE by the JNI hook.
7. **Per-thread cap counter** to keep volume bounded.

The trace writes BOTH stderr (for logcat capture via the existing `WestlakeVM:` prefix path) AND a durable file at `/sdcard/westlake_pfcut_trace.txt` (writable from dalvikvm process under permissive SELinux, will survive logger backpressure).

Why this set of points works:

- The PFCut path is the patched-runtime's array-element-write fast path. Every Unsafe-CAS that hits an `IsObjectArray()` object goes through `PFCutObjectArrayIndexFromOffset` — instrumenting the top of CAS captures both gate-active (post-gate-flip) AND gate-inactive (pre-gate-flip) cases.
- Hooking inside `mirror::Object::CasFieldObject<>` template would be too invasive (the template is in `mirror/object-inl.h` and inlines into hundreds of call sites; we'd lose location info anyway). Instead, instrument at the callers — the Unsafe natives — which is the only reasonable path the boot-time CAS goes through.
- Emit BOTH the source method (from the calling Java frame via `Thread::Current()->FindCurrentMethod(...)`) AND the array/value classes. Source method is the actionable signal; class info confirms the assignability check.

### 3.2 File 1: `/home/dspfac/art-latest/patches/runtime/runtime.cc`

#### 3.2.1 Add trace global + helpers next to the gate (after line 243)

Locate the existing gate definitions. Insert a trace-active flag, a JNI-callable stop, and a verification-log helper.

`old_string`:

```cpp
bool PFCutAppClassLoaderSeen() {
  return g_pfcut_app_loader_seen.load(std::memory_order_relaxed);
}

void PFCutMarkAppClassLoaderSeen() {
  g_pfcut_app_loader_seen.store(true, std::memory_order_release);
}
```

`new_string`:

```cpp
bool PFCutAppClassLoaderSeen() {
  return g_pfcut_app_loader_seen.load(std::memory_order_relaxed);
}

void PFCutMarkAppClassLoaderSeen() {
  g_pfcut_app_loader_seen.store(true, std::memory_order_release);
}

// PF-noice-001 (2026-05-04) per-Unsafe-array-write trace flag. Active from
// runtime startup to the JNI hook nativePfcutTraceStop() called by Java when
// MainActivity.onResume completes. Used to localize the boot-class
// ArrayStoreException cascade without adding permanent runtime overhead.
static std::atomic<bool> g_pfcut_trace_active{false};

bool PFCutTraceActive() {
  return g_pfcut_trace_active.load(std::memory_order_relaxed);
}

void PFCutTraceStart() {
  g_pfcut_trace_active.store(true, std::memory_order_release);
}

void PFCutTraceStop() {
  g_pfcut_trace_active.store(false, std::memory_order_release);
}

// Durable trace sink. Opens the file lazily under a once-init.
static FILE* PFCutTraceFile() {
  static FILE* f = []() -> FILE* {
    FILE* result = fopen("/sdcard/westlake_pfcut_trace.txt", "w");
    if (result == nullptr) {
      result = fopen("/data/local/tmp/westlake_pfcut_trace.txt", "w");
    }
    if (result != nullptr) {
      // Line-buffered so logcat backpressure doesn't lose lines on crash.
      setvbuf(result, nullptr, _IOLBF, 4096);
    }
    return result;
  }();
  return f;
}

void PFCutTraceWrite(const char* line) {
  // Always emit to stderr (logcat) for cheap inspection.
  fputs(line, stderr);
  fputc('\n', stderr);
  // Also emit to durable file.
  FILE* f = PFCutTraceFile();
  if (f != nullptr) {
    fputs(line, f);
    fputc('\n', f);
  }
}
```

#### 3.2.2 Flip the trace flag ON at runtime startup and add ABASE/ASHIFT verification

Locate the existing log line that prints ABASE=N ASHIFT=M. Add a verification-against-stock-aarch64 line right after it.

`old_string`:

```cpp
              fprintf(stderr, "[RT] Set ConcurrentHashMap ABASE=%d ASHIFT=%d\n", abase, ashift);
            }
          }
        }
      }
    }
    if (self->IsExceptionPending()) self->ClearException();
  }
```

`new_string`:

```cpp
              fprintf(stderr, "[RT] Set ConcurrentHashMap ABASE=%d ASHIFT=%d\n", abase, ashift);
              // PF-noice-001 verification: aarch64 dalvikvm with compressed
              // refs (kHeapReferenceSize=4) expects ABASE=12 ASHIFT=2; with
              // uncompressed refs ABASE=16 ASHIFT=3. Mismatch with the build's
              // actual layout = corrupt CHM Node[] addressing.
              const int32_t expected_abase_compressed = 12;
              const int32_t expected_ashift_compressed = 2;
              const int32_t expected_abase_uncompressed = 16;
              const int32_t expected_ashift_uncompressed = 3;
              const bool layout_compressed =
                  sizeof(mirror::HeapReference<mirror::Object>) == 4u;
              const int32_t expect_abase = layout_compressed
                  ? expected_abase_compressed
                  : expected_abase_uncompressed;
              const int32_t expect_ashift = layout_compressed
                  ? expected_ashift_compressed
                  : expected_ashift_uncompressed;
              const bool match = (abase == expect_abase) && (ashift == expect_ashift);
              fprintf(stderr,
                      "[PFCUT-VERIFY] CHM ABASE=%d ASHIFT=%d "
                      "kHeapReferenceSize=%zu compressed=%d expected_abase=%d "
                      "expected_ashift=%d match=%d\n",
                      abase, ashift, sizeof(mirror::HeapReference<mirror::Object>),
                      layout_compressed ? 1 : 0, expect_abase, expect_ashift,
                      match ? 1 : 0);
              fflush(stderr);
            }
          }
        }
      }
    }
    if (self->IsExceptionPending()) self->ClearException();
  }
```

#### 3.2.3 Start the trace flag at the same point as the gate flip (around line 3226)

`old_string`:

```cpp
      PFCutMarkAppClassLoaderSeen();
      fprintf(stderr, "[RT] PFCut boot gate flipped: app loader seen\n"); fflush(stderr);
```

`new_string`:

```cpp
      PFCutMarkAppClassLoaderSeen();
      fprintf(stderr, "[RT] PFCut boot gate flipped: app loader seen\n"); fflush(stderr);
      // PF-noice-001: start tracing every Unsafe-array write from this point
      // until JNI hook nativePfcutTraceStop() fires (called from Java when
      // MainActivity.onResume completes). The trace will localize the
      // boot-class ArrayStoreException cascade root cause.
      PFCutTraceStart();
      fprintf(stderr, "[PFCUT-TRACE] active=1\n"); fflush(stderr);
```

NOTE: the trace WILL miss writes that happen BEFORE the gate flip — but the actual cascade fires AFTER the gate flip (the cascade is observable in `Tolerating clinit failure` lines at logcat timestamps several hundred ms AFTER the gate-flip log line). If D1 trace finds zero offending writes, restart with the trace flag flipped earlier — at the very top of `Runtime::Start()` before any clinit. Add this as a fallback in the next section.

### 3.3 File 2: `/home/dspfac/art-latest/patches/runtime/native/sun_misc_Unsafe.cc`

#### 3.3.1 Forward-declare trace API at top of `namespace art HIDDEN` (after the `extern bool PFCutAppClassLoaderSeen();` declaration around line 125)

`old_string`:

```cpp
// PF-630 boot-aware routing gate (2026-05-04). Defined in runtime.cc. While
// false (i.e. before the app PathClassLoader is installed), PFCut's array-
// backed Unsafe path is bypassed and Unsafe ops fall through to stock
// CasFieldObject/GetFieldObject{Volatile}/SetFieldObject{Volatile}.
extern bool PFCutAppClassLoaderSeen();
```

`new_string`:

```cpp
// PF-630 boot-aware routing gate (2026-05-04). Defined in runtime.cc. While
// false (i.e. before the app PathClassLoader is installed), PFCut's array-
// backed Unsafe path is bypassed and Unsafe ops fall through to stock
// CasFieldObject/GetFieldObject{Volatile}/SetFieldObject{Volatile}.
extern bool PFCutAppClassLoaderSeen();

// PF-noice-001 (2026-05-04) trace API defined in runtime.cc.
extern bool PFCutTraceActive();
extern void PFCutTraceWrite(const char* line);

// Per-thread cap so trace volume stays bounded. The cascade fires within the
// first few hundred Unsafe writes; capping at 200 per thread is plenty.
static constexpr int kPFCutTracePerThreadCap = 200;

// Emit a structured trace line for the given Unsafe-array operation.
//
// `kind` is one of "cas|put|putVol|putOrd|getVol|cas.bypass|put.bypass" —
// the suffix `.bypass` flags writes that took the gate-disabled stock path
// (i.e. PFCutObjectArrayIndexFromOffset returned false). Both routes are
// recorded so we can compare which route the offending write took.
static void PFCutTraceUnsafeArrayWrite(const char* kind,
                                       ObjPtr<mirror::Object> array_obj,
                                       int32_t array_index,
                                       int32_t array_length,
                                       ObjPtr<mirror::Class> array_class,
                                       ObjPtr<mirror::Class> component_class,
                                       ObjPtr<mirror::Object> value,
                                       ObjPtr<mirror::Class> value_class,
                                       bool assignable)
    REQUIRES_SHARED(Locks::mutator_lock_) {
  if (!PFCutTraceActive()) {
    return;
  }
  static thread_local int per_thread_count = 0;
  if (per_thread_count >= kPFCutTracePerThreadCap) {
    return;
  }
  per_thread_count++;

  // Cheap monotonic timestamp (microseconds since arbitrary epoch).
  struct timespec ts;
  clock_gettime(CLOCK_MONOTONIC, &ts);
  uint64_t ts_us = (uint64_t)ts.tv_sec * 1000000ULL + (uint64_t)ts.tv_nsec / 1000ULL;

  // Resolve calling Java method via the current shadow frame.
  // GetCurrentMethod returns the most recent Java frame; for native methods
  // this is the Java caller, which is exactly what we want.
  std::string caller = "<unknown>";
  ArtMethod* current = Thread::Current()->GetCurrentMethod(/*dex_pc=*/ nullptr);
  if (current != nullptr) {
    caller = current->PrettyMethod();
  }

  // Thread name (best-effort; Thread::GetThreadName via std::string buffer).
  std::string thread_name;
  Thread::Current()->GetThreadName(thread_name);

  std::string array_class_name = array_class != nullptr
      ? array_class->PrettyDescriptor() : "<null>";
  std::string component_class_name = component_class != nullptr
      ? component_class->PrettyDescriptor() : "<null>";
  std::string value_class_name = value_class != nullptr
      ? value_class->PrettyDescriptor() : "<null>";

  char buf[1024];
  snprintf(buf, sizeof(buf),
           "[PFCUT-TRACE] thread=%s ts_us=%llu kind=%s "
           "caller=\"%s\" array.class=%s array.componentType=%s "
           "array.length=%d index=%d value.ptr=%p value.class=%s "
           "assignable=%d",
           thread_name.c_str(),
           (unsigned long long) ts_us,
           kind,
           caller.c_str(),
           array_class_name.c_str(),
           component_class_name.c_str(),
           array_length,
           array_index,
           value.Ptr(),
           value_class_name.c_str(),
           assignable ? 1 : 0);
  PFCutTraceWrite(buf);
}
```

#### 3.3.2 Hook the top of `Unsafe_compareAndSwapObject` (line 209) — capture entry-time array+value classes BEFORE any gate decision

`old_string`:

```cpp
static jboolean Unsafe_compareAndSwapObject(JNIEnv* env, jobject, jobject javaObj, jlong offset,
                                            jobject javaExpectedValue, jobject javaNewValue) {
  if (UNLIKELY(PFCutRejectUnsafeJObject("compareAndSwapObject.raw", javaObj, offset))) {
    return JNI_FALSE;
  }
  ScopedFastNativeObjectAccess soa(env);
  ObjPtr<mirror::Object> obj = soa.Decode<mirror::Object>(javaObj);
  if (UNLIKELY(PFCutRejectUnsafeAccess("compareAndSwapObject", obj, offset))) {
    return JNI_FALSE;
  }
  ObjPtr<mirror::Object> expectedValue = soa.Decode<mirror::Object>(javaExpectedValue);
  ObjPtr<mirror::Object> newValue = soa.Decode<mirror::Object>(javaNewValue);
  if (UNLIKELY(PFCutIsBogusUnsafeObject(expectedValue) ||
               PFCutIsBogusUnsafeObject(newValue))) {
    PFCutLogUnsafeBogusObject("compareAndSwapObject.arg", obj, offset,
                              PFCutIsBogusUnsafeObject(newValue) ? newValue : expectedValue);
    return JNI_FALSE;
  }
  int32_t array_index = -1;
  if (PFCutObjectArrayIndexFromOffset(obj, offset, &array_index)) {
```

`new_string`:

```cpp
static jboolean Unsafe_compareAndSwapObject(JNIEnv* env, jobject, jobject javaObj, jlong offset,
                                            jobject javaExpectedValue, jobject javaNewValue) {
  if (UNLIKELY(PFCutRejectUnsafeJObject("compareAndSwapObject.raw", javaObj, offset))) {
    return JNI_FALSE;
  }
  ScopedFastNativeObjectAccess soa(env);
  ObjPtr<mirror::Object> obj = soa.Decode<mirror::Object>(javaObj);
  if (UNLIKELY(PFCutRejectUnsafeAccess("compareAndSwapObject", obj, offset))) {
    return JNI_FALSE;
  }
  ObjPtr<mirror::Object> expectedValue = soa.Decode<mirror::Object>(javaExpectedValue);
  ObjPtr<mirror::Object> newValue = soa.Decode<mirror::Object>(javaNewValue);
  if (UNLIKELY(PFCutIsBogusUnsafeObject(expectedValue) ||
               PFCutIsBogusUnsafeObject(newValue))) {
    PFCutLogUnsafeBogusObject("compareAndSwapObject.arg", obj, offset,
                              PFCutIsBogusUnsafeObject(newValue) ? newValue : expectedValue);
    return JNI_FALSE;
  }
  // PF-noice-001 trace: emit one line for EVERY array-typed CAS regardless of
  // gate state. Skipped cheaply via PFCutTraceActive() if trace is off.
  if (UNLIKELY(PFCutTraceActive() && obj != nullptr && obj->IsObjectArray())) {
    ObjPtr<mirror::ObjectArray<mirror::Object>> array =
        obj->AsObjectArray<mirror::Object>();
    ObjPtr<mirror::Class> array_class = array->GetClass();
    ObjPtr<mirror::Class> component_class =
        array_class != nullptr ? array_class->GetComponentType() : nullptr;
    ObjPtr<mirror::Class> value_class =
        newValue != nullptr ? newValue->GetClass() : nullptr;
    bool assignable = (component_class == nullptr || value_class == nullptr) ||
                      component_class->IsAssignableFrom(value_class.Ptr());
    const int32_t base = mirror::Array::DataOffset(kHeapReferenceSize).Int32Value();
    int32_t idx = (offset >= base && (offset - base) % kHeapReferenceSize == 0)
        ? (int32_t)((offset - base) / kHeapReferenceSize)
        : -1;
    PFCutTraceUnsafeArrayWrite("cas", obj, idx, array->GetLength(),
                               array_class, component_class,
                               newValue, value_class, assignable);
  }
  int32_t array_index = -1;
  if (PFCutObjectArrayIndexFromOffset(obj, offset, &array_index)) {
```

#### 3.3.3 Hook `PFCutUnsafeSetObjectArraySlot` (line 160-165)

`old_string`:

```cpp
static void PFCutUnsafeSetObjectArraySlot(ObjPtr<mirror::Object> obj,
                                          int32_t index,
                                          ObjPtr<mirror::Object> value) {
  obj->AsObjectArray<mirror::Object>()->SetWithoutChecks</*kTransactionActive=*/ false,
                                                /*kCheckTransaction=*/ false>(index, value);
}
```

`new_string`:

```cpp
static void PFCutUnsafeSetObjectArraySlot(ObjPtr<mirror::Object> obj,
                                          int32_t index,
                                          ObjPtr<mirror::Object> value) {
  // PF-noice-001 trace: emit one line BEFORE the bypass-write, with the
  // assignability check that SetWithoutChecks elides. If assignable=0 here,
  // we have THE corrupting write.
  if (UNLIKELY(PFCutTraceActive())) {
    ObjPtr<mirror::ObjectArray<mirror::Object>> array =
        obj->AsObjectArray<mirror::Object>();
    ObjPtr<mirror::Class> array_class = array->GetClass();
    ObjPtr<mirror::Class> component_class =
        array_class != nullptr ? array_class->GetComponentType() : nullptr;
    ObjPtr<mirror::Class> value_class =
        value != nullptr ? value->GetClass() : nullptr;
    bool assignable = (component_class == nullptr || value_class == nullptr) ||
                      component_class->IsAssignableFrom(value_class.Ptr());
    PFCutTraceUnsafeArrayWrite("setSlot", obj, index, array->GetLength(),
                               array_class, component_class,
                               value, value_class, assignable);
  }
  obj->AsObjectArray<mirror::Object>()->SetWithoutChecks</*kTransactionActive=*/ false,
                                                /*kCheckTransaction=*/ false>(index, value);
}
```

#### 3.3.4 Hook `PFCutUnsafeGetObjectArraySlot` (line 155-158) for read-side trace too

`old_string`:

```cpp
static ObjPtr<mirror::Object> PFCutUnsafeGetObjectArraySlot(ObjPtr<mirror::Object> obj,
                                                            int32_t index) {
  return obj->AsObjectArray<mirror::Object>()->GetWithoutChecks(index);
}
```

`new_string`:

```cpp
static ObjPtr<mirror::Object> PFCutUnsafeGetObjectArraySlot(ObjPtr<mirror::Object> obj,
                                                            int32_t index) {
  ObjPtr<mirror::Object> result =
      obj->AsObjectArray<mirror::Object>()->GetWithoutChecks(index);
  // PF-noice-001 trace: emit one line per slot read. Useful for confirming
  // the slot's value class matches what the array's component type permits.
  if (UNLIKELY(PFCutTraceActive())) {
    ObjPtr<mirror::ObjectArray<mirror::Object>> array =
        obj->AsObjectArray<mirror::Object>();
    ObjPtr<mirror::Class> array_class = array->GetClass();
    ObjPtr<mirror::Class> component_class =
        array_class != nullptr ? array_class->GetComponentType() : nullptr;
    ObjPtr<mirror::Class> value_class =
        result != nullptr ? result->GetClass() : nullptr;
    bool assignable = (component_class == nullptr || value_class == nullptr) ||
                      component_class->IsAssignableFrom(value_class.Ptr());
    PFCutTraceUnsafeArrayWrite("getSlot", obj, index, array->GetLength(),
                               array_class, component_class,
                               result, value_class, assignable);
  }
  return result;
}
```

### 3.4 File 3: `/home/dspfac/art-latest/patches/runtime/native/jdk_internal_misc_Unsafe.cc`

Mirror the same changes inside the anonymous namespace. Note that `PFCutTraceUnsafeArrayWrite` cannot be defined twice (same TU vs. inline-static). Define it ONCE in `sun_misc_Unsafe.cc` and reference it from `jdk_internal_misc_Unsafe.cc` via the forward decl. Move the helper to a shared anonymous-namespace-free site (i.e. file scope of `sun_misc_Unsafe.cc`, NOT inside the anon namespace), then forward-declare in `jdk_internal_misc_Unsafe.cc`.

Edit 1 — pull `PFCutTraceUnsafeArrayWrite` out of `sun_misc_Unsafe.cc`'s anonymous namespace by leaving it as a file-scope `static` is not enough (it must be linkable across TUs). So drop the `static` and re-declare as `extern`. Update §3.3.1 accordingly: remove `static` from the function definition AND re-declare it as `extern void PFCutTraceUnsafeArrayWrite(...)` in a shared header OR in both .cc files via `extern` decl.

For simplicity, **declare `PFCutTraceUnsafeArrayWrite` inside `runtime.cc` instead of `sun_misc_Unsafe.cc`**. Move §3.3.1's trace-helper definition into `runtime.cc` next to `PFCutTraceWrite`, declare via `extern` in both Unsafe files.

#### 3.4.1 Move trace helper definition to runtime.cc

**Update §3.2.1** to also include the trace-helper definition. Replace the §3.2.1 `new_string` final paragraph:

`new_string` (replaces §3.2.1 final block):

```cpp
void PFCutTraceWrite(const char* line) {
  // Always emit to stderr (logcat) for cheap inspection.
  fputs(line, stderr);
  fputc('\n', stderr);
  // Also emit to durable file.
  FILE* f = PFCutTraceFile();
  if (f != nullptr) {
    fputs(line, f);
    fputc('\n', f);
  }
}

// Per-thread cap so trace volume stays bounded. The cascade fires within the
// first few hundred Unsafe writes; capping at 200 per thread is plenty.
static constexpr int kPFCutTracePerThreadCap = 200;

void PFCutTraceUnsafeArrayWrite(const char* kind,
                                ObjPtr<mirror::Object> obj,
                                int32_t array_index,
                                int32_t array_length,
                                ObjPtr<mirror::Class> array_class,
                                ObjPtr<mirror::Class> component_class,
                                ObjPtr<mirror::Object> value,
                                ObjPtr<mirror::Class> value_class,
                                bool assignable) {
  if (!PFCutTraceActive()) {
    return;
  }
  static thread_local int per_thread_count = 0;
  if (per_thread_count >= kPFCutTracePerThreadCap) {
    return;
  }
  per_thread_count++;

  struct timespec ts;
  clock_gettime(CLOCK_MONOTONIC, &ts);
  uint64_t ts_us = (uint64_t)ts.tv_sec * 1000000ULL + (uint64_t)ts.tv_nsec / 1000ULL;

  std::string caller = "<unknown>";
  uint32_t dex_pc = 0;
  ArtMethod* current = Thread::Current()->GetCurrentMethod(&dex_pc);
  if (current != nullptr) {
    caller = current->PrettyMethod();
  }

  std::string thread_name;
  Thread::Current()->GetThreadName(thread_name);

  std::string array_class_name = array_class != nullptr
      ? array_class->PrettyDescriptor() : "<null>";
  std::string component_class_name = component_class != nullptr
      ? component_class->PrettyDescriptor() : "<null>";
  std::string value_class_name = value_class != nullptr
      ? value_class->PrettyDescriptor() : "<null>";

  // Suppress unused warnings on builds where ObjPtr is the bare pointer.
  (void)obj;

  char buf[1280];
  snprintf(buf, sizeof(buf),
           "[PFCUT-TRACE] thread=%s ts_us=%llu kind=%s caller=\"%s\" "
           "dex_pc=%u array.class=%s array.componentType=%s array.length=%d "
           "index=%d value.ptr=%p value.class=%s assignable=%d",
           thread_name.c_str(),
           (unsigned long long) ts_us,
           kind,
           caller.c_str(),
           dex_pc,
           array_class_name.c_str(),
           component_class_name.c_str(),
           array_length,
           array_index,
           value.Ptr(),
           value_class_name.c_str(),
           assignable ? 1 : 0);
  PFCutTraceWrite(buf);
}
```

**IMPORTANT**: include must be added at top of `runtime.cc`. After the existing `#include` block, add:

`old_string`:

```cpp
#include "asm_defines.def"
#undef ASM_DEFINE
```

`new_string`:

```cpp
#include "asm_defines.def"
#undef ASM_DEFINE

// PF-noice-001 trace API includes
#include <ctime>
#include "art_method-inl.h"
#include "mirror/class-inl.h"
#include "mirror/object_array-inl.h"
```

#### 3.4.2 In `sun_misc_Unsafe.cc`, replace the §3.3.1 helper definition with an extern declaration

Update §3.3.1 to:

`old_string` (replace §3.3.1 block):

```cpp
extern bool PFCutAppClassLoaderSeen();

// PF-noice-001 (2026-05-04) trace API defined in runtime.cc.
extern bool PFCutTraceActive();
extern void PFCutTraceWrite(const char* line);

// Per-thread cap so trace volume stays bounded. The cascade fires within the
// first few hundred Unsafe writes; capping at 200 per thread is plenty.
static constexpr int kPFCutTracePerThreadCap = 200;
[…rest of helper body…]
```

`new_string` (replacement):

```cpp
extern bool PFCutAppClassLoaderSeen();

// PF-noice-001 (2026-05-04) trace API defined in runtime.cc.
extern bool PFCutTraceActive();
extern void PFCutTraceUnsafeArrayWrite(const char* kind,
                                       ObjPtr<mirror::Object> obj,
                                       int32_t array_index,
                                       int32_t array_length,
                                       ObjPtr<mirror::Class> array_class,
                                       ObjPtr<mirror::Class> component_class,
                                       ObjPtr<mirror::Object> value,
                                       ObjPtr<mirror::Class> value_class,
                                       bool assignable)
    REQUIRES_SHARED(Locks::mutator_lock_);
```

#### 3.4.3 In `jdk_internal_misc_Unsafe.cc`, add the same extern decls AND mirror the same hooks

Edit the file at line 44 to extend the `extern` block:

`old_string`:

```cpp
namespace art HIDDEN {

extern bool PFCutAppClassLoaderSeen();

namespace {
```

`new_string`:

```cpp
namespace art HIDDEN {

extern bool PFCutAppClassLoaderSeen();

// PF-noice-001 (2026-05-04) trace API defined in runtime.cc.
extern bool PFCutTraceActive();
extern void PFCutTraceUnsafeArrayWrite(const char* kind,
                                       ObjPtr<mirror::Object> obj,
                                       int32_t array_index,
                                       int32_t array_length,
                                       ObjPtr<mirror::Class> array_class,
                                       ObjPtr<mirror::Class> component_class,
                                       ObjPtr<mirror::Object> value,
                                       ObjPtr<mirror::Class> value_class,
                                       bool assignable)
    REQUIRES_SHARED(Locks::mutator_lock_);

namespace {
```

Then mirror §3.3.2 (top of `Unsafe_compareAndSetReference` at line 269) and §3.3.3, §3.3.4 (the slot helpers at line 155 and 160) inside the anonymous namespace. Pattern is identical; the only adjustments:

- `Unsafe_compareAndSetReference` — qualify `::art::PFCutTraceActive()`, `::art::PFCutTraceUnsafeArrayWrite()` since the helpers are in the outer `art` namespace.
- The `kind` strings should be `"jdk.cas"`, `"jdk.setSlot"`, `"jdk.getSlot"` to disambiguate the two TUs in the trace.

Edit `Unsafe_compareAndSetReference` at line 269. Use as `old_string` the existing top of that function that reads through `PFCutObjectArrayIndexFromOffset` (lines 269-283), and inject the trace block before line 283 in the same shape as §3.3.2.

Edit `PFCutUnsafeGetObjectArraySlot` at line 155 and `PFCutUnsafeSetObjectArraySlot` at line 160 with the `jdk.getSlot` / `jdk.setSlot` kind strings, qualified as `::art::PFCutTraceUnsafeArrayWrite(...)`.

(Detailed `old_string`/`new_string` blocks omitted here for brevity but follow §3.3.2-§3.3.4 exactly with the namespace qualifier and kind-string prefix changes.)

### 3.5 File 4: `/home/dspfac/art-latest/patches/dalvikvm/dalvikvm.cc`

#### 3.5.1 Register `nativePfcutTraceStop()` JNI hook (line ~2074)

`old_string`:

```cpp
    JNINativeMethod methods[] = {
      {"nativeAllocInstance", "(Ljava/lang/Class;)Ljava/lang/Object;", (void*)+allocInstance},
      {"nativeLog", "(Ljava/lang/String;)V", (void*)+nativeLog},
	      {"nativeCanOpenFile", "(Ljava/lang/String;)Z", (void*)+nativeCanOpenFile},
	      {"nativeReadFileBytes", "(Ljava/lang/String;)[B", (void*)+nativeReadFileBytes},
	      {"nativeAppendFileLine", "(Ljava/lang/String;Ljava/lang/String;)Z",
	          (void*)+nativeAppendFileLine},
	      {"nativeVmProperty", "(Ljava/lang/String;)Ljava/lang/String;", (void*)+nativeVmProperty},
      {"nativeVmArgCount", "()I", (void*)+nativeVmArgCount},
      {"nativeVmArg", "(I)Ljava/lang/String;", (void*)+nativeVmArg},
      {"nativeSystemClassLoader", "()Ljava/lang/ClassLoader;", (void*)+nativeSystemClassLoader},
      {"nativeFindClass", "(Ljava/lang/String;)Ljava/lang/Class;", (void*)+nativeFindClass},
      {"nativePatchClassNoop", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Z",
          (void*)+nativePatchClassNoop},
      {"nativePrimeLaunchConfig", "()V", (void*)+nativePrimeLaunchConfig},
      {"nativeIsCutoffCanaryLaunch", "()Z", (void*)+nativeIsCutoffCanaryLaunch},
      {"nativePrintException", "(Ljava/lang/Throwable;)V", (void*)+printException},
      {"nativeSetApkAssets", "(Ljava/lang/Object;[Ljava/lang/Object;J)V", (void*)+setApkAssetsNative},
    };
```

`new_string`:

```cpp
    // PF-noice-001 (2026-05-04) trace stop hook. Called from Java when
    // MainActivity.onResume completes; flips g_pfcut_trace_active to false
    // so the per-Unsafe-array-write trace stops emitting.
    static auto pfcutTraceStop = +[](JNIEnv* /*e*/, jclass) -> void {
      art::PFCutTraceStop();
      fprintf(stderr, "[PFCUT-TRACE] active=0 (stopped from Java)\n");
      fflush(stderr);
    };
    JNINativeMethod methods[] = {
      {"nativeAllocInstance", "(Ljava/lang/Class;)Ljava/lang/Object;", (void*)+allocInstance},
      {"nativeLog", "(Ljava/lang/String;)V", (void*)+nativeLog},
	      {"nativeCanOpenFile", "(Ljava/lang/String;)Z", (void*)+nativeCanOpenFile},
	      {"nativeReadFileBytes", "(Ljava/lang/String;)[B", (void*)+nativeReadFileBytes},
	      {"nativeAppendFileLine", "(Ljava/lang/String;Ljava/lang/String;)Z",
	          (void*)+nativeAppendFileLine},
	      {"nativeVmProperty", "(Ljava/lang/String;)Ljava/lang/String;", (void*)+nativeVmProperty},
      {"nativeVmArgCount", "()I", (void*)+nativeVmArgCount},
      {"nativeVmArg", "(I)Ljava/lang/String;", (void*)+nativeVmArg},
      {"nativeSystemClassLoader", "()Ljava/lang/ClassLoader;", (void*)+nativeSystemClassLoader},
      {"nativeFindClass", "(Ljava/lang/String;)Ljava/lang/Class;", (void*)+nativeFindClass},
      {"nativePatchClassNoop", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Z",
          (void*)+nativePatchClassNoop},
      {"nativePrimeLaunchConfig", "()V", (void*)+nativePrimeLaunchConfig},
      {"nativeIsCutoffCanaryLaunch", "()Z", (void*)+nativeIsCutoffCanaryLaunch},
      {"nativePrintException", "(Ljava/lang/Throwable;)V", (void*)+printException},
      {"nativeSetApkAssets", "(Ljava/lang/Object;[Ljava/lang/Object;J)V", (void*)+setApkAssetsNative},
      {"nativePfcutTraceStop", "()V", (void*)+pfcutTraceStop},
    };
```

The `RegisterNatives` loop already gracefully skips methods whose Java side is missing (per `[dalvikvm] RegisterNatives skip ... method missing`), so adding the entry doesn't break older shims. Java side is added in §3.6.

Forward-declare `PFCutTraceStop` in `dalvikvm.cc`. Add at the top of the file after the existing extern declarations (search for `extern "C" int JNI_OnLoad_icu` block; mirror its placement):

`old_string` (assumed to be near top of file):

```cpp
// (whatever the existing JNI_OnLoad externs are)
```

For the actual file, place the forward decl inside the `art` namespace if the file uses one, or at file scope:

```cpp
namespace art {
extern void PFCutTraceStop();
}
```

(The implementing agent should locate the appropriate spot after reading dalvikvm.cc lines 1-30 to determine the existing extern style, then add `extern void art::PFCutTraceStop();` just before the `pfcutTraceStop` lambda.)

### 3.6 File 5: `/home/dspfac/android-to-openharmony-migration/shim/java/android/app/MiniActivityManager.java`

#### 3.6.1 Add native method declaration + safe-wrap (top of class, near other natives)

The implementing agent should add (file-level, in the same class as `performResume DONE` log line at MiniActivityManager.java:1887):

```java
private static volatile boolean sPfcutTraceStopUnavailable = false;
private static native void nativePfcutTraceStop();

private static void safePfcutTraceStop() {
    if (sPfcutTraceStopUnavailable) return;
    try {
        nativePfcutTraceStop();
    } catch (Throwable ignored) {
        sPfcutTraceStopUnavailable = true;
    }
}
```

#### 3.6.2 Call the stop hook right after `performResume DONE`

Edit `MiniActivityManager.java:1887` to call `safePfcutTraceStop()` after the existing log line:

`old_string`:

```java
            } else {
                Log.d(TAG, "  performResume DONE for " + resolvedClassName);
            }
        } catch (Throwable e) {
```

`new_string`:

```java
            } else {
                Log.d(TAG, "  performResume DONE for " + resolvedClassName);
                // PF-noice-001 (2026-05-04): stop the per-Unsafe-array-write
                // trace at the same point — onResume done is the gate
                // boundary defined in the D1 contract.
                safePfcutTraceStop();
            }
        } catch (Throwable e) {
```

NOTE: the `nativePfcutTraceStop` declaration must live on `MiniActivityManager` (not `WestlakeLauncher`) because dalvikvm.cc's `RegisterNatives` loop registers methods on `Lcom/westlake/engine/WestlakeLauncher;` per `class_name`. Either (a) move the declaration to `WestlakeLauncher` (recommended, mirrors existing nativeAppendFileLine) and call it from `MiniActivityManager` via `WestlakeLauncher.safePfcutTraceStop()`, or (b) extend `dalvikvm.cc` to register the method on a different class.

**Recommended**: place `safePfcutTraceStop` in `WestlakeLauncher.java` (mirror the existing pattern around line 192 for `safeNativeAppendFileLine` — use the same try/catch + sentinel-flag pattern). The dalvikvm.cc `RegisterNatives` already targets `WestlakeLauncher`. Then call `com.westlake.engine.WestlakeLauncher.safePfcutTraceStop()` from `MiniActivityManager.java:1887`.

### 3.7 Build / sync / run sequence (D1)

```bash
cd /home/dspfac/art-latest
make -f Makefile.ohos-arm64 -j$(nproc) link-runtime
sha256sum build-ohos-arm64/bin/dalvikvm
# Record this hash as PF-noice-001 candidate runtime.

cd /home/dspfac/android-to-openharmony-migration
./scripts/build-shim-dex.sh
sha256sum out/aosp-shim.dex

# Sync to phone with the new runtime + new shim.
DALVIKVM_SRC=/home/dspfac/art-latest/build-ohos-arm64/bin/dalvikvm \
  ./scripts/sync-westlake-phone-runtime.sh

# Pre-clean and run noice gate.
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 \
  shell 'pm clear com.westlake.host'
sleep 2

# Allow phone to write to /sdcard
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 \
  shell 'rm -f /sdcard/westlake_pfcut_trace.txt'

scripts/run-noice-phone-gate.sh
```

After the run, pull the trace:

```bash
mkdir -p artifacts/noice-1day/$(date +%Y%m%d_%H%M%S)_noice_pfcut_trace_d1/
/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -s cfb7c9e3 \
  pull /sdcard/westlake_pfcut_trace.txt \
  artifacts/noice-1day/<timestamp>_noice_pfcut_trace_d1/pfcut_trace.txt
```

### 3.8 Read-the-tape protocol

Open the trace file. Apply these greps in order:

```bash
# 1. Verify trace activated and stopped cleanly
grep -E "PFCUT-TRACE active=" pfcut_trace.txt
# Expect: active=1 line near top, active=0 line near bottom (after performResume DONE)

# 2. Verify ABASE/ASHIFT match
grep -E "PFCUT-VERIFY" pfcut_trace.txt
# Expect: match=1. If match=0, this is the root cause; jump to D2.

# 3. Find the FIRST non-assignable write
grep -nE "assignable=0" pfcut_trace.txt | head -1
# This is the prime suspect. Save the line.

# 4. Get the calling Java method (the `caller=` field). This is the Java-side
#    entrypoint that's writing wrong-typed values into a typed array. The
#    fix in D2 will target this method.

# 5. Cross-check: does the array's component type match the value's class?
#    A line like:
#      array.componentType=java.lang.String value.class=java.util.HashMap$Node assignable=0
#    means a Node is being written into a String[] — BAD.
#    A line like:
#      array.componentType=java.lang.Object value.class=java.lang.String assignable=1
#    is normal (Object[] accepts anything).

# 6. Count by caller — is there ONE method dominating, or many?
grep -oE 'caller="[^"]+"' pfcut_trace.txt | sort | uniq -c | sort -rn | head -20

# 7. Check kind distribution to see whether the bypass-path or stock-path is dominant
grep -oE 'kind=[a-zA-Z.]+' pfcut_trace.txt | sort | uniq -c
```

### 3.9 Branches if D1 trace is empty or inconclusive

| Symptom | Branch |
|---|---|
| `pfcut_trace.txt` is empty (file exists but no lines) | trace flag never flipped — verify `[PFCUT-TRACE] active=1` log line is in logcat. If missing, the `PFCutTraceStart()` call at runtime.cc:3227+ never ran (gate flip itself failed). |
| `pfcut_trace.txt` exists but no `assignable=0` lines | the corruption is happening BEFORE the gate flip. **Branch**: move `PFCutTraceStart()` call earlier — to the very top of `Runtime::Start()` BEFORE InitializeIntrinsics. Tradeoff: the trace will be larger; need to bump per-thread cap to 1000 and use the file-only sink (skip stderr to reduce timing perturbation). |
| `assignable=0` lines exist but caller is `<unknown>` for all | `Thread::Current()->GetCurrentMethod()` returns null because the call stack is fully native. **Branch**: walk the stack via `StackVisitor` to find the topmost Java frame; or correlate timestamps to last `[PFCUT] InterpreterJni` log line. |
| `assignable=0` lines all from one method — clean win | proceed to D2. |
| `assignable=0` lines from many methods | **Branch**: D2 needs multiple fixes; budget D2-D3 for each, prioritize by frequency. |
| `match=0` for ABASE/ASHIFT | **Branch**: this IS the root cause; D2 just hardcodes the right values per build layout and verifies. |

### 3.10 Why this approach over alternatives

- **Hooking `mirror::Object::CasFieldObject<>` template directly** would catch every CAS in the runtime (not just Unsafe-routed). Rejected because: (a) the template is in `mirror/object-inl.h` and inlines into hundreds of call sites; instrumenting there adds overhead to all heap operations; (b) the typed-array bypass we suspect ONLY fires from the Unsafe path, so instrumenting there is more targeted.
- **Static analysis of the patched runtime** to spot the bypass path. Rejected because §13.7-§13.12 already did extensive static analysis and missed the actual cause; empirical trace is the only way past inference.
- **DEX-level instrumentation** (replacing `Unsafe.compareAndSwapObject` with a wrapper that logs). Rejected because the cascade hits boot-class clinit for ICUBinary, which runs BEFORE app classes load. We need runtime-level hooks.

---

## 4. D2-D5 validation probes per day

### 4.1 D2 — Fix root cause

Once D1 trace identifies the offending caller, choose one of:

- **Option A — Java-side preload (cheapest)**: in `WestlakeLauncher.java`, BEFORE `prepareCoroutineRuntimeForWestlake` at line 3943, force-load the offending class. e.g. if D1 says the bad write is into `String[]`, add `Class.forName("[Ljava.lang.String;").getDeclaredFields();` to pin the array class metadata. This is non-invasive.
- **Option B — Defer reflection-driven seed**: if D1 says the bad write is from `seedCoroutine*` reflection, move that call to AFTER `MainActivity.onResume` completes. This is also non-invasive but may break apps that rely on coroutines being seeded earlier.
- **Option C — Runtime patch**: if D1 says the bad write is via a specific PFCut path, narrow that path. NOT widening — narrowing.

Validation:

```bash
# 1. Same noice gate, with trace flag.
WESTLAKE_PFCUT_TRACE=1 scripts/run-noice-phone-gate.sh
# 2. Pull trace.
# 3. grep -nE "assignable=0" pfcut_trace.txt
#    Expect: zero matches.
# 4. Verify cascade gone:
grep -nE "Tolerating clinit failure for L(android/icu|java/nio/charset|sun/security/jca|sun/util/locale|java/util/Date|sun/net/spi/DefaultProxySelector|android/os/Build)" \
  artifacts/noice-1day/<run>/logcat-dump.txt
#    Expect: zero matches.
```

What to do if D2 fails:
- If `assignable=0` lines still appear with the SAME caller as D1, the patch missed the path. Re-read trace; consider Option C (runtime patch).
- If `assignable=0` lines now appear with a DIFFERENT caller, D1 found one but not all. Iterate: apply Option A/B for the new caller too.

### 4.2 D3 — Noice MainActivity paints view tree

```bash
scripts/run-noice-phone-gate.sh

# Acceptance greps:
grep -E "NOICE_VIEW_INFLATED" artifacts/noice-1day/<run>/check-noice-proof.txt
# count >= 5

grep -E "performResume DONE for com.github.ashutoshgngwr.noice" \
  artifacts/noice-1day/<run>/logcat-dump.txt
# at least 1 occurrence

# Visual check: open screen.png. Should NOT be the host Compose home; should
# show noice header / nav.

# Compute screenshot SHA delta from baseline:
sha256sum artifacts/noice-1day/<run>/screen.png \
  artifacts/noice-1day/20260503_232600_noice_noice_first_boot_v2/screen.png
# These should differ.
```

What to do if D3 fails:
- If `setContentView` STILL throws ASE: D2 didn't actually fix the cascade — return to D2.
- If `setContentView` succeeds but no Views inflate: this is a Hilt/lifecycle blocker (see Day-2-update §"Remaining noice paint blocker"). Try:
  - Manually inject `subscriptionBillingProvider` in `MiniActivityManager.tryRecoverContent` — instantiate a no-op stub.
  - Reach out via the existing `noteMarker("CV Hilt skip injectMembers")` pattern.
- If `setId` NPE persists: View-class statics still poisoned — D2 missed the View clinit cascade. Add `Class.forName("android.view.View").getDeclaredFields();` to the WestlakeLauncher preload list.

### 4.3 D4 — McD bounded gate doesn't regress + 5-min noice soak

```bash
# Run McD bounded gate.
scripts/run-real-mcd-phone-gate.sh

# Acceptance:
grep -E "gate_status" artifacts/real-mcd/<latest>/check-real-mcd-proof.txt
# Expect: gate_status=PASS

grep -cE "signal 7|Fatal signal" artifacts/real-mcd/<latest>/logcat-dump.txt
# Expect: 0

# Run noice 5-min soak.
NOICE_SOAK_SECONDS=300 NOICE_GATE_TAPS=center scripts/run-noice-phone-gate.sh

grep -E "noice_5min_soak_status" artifacts/noice-1day/<run>/check-noice-proof.txt
# Expect: PASS

grep -cE "signal 7|Fatal signal" artifacts/noice-1day/<run>/logcat-dump.txt
# Expect: 0
```

What to do if D4 McD regresses:
- Diff `Tolerating clinit failure` counts; if the D2 fix tightened a path McD relied on, revert the fix and consider an option-A-only approach (more conservative).

What to do if D4 noice soak fails:
- New SIGBUS at minute N: capture the full logcat; this is a separate workstream PF-noice-010+, may need a 7-day-soak follow-on.

### 4.4 D5 — Audio gap stub + wrap-up

```bash
# Build ohbridge_stub.so with new audio stubs.
cd /home/dspfac/art-latest/stubs
make -f Makefile.ohos-arm64

# Sync.
adb push libohbridge_stub.so /data/local/tmp/westlake/

# Re-run noice gate; check for foreground service start.
scripts/run-noice-phone-gate.sh

grep -E "MediaPlaybackService" artifacts/noice-1day/<run>/logcat-dump.txt
# Expect: service started, no immediate exception.

grep -E "AudioFocus.*REQUEST_GRANTED" artifacts/noice-1day/<run>/logcat-dump.txt
# Expect: at least 1
```

Wrap-up doc: `docs/program/WESTLAKE_PF_NOICE_DAY5_RESULTS_<actual-date>.md`. Acceptance per-criterion table.

---

## 5. Risk register

Beyond the original 1-day-contract risks (§7 of contract), this 5-day cycle has these additional risks:

| Risk | Likelihood | Severity | Mitigation |
|---|---|---|---|
| **Instrumentation overhead changes timing → cascade doesn't repro** | MED | HIGH | Use file-only trace sink (skip stderr fflush per-line); bump per-thread cap; if needed, run trace-off baseline first to establish timing, then trace-on to compare. |
| **Hypothesis isolation: multiple writes corrupt different arrays; no single fix** | MED | MED | D2 budget allows 2-3 sub-fixes; if N>3, escalate to extended D2-D3 and reduce D5 scope. |
| **Boot-time logcat lost (logger backpressure)** | HIGH | LOW | Trace-to-file is durable (`/sdcard/westlake_pfcut_trace.txt`); logcat is supplementary. |
| **Patched runtime size grows → slower link** | LOW | LOW | New code is ~150 lines; link-time impact ≪ 1s. |
| **`nativePfcutTraceStop()` JNI registration fails (method-not-found)** | LOW | MED | RegisterNatives loop already gracefully skips missing methods; `safePfcutTraceStop()` swallows. Trace-cap (200 per thread) prevents runaway even if stop signal is missed. |
| **`Thread::GetCurrentMethod()` returns null in heavily-native call paths** | MED | MED | Pre-D1 mitigation: if `<unknown>` dominates the trace, add a stack-walk fallback via `StackVisitor`. |
| **`/sdcard/westlake_pfcut_trace.txt` write fails (permissions)** | LOW | MED | Fallback to `/data/local/tmp/westlake_pfcut_trace.txt` is built into `PFCutTraceFile()`. |
| **Per-thread cap (200) is too low** | LOW | LOW | Bump to 1000 if first run shows truncation just before the cascade fires. |
| **`PFCutPf625EntryLooksInvalid` widening at `interpreter_common.cc:107` re-flags the new fast path** | LOW | MED | Trace points are inside the patched fast path itself; not affected by upstream stale-entry flag. |
| **D2 fix for noice regresses McD beyond bounded** | LOW | HIGH | Run real-mcd bounded BEFORE soak; if regression, revert atomically; D2 has 12h budget, McD-bounded check eats only 1h of it. |
| **PFCut-trace itself triggers an ASE during its `obj->GetClass()->PrettyDescriptor()` call** | LOW | MED | All trace-helper calls are wrapped under `REQUIRES_SHARED(Locks::mutator_lock_)` and use `ObjPtr` — read-only operations on already-loaded classes. The trace fires on the Unsafe path, so the array's class is already in a usable state. |
| **`PFCutMarkAppClassLoaderSeen()` / `PFCutTraceStart()` race** | LOW | LOW | Both use std::atomic with release ordering; the trace flag is set AFTER the gate flip via the same code path. |
| **Phone state drift between D1 and D5 (5 sequential days)** | MED | MED | Each day starts with `pm clear com.westlake.host` + `pm clear` of target app + reboot if checker shows variance. |
| **Cascade caller is a `<clinit>` method that itself fails to print stack** | MED | MED | `PrettyMethod()` returns the method's name even if the class is in an error state. If the trace shows `caller="<clinit>"` for some class X, X is the corrupting actor. |

---

## 6. Sign-off / handoff

### 6.1 Implementing-agent prompt (D1)

```
Goal: implement the D1 instrumentation patch per
docs/program/WESTLAKE_PF_NOICE_PLAN_20260504.md §3 and run one noice gate
to localize the boot-class ArrayStoreException cascade root cause.

Constraints:
- Read-only EXCEPT runtime + shim source edits per §3.
- Build via `make -f Makefile.ohos-arm64 -j$(nproc) link-runtime`.
- Run noice gate via `scripts/run-noice-phone-gate.sh`.
- Pull trace via `adb pull /sdcard/westlake_pfcut_trace.txt`.
- DO NOT push commits; supervisor batches.

Concrete steps:
1. Apply §3.2 edits to /home/dspfac/art-latest/patches/runtime/runtime.cc
2. Apply §3.3 edits to /home/dspfac/art-latest/patches/runtime/native/sun_misc_Unsafe.cc
3. Apply §3.4 edits to /home/dspfac/art-latest/patches/runtime/native/jdk_internal_misc_Unsafe.cc
4. Apply §3.5 edits to /home/dspfac/art-latest/patches/dalvikvm/dalvikvm.cc
5. Apply §3.6 edits to /home/dspfac/android-to-openharmony-migration/shim/java/android/app/MiniActivityManager.java
   AND /home/dspfac/android-to-openharmony-migration/shim/java/com/westlake/engine/WestlakeLauncher.java
   (the safePfcutTraceStop() helper)
6. cd /home/dspfac/art-latest && make -f Makefile.ohos-arm64 -j$(nproc) link-runtime
7. cd /home/dspfac/android-to-openharmony-migration && ./scripts/build-shim-dex.sh
8. DALVIKVM_SRC=/home/dspfac/art-latest/build-ohos-arm64/bin/dalvikvm \
     ./scripts/sync-westlake-phone-runtime.sh
9. Pre-clean phone state.
10. Run noice gate.
11. Pull trace.
12. Apply §3.8 read-the-tape protocol; surface the offending caller(s) in your final report.
13. Write a short artifact note at
    artifacts/noice-1day/<timestamp>_noice_pfcut_trace_d1/_TRACE_NOTES.txt
    summarizing the top 3 callers by count and the first assignable=0 line.
14. Surface the candidate D2 fix option (A/B/C) with rationale.

Final report (under 400 words):
- New runtime hash
- New shim hash
- Number of trace lines captured
- Top 3 callers with assignable=0
- First assignable=0 line in full
- ABASE/ASHIFT match status
- D2 fix recommendation
```

### 6.2 Implementing-agent prompt (D2)

```
Goal: apply the D2 fix per
docs/program/WESTLAKE_PF_NOICE_PLAN_20260504.md §4.1 + the D1 trace findings.

Steps:
1. Read the D1 _TRACE_NOTES.txt and pick option A/B/C per §4.1.
2. Apply the fix; build; sync.
3. Re-run noice gate; pull trace; verify zero assignable=0 lines.
4. Verify Tolerating clinit failure count == 0 for the cascaded classes.
5. If non-zero, iterate (the fix missed a path).

Final report: the chosen option, the fix diff, the new trace summary, and
the cascade-class status.
```

### 6.3 Acceptance gates summary table

| Day | Gate | Pass condition | Fail branch |
|---|---|---|---|
| **D1** | trace captured, root cause identified | ≥1 `assignable=0` line with named caller; ABASE/ASHIFT match=1 | move trace earlier (§3.9 row 2); add stack walker (§3.9 row 3) |
| **D2** | cascade eliminated | zero `assignable=0`; zero Tolerating-clinit for ICU/Charset/Provider/etc. | iterate; if McD regresses, revert and try option A only |
| **D3** | noice UI paints | ≥5 inflated views; screenshot != host home | preload View class; or fallback to MiniActivityManager.tryRecoverContent |
| **D4** | McD doesn't regress + soak passes | bounded PASS; 5-min no SIGBUS | revert D2; design narrower fix |
| **D5** | audio stub lands + wrap-up | foreground service starts; results doc lands | reduce scope to "noice soaks; audio is multi-week per gap doc" |

### 6.4 Phone-state checkpoint at D5 close

- Runtime: PF-noice-002 candidate hash (from D2 build)
- Shim: PF-noice-001/002 candidate hash (from D2/D3 build)
- Backup retained: pre-D1 snapshots at `/home/dspfac/westlake-runtime-backups/`
- Noice + Westlake host installed and `pm clear`'d
- McD bounded gate verified via D4

### 6.5 Open follow-ons after D5

- PF-noice-008: generic launcher token (post-D5)
- PF-noice-009: trace harness as permanent debug facility (post-D5)
- PF-noice-010: any new SIGBUS surfaced in D4 soak

---

## 7. References

- `/home/dspfac/art-latest/patches/runtime/runtime.cc` (gate flip @3226; ABASE/ASHIFT @2988; trace flag insert points @243, @2988-after, @3227)
- `/home/dspfac/art-latest/patches/runtime/native/sun_misc_Unsafe.cc` (CAS @209; PFCutObjectArrayIndexFromOffset @127; slot helpers @155, @160; bypass call sites @228, @411, @438, @470, @494, @521)
- `/home/dspfac/art-latest/patches/runtime/native/jdk_internal_misc_Unsafe.cc` (CAS @269; helpers @127, @155, @160; bypass call sites @284, @462, @488, @518, @540)
- `/home/dspfac/art-latest/patches/dalvikvm/dalvikvm.cc` (RegisterNatives @2074-2092; CHM patcher @180-307)
- `/home/dspfac/art-latest/patches/runtime/interpreter/interpreter_common.cc` (PFCutPf625 widening @107-116; bypass-routing reaction sites @9481, @10840, @11199)
- `/home/dspfac/art-latest/patches/runtime/class_linker.cc` (Tolerating-clinit hook @6687)
- `/home/dspfac/android-to-openharmony-migration/shim/java/com/westlake/engine/WestlakeLauncher.java` (Application bootstrap @3939; coroutine seed call @3943)
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/app/WestlakeActivityThread.java` (`prepareCoroutineRuntimeForWestlake` @1530; `seedCoroutineMainDispatcher` @1554)
- `/home/dspfac/android-to-openharmony-migration/shim/java/android/app/MiniActivityManager.java` (performResume DONE @1887; tryRecoverContent path @2280-end)

### 7.1 Key empirical artifacts to consult

- `artifacts/noice-1day/20260504_155531_noice_noice_pf630_boot_gate_v1/logcat-dump.txt` (cascade with gate active)
- `artifacts/noice-1day/20260504_213742_noice_noice_recovery_gate_v2/logcat-dump.txt` (recovery branch fires; setContentView ASE)
- `artifacts/noice-1day/20260504_214134_noice_noice_recovery_gate_v3_programmatic/logcat-dump.txt` (View.setId NPE; View statics poisoned)
- `artifacts/noice-1day/20260504_214514_noice_noice_bypass_off_v1/logcat-dump.txt` (bypass disable does NOT help; cascade survives)

---

## 8. Sign-off

This plan is authored read-only by a design agent (no phone, no runtime build, no commits beyond this single doc). The next agent must validate the patch lines against the current source state at start-of-D1 (the file line numbers may shift slightly if anyone else touches the source between this plan and the implementation).

**Out of scope for this plan**: any change to McD-specific path (PF-630 Realm emulation), audio bridge implementation beyond stubs, OHOS port, host APK genericization (deferred to PF-noice-008).

**Approval**: pending supervisor sign-off before next agent begins D1.
