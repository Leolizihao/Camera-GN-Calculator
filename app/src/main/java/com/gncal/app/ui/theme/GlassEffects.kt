package com.gncal.app.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.gncal.app.data.VisualStyle

/**
 * 玻璃质感参数表。
 *
 * 两种风格的差异全部收敛到这里：
 * - 毛玻璃（FROSTED）：以「背景模糊 + 半透白/灰」为核心，低对比、低饱和、
 *   边缘仅用极淡描边，追求稳定、克制、可读。
 * - 液态玻璃（LIQUID）：在毛玻璃基础上叠加「饱和度提升 + 镜面高光带 +
 *   内阴影 + 高对比折射描边 + 投影」，让表面呈现有厚度、有折射的液体感。
 */
data class GlassSpec(
    /** 背景采样后的模糊半径，越大越"雾" */
    val blurRadius: Dp,
    /** 背景饱和度（1.0 为原样，>1 提亮提艳，液态玻璃的关键） */
    val saturation: Float,
    /** 玻璃底色不透明度 */
    val tintAlpha: Float,
    /** 底色是否使用上下渐变（毛玻璃用） */
    val gradientTint: Boolean,
    /** 折射描边宽度 */
    val borderWidth: Dp,
    /** 折射描边亮度 */
    val borderAlpha: Float,
    /** 镜面高光强度，0 表示不画 */
    val specularAlpha: Float,
    /** 内阴影强度，0 表示不画 */
    val innerShadowAlpha: Float,
    /** 投影高度，玻璃越"厚"投影越大 */
    val elevation: Dp
) {
    companion object {
        /** 毛玻璃：均匀雾化，弱边界，强调内容可读性 */
        val Frosted = GlassSpec(
            blurRadius = 22.dp,
            saturation = 1.08f,
            tintAlpha = 0.62f,
            gradientTint = true,
            borderWidth = 1.dp,
            borderAlpha = 0.16f,
            specularAlpha = 0f,
            innerShadowAlpha = 0f,
            elevation = 0.dp
        )

        /** 液态玻璃：强折射、强高光、有厚度的液体表面 */
        val Liquid = GlassSpec(
            blurRadius = 30.dp,
            saturation = 1.35f,
            tintAlpha = 0.38f,
            gradientTint = false,
            borderWidth = 1.5.dp,
            borderAlpha = 0.55f,
            specularAlpha = 0.34f,
            innerShadowAlpha = 0.16f,
            elevation = 3.dp
        )

        fun of(style: VisualStyle): GlassSpec? = when (style) {
            VisualStyle.FROSTED -> Frosted
            VisualStyle.LIQUID -> Liquid
            VisualStyle.STANDARD -> null
        }
    }
}

/**
 * 背景绘制器：以「整屏尺寸」绘制一次装饰背景，玻璃面板内部会按自身窗口坐标
 * 取其中一块区域做采样，从而得到真实的背景模糊（backdrop blur）。
 */
typealias GlassBackdrop = @Composable (Modifier) -> Unit

val LocalGlassBackdrop = staticCompositionLocalOf<GlassBackdrop> { { } }

/**
 * 玻璃面板容器：按当前视觉风格渲染背景采样层、玻璃底色、高光与折射描边。
 *
 * 实现要点：
 * 1. 背景采样：在面板内部以「整屏尺寸 + 面板窗口偏移」重绘一次背景，
 *    再用 RenderEffect 模糊（Modifier.blur，API 31+），得到真实背景模糊；
 * 2. 只取「面板尺寸 + 2×模糊半径」的区域，避免整屏重绘带来的开销；
 * 3. API 31 以下没有 RenderEffect，自动退化为纯半透 + 描边（视觉降级但可用）；
 * 4. 液态玻璃额外叠加饱和度提升、镜面高光、内阴影与投影。
 */
