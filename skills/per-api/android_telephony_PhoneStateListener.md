# SKILL: android.telephony.PhoneStateListener

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.PhoneStateListener`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.PhoneStateListener` |
| **Package** | `android.telephony` |
| **Total Methods** | 16 |
| **Avg Score** | 1.4 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (18%) |
| **No Mapping** | 13 (81%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 16 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onCallStateChanged` | 3 | composite | Store callback, never fire |
| `onCellInfoChanged` | 3 | composite | Store callback, never fire |
| `onCellLocationChanged` | 3 | composite | Store callback, never fire |
| `PhoneStateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `PhoneStateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `onActiveDataSubscriptionIdChanged` | 1 | none | Store callback, never fire |
| `onBarringInfoChanged` | 1 | none | Store callback, never fire |
| `onCallForwardingIndicatorChanged` | 1 | none | Store callback, never fire |
| `onDataActivity` | 1 | none | Store callback, never fire |
| `onDataConnectionStateChanged` | 1 | none | Return dummy instance / no-op |
| `onDataConnectionStateChanged` | 1 | none | Return dummy instance / no-op |
| `onMessageWaitingIndicatorChanged` | 1 | none | Store callback, never fire |
| `onRegistrationFailed` | 1 | none | Return safe default (null/false/0/empty) |
| `onServiceStateChanged` | 1 | none | Store callback, never fire |
| `onSignalStrengthsChanged` | 1 | none | Store callback, never fire |
| `onUserMobileDataStateChanged` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.PhoneStateListener`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.PhoneStateListener` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
