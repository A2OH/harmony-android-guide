# CR62 E13 noice Checkpoint — original Stage C NPE RESOLVED, new downstream blocker (2026-05-15)

## Status vs CR62 success criteria

| Stage | Status | Evidence |
|---|---|---|
| A. noice dex loaded on BCP, MainActivity reachable | **PASS** (carried from CR60-e13) | `stage A: dex visible class=com.github.ashutoshgngwr.noice.activity.MainActivity` |
| B. NoiceApplication.onCreate completed | **PASS** | `stage B: Application.onCreate returned (com.github.ashutoshgngwr.noice.NoiceApplication)` |
| C. **Original NPE blocker RESOLVED**; new downstream `NoSuchMethodError` blocks | **PARTIAL** | NPE at `e.v.c:22` (AppCompatDelegate.attachBaseContext2) NO LONGER occurs in substrate path. New blocker: `NoSuchMethodError at v:44` ← `attachBaseContext:0x1fc` ← `<init>` super-chain, after LocaleList instantiation. |
| D. Pixel from noice on panel | not reached | gated on Stage C |

## What CR62 fixed

### Step 1 — Strict exception propagation toggle
`WestlakeInstrumentation.setStrictExceptionPropagation(boolean)` flips
`onException` from "swallow + return true" (AOSP default) to "propagate +
return false". This surfaces ctor-time AppCompat/Hilt failures that the
V2 substrate would otherwise silently drop. Default OFF preserves the
resilient behavior; the inproc-app-launcher flips it ON in apk-mode.

### Step 2 — Thread-local pre-attached Context (Option α)
The root cause of the original NPE: `new MainActivity()` → super-chain
→ `ContextWrapper(null)` → virtual `attachBaseContext(null)` →
AppCompatActivity's override → `AppCompatDelegate.attachBaseContext2(null)`
NPEs because the override runs DURING construction when the substrate
hasn't yet had a chance to call `Activity.attach(context, …)`.

Fix:
- `WestlakeInstrumentation.publishContextForNewActivity(Context)`
  stashes a Context in a thread-local;
- `ContextWrapper(Context)` ctor consults that thread-local when its
  argument is null and substitutes the real one;
- `WestlakeActivityThread.performLaunchActivityImpl` publishes the
  freshly-built `baseContext` to the thread-local right before
  `mInstrumentation.newActivity(...)` and clears it after.

Net effect: AppCompatDelegate sees a non-null Context and the original
NPE is gone. Verified in DAYU200 dalvikvm.stdout — the NPE pattern only
appears on the `step 3-pre` probe path (a direct `Class.newInstance()`
that intentionally bypasses the substrate's pre-attach to keep the
diagnostic surface tight).

## New downstream blocker (out-of-scope for CR62)

```
Handling exception Ljava/lang/NoSuchMethodError; at v:44
  ↑
Handling exception Ljava/lang/NoSuchMethodError; at attachBaseContext:0x1fc
  ↑
... <init> super-chain ...
  ↑
Handling exception Ljava/lang/InstantiationException; at instantiateActivity:98
  ↑
Handling exception Ljava/lang/InstantiationException; at newActivity:1174
  ↑
Handling exception Ljava/lang/RuntimeException; at performLaunchActivityImpl:1514
```

Class load sequence just before the error (from dalvikvm.stdout):
```
Configuration → WestlakeResources → Resources → DisplayMetrics
  → Math → LocaleList → [Ljava/util/Locale; → java.lang.reflect.Array
  → NoSuchMethodError
```

Reading noice's APK with dexdump shows multiple LocaleList method
signatures: `(Configuration)LocaleList`, `(String)LocaleList`,
`(LocaleList)String`, `(LocaleList,I)Locale`, `(Configuration,LocaleList)V`,
`(LocaleList)V`, `(EditText,LocaleList)V`. Our shim's LocaleList exposes
`get(int)`, `size()`, `isEmpty()`, `indexOf(Locale)`, `toLanguageTags()`,
`toString()`, `getDefault()`, `getAdjustedDefault()`, `getEmptyLocaleList()`,
`forLanguageTags(String)`. CR62 added: `setDefault(LocaleList)`,
`setDefault(LocaleList, int)`, `matchesLanguageAndScript(Locale)`,
`getFirstMatch(String[])`, `equals(Object)`, `hashCode()`. The
NoSuchMethodError persists with these additions in place, so the
missing API is on a different class (Configuration? Resources?
DisplayMetrics?) — not LocaleList itself.

