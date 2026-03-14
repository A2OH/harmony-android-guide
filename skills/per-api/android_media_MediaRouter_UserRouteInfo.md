# SKILL: android.media.MediaRouter.UserRouteInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter.UserRouteInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter.UserRouteInfo` |
| **Package** | `android.media.MediaRouter` |
| **Total Methods** | 17 |
| **Avg Score** | 2.9 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 12 (70%) |
| **No Mapping** | 5 (29%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setVolumeCallback` | `void setVolumeCallback(android.media.MediaRouter.VolumeCallback)` | 5 | partial | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |

## Stub APIs (score < 5): 16 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `VolumeCallback` | 5 | partial | throw UnsupportedOperationException |
| `setName` | 4 | partial | Log warning + no-op |
| `setName` | 4 | partial | Log warning + no-op |
| `setIconDrawable` | 4 | composite | Log warning + no-op |
| `setPlaybackType` | 3 | composite | Log warning + no-op |
| `setPlaybackStream` | 3 | composite | Log warning + no-op |
| `setRemoteControlClient` | 3 | composite | Log warning + no-op |
| `setIconResource` | 3 | composite | Log warning + no-op |
| `getRemoteControlClient` | 3 | composite | Return safe default (null/false/0/empty) |
| `setVolume` | 3 | composite | Log warning + no-op |
| `setVolumeMax` | 2 | composite | Log warning + no-op |
| `setDescription` | 1 | none | Log warning + no-op |
| `setStatus` | 1 | none | Log warning + no-op |
| `setVolumeHandling` | 1 | none | Log warning + no-op |
| `onVolumeSetRequest` | 1 | none | Log warning + no-op |
| `onVolumeUpdateRequest` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter.UserRouteInfo`:


## Quality Gates

Before marking `android.media.MediaRouter.UserRouteInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
