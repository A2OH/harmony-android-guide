# SKILL: android.view.Menu

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.Menu`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.Menu` |
| **Package** | `android.view` |
| **Total Methods** | 25 |
| **Avg Score** | 2.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 9 (36%) |
| **No Mapping** | 16 (64%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 25 |
| **Has Async Gap** | 25 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `add` | `android.view.MenuItem add(CharSequence)` | 5 | partial | rewrite | `.bindMenu` | `@internal/component/ets/common.CommonMethod` |
| `add` | `android.view.MenuItem add(@StringRes int)` | 5 | partial | rewrite | `.bindMenu` | `@internal/component/ets/common.CommonMethod` |
| `add` | `android.view.MenuItem add(int, int, int, CharSequence)` | 5 | partial | rewrite | `.bindMenu` | `@internal/component/ets/common.CommonMethod` |
| `add` | `android.view.MenuItem add(int, int, int, @StringRes int)` | 5 | partial | rewrite | `.bindMenu` | `@internal/component/ets/common.CommonMethod` |

## Gap Descriptions (per method)

- **`add`**: MenuElement array
- **`add`**: MenuElement array
- **`add`**: MenuElement array
- **`add`**: MenuElement array

## Stub APIs (score < 5): 21 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `clear` | 3 | composite | throw UnsupportedOperationException |
| `close` | 3 | composite | No-op |
| `removeGroup` | 3 | composite | Log warning + no-op |
| `size` | 3 | composite | throw UnsupportedOperationException |
| `getItem` | 3 | composite | Return safe default (null/false/0/empty) |
| `addIntentOptions` | 1 | none | Log warning + no-op |
| `addSubMenu` | 1 | none | Log warning + no-op |
| `addSubMenu` | 1 | none | Log warning + no-op |
| `addSubMenu` | 1 | none | Log warning + no-op |
| `addSubMenu` | 1 | none | Log warning + no-op |
| `findItem` | 1 | none | Return safe default (null/false/0/empty) |
| `hasVisibleItems` | 1 | none | Return safe default (null/false/0/empty) |
| `isShortcutKey` | 1 | none | Return safe default (null/false/0/empty) |
| `performIdentifierAction` | 1 | none | Store callback, never fire |
| `performShortcut` | 1 | none | throw UnsupportedOperationException |
| `removeItem` | 1 | none | Log warning + no-op |
| `setGroupCheckable` | 1 | none | Log warning + no-op |
| `setGroupDividerEnabled` | 1 | none | Log warning + no-op |
| `setGroupEnabled` | 1 | none | Log warning + no-op |
| `setGroupVisible` | 1 | none | Return safe default (null/false/0/empty) |
| `setQwertyMode` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.Menu`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.Menu` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 25 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
