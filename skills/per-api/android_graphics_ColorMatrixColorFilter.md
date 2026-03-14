# SKILL: android.graphics.ColorMatrixColorFilter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.ColorMatrixColorFilter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.ColorMatrixColorFilter` |
| **Package** | `android.graphics` |
| **Total Methods** | 3 |
| **Avg Score** | 6.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 2 (66%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 1 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `ColorMatrixColorFilter` | `ColorMatrixColorFilter(@NonNull android.graphics.ColorMatrix)` | 9 | direct | rewrite | `OH_Drawing_ColorFilterCreateMatrix` | `@ohos.graphics.drawing (NDK).OH_Drawing_ColorFilter` |
| `ColorMatrixColorFilter` | `ColorMatrixColorFilter(@NonNull float[])` | 9 | direct | rewrite | `OH_Drawing_ColorFilterCreateMatrix` | `@ohos.graphics.drawing (NDK).OH_Drawing_ColorFilter` |

## Gap Descriptions (per method)

- **`ColorMatrixColorFilter`**: float[20]
- **`ColorMatrixColorFilter`**: float[20]

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getColorMatrix` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 1 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.ColorMatrixColorFilter`:


## Quality Gates

Before marking `android.graphics.ColorMatrixColorFilter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
