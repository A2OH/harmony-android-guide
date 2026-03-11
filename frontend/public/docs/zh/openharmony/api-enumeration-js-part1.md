# OpenHarmony JS/TS SDK API 枚举 — 第1部分

**涵盖文件数：** 140

**API 元素总计：** 2033


### @ohos.InputMethodExtensionAbility (@ohos.InputMethodExtensionAbility.d.ts)
#### 类
- **InputMethodExtensionAbility**
  - `onCreate()`: void
  - `onDestroy()`: void
  - `context`: InputMethodExtensionContext

### @ohos.InputMethodExtensionContext (@ohos.InputMethodExtensionContext.d.ts)
#### 类
- **InputMethodExtensionContext**
  - `destroy()`: void
  - `destroy()`: Promise<void>

### @ohos.InputMethodSubtype (@ohos.InputMethodSubtype.d.ts)
#### 接口
- **InputMethodSubtype**
  - `readonly label?`: string
  - `readonly labelId?`: number
  - `readonly name`: string
  - `readonly id`: string
  - `readonly mode?`: 'upper' | 'lower'
  - `readonly locale`: string
  - `readonly language`: string
  - `readonly icon?`: string
  - `readonly iconId?`: number
  - `extra?`: object

### PiPWindow (@ohos.PiPWindow.d.ts)
#### 接口
- **PiPConfiguration**
  - `context`: BaseContext
  - `componentController`: XComponentController
  - `navigationId?`: string
  - `templateType?`: PiPTemplateType
  - `contentWidth?`: number
  - `contentHeight?`: number
- **PiPController**
  - `startPiP`: Promise<void>
  - `stopPiP`: Promise<void>
  - `setAutoStartEnabled`: void
  - `updateContentSize`: void
  - `off`: void
  - `off`: void
#### 枚举
- **PiPTemplateType**
- **PiPState**
  - `ABOUT_TO_START` = 1
  - `STARTED` = 2
  - `ABOUT_TO_STOP` = 3
  - `STOPPED` = 4
  - `ABOUT_TO_RESTORE` = 5
  - `ERROR` = 6
#### 函数
- `isPiPEnabled(): boolean`
- `create(config: PiPConfiguration): Promise<PiPController>`
#### 类型别名
- `PiPActionEventType` = PiPVideoActionEvent | PiPCallActionEvent | PiPMeetingActionEvent | PiPLiveActionEvent
- `PiPVideoActionEvent` = 'playbackStateChanged' | 'nextVideo' | 'previousVideo'
- `PiPCallActionEvent` = 'hangUp' | 'micStateChanged' | 'videoStateChanged'
- `PiPMeetingActionEvent` = 'hangUp' | 'voiceStateChanged' | 'videoStateChanged'
- `PiPLiveActionEvent` = 'playbackStateChanged'

### @ohos.UiTest (@ohos.UiTest.d.ts)
#### 接口
- **Point**
  - `readonly x`: number
  - `readonly y`: number
- **Rect**
  - `readonly left`: number
  - `readonly top`: number
  - `readonly right`: number
  - `readonly bottom`: number
- **WindowFilter**
  - `bundleName?`: string
  - `title?`: string
  - `focused?`: boolean
  - `actived?`: boolean
  - `active?`: boolean
- **UIElementInfo**
  - `readonly bundleName`: string
  - `readonly type`: string
  - `readonly text`: string
- **UIEventObserver**
  - `once`: void
  - `once`: void
#### 枚举
- **MatchPattern**
  - `EQUALS` = 0
  - `CONTAINS` = 1
  - `STARTS_WITH` = 2
  - `ENDS_WITH` = 3
- **WindowMode**
  - `FULLSCREEN` = 0
  - `PRIMARY` = 1
  - `SECONDARY` = 2
  - `FLOATING` = 3
- **ResizeDirection**
  - `LEFT` = 0
  - `RIGHT` = 1
  - `UP` = 2
  - `DOWN` = 3
  - `LEFT_UP` = 4
  - `LEFT_DOWN` = 5
  - `RIGHT_UP` = 6
  - `RIGHT_DOWN` = 7
- **DisplayRotation**
  - `ROTATION_0` = 0
  - `ROTATION_90` = 1
  - `ROTATION_180` = 2
  - `ROTATION_270` = 3
- **UiDirection**
  - `LEFT` = 0
  - `RIGHT` = 1
  - `UP` = 2
  - `DOWN` = 3
- **MouseButton**
  - `MOUSE_BUTTON_LEFT` = 0
  - `MOUSE_BUTTON_RIGHT` = 1
  - `MOUSE_BUTTON_MIDDLE` = 2
#### 类
- **By**
  - `text()`: By
  - `key()`: By
  - `id()`: By
  - `type()`: By
  - `clickable()`: By
  - `scrollable()`: By
  - `enabled()`: By
  - `focused()`: By
  - `selected()`: By
  - `isBefore()`: By
  - `isAfter()`: By
- **UiComponent**
  - `click()`: Promise<void>
  - `doubleClick()`: Promise<void>
  - `longClick()`: Promise<void>
  - `getId()`: Promise<number>
  - `getKey()`: Promise<string>
  - `getText()`: Promise<string>
  - `getType()`: Promise<string>
  - `isClickable()`: Promise<boolean>
  - `isScrollable()`: Promise<boolean>
  - `isEnabled()`: Promise<boolean>
  - `isFocused()`: Promise<boolean>
  - `isSelected()`: Promise<boolean>
  - `inputText()`: Promise<void>
  - `scrollSearch()`: Promise<UiComponent>
- **UiDriver**
  - `create()`: UiDriver
  - `delayMs()`: Promise<void>
  - `findComponent()`: Promise<UiComponent>
  - `findComponents()`: Promise<Array<UiComponent>>
  - `assertComponentExist()`: Promise<void>
  - `pressBack()`: Promise<void>
  - `triggerKey()`: Promise<void>
  - `click()`: Promise<void>
  - `doubleClick()`: Promise<void>
  - `longClick()`: Promise<void>
  - `swipe()`: Promise<void>
  - `screenCap()`: Promise<boolean>
- **On**
  - `text()`: On
  - `id()`: On
  - `type()`: On
  - `clickable()`: On
  - `longClickable()`: On
  - `scrollable()`: On
  - `enabled()`: On
  - `focused()`: On
  - `selected()`: On
  - `checked()`: On
  - `checkable()`: On
  - `isBefore()`: On
  - `isAfter()`: On
  - `within()`: On
  - `inWindow()`: On
  - `description()`: On
- **Component**
  - `click()`: Promise<void>
  - `doubleClick()`: Promise<void>
  - `longClick()`: Promise<void>
  - `getId()`: Promise<string>
  - `getText()`: Promise<string>
  - `getType()`: Promise<string>
  - `isClickable()`: Promise<boolean>
  - `isLongClickable()`: Promise<boolean>
  - `isScrollable()`: Promise<boolean>
  - `isEnabled()`: Promise<boolean>
  - `isFocused()`: Promise<boolean>
  - `isSelected()`: Promise<boolean>
  - `isChecked()`: Promise<boolean>
  - `isCheckable()`: Promise<boolean>
  - `inputText()`: Promise<void>
  - `clearText()`: Promise<void>
  - `scrollToTop()`: Promise<void>
  - `scrollToBottom()`: Promise<void>
  - `scrollSearch()`: Promise<Component>
  - `getBounds()`: Promise<Rect>
  - `getBoundsCenter()`: Promise<Point>
  - `dragTo()`: Promise<void>
  - `pinchOut()`: Promise<void>
  - `pinchIn()`: Promise<void>
  - `getDescription()`: Promise<string>
- **Driver**
  - `create()`: Driver
  - `delayMs()`: Promise<void>
  - `findComponent()`: Promise<Component>
  - `findWindow()`: Promise<UiWindow>
  - `waitForComponent()`: Promise<Component>
  - `findComponents()`: Promise<Array<Component>>
  - `assertComponentExist()`: Promise<void>
  - `pressBack()`: Promise<void>
  - `triggerKey()`: Promise<void>
  - `triggerCombineKeys()`: Promise<void>
  - `click()`: Promise<void>
  - `doubleClick()`: Promise<void>
  - `longClick()`: Promise<void>
  - `swipe()`: Promise<void>
  - `drag()`: Promise<void>
  - `screenCap()`: Promise<boolean>
  - `setDisplayRotation()`: Promise<void>
  - `getDisplayRotation()`: Promise<DisplayRotation>
  - `setDisplayRotationEnabled()`: Promise<void>
  - `getDisplaySize()`: Promise<Point>
  - `getDisplayDensity()`: Promise<Point>
  - `wakeUpDisplay()`: Promise<void>
  - `pressHome()`: Promise<void>
  - `waitForIdle()`: Promise<boolean>
  - `fling()`: Promise<void>
  - `injectMultiPointerAction()`: Promise<boolean>
  - `fling()`: Promise<void>
  - `mouseClick()`: Promise<void>
  - `mouseMoveTo()`: Promise<void>
  - `mouseScroll()`: Promise<void>
  - `mouseScroll()`: Promise<void>
  - `screenCapture()`: Promise<boolean>
  - `createUIEventObserver()`: UIEventObserver
  - `mouseDoubleClick()`: Promise<void>
  - `mouseLongClick()`: Promise<void>
  - `mouseMoveWithTrack()`: Promise<void>
  - `mouseDrag()`: Promise<void>
  - `inputText()`: Promise<void>
- **UiWindow**
  - `getBundleName()`: Promise<string>
  - `getBounds()`: Promise<Rect>
  - `getTitle()`: Promise<string>
  - `getWindowMode()`: Promise<WindowMode>
  - `isFocused()`: Promise<boolean>
  - `isActived()`: Promise<boolean>
  - `focus()`: Promise<void>
  - `moveTo()`: Promise<void>
  - `resize()`: Promise<void>
  - `split()`: Promise<void>
  - `maximize()`: Promise<void>
  - `minimize()`: Promise<void>
  - `resume()`: Promise<void>
  - `close()`: Promise<void>
  - `isActive()`: Promise<boolean>
- **PointerMatrix**
  - `create()`: PointerMatrix
  - `setPoint()`: void

### @ohos.WallpaperExtensionAbility (@ohos.WallpaperExtensionAbility.d.ts)
#### 类
- **WallpaperExtensionAbility**
  - `onCreate()`: void
  - `onWallpaperChange()`: void
  - `onDestroy()`: void

### @ohos.WorkSchedulerExtensionAbility (@ohos.WorkSchedulerExtensionAbility.d.ts)
#### 类
- **WorkSchedulerExtensionAbility**
  - `onWorkStart()`: void
  - `onWorkStop()`: void
  - `context`: WorkSchedulerExtensionContext
#### 类型别名
- `WorkSchedulerExtensionContext` = _WorkSchedulerExtensionContext

### ability (@ohos.ability.ability.d.ts)
#### 类型别名
- `DataAbilityHelper` = _DataAbilityHelper
- `PacMap` = _PacMap
- `DataAbilityOperation` = _DataAbilityOperation
- `DataAbilityResult` = _DataAbilityResult
- `AbilityResult` = _AbilityResult
- `ConnectOptions` = _ConnectOptions
- `StartAbilityParameter` = _StartAbilityParameter

### dataUriUtils (@ohos.ability.dataUriUtils.d.ts)
#### 函数
- `getId(uri: string): number`
- `attachId(uri: string, id: number): string`
- `deleteId(uri: string): string`
- `updateId(uri: string, id: number): string`

### @ohos.ability.errorCode (@ohos.ability.errorCode.d.ts)
#### 枚举
- **ErrorCode**
  - `PERMISSION_DENY` = -3
  - `ABILITY_NOT_FOUND` = -2
  - `INVALID_PARAMETER` = -1
  - `NO_ERROR` = 0

### featureAbility (@ohos.ability.featureAbility.d.ts)
#### 枚举
- **AbilityWindowConfiguration**
  - `WINDOW_MODE_UNDEFINED` = 0
  - `WINDOW_MODE_FULLSCREEN` = 1
  - `WINDOW_MODE_SPLIT_PRIMARY` = 100
  - `WINDOW_MODE_SPLIT_SECONDARY` = 101
  - `WINDOW_MODE_FLOATING` = 102
- **AbilityStartSetting**
  - `BOUNDS_KEY` = 'abilityBounds'
  - `WINDOW_MODE_KEY` = 'windowMode'
  - `DISPLAY_ID_KEY` = 'displayId'
- **ErrorCode**
  - `NO_ERROR` = 0
  - `INVALID_PARAMETER` = -1
  - `ABILITY_NOT_FOUND` = -2
  - `PERMISSION_DENY` = -3
- **DataAbilityOperationType**
  - `TYPE_INSERT` = 1
  - `TYPE_UPDATE` = 2
  - `TYPE_DELETE` = 3
  - `TYPE_ASSERT` = 4
#### 函数
- `getWant(callback: AsyncCallback<Want>): void`
- `getWant(): Promise<Want>`
- `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<number>): void`
- `startAbility(parameter: StartAbilityParameter): Promise<number>`
- `getContext(): Context`
- `startAbilityForResult(parameter: StartAbilityParameter, callback: AsyncCallback<AbilityResult>): void`
- `startAbilityForResult(parameter: StartAbilityParameter): Promise<AbilityResult>`
- `terminateSelfWithResult(parameter: AbilityResult, callback: AsyncCallback<void>): void`
- `terminateSelfWithResult(parameter: AbilityResult): Promise<void>`
- `terminateSelf(callback: AsyncCallback<void>): void`
- `terminateSelf(): Promise<void>`
- `acquireDataAbilityHelper(uri: string): DataAbilityHelper`
- `hasWindowFocus(callback: AsyncCallback<boolean>): void`
- `hasWindowFocus(): Promise<boolean>`
- `connectAbility(request: Want, options: ConnectOptions): number`
- `disconnectAbility(connection: number, callback: AsyncCallback<void>): void`
- `disconnectAbility(connection: number): Promise<void>`
- `getWindow(callback: AsyncCallback<window.Window>): void`
- `getWindow(): Promise<window.Window>`
#### 类型别名
- `Context` = _Context
- `AppVersionInfo` = _AppVersionInfo
- `ProcessInfo` = _ProcessInfo

### particleAbility (@ohos.ability.particleAbility.d.ts)
#### 枚举
- **ErrorCode**
  - `INVALID_PARAMETER` = -1
#### 函数
- `startAbility(parameter: StartAbilityParameter, callback: AsyncCallback<void>): void`
- `startAbility(parameter: StartAbilityParameter): Promise<void>`
- `terminateSelf(callback: AsyncCallback<void>): void`
- `terminateSelf(): Promise<void>`
- `acquireDataAbilityHelper(uri: string): DataAbilityHelper`
- `startBackgroundRunning(id: number, request: NotificationRequest, callback: AsyncCallback<void>): void`
- `startBackgroundRunning(id: number, request: NotificationRequest): Promise<void>`
- `cancelBackgroundRunning(callback: AsyncCallback<void>): void`
- `cancelBackgroundRunning(): Promise<void>`
- `connectAbility(request: Want, options: ConnectOptions): number`
- `disconnectAbility(connection: number, callback: AsyncCallback<void>): void`
- `disconnectAbility(connection: number): Promise<void>`

### wantConstant (@ohos.ability.wantConstant.d.ts)
#### 枚举
- **Action**
  - `ACTION_HOME` = 'ohos.want.action.home'
  - `ACTION_DIAL` = 'ohos.want.action.dial'
  - `ACTION_SEARCH` = 'ohos.want.action.search'
  - `ACTION_WIRELESS_SETTINGS` = 'ohos.settings.wireless'
  - `ACTION_MANAGE_APPLICATIONS_SETTINGS` = 'ohos.settings.manage.applications'
- **Entity**
  - `ENTITY_DEFAULT` = 'entity.system.default'
  - `ENTITY_HOME` = 'entity.system.home'
  - `ENTITY_VOICE` = 'entity.system.voice'
  - `ENTITY_BROWSABLE` = 'entity.system.browsable'
  - `ENTITY_VIDEO` = 'entity.system.video'
- **Flags**
  - `FLAG_AUTH_READ_URI_PERMISSION` = 0x00000001
  - `FLAG_AUTH_WRITE_URI_PERMISSION` = 0x00000002
  - `FLAG_ABILITY_FORWARD_RESULT` = 0x00000004
  - `FLAG_ABILITY_CONTINUATION` = 0x00000008
  - `FLAG_NOT_OHOS_COMPONENT` = 0x00000010
  - `FLAG_ABILITY_FORM_ENABLED` = 0x00000020
  - `FLAG_AUTH_PERSISTABLE_URI_PERMISSION` = 0x00000040
  - `FLAG_AUTH_PREFIX_URI_PERMISSION` = 0x00000080
  - `FLAG_ABILITYSLICE_MULTI_DEVICE` = 0x00000100
  - `FLAG_START_FOREGROUND_ABILITY` = 0x00000200
  - `FLAG_ABILITY_CONTINUATION_REVERSIBLE` = 0x00000400
  - `FLAG_INSTALL_ON_DEMAND` = 0x00000800
  - `FLAG_INSTALL_WITH_BACKGROUND_MODE` = 0x80000000

### abilityAccessCtrl (@ohos.abilityAccessCtrl.d.ts)
#### 接口
- **AtManager**
  - `verifyAccessToken`: Promise<GrantStatus>
  - `verifyAccessToken`: Promise<GrantStatus>
  - `verifyAccessTokenSync`: GrantStatus
  - `checkAccessToken`: Promise<GrantStatus>
  - `checkAccessTokenSync`: GrantStatus
  - `requestPermissionsFromUser`: void
  - `requestPermissionsFromUser`: Promise<PermissionRequestResult>
  - `grantUserGrantedPermission`: Promise<void>
  - `grantUserGrantedPermission`: void
  - `revokeUserGrantedPermission`: Promise<void>
  - `revokeUserGrantedPermission`: void
  - `getPermissionFlags`: Promise<number>
  - `getVersion`: Promise<number>
  - `on`: void
  - `off`: void
- **PermissionStateChangeInfo**
  - `change`: PermissionStateChangeType
  - `tokenID`: number
  - `permissionName`: Permissions
#### 枚举
- **GrantStatus**
  - `PERMISSION_DENIED` = -1
  - `PERMISSION_GRANTED` = 0
- **PermissionStateChangeType**
  - `PERMISSION_REVOKED_OPER` = 0
  - `PERMISSION_GRANTED_OPER` = 1
#### 函数
- `createAtManager(): AtManager`
#### 类型别名
- `PermissionRequestResult` = _PermissionRequestResult
- `Context` = _Context

### @ohos.accessibility.GesturePath (@ohos.accessibility.GesturePath.d.ts)
#### 类
- **GesturePath**
  - `points`: Array<GesturePoint>
  - `durationTime`: number

### @ohos.accessibility.GesturePoint (@ohos.accessibility.GesturePoint.d.ts)
#### 类
- **GesturePoint**
  - `positionX`: number
  - `positionY`: number

### config (@ohos.accessibility.config.d.ts)
#### 接口
- **Config**
  - `set`: Promise<void>
  - `set`: void
  - `get`: Promise<T>
  - `get`: void
  - `on`: void
  - `off`: void
#### 函数
- `enableAbility(name: string, capability: Array<accessibility.Capability>): Promise<void>`
- `enableAbility(
    name: string,
    capability: Array<accessibility.Capability>,
    callback: AsyncCallback<void>
  ): void`
- `disableAbility(name: string): Promise<void>`
- `disableAbility(name: string, callback: AsyncCallback<void>): void`
- `on(type: 'enabledAccessibilityExtensionListChange', callback: Callback<void>): void`
- `off(type: 'enabledAccessibilityExtensionListChange', callback?: Callback<void>): void`
#### 类型别名
- `DaltonizationColorFilter` = 'Normal' | 'Protanomaly' | 'Deuteranomaly' | 'Tritanomaly'
- `ClickResponseTime` = 'Short' | 'Medium' | 'Long'
- `RepeatClickInterval` = 'Shortest' | 'Short' | 'Medium' | 'Long' | 'Longest'

### accessibility (@ohos.accessibility.d.ts)
#### 接口
- **CaptionsManager**
  - `enabled`: boolean
  - `style`: CaptionsStyle
  - `on`: void
  - `on`: void
  - `off`: void
  - `off`: void
