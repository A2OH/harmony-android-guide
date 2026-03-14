# SKILL: android.widget.SimpleAdapter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.SimpleAdapter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.SimpleAdapter` |
| **Package** | `android.widget` |
| **Total Methods** | 14 |
| **Avg Score** | 1.8 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (42%) |
| **No Mapping** | 8 (57%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 13 |
| **Has Async Gap** | 13 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDropDownViewTheme` | 3 | composite | Return safe default (null/false/0/empty) |
| `getView` | 3 | composite | Return safe default (null/false/0/empty) |
| `getViewBinder` | 3 | composite | Return safe default (null/false/0/empty) |
| `getItem` | 3 | composite | Return safe default (null/false/0/empty) |
| `getItemId` | 2 | composite | Return safe default (null/false/0/empty) |
| `SimpleAdapter` | 1 | none | throw UnsupportedOperationException |
| `getFilter` | 1 | none | Return safe default (null/false/0/empty) |
| `setDropDownViewResource` | 1 | none | Log warning + no-op |
| `setDropDownViewTheme` | 1 | none | Log warning + no-op |
| `setViewBinder` | 1 | none | Log warning + no-op |
| `setViewImage` | 1 | none | Log warning + no-op |
| `setViewImage` | 1 | none | Log warning + no-op |
| `setViewText` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.SimpleAdapter`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.SimpleAdapter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
