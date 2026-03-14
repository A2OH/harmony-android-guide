# SKILL: android.text.Layout

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.text.Layout`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.text.Layout` |
| **Package** | `android.text` |
| **Total Methods** | 49 |
| **Avg Score** | 1.4 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 9 (18%) |
| **No Mapping** | 40 (81%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 49 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getParagraphDirection` | 4 | partial | Return safe default (null/false/0/empty) |
| `getParagraphLeft` | 4 | partial | Return safe default (null/false/0/empty) |
| `getParagraphRight` | 4 | composite | Return safe default (null/false/0/empty) |
| `getParagraphAlignment` | 4 | composite | Return safe default (null/false/0/empty) |
| `getText` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPaint` | 2 | composite | Return safe default (null/false/0/empty) |
| `getWidth` | 2 | composite | Return safe default (null/false/0/empty) |
| `getLineStart` | 2 | composite | Return dummy instance / no-op |
| `getLineAscent` | 2 | composite | Return safe default (null/false/0/empty) |
| `Layout` | 1 | none | throw UnsupportedOperationException |
| `draw` | 1 | none | throw UnsupportedOperationException |
| `draw` | 1 | none | throw UnsupportedOperationException |
| `getAlignment` | 1 | none | Return safe default (null/false/0/empty) |
| `getBottomPadding` | 1 | none | Return safe default (null/false/0/empty) |
| `getCursorPath` | 1 | none | Return safe default (null/false/0/empty) |
| `getDesiredWidth` | 1 | none | Return safe default (null/false/0/empty) |
| `getDesiredWidth` | 1 | none | Return safe default (null/false/0/empty) |
| `getEllipsisCount` | 1 | none | Return safe default (null/false/0/empty) |
| `getEllipsisStart` | 1 | none | Return dummy instance / no-op |
| `getEllipsizedWidth` | 1 | none | Return safe default (null/false/0/empty) |
| `getHeight` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineBaseline` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineBottom` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineBounds` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineContainsTab` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineCount` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineDescent` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineDirections` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineEnd` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineForOffset` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineForVertical` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineLeft` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineMax` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineRight` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineTop` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineVisibleEnd` | 1 | none | Return safe default (null/false/0/empty) |
| `getLineWidth` | 1 | none | Return safe default (null/false/0/empty) |
| `getOffsetForHorizontal` | 1 | none | Return safe default (null/false/0/empty) |
| `getOffsetToLeftOf` | 1 | none | Return safe default (null/false/0/empty) |
| `getOffsetToRightOf` | 1 | none | Return safe default (null/false/0/empty) |
| `getPrimaryHorizontal` | 1 | none | Return safe default (null/false/0/empty) |
| `getSecondaryHorizontal` | 1 | none | Return safe default (null/false/0/empty) |
| `getSelectionPath` | 1 | none | Return safe default (null/false/0/empty) |
| `getSpacingAdd` | 1 | none | Return safe default (null/false/0/empty) |
| `getSpacingMultiplier` | 1 | none | Return safe default (null/false/0/empty) |
| `getTopPadding` | 1 | none | Return safe default (null/false/0/empty) |
| `increaseWidthTo` | 1 | none | throw UnsupportedOperationException |
| `isRtlCharAt` | 1 | none | Return safe default (null/false/0/empty) |
| `isSpanned` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.text.Layout`:


## Quality Gates

Before marking `android.text.Layout` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 49 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
