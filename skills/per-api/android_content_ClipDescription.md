# SKILL: android.content.ClipDescription

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ClipDescription`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ClipDescription` |
| **Package** | `android.content` |
| **Total Methods** | 13 |
| **Avg Score** | 2.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (53%) |
| **No Mapping** | 6 (46%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getMimeTypeCount` | `int getMimeTypeCount()` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getExtras` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasMimeType` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLabel` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTimestamp` | 4 | partial | Return safe default (null/false/0/empty) |
| `setExtras` | 3 | composite | Log warning + no-op |
| `getMimeType` | 3 | composite | Return safe default (null/false/0/empty) |
| `ClipDescription` | 1 | none | Store callback, never fire |
| `ClipDescription` | 1 | none | Store callback, never fire |
| `compareMimeTypes` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `filterMimeTypes` | 1 | none | throw UnsupportedOperationException |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 12 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ClipDescription`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ClipDescription` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
