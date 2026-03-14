# SKILL: android.app.VoiceInteractor.Prompt

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.VoiceInteractor.Prompt`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.VoiceInteractor.Prompt` |
| **Package** | `android.app.VoiceInteractor` |
| **Total Methods** | 12 |
| **Avg Score** | 2.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (50%) |
| **No Mapping** | 6 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onCancel` | `void onCancel()` | 5 | partial | moderate | `onCancel` | `onCancel?: (data: SubscribeCallbackData) => void` |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onDetached` | 5 | partial | Store callback, never fire |
| `getContext` | 5 | partial | Return safe default (null/false/0/empty) |
| `getActivity` | 4 | partial | Return safe default (null/false/0/empty) |
| `cancel` | 4 | partial | Return safe default (null/false/0/empty) |
| `getName` | 3 | composite | Return safe default (null/false/0/empty) |
| `Prompt` | 1 | none | throw UnsupportedOperationException |
| `Prompt` | 1 | none | throw UnsupportedOperationException |
| `countVoicePrompts` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |
| `onAttached` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.app.VoiceInteractor.Prompt`:


## Quality Gates

Before marking `android.app.VoiceInteractor.Prompt` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
