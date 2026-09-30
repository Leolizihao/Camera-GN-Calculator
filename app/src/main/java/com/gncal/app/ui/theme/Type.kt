package com.gncal.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gncal.app.R

/**
 * 应用字族：拉丁字母与数字用 IBM Plex Sans，中文用思源宋体（Noto Serif SC）。
 *
 * 两个字体已按字重**合并进同一个字体文件**（见 .toolchain/merge_font.py）：
 * - Compose 的 FontFamily 回退链在多数设备上按顺序取字形，
 *   若中西文分拆成两个字体，中文会永远命中同一个字重，
 *   「大标题粗体 / 小标题细体」就无法生效；
 * - 合并后每个字重一个文件，字重由 TextStyle 精确匹配，中西文各取自己字形；
 * - 字符按应用实际用到的文案做子集（中文 518 字 + 拉丁/符号 304 个），
 *   每个字重约 214 KB，四个字重合计约 0.9 MB。
 */
val GnFontFamily = FontFamily(
    Font(R.font.gn_font_light, FontWeight.Light),    // 300 细体：小标题
    Font(R.font.gn_font_regular, FontWeight.Normal), // 400 常规：正文
    Font(R.font.gn_font_medium, FontWeight.Medium),  // 500 中等：按钮与标签
    Font(R.font.gn_font_bold, FontWeight.Bold)       // 700 粗体：大标题与结果数字
)

/**
 * Material 3 字阶：大标题用粗体、小标题用细体，正文常规。
 */
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = GnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = GnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = GnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    titleLarge = TextStyle(
        fontFamily = GnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = GnFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = GnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = GnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = GnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)

/** Scales the complete Material type ramp without loading any additional font assets. */
fun Typography.scaled(scale: Float): Typography {
    fun TextStyle.scaledStyle(): TextStyle = copy(
        fontSize = fontSize * scale,
        lineHeight = lineHeight * scale
    )

    return copy(
        displayLarge = displayLarge.scaledStyle(),
        displayMedium = displayMedium.scaledStyle(),
        displaySmall = displaySmall.scaledStyle(),
        headlineLarge = headlineLarge.scaledStyle(),
        headlineMedium = headlineMedium.scaledStyle(),
        headlineSmall = headlineSmall.scaledStyle(),
        titleLarge = titleLarge.scaledStyle(),
        titleMedium = titleMedium.scaledStyle(),
        titleSmall = titleSmall.scaledStyle(),
        bodyLarge = bodyLarge.scaledStyle(),
        bodyMedium = bodyMedium.scaledStyle(),
        bodySmall = bodySmall.scaledStyle(),
        labelLarge = labelLarge.scaledStyle(),
        labelMedium = labelMedium.scaledStyle(),
        labelSmall = labelSmall.scaledStyle()
    )
}
