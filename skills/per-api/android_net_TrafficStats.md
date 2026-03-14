# SKILL: android.net.TrafficStats

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.TrafficStats`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.TrafficStats` |
| **Package** | `android.net` |
| **Total Methods** | 30 |
| **Avg Score** | 3.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 6 (20%) |
| **Partial/Composite** | 15 (50%) |
| **No Mapping** | 9 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getMobileRxBytes` | `static long getMobileRxBytes()` | 9 | direct | rewrite | `getCellularRxBytes` | `@ohos.net.statistics.` |
| `getMobileTxBytes` | `static long getMobileTxBytes()` | 9 | direct | rewrite | `getCellularTxBytes` | `@ohos.net.statistics.` |
| `getTotalRxBytes` | `static long getTotalRxBytes()` | 9 | direct | hard | `getAllRxBytes` | `@ohos.net.statistics.` |
| `getTotalTxBytes` | `static long getTotalTxBytes()` | 9 | direct | hard | `getAllTxBytes` | `@ohos.net.statistics.` |
| `getUidRxBytes` | `static long getUidRxBytes(int)` | 9 | direct | hard | `getUidRxBytes` | `@ohos.net.statistics.` |
| `getUidTxBytes` | `static long getUidTxBytes(int)` | 9 | direct | hard | `getUidTxBytes` | `@ohos.net.statistics.` |

## Gap Descriptions (per method)

- **`getMobileRxBytes`**: Direct equivalent
- **`getMobileTxBytes`**: Direct equivalent
- **`getTotalRxBytes`**: Direct equivalent
- **`getTotalTxBytes`**: Direct equivalent
- **`getUidRxBytes`**: Direct equivalent
- **`getUidTxBytes`**: Direct equivalent

## Stub APIs (score < 5): 24 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `tagSocket` | 4 | partial | throw UnsupportedOperationException |
| `untagSocket` | 4 | partial | throw UnsupportedOperationException |
| `setThreadStatsUid` | 4 | composite | Return safe default (null/false/0/empty) |
| `setThreadStatsTag` | 3 | composite | Return safe default (null/false/0/empty) |
| `TrafficStats` | 3 | composite | throw UnsupportedOperationException |
| `getThreadStatsUid` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUidRxPackets` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUidTxPackets` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRxPackets` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTxPackets` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTotalTxPackets` | 3 | composite | Return safe default (null/false/0/empty) |
| `getThreadStatsTag` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTotalRxPackets` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMobileRxPackets` | 2 | composite | Return safe default (null/false/0/empty) |
| `getMobileTxPackets` | 2 | composite | Return safe default (null/false/0/empty) |
| `clearThreadStatsTag` | 1 | none | Return safe default (null/false/0/empty) |
| `clearThreadStatsUid` | 1 | none | Return safe default (null/false/0/empty) |
| `getAndSetThreadStatsTag` | 1 | none | Return safe default (null/false/0/empty) |
| `incrementOperationCount` | 1 | none | Store callback, never fire |
| `incrementOperationCount` | 1 | none | Store callback, never fire |
| `tagDatagramSocket` | 1 | none | throw UnsupportedOperationException |
| `tagFileDescriptor` | 1 | none | throw UnsupportedOperationException |
| `untagDatagramSocket` | 1 | none | throw UnsupportedOperationException |
| `untagFileDescriptor` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.net.TrafficStats`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.TrafficStats` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 30 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
