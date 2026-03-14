# SKILL: android.util.Range<T

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Range<T`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Range<T` |
| **Package** | `android.util` |
| **Total Methods** | 12 |
| **Avg Score** | 2.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (50%) |
| **No Mapping** | 6 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Range` | 5 | partial | throw UnsupportedOperationException |
| `contains` | 4 | partial | Store callback, never fire |
| `contains` | 4 | partial | Store callback, never fire |
| `create` | 4 | composite | Return dummy instance / no-op |
| `getUpper` | 3 | composite | Return safe default (null/false/0/empty) |
| `getLower` | 3 | composite | Return safe default (null/false/0/empty) |
| `clamp` | 1 | none | throw UnsupportedOperationException |
| `extend` | 1 | none | throw UnsupportedOperationException |
| `extend` | 1 | none | throw UnsupportedOperationException |
| `extend` | 1 | none | throw UnsupportedOperationException |
| `intersect` | 1 | none | throw UnsupportedOperationException |
| `intersect` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Range<T`:


## Quality Gates

Before marking `android.util.Range<T` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
