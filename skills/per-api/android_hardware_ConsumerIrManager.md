# SKILL: android.hardware.ConsumerIrManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.hardware.ConsumerIrManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.hardware.ConsumerIrManager` |
| **Package** | `android.hardware` |
| **Total Methods** | 3 |
| **Avg Score** | 2.3 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 1 (33%) |
| **No Mapping** | 2 (66%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 3 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getCarrierFrequencies` | 5 | partial | Return safe default (null/false/0/empty) |
| `hasIrEmitter` | 1 | none | Return safe default (null/false/0/empty) |
| `transmit` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 0 methods that have score >= 5
2. Stub 3 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.hardware.ConsumerIrManager`:


## Quality Gates

Before marking `android.hardware.ConsumerIrManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 3 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
