# SKILL: android.view.Display

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.Display`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.Display` |
| **Package** | `android.view` |
| **Total Methods** | 18 |
| **Avg Score** | 2.0 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 11 (61%) |
| **No Mapping** | 7 (38%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 18 |
| **Has Async Gap** | 18 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 18 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getHdrCapabilities` | 3 | composite | Return safe default (null/false/0/empty) |
| `getState` | 3 | composite | Return safe default (null/false/0/empty) |
| `isValid` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSupportedModes` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getName` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRealMetrics` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRealSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getCurrentSizeRange` | 2 | composite | Return safe default (null/false/0/empty) |
| `getFlags` | 2 | composite | Return safe default (null/false/0/empty) |
| `getRotation` | 2 | composite | Return safe default (null/false/0/empty) |
| `getAppVsyncOffsetNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `getDisplayId` | 1 | none | Return safe default (null/false/0/empty) |
| `getPresentationDeadlineNanos` | 1 | none | Return safe default (null/false/0/empty) |
| `getRefreshRate` | 1 | none | Return safe default (null/false/0/empty) |
| `isHdr` | 1 | none | Return safe default (null/false/0/empty) |
| `isMinimalPostProcessingSupported` | 1 | none | Return safe default (null/false/0/empty) |
| `isWideColorGamut` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.Display`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.view.Display` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 18 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
