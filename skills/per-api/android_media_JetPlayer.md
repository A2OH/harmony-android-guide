# SKILL: android.media.JetPlayer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.JetPlayer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.JetPlayer` |
| **Package** | `android.media` |
| **Total Methods** | 19 |
| **Avg Score** | 2.1 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 8 (42%) |
| **No Mapping** | 11 (57%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 19 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `release` | 4 | partial | No-op |
| `pause` | 4 | partial | throw UnsupportedOperationException |
| `play` | 4 | partial | throw UnsupportedOperationException |
| `setEventListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `setEventListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `getJetPlayer` | 3 | composite | Return safe default (null/false/0/empty) |
| `closeJetFile` | 3 | composite | No-op |
| `getMaxTracks` | 3 | composite | Return safe default (null/false/0/empty) |
| `clearQueue` | 1 | none | throw UnsupportedOperationException |
| `clone` | 1 | none | Store callback, never fire |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `loadJetFile` | 1 | none | throw UnsupportedOperationException |
| `loadJetFile` | 1 | none | throw UnsupportedOperationException |
| `queueJetSegment` | 1 | none | throw UnsupportedOperationException |
| `queueJetSegmentMuteArray` | 1 | none | throw UnsupportedOperationException |
| `setMuteArray` | 1 | none | Log warning + no-op |
| `setMuteFlag` | 1 | none | Log warning + no-op |
| `setMuteFlags` | 1 | none | Log warning + no-op |
| `triggerClip` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.JetPlayer`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.JetPlayer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