- **CaptionsStyle**
  - `fontFamily`: CaptionsFontFamily
  - `fontScale`: number
  - `fontColor`: number | string
  - `fontEdgeType`: CaptionsFontEdgeType
  - `backgroundColor`: number | string
  - `windowColor`: number | string
- **AccessibilityAbilityInfo**
  - `readonly id`: string
  - `readonly name`: string
  - `readonly bundleName`: string
  - `readonly targetBundleNames`: Array<string>
  - `readonly abilityTypes`: Array<AbilityType>
  - `readonly capabilities`: Array<Capability>
  - `readonly description`: string
  - `readonly eventTypes`: Array<EventType>
#### 函数
- `isOpenAccessibility(callback: AsyncCallback<boolean>): void`
- `isOpenAccessibility(): Promise<boolean>`
- `isOpenAccessibilitySync(): boolean`
- `isOpenTouchGuide(callback: AsyncCallback<boolean>): void`
- `isOpenTouchGuide(): Promise<boolean>`
- `isOpenTouchGuideSync(): boolean`
- `getAbilityLists(
    abilityType: AbilityType,
    stateType: AbilityState,
    callback: AsyncCallback<Array<AccessibilityAbilityInfo>>
  ): void`
- `getAbilityLists(abilityType: AbilityType, stateType: AbilityState): Promise<Array<AccessibilityAbilityInfo>>`
- `getAccessibilityExtensionList(
    abilityType: AbilityType,
    stateType: AbilityState
  ): Promise<Array<AccessibilityAbilityInfo>>`
- `getAccessibilityExtensionList(
    abilityType: AbilityType,
    stateType: AbilityState,
    callback: AsyncCallback<Array<AccessibilityAbilityInfo>>
  ): void`
- `sendEvent(event: EventInfo, callback: AsyncCallback<void>): void`
- `sendEvent(event: EventInfo): Promise<void>`
- `sendAccessibilityEvent(event: EventInfo, callback: AsyncCallback<void>): void`
- `sendAccessibilityEvent(event: EventInfo): Promise<void>`
- `on(type: 'accessibilityStateChange', callback: Callback<boolean>): void`
- `on(type: 'touchGuideStateChange', callback: Callback<boolean>): void`
- `off(type: 'accessibilityStateChange', callback?: Callback<boolean>): void`
- `off(type: 'touchGuideStateChange', callback?: Callback<boolean>): void`
- `getCaptionsManager(): CaptionsManager`
#### 类
- **EventInfo**
  - `type`: EventType
  - `windowUpdateType?`: WindowUpdateType
  - `bundleName`: string
  - `componentType?`: string
  - `pageId?`: number
  - `description?`: string
  - `triggerAction`: Action
  - `textMoveUnit?`: TextMoveUnit
  - `contents?`: Array<string>
  - `lastContent?`: string
  - `beginIndex?`: number
  - `currentIndex?`: number
  - `endIndex?`: number
  - `itemCount?`: number
#### 类型别名
- `AbilityType` = 'audible' | 'generic' | 'haptic' | 'spoken' | 'visual' | 'all'
- `Action` = 'accessibilityFocus' | 'clearAccessibilityFocus' | 'focus' | 'clearFocus' | 'clearSelection' |
  'click' | 'longClick' | 'cut' | 'copy' | 'paste' | 's
- `EventType` = 'accessibilityFocus' | 'accessibilityFocusClear' |
  'click' | 'longClick' | 'focus' | 'select' | 'hoverEnter' | 'hoverExit' |
  'textUpdate' | 'textS
- `WindowUpdateType` = 'add' | 'remove' | 'bounds' | 'active' | 'focus'
- `AbilityState` = 'enable' | 'disable' | 'install'
- `Capability` = 'retrieve' | 'touchGuide' | 'keyEventObserver' | 'zoom' | 'gesture'
- `TextMoveUnit` = 'char' | 'word' | 'line' | 'page' | 'paragraph'
- `CaptionsFontEdgeType` = 'none' | 'raised' | 'depressed' | 'uniform' | 'dropShadow'
- `CaptionsFontFamily` = 'default' | 'monospacedSerif' | 'serif' |
  'monospacedSansSerif' | 'sansSerif' | 'casual' | 'cursive' | 'smallCapitals'

### appAccount (@ohos.account.appAccount.d.ts)
#### 接口
- **AppAccountManager**
  - `addAccount`: void
  - `addAccount`: void
  - `addAccount`: Promise<void>
  - `createAccount`: void
  - `createAccount`: void
  - `createAccount`: Promise<void>
  - `addAccountImplicitly`: void
  - `createAccountImplicitly`: void
  - `createAccountImplicitly`: void
  - `deleteAccount`: void
  - `deleteAccount`: Promise<void>
  - `removeAccount`: void
  - `removeAccount`: Promise<void>
  - `disableAppAccess`: void
  - `disableAppAccess`: Promise<void>
  - `enableAppAccess`: void
  - `enableAppAccess`: Promise<void>
  - `setAppAccess`: void
  - `setAppAccess`: Promise<void>
  - `checkAppAccess`: void
  - `checkAppAccess`: Promise<boolean>
  - `checkAppAccountSyncEnable`: void
  - `checkAppAccountSyncEnable`: Promise<boolean>
  - `checkDataSyncEnabled`: void
  - `checkDataSyncEnabled`: Promise<boolean>
  - `setAccountCredential`: void
  - `setAccountCredential`: Promise<void>
  - `setCredential`: void
  - `setCredential`: Promise<void>
  - `setAccountExtraInfo`: void
  - `setAccountExtraInfo`: Promise<void>
  - `setAppAccountSyncEnable`: void
  - `setAppAccountSyncEnable`: Promise<void>
  - `setDataSyncEnabled`: void
  - `setDataSyncEnabled`: Promise<void>
  - `setAssociatedData`: void
  - `setAssociatedData`: Promise<void>
  - `setCustomData`: void
  - `setCustomData`: Promise<void>
  - `getAllAccessibleAccounts`: void
  - `getAllAccessibleAccounts`: Promise<Array<AppAccountInfo>>
  - `getAllAccounts`: void
  - `getAllAccounts`: Promise<Array<AppAccountInfo>>
  - `getAllAccounts`: void
  - `getAllAccounts`: Promise<Array<AppAccountInfo>>
  - `getAccountsByOwner`: void
  - `getAccountsByOwner`: Promise<Array<AppAccountInfo>>
  - `getAccountCredential`: void
  - `getAccountCredential`: Promise<string>
  - `getCredential`: void
  - `getCredential`: Promise<string>
  - `getAccountExtraInfo`: void
  - `getAccountExtraInfo`: Promise<string>
  - `getAssociatedData`: void
  - `getAssociatedData`: Promise<string>
  - `getCustomData`: void
  - `getCustomData`: Promise<string>
  - `getCustomDataSync`: string
  - `on`: void
  - `on`: void
  - `off`: void
  - `off`: void
  - `authenticate`: void
  - `auth`: void
  - `auth`: void
  - `getOAuthToken`: void
  - `getOAuthToken`: Promise<string>
  - `getAuthToken`: void
  - `getAuthToken`: Promise<string>
  - `setOAuthToken`: void
  - `setOAuthToken`: Promise<void>
  - `setAuthToken`: void
  - `setAuthToken`: Promise<void>
  - `deleteOAuthToken`: void
  - `deleteOAuthToken`: Promise<void>
  - `deleteAuthToken`: void
  - `deleteAuthToken`: Promise<void>
  - `setOAuthTokenVisibility`: void
  - `setOAuthTokenVisibility`: Promise<void>
  - `setAuthTokenVisibility`: void
  - `setAuthTokenVisibility`: Promise<void>
  - `checkOAuthTokenVisibility`: void
  - `checkOAuthTokenVisibility`: Promise<boolean>
  - `checkAuthTokenVisibility`: void
  - `checkAuthTokenVisibility`: Promise<boolean>
  - `getAllOAuthTokens`: void
  - `getAllOAuthTokens`: Promise<Array<OAuthTokenInfo>>
  - `getAllAuthTokens`: void
  - `getAllAuthTokens`: Promise<Array<AuthTokenInfo>>
  - `getOAuthList`: void
  - `getOAuthList`: Promise<Array<string>>
  - `getAuthList`: void
  - `getAuthList`: Promise<Array<string>>
  - `getAuthenticatorCallback`: void
  - `getAuthenticatorCallback`: Promise<AuthenticatorCallback>
  - `getAuthCallback`: void
  - `getAuthCallback`: Promise<AuthCallback>
  - `getAuthenticatorInfo`: void
  - `getAuthenticatorInfo`: Promise<AuthenticatorInfo>
  - `queryAuthenticatorInfo`: void
  - `queryAuthenticatorInfo`: Promise<AuthenticatorInfo>
  - `checkAccountLabels`: void
  - `checkAccountLabels`: Promise<boolean>
  - `deleteCredential`: void
  - `deleteCredential`: Promise<void>
  - `selectAccountsByOptions`: void
  - `selectAccountsByOptions`: Promise<Array<AppAccountInfo>>
  - `verifyCredential`: void
  - `verifyCredential`: void
  - `setAuthenticatorProperties`: void
  - `setAuthenticatorProperties`: void
- **AppAccountInfo**
  - `owner`: string
  - `name`: string
- **OAuthTokenInfo**
  - `authType`: string
  - `token`: string
- **AuthTokenInfo**
  - `authType`: string
  - `token`: string
  - `account?`: AppAccountInfo
- **AuthenticatorInfo**
  - `owner`: string
  - `iconId`: number
  - `labelId`: number
- **AuthResult**
  - `account?`: AppAccountInfo
  - `tokenInfo?`: AuthTokenInfo
- **CreateAccountOptions**
  - `customData?`: Record<string, string>
- **CreateAccountImplicitlyOptions**
  - `requiredLabels?`: Array<string>
  - `authType?`: string
  - `parameters?`: Record<string, Object>
- **SelectAccountsOptions**
  - `allowedAccounts?`: Array<AppAccountInfo>
  - `allowedOwners?`: Array<string>
  - `requiredLabels?`: Array<string>
- **VerifyCredentialOptions**
  - `credentialType?`: string
  - `credential?`: string
  - `parameters?`: Record<string, Object>
- **SetPropertiesOptions**
  - `properties?`: Record<string, Object>
  - `parameters?`: Record<string, Object>
- **AuthenticatorCallback**
  - `onResult`: (code: number, result: { [key: string]: any }) => void
  - `onRequestRedirected`: (request: Want) => void
- **AuthCallback**
  - `onResult`: (code: number, result?: AuthResult) => void
  - `onRequestRedirected`: (request: Want) => void
  - `onRequestContinued?`: () => void
#### 枚举
- **Constants**
  - `ACTION_ADD_ACCOUNT_IMPLICITLY` = 'addAccountImplicitly'
  - `ACTION_AUTHENTICATE` = 'authenticate'
  - `ACTION_CREATE_ACCOUNT_IMPLICITLY` = 'createAccountImplicitly'
  - `ACTION_AUTH` = 'auth'
  - `ACTION_VERIFY_CREDENTIAL` = 'verifyCredential'
  - `ACTION_SET_AUTHENTICATOR_PROPERTIES` = 'setAuthenticatorProperties'
  - `KEY_NAME` = 'name'
  - `KEY_OWNER` = 'owner'
  - `KEY_TOKEN` = 'token'
  - `KEY_ACTION` = 'action'
  - `KEY_AUTH_TYPE` = 'authType'
  - `KEY_SESSION_ID` = 'sessionId'
  - `KEY_CALLER_PID` = 'callerPid'
  - `KEY_CALLER_UID` = 'callerUid'
  - `KEY_CALLER_BUNDLE_NAME` = 'callerBundleName'
  - `KEY_REQUIRED_LABELS` = 'requiredLabels'
  - `KEY_BOOLEAN_RESULT` = 'booleanResult'
- **ResultCode**
  - `SUCCESS` = 0
  - `ERROR_ACCOUNT_NOT_EXIST` = 10001
  - `ERROR_APP_ACCOUNT_SERVICE_EXCEPTION` = 10002
  - `ERROR_INVALID_PASSWORD` = 10003
  - `ERROR_INVALID_REQUEST` = 10004
  - `ERROR_INVALID_RESPONSE` = 10005
  - `ERROR_NETWORK_EXCEPTION` = 10006
  - `ERROR_OAUTH_AUTHENTICATOR_NOT_EXIST` = 10007
  - `ERROR_OAUTH_CANCELED` = 10008
  - `ERROR_OAUTH_LIST_TOO_LARGE` = 10009
  - `ERROR_OAUTH_SERVICE_BUSY` = 10010
  - `ERROR_OAUTH_SERVICE_EXCEPTION` = 10011
  - `ERROR_OAUTH_SESSION_NOT_EXIST` = 10012
  - `ERROR_OAUTH_TIMEOUT` = 10013
  - `ERROR_OAUTH_TOKEN_NOT_EXIST` = 10014
  - `ERROR_OAUTH_TOKEN_TOO_MANY` = 10015
  - `ERROR_OAUTH_UNSUPPORT_ACTION` = 10016
  - `ERROR_OAUTH_UNSUPPORT_AUTH_TYPE` = 10017
  - `ERROR_PERMISSION_DENIED` = 10018
#### 函数
- `createAppAccountManager(): AppAccountManager`
#### 类
- **Authenticator**
  - `addAccountImplicitly()`: void
  - `createAccountImplicitly()`: void
  - `authenticate()`: void
  - `auth()`: void
  - `verifyCredential()`: void
  - `setProperties()`: void
  - `checkAccountLabels()`: void
  - `checkAccountRemovable()`: void
  - `getRemoteObject()`: rpc.RemoteObject

### distributedAccount (@ohos.account.distributedAccount.d.ts)
#### 接口
- **DistributedAccountAbility**
  - `queryOsAccountDistributedInfo`: void
  - `queryOsAccountDistributedInfo`: Promise<DistributedInfo>
  - `getOsAccountDistributedInfo`: void
  - `getOsAccountDistributedInfo`: Promise<DistributedInfo>
  - `getOsAccountDistributedInfoByLocalId`: void
  - `getOsAccountDistributedInfoByLocalId`: Promise<DistributedInfo>
  - `updateOsAccountDistributedInfo`: void
  - `updateOsAccountDistributedInfo`: Promise<void>
  - `setOsAccountDistributedInfo`: void
  - `setOsAccountDistributedInfo`: Promise<void>
  - `setOsAccountDistributedInfoByLocalId`: void
  - `setOsAccountDistributedInfoByLocalId`: Promise<void>
- **DistributedInfo**
  - `name`: string
  - `id`: string
  - `event`: string
  - `nickname?`: string
  - `avatar?`: string
  - `readonly status?`: DistributedAccountStatus
  - `scalableData?`: object
#### 枚举
- **DistributedAccountStatus**
  - `NOT_LOGGED_IN` = 0
  - `LOGGED_IN` = 1
#### 函数
- `getDistributedAccountAbility(): DistributedAccountAbility`

### osAccount (@ohos.account.osAccount.d.ts)
#### 接口
- **AccountManager**
  - `activateOsAccount`: void
  - `activateOsAccount`: Promise<void>
  - `isMultiOsAccountEnable`: void
  - `isMultiOsAccountEnable`: Promise<boolean>
  - `checkMultiOsAccountEnabled`: void
  - `checkMultiOsAccountEnabled`: Promise<boolean>
  - `isOsAccountActived`: void
  - `isOsAccountActived`: Promise<boolean>
  - `checkOsAccountActivated`: void
  - `checkOsAccountActivated`: Promise<boolean>
  - `isOsAccountActivated`: Promise<boolean>
  - `isOsAccountConstraintEnable`: void
  - `isOsAccountConstraintEnable`: Promise<boolean>
  - `checkOsAccountConstraintEnabled`: void
  - `checkOsAccountConstraintEnabled`: Promise<boolean>
  - `isOsAccountConstraintEnabled`: Promise<boolean>
  - `isOsAccountConstraintEnabled`: Promise<boolean>
  - `isTestOsAccount`: void
  - `isTestOsAccount`: Promise<boolean>
  - `checkOsAccountTestable`: void
  - `checkOsAccountTestable`: Promise<boolean>
  - `isOsAccountVerified`: void
  - `isOsAccountVerified`: void
  - `isOsAccountVerified`: Promise<boolean>
  - `checkOsAccountVerified`: void
  - `checkOsAccountVerified`: Promise<boolean>
  - `checkOsAccountVerified`: void
  - `checkOsAccountVerified`: Promise<boolean>
  - `isOsAccountUnlocked`: Promise<boolean>
  - `isOsAccountUnlocked`: Promise<boolean>
  - `removeOsAccount`: void
  - `removeOsAccount`: Promise<void>
  - `setOsAccountConstraints`: void
  - `setOsAccountConstraints`: Promise<void>
  - `setOsAccountName`: void
  - `setOsAccountName`: Promise<void>
  - `getCreatedOsAccountsCount`: void
  - `getCreatedOsAccountsCount`: Promise<number>
  - `getOsAccountCount`: void
  - `getOsAccountCount`: Promise<number>
  - `getOsAccountLocalIdFromProcess`: void
  - `getOsAccountLocalIdFromProcess`: Promise<number>
  - `getOsAccountLocalId`: void
  - `getOsAccountLocalId`: Promise<number>
  - `getOsAccountLocalIdFromUid`: void
  - `getOsAccountLocalIdFromUid`: Promise<number>
  - `getOsAccountLocalIdForUid`: void
  - `getOsAccountLocalIdForUid`: Promise<number>
  - `getOsAccountLocalIdForUidSync`: number
  - `getOsAccountLocalIdFromDomain`: void
  - `getOsAccountLocalIdFromDomain`: Promise<number>
  - `getOsAccountLocalIdForDomain`: void
  - `getOsAccountLocalIdForDomain`: Promise<number>
  - `queryMaxOsAccountNumber`: void
  - `queryMaxOsAccountNumber`: Promise<number>
  - `getOsAccountAllConstraints`: void
  - `getOsAccountAllConstraints`: Promise<Array<string>>
  - `getOsAccountConstraints`: void
  - `getOsAccountConstraints`: Promise<Array<string>>
  - `getEnabledOsAccountConstraints`: Promise<Array<string>>
  - `queryAllCreatedOsAccounts`: void
  - `queryAllCreatedOsAccounts`: Promise<Array<OsAccountInfo>>
  - `queryActivatedOsAccountIds`: void
  - `queryActivatedOsAccountIds`: Promise<Array<number>>
  - `getActivatedOsAccountLocalIds`: void
  - `getActivatedOsAccountLocalIds`: Promise<Array<number>>
  - `createOsAccount`: void
  - `createOsAccount`: Promise<OsAccountInfo>
  - `createOsAccountForDomain`: void
  - `createOsAccountForDomain`: Promise<OsAccountInfo>
  - `queryCurrentOsAccount`: void
  - `queryCurrentOsAccount`: Promise<OsAccountInfo>
  - `getCurrentOsAccount`: void
  - `getCurrentOsAccount`: Promise<OsAccountInfo>
  - `queryOsAccount`: Promise<OsAccountInfo>
  - `queryOsAccountById`: void
  - `queryOsAccountById`: Promise<OsAccountInfo>
  - `getOsAccountTypeFromProcess`: void
  - `getOsAccountTypeFromProcess`: Promise<OsAccountType>
  - `getOsAccountType`: void
  - `getOsAccountType`: Promise<OsAccountType>
  - `getDistributedVirtualDeviceId`: void
  - `getDistributedVirtualDeviceId`: Promise<string>
  - `queryDistributedVirtualDeviceId`: void
  - `queryDistributedVirtualDeviceId`: Promise<string>
  - `getOsAccountProfilePhoto`: void
  - `getOsAccountProfilePhoto`: Promise<string>
  - `setOsAccountProfilePhoto`: void
  - `setOsAccountProfilePhoto`: Promise<void>
  - `getOsAccountLocalIdBySerialNumber`: void
  - `getOsAccountLocalIdBySerialNumber`: Promise<number>
  - `getOsAccountLocalIdForSerialNumber`: void
  - `getOsAccountLocalIdForSerialNumber`: Promise<number>
  - `getSerialNumberByOsAccountLocalId`: void
  - `getSerialNumberByOsAccountLocalId`: Promise<number>
  - `getSerialNumberForOsAccountLocalId`: void
  - `getSerialNumberForOsAccountLocalId`: Promise<number>
  - `on`: void
  - `off`: void
  - `getBundleIdForUid`: void
  - `getBundleIdForUid`: Promise<number>
  - `getBundleIdForUidSync`: number
  - `isMainOsAccount`: void
  - `isMainOsAccount`: Promise<boolean>
  - `getOsAccountConstraintSourceTypes`: void
  - `getOsAccountConstraintSourceTypes`: Promise<Array<ConstraintSourceTypeInfo>>
