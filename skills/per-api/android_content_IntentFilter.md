# SKILL: android.content.IntentFilter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.IntentFilter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.IntentFilter` |
| **Package** | `android.content` |
| **Total Methods** | 53 |
| **Avg Score** | 1.9 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 22 (41%) |
| **No Mapping** | 31 (58%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 1 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `addAction` | `final void addAction(String)` | 5 | partial | impossible | `events` | `@ohos.commonEventManager.CommonEventSubscribeInfo` |

## Gap Descriptions (per method)

- **`addAction`**: Event-name list vs action filter

## Stub APIs (score < 5): 52 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getAction` | 5 | partial | Return safe default (null/false/0/empty) |
| `getCategory` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasDataType` | 4 | partial | Return safe default (null/false/0/empty) |
| `addDataPath` | 4 | partial | Log warning + no-op |
| `addDataType` | 4 | partial | Log warning + no-op |
| `getPriority` | 3 | composite | Return safe default (null/false/0/empty) |
| `create` | 3 | composite | Return dummy instance / no-op |
| `addDataScheme` | 3 | composite | Log warning + no-op |
| `countDataPaths` | 3 | composite | throw UnsupportedOperationException |
| `countDataAuthorities` | 3 | composite | throw UnsupportedOperationException |
| `getDataType` | 3 | composite | Return safe default (null/false/0/empty) |
| `matchData` | 3 | composite | throw UnsupportedOperationException |
| `setPriority` | 3 | composite | Log warning + no-op |
| `getDataPath` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDataScheme` | 3 | composite | Return safe default (null/false/0/empty) |
| `hasDataPath` | 2 | composite | Return safe default (null/false/0/empty) |
| `countDataSchemes` | 2 | composite | throw UnsupportedOperationException |
| `hasDataScheme` | 2 | composite | Return safe default (null/false/0/empty) |
| `countDataTypes` | 2 | composite | throw UnsupportedOperationException |
| `getDataSchemeSpecificPart` | 2 | composite | Return safe default (null/false/0/empty) |
| `getDataAuthority` | 2 | composite | Return safe default (null/false/0/empty) |
| `IntentFilter` | 1 | none | throw UnsupportedOperationException |
| `IntentFilter` | 1 | none | throw UnsupportedOperationException |
| `IntentFilter` | 1 | none | throw UnsupportedOperationException |
| `IntentFilter` | 1 | none | throw UnsupportedOperationException |
| `actionsIterator` | 1 | none | Store callback, never fire |
| `addCategory` | 1 | none | Log warning + no-op |
| `addDataAuthority` | 1 | none | Log warning + no-op |
| `addDataSchemeSpecificPart` | 1 | none | Log warning + no-op |
| `authoritiesIterator` | 1 | none | throw UnsupportedOperationException |
| `categoriesIterator` | 1 | none | throw UnsupportedOperationException |
| `countActions` | 1 | none | Store callback, never fire |
| `countCategories` | 1 | none | throw UnsupportedOperationException |
| `countDataSchemeSpecificParts` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `hasAction` | 1 | none | Return safe default (null/false/0/empty) |
| `hasCategory` | 1 | none | Return safe default (null/false/0/empty) |
| `hasDataAuthority` | 1 | none | Return safe default (null/false/0/empty) |
| `hasDataSchemeSpecificPart` | 1 | none | Return safe default (null/false/0/empty) |
| `match` | 1 | none | throw UnsupportedOperationException |
| `match` | 1 | none | throw UnsupportedOperationException |
| `matchAction` | 1 | none | Store callback, never fire |
| `matchCategories` | 1 | none | throw UnsupportedOperationException |
| `matchDataAuthority` | 1 | none | throw UnsupportedOperationException |
| `pathsIterator` | 1 | none | throw UnsupportedOperationException |
| `readFromXml` | 1 | none | Return safe default (null/false/0/empty) |
| `schemeSpecificPartsIterator` | 1 | none | throw UnsupportedOperationException |
| `schemesIterator` | 1 | none | throw UnsupportedOperationException |
| `typesIterator` | 1 | none | throw UnsupportedOperationException |
| `writeToParcel` | 1 | none | Log warning + no-op |
| `writeToXml` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.content.IntentFilter`:

- `android.content.Intent` (already shimmed)

## Quality Gates

Before marking `android.content.IntentFilter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 53 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
