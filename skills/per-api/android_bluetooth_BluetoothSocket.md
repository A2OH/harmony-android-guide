# SKILL: android.bluetooth.BluetoothSocket

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.bluetooth.BluetoothSocket`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.bluetooth.BluetoothSocket` |
| **Package** | `android.bluetooth` |
| **Total Methods** | 9 |
| **Avg Score** | 3.6 |
| **Scenario** | S7: Async/Threading Gap |
| **Strategy** | Promise wrapping, Handler/Looper emulation |
| **Direct/Near** | 1 (11%) |
| **Partial/Composite** | 6 (66%) |
| **No Mapping** | 2 (22%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 4 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock with concurrency tests) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close() throws java.io.IOException` | 7 | near | hard | `sppCloseClientSocket` | `@ohos.bluetooth.bluetooth` |
| `connect` | `void connect() throws java.io.IOException` | 5 | partial | rewrite | `sppConnect` | `@ohos.bluetooth.bluetooth` |
| `isConnected` | `boolean isConnected()` | 5 | partial | moderate | `isConnected` | `isConnected(): boolean` |

## Gap Descriptions (per method)

- **`close`**: Takes socket number
- **`connect`**: Module-level

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getConnectionType` | 5 | partial | Return dummy instance / no-op |
| `getRemoteDevice` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInputStream` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOutputStream` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxReceivePacketSize` | 1 | none | Return safe default (null/false/0/empty) |
| `getMaxTransmitPacketSize` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S7 — Async/Threading Gap**

1. Implement using Java concurrency primitives (ExecutorService, BlockingQueue)
2. For Handler: single-thread executor + message queue
3. For AsyncTask: thread pool + callbacks
4. For sync-over-async: CompletableFuture wrapping OH Promise (in bridge)
5. Test with concurrent calls to verify thread safety
6. Add timeout to all blocking operations to prevent deadlock

## Dependencies

Check if these related classes are already shimmed before generating `android.bluetooth.BluetoothSocket`:


## Quality Gates

Before marking `android.bluetooth.BluetoothSocket` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
