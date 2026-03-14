# SKILL: android.app.AppOpsManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AppOpsManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AppOpsManager` |
| **Package** | `android.app` |
| **Total Methods** | 18 |
| **Avg Score** | 1.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (38%) |
| **No Mapping** | 11 (61%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 18 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `startOpNoThrow` | 3 | composite | Return dummy instance / no-op |
| `noteProxyOp` | 3 | composite | throw UnsupportedOperationException |
| `startWatchingMode` | 3 | composite | Return dummy instance / no-op |
| `startWatchingMode` | 3 | composite | Return dummy instance / no-op |
| `setOnOpNotedCallback` | 3 | composite | Log warning + no-op |
| `startWatchingActive` | 2 | composite | Return dummy instance / no-op |
| `startOp` | 2 | composite | Return dummy instance / no-op |
| `finishOp` | 1 | none | Return safe default (null/false/0/empty) |
| `isOpActive` | 1 | none | Return safe default (null/false/0/empty) |
| `noteOp` | 1 | none | throw UnsupportedOperationException |
| `noteOpNoThrow` | 1 | none | throw UnsupportedOperationException |
| `noteProxyOpNoThrow` | 1 | none | throw UnsupportedOperationException |
| `stopWatchingActive` | 1 | none | No-op |
| `stopWatchingMode` | 1 | none | No-op |
| `unsafeCheckOp` | 1 | none | throw UnsupportedOperationException |
| `unsafeCheckOpNoThrow` | 1 | none | throw UnsupportedOperationException |
| `unsafeCheckOpRaw` | 1 | none | throw UnsupportedOperationException |
| `unsafeCheckOpRawNoThrow` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.AppOpsManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.AppOpsManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
