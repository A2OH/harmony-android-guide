# SKILL: android.location.GnssNavigationMessage

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.GnssNavigationMessage`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.GnssNavigationMessage` |
| **Package** | `android.location` |
| **Total Methods** | 10 |
| **Avg Score** | 4.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 9 (90%) |
| **No Mapping** | 1 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Callback` | `GnssNavigationMessage.Callback()` | 6 | partial | moderate | `callback` | `callback?: () => void` |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getType` | 5 | partial | Return safe default (null/false/0/empty) |
| `getSvid` | 5 | partial | Return safe default (null/false/0/empty) |
| `getStatus` | 5 | partial | Return safe default (null/false/0/empty) |
| `getMessageId` | 5 | partial | Return safe default (null/false/0/empty) |
| `onStatusChanged` | 5 | partial | Store callback, never fire |
| `getSubmessageId` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `onGnssNavigationMessageReceived` | 3 | composite | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 9 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.GnssNavigationMessage`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.GnssNavigationMessage` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
