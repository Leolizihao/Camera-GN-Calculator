package com.gncal.app.ui.theme

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.gncal.app.data.ColorPreset
import com.gncal.app.data.FontSizeMode
import com.gncal.app.data.LanguageMode
import com.gncal.app.data.ThemeMode

@Composable
fun GnCalTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    colorPreset: ColorPreset = ColorPreset.AMBER,
    fontSize: FontSizeMode = FontSizeMode.SYSTEM,
    language: LanguageMode = LanguageMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> getColorScheme(colorPreset, darkTheme)
    }

    val configuration = LocalConfiguration.current
    val fontScale = when (fontSize) {
        FontSizeMode.SYSTEM -> configuration.fontScale
        FontSizeMode.COMPACT -> 0.9f
        FontSizeMode.LARGE -> 1.15f
    }

    val typography = Typography.scaled(fontScale)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
            window.decorView.setBackgroundColor(colorScheme.surface.toArgb())
        }
    }

    val context = LocalContext.current
    val localizedContext = remember(language, context) {
        val locale = when (language) {
            LanguageMode.SYSTEM -> null
            LanguageMode.CHINESE -> java.util.Locale.SIMPLIFIED_CHINESE
            LanguageMode.TRADITIONAL_CHINESE -> java.util.Locale.TRADITIONAL_CHINESE
            LanguageMode.ENGLISH -> java.util.Locale.ENGLISH
        }
        if (locale == null) {
            context
        } else {
            Configuration(context.resources.configuration).let { localizedConfiguration ->
                localizedConfiguration.setLocale(locale)
                context.createConfigurationContext(localizedConfiguration)
            }
        }
    }

    CompositionLocalProvider(
        androidx.compose.ui.platform.LocalContext provides localizedContext,
        LocalConfiguration provides localizedContext.resources.configuration
    ) {
        MaterialTheme(colorScheme = colorScheme, typography = typography, content = content)
    }
}
