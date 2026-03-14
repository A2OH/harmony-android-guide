# SKILL: android.app.AlarmManager.AlarmClockInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AlarmManager.AlarmClockInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AlarmManager.AlarmClockInfo` |
| **Package** | `android.app.AlarmManager` |
| **Total Methods** | 5 |
| **Avg Score** | 2.0 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (60%) |
| **No Mapping** | 2 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getShowIntent` | 3 | composite | Return safe default (null/false/0/empty) |
| `AlarmClockInfo` | 3 | composite | throw UnsupportedOperationException |
| `getTriggerTime` | 3 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.AlarmManager.AlarmClockInfo`:


## Quality Gates

Before marking `android.app.AlarmManager.AlarmClockInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
