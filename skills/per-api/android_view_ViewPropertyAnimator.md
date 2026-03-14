# SKILL: android.view.ViewPropertyAnimator

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ViewPropertyAnimator`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ViewPropertyAnimator` |
| **Package** | `android.view` |
| **Total Methods** | 37 |
| **Avg Score** | 1.5 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (27%) |
| **No Mapping** | 27 (72%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 37 |
| **Has Async Gap** | 37 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 37 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `alpha` | 3 | composite | throw UnsupportedOperationException |
| `cancel` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDuration` | 3 | composite | Return safe default (null/false/0/empty) |
| `rotation` | 3 | composite | Store callback, never fire |
| `start` | 3 | composite | Return dummy instance / no-op |
| `x` | 3 | composite | throw UnsupportedOperationException |
| `y` | 3 | composite | throw UnsupportedOperationException |
| `z` | 3 | composite | throw UnsupportedOperationException |
| `withEndAction` | 3 | composite | Store callback, never fire |
| `withStartAction` | 2 | composite | Return dummy instance / no-op |
| `alphaBy` | 1 | none | throw UnsupportedOperationException |
| `getInterpolator` | 1 | none | Return safe default (null/false/0/empty) |
| `getStartDelay` | 1 | none | Return dummy instance / no-op |
| `rotationBy` | 1 | none | Store callback, never fire |
| `rotationX` | 1 | none | Store callback, never fire |
| `rotationXBy` | 1 | none | Store callback, never fire |
| `rotationY` | 1 | none | Store callback, never fire |
| `rotationYBy` | 1 | none | Store callback, never fire |
| `scaleX` | 1 | none | throw UnsupportedOperationException |
| `scaleXBy` | 1 | none | throw UnsupportedOperationException |
| `scaleY` | 1 | none | throw UnsupportedOperationException |
| `scaleYBy` | 1 | none | throw UnsupportedOperationException |
| `setDuration` | 1 | none | Log warning + no-op |
| `setInterpolator` | 1 | none | Log warning + no-op |
| `setListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setStartDelay` | 1 | none | Return dummy instance / no-op |
| `setUpdateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `translationX` | 1 | none | Store callback, never fire |
| `translationXBy` | 1 | none | Store callback, never fire |
| `translationY` | 1 | none | Store callback, never fire |
| `translationYBy` | 1 | none | Store callback, never fire |
| `translationZ` | 1 | none | Store callback, never fire |
| `translationZBy` | 1 | none | Store callback, never fire |
| `withLayer` | 1 | none | throw UnsupportedOperationException |
| `xBy` | 1 | none | throw UnsupportedOperationException |
| `yBy` | 1 | none | throw UnsupportedOperationException |
| `zBy` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ViewPropertyAnimator`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.ViewPropertyAnimator` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 37 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
