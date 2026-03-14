# SKILL: android.app.ActionBar

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.ActionBar`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.ActionBar` |
| **Package** | `android.app` |
| **Total Methods** | 44 |
| **Avg Score** | 1.7 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 15 (34%) |
| **No Mapping** | 29 (65%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 44 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getThemedContext` | 4 | partial | Return safe default (null/false/0/empty) |
| `show` | 3 | composite | throw UnsupportedOperationException |
| `setBackgroundDrawable` | 3 | composite | Log warning + no-op |
| `setSplitBackgroundDrawable` | 3 | composite | Log warning + no-op |
| `getHeight` | 3 | composite | Return safe default (null/false/0/empty) |
| `setStackedBackgroundDrawable` | 3 | composite | Log warning + no-op |
| `getHideOffset` | 3 | composite | Return safe default (null/false/0/empty) |
| `setElevation` | 3 | composite | Log warning + no-op |
| `setDisplayOptions` | 3 | composite | Return safe default (null/false/0/empty) |
| `setDisplayOptions` | 3 | composite | Return safe default (null/false/0/empty) |
| `getElevation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTitle` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSubtitle` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDisplayOptions` | 2 | composite | Return safe default (null/false/0/empty) |
| `getCustomView` | 2 | composite | Return safe default (null/false/0/empty) |
| `ActionBar` | 1 | none | Store callback, never fire |
| `addOnMenuVisibilityListener` | 1 | none | Return safe default (null/false/0/empty) |
| `hide` | 1 | none | throw UnsupportedOperationException |
| `isHideOnContentScrollEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `isShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `removeOnMenuVisibilityListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setCustomView` | 1 | none | Log warning + no-op |
| `setCustomView` | 1 | none | Log warning + no-op |
| `setCustomView` | 1 | none | Log warning + no-op |
| `setDisplayHomeAsUpEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `setDisplayShowCustomEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `setDisplayShowHomeEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `setDisplayShowTitleEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `setDisplayUseLogoEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `setHideOffset` | 1 | none | Log warning + no-op |
| `setHideOnContentScrollEnabled` | 1 | none | Log warning + no-op |
| `setHomeActionContentDescription` | 1 | none | Log warning + no-op |
| `setHomeActionContentDescription` | 1 | none | Log warning + no-op |
| `setHomeAsUpIndicator` | 1 | none | Log warning + no-op |
| `setHomeAsUpIndicator` | 1 | none | Log warning + no-op |
| `setHomeButtonEnabled` | 1 | none | Log warning + no-op |
| `setIcon` | 1 | none | Log warning + no-op |
| `setIcon` | 1 | none | Log warning + no-op |
| `setLogo` | 1 | none | Log warning + no-op |
| `setLogo` | 1 | none | Log warning + no-op |
| `setSubtitle` | 1 | none | Log warning + no-op |
| `setSubtitle` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.ActionBar`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.app.ActionBar` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 44 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
