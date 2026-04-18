package android.content.pm;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MiniPackageManager — resolves Intents for a single app.
 *
 * Replaces Android's PackageManagerService for single-app engine execution.
 * Handles:
 * - Hardcoded manifest registration (activities, services, providers)
 * - Intent filter registration per component
 * - Implicit intent resolution by action + category matching
 * - Launcher activity identification (ACTION_MAIN + CATEGORY_LAUNCHER)
 */
public class MiniPackageManager {
    private String mPackageName;

    // Registered components
    private final List<ActivityInfo> mActivities = new ArrayList<>();
    private final List<ServiceInfo> mServices = new ArrayList<>();
    private final List<ProviderInfo> mProviders = new ArrayList<>();

    // Intent filters per component class name
    private final Map<String, List<IntentFilter>> mActivityFilters = new HashMap<>();
    private final Map<String, List<IntentFilter>> mServiceFilters = new HashMap<>();

    // ContentProvider registry: authority → provider instance
    private final Map<String, ContentProvider> mProvidersByAuthority = new HashMap<>();

    public MiniPackageManager(String packageName) {
        mPackageName = packageName;
    }

    public String getPackageName() { return mPackageName; }

    public void setPackageName(String packageName) {
        if (packageName != null && !packageName.isEmpty()) {
            mPackageName = packageName;
        }
    }

    // ── Registration API ────────────────────────────────────────────────────

    /**
     * Register an Activity with its intent filters.
     * Called during manifest parsing or hardcoded setup.
     */
    public void addActivity(String className, IntentFilter... filters) {
        ActivityInfo info = new ActivityInfo();
        info.name = className;
        info.packageName = mPackageName;
        mActivities.add(info);

        if (filters.length > 0) {
            List<IntentFilter> list = new ArrayList<>();
            for (IntentFilter f : filters) {
                list.add(f);
            }
            mActivityFilters.put(className, list);
        }
    }

    /**
     * Register a Service with its intent filters.
     */
    public void addService(String className, IntentFilter... filters) {
        ServiceInfo info = new ServiceInfo();
        info.name = className;
        info.packageName = mPackageName;
        mServices.add(info);

        if (filters.length > 0) {
            List<IntentFilter> list = new ArrayList<>();
            for (IntentFilter f : filters) {
                list.add(f);
            }
            mServiceFilters.put(className, list);
        }
    }

    /**
     * Register a ContentProvider by class name and authority.
     */
    public void addProvider(String className, String authority) {
        ProviderInfo info = new ProviderInfo();
        info.name = className;
        info.packageName = mPackageName;
        mProviders.add(info);

        // Instantiate and register the provider
        if (authority != null) {
            try {
                Class<?> cls = Class.forName(className);
                ContentProvider provider = (ContentProvider) cls.newInstance();
                provider.attachInfo(null, new ContentProvider.ProviderInfo(authority));
                mProvidersByAuthority.put(authority, provider);
            } catch (Exception e) {
                // Provider class not found or can't be instantiated — skip
            }
        }
    }

    /**
     * Register a ContentProvider instance directly.
     */
    public void addProvider(String authority, ContentProvider provider) {
        mProvidersByAuthority.put(authority, provider);
    }

    /**
     * Resolve a ContentProvider by authority string.
     */
    public ContentProvider resolveProvider(String authority) {
        return mProvidersByAuthority.get(authority);
    }

    // ── Resolution API ──────────────────────────────────────────────────────

    /**
     * Find the launcher Activity (ACTION_MAIN + CATEGORY_LAUNCHER).
     * Returns null if none registered.
     */
    public ComponentName getLauncherActivity() {
        for (Map.Entry<String, List<IntentFilter>> entry : mActivityFilters.entrySet()) {
            for (IntentFilter filter : entry.getValue()) {
                if (filter.hasAction(Intent.ACTION_MAIN)
                        && filter.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                    return new ComponentName(mPackageName, entry.getKey());
                }
            }
        }
        // Fallback: return the first registered activity if any
        if (!mActivities.isEmpty()) {
            return new ComponentName(mPackageName, mActivities.get(0).name);
        }
        return null;
    }

    /**
     * Resolve an Activity Intent.
     * - Explicit intents (with component set): verify the component is registered
     * - Implicit intents: match action + categories against registered filters
     */
    public ResolveInfo resolveActivity(Intent intent) {
        ComponentName component = intent.getComponent();

        // Explicit intent
        if (component != null) {
            String className = component.getClassName();
            for (ActivityInfo info : mActivities) {
                if (className.equals(info.name)) {
                    ResolveInfo ri = new ResolveInfo();
                    ri.activityInfoObj = info;
                    return ri;
                }
            }
            return null;
        }

        // Implicit intent — match by action + categories
        return resolveImplicit(intent, mActivityFilters);
    }

