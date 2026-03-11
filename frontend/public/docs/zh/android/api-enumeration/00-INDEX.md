# Android 11 (API 30) — 完整公共 API 枚举

## 总索引

**来源：** `frameworks/base/api/current.txt`（82227 行）

### 总体统计

| 指标 | 数量 |
|--------|------:|
| 包 | 217 |
| 类型（类/接口/枚举） | 4617 |
| 方法 | 30254 |
| 字段/常量 | 21839 |
| 构造函数 | 4294 |
| **API 成员总计** | **56387** |

### 报告列表

| # | 报告 | 包数 | 类型数 | 方法数 | 字段数 | 构造函数数 |
|---|--------|:--------:|------:|--------:|-------:|------:|
| 01 | [Android 核心](A-android-core.md) | 2 | 25 | 0 | 2588 | 25 |
| 02 | [Android 应用](B-android-app.md) | 10 | 217 | 1780 | 883 | 160 |
| 03 | [Android 内容](C-android-content.md) | 6 | 192 | 1373 | 1188 | 212 |
| 04 | [Android 图形](D-android-graphics.md) | 8 | 264 | 2744 | 2111 | 245 |
| 05 | [Android 硬件](E-android-hardware.md) | 8 | 85 | 348 | 513 | 44 |
| 06 | [Android 媒体](F-android-media.md) | 8 | 315 | 1713 | 2006 | 180 |
| 07 | [Android 网络](G-android-net.md) | 14 | 179 | 853 | 419 | 98 |
| 08 | [Android 操作系统](H-android-os-system.md) | 8 | 183 | 803 | 999 | 154 |
| 09 | [Android 提供者与服务](I-android-provider-service.md) | 17 | 326 | 672 | 1872 | 124 |
| 10 | [Android 电话与通信](J-android-telephony-telecom.md) | 10 | 145 | 1025 | 1714 | 68 |
| 11 | [Android 文本与工具](K-android-text-util.md) | 7 | 211 | 1041 | 374 | 223 |
| 12 | [Android 视图](L-android-view.md) | 9 | 298 | 2530 | 1257 | 254 |
| 13 | [Android 控件](M-android-widget.md) | 2 | 181 | 1533 | 113 | 326 |
| 14 | [Android 其他](N-android-other.md) | 29 | 571 | 3699 | 3381 | 460 |
| 15 | [Java 标准库](O-java-standard.md) | 41 | 1133 | 8197 | 1424 | 1430 |
| 16 | [Javax Org 其他](P-javax-org-other.md) | 38 | 292 | 1943 | 997 | 291 |
| | **总计** | **217** | **4617** | **30254** | **21839** | **4294** |

### 全部 217 个包

