# SKILL: android.view.KeyEvent

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.KeyEvent`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.KeyEvent` |
| **Package** | `android.view` |
| **Total Methods** | 60 |
| **Avg Score** | 2.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 8 (13%) |
| **Partial/Composite** | 5 (8%) |
| **No Mapping** | 47 (78%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 52 |
| **Has Async Gap** | 44 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Implementable APIs (score >= 5): 8 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getDeviceId` | `final int getDeviceId()` | 9 | direct | hard | `deviceId` | `@ohos.multimodalInput.inputEvent.InputEvent` |
| `getUnicodeChar` | `int getUnicodeChar()` | 9 | direct | impossible | `unicodeChar` | `@ohos.multimodalInput.keyEvent.KeyEvent` |
| `getUnicodeChar` | `int getUnicodeChar(int)` | 9 | direct | impossible | `unicodeChar` | `@ohos.multimodalInput.keyEvent.KeyEvent` |
| `isAltPressed` | `final boolean isAltPressed()` | 9 | direct | impossible | `altKey` | `@ohos.multimodalInput.keyEvent.KeyEvent` |
| `isCtrlPressed` | `final boolean isCtrlPressed()` | 9 | direct | impossible | `ctrlKey` | `@ohos.multimodalInput.keyEvent.KeyEvent` |
| `isShiftPressed` | `final boolean isShiftPressed()` | 9 | direct | impossible | `shiftKey` | `@ohos.multimodalInput.keyEvent.KeyEvent` |
| `getAction` | `final int getAction()` | 7 | near | rewrite | `action` | `@ohos.multimodalInput.keyEvent.KeyEvent` |
| `getKeyCode` | `final int getKeyCode()` | 7 | near | impossible | `key.code` | `@ohos.multimodalInput.keyEvent.KeyEvent` |

## Gap Descriptions (per method)

- **`getDeviceId`**: Direct equivalent
- **`getUnicodeChar`**: Direct equivalent
- **`getUnicodeChar`**: Direct equivalent
- **`isAltPressed`**: Direct boolean
- **`isCtrlPressed`**: Direct boolean
- **`isShiftPressed`**: Direct boolean
- **`getAction`**: Action enum differs
- **`getKeyCode`**: Different enum values

## Stub APIs (score < 5): 52 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `changeAction` | 3 | composite | Store callback, never fire |
| `getFlags` | 2 | composite | Return safe default (null/false/0/empty) |
| `getMatch` | 2 | composite | Return safe default (null/false/0/empty) |
| `getMatch` | 2 | composite | Return safe default (null/false/0/empty) |
| `getScanCode` | 2 | composite | Return safe default (null/false/0/empty) |
| `KeyEvent` | 1 | none | throw UnsupportedOperationException |
| `KeyEvent` | 1 | none | throw UnsupportedOperationException |
| `KeyEvent` | 1 | none | throw UnsupportedOperationException |
| `KeyEvent` | 1 | none | throw UnsupportedOperationException |
| `KeyEvent` | 1 | none | throw UnsupportedOperationException |
| `KeyEvent` | 1 | none | throw UnsupportedOperationException |
| `KeyEvent` | 1 | none | throw UnsupportedOperationException |
| `KeyEvent` | 1 | none | throw UnsupportedOperationException |
| `changeFlags` | 1 | none | throw UnsupportedOperationException |
| `changeTimeRepeat` | 1 | none | Return safe default (null/false/0/empty) |
| `changeTimeRepeat` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatch` | 1 | none | Return safe default (null/false/0/empty) |
| `getDeadChar` | 1 | none | Return safe default (null/false/0/empty) |
| `getDisplayLabel` | 1 | none | Return safe default (null/false/0/empty) |
| `getDownTime` | 1 | none | Return safe default (null/false/0/empty) |
| `getEventTime` | 1 | none | Return safe default (null/false/0/empty) |
| `getKeyCharacterMap` | 1 | none | Return safe default (null/false/0/empty) |
| `getMaxKeyCode` | 1 | none | Return safe default (null/false/0/empty) |
| `getMetaState` | 1 | none | Return safe default (null/false/0/empty) |
| `getModifierMetaStateMask` | 1 | none | Return safe default (null/false/0/empty) |
| `getModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `getNumber` | 1 | none | Return safe default (null/false/0/empty) |
| `getRepeatCount` | 1 | none | Return safe default (null/false/0/empty) |
| `getSource` | 1 | none | Return safe default (null/false/0/empty) |
| `hasModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `hasNoModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `isCanceled` | 1 | none | Return safe default (null/false/0/empty) |
| `isCapsLockOn` | 1 | none | Return safe default (null/false/0/empty) |
| `isFunctionPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `isGamepadButton` | 1 | none | Return safe default (null/false/0/empty) |
| `isLongPress` | 1 | none | Return safe default (null/false/0/empty) |
| `isMetaPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `isModifierKey` | 1 | none | Return safe default (null/false/0/empty) |
| `isNumLockOn` | 1 | none | Return safe default (null/false/0/empty) |
| `isPrintingKey` | 1 | none | Return safe default (null/false/0/empty) |
| `isScrollLockOn` | 1 | none | Return safe default (null/false/0/empty) |
| `isSymPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `isSystem` | 1 | none | Return safe default (null/false/0/empty) |
| `isTracking` | 1 | none | Return safe default (null/false/0/empty) |
| `keyCodeFromString` | 1 | none | throw UnsupportedOperationException |
| `keyCodeToString` | 1 | none | throw UnsupportedOperationException |
| `metaStateHasModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `metaStateHasNoModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `normalizeMetaState` | 1 | none | throw UnsupportedOperationException |
| `setSource` | 1 | none | Log warning + no-op |
| `startTracking` | 1 | none | Return dummy instance / no-op |
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

Check if these related classes are already shimmed before generating `android.view.KeyEvent`:


## Quality Gates

Before marking `android.view.KeyEvent` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 60 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 8 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
