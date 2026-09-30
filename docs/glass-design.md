# 玻璃视觉风格设计：毛玻璃 vs 液态玻璃

> 适用版本：v1.3.0 · 引擎实现：`app/src/main/java/com/gncal/app/ui/theme/GlassEffects.kt`
> 两种风格共用同一套渲染管线，差异完全由 `GlassSpec` 参数表驱动。

---

## 一、设计目标

| 目标 | 说明 |
| --- | --- |
| 真实感 | 必须是**真实背景采样 + 模糊**，而不是"半透明 + 渐变"的伪装 |
| 可读优先 | 计算器是工具应用，玻璃只是质感层，正文对比度必须维持 Material 3 标准 |
| 可降级 | Android 8–11（API 26–30）无 `RenderEffect`，必须自动退化为可用外观 |
| 可控开销 | 不做整屏重绘，采样区域 = 面板尺寸 + 2×模糊半径 |
| 跟随主题 | 全部使用 `colorScheme` 角色色，动态取色 / 明暗模式自动适配 |

---

## 二、共用渲染管线（自下而上 5 层）

```
⑤ 折射描边   ← 左上亮、右下暗的线性渐变描边，模拟光线穿过玻璃边缘
④ 内阴影     ← 仅液态，底部暗边，制造"厚度"
③ 镜面高光   ← 仅液态，左上 → 右上的白色渐变，衰减到透明
② 玻璃底色   ← 毛玻璃用竖向渐变；液态用接近纯透的均色
① 背景采样层 ← 重绘一份屏幕背景 → 模糊 → 饱和度提升
   （内容层在最上方，不参与任何模糊或滤镜）
```

**关键实现：`Modifier` 无法采样兄弟节点的内容，所以玻璃效果必须是容器级 Composable（`GlassSurface`），不能只写成一个 Modifier。**

### ① 背景采样（真实 backdrop blur）

1. `onGloballyPositioned` 取得面板的窗口坐标与尺寸；
2. 在面板内部以 **整屏尺寸** 重绘一次 `AppGlassBackdrop`，再用 `offset(-x, -y)` 平移到与屏幕背景对齐；
3. 外层 `Box` 限定为 `面板尺寸 + 2×模糊半径` 的区域，避免整屏重绘；
4. `Modifier.blur(radius, BlurredEdgeTreatment.Unbounded)` 做模糊（内部走 `RenderEffect`，API 31+）；
5. `graphicsLayer { colorFilter = ColorMatrix.setToSaturation(...) }` 提升饱和度——**这是"液态"观感的关键**，模糊后的灰雾会被重新提亮提艳；
6. 最外层 `clip(shape)` 裁掉采样溢出。

```kotlin
Box(
    Modifier
        .requiredSize(sampleWidth, sampleHeight)
        .offset(x = -extra, y = -extra)
        .blur(spec.blurRadius, BlurredEdgeTreatment.Unbounded)
        .graphicsLayer { colorFilter = ColorFilter.colorMatrix(
            ColorMatrix().apply { setToSaturation(spec.saturation) }) }
) {
    backdrop(Modifier.requiredSize(screenW, screenH).offset(bgOffsetX, bgOffsetY))
}
```

### 背景本身的设计约束

`AppGlassBackdrop` = 1 个竖向底色 + 3 个柔和色斑（primary / tertiary / secondary）：

- **成本极低**：每个玻璃面板内部都要重绘一次它，所以只能画 3 个圆；
- **色斑要大且明暗分明**：否则模糊后退化成一片纯色，等于没有玻璃效果；
- 同一套绘制逻辑同时用于「Scaffold 真实背景」与「面板采样副本」，保证对齐一致。

---

## 三、两种风格的参数对比

