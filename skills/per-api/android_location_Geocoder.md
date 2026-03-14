# SKILL: android.location.Geocoder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.Geocoder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.Geocoder` |
| **Package** | `android.location` |
| **Total Methods** | 6 |
| **Avg Score** | 7.5 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 4 (66%) |
| **Partial/Composite** | 2 (33%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getFromLocation` | `java.util.List<android.location.Address> getFromLocation(double, double, int) throws java.io.IOException` | 9 | direct | moderate | `getAddressesFromLocation` | `getAddressesFromLocation(request: ReverseGeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void` |
| `getFromLocationName` | `java.util.List<android.location.Address> getFromLocationName(String, int) throws java.io.IOException` | 9 | direct | moderate | `getAddressesFromLocationName` | `getAddressesFromLocationName(request: GeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void` |
| `getFromLocationName` | `java.util.List<android.location.Address> getFromLocationName(String, int, double, double, double, double) throws java.io.IOException` | 9 | direct | moderate | `getAddressesFromLocationName` | `getAddressesFromLocationName(request: GeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void` |
| `isPresent` | `static boolean isPresent()` | 9 | direct | rewrite | `isGeocoderAvailable` | `@ohos.geoLocationManager.geoLocationManager` |

## Gap Descriptions (per method)

- **`getFromLocation`**: Geocoding
- **`getFromLocationName`**: Geocoding
- **`getFromLocationName`**: Geocoding
- **`isPresent`**: Direct equivalent

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Geocoder` | 4 | partial | throw UnsupportedOperationException |
| `Geocoder` | 4 | partial | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.location.Geocoder`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.Geocoder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
