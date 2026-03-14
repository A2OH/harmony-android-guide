# SKILL: android.graphics.Point

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Point`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Point` |
| **Package** | `android.graphics` |
| **Total Methods** | 10 |
| **Avg Score** | 2.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (60%) |
| **No Mapping** | 4 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Point` | 5 | partial | throw UnsupportedOperationException |
| `Point` | 5 | partial | throw UnsupportedOperationException |
| `Point` | 5 | partial | throw UnsupportedOperationException |
| `offset` | 4 | partial | Log warning + no-op |
| `set` | 3 | composite | Log warning + no-op |
| `readFromParcel` | 3 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `equals` | 1 | none | throw UnsupportedOperationException |
| `negate` | 1 | none | throw UnsupportedOperationException |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 10 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Point`:


## Quality Gates

Before marking `android.graphics.Point` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
