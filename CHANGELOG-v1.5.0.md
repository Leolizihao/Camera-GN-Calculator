# GN Cal v1.5.0

## 触感反馈（振动）

新增可开关、可调强度的振动反馈：

- 设置页新增 **振动反馈** 分区：启用开关 + 强度滑杆（0–100%，10% 一档）+ **试一试** 预览按钮
- 强度同时作用于振幅与时长：
  - 支持振幅控制的设备（API 26+ `hasAmplitudeControl`）映射振幅 60–255；
  - 不支持的设备退化为默认振幅，用时长（8–150 ms）体现强弱
- 触发场景按事件分级，避免"到处乱震"：

| 事件 | 场景 | 基准时长 |
| --- | --- | --- |
| `TICK` | 光圈 / ISO / 功率 / 补偿每跳一档；距离每跨 1 m 或 1 ft | 12 ms |
| `SELECT` | 切换求解模式、切换单位 | 24 ms |
| `CONFIRM` | 重置、设置页试振 | 40 ms |

- 连续型滑杆（距离）只在跨过整数单位时给一次反馈，拖动过程不会连震
- 使用 `Vibrator` + `VibrationEffect` 直接控制，而非 Compose 的 `LocalHapticFeedback`——
  后者不提供强度调节，且多数 ROM 会忽略其请求
- 无 `Vibrator` 硬件（如部分平板/模拟器）时自动静默降级
- 偏好通过 DataStore 持久化：`haptic_enabled`（默认 true）、`haptic_intensity`（默认 55）

## 主页四页左右滑动切换

- 顶部模式按钮下新增 `HorizontalPager`，四个求解模式各占一页，**左右滑动即可切换**
- 与顶部 `SingleChoiceSegmentedButtonRow` **双向同步**：
  - 滑动 → 更新 `CalculatorState.mode` 并触发 `SELECT` 振动；
  - 点击模式按钮 → `animateScrollToPage` 平滑滚到对应页
- 输入值（GN / 光圈 / 距离 / ISO / 功率 / 补偿）在四页间**共享**，切换模式不丢数据
- 每页按自己的模式渲染参数与结果：`CalculatorScreen` 新增 `mode` 参数，
  不再读取 `state.mode`，避免四页内容被同一状态串成一致
- 模式按钮下方补一行小字提示："左右滑动可切换四个求解模式"

## 权限

- 新增 `android.permission.VIBRATE`（普通权限，安装即授予、不弹窗、不读数据）
- 仍无任何危险权限

## 验证

- `./gradlew :app:assembleRelease` — 成功（7.49 MB，R8 压缩，签名 v2 + v3）
- `./gradlew :app:bundleRelease` — 成功；`bundletool check-transparency` 校验通过
- `./gradlew :app:testDebugUnitTest` — 8/8 通过

```
APK SHA-256: DBB3B01DE9EFC1815167BBEDFFB1250A2C586A87EEF7A6674902194D2676514C
```
