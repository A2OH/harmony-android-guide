# SKILL: android.net.ConnectivityManager.NetworkCallback

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.ConnectivityManager.NetworkCallback`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.ConnectivityManager.NetworkCallback` |
| **Package** | `android.net.ConnectivityManager` |
| **Total Methods** | 8 |
| **Avg Score** | 4.3 |
| **Scenario** | S7: Async/Threading Gap |
| **Strategy** | Promise wrapping, Handler/Looper emulation |
| **Direct/Near** | 3 (37%) |
| **Partial/Composite** | 1 (12%) |
| **No Mapping** | 4 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 3 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock with concurrency tests) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onAvailable` | `void onAvailable(@NonNull android.net.Network)` | 9 | direct | impossible | `on(netAvailable)` | `@ohos.net.connection.NetConnection` |
| `onCapabilitiesChanged` | `void onCapabilitiesChanged(@NonNull android.net.Network, @NonNull android.net.NetworkCapabilities)` | 9 | direct | hard | `on(netCapabilitiesChange)` | `@ohos.net.connection.NetConnection` |
| `onLost` | `void onLost(@NonNull android.net.Network)` | 9 | direct | impossible | `on(netLost)` | `@ohos.net.connection.NetConnection` |

## Gap Descriptions (per method)

- **`onAvailable`**: Direct callback mapping
- **`onCapabilitiesChanged`**: Direct callback mapping
- **`onLost`**: Direct callback mapping

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onLinkPropertiesChanged` | 3 | composite | Store callback, never fire |
| `NetworkCallback` | 1 | none | throw UnsupportedOperationException |
| `onBlockedStatusChanged` | 1 | none | Store callback, never fire |
| `onLosing` | 1 | none | Store callback, never fire |
| `onUnavailable` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S7 — Async/Threading Gap**

1. Implement using Java concurrency primitives (ExecutorService, BlockingQueue)
2. For Handler: single-thread executor + message queue
3. For AsyncTask: thread pool + callbacks
4. For sync-over-async: CompletableFuture wrapping OH Promise (in bridge)
5. Test with concurrent calls to verify thread safety
6. Add timeout to all blocking operations to prevent deadlock

## Dependencies

Check if these related classes are already shimmed before generating `android.net.ConnectivityManager.NetworkCallback`:


## Quality Gates

Before marking `android.net.ConnectivityManager.NetworkCallback` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
