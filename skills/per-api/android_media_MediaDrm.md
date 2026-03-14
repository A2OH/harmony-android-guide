# SKILL: android.media.MediaDrm

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaDrm`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaDrm` |
| **Package** | `android.media` |
| **Total Methods** | 34 |
| **Avg Score** | 2.2 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 15 (44%) |
| **No Mapping** | 19 (55%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getOpenSessionCount` | `int getOpenSessionCount()` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 33 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `MediaDrm` | 5 | partial | throw UnsupportedOperationException |
| `closeSession` | 4 | partial | No-op |
| `close` | 4 | partial | No-op |
| `setOnEventListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `setOnEventListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `setOnEventListener` | 4 | partial | Return safe default (null/false/0/empty) |
| `releaseSecureStops` | 4 | composite | No-op |
| `isCryptoSchemeSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `isCryptoSchemeSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `isCryptoSchemeSupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMetrics` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCryptoSession` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxSessionCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOfflineLicenseState` | 2 | composite | Return safe default (null/false/0/empty) |
| `clearOnEventListener` | 1 | none | Return safe default (null/false/0/empty) |
| `clearOnExpirationUpdateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `clearOnKeyStatusChangeListener` | 1 | none | Return safe default (null/false/0/empty) |
| `clearOnSessionLostStateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `getMaxSecurityLevel` | 1 | none | Return safe default (null/false/0/empty) |
| `provideProvisionResponse` | 1 | none | Return safe default (null/false/0/empty) |
| `removeAllSecureStops` | 1 | none | No-op |
| `removeKeys` | 1 | none | Log warning + no-op |
| `removeOfflineLicense` | 1 | none | Log warning + no-op |
| `removeSecureStop` | 1 | none | No-op |
| `restoreKeys` | 1 | none | throw UnsupportedOperationException |
| `setOnExpirationUpdateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnExpirationUpdateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnKeyStatusChangeListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnKeyStatusChangeListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnSessionLostStateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnSessionLostStateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setPropertyByteArray` | 1 | none | Log warning + no-op |
| `setPropertyString` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaDrm`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaDrm` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 34 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
