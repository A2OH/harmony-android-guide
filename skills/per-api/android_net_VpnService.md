# SKILL: android.net.VpnService

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.VpnService`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.VpnService` |
| **Package** | `android.net` |
| **Total Methods** | 10 |
| **Avg Score** | 4.5 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 3 (30%) |
| **Partial/Composite** | 4 (40%) |
| **No Mapping** | 3 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 1 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `protect` | `boolean protect(int)` | 9 | direct | impossible | `protect` | `@ohos.net.vpn.VpnConnection` |
| `protect` | `boolean protect(java.net.Socket)` | 9 | direct | impossible | `protect` | `@ohos.net.vpn.VpnConnection` |
| `protect` | `boolean protect(java.net.DatagramSocket)` | 9 | direct | impossible | `protect` | `@ohos.net.vpn.VpnConnection` |
| `prepare` | `static android.content.Intent prepare(android.content.Context)` | 5 | partial | rewrite | `createVpnConnection` | `@ohos.net.vpn.` |

## Gap Descriptions (per method)

- **`protect`**: Direct equivalent
- **`protect`**: Direct equivalent
- **`protect`**: Direct equivalent
- **`prepare`**: Different VPN setup model

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isAlwaysOn` | 3 | composite | Return safe default (null/false/0/empty) |
| `isLockdownEnabled` | 3 | composite | Return safe default (null/false/0/empty) |
| `VpnService` | 3 | composite | throw UnsupportedOperationException |
| `onBind` | 1 | none | Store callback, never fire |
| `onRevoke` | 1 | none | Store callback, never fire |
| `setUnderlyingNetworks` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.net.VpnService`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.VpnService` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
