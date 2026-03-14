# SKILL: android.bluetooth.BluetoothGattCharacteristic

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothGattCharacteristic`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothGattCharacteristic` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 21 |
| **Avg Score** | 2.8 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 15 (71%) |
| **No Mapping** | 6 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 21 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getUuid` | 5 | partial | Return safe default (null/false/0/empty) |
| `BluetoothGattCharacteristic` | 4 | partial | Return safe default (null/false/0/empty) |
| `getInstanceId` | 4 | partial | Return safe default (null/false/0/empty) |
| `getFloatValue` | 4 | partial | Return safe default (null/false/0/empty) |
| `getStringValue` | 4 | partial | Return safe default (null/false/0/empty) |
| `getIntValue` | 4 | partial | Return safe default (null/false/0/empty) |
| `getProperties` | 4 | partial | Return safe default (null/false/0/empty) |
| `getService` | 4 | partial | Return safe default (null/false/0/empty) |
| `getValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `setValue` | 3 | composite | Log warning + no-op |
| `setValue` | 3 | composite | Log warning + no-op |
| `setValue` | 3 | composite | Log warning + no-op |
| `setValue` | 3 | composite | Log warning + no-op |
| `setWriteType` | 3 | composite | Log warning + no-op |
| `getWriteType` | 2 | composite | Return safe default (null/false/0/empty) |
| `addDescriptor` | 1 | none | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `getDescriptor` | 1 | none | Return safe default (null/false/0/empty) |
| `getDescriptors` | 1 | none | Return safe default (null/false/0/empty) |
| `getPermissions` | 1 | none | Return safe default (null/false/0/empty) |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothGattCharacteristic`:


## Quality Gates

Before marking `android.bluetooth.BluetoothGattCharacteristic` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
