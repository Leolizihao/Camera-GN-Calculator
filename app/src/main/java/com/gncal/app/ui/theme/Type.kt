package com.gncal.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gncal.app.R

/**
 * 应用字族：IBM Plex Sans（拉丁字母与数字）。
 *
 * v1.4.1 起中文回退为系统默认字体：IBM Plex Sans 本身不含中日韩字形，
 * 缺失字符由 Android 字体回退链补齐（v1.4.0 曾合并思源宋体，现已回退）。
 *
 * 字体由 .toolchain/build_plex_font.py 从可变字体实例化并子集化：
 * - 四个字重各自独立文件，TextStyle 的字重可被精确匹配；
 * - 仅保留应用会渲染的 245 个拉丁/符号字形，每字重约 51 KB。
 */
val GnFontFamily = FontFamily(
    Font(R.font.ibm_plex_sans_light, FontWeight.Light),    // 300 细体：小标题
    Font(R.font.ibm_plex_sans_regular, FontWeight.Normal), // 400 常规：正文
    Font(R.font.ibm_plex_sans_medium, FontWeight.Medium),  // 500 中等：按钮与标签
    Font(R.font.ibm_plex_sans_bold, FontWeight.Bold)       // 700 粗体：大标题与结果数字
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
