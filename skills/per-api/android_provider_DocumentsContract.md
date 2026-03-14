# SKILL: android.provider.DocumentsContract

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.DocumentsContract`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.DocumentsContract` |
| **Package** | `android.provider` |
| **Total Methods** | 21 |
| **Avg Score** | 1.8 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 6 (28%) |
| **No Mapping** | 15 (71%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 21 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `deleteDocument` | 5 | partial | throw UnsupportedOperationException |
| `getDocumentId` | 5 | partial | Return safe default (null/false/0/empty) |
| `getSearchDocumentsQuery` | 4 | partial | Return safe default (null/false/0/empty) |
| `getTreeDocumentId` | 4 | partial | Return safe default (null/false/0/empty) |
| `getRootId` | 3 | composite | Return safe default (null/false/0/empty) |
| `isChildDocument` | 2 | composite | Return safe default (null/false/0/empty) |
| `buildChildDocumentsUri` | 1 | none | throw UnsupportedOperationException |
| `buildChildDocumentsUriUsingTree` | 1 | none | throw UnsupportedOperationException |
| `buildDocumentUri` | 1 | none | throw UnsupportedOperationException |
| `buildDocumentUriUsingTree` | 1 | none | throw UnsupportedOperationException |
| `buildRecentDocumentsUri` | 1 | none | throw UnsupportedOperationException |
| `buildRootUri` | 1 | none | throw UnsupportedOperationException |
| `buildRootsUri` | 1 | none | throw UnsupportedOperationException |
| `buildSearchDocumentsUri` | 1 | none | throw UnsupportedOperationException |
| `buildTreeDocumentUri` | 1 | none | throw UnsupportedOperationException |
| `ejectRoot` | 1 | none | throw UnsupportedOperationException |
| `isDocumentUri` | 1 | none | Return safe default (null/false/0/empty) |
| `isRootUri` | 1 | none | Return safe default (null/false/0/empty) |
| `isRootsUri` | 1 | none | Return safe default (null/false/0/empty) |
| `isTreeUri` | 1 | none | Return safe default (null/false/0/empty) |
| `removeDocument` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.DocumentsContract`:


## Quality Gates

Before marking `android.provider.DocumentsContract` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 21 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
