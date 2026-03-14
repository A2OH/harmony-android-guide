# SKILL: android.os.RemoteCallbackList<E

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.RemoteCallbackList<E`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.RemoteCallbackList<E` |
| **Package** | `android.os` |
| **Total Methods** | 14 |
| **Avg Score** | 3.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 14 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `onCallbackDied` | `void onCallbackDied(E)` | 5 | partial | moderate | `callback` | `callback?: () => void` |
| `onCallbackDied` | `void onCallbackDied(E, Object)` | 5 | partial | moderate | `callback` | `callback?: () => void` |

## Stub APIs (score < 5): 12 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `RemoteCallbackList` | 5 | partial | Return safe default (null/false/0/empty) |
| `getBroadcastCookie` | 4 | partial | Return safe default (null/false/0/empty) |
| `finishBroadcast` | 4 | composite | Return safe default (null/false/0/empty) |
| `kill` | 3 | composite | throw UnsupportedOperationException |
| `register` | 3 | composite | Return safe default (null/false/0/empty) |
| `register` | 3 | composite | Return safe default (null/false/0/empty) |
| `unregister` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRegisteredCallbackItem` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRegisteredCallbackCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRegisteredCallbackCookie` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBroadcastItem` | 3 | composite | Return safe default (null/false/0/empty) |
| `beginBroadcast` | 2 | composite | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.os.RemoteCallbackList<E`:


## Quality Gates

Before marking `android.os.RemoteCallbackList<E` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 14 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
