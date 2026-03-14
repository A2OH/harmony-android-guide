# SKILL: android.media.AudioRecord

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.AudioRecord`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.AudioRecord` |
| **Package** | `android.media` |
| **Total Methods** | 42 |
| **Avg Score** | 4.0 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 11 (26%) |
| **Partial/Composite** | 19 (45%) |
| **No Mapping** | 12 (28%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 7 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 13 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `release` | `void release()` | 9 | direct | hard | `release` | `@ohos.multimedia.audio.AudioCapturer` |
| `startRecording` | `void startRecording() throws java.lang.IllegalStateException` | 9 | direct | hard | `start` | `@ohos.multimedia.audio.AudioCapturer` |
| `startRecording` | `void startRecording(android.media.MediaSyncEvent) throws java.lang.IllegalStateException` | 9 | direct | hard | `start` | `@ohos.multimedia.audio.AudioCapturer` |
| `stop` | `void stop() throws java.lang.IllegalStateException` | 9 | direct | hard | `stop` | `@ohos.multimedia.audio.AudioCapturer` |
| `read` | `int read(@NonNull byte[], int, int)` | 7 | near | moderate | `read` | `@ohos.multimedia.audio.AudioCapturer` |
| `read` | `int read(@NonNull byte[], int, int, int)` | 7 | near | moderate | `read` | `@ohos.multimedia.audio.AudioCapturer` |
| `read` | `int read(@NonNull short[], int, int)` | 7 | near | moderate | `read` | `@ohos.multimedia.audio.AudioCapturer` |
| `read` | `int read(@NonNull short[], int, int, int)` | 7 | near | moderate | `read` | `@ohos.multimedia.audio.AudioCapturer` |
| `read` | `int read(@NonNull float[], int, int, int)` | 7 | near | moderate | `read` | `@ohos.multimedia.audio.AudioCapturer` |
| `read` | `int read(@NonNull java.nio.ByteBuffer, int)` | 7 | near | moderate | `read` | `@ohos.multimedia.audio.AudioCapturer` |
| `read` | `int read(@NonNull java.nio.ByteBuffer, int, int)` | 7 | near | moderate | `read` | `@ohos.multimedia.audio.AudioCapturer` |
| `getChannelCount` | `int getChannelCount()` | 5 | partial | moderate | `getCount` | `getCount(): number` |
| `getRecordingState` | `int getRecordingState()` | 5 | partial | moderate | `getCount` | `getCount(): number` |

## Gap Descriptions (per method)

- **`release`**: Direct equivalent
- **`startRecording`**: Direct equivalent
- **`startRecording`**: Direct equivalent
- **`stop`**: Direct equivalent
- **`read`**: ArrayBuffer via Promise vs byte[]
- **`read`**: ArrayBuffer via Promise vs byte[]
- **`read`**: ArrayBuffer via Promise vs byte[]
- **`read`**: ArrayBuffer via Promise vs byte[]
- **`read`**: ArrayBuffer via Promise vs byte[]
- **`read`**: ArrayBuffer via Promise vs byte[]
- **`read`**: ArrayBuffer via Promise vs byte[]

## Stub APIs (score < 5): 29 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `getSampleRate` | 5 | partial | Return safe default (null/false/0/empty) |
| `AudioRecord` | 5 | partial | throw UnsupportedOperationException |
| `getAudioSessionId` | 5 | partial | Return safe default (null/false/0/empty) |
| `isPrivacySensitive` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAudioFormat` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAudioSource` | 4 | partial | Return safe default (null/false/0/empty) |
| `getState` | 4 | partial | Return safe default (null/false/0/empty) |
| `registerAudioRecordingCallback` | 4 | partial | Return safe default (null/false/0/empty) |
| `getRoutedDevice` | 4 | partial | Return safe default (null/false/0/empty) |
| `unregisterAudioRecordingCallback` | 4 | partial | Return safe default (null/false/0/empty) |
| `getActiveMicrophones` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMetrics` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPositionNotificationPeriod` | 3 | composite | Return safe default (null/false/0/empty) |
| `setPositionNotificationPeriod` | 3 | composite | Log warning + no-op |
| `getTimestamp` | 3 | composite | Return safe default (null/false/0/empty) |
| `getBufferSizeInFrames` | 2 | composite | Return safe default (null/false/0/empty) |
| `getChannelConfiguration` | 2 | composite | Return safe default (null/false/0/empty) |
| `addOnRoutingChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `getMinBufferSize` | 1 | none | Return safe default (null/false/0/empty) |
| `getNotificationMarkerPosition` | 1 | none | Return safe default (null/false/0/empty) |
| `getPreferredDevice` | 1 | none | Return safe default (null/false/0/empty) |
| `removeOnRoutingChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setNotificationMarkerPosition` | 1 | none | Log warning + no-op |
| `setPreferredDevice` | 1 | none | Log warning + no-op |
| `setPreferredMicrophoneDirection` | 1 | none | Log warning + no-op |
| `setPreferredMicrophoneFieldDimension` | 1 | none | Log warning + no-op |
| `setRecordPositionUpdateListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setRecordPositionUpdateListener` | 1 | none | Return safe default (null/false/0/empty) |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.AudioRecord`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.AudioRecord` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 42 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 13 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
