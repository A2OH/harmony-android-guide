# SKILL: android.net.Uri.Builder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.net.Uri.Builder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.net.Uri.Builder` |
| **Package** | `android.net.Uri` |
| **Total Methods** | 17 |
| **Avg Score** | 1.3 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 2 (11%) |
| **No Mapping** | 15 (88%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-NETWORKING.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Stub APIs (score < 5): 17 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `path` | 4 | partial | throw UnsupportedOperationException |
| `query` | 3 | composite | Return safe default (null/false/0/empty) |
| `Builder` | 1 | none | throw UnsupportedOperationException |
| `appendEncodedPath` | 1 | none | throw UnsupportedOperationException |
| `appendPath` | 1 | none | throw UnsupportedOperationException |
| `appendQueryParameter` | 1 | none | Return safe default (null/false/0/empty) |
| `authority` | 1 | none | throw UnsupportedOperationException |
| `build` | 1 | none | throw UnsupportedOperationException |
| `clearQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `encodedAuthority` | 1 | none | throw UnsupportedOperationException |
| `encodedFragment` | 1 | none | throw UnsupportedOperationException |
| `encodedOpaquePart` | 1 | none | throw UnsupportedOperationException |
| `encodedPath` | 1 | none | throw UnsupportedOperationException |
| `encodedQuery` | 1 | none | Return safe default (null/false/0/empty) |
| `fragment` | 1 | none | throw UnsupportedOperationException |
| `opaquePart` | 1 | none | throw UnsupportedOperationException |
| `scheme` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.net.Uri.Builder`:


## Quality Gates

Before marking `android.net.Uri.Builder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
