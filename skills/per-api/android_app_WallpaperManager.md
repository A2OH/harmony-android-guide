# SKILL: android.app.WallpaperManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.WallpaperManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.WallpaperManager` |
| **Package** | `android.app` |
| **Total Methods** | 23 |
| **Avg Score** | 1.7 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (43%) |
| **No Mapping** | 13 (56%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 23 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getWallpaperId` | 3 | composite | Return safe default (null/false/0/empty) |
| `clearWallpaperOffsets` | 3 | composite | Log warning + no-op |
| `getWallpaperInfo` | 3 | composite | Return safe default (null/false/0/empty) |
| `getInstance` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBuiltInDrawable` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBuiltInDrawable` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBuiltInDrawable` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBuiltInDrawable` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDrawable` | 2 | composite | Return safe default (null/false/0/empty) |
| `isWallpaperSupported` | 2 | composite | Return safe default (null/false/0/empty) |
| `addOnColorsChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `forgetLoadedWallpaper` | 1 | none | Return safe default (null/false/0/empty) |
| `getCropAndSetWallpaperIntent` | 1 | none | Return safe default (null/false/0/empty) |
| `getDesiredMinimumHeight` | 1 | none | Return safe default (null/false/0/empty) |
| `getDesiredMinimumWidth` | 1 | none | Return safe default (null/false/0/empty) |
| `hasResourceWallpaper` | 1 | none | Return safe default (null/false/0/empty) |
| `isSetWallpaperAllowed` | 1 | none | Return safe default (null/false/0/empty) |
| `peekDrawable` | 1 | none | throw UnsupportedOperationException |
| `removeOnColorsChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `sendWallpaperCommand` | 1 | none | throw UnsupportedOperationException |
| `setWallpaperOffsetSteps` | 1 | none | Log warning + no-op |
| `setWallpaperOffsets` | 1 | none | Log warning + no-op |
| `suggestDesiredDimensions` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.WallpaperManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.WallpaperManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
