# SKILL: android.view.ViewStructure

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.ViewStructure`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.ViewStructure` |
| **Package** | `android.view` |
| **Total Methods** | 60 |
| **Avg Score** | 1.2 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (10%) |
| **No Mapping** | 54 (90%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 57 |
| **Has Async Gap** | 57 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 60 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setId` | 3 | composite | Log warning + no-op |
| `Builder` | 3 | composite | throw UnsupportedOperationException |
| `getHint` | 3 | composite | Return safe default (null/false/0/empty) |
| `getText` | 3 | composite | Return safe default (null/false/0/empty) |
| `newHtmlInfoBuilder` | 2 | composite | throw UnsupportedOperationException |
| `asyncCommit` | 2 | composite | throw UnsupportedOperationException |
| `ViewStructure` | 1 | none | throw UnsupportedOperationException |
| `HtmlInfo` | 1 | none | throw UnsupportedOperationException |
| `addChildCount` | 1 | none | Log warning + no-op |
| `asyncNewChild` | 1 | none | throw UnsupportedOperationException |
| `getChildCount` | 1 | none | Return safe default (null/false/0/empty) |
| `getExtras` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextSelectionEnd` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextSelectionStart` | 1 | none | Return dummy instance / no-op |
| `hasExtras` | 1 | none | Return safe default (null/false/0/empty) |
| `newChild` | 1 | none | throw UnsupportedOperationException |
| `setAccessibilityFocused` | 1 | none | Log warning + no-op |
| `setActivated` | 1 | none | Log warning + no-op |
| `setAlpha` | 1 | none | Log warning + no-op |
| `setAutofillHints` | 1 | none | Log warning + no-op |
| `setAutofillId` | 1 | none | Log warning + no-op |
| `setAutofillId` | 1 | none | Log warning + no-op |
| `setAutofillOptions` | 1 | none | Log warning + no-op |
| `setAutofillType` | 1 | none | Log warning + no-op |
| `setAutofillValue` | 1 | none | Log warning + no-op |
| `setCheckable` | 1 | none | Log warning + no-op |
| `setChecked` | 1 | none | Log warning + no-op |
| `setChildCount` | 1 | none | Log warning + no-op |
| `setClassName` | 1 | none | Log warning + no-op |
| `setClickable` | 1 | none | Log warning + no-op |
| `setContentDescription` | 1 | none | Log warning + no-op |
| `setContextClickable` | 1 | none | Log warning + no-op |
| `setDataIsSensitive` | 1 | none | Return safe default (null/false/0/empty) |
| `setDimens` | 1 | none | Log warning + no-op |
| `setElevation` | 1 | none | Log warning + no-op |
| `setEnabled` | 1 | none | Log warning + no-op |
| `setFocusable` | 1 | none | Log warning + no-op |
| `setFocused` | 1 | none | Log warning + no-op |
| `setHint` | 1 | none | Log warning + no-op |
| `setHintIdEntry` | 1 | none | Log warning + no-op |
| `setHtmlInfo` | 1 | none | Log warning + no-op |
| `setImportantForAutofill` | 1 | none | Log warning + no-op |
| `setInputType` | 1 | none | Log warning + no-op |
| `setLocaleList` | 1 | none | Return safe default (null/false/0/empty) |
| `setLongClickable` | 1 | none | Log warning + no-op |
| `setMaxTextEms` | 1 | none | Log warning + no-op |
| `setMaxTextLength` | 1 | none | Log warning + no-op |
| `setMinTextEms` | 1 | none | Log warning + no-op |
| `setOpaque` | 1 | none | Log warning + no-op |
| `setSelected` | 1 | none | Log warning + no-op |
| `setText` | 1 | none | Log warning + no-op |
| `setText` | 1 | none | Log warning + no-op |
| `setTextIdEntry` | 1 | none | Log warning + no-op |
| `setTextLines` | 1 | none | Log warning + no-op |
| `setTextStyle` | 1 | none | Log warning + no-op |
| `setTransformation` | 1 | none | Log warning + no-op |
| `setVisibility` | 1 | none | Return safe default (null/false/0/empty) |
| `setWebDomain` | 1 | none | Log warning + no-op |
| `addAttribute` | 1 | none | Log warning + no-op |
| `build` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.ViewStructure`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.ViewStructure` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 60 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
