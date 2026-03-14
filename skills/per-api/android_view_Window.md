# SKILL: android.view.Window

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.Window`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.Window` |
| **Package** | `android.view` |
| **Total Methods** | 129 |
| **Avg Score** | 1.3 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 4 (3%) |
| **Partial/Composite** | 6 (4%) |
| **No Mapping** | 119 (92%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 128 |
| **Has Async Gap** | 124 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAttributes` | `final android.view.WindowManager.LayoutParams getAttributes()` | 7 | near | hard | `getWindowProperties` | `@ohos.window.Window` |
| `setLayout` | `void setLayout(int, int)` | 7 | near | impossible | `resize` | `@ohos.window.Window` |
| `setNavigationBarColor` | `abstract void setNavigationBarColor(@ColorInt int)` | 7 | near | impossible | `setWindowSystemBarProperties` | `@ohos.window.Window` |
| `setStatusBarColor` | `abstract void setStatusBarColor(@ColorInt int)` | 7 | near | impossible | `setWindowSystemBarProperties` | `@ohos.window.Window` |

## Gap Descriptions (per method)

- **`getAttributes`**: WindowProperties object
- **`setLayout`**: Near equivalent
- **`setNavigationBarColor`**: Color as hex string
- **`setStatusBarColor`**: Color as hex string

## Stub APIs (score < 5): 125 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getContext` | 3 | composite | Return safe default (null/false/0/empty) |
| `isActive` | 3 | composite | Return safe default (null/false/0/empty) |
| `setCallback` | 2 | composite | Log warning + no-op |
| `getCallback` | 2 | composite | Return safe default (null/false/0/empty) |
| `getReturnTransition` | 2 | composite | Return safe default (null/false/0/empty) |
| `closeAllPanels` | 2 | composite | No-op |
| `Window` | 1 | none | throw UnsupportedOperationException |
| `addContentView` | 1 | none | Log warning + no-op |
| `addFlags` | 1 | none | Log warning + no-op |
| `addOnFrameMetricsAvailableListener` | 1 | none | Return safe default (null/false/0/empty) |
| `clearFlags` | 1 | none | throw UnsupportedOperationException |
| `closePanel` | 1 | none | No-op |
| `findViewById` | 1 | none | Return safe default (null/false/0/empty) |
| `getAllowEnterTransitionOverlap` | 1 | none | Return safe default (null/false/0/empty) |
| `getAllowReturnTransitionOverlap` | 1 | none | Return safe default (null/false/0/empty) |
| `getColorMode` | 1 | none | Return safe default (null/false/0/empty) |
| `getContainer` | 1 | none | Return safe default (null/false/0/empty) |
| `getContentScene` | 1 | none | Return safe default (null/false/0/empty) |
| `getDefaultFeatures` | 1 | none | Return safe default (null/false/0/empty) |
| `getEnterTransition` | 1 | none | Return safe default (null/false/0/empty) |
| `getExitTransition` | 1 | none | Return safe default (null/false/0/empty) |
| `getFeatures` | 1 | none | Return safe default (null/false/0/empty) |
| `getForcedWindowFlags` | 1 | none | Return safe default (null/false/0/empty) |
| `getLocalFeatures` | 1 | none | Return safe default (null/false/0/empty) |
| `getMediaController` | 1 | none | Return safe default (null/false/0/empty) |
| `getReenterTransition` | 1 | none | Return safe default (null/false/0/empty) |
| `getSharedElementEnterTransition` | 1 | none | Return safe default (null/false/0/empty) |
| `getSharedElementExitTransition` | 1 | none | Return safe default (null/false/0/empty) |
| `getSharedElementReenterTransition` | 1 | none | Return safe default (null/false/0/empty) |
| `getSharedElementReturnTransition` | 1 | none | Return safe default (null/false/0/empty) |
| `getSharedElementsUseOverlay` | 1 | none | Return safe default (null/false/0/empty) |
| `getTransitionBackgroundFadeDuration` | 1 | none | Return safe default (null/false/0/empty) |
| `getTransitionManager` | 1 | none | Return safe default (null/false/0/empty) |
| `getVolumeControlStream` | 1 | none | Return safe default (null/false/0/empty) |
| `getWindowManager` | 1 | none | Return safe default (null/false/0/empty) |
| `getWindowStyle` | 1 | none | Return safe default (null/false/0/empty) |
| `hasChildren` | 1 | none | Return safe default (null/false/0/empty) |
| `hasFeature` | 1 | none | Return safe default (null/false/0/empty) |
| `hasSoftInputMode` | 1 | none | Return safe default (null/false/0/empty) |
| `injectInputEvent` | 1 | none | Log warning + no-op |
| `invalidatePanelMenu` | 1 | none | throw UnsupportedOperationException |
| `isFloating` | 1 | none | Return safe default (null/false/0/empty) |
| `isNavigationBarContrastEnforced` | 1 | none | Return safe default (null/false/0/empty) |
| `isShortcutKey` | 1 | none | Return safe default (null/false/0/empty) |
| `isStatusBarContrastEnforced` | 1 | none | Return safe default (null/false/0/empty) |
| `isWideColorGamut` | 1 | none | Return safe default (null/false/0/empty) |
| `makeActive` | 1 | none | throw UnsupportedOperationException |
| `onActive` | 1 | none | Store callback, never fire |
| `onConfigurationChanged` | 1 | none | Store callback, never fire |
| `openPanel` | 1 | none | Return dummy instance / no-op |
| `peekDecorView` | 1 | none | throw UnsupportedOperationException |
| `performContextMenuIdentifierAction` | 1 | none | Store callback, never fire |
| `performPanelIdentifierAction` | 1 | none | Store callback, never fire |
| `performPanelShortcut` | 1 | none | throw UnsupportedOperationException |
| `removeOnFrameMetricsAvailableListener` | 1 | none | Return safe default (null/false/0/empty) |
| `requestFeature` | 1 | none | throw UnsupportedOperationException |
| `restoreHierarchyState` | 1 | none | throw UnsupportedOperationException |
| `saveHierarchyState` | 1 | none | throw UnsupportedOperationException |
| `setAllowEnterTransitionOverlap` | 1 | none | Log warning + no-op |
| `setAllowReturnTransitionOverlap` | 1 | none | Log warning + no-op |
| `setAttributes` | 1 | none | Log warning + no-op |
| `setBackgroundDrawable` | 1 | none | Log warning + no-op |
| `setBackgroundDrawableResource` | 1 | none | Log warning + no-op |
| `setChildDrawable` | 1 | none | Log warning + no-op |
| `setChildInt` | 1 | none | Log warning + no-op |
| `setClipToOutline` | 1 | none | Log warning + no-op |
| `setColorMode` | 1 | none | Log warning + no-op |
| `setContainer` | 1 | none | Log warning + no-op |
| `setContentView` | 1 | none | Log warning + no-op |
| `setContentView` | 1 | none | Log warning + no-op |
| `setContentView` | 1 | none | Log warning + no-op |
| `setDecorCaptionShade` | 1 | none | Log warning + no-op |
| `setDecorFitsSystemWindows` | 1 | none | Log warning + no-op |
| `setDefaultWindowFormat` | 1 | none | Log warning + no-op |
| `setDimAmount` | 1 | none | Log warning + no-op |
| `setElevation` | 1 | none | Log warning + no-op |
| `setEnterTransition` | 1 | none | Log warning + no-op |
| `setExitTransition` | 1 | none | Log warning + no-op |
| `setFeatureDrawable` | 1 | none | Log warning + no-op |
| `setFeatureDrawableAlpha` | 1 | none | Log warning + no-op |
| `setFeatureDrawableResource` | 1 | none | Log warning + no-op |
| `setFeatureDrawableUri` | 1 | none | Log warning + no-op |
| `setFeatureInt` | 1 | none | Log warning + no-op |
| `setFlags` | 1 | none | Log warning + no-op |
| `setFormat` | 1 | none | Log warning + no-op |
| `setGravity` | 1 | none | Log warning + no-op |
| `setIcon` | 1 | none | Log warning + no-op |
| `setLocalFocus` | 1 | none | Log warning + no-op |
| `setLogo` | 1 | none | Log warning + no-op |
| `setMediaController` | 1 | none | Log warning + no-op |
| `setNavigationBarContrastEnforced` | 1 | none | Log warning + no-op |
| `setNavigationBarDividerColor` | 1 | none | Log warning + no-op |
| `setPreferMinimalPostProcessing` | 1 | none | Log warning + no-op |
| `setReenterTransition` | 1 | none | Log warning + no-op |
| `setResizingCaptionDrawable` | 1 | none | Log warning + no-op |
| `setRestrictedCaptionAreaListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setReturnTransition` | 1 | none | Log warning + no-op |
| `setSharedElementEnterTransition` | 1 | none | Log warning + no-op |
| `setSharedElementExitTransition` | 1 | none | Log warning + no-op |
| `setSharedElementReenterTransition` | 1 | none | Log warning + no-op |
| `setSharedElementReturnTransition` | 1 | none | Log warning + no-op |
| `setSharedElementsUseOverlay` | 1 | none | Log warning + no-op |
| `setSoftInputMode` | 1 | none | Log warning + no-op |
| `setStatusBarContrastEnforced` | 1 | none | Log warning + no-op |
| `setSustainedPerformanceMode` | 1 | none | Log warning + no-op |
| `setSystemGestureExclusionRects` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |
| `setTransitionBackgroundFadeDuration` | 1 | none | Log warning + no-op |
| `setTransitionManager` | 1 | none | Log warning + no-op |
| `setType` | 1 | none | Log warning + no-op |
| `setUiOptions` | 1 | none | Log warning + no-op |
| `setUiOptions` | 1 | none | Log warning + no-op |
| `setVolumeControlStream` | 1 | none | Log warning + no-op |
| `setWindowAnimations` | 1 | none | Log warning + no-op |
| `setWindowManager` | 1 | none | Log warning + no-op |
| `setWindowManager` | 1 | none | Log warning + no-op |
| `superDispatchGenericMotionEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `superDispatchKeyEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `superDispatchKeyShortcutEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `superDispatchTouchEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `superDispatchTrackballEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `takeInputQueue` | 1 | none | Log warning + no-op |
| `takeKeyEvents` | 1 | none | throw UnsupportedOperationException |
| `takeSurface` | 1 | none | throw UnsupportedOperationException |
| `togglePanel` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.Window`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.Window` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 129 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
