# SKILL: android.view.KeyCharacterMap

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.KeyCharacterMap`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.KeyCharacterMap` |
| **Package** | `android.view` |
| **Total Methods** | 15 |
| **Avg Score** | 1.6 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 5 (33%) |
| **No Mapping** | 10 (66%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 15 |
| **Has Async Gap** | 15 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 15 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `get` | 3 | composite | Return safe default (null/false/0/empty) |
| `getEvents` | 3 | composite | Return safe default (null/false/0/empty) |
| `getKeyboardType` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMatch` | 2 | composite | Return safe default (null/false/0/empty) |
| `getMatch` | 2 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |
| `deviceHasKey` | 1 | none | Return safe default (null/false/0/empty) |
| `deviceHasKeys` | 1 | none | Return safe default (null/false/0/empty) |
| `getDeadChar` | 1 | none | Return safe default (null/false/0/empty) |
| `getDisplayLabel` | 1 | none | Return safe default (null/false/0/empty) |
| `getModifierBehavior` | 1 | none | Return safe default (null/false/0/empty) |
| `getNumber` | 1 | none | Return safe default (null/false/0/empty) |
| `isPrintingKey` | 1 | none | Return safe default (null/false/0/empty) |
| `load` | 1 | none | throw UnsupportedOperationException |
| `writeToParcel` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.KeyCharacterMap`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.KeyCharacterMap` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 15 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