- **OsAccountInfo**
  - `localId`: number
  - `localName`: string
  - `type`: OsAccountType
  - `constraints`: Array<string>
  - `isVerified`: boolean
  - `isUnlocked`: boolean
  - `photo`: string
  - `createTime`: number
  - `lastLoginTime`: number
  - `serialNumber`: number
  - `isActived`: boolean
  - `isActivated`: boolean
  - `isCreateCompleted`: boolean
  - `distributedInfo`: distributedAccount.DistributedInfo
  - `domainInfo`: DomainAccountInfo
- **DomainAccountInfo**
  - `domain`: string
  - `accountName`: string
  - `accountId?`: string
  - `isAuthenticated?`: boolean
- **AuthStatusInfo**
  - `remainTimes`: number
  - `freezingTime`: number
- **GetDomainAccessTokenOptions**
  - `domainAccountInfo`: DomainAccountInfo
  - `domainAccountToken`: Uint8Array
  - `businessParams`: Record<string, Object>
  - `callerUid`: number
- **GetDomainAccountInfoOptions**
  - `accountName`: string
  - `domain?`: string
- **GetDomainAccountInfoPluginOptions**
  - `callerUid`: number
- **DomainPlugin**
  - `auth`: void
  - `authWithPopup`: void
  - `authWithToken`: void
  - `getAccountInfo`: void
  - `getAuthStatusInfo`: void
  - `bindAccount`: void
  - `unbindAccount`: void
  - `isAccountTokenValid`: void
  - `getAccessToken`: void
- **IInputData**
  - `onSetData`: (authSubType: AuthSubType, data: Uint8Array) => void
- **IInputer**
  - `onGetData`: (authSubType: AuthSubType, callback: IInputData) => void
- **IUserAuthCallback**
  - `onResult`: (result: number, extraInfo: AuthResult) => void
  - `onAcquireInfo?`: (module: number, acquire: number, extraInfo: Uint8Array) => void
- **IIdmCallback**
  - `onResult`: (result: number, extraInfo: RequestResult) => void
  - `onAcquireInfo?`: (module: number, acquire: number, extraInfo: Uint8Array) => void
- **GetPropertyRequest**
  - `authType`: AuthType
  - `keys`: Array<GetPropertyType>
- **SetPropertyRequest**
  - `authType`: AuthType
  - `key`: SetPropertyType
  - `setInfo`: Uint8Array
- **ExecutorProperty**
  - `result`: number
  - `authSubType`: AuthSubType
  - `remainTimes?`: number
  - `freezingTime?`: number
  - `enrollmentProgress?`: string
  - `sensorInfo?`: string
- **AuthResult**
  - `token?`: Uint8Array
  - `remainTimes?`: number
  - `freezingTime?`: number
- **CredentialInfo**
  - `credType`: AuthType
  - `credSubType`: AuthSubType
  - `token`: Uint8Array
- **RequestResult**
  - `credentialId?`: Uint8Array
- **EnrolledCredInfo**
  - `credentialId`: Uint8Array
  - `authType`: AuthType
  - `authSubType`: AuthSubType
  - `templateId`: Uint8Array
- **ConstraintSourceTypeInfo**
  - `localId`: number
  - `type`: ConstraintSourceType
#### 枚举
- **OsAccountType**
  - `ADMIN` = 0
- **GetPropertyType**
  - `AUTH_SUB_TYPE` = 1
  - `REMAIN_TIMES` = 2
  - `FREEZING_TIME` = 3
  - `ENROLLMENT_PROGRESS` = 4
  - `SENSOR_INFO` = 5
- **SetPropertyType**
  - `INIT_ALGORITHM` = 1
- **AuthType**
  - `PIN` = 1
  - `FACE` = 2
  - `FINGERPRINT` = 4
  - `DOMAIN` = 1024
- **AuthSubType**
  - `PIN_SIX` = 10000
  - `PIN_NUMBER` = 10001
  - `PIN_MIXED` = 10002
  - `FACE_2D` = 20000
  - `FACE_3D` = 20001
  - `FINGERPRINT_CAPACITIVE` = 30000
  - `FINGERPRINT_OPTICAL` = 30001
  - `FINGERPRINT_ULTRASONIC` = 30002
  - `DOMAIN_MIXED` = 10240001
- **AuthTrustLevel**
  - `ATL1` = 10000
  - `ATL2` = 20000
  - `ATL3` = 30000
  - `ATL4` = 40000
- **Module**
  - `FACE_AUTH` = 1
- **ResultCode**
  - `SUCCESS` = 0
  - `FAIL` = 1
  - `GENERAL_ERROR` = 2
  - `CANCELED` = 3
  - `TIMEOUT` = 4
  - `TYPE_NOT_SUPPORT` = 5
  - `TRUST_LEVEL_NOT_SUPPORT` = 6
  - `BUSY` = 7
  - `INVALID_PARAMETERS` = 8
  - `LOCKED` = 9
  - `NOT_ENROLLED` = 10
- **FaceTipsCode**
  - `FACE_AUTH_TIP_TOO_BRIGHT` = 1
  - `FACE_AUTH_TIP_TOO_DARK` = 2
  - `FACE_AUTH_TIP_TOO_CLOSE` = 3
  - `FACE_AUTH_TIP_TOO_FAR` = 4
  - `FACE_AUTH_TIP_TOO_HIGH` = 5
  - `FACE_AUTH_TIP_TOO_LOW` = 6
  - `FACE_AUTH_TIP_TOO_RIGHT` = 7
  - `FACE_AUTH_TIP_TOO_LEFT` = 8
  - `FACE_AUTH_TIP_TOO_MUCH_MOTION` = 9
  - `FACE_AUTH_TIP_POOR_GAZE` = 10
  - `FACE_AUTH_TIP_NOT_DETECTED` = 11
- **FingerprintTips**
  - `FINGERPRINT_TIP_GOOD` = 0
  - `FINGERPRINT_TIP_IMAGER_DIRTY` = 1
  - `FINGERPRINT_TIP_INSUFFICIENT` = 2
  - `FINGERPRINT_TIP_PARTIAL` = 3
  - `FINGERPRINT_TIP_TOO_FAST` = 4
  - `FINGERPRINT_TIP_TOO_SLOW` = 5
  - `FINGERPRINT_TIP_FINGER_DOWN` = 6
  - `FINGERPRINT_TIP_FINGER_UP` = 7
- **ConstraintSourceType**
  - `CONSTRAINT_NOT_EXIST` = 0
  - `CONSTRAINT_TYPE_BASE` = 1
  - `CONSTRAINT_TYPE_DEVICE_OWNER` = 2
  - `CONSTRAINT_TYPE_PROFILE_OWNER` = 3
#### 函数
- `getAccountManager(): AccountManager`
#### 类
- **UserAuth**
  - `getVersion()`: number
  - `getAvailableStatus()`: number
  - `getProperty()`: void
  - `getProperty()`: Promise<ExecutorProperty>
  - `setProperty()`: void
  - `setProperty()`: Promise<void>
  - `auth()`: Uint8Array
  - `authUser()`: Uint8Array
  - `cancelAuth()`: void
- **PINAuth**
  - `registerInputer()`: void
  - `unregisterInputer()`: void
- **InputerManager**
  - `registerInputer()`: void
  - `unregisterInputer()`: void
- **DomainAccountManager**
  - `registerPlugin()`: void
  - `unregisterPlugin()`: void
  - `auth()`: void
  - `authWithPopup()`: void
  - `authWithPopup()`: void
  - `hasAccount()`: void
  - `hasAccount()`: Promise<boolean>
  - `updateAccountToken()`: void
  - `updateAccountToken()`: Promise<void>
  - `getAccountInfo()`: void
  - `getAccountInfo()`: Promise<DomainAccountInfo>
  - `getAccessToken()`: void
  - `getAccessToken()`: Promise<Uint8Array>
- **UserIdentityManager**
  - `openSession()`: void
  - `openSession()`: Promise<Uint8Array>
  - `addCredential()`: void
  - `updateCredential()`: void
  - `closeSession()`: void
  - `cancel()`: void
  - `delUser()`: void
  - `delCred()`: void
  - `getAuthInfo()`: void
  - `getAuthInfo()`: void
  - `getAuthInfo()`: Promise<Array<EnrolledCredInfo>>

### @ohos.advertising.AdComponent (@ohos.advertising.AdComponent.d.ets)

### @ohos.advertising.AdsServiceExtensionAbility (@ohos.advertising.AdsServiceExtensionAbility.d.ts)
#### 接口
- **RespCallback**
#### 类
- **AdsServiceExtensionAbility**

### @ohos.advertising.AutoAdComponent (@ohos.advertising.AutoAdComponent.d.ets)

### advertising (@ohos.advertising.d.ts)
#### 接口
- **AdRequestParams**
  - `adId`: string
  - `adType?`: number
  - `adCount?`: number
  - `adWidth?`: number
  - `adHeight?`: number
  - `adSearchKeyword?`: string
- **AdOptions**
  - `tagForChildProtection?`: number
  - `adContentClassification?`: string
  - `nonPersonalizedAd?`: number
- **AdDisplayOptions**
  - `customData?`: string
  - `userId?`: string
  - `useMobileDataReminder?`: boolean
  - `mute?`: boolean
  - `audioFocusType?`: number
- **AdInteractionListener**
- **AdLoadListener**
  - `onAdLoadFailure`: void
  - `onAdLoadSuccess`: void
- **MultiSlotsAdLoadListener**
  - `onAdLoadFailure`: void
  - `onAdLoadSuccess`: void
#### 函数
- `showAd(ad: Advertisement, options: AdDisplayOptions, context?: common.UIAbilityContext): void`
#### 类
- **AdLoader**
  - `loadAd()`: void
  - `loadAdWithMultiSlots()`: void
#### 类型别名
- `Advertisement` = _Advertisement

### intelligentVoice (@ohos.ai.intelligentVoice.d.ts)
#### 接口
- **IntelligentVoiceManager**
  - `getCapabilityInfo`: Array<IntelligentVoiceEngineType>
  - `on`: void
  - `off`: void
- **EnrollIntelligentVoiceEngineDescriptor**
  - `wakeupPhrase`: string
- **WakeupIntelligentVoiceEngineDescriptor**
  - `needReconfirm`: boolean
  - `wakeupPhrase`: string
- **EnrollEngineConfig**
  - `language`: string
  - `region`: string
- **WakeupHapInfo**
  - `bundleName`: string
  - `abilityName`: string
- **EnrollCallbackInfo**
  - `result`: EnrollResult
  - `context`: string
- **WakeupIntelligentVoiceEngineCallbackInfo**
  - `eventId`: WakeupIntelligentVoiceEventType
  - `isSuccess`: boolean
  - `context`: string
- **EnrollIntelligentVoiceEngine**
  - `getSupportedRegions`: void
  - `getSupportedRegions`: Promise<Array<string>>
  - `init`: void
  - `init`: Promise<void>
  - `enrollForResult`: void
  - `enrollForResult`: Promise<EnrollCallbackInfo>
  - `stop`: void
  - `stop`: Promise<void>
  - `commit`: void
  - `commit`: Promise<void>
  - `setWakeupHapInfo`: void
  - `setWakeupHapInfo`: Promise<void>
  - `setSensibility`: void
  - `setSensibility`: Promise<void>
  - `setParameter`: void
  - `setParameter`: Promise<void>
  - `getParameter`: void
  - `getParameter`: Promise<string>
  - `release`: void
  - `release`: Promise<void>
- **WakeupIntelligentVoiceEngine**
  - `getSupportedRegions`: void
  - `getSupportedRegions`: Promise<Array<string>>
  - `setWakeupHapInfo`: void
  - `setWakeupHapInfo`: Promise<void>
  - `setSensibility`: void
  - `setSensibility`: Promise<void>
  - `setParameter`: void
  - `setParameter`: Promise<void>
  - `getParameter`: void
  - `getParameter`: Promise<string>
  - `release`: void
  - `release`: Promise<void>
  - `on`: void
  - `off`: void
#### 枚举
- **ServiceChangeType**
  - `SERVICE_UNAVAILABLE` = 0
- **IntelligentVoiceEngineType**
  - `ENROLL_ENGINE_TYPE` = 0
  - `WAKEUP_ENGINE_TYPE` = 1
  - `UPDATE_ENGINE_TYPE` = 2
- **SensibilityType**
  - `LOW_SENSIBILITY` = 1
  - `MIDDLE_SENSIBILITY` = 2
  - `HIGH_SENSIBILITY` = 3
- **WakeupIntelligentVoiceEventType**
  - `INTELLIGENT_VOICE_EVENT_WAKEUP_NONE` = 0
  - `INTELLIGENT_VOICE_EVENT_RECOGNIZE_COMPLETE` = 1
- **IntelligentVoiceErrorCode**
  - `INTELLIGENT_VOICE_NO_MEMORY` = 22700101
  - `INTELLIGENT_VOICE_INVALID_PARAM` = 22700102
  - `INTELLIGENT_VOICE_INIT_FAILED` = 22700103
  - `INTELLIGENT_VOICE_COMMIT_ENROLL_FAILED` = 22700104
- **EnrollResult**
  - `SUCCESS` = 0
  - `VPR_TRAIN_FAILED` = -1
  - `WAKEUP_PHRASE_NOT_MATCH` = -2
  - `TOO_NOISY` = -3
  - `TOO_LOUD` = -4
  - `INTERVAL_LARGE` = -5
  - `DIFFERENT_PERSON` = -6
  - `UNKNOWN_ERROR` = -100
#### 函数
- `getIntelligentVoiceManager(): IntelligentVoiceManager`
- `createEnrollIntelligentVoiceEngine(descriptor: EnrollIntelligentVoiceEngineDescriptor, callback: AsyncCallback<EnrollIntelligentVoiceEngine>): void`
- `createEnrollIntelligentVoiceEngine(descriptor: EnrollIntelligentVoiceEngineDescriptor): Promise<EnrollIntelligentVoiceEngine>`
- `createWakeupIntelligentVoiceEngine(descriptor: WakeupIntelligentVoiceEngineDescriptor, callback: AsyncCallback<WakeupIntelligentVoiceEngine>): void`
- `createWakeupIntelligentVoiceEngine(descriptor: WakeupIntelligentVoiceEngineDescriptor): Promise<WakeupIntelligentVoiceEngine>`

### mindSporeLite (@ohos.ai.mindSporeLite.d.ts)
#### 接口
- **Model**
  - `getInputs`: MSTensor[]
  - `predict`: void
  - `predict`: Promise<MSTensor[]>
  - `resize`: boolean
- **Context**
  - `target?`: string[]
  - `cpu?`: CpuDevice
  - `nnrt?`: NNRTDevice
- **CpuDevice**
  - `threadNum?`: number
  - `threadAffinityMode?`: ThreadAffinityMode
  - `threadAffinityCoreList?`: number[]
  - `precisionMode?`: string
- **NNRTDevice**
- **MSTensor**
  - `name`: string
  - `shape`: number[]
  - `elementNum`: number
  - `dataSize`: number
  - `dtype`: DataType
  - `format`: Format
  - `getData`: ArrayBuffer
  - `setData`: void
#### 枚举
- **ThreadAffinityMode**
  - `NO_AFFINITIES` = 0
  - `BIG_CORES_FIRST` = 1
  - `LITTLE_CORES_FIRST` = 2
- **DataType**
  - `TYPE_UNKNOWN` = 0
  - `NUMBER_TYPE_INT8` = 32
  - `NUMBER_TYPE_INT16` = 33
  - `NUMBER_TYPE_INT32` = 34
  - `NUMBER_TYPE_INT64` = 35
  - `NUMBER_TYPE_UINT8` = 37
  - `NUMBER_TYPE_UINT16` = 38
  - `NUMBER_TYPE_UINT32` = 39
  - `NUMBER_TYPE_UINT64` = 40
  - `NUMBER_TYPE_FLOAT16` = 42
  - `NUMBER_TYPE_FLOAT32` = 43
  - `NUMBER_TYPE_FLOAT64` = 44
- **Format**
  - `DEFAULT_FORMAT` = -1
  - `NCHW` = 0
  - `NHWC` = 1
  - `NHWC4` = 2
  - `HWKC` = 3
  - `HWCK` = 4
  - `KCHW` = 5
#### 函数
- `loadModelFromFile(
    model: string,
    context?: Context): Promise<Model>`
- `loadModelFromFile(
    model: string, callback: Callback<Model>): void`
- `loadModelFromFile(
    model: string,
    context: Context, callback: Callback<Model>): void`
- `loadModelFromBuffer(
    model: ArrayBuffer,
    context?: Context): Promise<Model>`
- `loadModelFromBuffer(
    model: ArrayBuffer, callback: Callback<Model>): void`
- `loadModelFromBuffer(
    model: ArrayBuffer,
    context: Context, callback: Callback<Model>): void`
- `loadModelFromFd(
    model: number,
    context?: Context): Promise<Model>`
- `loadModelFromFd(
    model: number, callback: Callback<Model>): void`
- `loadModelFromFd(
    model: number,
    context: Context, callback: Callback<Model>): void`

### windowAnimationManager (@ohos.animation.windowAnimationManager.d.ts)
#### 接口
- **RRect**
  - `left`: number
  - `top`: number
  - `width`: number
  - `height`: number
  - `radius`: number
- **WindowAnimationTarget**
  - `readonly bundleName`: string
  - `readonly abilityName`: string
  - `readonly windowBounds`: RRect
  - `readonly missionId`: number
- **WindowAnimationFinishedCallback**
  - `onAnimationFinish`: void
- **WindowAnimationController**
  - `onStartAppFromLauncher`: void
  - `onStartAppFromRecent`: void
  - `onStartAppFromOther`: void
  - `onAppTransition`: void
  - `onMinimizeWindow`: void
  - `onCloseWindow`: void
  - `onScreenUnlock`: void
  - `onWindowAnimationTargetsUpdate`: void
#### 函数
- `setController(controller: WindowAnimationController): void`
- `minimizeWindowWithAnimation(windowTarget: WindowAnimationTarget,
    callback: AsyncCallback<WindowAnimationFinishedCallback>): void`
- `minimizeWindowWithAnimation(windowTarget: WindowAnimationTarget): Promise<WindowAnimationFinishedCallback>`

### @ohos.animator (@ohos.animator.d.ts)
#### 接口
- **AnimatorOptions**
  - `duration`: number
  - `easing`: string
  - `delay`: number
  - `fill`: "none" | "forwards" | "backwards" | "both"
  - `direction`: "normal" | "reverse" | "alternate" | "alternate-reverse"
  - `iterations`: number
  - `begin`: number
  - `end`: number
- **AnimatorResult**
  - `update`: void
  - `reset`: void
  - `play`: void
  - `finish`: void
  - `pause`: void
  - `cancel`: void
  - `reverse`: void
  - `onframe`: (progress: number) => void
  - `onfinish`: () => void
  - `oncancel`: () => void
  - `onrepeat`: () => void
#### 类
- **Animator**
  - `createAnimator()`: AnimatorResult
  - `create()`: AnimatorResult

### @ohos.app.ability.Ability (@ohos.app.ability.Ability.d.ts)
#### 类
- **Ability**
  - `onConfigurationUpdate()`: void
  - `onMemoryLevel()`: void

### AbilityConstant (@ohos.app.ability.AbilityConstant.d.ts)
#### 接口
- **LaunchParam**
  - `launchReason`: LaunchReason
  - `lastExitReason`: LastExitReason
#### 枚举
- **LaunchReason**
  - `UNKNOWN` = 0
  - `START_ABILITY` = 1
  - `CALL` = 2
  - `CONTINUATION` = 3
  - `APP_RECOVERY` = 4
  - `SHARE` = 5
  - `AUTO_STARTUP` = 8
  - `INSIGHT_INTENT` = 9
- **LastExitReason**
  - `UNKNOWN` = 0
  - `ABILITY_NOT_RESPONDING` = 1
  - `NORMAL` = 2
  - `CPP_CRASH` = 3
  - `JS_ERROR` = 4
  - `APP_FREEZE` = 5
  - `PERFORMANCE_CONTROL` = 6
  - `RESOURCE_CONTROL` = 7
  - `UPGRADE` = 8
