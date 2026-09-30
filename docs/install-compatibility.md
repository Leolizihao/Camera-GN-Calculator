# 安装稳定性保障策略

> 目标：让绝大多数 Android 设备（8.0+，尤其是 Android 15/16）能够稳定安装、不被安全机制拦截。
> 版本：v1.3.0

---

## 一、必须先讲清的事实

**侧载安装的"扫描确认"环节无法通过开发者手段关闭。** 它由设备端的 Google Play 保护机制 +
系统 `PackageInstaller` 触发，只要不是从 Play 商店安装就会出现。能做的是：

1. 让扫描**判定为安全**（签名完整、权限干净、行为无可疑特征）；
2. 提供**可验证凭据**（SHA-256、签名证书指纹、code transparency），让用户自己确认未被篡改；
3. 消除**硬性安装失败**（签名方案缺失、SDK 版本过低、包体不齐）。

结论：唯一能彻底去掉提示的途径是**上架 Google Play**；其余手段都是提高通过率与可信度。

---

## 二、签名方案矩阵（v1.3.0 已全部启用）

| 方案 | 状态 | 校验系统 | 作用 |
| --- | --- | --- | --- |
| v1（JAR） | ✅ | Android 7 以下 | 兼容老设备（当前因 minSdk 26 + R8 未产出，无影响） |
| v2 | ✅ | Android 7.0+ | 全量校验，必需 |
| **v3** | ✅（v1.2.0 为 false） | Android 9.0+ | 更强校验 + 支持密钥轮换 |
| v3.1 | — | Android 13+ | 单签名者场景不需要（多签名者/分 SDK 段签名才生成） |
| **v4** | ✅（生成 `.idsig`） | adb 增量安装 | 配合 `adb install --incremental` |

签发证书：`CN=GN Cal, OU=Mobile, O=GNCal, C=CN`，RSA 2048，
SHA-256 指纹 `9493CBED821D937DAB3B04C618F107A45DB09B37C7CFC07A30E239640EB458DB`。

**证书必须永久保持一致**：换 keystore 会让 Play 保护机制更倾向告警，且用户无法覆盖安装。

校验命令：

```powershell
apksigner verify --verbose --print-certs GNCal-v1.3.0-release.apk
```

---

## 三、SDK 与包体策略（决定"能不能装上"）

| 项 | 值 | 原因 |
| --- | --- | --- |
| `minSdk` | **26**（Android 8.0） | 覆盖绝大多数存量设备；再低会失去 Compose 的部分能力 |
| `targetSdk` | **36**（Android 16） | 满足 Play 目标 API 要求；Android 15/16 对低 targetSdk 有额外限制 |
| `compileSdk` | 37 | 使用最新构建工具，不影响运行时 |
| 16KB 内存页 | ✅ `useLegacyPackaging = false` | Android 15+ 设备要求，未适配会在部分新机上**安装失败** |
| 64 位 | ✅ 纯 Kotlin/Compose，无 native so | 天然满足 |
| 单 APK | ✅ 未做 ABI 拆分 | 避免用户选错包 |

---

## 四、降低"被判定为高风险"的概率

Play 保护机制的启发式重点盯以下特征，本项目逐项规避：

| 风险特征 | 本项目状态 |
| --- | --- |
| 短信 / 通话 / 联系人权限 | ✅ **零权限**（v1.3.0 移除了未使用的 `VIBRATE`） |
| 无障碍服务、设备管理器 | ✅ 无 |
| `REQUEST_INSTALL_PACKAGES`（自我更新） | ✅ 无 |
| 动态加载外部代码 / 热更新 / 插件化 | ✅ 无 |
| 反射调用隐藏 API | ✅ 无 |
| 第三方 SDK / 追踪库 | ✅ 无（仅 androidx + Compose） |
| 混淆后仍保留可读结构 | ✅ R8 仅做优化与瘦身，不改变行为 |

`AndroidManifest` 中无任何 `<uses-permission>`，这是最有效的加分项。

> 构建产物中会出现一条 `com.gncal.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`：
> 它由 AndroidX Core 自动生成（`protectionLevel="signature"`，仅本应用可用），
> **不是危险权限、不会弹窗、不影响 Play 保护机制判定**，无需处理。

---

## 五、可验证凭据（code transparency）

v1.3.0 的 Release 额外提供 AAB 与代码透明度文件：

```
GNCal-v1.3.0-release.apk        # 侧载安装用
GNCal-v1.3.0-release.aab        # 含 code transparency 的 App Bundle
transparency.cert               # 代码透明度公钥证书（可公开发布）
```

生成方式：

```powershell
bundletool add-transparency --bundle=app-release.aab --output=out.aab `
  --ks=transparency.jks --ks-key-alias=gncal-transparency ...
jarsigner -keystore <app keystore> -signedjar GNCal-v1.3.0-release.aab out.aab <alias>
bundletool check-transparency --mode=bundle --bundle=GNCal-v1.3.0-release.aab `
  --transparency-key-certificate=transparency.cert
```

**重要限制（官方原文）**：*Android 操作系统不会在安装时验证代码透明度文件*，安装校验始终依赖
APK 签名方案。它的用途是**开发者与终端用户自行审计**——确认 DEX / native 库哈希与开发者原始
构建一致。因此它**不会消除安装时的扫描提示**，但能让"这个包没被改过"这件事可被独立验证。

> 另注：透明度密钥必须 ≥3072 位（bundletool 1.18 强制），且与 APK 签名密钥不同。

---

## 六、Android 16（API 36）特别注意

| 事项 | 说明 |
| --- | --- |
| **高级保护模式** | 开启后系统会**禁用"安装未知应用"**，侧载被完全阻断，必须先在设置中关闭 |
| 高级侧载流程 | Google 正在推行的新侧载流程会要求开发者身份验证（与本项目无关，属分发侧政策） |
| Play 保护机制扫描 | 设置 → Play 保护机制 → 关闭"扫描应用"可去掉提示（用户侧操作） |
| 安装来源授权 | 设置 → 应用 → 特殊访问权限 → 安装未知应用 → 允许对应浏览器/文件管理器 |
| 绕过确认 UI | `adb install -r xxx.apk` 不经过 PackageInstaller 的确认界面 |

---

## 七、发布前自检清单

```powershell
# 1. 签名方案齐全
apksigner verify --verbose --print-certs dist/GNCal-v1.3.0-release.apk

# 2. 校验和
Get-FileHash dist/GNCal-v1.3.0-release.apk -Algorithm SHA256

# 3. 权限审计（应为空）
aapt2 dump permissions dist/GNCal-v1.3.0-release.apk   # 或 apkanalyzer manifest permissions

# 4. 单测
./gradlew :app:testDebugUnitTest

# 5. 关键设备验证
#    - Android 8/9（降级路径，无模糊）
#    - Android 12/13（v3 校验 + 模糊）
#    - Android 16（目标平台，边缘到边缘 + 预测返回）
```

| 检查项 | v1.3.0 |
| --- | --- |
| v2 签名 | ✅ |
| v3 签名 | ✅ |
| v4 / .idsig | ✅ |
| 零权限 | ✅ |
| minSdk 26 / targetSdk 36 | ✅ |
| 16KB 页适配 | ✅ |
| code transparency（AAB） | ✅ |
| 单元测试 | ✅ 8/8 通过 |
