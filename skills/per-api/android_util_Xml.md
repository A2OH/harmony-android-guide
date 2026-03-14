# SKILL: android.util.Xml

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Xml`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Xml` |
| **Package** | `android.util` |
| **Total Methods** | 7 |
| **Avg Score** | 4.0 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 2 (28%) |
| **Partial/Composite** | 4 (57%) |
| **No Mapping** | 1 (14%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 1 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `newPullParser` | `static org.xmlpull.v1.XmlPullParser newPullParser()` | 7 | near | impossible | `constructor` | `@ohos.xml.XmlPullParser` |
| `newSerializer` | `static org.xmlpull.v1.XmlSerializer newSerializer()` | 7 | near | rewrite | `constructor` | `@ohos.xml.XmlSerializer` |

## Gap Descriptions (per method)

- **`newPullParser`**: Callback-based parse
- **`newSerializer`**: Similar methods

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `parse` | 3 | composite | throw UnsupportedOperationException |
| `parse` | 3 | composite | throw UnsupportedOperationException |
| `parse` | 3 | composite | throw UnsupportedOperationException |
| `findEncodingByName` | 3 | composite | Return safe default (null/false/0/empty) |
| `asAttributeSet` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Xml`:


## Quality Gates

Before marking `android.util.Xml` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
