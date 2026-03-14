# SKILL: android.widget.Magnifier

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.Magnifier`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.Magnifier` |
| **Package** | `android.widget` |
| **Total Methods** | 7 |
| **Avg Score** | 2.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (57%) |
| **No Mapping** | 3 (42%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 7 |
| **Has Async Gap** | 7 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `show` | 3 | composite | throw UnsupportedOperationException |
| `show` | 3 | composite | throw UnsupportedOperationException |
| `update` | 3 | composite | Log warning + no-op |
| `getZoom` | 3 | composite | Return safe default (null/false/0/empty) |
| `dismiss` | 1 | none | Return safe default (null/false/0/empty) |
| `isClippingEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `setZoom` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.Magnifier`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.Magnifier` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
