# GN Cal v1.4.1

## 中文回退为系统默认字体

v1.4.0 曾把中文换成思源宋体（Noto Serif SC），本版本回退：

- 字体资源改为 **IBM Plex Sans 单独一套**（拉丁字母与数字），不再合并思源宋体
- 中文字形由 Android 字体回退链补齐（即设备默认中文字体）
- 字体体积从每字重 214 KB 降到 **51 KB**，四个字重合计约 0.2 MB，APK 从 7.90 MB 降到 **7.48 MB**
- 字重分级保留：大标题与结果数字 Bold(700)、小标题 Light(300)、正文 Regular(400)、按钮与标签 Medium(500)
  （粗/细对拉丁字符生效；中文由系统字体按同一字重渲染）

## 新增 4 套配色方案

配色预设从 4 套增加到 **8 套**，明暗模式各一套完整 Material 3 色板：

| 预设 | 色调 | 浅色主色 | 深色主色 |
| --- | --- | --- | --- |
| 琥珀橙 Amber | 暖黄橙 | `#9A5700` | `#FFB95E` |
| 海洋蓝 Ocean | 冷蓝 | `#006494` | `#8FCDFF` |
| 薄荷绿 Mint | 清新绿 | `#006E26` | `#72E284` |
| 玫瑰粉 Rose | 柔粉红 | `#984061` | `#FFB1C8` |
| **松林绿 Forest** | 深青绿 | `#1E6B4C` | `#7AD9A5` |
| **紫罗兰 Violet** | 紫 | `#6C4DC4` | `#CFBCFF` |
| **珊瑚橙 Coral** | 橙红 | `#B5442C` | `#FFB4A0` |
| **石墨灰 Slate** | 中性蓝灰 | `#4C5F70` | `#B3C7DB` |

- 新增枚举值 `FOREST` / `VIOLET` / `CORAL` / `SLATE` **追加在末尾**，老用户已保存的配色序号不受影响
- 每种配色均提供 primary / secondary / tertiary 三组角色及其 container、on 色，
  并单独指定 background、surface、surfaceVariant、outline，保证明暗模式对比度
- 三语名称齐全：简体中文、繁体中文、English

## 验证

- `./gradlew :app:assembleRelease` — 成功（7.48 MB，R8 压缩，签名 v2 + v3）
- `./gradlew :app:bundleRelease` — 成功；`bundletool check-transparency` 校验通过
- `./gradlew :app:testDebugUnitTest` — 8/8 通过
- 字体文件校验：合法 sfnt TTF，四个字重均含拉丁字形

```
APK SHA-256: E57AED92463D9E9F93E1D6A6E692F087A02572D8099F90239C52F74E01CABA32
```
