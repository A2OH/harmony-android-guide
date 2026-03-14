# SKILL: android.view.InputDevice.MotionRange

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.InputDevice.MotionRange`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.InputDevice.MotionRange` |
| **Package** | `android.view.InputDevice` |
| **Total Methods** | 9 |
| **Avg Score** | 2.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (66%) |
| **No Mapping** | 3 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 9 |
| **Has Async Gap** | 9 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getMax` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMin` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRange` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAxis` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFlat` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFuzz` | 3 | composite | Return safe default (null/false/0/empty) |
| `getResolution` | 1 | none | Return safe default (null/false/0/empty) |
| `getSource` | 1 | none | Return safe default (null/false/0/empty) |
| `isFromSource` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.InputDevice.MotionRange`:


## Quality Gates

Before marking `android.view.InputDevice.MotionRange` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
