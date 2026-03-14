# SKILL: android.location.GnssClock

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.GnssClock`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.GnssClock` |
| **Package** | `android.location` |
| **Total Methods** | 22 |
| **Avg Score** | 2.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (50%) |
| **No Mapping** | 11 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `hasLeapSecond` | 4 | partial | Return safe default (null/false/0/empty) |
| `hasReferenceCarrierFrequencyHzForIsb` | 4 | partial | Return safe default (null/false/0/empty) |
| `getLeapSecond` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTimeNanos` | 4 | composite | Return safe default (null/false/0/empty) |
| `getDriftNanosPerSecond` | 3 | composite | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `getHardwareClockDiscontinuityCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getElapsedRealtimeNanos` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBiasNanos` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFullBiasNanos` | 2 | composite | Return safe default (null/false/0/empty) |
| `getReferenceConstellationTypeForIsb` | 2 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `hasBiasNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `hasBiasUncertaintyNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `hasDriftNanosPerSecond` | 1 | none | Return safe default (null/false/0/empty) |
| `hasDriftUncertaintyNanosPerSecond` | 1 | none | Return safe default (null/false/0/empty) |
| `hasElapsedRealtimeNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `hasElapsedRealtimeUncertaintyNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `hasFullBiasNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `hasReferenceCodeTypeForIsb` | 1 | none | Return safe default (null/false/0/empty) |
| `hasReferenceConstellationTypeForIsb` | 1 | none | Return safe default (null/false/0/empty) |
| `hasTimeUncertaintyNanos` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.location.GnssClock`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.GnssClock` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 22 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
