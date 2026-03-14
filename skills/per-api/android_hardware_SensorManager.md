# SKILL: android.hardware.SensorManager

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.hardware.SensorManager`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.hardware.SensorManager` |
| **Package** | `android.hardware` |
| **Total Methods** | 30 |
| **Avg Score** | 5.7 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 12 (40%) |
| **Partial/Composite** | 15 (50%) |
| **No Mapping** | 3 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 17 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getAltitude` | `static float getAltitude(float, float)` | 9 | direct | moderate | `getAltitude` | `@ohos.sensor.sensor` |
| `getDefaultSensor` | `android.hardware.Sensor getDefaultSensor(int)` | 9 | direct | easy | `ACCELEROMETER` | `ACCELEROMETER = 1` |
| `getDefaultSensor` | `android.hardware.Sensor getDefaultSensor(int, boolean)` | 9 | direct | easy | `ACCELEROMETER` | `ACCELEROMETER = 1` |
| `getOrientation` | `static float[] getOrientation(float[], float[])` | 9 | direct | hard | `getOrientation` | `@ohos.sensor.sensor` |
| `getRotationMatrix` | `static boolean getRotationMatrix(float[], float[], float[], float[])` | 9 | direct | hard | `getRotationMatrix` | `@ohos.sensor.sensor` |
| `getSensorList` | `java.util.List<android.hardware.Sensor> getSensorList(int)` | 9 | direct | hard | `getSensorList` | `@ohos.sensor.sensor` |
| `registerListener` | `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int)` | 9 | direct | moderate | `on` | `on(type: SensorId.COLOR, callback: Callback<ColorResponse>, options?: Options): void` |
| `registerListener` | `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, int)` | 9 | direct | moderate | `on` | `on(type: SensorId.COLOR, callback: Callback<ColorResponse>, options?: Options): void` |
| `registerListener` | `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, android.os.Handler)` | 9 | direct | moderate | `on` | `on(type: SensorId.COLOR, callback: Callback<ColorResponse>, options?: Options): void` |
| `registerListener` | `boolean registerListener(android.hardware.SensorEventListener, android.hardware.Sensor, int, int, android.os.Handler)` | 9 | direct | moderate | `on` | `on(type: SensorId.COLOR, callback: Callback<ColorResponse>, options?: Options): void` |
| `unregisterListener` | `void unregisterListener(android.hardware.SensorEventListener, android.hardware.Sensor)` | 9 | direct | moderate | `off` | `off(type: SensorId.COLOR, callback?: Callback<ColorResponse>): void` |
| `unregisterListener` | `void unregisterListener(android.hardware.SensorEventListener)` | 9 | direct | moderate | `off` | `off(type: SensorId.COLOR, callback?: Callback<ColorResponse>): void` |
| `unregisterDynamicSensorCallback` | `void unregisterDynamicSensorCallback(android.hardware.SensorManager.DynamicSensorCallback)` | 5 | partial | moderate | `unregisterVsyncCallback` | `unregisterVsyncCallback(): void` |
| `onDynamicSensorConnected` | `void onDynamicSensorConnected(android.hardware.Sensor)` | 5 | partial | moderate | `onDisconnect` | `onDisconnect?: () => void` |
| `registerDynamicSensorCallback` | `void registerDynamicSensorCallback(android.hardware.SensorManager.DynamicSensorCallback)` | 5 | partial | moderate | `unregisterVsyncCallback` | `unregisterVsyncCallback(): void` |
| `registerDynamicSensorCallback` | `void registerDynamicSensorCallback(android.hardware.SensorManager.DynamicSensorCallback, android.os.Handler)` | 5 | partial | moderate | `unregisterVsyncCallback` | `unregisterVsyncCallback(): void` |
| `onDynamicSensorDisconnected` | `void onDynamicSensorDisconnected(android.hardware.Sensor)` | 5 | partial | moderate | `onDisconnect` | `onDisconnect?: () => void` |

## Gap Descriptions (per method)

- **`getAltitude`**: Direct static utility
- **`getDefaultSensor`**: Sensor type param in on()
- **`getDefaultSensor`**: Sensor type param in on()
- **`getOrientation`**: Direct static utility
- **`getRotationMatrix`**: Direct static utility
- **`getSensorList`**: Direct equivalent
- **`registerListener`**: Sensor listener
- **`registerListener`**: Sensor listener
- **`registerListener`**: Sensor listener
- **`registerListener`**: Sensor listener
- **`unregisterListener`**: Sensor listener
- **`unregisterListener`**: Sensor listener

## Stub APIs (score < 5): 13 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `DynamicSensorCallback` | 5 | partial | throw UnsupportedOperationException |
| `getInclination` | 4 | partial | Return safe default (null/false/0/empty) |
| `getDynamicSensorList` | 4 | partial | Return safe default (null/false/0/empty) |
| `requestTriggerSensor` | 3 | composite | throw UnsupportedOperationException |
| `getAngleChange` | 3 | composite | Return safe default (null/false/0/empty) |
| `isDynamicSensorDiscoverySupported` | 3 | composite | Return safe default (null/false/0/empty) |
| `createDirectChannel` | 3 | composite | Return dummy instance / no-op |
| `createDirectChannel` | 3 | composite | Return dummy instance / no-op |
| `cancelTriggerSensor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getQuaternionFromVector` | 2 | composite | Return safe default (null/false/0/empty) |
| `flush` | 1 | none | throw UnsupportedOperationException |
| `getRotationMatrixFromVector` | 1 | none | Return safe default (null/false/0/empty) |
| `remapCoordinateSystem` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 17 methods that have score >= 5
2. Stub 13 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.hardware.SensorManager`:


## Quality Gates

Before marking `android.hardware.SensorManager` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 30 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 17 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
