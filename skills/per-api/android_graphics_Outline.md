# SKILL: android.graphics.Outline

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Outline`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Outline` |
| **Package** | `android.graphics` |
| **Total Methods** | 18 |
| **Avg Score** | 1.8 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (33%) |
| **No Mapping** | 12 (66%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 18 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `offset` | 4 | partial | Log warning + no-op |
| `getRadius` | 4 | composite | Return safe default (null/false/0/empty) |
| `set` | 3 | composite | Log warning + no-op |
| `setAlpha` | 3 | composite | Log warning + no-op |
| `getRect` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAlpha` | 2 | composite | Return safe default (null/false/0/empty) |
| `Outline` | 1 | none | throw UnsupportedOperationException |
| `Outline` | 1 | none | throw UnsupportedOperationException |
| `canClip` | 1 | none | Return safe default (null/false/0/empty) |
| `isEmpty` | 1 | none | Return safe default (null/false/0/empty) |
| `setEmpty` | 1 | none | Log warning + no-op |
| `setOval` | 1 | none | Log warning + no-op |
| `setOval` | 1 | none | Log warning + no-op |
| `setPath` | 1 | none | Log warning + no-op |
| `setRect` | 1 | none | Log warning + no-op |
| `setRect` | 1 | none | Log warning + no-op |
| `setRoundRect` | 1 | none | Log warning + no-op |
| `setRoundRect` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Outline`:


## Quality Gates

Before marking `android.graphics.Outline` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
