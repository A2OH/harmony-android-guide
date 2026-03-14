# SKILL: android.provider.Settings.System

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.Settings.System`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.Settings.System` |
| **Package** | `android.provider.Settings` |
| **Total Methods** | 16 |
| **Avg Score** | 3.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 2 (12%) |
| **Partial/Composite** | 10 (62%) |
| **No Mapping** | 4 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 1 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getString` | `static String getString(android.content.ContentResolver, String)` | 7 | near | rewrite | `getValueSync` | `@ohos.settings.settings` |
| `putString` | `static boolean putString(android.content.ContentResolver, String, String)` | 7 | near | impossible | `setValueSync` | `@ohos.settings.settings` |
| `getLong` | `static long getLong(android.content.ContentResolver, String, long)` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getLong` | `static long getLong(android.content.ContentResolver, String) throws android.provider.Settings.SettingNotFoundException` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Gap Descriptions (per method)

- **`getString`**: Flat key-value; no namespace
- **`putString`**: Direct

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getConfiguration` | 4 | composite | Return safe default (null/false/0/empty) |
| `putConfiguration` | 3 | composite | Log warning + no-op |
| `System` | 3 | composite | throw UnsupportedOperationException |
| `getInt` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInt` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFloat` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFloat` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUriFor` | 3 | composite | Return safe default (null/false/0/empty) |
| `canWrite` | 1 | none | Return safe default (null/false/0/empty) |
| `putFloat` | 1 | none | Log warning + no-op |
| `putInt` | 1 | none | Log warning + no-op |
| `putLong` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.Settings.System`:


## Quality Gates

Before marking `android.provider.Settings.System` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
