# SKILL: android.graphics.Region

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Region`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Region` |
| **Package** | `android.graphics` |
| **Total Methods** | 30 |
| **Avg Score** | 1.9 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (33%) |
| **No Mapping** | 20 (66%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 30 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Region` | 5 | partial | Store callback, never fire |
| `Region` | 5 | partial | Store callback, never fire |
| `Region` | 5 | partial | Store callback, never fire |
| `Region` | 5 | partial | Store callback, never fire |
| `contains` | 3 | composite | Store callback, never fire |
| `set` | 3 | composite | Log warning + no-op |
| `set` | 3 | composite | Log warning + no-op |
| `set` | 3 | composite | Log warning + no-op |
| `translate` | 3 | composite | throw UnsupportedOperationException |
| `translate` | 3 | composite | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `getBoundaryPath` | 1 | none | Return safe default (null/false/0/empty) |
| `getBounds` | 1 | none | Return safe default (null/false/0/empty) |
| `isComplex` | 1 | none | Return safe default (null/false/0/empty) |
| `isEmpty` | 1 | none | Return safe default (null/false/0/empty) |
| `isRect` | 1 | none | Return safe default (null/false/0/empty) |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `op` | 1 | none | throw UnsupportedOperationException |
| `quickContains` | 1 | none | Store callback, never fire |
| `quickContains` | 1 | none | Store callback, never fire |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `quickReject` | 1 | none | throw UnsupportedOperationException |
| `setEmpty` | 1 | none | Log warning + no-op |
| `setPath` | 1 | none | Log warning + no-op |
| `union` | 1 | none | Store callback, never fire |
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

Check if these related classes are already shimmed before generating `android.graphics.Region`:


## Quality Gates

Before marking `android.graphics.Region` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 30 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
