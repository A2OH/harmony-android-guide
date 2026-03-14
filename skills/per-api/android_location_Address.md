# SKILL: android.location.Address

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.Address`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.Address` |
| **Package** | `android.location` |
| **Total Methods** | 43 |
| **Avg Score** | 4.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 5 (11%) |
| **Partial/Composite** | 37 (86%) |
| **No Mapping** | 1 (2%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 15 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getCountryName` | `String getCountryName()` | 6 | near | moderate | `countryName` | `@ohos.geolocation.GeoAddress` |
| `getSubLocality` | `String getSubLocality()` | 6 | near | moderate | `subLocality` | `@ohos.geolocation.GeoAddress` |
| `getPostalCode` | `String getPostalCode()` | 6 | near | moderate | `postalCode` | `@ohos.geolocation.GeoAddress` |
| `getLocality` | `String getLocality()` | 6 | near | moderate | `locality` | `@ohos.geolocation.GeoAddress` |
| `getPremises` | `String getPremises()` | 6 | near | moderate | `premises` | `@ohos.geolocation.GeoAddress` |
| `getSubAdminArea` | `String getSubAdminArea()` | 6 | partial | moderate | `subAdministrativeArea` | `subAdministrativeArea?: string` |
| `getLongitude` | `double getLongitude()` | 6 | partial | moderate | `longitude` | `longitude: number` |
| `getLatitude` | `double getLatitude()` | 6 | partial | moderate | `latitude` | `latitude: number` |
| `Address` | `Address(java.util.Locale)` | 5 | partial | moderate | `address` | `address: number` |
| `setCountryName` | `void setCountryName(String)` | 5 | partial | moderate | `countryName` | `countryName?: string` |
| `setSubLocality` | `void setSubLocality(String)` | 5 | partial | moderate | `subLocality` | `subLocality?: string` |
| `setPostalCode` | `void setPostalCode(String)` | 5 | partial | moderate | `postalCode` | `postalCode?: string` |
| `setLocality` | `void setLocality(String)` | 5 | partial | moderate | `locality` | `locality?: string` |
| `setPremises` | `void setPremises(String)` | 5 | partial | moderate | `premises` | `premises?: string` |
| `getFeatureName` | `String getFeatureName()` | 5 | partial | moderate | `getLocalName` | `getLocalName(): string` |

## Gap Descriptions (per method)

- **`getCountryName`**: Auto-promoted: near score=6.12764705882353
- **`getSubLocality`**: Auto-promoted: near score=6.12764705882353
- **`getPostalCode`**: Auto-promoted: near score=6.10677749360614
- **`getLocality`**: Auto-promoted: near score=6.05185758513932
- **`getPremises`**: Auto-promoted: near score=6.05185758513932

## Stub APIs (score < 5): 28 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setAddressLine` | 5 | partial | Log warning + no-op |
| `setSubAdminArea` | 5 | partial | Log warning + no-op |
| `hasLongitude` | 5 | partial | Return safe default (null/false/0/empty) |
| `setLongitude` | 5 | partial | Log warning + no-op |
| `hasLatitude` | 5 | partial | Return safe default (null/false/0/empty) |
| `setLatitude` | 5 | partial | Log warning + no-op |
| `getLocale` | 5 | partial | Return safe default (null/false/0/empty) |
| `clearLongitude` | 5 | partial | Store callback, never fire |
| `clearLatitude` | 5 | partial | throw UnsupportedOperationException |
| `getExtras` | 4 | partial | Return safe default (null/false/0/empty) |
| `getMaxAddressLineIndex` | 4 | partial | Return safe default (null/false/0/empty) |
| `getSubThoroughfare` | 4 | partial | Return safe default (null/false/0/empty) |
| `getThoroughfare` | 4 | partial | Return safe default (null/false/0/empty) |
| `setCountryCode` | 4 | partial | Log warning + no-op |
| `setFeatureName` | 3 | composite | Log warning + no-op |
| `setUrl` | 3 | composite | Log warning + no-op |
| `setPhone` | 3 | composite | Log warning + no-op |
| `setAdminArea` | 3 | composite | Log warning + no-op |
| `getCountryCode` | 3 | composite | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `getUrl` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAddressLine` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPhone` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAdminArea` | 3 | composite | Return safe default (null/false/0/empty) |
| `setThoroughfare` | 3 | composite | Log warning + no-op |
| `setExtras` | 2 | composite | Log warning + no-op |
| `setSubThoroughfare` | 2 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.location.Address`:


## Quality Gates

Before marking `android.location.Address` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 43 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 15 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
