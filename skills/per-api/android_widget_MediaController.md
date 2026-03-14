# SKILL: android.widget.MediaController

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.MediaController`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.MediaController` |
| **Package** | `android.widget` |
| **Total Methods** | 11 |
| **Avg Score** | 1.4 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (18%) |
| **No Mapping** | 9 (81%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 8 |
| **Has Async Gap** | 8 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 11 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `show` | 3 | composite | throw UnsupportedOperationException |
| `show` | 3 | composite | throw UnsupportedOperationException |
| `MediaController` | 1 | none | Store callback, never fire |
| `MediaController` | 1 | none | Store callback, never fire |
| `MediaController` | 1 | none | Store callback, never fire |
| `hide` | 1 | none | throw UnsupportedOperationException |
| `isShowing` | 1 | none | Return safe default (null/false/0/empty) |
| `onFinishInflate` | 1 | none | Return safe default (null/false/0/empty) |
| `setAnchorView` | 1 | none | Log warning + no-op |
| `setMediaPlayer` | 1 | none | Log warning + no-op |
| `setPrevNextListeners` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.MediaController`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.MediaController` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
