# SKILL: android.location.GnssMeasurement

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.GnssMeasurement`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.GnssMeasurement` |
| **Package** | `android.location` |
| **Total Methods** | 28 |
| **Avg Score** | 2.7 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 18 (64%) |
| **No Mapping** | 10 (35%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCarrierFrequencyHz` | `float getCarrierFrequencyHz()` | 5 | partial | moderate | `carrierFrequencies` | `carrierFrequencies: Array<number>` |

## Stub APIs (score < 5): 27 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getState` | 5 | partial | Return safe default (null/false/0/empty) |
| `getSvid` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasCarrierFrequencyHz` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasCodeType` | 4 | partial | Return safe default (null/false/0/empty) |
| `getConstellationType` | 4 | partial | Return safe default (null/false/0/empty) |
| `getMultipathIndicator` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAutomaticGainControlLevelDb` | 4 | partial | Return safe default (null/false/0/empty) |
| `getPseudorangeRateMetersPerSecond` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `hasSnrInDb` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSnrInDb` | 3 | composite | Return safe default (null/false/0/empty) |
| `getReceivedSvTimeNanos` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTimeOffsetNanos` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAccumulatedDeltaRangeMeters` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAccumulatedDeltaRangeState` | 2 | composite | Return safe default (null/false/0/empty) |
| `getFullInterSignalBiasNanos` | 2 | composite | Return safe default (null/false/0/empty) |
| `getReceivedSvTimeUncertaintyNanos` | 2 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `getAccumulatedDeltaRangeUncertaintyMeters` | 1 | none | Return safe default (null/false/0/empty) |
| `getPseudorangeRateUncertaintyMetersPerSecond` | 1 | none | Return safe default (null/false/0/empty) |
| `getSatelliteInterSignalBiasNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `hasAutomaticGainControlLevelDb` | 1 | none | Return safe default (null/false/0/empty) |
| `hasBasebandCn0DbHz` | 1 | none | Return safe default (null/false/0/empty) |
| `hasFullInterSignalBiasNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `hasFullInterSignalBiasUncertaintyNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `hasSatelliteInterSignalBiasNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `hasSatelliteInterSignalBiasUncertaintyNanos` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.location.GnssMeasurement`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.GnssMeasurement` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 28 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
