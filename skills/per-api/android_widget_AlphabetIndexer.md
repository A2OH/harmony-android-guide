# SKILL: android.widget.AlphabetIndexer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.AlphabetIndexer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.AlphabetIndexer` |
| **Package** | `android.widget` |
| **Total Methods** | 6 |
| **Avg Score** | 1.8 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 3 (50%) |
| **No Mapping** | 3 (50%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 5 |
| **Has Async Gap** | 5 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 6 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setCursor` | 3 | composite | Log warning + no-op |
| `compare` | 3 | composite | throw UnsupportedOperationException |
| `getSections` | 2 | composite | Return safe default (null/false/0/empty) |
| `AlphabetIndexer` | 1 | none | throw UnsupportedOperationException |
| `getPositionForSection` | 1 | none | Return safe default (null/false/0/empty) |
| `getSectionForPosition` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.AlphabetIndexer`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.AlphabetIndexer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 6 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
