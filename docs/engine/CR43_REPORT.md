# CR43 — `WestlakeRuntime.areViewsReady()` readiness signal

**Status:** done
**Date:** 2026-05-13
**Owner:** Builder (this work)
**Budget:** 30-45 min — actual ~25 min
**Parallel work:** M7-Step2 (active on WestlakeActivityThread / scripts), CR35 (M6 docs), CR42 already shipped its read-only audit

---

## §1. Goal (per CR42 §6 / CR-D)

CR36 (`docs/engine/CR36_FINDINGS.md`) established that standalone dalvikvm
cannot construct **any** framework `android.view.View` subclass through
normal ctor paths until M6's surface daemon (or the M7-Step2 host APK)
loads `libhwui.so` + `libandroid_runtime.so` into the dalvikvm process.
The `View(Context)` ctor unconditionally calls `RenderNode.create()`
whose `nCreate` JNI is unresolved pre-M6 — the ctor explodes with
`UnsatisfiedLinkError`.

CR36 fixed the symptom at one site (`Window.<init>` deferred its
eager `mDecorView = new FrameLayout(context)` to `mDecorView = null`,
with every consumer in `Window.java` already null-tolerant).  But
"null forever" is only correct *pre-M6* — once libhwui is in the
process, the lazy-init pattern wants to flip from "always null" to
"construct the real View on demand".

CR42 (`docs/engine/CR42_VIEW_CTOR_AUDIT.md`) catalogued 30 live
View-ctor sites + 23 live View-extending classes in `shim/java/` and
recommended (§6 CR-D) a single global readiness flag so every
CR36-style lazy-init can become transparently M6-aware **without
touching each site individually**.

CR43 stands up that flag.  No consumer is wired this CR; no producer
is wired this CR.  M7-Step2 (host APK production launch — APK already
has libhwui in-process via zygote) or M6-Step6 (surface daemon dlopen
of libhwui into dalvikvm) will flip the flag when natural for that
milestone; the CR36 lazy-init in `Window.java` will start consulting
the flag when its lazy-construct branch lands.  Standing up the flag
in its own CR keeps that future wiring a one-line check rather than a
coupled multi-file change.

---

## §2. What landed

### §2.1 New file (NEW, 148 LOC)

`shim/java/com/westlake/services/WestlakeRuntime.java` — utility class
with three entries:

```java
public final class WestlakeRuntime {
    private static volatile boolean sViewsReady = false;
    public static boolean areViewsReady() { return sViewsReady; }
    public static void markViewsReady() { sViewsReady = true; }
    private WestlakeRuntime() {}
}
```

Plus ~110 LOC of class/method javadoc + leading comment block explaining
the CR36 -> CR42 -> CR43 lineage and the consumer/producer usage
patterns.

**Package choice — `com.westlake.services` (not `com.westlake.engine`):**
the CR42 sketch (§6) named the package `com.westlake.engine`, but the
CR43 brief explicitly specified `com.westlake.services` and that is
where every existing Westlake utility class lives
(`ServiceRegistrar.java`, `ServiceMethodMissing.java`,
`SystemServiceWrapperRegistry.java`, `ColdBootstrap.java`,
`ResourceArscParser.java`, all of `Westlake*Service.java`).  Following
established convention; CR42 was a read-only sketch and its package
suggestion was illustrative, not binding.

### §2.2 No other source edits

Per CR43 brief explicit guidance:

- `shim/java/android/view/Window.java` — NOT touched.  M7-Step2 /
  M6-Step6 will wire the consumer when natural.
- `shim/java/android/app/Activity.java` — NOT touched.  M7-Step2 may.
- `shim/java/android/app/WestlakeActivityThread.java` — NOT touched.
  M7-Step2 is active there.
- `shim/java/com/westlake/services/*` other files — NOT touched
  (CR22 frozen surface).
- `aosp-libbinder-port/*`, daemons, `art-latest/` — NOT touched.
- Memory files — NOT touched.

### §2.3 Build artifact

- `aosp-shim.dex` rebuilt: 1,452,580 -> 1,453,180 bytes (+600 B /
  +0.04%).  Consistent with one tiny utility class (one `<clinit>`,
  one `<init>` private, two short static methods, one volatile field).
- DEX symbol verification: `strings aosp-shim.dex | grep WestlakeRuntime`
  prints `Lcom/westlake/services/WestlakeRuntime;` + `WestlakeRuntime.java`
  + `areViewsReady` + `markViewsReady` — all four present.
