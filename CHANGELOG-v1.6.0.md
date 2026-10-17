# GN Cal v1.6.0

## 新增 ISO 调节模式

工具主要面向胶片摄影，而胶片极少使用 ISO 1600 以上的高感光度。
设置页新增 **ISO 调节模式** 选项（默认胶片模式）：

| 模式 | 范围 | 调节方式 |
| --- | --- | --- |
| 胶片 ISO | **6 – 1600** | 6–9 按 1 连续；其余吸附到标准胶片感光度 |
| ISO 拓展 | 25 – 102400 | 沿用 1/3 档标准序列全范围调节 |

吸附的标准胶片感光度：

```
10, 16, 20, 25, 50, 64, 80, 100, 125, 160, 200, 250, 400, 500, 640, 800, 1000, 1250, 1600
```

## 滑杆采用对数刻度

胶片感光度按倍率分布。若把 6–1600 线性铺在滑杆上，
**ISO 100 以下会全部挤在最左侧 6% 的行程内**，10 / 16 / 20 / 25 几乎无法拖到。
因此胶片模式的滑杆按 `position = ln(iso/6) / ln(1600/6) × 100` 映射：

| ISO | 滑杆位置 |
| --- | --- |
| 6 | 0 |
| 25 | 25.5 |
| 100 | 50.3 |
| 400 | 75.2 |
| 1600 | 100 |

## 「+ / -」步长 1 微调

- 吸附只作用于**拖动滑杆**，不影响「+ / -」按钮
- 按钮直接对 ISO 数值做 ±1（夹取在 6–1600），因此可以在标准值之间取任意整数，
  例如从 ISO 250 微调到 **ISO 320**
- 实现上给 `SteppedSliderRow` 增加了方向式回调 `onStep(-1 / +1)`：
  对数刻度下无法用 `value ± 1` 得到 ISO ± 1，必须由调用方按真实数值步进

## ISO 数值与开关实时同步

- 切换模式时 `CalculatorState.applyIsoMode()` 立即把当前值夹取/吸附到新模式允许的范围：
  ISO 6400 → 胶片模式 → **1600**；ISO 6 → 拓展模式 → **25**
- 偏好经 DataStore 持久化（`iso_mode`，默认 `FILM`）

## 模型层改动

- `FlashInputs.isoIndex` → **`iso: Int`**：原来只存 1/3 档序列下标，
  无法表达胶片模式的 ISO 6–9 与标准值之间的任意整数
- `CalculatorState.isoIndex` → **`isoValue: Int`**，状态恢复时兼容旧数据
  （旧值落在 `0..36` 且小于 25 的视为下标，自动迁移为对应 ISO 值）
- `PhotoScales` 新增：`FILM_ISO_STOPS`、`FILM_MIN_ISO`、`FILM_MAX_ISO`、
  `DEFAULT_ISO`、`snapFilmIso()`、`filmIsoPosition()`、`filmIsoFromPosition()`

## 新增单元测试（共 21 项，全部通过）

| 用例 | 验证内容 |
| --- | --- |
| `snapFilmIso_singleDigitsStepByOne` | ISO 6–9 按 1 取值 |
| `snapFilmIso_snapsToStandardValuesAboveNine` | 9 以上吸附到最近标准值（按对数距离） |
| `snapFilmIso_clampsToFilmRange` | 越界值夹取到 6 / 1600 |
| `applyIsoMode_clampsValueIntoNewModeRange` | 切换模式时数值同步（6400→1600、6→25、400↔400） |
| `filmIsoPosition_isLogarithmicAndRoundTrips` | 对数刻度、ISO 100 位于轨道中段、全部标准值往返无损 |
| `filmIsoValues_stillProduceCorrectExposure` | 非标准值（320）与极低值（6）的曝光计算仍正确 |

## 验证

- `./gradlew :app:testDebugUnitTest` — 21/21 通过
- `./gradlew :app:assembleRelease` — 成功（7.50 MB，R8 压缩，签名 v2 + v3）
- `./gradlew :app:bundleRelease` — 成功；`bundletool check-transparency` 校验通过

```
APK SHA-256: 0F8F449E23067836F25380737507B906E1B8B32C738971998886A8844E67DBA3
```
