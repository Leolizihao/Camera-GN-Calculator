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

// Forest preset - deep pine green tones
private val ForestLight = lightColorScheme(
    primary = Color(0xFF1E6B4C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA5F0C7),
    onPrimaryContainer = Color(0xFF002114),
    secondary = Color(0xFF4E6355),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1E7D8),
    onSecondaryContainer = Color(0xFF0C2015),
    tertiary = Color(0xFF3B6173),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBFE7F9),
    onTertiaryContainer = Color(0xFF001E29),
    background = Color(0xFFF4FBF7),
    surface = Color(0xFFF4FBF7),
    surfaceVariant = Color(0xFFDBE5DE),
    onSurface = Color(0xFF181D1B),
    onSurfaceVariant = Color(0xFF414941),
    outline = Color(0xFF707972)
)

private val ForestDark = darkColorScheme(
    primary = Color(0xFF7AD9A5),
    onPrimary = Color(0xFF003823),
    primaryContainer = Color(0xFF00513A),
    onPrimaryContainer = Color(0xFFA5F0C7),
    secondary = Color(0xFFB5CCBE),
    onSecondary = Color(0xFF20332A),
    secondaryContainer = Color(0xFF37493F),
    onSecondaryContainer = Color(0xFFD1E7D8),
    tertiary = Color(0xFFA5CBD9),
    onTertiary = Color(0xFF053544),
    tertiaryContainer = Color(0xFF224C5C),
    onTertiaryContainer = Color(0xFFBFE7F9),
    background = Color(0xFF181D1B),
    surface = Color(0xFF181D1B),
    surfaceVariant = Color(0xFF414941),
    onSurface = Color(0xFFE0E3E0),
    onSurfaceVariant = Color(0xFFC0C8C1),
    outline = Color(0xFF8A938C)
)

// Violet preset - purple tones
private val VioletLight = lightColorScheme(
    primary = Color(0xFF6C4DC4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE6DFFF),
    onPrimaryContainer = Color(0xFF21005A),
    secondary = Color(0xFF605D70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE7E0F5),
    onSecondaryContainer = Color(0xFF1B1929),
    tertiary = Color(0xFF7C5266),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9E6),
    onTertiaryContainer = Color(0xFF2F1223),
    background = Color(0xFFFAF8FF),
    surface = Color(0xFFFAF8FF),
    surfaceVariant = Color(0xFFE6E0EC),
    onSurface = Color(0xFF1D1B20),
    onSurfaceVariant = Color(0xFF494552),
    outline = Color(0xFF7A7583)
)

private val VioletDark = darkColorScheme(
    primary = Color(0xFFCFBCFF),
    onPrimary = Color(0xFF37008F),
    primaryContainer = Color(0xFF4F3A95),
    onPrimaryContainer = Color(0xFFE6DFFF),
    secondary = Color(0xFFC9C3DA),
    onSecondary = Color(0xFF312E3F),
    secondaryContainer = Color(0xFF484556),
    onSecondaryContainer = Color(0xFFE7E0F5),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF4B2537),
    tertiaryContainer = Color(0xFF653C4F),
    onTertiaryContainer = Color(0xFFFFD9E6),
    background = Color(0xFF1D1B20),
    surface = Color(0xFF1D1B20),
    surfaceVariant = Color(0xFF494552),
    onSurface = Color(0xFFE6E1E9),
    onSurfaceVariant = Color(0xFFCAC4D2),
    outline = Color(0xFF948F9E)
)

// Coral preset - warm coral tones
private val CoralLight = lightColorScheme(
    primary = Color(0xFFB5442C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBD0),
    onPrimaryContainer = Color(0xFF3C0500),
    secondary = Color(0xFF76574E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDBCF),
    onSecondaryContainer = Color(0xFF2C150E),
    tertiary = Color(0xFF6D5C2F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF7E2A8),
    onTertiaryContainer = Color(0xFF231B00),
    background = Color(0xFFFFF9F7),
    surface = Color(0xFFFFF9F7),
    surfaceVariant = Color(0xFFF3DCD3),
    onSurface = Color(0xFF201A18),
    onSurfaceVariant = Color(0xFF51443F),
    outline = Color(0xFF837370)
)

