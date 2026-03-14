# SKILL: android.view.ViewConfiguration

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ViewConfiguration`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ViewConfiguration` |
| **Package** | `android.view` |
| **Total Methods** | 31 |
| **Avg Score** | 1.2 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (9%) |
| **No Mapping** | 28 (90%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 31 |
| **Has Async Gap** | 31 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 31 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `get` | 3 | composite | Return safe default (null/false/0/empty) |
| `getLongPressTimeout` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTapTimeout` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDefaultActionModeHideDuration` | 1 | none | Return safe default (null/false/0/empty) |
| `getDoubleTapTimeout` | 1 | none | Return safe default (null/false/0/empty) |
| `getJumpTapTimeout` | 1 | none | Return safe default (null/false/0/empty) |
| `getKeyRepeatDelay` | 1 | none | Return safe default (null/false/0/empty) |
| `getKeyRepeatTimeout` | 1 | none | Return safe default (null/false/0/empty) |
| `getPressedStateDuration` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledDoubleTapSlop` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledEdgeSlop` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledFadingEdgeLength` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledHorizontalScrollFactor` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledHoverSlop` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledMaximumDrawingCacheSize` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledMaximumFlingVelocity` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledMinimumFlingVelocity` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledMinimumScalingSpan` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledOverflingDistance` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledOverscrollDistance` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledPagingTouchSlop` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledScrollBarSize` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledTouchSlop` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledVerticalScrollFactor` | 1 | none | Return safe default (null/false/0/empty) |
| `getScaledWindowTouchSlop` | 1 | none | Return safe default (null/false/0/empty) |
| `getScrollBarFadeDuration` | 1 | none | Return safe default (null/false/0/empty) |
| `getScrollDefaultDelay` | 1 | none | Return safe default (null/false/0/empty) |
| `getScrollFriction` | 1 | none | Return safe default (null/false/0/empty) |
| `getZoomControlsTimeout` | 1 | none | Return safe default (null/false/0/empty) |
| `hasPermanentMenuKey` | 1 | none | Return safe default (null/false/0/empty) |
| `shouldShowMenuShortcutsWhenKeyboardPresent` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ViewConfiguration`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.ViewConfiguration` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 31 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
