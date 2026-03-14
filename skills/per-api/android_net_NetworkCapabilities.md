# SKILL: android.net.NetworkCapabilities

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.NetworkCapabilities`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.NetworkCapabilities` |
| **Package** | `android.net` |
| **Total Methods** | 10 |
| **Avg Score** | 3.7 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 3 (30%) |
| **Partial/Composite** | 4 (40%) |
| **No Mapping** | 3 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getLinkDownstreamBandwidthKbps` | `int getLinkDownstreamBandwidthKbps()` | 9 | direct | impossible | `linkDownBandwidthKbps` | `@ohos.net.connection.NetCapabilities` |
| `hasCapability` | `boolean hasCapability(int)` | 7 | near | impossible | `networkCap` | `@ohos.net.connection.NetCapabilities` |
| `hasTransport` | `boolean hasTransport(int)` | 7 | near | impossible | `bearerTypes` | `@ohos.net.connection.NetCapabilities` |

## Gap Descriptions (per method)

- **`getLinkDownstreamBandwidthKbps`**: Direct property
- **`hasCapability`**: Check networkCap array
- **`hasTransport`**: Check bearerTypes array

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `NetworkCapabilities` | 3 | composite | throw UnsupportedOperationException |
| `NetworkCapabilities` | 3 | composite | throw UnsupportedOperationException |
| `getOwnerUid` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSignalStrength` | 2 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `getLinkUpstreamBandwidthKbps` | 1 | none | Return safe default (null/false/0/empty) |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.net.NetworkCapabilities`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.NetworkCapabilities` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
