# SKILL: android.media.MediaCodec.LinearBlock

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodec.LinearBlock`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodec.LinearBlock` |
| **Package** | `android.media.MediaCodec` |
| **Total Methods** | 4 |
| **Avg Score** | 1.4 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (25%) |
| **No Mapping** | 3 (75%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isCodecCopyFreeCompatible` | 2 | composite | Return safe default (null/false/0/empty) |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `isMappable` | 1 | none | Return safe default (null/false/0/empty) |
| `recycle` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodec.LinearBlock`:


## Quality Gates

Before marking `android.media.MediaCodec.LinearBlock` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
