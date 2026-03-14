# SKILL: android.os.DropBoxManager.Entry

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.DropBoxManager.Entry`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.DropBoxManager.Entry` |
| **Package** | `android.os.DropBoxManager` |
| **Total Methods** | 13 |
| **Avg Score** | 2.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (53%) |
| **No Mapping** | 6 (46%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 13 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getText` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTag` | 4 | partial | Return safe default (null/false/0/empty) |
| `getFlags` | 4 | partial | Return safe default (null/false/0/empty) |
| `close` | 4 | partial | No-op |
| `getTimeMillis` | 4 | composite | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `getInputStream` | 3 | composite | Return safe default (null/false/0/empty) |
| `Entry` | 1 | none | throw UnsupportedOperationException |
| `Entry` | 1 | none | throw UnsupportedOperationException |
| `Entry` | 1 | none | throw UnsupportedOperationException |
| `Entry` | 1 | none | throw UnsupportedOperationException |
| `Entry` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 13 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.DropBoxManager.Entry`:


## Quality Gates

Before marking `android.os.DropBoxManager.Entry` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