| # | 包 | 类型数 | 方法数 | 字段数 |
|---|---------|------:|--------:|-------:|
| 1 | `android` | 25 | 0 | 2588 |
| 2 | `android.accessibilityservice` | 14 | 82 | 95 |
| 3 | `android.accounts` | 13 | 49 | 50 |
| 4 | `android.animation` | 26 | 199 | 9 |
| 5 | `android.annotation` | 0 | 0 | 0 |
| 6 | `android.app` | 149 | 1116 | 490 |
| 7 | `android.app.admin` | 17 | 265 | 227 |
| 8 | `android.app.assist` | 4 | 66 | 5 |
| 9 | `android.app.backup` | 11 | 41 | 4 |
| 10 | `android.app.blob` | 3 | 19 | 0 |
| 11 | `android.app.job` | 8 | 73 | 14 |
| 12 | `android.app.role` | 1 | 2 | 8 |
| 13 | `android.app.slice` | 7 | 53 | 41 |
| 14 | `android.app.usage` | 12 | 86 | 42 |
| 15 | `android.appwidget` | 5 | 59 | 52 |
| 16 | `android.bluetooth` | 29 | 191 | 486 |
| 17 | `android.bluetooth.le` | 20 | 130 | 56 |
| 18 | `android.companion` | 10 | 17 | 1 |
| 19 | `android.content` | 77 | 606 | 541 |
| 20 | `android.content.pm` | 42 | 270 | 516 |
| 21 | `android.content.res` | 14 | 106 | 99 |
| 22 | `android.content.res.loader` | 3 | 5 | 0 |
| 23 | `android.database` | 26 | 250 | 19 |
| 24 | `android.database.sqlite` | 30 | 136 | 13 |
| 25 | `android.drm` | 22 | 0 | 0 |
| 26 | `android.gesture` | 13 | 105 | 23 |
| 27 | `android.graphics` | 98 | 708 | 158 |
| 28 | `android.graphics.drawable` | 32 | 356 | 20 |
| 29 | `android.graphics.drawable.shapes` | 6 | 18 | 0 |
| 30 | `android.graphics.fonts` | 7 | 3 | 13 |
| 31 | `android.graphics.pdf` | 6 | 22 | 2 |
| 32 | `android.graphics.text` | 6 | 7 | 8 |
| 33 | `android.hardware` | 29 | 81 | 149 |
| 34 | `android.hardware.biometrics` | 6 | 10 | 29 |
| 35 | `android.hardware.camera2` | 17 | 80 | 241 |
| 36 | `android.hardware.camera2.params` | 16 | 84 | 29 |
| 37 | `android.hardware.display` | 3 | 18 | 6 |
| 38 | `android.hardware.fingerprint` | 3 | 0 | 17 |
| 39 | `android.hardware.input` | 2 | 7 | 2 |
| 40 | `android.hardware.usb` | 9 | 68 | 40 |
| 41 | `android.icu.lang` | 25 | 118 | 1333 |
| 42 | `android.icu.math` | 2 | 56 | 23 |
| 43 | `android.icu.number` | 26 | 77 | 0 |
| 44 | `android.icu.text` | 101 | 1067 | 318 |
| 45 | `android.icu.util` | 38 | 349 | 397 |
| 46 | `android.inputmethodservice` | 13 | 100 | 10 |
| 47 | `android.location` | 25 | 231 | 87 |
| 48 | `android.media` | 222 | 1065 | 1413 |
| 49 | `android.media.audiofx` | 38 | 241 | 109 |
| 50 | `android.media.browse` | 3 | 22 | 4 |
| 51 | `android.media.effect` | 4 | 11 | 27 |
| 52 | `android.media.midi` | 13 | 55 | 13 |
| 53 | `android.media.projection` | 2 | 7 | 0 |
| 54 | `android.media.session` | 14 | 131 | 34 |
| 55 | `android.media.tv` | 19 | 181 | 406 |
| 56 | `android.mtp` | 7 | 74 | 117 |
| 57 | `android.net` | 62 | 307 | 140 |
| 58 | `android.net.http` | 5 | 30 | 6 |
| 59 | `android.net.nsd` | 5 | 30 | 8 |
| 60 | `android.net.rtp` | 4 | 23 | 15 |
| 61 | `android.net.sip` | 11 | 105 | 28 |
| 62 | `android.net.ssl` | 2 | 4 | 0 |
| 63 | `android.net.wifi` | 29 | 112 | 112 |
| 64 | `android.net.wifi.aware` | 18 | 61 | 9 |
| 65 | `android.net.wifi.hotspot2` | 2 | 9 | 0 |
| 66 | `android.net.wifi.hotspot2.omadm` | 1 | 1 | 0 |
| 67 | `android.net.wifi.hotspot2.pps` | 5 | 46 | 0 |
| 68 | `android.net.wifi.p2p` | 22 | 73 | 47 |
| 69 | `android.net.wifi.p2p.nsd` | 6 | 13 | 4 |
| 70 | `android.net.wifi.rtt` | 7 | 39 | 50 |
| 71 | `android.nfc` | 13 | 35 | 44 |
| 72 | `android.nfc.cardemulation` | 5 | 28 | 17 |
| 73 | `android.nfc.tech` | 11 | 118 | 23 |
| 74 | `android.opengl` | 36 | 1004 | 1829 |
| 75 | `android.os` | 92 | 567 | 323 |
| 76 | `android.os.health` | 8 | 32 | 75 |
| 77 | `android.os.storage` | 4 | 22 | 13 |
| 78 | `android.os.strictmode` | 20 | 1 | 0 |
| 79 | `android.preference` | 24 | 0 | 0 |
| 80 | `android.print` | 19 | 53 | 105 |
| 81 | `android.print.pdf` | 1 | 0 | 0 |
| 82 | `android.printservice` | 5 | 19 | 7 |
| 83 | `android.provider` | 215 | 212 | 1627 |
| 84 | `android.renderscript` | 73 | 626 | 81 |
| 85 | `android.sax` | 7 | 13 | 0 |
| 86 | `android.se.omapi` | 5 | 12 | 0 |
| 87 | `android.security` | 15 | 18 | 9 |
| 88 | `android.security.identity` | 20 | 6 | 8 |
| 89 | `android.security.keystore` | 14 | 42 | 36 |
| 90 | `android.service.autofill` | 39 | 68 | 29 |
| 91 | `android.service.carrier` | 9 | 21 | 12 |
| 92 | `android.service.chooser` | 2 | 0 | 0 |
| 93 | `android.service.controls` | 5 | 8 | 65 |
| 94 | `android.service.controls.actions` | 5 | 9 | 11 |
| 95 | `android.service.controls.templates` | 7 | 17 | 18 |
| 96 | `android.service.dreams` | 1 | 42 | 2 |
| 97 | `android.service.media` | 4 | 17 | 4 |
| 98 | `android.service.notification` | 8 | 97 | 60 |
| 99 | `android.service.quickaccesswallet` | 9 | 24 | 5 |
| 100 | `android.service.quicksettings` | 2 | 26 | 7 |
| 101 | `android.service.restrictions` | 1 | 2 | 0 |
| 102 | `android.service.textservice` | 1 | 10 | 1 |
| 103 | `android.service.voice` | 15 | 92 | 28 |
| 104 | `android.service.vr` | 1 | 3 | 1 |
| 105 | `android.service.wallpaper` | 2 | 24 | 2 |
| 106 | `android.speech` | 6 | 31 | 52 |
| 107 | `android.speech.tts` | 10 | 73 | 51 |
| 108 | `android.system` | 10 | 115 | 535 |
| 109 | `android.telecom` | 29 | 494 | 266 |
| 110 | `android.telephony` | 71 | 415 | 1127 |
| 111 | `android.telephony.cdma` | 1 | 10 | 0 |
| 112 | `android.telephony.data` | 2 | 22 | 26 |
| 113 | `android.telephony.emergency` | 1 | 6 | 16 |
| 114 | `android.telephony.euicc` | 3 | 7 | 42 |
| 115 | `android.telephony.gsm` | 5 | 6 | 0 |
| 116 | `android.telephony.ims` | 9 | 10 | 187 |
| 117 | `android.telephony.ims.feature` | 2 | 0 | 4 |
| 118 | `android.telephony.mbms` | 22 | 55 | 46 |
| 119 | `android.text` | 53 | 284 | 96 |
| 120 | `android.text.format` | 4 | 26 | 26 |
| 121 | `android.text.method` | 25 | 116 | 7 |
| 122 | `android.text.style` | 59 | 221 | 94 |
| 123 | `android.text.util` | 5 | 23 | 7 |
| 124 | `android.transition` | 25 | 145 | 10 |
| 125 | `android.util` | 64 | 355 | 111 |
| 126 | `android.util.proto` | 1 | 16 | 33 |
| 127 | `android.view` | 154 | 1700 | 890 |
| 128 | `android.view.accessibility` | 18 | 290 | 155 |
| 129 | `android.view.animation` | 26 | 98 | 40 |
| 130 | `android.view.autofill` | 3 | 39 | 6 |
| 131 | `android.view.contentcapture` | 11 | 29 | 5 |
| 132 | `android.view.inputmethod` | 25 | 221 | 71 |
| 133 | `android.view.inspector` | 10 | 33 | 0 |
| 134 | `android.view.textclassifier` | 43 | 74 | 86 |
| 135 | `android.view.textservice` | 8 | 46 | 4 |
| 136 | `android.webkit` | 56 | 327 | 67 |
| 137 | `android.widget` | 177 | 1525 | 113 |
| 138 | `android.widget.inline` | 4 | 8 | 0 |
| 139 | `dalvik.annotation` | 0 | 0 | 0 |
| 140 | `dalvik.bytecode` | 2 | 0 | 265 |
| 141 | `dalvik.system` | 6 | 2 | 0 |
| 142 | `java.awt.font` | 3 | 12 | 79 |
| 143 | `java.beans` | 5 | 23 | 0 |
| 144 | `java.io` | 79 | 441 | 79 |
| 145 | `java.lang` | 97 | 670 | 345 |
| 146 | `java.lang.annotation` | 6 | 11 | 0 |
| 147 | `java.lang.invoke` | 11 | 102 | 13 |
| 148 | `java.lang.ref` | 5 | 8 | 0 |
| 149 | `java.lang.reflect` | 24 | 85 | 15 |
| 150 | `java.math` | 4 | 72 | 15 |
| 151 | `java.net` | 64 | 445 | 87 |
| 152 | `java.nio` | 14 | 175 | 2 |
| 153 | `java.nio.channels` | 54 | 188 | 7 |
| 154 | `java.nio.channels.spi` | 6 | 39 | 0 |
| 155 | `java.nio.charset` | 12 | 77 | 11 |
| 156 | `java.nio.charset.spi` | 1 | 2 | 0 |
| 157 | `java.nio.file` | 47 | 160 | 4 |
| 158 | `java.nio.file.attribute` | 25 | 64 | 3 |
| 159 | `java.nio.file.spi` | 2 | 29 | 0 |
| 160 | `java.security` | 85 | 333 | 11 |
| 161 | `java.security.acl` | 8 | 25 | 0 |
| 162 | `java.security.cert` | 53 | 283 | 0 |
| 163 | `java.security.interfaces` | 13 | 27 | 8 |
| 164 | `java.security.spec` | 27 | 65 | 9 |
| 165 | `java.sql` | 48 | 793 | 134 |
| 166 | `java.text` | 30 | 265 | 43 |
| 167 | `java.time` | 18 | 606 | 23 |
| 168 | `java.time.chrono` | 21 | 317 | 10 |
| 169 | `java.time.format` | 8 | 85 | 16 |
| 170 | `java.time.temporal` | 16 | 99 | 12 |
| 171 | `java.time.zone` | 5 | 39 | 0 |
| 172 | `java.util` | 124 | 915 | 86 |
| 173 | `java.util.concurrent` | 71 | 680 | 1 |
| 174 | `java.util.concurrent.atomic` | 16 | 194 | 0 |
| 175 | `java.util.concurrent.locks` | 14 | 177 | 0 |
| 176 | `java.util.function` | 43 | 78 | 0 |
| 177 | `java.util.jar` | 11 | 40 | 200 |
| 178 | `java.util.logging` | 17 | 117 | 7 |
| 179 | `java.util.prefs` | 9 | 94 | 5 |
| 180 | `java.util.regex` | 4 | 29 | 9 |
| 181 | `java.util.stream` | 13 | 233 | 0 |
| 182 | `java.util.zip` | 20 | 100 | 190 |
| 183 | `javax.crypto` | 24 | 158 | 9 |
| 184 | `javax.crypto.interfaces` | 4 | 6 | 3 |
| 185 | `javax.crypto.spec` | 16 | 42 | 4 |
| 186 | `javax.microedition.khronos.egl` | 7 | 25 | 72 |
| 187 | `javax.microedition.khronos.opengles` | 6 | 267 | 461 |
| 188 | `javax.net` | 2 | 11 | 0 |
| 189 | `javax.net.ssl` | 43 | 238 | 2 |
| 190 | `javax.security.auth` | 6 | 21 | 0 |
| 191 | `javax.security.auth.callback` | 4 | 7 | 0 |
| 192 | `javax.security.auth.login` | 1 | 0 | 0 |
| 193 | `javax.security.auth.x500` | 1 | 4 | 3 |
| 194 | `javax.security.cert` | 7 | 18 | 0 |
| 195 | `javax.sql` | 15 | 164 | 0 |
| 196 | `javax.xml` | 1 | 0 | 12 |
| 197 | `javax.xml.datatype` | 6 | 84 | 38 |
| 198 | `javax.xml.namespace` | 2 | 9 | 0 |
| 199 | `javax.xml.parsers` | 6 | 70 | 0 |
| 200 | `javax.xml.transform` | 12 | 47 | 12 |
| 201 | `javax.xml.transform.dom` | 3 | 11 | 2 |
| 202 | `javax.xml.transform.sax` | 5 | 26 | 4 |
| 203 | `javax.xml.transform.stream` | 2 | 16 | 2 |
| 204 | `javax.xml.validation` | 6 | 45 | 0 |
| 205 | `javax.xml.xpath` | 11 | 28 | 8 |
| 206 | `org.apache.http.conn` | 1 | 0 | 0 |
| 207 | `org.apache.http.conn.scheme` | 3 | 0 | 0 |
| 208 | `org.apache.http.conn.ssl` | 6 | 0 | 0 |
| 209 | `org.apache.http.params` | 3 | 0 | 0 |
| 210 | `org.json` | 5 | 75 | 0 |
| 211 | `org.w3c.dom` | 28 | 171 | 48 |
| 212 | `org.w3c.dom.ls` | 8 | 47 | 14 |
| 213 | `org.xml.sax` | 17 | 64 | 0 |
| 214 | `org.xml.sax.ext` | 8 | 46 | 0 |
| 215 | `org.xml.sax.helpers` | 10 | 134 | 2 |
| 216 | `org.xmlpull.v1` | 4 | 75 | 25 |
| 217 | `org.xmlpull.v1.sax2` | 1 | 32 | 11 |
