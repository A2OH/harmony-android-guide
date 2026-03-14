# SKILL: android.net.ConnectivityManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.ConnectivityManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.ConnectivityManager` |
| **Package** | `android.net` |
| **Total Methods** | 16 |
| **Avg Score** | 5.6 |
| **Scenario** | S7: Async/Threading Gap |
| **Strategy** | Promise wrapping, Handler/Looper emulation |
| **Direct/Near** | 10 (62%) |
| **Partial/Composite** | 4 (25%) |
| **No Mapping** | 2 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 5 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock with concurrency tests) |

## Implementable APIs (score >= 5): 10 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `reportNetworkConnectivity` | `void reportNetworkConnectivity(@Nullable android.net.Network, boolean)` | 9 | direct | hard | `reportNetConnected` | `@ohos.net.connection.` |
| `unregisterNetworkCallback` | `void unregisterNetworkCallback(@NonNull android.net.ConnectivityManager.NetworkCallback)` | 9 | direct | easy | `unregister` | `NET_CAPABILITY_MMS = 0` |
| `unregisterNetworkCallback` | `void unregisterNetworkCallback(@NonNull android.app.PendingIntent)` | 9 | direct | easy | `unregister` | `NET_CAPABILITY_MMS = 0` |
| `bindProcessToNetwork` | `boolean bindProcessToNetwork(@Nullable android.net.Network)` | 7 | near | impossible | `setAppNet` | `@ohos.net.connection.` |
| `isDefaultNetworkActive` | `boolean isDefaultNetworkActive()` | 7 | near | rewrite | `hasDefaultNet` | `@ohos.net.connection.` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback)` | 7 | near | impossible | `register` | `@ohos.net.connection.NetConnection` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback, @NonNull android.os.Handler)` | 7 | near | impossible | `register` | `@ohos.net.connection.NetConnection` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback, int)` | 7 | near | impossible | `register` | `@ohos.net.connection.NetConnection` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.net.ConnectivityManager.NetworkCallback, @NonNull android.os.Handler, int)` | 7 | near | impossible | `register` | `@ohos.net.connection.NetConnection` |
| `requestNetwork` | `void requestNetwork(@NonNull android.net.NetworkRequest, @NonNull android.app.PendingIntent)` | 7 | near | impossible | `register` | `@ohos.net.connection.NetConnection` |

## Gap Descriptions (per method)

- **`reportNetworkConnectivity`**: Direct equivalent
- **`unregisterNetworkCallback`**: Network callback
- **`unregisterNetworkCallback`**: Network callback
- **`bindProcessToNetwork`**: Binds app traffic to specific network
- **`isDefaultNetworkActive`**: Checks default network
- **`requestNetwork`**: NetSpecifier filters; callback-based
- **`requestNetwork`**: NetSpecifier filters; callback-based
- **`requestNetwork`**: NetSpecifier filters; callback-based
- **`requestNetwork`**: NetSpecifier filters; callback-based
- **`requestNetwork`**: NetSpecifier filters; callback-based

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addDefaultNetworkActiveListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeDefaultNetworkActiveListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `getConnectionOwnerUid` | 3 | composite | Return dummy instance / no-op |
| `getRestrictBackgroundStatus` | 2 | composite | Return safe default (null/false/0/empty) |
| `releaseNetworkRequest` | 1 | none | No-op |
| `requestBandwidthUpdate` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S7 — Async/Threading Gap**

1. Implement using Java concurrency primitives (ExecutorService, BlockingQueue)
2. For Handler: single-thread executor + message queue
3. For AsyncTask: thread pool + callbacks
4. For sync-over-async: CompletableFuture wrapping OH Promise (in bridge)
5. Test with concurrent calls to verify thread safety
6. Add timeout to all blocking operations to prevent deadlock

## Dependencies

Check if these related classes are already shimmed before generating `android.net.ConnectivityManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.ConnectivityManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 10 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
