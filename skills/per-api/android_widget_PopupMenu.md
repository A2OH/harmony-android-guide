# SKILL: android.widget.PopupMenu

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.PopupMenu`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.PopupMenu` |
| **Package** | `android.widget` |
| **Total Methods** | 14 |
| **Avg Score** | 1.5 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (28%) |
| **No Mapping** | 10 (71%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 11 |
| **Has Async Gap** | 11 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDragToOpenListener` | 3 | composite | Return dummy instance / no-op |
| `getGravity` | 3 | composite | Return safe default (null/false/0/empty) |
| `show` | 3 | composite | throw UnsupportedOperationException |
| `getMenu` | 3 | composite | Return safe default (null/false/0/empty) |
| `PopupMenu` | 1 | none | throw UnsupportedOperationException |
| `PopupMenu` | 1 | none | throw UnsupportedOperationException |
| `PopupMenu` | 1 | none | throw UnsupportedOperationException |
| `dismiss` | 1 | none | Return safe default (null/false/0/empty) |
| `getMenuInflater` | 1 | none | Return safe default (null/false/0/empty) |
| `inflate` | 1 | none | throw UnsupportedOperationException |
| `setForceShowIcon` | 1 | none | Log warning + no-op |
| `setGravity` | 1 | none | Log warning + no-op |
| `setOnDismissListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnMenuItemClickListener` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.PopupMenu`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.PopupMenu` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
