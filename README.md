# TetherGo · 热点直达

一个 38 KB 的安卓小工具：**点一下图标，直接跳到「更多热点设置」**——也就是 USB 共享网络开关所在的那一页。

系统里开启 USB 共享网络通常要点四层：设置 → 网络与互联网 → 个人热点 → 更多热点设置。
TetherGo 把它压缩成一次点击。

## 功能

- **图标直达**：点桌面图标，直接落在「更多热点设置」页，页面上就是「通过 USB 共享网络」开关
- **长按快捷方式**：`更多热点设置（含 USB 共享）` / `个人热点设置`，两个入口任选
- **通知栏磁贴**：下拉快捷面板点一下即可进入，不用翻设置
- **返回栈保留**：在「更多热点设置」按返回，回到「个人热点」页，而不是直接掉回桌面
- **零依赖**：纯 Java，不依赖 AndroidX，APK 仅 40 KB 左右

## 兼容性

在 **vivo / iQOO（OriginOS，Android 17）** 上端到端验证通过。

其他 ROM 走降级链，逐级尝试直到有一个能打开（每一级失败都会被捕获后继续）：

| 顺序 | 目标 | 适用 |
| --- | --- | --- |
| 1 | `android.settings.VIVO_TETHER_OTHER_SETTINGS` | vivo / iQOO「更多热点设置」 |
| 2 | `…Settings$VivoTetherOtherShareMethodsActivity` | 同上（显式组件） |
| 3 | `android.settings.VIVO_TETHER_SETTINGS` | vivo「个人热点」 |
| 4 | `android.settings.TETHER_SETTINGS` | Android 13+ 公开 Action |
| 5 | `…Settings$TetherSettingsActivity` / `…TetherSettings` / `…wifi.TetherSettings` | AOSP 及多数 OEM |
| 6 | `ACTION_WIRELESS_SETTINGS` → `ACTION_SETTINGS` | 最终保底 |

> 「更多热点设置」是 vivo 的私有页面，不在公开 SDK 里；组件名是用 `dumpsys package` + 实机截图确认的。

## 已知限制

**磁贴不能真正开关热点。** `ConnectivityManager` / `TetheringManager.startTethering` 受
`TETHER_PRIVILEGED`（signature 级）权限保护，普通第三方应用无法调用。所以磁贴的定位是
「一键直达」，而不是「一键开关」。

## 安装

```bash
adb install -r app-release.apk
```

或直接下载 Releases 里的 APK（已签名，可直接侧载）。

## 构建

需要 JDK 17 + Android SDK 34；`maven.google.com` 不可达时已配置阿里云镜像。

```bash
# 1) 生成自己的签名密钥（不要用仓库作者的那个）
keytool -genkeypair -v -keystore release.keystore -storetype PKCS12 \
        -alias tethergo -keyalg RSA -keysize 2048 -validity 10950 \
        -dname "CN=TetherGo,O=Example,C=CN"

# 2) 填好签名信息
cp keystore.properties.example keystore.properties   # 然后改里面的密码

# 3) 构建
gradle assembleRelease     # 或 ./gradlew assembleRelease
```

产物：`app/build/outputs/apk/release/app-release.apk`

## 项目结构

```
app/src/main/java/com/example/usbshortcut/
├── MainActivity.java        启动即跳转并 finish()，按 Intent action 分发
├── TetherLauncher.java      候选目标列表 + 逐个尝试（图标/快捷方式/磁贴共用）
└── HotspotTileService.java  通知栏磁贴（Android 14+ 用 PendingIntent 收起面板）
app/src/main/res/
├── xml/shortcuts.xml        长按菜单的两项
├── drawable/ic_tile_hotspot.xml, ic_shortcut_*.xml
└── mipmap-*/                自适应图标 + 各密度 PNG 回退
make_icons.py                生成 PNG 图标的脚本（Pillow）
```

## 名称与包名

- 项目名：**TetherGo（热点直达）**
- 应用名（桌面显示）：热点直达
- `applicationId`：`com.example.usbshortcut`（沿用早期版本，方便已安装用户直接升级；正式发布建议改成自己的反向域名）

## License

[MIT](LICENSE)
