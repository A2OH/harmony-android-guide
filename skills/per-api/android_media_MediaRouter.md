# SKILL: android.media.MediaRouter

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter` |
| **Package** | `android.media` |
| **Total Methods** | 26 |
| **Avg Score** | 2.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 14 (53%) |
| **No Mapping** | 12 (46%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `addCallback` | `void addCallback(int, android.media.MediaRouter.Callback)` | 6 | partial | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |
| `addCallback` | `void addCallback(int, android.media.MediaRouter.Callback, int)` | 6 | partial | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |
| `getRouteCount` | `int getRouteCount()` | 6 | partial | moderate | `getCount` | `getCount(): number` |
| `Callback` | `MediaRouter.Callback()` | 5 | partial | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |
| `removeCallback` | `void removeCallback(android.media.MediaRouter.Callback)` | 5 | partial | moderate | `callback` | `callback: AsyncCallback<boolean>): void` |
| `getCategoryCount` | `int getCategoryCount()` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Stub APIs (score < 5): 20 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getRouteAt` | 5 | partial | Return safe default (null/false/0/empty) |
| `onRouteAdded` | 5 | partial | Log warning + no-op |
| `getSelectedRoute` | 4 | partial | Return safe default (null/false/0/empty) |
| `getDefaultRoute` | 4 | partial | Return safe default (null/false/0/empty) |
| `createUserRoute` | 3 | composite | Return dummy instance / no-op |
| `createRouteCategory` | 3 | composite | Return dummy instance / no-op |
| `createRouteCategory` | 3 | composite | Return dummy instance / no-op |
| `getCategoryAt` | 2 | composite | Return safe default (null/false/0/empty) |
| `addUserRoute` | 1 | none | Log warning + no-op |
| `clearUserRoutes` | 1 | none | throw UnsupportedOperationException |
| `removeUserRoute` | 1 | none | Log warning + no-op |
| `selectRoute` | 1 | none | throw UnsupportedOperationException |
| `onRouteChanged` | 1 | none | Store callback, never fire |
| `onRouteGrouped` | 1 | none | Store callback, never fire |
| `onRoutePresentationDisplayChanged` | 1 | none | Return safe default (null/false/0/empty) |
| `onRouteRemoved` | 1 | none | Log warning + no-op |
| `onRouteSelected` | 1 | none | Store callback, never fire |
| `onRouteUngrouped` | 1 | none | Store callback, never fire |
| `onRouteUnselected` | 1 | none | Store callback, never fire |
| `onRouteVolumeChanged` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 6 methods that have score >= 5
2. Stub 20 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaRouter` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 26 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
