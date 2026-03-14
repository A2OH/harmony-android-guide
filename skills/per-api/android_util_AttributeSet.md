# SKILL: android.util.AttributeSet

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.AttributeSet`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.AttributeSet` |
| **Package** | `android.util` |
| **Total Methods** | 23 |
| **Avg Score** | 3.6 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 23 (100%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 3 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getPositionDescription` | `String getPositionDescription()` | 5 | partial | moderate | `description` | `description: string` |
| `getAttributeBooleanValue` | `boolean getAttributeBooleanValue(String, String, boolean)` | 5 | partial | moderate | `attributeValueCallbackFunction` | `attributeValueCallbackFunction?: (name: string, value: string) => boolean` |
| `getAttributeBooleanValue` | `boolean getAttributeBooleanValue(int, boolean)` | 5 | partial | moderate | `attributeValueCallbackFunction` | `attributeValueCallbackFunction?: (name: string, value: string) => boolean` |

## Stub APIs (score < 5): 20 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getIdAttribute` | 4 | partial | Return safe default (null/false/0/empty) |
| `getStyleAttribute` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAttributeResourceValue` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAttributeResourceValue` | 4 | partial | Return safe default (null/false/0/empty) |
| `getIdAttributeResourceValue` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAttributeNameResource` | 4 | partial | Return safe default (null/false/0/empty) |
| `getClassAttribute` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAttributeFloatValue` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAttributeFloatValue` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAttributeCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributeUnsignedIntValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributeUnsignedIntValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributeName` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributeValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributeValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributeListValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributeListValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributeNamespace` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAttributeIntValue` | 2 | composite | Return safe default (null/false/0/empty) |
| `getAttributeIntValue` | 2 | composite | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.util.AttributeSet`:


## Quality Gates

Before marking `android.util.AttributeSet` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 23 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 3 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
