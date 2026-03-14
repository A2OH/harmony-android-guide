# SKILL: android.media.MediaParser.InputReader

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaParser.InputReader`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaParser.InputReader` |
| **Package** | `android.media.MediaParser` |
| **Total Methods** | 3 |
| **Avg Score** | 4.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getPosition` | `long getPosition()` | 5 | partial | moderate | `getPosition` | `getPosition(): number` |
| `read` | `int read(@NonNull byte[], int, int) throws java.io.IOException` | 5 | partial | moderate | `read` | `read(): Promise<number[]>` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getLength` | 4 | partial | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaParser.InputReader`:


## Quality Gates

Before marking `android.media.MediaParser.InputReader` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
