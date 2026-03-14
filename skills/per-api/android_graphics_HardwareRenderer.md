# SKILL: android.graphics.HardwareRenderer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.HardwareRenderer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.HardwareRenderer` |
| **Package** | `android.graphics` |
| **Total Methods** | 13 |
| **Avg Score** | 1.5 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (23%) |
| **No Mapping** | 10 (76%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 13 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `start` | 3 | composite | Return dummy instance / no-op |
| `stop` | 3 | composite | No-op |
| `destroy` | 2 | composite | No-op |
| `HardwareRenderer` | 1 | none | throw UnsupportedOperationException |
| `clearContent` | 1 | none | Store callback, never fire |
| `isOpaque` | 1 | none | Return safe default (null/false/0/empty) |
| `notifyFramePending` | 1 | none | throw UnsupportedOperationException |
| `setContentRoot` | 1 | none | Log warning + no-op |
| `setLightSourceAlpha` | 1 | none | Log warning + no-op |
| `setLightSourceGeometry` | 1 | none | Log warning + no-op |
| `setName` | 1 | none | Log warning + no-op |
| `setOpaque` | 1 | none | Log warning + no-op |
| `setSurface` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.HardwareRenderer`:


## Quality Gates

Before marking `android.graphics.HardwareRenderer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
