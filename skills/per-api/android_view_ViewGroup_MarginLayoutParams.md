# SKILL: android.view.ViewGroup.MarginLayoutParams

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ViewGroup.MarginLayoutParams`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ViewGroup.MarginLayoutParams` |
| **Package** | `android.view.ViewGroup` |
| **Total Methods** | 12 |
| **Avg Score** | 1.2 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (8%) |
| **No Mapping** | 11 (91%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 8 |
| **Has Async Gap** | 8 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getMarginEnd` | 3 | composite | Return safe default (null/false/0/empty) |
| `MarginLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `MarginLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `MarginLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `MarginLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `getLayoutDirection` | 1 | none | Return safe default (null/false/0/empty) |
| `getMarginStart` | 1 | none | Return dummy instance / no-op |
| `isMarginRelative` | 1 | none | Return safe default (null/false/0/empty) |
| `setLayoutDirection` | 1 | none | Log warning + no-op |
| `setMarginEnd` | 1 | none | Log warning + no-op |
| `setMarginStart` | 1 | none | Return dummy instance / no-op |
| `setMargins` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ViewGroup.MarginLayoutParams`:


## Quality Gates

Before marking `android.view.ViewGroup.MarginLayoutParams` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