- **OnContinueResult**
  - `AGREE` = 0
  - `REJECT` = 1
  - `MISMATCH` = 2
- **MemoryLevel**
  - `MEMORY_LEVEL_MODERATE` = 0
  - `MEMORY_LEVEL_LOW` = 1
  - `MEMORY_LEVEL_CRITICAL` = 2
- **WindowMode**
  - `WINDOW_MODE_UNDEFINED` = 0
  - `WINDOW_MODE_FULLSCREEN` = 1
  - `WINDOW_MODE_SPLIT_PRIMARY` = 100
  - `WINDOW_MODE_SPLIT_SECONDARY` = 101
  - `WINDOW_MODE_FLOATING` = 102
- **OnSaveResult**
  - `ALL_AGREE` = 0
  - `CONTINUATION_REJECT` = 1
  - `CONTINUATION_MISMATCH` = 2
  - `RECOVERY_AGREE` = 3
  - `RECOVERY_REJECT` = 4
- **StateType**
  - `CONTINUATION` = 0
  - `APP_RECOVERY` = 1
- **ContinueState**
  - `ACTIVE` = 0
  - `INACTIVE` = 1

### @ohos.app.ability.AbilityLifecycleCallback (@ohos.app.ability.AbilityLifecycleCallback.d.ts)
#### 类
- **AbilityLifecycleCallback**
  - `onAbilityCreate()`: void
  - `onWindowStageCreate()`: void
  - `onWindowStageActive()`: void
  - `onWindowStageInactive()`: void
  - `onWindowStageDestroy()`: void
  - `onAbilityDestroy()`: void
  - `onAbilityForeground()`: void
  - `onAbilityBackground()`: void
  - `onAbilityContinue()`: void

### @ohos.app.ability.AbilityStage (@ohos.app.ability.AbilityStage.d.ts)
#### 类
- **AbilityStage**
  - `onCreate()`: void
  - `onAcceptWant()`: string
  - `onNewProcessRequest()`: string
  - `onConfigurationUpdate()`: void
  - `onMemoryLevel()`: void
  - `context`: AbilityStageContext

### @ohos.app.ability.ActionExtensionAbility (@ohos.app.ability.ActionExtensionAbility.d.ts)
#### 类
- **ActionExtensionAbility**

### @ohos.app.ability.ApplicationStateChangeCallback (@ohos.app.ability.ApplicationStateChangeCallback.d.ts)
#### 类
- **ApplicationStateChangeCallback**
  - `onApplicationForeground()`: void
  - `onApplicationBackground()`: void

### @ohos.app.ability.AutoFillExtensionAbility (@ohos.app.ability.AutoFillExtensionAbility.d.ts)
#### 类
- **AutoFillExtensionAbility**
  - `onCreate()`: void
  - `onFillRequest()`: void
  - `onSaveRequest()`: void
  - `onSessionDestroy()`: void
  - `onForeground()`: void
  - `onBackground()`: void
  - `onDestroy()`: void | Promise<void>
  - `context`: AutoFillExtensionContext

### @ohos.app.ability.ChildProcess (@ohos.app.ability.ChildProcess.d.ts)
#### 类
- **ChildProcess**
  - `onStart()`: void

### @ohos.app.ability.Configuration (@ohos.app.ability.Configuration.d.ts)
#### 接口
- **Configuration**
  - `language?`: string
  - `colorMode?`: ConfigurationConstant.ColorMode
  - `direction?`: ConfigurationConstant.Direction
  - `screenDensity?`: ConfigurationConstant.ScreenDensity
  - `displayId?`: number
  - `hasPointerDevice?`: boolean

### ConfigurationConstant (@ohos.app.ability.ConfigurationConstant.d.ts)
#### 枚举
- **ColorMode**
  - `COLOR_MODE_NOT_SET` = -1
  - `COLOR_MODE_DARK` = 0
  - `COLOR_MODE_LIGHT` = 1
- **Direction**
  - `DIRECTION_NOT_SET` = -1
  - `DIRECTION_VERTICAL` = 0
  - `DIRECTION_HORIZONTAL` = 1
- **ScreenDensity**
  - `SCREEN_DENSITY_NOT_SET` = 0
  - `SCREEN_DENSITY_SDPI` = 120
  - `SCREEN_DENSITY_MDPI` = 160
  - `SCREEN_DENSITY_LDPI` = 240
  - `SCREEN_DENSITY_XLDPI` = 320
  - `SCREEN_DENSITY_XXLDPI` = 480
  - `SCREEN_DENSITY_XXXLDPI` = 640

### @ohos.app.ability.DriverExtensionAbility (@ohos.app.ability.DriverExtensionAbility.d.ts)
#### 类
- **DriverExtensionAbility**
  - `onInit()`: void
  - `onRelease()`: void
  - `onConnect()`: rpc.RemoteObject | Promise<rpc.RemoteObject>
  - `onDisconnect()`: void | Promise<void>
  - `onDump()`: Array<string>
  - `context`: DriverExtensionContext
#### 类型别名
- `DriverExtensionContext` = _DriverExtensionContext

### @ohos.app.ability.EnvironmentCallback (@ohos.app.ability.EnvironmentCallback.d.ts)
#### 类
- **EnvironmentCallback**
  - `onConfigurationUpdated()`: void
  - `onMemoryLevel()`: void

### @ohos.app.ability.ExtensionAbility (@ohos.app.ability.ExtensionAbility.d.ts)
#### 类
- **ExtensionAbility**

### @ohos.app.ability.InsightIntentContext (@ohos.app.ability.InsightIntentContext.d.ts)
#### 类
- **InsightIntentContext**
  - `startAbility()`: void
  - `startAbility()`: Promise<void>

### @ohos.app.ability.InsightIntentExecutor (@ohos.app.ability.InsightIntentExecutor.d.ts)
#### 类
- **InsightIntentExecutor**
  - `onExecuteInUIAbilityForegroundMode()`: insightIntent.ExecuteResult | Promise<insightIntent.ExecuteResult>
  - `onExecuteInUIAbilityBackgroundMode()`: insightIntent.ExecuteResult | Promise<insightIntent.ExecuteResult>
  - `onExecuteInUIExtensionAbility()`: insightIntent.ExecuteResult | Promise<insightIntent.ExecuteResult>
  - `onExecuteInServiceExtensionAbility()`: insightIntent.ExecuteResult | Promise<insightIntent.ExecuteResult>
  - `context`: InsightIntentContext

### @ohos.app.ability.MediaControlExtensionAbility (@ohos.app.ability.MediaControlExtensionAbility.d.ts)
#### 类
- **MediaControlExtensionAbility**

### @ohos.app.ability.PrintExtensionAbility (@ohos.app.ability.PrintExtensionAbility.d.ts)
#### 类
- **PrintExtensionAbility**
  - `onCreate()`: void
  - `onStartDiscoverPrinter()`: void
  - `onStopDiscoverPrinter()`: void
  - `onConnectPrinter()`: void
  - `onDisconnectPrinter()`: void
  - `onStartPrintJob()`: void
  - `onCancelPrintJob()`: void
  - `onRequestPrinterCapability()`: print.PrinterCapability
  - `onRequestPreview()`: string
  - `onDestroy()`: void

### @ohos.app.ability.ServiceExtensionAbility (@ohos.app.ability.ServiceExtensionAbility.d.ts)
#### 类
- **ServiceExtensionAbility**
  - `onCreate()`: void
  - `onDestroy()`: void
  - `onRequest()`: void
  - `onConnect()`: rpc.RemoteObject | Promise<rpc.RemoteObject>
  - `onDisconnect()`: void | Promise<void>
  - `onReconnect()`: void
  - `onConfigurationUpdate()`: void
  - `onDump()`: Array<string>
  - `context`: ServiceExtensionContext

### @ohos.app.ability.ShareExtensionAbility (@ohos.app.ability.ShareExtensionAbility.d.ts)
#### 类
- **ShareExtensionAbility**

### @ohos.app.ability.StartOptions (@ohos.app.ability.StartOptions.d.ts)
#### 类
- **StartOptions**
  - `windowMode?`: number
  - `displayId?`: number
  - `withAnimation?`: boolean
  - `windowLeft?`: number
  - `windowTop?`: number
  - `windowWidth?`: number
  - `windowHeight?`: number

### @ohos.app.ability.UIAbility (@ohos.app.ability.UIAbility.d.ts)
#### 接口
- **OnReleaseCallback**
- **OnRemoteStateChangeCallback**
- **CalleeCallback**
- **Caller**
  - `call`: Promise<void>
  - `callWithResult`: Promise<rpc.MessageSequence>
  - `release`: void
  - `onRelease`: void
  - `onRemoteStateChange`: void
  - `on`: void
  - `off`: void
  - `off`: void
- **Callee**
  - `on`: void
  - `off`: void
#### 类
- **UIAbility**
  - `onCreate()`: void
  - `onWindowStageCreate()`: void
  - `onWindowStageDestroy()`: void
  - `onWindowStageRestore()`: void
  - `onDestroy()`: void | Promise<void>
  - `onForeground()`: void
  - `onBackground()`: void
  - `onContinue()`: AbilityConstant.OnContinueResult
  - `onNewWant()`: void
  - `onDump()`: Array<string>
  - `onSaveState()`: AbilityConstant.OnSaveResult
  - `onShare()`: void
  - `onPrepareToTerminate()`: boolean
  - `onBackPressed()`: boolean
  - `context`: UIAbilityContext
  - `launchWant`: Want
  - `lastRequestWant`: Want
  - `callee`: Callee

### @ohos.app.ability.UIExtensionAbility (@ohos.app.ability.UIExtensionAbility.d.ts)
#### 类
- **UIExtensionAbility**
  - `onCreate()`: void
  - `onSessionCreate()`: void
  - `onSessionDestroy()`: void
  - `onForeground()`: void
  - `onBackground()`: void
  - `onDestroy()`: void | Promise<void>
  - `context`: UIExtensionContext

### @ohos.app.ability.UIExtensionContentSession (@ohos.app.ability.UIExtensionContentSession.d.ts)
#### 类
- **UIExtensionContentSession**
  - `sendData()`: void
  - `loadContent()`: void
  - `startAbility()`: void
  - `startAbility()`: void
  - `startAbility()`: Promise<void>
  - `startAbilityAsCaller()`: void
  - `startAbilityAsCaller()`: void
  - `startAbilityAsCaller()`: Promise<void>
  - `startAbilityForResult()`: void
  - `startAbilityForResult()`: void
  - `startAbilityForResult()`: Promise<AbilityResult>
  - `terminateSelf()`: void
  - `terminateSelf()`: Promise<void>
  - `terminateSelfWithResult()`: void
  - `terminateSelfWithResult()`: Promise<void>
  - `setWindowBackgroundColor()`: void
  - `setWindowPrivacyMode()`: Promise<void>
  - `setWindowPrivacyMode()`: void
  - `startAbilityByType()`: void
  - `startAbilityByType()`: Promise<void>
  - `getUIExtensionHostWindowProxy()`: uiExtensionHost.UIExtensionHostWindowProxy
  - `abilityStartCallback`: AbilityStartCallback, callback: AsyncCallback<void>): void
  - `abilityStartCallback`: AbilityStartCallback): Promise<void>

### @ohos.app.ability.UserAuthExtensionAbility (@ohos.app.ability.UserAuthExtensionAbility.d.ts)
#### 类
- **UserAuthExtensionAbility**

### @ohos.app.ability.VpnExtensionAbility (@ohos.app.ability.VpnExtensionAbility.d.ts)
#### 类
- **VpnExtensionAbility**
  - `onCreate()`: void
  - `onDestroy()`: void
  - `context`: VpnExtensionContext

### @ohos.app.ability.Want (@ohos.app.ability.Want.d.ts)
#### 类
- **Want**
  - `bundleName?`: string
  - `abilityName?`: string
  - `deviceId?`: string
  - `uri?`: string
  - `type?`: string
  - `flags?`: number
  - `action?`: string
  - `parameters?`: Record<string, Object>
  - `entities?`: Array<string>
  - `moduleName?`: string

### abilityDelegatorRegistry (@ohos.app.ability.abilityDelegatorRegistry.d.ts)
#### 枚举
- **AbilityLifecycleState**
#### 函数
- `getAbilityDelegator(): AbilityDelegator`
- `getArguments(): AbilityDelegatorArgs`
#### 类型别名
- `AbilityDelegator` = _AbilityDelegator
- `AbilityDelegatorArgs` = _AbilityDelegatorArgs
- `AbilityMonitor` = _AbilityMonitor
- `ShellCmdResult` = _ShellCmdResult

### abilityManager (@ohos.app.ability.abilityManager.d.ts)
#### 枚举
- **AbilityState**
  - `INITIAL` = 0
  - `FOCUS` = 2
  - `FOREGROUND` = 9
  - `BACKGROUND` = 10
  - `FOREGROUNDING` = 11
  - `BACKGROUNDING` = 12
#### 函数
- `on(type: 'abilityForegroundState', observer: AbilityForegroundStateObserver): void`
- `off(type: 'abilityForegroundState', observer?: AbilityForegroundStateObserver): void`
- `updateConfiguration(config: Configuration, callback: AsyncCallback<void>): void`
- `updateConfiguration(config: Configuration): Promise<void>`
- `getAbilityRunningInfos(): Promise<Array<AbilityRunningInfo>>`
- `getAbilityRunningInfos(callback: AsyncCallback<Array<AbilityRunningInfo>>): void`
- `getExtensionRunningInfos(upperLimit: number): Promise<Array<ExtensionRunningInfo>>`
- `getExtensionRunningInfos(upperLimit: number, callback: AsyncCallback<Array<ExtensionRunningInfo>>): void`
- `getTopAbility(): Promise<ElementName>`
- `getTopAbility(callback: AsyncCallback<ElementName>): void`
- `acquireShareData(missionId: number, callback: AsyncCallback<Record<string, Object>>): void`
- `acquireShareData(missionId: number): Promise<Record<string, Object>>`
- `notifySaveAsResult(parameter: AbilityResult, requestCode: number, callback: AsyncCallback<void>): void`
- `notifySaveAsResult(parameter: AbilityResult, requestCode: number): Promise<void>`
- `getForegroundUIAbilities(callback: AsyncCallback<Array<AbilityStateData>>): void`
- `getForegroundUIAbilities(): Promise<Array<AbilityStateData>>`
#### 类型别名
- `AbilityRunningInfo` = _AbilityRunningInfo
- `AbilityStateData` = _AbilityStateData.default
- `ExtensionRunningInfo` = _ExtensionRunningInfo
- `AbilityForegroundStateObserver` = _AbilityForegroundStateObserver.default

### appManager (@ohos.app.ability.appManager.d.ts)
#### 枚举
- **ApplicationState**
- **ProcessState**
#### 函数
- `on(type: 'applicationState', observer: ApplicationStateObserver): number`
- `on(type: 'applicationState', observer: ApplicationStateObserver, bundleNameList: Array<string>): number`
- `on(type: 'appForegroundState', observer: AppForegroundStateObserver): void`
- `off(type: 'applicationState', observerId: number, callback: AsyncCallback<void>): void`
- `off(type: 'applicationState', observerId: number): Promise<void>`
- `off(type: 'appForegroundState', observer?: AppForegroundStateObserver): void`
- `getForegroundApplications(callback: AsyncCallback<Array<AppStateData>>): void`
- `getForegroundApplications(): Promise<Array<AppStateData>>`
- `killProcessWithAccount(bundleName: string, accountId: number): Promise<void>`
- `killProcessWithAccount(bundleName: string, accountId: number, callback: AsyncCallback<void>): void`
- `isRunningInStabilityTest(callback: AsyncCallback<boolean>): void`
- `isRunningInStabilityTest(): Promise<boolean>`
- `killProcessesByBundleName(bundleName: string): Promise<void>`
- `clearUpApplicationData(bundleName: string): Promise<void>`
- `isRamConstrainedDevice(): Promise<boolean>`
- `isRamConstrainedDevice(callback: AsyncCallback<boolean>): void`
- `getAppMemorySize(): Promise<number>`
- `getAppMemorySize(callback: AsyncCallback<number>): void`
- `getRunningProcessInformation(): Promise<Array<ProcessInformation>>`
- `getRunningProcessInformation(callback: AsyncCallback<Array<ProcessInformation>>): void`
- `isSharedBundleRunning(bundleName: string, versionCode: number): Promise<boolean>`
- `isSharedBundleRunning(bundleName: string, versionCode: number, callback: AsyncCallback<boolean>): void`
- `getProcessMemoryByPid(pid: number): Promise<number>`
- `getProcessMemoryByPid(pid: number, callback: AsyncCallback<number>): void`
- `getRunningProcessInfoByBundleName(bundleName: string, callback: AsyncCallback<Array<ProcessInformation>>): void`
- `getRunningProcessInfoByBundleName(bundleName: string, userId: number, callback: AsyncCallback<Array<ProcessInformation>>): void`
- `getRunningProcessInfoByBundleName(bundleName: string): Promise<Array<ProcessInformation>>`
- `getRunningProcessInfoByBundleName(bundleName: string, userId: number): Promise<Array<ProcessInformation>>`
- `isApplicationRunning(bundleName: string): Promise<boolean>`
- `isApplicationRunning(bundleName: string, callback: AsyncCallback<boolean>): void`
#### 类型别名
- `AbilityStateData` = _AbilityStateData.default
- `AppStateData` = _AppStateData.default
- `ApplicationStateObserver` = _ApplicationStateObserver.default
- `AppForegroundStateObserver` = _AppForegroundStateObserver.default
- `ProcessInformation` = _ProcessInformation
- `ProcessData` = _ProcessData.default

### appRecovery (@ohos.app.ability.appRecovery.d.ts)
#### 枚举
- **RestartFlag**
  - `ALWAYS_RESTART` = 0
  - `RESTART_WHEN_JS_CRASH` = 0x0001
  - `RESTART_WHEN_APP_FREEZE` = 0x0002
  - `NO_RESTART` = 0xFFFF
- **SaveOccasionFlag**
- **SaveModeFlag**
  - `SAVE_WITH_FILE` = 0x0001
#### 函数
- `enableAppRecovery(restart?: RestartFlag, saveOccasion?: SaveOccasionFlag, saveMode?: SaveModeFlag): void`
- `restartApp(): void`
- `setRestartWant(want: Want): void`
- `saveAppState(): boolean`
- `saveAppState(context?: UIAbilityContext): boolean`

### autoFillManager (@ohos.app.ability.autoFillManager.d.ts)
#### 接口
- **AutoSaveCallback**
  - `onSuccess`: void
  - `onFailure`: void
#### 函数
- `requestAutoSave(context: UIContext, callback?: AutoSaveCallback): void`
#### 类型别名
- `ViewData` = _ViewData.default
- `PageNodeInfo` = _PageNodeInfo.default
- `FillRequest` = _AutoFillRequest.FillRequest
- `SaveRequest` = _AutoFillRequest.SaveRequest
- `FillResponse` = _AutoFillRequest.FillResponse
- `FillRequestCallback` = _AutoFillRequest.FillRequestCallback
- `SaveRequestCallback` = _AutoFillRequest.SaveRequestCallback

### autoStartupManager (@ohos.app.ability.autoStartupManager.d.ts)
#### 函数
- `on(type: 'systemAutoStartup', callback: AutoStartupCallback): void`
- `off(type: 'systemAutoStartup', callback?: AutoStartupCallback): void`
- `setApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void`
- `setApplicationAutoStartup(info: AutoStartupInfo): Promise<void>`
- `cancelApplicationAutoStartup(info: AutoStartupInfo, callback: AsyncCallback<void>): void`
- `cancelApplicationAutoStartup(info: AutoStartupInfo): Promise<void>`
- `queryAllAutoStartupApplications(callback: AsyncCallback<Array<AutoStartupInfo>>): void`
- `queryAllAutoStartupApplications(): Promise<Array<AutoStartupInfo>>`

### childProcessManager (@ohos.app.ability.childProcessManager.d.ts)
#### 枚举
- **StartMode**
  - `SELF_FORK` = 0
  - `APP_SPAWN_FORK` = 1
#### 函数
- `startChildProcess(srcEntry: string, startMode: StartMode): Promise<number>`
- `startChildProcess(srcEntry: string, startMode: StartMode, callback: AsyncCallback<number>): void`

