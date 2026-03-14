# SKILL: android.bluetooth.BluetoothGatt

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothGatt`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothGatt` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 23 |
| **Avg Score** | 4.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 9 (39%) |
| **Partial/Composite** | 7 (30%) |
| **No Mapping** | 7 (30%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 3 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 9 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 9 | direct | hard | `close` | `@ohos.bluetooth.GattClientDevice` |
| `connect` | `boolean connect()` | 9 | direct | easy | `connect` | `getConnectedBLEDevices(): Array<string>` |
| `disconnect` | `void disconnect()` | 9 | direct | easy | `disconnect` | `createGattServer(): GattServer` |
| `discoverServices` | `boolean discoverServices()` | 9 | direct | moderate | `getServices` | `createGattServer(): GattServer` |
| `readCharacteristic` | `boolean readCharacteristic(android.bluetooth.BluetoothGattCharacteristic)` | 7 | near | impossible | `readCharacteristicValue` | `@ohos.bluetooth.GattClientDevice` |
| `readRemoteRssi` | `boolean readRemoteRssi()` | 7 | near | rewrite | `getRssiValue` | `@ohos.bluetooth.GattClientDevice` |
| `requestMtu` | `boolean requestMtu(int)` | 7 | near | impossible | `setBLEMtuSize` | `@ohos.bluetooth.GattClientDevice` |
| `setCharacteristicNotification` | `boolean setCharacteristicNotification(android.bluetooth.BluetoothGattCharacteristic, boolean)` | 7 | near | rewrite | `setNotifyCharacteristicChanged` | `@ohos.bluetooth.GattClientDevice` |
| `writeCharacteristic` | `boolean writeCharacteristic(android.bluetooth.BluetoothGattCharacteristic)` | 7 | near | impossible | `writeCharacteristicValue` | `@ohos.bluetooth.GattClientDevice` |

## Gap Descriptions (per method)

- **`close`**: Direct
- **`connect`**: BLE connect
- **`disconnect`**: BLE disconnect
- **`discoverServices`**: BLE services
- **`readCharacteristic`**: Returns Promise
- **`readRemoteRssi`**: Returns Promise
- **`requestMtu`**: Returns boolean sync
- **`setCharacteristicNotification`**: Direct
- **`writeCharacteristic`**: Returns boolean

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getConnectedDevices` | 5 | partial | Return dummy instance / no-op |
| `getConnectionState` | 5 | partial | Return dummy instance / no-op |
| `getDevicesMatchingConnectionStates` | 4 | partial | Return dummy instance / no-op |
| `getServices` | 4 | partial | Return safe default (null/false/0/empty) |
| `getService` | 4 | partial | Return safe default (null/false/0/empty) |
| `requestConnectionPriority` | 4 | partial | Return dummy instance / no-op |
| `getDevice` | 3 | composite | Return safe default (null/false/0/empty) |
| `abortReliableWrite` | 1 | none | Log warning + no-op |
| `beginReliableWrite` | 1 | none | Log warning + no-op |
| `executeReliableWrite` | 1 | none | Log warning + no-op |
| `readDescriptor` | 1 | none | Return safe default (null/false/0/empty) |
| `readPhy` | 1 | none | Return safe default (null/false/0/empty) |
| `setPreferredPhy` | 1 | none | Log warning + no-op |
| `writeDescriptor` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothGatt`:


## Quality Gates

Before marking `android.bluetooth.BluetoothGatt` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 9 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
