# SKILL: android.os.Binder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Binder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Binder` |
| **Package** | `android.os` |
| **Total Methods** | 23 |
| **Avg Score** | 2.1 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 12 (52%) |
| **No Mapping** | 11 (47%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 23 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `restoreCallingIdentity` | 5 | partial | throw UnsupportedOperationException |
| `onTransact` | 4 | partial | Store callback, never fire |
| `clearCallingIdentity` | 4 | partial | throw UnsupportedOperationException |
| `attachInterface` | 3 | composite | throw UnsupportedOperationException |
| `getCallingPid` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCallingUid` | 3 | composite | Return safe default (null/false/0/empty) |
| `unlinkToDeath` | 3 | composite | throw UnsupportedOperationException |
| `isBinderAlive` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCallingUidOrThrow` | 3 | composite | Return safe default (null/false/0/empty) |
| `clearCallingWorkSource` | 3 | composite | throw UnsupportedOperationException |
| `getCallingWorkSourceUid` | 2 | composite | Return safe default (null/false/0/empty) |
| `joinThreadPool` | 2 | composite | Return safe default (null/false/0/empty) |
| `Binder` | 1 | none | throw UnsupportedOperationException |
| `Binder` | 1 | none | throw UnsupportedOperationException |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `dump` | 1 | none | throw UnsupportedOperationException |
| `dumpAsync` | 1 | none | throw UnsupportedOperationException |
| `flushPendingCommands` | 1 | none | throw UnsupportedOperationException |
| `linkToDeath` | 1 | none | throw UnsupportedOperationException |
| `pingBinder` | 1 | none | throw UnsupportedOperationException |
| `restoreCallingWorkSource` | 1 | none | throw UnsupportedOperationException |
| `setCallingWorkSourceUid` | 1 | none | Log warning + no-op |
| `transact` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Binder`:


## Quality Gates

Before marking `android.os.Binder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
