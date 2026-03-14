# SKILL: android.media.MediaCodec

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodec`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodec` |
| **Package** | `android.media` |
| **Total Methods** | 24 |
| **Avg Score** | 2.8 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 20 (83%) |
| **No Mapping** | 4 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setCallback` | `void setCallback(@Nullable android.media.MediaCodec.Callback, @Nullable android.os.Handler)` | 6 | partial | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |
| `setCallback` | `void setCallback(@Nullable android.media.MediaCodec.Callback)` | 6 | partial | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `release` | 5 | partial | No-op |
| `reset` | 3 | composite | Log warning + no-op |
| `start` | 3 | composite | Return dummy instance / no-op |
| `stop` | 3 | composite | No-op |
| `setVideoScalingMode` | 3 | composite | Log warning + no-op |
| `setInputSurface` | 3 | composite | Log warning + no-op |
| `getMetrics` | 3 | composite | Return safe default (null/false/0/empty) |
| `dequeueOutputBuffer` | 3 | composite | Log warning + no-op |
| `setParameters` | 3 | composite | Log warning + no-op |
| `configure` | 3 | composite | Store callback, never fire |
| `configure` | 3 | composite | Store callback, never fire |
| `queueInputBuffer` | 3 | composite | Log warning + no-op |
| `releaseOutputBuffer` | 3 | composite | No-op |
| `releaseOutputBuffer` | 3 | composite | No-op |
| `dequeueInputBuffer` | 3 | composite | Log warning + no-op |
| `queueSecureInputBuffer` | 3 | composite | Log warning + no-op |
| `setAudioPresentation` | 2 | composite | Log warning + no-op |
| `setOutputSurface` | 2 | composite | Log warning + no-op |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `flush` | 1 | none | throw UnsupportedOperationException |
| `setOnFrameRenderedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `signalEndOfInputStream` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodec`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaCodec` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 24 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
