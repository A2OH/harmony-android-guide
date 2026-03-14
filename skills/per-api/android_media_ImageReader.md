# SKILL: android.media.ImageReader

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.ImageReader`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.ImageReader` |
| **Package** | `android.media` |
| **Total Methods** | 10 |
| **Avg Score** | 3.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 9 (90%) |
| **No Mapping** | 1 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getHeight` | `int getHeight()` | 6 | partial | moderate | `height` | `readonly height: number` |
| `getWidth` | `int getWidth()` | 6 | partial | moderate | `width` | `readonly width: number` |
| `getMaxImages` | `int getMaxImages()` | 5 | partial | moderate | `images` | `images: Array<string>, index: number, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `close` | 4 | partial | No-op |
| `acquireLatestImage` | 4 | partial | throw UnsupportedOperationException |
| `getImageFormat` | 3 | composite | Return safe default (null/false/0/empty) |
| `discardFreeBuffers` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSurface` | 2 | composite | Return safe default (null/false/0/empty) |
| `acquireNextImage` | 2 | composite | throw UnsupportedOperationException |
| `setOnImageAvailableListener` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.ImageReader`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.ImageReader` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
