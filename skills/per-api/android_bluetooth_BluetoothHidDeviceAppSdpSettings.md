# SKILL: android.bluetooth.BluetoothHidDeviceAppSdpSettings

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothHidDeviceAppSdpSettings`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothHidDeviceAppSdpSettings` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 8 |
| **Avg Score** | 2.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 5 (62%) |
| **No Mapping** | 3 (37%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getName` | `String getName()` | 5 | partial | moderate | `getLocalName` | `getLocalName(): string` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `BluetoothHidDeviceAppSdpSettings` | 4 | partial | Log warning + no-op |
| `getSubclass` | 4 | partial | Return safe default (null/false/0/empty) |
| `getDescription` | 4 | partial | Return safe default (null/false/0/empty) |
| `getProvider` | 3 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `getDescriptors` | 1 | none | Return safe default (null/false/0/empty) |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 7 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothHidDeviceAppSdpSettings`:


## Quality Gates

Before marking `android.bluetooth.BluetoothHidDeviceAppSdpSettings` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
