# SKILL: android.app.DownloadManager.Request

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.DownloadManager.Request`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.DownloadManager.Request` |
| **Package** | `android.app.DownloadManager` |
| **Total Methods** | 14 |
| **Avg Score** | 1.8 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (50%) |
| **No Mapping** | 7 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 14 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `Request` | 3 | composite | throw UnsupportedOperationException |
| `addRequestHeader` | 3 | composite | Log warning + no-op |
| `setDescription` | 2 | composite | Log warning + no-op |
| `setRequiresCharging` | 2 | composite | Log warning + no-op |
| `setDestinationUri` | 2 | composite | Log warning + no-op |
| `setMimeType` | 2 | composite | Log warning + no-op |
| `setAllowedNetworkTypes` | 2 | composite | Log warning + no-op |
| `setAllowedOverMetered` | 1 | none | Log warning + no-op |
| `setAllowedOverRoaming` | 1 | none | Log warning + no-op |
| `setDestinationInExternalFilesDir` | 1 | none | Log warning + no-op |
| `setDestinationInExternalPublicDir` | 1 | none | Log warning + no-op |
| `setNotificationVisibility` | 1 | none | Return safe default (null/false/0/empty) |
| `setRequiresDeviceIdle` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.DownloadManager.Request`:


## Quality Gates

Before marking `android.app.DownloadManager.Request` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
