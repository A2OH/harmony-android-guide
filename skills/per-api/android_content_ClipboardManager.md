# SKILL: android.content.ClipboardManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.content.ClipboardManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.content.ClipboardManager` |
| **Package** | `android.content` |
| **Total Methods** | 5 |
| **Avg Score** | 5.8 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (60%) |
| **Partial/Composite** | 0 (0%) |
| **No Mapping** | 2 (40%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-LIFECYCLE.md / A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `clearPrimaryClip` | `void clearPrimaryClip()` | 9 | direct | impossible | `clearDataSync` | `@ohos.pasteboard.SystemPasteboard` |
| `hasPrimaryClip` | `boolean hasPrimaryClip()` | 9 | direct | hard | `hasData` | `createHtmlData(htmlText: string): PasteData` |
| `setPrimaryClip` | `void setPrimaryClip(@NonNull android.content.ClipData)` | 9 | direct | hard | `setData` | `createHtmlData(htmlText: string): PasteData` |

## Gap Descriptions (per method)

- **`clearPrimaryClip`**: Direct
- **`hasPrimaryClip`**: Has clipboard
- **`setPrimaryClip`**: Set clipboard

## Stub APIs (score < 5): 2 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `addPrimaryClipChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `removePrimaryClipChangedListener` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 3 methods that have score >= 5
2. Stub 2 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.content.ClipboardManager`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.content.ClipboardManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 5 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
