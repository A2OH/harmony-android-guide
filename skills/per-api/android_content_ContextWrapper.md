# SKILL: android.content.ContextWrapper

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContextWrapper`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContextWrapper` |
| **Package** | `android.content` |
| **Total Methods** | 94 |
| **Avg Score** | 2.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 74 (78%) |
| **No Mapping** | 20 (21%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `moveDatabaseFrom` | `boolean moveDatabaseFrom(android.content.Context, String)` | 5 | partial | moderate | `moveToLast` | `moveToLast(): boolean` |

## Stub APIs (score < 5): 93 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `ContextWrapper` | 5 | partial | Store callback, never fire |
| `getBaseContext` | 5 | partial | Return safe default (null/false/0/empty) |
| `fileList` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAssets` | 4 | partial | Return safe default (null/false/0/empty) |
| `getContentResolver` | 4 | partial | Return safe default (null/false/0/empty) |
| `getCacheDir` | 4 | partial | Return safe default (null/false/0/empty) |
| `createDisplayContext` | 4 | partial | Return dummy instance / no-op |
| `createPackageContext` | 4 | partial | Return dummy instance / no-op |
| `grantUriPermission` | 4 | partial | Return safe default (null/false/0/empty) |
| `revokeUriPermission` | 4 | partial | Return safe default (null/false/0/empty) |
| `revokeUriPermission` | 4 | partial | Return safe default (null/false/0/empty) |
| `attachBaseContext` | 4 | partial | Store callback, never fire |
| `createContextForSplit` | 4 | partial | Return dummy instance / no-op |
| `enforceUriPermission` | 4 | composite | Return safe default (null/false/0/empty) |
| `enforceUriPermission` | 4 | composite | Return safe default (null/false/0/empty) |
| `startActivity` | 4 | composite | Return dummy instance / no-op |
| `startActivity` | 4 | composite | Return dummy instance / no-op |
| `enforcePermission` | 4 | composite | Return safe default (null/false/0/empty) |
| `enforceCallingUriPermission` | 4 | composite | Return safe default (null/false/0/empty) |
| `startActivities` | 3 | composite | Return dummy instance / no-op |
| `startActivities` | 3 | composite | Return dummy instance / no-op |
| `enforceCallingPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `enforceCallingOrSelfUriPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `unregisterReceiver` | 3 | composite | Return safe default (null/false/0/empty) |
| `enforceCallingOrSelfPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `deleteSharedPreferences` | 3 | composite | throw UnsupportedOperationException |
| `deleteDatabase` | 3 | composite | throw UnsupportedOperationException |
| `getSharedPreferences` | 3 | composite | Return safe default (null/false/0/empty) |
| `checkUriPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `checkUriPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `deleteFile` | 3 | composite | throw UnsupportedOperationException |
| `moveSharedPreferencesFrom` | 3 | composite | throw UnsupportedOperationException |
| `getDir` | 3 | composite | Return safe default (null/false/0/empty) |
| `createConfigurationContext` | 3 | composite | Return dummy instance / no-op |
| `checkPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `checkCallingUriPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `getApplicationContext` | 3 | composite | Return safe default (null/false/0/empty) |
| `getResources` | 3 | composite | Return safe default (null/false/0/empty) |
| `checkSelfPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPackageName` | 3 | composite | Return safe default (null/false/0/empty) |
| `isDeviceProtectedStorage` | 3 | composite | Return safe default (null/false/0/empty) |
| `checkCallingOrSelfUriPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `checkCallingPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `getClassLoader` | 3 | composite | Return safe default (null/false/0/empty) |
| `getApplicationInfo` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFilesDir` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDatabasePath` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDataDir` | 3 | composite | Return safe default (null/false/0/empty) |
| `startForegroundService` | 3 | composite | Return dummy instance / no-op |
| `getExternalCacheDirs` | 3 | composite | Return safe default (null/false/0/empty) |
| `startService` | 3 | composite | Return dummy instance / no-op |
| `openOrCreateDatabase` | 3 | composite | Return dummy instance / no-op |
| `openOrCreateDatabase` | 3 | composite | Return dummy instance / no-op |
| `getObbDir` | 2 | composite | Return safe default (null/false/0/empty) |
| `getTheme` | 2 | composite | Return safe default (null/false/0/empty) |
| `registerReceiver` | 2 | composite | Return safe default (null/false/0/empty) |
| `registerReceiver` | 2 | composite | Return safe default (null/false/0/empty) |
| `registerReceiver` | 2 | composite | Return safe default (null/false/0/empty) |
| `registerReceiver` | 2 | composite | Return safe default (null/false/0/empty) |
| `getMainLooper` | 2 | composite | Return safe default (null/false/0/empty) |
| `getPackageResourcePath` | 2 | composite | Return safe default (null/false/0/empty) |
| `checkCallingOrSelfPermission` | 2 | composite | Return safe default (null/false/0/empty) |
| `getPackageManager` | 2 | composite | Return safe default (null/false/0/empty) |
| `getExternalCacheDir` | 2 | composite | Return safe default (null/false/0/empty) |
| `getExternalFilesDir` | 2 | composite | Return safe default (null/false/0/empty) |
| `getExternalMediaDirs` | 2 | composite | Return safe default (null/false/0/empty) |
| `stopService` | 2 | composite | No-op |
| `getObbDirs` | 2 | composite | Return safe default (null/false/0/empty) |
| `getPackageCodePath` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSystemService` | 2 | composite | Return safe default (null/false/0/empty) |
| `getExternalFilesDirs` | 2 | composite | Return safe default (null/false/0/empty) |
| `getFileStreamPath` | 2 | composite | Return safe default (null/false/0/empty) |
| `unbindService` | 2 | composite | throw UnsupportedOperationException |
| `bindService` | 1 | none | throw UnsupportedOperationException |
| `createDeviceProtectedStorageContext` | 1 | none | Return dummy instance / no-op |
| `databaseList` | 1 | none | Return safe default (null/false/0/empty) |
| `getCodeCacheDir` | 1 | none | Return safe default (null/false/0/empty) |
| `getNoBackupFilesDir` | 1 | none | Return safe default (null/false/0/empty) |
| `getSystemServiceName` | 1 | none | Return safe default (null/false/0/empty) |
| `openFileInput` | 1 | none | Return dummy instance / no-op |
| `openFileOutput` | 1 | none | Return dummy instance / no-op |
| `sendBroadcast` | 1 | none | throw UnsupportedOperationException |
| `sendBroadcast` | 1 | none | throw UnsupportedOperationException |
| `sendBroadcastAsUser` | 1 | none | throw UnsupportedOperationException |
| `sendBroadcastAsUser` | 1 | none | throw UnsupportedOperationException |
| `sendOrderedBroadcast` | 1 | none | throw UnsupportedOperationException |
| `sendOrderedBroadcast` | 1 | none | throw UnsupportedOperationException |
| `sendOrderedBroadcast` | 1 | none | throw UnsupportedOperationException |
| `sendOrderedBroadcastAsUser` | 1 | none | throw UnsupportedOperationException |
| `setTheme` | 1 | none | Log warning + no-op |
| `startInstrumentation` | 1 | none | Return dummy instance / no-op |
| `startIntentSender` | 1 | none | Return dummy instance / no-op |
| `startIntentSender` | 1 | none | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContextWrapper`:

- `android.content.Context` (already shimmed)
- `android.content.Intent` (already shimmed)

## Quality Gates

Before marking `android.content.ContextWrapper` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 94 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
