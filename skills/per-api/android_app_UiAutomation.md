# SKILL: android.app.UiAutomation

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.UiAutomation`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.UiAutomation` |
| **Package** | `android.app` |
| **Total Methods** | 25 |
| **Avg Score** | 2.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 17 (68%) |
| **No Mapping** | 8 (32%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 25 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `revokeRuntimePermission` | 4 | composite | Return safe default (null/false/0/empty) |
| `grantRuntimePermission` | 4 | composite | Return safe default (null/false/0/empty) |
| `revokeRuntimePermissionAsUser` | 4 | composite | Return safe default (null/false/0/empty) |
| `grantRuntimePermissionAsUser` | 4 | composite | Return safe default (null/false/0/empty) |
| `setServiceInfo` | 3 | composite | Log warning + no-op |
| `dropShellPermissionIdentity` | 3 | composite | Return safe default (null/false/0/empty) |
| `getWindows` | 3 | composite | Return safe default (null/false/0/empty) |
| `clearWindowAnimationFrameStats` | 3 | composite | Store callback, never fire |
| `adoptShellPermissionIdentity` | 3 | composite | Return safe default (null/false/0/empty) |
| `adoptShellPermissionIdentity` | 3 | composite | Return safe default (null/false/0/empty) |
| `findFocus` | 3 | composite | Return safe default (null/false/0/empty) |
| `getServiceInfo` | 3 | composite | Return safe default (null/false/0/empty) |
| `setRotation` | 3 | composite | Log warning + no-op |
| `getRootInActiveWindow` | 3 | composite | Return safe default (null/false/0/empty) |
| `executeShellCommand` | 2 | composite | throw UnsupportedOperationException |
| `setOnAccessibilityEventListener` | 2 | composite | Return safe default (null/false/0/empty) |
| `getWindowContentFrameStats` | 2 | composite | Return safe default (null/false/0/empty) |
| `clearWindowContentFrameStats` | 1 | none | Store callback, never fire |
| `executeAndWaitForEvent` | 1 | none | throw UnsupportedOperationException |
| `getWindowAnimationFrameStats` | 1 | none | Return safe default (null/false/0/empty) |
| `injectInputEvent` | 1 | none | Log warning + no-op |
| `performGlobalAction` | 1 | none | Store callback, never fire |
| `setRunAsMonkey` | 1 | none | Log warning + no-op |
| `takeScreenshot` | 1 | none | throw UnsupportedOperationException |
| `waitForIdle` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.UiAutomation`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.UiAutomation` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 25 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
