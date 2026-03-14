# SKILL: android.graphics.BitmapFactory

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.graphics.BitmapFactory`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.graphics.BitmapFactory` |
| **Package** | `android.graphics` |
| **Total Methods** | 10 |
| **Avg Score** | 5.0 |
| **Scenario** | S7: Async/Threading Gap |
| **Strategy** | Promise wrapping, Handler/Looper emulation |
| **Direct/Near** | 6 (60%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 4 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 4 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock with concurrency tests) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `decodeFileDescriptor` | `static android.graphics.Bitmap decodeFileDescriptor(java.io.FileDescriptor, android.graphics.Rect, android.graphics.BitmapFactory.Options)` | 9 | direct | impossible | `createImageSource(fd)` | `@ohos.multimedia.image.ImageSource` |
| `decodeFileDescriptor` | `static android.graphics.Bitmap decodeFileDescriptor(java.io.FileDescriptor)` | 9 | direct | impossible | `createImageSource(fd)` | `@ohos.multimedia.image.ImageSource` |
| `decodeByteArray` | `static android.graphics.Bitmap decodeByteArray(byte[], int, int, android.graphics.BitmapFactory.Options)` | 7 | near | impossible | `createImageSource(ArrayBuffer)+createPixelMap` | `@ohos.multimedia.image.ImageSource` |
| `decodeByteArray` | `static android.graphics.Bitmap decodeByteArray(byte[], int, int)` | 7 | near | impossible | `createImageSource(ArrayBuffer)+createPixelMap` | `@ohos.multimedia.image.ImageSource` |
| `decodeFile` | `static android.graphics.Bitmap decodeFile(String, android.graphics.BitmapFactory.Options)` | 7 | near | impossible | `createImageSource+createPixelMap` | `@ohos.multimedia.image.ImageSource` |
| `decodeFile` | `static android.graphics.Bitmap decodeFile(String)` | 7 | near | impossible | `createImageSource+createPixelMap` | `@ohos.multimedia.image.ImageSource` |

## Gap Descriptions (per method)

- **`decodeFileDescriptor`**: Direct
- **`decodeFileDescriptor`**: Direct
- **`decodeByteArray`**: byte[]→ArrayBuffer
- **`decodeByteArray`**: byte[]→ArrayBuffer
- **`decodeFile`**: Two-step decode
- **`decodeFile`**: Two-step decode

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `BitmapFactory` | 1 | none | throw UnsupportedOperationException |
| `decodeResource` | 1 | none | throw UnsupportedOperationException |
| `decodeResource` | 1 | none | throw UnsupportedOperationException |
| `decodeStream` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S7 — Async/Threading Gap**

1. Implement using Java concurrency primitives (ExecutorService, BlockingQueue)
2. For Handler: single-thread executor + message queue
3. For AsyncTask: thread pool + callbacks
4. For sync-over-async: CompletableFuture wrapping OH Promise (in bridge)
5. Test with concurrent calls to verify thread safety
6. Add timeout to all blocking operations to prevent deadlock

## Dependencies

Check if these related classes are already shimmed before generating `android.graphics.BitmapFactory`:


## Quality Gates

Before marking `android.graphics.BitmapFactory` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
