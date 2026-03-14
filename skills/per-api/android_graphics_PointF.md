# SKILL: android.graphics.PointF

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.PointF`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.PointF` |
| **Package** | `android.graphics` |
| **Total Methods** | 14 |
| **Avg Score** | 3.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (71%) |
| **No Mapping** | 4 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `length` | `final float length()` | 5 | partial | moderate | `length` | `length?: number` |
| `length` | `static float length(float, float)` | 5 | partial | moderate | `length` | `length?: number` |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `PointF` | 5 | partial | throw UnsupportedOperationException |
| `PointF` | 5 | partial | throw UnsupportedOperationException |
| `PointF` | 5 | partial | throw UnsupportedOperationException |
| `PointF` | 5 | partial | throw UnsupportedOperationException |
| `offset` | 4 | partial | Log warning + no-op |
| `set` | 3 | composite | Log warning + no-op |
| `set` | 3 | composite | Log warning + no-op |
| `readFromParcel` | 3 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `equals` | 1 | none | throw UnsupportedOperationException |
| `negate` | 1 | none | throw UnsupportedOperationException |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 12 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.PointF`:


## Quality Gates

Before marking `android.graphics.PointF` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
