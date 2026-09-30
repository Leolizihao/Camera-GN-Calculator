# GN 闪光曝光计算器（GN Cal）

![Version](https://img.shields.io/badge/Version-1.2.0-blue)
![Platform](https://img.shields.io/badge/Platform-Android%2016%20(API%2036)-3DDC84?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?logo=kotlin)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material%203-4285F4?logo=jetpackcompose)
![License](https://img.shields.io/badge/License-MIT-green)

面向 Android 16（API 36）的原生闪光灯曝光计算工具，使用 **Kotlin + Jetpack Compose + Material 3** 实现。

> GitHub Pages 项目主页：[`docs/index.md`](docs/index.md)
> 变更记录：[v1.1.0](CHANGELOG-v1.1.md) · [v1.2.0](CHANGELOG-v1.2.md)

## 功能

- **四参数互算**（输入即算，实时刷新）
  - 求光圈：`光圈 = 有效 GN ÷ 距离`
  - 求距离：`距离 = 有效 GN ÷ 光圈`
  - 求 GN：`GN₁₀₀ = 光圈 × 距离 ÷ 增益系数`（选购闪光灯）
  - 求 ISO：`ISO = 100 × (光圈 × 距离 ÷ GN₁₀₀)²`
- **ISO 增益**：`GN(ISO) = GN₁₀₀ × √(ISO ÷ 100)`
- **标准档位**：光圈与感光度均按 1/3 档标准序列（f/1.0–f/32，ISO 25–102400）取值，自动推荐最接近的标准光圈并给出 EV 偏差
- **高级修正**：闪光输出功率 1/1 – 1/128、闪光曝光补偿 −3EV – +3EV（1/3 档）
- **单位切换**：米 / 英尺（GN 与距离同步换算）
- **提示与警告**：距离过近/过远、光圈越界、ISO 过高、所需 GN 超范围、闪光灯同步速度提醒
- **光圈 — 距离对照表**：当前有效 GN 下各档光圈对应的拍摄距离
- **闪光灯档案**（v1.2.0）：自定义名称与 GN 的灯具列表，内置 GN 24 / 36 / 60，支持增删改并本地持久化
- **设置与个性化**（v1.1.0）：底部导航、语言（跟随系统/中文/English）、字号（90%/100%/115%）、视觉风格（标准/磨砂玻璃/液态玻璃）、配色预设（Amber/Ocean/Mint/Rose）
- **偏好持久化**：DataStore 保存单位、主题、语言、字号、视觉风格、配色与闪光灯档案

## Android 16 适配

| 特性 | 实现位置 |
| --- | --- |
| 沉浸式全屏（API 36 强制） | `MainActivity.enableEdgeToEdge()` + `Scaffold` 内边距 |
| 预测返回手势 | `AndroidManifest` 中 `android:enableOnBackInvokedCallback="true"` |
| 动态取色 Material You | `Theme.kt` 中 `dynamicLight/DarkColorScheme`，可在设置中关闭 |
| 大屏 / 折叠屏自适应 | `calculateWindowSizeClass()`，宽屏（≥840dp）自动切换双栏布局 |
| 16KB 内存页 | `build.gradle.kts` 中 `packaging.jniLibs.useLegacyPackaging = false` |
| 主题图标 | 自适应图标提供 `monochrome` 图层（Android 13+） |
| 系统启动画面 | `values-v31/themes.xml` 配置 `windowSplashScreen*` |
| 系统栏图标配色 | `WindowCompat.getInsetsController` 按主题切换（不再直接设置系统栏颜色） |

## 技术栈（已验证可构建）

| 组件 | 版本 |
| --- | --- |
| Android Gradle Plugin | 9.4.1 |
| Gradle | 9.8.0 |
| Kotlin | 2.4.20 |
| Compose BOM | 2026.09.00（material3 1.4.0） |
| compileSdk / targetSdk / minSdk | 37 / **36（Android 16）** / 26 |
| versionCode / versionName | 3 / 1.2.0 |

> AGP 9 起内置 Kotlin 支持，项目中**不再**应用 `org.jetbrains.kotlin.android` 插件，仅保留 `org.jetbrains.kotlin.plugin.compose`。

## 环境要求

- JDK 17 及以上（推荐 JDK 21；本机已有的 JDK 26 高于 Gradle 9 支持范围，需用 17/21 构建）
- Android SDK Platform 37（编译用）+ Build-Tools 37.0.0；运行时目标为 Android 16（API 36）

命令行构建时需指定 JDK 21：

```powershell
$env:JAVA_HOME = "D:\GN Cal\.toolchain\jdk\jdk-21.0.12.1+1"
.\gradlew :app:assembleDebug
```

> 本仓库工作区内可准备一套本地工具链 `.toolchain/`（JDK 21 + Android SDK），已加入 `.gitignore`，不会入库。

## 构建、签名与安装

```bash
./gradlew :app:assembleDebug        # 调试包（applicationId 为 com.gncal.app.debug）
./gradlew :app:assembleRelease      # 已签名发布包
./gradlew :app:testDebugUnitTest    # 计算逻辑单元测试（8 项）
```

签名配置读取根目录 `keystore.properties`（已在 `.gitignore` 中），缺省回退到调试签名，
因此 `assembleRelease` 始终产出**可直接安装**的 APK：

```properties
storeFile=D:/GN Cal/.toolchain/gncal-release.jks
storePassword=gncal2026
keyAlias=gncal
keyPassword=gncal2026
```

已产出并校验的发布包：

| 文件 | 大小 | 校验 |
| --- | --- | --- |
| `dist/GNCal-v1.2.0-release.apk` | 7.36 MB | SHA-256 `45E638C7CB267A9E515FE8B020E4D623423552AF33C85A3A1BBAD74C11A7D724` |
| `dist/GNCal-v1.1.0-release.apk` | 7.36 MB | — |
| `dist/GNCal-v1.0.0-release.apk` | 7.33 MB | — |

均为 V2 签名、包名 `com.gncal.app`、`minSdk 26` / `targetSdk 36`。安装：

```powershell
adb install -r dist\GNCal-v1.2.0-release.apk
```

## 目录结构

```
app/src/main/java/com/gncal/app/
├─ MainActivity.kt                入口（edge-to-edge、语言与字阶配置）
├─ model/
│  ├─ FlashCalculator.kt          曝光计算核心（纯 Kotlin，含档位序列与提示判定）
│  └─ FlashProfile.kt             闪光灯档案数据模型
├─ data/UserPreferences.kt        DataStore 偏好（单位/主题/语言/字号/视觉风格/配色/档案）
└─ ui/
   ├─ GnCalApp.kt                 底部导航、路由、公式弹层、关于对话框
   ├─ theme/                      主题、配色预设、视觉特效、字阶缩放
   ├─ calc/                       计算页（模式、参数、结果、对照表）
   └─ settings/                   设置页
```

## 已验证

- `:app:assembleDebug` 成功
- `:app:assembleRelease` 成功并签名（R8 压缩后约 7.36 MB，`apksigner verify` 通过）
- `:app:testDebugUnitTest` 8/8 通过，覆盖 ISO 增益、光圈/距离/GN/ISO 互算、功率衰减、英尺单位与非法输入

## 许可

[MIT](LICENSE) — 计算结果仅供参考，实际拍摄请结合测光与直方图微调。
