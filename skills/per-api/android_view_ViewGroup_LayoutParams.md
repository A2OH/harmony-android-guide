# SKILL: android.view.ViewGroup.LayoutParams

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ViewGroup.LayoutParams`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ViewGroup.LayoutParams` |
| **Package** | `android.view.ViewGroup` |
| **Total Methods** | 5 |
| **Avg Score** | 1.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 5 (100%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 2 |
| **Has Async Gap** | 2 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `resolveLayoutDirection` | 1 | none | Store callback, never fire |
| `setBaseAttributes` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ViewGroup.LayoutParams`:


## Quality Gates

Before marking `android.view.ViewGroup.LayoutParams` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