- Build clean (only pre-existing kotlin/javac warnings carry through
  — none introduced by CR43).

### §2.4 Doc updates

- `docs/engine/CR43_REPORT.md` (this file, NEW)
- `docs/engine/PHASE_1_STATUS.md` — one row added at the top of
  §1.3 (CR43)

---

## §3. Consumer / producer roadmap (none wired in CR43)

### §3.1 Future consumer site (illustrative)

Inside `shim/java/android/view/Window.java` once CR36's
`mDecorView = null` deferral wants to opportunistically construct a
real FrameLayout when libhwui is online:

```java
// Sketch -- NOT in this CR.
public View getDecorView() {
    if (mDecorView == null && WestlakeRuntime.areViewsReady()) {
        try {
            mDecorView = new FrameLayout(getContext());
        } catch (Throwable t) {
            // Belt-and-suspenders if probe was wrong; stays null.
            mDecorView = null;
        }
    }
    return mDecorView;
}
```

Pre-M6 the predicate is `false`, the branch is dead, the existing CR36
behavior is preserved verbatim.  Post-M6 the predicate flips and the
existing null-tolerant call sites pick up a real FrameLayout the first
time they ask.

### §3.2 Future producer site (illustrative)

Inside the M6 surface-daemon initializer (or its host-side bootstrap):

```java
// Sketch -- NOT in this CR.
System.loadLibrary("hwui");
System.loadLibrary("android_runtime");
// Optionally probe RenderNode.nCreate via reflective dry-run.
WestlakeRuntime.markViewsReady();
```

Or for M7-Step2's host APK launcher (which already has libhwui via
the normal Android zygote process):

```java
// Sketch -- NOT in this CR (M7-Step2 ground).
WestlakeRuntime.markViewsReady();   // unconditional in a real Android process
```

---

## §4. Anti-drift self-audit (per memory/feedback_macro_shim_contract.md)

| Gate | Result |
|---|---|
| Only NEW file + rebuild + doc | PASS — exactly one new `.java` file, one rebuilt dex, one new `.md`, one row in `PHASE_1_STATUS.md` |
| Zero new `Unsafe` calls | PASS (utility class with one `volatile boolean`) |
| Zero new `setAccessible` calls | PASS |
| Zero per-app branches | PASS (architectural readiness signal, app-agnostic) |
| Zero edits to frozen files | PASS — `Window.java`, `Activity.java`, `WestlakeActivityThread.java`, sibling `Westlake*Service.java` all untouched |
| Zero edits to active parallel work | PASS — M7-Step2 + CR35 + memory files all untouched |
| Frozen-surface forward compatibility | PASS — API is append-only (additional `areXxxReady()` flags MAY be added; existing three entries MUST NOT change shape) |

---

## §5. Files touched

| File | Status | Delta |
|---|---|---|
| `shim/java/com/westlake/services/WestlakeRuntime.java` | NEW | +148 LOC |
| `aosp-shim.dex` (+`ohos-deploy/aosp-shim.dex` + `westlake-host-gradle/app/src/main/assets/aosp-shim.dex`) | REBUILT | +600 B |
| `docs/engine/CR43_REPORT.md` (this file) | NEW | ~170 LOC |
| `docs/engine/PHASE_1_STATUS.md` | edit | +1 row in §1.3 |

---

## §6. Person-time

~25 min — well inside the 30-45 min budget.

Breakdown:
- ~5 min: read CR42 §6 / CR36 / existing `com/westlake/services/` files
- ~10 min: write `WestlakeRuntime.java` (heavy on docstring per CR42's
  pattern that future readers will grep this class first)
- ~3 min: run `scripts/build-shim-dex.sh`, verify DEX, verify symbols
- ~7 min: write this report + `PHASE_1_STATUS.md` row

---

## §7. Cross-references

- `docs/engine/CR42_VIEW_CTOR_AUDIT.md` §6 (CR-D recommendation; this CR's
  authoritative spec)
- `docs/engine/CR36_FINDINGS.md` (root-cause for the lazy-init pattern
  that this flag will eventually drive)
- `docs/engine/PHASE_1_STATUS.md` §1.3 (CR43 row)
- `shim/java/android/view/Window.java` (future consumer; CR36 deferred
  ctor lives here)
- `aosp-libbinder-port/test/CR42_VIEW_CTOR_AUDIT.md` cross-reference
  (CR42's 30-row live-site catalogue informs which other sites may
  eventually consult `areViewsReady()`)
