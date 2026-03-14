# SKILL: android.content.ContentProviderResult

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ContentProviderResult`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ContentProviderResult` |
| **Package** | `android.content` |
| **Total Methods** | 7 |
| **Avg Score** | 1.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 7 (100%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 7 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `ContentProviderResult` | 1 | none | Store callback, never fire |
| `ContentProviderResult` | 1 | none | Store callback, never fire |
| `ContentProviderResult` | 1 | none | Store callback, never fire |
| `ContentProviderResult` | 1 | none | Store callback, never fire |
| `ContentProviderResult` | 1 | none | Store callback, never fire |
| `describeContents` | 1 | none | Store callback, never fire |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ContentProviderResult`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ContentProviderResult` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
