# SKILL: android.app.MediaRouteActionProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.MediaRouteActionProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.MediaRouteActionProvider` |
| **Package** | `android.app` |
| **Total Methods** | 4 |
| **Avg Score** | 1.6 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (25%) |
| **No Mapping** | 3 (75%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setRouteTypes` | 3 | composite | Log warning + no-op |
| `MediaRouteActionProvider` | 1 | none | Store callback, never fire |
| `onCreateActionView` | 1 | none | Return dummy instance / no-op |
| `setExtendedSettingsClickListener` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.MediaRouteActionProvider`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.MediaRouteActionProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 4 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
