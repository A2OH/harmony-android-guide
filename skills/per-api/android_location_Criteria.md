# SKILL: android.location.Criteria

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.Criteria`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.Criteria` |
| **Package** | `android.location` |
| **Total Methods** | 24 |
| **Avg Score** | 3.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 21 (87%) |
| **No Mapping** | 3 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAccuracy` | `int getAccuracy()` | 6 | partial | moderate | `accuracy` | `accuracy: number` |
| `getSpeedAccuracy` | `int getSpeedAccuracy()` | 5 | partial | moderate | `accuracy` | `accuracy: number` |
| `getBearingAccuracy` | `int getBearingAccuracy()` | 5 | partial | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `getVerticalAccuracy` | `int getVerticalAccuracy()` | 5 | partial | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `getHorizontalAccuracy` | `int getHorizontalAccuracy()` | 5 | partial | moderate | `maxAccuracy` | `maxAccuracy?: number` |
| `isSpeedRequired` | `boolean isSpeedRequired()` | 5 | partial | moderate | `isSelfPowered` | `isSelfPowered: boolean` |

## Stub APIs (score < 5): 18 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setAccuracy` | 5 | partial | Log warning + no-op |
| `setSpeedAccuracy` | 4 | partial | Log warning + no-op |
| `setBearingAccuracy` | 4 | partial | Log warning + no-op |
| `isAltitudeRequired` | 4 | partial | Return safe default (null/false/0/empty) |
| `setVerticalAccuracy` | 4 | partial | Log warning + no-op |
| `getPowerRequirement` | 4 | partial | Return safe default (null/false/0/empty) |
| `setCostAllowed` | 4 | composite | Log warning + no-op |
| `setPowerRequirement` | 3 | composite | Log warning + no-op |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `isCostAllowed` | 3 | composite | Return safe default (null/false/0/empty) |
| `setBearingRequired` | 3 | composite | Log warning + no-op |
| `isBearingRequired` | 3 | composite | Return safe default (null/false/0/empty) |
| `setAltitudeRequired` | 3 | composite | Log warning + no-op |
| `setHorizontalAccuracy` | 3 | composite | Log warning + no-op |
| `setSpeedRequired` | 2 | composite | Log warning + no-op |
| `Criteria` | 1 | none | throw UnsupportedOperationException |
| `Criteria` | 1 | none | throw UnsupportedOperationException |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.location.Criteria`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.Criteria` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 24 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
