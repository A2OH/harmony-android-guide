# SKILL: android.location.LocationManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.LocationManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.LocationManager` |
| **Package** | `android.location` |
| **Total Methods** | 14 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (21%) |
| **Partial/Composite** | 11 (78%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isLocationEnabled` | `boolean isLocationEnabled()` | 9 | direct | easy | `isLocationEnabled` | `@ohos.geoLocationManager.geoLocationManager` |
| `isProviderEnabled` | `boolean isProviderEnabled(@NonNull String)` | 9 | direct | easy | `isLocationEnabled` | `isLocationEnabled(): boolean` |
| `removeUpdates` | `void removeUpdates(@NonNull android.app.PendingIntent)` | 9 | direct | easy | `off('locationChange')` | `UNSET = 0x200` |
| `setTestProviderEnabled` | `void setTestProviderEnabled(@NonNull String, boolean)` | 6 | partial | moderate | `isLocationEnabled` | `isLocationEnabled(): boolean` |
| `unregisterAntennaInfoListener` | `void unregisterAntennaInfoListener(@NonNull android.location.GnssAntennaInfo.Listener)` | 5 | partial | moderate | `isLocationEnabled` | `isLocationEnabled(): boolean` |
| `sendExtraCommand` | `boolean sendExtraCommand(@NonNull String, @NonNull String, @Nullable android.os.Bundle)` | 5 | partial | moderate | `sendCommand` | `sendCommand(command: LocationCommand, callback: AsyncCallback<void>): void` |
| `setTestProviderLocation` | `void setTestProviderLocation(@NonNull String, @NonNull android.location.Location)` | 5 | partial | moderate | `setMockedLocations` | `setMockedLocations(config: LocationMockConfig): void` |

## Gap Descriptions (per method)

- **`isLocationEnabled`**: Direct equivalent
- **`isProviderEnabled`**: Provider status
- **`removeUpdates`**: Stop updates

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `removeTestProvider` | 5 | partial | Log warning + no-op |
| `unregisterGnssStatusCallback` | 5 | partial | Return safe default (null/false/0/empty) |
| `unregisterGnssNavigationMessageCallback` | 5 | partial | Return safe default (null/false/0/empty) |
| `addTestProvider` | 5 | partial | Log warning + no-op |
| `removeNmeaListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `unregisterGnssMeasurementsCallback` | 4 | partial | Return safe default (null/false/0/empty) |
| `getGnssYearOfHardware` | 4 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 7 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.LocationManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.LocationManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
