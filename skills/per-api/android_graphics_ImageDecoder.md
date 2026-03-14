# SKILL: android.graphics.ImageDecoder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.ImageDecoder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.ImageDecoder` |
| **Package** | `android.graphics` |
| **Total Methods** | 18 |
| **Avg Score** | 2.7 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 3 (16%) |
| **Partial/Composite** | 3 (16%) |
| **No Mapping** | 12 (66%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `close` | `void close()` | 9 | direct | hard | `release` | `@ohos.multimedia.image.ImageSource` |
| `setMutableRequired` | `void setMutableRequired(boolean)` | 9 | direct | impossible | `editable` | `@ohos.multimedia.image.DecodingOptions` |
| `setTargetSize` | `void setTargetSize(@IntRange(from=1) @Px int, @IntRange(from=1) @Px int)` | 9 | direct | hard | `desiredSize` | `@ohos.multimedia.image.DecodingOptions` |

## Gap Descriptions (per method)

- **`close`**: Naming diff
- **`setMutableRequired`**: Naming diff
- **`setTargetSize`**: Direct

## Stub APIs (score < 5): 15 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setOnPartialImageListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setTargetSampleSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `setTargetColorSpace` | 2 | composite | Return safe default (null/false/0/empty) |
| `getAllocator` | 1 | none | Return safe default (null/false/0/empty) |
| `getMemorySizePolicy` | 1 | none | Return safe default (null/false/0/empty) |
| `isDecodeAsAlphaMaskEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `isMimeTypeSupported` | 1 | none | Return safe default (null/false/0/empty) |
| `isMutableRequired` | 1 | none | Return safe default (null/false/0/empty) |
| `isUnpremultipliedRequired` | 1 | none | Return safe default (null/false/0/empty) |
| `setAllocator` | 1 | none | Log warning + no-op |
| `setCrop` | 1 | none | Log warning + no-op |
| `setDecodeAsAlphaMaskEnabled` | 1 | none | Log warning + no-op |
| `setMemorySizePolicy` | 1 | none | Log warning + no-op |
| `setPostProcessor` | 1 | none | Log warning + no-op |
| `setUnpremultipliedRequired` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.ImageDecoder`:


## Quality Gates

Before marking `android.graphics.ImageDecoder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