| 参数 | 毛玻璃 Frosted | 液态玻璃 Liquid | 作用 |
| --- | --- | --- | --- |
| `blurRadius` | 22 dp | 30 dp | 雾化程度 |
| `saturation` | 1.08 | **1.35** | 背景饱和度，液态更"透亮" |
| `tintAlpha` | 0.62 | **0.38** | 玻璃底色不透明度，液态更清透 |
| `gradientTint` | 是（上亮下暗磨砂层） | 否（均色） | 底色是否渐变 |
| `borderWidth` | 1 dp | 1.5 dp | 描边宽度 |
| `borderAlpha` | 0.16 | **0.55** | 折射描边亮度 |
| `specularAlpha` | 0（不画） | **0.34** | 镜面高光强度 |
| `innerShadowAlpha` | 0（不画） | **0.16** | 内阴影（厚度感） |
| `elevation` | 0 dp | **3 dp** | 投影高度 |

### 毛玻璃（Frosted Glass）

- **气质**：克制、稳定、雾面。像一层磨砂亚克力盖在背景上。
- **做法**：重模糊 + 较高底色不透明度 + 极淡描边 + **不画高光/阴影**。
- **取舍**：可读性最好，长时间看不疲劳；适合作为默认推荐风格。
- **为什么底色偏厚**：磨砂层越厚，正文对比度越稳；这也是它在浅色/深色/动态取色下都不会"糊字"的原因。

### 液态玻璃（Liquid Glass）

- **气质**：有厚度、有折射、有光泽的液体表面（iOS 26 Liquid Glass / Material 3 Expressive 方向）。
- **四要素**：
  1. **饱和度提升**（1.35）：模糊会把背景洗灰，提饱和才有"玻璃透过彩色光"的通透感；
  2. **镜面高光**：左上 → 中心衰减的白色渐变，模拟单一光源在弧面上的反射；
  3. **折射描边**：左上亮 0.55 / 中部暗 0.25 / 右下回升 0.6，模拟边缘透镜的明暗变化；
  4. **内阴影 + 3dp 投影**：底部暗边 + 悬浮投影，制造"有厚度的液体"。
- **取舍**：底色更透（0.38），依赖背景层次；因此在深色模式下观感最强。

---

## 四、降级与性能策略

| 场景 | 处理 |
| --- | --- |
| API < 31（无 `RenderEffect`） | 跳过采样层，只保留底色 + 描边 → 降级为"半透明玻璃"，视觉可接受且零风险 |
| 首帧（尺寸未测得） | 先渲染底色，拿到 `onGloballyPositioned` 后再补采样层，无闪烁（底色已铺满） |
| 滚动时 | 采样层随位置变化重绘；只重绘"面板 + 2r"区域，非整屏 |
| 面板数量 | 同屏玻璃面板控制在 6–8 个以内（当前页面满足） |
| 背景成本 | 3 个 `drawCircle` + 1 个 `drawRect`，可安全地在每个面板内重绘 |

**性能红线**：不要在可滚动列表的每一项上都套玻璃容器；本项目只在结果卡、分区卡、提示卡、公式行、顶栏使用。

---

## 五、可继续增强的方向（未做，记录备查）

1. **AGSL / RuntimeShader 折射与色散**（API 33+）：用 `RuntimeShader` 做真正的透镜位移 + RGB 色散，可得到更硬核的液态效果；API 33 以下需保留当前实现作为降级分支。
2. **按压弹性形变**：`InteractionSource` + spring 动画驱动 `scale`（0.96 → 1.0）与圆角半径变化，做出"果冻"手感。
3. **动态高光跟随**：高光角度跟随设备陀螺仪或滚动位置轻微偏移（成本较高，需节流）。
4. **噪声颗粒**：叠加极低透明度的噪点纹理，削弱"塑料感"（需预生成位图，注意包体积）。

---

## 六、验收清单

- [x] 两种风格均可在设置页实时切换并持久化（DataStore）
- [x] 毛玻璃：背景经 22dp 模糊，边界极淡，正文对比度不变
- [x] 液态玻璃：高饱和 + 镜面高光 + 折射描边 + 内阴影 + 投影
- [x] API 31 以下自动降级，不崩溃、不空白
- [x] 动态取色 / 明暗模式 / 配色预设下均正常
- [x] Release 构建通过（R8 混淆后无异常）
