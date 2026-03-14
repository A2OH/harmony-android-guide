# SKILL: android.telephony.PhoneNumberUtils

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.PhoneNumberUtils`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.PhoneNumberUtils` |
| **Package** | `android.telephony` |
| **Total Methods** | 35 |
| **Avg Score** | 1.9 |
| **Scenario** | S4: Multi-API Composition |
| **Strategy** | Multiple OH calls per Android call |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 18 (51%) |
| **No Mapping** | 17 (48%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Stub APIs (score < 5): 35 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `isNonSeparator` | 3 | composite | Return safe default (null/false/0/empty) |
| `addTtsSpan` | 3 | composite | Log warning + no-op |
| `formatNumberToE164` | 3 | composite | throw UnsupportedOperationException |
| `isVoiceMailNumber` | 3 | composite | Return safe default (null/false/0/empty) |
| `formatNumber` | 3 | composite | throw UnsupportedOperationException |
| `formatNumber` | 3 | composite | throw UnsupportedOperationException |
| `PhoneNumberUtils` | 3 | composite | Store callback, never fire |
| `formatNumberToRFC3966` | 3 | composite | throw UnsupportedOperationException |
| `isGlobalPhoneNumber` | 3 | composite | Return safe default (null/false/0/empty) |
| `createTtsSpan` | 3 | composite | Return dummy instance / no-op |
| `normalizeNumber` | 3 | composite | throw UnsupportedOperationException |
| `createTtsSpannable` | 3 | composite | Return dummy instance / no-op |
| `compare` | 3 | composite | throw UnsupportedOperationException |
| `compare` | 3 | composite | throw UnsupportedOperationException |
| `extractNetworkPortion` | 3 | composite | Store callback, never fire |
| `isReallyDialable` | 3 | composite | Return safe default (null/false/0/empty) |
| `getStrippedReversed` | 2 | composite | Return safe default (null/false/0/empty) |
| `isDialable` | 2 | composite | Return safe default (null/false/0/empty) |
| `calledPartyBCDFragmentToString` | 1 | none | throw UnsupportedOperationException |
| `calledPartyBCDToString` | 1 | none | throw UnsupportedOperationException |
| `convertKeypadLettersToDigits` | 1 | none | Store callback, never fire |
| `extractPostDialPortion` | 1 | none | Store callback, never fire |
| `getNumberFromIntent` | 1 | none | Return safe default (null/false/0/empty) |
| `is12Key` | 1 | none | Return safe default (null/false/0/empty) |
| `isISODigit` | 1 | none | Return safe default (null/false/0/empty) |
| `isStartsPostDial` | 1 | none | Return dummy instance / no-op |
| `isWellFormedSmsAddress` | 1 | none | Return safe default (null/false/0/empty) |
| `networkPortionToCalledPartyBCD` | 1 | none | Store callback, never fire |
| `networkPortionToCalledPartyBCDWithLength` | 1 | none | Store callback, never fire |
| `numberToCalledPartyBCD` | 1 | none | throw UnsupportedOperationException |
| `replaceUnicodeDigits` | 1 | none | throw UnsupportedOperationException |
| `stringFromStringAndTOA` | 1 | none | throw UnsupportedOperationException |
| `stripSeparators` | 1 | none | throw UnsupportedOperationException |
| `toCallerIDMinMatch` | 1 | none | throw UnsupportedOperationException |
| `toaFromString` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S4 — Multi-API Composition**

1. Study the OH equivalents in the table — note where one Android call maps to multiple OH calls
2. Create helper methods in OHBridge for multi-call compositions
3. Map action strings, enum values, and parameter structures
4. Test the composition logic end-to-end: Android input → shim → OH bridge mock → verify output
5. Check the Migration Guides above for specific conversion patterns

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.PhoneNumberUtils`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.telephony.PhoneNumberUtils` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 35 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
