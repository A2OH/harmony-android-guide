# SKILL: android.bluetooth.BluetoothHidDevice

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothHidDevice`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothHidDevice` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 18 |
| **Avg Score** | 2.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 8 (44%) |
| **No Mapping** | 10 (55%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `disconnect` | `boolean disconnect(android.bluetooth.BluetoothDevice)` | 5 | partial | moderate | `disconnect` | `disconnect(): boolean` |

## Stub APIs (score < 5): 17 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getConnectedDevices` | 5 | partial | Return dummy instance / no-op |
| `getConnectionState` | 5 | partial | Return dummy instance / no-op |
| `Callback` | 5 | partial | throw UnsupportedOperationException |
| `getDevicesMatchingConnectionStates` | 4 | partial | Return dummy instance / no-op |
| `onConnectionStateChanged` | 4 | partial | Return dummy instance / no-op |
| `connect` | 3 | composite | Return dummy instance / no-op |
| `onGetReport` | 2 | composite | Return safe default (null/false/0/empty) |
| `registerApp` | 1 | none | Return safe default (null/false/0/empty) |
| `replyReport` | 1 | none | throw UnsupportedOperationException |
| `reportError` | 1 | none | throw UnsupportedOperationException |
| `sendReport` | 1 | none | throw UnsupportedOperationException |
| `unregisterApp` | 1 | none | Return safe default (null/false/0/empty) |
| `onAppStatusChanged` | 1 | none | Store callback, never fire |
| `onInterruptData` | 1 | none | Store callback, never fire |
| `onSetProtocol` | 1 | none | Log warning + no-op |
| `onSetReport` | 1 | none | Log warning + no-op |
| `onVirtualCableUnplug` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 17 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothHidDevice`:


## Quality Gates

Before marking `android.bluetooth.BluetoothHidDevice` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
