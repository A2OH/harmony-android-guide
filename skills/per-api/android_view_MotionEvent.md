# SKILL: android.view.MotionEvent

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.MotionEvent`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.MotionEvent` |
| **Package** | `android.view` |
| **Total Methods** | 87 |
| **Avg Score** | 2.3 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 12 (13%) |
| **Partial/Composite** | 19 (21%) |
| **No Mapping** | 56 (64%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 87 |
| **Has Async Gap** | 75 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getPressure` | `float getPressure()` | 9 | direct | hard | `pressure` | `@ohos.multimodalInput.touchEvent.Touch` |
| `getPressure` | `float getPressure(int)` | 9 | direct | hard | `pressure` | `@ohos.multimodalInput.touchEvent.Touch` |
| `getRawX` | `float getRawX()` | 9 | direct | rewrite | `screenX` | `@ohos.multimodalInput.touchEvent.Touch` |
| `getRawX` | `float getRawX(int)` | 9 | direct | rewrite | `screenX` | `@ohos.multimodalInput.touchEvent.Touch` |
| `getRawY` | `float getRawY()` | 9 | direct | rewrite | `screenY` | `@ohos.multimodalInput.touchEvent.Touch` |
| `getRawY` | `float getRawY(int)` | 9 | direct | rewrite | `screenY` | `@ohos.multimodalInput.touchEvent.Touch` |
| `getAction` | `int getAction()` | 7 | near | rewrite | `action` | `@ohos.multimodalInput.touchEvent.TouchEvent` |
| `getPointerCount` | `int getPointerCount()` | 7 | near | impossible | `touches.length` | `@ohos.multimodalInput.touchEvent.TouchEvent` |
| `getX` | `float getX()` | 7 | near | hard | `windowX` | `@ohos.multimodalInput.touchEvent.Touch` |
| `getX` | `float getX(int)` | 7 | near | hard | `windowX` | `@ohos.multimodalInput.touchEvent.Touch` |
| `getY` | `float getY()` | 7 | near | hard | `windowY` | `@ohos.multimodalInput.touchEvent.Touch` |
| `getY` | `float getY(int)` | 7 | near | hard | `windowY` | `@ohos.multimodalInput.touchEvent.Touch` |

## Gap Descriptions (per method)

- **`getPressure`**: Direct equivalent
- **`getPressure`**: Direct equivalent
- **`getRawX`**: Screen-relative
- **`getRawX`**: Screen-relative
- **`getRawY`**: Screen-relative
- **`getRawY`**: Screen-relative
- **`getAction`**: Different enum values
- **`getPointerCount`**: Array of Touch
- **`getX`**: Window-relative
- **`getX`**: Window-relative
- **`getY`**: Window-relative
- **`getY`**: Window-relative

## Stub APIs (score < 5): 75 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDeviceId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalPressure` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalPressure` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPointerProperties` | 3 | composite | Return safe default (null/false/0/empty) |
| `getXPrecision` | 3 | composite | Return safe default (null/false/0/empty) |
| `getYPrecision` | 3 | composite | Return safe default (null/false/0/empty) |
| `setAction` | 3 | composite | Log warning + no-op |
| `getSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalPointerCoords` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFlags` | 2 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalOrientation` | 2 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalOrientation` | 2 | composite | Return safe default (null/false/0/empty) |
| `getOrientation` | 2 | composite | Return safe default (null/false/0/empty) |
| `getOrientation` | 2 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalToolMinor` | 2 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalToolMinor` | 2 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalTouchMinor` | 2 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalTouchMinor` | 2 | composite | Return safe default (null/false/0/empty) |
| `actionToString` | 1 | none | Store callback, never fire |
| `addBatch` | 1 | none | Log warning + no-op |
| `addBatch` | 1 | none | Log warning + no-op |
| `axisFromString` | 1 | none | Return safe default (null/false/0/empty) |
| `axisToString` | 1 | none | Return safe default (null/false/0/empty) |
| `findPointerIndex` | 1 | none | Return safe default (null/false/0/empty) |
| `getActionButton` | 1 | none | Return safe default (null/false/0/empty) |
| `getActionIndex` | 1 | none | Return safe default (null/false/0/empty) |
| `getActionMasked` | 1 | none | Return safe default (null/false/0/empty) |
| `getAxisValue` | 1 | none | Return safe default (null/false/0/empty) |
| `getAxisValue` | 1 | none | Return safe default (null/false/0/empty) |
| `getButtonState` | 1 | none | Return safe default (null/false/0/empty) |
| `getClassification` | 1 | none | Return safe default (null/false/0/empty) |
| `getDownTime` | 1 | none | Return safe default (null/false/0/empty) |
| `getEdgeFlags` | 1 | none | Return safe default (null/false/0/empty) |
| `getEventTime` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalAxisValue` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalAxisValue` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalEventTime` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalSize` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalSize` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalToolMajor` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalToolMajor` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalTouchMajor` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalTouchMajor` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalX` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalX` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalY` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistoricalY` | 1 | none | Return safe default (null/false/0/empty) |
| `getHistorySize` | 1 | none | Return safe default (null/false/0/empty) |
| `getMetaState` | 1 | none | Return safe default (null/false/0/empty) |
| `getPointerCoords` | 1 | none | Return safe default (null/false/0/empty) |
| `getPointerId` | 1 | none | Return safe default (null/false/0/empty) |
| `getSource` | 1 | none | Return safe default (null/false/0/empty) |
| `getToolMajor` | 1 | none | Return safe default (null/false/0/empty) |
| `getToolMajor` | 1 | none | Return safe default (null/false/0/empty) |
| `getToolMinor` | 1 | none | Return safe default (null/false/0/empty) |
| `getToolMinor` | 1 | none | Return safe default (null/false/0/empty) |
| `getToolType` | 1 | none | Return safe default (null/false/0/empty) |
| `getTouchMajor` | 1 | none | Return safe default (null/false/0/empty) |
| `getTouchMajor` | 1 | none | Return safe default (null/false/0/empty) |
| `getTouchMinor` | 1 | none | Return safe default (null/false/0/empty) |
| `getTouchMinor` | 1 | none | Return safe default (null/false/0/empty) |
| `isButtonPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtainNoHistory` | 1 | none | Return safe default (null/false/0/empty) |
| `offsetLocation` | 1 | none | Log warning + no-op |
| `recycle` | 1 | none | throw UnsupportedOperationException |
| `setEdgeFlags` | 1 | none | Log warning + no-op |
| `setLocation` | 1 | none | Log warning + no-op |
| `setSource` | 1 | none | Log warning + no-op |
| `transform` | 1 | none | throw UnsupportedOperationException |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.MotionEvent`:

- `android.view.View` (already shimmed)

## Quality Gates

Before marking `android.view.MotionEvent` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 87 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