### common (@ohos.app.ability.common.d.ts)
#### 类型别名
- `UIAbilityContext` = _UIAbilityContext.default
- `AbilityStageContext` = _AbilityStageContext.default
- `ApplicationContext` = _ApplicationContext.default
- `BaseContext` = _BaseContext.default
- `Context` = _Context.default
- `ExtensionContext` = _ExtensionContext.default
- `FormExtensionContext` = _FormExtensionContext.default
- `ServiceExtensionContext` = _ServiceExtensionContext.default
- `EventHub` = _EventHub.default
- `PacMap` = _PacMap
- `AbilityResult` = _AbilityResult
- `ConnectOptions` = _ConnectOptions
- `UIExtensionContext` = _UIExtensionContext.default
- `AutoFillExtensionContext` = _AutoFillExtensionContext.default
- `AbilityStartCallback` = _AbilityStartCallback
- `AutoStartupInfo` = _AutoStartupInfo
- `AutoStartupCallback` = _AutoStartupCallback
- `VpnExtensionContext` = _VpnExtensionContext.default

### contextConstant (@ohos.app.ability.contextConstant.d.ts)
#### 枚举
- **AreaMode**
  - `EL1` = 0
  - `EL2` = 1
  - `EL3` = 2
  - `EL4` = 3

### dataUriUtils (@ohos.app.ability.dataUriUtils.d.ts)
#### 函数
- `getId(uri: string): number`
- `attachId(uri: string, id: number): string`
- `deleteId(uri: string): string`
- `updateId(uri: string, id: number): string`

### dialogRequest (@ohos.app.ability.dialogRequest.d.ts)
#### 接口
- **WindowRect**
  - `left`: number
  - `top`: number
  - `width`: number
  - `height`: number
- **RequestInfo**
- **RequestResult**
  - `result`: ResultCode
  - `want?`: Want
- **RequestCallback**
  - `setRequestResult`: void
#### 枚举
- **ResultCode**
  - `RESULT_OK` = 0
  - `RESULT_CANCEL` = 1
#### 函数
- `getRequestInfo(want: Want): RequestInfo`
- `getRequestCallback(want: Want): RequestCallback`

### dialogSession (@ohos.app.ability.dialogSession.d.ts)
#### 接口
- **DialogAbilityInfo**
  - `bundleName`: string
  - `moduleName`: string
  - `abilityName`: string
  - `abilityIconId`: number
  - `abilityLabelId`: number
  - `bundleIconId`: number
  - `bundleLabelId`: number
- **DialogSessionInfo**
  - `callerAbilityInfo`: DialogAbilityInfo
  - `targetAbilityInfos`: Array<DialogAbilityInfo>
  - `parameters?`: Record<string, Object>
#### 函数
- `getDialogSessionInfo(dialogSessionId: string): DialogSessionInfo`
- `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean): Promise<void>`
- `sendDialogResult(dialogSessionId: string, targetWant: Want, isAllowed: boolean, callback: AsyncCallback<void>): void`

### errorManager (@ohos.app.ability.errorManager.d.ts)
#### 函数
- `on(type: 'error', observer: ErrorObserver): number`
- `off(type: 'error', observerId: number, callback: AsyncCallback<void>): void`
- `off(type: 'error', observerId: number): Promise<void>`
#### 类型别名
- `ErrorObserver` = _ErrorObserver.default

### insightIntent (@ohos.app.ability.insightIntent.d.ts)
#### 接口
- **ExecuteResult**
  - `code`: number
  - `result?`: Record<string, Object>
#### 枚举
- **ExecuteMode**
  - `UI_ABILITY_FOREGROUND` = 0
  - `UI_ABILITY_BACKGROUND` = 1
  - `UI_EXTENSION_ABILITY` = 2
  - `SERVICE_EXTENSION_ABILITY` = 3

### insightIntentDriver (@ohos.app.ability.insightIntentDriver.d.ts)
#### 接口
- **ExecuteParam**
  - `bundleName`: string
  - `moduleName`: string
  - `abilityName`: string
  - `insightIntentName`: string
  - `insightIntentParam`: Record<string, Object>
  - `executeMode`: insightIntent.ExecuteMode
#### 函数
- `execute(param: ExecuteParam, callback: AsyncCallback<insightIntent.ExecuteResult>): void`
- `execute(param: ExecuteParam): Promise<insightIntent.ExecuteResult>`

### missionManager (@ohos.app.ability.missionManager.d.ts)
#### 函数
- `on(type: 'mission', listener: MissionListener): number`
- `off(type: 'mission', listenerId: number, callback: AsyncCallback<void>): void`
- `off(type: 'mission', listenerId: number): Promise<void>`
- `getMissionInfo(deviceId: string, missionId: number, callback: AsyncCallback<MissionInfo>): void`
- `getMissionInfo(deviceId: string, missionId: number): Promise<MissionInfo>`
- `getMissionInfos(deviceId: string, numMax: number, callback: AsyncCallback<Array<MissionInfo>>): void`
- `getMissionInfos(deviceId: string, numMax: number): Promise<Array<MissionInfo>>`
- `getMissionSnapShot(deviceId: string, missionId: number, callback: AsyncCallback<MissionSnapshot>): void`
- `getMissionSnapShot(deviceId: string, missionId: number): Promise<MissionSnapshot>`
- `getLowResolutionMissionSnapShot(
    deviceId: string,
    missionId: number,
    callback: AsyncCallback<MissionSnapshot>
  ): void`
- `getLowResolutionMissionSnapShot(deviceId: string, missionId: number): Promise<MissionSnapshot>`
- `lockMission(missionId: number, callback: AsyncCallback<void>): void`
- `lockMission(missionId: number): Promise<void>`
- `unlockMission(missionId: number, callback: AsyncCallback<void>): void`
- `unlockMission(missionId: number): Promise<void>`
- `clearMission(missionId: number, callback: AsyncCallback<void>): void`
- `clearMission(missionId: number): Promise<void>`
- `clearAllMissions(callback: AsyncCallback<void>): void`
- `clearAllMissions(): Promise<void>`
- `moveMissionToFront(missionId: number, callback: AsyncCallback<void>): void`
- `moveMissionToFront(missionId: number, options: StartOptions, callback: AsyncCallback<void>): void`
- `moveMissionToFront(missionId: number, options?: StartOptions): Promise<void>`
- `moveMissionsToForeground(missionIds: Array<number>, callback: AsyncCallback<void>): void`
- `moveMissionsToForeground(missionIds: Array<number>, topMission: number, callback: AsyncCallback<void>): void`
- `moveMissionsToForeground(missionIds: Array<number>, topMission?: number): Promise<void>`
- `moveMissionsToBackground(missionIds: Array<number>, callback: AsyncCallback<Array<number>>): void`
- `moveMissionsToBackground(missionIds: Array<number>): Promise<Array<number>>`
#### 类型别名
- `MissionInfo` = _MissionInfo
- `MissionListener` = _MissionListener
- `MissionSnapshot` = _MissionSnapshot

### quickFixManager (@ohos.app.ability.quickFixManager.d.ts)
#### 接口
- **HapModuleQuickFixInfo**
  - `readonly moduleName`: string
  - `readonly originHapHash`: string
  - `readonly quickFixFilePath`: string
- **ApplicationQuickFixInfo**
  - `readonly bundleName`: string
  - `readonly bundleVersionCode`: number
  - `readonly bundleVersionName`: string
  - `readonly quickFixVersionCode`: number
  - `readonly quickFixVersionName`: string
  - `readonly hapModuleQuickFixInfo`: Array<HapModuleQuickFixInfo>
#### 函数
- `applyQuickFix(hapModuleQuickFixFiles: Array<string>, callback: AsyncCallback<void>): void`
- `applyQuickFix(hapModuleQuickFixFiles: Array<string>): Promise<void>`
- `revokeQuickFix(bundleName: string, callback: AsyncCallback<void>): void`
- `revokeQuickFix(bundleName: string): Promise<void>`
- `getApplicationQuickFixInfo(bundleName: string, callback: AsyncCallback<ApplicationQuickFixInfo>): void`
- `getApplicationQuickFixInfo(bundleName: string): Promise<ApplicationQuickFixInfo>`

### wantAgent (@ohos.app.ability.wantAgent.d.ts)
#### 接口
- **CompleteData**
  - `info`: WantAgent
  - `want`: Want
  - `finalCode`: number
  - `finalData`: string
  - `extraInfo?`: Record<string, Object>
#### 枚举
- **WantAgentFlags**
  - `ONE_TIME_FLAG` = 0
- **OperationType**
  - `UNKNOWN_TYPE` = 0
#### 函数
- `getBundleName(agent: WantAgent, callback: AsyncCallback<string>): void`
- `getBundleName(agent: WantAgent): Promise<string>`
- `getUid(agent: WantAgent, callback: AsyncCallback<number>): void`
- `getUid(agent: WantAgent): Promise<number>`
- `getWant(agent: WantAgent, callback: AsyncCallback<Want>): void`
- `getWant(agent: WantAgent): Promise<Want>`
- `cancel(agent: WantAgent, callback: AsyncCallback<void>): void`
- `cancel(agent: WantAgent): Promise<void>`
- `trigger(agent: WantAgent, triggerInfo: TriggerInfo, callback?: AsyncCallback<CompleteData>): void`
- `equal(agent: WantAgent, otherAgent: WantAgent, callback: AsyncCallback<boolean>): void`
- `equal(agent: WantAgent, otherAgent: WantAgent): Promise<boolean>`
- `getWantAgent(info: WantAgentInfo, callback: AsyncCallback<WantAgent>): void`
- `getWantAgent(info: WantAgentInfo): Promise<WantAgent>`
- `getOperationType(agent: WantAgent, callback: AsyncCallback<number>): void`
- `getOperationType(agent: WantAgent): Promise<number>`
#### 类型别名
- `TriggerInfo` = _TriggerInfo
- `WantAgentInfo` = _WantAgentInfo
- `WantAgent` = object

### wantConstant (@ohos.app.ability.wantConstant.d.ts)
#### 枚举
- **Params**
  - `DLP_PARAMS_SANDBOX` = 'ohos.dlp.params.sandbox'
  - `DLP_PARAMS_BUNDLE_NAME` = 'ohos.dlp.params.bundleName'
  - `DLP_PARAMS_MODULE_NAME` = 'ohos.dlp.params.moduleName'
  - `DLP_PARAMS_ABILITY_NAME` = 'ohos.dlp.params.abilityName'
  - `DLP_PARAMS_INDEX` = 'ohos.dlp.params.index'
  - `ABILITY_BACK_TO_OTHER_MISSION_STACK` = 'ability.params.backToOtherMissionStack'
  - `ABILITY_RECOVERY_RESTART` = 'ohos.ability.params.abilityRecoveryRestart'
  - `CONTENT_TITLE_KEY` = 'ohos.extra.param.key.contentTitle'
  - `SHARE_ABSTRACT_KEY` = 'ohos.extra.param.key.shareAbstract'
  - `SHARE_URL_KEY` = 'ohos.extra.param.key.shareUrl'
  - `SUPPORT_CONTINUE_PAGE_STACK_KEY` = 'ohos.extra.param.key.supportContinuePageStack'
  - `SUPPORT_CONTINUE_SOURCE_EXIT_KEY` = 'ohos.extra.param.key.supportContinueSourceExit'
- **Flags**
  - `FLAG_AUTH_READ_URI_PERMISSION` = 0x00000001
  - `FLAG_AUTH_WRITE_URI_PERMISSION` = 0x00000002
  - `flag` = FLAG_AUTH_READ_URI_PERMISSION | FLAG_AUTH_PERSISTABLE_URI_PERMISSION.
  - `flag` = FLAG_AUTH_WRITE_URI_PERMISSION | FLAG_AUTH_PERSISTABLE_URI_PERMISSION.
  - `FLAG_AUTH_PERSISTABLE_URI_PERMISSION` = 0x00000040
  - `FLAG_INSTALL_ON_DEMAND` = 0x00000800
  - `FLAG_START_WITHOUT_TIPS` = 0x40000000

### businessAbilityRouter (@ohos.app.businessAbilityRouter.d.ts)
#### 接口
- **BusinessAbilityFilter**
  - `businessType`: BusinessType
  - `mimeType?`: string
  - `uri?`: string
#### 枚举
- **BusinessType**
  - `SHARE` = 0
  - `UNSPECIFIED` = 255
#### 函数
- `queryBusinessAbilityInfo(
    filter: BusinessAbilityFilter,
    callback: AsyncCallback<Array<BusinessAbilityInfo>>
  ): void`
- `queryBusinessAbilityInfo(filter: BusinessAbilityFilter): Promise<Array<BusinessAbilityInfo>>`
#### 类型别名
- `BusinessAbilityInfo` = _BusinessAbilityInfo.BusinessAbilityInfo

### @ohos.app.form.FormExtensionAbility (@ohos.app.form.FormExtensionAbility.d.ts)
#### 类
- **FormExtensionAbility**
  - `onAddForm()`: formBindingData.FormBindingData
  - `onCastToNormalForm()`: void
  - `onUpdateForm()`: void
  - `onChangeFormVisibility()`: void
  - `onFormEvent()`: void
  - `onRemoveForm()`: void
  - `onConfigurationUpdate()`: void
  - `onAcquireFormState?()`: formInfo.FormState
  - `onShareForm?()`: Record<string, Object>
  - `onAcquireFormData?()`: Record<string, Object>
  - `context`: FormExtensionContext

### formAgent (@ohos.app.form.formAgent.d.ts)
#### 函数
- `requestPublishForm(want: Want, callback: AsyncCallback<string>): void`
- `requestPublishForm(want: Want): Promise<string>`

### formBindingData (@ohos.app.form.formBindingData.d.ts)
#### 接口
- **FormBindingData**
  - `data`: Object
  - `proxies?`: Array<ProxyData>
- **ProxyData**
  - `key`: string
  - `subscriberId?`: string
#### 函数
- `createFormBindingData(obj?: Object | string): FormBindingData`

