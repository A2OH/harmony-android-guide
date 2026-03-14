# SKILL: android.widget.OverScroller

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.OverScroller`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.OverScroller` |
| **Package** | `android.widget` |
| **Total Methods** | 22 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (9%) |
| **No Mapping** | 20 (90%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 20 |
| **Has Async Gap** | 20 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getCurrX` | 2 | composite | Return safe default (null/false/0/empty) |
| `getCurrY` | 2 | composite | Return safe default (null/false/0/empty) |
| `OverScroller` | 1 | none | throw UnsupportedOperationException |
| `OverScroller` | 1 | none | throw UnsupportedOperationException |
| `abortAnimation` | 1 | none | Store callback, never fire |
| `computeScrollOffset` | 1 | none | Log warning + no-op |
| `fling` | 1 | none | throw UnsupportedOperationException |
| `fling` | 1 | none | throw UnsupportedOperationException |
| `forceFinished` | 1 | none | Return safe default (null/false/0/empty) |
| `getCurrVelocity` | 1 | none | Return safe default (null/false/0/empty) |
| `getFinalX` | 1 | none | Return safe default (null/false/0/empty) |
| `getFinalY` | 1 | none | Return safe default (null/false/0/empty) |
| `getStartX` | 1 | none | Return dummy instance / no-op |
| `getStartY` | 1 | none | Return dummy instance / no-op |
| `isFinished` | 1 | none | Return safe default (null/false/0/empty) |
| `isOverScrolled` | 1 | none | Return safe default (null/false/0/empty) |
| `notifyHorizontalEdgeReached` | 1 | none | Store callback, never fire |
| `notifyVerticalEdgeReached` | 1 | none | throw UnsupportedOperationException |
| `setFriction` | 1 | none | Log warning + no-op |
| `springBack` | 1 | none | throw UnsupportedOperationException |
| `startScroll` | 1 | none | Return dummy instance / no-op |
| `startScroll` | 1 | none | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.OverScroller`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.OverScroller` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 22 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
