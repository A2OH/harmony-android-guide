# SKILL: android.provider.DocumentsProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.provider.DocumentsProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.provider.DocumentsProvider` |
| **Package** | `android.provider` |
| **Total Methods** | 35 |
| **Avg Score** | 2.3 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 22 (62%) |
| **No Mapping** | 13 (37%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DATA-LAYER.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 1 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `createDocument` | `String createDocument(String, String, String) throws java.io.FileNotFoundException` | 5 | partial | moderate | `createTime` | `createTime: string` |

## Stub APIs (score < 5): 34 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getType` | 5 | partial | Return safe default (null/false/0/empty) |
| `deleteDocument` | 4 | partial | throw UnsupportedOperationException |
| `moveDocument` | 4 | partial | throw UnsupportedOperationException |
| `getDocumentStreamTypes` | 4 | partial | Return safe default (null/false/0/empty) |
| `revokeDocumentPermission` | 4 | composite | Return safe default (null/false/0/empty) |
| `delete` | 3 | composite | throw UnsupportedOperationException |
| `insert` | 3 | composite | throw UnsupportedOperationException |
| `getDocumentType` | 3 | composite | Return safe default (null/false/0/empty) |
| `queryDocument` | 3 | composite | Return safe default (null/false/0/empty) |
| `queryRoots` | 3 | composite | Return safe default (null/false/0/empty) |
| `query` | 3 | composite | Return safe default (null/false/0/empty) |
| `query` | 3 | composite | Return safe default (null/false/0/empty) |
| `update` | 3 | composite | Log warning + no-op |
| `createWebLinkIntent` | 3 | composite | Return dummy instance / no-op |
| `querySearchDocuments` | 3 | composite | Return safe default (null/false/0/empty) |
| `isChildDocument` | 2 | composite | Return safe default (null/false/0/empty) |
| `queryChildDocuments` | 2 | composite | Return safe default (null/false/0/empty) |
| `queryChildDocuments` | 2 | composite | Return safe default (null/false/0/empty) |
| `queryRecentDocuments` | 2 | composite | Return safe default (null/false/0/empty) |
| `openTypedAssetFile` | 2 | composite | Return dummy instance / no-op |
| `openTypedAssetFile` | 2 | composite | Return dummy instance / no-op |
| `DocumentsProvider` | 1 | none | throw UnsupportedOperationException |
| `copyDocument` | 1 | none | throw UnsupportedOperationException |
| `ejectRoot` | 1 | none | throw UnsupportedOperationException |
| `findDocumentPath` | 1 | none | Return safe default (null/false/0/empty) |
| `openAssetFile` | 1 | none | Return dummy instance / no-op |
| `openAssetFile` | 1 | none | Return dummy instance / no-op |
| `openDocument` | 1 | none | Return dummy instance / no-op |
| `openDocumentThumbnail` | 1 | none | Return dummy instance / no-op |
| `openFile` | 1 | none | Return dummy instance / no-op |
| `openFile` | 1 | none | Return dummy instance / no-op |
| `openTypedDocument` | 1 | none | Return dummy instance / no-op |
| `removeDocument` | 1 | none | Log warning + no-op |
| `renameDocument` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.provider.DocumentsProvider`:


## Quality Gates

Before marking `android.provider.DocumentsProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 35 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 1 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
