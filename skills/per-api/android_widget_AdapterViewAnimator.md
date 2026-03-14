# SKILL: android.widget.AdapterViewAnimator

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.AdapterViewAnimator`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.AdapterViewAnimator` |
| **Package** | `android.widget` |
| **Total Methods** | 28 |
| **Avg Score** | 1.3 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (14%) |
| **No Mapping** | 24 (85%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 24 |
| **Has Async Gap** | 24 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 28 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getCurrentView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSelectedView` | 3 | composite | Return safe default (null/false/0/empty) |
| `onSaveInstanceState` | 3 | composite | Store callback, never fire |
| `onRestoreInstanceState` | 3 | composite | Store callback, never fire |
| `AdapterViewAnimator` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewAnimator` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewAnimator` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewAnimator` | 1 | none | throw UnsupportedOperationException |
| `advance` | 1 | none | throw UnsupportedOperationException |
| `deferNotifyDataSetChanged` | 1 | none | Log warning + no-op |
| `fyiWillBeAdvancedByHostKThx` | 1 | none | throw UnsupportedOperationException |
| `getAdapter` | 1 | none | Return safe default (null/false/0/empty) |
| `getDisplayedChild` | 1 | none | Return safe default (null/false/0/empty) |
| `getInAnimation` | 1 | none | Return safe default (null/false/0/empty) |
| `getOutAnimation` | 1 | none | Return safe default (null/false/0/empty) |
| `onRemoteAdapterConnected` | 1 | none | Return dummy instance / no-op |
| `onRemoteAdapterDisconnected` | 1 | none | Return dummy instance / no-op |
| `setAdapter` | 1 | none | Log warning + no-op |
| `setAnimateFirstView` | 1 | none | Log warning + no-op |
| `setDisplayedChild` | 1 | none | Return safe default (null/false/0/empty) |
| `setInAnimation` | 1 | none | Log warning + no-op |
| `setInAnimation` | 1 | none | Log warning + no-op |
| `setOutAnimation` | 1 | none | Log warning + no-op |
| `setOutAnimation` | 1 | none | Log warning + no-op |
| `setRemoteViewsAdapter` | 1 | none | Log warning + no-op |
| `setSelection` | 1 | none | Log warning + no-op |
| `showNext` | 1 | none | throw UnsupportedOperationException |
| `showPrevious` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.AdapterViewAnimator`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.AdapterViewAnimator` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 28 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
