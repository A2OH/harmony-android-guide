# SKILL: android.view.Window.Callback

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.Window.Callback`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.Window.Callback` |
| **Package** | `android.view.Window` |
| **Total Methods** | 22 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (4%) |
| **No Mapping** | 21 (95%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 22 |
| **Has Async Gap** | 22 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 22 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `dispatchTouchEvent` | 2 | composite | Return safe default (null/false/0/empty) |
| `dispatchGenericMotionEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchKeyEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchKeyShortcutEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchPopulateAccessibilityEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchTrackballEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `onActionModeFinished` | 1 | none | Return safe default (null/false/0/empty) |
| `onActionModeStarted` | 1 | none | Return dummy instance / no-op |
| `onAttachedToWindow` | 1 | none | Store callback, never fire |
| `onContentChanged` | 1 | none | Store callback, never fire |
| `onCreatePanelMenu` | 1 | none | Return dummy instance / no-op |
| `onDetachedFromWindow` | 1 | none | Store callback, never fire |
| `onMenuItemSelected` | 1 | none | Store callback, never fire |
| `onMenuOpened` | 1 | none | Return dummy instance / no-op |
| `onPanelClosed` | 1 | none | No-op |
| `onPointerCaptureChanged` | 1 | none | Store callback, never fire |
| `onPreparePanel` | 1 | none | Store callback, never fire |
| `onProvideKeyboardShortcuts` | 1 | none | Store callback, never fire |
| `onSearchRequested` | 1 | none | Store callback, never fire |
| `onSearchRequested` | 1 | none | Store callback, never fire |
| `onWindowAttributesChanged` | 1 | none | Store callback, never fire |
| `onWindowFocusChanged` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.Window.Callback`:


## Quality Gates

Before marking `android.view.Window.Callback` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 22 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
