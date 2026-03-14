# SKILL: android.widget.RelativeLayout.LayoutParams

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.RelativeLayout.LayoutParams`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.RelativeLayout.LayoutParams` |
| **Package** | `android.widget.RelativeLayout` |
| **Total Methods** | 11 |
| **Avg Score** | 2.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (54%) |
| **No Mapping** | 5 (45%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 6 |
| **Has Async Gap** | 6 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addRule` | 3 | composite | Log warning + no-op |
| `addRule` | 3 | composite | Log warning + no-op |
| `getRule` | 3 | composite | Return safe default (null/false/0/empty) |
| `removeRule` | 3 | composite | Log warning + no-op |
| `debug` | 3 | composite | throw UnsupportedOperationException |
| `getRules` | 2 | composite | Return safe default (null/false/0/empty) |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |
| `LayoutParams` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.RelativeLayout.LayoutParams`:


## Quality Gates

Before marking `android.widget.RelativeLayout.LayoutParams` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
