# SKILL: android.widget.NumberPicker

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.NumberPicker`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.NumberPicker` |
| **Package** | `android.widget` |
| **Total Methods** | 21 |
| **Avg Score** | 1.3 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (14%) |
| **No Mapping** | 18 (85%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 17 |
| **Has Async Gap** | 17 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 21 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getWrapSelectorWheel` | 3 | composite | Return safe default (null/false/0/empty) |
| `setValue` | 3 | composite | Log warning + no-op |
| `getValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `NumberPicker` | 1 | none | throw UnsupportedOperationException |
| `NumberPicker` | 1 | none | throw UnsupportedOperationException |
| `NumberPicker` | 1 | none | throw UnsupportedOperationException |
| `NumberPicker` | 1 | none | throw UnsupportedOperationException |
| `getDisplayedValues` | 1 | none | Return safe default (null/false/0/empty) |
| `getMaxValue` | 1 | none | Return safe default (null/false/0/empty) |
| `getMinValue` | 1 | none | Return safe default (null/false/0/empty) |
| `setDisplayedValues` | 1 | none | Return safe default (null/false/0/empty) |
| `setFormatter` | 1 | none | Log warning + no-op |
| `setMaxValue` | 1 | none | Log warning + no-op |
| `setMinValue` | 1 | none | Log warning + no-op |
| `setOnLongPressUpdateInterval` | 1 | none | Log warning + no-op |
| `setOnScrollListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnValueChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setSelectionDividerHeight` | 1 | none | Log warning + no-op |
| `setTextColor` | 1 | none | Log warning + no-op |
| `setTextSize` | 1 | none | Log warning + no-op |
| `setWrapSelectorWheel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.NumberPicker`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.NumberPicker` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
