# SKILL: android.os.FileObserver

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.FileObserver`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.FileObserver` |
| **Package** | `android.os` |
| **Total Methods** | 8 |
| **Avg Score** | 3.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (87%) |
| **No Mapping** | 1 (12%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onEvent` | `abstract void onEvent(int, @Nullable String)` | 6 | partial | moderate | `onEvent` | `onEvent: (info: SysEventInfo) => void` |
| `startWatching` | `void startWatching()` | 5 | partial | moderate | `startScan` | `startScan(): void` |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `stopWatching` | 3 | composite | No-op |
| `FileObserver` | 3 | composite | throw UnsupportedOperationException |
| `FileObserver` | 3 | composite | throw UnsupportedOperationException |
| `FileObserver` | 3 | composite | throw UnsupportedOperationException |
| `FileObserver` | 3 | composite | throw UnsupportedOperationException |
| `finalize` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.FileObserver`:


## Quality Gates

Before marking `android.os.FileObserver` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
