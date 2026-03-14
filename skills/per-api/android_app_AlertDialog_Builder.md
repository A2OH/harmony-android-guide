# SKILL: android.app.AlertDialog.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.app.AlertDialog.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.app.AlertDialog.Builder` |
| **Package** | `android.app.AlertDialog` |
| **Total Methods** | 37 |
| **Avg Score** | 3.4 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 4 (10%) |
| **Partial/Composite** | 31 (83%) |
| **No Mapping** | 2 (5%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 8 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `setMessage` | `android.app.AlertDialog.Builder setMessage(@StringRes int)` | 7 | near | rewrite | `showDialog` | `@ohos.promptAction.` |
| `setMessage` | `android.app.AlertDialog.Builder setMessage(CharSequence)` | 7 | near | rewrite | `showDialog` | `@ohos.promptAction.` |
| `setTitle` | `android.app.AlertDialog.Builder setTitle(@StringRes int)` | 7 | near | rewrite | `showDialog` | `@ohos.promptAction.` |
| `setTitle` | `android.app.AlertDialog.Builder setTitle(CharSequence)` | 7 | near | rewrite | `showDialog` | `@ohos.promptAction.` |
| `setPositiveButton` | `android.app.AlertDialog.Builder setPositiveButton(@StringRes int, android.content.DialogInterface.OnClickListener)` | 5 | partial | rewrite | `showDialog` | `@ohos.promptAction.` |
| `setPositiveButton` | `android.app.AlertDialog.Builder setPositiveButton(CharSequence, android.content.DialogInterface.OnClickListener)` | 5 | partial | rewrite | `showDialog` | `@ohos.promptAction.` |
| `setView` | `android.app.AlertDialog.Builder setView(int)` | 5 | partial | rewrite | `—` | `ArkUI.CustomDialogController` |
| `setView` | `android.app.AlertDialog.Builder setView(android.view.View)` | 5 | partial | rewrite | `—` | `ArkUI.CustomDialogController` |

## Gap Descriptions (per method)

- **`setMessage`**: message in ShowDialogOptions
- **`setMessage`**: message in ShowDialogOptions
- **`setTitle`**: title in ShowDialogOptions
- **`setTitle`**: title in ShowDialogOptions
- **`setPositiveButton`**: buttons array, no positive/negative
- **`setPositiveButton`**: buttons array, no positive/negative
- **`setView`**: Custom UI in dialogs
- **`setView`**: Custom UI in dialogs

## Stub APIs (score < 5): 29 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `show` | 4 | partial | throw UnsupportedOperationException |
| `setCursor` | 4 | composite | Log warning + no-op |
| `getContext` | 4 | composite | Return safe default (null/false/0/empty) |
| `setCustomTitle` | 3 | composite | Log warning + no-op |
| `create` | 3 | composite | Return dummy instance / no-op |
| `setOnDismissListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnKeyListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setOnCancelListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setIcon` | 3 | composite | Log warning + no-op |
| `setIcon` | 3 | composite | Log warning + no-op |
| `setAdapter` | 3 | composite | Log warning + no-op |
| `setIconAttribute` | 3 | composite | Log warning + no-op |
| `setOnItemSelectedListener` | 3 | composite | Return safe default (null/false/0/empty) |
| `setNeutralButton` | 3 | composite | Log warning + no-op |
| `setNeutralButton` | 3 | composite | Log warning + no-op |
| `setNegativeButton` | 3 | composite | Log warning + no-op |
| `setNegativeButton` | 3 | composite | Log warning + no-op |
| `setItems` | 3 | composite | Log warning + no-op |
| `setItems` | 3 | composite | Log warning + no-op |
| `setMultiChoiceItems` | 2 | composite | Log warning + no-op |
| `setMultiChoiceItems` | 2 | composite | Log warning + no-op |
| `setMultiChoiceItems` | 2 | composite | Log warning + no-op |
| `setSingleChoiceItems` | 2 | composite | Log warning + no-op |
| `setSingleChoiceItems` | 2 | composite | Log warning + no-op |
| `setSingleChoiceItems` | 2 | composite | Log warning + no-op |
| `setSingleChoiceItems` | 2 | composite | Log warning + no-op |
| `setCancelable` | 2 | composite | Return safe default (null/false/0/empty) |
| `Builder` | 2 | none | throw UnsupportedOperationException |
| `Builder` | 2 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.app.AlertDialog.Builder`:

- `android.content.Context` (already shimmed)
- `android.app.Dialog` (not yet shimmed)

## Quality Gates

Before marking `android.app.AlertDialog.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 37 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
