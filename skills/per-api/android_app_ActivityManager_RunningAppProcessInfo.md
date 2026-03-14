# SKILL: android.app.ActivityManager.RunningAppProcessInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.ActivityManager.RunningAppProcessInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.ActivityManager.RunningAppProcessInfo` |
| **Package** | `android.app.ActivityManager` |
| **Total Methods** | 5 |
| **Avg Score** | 2.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (40%) |
| **No Mapping** | 3 (60%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `RunningAppProcessInfo` | 5 | partial | throw UnsupportedOperationException |
| `RunningAppProcessInfo` | 5 | partial | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `readFromParcel` | 1 | none | Return safe default (null/false/0/empty) |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.ActivityManager.RunningAppProcessInfo`:


## Quality Gates

Before marking `android.app.ActivityManager.RunningAppProcessInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
