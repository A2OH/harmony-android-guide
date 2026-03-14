# SKILL: android.media.MediaExtractor

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaExtractor`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaExtractor` |
| **Package** | `android.media` |
| **Total Methods** | 27 |
| **Avg Score** | 2.9 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 23 (85%) |
| **No Mapping** | 4 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getTrackCount` | `int getTrackCount()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getCachedDuration` | `long getCachedDuration()` | 5 | partial | moderate | `duration` | `readonly duration: number` |

## Stub APIs (score < 5): 25 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `release` | 5 | partial | No-op |
| `MediaExtractor` | 5 | partial | throw UnsupportedOperationException |
| `getSampleFlags` | 4 | partial | Return safe default (null/false/0/empty) |
| `readSampleData` | 4 | partial | Return safe default (null/false/0/empty) |
| `getSampleTime` | 4 | partial | Return safe default (null/false/0/empty) |
| `setMediaCas` | 4 | composite | Log warning + no-op |
| `getMetrics` | 3 | composite | Return safe default (null/false/0/empty) |
| `selectTrack` | 3 | composite | throw UnsupportedOperationException |
| `getCasInfo` | 3 | composite | Return safe default (null/false/0/empty) |
| `unselectTrack` | 3 | composite | throw UnsupportedOperationException |
| `getSampleSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSampleTrackIndex` | 2 | composite | Return safe default (null/false/0/empty) |
| `setDataSource` | 2 | composite | Log warning + no-op |
| `setDataSource` | 2 | composite | Log warning + no-op |
| `setDataSource` | 2 | composite | Log warning + no-op |
| `setDataSource` | 2 | composite | Log warning + no-op |
| `setDataSource` | 2 | composite | Log warning + no-op |
| `setDataSource` | 2 | composite | Log warning + no-op |
| `setDataSource` | 2 | composite | Log warning + no-op |
| `getSampleCryptoInfo` | 2 | composite | Return safe default (null/false/0/empty) |
| `hasCacheReachedEndOfStream` | 2 | composite | Return safe default (null/false/0/empty) |
| `advance` | 1 | none | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `getDrmInitData` | 1 | none | Return dummy instance / no-op |
| `seekTo` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaExtractor`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaExtractor` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 27 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
