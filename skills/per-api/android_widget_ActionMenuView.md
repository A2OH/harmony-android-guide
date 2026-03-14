# SKILL: android.widget.ActionMenuView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.ActionMenuView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.ActionMenuView` |
| **Package** | `android.widget` |
| **Total Methods** | 16 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (6%) |
| **No Mapping** | 15 (93%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 14 |
| **Has Async Gap** | 14 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 16 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getMenu` | 3 | composite | Return safe default (null/false/0/empty) |
| `ActionMenuView` | 1 | none | Store callback, never fire |
| `ActionMenuView` | 1 | none | Store callback, never fire |
| `dismissPopupMenus` | 1 | none | Return safe default (null/false/0/empty) |
| `generateDefaultLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `generateLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `generateLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `getPopupTheme` | 1 | none | Return safe default (null/false/0/empty) |
| `hideOverflowMenu` | 1 | none | throw UnsupportedOperationException |
| `isOverflowMenuShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `onConfigurationChanged` | 1 | none | Store callback, never fire |
| `onDetachedFromWindow` | 1 | none | Store callback, never fire |
| `setOnMenuItemClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOverflowIcon` | 1 | none | Log warning + no-op |
| `setPopupTheme` | 1 | none | Log warning + no-op |
| `showOverflowMenu` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.ActionMenuView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.ActionMenuView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
