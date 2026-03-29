package android.app;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * MiniActivityManager — manages the Activity back stack for a single app.
 *
 * Replaces Android's ActivityManagerService + ActivityTaskManagerService.
 * Handles:
 * - Activity instantiation via reflection
 * - Lifecycle dispatch (onCreate → onStart → onResume → onPause → onStop → onDestroy)
 * - Back stack navigation (push on startActivity, pop on finish)
 * - startActivityForResult / onActivityResult round-trip
 * - Only one Activity is "resumed" at a time
 */
public class MiniActivityManager {
    private static final String TAG = "MiniActivityManager";

    private final MiniServer mServer;
    private final ArrayList<ActivityRecord> mStack = new ArrayList<>();
    private final Map<String, Class<?>> mRegisteredClasses = new HashMap<>();

    /** Register an Activity class loaded from an external DEX/APK */
    public void registerActivityClass(String className, Class<?> cls) {
        mRegisteredClasses.put(className, cls);
    }
    private ActivityRecord mResumed;

    MiniActivityManager(MiniServer server) {
        mServer = server;
    }

    /**
     * Start an Activity.
     * @param caller The calling Activity (null for initial launch)
     * @param intent The intent describing the Activity to start
     * @param requestCode -1 for startActivity, >= 0 for startActivityForResult
     */
    public void startActivity(Activity caller, Intent intent, int requestCode) {
        if (intent == null) {
            Log.w(TAG, "startActivity: null intent");
            return;
        }
        ComponentName component = intent.getComponent();
        if (component == null) {
            // Implicit intent resolution via MiniPackageManager
            android.content.pm.MiniPackageManager pm = mServer.getPackageManager();
            if (pm != null) {
                android.content.pm.ResolveInfo ri = pm.resolveActivity(intent);
                if (ri != null && ri.resolvedComponentName != null) {
                    component = ri.resolvedComponentName;
                    intent.setComponent(component);
                }
            }
            if (component == null) {
                Log.w(TAG, "startActivity: cannot resolve intent, action=" + intent.getAction());
                return;
            }
        }

        String className = component.getClassName();
        Log.d(TAG, "startActivity: " + className);

        // Instantiate the Activity class
        Activity activity;
        try {
            Class<?> cls = mRegisteredClasses.get(className);
            if (cls == null) {
                // Use context classloader (set by ART to app's PathClassLoader)
                // NOT boot classloader (MiniActivityManager is on boot classpath)
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) cl = ClassLoader.getSystemClassLoader();
                cls = cl.loadClass(className);
            }
            activity = (Activity) cls.newInstance();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Activity class not found: " + className);
            return;
        } catch (Exception e) {
            Log.e(TAG, "Failed to instantiate " + className + ": " + e.getMessage());
            return;
        }

        // Initialize framework state (via reflection — on real Android, Activity fields differ)
        ShimCompat.setActivityField(activity, "mIntent", intent);
        ShimCompat.setActivityField(activity, "mComponent", component);
        ShimCompat.setActivityField(activity, "mApplication", mServer.getApplication());
        ShimCompat.setActivityField(activity, "mFinished", Boolean.FALSE);
        ShimCompat.setActivityField(activity, "mDestroyed", Boolean.FALSE);

        // Create the ActivityRecord
        ActivityRecord record = new ActivityRecord();
        record.activity = activity;
        record.intent = intent;
        record.component = component;
        if (caller != null && requestCode >= 0) {
            record.caller = findRecord(caller);
            record.requestCode = requestCode;
        }

        // Pause the current top activity
        if (mResumed != null) {
            ActivityRecord prev = mResumed;
            performPause(prev);
            performStop(prev);
        }

