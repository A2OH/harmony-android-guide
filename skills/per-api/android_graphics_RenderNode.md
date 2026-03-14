# SKILL: android.graphics.RenderNode

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.RenderNode`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.RenderNode` |
| **Package** | `android.graphics` |
| **Total Methods** | 63 |
| **Avg Score** | 1.4 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 16 (25%) |
| **No Mapping** | 47 (74%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 63 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getMatrix` | 4 | composite | Return safe default (null/false/0/empty) |
| `getTop` | 3 | composite | Return safe default (null/false/0/empty) |
| `getScaleY` | 3 | composite | Return safe default (null/false/0/empty) |
| `getLeft` | 3 | composite | Return safe default (null/false/0/empty) |
| `getScaleX` | 2 | composite | Return safe default (null/false/0/empty) |
| `getAlpha` | 2 | composite | Return safe default (null/false/0/empty) |
| `getRight` | 2 | composite | Return safe default (null/false/0/empty) |
| `getWidth` | 2 | composite | Return safe default (null/false/0/empty) |
| `setAlpha` | 2 | composite | Log warning + no-op |
| `getUseCompositingLayer` | 2 | composite | Return safe default (null/false/0/empty) |
| `getUniqueId` | 2 | composite | Return safe default (null/false/0/empty) |
| `getTranslationX` | 2 | composite | Return safe default (null/false/0/empty) |
| `getTranslationY` | 2 | composite | Return safe default (null/false/0/empty) |
| `getTranslationZ` | 2 | composite | Return safe default (null/false/0/empty) |
| `getHeight` | 2 | composite | Return safe default (null/false/0/empty) |
| `setCameraDistance` | 2 | composite | Return safe default (null/false/0/empty) |
| `RenderNode` | 1 | none | throw UnsupportedOperationException |
| `computeApproximateMemoryUsage` | 1 | none | Log warning + no-op |
| `discardDisplayList` | 1 | none | Return safe default (null/false/0/empty) |
| `endRecording` | 1 | none | throw UnsupportedOperationException |
| `getBottom` | 1 | none | Return safe default (null/false/0/empty) |
| `getClipToBounds` | 1 | none | Return safe default (null/false/0/empty) |
| `getClipToOutline` | 1 | none | Return safe default (null/false/0/empty) |
| `getElevation` | 1 | none | Return safe default (null/false/0/empty) |
| `getInverseMatrix` | 1 | none | Return safe default (null/false/0/empty) |
| `getPivotX` | 1 | none | Return safe default (null/false/0/empty) |
| `getPivotY` | 1 | none | Return safe default (null/false/0/empty) |
| `getRotationX` | 1 | none | Return safe default (null/false/0/empty) |
| `getRotationY` | 1 | none | Return safe default (null/false/0/empty) |
| `getRotationZ` | 1 | none | Return safe default (null/false/0/empty) |
| `hasDisplayList` | 1 | none | Return safe default (null/false/0/empty) |
| `hasIdentityMatrix` | 1 | none | Return safe default (null/false/0/empty) |
| `hasOverlappingRendering` | 1 | none | Return safe default (null/false/0/empty) |
| `hasShadow` | 1 | none | Return safe default (null/false/0/empty) |
| `isForceDarkAllowed` | 1 | none | Return safe default (null/false/0/empty) |
| `isPivotExplicitlySet` | 1 | none | Return safe default (null/false/0/empty) |
| `offsetLeftAndRight` | 1 | none | Log warning + no-op |
| `offsetTopAndBottom` | 1 | none | Log warning + no-op |
| `resetPivot` | 1 | none | Log warning + no-op |
| `setAmbientShadowColor` | 1 | none | Log warning + no-op |
| `setClipRect` | 1 | none | Log warning + no-op |
| `setClipToBounds` | 1 | none | Log warning + no-op |
| `setClipToOutline` | 1 | none | Log warning + no-op |
| `setElevation` | 1 | none | Log warning + no-op |
| `setForceDarkAllowed` | 1 | none | Log warning + no-op |
| `setHasOverlappingRendering` | 1 | none | Return safe default (null/false/0/empty) |
| `setOutline` | 1 | none | Log warning + no-op |
| `setPivotX` | 1 | none | Log warning + no-op |
| `setPivotY` | 1 | none | Log warning + no-op |
| `setPosition` | 1 | none | Log warning + no-op |
| `setPosition` | 1 | none | Log warning + no-op |
| `setProjectBackwards` | 1 | none | Log warning + no-op |
| `setProjectionReceiver` | 1 | none | Log warning + no-op |
| `setRotationX` | 1 | none | Log warning + no-op |
| `setRotationY` | 1 | none | Log warning + no-op |
| `setRotationZ` | 1 | none | Log warning + no-op |
| `setScaleX` | 1 | none | Log warning + no-op |
| `setScaleY` | 1 | none | Log warning + no-op |
| `setSpotShadowColor` | 1 | none | Log warning + no-op |
| `setTranslationX` | 1 | none | Log warning + no-op |
| `setTranslationY` | 1 | none | Log warning + no-op |
| `setTranslationZ` | 1 | none | Log warning + no-op |
| `setUseCompositingLayer` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.RenderNode`:


## Quality Gates

Before marking `android.graphics.RenderNode` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 63 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
