# SKILL: android.widget.PopupWindow

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.PopupWindow`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.PopupWindow` |
| **Package** | `android.widget` |
| **Total Methods** | 69 |
| **Avg Score** | 1.5 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 1 (1%) |
| **Partial/Composite** | 11 (15%) |
| **No Mapping** | 57 (82%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 60 |
| **Has Async Gap** | 60 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `dismiss` | `void dismiss()` | 7 | near | impossible | `bindPopup(false)` | `@internal/component/ets/common.CommonAttribute` |
| `showAsDropDown` | `void showAsDropDown(android.view.View)` | 5 | partial | impossible | `bindPopup` | `@internal/component/ets/common.CommonAttribute` |
| `showAsDropDown` | `void showAsDropDown(android.view.View, int, int)` | 5 | partial | impossible | `bindPopup` | `@internal/component/ets/common.CommonAttribute` |
| `showAsDropDown` | `void showAsDropDown(android.view.View, int, int, int)` | 5 | partial | impossible | `bindPopup` | `@internal/component/ets/common.CommonAttribute` |

## Gap Descriptions (per method)

- **`dismiss`**: Set show to false
- **`showAsDropDown`**: Declarative popup
- **`showAsDropDown`**: Declarative popup
- **`showAsDropDown`**: Declarative popup

## Stub APIs (score < 5): 65 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getContentView` | 3 | composite | Return safe default (null/false/0/empty) |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `update` | 3 | composite | Log warning + no-op |
| `getWidth` | 2 | composite | Return safe default (null/false/0/empty) |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `PopupWindow` | 1 | none | throw UnsupportedOperationException |
| `getAnimationStyle` | 1 | none | Return safe default (null/false/0/empty) |
| `getBackground` | 1 | none | Return safe default (null/false/0/empty) |
| `getElevation` | 1 | none | Return safe default (null/false/0/empty) |
| `getHeight` | 1 | none | Return safe default (null/false/0/empty) |
| `getInputMethodMode` | 1 | none | Return safe default (null/false/0/empty) |
| `getMaxAvailableHeight` | 1 | none | Return safe default (null/false/0/empty) |
| `getMaxAvailableHeight` | 1 | none | Return safe default (null/false/0/empty) |
| `getMaxAvailableHeight` | 1 | none | Return safe default (null/false/0/empty) |
| `getOverlapAnchor` | 1 | none | Return safe default (null/false/0/empty) |
| `getSoftInputMode` | 1 | none | Return safe default (null/false/0/empty) |
| `getWindowLayoutType` | 1 | none | Return safe default (null/false/0/empty) |
| `isAboveAnchor` | 1 | none | Return safe default (null/false/0/empty) |
| `isAttachedInDecor` | 1 | none | Return safe default (null/false/0/empty) |
| `isClippedToScreen` | 1 | none | Return safe default (null/false/0/empty) |
| `isClippingEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `isFocusable` | 1 | none | Return safe default (null/false/0/empty) |
| `isLaidOutInScreen` | 1 | none | Return safe default (null/false/0/empty) |
| `isOutsideTouchable` | 1 | none | Return safe default (null/false/0/empty) |
| `isShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `isSplitTouchEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `isTouchModal` | 1 | none | Return safe default (null/false/0/empty) |
| `isTouchable` | 1 | none | Return safe default (null/false/0/empty) |
| `setAnimationStyle` | 1 | none | Log warning + no-op |
| `setAttachedInDecor` | 1 | none | Log warning + no-op |
| `setBackgroundDrawable` | 1 | none | Log warning + no-op |
| `setClippingEnabled` | 1 | none | Log warning + no-op |
| `setContentView` | 1 | none | Log warning + no-op |
| `setElevation` | 1 | none | Log warning + no-op |
| `setEnterTransition` | 1 | none | Log warning + no-op |
| `setEpicenterBounds` | 1 | none | Log warning + no-op |
| `setExitTransition` | 1 | none | Log warning + no-op |
| `setFocusable` | 1 | none | Log warning + no-op |
| `setHeight` | 1 | none | Log warning + no-op |
| `setIgnoreCheekPress` | 1 | none | Log warning + no-op |
| `setInputMethodMode` | 1 | none | Log warning + no-op |
| `setIsClippedToScreen` | 1 | none | Return safe default (null/false/0/empty) |
| `setIsLaidOutInScreen` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnDismissListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOutsideTouchable` | 1 | none | Log warning + no-op |
| `setOverlapAnchor` | 1 | none | Log warning + no-op |
| `setSoftInputMode` | 1 | none | Log warning + no-op |
| `setSplitTouchEnabled` | 1 | none | Log warning + no-op |
| `setTouchInterceptor` | 1 | none | Log warning + no-op |
| `setTouchModal` | 1 | none | Log warning + no-op |
| `setTouchable` | 1 | none | Log warning + no-op |
| `setWidth` | 1 | none | Log warning + no-op |
| `setWindowLayoutType` | 1 | none | Log warning + no-op |
| `showAtLocation` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.PopupWindow`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.PopupWindow` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 69 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
