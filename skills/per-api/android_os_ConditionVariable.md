# SKILL: android.os.ConditionVariable

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.ConditionVariable`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.ConditionVariable` |
| **Package** | `android.os` |
| **Total Methods** | 6 |
| **Avg Score** | 3.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (66%) |
| **No Mapping** | 2 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `ConditionVariable` | 4 | partial | Store callback, never fire |
| `ConditionVariable` | 4 | partial | Store callback, never fire |
| `close` | 4 | partial | No-op |
| `open` | 3 | composite | Return dummy instance / no-op |
| `block` | 1 | none | throw UnsupportedOperationException |
| `block` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 6 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.ConditionVariable`:


## Quality Gates

Before marking `android.os.ConditionVariable` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
