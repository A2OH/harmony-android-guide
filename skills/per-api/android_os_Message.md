# SKILL: android.os.Message

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Message`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Message` |
| **Package** | `android.os` |
| **Total Methods** | 23 |
| **Avg Score** | 2.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 12 (52%) |
| **No Mapping** | 11 (47%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Message` | `Message()` | 5 | partial | moderate | `message` | `readonly message: string` |

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getCallback` | 5 | partial | Return safe default (null/false/0/empty) |
| `getWhen` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTarget` | 5 | partial | Return safe default (null/false/0/empty) |
| `setTarget` | 5 | partial | Return safe default (null/false/0/empty) |
| `peekData` | 5 | partial | throw UnsupportedOperationException |
| `sendToTarget` | 4 | partial | Return safe default (null/false/0/empty) |
| `setData` | 4 | composite | Log warning + no-op |
| `setAsynchronous` | 3 | composite | Log warning + no-op |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `getData` | 3 | composite | Return safe default (null/false/0/empty) |
| `copyFrom` | 3 | composite | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `isAsynchronous` | 1 | none | Return safe default (null/false/0/empty) |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `recycle` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 22 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Message`:


## Quality Gates

Before marking `android.os.Message` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
