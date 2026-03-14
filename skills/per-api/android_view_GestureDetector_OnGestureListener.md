# SKILL: android.view.GestureDetector.OnGestureListener

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.GestureDetector.OnGestureListener`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.GestureDetector.OnGestureListener` |
| **Package** | `android.view.GestureDetector` |
| **Total Methods** | 6 |
| **Avg Score** | 4.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 3 (50%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 3 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 6 |
| **Has Async Gap** | 6 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onFling` | `boolean onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)` | 7 | near | impossible | `SwipeGesture().onAction` | `@internal/component/ets/gesture.SwipeGestureInterface` |
| `onLongPress` | `void onLongPress(android.view.MotionEvent)` | 7 | near | impossible | `LongPressGesture().onAction` | `@internal/component/ets/gesture.LongPressGestureInterface` |
| `onScroll` | `boolean onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)` | 7 | near | impossible | `PanGesture().onActionUpdate` | `@internal/component/ets/gesture.PanGestureInterface` |

## Gap Descriptions (per method)

- **`onFling`**: SwipeGestureEvent has angle+speed
- **`onLongPress`**: Near equivalent
- **`onScroll`**: offsetX/Y/velocity

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onDown` | 1 | none | Store callback, never fire |
| `onShowPress` | 1 | none | Store callback, never fire |
| `onSingleTapUp` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.GestureDetector.OnGestureListener`:


## Quality Gates

Before marking `android.view.GestureDetector.OnGestureListener` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
