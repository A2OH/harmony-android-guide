# SKILL: android.widget.Spinner

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.Spinner`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.Spinner` |
| **Package** | `android.widget` |
| **Total Methods** | 23 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (4%) |
| **No Mapping** | 22 (95%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 16 |
| **Has Async Gap** | 23 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 23 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getGravity` | 3 | composite | Return safe default (null/false/0/empty) |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `Spinner` | 1 | none | throw UnsupportedOperationException |
| `getDropDownHorizontalOffset` | 1 | none | Return safe default (null/false/0/empty) |
| `getDropDownVerticalOffset` | 1 | none | Return safe default (null/false/0/empty) |
| `getDropDownWidth` | 1 | none | Return safe default (null/false/0/empty) |
| `getPopupBackground` | 1 | none | Return safe default (null/false/0/empty) |
| `getPopupContext` | 1 | none | Return safe default (null/false/0/empty) |
| `getPrompt` | 1 | none | Return safe default (null/false/0/empty) |
| `onClick` | 1 | none | Store callback, never fire |
| `setDropDownHorizontalOffset` | 1 | none | Log warning + no-op |
| `setDropDownVerticalOffset` | 1 | none | Log warning + no-op |
| `setDropDownWidth` | 1 | none | Log warning + no-op |
| `setGravity` | 1 | none | Log warning + no-op |
| `setPopupBackgroundDrawable` | 1 | none | Log warning + no-op |
| `setPopupBackgroundResource` | 1 | none | Log warning + no-op |
| `setPrompt` | 1 | none | Log warning + no-op |
| `setPromptId` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.Spinner`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.Spinner` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
