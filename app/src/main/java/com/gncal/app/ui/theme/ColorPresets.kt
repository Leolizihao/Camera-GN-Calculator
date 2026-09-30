package com.gncal.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Amber preset (existing warm flash theme)
private val AmberLight = lightColorScheme(
    primary = FlashPrimary,
    onPrimary = FlashOnPrimary,
    primaryContainer = FlashPrimaryContainer,
    onPrimaryContainer = FlashOnPrimaryContainer,
    secondary = FlashSecondary,
    onSecondary = FlashOnSecondary,
    secondaryContainer = FlashSecondaryContainer,
    onSecondaryContainer = FlashOnSecondaryContainer,
    tertiary = FlashTertiary,
    onTertiary = FlashOnTertiary,
    tertiaryContainer = FlashTertiaryContainer,
    onTertiaryContainer = FlashOnTertiaryContainer,
    background = FlashLightBackground,
    surface = FlashLightSurface,
    surfaceVariant = FlashLightSurfaceVariant,
    onSurface = FlashLightOnSurface,
    onSurfaceVariant = FlashLightOnSurfaceVariant,
    outline = FlashLightOutline
)

private val AmberDark = darkColorScheme(
    primary = FlashDarkPrimary,
    onPrimary = FlashDarkOnPrimary,
    primaryContainer = FlashDarkPrimaryContainer,
    onPrimaryContainer = FlashDarkOnPrimaryContainer,
    secondary = FlashDarkSecondary,
    onSecondary = FlashDarkOnSecondary,
    secondaryContainer = FlashDarkSecondaryContainer,
    onSecondaryContainer = FlashDarkOnSecondaryContainer,
    tertiary = FlashDarkTertiary,
    onTertiary = FlashDarkOnTertiary,
    tertiaryContainer = FlashDarkTertiaryContainer,
    onTertiaryContainer = FlashDarkOnTertiaryContainer,
    background = FlashDarkBackground,
    surface = FlashDarkSurface,
    surfaceVariant = FlashDarkSurfaceVariant,
    onSurface = FlashDarkOnSurface,
    onSurfaceVariant = FlashDarkOnSurfaceVariant,
    outline = FlashDarkOutline
)

// Ocean preset - cool blue tones
private val OceanLight = lightColorScheme(
    primary = Color(0xFF006494),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCAE6FF),
    onPrimaryContainer = Color(0xFF001E31),
    secondary = Color(0xFF50606E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD3E4F5),
    onSecondaryContainer = Color(0xFF0C1D29),
    tertiary = Color(0xFF65587B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEBDCFF),
    onTertiaryContainer = Color(0xFF201634),
    background = Color(0xFFF6FAFE),
    surface = Color(0xFFF6FAFE),
    surfaceVariant = Color(0xFFDDE3EA),
    onSurface = Color(0xFF191C1E),
    onSurfaceVariant = Color(0xFF41484D),
    outline = Color(0xFF71787E)
)

private val OceanDark = darkColorScheme(
    primary = Color(0xFF8FCDFF),
    onPrimary = Color(0xFF003450),
    primaryContainer = Color(0xFF004C71),
    onPrimaryContainer = Color(0xFFCAE6FF),
    secondary = Color(0xFFB7C8D9),
    onSecondary = Color(0xFF22323F),
    secondaryContainer = Color(0xFF384956),
    onSecondaryContainer = Color(0xFFD3E4F5),
    tertiary = Color(0xFFCFBEE8),
    onTertiary = Color(0xFF362B4A),
    tertiaryContainer = Color(0xFF4D4162),
    onTertiaryContainer = Color(0xFFEBDCFF),
    background = Color(0xFF191C1E),
    surface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFF41484D),
    onSurface = Color(0xFFE1E3E5),
    onSurfaceVariant = Color(0xFFC1C7CE),
    outline = Color(0xFF8B9198)
)

// Mint preset - fresh green tones
private val MintLight = lightColorScheme(
    primary = Color(0xFF006E26),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF8EFF9E),
    onPrimaryContainer = Color(0xFF002106),
    secondary = Color(0xFF516351),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD4E8D0),
    onSecondaryContainer = Color(0xFF0F1F11),
    tertiary = Color(0xFF39656D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBCEBF4),
    onTertiaryContainer = Color(0xFF001F24),
    background = Color(0xFFF6FEF6),
    surface = Color(0xFFF6FEF6),
    surfaceVariant = Color(0xFFDEE5D9),
    onSurface = Color(0xFF191D19),
    onSurfaceVariant = Color(0xFF424940),
    outline = Color(0xFF72796F)
)

private val MintDark = darkColorScheme(
    primary = Color(0xFF72E284),
    onPrimary = Color(0xFF00390F),
    primaryContainer = Color(0xFF00531A),
    onPrimaryContainer = Color(0xFF8EFF9E),
    secondary = Color(0xFFB8CCB5),
    onSecondary = Color(0xFF243425),
    secondaryContainer = Color(0xFF3A4B3A),
    onSecondaryContainer = Color(0xFFD4E8D0),
    tertiary = Color(0xFFA1CED7),
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF1F4D54),
    onTertiaryContainer = Color(0xFFBCEBF4),
    background = Color(0xFF191D19),
    surface = Color(0xFF191D19),
    surfaceVariant = Color(0xFF424940),
    onSurface = Color(0xFFE1E3DF),
    onSurfaceVariant = Color(0xFFC2C9BD),
    outline = Color(0xFF8C9388)
)

// Rose preset - soft pink tones
private val RoseLight = lightColorScheme(
    primary = Color(0xFF984061),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD9E2),
    onPrimaryContainer = Color(0xFF3E001D),
    secondary = Color(0xFF75565F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD9E2),
    onSecondaryContainer = Color(0xFF2B151C),
    tertiary = Color(0xFF7D5636),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDCC1),
    onTertiaryContainer = Color(0xFF2F1500),
    background = Color(0xFFFFFBFF),
    surface = Color(0xFFFFFBFF),
    surfaceVariant = Color(0xFFF3DDE1),
    onSurface = Color(0xFF201A1B),
    onSurfaceVariant = Color(0xFF514347),
    outline = Color(0xFF837377)
)

private val RoseDark = darkColorScheme(
    primary = Color(0xFFFFB1C8),
    onPrimary = Color(0xFF5E1133),
    primaryContainer = Color(0xFF7B2949),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = Color(0xFFE3BDC6),
    onSecondary = Color(0xFF432931),
    secondaryContainer = Color(0xFF5B3F47),
    onSecondaryContainer = Color(0xFFFFD9E2),
    tertiary = Color(0xFFEFBD94),
    onTertiary = Color(0xFF48290C),
    tertiaryContainer = Color(0xFF623F21),
    onTertiaryContainer = Color(0xFFFFDCC1),
    background = Color(0xFF201A1B),
    surface = Color(0xFF201A1B),
    surfaceVariant = Color(0xFF514347),
    onSurface = Color(0xFFECE0E1),
    onSurfaceVariant = Color(0xFFD5C2C6),
    outline = Color(0xFF9E8C90)
)

fun getColorScheme(preset: com.gncal.app.data.ColorPreset, isDark: Boolean) = when (preset) {
    com.gncal.app.data.ColorPreset.AMBER -> if (isDark) AmberDark else AmberLight
    com.gncal.app.data.ColorPreset.OCEAN -> if (isDark) OceanDark else OceanLight
    com.gncal.app.data.ColorPreset.MINT -> if (isDark) MintDark else MintLight
    com.gncal.app.data.ColorPreset.ROSE -> if (isDark) RoseDark else RoseLight
}
