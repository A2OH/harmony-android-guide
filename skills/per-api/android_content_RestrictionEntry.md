# SKILL: android.content.RestrictionEntry

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.RestrictionEntry`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.RestrictionEntry` |
| **Package** | `android.content` |
| **Total Methods** | 33 |
| **Avg Score** | 3.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 27 (81%) |
| **No Mapping** | 6 (18%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 33 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setType` | 5 | partial | Log warning + no-op |
| `getChoiceEntries` | 5 | partial | Return safe default (null/false/0/empty) |
| `setChoiceEntries` | 5 | partial | Log warning + no-op |
| `setChoiceEntries` | 5 | partial | Log warning + no-op |
| `RestrictionEntry` | 5 | partial | Store callback, never fire |
| `RestrictionEntry` | 5 | partial | Store callback, never fire |
| `RestrictionEntry` | 5 | partial | Store callback, never fire |
| `RestrictionEntry` | 5 | partial | Store callback, never fire |
| `RestrictionEntry` | 5 | partial | Store callback, never fire |
| `RestrictionEntry` | 5 | partial | Store callback, never fire |
| `getKey` | 5 | partial | Return safe default (null/false/0/empty) |
| `getIntValue` | 5 | partial | Return safe default (null/false/0/empty) |
| `setIntValue` | 5 | partial | Log warning + no-op |
| `getRestrictions` | 5 | partial | Return safe default (null/false/0/empty) |
| `getType` | 4 | partial | Return safe default (null/false/0/empty) |
| `getChoiceValues` | 4 | partial | Return safe default (null/false/0/empty) |
| `getSelectedState` | 4 | partial | Return safe default (null/false/0/empty) |
| `setRestrictions` | 4 | composite | Log warning + no-op |
| `setDescription` | 3 | composite | Log warning + no-op |
| `setSelectedState` | 3 | composite | Log warning + no-op |
| `setSelectedString` | 3 | composite | Log warning + no-op |
| `getDescription` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitle` | 3 | composite | Return safe default (null/false/0/empty) |
| `createBundleEntry` | 3 | composite | Return dummy instance / no-op |
| `getSelectedString` | 3 | composite | Return safe default (null/false/0/empty) |
| `createBundleArrayEntry` | 2 | composite | Return dummy instance / no-op |
| `getAllSelectedStrings` | 2 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `setAllSelectedStrings` | 1 | none | Log warning + no-op |
| `setChoiceValues` | 1 | none | Log warning + no-op |
| `setChoiceValues` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.content.RestrictionEntry`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.RestrictionEntry` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 33 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
