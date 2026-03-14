# SKILL: android.widget.SearchView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.SearchView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.SearchView` |
| **Package** | `android.widget` |
| **Total Methods** | 31 |
| **Avg Score** | 1.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (3%) |
| **No Mapping** | 30 (96%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 27 |
| **Has Async Gap** | 31 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 31 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getQuery` | 2 | composite | Return safe default (null/false/0/empty) |
| `SearchView` | 1 | none | throw UnsupportedOperationException |
| `SearchView` | 1 | none | throw UnsupportedOperationException |
| `SearchView` | 1 | none | throw UnsupportedOperationException |
| `SearchView` | 1 | none | throw UnsupportedOperationException |
| `getImeOptions` | 1 | none | Return safe default (null/false/0/empty) |
| `getInputType` | 1 | none | Return safe default (null/false/0/empty) |
| `getMaxWidth` | 1 | none | Return safe default (null/false/0/empty) |
| `getSuggestionsAdapter` | 1 | none | Return safe default (null/false/0/empty) |
| `isIconified` | 1 | none | Return safe default (null/false/0/empty) |
| `isIconifiedByDefault` | 1 | none | Return safe default (null/false/0/empty) |
| `isQueryRefinementEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `isSubmitButtonEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `onActionViewCollapsed` | 1 | none | Store callback, never fire |
| `onActionViewExpanded` | 1 | none | Store callback, never fire |
| `setIconified` | 1 | none | Log warning + no-op |
| `setIconifiedByDefault` | 1 | none | Log warning + no-op |
| `setImeOptions` | 1 | none | Log warning + no-op |
| `setInputType` | 1 | none | Log warning + no-op |
| `setMaxWidth` | 1 | none | Log warning + no-op |
| `setOnCloseListener` | 1 | none | No-op |
| `setOnQueryTextFocusChangeListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnQueryTextListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnSearchClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnSuggestionListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `setQueryHint` | 1 | none | Return safe default (null/false/0/empty) |
| `setQueryRefinementEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `setSearchableInfo` | 1 | none | Log warning + no-op |
| `setSubmitButtonEnabled` | 1 | none | Log warning + no-op |
| `setSuggestionsAdapter` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.SearchView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.SearchView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 31 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
