# SKILL: android.view.MenuItem

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.MenuItem`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.MenuItem` |
| **Package** | `android.view` |
| **Total Methods** | 51 |
| **Avg Score** | 1.6 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 1 (1%) |
| **Partial/Composite** | 15 (29%) |
| **No Mapping** | 35 (68%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 51 |
| **Has Async Gap** | 51 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setOnMenuItemClickListener` | `android.view.MenuItem setOnMenuItemClickListener(android.view.MenuItem.OnMenuItemClickListener)` | 7 | near | impossible | `action` | `@internal/component/ets/common.MenuElement` |

## Gap Descriptions (per method)

- **`setOnMenuItemClickListener`**: Action callback

## Stub APIs (score < 5): 50 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getActionProvider` | 3 | composite | Return safe default (null/false/0/empty) |
| `getActionView` | 3 | composite | Return safe default (null/false/0/empty) |
| `isEnabled` | 3 | composite | Return safe default (null/false/0/empty) |
| `isVisible` | 3 | composite | Return safe default (null/false/0/empty) |
| `getIcon` | 3 | composite | Return safe default (null/false/0/empty) |
| `setShowAsAction` | 3 | composite | Log warning + no-op |
| `expandActionView` | 2 | composite | Store callback, never fire |
| `getItemId` | 2 | composite | Return safe default (null/false/0/empty) |
| `setActionView` | 2 | composite | Log warning + no-op |
| `setActionView` | 2 | composite | Log warning + no-op |
| `getOrder` | 2 | composite | Return safe default (null/false/0/empty) |
| `getTitle` | 2 | composite | Return safe default (null/false/0/empty) |
| `collapseActionView` | 2 | composite | Store callback, never fire |
| `getIntent` | 2 | composite | Return safe default (null/false/0/empty) |
| `setIntent` | 2 | composite | Log warning + no-op |
| `getAlphabeticModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `getAlphabeticShortcut` | 1 | none | Return safe default (null/false/0/empty) |
| `getContentDescription` | 1 | none | Return safe default (null/false/0/empty) |
| `getGroupId` | 1 | none | Return safe default (null/false/0/empty) |
| `getMenuInfo` | 1 | none | Return safe default (null/false/0/empty) |
| `getNumericModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `getNumericShortcut` | 1 | none | Return safe default (null/false/0/empty) |
| `getSubMenu` | 1 | none | Return safe default (null/false/0/empty) |
| `getTitleCondensed` | 1 | none | Return safe default (null/false/0/empty) |
| `getTooltipText` | 1 | none | Return safe default (null/false/0/empty) |
| `hasSubMenu` | 1 | none | Return safe default (null/false/0/empty) |
| `isActionViewExpanded` | 1 | none | Return safe default (null/false/0/empty) |
| `isCheckable` | 1 | none | Return safe default (null/false/0/empty) |
| `isChecked` | 1 | none | Return safe default (null/false/0/empty) |
| `setActionProvider` | 1 | none | Log warning + no-op |
| `setAlphabeticShortcut` | 1 | none | Log warning + no-op |
| `setAlphabeticShortcut` | 1 | none | Log warning + no-op |
| `setCheckable` | 1 | none | Log warning + no-op |
| `setChecked` | 1 | none | Log warning + no-op |
| `setContentDescription` | 1 | none | Log warning + no-op |
| `setEnabled` | 1 | none | Log warning + no-op |
| `setIcon` | 1 | none | Log warning + no-op |
| `setIcon` | 1 | none | Log warning + no-op |
| `setIconTintList` | 1 | none | Return safe default (null/false/0/empty) |
| `setNumericShortcut` | 1 | none | Log warning + no-op |
| `setNumericShortcut` | 1 | none | Log warning + no-op |
| `setOnActionExpandListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setShortcut` | 1 | none | Log warning + no-op |
| `setShortcut` | 1 | none | Log warning + no-op |
| `setShowAsActionFlags` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |
| `setTitleCondensed` | 1 | none | Log warning + no-op |
| `setTooltipText` | 1 | none | Log warning + no-op |
| `setVisible` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.MenuItem`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.MenuItem` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 51 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
