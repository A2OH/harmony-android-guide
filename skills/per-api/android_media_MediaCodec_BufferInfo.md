# SKILL: android.media.MediaCodec.BufferInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodec.BufferInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodec.BufferInfo` |
| **Package** | `android.media.MediaCodec` |
| **Total Methods** | 7 |
| **Avg Score** | 2.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (57%) |
| **No Mapping** | 3 (42%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `Callback` | `MediaCodec.Callback()` | 5 | partial | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `BufferInfo` | 5 | partial | throw UnsupportedOperationException |
| `set` | 3 | composite | Log warning + no-op |
| `onOutputBufferAvailable` | 2 | composite | Log warning + no-op |
| `onError` | 1 | none | Store callback, never fire |
| `onInputBufferAvailable` | 1 | none | Log warning + no-op |
| `onOutputFormatChanged` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodec.BufferInfo`:


## Quality Gates

Before marking `android.media.MediaCodec.BufferInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
