# V2 Step 6 Diff Specification — WestlakeActivityThread.attachActivity

Prep doc for Step 6: apply this diff after Step 2 (WestlakeActivity) lands.

## Current state (pre-V2-Step6)

`shim/java/android/app/WestlakeActivityThread.java` lines 2995-3266 (approximately) contain a 200+ line `attachActivity` method that:

1. Computes `strictStandalone`, `runMcdonaldsLifecycle`, `strictSkipLifecycle` flags (V1 control modes)
2. Tries `activity.attach(baseContext, app, intent, component, null, mInstrumentation)` (V1 6-arg shim attach)
3. Catches `NoSuchMethodError` when framework's Activity.attach (17-arg) wins → falls through
4. Field-sets `mBase` via `setInstanceField(activity, ContextWrapper.class, "mBase", baseContext)`
5. Calls `ensureActivityWindow(activity)`
6. Field-sets `mApplication`, `mIntent`, `mComponent`, `mFinished`, `mDestroyed` via reflection on `Activity.class`
7. Calls `initializeAndroidxActivityState(activity)` if non-strict
8. Calls `WestlakeLauncher.wireStandaloneActivityResources(...)` if shim attach succeeded
9. Has heavy `WestlakeLauncher.marker("PF301 strict WAT attachActivity ...")` instrumentation throughout

## V2 Step 6 replacement

After Step 2 lands, OUR `android.app.Activity` IS the runtime class (classpath shadow via framework_duplicates.txt). It has BOTH 6-arg and 18-arg `attach()` overloads. No reflection needed.

### Diff target

**REPLACE** lines 2995-3266 (the entire `attachActivity` method body) with:

```java
/**
 * V2 Step 6: WestlakeActivity owns attach() with 6-arg overload (Step 2 added
 * the 6-arg variant alongside the AOSP 18-arg). No reflection, no try/catch,
 * no field-set fallback path. Activity.attach() IS our code via classpath
 * shadowing (framework_duplicates.txt removed android/app/Activity).
 *
 * V1 deletions:
 *   - strictStandalone / runMcdonaldsLifecycle / strictSkipLifecycle flags
 *   - shimAttachOk try/catch + NoSuchMethodError detection
 *   - setInstanceField(activity, ContextWrapper.class, "mBase", ...) reflection
 *   - setInstanceField(activity, Activity.class, "mApplication"/"mIntent"/"mComponent"/"mFinished"/"mDestroyed", ...) reflection
 *   - wireStandaloneActivityResources (V1 hack — Step 4's WestlakeResources is now plumbed via activity.getResources())
 *   - PF301 strict markers (V2 doesn't have strict/control modes)
 */
private void attachActivity(Activity activity, Context baseContext,
                             Application app, Intent intent,
                             ComponentName component) {
    WestlakeLauncher.trace("[WestlakeActivityThread] V2 attachActivity begin: "
            + activity.getClass().getName());
    activity.attach(baseContext, app, intent, component, mInstrumentation, /*token*/ null);
    WestlakeLauncher.trace("[WestlakeActivityThread] V2 attachActivity done");
}
```

**ALSO DELETE** these helper methods that are no longer called:

- `shouldRunMcdonaldsLifecycleInStrict(...)` — V1 strict-mode helper
- `isCutoffCanaryLifecycleProbe(...)` — V1 helper (already mostly dead per CR14/CR16)
- `setInstanceField(Object, Class, String, Object)` — was the field-plant primitive; not needed after V2
- `ensureActivityWindow(activity)` — V1 Window/PhoneWindow synthesis hack (Step 5's PhoneWindow shadow handles this naturally in our Activity.attach)
- `initializeAndroidxActivityState(activity)` — AndroidX init that V1 needed because framework Activity.attach was bypassed; V2's Activity.attach does it correctly
- `throwableSummary(Throwable)` — only used by the deleted try/catch
- The class-level `PF301` instrumentation throughout (~20 marker() calls in attachActivity alone)

**KEEP** in WestlakeActivityThread.java:
- All other lifecycle methods (`performLaunchActivity`, `handleResumeActivity`, etc.)
- The MainActivity launch driver (`launchMainActivityForShim`)
- AOSP 18-arg vs Westlake 6-arg coordination (delete the 18-arg path; Step 2 unified into 6-arg via attachInternal)

## Estimated edit size

- Lines deleted: ~250 LOC in attachActivity + helpers
- Lines added: ~10 LOC (the new tiny attachActivity body)
- Net delta: **-240 LOC** in WestlakeActivityThread.java

## Acceptance for Step 6

After applying this diff:

```bash
cd /home/dspfac/android-to-openharmony-migration
bash scripts/build-shim-dex.sh 2>&1 | tail -10
# Expected: clean build

ADB="/mnt/c/Users/dspfa/Dev/platform-tools/adb.exe -H localhost -P 5037 -s cfb7c9e3"
$ADB push aosp-shim.dex /data/local/tmp/westlake/
bash scripts/binder-pivot-regression.sh --quick 2>&1 | tail -10
# Expected: 14/14 PASS (substrate stable post-V2)
```

## Dependencies

- **Blocked by Step 2** (Activity.java with 6-arg attach + 18-arg attach overloads, unified attachInternal)
- **Blocked by Step 3** (Application class shadowed) — DONE
- **Blocked by Step 5** (Window/PhoneWindow stubs) — provides PhoneWindow that Activity.attach instantiates

Should land in same wave as Step 7 (remove obsolete plants from WestlakeResources).
