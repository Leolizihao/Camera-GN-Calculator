# GN Cal v1.5.2

## 修复：切换米 / 英尺时数值不换算

**现象**：主页切换单位后，GN 与距离的数字原地不动，只有标签后缀从 `m` 变成 `ft`。

**根因**：单位只被当作显示标签。但 GN 与距离**同为长度量纲**，GN 是「光圈 × 距离」的乘积，
它的数值必须随距离单位一起换算：`GN(ft) = GN(m) × 3.28084`。
把 GN 36 直接当成 36 ft 用，等于把灯的功率错算成原来的 3.28 倍。

**修复**：

1. `DistanceUnit.convert(value, to)` —— 统一的线性换算（GN 与距离共用）
2. `CalculatorState.convertUnit(from, to)` —— 切换单位时换算已输入的 GN 与距离文本，
   换算后再按各自格式重新格式化；非数字输入原样保留
3. `GnCalApp.onUnitChange` —— 先换算数值，再写入偏好

示例：GN 36 / 3 m → 切到英制 → **GN 118 / 9.8 ft**（物理量不变，光圈结果仍为 f/12）

## 修复：GN 过大/过小阈值未按单位换算

`GN_LARGE_THRESHOLD = 60`、`GN_SMALL_THRESHOLD = 12` 定义在米制下，
英制下直接比较会让 GN 118（≈ 36 m）被误报为「GN 过大」。
现在先 `unit.fromMeters(...)` 换算（英制阈值约 197 / 39）再比较。

## 修复：闪光灯档案的 GN 与单位脱节

- 档案 GN **统一以米制保存**（与内置 GN 24 / 36 / 60 一致）
- 选择档案时换算到当前单位再填入输入框
- 比对「当前 GN 是否等于某个档案」时也换算到当前单位（容差 0.6，抵消格式化舍入）
- 下拉与按钮上显示的 GN 附带当前单位符号，不再出现看不出单位的裸数字
- 设置页档案编辑器的 GN 标签补上 `（m）`，明确要求填米制数值

## 新增单元测试（共 15 项，全部通过）

| 用例 | 验证内容 |
| --- | --- |
| `distanceUnit_convertsLinearlyBetweenMetersAndFeet` | 换算系数正确、米→米为恒等 |
| `solveAperture_samePhysicalSetup_givesSameResultInBothUnits` | 同一物理量在两种单位下光圈结果一致；求距离结果单位后缀正确且数值成比例 |
| `gnThreshold_isConvertedToCurrentUnit` | 英制下 118 ft（≈36 m）不再误报 GN 过大 |
| `gnThreshold_warnsConsistentlyForPhysicallyIdenticalSetups` | 物理上相同的场景在两种单位下报警一致 |
| `distanceTooNear_thresholdIsConvertedToFeet` | 0.5 m 阈值换算为 1.64 ft 后生效 |
| `calculatorState_convertsInputValuesWhenUnitChanges` | 米→英尺→米 往返换算正确 |
| `calculatorState_ignoresNonNumericInputOnUnitChange` | 空值/非法输入不崩溃、不被改写 |

## 验证

- `./gradlew :app:testDebugUnitTest` — 15/15 通过
- `./gradlew :app:assembleRelease` — 成功（7.50 MB，R8 压缩，签名 v2 + v3）
- `./gradlew :app:bundleRelease` — 成功；`bundletool check-transparency` 校验通过

```
APK SHA-256: EDCE6C12691675D942B6DA554D4BCAF5982598331411F098560F9B6D2FC6E09B
```
