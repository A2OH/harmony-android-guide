# SKILL: android.view.ActionProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ActionProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ActionProvider` |
| **Package** | `android.view` |
| **Total Methods** | 9 |
| **Avg Score** | 1.4 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (22%) |
| **No Mapping** | 7 (77%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 8 |
| **Has Async Gap** | 8 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isVisible` | 3 | composite | Return safe default (null/false/0/empty) |
| `onCreateActionView` | 3 | composite | Return dummy instance / no-op |
| `ActionProvider` | 1 | none | Store callback, never fire |
| `hasSubMenu` | 1 | none | Return safe default (null/false/0/empty) |
| `onPerformDefaultAction` | 1 | none | Store callback, never fire |
| `onPrepareSubMenu` | 1 | none | Store callback, never fire |
| `overridesItemVisibility` | 1 | none | Return safe default (null/false/0/empty) |
| `refreshVisibility` | 1 | none | Return safe default (null/false/0/empty) |
| `setVisibilityListener` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ActionProvider`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.ActionProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
