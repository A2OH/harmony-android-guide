# SKILL: android.view.GestureDetector.OnDoubleTapListener

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.GestureDetector.OnDoubleTapListener`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.GestureDetector.OnDoubleTapListener` |
| **Package** | `android.view.GestureDetector` |
| **Total Methods** | 3 |
| **Avg Score** | 3.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 1 (33%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 2 (66%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 3 |
| **Has Async Gap** | 3 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onDoubleTap` | `boolean onDoubleTap(android.view.MotionEvent)` | 7 | near | impossible | `TapGesture({count:2}).onAction` | `@internal/component/ets/gesture.TapGestureInterface` |

## Gap Descriptions (per method)

- **`onDoubleTap`**: Double tap

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onDoubleTapEvent` | 1 | none | Store callback, never fire |
| `onSingleTapConfirmed` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.GestureDetector.OnDoubleTapListener`:


## Quality Gates

Before marking `android.view.GestureDetector.OnDoubleTapListener` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
