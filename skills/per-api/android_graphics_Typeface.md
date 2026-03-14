# SKILL: android.graphics.Typeface

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.Typeface`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.Typeface` |
| **Package** | `android.graphics` |
| **Total Methods** | 9 |
| **Avg Score** | 3.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 2 (22%) |
| **Partial/Composite** | 5 (55%) |
| **No Mapping** | 2 (22%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 2 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `createFromFile` | `static android.graphics.Typeface createFromFile(@Nullable java.io.File)` | 7 | near | hard | `OH_Drawing_RegisterFont` | `@ohos.graphics.drawing (NDK).OH_Drawing_FontCollection` |
| `createFromFile` | `static android.graphics.Typeface createFromFile(@Nullable String)` | 7 | near | hard | `OH_Drawing_RegisterFont` | `@ohos.graphics.drawing (NDK).OH_Drawing_FontCollection` |

## Gap Descriptions (per method)

- **`createFromFile`**: Register then use
- **`createFromFile`**: Register then use

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `create` | 3 | composite | Return dummy instance / no-op |
| `create` | 3 | composite | Return dummy instance / no-op |
| `createFromAsset` | 3 | composite | Return dummy instance / no-op |
| `defaultFromStyle` | 2 | composite | throw UnsupportedOperationException |
| `getStyle` | 2 | composite | Return safe default (null/false/0/empty) |
| `isBold` | 1 | none | Return safe default (null/false/0/empty) |
| `isItalic` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.Typeface`:


## Quality Gates

Before marking `android.graphics.Typeface` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
