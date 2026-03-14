# SKILL: android.widget.AdapterViewFlipper

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.AdapterViewFlipper`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.AdapterViewFlipper` |
| **Package** | `android.widget` |
| **Total Methods** | 11 |
| **Avg Score** | 1.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 11 (100%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 7 |
| **Has Async Gap** | 7 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `AdapterViewFlipper` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewFlipper` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewFlipper` | 1 | none | throw UnsupportedOperationException |
| `AdapterViewFlipper` | 1 | none | throw UnsupportedOperationException |
| `getFlipInterval` | 1 | none | Return safe default (null/false/0/empty) |
| `isAutoStart` | 1 | none | Return dummy instance / no-op |
| `isFlipping` | 1 | none | Return safe default (null/false/0/empty) |
| `setAutoStart` | 1 | none | Return dummy instance / no-op |
| `setFlipInterval` | 1 | none | Log warning + no-op |
| `startFlipping` | 1 | none | Return dummy instance / no-op |
| `stopFlipping` | 1 | none | No-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.AdapterViewFlipper`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.AdapterViewFlipper` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
