# SKILL: android.media.ToneGenerator

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.ToneGenerator`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.ToneGenerator` |
| **Package** | `android.media` |
| **Total Methods** | 7 |
| **Avg Score** | 6.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (57%) |
| **Partial/Composite** | 2 (28%) |
| **No Mapping** | 1 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 2 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 9 | direct | hard | `release` | `@ohos.multimedia.audio.TonePlayer` |
| `stopTone` | `void stopTone()` | 9 | direct | hard | `stop` | `@ohos.multimedia.audio.TonePlayer` |
| `startTone` | `boolean startTone(int)` | 7 | near | rewrite | `start` | `@ohos.multimedia.audio.TonePlayer` |
| `startTone` | `boolean startTone(int, int)` | 7 | near | rewrite | `start` | `@ohos.multimedia.audio.TonePlayer` |

## Gap Descriptions (per method)

- **`release`**: Direct equivalent
- **`stopTone`**: Direct equivalent
- **`startTone`**: Requires separate load then start
- **`startTone`**: Requires separate load then start

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `ToneGenerator` | 5 | partial | Store callback, never fire |
| `getAudioSessionId` | 4 | partial | Return safe default (null/false/0/empty) |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.ToneGenerator`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.ToneGenerator` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
