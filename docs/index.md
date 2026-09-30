# GN 闪光曝光计算器

![Version](https://img.shields.io/badge/Version-1.2.0-blue)
![Platform](https://img.shields.io/badge/Platform-Android%2016%20(API%2036)-3DDC84?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material%203-4285F4?logo=jetpackcompose)
![AGP](https://img.shields.io/badge/AGP-9.4.1-02303A?logo=gradle)
![Tests](https://img.shields.io/badge/Unit%20Tests-8%2F8%20passing-brightgreen)
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

### 闪光灯档案（v1.2.0）

- 可自定义名称与 GN 值的闪光灯档案列表
- 首页 GN 输入框上方快速切换
- 内置 GN 24 / GN 36 / GN 60 默认档案
- 设置页支持增、改、删，数据经 DataStore 本地持久化

### 设置与个性化（v1.1.0）

- 底部导航栏：四个计算目标一键切换
- 语言：跟随系统 / 简体中文 / English（无需重启）
- 字号：跟随系统 / 紧凑 90% / 大号 115%
- 视觉风格：标准 Material 3 / 磨砂玻璃 / 液态玻璃（GPU 友好，非实时模糊）
- 配色预设（关闭动态取色时）：Amber / Ocean / Mint / Rose，均支持明暗模式
- 主题：浅色 / 深色 / 跟随系统，动态取色 Material You

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

从仓库的 **Releases** 页面下载最新版本 **v1.2.0**（`GNCal-v1.2.0-release.apk`，7.36 MB，V2 签名）：

```
SHA-256: 45E638C7CB267A9E515FE8B020E4D623423552AF33C85A3A1BBAD74C11A7D724
```

```powershell
adb install -r GNCal-v1.2.0-release.apk
```

| 项目 | 值 |
| --- | --- |
| 包名 | `com.gncal.app` |
| minSdkVersion | 26（Android 8.0+） |
| targetSdkVersion | **36（Android 16）** |
| 历史版本 | v1.0.0（7.33 MB）、v1.1.0（7.36 MB） |

## 自行构建

```bash
./gradlew :app:assembleDebug        # 调试包
./gradlew :app:assembleRelease      # 已签名发布包
./gradlew :app:testDebugUnitTest    # 单元测试（8 项）
```

要求：JDK 17+（推荐 21）、Android SDK Platform 37 与 Build-Tools 37.0.0。
签名信息放在根目录 `keystore.properties`（已 gitignore），缺省时回退调试签名。

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
├─ MainActivity.kt                入口（edge-to-edge、语言配置）
├─ model/
│  ├─ FlashCalculator.kt          曝光计算核心（纯 Kotlin，可单测）
│  └─ FlashProfile.kt             闪光灯档案数据模型
├─ data/UserPreferences.kt        DataStore 偏好（单位/主题/语言/字号/配色/档案）
└─ ui/
   ├─ GnCalApp.kt                 底部导航、路由、公式弹层、关于对话框
   ├─ theme/                      主题、配色预设、视觉特效、字阶缩放
   ├─ calc/                       计算页（模式、参数、结果、对照表）
   └─ settings/                   设置页
```

## 变更记录

- [v1.2.0](../CHANGELOG-v1.2.md)：自定义闪光灯档案
- [v1.1.0](../CHANGELOG-v1.1.md)：底部导航、设置页、语言/字号/视觉风格/配色预设

## 许可

[MIT](../LICENSE) — 计算结果仅供参考，实际拍摄请结合测光与直方图微调。
