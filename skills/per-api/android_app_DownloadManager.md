# SKILL: android.app.DownloadManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.DownloadManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.DownloadManager` |
| **Package** | `android.app` |
| **Total Methods** | 8 |
| **Avg Score** | 3.9 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 3 (37%) |
| **Partial/Composite** | 1 (12%) |
| **No Mapping** | 4 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `enqueue` | `long enqueue(android.app.DownloadManager.Request)` | 9 | direct | hard | `downloadFile` | `downloadFile(context: BaseContext, config: DownloadConfig, callback: AsyncCallback<DownloadTask>): void` |
| `remove` | `int remove(long...)` | 9 | direct | moderate | `delete` | `download(config: DownloadConfig, callback: AsyncCallback<DownloadTask>): void` |
| `query` | `android.database.Cursor query(android.app.DownloadManager.Query)` | 7 | near | rewrite | `query` | `@ohos.request.DownloadTask` |

## Gap Descriptions (per method)

- **`enqueue`**: Download
- **`remove`**: Delete download
- **`query`**: Query download status

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getUriForDownloadedFile` | 2 | composite | Return safe default (null/false/0/empty) |
| `getMaxBytesOverMobile` | 1 | none | Return safe default (null/false/0/empty) |
| `getMimeTypeForDownloadedFile` | 1 | none | Return safe default (null/false/0/empty) |
| `getRecommendedMaxBytesOverMobile` | 1 | none | Return safe default (null/false/0/empty) |
| `openDownloadedFile` | 1 | none | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.DownloadManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.DownloadManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
