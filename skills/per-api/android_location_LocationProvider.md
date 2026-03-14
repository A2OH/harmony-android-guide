# SKILL: android.location.LocationProvider

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.location.LocationProvider`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.location.LocationProvider` |
| **Package** | `android.location` |
| **Total Methods** | 11 |
| **Avg Score** | 3.4 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 7 (63%) |
| **No Mapping** | 4 (36%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 2 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAccuracy` | `int getAccuracy()` | 6 | partial | moderate | `accuracy` | `accuracy: number` |
| `getName` | `String getName()` | 5 | partial | moderate | `getLocalName` | `getLocalName(): string` |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `supportsAltitude` | 5 | partial | throw UnsupportedOperationException |
| `supportsSpeed` | 5 | partial | throw UnsupportedOperationException |
| `requiresSatellite` | 4 | partial | throw UnsupportedOperationException |
| `getPowerRequirement` | 4 | partial | Return safe default (null/false/0/empty) |
| `requiresNetwork` | 4 | partial | throw UnsupportedOperationException |
| `hasMonetaryCost` | 1 | none | Return safe default (null/false/0/empty) |
| `meetsCriteria` | 1 | none | throw UnsupportedOperationException |
| `requiresCell` | 1 | none | throw UnsupportedOperationException |
| `supportsBearing` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 2 methods that have score >= 5
2. Stub 9 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.location.LocationProvider`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.location.LocationProvider` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 11 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 2 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
