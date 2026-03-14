# SKILL: android.widget.AbsListView

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.widget.AbsListView`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.widget.AbsListView` |
| **Package** | `android.widget` |
| **Total Methods** | 78 |
| **Avg Score** | 1.1 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 4 (5%) |
| **No Mapping** | 74 (94%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 74 |
| **Has Async Gap** | 74 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 78 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getTranscriptMode` | 3 | composite | Return safe default (null/false/0/empty) |
| `onSaveInstanceState` | 3 | composite | Store callback, never fire |
| `onRestoreInstanceState` | 3 | composite | Store callback, never fire |
| `getListPaddingLeft` | 2 | composite | Return safe default (null/false/0/empty) |
| `AbsListView` | 1 | none | Return safe default (null/false/0/empty) |
| `AbsListView` | 1 | none | Return safe default (null/false/0/empty) |
| `AbsListView` | 1 | none | Return safe default (null/false/0/empty) |
| `AbsListView` | 1 | none | Return safe default (null/false/0/empty) |
| `afterTextChanged` | 1 | none | throw UnsupportedOperationException |
| `beforeTextChanged` | 1 | none | throw UnsupportedOperationException |
| `canScrollList` | 1 | none | Return safe default (null/false/0/empty) |
| `clearChoices` | 1 | none | throw UnsupportedOperationException |
| `clearTextFilter` | 1 | none | throw UnsupportedOperationException |
| `deferNotifyDataSetChanged` | 1 | none | Log warning + no-op |
| `fling` | 1 | none | throw UnsupportedOperationException |
| `generateLayoutParams` | 1 | none | throw UnsupportedOperationException |
| `getCheckedItemCount` | 1 | none | Return safe default (null/false/0/empty) |
| `getCheckedItemIds` | 1 | none | Return safe default (null/false/0/empty) |
| `getCheckedItemPosition` | 1 | none | Return safe default (null/false/0/empty) |
| `getCheckedItemPositions` | 1 | none | Return safe default (null/false/0/empty) |
| `getChoiceMode` | 1 | none | Return safe default (null/false/0/empty) |
| `getListPaddingBottom` | 1 | none | Return safe default (null/false/0/empty) |
| `getListPaddingRight` | 1 | none | Return safe default (null/false/0/empty) |
| `getListPaddingTop` | 1 | none | Return safe default (null/false/0/empty) |
| `getSelector` | 1 | none | Return safe default (null/false/0/empty) |
| `getTextFilter` | 1 | none | Return safe default (null/false/0/empty) |
| `handleDataChanged` | 1 | none | throw UnsupportedOperationException |
| `hasTextFilter` | 1 | none | Return safe default (null/false/0/empty) |
| `invalidateViews` | 1 | none | throw UnsupportedOperationException |
| `isDrawSelectorOnTop` | 1 | none | Return safe default (null/false/0/empty) |
| `isFastScrollAlwaysVisible` | 1 | none | Return safe default (null/false/0/empty) |
| `isInFilterMode` | 1 | none | Return safe default (null/false/0/empty) |
| `isItemChecked` | 1 | none | Return safe default (null/false/0/empty) |
| `layoutChildren` | 1 | none | throw UnsupportedOperationException |
| `onFilterComplete` | 1 | none | Store callback, never fire |
| `onGlobalLayout` | 1 | none | Store callback, never fire |
| `onInitializeAccessibilityNodeInfoForItem` | 1 | none | Return dummy instance / no-op |
| `onRemoteAdapterConnected` | 1 | none | Return dummy instance / no-op |
| `onRemoteAdapterDisconnected` | 1 | none | Return dummy instance / no-op |
| `onTextChanged` | 1 | none | Store callback, never fire |
| `onTouchModeChanged` | 1 | none | Store callback, never fire |
| `pointToPosition` | 1 | none | Store callback, never fire |
| `pointToRowId` | 1 | none | throw UnsupportedOperationException |
| `reclaimViews` | 1 | none | throw UnsupportedOperationException |
| `scrollListBy` | 1 | none | Return safe default (null/false/0/empty) |
| `setAdapter` | 1 | none | Log warning + no-op |
| `setBottomEdgeEffectColor` | 1 | none | Log warning + no-op |
| `setCacheColorHint` | 1 | none | Log warning + no-op |
| `setChoiceMode` | 1 | none | Log warning + no-op |
| `setDrawSelectorOnTop` | 1 | none | Log warning + no-op |
| `setEdgeEffectColor` | 1 | none | Log warning + no-op |
| `setFastScrollAlwaysVisible` | 1 | none | Return safe default (null/false/0/empty) |
| `setFastScrollEnabled` | 1 | none | Log warning + no-op |
| `setFastScrollStyle` | 1 | none | Log warning + no-op |
| `setFilterText` | 1 | none | Log warning + no-op |
| `setFriction` | 1 | none | Log warning + no-op |
| `setItemChecked` | 1 | none | Log warning + no-op |
| `setMultiChoiceModeListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnScrollListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setRecyclerListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setRemoteViewsAdapter` | 1 | none | Log warning + no-op |
| `setScrollIndicators` | 1 | none | Log warning + no-op |
| `setScrollingCacheEnabled` | 1 | none | Log warning + no-op |
| `setSelectionFromTop` | 1 | none | Log warning + no-op |
| `setSelector` | 1 | none | Log warning + no-op |
| `setSelector` | 1 | none | Log warning + no-op |
| `setSmoothScrollbarEnabled` | 1 | none | Log warning + no-op |
| `setStackFromBottom` | 1 | none | Log warning + no-op |
| `setTextFilterEnabled` | 1 | none | Log warning + no-op |
| `setTopEdgeEffectColor` | 1 | none | Log warning + no-op |
| `setTranscriptMode` | 1 | none | Log warning + no-op |
| `setVelocityScale` | 1 | none | Log warning + no-op |
| `smoothScrollBy` | 1 | none | throw UnsupportedOperationException |
| `smoothScrollToPosition` | 1 | none | Store callback, never fire |
| `smoothScrollToPosition` | 1 | none | Store callback, never fire |
| `smoothScrollToPositionFromTop` | 1 | none | Store callback, never fire |
| `smoothScrollToPositionFromTop` | 1 | none | Store callback, never fire |
| `verifyDrawable` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.widget.AbsListView`:

- `android.view.View` (already shimmed)
- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.widget.AbsListView` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 78 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
