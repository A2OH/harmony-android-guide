# SKILL: android.net.LinkProperties

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.LinkProperties`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.LinkProperties` |
| **Package** | `android.net` |
| **Total Methods** | 16 |
| **Avg Score** | 3.1 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 1 (6%) |
| **Partial/Composite** | 11 (68%) |
| **No Mapping** | 4 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getMtu` | `int getMtu()` | 9 | direct | rewrite | `mtu` | `@ohos.net.connection.ConnectionProperties` |
| `clear` | `void clear()` | 5 | partial | moderate | `clear` | `clear(): void` |

## Gap Descriptions (per method)

- **`getMtu`**: Direct property

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setHttpProxy` | 4 | composite | Log warning + no-op |
| `setLinkAddresses` | 3 | composite | Log warning + no-op |
| `setInterfaceName` | 3 | composite | Log warning + no-op |
| `setNat64Prefix` | 3 | composite | Log warning + no-op |
| `setDhcpServerAddress` | 3 | composite | Log warning + no-op |
| `setMtu` | 3 | composite | Log warning + no-op |
| `setDomains` | 3 | composite | Log warning + no-op |
| `LinkProperties` | 3 | composite | throw UnsupportedOperationException |
| `isPrivateDnsActive` | 3 | composite | Return safe default (null/false/0/empty) |
| `isWakeOnLanSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `addRoute` | 1 | none | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `setDnsServers` | 1 | none | Log warning + no-op |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.net.LinkProperties`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.LinkProperties` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
