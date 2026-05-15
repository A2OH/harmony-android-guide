# CR63 E13 noice Checkpoint — `v:44` blocker DEMINIFIED + RESOLVED, two new downstream blockers (2026-05-15)

Agent 21. Spike bound: 1-2 days. Actual elapsed: ~1.5h (deminify + fix + retest).

## Deminification result

Hex stack `at v:44 ← attachBaseContext:0x1fc ← <init>`:

| Symbol | Resolved-to |
|---|---|
| `Lv;` (inner) | **`Le/k0;->v(Context, int, k0/k, Configuration, Z) Configuration`** — `androidx.appcompat.app.AppCompatDelegateImpl.applyDayNight` static helper |
| `attachBaseContext` (outer) | **`Le/q;.attachBaseContext(Context)`** — `androidx.appcompat.app.AppCompatActivity.attachBaseContext` |
| `:44` (line) | source line 44 of `Le/k0;->v` — corresponds to `invoke-virtual Configuration.setTo(Configuration)V` |

The missing method was the **AOSP-public `Configuration.setTo(Configuration)`** — present on every Android release since API 1 but absent from our thin shim.

The deminify path: the noice APK was decoded once at `/tmp/cr40-noice/decoded/smali/` (apktool). The class `Le/k0;` (extends `Le/v;` = AppCompatDelegate) is at `e/k0.smali`; its method `v` starts at line 793; smali `.line 44` directive points at `invoke-virtual {p1, p3}, Landroid/content/res/Configuration;->setTo(Landroid/content/res/Configuration;)V`. R8 preserves line numbers in the dex debug info, so dalvik-kitkat's `dvmLineNumFromPC` returned source line 44 (not the bytecode PC).

## Fix

Per a corpus-wide sweep `grep 'Configuration;->' /tmp/cr40-noice/decoded/smali` showed four Configuration methods noice references but our shim lacked:
- `setTo(Configuration)V`
- `setLocale(Locale)V`
- `setLayoutDirection(Locale)V`
- `getLayoutDirection()I`

All four added to `shim/java/android/content/res/Configuration.java` with AOSP-default-bodied stubs (file/line/class cited in javadoc). No per-app branches, no Unsafe, no setAccessible.

Re-running E13 advanced past `attachBaseContext:0x1fc` to `attachBaseContext:0x215` (29 bytes deeper) and hit the next NSME:

| Symbol | Resolved-to |
|---|---|
| `Lo3/f;->T(Resources$Theme)V` | `androidx.core.content.res.ResourcesCompat.rebase` |
| `Lf0/q;->a(Resources$Theme)V` | inline wrapper around `Theme.rebase()` (called when SDK >= 29) |

The missing method was **`Resources.Theme.rebase()V`** — Android Q+ API (frameworks/base ResourcesImpl), absent from our `Resources$Theme` static-inner class. Added an AOSP-default no-op body.

## Validation

| Stage | Before CR63 | After CR63 |
|---|---|---|
| A | PASS | PASS |
| B | PASS | PASS |
| **C** | **FAIL** (NSME at `v:44`) | **FAIL** but **past attachBaseContext entirely** — new blocker is in `MainActivity.<init>` body at offset 0x19, `NoSuchFieldError` after loading `Landroidx/lifecycle/Lifecycle$State;` |
| D | not reached | not reached |

Net: original `v:44` NSME RESOLVED, full attachBaseContext chain now executes, new blocker is **deeper inside MainActivity's own constructor** (downstream of all AppCompat init).

## Regressions

| Test | Result |
|---|---|
| MVP-0 hello (arm32) | **PASS** |
| MVP-1 trivial-activity (arm32) | **PASS** |
| E12 smoke (hello-color-apk → BLUE) (arm32) | **PASS** |

## Files changed

- `shim/java/android/content/res/Configuration.java` (+65 LOC)
  - `+ public void setTo(Configuration o)` — full field copy
  - `+ public void setLocale(Locale)` — delegates to setLayoutDirection
  - `+ public void setLayoutDirection(Locale)` — updates SCREENLAYOUT_LAYOUTDIR_MASK bits
  - `+ public int getLayoutDirection()` — reads back the bits
  - `+ public static final int SCREENLAYOUT_LAYOUTDIR_*` constants (mask + shift + UNDEFINED/LTR/RTL)
- `shim/java/android/content/res/Resources.java` (+8 LOC)
  - `Theme: + public void rebase()` — no-op (no overlay state in this shim)
- `ohos-deploy/aosp-shim-ohos.dex` — rebuilt via `scripts/build-shim-dex-ohos.sh` (5088924 → 5089652 B)

## New downstream blocker (out-of-scope, for CR64+)

```
Class load just before crash:
  androidx/activity/OnBackPressedDispatcher
  androidx/fragment/app/{c0,i0,g0,...}
  androidx/lifecycle/{h1,w,y,p}
  androidx/lifecycle/Lifecycle$State          ← enum
  → NoSuchFieldError at <init>:26
  ← <init>:0x19
  ← MainActivity.<init> chain
```

The error is `NoSuchFieldError` (not NSME). The most likely cause: noice references a `Lifecycle.State` enum constant (e.g. `INITIALIZED`, `CREATED`, `STARTED`, `RESUMED`, `DESTROYED`) but the deployed androidx Lifecycle class in noice's APK was R8-shrunk to omit one of them. **Could equally be a `Build.VERSION_CODES` constant noice references that our `Build.java` lacks** — many Q/R/S/T/U constants are still missing. Recommend grep'ing the smali for static field references against our shim before runtime experimentation.

## Self-audit (pre-commit)

- [x] No `Unsafe.allocateInstance` in new Java code
- [x] No `setAccessible(true)` in new Java code
- [x] No per-app branches — all new methods are AOSP-API surface
- [x] `WestlakeContextImpl` untouched (frozen)
- [x] All new methods on classes WE own (`Configuration`, `Resources.Theme`)
- [x] All new methods have AOSP-default bodies, javadoc cites AOSP origin
- [x] MVP-0 + MVP-1 + E12 regression all PASS
- [x] E13 noice: original `v:44` NSME confirmed gone, advanced past `attachBaseContext` entirely

## Recommendation for CR64

1. Decode noice's references to `Landroidx/lifecycle/Lifecycle$State;` static fields (smali `sget-object` opcodes) and verify they all resolve. Most likely candidate: the R8 minifier dropped a State enum constant that MainActivity's `<init>` (or a Hilt-generated component) references.
2. Equally likely: a `Build.VERSION_CODES` field (e.g. UPSIDE_DOWN_CAKE = 34, VANILLA_ICE_CREAM = 35, BAKLAVA = 36) — grep `Landroid/os/Build$VERSION_CODES;->` in the smali and cross-check.
3. The error is in MainActivity.<init> at offset 0x19 — only ~12 bytes into the body. A bytecode dump of MainActivity's `<init>` (dexdump -d on noice-classes-min13.dex) would pinpoint the exact `sget-object` that fails.

Estimate for CR64: 1 day (similar pattern to CR63 — find the missing field, add it, re-run).
