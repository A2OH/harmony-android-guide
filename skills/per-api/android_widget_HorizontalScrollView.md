# SKILL: android.widget.HorizontalScrollView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.HorizontalScrollView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.HorizontalScrollView` |
| **Package** | `android.widget` |
| **Total Methods** | 20 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (5%) |
| **No Mapping** | 19 (95%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 16 |
| **Has Async Gap** | 20 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 20 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `executeKeyEvent` | 2 | composite | throw UnsupportedOperationException |
| `HorizontalScrollView` | 1 | none | Store callback, never fire |
| `HorizontalScrollView` | 1 | none | Store callback, never fire |
| `HorizontalScrollView` | 1 | none | Store callback, never fire |
| `HorizontalScrollView` | 1 | none | Store callback, never fire |
| `arrowScroll` | 1 | none | throw UnsupportedOperationException |
| `computeScrollDeltaToGetChildRectOnScreen` | 1 | none | Return safe default (null/false/0/empty) |
| `fling` | 1 | none | throw UnsupportedOperationException |
| `fullScroll` | 1 | none | throw UnsupportedOperationException |
| `getMaxScrollAmount` | 1 | none | Return safe default (null/false/0/empty) |
| `isFillViewport` | 1 | none | Return safe default (null/false/0/empty) |
| `isSmoothScrollingEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `pageScroll` | 1 | none | throw UnsupportedOperationException |
| `setEdgeEffectColor` | 1 | none | Log warning + no-op |
| `setFillViewport` | 1 | none | Log warning + no-op |
| `setLeftEdgeEffectColor` | 1 | none | Log warning + no-op |
| `setRightEdgeEffectColor` | 1 | none | Log warning + no-op |
| `setSmoothScrollingEnabled` | 1 | none | Log warning + no-op |
| `smoothScrollBy` | 1 | none | throw UnsupportedOperationException |
| `smoothScrollTo` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.HorizontalScrollView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.HorizontalScrollView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 20 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
