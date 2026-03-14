# SKILL: android.widget.AdapterView<T

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.AdapterView<T`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.AdapterView<T` |
| **Package** | `android.widget` |
| **Total Methods** | 21 |
| **Avg Score** | 1.4 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (19%) |
| **No Mapping** | 17 (80%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 17 |
| **Has Async Gap** | 17 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 21 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getEmptyView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPositionForView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSelectedView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSelectedItem` | 2 | composite | Return safe default (null/false/0/empty) |
| `AdapterView` | 1 | none | throw UnsupportedOperationException |
| `AdapterView` | 1 | none | throw UnsupportedOperationException |
| `AdapterView` | 1 | none | throw UnsupportedOperationException |
| `AdapterView` | 1 | none | throw UnsupportedOperationException |
| `getAdapter` | 1 | none | Return safe default (null/false/0/empty) |
| `getFirstVisiblePosition` | 1 | none | Return safe default (null/false/0/empty) |
| `getItemAtPosition` | 1 | none | Return safe default (null/false/0/empty) |
| `getItemIdAtPosition` | 1 | none | Return safe default (null/false/0/empty) |
| `getLastVisiblePosition` | 1 | none | Return safe default (null/false/0/empty) |
| `getOnItemLongClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `performItemClick` | 1 | none | throw UnsupportedOperationException |
| `setAdapter` | 1 | none | Log warning + no-op |
| `setEmptyView` | 1 | none | Log warning + no-op |
| `setOnItemClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnItemLongClickListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnItemSelectedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setSelection` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.AdapterView<T`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.AdapterView<T` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