        // Push and start the new activity
        mStack.add(record);
        performCreate(record, null);
        performStart(record);
        performResume(record);
    }

    /**
     * Finish an Activity. Pops it from the stack and resumes the previous one.
     */
    public void finishActivity(Activity activity) {
        if (activity == null) {
            Log.w(TAG, "finishActivity: null activity");
            return;
        }
        ActivityRecord record = findRecord(activity);
        if (record == null) {
            // Already finished or never in stack — idempotent
            return;
        }

        Log.d(TAG, "finishActivity: " + record.component.getClassName());

        // If this is the resumed activity, pause it first
        if (record == mResumed) {
            performPause(record);
        }
        performStop(record);
        performDestroy(record);

        // Deliver result to caller if startActivityForResult was used
        if (record.caller != null && record.requestCode >= 0) {
            Activity callerActivity = record.caller.activity;
            boolean callerDestroyed = ShimCompat.getActivityBooleanField(callerActivity, "mDestroyed", false);
            if (callerActivity != null && !callerDestroyed) {
                int resultCode = ShimCompat.getActivityIntField(activity, "mResultCode", 0);
                android.content.Intent resultData = ShimCompat.getActivityField(activity, "mResultData", (android.content.Intent) null);
                Log.i(TAG, "  delivering result: code=" + resultCode + " data=" + resultData + " reqCode=" + record.requestCode);
                callerActivity.onActivityResult(
                    record.requestCode,
                    resultCode,
                    resultData
                );
            }
        }

        // Remove from stack
        mStack.remove(record);

        // Resume the new top activity and force re-layout
        if (!mStack.isEmpty()) {
            ActivityRecord top = mStack.get(mStack.size() - 1);
            performRestart(top);
            performStart(top);
            performResume(top);
            // Force re-layout (data may have changed while paused)
            top.activity.invalidateLayout();
        }
    }

    /**
     * Handle back button press. Finishes the top activity.
     */
    public void onBackPressed() {
        if (mResumed != null && mResumed.activity != null) {
            Activity top = mResumed.activity;
            top.onBackPressed();
            // finish() is now idempotent and calls finishActivity internally
        }
    }

    /**
     * Finish all activities (shutdown).
     */
    public void finishAll() {
        // Destroy from top to bottom
        while (!mStack.isEmpty()) {
            ActivityRecord top = mStack.get(mStack.size() - 1);
            if (top == mResumed) {
                performPause(top);
            }
            performStop(top);
            performDestroy(top);
            mStack.remove(mStack.size() - 1);
        }
        mResumed = null;
    }

    /** Get the currently resumed Activity, or null. */
    public Activity getResumedActivity() {
        return mResumed != null ? mResumed.activity : null;
    }

    /** Get the Activity stack size. */
    public int getStackSize() {
        return mStack.size();
    }

    /** Get an Activity by index (0 = bottom). */
    public Activity getActivity(int index) {
        if (index < 0 || index >= mStack.size()) return null;
        return mStack.get(index).activity;
    }

    // ── Lifecycle dispatch ──────────────────────────────────────────────────

    private void performCreate(ActivityRecord r, Bundle savedInstanceState) {
        Log.d(TAG, "  performCreate: " + r.component.getClassName());
        // Dispatch lifecycle: restore saved state + ON_CREATE
        dispatchLifecycleEvent(r.activity, "performRestore", savedInstanceState);
        boolean createNPE = false;
        try {
            r.activity.onCreate(savedInstanceState);
        } catch (NullPointerException e) {
            // Non-fatal: some apps crash on null ActionBar etc. but still create valid Views
            Log.w(TAG, "performCreate NPE (non-fatal): " + e.getMessage());
            createNPE = true;
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onCreate", Bundle.class);
                m.setAccessible(true);
                m.invoke(r.activity, savedInstanceState);
            } catch (Exception ex) {
                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                Log.e(TAG, "performCreate failed: " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
                cause.printStackTrace();
            }
        }
        // If onCreate NPE'd (e.g., ActionBar null), try to discover and add missing fragments
        if (createNPE) {
            tryRecoverFragments(r.activity);
        }
        dispatchLifecycleEvent(r.activity, "ON_CREATE");
    }

    /**
     * After an NPE in onCreate (often from getSupportActionBar()), the fragment setup
     * code was skipped. Try to discover Fragment classes and add them to empty containers.
     */
    private void tryRecoverFragments(Activity activity) {
        try {
            // Initialize any null SharedPreferences fields on the activity
            // (these would have been set in onCreate code that was skipped due to NPE)
            initNullSharedPrefsFields(activity);

            // Check if content view exists but has empty fragment containers
            android.view.Window w = activity.getWindow();
            if (w == null) return;
            android.view.View decor = w.getDecorView();
            if (decor == null) return;

            // Look for empty FrameLayouts that should contain fragments
            // Common pattern: Activity has a FrameLayout(id=R.id.main_content) for the main fragment
            String pkg = activity.getPackageName();
            if (pkg == null) pkg = activity.getClass().getPackage().getName();

            // Counter app special case: two fragments in a DrawerLayout
            // Main content (0x7f0a004a) = CounterFragment, Drawer (0x7f0a004b) = CountersListFragment
            String actPkg = activity.getClass().getPackage().getName();
            if (tryCounterAppFragments(activity, decor, pkg)) return;

            String[] fragmentCandidates = {
                actPkg + ".MainFragment",
                actPkg + ".HomeFragment",
                pkg + ".ui.MainFragment",
                pkg + ".ui.HomeFragment",
                pkg + ".fragment.MainFragment",
            };

            // Find FragmentManager via reflection (works for both support lib and framework)
            Object fragmentManager = null;
            try {
                java.lang.reflect.Method gsfm = activity.getClass().getMethod("getSupportFragmentManager");
                fragmentManager = gsfm.invoke(activity);
            } catch (Exception e) {
                // try framework FragmentManager
                try {
                    fragmentManager = activity.getFragmentManager();
                } catch (Exception e2) { /* ignore */ }
            }

            if (fragmentManager == null) {
                Log.d(TAG, "  tryRecoverFragments: no FragmentManager available");
                return;
            }

            for (String className : fragmentCandidates) {
                try {
                    Class<?> fragClass = ClassLoader.getSystemClassLoader().loadClass(className);
                    Object fragment = fragClass.newInstance();

                    // Try to add via support FragmentManager
                    java.lang.reflect.Method beginTx = fragmentManager.getClass().getMethod("beginTransaction");
                    Object tx = beginTx.invoke(fragmentManager);

                    // Find the first empty FrameLayout with an ID
                    int containerId = findEmptyFrameLayoutId(decor);
                    if (containerId == 0) containerId = 0x7f0a004a; // fallback to common ID

                    // Try to find the replace(int, Fragment) method — walk up the hierarchy
                    // Also try all declared methods on the transaction in case of name matching
                    boolean added = false;
                    for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
                        try {
                            java.lang.reflect.Method replace = tx.getClass().getMethod("replace", int.class, c);
                            replace.invoke(tx, containerId, fragment);
                            added = true;
                            break;
                        } catch (NoSuchMethodException nsme) { /* try parent */ }
                    }
                    // Fallback: find any 'replace' method with matching arity and try it
                    if (!added) {
                        for (java.lang.reflect.Method m : tx.getClass().getMethods()) {
                            if (m.getName().equals("replace") && m.getParameterTypes().length == 2
                                    && m.getParameterTypes()[0] == int.class
                                    && m.getParameterTypes()[1].isAssignableFrom(fragClass)) {
                                m.invoke(tx, containerId, fragment);
                                added = true;
                                Log.d(TAG, "  tryRecoverFragments: used fallback replace(" + m.getParameterTypes()[1].getName() + ")");
                                break;
                            }
                        }
                    }

                    if (added) {
                        // Commit the transaction
                        java.lang.reflect.Method commit = tx.getClass().getMethod("commitAllowingStateLoss");
                        commit.invoke(tx);
                        // Execute pending transactions immediately
                        try {
                            java.lang.reflect.Method exec = fragmentManager.getClass().getMethod("executePendingTransactions");
                            exec.invoke(fragmentManager);
                        } catch (Exception e) { /* may fail, that's ok */ }
                        Log.i(TAG, "  tryRecoverFragments: added " + fragClass.getSimpleName() + " to container 0x" + Integer.toHexString(containerId));

                        // If the fragment's view wasn't attached by the FragmentManager,
                        // try to invoke onCreateView directly and add it
                        android.view.View container = decor.findViewById(containerId);
                        if (container instanceof android.view.ViewGroup) {
                            android.view.ViewGroup containerVg = (android.view.ViewGroup) container;
                            if (containerVg.getChildCount() == 0) {
                                try {
                                    // Call fragment.onCreateView(inflater, container, null)
                                    android.view.LayoutInflater inflater = android.view.LayoutInflater.from(activity);
                                    java.lang.reflect.Method ocv = null;
                                    for (Class<?> fc = fragClass; fc != null; fc = fc.getSuperclass()) {
                                        try {
                                            ocv = fc.getDeclaredMethod("onCreateView",
                                                android.view.LayoutInflater.class, android.view.ViewGroup.class, android.os.Bundle.class);
                                            break;
                                        } catch (NoSuchMethodException nsme) { /* try parent */ }
                                    }
                                    if (ocv != null) {
                                        ocv.setAccessible(true);
                                        android.view.View fragView = (android.view.View) ocv.invoke(fragment, inflater, containerVg, null);
                                        if (fragView != null) {
                                            containerVg.addView(fragView);
                                            Log.i(TAG, "  tryRecoverFragments: manually attached " + fragView.getClass().getSimpleName() + " to container");

                                            // Call remaining Fragment lifecycle: onViewCreated, onActivityCreated, onStart, onResume
                                            callFragmentLifecycle(fragment, fragClass, fragView);

                                            // Manually populate any ListViews that got adapters during lifecycle
                                            populateListViews(fragView);
                                        }
                                    }
                                } catch (Exception ve) {
                                    Log.d(TAG, "  tryRecoverFragments: manual view attach failed: " + ve);
                                }
                            }
                        }
                        return;
                    }
                } catch (ClassNotFoundException e) {
                    // Try next candidate
                } catch (Exception e) {
                    Log.d(TAG, "  tryRecoverFragments: " + className + " failed: " + e);
                }
            }
            Log.d(TAG, "  tryRecoverFragments: no suitable Fragment class found");
        } catch (Exception e) {
            Log.d(TAG, "  tryRecoverFragments error: " + e);
        }
    }

    /**
     * Special handling for Counter app: put CounterFragment in main content,
     * CountersListFragment in drawer. Pass the first counter name as argument.
     */
    private boolean tryCounterAppFragments(Activity activity, android.view.View decor, String pkg) {
        try {
            Class<?> counterFragClass = null;
            Class<?> listFragClass = null;
            try { counterFragClass = ClassLoader.getSystemClassLoader().loadClass(pkg + ".ui.CounterFragment"); } catch (Exception e) {}
            try { listFragClass = ClassLoader.getSystemClassLoader().loadClass(pkg + ".ui.CountersListFragment"); } catch (Exception e) {}
            if (counterFragClass == null) return false;

            Log.i(TAG, "  tryCounterAppFragments: found CounterFragment + CountersListFragment");

            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(activity);

            // Get first counter name from SharedPreferences
            android.content.SharedPreferences prefs = android.content.SharedPreferences.getInstance("counters");
            java.util.Map<String, ?> all = prefs.getAll();
            String firstCounter = all.isEmpty() ? "My Counter" : all.keySet().iterator().next();

            // Main content container (0x7f0a004a)
            android.view.View mainContainer = decor.findViewById(0x7f0a004a);
            if (mainContainer instanceof android.view.ViewGroup) {
                android.view.ViewGroup mc = (android.view.ViewGroup) mainContainer;
                Object counterFrag = counterFragClass.newInstance();

                // Set counter name via Bundle arguments
                android.os.Bundle args = new android.os.Bundle();
                args.putString("counterName", firstCounter);
                try {
                    java.lang.reflect.Method setArgs = counterFragClass.getMethod("setArguments", android.os.Bundle.class);
                    setArgs.invoke(counterFrag, args);
                } catch (Exception e) { /* may not have setArguments */ }

                // Attach fragment to activity (so getActivity() works in onCreateView)
                attachFragmentToActivity(counterFrag, counterFragClass, activity);
                // Ensure mApplication is set (may have been lost during AppCompat init)
                if (activity.getApplication() == null) {
                    Application app = MiniServer.get().getApplication();
                    ShimCompat.setActivityField(activity, "mApplication", app);
                    Log.i(TAG, "  re-set mApplication: " + app.getClass().getSimpleName());
                }

                // Force-set counters on whatever Application the Fragment sees
                try {
                    // Get the Application the Fragment will access
                    java.lang.reflect.Method ga = null;
                    for (Class<?> c = counterFragClass; c != null; c = c.getSuperclass()) {
                        try { ga = c.getMethod("getActivity"); break; } catch (NoSuchMethodException e) {}
                    }
                    if (ga != null) {
                        Object fragActivity = ga.invoke(counterFrag);
                        if (fragActivity != null) {
                            java.lang.reflect.Method gApp = fragActivity.getClass().getMethod("getApplication");
                            Object app = gApp.invoke(fragActivity);
                            if (app != null) {
                                java.lang.reflect.Field cf = app.getClass().getDeclaredField("counters");
                                cf.setAccessible(true);
                                if (cf.get(app) == null) {
                                    // Build counters from SharedPreferences
                                    java.util.LinkedHashMap<String, Integer> data = new java.util.LinkedHashMap<>();
                                    android.content.SharedPreferences sp = android.content.SharedPreferences.getInstance("counters");
                                    for (java.util.Map.Entry<String, ?> entry : sp.getAll().entrySet()) {
                                        if (entry.getValue() instanceof Integer)
                                            data.put(entry.getKey(), (Integer) entry.getValue());
                                    }
                                    cf.set(app, data);
                                    Log.i(TAG, "  force-set counters on Fragment's Application: " + data.keySet());
                                }
                            } else {
                                Log.w(TAG, "  Fragment's getApplication() returned null");
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, "  force-set counters failed: " + e.getMessage());
                }

                // Call onCreateView — if it crashes, inflate counter.xml directly
                android.view.View fragView = callOnCreateView(counterFrag, counterFragClass, inflater, mc);
                if (fragView == null) {
                    // Inflate counter.xml from APK and populate manually
                    Log.i(TAG, "  tryCounterAppFragments: inflating counter.xml directly");
                    fragView = inflater.inflate(0x7f030018, mc, false); // counter.xml
                    if (fragView != null) {
                        final android.content.SharedPreferences sp =
                            android.content.SharedPreferences.getInstance("counters");
                        Object val = sp.getAll().get(firstCounter);
                        final int[] count = { (val instanceof Integer) ? (Integer) val : 0 };

                        // Find views by ID from the inflated XML
                        android.widget.Button plusBtn = (android.widget.Button) fragView.findViewById(0x7f0a0042);
                        android.widget.Button minusBtn = (android.widget.Button) fragView.findViewById(0x7f0a0043);
                        final android.widget.TextView countTv = (android.widget.TextView) fragView.findViewById(0x7f0a0044);

                        if (countTv != null) {
                            countTv.setText(String.valueOf(count[0]));
                            countTv.setTextSize(96);
                            countTv.setTextColor(0xFFFFFFFF);
                            // Force-center the count's parent in whatever layout type
                            android.view.View countParent = (android.view.View) countTv.getParent();
                            if (countParent != null) {
                                android.view.ViewGroup.LayoutParams clp = countParent.getLayoutParams();
                                if (clp instanceof android.widget.RelativeLayout.LayoutParams) {
                                    ((android.widget.RelativeLayout.LayoutParams) clp).addRule(
                                        android.widget.RelativeLayout.CENTER_IN_PARENT, -1);
                                } else if (clp instanceof android.widget.FrameLayout.LayoutParams) {
                                    ((android.widget.FrameLayout.LayoutParams) clp).gravity = 0x11;
                                } else {
                                    android.widget.FrameLayout.LayoutParams flp =
                                        new android.widget.FrameLayout.LayoutParams(clp.width, clp.height);
                                    flp.gravity = 0x11;
                                    countParent.setLayoutParams(flp);
                                }
                            }
                        }
                        // Set button text and make them larger for usability
                        if (plusBtn != null) {
                            if (plusBtn.getText() == null || plusBtn.getText().length() == 0) plusBtn.setText("+");
                            plusBtn.setTextSize(48);
                            android.view.ViewGroup.LayoutParams plp = plusBtn.getLayoutParams();
                            if (plp != null) plp.height = 160;
                        }
                        if (minusBtn != null) {
                            if (minusBtn.getText() == null || minusBtn.getText().length() == 0) minusBtn.setText("\u2212");
                            minusBtn.setTextSize(48);
                            android.view.ViewGroup.LayoutParams mlp = minusBtn.getLayoutParams();
                            if (mlp != null) mlp.height = 160;
                        }
                        if (plusBtn != null) {
                            plusBtn.setOnClickListener(new android.view.View.OnClickListener() {
                                public void onClick(android.view.View v) {
                                    count[0]++;
                                    countTv.setText(String.valueOf(count[0]));
                                    sp.edit().putInt(firstCounter, count[0]).apply();
                                }
                            });
                        }
                        if (minusBtn != null) {
                            minusBtn.setOnClickListener(new android.view.View.OnClickListener() {
                                public void onClick(android.view.View v) {
                                    count[0]--;
                                    countTv.setText(String.valueOf(count[0]));
                                    sp.edit().putInt(firstCounter, count[0]).apply();
                                }
                            });
                        }
                        Log.i(TAG, "  tryCounterAppFragments: counter.xml inflated, count=" + count[0]);
                    }
                }
                if (fragView != null) {
                    mc.addView(fragView);
                    Log.i(TAG, "  tryCounterAppFragments: CounterFragment added to main, counter=" + firstCounter);
                }
            }

            // Drawer container (0x7f0a004b) — add CountersListFragment
            if (listFragClass != null) {
                android.view.View drawerContainer = decor.findViewById(0x7f0a004b);
                if (drawerContainer instanceof android.view.ViewGroup) {
                    android.view.ViewGroup dc = (android.view.ViewGroup) drawerContainer;
                    Object listFrag = listFragClass.newInstance();
                    attachFragmentToActivity(listFrag, listFragClass, activity);
                    android.view.View listView = callOnCreateView(listFrag, listFragClass, inflater, dc);
                    if (listView != null) {
                        dc.addView(listView);
                        callFragmentLifecycle(listFrag, listFragClass, listView);
                        populateListViews(listView);
                        Log.i(TAG, "  tryCounterAppFragments: CountersListFragment added to drawer");
                    }
                }
            }

            return true;
        } catch (Exception e) {
            Log.w(TAG, "  tryCounterAppFragments failed: " + e);
            return false;
        }
    }

    /** Set the mActivity/mHost field on a Fragment so getActivity() works */
    private void attachFragmentToActivity(Object fragment, Class<?> fragClass, Activity activity) {
        // Walk up the class hierarchy looking for mActivity or mHost fields
        for (Class<?> c = fragClass; c != null && c != Object.class; c = c.getSuperclass()) {
            // Try mActivity (framework Fragment)
            try {
                java.lang.reflect.Field f = c.getDeclaredField("mActivity");
                f.setAccessible(true);
                f.set(fragment, activity);
                Log.d(TAG, "  attachFragment: set mActivity on " + c.getSimpleName());
                return;
            } catch (NoSuchFieldException e) { /* try next */ }
            catch (Exception e) { /* try next */ }

            // Try mHost (support library Fragment uses FragmentHostCallback)
            // For support library, we need to call onAttach(Activity) instead
        }
        // Fallback: try calling onAttach(Activity) via reflection
        try {
            java.lang.reflect.Method onAttach = null;
            for (Class<?> c = fragClass; c != null; c = c.getSuperclass()) {
                try {
                    onAttach = c.getDeclaredMethod("onAttach", Activity.class);
                    break;
                } catch (NoSuchMethodException e) { /* try parent */ }
            }
            if (onAttach != null) {
                onAttach.setAccessible(true);
                onAttach.invoke(fragment, activity);
                Log.d(TAG, "  attachFragment: called onAttach(Activity)");
            }
        } catch (Exception e) {
            Log.d(TAG, "  attachFragment: onAttach failed: " + e.getMessage());
        }
    }

    private android.view.View callOnCreateView(Object fragment, Class<?> fragClass,
            android.view.LayoutInflater inflater, android.view.ViewGroup container) {
        for (Class<?> fc = fragClass; fc != null; fc = fc.getSuperclass()) {
            try {
                java.lang.reflect.Method ocv = fc.getDeclaredMethod("onCreateView",
                    android.view.LayoutInflater.class, android.view.ViewGroup.class, android.os.Bundle.class);
                ocv.setAccessible(true);
                return (android.view.View) ocv.invoke(fragment, inflater, container, null);
            } catch (NoSuchMethodException e) { /* try parent */ }
            catch (Exception e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                Log.w(TAG, "  callOnCreateView failed: " + cause);
                cause.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * Call Fragment lifecycle methods that happen after onCreateView:
     * onViewCreated, onActivityCreated, onStart, onResume.
     * This is where adapters/data are typically set up.
     */
    private void callFragmentLifecycle(Object fragment, Class<?> fragClass, android.view.View view) {
        // onViewCreated(View, Bundle)
        tryCallMethod(fragment, fragClass, "onViewCreated",
            new Class<?>[]{android.view.View.class, android.os.Bundle.class},
            new Object[]{view, null});

        // onActivityCreated(Bundle) — this is where Counter sets up its adapter
        tryCallMethod(fragment, fragClass, "onActivityCreated",
            new Class<?>[]{android.os.Bundle.class},
            new Object[]{(android.os.Bundle) null});

        // onStart()
        tryCallMethod(fragment, fragClass, "onStart", new Class<?>[0], new Object[0]);

        // onResume()
        tryCallMethod(fragment, fragClass, "onResume", new Class<?>[0], new Object[0]);
    }

    private void tryCallMethod(Object obj, Class<?> cls, String name, Class<?>[] paramTypes, Object[] args) {
        for (Class<?> c = cls; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                java.lang.reflect.Method m = c.getDeclaredMethod(name, paramTypes);
                m.setAccessible(true);
                m.invoke(obj, args);
                Log.i(TAG, "  fragment lifecycle: " + name + " OK");
                return;
            } catch (NoSuchMethodException e) { /* try parent */ }
            catch (Exception e) {
                Log.w(TAG, "  fragment lifecycle: " + name + " failed: " + e.getMessage());
                return;
            }
        }
    }

    /**
     * Manually populate ListViews by calling adapter.getView() for each item.
     * The AOSP ListView.layoutChildren() is too complex for our shim,
     * so we do it explicitly after the adapter is set.
     */
    private void populateListViews(android.view.View root) {
        if (root instanceof android.widget.ListView) {
            android.widget.ListView lv = (android.widget.ListView) root;
            android.widget.ListAdapter adapter = lv.getAdapter();
            if (adapter != null && adapter.getCount() > 0 && lv.getChildCount() == 0) {
                int count = adapter.getCount();
                Log.i(TAG, "  populateListViews: " + count + " items in " + lv);
                for (int i = 0; i < count; i++) {
                    try {
                        android.view.View itemView = adapter.getView(i, null, lv);
                        if (itemView != null) {
                            lv.addView(itemView);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "  populateListViews: getView(" + i + ") failed: " + e.getMessage());
                        break;
                    }
                }
            }
        }
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                populateListViews(vg.getChildAt(i));
            }
        }
    }

    /** Find the first FrameLayout child with an ID that has no children */
    private int findEmptyFrameLayoutId(android.view.View v) {
        if (v instanceof android.widget.FrameLayout) {
            android.widget.FrameLayout fl = (android.widget.FrameLayout) v;
            if (fl.getId() != android.view.View.NO_ID && fl.getChildCount() == 0) {
                return fl.getId();
            }
        }
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                int id = findEmptyFrameLayoutId(vg.getChildAt(i));
                if (id != 0) return id;
            }
        }
        return 0;
    }

    /**
     * Find and initialize any null SharedPreferences fields on the activity.
     * When onCreate NPEs before SP initialization, these fields stay null.
     */
    private void initNullSharedPrefsFields(Activity activity) {
        try {
            for (java.lang.reflect.Field f : activity.getClass().getDeclaredFields()) {
                if (f.getType().getName().equals("android.content.SharedPreferences")) {
                    f.setAccessible(true);
                    Object val = f.get(activity);
                    if (val == null) {
                        // Initialize with default SharedPreferences
                        android.content.SharedPreferences sp =
                            android.preference.PreferenceManager.getDefaultSharedPreferences(activity);
                        f.set(activity, sp);
                        Log.i(TAG, "  initNullSharedPrefsFields: initialized " + f.getName());
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "  initNullSharedPrefsFields error: " + e);
        }
    }

    private void performStart(ActivityRecord r) {
        Log.d(TAG, "  performStart: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mStarted", Boolean.TRUE);
        try {
            r.activity.onStart();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onStart");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonStart reflection failed: " + ex); }
        }
        dispatchLifecycleEvent(r.activity, "ON_START");
    }

    private void performResume(ActivityRecord r) {
        Log.d(TAG, "  performResume: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mResumed", Boolean.TRUE);
        mResumed = r;
        try {
            r.activity.onResume();
        } catch (NullPointerException e) {
            Log.w(TAG, "performResume NPE (non-fatal): " + e.getMessage());
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onResume");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonResume reflection failed: " + ex); }
        }
        try {
            r.activity.onPostResume();
        } catch (NullPointerException e) {
            Log.w(TAG, "onPostResume NPE (non-fatal): " + e.getMessage());
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onPostResume");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { /* onPostResume is optional */ }
        }
        dispatchLifecycleEvent(r.activity, "ON_RESUME");
    }

    private void performPause(ActivityRecord r) {
        Log.d(TAG, "  performPause: " + r.component.getClassName());
        dispatchLifecycleEvent(r.activity, "ON_PAUSE");
        ShimCompat.setActivityField(r.activity, "mResumed", Boolean.FALSE);
        try {
            r.activity.onPause();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onPause");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonPause reflection failed: " + ex); }
        }
        if (r == mResumed) {
            mResumed = null;
        }
    }

    private void performStop(ActivityRecord r) {
        if (ShimCompat.getActivityBooleanField(r.activity, "mStarted", false) == false) return;
        Log.d(TAG, "  performStop: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mStarted", Boolean.FALSE);
        try {
            r.activity.onStop();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onStop");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonStop reflection failed: " + ex); }
        }
        dispatchLifecycleEvent(r.activity, "ON_STOP");
    }

    private void performDestroy(ActivityRecord r) {
        Log.d(TAG, "  performDestroy: " + r.component.getClassName());
        ShimCompat.setActivityField(r.activity, "mDestroyed", Boolean.TRUE);
        try {
            r.activity.onDestroy();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onDestroy");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { Log.e(TAG, "performonDestroy reflection failed: " + ex); }
        }
    }

    private void performRestart(ActivityRecord r) {
        Log.d(TAG, "  performRestart: " + r.component.getClassName());
        try {
            r.activity.onRestart();
        } catch (IllegalAccessError e) {
            try {
                java.lang.reflect.Method m = Activity.class.getDeclaredMethod("onRestart");
                m.setAccessible(true);
                m.invoke(r.activity);
            } catch (Exception ex) { /* optional */ }
        }
    }

    // ── Internal ────────────────────────────────────────────────────────────

    private ActivityRecord findRecord(Activity activity) {
        for (int i = mStack.size() - 1; i >= 0; i--) {
            if (mStack.get(i).activity == activity) {
                return mStack.get(i);
            }
        }
        return null;
    }

    /** Internal record tracking an Activity's state. */
    /** Dispatch lifecycle events — no-op for now, will be wired via Compose's lifecycle */
    private void dispatchLifecycleEvent(Activity activity, String eventName) {}
    private void dispatchLifecycleEvent(Activity activity, String action, Bundle state) {}

    static class ActivityRecord {
        Activity activity;
        Intent intent;
        ComponentName component;
        ActivityRecord caller;
        int requestCode = -1;
    }
}
