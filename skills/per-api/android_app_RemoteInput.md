# SKILL: android.app.RemoteInput

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.RemoteInput`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.RemoteInput` |
| **Package** | `android.app` |
| **Total Methods** | 16 |
| **Avg Score** | 2.5 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (62%) |
| **No Mapping** | 6 (37%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 16 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getExtras` | 4 | partial | Return safe default (null/false/0/empty) |
| `getChoices` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLabel` | 4 | partial | Return safe default (null/false/0/empty) |
| `getResultsFromIntent` | 4 | partial | Return safe default (null/false/0/empty) |
| `getDataResultsFromIntent` | 4 | partial | Return safe default (null/false/0/empty) |
| `setResultsSource` | 3 | composite | Log warning + no-op |
| `getAllowFreeFormInput` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAllowedDataTypes` | 3 | composite | Return safe default (null/false/0/empty) |
| `getResultKey` | 2 | composite | Return safe default (null/false/0/empty) |
| `getResultsSource` | 2 | composite | Return safe default (null/false/0/empty) |
| `addDataResultToIntent` | 1 | none | Log warning + no-op |
| `addResultsToIntent` | 1 | none | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `getEditChoicesBeforeSending` | 1 | none | Return safe default (null/false/0/empty) |
| `isDataOnly` | 1 | none | Return safe default (null/false/0/empty) |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.RemoteInput`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.RemoteInput` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
