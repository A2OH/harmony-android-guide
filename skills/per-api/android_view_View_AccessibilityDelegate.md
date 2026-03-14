# SKILL: android.view.View.AccessibilityDelegate

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.View.AccessibilityDelegate`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.View.AccessibilityDelegate` |
| **Package** | `android.view.View` |
| **Total Methods** | 11 |
| **Avg Score** | 1.2 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (9%) |
| **No Mapping** | 10 (90%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 10 |
| **Has Async Gap** | 10 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `sendAccessibilityEvent` | 3 | composite | throw UnsupportedOperationException |
| `AccessibilityDelegate` | 1 | none | throw UnsupportedOperationException |
| `addExtraDataToAccessibilityNodeInfo` | 1 | none | Log warning + no-op |
| `dispatchPopulateAccessibilityEvent` | 1 | none | Return safe default (null/false/0/empty) |
| `getAccessibilityNodeProvider` | 1 | none | Return safe default (null/false/0/empty) |
| `onInitializeAccessibilityEvent` | 1 | none | Return dummy instance / no-op |
| `onInitializeAccessibilityNodeInfo` | 1 | none | Return dummy instance / no-op |
| `onPopulateAccessibilityEvent` | 1 | none | Store callback, never fire |
| `onRequestSendAccessibilityEvent` | 1 | none | Store callback, never fire |
| `performAccessibilityAction` | 1 | none | Store callback, never fire |
| `sendAccessibilityEventUnchecked` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.View.AccessibilityDelegate`:


## Quality Gates

Before marking `android.view.View.AccessibilityDelegate` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
