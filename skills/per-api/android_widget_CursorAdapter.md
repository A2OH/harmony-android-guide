# SKILL: android.widget.CursorAdapter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.CursorAdapter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.CursorAdapter` |
| **Package** | `android.widget` |
| **Total Methods** | 19 |
| **Avg Score** | 1.5 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 5 (26%) |
| **No Mapping** | 14 (73%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 17 |
| **Has Async Gap** | 17 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 19 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownViewTheme` | 3 | composite | Return safe default (null/false/0/empty) |
| `getView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getItem` | 3 | composite | Return safe default (null/false/0/empty) |
| `getItemId` | 2 | composite | Return safe default (null/false/0/empty) |
| `CursorAdapter` | 1 | none | throw UnsupportedOperationException |
| `CursorAdapter` | 1 | none | throw UnsupportedOperationException |
| `bindView` | 1 | none | throw UnsupportedOperationException |
| `changeCursor` | 1 | none | throw UnsupportedOperationException |
| `convertToString` | 1 | none | Store callback, never fire |
| `getCursor` | 1 | none | Return safe default (null/false/0/empty) |
| `getFilter` | 1 | none | Return safe default (null/false/0/empty) |
| `getFilterQueryProvider` | 1 | none | Return safe default (null/false/0/empty) |
| `newDropDownView` | 1 | none | throw UnsupportedOperationException |
| `newView` | 1 | none | throw UnsupportedOperationException |
| `onContentChanged` | 1 | none | Store callback, never fire |
| `setDropDownViewTheme` | 1 | none | Log warning + no-op |
| `setFilterQueryProvider` | 1 | none | Return safe default (null/false/0/empty) |
| `swapCursor` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.CursorAdapter`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.CursorAdapter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 19 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