private val CoralDark = darkColorScheme(
    primary = Color(0xFFFFB4A0),
    onPrimary = Color(0xFF690F00),
    primaryContainer = Color(0xFF8F2B17),
    onPrimaryContainer = Color(0xFFFFDBD0),
    secondary = Color(0xFFE5BEB1),
    onSecondary = Color(0xFF442A21),
    secondaryContainer = Color(0xFF5D4038),
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = Color(0xFFDEC791),
    onTertiary = Color(0xFF3C2F00),
    tertiaryContainer = Color(0xFF54451A),
    onTertiaryContainer = Color(0xFFF7E2A8),
    background = Color(0xFF201A18),
    surface = Color(0xFF201A18),
    surfaceVariant = Color(0xFF51443F),
    onSurface = Color(0xFFECDEDA),
    onSurfaceVariant = Color(0xFFD5C3BD),
    outline = Color(0xFF9E8D88)
)

// Slate preset - neutral blue-grey tones
private val SlateLight = lightColorScheme(
    primary = Color(0xFF4C5F70),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCFE3F6),
    onPrimaryContainer = Color(0xFF041D2C),
    secondary = Color(0xFF56616B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDAE4EE),
    onSecondaryContainer = Color(0xFF131E27),
    tertiary = Color(0xFF6A566F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF0DDF3),
    onTertiaryContainer = Color(0xFF25132A),
    background = Color(0xFFF7F9FB),
    surface = Color(0xFFF7F9FB),
    surfaceVariant = Color(0xFFDBE2E9),
    onSurface = Color(0xFF1A1C1E),
    onSurfaceVariant = Color(0xFF41484D),
    outline = Color(0xFF71787E)
)

private val SlateDark = darkColorScheme(
    primary = Color(0xFFB3C7DB),
    onPrimary = Color(0xFF1B3243),
    primaryContainer = Color(0xFF33485A),
    onPrimaryContainer = Color(0xFFCFE3F6),
    secondary = Color(0xFFBEC8D2),
    onSecondary = Color(0xFF28333C),
    secondaryContainer = Color(0xFF3F4954),
    onSecondaryContainer = Color(0xFFDAE4EE),
    tertiary = Color(0xFFD3C1D6),
    onTertiary = Color(0xFF3A2840),
    tertiaryContainer = Color(0xFF523F58),
    onTertiaryContainer = Color(0xFFF0DDF3),
    background = Color(0xFF1A1C1E),
    surface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFF41484D),
    onSurface = Color(0xFFE1E2E5),
    onSurfaceVariant = Color(0xFFC0C7CE),
    outline = Color(0xFF8A9198)
)

fun getColorScheme(preset: com.gncal.app.data.ColorPreset, isDark: Boolean) = when (preset) {
    com.gncal.app.data.ColorPreset.AMBER -> if (isDark) AmberDark else AmberLight
    com.gncal.app.data.ColorPreset.OCEAN -> if (isDark) OceanDark else OceanLight
    com.gncal.app.data.ColorPreset.MINT -> if (isDark) MintDark else MintLight
    com.gncal.app.data.ColorPreset.ROSE -> if (isDark) RoseDark else RoseLight
    com.gncal.app.data.ColorPreset.FOREST -> if (isDark) ForestDark else ForestLight
    com.gncal.app.data.ColorPreset.VIOLET -> if (isDark) VioletDark else VioletLight
    com.gncal.app.data.ColorPreset.CORAL -> if (isDark) CoralDark else CoralLight
    com.gncal.app.data.ColorPreset.SLATE -> if (isDark) SlateDark else SlateLight
}
