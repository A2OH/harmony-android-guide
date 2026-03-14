# SKILL: android.media.MediaSyncEvent

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaSyncEvent`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaSyncEvent` |
| **Package** | `android.media` |
| **Total Methods** | 4 |
| **Avg Score** | 4.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getType` | 5 | partial | Return safe default (null/false/0/empty) |
| `getAudioSessionId` | 5 | partial | Return safe default (null/false/0/empty) |
| `setAudioSessionId` | 5 | partial | Log warning + no-op |
| `createEvent` | 3 | composite | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaSyncEvent`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaSyncEvent` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
