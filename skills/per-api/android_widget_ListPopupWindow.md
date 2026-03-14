# SKILL: android.widget.ListPopupWindow

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.ListPopupWindow`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.ListPopupWindow` |
| **Package** | `android.widget` |
| **Total Methods** | 47 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (8%) |
| **No Mapping** | 43 (91%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 43 |
| **Has Async Gap** | 43 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 47 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `show` | 3 | composite | throw UnsupportedOperationException |
| `createDragToOpenListener` | 3 | composite | Return dummy instance / no-op |
| `getSelectedItemId` | 2 | composite | Return safe default (null/false/0/empty) |
| `getWidth` | 2 | composite | Return safe default (null/false/0/empty) |
| `ListPopupWindow` | 1 | none | Return safe default (null/false/0/empty) |
| `ListPopupWindow` | 1 | none | Return safe default (null/false/0/empty) |
| `ListPopupWindow` | 1 | none | Return safe default (null/false/0/empty) |
| `ListPopupWindow` | 1 | none | Return safe default (null/false/0/empty) |
| `clearListSelection` | 1 | none | Return safe default (null/false/0/empty) |
| `dismiss` | 1 | none | Return safe default (null/false/0/empty) |
| `getHeight` | 1 | none | Return safe default (null/false/0/empty) |
| `getHorizontalOffset` | 1 | none | Return safe default (null/false/0/empty) |
| `getInputMethodMode` | 1 | none | Return safe default (null/false/0/empty) |
| `getPromptPosition` | 1 | none | Return safe default (null/false/0/empty) |
| `getSelectedItemPosition` | 1 | none | Return safe default (null/false/0/empty) |
| `getSoftInputMode` | 1 | none | Return safe default (null/false/0/empty) |
| `getVerticalOffset` | 1 | none | Return safe default (null/false/0/empty) |
| `isInputMethodNotNeeded` | 1 | none | Return safe default (null/false/0/empty) |
| `isModal` | 1 | none | Return safe default (null/false/0/empty) |
| `isShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `onKeyDown` | 1 | none | Store callback, never fire |
| `onKeyPreIme` | 1 | none | Store callback, never fire |
| `onKeyUp` | 1 | none | Store callback, never fire |
| `performItemClick` | 1 | none | throw UnsupportedOperationException |
| `postShow` | 1 | none | throw UnsupportedOperationException |
| `setAdapter` | 1 | none | Log warning + no-op |
| `setAnchorView` | 1 | none | Log warning + no-op |
| `setAnimationStyle` | 1 | none | Log warning + no-op |
| `setBackgroundDrawable` | 1 | none | Log warning + no-op |
| `setContentWidth` | 1 | none | Log warning + no-op |
| `setDropDownGravity` | 1 | none | Log warning + no-op |
| `setEpicenterBounds` | 1 | none | Log warning + no-op |
| `setHeight` | 1 | none | Log warning + no-op |
| `setHorizontalOffset` | 1 | none | Log warning + no-op |
| `setInputMethodMode` | 1 | none | Log warning + no-op |
| `setListSelector` | 1 | none | Return safe default (null/false/0/empty) |
| `setModal` | 1 | none | Log warning + no-op |
| `setOnDismissListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnItemClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnItemSelectedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setPromptPosition` | 1 | none | Log warning + no-op |
| `setPromptView` | 1 | none | Log warning + no-op |
| `setSelection` | 1 | none | Log warning + no-op |
| `setSoftInputMode` | 1 | none | Log warning + no-op |
| `setVerticalOffset` | 1 | none | Log warning + no-op |
| `setWidth` | 1 | none | Log warning + no-op |
| `setWindowLayoutType` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.ListPopupWindow`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.ListPopupWindow` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 47 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
