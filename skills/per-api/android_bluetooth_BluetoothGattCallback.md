# SKILL: android.bluetooth.BluetoothGattCallback

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothGattCallback`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothGattCallback` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 13 |
| **Avg Score** | 2.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 5 (38%) |
| **No Mapping** | 8 (61%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 2 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onCharacteristicChanged` | `void onCharacteristicChanged(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic)` | 5 | partial | impossible | `on(BLECharacteristicChange)` | `@ohos.bluetooth.GattClientDevice` |
| `onConnectionStateChange` | `void onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)` | 5 | partial | hard | `on(BLEConnectionStateChange)` | `@ohos.bluetooth.GattClientDevice` |

## Gap Descriptions (per method)

- **`onCharacteristicChanged`**: Event listener
- **`onConnectionStateChange`**: Event listener

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `BluetoothGattCallback` | 3 | composite | throw UnsupportedOperationException |
| `onDescriptorWrite` | 2 | composite | Log warning + no-op |
| `onReadRemoteRssi` | 2 | composite | Return safe default (null/false/0/empty) |
| `onCharacteristicRead` | 1 | none | Return safe default (null/false/0/empty) |
| `onCharacteristicWrite` | 1 | none | Return safe default (null/false/0/empty) |
| `onDescriptorRead` | 1 | none | Return safe default (null/false/0/empty) |
| `onMtuChanged` | 1 | none | Store callback, never fire |
| `onPhyRead` | 1 | none | Return safe default (null/false/0/empty) |
| `onPhyUpdate` | 1 | none | Log warning + no-op |
| `onReliableWriteCompleted` | 1 | none | Log warning + no-op |
| `onServicesDiscovered` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothGattCallback`:


## Quality Gates

Before marking `android.bluetooth.BluetoothGattCallback` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
