# SKILL: android.widget.RatingBar

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.RatingBar`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.RatingBar` |
| **Package** | `android.widget` |
| **Total Methods** | 14 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (7%) |
| **No Mapping** | 13 (92%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 10 |
| **Has Async Gap** | 10 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getRating` | 2 | composite | Return safe default (null/false/0/empty) |
| `RatingBar` | 1 | none | throw UnsupportedOperationException |
| `RatingBar` | 1 | none | throw UnsupportedOperationException |
| `RatingBar` | 1 | none | throw UnsupportedOperationException |
| `RatingBar` | 1 | none | throw UnsupportedOperationException |
| `getNumStars` | 1 | none | Return safe default (null/false/0/empty) |
| `getOnRatingBarChangeListener` | 1 | none | Return safe default (null/false/0/empty) |
| `getStepSize` | 1 | none | Return safe default (null/false/0/empty) |
| `isIndicator` | 1 | none | Return safe default (null/false/0/empty) |
| `setIsIndicator` | 1 | none | Return safe default (null/false/0/empty) |
| `setNumStars` | 1 | none | Log warning + no-op |
| `setOnRatingBarChangeListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setRating` | 1 | none | Log warning + no-op |
| `setStepSize` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.RatingBar`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.RatingBar` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
