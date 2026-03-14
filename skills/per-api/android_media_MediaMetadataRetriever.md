# SKILL: android.media.MediaMetadataRetriever

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaMetadataRetriever`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaMetadataRetriever` |
| **Package** | `android.media` |
| **Total Methods** | 9 |
| **Avg Score** | 6.6 |
| **Scenario** | S7: Async/Threading Gap |
| **Strategy** | Promise wrapping, Handler/Looper emulation |
| **Direct/Near** | 7 (77%) |
| **Partial/Composite** | 2 (22%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 6 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock with concurrency tests) |

## Implementable APIs (score >= 5): 7 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 9 | direct | hard | `release` | `@ohos.multimedia.media.AVMetadataExtractor` |
| `setDataSource` | `void setDataSource(String) throws java.lang.IllegalArgumentException` | 7 | near | rewrite | `fdSrc` | `@ohos.multimedia.media.AVMetadataExtractor` |
| `setDataSource` | `void setDataSource(String, java.util.Map<java.lang.String,java.lang.String>) throws java.lang.IllegalArgumentException` | 7 | near | rewrite | `fdSrc` | `@ohos.multimedia.media.AVMetadataExtractor` |
| `setDataSource` | `void setDataSource(java.io.FileDescriptor, long, long) throws java.lang.IllegalArgumentException` | 7 | near | rewrite | `fdSrc` | `@ohos.multimedia.media.AVMetadataExtractor` |
| `setDataSource` | `void setDataSource(java.io.FileDescriptor) throws java.lang.IllegalArgumentException` | 7 | near | rewrite | `fdSrc` | `@ohos.multimedia.media.AVMetadataExtractor` |
| `setDataSource` | `void setDataSource(android.content.Context, android.net.Uri) throws java.lang.IllegalArgumentException, java.lang.SecurityException` | 7 | near | rewrite | `fdSrc` | `@ohos.multimedia.media.AVMetadataExtractor` |
| `setDataSource` | `void setDataSource(android.media.MediaDataSource) throws java.lang.IllegalArgumentException` | 7 | near | rewrite | `fdSrc` | `@ohos.multimedia.media.AVMetadataExtractor` |

## Gap Descriptions (per method)

- **`release`**: Direct equivalent
- **`setDataSource`**: Property assignment
- **`setDataSource`**: Property assignment
- **`setDataSource`**: Property assignment
- **`setDataSource`**: Property assignment
- **`setDataSource`**: Property assignment
- **`setDataSource`**: Property assignment

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `MediaMetadataRetriever` | 4 | partial | throw UnsupportedOperationException |
| `close` | 4 | partial | No-op |

## AI Agent Instructions

**Scenario: S7 — Async/Threading Gap**

1. Implement using Java concurrency primitives (ExecutorService, BlockingQueue)
2. For Handler: single-thread executor + message queue
3. For AsyncTask: thread pool + callbacks
4. For sync-over-async: CompletableFuture wrapping OH Promise (in bridge)
5. Test with concurrent calls to verify thread safety
6. Add timeout to all blocking operations to prevent deadlock

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaMetadataRetriever`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaMetadataRetriever` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 9 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 7 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
