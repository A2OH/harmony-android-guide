# SKILL: android.media.MediaCodecInfo.VideoCapabilities

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaCodecInfo.VideoCapabilities`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaCodecInfo.VideoCapabilities` |
| **Package** | `android.media.MediaCodecInfo` |
| **Total Methods** | 11 |
| **Avg Score** | 2.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isSizeSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBitrateRange` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSupportedFrameRates` | 3 | composite | Return safe default (null/false/0/empty) |
| `areSizeAndRateSupported` | 3 | composite | throw UnsupportedOperationException |
| `getHeightAlignment` | 3 | composite | Return safe default (null/false/0/empty) |
| `getWidthAlignment` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSupportedFrameRatesFor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSupportedWidthsFor` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSupportedWidths` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSupportedHeightsFor` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSupportedHeights` | 2 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaCodecInfo.VideoCapabilities`:


## Quality Gates

Before marking `android.media.MediaCodecInfo.VideoCapabilities` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
