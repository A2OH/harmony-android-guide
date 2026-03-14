# SKILL: android.view.ViewParent

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ViewParent`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ViewParent` |
| **Package** | `android.view` |
| **Total Methods** | 43 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (4%) |
| **No Mapping** | 41 (95%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 43 |
| **Has Async Gap** | 43 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 43 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getParent` | 3 | composite | Return safe default (null/false/0/empty) |
| `createContextMenu` | 3 | composite | Return dummy instance / no-op |
| `bringChildToFront` | 1 | none | Store callback, never fire |
| `canResolveLayoutDirection` | 1 | none | Return safe default (null/false/0/empty) |
| `canResolveTextAlignment` | 1 | none | Return safe default (null/false/0/empty) |
| `canResolveTextDirection` | 1 | none | Return safe default (null/false/0/empty) |
| `childDrawableStateChanged` | 1 | none | throw UnsupportedOperationException |
| `childHasTransientStateChanged` | 1 | none | Return safe default (null/false/0/empty) |
| `clearChildFocus` | 1 | none | throw UnsupportedOperationException |
| `focusSearch` | 1 | none | throw UnsupportedOperationException |
| `focusableViewAvailable` | 1 | none | throw UnsupportedOperationException |
| `getChildVisibleRect` | 1 | none | Return safe default (null/false/0/empty) |
| `getLayoutDirection` | 1 | none | Return safe default (null/false/0/empty) |
| `getParentForAccessibility` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextAlignment` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextDirection` | 1 | none | Return safe default (null/false/0/empty) |
| `isLayoutDirectionResolved` | 1 | none | Return safe default (null/false/0/empty) |
| `isLayoutRequested` | 1 | none | Return safe default (null/false/0/empty) |
| `isTextAlignmentResolved` | 1 | none | Return safe default (null/false/0/empty) |
| `isTextDirectionResolved` | 1 | none | Return safe default (null/false/0/empty) |
| `keyboardNavigationClusterSearch` | 1 | none | Store callback, never fire |
| `notifySubtreeAccessibilityStateChanged` | 1 | none | throw UnsupportedOperationException |
| `onDescendantInvalidated` | 1 | none | Store callback, never fire |
| `onNestedFling` | 1 | none | Store callback, never fire |
| `onNestedPreFling` | 1 | none | Store callback, never fire |
| `onNestedPrePerformAccessibilityAction` | 1 | none | Store callback, never fire |
| `onNestedPreScroll` | 1 | none | Store callback, never fire |
| `onNestedScroll` | 1 | none | Store callback, never fire |
| `onNestedScrollAccepted` | 1 | none | Store callback, never fire |
| `onStartNestedScroll` | 1 | none | Return dummy instance / no-op |
| `onStopNestedScroll` | 1 | none | No-op |
| `recomputeViewAttributes` | 1 | none | Log warning + no-op |
| `requestChildFocus` | 1 | none | throw UnsupportedOperationException |
| `requestChildRectangleOnScreen` | 1 | none | Store callback, never fire |
| `requestDisallowInterceptTouchEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `requestFitSystemWindows` | 1 | none | throw UnsupportedOperationException |
| `requestLayout` | 1 | none | throw UnsupportedOperationException |
| `requestSendAccessibilityEvent` | 1 | none | throw UnsupportedOperationException |
| `requestTransparentRegion` | 1 | none | Store callback, never fire |
| `showContextMenuForChild` | 1 | none | Store callback, never fire |
| `showContextMenuForChild` | 1 | none | Store callback, never fire |
| `startActionModeForChild` | 1 | none | Return dummy instance / no-op |
| `startActionModeForChild` | 1 | none | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ViewParent`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.ViewParent` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 43 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
