# SKILL: android.widget.AutoCompleteTextView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.AutoCompleteTextView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.AutoCompleteTextView` |
| **Package** | `android.widget` |
| **Total Methods** | 50 |
| **Avg Score** | 1.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 50 (100%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 45 |
| **Has Async Gap** | 45 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 50 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `AutoCompleteTextView` | 1 | none | throw UnsupportedOperationException |
| `clearListSelection` | 1 | none | Return safe default (null/false/0/empty) |
| `convertSelectionToString` | 1 | none | Store callback, never fire |
| `dismissDropDown` | 1 | none | Return safe default (null/false/0/empty) |
| `enoughToFilter` | 1 | none | throw UnsupportedOperationException |
| `getAdapter` | 1 | none | Return safe default (null/false/0/empty) |
| `getCompletionHint` | 1 | none | Return safe default (null/false/0/empty) |
| `getDropDownAnchor` | 1 | none | Return safe default (null/false/0/empty) |
| `getDropDownBackground` | 1 | none | Return safe default (null/false/0/empty) |
| `getDropDownHeight` | 1 | none | Return safe default (null/false/0/empty) |
| `getDropDownHorizontalOffset` | 1 | none | Return safe default (null/false/0/empty) |
| `getDropDownVerticalOffset` | 1 | none | Return safe default (null/false/0/empty) |
| `getDropDownWidth` | 1 | none | Return safe default (null/false/0/empty) |
| `getFilter` | 1 | none | Return safe default (null/false/0/empty) |
| `getInputMethodMode` | 1 | none | Return safe default (null/false/0/empty) |
| `getListSelection` | 1 | none | Return safe default (null/false/0/empty) |
| `getOnItemClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `getOnItemSelectedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `getThreshold` | 1 | none | Return safe default (null/false/0/empty) |
| `getValidator` | 1 | none | Return safe default (null/false/0/empty) |
| `isPerformingCompletion` | 1 | none | Return safe default (null/false/0/empty) |
| `isPopupShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `onFilterComplete` | 1 | none | Store callback, never fire |
| `performCompletion` | 1 | none | Store callback, never fire |
| `performFiltering` | 1 | none | throw UnsupportedOperationException |
| `performValidation` | 1 | none | Store callback, never fire |
| `refreshAutoCompleteResults` | 1 | none | throw UnsupportedOperationException |
| `replaceText` | 1 | none | throw UnsupportedOperationException |
| `setAdapter` | 1 | none | Log warning + no-op |
| `setCompletionHint` | 1 | none | Log warning + no-op |
| `setDropDownAnchor` | 1 | none | Log warning + no-op |
| `setDropDownBackgroundDrawable` | 1 | none | Log warning + no-op |
| `setDropDownBackgroundResource` | 1 | none | Log warning + no-op |
| `setDropDownHeight` | 1 | none | Log warning + no-op |
| `setDropDownHorizontalOffset` | 1 | none | Log warning + no-op |
| `setDropDownVerticalOffset` | 1 | none | Log warning + no-op |
| `setDropDownWidth` | 1 | none | Log warning + no-op |
| `setInputMethodMode` | 1 | none | Log warning + no-op |
| `setListSelection` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnDismissListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnItemClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnItemSelectedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setText` | 1 | none | Log warning + no-op |
| `setThreshold` | 1 | none | Log warning + no-op |
| `setValidator` | 1 | none | Log warning + no-op |
| `showDropDown` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.AutoCompleteTextView`:

- `android.widget.EditText` (already shimmed)
- `android.widget.TextView` (already shimmed)

## Quality Gates

Before marking `android.widget.AutoCompleteTextView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 50 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