    /**
     * Resolve a Service Intent.
     */
    public ResolveInfo resolveService(Intent intent) {
        ComponentName component = intent.getComponent();

        if (component != null) {
            String className = component.getClassName();
            for (ServiceInfo info : mServices) {
                if (className.equals(info.name)) {
                    ResolveInfo ri = new ResolveInfo();
                    ri.serviceInfoObj = info;
                    return ri;
                }
            }
            return null;
        }

        return resolveImplicit(intent, mServiceFilters);
    }

    /**
     * Query all activities matching an intent (for queryIntentActivities).
     */
    public List<ResolveInfo> queryIntentActivities(Intent intent) {
        List<ResolveInfo> results = new ArrayList<>();

        ComponentName component = intent.getComponent();
        if (component != null) {
            ResolveInfo ri = resolveActivity(intent);
            if (ri != null) results.add(ri);
            return results;
        }

        String action = intent.getAction();
        Set<String> categories = intent.getCategories();

        for (Map.Entry<String, List<IntentFilter>> entry : mActivityFilters.entrySet()) {
            for (IntentFilter filter : entry.getValue()) {
                if (matchesFilter(filter, action, categories)) {
                    ResolveInfo ri = new ResolveInfo();
                    // Find the corresponding ActivityInfo
                    for (ActivityInfo info : mActivities) {
                        if (entry.getKey().equals(info.name)) {
                            ri.activityInfoObj = info;
                            break;
                        }
                    }
                    results.add(ri);
                    break; // One match per component
                }
            }
        }

        return results;
    }

    /**
     * Get the list of registered activities.
     */
    public List<ActivityInfo> getActivities() {
        return new ArrayList<>(mActivities);
    }

    /**
     * Get the list of registered services.
     */
    public List<ServiceInfo> getServices() {
        return new ArrayList<>(mServices);
    }

    /**
     * Get the number of registered activities.
     */
    public int getActivityCount() {
        return mActivities.size();
    }

    /**
     * Get the number of registered services.
     */
    public int getServiceCount() {
        return mServices.size();
    }

    // ── Internal ────────────────────────────────────────────────────────────

    private ResolveInfo resolveImplicit(Intent intent,
            Map<String, List<IntentFilter>> filterMap) {
        String action = intent.getAction();
        Set<String> categories = intent.getCategories();

        ResolveInfo bestMatch = null;
        int bestPriority = Integer.MIN_VALUE;

        for (Map.Entry<String, List<IntentFilter>> entry : filterMap.entrySet()) {
            for (IntentFilter filter : entry.getValue()) {
                if (matchesFilter(filter, action, categories)) {
                    if (filter.getPriority() > bestPriority) {
                        bestPriority = filter.getPriority();
                        bestMatch = new ResolveInfo();
                        // Set component info based on which map we're searching
                        if (filterMap == mActivityFilters) {
                            for (ActivityInfo info : mActivities) {
                                if (entry.getKey().equals(info.name)) {
                                    bestMatch.activityInfoObj = info;
                                    break;
                                }
                            }
                        } else {
                            for (ServiceInfo info : mServices) {
                                if (entry.getKey().equals(info.name)) {
                                    bestMatch.serviceInfoObj = info;
                                    break;
                                }
                            }
                        }
                        bestMatch.resolvedComponentName =
                                new ComponentName(mPackageName, entry.getKey());
                    }
                }
            }
        }

        return bestMatch;
    }

    /**
     * Check if an IntentFilter matches the given action and categories.
     * Matches Android's intent resolution rules:
     * - Action must match (if filter has actions)
     * - All intent categories must be present in the filter
     */
    private boolean matchesFilter(IntentFilter filter, String action, Set<String> categories) {
        // Action test: filter must contain the intent's action
        if (action != null && !filter.matchAction(action)) {
            return false;
        }
        // If intent has no action but filter requires one, no match
        if (action == null && filter.countActions() > 0) {
            return false;
        }

        // Category test: every category in the intent must be in the filter
        if (categories != null) {
            for (String cat : categories) {
                if (!filter.hasCategory(cat)) {
                    return false;
                }
            }
        }

        return true;
    }
}
