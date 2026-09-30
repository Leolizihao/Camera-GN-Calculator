package com.gncal.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Material 3 默认字阶（含大字号显示样式，用于结果数字） */
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
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
