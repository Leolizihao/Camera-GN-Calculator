# GN 闪光曝光计算器

![Version](https://img.shields.io/badge/Version-1.4.0-blue)
![Platform](https://img.shields.io/badge/Platform-Android%2016%20(API%2036)-3DDC84?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material%203-4285F4?logo=jetpackcompose)
![AGP](https://img.shields.io/badge/AGP-9.4.1-02303A?logo=gradle)
![Font](https://img.shields.io/badge/Font-IBM%20Plex%20Sans%20%2B%20%E6%80%9D%E6%BA%90%E5%AE%8B%E4%BD%93-orange)
![License](https://img.shields.io/badge/License-MIT-green)

> 面向 **Android 16（API 36）** 的原生闪光灯曝光计算工具。输入闪光灯 GN、光圈、ISO 与拍摄距离，
> 实时算出正确曝光参数，并在参数不匹配时给出警告与建议。

---

## 功能一览

### 曝光互算（核心）

| 模式 | 公式 | 用途 |
| --- | --- | --- |
| 求光圈 | `光圈 = 有效 GN ÷ 距离` | 已知灯与距离，确定光圈 |
| 求距离 | `距离 = 有效 GN ÷ 光圈` | 已知灯与光圈，确定有效距离 |
| 求 GN | `GN₁₀₀ = 光圈 × 距离 ÷ 增益` | 选购闪光灯的反推指标 |
| 求 ISO | `ISO = 100 × (光圈 × 距离 ÷ GN₁₀₀)²` | 灯位固定时确定感光度 |

- ISO 增益：`GN(ISO) = GN₁₀₀ × √(ISO ÷ 100)`
- 1/3 档标准档位：光圈 f/1.0–f/32、ISO 25–102400，自动推荐最近的标准光圈并给出 EV 偏差
- 高级修正：输出功率 1/1–1/128、闪光曝光补偿 ±3EV（1/3 档）
- 实时提示与警告：距离过近/过远、光圈越界、ISO 过高、所需 GN 超范围、同步速度提醒
- 光圈 — 距离对照表
- 米 / 英尺一键切换（GN 与距离同步换算）

### 字体（v1.4.0）

- **拉丁字母与数字**：IBM Plex Sans
- **中文**：思源宋体（Source Han Serif / Noto Serif SC）
- 两者**按字重合并进同一个字体文件**：Compose 的 `FontFamily` 回退链在多数设备上按列表顺序取字形，
  若中西文分拆为两个字体，中文会永远落到同一个字重，粗细区分就失效了
- 字重分配：**大标题与结果数字用粗体（700）**，**小标题用细体（300）**，正文常规（400），按钮与标签中等（500）
- 字符按应用实际用到的文案做子集（中文 518 字 + 拉丁/符号 304 个），每字重约 214 KB

### 闪光灯档案（v1.2.0）

- 可自定义名称与 GN 值的闪光灯档案列表
- 首页 GN 输入框上方快速切换
- 内置 GN 24 / GN 36 / GN 60 默认档案
- 设置页支持增、改、删，数据经 DataStore 本地持久化

### 设置与个性化（v1.1.0 / v1.4.0）

- 底部导航栏：四个计算目标一键切换
- 语言：跟随系统 / 简体中文 / **繁体中文** / English（v1.4.0 新增，无需重启）
- 字号：跟随系统 / 紧凑 90% / 大号 115%
- 主题：浅色 / 深色 / 跟随系统，动态取色 Material You
- 配色预设（关闭动态取色时）：Amber / Ocean / Mint / Rose，均支持明暗模式
- **外观设置仅保留配色**（v1.4.0 移除了毛玻璃 / 液态玻璃选项）

## Android 16 适配

| 特性 | 说明 |
| --- | --- |
| 沉浸式全屏 | API 36 强制 edge-to-edge，`enableEdgeToEdge()` + `Scaffold` 安全区内边距 |
| 预测返回手势 | `android:enableOnBackInvokedCallback="true"` |
| 动态取色 Material You | `dynamicLight/DarkColorScheme` |
| 大屏 / 折叠屏 | `calculateWindowSizeClass()`，宽屏 ≥840dp 自动双栏 |
| 16KB 内存页 | `packaging.jniLibs.useLegacyPackaging = false` |
| 主题图标 | 自适应图标含 `monochrome` 图层 |
| 系统启动画面 | `values-v31` 主题配置 `windowSplashScreen*` |

## 下载安装

从仓库的 **Releases** 页面下载最新版本 **v1.4.0**：

| 文件 | 大小 | 说明 |
| --- | --- | --- |
| `GNCal-v1.4.0-release.apk` | 7.90 MB | 侧载安装用，V2 + V3 签名（含 v4 `.idsig`） |
| `GNCal-v1.4.0-release.aab` | 19.93 MB | 含 code transparency 的 App Bundle |
| `transparency.cert` | < 1 KB | 代码透明度公钥证书，供独立校验 |

```
APK SHA-256: A71C6E2786DA620004653B8C4103AF488D3AD4441B90803BEF3A33E09AAA356E
```

```powershell
adb install -r GNCal-v1.4.0-release.apk
```

| 项目 | 值 |
| --- | --- |
| 包名 | `com.gncal.app` |
| minSdkVersion | 26（Android 8.0+） |
| targetSdkVersion | **36（Android 16）** |
| 权限 | **无（零权限）** |
| 签名证书 SHA-256 | `9493CBED…40EB458DB`（RSA 2048，永久固定） |
| 历史版本 | v1.0.0（7.33 MB）、v1.1.0（7.36 MB）、v1.2.0（7.36 MB）、v1.3.0（7.38 MB） |

> 侧载安装的"Play 保护机制扫描"提示由设备端触发，开发者无法关闭；
> 本项目通过完整签名方案、零权限与可验证凭据把拦截概率降到最低。
> 完整说明见 [安装稳定性保障](install-compatibility.md)。

## 自行构建

```bash
./gradlew :app:assembleDebug        # 调试包
./gradlew :app:assembleRelease      # 已签名发布包（v1/v2/v3/v4）
./gradlew :app:bundleRelease        # App Bundle
./gradlew :app:testDebugUnitTest    # 单元测试（8 项）
```

要求：JDK 17+（推荐 21）、Android SDK Platform 37 与 Build-Tools 37.0.0。
签名信息放在根目录 `keystore.properties`（已 gitignore），缺省时回退调试签名。
代码透明度密钥为 `transparency.jks`（已 gitignore，需 ≥3072 位）。
字体由 `.toolchain/merge_font.py` 生成（需 Python + fontTools），产物在 `app/src/main/res/font/`。

## 技术栈

| 组件 | 版本 |
| --- | --- |
| Android Gradle Plugin | 9.4.1 |
| Gradle | 9.8.0 |
| Kotlin | 2.4.20 |
| Compose BOM | 2026.09.00（material3 1.4.0） |
| compileSdk / targetSdk / minSdk | 37 / 36 / 26 |

## 目录结构

```
app/src/main/java/com/gncal/app/
├─ MainActivity.kt                入口（edge-to-edge、语言区域设置）
├─ model/
│  ├─ FlashCalculator.kt          曝光计算核心（纯 Kotlin，可单测）
│  └─ FlashProfile.kt             闪光灯档案数据模型
├─ data/UserPreferences.kt        DataStore 偏好（单位/主题/语言/字号/配色/档案）
└─ ui/
   ├─ GnCalApp.kt                 顶部导航、路由、公式弹层、关于对话框
   ├─ theme/
   │  ├─ Theme.kt                 Material 3 主题、动态取色、语言与字阶
   │  ├─ Type.kt                  字族与字阶（IBM Plex Sans + 思源宋体，按标题分粗细）
   │  └─ ColorPresets.kt          配色预设
   ├─ calc/                       计算页（模式、参数、结果、对照表）
   └─ settings/                   设置页

app/src/main/res/
├─ font/gn_font_{light,regular,medium,bold}.ttf   中西文合并字体（每档约 214 KB）
└─ values / values-en / values-zh-rTW             简中 / 英文 / 繁中
```

## 文档

- [安装稳定性保障策略](install-compatibility.md)

## 变更记录

- [v1.4.0](../CHANGELOG-v1.4.md)：移除玻璃风格、IBM Plex Sans + 思源宋体合并字体、新增繁体中文、零权限
- [v1.3.0](../CHANGELOG-v1.3.md)：v3/v4 签名、code transparency（玻璃风格已于 v1.4.0 移除）
- [v1.2.0](../CHANGELOG-v1.2.md)：自定义闪光灯档案
- [v1.1.0](../CHANGELOG-v1.1.md)：底部导航、设置页、语言/字号/视觉风格/配色预设

## 许可

[MIT](../LICENSE) — 计算结果仅供参考，实际拍摄请结合测光与直方图微调。
