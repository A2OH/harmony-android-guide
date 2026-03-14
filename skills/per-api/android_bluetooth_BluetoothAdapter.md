# SKILL: android.bluetooth.BluetoothAdapter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothAdapter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothAdapter` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 17 |
| **Avg Score** | 5.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (17%) |
| **Partial/Composite** | 14 (82%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 1 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getName` | `String getName()` | 9 | direct | moderate | `getLocalName` | `@ohos.bluetooth.bluetooth` |
| `getProfileProxy` | `boolean getProfileProxy(android.content.Context, android.bluetooth.BluetoothProfile.ServiceListener, int)` | 7 | near | moderate | `getProfile` | `@ohos.bluetooth.bluetooth` |
| `getBluetoothLeScanner` | `android.bluetooth.le.BluetoothLeScanner getBluetoothLeScanner()` | 6 | near | moderate | `enableBluetooth` | `enableBluetooth(): void` |
| `getRemoteDevice` | `android.bluetooth.BluetoothDevice getRemoteDevice(String)` | 6 | partial | moderate | `getState` | `getState(): BluetoothState` |
| `getRemoteDevice` | `android.bluetooth.BluetoothDevice getRemoteDevice(byte[])` | 6 | partial | moderate | `getState` | `getState(): BluetoothState` |
| `checkBluetoothAddress` | `static boolean checkBluetoothAddress(String)` | 6 | partial | moderate | `enableBluetooth` | `enableBluetooth(): void` |
| `getDefaultAdapter` | `static android.bluetooth.BluetoothAdapter getDefaultAdapter()` | 6 | partial | moderate | `getState` | `getState(): BluetoothState` |
| `getBluetoothLeAdvertiser` | `android.bluetooth.le.BluetoothLeAdvertiser getBluetoothLeAdvertiser()` | 6 | partial | moderate | `enableBluetooth` | `enableBluetooth(): void` |
| `getLeMaximumAdvertisingDataLength` | `int getLeMaximumAdvertisingDataLength()` | 5 | partial | moderate | `getLocalAddress` | `getLocalAddress(): string` |
| `isLePeriodicAdvertisingSupported` | `boolean isLePeriodicAdvertisingSupported()` | 5 | partial | moderate | `getLocalAddress` | `getLocalAddress(): string` |
| `isLeCodedPhySupported` | `boolean isLeCodedPhySupported()` | 5 | partial | moderate | `disableBluetooth` | `disableBluetooth(): void` |
| `isLeExtendedAdvertisingSupported` | `boolean isLeExtendedAdvertisingSupported()` | 5 | partial | moderate | `disableBluetooth` | `disableBluetooth(): void` |

## Gap Descriptions (per method)

- **`getName`**: Direct
- **`getProfileProxy`**: Direct return vs ServiceListener

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isLe2MPhySupported` | 5 | partial | Return safe default (null/false/0/empty) |
| `isMultipleAdvertisementSupported` | 5 | partial | Return safe default (null/false/0/empty) |
| `closeProfileProxy` | 4 | partial | No-op |
| `isOffloadedFilteringSupported` | 4 | composite | Return safe default (null/false/0/empty) |
| `isOffloadedScanBatchingSupported` | 3 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothAdapter`:


## Quality Gates

Before marking `android.bluetooth.BluetoothAdapter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
