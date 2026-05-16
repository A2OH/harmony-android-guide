/*
 * IntentWantConverter.java
 *
 * Bidirectional converter between Android Intent and OpenHarmony Want.
 *
 * Android Intent fields: action / category / data(URI) / extras(Bundle) / component(pkg/cls)
 * OH Want fields:        bundleName / abilityName / action / entities / parameters(json)
 */
package adapter.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class IntentWantConverter {

    private static final String TAG = "OH_IntentWantConverter";

    /**
     * Extracted key fields from Android Intent, ready for JNI layer to construct OH Want.
     */
    public static class WantParams {
        public String bundleName;     // Maps to OH Want.bundleName
        public String abilityName;    // Maps to OH Want.abilityName
        public String action;         // Maps to OH Want.action
        public String uri;            // Maps to OH Want.uri
        public String extrasJson;     // Extras serialized as JSON, maps to OH Want.parameters
    }

    /**
     * Convert Android Intent to WantParams.
     *
     * Mapping rules:
     * - Intent.component.packageName  -> Want.bundleName
     * - Intent.component.className    -> Want.abilityName
     * - Intent.action                 -> Want.action (requires action mapping table)
     * - Intent.data (URI)             -> Want.uri
     * - Intent.extras (Bundle)        -> Want.parameters (JSON)
     */
    public static WantParams intentToWant(Intent intent) {
        WantParams params = new WantParams();

        // 1. Component mapping
        ComponentName component = intent.getComponent();
        if (component != null) {
            params.bundleName = mapPackageToBundleName(component.getPackageName());
            params.abilityName = mapClassToAbilityName(component.getClassName());
        }

        // 2. Action mapping
        params.action = mapAction(intent.getAction());

        // 3. URI mapping
        Uri data = intent.getData();
        if (data != null) {
            params.uri = data.toString();
        }

        // 4. Extras -> JSON
        Bundle extras = intent.getExtras();
        if (extras != null) {
            params.extrasJson = bundleToJson(extras);
        }

        Log.d(TAG, "Intent -> Want: bundle=" + params.bundleName
                + ", ability=" + params.abilityName
                + ", action=" + params.action);

        return params;
    }

    /**
     * Construct an Android Intent from OH callback parameters (reverse conversion).
     */
    public static Intent wantToIntent(String bundleName, String abilityName,
                                      String action, String uri, String paramsJson) {
        Intent intent = new Intent();

        // 1. Reverse component mapping
        if (bundleName != null && abilityName != null) {
            String packageName = mapBundleToPackageName(bundleName);
            String className = mapAbilityToClassName(abilityName);
            intent.setComponent(new ComponentName(packageName, className));
        }

        // 2. Reverse action mapping
        if (action != null) {
            intent.setAction(reverseMapAction(action));
        }

        // 3. URI
        if (uri != null) {
            intent.setData(Uri.parse(uri));
        }

        // 4. JSON -> Extras
        if (paramsJson != null) {
            Bundle extras = jsonToBundle(paramsJson);
            if (extras != null) {
                intent.putExtras(extras);
            }
        }

        return intent;
    }

    // ==================== Mapping Rules ====================

    /**
     * Android packageName -> OH bundleName mapping.
     * Phase 1: direct passthrough (configurable mapping table in later phases).
     */
    private static String mapPackageToBundleName(String packageName) {
        return packageName;
    }

    private static String mapBundleToPackageName(String bundleName) {
        return bundleName;
    }

    /**
     * Android Activity className -> OH abilityName mapping.
     * Uses the simple class name with "Ability" suffix.
     */
    private static String mapClassToAbilityName(String className) {
        int lastDot = className.lastIndexOf('.');
        if (lastDot >= 0) {
            return className.substring(lastDot + 1) + "Ability";
        }
        return className + "Ability";
    }

    private static String mapAbilityToClassName(String abilityName) {
        if (abilityName.endsWith("Ability")) {
            return abilityName.substring(0, abilityName.length() - "Ability".length());
        }
        return abilityName;
    }

    /**
     * Public API: convert OH abilityName to Android className.
     * Used by reverse callback bridges (e.g., AppSchedulerBridge) when
     * OH system launches an ability and we need the Android Activity class name.
     */
    public static String abilityNameToClassName(String abilityName) {
        return mapAbilityToClassName(abilityName);
    }

    /**
     * Public API: convert OH abilityName to fully-qualified Android className.
     * @param packageName Android package name (used as prefix if className is simple)
     * @param abilityName OH ability name
     */
    public static String abilityNameToClassName(String packageName, String abilityName) {
        String simpleName = mapAbilityToClassName(abilityName);
        if (simpleName.contains(".")) {
            return simpleName;
        }
        return packageName + "." + simpleName;
    }

    /**
     * Parse a Want JSON string (from C++ Want::ToString()) and construct an Android Intent.
     * JSON format: {"bundleName":"...","abilityName":"...","action":"...","uri":"...","params":{...}}
     */
    public static Intent wantJsonToIntent(String wantJson) {
        Intent intent = new Intent();
        if (wantJson == null || wantJson.isEmpty()) {
            return intent;
        }
        try {
            org.json.JSONObject json = new org.json.JSONObject(wantJson);
            String bundleName = json.optString("bundleName", null);
            String abilityName = json.optString("abilityName", null);

            if (bundleName == null) {
                // Try nested element format from Want::ToString()
                org.json.JSONObject element = json.optJSONObject("element");
                if (element != null) {
                    bundleName = element.optString("bundleName", null);
                    abilityName = element.optString("abilityName", null);
                }
            }

            if (bundleName != null && abilityName != null) {
                String packageName = mapBundleToPackageName(bundleName);
                String className = mapAbilityToClassName(abilityName);
                intent.setComponent(new ComponentName(packageName, className));
            }

            String action = json.optString("action", null);
            if (action != null) {
                intent.setAction(reverseMapAction(action));
            }

            String uri = json.optString("uri", null);
            if (uri != null && !uri.isEmpty()) {
                intent.setData(Uri.parse(uri));
            }

            String paramsJson = json.optString("params", null);
            if (paramsJson == null) {
                paramsJson = json.optString("android_extras_json", null);
            }
            if (paramsJson != null) {
                Bundle extras = jsonToBundle(paramsJson);
                if (extras != null) {
                    intent.putExtras(extras);
                }
            }
        } catch (org.json.JSONException e) {
            Log.w(TAG, "Failed to parse Want JSON: " + e.getMessage());
        }
        return intent;
    }

    /**
     * Android Action -> OH Action mapping table.
     */
    private static String mapAction(String androidAction) {
        if (androidAction == null) return null;

        switch (androidAction) {
            case Intent.ACTION_MAIN:
                return "ohos.want.action.home";
            case Intent.ACTION_VIEW:
                return "ohos.want.action.viewData";
            case Intent.ACTION_SEND:
                return "ohos.want.action.sendData";
            case Intent.ACTION_PICK:
                return "ohos.want.action.select";
            default:
                // Preserve original action with ohos prefix
                return "ohos.want.action." + androidAction;
        }
    }

    private static String reverseMapAction(String ohAction) {
        if (ohAction == null) return null;

        switch (ohAction) {
            case "ohos.want.action.home":
                return Intent.ACTION_MAIN;
            case "ohos.want.action.viewData":
                return Intent.ACTION_VIEW;
            case "ohos.want.action.sendData":
                return Intent.ACTION_SEND;
            case "ohos.want.action.select":
                return Intent.ACTION_PICK;
            default:
                return ohAction;
        }
    }

    // ==================== Serialization Utilities ====================

    private static String bundleToJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        try {
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                Object value = bundle.get(key);
                if (value instanceof String) {
                    json.put(key, (String) value);
                } else if (value instanceof Integer) {
                    json.put(key, (int) value);
                } else if (value instanceof Long) {
                    json.put(key, (long) value);
                } else if (value instanceof Boolean) {
                    json.put(key, (boolean) value);
                } else if (value instanceof Double) {
                    json.put(key, (double) value);
                } else if (value != null) {
                    json.put(key, value.toString());
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to convert Bundle to JSON", e);
        }
        return json.toString();
    }

    private static Bundle jsonToBundle(String jsonStr) {
        Bundle bundle = new Bundle();
        try {
            JSONObject json = new JSONObject(jsonStr);
            java.util.Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = json.get(key);
                if (value instanceof String) {
                    bundle.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    bundle.putInt(key, (int) value);
                } else if (value instanceof Long) {
                    bundle.putLong(key, (long) value);
                } else if (value instanceof Boolean) {
                    bundle.putBoolean(key, (boolean) value);
                } else if (value instanceof Double) {
                    bundle.putDouble(key, (double) value);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to convert JSON to Bundle", e);
            return null;
        }
        return bundle;
    }
}
