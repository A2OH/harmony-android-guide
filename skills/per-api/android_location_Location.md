# SKILL: android.location.Location

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.Location`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.Location` |
| **Package** | `android.location` |
| **Total Methods** | 49 |
| **Avg Score** | 3.5 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (6%) |
| **Partial/Composite** | 34 (69%) |
| **No Mapping** | 12 (24%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAccuracy` | `float getAccuracy()` | 6 | near | moderate | `accuracy` | `@ohos.geolocation.Location` |
| `getAltitude` | `double getAltitude()` | 6 | near | moderate | `altitude` | `@ohos.geolocation.Location` |
| `getSpeed` | `float getSpeed()` | 6 | near | moderate | `speed` | `@ohos.geolocation.Location` |
| `distanceTo` | `float distanceTo(android.location.Location)` | 6 | partial | moderate | `distance` | `distance: number` |
| `getLongitude` | `double getLongitude()` | 6 | partial | moderate | `longitude` | `longitude: number` |
| `getLatitude` | `double getLatitude()` | 6 | partial | moderate | `latitude` | `latitude: number` |
| `hasAccuracy` | `boolean hasAccuracy()` | 5 | partial | moderate | `accuracy` | `accuracy: number` |
| `hasAltitude` | `boolean hasAltitude()` | 5 | partial | moderate | `altitude` | `altitude: number` |
| `setAccuracy` | `void setAccuracy(float)` | 5 | partial | moderate | `accuracy` | `accuracy: number` |
| `setAltitude` | `void setAltitude(double)` | 5 | partial | moderate | `altitude` | `altitude: number` |
| `hasSpeed` | `boolean hasSpeed()` | 5 | partial | moderate | `speed` | `speed: number` |
| `setSpeed` | `void setSpeed(float)` | 5 | partial | moderate | `speed` | `speed: number` |

## Gap Descriptions (per method)

- **`getAccuracy`**: Auto-promoted: near score=6.18421052631579
- **`getAltitude`**: Auto-promoted: near score=6.18421052631579
- **`getSpeed`**: Auto-promoted: near score=6.03846153846154

## Stub APIs (score < 5): 37 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setLongitude` | 5 | partial | Log warning + no-op |
| `setLatitude` | 5 | partial | Log warning + no-op |
| `hasSpeedAccuracy` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasVerticalAccuracy` | 5 | partial | Return safe default (null/false/0/empty) |
| `distanceBetween` | 5 | partial | Return safe default (null/false/0/empty) |
| `getProvider` | 4 | partial | Return safe default (null/false/0/empty) |
| `getExtras` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTime` | 4 | partial | Return safe default (null/false/0/empty) |
| `reset` | 4 | partial | Log warning + no-op |
| `set` | 4 | partial | Log warning + no-op |
| `setTime` | 4 | partial | Log warning + no-op |
| `setElapsedRealtimeNanos` | 4 | partial | Log warning + no-op |
| `getBearingAccuracyDegrees` | 4 | partial | Return safe default (null/false/0/empty) |
| `setProvider` | 4 | composite | Log warning + no-op |
| `hasBearing` | 3 | composite | Return safe default (null/false/0/empty) |
| `setBearing` | 3 | composite | Log warning + no-op |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `Location` | 3 | composite | Store callback, never fire |
| `Location` | 3 | composite | Store callback, never fire |
| `getBearing` | 3 | composite | Return safe default (null/false/0/empty) |
| `getElapsedRealtimeNanos` | 3 | composite | Return safe default (null/false/0/empty) |
| `hasBearingAccuracy` | 3 | composite | Return safe default (null/false/0/empty) |
| `setExtras` | 2 | composite | Log warning + no-op |
| `getVerticalAccuracyMeters` | 2 | composite | Return safe default (null/false/0/empty) |
| `setVerticalAccuracyMeters` | 2 | composite | Log warning + no-op |
| `bearingTo` | 1 | none | throw UnsupportedOperationException |
| `convert` | 1 | none | Store callback, never fire |
| `convert` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `getElapsedRealtimeUncertaintyNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `getSpeedAccuracyMetersPerSecond` | 1 | none | Return safe default (null/false/0/empty) |
| `hasElapsedRealtimeUncertaintyNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `isFromMockProvider` | 1 | none | Return safe default (null/false/0/empty) |
| `setBearingAccuracyDegrees` | 1 | none | Log warning + no-op |
| `setElapsedRealtimeUncertaintyNanos` | 1 | none | Log warning + no-op |
| `setSpeedAccuracyMetersPerSecond` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 37 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.Location`:


## Quality Gates

Before marking `android.location.Location` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 49 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
