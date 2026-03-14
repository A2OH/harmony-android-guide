# SKILL: android.app.Notification.MessagingStyle.Message

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Notification.MessagingStyle.Message`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Notification.MessagingStyle.Message` |
| **Package** | `android.app.Notification.MessagingStyle` |
| **Total Methods** | 14 |
| **Avg Score** | 2.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 8 (57%) |
| **No Mapping** | 6 (42%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Style` | 5 | partial | throw UnsupportedOperationException |
| `getText` | 5 | partial | Return safe default (null/false/0/empty) |
| `Message` | 5 | partial | throw UnsupportedOperationException |
| `getExtras` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTimestamp` | 4 | partial | Return safe default (null/false/0/empty) |
| `getDataMimeType` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDataUri` | 3 | composite | Return safe default (null/false/0/empty) |
| `setData` | 2 | composite | Log warning + no-op |
| `build` | 1 | none | throw UnsupportedOperationException |
| `checkBuilder` | 1 | none | throw UnsupportedOperationException |
| `getStandardView` | 1 | none | Return safe default (null/false/0/empty) |
| `internalSetBigContentTitle` | 1 | none | Log warning + no-op |
| `internalSetSummaryText` | 1 | none | Log warning + no-op |
| `setBuilder` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 14 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Notification.MessagingStyle.Message`:


## Quality Gates

Before marking `android.app.Notification.MessagingStyle.Message` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
