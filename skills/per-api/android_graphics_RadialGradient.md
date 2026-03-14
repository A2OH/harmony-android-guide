# SKILL: android.graphics.RadialGradient

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.RadialGradient`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.RadialGradient` |
| **Package** | `android.graphics` |
| **Total Methods** | 4 |
| **Avg Score** | 9.0 |
| **Scenario** | S1: Direct Mapping (Thin Wrapper) |
| **Strategy** | Simple delegation to OHBridge |
| **Direct/Near** | 4 (100%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `RadialGradient` | `RadialGradient(float, float, float, @ColorInt @NonNull int[], @Nullable float[], @NonNull android.graphics.Shader.TileMode)` | 9 | direct | impossible | `OH_Drawing_ShaderEffectCreateRadialGradient` | `@ohos.graphics.drawing (NDK).OH_Drawing_ShaderEffect` |
| `RadialGradient` | `RadialGradient(float, float, float, @ColorLong @NonNull long[], @Nullable float[], @NonNull android.graphics.Shader.TileMode)` | 9 | direct | impossible | `OH_Drawing_ShaderEffectCreateRadialGradient` | `@ohos.graphics.drawing (NDK).OH_Drawing_ShaderEffect` |
| `RadialGradient` | `RadialGradient(float, float, float, @ColorInt int, @ColorInt int, @NonNull android.graphics.Shader.TileMode)` | 9 | direct | impossible | `OH_Drawing_ShaderEffectCreateRadialGradient` | `@ohos.graphics.drawing (NDK).OH_Drawing_ShaderEffect` |
| `RadialGradient` | `RadialGradient(float, float, float, @ColorLong long, @ColorLong long, @NonNull android.graphics.Shader.TileMode)` | 9 | direct | impossible | `OH_Drawing_ShaderEffectCreateRadialGradient` | `@ohos.graphics.drawing (NDK).OH_Drawing_ShaderEffect` |

## Gap Descriptions (per method)

- **`RadialGradient`**: Direct
- **`RadialGradient`**: Direct
- **`RadialGradient`**: Direct
- **`RadialGradient`**: Direct

## AI Agent Instructions

**Scenario: S1 — Direct Mapping (Thin Wrapper)**

1. Create Java shim at `shim/java/android/graphics/RadialGradient.java`
2. For each method, delegate to `OHBridge.xxx()` — one bridge call per Android call
3. Add `static native` declarations to `OHBridge.java`
4. Add mock implementations to `test-apps/mock/.../OHBridge.java`
5. Add test section to `HeadlessTest.java` — call each method with valid + edge inputs
6. Test null args, boundary values, return types

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.RadialGradient`:


## Quality Gates

Before marking `android.graphics.RadialGradient` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
