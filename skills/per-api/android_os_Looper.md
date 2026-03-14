# SKILL: android.os.Looper

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Looper`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Looper` |
| **Package** | `android.os` |
| **Total Methods** | 8 |
| **Avg Score** | 2.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 5 (62%) |
| **No Mapping** | 3 (37%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `loop` | `static void loop()` | 5 | partial | moderate | `loop` | `loop: boolean` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isCurrentThread` | 4 | partial | Return safe default (null/false/0/empty) |
| `setMessageLogging` | 4 | partial | Log warning + no-op |
| `prepare` | 4 | partial | throw UnsupportedOperationException |
| `getMainLooper` | 3 | composite | Return safe default (null/false/0/empty) |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `quit` | 1 | none | throw UnsupportedOperationException |
| `quitSafely` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Looper`:


## Quality Gates

Before marking `android.os.Looper` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
