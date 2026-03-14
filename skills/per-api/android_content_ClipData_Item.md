# SKILL: android.content.ClipData.Item

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ClipData.Item`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ClipData.Item` |
| **Package** | `android.content.ClipData` |
| **Total Methods** | 13 |
| **Avg Score** | 4.1 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 12 (92%) |
| **No Mapping** | 1 (7%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 13 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Item` | 5 | partial | throw UnsupportedOperationException |
| `Item` | 5 | partial | throw UnsupportedOperationException |
| `Item` | 5 | partial | throw UnsupportedOperationException |
| `Item` | 5 | partial | throw UnsupportedOperationException |
| `Item` | 5 | partial | throw UnsupportedOperationException |
| `Item` | 5 | partial | throw UnsupportedOperationException |
| `getText` | 5 | partial | Return safe default (null/false/0/empty) |
| `coerceToText` | 4 | partial | throw UnsupportedOperationException |
| `getHtmlText` | 4 | partial | Return safe default (null/false/0/empty) |
| `coerceToHtmlText` | 4 | partial | throw UnsupportedOperationException |
| `getIntent` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUri` | 3 | composite | Return safe default (null/false/0/empty) |
| `coerceToStyledText` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 13 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ClipData.Item`:


## Quality Gates

Before marking `android.content.ClipData.Item` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
