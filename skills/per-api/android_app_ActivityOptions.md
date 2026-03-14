# SKILL: android.app.ActivityOptions

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.ActivityOptions`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.ActivityOptions` |
| **Package** | `android.app` |
| **Total Methods** | 16 |
| **Avg Score** | 1.7 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (37%) |
| **No Mapping** | 10 (62%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 16 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `update` | 3 | composite | Log warning + no-op |
| `requestUsageTimeReport` | 3 | composite | Return safe default (null/false/0/empty) |
| `toBundle` | 3 | composite | throw UnsupportedOperationException |
| `makeScaleUpAnimation` | 2 | composite | Store callback, never fire |
| `setAppVerificationBundle` | 2 | composite | Log warning + no-op |
| `getLaunchDisplayId` | 2 | composite | Return safe default (null/false/0/empty) |
| `getLockTaskMode` | 1 | none | Return safe default (null/false/0/empty) |
| `makeBasic` | 1 | none | throw UnsupportedOperationException |
| `makeClipRevealAnimation` | 1 | none | Store callback, never fire |
| `makeCustomAnimation` | 1 | none | Store callback, never fire |
| `makeSceneTransitionAnimation` | 1 | none | Store callback, never fire |
| `makeTaskLaunchBehind` | 1 | none | throw UnsupportedOperationException |
| `makeThumbnailScaleUpAnimation` | 1 | none | Store callback, never fire |
| `setLaunchBounds` | 1 | none | Log warning + no-op |
| `setLaunchDisplayId` | 1 | none | Return safe default (null/false/0/empty) |
| `setLockTaskEnabled` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.ActivityOptions`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.ActivityOptions` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 16 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
