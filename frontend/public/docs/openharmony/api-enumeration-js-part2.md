# OpenHarmony JS/TS SDK API Enumeration — Part 2

**Files covered:** 140

**Total API elements:** 5589


### constant (@ohos.bluetooth.constant.d.ts)
#### Enums
- **ProfileId**
  - `PROFILE_A2DP_SOURCE` = 1
  - `PROFILE_HANDSFREE_AUDIO_GATEWAY` = 4
  - `PROFILE_HID_HOST` = 6
  - `PROFILE_PAN_NETWORK` = 7
- **ProfileUuids**
  - `PROFILE_UUID_HFP_AG` = '0000111F-0000-1000-8000-00805F9B34FB'
  - `PROFILE_UUID_HFP_HF` = '0000111E-0000-1000-8000-00805F9B34FB'
  - `PROFILE_UUID_HSP_AG` = '00001112-0000-1000-8000-00805F9B34FB'
  - `PROFILE_UUID_HSP_HS` = '00001108-0000-1000-8000-00805F9B34FB'
  - `PROFILE_UUID_A2DP_SRC` = '0000110A-0000-1000-8000-00805F9B34FB'
  - `PROFILE_UUID_A2DP_SINK` = '0000110B-0000-1000-8000-00805F9B34FB'
  - `PROFILE_UUID_AVRCP_CT` = '0000110E-0000-1000-8000-00805F9B34FB'
  - `PROFILE_UUID_AVRCP_TG` = '0000110C-0000-1000-8000-00805F9B34FB'
  - `PROFILE_UUID_HID` = '00001124-0000-1000-8000-00805F9B34FB'
  - `PROFILE_UUID_HOGP` = '00001812-0000-1000-8000-00805F9B34FB'
- **ProfileConnectionState**
  - `STATE_DISCONNECTED` = 0
  - `STATE_CONNECTING` = 1
  - `STATE_CONNECTED` = 2
  - `STATE_DISCONNECTING` = 3
- **MajorClass**
  - `MAJOR_MISC` = 0x0000
  - `MAJOR_COMPUTER` = 0x0100
  - `MAJOR_PHONE` = 0x0200
  - `MAJOR_NETWORKING` = 0x0300
  - `MAJOR_AUDIO_VIDEO` = 0x0400
  - `MAJOR_PERIPHERAL` = 0x0500
  - `MAJOR_IMAGING` = 0x0600
  - `MAJOR_WEARABLE` = 0x0700
  - `MAJOR_TOY` = 0x0800
  - `MAJOR_HEALTH` = 0x0900
  - `MAJOR_UNCATEGORIZED` = 0x1F00
- **MajorMinorClass**
  - `COMPUTER_UNCATEGORIZED` = 0x0100
  - `COMPUTER_DESKTOP` = 0x0104
  - `COMPUTER_SERVER` = 0x0108
  - `COMPUTER_LAPTOP` = 0x010C
  - `COMPUTER_HANDHELD_PC_PDA` = 0x0110
  - `COMPUTER_PALM_SIZE_PC_PDA` = 0x0114
  - `COMPUTER_WEARABLE` = 0x0118
  - `COMPUTER_TABLET` = 0x011C
  - `PHONE_UNCATEGORIZED` = 0x0200
  - `PHONE_CELLULAR` = 0x0204
  - `PHONE_CORDLESS` = 0x0208
  - `PHONE_SMART` = 0x020C
  - `PHONE_MODEM_OR_GATEWAY` = 0x0210
  - `PHONE_ISDN` = 0x0214
  - `NETWORK_FULLY_AVAILABLE` = 0x0300
  - `NETWORK_1_TO_17_UTILIZED` = 0x0320
  - `NETWORK_17_TO_33_UTILIZED` = 0x0340
  - `NETWORK_33_TO_50_UTILIZED` = 0x0360
  - `NETWORK_60_TO_67_UTILIZED` = 0x0380
  - `NETWORK_67_TO_83_UTILIZED` = 0x03A0
  - `NETWORK_83_TO_99_UTILIZED` = 0x03C0
  - `NETWORK_NO_SERVICE` = 0x03E0
  - `AUDIO_VIDEO_UNCATEGORIZED` = 0x0400
  - `AUDIO_VIDEO_WEARABLE_HEADSET` = 0x0404
  - `AUDIO_VIDEO_HANDSFREE` = 0x0408
  - `AUDIO_VIDEO_MICROPHONE` = 0x0410
  - `AUDIO_VIDEO_LOUDSPEAKER` = 0x0414
  - `AUDIO_VIDEO_HEADPHONES` = 0x0418
  - `AUDIO_VIDEO_PORTABLE_AUDIO` = 0x041C
  - `AUDIO_VIDEO_CAR_AUDIO` = 0x0420
  - `AUDIO_VIDEO_SET_TOP_BOX` = 0x0424
  - `AUDIO_VIDEO_HIFI_AUDIO` = 0x0428
  - `AUDIO_VIDEO_VCR` = 0x042C
  - `AUDIO_VIDEO_VIDEO_CAMERA` = 0x0430
  - `AUDIO_VIDEO_CAMCORDER` = 0x0434
  - `AUDIO_VIDEO_VIDEO_MONITOR` = 0x0438
  - `AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER` = 0x043C
  - `AUDIO_VIDEO_VIDEO_CONFERENCING` = 0x0440
  - `AUDIO_VIDEO_VIDEO_GAMING_TOY` = 0x0448
  - `PERIPHERAL_NON_KEYBOARD_NON_POINTING` = 0x0500
  - `PERIPHERAL_KEYBOARD` = 0x0540
  - `PERIPHERAL_POINTING_DEVICE` = 0x0580
  - `PERIPHERAL_KEYBOARD_POINTING` = 0x05C0
  - `PERIPHERAL_UNCATEGORIZED` = 0x0500
  - `PERIPHERAL_JOYSTICK` = 0x0504
  - `PERIPHERAL_GAMEPAD` = 0x0508
  - `PERIPHERAL_REMOTE_CONTROL` = 0x05C0
  - `PERIPHERAL_SENSING_DEVICE` = 0x0510
  - `PERIPHERAL_DIGITIZER_TABLET` = 0x0514
  - `PERIPHERAL_CARD_READER` = 0x0518
  - `PERIPHERAL_DIGITAL_PEN` = 0x051C
  - `PERIPHERAL_SCANNER_RFID` = 0x0520
  - `PERIPHERAL_GESTURAL_INPUT` = 0x0522
  - `IMAGING_UNCATEGORIZED` = 0x0600
  - `IMAGING_DISPLAY` = 0x0610
  - `IMAGING_CAMERA` = 0x0620
  - `IMAGING_SCANNER` = 0x0640
  - `IMAGING_PRINTER` = 0x0680
  - `WEARABLE_UNCATEGORIZED` = 0x0700
  - `WEARABLE_WRIST_WATCH` = 0x0704
  - `WEARABLE_PAGER` = 0x0708
  - `WEARABLE_JACKET` = 0x070C
  - `WEARABLE_HELMET` = 0x0710
  - `WEARABLE_GLASSES` = 0x0714
  - `TOY_UNCATEGORIZED` = 0x0800
  - `TOY_ROBOT` = 0x0804
  - `TOY_VEHICLE` = 0x0808
  - `TOY_DOLL_ACTION_FIGURE` = 0x080C
  - `TOY_CONTROLLER` = 0x0810
  - `TOY_GAME` = 0x0814
  - `HEALTH_UNCATEGORIZED` = 0x0900
  - `HEALTH_BLOOD_PRESSURE` = 0x0904
  - `HEALTH_THERMOMETER` = 0x0908
  - `HEALTH_WEIGHING` = 0x090C
  - `HEALTH_GLUCOSE` = 0x0910
  - `HEALTH_PULSE_OXIMETER` = 0x0914
  - `HEALTH_PULSE_RATE` = 0x0918
  - `HEALTH_DATA_DISPLAY` = 0x091C
  - `HEALTH_STEP_COUNTER` = 0x0920
  - `HEALTH_BODY_COMPOSITION_ANALYZER` = 0x0924
  - `HEALTH_PEAK_FLOW_MONITOR` = 0x0928
  - `HEALTH_MEDICATION_MONITOR` = 0x092C
  - `HEALTH_KNEE_PROSTHESIS` = 0x0930
  - `HEALTH_ANKLE_PROSTHESIS` = 0x0934
  - `HEALTH_GENERIC_HEALTH_MANAGER` = 0x0938
  - `HEALTH_PERSONAL_MOBILITY_DEVICE` = 0x093C
- **AccessAuthorization**
  - `UNKNOWN` = 0
  - `ALLOWED` = 1
  - `REJECTED` = 2

### bluetooth (@ohos.bluetooth.d.ts)
#### Interfaces
- **BaseProfile**
  - `getConnectionDevices`: Array<string>
  - `getDeviceState`: ProfileConnectionState
- **A2dpSourceProfile**
  - `connect`: boolean
  - `disconnect`: boolean
  - `on`: void
  - `off`: void
  - `getPlayingState`: PlayingState
- **HandsFreeAudioGatewayProfile**
  - `connect`: boolean
  - `disconnect`: boolean
  - `on`: void
  - `off`: void
- **GattServer**
  - `startAdvertising`: void
  - `stopAdvertising`: void
  - `addService`: boolean
  - `removeService`: boolean
  - `close`: void
  - `notifyCharacteristicChanged`: boolean
  - `sendResponse`: boolean
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
  - `connect`: boolean
  - `disconnect`: boolean
  - `close`: boolean
  - `getDeviceName`: void
  - `getDeviceName`: Promise<string>
  - `getServices`: void
  - `getServices`: Promise<Array<GattService>>
  - `readCharacteristicValue`: void
  - `readCharacteristicValue`: Promise<BLECharacteristic>
  - `readDescriptorValue`: void
  - `readDescriptorValue`: Promise<BLEDescriptor>
  - `writeCharacteristicValue`: boolean
  - `writeDescriptorValue`: boolean
  - `getRssiValue`: void
  - `getRssiValue`: Promise<number>
  - `setBLEMtuSize`: boolean
  - `setNotifyCharacteristicChanged`: boolean
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
- **CharacteristicReadReq**
  - `deviceId`: string
  - `transId`: number
  - `offset`: number
  - `characteristicUuid`: string
  - `serviceUuid`: string
- **CharacteristicWriteReq**
  - `deviceId`: string
  - `transId`: number
  - `offset`: number
  - `isPrep`: boolean
  - `needRsp`: boolean
  - `value`: ArrayBuffer
  - `characteristicUuid`: string
  - `serviceUuid`: string
- **DescriptorReadReq**
  - `deviceId`: string
  - `transId`: number
  - `offset`: number
  - `descriptorUuid`: string
  - `characteristicUuid`: string
  - `serviceUuid`: string
- **DescriptorWriteReq**
  - `deviceId`: string
  - `transId`: number
  - `offset`: number
  - `isPrep`: boolean
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
- **BLEConnectChangedState**
  - `deviceId`: string
  - `state`: ProfileConnectionState
- **ScanResult**
  - `deviceId`: string
  - `rssi`: number
  - `data`: ArrayBuffer
- **AdvertiseSetting**
  - `interval?`: number
  - `txPower?`: number
  - `connectable?`: boolean
- **AdvertiseData**
  - `serviceUuids`: Array<string>
  - `manufactureData`: Array<ManufactureData>
  - `serviceData`: Array<ServiceData>
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
- **ScanOptions**
  - `interval?`: number
  - `dutyMode?`: ScanDuty
  - `matchMode?`: MatchMode
- **SppOption**
  - `uuid`: string
  - `secure`: boolean
  - `type`: SppType
- **PinRequiredParam**
  - `deviceId`: string
  - `pinCode`: string
- **DeviceClass**
  - `majorClass`: MajorClass
  - `majorMinorClass`: MajorMinorClass
  - `classOfDevice`: number
- **BondStateParam**
  - `deviceId`: string
  - `state`: BondState
- **StateChangeParam**
  - `deviceId`: string
  - `state`: ProfileConnectionState
#### Enums
- **ScanDuty**
  - `SCAN_MODE_LOW_POWER` = 0
  - `SCAN_MODE_BALANCED` = 1
  - `SCAN_MODE_LOW_LATENCY` = 2
- **MatchMode**
  - `MATCH_MODE_AGGRESSIVE` = 1
  - `MATCH_MODE_STICKY` = 2
- **ProfileConnectionState**
  - `STATE_DISCONNECTED` = 0
  - `STATE_CONNECTING` = 1
  - `STATE_CONNECTED` = 2
  - `STATE_DISCONNECTING` = 3
- **BluetoothState**
  - `STATE_OFF` = 0
  - `STATE_TURNING_ON` = 1
  - `STATE_ON` = 2
  - `STATE_TURNING_OFF` = 3
  - `STATE_BLE_TURNING_ON` = 4
  - `STATE_BLE_ON` = 5
  - `STATE_BLE_TURNING_OFF` = 6
- **SppType**
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
- **MajorClass**
  - `MAJOR_MISC` = 0x0000
  - `MAJOR_COMPUTER` = 0x0100
  - `MAJOR_PHONE` = 0x0200
  - `MAJOR_NETWORKING` = 0x0300
  - `MAJOR_AUDIO_VIDEO` = 0x0400
  - `MAJOR_PERIPHERAL` = 0x0500
  - `MAJOR_IMAGING` = 0x0600
  - `MAJOR_WEARABLE` = 0x0700
  - `MAJOR_TOY` = 0x0800
  - `MAJOR_HEALTH` = 0x0900
  - `MAJOR_UNCATEGORIZED` = 0x1F00
- **MajorMinorClass**
  - `COMPUTER_UNCATEGORIZED` = 0x0100
  - `COMPUTER_DESKTOP` = 0x0104
  - `COMPUTER_SERVER` = 0x0108
  - `COMPUTER_LAPTOP` = 0x010C
  - `COMPUTER_HANDHELD_PC_PDA` = 0x0110
  - `COMPUTER_PALM_SIZE_PC_PDA` = 0x0114
  - `COMPUTER_WEARABLE` = 0x0118
  - `COMPUTER_TABLET` = 0x011C
  - `PHONE_UNCATEGORIZED` = 0x0200
  - `PHONE_CELLULAR` = 0x0204
  - `PHONE_CORDLESS` = 0x0208
  - `PHONE_SMART` = 0x020C
  - `PHONE_MODEM_OR_GATEWAY` = 0x0210
  - `PHONE_ISDN` = 0x0214
  - `NETWORK_FULLY_AVAILABLE` = 0x0300
  - `NETWORK_1_TO_17_UTILIZED` = 0x0320
  - `NETWORK_17_TO_33_UTILIZED` = 0x0340
  - `NETWORK_33_TO_50_UTILIZED` = 0x0360
  - `NETWORK_60_TO_67_UTILIZED` = 0x0380
  - `NETWORK_67_TO_83_UTILIZED` = 0x03A0
  - `NETWORK_83_TO_99_UTILIZED` = 0x03C0
  - `NETWORK_NO_SERVICE` = 0x03E0
  - `AUDIO_VIDEO_UNCATEGORIZED` = 0x0400
  - `AUDIO_VIDEO_WEARABLE_HEADSET` = 0x0404
  - `AUDIO_VIDEO_HANDSFREE` = 0x0408
  - `AUDIO_VIDEO_MICROPHONE` = 0x0410
  - `AUDIO_VIDEO_LOUDSPEAKER` = 0x0414
  - `AUDIO_VIDEO_HEADPHONES` = 0x0418
  - `AUDIO_VIDEO_PORTABLE_AUDIO` = 0x041C
  - `AUDIO_VIDEO_CAR_AUDIO` = 0x0420
  - `AUDIO_VIDEO_SET_TOP_BOX` = 0x0424
  - `AUDIO_VIDEO_HIFI_AUDIO` = 0x0428
  - `AUDIO_VIDEO_VCR` = 0x042C
  - `AUDIO_VIDEO_VIDEO_CAMERA` = 0x0430
  - `AUDIO_VIDEO_CAMCORDER` = 0x0434
  - `AUDIO_VIDEO_VIDEO_MONITOR` = 0x0438
  - `AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER` = 0x043C
  - `AUDIO_VIDEO_VIDEO_CONFERENCING` = 0x0440
  - `AUDIO_VIDEO_VIDEO_GAMING_TOY` = 0x0448
  - `PERIPHERAL_NON_KEYBOARD_NON_POINTING` = 0x0500
  - `PERIPHERAL_KEYBOARD` = 0x0540
  - `PERIPHERAL_POINTING_DEVICE` = 0x0580
  - `PERIPHERAL_KEYBOARD_POINTING` = 0x05C0
  - `PERIPHERAL_UNCATEGORIZED` = 0x0500
  - `PERIPHERAL_JOYSTICK` = 0x0504
  - `PERIPHERAL_GAMEPAD` = 0x0508
  - `PERIPHERAL_REMOTE_CONTROL` = 0x05C0
  - `PERIPHERAL_SENSING_DEVICE` = 0x0510
  - `PERIPHERAL_DIGITIZER_TABLET` = 0x0514
  - `PERIPHERAL_CARD_READER` = 0x0518
  - `PERIPHERAL_DIGITAL_PEN` = 0x051C
  - `PERIPHERAL_SCANNER_RFID` = 0x0520
  - `PERIPHERAL_GESTURAL_INPUT` = 0x0522
  - `IMAGING_UNCATEGORIZED` = 0x0600
  - `IMAGING_DISPLAY` = 0x0610
  - `IMAGING_CAMERA` = 0x0620
  - `IMAGING_SCANNER` = 0x0640
  - `IMAGING_PRINTER` = 0x0680
  - `WEARABLE_UNCATEGORIZED` = 0x0700
  - `WEARABLE_WRIST_WATCH` = 0x0704
  - `WEARABLE_PAGER` = 0x0708
  - `WEARABLE_JACKET` = 0x070C
  - `WEARABLE_HELMET` = 0x0710
  - `WEARABLE_GLASSES` = 0x0714
  - `TOY_UNCATEGORIZED` = 0x0800
  - `TOY_ROBOT` = 0x0804
  - `TOY_VEHICLE` = 0x0808
  - `TOY_DOLL_ACTION_FIGURE` = 0x080C
  - `TOY_CONTROLLER` = 0x0810
  - `TOY_GAME` = 0x0814
  - `HEALTH_UNCATEGORIZED` = 0x0900
  - `HEALTH_BLOOD_PRESSURE` = 0x0904
  - `HEALTH_THERMOMETER` = 0x0908
  - `HEALTH_WEIGHING` = 0x090C
  - `HEALTH_GLUCOSE` = 0x0910
  - `HEALTH_PULSE_OXIMETER` = 0x0914
  - `HEALTH_PULSE_RATE` = 0x0918
  - `HEALTH_DATA_DISPLAY` = 0x091C
  - `HEALTH_STEP_COUNTER` = 0x0920
  - `HEALTH_BODY_COMPOSITION_ANALYZER` = 0x0924
  - `HEALTH_PEAK_FLOW_MOITOR` = 0x0928
  - `HEALTH_MEDICATION_MONITOR` = 0x092C
  - `HEALTH_KNEE_PROSTHESIS` = 0x0930
  - `HEALTH_ANKLE_PROSTHESIS` = 0x0934
  - `HEALTH_GENERIC_HEALTH_MANAGER` = 0x0938
  - `HEALTH_PERSONAL_MOBILITY_DEVICE` = 0x093C
- **PlayingState**
- **ProfileId**
  - `PROFILE_A2DP_SOURCE` = 1
  - `PROFILE_HANDS_FREE_AUDIO_GATEWAY` = 4
#### Functions
- `getState(): BluetoothState`
- `getBtConnectionState(): ProfileConnectionState`
- `pairDevice(deviceId: string): boolean`
- `cancelPairedDevice(deviceId: string): boolean`
- `getRemoteDeviceName(deviceId: string): string`
- `getRemoteDeviceClass(deviceId: string): DeviceClass`
- `enableBluetooth(): boolean`
- `disableBluetooth(): boolean`
- `getLocalName(): string`
- `getPairedDevices(): Array<string>`
- `getProfileConnState(profileId: ProfileId): ProfileConnectionState`
- `setDevicePairingConfirmation(device: string, accept: boolean): boolean`
- `setLocalName(name: string): boolean`
- `setBluetoothScanMode(mode: ScanMode, duration: number): boolean`
- `getBluetoothScanMode(): ScanMode`
- `startBluetoothDiscovery(): boolean`
- `stopBluetoothDiscovery(): boolean`
- `on(type: 'bluetoothDeviceFind', callback: Callback<Array<string>>): void`
- `off(type: 'bluetoothDeviceFind', callback?: Callback<Array<string>>): void`
- `on(type: 'bondStateChange', callback: Callback<BondStateParam>): void`
- `off(type: 'bondStateChange', callback?: Callback<BondStateParam>): void`
- `on(type: 'pinRequired', callback: Callback<PinRequiredParam>): void`
- `off(type: 'pinRequired', callback?: Callback<PinRequiredParam>): void`
- `on(type: 'stateChange', callback: Callback<BluetoothState>): void`
- `off(type: 'stateChange', callback?: Callback<BluetoothState>): void`
- `sppListen(name: string, option: SppOption, callback: AsyncCallback<number>): void`
- `sppAccept(serverSocket: number, callback: AsyncCallback<number>): void`
- `sppConnect(device: string, option: SppOption, callback: AsyncCallback<number>): void`
- `sppCloseServerSocket(socket: number): void`
- `sppCloseClientSocket(socket: number): void`
- `sppWrite(clientSocket: number, data: ArrayBuffer): boolean`
- `on(type: 'sppRead', clientSocket: number, callback: Callback<ArrayBuffer>): void`
- `off(type: 'sppRead', clientSocket: number, callback?: Callback<ArrayBuffer>): void`
- `getProfile(profileId: ProfileId): A2dpSourceProfile | HandsFreeAudioGatewayProfile`
- `createGattServer(): GattServer`
- `createGattClientDevice(deviceId: string): GattClientDevice`
- `getConnectedBLEDevices(): Array<string>`
- `startBLEScan(filters: Array<ScanFilter>, options?: ScanOptions): void`
- `stopBLEScan(): void`
- `on(type: 'BLEDeviceFind', callback: Callback<Array<ScanResult>>): void`
- `off(type: 'BLEDeviceFind', callback?: Callback<Array<ScanResult>>): void`

### hfp (@ohos.bluetooth.hfp.d.ts)
#### Interfaces
- **HandsFreeAudioGatewayProfile**
  - `connect`: void
  - `disconnect`: void
#### Functions
- `createHfpAgProfile(): HandsFreeAudioGatewayProfile`
#### Type Aliases
- `BaseProfile` = baseProfile.BaseProfile

### hid (@ohos.bluetooth.hid.d.ts)
#### Interfaces
- **HidHostProfile**
  - `connect`: void
  - `disconnect`: void
#### Functions
- `createHidHostProfile(): HidHostProfile`
#### Type Aliases
- `BaseProfile` = baseProfile.BaseProfile

### map (@ohos.bluetooth.map.d.ts)
#### Interfaces
- **MapMseProfile**
  - `disconnect`: void
  - `setMessageAccessAuthorization`: Promise<void>
  - `getMessageAccessAuthorization`: Promise<AccessAuthorization>
#### Functions
- `createMapMseProfile(): MapMseProfile`
#### Type Aliases
- `BaseProfile` = baseProfile.BaseProfile
- `AccessAuthorization` = constant.AccessAuthorization

### pan (@ohos.bluetooth.pan.d.ts)
#### Interfaces
- **PanProfile**
  - `disconnect`: void
  - `setTethering`: void
  - `isTetheringOn`: boolean
#### Functions
- `createPanProfile(): PanProfile`
#### Type Aliases
- `BaseProfile` = baseProfile.BaseProfile

### pbap (@ohos.bluetooth.pbap.d.ts)
#### Interfaces
- **PbapServerProfile**
  - `disconnect`: void
  - `setShareType`: void
  - `setShareType`: Promise<void>
  - `getShareType`: void
  - `getShareType`: Promise<ShareType>
  - `setPhoneBookAccessAuthorization`: void
  - `setPhoneBookAccessAuthorization`: Promise<void>
  - `getPhoneBookAccessAuthorization`: void
  - `getPhoneBookAccessAuthorization`: Promise<AccessAuthorization>
#### Enums
- **ShareType**
  - `SHARE_NAME_AND_PHONE_NUMBER` = 0
  - `SHARE_ALL` = 1
  - `SHARE_NOTHING` = 2
#### Functions
- `createPbapServerProfile(): PbapServerProfile`
#### Type Aliases
- `BaseProfile` = baseProfile.BaseProfile
- `AccessAuthorization` = constant.AccessAuthorization

### socket (@ohos.bluetooth.socket.d.ts)
#### Interfaces
- **SppOptions**
  - `uuid`: string
  - `secure`: boolean
  - `type`: SppType
#### Enums
- **SppType**
#### Functions
- `sppListen(name: string, options: SppOptions, callback: AsyncCallback<number>): void`
- `sppAccept(serverSocket: number, callback: AsyncCallback<number>): void`
- `sppConnect(deviceId: string, options: SppOptions, callback: AsyncCallback<number>): void`
- `sppCloseServerSocket(socket: number): void`
- `sppCloseClientSocket(socket: number): void`
- `sppWrite(clientSocket: number, data: ArrayBuffer): void`
- `on(type: 'sppRead', clientSocket: number, callback: Callback<ArrayBuffer>): void`
- `off(type: 'sppRead', clientSocket: number, callback?: Callback<ArrayBuffer>): void`

### wearDetection (@ohos.bluetooth.wearDetection.d.ts)
#### Functions
- `enableWearDetection(deviceId: string, callback: AsyncCallback<void>): void`
- `enableWearDetection(deviceId: string): Promise<void>`
- `disableWearDetection(deviceId: string, callback: AsyncCallback<void>): void`
- `disableWearDetection(deviceId: string): Promise<void>`
- `isWearDetectionSupported(deviceId: string, callback: AsyncCallback<boolean>): void`
- `isWearDetectionSupported(deviceId: string): Promise<boolean>`
- `isWearDetectionEnabled(deviceId: string, callback: AsyncCallback<boolean>): void`
- `isWearDetectionEnabled(deviceId: string): Promise<boolean>`

### bluetoothManager (@ohos.bluetoothManager.d.ts)
#### Interfaces
- **BaseProfile**
  - `getConnectionDevices`: Array<string>
  - `getDeviceState`: ProfileConnectionState
- **A2dpSourceProfile**
  - `connect`: void
  - `disconnect`: void
  - `on`: void
  - `off`: void
  - `getPlayingState`: PlayingState
- **HandsFreeAudioGatewayProfile**
  - `connect`: void
  - `disconnect`: void
  - `on`: void
  - `off`: void
- **HidHostProfile**
  - `connect`: void
  - `disconnect`: void
  - `on`: void
  - `off`: void
- **PanProfile**
  - `disconnect`: void
  - `on`: void
  - `off`: void
  - `setTethering`: void
  - `isTetheringOn`: boolean
- **GattServer**
  - `startAdvertising`: void
  - `stopAdvertising`: void
  - `addService`: void
  - `removeService`: void
  - `close`: void
  - `notifyCharacteristicChanged`: void
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
  - `writeDescriptorValue`: void
  - `getRssiValue`: void
  - `getRssiValue`: Promise<number>
  - `setBLEMtuSize`: void
  - `setNotifyCharacteristicChanged`: void
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
  - `isPrep`: boolean
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
  - `isPrep`: boolean
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
- **BLEConnectChangedState**
  - `deviceId`: string
  - `state`: ProfileConnectionState
- **ScanResult**
  - `deviceId`: string
  - `rssi`: number
  - `data`: ArrayBuffer
- **AdvertiseSetting**
  - `interval?`: number
  - `txPower?`: number
  - `connectable?`: boolean
- **AdvertiseData**
  - `serviceUuids`: Array<string>
  - `manufactureData`: Array<ManufactureData>
  - `serviceData`: Array<ServiceData>
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
- **SppOption**
  - `uuid`: string
  - `secure`: boolean
  - `type`: SppType
- **PinRequiredParam**
  - `deviceId`: string
  - `pinCode`: string
- **DeviceClass**
  - `majorClass`: MajorClass
  - `majorMinorClass`: MajorMinorClass
  - `classOfDevice`: number
- **BondStateParam**
  - `deviceId`: string
  - `state`: BondState
- **StateChangeParam**
  - `deviceId`: string
  - `state`: ProfileConnectionState
#### Enums
- **ScanDuty**
  - `SCAN_MODE_LOW_POWER` = 0
  - `SCAN_MODE_BALANCED` = 1
  - `SCAN_MODE_LOW_LATENCY` = 2
- **MatchMode**
  - `MATCH_MODE_AGGRESSIVE` = 1
  - `MATCH_MODE_STICKY` = 2
- **ProfileConnectionState**
  - `STATE_DISCONNECTED` = 0
  - `STATE_CONNECTING` = 1
  - `STATE_CONNECTED` = 2
  - `STATE_DISCONNECTING` = 3
- **BluetoothState**
  - `STATE_OFF` = 0
  - `STATE_TURNING_ON` = 1
  - `STATE_ON` = 2
  - `STATE_TURNING_OFF` = 3
  - `STATE_BLE_TURNING_ON` = 4
  - `STATE_BLE_ON` = 5
  - `STATE_BLE_TURNING_OFF` = 6
- **SppType**
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
- **MajorClass**
  - `MAJOR_MISC` = 0x0000
  - `MAJOR_COMPUTER` = 0x0100
  - `MAJOR_PHONE` = 0x0200
  - `MAJOR_NETWORKING` = 0x0300
  - `MAJOR_AUDIO_VIDEO` = 0x0400
  - `MAJOR_PERIPHERAL` = 0x0500
  - `MAJOR_IMAGING` = 0x0600
  - `MAJOR_WEARABLE` = 0x0700
  - `MAJOR_TOY` = 0x0800
  - `MAJOR_HEALTH` = 0x0900
  - `MAJOR_UNCATEGORIZED` = 0x1F00
- **MajorMinorClass**
  - `COMPUTER_UNCATEGORIZED` = 0x0100
  - `COMPUTER_DESKTOP` = 0x0104
  - `COMPUTER_SERVER` = 0x0108
  - `COMPUTER_LAPTOP` = 0x010C
  - `COMPUTER_HANDHELD_PC_PDA` = 0x0110
  - `COMPUTER_PALM_SIZE_PC_PDA` = 0x0114
  - `COMPUTER_WEARABLE` = 0x0118
  - `COMPUTER_TABLET` = 0x011C
  - `PHONE_UNCATEGORIZED` = 0x0200
  - `PHONE_CELLULAR` = 0x0204
  - `PHONE_CORDLESS` = 0x0208
  - `PHONE_SMART` = 0x020C
  - `PHONE_MODEM_OR_GATEWAY` = 0x0210
  - `PHONE_ISDN` = 0x0214
  - `NETWORK_FULLY_AVAILABLE` = 0x0300
  - `NETWORK_1_TO_17_UTILIZED` = 0x0320
  - `NETWORK_17_TO_33_UTILIZED` = 0x0340
  - `NETWORK_33_TO_50_UTILIZED` = 0x0360
  - `NETWORK_60_TO_67_UTILIZED` = 0x0380
  - `NETWORK_67_TO_83_UTILIZED` = 0x03A0
  - `NETWORK_83_TO_99_UTILIZED` = 0x03C0
  - `NETWORK_NO_SERVICE` = 0x03E0
  - `AUDIO_VIDEO_UNCATEGORIZED` = 0x0400
  - `AUDIO_VIDEO_WEARABLE_HEADSET` = 0x0404
  - `AUDIO_VIDEO_HANDSFREE` = 0x0408
  - `AUDIO_VIDEO_MICROPHONE` = 0x0410
  - `AUDIO_VIDEO_LOUDSPEAKER` = 0x0414
  - `AUDIO_VIDEO_HEADPHONES` = 0x0418
  - `AUDIO_VIDEO_PORTABLE_AUDIO` = 0x041C
  - `AUDIO_VIDEO_CAR_AUDIO` = 0x0420
  - `AUDIO_VIDEO_SET_TOP_BOX` = 0x0424
  - `AUDIO_VIDEO_HIFI_AUDIO` = 0x0428
  - `AUDIO_VIDEO_VCR` = 0x042C
  - `AUDIO_VIDEO_VIDEO_CAMERA` = 0x0430
  - `AUDIO_VIDEO_CAMCORDER` = 0x0434
  - `AUDIO_VIDEO_VIDEO_MONITOR` = 0x0438
  - `AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER` = 0x043C
  - `AUDIO_VIDEO_VIDEO_CONFERENCING` = 0x0440
  - `AUDIO_VIDEO_VIDEO_GAMING_TOY` = 0x0448
  - `PERIPHERAL_NON_KEYBOARD_NON_POINTING` = 0x0500
  - `PERIPHERAL_KEYBOARD` = 0x0540
  - `PERIPHERAL_POINTING_DEVICE` = 0x0580
  - `PERIPHERAL_KEYBOARD_POINTING` = 0x05C0
  - `PERIPHERAL_UNCATEGORIZED` = 0x0500
  - `PERIPHERAL_JOYSTICK` = 0x0504
  - `PERIPHERAL_GAMEPAD` = 0x0508
  - `PERIPHERAL_REMOTE_CONTROL` = 0x05C0
  - `PERIPHERAL_SENSING_DEVICE` = 0x0510
  - `PERIPHERAL_DIGITIZER_TABLET` = 0x0514
  - `PERIPHERAL_CARD_READER` = 0x0518
  - `PERIPHERAL_DIGITAL_PEN` = 0x051C
  - `PERIPHERAL_SCANNER_RFID` = 0x0520
  - `PERIPHERAL_GESTURAL_INPUT` = 0x0522
  - `IMAGING_UNCATEGORIZED` = 0x0600
  - `IMAGING_DISPLAY` = 0x0610
  - `IMAGING_CAMERA` = 0x0620
  - `IMAGING_SCANNER` = 0x0640
  - `IMAGING_PRINTER` = 0x0680
  - `WEARABLE_UNCATEGORIZED` = 0x0700
  - `WEARABLE_WRIST_WATCH` = 0x0704
  - `WEARABLE_PAGER` = 0x0708
  - `WEARABLE_JACKET` = 0x070C
  - `WEARABLE_HELMET` = 0x0710
  - `WEARABLE_GLASSES` = 0x0714
  - `TOY_UNCATEGORIZED` = 0x0800
  - `TOY_ROBOT` = 0x0804
  - `TOY_VEHICLE` = 0x0808
  - `TOY_DOLL_ACTION_FIGURE` = 0x080C
  - `TOY_CONTROLLER` = 0x0810
  - `TOY_GAME` = 0x0814
  - `HEALTH_UNCATEGORIZED` = 0x0900
  - `HEALTH_BLOOD_PRESSURE` = 0x0904
  - `HEALTH_THERMOMETER` = 0x0908
  - `HEALTH_WEIGHING` = 0x090C
  - `HEALTH_GLUCOSE` = 0x0910
  - `HEALTH_PULSE_OXIMETER` = 0x0914
  - `HEALTH_PULSE_RATE` = 0x0918
  - `HEALTH_DATA_DISPLAY` = 0x091C
  - `HEALTH_STEP_COUNTER` = 0x0920
  - `HEALTH_BODY_COMPOSITION_ANALYZER` = 0x0924
  - `HEALTH_PEAK_FLOW_MONITOR` = 0x0928
  - `HEALTH_MEDICATION_MONITOR` = 0x092C
  - `HEALTH_KNEE_PROSTHESIS` = 0x0930
  - `HEALTH_ANKLE_PROSTHESIS` = 0x0934
  - `HEALTH_GENERIC_HEALTH_MANAGER` = 0x0938
  - `HEALTH_PERSONAL_MOBILITY_DEVICE` = 0x093C
- **PlayingState**
- **ProfileId**
  - `PROFILE_A2DP_SOURCE` = 1
  - `PROFILE_HANDS_FREE_AUDIO_GATEWAY` = 4
  - `PROFILE_HID_HOST` = 6
  - `PROFILE_PAN_NETWORK` = 7
#### Functions
- `getState(): BluetoothState`
- `getBtConnectionState(): ProfileConnectionState`
- `pairDevice(deviceId: string): void`
- `cancelPairedDevice(deviceId: string): void`
- `getRemoteDeviceName(deviceId: string): string`
- `getRemoteDeviceClass(deviceId: string): DeviceClass`
- `enableBluetooth(): void`
- `disableBluetooth(): void`
- `getLocalName(): string`
- `getPairedDevices(): Array<string>`
- `getProfileConnectionState(profileId: ProfileId): ProfileConnectionState`
- `setDevicePairingConfirmation(device: string, accept: boolean): void`
- `setLocalName(name: string): void`
- `setBluetoothScanMode(mode: ScanMode, duration: number): void`
- `getBluetoothScanMode(): ScanMode`
- `startBluetoothDiscovery(): void`
- `stopBluetoothDiscovery(): void`
- `on(type: 'bluetoothDeviceFind', callback: Callback<Array<string>>): void`
- `off(type: 'bluetoothDeviceFind', callback?: Callback<Array<string>>): void`
- `on(type: 'bondStateChange', callback: Callback<BondStateParam>): void`
- `off(type: 'bondStateChange', callback?: Callback<BondStateParam>): void`
- `on(type: 'pinRequired', callback: Callback<PinRequiredParam>): void`
- `off(type: 'pinRequired', callback?: Callback<PinRequiredParam>): void`
- `on(type: 'stateChange', callback: Callback<BluetoothState>): void`
- `off(type: 'stateChange', callback?: Callback<BluetoothState>): void`
- `sppListen(name: string, option: SppOption, callback: AsyncCallback<number>): void`
- `sppAccept(serverSocket: number, callback: AsyncCallback<number>): void`
- `sppConnect(device: string, option: SppOption, callback: AsyncCallback<number>): void`
- `sppCloseServerSocket(socket: number): void`
- `sppCloseClientSocket(socket: number): void`
- `sppWrite(clientSocket: number, data: ArrayBuffer): void`
- `on(type: 'sppRead', clientSocket: number, callback: Callback<ArrayBuffer>): void`
- `off(type: 'sppRead', clientSocket: number, callback?: Callback<ArrayBuffer>): void`
- `getProfileInstance(
    profileId: ProfileId
  ): A2dpSourceProfile | HandsFreeAudioGatewayProfile | HidHostProfile | PanProfile`
- `createGattServer(): GattServer`
- `createGattClientDevice(deviceId: string): GattClientDevice`
- `getConnectedBLEDevices(): Array<string>`
- `startBLEScan(filters: Array<ScanFilter>, options?: ScanOptions): void`
- `stopBLEScan(): void`
- `on(type: 'BLEDeviceFind', callback: Callback<Array<ScanResult>>): void`
- `off(type: 'BLEDeviceFind', callback?: Callback<Array<ScanResult>>): void`

### brightness (@ohos.brightness.d.ts)
#### Functions
- `setValue(value: number): void`
- `setValue(value: number, continuous: boolean): void`

### buffer (@ohos.buffer.d.ts)
#### Interfaces
- **TypedArray**
#### Functions
- `alloc(size: number, fill?: string | Buffer | number, encoding?: BufferEncoding): Buffer`
- `allocUninitializedFromPool(size: number): Buffer`
- `allocUninitialized(size: number): Buffer`
- `byteLength(
    string: string | Buffer | TypedArray | DataView | ArrayBuffer | SharedArrayBuffer,
    encoding?: BufferEncoding
  ): number`
- `concat(list: Buffer[] | Uint8Array[], totalLength?: number): Buffer`
- `from(array: number[]): Buffer`
- `from(arrayBuffer: ArrayBuffer | SharedArrayBuffer, byteOffset?: number, length?: number): Buffer`
- `from(buffer: Buffer | Uint8Array): Buffer`
- `from(object: Object, offsetOrEncoding: number | string, length: number): Buffer`
- `from(string: String, encoding?: BufferEncoding): Buffer`
- `isBuffer(obj: Object): boolean`
- `isEncoding(encoding: string): boolean`
- `compare(buf1: Buffer | Uint8Array, buf2: Buffer | Uint8Array): -1 | 0 | 1`
- `transcode(source: Buffer | Uint8Array, fromEnc: string, toEnc: string): Buffer`
#### Classes
- **Buffer**
  - `fill()`: Buffer
  - `compare()`: -1 | 0 | 1
  - `copy()`: number
  - `equals()`: boolean
  - `includes()`: boolean
  - `indexOf()`: number
  - `keys()`: IterableIterator<number>
  - `values()`: IterableIterator<number>
  - `entries()`: IterableIterator<[number, number]>
  - `lastIndexOf()`: number
  - `readBigInt64BE()`: bigint
  - `readBigInt64LE()`: bigint
  - `readBigUInt64BE()`: bigint
  - `readBigUInt64LE()`: bigint
  - `readDoubleBE()`: number
  - `readDoubleLE()`: number
  - `readFloatBE()`: number
  - `readFloatLE()`: number
  - `readInt8()`: number
  - `readInt16BE()`: number
  - `readInt16LE()`: number
  - `readInt32BE()`: number
  - `readInt32LE()`: number
  - `readIntBE()`: number
  - `readIntLE()`: number
  - `readUInt8()`: number
  - `readUInt16BE()`: number
  - `readUInt16LE()`: number
  - `readUInt32BE()`: number
  - `readUInt32LE()`: number
  - `readUIntBE()`: number
  - `readUIntLE()`: number
  - `subarray()`: Buffer
  - `swap16()`: Buffer
  - `swap32()`: Buffer
  - `swap64()`: Buffer
  - `toJSON()`: Object
  - `toString()`: string
  - `write()`: number
  - `writeBigInt64BE()`: number
  - `writeBigInt64LE()`: number
  - `writeBigUInt64BE()`: number
  - `writeBigUInt64LE()`: number
  - `writeDoubleBE()`: number
  - `writeDoubleLE()`: number
  - `writeFloatBE()`: number
  - `writeFloatLE()`: number
  - `writeInt8()`: number
  - `writeInt16BE()`: number
  - `writeInt16LE()`: number
  - `writeInt32BE()`: number
  - `writeInt32LE()`: number
  - `writeIntBE()`: number
  - `writeIntLE()`: number
  - `writeUInt8()`: number
  - `writeUInt16BE()`: number
  - `writeUInt16LE()`: number
  - `writeUInt32BE()`: number
  - `writeUInt32LE()`: number
  - `writeUIntBE()`: number
  - `writeUIntLE()`: number
  - `length`: number
  - `buffer`: ArrayBuffer
  - `byteOffset`: number
- **Blob**
  - `arrayBuffer()`: Promise<ArrayBuffer>
  - `slice()`: Blob
  - `text()`: Promise<string>
  - `size`: number
  - `type`: string
#### Type Aliases
- `BufferEncoding` = | 'ascii'
    | 'utf8'
    | 'utf-8'
    | 'utf16le'
    | 'ucs2'
    | 'ucs-2'
    | 'base64'
    | 'base64url'
    | 'latin1'
    | 'binary'
    | '

### appControl (@ohos.bundle.appControl.d.ts)
#### Interfaces
- **DisposedRule**
  - `want`: Want
  - `componentType`: ComponentType
  - `disposedType`: DisposedType
  - `controlType`: ControlType
  - `elementList`: Array<ElementName>
  - `priority`: number
#### Enums
- **ComponentType**
  - `UI_ABILITY` = 1
  - `UI_EXTENSION` = 2
- **DisposedType**
  - `BLOCK_APPLICATION` = 1
  - `BLOCK_ABILITY` = 2
  - `NON_BLOCK` = 3
- **ControlType**
  - `ALLOWED_LIST` = 1
  - `DISALLOWED_LIST` = 2
#### Functions
- `setDisposedStatus(appId: string, disposedWant: Want, callback: AsyncCallback<void>): void`
- `setDisposedStatus(appId: string, disposedWant: Want): Promise<void>`
- `setDisposedStatusSync(appId: string, disposedWant: Want): void`
- `getDisposedStatus(appId: string, callback: AsyncCallback<Want>): void`
- `getDisposedStatus(appId: string): Promise<Want>`
- `getDisposedStatusSync(appId: string): Want`
- `deleteDisposedStatus(appId: string, callback: AsyncCallback<void>): void`
- `deleteDisposedStatus(appId: string): Promise<void>`
- `deleteDisposedStatusSync(appId: string): void`
- `getDisposedRule(appId: string): DisposedRule`
- `setDisposedRule(appId: string, rule: DisposedRule): void`

### bundleManager (@ohos.bundle.bundleManager.d.ts)
#### Enums
- **BundleFlag**
  - `GET_BUNDLE_INFO_DEFAULT` = 0x00000000
  - `GET_BUNDLE_INFO_WITH_APPLICATION` = 0x00000001
  - `GET_BUNDLE_INFO_WITH_HAP_MODULE` = 0x00000002
  - `GET_BUNDLE_INFO_WITH_ABILITY` = 0x00000004
  - `GET_BUNDLE_INFO_WITH_EXTENSION_ABILITY` = 0x00000008
  - `GET_BUNDLE_INFO_WITH_REQUESTED_PERMISSION` = 0x00000010
  - `GET_BUNDLE_INFO_WITH_METADATA` = 0x00000020
  - `GET_BUNDLE_INFO_WITH_DISABLE` = 0x00000040
  - `GET_BUNDLE_INFO_WITH_SIGNATURE_INFO` = 0x00000080
  - `GET_BUNDLE_INFO_WITH_MENU` = 0x00000100
- **ApplicationFlag**
  - `GET_APPLICATION_INFO_DEFAULT` = 0x00000000
  - `GET_APPLICATION_INFO_WITH_PERMISSION` = 0x00000001
  - `GET_APPLICATION_INFO_WITH_METADATA` = 0x00000002
  - `GET_APPLICATION_INFO_WITH_DISABLE` = 0x00000004
- **AbilityFlag**
  - `GET_ABILITY_INFO_DEFAULT` = 0x00000000
  - `GET_ABILITY_INFO_WITH_PERMISSION` = 0x00000001
  - `GET_ABILITY_INFO_WITH_APPLICATION` = 0x00000002
  - `GET_ABILITY_INFO_WITH_METADATA` = 0x00000004
  - `GET_ABILITY_INFO_WITH_DISABLE` = 0x00000008
  - `GET_ABILITY_INFO_ONLY_SYSTEM_APP` = 0x00000010
- **ExtensionAbilityFlag**
  - `GET_EXTENSION_ABILITY_INFO_DEFAULT` = 0x00000000
  - `GET_EXTENSION_ABILITY_INFO_WITH_PERMISSION` = 0x00000001
  - `GET_EXTENSION_ABILITY_INFO_WITH_APPLICATION` = 0x00000002
  - `GET_EXTENSION_ABILITY_INFO_WITH_METADATA` = 0x00000004
- **ExtensionAbilityType**
  - `FORM` = 0
  - `WORK_SCHEDULER` = 1
  - `INPUT_METHOD` = 2
  - `SERVICE` = 3
  - `ACCESSIBILITY` = 4
  - `DATA_SHARE` = 5
  - `FILE_SHARE` = 6
  - `STATIC_SUBSCRIBER` = 7
  - `WALLPAPER` = 8
  - `BACKUP` = 9
  - `WINDOW` = 10
  - `ENTERPRISE_ADMIN` = 11
  - `THUMBNAIL` = 13
  - `PREVIEW` = 14
  - `PRINT` = 15
  - `SHARE` = 16
  - `PUSH` = 17
  - `DRIVER` = 18
  - `ACTION` = 19
  - `ADS_SERVICE` = 20
  - `UNSPECIFIED` = 255
- **PermissionGrantState**
  - `PERMISSION_DENIED` = -1
  - `PERMISSION_GRANTED` = 0
- **SupportWindowMode**
  - `FULL_SCREEN` = 0
  - `SPLIT` = 1
  - `FLOATING` = 2
- **LaunchType**
  - `SINGLETON` = 0
  - `MULTITON` = 1
  - `SPECIFIED` = 2
- **AbilityType**
  - `PAGE` = 1
  - `SERVICE` = 2
  - `DATA` = 3
- **DisplayOrientation**
- **ModuleType**
  - `ENTRY` = 1
  - `FEATURE` = 2
  - `SHARED` = 3
- **BundleType**
  - `APP` = 0
  - `ATOMIC_SERVICE` = 1
- **CompatiblePolicy**
  - `BACKWARD_COMPATIBILITY` = 1
- **ProfileType**
  - `INTENT_PROFILE` = 1
#### Functions
- `getBundleInfoForSelf(bundleFlags: number): Promise<BundleInfo>`
- `getBundleInfoForSelf(bundleFlags: number, callback: AsyncCallback<BundleInfo>): void`
- `getBundleInfoForSelfSync(bundleFlags: number): BundleInfo`
- `getBundleInfo(bundleName: string, bundleFlags: number, callback: AsyncCallback<BundleInfo>): void`
- `getBundleInfo(bundleName: string,
    bundleFlags: number, userId: number, callback: AsyncCallback<BundleInfo>): void`
- `getBundleInfo(bundleName: string, bundleFlags: number, userId?: number): Promise<BundleInfo>`
- `getApplicationInfo(bundleName: string, appFlags: number, callback: AsyncCallback<ApplicationInfo>): void`
- `getApplicationInfo(bundleName: string,
    appFlags: number, userId: number, callback: AsyncCallback<ApplicationInfo>): void`
- `getApplicationInfo(bundleName: string, appFlags: number, userId?: number): Promise<ApplicationInfo>`
- `getAllBundleInfo(bundleFlags: number, callback: AsyncCallback<Array<BundleInfo>>): void`
- `getAllBundleInfo(bundleFlags: number, userId: number, callback: AsyncCallback<Array<BundleInfo>>): void`
- `getAllBundleInfo(bundleFlags: number, userId?: number): Promise<Array<BundleInfo>>`
- `getAllApplicationInfo(appFlags: number, callback: AsyncCallback<Array<ApplicationInfo>>): void`
- `getAllApplicationInfo(appFlags: number,
    userId: number, callback: AsyncCallback<Array<ApplicationInfo>>): void`
- `getAllApplicationInfo(appFlags: number, userId?: number): Promise<Array<ApplicationInfo>>`
- `queryAbilityInfo(want: Want, abilityFlags: number, callback: AsyncCallback<Array<AbilityInfo>>): void`
- `queryAbilityInfo(want: Want,
    abilityFlags: number, userId: number, callback: AsyncCallback<Array<AbilityInfo>>): void`
- `queryAbilityInfo(want: Want, abilityFlags: number, userId?: number): Promise<Array<AbilityInfo>>`
- `queryAbilityInfoSync(want: Want, abilityFlags: number, userId?: number): Array<AbilityInfo>`
- `queryExtensionAbilityInfo(want: Want, extensionAbilityType: ExtensionAbilityType,
    extensionAbilityFlags: number, callback: AsyncCallback<Array<ExtensionAbilityInfo>>): void`
- `queryExtensionAbilityInfo(want: Want, extensionAbilityType: ExtensionAbilityType,
    extensionAbilityFlags: number, userId: number, callback: AsyncCallback<Array<ExtensionAbilityInfo>>): void`
- `queryExtensionAbilityInfo(want: Want, extensionAbilityType: ExtensionAbilityType,
    extensionAbilityFlags: number, userId?: number): Promise<Array<ExtensionAbilityInfo>>`
- `queryExtensionAbilityInfoSync(want: Want, extensionAbilityType: ExtensionAbilityType,
    extensionAbilityFlags: number, userId?: number): Array<ExtensionAbilityInfo>`
- `queryExtensionAbilityInfoSync(want: Want, extensionAbilityType: string,
    extensionAbilityFlags: number, userId?: number): Array<ExtensionAbilityInfo>`
- `queryExtensionAbilityInfoSync(extensionAbilityType: string, extensionAbilityFlags: number,
    userId?: number): Array<ExtensionAbilityInfo>`
- `getBundleNameByUid(uid: number, callback: AsyncCallback<string>): void`
- `getBundleNameByUid(uid: number): Promise<string>`
- `getBundleNameByUidSync(uid: number): string`
- `getBundleArchiveInfo(hapFilePath: string, bundleFlags: number, callback: AsyncCallback<BundleInfo>): void`
- `getBundleArchiveInfo(hapFilePath: string, bundleFlags: number): Promise<BundleInfo>`
- `getBundleArchiveInfoSync(hapFilePath: string, bundleFlags: number): BundleInfo`
- `cleanBundleCacheFiles(bundleName: string, callback: AsyncCallback<void>): void`
- `cleanBundleCacheFiles(bundleName: string): Promise<void>`
- `setApplicationEnabled(bundleName: string, isEnabled: boolean, callback: AsyncCallback<void>): void`
- `setApplicationEnabled(bundleName: string, isEnabled: boolean): Promise<void>`
- `setApplicationEnabledSync(bundleName: string, isEnabled: boolean): void`
- `setAbilityEnabled(info: AbilityInfo, isEnabled: boolean, callback: AsyncCallback<void>): void`
- `setAbilityEnabled(info: AbilityInfo, isEnabled: boolean): Promise<void>`
- `setAbilityEnabledSync(info: AbilityInfo, isEnabled: boolean): void`
- `isApplicationEnabled(bundleName: string, callback: AsyncCallback<boolean>): void`
- `isApplicationEnabled(bundleName: string): Promise<boolean>`
- `isApplicationEnabledSync(bundleName: string): boolean`
- `isAbilityEnabled(info: AbilityInfo, callback: AsyncCallback<boolean>): void`
- `isAbilityEnabled(info: AbilityInfo): Promise<boolean>`
- `isAbilityEnabledSync(info: AbilityInfo): boolean`
- `getLaunchWantForBundle(bundleName: string, userId: number, callback: AsyncCallback<Want>): void`
- `getLaunchWantForBundle(bundleName: string, callback: AsyncCallback<Want>): void`
- `getLaunchWantForBundle(bundleName: string, userId?: number): Promise<Want>`
- `getLaunchWantForBundleSync(bundleName: string, userId?: number): Want`
- `getProfileByAbility(moduleName: string, abilityName: string, metadataName: string, callback: AsyncCallback<Array<string>>): void`
- `getProfileByAbility(moduleName: string, abilityName: string, metadataName?: string): Promise<Array<string>>`
- `getProfileByAbilitySync(moduleName: string, abilityName: string, metadataName?: string): Array<string>`
- `getProfileByExtensionAbility(moduleName: string, extensionAbilityName: string, metadataName: string, callback: AsyncCallback<Array<string>>): void`
- `getProfileByExtensionAbility(moduleName: string, extensionAbilityName: string, metadataName?: string): Promise<Array<string>>`
- `getProfileByExtensionAbilitySync(moduleName: string, extensionAbilityName: string, metadataName?: string): Array<string>`
- `getPermissionDef(permissionName: string, callback: AsyncCallback<PermissionDef>): void`
- `getPermissionDef(permissionName: string): Promise<PermissionDef>`
- `getPermissionDefSync(permissionName: string): PermissionDef`
- `getAbilityLabel(bundleName: string, moduleName: string, abilityName: string, callback: AsyncCallback<string>): void`
- `getAbilityLabel(bundleName: string, moduleName: string, abilityName: string): Promise<string>`
- `getAbilityLabelSync(bundleName: string, moduleName: string, abilityName: string): string`
- `getApplicationInfoSync(bundleName: string, applicationFlags: number, userId: number): ApplicationInfo`
- `getApplicationInfoSync(bundleName: string, applicationFlags: number): ApplicationInfo`
- `getBundleInfoSync(bundleName: string, bundleFlags: number, userId: number): BundleInfo`
- `getBundleInfoSync(bundleName: string, bundleFlags: number): BundleInfo`
- `getAllSharedBundleInfo(callback: AsyncCallback<Array<SharedBundleInfo>>): void`
- `getAllSharedBundleInfo(): Promise<Array<SharedBundleInfo>>`
- `getSharedBundleInfo(bundleName: string, moduleName: string, callback: AsyncCallback<Array<SharedBundleInfo>>): void`
- `getSharedBundleInfo(bundleName: string, moduleName: string): Promise<Array<SharedBundleInfo>>`
- `getAppProvisionInfo(bundleName: string, callback: AsyncCallback<AppProvisionInfo>): void`
- `getAppProvisionInfo(bundleName: string, userId: number, callback: AsyncCallback<AppProvisionInfo>): void`
- `getAppProvisionInfo(bundleName: string, userId?: number): Promise<AppProvisionInfo>`
- `getAppProvisionInfoSync(bundleName: string, userId?: number): AppProvisionInfo`
- `getSpecifiedDistributionType(bundleName: string): string`
- `getAdditionalInfo(bundleName: string): string`
- `getJsonProfile(profileType: ProfileType, bundleName: string, moduleName?: string): string`
- `verifyAbc(abcPaths: Array<string>, deleteOriginalFiles: boolean, callback: AsyncCallback<void>): void`
- `verifyAbc(abcPaths: Array<string>, deleteOriginalFiles: boolean): Promise<void>`
- `getRecoverableApplicationInfo(callback: AsyncCallback<Array<RecoverableApplicationInfo>>): void`
- `getRecoverableApplicationInfo(): Promise<Array<RecoverableApplicationInfo>>`
- `setAdditionalInfo(bundleName: string, additionalInfo: string): void`
- `deleteAbc(abcPath: string): Promise<void>`
#### Type Aliases
- `ApplicationInfo` = _ApplicationInfo
- `ModuleMetadata` = _ModuleMetadata
- `Metadata` = _Metadata
- `BundleInfo` = _BundleInfo.BundleInfo
- `UsedScene` = _BundleInfo.UsedScene
- `ReqPermissionDetail` = _BundleInfo.ReqPermissionDetail
- `SignatureInfo` = _BundleInfo.SignatureInfo
- `HapModuleInfo` = _HapModuleInfo.HapModuleInfo
- `PreloadItem` = _HapModuleInfo.PreloadItem
- `Dependency` = _HapModuleInfo.Dependency
- `AbilityInfo` = _AbilityInfo.AbilityInfo
- `WindowSize` = _AbilityInfo.WindowSize
- `ExtensionAbilityInfo` = _ExtensionAbilityInfo.ExtensionAbilityInfo
- `PermissionDef` = _PermissionDef
- `ElementName` = _ElementName
- `SharedBundleInfo` = _SharedBundleInfo
- `AppProvisionInfo` = _AppProvisionInfo.AppProvisionInfo
- `Validity` = _AppProvisionInfo.Validity
- `RecoverableApplicationInfo` = _RecoverableApplicationInfo

### bundleMonitor (@ohos.bundle.bundleMonitor.d.ts)
#### Interfaces
- **BundleChangedInfo**
  - `readonly bundleName`: string
  - `readonly userId`: number
#### Functions
- `on(type: BundleChangedEvent, callback: Callback<BundleChangedInfo>): void`
- `off(type: BundleChangedEvent, callback?: Callback<BundleChangedInfo>): void`
#### Type Aliases
- `BundleChangedEvent` = 'add' | 'update' | 'remove'

### bundleResourceManager (@ohos.bundle.bundleResourceManager.d.ts)
#### Enums
- **ResourceFlag**
  - `GET_RESOURCE_INFO_ALL` = 0x00000001
  - `GET_RESOURCE_INFO_WITH_LABEL` = 0x00000002
  - `GET_RESOURCE_INFO_WITH_ICON` = 0x00000004
  - `GET_RESOURCE_INFO_WITH_SORTED_BY_LABEL` = 0x00000008
#### Functions
- `getBundleResourceInfo(bundleName: string, resourceFlags?: number): BundleResourceInfo`
- `getLauncherAbilityResourceInfo(bundleName: string, resourceFlags?: number): Array<LauncherAbilityResourceInfo>`
- `getAllBundleResourceInfo(resourceFlags: number, callback: AsyncCallback<Array<BundleResourceInfo>>): void`
- `getAllBundleResourceInfo(resourceFlags: number): Promise<Array<BundleResourceInfo>>`
- `getAllLauncherAbilityResourceInfo(resourceFlags: number, callback: AsyncCallback<Array<LauncherAbilityResourceInfo>>): void`
- `getAllLauncherAbilityResourceInfo(resourceFlags: number): Promise<Array<LauncherAbilityResourceInfo>>`
#### Type Aliases
- `BundleResourceInfo` = _BundleResourceInfo
- `LauncherAbilityResourceInfo` = _LauncherAbilityResourceInfo

### bundle (@ohos.bundle.d.ts)
#### Interfaces
- **BundleOptions**
  - `userId?`: number
#### Enums
- **BundleFlag**
  - `GET_BUNDLE_DEFAULT` = 0x00000000
  - `GET_BUNDLE_WITH_ABILITIES` = 0x00000001
  - `GET_ABILITY_INFO_WITH_PERMISSION` = 0x00000002
  - `GET_ABILITY_INFO_WITH_APPLICATION` = 0x00000004
  - `GET_APPLICATION_INFO_WITH_PERMISSION` = 0x00000008
  - `GET_BUNDLE_WITH_REQUESTED_PERMISSION` = 0x00000010
  - `GET_ALL_APPLICATION_INFO` = 0xFFFF0000
  - `GET_ABILITY_INFO_WITH_METADATA` = 0x00000020
  - `GET_APPLICATION_INFO_WITH_METADATA` = 0x00000040
  - `GET_ABILITY_INFO_SYSTEMAPP_ONLY` = 0x00000080
  - `GET_ABILITY_INFO_WITH_DISABLE` = 0x00000100
  - `GET_APPLICATION_INFO_WITH_DISABLE` = 0x00000200
- **ColorMode**
  - `AUTO_MODE` = -1
  - `DARK_MODE` = 0
  - `LIGHT_MODE` = 1
- **GrantStatus**
  - `PERMISSION_DENIED` = -1
  - `PERMISSION_GRANTED` = 0
- **AbilityType**
- **AbilitySubType**
  - `UNSPECIFIED` = 0
  - `CA` = 1
- **DisplayOrientation**
- **LaunchMode**
  - `SINGLETON` = 0
  - `STANDARD` = 1
- **InstallErrorCode**
  - `SUCCESS` = 0
  - `STATUS_INSTALL_FAILURE` = 1
  - `STATUS_INSTALL_FAILURE_ABORTED` = 2
  - `STATUS_INSTALL_FAILURE_INVALID` = 3
  - `STATUS_INSTALL_FAILURE_CONFLICT` = 4
  - `STATUS_INSTALL_FAILURE_STORAGE` = 5
  - `STATUS_INSTALL_FAILURE_INCOMPATIBLE` = 6
  - `STATUS_UNINSTALL_FAILURE` = 7
  - `STATUS_UNINSTALL_FAILURE_BLOCKED` = 8
  - `STATUS_UNINSTALL_FAILURE_ABORTED` = 9
  - `STATUS_UNINSTALL_FAILURE_CONFLICT` = 10
  - `STATUS_INSTALL_FAILURE_DOWNLOAD_TIMEOUT` = 0x0B
  - `STATUS_INSTALL_FAILURE_DOWNLOAD_FAILED` = 0x0C
  - `STATUS_RECOVER_FAILURE_INVALID` = 0x0D
  - `STATUS_ABILITY_NOT_FOUND` = 0x40
  - `STATUS_BMS_SERVICE_ERROR` = 0x41
  - `STATUS_FAILED_NO_SPACE_LEFT` = 0x42
  - `STATUS_GRANT_REQUEST_PERMISSIONS_FAILED` = 0x43
  - `STATUS_INSTALL_PERMISSION_DENIED` = 0x44
  - `STATUS_UNINSTALL_PERMISSION_DENIED` = 0x45
#### Functions
- `getBundleInfo(bundleName: string,
    bundleFlags: number, options: BundleOptions, callback: AsyncCallback<BundleInfo>): void`
- `getBundleInfo(bundleName: string, bundleFlags: number, callback: AsyncCallback<BundleInfo>): void`
- `getBundleInfo(bundleName: string, bundleFlags: number, options?: BundleOptions): Promise<BundleInfo>`
- `getBundleInstaller(callback: AsyncCallback<BundleInstaller>): void`
- `getBundleInstaller(): Promise<BundleInstaller>`
- `getAbilityInfo(bundleName: string, abilityName: string, callback: AsyncCallback<AbilityInfo>): void`
- `getAbilityInfo(bundleName: string, abilityName: string): Promise<AbilityInfo>`
- `getApplicationInfo(bundleName: string,
    bundleFlags: number, userId: number, callback: AsyncCallback<ApplicationInfo>): void`
- `getApplicationInfo(bundleName: string, bundleFlags: number, callback: AsyncCallback<ApplicationInfo>): void`
- `getApplicationInfo(bundleName: string, bundleFlags: number, userId?: number): Promise<ApplicationInfo>`
- `queryAbilityByWant(want: Want,
    bundleFlags: number, userId: number, callback: AsyncCallback<Array<AbilityInfo>>): void`
- `queryAbilityByWant(want: Want, bundleFlags: number, callback: AsyncCallback<Array<AbilityInfo>>): void`
- `queryAbilityByWant(want: Want, bundleFlags: number, userId?: number): Promise<Array<AbilityInfo>>`
- `getAllBundleInfo(bundleFlag: BundleFlag, userId: number, callback: AsyncCallback<Array<BundleInfo>>): void`
- `getAllBundleInfo(bundleFlag: BundleFlag, callback: AsyncCallback<Array<BundleInfo>>): void`
- `getAllBundleInfo(bundleFlag: BundleFlag, userId?: number): Promise<Array<BundleInfo>>`
- `getAllApplicationInfo(bundleFlags: number,
    userId: number, callback: AsyncCallback<Array<ApplicationInfo>>): void`
- `getAllApplicationInfo(bundleFlags: number, callback: AsyncCallback<Array<ApplicationInfo>>): void`
- `getAllApplicationInfo(bundleFlags: number, userId?: number): Promise<Array<ApplicationInfo>>`
- `getNameForUid(uid: number, callback: AsyncCallback<string>): void`
- `getNameForUid(uid: number): Promise<string>`
- `getBundleArchiveInfo(hapFilePath: string, bundleFlags: number, callback: AsyncCallback<BundleInfo>): void`
- `getBundleArchiveInfo(hapFilePath: string, bundleFlags: number): Promise<BundleInfo>`
- `getLaunchWantForBundle(bundleName: string, callback: AsyncCallback<Want>): void`
- `getLaunchWantForBundle(bundleName: string): Promise<Want>`
- `cleanBundleCacheFiles(bundleName: string, callback: AsyncCallback<void>): void`
- `cleanBundleCacheFiles(bundleName: string): Promise<void>`
- `setApplicationEnabled(bundleName: string, isEnable: boolean, callback: AsyncCallback<void>): void`
- `setApplicationEnabled(bundleName: string, isEnable: boolean): Promise<void>`
- `setAbilityEnabled(info: AbilityInfo, isEnable: boolean, callback: AsyncCallback<void>): void`
- `setAbilityEnabled(info: AbilityInfo, isEnable: boolean): Promise<void>`
- `getPermissionDef(permissionName: string, callback: AsyncCallback<PermissionDef>): void`
- `getPermissionDef(permissionName: string): Promise<PermissionDef>`
- `getAbilityLabel(bundleName: string, abilityName: string, callback: AsyncCallback<string>): void`
- `getAbilityLabel(bundleName: string, abilityName: string): Promise<string>`
- `getAbilityIcon(bundleName: string, abilityName: string, callback: AsyncCallback<image.PixelMap>): void`
- `getAbilityIcon(bundleName: string, abilityName: string): Promise<image.PixelMap>`
- `isAbilityEnabled(info: AbilityInfo, callback: AsyncCallback<boolean>): void`
- `isAbilityEnabled(info: AbilityInfo): Promise<boolean>`
- `isApplicationEnabled(bundleName: string, callback: AsyncCallback<boolean>): void`
- `isApplicationEnabled(bundleName: string): Promise<boolean>`

### defaultAppManager (@ohos.bundle.defaultAppManager.d.ts)
#### Enums
- **ApplicationType**
  - `BROWSER` = 'Web Browser'
  - `IMAGE` = 'Image Gallery'
  - `AUDIO` = 'Audio Player'
  - `VIDEO` = 'Video Player'
  - `PDF` = 'PDF Viewer'
  - `WORD` = 'Word Viewer'
  - `EXCEL` = 'Excel Viewer'
  - `PPT` = 'PPT Viewer'
#### Functions
- `isDefaultApplication(type: string, callback: AsyncCallback<boolean>): void`
- `isDefaultApplication(type: string): Promise<boolean>`
- `isDefaultApplicationSync(type: string): boolean`
- `getDefaultApplication(type: string, userId: number, callback: AsyncCallback<BundleInfo>): void`
- `getDefaultApplication(type: string, callback: AsyncCallback<BundleInfo>): void`
- `getDefaultApplication(type: string, userId?: number): Promise<BundleInfo>`
- `getDefaultApplicationSync(type: string, userId?: number): BundleInfo`
- `setDefaultApplication(type: string,
    elementName: ElementName, userId: number, callback: AsyncCallback<void>): void`
- `setDefaultApplication(type: string, elementName: ElementName, callback: AsyncCallback<void>): void`
- `setDefaultApplication(type: string, elementName: ElementName, userId?: number): Promise<void>`
- `setDefaultApplicationSync(type: string, elementName: ElementName, userId?: number): void`
- `resetDefaultApplication(type: string, userId: number, callback: AsyncCallback<void>): void`
- `resetDefaultApplication(type: string, callback: AsyncCallback<void>): void`
- `resetDefaultApplication(type: string, userId?: number): Promise<void>`
- `resetDefaultApplicationSync(type: string, userId?: number): void`

### distributedBundleManager (@ohos.bundle.distributedBundleManager.d.ts)
#### Functions
- `getRemoteAbilityInfo(elementName: ElementName, callback: AsyncCallback<RemoteAbilityInfo>): void`
- `getRemoteAbilityInfo(elementName: ElementName): Promise<RemoteAbilityInfo>`
- `getRemoteAbilityInfo(elementNames: Array<ElementName>,
    callback: AsyncCallback<Array<RemoteAbilityInfo>>): void`
- `getRemoteAbilityInfo(elementNames: Array<ElementName>): Promise<Array<RemoteAbilityInfo>>`
- `getRemoteAbilityInfo(elementName: ElementName,
    locale: string, callback: AsyncCallback<RemoteAbilityInfo>): void`
- `getRemoteAbilityInfo(elementName: ElementName, locale: string): Promise<RemoteAbilityInfo>`
- `getRemoteAbilityInfo(elementNames: Array<ElementName>,
    locale: string, callback: AsyncCallback<Array<RemoteAbilityInfo>>): void`
- `getRemoteAbilityInfo(elementNames: Array<ElementName>, locale: string): Promise<Array<RemoteAbilityInfo>>`
#### Type Aliases
- `RemoteAbilityInfo` = _RemoteAbilityInfo

### freeInstall (@ohos.bundle.freeInstall.d.ts)
#### Enums
- **UpgradeFlag**
  - `NOT_UPGRADE` = 0
  - `SINGLE_UPGRADE` = 1
  - `RELATION_UPGRADE` = 2
- **BundlePackFlag**
  - `GET_PACK_INFO_ALL` = 0x00000000
  - `GET_PACKAGES` = 0x00000001
  - `GET_BUNDLE_SUMMARY` = 0x00000002
  - `GET_MODULE_SUMMARY` = 0x00000004
#### Functions
- `setHapModuleUpgradeFlag(bundleName: string,
    moduleName: string, upgradeFlag: UpgradeFlag, callback: AsyncCallback<void>): void`
- `setHapModuleUpgradeFlag(bundleName: string, moduleName: string, upgradeFlag: UpgradeFlag): Promise<void>`
- `isHapModuleRemovable(bundleName: string, moduleName: string, callback: AsyncCallback<boolean>): void`
- `isHapModuleRemovable(bundleName: string, moduleName: string): Promise<boolean>`
- `getBundlePackInfo(bundleName: string,
    bundlePackFlag: BundlePackFlag, callback: AsyncCallback<BundlePackInfo>): void`
- `getBundlePackInfo(bundleName: string, bundlePackFlag: BundlePackFlag): Promise<BundlePackInfo>`
- `getDispatchInfo(callback: AsyncCallback<DispatchInfo>): void`
- `getDispatchInfo(): Promise<DispatchInfo>`
#### Type Aliases
- `DispatchInfo` = _DispatchInfo
- `BundlePackInfo` = _PackInfo.BundlePackInfo
- `PackageConfig` = _PackInfo.PackageConfig
- `PackageSummary` = _PackInfo.PackageSummary
- `BundleConfigInfo` = _PackInfo.BundleConfigInfo
- `ExtensionAbility` = _PackInfo.ExtensionAbility
- `ModuleConfigInfo` = _PackInfo.ModuleConfigInfo
- `ModuleDistroInfo` = _PackInfo.ModuleDistroInfo
- `ModuleAbilityInfo` = _PackInfo.ModuleAbilityInfo
- `AbilityFormInfo` = _PackInfo.AbilityFormInfo
- `Version` = _PackInfo.Version
- `ApiVersion` = _PackInfo.ApiVersion

### innerBundleManager (@ohos.bundle.innerBundleManager.d.ts)
#### Functions
- `getLauncherAbilityInfos(bundleName: string,
    userId: number, callback: AsyncCallback<Array<LauncherAbilityInfo>>): void`
- `getLauncherAbilityInfos(bundleName: string, userId: number): Promise<Array<LauncherAbilityInfo>>`
- `on(type: 'BundleStatusChange',
    bundleStatusCallback: BundleStatusCallback, callback: AsyncCallback<string>): void`
- `on(type: 'BundleStatusChange', bundleStatusCallback: BundleStatusCallback): Promise<string>`
- `off(type: 'BundleStatusChange', callback: AsyncCallback<string>): void`
- `off(type: 'BundleStatusChange'): Promise<string>`
- `getAllLauncherAbilityInfos(userId: number, callback: AsyncCallback<Array<LauncherAbilityInfo>>): void`
- `getAllLauncherAbilityInfos(userId: number): Promise<Array<LauncherAbilityInfo>>`
- `getShortcutInfos(bundleName: string, callback: AsyncCallback<Array<ShortcutInfo>>): void`
- `getShortcutInfos(bundleName: string): Promise<Array<ShortcutInfo>>`
#### Type Aliases
- `BundleStatusCallback` = _BundleStatusCallback

### installer (@ohos.bundle.installer.d.ts)
#### Interfaces
- **BundleInstaller**
  - `install`: void
  - `install`: void
  - `install`: Promise<void>
  - `uninstall`: void
  - `uninstall`: void
  - `uninstall`: Promise<void>
  - `recover`: void
  - `recover`: void
  - `recover`: Promise<void>
  - `uninstall`: void
  - `uninstall`: Promise<void>
  - `updateBundleForSelf`: void
  - `updateBundleForSelf`: void
  - `updateBundleForSelf`: Promise<void>
- **HashParam**
  - `moduleName`: string
  - `hashValue`: string
- **VerifyCodeParam**
  - `moduleName`: string
  - `signatureFilePath`: string
- **PGOParam**
  - `moduleName`: string
  - `pgoFilePath`: string
- **InstallParam**
  - `userId?`: number
  - `installFlag?`: number
  - `isKeepData?`: boolean
  - `hashParams?`: Array<HashParam>
  - `crowdtestDeadline?`: number
  - `sharedBundleDirPaths?`: Array<String>
  - `specifiedDistributionType?`: string
  - `additionalInfo?`: string
  - `verifyCodeParams?`: Array<VerifyCodeParam>
  - `pgoParams?`: Array<PGOParam>
- **UninstallParam**
  - `bundleName`: string
  - `versionCode?`: number
#### Functions
- `getBundleInstaller(callback: AsyncCallback<BundleInstaller>): void`
- `getBundleInstaller(): Promise<BundleInstaller>`
- `getBundleInstallerSync(): BundleInstaller`

### launcherBundleManager (@ohos.bundle.launcherBundleManager.d.ts)
#### Functions
- `getLauncherAbilityInfo(bundleName: string,
    userId: number, callback: AsyncCallback<Array<LauncherAbilityInfo>>): void`
- `getLauncherAbilityInfo(bundleName: string, userId: number): Promise<Array<LauncherAbilityInfo>>`
- `getLauncherAbilityInfoSync(bundleName: string, userId: number): Array<LauncherAbilityInfo>`
- `getAllLauncherAbilityInfo(userId: number, callback: AsyncCallback<Array<LauncherAbilityInfo>>): void`
- `getAllLauncherAbilityInfo(userId: number): Promise<Array<LauncherAbilityInfo>>`
- `getShortcutInfo(bundleName: string, callback: AsyncCallback<Array<ShortcutInfo>>): void`
- `getShortcutInfo(bundleName: string): Promise<Array<ShortcutInfo>>`
- `getShortcutInfoSync(bundleName: string): Array<ShortcutInfo>`
#### Type Aliases
- `LauncherAbilityInfo` = _LauncherAbilityInfo
- `ShortcutInfo` = _ShortcutInfo
- `ShortcutWant` = _ShortcutWant

### overlay (@ohos.bundle.overlay.d.ts)
#### Functions
- `setOverlayEnabled(moduleName: string, isEnabled: boolean, callback: AsyncCallback<void>): void`
- `setOverlayEnabled(moduleName: string, isEnabled: boolean): Promise<void>`
- `setOverlayEnabledByBundleName(bundleName: string,
    moduleName: string, isEnabled: boolean, callback: AsyncCallback<void>): void`
- `setOverlayEnabledByBundleName(bundleName: string, moduleName: string, isEnabled: boolean): Promise<void>`
- `getOverlayModuleInfo(moduleName: string, callback: AsyncCallback<OverlayModuleInfo>): void`
- `getOverlayModuleInfo(moduleName: string): Promise<OverlayModuleInfo>`
- `getTargetOverlayModuleInfos(targetModuleName: string,
    callback: AsyncCallback<Array<OverlayModuleInfo>>): void`
- `getTargetOverlayModuleInfos(targetModuleName: string): Promise<Array<OverlayModuleInfo>>`
- `getOverlayModuleInfoByBundleName(bundleName: string,
    callback: AsyncCallback<Array<OverlayModuleInfo>>): void`
- `getOverlayModuleInfoByBundleName(bundleName: string,
    moduleName: string, callback: AsyncCallback<Array<OverlayModuleInfo>>): void`
- `getOverlayModuleInfoByBundleName(bundleName: string,
    moduleName?: string): Promise<Array<OverlayModuleInfo>>`
- `getTargetOverlayModuleInfosByBundleName(targetBundleName: string,
    callback: AsyncCallback<Array<OverlayModuleInfo>>): void`
- `getTargetOverlayModuleInfosByBundleName(targetBundleName: string,
    moduleName: string, callback: AsyncCallback<Array<OverlayModuleInfo>>): void`
- `getTargetOverlayModuleInfosByBundleName(targetBundleName: string,
    moduleName?: string): Promise<Array<OverlayModuleInfo>>`
#### Type Aliases
- `OverlayModuleInfo` = _OverlayModuleInfo.OverlayModuleInfo

### bundleState (@ohos.bundleState.d.ts)
#### Interfaces
- **BundleStateInfo**
  - `id`: number
  - `abilityInFgTotalTime?`: number
  - `abilityPrevAccessTime?`: number
  - `abilityPrevSeenTime?`: number
  - `abilitySeenTotalTime?`: number
  - `bundleName?`: string
  - `fgAbilityAccessTotalTime?`: number
  - `fgAbilityPrevAccessTime?`: number
  - `infosBeginTime?`: number
  - `infosEndTime?`: number
  - `merge`: void
- **BundleActiveState**
  - `appUsagePriorityGroup?`: number
  - `bundleName?`: string
  - `indexOfLink?`: string
  - `nameOfClass?`: string
  - `stateOccurredTime?`: number
  - `stateType?`: number
- **BundleActiveInfoResponse**
#### Enums
- **IntervalType**
  - `BY_OPTIMIZED` = 0
  - `BY_DAILY` = 1
  - `BY_WEEKLY` = 2
  - `BY_MONTHLY` = 3
  - `BY_ANNUALLY` = 4
#### Functions
- `isIdleState(bundleName: string, callback: AsyncCallback<boolean>): void`
- `isIdleState(bundleName: string): Promise<boolean>`
- `queryAppUsagePriorityGroup(callback: AsyncCallback<number>): void`
- `queryAppUsagePriorityGroup(): Promise<number>`
- `queryBundleStateInfos(begin: number, end: number, callback: AsyncCallback<BundleActiveInfoResponse>): void`
- `queryBundleStateInfos(begin: number, end: number): Promise<BundleActiveInfoResponse>`
- `queryBundleStateInfoByInterval(
    byInterval: IntervalType,
    begin: number,
    end: number,
    callback: AsyncCallback<Array<BundleStateInfo>>
  ): void`
- `queryBundleStateInfoByInterval(
    byInterval: IntervalType,
    begin: number,
    end: number
  ): Promise<Array<BundleStateInfo>>`
- `queryBundleActiveStates(begin: number, end: number, callback: AsyncCallback<Array<BundleActiveState>>): void`
- `queryBundleActiveStates(begin: number, end: number): Promise<Array<BundleActiveState>>`
- `queryCurrentBundleActiveStates(
    begin: number,
    end: number,
    callback: AsyncCallback<Array<BundleActiveState>>
  ): void`
- `queryCurrentBundleActiveStates(begin: number, end: number): Promise<Array<BundleActiveState>>`

### bytrace (@ohos.bytrace.d.ts)
#### Functions
- `startTrace(name: string, taskId: number, expectedTime?: number): void`
- `finishTrace(name: string, taskId: number): void`
- `traceByValue(name: string, count: number): void`

### calendarManager (@ohos.calendarManager.d.ts)
#### Interfaces
- **CalendarManager**
  - `createCalendar`: Promise<Calendar>
  - `createCalendar`: void
  - `deleteCalendar`: Promise<void>
  - `deleteCalendar`: void
  - `getCalendar`: Promise<Calendar>
  - `getCalendar`: void
  - `getCalendar`: void
  - `getAllCalendars`: Promise<Calendar[]>
  - `getAllCalendars`: void
- **Calendar**
  - `addEvent`: Promise<number>
  - `addEvent`: void
  - `addEvents`: Promise<void>
  - `addEvents`: void
  - `deleteEvent`: Promise<void>
  - `deleteEvent`: void
  - `deleteEvents`: Promise<void>
  - `deleteEvents`: void
  - `updateEvent`: Promise<void>
  - `updateEvent`: void
  - `getEvents`: void
  - `getConfig`: CalendarConfig
  - `setConfig`: Promise<void>
  - `setConfig`: void
  - `getAccount`: CalendarAccount
- **CalendarAccount**
  - `readonly name`: string
  - `type`: CalendarType
- **CalendarConfig**
  - `enableReminder?`: boolean
  - `color?`: number | string
- **Event**
  - `id?`: number
  - `type`: EventType
  - `title?`: string
  - `location?`: Location
  - `startTime`: number
  - `endTime`: number
  - `isAllDay?`: boolean
  - `attendee?`: Attendee[]
  - `timeZone?`: string
  - `reminderTime?`: number[]
  - `recurrenceRule?`: RecurrenceRule
  - `description?`: string
  - `service?`: EventService
- **Location**
  - `location?`: string
  - `longitude?`: number
  - `latitude?`: number
- **RecurrenceRule**
  - `recurrenceFrequency`: RecurrenceFrequency
  - `expire?`: number
- **Attendee**
  - `name`: string
  - `email`: string
- **EventService**
  - `type`: ServiceType
  - `uri`: string
  - `description?`: string
#### Enums
- **CalendarType**
  - `LOCAL` = 'local'
  - `EMAIL` = 'email'
  - `BIRTHDAY` = 'birthday'
  - `CALDAV` = 'caldav'
  - `SUBSCRIBED` = 'subscribed'
- **EventType**
  - `NORMAL` = 0
  - `IMPORTANT` = 1
- **RecurrenceFrequency**
  - `YEARLY` = 0
  - `MONTHLY` = 1
  - `WEEKLY` = 2
  - `DAILY` = 3
- **ServiceType**
  - `MEETING` = 'Meeting'
  - `WATCHING` = 'Watching'
  - `REPAYMENT` = 'Repayment'
  - `LIVE` = 'Live'
  - `SHOPPING` = 'Shopping'
  - `TRIP` = 'Trip'
  - `CLASS` = 'Class'
  - `SPORTS_EVENTS` = 'SportsEvents'
  - `SPORTS_EXERCISE` = 'SportsExercise'
#### Functions
- `getCalendarManager(context: Context) : CalendarManager`
#### Classes
- **EventFilter**
  - `filterById()`: EventFilter
  - `filterByTime()`: EventFilter
  - `filterByTitle()`: EventFilter

### charger (@ohos.charger.d.ts)
#### Enums
- **ChargeType**

### commonEvent (@ohos.commonEvent.d.ts)
#### Enums
- **Support**
  - `COMMON_EVENT_BOOT_COMPLETED` = 'usual.event.BOOT_COMPLETED'
  - `COMMON_EVENT_LOCKED_BOOT_COMPLETED` = 'usual.event.LOCKED_BOOT_COMPLETED'
  - `COMMON_EVENT_SHUTDOWN` = 'usual.event.SHUTDOWN'
  - `COMMON_EVENT_BATTERY_CHANGED` = 'usual.event.BATTERY_CHANGED'
  - `COMMON_EVENT_BATTERY_LOW` = 'usual.event.BATTERY_LOW'
  - `COMMON_EVENT_BATTERY_OKAY` = 'usual.event.BATTERY_OKAY'
  - `COMMON_EVENT_POWER_CONNECTED` = 'usual.event.POWER_CONNECTED'
  - `COMMON_EVENT_POWER_DISCONNECTED` = 'usual.event.POWER_DISCONNECTED'
  - `COMMON_EVENT_SCREEN_OFF` = 'usual.event.SCREEN_OFF'
  - `COMMON_EVENT_SCREEN_ON` = 'usual.event.SCREEN_ON'
  - `COMMON_EVENT_THERMAL_LEVEL_CHANGED` = 'usual.event.THERMAL_LEVEL_CHANGED'
  - `COMMON_EVENT_USER_PRESENT` = 'usual.event.USER_PRESENT'
  - `COMMON_EVENT_TIME_TICK` = 'usual.event.TIME_TICK'
  - `COMMON_EVENT_TIME_CHANGED` = 'usual.event.TIME_CHANGED'
  - `COMMON_EVENT_DATE_CHANGED` = 'usual.event.DATE_CHANGED'
  - `COMMON_EVENT_TIMEZONE_CHANGED` = 'usual.event.TIMEZONE_CHANGED'
  - `COMMON_EVENT_CLOSE_SYSTEM_DIALOGS` = 'usual.event.CLOSE_SYSTEM_DIALOGS'
  - `COMMON_EVENT_PACKAGE_ADDED` = 'usual.event.PACKAGE_ADDED'
  - `COMMON_EVENT_PACKAGE_REPLACED` = 'usual.event.PACKAGE_REPLACED'
  - `COMMON_EVENT_MY_PACKAGE_REPLACED` = 'usual.event.MY_PACKAGE_REPLACED'
  - `COMMON_EVENT_PACKAGE_REMOVED` = 'usual.event.PACKAGE_REMOVED'
  - `COMMON_EVENT_BUNDLE_REMOVED` = 'usual.event.BUNDLE_REMOVED'
  - `COMMON_EVENT_PACKAGE_FULLY_REMOVED` = 'usual.event.PACKAGE_FULLY_REMOVED'
  - `COMMON_EVENT_PACKAGE_CHANGED` = 'usual.event.PACKAGE_CHANGED'
  - `COMMON_EVENT_PACKAGE_RESTARTED` = 'usual.event.PACKAGE_RESTARTED'
  - `COMMON_EVENT_PACKAGE_DATA_CLEARED` = 'usual.event.PACKAGE_DATA_CLEARED'
  - `COMMON_EVENT_PACKAGES_SUSPENDED` = 'usual.event.PACKAGES_SUSPENDED'
  - `COMMON_EVENT_PACKAGES_UNSUSPENDED` = 'usual.event.PACKAGES_UNSUSPENDED'
  - `COMMON_EVENT_MY_PACKAGE_SUSPENDED` = 'usual.event.MY_PACKAGE_SUSPENDED'
  - `COMMON_EVENT_MY_PACKAGE_UNSUSPENDED` = 'usual.event.MY_PACKAGE_UNSUSPENDED'
  - `COMMON_EVENT_UID_REMOVED` = 'usual.event.UID_REMOVED'
  - `COMMON_EVENT_PACKAGE_FIRST_LAUNCH` = 'usual.event.PACKAGE_FIRST_LAUNCH'
  - `COMMON_EVENT_PACKAGE_NEEDS_VERIFICATION` = 'usual.event.PACKAGE_NEEDS_VERIFICATION'
  - `COMMON_EVENT_PACKAGE_VERIFIED` = 'usual.event.PACKAGE_VERIFIED'
  - `COMMON_EVENT_EXTERNAL_APPLICATIONS_AVAILABLE` = 'usual.event.EXTERNAL_APPLICATIONS_AVAILABLE'
  - `COMMON_EVENT_EXTERNAL_APPLICATIONS_UNAVAILABLE` = 'usual.event.EXTERNAL_APPLICATIONS_UNAVAILABLE'
  - `COMMON_EVENT_CONFIGURATION_CHANGED` = 'usual.event.CONFIGURATION_CHANGED'
  - `COMMON_EVENT_LOCALE_CHANGED` = 'usual.event.LOCALE_CHANGED'
  - `COMMON_EVENT_MANAGE_PACKAGE_STORAGE` = 'usual.event.MANAGE_PACKAGE_STORAGE'
  - `COMMON_EVENT_DRIVE_MODE` = 'common.event.DRIVE_MODE'
  - `COMMON_EVENT_HOME_MODE` = 'common.event.HOME_MODE'
  - `COMMON_EVENT_OFFICE_MODE` = 'common.event.OFFICE_MODE'
  - `COMMON_EVENT_USER_STARTED` = 'usual.event.USER_STARTED'
  - `COMMON_EVENT_USER_BACKGROUND` = 'usual.event.USER_BACKGROUND'
  - `COMMON_EVENT_USER_FOREGROUND` = 'usual.event.USER_FOREGROUND'
  - `COMMON_EVENT_USER_SWITCHED` = 'usual.event.USER_SWITCHED'
  - `COMMON_EVENT_USER_STARTING` = 'usual.event.USER_STARTING'
  - `COMMON_EVENT_USER_UNLOCKED` = 'usual.event.USER_UNLOCKED'
  - `COMMON_EVENT_USER_STOPPING` = 'usual.event.USER_STOPPING'
  - `COMMON_EVENT_USER_STOPPED` = 'usual.event.USER_STOPPED'
  - `COMMON_EVENT_HWID_LOGIN` = 'common.event.HWID_LOGIN'
  - `COMMON_EVENT_HWID_LOGOUT` = 'common.event.HWID_LOGOUT'
  - `COMMON_EVENT_HWID_TOKEN_INVALID` = 'common.event.HWID_TOKEN_INVALID'
  - `COMMON_EVENT_HWID_LOGOFF` = 'common.event.HWID_LOGOFF'
  - `COMMON_EVENT_WIFI_POWER_STATE` = 'usual.event.wifi.POWER_STATE'
  - `COMMON_EVENT_WIFI_SCAN_FINISHED` = 'usual.event.wifi.SCAN_FINISHED'
  - `COMMON_EVENT_WIFI_RSSI_VALUE` = 'usual.event.wifi.RSSI_VALUE'
  - `COMMON_EVENT_WIFI_CONN_STATE` = 'usual.event.wifi.CONN_STATE'
  - `COMMON_EVENT_WIFI_HOTSPOT_STATE` = 'usual.event.wifi.HOTSPOT_STATE'
  - `COMMON_EVENT_WIFI_AP_STA_JOIN` = 'usual.event.wifi.WIFI_HS_STA_JOIN'
  - `COMMON_EVENT_WIFI_AP_STA_LEAVE` = 'usual.event.wifi.WIFI_HS_STA_LEAVE'
  - `COMMON_EVENT_WIFI_MPLINK_STATE_CHANGE` = 'usual.event.wifi.mplink.STATE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_CONN_STATE` = 'usual.event.wifi.p2p.CONN_STATE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_STATE_CHANGED` = 'usual.event.wifi.p2p.STATE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_PEERS_STATE_CHANGED` = 'usual.event.wifi.p2p.DEVICES_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_PEERS_DISCOVERY_STATE_CHANGED` = 'usual.event.wifi.p2p.PEER_DISCOVERY_STATE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_CURRENT_DEVICE_STATE_CHANGED` = 'usual.event.wifi.p2p.CURRENT_DEVICE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_GROUP_STATE_CHANGED` = 'usual.event.wifi.p2p.GROUP_STATE_CHANGED'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREE_AG_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.handsfree.ag.CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREE_AG_CURRENT_DEVICE_UPDATE` = 'usual.event.bluetooth.handsfree.ag.CURRENT_DEVICE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREE_AG_AUDIO_STATE_UPDATE` = 'usual.event.bluetooth.handsfree.ag.AUDIO_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsource.CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_CURRENT_DEVICE_UPDATE` = 'usual.event.bluetooth.a2dpsource.CURRENT_DEVICE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_PLAYING_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsource.PLAYING_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_AVRCP_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsource.AVRCP_CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_CODEC_VALUE_UPDATE` = 'usual.event.bluetooth.a2dpsource.CODEC_VALUE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_DISCOVERED` = 'usual.event.bluetooth.remotedevice.DISCOVERED'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_CLASS_VALUE_UPDATE` = 'usual.event.bluetooth.remotedevice.CLASS_VALUE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_ACL_CONNECTED` = 'usual.event.bluetooth.remotedevice.ACL_CONNECTED'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_ACL_DISCONNECTED` = 'usual.event.bluetooth.remotedevice.ACL_DISCONNECTED'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_NAME_UPDATE` = 'usual.event.bluetooth.remotedevice.NAME_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_PAIR_STATE` = 'usual.event.bluetooth.remotedevice.PAIR_STATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_BATTERY_VALUE_UPDATE` = 'usual.event.bluetooth.remotedevice.BATTERY_VALUE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_SDP_RESULT` = 'usual.event.bluetooth.remotedevice.SDP_RESULT'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_UUID_VALUE` = 'usual.event.bluetooth.remotedevice.UUID_VALUE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_PAIRING_REQ` = 'usual.event.bluetooth.remotedevice.PAIRING_REQ'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_PAIRING_CANCEL` = 'usual.event.bluetooth.remotedevice.PAIRING_CANCEL'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_CONNECT_REQ` = 'usual.event.bluetooth.remotedevice.CONNECT_REQ'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_CONNECT_REPLY` = 'usual.event.bluetooth.remotedevice.CONNECT_REPLY'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_CONNECT_CANCEL` = 'usual.event.bluetooth.remotedevice.CONNECT_CANCEL'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREEUNIT_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.handsfreeunit.CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREEUNIT_AUDIO_STATE_UPDATE` = 'usual.event.bluetooth.handsfreeunit.AUDIO_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREEUNIT_AG_COMMON_EVENT` = 'usual.event.bluetooth.handsfreeunit.AG_COMMON_EVENT'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREEUNIT_AG_CALL_STATE_UPDATE` = 'usual.event.bluetooth.handsfreeunit.AG_CALL_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HOST_STATE_UPDATE` = 'usual.event.bluetooth.host.STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HOST_REQ_DISCOVERABLE` = 'usual.event.bluetooth.host.REQ_DISCOVERABLE'
  - `COMMON_EVENT_BLUETOOTH_HOST_REQ_ENABLE` = 'usual.event.bluetooth.host.REQ_ENABLE'
  - `COMMON_EVENT_BLUETOOTH_HOST_REQ_DISABLE` = 'usual.event.bluetooth.host.REQ_DISABLE'
  - `COMMON_EVENT_BLUETOOTH_HOST_SCAN_MODE_UPDATE` = 'usual.event.bluetooth.host.SCAN_MODE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HOST_DISCOVERY_STARTED` = 'usual.event.bluetooth.host.DISCOVERY_STARTED'
  - `COMMON_EVENT_BLUETOOTH_HOST_DISCOVERY_FINISHED` = 'usual.event.bluetooth.host.DISCOVERY_FINISHED'
  - `COMMON_EVENT_BLUETOOTH_HOST_NAME_UPDATE` = 'usual.event.bluetooth.host.NAME_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSINK_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsink.CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSINK_PLAYING_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsink.PLAYING_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSINK_AUDIO_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsink.AUDIO_STATE_UPDATE'
  - `COMMON_EVENT_NFC_ACTION_ADAPTER_STATE_CHANGED` = 'usual.event.nfc.action.ADAPTER_STATE_CHANGED'
  - `COMMON_EVENT_NFC_ACTION_RF_FIELD_ON_DETECTED` = 'usual.event.nfc.action.RF_FIELD_ON_DETECTED'
  - `COMMON_EVENT_NFC_ACTION_RF_FIELD_OFF_DETECTED` = 'usual.event.nfc.action.RF_FIELD_OFF_DETECTED'
  - `COMMON_EVENT_DISCHARGING` = 'usual.event.DISCHARGING'
  - `COMMON_EVENT_CHARGING` = 'usual.event.CHARGING'
  - `COMMON_EVENT_DEVICE_IDLE_MODE_CHANGED` = 'usual.event.DEVICE_IDLE_MODE_CHANGED'
  - `COMMON_EVENT_POWER_SAVE_MODE_CHANGED` = 'usual.event.POWER_SAVE_MODE_CHANGED'
  - `COMMON_EVENT_USER_ADDED` = 'usual.event.USER_ADDED'
  - `COMMON_EVENT_USER_REMOVED` = 'usual.event.USER_REMOVED'
  - `COMMON_EVENT_ABILITY_ADDED` = 'common.event.ABILITY_ADDED'
  - `COMMON_EVENT_ABILITY_REMOVED` = 'common.event.ABILITY_REMOVED'
  - `COMMON_EVENT_ABILITY_UPDATED` = 'common.event.ABILITY_UPDATED'
  - `COMMON_EVENT_LOCATION_MODE_STATE_CHANGED` = 'usual.event.location.MODE_STATE_CHANGED'
  - `COMMON_EVENT_IVI_SLEEP` = 'common.event.IVI_SLEEP'
  - `COMMON_EVENT_IVI_PAUSE` = 'common.event.IVI_PAUSE'
  - `COMMON_EVENT_IVI_STANDBY` = 'common.event.IVI_STANDBY'
  - `COMMON_EVENT_IVI_LASTMODE_SAVE` = 'common.event.IVI_LASTMODE_SAVE'
  - `COMMON_EVENT_IVI_VOLTAGE_ABNORMAL` = 'common.event.IVI_VOLTAGE_ABNORMAL'
  - `COMMON_EVENT_IVI_HIGH_TEMPERATURE` = 'common.event.IVI_HIGH_TEMPERATURE'
  - `COMMON_EVENT_IVI_EXTREME_TEMPERATURE` = 'common.event.IVI_EXTREME_TEMPERATURE'
  - `COMMON_EVENT_IVI_TEMPERATURE_ABNORMAL` = 'common.event.IVI_TEMPERATURE_ABNORMAL'
  - `COMMON_EVENT_IVI_VOLTAGE_RECOVERY` = 'common.event.IVI_VOLTAGE_RECOVERY'
  - `COMMON_EVENT_IVI_TEMPERATURE_RECOVERY` = 'common.event.IVI_TEMPERATURE_RECOVERY'
  - `COMMON_EVENT_IVI_ACTIVE` = 'common.event.IVI_ACTIVE'
  - `COMMON_EVENT_USB_DEVICE_ATTACHED` = 'usual.event.hardware.usb.action.USB_DEVICE_ATTACHED'
  - `COMMON_EVENT_USB_DEVICE_DETACHED` = 'usual.event.hardware.usb.action.USB_DEVICE_DETACHED'
  - `COMMON_EVENT_USB_ACCESSORY_ATTACHED` = 'usual.event.hardware.usb.action.USB_ACCESSORY_ATTACHED'
  - `COMMON_EVENT_USB_ACCESSORY_DETACHED` = 'usual.event.hardware.usb.action.USB_ACCESSORY_DETACHED'
  - `COMMON_EVENT_DISK_REMOVED` = 'usual.event.data.DISK_REMOVED'
  - `COMMON_EVENT_DISK_UNMOUNTED` = 'usual.event.data.DISK_UNMOUNTED'
  - `COMMON_EVENT_DISK_MOUNTED` = 'usual.event.data.DISK_MOUNTED'
  - `COMMON_EVENT_DISK_BAD_REMOVAL` = 'usual.event.data.DISK_BAD_REMOVAL'
  - `COMMON_EVENT_DISK_UNMOUNTABLE` = 'usual.event.data.DISK_UNMOUNTABLE'
  - `COMMON_EVENT_DISK_EJECT` = 'usual.event.data.DISK_EJECT'
  - `COMMON_EVENT_VISIBLE_ACCOUNTS_UPDATED` = 'usual.event.data.VISIBLE_ACCOUNTS_UPDATED'
  - `COMMON_EVENT_ACCOUNT_DELETED` = 'usual.event.data.ACCOUNT_DELETED'
  - `COMMON_EVENT_FOUNDATION_READY` = 'common.event.FOUNDATION_READY'
  - `COMMON_EVENT_AIRPLANE_MODE_CHANGED` = 'usual.event.AIRPLANE_MODE'
  - `COMMON_EVENT_SPLIT_SCREEN` = 'common.event.SPLIT_SCREEN'
#### Functions
- `publish(event: string, callback: AsyncCallback<void>): void`
- `publish(event: string, options: CommonEventPublishData, callback: AsyncCallback<void>): void`
- `publishAsUser(event: string, userId: number, callback: AsyncCallback<void>): void`
- `publishAsUser(
    event: string,
    userId: number,
    options: CommonEventPublishData,
    callback: AsyncCallback<void>
  ): void`
- `createSubscriber(
    subscribeInfo: CommonEventSubscribeInfo,
    callback: AsyncCallback<CommonEventSubscriber>
  ): void`
- `createSubscriber(subscribeInfo: CommonEventSubscribeInfo): Promise<CommonEventSubscriber>`
- `subscribe(subscriber: CommonEventSubscriber, callback: AsyncCallback<CommonEventData>): void`
- `unsubscribe(subscriber: CommonEventSubscriber, callback?: AsyncCallback<void>): void`

### commonEventManager (@ohos.commonEventManager.d.ts)
#### Enums
- **Support**
  - `COMMON_EVENT_BOOT_COMPLETED` = 'usual.event.BOOT_COMPLETED'
  - `COMMON_EVENT_LOCKED_BOOT_COMPLETED` = 'usual.event.LOCKED_BOOT_COMPLETED'
  - `COMMON_EVENT_SHUTDOWN` = 'usual.event.SHUTDOWN'
  - `COMMON_EVENT_BATTERY_CHANGED` = 'usual.event.BATTERY_CHANGED'
  - `COMMON_EVENT_BATTERY_LOW` = 'usual.event.BATTERY_LOW'
  - `COMMON_EVENT_BATTERY_OKAY` = 'usual.event.BATTERY_OKAY'
  - `COMMON_EVENT_POWER_CONNECTED` = 'usual.event.POWER_CONNECTED'
  - `COMMON_EVENT_POWER_DISCONNECTED` = 'usual.event.POWER_DISCONNECTED'
  - `COMMON_EVENT_SCREEN_OFF` = 'usual.event.SCREEN_OFF'
  - `COMMON_EVENT_SCREEN_ON` = 'usual.event.SCREEN_ON'
  - `COMMON_EVENT_THERMAL_LEVEL_CHANGED` = 'usual.event.THERMAL_LEVEL_CHANGED'
  - `COMMON_EVENT_USER_PRESENT` = 'usual.event.USER_PRESENT'
  - `COMMON_EVENT_TIME_TICK` = 'usual.event.TIME_TICK'
  - `COMMON_EVENT_TIME_CHANGED` = 'usual.event.TIME_CHANGED'
  - `COMMON_EVENT_DATE_CHANGED` = 'usual.event.DATE_CHANGED'
  - `COMMON_EVENT_TIMEZONE_CHANGED` = 'usual.event.TIMEZONE_CHANGED'
  - `COMMON_EVENT_CLOSE_SYSTEM_DIALOGS` = 'usual.event.CLOSE_SYSTEM_DIALOGS'
  - `COMMON_EVENT_PACKAGE_ADDED` = 'usual.event.PACKAGE_ADDED'
  - `COMMON_EVENT_PACKAGE_REPLACED` = 'usual.event.PACKAGE_REPLACED'
  - `COMMON_EVENT_MY_PACKAGE_REPLACED` = 'usual.event.MY_PACKAGE_REPLACED'
  - `COMMON_EVENT_PACKAGE_REMOVED` = 'usual.event.PACKAGE_REMOVED'
  - `COMMON_EVENT_BUNDLE_REMOVED` = 'usual.event.BUNDLE_REMOVED'
  - `COMMON_EVENT_PACKAGE_FULLY_REMOVED` = 'usual.event.PACKAGE_FULLY_REMOVED'
  - `COMMON_EVENT_PACKAGE_CHANGED` = 'usual.event.PACKAGE_CHANGED'
  - `COMMON_EVENT_PACKAGE_RESTARTED` = 'usual.event.PACKAGE_RESTARTED'
  - `COMMON_EVENT_PACKAGE_DATA_CLEARED` = 'usual.event.PACKAGE_DATA_CLEARED'
  - `COMMON_EVENT_PACKAGE_CACHE_CLEARED` = 'usual.event.PACKAGE_CACHE_CLEARED'
  - `COMMON_EVENT_PACKAGES_SUSPENDED` = 'usual.event.PACKAGES_SUSPENDED'
  - `COMMON_EVENT_PACKAGES_UNSUSPENDED` = 'usual.event.PACKAGES_UNSUSPENDED'
  - `COMMON_EVENT_MY_PACKAGE_SUSPENDED` = 'usual.event.MY_PACKAGE_SUSPENDED'
  - `COMMON_EVENT_MY_PACKAGE_UNSUSPENDED` = 'usual.event.MY_PACKAGE_UNSUSPENDED'
  - `COMMON_EVENT_UID_REMOVED` = 'usual.event.UID_REMOVED'
  - `COMMON_EVENT_PACKAGE_FIRST_LAUNCH` = 'usual.event.PACKAGE_FIRST_LAUNCH'
  - `COMMON_EVENT_PACKAGE_NEEDS_VERIFICATION` = 'usual.event.PACKAGE_NEEDS_VERIFICATION'
  - `COMMON_EVENT_PACKAGE_VERIFIED` = 'usual.event.PACKAGE_VERIFIED'
  - `COMMON_EVENT_EXTERNAL_APPLICATIONS_AVAILABLE` = 'usual.event.EXTERNAL_APPLICATIONS_AVAILABLE'
  - `COMMON_EVENT_EXTERNAL_APPLICATIONS_UNAVAILABLE` = 'usual.event.EXTERNAL_APPLICATIONS_UNAVAILABLE'
  - `COMMON_EVENT_CONFIGURATION_CHANGED` = 'usual.event.CONFIGURATION_CHANGED'
  - `COMMON_EVENT_LOCALE_CHANGED` = 'usual.event.LOCALE_CHANGED'
  - `COMMON_EVENT_MANAGE_PACKAGE_STORAGE` = 'usual.event.MANAGE_PACKAGE_STORAGE'
  - `COMMON_EVENT_DRIVE_MODE` = 'common.event.DRIVE_MODE'
  - `COMMON_EVENT_HOME_MODE` = 'common.event.HOME_MODE'
  - `COMMON_EVENT_OFFICE_MODE` = 'common.event.OFFICE_MODE'
  - `COMMON_EVENT_USER_STARTED` = 'usual.event.USER_STARTED'
  - `COMMON_EVENT_USER_BACKGROUND` = 'usual.event.USER_BACKGROUND'
  - `COMMON_EVENT_USER_FOREGROUND` = 'usual.event.USER_FOREGROUND'
  - `COMMON_EVENT_USER_SWITCHED` = 'usual.event.USER_SWITCHED'
  - `COMMON_EVENT_USER_STARTING` = 'usual.event.USER_STARTING'
  - `COMMON_EVENT_USER_UNLOCKED` = 'usual.event.USER_UNLOCKED'
  - `COMMON_EVENT_USER_STOPPING` = 'usual.event.USER_STOPPING'
  - `COMMON_EVENT_USER_STOPPED` = 'usual.event.USER_STOPPED'
  - `COMMON_EVENT_DISTRIBUTED_ACCOUNT_LOGIN` = 'common.event.DISTRIBUTED_ACCOUNT_LOGIN'
  - `COMMON_EVENT_DISTRIBUTED_ACCOUNT_LOGOUT` = 'common.event.DISTRIBUTED_ACCOUNT_LOGOUT'
  - `COMMON_EVENT_DISTRIBUTED_ACCOUNT_TOKEN_INVALID` = 'common.event.DISTRIBUTED_ACCOUNT_TOKEN_INVALID'
  - `COMMON_EVENT_DISTRIBUTED_ACCOUNT_LOGOFF` = 'common.event.DISTRIBUTED_ACCOUNT_LOGOFF'
  - `COMMON_EVENT_WIFI_POWER_STATE` = 'usual.event.wifi.POWER_STATE'
  - `COMMON_EVENT_WIFI_SCAN_FINISHED` = 'usual.event.wifi.SCAN_FINISHED'
  - `COMMON_EVENT_WIFI_RSSI_VALUE` = 'usual.event.wifi.RSSI_VALUE'
  - `COMMON_EVENT_WIFI_CONN_STATE` = 'usual.event.wifi.CONN_STATE'
  - `COMMON_EVENT_WIFI_HOTSPOT_STATE` = 'usual.event.wifi.HOTSPOT_STATE'
  - `COMMON_EVENT_WIFI_AP_STA_JOIN` = 'usual.event.wifi.WIFI_HS_STA_JOIN'
  - `COMMON_EVENT_WIFI_AP_STA_LEAVE` = 'usual.event.wifi.WIFI_HS_STA_LEAVE'
  - `COMMON_EVENT_WIFI_MPLINK_STATE_CHANGE` = 'usual.event.wifi.mplink.STATE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_CONN_STATE` = 'usual.event.wifi.p2p.CONN_STATE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_STATE_CHANGED` = 'usual.event.wifi.p2p.STATE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_PEERS_STATE_CHANGED` = 'usual.event.wifi.p2p.DEVICES_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_PEERS_DISCOVERY_STATE_CHANGED` = 'usual.event.wifi.p2p.PEER_DISCOVERY_STATE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_CURRENT_DEVICE_STATE_CHANGED` = 'usual.event.wifi.p2p.CURRENT_DEVICE_CHANGE'
  - `COMMON_EVENT_WIFI_P2P_GROUP_STATE_CHANGED` = 'usual.event.wifi.p2p.GROUP_STATE_CHANGED'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREE_AG_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.handsfree.ag.CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREE_AG_CURRENT_DEVICE_UPDATE` = 'usual.event.bluetooth.handsfree.ag.CURRENT_DEVICE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREE_AG_AUDIO_STATE_UPDATE` = 'usual.event.bluetooth.handsfree.ag.AUDIO_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsource.CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_CURRENT_DEVICE_UPDATE` = 'usual.event.bluetooth.a2dpsource.CURRENT_DEVICE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_PLAYING_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsource.PLAYING_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_AVRCP_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsource.AVRCP_CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSOURCE_CODEC_VALUE_UPDATE` = 'usual.event.bluetooth.a2dpsource.CODEC_VALUE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_DISCOVERED` = 'usual.event.bluetooth.remotedevice.DISCOVERED'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_CLASS_VALUE_UPDATE` = 'usual.event.bluetooth.remotedevice.CLASS_VALUE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_ACL_CONNECTED` = 'usual.event.bluetooth.remotedevice.ACL_CONNECTED'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_ACL_DISCONNECTED` = 'usual.event.bluetooth.remotedevice.ACL_DISCONNECTED'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_NAME_UPDATE` = 'usual.event.bluetooth.remotedevice.NAME_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_PAIR_STATE` = 'usual.event.bluetooth.remotedevice.PAIR_STATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_BATTERY_VALUE_UPDATE` = 'usual.event.bluetooth.remotedevice.BATTERY_VALUE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_SDP_RESULT` = 'usual.event.bluetooth.remotedevice.SDP_RESULT'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_UUID_VALUE` = 'usual.event.bluetooth.remotedevice.UUID_VALUE'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_PAIRING_REQ` = 'usual.event.bluetooth.remotedevice.PAIRING_REQ'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_PAIRING_CANCEL` = 'usual.event.bluetooth.remotedevice.PAIRING_CANCEL'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_CONNECT_REQ` = 'usual.event.bluetooth.remotedevice.CONNECT_REQ'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_CONNECT_REPLY` = 'usual.event.bluetooth.remotedevice.CONNECT_REPLY'
  - `COMMON_EVENT_BLUETOOTH_REMOTEDEVICE_CONNECT_CANCEL` = 'usual.event.bluetooth.remotedevice.CONNECT_CANCEL'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREEUNIT_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.handsfreeunit.CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREEUNIT_AUDIO_STATE_UPDATE` = 'usual.event.bluetooth.handsfreeunit.AUDIO_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREEUNIT_AG_COMMON_EVENT` = 'usual.event.bluetooth.handsfreeunit.AG_COMMON_EVENT'
  - `COMMON_EVENT_BLUETOOTH_HANDSFREEUNIT_AG_CALL_STATE_UPDATE` = 'usual.event.bluetooth.handsfreeunit.AG_CALL_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HOST_STATE_UPDATE` = 'usual.event.bluetooth.host.STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HOST_REQ_DISCOVERABLE` = 'usual.event.bluetooth.host.REQ_DISCOVERABLE'
  - `COMMON_EVENT_BLUETOOTH_HOST_REQ_ENABLE` = 'usual.event.bluetooth.host.REQ_ENABLE'
  - `COMMON_EVENT_BLUETOOTH_HOST_REQ_DISABLE` = 'usual.event.bluetooth.host.REQ_DISABLE'
  - `COMMON_EVENT_BLUETOOTH_HOST_SCAN_MODE_UPDATE` = 'usual.event.bluetooth.host.SCAN_MODE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_HOST_DISCOVERY_STARTED` = 'usual.event.bluetooth.host.DISCOVERY_STARTED'
  - `COMMON_EVENT_BLUETOOTH_HOST_DISCOVERY_FINISHED` = 'usual.event.bluetooth.host.DISCOVERY_FINISHED'
  - `COMMON_EVENT_BLUETOOTH_HOST_NAME_UPDATE` = 'usual.event.bluetooth.host.NAME_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSINK_CONNECT_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsink.CONNECT_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSINK_PLAYING_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsink.PLAYING_STATE_UPDATE'
  - `COMMON_EVENT_BLUETOOTH_A2DPSINK_AUDIO_STATE_UPDATE` = 'usual.event.bluetooth.a2dpsink.AUDIO_STATE_UPDATE'
  - `COMMON_EVENT_NFC_ACTION_ADAPTER_STATE_CHANGED` = 'usual.event.nfc.action.ADAPTER_STATE_CHANGED'
  - `COMMON_EVENT_NFC_ACTION_RF_FIELD_ON_DETECTED` = 'usual.event.nfc.action.RF_FIELD_ON_DETECTED'
  - `COMMON_EVENT_NFC_ACTION_RF_FIELD_OFF_DETECTED` = 'usual.event.nfc.action.RF_FIELD_OFF_DETECTED'
  - `COMMON_EVENT_DISCHARGING` = 'usual.event.DISCHARGING'
  - `COMMON_EVENT_CHARGING` = 'usual.event.CHARGING'
  - `COMMON_EVENT_CHARGE_TYPE_CHANGED` = 'usual.event.CHARGE_TYPE_CHANGED'
  - `COMMON_EVENT_DEVICE_IDLE_MODE_CHANGED` = 'usual.event.DEVICE_IDLE_MODE_CHANGED'
  - `COMMON_EVENT_CHARGE_IDLE_MODE_CHANGED` = 'usual.event.CHARGE_IDLE_MODE_CHANGED'
  - `COMMON_EVENT_DEVICE_IDLE_EXEMPTION_LIST_UPDATED` = 'usual.event.DEVICE_IDLE_EXEMPTION_LIST_UPDATED'
  - `COMMON_EVENT_POWER_SAVE_MODE_CHANGED` = 'usual.event.POWER_SAVE_MODE_CHANGED'
  - `COMMON_EVENT_USER_ADDED` = 'usual.event.USER_ADDED'
  - `COMMON_EVENT_USER_REMOVED` = 'usual.event.USER_REMOVED'
  - `COMMON_EVENT_ABILITY_ADDED` = 'common.event.ABILITY_ADDED'
  - `COMMON_EVENT_ABILITY_REMOVED` = 'common.event.ABILITY_REMOVED'
  - `COMMON_EVENT_ABILITY_UPDATED` = 'common.event.ABILITY_UPDATED'
  - `COMMON_EVENT_LOCATION_MODE_STATE_CHANGED` = 'usual.event.location.MODE_STATE_CHANGED'
  - `COMMON_EVENT_IVI_SLEEP` = 'common.event.IVI_SLEEP'
  - `COMMON_EVENT_IVI_PAUSE` = 'common.event.IVI_PAUSE'
  - `COMMON_EVENT_IVI_STANDBY` = 'common.event.IVI_STANDBY'
  - `COMMON_EVENT_IVI_LASTMODE_SAVE` = 'common.event.IVI_LASTMODE_SAVE'
  - `COMMON_EVENT_IVI_VOLTAGE_ABNORMAL` = 'common.event.IVI_VOLTAGE_ABNORMAL'
  - `COMMON_EVENT_IVI_HIGH_TEMPERATURE` = 'common.event.IVI_HIGH_TEMPERATURE'
  - `COMMON_EVENT_IVI_EXTREME_TEMPERATURE` = 'common.event.IVI_EXTREME_TEMPERATURE'
  - `COMMON_EVENT_IVI_TEMPERATURE_ABNORMAL` = 'common.event.IVI_TEMPERATURE_ABNORMAL'
  - `COMMON_EVENT_IVI_VOLTAGE_RECOVERY` = 'common.event.IVI_VOLTAGE_RECOVERY'
  - `COMMON_EVENT_IVI_TEMPERATURE_RECOVERY` = 'common.event.IVI_TEMPERATURE_RECOVERY'
  - `COMMON_EVENT_IVI_ACTIVE` = 'common.event.IVI_ACTIVE'
  - `COMMON_EVENT_USB_STATE` = 'usual.event.hardware.usb.action.USB_STATE'
  - `COMMON_EVENT_USB_PORT_CHANGED` = 'usual.event.hardware.usb.action.USB_PORT_CHANGED'
  - `COMMON_EVENT_USB_DEVICE_ATTACHED` = 'usual.event.hardware.usb.action.USB_DEVICE_ATTACHED'
  - `COMMON_EVENT_USB_DEVICE_DETACHED` = 'usual.event.hardware.usb.action.USB_DEVICE_DETACHED'
  - `COMMON_EVENT_USB_ACCESSORY_ATTACHED` = 'usual.event.hardware.usb.action.USB_ACCESSORY_ATTACHED'
  - `COMMON_EVENT_USB_ACCESSORY_DETACHED` = 'usual.event.hardware.usb.action.USB_ACCESSORY_DETACHED'
  - `COMMON_EVENT_DISK_REMOVED` = 'usual.event.data.DISK_REMOVED'
  - `COMMON_EVENT_DISK_UNMOUNTED` = 'usual.event.data.DISK_UNMOUNTED'
  - `COMMON_EVENT_DISK_MOUNTED` = 'usual.event.data.DISK_MOUNTED'
  - `COMMON_EVENT_DISK_BAD_REMOVAL` = 'usual.event.data.DISK_BAD_REMOVAL'
  - `COMMON_EVENT_DISK_UNMOUNTABLE` = 'usual.event.data.DISK_UNMOUNTABLE'
  - `COMMON_EVENT_DISK_EJECT` = 'usual.event.data.DISK_EJECT'
  - `COMMON_EVENT_VOLUME_REMOVED` = 'usual.event.data.VOLUME_REMOVED'
  - `COMMON_EVENT_VOLUME_UNMOUNTED` = 'usual.event.data.VOLUME_UNMOUNTED'
  - `COMMON_EVENT_VOLUME_MOUNTED` = 'usual.event.data.VOLUME_MOUNTED'
  - `COMMON_EVENT_VOLUME_BAD_REMOVAL` = 'usual.event.data.VOLUME_BAD_REMOVAL'
  - `COMMON_EVENT_VOLUME_EJECT` = 'usual.event.data.VOLUME_EJECT'
  - `COMMON_EVENT_VISIBLE_ACCOUNTS_UPDATED` = 'usual.event.data.VISIBLE_ACCOUNTS_UPDATED'
  - `COMMON_EVENT_ACCOUNT_DELETED` = 'usual.event.data.ACCOUNT_DELETED'
  - `COMMON_EVENT_FOUNDATION_READY` = 'common.event.FOUNDATION_READY'
  - `COMMON_EVENT_AIRPLANE_MODE_CHANGED` = 'usual.event.AIRPLANE_MODE'
  - `COMMON_EVENT_SPLIT_SCREEN` = 'common.event.SPLIT_SCREEN'
  - `COMMON_EVENT_SLOT_CHANGE` = 'usual.event.SLOT_CHANGE'
  - `COMMON_EVENT_SPN_INFO_CHANGED` = 'usual.event.SPN_INFO_CHANGED'
  - `COMMON_EVENT_QUICK_FIX_APPLY_RESULT` = 'usual.event.QUICK_FIX_APPLY_RESULT'
  - `COMMON_EVENT_QUICK_FIX_REVOKE_RESULT` = 'usual.event.QUICK_FIX_REVOKE_RESULT'
  - `COMMON_EVENT_USER_INFO_UPDATED` = 'usual.event.USER_INFO_UPDATED'
  - `COMMON_EVENT_HTTP_PROXY_CHANGE` = 'usual.event.HTTP_PROXY_CHANGE'
  - `COMMON_EVENT_SIM_STATE_CHANGED` = 'usual.event.SIM_STATE_CHANGED'
  - `COMMON_EVENT_SMS_RECEIVE_COMPLETED` = 'usual.event.SMS_RECEIVE_COMPLETED'
  - `COMMON_EVENT_SMS_EMERGENCY_CB_RECEIVE_COMPLETED` = 'usual.event.SMS_EMERGENCY_CB_RECEIVE_COMPLETED'
  - `COMMON_EVENT_SMS_CB_RECEIVE_COMPLETED` = 'usual.event.SMS_CB_RECEIVE_COMPLETED'
  - `COMMON_EVENT_STK_COMMAND` = 'usual.event.STK_COMMAND'
  - `COMMON_EVENT_STK_SESSION_END` = 'usual.event.STK_SESSION_END'
  - `COMMON_EVENT_STK_CARD_STATE_CHANGED` = 'usual.event.STK_CARD_STATE_CHANGED'
  - `COMMON_EVENT_STK_ALPHA_IDENTIFIER` = 'usual.event.STK_ALPHA_IDENTIFIER'
  - `COMMON_EVENT_SMS_WAPPUSH_RECEIVE_COMPLETED` = 'usual.event.SMS_WAPPUSH_RECEIVE_COMPLETED'
  - `COMMON_EVENT_OPERATOR_CONFIG_CHANGED` = 'usual.event.OPERATOR_CONFIG_CHANGED'
  - `COMMON_EVENT_SIM_CARD_DEFAULT_SMS_SUBSCRIPTION_CHANGED` = 'usual.event.SIM.DEFAULT_SMS_SUBSCRIPTION_CHANGED'
  - `COMMON_EVENT_SIM_CARD_DEFAULT_DATA_SUBSCRIPTION_CHANGED` = 'usual.event.SIM.DEFAULT_DATA_SUBSCRIPTION_CHANGED'
  - `COMMON_EVENT_SIM_CARD_DEFAULT_MAIN_SUBSCRIPTION_CHANGED` = 'usual.event.SIM.DEFAULT_MAIN_SUBSCRIPTION_CHANGED'
  - `COMMON_EVENT_SET_PRIMARY_SLOT_STATUS` = 'usual.event.SET_PRIMARY_SLOT_STATUS'
  - `COMMON_EVENT_PRIMARY_SLOT_ROAMING` = 'usual.event.PRIMARY_SLOT_ROAMING'
  - `COMMON_EVENT_SIM_CARD_DEFAULT_VOICE_SUBSCRIPTION_CHANGED` = 'usual.event.SIM.DEFAULT_VOICE_SUBSCRIPTION_CHANGED'
  - `COMMON_EVENT_CALL_STATE_CHANGED` = 'usual.event.CALL_STATE_CHANGED'
  - `COMMON_EVENT_CELLULAR_DATA_STATE_CHANGED` = 'usual.event.CELLULAR_DATA_STATE_CHANGED'
  - `COMMON_EVENT_NETWORK_STATE_CHANGED` = 'usual.event.NETWORK_STATE_CHANGED'
  - `COMMON_EVENT_SIGNAL_INFO_CHANGED` = 'usual.event.SIGNAL_INFO_CHANGED'
  - `COMMON_EVENT_INCOMING_CALL_MISSED` = 'usual.event.INCOMING_CALL_MISSED'
  - `COMMON_EVENT_RADIO_STATE_CHANGE` = 'usual.event.RADIO_STATE_CHANGE'
  - `COMMON_EVENT_DOMAIN_ACCOUNT_STATUS_CHANGED` = 'usual.event.DOMAIN_ACCOUNT_STATUS_CHANGED'
  - `COMMON_EVENT_SCREEN_UNLOCKED` = 'usual.event.SCREEN_UNLOCKED'
  - `COMMON_EVENT_SCREEN_LOCKED` = 'usual.event.SCREEN_LOCKED'
  - `COMMON_EVENT_CONNECTIVITY_CHANGE` = 'usual.event.CONNECTIVITY_CHANGE'
  - `COMMON_EVENT_SPECIAL_CODE` = 'common.event.SPECIAL_CODE'
  - `COMMON_EVENT_AUDIO_QUALITY_CHANGE` = 'usual.event.AUDIO_QUALITY_CHANGE'
  - `COMMON_EVENT_PRIVACY_STATE_CHANGED` = 'usual.event.PRIVACY_STATE_CHANGED'
#### Functions
- `publish(event: string, callback: AsyncCallback<void>): void`
- `publish(event: string, options: CommonEventPublishData, callback: AsyncCallback<void>): void`
- `publishAsUser(event: string, userId: number, callback: AsyncCallback<void>): void`
- `publishAsUser(
    event: string,
    userId: number,
    options: CommonEventPublishData,
    callback: AsyncCallback<void>
  ): void`
- `createSubscriber(
    subscribeInfo: CommonEventSubscribeInfo,
    callback: AsyncCallback<CommonEventSubscriber>
  ): void`
- `createSubscriber(subscribeInfo: CommonEventSubscribeInfo): Promise<CommonEventSubscriber>`
- `createSubscriberSync(subscribeInfo: CommonEventSubscribeInfo): CommonEventSubscriber`
- `subscribe(subscriber: CommonEventSubscriber, callback: AsyncCallback<CommonEventData>): void`
- `unsubscribe(subscriber: CommonEventSubscriber, callback?: AsyncCallback<void>): void`
- `removeStickyCommonEvent(event: string, callback: AsyncCallback<void>): void`
- `removeStickyCommonEvent(event: string): Promise<void>`
- `setStaticSubscriberState(enable: boolean, callback: AsyncCallback<void>): void`
- `setStaticSubscriberState(enable: boolean): Promise<void>`
#### Type Aliases
- `CommonEventData` = _CommonEventData
- `CommonEventSubscriber` = _CommonEventSubscriber
- `CommonEventSubscribeInfo` = _CommonEventSubscribeInfo
- `CommonEventPublishData` = _CommonEventPublishData

### configPolicy (@ohos.configPolicy.d.ts)
#### Enums
- **FollowXMode**
  - `DEFAULT` = 0
  - `NO_RULE_FOLLOWED` = 1
  - `SIM_DEFAULT` = 10
  - `SIM_1` = 11
  - `SIM_2` = 12
  - `USER_DEFINED` = 100
#### Functions
- `getOneCfgFile(relPath: string): Promise<string>`
- `getOneCfgFile(relPath: string, followMode: FollowXMode, extra?: string): Promise<string>`
- `getOneCfgFileSync(relPath: string, followMode?: FollowXMode, extra?: string): string`
- `getCfgFiles(relPath: string): Promise<Array<string>>`
- `getCfgFiles(relPath: string, followMode: FollowXMode, extra?: string): Promise<Array<string>>`
- `getCfgFilesSync(relPath: string, followMode?: FollowXMode, extra?: string): Array<string>`
- `getCfgDirList(): Promise<Array<string>>`
- `getCfgDirListSync(): Array<string>`

### connectedTag (@ohos.connectedTag.d.ts)
#### Enums
- **NfcRfType**
  - `NFC_RF_LEAVE` = 0
  - `NFC_RF_ENTER` = 1
#### Functions
- `init(): boolean`
- `initialize(): void`
- `uninit(): boolean`
- `uninitialize(): void`
- `readNdefTag(): Promise<string>`
- `readNdefTag(callback: AsyncCallback<string>): void`
- `read(): Promise<number[]>`
- `read(callback: AsyncCallback<number[]>): void`
- `writeNdefTag(data: string): Promise<void>`
- `writeNdefTag(data: string, callback: AsyncCallback<void>): void`
- `write(data: number[]): Promise<void>`
- `write(data: number[], callback: AsyncCallback<void>): void`
- `on(type: 'notify', callback: Callback<number>): void`
- `off(type: 'notify', callback?: Callback<number>): void`

### contact (@ohos.contact.d.ts)
#### Interfaces
- **ContactSelectionOptions**
  - `isMultiSelect?`: boolean
#### Enums
- **Attribute**
#### Functions
- `addContact(contact: Contact, callback: AsyncCallback<number>): void`
- `addContact(context: Context, contact: Contact, callback: AsyncCallback<number>): void`
- `addContact(contact: Contact): Promise<number>`
- `addContact(context: Context, contact: Contact): Promise<number>`
- `selectContact(callback: AsyncCallback<Array<Contact>>): void`
- `selectContacts(callback: AsyncCallback<Array<Contact>>): void`
- `selectContact(): Promise<Array<Contact>>`
- `selectContacts(): Promise<Array<Contact>>`
- `selectContacts(options: ContactSelectionOptions, callback: AsyncCallback<Array<Contact>>): void`
- `selectContacts(options: ContactSelectionOptions): Promise<Array<Contact>>`
- `deleteContact(key: string, callback: AsyncCallback<void>): void`
- `deleteContact(context: Context, key: string, callback: AsyncCallback<void>): void`
- `deleteContact(key: string): Promise<void>`
- `deleteContact(context: Context, key: string): Promise<void>`
- `queryContact(key: string, callback: AsyncCallback<Contact>): void`
- `queryContact(context: Context, key: string, callback: AsyncCallback<Contact>): void`
- `queryContact(key: string, holder: Holder, callback: AsyncCallback<Contact>): void`
- `queryContact(context: Context, key: string, holder: Holder, callback: AsyncCallback<Contact>): void`
- `queryContact(key: string, attrs: ContactAttributes, callback: AsyncCallback<Contact>): void`
- `queryContact(context: Context, key: string, attrs: ContactAttributes, callback: AsyncCallback<Contact>): void`
- `queryContact(key: string, holder: Holder, attrs: ContactAttributes, callback: AsyncCallback<Contact>): void`
- `queryContact(context: Context, key: string, holder: Holder, attrs: ContactAttributes, callback: AsyncCallback<Contact>): void`
- `queryContact(key: string, holder?: Holder, attrs?: ContactAttributes): Promise<Contact>`
- `queryContact(context: Context, key: string, holder?: Holder, attrs?: ContactAttributes): Promise<Contact>`
- `queryContacts(callback: AsyncCallback<Array<Contact>>): void`
- `queryContacts(context: Context, callback: AsyncCallback<Array<Contact>>): void`
- `queryContacts(holder: Holder, callback: AsyncCallback<Array<Contact>>): void`
- `queryContacts(context: Context, holder: Holder, callback: AsyncCallback<Array<Contact>>): void`
- `queryContacts(attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContacts(context: Context, attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContacts(holder: Holder, attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContacts(context: Context, holder: Holder, attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContacts(holder?: Holder, attrs?: ContactAttributes): Promise<Array<Contact>>`
- `queryContacts(context: Context, holder?: Holder, attrs?: ContactAttributes): Promise<Array<Contact>>`
- `queryContactsByEmail(email: string, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByEmail(context: Context, email: string, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByEmail(email: string, holder: Holder, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByEmail(context: Context, email: string, holder: Holder,
    callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByEmail(email: string, attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByEmail(context: Context, email: string, attrs: ContactAttributes,
    callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByEmail(email: string, holder: Holder, attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByEmail(context: Context, email: string, holder: Holder, attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByEmail(email: string, holder?: Holder, attrs?: ContactAttributes): Promise<Array<Contact>>`
- `queryContactsByEmail(context: Context, email: string, holder?: Holder, attrs?: ContactAttributes): Promise<Array<Contact>>`
- `queryContactsByPhoneNumber(phoneNumber: string, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByPhoneNumber(context: Context, phoneNumber: string, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByPhoneNumber(phoneNumber: string, holder: Holder, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByPhoneNumber(context: Context, phoneNumber: string, holder: Holder, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByPhoneNumber(phoneNumber: string, attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByPhoneNumber(context: Context, phoneNumber: string, attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByPhoneNumber(phoneNumber: string, holder: Holder, attrs: ContactAttributes, callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByPhoneNumber(context: Context, phoneNumber: string, holder: Holder, attrs: ContactAttributes,
    callback: AsyncCallback<Array<Contact>>): void`
- `queryContactsByPhoneNumber(phoneNumber: string, holder?: Holder, attrs?: ContactAttributes): Promise<Array<Contact>>`
- `queryContactsByPhoneNumber(context: Context, phoneNumber: string, holder?: Holder, attrs?: ContactAttributes): Promise<Array<Contact>>`
- `queryGroups(callback: AsyncCallback<Array<Group>>): void`
- `queryGroups(context: Context, callback: AsyncCallback<Array<Group>>): void`
- `queryGroups(holder: Holder, callback: AsyncCallback<Array<Group>>): void`
- `queryGroups(context: Context, holder: Holder, callback: AsyncCallback<Array<Group>>): void`
- `queryGroups(holder?: Holder): Promise<Array<Group>>`
- `queryGroups(context: Context, holder?: Holder): Promise<Array<Group>>`
- `queryHolders(callback: AsyncCallback<Array<Holder>>): void`
- `queryHolders(context: Context, callback: AsyncCallback<Array<Holder>>): void`
- `queryHolders(): Promise<Array<Holder>>`
- `queryHolders(context: Context): Promise<Array<Holder>>`
- `queryKey(id: number, callback: AsyncCallback<string>): void`
- `queryKey(context: Context, id: number, callback: AsyncCallback<string>): void`
- `queryKey(id: number, holder: Holder, callback: AsyncCallback<string>): void`
- `queryKey(context: Context, id: number, holder: Holder, callback: AsyncCallback<string>): void`
- `queryKey(id: number, holder?: Holder): Promise<string>`
- `queryKey(context: Context, id: number, holder?: Holder): Promise<string>`
- `queryMyCard(callback: AsyncCallback<Contact>): void`
- `queryMyCard(context: Context, callback: AsyncCallback<Contact>): void`
- `queryMyCard(attrs: ContactAttributes, callback: AsyncCallback<Contact>): void`
- `queryMyCard(context: Context, attrs: ContactAttributes, callback: AsyncCallback<Contact>): void`
- `queryMyCard(attrs?: ContactAttributes): Promise<Contact>`
- `queryMyCard(context: Context, attrs?: ContactAttributes): Promise<Contact>`
- `updateContact(contact: Contact, callback: AsyncCallback<void>): void`
- `updateContact(context: Context, contact: Contact, callback: AsyncCallback<void>): void`
- `updateContact(contact: Contact, attrs: ContactAttributes, callback: AsyncCallback<void>): void`
- `updateContact(context: Context, contact: Contact, attrs: ContactAttributes, callback: AsyncCallback<void>): void`
- `updateContact(contact: Contact, attrs?: ContactAttributes): Promise<void>`
- `updateContact(context: Context, contact: Contact, attrs?: ContactAttributes): Promise<void>`
- `isLocalContact(id: number, callback: AsyncCallback<boolean>): void`
- `isLocalContact(context: Context, id: number, callback: AsyncCallback<boolean>): void`
- `isLocalContact(id: number): Promise<boolean>`
- `isLocalContact(context: Context, id: number): Promise<boolean>`
- `isMyCard(id: number, callback: AsyncCallback<boolean>): void`
- `isMyCard(context: Context, id: number, callback: AsyncCallback<boolean>): void`
- `isMyCard(id: number): Promise<boolean>`
- `isMyCard(context: Context, id: number): Promise<boolean>`
#### Classes
- **Contact**
- **ContactAttributes**
- **Email**
- **Event**
- **Group**
- **Holder**
- **ImAddress**
- **Name**
- **NickName**
- **Note**
- **Organization**
- **PhoneNumber**
- **Portrait**
- **PostalAddress**
- **Relation**
- **SipAddress**
- **Website**

### continuationManager (@ohos.continuation.continuationManager.d.ts)
#### Enums
- **DeviceConnectState**
  - `IDLE` = 0
  - `CONNECTING` = 1
  - `CONNECTED` = 2
  - `DISCONNECTING` = 3
- **ContinuationMode**
  - `COLLABORATION_SINGLE` = 0
  - `COLLABORATION_MULTIPLE` = 1
#### Functions
- `on(type: 'deviceSelected', token: number, callback: Callback<Array<ContinuationResult>>): void`
- `off(type: 'deviceSelected', token: number): void`
- `on(type: 'deviceUnselected', token: number, callback: Callback<Array<ContinuationResult>>): void`
- `off(type: 'deviceUnselected', token: number): void`
- `on(type: 'deviceConnect', callback: Callback<ContinuationResult>): void`
- `off(type: 'deviceConnect', callback?: Callback<ContinuationResult>): void`
- `on(type: 'deviceDisconnect', callback: Callback<string>): void`
- `off(type: 'deviceDisconnect', callback?: Callback<string>): void`
- `register(callback: AsyncCallback<number>): void`
- `register(options: ContinuationExtraParams, callback: AsyncCallback<number>): void`
- `register(options?: ContinuationExtraParams): Promise<number>`
- `unregister(token: number, callback: AsyncCallback<void>): void`
- `unregister(token: number): Promise<void>`
- `updateConnectStatus(
    token: number,
    deviceId: string,
    status: DeviceConnectState,
    callback: AsyncCallback<void>
  ): void`
- `updateConnectStatus(token: number, deviceId: string, status: DeviceConnectState): Promise<void>`
- `startDeviceManager(token: number, callback: AsyncCallback<void>): void`
- `startDeviceManager(token: number, options: ContinuationExtraParams, callback: AsyncCallback<void>): void`
- `startDeviceManager(token: number, options?: ContinuationExtraParams): Promise<void>`
- `registerContinuation(callback: AsyncCallback<number>): void`
- `registerContinuation(options: ContinuationExtraParams, callback: AsyncCallback<number>): void`
- `registerContinuation(options?: ContinuationExtraParams): Promise<number>`
- `unregisterContinuation(token: number, callback: AsyncCallback<void>): void`
- `unregisterContinuation(token: number): Promise<void>`
- `updateContinuationState(
    token: number,
    deviceId: string,
    status: DeviceConnectState,
    callback: AsyncCallback<void>
  ): void`
- `updateContinuationState(token: number, deviceId: string, status: DeviceConnectState): Promise<void>`
- `startContinuationDeviceManager(token: number, callback: AsyncCallback<void>): void`
- `startContinuationDeviceManager(
    token: number,
    options: ContinuationExtraParams,
    callback: AsyncCallback<void>
  ): void`
- `startContinuationDeviceManager(token: number, options?: ContinuationExtraParams): Promise<void>`
#### Type Aliases
- `ContinuationResult` = _ContinuationResult
- `ContinuationExtraParams` = _ContinuationExtraParams

### xml (@ohos.convertxml.d.ts)
#### Interfaces
- **ConvertOptions**
  - `trim`: boolean
  - `ignoreDeclaration?`: boolean
  - `ignoreInstruction?`: boolean
  - `ignoreAttributes?`: boolean
  - `ignoreComment?`: boolean
  - `ignoreCDATA?`: boolean
  - `ignoreDoctype?`: boolean
  - `ignoreText?`: boolean
  - `declarationKey`: string
  - `instructionKey`: string
  - `attributesKey`: string
  - `textKey`: string
  - `cdataKey`: string
  - `doctypeKey`: string
  - `commentKey`: string
  - `parentKey`: string
  - `typeKey`: string
  - `nameKey`: string
  - `elementsKey`: string
#### Classes
- **ConvertXML**
  - `convert()`: Object
  - `convertToJSObject()`: Object

### cooperate (@ohos.cooperate.d.ts)
#### Interfaces
- **CooperateMessage**
  - `networkId`: string
  - `state`: CooperateState
#### Enums
- **CooperateMsg**
  - `COOPERATE_PREPARE` = 0
  - `COOPERATE_UNPREPARE` = 1
  - `COOPERATE_ACTIVATE` = 2
  - `COOPERATE_ACTIVATE_SUCCESS` = 3
  - `COOPERATE_ACTIVATE_FAIL` = 4
  - `COOPERATE_DEACTIVATE_SUCCESS` = 5
  - `COOPERATE_DEACTIVATE_FAIL` = 6
  - `COOPERATE_SESSION_DISCONNECTED` = 7
- **CooperateState**
  - `COOPERATE_PREPARE` = 0
  - `COOPERATE_UNPREPARE` = 1
  - `COOPERATE_ACTIVATE` = 2
  - `COOPERATE_ACTIVATE_SUCCESS` = 3
  - `COOPERATE_ACTIVATE_FAILURE` = 4
  - `COOPERATE_DEACTIVATE_SUCCESS` = 5
  - `COOPERATE_DEACTIVATE_FAILURE` = 6
  - `COOPERATE_SESSION_DISCONNECTED` = 7
#### Functions
- `prepare(callback: AsyncCallback<void>): void`
- `prepare(): Promise<void>`
- `prepareCooperate(callback: AsyncCallback<void>): void`
- `prepareCooperate(): Promise<void>`
- `unprepare(callback: AsyncCallback<void>): void`
- `unprepare(): Promise<void>`
- `unprepareCooperate(callback: AsyncCallback<void>): void`
- `unprepareCooperate(): Promise<void>`
- `activate(targetNetworkId: string, inputDeviceId: number, callback: AsyncCallback<void>): void`
- `activate(targetNetworkId: string, inputDeviceId: number): Promise<void>`
- `activateCooperate(targetNetworkId: string, inputDeviceId: number, callback: AsyncCallback<void>): void`
- `activateCooperate(targetNetworkId: string, inputDeviceId: number): Promise<void>`
- `deactivate(isUnchained: boolean, callback: AsyncCallback<void>): void`
- `deactivate(isUnchained: boolean): Promise<void>`
- `deactivateCooperate(isUnchained: boolean, callback: AsyncCallback<void>): void`
- `deactivateCooperate(isUnchained: boolean): Promise<void>`
- `getCrossingSwitchState(networkId: string, callback: AsyncCallback<boolean>): void`
- `getCrossingSwitchState(networkId: string): Promise<boolean>`
- `getCooperateSwitchState(networkId: string, callback: AsyncCallback<boolean>): void`
- `getCooperateSwitchState(networkId: string): Promise<boolean>`
- `on(type: 'cooperate', callback: Callback<{ networkId: string, msg: CooperateMsg }>): void`
- `off(type: 'cooperate', callback?: Callback<void>): void`
- `on(type: 'cooperateMessage', callback: Callback<CooperateMessage>): void`
- `off(type: 'cooperateMessage', callback?: Callback<CooperateMessage>): void`

### curves (@ohos.curves.d.ts)
#### Interfaces
- **ICurve**
  - `interpolate`: number
#### Enums
- **Curve**
#### Functions
- `initCurve(curve?: Curve): ICurve`
- `init(curve?: Curve): string`
- `stepsCurve(count: number, end: boolean): ICurve`
- `steps(count: number, end: boolean): string`
- `cubicBezierCurve(x1: number, y1: number, x2: number, y2: number): ICurve`
- `cubicBezier(x1: number, y1: number, x2: number, y2: number): string`
- `springCurve(velocity: number, mass: number, stiffness: number, damping: number): ICurve`
- `spring(velocity: number, mass: number, stiffness: number, damping: number): string`
- `springMotion(response?: number, dampingFraction?: number, overlapDuration?: number): ICurve`
- `responsiveSpringMotion(response?: number, dampingFraction?: number, overlapDuration?: number): ICurve`
- `interpolatingSpring(velocity: number, mass: number, stiffness: number, damping: number): ICurve`

### @ohos.data.DataShareResultSet (@ohos.data.DataShareResultSet.d.ts)
#### Interfaces
- **DataShareResultSet**
  - `columnNames`: Array<string>
  - `columnCount`: number
  - `rowCount`: number
  - `isClosed`: boolean
  - `goToFirstRow`: boolean
  - `goToLastRow`: boolean
  - `goToNextRow`: boolean
  - `goToPreviousRow`: boolean
  - `goTo`: boolean
  - `goToRow`: boolean
  - `getBlob`: Uint8Array
  - `getString`: string
  - `getLong`: number
  - `getDouble`: number
  - `close`: void
  - `getColumnIndex`: number
  - `getColumnName`: string
  - `getDataType`: DataType
#### Enums
- **DataType**
  - `TYPE_NULL` = 0
  - `TYPE_LONG` = 1
  - `TYPE_DOUBLE` = 2
  - `TYPE_STRING` = 3
  - `TYPE_BLOB` = 4

### @ohos.data.ValuesBucket (@ohos.data.ValuesBucket.d.ts)
#### Type Aliases
- `ValueType` = number | string | boolean
- `ValuesBucket` = Record<string, ValueType | Uint8Array | null>

### cloudData (@ohos.data.cloudData.d.ts)
#### Interfaces
- **ExtraData**
  - `eventId`: string
  - `extraData`: string
- **Result**
  - `code`: number
  - `description?`: string
  - `value?`: T
- **Privilege**
  - `writable?`: boolean
  - `readable?`: boolean
  - `creatable?`: boolean
  - `deletable?`: boolean
  - `shareable?`: boolean
- **Participant**
  - `identity`: string
  - `role?`: Role
  - `state?`: State
  - `privilege?`: Privilege
  - `attachInfo?`: string
#### Enums
- **ClearAction**
- **Role**
  - `ROLE_INVITER` = 0
  - `ROLE_INVITEE` = 1
- **State**
  - `STATE_UNKNOWN` = 0
  - `STATE_ACCEPTED` = 1
  - `STATE_REJECTED` = 2
  - `STATE_SUSPENDED` = 3
- **SharingCode**
  - `SUCCESS` = 0
  - `REPEATED_REQUEST` = 1
  - `NOT_INVITER` = 2
  - `NOT_INVITER_OR_INVITEE` = 3
  - `OVER_QUOTA` = 4
  - `TOO_MANY_PARTICIPANTS` = 5
  - `INVALID_ARGS` = 6
  - `NETWORK_ERROR` = 7
  - `CLOUD_DISABLED` = 8
  - `SERVER_ERROR` = 9
  - `INNER_ERROR` = 10
  - `INVALID_INVITATION` = 11
  - `RATE_LIMIT` = 12
  - `CUSTOM_ERROR` = 1000
#### Functions
- `allocResourceAndShare(
      storeId: string,
      predicates: relationalStore.RdbPredicates,
      participants: Array<Participant>,
      columns?: Array<string>
    ): Promise<relationalStore.Resu`
- `allocResourceAndShare(
      storeId: string,
      predicates: relationalStore.RdbPredicates,
      participants: Array<Participant>,
      callback: AsyncCallback<relationalStore.ResultSet>
    ): v`
- `allocResourceAndShare(
      storeId: string,
      predicates: relationalStore.RdbPredicates,
      participants: Array<Participant>,
      columns: Array<string>,
      callback: AsyncCallback<relat`
- `share(
      sharingResource: string,
      participants: Array<Participant>,
      callback: AsyncCallback<Result<Array<Result<Participant>>>>
    ): void`
- `share(
      sharingResource: string,
      participants: Array<Participant>
    ): Promise<Result<Array<Result<Participant>>>>`
- `unshare(
      sharingResource: string,
      participants: Array<Participant>,
      callback: AsyncCallback<Result<Array<Result<Participant>>>>
    ): void`
- `unshare(
      sharingResource: string,
      participants: Array<Participant>
    ): Promise<Result<Array<Result<Participant>>>>`
- `exit(sharingResource: string, callback: AsyncCallback<Result<void>>): void`
- `exit(sharingResource: string): Promise<Result<void>>`
- `changePrivilege(
      sharingResource: string,
      participants: Array<Participant>,
      callback: AsyncCallback<Result<Array<Result<Participant>>>>
    ): void`
- `changePrivilege(
      sharingResource: string,
      participants: Array<Participant>
    ): Promise<Result<Array<Result<Participant>>>>`
- `queryParticipants(sharingResource: string, callback: AsyncCallback<Result<Array<Participant>>>): void`
- `queryParticipants(sharingResource: string): Promise<Result<Array<Participant>>>`
- `queryParticipantsByInvitation(
      invitationCode: string,
      callback: AsyncCallback<Result<Array<Participant>>>
    ): void`
- `queryParticipantsByInvitation(invitationCode: string): Promise<Result<Array<Participant>>>`
- `confirmInvitation(invitationCode: string, state: State, callback: AsyncCallback<Result<string>>): void`
- `confirmInvitation(invitationCode: string, state: State): Promise<Result<string>>`
- `changeConfirmation(sharingResource: string, state: State, callback: AsyncCallback<Result<void>>): void`
- `changeConfirmation(sharingResource: string, state: State): Promise<Result<void>>`
#### Classes
- **Config**
  - `enableCloud()`: void
  - `enableCloud()`: Promise<void>
  - `disableCloud()`: void
  - `disableCloud()`: Promise<void>
  - `changeAppCloudSwitch()`: void
  - `changeAppCloudSwitch()`: Promise<void>
  - `notifyDataChange()`: Promise<void>
  - `notifyDataChange()`: void
  - `notifyDataChange()`: void
  - `notifyDataChange()`: Promise<void>
  - `notifyDataChange()`: void
  - `clear()`: void
  - `clear()`: Promise<void>

### cloudExtension (@ohos.data.cloudExtension.d.ts)
#### Interfaces
- **CloudAsset**
  - `assetId`: string
  - `hash`: string
- **CloudInfo**
  - `cloudInfo`: ServiceInfo
  - `apps`: Record<string, AppBriefInfo>
- **ServiceInfo**
  - `enableCloud`: boolean
  - `id`: string
  - `totalSpace`: number
  - `remainingSpace`: number
  - `user`: number
- **AppBriefInfo**
  - `appId`: string
  - `bundleName`: string
  - `cloudSwitch`: boolean
  - `instanceId`: number
- **Field**
  - `alias`: string
  - `colName`: string
  - `type`: FieldType
  - `primary`: boolean
  - `nullable`: boolean
- **Table**
  - `alias`: string
  - `name`: string
  - `fields`: Array<Field>
- **Database**
  - `name`: string
  - `alias`: string
  - `tables`: Array<Table>
- **AppSchema**
  - `bundleName`: string
  - `version`: number
  - `databases`: Array<Database>
- **CloudData**
  - `nextCursor`: string
  - `hasMore`: boolean
  - `values`: Array<Record<string, CloudType>>
- **SubscribeInfo**
  - `expirationTime`: number
  - `subscribe`: Record<string, Array<SubscribeId>>
- **SubscribeId**
  - `databaseAlias`: string
  - `id`: string
- **ExtensionValue**
  - `readonly id`: string
  - `readonly createTime`: number
  - `readonly modifyTime`: number
  - `readonly operation`: Flag
- **LockInfo**
  - `interval`: number
  - `lockId`: number
- **Result**
  - `code`: number
  - `description?`: string
  - `value?`: T
- **CloudDB**
  - `generateId`: Promise<Result<Array<string>>>
  - `insert`: Promise<Array<Result<Record<string, CloudType>>>>
  - `update`: Promise<Array<Result<Record<string, CloudType>>>>
  - `delete`: Promise<Array<Result<Record<string, CloudType>>>>
  - `query`: Promise<Result<CloudData>>
  - `lock`: Promise<Result<LockInfo>>
  - `heartbeat`: Promise<Result<LockInfo>>
  - `unlock`: Promise<Result<boolean>>
- **AssetLoader**
  - `download`: Promise<Array<Result<CloudAsset>>>
  - `upload`: Promise<Array<Result<CloudAsset>>>
- **ShareCenter**
  - `share`: Promise<Result<Array<Result<cloudData.sharing.Participant>>>>
  - `unshare`: Promise<Result<Array<Result<cloudData.sharing.Participant>>>>
  - `exit`: Promise<Result<void>>
  - `changePrivilege`: Promise<Result<Array<Result<cloudData.sharing.Participant>>>>
  - `queryParticipants`: Promise<Result<Array<cloudData.sharing.Participant>>>
  - `queryParticipantsByInvitation`: Promise<Result<Array<cloudData.sharing.Participant>>>
  - `confirmInvitation`: Promise<Result<string>>
  - `changeConfirmation`: Promise<Result<void>>
- **CloudService**
  - `getServiceInfo`: Promise<ServiceInfo>
  - `getAppBriefInfo`: Promise<Record<string, AppBriefInfo>>
  - `getAppSchema`: Promise<Result<AppSchema>>
  - `subscribe`: Promise<Result<SubscribeInfo>>
  - `unsubscribe`: Promise<number>
  - `connectDB`: Promise<rpc.RemoteObject>
  - `connectAssetLoader`: Promise<rpc.RemoteObject>
  - `connectShareCenter`: Promise<rpc.RemoteObject>
#### Enums
- **FieldType**
  - `NULL` = 0
  - `NUMBER` = 1
  - `REAL` = 2
  - `TEXT` = 3
  - `BOOL` = 4
  - `BLOB` = 5
- **Flag**
  - `INSERT` = 0
  - `UPDATE` = 1
  - `DELETE` = 2
- **ErrorCode**
  - `SUCCESS` = 0
  - `UNKNOWN_ERROR` = 1
  - `NETWORK_ERROR` = 2
  - `CLOUD_DISABLED` = 3
  - `LOCKED_BY_OTHERS` = 4
  - `RECORD_LIMIT_EXCEEDED` = 5
  - `NO_SPACE_FOR_ASSET` = 6
#### Functions
- `createShareServiceStub(instance: ShareCenter): Promise<rpc.RemoteObject>`
- `createCloudServiceStub(instance: CloudService): Promise<rpc.RemoteObject>`
- `createCloudDBStub(instance: CloudDB): Promise<rpc.RemoteObject>`
- `createAssetLoaderStub(instance: AssetLoader): Promise<rpc.RemoteObject>`
#### Type Aliases
- `CloudAssets` = Array<CloudAsset>
- `CloudType` = null | number | string | boolean | Uint8Array | CloudAsset | CloudAssets

### commonType (@ohos.data.commonType.d.ts)
#### Interfaces
- **Asset**
  - `name`: string
  - `uri`: string
  - `path`: string
  - `createTime`: string
  - `modifyTime`: string
  - `size`: string
  - `status?`: AssetStatus
#### Enums
- **AssetStatus**
#### Type Aliases
- `Assets` = Array<Asset>
- `ValueType` = null | number | string | boolean | Uint8Array | Asset | Assets
- `ValuesBucket` = Record<string, ValueType>

### dataAbility (@ohos.data.dataAbility.d.ts)
#### Functions
- `createRdbPredicates(name: string, dataAbilityPredicates: DataAbilityPredicates): rdb.RdbPredicates`
#### Classes
- **DataAbilityPredicates**
  - `equalTo()`: DataAbilityPredicates
  - `notEqualTo()`: DataAbilityPredicates
  - `beginWrap()`: DataAbilityPredicates
  - `endWrap()`: DataAbilityPredicates
  - `or()`: DataAbilityPredicates
  - `and()`: DataAbilityPredicates
  - `contains()`: DataAbilityPredicates
  - `beginsWith()`: DataAbilityPredicates
  - `endsWith()`: DataAbilityPredicates
  - `isNull()`: DataAbilityPredicates
  - `isNotNull()`: DataAbilityPredicates
  - `like()`: DataAbilityPredicates
  - `glob()`: DataAbilityPredicates
  - `between()`: DataAbilityPredicates
#### Type Aliases
- `ValueType` = number | string | boolean

### dataShare (@ohos.data.dataShare.d.ts)
#### Interfaces
- **DataShareHelperOptions**
  - `isProxy?`: boolean
- **TemplateId**
  - `subscriberId`: string
  - `bundleNameOfOwner`: string
- **PublishedItem**
  - `key`: string
  - `data`: string | ArrayBuffer
  - `subscriberId`: string
- **RdbDataChangeNode**
  - `uri`: string
  - `templateId`: TemplateId
  - `data`: Array<string>
- **PublishedDataChangeNode**
  - `bundleName`: string
  - `data`: Array<PublishedItem>
- **Template**
  - `predicates`: Record<string, string>
  - `scheduler`: string
- **OperationResult**
  - `key`: string
  - `result`: number
- **DataShareHelper**
  - `on`: void
  - `off`: void
  - `addTemplate`: void
  - `delTemplate`: void
  - `on`: Array<OperationResult>
  - `off`: Array<OperationResult>
  - `on`: Array<OperationResult>
  - `off`: Array<OperationResult>
  - `publish`: void
  - `publish`: void
  - `publish`: Promise<Array<OperationResult>>
  - `getPublishedData`: void
  - `getPublishedData`: Promise<Array<PublishedItem>>
  - `insert`: void
  - `insert`: Promise<number>
  - `delete`: void
  - `delete`: Promise<number>
  - `query`: void
  - `query`: Promise<DataShareResultSet>
  - `update`: void
  - `update`: Promise<number>
  - `batchInsert`: void
  - `batchInsert`: Promise<number>
  - `normalizeUri`: void
  - `normalizeUri`: Promise<string>
  - `denormalizeUri`: void
  - `denormalizeUri`: Promise<string>
  - `notifyChange`: void
  - `notifyChange`: Promise<void>
#### Functions
- `createDataShareHelper(context: Context, uri: string, callback: AsyncCallback<DataShareHelper>): void`
- `createDataShareHelper(
    context: Context,
    uri: string,
    options: DataShareHelperOptions,
    callback: AsyncCallback<DataShareHelper>
  ): void`
- `createDataShareHelper(
    context: Context,
    uri: string,
    options?: DataShareHelperOptions
  ): Promise<DataShareHelper>`
- `enableSilentProxy(context: Context, uri?: string): Promise<void>`
- `disableSilentProxy(context: Context, uri?: string): Promise<void>`

### dataSharePredicates (@ohos.data.dataSharePredicates.d.ts)
#### Classes
- **DataSharePredicates**
  - `equalTo()`: DataSharePredicates
  - `notEqualTo()`: DataSharePredicates
  - `beginWrap()`: DataSharePredicates
  - `endWrap()`: DataSharePredicates
  - `or()`: DataSharePredicates
  - `and()`: DataSharePredicates
  - `contains()`: DataSharePredicates
  - `beginsWith()`: DataSharePredicates
  - `endsWith()`: DataSharePredicates
  - `isNull()`: DataSharePredicates
  - `isNotNull()`: DataSharePredicates
  - `like()`: DataSharePredicates
  - `unlike()`: DataSharePredicates
  - `glob()`: DataSharePredicates
  - `between()`: DataSharePredicates
  - `notBetween()`: DataSharePredicates
  - `greaterThan()`: DataSharePredicates
  - `lessThan()`: DataSharePredicates
  - `greaterThanOrEqualTo()`: DataSharePredicates
  - `lessThanOrEqualTo()`: DataSharePredicates
  - `orderByAsc()`: DataSharePredicates
  - `orderByDesc()`: DataSharePredicates
  - `distinct()`: DataSharePredicates
  - `limit()`: DataSharePredicates
  - `groupBy()`: DataSharePredicates
  - `indexedBy()`: DataSharePredicates
  - `in()`: DataSharePredicates
  - `notIn()`: DataSharePredicates
  - `prefixKey()`: DataSharePredicates
  - `inKeys()`: DataSharePredicates

### distributedData (@ohos.data.distributedData.d.ts)
#### Interfaces
- **KVManagerConfig**
  - `userInfo`: UserInfo
  - `bundleName`: string
- **UserInfo**
  - `userId?`: string
  - `userType?`: UserType
- **Value**
  - `type`: ValueType
  - `value`: Uint8Array | string | number | boolean
- **Entry**
  - `key`: string
  - `value`: Value
- **ChangeNotification**
  - `insertEntries`: Entry[]
  - `updateEntries`: Entry[]
  - `deleteEntries`: Entry[]
  - `deviceId`: string
- **Options**
  - `createIfMissing?`: boolean
  - `encrypt?`: boolean
  - `backup?`: boolean
  - `autoSync?`: boolean
  - `kvStoreType?`: KVStoreType
  - `securityLevel?`: SecurityLevel
  - `schema?`: Schema
- **KvStoreResultSet**
  - `getCount`: number
  - `getPosition`: number
  - `moveToFirst`: boolean
  - `moveToLast`: boolean
  - `moveToNext`: boolean
  - `moveToPrevious`: boolean
  - `move`: boolean
  - `moveToPosition`: boolean
  - `isFirst`: boolean
  - `isLast`: boolean
  - `isBeforeFirst`: boolean
  - `isAfterLast`: boolean
  - `getEntry`: Entry
- **KVStore**
  - `put`: void
  - `put`: Promise<void>
  - `delete`: void
  - `delete`: Promise<void>
  - `on`: void
  - `on`: void
  - `off`: void
  - `off`: void
  - `putBatch`: void
  - `putBatch`: Promise<void>
  - `deleteBatch`: void
  - `deleteBatch`: Promise<void>
  - `startTransaction`: void
  - `startTransaction`: Promise<void>
  - `commit`: void
  - `commit`: Promise<void>
  - `rollback`: void
  - `rollback`: Promise<void>
  - `enableSync`: void
  - `enableSync`: Promise<void>
  - `setSyncRange`: void
  - `setSyncRange`: Promise<void>
- **SingleKVStore**
  - `get`: void
  - `get`: Promise<Uint8Array | string | boolean | number>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getResultSet`: void
  - `getResultSet`: Promise<KvStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KvStoreResultSet>
  - `closeResultSet`: void
  - `closeResultSet`: Promise<void>
  - `getResultSize`: void
  - `getResultSize`: Promise<number>
  - `removeDeviceData`: void
  - `removeDeviceData`: Promise<void>
  - `sync`: void
  - `on`: void
  - `on`: void
  - `off`: void
  - `off`: void
  - `setSyncParam`: void
  - `setSyncParam`: Promise<void>
  - `getSecurityLevel`: void
  - `getSecurityLevel`: Promise<SecurityLevel>
- **DeviceKVStore**
  - `get`: void
  - `get`: Promise<boolean | string | number | Uint8Array>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getResultSet`: void
  - `getResultSet`: Promise<KvStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KvStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KvStoreResultSet>
  - `closeResultSet`: void
  - `closeResultSet`: Promise<void>
  - `getResultSize`: void
  - `getResultSize`: Promise<number>
  - `getResultSize`: void
  - `getResultSize`: Promise<number>
  - `removeDeviceData`: void
  - `removeDeviceData`: Promise<void>
  - `sync`: void
  - `on`: void
  - `on`: void
  - `off`: void
  - `off`: void
- **KVManager**
  - `closeKVStore`: void
  - `closeKVStore`: Promise<void>
  - `deleteKVStore`: void
  - `deleteKVStore`: Promise<void>
  - `getAllKVStoreId`: void
  - `getAllKVStoreId`: Promise<string[]>
  - `on`: void
  - `off`: void
#### Enums
- **UserType**
  - `SAME_USER_ID` = 0
- **ValueType**
  - `STRING` = 0
  - `INTEGER` = 1
  - `FLOAT` = 2
  - `BYTE_ARRAY` = 3
  - `BOOLEAN` = 4
  - `DOUBLE` = 5
- **SyncMode**
  - `PULL_ONLY` = 0
  - `PUSH_ONLY` = 1
  - `PUSH_PULL` = 2
- **SubscribeType**
  - `SUBSCRIBE_TYPE_LOCAL` = 0
  - `SUBSCRIBE_TYPE_REMOTE` = 1
  - `SUBSCRIBE_TYPE_ALL` = 2
- **KVStoreType**
- **SecurityLevel**
  - `NO_LEVEL` = 0
  - `S0` = 1
  - `S1` = 2
  - `S2` = 3
  - `S3` = 5
  - `S4` = 6
#### Functions
- `createKVManager(config: KVManagerConfig, callback: AsyncCallback<KVManager>): void`
- `createKVManager(config: KVManagerConfig): Promise<KVManager>`
#### Classes
- **Schema**
  - `root`: FieldNode
  - `indexes`: Array<string>
  - `mode`: number
  - `skip`: number
- **FieldNode**
  - `appendChild()`: boolean
  - `default`: string
  - `nullable`: boolean
  - `type`: number
- **Query**
  - `reset()`: Query
  - `equalTo()`: Query
  - `notEqualTo()`: Query
  - `greaterThan()`: Query
  - `lessThan()`: Query
  - `greaterThanOrEqualTo()`: Query
  - `lessThanOrEqualTo()`: Query
  - `isNull()`: Query
  - `inNumber()`: Query
  - `inString()`: Query
  - `notInNumber()`: Query
  - `notInString()`: Query
  - `like()`: Query
  - `unlike()`: Query
  - `and()`: Query
  - `or()`: Query
  - `orderByAsc()`: Query
  - `orderByDesc()`: Query
  - `limit()`: Query
  - `isNotNull()`: Query
  - `beginGroup()`: Query
  - `endGroup()`: Query
  - `prefixKey()`: Query
  - `setSuggestIndex()`: Query
  - `deviceId()`: Query
  - `getSqlLike()`: string

### distributedDataObject (@ohos.data.distributedDataObject.d.ts)
#### Interfaces
- **BindInfo**
  - `storeName`: string
  - `tableName`: string
  - `primaryKey`: commonType.ValuesBucket
  - `field`: string
  - `assetName`: string
- **SaveSuccessResponse**
  - `sessionId`: string
  - `version`: number
  - `deviceId`: string
- **RevokeSaveSuccessResponse**
  - `sessionId`: string
- **DistributedObject**
  - `setSessionId`: boolean
- **DataObject**
  - `setSessionId`: void
  - `setSessionId`: void
  - `setSessionId`: Promise<void>
  - `save`: void
  - `save`: Promise<SaveSuccessResponse>
  - `revokeSave`: void
  - `revokeSave`: Promise<RevokeSaveSuccessResponse>
  - `bindAssetStore`: void
  - `bindAssetStore`: Promise<void>
#### Functions
- `createDistributedObject(source: object): DistributedObject`
- `create(context: Context, source: object): DataObject`
- `genSessionId(): string`

### distributedKVStore (@ohos.data.distributedKVStore.d.ts)
#### Interfaces
- **KVManagerConfig**
  - `bundleName`: string
  - `context`: BaseContext
- **Constants**
  - `readonly MAX_KEY_LENGTH`: number
  - `readonly MAX_VALUE_LENGTH`: number
  - `readonly MAX_KEY_LENGTH_DEVICE`: number
  - `readonly MAX_STORE_ID_LENGTH`: number
  - `readonly MAX_QUERY_LENGTH`: number
  - `readonly MAX_BATCH_SIZE`: number
- **Value**
  - `type`: ValueType
  - `value`: Uint8Array | string | number | boolean
- **Entry**
  - `key`: string
  - `value`: Value
- **ChangeNotification**
  - `insertEntries`: Entry[]
  - `updateEntries`: Entry[]
  - `deleteEntries`: Entry[]
  - `deviceId`: string
- **Options**
  - `createIfMissing?`: boolean
  - `encrypt?`: boolean
  - `backup?`: boolean
  - `autoSync?`: boolean
  - `kvStoreType?`: KVStoreType
  - `securityLevel`: SecurityLevel
  - `schema?`: Schema
- **KVStoreResultSet**
  - `getCount`: number
  - `getPosition`: number
  - `moveToFirst`: boolean
  - `moveToLast`: boolean
  - `moveToNext`: boolean
  - `moveToPrevious`: boolean
  - `move`: boolean
  - `moveToPosition`: boolean
  - `isFirst`: boolean
  - `isLast`: boolean
  - `isBeforeFirst`: boolean
  - `isAfterLast`: boolean
  - `getEntry`: Entry
- **SingleKVStore**
  - `put`: void
  - `put`: Promise<void>
  - `putBatch`: void
  - `putBatch`: Promise<void>
  - `putBatch`: void
  - `putBatch`: Promise<void>
  - `delete`: void
  - `delete`: Promise<void>
  - `delete`: Promise<void>
  - `deleteBatch`: void
  - `deleteBatch`: Promise<void>
  - `removeDeviceData`: void
  - `removeDeviceData`: Promise<void>
  - `get`: void
  - `get`: Promise<boolean | string | number | Uint8Array>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getResultSet`: void
  - `getResultSet`: Promise<KVStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KVStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KVStoreResultSet>
  - `closeResultSet`: void
  - `closeResultSet`: Promise<void>
  - `getResultSize`: void
  - `getResultSize`: Promise<number>
  - `backup`: void
  - `backup`: Promise<void>
  - `restore`: void
  - `restore`: Promise<void>
  - `deleteBackup`: void
  - `deleteBackup`: Promise<Array<[string, number]>>
  - `startTransaction`: void
  - `startTransaction`: Promise<void>
  - `commit`: void
  - `commit`: Promise<void>
  - `rollback`: void
  - `rollback`: Promise<void>
  - `enableSync`: void
  - `enableSync`: Promise<void>
  - `setSyncRange`: void
  - `setSyncRange`: Promise<void>
  - `setSyncParam`: void
  - `setSyncParam`: Promise<void>
  - `sync`: void
  - `sync`: void
  - `on`: void
  - `on`: void
  - `off`: void
  - `off`: void
  - `getSecurityLevel`: void
  - `getSecurityLevel`: Promise<SecurityLevel>
- **DeviceKVStore**
  - `get`: void
  - `get`: Promise<boolean | string | number | Uint8Array>
  - `get`: void
  - `get`: Promise<boolean | string | number | Uint8Array>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getEntries`: void
  - `getEntries`: Promise<Entry[]>
  - `getResultSet`: void
  - `getResultSet`: Promise<KVStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KVStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KVStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KVStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KVStoreResultSet>
  - `getResultSet`: void
  - `getResultSet`: Promise<KVStoreResultSet>
  - `getResultSize`: void
  - `getResultSize`: Promise<number>
  - `getResultSize`: void
  - `getResultSize`: Promise<number>
- **KVManager**
  - `closeKVStore`: void
  - `closeKVStore`: Promise<void>
  - `deleteKVStore`: void
  - `deleteKVStore`: Promise<void>
  - `getAllKVStoreId`: void
  - `getAllKVStoreId`: Promise<string[]>
  - `on`: void
  - `off`: void
#### Enums
- **ValueType**
- **SyncMode**
- **SubscribeType**
- **KVStoreType**
- **SecurityLevel**
#### Functions
- `createKVManager(config: KVManagerConfig): KVManager`
#### Classes
- **Schema**
  - `root`: FieldNode
  - `indexes`: Array<string>
  - `mode`: number
  - `skip`: number
- **FieldNode**
  - `appendChild()`: boolean
  - `default`: string
  - `nullable`: boolean
  - `type`: number
- **Query**
  - `reset()`: Query
  - `equalTo()`: Query
  - `notEqualTo()`: Query
  - `greaterThan()`: Query
  - `lessThan()`: Query
  - `greaterThanOrEqualTo()`: Query
  - `lessThanOrEqualTo()`: Query
  - `isNull()`: Query
  - `inNumber()`: Query
  - `inString()`: Query
  - `notInNumber()`: Query
  - `notInString()`: Query
  - `like()`: Query
  - `unlike()`: Query
  - `and()`: Query
  - `or()`: Query
  - `orderByAsc()`: Query
  - `orderByDesc()`: Query
  - `limit()`: Query
  - `isNotNull()`: Query
  - `beginGroup()`: Query
  - `endGroup()`: Query
  - `prefixKey()`: Query
  - `setSuggestIndex()`: Query
  - `deviceId()`: Query
  - `getSqlLike()`: string

### preferences (@ohos.data.preferences.d.ts)
#### Interfaces
- **Options**
  - `name`: string
  - `dataGroupId?`: string | null | undefined
- **Preferences**
  - `get`: void
  - `get`: Promise<ValueType>
  - `getSync`: ValueType
  - `getAll`: void
  - `getAll`: Promise<Object>
  - `getAllSync`: Object
  - `has`: void
  - `has`: Promise<boolean>
  - `hasSync`: boolean
  - `put`: void
  - `put`: Promise<void>
  - `putSync`: void
  - `delete`: void
  - `delete`: Promise<void>
  - `deleteSync`: void
  - `clear`: void
  - `clear`: Promise<void>
  - `clearSync`: void
  - `flush`: void
  - `flush`: Promise<void>
  - `on`: void
  - `on`: void
  - `off`: void
  - `off`: void
#### Functions
- `getPreferences(context: Context, name: string, callback: AsyncCallback<Preferences>): void`
- `getPreferences(context: Context, options: Options, callback: AsyncCallback<Preferences>): void`
- `getPreferences(context: Context, name: string): Promise<Preferences>`
- `getPreferences(context: Context, options: Options): Promise<Preferences>`
- `getPreferencesSync(context: Context, options: Options): Preferences`
- `deletePreferences(context: Context, name: string, callback: AsyncCallback<void>): void`
- `deletePreferences(context: Context, options: Options, callback: AsyncCallback<void>): void`
- `deletePreferences(context: Context, name: string): Promise<void>`
- `deletePreferences(context: Context, options: Options): Promise<void>`
- `removePreferencesFromCache(context: Context, name: string, callback: AsyncCallback<void>): void`
- `removePreferencesFromCache(context: Context, options: Options, callback: AsyncCallback<void>): void`
- `removePreferencesFromCache(context: Context, name: string): Promise<void>`
- `removePreferencesFromCache(context: Context, options: Options): Promise<void>`
- `removePreferencesFromCacheSync(context: Context, name: string): void`
- `removePreferencesFromCacheSync(context: Context, options: Options): void`
#### Type Aliases
- `ValueType` = number | string | boolean | Array<number> | Array<string> | Array<boolean> | Uint8Array

### rdb (@ohos.data.rdb.d.ts)
#### Interfaces
- **RdbStore**
  - `insert`: void
  - `insert`: Promise<number>
  - `batchInsert`: void
  - `batchInsert`: Promise<number>
  - `update`: void
  - `update`: Promise<number>
  - `delete`: void
  - `delete`: Promise<number>
  - `query`: void
  - `query`: Promise<ResultSet>
  - `querySql`: void
  - `querySql`: Promise<ResultSet>
  - `executeSql`: void
  - `executeSql`: Promise<void>
  - `beginTransaction`: void
  - `commit`: void
  - `rollBack`: void
  - `setDistributedTables`: void
  - `setDistributedTables`: Promise<void>
  - `obtainDistributedTableName`: void
  - `obtainDistributedTableName`: Promise<string>
  - `sync`: void
  - `sync`: Promise<Array<[string, number]>>
  - `on`: void
  - `off`: void
- **StoreConfig**
  - `name`: string
#### Enums
- **SyncMode**
  - `SYNC_MODE_PUSH` = 0
  - `SYNC_MODE_PULL` = 1
- **SubscribeType**
  - `SUBSCRIBE_TYPE_REMOTE` = 0
#### Functions
- `getRdbStore(context: Context, config: StoreConfig, version: number, callback: AsyncCallback<RdbStore>): void`
- `getRdbStore(context: Context, config: StoreConfig, version: number): Promise<RdbStore>`
- `deleteRdbStore(context: Context, name: string, callback: AsyncCallback<void>): void`
- `deleteRdbStore(context: Context, name: string): Promise<void>`
#### Classes
- **RdbPredicates**
  - `inDevices()`: RdbPredicates
  - `inAllDevices()`: RdbPredicates
  - `equalTo()`: RdbPredicates
  - `notEqualTo()`: RdbPredicates
  - `beginWrap()`: RdbPredicates
  - `endWrap()`: RdbPredicates
  - `or()`: RdbPredicates
  - `and()`: RdbPredicates
  - `contains()`: RdbPredicates
  - `beginsWith()`: RdbPredicates
  - `endsWith()`: RdbPredicates
  - `isNull()`: RdbPredicates
  - `isNotNull()`: RdbPredicates
  - `like()`: RdbPredicates
  - `glob()`: RdbPredicates
  - `between()`: RdbPredicates
  - `notBetween()`: RdbPredicates
  - `greaterThan()`: RdbPredicates
  - `lessThan()`: RdbPredicates
  - `greaterThanOrEqualTo()`: RdbPredicates
  - `lessThanOrEqualTo()`: RdbPredicates
  - `orderByAsc()`: RdbPredicates
  - `orderByDesc()`: RdbPredicates
  - `distinct()`: RdbPredicates
  - `limitAs()`: RdbPredicates
  - `offsetAs()`: RdbPredicates
  - `groupBy()`: RdbPredicates
  - `indexedBy()`: RdbPredicates
  - `in()`: RdbPredicates
  - `notIn()`: RdbPredicates
#### Type Aliases
- `ValueType` = number | string | boolean
- `ValuesBucket` = { [key: string]: ValueType | Uint8Array | null }
- `ResultSet` = _ResultSet

### relationalStore (@ohos.data.relationalStore.d.ts)
#### Interfaces
- **Asset**
  - `name`: string
  - `uri`: string
  - `path`: string
  - `createTime`: string
  - `modifyTime`: string
  - `size`: string
  - `status?`: AssetStatus
- **StoreConfig**
  - `name`: string
  - `securityLevel`: SecurityLevel
  - `encrypt?`: boolean
  - `dataGroupId?`: string
  - `customDir?`: string
  - `autoCleanDirtyData?`: boolean
  - `isSearchable?`: boolean
- **Statistic**
  - `total`: number
  - `successful`: number
  - `failed`: number
  - `remained`: number
- **TableDetails**
  - `upload`: Statistic
  - `download`: Statistic
- **ProgressDetails**
  - `schedule`: Progress
  - `code`: ProgressCode
  - `details`: Record<string, TableDetails>
- **ChangeInfo**
  - `table`: string
  - `type`: ChangeType
  - `inserted`: Array<string> | Array<number>
  - `updated`: Array<string> | Array<number>
  - `deleted`: Array<string> | Array<number>
- **Reference**
  - `sourceTable`: string
  - `targetTable`: string
- **DistributedConfig**
  - `autoSync`: boolean
  - `references?`: Array<Reference>
- **ResultSet**
  - `columnNames`: Array<string>
  - `columnCount`: number
  - `rowCount`: number
  - `rowIndex`: number
  - `isAtFirstRow`: boolean
  - `isAtLastRow`: boolean
  - `isEnded`: boolean
  - `isStarted`: boolean
  - `isClosed`: boolean
  - `getColumnIndex`: number
  - `getColumnName`: string
  - `goTo`: boolean
  - `goToRow`: boolean
  - `goToFirstRow`: boolean
  - `goToLastRow`: boolean
  - `goToNextRow`: boolean
  - `goToPreviousRow`: boolean
  - `getBlob`: Uint8Array
  - `getString`: string
  - `getLong`: number
  - `getDouble`: number
  - `getAsset`: Asset
  - `getAssets`: Assets
  - `getRow`: ValuesBucket
  - `isColumnNull`: boolean
  - `close`: void
- **RdbStore**
  - `version`: number
  - `insert`: void
  - `insert`: void
  - `insert`: Promise<number>
  - `insert`: Promise<number>
  - `batchInsert`: void
  - `batchInsert`: Promise<number>
  - `update`: void
  - `update`: void
  - `update`: Promise<number>
  - `update`: Promise<number>
  - `update`: void
  - `update`: Promise<number>
  - `delete`: void
  - `delete`: Promise<number>
  - `delete`: void
  - `delete`: Promise<number>
  - `query`: void
  - `query`: void
  - `query`: Promise<ResultSet>
  - `query`: void
  - `query`: void
  - `query`: Promise<ResultSet>
  - `querySql`: void
  - `querySql`: void
  - `querySql`: Promise<ResultSet>
  - `getModifyTime`: Promise<ModifyTime>
  - `getModifyTime`: void
  - `cleanDirtyData`: void
  - `cleanDirtyData`: void
  - `cleanDirtyData`: Promise<void>
  - `querySharingResource`: Promise<ResultSet>
  - `querySharingResource`: void
  - `querySharingResource`: void
  - `executeSql`: void
  - `executeSql`: void
  - `executeSql`: Promise<void>
  - `beginTransaction`: void
  - `commit`: void
  - `rollBack`: void
  - `backup`: void
  - `backup`: Promise<void>
  - `restore`: void
  - `restore`: Promise<void>
  - `setDistributedTables`: void
  - `setDistributedTables`: Promise<void>
  - `setDistributedTables`: void
  - `setDistributedTables`: void
  - `setDistributedTables`: Promise<void>
  - `obtainDistributedTableName`: void
  - `obtainDistributedTableName`: Promise<string>
  - `sync`: void
  - `sync`: Promise<Array<[string, number]>>
  - `cloudSync`: void
  - `cloudSync`: Promise<void>
  - `cloudSync`: void
  - `cloudSync`: Promise<void>
  - `cloudSync`: void
  - `cloudSync`: Promise<void>
  - `remoteQuery`: void
  - `remoteQuery`: Promise<ResultSet>
  - `on`: void
  - `on`: void
  - `on`: void
  - `on`: void
  - `off`: void
  - `off`: void
  - `off`: void
  - `off`: void
  - `emit`: void
#### Enums
- **AssetStatus**
- **Progress**
- **ProgressCode**
- **SecurityLevel**
  - `S1` = 1
  - `S2` = 2
  - `S3` = 3
  - `S4` = 4
- **SyncMode**
  - `SYNC_MODE_PUSH` = 0
  - `SYNC_MODE_PULL` = 1
- **SubscribeType**
  - `SUBSCRIBE_TYPE_REMOTE` = 0
- **ChangeType**
- **DistributedType**
- **ConflictResolution**
  - `ON_CONFLICT_NONE` = 0
  - `ON_CONFLICT_ROLLBACK` = 1
  - `ON_CONFLICT_ABORT` = 2
  - `ON_CONFLICT_FAIL` = 3
  - `ON_CONFLICT_IGNORE` = 4
  - `ON_CONFLICT_REPLACE` = 5
- **Origin**
- **Field**
  - `CURSOR_FIELD` = '#_cursor'
#### Functions
- `getRdbStore(context: Context, config: StoreConfig, callback: AsyncCallback<RdbStore>): void`
- `getRdbStore(context: Context, config: StoreConfig): Promise<RdbStore>`
- `deleteRdbStore(context: Context, name: string, callback: AsyncCallback<void>): void`
- `deleteRdbStore(context: Context, config: StoreConfig, callback: AsyncCallback<void>): void`
- `deleteRdbStore(context: Context, name: string): Promise<void>`
- `deleteRdbStore(context: Context, config: StoreConfig): Promise<void>`
#### Classes
- **RdbPredicates**
  - `inDevices()`: RdbPredicates
  - `inAllDevices()`: RdbPredicates
  - `equalTo()`: RdbPredicates
  - `notEqualTo()`: RdbPredicates
  - `beginWrap()`: RdbPredicates
  - `endWrap()`: RdbPredicates
  - `or()`: RdbPredicates
  - `and()`: RdbPredicates
  - `contains()`: RdbPredicates
  - `beginsWith()`: RdbPredicates
  - `endsWith()`: RdbPredicates
  - `isNull()`: RdbPredicates
  - `isNotNull()`: RdbPredicates
  - `like()`: RdbPredicates
  - `glob()`: RdbPredicates
  - `between()`: RdbPredicates
  - `notBetween()`: RdbPredicates
  - `greaterThan()`: RdbPredicates
  - `lessThan()`: RdbPredicates
  - `greaterThanOrEqualTo()`: RdbPredicates
  - `lessThanOrEqualTo()`: RdbPredicates
  - `orderByAsc()`: RdbPredicates
  - `orderByDesc()`: RdbPredicates
  - `distinct()`: RdbPredicates
  - `limitAs()`: RdbPredicates
  - `offsetAs()`: RdbPredicates
  - `groupBy()`: RdbPredicates
  - `indexedBy()`: RdbPredicates
  - `in()`: RdbPredicates
  - `notIn()`: RdbPredicates
#### Type Aliases
- `Assets` = Asset[]
- `ValueType` = null | number | string | boolean | Uint8Array | Asset | Assets
- `ValuesBucket` = Record<string, ValueType>
- `PRIKeyType` = number | string
- `UTCTime` = Date
- `ModifyTime` = Map<PRIKeyType, UTCTime>

### storage (@ohos.data.storage.d.ts)
#### Interfaces
- **Storage**
  - `getSync`: ValueType
  - `get`: void
  - `get`: Promise<ValueType>
  - `hasSync`: boolean
  - `has`: boolean
  - `has`: Promise<boolean>
  - `putSync`: void
  - `put`: void
  - `put`: Promise<void>
  - `deleteSync`: void
  - `delete`: void
  - `delete`: Promise<void>
  - `clearSync`: void
  - `clear`: void
  - `clear`: Promise<void>
  - `flushSync`: void
  - `flush`: void
  - `flush`: Promise<void>
  - `on`: void
  - `off`: void
- **StorageObserver**
  - `key`: string
#### Functions
- `getStorageSync(path: string): Storage`
- `getStorage(path: string, callback: AsyncCallback<Storage>): void`
- `getStorage(path: string): Promise<Storage>`
- `deleteStorageSync(path: string): void`
- `deleteStorage(path: string, callback: AsyncCallback<void>): void`
- `deleteStorage(path: string): Promise<void>`
- `removeStorageFromCacheSync(path: string): void`
- `removeStorageFromCache(path: string, callback: AsyncCallback<void>): void`
- `removeStorageFromCache(path: string): Promise<void>`
#### Type Aliases
- `ValueType` = number | string | boolean

### unifiedDataChannel (@ohos.data.unifiedDataChannel.d.ts)
#### Enums
- **Intention**
  - `DATA_HUB` = 'DataHub'
#### Functions
- `insertData(options: Options, data: UnifiedData, callback: AsyncCallback<string>): void`
- `insertData(options: Options, data: UnifiedData): Promise<string>`
- `updateData(options: Options, data: UnifiedData, callback: AsyncCallback<void>): void`
- `updateData(options: Options, data: UnifiedData): Promise<void>`
- `queryData(options: Options, callback: AsyncCallback<Array<UnifiedData>>): void`
- `queryData(options: Options): Promise<Array<UnifiedData>>`
- `deleteData(options: Options, callback: AsyncCallback<Array<UnifiedData>>): void`
- `deleteData(options: Options): Promise<Array<UnifiedData>>`
#### Classes
- **UnifiedData**
  - `addRecord()`: void
  - `getRecords()`: Array<UnifiedRecord>
- **Summary**
  - `summary`: Record<string, number>
  - `totalSize`: number
- **UnifiedRecord**
  - `getType()`: string
- **Text**
  - `details?`: Record<string, string>
- **PlainText**
  - `textContent`: string
  - `abstract?`: string
- **Hyperlink**
  - `url`: string
  - `description?`: string
- **HTML**
  - `htmlContent`: string
  - `plainContent?`: string
- **File**
  - `details?`: Record<string, string>
  - `uri`: string
- **Image**
  - `imageUri`: string
- **Video**
  - `videoUri`: string
- **Audio**
  - `audioUri`: string
- **Folder**
  - `folderUri`: string
- **SystemDefinedRecord**
  - `details?`: Record<string, number | string | Uint8Array>
- **SystemDefinedForm**
  - `formId`: number
  - `formName`: string
  - `bundleName`: string
  - `abilityName`: string
  - `module`: string
- **SystemDefinedAppItem**
  - `appId`: string
  - `appName`: string
  - `appIconId`: string
  - `appLabelId`: string
  - `bundleName`: string
  - `abilityName`: string
- **SystemDefinedPixelMap**
  - `rawData`: Uint8Array
- **ApplicationDefinedRecord**
  - `applicationDefinedType`: string
  - `rawData`: Uint8Array
#### Type Aliases
- `Options` = {
    /**
     * Indicates the target Intention
     *
     * @syscap SystemCapability.DistributedDataManager.UDMF.Core
     * @since 10
     */
    /

### uniformTypeDescriptor (@ohos.data.uniformTypeDescriptor.d.ts)
#### Enums
- **UniformDataType**
  - `ENTITY` = 'general.entity'
  - `OBJECT` = 'general.object'
  - `COMPOSITE_OBJECT` = 'general.composite-object'
  - `TEXT` = 'general.text'
  - `PLAIN_TEXT` = 'general.plain-text'
  - `HTML` = 'general.html'
  - `HYPERLINK` = 'general.hyperlink'
  - `XML` = 'general.xml'
  - `SOURCE_CODE` = 'general.source-code'
  - `SCRIPT` = 'general.script'
  - `SHELL_SCRIPT` = 'general.shell-script'
  - `CSH_SCRIPT` = 'general.csh-script'
  - `PERL_SCRIPT` = 'general.perl-script'
  - `PHP_SCRIPT` = 'general.php-script'
  - `PYTHON_SCRIPT` = 'general.python-script'
  - `RUBY_SCRIPT` = 'general.ruby-script'
  - `TYPE_SCRIPT` = 'general.type-script'
  - `JAVA_SCRIPT` = 'general.java-script'
  - `C_HEADER` = 'general.c-header'
  - `C_SOURCE` = 'general.c-source'
  - `C_PLUS_PLUS_HEADER` = 'general.c-plus-plus-header'
  - `C_PLUS_PLUS_SOURCE` = 'general.c-plus-plus-source'
  - `JAVA_SOURCE` = 'general.java-source'
  - `EBOOK` = 'general.ebook'
  - `EPUB` = 'general.epub'
  - `AZW` = 'com.amazon.azw'
  - `AZW3` = 'com.amazon.azw3'
  - `KFX` = 'com.amazon.kfx'
  - `MOBI` = 'com.amazon.mobi'
  - `MEDIA` = 'general.media'
  - `IMAGE` = 'general.image'
  - `JPEG` = 'general.jpeg'
  - `PNG` = 'general.png'
  - `RAW_IMAGE` = 'general.raw-image'
  - `TIFF` = 'general.tiff'
  - `BMP` = 'com.microsoft.bmp'
  - `ICO` = 'com.microsoft.ico'
  - `PHOTOSHOP_IMAGE` = 'com.adobe.photoshop-image'
  - `AI_IMAGE` = 'com.adobe.illustrator.ai-image'
  - `WORD_DOC` = 'com.microsoft.word.doc'
  - `EXCEL` = 'com.microsoft.excel.xls'
  - `PPT` = 'com.microsoft.powerpoint.ppt'
  - `PDF` = 'com.adobe.pdf'
  - `POSTSCRIPT` = 'com.adobe.postscript'
  - `ENCAPSULATED_POSTSCRIPT` = 'com.adobe.encapsulated-postscript'
  - `VIDEO` = 'general.video'
  - `AVI` = 'general.avi'
  - `MPEG` = 'general.mpeg'
  - `MPEG4` = 'general.mpeg-4'
  - `VIDEO_3GPP` = 'general.3gpp'
  - `VIDEO_3GPP2` = 'general.3gpp2'
  - `WINDOWS_MEDIA_WM` = 'com.microsoft.windows-media-wm'
  - `WINDOWS_MEDIA_WMV` = 'com.microsoft.windows-media-wmv'
  - `WINDOWS_MEDIA_WMP` = 'com.microsoft.windows-media-wmp'
  - `AUDIO` = 'general.audio'
  - `AAC` = 'general.aac'
  - `AIFF` = 'general.aiff'
  - `ALAC` = 'general.alac'
  - `FLAC` = 'general.flac'
  - `MP3` = 'general.mp3'
  - `OGG` = 'general.ogg'
  - `PCM` = 'general.pcm'
  - `WINDOWS_MEDIA_WMA` = 'com.microsoft.windows-media-wma'
  - `WAVEFORM_AUDIO` = 'com.microsoft.waveform-audio'
  - `WINDOWS_MEDIA_WMX` = 'com.microsoft.windows-media-wmx'
  - `WINDOWS_MEDIA_WVX` = 'com.microsoft.windows-media-wvx'
  - `WINDOWS_MEDIA_WAX` = 'com.microsoft.windows-media-wax'
  - `FILE` = 'general.file'
  - `DIRECTORY` = 'general.directory'
  - `FOLDER` = 'general.folder'
  - `SYMLINK` = 'general.symlink'
  - `ARCHIVE` = 'general.archive'
  - `BZ2_ARCHIVE` = 'general.bz2-archive'
  - `DISK_IMAGE` = 'general.disk-image'
  - `TAR_ARCHIVE` = 'general.tar-archive'
  - `ZIP_ARCHIVE` = 'general.zip-archive'
  - `JAVA_ARCHIVE` = 'com.sun.java-archive'
  - `GNU_TAR_ARCHIVE` = 'org.gnu.gnu-tar-archive'
  - `GNU_ZIP_ARCHIVE` = 'org.gnu.gnu-zip-archive'
  - `GNU_ZIP_TAR_ARCHIVE` = 'org.gnu.gnu-zip-tar-archive'
  - `CALENDAR` = 'general.calendar'
  - `CONTACT` = 'general.contact'
  - `DATABASE` = 'general.database'
  - `MESSAGE` = 'general.message'
  - `VCARD` = 'general.vcard'
  - `NAVIGATION` = 'general.navigation'
  - `LOCATION` = 'general.location'
  - `OPENHARMONY_FORM` = 'openharmony.form'
  - `OPENHARMONY_APP_ITEM` = 'openharmony.app-item'
  - `OPENHARMONY_PIXEL_MAP` = 'openharmony.pixel-map'
  - `OPENHARMONY_ATOMIC_SERVICE` = 'openharmony.atomic-service'
  - `OPENHARMONY_PACKAGE` = 'openharmony.package'
  - `OPENHARMONY_HAP` = 'openharmony.hap'
#### Functions
- `getTypeDescriptor(typeId: string): TypeDescriptor`
- `getUniformDataTypeByFilenameExtension(filenameExtension: string, belongsTo?: string): string`
- `getUniformDataTypeByMIMEType(mimeType: string, belongsTo?: string): string`
#### Classes
- **TypeDescriptor**
  - `belongsTo()`: boolean
  - `isLowerLevelType()`: boolean
  - `isHigherLevelType()`: boolean
  - `equals()`: boolean
  - `readonly typeId`: string
  - `readonly belongingToTypes`: Array<string>
  - `readonly description`: string
  - `readonly referenceURL`: string
  - `readonly iconFile`: string

### deviceAttest (@ohos.deviceAttest.d.ts)
#### Interfaces
- **AttestResultInfo**
  - `authResult`: number
  - `softwareResult`: number
  - `softwareResultDetail`: Array<number>
  - `ticket`: string
#### Functions
- `getAttestStatus(callback: AsyncCallback<AttestResultInfo>): void`
- `getAttestStatus(): Promise<AttestResultInfo>`
- `getAttestStatusSync(): AttestResultInfo`

### deviceInfo (@ohos.deviceInfo.d.ts)

### dragInteraction (@ohos.deviceStatus.dragInteraction.d.ts)
#### Interfaces
- **Summary**
  - `dataType`: string
  - `dataSize`: number
#### Enums
- **DragState**
  - `MSG_DRAG_STATE_START` = 1
  - `MSG_DRAG_STATE_STOP` = 2
  - `MSG_DRAG_STATE_CANCEL` = 3
#### Functions
- `on(type: 'drag', callback: Callback<DragState>): void`
- `off(type: 'drag', callback?: Callback<DragState>): void`
- `getDataSummary(): Array<Summary>`

### display (@ohos.display.d.ts)
#### Interfaces
- **FoldCreaseRegion**
  - `readonly displayId`: number
  - `readonly creaseRects`: Array<Rect>
- **Rect**
  - `left`: number
  - `top`: number
  - `width`: number
  - `height`: number
- **WaterfallDisplayAreaRects**
  - `readonly left`: Rect
  - `readonly right`: Rect
  - `readonly top`: Rect
  - `readonly bottom`: Rect
- **CutoutInfo**
  - `readonly boundingRects`: Array<Rect>
  - `readonly waterfallDisplayAreaRects`: WaterfallDisplayAreaRects
- **Display**
  - `id`: number
  - `name`: string
  - `alive`: boolean
  - `state`: DisplayState
  - `refreshRate`: number
  - `rotation`: number
  - `width`: number
  - `height`: number
  - `densityDPI`: number
  - `orientation`: Orientation
  - `densityPixels`: number
  - `scaledDensity`: number
  - `xDPI`: number
  - `yDPI`: number
  - `colorSpaces`: Array<colorSpaceManager.ColorSpace>
  - `hdrFormats`: Array<hdrCapability.HDRFormat>
  - `getCutoutInfo`: void
  - `getCutoutInfo`: Promise<CutoutInfo>
  - `hasImmersiveWindow`: void
  - `hasImmersiveWindow`: Promise<boolean>
  - `getAvailableArea`: Promise<Rect>
  - `on`: void
  - `off`: void
#### Enums
- **FoldStatus**
  - `FOLD_STATUS_UNKNOWN` = 0
- **FoldDisplayMode**
  - `FOLD_DISPLAY_MODE_UNKNOWN` = 0
- **DisplayState**
  - `STATE_UNKNOWN` = 0
- **Orientation**
  - `PORTRAIT` = 0
  - `LANDSCAPE` = 1
  - `PORTRAIT_INVERTED` = 2
  - `LANDSCAPE_INVERTED` = 3
#### Functions
- `getDefaultDisplay(callback: AsyncCallback<Display>): void`
- `getDefaultDisplay(): Promise<Display>`
- `getDefaultDisplaySync(): Display`
- `getAllDisplay(callback: AsyncCallback<Array<Display>>): void`
- `getAllDisplay(): Promise<Array<Display>>`
- `getAllDisplays(callback: AsyncCallback<Array<Display>>): void`
- `getAllDisplays(): Promise<Array<Display>>`
- `hasPrivateWindow(displayId: number): boolean`
- `on(type: 'add' | 'remove' | 'change', callback: Callback<number>): void`
- `off(type: 'add' | 'remove' | 'change', callback?: Callback<number>): void`
- `on(type: 'privateModeChange', callback: Callback<boolean>): void`
- `off(type: 'privateModeChange', callback?: Callback<boolean>): void`
- `isFoldable(): boolean`
- `getFoldStatus(): FoldStatus`
- `on(type: 'foldStatusChange', callback: Callback<FoldStatus>): void`
- `off(type: 'foldStatusChange', callback?: Callback<FoldStatus>): void`
- `getFoldDisplayMode(): FoldDisplayMode`
- `setFoldDisplayMode(mode: FoldDisplayMode): void`
- `on(type: 'foldDisplayModeChange', callback: Callback<FoldDisplayMode>): void`
- `off(type: 'foldDisplayModeChange', callback?: Callback<FoldDisplayMode>): void`
- `getCurrentFoldCreaseRegion(): FoldCreaseRegion`
- `setFoldStatusLocked(locked: boolean): void`

### distributedBundle (@ohos.distributedBundle.d.ts)
#### Functions
- `getRemoteAbilityInfo(elementName: ElementName, callback: AsyncCallback<RemoteAbilityInfo>): void`
- `getRemoteAbilityInfo(elementName: ElementName): Promise<RemoteAbilityInfo>`
- `getRemoteAbilityInfos(elementNames: Array<ElementName>,
    callback: AsyncCallback<Array<RemoteAbilityInfo>>): void`
- `getRemoteAbilityInfos(elementNames: Array<ElementName>): Promise<Array<RemoteAbilityInfo>>`

### distributedDeviceManager (@ohos.distributedDeviceManager.d.ts)
#### Interfaces
- **DeviceBasicInfo**
  - `deviceId`: string
  - `deviceName`: string
  - `deviceType`: string
  - `networkId?`: string
- **DeviceManager**
  - `getAvailableDeviceListSync`: Array<DeviceBasicInfo>
  - `getAvailableDeviceList`: void
  - `getAvailableDeviceList`: Promise<Array<DeviceBasicInfo>>
  - `getLocalDeviceNetworkId`: string
  - `getLocalDeviceName`: string
  - `getLocalDeviceType`: number
  - `getLocalDeviceId`: string
  - `getDeviceName`: string
  - `getDeviceType`: number
  - `startDiscovering`: void
  - `stopDiscovering`: void
  - `bindTarget`: void
  - `unbindTarget`: void
  - `replyUiAction`: void
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
#### Enums
- **DeviceStateChange**
  - `UNKNOWN` = 0
  - `AVAILABLE` = 1
  - `UNAVAILABLE` = 2
#### Functions
- `createDeviceManager(bundleName: string): DeviceManager`
- `releaseDeviceManager(deviceManager: DeviceManager): void`

### deviceManager (@ohos.distributedHardware.deviceManager.d.ts)
#### Interfaces
- **DeviceInfo**
  - `deviceId`: string
  - `deviceName`: string
  - `deviceType`: DeviceType
  - `networkId`: string
  - `range`: number
  - `authForm`: AuthForm
- **SubscribeInfo**
  - `subscribeId`: number
  - `mode`: DiscoverMode
  - `medium`: ExchangeMedium
  - `freq`: ExchangeFreq
  - `isSameAccount`: boolean
  - `isWakeRemote`: boolean
  - `capability`: SubscribeCap
- **PublishInfo**
  - `publishId`: number
  - `mode`: DiscoverMode
  - `freq`: ExchangeFreq
  - `ranging`: boolean
- **AuthParam**
  - `authType`: number
  - `extraInfo`: { [key: string]: any }
- **AuthInfo**
  - `authType`: number
  - `token`: number
  - `extraInfo`: { [key: string]: any }
- **DeviceManager**
  - `release`: void
  - `getTrustedDeviceListSync`: Array<DeviceInfo>
  - `getTrustedDeviceListSync`: Array<DeviceInfo>
  - `getTrustedDeviceList`: void
  - `getTrustedDeviceList`: Promise<Array<DeviceInfo>>
  - `getLocalDeviceInfoSync`: DeviceInfo
  - `getLocalDeviceInfo`: void
  - `getLocalDeviceInfo`: Promise<DeviceInfo>
  - `getDeviceInfo`: void
  - `getDeviceInfo`: Promise<DeviceInfo>
  - `startDeviceDiscovery`: void
  - `startDeviceDiscovery`: void
  - `stopDeviceDiscovery`: void
  - `publishDeviceDiscovery`: void
  - `unPublishDeviceDiscovery`: void
  - `authenticateDevice`: void
  - `unAuthenticateDevice`: void
  - `verifyAuthInfo`: void
  - `setUserOperation`: void
  - `requestCredentialRegisterInfo`: void
  - `importCredential`: void
  - `deleteCredential`: void
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
#### Enums
- **AuthForm**
  - `INVALID_TYPE` = -1
  - `PEER_TO_PEER` = 0
  - `IDENTICAL_ACCOUNT` = 1
  - `ACROSS_ACCOUNT` = 2
- **DeviceType**
  - `UNKNOWN_TYPE` = 0
  - `SPEAKER` = 0x0A
  - `PHONE` = 0x0E
  - `TABLET` = 0x11
  - `WEARABLE` = 0x6D
  - `CAR` = 0x83
  - `TV` = 0x9C
- **DeviceStateChangeAction**
  - `ONLINE` = 0
  - `READY` = 1
  - `OFFLINE` = 2
  - `CHANGE` = 3
- **DiscoverMode**
  - `DISCOVER_MODE_PASSIVE` = 0x55
  - `DISCOVER_MODE_ACTIVE` = 0xAA
- **ExchangeMedium**
  - `AUTO` = 0
  - `BLE` = 1
  - `COAP` = 2
  - `USB` = 3
- **ExchangeFreq**
  - `LOW` = 0
  - `MID` = 1
  - `HIGH` = 2
  - `SUPER_HIGH` = 3
- **SubscribeCap**
  - `SUBSCRIBE_CAPABILITY_DDMP` = 0
  - `SUBSCRIBE_CAPABILITY_OSD` = 1
#### Functions
- `createDeviceManager(bundleName: string, callback: AsyncCallback<DeviceManager>): void`

### hardwareManager (@ohos.distributedHardware.hardwareManager.d.ts)
#### Interfaces
- **HardwareDescriptor**
  - `type`: DistributedHardwareType
  - `srcNetworkId?`: string
#### Enums
- **DistributedHardwareType**
  - `ALL` = 0
  - `CAMERA` = 1
  - `SCREEN` = 8
  - `MODEM_MIC` = 256
  - `MODEM_SPEAKER` = 512
  - `MIC` = 1024
  - `SPEAKER` = 2048
- **DistributedHardwareErrorCode**
  - `ERR_CODE_DISTRIBUTED_HARDWARE_NOT_STARTED` = 24200101
  - `ERR_CODE_DEVICE_NOT_CONNECTED` = 24200102
#### Functions
- `pauseDistributedHardware(description: HardwareDescriptor): Promise<void>`
- `resumeDistributedHardware(description: HardwareDescriptor): Promise<void>`
- `stopDistributedHardware(description: HardwareDescriptor): Promise<void>`

### distributedMissionManager (@ohos.distributedMissionManager.d.ts)
#### Interfaces
- **ContinueCallbackInfo**
  - `state`: ContinueState
  - `info`: ContinuableInfo
#### Enums
- **ContinueState**
  - `ACTIVE` = 0
  - `INACTIVE` = 1
#### Functions
- `startSyncRemoteMissions(parameter: MissionParameter, callback: AsyncCallback<void>): void`
- `startSyncRemoteMissions(parameter: MissionParameter): Promise<void>`
- `stopSyncRemoteMissions(parameter: MissionDeviceInfo, callback: AsyncCallback<void>): void`
- `stopSyncRemoteMissions(parameter: MissionDeviceInfo): Promise<void>`
- `registerMissionListener(
    parameter: MissionDeviceInfo,
    options: MissionCallback,
    callback: AsyncCallback<void>
  ): void`
- `registerMissionListener(parameter: MissionDeviceInfo, options: MissionCallback): Promise<void>`
- `unRegisterMissionListener(parameter: MissionDeviceInfo, callback: AsyncCallback<void>): void`
- `unRegisterMissionListener(parameter: MissionDeviceInfo): Promise<void>`
- `on(type: 'continueStateChange', callback: Callback<ContinueCallbackInfo>): void`
- `off(type: 'continueStateChange', callback?: Callback<ContinueCallbackInfo>): void`
- `continueMission(
    parameter: ContinueDeviceInfo,
    options: ContinueCallback,
    callback: AsyncCallback<void>
  ): void`
- `continueMission(parameter: ContinueDeviceInfo, options: ContinueCallback): Promise<void>`
- `continueMission(parameter: ContinueMissionInfo, callback: AsyncCallback<void>): void`
- `continueMission(parameter: ContinueMissionInfo): Promise<void>`
#### Type Aliases
- `ContinuableInfo` = _ContinuableInfo
- `ContinueMissionInfo` = _ContinueMissionInfo
- `ContinueCallback` = _ContinueCallback
- `ContinueDeviceInfo` = _ContinueDeviceInfo
- `MissionCallback` = _MissionCallback
- `MissionDeviceInfo` = _MissionDeviceInfo
- `MissionParameter` = _MissionParameter

### dlpPermission (@ohos.dlpPermission.d.ts)
#### Interfaces
- **DLPPermissionInfo**
  - `dlpFileAccess`: DLPFileAccess
  - `flags`: number
- **AccessedDLPFileInfo**
  - `uri`: string
  - `lastOpenTime`: number
- **RetentionSandboxInfo**
  - `appIndex`: number
  - `bundleName`: string
  - `docUris`: Array<string>
- **DLPManagerResult**
  - `resultCode`: number
  - `want`: Want
- **DLPSandboxInfo**
  - `appIndex`: number
  - `tokenID`: number
- **DLPSandboxState**
  - `bundleName`: string
  - `appIndex`: number
- **AuthUser**
  - `authAccount`: string
  - `authAccountType`: AccountType
  - `dlpFileAccess`: DLPFileAccess
  - `permExpiryTime`: number
- **DLPProperty**
  - `ownerAccount`: string
  - `ownerAccountID`: string
  - `ownerAccountType`: AccountType
  - `authUserList?`: Array<AuthUser>
  - `contactAccount`: string
  - `offlineAccess`: boolean
  - `everyoneAccessList?`: Array<DLPFileAccess>
  - `expireTime?`: number
- **DLPFile**
  - `dlpProperty`: DLPProperty
  - `addDLPLinkFile`: Promise<void>
  - `addDLPLinkFile`: void
  - `stopFuseLink`: Promise<void>
  - `stopFuseLink`: void
  - `resumeFuseLink`: Promise<void>
  - `resumeFuseLink`: void
  - `replaceDLPLinkFile`: Promise<void>
  - `replaceDLPLinkFile`: void
  - `deleteDLPLinkFile`: Promise<void>
  - `deleteDLPLinkFile`: void
  - `recoverDLPFile`: Promise<void>
  - `recoverDLPFile`: void
  - `closeDLPFile`: Promise<void>
  - `closeDLPFile`: void
#### Enums
- **ActionFlagType**
  - `ACTION_VIEW` = 0x00000001
  - `ACTION_SAVE` = 0x00000002
  - `ACTION_SAVE_AS` = 0x00000004
  - `ACTION_EDIT` = 0x00000008
  - `ACTION_SCREEN_CAPTURE` = 0x00000010
  - `ACTION_SCREEN_SHARE` = 0x00000020
  - `ACTION_SCREEN_RECORD` = 0x00000040
  - `ACTION_COPY` = 0x00000080
  - `ACTION_PRINT` = 0x00000100
  - `ACTION_EXPORT` = 0x00000200
  - `ACTION_PERMISSION_CHANGE` = 0x00000400
- **DLPFileAccess**
  - `NO_PERMISSION` = 0
  - `READ_ONLY` = 1
  - `CONTENT_EDIT` = 2
  - `FULL_CONTROL` = 3
- **GatheringPolicyType**
  - `GATHERING` = 1
  - `NON_GATHERING` = 2
- **AccountType**
  - `CLOUD_ACCOUNT` = 1
  - `DOMAIN_ACCOUNT` = 2
#### Functions
- `isDLPFile(fd: number): Promise<boolean>`
- `isDLPFile(fd: number, callback: AsyncCallback<boolean>): void`
- `getDLPPermissionInfo(): Promise<DLPPermissionInfo>`
- `getDLPPermissionInfo(callback: AsyncCallback<DLPPermissionInfo>): void`
- `getOriginalFileName(fileName: string): string`
- `getDLPSuffix(): string`
- `on(type: 'openDLPFile', listener: Callback<AccessedDLPFileInfo>): void`
- `off(type: 'openDLPFile', listener?: Callback<AccessedDLPFileInfo>): void`
- `isInSandbox(): Promise<boolean>`
- `isInSandbox(callback: AsyncCallback<boolean>): void`
- `getDLPSupportedFileTypes(): Promise<Array<string>>`
- `getDLPSupportedFileTypes(callback: AsyncCallback<Array<string>>): void`
- `setRetentionState(docUris: Array<string>): Promise<void>`
- `setRetentionState(docUris: Array<string>, callback: AsyncCallback<void>): void`
- `cancelRetentionState(docUris: Array<string>): Promise<void>`
- `cancelRetentionState(docUris: Array<string>, callback: AsyncCallback<void>): void`
- `getRetentionSandboxList(bundleName?: string): Promise<Array<RetentionSandboxInfo>>`
- `getRetentionSandboxList(bundleName: string, callback: AsyncCallback<Array<RetentionSandboxInfo>>): void`
- `getRetentionSandboxList(callback: AsyncCallback<Array<RetentionSandboxInfo>>): void`
- `getDLPFileAccessRecords(): Promise<Array<AccessedDLPFileInfo>>`
- `getDLPFileAccessRecords(callback: AsyncCallback<Array<AccessedDLPFileInfo>>): void`
- `startDLPManagerForResult(context: common.UIAbilityContext, want: Want): Promise<DLPManagerResult>`
- `getDLPGatheringPolicy(): Promise<GatheringPolicyType>`
- `getDLPGatheringPolicy(callback: AsyncCallback<GatheringPolicyType>): void`
- `installDLPSandbox(
    bundleName: string,
    access: DLPFileAccess,
    userId: number,
    uri: string
  ): Promise<DLPSandboxInfo>`
- `installDLPSandbox(
    bundleName: string,
    access: DLPFileAccess,
    userId: number,
    uri: string,
    callback: AsyncCallback<DLPSandboxInfo>
  ): void`
- `uninstallDLPSandbox(bundleName: string, userId: number, appIndex: number): Promise<void>`
- `uninstallDLPSandbox(
    bundleName: string,
    userId: number,
    appIndex: number,
    callback: AsyncCallback<void>
  ): void`
- `on(type: 'uninstallDLPSandbox', listener: Callback<DLPSandboxState>): void`
- `off(type: 'uninstallDLPSandbox', listener?: Callback<DLPSandboxState>): void`
- `generateDLPFile(plaintextFd: number, ciphertextFd: number, property: DLPProperty): Promise<DLPFile>`
- `generateDLPFile(
    plaintextFd: number,
    ciphertextFd: number,
    property: DLPProperty,
    callback: AsyncCallback<DLPFile>
  ): void`
- `openDLPFile(ciphertextFd: number, appId: string): Promise<DLPFile>`
- `openDLPFile(ciphertextFd: number, appId: string, callback: AsyncCallback<DLPFile>): void`
- `setSandboxAppConfig(configInfo: string): Promise<void>`
- `cleanSandboxAppConfig(): Promise<void>`
- `getSandboxAppConfig(): Promise<string>`

### document (@ohos.document.d.ts)
#### Functions
- `choose(types?: string[]): Promise<string>`
- `choose(callback: AsyncCallback<string>): void`
- `choose(types: string[], callback: AsyncCallback<string>): void`
- `show(uri: string, type: string): Promise<void>`
- `show(uri: string, type: string, callback: AsyncCallback<void>): void`

### deviceManager (@ohos.driver.deviceManager.d.ts)
#### Interfaces
- **Device**
  - `busType`: BusType
  - `deviceId`: number
  - `description`: string
- **USBDevice**
  - `vendorId`: number
  - `productId`: number
- **RemoteDeviceDriver**
  - `deviceId`: number
  - `remote`: rpc.IRemoteObject
#### Enums
- **BusType**
  - `USB` = 1
#### Functions
- `queryDevices(busType?: number): Array<Readonly<Device>>`
- `bindDevice(deviceId: number, onDisconnect: AsyncCallback<number>,
    callback: AsyncCallback<{deviceId: number, remote: rpc.IRemoteObject}>): void`
- `bindDeviceDriver(deviceId: number, onDisconnect: AsyncCallback<number>,
    callback: AsyncCallback<RemoteDeviceDriver>): void`
- `bindDevice(deviceId: number, onDisconnect: AsyncCallback<number>): Promise<{deviceId: number,
    remote: rpc.IRemoteObject}>`
- `bindDeviceDriver(deviceId: number, onDisconnect: AsyncCallback<number>): Promise<RemoteDeviceDriver>`
- `unbindDevice(deviceId: number, callback: AsyncCallback<number>): void`
- `unbindDevice(deviceId: number): Promise<number>`

### effectKit (@ohos.effectKit.d.ts)
#### Interfaces
- **Filter**
  - `blur`: Filter
  - `brightness`: Filter
  - `grayscale`: Filter
  - `getPixelMap`: image.PixelMap
  - `getEffectPixelMap`: Promise<image.PixelMap>
- **ColorPicker**
  - `getMainColor`: Promise<Color>
  - `getMainColorSync`: Color
  - `getLargestProportionColor`: Color
  - `getHighestSaturationColor`: Color
  - `getAverageColor`: Color
  - `isBlackOrWhiteOrGrayColor`: boolean
- **Color**
  - `red`: number
  - `green`: number
  - `blue`: number
  - `alpha`: number
#### Functions
- `createEffect(source: image.PixelMap): Filter`
- `createColorPicker(source: image.PixelMap): Promise<ColorPicker>`
- `createColorPicker(source: image.PixelMap, region: Array<number>): Promise<ColorPicker>`
- `createColorPicker(source: image.PixelMap, callback: AsyncCallback<ColorPicker>): void`
- `createColorPicker(source: image.PixelMap, region: Array<number>, callback: AsyncCallback<ColorPicker>): void`

### @ohos.enterprise.EnterpriseAdminExtensionAbility (@ohos.enterprise.EnterpriseAdminExtensionAbility.d.ts)
#### Classes
- **EnterpriseAdminExtensionAbility**
  - `onAdminEnabled()`: void
  - `onAdminDisabled()`: void
  - `onBundleAdded()`: void
  - `onBundleRemoved()`: void
  - `onAppStart()`: void
  - `onAppStop()`: void
  - `onSystemUpdate()`: void
  - `onStart()`: void

### accountManager (@ohos.enterprise.accountManager.d.ts)
#### Functions
- `disallowAddLocalAccount(admin: Want, disallow: boolean, callback: AsyncCallback<void>): void`
- `disallowAddLocalAccount(admin: Want, disallow: boolean): Promise<void>`
- `disallowAddOsAccountByUser(admin: Want, userId: number, disallow: boolean): void`
- `isAddOsAccountByUserDisallowed(admin: Want, userId: number): boolean`
- `addOsAccount(admin: Want, name: string, type: osAccount.OsAccountType): osAccount.OsAccountInfo`

### adminManager (@ohos.enterprise.adminManager.d.ts)
#### Interfaces
- **EnterpriseInfo**
  - `name`: string
  - `description`: string
#### Enums
- **AdminType**
  - `ADMIN_TYPE_NORMAL` = 0x00
  - `ADMIN_TYPE_SUPER` = 0x01
- **ManagedEvent**
  - `MANAGED_EVENT_BUNDLE_ADDED` = 0
  - `MANAGED_EVENT_BUNDLE_REMOVED` = 1
  - `MANAGED_EVENT_APP_START` = 2
  - `MANAGED_EVENT_APP_STOP` = 3
  - `MANAGED_EVENT_SYSTEM_UPDATE` = 4
#### Functions
- `enableAdmin(admin: Want, enterpriseInfo: EnterpriseInfo, type: AdminType, callback: AsyncCallback<void>): void`
- `enableAdmin(admin: Want, enterpriseInfo: EnterpriseInfo, type: AdminType, userId: number, callback: AsyncCallback<void>): void`
- `enableAdmin(admin: Want, enterpriseInfo: EnterpriseInfo, type: AdminType, userId?: number): Promise<void>`
- `disableAdmin(admin: Want, callback: AsyncCallback<void>): void`
- `disableAdmin(admin: Want, userId: number, callback: AsyncCallback<void>): void`
- `disableAdmin(admin: Want, userId?: number): Promise<void>`
- `disableSuperAdmin(bundleName: String, callback: AsyncCallback<void>): void`
- `disableSuperAdmin(bundleName: String): Promise<void>`
- `isAdminEnabled(admin: Want, callback: AsyncCallback<boolean>): void`
- `isAdminEnabled(admin: Want, userId: number, callback: AsyncCallback<boolean>): void`
- `isAdminEnabled(admin: Want, userId?: number): Promise<boolean>`
- `getEnterpriseInfo(admin: Want, callback: AsyncCallback<EnterpriseInfo>): void`
- `getEnterpriseInfo(admin: Want): Promise<EnterpriseInfo>`
- `setEnterpriseInfo(admin: Want, enterpriseInfo: EnterpriseInfo, callback: AsyncCallback<void>): void`
- `setEnterpriseInfo(admin: Want, enterpriseInfo: EnterpriseInfo): Promise<void>`
- `isSuperAdmin(bundleName: String, callback: AsyncCallback<boolean>): void`
- `isSuperAdmin(bundleName: String): Promise<boolean>`
- `subscribeManagedEvent(admin: Want, managedEvents: Array<ManagedEvent>, callback: AsyncCallback<void>): void`
- `subscribeManagedEvent(admin: Want, managedEvents: Array<ManagedEvent>): Promise<void>`
- `unsubscribeManagedEvent(admin: Want, managedEvents: Array<ManagedEvent>, callback: AsyncCallback<void>): void`
- `unsubscribeManagedEvent(admin: Want, managedEvents: Array<ManagedEvent>): Promise<void>`
- `authorizeAdmin(admin: Want, bundleName: string, callback: AsyncCallback<void>): void`
- `authorizeAdmin(admin: Want, bundleName: string): Promise<void>`

### applicationManager (@ohos.enterprise.applicationManager.d.ts)
#### Functions
- `addDisallowedRunningBundles(admin: Want, appIds: Array<string>, callback: AsyncCallback<void>): void`
- `addDisallowedRunningBundles(admin: Want, appIds: Array<string>, userId: number, callback: AsyncCallback<void>): void`
- `addDisallowedRunningBundles(admin: Want, appIds: Array<string>, userId?: number): Promise<void>`
- `removeDisallowedRunningBundles(admin: Want, appIds: Array<string>, callback: AsyncCallback<void>): void`
- `removeDisallowedRunningBundles(admin: Want, appIds: Array<string>, userId: number, callback: AsyncCallback<void>): void`
- `removeDisallowedRunningBundles(admin: Want, appIds: Array<string>, userId?: number): Promise<void>`
- `getDisallowedRunningBundles(admin: Want, callback: AsyncCallback<Array<string>>): void`
- `getDisallowedRunningBundles(admin: Want, userId: number, callback: AsyncCallback<Array<string>>): void`
- `getDisallowedRunningBundles(admin: Want, userId?: number): Promise<Array<string>>`
- `addAutoStartApps(admin: Want, autoStartApps: Array<Want>): void`
- `removeAutoStartApps(admin: Want, autoStartApps: Array<Want>): void`
- `getAutoStartApps(admin: Want): Array<Want>`

### bluetoothManager (@ohos.enterprise.bluetoothManager.d.ts)
#### Interfaces
- **BluetoothInfo**
  - `name`: string
  - `state`: access.BluetoothState
  - `connectionState`: constant.ProfileConnectionState
#### Functions
- `getBluetoothInfo(admin: Want): BluetoothInfo`
- `setBluetoothDisabled(admin: Want, disabled: boolean): void`
- `isBluetoothDisabled(admin: Want): boolean`

### browser (@ohos.enterprise.browser.d.ts)
#### Functions
- `setPolicies(admin: Want, appId: string, policies: string, callback: AsyncCallback<void>): void`
- `setPolicies(admin: Want, appId: string, policies: string): Promise<void>`
- `getPolicies(admin: Want, appId: string, callback: AsyncCallback<string>): void`
- `getPolicies(admin: Want, appId: string): Promise<string>`

### bundleManager (@ohos.enterprise.bundleManager.d.ts)
#### Interfaces
- **InstallParam**
  - `userId?`: number
  - `installFlag?`: number
#### Functions
- `addAllowedInstallBundles(admin: Want, appIds: Array<string>, callback: AsyncCallback<void>): void`
- `addAllowedInstallBundles(admin: Want, appIds: Array<string>, userId: number, callback: AsyncCallback<void>): void`
- `addAllowedInstallBundles(admin: Want, appIds: Array<string>, userId?: number): Promise<void>`
- `removeAllowedInstallBundles(admin: Want, appIds: Array<string>, callback: AsyncCallback<void>): void`
- `removeAllowedInstallBundles(admin: Want, appIds: Array<string>, userId: number, callback: AsyncCallback<void>): void`
- `removeAllowedInstallBundles(admin: Want, appIds: Array<string>, userId?: number): Promise<void>`
- `getAllowedInstallBundles(admin: Want, callback: AsyncCallback<Array<string>>): void`
- `getAllowedInstallBundles(admin: Want, userId: number, callback: AsyncCallback<Array<string>>): void`
- `getAllowedInstallBundles(admin: Want, userId?: number): Promise<Array<string>>`
- `addDisallowedInstallBundles(admin: Want, appIds: Array<string>, callback: AsyncCallback<void>): void`
- `addDisallowedInstallBundles(admin: Want, appIds: Array<string>, userId: number, callback: AsyncCallback<void>): void`
- `addDisallowedInstallBundles(admin: Want, appIds: Array<string>, userId?: number): Promise<void>`
- `removeDisallowedInstallBundles(admin: Want, appIds: Array<string>, callback: AsyncCallback<void>): void`
- `removeDisallowedInstallBundles(admin: Want, appIds: Array<string>, userId: number, callback: AsyncCallback<void>): void`
- `removeDisallowedInstallBundles(admin: Want, appIds: Array<string>, userId?: number): Promise<void>`
- `getDisallowedInstallBundles(admin: Want, callback: AsyncCallback<Array<string>>): void`
- `getDisallowedInstallBundles(admin: Want, userId: number, callback: AsyncCallback<Array<string>>): void`
- `getDisallowedInstallBundles(admin: Want, userId?: number): Promise<Array<string>>`
- `addDisallowedUninstallBundles(admin: Want, appIds: Array<string>, callback: AsyncCallback<void>): void`
- `addDisallowedUninstallBundles(admin: Want, appIds: Array<string>, userId: number, callback: AsyncCallback<void>): void`
- `addDisallowedUninstallBundles(admin: Want, appIds: Array<string>, userId?: number): Promise<void>`
- `removeDisallowedUninstallBundles(admin: Want, appIds: Array<string>, callback: AsyncCallback<void>): void`
- `removeDisallowedUninstallBundles(admin: Want, appIds: Array<string>, userId: number, callback: AsyncCallback<void>): void`
- `removeDisallowedUninstallBundles(admin: Want, appIds: Array<string>, userId?: number): Promise<void>`
- `getDisallowedUninstallBundles(admin: Want, callback: AsyncCallback<Array<string>>): void`
- `getDisallowedUninstallBundles(admin: Want, userId: number, callback: AsyncCallback<Array<string>>): void`
- `getDisallowedUninstallBundles(admin: Want, userId?: number): Promise<Array<string>>`
- `uninstall(admin: Want, bundleName: string, callback: AsyncCallback<void>): void`
- `uninstall(admin: Want, bundleName: string, userId: number, callback: AsyncCallback<void>): void`
- `uninstall(admin: Want, bundleName: string, isKeepData: boolean, callback: AsyncCallback<void>): void`
- `uninstall(admin: Want, bundleName: string, userId: number, isKeepData: boolean, callback: AsyncCallback<void>): void`
- `uninstall(admin: Want, bundleName: string, userId?: number, isKeepData?: boolean): Promise<void>`
- `install(admin: Want, hapFilePaths: Array<string>, callback: AsyncCallback<void>): void`
- `install(admin: Want, hapFilePaths: Array<string>, installParam: InstallParam, callback: AsyncCallback<void>): void`
- `install(admin: Want, hapFilePaths: Array<string>, installParam?: InstallParam): Promise<void>`

### dateTimeManager (@ohos.enterprise.dateTimeManager.d.ts)
#### Functions
- `setDateTime(admin: Want, time: number, callback: AsyncCallback<void>): void`
- `setDateTime(admin: Want, time: number): Promise<void>`
- `disallowModifyDateTime(admin: Want, disallow: boolean, callback: AsyncCallback<void>): void`
- `disallowModifyDateTime(admin: Want, disallow: boolean): Promise<void>`
- `isModifyDateTimeDisallowed(admin: Want, callback: AsyncCallback<boolean>): void`
- `isModifyDateTimeDisallowed(admin: Want): Promise<boolean>`

### deviceControl (@ohos.enterprise.deviceControl.d.ts)
#### Functions
- `resetFactory(admin: Want, callback: AsyncCallback<void>): void`
- `resetFactory(admin: Want): Promise<void>`
- `shutdown(admin: Want): void`
- `reboot(admin: Want): void`
- `lockScreen(admin: Want): void`

### deviceInfo (@ohos.enterprise.deviceInfo.d.ts)
#### Functions
- `getDeviceSerial(admin: Want, callback: AsyncCallback<string>): void`
- `getDeviceSerial(admin: Want): Promise<string>`
- `getDisplayVersion(admin: Want, callback: AsyncCallback<string>): void`
- `getDisplayVersion(admin: Want): Promise<string>`
- `getDeviceName(admin: Want, callback: AsyncCallback<string>): void`
- `getDeviceName(admin: Want): Promise<string>`

### deviceSettings (@ohos.enterprise.deviceSettings.d.ts)
#### Interfaces
- **PowerPolicy**
  - `powerPolicyAction`: PowerPolicyAction
  - `delayTime`: number
- **CertBlob**
  - `inData`: Uint8Array
  - `alias`: string
#### Enums
- **PowerPolicyAction**
  - `NONE` = 0
- **PowerScene**
  - `TIME_OUT` = 0
#### Functions
- `setScreenOffTime(admin: Want, time: number): void`
- `getScreenOffTime(admin: Want, callback: AsyncCallback<number>): void`
- `getScreenOffTime(admin: Want): Promise<number>`
- `installUserCertificate(admin: Want, certificate: CertBlob, callback: AsyncCallback<string>): void`
- `installUserCertificate(admin: Want, certificate: CertBlob): Promise<string>`
- `uninstallUserCertificate(admin: Want, certUri: string, callback: AsyncCallback<void>): void`
- `uninstallUserCertificate(admin: Want, certUri: string): Promise<void>`
- `setPowerPolicy(admin: Want, powerScene: PowerScene, powerPolicy: PowerPolicy): void`
- `getPowerPolicy(admin: Want, powerScene: PowerScene): PowerPolicy`

### locationManager (@ohos.enterprise.locationManager.d.ts)
#### Enums
- **LocationPolicy**
  - `DEFAULT_LOCATION_SERVICE` = 0
  - `DISALLOW_LOCATION_SERVICE` = 1
  - `FORCE_OPEN_LOCATION_SERVICE` = 2
#### Functions
- `setLocationPolicy(admin: Want, policy: LocationPolicy): void`
- `getLocationPolicy(admin: Want): LocationPolicy`

### networkManager (@ohos.enterprise.networkManager.d.ts)
#### Interfaces
- **AddFilterRule**
  - `ruleNo?`: number
  - `srcAddr?`: string
  - `destAddr?`: string
  - `srcPort?`: string
  - `destPort?`: string
  - `uid?`: string
  - `method`: AddMethod
  - `direction`: Direction
  - `action`: Action
  - `protocol?`: Protocol
- **RemoveFilterRule**
  - `srcAddr?`: string
  - `destAddr?`: string
  - `srcPort?`: string
  - `destPort?`: string
  - `uid?`: string
  - `direction`: Direction
  - `action?`: Action
  - `protocol?`: Protocol
- **FirewallRule**
  - `srcAddr?`: string
  - `destAddr?`: string
  - `srcPort?`: string
  - `destPort?`: string
  - `appUid?`: string
  - `direction?`: Direction
  - `action?`: Action
  - `protocol?`: Protocol
- **DomainFilterRule**
  - `domainName?`: string
  - `appUid?`: string
  - `action?`: Action
#### Enums
- **AddMethod**
  - `APPEND` = 0
  - `INSERT` = 1
- **Direction**
  - `INPUT` = 0
  - `OUTPUT` = 1
- **Action**
  - `ALLOW` = 0
  - `DENY` = 1
- **Protocol**
  - `ALL` = 0
  - `TCP` = 1
  - `UDP` = 2
  - `ICMP` = 3
#### Functions
- `getAllNetworkInterfaces(admin: Want, callback: AsyncCallback<Array<string>>): void`
- `getAllNetworkInterfaces(admin: Want): Promise<Array<string>>`
- `getIpAddress(admin: Want, networkInterface: string, callback: AsyncCallback<string>): void`
- `getIpAddress(admin: Want, networkInterface: string): Promise<string>`
- `getMac(admin: Want, networkInterface: string, callback: AsyncCallback<string>): void`
- `getMac(admin: Want, networkInterface: string): Promise<string>`
- `isNetworkInterfaceDisabled(admin: Want, networkInterface: string, callback: AsyncCallback<boolean>): void`
- `isNetworkInterfaceDisabled(admin: Want, networkInterface: string): Promise<boolean>`
- `setNetworkInterfaceDisabled(admin: Want, networkInterface: string, isDisabled: boolean, callback: AsyncCallback<void>): void`
- `setNetworkInterfaceDisabled(admin: Want, networkInterface: string, isDisabled: boolean): Promise<void>`
- `setGlobalProxy(admin: Want, httpProxy: connection.HttpProxy, callback: AsyncCallback<void>): void`
- `setGlobalProxy(admin: Want, httpProxy: connection.HttpProxy): Promise<void>`
- `getGlobalProxy(admin: Want, callback: AsyncCallback<connection.HttpProxy>): void`
- `getGlobalProxy(admin: Want): Promise<connection.HttpProxy>`
- `addIptablesFilterRule(admin: Want, filterRule: AddFilterRule, callback: AsyncCallback<void>): void`
- `addIptablesFilterRule(admin: Want, filterRule: AddFilterRule): Promise<void>`
- `removeIptablesFilterRule(admin: Want, filterRule: RemoveFilterRule, callback: AsyncCallback<void>): void`
- `removeIptablesFilterRule(admin: Want, filterRule: RemoveFilterRule): Promise<void>`
- `listIptablesFilterRules(admin: Want, callback: AsyncCallback<string>): void`
- `listIptablesFilterRules(admin: Want): Promise<string>`
- `addFirewallRule(admin: Want, firewallRule: FirewallRule): void`
- `removeFirewallRule(admin: Want, firewallRule?: FirewallRule): void`
- `getFirewallRules(admin: Want): Array<FirewallRule>`
- `addDomainFilterRule(admin: Want, domainFilterRule: DomainFilterRule): void`
- `removeDomainFilterRule(admin: Want, domainFilterRule?: DomainFilterRule): void`
- `getDomainFilterRules(admin: Want): Array<DomainFilterRule>`

### restrictions (@ohos.enterprise.restrictions.d.ts)
#### Functions
- `setPrinterDisabled(admin: Want, disabled: boolean, callback: AsyncCallback<void>): void`
- `setPrinterDisabled(admin: Want, disabled: boolean): Promise<void>`
- `isPrinterDisabled(admin: Want, callback: AsyncCallback<boolean>): void`
- `isPrinterDisabled(admin: Want): Promise<boolean>`
- `setHdcDisabled(admin: Want, disabled: boolean, callback: AsyncCallback<void>): void`
- `setHdcDisabled(admin: Want, disabled: boolean): Promise<void>`
- `isHdcDisabled(admin: Want, callback: AsyncCallback<boolean>): void`
- `isHdcDisabled(admin: Want): Promise<boolean>`
- `disableMicrophone(admin: Want, disable: boolean): void`
- `isMicrophoneDisabled(admin: Want): boolean`
- `setFingerprintAuthDisabled(admin: Want, disabled: boolean): void`
- `isFingerprintAuthDisabled(admin: Want): boolean`

### securityManager (@ohos.enterprise.securityManager.d.ts)
#### Interfaces
- **DeviceEncryptionStatus**
  - `isEncrypted`: boolean
#### Functions
- `getSecurityPatchTag(admin: Want): string`
- `getDeviceEncryptionStatus(admin: Want): DeviceEncryptionStatus`

### systemManager (@ohos.enterprise.systemManager.d.ts)
#### Interfaces
- **SystemUpdateInfo**
  - `versionName`: string
  - `firstReceivedTime`: number
  - `packageType`: string
#### Functions
- `setNTPServer(admin: Want, server: string): void`
- `getNTPServer(admin: Want): string`

### usbManager (@ohos.enterprise.usbManager.d.ts)
#### Interfaces
- **UsbDeviceId**
  - `vendorId`: number
  - `productId`: number
#### Enums
- **UsbPolicy**
  - `READ_WRITE` = 0
  - `READ_ONLY` = 1
  - `DISABLED` = 2
#### Functions
- `setUsbPolicy(admin: Want, usbPolicy: UsbPolicy, callback: AsyncCallback<void>): void`
- `setUsbPolicy(admin: Want, usbPolicy: UsbPolicy): Promise<void>`
- `disableUsb(admin: Want, disable: boolean): void`
- `isUsbDisabled(admin: Want): boolean`
- `addAllowedUsbDevices(admin: Want, usbDeviceIds: Array<UsbDeviceId>): void`
- `removeAllowedUsbDevices(admin: Want, usbDeviceIds: Array<UsbDeviceId>): void`
- `getAllowedUsbDevices(admin: Want): Array<UsbDeviceId>`
- `setUsbStorageDeviceAccessPolicy(admin: Want, usbPolicy: UsbPolicy): void`
- `getUsbStorageDeviceAccessPolicy(admin: Want): UsbPolicy`

### wifiManager (@ohos.enterprise.wifiManager.d.ts)
#### Interfaces
- **IpProfile**
  - `ipAddress`: number
  - `gateway`: number
  - `prefixLength`: number
  - `dnsServers`: number[]
  - `domains`: Array<string>
- **WifiEapProfile**
  - `eapMethod`: EapMethod
  - `phase2Method`: Phase2Method
  - `identity`: string
  - `anonymousIdentity`: string
  - `password`: string
  - `caCertAliases`: string
  - `caPath`: string
  - `clientCertAliases`: string
  - `certEntry`: Uint8Array
  - `certPassword`: string
  - `altSubjectMatch`: string
  - `domainSuffixMatch`: string
  - `realm`: string
  - `plmn`: string
  - `eapSubId`: number
- **WifiProfile**
  - `ssid`: string
  - `bssid?`: string
  - `preSharedKey`: string
  - `isHiddenSsid?`: boolean
  - `securityType`: WifiSecurityType
  - `creatorUid?`: number
  - `disableReason?`: number
  - `netId?`: number
  - `randomMacType?`: number
  - `randomMacAddr?`: string
  - `ipType?`: IpType
  - `staticIp?`: IpProfile
  - `eapProfile?`: WifiEapProfile
#### Enums
- **WifiSecurityType**
  - `WIFI_SEC_TYPE_INVALID` = 0
  - `WIFI_SEC_TYPE_OPEN` = 1
  - `WIFI_SEC_TYPE_WEP` = 2
  - `WIFI_SEC_TYPE_PSK` = 3
  - `WIFI_SEC_TYPE_SAE` = 4
  - `WIFI_SEC_TYPE_EAP` = 5
  - `WIFI_SEC_TYPE_EAP_SUITE_B` = 6
  - `WIFI_SEC_TYPE_OWE` = 7
  - `WIFI_SEC_TYPE_WAPI_CERT` = 8
  - `WIFI_SEC_TYPE_WAPI_PSK` = 9
- **IpType**
- **EapMethod**
- **Phase2Method**
#### Functions
- `isWifiActive(admin: Want, callback: AsyncCallback<boolean>): void`
- `isWifiActive(admin: Want): Promise<boolean>`
- `setWifiProfile(admin: Want, profile: WifiProfile, callback: AsyncCallback<void>): void`
- `setWifiProfile(admin: Want, profile: WifiProfile): Promise<void>`
- `setWifiDisabled(admin: Want, disabled: boolean): void`
- `isWifiDisabled(admin: Want): boolean`

### emitter (@ohos.events.emitter.d.ts)
#### Interfaces
- **EventData**
  - `data?`: { [key: string]: any }
- **InnerEvent**
  - `eventId`: number
  - `priority?`: EventPriority
- **Options**
  - `priority?`: EventPriority
#### Enums
- **EventPriority**
  - `IMMEDIATE` = 0
#### Functions
- `on(event: InnerEvent, callback: Callback<EventData>): void`
- `on(eventId: string, callback: Callback<EventData>): void`
- `once(event: InnerEvent, callback: Callback<EventData>): void`
- `once(eventId: string, callback: Callback<EventData>): void`
- `off(eventId: number): void`
- `off(eventId: string): void`
- `off(eventId: number, callback: Callback<EventData>): void`
- `off(eventId: string, callback: Callback<EventData>): void`
- `emit(event: InnerEvent, data?: EventData): void`
- `emit(eventId: string, data?: EventData): void`
- `emit(eventId: string, options: Options, data?: EventData): void`
- `getListenerCount(eventId: number | string): number`

### FaultLogger (@ohos.faultLogger.d.ts)
#### Interfaces
- **FaultLogInfo**
  - `pid`: number
  - `uid`: number
  - `type`: FaultType
  - `timestamp`: number
  - `reason`: string
  - `module`: string
  - `summary`: string
  - `fullLog`: string
#### Enums
- **FaultType**
  - `NO_SPECIFIC` = 0
  - `CPP_CRASH` = 2
  - `JS_CRASH` = 3
  - `APP_FREEZE` = 4
#### Functions
- `querySelfFaultLog(faultType: FaultType, callback: AsyncCallback<Array<FaultLogInfo>>): void`
- `querySelfFaultLog(faultType: FaultType): Promise<Array<FaultLogInfo>>`
- `query(faultType: FaultType, callback: AsyncCallback<Array<FaultLogInfo>>): void`
- `query(faultType: FaultType): Promise<Array<FaultLogInfo>>`

### backup (@ohos.file.backup.d.ts)
#### Interfaces
- **FileMeta**
  - `bundleName`: string
  - `uri`: string
- **FileData**
  - `fd`: number
- **File**
- **GeneralCallbacks**
  - `onFileReady`: AsyncCallback<File>
  - `onBundleBegin`: AsyncCallback<string>
  - `onBundleEnd`: AsyncCallback<string>
  - `onAllBundlesEnd`: AsyncCallback<undefined>
  - `onBackupServiceDied`: Callback<undefined>
#### Functions
- `getLocalCapabilities(): Promise<FileData>`
- `getLocalCapabilities(callback: AsyncCallback<FileData>): void`
#### Classes
- **SessionBackup**
  - `appendBundles()`: Promise<void>
  - `appendBundles()`: void
- **SessionRestore**
  - `appendBundles()`: Promise<void>
  - `appendBundles()`: void
  - `publishFile()`: Promise<void>
  - `publishFile()`: void
  - `getFileHandle()`: Promise<void>
  - `getFileHandle()`: void

### cloudSync (@ohos.file.cloudSync.d.ts)
#### Interfaces
- **SyncProgress**
  - `state`: SyncState
  - `error`: ErrorType
- **DownloadProgress**
  - `state`: State
  - `processed`: number
  - `size`: number
  - `uri`: string
  - `error`: DownloadErrorType
#### Enums
- **SyncState**
- **ErrorType**
- **State**
- **DownloadErrorType**
- **FileSyncState**
#### Functions
- `getFileSyncState(uri: Array<string>): Promise<Array<FileSyncState>>`
- `getFileSyncState(uri: Array<string>, callback: AsyncCallback<Array<FileSyncState>>): void`
#### Classes
- **GallerySync**
  - `off()`: void
  - `start()`: Promise<void>
  - `start()`: void
  - `stop()`: Promise<void>
  - `stop()`: void
- **Download**
  - `off()`: void
  - `start()`: Promise<void>
  - `start()`: void
  - `stop()`: Promise<void>
  - `stop()`: void
- **FileSync**
  - `on()`: void
  - `off()`: void
  - `start()`: Promise<void>
  - `start()`: void
  - `stop()`: Promise<void>
  - `stop()`: void
  - `getLastSyncTime()`: Promise<number>
  - `getLastSyncTime()`: void
- **CloudFileCache**
  - `on()`: void
  - `off()`: void
  - `start()`: Promise<void>
  - `start()`: void
  - `stop()`: Promise<void>
  - `stop()`: void
  - `cleanCache()`: void

### cloudSyncManager (@ohos.file.cloudSyncManager.d.ts)
#### Interfaces
- **ExtraData**
  - `eventId`: string
  - `extraData`: string
#### Enums
- **Action**
#### Functions
- `changeAppCloudSwitch(accountId: string, bundleName: string, status: boolean): Promise<void>`
- `changeAppCloudSwitch(
    accountId: string,
    bundleName: string,
    status: boolean,
    callback: AsyncCallback<void>
  ): void`
- `notifyDataChange(accountId: string, bundleName: string): Promise<void>`
- `notifyDataChange(accountId: string, bundleName: string, callback: AsyncCallback<void>): void`
- `enableCloud(accountId: string, switches: Record<string, boolean>): Promise<void>`
- `enableCloud(accountId: string, switches: Record<string, boolean>, callback: AsyncCallback<void>): void`
- `disableCloud(accountId: string): Promise<void>`
- `disableCloud(accountId: string, callback: AsyncCallback<void>): void`
- `clean(accountId: string, appActions: Record<string, Action>): Promise<void>`
- `clean(accountId: string, appActions: Record<string, Action>, callback: AsyncCallback<void>): void`
- `notifyDataChange(userId: number, extraData: ExtraData): Promise<void>`
- `notifyDataChange(userId: number, extraData: ExtraData, callback: AsyncCallback<void>): void`

### Environment (@ohos.file.environment.d.ts)
#### Functions
- `getStorageDataDir(): Promise<string>`
- `getStorageDataDir(callback: AsyncCallback<string>): void`
- `getUserDataDir(): Promise<string>`
- `getUserDataDir(callback: AsyncCallback<string>): void`
- `getUserDownloadDir(): string`
- `getUserDesktopDir(): string`
- `getUserDocumentDir(): string`
- `getExternalStorageDir(): string`
- `getUserHomeDir(): string`

### fileAccess (@ohos.file.fileAccess.d.ts)
#### Interfaces
- **FileInfo**
  - `uri`: string
  - `relativePath`: string
  - `fileName`: string
  - `mode`: number
  - `size`: number
  - `mtime`: number
  - `mimeType`: string
  - `listFile`: FileIterator
  - `scanFile`: FileIterator
- **FileIterator**
  - `next`: { value: FileInfo, done: boolean }
- **RootInfo**
  - `deviceType`: number
  - `uri`: string
  - `relativePath`: string
  - `displayName`: string
  - `deviceFlags`: number
  - `listFile`: FileIterator
  - `scanFile`: FileIterator
- **RootIterator**
  - `next`: { value: RootInfo, done: boolean }
- **CopyResult**
  - `sourceUri`: string
  - `destUri`: string
  - `errCode`: number
  - `errMsg`: string
- **NotifyMessage**
  - `type`: NotifyType
  - `uris`: Array<string>
- **MoveResult**
  - `sourceUri`: string
  - `destUri`: string
  - `errCode`: number
  - `errMsg`: string
- **FileAccessHelper**
  - `openFile`: Promise<number>
  - `openFile`: void
  - `createFile`: Promise<string>
  - `createFile`: void
  - `mkDir`: Promise<string>
  - `mkDir`: void
  - `delete`: Promise<number>
  - `delete`: void
  - `move`: Promise<string>
  - `move`: void
  - `copy`: Promise<Array<CopyResult>>
  - `copy`: void
  - `copy`: void
  - `copyFile`: Promise<string>
  - `copyFile`: void
  - `rename`: Promise<string>
  - `rename`: void
  - `access`: Promise<boolean>
  - `access`: void
  - `query`: Promise<string>
  - `query`: void
  - `getFileInfoFromUri`: Promise<FileInfo>
  - `getFileInfoFromUri`: void
  - `getFileInfoFromRelativePath`: Promise<FileInfo>
  - `getFileInfoFromRelativePath`: void
  - `getRoots`: Promise<RootIterator>
  - `getRoots`: void
  - `registerObserver`: void
  - `unregisterObserver`: void
  - `moveItem`: Promise<Array<MoveResult>>
  - `moveItem`: void
  - `moveItem`: void
  - `moveFile`: Promise<string>
  - `moveFile`: void
#### Enums
- **OPENFLAGS**
  - `READ` = 0o0
  - `WRITE` = 0o1
  - `WRITE_READ` = 0o2
- **FileKey**
  - `DISPLAY_NAME` = 'display_name'
  - `DATE_ADDED` = 'date_added'
  - `DATE_MODIFIED` = 'date_modified'
  - `RELATIVE_PATH` = 'relative_path'
  - `FILE_SIZE` = 'size'
- **NotifyType**
#### Functions
- `getFileAccessAbilityInfo(callback: AsyncCallback<Array<Want>>): void`
- `getFileAccessAbilityInfo(): Promise<Array<Want>>`
- `createFileAccessHelper(context: Context): FileAccessHelper`
- `createFileAccessHelper(context: Context, wants: Array<Want>): FileAccessHelper`

### fileExtensionInfo (@ohos.file.fileExtensionInfo.d.ts)
#### Enums
- **DeviceType**
  - `DEVICE_LOCAL_DISK` = 1

### fileUri (@ohos.file.fileuri.d.ts)
#### Functions
- `getUriFromPath(path: string): string`

### fileIo (@ohos.file.fs.d.ts)
#### Interfaces
- **Progress**
  - `readonly processedSize`: number
  - `readonly totalSize`: number
- **CopyOptions**
  - `progressListener?`: ProgressListener
- **File**
  - `readonly fd`: number
  - `readonly path`: string
  - `readonly name`: string
  - `getParent`: string
  - `lock`: Promise<void>
  - `lock`: void
  - `lock`: void
  - `tryLock`: void
  - `unlock`: void
- **RandomAccessFile**
  - `readonly fd`: number
  - `readonly filePointer`: number
  - `setFilePointer`: void
  - `close`: void
  - `write`: Promise<number>
  - `write`: void
  - `write`: void
  - `writeSync`: number
  - `read`: Promise<number>
  - `read`: void
  - `read`: void
  - `readSync`: number
- **Stat**
  - `readonly ino`: bigint
  - `readonly mode`: number
  - `readonly uid`: number
  - `readonly gid`: number
  - `readonly size`: number
  - `readonly atime`: number
  - `readonly mtime`: number
  - `readonly ctime`: number
  - `readonly location`: LocationType
  - `isBlockDevice`: boolean
  - `isCharacterDevice`: boolean
  - `isDirectory`: boolean
  - `isFIFO`: boolean
  - `isFile`: boolean
  - `isSocket`: boolean
  - `isSymbolicLink`: boolean
- **Stream**
  - `close`: Promise<void>
  - `close`: void
  - `closeSync`: void
  - `flush`: Promise<void>
  - `flush`: void
  - `flushSync`: void
  - `write`: Promise<number>
  - `write`: void
  - `write`: void
  - `writeSync`: number
  - `read`: Promise<number>
  - `read`: void
  - `read`: void
  - `readSync`: number
- **WatchEventListener**
- **WatchEvent**
  - `readonly fileName`: string
  - `readonly event`: number
  - `readonly cookie`: number
- **Watcher**
  - `start`: void
  - `stop`: void
- **ReaderIteratorResult**
  - `done`: boolean
  - `value`: string
- **ReaderIterator**
  - `next`: ReaderIteratorResult
- **Filter**
  - `suffix?`: Array<string>
  - `displayName?`: Array<string>
  - `mimeType?`: Array<string>
  - `fileSizeOver?`: number
  - `lastModifiedAfter?`: number
  - `excludeMedia?`: boolean
- **ConflictFiles**
  - `srcFile`: string
  - `destFile`: string
- **Options**
  - `encoding?`: string
- **ReadOptions**
  - `offset?`: number
  - `length?`: number
- **ReadTextOptions**
  - `encoding?`: string
- **WriteOptions**
  - `offset?`: number
  - `length?`: number
- **ListFileOptions**
  - `recursion?`: boolean
  - `listNum?`: number
  - `filter?`: Filter
#### Enums
- **WhenceType**
  - `SEEK_SET` = 0
  - `SEEK_CUR` = 1
  - `SEEK_END` = 2
- **LocationType**
  - `LOCAL` = 1 << 0
  - `CLOUD` = 1 << 1
#### Functions
- `access(path: string): Promise<boolean>`
- `access(path: string, callback: AsyncCallback<boolean>): void`
- `accessSync(path: string): boolean`
- `close(file: number | File): Promise<void>`
- `close(file: number | File, callback: AsyncCallback<void>): void`
- `closeSync(file: number | File): void`
- `copy(srcUri: string, destUri: string, options?: CopyOptions): Promise<void>`
- `copy(srcUri: string, destUri: string, callback: AsyncCallback<void>): void`
- `copy(srcUri: string, destUri: string, options: CopyOptions, callback: AsyncCallback<void>): void`
- `copyDir(src: string, dest: string, mode?: number): Promise<void>`
- `copyDir(src: string, dest: string, callback: AsyncCallback<void>): void`
- `copyDir(src: string, dest: string, callback: AsyncCallback<void, Array<ConflictFiles>>): void`
- `copyDir(src: string, dest: string, mode: number, callback: AsyncCallback<void>): void`
- `copyDir(src: string, dest: string, mode: number, callback: AsyncCallback<void, Array<ConflictFiles>>): void`
- `copyDirSync(src: string, dest: string, mode?: number): void`
- `copyFile(src: string | number, dest: string | number, mode?: number): Promise<void>`
- `copyFile(src: string | number, dest: string | number, callback: AsyncCallback<void>): void`
- `copyFile(
  src: string | number,
  dest: string | number,
  mode: number,
  callback: AsyncCallback<void>
): void`
- `copyFileSync(src: string | number, dest: string | number, mode?: number): void`
- `createStream(path: string, mode: string): Promise<Stream>`
- `createStream(path: string, mode: string, callback: AsyncCallback<Stream>): void`
- `createStreamSync(path: string, mode: string): Stream`
- `createRandomAccessFile(file: string | File, mode?: number): Promise<RandomAccessFile>`
- `createRandomAccessFile(file: string | File, callback: AsyncCallback<RandomAccessFile>): void`
- `createRandomAccessFile(file: string | File, mode: number, callback: AsyncCallback<RandomAccessFile>): void`
- `createRandomAccessFileSync(file: string | File, mode?: number): RandomAccessFile`
- `createWatcher(path: string, events: number, listener: WatchEventListener): Watcher`
- `dup(fd: number): File`
- `fdatasync(fd: number): Promise<void>`
- `fdatasync(fd: number, callback: AsyncCallback<void>): void`
- `fdatasyncSync(fd: number): void`
- `fdopenStream(fd: number, mode: string): Promise<Stream>`
- `fdopenStream(fd: number, mode: string, callback: AsyncCallback<Stream>): void`
- `fdopenStreamSync(fd: number, mode: string): Stream`
- `fsync(fd: number): Promise<void>`
- `fsync(fd: number, callback: AsyncCallback<void>): void`
- `fsyncSync(fd: number): void`
- `listFile(
  path: string,
  options?: ListFileOptions
): Promise<string[]>`
- `listFile(path: string, callback: AsyncCallback<string[]>): void`
- `listFile(
  path: string,
  options: ListFileOptions,
  callback: AsyncCallback<string[]>
): void`
- `listFileSync(
  path: string,
  options?: ListFileOptions
): string[]`
- `lseek(fd: number, offset: number, whence?: WhenceType): number`
- `lstat(path: string): Promise<Stat>`
- `lstat(path: string, callback: AsyncCallback<Stat>): void`
- `lstatSync(path: string): Stat`
- `mkdir(path: string): Promise<void>`
- `mkdir(path: string, recursion: boolean): Promise<void>`
- `mkdir(path: string, callback: AsyncCallback<void>): void`
- `mkdir(path: string, recursion: boolean, callback: AsyncCallback<void>): void`
- `mkdirSync(path: string): void`
- `mkdirSync(path: string, recursion: boolean): void`
- `mkdtemp(prefix: string): Promise<string>`
- `mkdtemp(prefix: string, callback: AsyncCallback<string>): void`
- `mkdtempSync(prefix: string): string`
- `moveDir(src: string, dest: string, mode?: number): Promise<void>`
- `moveDir(src: string, dest: string, callback: AsyncCallback<void>): void`
- `moveDir(src: string, dest: string, callback: AsyncCallback<void, Array<ConflictFiles>>): void`
- `moveDir(src: string, dest: string, mode: number, callback: AsyncCallback<void>): void`
- `moveDir(src: string, dest: string, mode: number, callback: AsyncCallback<void, Array<ConflictFiles>>): void`
- `moveDirSync(src: string, dest: string, mode?: number): void`
- `moveFile(src: string, dest: string, mode?: number): Promise<void>`
- `moveFile(src: string, dest: string, callback: AsyncCallback<void>): void`
- `moveFile(src: string, dest: string, mode: number, callback: AsyncCallback<void>): void`
- `moveFileSync(src: string, dest: string, mode?: number): void`
- `open(path: string, mode?: number): Promise<File>`
- `open(path: string, callback: AsyncCallback<File>): void`
- `open(path: string, mode: number, callback: AsyncCallback<File>): void`
- `openSync(path: string, mode?: number): File`
- `read(
  fd: number,
  buffer: ArrayBuffer,
  options?: ReadOptions
): Promise<number>`
- `read(fd: number, buffer: ArrayBuffer, callback: AsyncCallback<number>): void`
- `read(
  fd: number,
  buffer: ArrayBuffer,
  options: ReadOptions,
  callback: AsyncCallback<number>
): void`
- `readSync(
  fd: number,
  buffer: ArrayBuffer,
  options?: ReadOptions
): number`
- `readLines(filePath: string, options?: Options): Promise<ReaderIterator>`
- `readLines(filePath: string, callback: AsyncCallback<ReaderIterator>): void`
- `readLines(filePath: string, options: Options, callback: AsyncCallback<ReaderIterator>): void`
- `readLinesSync(filePath: string, options?: Options): ReaderIterator`
- `readText(
  filePath: string,
  options?: ReadTextOptions
): Promise<string>`
- `readText(filePath: string, callback: AsyncCallback<string>): void`
- `readText(
  filePath: string,
  options: ReadTextOptions,
  callback: AsyncCallback<string>
): void`
- `readTextSync(
  filePath: string,
  options?: ReadTextOptions
): string`
- `rename(oldPath: string, newPath: string): Promise<void>`
- `rename(oldPath: string, newPath: string, callback: AsyncCallback<void>): void`
- `renameSync(oldPath: string, newPath: string): void`
- `rmdir(path: string): Promise<void>`
- `rmdir(path: string, callback: AsyncCallback<void>): void`
- `rmdirSync(path: string): void`
- `stat(file: string | number): Promise<Stat>`
- `stat(file: string | number, callback: AsyncCallback<Stat>): void`
- `statSync(file: string | number): Stat`
- `symlink(target: string, srcPath: string): Promise<void>`
- `symlink(target: string, srcPath: string, callback: AsyncCallback<void>): void`
- `symlinkSync(target: string, srcPath: string): void`
- `truncate(file: string | number, len?: number): Promise<void>`
- `truncate(file: string | number, callback: AsyncCallback<void>): void`
- `truncate(file: string | number, len: number, callback: AsyncCallback<void>): void`
- `truncateSync(file: string | number, len?: number): void`
- `unlink(path: string): Promise<void>`
- `unlink(path: string, callback: AsyncCallback<void>): void`
- `unlinkSync(path: string): void`
- `utimes(path: string, mtime: number): void`
- `write(
  fd: number,
  buffer: ArrayBuffer | string,
  options?: WriteOptions
): Promise<number>`
- `write(fd: number, buffer: ArrayBuffer | string, callback: AsyncCallback<number>): void`
- `write(
  fd: number,
  buffer: ArrayBuffer | string,
  options: WriteOptions,
  callback: AsyncCallback<number>
): void`
- `writeSync(
  fd: number,
  buffer: ArrayBuffer | string,
  options?: WriteOptions
): number`
#### Type Aliases
- `ProgressListener` = (progress: Progress) => void

### hash (@ohos.file.hash.d.ts)
#### Functions
- `hash(path: string, algorithm: string): Promise<string>`
- `hash(path: string, algorithm: string, callback: AsyncCallback<string>): void`

### photoAccessHelper (@ohos.file.photoAccessHelper.d.ts)
#### Interfaces
- **RequestOptions**
  - `deliveryMode`: DeliveryMode
- **MediaAssetDataHandler**
  - `onDataPrepared`: void
- **PhotoProxy**
- **PhotoAsset**
  - `readonly uri`: string
  - `readonly photoType`: PhotoType
  - `readonly displayName`: string
  - `get`: MemberType
  - `set`: void
  - `commitModify`: void
  - `commitModify`: Promise<void>
  - `open`: void
  - `open`: Promise<number>
  - `getReadOnlyFd`: void
  - `getReadOnlyFd`: Promise<number>
  - `close`: void
  - `close`: Promise<void>
  - `getThumbnail`: void
  - `getThumbnail`: void
  - `getThumbnail`: Promise<image.PixelMap>
  - `setFavorite`: void
  - `setFavorite`: Promise<void>
  - `setHidden`: void
  - `setHidden`: Promise<void>
  - `setUserComment`: void
  - `setUserComment`: Promise<void>
  - `getExif`: void
  - `getAnalysisData`: Promise<string>
  - `getExif`: Promise<string>
  - `setPending`: void
  - `setPending`: Promise<void>
  - `isEdited`: void
  - `isEdited`: Promise<boolean>
  - `requestEditData`: void
  - `requestEditData`: Promise<string>
  - `getEditData`: Promise<MediaAssetEditData>
  - `requestSource`: void
  - `requestSource`: Promise<number>
  - `commitEditedAsset`: Promise<void>
  - `revertToOriginal`: Promise<void>
  - `requestPhoto`: string
  - `requestPhoto`: string
  - `cancelPhotoRequest`: void
- **FetchOptions**
  - `fetchColumns`: Array<string>
  - `predicates`: dataSharePredicates.DataSharePredicates
- **PhotoCreateOptions**
  - `subtype?`: PhotoSubtype
  - `cameraShotKey?`: string
- **CreateOptions**
  - `title?`: string
- **RequestPhotoOptions**
  - `size?`: image.Size
  - `requestPhotoType?`: RequestPhotoType
- **FetchResult**
  - `getCount`: number
  - `isAfterLast`: boolean
  - `getFirstObject`: void
  - `getFirstObject`: Promise<T>
  - `getNextObject`: void
  - `getNextObject`: Promise<T>
  - `getLastObject`: void
  - `getLastObject`: Promise<T>
  - `getObjectByPosition`: void
  - `getObjectByPosition`: Promise<T>
  - `getAllObjects`: void
  - `getAllObjects`: Promise<Array<T>>
  - `close`: void
- **AbsAlbum**
  - `readonly albumType`: AlbumType
  - `readonly albumSubtype`: AlbumSubtype
  - `albumName`: string
  - `readonly albumUri`: string
  - `readonly count`: number
  - `readonly coverUri`: string
  - `getAssets`: void
  - `getAssets`: Promise<FetchResult<PhotoAsset>>
- **Album**
  - `readonly imageCount?`: number
  - `readonly videoCount?`: number
  - `commitModify`: void
  - `commitModify`: Promise<void>
  - `addAssets`: void
  - `addAssets`: Promise<void>
  - `removeAssets`: void
  - `removeAssets`: Promise<void>
  - `recoverAssets`: void
  - `recoverAssets`: Promise<void>
  - `deleteAssets`: void
  - `deleteAssets`: Promise<void>
  - `setCoverUri`: void
  - `setCoverUri`: Promise<void>
- **PhotoAccessHelper**
  - `getAssets`: void
  - `getAssets`: Promise<FetchResult<PhotoAsset>>
  - `createAsset`: void
  - `createAsset`: Promise<PhotoAsset>
  - `createAsset`: Promise<PhotoAsset>
  - `createAsset`: void
  - `createAsset`: void
  - `createAsset`: void
  - `createAsset`: Promise<string>
  - `createAlbum`: void
  - `createAlbum`: Promise<Album>
  - `deleteAlbums`: void
  - `deleteAlbums`: Promise<void>
  - `getAlbums`: void
  - `getAlbums`: void
  - `getAlbums`: Promise<FetchResult<Album>>
  - `getHiddenAlbums`: void
  - `getHiddenAlbums`: void
  - `getHiddenAlbums`: Promise<FetchResult<Album>>
  - `deleteAssets`: void
  - `deleteAssets`: Promise<void>
  - `registerChange`: void
  - `unRegisterChange`: void
  - `createDeleteRequest`: void
  - `createDeleteRequest`: Promise<void>
  - `getPhotoIndex`: void
  - `getPhotoIndex`: Promise<number>
  - `release`: void
  - `release`: Promise<void>
  - `saveFormInfo`: void
  - `saveFormInfo`: Promise<void>
  - `removeFormInfo`: void
  - `removeFormInfo`: Promise<void>
  - `applyChanges`: Promise<void>
- **FormInfo**
  - `formId`: string
  - `uri`: string
- **ChangeData**
  - `type`: NotifyType
  - `uris`: Array<string>
  - `extraUris`: Array<string>
- **MediaChangeRequest**
#### Enums
- **PhotoType**
  - `IMAGE` = 1
- **PhotoSubtype**
- **PositionType**
  - `LOCAL` = 1 << 0
  - `CLOUD` = 1 << 1
- **AnalysisType**
  - `ANALYSIS_AESTHETICS_SCORE` = 0
- **RecommendationType**
  - `QR_OR_BAR_CODE` = 1
  - `QR_CODE` = 2
  - `BAR_CODE` = 3
  - `ID_CARD` = 4
  - `PROFILE_PICTURE` = 5
- **DeliveryMode**
  - `FAST_MODE` = 0
  - `HIGH_QUALITY_MODE` = 1
  - `BALANCE_MODE` = 2
- **SourceMode**
  - `ORIGINAL_MODE` = 0
  - `EDITED_MODE` = 1
- **PhotoKeys**
  - `URI` = 'uri'
  - `PHOTO_TYPE` = 'media_type'
  - `DISPLAY_NAME` = 'display_name'
  - `SIZE` = 'size'
  - `DATE_ADDED` = 'date_added'
  - `DATE_MODIFIED` = 'date_modified'
  - `DURATION` = 'duration'
  - `WIDTH` = 'width'
  - `HEIGHT` = 'height'
  - `DATE_TAKEN` = 'date_taken'
  - `ORIENTATION` = 'orientation'
  - `FAVORITE` = 'is_favorite'
  - `TITLE` = 'title'
  - `POSITION` = 'position'
  - `DATE_TRASHED` = 'date_trashed'
  - `HIDDEN` = 'hidden'
  - `USER_COMMENT` = 'user_comment'
  - `CAMERA_SHOT_KEY` = 'camera_shot_key'
  - `DATE_YEAR` = 'date_year'
  - `DATE_MONTH` = 'date_month'
  - `DATE_DAY` = 'date_day'
  - `PENDING` = 'pending'
- **AlbumKeys**
  - `URI` = 'uri'
  - `ALBUM_NAME` = 'album_name'
- **HiddenPhotosDisplayMode**
- **AlbumType**
  - `USER` = 0
  - `SYSTEM` = 1024
  - `SMART` = 4096
- **AlbumSubtype**
  - `USER_GENERIC` = 1
  - `FAVORITE` = 1025
  - `SOURCE_GENERIC` = 2049
  - `CLASSIFY` = 4097
  - `GEOGRAPHY_LOCATION` = 4099
  - `ANY` = 2147483647
- **RequestPhotoType**
  - `REQUEST_ALL_THUMBNAILS` = 0
- **NotifyType**
- **DefaultChangeUri**
- **PhotoViewMIMETypes**
  - `IMAGE_TYPE` = 'image/*'
  - `VIDEO_TYPE` = 'video/*'
  - `IMAGE_VIDEO_TYPE` = '*/*'
- **ResourceType**
  - `IMAGE_RESOURCE` = 1
  - `VIDEO_RESOURCE` = 2
  - `PHOTO_PROXY` = 3
#### Functions
- `getPhotoAccessHelper(context: Context): PhotoAccessHelper`
#### Classes
- **MediaAssetManager**
  - `requestImage()`: Promise<string>
  - `requestImageData()`: Promise<string>
- **PhotoSelectOptions**
  - `MIMEType?`: PhotoViewMIMETypes
  - `maxSelectNumber?`: number
  - `isSearchSupported?`: boolean
  - `isPhotoTakingSupported?`: boolean
  - `isEditSupported?`: boolean
  - `recommendationOptions?`: RecommendationOptions
  - `preselectedUris?`: Array<string>
- **RecommendationOptions**
  - `recommendationType?`: RecommendationType
- **PhotoSelectResult**
  - `photoUris`: Array<string>
  - `isOriginalPhoto`: boolean
- **PhotoViewPicker**
  - `select()`: Promise<PhotoSelectResult>
  - `select()`: void
  - `select()`: void
- **MediaAssetEditData**
  - `compatibleFormat`: string
  - `formatVersion`: string
  - `data`: string
#### Type Aliases
- `MemberType` = number | string | boolean

### picker (@ohos.file.picker.d.ts)
#### Enums
- **PhotoViewMIMETypes**
  - `IMAGE_TYPE` = 'image/*'
  - `VIDEO_TYPE` = 'video/*'
  - `IMAGE_VIDEO_TYPE` = '*/*'
- **DocumentSelectMode**
  - `FILE` = 0
  - `FOLDER` = 1
  - `MIXED` = 2
#### Classes
- **PhotoSelectOptions**
  - `MIMEType?`: PhotoViewMIMETypes
  - `maxSelectNumber?`: number
- **PhotoSelectResult**
  - `photoUris`: Array<string>
  - `isOriginalPhoto`: boolean
- **PhotoSaveOptions**
  - `newFileNames?`: Array<string>
- **PhotoViewPicker**
  - `select()`: Promise<PhotoSelectResult>
  - `select()`: void
  - `select()`: void
  - `save()`: Promise<Array<string>>
  - `save()`: void
  - `save()`: void
- **DocumentSelectOptions**
  - `defaultFilePathUri?`: string
  - `fileSuffixFilters?`: Array<string>
  - `maxSelectNumber?`: number
  - `selectMode?`: DocumentSelectMode
- **DocumentSaveOptions**
  - `newFileNames?`: Array<string>
  - `defaultFilePathUri?`: string
  - `fileSuffixChoices?`: Array<string>
- **DocumentViewPicker**
  - `select()`: Promise<Array<string>>
  - `select()`: void
  - `select()`: void
  - `save()`: Promise<Array<string>>
  - `save()`: void
  - `save()`: void
- **AudioSelectOptions**
- **AudioSaveOptions**
  - `newFileNames?`: Array<string>
- **AudioViewPicker**
  - `select()`: Promise<Array<string>>
  - `select()`: void
  - `select()`: void
  - `save()`: Promise<Array<string>>
  - `save()`: void
  - `save()`: void

### recent (@ohos.file.recent.d.ts)
#### Interfaces
- **FileInfo**
  - `readonly uri`: string
  - `readonly srcPath`: string
  - `readonly fileName`: string
  - `readonly mode`: number
  - `readonly size`: number
  - `readonly mtime`: number
  - `readonly ctime`: number
#### Functions
- `listFile(): Array<FileInfo>`
- `add(uri: string): void`
- `remove(uri: string): void`

### securityLabel (@ohos.file.securityLabel.d.ts)
#### Functions
- `setSecurityLabel(path: string, type: DataLevel): Promise<void>`
- `setSecurityLabel(path: string, type: DataLevel, callback: AsyncCallback<void>): void`
- `setSecurityLabelSync(path: string, type: DataLevel): void`
- `getSecurityLabel(path: string): Promise<string>`
- `getSecurityLabel(path: string, callback: AsyncCallback<string>): void`
- `getSecurityLabelSync(path: string): string`
#### Type Aliases
- `DataLevel` = 's0' | 's1' | 's2' | 's3' | 's4'

### statfs (@ohos.file.statvfs.d.ts)
#### Functions
- `getFreeSize(path: string): Promise<number>`
- `getFreeSize(path: string, callback: AsyncCallback<number>): void`
- `getFreeSizeSync(path: string): number`
- `getTotalSize(path: string): Promise<number>`
- `getTotalSize(path: string, callback: AsyncCallback<number>): void`
- `getTotalSizeSync(path: string): number`

### storageStatistics (@ohos.file.storageStatistics.d.ts)
#### Interfaces
- **BundleStats**
  - `appSize`: number
  - `cacheSize`: number
  - `dataSize`: number
- **StorageStats**
  - `total`: number
  - `audio`: number
  - `video`: number
  - `image`: number
  - `file`: number
  - `app`: number
#### Functions
- `getTotalSizeOfVolume(volumeUuid: string, callback: AsyncCallback<number>): void`
- `getTotalSizeOfVolume(volumeUuid: string): Promise<number>`
- `getFreeSizeOfVolume(volumeUuid: string, callback: AsyncCallback<number>): void`
- `getFreeSizeOfVolume(volumeUuid: string): Promise<number>`
- `getBundleStats(packageName: string, callback: AsyncCallback<BundleStats>): void`
- `getBundleStats(packageName: string): Promise<BundleStats>`
- `getCurrentBundleStats(callback: AsyncCallback<BundleStats>): void`
- `getCurrentBundleStats(): Promise<BundleStats>`
- `getSystemSize(callback: AsyncCallback<number>): void`
- `getSystemSize(): Promise<number>`
- `getUserStorageStats(): Promise<StorageStats>`
- `getUserStorageStats(callback: AsyncCallback<StorageStats>): void`
- `getUserStorageStats(userId: number): Promise<StorageStats>`
- `getUserStorageStats(userId: number, callback: AsyncCallback<StorageStats>): void`
- `getTotalSize(callback: AsyncCallback<number>): void`
- `getTotalSize(): Promise<number>`
- `getTotalSizeSync(): number`
- `getFreeSize(callback: AsyncCallback<number>): void`
- `getFreeSize(): Promise<number>`
- `getFreeSizeSync(): number`

### trash (@ohos.file.trash.d.ts)
#### Interfaces
- **FileInfo**
  - `readonly uri`: string
  - `readonly srcPath`: string
  - `readonly fileName`: string
  - `readonly mode`: number
  - `readonly size`: number
  - `readonly mtime`: number
  - `readonly ctime`: number
#### Functions
- `listFile(): Array<FileInfo>`
- `recover(uri: string): void`
- `completelyDelete(uri: string): void`

### volumeManager (@ohos.file.volumeManager.d.ts)
#### Interfaces
- **Volume**
  - `id`: string
  - `uuid`: string
  - `diskId`: string
  - `description`: string
  - `removable`: boolean
  - `state`: number
  - `path`: string
#### Functions
- `getAllVolumes(callback: AsyncCallback<Array<Volume>>): void`
- `getAllVolumes(): Promise<Array<Volume>>`
- `mount(volumeId: string, callback: AsyncCallback<void>): void`
- `mount(volumeId: string): Promise<void>`
- `unmount(volumeId: string, callback: AsyncCallback<void>): void`
- `unmount(volumeId: string): Promise<void>`
- `getVolumeByUuid(uuid: string, callback: AsyncCallback<Volume>): void`
- `getVolumeByUuid(uuid: string): Promise<Volume>`
- `getVolumeById(volumeId: string, callback: AsyncCallback<Volume>): void`
- `getVolumeById(volumeId: string): Promise<Volume>`
- `setVolumeDescription(uuid: string, description: string, callback: AsyncCallback<void>): void`
- `setVolumeDescription(uuid: string, description: string): Promise<void>`
- `format(volumeId: string, fsType: string, callback: AsyncCallback<void>): void`
- `format(volumeId: string, fsType: string): Promise<void>`
- `partition(diskId: string, type: number, callback: AsyncCallback<void>): void`
- `partition(diskId: string, type: number): Promise<void>`

### fileIO (@ohos.fileio.d.ts)
#### Interfaces
- **Dir**
  - `read`: Promise<Dirent>
  - `read`: void
  - `readSync`: Dirent
  - `close`: Promise<void>
  - `close`: void
  - `closeSync`: void
- **Dirent**
  - `readonly name`: string
  - `isBlockDevice`: boolean
  - `isCharacterDevice`: boolean
  - `isDirectory`: boolean
  - `isFIFO`: boolean
  - `isFile`: boolean
  - `isSocket`: boolean
  - `isSymbolicLink`: boolean
- **Stat**
  - `readonly dev`: number
  - `readonly ino`: number
  - `readonly mode`: number
  - `readonly nlink`: number
  - `readonly uid`: number
  - `readonly gid`: number
  - `readonly rdev`: number
  - `readonly size`: number
  - `readonly blocks`: number
  - `readonly atime`: number
  - `readonly mtime`: number
  - `readonly ctime`: number
  - `isBlockDevice`: boolean
  - `isCharacterDevice`: boolean
  - `isDirectory`: boolean
  - `isFIFO`: boolean
  - `isFile`: boolean
  - `isSocket`: boolean
  - `isSymbolicLink`: boolean
- **Stream**
  - `close`: Promise<void>
  - `close`: void
  - `closeSync`: void
  - `flush`: Promise<void>
  - `flush`: void
  - `flushSync`: void
  - `write`: Promise<number>
  - `write`: void
  - `write`: void
  - `writeSync`: number
  - `read`: Promise<ReadOut>
  - `read`: void
  - `read`: void
  - `readSync`: number
- **ReadOut**
  - `bytesRead`: number
  - `offset`: number
  - `buffer`: ArrayBuffer
- **Watcher**
  - `stop`: Promise<void>
  - `stop`: void
#### Functions
- `access(path: string, mode?: number): Promise<void>`
- `access(path: string, callback: AsyncCallback<void>): void`
- `access(path: string, mode: number, callback: AsyncCallback<void>): void`
- `accessSync(path: string, mode?: number): void`
- `close(fd: number): Promise<void>`
- `close(fd: number, callback: AsyncCallback<void>): void`
- `closeSync(fd: number): void`
- `copyFile(src: string | number, dest: string | number, mode?: number): Promise<void>`
- `copyFile(src: string | number, dest: string | number, callback: AsyncCallback<void>): void`
- `copyFile(
  src: string | number,
  dest: string | number,
  mode: number,
  callback: AsyncCallback<void>
): void`
- `copyFileSync(src: string | number, dest: string | number, mode?: number): void`
- `createStream(path: string, mode: string): Promise<Stream>`
- `createStream(path: string, mode: string, callback: AsyncCallback<Stream>): void`
- `createStreamSync(path: string, mode: string): Stream`
- `chown(path: string, uid: number, gid: number): Promise<void>`
- `chown(path: string, uid: number, gid: number, callback: AsyncCallback<void>): void`
- `chownSync(path: string, uid: number, gid: number): void`
- `chmod(path: string, mode: number): Promise<void>`
- `chmod(path: string, mode: number, callback: AsyncCallback<void>): void`
- `chmodSync(path: string, mode: number): void`
- `ftruncate(fd: number, len?: number): Promise<void>`
- `ftruncate(fd: number, callback: AsyncCallback<void>): void`
- `ftruncate(fd: number, len: number, callback: AsyncCallback<void>): void`
- `ftruncateSync(fd: number, len?: number): void`
- `fsync(fd: number): Promise<void>`
- `fsync(fd: number, callback: AsyncCallback<void>): void`
- `fsyncSync(fd: number): void`
- `fstat(fd: number): Promise<Stat>`
- `fstat(fd: number, callback: AsyncCallback<Stat>): void`
- `fstatSync(fd: number): Stat`
- `fdatasync(fd: number): Promise<void>`
- `fdatasync(fd: number, callback: AsyncCallback<void>): void`
- `fdatasyncSync(fd: number): void`
- `fchown(fd: number, uid: number, gid: number): Promise<void>`
- `fchown(fd: number, uid: number, gid: number, callback: AsyncCallback<void>): void`
- `fchownSync(fd: number, uid: number, gid: number): void`
- `fchmod(fd: number, mode: number): Promise<void>`
- `fchmod(fd: number, mode: number, callback: AsyncCallback<void>): void`
- `fchmodSync(fd: number, mode: number): void`
- `fdopenStream(fd: number, mode: string): Promise<Stream>`
- `fdopenStream(fd: number, mode: string, callback: AsyncCallback<Stream>): void`
- `fdopenStreamSync(fd: number, mode: string): Stream`
- `hash(path: string, algorithm: string): Promise<string>`
- `hash(path: string, algorithm: string, callback: AsyncCallback<string>): void`
- `lchown(path: string, uid: number, gid: number): Promise<void>`
- `lchown(path: string, uid: number, gid: number, callback: AsyncCallback<void>): void`
- `lchownSync(path: string, uid: number, gid: number): void`
- `lstat(path: string): Promise<Stat>`
- `lstat(path: string, callback: AsyncCallback<Stat>): void`
- `lstatSync(path: string): Stat`
- `mkdir(path: string, mode?: number): Promise<void>`
- `mkdir(path: string, callback: AsyncCallback<void>): void`
- `mkdir(path: string, mode: number, callback: AsyncCallback<void>): void`
- `mkdirSync(path: string, mode?: number): void`
- `mkdtemp(prefix: string): Promise<string>`
- `mkdtemp(prefix: string, callback: AsyncCallback<string>): void`
- `mkdtempSync(prefix: string): string`
- `open(path: string, flags?: number, mode?: number): Promise<number>`
- `open(path: string, callback: AsyncCallback<number>): void`
- `open(path: string, flags: number, callback: AsyncCallback<number>): void`
- `open(path: string, flags: number, mode: number, callback: AsyncCallback<number>): void`
- `openSync(path: string, flags?: number, mode?: number): number`
- `opendir(path: string): Promise<Dir>`
- `opendir(path: string, callback: AsyncCallback<Dir>): void`
- `opendirSync(path: string): Dir`
- `readText(
  filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }
): Promise<string>`
- `readText(
  filePath: string,
  options: {
    position?: number;
    length?: number;
    encoding?: string;
  },
  callback: AsyncCallback<string>
): void`
- `readTextSync(
  filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }
): string`
- `read(
  fd: number,
  buffer: ArrayBuffer,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
  }
): Promise<ReadOut>`
- `read(fd: number, buffer: ArrayBuffer, callback: AsyncCallback<ReadOut>): void`
- `read(
  fd: number,
  buffer: ArrayBuffer,
  options: {
    offset?: number;
    length?: number;
    position?: number;
  },
  callback: AsyncCallback<ReadOut>
): void`
- `readSync(
  fd: number,
  buffer: ArrayBuffer,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
  }
): number`
- `rename(oldPath: string, newPath: string): Promise<void>`
- `rename(oldPath: string, newPath: string, callback: AsyncCallback<void>): void`
- `renameSync(oldPath: string, newPath: string): void`
- `rmdir(path: string): Promise<void>`
- `rmdir(path: string, callback: AsyncCallback<void>): void`
- `rmdirSync(path: string): void`
- `stat(path: string): Promise<Stat>`
- `stat(path: string, callback: AsyncCallback<Stat>): void`
- `statSync(path: string): Stat`
- `symlink(target: string, srcPath: string): Promise<void>`
- `symlink(target: string, srcPath: string, callback: AsyncCallback<void>): void`
- `symlinkSync(target: string, srcPath: string): void`
- `truncate(path: string, len?: number): Promise<void>`
- `truncate(path: string, callback: AsyncCallback<void>): void`
- `truncate(path: string, len: number, callback: AsyncCallback<void>): void`
- `truncateSync(path: string, len?: number): void`
- `unlink(path: string): Promise<void>`
- `unlink(path: string, callback: AsyncCallback<void>): void`
- `unlinkSync(path: string): void`
- `write(
  fd: number,
  buffer: ArrayBuffer | string,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
    encoding?: string;
  }
): Promise<number>`
- `write(fd: number, buffer: ArrayBuffer | string, callback: AsyncCallback<number>): void`
- `write(
  fd: number,
  buffer: ArrayBuffer | string,
  options: {
    offset?: number;
    length?: number;
    position?: number;
    encoding?: string;
  },
  callback: AsyncCallback<number>
): void`
- `writeSync(
  fd: number,
  buffer: ArrayBuffer | string,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
    encoding?: string;
  }
): number`
- `createWatcher(filename: string, events: number, callback: AsyncCallback<number>): Watcher`

### userFileManager (@ohos.filemanagement.userFileManager.d.ts)
#### Interfaces
- **FileAsset**
  - `readonly uri`: string
  - `readonly fileType`: FileType
  - `displayName`: string
  - `get`: MemberType
  - `set`: void
  - `commitModify`: void
  - `commitModify`: Promise<void>
  - `open`: void
  - `open`: Promise<number>
  - `close`: void
  - `close`: Promise<void>
  - `getThumbnail`: void
  - `getThumbnail`: void
  - `getThumbnail`: Promise<image.PixelMap>
  - `favorite`: void
  - `favorite`: Promise<void>
  - `setHidden`: void
  - `setHidden`: Promise<void>
  - `setUserComment`: void
  - `setUserComment`: Promise<void>
  - `getExif`: void
  - `getExif`: Promise<string>
- **FetchOptions**
  - `fetchColumns`: Array<string>
  - `predicates`: dataSharePredicates.DataSharePredicates
- **AlbumFetchOptions**
  - `predicates`: dataSharePredicates.DataSharePredicates
- **PhotoCreateOptions**
  - `subType?`: PhotoSubType
  - `cameraShotKey?`: string
- **FetchResult**
  - `getCount`: number
  - `isAfterLast`: boolean
  - `close`: void
  - `getFirstObject`: void
  - `getFirstObject`: Promise<T>
  - `getNextObject`: void
  - `getNextObject`: Promise<T>
  - `getLastObject`: void
  - `getLastObject`: Promise<T>
  - `getPositionObject`: void
  - `getPositionObject`: Promise<T>
  - `getAllObject`: void
  - `getAllObject`: Promise<Array<T>>
- **AbsAlbum**
  - `readonly albumType`: AlbumType
  - `readonly albumSubType`: AlbumSubType
  - `albumName`: string
  - `readonly albumUri`: string
  - `readonly dateModified`: number
  - `readonly count`: number
  - `coverUri`: string
  - `getPhotoAssets`: void
  - `getPhotoAssets`: Promise<FetchResult<FileAsset>>
- **Album**
  - `commitModify`: void
  - `commitModify`: Promise<void>
  - `addPhotoAssets`: void
  - `addPhotoAssets`: Promise<void>
  - `removePhotoAssets`: void
  - `removePhotoAssets`: Promise<void>
  - `recoverPhotoAssets`: void
  - `recoverPhotoAssets`: Promise<void>
  - `deletePhotoAssets`: void
  - `deletePhotoAssets`: Promise<void>
- **UserFileManager**
  - `getPhotoAssets`: void
  - `getPhotoAssets`: Promise<FetchResult<FileAsset>>
  - `createPhotoAsset`: void
  - `createPhotoAsset`: void
  - `createPhotoAsset`: Promise<FileAsset>
  - `createPhotoAsset`: Promise<FileAsset>
  - `createPhotoAsset`: void
  - `createAudioAsset`: void
  - `createAudioAsset`: Promise<FileAsset>
  - `getPhotoAlbums`: void
  - `getPhotoAlbums`: Promise<FetchResult<Album>>
  - `createAlbum`: void
  - `createAlbum`: Promise<Album>
  - `deleteAlbums`: void
  - `deleteAlbums`: Promise<void>
  - `getAlbums`: void
  - `getAlbums`: void
  - `getAlbums`: Promise<FetchResult<Album>>
  - `getPrivateAlbum`: void
  - `getPrivateAlbum`: Promise<FetchResult<PrivateAlbum>>
  - `getAudioAssets`: void
  - `getAudioAssets`: Promise<FetchResult<FileAsset>>
  - `delete`: void
  - `delete`: Promise<void>
  - `getPhotoIndex`: void
  - `getPhotoIndex`: Promise<number>
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `getActivePeers`: void
  - `getActivePeers`: Promise<Array<PeerInfo>>
  - `getAllPeers`: void
  - `getAllPeers`: Promise<Array<PeerInfo>>
  - `release`: void
  - `release`: Promise<void>
- **ChangeData**
  - `type`: NotifyType
  - `uris`: Array<string>
  - `subUris`: Array<string>
- **PeerInfo**
  - `readonly deviceName`: string
  - `readonly networkId`: string
  - `readonly isOnline`: boolean
- **PrivateAlbum**
  - `delete`: void
  - `delete`: Promise<void>
  - `recover`: void
  - `recover`: Promise<void>
#### Enums
- **FileType**
  - `IMAGE` = 1
- **PhotoSubType**
- **PositionType**
  - `LOCAL` = 1
- **AudioKey**
- **ImageVideoKey**
- **AlbumKey**
- **AlbumType**
  - `USER` = 0
  - `SYSTEM` = 1024
- **AlbumSubType**
  - `USER_GENERIC` = 1
  - `FAVORITE` = 1025
  - `ANY` = 2147483647
- **NotifyType**
- **DefaultChangeUri**
- **PrivateAlbumType**
#### Functions
- `getUserFileMgr(context: Context): UserFileManager`
#### Type Aliases
- `MemberType` = number | string | boolean
- `ChangeEvent` = 'deviceChange'
    | 'albumChange'
    | 'imageChange'
    | 'audioChange'
    | 'videoChange'
    | 'remoteFileChange'

### fileShare (@ohos.fileshare.d.ts)
#### Interfaces
- **PolicyInfo**
  - `uri`: string
  - `operationMode`: number
#### Enums
- **OperationMode**
  - `READ_MODE` = 0b1
  - `WRITE_MODE` = 0b10
- **PolicyErrorCode**
  - `PERSISTENCE_FORBIDDEN` = 1
  - `INVALID_MODE` = 2
  - `INVALID_PATH` = 3
#### Functions
- `grantUriPermission(
    uri: string,
    bundleName: string,
    flag: wantConstant.Flags,
    callback: AsyncCallback<void>
  ): void`
- `grantUriPermission(uri: string, bundleName: string, flag: wantConstant.Flags): Promise<void>`
- `persistPermission(policies: Array<PolicyInfo>): Promise<void>`
- `revokePermission(policies: Array<PolicyInfo>): Promise<void>`
- `activatePermission(policies: Array<PolicyInfo>): Promise<void>`
- `deactivatePermission(policies: Array<PolicyInfo>): Promise<void>`
#### Type Aliases
- `PolicyErrorResult` = {
    /**
     * Indicates the failed uri of the policy information.
     *
     * @type { string }
     * @syscap SystemCapability.FileManagement.App

### font (@ohos.font.d.ts)
#### Interfaces
- **FontOptions**
  - `familyName`: string | Resource
  - `familySrc`: string | Resource
- **FontInfo**
  - `path`: string
  - `postScriptName`: string
  - `fullName`: string
  - `family`: string
  - `subfamily`: string
  - `weight`: number
  - `width`: number
  - `italic`: boolean
  - `monoSpace`: boolean
  - `symbolic`: boolean
- **UIFontConfig**
  - `fontDir`: Array<string>
  - `generic`: Array<UIFontGenericInfo>
  - `fallbackGroups`: Array<UIFontFallbackGroupInfo>
- **UIFontGenericInfo**
  - `family`: string
  - `alias`: Array<UIFontAliasInfo>
  - `adjust`: Array<UIFontAdjustInfo>
- **UIFontAliasInfo**
  - `name`: string
  - `weight`: number
- **UIFontAdjustInfo**
  - `weight`: number
  - `to`: number
- **UIFontFallbackGroupInfo**
  - `fontSetName`: string
  - `fallback`: Array<UIFontFallbackInfo>
- **UIFontFallbackInfo**
  - `language`: string
  - `family`: string
#### Functions
- `registerFont(options: FontOptions): void`
- `getSystemFontList(): Array<string>`
- `getFontByName(fontName: string): FontInfo`
- `getUIFontConfig(): UIFontConfig`

### geoLocationManager (@ohos.geoLocationManager.d.ts)
#### Interfaces
- **ReverseGeocodingMockInfo**
  - `location`: ReverseGeoCodeRequest
  - `geoAddress`: GeoAddress
- **LocationMockConfig**
  - `timeInterval`: number
  - `locations`: Array<Location>
- **SatelliteStatusInfo**
  - `satellitesNumber`: number
  - `satelliteIds`: Array<number>
  - `carrierToNoiseDensitys`: Array<number>
  - `altitudes`: Array<number>
  - `azimuths`: Array<number>
  - `carrierFrequencies`: Array<number>
- **CachedGnssLocationsRequest**
  - `reportingPeriodSec`: number
  - `wakeUpCacheQueueFull`: boolean
- **GeofenceRequest**
  - `scenario`: LocationRequestScenario
  - `geofence`: Geofence
- **Geofence**
  - `latitude`: number
  - `longitude`: number
  - `radius`: number
  - `expiration`: number
- **ReverseGeoCodeRequest**
  - `locale?`: string
  - `latitude`: number
  - `longitude`: number
  - `maxItems?`: number
- **GeoCodeRequest**
  - `locale?`: string
  - `description`: string
  - `maxItems?`: number
  - `minLatitude?`: number
  - `minLongitude?`: number
  - `maxLatitude?`: number
  - `maxLongitude?`: number
- **GeoAddress**
  - `latitude?`: number
  - `longitude?`: number
  - `locale?`: string
  - `placeName?`: string
  - `countryCode?`: string
  - `countryName?`: string
  - `administrativeArea?`: string
  - `subAdministrativeArea?`: string
  - `locality?`: string
  - `subLocality?`: string
  - `roadName?`: string
  - `subRoadName?`: string
  - `premises?`: string
  - `postalCode?`: string
  - `phoneNumber?`: string
  - `addressUrl?`: string
  - `descriptions?`: Array<string>
  - `descriptionsSize?`: number
  - `isFromMock?`: Boolean
- **LocationRequest**
  - `priority?`: LocationRequestPriority
  - `scenario?`: LocationRequestScenario
  - `timeInterval?`: number
  - `distanceInterval?`: number
  - `maxAccuracy?`: number
- **CurrentLocationRequest**
  - `priority?`: LocationRequestPriority
  - `scenario?`: LocationRequestScenario
  - `maxAccuracy?`: number
  - `timeoutMs?`: number
- **Location**
  - `latitude`: number
  - `longitude`: number
  - `altitude`: number
  - `accuracy`: number
  - `speed`: number
  - `timeStamp`: number
  - `direction`: number
  - `timeSinceBoot`: number
  - `additions?`: Array<string>
  - `additionSize?`: number
  - `isFromMock?`: Boolean
- **LocatingRequiredDataConfig**
  - `type`: LocatingRequiredDataType
  - `needStartScan`: boolean
  - `scanInterval?`: number
  - `scanTimeout?`: number
- **LocatingRequiredData**
  - `wifiData?`: WifiScanInfo
  - `bluetoothData?`: BluetoothScanInfo
- **WifiScanInfo**
  - `ssid`: string
  - `bssid`: string
  - `rssi`: number
  - `frequency`: number
  - `timestamp`: number
- **BluetoothScanInfo**
  - `deviceName`: string
  - `macAddress`: string
  - `rssi`: number
  - `timestamp`: number
- **LocationCommand**
  - `scenario`: LocationRequestScenario
  - `command`: string
- **CountryCode**
  - `country`: string
  - `type`: CountryCodeType
#### Enums
- **LocationRequestPriority**
  - `UNSET` = 0x200
- **LocationRequestScenario**
  - `UNSET` = 0x300
- **LocationPrivacyType**
  - `OTHERS` = 0
- **CountryCodeType**
  - `COUNTRY_CODE_FROM_LOCALE` = 1
- **LocatingRequiredDataType**
  - `WIFI` = 1
#### Functions
- `on(type: 'locationChange', request: LocationRequest, callback: Callback<Location>): void`
- `off(type: 'locationChange', callback?: Callback<Location>): void`
- `on(type: 'locationEnabledChange', callback: Callback<boolean>): void`
- `off(type: 'locationEnabledChange', callback?: Callback<boolean>): void`
- `on(type: 'cachedGnssLocationsChange', request: CachedGnssLocationsRequest, callback: Callback<Array<Location>>): void`
- `off(type: 'cachedGnssLocationsChange', callback?: Callback<Array<Location>>): void`
- `on(type: 'satelliteStatusChange', callback: Callback<SatelliteStatusInfo>): void`
- `off(type: 'satelliteStatusChange', callback?: Callback<SatelliteStatusInfo>): void`
- `on(type: 'nmeaMessage', callback: Callback<string>): void`
- `off(type: 'nmeaMessage', callback?: Callback<string>): void`
- `on(type: 'gnssFenceStatusChange', request: GeofenceRequest, want: WantAgent): void`
- `off(type: 'gnssFenceStatusChange', request: GeofenceRequest, want: WantAgent): void`
- `on(type: 'countryCodeChange', callback: Callback<CountryCode>): void`
- `off(type: 'countryCodeChange', callback?: Callback<CountryCode>): void`
- `on(type: 'locatingRequiredDataChange', config: LocatingRequiredDataConfig, callback: Callback<Array<LocatingRequiredData>>): void`
- `off(type: 'locatingRequiredDataChange', callback?: Callback<Array<LocatingRequiredData>>): void`
- `getCurrentLocation(request: CurrentLocationRequest, callback: AsyncCallback<Location>): void`
- `getCurrentLocation(callback: AsyncCallback<Location>): void`
- `getCurrentLocation(request?: CurrentLocationRequest): Promise<Location>`
- `getLastLocation(): Location`
- `isLocationEnabled(): boolean`
- `enableLocation(callback: AsyncCallback<void>): void`
- `enableLocation(): Promise<void>`
- `disableLocation(): void`
- `getAddressesFromLocation(request: ReverseGeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void`
- `getAddressesFromLocation(request: ReverseGeoCodeRequest): Promise<Array<GeoAddress>>`
- `getAddressesFromLocationName(request: GeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void`
- `getAddressesFromLocationName(request: GeoCodeRequest): Promise<Array<GeoAddress>>`
- `isGeocoderAvailable(): boolean`
- `getCachedGnssLocationsSize(callback: AsyncCallback<number>): void`
- `getCachedGnssLocationsSize(): Promise<number>`
- `flushCachedGnssLocations(callback: AsyncCallback<void>): void`
- `flushCachedGnssLocations(): Promise<void>`
- `sendCommand(command: LocationCommand, callback: AsyncCallback<void>): void`
- `sendCommand(command: LocationCommand): Promise<void>`
- `getCountryCode(callback: AsyncCallback<CountryCode>): void`
- `getCountryCode(): Promise<CountryCode>`
- `enableLocationMock(): void`
- `disableLocationMock(): void`
- `setMockedLocations(config: LocationMockConfig): void`
- `enableReverseGeocodingMock(): void`
- `disableReverseGeocodingMock(): void`
- `setReverseGeocodingMockInfo(mockInfos: Array<ReverseGeocodingMockInfo>): void`
- `isLocationPrivacyConfirmed(type: LocationPrivacyType): boolean`
- `setLocationPrivacyConfirmStatus(type: LocationPrivacyType, isConfirmed: boolean): void`
- `getLocatingRequiredData(config: LocatingRequiredDataConfig): Promise<Array<LocatingRequiredData>>`

### geolocation (@ohos.geolocation.d.ts)
#### Interfaces
- **SatelliteStatusInfo**
  - `satellitesNumber`: number
  - `satelliteIds`: Array<number>
  - `carrierToNoiseDensitys`: Array<number>
  - `altitudes`: Array<number>
  - `azimuths`: Array<number>
  - `carrierFrequencies`: Array<number>
- **CachedGnssLocationsRequest**
  - `reportingPeriodSec`: number
  - `wakeUpCacheQueueFull`: boolean
- **GeofenceRequest**
  - `priority`: LocationRequestPriority
  - `scenario`: LocationRequestScenario
  - `geofence`: Geofence
- **Geofence**
  - `latitude`: number
  - `longitude`: number
  - `radius`: number
  - `expiration`: number
- **ReverseGeoCodeRequest**
  - `locale?`: string
  - `latitude`: number
  - `longitude`: number
  - `maxItems?`: number
- **GeoCodeRequest**
  - `locale?`: string
  - `description`: string
  - `maxItems?`: number
  - `minLatitude?`: number
  - `minLongitude?`: number
  - `maxLatitude?`: number
  - `maxLongitude?`: number
- **GeoAddress**
  - `latitude?`: number
  - `longitude?`: number
  - `locale?`: string
  - `placeName?`: string
  - `countryCode?`: string
  - `countryName?`: string
  - `administrativeArea?`: string
  - `subAdministrativeArea?`: string
  - `locality?`: string
  - `subLocality?`: string
  - `roadName?`: string
  - `subRoadName?`: string
  - `premises?`: string
  - `postalCode?`: string
  - `phoneNumber?`: string
  - `addressUrl?`: string
  - `descriptions?`: Array<string>
  - `descriptionsSize?`: number
- **LocationRequest**
  - `priority?`: LocationRequestPriority
  - `scenario?`: LocationRequestScenario
  - `timeInterval?`: number
  - `distanceInterval?`: number
  - `maxAccuracy?`: number
- **CurrentLocationRequest**
  - `priority?`: LocationRequestPriority
  - `scenario?`: LocationRequestScenario
  - `maxAccuracy?`: number
  - `timeoutMs?`: number
- **Location**
  - `latitude`: number
  - `longitude`: number
  - `altitude`: number
  - `accuracy`: number
  - `speed`: number
  - `timeStamp`: number
  - `direction`: number
  - `timeSinceBoot`: number
  - `additions?`: Array<string>
  - `additionSize?`: number
- **LocationCommand**
  - `scenario`: LocationRequestScenario
  - `command`: string
#### Enums
- **LocationRequestPriority**
  - `UNSET` = 0x200
- **LocationRequestScenario**
  - `UNSET` = 0x300
- **GeoLocationErrorCode**
- **LocationPrivacyType**
  - `OTHERS` = 0
#### Functions
- `on(type: 'locationChange', request: LocationRequest, callback: Callback<Location>): void`
- `off(type: 'locationChange', callback?: Callback<Location>): void`
- `on(type: 'locationServiceState', callback: Callback<boolean>): void`
- `off(type: 'locationServiceState', callback?: Callback<boolean>): void`
- `on(type: 'cachedGnssLocationsReporting', request: CachedGnssLocationsRequest, callback: Callback<Array<Location>>): void`
- `off(type: 'cachedGnssLocationsReporting', callback?: Callback<Array<Location>>): void`
- `on(type: 'gnssStatusChange', callback: Callback<SatelliteStatusInfo>): void`
- `off(type: 'gnssStatusChange', callback?: Callback<SatelliteStatusInfo>): void`
- `on(type: 'nmeaMessageChange', callback: Callback<string>): void`
- `off(type: 'nmeaMessageChange', callback?: Callback<string>): void`
- `on(type: 'fenceStatusChange', request: GeofenceRequest, want: WantAgent): void`
- `off(type: 'fenceStatusChange', request: GeofenceRequest, want: WantAgent): void`
- `getCurrentLocation(request: CurrentLocationRequest, callback: AsyncCallback<Location>): void`
- `getCurrentLocation(callback: AsyncCallback<Location>): void`
- `getCurrentLocation(request?: CurrentLocationRequest): Promise<Location>`
- `getLastLocation(callback: AsyncCallback<Location>): void`
- `getLastLocation(): Promise<Location>`
- `isLocationEnabled(callback: AsyncCallback<boolean>): void`
- `isLocationEnabled(): Promise<boolean>`
- `requestEnableLocation(callback: AsyncCallback<boolean>): void`
- `requestEnableLocation(): Promise<boolean>`
- `getAddressesFromLocation(request: ReverseGeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void`
- `getAddressesFromLocation(request: ReverseGeoCodeRequest): Promise<Array<GeoAddress>>`
- `getAddressesFromLocationName(request: GeoCodeRequest, callback: AsyncCallback<Array<GeoAddress>>): void`
- `getAddressesFromLocationName(request: GeoCodeRequest): Promise<Array<GeoAddress>>`
- `isGeoServiceAvailable(callback: AsyncCallback<boolean>): void`
- `isGeoServiceAvailable(): Promise<boolean>`
- `getCachedGnssLocationsSize(callback: AsyncCallback<number>): void`
- `getCachedGnssLocationsSize(): Promise<number>`
- `flushCachedGnssLocations(callback: AsyncCallback<boolean>): void`
- `flushCachedGnssLocations(): Promise<boolean>`
- `sendCommand(command: LocationCommand, callback: AsyncCallback<boolean>): void`
- `sendCommand(command: LocationCommand): Promise<boolean>`

### colorSpaceManager (@ohos.graphics.colorSpaceManager.d.ts)
#### Interfaces
- **ColorSpacePrimaries**
  - `redX`: number
  - `redY`: number
  - `greenX`: number
  - `greenY`: number
  - `blueX`: number
  - `blueY`: number
  - `whitePointX`: number
  - `whitePointY`: number
- **ColorSpaceManager**
  - `getColorSpaceName`: ColorSpace
  - `getWhitePoint`: Array<number>
  - `getGamma`: number
#### Enums
- **ColorSpace**
  - `UNKNOWN` = 0
  - `ADOBE_RGB_1998` = 1
  - `DCI_P3` = 2
  - `DISPLAY_P3` = 3
  - `SRGB` = 4
  - `BT709` = 6
  - `BT601_EBU` = 7
  - `BT601_SMPTE_C` = 8
  - `BT2020_HLG` = 9
  - `BT2020_PQ` = 10
  - `P3_HLG` = 11
  - `P3_PQ` = 12
  - `ADOBE_RGB_1998_LIMIT` = 13
  - `DISPLAY_P3_LIMIT` = 14
  - `SRGB_LIMIT` = 15
  - `BT709_LIMIT` = 16
  - `BT601_EBU_LIMIT` = 17
  - `BT601_SMPTE_C_LIMIT` = 18
  - `BT2020_HLG_LIMIT` = 19
  - `BT2020_PQ_LIMIT` = 20
  - `P3_HLG_LIMIT` = 21
  - `P3_PQ_LIMIT` = 22
  - `LINEAR_P3` = 23
  - `LINEAR_SRGB` = 24
  - `LINEAR_BT709` = LINEAR_SRGB
  - `LINEAR_BT2020` = 25
  - `DISPLAY_SRGB` = SRGB
  - `DISPLAY_P3_SRGB` = DISPLAY_P3
  - `DISPLAY_P3_HLG` = P3_HLG
  - `DISPLAY_P3_PQ` = P3_PQ
  - `CUSTOM` = 5
#### Functions
- `create(colorSpaceName: ColorSpace): ColorSpaceManager`
- `create(primaries: ColorSpacePrimaries, gamma: number): ColorSpaceManager`

### common2D (@ohos.graphics.common2D.d.ts)
#### Interfaces
- **Color**
  - `alpha`: number
  - `red`: number
  - `green`: number
  - `blue`: number
- **Rect**
  - `left`: number
  - `top`: number
  - `right`: number
  - `bottom`: number

### displaySync (@ohos.graphics.displaySync.d.ts)
#### Interfaces
- **IntervalInfo**
  - `timestamp`: number
  - `targetTimestamp`: number
- **DisplaySync**
  - `setExpectedFrameRateRange`: void
  - `on`: void
  - `off`: void
  - `start`: void
  - `stop`: void
#### Functions
- `create(): DisplaySync`

### drawing (@ohos.graphics.drawing.d.ts)
#### Interfaces
- **TextBlobRunBuffer**
  - `glyph`: number
  - `positionX`: number
  - `positionY`: number
- **FontMetrics**
  - `top`: number
  - `ascent`: number
  - `descent`: number
  - `bottom`: number
  - `leading`: number
#### Enums
- **BlendMode**
  - `CLEAR` = 0
  - `SRC` = 1
  - `DST` = 2
  - `SRC_OVER` = 3
  - `DST_OVER` = 4
  - `SRC_IN` = 5
  - `DST_IN` = 6
  - `SRC_OUT` = 7
  - `DST_OUT` = 8
  - `SRC_ATOP` = 9
  - `DST_ATOP` = 10
  - `XOR` = 11
  - `PLUS` = 12
  - `MODULATE` = 13
  - `SCREEN` = 14
  - `OVERLAY` = 15
  - `DARKEN` = 16
  - `LIGHTEN` = 17
  - `COLOR_DODGE` = 18
  - `COLOR_BURN` = 19
  - `HARD_LIGHT` = 20
  - `SOFT_LIGHT` = 21
  - `DIFFERENCE` = 22
  - `EXCLUSION` = 23
  - `MULTIPLY` = 24
  - `HUE` = 25
  - `SATURATION` = 26
  - `COLOR` = 27
  - `LUMINOSITY` = 28
- **TextEncoding**
  - `TEXT_ENCODING_UTF8` = 0
  - `TEXT_ENCODING_UTF16` = 1
  - `TEXT_ENCODING_UTF32` = 2
  - `TEXT_ENCODING_GLYPH_ID` = 3
#### Classes
- **Path**
  - `moveTo()`: void
  - `lineTo()`: void
  - `arcTo()`: void
  - `quadTo()`: void
  - `cubicTo()`: void
  - `close()`: void
  - `reset()`: void
- **Canvas**
  - `drawRect()`: void
  - `drawCircle()`: void
  - `drawImage()`: void
  - `drawColor()`: void
  - `drawPoint()`: void
  - `drawPath()`: void
  - `drawLine()`: void
  - `drawTextBlob()`: void
  - `attachPen()`: void
  - `attachBrush()`: void
  - `detachPen()`: void
  - `detachBrush()`: void
- **TextBlob**
  - `makeFromString()`: TextBlob
  - `makeFromRunBuffer()`: TextBlob
  - `bounds()`: common2D.Rect
- **Typeface**
  - `getFamilyName()`: string
- **Font**
  - `enableSubpixel()`: void
  - `enableEmbolden()`: void
  - `enableLinearMetrics()`: void
  - `setSize()`: void
  - `getSize()`: number
  - `setTypeface()`: void
  - `getTypeface()`: Typeface
  - `getMetrics()`: FontMetrics
  - `measureText()`: number
- **ColorFilter**
  - `createBlendModeColorFilter()`: ColorFilter
  - `createComposeColorFilter()`: ColorFilter
  - `createLinearToSRGBGamma()`: ColorFilter
  - `createSRGBGammaToLinear()`: ColorFilter
  - `createLumaColorFilter()`: ColorFilter
- **Pen**
  - `setColor()`: void
  - `setStrokeWidth()`: void
  - `setAntiAlias()`: void
  - `setAlpha()`: void
  - `setColorFilter()`: void
  - `setBlendMode()`: void
  - `setDither()`: void
- **Brush**
  - `setColor()`: void
  - `setAntiAlias()`: void
  - `setAlpha()`: void
  - `setColorFilter()`: void
  - `setBlendMode()`: void

### hdrCapability (@ohos.graphics.hdrCapability.d.ts)
#### Enums
- **HDRFormat**
  - `NONE` = 0
  - `VIDEO_HLG` = 1
  - `VIDEO_HDR10` = 2
  - `VIDEO_HDR_VIVID` = 3
  - `IMAGE_HDR_VIVID_DUAL` = 4
  - `IMAGE_HDR_VIVID_SINGLE` = 5
  - `IMAGE_HDR_ISO_DUAL` = 6
  - `IMAGE_HDR_ISO_SINGLE` = 7

### hiAppEvent (@ohos.hiAppEvent.d.ts)
#### Interfaces
- **ConfigOption**
  - `disable?`: boolean
  - `maxStorage?`: string
#### Enums
- **EventType**
  - `FAULT` = 1
  - `STATISTIC` = 2
  - `SECURITY` = 3
  - `BEHAVIOR` = 4
#### Functions
- `write(eventName: string, eventType: EventType, keyValues: object): Promise<void>`
- `write(eventName: string, eventType: EventType, keyValues: object, callback: AsyncCallback<void>): void`
- `configure(config: ConfigOption): boolean`

### hiSysEvent (@ohos.hiSysEvent.d.ts)
#### Interfaces
- **SysEventInfo**
  - `domain`: string
  - `name`: string
  - `eventType`: EventType
  - `params`: object
- **WatchRule**
  - `domain`: string
  - `name`: string
  - `tag`: string
  - `ruleType`: RuleType
- **Watcher**
  - `rules`: WatchRule[]
  - `onEvent`: (info: SysEventInfo) => void
  - `onServiceDied`: () => void
- **QueryArg**
  - `beginTime`: number
  - `endTime`: number
  - `maxEvents`: number
  - `fromSeq?`: number
  - `toSeq?`: number
- **QueryRule**
  - `domain`: string
  - `names`: string[]
  - `condition?`: string
- **Querier**
  - `onQuery`: (infos: SysEventInfo[]) => void
  - `onComplete`: (reason: number, total: number) => void
#### Enums
- **EventType**
  - `FAULT` = 1
  - `STATISTIC` = 2
  - `SECURITY` = 3
  - `BEHAVIOR` = 4
- **RuleType**
  - `WHOLE_WORD` = 1
  - `PREFIX` = 2
  - `REGULAR` = 3
#### Functions
- `write(info: SysEventInfo): Promise<void>`
- `write(info: SysEventInfo, callback: AsyncCallback<void>): void`
- `addWatcher(watcher: Watcher): void`
- `removeWatcher(watcher: Watcher): void`
- `query(queryArg: QueryArg, rules: QueryRule[], querier: Querier): void`
- `exportSysEvents(queryArg: QueryArg, rules: QueryRule[]): number`
- `subscribe(rules: QueryRule[]): number`
- `unsubscribe(): void`

### hiTraceChain (@ohos.hiTraceChain.d.ts)
#### Interfaces
- **HiTraceId**
  - `chainId`: bigint
  - `spanId?`: number
  - `parentSpanId?`: number
  - `flags?`: number
#### Enums
- **HiTraceFlag**
  - `DEFAULT` = 0
  - `INCLUDE_ASYNC` = 1
  - `DONOT_CREATE_SPAN` = 1 << 1
  - `TP_INFO` = 1 << 2
  - `NO_BE_INFO` = 1 << 3
  - `DISABLE_LOG` = 1 << 4
  - `FAILURE_TRIGGER` = 1 << 5
  - `D2D_TP_INFO` = 1 << 6
- **HiTraceTracepointType**
  - `CS` = 0
  - `CR` = 1
  - `SS` = 2
  - `SR` = 3
  - `GENERAL` = 4
- **HiTraceCommunicationMode**
  - `DEFAULT` = 0
  - `THREAD` = 1
  - `PROCESS` = 2
  - `DEVICE` = 3
#### Functions
- `begin(name: string, flags?: number): HiTraceId`
- `end(id: HiTraceId): void`
- `getId(): HiTraceId`
- `setId(id: HiTraceId): void`
- `clearId(): void`
- `createSpan(): HiTraceId`
- `tracepoint(mode: HiTraceCommunicationMode, type: HiTraceTracepointType, id: HiTraceId, msg?: string): void`
- `isValid(id: HiTraceId): boolean`
- `isFlagEnabled(id: HiTraceId, flag: HiTraceFlag): boolean`
- `enableFlag(id: HiTraceId, flag: HiTraceFlag): void`

### hiTraceMeter (@ohos.hiTraceMeter.d.ts)
#### Functions
- `startTrace(name: string, taskId: number): void`
- `finishTrace(name: string, taskId: number): void`
- `traceByValue(name: string, count: number): void`

### hichecker (@ohos.hichecker.d.ts)
#### Functions
- `addRule(rule: bigint): void`
- `removeRule(rule: bigint): void`
- `getRule(): bigint`
- `contains(rule: bigint): boolean`
- `addCheckRule(rule: bigint): void`
- `removeCheckRule(rule: bigint): void`
- `containsCheckRule(rule: bigint): boolean`

### hidebug (@ohos.hidebug.d.ts)
#### Functions
- `getNativeHeapSize(): bigint`
- `getNativeHeapAllocatedSize(): bigint`
- `getNativeHeapFreeSize(): bigint`
- `getVss(): bigint`
- `getPss(): bigint`
- `getSharedDirty(): bigint`
- `getPrivateDirty(): bigint`
- `getCpuUsage(): number`
- `startProfiling(filename: string): void`
- `stopProfiling(): void`
- `dumpHeapData(filename: string): void`
- `startJsCpuProfiling(filename: string): void`
- `stopJsCpuProfiling(): void`
- `dumpJsHeapData(filename: string): void`
- `getServiceDump(serviceid: number, fd: number, args: Array<string>): void`

### hilog (@ohos.hilog.d.ts)
#### Enums
- **LogLevel**
  - `DEBUG` = 3
  - `INFO` = 4
  - `WARN` = 5
  - `ERROR` = 6
  - `FATAL` = 7
#### Functions
- `debug(domain: number, tag: string, format: string, ...args: any[]): void`
- `info(domain: number, tag: string, format: string, ...args: any[]): void`
- `warn(domain: number, tag: string, format: string, ...args: any[]): void`
- `error(domain: number, tag: string, format: string, ...args: any[]): void`
- `fatal(domain: number, tag: string, format: string, ...args: any[]): void`
- `isLoggable(domain: number, tag: string, level: LogLevel): boolean`

### hiAppEvent (@ohos.hiviewdfx.hiAppEvent.d.ts)
#### Interfaces
- **ConfigOption**
  - `disable?`: boolean
  - `maxStorage?`: string
- **AppEventInfo**
  - `domain`: string
  - `name`: string
  - `eventType`: EventType
  - `params`: object
- **AppEventPackage**
  - `packageId`: number
  - `row`: number
  - `size`: number
  - `data`: string[]
- **TriggerCondition**
  - `row?`: number
  - `size?`: number
  - `timeOut?`: number
- **AppEventFilter**
  - `domain`: string
  - `eventTypes?`: EventType[]
  - `names?`: string[]
- **AppEventGroup**
  - `name`: string
  - `appEventInfos`: Array<AppEventInfo>
- **Watcher**
  - `name`: string
  - `triggerCondition?`: TriggerCondition
  - `appEventFilters?`: AppEventFilter[]
  - `onTrigger?`: (curRow: number, curSize: number, holder: AppEventPackageHolder) => void
  - `onReceive?`: (domain: string, appEventGroups: Array<AppEventGroup>) => void
- **AppEventReportConfig**
  - `domain?`: string
  - `name?`: string
  - `isRealTime?`: boolean
- **Processor**
  - `name`: string
  - `debugMode?`: boolean
  - `routeInfo?`: string
  - `appId?`: string
  - `onStartReport?`: boolean
  - `onBackgroundReport?`: boolean
  - `periodReport?`: number
  - `batchReport?`: number
  - `userIds?`: string[]
  - `userProperties?`: string[]
  - `eventConfigs?`: AppEventReportConfig[]
#### Enums
- **EventType**
  - `FAULT` = 1
  - `STATISTIC` = 2
  - `SECURITY` = 3
  - `BEHAVIOR` = 4
#### Functions
- `configure(config: ConfigOption): void`
- `write(info: AppEventInfo): Promise<void>`
- `write(info: AppEventInfo, callback: AsyncCallback<void>): void`
- `addWatcher(watcher: Watcher): AppEventPackageHolder`
- `removeWatcher(watcher: Watcher): void`
- `clearData(): void`
- `setUserId(name: string, value: string): void`
- `getUserId(name: string): string`
- `setUserProperty(name: string, value: string): void`
- `getUserProperty(name: string): string`
- `addProcessor(processor: Processor): number`
- `removeProcessor(id: number): void`
#### Classes
- **AppEventPackageHolder**
  - `setSize()`: void
  - `takeNext()`: AppEventPackage

### i18n (@ohos.i18n.d.ts)
#### Interfaces
- **Util**
  - `unitConvert`: string
- **UnitInfo**
  - `unit`: string
  - `measureSystem`: string
- **PhoneNumberFormatOptions**
  - `type?`: string
- **SortOptions**
  - `locale?`: string
  - `isUseLocalName?`: boolean
  - `isSuggestedFirst?`: boolean
- **LocaleItem**
  - `id`: string
  - `suggestionType`: SuggestionType
  - `displayName`: string
  - `localName?`: string
- **TimeZoneCityItem**
  - `zoneId`: string
  - `cityId`: string
  - `cityDisplayName`: string
  - `offset`: number
  - `zoneDisplayName`: string
  - `rawOffset?`: number
- **HolidayInfoItem**
  - `baseName`: string
  - `year`: number
  - `month`: number
  - `day`: number
  - `localNames?`: Array<HolidayLocalName>
- **HolidayLocalName**
  - `language`: string
  - `name`: string
- **EntityInfoItem**
  - `begin`: number
  - `end`: number
  - `type`: string
#### Enums
- **NormalizerMode**
  - `NFC` = 1
  - `NFD` = 2
  - `NFKC` = 3
  - `NFKD` = 4
- **SuggestionType**
  - `SUGGESTION_TYPE_NONE` = 0
  - `SUGGESTION_TYPE_RELATED` = 1
  - `SUGGESTION_TYPE_SIM` = 2
#### Functions
- `getDisplayCountry(country: string, locale: string, sentenceCase?: boolean): string`
- `getDisplayLanguage(language: string, locale: string, sentenceCase?: boolean): string`
- `getSystemLanguage(): string`
- `getSystemRegion(): string`
- `getSystemLocale(): string`
- `getCalendar(locale: string, type?: string): Calendar`
- `isRTL(locale: string): boolean`
- `getLineInstance(locale: string): BreakIterator`
- `getInstance(locale?: string): IndexUtil`
- `is24HourClock(): boolean`
- `set24HourClock(option: boolean): boolean`
- `addPreferredLanguage(language: string, index?: number): boolean`
- `removePreferredLanguage(index: number): boolean`
- `getPreferredLanguageList(): Array<string>`
- `getFirstPreferredLanguage(): string`
- `getTimeZone(zoneID?: string): TimeZone`
#### Classes
- **System**
  - `getDisplayCountry()`: string
  - `getDisplayLanguage()`: string
  - `getSystemLanguages()`: Array<string>
  - `getSystemCountries()`: Array<string>
  - `isSuggested()`: boolean
  - `getSystemLanguage()`: string
  - `setSystemLanguage()`: void
  - `getSystemRegion()`: string
  - `setSystemRegion()`: void
  - `getSystemLocale()`: string
  - `setSystemLocale()`: void
  - `is24HourClock()`: boolean
  - `set24HourClock()`: void
  - `addPreferredLanguage()`: void
  - `removePreferredLanguage()`: void
  - `getPreferredLanguageList()`: Array<string>
  - `getFirstPreferredLanguage()`: string
  - `setAppPreferredLanguage()`: void
  - `getAppPreferredLanguage()`: string
  - `setUsingLocalDigit()`: void
  - `getUsingLocalDigit()`: boolean
- **I18NUtil**
  - `unitConvert()`: string
  - `getDateOrder()`: string
  - `getTimePeriodName()`: string
- **PhoneNumberFormat**
  - `isValidNumber()`: boolean
  - `format()`: string
  - `getLocationName()`: string
- **Calendar**
  - `setTime()`: void
  - `setTime()`: void
  - `set()`: void
  - `setTimeZone()`: void
  - `getTimeZone()`: string
  - `getFirstDayOfWeek()`: number
  - `setFirstDayOfWeek()`: void
  - `getMinimalDaysInFirstWeek()`: number
  - `setMinimalDaysInFirstWeek()`: void
  - `get()`: number
  - `getDisplayName()`: string
  - `isWeekend()`: boolean
  - `add()`: void
  - `getTimeInMillis()`: number
  - `compareDays()`: number
- **BreakIterator**
  - `current()`: number
  - `first()`: number
  - `last()`: number
  - `next()`: number
  - `previous()`: number
  - `setLineBreakText()`: void
  - `following()`: number
  - `getLineBreakText()`: string
  - `isBoundary()`: boolean
- **IndexUtil**
  - `getIndexList()`: Array<string>
  - `addLocale()`: void
  - `getIndex()`: string
- **Character**
  - `isDigit()`: boolean
  - `isSpaceChar()`: boolean
  - `isWhitespace()`: boolean
  - `isRTL()`: boolean
  - `isIdeograph()`: boolean
  - `isLetter()`: boolean
  - `isLowerCase()`: boolean
  - `isUpperCase()`: boolean
  - `getType()`: string
- **Unicode**
  - `isDigit()`: boolean
  - `isSpaceChar()`: boolean
  - `isWhitespace()`: boolean
  - `isRTL()`: boolean
  - `isIdeograph()`: boolean
  - `isLetter()`: boolean
  - `isLowerCase()`: boolean
  - `isUpperCase()`: boolean
  - `getType()`: string
- **TimeZone**
  - `getID()`: string
  - `getDisplayName()`: string
  - `getRawOffset()`: number
  - `getOffset()`: number
  - `getAvailableIDs()`: Array<string>
  - `getAvailableZoneCityIDs()`: Array<string>
  - `getCityDisplayName()`: string
  - `getTimezoneFromCity()`: TimeZone
  - `getTimezonesByLocation()`: Array<TimeZone>
- **Transliterator**
  - `getAvailableIDs()`: string[]
  - `getInstance()`: Transliterator
  - `transform()`: string
- **Normalizer**
  - `getInstance()`: Normalizer
  - `normalize()`: string
- **SystemLocaleManager**
  - `getLanguageInfoArray()`: Array<LocaleItem>
  - `getRegionInfoArray()`: Array<LocaleItem>
  - `getTimeZoneCityItemArray()`: Array<TimeZoneCityItem>
- **HolidayManager**
  - `isHoliday()`: boolean
  - `getHolidayInfoItemArray()`: Array<HolidayInfoItem>
- **EntityRecognizer**
  - `findEntityInfo()`: Array<EntityInfoItem>

### identifier (@ohos.identifier.oaid.d.ts)
#### Functions
- `getOAID(callback: AsyncCallback<string>): void`
- `getOAID(): Promise<string>`
- `resetOAID(): void`

### @ohos.inputMethod.Panel (@ohos.inputMethod.Panel.d.ts)
#### Interfaces
- **PanelInfo**
  - `type`: PanelType
  - `flag?`: PanelFlag
#### Enums
- **PanelFlag**
  - `FLAG_FIXED` = 0
- **PanelType**
  - `SOFT_KEYBOARD` = 0

### inputMethod (@ohos.inputMethod.d.ts)
#### Interfaces
- **InputMethodSetting**
  - `isPanelShown`: boolean
  - `listInputMethodSubtype`: void
  - `listInputMethodSubtype`: Promise<Array<InputMethodSubtype>>
  - `listCurrentInputMethodSubtype`: void
  - `listCurrentInputMethodSubtype`: Promise<Array<InputMethodSubtype>>
  - `getInputMethods`: void
  - `getInputMethods`: Promise<Array<InputMethodProperty>>
  - `getInputMethodsSync`: Array<InputMethodProperty>
  - `getAllInputMethods`: void
  - `getAllInputMethods`: Promise<Array<InputMethodProperty>>
  - `getAllInputMethodsSync`: Array<InputMethodProperty>
  - `listInputMethod`: void
  - `listInputMethod`: Promise<Array<InputMethodProperty>>
  - `showOptionalInputMethods`: void
  - `showOptionalInputMethods`: Promise<boolean>
  - `displayOptionalInputMethod`: void
  - `displayOptionalInputMethod`: Promise<void>
- **InputMethodController**
  - `attach`: void
  - `attach`: Promise<void>
  - `showTextInput`: void
  - `showTextInput`: Promise<void>
  - `hideTextInput`: void
  - `hideTextInput`: Promise<void>
  - `detach`: void
  - `detach`: Promise<void>
  - `setCallingWindow`: void
  - `setCallingWindow`: Promise<void>
  - `updateCursor`: void
  - `updateCursor`: Promise<void>
  - `changeSelection`: void
  - `changeSelection`: Promise<void>
  - `updateAttribute`: void
  - `updateAttribute`: Promise<void>
  - `stopInputSession`: void
  - `stopInputSession`: Promise<boolean>
  - `stopInput`: void
  - `stopInput`: Promise<boolean>
  - `showSoftKeyboard`: void
  - `showSoftKeyboard`: Promise<void>
  - `hideSoftKeyboard`: void
  - `hideSoftKeyboard`: Promise<void>
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **InputMethodProperty**
  - `readonly packageName`: string
  - `readonly methodId`: string
  - `readonly name`: string
  - `readonly id`: string
  - `readonly label?`: string
  - `readonly labelId?`: number
  - `readonly icon?`: string
  - `readonly iconId?`: number
  - `extra?`: object
- **Range**
  - `start`: number
  - `end`: number
- **Movement**
  - `direction`: Direction
- **InputAttribute**
  - `textInputType`: TextInputType
  - `enterKeyType`: EnterKeyType
- **FunctionKey**
  - `enterKeyType`: EnterKeyType
- **CursorInfo**
  - `left`: number
  - `top`: number
  - `width`: number
  - `height`: number
- **TextConfig**
  - `inputAttribute`: InputAttribute
  - `cursorInfo?`: CursorInfo
  - `selection?`: Range
  - `windowId?`: number
- **InputWindowInfo**
  - `name`: string
  - `left`: number
  - `top`: number
  - `width`: number
  - `height`: number
#### Enums
- **Direction**
  - `CURSOR_UP` = 1
- **TextInputType**
  - `NONE` = -1
  - `TEXT` = 0
- **EnterKeyType**
  - `UNSPECIFIED` = 0
- **KeyboardStatus**
  - `NONE` = 0
  - `HIDE` = 1
  - `SHOW` = 2
- **ExtendAction**
  - `SELECT_ALL` = 0
  - `CUT` = 3
  - `COPY` = 4
  - `PASTE` = 5
#### Functions
- `getInputMethodSetting(): InputMethodSetting`
- `getInputMethodController(): InputMethodController`
- `getSetting(): InputMethodSetting`
- `getController(): InputMethodController`
- `getDefaultInputMethod(): InputMethodProperty`
- `getSystemInputMethodConfigAbility(): ElementName`
- `switchInputMethod(target: InputMethodProperty, callback: AsyncCallback<boolean>): void`
- `switchInputMethod(target: InputMethodProperty): Promise<boolean>`
- `getCurrentInputMethod(): InputMethodProperty`
- `switchCurrentInputMethodSubtype(target: InputMethodSubtype, callback: AsyncCallback<boolean>): void`
- `switchCurrentInputMethodSubtype(target: InputMethodSubtype): Promise<boolean>`
- `getCurrentInputMethodSubtype(): InputMethodSubtype`
- `switchCurrentInputMethodAndSubtype(
    inputMethodProperty: InputMethodProperty,
    inputMethodSubtype: InputMethodSubtype,
    callback: AsyncCallback<boolean>
  ): void`
- `switchCurrentInputMethodAndSubtype(
    inputMethodProperty: InputMethodProperty,
    inputMethodSubtype: InputMethodSubtype
  ): Promise<boolean>`
- `switchInputMethod(bundleName: string, subtypeId?: string): Promise<void>`

### inputMethodEngine (@ohos.inputMethodEngine.d.ts)
#### Interfaces
- **KeyboardController**
  - `hide`: void
  - `hide`: Promise<void>
  - `hideKeyboard`: void
  - `hideKeyboard`: Promise<void>
  - `exitCurrentInputType`: void
  - `exitCurrentInputType`: Promise<void>
- **InputMethodEngine**
- **InputMethodAbility**
  - `on`: void
  - `off`: void
  - `getSecurityMode`: SecurityMode
  - `createPanel`: void
  - `createPanel`: Promise<Panel>
  - `destroyPanel`: void
  - `destroyPanel`: Promise<void>
- **TextInputClient**
  - `sendKeyFunction`: void
  - `sendKeyFunction`: Promise<boolean>
  - `deleteForward`: void
  - `deleteForward`: Promise<boolean>
  - `deleteBackward`: void
  - `deleteBackward`: Promise<boolean>
  - `insertText`: void
  - `insertText`: Promise<boolean>
  - `getForward`: void
  - `getForward`: Promise<string>
  - `getBackward`: void
  - `getBackward`: Promise<string>
  - `getEditorAttribute`: void
  - `getEditorAttribute`: Promise<EditorAttribute>
- **InputClient**
  - `sendKeyFunction`: void
  - `sendKeyFunction`: Promise<boolean>
  - `deleteForward`: void
  - `deleteForward`: Promise<boolean>
  - `deleteForwardSync`: void
  - `deleteBackward`: void
  - `deleteBackward`: Promise<boolean>
  - `deleteBackwardSync`: void
  - `insertText`: void
  - `insertText`: Promise<boolean>
  - `insertTextSync`: void
  - `getForward`: void
  - `getForward`: Promise<string>
  - `getForwardSync`: string
  - `getBackward`: void
  - `getBackward`: Promise<string>
  - `getBackwardSync`: string
  - `getEditorAttribute`: void
  - `getEditorAttribute`: Promise<EditorAttribute>
  - `getEditorAttributeSync`: EditorAttribute
  - `moveCursor`: void
  - `moveCursor`: Promise<void>
  - `moveCursorSync`: void
  - `selectByRange`: void
  - `selectByRange`: Promise<void>
  - `selectByRangeSync`: void
  - `selectByMovement`: void
  - `selectByMovement`: Promise<void>
  - `selectByMovementSync`: void
  - `getTextIndexAtCursor`: void
  - `getTextIndexAtCursor`: Promise<number>
  - `getTextIndexAtCursorSync`: number
  - `sendExtendAction`: void
  - `sendExtendAction`: Promise<void>
- **KeyboardDelegate**
- **Panel**
  - `setUiContent`: void
  - `setUiContent`: Promise<void>
  - `setUiContent`: void
  - `setUiContent`: Promise<void>
  - `resize`: void
  - `resize`: Promise<void>
  - `moveTo`: void
  - `moveTo`: Promise<void>
  - `show`: void
  - `show`: Promise<void>
  - `hide`: void
  - `hide`: Promise<void>
  - `changeFlag`: void
  - `setPrivacyMode`: void
- **EditorAttribute**
  - `readonly inputPattern`: number
  - `readonly enterKeyType`: number
- **KeyEvent**
  - `readonly keyCode`: number
  - `readonly keyAction`: number
- **PanelInfo**
  - `type`: PanelType
  - `flag?`: PanelFlag
- **Range**
  - `start`: number
  - `end`: number
- **Movement**
  - `direction`: Direction
#### Enums
- **PanelFlag**
  - `FLG_FIXED` = 0
- **PanelType**
  - `SOFT_KEYBOARD` = 0
- **Direction**
  - `CURSOR_UP` = 1
- **SecurityMode**
  - `BASIC` = 0
- **ExtendAction**
  - `SELECT_ALL` = 0
  - `CUT` = 3
  - `COPY` = 4
  - `PASTE` = 5
#### Functions
- `getInputMethodAbility(): InputMethodAbility`
- `getInputMethodEngine(): InputMethodEngine`
- `getKeyboardDelegate(): KeyboardDelegate`
- `createKeyboardDelegate(): KeyboardDelegate`

### @ohos.inputMethodList (@ohos.inputMethodList.d.ets)
#### Interfaces
- **PatternOptions**
  - `defaultSelected?`: number
  - `patterns`: Array<Pattern>
  - `action`: (index: number) => void
- **Pattern**
  - `icon`: Resource
  - `selectedIcon`: Resource

### intl (@ohos.intl.d.ts)
#### Interfaces
- **LocaleOptions**
  - `calendar?`: string
  - `collation?`: string
  - `hourCycle?`: string
  - `numberingSystem?`: string
  - `numeric?`: boolean
  - `caseFirst?`: string
- **DateTimeOptions**
  - `locale?`: string
  - `dateStyle?`: string
  - `timeStyle?`: string
  - `hourCycle?`: string
  - `timeZone?`: string
  - `numberingSystem?`: string
  - `hour12?`: boolean
  - `weekday?`: string
  - `era?`: string
  - `year?`: string
  - `month?`: string
  - `day?`: string
  - `hour?`: string
  - `minute?`: string
  - `second?`: string
  - `timeZoneName?`: string
  - `dayPeriod?`: string
  - `localeMatcher?`: string
  - `formatMatcher?`: string
- **NumberOptions**
  - `locale?`: string
  - `currency?`: string
  - `currencySign?`: string
  - `currencyDisplay?`: string
  - `unit?`: string
  - `unitDisplay?`: string
  - `unitUsage?`: string
  - `signDisplay?`: string
  - `compactDisplay?`: string
  - `notation?`: string
  - `localeMatcher?`: string
  - `style?`: string
  - `numberingSystem?`: string
  - `useGrouping?`: boolean
  - `minimumIntegerDigits?`: number
  - `minimumFractionDigits?`: number
  - `maximumFractionDigits?`: number
  - `minimumSignificantDigits?`: number
  - `maximumSignificantDigits?`: number
- **CollatorOptions**
  - `localeMatcher?`: string
  - `usage?`: string
  - `sensitivity?`: string
  - `ignorePunctuation?`: boolean
  - `collation?`: string
  - `numeric?`: boolean
  - `caseFirst?`: string
- **PluralRulesOptions**
  - `localeMatcher?`: string
  - `type?`: string
  - `minimumIntegerDigits?`: number
  - `minimumFractionDigits?`: number
  - `maximumFractionDigits?`: number
  - `minimumSignificantDigits?`: number
  - `maximumSignificantDigits?`: number
- **RelativeTimeFormatInputOptions**
  - `localeMatcher?`: string
  - `numeric?`: string
  - `style?`: string
- **RelativeTimeFormatResolvedOptions**
  - `locale`: string
  - `style`: string
  - `numeric`: string
  - `numberingSystem`: string
#### Classes
- **Locale**
  - `toString()`: string
  - `maximize()`: Locale
  - `minimize()`: Locale
  - `language`: string
  - `script`: string
  - `region`: string
  - `baseName`: string
  - `caseFirst`: string
  - `calendar`: string
  - `collation`: string
  - `hourCycle`: string
  - `numberingSystem`: string
  - `numeric`: boolean
- **DateTimeFormat**
  - `format()`: string
  - `formatRange()`: string
  - `resolvedOptions()`: DateTimeOptions
- **NumberFormat**
  - `format()`: string
  - `resolvedOptions()`: NumberOptions
- **Collator**
  - `compare()`: number
  - `resolvedOptions()`: CollatorOptions
- **PluralRules**
  - `select()`: string
- **RelativeTimeFormat**
  - `format()`: string
  - `formatToParts()`: Array<object>
  - `resolvedOptions()`: RelativeTimeFormatResolvedOptions

### logLibrary (@ohos.logLibrary.d.ts)
#### Interfaces
- **LogEntry**
  - `name`: string
  - `mtime`: number
  - `size`: number
#### Functions
- `list(logType: string): LogEntry[]`
- `copy(logType: string, logName: string, dest: string): Promise<void>`
- `copy(logType: string, logName: string, dest: string, callback: AsyncCallback<void>): void`
- `move(logType: string, logName: string, dest: string): Promise<void>`
- `move(logType: string, logName: string, dest: string, callback: AsyncCallback<void>): void`
- `remove(logType: string, logName: string): void`

### matrix4 (@ohos.matrix4.d.ts)
#### Interfaces
- **TranslateOption**
  - `x?`: number
  - `y?`: number
  - `z?`: number
- **ScaleOption**
  - `x?`: number
  - `y?`: number
  - `z?`: number
  - `centerX?`: number
  - `centerY?`: number
- **RotateOption**
  - `x?`: number
  - `y?`: number
  - `z?`: number
  - `centerX?`: number
  - `centerY?`: number
  - `angle?`: number
- **Matrix4Transit**
  - `copy`: Matrix4Transit
  - `invert`: Matrix4Transit
  - `combine`: Matrix4Transit
  - `translate`: Matrix4Transit
  - `scale`: Matrix4Transit
  - `rotate`: Matrix4Transit
  - `transformPoint`: [number, number]
#### Functions
- `init(
    options: [
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
      numbe`
- `identity(): Matrix4Transit`
- `copy(): Matrix4Transit`
- `invert(): Matrix4Transit`
- `combine(options: Matrix4Transit): Matrix4Transit`
- `translate(options: TranslateOption): Matrix4Transit`
- `scale(options: ScaleOption): Matrix4Transit`
- `rotate(options: RotateOption): Matrix4Transit`
- `transformPoint(options: [number, number]): [number, number]`

### @ohos.measure (@ohos.measure.d.ts)
#### Interfaces
- **MeasureOptions**
  - `textContent`: string | Resource
  - `constraintWidth?`: number | string | Resource
  - `fontSize?`: number | string | Resource
  - `fontStyle?`: number | FontStyle
  - `fontWeight?`: number | string | FontWeight
  - `fontFamily?`: string | Resource
  - `letterSpacing?`: number | string
  - `textAlign?`: number | TextAlign
  - `overflow?`: number | TextOverflow
  - `maxLines?`: number
  - `lineHeight?`: number | string | Resource
  - `baselineOffset?`: number | string
  - `textCase?`: number | TextCase
  - `textIndent?`: number | string
  - `wordBreak?`: WordBreak
#### Classes
- **MeasureText**
  - `measureText()`: number
  - `measureTextSize()`: SizeOptions

### mediaquery (@ohos.mediaquery.d.ts)
#### Interfaces
- **MediaQueryResult**
  - `readonly matches`: boolean
  - `readonly media`: string
- **MediaQueryListener**
  - `on`: void
  - `off`: void
#### Functions
- `matchMediaSync(condition: string): MediaQueryListener`

### audio (@ohos.multimedia.audio.d.ts)
#### Interfaces
- **AudioStreamInfo**
  - `samplingRate`: AudioSamplingRate
  - `channels`: AudioChannel
  - `sampleFormat`: AudioSampleFormat
  - `encodingType`: AudioEncodingType
  - `channelLayout?`: AudioChannelLayout
- **AudioRendererInfo**
  - `content?`: ContentType
  - `usage`: StreamUsage
  - `rendererFlags`: number
- **AudioRendererFilter**
  - `uid?`: number
  - `rendererInfo?`: AudioRendererInfo
  - `rendererId?`: number
- **AudioRendererOptions**
  - `streamInfo`: AudioStreamInfo
  - `rendererInfo`: AudioRendererInfo
  - `privacyType?`: AudioPrivacyType
- **InterruptEvent**
  - `eventType`: InterruptType
  - `forceType`: InterruptForceType
  - `hintType`: InterruptHint
- **AudioManager**
  - `setVolume`: void
  - `setVolume`: Promise<void>
  - `getVolume`: void
  - `getVolume`: Promise<number>
  - `getMinVolume`: void
  - `getMinVolume`: Promise<number>
  - `getMaxVolume`: void
  - `getMaxVolume`: Promise<number>
  - `getDevices`: void
  - `getDevices`: Promise<AudioDeviceDescriptors>
  - `mute`: void
  - `mute`: Promise<void>
  - `isMute`: void
  - `isMute`: Promise<boolean>
  - `isActive`: void
  - `isActive`: Promise<boolean>
  - `setMicrophoneMute`: void
  - `setMicrophoneMute`: Promise<void>
  - `isMicrophoneMute`: void
  - `isMicrophoneMute`: Promise<boolean>
  - `setRingerMode`: void
  - `setRingerMode`: Promise<void>
  - `getRingerMode`: void
  - `getRingerMode`: Promise<AudioRingMode>
  - `setAudioParameter`: void
  - `setAudioParameter`: Promise<void>
  - `getAudioParameter`: void
  - `getAudioParameter`: Promise<string>
  - `setExtraParameters`: Promise<void>
  - `getExtraParameters`: Promise<Record<string, string>>
  - `setDeviceActive`: void
  - `setDeviceActive`: Promise<void>
  - `isDeviceActive`: void
  - `isDeviceActive`: Promise<boolean>
  - `on`: void
  - `on`: void
  - `setAudioScene`: void
  - `setAudioScene`: Promise<void>
  - `getAudioScene`: void
  - `getAudioScene`: Promise<AudioScene>
  - `getAudioSceneSync`: AudioScene
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `getVolumeManager`: AudioVolumeManager
  - `getStreamManager`: AudioStreamManager
  - `getRoutingManager`: AudioRoutingManager
  - `getSpatializationManager`: AudioSpatializationManager
- **InterruptResult**
  - `requestResult`: InterruptRequestResultType
  - `interruptNode`: number
- **AudioRoutingManager**
  - `getDevices`: void
  - `getDevices`: Promise<AudioDeviceDescriptors>
  - `getDevicesSync`: AudioDeviceDescriptors
  - `on`: void
  - `off`: void
  - `getAvailableDevices`: AudioDeviceDescriptors
  - `on`: void
  - `off`: void
  - `setCommunicationDevice`: void
  - `setCommunicationDevice`: Promise<void>
  - `isCommunicationDeviceActive`: void
  - `isCommunicationDeviceActive`: Promise<boolean>
  - `isCommunicationDeviceActiveSync`: boolean
  - `selectOutputDevice`: void
  - `selectOutputDevice`: Promise<void>
  - `selectOutputDeviceByFilter`: void
  - `selectOutputDeviceByFilter`: Promise<void>
  - `selectInputDevice`: void
  - `selectInputDevice`: Promise<void>
  - `getPreferOutputDeviceForRendererInfo`: void
  - `getPreferOutputDeviceForRendererInfo`: Promise<AudioDeviceDescriptors>
  - `getPreferredOutputDeviceForRendererInfoSync`: AudioDeviceDescriptors
  - `on`: void
  - `off`: void
  - `getPreferredInputDeviceForCapturerInfo`: void
  - `getPreferredInputDeviceForCapturerInfo`: Promise<AudioDeviceDescriptors>
  - `on`: void
  - `off`: void
  - `getPreferredInputDeviceForCapturerInfoSync`: AudioDeviceDescriptors
- **AudioStreamManager**
  - `getCurrentAudioRendererInfoArray`: void
  - `getCurrentAudioRendererInfoArray`: Promise<AudioRendererChangeInfoArray>
  - `getCurrentAudioRendererInfoArraySync`: AudioRendererChangeInfoArray
  - `getCurrentAudioCapturerInfoArray`: void
  - `getCurrentAudioCapturerInfoArray`: Promise<AudioCapturerChangeInfoArray>
  - `getCurrentAudioCapturerInfoArraySync`: AudioCapturerChangeInfoArray
  - `getAudioEffectInfoArray`: void
  - `getAudioEffectInfoArray`: Promise<AudioEffectInfoArray>
  - `getAudioEffectInfoArraySync`: AudioEffectInfoArray
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `isActive`: void
  - `isActive`: Promise<boolean>
  - `isActiveSync`: boolean
- **AudioVolumeManager**
  - `getVolumeGroupInfos`: void
  - `getVolumeGroupInfos`: Promise<VolumeGroupInfos>
  - `getVolumeGroupInfosSync`: VolumeGroupInfos
  - `getVolumeGroupManager`: void
  - `getVolumeGroupManager`: Promise<AudioVolumeGroupManager>
  - `getVolumeGroupManagerSync`: AudioVolumeGroupManager
  - `on`: void
- **AudioVolumeGroupManager**
  - `setVolume`: void
  - `setVolume`: Promise<void>
  - `getVolume`: void
  - `getVolume`: Promise<number>
  - `getVolumeSync`: number
  - `getMinVolume`: void
  - `getMinVolume`: Promise<number>
  - `getMinVolumeSync`: number
  - `getMaxVolume`: void
  - `getMaxVolume`: Promise<number>
  - `getMaxVolumeSync`: number
  - `mute`: void
  - `mute`: Promise<void>
  - `isMute`: void
  - `isMute`: Promise<boolean>
  - `isMuteSync`: boolean
  - `setRingerMode`: void
  - `setRingerMode`: Promise<void>
  - `getRingerMode`: void
  - `getRingerMode`: Promise<AudioRingMode>
  - `getRingerModeSync`: AudioRingMode
  - `on`: void
  - `setMicrophoneMute`: void
  - `setMicrophoneMute`: Promise<void>
  - `setMicMute`: Promise<void>
  - `isMicrophoneMute`: void
  - `isMicrophoneMute`: Promise<boolean>
  - `isMicrophoneMuteSync`: boolean
  - `on`: void
  - `isVolumeUnadjustable`: boolean
  - `adjustVolumeByStep`: void
  - `adjustVolumeByStep`: Promise<void>
  - `adjustSystemVolumeByStep`: void
  - `adjustSystemVolumeByStep`: Promise<void>
  - `getSystemVolumeInDb`: void
  - `getSystemVolumeInDb`: Promise<number>
  - `getSystemVolumeInDbSync`: number
- **AudioSpatializationManager**
  - `isSpatializationSupported`: boolean
  - `isSpatializationSupportedForDevice`: boolean
  - `isHeadTrackingSupported`: boolean
  - `isHeadTrackingSupportedForDevice`: boolean
  - `setSpatializationEnabled`: void
  - `setSpatializationEnabled`: Promise<void>
  - `isSpatializationEnabled`: boolean
  - `on`: void
  - `off`: void
  - `setHeadTrackingEnabled`: void
  - `setHeadTrackingEnabled`: Promise<void>
  - `isHeadTrackingEnabled`: boolean
  - `on`: void
  - `off`: void
  - `updateSpatialDeviceState`: void
- **VolumeGroupInfo**
  - `readonly networkId`: string
  - `readonly groupId`: number
  - `readonly mappingId`: number
  - `readonly groupName`: string
  - `readonly type`: ConnectType
- **AudioRendererChangeInfo**
  - `readonly streamId`: number
  - `readonly clientUid`: number
  - `readonly rendererInfo`: AudioRendererInfo
  - `readonly rendererState`: AudioState
  - `readonly deviceDescriptors`: AudioDeviceDescriptors
- **AudioCapturerChangeInfo**
  - `readonly streamId`: number
  - `readonly clientUid`: number
  - `readonly capturerInfo`: AudioCapturerInfo
  - `readonly capturerState`: AudioState
  - `readonly deviceDescriptors`: AudioDeviceDescriptors
  - `readonly muted?`: boolean
- **AudioDeviceDescriptor**
  - `readonly deviceRole`: DeviceRole
  - `readonly deviceType`: DeviceType
  - `readonly id`: number
  - `readonly name`: string
  - `readonly address`: string
  - `readonly sampleRates`: Array<number>
  - `readonly channelCounts`: Array<number>
  - `readonly channelMasks`: Array<number>
  - `readonly networkId`: string
  - `readonly interruptGroupId`: number
  - `readonly volumeGroupId`: number
  - `readonly displayName`: string
  - `readonly encodingTypes?`: Array<AudioEncodingType>
- **VolumeEvent**
  - `volumeType`: AudioVolumeType
  - `volume`: number
  - `updateUi`: boolean
  - `volumeGroupId`: number
  - `networkId`: string
- **InterruptAction**
  - `actionType`: InterruptActionType
  - `type?`: InterruptType
  - `hint?`: InterruptHint
  - `activated?`: boolean
- **AudioInterrupt**
  - `streamUsage`: StreamUsage
  - `contentType`: ContentType
  - `pauseWhenDucked`: boolean
- **MicStateChangeEvent**
  - `mute`: boolean
- **DeviceChangeAction**
  - `type`: DeviceChangeType
  - `deviceDescriptors`: AudioDeviceDescriptors
- **AudioStreamDeviceChangeInfo**
  - `devices`: AudioDeviceDescriptors
  - `changeReason`: AudioStreamDeviceChangeReason
- **AudioRenderer**
  - `readonly state`: AudioState
  - `getRendererInfo`: void
  - `getRendererInfo`: Promise<AudioRendererInfo>
  - `getRendererInfoSync`: AudioRendererInfo
  - `getStreamInfo`: void
  - `getStreamInfo`: Promise<AudioStreamInfo>
  - `getStreamInfoSync`: AudioStreamInfo
  - `getAudioStreamId`: void
  - `getAudioStreamId`: Promise<number>
  - `getAudioStreamIdSync`: number
  - `getAudioEffectMode`: void
  - `getAudioEffectMode`: Promise<AudioEffectMode>
  - `setAudioEffectMode`: void
  - `setAudioEffectMode`: Promise<void>
  - `start`: void
  - `start`: Promise<void>
  - `write`: void
  - `write`: Promise<number>
  - `getAudioTime`: void
  - `getAudioTime`: Promise<number>
  - `getAudioTimeSync`: number
  - `drain`: void
  - `drain`: Promise<void>
  - `flush`: Promise<void>
  - `pause`: void
  - `pause`: Promise<void>
  - `stop`: void
  - `stop`: Promise<void>
  - `release`: void
  - `release`: Promise<void>
  - `getBufferSize`: void
  - `getBufferSize`: Promise<number>
  - `getBufferSizeSync`: number
  - `setRenderRate`: void
  - `setRenderRate`: Promise<void>
  - `setSpeed`: void
  - `getRenderRate`: void
  - `getRenderRate`: Promise<AudioRendererRate>
  - `getRenderRateSync`: AudioRendererRate
  - `getSpeed`: number
  - `setInterruptMode`: void
  - `setInterruptMode`: Promise<void>
  - `setInterruptModeSync`: void
  - `setVolume`: void
  - `setVolume`: Promise<void>
  - `setVolumeWithRamp`: void
  - `getMinStreamVolume`: void
  - `getMinStreamVolume`: Promise<number>
  - `getMinStreamVolumeSync`: number
  - `getMaxStreamVolume`: void
  - `getMaxStreamVolume`: Promise<number>
  - `getMaxStreamVolumeSync`: number
  - `getUnderflowCount`: void
  - `getUnderflowCount`: Promise<number>
  - `getUnderflowCountSync`: number
  - `getCurrentOutputDevices`: void
  - `getCurrentOutputDevices`: Promise<AudioDeviceDescriptors>
  - `getCurrentOutputDevicesSync`: AudioDeviceDescriptors
  - `setChannelBlendMode`: void
  - `on`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `on`: void
  - `on`: void
  - `off`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **AudioCapturerInfo**
  - `source`: SourceType
  - `capturerFlags`: number
- **AudioCapturerOptions**
  - `streamInfo`: AudioStreamInfo
  - `capturerInfo`: AudioCapturerInfo
  - `playbackCaptureConfig?`: AudioPlaybackCaptureConfig
- **CaptureFilterOptions**
  - `usages`: Array<StreamUsage>
- **AudioPlaybackCaptureConfig**
  - `filterOptions`: CaptureFilterOptions
- **AudioCapturer**
  - `readonly state`: AudioState
  - `getCapturerInfo`: void
  - `getCapturerInfo`: Promise<AudioCapturerInfo>
  - `getCapturerInfoSync`: AudioCapturerInfo
  - `getStreamInfo`: void
  - `getStreamInfo`: Promise<AudioStreamInfo>
  - `getStreamInfoSync`: AudioStreamInfo
  - `getAudioStreamId`: void
  - `getAudioStreamId`: Promise<number>
  - `getAudioStreamIdSync`: number
  - `start`: void
  - `start`: Promise<void>
  - `read`: void
  - `read`: Promise<ArrayBuffer>
  - `getAudioTime`: void
  - `getAudioTime`: Promise<number>
  - `getAudioTimeSync`: number
  - `stop`: void
  - `stop`: Promise<void>
  - `release`: void
  - `release`: Promise<void>
  - `getBufferSize`: void
  - `getBufferSize`: Promise<number>
  - `getBufferSizeSync`: number
  - `getCurrentInputDevices`: AudioDeviceDescriptors
  - `getCurrentAudioCapturerChangeInfo`: AudioCapturerChangeInfo
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **TonePlayer**
  - `load`: void
  - `load`: Promise<void>
  - `start`: void
  - `start`: Promise<void>
  - `stop`: void
  - `stop`: Promise<void>
  - `release`: void
  - `release`: Promise<void>
- **AudioSpatialDeviceState**
  - `address`: string
  - `isSpatializationSupported`: boolean
  - `isHeadTrackingSupported`: boolean
  - `spatialDeviceType`: AudioSpatialDeviceType
#### Enums
- **AudioErrors**
  - `ERROR_INVALID_PARAM` = 6800101
  - `ERROR_NO_MEMORY` = 6800102
  - `ERROR_ILLEGAL_STATE` = 6800103
  - `ERROR_UNSUPPORTED` = 6800104
  - `ERROR_TIMEOUT` = 6800105
  - `ERROR_STREAM_LIMIT` = 6800201
  - `ERROR_SYSTEM` = 6800301
- **AudioState**
  - `STATE_INVALID` = -1
  - `STATE_NEW` = 0
  - `STATE_PREPARED` = 1
  - `STATE_RUNNING` = 2
  - `STATE_STOPPED` = 3
  - `STATE_RELEASED` = 4
  - `STATE_PAUSED` = 5
- **AudioVolumeType**
  - `VOICE_CALL` = 0
  - `RINGTONE` = 2
  - `MEDIA` = 3
  - `ALARM` = 4
  - `ACCESSIBILITY` = 5
  - `VOICE_ASSISTANT` = 9
  - `ULTRASONIC` = 10
  - `ALL` = 100
- **DeviceFlag**
  - `NONE_DEVICES_FLAG` = 0
  - `OUTPUT_DEVICES_FLAG` = 1
  - `INPUT_DEVICES_FLAG` = 2
  - `ALL_DEVICES_FLAG` = 3
  - `DISTRIBUTED_OUTPUT_DEVICES_FLAG` = 4
  - `DISTRIBUTED_INPUT_DEVICES_FLAG` = 8
  - `ALL_DISTRIBUTED_DEVICES_FLAG` = 12
- **DeviceUsage**
  - `MEDIA_OUTPUT_DEVICES` = 1
  - `MEDIA_INPUT_DEVICES` = 2
  - `ALL_MEDIA_DEVICES` = 3
  - `CALL_OUTPUT_DEVICES` = 4
  - `CALL_INPUT_DEVICES` = 8
  - `ALL_CALL_DEVICES` = 12
- **DeviceRole**
  - `INPUT_DEVICE` = 1
  - `OUTPUT_DEVICE` = 2
- **DeviceType**
  - `INVALID` = 0
  - `EARPIECE` = 1
  - `SPEAKER` = 2
  - `WIRED_HEADSET` = 3
  - `WIRED_HEADPHONES` = 4
  - `BLUETOOTH_SCO` = 7
  - `BLUETOOTH_A2DP` = 8
  - `MIC` = 15
  - `USB_HEADSET` = 22
  - `DEFAULT` = 1000
- **ActiveDeviceType**
  - `SPEAKER` = 2
  - `BLUETOOTH_SCO` = 7
- **CommunicationDeviceType**
  - `SPEAKER` = 2
- **AudioRingMode**
  - `RINGER_MODE_SILENT` = 0
  - `RINGER_MODE_VIBRATE` = 1
  - `RINGER_MODE_NORMAL` = 2
- **AudioSampleFormat**
  - `SAMPLE_FORMAT_INVALID` = -1
  - `SAMPLE_FORMAT_U8` = 0
  - `SAMPLE_FORMAT_S16LE` = 1
  - `SAMPLE_FORMAT_S24LE` = 2
  - `SAMPLE_FORMAT_S32LE` = 3
  - `SAMPLE_FORMAT_F32LE` = 4
- **AudioChannel**
  - `CHANNEL_1` = 0x1 << 0
  - `CHANNEL_2` = 0x1 << 1
  - `CHANNEL_3` = 3
  - `CHANNEL_4` = 4
  - `CHANNEL_5` = 5
  - `CHANNEL_6` = 6
  - `CHANNEL_7` = 7
  - `CHANNEL_8` = 8
  - `CHANNEL_9` = 9
  - `CHANNEL_10` = 10
  - `CHANNEL_12` = 12
  - `CHANNEL_14` = 14
  - `CHANNEL_16` = 16
- **AudioSamplingRate**
  - `SAMPLE_RATE_8000` = 8000
  - `SAMPLE_RATE_11025` = 11025
  - `SAMPLE_RATE_12000` = 12000
  - `SAMPLE_RATE_16000` = 16000
  - `SAMPLE_RATE_22050` = 22050
  - `SAMPLE_RATE_24000` = 24000
  - `SAMPLE_RATE_32000` = 32000
  - `SAMPLE_RATE_44100` = 44100
  - `SAMPLE_RATE_48000` = 48000
  - `SAMPLE_RATE_64000` = 64000
  - `SAMPLE_RATE_96000` = 96000
- **AudioEncodingType**
  - `ENCODING_TYPE_INVALID` = -1
  - `ENCODING_TYPE_RAW` = 0
- **ContentType**
  - `CONTENT_TYPE_UNKNOWN` = 0
  - `CONTENT_TYPE_SPEECH` = 1
  - `CONTENT_TYPE_MUSIC` = 2
  - `CONTENT_TYPE_MOVIE` = 3
  - `CONTENT_TYPE_SONIFICATION` = 4
  - `CONTENT_TYPE_RINGTONE` = 5
- **StreamUsage**
  - `STREAM_USAGE_UNKNOWN` = 0
  - `STREAM_USAGE_MEDIA` = 1
  - `STREAM_USAGE_MUSIC` = 1
  - `STREAM_USAGE_VOICE_COMMUNICATION` = 2
  - `STREAM_USAGE_VOICE_ASSISTANT` = 3
  - `STREAM_USAGE_ALARM` = 4
  - `STREAM_USAGE_VOICE_MESSAGE` = 5
  - `STREAM_USAGE_NOTIFICATION_RINGTONE` = 6
  - `STREAM_USAGE_RINGTONE` = 6
  - `STREAM_USAGE_NOTIFICATION` = 7
  - `STREAM_USAGE_ACCESSIBILITY` = 8
  - `STREAM_USAGE_SYSTEM` = 9
  - `STREAM_USAGE_MOVIE` = 10
  - `STREAM_USAGE_GAME` = 11
  - `STREAM_USAGE_AUDIOBOOK` = 12
  - `STREAM_USAGE_NAVIGATION` = 13
  - `STREAM_USAGE_DTMF` = 14
  - `STREAM_USAGE_ENFORCED_TONE` = 15
  - `STREAM_USAGE_ULTRASONIC` = 16
- **InterruptRequestType**
  - `INTERRUPT_REQUEST_TYPE_DEFAULT` = 0
- **AudioPrivacyType**
  - `PRIVACY_TYPE_PUBLIC` = 0
  - `PRIVACY_TYPE_PRIVATE` = 1
- **InterruptMode**
  - `SHARE_MODE` = 0
  - `INDEPENDENT_MODE` = 1
- **AudioRendererRate**
  - `RENDER_RATE_NORMAL` = 0
  - `RENDER_RATE_DOUBLE` = 1
  - `RENDER_RATE_HALF` = 2
- **InterruptType**
  - `INTERRUPT_TYPE_BEGIN` = 1
  - `INTERRUPT_TYPE_END` = 2
- **InterruptHint**
  - `INTERRUPT_HINT_NONE` = 0
  - `INTERRUPT_HINT_RESUME` = 1
  - `INTERRUPT_HINT_PAUSE` = 2
  - `INTERRUPT_HINT_STOP` = 3
  - `INTERRUPT_HINT_DUCK` = 4
  - `INTERRUPT_HINT_UNDUCK` = 5
- **InterruptForceType**
  - `INTERRUPT_FORCE` = 0
  - `INTERRUPT_SHARE` = 1
- **InterruptActionType**
  - `TYPE_ACTIVATED` = 0
  - `TYPE_INTERRUPT` = 1
- **DeviceChangeType**
  - `CONNECT` = 0
  - `DISCONNECT` = 1
- **AudioScene**
  - `AUDIO_SCENE_DEFAULT` = 0
  - `AUDIO_SCENE_RINGING` = 1
  - `AUDIO_SCENE_PHONE_CALL` = 2
  - `AUDIO_SCENE_VOICE_CHAT` = 3
- **VolumeAdjustType**
  - `VOLUME_UP` = 0
  - `VOLUME_DOWN` = 1
- **InterruptRequestResultType**
  - `INTERRUPT_REQUEST_GRANT` = 0
  - `INTERRUPT_REQUEST_REJECT` = 1
- **ConnectType**
  - `CONNECT_TYPE_LOCAL` = 1
  - `CONNECT_TYPE_DISTRIBUTED` = 2
- **ChannelBlendMode**
  - `MODE_DEFAULT` = 0
  - `MODE_BLEND_LR` = 1
  - `MODE_ALL_LEFT` = 2
  - `MODE_ALL_RIGHT` = 3
- **AudioStreamDeviceChangeReason**
  - `REASON_UNKNOWN` = 0
  - `REASON_NEW_DEVICE_AVAILABLE` = 1
  - `REASON_OLD_DEVICE_UNAVAILABLE` = 2
  - `REASON_OVERRODE` = 3
- **SourceType**
  - `SOURCE_TYPE_INVALID` = -1
  - `SOURCE_TYPE_MIC` = 0
  - `SOURCE_TYPE_VOICE_RECOGNITION` = 1
  - `SOURCE_TYPE_PLAYBACK_CAPTURE` = 2
  - `SOURCE_TYPE_WAKEUP` = 3
  - `SOURCE_TYPE_VOICE_CALL` = 4
  - `SOURCE_TYPE_VOICE_COMMUNICATION` = 7
- **ToneType**
  - `TONE_TYPE_DIAL_0` = 0
  - `TONE_TYPE_DIAL_1` = 1
  - `TONE_TYPE_DIAL_2` = 2
  - `TONE_TYPE_DIAL_3` = 3
  - `TONE_TYPE_DIAL_4` = 4
  - `TONE_TYPE_DIAL_5` = 5
  - `TONE_TYPE_DIAL_6` = 6
  - `TONE_TYPE_DIAL_7` = 7
  - `TONE_TYPE_DIAL_8` = 8
  - `TONE_TYPE_DIAL_9` = 9
  - `TONE_TYPE_DIAL_S` = 10
  - `TONE_TYPE_DIAL_P` = 11
  - `TONE_TYPE_DIAL_A` = 12
  - `TONE_TYPE_DIAL_B` = 13
  - `TONE_TYPE_DIAL_C` = 14
  - `TONE_TYPE_DIAL_D` = 15
  - `TONE_TYPE_COMMON_SUPERVISORY_DIAL` = 100
  - `TONE_TYPE_COMMON_SUPERVISORY_BUSY` = 101
  - `TONE_TYPE_COMMON_SUPERVISORY_CONGESTION` = 102
  - `TONE_TYPE_COMMON_SUPERVISORY_RADIO_ACK` = 103
  - `TONE_TYPE_COMMON_SUPERVISORY_RADIO_NOT_AVAILABLE` = 104
  - `TONE_TYPE_COMMON_SUPERVISORY_CALL_WAITING` = 106
  - `TONE_TYPE_COMMON_SUPERVISORY_RINGTONE` = 107
  - `TONE_TYPE_COMMON_PROPRIETARY_BEEP` = 200
  - `TONE_TYPE_COMMON_PROPRIETARY_ACK` = 201
  - `TONE_TYPE_COMMON_PROPRIETARY_PROMPT` = 203
  - `TONE_TYPE_COMMON_PROPRIETARY_DOUBLE_BEEP` = 204
- **AudioEffectMode**
  - `EFFECT_NONE` = 0
  - `EFFECT_DEFAULT` = 1
- **AudioSpatialDeviceType**
  - `SPATIAL_DEVICE_TYPE_NONE` = 0
  - `SPATIAL_DEVICE_TYPE_IN_EAR_HEADPHONE` = 1
  - `SPATIAL_DEVICE_TYPE_HALF_IN_EAR_HEADPHONE` = 2
  - `SPATIAL_DEVICE_TYPE_OVER_EAR_HEADPHONE` = 3
  - `SPATIAL_DEVICE_TYPE_GLASSES` = 4
  - `SPATIAL_DEVICE_TYPE_OTHERS` = 5
- **AudioChannelLayout**
  - `CH_LAYOUT_UNKNOWN` = 0x0
  - `CH_LAYOUT_MONO` = 0x4
  - `CH_LAYOUT_STEREO` = 0x3
  - `CH_LAYOUT_STEREO_DOWNMIX` = 0x60000000
  - `CH_LAYOUT_2POINT1` = 0xB
  - `CH_LAYOUT_3POINT0` = 0x103
  - `CH_LAYOUT_SURROUND` = 0x7
  - `CH_LAYOUT_3POINT1` = 0xF
  - `CH_LAYOUT_4POINT0` = 0x107
  - `CH_LAYOUT_QUAD` = 0x33
  - `CH_LAYOUT_QUAD_SIDE` = 0x603
  - `CH_LAYOUT_2POINT0POINT2` = 0x3000000003
  - `CH_LAYOUT_AMB_ORDER1_ACN_N3D` = 0x100000000001
  - `CH_LAYOUT_AMB_ORDER1_ACN_SN3D` = 0x100000001001
  - `CH_LAYOUT_AMB_ORDER1_FUMA` = 0x100000000101
  - `CH_LAYOUT_4POINT1` = 0x10F
  - `CH_LAYOUT_5POINT0` = 0x607
  - `CH_LAYOUT_5POINT0_BACK` = 0x37
  - `CH_LAYOUT_2POINT1POINT2` = 0x300000000B
  - `CH_LAYOUT_3POINT0POINT2` = 0x3000000007
  - `CH_LAYOUT_5POINT1` = 0x60F
  - `CH_LAYOUT_5POINT1_BACK` = 0x3F
  - `CH_LAYOUT_6POINT0` = 0x707
  - `CH_LAYOUT_HEXAGONAL` = 0x137
  - `CH_LAYOUT_3POINT1POINT2` = 0x500F
  - `CH_LAYOUT_6POINT0_FRONT` = 0x6C3
  - `CH_LAYOUT_6POINT1` = 0x70F
  - `CH_LAYOUT_6POINT1_BACK` = 0x13F
  - `CH_LAYOUT_6POINT1_FRONT` = 0x6CB
  - `CH_LAYOUT_7POINT0` = 0x637
  - `CH_LAYOUT_7POINT0_FRONT` = 0x6C7
  - `CH_LAYOUT_7POINT1` = 0x63F
  - `CH_LAYOUT_OCTAGONAL` = 0x737
  - `CH_LAYOUT_5POINT1POINT2` = 0x300000060F
  - `CH_LAYOUT_7POINT1_WIDE` = 0x6CF
  - `CH_LAYOUT_7POINT1_WIDE_BACK` = 0xFF
  - `CH_LAYOUT_AMB_ORDER2_ACN_N3D` = 0x100000000002
  - `CH_LAYOUT_AMB_ORDER2_ACN_SN3D` = 0x100000001002
  - `CH_LAYOUT_AMB_ORDER2_FUMA` = 0x100000000102
  - `CH_LAYOUT_5POINT1POINT4` = 0x2D60F
  - `CH_LAYOUT_7POINT1POINT2` = 0x300000063F
  - `CH_LAYOUT_7POINT1POINT4` = 0x2D63F
  - `CH_LAYOUT_10POINT2` = 0x180005737
  - `CH_LAYOUT_9POINT1POINT4` = 0x18002D63F
  - `CH_LAYOUT_9POINT1POINT6` = 0x318002D63F
  - `CH_LAYOUT_HEXADECAGONAL` = 0x18003F737
  - `CH_LAYOUT_AMB_ORDER3_ACN_N3D` = 0x100000000003
  - `CH_LAYOUT_AMB_ORDER3_ACN_SN3D` = 0x100000001003
  - `CH_LAYOUT_AMB_ORDER3_FUMA` = 0x100000000103
#### Functions
- `getAudioManager(): AudioManager`
- `createAudioCapturer(options: AudioCapturerOptions, callback: AsyncCallback<AudioCapturer>): void`
- `createAudioCapturer(options: AudioCapturerOptions): Promise<AudioCapturer>`
- `createAudioRenderer(options: AudioRendererOptions, callback: AsyncCallback<AudioRenderer>): void`
- `createAudioRenderer(options: AudioRendererOptions): Promise<AudioRenderer>`
- `createTonePlayer(options: AudioRendererInfo, callback: AsyncCallback<TonePlayer>): void`
- `createTonePlayer(options: AudioRendererInfo): Promise<TonePlayer>`
#### Type Aliases
- `VolumeGroupInfos` = Array<Readonly<VolumeGroupInfo>>
- `AudioRendererChangeInfoArray` = Array<Readonly<AudioRendererChangeInfo>>
- `AudioCapturerChangeInfoArray` = Array<Readonly<AudioCapturerChangeInfo>>
- `AudioDeviceDescriptors` = Array<Readonly<AudioDeviceDescriptor>>
- `AudioEffectInfoArray` = Array<Readonly<AudioEffectMode>>

### audioHaptic (@ohos.multimedia.audioHaptic.d.ts)
#### Interfaces
- **AudioHapticPlayerOptions**
  - `muteAudio?`: boolean
  - `muteHaptics?`: boolean
- **AudioHapticManager**
  - `registerSource`: Promise<number>
  - `unregisterSource`: Promise<void>
  - `setAudioLatencyMode`: void
  - `setStreamUsage`: void
  - `createPlayer`: Promise<AudioHapticPlayer>
- **AudioHapticPlayer**
  - `isMuted`: boolean
  - `start`: Promise<void>
  - `stop`: Promise<void>
  - `release`: Promise<void>
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
#### Enums
- **AudioLatencyMode**
  - `AUDIO_LATENCY_MODE_NORMAL` = 0
  - `AUDIO_LATENCY_MODE_FAST` = 1
- **AudioHapticType**
  - `AUDIO_HAPTIC_TYPE_AUDIO` = 0
  - `AUDIO_HAPTIC_TYPE_HAPTIC` = 1
#### Functions
- `getAudioHapticManager(): AudioHapticManager`

### @ohos.multimedia.avCastPicker (@ohos.multimedia.avCastPicker.d.ets)

### @ohos.multimedia.avCastPickerParam (@ohos.multimedia.avCastPickerParam.d.ts)
#### Enums
- **AVCastPickerState**

### avSession (@ohos.multimedia.avsession.d.ts)
#### Interfaces
- **SessionToken**
  - `sessionId`: string
  - `pid?`: number
  - `uid?`: number
- **AVSession**
  - `readonly sessionId`: string
  - `readonly sessionType`: AVSessionType
  - `setAVMetadata`: void
  - `setAVMetadata`: Promise<void>
  - `setCallMetadata`: void
  - `setCallMetadata`: Promise<void>
  - `setAVPlaybackState`: void
  - `setAVPlaybackState`: Promise<void>
  - `setAVCallState`: void
  - `setAVCallState`: Promise<void>
  - `setLaunchAbility`: void
  - `setLaunchAbility`: Promise<void>
  - `dispatchSessionEvent`: void
  - `dispatchSessionEvent`: Promise<void>
  - `setAVQueueItems`: void
  - `setAVQueueItems`: Promise<void>
  - `setAVQueueTitle`: void
  - `setAVQueueTitle`: Promise<void>
  - `setExtras`: void
  - `setExtras`: Promise<void>
  - `getController`: void
  - `getController`: Promise<AVSessionController>
  - `getAVCastController`: void
  - `getAVCastController`: Promise<AVCastController>
  - `getOutputDevice`: void
  - `getOutputDevice`: Promise<OutputDeviceInfo>
  - `getOutputDeviceSync`: OutputDeviceInfo
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `stopCasting`: void
  - `stopCasting`: Promise<void>
  - `activate`: void
  - `activate`: Promise<void>
  - `deactivate`: void
  - `deactivate`: Promise<void>
  - `destroy`: void
  - `destroy`: Promise<void>
- **AVCastControlCommand**
  - `command`: AVCastControlCommandType
  - `parameter?`: media.PlaybackSpeed | number | string | LoopMode
- **AVCastController**
  - `setDisplaySurface`: void
  - `setDisplaySurface`: Promise<void>
  - `getAVPlaybackState`: void
  - `getAVPlaybackState`: Promise<AVPlaybackState>
  - `sendControlCommand`: void
  - `sendControlCommand`: Promise<void>
  - `start`: void
  - `start`: Promise<void>
  - `prepare`: void
  - `prepare`: Promise<void>
  - `getCurrentItem`: void
  - `getCurrentItem`: Promise<AVQueueItem>
  - `getValidCommands`: void
  - `getValidCommands`: Promise<Array<AVCastControlCommandType>>
  - `release`: void
  - `release`: Promise<void>
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
  - `off`: void
  - `on`: void
  - `off`: void
- **AVQueueInfo**
  - `bundleName`: string
  - `avQueueName`: string
  - `avQueueId`: string
  - `avQueueImage`: image.PixelMap | string
  - `lastPlayedTime?`: number
- **AVMetadata**
  - `assetId`: string
  - `title?`: string
  - `artist?`: string
  - `author?`: string
  - `avQueueName?`: string
  - `avQueueId?`: string
  - `avQueueImage?`: image.PixelMap | string
  - `album?`: string
  - `writer?`: string
  - `composer?`: string
  - `duration?`: number
  - `mediaImage?`: image.PixelMap | string
  - `publishDate?`: Date
  - `subtitle?`: string
  - `description?`: string
  - `lyric?`: string
  - `previousAssetId?`: string
  - `nextAssetId?`: string
  - `filter?`: number
  - `skipIntervals?`: SkipIntervals
  - `displayTags?`: number
- **AVMediaDescription**
  - `assetId`: string
  - `title?`: string
  - `subtitle?`: string
  - `description?`: string
  - `mediaImage?`: image.PixelMap | string
  - `extras?`: {[key: string]: Object}
  - `mediaType?`: string
  - `mediaSize?`: number
  - `albumTitle?`: string
  - `albumCoverUri?`: string
  - `lyricContent?`: string
  - `lyricUri?`: string
  - `artist?`: string
  - `mediaUri?`: string
  - `fdSrc?`: media.AVFileDescriptor
  - `duration?`: number
  - `startPosition?`: number
  - `creditsPosition?`: number
  - `appName?`: string
  - `displayTags?`: number
- **AVQueueItem**
  - `itemId`: number
  - `description?`: AVMediaDescription
- **AVPlaybackState**
  - `state?`: PlaybackState
  - `speed?`: number
  - `position?`: PlaybackPosition
  - `bufferedTime?`: number
  - `loopMode?`: LoopMode
  - `isFavorite?`: boolean
  - `activeItemId?`: number
  - `volume?`: number
  - `maxVolume?`: number
  - `muted?`: boolean
  - `duration?`: number
  - `videoWidth?`: number
  - `videoHeight?`: number
  - `extras?`: {[key: string]: Object}
- **PlaybackPosition**
  - `elapsedTime`: number
  - `updateTime`: number
- **CallMetadata**
  - `name?`: string
  - `phoneNumber?`: string
  - `avatar?`: image.PixelMap
- **AVCallState**
  - `state`: CallState
  - `muted`: boolean
- **DeviceInfo**
  - `castCategory`: AVCastCategory
  - `deviceId`: string
  - `deviceName`: string
  - `deviceType`: DeviceType
  - `ipAddress?`: string
  - `providerId?`: number
  - `supportedProtocols?`: number
  - `authenticationStatus?`: number
- **OutputDeviceInfo**
  - `devices`: Array<DeviceInfo>
- **AVSessionDescriptor**
  - `sessionId`: string
  - `type`: AVSessionType
  - `sessionTag`: string
  - `elementName`: ElementName
  - `isActive`: boolean
  - `isTopSession`: boolean
  - `outputDevice`: OutputDeviceInfo
- **AVSessionController**
  - `readonly sessionId`: string
  - `getAVPlaybackState`: void
  - `getAVPlaybackState`: Promise<AVPlaybackState>
  - `getAVPlaybackStateSync`: AVPlaybackState
  - `getAVMetadata`: void
  - `getAVMetadata`: Promise<AVMetadata>
  - `getAVMetadataSync`: AVMetadata
  - `getAVCallState`: void
  - `getAVCallState`: Promise<AVCallState>
  - `getCallMetadata`: void
  - `getCallMetadata`: Promise<CallMetadata>
  - `getAVQueueTitle`: void
  - `getAVQueueTitle`: Promise<string>
  - `getAVQueueTitleSync`: string
  - `getAVQueueItems`: void
  - `getAVQueueItems`: Promise<Array<AVQueueItem>>
  - `getAVQueueItemsSync`: Array<AVQueueItem>
  - `skipToQueueItem`: void
  - `skipToQueueItem`: Promise<void>
  - `getOutputDevice`: void
  - `getOutputDevice`: Promise<OutputDeviceInfo>
  - `getOutputDeviceSync`: OutputDeviceInfo
  - `sendAVKeyEvent`: void
  - `sendAVKeyEvent`: Promise<void>
  - `getLaunchAbility`: void
  - `getLaunchAbility`: Promise<WantAgent>
  - `getRealPlaybackPositionSync`: number
  - `isActive`: void
  - `isActive`: Promise<boolean>
  - `isActiveSync`: boolean
  - `destroy`: void
  - `destroy`: Promise<void>
  - `getValidCommands`: void
  - `getValidCommands`: Promise<Array<AVControlCommandType>>
  - `getValidCommandsSync`: Array<AVControlCommandType>
  - `sendControlCommand`: void
  - `sendControlCommand`: Promise<void>
  - `sendCommonCommand`: void
  - `sendCommonCommand`: Promise<void>
  - `getExtras`: void
  - `getExtras`: Promise<{[key: string]: Object}>
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **AVControlCommand**
  - `command`: AVControlCommandType
  - `parameter?`: LoopMode | string | number
#### Enums
- **ProtocolType**
  - `TYPE_LOCAL` = 0
  - `TYPE_CAST_PLUS_MIRROR` = 1
  - `TYPE_CAST_PLUS_STREAM` = 2
- **ConnectionState**
  - `STATE_CONNECTING` = 0
  - `STATE_CONNECTED` = 1
  - `STATE_DISCONNECTED` = 6
- **DisplayTag**
  - `TAG_AUDIO_VIVID` = 1
- **CallState**
  - `CALL_STATE_IDLE` = 0
  - `CALL_STATE_INCOMING` = 1
  - `CALL_STATE_ACTIVE` = 2
  - `CALL_STATE_DIALING` = 3
  - `CALL_STATE_WAITING` = 4
  - `CALL_STATE_HOLDING` = 5
  - `CALL_STATE_DISCONNECTING` = 6
- **AVCastCategory**
  - `CATEGORY_LOCAL` = 0
  - `CATEGORY_REMOTE` = 1
- **DeviceType**
  - `DEVICE_TYPE_LOCAL` = 0
  - `DEVICE_TYPE_TV` = 2
  - `DEVICE_TYPE_SMART_SPEAKER` = 3
  - `DEVICE_TYPE_BLUETOOTH` = 10
- **LoopMode**
  - `LOOP_MODE_SEQUENCE` = 0
  - `LOOP_MODE_SINGLE` = 1
  - `LOOP_MODE_LIST` = 2
  - `LOOP_MODE_SHUFFLE` = 3
  - `LOOP_MODE_CUSTOM` = 4
- **SkipIntervals**
  - `SECONDS_10` = 10
  - `SECONDS_15` = 15
  - `SECONDS_30` = 30
- **PlaybackState**
  - `PLAYBACK_STATE_INITIAL` = 0
  - `PLAYBACK_STATE_PREPARE` = 1
  - `PLAYBACK_STATE_PLAY` = 2
  - `PLAYBACK_STATE_PAUSE` = 3
  - `PLAYBACK_STATE_FAST_FORWARD` = 4
  - `PLAYBACK_STATE_REWIND` = 5
  - `PLAYBACK_STATE_STOP` = 6
  - `PLAYBACK_STATE_COMPLETED` = 7
  - `PLAYBACK_STATE_RELEASED` = 8
  - `PLAYBACK_STATE_ERROR` = 9
  - `PLAYBACK_STATE_IDLE` = 10
  - `PLAYBACK_STATE_BUFFERING` = 11
- **AVSessionErrorCode**
  - `ERR_CODE_SERVICE_EXCEPTION` = 6600101
  - `ERR_CODE_SESSION_NOT_EXIST` = 6600102
  - `ERR_CODE_CONTROLLER_NOT_EXIST` = 6600103
  - `ERR_CODE_REMOTE_CONNECTION_ERR` = 6600104
  - `ERR_CODE_COMMAND_INVALID` = 6600105
  - `ERR_CODE_SESSION_INACTIVE` = 6600106
  - `ERR_CODE_MESSAGE_OVERLOAD` = 6600107
  - `ERR_CODE_DEVICE_CONNECTION_FAILED` = 6600108
  - `ERR_CODE_REMOTE_CONNECTION_NOT_EXIST` = 6600109
#### Functions
- `createAVSession(context: Context, tag: string, type: AVSessionType, callback: AsyncCallback<AVSession>): void`
- `createAVSession(context: Context, tag: string, type: AVSessionType): Promise<AVSession>`
- `getAllSessionDescriptors(callback: AsyncCallback<Array<Readonly<AVSessionDescriptor>>>): void`
- `getAllSessionDescriptors(): Promise<Array<Readonly<AVSessionDescriptor>>>`
- `getHistoricalSessionDescriptors(maxSize: number, callback: AsyncCallback<Array<Readonly<AVSessionDescriptor>>>): void`
- `getHistoricalSessionDescriptors(maxSize?: number): Promise<Array<Readonly<AVSessionDescriptor>>>`
- `getHistoricalAVQueueInfos(maxSize: number, maxAppSize: number, callback: AsyncCallback<Array<Readonly<AVQueueInfo>>>): void`
- `getHistoricalAVQueueInfos(maxSize: number, maxAppSize: number): Promise<Array<Readonly<AVQueueInfo>>>`
- `createController(sessionId: string, callback: AsyncCallback<AVSessionController>): void`
- `createController(sessionId: string): Promise<AVSessionController>`
- `castAudio(session: SessionToken | 'all', audioDevices: Array<audio.AudioDeviceDescriptor>, callback: AsyncCallback<void>): void`
- `castAudio(session: SessionToken | 'all', audioDevices: Array<audio.AudioDeviceDescriptor>): Promise<void>`
- `startAVPlayback(bundleName: string, assetId: string): Promise<void>`
- `sendSystemAVKeyEvent(event: KeyEvent, callback: AsyncCallback<void>): void`
- `sendSystemAVKeyEvent(event: KeyEvent): Promise<void>`
- `sendSystemControlCommand(command: AVControlCommand, callback: AsyncCallback<void>): void`
- `sendSystemControlCommand(command: AVControlCommand): Promise<void>`
- `startCastDeviceDiscovery(callback: AsyncCallback<void>): void`
- `startCastDeviceDiscovery(filter: number, callback: AsyncCallback<void>): void`
- `startCastDeviceDiscovery(filter?: number): Promise<void>`
- `stopCastDeviceDiscovery(callback: AsyncCallback<void>): void`
- `stopCastDeviceDiscovery(): Promise<void>`
- `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void`
- `setDiscoverable(enable: boolean): Promise<void>`
- `getAVCastController(sessionId: string, callback: AsyncCallback<AVCastController>): void`
- `getAVCastController(sessionId: string): Promise<AVCastController>`
- `startCasting(session: SessionToken, device: OutputDeviceInfo, callback: AsyncCallback<void>): void`
- `startCasting(session: SessionToken, device: OutputDeviceInfo): Promise<void>`
- `stopCasting(session: SessionToken, callback: AsyncCallback<void>): void`
- `stopCasting(session: SessionToken): Promise<void>`
#### Type Aliases
- `AVSessionType` = 'audio' | 'video' | 'voice_call'
- `AVCastControlCommandType` = 'play' | 'pause' | 'stop' | 'playNext' | 'playPrevious' | 'fastForward' | 'rewind' |
  'seek' | 'setVolume' | 'setSpeed' | 'setLoopMode' | 'toggleFavo
- `AVControlCommandType` = 'play' | 'pause' | 'stop' | 'playNext' | 'playPrevious' | 'fastForward' | 'rewind' |
  'seek' | 'setSpeed' | 'setLoopMode' | 'toggleFavorite' | 'playF

### camera (@ohos.multimedia.camera.d.ts)
#### Interfaces
- **Profile**
  - `readonly format`: CameraFormat
  - `readonly size`: Size
- **FrameRateRange**
  - `readonly min`: number
  - `readonly max`: number
- **VideoProfile**
  - `readonly frameRateRange`: FrameRateRange
- **CameraOutputCapability**
  - `readonly previewProfiles`: Array<Profile>
  - `readonly photoProfiles`: Array<Profile>
  - `readonly videoProfiles`: Array<VideoProfile>
  - `readonly supportedMetadataObjectTypes`: Array<MetadataObjectType>
- **SettingParam**
  - `skinSmoothLevel`: number
  - `faceSlender`: number
  - `skinTone`: number
- **PrelaunchConfig**
  - `cameraDevice`: CameraDevice
  - `restoreParamType?`: RestoreParamType
  - `activeTime?`: number
  - `settingParam?`: SettingParam
- **CameraManager**
  - `getSupportedCameras`: Array<CameraDevice>
  - `getSupportedOutputCapability`: CameraOutputCapability
  - `getSupportedSceneModes`: Array<SceneMode>
  - `getSupportedOutputCapability`: CameraOutputCapability
  - `isCameraMuted`: boolean
  - `isCameraMuteSupported`: boolean
  - `muteCamera`: void
  - `createCameraInput`: CameraInput
  - `createCameraInput`: CameraInput
  - `createPreviewOutput`: PreviewOutput
  - `createPhotoOutput`: PhotoOutput
  - `createPhotoOutput`: PhotoOutput
  - `createVideoOutput`: VideoOutput
  - `createMetadataOutput`: MetadataOutput
  - `createCaptureSession`: CaptureSession
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `isPrelaunchSupported`: boolean
  - `setPrelaunchConfig`: void
  - `prelaunch`: void
  - `preSwitchCamera`: void
  - `createDeferredPreviewOutput`: PreviewOutput
  - `isTorchSupported`: boolean
  - `isTorchModeSupported`: boolean
  - `getTorchMode`: TorchMode
  - `setTorchMode`: void
  - `on`: void
  - `off`: void
- **TorchStatusInfo**
  - `readonly isTorchAvailable`: boolean
  - `readonly isTorchActive`: boolean
  - `readonly torchLevel`: number
- **CameraStatusInfo**
  - `camera`: CameraDevice
  - `status`: CameraStatus
- **CameraDevice**
  - `readonly cameraId`: string
  - `readonly cameraPosition`: CameraPosition
  - `readonly cameraType`: CameraType
  - `readonly connectionType`: ConnectionType
  - `readonly hostDeviceName`: string
  - `readonly hostDeviceType`: HostDeviceType
- **Size**
  - `height`: number
  - `width`: number
- **Point**
  - `x`: number
  - `y`: number
- **CameraInput**
  - `open`: void
  - `open`: Promise<void>
  - `close`: void
  - `close`: Promise<void>
  - `on`: void
  - `off`: void
- **Flash**
  - `hasFlash`: boolean
  - `isFlashModeSupported`: boolean
  - `getFlashMode`: FlashMode
  - `setFlashMode`: void
- **AutoExposure**
  - `isExposureModeSupported`: boolean
  - `getExposureMode`: ExposureMode
  - `setExposureMode`: void
  - `getMeteringPoint`: Point
  - `setMeteringPoint`: void
  - `getExposureBiasRange`: Array<number>
  - `setExposureBias`: void
  - `getExposureValue`: number
- **Focus**
  - `isFocusModeSupported`: boolean
  - `getFocusMode`: FocusMode
  - `setFocusMode`: void
  - `setFocusPoint`: void
  - `getFocusPoint`: Point
  - `getFocalLength`: number
- **SmoothZoomInfo**
  - `duration`: number
- **Zoom**
  - `getZoomRatioRange`: Array<number>
  - `getZoomRatio`: number
  - `setZoomRatio`: void
  - `setSmoothZoom`: void
  - `prepareZoom`: void
  - `unprepareZoom`: void
- **Stabilization**
  - `isVideoStabilizationModeSupported`: boolean
  - `getActiveVideoStabilizationMode`: VideoStabilizationMode
  - `setVideoStabilizationMode`: void
- **Beauty**
  - `getSupportedBeautyTypes`: Array<BeautyType>
  - `getSupportedBeautyRange`: Array<number>
  - `getBeauty`: number
  - `setBeauty`: void
- **ColorEffect**
  - `getSupportedColorEffects`: Array<ColorEffectType>
  - `getColorEffect`: ColorEffectType
  - `setColorEffect`: void
- **ColorManagement**
  - `getActiveColorSpace`: colorSpaceManager.ColorSpace
  - `getSupportedColorSpaces`: Array<colorSpaceManager.ColorSpace>
  - `setColorSpace`: void
- **Macro**
  - `isMacroSupported`: boolean
  - `enableMacro`: void
- **Session**
  - `beginConfig`: void
  - `commitConfig`: void
  - `commitConfig`: Promise<void>
  - `canAddInput`: boolean
  - `addInput`: void
  - `removeInput`: void
  - `canAddOutput`: boolean
  - `addOutput`: void
  - `removeOutput`: void
  - `start`: void
  - `start`: Promise<void>
  - `stop`: void
  - `stop`: Promise<void>
  - `release`: void
  - `release`: Promise<void>
- **CaptureSession**
  - `beginConfig`: void
  - `commitConfig`: void
  - `commitConfig`: Promise<void>
  - `addInput`: void
  - `removeInput`: void
  - `addOutput`: void
  - `removeOutput`: void
  - `start`: void
  - `start`: Promise<void>
  - `stop`: void
  - `stop`: Promise<void>
  - `release`: void
  - `release`: Promise<void>
  - `hasFlash`: boolean
  - `isFlashModeSupported`: boolean
  - `getFlashMode`: FlashMode
  - `setFlashMode`: void
  - `isExposureModeSupported`: boolean
  - `getExposureMode`: ExposureMode
  - `setExposureMode`: void
  - `getMeteringPoint`: Point
  - `setMeteringPoint`: void
  - `getExposureBiasRange`: Array<number>
  - `setExposureBias`: void
  - `getExposureValue`: number
  - `isFocusModeSupported`: boolean
  - `getFocusMode`: FocusMode
  - `setFocusMode`: void
  - `setFocusPoint`: void
  - `getFocusPoint`: Point
  - `getFocalLength`: number
  - `getZoomRatioRange`: Array<number>
  - `getZoomRatio`: number
  - `setZoomRatio`: void
  - `isVideoStabilizationModeSupported`: boolean
  - `getActiveVideoStabilizationMode`: VideoStabilizationMode
  - `setVideoStabilizationMode`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `getSupportedBeautyTypes`: Array<BeautyType>
  - `getSupportedBeautyRange`: Array<number>
  - `getBeauty`: number
  - `setBeauty`: void
- **PhotoSessionForSys**
- **PhotoSession**
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **VideoSessionForSys**
- **VideoSession**
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **Portrait**
  - `getSupportedPortraitEffects`: Array<PortraitEffect>
  - `getPortraitEffect`: PortraitEffect
  - `setPortraitEffect`: void
- **ZoomRange**
  - `readonly min`: number
  - `readonly max`: number
- **PhysicalAperture**
  - `zoomRange`: ZoomRange
  - `apertures`: Array<number>
- **Aperture**
  - `getSupportedVirtualApertures`: Array<number>
  - `getVirtualAperture`: number
  - `setVirtualAperture`: void
  - `getSupportedPhysicalApertures`: Array<PhysicalAperture>
  - `getPhysicalAperture`: number
  - `setPhysicalAperture`: void
- **PortraitPhotoSession**
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **ManualExposure**
  - `getSupportedExposureRange`: Array<number>
  - `getExposure`: number
  - `setExposure`: void
- **NightPhotoSession**
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **CameraOutput**
  - `release`: void
  - `release`: Promise<void>
- **SketchStatusData**
  - `status`: number
  - `sketchRatio`: number
- **PreviewOutput**
  - `start`: void
  - `start`: Promise<void>
  - `stop`: void
  - `stop`: Promise<void>
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `addDeferredSurface`: void
  - `isSketchSupported`: boolean
  - `getSketchRatio`: number
  - `enableSketch`: void
  - `attachSketchSurface`: void
  - `on`: void
  - `off`: void
- **Location**
  - `latitude`: number
  - `longitude`: number
  - `altitude`: number
- **PhotoCaptureSetting**
  - `quality?`: QualityLevel
  - `rotation?`: ImageRotation
  - `location?`: Location
  - `mirror?`: boolean
- **Photo**
  - `main`: image.Image
  - `release`: Promise<void>
- **DeferredPhotoProxy**
  - `getThumbnail`: Promise<image.PixelMap>
  - `release`: Promise<void>
- **PhotoOutput**
  - `capture`: void
  - `capture`: Promise<void>
  - `capture`: void
  - `capture`: Promise<void>
  - `isDeferredImageDeliverySupported`: boolean
  - `isDeferredImageDeliveryEnabled`: boolean
  - `deferImageDelivery`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `isMirrorSupported`: boolean
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
  - `isQuickThumbnailSupported`: boolean
  - `enableQuickThumbnail`: void
  - `on`: void
  - `off`: void
- **FrameShutterInfo**
  - `captureId`: number
  - `timestamp`: number
- **CaptureStartInfo**
  - `captureId`: number
  - `time`: number
- **CaptureEndInfo**
  - `captureId`: number
  - `frameCount`: number
- **VideoOutput**
  - `start`: void
  - `start`: Promise<void>
  - `stop`: void
  - `stop`: Promise<void>
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
- **Rect**
  - `topLeftX`: number
  - `topLeftY`: number
  - `width`: number
  - `height`: number
- **MetadataObject**
  - `readonly type`: MetadataObjectType
  - `readonly timestamp`: number
  - `readonly boundingBox`: Rect
- **MetadataOutput**
  - `start`: void
  - `start`: Promise<void>
  - `stop`: void
  - `stop`: Promise<void>
  - `on`: void
  - `off`: void
  - `on`: void
  - `off`: void
#### Enums
- **CameraStatus**
  - `CAMERA_STATUS_APPEAR` = 0
  - `CAMERA_STATUS_DISAPPEAR` = 1
  - `CAMERA_STATUS_AVAILABLE` = 2
  - `CAMERA_STATUS_UNAVAILABLE` = 3
- **CameraErrorCode**
  - `INVALID_ARGUMENT` = 7400101
  - `OPERATION_NOT_ALLOWED` = 7400102
  - `SESSION_NOT_CONFIG` = 7400103
  - `SESSION_NOT_RUNNING` = 7400104
  - `SESSION_CONFIG_LOCKED` = 7400105
  - `DEVICE_SETTING_LOCKED` = 7400106
  - `CONFLICT_CAMERA` = 7400107
  - `DEVICE_DISABLED` = 7400108
  - `DEVICE_PREEMPTED` = 7400109
  - `SERVICE_FATAL_ERROR` = 7400201
- **RestoreParamType**
  - `NO_NEED_RESTORE_PARAM` = 0
  - `PRESISTENT_DEFAULT_PARAM` = 1
  - `TRANSIENT_ACTIVE_PARAM` = 2
- **TorchMode**
  - `OFF` = 0
  - `ON` = 1
  - `AUTO` = 2
- **CameraPosition**
  - `CAMERA_POSITION_UNSPECIFIED` = 0
  - `CAMERA_POSITION_BACK` = 1
  - `CAMERA_POSITION_FRONT` = 2
  - `CAMERA_POSITION_FOLD_INNER` = 3
- **CameraType**
  - `CAMERA_TYPE_DEFAULT` = 0
  - `CAMERA_TYPE_WIDE_ANGLE` = 1
  - `CAMERA_TYPE_ULTRA_WIDE` = 2
  - `CAMERA_TYPE_TELEPHOTO` = 3
  - `CAMERA_TYPE_TRUE_DEPTH` = 4
- **ConnectionType**
  - `CAMERA_CONNECTION_BUILT_IN` = 0
  - `CAMERA_CONNECTION_USB_PLUGIN` = 1
  - `CAMERA_CONNECTION_REMOTE` = 2
- **HostDeviceType**
  - `UNKNOWN_TYPE` = 0
  - `PHONE` = 0x0E
  - `TABLET` = 0x11
- **SceneMode**
  - `NORMAL_PHOTO` = 1
  - `NORMAL_VIDEO` = 2
  - `PORTRAIT_PHOTO` = 3
  - `NIGHT_PHOTO` = 4
- **CameraFormat**
  - `CAMERA_FORMAT_RGBA_8888` = 3
  - `CAMERA_FORMAT_YUV_420_SP` = 1003
  - `CAMERA_FORMAT_JPEG` = 2000
- **FlashMode**
  - `FLASH_MODE_CLOSE` = 0
  - `FLASH_MODE_OPEN` = 1
  - `FLASH_MODE_AUTO` = 2
  - `FLASH_MODE_ALWAYS_OPEN` = 3
- **ExposureMode**
  - `EXPOSURE_MODE_LOCKED` = 0
  - `EXPOSURE_MODE_AUTO` = 1
  - `EXPOSURE_MODE_CONTINUOUS_AUTO` = 2
- **FocusMode**
  - `FOCUS_MODE_MANUAL` = 0
  - `FOCUS_MODE_CONTINUOUS_AUTO` = 1
  - `FOCUS_MODE_AUTO` = 2
  - `FOCUS_MODE_LOCKED` = 3
- **FocusState**
  - `FOCUS_STATE_SCAN` = 0
  - `FOCUS_STATE_FOCUSED` = 1
  - `FOCUS_STATE_UNFOCUSED` = 2
- **SmoothZoomMode**
  - `NORMAL` = 0
- **VideoStabilizationMode**
  - `OFF` = 0
  - `LOW` = 1
  - `MIDDLE` = 2
  - `HIGH` = 3
  - `AUTO` = 4
- **BeautyType**
  - `AUTO` = 0
  - `SKIN_SMOOTH` = 1
  - `FACE_SLENDER` = 2
  - `SKIN_TONE` = 3
- **ColorEffectType**
  - `NORMAL` = 0
  - `BRIGHT` = 1
  - `SOFT` = 2
- **PortraitEffect**
  - `OFF` = 0
  - `CIRCLES` = 1
  - `HEART` = 2
  - `ROTATED` = 3
  - `STUDIO` = 4
  - `THEATER` = 5
- **ImageRotation**
  - `ROTATION_0` = 0
  - `ROTATION_90` = 90
  - `ROTATION_180` = 180
  - `ROTATION_270` = 270
- **QualityLevel**
  - `QUALITY_LEVEL_HIGH` = 0
  - `QUALITY_LEVEL_MEDIUM` = 1
  - `QUALITY_LEVEL_LOW` = 2
- **DeferredDeliveryImageType**
  - `NONE` = 0
  - `PHOTO` = 1
  - `VIDEO` = 2
- **MetadataObjectType**
  - `FACE_DETECTION` = 0
#### Functions
- `getCameraManager(context: Context): CameraManager`