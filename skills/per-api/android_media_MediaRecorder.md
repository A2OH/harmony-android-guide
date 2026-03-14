# SKILL: android.media.MediaRecorder

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRecorder`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRecorder` |
| **Package** | `android.media` |
| **Total Methods** | 52 |
| **Avg Score** | 4.9 |
| **Scenario** | S8: No Mapping (Stub) |
| **Strategy** | Stub with UnsupportedOperationException or no-op |
| **Direct/Near** | 23 (44%) |
| **Partial/Composite** | 19 (36%) |
| **No Mapping** | 10 (19%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 15 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 1 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 23 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getMaxAmplitude` | `int getMaxAmplitude() throws java.lang.IllegalStateException` | 9 | direct | impossible | `getAudioCapturerMaxAmplitude` | `@ohos.multimedia.media.AVRecorder` |
| `pause` | `void pause() throws java.lang.IllegalStateException` | 9 | direct | moderate | `pause` | `@ohos.multimedia.media.AVRecorder` |
| `prepare` | `void prepare() throws java.io.IOException, java.lang.IllegalStateException` | 9 | direct | hard | `prepare` | `@ohos.multimedia.media.AVRecorder` |
| `release` | `void release()` | 9 | direct | hard | `release` | `@ohos.multimedia.media.AVRecorder` |
| `reset` | `void reset()` | 9 | direct | hard | `reset` | `@ohos.multimedia.media.AVRecorder` |
| `resume` | `void resume() throws java.lang.IllegalStateException` | 9 | direct | moderate | `resume` | `@ohos.multimedia.media.AVRecorder` |
| `start` | `void start() throws java.lang.IllegalStateException` | 9 | direct | hard | `start` | `@ohos.multimedia.media.AVRecorder` |
| `stop` | `void stop() throws java.lang.IllegalStateException` | 9 | direct | hard | `stop` | `@ohos.multimedia.media.AVRecorder` |
| `setAudioChannels` | `void setAudioChannels(int)` | 7 | near | hard | `audioChannels` | `@ohos.multimedia.media.AVRecorderProfile` |
| `setAudioEncoder` | `void setAudioEncoder(int) throws java.lang.IllegalStateException` | 7 | near | hard | `audioCodec` | `@ohos.multimedia.media.AVRecorderProfile` |
| `setAudioEncodingBitRate` | `void setAudioEncodingBitRate(int)` | 7 | near | rewrite | `audioBitrate` | `@ohos.multimedia.media.AVRecorderProfile` |
| `setAudioSamplingRate` | `void setAudioSamplingRate(int)` | 7 | near | hard | `audioSampleRate` | `@ohos.multimedia.media.AVRecorderProfile` |
| `setAudioSource` | `void setAudioSource(int) throws java.lang.IllegalStateException` | 7 | near | hard | `audioSourceType` | `@ohos.multimedia.media.AVRecorderConfig` |
| `setLocation` | `void setLocation(float, float)` | 7 | near | rewrite | `location` | `@ohos.multimedia.media.AVRecorderConfig` |
| `setOrientationHint` | `void setOrientationHint(int)` | 7 | near | hard | `rotation` | `@ohos.multimedia.media.AVRecorderConfig` |
| `setOutputFile` | `void setOutputFile(java.io.FileDescriptor) throws java.lang.IllegalStateException` | 7 | near | rewrite | `url` | `@ohos.multimedia.media.AVRecorderConfig` |
| `setOutputFile` | `void setOutputFile(java.io.File)` | 7 | near | rewrite | `url` | `@ohos.multimedia.media.AVRecorderConfig` |
| `setOutputFile` | `void setOutputFile(String) throws java.lang.IllegalStateException` | 7 | near | rewrite | `url` | `@ohos.multimedia.media.AVRecorderConfig` |
| `setOutputFormat` | `void setOutputFormat(int) throws java.lang.IllegalStateException` | 7 | near | hard | `fileFormat` | `@ohos.multimedia.media.AVRecorderProfile` |
| `setVideoEncoder` | `void setVideoEncoder(int) throws java.lang.IllegalStateException` | 7 | near | hard | `videoCodec` | `@ohos.multimedia.media.AVRecorderProfile` |
| `setVideoEncodingBitRate` | `void setVideoEncodingBitRate(int)` | 7 | near | rewrite | `videoBitrate` | `@ohos.multimedia.media.AVRecorderProfile` |
| `setVideoFrameRate` | `void setVideoFrameRate(int) throws java.lang.IllegalStateException` | 7 | near | hard | `videoFrameRate` | `@ohos.multimedia.media.AVRecorderProfile` |
| `setVideoSource` | `void setVideoSource(int) throws java.lang.IllegalStateException` | 7 | near | rewrite | `videoSourceType` | `@ohos.multimedia.media.AVRecorderConfig` |

## Gap Descriptions (per method)

- **`getMaxAmplitude`**: Direct equivalent
- **`pause`**: Direct equivalent
- **`prepare`**: Direct equivalent
- **`release`**: Direct equivalent
- **`reset`**: Direct equivalent
- **`resume`**: Direct equivalent
- **`start`**: Direct equivalent
- **`stop`**: Direct equivalent
- **`setAudioChannels`**: Config property
- **`setAudioEncoder`**: CodecMimeType
- **`setAudioEncodingBitRate`**: Config property
- **`setAudioSamplingRate`**: Config property
- **`setAudioSource`**: Config property vs method
- **`setLocation`**: Config Location object
- **`setOrientationHint`**: Config property
- **`setOutputFile`**: fd:// or file:// URI
- **`setOutputFile`**: fd:// or file:// URI
- **`setOutputFile`**: fd:// or file:// URI
- **`setOutputFormat`**: ContainerFormatType enum
- **`setVideoEncoder`**: CodecMimeType
- **`setVideoEncodingBitRate`**: Config property
- **`setVideoFrameRate`**: Config property
- **`setVideoSource`**: Config property vs method

## Stub APIs (score < 5): 29 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `setMaxDuration` | 5 | partial | Log warning + no-op |
| `isPrivacySensitive` | 4 | partial | Return safe default (null/false/0/empty) |
| `setPrivacySensitive` | 4 | partial | Log warning + no-op |
| `getRoutedDevice` | 4 | partial | Return safe default (null/false/0/empty) |
| `getAudioSourceMax` | 4 | partial | Return safe default (null/false/0/empty) |
| `registerAudioRecordingCallback` | 4 | partial | Return safe default (null/false/0/empty) |
| `unregisterAudioRecordingCallback` | 4 | partial | Return safe default (null/false/0/empty) |
| `setPreviewDisplay` | 4 | composite | Return safe default (null/false/0/empty) |
| `setNextOutputFile` | 3 | composite | Log warning + no-op |
| `setNextOutputFile` | 3 | composite | Log warning + no-op |
| `setMaxFileSize` | 3 | composite | Log warning + no-op |
| `setVideoSize` | 3 | composite | Log warning + no-op |
| `setInputSurface` | 3 | composite | Log warning + no-op |
| `getActiveMicrophones` | 3 | composite | Return safe default (null/false/0/empty) |
| `MediaRecorder` | 3 | composite | throw UnsupportedOperationException |
| `getMetrics` | 3 | composite | Return safe default (null/false/0/empty) |
| `setCaptureRate` | 3 | composite | Log warning + no-op |
| `getSurface` | 2 | composite | Return safe default (null/false/0/empty) |
| `setVideoEncodingProfileLevel` | 2 | composite | Log warning + no-op |
| `addOnRoutingChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `finalize` | 1 | none | throw UnsupportedOperationException |
| `getPreferredDevice` | 1 | none | Return safe default (null/false/0/empty) |
| `removeOnRoutingChangedListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnErrorListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setOnInfoListener` | 1 | none | Return safe default (null/false/0/empty) |
| `setPreferredDevice` | 1 | none | Log warning + no-op |
| `setPreferredMicrophoneDirection` | 1 | none | Log warning + no-op |
| `setPreferredMicrophoneFieldDimension` | 1 | none | Log warning + no-op |
| `setProfile` | 1 | none | Log warning + no-op |

## AI Agent Instructions

**Scenario: S8 — No Mapping (Stub)**

1. Create minimal stub class matching AOSP package/class name
2. All lifecycle methods (create/destroy): no-op, return dummy
3. All computation methods: throw UnsupportedOperationException with message
4. All query methods: return safe defaults
5. Log a warning on first use: "X is not supported on OHOS"
6. Only test: no crash on construction, expected exceptions

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRecorder`:

- `android.content.Context` (already shimmed)

## Quality Gates

Before marking `android.media.MediaRecorder` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 52 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 23 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
