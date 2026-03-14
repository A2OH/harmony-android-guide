# SKILL: android.media.Image

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.Image`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.Image` |
| **Package** | `android.media` |
| **Total Methods** | 12 |
| **Avg Score** | 3.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (91%) |
| **No Mapping** | 1 (8%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getHeight` | `abstract int getHeight()` | 6 | partial | moderate | `height` | `readonly height: number` |
| `getWidth` | `abstract int getWidth()` | 6 | partial | moderate | `width` | `readonly width: number` |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `close` | 4 | partial | No-op |
| `getFormat` | 4 | partial | Return safe default (null/false/0/empty) |
| `getCropRect` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPlanes` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRowStride` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBuffer` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPixelStride` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTimestamp` | 3 | composite | Return safe default (null/false/0/empty) |
| `setTimestamp` | 2 | composite | Log warning + no-op |
| `setCropRect` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.Image`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.Image` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
