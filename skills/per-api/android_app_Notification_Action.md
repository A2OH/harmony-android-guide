# SKILL: android.app.Notification.Action

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Notification.Action`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Notification.Action` |
| **Package** | `android.app.Notification` |
| **Total Methods** | 10 |
| **Avg Score** | 2.1 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (40%) |
| **No Mapping** | 6 (60%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getIcon` | 4 | partial | Return safe default (null/false/0/empty) |
| `getExtras` | 4 | partial | Return safe default (null/false/0/empty) |
| `getRemoteInputs` | 4 | partial | Return safe default (null/false/0/empty) |
| `getSemanticAction` | 2 | composite | Return safe default (null/false/0/empty) |
| `clone` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |
| `getAllowGeneratedReplies` | 1 | none | Return safe default (null/false/0/empty) |
| `getDataOnlyRemoteInputs` | 1 | none | Return safe default (null/false/0/empty) |
| `isContextual` | 1 | none | Return safe default (null/false/0/empty) |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Notification.Action`:


## Quality Gates

Before marking `android.app.Notification.Action` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
