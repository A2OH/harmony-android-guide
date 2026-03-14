# SKILL: android.view.SurfaceView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.SurfaceView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.SurfaceView` |
| **Package** | `android.view` |
| **Total Methods** | 11 |
| **Avg Score** | 1.4 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (9%) |
| **No Mapping** | 10 (90%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 7 |
| **Has Async Gap** | 7 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getHolder` | `android.view.SurfaceHolder getHolder()` | 5 | partial | impossible | `getXComponentSurfaceId` | `@internal/component/ets/xcomponent.XComponentController` |

## Gap Descriptions (per method)

- **`getHolder`**: XComponent surface ID

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `SurfaceView` | 1 | none | throw UnsupportedOperationException |
| `SurfaceView` | 1 | none | throw UnsupportedOperationException |
| `SurfaceView` | 1 | none | throw UnsupportedOperationException |
| `SurfaceView` | 1 | none | throw UnsupportedOperationException |
| `gatherTransparentRegion` | 1 | none | Store callback, never fire |
| `getSurfaceControl` | 1 | none | Return safe default (null/false/0/empty) |
| `setChildSurfacePackage` | 1 | none | Log warning + no-op |
| `setSecure` | 1 | none | Log warning + no-op |
| `setZOrderMediaOverlay` | 1 | none | Log warning + no-op |
| `setZOrderOnTop` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.SurfaceView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.SurfaceView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
