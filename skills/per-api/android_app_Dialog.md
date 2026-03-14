# SKILL: android.app.Dialog

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.Dialog`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.Dialog` |
| **Package** | `android.app` |
| **Total Methods** | 80 |
| **Avg Score** | 2.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 1 (1%) |
| **Partial/Composite** | 31 (38%) |
| **No Mapping** | 48 (60%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 1 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `show` | `void show()` | 7 | near | hard | `showDialog` | `@ohos.promptAction.` |
| `onCreate` | `void onCreate(android.os.Bundle)` | 5 | partial | moderate | `onPrepare` | `onPrepare(): void` |

## Gap Descriptions (per method)

- **`show`**: promptAction.showDialog()

## Stub APIs (score < 5): 78 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `onBackPressed` | 5 | partial | Store callback, never fire |
| `onPreparePanel` | 5 | partial | Store callback, never fire |
| `openContextMenu` | 4 | partial | Return dummy instance / no-op |
| `onCreateContextMenu` | 4 | partial | Return dummy instance / no-op |
| `onPrepareOptionsMenu` | 4 | partial | Store callback, never fire |
| `cancel` | 4 | partial | Return safe default (null/false/0/empty) |
| `onCreatePanelMenu` | 4 | partial | Return dummy instance / no-op |
| `onCreatePanelView` | 4 | partial | Return dummy instance / no-op |
| `getVolumeControlStream` | 4 | partial | Return safe default (null/false/0/empty) |
| `onWindowFocusChanged` | 3 | composite | Store callback, never fire |
| `unregisterForContextMenu` | 3 | composite | Return safe default (null/false/0/empty) |
| `setContentView` | 3 | composite | Log warning + no-op |
| `setContentView` | 3 | composite | Log warning + no-op |
| `setContentView` | 3 | composite | Log warning + no-op |
| `onDetachedFromWindow` | 3 | composite | Store callback, never fire |
| `setCancelMessage` | 3 | composite | Return safe default (null/false/0/empty) |
| `onStart` | 3 | composite | Return dummy instance / no-op |
| `onAttachedToWindow` | 3 | composite | Store callback, never fire |
| `setFeatureDrawableUri` | 3 | composite | Log warning + no-op |
| `setVolumeControlStream` | 3 | composite | Log warning + no-op |
| `setOnDismissListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `Dialog` | 3 | composite | throw UnsupportedOperationException |
| `Dialog` | 3 | composite | throw UnsupportedOperationException |
| `Dialog` | 3 | composite | throw UnsupportedOperationException |
| `create` | 3 | composite | Return dummy instance / no-op |
| `setOnKeyListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnShowListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `requestWindowFeature` | 3 | composite | throw UnsupportedOperationException |
| `registerForContextMenu` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnCancelListener` | 2 | composite | Return safe default (null/false/0/empty) |
| `addContentView` | 1 | none | Log warning + no-op |
| `closeOptionsMenu` | 1 | none | No-op |
| `dismiss` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchGenericMotionEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchKeyEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchKeyShortcutEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchPopulateAccessibilityEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchTouchEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatchTrackballEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `findViewById` | 1 | none | Return safe default (null/false/0/empty) |
| `hide` | 1 | none | throw UnsupportedOperationException |
| `invalidateOptionsMenu` | 1 | none | Store callback, never fire |
| `isShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `onContentChanged` | 1 | none | Store callback, never fire |
| `onContextItemSelected` | 1 | none | Store callback, never fire |
| `onContextMenuClosed` | 1 | none | No-op |
| `onCreateOptionsMenu` | 1 | none | Return dummy instance / no-op |
| `onGenericMotionEvent` | 1 | none | Store callback, never fire |
| `onKeyDown` | 1 | none | Store callback, never fire |
| `onKeyLongPress` | 1 | none | Store callback, never fire |
| `onKeyMultiple` | 1 | none | Store callback, never fire |
| `onKeyShortcut` | 1 | none | Store callback, never fire |
| `onKeyUp` | 1 | none | Store callback, never fire |
| `onMenuItemSelected` | 1 | none | Store callback, never fire |
| `onMenuOpened` | 1 | none | Return dummy instance / no-op |
| `onOptionsItemSelected` | 1 | none | Store callback, never fire |
| `onOptionsMenuClosed` | 1 | none | No-op |
| `onPanelClosed` | 1 | none | No-op |
| `onRestoreInstanceState` | 1 | none | Store callback, never fire |
| `onSearchRequested` | 1 | none | Store callback, never fire |
| `onSearchRequested` | 1 | none | Store callback, never fire |
| `onStop` | 1 | none | No-op |
| `onTouchEvent` | 1 | none | Store callback, never fire |
| `onTrackballEvent` | 1 | none | Store callback, never fire |
| `onWindowAttributesChanged` | 1 | none | Store callback, never fire |
| `onWindowStartingActionMode` | 1 | none | Return dummy instance / no-op |
| `onWindowStartingActionMode` | 1 | none | Return dummy instance / no-op |
| `openOptionsMenu` | 1 | none | Return dummy instance / no-op |
| `setCancelable` | 1 | none | Return safe default (null/false/0/empty) |
| `setCanceledOnTouchOutside` | 1 | none | Return safe default (null/false/0/empty) |
| `setDismissMessage` | 1 | none | Return safe default (null/false/0/empty) |
| `setFeatureDrawable` | 1 | none | Log warning + no-op |
| `setFeatureDrawableAlpha` | 1 | none | Log warning + no-op |
| `setFeatureDrawableResource` | 1 | none | Log warning + no-op |
| `setOwnerActivity` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |
| `setTitle` | 1 | none | Log warning + no-op |
| `takeKeyEvents` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.app.Dialog`:

- `android.content.Context` (already shimmed)
- `android.view.View` (already shimmed)

## Quality Gates

Before marking `android.app.Dialog` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 80 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
