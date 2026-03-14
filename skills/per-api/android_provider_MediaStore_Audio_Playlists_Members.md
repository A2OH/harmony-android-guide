# SKILL: android.provider.MediaStore.Audio.Playlists.Members

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.MediaStore.Audio.Playlists.Members`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.MediaStore.Audio.Playlists.Members` |
| **Package** | `android.provider.MediaStore.Audio.Playlists` |
| **Total Methods** | 3 |
| **Avg Score** | 3.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (66%) |
| **No Mapping** | 1 (33%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `moveItem` | `static boolean moveItem(android.content.ContentResolver, long, int, int)` | 5 | partial | moderate | `moveToNext` | `moveToNext(): boolean` |

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getContentUri` | 5 | partial | Return safe default (null/false/0/empty) |
| `Members` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 1 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.MediaStore.Audio.Playlists.Members`:


## Quality Gates

Before marking `android.provider.MediaStore.Audio.Playlists.Members` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
