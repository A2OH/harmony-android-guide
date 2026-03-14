# SKILL: android.text.Selection

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.Selection`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.Selection` |
| **Package** | `android.text` |
| **Total Methods** | 19 |
| **Avg Score** | 1.1 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (5%) |
| **No Mapping** | 18 (94%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 19 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getSelectionEnd` | 2 | composite | Return safe default (null/false/0/empty) |
| `extendDown` | 1 | none | throw UnsupportedOperationException |
| `extendLeft` | 1 | none | throw UnsupportedOperationException |
| `extendRight` | 1 | none | throw UnsupportedOperationException |
| `extendSelection` | 1 | none | Store callback, never fire |
| `extendToLeftEdge` | 1 | none | throw UnsupportedOperationException |
| `extendToRightEdge` | 1 | none | throw UnsupportedOperationException |
| `extendUp` | 1 | none | throw UnsupportedOperationException |
| `getSelectionStart` | 1 | none | Return dummy instance / no-op |
| `moveDown` | 1 | none | throw UnsupportedOperationException |
| `moveLeft` | 1 | none | throw UnsupportedOperationException |
| `moveRight` | 1 | none | throw UnsupportedOperationException |
| `moveToLeftEdge` | 1 | none | throw UnsupportedOperationException |
| `moveToRightEdge` | 1 | none | throw UnsupportedOperationException |
| `moveUp` | 1 | none | throw UnsupportedOperationException |
| `removeSelection` | 1 | none | Log warning + no-op |
| `selectAll` | 1 | none | throw UnsupportedOperationException |
| `setSelection` | 1 | none | Log warning + no-op |
| `setSelection` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.text.Selection`:


## Quality Gates

Before marking `android.text.Selection` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
