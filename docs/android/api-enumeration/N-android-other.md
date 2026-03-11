# Android 11 (API 30) Public API Enumeration: Android Other

Generated from `frameworks/base/api/current.txt`

## Summary

| Package | Types | Methods | Fields | Ctors |
|---------|------:|--------:|-------:|------:|
| `android.accessibilityservice` | 14 | 82 | 95 | 8 |
| `android.accounts` | 13 | 49 | 50 | 22 |
| `android.animation` | 26 | 199 | 9 | 23 |
| `android.bluetooth` | 29 | 191 | 486 | 11 |
| `android.bluetooth.le` | 20 | 130 | 56 | 10 |
| `android.companion` | 10 | 17 | 1 | 5 |
| `android.drm` | 22 | 0 | 0 | 0 |
| `android.gesture` | 13 | 105 | 23 | 9 |
| `android.icu.lang` | 25 | 118 | 1333 | 0 |
| `android.icu.math` | 2 | 56 | 23 | 13 |
| `android.icu.number` | 26 | 77 | 0 | 0 |
| `android.icu.text` | 101 | 1067 | 318 | 100 |
| `android.icu.util` | 38 | 349 | 397 | 120 |
| `android.inputmethodservice` | 13 | 100 | 10 | 11 |
| `android.location` | 25 | 231 | 87 | 15 |
| `android.mtp` | 7 | 74 | 117 | 4 |
| `android.nfc` | 13 | 35 | 44 | 9 |
| `android.nfc.cardemulation` | 5 | 28 | 17 | 3 |
| `android.nfc.tech` | 11 | 118 | 23 | 0 |
| `android.preference` | 24 | 0 | 0 | 0 |
| `android.print` | 19 | 53 | 105 | 11 |
| `android.print.pdf` | 1 | 0 | 0 | 1 |
| `android.printservice` | 5 | 19 | 7 | 2 |
| `android.sax` | 7 | 13 | 0 | 2 |
| `android.se.omapi` | 5 | 12 | 0 | 1 |
| `android.speech` | 6 | 31 | 52 | 1 |
| `android.speech.tts` | 10 | 73 | 51 | 9 |
| `android.transition` | 25 | 145 | 10 | 43 |
| `android.webkit` | 56 | 327 | 67 | 27 |
| **TOTAL** | **571** | **3699** | **3381** | **460** |

---

## Package: `android.accessibilityservice`

### `class final AccessibilityButtonController`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityButtonController.AccessibilityButtonCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isAccessibilityButtonAvailable()` |  |
| `void registerAccessibilityButtonCallback(@NonNull android.accessibilityservice.AccessibilityButtonController.AccessibilityButtonCallback)` |  |
| `void registerAccessibilityButtonCallback(@NonNull android.accessibilityservice.AccessibilityButtonController.AccessibilityButtonCallback, @NonNull android.os.Handler)` |  |
| `void unregisterAccessibilityButtonCallback(@NonNull android.accessibilityservice.AccessibilityButtonController.AccessibilityButtonCallback)` |  |
| `void onAvailabilityChanged(android.accessibilityservice.AccessibilityButtonController, boolean)` |  |
| `void onClicked(android.accessibilityservice.AccessibilityButtonController)` |  |

---

### `class final AccessibilityGestureEvent`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDisplayId()` |  |
| `int getGestureId()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class abstract AccessibilityService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityService()` |  |
| `AccessibilityService.GestureResultCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ERROR_TAKE_SCREENSHOT_INTERNAL_ERROR = 1` |  |
| `static final int ERROR_TAKE_SCREENSHOT_INTERVAL_TIME_SHORT = 3` |  |
| `static final int ERROR_TAKE_SCREENSHOT_INVALID_DISPLAY = 4` |  |
| `static final int ERROR_TAKE_SCREENSHOT_NO_ACCESSIBILITY_ACCESS = 2` |  |
| `static final int GESTURE_2_FINGER_DOUBLE_TAP = 20` |  |
| `static final int GESTURE_2_FINGER_DOUBLE_TAP_AND_HOLD = 40` |  |
| `static final int GESTURE_2_FINGER_SINGLE_TAP = 19` |  |
| `static final int GESTURE_2_FINGER_SWIPE_DOWN = 26` |  |
| `static final int GESTURE_2_FINGER_SWIPE_LEFT = 27` |  |
| `static final int GESTURE_2_FINGER_SWIPE_RIGHT = 28` |  |
| `static final int GESTURE_2_FINGER_SWIPE_UP = 25` |  |
| `static final int GESTURE_2_FINGER_TRIPLE_TAP = 21` |  |
| `static final int GESTURE_3_FINGER_DOUBLE_TAP = 23` |  |
| `static final int GESTURE_3_FINGER_DOUBLE_TAP_AND_HOLD = 41` |  |
| `static final int GESTURE_3_FINGER_SINGLE_TAP = 22` |  |
| `static final int GESTURE_3_FINGER_SWIPE_DOWN = 30` |  |
| `static final int GESTURE_3_FINGER_SWIPE_LEFT = 31` |  |
| `static final int GESTURE_3_FINGER_SWIPE_RIGHT = 32` |  |
| `static final int GESTURE_3_FINGER_SWIPE_UP = 29` |  |
| `static final int GESTURE_3_FINGER_TRIPLE_TAP = 24` |  |
| `static final int GESTURE_4_FINGER_DOUBLE_TAP = 38` |  |
| `static final int GESTURE_4_FINGER_DOUBLE_TAP_AND_HOLD = 42` |  |
| `static final int GESTURE_4_FINGER_SINGLE_TAP = 37` |  |
| `static final int GESTURE_4_FINGER_SWIPE_DOWN = 34` |  |
| `static final int GESTURE_4_FINGER_SWIPE_LEFT = 35` |  |
| `static final int GESTURE_4_FINGER_SWIPE_RIGHT = 36` |  |
| `static final int GESTURE_4_FINGER_SWIPE_UP = 33` |  |
| `static final int GESTURE_4_FINGER_TRIPLE_TAP = 39` |  |
| `static final int GESTURE_DOUBLE_TAP = 17` |  |
| `static final int GESTURE_DOUBLE_TAP_AND_HOLD = 18` |  |
| `static final int GESTURE_SWIPE_DOWN = 2` |  |
| `static final int GESTURE_SWIPE_DOWN_AND_LEFT = 15` |  |
| `static final int GESTURE_SWIPE_DOWN_AND_RIGHT = 16` |  |
| `static final int GESTURE_SWIPE_DOWN_AND_UP = 8` |  |
| `static final int GESTURE_SWIPE_LEFT = 3` |  |
| `static final int GESTURE_SWIPE_LEFT_AND_DOWN = 10` |  |
| `static final int GESTURE_SWIPE_LEFT_AND_RIGHT = 5` |  |
| `static final int GESTURE_SWIPE_LEFT_AND_UP = 9` |  |
| `static final int GESTURE_SWIPE_RIGHT = 4` |  |
| `static final int GESTURE_SWIPE_RIGHT_AND_DOWN = 12` |  |
| `static final int GESTURE_SWIPE_RIGHT_AND_LEFT = 6` |  |
| `static final int GESTURE_SWIPE_RIGHT_AND_UP = 11` |  |
| `static final int GESTURE_SWIPE_UP = 1` |  |
| `static final int GESTURE_SWIPE_UP_AND_DOWN = 7` |  |
| `static final int GESTURE_SWIPE_UP_AND_LEFT = 13` |  |
| `static final int GESTURE_SWIPE_UP_AND_RIGHT = 14` |  |
| `static final int GLOBAL_ACTION_BACK = 1` |  |
| `static final int GLOBAL_ACTION_HOME = 2` |  |
| `static final int GLOBAL_ACTION_LOCK_SCREEN = 8` |  |
| `static final int GLOBAL_ACTION_NOTIFICATIONS = 4` |  |
| `static final int GLOBAL_ACTION_POWER_DIALOG = 6` |  |
| `static final int GLOBAL_ACTION_QUICK_SETTINGS = 5` |  |
| `static final int GLOBAL_ACTION_RECENTS = 3` |  |
| `static final int GLOBAL_ACTION_TAKE_SCREENSHOT = 9` |  |
| `static final int GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN = 7` |  |
| `static final String SERVICE_INTERFACE = "android.accessibilityservice.AccessibilityService"` |  |
| `static final String SERVICE_META_DATA = "android.accessibilityservice"` |  |
| `static final int SHOW_MODE_AUTO = 0` |  |
| `static final int SHOW_MODE_HIDDEN = 1` |  |
| `static final int SHOW_MODE_IGNORE_HARD_KEYBOARD = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void disableSelf()` |  |
| `final boolean dispatchGesture(@NonNull android.accessibilityservice.GestureDescription, @Nullable android.accessibilityservice.AccessibilityService.GestureResultCallback, @Nullable android.os.Handler)` |  |
| `android.view.accessibility.AccessibilityNodeInfo findFocus(int)` |  |
| `android.view.accessibility.AccessibilityNodeInfo getRootInActiveWindow()` |  |
| `final android.accessibilityservice.AccessibilityServiceInfo getServiceInfo()` |  |
| `java.util.List<android.view.accessibility.AccessibilityWindowInfo> getWindows()` |  |
| `abstract void onAccessibilityEvent(android.view.accessibility.AccessibilityEvent)` |  |
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `boolean onGesture(@NonNull android.accessibilityservice.AccessibilityGestureEvent)` |  |
| `abstract void onInterrupt()` |  |
| `boolean onKeyEvent(android.view.KeyEvent)` |  |
| `void onServiceConnected()` |  |
| `void onSystemActionsChanged()` |  |
| `final boolean performGlobalAction(int)` |  |
| `void setGestureDetectionPassthroughRegion(int, @NonNull android.graphics.Region)` |  |
| `final void setServiceInfo(android.accessibilityservice.AccessibilityServiceInfo)` |  |
| `void setTouchExplorationPassthroughRegion(int, @NonNull android.graphics.Region)` |  |
| `void takeScreenshot(int, @NonNull java.util.concurrent.Executor, @NonNull android.accessibilityservice.AccessibilityService.TakeScreenshotCallback)` |  |
| `void onCancelled(android.accessibilityservice.GestureDescription)` |  |
| `void onCompleted(android.accessibilityservice.GestureDescription)` |  |

---

### `class static final AccessibilityService.MagnificationController`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addListener(@NonNull android.accessibilityservice.AccessibilityService.MagnificationController.OnMagnificationChangedListener)` |  |
| `void addListener(@NonNull android.accessibilityservice.AccessibilityService.MagnificationController.OnMagnificationChangedListener, @Nullable android.os.Handler)` |  |
| `float getCenterX()` |  |
| `float getCenterY()` |  |
| `float getScale()` |  |
| `boolean removeListener(@NonNull android.accessibilityservice.AccessibilityService.MagnificationController.OnMagnificationChangedListener)` |  |
| `boolean reset(boolean)` |  |
| `boolean setCenter(float, float, boolean)` |  |
| `boolean setScale(float, boolean)` |  |

---

### `interface static AccessibilityService.MagnificationController.OnMagnificationChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onMagnificationChanged(@NonNull android.accessibilityservice.AccessibilityService.MagnificationController, @NonNull android.graphics.Region, float, float, float)` |  |

---

### `class static final AccessibilityService.ScreenshotResult`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getTimestamp()` |  |

---

### `class static final AccessibilityService.SoftKeyboardController`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnShowModeChangedListener(@NonNull android.accessibilityservice.AccessibilityService.SoftKeyboardController.OnShowModeChangedListener)` |  |
| `void addOnShowModeChangedListener(@NonNull android.accessibilityservice.AccessibilityService.SoftKeyboardController.OnShowModeChangedListener, @Nullable android.os.Handler)` |  |
| `int getShowMode()` |  |
| `boolean removeOnShowModeChangedListener(@NonNull android.accessibilityservice.AccessibilityService.SoftKeyboardController.OnShowModeChangedListener)` |  |
| `boolean setShowMode(int)` |  |
| `boolean switchToInputMethod(@NonNull String)` |  |

---

### `interface static AccessibilityService.SoftKeyboardController.OnShowModeChangedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onShowModeChanged(@NonNull android.accessibilityservice.AccessibilityService.SoftKeyboardController, int)` |  |

---

### `interface static AccessibilityService.TakeScreenshotCallback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onFailure(int)` |  |
| `void onSuccess(@NonNull android.accessibilityservice.AccessibilityService.ScreenshotResult)` |  |

---

### `class AccessibilityServiceInfo`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccessibilityServiceInfo()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CAPABILITY_CAN_CONTROL_MAGNIFICATION = 16` |  |
| `static final int CAPABILITY_CAN_PERFORM_GESTURES = 32` |  |
| `static final int CAPABILITY_CAN_REQUEST_FILTER_KEY_EVENTS = 8` |  |
| `static final int CAPABILITY_CAN_REQUEST_FINGERPRINT_GESTURES = 64` |  |
| `static final int CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION = 2` |  |
| `static final int CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT = 1` |  |
| `static final int CAPABILITY_CAN_TAKE_SCREENSHOT = 128` |  |
| `static final int DEFAULT = 1` |  |
| `static final int FEEDBACK_ALL_MASK = -1` |  |
| `static final int FEEDBACK_AUDIBLE = 4` |  |
| `static final int FEEDBACK_BRAILLE = 32` |  |
| `static final int FEEDBACK_GENERIC = 16` |  |
| `static final int FEEDBACK_HAPTIC = 2` |  |
| `static final int FEEDBACK_SPOKEN = 1` |  |
| `static final int FEEDBACK_VISUAL = 8` |  |
| `static final int FLAG_ENABLE_ACCESSIBILITY_VOLUME = 128` |  |
| `static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 2` |  |
| `static final int FLAG_REPORT_VIEW_IDS = 16` |  |
| `static final int FLAG_REQUEST_ACCESSIBILITY_BUTTON = 256` |  |
| `static final int FLAG_REQUEST_FILTER_KEY_EVENTS = 32` |  |
| `static final int FLAG_REQUEST_FINGERPRINT_GESTURES = 512` |  |
| `static final int FLAG_REQUEST_MULTI_FINGER_GESTURES = 4096` |  |
| `static final int FLAG_REQUEST_SHORTCUT_WARNING_DIALOG_SPOKEN_FEEDBACK = 1024` |  |
| `static final int FLAG_REQUEST_TOUCH_EXPLORATION_MODE = 4` |  |
| `static final int FLAG_RETRIEVE_INTERACTIVE_WINDOWS = 64` |  |
| `static final int FLAG_SERVICE_HANDLES_DOUBLE_TAP = 2048` |  |
| `int eventTypes` |  |
| `int feedbackType` |  |
| `int flags` |  |
| `long notificationTimeout` |  |
| `String[] packageNames` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String capabilityToString(int)` |  |
| `int describeContents()` |  |
| `static String feedbackTypeToString(int)` |  |
| `static String flagToString(int)` |  |
| `int getCapabilities()` |  |
| `String getId()` |  |
| `int getInteractiveUiTimeoutMillis()` |  |
| `int getNonInteractiveUiTimeoutMillis()` |  |
| `android.content.pm.ResolveInfo getResolveInfo()` |  |
| `String getSettingsActivityName()` |  |
| `String loadDescription(android.content.pm.PackageManager)` |  |
| `CharSequence loadSummary(android.content.pm.PackageManager)` |  |
| `void setInteractiveUiTimeoutMillis(@IntRange(from=0) int)` |  |
| `void setNonInteractiveUiTimeoutMillis(@IntRange(from=0) int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final FingerprintGestureController`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FingerprintGestureController.FingerprintGestureCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FINGERPRINT_GESTURE_SWIPE_DOWN = 8` |  |
| `static final int FINGERPRINT_GESTURE_SWIPE_LEFT = 2` |  |
| `static final int FINGERPRINT_GESTURE_SWIPE_RIGHT = 1` |  |
| `static final int FINGERPRINT_GESTURE_SWIPE_UP = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isGestureDetectionAvailable()` |  |
| `void registerFingerprintGestureCallback(@NonNull android.accessibilityservice.FingerprintGestureController.FingerprintGestureCallback, @Nullable android.os.Handler)` |  |
| `void unregisterFingerprintGestureCallback(android.accessibilityservice.FingerprintGestureController.FingerprintGestureCallback)` |  |
| `void onGestureDetected(int)` |  |
| `void onGestureDetectionAvailabilityChanged(boolean)` |  |

---

### `class final GestureDescription`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getDisplayId()` |  |
| `static long getMaxGestureDuration()` |  |
| `static int getMaxStrokeCount()` |  |
| `android.accessibilityservice.GestureDescription.StrokeDescription getStroke(@IntRange(from=0) int)` |  |
| `int getStrokeCount()` |  |

---

### `class static GestureDescription.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GestureDescription.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.accessibilityservice.GestureDescription.Builder addStroke(@NonNull android.accessibilityservice.GestureDescription.StrokeDescription)` |  |
| `android.accessibilityservice.GestureDescription build()` |  |

---

### `class static GestureDescription.StrokeDescription`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GestureDescription.StrokeDescription(@NonNull android.graphics.Path, @IntRange(from=0) long, @IntRange(from=0) long)` |  |
| `GestureDescription.StrokeDescription(@NonNull android.graphics.Path, @IntRange(from=0) long, @IntRange(from=0) long, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.accessibilityservice.GestureDescription.StrokeDescription continueStroke(android.graphics.Path, long, long, boolean)` |  |
| `long getDuration()` |  |
| `android.graphics.Path getPath()` |  |
| `long getStartTime()` |  |
| `boolean willContinue()` |  |

---

## Package: `android.accounts`

### `class abstract AbstractAccountAuthenticator`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractAccountAuthenticator(android.content.Context)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String KEY_CUSTOM_TOKEN_EXPIRY = "android.accounts.expiry"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract android.os.Bundle addAccount(android.accounts.AccountAuthenticatorResponse, String, String, String[], android.os.Bundle) throws android.accounts.NetworkErrorException` |  |
| `android.os.Bundle addAccountFromCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, android.os.Bundle) throws android.accounts.NetworkErrorException` |  |
| `abstract android.os.Bundle confirmCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, android.os.Bundle) throws android.accounts.NetworkErrorException` |  |
| `abstract android.os.Bundle editProperties(android.accounts.AccountAuthenticatorResponse, String)` |  |
| `android.os.Bundle finishSession(android.accounts.AccountAuthenticatorResponse, String, android.os.Bundle) throws android.accounts.NetworkErrorException` |  |
| `android.os.Bundle getAccountCredentialsForCloning(android.accounts.AccountAuthenticatorResponse, android.accounts.Account) throws android.accounts.NetworkErrorException` |  |
| `android.os.Bundle getAccountRemovalAllowed(android.accounts.AccountAuthenticatorResponse, android.accounts.Account) throws android.accounts.NetworkErrorException` |  |
| `abstract android.os.Bundle getAuthToken(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, String, android.os.Bundle) throws android.accounts.NetworkErrorException` |  |
| `abstract String getAuthTokenLabel(String)` |  |
| `final android.os.IBinder getIBinder()` |  |
| `abstract android.os.Bundle hasFeatures(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, String[]) throws android.accounts.NetworkErrorException` |  |
| `android.os.Bundle isCredentialsUpdateSuggested(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, String) throws android.accounts.NetworkErrorException` |  |
| `android.os.Bundle startAddAccountSession(android.accounts.AccountAuthenticatorResponse, String, String, String[], android.os.Bundle) throws android.accounts.NetworkErrorException` |  |
| `android.os.Bundle startUpdateCredentialsSession(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, String, android.os.Bundle) throws android.accounts.NetworkErrorException` |  |
| `abstract android.os.Bundle updateCredentials(android.accounts.AccountAuthenticatorResponse, android.accounts.Account, String, android.os.Bundle) throws android.accounts.NetworkErrorException` |  |

---

### `class Account`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Account(String, String)` |  |
| `Account(android.os.Parcel)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final String name` |  |
| `final String type` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class AccountAuthenticatorActivity` ~~DEPRECATED~~

- **Extends:** `android.app.Activity`
- **Annotations:** `@Deprecated`

---

### `class AccountAuthenticatorResponse`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccountAuthenticatorResponse(android.os.Parcel)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void onError(int, String)` |  |
| `void onRequestContinued()` |  |
| `void onResult(android.os.Bundle)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class AccountManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_ACCOUNT_REMOVED = "android.accounts.action.ACCOUNT_REMOVED"` |  |
| `static final String ACTION_AUTHENTICATOR_INTENT = "android.accounts.AccountAuthenticator"` |  |
| `static final String AUTHENTICATOR_ATTRIBUTES_NAME = "account-authenticator"` |  |
| `static final String AUTHENTICATOR_META_DATA_NAME = "android.accounts.AccountAuthenticator"` |  |
| `static final int ERROR_CODE_BAD_ARGUMENTS = 7` |  |
| `static final int ERROR_CODE_BAD_AUTHENTICATION = 9` |  |
| `static final int ERROR_CODE_BAD_REQUEST = 8` |  |
| `static final int ERROR_CODE_CANCELED = 4` |  |
| `static final int ERROR_CODE_INVALID_RESPONSE = 5` |  |
| `static final int ERROR_CODE_NETWORK_ERROR = 3` |  |
| `static final int ERROR_CODE_REMOTE_EXCEPTION = 1` |  |
| `static final int ERROR_CODE_UNSUPPORTED_OPERATION = 6` |  |
| `static final String KEY_ACCOUNTS = "accounts"` |  |
| `static final String KEY_ACCOUNT_AUTHENTICATOR_RESPONSE = "accountAuthenticatorResponse"` |  |
| `static final String KEY_ACCOUNT_MANAGER_RESPONSE = "accountManagerResponse"` |  |
| `static final String KEY_ACCOUNT_NAME = "authAccount"` |  |
| `static final String KEY_ACCOUNT_SESSION_BUNDLE = "accountSessionBundle"` |  |
| `static final String KEY_ACCOUNT_STATUS_TOKEN = "accountStatusToken"` |  |
| `static final String KEY_ACCOUNT_TYPE = "accountType"` |  |
| `static final String KEY_ANDROID_PACKAGE_NAME = "androidPackageName"` |  |
| `static final String KEY_AUTHENTICATOR_TYPES = "authenticator_types"` |  |
| `static final String KEY_AUTHTOKEN = "authtoken"` |  |
| `static final String KEY_AUTH_FAILED_MESSAGE = "authFailedMessage"` |  |
| `static final String KEY_AUTH_TOKEN_LABEL = "authTokenLabelKey"` |  |
| `static final String KEY_BOOLEAN_RESULT = "booleanResult"` |  |
| `static final String KEY_CALLER_PID = "callerPid"` |  |
| `static final String KEY_CALLER_UID = "callerUid"` |  |
| `static final String KEY_ERROR_CODE = "errorCode"` |  |
| `static final String KEY_ERROR_MESSAGE = "errorMessage"` |  |
| `static final String KEY_INTENT = "intent"` |  |
| `static final String KEY_LAST_AUTHENTICATED_TIME = "lastAuthenticatedTime"` |  |
| `static final String KEY_PASSWORD = "password"` |  |
| `static final String KEY_USERDATA = "userdata"` |  |
| `static final String PACKAGE_NAME_KEY_LEGACY_NOT_VISIBLE = "android:accounts:key_legacy_not_visible"` |  |
| `static final String PACKAGE_NAME_KEY_LEGACY_VISIBLE = "android:accounts:key_legacy_visible"` |  |
| `static final int VISIBILITY_NOT_VISIBLE = 3` |  |
| `static final int VISIBILITY_UNDEFINED = 0` |  |
| `static final int VISIBILITY_USER_MANAGED_NOT_VISIBLE = 4` |  |
| `static final int VISIBILITY_USER_MANAGED_VISIBLE = 2` |  |
| `static final int VISIBILITY_VISIBLE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addAccountExplicitly(android.accounts.Account, String, android.os.Bundle, java.util.Map<java.lang.String,java.lang.Integer>)` |  |
| `void addOnAccountsUpdatedListener(android.accounts.OnAccountsUpdateListener, android.os.Handler, boolean, String[])` |  |
| `android.accounts.AccountManagerFuture<android.os.Bundle> finishSession(android.os.Bundle, android.app.Activity, android.accounts.AccountManagerCallback<android.os.Bundle>, android.os.Handler)` |  |
| `static android.accounts.AccountManager get(android.content.Context)` |  |
| `int getAccountVisibility(android.accounts.Account, String)` |  |
| `java.util.Map<android.accounts.Account,java.lang.Integer> getAccountsAndVisibilityForPackage(String, String)` |  |
| `android.accounts.AccountManagerFuture<android.accounts.Account[]> getAccountsByTypeAndFeatures(String, String[], android.accounts.AccountManagerCallback<android.accounts.Account[]>, android.os.Handler)` |  |
| `android.accounts.AuthenticatorDescription[] getAuthenticatorTypes()` |  |
| `java.util.Map<java.lang.String,java.lang.Integer> getPackagesAndVisibilityForAccount(android.accounts.Account)` |  |
| `String getPreviousName(android.accounts.Account)` |  |
| `android.accounts.AccountManagerFuture<java.lang.Boolean> hasFeatures(android.accounts.Account, String[], android.accounts.AccountManagerCallback<java.lang.Boolean>, android.os.Handler)` |  |
| `android.accounts.AccountManagerFuture<java.lang.Boolean> isCredentialsUpdateSuggested(android.accounts.Account, String, android.accounts.AccountManagerCallback<java.lang.Boolean>, android.os.Handler)` |  |
| `static android.content.Intent newChooseAccountIntent(android.accounts.Account, java.util.List<android.accounts.Account>, String[], String, String, String[], android.os.Bundle)` |  |
| `void removeOnAccountsUpdatedListener(android.accounts.OnAccountsUpdateListener)` |  |
| `boolean setAccountVisibility(android.accounts.Account, String, int)` |  |
| `android.accounts.AccountManagerFuture<android.os.Bundle> startAddAccountSession(String, String, String[], android.os.Bundle, android.app.Activity, android.accounts.AccountManagerCallback<android.os.Bundle>, android.os.Handler)` |  |
| `android.accounts.AccountManagerFuture<android.os.Bundle> startUpdateCredentialsSession(android.accounts.Account, String, android.os.Bundle, android.app.Activity, android.accounts.AccountManagerCallback<android.os.Bundle>, android.os.Handler)` |  |

---

### `interface AccountManagerCallback<V>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void run(android.accounts.AccountManagerFuture<V>)` |  |

---

### `interface AccountManagerFuture<V>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean cancel(boolean)` |  |
| `V getResult() throws android.accounts.AuthenticatorException, java.io.IOException, android.accounts.OperationCanceledException` |  |
| `V getResult(long, java.util.concurrent.TimeUnit) throws android.accounts.AuthenticatorException, java.io.IOException, android.accounts.OperationCanceledException` |  |
| `boolean isCancelled()` |  |
| `boolean isDone()` |  |

---

### `class AccountsException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AccountsException()` |  |
| `AccountsException(String)` |  |
| `AccountsException(String, Throwable)` |  |
| `AccountsException(Throwable)` |  |

---

### `class AuthenticatorDescription`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AuthenticatorDescription(String, String, int, int, int, int, boolean)` |  |
| `AuthenticatorDescription(String, String, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final int accountPreferencesId` |  |
| `final boolean customTokens` |  |
| `final int iconId` |  |
| `final int labelId` |  |
| `final String packageName` |  |
| `final int smallIconId` |  |
| `final String type` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static android.accounts.AuthenticatorDescription newKey(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class AuthenticatorException`

- **Extends:** `android.accounts.AccountsException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AuthenticatorException()` |  |
| `AuthenticatorException(String)` |  |
| `AuthenticatorException(String, Throwable)` |  |
| `AuthenticatorException(Throwable)` |  |

---

### `class NetworkErrorException`

- **Extends:** `android.accounts.AccountsException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NetworkErrorException()` |  |
| `NetworkErrorException(String)` |  |
| `NetworkErrorException(String, Throwable)` |  |
| `NetworkErrorException(Throwable)` |  |

---

### `interface OnAccountsUpdateListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAccountsUpdated(android.accounts.Account[])` |  |

---

### `class OperationCanceledException`

- **Extends:** `android.accounts.AccountsException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OperationCanceledException()` |  |
| `OperationCanceledException(String)` |  |
| `OperationCanceledException(String, Throwable)` |  |
| `OperationCanceledException(Throwable)` |  |

---

## Package: `android.animation`

### `class abstract Animator`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Animator()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final long DURATION_INFINITE = -1L` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addListener(android.animation.Animator.AnimatorListener)` |  |
| `void addPauseListener(android.animation.Animator.AnimatorPauseListener)` |  |
| `void cancel()` |  |
| `android.animation.Animator clone()` |  |
| `void end()` |  |
| `abstract long getDuration()` |  |
| `android.animation.TimeInterpolator getInterpolator()` |  |
| `java.util.ArrayList<android.animation.Animator.AnimatorListener> getListeners()` |  |
| `abstract long getStartDelay()` |  |
| `long getTotalDuration()` |  |
| `boolean isPaused()` |  |
| `abstract boolean isRunning()` |  |
| `boolean isStarted()` |  |
| `void pause()` |  |
| `void removeAllListeners()` |  |
| `void removeListener(android.animation.Animator.AnimatorListener)` |  |
| `void removePauseListener(android.animation.Animator.AnimatorPauseListener)` |  |
| `void resume()` |  |
| `abstract android.animation.Animator setDuration(long)` |  |
| `abstract void setInterpolator(android.animation.TimeInterpolator)` |  |
| `abstract void setStartDelay(long)` |  |
| `void setTarget(@Nullable Object)` |  |
| `void setupEndValues()` |  |
| `void setupStartValues()` |  |
| `void start()` |  |

---

### `interface static Animator.AnimatorListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAnimationCancel(android.animation.Animator)` |  |
| `default void onAnimationEnd(android.animation.Animator, boolean)` |  |
| `void onAnimationEnd(android.animation.Animator)` |  |
| `void onAnimationRepeat(android.animation.Animator)` |  |
| `default void onAnimationStart(android.animation.Animator, boolean)` |  |
| `void onAnimationStart(android.animation.Animator)` |  |

---

### `interface static Animator.AnimatorPauseListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAnimationPause(android.animation.Animator)` |  |
| `void onAnimationResume(android.animation.Animator)` |  |

---

### `class AnimatorInflater`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AnimatorInflater()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.animation.Animator loadAnimator(android.content.Context, @AnimatorRes int) throws android.content.res.Resources.NotFoundException` |  |
| `static android.animation.StateListAnimator loadStateListAnimator(android.content.Context, int) throws android.content.res.Resources.NotFoundException` |  |

---

### `class abstract AnimatorListenerAdapter`

- **Implements:** `android.animation.Animator.AnimatorListener android.animation.Animator.AnimatorPauseListener`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AnimatorListenerAdapter()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAnimationCancel(android.animation.Animator)` |  |
| `void onAnimationEnd(android.animation.Animator)` |  |
| `void onAnimationPause(android.animation.Animator)` |  |
| `void onAnimationRepeat(android.animation.Animator)` |  |
| `void onAnimationResume(android.animation.Animator)` |  |
| `void onAnimationStart(android.animation.Animator)` |  |

---

### `class final AnimatorSet`

- **Extends:** `android.animation.Animator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AnimatorSet()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.animation.AnimatorSet clone()` |  |
| `java.util.ArrayList<android.animation.Animator> getChildAnimations()` |  |
| `long getCurrentPlayTime()` |  |
| `long getDuration()` |  |
| `long getStartDelay()` |  |
| `boolean isRunning()` |  |
| `android.animation.AnimatorSet.Builder play(android.animation.Animator)` |  |
| `void playSequentially(android.animation.Animator...)` |  |
| `void playSequentially(java.util.List<android.animation.Animator>)` |  |
| `void playTogether(android.animation.Animator...)` |  |
| `void playTogether(java.util.Collection<android.animation.Animator>)` |  |
| `void reverse()` |  |
| `void setCurrentPlayTime(long)` |  |
| `android.animation.AnimatorSet setDuration(long)` |  |
| `void setInterpolator(android.animation.TimeInterpolator)` |  |
| `void setStartDelay(long)` |  |

---

### `class AnimatorSet.Builder`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.animation.AnimatorSet.Builder after(android.animation.Animator)` |  |
| `android.animation.AnimatorSet.Builder after(long)` |  |
| `android.animation.AnimatorSet.Builder before(android.animation.Animator)` |  |
| `android.animation.AnimatorSet.Builder with(android.animation.Animator)` |  |

---

### `class ArgbEvaluator`

- **Implements:** `android.animation.TypeEvaluator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ArgbEvaluator()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object evaluate(float, Object, Object)` |  |

---

### `class abstract BidirectionalTypeConverter<T, V>`

- **Extends:** `android.animation.TypeConverter<T,V>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BidirectionalTypeConverter(Class<T>, Class<V>)` |  |
| `FloatArrayEvaluator()` |  |
| `FloatArrayEvaluator(float[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract T convertBack(V)` |  |
| `android.animation.BidirectionalTypeConverter<V,T> invert()` |  |
| `float[] evaluate(float, float[], float[])` |  |

---

### `class FloatEvaluator`

- **Implements:** `android.animation.TypeEvaluator<java.lang.Number>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FloatEvaluator()` |  |
| `IntArrayEvaluator()` |  |
| `IntArrayEvaluator(int[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Float evaluate(float, Number, Number)` |  |
| `int[] evaluate(float, int[], int[])` |  |

---

### `class IntEvaluator`

- **Implements:** `android.animation.TypeEvaluator<java.lang.Integer>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IntEvaluator()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Integer evaluate(float, Integer, Integer)` |  |

---

### `class abstract Keyframe`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Keyframe()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract android.animation.Keyframe clone()` |  |
| `float getFraction()` |  |
| `android.animation.TimeInterpolator getInterpolator()` |  |
| `Class getType()` |  |
| `abstract Object getValue()` |  |
| `boolean hasValue()` |  |
| `static android.animation.Keyframe ofFloat(float, float)` |  |
| `static android.animation.Keyframe ofFloat(float)` |  |
| `static android.animation.Keyframe ofInt(float, int)` |  |
| `static android.animation.Keyframe ofInt(float)` |  |
| `static android.animation.Keyframe ofObject(float, Object)` |  |
| `static android.animation.Keyframe ofObject(float)` |  |
| `void setFraction(float)` |  |
| `void setInterpolator(android.animation.TimeInterpolator)` |  |
| `abstract void setValue(Object)` |  |

---

### `class LayoutTransition`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LayoutTransition()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int APPEARING = 2` |  |
| `static final int CHANGE_APPEARING = 0` |  |
| `static final int CHANGE_DISAPPEARING = 1` |  |
| `static final int CHANGING = 4` |  |
| `static final int DISAPPEARING = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addChild(android.view.ViewGroup, android.view.View)` |  |
| `void addTransitionListener(android.animation.LayoutTransition.TransitionListener)` |  |
| `void disableTransitionType(int)` |  |
| `void enableTransitionType(int)` |  |
| `android.animation.Animator getAnimator(int)` |  |
| `long getDuration(int)` |  |
| `android.animation.TimeInterpolator getInterpolator(int)` |  |
| `long getStagger(int)` |  |
| `long getStartDelay(int)` |  |
| `java.util.List<android.animation.LayoutTransition.TransitionListener> getTransitionListeners()` |  |
| `void hideChild(android.view.ViewGroup, android.view.View, int)` |  |
| `boolean isChangingLayout()` |  |
| `boolean isRunning()` |  |
| `boolean isTransitionTypeEnabled(int)` |  |
| `void removeChild(android.view.ViewGroup, android.view.View)` |  |
| `void removeTransitionListener(android.animation.LayoutTransition.TransitionListener)` |  |
| `void setAnimateParentHierarchy(boolean)` |  |
| `void setAnimator(int, android.animation.Animator)` |  |
| `void setDuration(long)` |  |
| `void setDuration(int, long)` |  |
| `void setInterpolator(int, android.animation.TimeInterpolator)` |  |
| `void setStagger(int, long)` |  |
| `void setStartDelay(int, long)` |  |
| `void showChild(android.view.ViewGroup, android.view.View, int)` |  |

---

### `interface static LayoutTransition.TransitionListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void endTransition(android.animation.LayoutTransition, android.view.ViewGroup, android.view.View, int)` |  |
| `void startTransition(android.animation.LayoutTransition, android.view.ViewGroup, android.view.View, int)` |  |

---

### `class final ObjectAnimator`

- **Extends:** `android.animation.ValueAnimator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ObjectAnimator()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.animation.ObjectAnimator clone()` |  |
| `static android.animation.ObjectAnimator ofArgb(Object, String, int...)` |  |
| `static <T> android.animation.ObjectAnimator ofArgb(T, android.util.Property<T,java.lang.Integer>, int...)` |  |
| `static android.animation.ObjectAnimator ofFloat(Object, String, float...)` |  |
| `static android.animation.ObjectAnimator ofFloat(Object, String, String, android.graphics.Path)` |  |
| `static <T> android.animation.ObjectAnimator ofFloat(T, android.util.Property<T,java.lang.Float>, float...)` |  |
| `static <T> android.animation.ObjectAnimator ofFloat(T, android.util.Property<T,java.lang.Float>, android.util.Property<T,java.lang.Float>, android.graphics.Path)` |  |
| `static android.animation.ObjectAnimator ofInt(Object, String, int...)` |  |
| `static android.animation.ObjectAnimator ofInt(Object, String, String, android.graphics.Path)` |  |
| `static <T> android.animation.ObjectAnimator ofInt(T, android.util.Property<T,java.lang.Integer>, int...)` |  |
| `static <T> android.animation.ObjectAnimator ofInt(T, android.util.Property<T,java.lang.Integer>, android.util.Property<T,java.lang.Integer>, android.graphics.Path)` |  |
| `static android.animation.ObjectAnimator ofMultiFloat(Object, String, float[][])` |  |
| `static android.animation.ObjectAnimator ofMultiFloat(Object, String, android.graphics.Path)` |  |
| `static android.animation.ObjectAnimator ofMultiInt(Object, String, int[][])` |  |
| `static android.animation.ObjectAnimator ofMultiInt(Object, String, android.graphics.Path)` |  |
| `static android.animation.ObjectAnimator ofObject(Object, String, android.animation.TypeEvaluator, java.lang.Object...)` |  |
| `void setAutoCancel(boolean)` |  |
| `void setProperty(@NonNull android.util.Property)` |  |
| `void setPropertyName(@NonNull String)` |  |

---

### `class PointFEvaluator`

- **Implements:** `android.animation.TypeEvaluator<android.graphics.PointF>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PointFEvaluator()` |  |
| `PointFEvaluator(android.graphics.PointF)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.graphics.PointF evaluate(float, android.graphics.PointF, android.graphics.PointF)` |  |

---

### `class PropertyValuesHolder`

- **Implements:** `java.lang.Cloneable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.animation.PropertyValuesHolder clone()` |  |
| `String getPropertyName()` |  |
| `static android.animation.PropertyValuesHolder ofFloat(String, float...)` |  |
| `static android.animation.PropertyValuesHolder ofFloat(android.util.Property<?,java.lang.Float>, float...)` |  |
| `static android.animation.PropertyValuesHolder ofInt(String, int...)` |  |
| `static android.animation.PropertyValuesHolder ofInt(android.util.Property<?,java.lang.Integer>, int...)` |  |
| `static android.animation.PropertyValuesHolder ofKeyframe(String, android.animation.Keyframe...)` |  |
| `static android.animation.PropertyValuesHolder ofKeyframe(android.util.Property, android.animation.Keyframe...)` |  |
| `static android.animation.PropertyValuesHolder ofMultiFloat(String, float[][])` |  |
| `static android.animation.PropertyValuesHolder ofMultiFloat(String, android.graphics.Path)` |  |
| `static <T> android.animation.PropertyValuesHolder ofMultiFloat(String, android.animation.TypeConverter<T,float[]>, android.animation.TypeEvaluator<T>, android.animation.Keyframe...)` |  |
| `static android.animation.PropertyValuesHolder ofMultiInt(String, int[][])` |  |
| `static android.animation.PropertyValuesHolder ofMultiInt(String, android.graphics.Path)` |  |
| `static <T> android.animation.PropertyValuesHolder ofMultiInt(String, android.animation.TypeConverter<T,int[]>, android.animation.TypeEvaluator<T>, android.animation.Keyframe...)` |  |
| `static android.animation.PropertyValuesHolder ofObject(String, android.animation.TypeEvaluator, java.lang.Object...)` |  |
| `static android.animation.PropertyValuesHolder ofObject(String, android.animation.TypeConverter<android.graphics.PointF,?>, android.graphics.Path)` |  |
| `static <V> android.animation.PropertyValuesHolder ofObject(android.util.Property<?,V>, android.animation.TypeConverter<android.graphics.PointF,V>, android.graphics.Path)` |  |
| `void setConverter(android.animation.TypeConverter)` |  |
| `void setEvaluator(android.animation.TypeEvaluator)` |  |
| `void setFloatValues(float...)` |  |
| `void setIntValues(int...)` |  |
| `void setKeyframes(android.animation.Keyframe...)` |  |
| `void setObjectValues(java.lang.Object...)` |  |
| `void setProperty(android.util.Property)` |  |
| `void setPropertyName(String)` |  |

---

### `class RectEvaluator`

- **Implements:** `android.animation.TypeEvaluator<android.graphics.Rect>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RectEvaluator()` |  |
| `RectEvaluator(android.graphics.Rect)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.graphics.Rect evaluate(float, android.graphics.Rect, android.graphics.Rect)` |  |

---

### `class StateListAnimator`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StateListAnimator()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addState(int[], android.animation.Animator)` |  |
| `android.animation.StateListAnimator clone()` |  |
| `void jumpToCurrentState()` |  |

---

### `class TimeAnimator`

- **Extends:** `android.animation.ValueAnimator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TimeAnimator()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void setTimeListener(android.animation.TimeAnimator.TimeListener)` |  |

---

### `interface static TimeAnimator.TimeListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onTimeUpdate(android.animation.TimeAnimator, long, long)` |  |

---

### `interface TimeInterpolator`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getInterpolation(float)` |  |

---

### `class abstract TypeConverter<T, V>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TypeConverter(Class<T>, Class<V>)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract V convert(T)` |  |

---

### `interface TypeEvaluator<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T evaluate(float, T, T)` |  |

---

### `class ValueAnimator`

- **Extends:** `android.animation.Animator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ValueAnimator()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int INFINITE = -1` |  |
| `static final int RESTART = 1` |  |
| `static final int REVERSE = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addUpdateListener(android.animation.ValueAnimator.AnimatorUpdateListener)` |  |
| `static boolean areAnimatorsEnabled()` |  |
| `android.animation.ValueAnimator clone()` |  |
| `float getAnimatedFraction()` |  |
| `Object getAnimatedValue()` |  |
| `Object getAnimatedValue(String)` |  |
| `long getCurrentPlayTime()` |  |
| `long getDuration()` |  |
| `static long getFrameDelay()` |  |
| `int getRepeatCount()` |  |
| `int getRepeatMode()` |  |
| `long getStartDelay()` |  |
| `android.animation.PropertyValuesHolder[] getValues()` |  |
| `boolean isRunning()` |  |
| `static android.animation.ValueAnimator ofArgb(int...)` |  |
| `static android.animation.ValueAnimator ofFloat(float...)` |  |
| `static android.animation.ValueAnimator ofInt(int...)` |  |
| `static android.animation.ValueAnimator ofObject(android.animation.TypeEvaluator, java.lang.Object...)` |  |
| `static android.animation.ValueAnimator ofPropertyValuesHolder(android.animation.PropertyValuesHolder...)` |  |
| `void removeAllUpdateListeners()` |  |
| `void removeUpdateListener(android.animation.ValueAnimator.AnimatorUpdateListener)` |  |
| `void reverse()` |  |
| `void setCurrentFraction(float)` |  |
| `void setCurrentPlayTime(long)` |  |
| `android.animation.ValueAnimator setDuration(long)` |  |
| `void setEvaluator(android.animation.TypeEvaluator)` |  |
| `void setFloatValues(float...)` |  |
| `static void setFrameDelay(long)` |  |
| `void setIntValues(int...)` |  |
| `void setInterpolator(android.animation.TimeInterpolator)` |  |
| `void setObjectValues(java.lang.Object...)` |  |
| `void setRepeatCount(int)` |  |
| `void setRepeatMode(int)` |  |
| `void setStartDelay(long)` |  |
| `void setValues(android.animation.PropertyValuesHolder...)` |  |

---

### `interface static ValueAnimator.AnimatorUpdateListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAnimationUpdate(android.animation.ValueAnimator)` |  |

---

## Package: `android.bluetooth`

### `class final BluetoothA2dp`

- **Implements:** `android.bluetooth.BluetoothProfile`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED"` |  |
| `static final String ACTION_PLAYING_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED"` |  |
| `static final int STATE_NOT_PLAYING = 11` |  |
| `static final int STATE_PLAYING = 10` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finalize()` |  |
| `java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices()` |  |
| `int getConnectionState(android.bluetooth.BluetoothDevice)` |  |
| `java.util.List<android.bluetooth.BluetoothDevice> getDevicesMatchingConnectionStates(int[])` |  |
| `boolean isA2dpPlaying(android.bluetooth.BluetoothDevice)` |  |

---

### `class final BluetoothAdapter`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED"` |  |
| `static final String ACTION_DISCOVERY_FINISHED = "android.bluetooth.adapter.action.DISCOVERY_FINISHED"` |  |
| `static final String ACTION_DISCOVERY_STARTED = "android.bluetooth.adapter.action.DISCOVERY_STARTED"` |  |
| `static final String ACTION_LOCAL_NAME_CHANGED = "android.bluetooth.adapter.action.LOCAL_NAME_CHANGED"` |  |
| `static final String ACTION_REQUEST_DISCOVERABLE = "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE"` |  |
| `static final String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE"` |  |
| `static final String ACTION_SCAN_MODE_CHANGED = "android.bluetooth.adapter.action.SCAN_MODE_CHANGED"` |  |
| `static final String ACTION_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED"` |  |
| `static final int ERROR = -2147483648` |  |
| `static final String EXTRA_CONNECTION_STATE = "android.bluetooth.adapter.extra.CONNECTION_STATE"` |  |
| `static final String EXTRA_DISCOVERABLE_DURATION = "android.bluetooth.adapter.extra.DISCOVERABLE_DURATION"` |  |
| `static final String EXTRA_LOCAL_NAME = "android.bluetooth.adapter.extra.LOCAL_NAME"` |  |
| `static final String EXTRA_PREVIOUS_CONNECTION_STATE = "android.bluetooth.adapter.extra.PREVIOUS_CONNECTION_STATE"` |  |
| `static final String EXTRA_PREVIOUS_SCAN_MODE = "android.bluetooth.adapter.extra.PREVIOUS_SCAN_MODE"` |  |
| `static final String EXTRA_PREVIOUS_STATE = "android.bluetooth.adapter.extra.PREVIOUS_STATE"` |  |
| `static final String EXTRA_SCAN_MODE = "android.bluetooth.adapter.extra.SCAN_MODE"` |  |
| `static final String EXTRA_STATE = "android.bluetooth.adapter.extra.STATE"` |  |
| `static final int SCAN_MODE_CONNECTABLE = 21` |  |
| `static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 23` |  |
| `static final int SCAN_MODE_NONE = 20` |  |
| `static final int STATE_CONNECTED = 2` |  |
| `static final int STATE_CONNECTING = 1` |  |
| `static final int STATE_DISCONNECTED = 0` |  |
| `static final int STATE_DISCONNECTING = 3` |  |
| `static final int STATE_OFF = 10` |  |
| `static final int STATE_ON = 12` |  |
| `static final int STATE_TURNING_OFF = 13` |  |
| `static final int STATE_TURNING_ON = 11` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static boolean checkBluetoothAddress(String)` |  |
| `void closeProfileProxy(int, android.bluetooth.BluetoothProfile)` |  |
| `android.bluetooth.le.BluetoothLeAdvertiser getBluetoothLeAdvertiser()` |  |
| `android.bluetooth.le.BluetoothLeScanner getBluetoothLeScanner()` |  |
| `static android.bluetooth.BluetoothAdapter getDefaultAdapter()` |  |
| `int getLeMaximumAdvertisingDataLength()` |  |
| `String getName()` |  |
| `boolean getProfileProxy(android.content.Context, android.bluetooth.BluetoothProfile.ServiceListener, int)` |  |
| `android.bluetooth.BluetoothDevice getRemoteDevice(String)` |  |
| `android.bluetooth.BluetoothDevice getRemoteDevice(byte[])` |  |
| `boolean isLe2MPhySupported()` |  |
| `boolean isLeCodedPhySupported()` |  |
| `boolean isLeExtendedAdvertisingSupported()` |  |
| `boolean isLePeriodicAdvertisingSupported()` |  |
| `boolean isMultipleAdvertisementSupported()` |  |
| `boolean isOffloadedFilteringSupported()` |  |
| `boolean isOffloadedScanBatchingSupported()` |  |

---

### `interface static BluetoothAdapter.LeScanCallback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onLeScan(android.bluetooth.BluetoothDevice, int, byte[])` |  |

---

### `class BluetoothAssignedNumbers`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AAMP_OF_AMERICA = 190` |  |
| `static final int ACCEL_SEMICONDUCTOR = 74` |  |
| `static final int ACE_SENSOR = 188` |  |
| `static final int ADIDAS = 195` |  |
| `static final int ADVANCED_PANMOBIL_SYSTEMS = 145` |  |
| `static final int AIROHA_TECHNOLOGY = 148` |  |
| `static final int ALCATEL = 36` |  |
| `static final int ALPWISE = 154` |  |
| `static final int AMICCOM_ELECTRONICS = 192` |  |
| `static final int APLIX = 189` |  |
| `static final int APPLE = 76` |  |
| `static final int APT_LICENSING = 79` |  |
| `static final int ARCHOS = 207` |  |
| `static final int ARP_DEVICES = 168` |  |
| `static final int ATHEROS_COMMUNICATIONS = 69` |  |
| `static final int ATMEL = 19` |  |
| `static final int AUSTCO_COMMUNICATION_SYSTEMS = 213` |  |
| `static final int AUTONET_MOBILE = 127` |  |
| `static final int AVAGO = 78` |  |
| `static final int AVM_BERLIN = 31` |  |
| `static final int A_AND_D_ENGINEERING = 105` |  |
| `static final int A_AND_R_CAMBRIDGE = 124` |  |
| `static final int BANDSPEED = 32` |  |
| `static final int BAND_XI_INTERNATIONAL = 100` |  |
| `static final int BDE_TECHNOLOGY = 180` |  |
| `static final int BEATS_ELECTRONICS = 204` |  |
| `static final int BEAUTIFUL_ENTERPRISE = 108` |  |
| `static final int BEKEY = 178` |  |
| `static final int BELKIN_INTERNATIONAL = 92` |  |
| `static final int BINAURIC = 203` |  |
| `static final int BIOSENTRONICS = 219` |  |
| `static final int BLUEGIGA = 71` |  |
| `static final int BLUERADIOS = 133` |  |
| `static final int BLUETOOTH_SIG = 63` |  |
| `static final int BLUETREK_TECHNOLOGIES = 151` |  |
| `static final int BOSE = 158` |  |
| `static final int BRIARTEK = 109` |  |
| `static final int BROADCOM = 15` |  |
| `static final int CAEN_RFID = 170` |  |
| `static final int CAMBRIDGE_SILICON_RADIO = 10` |  |
| `static final int CATC = 52` |  |
| `static final int CINETIX = 175` |  |
| `static final int CLARINOX_TECHNOLOGIES = 179` |  |
| `static final int COLORFY = 156` |  |
| `static final int COMMIL = 51` |  |
| `static final int CONEXANT_SYSTEMS = 28` |  |
| `static final int CONNECTBLUE = 113` |  |
| `static final int CONTINENTAL_AUTOMOTIVE = 75` |  |
| `static final int CONWISE_TECHNOLOGY = 66` |  |
| `static final int CREATIVE_TECHNOLOGY = 118` |  |
| `static final int C_TECHNOLOGIES = 38` |  |
| `static final int DANLERS = 225` |  |
| `static final int DELORME_PUBLISHING_COMPANY = 128` |  |
| `static final int DEXCOM = 208` |  |
| `static final int DIALOG_SEMICONDUCTOR = 210` |  |
| `static final int DIGIANSWER = 12` |  |
| `static final int ECLIPSE = 53` |  |
| `static final int ECOTEST = 136` |  |
| `static final int ELGATO_SYSTEMS = 206` |  |
| `static final int EM_MICROELECTRONIC_MARIN = 90` |  |
| `static final int EQUINOX_AG = 134` |  |
| `static final int ERICSSON_TECHNOLOGY = 0` |  |
| `static final int EVLUMA = 201` |  |
| `static final int FREE2MOVE = 83` |  |
| `static final int FUNAI_ELECTRIC = 144` |  |
| `static final int GARMIN_INTERNATIONAL = 135` |  |
| `static final int GCT_SEMICONDUCTOR = 45` |  |
| `static final int GELO = 200` |  |
| `static final int GENEQ = 194` |  |
| `static final int GENERAL_MOTORS = 104` |  |
| `static final int GENNUM = 59` |  |
| `static final int GEOFORCE = 157` |  |
| `static final int GIBSON_GUITARS = 98` |  |
| `static final int GN_NETCOM = 103` |  |
| `static final int GN_RESOUND = 137` |  |
| `static final int GOOGLE = 224` |  |
| `static final int GREEN_THROTTLE_GAMES = 172` |  |
| `static final int GROUP_SENSE = 115` |  |
| `static final int HANLYNN_TECHNOLOGIES = 123` |  |
| `static final int HARMAN_INTERNATIONAL = 87` |  |
| `static final int HEWLETT_PACKARD = 101` |  |
| `static final int HITACHI = 41` |  |
| `static final int HOSIDEN = 221` |  |
| `static final int IBM = 3` |  |
| `static final int INFINEON_TECHNOLOGIES = 9` |  |
| `static final int INGENIEUR_SYSTEMGRUPPE_ZAHN = 171` |  |
| `static final int INTEGRATED_SILICON_SOLUTION = 65` |  |
| `static final int INTEGRATED_SYSTEM_SOLUTION = 57` |  |
| `static final int INTEL = 2` |  |
| `static final int INVENTEL = 30` |  |
| `static final int IPEXTREME = 61` |  |
| `static final int I_TECH_DYNAMIC_GLOBAL_DISTRIBUTION = 153` |  |
| `static final int JAWBONE = 138` |  |
| `static final int JIANGSU_TOPPOWER_AUTOMOTIVE_ELECTRONICS = 155` |  |
| `static final int JOHNSON_CONTROLS = 185` |  |
| `static final int J_AND_M = 82` |  |
| `static final int KAWANTECH = 212` |  |
| `static final int KC_TECHNOLOGY = 22` |  |
| `static final int KENSINGTON_COMPUTER_PRODUCTS_GROUP = 160` |  |
| `static final int LAIRD_TECHNOLOGIES = 119` |  |
| `static final int LESSWIRE = 121` |  |
| `static final int LG_ELECTRONICS = 196` |  |
| `static final int LINAK = 164` |  |
| `static final int LUCENT = 7` |  |
| `static final int LUDUS_HELSINKI = 132` |  |
| `static final int MACRONIX = 44` |  |
| `static final int MAGNETI_MARELLI = 169` |  |
| `static final int MANSELLA = 33` |  |
| `static final int MARVELL = 72` |  |
| `static final int MATSUSHITA_ELECTRIC = 58` |  |
| `static final int MC10 = 202` |  |
| `static final int MEDIATEK = 70` |  |
| `static final int MESO_INTERNATIONAL = 182` |  |
| `static final int META_WATCH = 163` |  |
| `static final int MEWTEL_TECHNOLOGY = 47` |  |
| `static final int MICOMMAND = 99` |  |
| `static final int MICROCHIP_TECHNOLOGY = 205` |  |
| `static final int MICROSOFT = 6` |  |
| `static final int MINDTREE = 106` |  |
| `static final int MISFIT_WEARABLES = 223` |  |
| `static final int MITEL_SEMICONDUCTOR = 16` |  |
| `static final int MITSUBISHI_ELECTRIC = 20` |  |
| `static final int MOBILIAN_CORPORATION = 55` |  |
| `static final int MONSTER = 112` |  |
| `static final int MOTOROLA = 8` |  |
| `static final int MSTAR_SEMICONDUCTOR = 122` |  |
| `static final int MUZIK = 222` |  |
| `static final int NEC = 34` |  |
| `static final int NEC_LIGHTING = 149` |  |
| `static final int NEWLOGIC = 23` |  |
| `static final int NIKE = 120` |  |
| `static final int NINE_SOLUTIONS = 102` |  |
| `static final int NOKIA_MOBILE_PHONES = 1` |  |
| `static final int NORDIC_SEMICONDUCTOR = 89` |  |
| `static final int NORWOOD_SYSTEMS = 46` |  |
| `static final int ODM_TECHNOLOGY = 150` |  |
| `static final int OMEGAWAVE = 174` |  |
| `static final int ONSET_COMPUTER = 197` |  |
| `static final int OPEN_INTERFACE = 39` |  |
| `static final int OTL_DYNAMICS = 165` |  |
| `static final int PANDA_OCEAN = 166` |  |
| `static final int PARROT = 67` |  |
| `static final int PARTHUS_TECHNOLOGIES = 14` |  |
| `static final int PASSIF_SEMICONDUCTOR = 176` |  |
| `static final int PETER_SYSTEMTECHNIK = 173` |  |
| `static final int PHILIPS_SEMICONDUCTORS = 37` |  |
| `static final int PLANTRONICS = 85` |  |
| `static final int POLAR_ELECTRO = 107` |  |
| `static final int POLAR_ELECTRO_EUROPE = 209` |  |
| `static final int PROCTER_AND_GAMBLE = 220` |  |
| `static final int QUALCOMM = 29` |  |
| `static final int QUALCOMM_CONNECTED_EXPERIENCES = 216` |  |
| `static final int QUALCOMM_INNOVATION_CENTER = 184` |  |
| `static final int QUALCOMM_LABS = 140` |  |
| `static final int QUALCOMM_TECHNOLOGIES = 215` |  |
| `static final int QUINTIC = 142` |  |
| `static final int QUUPPA = 199` |  |
| `static final int RALINK_TECHNOLOGY = 91` |  |
| `static final int RDA_MICROELECTRONICS = 97` |  |
| `static final int REALTEK_SEMICONDUCTOR = 93` |  |
| `static final int RED_M = 50` |  |
| `static final int RENESAS_TECHNOLOGY = 54` |  |
| `static final int RESEARCH_IN_MOTION = 60` |  |
| `static final int RF_MICRO_DEVICES = 40` |  |
| `static final int RIVIERAWAVES = 96` |  |
| `static final int ROHDE_AND_SCHWARZ = 25` |  |
| `static final int RTX_TELECOM = 21` |  |
| `static final int SAMSUNG_ELECTRONICS = 117` |  |
| `static final int SARIS_CYCLING_GROUP = 177` |  |
| `static final int SEERS_TECHNOLOGY = 125` |  |
| `static final int SEIKO_EPSON = 64` |  |
| `static final int SELFLY = 198` |  |
| `static final int SEMILINK = 226` |  |
| `static final int SENNHEISER_COMMUNICATIONS = 130` |  |
| `static final int SHANGHAI_SUPER_SMART_ELECTRONICS = 114` |  |
| `static final int SHENZHEN_EXCELSECU_DATA_TECHNOLOGY = 193` |  |
| `static final int SIGNIA_TECHNOLOGIES = 27` |  |
| `static final int SILICON_WAVE = 11` |  |
| `static final int SIRF_TECHNOLOGY = 80` |  |
| `static final int SOCKET_MOBILE = 68` |  |
| `static final int SONY_ERICSSON = 86` |  |
| `static final int SOUND_ID = 111` |  |
| `static final int SPORTS_TRACKING_TECHNOLOGIES = 126` |  |
| `static final int SR_MEDIZINELEKTRONIK = 161` |  |
| `static final int STACCATO_COMMUNICATIONS = 77` |  |
| `static final int STALMART_TECHNOLOGY = 191` |  |
| `static final int STARKEY_LABORATORIES = 186` |  |
| `static final int STOLLMAN_E_PLUS_V = 143` |  |
| `static final int STONESTREET_ONE = 94` |  |
| `static final int ST_MICROELECTRONICS = 48` |  |
| `static final int SUMMIT_DATA_COMMUNICATIONS = 110` |  |
| `static final int SUUNTO = 159` |  |
| `static final int SWIRL_NETWORKS = 181` |  |
| `static final int SYMBOL_TECHNOLOGIES = 42` |  |
| `static final int SYNOPSYS = 49` |  |
| `static final int SYSTEMS_AND_CHIPS = 62` |  |
| `static final int S_POWER_ELECTRONICS = 187` |  |
| `static final int TAIXINGBANG_TECHNOLOGY = 211` |  |
| `static final int TENOVIS = 43` |  |
| `static final int TERAX = 56` |  |
| `static final int TEXAS_INSTRUMENTS = 13` |  |
| `static final int THINKOPTICS = 146` |  |
| `static final int THREECOM = 5` |  |
| `static final int THREE_DIJOY = 84` |  |
| `static final int THREE_DSP = 73` |  |
| `static final int TIMEKEEPING_SYSTEMS = 131` |  |
| `static final int TIMEX_GROUP_USA = 214` |  |
| `static final int TOPCORN_POSITIONING_SYSTEMS = 139` |  |
| `static final int TOSHIBA = 4` |  |
| `static final int TRANSILICA = 24` |  |
| `static final int TRELAB = 183` |  |
| `static final int TTPCOM = 26` |  |
| `static final int TXTR = 218` |  |
| `static final int TZERO_TECHNOLOGIES = 81` |  |
| `static final int UNIVERSAL_ELECTRONICS = 147` |  |
| `static final int VERTU = 162` |  |
| `static final int VISTEON = 167` |  |
| `static final int VIZIO = 88` |  |
| `static final int VOYETRA_TURTLE_BEACH = 217` |  |
| `static final int WAVEPLUS_TECHNOLOGY = 35` |  |
| `static final int WICENTRIC = 95` |  |
| `static final int WIDCOMM = 17` |  |
| `static final int WUXI_VIMICRO = 129` |  |
| `static final int ZEEVO = 18` |  |
| `static final int ZER01_TV = 152` |  |
| `static final int ZOMM = 116` |  |
| `static final int ZSCAN_SOFTWARE = 141` |  |

---

### `class final BluetoothClass`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDeviceClass()` |  |
| `int getMajorDeviceClass()` |  |
| `boolean hasService(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static BluetoothClass.Device`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothClass.Device()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AUDIO_VIDEO_CAMCORDER = 1076` |  |
| `static final int AUDIO_VIDEO_CAR_AUDIO = 1056` |  |
| `static final int AUDIO_VIDEO_HANDSFREE = 1032` |  |
| `static final int AUDIO_VIDEO_HEADPHONES = 1048` |  |
| `static final int AUDIO_VIDEO_HIFI_AUDIO = 1064` |  |
| `static final int AUDIO_VIDEO_LOUDSPEAKER = 1044` |  |
| `static final int AUDIO_VIDEO_MICROPHONE = 1040` |  |
| `static final int AUDIO_VIDEO_PORTABLE_AUDIO = 1052` |  |
| `static final int AUDIO_VIDEO_SET_TOP_BOX = 1060` |  |
| `static final int AUDIO_VIDEO_UNCATEGORIZED = 1024` |  |
| `static final int AUDIO_VIDEO_VCR = 1068` |  |
| `static final int AUDIO_VIDEO_VIDEO_CAMERA = 1072` |  |
| `static final int AUDIO_VIDEO_VIDEO_CONFERENCING = 1088` |  |
| `static final int AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER = 1084` |  |
| `static final int AUDIO_VIDEO_VIDEO_GAMING_TOY = 1096` |  |
| `static final int AUDIO_VIDEO_VIDEO_MONITOR = 1080` |  |
| `static final int AUDIO_VIDEO_WEARABLE_HEADSET = 1028` |  |
| `static final int COMPUTER_DESKTOP = 260` |  |
| `static final int COMPUTER_HANDHELD_PC_PDA = 272` |  |
| `static final int COMPUTER_LAPTOP = 268` |  |
| `static final int COMPUTER_PALM_SIZE_PC_PDA = 276` |  |
| `static final int COMPUTER_SERVER = 264` |  |
| `static final int COMPUTER_UNCATEGORIZED = 256` |  |
| `static final int COMPUTER_WEARABLE = 280` |  |
| `static final int HEALTH_BLOOD_PRESSURE = 2308` |  |
| `static final int HEALTH_DATA_DISPLAY = 2332` |  |
| `static final int HEALTH_GLUCOSE = 2320` |  |
| `static final int HEALTH_PULSE_OXIMETER = 2324` |  |
| `static final int HEALTH_PULSE_RATE = 2328` |  |
| `static final int HEALTH_THERMOMETER = 2312` |  |
| `static final int HEALTH_UNCATEGORIZED = 2304` |  |
| `static final int HEALTH_WEIGHING = 2316` |  |
| `static final int PHONE_CELLULAR = 516` |  |
| `static final int PHONE_CORDLESS = 520` |  |
| `static final int PHONE_ISDN = 532` |  |
| `static final int PHONE_MODEM_OR_GATEWAY = 528` |  |
| `static final int PHONE_SMART = 524` |  |
| `static final int PHONE_UNCATEGORIZED = 512` |  |
| `static final int TOY_CONTROLLER = 2064` |  |
| `static final int TOY_DOLL_ACTION_FIGURE = 2060` |  |
| `static final int TOY_GAME = 2068` |  |
| `static final int TOY_ROBOT = 2052` |  |
| `static final int TOY_UNCATEGORIZED = 2048` |  |
| `static final int TOY_VEHICLE = 2056` |  |
| `static final int WEARABLE_GLASSES = 1812` |  |
| `static final int WEARABLE_HELMET = 1808` |  |
| `static final int WEARABLE_JACKET = 1804` |  |
| `static final int WEARABLE_PAGER = 1800` |  |
| `static final int WEARABLE_UNCATEGORIZED = 1792` |  |
| `static final int WEARABLE_WRIST_WATCH = 1796` |  |

---

### `class static BluetoothClass.Device.Major`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothClass.Device.Major()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AUDIO_VIDEO = 1024` |  |
| `static final int COMPUTER = 256` |  |
| `static final int HEALTH = 2304` |  |
| `static final int IMAGING = 1536` |  |
| `static final int MISC = 0` |  |
| `static final int NETWORKING = 768` |  |
| `static final int PERIPHERAL = 1280` |  |
| `static final int PHONE = 512` |  |
| `static final int TOY = 2048` |  |
| `static final int UNCATEGORIZED = 7936` |  |
| `static final int WEARABLE = 1792` |  |

---

### `class static final BluetoothClass.Service`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothClass.Service()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AUDIO = 2097152` |  |
| `static final int CAPTURE = 524288` |  |
| `static final int INFORMATION = 8388608` |  |
| `static final int LIMITED_DISCOVERABILITY = 8192` |  |
| `static final int NETWORKING = 131072` |  |
| `static final int OBJECT_TRANSFER = 1048576` |  |
| `static final int POSITIONING = 65536` |  |
| `static final int RENDER = 262144` |  |
| `static final int TELEPHONY = 4194304` |  |

---

### `class final BluetoothDevice`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_ACL_CONNECTED = "android.bluetooth.device.action.ACL_CONNECTED"` |  |
| `static final String ACTION_ACL_DISCONNECTED = "android.bluetooth.device.action.ACL_DISCONNECTED"` |  |
| `static final String ACTION_ACL_DISCONNECT_REQUESTED = "android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED"` |  |
| `static final String ACTION_ALIAS_CHANGED = "android.bluetooth.device.action.ALIAS_CHANGED"` |  |
| `static final String ACTION_BOND_STATE_CHANGED = "android.bluetooth.device.action.BOND_STATE_CHANGED"` |  |
| `static final String ACTION_CLASS_CHANGED = "android.bluetooth.device.action.CLASS_CHANGED"` |  |
| `static final String ACTION_FOUND = "android.bluetooth.device.action.FOUND"` |  |
| `static final String ACTION_NAME_CHANGED = "android.bluetooth.device.action.NAME_CHANGED"` |  |
| `static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST"` |  |
| `static final String ACTION_UUID = "android.bluetooth.device.action.UUID"` |  |
| `static final int BOND_BONDED = 12` |  |
| `static final int BOND_BONDING = 11` |  |
| `static final int BOND_NONE = 10` |  |
| `static final int DEVICE_TYPE_CLASSIC = 1` |  |
| `static final int DEVICE_TYPE_DUAL = 3` |  |
| `static final int DEVICE_TYPE_LE = 2` |  |
| `static final int DEVICE_TYPE_UNKNOWN = 0` |  |
| `static final int ERROR = -2147483648` |  |
| `static final String EXTRA_BOND_STATE = "android.bluetooth.device.extra.BOND_STATE"` |  |
| `static final String EXTRA_CLASS = "android.bluetooth.device.extra.CLASS"` |  |
| `static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE"` |  |
| `static final String EXTRA_NAME = "android.bluetooth.device.extra.NAME"` |  |
| `static final String EXTRA_PAIRING_KEY = "android.bluetooth.device.extra.PAIRING_KEY"` |  |
| `static final String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT"` |  |
| `static final String EXTRA_PREVIOUS_BOND_STATE = "android.bluetooth.device.extra.PREVIOUS_BOND_STATE"` |  |
| `static final String EXTRA_RSSI = "android.bluetooth.device.extra.RSSI"` |  |
| `static final String EXTRA_UUID = "android.bluetooth.device.extra.UUID"` |  |
| `static final int PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2` |  |
| `static final int PAIRING_VARIANT_PIN = 0` |  |
| `static final int PHY_LE_1M = 1` |  |
| `static final int PHY_LE_1M_MASK = 1` |  |
| `static final int PHY_LE_2M = 2` |  |
| `static final int PHY_LE_2M_MASK = 2` |  |
| `static final int PHY_LE_CODED = 3` |  |
| `static final int PHY_LE_CODED_MASK = 4` |  |
| `static final int PHY_OPTION_NO_PREFERRED = 0` |  |
| `static final int PHY_OPTION_S2 = 1` |  |
| `static final int PHY_OPTION_S8 = 2` |  |
| `static final int TRANSPORT_AUTO = 0` |  |
| `static final int TRANSPORT_BREDR = 1` |  |
| `static final int TRANSPORT_LE = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback)` |  |
| `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback, int)` |  |
| `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback, int, int)` |  |
| `android.bluetooth.BluetoothGatt connectGatt(android.content.Context, boolean, android.bluetooth.BluetoothGattCallback, int, int, android.os.Handler)` |  |
| `int describeContents()` |  |
| `String getAddress()` |  |
| `boolean setPin(byte[])` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final BluetoothGatt`

- **Implements:** `android.bluetooth.BluetoothProfile`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CONNECTION_PRIORITY_BALANCED = 0` |  |
| `static final int CONNECTION_PRIORITY_HIGH = 1` |  |
| `static final int CONNECTION_PRIORITY_LOW_POWER = 2` |  |
| `static final int GATT_CONNECTION_CONGESTED = 143` |  |
| `static final int GATT_FAILURE = 257` |  |
| `static final int GATT_INSUFFICIENT_AUTHENTICATION = 5` |  |
| `static final int GATT_INSUFFICIENT_ENCRYPTION = 15` |  |
| `static final int GATT_INVALID_ATTRIBUTE_LENGTH = 13` |  |
| `static final int GATT_INVALID_OFFSET = 7` |  |
| `static final int GATT_READ_NOT_PERMITTED = 2` |  |
| `static final int GATT_REQUEST_NOT_SUPPORTED = 6` |  |
| `static final int GATT_SUCCESS = 0` |  |
| `static final int GATT_WRITE_NOT_PERMITTED = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void abortReliableWrite()` |  |
| `boolean beginReliableWrite()` |  |
| `void close()` |  |
| `boolean connect()` |  |
| `void disconnect()` |  |
| `boolean discoverServices()` |  |
| `boolean executeReliableWrite()` |  |
| `java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices()` |  |
| `int getConnectionState(android.bluetooth.BluetoothDevice)` |  |
| `android.bluetooth.BluetoothDevice getDevice()` |  |
| `java.util.List<android.bluetooth.BluetoothDevice> getDevicesMatchingConnectionStates(int[])` |  |
| `android.bluetooth.BluetoothGattService getService(java.util.UUID)` |  |
| `java.util.List<android.bluetooth.BluetoothGattService> getServices()` |  |
| `boolean readCharacteristic(android.bluetooth.BluetoothGattCharacteristic)` |  |
| `boolean readDescriptor(android.bluetooth.BluetoothGattDescriptor)` |  |
| `void readPhy()` |  |
| `boolean readRemoteRssi()` |  |
| `boolean requestConnectionPriority(int)` |  |
| `boolean requestMtu(int)` |  |
| `boolean setCharacteristicNotification(android.bluetooth.BluetoothGattCharacteristic, boolean)` |  |
| `void setPreferredPhy(int, int, int)` |  |
| `boolean writeCharacteristic(android.bluetooth.BluetoothGattCharacteristic)` |  |
| `boolean writeDescriptor(android.bluetooth.BluetoothGattDescriptor)` |  |

---

### `class abstract BluetoothGattCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothGattCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCharacteristicChanged(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic)` |  |
| `void onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)` |  |
| `void onCharacteristicWrite(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)` |  |
| `void onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)` |  |
| `void onDescriptorRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattDescriptor, int)` |  |
| `void onDescriptorWrite(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattDescriptor, int)` |  |
| `void onMtuChanged(android.bluetooth.BluetoothGatt, int, int)` |  |
| `void onPhyRead(android.bluetooth.BluetoothGatt, int, int, int)` |  |
| `void onPhyUpdate(android.bluetooth.BluetoothGatt, int, int, int)` |  |
| `void onReadRemoteRssi(android.bluetooth.BluetoothGatt, int, int)` |  |
| `void onReliableWriteCompleted(android.bluetooth.BluetoothGatt, int)` |  |
| `void onServicesDiscovered(android.bluetooth.BluetoothGatt, int)` |  |

---

### `class BluetoothGattCharacteristic`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothGattCharacteristic(java.util.UUID, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FORMAT_FLOAT = 52` |  |
| `static final int FORMAT_SFLOAT = 50` |  |
| `static final int FORMAT_SINT16 = 34` |  |
| `static final int FORMAT_SINT32 = 36` |  |
| `static final int FORMAT_SINT8 = 33` |  |
| `static final int FORMAT_UINT16 = 18` |  |
| `static final int FORMAT_UINT32 = 20` |  |
| `static final int FORMAT_UINT8 = 17` |  |
| `static final int PERMISSION_READ = 1` |  |
| `static final int PERMISSION_READ_ENCRYPTED = 2` |  |
| `static final int PERMISSION_READ_ENCRYPTED_MITM = 4` |  |
| `static final int PERMISSION_WRITE = 16` |  |
| `static final int PERMISSION_WRITE_ENCRYPTED = 32` |  |
| `static final int PERMISSION_WRITE_ENCRYPTED_MITM = 64` |  |
| `static final int PERMISSION_WRITE_SIGNED = 128` |  |
| `static final int PERMISSION_WRITE_SIGNED_MITM = 256` |  |
| `static final int PROPERTY_BROADCAST = 1` |  |
| `static final int PROPERTY_EXTENDED_PROPS = 128` |  |
| `static final int PROPERTY_INDICATE = 32` |  |
| `static final int PROPERTY_NOTIFY = 16` |  |
| `static final int PROPERTY_READ = 2` |  |
| `static final int PROPERTY_SIGNED_WRITE = 64` |  |
| `static final int PROPERTY_WRITE = 8` |  |
| `static final int PROPERTY_WRITE_NO_RESPONSE = 4` |  |
| `static final int WRITE_TYPE_DEFAULT = 2` |  |
| `static final int WRITE_TYPE_NO_RESPONSE = 1` |  |
| `static final int WRITE_TYPE_SIGNED = 4` |  |
| `java.util.List<android.bluetooth.BluetoothGattDescriptor> mDescriptors` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addDescriptor(android.bluetooth.BluetoothGattDescriptor)` |  |
| `int describeContents()` |  |
| `android.bluetooth.BluetoothGattDescriptor getDescriptor(java.util.UUID)` |  |
| `java.util.List<android.bluetooth.BluetoothGattDescriptor> getDescriptors()` |  |
| `Float getFloatValue(int, int)` |  |
| `int getInstanceId()` |  |
| `Integer getIntValue(int, int)` |  |
| `int getPermissions()` |  |
| `int getProperties()` |  |
| `android.bluetooth.BluetoothGattService getService()` |  |
| `String getStringValue(int)` |  |
| `java.util.UUID getUuid()` |  |
| `byte[] getValue()` |  |
| `int getWriteType()` |  |
| `boolean setValue(byte[])` |  |
| `boolean setValue(int, int, int)` |  |
| `boolean setValue(int, int, int, int)` |  |
| `boolean setValue(String)` |  |
| `void setWriteType(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class BluetoothGattDescriptor`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothGattDescriptor(java.util.UUID, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final byte[] DISABLE_NOTIFICATION_VALUE` |  |
| `static final byte[] ENABLE_INDICATION_VALUE` |  |
| `static final byte[] ENABLE_NOTIFICATION_VALUE` |  |
| `static final int PERMISSION_READ = 1` |  |
| `static final int PERMISSION_READ_ENCRYPTED = 2` |  |
| `static final int PERMISSION_READ_ENCRYPTED_MITM = 4` |  |
| `static final int PERMISSION_WRITE = 16` |  |
| `static final int PERMISSION_WRITE_ENCRYPTED = 32` |  |
| `static final int PERMISSION_WRITE_ENCRYPTED_MITM = 64` |  |
| `static final int PERMISSION_WRITE_SIGNED = 128` |  |
| `static final int PERMISSION_WRITE_SIGNED_MITM = 256` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `android.bluetooth.BluetoothGattCharacteristic getCharacteristic()` |  |
| `int getPermissions()` |  |
| `java.util.UUID getUuid()` |  |
| `byte[] getValue()` |  |
| `boolean setValue(byte[])` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final BluetoothGattServer`

- **Implements:** `android.bluetooth.BluetoothProfile`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addService(android.bluetooth.BluetoothGattService)` |  |
| `void cancelConnection(android.bluetooth.BluetoothDevice)` |  |
| `void clearServices()` |  |
| `void close()` |  |
| `boolean connect(android.bluetooth.BluetoothDevice, boolean)` |  |
| `java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices()` |  |
| `int getConnectionState(android.bluetooth.BluetoothDevice)` |  |
| `java.util.List<android.bluetooth.BluetoothDevice> getDevicesMatchingConnectionStates(int[])` |  |
| `android.bluetooth.BluetoothGattService getService(java.util.UUID)` |  |
| `java.util.List<android.bluetooth.BluetoothGattService> getServices()` |  |
| `boolean notifyCharacteristicChanged(android.bluetooth.BluetoothDevice, android.bluetooth.BluetoothGattCharacteristic, boolean)` |  |
| `void readPhy(android.bluetooth.BluetoothDevice)` |  |
| `boolean removeService(android.bluetooth.BluetoothGattService)` |  |
| `boolean sendResponse(android.bluetooth.BluetoothDevice, int, int, int, byte[])` |  |
| `void setPreferredPhy(android.bluetooth.BluetoothDevice, int, int, int)` |  |

---

### `class abstract BluetoothGattServerCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothGattServerCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCharacteristicReadRequest(android.bluetooth.BluetoothDevice, int, int, android.bluetooth.BluetoothGattCharacteristic)` |  |
| `void onCharacteristicWriteRequest(android.bluetooth.BluetoothDevice, int, android.bluetooth.BluetoothGattCharacteristic, boolean, boolean, int, byte[])` |  |
| `void onConnectionStateChange(android.bluetooth.BluetoothDevice, int, int)` |  |
| `void onDescriptorReadRequest(android.bluetooth.BluetoothDevice, int, int, android.bluetooth.BluetoothGattDescriptor)` |  |
| `void onDescriptorWriteRequest(android.bluetooth.BluetoothDevice, int, android.bluetooth.BluetoothGattDescriptor, boolean, boolean, int, byte[])` |  |
| `void onExecuteWrite(android.bluetooth.BluetoothDevice, int, boolean)` |  |
| `void onMtuChanged(android.bluetooth.BluetoothDevice, int)` |  |
| `void onNotificationSent(android.bluetooth.BluetoothDevice, int)` |  |
| `void onPhyRead(android.bluetooth.BluetoothDevice, int, int, int)` |  |
| `void onPhyUpdate(android.bluetooth.BluetoothDevice, int, int, int)` |  |
| `void onServiceAdded(int, android.bluetooth.BluetoothGattService)` |  |

---

### `class BluetoothGattService`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothGattService(java.util.UUID, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int SERVICE_TYPE_PRIMARY = 0` |  |
| `static final int SERVICE_TYPE_SECONDARY = 1` |  |
| `java.util.List<android.bluetooth.BluetoothGattCharacteristic> mCharacteristics` |  |
| `java.util.List<android.bluetooth.BluetoothGattService> mIncludedServices` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean addCharacteristic(android.bluetooth.BluetoothGattCharacteristic)` |  |
| `boolean addService(android.bluetooth.BluetoothGattService)` |  |
| `int describeContents()` |  |
| `android.bluetooth.BluetoothGattCharacteristic getCharacteristic(java.util.UUID)` |  |
| `java.util.List<android.bluetooth.BluetoothGattCharacteristic> getCharacteristics()` |  |
| `java.util.List<android.bluetooth.BluetoothGattService> getIncludedServices()` |  |
| `int getInstanceId()` |  |
| `int getType()` |  |
| `java.util.UUID getUuid()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final BluetoothHeadset`

- **Implements:** `android.bluetooth.BluetoothProfile`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_AUDIO_STATE_CHANGED = "android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED"` |  |
| `static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"` |  |
| `static final String ACTION_VENDOR_SPECIFIC_HEADSET_EVENT = "android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT"` |  |
| `static final int AT_CMD_TYPE_ACTION = 4` |  |
| `static final int AT_CMD_TYPE_BASIC = 3` |  |
| `static final int AT_CMD_TYPE_READ = 0` |  |
| `static final int AT_CMD_TYPE_SET = 2` |  |
| `static final int AT_CMD_TYPE_TEST = 1` |  |
| `static final String EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_ARGS = "android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_ARGS"` |  |
| `static final String EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_CMD = "android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_CMD"` |  |
| `static final String EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_CMD_TYPE = "android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_CMD_TYPE"` |  |
| `static final int STATE_AUDIO_CONNECTED = 12` |  |
| `static final int STATE_AUDIO_CONNECTING = 11` |  |
| `static final int STATE_AUDIO_DISCONNECTED = 10` |  |
| `static final String VENDOR_RESULT_CODE_COMMAND_ANDROID = "+ANDROID"` |  |
| `static final String VENDOR_SPECIFIC_HEADSET_EVENT_COMPANY_ID_CATEGORY = "android.bluetooth.headset.intent.category.companyid"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices()` |  |
| `int getConnectionState(android.bluetooth.BluetoothDevice)` |  |
| `java.util.List<android.bluetooth.BluetoothDevice> getDevicesMatchingConnectionStates(int[])` |  |
| `boolean isAudioConnected(android.bluetooth.BluetoothDevice)` |  |
| `boolean sendVendorSpecificResultCode(android.bluetooth.BluetoothDevice, String, String)` |  |
| `boolean startVoiceRecognition(android.bluetooth.BluetoothDevice)` |  |
| `boolean stopVoiceRecognition(android.bluetooth.BluetoothDevice)` |  |

---

### `class final BluetoothHealth` ~~DEPRECATED~~

- **Implements:** `android.bluetooth.BluetoothProfile`
- **Annotations:** `@Deprecated`

---

### `class final BluetoothHealthAppConfiguration` ~~DEPRECATED~~

- **Implements:** `android.os.Parcelable`
- **Annotations:** `@Deprecated`

---

### `class abstract BluetoothHealthCallback` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final BluetoothHearingAid`

- **Implements:** `android.bluetooth.BluetoothProfile`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getConnectionState(@NonNull android.bluetooth.BluetoothDevice)` |  |

---

### `class final BluetoothHidDevice`

- **Implements:** `android.bluetooth.BluetoothProfile`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothHidDevice.Callback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.hiddevice.profile.action.CONNECTION_STATE_CHANGED"` |  |
| `static final byte ERROR_RSP_INVALID_PARAM = 4` |  |
| `static final byte ERROR_RSP_INVALID_RPT_ID = 2` |  |
| `static final byte ERROR_RSP_NOT_READY = 1` |  |
| `static final byte ERROR_RSP_SUCCESS = 0` |  |
| `static final byte ERROR_RSP_UNKNOWN = 14` |  |
| `static final byte ERROR_RSP_UNSUPPORTED_REQ = 3` |  |
| `static final byte PROTOCOL_BOOT_MODE = 0` |  |
| `static final byte PROTOCOL_REPORT_MODE = 1` |  |
| `static final byte REPORT_TYPE_FEATURE = 3` |  |
| `static final byte REPORT_TYPE_INPUT = 1` |  |
| `static final byte REPORT_TYPE_OUTPUT = 2` |  |
| `static final byte SUBCLASS1_COMBO = -64` |  |
| `static final byte SUBCLASS1_KEYBOARD = 64` |  |
| `static final byte SUBCLASS1_MOUSE = -128` |  |
| `static final byte SUBCLASS1_NONE = 0` |  |
| `static final byte SUBCLASS2_CARD_READER = 6` |  |
| `static final byte SUBCLASS2_DIGITIZER_TABLET = 5` |  |
| `static final byte SUBCLASS2_GAMEPAD = 2` |  |
| `static final byte SUBCLASS2_JOYSTICK = 1` |  |
| `static final byte SUBCLASS2_REMOTE_CONTROL = 3` |  |
| `static final byte SUBCLASS2_SENSING_DEVICE = 4` |  |
| `static final byte SUBCLASS2_UNCATEGORIZED = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean connect(android.bluetooth.BluetoothDevice)` |  |
| `boolean disconnect(android.bluetooth.BluetoothDevice)` |  |
| `java.util.List<android.bluetooth.BluetoothDevice> getConnectedDevices()` |  |
| `int getConnectionState(android.bluetooth.BluetoothDevice)` |  |
| `java.util.List<android.bluetooth.BluetoothDevice> getDevicesMatchingConnectionStates(int[])` |  |
| `boolean registerApp(android.bluetooth.BluetoothHidDeviceAppSdpSettings, android.bluetooth.BluetoothHidDeviceAppQosSettings, android.bluetooth.BluetoothHidDeviceAppQosSettings, java.util.concurrent.Executor, android.bluetooth.BluetoothHidDevice.Callback)` |  |
| `boolean replyReport(android.bluetooth.BluetoothDevice, byte, byte, byte[])` |  |
| `boolean reportError(android.bluetooth.BluetoothDevice, byte)` |  |
| `boolean sendReport(android.bluetooth.BluetoothDevice, int, byte[])` |  |
| `boolean unregisterApp()` |  |
| `void onAppStatusChanged(android.bluetooth.BluetoothDevice, boolean)` |  |
| `void onConnectionStateChanged(android.bluetooth.BluetoothDevice, int)` |  |
| `void onGetReport(android.bluetooth.BluetoothDevice, byte, byte, int)` |  |
| `void onInterruptData(android.bluetooth.BluetoothDevice, byte, byte[])` |  |
| `void onSetProtocol(android.bluetooth.BluetoothDevice, byte)` |  |
| `void onSetReport(android.bluetooth.BluetoothDevice, byte, byte, byte[])` |  |
| `void onVirtualCableUnplug(android.bluetooth.BluetoothDevice)` |  |

---

### `class final BluetoothHidDeviceAppQosSettings`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothHidDeviceAppQosSettings(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MAX = -1` |  |
| `static final int SERVICE_BEST_EFFORT = 1` |  |
| `static final int SERVICE_GUARANTEED = 2` |  |
| `static final int SERVICE_NO_TRAFFIC = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getDelayVariation()` |  |
| `int getLatency()` |  |
| `int getPeakBandwidth()` |  |
| `int getServiceType()` |  |
| `int getTokenBucketSize()` |  |
| `int getTokenRate()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final BluetoothHidDeviceAppSdpSettings`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothHidDeviceAppSdpSettings(String, String, String, byte, byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `String getDescription()` |  |
| `byte[] getDescriptors()` |  |
| `String getName()` |  |
| `String getProvider()` |  |
| `byte getSubclass()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final BluetoothManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.bluetooth.BluetoothAdapter getAdapter()` |  |
| `android.bluetooth.BluetoothGattServer openGattServer(android.content.Context, android.bluetooth.BluetoothGattServerCallback)` |  |

---

### `interface BluetoothProfile`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int A2DP = 2` |  |
| `static final String EXTRA_PREVIOUS_STATE = "android.bluetooth.profile.extra.PREVIOUS_STATE"` |  |
| `static final String EXTRA_STATE = "android.bluetooth.profile.extra.STATE"` |  |
| `static final int GATT = 7` |  |
| `static final int GATT_SERVER = 8` |  |
| `static final int HEADSET = 1` |  |
| `static final int HEARING_AID = 21` |  |
| `static final int HID_DEVICE = 19` |  |
| `static final int SAP = 10` |  |
| `static final int STATE_CONNECTED = 2` |  |
| `static final int STATE_CONNECTING = 1` |  |
| `static final int STATE_DISCONNECTED = 0` |  |
| `static final int STATE_DISCONNECTING = 3` |  |

---

### `interface static BluetoothProfile.ServiceListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onServiceConnected(int, android.bluetooth.BluetoothProfile)` |  |
| `void onServiceDisconnected(int)` |  |

---

### `class final BluetoothServerSocket`

- **Implements:** `java.io.Closeable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.bluetooth.BluetoothSocket accept() throws java.io.IOException` |  |
| `android.bluetooth.BluetoothSocket accept(int) throws java.io.IOException` |  |
| `void close() throws java.io.IOException` |  |
| `int getPsm()` |  |

---

### `class final BluetoothSocket`

- **Implements:** `java.io.Closeable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TYPE_L2CAP = 3` |  |
| `static final int TYPE_RFCOMM = 1` |  |
| `static final int TYPE_SCO = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `int getConnectionType()` |  |
| `java.io.InputStream getInputStream() throws java.io.IOException` |  |
| `int getMaxReceivePacketSize()` |  |
| `int getMaxTransmitPacketSize()` |  |
| `java.io.OutputStream getOutputStream() throws java.io.IOException` |  |
| `android.bluetooth.BluetoothDevice getRemoteDevice()` |  |
| `boolean isConnected()` |  |

---

## Package: `android.bluetooth.le`

### `class abstract AdvertiseCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AdvertiseCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ADVERTISE_FAILED_ALREADY_STARTED = 3` |  |
| `static final int ADVERTISE_FAILED_DATA_TOO_LARGE = 1` |  |
| `static final int ADVERTISE_FAILED_FEATURE_UNSUPPORTED = 5` |  |
| `static final int ADVERTISE_FAILED_INTERNAL_ERROR = 4` |  |
| `static final int ADVERTISE_FAILED_TOO_MANY_ADVERTISERS = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onStartFailure(int)` |  |
| `void onStartSuccess(android.bluetooth.le.AdvertiseSettings)` |  |

---

### `class final AdvertiseData`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean getIncludeDeviceName()` |  |
| `boolean getIncludeTxPowerLevel()` |  |
| `android.util.SparseArray<byte[]> getManufacturerSpecificData()` |  |
| `java.util.Map<android.os.ParcelUuid,byte[]> getServiceData()` |  |
| `java.util.List<android.os.ParcelUuid> getServiceUuids()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final AdvertiseData.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AdvertiseData.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.bluetooth.le.AdvertiseData.Builder addManufacturerData(int, byte[])` |  |
| `android.bluetooth.le.AdvertiseData.Builder addServiceData(android.os.ParcelUuid, byte[])` |  |
| `android.bluetooth.le.AdvertiseData.Builder addServiceUuid(android.os.ParcelUuid)` |  |
| `android.bluetooth.le.AdvertiseData build()` |  |
| `android.bluetooth.le.AdvertiseData.Builder setIncludeDeviceName(boolean)` |  |
| `android.bluetooth.le.AdvertiseData.Builder setIncludeTxPowerLevel(boolean)` |  |

---

### `class final AdvertiseSettings`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ADVERTISE_MODE_BALANCED = 1` |  |
| `static final int ADVERTISE_MODE_LOW_LATENCY = 2` |  |
| `static final int ADVERTISE_MODE_LOW_POWER = 0` |  |
| `static final int ADVERTISE_TX_POWER_HIGH = 3` |  |
| `static final int ADVERTISE_TX_POWER_LOW = 1` |  |
| `static final int ADVERTISE_TX_POWER_MEDIUM = 2` |  |
| `static final int ADVERTISE_TX_POWER_ULTRA_LOW = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getMode()` |  |
| `int getTimeout()` |  |
| `int getTxPowerLevel()` |  |
| `boolean isConnectable()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final AdvertiseSettings.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AdvertiseSettings.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.bluetooth.le.AdvertiseSettings build()` |  |
| `android.bluetooth.le.AdvertiseSettings.Builder setAdvertiseMode(int)` |  |
| `android.bluetooth.le.AdvertiseSettings.Builder setConnectable(boolean)` |  |
| `android.bluetooth.le.AdvertiseSettings.Builder setTimeout(int)` |  |
| `android.bluetooth.le.AdvertiseSettings.Builder setTxPowerLevel(int)` |  |

---

### `class final AdvertisingSet`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void enableAdvertising(boolean, int, int)` |  |
| `void setAdvertisingData(android.bluetooth.le.AdvertiseData)` |  |
| `void setAdvertisingParameters(android.bluetooth.le.AdvertisingSetParameters)` |  |
| `void setPeriodicAdvertisingData(android.bluetooth.le.AdvertiseData)` |  |
| `void setPeriodicAdvertisingEnabled(boolean)` |  |
| `void setPeriodicAdvertisingParameters(android.bluetooth.le.PeriodicAdvertisingParameters)` |  |
| `void setScanResponseData(android.bluetooth.le.AdvertiseData)` |  |

---

### `class abstract AdvertisingSetCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AdvertisingSetCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ADVERTISE_FAILED_ALREADY_STARTED = 3` |  |
| `static final int ADVERTISE_FAILED_DATA_TOO_LARGE = 1` |  |
| `static final int ADVERTISE_FAILED_FEATURE_UNSUPPORTED = 5` |  |
| `static final int ADVERTISE_FAILED_INTERNAL_ERROR = 4` |  |
| `static final int ADVERTISE_FAILED_TOO_MANY_ADVERTISERS = 2` |  |
| `static final int ADVERTISE_SUCCESS = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAdvertisingDataSet(android.bluetooth.le.AdvertisingSet, int)` |  |
| `void onAdvertisingEnabled(android.bluetooth.le.AdvertisingSet, boolean, int)` |  |
| `void onAdvertisingParametersUpdated(android.bluetooth.le.AdvertisingSet, int, int)` |  |
| `void onAdvertisingSetStarted(android.bluetooth.le.AdvertisingSet, int, int)` |  |
| `void onAdvertisingSetStopped(android.bluetooth.le.AdvertisingSet)` |  |
| `void onPeriodicAdvertisingDataSet(android.bluetooth.le.AdvertisingSet, int)` |  |
| `void onPeriodicAdvertisingEnabled(android.bluetooth.le.AdvertisingSet, boolean, int)` |  |
| `void onPeriodicAdvertisingParametersUpdated(android.bluetooth.le.AdvertisingSet, int)` |  |
| `void onScanResponseDataSet(android.bluetooth.le.AdvertisingSet, int)` |  |

---

### `class final AdvertisingSetParameters`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int INTERVAL_HIGH = 1600` |  |
| `static final int INTERVAL_LOW = 160` |  |
| `static final int INTERVAL_MAX = 16777215` |  |
| `static final int INTERVAL_MEDIUM = 400` |  |
| `static final int INTERVAL_MIN = 160` |  |
| `static final int TX_POWER_HIGH = 1` |  |
| `static final int TX_POWER_LOW = -15` |  |
| `static final int TX_POWER_MAX = 1` |  |
| `static final int TX_POWER_MEDIUM = -7` |  |
| `static final int TX_POWER_MIN = -127` |  |
| `static final int TX_POWER_ULTRA_LOW = -21` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getInterval()` |  |
| `int getPrimaryPhy()` |  |
| `int getSecondaryPhy()` |  |
| `int getTxPowerLevel()` |  |
| `boolean includeTxPower()` |  |
| `boolean isAnonymous()` |  |
| `boolean isConnectable()` |  |
| `boolean isLegacy()` |  |
| `boolean isScannable()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final AdvertisingSetParameters.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AdvertisingSetParameters.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.bluetooth.le.AdvertisingSetParameters build()` |  |
| `android.bluetooth.le.AdvertisingSetParameters.Builder setAnonymous(boolean)` |  |
| `android.bluetooth.le.AdvertisingSetParameters.Builder setConnectable(boolean)` |  |
| `android.bluetooth.le.AdvertisingSetParameters.Builder setIncludeTxPower(boolean)` |  |
| `android.bluetooth.le.AdvertisingSetParameters.Builder setInterval(int)` |  |
| `android.bluetooth.le.AdvertisingSetParameters.Builder setLegacyMode(boolean)` |  |
| `android.bluetooth.le.AdvertisingSetParameters.Builder setPrimaryPhy(int)` |  |
| `android.bluetooth.le.AdvertisingSetParameters.Builder setScannable(boolean)` |  |
| `android.bluetooth.le.AdvertisingSetParameters.Builder setSecondaryPhy(int)` |  |
| `android.bluetooth.le.AdvertisingSetParameters.Builder setTxPowerLevel(int)` |  |

---

### `class final BluetoothLeAdvertiser`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void startAdvertising(android.bluetooth.le.AdvertiseSettings, android.bluetooth.le.AdvertiseData, android.bluetooth.le.AdvertiseCallback)` |  |
| `void startAdvertising(android.bluetooth.le.AdvertiseSettings, android.bluetooth.le.AdvertiseData, android.bluetooth.le.AdvertiseData, android.bluetooth.le.AdvertiseCallback)` |  |
| `void startAdvertisingSet(android.bluetooth.le.AdvertisingSetParameters, android.bluetooth.le.AdvertiseData, android.bluetooth.le.AdvertiseData, android.bluetooth.le.PeriodicAdvertisingParameters, android.bluetooth.le.AdvertiseData, android.bluetooth.le.AdvertisingSetCallback)` |  |
| `void startAdvertisingSet(android.bluetooth.le.AdvertisingSetParameters, android.bluetooth.le.AdvertiseData, android.bluetooth.le.AdvertiseData, android.bluetooth.le.PeriodicAdvertisingParameters, android.bluetooth.le.AdvertiseData, android.bluetooth.le.AdvertisingSetCallback, android.os.Handler)` |  |
| `void startAdvertisingSet(android.bluetooth.le.AdvertisingSetParameters, android.bluetooth.le.AdvertiseData, android.bluetooth.le.AdvertiseData, android.bluetooth.le.PeriodicAdvertisingParameters, android.bluetooth.le.AdvertiseData, int, int, android.bluetooth.le.AdvertisingSetCallback)` |  |
| `void startAdvertisingSet(android.bluetooth.le.AdvertisingSetParameters, android.bluetooth.le.AdvertiseData, android.bluetooth.le.AdvertiseData, android.bluetooth.le.PeriodicAdvertisingParameters, android.bluetooth.le.AdvertiseData, int, int, android.bluetooth.le.AdvertisingSetCallback, android.os.Handler)` |  |
| `void stopAdvertising(android.bluetooth.le.AdvertiseCallback)` |  |
| `void stopAdvertisingSet(android.bluetooth.le.AdvertisingSetCallback)` |  |

---

### `class final BluetoothLeScanner`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_CALLBACK_TYPE = "android.bluetooth.le.extra.CALLBACK_TYPE"` |  |
| `static final String EXTRA_ERROR_CODE = "android.bluetooth.le.extra.ERROR_CODE"` |  |
| `static final String EXTRA_LIST_SCAN_RESULT = "android.bluetooth.le.extra.LIST_SCAN_RESULT"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void flushPendingScanResults(android.bluetooth.le.ScanCallback)` |  |

---

### `class final PeriodicAdvertisingParameters`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.os.Parcelable.Creator<android.bluetooth.le.PeriodicAdvertisingParameters> CREATOR` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `boolean getIncludeTxPower()` |  |
| `int getInterval()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final PeriodicAdvertisingParameters.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PeriodicAdvertisingParameters.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.bluetooth.le.PeriodicAdvertisingParameters build()` |  |
| `android.bluetooth.le.PeriodicAdvertisingParameters.Builder setIncludeTxPower(boolean)` |  |
| `android.bluetooth.le.PeriodicAdvertisingParameters.Builder setInterval(int)` |  |

---

### `class abstract ScanCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ScanCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int SCAN_FAILED_ALREADY_STARTED = 1` |  |
| `static final int SCAN_FAILED_APPLICATION_REGISTRATION_FAILED = 2` |  |
| `static final int SCAN_FAILED_FEATURE_UNSUPPORTED = 4` |  |
| `static final int SCAN_FAILED_INTERNAL_ERROR = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onBatchScanResults(java.util.List<android.bluetooth.le.ScanResult>)` |  |
| `void onScanFailed(int)` |  |
| `void onScanResult(int, android.bluetooth.le.ScanResult)` |  |

---

### `class final ScanFilter`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getManufacturerId()` |  |
| `boolean matches(android.bluetooth.le.ScanResult)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final ScanFilter.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ScanFilter.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.bluetooth.le.ScanFilter build()` |  |
| `android.bluetooth.le.ScanFilter.Builder setDeviceAddress(String)` |  |
| `android.bluetooth.le.ScanFilter.Builder setDeviceName(String)` |  |
| `android.bluetooth.le.ScanFilter.Builder setManufacturerData(int, byte[])` |  |
| `android.bluetooth.le.ScanFilter.Builder setManufacturerData(int, byte[], byte[])` |  |
| `android.bluetooth.le.ScanFilter.Builder setServiceData(android.os.ParcelUuid, byte[])` |  |
| `android.bluetooth.le.ScanFilter.Builder setServiceData(android.os.ParcelUuid, byte[], byte[])` |  |
| `android.bluetooth.le.ScanFilter.Builder setServiceUuid(android.os.ParcelUuid)` |  |
| `android.bluetooth.le.ScanFilter.Builder setServiceUuid(android.os.ParcelUuid, android.os.ParcelUuid)` |  |

---

### `class final ScanRecord`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getAdvertiseFlags()` |  |
| `byte[] getBytes()` |  |
| `android.util.SparseArray<byte[]> getManufacturerSpecificData()` |  |
| `java.util.Map<android.os.ParcelUuid,byte[]> getServiceData()` |  |
| `java.util.List<android.os.ParcelUuid> getServiceUuids()` |  |
| `int getTxPowerLevel()` |  |

---

### `class final ScanResult`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ScanResult(android.bluetooth.BluetoothDevice, int, int, int, int, int, int, int, android.bluetooth.le.ScanRecord, long)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DATA_COMPLETE = 0` |  |
| `static final int DATA_TRUNCATED = 2` |  |
| `static final int PERIODIC_INTERVAL_NOT_PRESENT = 0` |  |
| `static final int PHY_UNUSED = 0` |  |
| `static final int SID_NOT_PRESENT = 255` |  |
| `static final int TX_POWER_NOT_PRESENT = 127` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAdvertisingSid()` |  |
| `int getDataStatus()` |  |
| `android.bluetooth.BluetoothDevice getDevice()` |  |
| `int getPeriodicAdvertisingInterval()` |  |
| `int getPrimaryPhy()` |  |
| `int getRssi()` |  |
| `int getSecondaryPhy()` |  |
| `long getTimestampNanos()` |  |
| `int getTxPower()` |  |
| `boolean isConnectable()` |  |
| `boolean isLegacy()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final ScanSettings`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CALLBACK_TYPE_ALL_MATCHES = 1` |  |
| `static final int CALLBACK_TYPE_FIRST_MATCH = 2` |  |
| `static final int CALLBACK_TYPE_MATCH_LOST = 4` |  |
| `static final int MATCH_MODE_AGGRESSIVE = 1` |  |
| `static final int MATCH_MODE_STICKY = 2` |  |
| `static final int MATCH_NUM_FEW_ADVERTISEMENT = 2` |  |
| `static final int MATCH_NUM_MAX_ADVERTISEMENT = 3` |  |
| `static final int MATCH_NUM_ONE_ADVERTISEMENT = 1` |  |
| `static final int PHY_LE_ALL_SUPPORTED = 255` |  |
| `static final int SCAN_MODE_BALANCED = 1` |  |
| `static final int SCAN_MODE_LOW_LATENCY = 2` |  |
| `static final int SCAN_MODE_LOW_POWER = 0` |  |
| `static final int SCAN_MODE_OPPORTUNISTIC = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getCallbackType()` |  |
| `boolean getLegacy()` |  |
| `int getPhy()` |  |
| `long getReportDelayMillis()` |  |
| `int getScanMode()` |  |
| `int getScanResultType()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final ScanSettings.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ScanSettings.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.bluetooth.le.ScanSettings build()` |  |
| `android.bluetooth.le.ScanSettings.Builder setCallbackType(int)` |  |
| `android.bluetooth.le.ScanSettings.Builder setLegacy(boolean)` |  |
| `android.bluetooth.le.ScanSettings.Builder setMatchMode(int)` |  |
| `android.bluetooth.le.ScanSettings.Builder setNumOfMatches(int)` |  |
| `android.bluetooth.le.ScanSettings.Builder setPhy(int)` |  |
| `android.bluetooth.le.ScanSettings.Builder setReportDelay(long)` |  |
| `android.bluetooth.le.ScanSettings.Builder setScanMode(int)` |  |

---

## Package: `android.companion`

### `class final AssociationRequest`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final AssociationRequest.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AssociationRequest.Builder()` |  |

---

### `class final BluetoothDeviceFilter`

- **Implements:** `android.companion.DeviceFilter<android.bluetooth.BluetoothDevice>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final BluetoothDeviceFilter.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothDeviceFilter.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.companion.BluetoothDeviceFilter.Builder setNamePattern(@Nullable java.util.regex.Pattern)` |  |

---

### `class final BluetoothLeDeviceFilter`

- **Implements:** `android.companion.DeviceFilter<android.bluetooth.le.ScanResult>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `static int getRenamePrefixLengthLimit()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final BluetoothLeDeviceFilter.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BluetoothLeDeviceFilter.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.companion.BluetoothLeDeviceFilter.Builder setNamePattern(@Nullable java.util.regex.Pattern)` |  |

---

### `class final CompanionDeviceManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CompanionDeviceManager.Callback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_DEVICE = "android.companion.extra.DEVICE"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void associate(@NonNull android.companion.AssociationRequest, @NonNull android.companion.CompanionDeviceManager.Callback, @Nullable android.os.Handler)` |  |
| `void disassociate(@NonNull String)` |  |
| `boolean hasNotificationAccess(android.content.ComponentName)` |  |
| `void requestNotificationAccess(android.content.ComponentName)` |  |
| `abstract void onDeviceFound(android.content.IntentSender)` |  |
| `abstract void onFailure(CharSequence)` |  |

---

### `interface DeviceFilter<D`

- **Extends:** `android.os.Parcelable> extends android.os.Parcelable`

---

### `class final WifiDeviceFilter`

- **Implements:** `android.companion.DeviceFilter<android.net.wifi.ScanResult>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final WifiDeviceFilter.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WifiDeviceFilter.Builder()` |  |

---

## Package: `android.drm`

### `class DrmConvertedStatus` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class DrmErrorEvent` ~~DEPRECATED~~

- **Extends:** `android.drm.DrmEvent`
- **Annotations:** `@Deprecated`

---

### `class DrmEvent` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class DrmInfo` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class DrmInfoEvent` ~~DEPRECATED~~

- **Extends:** `android.drm.DrmEvent`
- **Annotations:** `@Deprecated`

---

### `class DrmInfoRequest` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class DrmInfoStatus` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class DrmManagerClient` ~~DEPRECATED~~

- **Implements:** `java.lang.AutoCloseable`
- **Annotations:** `@Deprecated`

---

### `interface static DrmManagerClient.OnErrorListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static DrmManagerClient.OnEventListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static DrmManagerClient.OnInfoListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class DrmRights` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class DrmStore` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static DrmStore.Action` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static DrmStore.ConstraintsColumns` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static DrmStore.DrmObjectType` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static DrmStore.Playback` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static DrmStore.RightsStatus` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class DrmSupportInfo` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class DrmUtils` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static DrmUtils.ExtendedMetadataParser` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class ProcessedData` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

## Package: `android.gesture`

### `class Gesture`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Gesture()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addStroke(android.gesture.GestureStroke)` |  |
| `Object clone()` |  |
| `int describeContents()` |  |
| `android.graphics.RectF getBoundingBox()` |  |
| `long getID()` |  |
| `float getLength()` |  |
| `java.util.ArrayList<android.gesture.GestureStroke> getStrokes()` |  |
| `int getStrokesCount()` |  |
| `android.graphics.Bitmap toBitmap(int, int, int, int, int)` |  |
| `android.graphics.Bitmap toBitmap(int, int, int, int)` |  |
| `android.graphics.Path toPath()` |  |
| `android.graphics.Path toPath(android.graphics.Path)` |  |
| `android.graphics.Path toPath(int, int, int, int)` |  |
| `android.graphics.Path toPath(android.graphics.Path, int, int, int, int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final GestureLibraries`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.gesture.GestureLibrary fromFile(String)` |  |
| `static android.gesture.GestureLibrary fromFile(java.io.File)` |  |
| `static android.gesture.GestureLibrary fromPrivateFile(android.content.Context, String)` |  |
| `static android.gesture.GestureLibrary fromRawResource(android.content.Context, @RawRes int)` |  |

---

### `class abstract GestureLibrary`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GestureLibrary()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final android.gesture.GestureStore mStore` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addGesture(String, android.gesture.Gesture)` |  |
| `java.util.Set<java.lang.String> getGestureEntries()` |  |
| `java.util.ArrayList<android.gesture.Gesture> getGestures(String)` |  |
| `int getOrientationStyle()` |  |
| `int getSequenceType()` |  |
| `boolean isReadOnly()` |  |
| `abstract boolean load()` |  |
| `java.util.ArrayList<android.gesture.Prediction> recognize(android.gesture.Gesture)` |  |
| `void removeEntry(String)` |  |
| `void removeGesture(String, android.gesture.Gesture)` |  |
| `abstract boolean save()` |  |
| `void setOrientationStyle(int)` |  |
| `void setSequenceType(int)` |  |

---

### `class GestureOverlayView`

- **Extends:** `android.widget.FrameLayout`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GestureOverlayView(android.content.Context)` |  |
| `GestureOverlayView(android.content.Context, android.util.AttributeSet)` |  |
| `GestureOverlayView(android.content.Context, android.util.AttributeSet, int)` |  |
| `GestureOverlayView(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int GESTURE_STROKE_TYPE_MULTIPLE = 1` |  |
| `static final int GESTURE_STROKE_TYPE_SINGLE = 0` |  |
| `static final int ORIENTATION_HORIZONTAL = 0` |  |
| `static final int ORIENTATION_VERTICAL = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addOnGestureListener(android.gesture.GestureOverlayView.OnGestureListener)` |  |
| `void addOnGesturePerformedListener(android.gesture.GestureOverlayView.OnGesturePerformedListener)` |  |
| `void addOnGesturingListener(android.gesture.GestureOverlayView.OnGesturingListener)` |  |
| `void cancelClearAnimation()` |  |
| `void cancelGesture()` |  |
| `void clear(boolean)` |  |
| `java.util.ArrayList<android.gesture.GesturePoint> getCurrentStroke()` |  |
| `long getFadeOffset()` |  |
| `android.gesture.Gesture getGesture()` |  |
| `android.graphics.Path getGesturePath()` |  |
| `android.graphics.Path getGesturePath(android.graphics.Path)` |  |
| `float getGestureStrokeAngleThreshold()` |  |
| `float getGestureStrokeLengthThreshold()` |  |
| `float getGestureStrokeSquarenessTreshold()` |  |
| `int getGestureStrokeType()` |  |
| `float getGestureStrokeWidth()` |  |
| `int getOrientation()` |  |
| `boolean isEventsInterceptionEnabled()` |  |
| `boolean isFadeEnabled()` |  |
| `boolean isGestureVisible()` |  |
| `boolean isGesturing()` |  |
| `void removeAllOnGestureListeners()` |  |
| `void removeAllOnGesturePerformedListeners()` |  |
| `void removeAllOnGesturingListeners()` |  |
| `void removeOnGestureListener(android.gesture.GestureOverlayView.OnGestureListener)` |  |
| `void removeOnGesturePerformedListener(android.gesture.GestureOverlayView.OnGesturePerformedListener)` |  |
| `void removeOnGesturingListener(android.gesture.GestureOverlayView.OnGesturingListener)` |  |
| `void setEventsInterceptionEnabled(boolean)` |  |
| `void setFadeEnabled(boolean)` |  |
| `void setFadeOffset(long)` |  |
| `void setGesture(android.gesture.Gesture)` |  |
| `void setGestureColor(@ColorInt int)` |  |
| `void setGestureStrokeAngleThreshold(float)` |  |
| `void setGestureStrokeLengthThreshold(float)` |  |
| `void setGestureStrokeSquarenessTreshold(float)` |  |
| `void setGestureStrokeType(int)` |  |
| `void setGestureStrokeWidth(float)` |  |
| `void setGestureVisible(boolean)` |  |
| `void setOrientation(int)` |  |
| `void setUncertainGestureColor(@ColorInt int)` |  |

---

### `interface static GestureOverlayView.OnGestureListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onGesture(android.gesture.GestureOverlayView, android.view.MotionEvent)` |  |
| `void onGestureCancelled(android.gesture.GestureOverlayView, android.view.MotionEvent)` |  |
| `void onGestureEnded(android.gesture.GestureOverlayView, android.view.MotionEvent)` |  |
| `void onGestureStarted(android.gesture.GestureOverlayView, android.view.MotionEvent)` |  |

---

### `interface static GestureOverlayView.OnGesturePerformedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onGesturePerformed(android.gesture.GestureOverlayView, android.gesture.Gesture)` |  |

---

### `interface static GestureOverlayView.OnGesturingListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onGesturingEnded(android.gesture.GestureOverlayView)` |  |
| `void onGesturingStarted(android.gesture.GestureOverlayView)` |  |

---

### `class GesturePoint`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GesturePoint(float, float, long)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final long timestamp` |  |
| `final float x` |  |
| `final float y` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |

---

### `class GestureStore`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GestureStore()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ORIENTATION_INVARIANT = 1` |  |
| `static final int ORIENTATION_SENSITIVE = 2` |  |
| `static final int SEQUENCE_INVARIANT = 1` |  |
| `static final int SEQUENCE_SENSITIVE = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addGesture(String, android.gesture.Gesture)` |  |
| `java.util.Set<java.lang.String> getGestureEntries()` |  |
| `java.util.ArrayList<android.gesture.Gesture> getGestures(String)` |  |
| `int getOrientationStyle()` |  |
| `int getSequenceType()` |  |
| `boolean hasChanged()` |  |
| `void load(java.io.InputStream) throws java.io.IOException` |  |
| `void load(java.io.InputStream, boolean) throws java.io.IOException` |  |
| `java.util.ArrayList<android.gesture.Prediction> recognize(android.gesture.Gesture)` |  |
| `void removeEntry(String)` |  |
| `void removeGesture(String, android.gesture.Gesture)` |  |
| `void save(java.io.OutputStream) throws java.io.IOException` |  |
| `void save(java.io.OutputStream, boolean) throws java.io.IOException` |  |
| `void setOrientationStyle(int)` |  |
| `void setSequenceType(int)` |  |

---

### `class GestureStroke`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GestureStroke(java.util.ArrayList<android.gesture.GesturePoint>)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final android.graphics.RectF boundingBox` |  |
| `final float length` |  |
| `final float[] points` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clearPath()` |  |
| `Object clone()` |  |
| `android.gesture.OrientedBoundingBox computeOrientedBoundingBox()` |  |
| `android.graphics.Path getPath()` |  |
| `android.graphics.Path toPath(float, float, int)` |  |

---

### `class final GestureUtils`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.gesture.OrientedBoundingBox computeOrientedBoundingBox(java.util.ArrayList<android.gesture.GesturePoint>)` |  |
| `static android.gesture.OrientedBoundingBox computeOrientedBoundingBox(float[])` |  |
| `static float[] spatialSampling(android.gesture.Gesture, int)` |  |
| `static float[] spatialSampling(android.gesture.Gesture, int, boolean)` |  |
| `static float[] temporalSampling(android.gesture.GestureStroke, int)` |  |

---

### `class OrientedBoundingBox`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final float centerX` |  |
| `final float centerY` |  |
| `final float height` |  |
| `final float orientation` |  |
| `final float squareness` |  |
| `final float width` |  |

---

### `class Prediction`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final String name` |  |
| `double score` |  |

---

## Package: `android.icu.lang`

### `class final UCharacter`

- **Implements:** `android.icu.lang.UCharacterEnums.ECharacterCategory android.icu.lang.UCharacterEnums.ECharacterDirection`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FOLD_CASE_DEFAULT = 0` |  |
| `static final int FOLD_CASE_EXCLUDE_SPECIAL_I = 1` |  |
| `static final int MAX_CODE_POINT = 1114111` |  |
| `static final char MAX_HIGH_SURROGATE = 56319` |  |
| `static final char MAX_LOW_SURROGATE = 57343` |  |
| `static final int MAX_RADIX = 36` |  |
| `static final char MAX_SURROGATE = 57343` |  |
| `static final int MAX_VALUE = 1114111` |  |
| `static final int MIN_CODE_POINT = 0` |  |
| `static final char MIN_HIGH_SURROGATE = 55296` |  |
| `static final char MIN_LOW_SURROGATE = 56320` |  |
| `static final int MIN_RADIX = 2` |  |
| `static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536` |  |
| `static final char MIN_SURROGATE = 55296` |  |
| `static final int MIN_VALUE = 0` |  |
| `static final double NO_NUMERIC_VALUE = -1.23456789E8` |  |
| `static final int REPLACEMENT_CHAR = 65533` |  |
| `static final int SUPPLEMENTARY_MIN_VALUE = 65536` |  |
| `static final int TITLECASE_NO_BREAK_ADJUSTMENT = 512` |  |
| `static final int TITLECASE_NO_LOWERCASE = 256` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int charCount(int)` |  |
| `static int codePointAt(CharSequence, int)` |  |
| `static int codePointAt(char[], int)` |  |
| `static int codePointAt(char[], int, int)` |  |
| `static int codePointBefore(CharSequence, int)` |  |
| `static int codePointBefore(char[], int)` |  |
| `static int codePointBefore(char[], int, int)` |  |
| `static int codePointCount(CharSequence, int, int)` |  |
| `static int codePointCount(char[], int, int)` |  |
| `static int digit(int, int)` |  |
| `static int digit(int)` |  |
| `static int foldCase(int, boolean)` |  |
| `static String foldCase(String, boolean)` |  |
| `static int foldCase(int, int)` |  |
| `static String foldCase(String, int)` |  |
| `static char forDigit(int, int)` |  |
| `static android.icu.util.VersionInfo getAge(int)` |  |
| `static int getBidiPairedBracket(int)` |  |
| `static int getCharFromExtendedName(String)` |  |
| `static int getCharFromName(String)` |  |
| `static int getCharFromNameAlias(String)` |  |
| `static int getCodePoint(char, char)` |  |
| `static int getCodePoint(char)` |  |
| `static int getCombiningClass(int)` |  |
| `static int getDirection(int)` |  |
| `static byte getDirectionality(int)` |  |
| `static String getExtendedName(int)` |  |
| `static android.icu.util.ValueIterator getExtendedNameIterator()` |  |
| `static int getHanNumericValue(int)` |  |
| `static int getIntPropertyMaxValue(int)` |  |
| `static int getIntPropertyMinValue(int)` |  |
| `static int getIntPropertyValue(int, int)` |  |
| `static int getMirror(int)` |  |
| `static String getName(int)` |  |
| `static String getName(String, String)` |  |
| `static String getNameAlias(int)` |  |
| `static android.icu.util.ValueIterator getNameIterator()` |  |
| `static int getNumericValue(int)` |  |
| `static int getPropertyEnum(CharSequence)` |  |
| `static String getPropertyName(int, int)` |  |
| `static int getPropertyValueEnum(int, CharSequence)` |  |
| `static String getPropertyValueName(int, int, int)` |  |
| `static int getType(int)` |  |
| `static android.icu.util.RangeValueIterator getTypeIterator()` |  |
| `static double getUnicodeNumericValue(int)` |  |
| `static android.icu.util.VersionInfo getUnicodeVersion()` |  |
| `static boolean hasBinaryProperty(int, int)` |  |
| `static boolean isBMP(int)` |  |
| `static boolean isBaseForm(int)` |  |
| `static boolean isDefined(int)` |  |
| `static boolean isDigit(int)` |  |
| `static boolean isHighSurrogate(char)` |  |
| `static boolean isISOControl(int)` |  |
| `static boolean isIdentifierIgnorable(int)` |  |
| `static boolean isJavaIdentifierPart(int)` |  |
| `static boolean isJavaIdentifierStart(int)` |  |
| `static boolean isLegal(int)` |  |
| `static boolean isLegal(String)` |  |
| `static boolean isLetter(int)` |  |
| `static boolean isLetterOrDigit(int)` |  |
| `static boolean isLowSurrogate(char)` |  |
| `static boolean isLowerCase(int)` |  |
| `static boolean isMirrored(int)` |  |
| `static boolean isPrintable(int)` |  |
| `static boolean isSpaceChar(int)` |  |
| `static boolean isSupplementary(int)` |  |
| `static boolean isSupplementaryCodePoint(int)` |  |
| `static boolean isSurrogatePair(char, char)` |  |
| `static boolean isTitleCase(int)` |  |
| `static boolean isUAlphabetic(int)` |  |
| `static boolean isULowercase(int)` |  |
| `static boolean isUUppercase(int)` |  |
| `static boolean isUWhiteSpace(int)` |  |
| `static boolean isUnicodeIdentifierPart(int)` |  |
| `static boolean isUnicodeIdentifierStart(int)` |  |
| `static boolean isUpperCase(int)` |  |
| `static boolean isValidCodePoint(int)` |  |
| `static boolean isWhitespace(int)` |  |
| `static int offsetByCodePoints(CharSequence, int, int)` |  |
| `static int offsetByCodePoints(char[], int, int, int, int)` |  |
| `static int toChars(int, char[], int)` |  |
| `static char[] toChars(int)` |  |
| `static int toCodePoint(char, char)` |  |
| `static int toLowerCase(int)` |  |
| `static String toLowerCase(String)` |  |
| `static String toLowerCase(java.util.Locale, String)` |  |
| `static String toLowerCase(android.icu.util.ULocale, String)` |  |
| `static String toString(int)` |  |
| `static int toTitleCase(int)` |  |
| `static String toTitleCase(String, android.icu.text.BreakIterator)` |  |
| `static String toTitleCase(java.util.Locale, String, android.icu.text.BreakIterator)` |  |
| `static String toTitleCase(android.icu.util.ULocale, String, android.icu.text.BreakIterator)` |  |
| `static String toTitleCase(android.icu.util.ULocale, String, android.icu.text.BreakIterator, int)` |  |
| `static String toTitleCase(java.util.Locale, String, android.icu.text.BreakIterator, int)` |  |
| `static int toUpperCase(int)` |  |
| `static String toUpperCase(String)` |  |
| `static String toUpperCase(java.util.Locale, String)` |  |
| `static String toUpperCase(android.icu.util.ULocale, String)` |  |

---

### `interface static UCharacter.BidiPairedBracketType`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CLOSE = 2` |  |
| `static final int NONE = 0` |  |
| `static final int OPEN = 1` |  |

---

### `interface static UCharacter.DecompositionType`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CANONICAL = 1` |  |
| `static final int CIRCLE = 3` |  |
| `static final int COMPAT = 2` |  |
| `static final int FINAL = 4` |  |
| `static final int FONT = 5` |  |
| `static final int FRACTION = 6` |  |
| `static final int INITIAL = 7` |  |
| `static final int ISOLATED = 8` |  |
| `static final int MEDIAL = 9` |  |
| `static final int NARROW = 10` |  |
| `static final int NOBREAK = 11` |  |
| `static final int NONE = 0` |  |
| `static final int SMALL = 12` |  |
| `static final int SQUARE = 13` |  |
| `static final int SUB = 14` |  |
| `static final int SUPER = 15` |  |
| `static final int VERTICAL = 16` |  |
| `static final int WIDE = 17` |  |

---

### `interface static UCharacter.EastAsianWidth`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AMBIGUOUS = 1` |  |
| `static final int FULLWIDTH = 3` |  |
| `static final int HALFWIDTH = 2` |  |
| `static final int NARROW = 4` |  |
| `static final int NEUTRAL = 0` |  |
| `static final int WIDE = 5` |  |

---

### `interface static UCharacter.GraphemeClusterBreak`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CONTROL = 1` |  |
| `static final int CR = 2` |  |
| `static final int EXTEND = 3` |  |
| `static final int E_BASE = 13` |  |
| `static final int E_BASE_GAZ = 14` |  |
| `static final int E_MODIFIER = 15` |  |
| `static final int GLUE_AFTER_ZWJ = 16` |  |
| `static final int L = 4` |  |
| `static final int LF = 5` |  |
| `static final int LV = 6` |  |
| `static final int LVT = 7` |  |
| `static final int OTHER = 0` |  |
| `static final int PREPEND = 11` |  |
| `static final int REGIONAL_INDICATOR = 12` |  |
| `static final int SPACING_MARK = 10` |  |
| `static final int T = 8` |  |
| `static final int V = 9` |  |
| `static final int ZWJ = 17` |  |

---

### `interface static UCharacter.HangulSyllableType`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int LEADING_JAMO = 1` |  |
| `static final int LVT_SYLLABLE = 5` |  |
| `static final int LV_SYLLABLE = 4` |  |
| `static final int NOT_APPLICABLE = 0` |  |
| `static final int TRAILING_JAMO = 3` |  |
| `static final int VOWEL_JAMO = 2` |  |

---

### `interface static UCharacter.IndicPositionalCategory`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BOTTOM = 1` |  |
| `static final int BOTTOM_AND_LEFT = 2` |  |
| `static final int BOTTOM_AND_RIGHT = 3` |  |
| `static final int LEFT = 4` |  |
| `static final int LEFT_AND_RIGHT = 5` |  |
| `static final int NA = 0` |  |
| `static final int OVERSTRUCK = 6` |  |
| `static final int RIGHT = 7` |  |
| `static final int TOP = 8` |  |
| `static final int TOP_AND_BOTTOM = 9` |  |
| `static final int TOP_AND_BOTTOM_AND_LEFT = 15` |  |
| `static final int TOP_AND_BOTTOM_AND_RIGHT = 10` |  |
| `static final int TOP_AND_LEFT = 11` |  |
| `static final int TOP_AND_LEFT_AND_RIGHT = 12` |  |
| `static final int TOP_AND_RIGHT = 13` |  |
| `static final int VISUAL_ORDER_LEFT = 14` |  |

---

### `interface static UCharacter.IndicSyllabicCategory`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AVAGRAHA = 1` |  |
| `static final int BINDU = 2` |  |
| `static final int BRAHMI_JOINING_NUMBER = 3` |  |
| `static final int CANTILLATION_MARK = 4` |  |
| `static final int CONSONANT = 5` |  |
| `static final int CONSONANT_DEAD = 6` |  |
| `static final int CONSONANT_FINAL = 7` |  |
| `static final int CONSONANT_HEAD_LETTER = 8` |  |
| `static final int CONSONANT_INITIAL_POSTFIXED = 9` |  |
| `static final int CONSONANT_KILLER = 10` |  |
| `static final int CONSONANT_MEDIAL = 11` |  |
| `static final int CONSONANT_PLACEHOLDER = 12` |  |
| `static final int CONSONANT_PRECEDING_REPHA = 13` |  |
| `static final int CONSONANT_PREFIXED = 14` |  |
| `static final int CONSONANT_SUBJOINED = 15` |  |
| `static final int CONSONANT_SUCCEEDING_REPHA = 16` |  |
| `static final int CONSONANT_WITH_STACKER = 17` |  |
| `static final int GEMINATION_MARK = 18` |  |
| `static final int INVISIBLE_STACKER = 19` |  |
| `static final int JOINER = 20` |  |
| `static final int MODIFYING_LETTER = 21` |  |
| `static final int NON_JOINER = 22` |  |
| `static final int NUKTA = 23` |  |
| `static final int NUMBER = 24` |  |
| `static final int NUMBER_JOINER = 25` |  |
| `static final int OTHER = 0` |  |
| `static final int PURE_KILLER = 26` |  |
| `static final int REGISTER_SHIFTER = 27` |  |
| `static final int SYLLABLE_MODIFIER = 28` |  |
| `static final int TONE_LETTER = 29` |  |
| `static final int TONE_MARK = 30` |  |
| `static final int VIRAMA = 31` |  |
| `static final int VISARGA = 32` |  |
| `static final int VOWEL = 33` |  |
| `static final int VOWEL_DEPENDENT = 34` |  |
| `static final int VOWEL_INDEPENDENT = 35` |  |

---

### `interface static UCharacter.JoiningGroup`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AFRICAN_FEH = 86` |  |
| `static final int AFRICAN_NOON = 87` |  |
| `static final int AFRICAN_QAF = 88` |  |
| `static final int AIN = 1` |  |
| `static final int ALAPH = 2` |  |
| `static final int ALEF = 3` |  |
| `static final int BEH = 4` |  |
| `static final int BETH = 5` |  |
| `static final int BURUSHASKI_YEH_BARREE = 54` |  |
| `static final int DAL = 6` |  |
| `static final int DALATH_RISH = 7` |  |
| `static final int E = 8` |  |
| `static final int FARSI_YEH = 55` |  |
| `static final int FE = 51` |  |
| `static final int FEH = 9` |  |
| `static final int FINAL_SEMKATH = 10` |  |
| `static final int GAF = 11` |  |
| `static final int GAMAL = 12` |  |
| `static final int HAH = 13` |  |
| `static final int HAMZA_ON_HEH_GOAL = 14` |  |
| `static final int HANIFI_ROHINGYA_KINNA_YA = 100` |  |
| `static final int HANIFI_ROHINGYA_PA = 101` |  |
| `static final int HE = 15` |  |
| `static final int HEH = 16` |  |
| `static final int HEH_GOAL = 17` |  |
| `static final int HETH = 18` |  |
| `static final int KAF = 19` |  |
| `static final int KAPH = 20` |  |
| `static final int KHAPH = 52` |  |
| `static final int KNOTTED_HEH = 21` |  |
| `static final int LAM = 22` |  |
| `static final int LAMADH = 23` |  |
| `static final int MALAYALAM_BHA = 89` |  |
| `static final int MALAYALAM_JA = 90` |  |
| `static final int MALAYALAM_LLA = 91` |  |
| `static final int MALAYALAM_LLLA = 92` |  |
| `static final int MALAYALAM_NGA = 93` |  |
| `static final int MALAYALAM_NNA = 94` |  |
| `static final int MALAYALAM_NNNA = 95` |  |
| `static final int MALAYALAM_NYA = 96` |  |
| `static final int MALAYALAM_RA = 97` |  |
| `static final int MALAYALAM_SSA = 98` |  |
| `static final int MALAYALAM_TTA = 99` |  |
| `static final int MANICHAEAN_ALEPH = 58` |  |
| `static final int MANICHAEAN_AYIN = 59` |  |
| `static final int MANICHAEAN_BETH = 60` |  |
| `static final int MANICHAEAN_DALETH = 61` |  |
| `static final int MANICHAEAN_DHAMEDH = 62` |  |
| `static final int MANICHAEAN_FIVE = 63` |  |
| `static final int MANICHAEAN_GIMEL = 64` |  |
| `static final int MANICHAEAN_HETH = 65` |  |
| `static final int MANICHAEAN_HUNDRED = 66` |  |
| `static final int MANICHAEAN_KAPH = 67` |  |
| `static final int MANICHAEAN_LAMEDH = 68` |  |
| `static final int MANICHAEAN_MEM = 69` |  |
| `static final int MANICHAEAN_NUN = 70` |  |
| `static final int MANICHAEAN_ONE = 71` |  |
| `static final int MANICHAEAN_PE = 72` |  |
| `static final int MANICHAEAN_QOPH = 73` |  |
| `static final int MANICHAEAN_RESH = 74` |  |
| `static final int MANICHAEAN_SADHE = 75` |  |
| `static final int MANICHAEAN_SAMEKH = 76` |  |
| `static final int MANICHAEAN_TAW = 77` |  |
| `static final int MANICHAEAN_TEN = 78` |  |
| `static final int MANICHAEAN_TETH = 79` |  |
| `static final int MANICHAEAN_THAMEDH = 80` |  |
| `static final int MANICHAEAN_TWENTY = 81` |  |
| `static final int MANICHAEAN_WAW = 82` |  |
| `static final int MANICHAEAN_YODH = 83` |  |
| `static final int MANICHAEAN_ZAYIN = 84` |  |
| `static final int MEEM = 24` |  |
| `static final int MIM = 25` |  |
| `static final int NOON = 26` |  |
| `static final int NO_JOINING_GROUP = 0` |  |
| `static final int NUN = 27` |  |
| `static final int NYA = 56` |  |
| `static final int PE = 28` |  |
| `static final int QAF = 29` |  |
| `static final int QAPH = 30` |  |
| `static final int REH = 31` |  |
| `static final int REVERSED_PE = 32` |  |
| `static final int ROHINGYA_YEH = 57` |  |
| `static final int SAD = 33` |  |
| `static final int SADHE = 34` |  |
| `static final int SEEN = 35` |  |
| `static final int SEMKATH = 36` |  |
| `static final int SHIN = 37` |  |
| `static final int STRAIGHT_WAW = 85` |  |
| `static final int SWASH_KAF = 38` |  |
| `static final int SYRIAC_WAW = 39` |  |
| `static final int TAH = 40` |  |
| `static final int TAW = 41` |  |
| `static final int TEH_MARBUTA = 42` |  |
| `static final int TEH_MARBUTA_GOAL = 14` |  |
| `static final int TETH = 43` |  |
| `static final int WAW = 44` |  |
| `static final int YEH = 45` |  |
| `static final int YEH_BARREE = 46` |  |
| `static final int YEH_WITH_TAIL = 47` |  |
| `static final int YUDH = 48` |  |
| `static final int YUDH_HE = 49` |  |
| `static final int ZAIN = 50` |  |
| `static final int ZHAIN = 53` |  |

---

### `interface static UCharacter.JoiningType`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DUAL_JOINING = 2` |  |
| `static final int JOIN_CAUSING = 1` |  |
| `static final int LEFT_JOINING = 3` |  |
| `static final int NON_JOINING = 0` |  |
| `static final int RIGHT_JOINING = 4` |  |
| `static final int TRANSPARENT = 5` |  |

---

### `interface static UCharacter.LineBreak`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALPHABETIC = 2` |  |
| `static final int AMBIGUOUS = 1` |  |
| `static final int BREAK_AFTER = 4` |  |
| `static final int BREAK_BEFORE = 5` |  |
| `static final int BREAK_BOTH = 3` |  |
| `static final int BREAK_SYMBOLS = 27` |  |
| `static final int CARRIAGE_RETURN = 10` |  |
| `static final int CLOSE_PARENTHESIS = 36` |  |
| `static final int CLOSE_PUNCTUATION = 8` |  |
| `static final int COMBINING_MARK = 9` |  |
| `static final int COMPLEX_CONTEXT = 24` |  |
| `static final int CONDITIONAL_JAPANESE_STARTER = 37` |  |
| `static final int CONTINGENT_BREAK = 7` |  |
| `static final int EXCLAMATION = 11` |  |
| `static final int E_BASE = 40` |  |
| `static final int E_MODIFIER = 41` |  |
| `static final int GLUE = 12` |  |
| `static final int H2 = 31` |  |
| `static final int H3 = 32` |  |
| `static final int HEBREW_LETTER = 38` |  |
| `static final int HYPHEN = 13` |  |
| `static final int IDEOGRAPHIC = 14` |  |
| `static final int INFIX_NUMERIC = 16` |  |
| `static final int INSEPARABLE = 15` |  |
| `static final int INSEPERABLE = 15` |  |
| `static final int JL = 33` |  |
| `static final int JT = 34` |  |
| `static final int JV = 35` |  |
| `static final int LINE_FEED = 17` |  |
| `static final int MANDATORY_BREAK = 6` |  |
| `static final int NEXT_LINE = 29` |  |
| `static final int NONSTARTER = 18` |  |
| `static final int NUMERIC = 19` |  |
| `static final int OPEN_PUNCTUATION = 20` |  |
| `static final int POSTFIX_NUMERIC = 21` |  |
| `static final int PREFIX_NUMERIC = 22` |  |
| `static final int QUOTATION = 23` |  |
| `static final int REGIONAL_INDICATOR = 39` |  |
| `static final int SPACE = 26` |  |
| `static final int SURROGATE = 25` |  |
| `static final int UNKNOWN = 0` |  |
| `static final int WORD_JOINER = 30` |  |
| `static final int ZWJ = 42` |  |
| `static final int ZWSPACE = 28` |  |

---

### `interface static UCharacter.NumericType`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DECIMAL = 1` |  |
| `static final int DIGIT = 2` |  |
| `static final int NONE = 0` |  |
| `static final int NUMERIC = 3` |  |

---

### `interface static UCharacter.SentenceBreak`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ATERM = 1` |  |
| `static final int CLOSE = 2` |  |
| `static final int CR = 11` |  |
| `static final int EXTEND = 12` |  |
| `static final int FORMAT = 3` |  |
| `static final int LF = 13` |  |
| `static final int LOWER = 4` |  |
| `static final int NUMERIC = 5` |  |
| `static final int OLETTER = 6` |  |
| `static final int OTHER = 0` |  |
| `static final int SCONTINUE = 14` |  |
| `static final int SEP = 7` |  |
| `static final int SP = 8` |  |
| `static final int STERM = 9` |  |
| `static final int UPPER = 10` |  |

---

### `class static final UCharacter.UnicodeBlock`

- **Extends:** `java.lang.Character.Subset`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.lang.UCharacter.UnicodeBlock ADLAM` |  |
| `static final int ADLAM_ID = 263` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock AEGEAN_NUMBERS` |  |
| `static final int AEGEAN_NUMBERS_ID = 119` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock AHOM` |  |
| `static final int AHOM_ID = 253` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ALCHEMICAL_SYMBOLS` |  |
| `static final int ALCHEMICAL_SYMBOLS_ID = 208` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ALPHABETIC_PRESENTATION_FORMS` |  |
| `static final int ALPHABETIC_PRESENTATION_FORMS_ID = 80` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ANATOLIAN_HIEROGLYPHS` |  |
| `static final int ANATOLIAN_HIEROGLYPHS_ID = 254` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ANCIENT_GREEK_MUSICAL_NOTATION` |  |
| `static final int ANCIENT_GREEK_MUSICAL_NOTATION_ID = 126` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ANCIENT_GREEK_NUMBERS` |  |
| `static final int ANCIENT_GREEK_NUMBERS_ID = 127` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ANCIENT_SYMBOLS` |  |
| `static final int ANCIENT_SYMBOLS_ID = 165` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ARABIC` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ARABIC_EXTENDED_A` |  |
| `static final int ARABIC_EXTENDED_A_ID = 210` |  |
| `static final int ARABIC_ID = 12` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS` |  |
| `static final int ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS_ID = 211` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ARABIC_PRESENTATION_FORMS_A` |  |
| `static final int ARABIC_PRESENTATION_FORMS_A_ID = 81` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ARABIC_PRESENTATION_FORMS_B` |  |
| `static final int ARABIC_PRESENTATION_FORMS_B_ID = 85` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ARABIC_SUPPLEMENT` |  |
| `static final int ARABIC_SUPPLEMENT_ID = 128` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ARMENIAN` |  |
| `static final int ARMENIAN_ID = 10` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ARROWS` |  |
| `static final int ARROWS_ID = 46` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock AVESTAN` |  |
| `static final int AVESTAN_ID = 188` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BALINESE` |  |
| `static final int BALINESE_ID = 147` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BAMUM` |  |
| `static final int BAMUM_ID = 177` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BAMUM_SUPPLEMENT` |  |
| `static final int BAMUM_SUPPLEMENT_ID = 202` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BASIC_LATIN` |  |
| `static final int BASIC_LATIN_ID = 1` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BASSA_VAH` |  |
| `static final int BASSA_VAH_ID = 221` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BATAK` |  |
| `static final int BATAK_ID = 199` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BENGALI` |  |
| `static final int BENGALI_ID = 16` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BHAIKSUKI` |  |
| `static final int BHAIKSUKI_ID = 264` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BLOCK_ELEMENTS` |  |
| `static final int BLOCK_ELEMENTS_ID = 53` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BOPOMOFO` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BOPOMOFO_EXTENDED` |  |
| `static final int BOPOMOFO_EXTENDED_ID = 67` |  |
| `static final int BOPOMOFO_ID = 64` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BOX_DRAWING` |  |
| `static final int BOX_DRAWING_ID = 52` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BRAHMI` |  |
| `static final int BRAHMI_ID = 201` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BRAILLE_PATTERNS` |  |
| `static final int BRAILLE_PATTERNS_ID = 57` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BUGINESE` |  |
| `static final int BUGINESE_ID = 129` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BUHID` |  |
| `static final int BUHID_ID = 100` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS` |  |
| `static final int BYZANTINE_MUSICAL_SYMBOLS_ID = 91` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CARIAN` |  |
| `static final int CARIAN_ID = 168` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CAUCASIAN_ALBANIAN` |  |
| `static final int CAUCASIAN_ALBANIAN_ID = 222` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CHAKMA` |  |
| `static final int CHAKMA_ID = 212` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CHAM` |  |
| `static final int CHAM_ID = 164` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CHEROKEE` |  |
| `static final int CHEROKEE_ID = 32` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CHEROKEE_SUPPLEMENT` |  |
| `static final int CHEROKEE_SUPPLEMENT_ID = 255` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CHESS_SYMBOLS` |  |
| `static final int CHESS_SYMBOLS_ID = 281` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CHORASMIAN` |  |
| `static final int CHORASMIAN_ID = 301` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_COMPATIBILITY` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_COMPATIBILITY_FORMS` |  |
| `static final int CJK_COMPATIBILITY_FORMS_ID = 83` |  |
| `static final int CJK_COMPATIBILITY_ID = 69` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS` |  |
| `static final int CJK_COMPATIBILITY_IDEOGRAPHS_ID = 79` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT` |  |
| `static final int CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT_ID = 95` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_RADICALS_SUPPLEMENT` |  |
| `static final int CJK_RADICALS_SUPPLEMENT_ID = 58` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_STROKES` |  |
| `static final int CJK_STROKES_ID = 130` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION` |  |
| `static final int CJK_SYMBOLS_AND_PUNCTUATION_ID = 61` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A` |  |
| `static final int CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A_ID = 70` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B` |  |
| `static final int CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B_ID = 94` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C` |  |
| `static final int CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C_ID = 197` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D` |  |
| `static final int CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D_ID = 209` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_E` |  |
| `static final int CJK_UNIFIED_IDEOGRAPHS_EXTENSION_E_ID = 256` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_F` |  |
| `static final int CJK_UNIFIED_IDEOGRAPHS_EXTENSION_F_ID = 274` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_G` |  |
| `static final int CJK_UNIFIED_IDEOGRAPHS_EXTENSION_G_ID = 302` |  |
| `static final int CJK_UNIFIED_IDEOGRAPHS_ID = 71` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock COMBINING_DIACRITICAL_MARKS` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock COMBINING_DIACRITICAL_MARKS_EXTENDED` |  |
| `static final int COMBINING_DIACRITICAL_MARKS_EXTENDED_ID = 224` |  |
| `static final int COMBINING_DIACRITICAL_MARKS_ID = 7` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock COMBINING_DIACRITICAL_MARKS_SUPPLEMENT` |  |
| `static final int COMBINING_DIACRITICAL_MARKS_SUPPLEMENT_ID = 131` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock COMBINING_HALF_MARKS` |  |
| `static final int COMBINING_HALF_MARKS_ID = 82` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS` |  |
| `static final int COMBINING_MARKS_FOR_SYMBOLS_ID = 43` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock COMMON_INDIC_NUMBER_FORMS` |  |
| `static final int COMMON_INDIC_NUMBER_FORMS_ID = 178` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CONTROL_PICTURES` |  |
| `static final int CONTROL_PICTURES_ID = 49` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock COPTIC` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock COPTIC_EPACT_NUMBERS` |  |
| `static final int COPTIC_EPACT_NUMBERS_ID = 223` |  |
| `static final int COPTIC_ID = 132` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock COUNTING_ROD_NUMERALS` |  |
| `static final int COUNTING_ROD_NUMERALS_ID = 154` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CUNEIFORM` |  |
| `static final int CUNEIFORM_ID = 152` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CUNEIFORM_NUMBERS_AND_PUNCTUATION` |  |
| `static final int CUNEIFORM_NUMBERS_AND_PUNCTUATION_ID = 153` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CURRENCY_SYMBOLS` |  |
| `static final int CURRENCY_SYMBOLS_ID = 42` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CYPRIOT_SYLLABARY` |  |
| `static final int CYPRIOT_SYLLABARY_ID = 123` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CYRILLIC` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CYRILLIC_EXTENDED_A` |  |
| `static final int CYRILLIC_EXTENDED_A_ID = 158` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CYRILLIC_EXTENDED_B` |  |
| `static final int CYRILLIC_EXTENDED_B_ID = 160` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CYRILLIC_EXTENDED_C` |  |
| `static final int CYRILLIC_EXTENDED_C_ID = 265` |  |
| `static final int CYRILLIC_ID = 9` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CYRILLIC_SUPPLEMENT` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock CYRILLIC_SUPPLEMENTARY` |  |
| `static final int CYRILLIC_SUPPLEMENTARY_ID = 97` |  |
| `static final int CYRILLIC_SUPPLEMENT_ID = 97` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock DESERET` |  |
| `static final int DESERET_ID = 90` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock DEVANAGARI` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock DEVANAGARI_EXTENDED` |  |
| `static final int DEVANAGARI_EXTENDED_ID = 179` |  |
| `static final int DEVANAGARI_ID = 15` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock DINGBATS` |  |
| `static final int DINGBATS_ID = 56` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock DIVES_AKURU` |  |
| `static final int DIVES_AKURU_ID = 303` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock DOGRA` |  |
| `static final int DOGRA_ID = 282` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock DOMINO_TILES` |  |
| `static final int DOMINO_TILES_ID = 171` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock DUPLOYAN` |  |
| `static final int DUPLOYAN_ID = 225` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock EARLY_DYNASTIC_CUNEIFORM` |  |
| `static final int EARLY_DYNASTIC_CUNEIFORM_ID = 257` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock EGYPTIAN_HIEROGLYPHS` |  |
| `static final int EGYPTIAN_HIEROGLYPHS_ID = 194` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock EGYPTIAN_HIEROGLYPH_FORMAT_CONTROLS` |  |
| `static final int EGYPTIAN_HIEROGLYPH_FORMAT_CONTROLS_ID = 292` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ELBASAN` |  |
| `static final int ELBASAN_ID = 226` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ELYMAIC` |  |
| `static final int ELYMAIC_ID = 293` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock EMOTICONS` |  |
| `static final int EMOTICONS_ID = 206` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ENCLOSED_ALPHANUMERICS` |  |
| `static final int ENCLOSED_ALPHANUMERICS_ID = 51` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ENCLOSED_ALPHANUMERIC_SUPPLEMENT` |  |
| `static final int ENCLOSED_ALPHANUMERIC_SUPPLEMENT_ID = 195` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS` |  |
| `static final int ENCLOSED_CJK_LETTERS_AND_MONTHS_ID = 68` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ENCLOSED_IDEOGRAPHIC_SUPPLEMENT` |  |
| `static final int ENCLOSED_IDEOGRAPHIC_SUPPLEMENT_ID = 196` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ETHIOPIC` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ETHIOPIC_EXTENDED` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ETHIOPIC_EXTENDED_A` |  |
| `static final int ETHIOPIC_EXTENDED_A_ID = 200` |  |
| `static final int ETHIOPIC_EXTENDED_ID = 133` |  |
| `static final int ETHIOPIC_ID = 31` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ETHIOPIC_SUPPLEMENT` |  |
| `static final int ETHIOPIC_SUPPLEMENT_ID = 134` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GENERAL_PUNCTUATION` |  |
| `static final int GENERAL_PUNCTUATION_ID = 40` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GEOMETRIC_SHAPES` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GEOMETRIC_SHAPES_EXTENDED` |  |
| `static final int GEOMETRIC_SHAPES_EXTENDED_ID = 227` |  |
| `static final int GEOMETRIC_SHAPES_ID = 54` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GEORGIAN` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GEORGIAN_EXTENDED` |  |
| `static final int GEORGIAN_EXTENDED_ID = 283` |  |
| `static final int GEORGIAN_ID = 29` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GEORGIAN_SUPPLEMENT` |  |
| `static final int GEORGIAN_SUPPLEMENT_ID = 135` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GLAGOLITIC` |  |
| `static final int GLAGOLITIC_ID = 136` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GLAGOLITIC_SUPPLEMENT` |  |
| `static final int GLAGOLITIC_SUPPLEMENT_ID = 266` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GOTHIC` |  |
| `static final int GOTHIC_ID = 89` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GRANTHA` |  |
| `static final int GRANTHA_ID = 228` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GREEK` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GREEK_EXTENDED` |  |
| `static final int GREEK_EXTENDED_ID = 39` |  |
| `static final int GREEK_ID = 8` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GUJARATI` |  |
| `static final int GUJARATI_ID = 18` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GUNJALA_GONDI` |  |
| `static final int GUNJALA_GONDI_ID = 284` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock GURMUKHI` |  |
| `static final int GURMUKHI_ID = 17` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS` |  |
| `static final int HALFWIDTH_AND_FULLWIDTH_FORMS_ID = 87` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HANGUL_COMPATIBILITY_JAMO` |  |
| `static final int HANGUL_COMPATIBILITY_JAMO_ID = 65` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HANGUL_JAMO` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HANGUL_JAMO_EXTENDED_A` |  |
| `static final int HANGUL_JAMO_EXTENDED_A_ID = 180` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HANGUL_JAMO_EXTENDED_B` |  |
| `static final int HANGUL_JAMO_EXTENDED_B_ID = 185` |  |
| `static final int HANGUL_JAMO_ID = 30` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HANGUL_SYLLABLES` |  |
| `static final int HANGUL_SYLLABLES_ID = 74` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HANIFI_ROHINGYA` |  |
| `static final int HANIFI_ROHINGYA_ID = 285` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HANUNOO` |  |
| `static final int HANUNOO_ID = 99` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HATRAN` |  |
| `static final int HATRAN_ID = 258` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HEBREW` |  |
| `static final int HEBREW_ID = 11` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HIGH_PRIVATE_USE_SURROGATES` |  |
| `static final int HIGH_PRIVATE_USE_SURROGATES_ID = 76` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HIGH_SURROGATES` |  |
| `static final int HIGH_SURROGATES_ID = 75` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock HIRAGANA` |  |
| `static final int HIRAGANA_ID = 62` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS` |  |
| `static final int IDEOGRAPHIC_DESCRIPTION_CHARACTERS_ID = 60` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock IDEOGRAPHIC_SYMBOLS_AND_PUNCTUATION` |  |
| `static final int IDEOGRAPHIC_SYMBOLS_AND_PUNCTUATION_ID = 267` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock IMPERIAL_ARAMAIC` |  |
| `static final int IMPERIAL_ARAMAIC_ID = 186` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock INDIC_SIYAQ_NUMBERS` |  |
| `static final int INDIC_SIYAQ_NUMBERS_ID = 286` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock INSCRIPTIONAL_PAHLAVI` |  |
| `static final int INSCRIPTIONAL_PAHLAVI_ID = 190` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock INSCRIPTIONAL_PARTHIAN` |  |
| `static final int INSCRIPTIONAL_PARTHIAN_ID = 189` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock INVALID_CODE` |  |
| `static final int INVALID_CODE_ID = -1` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock IPA_EXTENSIONS` |  |
| `static final int IPA_EXTENSIONS_ID = 5` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock JAVANESE` |  |
| `static final int JAVANESE_ID = 181` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KAITHI` |  |
| `static final int KAITHI_ID = 193` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KANA_EXTENDED_A` |  |
| `static final int KANA_EXTENDED_A_ID = 275` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KANA_SUPPLEMENT` |  |
| `static final int KANA_SUPPLEMENT_ID = 203` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KANBUN` |  |
| `static final int KANBUN_ID = 66` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KANGXI_RADICALS` |  |
| `static final int KANGXI_RADICALS_ID = 59` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KANNADA` |  |
| `static final int KANNADA_ID = 22` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KATAKANA` |  |
| `static final int KATAKANA_ID = 63` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS` |  |
| `static final int KATAKANA_PHONETIC_EXTENSIONS_ID = 107` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KAYAH_LI` |  |
| `static final int KAYAH_LI_ID = 162` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KHAROSHTHI` |  |
| `static final int KHAROSHTHI_ID = 137` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KHITAN_SMALL_SCRIPT` |  |
| `static final int KHITAN_SMALL_SCRIPT_ID = 304` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KHMER` |  |
| `static final int KHMER_ID = 36` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KHMER_SYMBOLS` |  |
| `static final int KHMER_SYMBOLS_ID = 113` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KHOJKI` |  |
| `static final int KHOJKI_ID = 229` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock KHUDAWADI` |  |
| `static final int KHUDAWADI_ID = 230` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LAO` |  |
| `static final int LAO_ID = 26` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LATIN_1_SUPPLEMENT` |  |
| `static final int LATIN_1_SUPPLEMENT_ID = 2` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LATIN_EXTENDED_A` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LATIN_EXTENDED_ADDITIONAL` |  |
| `static final int LATIN_EXTENDED_ADDITIONAL_ID = 38` |  |
| `static final int LATIN_EXTENDED_A_ID = 3` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LATIN_EXTENDED_B` |  |
| `static final int LATIN_EXTENDED_B_ID = 4` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LATIN_EXTENDED_C` |  |
| `static final int LATIN_EXTENDED_C_ID = 148` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LATIN_EXTENDED_D` |  |
| `static final int LATIN_EXTENDED_D_ID = 149` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LATIN_EXTENDED_E` |  |
| `static final int LATIN_EXTENDED_E_ID = 231` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LEPCHA` |  |
| `static final int LEPCHA_ID = 156` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LETTERLIKE_SYMBOLS` |  |
| `static final int LETTERLIKE_SYMBOLS_ID = 44` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LIMBU` |  |
| `static final int LIMBU_ID = 111` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LINEAR_A` |  |
| `static final int LINEAR_A_ID = 232` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LINEAR_B_IDEOGRAMS` |  |
| `static final int LINEAR_B_IDEOGRAMS_ID = 118` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LINEAR_B_SYLLABARY` |  |
| `static final int LINEAR_B_SYLLABARY_ID = 117` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LISU` |  |
| `static final int LISU_ID = 176` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LISU_SUPPLEMENT` |  |
| `static final int LISU_SUPPLEMENT_ID = 305` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LOW_SURROGATES` |  |
| `static final int LOW_SURROGATES_ID = 77` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LYCIAN` |  |
| `static final int LYCIAN_ID = 167` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock LYDIAN` |  |
| `static final int LYDIAN_ID = 169` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MAHAJANI` |  |
| `static final int MAHAJANI_ID = 233` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MAHJONG_TILES` |  |
| `static final int MAHJONG_TILES_ID = 170` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MAKASAR` |  |
| `static final int MAKASAR_ID = 287` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MALAYALAM` |  |
| `static final int MALAYALAM_ID = 23` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MANDAIC` |  |
| `static final int MANDAIC_ID = 198` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MANICHAEAN` |  |
| `static final int MANICHAEAN_ID = 234` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MARCHEN` |  |
| `static final int MARCHEN_ID = 268` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MASARAM_GONDI` |  |
| `static final int MASARAM_GONDI_ID = 276` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS` |  |
| `static final int MATHEMATICAL_ALPHANUMERIC_SYMBOLS_ID = 93` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MATHEMATICAL_OPERATORS` |  |
| `static final int MATHEMATICAL_OPERATORS_ID = 47` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MAYAN_NUMERALS` |  |
| `static final int MAYAN_NUMERALS_ID = 288` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MEDEFAIDRIN` |  |
| `static final int MEDEFAIDRIN_ID = 289` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MEETEI_MAYEK` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MEETEI_MAYEK_EXTENSIONS` |  |
| `static final int MEETEI_MAYEK_EXTENSIONS_ID = 213` |  |
| `static final int MEETEI_MAYEK_ID = 184` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MENDE_KIKAKUI` |  |
| `static final int MENDE_KIKAKUI_ID = 235` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MEROITIC_CURSIVE` |  |
| `static final int MEROITIC_CURSIVE_ID = 214` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MEROITIC_HIEROGLYPHS` |  |
| `static final int MEROITIC_HIEROGLYPHS_ID = 215` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MIAO` |  |
| `static final int MIAO_ID = 216` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A` |  |
| `static final int MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A_ID = 102` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B` |  |
| `static final int MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B_ID = 105` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MISCELLANEOUS_SYMBOLS` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS` |  |
| `static final int MISCELLANEOUS_SYMBOLS_AND_ARROWS_ID = 115` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS` |  |
| `static final int MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS_ID = 205` |  |
| `static final int MISCELLANEOUS_SYMBOLS_ID = 55` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MISCELLANEOUS_TECHNICAL` |  |
| `static final int MISCELLANEOUS_TECHNICAL_ID = 48` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MODI` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MODIFIER_TONE_LETTERS` |  |
| `static final int MODIFIER_TONE_LETTERS_ID = 138` |  |
| `static final int MODI_ID = 236` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MONGOLIAN` |  |
| `static final int MONGOLIAN_ID = 37` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MONGOLIAN_SUPPLEMENT` |  |
| `static final int MONGOLIAN_SUPPLEMENT_ID = 269` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MRO` |  |
| `static final int MRO_ID = 237` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MULTANI` |  |
| `static final int MULTANI_ID = 259` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MUSICAL_SYMBOLS` |  |
| `static final int MUSICAL_SYMBOLS_ID = 92` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MYANMAR` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MYANMAR_EXTENDED_A` |  |
| `static final int MYANMAR_EXTENDED_A_ID = 182` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock MYANMAR_EXTENDED_B` |  |
| `static final int MYANMAR_EXTENDED_B_ID = 238` |  |
| `static final int MYANMAR_ID = 28` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock NABATAEAN` |  |
| `static final int NABATAEAN_ID = 239` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock NANDINAGARI` |  |
| `static final int NANDINAGARI_ID = 294` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock NEWA` |  |
| `static final int NEWA_ID = 270` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock NEW_TAI_LUE` |  |
| `static final int NEW_TAI_LUE_ID = 139` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock NKO` |  |
| `static final int NKO_ID = 146` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock NO_BLOCK` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock NUMBER_FORMS` |  |
| `static final int NUMBER_FORMS_ID = 45` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock NUSHU` |  |
| `static final int NUSHU_ID = 277` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock NYIAKENG_PUACHUE_HMONG` |  |
| `static final int NYIAKENG_PUACHUE_HMONG_ID = 295` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OGHAM` |  |
| `static final int OGHAM_ID = 34` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OLD_HUNGARIAN` |  |
| `static final int OLD_HUNGARIAN_ID = 260` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OLD_ITALIC` |  |
| `static final int OLD_ITALIC_ID = 88` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OLD_NORTH_ARABIAN` |  |
| `static final int OLD_NORTH_ARABIAN_ID = 240` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OLD_PERMIC` |  |
| `static final int OLD_PERMIC_ID = 241` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OLD_PERSIAN` |  |
| `static final int OLD_PERSIAN_ID = 140` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OLD_SOGDIAN` |  |
| `static final int OLD_SOGDIAN_ID = 290` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OLD_SOUTH_ARABIAN` |  |
| `static final int OLD_SOUTH_ARABIAN_ID = 187` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OLD_TURKIC` |  |
| `static final int OLD_TURKIC_ID = 191` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OL_CHIKI` |  |
| `static final int OL_CHIKI_ID = 157` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OPTICAL_CHARACTER_RECOGNITION` |  |
| `static final int OPTICAL_CHARACTER_RECOGNITION_ID = 50` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ORIYA` |  |
| `static final int ORIYA_ID = 19` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ORNAMENTAL_DINGBATS` |  |
| `static final int ORNAMENTAL_DINGBATS_ID = 242` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OSAGE` |  |
| `static final int OSAGE_ID = 271` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OSMANYA` |  |
| `static final int OSMANYA_ID = 122` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock OTTOMAN_SIYAQ_NUMBERS` |  |
| `static final int OTTOMAN_SIYAQ_NUMBERS_ID = 296` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PAHAWH_HMONG` |  |
| `static final int PAHAWH_HMONG_ID = 243` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PALMYRENE` |  |
| `static final int PALMYRENE_ID = 244` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PAU_CIN_HAU` |  |
| `static final int PAU_CIN_HAU_ID = 245` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PHAGS_PA` |  |
| `static final int PHAGS_PA_ID = 150` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PHAISTOS_DISC` |  |
| `static final int PHAISTOS_DISC_ID = 166` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PHOENICIAN` |  |
| `static final int PHOENICIAN_ID = 151` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PHONETIC_EXTENSIONS` |  |
| `static final int PHONETIC_EXTENSIONS_ID = 114` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PHONETIC_EXTENSIONS_SUPPLEMENT` |  |
| `static final int PHONETIC_EXTENSIONS_SUPPLEMENT_ID = 141` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PLAYING_CARDS` |  |
| `static final int PLAYING_CARDS_ID = 204` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PRIVATE_USE` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PRIVATE_USE_AREA` |  |
| `static final int PRIVATE_USE_AREA_ID = 78` |  |
| `static final int PRIVATE_USE_ID = 78` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock PSALTER_PAHLAVI` |  |
| `static final int PSALTER_PAHLAVI_ID = 246` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock REJANG` |  |
| `static final int REJANG_ID = 163` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock RUMI_NUMERAL_SYMBOLS` |  |
| `static final int RUMI_NUMERAL_SYMBOLS_ID = 192` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock RUNIC` |  |
| `static final int RUNIC_ID = 35` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SAMARITAN` |  |
| `static final int SAMARITAN_ID = 172` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SAURASHTRA` |  |
| `static final int SAURASHTRA_ID = 161` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SHARADA` |  |
| `static final int SHARADA_ID = 217` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SHAVIAN` |  |
| `static final int SHAVIAN_ID = 121` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SHORTHAND_FORMAT_CONTROLS` |  |
| `static final int SHORTHAND_FORMAT_CONTROLS_ID = 247` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SIDDHAM` |  |
| `static final int SIDDHAM_ID = 248` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SINHALA` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SINHALA_ARCHAIC_NUMBERS` |  |
| `static final int SINHALA_ARCHAIC_NUMBERS_ID = 249` |  |
| `static final int SINHALA_ID = 24` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SMALL_FORM_VARIANTS` |  |
| `static final int SMALL_FORM_VARIANTS_ID = 84` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SMALL_KANA_EXTENSION` |  |
| `static final int SMALL_KANA_EXTENSION_ID = 297` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SOGDIAN` |  |
| `static final int SOGDIAN_ID = 291` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SORA_SOMPENG` |  |
| `static final int SORA_SOMPENG_ID = 218` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SOYOMBO` |  |
| `static final int SOYOMBO_ID = 278` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SPACING_MODIFIER_LETTERS` |  |
| `static final int SPACING_MODIFIER_LETTERS_ID = 6` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SPECIALS` |  |
| `static final int SPECIALS_ID = 86` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUNDANESE` |  |
| `static final int SUNDANESE_ID = 155` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUNDANESE_SUPPLEMENT` |  |
| `static final int SUNDANESE_SUPPLEMENT_ID = 219` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS` |  |
| `static final int SUPERSCRIPTS_AND_SUBSCRIPTS_ID = 41` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUPPLEMENTAL_ARROWS_A` |  |
| `static final int SUPPLEMENTAL_ARROWS_A_ID = 103` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUPPLEMENTAL_ARROWS_B` |  |
| `static final int SUPPLEMENTAL_ARROWS_B_ID = 104` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUPPLEMENTAL_ARROWS_C` |  |
| `static final int SUPPLEMENTAL_ARROWS_C_ID = 250` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS` |  |
| `static final int SUPPLEMENTAL_MATHEMATICAL_OPERATORS_ID = 106` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUPPLEMENTAL_PUNCTUATION` |  |
| `static final int SUPPLEMENTAL_PUNCTUATION_ID = 142` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUPPLEMENTAL_SYMBOLS_AND_PICTOGRAPHS` |  |
| `static final int SUPPLEMENTAL_SYMBOLS_AND_PICTOGRAPHS_ID = 261` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A` |  |
| `static final int SUPPLEMENTARY_PRIVATE_USE_AREA_A_ID = 109` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B` |  |
| `static final int SUPPLEMENTARY_PRIVATE_USE_AREA_B_ID = 110` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SUTTON_SIGNWRITING` |  |
| `static final int SUTTON_SIGNWRITING_ID = 262` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SYLOTI_NAGRI` |  |
| `static final int SYLOTI_NAGRI_ID = 143` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SYMBOLS_AND_PICTOGRAPHS_EXTENDED_A` |  |
| `static final int SYMBOLS_AND_PICTOGRAPHS_EXTENDED_A_ID = 298` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SYMBOLS_FOR_LEGACY_COMPUTING` |  |
| `static final int SYMBOLS_FOR_LEGACY_COMPUTING_ID = 306` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SYRIAC` |  |
| `static final int SYRIAC_ID = 13` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock SYRIAC_SUPPLEMENT` |  |
| `static final int SYRIAC_SUPPLEMENT_ID = 279` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAGALOG` |  |
| `static final int TAGALOG_ID = 98` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAGBANWA` |  |
| `static final int TAGBANWA_ID = 101` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAGS` |  |
| `static final int TAGS_ID = 96` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAI_LE` |  |
| `static final int TAI_LE_ID = 112` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAI_THAM` |  |
| `static final int TAI_THAM_ID = 174` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAI_VIET` |  |
| `static final int TAI_VIET_ID = 183` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAI_XUAN_JING_SYMBOLS` |  |
| `static final int TAI_XUAN_JING_SYMBOLS_ID = 124` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAKRI` |  |
| `static final int TAKRI_ID = 220` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAMIL` |  |
| `static final int TAMIL_ID = 20` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TAMIL_SUPPLEMENT` |  |
| `static final int TAMIL_SUPPLEMENT_ID = 299` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TANGUT` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TANGUT_COMPONENTS` |  |
| `static final int TANGUT_COMPONENTS_ID = 273` |  |
| `static final int TANGUT_ID = 272` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TANGUT_SUPPLEMENT` |  |
| `static final int TANGUT_SUPPLEMENT_ID = 307` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TELUGU` |  |
| `static final int TELUGU_ID = 21` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock THAANA` |  |
| `static final int THAANA_ID = 14` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock THAI` |  |
| `static final int THAI_ID = 25` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TIBETAN` |  |
| `static final int TIBETAN_ID = 27` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TIFINAGH` |  |
| `static final int TIFINAGH_ID = 144` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TIRHUTA` |  |
| `static final int TIRHUTA_ID = 251` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock TRANSPORT_AND_MAP_SYMBOLS` |  |
| `static final int TRANSPORT_AND_MAP_SYMBOLS_ID = 207` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock UGARITIC` |  |
| `static final int UGARITIC_ID = 120` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED` |  |
| `static final int UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED_ID = 173` |  |
| `static final int UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_ID = 33` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock VAI` |  |
| `static final int VAI_ID = 159` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock VARIATION_SELECTORS` |  |
| `static final int VARIATION_SELECTORS_ID = 108` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT` |  |
| `static final int VARIATION_SELECTORS_SUPPLEMENT_ID = 125` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock VEDIC_EXTENSIONS` |  |
| `static final int VEDIC_EXTENSIONS_ID = 175` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock VERTICAL_FORMS` |  |
| `static final int VERTICAL_FORMS_ID = 145` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock WANCHO` |  |
| `static final int WANCHO_ID = 300` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock WARANG_CITI` |  |
| `static final int WARANG_CITI_ID = 252` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock YEZIDI` |  |
| `static final int YEZIDI_ID = 308` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock YIJING_HEXAGRAM_SYMBOLS` |  |
| `static final int YIJING_HEXAGRAM_SYMBOLS_ID = 116` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock YI_RADICALS` |  |
| `static final int YI_RADICALS_ID = 73` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock YI_SYLLABLES` |  |
| `static final int YI_SYLLABLES_ID = 72` |  |
| `static final android.icu.lang.UCharacter.UnicodeBlock ZANABAZAR_SQUARE` |  |
| `static final int ZANABAZAR_SQUARE_ID = 280` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.lang.UCharacter.UnicodeBlock forName(String)` |  |
| `int getID()` |  |
| `static android.icu.lang.UCharacter.UnicodeBlock getInstance(int)` |  |
| `static android.icu.lang.UCharacter.UnicodeBlock of(int)` |  |

---

### `interface static UCharacter.VerticalOrientation`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ROTATED = 0` |  |
| `static final int TRANSFORMED_ROTATED = 1` |  |
| `static final int TRANSFORMED_UPRIGHT = 2` |  |
| `static final int UPRIGHT = 3` |  |

---

### `interface static UCharacter.WordBreak`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALETTER = 1` |  |
| `static final int CR = 8` |  |
| `static final int DOUBLE_QUOTE = 16` |  |
| `static final int EXTEND = 9` |  |
| `static final int EXTENDNUMLET = 7` |  |
| `static final int E_BASE = 17` |  |
| `static final int E_BASE_GAZ = 18` |  |
| `static final int E_MODIFIER = 19` |  |
| `static final int FORMAT = 2` |  |
| `static final int GLUE_AFTER_ZWJ = 20` |  |
| `static final int HEBREW_LETTER = 14` |  |
| `static final int KATAKANA = 3` |  |
| `static final int LF = 10` |  |
| `static final int MIDLETTER = 4` |  |
| `static final int MIDNUM = 5` |  |
| `static final int MIDNUMLET = 11` |  |
| `static final int NEWLINE = 12` |  |
| `static final int NUMERIC = 6` |  |
| `static final int OTHER = 0` |  |
| `static final int REGIONAL_INDICATOR = 13` |  |
| `static final int SINGLE_QUOTE = 15` |  |
| `static final int WSEGSPACE = 22` |  |
| `static final int ZWJ = 21` |  |

---

### `class final UCharacterCategory`

- **Implements:** `android.icu.lang.UCharacterEnums.ECharacterCategory`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String toString(int)` |  |

---

### `class final UCharacterDirection`

- **Implements:** `android.icu.lang.UCharacterEnums.ECharacterDirection`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String toString(int)` |  |

---

### `class UCharacterEnums`


---

### `interface static UCharacterEnums.ECharacterCategory`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final byte COMBINING_SPACING_MARK = 8` |  |
| `static final byte CONNECTOR_PUNCTUATION = 22` |  |
| `static final byte CONTROL = 15` |  |
| `static final byte CURRENCY_SYMBOL = 25` |  |
| `static final byte DASH_PUNCTUATION = 19` |  |
| `static final byte DECIMAL_DIGIT_NUMBER = 9` |  |
| `static final byte ENCLOSING_MARK = 7` |  |
| `static final byte END_PUNCTUATION = 21` |  |
| `static final byte FINAL_PUNCTUATION = 29` |  |
| `static final byte FINAL_QUOTE_PUNCTUATION = 29` |  |
| `static final byte FORMAT = 16` |  |
| `static final byte GENERAL_OTHER_TYPES = 0` |  |
| `static final byte INITIAL_PUNCTUATION = 28` |  |
| `static final byte INITIAL_QUOTE_PUNCTUATION = 28` |  |
| `static final byte LETTER_NUMBER = 10` |  |
| `static final byte LINE_SEPARATOR = 13` |  |
| `static final byte LOWERCASE_LETTER = 2` |  |
| `static final byte MATH_SYMBOL = 24` |  |
| `static final byte MODIFIER_LETTER = 4` |  |
| `static final byte MODIFIER_SYMBOL = 26` |  |
| `static final byte NON_SPACING_MARK = 6` |  |
| `static final byte OTHER_LETTER = 5` |  |
| `static final byte OTHER_NUMBER = 11` |  |
| `static final byte OTHER_PUNCTUATION = 23` |  |
| `static final byte OTHER_SYMBOL = 27` |  |
| `static final byte PARAGRAPH_SEPARATOR = 14` |  |
| `static final byte PRIVATE_USE = 17` |  |
| `static final byte SPACE_SEPARATOR = 12` |  |
| `static final byte START_PUNCTUATION = 20` |  |
| `static final byte SURROGATE = 18` |  |
| `static final byte TITLECASE_LETTER = 3` |  |
| `static final byte UNASSIGNED = 0` |  |
| `static final byte UPPERCASE_LETTER = 1` |  |

---

### `interface static UCharacterEnums.ECharacterDirection`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ARABIC_NUMBER = 5` |  |
| `static final int BLOCK_SEPARATOR = 7` |  |
| `static final int BOUNDARY_NEUTRAL = 18` |  |
| `static final int COMMON_NUMBER_SEPARATOR = 6` |  |
| `static final byte DIRECTIONALITY_ARABIC_NUMBER = 5` |  |
| `static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 18` |  |
| `static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 6` |  |
| `static final byte DIRECTIONALITY_EUROPEAN_NUMBER = 2` |  |
| `static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 3` |  |
| `static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 4` |  |
| `static final byte DIRECTIONALITY_LEFT_TO_RIGHT = 0` |  |
| `static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 11` |  |
| `static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 12` |  |
| `static final byte DIRECTIONALITY_NONSPACING_MARK = 17` |  |
| `static final byte DIRECTIONALITY_OTHER_NEUTRALS = 10` |  |
| `static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 7` |  |
| `static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 16` |  |
| `static final byte DIRECTIONALITY_RIGHT_TO_LEFT = 1` |  |
| `static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 13` |  |
| `static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 14` |  |
| `static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 15` |  |
| `static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = 8` |  |
| `static final byte DIRECTIONALITY_UNDEFINED = -1` |  |
| `static final byte DIRECTIONALITY_WHITESPACE = 9` |  |
| `static final int DIR_NON_SPACING_MARK = 17` |  |
| `static final int EUROPEAN_NUMBER = 2` |  |
| `static final int EUROPEAN_NUMBER_SEPARATOR = 3` |  |
| `static final int EUROPEAN_NUMBER_TERMINATOR = 4` |  |
| `static final byte FIRST_STRONG_ISOLATE = 19` |  |
| `static final int LEFT_TO_RIGHT = 0` |  |
| `static final int LEFT_TO_RIGHT_EMBEDDING = 11` |  |
| `static final byte LEFT_TO_RIGHT_ISOLATE = 20` |  |
| `static final int LEFT_TO_RIGHT_OVERRIDE = 12` |  |
| `static final int OTHER_NEUTRAL = 10` |  |
| `static final int POP_DIRECTIONAL_FORMAT = 16` |  |
| `static final byte POP_DIRECTIONAL_ISOLATE = 22` |  |
| `static final int RIGHT_TO_LEFT = 1` |  |
| `static final int RIGHT_TO_LEFT_ARABIC = 13` |  |
| `static final int RIGHT_TO_LEFT_EMBEDDING = 14` |  |
| `static final byte RIGHT_TO_LEFT_ISOLATE = 21` |  |
| `static final int RIGHT_TO_LEFT_OVERRIDE = 15` |  |
| `static final int SEGMENT_SEPARATOR = 8` |  |
| `static final int WHITE_SPACE_NEUTRAL = 9` |  |

---

### `interface UProperty`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AGE = 16384` |  |
| `static final int ALPHABETIC = 0` |  |
| `static final int ASCII_HEX_DIGIT = 1` |  |
| `static final int BIDI_CLASS = 4096` |  |
| `static final int BIDI_CONTROL = 2` |  |
| `static final int BIDI_MIRRORED = 3` |  |
| `static final int BIDI_MIRRORING_GLYPH = 16385` |  |
| `static final int BIDI_PAIRED_BRACKET = 16397` |  |
| `static final int BIDI_PAIRED_BRACKET_TYPE = 4117` |  |
| `static final int BINARY_START = 0` |  |
| `static final int BLOCK = 4097` |  |
| `static final int CANONICAL_COMBINING_CLASS = 4098` |  |
| `static final int CASED = 49` |  |
| `static final int CASE_FOLDING = 16386` |  |
| `static final int CASE_IGNORABLE = 50` |  |
| `static final int CASE_SENSITIVE = 34` |  |
| `static final int CHANGES_WHEN_CASEFOLDED = 54` |  |
| `static final int CHANGES_WHEN_CASEMAPPED = 55` |  |
| `static final int CHANGES_WHEN_LOWERCASED = 51` |  |
| `static final int CHANGES_WHEN_NFKC_CASEFOLDED = 56` |  |
| `static final int CHANGES_WHEN_TITLECASED = 53` |  |
| `static final int CHANGES_WHEN_UPPERCASED = 52` |  |
| `static final int DASH = 4` |  |
| `static final int DECOMPOSITION_TYPE = 4099` |  |
| `static final int DEFAULT_IGNORABLE_CODE_POINT = 5` |  |
| `static final int DEPRECATED = 6` |  |
| `static final int DIACRITIC = 7` |  |
| `static final int DOUBLE_START = 12288` |  |
| `static final int EAST_ASIAN_WIDTH = 4100` |  |
| `static final int EMOJI = 57` |  |
| `static final int EMOJI_COMPONENT = 61` |  |
| `static final int EMOJI_MODIFIER = 59` |  |
| `static final int EMOJI_MODIFIER_BASE = 60` |  |
| `static final int EMOJI_PRESENTATION = 58` |  |
| `static final int EXTENDED_PICTOGRAPHIC = 64` |  |
| `static final int EXTENDER = 8` |  |
| `static final int FULL_COMPOSITION_EXCLUSION = 9` |  |
| `static final int GENERAL_CATEGORY = 4101` |  |
| `static final int GENERAL_CATEGORY_MASK = 8192` |  |
| `static final int GRAPHEME_BASE = 10` |  |
| `static final int GRAPHEME_CLUSTER_BREAK = 4114` |  |
| `static final int GRAPHEME_EXTEND = 11` |  |
| `static final int GRAPHEME_LINK = 12` |  |
| `static final int HANGUL_SYLLABLE_TYPE = 4107` |  |
| `static final int HEX_DIGIT = 13` |  |
| `static final int HYPHEN = 14` |  |
| `static final int IDEOGRAPHIC = 17` |  |
| `static final int IDS_BINARY_OPERATOR = 18` |  |
| `static final int IDS_TRINARY_OPERATOR = 19` |  |
| `static final int ID_CONTINUE = 15` |  |
| `static final int ID_START = 16` |  |
| `static final int INDIC_POSITIONAL_CATEGORY = 4118` |  |
| `static final int INDIC_SYLLABIC_CATEGORY = 4119` |  |
| `static final int INT_START = 4096` |  |
| `static final int JOINING_GROUP = 4102` |  |
| `static final int JOINING_TYPE = 4103` |  |
| `static final int JOIN_CONTROL = 20` |  |
| `static final int LEAD_CANONICAL_COMBINING_CLASS = 4112` |  |
| `static final int LINE_BREAK = 4104` |  |
| `static final int LOGICAL_ORDER_EXCEPTION = 21` |  |
| `static final int LOWERCASE = 22` |  |
| `static final int LOWERCASE_MAPPING = 16388` |  |
| `static final int MASK_START = 8192` |  |
| `static final int MATH = 23` |  |
| `static final int NAME = 16389` |  |
| `static final int NFC_INERT = 39` |  |
| `static final int NFC_QUICK_CHECK = 4110` |  |
| `static final int NFD_INERT = 37` |  |
| `static final int NFD_QUICK_CHECK = 4108` |  |
| `static final int NFKC_INERT = 40` |  |
| `static final int NFKC_QUICK_CHECK = 4111` |  |
| `static final int NFKD_INERT = 38` |  |
| `static final int NFKD_QUICK_CHECK = 4109` |  |
| `static final int NONCHARACTER_CODE_POINT = 24` |  |
| `static final int NUMERIC_TYPE = 4105` |  |
| `static final int NUMERIC_VALUE = 12288` |  |
| `static final int OTHER_PROPERTY_START = 28672` |  |
| `static final int PATTERN_SYNTAX = 42` |  |
| `static final int PATTERN_WHITE_SPACE = 43` |  |
| `static final int POSIX_ALNUM = 44` |  |
| `static final int POSIX_BLANK = 45` |  |
| `static final int POSIX_GRAPH = 46` |  |
| `static final int POSIX_PRINT = 47` |  |
| `static final int POSIX_XDIGIT = 48` |  |
| `static final int PREPENDED_CONCATENATION_MARK = 63` |  |
| `static final int QUOTATION_MARK = 25` |  |
| `static final int RADICAL = 26` |  |
| `static final int REGIONAL_INDICATOR = 62` |  |
| `static final int SCRIPT = 4106` |  |
| `static final int SCRIPT_EXTENSIONS = 28672` |  |
| `static final int SEGMENT_STARTER = 41` |  |
| `static final int SENTENCE_BREAK = 4115` |  |
| `static final int SIMPLE_CASE_FOLDING = 16390` |  |
| `static final int SIMPLE_LOWERCASE_MAPPING = 16391` |  |
| `static final int SIMPLE_TITLECASE_MAPPING = 16392` |  |
| `static final int SIMPLE_UPPERCASE_MAPPING = 16393` |  |
| `static final int SOFT_DOTTED = 27` |  |
| `static final int STRING_START = 16384` |  |
| `static final int S_TERM = 35` |  |
| `static final int TERMINAL_PUNCTUATION = 28` |  |
| `static final int TITLECASE_MAPPING = 16394` |  |
| `static final int TRAIL_CANONICAL_COMBINING_CLASS = 4113` |  |
| `static final int UNIFIED_IDEOGRAPH = 29` |  |
| `static final int UPPERCASE = 30` |  |
| `static final int UPPERCASE_MAPPING = 16396` |  |
| `static final int VARIATION_SELECTOR = 36` |  |
| `static final int VERTICAL_ORIENTATION = 4120` |  |
| `static final int WHITE_SPACE = 31` |  |
| `static final int WORD_BREAK = 4116` |  |
| `static final int XID_CONTINUE = 32` |  |
| `static final int XID_START = 33` |  |

---

### `interface static UProperty.NameChoice`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int LONG = 1` |  |
| `static final int SHORT = 0` |  |

---

### `class final UScript`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ADLAM = 167` |  |
| `static final int AFAKA = 147` |  |
| `static final int AHOM = 161` |  |
| `static final int ANATOLIAN_HIEROGLYPHS = 156` |  |
| `static final int ARABIC = 2` |  |
| `static final int ARMENIAN = 3` |  |
| `static final int AVESTAN = 117` |  |
| `static final int BALINESE = 62` |  |
| `static final int BAMUM = 130` |  |
| `static final int BASSA_VAH = 134` |  |
| `static final int BATAK = 63` |  |
| `static final int BENGALI = 4` |  |
| `static final int BHAIKSUKI = 168` |  |
| `static final int BLISSYMBOLS = 64` |  |
| `static final int BOOK_PAHLAVI = 124` |  |
| `static final int BOPOMOFO = 5` |  |
| `static final int BRAHMI = 65` |  |
| `static final int BRAILLE = 46` |  |
| `static final int BUGINESE = 55` |  |
| `static final int BUHID = 44` |  |
| `static final int CANADIAN_ABORIGINAL = 40` |  |
| `static final int CARIAN = 104` |  |
| `static final int CAUCASIAN_ALBANIAN = 159` |  |
| `static final int CHAKMA = 118` |  |
| `static final int CHAM = 66` |  |
| `static final int CHEROKEE = 6` |  |
| `static final int CHORASMIAN = 189` |  |
| `static final int CIRTH = 67` |  |
| `static final int COMMON = 0` |  |
| `static final int COPTIC = 7` |  |
| `static final int CUNEIFORM = 101` |  |
| `static final int CYPRIOT = 47` |  |
| `static final int CYRILLIC = 8` |  |
| `static final int DEMOTIC_EGYPTIAN = 69` |  |
| `static final int DESERET = 9` |  |
| `static final int DEVANAGARI = 10` |  |
| `static final int DIVES_AKURU = 190` |  |
| `static final int DOGRA = 178` |  |
| `static final int DUPLOYAN = 135` |  |
| `static final int EASTERN_SYRIAC = 97` |  |
| `static final int EGYPTIAN_HIEROGLYPHS = 71` |  |
| `static final int ELBASAN = 136` |  |
| `static final int ELYMAIC = 185` |  |
| `static final int ESTRANGELO_SYRIAC = 95` |  |
| `static final int ETHIOPIC = 11` |  |
| `static final int GEORGIAN = 12` |  |
| `static final int GLAGOLITIC = 56` |  |
| `static final int GOTHIC = 13` |  |
| `static final int GRANTHA = 137` |  |
| `static final int GREEK = 14` |  |
| `static final int GUJARATI = 15` |  |
| `static final int GUNJALA_GONDI = 179` |  |
| `static final int GURMUKHI = 16` |  |
| `static final int HAN = 17` |  |
| `static final int HANGUL = 18` |  |
| `static final int HANIFI_ROHINGYA = 182` |  |
| `static final int HANUNOO = 43` |  |
| `static final int HAN_WITH_BOPOMOFO = 172` |  |
| `static final int HARAPPAN_INDUS = 77` |  |
| `static final int HATRAN = 162` |  |
| `static final int HEBREW = 19` |  |
| `static final int HIERATIC_EGYPTIAN = 70` |  |
| `static final int HIRAGANA = 20` |  |
| `static final int IMPERIAL_ARAMAIC = 116` |  |
| `static final int INHERITED = 1` |  |
| `static final int INSCRIPTIONAL_PAHLAVI = 122` |  |
| `static final int INSCRIPTIONAL_PARTHIAN = 125` |  |
| `static final int INVALID_CODE = -1` |  |
| `static final int JAMO = 173` |  |
| `static final int JAPANESE = 105` |  |
| `static final int JAVANESE = 78` |  |
| `static final int JURCHEN = 148` |  |
| `static final int KAITHI = 120` |  |
| `static final int KANNADA = 21` |  |
| `static final int KATAKANA = 22` |  |
| `static final int KATAKANA_OR_HIRAGANA = 54` |  |
| `static final int KAYAH_LI = 79` |  |
| `static final int KHAROSHTHI = 57` |  |
| `static final int KHITAN_SMALL_SCRIPT = 191` |  |
| `static final int KHMER = 23` |  |
| `static final int KHOJKI = 157` |  |
| `static final int KHUDAWADI = 145` |  |
| `static final int KHUTSURI = 72` |  |
| `static final int KOREAN = 119` |  |
| `static final int KPELLE = 138` |  |
| `static final int LANNA = 106` |  |
| `static final int LAO = 24` |  |
| `static final int LATIN = 25` |  |
| `static final int LATIN_FRAKTUR = 80` |  |
| `static final int LATIN_GAELIC = 81` |  |
| `static final int LEPCHA = 82` |  |
| `static final int LIMBU = 48` |  |
| `static final int LINEAR_A = 83` |  |
| `static final int LINEAR_B = 49` |  |
| `static final int LISU = 131` |  |
| `static final int LOMA = 139` |  |
| `static final int LYCIAN = 107` |  |
| `static final int LYDIAN = 108` |  |
| `static final int MAHAJANI = 160` |  |
| `static final int MAKASAR = 180` |  |
| `static final int MALAYALAM = 26` |  |
| `static final int MANDAEAN = 84` |  |
| `static final int MANDAIC = 84` |  |
| `static final int MANICHAEAN = 121` |  |
| `static final int MARCHEN = 169` |  |
| `static final int MASARAM_GONDI = 175` |  |
| `static final int MATHEMATICAL_NOTATION = 128` |  |
| `static final int MAYAN_HIEROGLYPHS = 85` |  |
| `static final int MEDEFAIDRIN = 181` |  |
| `static final int MEITEI_MAYEK = 115` |  |
| `static final int MENDE = 140` |  |
| `static final int MEROITIC = 86` |  |
| `static final int MEROITIC_CURSIVE = 141` |  |
| `static final int MEROITIC_HIEROGLYPHS = 86` |  |
| `static final int MIAO = 92` |  |
| `static final int MODI = 163` |  |
| `static final int MONGOLIAN = 27` |  |
| `static final int MOON = 114` |  |
| `static final int MRO = 149` |  |
| `static final int MULTANI = 164` |  |
| `static final int MYANMAR = 28` |  |
| `static final int NABATAEAN = 143` |  |
| `static final int NAKHI_GEBA = 132` |  |
| `static final int NANDINAGARI = 187` |  |
| `static final int NEWA = 170` |  |
| `static final int NEW_TAI_LUE = 59` |  |
| `static final int NKO = 87` |  |
| `static final int NUSHU = 150` |  |
| `static final int NYIAKENG_PUACHUE_HMONG = 186` |  |
| `static final int OGHAM = 29` |  |
| `static final int OLD_CHURCH_SLAVONIC_CYRILLIC = 68` |  |
| `static final int OLD_HUNGARIAN = 76` |  |
| `static final int OLD_ITALIC = 30` |  |
| `static final int OLD_NORTH_ARABIAN = 142` |  |
| `static final int OLD_PERMIC = 89` |  |
| `static final int OLD_PERSIAN = 61` |  |
| `static final int OLD_SOGDIAN = 184` |  |
| `static final int OLD_SOUTH_ARABIAN = 133` |  |
| `static final int OL_CHIKI = 109` |  |
| `static final int ORIYA = 31` |  |
| `static final int ORKHON = 88` |  |
| `static final int OSAGE = 171` |  |
| `static final int OSMANYA = 50` |  |
| `static final int PAHAWH_HMONG = 75` |  |
| `static final int PALMYRENE = 144` |  |
| `static final int PAU_CIN_HAU = 165` |  |
| `static final int PHAGS_PA = 90` |  |
| `static final int PHOENICIAN = 91` |  |
| `static final int PHONETIC_POLLARD = 92` |  |
| `static final int PSALTER_PAHLAVI = 123` |  |
| `static final int REJANG = 110` |  |
| `static final int RONGORONGO = 93` |  |
| `static final int RUNIC = 32` |  |
| `static final int SAMARITAN = 126` |  |
| `static final int SARATI = 94` |  |
| `static final int SAURASHTRA = 111` |  |
| `static final int SHARADA = 151` |  |
| `static final int SHAVIAN = 51` |  |
| `static final int SIDDHAM = 166` |  |
| `static final int SIGN_WRITING = 112` |  |
| `static final int SIMPLIFIED_HAN = 73` |  |
| `static final int SINDHI = 145` |  |
| `static final int SINHALA = 33` |  |
| `static final int SOGDIAN = 183` |  |
| `static final int SORA_SOMPENG = 152` |  |
| `static final int SOYOMBO = 176` |  |
| `static final int SUNDANESE = 113` |  |
| `static final int SYLOTI_NAGRI = 58` |  |
| `static final int SYMBOLS = 129` |  |
| `static final int SYMBOLS_EMOJI = 174` |  |
| `static final int SYRIAC = 34` |  |
| `static final int TAGALOG = 42` |  |
| `static final int TAGBANWA = 45` |  |
| `static final int TAI_LE = 52` |  |
| `static final int TAI_VIET = 127` |  |
| `static final int TAKRI = 153` |  |
| `static final int TAMIL = 35` |  |
| `static final int TANGUT = 154` |  |
| `static final int TELUGU = 36` |  |
| `static final int TENGWAR = 98` |  |
| `static final int THAANA = 37` |  |
| `static final int THAI = 38` |  |
| `static final int TIBETAN = 39` |  |
| `static final int TIFINAGH = 60` |  |
| `static final int TIRHUTA = 158` |  |
| `static final int TRADITIONAL_HAN = 74` |  |
| `static final int UCAS = 40` |  |
| `static final int UGARITIC = 53` |  |
| `static final int UNKNOWN = 103` |  |
| `static final int UNWRITTEN_LANGUAGES = 102` |  |
| `static final int VAI = 99` |  |
| `static final int VISIBLE_SPEECH = 100` |  |
| `static final int WANCHO = 188` |  |
| `static final int WARANG_CITI = 146` |  |
| `static final int WESTERN_SYRIAC = 96` |  |
| `static final int WOLEAI = 155` |  |
| `static final int YEZIDI = 192` |  |
| `static final int YI = 41` |  |
| `static final int ZANABAZAR_SQUARE = 177` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static boolean breaksBetweenLetters(int)` |  |
| `static int[] getCode(java.util.Locale)` |  |
| `static int[] getCode(android.icu.util.ULocale)` |  |
| `static int[] getCode(String)` |  |
| `static int getCodeFromName(String)` |  |
| `static String getName(int)` |  |
| `static String getSampleString(int)` |  |
| `static int getScript(int)` |  |
| `static int getScriptExtensions(int, java.util.BitSet)` |  |
| `static String getShortName(int)` |  |
| `static android.icu.lang.UScript.ScriptUsage getUsage(int)` |  |
| `static boolean hasScript(int, int)` |  |
| `static boolean isCased(int)` |  |
| `static boolean isRightToLeft(int)` |  |

---

### `enum UScript.ScriptUsage`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.lang.UScript.ScriptUsage ASPIRATIONAL` |  |
| `static final android.icu.lang.UScript.ScriptUsage EXCLUDED` |  |
| `static final android.icu.lang.UScript.ScriptUsage LIMITED_USE` |  |
| `static final android.icu.lang.UScript.ScriptUsage NOT_ENCODED` |  |
| `static final android.icu.lang.UScript.ScriptUsage RECOMMENDED` |  |
| `static final android.icu.lang.UScript.ScriptUsage UNKNOWN` |  |

---

## Package: `android.icu.math`

### `class BigDecimal`

- **Extends:** `java.lang.Number`
- **Implements:** `java.lang.Comparable<android.icu.math.BigDecimal> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BigDecimal(java.math.BigDecimal)` |  |
| `BigDecimal(java.math.BigInteger)` |  |
| `BigDecimal(java.math.BigInteger, int)` |  |
| `BigDecimal(char[])` |  |
| `BigDecimal(char[], int, int)` |  |
| `BigDecimal(double)` |  |
| `BigDecimal(int)` |  |
| `BigDecimal(long)` |  |
| `BigDecimal(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.math.BigDecimal ONE` |  |
| `static final int ROUND_CEILING = 2` |  |
| `static final int ROUND_DOWN = 1` |  |
| `static final int ROUND_FLOOR = 3` |  |
| `static final int ROUND_HALF_DOWN = 5` |  |
| `static final int ROUND_HALF_EVEN = 6` |  |
| `static final int ROUND_HALF_UP = 4` |  |
| `static final int ROUND_UNNECESSARY = 7` |  |
| `static final int ROUND_UP = 0` |  |
| `static final android.icu.math.BigDecimal TEN` |  |
| `static final android.icu.math.BigDecimal ZERO` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.math.BigDecimal abs()` |  |
| `android.icu.math.BigDecimal abs(android.icu.math.MathContext)` |  |
| `android.icu.math.BigDecimal add(android.icu.math.BigDecimal)` |  |
| `android.icu.math.BigDecimal add(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `byte byteValueExact()` |  |
| `int compareTo(android.icu.math.BigDecimal)` |  |
| `int compareTo(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `android.icu.math.BigDecimal divide(android.icu.math.BigDecimal)` |  |
| `android.icu.math.BigDecimal divide(android.icu.math.BigDecimal, int)` |  |
| `android.icu.math.BigDecimal divide(android.icu.math.BigDecimal, int, int)` |  |
| `android.icu.math.BigDecimal divide(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `android.icu.math.BigDecimal divideInteger(android.icu.math.BigDecimal)` |  |
| `android.icu.math.BigDecimal divideInteger(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `double doubleValue()` |  |
| `float floatValue()` |  |
| `String format(int, int)` |  |
| `String format(int, int, int, int, int, int)` |  |
| `int intValue()` |  |
| `int intValueExact()` |  |
| `long longValue()` |  |
| `long longValueExact()` |  |
| `android.icu.math.BigDecimal max(android.icu.math.BigDecimal)` |  |
| `android.icu.math.BigDecimal max(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `android.icu.math.BigDecimal min(android.icu.math.BigDecimal)` |  |
| `android.icu.math.BigDecimal min(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `android.icu.math.BigDecimal movePointLeft(int)` |  |
| `android.icu.math.BigDecimal movePointRight(int)` |  |
| `android.icu.math.BigDecimal multiply(android.icu.math.BigDecimal)` |  |
| `android.icu.math.BigDecimal multiply(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `android.icu.math.BigDecimal negate()` |  |
| `android.icu.math.BigDecimal negate(android.icu.math.MathContext)` |  |
| `android.icu.math.BigDecimal plus()` |  |
| `android.icu.math.BigDecimal plus(android.icu.math.MathContext)` |  |
| `android.icu.math.BigDecimal pow(android.icu.math.BigDecimal)` |  |
| `android.icu.math.BigDecimal pow(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `android.icu.math.BigDecimal remainder(android.icu.math.BigDecimal)` |  |
| `android.icu.math.BigDecimal remainder(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `int scale()` |  |
| `android.icu.math.BigDecimal setScale(int)` |  |
| `android.icu.math.BigDecimal setScale(int, int)` |  |
| `short shortValueExact()` |  |
| `int signum()` |  |
| `android.icu.math.BigDecimal subtract(android.icu.math.BigDecimal)` |  |
| `android.icu.math.BigDecimal subtract(android.icu.math.BigDecimal, android.icu.math.MathContext)` |  |
| `java.math.BigDecimal toBigDecimal()` |  |
| `java.math.BigInteger toBigInteger()` |  |
| `java.math.BigInteger toBigIntegerExact()` |  |
| `char[] toCharArray()` |  |
| `java.math.BigInteger unscaledValue()` |  |
| `static android.icu.math.BigDecimal valueOf(double)` |  |
| `static android.icu.math.BigDecimal valueOf(long)` |  |
| `static android.icu.math.BigDecimal valueOf(long, int)` |  |

---

### `class final MathContext`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MathContext(int)` |  |
| `MathContext(int, int)` |  |
| `MathContext(int, int, boolean)` |  |
| `MathContext(int, int, boolean, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.math.MathContext DEFAULT` |  |
| `static final int ENGINEERING = 2` |  |
| `static final int PLAIN = 0` |  |
| `static final int ROUND_CEILING = 2` |  |
| `static final int ROUND_DOWN = 1` |  |
| `static final int ROUND_FLOOR = 3` |  |
| `static final int ROUND_HALF_DOWN = 5` |  |
| `static final int ROUND_HALF_EVEN = 6` |  |
| `static final int ROUND_HALF_UP = 4` |  |
| `static final int ROUND_UNNECESSARY = 7` |  |
| `static final int ROUND_UP = 0` |  |
| `static final int SCIENTIFIC = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getDigits()` |  |
| `int getForm()` |  |
| `boolean getLostDigits()` |  |
| `int getRoundingMode()` |  |

---

## Package: `android.icu.number`

### `class CompactNotation`

- **Extends:** `android.icu.number.Notation`

---

### `class abstract CurrencyPrecision`

- **Extends:** `android.icu.number.Precision`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.number.Precision withCurrency(android.icu.util.Currency)` |  |

---

### `class FormattedNumber`

- **Implements:** `java.lang.CharSequence`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `char charAt(int)` |  |
| `int length()` |  |
| `CharSequence subSequence(int, int)` |  |
| `java.math.BigDecimal toBigDecimal()` |  |
| `java.text.AttributedCharacterIterator toCharacterIterator()` |  |

---

### `class FormattedNumberRange`

- **Implements:** `java.lang.CharSequence`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `char charAt(int)` |  |
| `java.math.BigDecimal getFirstBigDecimal()` |  |
| `android.icu.number.NumberRangeFormatter.RangeIdentityResult getIdentityResult()` |  |
| `java.math.BigDecimal getSecondBigDecimal()` |  |
| `int length()` |  |
| `CharSequence subSequence(int, int)` |  |
| `java.text.AttributedCharacterIterator toCharacterIterator()` |  |

---

### `class abstract FractionPrecision`

- **Extends:** `android.icu.number.Precision`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.number.Precision withMaxDigits(int)` |  |
| `android.icu.number.Precision withMinDigits(int)` |  |

---

### `class IntegerWidth`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.number.IntegerWidth truncateAt(int)` |  |
| `static android.icu.number.IntegerWidth zeroFillTo(int)` |  |

---

### `class LocalizedNumberFormatter`

- **Extends:** `android.icu.number.NumberFormatterSettings<android.icu.number.LocalizedNumberFormatter>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.number.FormattedNumber format(long)` |  |
| `android.icu.number.FormattedNumber format(double)` |  |
| `android.icu.number.FormattedNumber format(Number)` |  |
| `android.icu.number.FormattedNumber format(android.icu.util.Measure)` |  |
| `java.text.Format toFormat()` |  |

---

### `class LocalizedNumberRangeFormatter`

- **Extends:** `android.icu.number.NumberRangeFormatterSettings<android.icu.number.LocalizedNumberRangeFormatter>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.number.FormattedNumberRange formatRange(int, int)` |  |
| `android.icu.number.FormattedNumberRange formatRange(double, double)` |  |
| `android.icu.number.FormattedNumberRange formatRange(Number, Number)` |  |

---

### `class Notation`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.number.CompactNotation compactLong()` |  |
| `static android.icu.number.CompactNotation compactShort()` |  |
| `static android.icu.number.ScientificNotation engineering()` |  |
| `static android.icu.number.ScientificNotation scientific()` |  |
| `static android.icu.number.SimpleNotation simple()` |  |

---

### `class final NumberFormatter`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.number.UnlocalizedNumberFormatter with()` |  |
| `static android.icu.number.LocalizedNumberFormatter withLocale(java.util.Locale)` |  |
| `static android.icu.number.LocalizedNumberFormatter withLocale(android.icu.util.ULocale)` |  |

---

### `enum NumberFormatter.DecimalSeparatorDisplay`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.number.NumberFormatter.DecimalSeparatorDisplay ALWAYS` |  |
| `static final android.icu.number.NumberFormatter.DecimalSeparatorDisplay AUTO` |  |

---

### `enum NumberFormatter.GroupingStrategy`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.number.NumberFormatter.GroupingStrategy AUTO` |  |
| `static final android.icu.number.NumberFormatter.GroupingStrategy MIN2` |  |
| `static final android.icu.number.NumberFormatter.GroupingStrategy OFF` |  |
| `static final android.icu.number.NumberFormatter.GroupingStrategy ON_ALIGNED` |  |
| `static final android.icu.number.NumberFormatter.GroupingStrategy THOUSANDS` |  |

---

### `enum NumberFormatter.SignDisplay`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.number.NumberFormatter.SignDisplay ACCOUNTING` |  |
| `static final android.icu.number.NumberFormatter.SignDisplay ACCOUNTING_ALWAYS` |  |
| `static final android.icu.number.NumberFormatter.SignDisplay ACCOUNTING_EXCEPT_ZERO` |  |
| `static final android.icu.number.NumberFormatter.SignDisplay ALWAYS` |  |
| `static final android.icu.number.NumberFormatter.SignDisplay AUTO` |  |
| `static final android.icu.number.NumberFormatter.SignDisplay EXCEPT_ZERO` |  |
| `static final android.icu.number.NumberFormatter.SignDisplay NEVER` |  |

---

### `enum NumberFormatter.UnitWidth`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.number.NumberFormatter.UnitWidth FULL_NAME` |  |
| `static final android.icu.number.NumberFormatter.UnitWidth HIDDEN` |  |
| `static final android.icu.number.NumberFormatter.UnitWidth ISO_CODE` |  |
| `static final android.icu.number.NumberFormatter.UnitWidth NARROW` |  |
| `static final android.icu.number.NumberFormatter.UnitWidth SHORT` |  |

---

### `class abstract NumberFormatterSettings<T`

- **Extends:** `android.icu.number.NumberFormatterSettings<?>>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T decimal(android.icu.number.NumberFormatter.DecimalSeparatorDisplay)` |  |
| `T grouping(android.icu.number.NumberFormatter.GroupingStrategy)` |  |
| `T integerWidth(android.icu.number.IntegerWidth)` |  |
| `T notation(android.icu.number.Notation)` |  |
| `T perUnit(android.icu.util.MeasureUnit)` |  |
| `T precision(android.icu.number.Precision)` |  |
| `T roundingMode(java.math.RoundingMode)` |  |
| `T scale(android.icu.number.Scale)` |  |
| `T sign(android.icu.number.NumberFormatter.SignDisplay)` |  |
| `T symbols(android.icu.text.DecimalFormatSymbols)` |  |
| `T symbols(android.icu.text.NumberingSystem)` |  |
| `T unit(android.icu.util.MeasureUnit)` |  |
| `T unitWidth(android.icu.number.NumberFormatter.UnitWidth)` |  |

---

### `class abstract NumberRangeFormatter`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.number.UnlocalizedNumberRangeFormatter with()` |  |
| `static android.icu.number.LocalizedNumberRangeFormatter withLocale(java.util.Locale)` |  |
| `static android.icu.number.LocalizedNumberRangeFormatter withLocale(android.icu.util.ULocale)` |  |

---

### `enum NumberRangeFormatter.RangeCollapse`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.number.NumberRangeFormatter.RangeCollapse ALL` |  |
| `static final android.icu.number.NumberRangeFormatter.RangeCollapse AUTO` |  |
| `static final android.icu.number.NumberRangeFormatter.RangeCollapse NONE` |  |
| `static final android.icu.number.NumberRangeFormatter.RangeCollapse UNIT` |  |

---

### `enum NumberRangeFormatter.RangeIdentityFallback`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.number.NumberRangeFormatter.RangeIdentityFallback APPROXIMATELY` |  |
| `static final android.icu.number.NumberRangeFormatter.RangeIdentityFallback APPROXIMATELY_OR_SINGLE_VALUE` |  |
| `static final android.icu.number.NumberRangeFormatter.RangeIdentityFallback RANGE` |  |
| `static final android.icu.number.NumberRangeFormatter.RangeIdentityFallback SINGLE_VALUE` |  |

---

### `enum NumberRangeFormatter.RangeIdentityResult`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.number.NumberRangeFormatter.RangeIdentityResult EQUAL_AFTER_ROUNDING` |  |
| `static final android.icu.number.NumberRangeFormatter.RangeIdentityResult EQUAL_BEFORE_ROUNDING` |  |
| `static final android.icu.number.NumberRangeFormatter.RangeIdentityResult NOT_EQUAL` |  |

---

### `class abstract NumberRangeFormatterSettings<T`

- **Extends:** `android.icu.number.NumberRangeFormatterSettings<?>>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T collapse(android.icu.number.NumberRangeFormatter.RangeCollapse)` |  |
| `T identityFallback(android.icu.number.NumberRangeFormatter.RangeIdentityFallback)` |  |
| `T numberFormatterBoth(android.icu.number.UnlocalizedNumberFormatter)` |  |
| `T numberFormatterFirst(android.icu.number.UnlocalizedNumberFormatter)` |  |
| `T numberFormatterSecond(android.icu.number.UnlocalizedNumberFormatter)` |  |

---

### `class abstract Precision`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.number.CurrencyPrecision currency(android.icu.util.Currency.CurrencyUsage)` |  |
| `static android.icu.number.FractionPrecision fixedFraction(int)` |  |
| `static android.icu.number.Precision fixedSignificantDigits(int)` |  |
| `static android.icu.number.Precision increment(java.math.BigDecimal)` |  |
| `static android.icu.number.FractionPrecision integer()` |  |
| `static android.icu.number.FractionPrecision maxFraction(int)` |  |
| `static android.icu.number.Precision maxSignificantDigits(int)` |  |
| `static android.icu.number.FractionPrecision minFraction(int)` |  |
| `static android.icu.number.FractionPrecision minMaxFraction(int, int)` |  |
| `static android.icu.number.Precision minMaxSignificantDigits(int, int)` |  |
| `static android.icu.number.Precision minSignificantDigits(int)` |  |
| `static android.icu.number.Precision unlimited()` |  |

---

### `class Scale`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.number.Scale byBigDecimal(java.math.BigDecimal)` |  |
| `static android.icu.number.Scale byDouble(double)` |  |
| `static android.icu.number.Scale byDoubleAndPowerOfTen(double, int)` |  |
| `static android.icu.number.Scale none()` |  |
| `static android.icu.number.Scale powerOfTen(int)` |  |

---

### `class ScientificNotation`

- **Extends:** `android.icu.number.Notation`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.number.ScientificNotation withExponentSignDisplay(android.icu.number.NumberFormatter.SignDisplay)` |  |
| `android.icu.number.ScientificNotation withMinExponentDigits(int)` |  |

---

### `class SimpleNotation`

- **Extends:** `android.icu.number.Notation`

---

### `class UnlocalizedNumberFormatter`

- **Extends:** `android.icu.number.NumberFormatterSettings<android.icu.number.UnlocalizedNumberFormatter>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.number.LocalizedNumberFormatter locale(java.util.Locale)` |  |
| `android.icu.number.LocalizedNumberFormatter locale(android.icu.util.ULocale)` |  |

---

### `class UnlocalizedNumberRangeFormatter`

- **Extends:** `android.icu.number.NumberRangeFormatterSettings<android.icu.number.UnlocalizedNumberRangeFormatter>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.number.LocalizedNumberRangeFormatter locale(java.util.Locale)` |  |
| `android.icu.number.LocalizedNumberRangeFormatter locale(android.icu.util.ULocale)` |  |

---

## Package: `android.icu.text`

### `class final AlphabeticIndex<V>`

- **Implements:** `java.lang.Iterable<android.icu.text.AlphabeticIndex.Bucket<V>>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AlphabeticIndex(android.icu.util.ULocale)` |  |
| `AlphabeticIndex(java.util.Locale)` |  |
| `AlphabeticIndex(android.icu.text.RuleBasedCollator)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.text.AlphabeticIndex<V> addLabels(android.icu.text.UnicodeSet)` |  |
| `android.icu.text.AlphabeticIndex<V> addLabels(android.icu.util.ULocale...)` |  |
| `android.icu.text.AlphabeticIndex<V> addLabels(java.util.Locale...)` |  |
| `android.icu.text.AlphabeticIndex<V> addRecord(CharSequence, V)` |  |
| `android.icu.text.AlphabeticIndex.ImmutableIndex<V> buildImmutableIndex()` |  |
| `android.icu.text.AlphabeticIndex<V> clearRecords()` |  |
| `int getBucketCount()` |  |
| `int getBucketIndex(CharSequence)` |  |
| `java.util.List<java.lang.String> getBucketLabels()` |  |
| `android.icu.text.RuleBasedCollator getCollator()` |  |
| `String getInflowLabel()` |  |
| `int getMaxLabelCount()` |  |
| `String getOverflowLabel()` |  |
| `int getRecordCount()` |  |
| `String getUnderflowLabel()` |  |
| `java.util.Iterator<android.icu.text.AlphabeticIndex.Bucket<V>> iterator()` |  |
| `android.icu.text.AlphabeticIndex<V> setInflowLabel(String)` |  |
| `android.icu.text.AlphabeticIndex<V> setMaxLabelCount(int)` |  |
| `android.icu.text.AlphabeticIndex<V> setOverflowLabel(String)` |  |
| `android.icu.text.AlphabeticIndex<V> setUnderflowLabel(String)` |  |

---

### `class static AlphabeticIndex.Bucket<V>`

- **Implements:** `java.lang.Iterable<android.icu.text.AlphabeticIndex.Record<V>>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getLabel()` |  |
| `android.icu.text.AlphabeticIndex.Bucket.LabelType getLabelType()` |  |
| `java.util.Iterator<android.icu.text.AlphabeticIndex.Record<V>> iterator()` |  |
| `int size()` |  |

---

### `enum AlphabeticIndex.Bucket.LabelType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.AlphabeticIndex.Bucket.LabelType INFLOW` |  |
| `static final android.icu.text.AlphabeticIndex.Bucket.LabelType NORMAL` |  |
| `static final android.icu.text.AlphabeticIndex.Bucket.LabelType OVERFLOW` |  |
| `static final android.icu.text.AlphabeticIndex.Bucket.LabelType UNDERFLOW` |  |

---

### `class static final AlphabeticIndex.ImmutableIndex<V>`

- **Implements:** `java.lang.Iterable<android.icu.text.AlphabeticIndex.Bucket<V>>`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.text.AlphabeticIndex.Bucket<V> getBucket(int)` |  |
| `int getBucketCount()` |  |
| `int getBucketIndex(CharSequence)` |  |
| `java.util.Iterator<android.icu.text.AlphabeticIndex.Bucket<V>> iterator()` |  |

---

### `class static AlphabeticIndex.Record<V>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `V getData()` |  |
| `CharSequence getName()` |  |

---

### `class Bidi`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Bidi()` |  |
| `Bidi(int, int)` |  |
| `Bidi(String, int)` |  |
| `Bidi(java.text.AttributedCharacterIterator)` |  |
| `Bidi(char[], int, byte[], int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DIRECTION_DEFAULT_LEFT_TO_RIGHT = 126` |  |
| `static final int DIRECTION_DEFAULT_RIGHT_TO_LEFT = 127` |  |
| `static final int DIRECTION_LEFT_TO_RIGHT = 0` |  |
| `static final int DIRECTION_RIGHT_TO_LEFT = 1` |  |
| `static final short DO_MIRRORING = 2` |  |
| `static final short INSERT_LRM_FOR_NUMERIC = 4` |  |
| `static final short KEEP_BASE_COMBINING = 1` |  |
| `static final byte LEVEL_DEFAULT_LTR = 126` |  |
| `static final byte LEVEL_DEFAULT_RTL = 127` |  |
| `static final byte LEVEL_OVERRIDE = -128` |  |
| `static final byte LTR = 0` |  |
| `static final int MAP_NOWHERE = -1` |  |
| `static final byte MAX_EXPLICIT_LEVEL = 125` |  |
| `static final byte MIXED = 2` |  |
| `static final byte NEUTRAL = 3` |  |
| `static final int OPTION_DEFAULT = 0` |  |
| `static final int OPTION_INSERT_MARKS = 1` |  |
| `static final int OPTION_REMOVE_CONTROLS = 2` |  |
| `static final int OPTION_STREAMING = 4` |  |
| `static final short OUTPUT_REVERSE = 16` |  |
| `static final short REMOVE_BIDI_CONTROLS = 8` |  |
| `static final short REORDER_DEFAULT = 0` |  |
| `static final short REORDER_GROUP_NUMBERS_WITH_R = 2` |  |
| `static final short REORDER_INVERSE_FOR_NUMBERS_SPECIAL = 6` |  |
| `static final short REORDER_INVERSE_LIKE_DIRECT = 5` |  |
| `static final short REORDER_INVERSE_NUMBERS_AS_L = 4` |  |
| `static final short REORDER_NUMBERS_SPECIAL = 1` |  |
| `static final short REORDER_RUNS_ONLY = 3` |  |
| `static final byte RTL = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean baseIsLeftToRight()` |  |
| `int countParagraphs()` |  |
| `int countRuns()` |  |
| `android.icu.text.Bidi createLineBidi(int, int)` |  |
| `static byte getBaseDirection(CharSequence)` |  |
| `int getBaseLevel()` |  |
| `android.icu.text.BidiClassifier getCustomClassifier()` |  |
| `int getCustomizedClass(int)` |  |
| `byte getDirection()` |  |
| `int getLength()` |  |
| `byte getLevelAt(int)` |  |
| `byte[] getLevels()` |  |
| `int getLogicalIndex(int)` |  |
| `int[] getLogicalMap()` |  |
| `android.icu.text.BidiRun getLogicalRun(int)` |  |
| `byte getParaLevel()` |  |
| `android.icu.text.BidiRun getParagraph(int)` |  |
| `android.icu.text.BidiRun getParagraphByIndex(int)` |  |
| `int getParagraphIndex(int)` |  |
| `int getProcessedLength()` |  |
| `int getReorderingMode()` |  |
| `int getReorderingOptions()` |  |
| `int getResultLength()` |  |
| `int getRunCount()` |  |
| `int getRunLevel(int)` |  |
| `int getRunLimit(int)` |  |
| `int getRunStart(int)` |  |
| `char[] getText()` |  |
| `String getTextAsString()` |  |
| `int getVisualIndex(int)` |  |
| `int[] getVisualMap()` |  |
| `android.icu.text.BidiRun getVisualRun(int)` |  |
| `static int[] invertMap(int[])` |  |
| `boolean isInverse()` |  |
| `boolean isLeftToRight()` |  |
| `boolean isMixed()` |  |
| `boolean isOrderParagraphsLTR()` |  |
| `boolean isRightToLeft()` |  |
| `void orderParagraphsLTR(boolean)` |  |
| `static int[] reorderLogical(byte[])` |  |
| `static int[] reorderVisual(byte[])` |  |
| `static void reorderVisually(byte[], int, Object[], int, int)` |  |
| `static boolean requiresBidi(char[], int, int)` |  |
| `void setContext(String, String)` |  |
| `void setCustomClassifier(android.icu.text.BidiClassifier)` |  |
| `void setInverse(boolean)` |  |
| `android.icu.text.Bidi setLine(int, int)` |  |
| `void setPara(String, byte, byte[])` |  |
| `void setPara(char[], byte, byte[])` |  |
| `void setPara(java.text.AttributedCharacterIterator)` |  |
| `void setReorderingMode(int)` |  |
| `void setReorderingOptions(int)` |  |
| `String writeReordered(int)` |  |
| `static String writeReverse(String, int)` |  |

---

### `class BidiClassifier`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BidiClassifier(Object)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int classify(int)` |  |
| `Object getContext()` |  |
| `void setContext(Object)` |  |

---

### `class BidiRun`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `byte getDirection()` |  |
| `byte getEmbeddingLevel()` |  |
| `int getLength()` |  |
| `int getLimit()` |  |
| `int getStart()` |  |
| `boolean isEvenRun()` |  |
| `boolean isOddRun()` |  |

---

### `class abstract BreakIterator`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BreakIterator()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DONE = -1` |  |
| `static final int KIND_CHARACTER = 0` |  |
| `static final int KIND_LINE = 2` |  |
| `static final int KIND_SENTENCE = 3` |  |
| `static final int KIND_WORD = 1` |  |
| `static final int WORD_IDEO = 400` |  |
| `static final int WORD_IDEO_LIMIT = 500` |  |
| `static final int WORD_KANA = 300` |  |
| `static final int WORD_KANA_LIMIT = 400` |  |
| `static final int WORD_LETTER = 200` |  |
| `static final int WORD_LETTER_LIMIT = 300` |  |
| `static final int WORD_NONE = 0` |  |
| `static final int WORD_NONE_LIMIT = 100` |  |
| `static final int WORD_NUMBER = 100` |  |
| `static final int WORD_NUMBER_LIMIT = 200` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `abstract int current()` |  |
| `abstract int first()` |  |
| `abstract int following(int)` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `static android.icu.text.BreakIterator getCharacterInstance()` |  |
| `static android.icu.text.BreakIterator getCharacterInstance(java.util.Locale)` |  |
| `static android.icu.text.BreakIterator getCharacterInstance(android.icu.util.ULocale)` |  |
| `static android.icu.text.BreakIterator getLineInstance()` |  |
| `static android.icu.text.BreakIterator getLineInstance(java.util.Locale)` |  |
| `static android.icu.text.BreakIterator getLineInstance(android.icu.util.ULocale)` |  |
| `int getRuleStatus()` |  |
| `int getRuleStatusVec(int[])` |  |
| `static android.icu.text.BreakIterator getSentenceInstance()` |  |
| `static android.icu.text.BreakIterator getSentenceInstance(java.util.Locale)` |  |
| `static android.icu.text.BreakIterator getSentenceInstance(android.icu.util.ULocale)` |  |
| `abstract java.text.CharacterIterator getText()` |  |
| `static android.icu.text.BreakIterator getWordInstance()` |  |
| `static android.icu.text.BreakIterator getWordInstance(java.util.Locale)` |  |
| `static android.icu.text.BreakIterator getWordInstance(android.icu.util.ULocale)` |  |
| `boolean isBoundary(int)` |  |
| `abstract int last()` |  |
| `abstract int next(int)` |  |
| `abstract int next()` |  |
| `int preceding(int)` |  |
| `abstract int previous()` |  |
| `void setText(String)` |  |
| `void setText(CharSequence)` |  |
| `abstract void setText(java.text.CharacterIterator)` |  |

---

### `class abstract CaseMap`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.text.CaseMap.Fold fold()` |  |
| `abstract android.icu.text.CaseMap omitUnchangedText()` |  |
| `static android.icu.text.CaseMap.Lower toLower()` |  |
| `static android.icu.text.CaseMap.Title toTitle()` |  |
| `static android.icu.text.CaseMap.Upper toUpper()` |  |

---

### `class static final CaseMap.Fold`

- **Extends:** `android.icu.text.CaseMap`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String apply(CharSequence)` |  |
| `<A extends java.lang.Appendable> A apply(CharSequence, A, android.icu.text.Edits)` |  |
| `android.icu.text.CaseMap.Fold omitUnchangedText()` |  |
| `android.icu.text.CaseMap.Fold turkic()` |  |

---

### `class static final CaseMap.Lower`

- **Extends:** `android.icu.text.CaseMap`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String apply(java.util.Locale, CharSequence)` |  |
| `<A extends java.lang.Appendable> A apply(java.util.Locale, CharSequence, A, android.icu.text.Edits)` |  |
| `android.icu.text.CaseMap.Lower omitUnchangedText()` |  |

---

### `class static final CaseMap.Title`

- **Extends:** `android.icu.text.CaseMap`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.text.CaseMap.Title adjustToCased()` |  |
| `String apply(java.util.Locale, android.icu.text.BreakIterator, CharSequence)` |  |
| `<A extends java.lang.Appendable> A apply(java.util.Locale, android.icu.text.BreakIterator, CharSequence, A, android.icu.text.Edits)` |  |
| `android.icu.text.CaseMap.Title noBreakAdjustment()` |  |
| `android.icu.text.CaseMap.Title noLowercase()` |  |
| `android.icu.text.CaseMap.Title omitUnchangedText()` |  |
| `android.icu.text.CaseMap.Title sentences()` |  |
| `android.icu.text.CaseMap.Title wholeString()` |  |

---

### `class static final CaseMap.Upper`

- **Extends:** `android.icu.text.CaseMap`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String apply(java.util.Locale, CharSequence)` |  |
| `<A extends java.lang.Appendable> A apply(java.util.Locale, CharSequence, A, android.icu.text.Edits)` |  |
| `android.icu.text.CaseMap.Upper omitUnchangedText()` |  |

---

### `class final CollationElementIterator`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int IGNORABLE = 0` |  |
| `static final int NULLORDER = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getMaxExpansion(int)` |  |
| `int getOffset()` |  |
| `int next()` |  |
| `int previous()` |  |
| `static int primaryOrder(int)` |  |
| `void reset()` |  |
| `static int secondaryOrder(int)` |  |
| `void setOffset(int)` |  |
| `void setText(String)` |  |
| `void setText(android.icu.text.UCharacterIterator)` |  |
| `void setText(java.text.CharacterIterator)` |  |
| `static int tertiaryOrder(int)` |  |

---

### `class final CollationKey`

- **Implements:** `java.lang.Comparable<android.icu.text.CollationKey>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CollationKey(String, byte[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(android.icu.text.CollationKey)` |  |
| `boolean equals(android.icu.text.CollationKey)` |  |
| `android.icu.text.CollationKey getBound(int, int)` |  |
| `String getSourceString()` |  |
| `android.icu.text.CollationKey merge(android.icu.text.CollationKey)` |  |
| `byte[] toByteArray()` |  |

---

### `class static final CollationKey.BoundMode`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int LOWER = 0` |  |
| `static final int UPPER = 1` |  |
| `static final int UPPER_LONG = 2` |  |

---

### `class abstract Collator`

- **Implements:** `java.lang.Cloneable java.util.Comparator<java.lang.Object> android.icu.util.Freezable<android.icu.text.Collator>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Collator()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CANONICAL_DECOMPOSITION = 17` |  |
| `static final int FULL_DECOMPOSITION = 15` |  |
| `static final int IDENTICAL = 15` |  |
| `static final int NO_DECOMPOSITION = 16` |  |
| `static final int PRIMARY = 0` |  |
| `static final int QUATERNARY = 3` |  |
| `static final int SECONDARY = 1` |  |
| `static final int TERTIARY = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone() throws java.lang.CloneNotSupportedException` |  |
| `android.icu.text.Collator cloneAsThawed()` |  |
| `abstract int compare(String, String)` |  |
| `int compare(Object, Object)` |  |
| `boolean equals(String, String)` |  |
| `android.icu.text.Collator freeze()` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `static final android.icu.util.ULocale[] getAvailableULocales()` |  |
| `abstract android.icu.text.CollationKey getCollationKey(String)` |  |
| `int getDecomposition()` |  |
| `static String getDisplayName(java.util.Locale, java.util.Locale)` |  |
| `static String getDisplayName(android.icu.util.ULocale, android.icu.util.ULocale)` |  |
| `static String getDisplayName(java.util.Locale)` |  |
| `static String getDisplayName(android.icu.util.ULocale)` |  |
| `static int[] getEquivalentReorderCodes(int)` |  |
| `static final android.icu.util.ULocale getFunctionalEquivalent(String, android.icu.util.ULocale, boolean[])` |  |
| `static final android.icu.util.ULocale getFunctionalEquivalent(String, android.icu.util.ULocale)` |  |
| `static final android.icu.text.Collator getInstance()` |  |
| `static final android.icu.text.Collator getInstance(android.icu.util.ULocale)` |  |
| `static final android.icu.text.Collator getInstance(java.util.Locale)` |  |
| `static final String[] getKeywordValues(String)` |  |
| `static final String[] getKeywordValuesForLocale(String, android.icu.util.ULocale, boolean)` |  |
| `static final String[] getKeywords()` |  |
| `int getMaxVariable()` |  |
| `int[] getReorderCodes()` |  |
| `int getStrength()` |  |
| `android.icu.text.UnicodeSet getTailoredSet()` |  |
| `abstract android.icu.util.VersionInfo getUCAVersion()` |  |
| `abstract int getVariableTop()` |  |
| `abstract android.icu.util.VersionInfo getVersion()` |  |
| `boolean isFrozen()` |  |
| `void setDecomposition(int)` |  |
| `android.icu.text.Collator setMaxVariable(int)` |  |
| `void setReorderCodes(int...)` |  |
| `void setStrength(int)` |  |

---

### `interface static Collator.ReorderCodes`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CURRENCY = 4099` |  |
| `static final int DEFAULT = -1` |  |
| `static final int DIGIT = 4100` |  |
| `static final int FIRST = 4096` |  |
| `static final int NONE = 103` |  |
| `static final int OTHERS = 103` |  |
| `static final int PUNCTUATION = 4097` |  |
| `static final int SPACE = 4096` |  |
| `static final int SYMBOL = 4098` |  |

---

### `class CompactDecimalFormat`

- **Extends:** `android.icu.text.DecimalFormat`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.text.CompactDecimalFormat getInstance(android.icu.util.ULocale, android.icu.text.CompactDecimalFormat.CompactStyle)` |  |
| `static android.icu.text.CompactDecimalFormat getInstance(java.util.Locale, android.icu.text.CompactDecimalFormat.CompactStyle)` |  |

---

### `enum CompactDecimalFormat.CompactStyle`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.CompactDecimalFormat.CompactStyle LONG` |  |
| `static final android.icu.text.CompactDecimalFormat.CompactStyle SHORT` |  |

---

### `class CurrencyPluralInfo`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CurrencyPluralInfo()` |  |
| `CurrencyPluralInfo(java.util.Locale)` |  |
| `CurrencyPluralInfo(android.icu.util.ULocale)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `String getCurrencyPluralPattern(String)` |  |
| `static android.icu.text.CurrencyPluralInfo getInstance()` |  |
| `static android.icu.text.CurrencyPluralInfo getInstance(java.util.Locale)` |  |
| `static android.icu.text.CurrencyPluralInfo getInstance(android.icu.util.ULocale)` |  |
| `android.icu.util.ULocale getLocale()` |  |
| `android.icu.text.PluralRules getPluralRules()` |  |
| `void setCurrencyPluralPattern(String, String)` |  |
| `void setLocale(android.icu.util.ULocale)` |  |
| `void setPluralRules(String)` |  |

---

### `class abstract DateFormat`

- **Extends:** `android.icu.text.UFormat`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateFormat()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ABBR_GENERIC_TZ = "v"` |  |
| `static final String ABBR_MONTH = "MMM"` |  |
| `static final String ABBR_MONTH_DAY = "MMMd"` |  |
| `static final String ABBR_MONTH_WEEKDAY_DAY = "MMMEd"` |  |
| `static final String ABBR_QUARTER = "QQQ"` |  |
| `static final String ABBR_SPECIFIC_TZ = "z"` |  |
| `static final String ABBR_UTC_TZ = "ZZZZ"` |  |
| `static final String ABBR_WEEKDAY = "E"` |  |
| `static final int AM_PM_FIELD = 14` |  |
| `static final int AM_PM_MIDNIGHT_NOON_FIELD = 35` |  |
| `static final int DATE_FIELD = 3` |  |
| `static final String DAY = "d"` |  |
| `static final int DAY_OF_WEEK_FIELD = 9` |  |
| `static final int DAY_OF_WEEK_IN_MONTH_FIELD = 11` |  |
| `static final int DAY_OF_YEAR_FIELD = 10` |  |
| `static final int DEFAULT = 2` |  |
| `static final int DOW_LOCAL_FIELD = 19` |  |
| `static final int ERA_FIELD = 0` |  |
| `static final int EXTENDED_YEAR_FIELD = 20` |  |
| `static final int FLEXIBLE_DAY_PERIOD_FIELD = 36` |  |
| `static final int FRACTIONAL_SECOND_FIELD = 8` |  |
| `static final int FULL = 0` |  |
| `static final String GENERIC_TZ = "vvvv"` |  |
| `static final String HOUR = "j"` |  |
| `static final int HOUR0_FIELD = 16` |  |
| `static final int HOUR1_FIELD = 15` |  |
| `static final String HOUR24 = "H"` |  |
| `static final String HOUR24_MINUTE = "Hm"` |  |
| `static final String HOUR24_MINUTE_SECOND = "Hms"` |  |
| `static final String HOUR_MINUTE = "jm"` |  |
| `static final String HOUR_MINUTE_SECOND = "jms"` |  |
| `static final int HOUR_OF_DAY0_FIELD = 5` |  |
| `static final int HOUR_OF_DAY1_FIELD = 4` |  |
| `static final int JULIAN_DAY_FIELD = 21` |  |
| `static final String LOCATION_TZ = "VVVV"` |  |
| `static final int LONG = 1` |  |
| `static final int MEDIUM = 2` |  |
| `static final int MILLISECONDS_IN_DAY_FIELD = 22` |  |
| `static final int MILLISECOND_FIELD = 8` |  |
| `static final String MINUTE = "m"` |  |
| `static final int MINUTE_FIELD = 6` |  |
| `static final String MINUTE_SECOND = "ms"` |  |
| `static final String MONTH = "MMMM"` |  |
| `static final String MONTH_DAY = "MMMMd"` |  |
| `static final int MONTH_FIELD = 2` |  |
| `static final String MONTH_WEEKDAY_DAY = "MMMMEEEEd"` |  |
| `static final int NONE = -1` |  |
| `static final String NUM_MONTH = "M"` |  |
| `static final String NUM_MONTH_DAY = "Md"` |  |
| `static final String NUM_MONTH_WEEKDAY_DAY = "MEd"` |  |
| `static final String QUARTER = "QQQQ"` |  |
| `static final int QUARTER_FIELD = 27` |  |
| `static final int RELATIVE = 128` |  |
| `static final int RELATIVE_DEFAULT = 130` |  |
| `static final int RELATIVE_FULL = 128` |  |
| `static final int RELATIVE_LONG = 129` |  |
| `static final int RELATIVE_MEDIUM = 130` |  |
| `static final int RELATIVE_SHORT = 131` |  |
| `static final String SECOND = "s"` |  |
| `static final int SECOND_FIELD = 7` |  |
| `static final int SHORT = 3` |  |
| `static final String SPECIFIC_TZ = "zzzz"` |  |
| `static final int STANDALONE_DAY_FIELD = 25` |  |
| `static final int STANDALONE_MONTH_FIELD = 26` |  |
| `static final int STANDALONE_QUARTER_FIELD = 28` |  |
| `static final int TIMEZONE_FIELD = 17` |  |
| `static final int TIMEZONE_GENERIC_FIELD = 24` |  |
| `static final int TIMEZONE_ISO_FIELD = 32` |  |
| `static final int TIMEZONE_ISO_LOCAL_FIELD = 33` |  |
| `static final int TIMEZONE_LOCALIZED_GMT_OFFSET_FIELD = 31` |  |
| `static final int TIMEZONE_RFC_FIELD = 23` |  |
| `static final int TIMEZONE_SPECIAL_FIELD = 29` |  |
| `static final String WEEKDAY = "EEEE"` |  |
| `static final int WEEK_OF_MONTH_FIELD = 13` |  |
| `static final int WEEK_OF_YEAR_FIELD = 12` |  |
| `static final String YEAR = "y"` |  |
| `static final String YEAR_ABBR_MONTH = "yMMM"` |  |
| `static final String YEAR_ABBR_MONTH_DAY = "yMMMd"` |  |
| `static final String YEAR_ABBR_MONTH_WEEKDAY_DAY = "yMMMEd"` |  |
| `static final String YEAR_ABBR_QUARTER = "yQQQ"` |  |
| `static final int YEAR_FIELD = 1` |  |
| `static final String YEAR_MONTH = "yMMMM"` |  |
| `static final String YEAR_MONTH_DAY = "yMMMMd"` |  |
| `static final String YEAR_MONTH_WEEKDAY_DAY = "yMMMMEEEEd"` |  |
| `static final int YEAR_NAME_FIELD = 30` |  |
| `static final String YEAR_NUM_MONTH = "yM"` |  |
| `static final String YEAR_NUM_MONTH_DAY = "yMd"` |  |
| `static final String YEAR_NUM_MONTH_WEEKDAY_DAY = "yMEd"` |  |
| `static final String YEAR_QUARTER = "yQQQQ"` |  |
| `static final int YEAR_WOY_FIELD = 18` |  |
| `android.icu.util.Calendar calendar` |  |
| `android.icu.text.NumberFormat numberFormat` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `abstract StringBuffer format(android.icu.util.Calendar, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuffer format(java.util.Date, StringBuffer, java.text.FieldPosition)` |  |
| `final String format(java.util.Date)` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `boolean getBooleanAttribute(android.icu.text.DateFormat.BooleanAttribute)` |  |
| `android.icu.util.Calendar getCalendar()` |  |
| `android.icu.text.DisplayContext getContext(android.icu.text.DisplayContext.Type)` |  |
| `static final android.icu.text.DateFormat getDateInstance()` |  |
| `static final android.icu.text.DateFormat getDateInstance(int)` |  |
| `static final android.icu.text.DateFormat getDateInstance(int, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getDateInstance(int, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateFormat getDateInstance(android.icu.util.Calendar, int, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getDateInstance(android.icu.util.Calendar, int, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateFormat getDateInstance(android.icu.util.Calendar, int)` |  |
| `static final android.icu.text.DateFormat getDateTimeInstance()` |  |
| `static final android.icu.text.DateFormat getDateTimeInstance(int, int)` |  |
| `static final android.icu.text.DateFormat getDateTimeInstance(int, int, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getDateTimeInstance(int, int, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateFormat getDateTimeInstance(android.icu.util.Calendar, int, int, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getDateTimeInstance(android.icu.util.Calendar, int, int, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateFormat getDateTimeInstance(android.icu.util.Calendar, int, int)` |  |
| `static final android.icu.text.DateFormat getInstance()` |  |
| `static final android.icu.text.DateFormat getInstance(android.icu.util.Calendar, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getInstance(android.icu.util.Calendar)` |  |
| `static final android.icu.text.DateFormat getInstanceForSkeleton(String)` |  |
| `static final android.icu.text.DateFormat getInstanceForSkeleton(String, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getInstanceForSkeleton(String, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateFormat getInstanceForSkeleton(android.icu.util.Calendar, String, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getInstanceForSkeleton(android.icu.util.Calendar, String, android.icu.util.ULocale)` |  |
| `android.icu.text.NumberFormat getNumberFormat()` |  |
| `static final android.icu.text.DateFormat getPatternInstance(String)` |  |
| `static final android.icu.text.DateFormat getPatternInstance(String, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getPatternInstance(String, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateFormat getPatternInstance(android.icu.util.Calendar, String, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getPatternInstance(android.icu.util.Calendar, String, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateFormat getTimeInstance()` |  |
| `static final android.icu.text.DateFormat getTimeInstance(int)` |  |
| `static final android.icu.text.DateFormat getTimeInstance(int, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getTimeInstance(int, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateFormat getTimeInstance(android.icu.util.Calendar, int, java.util.Locale)` |  |
| `static final android.icu.text.DateFormat getTimeInstance(android.icu.util.Calendar, int, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateFormat getTimeInstance(android.icu.util.Calendar, int)` |  |
| `android.icu.util.TimeZone getTimeZone()` |  |
| `boolean isCalendarLenient()` |  |
| `boolean isLenient()` |  |
| `java.util.Date parse(String) throws java.text.ParseException` |  |
| `abstract void parse(String, android.icu.util.Calendar, java.text.ParsePosition)` |  |
| `java.util.Date parse(String, java.text.ParsePosition)` |  |
| `Object parseObject(String, java.text.ParsePosition)` |  |
| `android.icu.text.DateFormat setBooleanAttribute(android.icu.text.DateFormat.BooleanAttribute, boolean)` |  |
| `void setCalendar(android.icu.util.Calendar)` |  |
| `void setCalendarLenient(boolean)` |  |
| `void setContext(android.icu.text.DisplayContext)` |  |
| `void setLenient(boolean)` |  |
| `void setNumberFormat(android.icu.text.NumberFormat)` |  |
| `void setTimeZone(android.icu.util.TimeZone)` |  |

---

### `enum DateFormat.BooleanAttribute`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.DateFormat.BooleanAttribute PARSE_ALLOW_NUMERIC` |  |
| `static final android.icu.text.DateFormat.BooleanAttribute PARSE_ALLOW_WHITESPACE` |  |
| `static final android.icu.text.DateFormat.BooleanAttribute PARSE_MULTIPLE_PATTERNS_FOR_MATCH` |  |
| `static final android.icu.text.DateFormat.BooleanAttribute PARSE_PARTIAL_LITERAL_MATCH` |  |

---

### `class static DateFormat.Field`

- **Extends:** `java.text.Format.Field`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateFormat.Field(String, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.text.DateFormat.Field AM_PM` |  |
| `static final android.icu.text.DateFormat.Field AM_PM_MIDNIGHT_NOON` |  |
| `static final android.icu.text.DateFormat.Field DAY_OF_MONTH` |  |
| `static final android.icu.text.DateFormat.Field DAY_OF_WEEK` |  |
| `static final android.icu.text.DateFormat.Field DAY_OF_WEEK_IN_MONTH` |  |
| `static final android.icu.text.DateFormat.Field DAY_OF_YEAR` |  |
| `static final android.icu.text.DateFormat.Field DOW_LOCAL` |  |
| `static final android.icu.text.DateFormat.Field ERA` |  |
| `static final android.icu.text.DateFormat.Field EXTENDED_YEAR` |  |
| `static final android.icu.text.DateFormat.Field FLEXIBLE_DAY_PERIOD` |  |
| `static final android.icu.text.DateFormat.Field HOUR0` |  |
| `static final android.icu.text.DateFormat.Field HOUR1` |  |
| `static final android.icu.text.DateFormat.Field HOUR_OF_DAY0` |  |
| `static final android.icu.text.DateFormat.Field HOUR_OF_DAY1` |  |
| `static final android.icu.text.DateFormat.Field JULIAN_DAY` |  |
| `static final android.icu.text.DateFormat.Field MILLISECOND` |  |
| `static final android.icu.text.DateFormat.Field MILLISECONDS_IN_DAY` |  |
| `static final android.icu.text.DateFormat.Field MINUTE` |  |
| `static final android.icu.text.DateFormat.Field MONTH` |  |
| `static final android.icu.text.DateFormat.Field QUARTER` |  |
| `static final android.icu.text.DateFormat.Field SECOND` |  |
| `static final android.icu.text.DateFormat.Field TIME_ZONE` |  |
| `static final android.icu.text.DateFormat.Field WEEK_OF_MONTH` |  |
| `static final android.icu.text.DateFormat.Field WEEK_OF_YEAR` |  |
| `static final android.icu.text.DateFormat.Field YEAR` |  |
| `static final android.icu.text.DateFormat.Field YEAR_WOY` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getCalendarField()` |  |
| `static android.icu.text.DateFormat.Field ofCalendarField(int)` |  |

---

### `class DateFormatSymbols`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateFormatSymbols()` |  |
| `DateFormatSymbols(java.util.Locale)` |  |
| `DateFormatSymbols(android.icu.util.ULocale)` |  |
| `DateFormatSymbols(android.icu.util.Calendar, java.util.Locale)` |  |
| `DateFormatSymbols(android.icu.util.Calendar, android.icu.util.ULocale)` |  |
| `DateFormatSymbols(Class<? extends android.icu.util.Calendar>, java.util.Locale)` |  |
| `DateFormatSymbols(Class<? extends android.icu.util.Calendar>, android.icu.util.ULocale)` |  |
| `DateFormatSymbols(java.util.ResourceBundle, java.util.Locale)` |  |
| `DateFormatSymbols(java.util.ResourceBundle, android.icu.util.ULocale)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ABBREVIATED = 0` |  |
| `static final int FORMAT = 0` |  |
| `static final int NARROW = 2` |  |
| `static final int SHORT = 3` |  |
| `static final int STANDALONE = 1` |  |
| `static final int WIDE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `String[] getAmPmStrings()` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `String[] getEraNames()` |  |
| `String[] getEras()` |  |
| `static android.icu.text.DateFormatSymbols getInstance()` |  |
| `static android.icu.text.DateFormatSymbols getInstance(java.util.Locale)` |  |
| `static android.icu.text.DateFormatSymbols getInstance(android.icu.util.ULocale)` |  |
| `String getLocalPatternChars()` |  |
| `String[] getMonths()` |  |
| `String[] getMonths(int, int)` |  |
| `String[] getQuarters(int, int)` |  |
| `String[] getShortMonths()` |  |
| `String[] getShortWeekdays()` |  |
| `String[] getWeekdays()` |  |
| `String[] getWeekdays(int, int)` |  |
| `String[] getYearNames(int, int)` |  |
| `String[] getZodiacNames(int, int)` |  |
| `String[][] getZoneStrings()` |  |
| `void initializeData(android.icu.util.ULocale, String)` |  |
| `void setAmPmStrings(String[])` |  |
| `void setEraNames(String[])` |  |
| `void setEras(String[])` |  |
| `void setLocalPatternChars(String)` |  |
| `void setMonths(String[])` |  |
| `void setMonths(String[], int, int)` |  |
| `void setQuarters(String[], int, int)` |  |
| `void setShortMonths(String[])` |  |
| `void setShortWeekdays(String[])` |  |
| `void setWeekdays(String[], int, int)` |  |
| `void setWeekdays(String[])` |  |
| `void setYearNames(String[], int, int)` |  |
| `void setZodiacNames(String[], int, int)` |  |
| `void setZoneStrings(String[][])` |  |

---

### `class DateIntervalFormat`

- **Extends:** `android.icu.text.UFormat`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `final StringBuffer format(android.icu.util.DateInterval, StringBuffer, java.text.FieldPosition)` |  |
| `final StringBuffer format(android.icu.util.Calendar, android.icu.util.Calendar, StringBuffer, java.text.FieldPosition)` |  |
| `android.icu.text.DateFormat getDateFormat()` |  |
| `android.icu.text.DateIntervalInfo getDateIntervalInfo()` |  |
| `static final android.icu.text.DateIntervalFormat getInstance(String)` |  |
| `static final android.icu.text.DateIntervalFormat getInstance(String, java.util.Locale)` |  |
| `static final android.icu.text.DateIntervalFormat getInstance(String, android.icu.util.ULocale)` |  |
| `static final android.icu.text.DateIntervalFormat getInstance(String, android.icu.text.DateIntervalInfo)` |  |
| `static final android.icu.text.DateIntervalFormat getInstance(String, java.util.Locale, android.icu.text.DateIntervalInfo)` |  |
| `static final android.icu.text.DateIntervalFormat getInstance(String, android.icu.util.ULocale, android.icu.text.DateIntervalInfo)` |  |
| `android.icu.util.TimeZone getTimeZone()` |  |
| `void setDateIntervalInfo(android.icu.text.DateIntervalInfo)` |  |
| `void setTimeZone(android.icu.util.TimeZone)` |  |

---

### `class DateIntervalInfo`

- **Implements:** `java.lang.Cloneable android.icu.util.Freezable<android.icu.text.DateIntervalInfo> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateIntervalInfo(android.icu.util.ULocale)` |  |
| `DateIntervalInfo(java.util.Locale)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `android.icu.text.DateIntervalInfo cloneAsThawed()` |  |
| `android.icu.text.DateIntervalInfo freeze()` |  |
| `boolean getDefaultOrder()` |  |
| `String getFallbackIntervalPattern()` |  |
| `android.icu.text.DateIntervalInfo.PatternInfo getIntervalPattern(String, int)` |  |
| `boolean isFrozen()` |  |
| `void setFallbackIntervalPattern(String)` |  |
| `void setIntervalPattern(String, int, String)` |  |

---

### `class static final DateIntervalInfo.PatternInfo`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateIntervalInfo.PatternInfo(String, String, boolean)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean firstDateInPtnIsLaterDate()` |  |
| `String getFirstPart()` |  |
| `String getSecondPart()` |  |

---

### `class DateTimePatternGenerator`

- **Implements:** `java.lang.Cloneable android.icu.util.Freezable<android.icu.text.DateTimePatternGenerator>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateTimePatternGenerator()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DAY = 7` |  |
| `static final int DAYPERIOD = 10` |  |
| `static final int DAY_OF_WEEK_IN_MONTH = 9` |  |
| `static final int DAY_OF_YEAR = 8` |  |
| `static final int ERA = 0` |  |
| `static final int FRACTIONAL_SECOND = 14` |  |
| `static final int HOUR = 11` |  |
| `static final int MATCH_ALL_FIELDS_LENGTH = 65535` |  |
| `static final int MATCH_HOUR_FIELD_LENGTH = 2048` |  |
| `static final int MATCH_NO_OPTIONS = 0` |  |
| `static final int MINUTE = 12` |  |
| `static final int MONTH = 3` |  |
| `static final int QUARTER = 2` |  |
| `static final int SECOND = 13` |  |
| `static final int WEEKDAY = 6` |  |
| `static final int WEEK_OF_MONTH = 5` |  |
| `static final int WEEK_OF_YEAR = 4` |  |
| `static final int YEAR = 1` |  |
| `static final int ZONE = 15` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.text.DateTimePatternGenerator addPattern(String, boolean, android.icu.text.DateTimePatternGenerator.PatternInfo)` |  |
| `Object clone()` |  |
| `android.icu.text.DateTimePatternGenerator cloneAsThawed()` |  |
| `android.icu.text.DateTimePatternGenerator freeze()` |  |
| `String getAppendItemFormat(int)` |  |
| `String getAppendItemName(int)` |  |
| `String getBaseSkeleton(String)` |  |
| `java.util.Set<java.lang.String> getBaseSkeletons(java.util.Set<java.lang.String>)` |  |
| `String getBestPattern(String)` |  |
| `String getBestPattern(String, int)` |  |
| `String getDateTimeFormat()` |  |
| `String getDecimal()` |  |
| `static android.icu.text.DateTimePatternGenerator getEmptyInstance()` |  |
| `String getFieldDisplayName(int, android.icu.text.DateTimePatternGenerator.DisplayWidth)` |  |
| `static android.icu.text.DateTimePatternGenerator getInstance()` |  |
| `static android.icu.text.DateTimePatternGenerator getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.text.DateTimePatternGenerator getInstance(java.util.Locale)` |  |
| `String getSkeleton(String)` |  |
| `java.util.Map<java.lang.String,java.lang.String> getSkeletons(java.util.Map<java.lang.String,java.lang.String>)` |  |
| `boolean isFrozen()` |  |
| `String replaceFieldTypes(String, String)` |  |
| `String replaceFieldTypes(String, String, int)` |  |
| `void setAppendItemFormat(int, String)` |  |
| `void setAppendItemName(int, String)` |  |
| `void setDateTimeFormat(String)` |  |
| `void setDecimal(String)` |  |

---

### `enum DateTimePatternGenerator.DisplayWidth`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.DateTimePatternGenerator.DisplayWidth ABBREVIATED` |  |
| `static final android.icu.text.DateTimePatternGenerator.DisplayWidth NARROW` |  |
| `static final android.icu.text.DateTimePatternGenerator.DisplayWidth WIDE` |  |

---

### `class static final DateTimePatternGenerator.PatternInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateTimePatternGenerator.PatternInfo()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BASE_CONFLICT = 1` |  |
| `static final int CONFLICT = 2` |  |
| `static final int OK = 0` |  |
| `String conflictingPattern` |  |
| `int status` |  |

---

### `class DecimalFormat`

- **Extends:** `android.icu.text.NumberFormat`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DecimalFormat()` |  |
| `DecimalFormat(String)` |  |
| `DecimalFormat(String, android.icu.text.DecimalFormatSymbols)` |  |
| `DecimalFormat(String, android.icu.text.DecimalFormatSymbols, android.icu.text.CurrencyPluralInfo, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PAD_AFTER_PREFIX = 1` |  |
| `static final int PAD_AFTER_SUFFIX = 3` |  |
| `static final int PAD_BEFORE_PREFIX = 0` |  |
| `static final int PAD_BEFORE_SUFFIX = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyLocalizedPattern(String)` |  |
| `void applyPattern(String)` |  |
| `boolean areSignificantDigitsUsed()` |  |
| `StringBuffer format(double, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuffer format(long, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuffer format(java.math.BigInteger, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuffer format(java.math.BigDecimal, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuffer format(android.icu.math.BigDecimal, StringBuffer, java.text.FieldPosition)` |  |
| `android.icu.text.CurrencyPluralInfo getCurrencyPluralInfo()` |  |
| `android.icu.util.Currency.CurrencyUsage getCurrencyUsage()` |  |
| `android.icu.text.DecimalFormatSymbols getDecimalFormatSymbols()` |  |
| `int getFormatWidth()` |  |
| `int getGroupingSize()` |  |
| `java.math.MathContext getMathContext()` |  |
| `android.icu.math.MathContext getMathContextICU()` |  |
| `int getMaximumSignificantDigits()` |  |
| `byte getMinimumExponentDigits()` |  |
| `int getMinimumSignificantDigits()` |  |
| `int getMultiplier()` |  |
| `String getNegativePrefix()` |  |
| `String getNegativeSuffix()` |  |
| `char getPadCharacter()` |  |
| `int getPadPosition()` |  |
| `String getPositivePrefix()` |  |
| `String getPositiveSuffix()` |  |
| `java.math.BigDecimal getRoundingIncrement()` |  |
| `int getSecondaryGroupingSize()` |  |
| `boolean isDecimalPatternMatchRequired()` |  |
| `boolean isDecimalSeparatorAlwaysShown()` |  |
| `boolean isExponentSignAlwaysShown()` |  |
| `boolean isParseBigDecimal()` |  |
| `boolean isScientificNotation()` |  |
| `Number parse(String, java.text.ParsePosition)` |  |
| `void setCurrencyPluralInfo(android.icu.text.CurrencyPluralInfo)` |  |
| `void setCurrencyUsage(android.icu.util.Currency.CurrencyUsage)` |  |
| `void setDecimalFormatSymbols(android.icu.text.DecimalFormatSymbols)` |  |
| `void setDecimalPatternMatchRequired(boolean)` |  |
| `void setDecimalSeparatorAlwaysShown(boolean)` |  |
| `void setExponentSignAlwaysShown(boolean)` |  |
| `void setFormatWidth(int)` |  |
| `void setGroupingSize(int)` |  |
| `void setMathContext(java.math.MathContext)` |  |
| `void setMathContextICU(android.icu.math.MathContext)` |  |
| `void setMaximumSignificantDigits(int)` |  |
| `void setMinimumExponentDigits(byte)` |  |
| `void setMinimumSignificantDigits(int)` |  |
| `void setMultiplier(int)` |  |
| `void setNegativePrefix(String)` |  |
| `void setNegativeSuffix(String)` |  |
| `void setPadCharacter(char)` |  |
| `void setPadPosition(int)` |  |
| `void setParseBigDecimal(boolean)` |  |
| `void setPositivePrefix(String)` |  |
| `void setPositiveSuffix(String)` |  |
| `void setRoundingIncrement(java.math.BigDecimal)` |  |
| `void setRoundingIncrement(android.icu.math.BigDecimal)` |  |
| `void setRoundingIncrement(double)` |  |
| `void setScientificNotation(boolean)` |  |
| `void setSecondaryGroupingSize(int)` |  |
| `void setSignificantDigitsUsed(boolean)` |  |
| `String toLocalizedPattern()` |  |
| `String toPattern()` |  |

---

### `class DecimalFormatSymbols`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DecimalFormatSymbols()` |  |
| `DecimalFormatSymbols(java.util.Locale)` |  |
| `DecimalFormatSymbols(android.icu.util.ULocale)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CURRENCY_SPC_CURRENCY_MATCH = 0` |  |
| `static final int CURRENCY_SPC_INSERT = 2` |  |
| `static final int CURRENCY_SPC_SURROUNDING_MATCH = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `static android.icu.text.DecimalFormatSymbols forNumberingSystem(java.util.Locale, android.icu.text.NumberingSystem)` |  |
| `static android.icu.text.DecimalFormatSymbols forNumberingSystem(android.icu.util.ULocale, android.icu.text.NumberingSystem)` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `android.icu.util.Currency getCurrency()` |  |
| `String getCurrencySymbol()` |  |
| `char getDecimalSeparator()` |  |
| `String getDecimalSeparatorString()` |  |
| `char getDigit()` |  |
| `String[] getDigitStrings()` |  |
| `char[] getDigits()` |  |
| `String getExponentMultiplicationSign()` |  |
| `String getExponentSeparator()` |  |
| `char getGroupingSeparator()` |  |
| `String getGroupingSeparatorString()` |  |
| `String getInfinity()` |  |
| `static android.icu.text.DecimalFormatSymbols getInstance()` |  |
| `static android.icu.text.DecimalFormatSymbols getInstance(java.util.Locale)` |  |
| `static android.icu.text.DecimalFormatSymbols getInstance(android.icu.util.ULocale)` |  |
| `String getInternationalCurrencySymbol()` |  |
| `java.util.Locale getLocale()` |  |
| `char getMinusSign()` |  |
| `String getMinusSignString()` |  |
| `char getMonetaryDecimalSeparator()` |  |
| `String getMonetaryDecimalSeparatorString()` |  |
| `char getMonetaryGroupingSeparator()` |  |
| `String getMonetaryGroupingSeparatorString()` |  |
| `String getNaN()` |  |
| `char getPadEscape()` |  |
| `String getPatternForCurrencySpacing(int, boolean)` |  |
| `char getPatternSeparator()` |  |
| `char getPerMill()` |  |
| `String getPerMillString()` |  |
| `char getPercent()` |  |
| `String getPercentString()` |  |
| `char getPlusSign()` |  |
| `String getPlusSignString()` |  |
| `char getSignificantDigit()` |  |
| `android.icu.util.ULocale getULocale()` |  |
| `char getZeroDigit()` |  |
| `void setCurrency(android.icu.util.Currency)` |  |
| `void setCurrencySymbol(String)` |  |
| `void setDecimalSeparator(char)` |  |
| `void setDecimalSeparatorString(String)` |  |
| `void setDigit(char)` |  |
| `void setDigitStrings(String[])` |  |
| `void setExponentMultiplicationSign(String)` |  |
| `void setExponentSeparator(String)` |  |
| `void setGroupingSeparator(char)` |  |
| `void setGroupingSeparatorString(String)` |  |
| `void setInfinity(String)` |  |
| `void setInternationalCurrencySymbol(String)` |  |
| `void setMinusSign(char)` |  |
| `void setMinusSignString(String)` |  |
| `void setMonetaryDecimalSeparator(char)` |  |
| `void setMonetaryDecimalSeparatorString(String)` |  |
| `void setMonetaryGroupingSeparator(char)` |  |
| `void setMonetaryGroupingSeparatorString(String)` |  |
| `void setNaN(String)` |  |
| `void setPadEscape(char)` |  |
| `void setPatternForCurrencySpacing(int, boolean, String)` |  |
| `void setPatternSeparator(char)` |  |
| `void setPerMill(char)` |  |
| `void setPerMillString(String)` |  |
| `void setPercent(char)` |  |
| `void setPercentString(String)` |  |
| `void setPlusSign(char)` |  |
| `void setPlusSignString(String)` |  |
| `void setSignificantDigit(char)` |  |
| `void setZeroDigit(char)` |  |

---

### `enum DisplayContext`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.DisplayContext CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE` |  |
| `static final android.icu.text.DisplayContext CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE` |  |
| `static final android.icu.text.DisplayContext CAPITALIZATION_FOR_STANDALONE` |  |
| `static final android.icu.text.DisplayContext CAPITALIZATION_FOR_UI_LIST_OR_MENU` |  |
| `static final android.icu.text.DisplayContext CAPITALIZATION_NONE` |  |
| `static final android.icu.text.DisplayContext DIALECT_NAMES` |  |
| `static final android.icu.text.DisplayContext LENGTH_FULL` |  |
| `static final android.icu.text.DisplayContext LENGTH_SHORT` |  |
| `static final android.icu.text.DisplayContext NO_SUBSTITUTE` |  |
| `static final android.icu.text.DisplayContext STANDARD_NAMES` |  |
| `static final android.icu.text.DisplayContext SUBSTITUTE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.text.DisplayContext.Type type()` |  |
| `int value()` |  |

---

### `enum DisplayContext.Type`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.DisplayContext.Type CAPITALIZATION` |  |
| `static final android.icu.text.DisplayContext.Type DIALECT_HANDLING` |  |
| `static final android.icu.text.DisplayContext.Type DISPLAY_LENGTH` |  |
| `static final android.icu.text.DisplayContext.Type SUBSTITUTE_HANDLING` |  |

---

### `class final Edits`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Edits()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addReplace(int, int)` |  |
| `void addUnchanged(int)` |  |
| `android.icu.text.Edits.Iterator getCoarseChangesIterator()` |  |
| `android.icu.text.Edits.Iterator getCoarseIterator()` |  |
| `android.icu.text.Edits.Iterator getFineChangesIterator()` |  |
| `android.icu.text.Edits.Iterator getFineIterator()` |  |
| `boolean hasChanges()` |  |
| `int lengthDelta()` |  |
| `android.icu.text.Edits mergeAndAppend(android.icu.text.Edits, android.icu.text.Edits)` |  |
| `int numberOfChanges()` |  |
| `void reset()` |  |

---

### `class static final Edits.Iterator`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int destinationIndex()` |  |
| `int destinationIndexFromSourceIndex(int)` |  |
| `boolean findDestinationIndex(int)` |  |
| `boolean findSourceIndex(int)` |  |
| `boolean hasChange()` |  |
| `int newLength()` |  |
| `boolean next()` |  |
| `int oldLength()` |  |
| `int replacementIndex()` |  |
| `int sourceIndex()` |  |
| `int sourceIndexFromDestinationIndex(int)` |  |

---

### `class abstract IDNA`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CHECK_BIDI = 4` |  |
| `static final int CHECK_CONTEXTJ = 8` |  |
| `static final int CHECK_CONTEXTO = 64` |  |
| `static final int DEFAULT = 0` |  |
| `static final int NONTRANSITIONAL_TO_ASCII = 16` |  |
| `static final int NONTRANSITIONAL_TO_UNICODE = 32` |  |
| `static final int USE_STD3_RULES = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.text.IDNA getUTS46Instance(int)` |  |
| `abstract StringBuilder labelToASCII(CharSequence, StringBuilder, android.icu.text.IDNA.Info)` |  |
| `abstract StringBuilder labelToUnicode(CharSequence, StringBuilder, android.icu.text.IDNA.Info)` |  |
| `abstract StringBuilder nameToASCII(CharSequence, StringBuilder, android.icu.text.IDNA.Info)` |  |
| `abstract StringBuilder nameToUnicode(CharSequence, StringBuilder, android.icu.text.IDNA.Info)` |  |

---

### `enum IDNA.Error`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.IDNA.Error BIDI` |  |
| `static final android.icu.text.IDNA.Error CONTEXTJ` |  |
| `static final android.icu.text.IDNA.Error CONTEXTO_DIGITS` |  |
| `static final android.icu.text.IDNA.Error CONTEXTO_PUNCTUATION` |  |
| `static final android.icu.text.IDNA.Error DISALLOWED` |  |
| `static final android.icu.text.IDNA.Error DOMAIN_NAME_TOO_LONG` |  |
| `static final android.icu.text.IDNA.Error EMPTY_LABEL` |  |
| `static final android.icu.text.IDNA.Error HYPHEN_3_4` |  |
| `static final android.icu.text.IDNA.Error INVALID_ACE_LABEL` |  |
| `static final android.icu.text.IDNA.Error LABEL_HAS_DOT` |  |
| `static final android.icu.text.IDNA.Error LABEL_TOO_LONG` |  |
| `static final android.icu.text.IDNA.Error LEADING_COMBINING_MARK` |  |
| `static final android.icu.text.IDNA.Error LEADING_HYPHEN` |  |
| `static final android.icu.text.IDNA.Error PUNYCODE` |  |
| `static final android.icu.text.IDNA.Error TRAILING_HYPHEN` |  |

---

### `class static final IDNA.Info`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IDNA.Info()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.Set<android.icu.text.IDNA.Error> getErrors()` |  |
| `boolean hasErrors()` |  |
| `boolean isTransitionalDifferent()` |  |

---

### `class final ListFormatter`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String format(java.lang.Object...)` |  |
| `String format(java.util.Collection<?>)` |  |
| `static android.icu.text.ListFormatter getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.text.ListFormatter getInstance(java.util.Locale)` |  |
| `static android.icu.text.ListFormatter getInstance()` |  |
| `String getPatternForNumItems(int)` |  |

---

### `class abstract LocaleDisplayNames`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract android.icu.text.DisplayContext getContext(android.icu.text.DisplayContext.Type)` |  |
| `abstract android.icu.text.LocaleDisplayNames.DialectHandling getDialectHandling()` |  |
| `static android.icu.text.LocaleDisplayNames getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.text.LocaleDisplayNames getInstance(java.util.Locale)` |  |
| `static android.icu.text.LocaleDisplayNames getInstance(android.icu.util.ULocale, android.icu.text.LocaleDisplayNames.DialectHandling)` |  |
| `static android.icu.text.LocaleDisplayNames getInstance(android.icu.util.ULocale, android.icu.text.DisplayContext...)` |  |
| `static android.icu.text.LocaleDisplayNames getInstance(java.util.Locale, android.icu.text.DisplayContext...)` |  |
| `abstract android.icu.util.ULocale getLocale()` |  |
| `java.util.List<android.icu.text.LocaleDisplayNames.UiListItem> getUiList(java.util.Set<android.icu.util.ULocale>, boolean, java.util.Comparator<java.lang.Object>)` |  |
| `abstract java.util.List<android.icu.text.LocaleDisplayNames.UiListItem> getUiListCompareWholeItems(java.util.Set<android.icu.util.ULocale>, java.util.Comparator<android.icu.text.LocaleDisplayNames.UiListItem>)` |  |
| `abstract String keyDisplayName(String)` |  |
| `abstract String keyValueDisplayName(String, String)` |  |
| `abstract String languageDisplayName(String)` |  |
| `abstract String localeDisplayName(android.icu.util.ULocale)` |  |
| `abstract String localeDisplayName(java.util.Locale)` |  |
| `abstract String localeDisplayName(String)` |  |
| `abstract String regionDisplayName(String)` |  |
| `abstract String scriptDisplayName(String)` |  |
| `abstract String scriptDisplayName(int)` |  |
| `abstract String variantDisplayName(String)` |  |

---

### `enum LocaleDisplayNames.DialectHandling`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.LocaleDisplayNames.DialectHandling DIALECT_NAMES` |  |
| `static final android.icu.text.LocaleDisplayNames.DialectHandling STANDARD_NAMES` |  |

---

### `class static LocaleDisplayNames.UiListItem`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `LocaleDisplayNames.UiListItem(android.icu.util.ULocale, android.icu.util.ULocale, String, String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final android.icu.util.ULocale minimized` |  |
| `final android.icu.util.ULocale modified` |  |
| `final String nameInDisplayLocale` |  |
| `final String nameInSelf` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.Comparator<android.icu.text.LocaleDisplayNames.UiListItem> getComparator(java.util.Comparator<java.lang.Object>, boolean)` |  |

---

### `class MeasureFormat`

- **Extends:** `android.icu.text.UFormat`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final boolean equals(Object)` |  |
| `StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuilder formatMeasurePerUnit(android.icu.util.Measure, android.icu.util.MeasureUnit, StringBuilder, java.text.FieldPosition)` |  |
| `final String formatMeasures(android.icu.util.Measure...)` |  |
| `StringBuilder formatMeasures(StringBuilder, java.text.FieldPosition, android.icu.util.Measure...)` |  |
| `static android.icu.text.MeasureFormat getCurrencyFormat(android.icu.util.ULocale)` |  |
| `static android.icu.text.MeasureFormat getCurrencyFormat(java.util.Locale)` |  |
| `static android.icu.text.MeasureFormat getCurrencyFormat()` |  |
| `static android.icu.text.MeasureFormat getInstance(android.icu.util.ULocale, android.icu.text.MeasureFormat.FormatWidth)` |  |
| `static android.icu.text.MeasureFormat getInstance(java.util.Locale, android.icu.text.MeasureFormat.FormatWidth)` |  |
| `static android.icu.text.MeasureFormat getInstance(android.icu.util.ULocale, android.icu.text.MeasureFormat.FormatWidth, android.icu.text.NumberFormat)` |  |
| `static android.icu.text.MeasureFormat getInstance(java.util.Locale, android.icu.text.MeasureFormat.FormatWidth, android.icu.text.NumberFormat)` |  |
| `final android.icu.util.ULocale getLocale()` |  |
| `android.icu.text.NumberFormat getNumberFormat()` |  |
| `String getUnitDisplayName(android.icu.util.MeasureUnit)` |  |
| `android.icu.text.MeasureFormat.FormatWidth getWidth()` |  |
| `final int hashCode()` |  |
| `android.icu.util.Measure parseObject(String, java.text.ParsePosition)` |  |

---

### `enum MeasureFormat.FormatWidth`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.MeasureFormat.FormatWidth NARROW` |  |
| `static final android.icu.text.MeasureFormat.FormatWidth NUMERIC` |  |
| `static final android.icu.text.MeasureFormat.FormatWidth SHORT` |  |
| `static final android.icu.text.MeasureFormat.FormatWidth WIDE` |  |

---

### `class MessageFormat`

- **Extends:** `android.icu.text.UFormat`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MessageFormat(String)` |  |
| `MessageFormat(String, java.util.Locale)` |  |
| `MessageFormat(String, android.icu.util.ULocale)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyPattern(String)` |  |
| `void applyPattern(String, android.icu.text.MessagePattern.ApostropheMode)` |  |
| `static String autoQuoteApostrophe(String)` |  |
| `final StringBuffer format(Object[], StringBuffer, java.text.FieldPosition)` |  |
| `final StringBuffer format(java.util.Map<java.lang.String,java.lang.Object>, StringBuffer, java.text.FieldPosition)` |  |
| `static String format(String, java.lang.Object...)` |  |
| `static String format(String, java.util.Map<java.lang.String,java.lang.Object>)` |  |
| `final StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `android.icu.text.MessagePattern.ApostropheMode getApostropheMode()` |  |
| `java.util.Set<java.lang.String> getArgumentNames()` |  |
| `java.text.Format getFormatByArgumentName(String)` |  |
| `java.text.Format[] getFormats()` |  |
| `java.text.Format[] getFormatsByArgumentIndex()` |  |
| `java.util.Locale getLocale()` |  |
| `android.icu.util.ULocale getULocale()` |  |
| `Object[] parse(String, java.text.ParsePosition)` |  |
| `Object[] parse(String) throws java.text.ParseException` |  |
| `Object parseObject(String, java.text.ParsePosition)` |  |
| `java.util.Map<java.lang.String,java.lang.Object> parseToMap(String, java.text.ParsePosition)` |  |
| `java.util.Map<java.lang.String,java.lang.Object> parseToMap(String) throws java.text.ParseException` |  |
| `void setFormat(int, java.text.Format)` |  |
| `void setFormatByArgumentIndex(int, java.text.Format)` |  |
| `void setFormatByArgumentName(String, java.text.Format)` |  |
| `void setFormats(java.text.Format[])` |  |
| `void setFormatsByArgumentIndex(java.text.Format[])` |  |
| `void setFormatsByArgumentName(java.util.Map<java.lang.String,java.text.Format>)` |  |
| `void setLocale(java.util.Locale)` |  |
| `void setLocale(android.icu.util.ULocale)` |  |
| `String toPattern()` |  |
| `boolean usesNamedArguments()` |  |

---

### `class static MessageFormat.Field`

- **Extends:** `java.text.Format.Field`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MessageFormat.Field(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.text.MessageFormat.Field ARGUMENT` |  |

---

### `class final MessagePattern`

- **Implements:** `java.lang.Cloneable android.icu.util.Freezable<android.icu.text.MessagePattern>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MessagePattern()` |  |
| `MessagePattern(android.icu.text.MessagePattern.ApostropheMode)` |  |
| `MessagePattern(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ARG_NAME_NOT_NUMBER = -1` |  |
| `static final int ARG_NAME_NOT_VALID = -2` |  |
| `static final double NO_NUMERIC_VALUE = -1.23456789E8` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String autoQuoteApostropheDeep()` |  |
| `void clear()` |  |
| `void clearPatternAndSetApostropheMode(android.icu.text.MessagePattern.ApostropheMode)` |  |
| `Object clone()` |  |
| `android.icu.text.MessagePattern cloneAsThawed()` |  |
| `int countParts()` |  |
| `android.icu.text.MessagePattern freeze()` |  |
| `android.icu.text.MessagePattern.ApostropheMode getApostropheMode()` |  |
| `int getLimitPartIndex(int)` |  |
| `double getNumericValue(android.icu.text.MessagePattern.Part)` |  |
| `android.icu.text.MessagePattern.Part getPart(int)` |  |
| `android.icu.text.MessagePattern.Part.Type getPartType(int)` |  |
| `int getPatternIndex(int)` |  |
| `String getPatternString()` |  |
| `double getPluralOffset(int)` |  |
| `String getSubstring(android.icu.text.MessagePattern.Part)` |  |
| `boolean hasNamedArguments()` |  |
| `boolean hasNumberedArguments()` |  |
| `boolean isFrozen()` |  |
| `android.icu.text.MessagePattern parse(String)` |  |
| `android.icu.text.MessagePattern parseChoiceStyle(String)` |  |
| `android.icu.text.MessagePattern parsePluralStyle(String)` |  |
| `android.icu.text.MessagePattern parseSelectStyle(String)` |  |
| `boolean partSubstringMatches(android.icu.text.MessagePattern.Part, String)` |  |
| `static int validateArgumentName(String)` |  |

---

### `enum MessagePattern.ApostropheMode`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.MessagePattern.ApostropheMode DOUBLE_OPTIONAL` |  |
| `static final android.icu.text.MessagePattern.ApostropheMode DOUBLE_REQUIRED` |  |

---

### `enum MessagePattern.ArgType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.MessagePattern.ArgType CHOICE` |  |
| `static final android.icu.text.MessagePattern.ArgType NONE` |  |
| `static final android.icu.text.MessagePattern.ArgType PLURAL` |  |
| `static final android.icu.text.MessagePattern.ArgType SELECT` |  |
| `static final android.icu.text.MessagePattern.ArgType SELECTORDINAL` |  |
| `static final android.icu.text.MessagePattern.ArgType SIMPLE` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean hasPluralStyle()` |  |

---

### `class static final MessagePattern.Part`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.text.MessagePattern.ArgType getArgType()` |  |
| `int getIndex()` |  |
| `int getLength()` |  |
| `int getLimit()` |  |
| `android.icu.text.MessagePattern.Part.Type getType()` |  |
| `int getValue()` |  |

---

### `enum MessagePattern.Part.Type`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.MessagePattern.Part.Type ARG_DOUBLE` |  |
| `static final android.icu.text.MessagePattern.Part.Type ARG_INT` |  |
| `static final android.icu.text.MessagePattern.Part.Type ARG_LIMIT` |  |
| `static final android.icu.text.MessagePattern.Part.Type ARG_NAME` |  |
| `static final android.icu.text.MessagePattern.Part.Type ARG_NUMBER` |  |
| `static final android.icu.text.MessagePattern.Part.Type ARG_SELECTOR` |  |
| `static final android.icu.text.MessagePattern.Part.Type ARG_START` |  |
| `static final android.icu.text.MessagePattern.Part.Type ARG_STYLE` |  |
| `static final android.icu.text.MessagePattern.Part.Type ARG_TYPE` |  |
| `static final android.icu.text.MessagePattern.Part.Type INSERT_CHAR` |  |
| `static final android.icu.text.MessagePattern.Part.Type MSG_LIMIT` |  |
| `static final android.icu.text.MessagePattern.Part.Type MSG_START` |  |
| `static final android.icu.text.MessagePattern.Part.Type REPLACE_NUMBER` |  |
| `static final android.icu.text.MessagePattern.Part.Type SKIP_SYNTAX` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean hasNumericValue()` |  |

---

### `class final Normalizer`

- **Implements:** `java.lang.Cloneable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int COMPARE_CODE_POINT_ORDER = 32768` |  |
| `static final int COMPARE_IGNORE_CASE = 65536` |  |
| `static final int FOLD_CASE_DEFAULT = 0` |  |
| `static final int FOLD_CASE_EXCLUDE_SPECIAL_I = 1` |  |
| `static final int INPUT_IS_FCD = 131072` |  |
| `static final android.icu.text.Normalizer.QuickCheckResult MAYBE` |  |
| `static final android.icu.text.Normalizer.QuickCheckResult NO` |  |
| `static final android.icu.text.Normalizer.QuickCheckResult YES` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static int compare(char[], int, int, char[], int, int, int)` |  |
| `static int compare(String, String, int)` |  |
| `static int compare(char[], char[], int)` |  |
| `static int compare(int, int, int)` |  |
| `static int compare(int, String, int)` |  |

---

### `class static final Normalizer.QuickCheckResult`


---

### `class abstract Normalizer2`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract StringBuilder append(StringBuilder, CharSequence)` |  |
| `int composePair(int, int)` |  |
| `int getCombiningClass(int)` |  |
| `abstract String getDecomposition(int)` |  |
| `static android.icu.text.Normalizer2 getInstance(java.io.InputStream, String, android.icu.text.Normalizer2.Mode)` |  |
| `static android.icu.text.Normalizer2 getNFCInstance()` |  |
| `static android.icu.text.Normalizer2 getNFDInstance()` |  |
| `static android.icu.text.Normalizer2 getNFKCCasefoldInstance()` |  |
| `static android.icu.text.Normalizer2 getNFKCInstance()` |  |
| `static android.icu.text.Normalizer2 getNFKDInstance()` |  |
| `String getRawDecomposition(int)` |  |
| `abstract boolean hasBoundaryAfter(int)` |  |
| `abstract boolean hasBoundaryBefore(int)` |  |
| `abstract boolean isInert(int)` |  |
| `abstract boolean isNormalized(CharSequence)` |  |
| `String normalize(CharSequence)` |  |
| `abstract StringBuilder normalize(CharSequence, StringBuilder)` |  |
| `abstract Appendable normalize(CharSequence, Appendable)` |  |
| `abstract StringBuilder normalizeSecondAndAppend(StringBuilder, CharSequence)` |  |
| `abstract android.icu.text.Normalizer.QuickCheckResult quickCheck(CharSequence)` |  |
| `abstract int spanQuickCheckYes(CharSequence)` |  |

---

### `enum Normalizer2.Mode`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.Normalizer2.Mode COMPOSE` |  |
| `static final android.icu.text.Normalizer2.Mode COMPOSE_CONTIGUOUS` |  |
| `static final android.icu.text.Normalizer2.Mode DECOMPOSE` |  |
| `static final android.icu.text.Normalizer2.Mode FCD` |  |

---

### `class abstract NumberFormat`

- **Extends:** `android.icu.text.UFormat`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NumberFormat()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACCOUNTINGCURRENCYSTYLE = 7` |  |
| `static final int CASHCURRENCYSTYLE = 8` |  |
| `static final int CURRENCYSTYLE = 1` |  |
| `static final int FRACTION_FIELD = 1` |  |
| `static final int INTEGERSTYLE = 4` |  |
| `static final int INTEGER_FIELD = 0` |  |
| `static final int ISOCURRENCYSTYLE = 5` |  |
| `static final int NUMBERSTYLE = 0` |  |
| `static final int PERCENTSTYLE = 2` |  |
| `static final int PLURALCURRENCYSTYLE = 6` |  |
| `static final int SCIENTIFICSTYLE = 3` |  |
| `static final int STANDARDCURRENCYSTYLE = 9` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `final String format(double)` |  |
| `final String format(long)` |  |
| `final String format(java.math.BigInteger)` |  |
| `final String format(java.math.BigDecimal)` |  |
| `final String format(android.icu.math.BigDecimal)` |  |
| `final String format(android.icu.util.CurrencyAmount)` |  |
| `abstract StringBuffer format(double, StringBuffer, java.text.FieldPosition)` |  |
| `abstract StringBuffer format(long, StringBuffer, java.text.FieldPosition)` |  |
| `abstract StringBuffer format(java.math.BigInteger, StringBuffer, java.text.FieldPosition)` |  |
| `abstract StringBuffer format(java.math.BigDecimal, StringBuffer, java.text.FieldPosition)` |  |
| `abstract StringBuffer format(android.icu.math.BigDecimal, StringBuffer, java.text.FieldPosition)` |  |
| `StringBuffer format(android.icu.util.CurrencyAmount, StringBuffer, java.text.FieldPosition)` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `android.icu.text.DisplayContext getContext(android.icu.text.DisplayContext.Type)` |  |
| `android.icu.util.Currency getCurrency()` |  |
| `static final android.icu.text.NumberFormat getCurrencyInstance()` |  |
| `static android.icu.text.NumberFormat getCurrencyInstance(java.util.Locale)` |  |
| `static android.icu.text.NumberFormat getCurrencyInstance(android.icu.util.ULocale)` |  |
| `static final android.icu.text.NumberFormat getInstance()` |  |
| `static android.icu.text.NumberFormat getInstance(java.util.Locale)` |  |
| `static android.icu.text.NumberFormat getInstance(android.icu.util.ULocale)` |  |
| `static final android.icu.text.NumberFormat getInstance(int)` |  |
| `static android.icu.text.NumberFormat getInstance(java.util.Locale, int)` |  |
| `static android.icu.text.NumberFormat getInstance(android.icu.util.ULocale, int)` |  |
| `static final android.icu.text.NumberFormat getIntegerInstance()` |  |
| `static android.icu.text.NumberFormat getIntegerInstance(java.util.Locale)` |  |
| `static android.icu.text.NumberFormat getIntegerInstance(android.icu.util.ULocale)` |  |
| `int getMaximumFractionDigits()` |  |
| `int getMaximumIntegerDigits()` |  |
| `int getMinimumFractionDigits()` |  |
| `int getMinimumIntegerDigits()` |  |
| `static final android.icu.text.NumberFormat getNumberInstance()` |  |
| `static android.icu.text.NumberFormat getNumberInstance(java.util.Locale)` |  |
| `static android.icu.text.NumberFormat getNumberInstance(android.icu.util.ULocale)` |  |
| `static String getPattern(android.icu.util.ULocale, int)` |  |
| `static final android.icu.text.NumberFormat getPercentInstance()` |  |
| `static android.icu.text.NumberFormat getPercentInstance(java.util.Locale)` |  |
| `static android.icu.text.NumberFormat getPercentInstance(android.icu.util.ULocale)` |  |
| `int getRoundingMode()` |  |
| `static final android.icu.text.NumberFormat getScientificInstance()` |  |
| `static android.icu.text.NumberFormat getScientificInstance(java.util.Locale)` |  |
| `static android.icu.text.NumberFormat getScientificInstance(android.icu.util.ULocale)` |  |
| `boolean isGroupingUsed()` |  |
| `boolean isParseIntegerOnly()` |  |
| `boolean isParseStrict()` |  |
| `abstract Number parse(String, java.text.ParsePosition)` |  |
| `Number parse(String) throws java.text.ParseException` |  |
| `android.icu.util.CurrencyAmount parseCurrency(CharSequence, java.text.ParsePosition)` |  |
| `final Object parseObject(String, java.text.ParsePosition)` |  |
| `void setContext(android.icu.text.DisplayContext)` |  |
| `void setCurrency(android.icu.util.Currency)` |  |
| `void setGroupingUsed(boolean)` |  |
| `void setMaximumFractionDigits(int)` |  |
| `void setMaximumIntegerDigits(int)` |  |
| `void setMinimumFractionDigits(int)` |  |
| `void setMinimumIntegerDigits(int)` |  |
| `void setParseIntegerOnly(boolean)` |  |
| `void setParseStrict(boolean)` |  |
| `void setRoundingMode(int)` |  |

---

### `class static NumberFormat.Field`

- **Extends:** `java.text.Format.Field`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NumberFormat.Field(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.text.NumberFormat.Field CURRENCY` |  |
| `static final android.icu.text.NumberFormat.Field DECIMAL_SEPARATOR` |  |
| `static final android.icu.text.NumberFormat.Field EXPONENT` |  |
| `static final android.icu.text.NumberFormat.Field EXPONENT_SIGN` |  |
| `static final android.icu.text.NumberFormat.Field EXPONENT_SYMBOL` |  |
| `static final android.icu.text.NumberFormat.Field FRACTION` |  |
| `static final android.icu.text.NumberFormat.Field GROUPING_SEPARATOR` |  |
| `static final android.icu.text.NumberFormat.Field INTEGER` |  |
| `static final android.icu.text.NumberFormat.Field PERCENT` |  |
| `static final android.icu.text.NumberFormat.Field PERMILLE` |  |
| `static final android.icu.text.NumberFormat.Field SIGN` |  |

---

### `class NumberingSystem`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NumberingSystem()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.text.NumberingSystem LATIN` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String[] getAvailableNames()` |  |
| `String getDescription()` |  |
| `static android.icu.text.NumberingSystem getInstance(int, boolean, String)` |  |
| `static android.icu.text.NumberingSystem getInstance(java.util.Locale)` |  |
| `static android.icu.text.NumberingSystem getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.text.NumberingSystem getInstance()` |  |
| `static android.icu.text.NumberingSystem getInstanceByName(String)` |  |
| `String getName()` |  |
| `int getRadix()` |  |
| `boolean isAlgorithmic()` |  |
| `static boolean isValidDigitString(String)` |  |

---

### `class PluralFormat`

- **Extends:** `android.icu.text.UFormat`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PluralFormat()` |  |
| `PluralFormat(android.icu.util.ULocale)` |  |
| `PluralFormat(java.util.Locale)` |  |
| `PluralFormat(android.icu.text.PluralRules)` |  |
| `PluralFormat(android.icu.util.ULocale, android.icu.text.PluralRules)` |  |
| `PluralFormat(java.util.Locale, android.icu.text.PluralRules)` |  |
| `PluralFormat(android.icu.util.ULocale, android.icu.text.PluralRules.PluralType)` |  |
| `PluralFormat(java.util.Locale, android.icu.text.PluralRules.PluralType)` |  |
| `PluralFormat(String)` |  |
| `PluralFormat(android.icu.util.ULocale, String)` |  |
| `PluralFormat(android.icu.text.PluralRules, String)` |  |
| `PluralFormat(android.icu.util.ULocale, android.icu.text.PluralRules, String)` |  |
| `PluralFormat(android.icu.util.ULocale, android.icu.text.PluralRules.PluralType, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyPattern(String)` |  |
| `boolean equals(android.icu.text.PluralFormat)` |  |
| `final String format(double)` |  |
| `StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `Number parse(String, java.text.ParsePosition)` |  |
| `Object parseObject(String, java.text.ParsePosition)` |  |
| `void setNumberFormat(android.icu.text.NumberFormat)` |  |
| `String toPattern()` |  |

---

### `class PluralRules`

- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.text.PluralRules DEFAULT` |  |
| `static final String KEYWORD_FEW = "few"` |  |
| `static final String KEYWORD_MANY = "many"` |  |
| `static final String KEYWORD_ONE = "one"` |  |
| `static final String KEYWORD_OTHER = "other"` |  |
| `static final String KEYWORD_TWO = "two"` |  |
| `static final String KEYWORD_ZERO = "zero"` |  |
| `static final double NO_UNIQUE_VALUE = -0.00123456777` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.text.PluralRules createRules(String)` |  |
| `boolean equals(android.icu.text.PluralRules)` |  |
| `static android.icu.text.PluralRules forLocale(android.icu.util.ULocale)` |  |
| `static android.icu.text.PluralRules forLocale(java.util.Locale)` |  |
| `static android.icu.text.PluralRules forLocale(android.icu.util.ULocale, android.icu.text.PluralRules.PluralType)` |  |
| `static android.icu.text.PluralRules forLocale(java.util.Locale, android.icu.text.PluralRules.PluralType)` |  |
| `java.util.Collection<java.lang.Double> getAllKeywordValues(String)` |  |
| `java.util.Set<java.lang.String> getKeywords()` |  |
| `java.util.Collection<java.lang.Double> getSamples(String)` |  |
| `double getUniqueKeywordValue(String)` |  |
| `static android.icu.text.PluralRules parseDescription(String) throws java.text.ParseException` |  |
| `String select(double)` |  |

---

### `enum PluralRules.PluralType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.PluralRules.PluralType CARDINAL` |  |
| `static final android.icu.text.PluralRules.PluralType ORDINAL` |  |

---

### `class final RelativeDateTimeFormatter`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String combineDateAndTime(String, String)` |  |
| `String format(double, android.icu.text.RelativeDateTimeFormatter.Direction, android.icu.text.RelativeDateTimeFormatter.RelativeUnit)` |  |
| `String format(android.icu.text.RelativeDateTimeFormatter.Direction, android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit)` |  |
| `String format(double, android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit)` |  |
| `String formatNumeric(double, android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit)` |  |
| `android.icu.text.DisplayContext getCapitalizationContext()` |  |
| `android.icu.text.RelativeDateTimeFormatter.Style getFormatStyle()` |  |
| `static android.icu.text.RelativeDateTimeFormatter getInstance()` |  |
| `static android.icu.text.RelativeDateTimeFormatter getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.text.RelativeDateTimeFormatter getInstance(java.util.Locale)` |  |
| `static android.icu.text.RelativeDateTimeFormatter getInstance(android.icu.util.ULocale, android.icu.text.NumberFormat)` |  |
| `static android.icu.text.RelativeDateTimeFormatter getInstance(android.icu.util.ULocale, android.icu.text.NumberFormat, android.icu.text.RelativeDateTimeFormatter.Style, android.icu.text.DisplayContext)` |  |
| `static android.icu.text.RelativeDateTimeFormatter getInstance(java.util.Locale, android.icu.text.NumberFormat)` |  |
| `android.icu.text.NumberFormat getNumberFormat()` |  |

---

### `enum RelativeDateTimeFormatter.AbsoluteUnit`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit DAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit FRIDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit MONDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit MONTH` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit NOW` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit SATURDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit SUNDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit THURSDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit TUESDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit WEDNESDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit WEEK` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.AbsoluteUnit YEAR` |  |

---

### `enum RelativeDateTimeFormatter.Direction`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.RelativeDateTimeFormatter.Direction LAST` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.Direction LAST_2` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.Direction NEXT` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.Direction NEXT_2` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.Direction PLAIN` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.Direction THIS` |  |

---

### `enum RelativeDateTimeFormatter.RelativeDateTimeUnit`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit DAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit FRIDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit HOUR` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit MINUTE` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit MONDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit MONTH` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit QUARTER` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit SATURDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit SECOND` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit SUNDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit THURSDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit TUESDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit WEDNESDAY` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit WEEK` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit YEAR` |  |

---

### `enum RelativeDateTimeFormatter.RelativeUnit`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeUnit DAYS` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeUnit HOURS` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeUnit MINUTES` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeUnit MONTHS` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeUnit SECONDS` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeUnit WEEKS` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.RelativeUnit YEARS` |  |

---

### `enum RelativeDateTimeFormatter.Style`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.RelativeDateTimeFormatter.Style LONG` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.Style NARROW` |  |
| `static final android.icu.text.RelativeDateTimeFormatter.Style SHORT` |  |

---

### `interface Replaceable`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int char32At(int)` |  |
| `char charAt(int)` |  |
| `void copy(int, int, int)` |  |
| `void getChars(int, int, char[], int)` |  |
| `boolean hasMetaData()` |  |
| `int length()` |  |
| `void replace(int, int, String)` |  |
| `void replace(int, int, char[], int, int)` |  |

---

### `class final RuleBasedCollator`

- **Extends:** `android.icu.text.Collator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RuleBasedCollator(String) throws java.lang.Exception` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.text.RuleBasedCollator cloneAsThawed()` |  |
| `int compare(String, String)` |  |
| `android.icu.text.CollationElementIterator getCollationElementIterator(String)` |  |
| `android.icu.text.CollationElementIterator getCollationElementIterator(java.text.CharacterIterator)` |  |
| `android.icu.text.CollationElementIterator getCollationElementIterator(android.icu.text.UCharacterIterator)` |  |
| `android.icu.text.CollationKey getCollationKey(String)` |  |
| `void getContractionsAndExpansions(android.icu.text.UnicodeSet, android.icu.text.UnicodeSet, boolean) throws java.lang.Exception` |  |
| `boolean getNumericCollation()` |  |
| `String getRules()` |  |
| `String getRules(boolean)` |  |
| `android.icu.util.VersionInfo getUCAVersion()` |  |
| `int getVariableTop()` |  |
| `android.icu.util.VersionInfo getVersion()` |  |
| `boolean isAlternateHandlingShifted()` |  |
| `boolean isCaseLevel()` |  |
| `boolean isFrenchCollation()` |  |
| `boolean isLowerCaseFirst()` |  |
| `boolean isUpperCaseFirst()` |  |
| `void setAlternateHandlingDefault()` |  |
| `void setAlternateHandlingShifted(boolean)` |  |
| `void setCaseFirstDefault()` |  |
| `void setCaseLevel(boolean)` |  |
| `void setCaseLevelDefault()` |  |
| `void setDecompositionDefault()` |  |
| `void setFrenchCollation(boolean)` |  |
| `void setFrenchCollationDefault()` |  |
| `void setLowerCaseFirst(boolean)` |  |
| `android.icu.text.RuleBasedCollator setMaxVariable(int)` |  |
| `void setNumericCollation(boolean)` |  |
| `void setNumericCollationDefault()` |  |
| `void setStrengthDefault()` |  |
| `void setUpperCaseFirst(boolean)` |  |

---

### `class final ScientificNumberFormatter`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String format(Object)` |  |
| `static android.icu.text.ScientificNumberFormatter getMarkupInstance(android.icu.util.ULocale, String, String)` |  |
| `static android.icu.text.ScientificNumberFormatter getMarkupInstance(android.icu.text.DecimalFormat, String, String)` |  |
| `static android.icu.text.ScientificNumberFormatter getSuperscriptInstance(android.icu.util.ULocale)` |  |
| `static android.icu.text.ScientificNumberFormatter getSuperscriptInstance(android.icu.text.DecimalFormat)` |  |

---

### `class abstract SearchIterator`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SearchIterator(java.text.CharacterIterator, android.icu.text.BreakIterator)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DONE = -1` |  |
| `android.icu.text.BreakIterator breakIterator` |  |
| `int matchLength` |  |
| `java.text.CharacterIterator targetText` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final int first()` |  |
| `final int following(int)` |  |
| `android.icu.text.BreakIterator getBreakIterator()` |  |
| `android.icu.text.SearchIterator.ElementComparisonType getElementComparisonType()` |  |
| `abstract int getIndex()` |  |
| `int getMatchLength()` |  |
| `int getMatchStart()` |  |
| `String getMatchedText()` |  |
| `java.text.CharacterIterator getTarget()` |  |
| `abstract int handleNext(int)` |  |
| `abstract int handlePrevious(int)` |  |
| `boolean isOverlapping()` |  |
| `final int last()` |  |
| `int next()` |  |
| `final int preceding(int)` |  |
| `int previous()` |  |
| `void reset()` |  |
| `void setBreakIterator(android.icu.text.BreakIterator)` |  |
| `void setElementComparisonType(android.icu.text.SearchIterator.ElementComparisonType)` |  |
| `void setIndex(int)` |  |
| `void setMatchLength(int)` |  |
| `void setOverlapping(boolean)` |  |
| `void setTarget(java.text.CharacterIterator)` |  |

---

### `enum SearchIterator.ElementComparisonType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.SearchIterator.ElementComparisonType ANY_BASE_WEIGHT_IS_WILDCARD` |  |
| `static final android.icu.text.SearchIterator.ElementComparisonType PATTERN_BASE_WEIGHT_IS_WILDCARD` |  |
| `static final android.icu.text.SearchIterator.ElementComparisonType STANDARD_ELEMENT_COMPARISON` |  |

---

### `class SelectFormat`

- **Extends:** `java.text.Format`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SelectFormat(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyPattern(String)` |  |
| `final String format(String)` |  |
| `StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `Object parseObject(String, java.text.ParsePosition)` |  |
| `String toPattern()` |  |

---

### `class SimpleDateFormat`

- **Extends:** `android.icu.text.DateFormat`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SimpleDateFormat()` |  |
| `SimpleDateFormat(String)` |  |
| `SimpleDateFormat(String, java.util.Locale)` |  |
| `SimpleDateFormat(String, android.icu.util.ULocale)` |  |
| `SimpleDateFormat(String, String, android.icu.util.ULocale)` |  |
| `SimpleDateFormat(String, android.icu.text.DateFormatSymbols)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void applyLocalizedPattern(String)` |  |
| `void applyPattern(String)` |  |
| `StringBuffer format(android.icu.util.Calendar, StringBuffer, java.text.FieldPosition)` |  |
| `java.util.Date get2DigitYearStart()` |  |
| `android.icu.text.DateFormatSymbols getDateFormatSymbols()` |  |
| `android.icu.text.NumberFormat getNumberFormat(char)` |  |
| `android.icu.text.DateFormatSymbols getSymbols()` |  |
| `android.icu.text.TimeZoneFormat getTimeZoneFormat()` |  |
| `int matchQuarterString(String, int, int, String[], android.icu.util.Calendar)` |  |
| `int matchString(String, int, int, String[], android.icu.util.Calendar)` |  |
| `void parse(String, android.icu.util.Calendar, java.text.ParsePosition)` |  |
| `android.icu.text.DateFormat.Field patternCharToDateFormatField(char)` |  |
| `void set2DigitYearStart(java.util.Date)` |  |
| `void setDateFormatSymbols(android.icu.text.DateFormatSymbols)` |  |
| `void setNumberFormat(String, android.icu.text.NumberFormat)` |  |
| `void setTimeZoneFormat(android.icu.text.TimeZoneFormat)` |  |
| `String subFormat(char, int, int, java.text.FieldPosition, android.icu.text.DateFormatSymbols, android.icu.util.Calendar) throws java.lang.IllegalArgumentException` |  |
| `int subParse(String, int, char, int, boolean, boolean, boolean[], android.icu.util.Calendar)` |  |
| `String toLocalizedPattern()` |  |
| `String toPattern()` |  |
| `String zeroPaddingNumber(long, int, int)` |  |

---

### `class StringPrepParseException`

- **Extends:** `java.text.ParseException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringPrepParseException(String, int)` |  |
| `StringPrepParseException(String, int, String, int)` |  |
| `StringPrepParseException(String, int, String, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACE_PREFIX_ERROR = 6` |  |
| `static final int BUFFER_OVERFLOW_ERROR = 9` |  |
| `static final int CHECK_BIDI_ERROR = 4` |  |
| `static final int DOMAIN_NAME_TOO_LONG_ERROR = 11` |  |
| `static final int ILLEGAL_CHAR_FOUND = 1` |  |
| `static final int INVALID_CHAR_FOUND = 0` |  |
| `static final int LABEL_TOO_LONG_ERROR = 8` |  |
| `static final int PROHIBITED_ERROR = 2` |  |
| `static final int STD3_ASCII_RULES_ERROR = 5` |  |
| `static final int UNASSIGNED_ERROR = 3` |  |
| `static final int VERIFICATION_ERROR = 7` |  |
| `static final int ZERO_LENGTH_LABEL = 10` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getError()` |  |

---

### `class final StringSearch`

- **Extends:** `android.icu.text.SearchIterator`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `StringSearch(String, java.text.CharacterIterator, android.icu.text.RuleBasedCollator, android.icu.text.BreakIterator)` |  |
| `StringSearch(String, java.text.CharacterIterator, android.icu.text.RuleBasedCollator)` |  |
| `StringSearch(String, java.text.CharacterIterator, java.util.Locale)` |  |
| `StringSearch(String, java.text.CharacterIterator, android.icu.util.ULocale)` |  |
| `StringSearch(String, String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.text.RuleBasedCollator getCollator()` |  |
| `int getIndex()` |  |
| `String getPattern()` |  |
| `int handleNext(int)` |  |
| `int handlePrevious(int)` |  |
| `boolean isCanonical()` |  |
| `void setCanonical(boolean)` |  |
| `void setCollator(android.icu.text.RuleBasedCollator)` |  |
| `void setPattern(String)` |  |

---

### `interface SymbolTable`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final char SYMBOL_REF = 36` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `char[] lookup(String)` |  |
| `android.icu.text.UnicodeMatcher lookupMatcher(int)` |  |
| `String parseReference(String, java.text.ParsePosition, int)` |  |

---

### `class TimeZoneFormat`

- **Extends:** `android.icu.text.UFormat`
- **Implements:** `android.icu.util.Freezable<android.icu.text.TimeZoneFormat> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TimeZoneFormat(android.icu.util.ULocale)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.text.TimeZoneFormat cloneAsThawed()` |  |
| `final String format(android.icu.text.TimeZoneFormat.Style, android.icu.util.TimeZone, long)` |  |
| `String format(android.icu.text.TimeZoneFormat.Style, android.icu.util.TimeZone, long, android.icu.util.Output<android.icu.text.TimeZoneFormat.TimeType>)` |  |
| `StringBuffer format(Object, StringBuffer, java.text.FieldPosition)` |  |
| `final String formatOffsetISO8601Basic(int, boolean, boolean, boolean)` |  |
| `final String formatOffsetISO8601Extended(int, boolean, boolean, boolean)` |  |
| `String formatOffsetLocalizedGMT(int)` |  |
| `String formatOffsetShortLocalizedGMT(int)` |  |
| `android.icu.text.TimeZoneFormat freeze()` |  |
| `java.util.EnumSet<android.icu.text.TimeZoneFormat.ParseOption> getDefaultParseOptions()` |  |
| `String getGMTOffsetDigits()` |  |
| `String getGMTOffsetPattern(android.icu.text.TimeZoneFormat.GMTOffsetPatternType)` |  |
| `String getGMTPattern()` |  |
| `String getGMTZeroFormat()` |  |
| `static android.icu.text.TimeZoneFormat getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.text.TimeZoneFormat getInstance(java.util.Locale)` |  |
| `android.icu.text.TimeZoneNames getTimeZoneNames()` |  |
| `boolean isFrozen()` |  |
| `android.icu.util.TimeZone parse(android.icu.text.TimeZoneFormat.Style, String, java.text.ParsePosition, java.util.EnumSet<android.icu.text.TimeZoneFormat.ParseOption>, android.icu.util.Output<android.icu.text.TimeZoneFormat.TimeType>)` |  |
| `android.icu.util.TimeZone parse(android.icu.text.TimeZoneFormat.Style, String, java.text.ParsePosition, android.icu.util.Output<android.icu.text.TimeZoneFormat.TimeType>)` |  |
| `final android.icu.util.TimeZone parse(String, java.text.ParsePosition)` |  |
| `final android.icu.util.TimeZone parse(String) throws java.text.ParseException` |  |
| `Object parseObject(String, java.text.ParsePosition)` |  |
| `final int parseOffsetISO8601(String, java.text.ParsePosition)` |  |
| `int parseOffsetLocalizedGMT(String, java.text.ParsePosition)` |  |
| `int parseOffsetShortLocalizedGMT(String, java.text.ParsePosition)` |  |
| `android.icu.text.TimeZoneFormat setDefaultParseOptions(java.util.EnumSet<android.icu.text.TimeZoneFormat.ParseOption>)` |  |
| `android.icu.text.TimeZoneFormat setGMTOffsetDigits(String)` |  |
| `android.icu.text.TimeZoneFormat setGMTOffsetPattern(android.icu.text.TimeZoneFormat.GMTOffsetPatternType, String)` |  |
| `android.icu.text.TimeZoneFormat setGMTPattern(String)` |  |
| `android.icu.text.TimeZoneFormat setGMTZeroFormat(String)` |  |
| `android.icu.text.TimeZoneFormat setTimeZoneNames(android.icu.text.TimeZoneNames)` |  |

---

### `enum TimeZoneFormat.GMTOffsetPatternType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.TimeZoneFormat.GMTOffsetPatternType NEGATIVE_H` |  |
| `static final android.icu.text.TimeZoneFormat.GMTOffsetPatternType NEGATIVE_HM` |  |
| `static final android.icu.text.TimeZoneFormat.GMTOffsetPatternType NEGATIVE_HMS` |  |
| `static final android.icu.text.TimeZoneFormat.GMTOffsetPatternType POSITIVE_H` |  |
| `static final android.icu.text.TimeZoneFormat.GMTOffsetPatternType POSITIVE_HM` |  |
| `static final android.icu.text.TimeZoneFormat.GMTOffsetPatternType POSITIVE_HMS` |  |

---

### `enum TimeZoneFormat.ParseOption`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.TimeZoneFormat.ParseOption ALL_STYLES` |  |
| `static final android.icu.text.TimeZoneFormat.ParseOption TZ_DATABASE_ABBREVIATIONS` |  |

---

### `enum TimeZoneFormat.Style`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.TimeZoneFormat.Style EXEMPLAR_LOCATION` |  |
| `static final android.icu.text.TimeZoneFormat.Style GENERIC_LOCATION` |  |
| `static final android.icu.text.TimeZoneFormat.Style GENERIC_LONG` |  |
| `static final android.icu.text.TimeZoneFormat.Style GENERIC_SHORT` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_BASIC_FIXED` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_BASIC_FULL` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_BASIC_LOCAL_FIXED` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_BASIC_LOCAL_FULL` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_BASIC_LOCAL_SHORT` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_BASIC_SHORT` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_EXTENDED_FIXED` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_EXTENDED_FULL` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_EXTENDED_LOCAL_FIXED` |  |
| `static final android.icu.text.TimeZoneFormat.Style ISO_EXTENDED_LOCAL_FULL` |  |
| `static final android.icu.text.TimeZoneFormat.Style LOCALIZED_GMT` |  |
| `static final android.icu.text.TimeZoneFormat.Style LOCALIZED_GMT_SHORT` |  |
| `static final android.icu.text.TimeZoneFormat.Style SPECIFIC_LONG` |  |
| `static final android.icu.text.TimeZoneFormat.Style SPECIFIC_SHORT` |  |
| `static final android.icu.text.TimeZoneFormat.Style ZONE_ID` |  |
| `static final android.icu.text.TimeZoneFormat.Style ZONE_ID_SHORT` |  |

---

### `enum TimeZoneFormat.TimeType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.TimeZoneFormat.TimeType DAYLIGHT` |  |
| `static final android.icu.text.TimeZoneFormat.TimeType STANDARD` |  |
| `static final android.icu.text.TimeZoneFormat.TimeType UNKNOWN` |  |

---

### `class abstract TimeZoneNames`

- **Implements:** `java.io.Serializable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract java.util.Set<java.lang.String> getAvailableMetaZoneIDs()` |  |
| `abstract java.util.Set<java.lang.String> getAvailableMetaZoneIDs(String)` |  |
| `final String getDisplayName(String, android.icu.text.TimeZoneNames.NameType, long)` |  |
| `String getExemplarLocationName(String)` |  |
| `static android.icu.text.TimeZoneNames getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.text.TimeZoneNames getInstance(java.util.Locale)` |  |
| `abstract String getMetaZoneDisplayName(String, android.icu.text.TimeZoneNames.NameType)` |  |
| `abstract String getMetaZoneID(String, long)` |  |
| `abstract String getReferenceZoneID(String, String)` |  |
| `static android.icu.text.TimeZoneNames getTZDBInstance(android.icu.util.ULocale)` |  |
| `abstract String getTimeZoneDisplayName(String, android.icu.text.TimeZoneNames.NameType)` |  |

---

### `enum TimeZoneNames.NameType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.TimeZoneNames.NameType EXEMPLAR_LOCATION` |  |
| `static final android.icu.text.TimeZoneNames.NameType LONG_DAYLIGHT` |  |
| `static final android.icu.text.TimeZoneNames.NameType LONG_GENERIC` |  |
| `static final android.icu.text.TimeZoneNames.NameType LONG_STANDARD` |  |
| `static final android.icu.text.TimeZoneNames.NameType SHORT_DAYLIGHT` |  |
| `static final android.icu.text.TimeZoneNames.NameType SHORT_GENERIC` |  |
| `static final android.icu.text.TimeZoneNames.NameType SHORT_STANDARD` |  |

---

### `class abstract Transliterator`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FORWARD = 0` |  |
| `static final int REVERSE = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.text.Transliterator createFromRules(String, String, int)` |  |
| `void filteredTransliterate(android.icu.text.Replaceable, android.icu.text.Transliterator.Position, boolean)` |  |
| `final void finishTransliteration(android.icu.text.Replaceable, android.icu.text.Transliterator.Position)` |  |
| `static final java.util.Enumeration<java.lang.String> getAvailableIDs()` |  |
| `static final java.util.Enumeration<java.lang.String> getAvailableSources()` |  |
| `static final java.util.Enumeration<java.lang.String> getAvailableTargets(String)` |  |
| `static final java.util.Enumeration<java.lang.String> getAvailableVariants(String, String)` |  |
| `static final String getDisplayName(String)` |  |
| `static String getDisplayName(String, java.util.Locale)` |  |
| `static String getDisplayName(String, android.icu.util.ULocale)` |  |
| `android.icu.text.Transliterator[] getElements()` |  |
| `final android.icu.text.UnicodeFilter getFilter()` |  |
| `final String getID()` |  |
| `static final android.icu.text.Transliterator getInstance(String)` |  |
| `static android.icu.text.Transliterator getInstance(String, int)` |  |
| `final android.icu.text.Transliterator getInverse()` |  |
| `final int getMaximumContextLength()` |  |
| `final android.icu.text.UnicodeSet getSourceSet()` |  |
| `android.icu.text.UnicodeSet getTargetSet()` |  |
| `void setFilter(android.icu.text.UnicodeFilter)` |  |
| `String toRules(boolean)` |  |
| `final int transliterate(android.icu.text.Replaceable, int, int)` |  |
| `final void transliterate(android.icu.text.Replaceable)` |  |
| `final String transliterate(String)` |  |
| `final void transliterate(android.icu.text.Replaceable, android.icu.text.Transliterator.Position, String)` |  |
| `final void transliterate(android.icu.text.Replaceable, android.icu.text.Transliterator.Position, int)` |  |
| `final void transliterate(android.icu.text.Replaceable, android.icu.text.Transliterator.Position)` |  |

---

### `class static Transliterator.Position`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Transliterator.Position()` |  |
| `Transliterator.Position(int, int, int)` |  |
| `Transliterator.Position(int, int, int, int)` |  |
| `Transliterator.Position(android.icu.text.Transliterator.Position)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int contextLimit` |  |
| `int contextStart` |  |
| `int limit` |  |
| `int start` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void set(android.icu.text.Transliterator.Position)` |  |
| `final void validate(int)` |  |

---

### `class abstract UCharacterIterator`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UCharacterIterator()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DONE = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone() throws java.lang.CloneNotSupportedException` |  |
| `abstract int current()` |  |
| `int currentCodePoint()` |  |
| `java.text.CharacterIterator getCharacterIterator()` |  |
| `abstract int getIndex()` |  |
| `static final android.icu.text.UCharacterIterator getInstance(android.icu.text.Replaceable)` |  |
| `static final android.icu.text.UCharacterIterator getInstance(String)` |  |
| `static final android.icu.text.UCharacterIterator getInstance(char[])` |  |
| `static final android.icu.text.UCharacterIterator getInstance(char[], int, int)` |  |
| `static final android.icu.text.UCharacterIterator getInstance(StringBuffer)` |  |
| `static final android.icu.text.UCharacterIterator getInstance(java.text.CharacterIterator)` |  |
| `abstract int getLength()` |  |
| `abstract int getText(char[], int)` |  |
| `final int getText(char[])` |  |
| `String getText()` |  |
| `int moveCodePointIndex(int)` |  |
| `int moveIndex(int)` |  |
| `abstract int next()` |  |
| `int nextCodePoint()` |  |
| `abstract int previous()` |  |
| `int previousCodePoint()` |  |
| `abstract void setIndex(int)` |  |
| `void setToLimit()` |  |
| `void setToStart()` |  |

---

### `class abstract UFormat`

- **Extends:** `java.text.Format`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UFormat()` |  |

---

### `class abstract UnicodeFilter`

- **Implements:** `android.icu.text.UnicodeMatcher`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract boolean contains(int)` |  |
| `int matches(android.icu.text.Replaceable, int[], int, boolean)` |  |

---

### `interface UnicodeMatcher`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final char ETHER = 65535` |  |
| `static final int U_MATCH = 2` |  |
| `static final int U_MISMATCH = 0` |  |
| `static final int U_PARTIAL_MATCH = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addMatchSetTo(android.icu.text.UnicodeSet)` |  |
| `int matches(android.icu.text.Replaceable, int[], int, boolean)` |  |
| `boolean matchesIndexValue(int)` |  |
| `String toPattern(boolean)` |  |

---

### `class UnicodeSet`

- **Extends:** `android.icu.text.UnicodeFilter`
- **Implements:** `java.lang.Comparable<android.icu.text.UnicodeSet> android.icu.util.Freezable<android.icu.text.UnicodeSet> java.lang.Iterable<java.lang.String>`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnicodeSet()` |  |
| `UnicodeSet(android.icu.text.UnicodeSet)` |  |
| `UnicodeSet(int, int)` |  |
| `UnicodeSet(int...)` |  |
| `UnicodeSet(String)` |  |
| `UnicodeSet(String, boolean)` |  |
| `UnicodeSet(String, int)` |  |
| `UnicodeSet(String, java.text.ParsePosition, android.icu.text.SymbolTable)` |  |
| `UnicodeSet(String, java.text.ParsePosition, android.icu.text.SymbolTable, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ADD_CASE_MAPPINGS = 4` |  |
| `static final android.icu.text.UnicodeSet ALL_CODE_POINTS` |  |
| `static final int CASE = 2` |  |
| `static final int CASE_INSENSITIVE = 2` |  |
| `static final android.icu.text.UnicodeSet EMPTY` |  |
| `static final int IGNORE_SPACE = 1` |  |
| `static final int MAX_VALUE = 1114111` |  |
| `static final int MIN_VALUE = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `StringBuffer _generatePattern(StringBuffer, boolean)` |  |
| `StringBuffer _generatePattern(StringBuffer, boolean, boolean)` |  |
| `android.icu.text.UnicodeSet add(int, int)` |  |
| `final android.icu.text.UnicodeSet add(int)` |  |
| `final android.icu.text.UnicodeSet add(CharSequence)` |  |
| `android.icu.text.UnicodeSet add(Iterable<?>)` |  |
| `android.icu.text.UnicodeSet addAll(int, int)` |  |
| `final android.icu.text.UnicodeSet addAll(CharSequence)` |  |
| `android.icu.text.UnicodeSet addAll(android.icu.text.UnicodeSet)` |  |
| `android.icu.text.UnicodeSet addAll(Iterable<?>)` |  |
| `<T extends java.lang.CharSequence> android.icu.text.UnicodeSet addAll(T...)` |  |
| `<T extends java.util.Collection<java.lang.String>> T addAllTo(T)` |  |
| `void addMatchSetTo(android.icu.text.UnicodeSet)` |  |
| `android.icu.text.UnicodeSet applyIntPropertyValue(int, int)` |  |
| `final android.icu.text.UnicodeSet applyPattern(String)` |  |
| `android.icu.text.UnicodeSet applyPattern(String, boolean)` |  |
| `android.icu.text.UnicodeSet applyPattern(String, int)` |  |
| `android.icu.text.UnicodeSet applyPropertyAlias(String, String)` |  |
| `android.icu.text.UnicodeSet applyPropertyAlias(String, String, android.icu.text.SymbolTable)` |  |
| `int charAt(int)` |  |
| `android.icu.text.UnicodeSet clear()` |  |
| `Object clone()` |  |
| `android.icu.text.UnicodeSet cloneAsThawed()` |  |
| `android.icu.text.UnicodeSet closeOver(int)` |  |
| `android.icu.text.UnicodeSet compact()` |  |
| `int compareTo(android.icu.text.UnicodeSet)` |  |
| `int compareTo(android.icu.text.UnicodeSet, android.icu.text.UnicodeSet.ComparisonStyle)` |  |
| `int compareTo(Iterable<java.lang.String>)` |  |
| `android.icu.text.UnicodeSet complement(int, int)` |  |
| `final android.icu.text.UnicodeSet complement(int)` |  |
| `android.icu.text.UnicodeSet complement()` |  |
| `final android.icu.text.UnicodeSet complement(CharSequence)` |  |
| `final android.icu.text.UnicodeSet complementAll(CharSequence)` |  |
| `android.icu.text.UnicodeSet complementAll(android.icu.text.UnicodeSet)` |  |
| `boolean contains(int)` |  |
| `boolean contains(int, int)` |  |
| `final boolean contains(CharSequence)` |  |
| `boolean containsAll(android.icu.text.UnicodeSet)` |  |
| `boolean containsAll(String)` |  |
| `<T extends java.lang.CharSequence> boolean containsAll(Iterable<T>)` |  |
| `boolean containsNone(int, int)` |  |
| `boolean containsNone(android.icu.text.UnicodeSet)` |  |
| `boolean containsNone(CharSequence)` |  |
| `<T extends java.lang.CharSequence> boolean containsNone(Iterable<T>)` |  |
| `final boolean containsSome(int, int)` |  |
| `final boolean containsSome(android.icu.text.UnicodeSet)` |  |
| `final boolean containsSome(CharSequence)` |  |
| `final <T extends java.lang.CharSequence> boolean containsSome(Iterable<T>)` |  |
| `android.icu.text.UnicodeSet freeze()` |  |
| `static android.icu.text.UnicodeSet from(CharSequence)` |  |
| `static android.icu.text.UnicodeSet fromAll(CharSequence)` |  |
| `int getRangeCount()` |  |
| `int getRangeEnd(int)` |  |
| `int getRangeStart(int)` |  |
| `int indexOf(int)` |  |
| `boolean isEmpty()` |  |
| `boolean isFrozen()` |  |
| `java.util.Iterator<java.lang.String> iterator()` |  |
| `boolean matchesIndexValue(int)` |  |
| `Iterable<android.icu.text.UnicodeSet.EntryRange> ranges()` |  |
| `android.icu.text.UnicodeSet remove(int, int)` |  |
| `final android.icu.text.UnicodeSet remove(int)` |  |
| `final android.icu.text.UnicodeSet remove(CharSequence)` |  |
| `final android.icu.text.UnicodeSet removeAll(CharSequence)` |  |
| `android.icu.text.UnicodeSet removeAll(android.icu.text.UnicodeSet)` |  |
| `<T extends java.lang.CharSequence> android.icu.text.UnicodeSet removeAll(Iterable<T>)` |  |
| `final android.icu.text.UnicodeSet removeAllStrings()` |  |
| `android.icu.text.UnicodeSet retain(int, int)` |  |
| `final android.icu.text.UnicodeSet retain(int)` |  |
| `final android.icu.text.UnicodeSet retain(CharSequence)` |  |
| `final android.icu.text.UnicodeSet retainAll(CharSequence)` |  |
| `android.icu.text.UnicodeSet retainAll(android.icu.text.UnicodeSet)` |  |
| `<T extends java.lang.CharSequence> android.icu.text.UnicodeSet retainAll(Iterable<T>)` |  |
| `android.icu.text.UnicodeSet set(int, int)` |  |
| `android.icu.text.UnicodeSet set(android.icu.text.UnicodeSet)` |  |
| `int size()` |  |
| `int span(CharSequence, android.icu.text.UnicodeSet.SpanCondition)` |  |
| `int span(CharSequence, int, android.icu.text.UnicodeSet.SpanCondition)` |  |
| `int spanBack(CharSequence, android.icu.text.UnicodeSet.SpanCondition)` |  |
| `int spanBack(CharSequence, int, android.icu.text.UnicodeSet.SpanCondition)` |  |
| `java.util.Collection<java.lang.String> strings()` |  |
| `String toPattern(boolean)` |  |

---

### `enum UnicodeSet.ComparisonStyle`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.UnicodeSet.ComparisonStyle LEXICOGRAPHIC` |  |
| `static final android.icu.text.UnicodeSet.ComparisonStyle LONGER_FIRST` |  |
| `static final android.icu.text.UnicodeSet.ComparisonStyle SHORTER_FIRST` |  |

---

### `class static UnicodeSet.EntryRange`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int codepoint` |  |
| `int codepointEnd` |  |

---

### `enum UnicodeSet.SpanCondition`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.UnicodeSet.SpanCondition CONDITION_COUNT` |  |
| `static final android.icu.text.UnicodeSet.SpanCondition CONTAINED` |  |
| `static final android.icu.text.UnicodeSet.SpanCondition NOT_CONTAINED` |  |
| `static final android.icu.text.UnicodeSet.SpanCondition SIMPLE` |  |

---

### `class UnicodeSetIterator`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnicodeSetIterator(android.icu.text.UnicodeSet)` |  |
| `UnicodeSetIterator()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static int IS_STRING` |  |
| `int codepoint` |  |
| `int codepointEnd` |  |
| `String string` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getString()` |  |
| `boolean next()` |  |
| `boolean nextRange()` |  |
| `void reset(android.icu.text.UnicodeSet)` |  |
| `void reset()` |  |

---

### `class UnicodeSetSpanner`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UnicodeSetSpanner(android.icu.text.UnicodeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int countIn(CharSequence)` |  |
| `int countIn(CharSequence, android.icu.text.UnicodeSetSpanner.CountMethod)` |  |
| `int countIn(CharSequence, android.icu.text.UnicodeSetSpanner.CountMethod, android.icu.text.UnicodeSet.SpanCondition)` |  |
| `String deleteFrom(CharSequence)` |  |
| `String deleteFrom(CharSequence, android.icu.text.UnicodeSet.SpanCondition)` |  |
| `android.icu.text.UnicodeSet getUnicodeSet()` |  |
| `String replaceFrom(CharSequence, CharSequence)` |  |
| `String replaceFrom(CharSequence, CharSequence, android.icu.text.UnicodeSetSpanner.CountMethod)` |  |
| `String replaceFrom(CharSequence, CharSequence, android.icu.text.UnicodeSetSpanner.CountMethod, android.icu.text.UnicodeSet.SpanCondition)` |  |
| `CharSequence trim(CharSequence)` |  |
| `CharSequence trim(CharSequence, android.icu.text.UnicodeSetSpanner.TrimOption)` |  |
| `CharSequence trim(CharSequence, android.icu.text.UnicodeSetSpanner.TrimOption, android.icu.text.UnicodeSet.SpanCondition)` |  |

---

### `enum UnicodeSetSpanner.CountMethod`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.UnicodeSetSpanner.CountMethod MIN_ELEMENTS` |  |
| `static final android.icu.text.UnicodeSetSpanner.CountMethod WHOLE_SPAN` |  |

---

### `enum UnicodeSetSpanner.TrimOption`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.text.UnicodeSetSpanner.TrimOption BOTH` |  |
| `static final android.icu.text.UnicodeSetSpanner.TrimOption LEADING` |  |
| `static final android.icu.text.UnicodeSetSpanner.TrimOption TRAILING` |  |

---

## Package: `android.icu.util`

### `class BuddhistCalendar`

- **Extends:** `android.icu.util.GregorianCalendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `BuddhistCalendar()` |  |
| `BuddhistCalendar(android.icu.util.TimeZone)` |  |
| `BuddhistCalendar(java.util.Locale)` |  |
| `BuddhistCalendar(android.icu.util.ULocale)` |  |
| `BuddhistCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `BuddhistCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `BuddhistCalendar(java.util.Date)` |  |
| `BuddhistCalendar(int, int, int)` |  |
| `BuddhistCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BE = 0` |  |

---

### `class abstract Calendar`

- **Implements:** `java.lang.Cloneable java.lang.Comparable<android.icu.util.Calendar> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Calendar()` |  |
| `Calendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `Calendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AM = 0` |  |
| `static final int AM_PM = 9` |  |
| `static final int APRIL = 3` |  |
| `static final int AUGUST = 7` |  |
| `static final int DATE = 5` |  |
| `static final int DAY_OF_MONTH = 5` |  |
| `static final int DAY_OF_WEEK = 7` |  |
| `static final int DAY_OF_WEEK_IN_MONTH = 8` |  |
| `static final int DAY_OF_YEAR = 6` |  |
| `static final int DECEMBER = 11` |  |
| `static final int DOW_LOCAL = 18` |  |
| `static final int DST_OFFSET = 16` |  |
| `static final int EPOCH_JULIAN_DAY = 2440588` |  |
| `static final int ERA = 0` |  |
| `static final int EXTENDED_YEAR = 19` |  |
| `static final int FEBRUARY = 1` |  |
| `static final int FRIDAY = 6` |  |
| `static final int GREATEST_MINIMUM = 1` |  |
| `static final int HOUR = 10` |  |
| `static final int HOUR_OF_DAY = 11` |  |
| `static final int INTERNALLY_SET = 1` |  |
| `static final int IS_LEAP_MONTH = 22` |  |
| `static final int JANUARY = 0` |  |
| `static final int JAN_1_1_JULIAN_DAY = 1721426` |  |
| `static final int JULIAN_DAY = 20` |  |
| `static final int JULY = 6` |  |
| `static final int JUNE = 5` |  |
| `static final int LEAST_MAXIMUM = 2` |  |
| `static final int MARCH = 2` |  |
| `static final int MAXIMUM = 3` |  |
| `static final java.util.Date MAX_DATE` |  |
| `static final int MAX_JULIAN = 2130706432` |  |
| `static final long MAX_MILLIS = 183882168921600000L` |  |
| `static final int MAY = 4` |  |
| `static final int MILLISECOND = 14` |  |
| `static final int MILLISECONDS_IN_DAY = 21` |  |
| `static final int MINIMUM = 0` |  |
| `static final int MINIMUM_USER_STAMP = 2` |  |
| `static final int MINUTE = 12` |  |
| `static final java.util.Date MIN_DATE` |  |
| `static final int MIN_JULIAN = -2130706432` |  |
| `static final long MIN_MILLIS = -184303902528000000L` |  |
| `static final int MONDAY = 2` |  |
| `static final int MONTH = 2` |  |
| `static final int NOVEMBER = 10` |  |
| `static final int OCTOBER = 9` |  |
| `static final long ONE_DAY = 86400000L` |  |
| `static final int ONE_HOUR = 3600000` |  |
| `static final int ONE_MINUTE = 60000` |  |
| `static final int ONE_SECOND = 1000` |  |
| `static final long ONE_WEEK = 604800000L` |  |
| `static final int PM = 1` |  |
| `static final int RESOLVE_REMAP = 32` |  |
| `static final int SATURDAY = 7` |  |
| `static final int SECOND = 13` |  |
| `static final int SEPTEMBER = 8` |  |
| `static final int SUNDAY = 1` |  |
| `static final int THURSDAY = 5` |  |
| `static final int TUESDAY = 3` |  |
| `static final int UNDECIMBER = 12` |  |
| `static final int UNSET = 0` |  |
| `static final int WALLTIME_FIRST = 1` |  |
| `static final int WALLTIME_LAST = 0` |  |
| `static final int WALLTIME_NEXT_VALID = 2` |  |
| `static final int WEDNESDAY = 4` |  |
| `static final int WEEK_OF_MONTH = 4` |  |
| `static final int WEEK_OF_YEAR = 3` |  |
| `static final int YEAR = 1` |  |
| `static final int YEAR_WOY = 17` |  |
| `static final int ZONE_OFFSET = 15` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void add(int, int)` |  |
| `boolean after(Object)` |  |
| `boolean before(Object)` |  |
| `final void clear()` |  |
| `final void clear(int)` |  |
| `Object clone()` |  |
| `int compareTo(android.icu.util.Calendar)` |  |
| `void complete()` |  |
| `void computeFields()` |  |
| `final void computeGregorianFields(int)` |  |
| `int computeGregorianMonthStart(int, int)` |  |
| `int computeJulianDay()` |  |
| `void computeTime()` |  |
| `int fieldDifference(java.util.Date, int)` |  |
| `String fieldName(int)` |  |
| `static final long floorDivide(long, long)` |  |
| `static final int floorDivide(int, int)` |  |
| `static final int floorDivide(int, int, int[])` |  |
| `static final int floorDivide(long, int, int[])` |  |
| `final int get(int)` |  |
| `int getActualMaximum(int)` |  |
| `int getActualMinimum(int)` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `android.icu.text.DateFormat getDateTimeFormat(int, int, java.util.Locale)` |  |
| `android.icu.text.DateFormat getDateTimeFormat(int, int, android.icu.util.ULocale)` |  |
| `String getDisplayName(java.util.Locale)` |  |
| `String getDisplayName(android.icu.util.ULocale)` |  |
| `final int getFieldCount()` |  |
| `int[][][] getFieldResolutionTable()` |  |
| `int getFirstDayOfWeek()` |  |
| `final int getGreatestMinimum(int)` |  |
| `final int getGregorianDayOfMonth()` |  |
| `final int getGregorianDayOfYear()` |  |
| `final int getGregorianMonth()` |  |
| `final int getGregorianYear()` |  |
| `static android.icu.util.Calendar getInstance()` |  |
| `static android.icu.util.Calendar getInstance(android.icu.util.TimeZone)` |  |
| `static android.icu.util.Calendar getInstance(java.util.Locale)` |  |
| `static android.icu.util.Calendar getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.util.Calendar getInstance(android.icu.util.TimeZone, java.util.Locale)` |  |
| `static android.icu.util.Calendar getInstance(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `static final String[] getKeywordValuesForLocale(String, android.icu.util.ULocale, boolean)` |  |
| `final int getLeastMaximum(int)` |  |
| `int getLimit(int, int)` |  |
| `final int getMaximum(int)` |  |
| `int getMinimalDaysInFirstWeek()` |  |
| `final int getMinimum(int)` |  |
| `int getRepeatedWallTimeOption()` |  |
| `int getSkippedWallTimeOption()` |  |
| `final int getStamp(int)` |  |
| `final java.util.Date getTime()` |  |
| `long getTimeInMillis()` |  |
| `android.icu.util.TimeZone getTimeZone()` |  |
| `String getType()` |  |
| `android.icu.util.Calendar.WeekData getWeekData()` |  |
| `static android.icu.util.Calendar.WeekData getWeekDataForRegion(String)` |  |
| `static final int gregorianMonthLength(int, int)` |  |
| `static final int gregorianPreviousMonthLength(int, int)` |  |
| `void handleComputeFields(int)` |  |
| `int handleComputeJulianDay(int)` |  |
| `abstract int handleComputeMonthStart(int, int, boolean)` |  |
| `int[] handleCreateFields()` |  |
| `android.icu.text.DateFormat handleGetDateFormat(String, java.util.Locale)` |  |
| `android.icu.text.DateFormat handleGetDateFormat(String, String, java.util.Locale)` |  |
| `android.icu.text.DateFormat handleGetDateFormat(String, android.icu.util.ULocale)` |  |
| `abstract int handleGetExtendedYear()` |  |
| `abstract int handleGetLimit(int, int)` |  |
| `int handleGetMonthLength(int, int)` |  |
| `int handleGetYearLength(int)` |  |
| `final int internalGet(int)` |  |
| `final int internalGet(int, int)` |  |
| `final long internalGetTimeInMillis()` |  |
| `final void internalSet(int, int)` |  |
| `boolean isEquivalentTo(android.icu.util.Calendar)` |  |
| `static final boolean isGregorianLeapYear(int)` |  |
| `boolean isLenient()` |  |
| `final boolean isSet(int)` |  |
| `boolean isWeekend(java.util.Date)` |  |
| `boolean isWeekend()` |  |
| `static final int julianDayToDayOfWeek(int)` |  |
| `static final long julianDayToMillis(int)` |  |
| `static final int millisToJulianDay(long)` |  |
| `int newerField(int, int)` |  |
| `int newestStamp(int, int, int)` |  |
| `void pinField(int)` |  |
| `void prepareGetActual(int, boolean)` |  |
| `int resolveFields(int[][][])` |  |
| `final void roll(int, boolean)` |  |
| `void roll(int, int)` |  |
| `final void set(int, int)` |  |
| `final void set(int, int, int)` |  |
| `final void set(int, int, int, int, int)` |  |
| `final void set(int, int, int, int, int, int)` |  |
| `void setFirstDayOfWeek(int)` |  |
| `void setLenient(boolean)` |  |
| `void setMinimalDaysInFirstWeek(int)` |  |
| `void setRepeatedWallTimeOption(int)` |  |
| `void setSkippedWallTimeOption(int)` |  |
| `final void setTime(java.util.Date)` |  |
| `void setTimeInMillis(long)` |  |
| `void setTimeZone(android.icu.util.TimeZone)` |  |
| `android.icu.util.Calendar setWeekData(android.icu.util.Calendar.WeekData)` |  |
| `void validateField(int)` |  |
| `final void validateField(int, int, int)` |  |
| `void validateFields()` |  |
| `int weekNumber(int, int, int)` |  |
| `final int weekNumber(int, int)` |  |

---

### `class static final Calendar.WeekData`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Calendar.WeekData(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final int firstDayOfWeek` |  |
| `final int minimalDaysInFirstWeek` |  |
| `final int weekendCease` |  |
| `final int weekendCeaseMillis` |  |
| `final int weekendOnset` |  |
| `final int weekendOnsetMillis` |  |

---

### `class ChineseCalendar`

- **Extends:** `android.icu.util.Calendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ChineseCalendar()` |  |
| `ChineseCalendar(java.util.Date)` |  |
| `ChineseCalendar(int, int, int, int)` |  |
| `ChineseCalendar(int, int, int, int, int, int, int)` |  |
| `ChineseCalendar(int, int, int, int, int)` |  |
| `ChineseCalendar(int, int, int, int, int, int, int, int)` |  |
| `ChineseCalendar(java.util.Locale)` |  |
| `ChineseCalendar(android.icu.util.TimeZone)` |  |
| `ChineseCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `ChineseCalendar(android.icu.util.ULocale)` |  |
| `ChineseCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int handleComputeMonthStart(int, int, boolean)` |  |
| `android.icu.text.DateFormat handleGetDateFormat(String, String, android.icu.util.ULocale)` |  |
| `int handleGetExtendedYear()` |  |
| `int handleGetLimit(int, int)` |  |

---

### `class final CopticCalendar`

- **Extends:** `android.icu.util.Calendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CopticCalendar()` |  |
| `CopticCalendar(android.icu.util.TimeZone)` |  |
| `CopticCalendar(java.util.Locale)` |  |
| `CopticCalendar(android.icu.util.ULocale)` |  |
| `CopticCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `CopticCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `CopticCalendar(int, int, int)` |  |
| `CopticCalendar(java.util.Date)` |  |
| `CopticCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AMSHIR = 5` |  |
| `static final int BABA = 1` |  |
| `static final int BARAMHAT = 6` |  |
| `static final int BARAMOUDA = 7` |  |
| `static final int BASHANS = 8` |  |
| `static final int EPEP = 10` |  |
| `static final int HATOR = 2` |  |
| `static final int KIAHK = 3` |  |
| `static final int MESRA = 11` |  |
| `static final int NASIE = 12` |  |
| `static final int PAONA = 9` |  |
| `static final int TOBA = 4` |  |
| `static final int TOUT = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int handleComputeMonthStart(int, int, boolean)` |  |
| `int handleGetLimit(int, int)` |  |

---

### `class Currency`

- **Extends:** `android.icu.util.MeasureUnit`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Currency(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int LONG_NAME = 1` |  |
| `static final int NARROW_SYMBOL_NAME = 3` |  |
| `static final int PLURAL_LONG_NAME = 2` |  |
| `static final int SYMBOL_NAME = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.util.Currency fromJavaCurrency(java.util.Currency)` |  |
| `static java.util.Set<android.icu.util.Currency> getAvailableCurrencies()` |  |
| `static String[] getAvailableCurrencyCodes(android.icu.util.ULocale, java.util.Date)` |  |
| `static String[] getAvailableCurrencyCodes(java.util.Locale, java.util.Date)` |  |
| `static java.util.Locale[] getAvailableLocales()` |  |
| `static android.icu.util.ULocale[] getAvailableULocales()` |  |
| `String getCurrencyCode()` |  |
| `int getDefaultFractionDigits()` |  |
| `int getDefaultFractionDigits(android.icu.util.Currency.CurrencyUsage)` |  |
| `String getDisplayName()` |  |
| `String getDisplayName(java.util.Locale)` |  |
| `static android.icu.util.Currency getInstance(java.util.Locale)` |  |
| `static android.icu.util.Currency getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.util.Currency getInstance(String)` |  |
| `static final String[] getKeywordValuesForLocale(String, android.icu.util.ULocale, boolean)` |  |
| `String getName(java.util.Locale, int, boolean[])` |  |
| `String getName(android.icu.util.ULocale, int, boolean[])` |  |
| `String getName(java.util.Locale, int, String, boolean[])` |  |
| `String getName(android.icu.util.ULocale, int, String, boolean[])` |  |
| `int getNumericCode()` |  |
| `double getRoundingIncrement()` |  |
| `double getRoundingIncrement(android.icu.util.Currency.CurrencyUsage)` |  |
| `String getSymbol()` |  |
| `String getSymbol(java.util.Locale)` |  |
| `String getSymbol(android.icu.util.ULocale)` |  |
| `static boolean isAvailable(String, java.util.Date, java.util.Date)` |  |
| `java.util.Currency toJavaCurrency()` |  |

---

### `enum Currency.CurrencyUsage`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.util.Currency.CurrencyUsage CASH` |  |
| `static final android.icu.util.Currency.CurrencyUsage STANDARD` |  |

---

### `class CurrencyAmount`

- **Extends:** `android.icu.util.Measure`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CurrencyAmount(Number, android.icu.util.Currency)` |  |
| `CurrencyAmount(double, android.icu.util.Currency)` |  |
| `CurrencyAmount(Number, java.util.Currency)` |  |
| `CurrencyAmount(double, java.util.Currency)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.util.Currency getCurrency()` |  |

---

### `class final DateInterval`

- **Implements:** `java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateInterval(long, long)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getFromDate()` |  |
| `long getToDate()` |  |

---

### `class final EthiopicCalendar`

- **Extends:** `android.icu.util.Calendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `EthiopicCalendar()` |  |
| `EthiopicCalendar(android.icu.util.TimeZone)` |  |
| `EthiopicCalendar(java.util.Locale)` |  |
| `EthiopicCalendar(android.icu.util.ULocale)` |  |
| `EthiopicCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `EthiopicCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `EthiopicCalendar(int, int, int)` |  |
| `EthiopicCalendar(java.util.Date)` |  |
| `EthiopicCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int GENBOT = 8` |  |
| `static final int HAMLE = 10` |  |
| `static final int HEDAR = 2` |  |
| `static final int MEGABIT = 6` |  |
| `static final int MESKEREM = 0` |  |
| `static final int MIAZIA = 7` |  |
| `static final int NEHASSE = 11` |  |
| `static final int PAGUMEN = 12` |  |
| `static final int SENE = 9` |  |
| `static final int TAHSAS = 3` |  |
| `static final int TEKEMT = 1` |  |
| `static final int TER = 4` |  |
| `static final int YEKATIT = 5` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int handleComputeMonthStart(int, int, boolean)` |  |
| `boolean isAmeteAlemEra()` |  |
| `void setAmeteAlemEra(boolean)` |  |

---

### `interface Freezable<T>`

- **Extends:** `java.lang.Cloneable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `T cloneAsThawed()` |  |
| `T freeze()` |  |
| `boolean isFrozen()` |  |

---

### `class GregorianCalendar`

- **Extends:** `android.icu.util.Calendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GregorianCalendar()` |  |
| `GregorianCalendar(android.icu.util.TimeZone)` |  |
| `GregorianCalendar(java.util.Locale)` |  |
| `GregorianCalendar(android.icu.util.ULocale)` |  |
| `GregorianCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `GregorianCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `GregorianCalendar(int, int, int)` |  |
| `GregorianCalendar(int, int, int, int, int)` |  |
| `GregorianCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AD = 1` |  |
| `static final int BC = 0` |  |
| `transient boolean invertGregorian` |  |
| `transient boolean isGregorian` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final java.util.Date getGregorianChange()` |  |
| `int handleComputeMonthStart(int, int, boolean)` |  |
| `int handleGetExtendedYear()` |  |
| `int handleGetLimit(int, int)` |  |
| `boolean isLeapYear(int)` |  |
| `void setGregorianChange(java.util.Date)` |  |

---

### `class HebrewCalendar`

- **Extends:** `android.icu.util.Calendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `HebrewCalendar()` |  |
| `HebrewCalendar(android.icu.util.TimeZone)` |  |
| `HebrewCalendar(java.util.Locale)` |  |
| `HebrewCalendar(android.icu.util.ULocale)` |  |
| `HebrewCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `HebrewCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `HebrewCalendar(int, int, int)` |  |
| `HebrewCalendar(java.util.Date)` |  |
| `HebrewCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ADAR = 6` |  |
| `static final int ADAR_1 = 5` |  |
| `static final int AV = 11` |  |
| `static final int ELUL = 12` |  |
| `static final int HESHVAN = 1` |  |
| `static final int IYAR = 8` |  |
| `static final int KISLEV = 2` |  |
| `static final int NISAN = 7` |  |
| `static final int SHEVAT = 4` |  |
| `static final int SIVAN = 9` |  |
| `static final int TAMUZ = 10` |  |
| `static final int TEVET = 3` |  |
| `static final int TISHRI = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int handleComputeMonthStart(int, int, boolean)` |  |
| `int handleGetExtendedYear()` |  |
| `int handleGetLimit(int, int)` |  |

---

### `class ICUUncheckedIOException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ICUUncheckedIOException()` |  |
| `ICUUncheckedIOException(String)` |  |
| `ICUUncheckedIOException(Throwable)` |  |
| `ICUUncheckedIOException(String, Throwable)` |  |

---

### `class IllformedLocaleException`

- **Extends:** `java.lang.RuntimeException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IllformedLocaleException()` |  |
| `IllformedLocaleException(String)` |  |
| `IllformedLocaleException(String, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getErrorIndex()` |  |

---

### `class IndianCalendar`

- **Extends:** `android.icu.util.Calendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IndianCalendar()` |  |
| `IndianCalendar(android.icu.util.TimeZone)` |  |
| `IndianCalendar(java.util.Locale)` |  |
| `IndianCalendar(android.icu.util.ULocale)` |  |
| `IndianCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `IndianCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `IndianCalendar(java.util.Date)` |  |
| `IndianCalendar(int, int, int)` |  |
| `IndianCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int AGRAHAYANA = 8` |  |
| `static final int ASADHA = 3` |  |
| `static final int ASVINA = 6` |  |
| `static final int BHADRA = 5` |  |
| `static final int CHAITRA = 0` |  |
| `static final int IE = 0` |  |
| `static final int JYAISTHA = 2` |  |
| `static final int KARTIKA = 7` |  |
| `static final int MAGHA = 10` |  |
| `static final int PAUSA = 9` |  |
| `static final int PHALGUNA = 11` |  |
| `static final int SRAVANA = 4` |  |
| `static final int VAISAKHA = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int handleComputeMonthStart(int, int, boolean)` |  |
| `int handleGetExtendedYear()` |  |
| `int handleGetLimit(int, int)` |  |

---

### `class IslamicCalendar`

- **Extends:** `android.icu.util.Calendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `IslamicCalendar()` |  |
| `IslamicCalendar(android.icu.util.TimeZone)` |  |
| `IslamicCalendar(java.util.Locale)` |  |
| `IslamicCalendar(android.icu.util.ULocale)` |  |
| `IslamicCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `IslamicCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `IslamicCalendar(java.util.Date)` |  |
| `IslamicCalendar(int, int, int)` |  |
| `IslamicCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DHU_AL_HIJJAH = 11` |  |
| `static final int DHU_AL_QIDAH = 10` |  |
| `static final int JUMADA_1 = 4` |  |
| `static final int JUMADA_2 = 5` |  |
| `static final int MUHARRAM = 0` |  |
| `static final int RABI_1 = 2` |  |
| `static final int RABI_2 = 3` |  |
| `static final int RAJAB = 6` |  |
| `static final int RAMADAN = 8` |  |
| `static final int SAFAR = 1` |  |
| `static final int SHABAN = 7` |  |
| `static final int SHAWWAL = 9` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.util.IslamicCalendar.CalculationType getCalculationType()` |  |
| `int handleComputeMonthStart(int, int, boolean)` |  |
| `int handleGetExtendedYear()` |  |
| `int handleGetLimit(int, int)` |  |
| `void setCalculationType(android.icu.util.IslamicCalendar.CalculationType)` |  |

---

### `enum IslamicCalendar.CalculationType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.util.IslamicCalendar.CalculationType ISLAMIC` |  |
| `static final android.icu.util.IslamicCalendar.CalculationType ISLAMIC_CIVIL` |  |
| `static final android.icu.util.IslamicCalendar.CalculationType ISLAMIC_TBLA` |  |
| `static final android.icu.util.IslamicCalendar.CalculationType ISLAMIC_UMALQURA` |  |

---

### `class JapaneseCalendar`

- **Extends:** `android.icu.util.GregorianCalendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `JapaneseCalendar()` |  |
| `JapaneseCalendar(android.icu.util.TimeZone)` |  |
| `JapaneseCalendar(java.util.Locale)` |  |
| `JapaneseCalendar(android.icu.util.ULocale)` |  |
| `JapaneseCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `JapaneseCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `JapaneseCalendar(java.util.Date)` |  |
| `JapaneseCalendar(int, int, int, int)` |  |
| `JapaneseCalendar(int, int, int)` |  |
| `JapaneseCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int HEISEI` |  |
| `static final int MEIJI` |  |
| `static final int REIWA` |  |
| `static final int SHOWA` |  |
| `static final int TAISHO` |  |

---

### `class final LocaleData`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ALT_QUOTATION_END = 3` |  |
| `static final int ALT_QUOTATION_START = 2` |  |
| `static final int QUOTATION_END = 1` |  |
| `static final int QUOTATION_START = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.util.VersionInfo getCLDRVersion()` |  |
| `String getDelimiter(int)` |  |
| `static android.icu.util.LocaleData getInstance(android.icu.util.ULocale)` |  |
| `static android.icu.util.LocaleData getInstance()` |  |
| `static android.icu.util.LocaleData.MeasurementSystem getMeasurementSystem(android.icu.util.ULocale)` |  |
| `boolean getNoSubstitute()` |  |
| `static android.icu.util.LocaleData.PaperSize getPaperSize(android.icu.util.ULocale)` |  |
| `void setNoSubstitute(boolean)` |  |

---

### `class static final LocaleData.MeasurementSystem`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.util.LocaleData.MeasurementSystem SI` |  |
| `static final android.icu.util.LocaleData.MeasurementSystem UK` |  |
| `static final android.icu.util.LocaleData.MeasurementSystem US` |  |

---

### `class static final LocaleData.PaperSize`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getHeight()` |  |
| `int getWidth()` |  |

---

### `class Measure`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Measure(Number, android.icu.util.MeasureUnit)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Number getNumber()` |  |
| `android.icu.util.MeasureUnit getUnit()` |  |

---

### `class MeasureUnit`

- **Implements:** `java.io.Serializable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.util.MeasureUnit ACRE` |  |
| `static final android.icu.util.MeasureUnit ACRE_FOOT` |  |
| `static final android.icu.util.MeasureUnit AMPERE` |  |
| `static final android.icu.util.MeasureUnit ARC_MINUTE` |  |
| `static final android.icu.util.MeasureUnit ARC_SECOND` |  |
| `static final android.icu.util.MeasureUnit ASTRONOMICAL_UNIT` |  |
| `static final android.icu.util.MeasureUnit ATMOSPHERE` |  |
| `static final android.icu.util.MeasureUnit BIT` |  |
| `static final android.icu.util.MeasureUnit BUSHEL` |  |
| `static final android.icu.util.MeasureUnit BYTE` |  |
| `static final android.icu.util.MeasureUnit CALORIE` |  |
| `static final android.icu.util.MeasureUnit CARAT` |  |
| `static final android.icu.util.MeasureUnit CELSIUS` |  |
| `static final android.icu.util.MeasureUnit CENTILITER` |  |
| `static final android.icu.util.MeasureUnit CENTIMETER` |  |
| `static final android.icu.util.MeasureUnit CENTURY` |  |
| `static final android.icu.util.MeasureUnit CUBIC_CENTIMETER` |  |
| `static final android.icu.util.MeasureUnit CUBIC_FOOT` |  |
| `static final android.icu.util.MeasureUnit CUBIC_INCH` |  |
| `static final android.icu.util.MeasureUnit CUBIC_KILOMETER` |  |
| `static final android.icu.util.MeasureUnit CUBIC_METER` |  |
| `static final android.icu.util.MeasureUnit CUBIC_MILE` |  |
| `static final android.icu.util.MeasureUnit CUBIC_YARD` |  |
| `static final android.icu.util.MeasureUnit CUP` |  |
| `static final android.icu.util.MeasureUnit CUP_METRIC` |  |
| `static final android.icu.util.TimeUnit DAY` |  |
| `static final android.icu.util.MeasureUnit DECILITER` |  |
| `static final android.icu.util.MeasureUnit DECIMETER` |  |
| `static final android.icu.util.MeasureUnit DEGREE` |  |
| `static final android.icu.util.MeasureUnit FAHRENHEIT` |  |
| `static final android.icu.util.MeasureUnit FATHOM` |  |
| `static final android.icu.util.MeasureUnit FLUID_OUNCE` |  |
| `static final android.icu.util.MeasureUnit FOODCALORIE` |  |
| `static final android.icu.util.MeasureUnit FOOT` |  |
| `static final android.icu.util.MeasureUnit FURLONG` |  |
| `static final android.icu.util.MeasureUnit GALLON` |  |
| `static final android.icu.util.MeasureUnit GALLON_IMPERIAL` |  |
| `static final android.icu.util.MeasureUnit GENERIC_TEMPERATURE` |  |
| `static final android.icu.util.MeasureUnit GIGABIT` |  |
| `static final android.icu.util.MeasureUnit GIGABYTE` |  |
| `static final android.icu.util.MeasureUnit GIGAHERTZ` |  |
| `static final android.icu.util.MeasureUnit GIGAWATT` |  |
| `static final android.icu.util.MeasureUnit GRAM` |  |
| `static final android.icu.util.MeasureUnit G_FORCE` |  |
| `static final android.icu.util.MeasureUnit HECTARE` |  |
| `static final android.icu.util.MeasureUnit HECTOLITER` |  |
| `static final android.icu.util.MeasureUnit HECTOPASCAL` |  |
| `static final android.icu.util.MeasureUnit HERTZ` |  |
| `static final android.icu.util.MeasureUnit HORSEPOWER` |  |
| `static final android.icu.util.TimeUnit HOUR` |  |
| `static final android.icu.util.MeasureUnit INCH` |  |
| `static final android.icu.util.MeasureUnit INCH_HG` |  |
| `static final android.icu.util.MeasureUnit JOULE` |  |
| `static final android.icu.util.MeasureUnit KARAT` |  |
| `static final android.icu.util.MeasureUnit KELVIN` |  |
| `static final android.icu.util.MeasureUnit KILOBIT` |  |
| `static final android.icu.util.MeasureUnit KILOBYTE` |  |
| `static final android.icu.util.MeasureUnit KILOCALORIE` |  |
| `static final android.icu.util.MeasureUnit KILOGRAM` |  |
| `static final android.icu.util.MeasureUnit KILOHERTZ` |  |
| `static final android.icu.util.MeasureUnit KILOJOULE` |  |
| `static final android.icu.util.MeasureUnit KILOMETER` |  |
| `static final android.icu.util.MeasureUnit KILOMETER_PER_HOUR` |  |
| `static final android.icu.util.MeasureUnit KILOWATT` |  |
| `static final android.icu.util.MeasureUnit KILOWATT_HOUR` |  |
| `static final android.icu.util.MeasureUnit KNOT` |  |
| `static final android.icu.util.MeasureUnit LIGHT_YEAR` |  |
| `static final android.icu.util.MeasureUnit LITER` |  |
| `static final android.icu.util.MeasureUnit LITER_PER_100KILOMETERS` |  |
| `static final android.icu.util.MeasureUnit LITER_PER_KILOMETER` |  |
| `static final android.icu.util.MeasureUnit LUX` |  |
| `static final android.icu.util.MeasureUnit MEGABIT` |  |
| `static final android.icu.util.MeasureUnit MEGABYTE` |  |
| `static final android.icu.util.MeasureUnit MEGAHERTZ` |  |
| `static final android.icu.util.MeasureUnit MEGALITER` |  |
| `static final android.icu.util.MeasureUnit MEGAWATT` |  |
| `static final android.icu.util.MeasureUnit METER` |  |
| `static final android.icu.util.MeasureUnit METER_PER_SECOND` |  |
| `static final android.icu.util.MeasureUnit METER_PER_SECOND_SQUARED` |  |
| `static final android.icu.util.MeasureUnit METRIC_TON` |  |
| `static final android.icu.util.MeasureUnit MICROGRAM` |  |
| `static final android.icu.util.MeasureUnit MICROMETER` |  |
| `static final android.icu.util.MeasureUnit MICROSECOND` |  |
| `static final android.icu.util.MeasureUnit MILE` |  |
| `static final android.icu.util.MeasureUnit MILE_PER_GALLON` |  |
| `static final android.icu.util.MeasureUnit MILE_PER_GALLON_IMPERIAL` |  |
| `static final android.icu.util.MeasureUnit MILE_PER_HOUR` |  |
| `static final android.icu.util.MeasureUnit MILE_SCANDINAVIAN` |  |
| `static final android.icu.util.MeasureUnit MILLIAMPERE` |  |
| `static final android.icu.util.MeasureUnit MILLIBAR` |  |
| `static final android.icu.util.MeasureUnit MILLIGRAM` |  |
| `static final android.icu.util.MeasureUnit MILLIGRAM_PER_DECILITER` |  |
| `static final android.icu.util.MeasureUnit MILLILITER` |  |
| `static final android.icu.util.MeasureUnit MILLIMETER` |  |
| `static final android.icu.util.MeasureUnit MILLIMETER_OF_MERCURY` |  |
| `static final android.icu.util.MeasureUnit MILLIMOLE_PER_LITER` |  |
| `static final android.icu.util.MeasureUnit MILLISECOND` |  |
| `static final android.icu.util.MeasureUnit MILLIWATT` |  |
| `static final android.icu.util.TimeUnit MINUTE` |  |
| `static final android.icu.util.TimeUnit MONTH` |  |
| `static final android.icu.util.MeasureUnit NANOMETER` |  |
| `static final android.icu.util.MeasureUnit NANOSECOND` |  |
| `static final android.icu.util.MeasureUnit NAUTICAL_MILE` |  |
| `static final android.icu.util.MeasureUnit OHM` |  |
| `static final android.icu.util.MeasureUnit OUNCE` |  |
| `static final android.icu.util.MeasureUnit OUNCE_TROY` |  |
| `static final android.icu.util.MeasureUnit PARSEC` |  |
| `static final android.icu.util.MeasureUnit PART_PER_MILLION` |  |
| `static final android.icu.util.MeasureUnit PERCENT` |  |
| `static final android.icu.util.MeasureUnit PERMILLE` |  |
| `static final android.icu.util.MeasureUnit PETABYTE` |  |
| `static final android.icu.util.MeasureUnit PICOMETER` |  |
| `static final android.icu.util.MeasureUnit PINT` |  |
| `static final android.icu.util.MeasureUnit PINT_METRIC` |  |
| `static final android.icu.util.MeasureUnit POINT` |  |
| `static final android.icu.util.MeasureUnit POUND` |  |
| `static final android.icu.util.MeasureUnit POUND_PER_SQUARE_INCH` |  |
| `static final android.icu.util.MeasureUnit QUART` |  |
| `static final android.icu.util.MeasureUnit RADIAN` |  |
| `static final android.icu.util.MeasureUnit REVOLUTION_ANGLE` |  |
| `static final android.icu.util.TimeUnit SECOND` |  |
| `static final android.icu.util.MeasureUnit SQUARE_CENTIMETER` |  |
| `static final android.icu.util.MeasureUnit SQUARE_FOOT` |  |
| `static final android.icu.util.MeasureUnit SQUARE_INCH` |  |
| `static final android.icu.util.MeasureUnit SQUARE_KILOMETER` |  |
| `static final android.icu.util.MeasureUnit SQUARE_METER` |  |
| `static final android.icu.util.MeasureUnit SQUARE_MILE` |  |
| `static final android.icu.util.MeasureUnit SQUARE_YARD` |  |
| `static final android.icu.util.MeasureUnit STONE` |  |
| `static final android.icu.util.MeasureUnit TABLESPOON` |  |
| `static final android.icu.util.MeasureUnit TEASPOON` |  |
| `static final android.icu.util.MeasureUnit TERABIT` |  |
| `static final android.icu.util.MeasureUnit TERABYTE` |  |
| `static final android.icu.util.MeasureUnit TON` |  |
| `static final android.icu.util.MeasureUnit VOLT` |  |
| `static final android.icu.util.MeasureUnit WATT` |  |
| `static final android.icu.util.TimeUnit WEEK` |  |
| `static final android.icu.util.MeasureUnit YARD` |  |
| `static final android.icu.util.TimeUnit YEAR` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static java.util.Set<android.icu.util.MeasureUnit> getAvailable(String)` |  |
| `static java.util.Set<android.icu.util.MeasureUnit> getAvailable()` |  |
| `static java.util.Set<java.lang.String> getAvailableTypes()` |  |
| `String getSubtype()` |  |
| `String getType()` |  |

---

### `class Output<T>`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Output()` |  |
| `Output(T)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `T value` |  |

---

### `interface RangeValueIterator`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean next(android.icu.util.RangeValueIterator.Element)` |  |
| `void reset()` |  |

---

### `class static RangeValueIterator.Element`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RangeValueIterator.Element()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int limit` |  |
| `int start` |  |
| `int value` |  |

---

### `class TaiwanCalendar`

- **Extends:** `android.icu.util.GregorianCalendar`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TaiwanCalendar()` |  |
| `TaiwanCalendar(android.icu.util.TimeZone)` |  |
| `TaiwanCalendar(java.util.Locale)` |  |
| `TaiwanCalendar(android.icu.util.ULocale)` |  |
| `TaiwanCalendar(android.icu.util.TimeZone, java.util.Locale)` |  |
| `TaiwanCalendar(android.icu.util.TimeZone, android.icu.util.ULocale)` |  |
| `TaiwanCalendar(java.util.Date)` |  |
| `TaiwanCalendar(int, int, int)` |  |
| `TaiwanCalendar(int, int, int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BEFORE_MINGUO = 0` |  |
| `static final int MINGUO = 1` |  |

---

### `class TimeUnit`

- **Extends:** `android.icu.util.MeasureUnit`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.util.TimeUnit[] values()` |  |

---

### `class abstract TimeZone`

- **Implements:** `java.lang.Cloneable android.icu.util.Freezable<android.icu.util.TimeZone> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TimeZone()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int GENERIC_LOCATION = 7` |  |
| `static final android.icu.util.TimeZone GMT_ZONE` |  |
| `static final int LONG = 1` |  |
| `static final int LONG_GENERIC = 3` |  |
| `static final int LONG_GMT = 5` |  |
| `static final int SHORT = 0` |  |
| `static final int SHORT_COMMONLY_USED = 6` |  |
| `static final int SHORT_GENERIC = 2` |  |
| `static final int SHORT_GMT = 4` |  |
| `static final int TIMEZONE_ICU = 0` |  |
| `static final int TIMEZONE_JDK = 1` |  |
| `static final android.icu.util.TimeZone UNKNOWN_ZONE` |  |
| `static final String UNKNOWN_ZONE_ID = "Etc/Unknown"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `Object clone()` |  |
| `android.icu.util.TimeZone cloneAsThawed()` |  |
| `static int countEquivalentIDs(String)` |  |
| `android.icu.util.TimeZone freeze()` |  |
| `static java.util.Set<java.lang.String> getAvailableIDs(android.icu.util.TimeZone.SystemTimeZoneType, String, Integer)` |  |
| `static String[] getAvailableIDs(int)` |  |
| `static String[] getAvailableIDs(String)` |  |
| `static String[] getAvailableIDs()` |  |
| `static String getCanonicalID(String)` |  |
| `static String getCanonicalID(String, boolean[])` |  |
| `int getDSTSavings()` |  |
| `static android.icu.util.TimeZone getDefault()` |  |
| `final String getDisplayName()` |  |
| `final String getDisplayName(java.util.Locale)` |  |
| `final String getDisplayName(android.icu.util.ULocale)` |  |
| `final String getDisplayName(boolean, int)` |  |
| `String getDisplayName(boolean, int, java.util.Locale)` |  |
| `String getDisplayName(boolean, int, android.icu.util.ULocale)` |  |
| `static String getEquivalentID(String, int)` |  |
| `static android.icu.util.TimeZone getFrozenTimeZone(String)` |  |
| `String getID()` |  |
| `static String getIDForWindowsID(String, String)` |  |
| `abstract int getOffset(int, int, int, int, int, int)` |  |
| `int getOffset(long)` |  |
| `void getOffset(long, boolean, int[])` |  |
| `abstract int getRawOffset()` |  |
| `static String getRegion(String)` |  |
| `static String getTZDataVersion()` |  |
| `static android.icu.util.TimeZone getTimeZone(String)` |  |
| `static android.icu.util.TimeZone getTimeZone(String, int)` |  |
| `static String getWindowsID(String)` |  |
| `boolean hasSameRules(android.icu.util.TimeZone)` |  |
| `abstract boolean inDaylightTime(java.util.Date)` |  |
| `boolean isFrozen()` |  |
| `boolean observesDaylightTime()` |  |
| `void setID(String)` |  |
| `abstract void setRawOffset(int)` |  |
| `abstract boolean useDaylightTime()` |  |

---

### `enum TimeZone.SystemTimeZoneType`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.util.TimeZone.SystemTimeZoneType ANY` |  |
| `static final android.icu.util.TimeZone.SystemTimeZoneType CANONICAL` |  |
| `static final android.icu.util.TimeZone.SystemTimeZoneType CANONICAL_LOCATION` |  |

---

### `class final ULocale`

- **Implements:** `java.lang.Comparable<android.icu.util.ULocale> java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ULocale(String)` |  |
| `ULocale(String, String)` |  |
| `ULocale(String, String, String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.util.ULocale CANADA` |  |
| `static final android.icu.util.ULocale CANADA_FRENCH` |  |
| `static final android.icu.util.ULocale CHINA` |  |
| `static final android.icu.util.ULocale CHINESE` |  |
| `static final android.icu.util.ULocale ENGLISH` |  |
| `static final android.icu.util.ULocale FRANCE` |  |
| `static final android.icu.util.ULocale FRENCH` |  |
| `static final android.icu.util.ULocale GERMAN` |  |
| `static final android.icu.util.ULocale GERMANY` |  |
| `static final android.icu.util.ULocale ITALIAN` |  |
| `static final android.icu.util.ULocale ITALY` |  |
| `static final android.icu.util.ULocale JAPAN` |  |
| `static final android.icu.util.ULocale JAPANESE` |  |
| `static final android.icu.util.ULocale KOREA` |  |
| `static final android.icu.util.ULocale KOREAN` |  |
| `static final android.icu.util.ULocale PRC` |  |
| `static final char PRIVATE_USE_EXTENSION = 120` |  |
| `static final android.icu.util.ULocale ROOT` |  |
| `static final android.icu.util.ULocale SIMPLIFIED_CHINESE` |  |
| `static final android.icu.util.ULocale TAIWAN` |  |
| `static final android.icu.util.ULocale TRADITIONAL_CHINESE` |  |
| `static final android.icu.util.ULocale UK` |  |
| `static final char UNICODE_LOCALE_EXTENSION = 117` |  |
| `static final android.icu.util.ULocale US` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.util.ULocale acceptLanguage(String, android.icu.util.ULocale[], boolean[])` |  |
| `static android.icu.util.ULocale acceptLanguage(android.icu.util.ULocale[], android.icu.util.ULocale[], boolean[])` |  |
| `static android.icu.util.ULocale acceptLanguage(String, boolean[])` |  |
| `static android.icu.util.ULocale acceptLanguage(android.icu.util.ULocale[], boolean[])` |  |
| `static android.icu.util.ULocale addLikelySubtags(android.icu.util.ULocale)` |  |
| `static String canonicalize(String)` |  |
| `Object clone()` |  |
| `int compareTo(android.icu.util.ULocale)` |  |
| `static android.icu.util.ULocale createCanonical(String)` |  |
| `static android.icu.util.ULocale forLanguageTag(String)` |  |
| `static android.icu.util.ULocale forLocale(java.util.Locale)` |  |
| `static android.icu.util.ULocale[] getAvailableLocales()` |  |
| `String getBaseName()` |  |
| `static String getBaseName(String)` |  |
| `String getCharacterOrientation()` |  |
| `String getCountry()` |  |
| `static String getCountry(String)` |  |
| `static android.icu.util.ULocale getDefault()` |  |
| `static android.icu.util.ULocale getDefault(android.icu.util.ULocale.Category)` |  |
| `String getDisplayCountry()` |  |
| `String getDisplayCountry(android.icu.util.ULocale)` |  |
| `static String getDisplayCountry(String, String)` |  |
| `static String getDisplayCountry(String, android.icu.util.ULocale)` |  |
| `static String getDisplayKeyword(String)` |  |
| `static String getDisplayKeyword(String, String)` |  |
| `static String getDisplayKeyword(String, android.icu.util.ULocale)` |  |
| `String getDisplayKeywordValue(String)` |  |
| `String getDisplayKeywordValue(String, android.icu.util.ULocale)` |  |
| `static String getDisplayKeywordValue(String, String, String)` |  |
| `static String getDisplayKeywordValue(String, String, android.icu.util.ULocale)` |  |
| `String getDisplayLanguage()` |  |
| `String getDisplayLanguage(android.icu.util.ULocale)` |  |
| `static String getDisplayLanguage(String, String)` |  |
| `static String getDisplayLanguage(String, android.icu.util.ULocale)` |  |
| `String getDisplayLanguageWithDialect()` |  |
| `String getDisplayLanguageWithDialect(android.icu.util.ULocale)` |  |
| `static String getDisplayLanguageWithDialect(String, String)` |  |
| `static String getDisplayLanguageWithDialect(String, android.icu.util.ULocale)` |  |
| `String getDisplayName()` |  |
| `String getDisplayName(android.icu.util.ULocale)` |  |
| `static String getDisplayName(String, String)` |  |
| `static String getDisplayName(String, android.icu.util.ULocale)` |  |
| `String getDisplayNameWithDialect()` |  |
| `String getDisplayNameWithDialect(android.icu.util.ULocale)` |  |
| `static String getDisplayNameWithDialect(String, String)` |  |
| `static String getDisplayNameWithDialect(String, android.icu.util.ULocale)` |  |
| `String getDisplayScript()` |  |
| `String getDisplayScript(android.icu.util.ULocale)` |  |
| `static String getDisplayScript(String, String)` |  |
| `static String getDisplayScript(String, android.icu.util.ULocale)` |  |
| `String getDisplayVariant()` |  |
| `String getDisplayVariant(android.icu.util.ULocale)` |  |
| `static String getDisplayVariant(String, String)` |  |
| `static String getDisplayVariant(String, android.icu.util.ULocale)` |  |
| `String getExtension(char)` |  |
| `java.util.Set<java.lang.Character> getExtensionKeys()` |  |
| `static String getFallback(String)` |  |
| `android.icu.util.ULocale getFallback()` |  |
| `String getISO3Country()` |  |
| `static String getISO3Country(String)` |  |
| `String getISO3Language()` |  |
| `static String getISO3Language(String)` |  |
| `static String[] getISOCountries()` |  |
| `static String[] getISOLanguages()` |  |
| `String getKeywordValue(String)` |  |
| `static String getKeywordValue(String, String)` |  |
| `java.util.Iterator<java.lang.String> getKeywords()` |  |
| `static java.util.Iterator<java.lang.String> getKeywords(String)` |  |
| `String getLanguage()` |  |
| `static String getLanguage(String)` |  |
| `String getLineOrientation()` |  |
| `String getName()` |  |
| `static String getName(String)` |  |
| `String getScript()` |  |
| `static String getScript(String)` |  |
| `java.util.Set<java.lang.String> getUnicodeLocaleAttributes()` |  |
| `java.util.Set<java.lang.String> getUnicodeLocaleKeys()` |  |
| `String getUnicodeLocaleType(String)` |  |
| `String getVariant()` |  |
| `static String getVariant(String)` |  |
| `boolean isRightToLeft()` |  |
| `static android.icu.util.ULocale minimizeSubtags(android.icu.util.ULocale)` |  |
| `android.icu.util.ULocale setKeywordValue(String, String)` |  |
| `static String setKeywordValue(String, String, String)` |  |
| `String toLanguageTag()` |  |
| `static String toLegacyKey(String)` |  |
| `static String toLegacyType(String, String)` |  |
| `java.util.Locale toLocale()` |  |
| `static String toUnicodeLocaleKey(String)` |  |
| `static String toUnicodeLocaleType(String, String)` |  |

---

### `class static final ULocale.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ULocale.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.icu.util.ULocale.Builder addUnicodeLocaleAttribute(String)` |  |
| `android.icu.util.ULocale build()` |  |
| `android.icu.util.ULocale.Builder clear()` |  |
| `android.icu.util.ULocale.Builder clearExtensions()` |  |
| `android.icu.util.ULocale.Builder removeUnicodeLocaleAttribute(String)` |  |
| `android.icu.util.ULocale.Builder setExtension(char, String)` |  |
| `android.icu.util.ULocale.Builder setLanguage(String)` |  |
| `android.icu.util.ULocale.Builder setLanguageTag(String)` |  |
| `android.icu.util.ULocale.Builder setLocale(android.icu.util.ULocale)` |  |
| `android.icu.util.ULocale.Builder setRegion(String)` |  |
| `android.icu.util.ULocale.Builder setScript(String)` |  |
| `android.icu.util.ULocale.Builder setUnicodeLocaleKeyword(String, String)` |  |
| `android.icu.util.ULocale.Builder setVariant(String)` |  |

---

### `enum ULocale.Category`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.icu.util.ULocale.Category DISPLAY` |  |
| `static final android.icu.util.ULocale.Category FORMAT` |  |

---

### `class final UniversalTimeScale`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DB2_TIME = 8` |  |
| `static final int DOTNET_DATE_TIME = 4` |  |
| `static final int EPOCH_OFFSET_PLUS_1_VALUE = 6` |  |
| `static final int EPOCH_OFFSET_VALUE = 1` |  |
| `static final int EXCEL_TIME = 7` |  |
| `static final int FROM_MAX_VALUE = 3` |  |
| `static final int FROM_MIN_VALUE = 2` |  |
| `static final int ICU4C_TIME = 2` |  |
| `static final int JAVA_TIME = 0` |  |
| `static final int MAC_OLD_TIME = 5` |  |
| `static final int MAC_TIME = 6` |  |
| `static final int TO_MAX_VALUE = 5` |  |
| `static final int TO_MIN_VALUE = 4` |  |
| `static final int UNITS_VALUE = 0` |  |
| `static final int UNIX_MICROSECONDS_TIME = 9` |  |
| `static final int UNIX_TIME = 1` |  |
| `static final int WINDOWS_FILE_TIME = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.icu.math.BigDecimal bigDecimalFrom(double, int)` |  |
| `static android.icu.math.BigDecimal bigDecimalFrom(long, int)` |  |
| `static android.icu.math.BigDecimal bigDecimalFrom(android.icu.math.BigDecimal, int)` |  |
| `static long from(long, int)` |  |
| `static long getTimeScaleValue(int, int)` |  |
| `static android.icu.math.BigDecimal toBigDecimal(long, int)` |  |
| `static android.icu.math.BigDecimal toBigDecimal(android.icu.math.BigDecimal, int)` |  |
| `static long toLong(long, int)` |  |

---

### `interface ValueIterator`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean next(android.icu.util.ValueIterator.Element)` |  |
| `void reset()` |  |
| `void setRange(int, int)` |  |

---

### `class static final ValueIterator.Element`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ValueIterator.Element()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int integer` |  |
| `Object value` |  |

---

### `class final VersionInfo`

- **Implements:** `java.lang.Comparable<android.icu.util.VersionInfo>`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.icu.util.VersionInfo ICU_VERSION` |  |
| `static final android.icu.util.VersionInfo UCOL_BUILDER_VERSION` |  |
| `static final android.icu.util.VersionInfo UCOL_RUNTIME_VERSION` |  |
| `static final android.icu.util.VersionInfo UNICODE_10_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_11_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_12_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_12_1` |  |
| `static final android.icu.util.VersionInfo UNICODE_13_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_1_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_1_0_1` |  |
| `static final android.icu.util.VersionInfo UNICODE_1_1_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_1_1_5` |  |
| `static final android.icu.util.VersionInfo UNICODE_2_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_2_1_2` |  |
| `static final android.icu.util.VersionInfo UNICODE_2_1_5` |  |
| `static final android.icu.util.VersionInfo UNICODE_2_1_8` |  |
| `static final android.icu.util.VersionInfo UNICODE_2_1_9` |  |
| `static final android.icu.util.VersionInfo UNICODE_3_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_3_0_1` |  |
| `static final android.icu.util.VersionInfo UNICODE_3_1_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_3_1_1` |  |
| `static final android.icu.util.VersionInfo UNICODE_3_2` |  |
| `static final android.icu.util.VersionInfo UNICODE_4_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_4_0_1` |  |
| `static final android.icu.util.VersionInfo UNICODE_4_1` |  |
| `static final android.icu.util.VersionInfo UNICODE_5_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_5_1` |  |
| `static final android.icu.util.VersionInfo UNICODE_5_2` |  |
| `static final android.icu.util.VersionInfo UNICODE_6_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_6_1` |  |
| `static final android.icu.util.VersionInfo UNICODE_6_2` |  |
| `static final android.icu.util.VersionInfo UNICODE_6_3` |  |
| `static final android.icu.util.VersionInfo UNICODE_7_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_8_0` |  |
| `static final android.icu.util.VersionInfo UNICODE_9_0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int compareTo(android.icu.util.VersionInfo)` |  |
| `static android.icu.util.VersionInfo getInstance(String)` |  |
| `static android.icu.util.VersionInfo getInstance(int, int, int, int)` |  |
| `static android.icu.util.VersionInfo getInstance(int, int, int)` |  |
| `static android.icu.util.VersionInfo getInstance(int, int)` |  |
| `static android.icu.util.VersionInfo getInstance(int)` |  |
| `int getMajor()` |  |
| `int getMicro()` |  |
| `int getMilli()` |  |
| `int getMinor()` |  |

---

## Package: `android.inputmethodservice`

### `class abstract AbstractInputMethodService`

- **Extends:** `android.app.Service`
- **Implements:** `android.view.KeyEvent.Callback`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractInputMethodService()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.KeyEvent.DispatcherState getKeyDispatcherState()` |  |
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract android.inputmethodservice.AbstractInputMethodService.AbstractInputMethodImpl onCreateInputMethodInterface()` |  |
| `abstract android.inputmethodservice.AbstractInputMethodService.AbstractInputMethodSessionImpl onCreateInputMethodSessionInterface()` |  |
| `boolean onGenericMotionEvent(android.view.MotionEvent)` |  |
| `boolean onTrackballEvent(android.view.MotionEvent)` |  |

---

### `class abstract AbstractInputMethodService.AbstractInputMethodImpl`

- **Implements:** `android.view.inputmethod.InputMethod`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractInputMethodService.AbstractInputMethodImpl()` |  |

---

### `class abstract AbstractInputMethodService.AbstractInputMethodSessionImpl`

- **Implements:** `android.view.inputmethod.InputMethodSession`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AbstractInputMethodService.AbstractInputMethodSessionImpl()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void dispatchGenericMotionEvent(int, android.view.MotionEvent, android.view.inputmethod.InputMethodSession.EventCallback)` |  |
| `void dispatchKeyEvent(int, android.view.KeyEvent, android.view.inputmethod.InputMethodSession.EventCallback)` |  |
| `void dispatchTrackballEvent(int, android.view.MotionEvent, android.view.inputmethod.InputMethodSession.EventCallback)` |  |
| `boolean isEnabled()` |  |
| `boolean isRevoked()` |  |
| `void revokeSelf()` |  |
| `void setEnabled(boolean)` |  |

---

### `class ExtractEditText`

- **Extends:** `android.widget.EditText`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ExtractEditText(android.content.Context)` |  |
| `ExtractEditText(android.content.Context, android.util.AttributeSet)` |  |
| `ExtractEditText(android.content.Context, android.util.AttributeSet, int)` |  |
| `ExtractEditText(android.content.Context, android.util.AttributeSet, int, int)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void finishInternalChanges()` |  |
| `boolean hasVerticalScrollBar()` |  |
| `void startInternalChanges()` |  |

---

### `class InputMethodService`

- **Extends:** `android.inputmethodservice.AbstractInputMethodService`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputMethodService()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BACK_DISPOSITION_ADJUST_NOTHING = 3` |  |
| `static final int BACK_DISPOSITION_DEFAULT = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getBackDisposition()` |  |
| `int getCandidatesHiddenVisibility()` |  |
| `android.view.inputmethod.InputBinding getCurrentInputBinding()` |  |
| `android.view.inputmethod.InputConnection getCurrentInputConnection()` |  |
| `android.view.inputmethod.EditorInfo getCurrentInputEditorInfo()` |  |
| `boolean getCurrentInputStarted()` |  |
| `android.view.LayoutInflater getLayoutInflater()` |  |
| `int getMaxWidth()` |  |
| `CharSequence getTextForImeAction(int)` |  |
| `android.app.Dialog getWindow()` |  |
| `void hideStatusIcon()` |  |
| `void hideWindow()` |  |
| `boolean isExtractViewShown()` |  |
| `boolean isFullscreenMode()` |  |
| `boolean isInputViewShown()` |  |
| `boolean isShowInputRequested()` |  |
| `void onAppPrivateCommand(String, android.os.Bundle)` |  |
| `void onBindInput()` |  |
| `void onComputeInsets(android.inputmethodservice.InputMethodService.Insets)` |  |
| `void onConfigureWindow(android.view.Window, boolean, boolean)` |  |
| `android.view.View onCreateCandidatesView()` |  |
| `android.view.View onCreateExtractTextView()` |  |
| `android.inputmethodservice.AbstractInputMethodService.AbstractInputMethodImpl onCreateInputMethodInterface()` |  |
| `android.inputmethodservice.AbstractInputMethodService.AbstractInputMethodSessionImpl onCreateInputMethodSessionInterface()` |  |
| `android.view.View onCreateInputView()` |  |
| `void onCurrentInputMethodSubtypeChanged(android.view.inputmethod.InputMethodSubtype)` |  |
| `void onDisplayCompletions(android.view.inputmethod.CompletionInfo[])` |  |
| `boolean onEvaluateFullscreenMode()` |  |
| `boolean onExtractTextContextMenuItem(int)` |  |
| `void onExtractedCursorMovement(int, int)` |  |
| `void onExtractedSelectionChanged(int, int)` |  |
| `void onExtractedTextClicked()` |  |
| `void onExtractingInputChanged(android.view.inputmethod.EditorInfo)` |  |
| `void onFinishCandidatesView(boolean)` |  |
| `void onFinishInput()` |  |
| `void onFinishInputView(boolean)` |  |
| `void onInitializeInterface()` |  |
| `boolean onInlineSuggestionsResponse(@NonNull android.view.inputmethod.InlineSuggestionsResponse)` |  |
| `boolean onKeyDown(int, android.view.KeyEvent)` |  |
| `boolean onKeyLongPress(int, android.view.KeyEvent)` |  |
| `boolean onKeyMultiple(int, int, android.view.KeyEvent)` |  |
| `boolean onKeyUp(int, android.view.KeyEvent)` |  |
| `boolean onShowInputRequested(int, boolean)` |  |
| `void onStartCandidatesView(android.view.inputmethod.EditorInfo, boolean)` |  |
| `void onStartInput(android.view.inputmethod.EditorInfo, boolean)` |  |
| `void onStartInputView(android.view.inputmethod.EditorInfo, boolean)` |  |
| `void onUnbindInput()` |  |
| `void onUpdateCursorAnchorInfo(android.view.inputmethod.CursorAnchorInfo)` |  |
| `void onUpdateExtractedText(int, android.view.inputmethod.ExtractedText)` |  |
| `void onUpdateExtractingViews(android.view.inputmethod.EditorInfo)` |  |
| `void onUpdateExtractingVisibility(android.view.inputmethod.EditorInfo)` |  |
| `void onUpdateSelection(int, int, int, int, int, int)` |  |
| `void onWindowHidden()` |  |
| `void onWindowShown()` |  |
| `void requestHideSelf(int)` |  |
| `final void requestShowSelf(int)` |  |
| `boolean sendDefaultEditorAction(boolean)` |  |
| `void sendDownUpKeyEvents(int)` |  |
| `void sendKeyChar(char)` |  |
| `void setBackDisposition(int)` |  |
| `void setCandidatesView(android.view.View)` |  |
| `void setCandidatesViewShown(boolean)` |  |
| `void setExtractView(android.view.View)` |  |
| `void setExtractViewShown(boolean)` |  |
| `void setInputView(android.view.View)` |  |
| `final boolean shouldOfferSwitchingToNextInputMethod()` |  |
| `void showStatusIcon(@DrawableRes int)` |  |
| `void showWindow(boolean)` |  |
| `void switchInputMethod(String)` |  |
| `final void switchInputMethod(String, android.view.inputmethod.InputMethodSubtype)` |  |
| `final boolean switchToNextInputMethod(boolean)` |  |
| `final boolean switchToPreviousInputMethod()` |  |
| `void updateFullscreenMode()` |  |
| `void updateInputViewShown()` |  |

---

### `class InputMethodService.InputMethodImpl`

- **Extends:** `android.inputmethodservice.AbstractInputMethodService.AbstractInputMethodImpl`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputMethodService.InputMethodImpl()` |  |

---

### `class InputMethodService.InputMethodSessionImpl`

- **Extends:** `android.inputmethodservice.AbstractInputMethodService.AbstractInputMethodSessionImpl`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputMethodService.InputMethodSessionImpl()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void appPrivateCommand(String, android.os.Bundle)` |  |
| `void displayCompletions(android.view.inputmethod.CompletionInfo[])` |  |
| `void finishInput()` |  |
| `void toggleSoftInput(int, int)` |  |
| `void updateCursor(android.graphics.Rect)` |  |
| `void updateCursorAnchorInfo(android.view.inputmethod.CursorAnchorInfo)` |  |
| `void updateExtractedText(int, android.view.inputmethod.ExtractedText)` |  |
| `void updateSelection(int, int, int, int, int, int)` |  |
| `void viewClicked(boolean)` |  |

---

### `class static final InputMethodService.Insets`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `InputMethodService.Insets()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TOUCHABLE_INSETS_CONTENT = 1` |  |
| `static final int TOUCHABLE_INSETS_FRAME = 0` |  |
| `static final int TOUCHABLE_INSETS_REGION = 3` |  |
| `static final int TOUCHABLE_INSETS_VISIBLE = 2` |  |
| `int contentTopInsets` |  |
| `int touchableInsets` |  |
| `final android.graphics.Region touchableRegion` |  |
| `int visibleTopInsets` |  |

---

### `class Keyboard` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static Keyboard.Key` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class static Keyboard.Row` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class KeyboardView` ~~DEPRECATED~~

- **Extends:** `android.view.View`
- **Implements:** `android.view.View.OnClickListener`
- **Annotations:** `@Deprecated`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onClick(android.view.View)` |  |

---

### `interface static KeyboardView.OnKeyboardActionListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

## Package: `android.location`

### `class Address`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Address(java.util.Locale)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void clearLatitude()` |  |
| `void clearLongitude()` |  |
| `int describeContents()` |  |
| `String getAddressLine(int)` |  |
| `String getAdminArea()` |  |
| `String getCountryCode()` |  |
| `String getCountryName()` |  |
| `android.os.Bundle getExtras()` |  |
| `String getFeatureName()` |  |
| `double getLatitude()` |  |
| `java.util.Locale getLocale()` |  |
| `String getLocality()` |  |
| `double getLongitude()` |  |
| `int getMaxAddressLineIndex()` |  |
| `String getPhone()` |  |
| `String getPostalCode()` |  |
| `String getPremises()` |  |
| `String getSubAdminArea()` |  |
| `String getSubLocality()` |  |
| `String getSubThoroughfare()` |  |
| `String getThoroughfare()` |  |
| `String getUrl()` |  |
| `boolean hasLatitude()` |  |
| `boolean hasLongitude()` |  |
| `void setAddressLine(int, String)` |  |
| `void setAdminArea(String)` |  |
| `void setCountryCode(String)` |  |
| `void setCountryName(String)` |  |
| `void setExtras(android.os.Bundle)` |  |
| `void setFeatureName(String)` |  |
| `void setLatitude(double)` |  |
| `void setLocality(String)` |  |
| `void setLongitude(double)` |  |
| `void setPhone(String)` |  |
| `void setPostalCode(String)` |  |
| `void setPremises(String)` |  |
| `void setSubAdminArea(String)` |  |
| `void setSubLocality(String)` |  |
| `void setSubThoroughfare(String)` |  |
| `void setThoroughfare(String)` |  |
| `void setUrl(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class Criteria`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Criteria()` |  |
| `Criteria(android.location.Criteria)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ACCURACY_COARSE = 2` |  |
| `static final int ACCURACY_FINE = 1` |  |
| `static final int ACCURACY_HIGH = 3` |  |
| `static final int ACCURACY_LOW = 1` |  |
| `static final int ACCURACY_MEDIUM = 2` |  |
| `static final int NO_REQUIREMENT = 0` |  |
| `static final int POWER_HIGH = 3` |  |
| `static final int POWER_LOW = 1` |  |
| `static final int POWER_MEDIUM = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAccuracy()` |  |
| `int getBearingAccuracy()` |  |
| `int getHorizontalAccuracy()` |  |
| `int getPowerRequirement()` |  |
| `int getSpeedAccuracy()` |  |
| `int getVerticalAccuracy()` |  |
| `boolean isAltitudeRequired()` |  |
| `boolean isBearingRequired()` |  |
| `boolean isCostAllowed()` |  |
| `boolean isSpeedRequired()` |  |
| `void setAccuracy(int)` |  |
| `void setAltitudeRequired(boolean)` |  |
| `void setBearingAccuracy(int)` |  |
| `void setBearingRequired(boolean)` |  |
| `void setCostAllowed(boolean)` |  |
| `void setHorizontalAccuracy(int)` |  |
| `void setPowerRequirement(int)` |  |
| `void setSpeedAccuracy(int)` |  |
| `void setSpeedRequired(boolean)` |  |
| `void setVerticalAccuracy(int)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final Geocoder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Geocoder(android.content.Context, java.util.Locale)` |  |
| `Geocoder(android.content.Context)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.util.List<android.location.Address> getFromLocation(double, double, int) throws java.io.IOException` |  |
| `java.util.List<android.location.Address> getFromLocationName(String, int) throws java.io.IOException` |  |
| `java.util.List<android.location.Address> getFromLocationName(String, int, double, double, double, double) throws java.io.IOException` |  |
| `static boolean isPresent()` |  |

---

### `class final GnssAntennaInfo`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static GnssAntennaInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GnssAntennaInfo.Builder()` |  |

---

### `interface static GnssAntennaInfo.Listener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onGnssAntennaInfoReceived(@NonNull java.util.List<android.location.GnssAntennaInfo>)` |  |

---

### `class static final GnssAntennaInfo.PhaseCenterOffset`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GnssAntennaInfo.PhaseCenterOffset(double, double, double, double, double, double)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class static final GnssAntennaInfo.SphericalCorrections`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GnssAntennaInfo.SphericalCorrections(@NonNull double[][], @NonNull double[][])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(@NonNull android.os.Parcel, int)` |  |

---

### `class final GnssCapabilities`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean hasGnssAntennaInfo()` |  |

---

### `class final GnssClock`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `double getBiasNanos()` |  |
| `double getDriftNanosPerSecond()` |  |
| `long getElapsedRealtimeNanos()` |  |
| `long getFullBiasNanos()` |  |
| `int getHardwareClockDiscontinuityCount()` |  |
| `int getLeapSecond()` |  |
| `int getReferenceConstellationTypeForIsb()` |  |
| `long getTimeNanos()` |  |
| `boolean hasBiasNanos()` |  |
| `boolean hasBiasUncertaintyNanos()` |  |
| `boolean hasDriftNanosPerSecond()` |  |
| `boolean hasDriftUncertaintyNanosPerSecond()` |  |
| `boolean hasElapsedRealtimeNanos()` |  |
| `boolean hasElapsedRealtimeUncertaintyNanos()` |  |
| `boolean hasFullBiasNanos()` |  |
| `boolean hasLeapSecond()` |  |
| `boolean hasReferenceCarrierFrequencyHzForIsb()` |  |
| `boolean hasReferenceCodeTypeForIsb()` |  |
| `boolean hasReferenceConstellationTypeForIsb()` |  |
| `boolean hasTimeUncertaintyNanos()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final GnssMeasurement`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ADR_STATE_CYCLE_SLIP = 4` |  |
| `static final int ADR_STATE_HALF_CYCLE_REPORTED = 16` |  |
| `static final int ADR_STATE_HALF_CYCLE_RESOLVED = 8` |  |
| `static final int ADR_STATE_RESET = 2` |  |
| `static final int ADR_STATE_UNKNOWN = 0` |  |
| `static final int ADR_STATE_VALID = 1` |  |
| `static final int MULTIPATH_INDICATOR_DETECTED = 1` |  |
| `static final int MULTIPATH_INDICATOR_NOT_DETECTED = 2` |  |
| `static final int MULTIPATH_INDICATOR_UNKNOWN = 0` |  |
| `static final int STATE_2ND_CODE_LOCK = 65536` |  |
| `static final int STATE_BDS_D2_BIT_SYNC = 256` |  |
| `static final int STATE_BDS_D2_SUBFRAME_SYNC = 512` |  |
| `static final int STATE_BIT_SYNC = 2` |  |
| `static final int STATE_CODE_LOCK = 1` |  |
| `static final int STATE_GAL_E1BC_CODE_LOCK = 1024` |  |
| `static final int STATE_GAL_E1B_PAGE_SYNC = 4096` |  |
| `static final int STATE_GAL_E1C_2ND_CODE_LOCK = 2048` |  |
| `static final int STATE_GLO_STRING_SYNC = 64` |  |
| `static final int STATE_GLO_TOD_DECODED = 128` |  |
| `static final int STATE_GLO_TOD_KNOWN = 32768` |  |
| `static final int STATE_MSEC_AMBIGUOUS = 16` |  |
| `static final int STATE_SBAS_SYNC = 8192` |  |
| `static final int STATE_SUBFRAME_SYNC = 4` |  |
| `static final int STATE_SYMBOL_SYNC = 32` |  |
| `static final int STATE_TOW_DECODED = 8` |  |
| `static final int STATE_TOW_KNOWN = 16384` |  |
| `static final int STATE_UNKNOWN = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `double getAccumulatedDeltaRangeMeters()` |  |
| `int getAccumulatedDeltaRangeState()` |  |
| `double getAccumulatedDeltaRangeUncertaintyMeters()` |  |
| `double getAutomaticGainControlLevelDb()` |  |
| `float getCarrierFrequencyHz()` |  |
| `int getConstellationType()` |  |
| `double getFullInterSignalBiasNanos()` |  |
| `int getMultipathIndicator()` |  |
| `double getPseudorangeRateMetersPerSecond()` |  |
| `double getPseudorangeRateUncertaintyMetersPerSecond()` |  |
| `long getReceivedSvTimeNanos()` |  |
| `long getReceivedSvTimeUncertaintyNanos()` |  |
| `double getSatelliteInterSignalBiasNanos()` |  |
| `double getSnrInDb()` |  |
| `int getState()` |  |
| `int getSvid()` |  |
| `double getTimeOffsetNanos()` |  |
| `boolean hasAutomaticGainControlLevelDb()` |  |
| `boolean hasBasebandCn0DbHz()` |  |
| `boolean hasCarrierFrequencyHz()` |  |
| `boolean hasCodeType()` |  |
| `boolean hasFullInterSignalBiasNanos()` |  |
| `boolean hasFullInterSignalBiasUncertaintyNanos()` |  |
| `boolean hasSatelliteInterSignalBiasNanos()` |  |
| `boolean hasSatelliteInterSignalBiasUncertaintyNanos()` |  |
| `boolean hasSnrInDb()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final GnssMeasurementsEvent`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GnssMeasurementsEvent.Callback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int STATUS_LOCATION_DISABLED = 2` |  |
| `static final int STATUS_NOT_ALLOWED = 3` |  |
| `static final int STATUS_NOT_SUPPORTED = 0` |  |
| `static final int STATUS_READY = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |
| `void onGnssMeasurementsReceived(android.location.GnssMeasurementsEvent)` |  |
| `void onStatusChanged(int)` |  |

---

### `class final GnssNavigationMessage`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GnssNavigationMessage.Callback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int STATUS_PARITY_PASSED = 1` |  |
| `static final int STATUS_PARITY_REBUILT = 2` |  |
| `static final int STATUS_UNKNOWN = 0` |  |
| `static final int TYPE_BDS_CNAV1 = 1283` |  |
| `static final int TYPE_BDS_CNAV2 = 1284` |  |
| `static final int TYPE_BDS_D1 = 1281` |  |
| `static final int TYPE_BDS_D2 = 1282` |  |
| `static final int TYPE_GAL_F = 1538` |  |
| `static final int TYPE_GAL_I = 1537` |  |
| `static final int TYPE_GLO_L1CA = 769` |  |
| `static final int TYPE_GPS_CNAV2 = 260` |  |
| `static final int TYPE_GPS_L1CA = 257` |  |
| `static final int TYPE_GPS_L2CNAV = 258` |  |
| `static final int TYPE_GPS_L5CNAV = 259` |  |
| `static final int TYPE_IRN_L5CA = 1793` |  |
| `static final int TYPE_QZS_L1CA = 1025` |  |
| `static final int TYPE_SBS = 513` |  |
| `static final int TYPE_UNKNOWN = 0` |  |
| `static final int STATUS_LOCATION_DISABLED = 2` |  |
| `static final int STATUS_NOT_SUPPORTED = 0` |  |
| `static final int STATUS_READY = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getMessageId()` |  |
| `int getStatus()` |  |
| `int getSubmessageId()` |  |
| `int getSvid()` |  |
| `int getType()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |
| `void onGnssNavigationMessageReceived(android.location.GnssNavigationMessage)` |  |
| `void onStatusChanged(int)` |  |

---

### `class final GnssStatus`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CONSTELLATION_BEIDOU = 5` |  |
| `static final int CONSTELLATION_GALILEO = 6` |  |
| `static final int CONSTELLATION_GLONASS = 3` |  |
| `static final int CONSTELLATION_GPS = 1` |  |
| `static final int CONSTELLATION_IRNSS = 7` |  |
| `static final int CONSTELLATION_QZSS = 4` |  |
| `static final int CONSTELLATION_SBAS = 2` |  |
| `static final int CONSTELLATION_UNKNOWN = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getConstellationType(@IntRange(from=0) int)` |  |
| `boolean hasAlmanacData(@IntRange(from=0) int)` |  |
| `boolean hasBasebandCn0DbHz(@IntRange(from=0) int)` |  |
| `boolean hasCarrierFrequencyHz(@IntRange(from=0) int)` |  |
| `boolean hasEphemerisData(@IntRange(from=0) int)` |  |
| `boolean usedInFix(@IntRange(from=0) int)` |  |

---

### `class static final GnssStatus.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `GnssStatus.Builder()` |  |
| `GnssStatus.Callback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onFirstFix(int)` |  |
| `void onSatelliteStatusChanged(@NonNull android.location.GnssStatus)` |  |
| `void onStarted()` |  |
| `void onStopped()` |  |

---

### `class final GpsSatellite` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final GpsStatus` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static GpsStatus.Listener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static GpsStatus.NmeaListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class Location`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Location(String)` |  |
| `Location(android.location.Location)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FORMAT_DEGREES = 0` |  |
| `static final int FORMAT_MINUTES = 1` |  |
| `static final int FORMAT_SECONDS = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float bearingTo(android.location.Location)` |  |
| `static String convert(double, int)` |  |
| `static double convert(String)` |  |
| `int describeContents()` |  |
| `static void distanceBetween(double, double, double, double, float[])` |  |
| `float distanceTo(android.location.Location)` |  |
| `void dump(android.util.Printer, String)` |  |
| `float getAccuracy()` |  |
| `double getAltitude()` |  |
| `float getBearing()` |  |
| `float getBearingAccuracyDegrees()` |  |
| `long getElapsedRealtimeNanos()` |  |
| `double getElapsedRealtimeUncertaintyNanos()` |  |
| `android.os.Bundle getExtras()` |  |
| `double getLatitude()` |  |
| `double getLongitude()` |  |
| `String getProvider()` |  |
| `float getSpeed()` |  |
| `float getSpeedAccuracyMetersPerSecond()` |  |
| `long getTime()` |  |
| `float getVerticalAccuracyMeters()` |  |
| `boolean hasAccuracy()` |  |
| `boolean hasAltitude()` |  |
| `boolean hasBearing()` |  |
| `boolean hasBearingAccuracy()` |  |
| `boolean hasElapsedRealtimeUncertaintyNanos()` |  |
| `boolean hasSpeed()` |  |
| `boolean hasSpeedAccuracy()` |  |
| `boolean hasVerticalAccuracy()` |  |
| `boolean isFromMockProvider()` |  |
| `void reset()` |  |
| `void set(android.location.Location)` |  |
| `void setAccuracy(float)` |  |
| `void setAltitude(double)` |  |
| `void setBearing(float)` |  |
| `void setBearingAccuracyDegrees(float)` |  |
| `void setElapsedRealtimeNanos(long)` |  |
| `void setElapsedRealtimeUncertaintyNanos(double)` |  |
| `void setExtras(android.os.Bundle)` |  |
| `void setLatitude(double)` |  |
| `void setLongitude(double)` |  |
| `void setProvider(String)` |  |
| `void setSpeed(float)` |  |
| `void setSpeedAccuracyMetersPerSecond(float)` |  |
| `void setTime(long)` |  |
| `void setVerticalAccuracyMeters(float)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `interface LocationListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onLocationChanged(@NonNull android.location.Location)` |  |
| `default void onProviderDisabled(@NonNull String)` |  |
| `default void onProviderEnabled(@NonNull String)` |  |

---

### `class LocationManager`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_LOCATION_ENABLED = "android.location.extra.LOCATION_ENABLED"` |  |
| `static final String EXTRA_PROVIDER_ENABLED = "android.location.extra.PROVIDER_ENABLED"` |  |
| `static final String EXTRA_PROVIDER_NAME = "android.location.extra.PROVIDER_NAME"` |  |
| `static final String GPS_PROVIDER = "gps"` |  |
| `static final String KEY_LOCATION_CHANGED = "location"` |  |
| `static final String KEY_PROVIDER_ENABLED = "providerEnabled"` |  |
| `static final String KEY_PROXIMITY_ENTERING = "entering"` |  |
| `static final String MODE_CHANGED_ACTION = "android.location.MODE_CHANGED"` |  |
| `static final String NETWORK_PROVIDER = "network"` |  |
| `static final String PASSIVE_PROVIDER = "passive"` |  |
| `static final String PROVIDERS_CHANGED_ACTION = "android.location.PROVIDERS_CHANGED"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addTestProvider(@NonNull String, boolean, boolean, boolean, boolean, boolean, boolean, boolean, int, int)` |  |
| `int getGnssYearOfHardware()` |  |
| `boolean isLocationEnabled()` |  |
| `boolean isProviderEnabled(@NonNull String)` |  |
| `void removeNmeaListener(@NonNull android.location.OnNmeaMessageListener)` |  |
| `void removeTestProvider(@NonNull String)` |  |
| `void removeUpdates(@NonNull android.app.PendingIntent)` |  |
| `boolean sendExtraCommand(@NonNull String, @NonNull String, @Nullable android.os.Bundle)` |  |
| `void setTestProviderEnabled(@NonNull String, boolean)` |  |
| `void setTestProviderLocation(@NonNull String, @NonNull android.location.Location)` |  |
| `void unregisterAntennaInfoListener(@NonNull android.location.GnssAntennaInfo.Listener)` |  |
| `void unregisterGnssMeasurementsCallback(@NonNull android.location.GnssMeasurementsEvent.Callback)` |  |
| `void unregisterGnssNavigationMessageCallback(@NonNull android.location.GnssNavigationMessage.Callback)` |  |
| `void unregisterGnssStatusCallback(@NonNull android.location.GnssStatus.Callback)` |  |

---

### `class LocationProvider`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getAccuracy()` |  |
| `String getName()` |  |
| `int getPowerRequirement()` |  |
| `boolean hasMonetaryCost()` |  |
| `boolean meetsCriteria(android.location.Criteria)` |  |
| `boolean requiresCell()` |  |
| `boolean requiresNetwork()` |  |
| `boolean requiresSatellite()` |  |
| `boolean supportsAltitude()` |  |
| `boolean supportsBearing()` |  |
| `boolean supportsSpeed()` |  |

---

### `interface OnNmeaMessageListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onNmeaMessage(String, long)` |  |

---

### `class abstract SettingInjectorService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SettingInjectorService(String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_INJECTED_SETTING_CHANGED = "android.location.InjectedSettingChanged"` |  |
| `static final String ACTION_SERVICE_INTENT = "android.location.SettingInjectorService"` |  |
| `static final String ATTRIBUTES_NAME = "injected-location-setting"` |  |
| `static final String META_DATA_NAME = "android.location.SettingInjectorService"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract boolean onGetEnabled()` |  |
| `abstract String onGetSummary()` |  |
| `final void onStart(android.content.Intent, int)` |  |
| `final int onStartCommand(android.content.Intent, int, int)` |  |
| `static final void refreshSettings(@NonNull android.content.Context)` |  |

---

## Package: `android.mtp`

### `class final MtpConstants`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MtpConstants()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ASSOCIATION_TYPE_GENERIC_FOLDER = 1` |  |
| `static final int FORMAT_3GP_CONTAINER = 47492` |  |
| `static final int FORMAT_AAC = 47363` |  |
| `static final int FORMAT_ABSTRACT_AUDIO_ALBUM = 47619` |  |
| `static final int FORMAT_ABSTRACT_AUDIO_PLAYLIST = 47625` |  |
| `static final int FORMAT_ABSTRACT_AV_PLAYLIST = 47621` |  |
| `static final int FORMAT_ABSTRACT_DOCUMENT = 47745` |  |
| `static final int FORMAT_ABSTRACT_IMAGE_ALBUM = 47618` |  |
| `static final int FORMAT_ABSTRACT_MEDIACAST = 47627` |  |
| `static final int FORMAT_ABSTRACT_MULTIMEDIA_ALBUM = 47617` |  |
| `static final int FORMAT_ABSTRACT_VIDEO_ALBUM = 47620` |  |
| `static final int FORMAT_ABSTRACT_VIDEO_PLAYLIST = 47626` |  |
| `static final int FORMAT_AIFF = 12295` |  |
| `static final int FORMAT_ASF = 12300` |  |
| `static final int FORMAT_ASSOCIATION = 12289` |  |
| `static final int FORMAT_ASX_PLAYLIST = 47635` |  |
| `static final int FORMAT_AUDIBLE = 47364` |  |
| `static final int FORMAT_AVI = 12298` |  |
| `static final int FORMAT_BMP = 14340` |  |
| `static final int FORMAT_DEFINED = 14336` |  |
| `static final int FORMAT_DNG = 14353` |  |
| `static final int FORMAT_DPOF = 12294` |  |
| `static final int FORMAT_EXECUTABLE = 12291` |  |
| `static final int FORMAT_EXIF_JPEG = 14337` |  |
| `static final int FORMAT_FLAC = 47366` |  |
| `static final int FORMAT_GIF = 14343` |  |
| `static final int FORMAT_HTML = 12293` |  |
| `static final int FORMAT_JFIF = 14344` |  |
| `static final int FORMAT_JP2 = 14351` |  |
| `static final int FORMAT_JPX = 14352` |  |
| `static final int FORMAT_M3U_PLAYLIST = 47633` |  |
| `static final int FORMAT_MP2 = 47491` |  |
| `static final int FORMAT_MP3 = 12297` |  |
| `static final int FORMAT_MP4_CONTAINER = 47490` |  |
| `static final int FORMAT_MPEG = 12299` |  |
| `static final int FORMAT_MPL_PLAYLIST = 47634` |  |
| `static final int FORMAT_MS_EXCEL_SPREADSHEET = 47749` |  |
| `static final int FORMAT_MS_POWERPOINT_PRESENTATION = 47750` |  |
| `static final int FORMAT_MS_WORD_DOCUMENT = 47747` |  |
| `static final int FORMAT_OGG = 47362` |  |
| `static final int FORMAT_PICT = 14346` |  |
| `static final int FORMAT_PLS_PLAYLIST = 47636` |  |
| `static final int FORMAT_PNG = 14347` |  |
| `static final int FORMAT_SCRIPT = 12290` |  |
| `static final int FORMAT_TEXT = 12292` |  |
| `static final int FORMAT_TIFF = 14349` |  |
| `static final int FORMAT_TIFF_EP = 14338` |  |
| `static final int FORMAT_UNDEFINED = 12288` |  |
| `static final int FORMAT_UNDEFINED_AUDIO = 47360` |  |
| `static final int FORMAT_UNDEFINED_COLLECTION = 47616` |  |
| `static final int FORMAT_UNDEFINED_DOCUMENT = 47744` |  |
| `static final int FORMAT_UNDEFINED_FIRMWARE = 47106` |  |
| `static final int FORMAT_UNDEFINED_VIDEO = 47488` |  |
| `static final int FORMAT_WAV = 12296` |  |
| `static final int FORMAT_WINDOWS_IMAGE_FORMAT = 47233` |  |
| `static final int FORMAT_WMA = 47361` |  |
| `static final int FORMAT_WMV = 47489` |  |
| `static final int FORMAT_WPL_PLAYLIST = 47632` |  |
| `static final int FORMAT_XML_DOCUMENT = 47746` |  |
| `static final int OPERATION_CLOSE_SESSION = 4099` |  |
| `static final int OPERATION_COPY_OBJECT = 4122` |  |
| `static final int OPERATION_DELETE_OBJECT = 4107` |  |
| `static final int OPERATION_FORMAT_STORE = 4111` |  |
| `static final int OPERATION_GET_DEVICE_INFO = 4097` |  |
| `static final int OPERATION_GET_DEVICE_PROP_DESC = 4116` |  |
| `static final int OPERATION_GET_DEVICE_PROP_VALUE = 4117` |  |
| `static final int OPERATION_GET_NUM_OBJECTS = 4102` |  |
| `static final int OPERATION_GET_OBJECT = 4105` |  |
| `static final int OPERATION_GET_OBJECT_HANDLES = 4103` |  |
| `static final int OPERATION_GET_OBJECT_INFO = 4104` |  |
| `static final int OPERATION_GET_OBJECT_PROPS_SUPPORTED = 38913` |  |
| `static final int OPERATION_GET_OBJECT_PROP_DESC = 38914` |  |
| `static final int OPERATION_GET_OBJECT_PROP_VALUE = 38915` |  |
| `static final int OPERATION_GET_OBJECT_REFERENCES = 38928` |  |
| `static final int OPERATION_GET_PARTIAL_OBJECT = 4123` |  |
| `static final int OPERATION_GET_PARTIAL_OBJECT_64 = 38337` |  |
| `static final int OPERATION_GET_STORAGE_INFO = 4101` |  |
| `static final int OPERATION_GET_STORAGE_I_DS = 4100` |  |
| `static final int OPERATION_GET_THUMB = 4106` |  |
| `static final int OPERATION_INITIATE_CAPTURE = 4110` |  |
| `static final int OPERATION_INITIATE_OPEN_CAPTURE = 4124` |  |
| `static final int OPERATION_MOVE_OBJECT = 4121` |  |
| `static final int OPERATION_OPEN_SESSION = 4098` |  |
| `static final int OPERATION_POWER_DOWN = 4115` |  |
| `static final int OPERATION_RESET_DEVICE = 4112` |  |
| `static final int OPERATION_RESET_DEVICE_PROP_VALUE = 4119` |  |
| `static final int OPERATION_SELF_TEST = 4113` |  |
| `static final int OPERATION_SEND_OBJECT = 4109` |  |
| `static final int OPERATION_SEND_OBJECT_INFO = 4108` |  |
| `static final int OPERATION_SET_DEVICE_PROP_VALUE = 4118` |  |
| `static final int OPERATION_SET_OBJECT_PROP_VALUE = 38916` |  |
| `static final int OPERATION_SET_OBJECT_PROTECTION = 4114` |  |
| `static final int OPERATION_SET_OBJECT_REFERENCES = 38929` |  |
| `static final int OPERATION_SKIP = 38944` |  |
| `static final int OPERATION_TERMINATE_OPEN_CAPTURE = 4120` |  |
| `static final int PROTECTION_STATUS_NONE = 0` |  |
| `static final int PROTECTION_STATUS_NON_TRANSFERABLE_DATA = 32771` |  |
| `static final int PROTECTION_STATUS_READ_ONLY = 32769` |  |
| `static final int PROTECTION_STATUS_READ_ONLY_DATA = 32770` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static boolean isAbstractObject(int)` |  |

---

### `class final MtpDevice`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MtpDevice(@NonNull android.hardware.usb.UsbDevice)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `boolean deleteObject(int)` |  |
| `int getDeviceId()` |  |
| `long getParent(int)` |  |
| `long getPartialObject(int, long, long, @NonNull byte[]) throws java.io.IOException` |  |
| `long getPartialObject64(int, long, long, @NonNull byte[]) throws java.io.IOException` |  |
| `long getStorageId(int)` |  |
| `boolean importFile(int, @NonNull String)` |  |
| `boolean importFile(int, @NonNull android.os.ParcelFileDescriptor)` |  |
| `boolean open(@NonNull android.hardware.usb.UsbDeviceConnection)` |  |
| `boolean sendObject(int, long, @NonNull android.os.ParcelFileDescriptor)` |  |

---

### `class MtpDeviceInfo`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isEventSupported(int)` |  |
| `boolean isOperationSupported(int)` |  |

---

### `class MtpEvent`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int EVENT_CANCEL_TRANSACTION = 16385` |  |
| `static final int EVENT_CAPTURE_COMPLETE = 16397` |  |
| `static final int EVENT_DEVICE_INFO_CHANGED = 16392` |  |
| `static final int EVENT_DEVICE_PROP_CHANGED = 16390` |  |
| `static final int EVENT_DEVICE_RESET = 16395` |  |
| `static final int EVENT_OBJECT_ADDED = 16386` |  |
| `static final int EVENT_OBJECT_INFO_CHANGED = 16391` |  |
| `static final int EVENT_OBJECT_PROP_CHANGED = 51201` |  |
| `static final int EVENT_OBJECT_PROP_DESC_CHANGED = 51202` |  |
| `static final int EVENT_OBJECT_REFERENCES_CHANGED = 51203` |  |
| `static final int EVENT_OBJECT_REMOVED = 16387` |  |
| `static final int EVENT_REQUEST_OBJECT_TRANSFER = 16393` |  |
| `static final int EVENT_STORAGE_INFO_CHANGED = 16396` |  |
| `static final int EVENT_STORE_ADDED = 16388` |  |
| `static final int EVENT_STORE_FULL = 16394` |  |
| `static final int EVENT_STORE_REMOVED = 16389` |  |
| `static final int EVENT_UNDEFINED = 16384` |  |
| `static final int EVENT_UNREPORTED_STATUS = 16398` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getDevicePropCode()` |  |
| `int getEventCode()` |  |
| `int getObjectFormatCode()` |  |
| `int getObjectHandle()` |  |
| `int getObjectPropCode()` |  |
| `int getParameter1()` |  |
| `int getParameter2()` |  |
| `int getParameter3()` |  |
| `int getStorageId()` |  |
| `int getTransactionId()` |  |

---

### `class final MtpObjectInfo`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getAssociationDesc()` |  |
| `int getAssociationType()` |  |
| `int getCompressedSize()` |  |
| `long getCompressedSizeLong()` |  |
| `long getDateCreated()` |  |
| `long getDateModified()` |  |
| `int getFormat()` |  |
| `int getImagePixDepth()` |  |
| `long getImagePixDepthLong()` |  |
| `int getImagePixHeight()` |  |
| `long getImagePixHeightLong()` |  |
| `int getImagePixWidth()` |  |
| `long getImagePixWidthLong()` |  |
| `int getObjectHandle()` |  |
| `int getParent()` |  |
| `int getProtectionStatus()` |  |
| `int getSequenceNumber()` |  |
| `long getSequenceNumberLong()` |  |
| `int getStorageId()` |  |
| `int getThumbCompressedSize()` |  |
| `long getThumbCompressedSizeLong()` |  |
| `int getThumbFormat()` |  |
| `int getThumbPixHeight()` |  |
| `long getThumbPixHeightLong()` |  |
| `int getThumbPixWidth()` |  |
| `long getThumbPixWidthLong()` |  |

---

### `class static MtpObjectInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `MtpObjectInfo.Builder()` |  |
| `MtpObjectInfo.Builder(android.mtp.MtpObjectInfo)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.mtp.MtpObjectInfo build()` |  |
| `android.mtp.MtpObjectInfo.Builder setAssociationDesc(int)` |  |
| `android.mtp.MtpObjectInfo.Builder setAssociationType(int)` |  |
| `android.mtp.MtpObjectInfo.Builder setCompressedSize(long)` |  |
| `android.mtp.MtpObjectInfo.Builder setDateCreated(long)` |  |
| `android.mtp.MtpObjectInfo.Builder setDateModified(long)` |  |
| `android.mtp.MtpObjectInfo.Builder setFormat(int)` |  |
| `android.mtp.MtpObjectInfo.Builder setImagePixDepth(long)` |  |
| `android.mtp.MtpObjectInfo.Builder setImagePixHeight(long)` |  |
| `android.mtp.MtpObjectInfo.Builder setImagePixWidth(long)` |  |
| `android.mtp.MtpObjectInfo.Builder setKeywords(@NonNull String)` |  |
| `android.mtp.MtpObjectInfo.Builder setName(@NonNull String)` |  |
| `android.mtp.MtpObjectInfo.Builder setObjectHandle(int)` |  |
| `android.mtp.MtpObjectInfo.Builder setParent(int)` |  |
| `android.mtp.MtpObjectInfo.Builder setProtectionStatus(int)` |  |
| `android.mtp.MtpObjectInfo.Builder setSequenceNumber(long)` |  |
| `android.mtp.MtpObjectInfo.Builder setStorageId(int)` |  |
| `android.mtp.MtpObjectInfo.Builder setThumbCompressedSize(long)` |  |
| `android.mtp.MtpObjectInfo.Builder setThumbFormat(int)` |  |
| `android.mtp.MtpObjectInfo.Builder setThumbPixHeight(long)` |  |
| `android.mtp.MtpObjectInfo.Builder setThumbPixWidth(long)` |  |

---

### `class final MtpStorageInfo`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getFreeSpace()` |  |
| `long getMaxCapacity()` |  |
| `int getStorageId()` |  |

---

## Package: `android.nfc`

### `class FormatException`

- **Extends:** `java.lang.Exception`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `FormatException()` |  |
| `FormatException(String)` |  |
| `FormatException(String, Throwable)` |  |

---

### `class final NdefMessage`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NdefMessage(byte[]) throws android.nfc.FormatException` |  |
| `NdefMessage(android.nfc.NdefRecord, android.nfc.NdefRecord...)` |  |
| `NdefMessage(android.nfc.NdefRecord[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getByteArrayLength()` |  |
| `android.nfc.NdefRecord[] getRecords()` |  |
| `byte[] toByteArray()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final NdefRecord`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `NdefRecord(short, byte[], byte[], byte[])` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final byte[] RTD_ALTERNATIVE_CARRIER` |  |
| `static final byte[] RTD_HANDOVER_CARRIER` |  |
| `static final byte[] RTD_HANDOVER_REQUEST` |  |
| `static final byte[] RTD_HANDOVER_SELECT` |  |
| `static final byte[] RTD_SMART_POSTER` |  |
| `static final byte[] RTD_TEXT` |  |
| `static final byte[] RTD_URI` |  |
| `static final short TNF_ABSOLUTE_URI = 3` |  |
| `static final short TNF_EMPTY = 0` |  |
| `static final short TNF_EXTERNAL_TYPE = 4` |  |
| `static final short TNF_MIME_MEDIA = 2` |  |
| `static final short TNF_UNCHANGED = 6` |  |
| `static final short TNF_UNKNOWN = 5` |  |
| `static final short TNF_WELL_KNOWN = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.nfc.NdefRecord createApplicationRecord(String)` |  |
| `static android.nfc.NdefRecord createExternal(String, String, byte[])` |  |
| `static android.nfc.NdefRecord createMime(String, byte[])` |  |
| `static android.nfc.NdefRecord createTextRecord(String, String)` |  |
| `static android.nfc.NdefRecord createUri(android.net.Uri)` |  |
| `static android.nfc.NdefRecord createUri(String)` |  |
| `int describeContents()` |  |
| `byte[] getId()` |  |
| `byte[] getPayload()` |  |
| `short getTnf()` |  |
| `byte[] getType()` |  |
| `String toMimeType()` |  |
| `android.net.Uri toUri()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final NfcAdapter`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_ADAPTER_STATE_CHANGED = "android.nfc.action.ADAPTER_STATE_CHANGED"` |  |
| `static final String ACTION_NDEF_DISCOVERED = "android.nfc.action.NDEF_DISCOVERED"` |  |
| `static final String ACTION_TAG_DISCOVERED = "android.nfc.action.TAG_DISCOVERED"` |  |
| `static final String ACTION_TECH_DISCOVERED = "android.nfc.action.TECH_DISCOVERED"` |  |
| `static final String EXTRA_ADAPTER_STATE = "android.nfc.extra.ADAPTER_STATE"` |  |
| `static final String EXTRA_AID = "android.nfc.extra.AID"` |  |
| `static final String EXTRA_DATA = "android.nfc.extra.DATA"` |  |
| `static final String EXTRA_ID = "android.nfc.extra.ID"` |  |
| `static final String EXTRA_NDEF_MESSAGES = "android.nfc.extra.NDEF_MESSAGES"` |  |
| `static final String EXTRA_PREFERRED_PAYMENT_CHANGED_REASON = "android.nfc.extra.PREFERRED_PAYMENT_CHANGED_REASON"` |  |
| `static final String EXTRA_READER_PRESENCE_CHECK_DELAY = "presence"` |  |
| `static final String EXTRA_SECURE_ELEMENT_NAME = "android.nfc.extra.SECURE_ELEMENT_NAME"` |  |
| `static final String EXTRA_TAG = "android.nfc.extra.TAG"` |  |
| `static final int FLAG_READER_NFC_A = 1` |  |
| `static final int FLAG_READER_NFC_B = 2` |  |
| `static final int FLAG_READER_NFC_BARCODE = 16` |  |
| `static final int FLAG_READER_NFC_F = 4` |  |
| `static final int FLAG_READER_NFC_V = 8` |  |
| `static final int FLAG_READER_NO_PLATFORM_SOUNDS = 256` |  |
| `static final int FLAG_READER_SKIP_NDEF_CHECK = 128` |  |
| `static final int PREFERRED_PAYMENT_CHANGED = 2` |  |
| `static final int PREFERRED_PAYMENT_LOADED = 1` |  |
| `static final int PREFERRED_PAYMENT_UPDATED = 3` |  |
| `static final int STATE_OFF = 1` |  |
| `static final int STATE_ON = 3` |  |
| `static final int STATE_TURNING_OFF = 4` |  |
| `static final int STATE_TURNING_ON = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void disableForegroundDispatch(android.app.Activity)` |  |
| `void disableReaderMode(android.app.Activity)` |  |
| `void enableForegroundDispatch(android.app.Activity, android.app.PendingIntent, android.content.IntentFilter[], String[][])` |  |
| `void enableReaderMode(android.app.Activity, android.nfc.NfcAdapter.ReaderCallback, int, android.os.Bundle)` |  |
| `static android.nfc.NfcAdapter getDefaultAdapter(android.content.Context)` |  |
| `boolean ignore(android.nfc.Tag, int, android.nfc.NfcAdapter.OnTagRemovedListener, android.os.Handler)` |  |
| `boolean isEnabled()` |  |
| `boolean isSecureNfcEnabled()` |  |
| `boolean isSecureNfcSupported()` |  |

---

### `interface static NfcAdapter.CreateBeamUrisCallback` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static NfcAdapter.CreateNdefMessageCallback` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static NfcAdapter.OnNdefPushCompleteCallback` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static NfcAdapter.OnTagRemovedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onTagRemoved()` |  |

---

### `interface static NfcAdapter.ReaderCallback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onTagDiscovered(android.nfc.Tag)` |  |

---

### `class final NfcEvent`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `final android.nfc.NfcAdapter nfcAdapter` |  |
| `final int peerLlcpMajorVersion` |  |
| `final int peerLlcpMinorVersion` |  |

---

### `class final NfcManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.nfc.NfcAdapter getDefaultAdapter()` |  |

---

### `class final Tag`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `byte[] getId()` |  |
| `String[] getTechList()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class TagLostException`

- **Extends:** `java.io.IOException`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TagLostException()` |  |
| `TagLostException(String)` |  |

---

## Package: `android.nfc.cardemulation`

### `class final CardEmulation`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CHANGE_DEFAULT = "android.nfc.cardemulation.action.ACTION_CHANGE_DEFAULT"` |  |
| `static final String CATEGORY_OTHER = "other"` |  |
| `static final String CATEGORY_PAYMENT = "payment"` |  |
| `static final String EXTRA_CATEGORY = "category"` |  |
| `static final String EXTRA_SERVICE_COMPONENT = "component"` |  |
| `static final int SELECTION_MODE_ALWAYS_ASK = 1` |  |
| `static final int SELECTION_MODE_ASK_IF_CONFLICT = 2` |  |
| `static final int SELECTION_MODE_PREFER_DEFAULT = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean categoryAllowsForegroundPreference(String)` |  |
| `java.util.List<java.lang.String> getAidsForService(android.content.ComponentName, String)` |  |
| `static android.nfc.cardemulation.CardEmulation getInstance(android.nfc.NfcAdapter)` |  |
| `int getSelectionModeForCategory(String)` |  |
| `boolean isDefaultServiceForAid(android.content.ComponentName, String)` |  |
| `boolean isDefaultServiceForCategory(android.content.ComponentName, String)` |  |
| `boolean registerAidsForService(android.content.ComponentName, String, java.util.List<java.lang.String>)` |  |
| `boolean removeAidsForService(android.content.ComponentName, String)` |  |
| `boolean setPreferredService(android.app.Activity, android.content.ComponentName)` |  |
| `boolean supportsAidPrefixRegistration()` |  |
| `boolean unsetPreferredService(android.app.Activity)` |  |

---

### `class abstract HostApduService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `HostApduService()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DEACTIVATION_DESELECTED = 1` |  |
| `static final int DEACTIVATION_LINK_LOSS = 0` |  |
| `static final String SERVICE_INTERFACE = "android.nfc.cardemulation.action.HOST_APDU_SERVICE"` |  |
| `static final String SERVICE_META_DATA = "android.nfc.cardemulation.host_apdu_service"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void notifyUnhandled()` |  |
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract void onDeactivated(int)` |  |
| `abstract byte[] processCommandApdu(byte[], android.os.Bundle)` |  |
| `final void sendResponseApdu(byte[])` |  |

---

### `class abstract HostNfcFService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `HostNfcFService()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DEACTIVATION_LINK_LOSS = 0` |  |
| `static final String SERVICE_INTERFACE = "android.nfc.cardemulation.action.HOST_NFCF_SERVICE"` |  |
| `static final String SERVICE_META_DATA = "android.nfc.cardemulation.host_nfcf_service"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract void onDeactivated(int)` |  |
| `abstract byte[] processNfcFPacket(byte[], android.os.Bundle)` |  |
| `final void sendResponsePacket(byte[])` |  |

---

### `class final NfcFCardEmulation`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean disableService(android.app.Activity) throws java.lang.RuntimeException` |  |
| `boolean enableService(android.app.Activity, android.content.ComponentName) throws java.lang.RuntimeException` |  |
| `static android.nfc.cardemulation.NfcFCardEmulation getInstance(android.nfc.NfcAdapter)` |  |
| `String getNfcid2ForService(android.content.ComponentName) throws java.lang.RuntimeException` |  |
| `String getSystemCodeForService(android.content.ComponentName) throws java.lang.RuntimeException` |  |
| `boolean registerSystemCodeForService(android.content.ComponentName, String) throws java.lang.RuntimeException` |  |
| `boolean setNfcid2ForService(android.content.ComponentName, String) throws java.lang.RuntimeException` |  |
| `boolean unregisterSystemCodeForService(android.content.ComponentName) throws java.lang.RuntimeException` |  |

---

### `class abstract OffHostApduService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `OffHostApduService()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.nfc.cardemulation.action.OFF_HOST_APDU_SERVICE"` |  |
| `static final String SERVICE_META_DATA = "android.nfc.cardemulation.off_host_apdu_service"` |  |

---

## Package: `android.nfc.tech`

### `class final IsoDep`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `static android.nfc.tech.IsoDep get(android.nfc.Tag)` |  |
| `byte[] getHiLayerResponse()` |  |
| `byte[] getHistoricalBytes()` |  |
| `int getMaxTransceiveLength()` |  |
| `android.nfc.Tag getTag()` |  |
| `int getTimeout()` |  |
| `boolean isConnected()` |  |
| `boolean isExtendedLengthApduSupported()` |  |
| `void setTimeout(int)` |  |
| `byte[] transceive(byte[]) throws java.io.IOException` |  |

---

### `class final MifareClassic`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int BLOCK_SIZE = 16` |  |
| `static final byte[] KEY_DEFAULT` |  |
| `static final byte[] KEY_MIFARE_APPLICATION_DIRECTORY` |  |
| `static final byte[] KEY_NFC_FORUM` |  |
| `static final int SIZE_1K = 1024` |  |
| `static final int SIZE_2K = 2048` |  |
| `static final int SIZE_4K = 4096` |  |
| `static final int SIZE_MINI = 320` |  |
| `static final int TYPE_CLASSIC = 0` |  |
| `static final int TYPE_PLUS = 1` |  |
| `static final int TYPE_PRO = 2` |  |
| `static final int TYPE_UNKNOWN = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean authenticateSectorWithKeyA(int, byte[]) throws java.io.IOException` |  |
| `boolean authenticateSectorWithKeyB(int, byte[]) throws java.io.IOException` |  |
| `int blockToSector(int)` |  |
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `void decrement(int, int) throws java.io.IOException` |  |
| `static android.nfc.tech.MifareClassic get(android.nfc.Tag)` |  |
| `int getBlockCount()` |  |
| `int getBlockCountInSector(int)` |  |
| `int getMaxTransceiveLength()` |  |
| `int getSectorCount()` |  |
| `int getSize()` |  |
| `android.nfc.Tag getTag()` |  |
| `int getTimeout()` |  |
| `int getType()` |  |
| `void increment(int, int) throws java.io.IOException` |  |
| `boolean isConnected()` |  |
| `byte[] readBlock(int) throws java.io.IOException` |  |
| `void restore(int) throws java.io.IOException` |  |
| `int sectorToBlock(int)` |  |
| `void setTimeout(int)` |  |
| `byte[] transceive(byte[]) throws java.io.IOException` |  |
| `void transfer(int) throws java.io.IOException` |  |
| `void writeBlock(int, byte[]) throws java.io.IOException` |  |

---

### `class final MifareUltralight`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int PAGE_SIZE = 4` |  |
| `static final int TYPE_ULTRALIGHT = 1` |  |
| `static final int TYPE_ULTRALIGHT_C = 2` |  |
| `static final int TYPE_UNKNOWN = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `static android.nfc.tech.MifareUltralight get(android.nfc.Tag)` |  |
| `int getMaxTransceiveLength()` |  |
| `android.nfc.Tag getTag()` |  |
| `int getTimeout()` |  |
| `int getType()` |  |
| `boolean isConnected()` |  |
| `byte[] readPages(int) throws java.io.IOException` |  |
| `void setTimeout(int)` |  |
| `byte[] transceive(byte[]) throws java.io.IOException` |  |
| `void writePage(int, byte[]) throws java.io.IOException` |  |

---

### `class final Ndef`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String MIFARE_CLASSIC = "com.nxp.ndef.mifareclassic"` |  |
| `static final String NFC_FORUM_TYPE_1 = "org.nfcforum.ndef.type1"` |  |
| `static final String NFC_FORUM_TYPE_2 = "org.nfcforum.ndef.type2"` |  |
| `static final String NFC_FORUM_TYPE_3 = "org.nfcforum.ndef.type3"` |  |
| `static final String NFC_FORUM_TYPE_4 = "org.nfcforum.ndef.type4"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean canMakeReadOnly()` |  |
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `static android.nfc.tech.Ndef get(android.nfc.Tag)` |  |
| `android.nfc.NdefMessage getCachedNdefMessage()` |  |
| `int getMaxSize()` |  |
| `android.nfc.NdefMessage getNdefMessage() throws android.nfc.FormatException, java.io.IOException` |  |
| `android.nfc.Tag getTag()` |  |
| `String getType()` |  |
| `boolean isConnected()` |  |
| `boolean isWritable()` |  |
| `boolean makeReadOnly() throws java.io.IOException` |  |
| `void writeNdefMessage(android.nfc.NdefMessage) throws android.nfc.FormatException, java.io.IOException` |  |

---

### `class final NdefFormatable`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `void format(android.nfc.NdefMessage) throws android.nfc.FormatException, java.io.IOException` |  |
| `void formatReadOnly(android.nfc.NdefMessage) throws android.nfc.FormatException, java.io.IOException` |  |
| `static android.nfc.tech.NdefFormatable get(android.nfc.Tag)` |  |
| `android.nfc.Tag getTag()` |  |
| `boolean isConnected()` |  |

---

### `class final NfcA`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `static android.nfc.tech.NfcA get(android.nfc.Tag)` |  |
| `byte[] getAtqa()` |  |
| `int getMaxTransceiveLength()` |  |
| `short getSak()` |  |
| `android.nfc.Tag getTag()` |  |
| `int getTimeout()` |  |
| `boolean isConnected()` |  |
| `void setTimeout(int)` |  |
| `byte[] transceive(byte[]) throws java.io.IOException` |  |

---

### `class final NfcB`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `static android.nfc.tech.NfcB get(android.nfc.Tag)` |  |
| `byte[] getApplicationData()` |  |
| `int getMaxTransceiveLength()` |  |
| `byte[] getProtocolInfo()` |  |
| `android.nfc.Tag getTag()` |  |
| `boolean isConnected()` |  |
| `byte[] transceive(byte[]) throws java.io.IOException` |  |

---

### `class final NfcBarcode`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int TYPE_KOVIO = 1` |  |
| `static final int TYPE_UNKNOWN = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `static android.nfc.tech.NfcBarcode get(android.nfc.Tag)` |  |
| `byte[] getBarcode()` |  |
| `android.nfc.Tag getTag()` |  |
| `int getType()` |  |
| `boolean isConnected()` |  |

---

### `class final NfcF`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `static android.nfc.tech.NfcF get(android.nfc.Tag)` |  |
| `byte[] getManufacturer()` |  |
| `int getMaxTransceiveLength()` |  |
| `byte[] getSystemCode()` |  |
| `android.nfc.Tag getTag()` |  |
| `int getTimeout()` |  |
| `boolean isConnected()` |  |
| `void setTimeout(int)` |  |
| `byte[] transceive(byte[]) throws java.io.IOException` |  |

---

### `class final NfcV`

- **Implements:** `android.nfc.tech.TagTechnology`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close() throws java.io.IOException` |  |
| `void connect() throws java.io.IOException` |  |
| `static android.nfc.tech.NfcV get(android.nfc.Tag)` |  |
| `byte getDsfId()` |  |
| `int getMaxTransceiveLength()` |  |
| `byte getResponseFlags()` |  |
| `android.nfc.Tag getTag()` |  |
| `boolean isConnected()` |  |
| `byte[] transceive(byte[]) throws java.io.IOException` |  |

---

### `interface TagTechnology`

- **Extends:** `java.io.Closeable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void connect() throws java.io.IOException` |  |
| `android.nfc.Tag getTag()` |  |
| `boolean isConnected()` |  |

---

## Package: `android.preference`

### `class CheckBoxPreference` ~~DEPRECATED~~

- **Extends:** `android.preference.TwoStatePreference`
- **Annotations:** `@Deprecated`

---

### `class abstract DialogPreference` ~~DEPRECATED~~

- **Extends:** `android.preference.Preference`
- **Implements:** `android.content.DialogInterface.OnClickListener android.content.DialogInterface.OnDismissListener android.preference.PreferenceManager.OnActivityDestroyListener`
- **Annotations:** `@Deprecated`

---

### `class EditTextPreference` ~~DEPRECATED~~

- **Extends:** `android.preference.DialogPreference`
- **Annotations:** `@Deprecated`

---

### `class ListPreference` ~~DEPRECATED~~

- **Extends:** `android.preference.DialogPreference`
- **Annotations:** `@Deprecated`

---

### `class MultiSelectListPreference` ~~DEPRECATED~~

- **Extends:** `android.preference.DialogPreference`
- **Annotations:** `@Deprecated`

---

### `class Preference` ~~DEPRECATED~~

- **Implements:** `java.lang.Comparable<android.preference.Preference>`
- **Annotations:** `@Deprecated`

---

### `class static Preference.BaseSavedState` ~~DEPRECATED~~

- **Extends:** `android.view.AbsSavedState`
- **Annotations:** `@Deprecated`

---

### `interface static Preference.OnPreferenceChangeListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static Preference.OnPreferenceClickListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class abstract PreferenceActivity` ~~DEPRECATED~~

- **Extends:** `android.app.ListActivity`
- **Implements:** `android.preference.PreferenceFragment.OnPreferenceStartFragmentCallback`
- **Annotations:** `@Deprecated`

---

### `class static final PreferenceActivity.Header` ~~DEPRECATED~~

- **Implements:** `android.os.Parcelable`
- **Annotations:** `@Deprecated`

---

### `class PreferenceCategory` ~~DEPRECATED~~

- **Extends:** `android.preference.PreferenceGroup`
- **Annotations:** `@Deprecated`

---

### `interface PreferenceDataStore` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class abstract PreferenceFragment` ~~DEPRECATED~~

- **Extends:** `android.app.Fragment`
- **Annotations:** `@Deprecated`

---

### `interface static PreferenceFragment.OnPreferenceStartFragmentCallback` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class abstract PreferenceGroup` ~~DEPRECATED~~

- **Extends:** `android.preference.Preference`
- **Annotations:** `@Deprecated`

---

### `class PreferenceManager` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static PreferenceManager.OnActivityDestroyListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static PreferenceManager.OnActivityResultListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static PreferenceManager.OnActivityStopListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class final PreferenceScreen` ~~DEPRECATED~~

- **Extends:** `android.preference.PreferenceGroup`
- **Implements:** `android.widget.AdapterView.OnItemClickListener android.content.DialogInterface.OnDismissListener`
- **Annotations:** `@Deprecated`

---

### `class RingtonePreference` ~~DEPRECATED~~

- **Extends:** `android.preference.Preference`
- **Implements:** `android.preference.PreferenceManager.OnActivityResultListener`
- **Annotations:** `@Deprecated`

---

### `class SwitchPreference` ~~DEPRECATED~~

- **Extends:** `android.preference.TwoStatePreference`
- **Annotations:** `@Deprecated`

---

### `class abstract TwoStatePreference` ~~DEPRECATED~~

- **Extends:** `android.preference.Preference`
- **Annotations:** `@Deprecated`

---

## Package: `android.print`

### `class final PageRange`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PageRange(@IntRange(from=0) int, @IntRange(from=0) int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.print.PageRange ALL_PAGES` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final PrintAttributes`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int COLOR_MODE_COLOR = 2` |  |
| `static final int COLOR_MODE_MONOCHROME = 1` |  |
| `static final int DUPLEX_MODE_LONG_EDGE = 2` |  |
| `static final int DUPLEX_MODE_NONE = 1` |  |
| `static final int DUPLEX_MODE_SHORT_EDGE = 4` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final PrintAttributes.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintAttributes.Builder()` |  |

---

### `class static final PrintAttributes.Margins`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintAttributes.Margins(int, int, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.print.PrintAttributes.Margins NO_MARGINS` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getBottomMils()` |  |
| `int getLeftMils()` |  |
| `int getRightMils()` |  |
| `int getTopMils()` |  |

---

### `class static final PrintAttributes.MediaSize`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintAttributes.MediaSize(@NonNull String, @NonNull String, @IntRange(from=1) int, @IntRange(from=1) int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.print.PrintAttributes.MediaSize ISO_A0` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A1` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A10` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A2` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A3` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A4` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A5` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A6` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A7` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A8` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_A9` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B0` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B1` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B10` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B2` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B3` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B4` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B5` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B6` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B7` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B8` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_B9` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C0` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C1` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C10` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C2` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C3` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C4` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C5` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C6` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C7` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C8` |  |
| `static final android.print.PrintAttributes.MediaSize ISO_C9` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B0` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B1` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B10` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B2` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B3` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B4` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B5` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B6` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B7` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B8` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_B9` |  |
| `static final android.print.PrintAttributes.MediaSize JIS_EXEC` |  |
| `static final android.print.PrintAttributes.MediaSize JPN_CHOU2` |  |
| `static final android.print.PrintAttributes.MediaSize JPN_CHOU3` |  |
| `static final android.print.PrintAttributes.MediaSize JPN_CHOU4` |  |
| `static final android.print.PrintAttributes.MediaSize JPN_HAGAKI` |  |
| `static final android.print.PrintAttributes.MediaSize JPN_KAHU` |  |
| `static final android.print.PrintAttributes.MediaSize JPN_KAKU2` |  |
| `static final android.print.PrintAttributes.MediaSize JPN_OUFUKU` |  |
| `static final android.print.PrintAttributes.MediaSize JPN_YOU4` |  |
| `static final android.print.PrintAttributes.MediaSize NA_FOOLSCAP` |  |
| `static final android.print.PrintAttributes.MediaSize NA_GOVT_LETTER` |  |
| `static final android.print.PrintAttributes.MediaSize NA_INDEX_3X5` |  |
| `static final android.print.PrintAttributes.MediaSize NA_INDEX_4X6` |  |
| `static final android.print.PrintAttributes.MediaSize NA_INDEX_5X8` |  |
| `static final android.print.PrintAttributes.MediaSize NA_JUNIOR_LEGAL` |  |
| `static final android.print.PrintAttributes.MediaSize NA_LEDGER` |  |
| `static final android.print.PrintAttributes.MediaSize NA_LEGAL` |  |
| `static final android.print.PrintAttributes.MediaSize NA_LETTER` |  |
| `static final android.print.PrintAttributes.MediaSize NA_MONARCH` |  |
| `static final android.print.PrintAttributes.MediaSize NA_QUARTO` |  |
| `static final android.print.PrintAttributes.MediaSize NA_TABLOID` |  |
| `static final android.print.PrintAttributes.MediaSize OM_DAI_PA_KAI` |  |
| `static final android.print.PrintAttributes.MediaSize OM_JUURO_KU_KAI` |  |
| `static final android.print.PrintAttributes.MediaSize OM_PA_KAI` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_1` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_10` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_16K` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_2` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_3` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_4` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_5` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_6` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_7` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_8` |  |
| `static final android.print.PrintAttributes.MediaSize PRC_9` |  |
| `static final android.print.PrintAttributes.MediaSize ROC_16K` |  |
| `static final android.print.PrintAttributes.MediaSize ROC_8K` |  |
| `static final android.print.PrintAttributes.MediaSize UNKNOWN_LANDSCAPE` |  |
| `static final android.print.PrintAttributes.MediaSize UNKNOWN_PORTRAIT` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isPortrait()` |  |

---

### `class static final PrintAttributes.Resolution`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintAttributes.Resolution(@NonNull String, @NonNull String, @IntRange(from=1) int, @IntRange(from=1) int)` |  |

---

### `class abstract PrintDocumentAdapter`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintDocumentAdapter()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_PRINT_PREVIEW = "EXTRA_PRINT_PREVIEW"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onFinish()` |  |
| `abstract void onLayout(android.print.PrintAttributes, android.print.PrintAttributes, android.os.CancellationSignal, android.print.PrintDocumentAdapter.LayoutResultCallback, android.os.Bundle)` |  |
| `void onStart()` |  |
| `abstract void onWrite(android.print.PageRange[], android.os.ParcelFileDescriptor, android.os.CancellationSignal, android.print.PrintDocumentAdapter.WriteResultCallback)` |  |
| `void onLayoutCancelled()` |  |
| `void onLayoutFailed(CharSequence)` |  |
| `void onLayoutFinished(android.print.PrintDocumentInfo, boolean)` |  |
| `void onWriteCancelled()` |  |
| `void onWriteFailed(CharSequence)` |  |
| `void onWriteFinished(android.print.PageRange[])` |  |

---

### `class final PrintDocumentInfo`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CONTENT_TYPE_DOCUMENT = 0` |  |
| `static final int CONTENT_TYPE_PHOTO = 1` |  |
| `static final int CONTENT_TYPE_UNKNOWN = -1` |  |
| `static final int PAGE_COUNT_UNKNOWN = -1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getContentType()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final PrintDocumentInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintDocumentInfo.Builder(@NonNull String)` |  |

---

### `class final PrintJob`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `boolean isBlocked()` |  |
| `boolean isCancelled()` |  |
| `boolean isCompleted()` |  |
| `boolean isFailed()` |  |
| `boolean isQueued()` |  |
| `boolean isStarted()` |  |
| `void restart()` |  |

---

### `class final PrintJobId`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final PrintJobInfo`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int STATE_BLOCKED = 4` |  |
| `static final int STATE_CANCELED = 7` |  |
| `static final int STATE_COMPLETED = 5` |  |
| `static final int STATE_CREATED = 1` |  |
| `static final int STATE_FAILED = 6` |  |
| `static final int STATE_QUEUED = 2` |  |
| `static final int STATE_STARTED = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getAdvancedIntOption(String)` |  |
| `String getAdvancedStringOption(String)` |  |
| `long getCreationTime()` |  |
| `int getState()` |  |
| `boolean hasAdvancedOption(String)` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final PrintJobInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintJobInfo.Builder(@Nullable android.print.PrintJobInfo)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void putAdvancedOption(@NonNull String, @Nullable String)` |  |
| `void putAdvancedOption(@NonNull String, int)` |  |
| `void setAttributes(@NonNull android.print.PrintAttributes)` |  |
| `void setCopies(@IntRange(from=1) int)` |  |
| `void setPages(@NonNull android.print.PageRange[])` |  |

---

### `class final PrintManager`


---

### `class final PrinterCapabilitiesInfo`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getColorModes()` |  |
| `int getDuplexModes()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final PrinterCapabilitiesInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrinterCapabilitiesInfo.Builder(@NonNull android.print.PrinterId)` |  |

---

### `class final PrinterId`

- **Implements:** `android.os.Parcelable`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class final PrinterInfo`

- **Implements:** `android.os.Parcelable`

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int STATUS_BUSY = 2` |  |
| `static final int STATUS_IDLE = 1` |  |
| `static final int STATUS_UNAVAILABLE = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `int getStatus()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

### `class static final PrinterInfo.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrinterInfo.Builder(@NonNull android.print.PrinterId, @NonNull String, int)` |  |
| `PrinterInfo.Builder(@NonNull android.print.PrinterInfo)` |  |

---

## Package: `android.print.pdf`

### `class PrintedPdfDocument`

- **Extends:** `android.graphics.pdf.PdfDocument`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintedPdfDocument(@NonNull android.content.Context, @NonNull android.print.PrintAttributes)` |  |

---

## Package: `android.printservice`

### `class final CustomPrinterIconCallback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean onCustomPrinterIconLoaded(@Nullable android.graphics.drawable.Icon)` |  |

---

### `class final PrintDocument`


---

### `class final PrintJob`


---

### `class abstract PrintService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrintService()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String EXTRA_CAN_SELECT_PRINTER = "android.printservice.extra.CAN_SELECT_PRINTER"` |  |
| `static final String EXTRA_PRINTER_INFO = "android.intent.extra.print.EXTRA_PRINTER_INFO"` |  |
| `static final String EXTRA_PRINT_DOCUMENT_INFO = "android.printservice.extra.PRINT_DOCUMENT_INFO"` |  |
| `static final String EXTRA_PRINT_JOB_INFO = "android.intent.extra.print.PRINT_JOB_INFO"` |  |
| `static final String EXTRA_SELECT_PRINTER = "android.printservice.extra.SELECT_PRINTER"` |  |
| `static final String SERVICE_INTERFACE = "android.printservice.PrintService"` |  |
| `static final String SERVICE_META_DATA = "android.printservice"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void attachBaseContext(android.content.Context)` |  |
| `final java.util.List<android.printservice.PrintJob> getActivePrintJobs()` |  |
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `void onConnected()` |  |
| `void onDisconnected()` |  |
| `abstract void onPrintJobQueued(android.printservice.PrintJob)` |  |
| `abstract void onRequestCancelPrintJob(android.printservice.PrintJob)` |  |

---

### `class abstract PrinterDiscoverySession`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PrinterDiscoverySession()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void addPrinters(@NonNull java.util.List<android.print.PrinterInfo>)` |  |
| `final boolean isDestroyed()` |  |
| `final boolean isPrinterDiscoveryStarted()` |  |
| `abstract void onDestroy()` |  |
| `void onRequestCustomPrinterIcon(@NonNull android.print.PrinterId, @NonNull android.os.CancellationSignal, @NonNull android.printservice.CustomPrinterIconCallback)` |  |
| `abstract void onStartPrinterDiscovery(@NonNull java.util.List<android.print.PrinterId>)` |  |
| `abstract void onStartPrinterStateTracking(@NonNull android.print.PrinterId)` |  |
| `abstract void onStopPrinterDiscovery()` |  |
| `abstract void onStopPrinterStateTracking(@NonNull android.print.PrinterId)` |  |
| `abstract void onValidatePrinters(@NonNull java.util.List<android.print.PrinterId>)` |  |
| `final void removePrinters(@NonNull java.util.List<android.print.PrinterId>)` |  |

---

## Package: `android.sax`

### `class Element`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.sax.Element getChild(String)` |  |
| `android.sax.Element getChild(String, String)` |  |
| `android.sax.Element requireChild(String)` |  |
| `android.sax.Element requireChild(String, String)` |  |
| `void setElementListener(android.sax.ElementListener)` |  |
| `void setEndElementListener(android.sax.EndElementListener)` |  |
| `void setEndTextElementListener(android.sax.EndTextElementListener)` |  |
| `void setStartElementListener(android.sax.StartElementListener)` |  |
| `void setTextElementListener(android.sax.TextElementListener)` |  |

---

### `interface ElementListener`

- **Extends:** `android.sax.StartElementListener android.sax.EndElementListener`

---

### `interface EndElementListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void end()` |  |

---

### `interface EndTextElementListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void end(String)` |  |

---

### `class RootElement`

- **Extends:** `android.sax.Element`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RootElement(String, String)` |  |
| `RootElement(String)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `org.xml.sax.ContentHandler getContentHandler()` |  |

---

### `interface StartElementListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void start(org.xml.sax.Attributes)` |  |

---

### `interface TextElementListener`

- **Extends:** `android.sax.StartElementListener android.sax.EndTextElementListener`

---

## Package: `android.se.omapi`

### `class final Channel`

- **Implements:** `java.nio.channels.Channel`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `boolean isBasicChannel()` |  |
| `boolean isOpen()` |  |
| `boolean selectNext() throws java.io.IOException` |  |

---

### `class final Reader`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void closeSessions()` |  |
| `boolean isSecureElementPresent()` |  |

---

### `class final SEService`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SEService(@NonNull android.content.Context, @NonNull java.util.concurrent.Executor, @NonNull android.se.omapi.SEService.OnConnectedListener)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `boolean isConnected()` |  |
| `void shutdown()` |  |

---

### `interface static SEService.OnConnectedListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onConnected()` |  |

---

### `class final Session`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void close()` |  |
| `void closeChannels()` |  |
| `boolean isClosed()` |  |

---

## Package: `android.speech`

### `interface RecognitionListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onBeginningOfSpeech()` |  |
| `void onBufferReceived(byte[])` |  |
| `void onEndOfSpeech()` |  |
| `void onError(int)` |  |
| `void onEvent(int, android.os.Bundle)` |  |
| `void onPartialResults(android.os.Bundle)` |  |
| `void onReadyForSpeech(android.os.Bundle)` |  |
| `void onResults(android.os.Bundle)` |  |
| `void onRmsChanged(float)` |  |

---

### `class abstract RecognitionService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `RecognitionService()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String SERVICE_INTERFACE = "android.speech.RecognitionService"` |  |
| `static final String SERVICE_META_DATA = "android.speech"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final android.os.IBinder onBind(android.content.Intent)` |  |
| `abstract void onCancel(android.speech.RecognitionService.Callback)` |  |
| `abstract void onStartListening(android.content.Intent, android.speech.RecognitionService.Callback)` |  |
| `abstract void onStopListening(android.speech.RecognitionService.Callback)` |  |

---

### `class RecognitionService.Callback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void beginningOfSpeech() throws android.os.RemoteException` |  |
| `void bufferReceived(byte[]) throws android.os.RemoteException` |  |
| `void endOfSpeech() throws android.os.RemoteException` |  |
| `void error(int) throws android.os.RemoteException` |  |
| `int getCallingUid()` |  |
| `void partialResults(android.os.Bundle) throws android.os.RemoteException` |  |
| `void readyForSpeech(android.os.Bundle) throws android.os.RemoteException` |  |
| `void results(android.os.Bundle) throws android.os.RemoteException` |  |
| `void rmsChanged(float) throws android.os.RemoteException` |  |

---

### `class RecognizerIntent`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_GET_LANGUAGE_DETAILS = "android.speech.action.GET_LANGUAGE_DETAILS"` |  |
| `static final String ACTION_RECOGNIZE_SPEECH = "android.speech.action.RECOGNIZE_SPEECH"` |  |
| `static final String ACTION_VOICE_SEARCH_HANDS_FREE = "android.speech.action.VOICE_SEARCH_HANDS_FREE"` |  |
| `static final String ACTION_WEB_SEARCH = "android.speech.action.WEB_SEARCH"` |  |
| `static final String DETAILS_META_DATA = "android.speech.DETAILS"` |  |
| `static final String EXTRA_CALLING_PACKAGE = "calling_package"` |  |
| `static final String EXTRA_CONFIDENCE_SCORES = "android.speech.extra.CONFIDENCE_SCORES"` |  |
| `static final String EXTRA_LANGUAGE = "android.speech.extra.LANGUAGE"` |  |
| `static final String EXTRA_LANGUAGE_MODEL = "android.speech.extra.LANGUAGE_MODEL"` |  |
| `static final String EXTRA_LANGUAGE_PREFERENCE = "android.speech.extra.LANGUAGE_PREFERENCE"` |  |
| `static final String EXTRA_MAX_RESULTS = "android.speech.extra.MAX_RESULTS"` |  |
| `static final String EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE = "android.speech.extra.ONLY_RETURN_LANGUAGE_PREFERENCE"` |  |
| `static final String EXTRA_ORIGIN = "android.speech.extra.ORIGIN"` |  |
| `static final String EXTRA_PARTIAL_RESULTS = "android.speech.extra.PARTIAL_RESULTS"` |  |
| `static final String EXTRA_PREFER_OFFLINE = "android.speech.extra.PREFER_OFFLINE"` |  |
| `static final String EXTRA_PROMPT = "android.speech.extra.PROMPT"` |  |
| `static final String EXTRA_RESULTS = "android.speech.extra.RESULTS"` |  |
| `static final String EXTRA_RESULTS_PENDINGINTENT = "android.speech.extra.RESULTS_PENDINGINTENT"` |  |
| `static final String EXTRA_RESULTS_PENDINGINTENT_BUNDLE = "android.speech.extra.RESULTS_PENDINGINTENT_BUNDLE"` |  |
| `static final String EXTRA_SECURE = "android.speech.extras.EXTRA_SECURE"` |  |
| `static final String EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS = "android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS"` |  |
| `static final String EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS = "android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS"` |  |
| `static final String EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS = "android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS"` |  |
| `static final String EXTRA_SUPPORTED_LANGUAGES = "android.speech.extra.SUPPORTED_LANGUAGES"` |  |
| `static final String EXTRA_WEB_SEARCH_ONLY = "android.speech.extra.WEB_SEARCH_ONLY"` |  |
| `static final String LANGUAGE_MODEL_FREE_FORM = "free_form"` |  |
| `static final String LANGUAGE_MODEL_WEB_SEARCH = "web_search"` |  |
| `static final int RESULT_AUDIO_ERROR = 5` |  |
| `static final int RESULT_CLIENT_ERROR = 2` |  |
| `static final int RESULT_NETWORK_ERROR = 4` |  |
| `static final int RESULT_NO_MATCH = 1` |  |
| `static final int RESULT_SERVER_ERROR = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static final android.content.Intent getVoiceDetailsIntent(android.content.Context)` |  |

---

### `class RecognizerResultsIntent`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_VOICE_SEARCH_RESULTS = "android.speech.action.VOICE_SEARCH_RESULTS"` |  |
| `static final String EXTRA_VOICE_SEARCH_RESULT_HTML = "android.speech.extras.VOICE_SEARCH_RESULT_HTML"` |  |
| `static final String EXTRA_VOICE_SEARCH_RESULT_HTML_BASE_URLS = "android.speech.extras.VOICE_SEARCH_RESULT_HTML_BASE_URLS"` |  |
| `static final String EXTRA_VOICE_SEARCH_RESULT_HTTP_HEADERS = "android.speech.extras.EXTRA_VOICE_SEARCH_RESULT_HTTP_HEADERS"` |  |
| `static final String EXTRA_VOICE_SEARCH_RESULT_STRINGS = "android.speech.extras.VOICE_SEARCH_RESULT_STRINGS"` |  |
| `static final String EXTRA_VOICE_SEARCH_RESULT_URLS = "android.speech.extras.VOICE_SEARCH_RESULT_URLS"` |  |
| `static final String URI_SCHEME_INLINE = "inline"` |  |

---

### `class SpeechRecognizer`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String CONFIDENCE_SCORES = "confidence_scores"` |  |
| `static final int ERROR_AUDIO = 3` |  |
| `static final int ERROR_CLIENT = 5` |  |
| `static final int ERROR_INSUFFICIENT_PERMISSIONS = 9` |  |
| `static final int ERROR_NETWORK = 2` |  |
| `static final int ERROR_NETWORK_TIMEOUT = 1` |  |
| `static final int ERROR_NO_MATCH = 7` |  |
| `static final int ERROR_RECOGNIZER_BUSY = 8` |  |
| `static final int ERROR_SERVER = 4` |  |
| `static final int ERROR_SPEECH_TIMEOUT = 6` |  |
| `static final String RESULTS_RECOGNITION = "results_recognition"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `static android.speech.SpeechRecognizer createSpeechRecognizer(android.content.Context)` |  |
| `static android.speech.SpeechRecognizer createSpeechRecognizer(android.content.Context, android.content.ComponentName)` |  |
| `void destroy()` |  |
| `static boolean isRecognitionAvailable(android.content.Context)` |  |
| `void setRecognitionListener(android.speech.RecognitionListener)` |  |
| `void startListening(android.content.Intent)` |  |
| `void stopListening()` |  |

---

## Package: `android.speech.tts`

### `interface SynthesisCallback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int audioAvailable(byte[], int, int)` |  |
| `int done()` |  |
| `void error()` |  |
| `void error(int)` |  |
| `int getMaxBufferSize()` |  |
| `boolean hasFinished()` |  |
| `boolean hasStarted()` |  |
| `default void rangeStart(int, int, int)` |  |
| `int start(int, int, @IntRange(from=1, to=2) int)` |  |

---

### `class final SynthesisRequest`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SynthesisRequest(String, android.os.Bundle)` |  |
| `SynthesisRequest(CharSequence, android.os.Bundle)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getCallerUid()` |  |
| `CharSequence getCharSequenceText()` |  |
| `String getCountry()` |  |
| `String getLanguage()` |  |
| `android.os.Bundle getParams()` |  |
| `int getPitch()` |  |
| `int getSpeechRate()` |  |
| `String getVariant()` |  |
| `String getVoiceName()` |  |

---

### `class TextToSpeech`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextToSpeech(android.content.Context, android.speech.tts.TextToSpeech.OnInitListener)` |  |
| `TextToSpeech(android.content.Context, android.speech.tts.TextToSpeech.OnInitListener, String)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_TTS_QUEUE_PROCESSING_COMPLETED = "android.speech.tts.TTS_QUEUE_PROCESSING_COMPLETED"` |  |
| `static final int ERROR = -1` |  |
| `static final int ERROR_INVALID_REQUEST = -8` |  |
| `static final int ERROR_NETWORK = -6` |  |
| `static final int ERROR_NETWORK_TIMEOUT = -7` |  |
| `static final int ERROR_NOT_INSTALLED_YET = -9` |  |
| `static final int ERROR_OUTPUT = -5` |  |
| `static final int ERROR_SERVICE = -4` |  |
| `static final int ERROR_SYNTHESIS = -3` |  |
| `static final int LANG_AVAILABLE = 0` |  |
| `static final int LANG_COUNTRY_AVAILABLE = 1` |  |
| `static final int LANG_COUNTRY_VAR_AVAILABLE = 2` |  |
| `static final int LANG_MISSING_DATA = -1` |  |
| `static final int LANG_NOT_SUPPORTED = -2` |  |
| `static final int QUEUE_ADD = 1` |  |
| `static final int QUEUE_FLUSH = 0` |  |
| `static final int STOPPED = -2` |  |
| `static final int SUCCESS = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int addEarcon(String, String, @RawRes int)` |  |
| `int addEarcon(String, java.io.File)` |  |
| `int addSpeech(String, String, @RawRes int)` |  |
| `int addSpeech(CharSequence, String, @RawRes int)` |  |
| `int addSpeech(String, String)` |  |
| `int addSpeech(CharSequence, java.io.File)` |  |
| `java.util.Set<java.util.Locale> getAvailableLanguages()` |  |
| `String getDefaultEngine()` |  |
| `android.speech.tts.Voice getDefaultVoice()` |  |
| `java.util.List<android.speech.tts.TextToSpeech.EngineInfo> getEngines()` |  |
| `static int getMaxSpeechInputLength()` |  |
| `android.speech.tts.Voice getVoice()` |  |
| `java.util.Set<android.speech.tts.Voice> getVoices()` |  |
| `int isLanguageAvailable(java.util.Locale)` |  |
| `boolean isSpeaking()` |  |
| `int playEarcon(String, int, android.os.Bundle, String)` |  |
| `int playSilentUtterance(long, int, String)` |  |
| `int setAudioAttributes(android.media.AudioAttributes)` |  |
| `int setLanguage(java.util.Locale)` |  |
| `int setOnUtteranceProgressListener(android.speech.tts.UtteranceProgressListener)` |  |
| `int setPitch(float)` |  |
| `int setSpeechRate(float)` |  |
| `int setVoice(android.speech.tts.Voice)` |  |
| `void shutdown()` |  |
| `int speak(CharSequence, int, android.os.Bundle, String)` |  |
| `int stop()` |  |
| `int synthesizeToFile(@NonNull CharSequence, @NonNull android.os.Bundle, @NonNull android.os.ParcelFileDescriptor, @NonNull String)` |  |
| `int synthesizeToFile(CharSequence, android.os.Bundle, java.io.File, String)` |  |

---

### `class TextToSpeech.Engine`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextToSpeech.Engine()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String ACTION_CHECK_TTS_DATA = "android.speech.tts.engine.CHECK_TTS_DATA"` |  |
| `static final String ACTION_GET_SAMPLE_TEXT = "android.speech.tts.engine.GET_SAMPLE_TEXT"` |  |
| `static final String ACTION_INSTALL_TTS_DATA = "android.speech.tts.engine.INSTALL_TTS_DATA"` |  |
| `static final String ACTION_TTS_DATA_INSTALLED = "android.speech.tts.engine.TTS_DATA_INSTALLED"` |  |
| `static final int CHECK_VOICE_DATA_FAIL = 0` |  |
| `static final int CHECK_VOICE_DATA_PASS = 1` |  |
| `static final int DEFAULT_STREAM = 3` |  |
| `static final String EXTRA_AVAILABLE_VOICES = "availableVoices"` |  |
| `static final String EXTRA_SAMPLE_TEXT = "sampleText"` |  |
| `static final String EXTRA_UNAVAILABLE_VOICES = "unavailableVoices"` |  |
| `static final String INTENT_ACTION_TTS_SERVICE = "android.intent.action.TTS_SERVICE"` |  |
| `static final String KEY_FEATURE_NETWORK_RETRIES_COUNT = "networkRetriesCount"` |  |
| `static final String KEY_FEATURE_NETWORK_TIMEOUT_MS = "networkTimeoutMs"` |  |
| `static final String KEY_FEATURE_NOT_INSTALLED = "notInstalled"` |  |
| `static final String KEY_PARAM_PAN = "pan"` |  |
| `static final String KEY_PARAM_SESSION_ID = "sessionId"` |  |
| `static final String KEY_PARAM_STREAM = "streamType"` |  |
| `static final String KEY_PARAM_UTTERANCE_ID = "utteranceId"` |  |
| `static final String KEY_PARAM_VOLUME = "volume"` |  |
| `static final String SERVICE_META_DATA = "android.speech.tts"` |  |

---

### `class static TextToSpeech.EngineInfo`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextToSpeech.EngineInfo()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `int icon` |  |
| `String label` |  |
| `String name` |  |

---

### `interface static TextToSpeech.OnInitListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onInit(int)` |  |

---

### `interface static TextToSpeech.OnUtteranceCompletedListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class abstract TextToSpeechService`

- **Extends:** `android.app.Service`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TextToSpeechService()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.os.IBinder onBind(android.content.Intent)` |  |
| `String onGetDefaultVoiceNameFor(String, String, String)` |  |
| `java.util.Set<java.lang.String> onGetFeaturesForLanguage(String, String, String)` |  |
| `abstract String[] onGetLanguage()` |  |
| `java.util.List<android.speech.tts.Voice> onGetVoices()` |  |
| `abstract int onIsLanguageAvailable(String, String, String)` |  |
| `int onIsValidVoiceName(String)` |  |
| `abstract int onLoadLanguage(String, String, String)` |  |
| `int onLoadVoice(String)` |  |
| `abstract void onStop()` |  |
| `abstract void onSynthesizeText(android.speech.tts.SynthesisRequest, android.speech.tts.SynthesisCallback)` |  |

---

### `class abstract UtteranceProgressListener`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `UtteranceProgressListener()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onAudioAvailable(String, byte[])` |  |
| `void onBeginSynthesis(String, int, int, int)` |  |
| `abstract void onDone(String)` |  |
| `void onError(String, int)` |  |
| `void onRangeStart(String, int, int, int)` |  |
| `abstract void onStart(String)` |  |
| `void onStop(String, boolean)` |  |

---

### `class Voice`

- **Implements:** `android.os.Parcelable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Voice(String, java.util.Locale, int, int, boolean, java.util.Set<java.lang.String>)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int LATENCY_HIGH = 400` |  |
| `static final int LATENCY_LOW = 200` |  |
| `static final int LATENCY_NORMAL = 300` |  |
| `static final int LATENCY_VERY_HIGH = 500` |  |
| `static final int LATENCY_VERY_LOW = 100` |  |
| `static final int QUALITY_HIGH = 400` |  |
| `static final int QUALITY_LOW = 200` |  |
| `static final int QUALITY_NORMAL = 300` |  |
| `static final int QUALITY_VERY_HIGH = 500` |  |
| `static final int QUALITY_VERY_LOW = 100` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int describeContents()` |  |
| `java.util.Set<java.lang.String> getFeatures()` |  |
| `int getLatency()` |  |
| `java.util.Locale getLocale()` |  |
| `String getName()` |  |
| `int getQuality()` |  |
| `boolean isNetworkConnectionRequired()` |  |
| `void writeToParcel(android.os.Parcel, int)` |  |

---

## Package: `android.transition`

### `class ArcMotion`

- **Extends:** `android.transition.PathMotion`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ArcMotion()` |  |
| `ArcMotion(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `float getMaximumAngle()` |  |
| `float getMinimumHorizontalAngle()` |  |
| `float getMinimumVerticalAngle()` |  |
| `android.graphics.Path getPath(float, float, float, float)` |  |
| `void setMaximumAngle(float)` |  |
| `void setMinimumHorizontalAngle(float)` |  |
| `void setMinimumVerticalAngle(float)` |  |

---

### `class AutoTransition`

- **Extends:** `android.transition.TransitionSet`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `AutoTransition()` |  |
| `AutoTransition(android.content.Context, android.util.AttributeSet)` |  |

---

### `class ChangeBounds`

- **Extends:** `android.transition.Transition`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ChangeBounds()` |  |
| `ChangeBounds(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void captureEndValues(android.transition.TransitionValues)` |  |
| `void captureStartValues(android.transition.TransitionValues)` |  |
| `boolean getResizeClip()` |  |
| `void setResizeClip(boolean)` |  |

---

### `class ChangeClipBounds`

- **Extends:** `android.transition.Transition`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ChangeClipBounds()` |  |
| `ChangeClipBounds(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void captureEndValues(android.transition.TransitionValues)` |  |
| `void captureStartValues(android.transition.TransitionValues)` |  |

---

### `class ChangeImageTransform`

- **Extends:** `android.transition.Transition`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ChangeImageTransform()` |  |
| `ChangeImageTransform(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void captureEndValues(android.transition.TransitionValues)` |  |
| `void captureStartValues(android.transition.TransitionValues)` |  |

---

### `class ChangeScroll`

- **Extends:** `android.transition.Transition`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ChangeScroll()` |  |
| `ChangeScroll(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void captureEndValues(android.transition.TransitionValues)` |  |
| `void captureStartValues(android.transition.TransitionValues)` |  |

---

### `class ChangeTransform`

- **Extends:** `android.transition.Transition`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ChangeTransform()` |  |
| `ChangeTransform(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void captureEndValues(android.transition.TransitionValues)` |  |
| `void captureStartValues(android.transition.TransitionValues)` |  |
| `boolean getReparent()` |  |
| `boolean getReparentWithOverlay()` |  |
| `void setReparent(boolean)` |  |
| `void setReparentWithOverlay(boolean)` |  |

---

### `class CircularPropagation`

- **Extends:** `android.transition.VisibilityPropagation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `CircularPropagation()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getStartDelay(android.view.ViewGroup, android.transition.Transition, android.transition.TransitionValues, android.transition.TransitionValues)` |  |
| `void setPropagationSpeed(float)` |  |

---

### `class Explode`

- **Extends:** `android.transition.Visibility`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Explode()` |  |
| `Explode(android.content.Context, android.util.AttributeSet)` |  |

---

### `class Fade`

- **Extends:** `android.transition.Visibility`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Fade()` |  |
| `Fade(int)` |  |
| `Fade(android.content.Context, android.util.AttributeSet)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int IN = 1` |  |
| `static final int OUT = 2` |  |

---

### `class abstract PathMotion`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PathMotion()` |  |
| `PathMotion(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract android.graphics.Path getPath(float, float, float, float)` |  |

---

### `class PatternPathMotion`

- **Extends:** `android.transition.PathMotion`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PatternPathMotion()` |  |
| `PatternPathMotion(android.content.Context, android.util.AttributeSet)` |  |
| `PatternPathMotion(android.graphics.Path)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.graphics.Path getPath(float, float, float, float)` |  |
| `android.graphics.Path getPatternPath()` |  |
| `void setPatternPath(android.graphics.Path)` |  |

---

### `class final Scene`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Scene(android.view.ViewGroup)` |  |
| `Scene(android.view.ViewGroup, android.view.View)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void enter()` |  |
| `void exit()` |  |
| `static android.transition.Scene getSceneForLayout(android.view.ViewGroup, int, android.content.Context)` |  |
| `android.view.ViewGroup getSceneRoot()` |  |
| `void setEnterAction(Runnable)` |  |
| `void setExitAction(Runnable)` |  |

---

### `class SidePropagation`

- **Extends:** `android.transition.VisibilityPropagation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `SidePropagation()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getStartDelay(android.view.ViewGroup, android.transition.Transition, android.transition.TransitionValues, android.transition.TransitionValues)` |  |
| `void setPropagationSpeed(float)` |  |
| `void setSide(int)` |  |

---

### `class Slide`

- **Extends:** `android.transition.Visibility`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Slide()` |  |
| `Slide(int)` |  |
| `Slide(android.content.Context, android.util.AttributeSet)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getSlideEdge()` |  |
| `void setSlideEdge(int)` |  |

---

### `class abstract Transition`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Transition()` |  |
| `Transition(android.content.Context, android.util.AttributeSet)` |  |
| `Transition.EpicenterCallback()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MATCH_ID = 3` |  |
| `static final int MATCH_INSTANCE = 1` |  |
| `static final int MATCH_ITEM_ID = 4` |  |
| `static final int MATCH_NAME = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.transition.Transition addListener(android.transition.Transition.TransitionListener)` |  |
| `android.transition.Transition addTarget(int)` |  |
| `android.transition.Transition addTarget(String)` |  |
| `android.transition.Transition addTarget(Class)` |  |
| `android.transition.Transition addTarget(android.view.View)` |  |
| `boolean canRemoveViews()` |  |
| `abstract void captureEndValues(android.transition.TransitionValues)` |  |
| `abstract void captureStartValues(android.transition.TransitionValues)` |  |
| `android.transition.Transition clone()` |  |
| `android.animation.Animator createAnimator(android.view.ViewGroup, android.transition.TransitionValues, android.transition.TransitionValues)` |  |
| `android.transition.Transition excludeChildren(int, boolean)` |  |
| `android.transition.Transition excludeChildren(android.view.View, boolean)` |  |
| `android.transition.Transition excludeChildren(Class, boolean)` |  |
| `android.transition.Transition excludeTarget(int, boolean)` |  |
| `android.transition.Transition excludeTarget(String, boolean)` |  |
| `android.transition.Transition excludeTarget(android.view.View, boolean)` |  |
| `android.transition.Transition excludeTarget(Class, boolean)` |  |
| `long getDuration()` |  |
| `android.graphics.Rect getEpicenter()` |  |
| `android.transition.Transition.EpicenterCallback getEpicenterCallback()` |  |
| `android.animation.TimeInterpolator getInterpolator()` |  |
| `String getName()` |  |
| `android.transition.PathMotion getPathMotion()` |  |
| `android.transition.TransitionPropagation getPropagation()` |  |
| `long getStartDelay()` |  |
| `java.util.List<java.lang.Integer> getTargetIds()` |  |
| `java.util.List<java.lang.String> getTargetNames()` |  |
| `java.util.List<java.lang.Class> getTargetTypes()` |  |
| `java.util.List<android.view.View> getTargets()` |  |
| `String[] getTransitionProperties()` |  |
| `android.transition.TransitionValues getTransitionValues(android.view.View, boolean)` |  |
| `boolean isTransitionRequired(@Nullable android.transition.TransitionValues, @Nullable android.transition.TransitionValues)` |  |
| `android.transition.Transition removeListener(android.transition.Transition.TransitionListener)` |  |
| `android.transition.Transition removeTarget(int)` |  |
| `android.transition.Transition removeTarget(String)` |  |
| `android.transition.Transition removeTarget(android.view.View)` |  |
| `android.transition.Transition removeTarget(Class)` |  |
| `android.transition.Transition setDuration(long)` |  |
| `void setEpicenterCallback(android.transition.Transition.EpicenterCallback)` |  |
| `android.transition.Transition setInterpolator(android.animation.TimeInterpolator)` |  |
| `void setMatchOrder(int...)` |  |
| `void setPathMotion(android.transition.PathMotion)` |  |
| `void setPropagation(android.transition.TransitionPropagation)` |  |
| `android.transition.Transition setStartDelay(long)` |  |
| `abstract android.graphics.Rect onGetEpicenter(android.transition.Transition)` |  |

---

### `interface static Transition.TransitionListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onTransitionCancel(android.transition.Transition)` |  |
| `void onTransitionEnd(android.transition.Transition)` |  |
| `void onTransitionPause(android.transition.Transition)` |  |
| `void onTransitionResume(android.transition.Transition)` |  |
| `void onTransitionStart(android.transition.Transition)` |  |

---

### `class TransitionInflater`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static android.transition.TransitionInflater from(android.content.Context)` |  |
| `android.transition.Transition inflateTransition(@TransitionRes int)` |  |
| `android.transition.TransitionManager inflateTransitionManager(@TransitionRes int, android.view.ViewGroup)` |  |

---

### `class abstract TransitionListenerAdapter`

- **Implements:** `android.transition.Transition.TransitionListener`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TransitionListenerAdapter()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onTransitionCancel(android.transition.Transition)` |  |
| `void onTransitionEnd(android.transition.Transition)` |  |
| `void onTransitionPause(android.transition.Transition)` |  |
| `void onTransitionResume(android.transition.Transition)` |  |
| `void onTransitionStart(android.transition.Transition)` |  |

---

### `class TransitionManager`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TransitionManager()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static void beginDelayedTransition(android.view.ViewGroup)` |  |
| `static void beginDelayedTransition(android.view.ViewGroup, android.transition.Transition)` |  |
| `static void endTransitions(android.view.ViewGroup)` |  |
| `static void go(android.transition.Scene)` |  |
| `static void go(android.transition.Scene, android.transition.Transition)` |  |
| `void setTransition(android.transition.Scene, android.transition.Transition)` |  |
| `void setTransition(android.transition.Scene, android.transition.Scene, android.transition.Transition)` |  |
| `void transitionTo(android.transition.Scene)` |  |

---

### `class abstract TransitionPropagation`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TransitionPropagation()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void captureValues(android.transition.TransitionValues)` |  |
| `abstract String[] getPropagationProperties()` |  |
| `abstract long getStartDelay(android.view.ViewGroup, android.transition.Transition, android.transition.TransitionValues, android.transition.TransitionValues)` |  |

---

### `class TransitionSet`

- **Extends:** `android.transition.Transition`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TransitionSet()` |  |
| `TransitionSet(android.content.Context, android.util.AttributeSet)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ORDERING_SEQUENTIAL = 1` |  |
| `static final int ORDERING_TOGETHER = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.transition.TransitionSet addListener(android.transition.Transition.TransitionListener)` |  |
| `android.transition.TransitionSet addTarget(android.view.View)` |  |
| `android.transition.TransitionSet addTarget(int)` |  |
| `android.transition.TransitionSet addTarget(String)` |  |
| `android.transition.TransitionSet addTarget(Class)` |  |
| `android.transition.TransitionSet addTransition(android.transition.Transition)` |  |
| `void captureEndValues(android.transition.TransitionValues)` |  |
| `void captureStartValues(android.transition.TransitionValues)` |  |
| `android.transition.TransitionSet clone()` |  |
| `int getOrdering()` |  |
| `android.transition.Transition getTransitionAt(int)` |  |
| `int getTransitionCount()` |  |
| `android.transition.TransitionSet removeListener(android.transition.Transition.TransitionListener)` |  |
| `android.transition.TransitionSet removeTarget(int)` |  |
| `android.transition.TransitionSet removeTarget(android.view.View)` |  |
| `android.transition.TransitionSet removeTarget(Class)` |  |
| `android.transition.TransitionSet removeTarget(String)` |  |
| `android.transition.TransitionSet removeTransition(android.transition.Transition)` |  |
| `android.transition.TransitionSet setDuration(long)` |  |
| `android.transition.TransitionSet setInterpolator(android.animation.TimeInterpolator)` |  |
| `android.transition.TransitionSet setOrdering(int)` |  |
| `android.transition.TransitionSet setStartDelay(long)` |  |

---

### `class TransitionValues`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TransitionValues(@NonNull android.view.View)` |  |

---

### `class abstract Visibility`

- **Extends:** `android.transition.Transition`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `Visibility()` |  |
| `Visibility(android.content.Context, android.util.AttributeSet)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MODE_IN = 1` |  |
| `static final int MODE_OUT = 2` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void captureEndValues(android.transition.TransitionValues)` |  |
| `void captureStartValues(android.transition.TransitionValues)` |  |
| `int getMode()` |  |
| `boolean isVisible(android.transition.TransitionValues)` |  |
| `android.animation.Animator onAppear(android.view.ViewGroup, android.transition.TransitionValues, int, android.transition.TransitionValues, int)` |  |
| `android.animation.Animator onAppear(android.view.ViewGroup, android.view.View, android.transition.TransitionValues, android.transition.TransitionValues)` |  |
| `android.animation.Animator onDisappear(android.view.ViewGroup, android.transition.TransitionValues, int, android.transition.TransitionValues, int)` |  |
| `android.animation.Animator onDisappear(android.view.ViewGroup, android.view.View, android.transition.TransitionValues, android.transition.TransitionValues)` |  |
| `void setMode(int)` |  |

---

### `class abstract VisibilityPropagation`

- **Extends:** `android.transition.TransitionPropagation`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `VisibilityPropagation()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void captureValues(android.transition.TransitionValues)` |  |
| `String[] getPropagationProperties()` |  |
| `int getViewVisibility(android.transition.TransitionValues)` |  |
| `int getViewX(android.transition.TransitionValues)` |  |
| `int getViewY(android.transition.TransitionValues)` |  |

---

## Package: `android.webkit`

### `class abstract ClientCertRequest`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ClientCertRequest()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void cancel()` |  |
| `abstract String getHost()` |  |
| `abstract int getPort()` |  |
| `abstract void ignore()` |  |
| `abstract void proceed(java.security.PrivateKey, java.security.cert.X509Certificate[])` |  |

---

### `class ConsoleMessage`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ConsoleMessage(String, String, int, android.webkit.ConsoleMessage.MessageLevel)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int lineNumber()` |  |
| `String message()` |  |
| `android.webkit.ConsoleMessage.MessageLevel messageLevel()` |  |
| `String sourceId()` |  |

---

### `enum ConsoleMessage.MessageLevel`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.webkit.ConsoleMessage.MessageLevel DEBUG` |  |
| `static final android.webkit.ConsoleMessage.MessageLevel ERROR` |  |
| `static final android.webkit.ConsoleMessage.MessageLevel LOG` |  |
| `static final android.webkit.ConsoleMessage.MessageLevel TIP` |  |
| `static final android.webkit.ConsoleMessage.MessageLevel WARNING` |  |

---

### `class abstract CookieManager`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract boolean acceptCookie()` |  |
| `abstract boolean acceptThirdPartyCookies(android.webkit.WebView)` |  |
| `static boolean allowFileSchemeCookies()` |  |
| `abstract void flush()` |  |
| `abstract String getCookie(String)` |  |
| `static android.webkit.CookieManager getInstance()` |  |
| `abstract boolean hasCookies()` |  |
| `abstract void removeAllCookies(@Nullable android.webkit.ValueCallback<java.lang.Boolean>)` |  |
| `abstract void removeSessionCookies(@Nullable android.webkit.ValueCallback<java.lang.Boolean>)` |  |
| `abstract void setAcceptCookie(boolean)` |  |
| `abstract void setAcceptThirdPartyCookies(android.webkit.WebView, boolean)` |  |
| `abstract void setCookie(String, String)` |  |
| `abstract void setCookie(String, String, @Nullable android.webkit.ValueCallback<java.lang.Boolean>)` |  |

---

### `class final CookieSyncManager` ~~DEPRECATED~~

- **Implements:** `java.lang.Runnable`
- **Annotations:** `@Deprecated`

---

### `class DateSorter`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `DateSorter(android.content.Context)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int DAY_COUNT = 5` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `long getBoundary(int)` |  |
| `int getIndex(long)` |  |
| `String getLabel(int)` |  |

---

### `interface DownloadListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onDownloadStart(String, String, String, String, long)` |  |

---

### `class GeolocationPermissions`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void allow(String)` |  |
| `void clear(String)` |  |
| `void clearAll()` |  |
| `void getAllowed(String, android.webkit.ValueCallback<java.lang.Boolean>)` |  |
| `static android.webkit.GeolocationPermissions getInstance()` |  |
| `void getOrigins(android.webkit.ValueCallback<java.util.Set<java.lang.String>>)` |  |

---

### `interface static GeolocationPermissions.Callback`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void invoke(String, boolean, boolean)` |  |

---

### `class HttpAuthHandler`

- **Extends:** `android.os.Handler`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `void proceed(String, String)` |  |
| `boolean useHttpAuthUsernamePassword()` |  |

---

### `class JsPromptResult`

- **Extends:** `android.webkit.JsResult`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void confirm(String)` |  |

---

### `class JsResult`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `final void cancel()` |  |
| `final void confirm()` |  |

---

### `class MimeTypeMap`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String getFileExtensionFromUrl(String)` |  |
| `static android.webkit.MimeTypeMap getSingleton()` |  |
| `boolean hasExtension(String)` |  |
| `boolean hasMimeType(String)` |  |

---

### `class abstract PermissionRequest`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `PermissionRequest()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final String RESOURCE_AUDIO_CAPTURE = "android.webkit.resource.AUDIO_CAPTURE"` |  |
| `static final String RESOURCE_MIDI_SYSEX = "android.webkit.resource.MIDI_SYSEX"` |  |
| `static final String RESOURCE_PROTECTED_MEDIA_ID = "android.webkit.resource.PROTECTED_MEDIA_ID"` |  |
| `static final String RESOURCE_VIDEO_CAPTURE = "android.webkit.resource.VIDEO_CAPTURE"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void deny()` |  |
| `abstract android.net.Uri getOrigin()` |  |
| `abstract String[] getResources()` |  |
| `abstract void grant(String[])` |  |

---

### `interface PluginStub`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.view.View getEmbeddedView(int, android.content.Context)` |  |
| `android.view.View getFullScreenView(int, android.content.Context)` |  |

---

### `class abstract RenderProcessGoneDetail`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract boolean didCrash()` |  |
| `abstract int rendererPriorityAtExit()` |  |

---

### `class abstract SafeBrowsingResponse`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void backToSafety(boolean)` |  |
| `abstract void proceed(boolean)` |  |
| `abstract void showInterstitial(boolean)` |  |

---

### `class ServiceWorkerClient`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ServiceWorkerClient()` |  |

---

### `class abstract ServiceWorkerController`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void setServiceWorkerClient(@Nullable android.webkit.ServiceWorkerClient)` |  |

---

### `class abstract ServiceWorkerWebSettings`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `ServiceWorkerWebSettings()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract boolean getAllowContentAccess()` |  |
| `abstract boolean getAllowFileAccess()` |  |
| `abstract boolean getBlockNetworkLoads()` |  |
| `abstract int getCacheMode()` |  |
| `abstract void setAllowContentAccess(boolean)` |  |
| `abstract void setAllowFileAccess(boolean)` |  |
| `abstract void setBlockNetworkLoads(boolean)` |  |
| `abstract void setCacheMode(int)` |  |

---

### `class SslErrorHandler`

- **Extends:** `android.os.Handler`

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void cancel()` |  |
| `void proceed()` |  |

---

### `class TracingConfig`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int CATEGORIES_ALL = 1` |  |
| `static final int CATEGORIES_ANDROID_WEBVIEW = 2` |  |
| `static final int CATEGORIES_FRAME_VIEWER = 64` |  |
| `static final int CATEGORIES_INPUT_LATENCY = 8` |  |
| `static final int CATEGORIES_JAVASCRIPT_AND_RENDERING = 32` |  |
| `static final int CATEGORIES_NONE = 0` |  |
| `static final int CATEGORIES_RENDERING = 16` |  |
| `static final int CATEGORIES_WEB_DEVELOPER = 4` |  |
| `static final int RECORD_CONTINUOUSLY = 1` |  |
| `static final int RECORD_UNTIL_FULL = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getPredefinedCategories()` |  |
| `int getTracingMode()` |  |

---

### `class static TracingConfig.Builder`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `TracingConfig.Builder()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `android.webkit.TracingConfig.Builder addCategories(int...)` |  |
| `android.webkit.TracingConfig.Builder addCategories(java.lang.String...)` |  |
| `android.webkit.TracingConfig.Builder addCategories(java.util.Collection<java.lang.String>)` |  |
| `android.webkit.TracingConfig build()` |  |
| `android.webkit.TracingConfig.Builder setTracingMode(int)` |  |

---

### `class abstract TracingController`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract boolean isTracing()` |  |
| `abstract void start(@NonNull android.webkit.TracingConfig)` |  |
| `abstract boolean stop(@Nullable java.io.OutputStream, @NonNull java.util.concurrent.Executor)` |  |

---

### `class final URLUtil`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `URLUtil()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `static String composeSearchUrl(String, String, String)` |  |
| `static byte[] decode(byte[]) throws java.lang.IllegalArgumentException` |  |
| `static String guessFileName(String, @Nullable String, @Nullable String)` |  |
| `static String guessUrl(String)` |  |
| `static boolean isAboutUrl(String)` |  |
| `static boolean isAssetUrl(String)` |  |
| `static boolean isContentUrl(String)` |  |
| `static boolean isDataUrl(String)` |  |
| `static boolean isFileUrl(String)` |  |
| `static boolean isHttpUrl(String)` |  |
| `static boolean isHttpsUrl(String)` |  |
| `static boolean isJavaScriptUrl(String)` |  |
| `static boolean isNetworkUrl(String)` |  |
| `static boolean isValidUrl(String)` |  |
| `static String stripAnchor(String)` |  |

---

### `interface ValueCallback<T>`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onReceiveValue(T)` |  |

---

### `class abstract WebBackForwardList`

- **Implements:** `java.lang.Cloneable java.io.Serializable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebBackForwardList()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract android.webkit.WebBackForwardList clone()` |  |
| `abstract int getCurrentIndex()` |  |
| `abstract android.webkit.WebHistoryItem getItemAtIndex(int)` |  |
| `abstract int getSize()` |  |

---

### `class WebChromeClient`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebChromeClient()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void getVisitedHistory(android.webkit.ValueCallback<java.lang.String[]>)` |  |
| `void onCloseWindow(android.webkit.WebView)` |  |
| `boolean onConsoleMessage(android.webkit.ConsoleMessage)` |  |
| `boolean onCreateWindow(android.webkit.WebView, boolean, boolean, android.os.Message)` |  |
| `void onGeolocationPermissionsHidePrompt()` |  |
| `void onGeolocationPermissionsShowPrompt(String, android.webkit.GeolocationPermissions.Callback)` |  |
| `void onHideCustomView()` |  |
| `boolean onJsAlert(android.webkit.WebView, String, String, android.webkit.JsResult)` |  |
| `boolean onJsBeforeUnload(android.webkit.WebView, String, String, android.webkit.JsResult)` |  |
| `boolean onJsConfirm(android.webkit.WebView, String, String, android.webkit.JsResult)` |  |
| `boolean onJsPrompt(android.webkit.WebView, String, String, String, android.webkit.JsPromptResult)` |  |
| `void onPermissionRequest(android.webkit.PermissionRequest)` |  |
| `void onPermissionRequestCanceled(android.webkit.PermissionRequest)` |  |
| `void onProgressChanged(android.webkit.WebView, int)` |  |
| `void onReceivedIcon(android.webkit.WebView, android.graphics.Bitmap)` |  |
| `void onReceivedTitle(android.webkit.WebView, String)` |  |
| `void onReceivedTouchIconUrl(android.webkit.WebView, String, boolean)` |  |
| `void onRequestFocus(android.webkit.WebView)` |  |
| `void onShowCustomView(android.view.View, android.webkit.WebChromeClient.CustomViewCallback)` |  |
| `boolean onShowFileChooser(android.webkit.WebView, android.webkit.ValueCallback<android.net.Uri[]>, android.webkit.WebChromeClient.FileChooserParams)` |  |

---

### `interface static WebChromeClient.CustomViewCallback`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebChromeClient.FileChooserParams()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int MODE_OPEN = 0` |  |
| `static final int MODE_OPEN_MULTIPLE = 1` |  |
| `static final int MODE_SAVE = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onCustomViewHidden()` |  |
| `abstract android.content.Intent createIntent()` |  |
| `abstract String[] getAcceptTypes()` |  |
| `abstract int getMode()` |  |
| `abstract boolean isCaptureEnabled()` |  |

---

### `class abstract WebHistoryItem`

- **Implements:** `java.lang.Cloneable`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebHistoryItem()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract android.webkit.WebHistoryItem clone()` |  |
| `abstract String getOriginalUrl()` |  |
| `abstract String getTitle()` |  |
| `abstract String getUrl()` |  |

---

### `class abstract WebIconDatabase` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `interface static WebIconDatabase.IconListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class WebMessage`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebMessage(String)` |  |
| `WebMessage(String, android.webkit.WebMessagePort[])` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getData()` |  |

---

### `class abstract WebMessagePort`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebMessagePort.WebMessageCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void close()` |  |
| `abstract void postMessage(android.webkit.WebMessage)` |  |
| `abstract void setWebMessageCallback(android.webkit.WebMessagePort.WebMessageCallback)` |  |
| `abstract void setWebMessageCallback(android.webkit.WebMessagePort.WebMessageCallback, android.os.Handler)` |  |
| `void onMessage(android.webkit.WebMessagePort, android.webkit.WebMessage)` |  |

---

### `class abstract WebResourceError`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract CharSequence getDescription()` |  |
| `abstract int getErrorCode()` |  |

---

### `interface WebResourceRequest`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getMethod()` |  |
| `java.util.Map<java.lang.String,java.lang.String> getRequestHeaders()` |  |
| `android.net.Uri getUrl()` |  |
| `boolean hasGesture()` |  |
| `boolean isForMainFrame()` |  |
| `boolean isRedirect()` |  |

---

### `class WebResourceResponse`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebResourceResponse(String, String, java.io.InputStream)` |  |
| `WebResourceResponse(String, String, int, @NonNull String, java.util.Map<java.lang.String,java.lang.String>, java.io.InputStream)` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `java.io.InputStream getData()` |  |
| `String getEncoding()` |  |
| `String getMimeType()` |  |
| `String getReasonPhrase()` |  |
| `java.util.Map<java.lang.String,java.lang.String> getResponseHeaders()` |  |
| `int getStatusCode()` |  |
| `void setData(java.io.InputStream)` |  |
| `void setEncoding(String)` |  |
| `void setMimeType(String)` |  |
| `void setResponseHeaders(java.util.Map<java.lang.String,java.lang.String>)` |  |
| `void setStatusCodeAndReasonPhrase(int, @NonNull String)` |  |

---

### `class abstract WebSettings`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebSettings()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int FORCE_DARK_AUTO = 1` |  |
| `static final int FORCE_DARK_OFF = 0` |  |
| `static final int FORCE_DARK_ON = 2` |  |
| `static final int LOAD_CACHE_ELSE_NETWORK = 1` |  |
| `static final int LOAD_CACHE_ONLY = 3` |  |
| `static final int LOAD_DEFAULT = -1` |  |
| `static final int LOAD_NO_CACHE = 2` |  |
| `static final int MENU_ITEM_NONE = 0` |  |
| `static final int MENU_ITEM_PROCESS_TEXT = 4` |  |
| `static final int MENU_ITEM_SHARE = 1` |  |
| `static final int MENU_ITEM_WEB_SEARCH = 2` |  |
| `static final int MIXED_CONTENT_ALWAYS_ALLOW = 0` |  |
| `static final int MIXED_CONTENT_COMPATIBILITY_MODE = 2` |  |
| `static final int MIXED_CONTENT_NEVER_ALLOW = 1` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract boolean getAllowContentAccess()` |  |
| `abstract boolean getAllowFileAccess()` |  |
| `abstract boolean getAllowFileAccessFromFileURLs()` |  |
| `abstract boolean getAllowUniversalAccessFromFileURLs()` |  |
| `abstract boolean getBlockNetworkImage()` |  |
| `abstract boolean getBlockNetworkLoads()` |  |
| `abstract boolean getBuiltInZoomControls()` |  |
| `abstract int getCacheMode()` |  |
| `abstract String getCursiveFontFamily()` |  |
| `abstract boolean getDatabaseEnabled()` |  |
| `abstract int getDefaultFixedFontSize()` |  |
| `abstract int getDefaultFontSize()` |  |
| `abstract String getDefaultTextEncodingName()` |  |
| `static String getDefaultUserAgent(android.content.Context)` |  |
| `abstract int getDisabledActionModeMenuItems()` |  |
| `abstract boolean getDisplayZoomControls()` |  |
| `abstract boolean getDomStorageEnabled()` |  |
| `abstract String getFantasyFontFamily()` |  |
| `abstract String getFixedFontFamily()` |  |
| `int getForceDark()` |  |
| `abstract boolean getJavaScriptCanOpenWindowsAutomatically()` |  |
| `abstract boolean getJavaScriptEnabled()` |  |
| `abstract android.webkit.WebSettings.LayoutAlgorithm getLayoutAlgorithm()` |  |
| `abstract boolean getLoadWithOverviewMode()` |  |
| `abstract boolean getLoadsImagesAutomatically()` |  |
| `abstract boolean getMediaPlaybackRequiresUserGesture()` |  |
| `abstract int getMinimumFontSize()` |  |
| `abstract int getMinimumLogicalFontSize()` |  |
| `abstract int getMixedContentMode()` |  |
| `abstract boolean getOffscreenPreRaster()` |  |
| `abstract boolean getSafeBrowsingEnabled()` |  |
| `abstract String getSansSerifFontFamily()` |  |
| `abstract String getSerifFontFamily()` |  |
| `abstract String getStandardFontFamily()` |  |
| `abstract int getTextZoom()` |  |
| `abstract boolean getUseWideViewPort()` |  |
| `abstract String getUserAgentString()` |  |
| `abstract void setAllowContentAccess(boolean)` |  |
| `abstract void setAllowFileAccess(boolean)` |  |
| `abstract void setBlockNetworkImage(boolean)` |  |
| `abstract void setBlockNetworkLoads(boolean)` |  |
| `abstract void setBuiltInZoomControls(boolean)` |  |
| `abstract void setCacheMode(int)` |  |
| `abstract void setCursiveFontFamily(String)` |  |
| `abstract void setDatabaseEnabled(boolean)` |  |
| `abstract void setDefaultFixedFontSize(int)` |  |
| `abstract void setDefaultFontSize(int)` |  |
| `abstract void setDefaultTextEncodingName(String)` |  |
| `abstract void setDisabledActionModeMenuItems(int)` |  |
| `abstract void setDisplayZoomControls(boolean)` |  |
| `abstract void setDomStorageEnabled(boolean)` |  |
| `abstract void setFantasyFontFamily(String)` |  |
| `abstract void setFixedFontFamily(String)` |  |
| `void setForceDark(int)` |  |
| `abstract void setGeolocationEnabled(boolean)` |  |
| `abstract void setJavaScriptCanOpenWindowsAutomatically(boolean)` |  |
| `abstract void setJavaScriptEnabled(boolean)` |  |
| `abstract void setLayoutAlgorithm(android.webkit.WebSettings.LayoutAlgorithm)` |  |
| `abstract void setLoadWithOverviewMode(boolean)` |  |
| `abstract void setLoadsImagesAutomatically(boolean)` |  |
| `abstract void setMediaPlaybackRequiresUserGesture(boolean)` |  |
| `abstract void setMinimumFontSize(int)` |  |
| `abstract void setMinimumLogicalFontSize(int)` |  |
| `abstract void setMixedContentMode(int)` |  |
| `abstract void setNeedInitialFocus(boolean)` |  |
| `abstract void setOffscreenPreRaster(boolean)` |  |
| `abstract void setSafeBrowsingEnabled(boolean)` |  |
| `abstract void setSansSerifFontFamily(String)` |  |
| `abstract void setSerifFontFamily(String)` |  |
| `abstract void setStandardFontFamily(String)` |  |
| `abstract void setSupportMultipleWindows(boolean)` |  |
| `abstract void setSupportZoom(boolean)` |  |
| `abstract void setTextZoom(int)` |  |
| `abstract void setUseWideViewPort(boolean)` |  |
| `abstract void setUserAgentString(@Nullable String)` |  |
| `abstract boolean supportMultipleWindows()` |  |
| `abstract boolean supportZoom()` |  |

---

### `enum WebSettings.LayoutAlgorithm`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.webkit.WebSettings.LayoutAlgorithm NORMAL` |  |
| `static final android.webkit.WebSettings.LayoutAlgorithm TEXT_AUTOSIZING` |  |

---

### `enum WebSettings.PluginState`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.webkit.WebSettings.PluginState OFF` |  |
| `static final android.webkit.WebSettings.PluginState ON` |  |
| `static final android.webkit.WebSettings.PluginState ON_DEMAND` |  |

---

### `enum WebSettings.RenderPriority`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.webkit.WebSettings.RenderPriority HIGH` |  |
| `static final android.webkit.WebSettings.RenderPriority LOW` |  |
| `static final android.webkit.WebSettings.RenderPriority NORMAL` |  |

---

### `enum WebSettings.TextSize` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `enum WebSettings.ZoomDensity`


#### Enum Constants

| Constant | Deprecated |
|----------|:----------:|
| `static final android.webkit.WebSettings.ZoomDensity CLOSE` |  |
| `static final android.webkit.WebSettings.ZoomDensity FAR` |  |
| `static final android.webkit.WebSettings.ZoomDensity MEDIUM` |  |

---

### `class WebStorage`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void deleteAllData()` |  |
| `void deleteOrigin(String)` |  |
| `static android.webkit.WebStorage getInstance()` |  |
| `void getOrigins(android.webkit.ValueCallback<java.util.Map>)` |  |
| `void getQuotaForOrigin(String, android.webkit.ValueCallback<java.lang.Long>)` |  |
| `void getUsageForOrigin(String, android.webkit.ValueCallback<java.lang.Long>)` |  |

---

### `class static WebStorage.Origin`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `String getOrigin()` |  |
| `long getQuota()` |  |
| `long getUsage()` |  |

---

### `interface static WebStorage.QuotaUpdater` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

---

### `class WebView`

- **Extends:** `android.widget.AbsoluteLayout`
- **Implements:** `android.view.ViewGroup.OnHierarchyChangeListener android.view.ViewTreeObserver.OnGlobalFocusChangeListener`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebView(@NonNull android.content.Context)` |  |
| `WebView(@NonNull android.content.Context, @Nullable android.util.AttributeSet)` |  |
| `WebView(@NonNull android.content.Context, @Nullable android.util.AttributeSet, int)` |  |
| `WebView(@NonNull android.content.Context, @Nullable android.util.AttributeSet, int, int)` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int RENDERER_PRIORITY_BOUND = 1` |  |
| `static final int RENDERER_PRIORITY_IMPORTANT = 2` |  |
| `static final int RENDERER_PRIORITY_WAIVED = 0` |  |
| `static final String SCHEME_GEO = "geo:0,0?q="` |  |
| `static final String SCHEME_MAILTO = "mailto:"` |  |
| `static final String SCHEME_TEL = "tel:"` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void addJavascriptInterface(@NonNull Object, @NonNull String)` |  |
| `boolean canGoBack()` |  |
| `boolean canGoBackOrForward(int)` |  |
| `boolean canGoForward()` |  |
| `void clearCache(boolean)` |  |
| `static void clearClientCertPreferences(@Nullable Runnable)` |  |
| `void clearFormData()` |  |
| `void clearHistory()` |  |
| `void clearMatches()` |  |
| `void clearSslPreferences()` |  |
| `void destroy()` |  |
| `static void disableWebView()` |  |
| `void documentHasImages(@NonNull android.os.Message)` |  |
| `static void enableSlowWholeDocumentDraw()` |  |
| `void evaluateJavascript(@NonNull String, @Nullable android.webkit.ValueCallback<java.lang.String>)` |  |
| `void findAllAsync(@NonNull String)` |  |
| `void findNext(boolean)` |  |
| `void flingScroll(int, int)` |  |
| `int getProgress()` |  |
| `boolean getRendererPriorityWaivedWhenNotVisible()` |  |
| `int getRendererRequestedPriority()` |  |
| `void goBack()` |  |
| `void goBackOrForward(int)` |  |
| `void goForward()` |  |
| `void invokeZoomPicker()` |  |
| `boolean isPrivateBrowsingEnabled()` |  |
| `void loadData(@NonNull String, @Nullable String, @Nullable String)` |  |
| `void loadDataWithBaseURL(@Nullable String, @NonNull String, @Nullable String, @Nullable String, @Nullable String)` |  |
| `void loadUrl(@NonNull String, @NonNull java.util.Map<java.lang.String,java.lang.String>)` |  |
| `void loadUrl(@NonNull String)` |  |
| `void onPause()` |  |
| `void onResume()` |  |
| `boolean pageDown(boolean)` |  |
| `boolean pageUp(boolean)` |  |
| `void pauseTimers()` |  |
| `void postUrl(@NonNull String, @NonNull byte[])` |  |
| `void postVisualStateCallback(long, @NonNull android.webkit.WebView.VisualStateCallback)` |  |
| `void postWebMessage(@NonNull android.webkit.WebMessage, @NonNull android.net.Uri)` |  |
| `void reload()` |  |
| `void removeJavascriptInterface(@NonNull String)` |  |
| `void requestFocusNodeHref(@Nullable android.os.Message)` |  |
| `void requestImageRef(@NonNull android.os.Message)` |  |
| `void resumeTimers()` |  |
| `void saveWebArchive(@NonNull String)` |  |
| `void saveWebArchive(@NonNull String, boolean, @Nullable android.webkit.ValueCallback<java.lang.String>)` |  |
| `static void setDataDirectorySuffix(@NonNull String)` |  |
| `void setDownloadListener(@Nullable android.webkit.DownloadListener)` |  |
| `void setFindListener(@Nullable android.webkit.WebView.FindListener)` |  |
| `void setInitialScale(int)` |  |
| `void setNetworkAvailable(boolean)` |  |
| `void setRendererPriorityPolicy(int, boolean)` |  |
| `static void setSafeBrowsingWhitelist(@NonNull java.util.List<java.lang.String>, @Nullable android.webkit.ValueCallback<java.lang.Boolean>)` |  |
| `void setTextClassifier(@Nullable android.view.textclassifier.TextClassifier)` |  |
| `void setWebChromeClient(@Nullable android.webkit.WebChromeClient)` |  |
| `static void setWebContentsDebuggingEnabled(boolean)` |  |
| `void setWebViewClient(@NonNull android.webkit.WebViewClient)` |  |
| `void setWebViewRenderProcessClient(@NonNull java.util.concurrent.Executor, @NonNull android.webkit.WebViewRenderProcessClient)` |  |
| `void setWebViewRenderProcessClient(@Nullable android.webkit.WebViewRenderProcessClient)` |  |
| `static void startSafeBrowsing(@NonNull android.content.Context, @Nullable android.webkit.ValueCallback<java.lang.Boolean>)` |  |
| `void stopLoading()` |  |
| `void zoomBy(float)` |  |
| `boolean zoomIn()` |  |
| `boolean zoomOut()` |  |

---

### `interface static WebView.FindListener`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void onFindResultReceived(int, int, boolean)` |  |

---

### `class static WebView.HitTestResult`


#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int EDIT_TEXT_TYPE = 9` |  |
| `static final int EMAIL_TYPE = 4` |  |
| `static final int GEO_TYPE = 3` |  |
| `static final int IMAGE_TYPE = 5` |  |
| `static final int PHONE_TYPE = 2` |  |
| `static final int SRC_ANCHOR_TYPE = 7` |  |
| `static final int SRC_IMAGE_ANCHOR_TYPE = 8` |  |
| `static final int UNKNOWN_TYPE = 0` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `int getType()` |  |

---

### `interface static WebView.PictureListener` ~~DEPRECATED~~

- **Annotations:** `@Deprecated`

#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebView.VisualStateCallback()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void onComplete(long)` |  |

---

### `class WebView.WebViewTransport`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebView.WebViewTransport()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void setWebView(@Nullable android.webkit.WebView)` |  |

---

### `class WebViewClient`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebViewClient()` |  |

#### Fields / Constants

| Signature | Deprecated |
|-----------|:----------:|
| `static final int ERROR_AUTHENTICATION = -4` |  |
| `static final int ERROR_BAD_URL = -12` |  |
| `static final int ERROR_CONNECT = -6` |  |
| `static final int ERROR_FAILED_SSL_HANDSHAKE = -11` |  |
| `static final int ERROR_FILE = -13` |  |
| `static final int ERROR_FILE_NOT_FOUND = -14` |  |
| `static final int ERROR_HOST_LOOKUP = -2` |  |
| `static final int ERROR_IO = -7` |  |
| `static final int ERROR_PROXY_AUTHENTICATION = -5` |  |
| `static final int ERROR_REDIRECT_LOOP = -9` |  |
| `static final int ERROR_TIMEOUT = -8` |  |
| `static final int ERROR_TOO_MANY_REQUESTS = -15` |  |
| `static final int ERROR_UNKNOWN = -1` |  |
| `static final int ERROR_UNSAFE_RESOURCE = -16` |  |
| `static final int ERROR_UNSUPPORTED_AUTH_SCHEME = -3` |  |
| `static final int ERROR_UNSUPPORTED_SCHEME = -10` |  |
| `static final int SAFE_BROWSING_THREAT_BILLING = 4` |  |
| `static final int SAFE_BROWSING_THREAT_MALWARE = 1` |  |
| `static final int SAFE_BROWSING_THREAT_PHISHING = 2` |  |
| `static final int SAFE_BROWSING_THREAT_UNKNOWN = 0` |  |
| `static final int SAFE_BROWSING_THREAT_UNWANTED_SOFTWARE = 3` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `void doUpdateVisitedHistory(android.webkit.WebView, String, boolean)` |  |
| `void onFormResubmission(android.webkit.WebView, android.os.Message, android.os.Message)` |  |
| `void onLoadResource(android.webkit.WebView, String)` |  |
| `void onPageCommitVisible(android.webkit.WebView, String)` |  |
| `void onPageFinished(android.webkit.WebView, String)` |  |
| `void onPageStarted(android.webkit.WebView, String, android.graphics.Bitmap)` |  |
| `void onReceivedClientCertRequest(android.webkit.WebView, android.webkit.ClientCertRequest)` |  |
| `void onReceivedError(android.webkit.WebView, android.webkit.WebResourceRequest, android.webkit.WebResourceError)` |  |
| `void onReceivedHttpAuthRequest(android.webkit.WebView, android.webkit.HttpAuthHandler, String, String)` |  |
| `void onReceivedHttpError(android.webkit.WebView, android.webkit.WebResourceRequest, android.webkit.WebResourceResponse)` |  |
| `void onReceivedLoginRequest(android.webkit.WebView, String, @Nullable String, String)` |  |
| `void onReceivedSslError(android.webkit.WebView, android.webkit.SslErrorHandler, android.net.http.SslError)` |  |
| `boolean onRenderProcessGone(android.webkit.WebView, android.webkit.RenderProcessGoneDetail)` |  |
| `void onSafeBrowsingHit(android.webkit.WebView, android.webkit.WebResourceRequest, int, android.webkit.SafeBrowsingResponse)` |  |
| `void onScaleChanged(android.webkit.WebView, float, float)` |  |
| `void onUnhandledKeyEvent(android.webkit.WebView, android.view.KeyEvent)` |  |
| `boolean shouldOverrideKeyEvent(android.webkit.WebView, android.view.KeyEvent)` |  |
| `boolean shouldOverrideUrlLoading(android.webkit.WebView, android.webkit.WebResourceRequest)` |  |

---

### `class abstract WebViewDatabase`


#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void clearHttpAuthUsernamePassword()` |  |
| `static android.webkit.WebViewDatabase getInstance(android.content.Context)` |  |
| `abstract boolean hasHttpAuthUsernamePassword()` |  |
| `abstract void setHttpAuthUsernamePassword(String, String, String, String)` |  |

---

### `class WebViewFragment` ~~DEPRECATED~~

- **Extends:** `android.app.Fragment`
- **Annotations:** `@Deprecated`

---

### `class abstract WebViewRenderProcess`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebViewRenderProcess()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract boolean terminate()` |  |

---

### `class abstract WebViewRenderProcessClient`


#### Constructors

| Signature | Deprecated |
|-----------|:----------:|
| `WebViewRenderProcessClient()` |  |

#### Methods

| Signature | Deprecated |
|-----------|:----------:|
| `abstract void onRenderProcessResponsive(@NonNull android.webkit.WebView, @Nullable android.webkit.WebViewRenderProcess)` |  |
| `abstract void onRenderProcessUnresponsive(@NonNull android.webkit.WebView, @Nullable android.webkit.WebViewRenderProcess)` |  |

---

