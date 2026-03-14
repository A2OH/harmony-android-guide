# SKILL: android.net.LocalSocket

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.LocalSocket`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.LocalSocket` |
| **Package** | `android.net` |
| **Total Methods** | 27 |
| **Avg Score** | 2.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 17 (62%) |
| **No Mapping** | 10 (37%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isClosed` | `boolean isClosed()` | 5 | partial | moderate | `isClosed` | `isClosed: boolean` |
| `isConnected` | `boolean isConnected()` | 5 | partial | moderate | `isConnected` | `isConnected(): boolean` |

## Stub APIs (score < 5): 25 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `close` | 3 | composite | No-op |
| `setReceiveBufferSize` | 3 | composite | Log warning + no-op |
| `LocalSocket` | 3 | composite | throw UnsupportedOperationException |
| `LocalSocket` | 3 | composite | throw UnsupportedOperationException |
| `bind` | 3 | composite | throw UnsupportedOperationException |
| `connect` | 3 | composite | Return dummy instance / no-op |
| `connect` | 3 | composite | Return dummy instance / no-op |
| `getFileDescriptor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getLocalSocketAddress` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRemoteSocketAddress` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInputStream` | 2 | composite | Return safe default (null/false/0/empty) |
| `isBound` | 2 | composite | Return safe default (null/false/0/empty) |
| `getOutputStream` | 2 | composite | Return safe default (null/false/0/empty) |
| `getReceiveBufferSize` | 2 | composite | Return safe default (null/false/0/empty) |
| `getPeerCredentials` | 2 | composite | Return safe default (null/false/0/empty) |
| `getAncillaryFileDescriptors` | 1 | none | Return safe default (null/false/0/empty) |
| `getSendBufferSize` | 1 | none | Return safe default (null/false/0/empty) |
| `getSoTimeout` | 1 | none | Return safe default (null/false/0/empty) |
| `isInputShutdown` | 1 | none | Return safe default (null/false/0/empty) |
| `isOutputShutdown` | 1 | none | Return safe default (null/false/0/empty) |
| `setFileDescriptorsForSend` | 1 | none | Log warning + no-op |
| `setSendBufferSize` | 1 | none | Log warning + no-op |
| `setSoTimeout` | 1 | none | Log warning + no-op |
| `shutdownInput` | 1 | none | Log warning + no-op |
| `shutdownOutput` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.net.LocalSocket`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.LocalSocket` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 27 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
