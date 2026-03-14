# SKILL: android.hardware.HardwareBuffer

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.hardware.HardwareBuffer`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.hardware.HardwareBuffer` |
| **Package** | `android.hardware` |
| **Total Methods** | 10 |
| **Avg Score** | 3.7 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 1 (10%) |
| **Partial/Composite** | 8 (80%) |
| **No Mapping** | 1 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `isClosed` | `boolean isClosed()` | 6 | near | moderate | `isClosed` | `resultSet.ResultSet` |
| `getUsage` | `long getUsage()` | 5 | partial | moderate | `getCpuUsage` | `getCpuUsage(): number` |

## Gap Descriptions (per method)

- **`isClosed`**: Auto-promoted: near score=6.01086956521739

## Stub APIs (score < 5): 8 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getLayers` | 4 | partial | Return safe default (null/false/0/empty) |
| `close` | 4 | partial | No-op |
| `isSupported` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeToParcel` | 3 | composite | Log warning + no-op |
| `getHeight` | 3 | composite | Return safe default (null/false/0/empty) |
| `getWidth` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFormat` | 3 | composite | Return safe default (null/false/0/empty) |
| `describeContents` | 1 | none | Store callback, never fire |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.hardware.HardwareBuffer`:


## Quality Gates

Before marking `android.hardware.HardwareBuffer` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 10 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
