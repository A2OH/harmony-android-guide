# SKILL: android.widget.ExpandableListAdapter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.ExpandableListAdapter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.ExpandableListAdapter` |
| **Package** | `android.widget` |
| **Total Methods** | 18 |
| **Avg Score** | 1.6 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (38%) |
| **No Mapping** | 11 (61%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 18 |
| **Has Async Gap** | 18 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 18 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getChildView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getGroupView` | 3 | composite | Return safe default (null/false/0/empty) |
| `registerDataSetObserver` | 3 | composite | Return safe default (null/false/0/empty) |
| `unregisterDataSetObserver` | 3 | composite | Return safe default (null/false/0/empty) |
| `getChild` | 2 | composite | Return safe default (null/false/0/empty) |
| `getGroup` | 2 | composite | Return safe default (null/false/0/empty) |
| `getChildId` | 2 | composite | Return safe default (null/false/0/empty) |
| `areAllItemsEnabled` | 1 | none | throw UnsupportedOperationException |
| `getChildrenCount` | 1 | none | Return safe default (null/false/0/empty) |
| `getCombinedChildId` | 1 | none | Return safe default (null/false/0/empty) |
| `getCombinedGroupId` | 1 | none | Return safe default (null/false/0/empty) |
| `getGroupCount` | 1 | none | Return safe default (null/false/0/empty) |
| `getGroupId` | 1 | none | Return safe default (null/false/0/empty) |
| `hasStableIds` | 1 | none | Return safe default (null/false/0/empty) |
| `isChildSelectable` | 1 | none | Return safe default (null/false/0/empty) |
| `isEmpty` | 1 | none | Return safe default (null/false/0/empty) |
| `onGroupCollapsed` | 1 | none | Store callback, never fire |
| `onGroupExpanded` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.ExpandableListAdapter`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.ExpandableListAdapter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