### formHost (@ohos.app.form.formHost.d.ts)
#### 函数
- `deleteForm(formId: string, callback: AsyncCallback<void>): void`
- `deleteForm(formId: string): Promise<void>`
- `releaseForm(formId: string, callback: AsyncCallback<void>): void`
- `releaseForm(formId: string, isReleaseCache: boolean, callback: AsyncCallback<void>): void`
- `releaseForm(formId: string, isReleaseCache?: boolean): Promise<void>`
- `requestForm(formId: string, callback: AsyncCallback<void>): void`
- `requestForm(formId: string): Promise<void>`
- `castToNormalForm(formId: string, callback: AsyncCallback<void>): void`
- `castToNormalForm(formId: string): Promise<void>`
- `notifyVisibleForms(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `notifyVisibleForms(formIds: Array<string>): Promise<void>`
- `notifyInvisibleForms(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `notifyInvisibleForms(formIds: Array<string>): Promise<void>`
- `enableFormsUpdate(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `enableFormsUpdate(formIds: Array<string>): Promise<void>`
- `disableFormsUpdate(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `disableFormsUpdate(formIds: Array<string>): Promise<void>`
- `isSystemReady(callback: AsyncCallback<void>): void`
- `isSystemReady(): Promise<void>`
- `getAllFormsInfo(callback: AsyncCallback<Array<formInfo.FormInfo>>): void`
- `getAllFormsInfo(): Promise<Array<formInfo.FormInfo>>`
- `getFormsInfo(bundleName: string, callback: AsyncCallback<Array<formInfo.FormInfo>>): void`
- `getFormsInfo(
    bundleName: string,
    moduleName: string,
    callback: AsyncCallback<Array<formInfo.FormInfo>>
  ): void`
- `getFormsInfo(bundleName: string, moduleName?: string): Promise<Array<formInfo.FormInfo>>`
- `deleteInvalidForms(formIds: Array<string>, callback: AsyncCallback<number>): void`
- `deleteInvalidForms(formIds: Array<string>): Promise<number>`
- `acquireFormState(want: Want, callback: AsyncCallback<formInfo.FormStateInfo>): void`
- `acquireFormState(want: Want): Promise<formInfo.FormStateInfo>`
- `on(type: 'formUninstall', callback: Callback<string>): void`
- `off(type: 'formUninstall', callback?: Callback<string>): void`
- `notifyFormsVisible(formIds: Array<string>, isVisible: boolean, callback: AsyncCallback<void>): void`
- `notifyFormsVisible(formIds: Array<string>, isVisible: boolean): Promise<void>`
- `notifyFormsEnableUpdate(
    formIds: Array<string>,
    isEnableUpdate: boolean,
    callback: AsyncCallback<void>
  ): void`
- `notifyFormsEnableUpdate(formIds: Array<string>, isEnableUpdate: boolean): Promise<void>`
- `shareForm(formId: string, deviceId: string, callback: AsyncCallback<void>): void`
- `shareForm(formId: string, deviceId: string): Promise<void>`
- `notifyFormsPrivacyProtected(
    formIds: Array<string>,
    isProtected: boolean,
    callback: AsyncCallback<void>
  ): void`
- `notifyFormsPrivacyProtected(formIds: Array<string>, isProtected: boolean): Promise<void>`
- `acquireFormData(formId: string, callback: AsyncCallback<Record<string, Object>>): void`
- `acquireFormData(formId: string): Promise<Record<string, Object>>`
- `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>, callback: AsyncCallback<void>): void`
- `setRouterProxy(formIds: Array<string>, proxy: Callback<Want>): Promise<void>`
- `clearRouterProxy(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `clearRouterProxy(formIds: Array<string>): Promise<void>`
- `setFormsRecyclable(formIds: Array<string>): Promise<void>`
- `setFormsRecyclable(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `recoverForms(formIds: Array<string>): Promise<void>`
- `recoverForms(formIds: Array<string>, callback: AsyncCallback<void>): void`

### formInfo (@ohos.app.form.formInfo.d.ts)
#### 接口
- **FormInfo**
  - `bundleName`: string
  - `moduleName`: string
  - `abilityName`: string
  - `name`: string
  - `displayName`: string
  - `displayNameId`: number
  - `description`: string
  - `descriptionId`: number
  - `type`: FormType
  - `jsComponentName`: string
  - `colorMode`: ColorMode
  - `isDefault`: boolean
  - `updateEnabled`: boolean
  - `formVisibleNotify`: boolean
  - `scheduledUpdateTime`: string
  - `formConfigAbility`: string
  - `updateDuration`: number
  - `defaultDimension`: number
  - `supportDimensions`: Array<number>
  - `customizeData`: Record<string, string>
  - `isDynamic`: boolean
  - `transparencyEnabled`: boolean
- **FormStateInfo**
  - `formState`: FormState
  - `want`: Want
- **FormInfoFilter**
  - `moduleName?`: string
- **FormProviderFilter**
  - `bundleName`: string
  - `formName?`: string
  - `moduleName?`: string
  - `abilityName?`: string
  - `isUnusedIncluded?`: boolean
- **RunningFormInfo**
  - `readonly formId`: string
  - `readonly bundleName`: string
  - `readonly hostBundleName`: string
  - `readonly visibilityType`: VisibilityType
  - `readonly moduleName`: string
  - `readonly abilityName`: string
  - `readonly formName`: string
  - `readonly dimension`: number
  - `readonly formUsageState`: FormUsageState
  - `readonly formDescription`: string
#### 枚举
- **FormType**
  - `JS` = 1
  - `eTS` = 2
- **ColorMode**
  - `MODE_AUTO` = -1
  - `MODE_DARK` = 0
  - `MODE_LIGHT` = 1
- **FormState**
  - `UNKNOWN` = -1
  - `DEFAULT` = 0
  - `READY` = 1
- **FormParam**
- **FormDimension**
  - `Dimension_1_2` = 1
- **VisibilityType**
  - `UNKNOWN` = 0
  - `FORM_VISIBLE` = 1
- **LaunchReason**
  - `FORM_DEFAULT` = 1
- **FormUsageState**
  - `USED` = 0
  - `UNUSED` = 1

### formObserver (@ohos.app.form.formObserver.d.ts)
#### 函数
- `on(type: 'formAdd', observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `on(type: 'formAdd', hostBundleName: string, observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `off(type: 'formAdd', hostBundleName?: string, observerCallback?: Callback<formInfo.RunningFormInfo>): void`
- `on(type: 'formRemove', observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `on(type: 'formRemove', hostBundleName: string, observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `off(type: 'formRemove', hostBundleName?: string, observerCallback?: Callback<formInfo.RunningFormInfo>): void`
- `on(type: 'notifyVisible', observerCallback: Callback<Array<formInfo.RunningFormInfo>>): void`
- `on(
    type: 'notifyVisible',
    hostBundleName: string,
    observerCallback: Callback<Array<formInfo.RunningFormInfo>>
  ): void`
- `on(type: 'notifyInvisible', observerCallback: Callback<Array<formInfo.RunningFormInfo>>): void`
- `on(
    type: 'notifyInvisible',
    hostBundleName: string,
    observerCallback: Callback<Array<formInfo.RunningFormInfo>>,
  ): void`
- `off(
    type: 'notifyVisible',
    hostBundleName?: string,
    observerCallback?: Callback<Array<formInfo.RunningFormInfo>>
  ): void`
- `off(
    type: 'notifyInvisible',
    hostBundleName?: string,
    observerCallback?: Callback<Array<formInfo.RunningFormInfo>>
  ): void`
- `getRunningFormInfos(callback: AsyncCallback<Array<formInfo.RunningFormInfo>>, hostBundleName?: string): void`
- `getRunningFormInfos(
    callback: AsyncCallback<Array<formInfo.RunningFormInfo>>,
    isUnusedIncluded: boolean,
    hostBundleName?: string
  ): void`
- `getRunningFormInfos(hostBundleName?: string): Promise<Array<formInfo.RunningFormInfo>>`
- `getRunningFormInfos(
    isUnusedIncluded: boolean,
    hostBundleName?: string
  ): Promise<Array<formInfo.RunningFormInfo>>`
- `getRunningFormInfosByFilter(
    formProviderFilter: formInfo.FormProviderFilter
  ): Promise<Array<formInfo.RunningFormInfo>>`
- `getRunningFormInfosByFilter(
    formProviderFilter: formInfo.FormProviderFilter,
    callback: AsyncCallback<Array<formInfo.RunningFormInfo>>
  ): void`
- `getRunningFormInfoById(formId: string): Promise<formInfo.RunningFormInfo>`
- `getRunningFormInfoById(formId: string, isUnusedIncluded: boolean): Promise<formInfo.RunningFormInfo>`
- `getRunningFormInfoById(formId: string, callback: AsyncCallback<formInfo.RunningFormInfo>): void`
- `getRunningFormInfoById(
    formId: string,
    isUnusedIncluded: boolean,
    callback: AsyncCallback<formInfo.RunningFormInfo>
  ): void`
- `on(type: 'router', observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `on(type: 'router', hostBundleName: string, observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `off(type: 'router', hostBundleName?: string, observerCallback?: Callback<formInfo.RunningFormInfo>): void`
- `on(type: 'message', observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `on(type: 'message', hostBundleName: string, observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `off(type: 'message', hostBundleName?: string, observerCallback?: Callback<formInfo.RunningFormInfo>): void`
- `on(type: 'call', observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `on(type: 'call', hostBundleName: string, observerCallback: Callback<formInfo.RunningFormInfo>): void`
- `off(type: 'call', hostBundleName?: string, observerCallback?: Callback<formInfo.RunningFormInfo>): void`

### formProvider (@ohos.app.form.formProvider.d.ts)
#### 函数
- `setFormNextRefreshTime(formId: string, minute: number, callback: AsyncCallback<void>): void`
- `setFormNextRefreshTime(formId: string, minute: number): Promise<void>`
- `updateForm(
    formId: string,
    formBindingData: formBindingData.FormBindingData,
    callback: AsyncCallback<void>
  ): void`
- `updateForm(formId: string, formBindingData: formBindingData.FormBindingData): Promise<void>`
- `getFormsInfo(filter: formInfo.FormInfoFilter, callback: AsyncCallback<Array<formInfo.FormInfo>>): void`
- `getFormsInfo(callback: AsyncCallback<Array<formInfo.FormInfo>>): void`
- `getFormsInfo(filter?: formInfo.FormInfoFilter): Promise<Array<formInfo.FormInfo>>`
- `requestPublishForm(
    want: Want,
    formBindingData: formBindingData.FormBindingData,
    callback: AsyncCallback<string>
  ): void`
- `requestPublishForm(want: Want, callback: AsyncCallback<string>): void`
- `requestPublishForm(want: Want, formBindingData?: formBindingData.FormBindingData): Promise<string>`
- `isRequestPublishFormSupported(callback: AsyncCallback<boolean>): void`
- `isRequestPublishFormSupported(): Promise<boolean>`

### @ohos.application.AccessibilityExtensionAbility (@ohos.application.AccessibilityExtensionAbility.d.ts)
#### 接口
- **AccessibilityEvent**
  - `eventType`: accessibility.EventType | accessibility.WindowUpdateType | TouchGuideType | GestureType | PageUpdateType
  - `target?`: AccessibilityElement
  - `timeStamp?`: number
#### 类
- **AccessibilityExtensionAbility**
  - `onConnect()`: void
  - `onDisconnect()`: void
  - `onAccessibilityEvent()`: void
  - `onKeyEvent()`: boolean
  - `context`: AccessibilityExtensionContext
#### 类型别名
- `AccessibilityElement` = _AccessibilityElement
- `ElementAttributeValues` = _ElementAttributeValues
- `FocusDirection` = _FocusDirection
- `ElementAttributeKeys` = keyof ElementAttributeValues
- `FocusType` = _FocusType
- `WindowType` = _WindowType
- `Rect` = _Rect
- `AccessibilityExtensionContext` = _AccessibilityExtensionContext.default
- `GestureType` = 'left' | 'leftThenRight' | 'leftThenUp' | 'leftThenDown' |
  'right' | 'rightThenLeft' | 'rightThenUp' | 'rightThenDown' |
  'up' | 'upThenLeft' | 'up
- `PageUpdateType` = 'pageContentUpdate' | 'pageStateUpdate'
- `TouchGuideType` = 'touchBegin' | 'touchEnd'

### @ohos.application.BackupExtensionAbility (@ohos.application.BackupExtensionAbility.d.ts)
#### 接口
- **BundleVersion**
  - `code`: number
  - `name`: string
#### 类
- **BackupExtensionAbility**
  - `onBackup()`: void
  - `onRestore()`: void
  - `context`: ExtensionContext

### @ohos.application.Configuration (@ohos.application.Configuration.d.ts)
#### 接口
- **Configuration**
  - `language?`: string
  - `colorMode?`: ConfigurationConstant.ColorMode

### ConfigurationConstant (@ohos.application.ConfigurationConstant.d.ts)
#### 枚举
- **ColorMode**
  - `COLOR_MODE_NOT_SET` = -1
  - `COLOR_MODE_DARK` = 0
  - `COLOR_MODE_LIGHT` = 1

### @ohos.application.DataShareExtensionAbility (@ohos.application.DataShareExtensionAbility.d.ts)
#### 类
- **DataShareExtensionAbility**
  - `onCreate?()`: void
  - `insert?()`: void
  - `update?()`: void
  - `delete?()`: void
  - `query?()`: void
  - `batchInsert?()`: void
  - `normalizeUri?()`: void
  - `denormalizeUri?()`: void
  - `context`: ExtensionContext

### @ohos.application.StaticSubscriberExtensionAbility (@ohos.application.StaticSubscriberExtensionAbility.d.ts)
#### 类
- **StaticSubscriberExtensionAbility**
  - `onReceiveEvent()`: void
  - `context`: StaticSubscriberExtensionContext

### @ohos.application.StaticSubscriberExtensionContext (@ohos.application.StaticSubscriberExtensionContext.d.ts)
#### 类
- **StaticSubscriberExtensionContext**
  - `startAbility()`: void
  - `startAbility()`: Promise<void>

### @ohos.application.Want (@ohos.application.Want.d.ts)
#### 类
- **Want**
  - `deviceId?`: string
  - `bundleName?`: string
  - `abilityName?`: string
  - `uri?`: string
  - `type?`: string
  - `flags?`: number
  - `action?`: string
  - `parameters?`: { [key: string]: any }
  - `entities?`: Array<string>

### @ohos.application.WindowExtensionAbility (@ohos.application.WindowExtensionAbility.d.ts)
#### 类
- **WindowExtensionAbility**
  - `onConnect()`: void
  - `onDisconnect()`: void
  - `onWindowReady()`: void
  - `context`: WindowExtensionContext
#### 类型别名
- `WindowExtensionContext` = _WindowExtensionContext

### abilityDelegatorRegistry (@ohos.application.abilityDelegatorRegistry.d.ts)
#### 枚举
- **AbilityLifecycleState**
#### 函数
- `getAbilityDelegator(): AbilityDelegator`
- `getArguments(): AbilityDelegatorArgs`

### abilityManager (@ohos.application.abilityManager.d.ts)
#### 枚举
- **AbilityState**
  - `INITIAL` = 0
  - `FOREGROUND` = 9
  - `BACKGROUND` = 10
  - `FOREGROUNDING` = 11
  - `BACKGROUNDING` = 12
#### 函数
- `updateConfiguration(config: Configuration, callback: AsyncCallback<void>): void`
- `updateConfiguration(config: Configuration): Promise<void>`
- `getAbilityRunningInfos(): Promise<Array<AbilityRunningInfo>>`
- `getAbilityRunningInfos(callback: AsyncCallback<Array<AbilityRunningInfo>>): void`

### appManager (@ohos.application.appManager.d.ts)
#### 函数
- `registerApplicationStateObserver(observer: ApplicationStateObserver): number`
- `unregisterApplicationStateObserver(observerId: number, callback: AsyncCallback<void>): void`
- `unregisterApplicationStateObserver(observerId: number): Promise<void>`
- `getForegroundApplications(callback: AsyncCallback<Array<AppStateData>>): void`
- `getForegroundApplications(): Promise<Array<AppStateData>>`
- `killProcessWithAccount(bundleName: string, accountId: number): Promise<void>`
- `killProcessWithAccount(bundleName: string, accountId: number, callback: AsyncCallback<void>): void`
- `isRunningInStabilityTest(callback: AsyncCallback<boolean>): void`
- `isRunningInStabilityTest(): Promise<boolean>`
- `getProcessRunningInfos(): Promise<Array<ProcessRunningInfo>>`
- `getProcessRunningInfos(callback: AsyncCallback<Array<ProcessRunningInfo>>): void`
- `killProcessesByBundleName(bundleName: string): Promise<void>`
- `clearUpApplicationData(bundleName: string): Promise<void>`
- `isRamConstrainedDevice(): Promise<boolean>`
- `isRamConstrainedDevice(callback: AsyncCallback<boolean>): void`
- `getAppMemorySize(): Promise<number>`
- `getAppMemorySize(callback: AsyncCallback<number>): void`

### formBindingData (@ohos.application.formBindingData.d.ts)
#### 接口
- **FormBindingData**
  - `data`: Object
#### 函数
- `createFormBindingData(obj?: Object | string): FormBindingData`

### formError (@ohos.application.formError.d.ts)
#### 枚举
- **FormError**
  - `ERR_COMMON` = 1
  - `ERR_PERMISSION_DENY` = 2
  - `ERR_GET_INFO_FAILED` = 4
  - `ERR_GET_BUNDLE_FAILED` = 5
  - `ERR_GET_LAYOUT_FAILED` = 6
  - `ERR_ADD_INVALID_PARAM` = 7
  - `ERR_CFG_NOT_MATCH_ID` = 8
  - `ERR_NOT_EXIST_ID` = 9
  - `ERR_BIND_PROVIDER_FAILED` = 10
  - `ERR_MAX_SYSTEM_FORMS` = 11
  - `ERR_MAX_INSTANCES_PER_FORM` = 12
  - `ERR_OPERATION_FORM_NOT_SELF` = 13
  - `ERR_PROVIDER_DEL_FAIL` = 14
  - `ERR_MAX_FORMS_PER_CLIENT` = 15
  - `ERR_MAX_SYSTEM_TEMP_FORMS` = 16
  - `ERR_FORM_NO_SUCH_MODULE` = 17
  - `ERR_FORM_NO_SUCH_ABILITY` = 18
  - `ERR_FORM_NO_SUCH_DIMENSION` = 19
  - `ERR_FORM_FA_NOT_INSTALLED` = 20
  - `ERR_SYSTEM_RESPONSES_FAILED` = 30
  - `ERR_FORM_DUPLICATE_ADDED` = 31
  - `ERR_IN_RECOVERY` = 36
  - `ERR_DISTRIBUTED_SCHEDULE_FAILED` = 37

### formHost (@ohos.application.formHost.d.ts)
#### 函数
- `deleteForm(formId: string, callback: AsyncCallback<void>): void`
- `deleteForm(formId: string): Promise<void>`
- `releaseForm(formId: string, callback: AsyncCallback<void>): void`
- `releaseForm(formId: string, isReleaseCache: boolean, callback: AsyncCallback<void>): void`
- `releaseForm(formId: string, isReleaseCache?: boolean): Promise<void>`
- `requestForm(formId: string, callback: AsyncCallback<void>): void`
- `requestForm(formId: string): Promise<void>`
- `castTempForm(formId: string, callback: AsyncCallback<void>): void`
- `castTempForm(formId: string): Promise<void>`
- `notifyVisibleForms(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `notifyVisibleForms(formIds: Array<string>): Promise<void>`
- `notifyInvisibleForms(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `notifyInvisibleForms(formIds: Array<string>): Promise<void>`
- `enableFormsUpdate(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `enableFormsUpdate(formIds: Array<string>): Promise<void>`
- `disableFormsUpdate(formIds: Array<string>, callback: AsyncCallback<void>): void`
- `disableFormsUpdate(formIds: Array<string>): Promise<void>`
- `isSystemReady(callback: AsyncCallback<void>): void`
- `isSystemReady(): Promise<void>`
- `getAllFormsInfo(callback: AsyncCallback<Array<formInfo.FormInfo>>): void`
- `getAllFormsInfo(): Promise<Array<formInfo.FormInfo>>`
- `getFormsInfo(bundleName: string, callback: AsyncCallback<Array<formInfo.FormInfo>>): void`
- `getFormsInfo(
    bundleName: string,
    moduleName: string,
    callback: AsyncCallback<Array<formInfo.FormInfo>>
  ): void`
- `getFormsInfo(bundleName: string, moduleName?: string): Promise<Array<formInfo.FormInfo>>`
- `deleteInvalidForms(formIds: Array<string>, callback: AsyncCallback<number>): void`
- `deleteInvalidForms(formIds: Array<string>): Promise<number>`
- `acquireFormState(want: Want, callback: AsyncCallback<formInfo.FormStateInfo>): void`
- `acquireFormState(want: Want): Promise<formInfo.FormStateInfo>`
- `on(type: 'formUninstall', callback: Callback<string>): void`
- `off(type: 'formUninstall', callback?: Callback<string>): void`
- `notifyFormsVisible(formIds: Array<string>, isVisible: boolean, callback: AsyncCallback<void>): void`
- `notifyFormsVisible(formIds: Array<string>, isVisible: boolean): Promise<void>`
- `notifyFormsEnableUpdate(
    formIds: Array<string>,
    isEnableUpdate: boolean,
    callback: AsyncCallback<void>
  ): void`
- `notifyFormsEnableUpdate(formIds: Array<string>, isEnableUpdate: boolean): Promise<void>`

### formInfo (@ohos.application.formInfo.d.ts)
#### 接口
- **FormInfo**
  - `bundleName`: string
  - `moduleName`: string
  - `abilityName`: string
  - `name`: string
  - `description`: string
  - `type`: FormType
  - `jsComponentName`: string
  - `colorMode`: ColorMode
  - `isDefault`: boolean
  - `updateEnabled`: boolean
  - `formVisibleNotify`: boolean
  - `relatedBundleName`: string
  - `scheduledUpdateTime`: string
  - `formConfigAbility`: string
  - `updateDuration`: number
  - `defaultDimension`: number
  - `supportDimensions`: Array<number>
  - `customizeData`: { [key: string]: [value: string] }
- **FormStateInfo**
  - `formState`: FormState
  - `want`: Want
#### 枚举
- **FormType**
  - `JS` = 1
- **ColorMode**
  - `MODE_AUTO` = -1
  - `MODE_DARK` = 0
  - `MODE_LIGHT` = 1
- **FormState**
  - `UNKNOWN` = -1
  - `DEFAULT` = 0
  - `READY` = 1
- **FormParam**

### formProvider (@ohos.application.formProvider.d.ts)
#### 函数
- `setFormNextRefreshTime(formId: string, minute: number, callback: AsyncCallback<void>): void`
- `setFormNextRefreshTime(formId: string, minute: number): Promise<void>`
- `updateForm(
    formId: string,
    formBindingData: formBindingData.FormBindingData,
    callback: AsyncCallback<void>
  ): void`
- `updateForm(formId: string, formBindingData: formBindingData.FormBindingData): Promise<void>`

### missionManager (@ohos.application.missionManager.d.ts)
#### 函数
- `registerMissionListener(listener: MissionListener): number`
- `unregisterMissionListener(listenerId: number, callback: AsyncCallback<void>): void`
- `unregisterMissionListener(listenerId: number): Promise<void>`
- `getMissionInfo(deviceId: string, missionId: number, callback: AsyncCallback<MissionInfo>): void`
- `getMissionInfo(deviceId: string, missionId: number): Promise<MissionInfo>`
- `getMissionInfos(deviceId: string, numMax: number, callback: AsyncCallback<Array<MissionInfo>>): void`
- `getMissionInfos(deviceId: string, numMax: number): Promise<Array<MissionInfo>>`
- `getMissionSnapShot(deviceId: string, missionId: number, callback: AsyncCallback<MissionSnapshot>): void`
- `getMissionSnapShot(deviceId: string, missionId: number): Promise<MissionSnapshot>`
- `lockMission(missionId: number, callback: AsyncCallback<void>): void`
- `lockMission(missionId: number): Promise<void>`
- `unlockMission(missionId: number, callback: AsyncCallback<void>): void`
- `unlockMission(missionId: number): Promise<void>`
- `clearMission(missionId: number, callback: AsyncCallback<void>): void`
- `clearMission(missionId: number): Promise<void>`
- `clearAllMissions(callback: AsyncCallback<void>): void`
- `clearAllMissions(): Promise<void>`
- `moveMissionToFront(missionId: number, callback: AsyncCallback<void>): void`
- `moveMissionToFront(missionId: number, options: StartOptions, callback: AsyncCallback<void>): void`
- `moveMissionToFront(missionId: number, options?: StartOptions): Promise<void>`

### @ohos.application.testRunner (@ohos.application.testRunner.d.ts)
#### 接口
- **TestRunner**
  - `onPrepare`: void
  - `onRun`: void

### uriPermissionManager (@ohos.application.uriPermissionManager.d.ts)
#### 函数
- `grantUriPermission(
    uri: string,
    flag: wantConstant.Flags,
    targetBundleName: string,
    callback: AsyncCallback<number>
  ): void`
- `grantUriPermission(uri: string, flag: wantConstant.Flags, targetBundleName: string): Promise<number>`
- `revokeUriPermission(uri: string, targetBundleName: string, callback: AsyncCallback<number>): void`
- `revokeUriPermission(uri: string, targetBundleName: string): Promise<number>`

### @ohos.arkui.UIContext (@ohos.arkui.UIContext.d.ts)
#### 接口
- **AtomicServiceBar**
  - `setVisible`: void
  - `setBackgroundColor`: void
  - `setTitleContent`: void
  - `setTitleFontStyle`: void
  - `setIconColor`: void
#### 枚举
- **KeyboardAvoidMode**
  - `OFFSET` = 0
  - `RESIZE` = 1
#### 类
- **Font**
  - `registerFont()`: void
  - `getSystemFontList()`: Array<string>
  - `getFontByName()`: font.FontInfo
- **MediaQuery**
  - `matchMediaSync()`: mediaQuery.MediaQueryListener
- **UIInspector**
  - `createComponentObserver()`: inspector.ComponentObserver
- **Router**
  - `pushUrl()`: void
  - `pushUrl()`: Promise<void>
  - `pushUrl()`: void
  - `pushUrl()`: Promise<void>
  - `replaceUrl()`: void
  - `replaceUrl()`: Promise<void>
  - `replaceUrl()`: void
  - `replaceUrl()`: Promise<void>
  - `back()`: void
  - `clear()`: void
  - `getLength()`: string
  - `getState()`: router.RouterState
  - `showAlertBeforeBackPage()`: void
  - `hideAlertBeforeBackPage()`: void
  - `getParams()`: Object
  - `pushNamedRoute()`: void
  - `pushNamedRoute()`: Promise<void>
  - `pushNamedRoute()`: void
  - `pushNamedRoute()`: Promise<void>
  - `replaceNamedRoute()`: void
  - `replaceNamedRoute()`: Promise<void>
  - `replaceNamedRoute()`: void
  - `replaceNamedRoute()`: Promise<void>
- **PromptAction**
  - `showToast()`: void
  - `showDialog()`: void
  - `showDialog()`: Promise<promptAction.ShowDialogSuccessResponse>
  - `showActionMenu()`: void
  - `showActionMenu()`: void
  - `showActionMenu()`: Promise<promptAction.ActionMenuSuccessResponse>
- **UIObserver**
  - `on()`: void
  - `off()`: void
  - `on()`: void
  - `off()`: void
  - `on()`: void
  - `off()`: void
- **ComponentUtils**
  - `getRectangleById()`: componentUtils.ComponentInfo
- **DragController**
  - `executeDrag()`: void
  - `createDragAction()`: dragController.DragAction
  - `getDragPreview()`: dragController.DragPreview
- **UIContext**
  - `getFont()`: Font
  - `getMediaQuery()`: MediaQuery
  - `getUIInspector()`: UIInspector
  - `getRouter()`: Router
  - `getPromptAction()`: PromptAction
  - `getComponentUtils()`: ComponentUtils
  - `getUIObserver()`: UIObserver
  - `createAnimator()`: AnimatorResult
  - `showAlertDialog()`: void
  - `showActionSheet()`: void
  - `showDatePickerDialog()`: void
  - `showTimePickerDialog()`: void
  - `showTextPickerDialog()`: void
  - `setKeyboardAvoidMode()`: void
  - `getKeyboardAvoidMode()`: KeyboardAvoidMode
  - `getAtomicServiceBar()`: Nullable<AtomicServiceBar>
  - `getDragController()`: DragController
  - `keyframeAnimateTo()`: void

### @ohos.arkui.advanced.Chip (@ohos.arkui.advanced.Chip.d.ets)
#### 接口
- **IconCommonOptions**
  - `src`: ResourceStr
  - `size?`: SizeOptions
  - `fillColor?`: ResourceColor
- **SuffixIconOptions**
  - `action?`: () => void
- **PrefixIconOptions**
- **LabelMarginOptions**
  - `left?`: Dimension
  - `right?`: Dimension
- **LabelOptions**
  - `text`: string
  - `fontSize?`: Dimension
  - `fontColor?`: ResourceColor
  - `fontFamily?`: string
  - `labelMargin?`: LabelMarginOptions
- **ChipOptions**
  - `prefixIcon?`: PrefixIconOptions
  - `label`: LabelOptions
  - `suffixIcon?`: SuffixIconOptions
  - `allowClose?`: boolean
  - `enabled?`: boolean
  - `backgroundColor?`: ResourceColor
  - `borderRadius?`: Dimension
  - `size?`: ChipSize | SizeOptions
  - `onClose?`: () => void
#### 枚举
- **ChipSize**
  - `NORMAL` = "NORMAL"
  - `SMALL` = "SMALL"
#### 函数
- `Chip(options: ChipOptions): void`

### @ohos.arkui.advanced.ComposeListItem (@ohos.arkui.advanced.ComposeListItem.d.ets)
#### 枚举
- **IconType**
  - `BADGE` = 1
  - `NORMAL_ICON` = 2
  - `SYSTEM_ICON` = 3
  - `HEAD_SCULPTURE` = 4
  - `APP_ICON` = 5
  - `PREVIEW` = 6
  - `LONGITUDINAL` = 7
  - `VERTICAL` = 8
#### 类
- **OperateIcon**
  - `value`: ResourceStr
  - `action?`: () => void
- **OperateCheck**
  - `isCheck?`: boolean
  - `onChange?`: (value: boolean) => void
- **OperateButton**
  - `text?`: ResourceStr
- **ContentItem**
  - `iconStyle?`: IconType
  - `icon?`: ResourceStr
  - `primaryText?`: ResourceStr
  - `secondaryText?`: ResourceStr
  - `description?`: ResourceStr
- **OperateItem**
  - `icon?`: OperateIcon
  - `subIcon?`: OperateIcon
  - `button?`: OperateButton
  - `switch?`: OperateCheck
  - `checkbox?`: OperateCheck
  - `radio?`: OperateCheck
  - `image?`: ResourceStr
  - `text?`: ResourceStr
  - `arrow?`: OperateIcon

### @ohos.arkui.advanced.ComposeTitleBar (@ohos.arkui.advanced.ComposeTitleBar.d.ets)
#### 类
- **ComposeTitleBarMenuItem**
  - `value`: ResourceStr
  - `isEnabled?`: boolean
  - `action?`: () => void

### @ohos.arkui.advanced.Counter (@ohos.arkui.advanced.Counter.d.ets)
#### 枚举
- **CounterType**
  - `LIST` = 0
  - `COMPACT` = 1
  - `INLINE` = 2
  - `INLINE_DATE` = 3
#### 类
- **CommonOptions**
  - `focusable?`: boolean
  - `step?`: number
  - `onHoverIncrease?`: (isHover: boolean) => void
  - `onHoverDecrease?`: (isHover: boolean) => void
- **InlineStyleOptions**
  - `value?`: number
  - `min?`: number
  - `max?`: number
  - `textWidth?`: number
  - `onChange?`: (value: number) => void
- **NumberStyleOptions**
  - `label?`: ResourceStr
  - `onFocusIncrease?`: () => void
  - `onFocusDecrease?`: () => void
  - `onBlurIncrease?`: () => void
  - `onBlurDecrease?`: () => void
- **DateData**
  - `toString()`: string
  - `year`: number
  - `month`: number
  - `day`: number
- **DateStyleOptions**
  - `year?`: number
  - `month?`: number
  - `day?`: number
  - `onDateChange?`: (date: DateData) => void
- **CounterOptions**
  - `type`: CounterType
  - `numberOptions?`: NumberStyleOptions
  - `inlineOptions?`: InlineStyleOptions
  - `dateOptions?`: DateStyleOptions

### @ohos.arkui.advanced.Dialog (@ohos.arkui.advanced.Dialog.d.ets)
#### 类
- **ButtonOptions**
  - `value`: ResourceStr
  - `action?`: () => void
  - `background?`: ResourceColor
  - `fontColor?`: ResourceColor

### @ohos.arkui.advanced.EditableTitleBar (@ohos.arkui.advanced.EditableTitleBar.d.ets)
#### 枚举
- **EditableLeftIconType**
  - `Back` = 0
  - `Cancel` = 1
#### 类
- **EditableTitleBarMenuItem**
  - `value`: ResourceStr
  - `isEnabled?`: boolean
  - `action?`: () => void

### @ohos.arkui.advanced.ExceptionPrompt (@ohos.arkui.advanced.ExceptionPrompt.d.ets)
#### 接口
- **PromptOptions**
  - `icon?`: ResourceStr
  - `tip?`: ResourceStr
  - `marginType`: MarginType
  - `actionText?`: ResourceStr
#### 枚举
- **MarginType**
  - `DEFAULT_MARGIN` = 0
  - `FIT_MARGIN` = 1

### @ohos.arkui.advanced.Filter (@ohos.arkui.advanced.Filter.d.ets)
#### 枚举
- **FilterType**
  - `MULTI_LINE_FILTER` = 0
  - `LIST_FILTER` = 1
#### 类
- **FilterParams**
  - `name`: ResourceStr
  - `options`: Array<ResourceStr>
- **FilterResult**
  - `name`: ResourceStr
  - `index`: number
  - `value`: ResourceStr

### @ohos.arkui.advanced.GridObjectSortComponent (@ohos.arkui.advanced.GridObjectSortComponent.d.ets)
#### 接口
- **GridObjectSortComponentItem**
  - `id`: number | string
  - `text`: ResourceStr
  - `selected`: boolean
  - `order`: number
  - `url?`: ResourceStr
- **GridObjectSortComponentOptions**
  - `type?`: GridObjectSortComponentType
  - `imageSize?`: number | Resource
  - `normalTitle?`: ResourceStr
  - `editTitle?`: ResourceStr
  - `showAreaTitle?`: ResourceStr
  - `addAreaTitle?`: ResourceStr
#### 枚举
- **GridObjectSortComponentType**
  - `IMAGE_TEXT` = "image_text"
  - `TEXT` = "text"

### @ohos.arkui.advanced.Popup (@ohos.arkui.advanced.Popup.d.ets)
#### 接口
- **PopupTextOptions**
  - `text`: ResourceStr
  - `fontSize?`: number | string | Resource
  - `fontColor?`: ResourceColor
  - `fontWeight?`: number | FontWeight | string
- **PopupButtonOptions**
  - `text`: ResourceStr
  - `action?`: () => void
  - `fontSize?`: number | string | Resource
  - `fontColor?`: ResourceColor
- **PopupIconOptions**
  - `image`: ResourceStr
  - `width?`: Dimension
  - `height?`: Dimension
  - `fillColor?`: ResourceColor
  - `borderRadius?`: Length | BorderRadiuses
- **PopupOptions**
  - `icon?`: PopupIconOptions
  - `title?`: PopupTextOptions
  - `message`: PopupTextOptions
  - `showClose?`: boolean | Resource
  - `onClose?`: () => void
  - `buttons?`: [PopupButtonOptions?, PopupButtonOptions?]
#### 函数
- `Popup(options: PopupOptions): void`

### @ohos.arkui.advanced.ProgressButton (@ohos.arkui.advanced.ProgressButton.d.ets)

### @ohos.arkui.advanced.SegmentButton (@ohos.arkui.advanced.SegmentButton.d.ets)
#### 接口
- **SegmentButtonTextItem**
  - `text`: ResourceStr
- **SegmentButtonIconItem**
  - `icon`: ResourceStr
  - `selectedIcon`: ResourceStr
- **SegmentButtonIconTextItem**
  - `icon`: ResourceStr
  - `selectedIcon`: ResourceStr
  - `text`: ResourceStr
- **CommonSegmentButtonOptions**
  - `fontColor?`: ResourceColor
  - `selectedFontColor?`: ResourceColor
  - `fontSize?`: DimensionNoPercentage
  - `selectedFontSize?`: DimensionNoPercentage
  - `fontWeight?`: FontWeight
  - `selectedFontWeight?`: FontWeight
  - `backgroundColor?`: ResourceColor
  - `selectedBackgroundColor?`: ResourceColor
  - `imageSize?`: SizeOptions
  - `buttonPadding?`: Padding | Dimension
  - `textPadding?`: Padding | Dimension
  - `backgroundBlurStyle?`: BlurStyle
- **TabSegmentButtonConstructionOptions**
  - `buttons`: ItemRestriction<SegmentButtonTextItem>
- **CapsuleSegmentButtonConstructionOptions**
  - `buttons`: SegmentButtonItemTuple
  - `multiply?`: boolean
- **TabSegmentButtonOptions**
  - `type`: "tab"
- **CapsuleSegmentButtonOptions**
  - `type`: "capsule"
- **SegmentButtonItemOptionsConstructorOptions**
  - `icon?`: ResourceStr
  - `selectedIcon?`: ResourceStr
  - `text?`: ResourceStr
#### 类
- **SegmentButtonItemOptions**
  - `icon?`: ResourceStr
  - `selectedIcon?`: ResourceStr
  - `text?`: ResourceStr
- **SegmentButtonOptions**
  - `tab()`: SegmentButtonOptions
  - `capsule()`: SegmentButtonOptions
  - `type`: "tab" | "capsule"
  - `multiply`: boolean
  - `buttons`: SegmentButtonItemOptionsArray
  - `fontColor`: ResourceColor
  - `selectedFontColor`: ResourceColor
  - `fontSize`: DimensionNoPercentage
  - `selectedFontSize`: DimensionNoPercentage
  - `fontWeight`: FontWeight
  - `selectedFontWeight`: FontWeight
  - `backgroundColor`: ResourceColor
  - `selectedBackgroundColor`: ResourceColor
  - `imageSize`: SizeOptions
  - `buttonPadding`: Padding | Dimension
  - `textPadding`: Padding | Dimension
  - `backgroundBlurStyle`: BlurStyle
#### 类型别名
- `DimensionNoPercentage` = PX | VP | FP | LPX | Resource
- `ItemRestriction` = [T, T, T?, T?, T?]
- `SegmentButtonItemTuple` = ItemRestriction<SegmentButtonTextItem> | ItemRestriction<SegmentButtonIconItem> | ItemRestriction<SegmentButtonIconTextItem>
- `SegmentButtonItemArray` = Array<SegmentButtonTextItem> | Array<SegmentButtonIconItem> | Array<SegmentButtonIconTextItem>

### @ohos.arkui.advanced.SelectTitleBar (@ohos.arkui.advanced.SelectTitleBar.d.ets)
#### 类
- **SelectTitleBarMenuItem**
  - `value`: ResourceStr
  - `isEnabled?`: boolean
  - `action?`: () => void

### @ohos.arkui.advanced.SelectionMenu (@ohos.arkui.advanced.SelectionMenu.d.ets)
#### 接口
- **EditorMenuOptions**
  - `icon`: ResourceStr
  - `action?`: () => void
  - `builder?`: () => void
- **ExpandedMenuOptions**
  - `action?`: () => void
- **EditorEventInfo**
  - `content?`: RichEditorSelection
- **SelectionMenuOptions**
  - `editorMenuOptions?`: Array<EditorMenuOptions>
  - `expandedMenuOptions?`: Array<ExpandedMenuOptions>
  - `controller?`: RichEditorController
  - `onPaste?`: (event?: EditorEventInfo) => void
  - `onCopy?`: (event?: EditorEventInfo) => void
  - `onCut?`: (event?: EditorEventInfo) => void
  - `onSelectAll?`: (event?: EditorEventInfo) => void
#### 函数
- `SelectionMenu(options: SelectionMenuOptions): void`

### @ohos.arkui.advanced.SplitLayout (@ohos.arkui.advanced.SplitLayout.d.ets)

### @ohos.arkui.advanced.SubHeader (@ohos.arkui.advanced.SubHeader.d.ets)
#### 枚举
- **OperationType**
  - `TEXT_ARROW` = 0
  - `BUTTON` = 1
  - `ICON_GROUP` = 2
  - `LOADING` = 3
#### 类
- **OperationOption**
  - `value`: ResourceStr
  - `action?`: () => void
- **SelectOptions**
  - `options`: Array<SelectOption>
  - `selected?`: number
  - `value?`: string
  - `onSelect?`: (index: number, value?: string) => void

### @ohos.arkui.advanced.SwipeRefresher (@ohos.arkui.advanced.SwipeRefresher.d.ets)

### @ohos.arkui.advanced.TabTitleBar (@ohos.arkui.advanced.TabTitleBar.d.ets)
#### 类
- **TabTitleBarMenuItem**
  - `value`: ResourceStr
  - `isEnabled?`: boolean
  - `action?`: () => void
- **TabTitleBarTabItem**
  - `title`: ResourceStr
  - `icon?`: ResourceStr

### @ohos.arkui.advanced.ToolBar (@ohos.arkui.advanced.ToolBar.d.ets)
#### 枚举
- **ItemState**
  - `ENABLE` = 1
  - `DISABLE` = 2
  - `ACTIVATE` = 3
#### 类
- **ToolBarOption**
  - `content`: ResourceStr
  - `action?`: () => void
  - `icon?`: Resource
  - `state?`: ItemState

### @ohos.arkui.advanced.TreeView (@ohos.arkui.advanced.TreeView.d.ets)
#### 接口
- **CallbackParam**
- **NodeParam**
  - `isFolder?`: boolean
  - `icon?`: ResourceStr
  - `selectedIcon?`: ResourceStr
  - `editIcon?`: ResourceStr
  - `primaryTitle?`: ResourceStr
  - `secondaryTitle?`: ResourceStr
  - `container?`: () => void
#### 枚举
- **TreeListenType**
  - `NODE_CLICK` = "NodeClick"
  - `NODE_ADD` = "NodeAdd"
  - `NODE_DELETE` = "NodeDelete"
  - `NODE_MODIFY` = "NodeModify"
  - `NODE_MOVE` = "NodeMove"
#### 类
- **TreeListener**
- **TreeListenerManager**
  - `getInstance()`: TreeListenerManager
  - `getTreeListener()`: TreeListener
- **TreeController**
  - `removeNode()`: void
  - `modifyNode()`: void
  - `addNode()`: TreeController
  - `refreshNode()`: void
  - `buildDone()`: void

### componentSnapshot (@ohos.arkui.componentSnapshot.d.ts)
#### 函数
- `get(id: string, callback: AsyncCallback<image.PixelMap>): void`
- `get(id: string): Promise<image.PixelMap>`
- `createFromBuilder(builder: CustomBuilder, callback: AsyncCallback<image.PixelMap>): void`
- `createFromBuilder(builder: CustomBuilder): Promise<image.PixelMap>`

### componentUtils (@ohos.arkui.componentUtils.d.ts)
#### 接口
- **ComponentInfo**
- **Size**
- **Offset**
- **TranslateResult**
- **ScaleResult**
- **RotateResult**
#### 函数
- `getRectangleById(id: string): ComponentInfo`
#### 类型别名
- `Matrix4Result` = [
    number,
    number,
    number,
    number,
    number,
    number,
    number,
    number,
    number,
    number,
    number,
    number,
    

### dragController (@ohos.arkui.dragController.d.ts)
#### 接口
- **DragAndDropInfo**
  - `status`: DragStatus
  - `event`: DragEvent
  - `extraParams?`: string
- **DragAction**
  - `startDrag`: Promise<void>
  - `on`: void
  - `off`: void
- **DragInfo**
  - `pointerId`: number
  - `data?`: unifiedDataChannel.UnifiedData
  - `extraParams?`: string
  - `touchPoint?`: TouchPoint
  - `previewOptions?`: DragPreviewOptions
- **AnimationOptions**
  - `duration?`: number
  - `curve?`: Curve | ICurve
#### 枚举
- **DragStatus**
  - `STARTED` = 0
  - `ENDED` = 1
#### 函数
- `executeDrag(custom: CustomBuilder | DragItemInfo, dragInfo: DragInfo, callback: AsyncCallback<{
    event: DragEvent, extraParams: string
  }>): void`
- `executeDrag(custom: CustomBuilder | DragItemInfo, dragInfo: DragInfo): Promise<{
    event: DragEvent, extraParams: string
  }>`
- `createDragAction(customArray: Array<CustomBuilder | DragItemInfo>, dragInfo: DragInfo): DragAction`
- `getDragPreview(): DragPreview`
#### 类
- **DragPreview**
  - `setForegroundColor()`: void

### @ohos.arkui.drawableDescriptor (@ohos.arkui.drawableDescriptor.d.ts)
#### 类
- **DrawableDescriptor**
  - `getPixelMap()`: image.PixelMap
- **LayeredDrawableDescriptor**
  - `getForeground()`: DrawableDescriptor
  - `getBackground()`: DrawableDescriptor
  - `getMask()`: DrawableDescriptor
  - `getMaskClipPath()`: string

### inspector (@ohos.arkui.inspector.d.ts)
#### 接口
- **ComponentObserver**
#### 函数
- `createComponentObserver(id: string): ComponentObserver`

### @ohos.arkui.node (@ohos.arkui.node.d.ts)

### uiObserver (@ohos.arkui.observer.d.ts)
#### 接口
- **NavDestinationInfo**
#### 枚举
- **NavDestinationState**
  - `ON_SHOWN` = 0
  - `ON_HIDDEN` = 1
- **RouterPageState**
  - `ABOUT_TO_APPEAR` = 0
  - `ABOUT_TO_DISAPPEAR` = 1
  - `ON_PAGE_SHOW` = 2
  - `ON_PAGE_HIDE` = 3
  - `ON_BACK_PRESS` = 4
#### 函数
- `on(type: 'navDestinationUpdate', options: { navigationId: ResourceStr }, callback: Callback<NavDestinationInfo>): void`
- `off(type: 'navDestinationUpdate', options: { navigationId: ResourceStr }, callback?: Callback<NavDestinationInfo>): void`
- `on(type: 'navDestinationUpdate', callback: Callback<NavDestinationInfo>): void`
- `off(type: 'navDestinationUpdate', callback?: Callback<NavDestinationInfo>): void`
- `on(type: 'routerPageUpdate', context: UIAbilityContext | UIContext, callback: Callback<RouterPageInfo>): void`
- `off(type: 'routerPageUpdate', context: UIAbilityContext | UIContext, callback?: Callback<RouterPageInfo>): void`
#### 类
- **RouterPageInfo**
  - `context`: UIAbilityContext | UIContext
  - `index`: number
  - `name`: string
  - `path`: string
  - `state`: RouterPageState

### performanceMonitor (@ohos.arkui.performanceMonitor.d.ts)
#### 枚举
- **ActionType**
  - `LAST_DOWN` = 0
  - `LAST_UP` = 1
  - `FIRST_MOVE` = 2
#### 函数
- `begin(scene: string, startInputType: ActionType, note?: string): void`
- `end(scene: string): void`

### backgroundTaskManager (@ohos.backgroundTaskManager.d.ts)
#### 接口
- **DelaySuspendInfo**
  - `requestId`: number
  - `actualDelayTime`: number
#### 枚举
- **BackgroundMode**
  - `DATA_TRANSFER` = 1
  - `AUDIO_PLAYBACK` = 2
  - `AUDIO_RECORDING` = 3
  - `LOCATION` = 4
  - `BLUETOOTH_INTERACTION` = 5
  - `MULTI_DEVICE_CONNECTION` = 6
  - `WIFI_INTERACTION` = 7
  - `VOIP` = 8
  - `TASK_KEEPING` = 9
#### 函数
- `cancelSuspendDelay(requestId: number): void`
- `getRemainingDelayTime(requestId: number, callback: AsyncCallback<number>): void`
- `getRemainingDelayTime(requestId: number): Promise<number>`
- `requestSuspendDelay(reason: string, callback: Callback<void>): DelaySuspendInfo`
- `startBackgroundRunning(context: Context, bgMode: BackgroundMode, wantAgent: WantAgent, callback: AsyncCallback<void>): void`
- `startBackgroundRunning(context: Context, bgMode: BackgroundMode, wantAgent: WantAgent): Promise<void>`
- `stopBackgroundRunning(context: Context, callback: AsyncCallback<void>): void`
- `stopBackgroundRunning(context: Context): Promise<void>`

### @ohos.base (@ohos.base.d.ts)
#### 接口
- **Callback**
- **ErrorCallback**
- **AsyncCallback**
- **BusinessError**
  - `code`: number
  - `data?`: T

### batteryInfo (@ohos.batteryInfo.d.ts)
#### 枚举
- **BatteryPluggedType**
- **BatteryChargeState**
- **BatteryHealthState**
- **BatteryCapacityLevel**
- **CommonEventBatteryChangedKey**
  - `EXTRA_SOC` = 'soc'
  - `EXTRA_CHARGE_STATE` = 'chargeState'
  - `EXTRA_HEALTH_STATE` = 'healthState'
  - `EXTRA_PLUGGED_TYPE` = 'pluggedType'
  - `EXTRA_VOLTAGE` = 'voltage'
  - `EXTRA_TECHNOLOGY` = 'technology'
  - `EXTRA_TEMPERATURE` = 'temperature'
  - `EXTRA_PRESENT` = 'present'
  - `EXTRA_CAPACITY_LEVEL` = 'capacityLevel'
#### 函数
- `setBatteryConfig(sceneName: string, sceneValue: string): number`
- `getBatteryConfig(sceneName: string): string`
- `isBatteryConfigSupported(sceneName: string): boolean`

### batteryStats (@ohos.batteryStatistics.d.ts)
#### 接口
- **BatteryStatsInfo**
  - `uid`: number
  - `type`: ConsumptionType
  - `power`: number
#### 枚举
- **ConsumptionType**
  - `CONSUMPTION_TYPE_INVALID` = -17
#### 函数
- `getBatteryStats(): Promise<Array<BatteryStatsInfo>>`
- `getBatteryStats(callback: AsyncCallback<Array<BatteryStatsInfo>>): void`
- `getAppPowerValue(uid: number): number`
- `getAppPowerPercent(uid: number): number`
- `getHardwareUnitPowerValue(type: ConsumptionType): number`
- `getHardwareUnitPowerPercent(type: ConsumptionType): number`

### a2dp (@ohos.bluetooth.a2dp.d.ts)
#### 接口
- **A2dpSourceProfile**
  - `connect`: void
  - `disconnect`: void
  - `getPlayingState`: PlayingState
  - `isAbsoluteVolumeSupported`: Promise<boolean>
  - `isAbsoluteVolumeSupported`: void
  - `isAbsoluteVolumeEnabled`: Promise<boolean>
  - `isAbsoluteVolumeEnabled`: void
  - `enableAbsoluteVolume`: Promise<void>
  - `enableAbsoluteVolume`: void
  - `disableAbsoluteVolume`: Promise<void>
  - `disableAbsoluteVolume`: void
  - `getCurrentCodecInfo`: CodecInfo
  - `setCurrentCodecInfo`: void
- **CodecInfo**
  - `codecType`: CodecType
  - `codecBitsPerSample`: CodecBitsPerSample
  - `codecChannelMode`: CodecChannelMode
  - `codecSampleRate`: CodecSampleRate
#### 枚举
- **PlayingState**
- **CodecType**
  - `CODEC_TYPE_INVALID` = -1
  - `CODEC_TYPE_SBC` = 0
  - `CODEC_TYPE_AAC` = 1
  - `CODEC_TYPE_L2HC` = 2
- **CodecChannelMode**
  - `CODEC_CHANNEL_MODE_NONE` = 0
  - `CODEC_CHANNEL_MODE_MONO` = 1
  - `CODEC_CHANNEL_MODE_STEREO` = 2
- **CodecBitsPerSample**
  - `CODEC_BITS_PER_SAMPLE_NONE` = 0
  - `CODEC_BITS_PER_SAMPLE_16` = 1
  - `CODEC_BITS_PER_SAMPLE_24` = 2
  - `CODEC_BITS_PER_SAMPLE_32` = 3
- **CodecSampleRate**
  - `CODEC_SAMPLE_RATE_NONE` = 0
  - `CODEC_SAMPLE_RATE_44100` = 1
  - `CODEC_SAMPLE_RATE_48000` = 2
  - `CODEC_SAMPLE_RATE_88200` = 3
  - `CODEC_SAMPLE_RATE_96000` = 4
  - `CODEC_SAMPLE_RATE_176400` = 5
  - `CODEC_SAMPLE_RATE_192000` = 6
#### 函数
- `createA2dpSrcProfile(): A2dpSourceProfile`
#### 类型别名
- `BaseProfile` = baseProfile.BaseProfile

### access (@ohos.bluetooth.access.d.ts)
#### 枚举
- **BluetoothState**
  - `STATE_OFF` = 0
  - `STATE_TURNING_ON` = 1
  - `STATE_ON` = 2
  - `STATE_TURNING_OFF` = 3
  - `STATE_BLE_TURNING_ON` = 4
  - `STATE_BLE_ON` = 5
  - `STATE_BLE_TURNING_OFF` = 6
#### 函数
- `enableBluetooth(): void`
- `disableBluetooth(): void`
- `getState(): BluetoothState`
- `factoryReset(callback: AsyncCallback<void>): void`
- `factoryReset(): Promise<void>`
- `getLocalAddress(): string`
- `on(type: 'stateChange', callback: Callback<BluetoothState>): void`
- `off(type: 'stateChange', callback?: Callback<BluetoothState>): void`

### baseProfile (@ohos.bluetooth.baseProfile.d.ts)
#### 接口
- **StateChangeParam**
  - `deviceId`: string
  - `state`: ProfileConnectionState
- **BaseProfile**
  - `setConnectionStrategy`: Promise<void>
  - `setConnectionStrategy`: void
  - `getConnectionStrategy`: void
  - `getConnectionStrategy`: Promise<ConnectionStrategy>
  - `getConnectedDevices`: Array<string>
  - `getConnectionState`: ProfileConnectionState
  - `on`: void
  - `off`: void
#### 枚举
- **ConnectionStrategy**
  - `CONNECTION_STRATEGY_UNSUPPORTED` = 0
  - `CONNECTION_STRATEGY_ALLOWED` = 1
  - `CONNECTION_STRATEGY_FORBIDDEN` = 2
#### 类型别名
- `ProfileConnectionState` = constant.ProfileConnectionState

### ble (@ohos.bluetooth.ble.d.ts)
#### 接口
- **GattServer**
  - `addService`: void
  - `removeService`: void
  - `close`: void
  - `notifyCharacteristicChanged`: void
  - `notifyCharacteristicChanged`: Promise<void>
  - `sendResponse`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **GattClientDevice**
  - `connect`: void
  - `disconnect`: void
  - `close`: void
  - `getDeviceName`: void
  - `getDeviceName`: Promise<string>
  - `getServices`: void
  - `getServices`: Promise<Array<GattService>>
  - `readCharacteristicValue`: void
  - `readCharacteristicValue`: Promise<BLECharacteristic>
  - `readDescriptorValue`: void
  - `readDescriptorValue`: Promise<BLEDescriptor>
  - `writeCharacteristicValue`: void
  - `writeCharacteristicValue`: Promise<void>
  - `writeDescriptorValue`: void
  - `writeDescriptorValue`: Promise<void>
  - `getRssiValue`: void
  - `getRssiValue`: Promise<number>
  - `setBLEMtuSize`: void
  - `setCharacteristicChangeNotification`: void
  - `setCharacteristicChangeNotification`: Promise<void>
  - `setCharacteristicChangeIndication`: void
  - `setCharacteristicChangeIndication`: Promise<void>
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **GattService**
  - `serviceUuid`: string
  - `isPrimary`: boolean
  - `characteristics`: Array<BLECharacteristic>
  - `includeServices?`: Array<GattService>
- **BLECharacteristic**
  - `serviceUuid`: string
  - `characteristicUuid`: string
  - `characteristicValue`: ArrayBuffer
  - `descriptors`: Array<BLEDescriptor>
  - `properties?`: GattProperties
- **BLEDescriptor**
  - `serviceUuid`: string
  - `characteristicUuid`: string
  - `descriptorUuid`: string
  - `descriptorValue`: ArrayBuffer
- **NotifyCharacteristic**
  - `serviceUuid`: string
  - `characteristicUuid`: string
  - `characteristicValue`: ArrayBuffer
  - `confirm`: boolean
- **CharacteristicReadRequest**
  - `deviceId`: string
  - `transId`: number
  - `offset`: number
  - `characteristicUuid`: string
  - `serviceUuid`: string
- **CharacteristicWriteRequest**
  - `deviceId`: string
  - `transId`: number
  - `offset`: number
  - `isPrepared`: boolean
  - `needRsp`: boolean
  - `value`: ArrayBuffer
  - `characteristicUuid`: string
  - `serviceUuid`: string
- **DescriptorReadRequest**
  - `deviceId`: string
  - `transId`: number
  - `offset`: number
  - `descriptorUuid`: string
  - `characteristicUuid`: string
  - `serviceUuid`: string
- **DescriptorWriteRequest**
  - `deviceId`: string
  - `transId`: number
  - `offset`: number
  - `isPrepared`: boolean
  - `needRsp`: boolean
  - `value`: ArrayBuffer
  - `descriptorUuid`: string
  - `characteristicUuid`: string
  - `serviceUuid`: string
- **ServerResponse**
  - `deviceId`: string
  - `transId`: number
  - `status`: number
  - `offset`: number
  - `value`: ArrayBuffer
- **BLEConnectionChangeState**
  - `deviceId`: string
  - `state`: ProfileConnectionState
- **ScanResult**
  - `deviceId`: string
  - `rssi`: number
  - `data`: ArrayBuffer
  - `deviceName`: string
  - `connectable`: boolean
- **AdvertiseSetting**
  - `interval?`: number
  - `txPower?`: number
  - `connectable?`: boolean
- **AdvertiseData**
  - `serviceUuids`: Array<string>
  - `manufactureData`: Array<ManufactureData>
  - `serviceData`: Array<ServiceData>
  - `includeDeviceName?`: boolean
- **AdvertisingParams**
  - `advertisingSettings`: AdvertiseSetting
  - `advertisingData`: AdvertiseData
  - `advertisingResponse?`: AdvertiseData
  - `duration?`: number
- **AdvertisingEnableParams**
  - `advertisingId`: number
  - `duration?`: number
- **AdvertisingDisableParams**
  - `advertisingId`: number
- **AdvertisingStateChangeInfo**
  - `advertisingId`: number
  - `state`: AdvertisingState
- **ManufactureData**
  - `manufactureId`: number
  - `manufactureValue`: ArrayBuffer
- **ServiceData**
  - `serviceUuid`: string
  - `serviceValue`: ArrayBuffer
- **ScanFilter**
  - `deviceId?`: string
  - `name?`: string
  - `serviceUuid?`: string
  - `serviceUuidMask?`: string
  - `serviceSolicitationUuid?`: string
  - `serviceSolicitationUuidMask?`: string
  - `serviceData?`: ArrayBuffer
  - `serviceDataMask?`: ArrayBuffer
  - `manufactureId?`: number
  - `manufactureData?`: ArrayBuffer
  - `manufactureDataMask?`: ArrayBuffer
- **ScanOptions**
  - `interval?`: number
  - `dutyMode?`: ScanDuty
  - `matchMode?`: MatchMode
- **GattProperties**
  - `write?`: boolean
  - `writeNoResponse?`: boolean
  - `read?`: boolean
  - `notify?`: boolean
  - `indicate?`: boolean
#### 枚举
- **GattWriteType**
  - `WRITE` = 1
  - `WRITE_NO_RESPONSE` = 2
- **ScanDuty**
  - `SCAN_MODE_LOW_POWER` = 0
  - `SCAN_MODE_BALANCED` = 1
  - `SCAN_MODE_LOW_LATENCY` = 2
- **MatchMode**
  - `MATCH_MODE_AGGRESSIVE` = 1
  - `MATCH_MODE_STICKY` = 2
- **AdvertisingState**
  - `STARTED` = 1
  - `ENABLED` = 2
  - `DISABLED` = 3
  - `STOPPED` = 4
#### 函数
- `createGattServer(): GattServer`
- `createGattClientDevice(deviceId: string): GattClientDevice`
- `getConnectedBLEDevices(): Array<string>`
- `startBLEScan(filters: Array<ScanFilter>, options?: ScanOptions): void`
- `stopBLEScan(): void`
- `startAdvertising(setting: AdvertiseSetting, advData: AdvertiseData, advResponse?: AdvertiseData): void`
- `stopAdvertising(): void`
- `startAdvertising(advertisingParams: AdvertisingParams, callback: AsyncCallback<number>): void`
- `startAdvertising(advertisingParams: AdvertisingParams): Promise<number>`
- `enableAdvertising(advertisingEnableParams: AdvertisingEnableParams, callback: AsyncCallback<void>): void`
- `enableAdvertising(advertisingEnableParams: AdvertisingEnableParams): Promise<void>`
- `disableAdvertising(advertisingDisableParams: AdvertisingDisableParams, callback: AsyncCallback<void>): void`
- `disableAdvertising(advertisingDisableParams: AdvertisingDisableParams): Promise<void>`
- `stopAdvertising(advertisingId: number, callback: AsyncCallback<void>): void`
- `stopAdvertising(advertisingId: number): Promise<void>`
- `on(type: 'advertisingStateChange', callback: Callback<AdvertisingStateChangeInfo>): void`
- `off(type: 'advertisingStateChange', callback?: Callback<AdvertisingStateChangeInfo>): void`
- `on(type: 'BLEDeviceFind', callback: Callback<Array<ScanResult>>): void`
- `off(type: 'BLEDeviceFind', callback?: Callback<Array<ScanResult>>): void`
#### 类型别名
- `ProfileConnectionState` = constant.ProfileConnectionState

### connection (@ohos.bluetooth.connection.d.ts)
#### 接口
- **BondStateParam**
  - `deviceId`: string
  - `state`: BondState
- **PinRequiredParam**
  - `deviceId`: string
  - `pinCode`: string
  - `pinType`: PinType
- **DeviceClass**
  - `majorClass`: MajorClass
  - `majorMinorClass`: MajorMinorClass
  - `classOfDevice`: number
#### 枚举
- **BluetoothTransport**
  - `TRANSPORT_BR_EDR` = 0
  - `TRANSPORT_LE` = 1
- **ScanMode**
  - `SCAN_MODE_NONE` = 0
  - `SCAN_MODE_CONNECTABLE` = 1
  - `SCAN_MODE_GENERAL_DISCOVERABLE` = 2
  - `SCAN_MODE_LIMITED_DISCOVERABLE` = 3
  - `SCAN_MODE_CONNECTABLE_GENERAL_DISCOVERABLE` = 4
  - `SCAN_MODE_CONNECTABLE_LIMITED_DISCOVERABLE` = 5
- **BondState**
  - `BOND_STATE_INVALID` = 0
  - `BOND_STATE_BONDING` = 1
  - `BOND_STATE_BONDED` = 2
- **PinType**
  - `PIN_TYPE_ENTER_PIN_CODE` = 0
  - `PIN_TYPE_ENTER_PASSKEY` = 1
  - `PIN_TYPE_CONFIRM_PASSKEY` = 2
  - `PIN_TYPE_NO_PASSKEY_CONSENT` = 3
  - `PIN_TYPE_NOTIFY_PASSKEY` = 4
  - `PIN_TYPE_DISPLAY_PIN_CODE` = 5
  - `PIN_TYPE_OOB_CONSENT` = 6
  - `PIN_TYPE_PIN_16_DIGITS` = 7
#### 函数
- `getProfileConnectionState(profileId?: ProfileId): ProfileConnectionState`
- `pairDevice(deviceId: string, callback: AsyncCallback<void>): void`
- `pairDevice(deviceId: string): Promise<void>`
- `pairCredibleDevice(deviceId: string, transport: BluetoothTransport, callback: AsyncCallback<void>): void`
- `pairCredibleDevice(deviceId: string, transport: BluetoothTransport): Promise<void>`
- `cancelPairedDevice(deviceId: string, callback: AsyncCallback<void>): void`
- `cancelPairedDevice(deviceId: string): Promise<void>`
- `cancelPairingDevice(deviceId: string, callback: AsyncCallback<void>): void`
- `cancelPairingDevice(deviceId: string): Promise<void>`
- `getRemoteDeviceName(deviceId: string): string`
- `getRemoteDeviceClass(deviceId: string): DeviceClass`
- `getLocalName(): string`
- `getPairedDevices(): Array<string>`
- `getPairState(deviceId: string): BondState`
- `setDevicePairingConfirmation(deviceId: string, accept: boolean): void`
- `setDevicePinCode(deviceId: string, code: string, callback: AsyncCallback<void>): void`
- `setDevicePinCode(deviceId: string, code: string): Promise<void>`
- `setLocalName(name: string): void`
- `setBluetoothScanMode(mode: ScanMode, duration: number): void`
- `getBluetoothScanMode(): ScanMode`
- `startBluetoothDiscovery(): void`
- `stopBluetoothDiscovery(): void`
- `isBluetoothDiscovering(): boolean`
- `getLocalProfileUuids(callback: AsyncCallback<Array<ProfileUuids>>): void`
- `getLocalProfileUuids(): Promise<Array<ProfileUuids>>`
- `getRemoteProfileUuids(deviceId: string, callback: AsyncCallback<Array<ProfileUuids>>): void`
- `getRemoteProfileUuids(deviceId: string): Promise<Array<ProfileUuids>>`
- `connectAllowedProfiles(deviceId: string, callback: AsyncCallback<void>): void`
- `connectAllowedProfiles(deviceId: string): Promise<void>`
- `disconnectAllowedProfiles(deviceId: string, callback: AsyncCallback<void>): void`
- `getRemoteProductId(deviceId: string): string`
- `disconnectAllowedProfiles(deviceId: string): Promise<void>`
- `on(type: 'bluetoothDeviceFind', callback: Callback<Array<string>>): void`
- `off(type: 'bluetoothDeviceFind', callback?: Callback<Array<string>>): void`
- `on(type: 'bondStateChange', callback: Callback<BondStateParam>): void`
- `off(type: 'bondStateChange', callback?: Callback<BondStateParam>): void`
- `on(type: 'pinRequired', callback: Callback<PinRequiredParam>): void`
- `off(type: 'pinRequired', callback?: Callback<PinRequiredParam>): void`
#### 类型别名
- `ProfileConnectionState` = constant.ProfileConnectionState
- `ProfileId` = constant.ProfileId
- `ProfileUuids` = constant.ProfileUuids
- `MajorClass` = constant.MajorClass
- `MajorMinorClass` = constant.MajorMinorClass