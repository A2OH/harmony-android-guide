# SKILL: android.os.Debug.MemoryInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Debug.MemoryInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Debug.MemoryInfo` |
| **Package** | `android.os.Debug` |
| **Total Methods** | 12 |
| **Avg Score** | 3.2 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 10 (83%) |
| **No Mapping** | 2 (16%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getTotalPrivateDirty` | 5 | partial | Return safe default (null/false/0/empty) |
| `getTotalSharedDirty` | 5 | partial | Return safe default (null/false/0/empty) |
| `getMemoryStats` | 4 | partial | Return safe default (null/false/0/empty) |
| `getMemoryStat` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTotalSwappablePss` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `MemoryInfo` | 3 | composite | throw UnsupportedOperationException |
| `getTotalPss` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTotalSharedClean` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTotalPrivateClean` | 3 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `readFromParcel` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Debug.MemoryInfo`:


## Quality Gates

Before marking `android.os.Debug.MemoryInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 12 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
