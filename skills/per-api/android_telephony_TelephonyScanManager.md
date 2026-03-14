# SKILL: android.telephony.TelephonyScanManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.TelephonyScanManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.TelephonyScanManager` |
| **Package** | `android.telephony` |
| **Total Methods** | 5 |
| **Avg Score** | 2.2 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (40%) |
| **No Mapping** | 3 (60%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onComplete` | `void onComplete()` | 5 | partial | moderate | `onComplete` | `onComplete: (reason: number, total: number) => void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `NetworkScanCallback` | 3 | composite | Return safe default (null/false/0/empty) |
| `TelephonyScanManager` | 1 | none | Return safe default (null/false/0/empty) |
| `onError` | 1 | none | Store callback, never fire |
| `onResults` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.TelephonyScanManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.TelephonyScanManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
