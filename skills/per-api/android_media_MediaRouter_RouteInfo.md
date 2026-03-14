# SKILL: android.media.MediaRouter.RouteInfo

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter.RouteInfo`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter.RouteInfo` |
| **Package** | `android.media.MediaRouter` |
| **Total Methods** | 21 |
| **Avg Score** | 2.9 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 17 (80%) |
| **No Mapping** | 4 (19%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isEnabled` | `boolean isEnabled()` | 5 | partial | moderate | `isEnabled` | `readonly isEnabled?: boolean` |
| `isConnecting` | `boolean isConnecting()` | 5 | partial | moderate | `isOnline` | `readonly isOnline: boolean` |
| `getDeviceType` | `int getDeviceType()` | 5 | partial | moderate | `deviceType` | `readonly deviceType: DeviceType` |

## Stub APIs (score < 5): 18 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getGroup` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTag` | 4 | partial | Return safe default (null/false/0/empty) |
| `getVolume` | 3 | composite | Return safe default (null/false/0/empty) |
| `getName` | 3 | composite | Return safe default (null/false/0/empty) |
| `getName` | 3 | composite | Return safe default (null/false/0/empty) |
| `getVolumeMax` | 3 | composite | Return safe default (null/false/0/empty) |
| `getStatus` | 3 | composite | Return safe default (null/false/0/empty) |
| `requestSetVolume` | 3 | composite | Log warning + no-op |
| `getDescription` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPlaybackType` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCategory` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPlaybackStream` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSupportedTypes` | 2 | composite | Return safe default (null/false/0/empty) |
| `getIconDrawable` | 2 | composite | Return safe default (null/false/0/empty) |
| `getPresentationDisplay` | 1 | none | Return safe default (null/false/0/empty) |
| `getVolumeHandling` | 1 | none | Return safe default (null/false/0/empty) |
| `requestUpdateVolume` | 1 | none | Log warning + no-op |
| `setTag` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter.RouteInfo`:


## Quality Gates

Before marking `android.media.MediaRouter.RouteInfo` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
