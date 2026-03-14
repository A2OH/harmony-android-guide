# SKILL: android.content.ContentProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContentProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContentProvider` |
| **Package** | `android.content` |
| **Total Methods** | 20 |
| **Avg Score** | 3.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 4 (20%) |
| **Partial/Composite** | 9 (45%) |
| **No Mapping** | 7 (35%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `delete` | `abstract int delete(@NonNull android.net.Uri, @Nullable String, @Nullable String[])` | 9 | direct | easy | `delete` | `@ohos.data.dataShare.DataShareExtensionAbility` |
| `delete` | `int delete(@NonNull android.net.Uri, @Nullable android.os.Bundle)` | 9 | direct | easy | `delete` | `@ohos.data.dataShare.DataShareExtensionAbility` |
| `update` | `abstract int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable String, @Nullable String[])` | 9 | direct | easy | `update` | `@ohos.data.dataShare.DataShareExtensionAbility` |
| `update` | `int update(@NonNull android.net.Uri, @Nullable android.content.ContentValues, @Nullable android.os.Bundle)` | 9 | direct | easy | `update` | `@ohos.data.dataShare.DataShareExtensionAbility` |

## Gap Descriptions (per method)

- **`delete`**: DataShare
- **`delete`**: DataShare
- **`update`**: DataShare
- **`update`**: DataShare

## Stub APIs (score < 5): 16 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setWritePermission` | 4 | composite | Return safe default (null/false/0/empty) |
| `setReadPermission` | 4 | composite | Return safe default (null/false/0/empty) |
| `onConfigurationChanged` | 3 | composite | Store callback, never fire |
| `shutdown` | 3 | composite | throw UnsupportedOperationException |
| `bulkInsert` | 3 | composite | throw UnsupportedOperationException |
| `onCreate` | 3 | composite | Return dummy instance / no-op |
| `attachInfo` | 3 | composite | throw UnsupportedOperationException |
| `isTemporary` | 3 | composite | Return safe default (null/false/0/empty) |
| `setPathPermissions` | 2 | composite | Return safe default (null/false/0/empty) |
| `ContentProvider` | 1 | none | Store callback, never fire |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `onCallingPackageChanged` | 1 | none | Store callback, never fire |
| `onLowMemory` | 1 | none | Store callback, never fire |
| `onTrimMemory` | 1 | none | Store callback, never fire |
| `refresh` | 1 | none | throw UnsupportedOperationException |
| `restoreCallingIdentity` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContentProvider`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ContentProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
