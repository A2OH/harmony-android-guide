# SKILL: android.net.Network

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.Network`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.Network` |
| **Package** | `android.net` |
| **Total Methods** | 12 |
| **Avg Score** | 4.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 4 (33%) |
| **Partial/Composite** | 5 (41%) |
| **No Mapping** | 3 (25%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `bindSocket` | `void bindSocket(java.net.DatagramSocket) throws java.io.IOException` | 9 | direct | hard | `bindSocket` | `@ohos.net.connection.NetHandle` |
| `bindSocket` | `void bindSocket(java.net.Socket) throws java.io.IOException` | 9 | direct | hard | `bindSocket` | `@ohos.net.connection.NetHandle` |
| `bindSocket` | `void bindSocket(java.io.FileDescriptor) throws java.io.IOException` | 9 | direct | hard | `bindSocket` | `@ohos.net.connection.NetHandle` |
| `getAllByName` | `java.net.InetAddress[] getAllByName(String) throws java.net.UnknownHostException` | 9 | direct | rewrite | `getAddressesByName` | `@ohos.net.connection.NetHandle` |

## Gap Descriptions (per method)

- **`bindSocket`**: Direct equivalent
- **`bindSocket`**: Direct equivalent
- **`bindSocket`**: Direct equivalent
- **`getAllByName`**: Direct DNS resolution

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getByName` | 3 | composite | Return safe default (null/false/0/empty) |
| `openConnection` | 3 | composite | Return dummy instance / no-op |
| `openConnection` | 3 | composite | Return dummy instance / no-op |
| `getNetworkHandle` | 2 | composite | Return safe default (null/false/0/empty) |
| `getSocketFactory` | 2 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `fromNetworkHandle` | 1 | none | throw UnsupportedOperationException |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.net.Network`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.net.Network` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
