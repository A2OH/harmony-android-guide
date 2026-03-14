# SKILL: android.app.Application.ActivityLifecycleCallbacks

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Application.ActivityLifecycleCallbacks`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Application.ActivityLifecycleCallbacks` |
| **Package** | `android.app.Application` |
| **Total Methods** | 21 |
| **Avg Score** | 1.5 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (14%) |
| **No Mapping** | 18 (85%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 21 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onActivityPrePaused` | 5 | partial | Store callback, never fire |
| `onActivityPreStarted` | 5 | partial | Return dummy instance / no-op |
| `onActivitySaveInstanceState` | 3 | composite | Store callback, never fire |
| `onActivityCreated` | 1 | none | Return dummy instance / no-op |
| `onActivityDestroyed` | 1 | none | No-op |
| `onActivityPaused` | 1 | none | Store callback, never fire |
| `onActivityPostCreated` | 1 | none | Return dummy instance / no-op |
| `onActivityPostDestroyed` | 1 | none | No-op |
| `onActivityPostPaused` | 1 | none | Store callback, never fire |
| `onActivityPostResumed` | 1 | none | Store callback, never fire |
| `onActivityPostSaveInstanceState` | 1 | none | Store callback, never fire |
| `onActivityPostStarted` | 1 | none | Return dummy instance / no-op |
| `onActivityPostStopped` | 1 | none | No-op |
| `onActivityPreCreated` | 1 | none | Return dummy instance / no-op |
| `onActivityPreDestroyed` | 1 | none | No-op |
| `onActivityPreResumed` | 1 | none | Store callback, never fire |
| `onActivityPreSaveInstanceState` | 1 | none | Store callback, never fire |
| `onActivityPreStopped` | 1 | none | No-op |
| `onActivityResumed` | 1 | none | Store callback, never fire |
| `onActivityStarted` | 1 | none | Return dummy instance / no-op |
| `onActivityStopped` | 1 | none | No-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Application.ActivityLifecycleCallbacks`:


## Quality Gates

Before marking `android.app.Application.ActivityLifecycleCallbacks` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
