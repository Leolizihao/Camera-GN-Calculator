# GN Cal v1.5.1

修复 v1.5.0 中主页滑动与顶部模式按钮的两个交互问题。

## 1. 滑动时顶部按钮切换滞后

**原因**：Pager 与模式的同步读的是 `PagerState.settledPage`。
`settledPage` 只在惯性动画**完全停稳**后才更新，因此手指松开后按钮还要等 200–400 ms 才跟着变。

**修复**：

- 同步源改为 `currentPage`：页面跨过中线即更新，按钮随之切换
- 顶部 `ModeNavigation` 的选中态**直接读 `pagerState.currentPage`**，不再经过 `LaunchedEffect` 协程中转，
  与滑动**同帧**刷新，省掉一次协程调度 + 一次重组
- 附带收益：`calculatorState.mode` 现在不再被任何界面读取，模式变化不再触发整屏重组

## 2. 快速点按两个不同按钮迟滞卡顿

**原因**：原来用 `LaunchedEffect(calculatorState.mode)` 反推滚动。
每次点按都会让该 effect 重启，**取消上一段 `animateScrollToPage` 动画并从当前中间位置重新启动**，
连点时表现为反复打断的卡顿；同时每次点按都直接调用 `Vibrator`，高频 IPC 也拖慢主线程。

**修复**：

- 删掉 `LaunchedEffect(mode)` 反向驱动，改为**单向**：按钮只负责驱动 Pager（`scrollToMode`），
  Pager 再把结果写回 mode。两个方向不再互相打断
- 新增 `programmaticScrolls` 计数：点击按钮发起的滚动期间抑制沿途每一页的触感反馈，
  避免从第 1 页跳到第 4 页时连震三次
- `Haptics.perform` 增加 **25 ms 节流**，连点与快速拖动滑杆时不再高频调用振动器
- 重置操作（`CONFIRM` 振动）额外把 Pager 滚回第 1 页，不再与状态脱节

## 3. 滑动掉帧

- `HorizontalPager` 设置 `beyondViewportPageCount = 1`，预加载相邻页，
  避免滑到新页时才首次组合造成掉帧

## 验证

- `./gradlew :app:assembleRelease` — 成功（7.49 MB，R8 压缩，签名 v2 + v3）
- `./gradlew :app:bundleRelease` — 成功；`bundletool check-transparency` 校验通过
- `./gradlew :app:testDebugUnitTest` — 8/8 通过

```
APK SHA-256: 813FBD37C1F1CBB6BEAB72738C6D6CAAF83721B2EEAE7D298C3A543A278624FF
```
