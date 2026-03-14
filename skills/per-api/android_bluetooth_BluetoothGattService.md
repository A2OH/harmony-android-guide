# SKILL: android.bluetooth.BluetoothGattService

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothGattService`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothGattService` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 11 |
| **Avg Score** | 3.0 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (63%) |
| **No Mapping** | 4 (36%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getType` | 5 | partial | Return safe default (null/false/0/empty) |
| `getUuid` | 5 | partial | Return safe default (null/false/0/empty) |
| `BluetoothGattService` | 4 | partial | throw UnsupportedOperationException |
| `getInstanceId` | 4 | partial | Return safe default (null/false/0/empty) |
| `getIncludedServices` | 4 | partial | Return safe default (null/false/0/empty) |
| `getCharacteristics` | 4 | partial | Return safe default (null/false/0/empty) |
| `getCharacteristic` | 2 | composite | Return safe default (null/false/0/empty) |
| `addCharacteristic` | 1 | none | Return safe default (null/false/0/empty) |
| `addService` | 1 | none | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 11 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothGattService`:


## Quality Gates

Before marking `android.bluetooth.BluetoothGattService` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
