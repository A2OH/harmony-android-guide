# SKILL: android.widget.TextClock

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.TextClock`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.TextClock` |
| **Package** | `android.widget` |
| **Total Methods** | 10 |
| **Avg Score** | 1.4 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (20%) |
| **No Mapping** | 8 (80%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 6 |
| **Has Async Gap** | 6 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 10 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setTimeZone` | 3 | composite | Log warning + no-op |
| `getTimeZone` | 3 | composite | Return safe default (null/false/0/empty) |
| `TextClock` | 1 | none | throw UnsupportedOperationException |
| `TextClock` | 1 | none | throw UnsupportedOperationException |
| `TextClock` | 1 | none | throw UnsupportedOperationException |
| `TextClock` | 1 | none | throw UnsupportedOperationException |
| `is24HourModeEnabled` | 1 | none | Return safe default (null/false/0/empty) |
| `refreshTime` | 1 | none | throw UnsupportedOperationException |
| `setFormat12Hour` | 1 | none | Log warning + no-op |
| `setFormat24Hour` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.TextClock`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.TextClock` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
