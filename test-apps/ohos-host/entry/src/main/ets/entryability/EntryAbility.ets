/**
 * Android-on-OpenHarmony host ability.
 *
 * This UIAbility hosts the XComponent surface that the Android shim layer
 * renders into. The native library (liboh_bridge.so) is loaded by the
 * XComponent, which triggers:
 *   1. JNI_OnLoad → initializes Rust bridge
 *   2. XComponent callbacks → C++ on_surface_created → Rust surface module
 *   3. Java Activity.onSurfaceCreated → renderFrame loop
 */

import UIAbility from '@ohos.app.ability.UIAbility';
import AbilityConstant from '@ohos.app.ability.AbilityConstant';
import Want from '@ohos.app.ability.Want';
import window from '@ohos.window';
import { hilog } from '@kit.PerformanceAnalysisKit';

const TAG = 'A2OHEntryAbility';
const DOMAIN = 0xFF00;

export default class EntryAbility extends UIAbility {

  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    hilog.info(DOMAIN, TAG, 'onCreate');

    // Pass the APK path from the launch Want so the native side can load it
    if (want.parameters?.['apkPath']) {
      AppStorage.setOrCreate<string>('apkPath', want.parameters['apkPath'] as string);
    }

    // Pass the Activity class name to launch
    if (want.parameters?.['activityClass']) {
      AppStorage.setOrCreate<string>('activityClass', want.parameters['activityClass'] as string);
    }
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    hilog.info(DOMAIN, TAG, 'onWindowStageCreate');

    // Load the XComponent page that hosts the Android rendering surface
    windowStage.loadContent('pages/Index', (err) => {
      if (err.code) {
        hilog.error(DOMAIN, TAG, 'Failed to load content: %{public}s', JSON.stringify(err));
        return;
      }
      hilog.info(DOMAIN, TAG, 'Content loaded successfully');
    });
  }

  onForeground(): void {
    hilog.info(DOMAIN, TAG, 'onForeground');
    // Maps to Android Activity.onResume()
  }

  onBackground(): void {
    hilog.info(DOMAIN, TAG, 'onBackground');
    // Maps to Android Activity.onPause() + onStop()
  }

  onDestroy(): void {
    hilog.info(DOMAIN, TAG, 'onDestroy');
  }
}
