# SKILL: android.graphics.Color

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Color`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Color` |
| **Package** | `android.graphics` |
| **Total Methods** | 21 |
| **Avg Score** | 3.1 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 6 (28%) |
| **Partial/Composite** | 5 (23%) |
| **No Mapping** | 10 (47%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `blue` | `float blue()` | 6 | near | moderate | `blueX` | `@ohos.graphics.colorSpaceManager.ColorSpacePrimaries` |
| `blue` | `static float blue(@ColorLong long)` | 6 | near | moderate | `blueX` | `@ohos.graphics.colorSpaceManager.ColorSpacePrimaries` |
| `green` | `float green()` | 6 | near | moderate | `greenX` | `@ohos.graphics.colorSpaceManager.ColorSpacePrimaries` |
| `green` | `static float green(@ColorLong long)` | 6 | near | moderate | `greenX` | `@ohos.graphics.colorSpaceManager.ColorSpacePrimaries` |
| `red` | `float red()` | 6 | near | moderate | `redX` | `@ohos.graphics.colorSpaceManager.ColorSpacePrimaries` |
| `red` | `static float red(@ColorLong long)` | 6 | near | moderate | `redX` | `@ohos.graphics.colorSpaceManager.ColorSpacePrimaries` |
| `alpha` | `float alpha()` | 6 | partial | moderate | `alpha` | `alpha: number` |
| `alpha` | `static float alpha(@ColorLong long)` | 6 | partial | moderate | `alpha` | `alpha: number` |

## Gap Descriptions (per method)

- **`blue`**: Auto-promoted: near score=6.0625
- **`blue`**: Auto-promoted: near score=6.0625
- **`green`**: Auto-promoted: near score=6.0625
- **`green`**: Auto-promoted: near score=6.0625
- **`red`**: Auto-promoted: near score=6.0625
- **`red`**: Auto-promoted: near score=6.0625

## Stub APIs (score < 5): 13 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getComponent` | 3 | composite | Return safe default (null/false/0/empty) |
| `isInColorSpace` | 2 | composite | Return safe default (null/false/0/empty) |
| `getModel` | 2 | composite | Return safe default (null/false/0/empty) |
| `Color` | 1 | none | throw UnsupportedOperationException |
| `RGBToHSV` | 1 | none | throw UnsupportedOperationException |
| `colorToHSV` | 1 | none | throw UnsupportedOperationException |
| `isSrgb` | 1 | none | Return safe default (null/false/0/empty) |
| `isSrgb` | 1 | none | Return safe default (null/false/0/empty) |
| `isWideGamut` | 1 | none | Return safe default (null/false/0/empty) |
| `isWideGamut` | 1 | none | Return safe default (null/false/0/empty) |
| `luminance` | 1 | none | throw UnsupportedOperationException |
| `luminance` | 1 | none | throw UnsupportedOperationException |
| `luminance` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Color`:


## Quality Gates

Before marking `android.graphics.Color` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
