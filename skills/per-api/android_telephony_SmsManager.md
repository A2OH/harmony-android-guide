# SKILL: android.telephony.SmsManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.telephony.SmsManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.telephony.SmsManager` |
| **Package** | `android.telephony` |
| **Total Methods** | 17 |
| **Avg Score** | 5.6 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 4 (23%) |
| **Partial/Composite** | 13 (76%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 12 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `divideMessage` | `java.util.ArrayList<java.lang.String> divideMessage(String)` | 9 | direct | hard | `splitMessage` | `@ohos.telephony.sms.sms` |
| `sendTextMessage` | `void sendTextMessage(String, String, String, android.app.PendingIntent, android.app.PendingIntent)` | 9 | direct | easy | `sendShortMessage` | `sendShortMessage(options: SendMessageOptions, callback: AsyncCallback<void>): void` |
| `sendTextMessage` | `void sendTextMessage(@NonNull String, @Nullable String, @NonNull String, @Nullable android.app.PendingIntent, @Nullable android.app.PendingIntent, long)` | 9 | direct | easy | `sendShortMessage` | `sendShortMessage(options: SendMessageOptions, callback: AsyncCallback<void>): void` |
| `sendDataMessage` | `void sendDataMessage(String, String, short, byte[], android.app.PendingIntent, android.app.PendingIntent)` | 6 | near | moderate | `sendMessage` | `sendMessage(options: SendMessageOptions): void` |
| `sendMultimediaMessage` | `void sendMultimediaMessage(android.content.Context, android.net.Uri, String, android.os.Bundle, android.app.PendingIntent)` | 5 | partial | moderate | `sendMessage` | `sendMessage(options: SendMessageOptions): void` |
| `sendMultipartTextMessage` | `void sendMultipartTextMessage(String, String, java.util.ArrayList<java.lang.String>, java.util.ArrayList<android.app.PendingIntent>, java.util.ArrayList<android.app.PendingIntent>)` | 5 | partial | moderate | `sendShortMessage` | `sendShortMessage(options: SendMessageOptions, callback: AsyncCallback<void>): void` |
| `sendMultipartTextMessage` | `void sendMultipartTextMessage(@NonNull String, @Nullable String, @NonNull java.util.List<java.lang.String>, @Nullable java.util.List<android.app.PendingIntent>, @Nullable java.util.List<android.app.PendingIntent>, long)` | 5 | partial | moderate | `sendShortMessage` | `sendShortMessage(options: SendMessageOptions, callback: AsyncCallback<void>): void` |
| `sendMultipartTextMessage` | `void sendMultipartTextMessage(@NonNull String, @Nullable String, @NonNull java.util.List<java.lang.String>, @Nullable java.util.List<android.app.PendingIntent>, @Nullable java.util.List<android.app.PendingIntent>, @NonNull String, @Nullable String)` | 5 | partial | moderate | `sendShortMessage` | `sendShortMessage(options: SendMessageOptions, callback: AsyncCallback<void>): void` |
| `getDefaultSmsSubscriptionId` | `static int getDefaultSmsSubscriptionId()` | 5 | partial | moderate | `getDefaultSmsSlotId` | `getDefaultSmsSlotId(callback: AsyncCallback<number>): void` |
| `FinancialSmsCallback` | `SmsManager.FinancialSmsCallback()` | 5 | partial | moderate | `hasSmsCapability` | `hasSmsCapability(): boolean` |
| `onFinancialSmsMessages` | `abstract void onFinancialSmsMessages(android.database.CursorWindow)` | 5 | partial | moderate | `getAllSimMessages` | `getAllSimMessages(slotId: number, callback: AsyncCallback<Array<SimShortMessage>>): void` |
| `downloadMultimediaMessage` | `void downloadMultimediaMessage(android.content.Context, String, android.net.Uri, android.os.Bundle, android.app.PendingIntent)` | 5 | partial | moderate | `downloadMms` | `downloadMms(context: Context, mmsParams: MmsParams, callback: AsyncCallback<void>): void` |

## Gap Descriptions (per method)

- **`divideMessage`**: Direct equivalent
- **`sendTextMessage`**: Send SMS
- **`sendTextMessage`**: Send SMS

## Stub APIs (score < 5): 5 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getDefault` | 5 | partial | Return safe default (null/false/0/empty) |
| `injectSmsPdu` | 4 | partial | throw UnsupportedOperationException |
| `getSubscriptionId` | 4 | partial | Return safe default (null/false/0/empty) |
| `getSmsManagerForSubscriptionId` | 4 | composite | Return safe default (null/false/0/empty) |
| `createAppSpecificSmsToken` | 3 | composite | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 12 methods that have score >= 5
2. Stub 5 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.telephony.SmsManager`:

- `android.app.PendingIntent` (already shimmed)

## Quality Gates

Before marking `android.telephony.SmsManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 17 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 12 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