Triaging which exact method is missing requires deminifying
`Lv;.<some_method>` from noice's classes.dex — a separate work
item that exceeds CR62's spike budget per the 4-hour rule.

## Regressions verified PASS

| Test | Result |
|---|---|
| MVP-0 hello (arm32) | PASS |
| MVP-1 trivial-activity (arm32) | PASS |
| E12 smoke (hello-color-apk → BLUE pixel) (arm32) | PASS |

## Files changed

- `shim/java/android/app/WestlakeInstrumentation.java`
  - +`setStrictExceptionPropagation(boolean)` / `isStrictExceptionPropagation()`
  - +`publishContextForNewActivity(Context)` / `consumePendingBaseContext()` / `clearPendingBaseContext()` + thread-local
  - onException: propagate when strict-mode on (else legacy swallow)
- `shim/java/android/content/ContextWrapper.java`
  - ctor: when arg is null, consult WestlakeInstrumentation thread-local
- `shim/java/android/app/WestlakeActivityThread.java`
  - performLaunchActivityImpl step 4: publish baseContext to thread-local before newActivity; clear after
- `shim/java/android/os/LocaleList.java`
  - +`setDefault(LocaleList)`, +`setDefault(LocaleList, int)`,
    +`matchesLanguageAndScript(Locale)`, +`getFirstMatch(String[])`,
    +`equals(Object)`, +`hashCode()`
- `ohos-tests-gradle/inproc-app-launcher/src/main/java/com/westlake/ohostests/inproc/InProcessAppLauncher.java`
  - apk-mode step 3-strict: enable WestlakeInstrumentation strict-mode early
  - apk-mode step 3d/3e: probe Application state INDEPENDENT of launchActivity success → Stage B captured even when Stage C fails
- `scripts/run-ohos-test.sh`
  - cmd_inproc_app: refresh aosp-shim-ohos.dex on board when local was rebuilt

## Self-audit gate

- [x] No `Unsafe.allocateInstance` in new Java code
- [x] No `setAccessible(true)` in new Java code
- [x] No per-app branches (launcher remains generic; CR62 logic keys on
       boolean toggles, never on noice/mcd/etc.)
- [x] `WestlakeContextImpl` untouched (frozen surface)
- [x] All new methods on classes we own:
  - WestlakeInstrumentation (we own)
  - ContextWrapper (framework-duplicate, we own)
  - WestlakeActivityThread (we own)
  - LocaleList (framework-duplicate, we own)
  - InProcessAppLauncher (we own)
- [x] All E1-E12 + E13 stage A/B/C regression matrix:
  - MVP-0 hello PASS
  - MVP-1 trivial-activity PASS
  - E12 smoke (hello-color-apk → BLUE) PASS
  - E13 noice stage A PASS, stage B PASS
- [x] Stage C honest reporting: original blocker (AppCompatDelegate NPE)
       resolved; Stage C marker not yet emitted due to new downstream
       NoSuchMethodError → reported as PARTIAL with full triage detail
- [ ] Stage C complete: NO (new downstream blocker)
- [ ] Stage D: NO (gated on Stage C)

## Recommendation for CR63

Deminify the noice APK's `Lv;` class (in `classes.dex`) to identify the
exact method signature `attachBaseContext` is calling at offset `v:44`.
Add the missing method (likely on `Configuration`, `Resources`, or
`DisplayMetrics`) as an AOSP-default-bodied stub. Estimate 1-2 days
once the missing method is identified.

Alternative if multiple methods are missing: enable dalvik-kitkat's
`-verbose:class` and re-run to enumerate all class-resolution failures
before the NoSuchMethodError, then close the surface in one CR63 pass.

Once Stage C reaches, Stage D should follow quickly per the CR62 brief
("noice's window background drawable should be theme-driven (not
Compose-driven) and reachable via Window.getDecorView() after
setContentView runs") — the launcher already paints the fallback ARGB
and presents through `libdrm_inproc_bridge.so`.
