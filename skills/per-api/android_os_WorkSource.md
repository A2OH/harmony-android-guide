# SKILL: android.os.WorkSource

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.WorkSource`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.WorkSource` |
| **Package** | `android.os` |
| **Total Methods** | 9 |
| **Avg Score** | 3.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (77%) |
| **No Mapping** | 2 (22%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clear` | `void clear()` | 6 | partial | moderate | `clear` | `clear(): void` |
| `add` | `boolean add(android.os.WorkSource)` | 5 | partial | moderate | `add` | `add: (bundleName: string, userId: number) => void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `WorkSource` | 5 | partial | throw UnsupportedOperationException |
| `WorkSource` | 5 | partial | throw UnsupportedOperationException |
| `set` | 4 | partial | Log warning + no-op |
| `remove` | 3 | composite | Log warning + no-op |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `diff` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.WorkSource`:


## Quality Gates

Before marking `android.os.WorkSource` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
