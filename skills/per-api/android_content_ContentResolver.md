# SKILL: android.content.ContentResolver

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContentResolver`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContentResolver` |
| **Package** | `android.content` |
| **Total Methods** | 34 |
| **Avg Score** | 3.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 4 (11%) |
| **Partial/Composite** | 22 (64%) |
| **No Mapping** | 8 (23%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 5 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `bulkInsert` | `final int bulkInsert(@NonNull @RequiresPermission.Write android.net.Uri, @NonNull android.content.ContentValues[])` | 7 | near | hard | `batchInsert` | `@ohos.data.dataShare.DataShareHelper` |
| `notifyChange` | `void notifyChange(@NonNull android.net.Uri, @Nullable android.database.ContentObserver)` | 7 | near | hard | `notifyChange` | `@ohos.data.dataShare.DataShareHelper` |
| `notifyChange` | `void notifyChange(@NonNull android.net.Uri, @Nullable android.database.ContentObserver, int)` | 7 | near | hard | `notifyChange` | `@ohos.data.dataShare.DataShareHelper` |
| `notifyChange` | `void notifyChange(@NonNull java.util.Collection<android.net.Uri>, @Nullable android.database.ContentObserver, int)` | 7 | near | hard | `notifyChange` | `@ohos.data.dataShare.DataShareHelper` |
| `delete` | `final int delete(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable String, @Nullable String[])` | 5 | partial | hard | `delete` | `@ohos.data.dataShare.DataShareHelper` |
| `delete` | `final int delete(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable android.os.Bundle)` | 5 | partial | hard | `delete` | `@ohos.data.dataShare.DataShareHelper` |
| `registerContentObserver` | `final void registerContentObserver(@NonNull android.net.Uri, boolean, @NonNull android.database.ContentObserver)` | 5 | partial | rewrite | `on` | `@ohos.data.dataShare.DataShareHelper` |
| `update` | `final int update(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable android.content.ContentValues, @Nullable String, @Nullable String[])` | 5 | partial | rewrite | `update` | `@ohos.data.dataShare.DataShareHelper` |
| `update` | `final int update(@NonNull @RequiresPermission.Write android.net.Uri, @Nullable android.content.ContentValues, @Nullable android.os.Bundle)` | 5 | partial | rewrite | `update` | `@ohos.data.dataShare.DataShareHelper` |

## Gap Descriptions (per method)

- **`bulkInsert`**: Array of ValuesBuckets
- **`notifyChange`**: No observer param needed
- **`notifyChange`**: No observer param needed
- **`notifyChange`**: No observer param needed
- **`delete`**: Predicate-based
- **`delete`**: Predicate-based
- **`registerContentObserver`**: Event-based observer
- **`update`**: Predicate-based
- **`update`**: Predicate-based

## Stub APIs (score < 5): 25 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addPeriodicSync` | 4 | partial | Log warning + no-op |
| `unregisterContentObserver` | 4 | composite | Return safe default (null/false/0/empty) |
| `cancelSync` | 4 | composite | Return safe default (null/false/0/empty) |
| `cancelSync` | 4 | composite | Return safe default (null/false/0/empty) |
| `releasePersistableUriPermission` | 3 | composite | No-op |
| `takePersistableUriPermission` | 3 | composite | Return safe default (null/false/0/empty) |
| `removePeriodicSync` | 3 | composite | Log warning + no-op |
| `isSyncPending` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCurrentSyncs` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSyncAutomatically` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPeriodicSyncs` | 3 | composite | Return safe default (null/false/0/empty) |
| `requestSync` | 3 | composite | throw UnsupportedOperationException |
| `requestSync` | 3 | composite | throw UnsupportedOperationException |
| `getIsSyncable` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSyncAdapterTypes` | 2 | composite | Return safe default (null/false/0/empty) |
| `removeStatusChangeListener` | 2 | composite | Return safe default (null/false/0/empty) |
| `addStatusChangeListener` | 2 | composite | Return safe default (null/false/0/empty) |
| `ContentResolver` | 1 | none | Store callback, never fire |
| `getMasterSyncAutomatically` | 1 | none | Return safe default (null/false/0/empty) |
| `isSyncActive` | 1 | none | Return safe default (null/false/0/empty) |
| `refresh` | 1 | none | throw UnsupportedOperationException |
| `setIsSyncable` | 1 | none | Return safe default (null/false/0/empty) |
| `setMasterSyncAutomatically` | 1 | none | Log warning + no-op |
| `setSyncAutomatically` | 1 | none | Log warning + no-op |
| `validateSyncExtrasBundle` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContentResolver`:

- `android.net.Uri` (already shimmed)
- `android.content.ContentValues` (already shimmed)
- `android.database.Cursor` (already shimmed)

## Quality Gates

Before marking `android.content.ContentResolver` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 34 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