@Composable
fun GlassSurface(
    style: VisualStyle,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    tint: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.() -> Unit
) {
    val spec = remember(style) { GlassSpec.of(style) }
    if (spec == null) {
        Box(modifier = modifier.clip(shape), content = content)
        return
    }

    val backdrop = LocalGlassBackdrop.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    var windowPosition by remember { mutableStateOf(Offset.Zero) }
    var surfaceSize by remember { mutableStateOf(IntSize.Zero) }
    val supportsBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                windowPosition = coordinates.positionInWindow()
                surfaceSize = coordinates.size
            }
            .then(
                if (spec.elevation > 0.dp) {
                    Modifier.shadow(spec.elevation, shape, clip = false)
                } else {
                    Modifier
                }
            )
            .clip(shape)
    ) {
        // ① 背景采样 + 模糊（真实 backdrop blur）
        if (supportsBlur && surfaceSize != IntSize.Zero) {
            val extra = spec.blurRadius * 2f
            val sampleWidth = with(density) { surfaceSize.width.toDp() } + extra
            val sampleHeight = with(density) { surfaceSize.height.toDp() } + extra
            val backgroundOffsetX = with(density) { -windowPosition.x.toDp() } + extra
            val backgroundOffsetY = with(density) { -windowPosition.y.toDp() } + extra

            Box(
                Modifier
                    .requiredSize(sampleWidth, sampleHeight)
                    .offset(x = -extra, y = -extra)
                    .blur(spec.blurRadius, BlurredEdgeTreatment.Unbounded)
                    .graphicsLayer {
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply { setToSaturation(spec.saturation) }
                        )
                    }
            ) {
                backdrop(
                    Modifier
                        .requiredSize(
                            width = configuration.screenWidthDp.dp,
                            height = configuration.screenHeightDp.dp
                        )
                        .offset(x = backgroundOffsetX, y = backgroundOffsetY)
                )
            }
        }

        // ② 玻璃底色
        Box(
            Modifier
                .matchParentSize()
                .background(rememberGlassTintBrush(spec, tint))
        )

        // ③ 镜面高光（仅液态玻璃）
        if (spec.specularAlpha > 0f) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = spec.specularAlpha),
                                Color.White.copy(alpha = spec.specularAlpha * 0.15f),
                                Color.Transparent,
                                Color.Transparent
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
            )
        }

        // ④ 内阴影：模拟玻璃厚度带来的底部暗边（仅液态玻璃）
        if (spec.innerShadowAlpha > 0f) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = spec.innerShadowAlpha)
                            )
                        )
                    )
            )
        }

        // ⑤ 折射描边：左上亮、右下暗，模拟光线穿过玻璃边缘的折射
        Box(
            Modifier
                .matchParentSize()
                .border(
                    width = spec.borderWidth,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = spec.borderAlpha),
                            Color.White.copy(alpha = spec.borderAlpha * 0.25f),
                            Color.White.copy(alpha = spec.borderAlpha * 0.6f)
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    ),
                    shape = shape
                )
        )

        content()
    }
}

/** 玻璃底色：毛玻璃用竖向渐变制造"磨砂层"，液态玻璃用接近纯透的均色 */
@Composable
private fun rememberGlassTintBrush(spec: GlassSpec, tint: Color): Brush {
    val alpha = spec.tintAlpha
    return if (spec.gradientTint) {
        Brush.verticalGradient(
            colors = listOf(
                tint.copy(alpha = alpha + 0.08f),
                tint.copy(alpha = alpha - 0.12f)
            )
        )
    } else {
        Brush.linearGradient(listOf(tint.copy(alpha = alpha), tint.copy(alpha = alpha)))
    }
}

/**
 * 应用级装饰背景（绘制实现）。
 *
 * 设计约束：
 * - 只有 1 个竖向底色 + 3 个柔和色斑，绘制成本极低，
 *   因为每个玻璃面板内部都要重绘一次它来做背景采样；
 * - 色斑足够大且明暗分明，模糊后仍能看出层次，
 *   否则毛玻璃会退化成一片纯色，等于没有效果；
 * - 全部使用 colorScheme 的角色色，跟随动态取色与主题切换。
 */
fun Modifier.glassBackdrop(): Modifier = composed {
    val colorScheme = MaterialTheme.colorScheme
    drawBehind {
        drawRect(
            brush = Brush.verticalGradient(
                listOf(colorScheme.surface, colorScheme.surfaceContainerLowest)
            )
        )
        val minDimension = size.minDimension
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colorScheme.primary.copy(alpha = 0.38f), Color.Transparent),
                center = Offset(size.width * 0.15f, size.height * 0.12f),
                radius = minDimension * 0.75f
            ),
            radius = minDimension * 0.75f,
            center = Offset(size.width * 0.15f, size.height * 0.12f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colorScheme.tertiary.copy(alpha = 0.32f), Color.Transparent),
                center = Offset(size.width * 0.85f, size.height * 0.85f),
                radius = minDimension * 0.65f
            ),
            radius = minDimension * 0.65f,
            center = Offset(size.width * 0.85f, size.height * 0.85f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colorScheme.secondary.copy(alpha = 0.26f), Color.Transparent),
                center = Offset(size.width * 0.95f, size.height * 0.5f),
                radius = minDimension * 0.55f
            ),
            radius = minDimension * 0.55f,
            center = Offset(size.width * 0.95f, size.height * 0.5f)
        )
    }
}

/** 背景采样用的背景组件：与 [Modifier.glassBackdrop] 完全同一套绘制逻辑 */
@Composable
fun AppGlassBackdrop(modifier: Modifier = Modifier) {
    Box(modifier = modifier.glassBackdrop())
}
