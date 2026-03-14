# SKILL: android.widget.Toolbar

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.Toolbar`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.Toolbar` |
| **Package** | `android.widget` |
| **Total Methods** | 67 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (5%) |
| **No Mapping** | 63 (94%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 63 |
| **Has Async Gap** | 67 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 67 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getLogo` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMenu` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitle` | 2 | composite | Return safe default (null/false/0/empty) |
| `collapseActionView` | 2 | composite | Store callback, never fire |
| `Toolbar` | 1 | none | throw UnsupportedOperationException |
| `Toolbar` | 1 | none | throw UnsupportedOperationException |
| `Toolbar` | 1 | none | throw UnsupportedOperationException |
| `Toolbar` | 1 | none | throw UnsupportedOperationException |
| `dismissPopupMenus` | 1 | none | Return safe default (null/false/0/empty) |
| `generateDefaultLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `generateLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `generateLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `getContentInsetEnd` | 1 | none | Return safe default (null/false/0/empty) |
| `getContentInsetEndWithActions` | 1 | none | Return safe default (null/false/0/empty) |
| `getContentInsetLeft` | 1 | none | Return safe default (null/false/0/empty) |
| `getContentInsetRight` | 1 | none | Return safe default (null/false/0/empty) |
| `getContentInsetStart` | 1 | none | Return dummy instance / no-op |
| `getContentInsetStartWithNavigation` | 1 | none | Return dummy instance / no-op |
| `getCurrentContentInsetEnd` | 1 | none | Return safe default (null/false/0/empty) |
| `getCurrentContentInsetLeft` | 1 | none | Return safe default (null/false/0/empty) |
| `getCurrentContentInsetRight` | 1 | none | Return safe default (null/false/0/empty) |
| `getCurrentContentInsetStart` | 1 | none | Return dummy instance / no-op |
| `getLogoDescription` | 1 | none | Return safe default (null/false/0/empty) |
| `getPopupTheme` | 1 | none | Return safe default (null/false/0/empty) |
| `getSubtitle` | 1 | none | Return safe default (null/false/0/empty) |
| `getTitleMarginBottom` | 1 | none | Return safe default (null/false/0/empty) |
| `getTitleMarginEnd` | 1 | none | Return safe default (null/false/0/empty) |
| `getTitleMarginStart` | 1 | none | Return dummy instance / no-op |
| `getTitleMarginTop` | 1 | none | Return safe default (null/false/0/empty) |
| `hasExpandedActionView` | 1 | none | Return safe default (null/false/0/empty) |
| `hideOverflowMenu` | 1 | none | throw UnsupportedOperationException |
| `inflateMenu` | 1 | none | throw UnsupportedOperationException |
| `isOverflowMenuShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `setCollapseContentDescription` | 1 | none | Log warning + no-op |
| `setCollapseContentDescription` | 1 | none | Log warning + no-op |
| `setCollapseIcon` | 1 | none | Log warning + no-op |
| `setCollapseIcon` | 1 | none | Log warning + no-op |
| `setContentInsetEndWithActions` | 1 | none | Log warning + no-op |
| `setContentInsetStartWithNavigation` | 1 | none | Return dummy instance / no-op |
| `setContentInsetsAbsolute` | 1 | none | Log warning + no-op |
| `setContentInsetsRelative` | 1 | none | Log warning + no-op |
| `setLogo` | 1 | none | Log warning + no-op |
| `setLogo` | 1 | none | Log warning + no-op |
| `setLogoDescription` | 1 | none | Log warning + no-op |
| `setLogoDescription` | 1 | none | Log warning + no-op |
| `setNavigationContentDescription` | 1 | none | Log warning + no-op |
| `setNavigationContentDescription` | 1 | none | Log warning + no-op |
| `setNavigationIcon` | 1 | none | Log warning + no-op |
| `setNavigationIcon` | 1 | none | Log warning + no-op |
| `setNavigationOnClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnMenuItemClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOverflowIcon` | 1 | none | Log warning + no-op |
| `setPopupTheme` | 1 | none | Log warning + no-op |
| `setSubtitle` | 1 | none | Log warning + no-op |
| `setSubtitle` | 1 | none | Log warning + no-op |
| `setSubtitleTextAppearance` | 1 | none | Log warning + no-op |
| `setSubtitleTextColor` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |
| `setTitleMargin` | 1 | none | Log warning + no-op |
| `setTitleMarginBottom` | 1 | none | Log warning + no-op |
| `setTitleMarginEnd` | 1 | none | Log warning + no-op |
| `setTitleMarginStart` | 1 | none | Return dummy instance / no-op |
| `setTitleMarginTop` | 1 | none | Log warning + no-op |
| `setTitleTextAppearance` | 1 | none | Log warning + no-op |
| `setTitleTextColor` | 1 | none | Log warning + no-op |
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

Check if these related classes are already shimmed before generating `android.widget.Toolbar`:

- `android.view.View` (already shimmed)
- `android.view.ViewGroup` (already shimmed)

## Quality Gates

Before marking `android.widget.Toolbar` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 67 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
