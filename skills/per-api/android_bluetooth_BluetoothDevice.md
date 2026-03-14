# SKILL: android.bluetooth.BluetoothDevice

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothDevice`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothDevice` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 8 |
| **Avg Score** | 3.7 |
| **Scenario** | S7: Async/Threading Gap |
| **Strategy** | Promise wrapping, Handler/Looper emulation |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (75%) |
| **No Mapping** | 2 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 4 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock with concurrency tests) |

## Implementable APIs (score >= 5): 5 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAddress` | `String getAddress()` | 5 | partial | moderate | `getLocalAddress` | `getLocalAddress(): string` |
| `connectGatt` | `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback)` | 5 | partial | rewrite | `createGattClientDevice` | `@ohos.bluetooth.bluetooth` |
| `connectGatt` | `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback, int)` | 5 | partial | rewrite | `createGattClientDevice` | `@ohos.bluetooth.bluetooth` |
| `connectGatt` | `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback, int, int)` | 5 | partial | rewrite | `createGattClientDevice` | `@ohos.bluetooth.bluetooth` |
| `connectGatt` | `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback, int, int, android.os.Handler)` | 5 | partial | rewrite | `createGattClientDevice` | `@ohos.bluetooth.bluetooth` |

## Gap Descriptions (per method)

- **`connectGatt`**: Two-step: create then connect
- **`connectGatt`**: Two-step: create then connect
- **`connectGatt`**: Two-step: create then connect
- **`connectGatt`**: Two-step: create then connect

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setPin` | 2 | composite | Log warning + no-op |
| `describeContents` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S7 — Async/Threading Gap**

1. Implement using Java concurrency primitives (ExecutorService, BlockingQueue)
2. For Handler: single-thread executor + message queue
3. For AsyncTask: thread pool + callbacks
4. For sync-over-async: CompletableFuture wrapping OH Promise (in bridge)
5. Test with concurrent calls to verify thread safety
6. Add timeout to all blocking operations to prevent deadlock

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothDevice`:


## Quality Gates

Before marking `android.bluetooth.BluetoothDevice` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 5 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
