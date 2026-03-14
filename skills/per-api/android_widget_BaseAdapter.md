# SKILL: android.widget.BaseAdapter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.BaseAdapter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.BaseAdapter` |
| **Package** | `android.widget` |
| **Total Methods** | 13 |
| **Avg Score** | 2.2 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 1 (7%) |
| **Partial/Composite** | 5 (38%) |
| **No Mapping** | 7 (53%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 12 |
| **Has Async Gap** | 12 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `notifyDataSetChanged` | `void notifyDataSetChanged()` | 7 | near | impossible | `onDataReloaded` | `@internal/component/ets/lazy_for_each.DataChangeListener` |

## Gap Descriptions (per method)

- **`notifyDataSetChanged`**: Via DataChangeListener

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDropDownView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getItemViewType` | 3 | composite | Return safe default (null/false/0/empty) |
| `isEnabled` | 3 | composite | Return safe default (null/false/0/empty) |
| `registerDataSetObserver` | 3 | composite | Return safe default (null/false/0/empty) |
| `unregisterDataSetObserver` | 3 | composite | Return safe default (null/false/0/empty) |
| `BaseAdapter` | 1 | none | throw UnsupportedOperationException |
| `areAllItemsEnabled` | 1 | none | throw UnsupportedOperationException |
| `getViewTypeCount` | 1 | none | Return safe default (null/false/0/empty) |
| `hasStableIds` | 1 | none | Return safe default (null/false/0/empty) |
| `isEmpty` | 1 | none | Return safe default (null/false/0/empty) |
| `notifyDataSetInvalidated` | 1 | none | Log warning + no-op |
| `setAutofillOptions` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.BaseAdapter`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.BaseAdapter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 13 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
