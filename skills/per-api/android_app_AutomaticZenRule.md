# SKILL: android.app.AutomaticZenRule

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AutomaticZenRule`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AutomaticZenRule` |
| **Package** | `android.app` |
| **Total Methods** | 17 |
| **Avg Score** | 2.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (64%) |
| **No Mapping** | 6 (35%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isEnabled` | `boolean isEnabled()` | 5 | partial | moderate | `isEnabled` | `readonly isEnabled?: boolean` |

## Stub APIs (score < 5): 16 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getZenPolicy` | 4 | partial | Return safe default (null/false/0/empty) |
| `setConfigurationActivity` | 3 | composite | Log warning + no-op |
| `setName` | 3 | composite | Log warning + no-op |
| `setZenPolicy` | 3 | composite | Log warning + no-op |
| `getCreationTime` | 3 | composite | Return safe default (null/false/0/empty) |
| `getName` | 3 | composite | Return safe default (null/false/0/empty) |
| `getConditionId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOwner` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInterruptionFilter` | 3 | composite | Return safe default (null/false/0/empty) |
| `setInterruptionFilter` | 2 | composite | Log warning + no-op |
| `AutomaticZenRule` | 1 | none | throw UnsupportedOperationException |
| `AutomaticZenRule` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `setConditionId` | 1 | none | Log warning + no-op |
| `setEnabled` | 1 | none | Log warning + no-op |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.AutomaticZenRule`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.AutomaticZenRule` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
