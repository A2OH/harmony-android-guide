# SKILL: android.media.MediaScannerConnection

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaScannerConnection`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaScannerConnection` |
| **Package** | `android.media` |
| **Total Methods** | 8 |
| **Avg Score** | 2.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (50%) |
| **No Mapping** | 4 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isConnected` | `boolean isConnected()` | 5 | partial | moderate | `isConnected` | `isConnected(): boolean` |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `disconnect` | 4 | partial | Return dummy instance / no-op |
| `MediaScannerConnection` | 3 | composite | Return dummy instance / no-op |
| `connect` | 3 | composite | Return dummy instance / no-op |
| `onServiceConnected` | 1 | none | Return dummy instance / no-op |
| `onServiceDisconnected` | 1 | none | Return dummy instance / no-op |
| `scanFile` | 1 | none | Return safe default (null/false/0/empty) |
| `scanFile` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaScannerConnection`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaScannerConnection` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
