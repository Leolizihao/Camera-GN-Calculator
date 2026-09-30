package com.gncal.app.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gncal.app.BuildConfig
import com.gncal.app.R
import com.gncal.app.data.ColorPreset
import com.gncal.app.data.FontSizeMode
import com.gncal.app.data.LanguageMode
import com.gncal.app.data.ThemeMode
import com.gncal.app.data.UserPreferencesRepository
import com.gncal.app.data.VisualStyle
import com.gncal.app.model.DistanceUnit
import com.gncal.app.model.FlashProfile
import com.gncal.app.model.SolveMode
import com.gncal.app.ui.calc.CalculatorScreen
import com.gncal.app.ui.calc.rememberCalculatorState
import com.gncal.app.ui.settings.SettingsScreen
import com.gncal.app.ui.theme.GlassSurface
import com.gncal.app.ui.theme.glassBackdrop
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

enum class Destination { CALCULATOR, SETTINGS }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun GnCalApp(preferences: UserPreferencesRepository, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val calculatorState = rememberCalculatorState()

    val unit by preferences.distanceUnit.collectAsStateWithLifecycle(DistanceUnit.METER)
    val themeMode by preferences.themeMode.collectAsStateWithLifecycle(ThemeMode.SYSTEM)
    val dynamicColor by preferences.dynamicColor.collectAsStateWithLifecycle(true)
    val language by preferences.language.collectAsStateWithLifecycle(LanguageMode.SYSTEM)
    val fontSize by preferences.fontSize.collectAsStateWithLifecycle(FontSizeMode.SYSTEM)
    val visualStyle by preferences.visualStyle.collectAsStateWithLifecycle(VisualStyle.STANDARD)
    val colorPreset by preferences.colorPreset.collectAsStateWithLifecycle(ColorPreset.AMBER)
    val flashProfiles by preferences.flashProfiles.collectAsStateWithLifecycle(FlashProfile.defaults)

    var destination by rememberSaveable { mutableStateOf(Destination.CALCULATOR) }
    var showFormulaSheet by rememberSaveable { mutableStateOf(false) }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    val activity = LocalView.current.context as? Activity
    val widthSizeClass = activity?.let { calculateWindowSizeClass(it).widthSizeClass }
    val expandedLayout = widthSizeClass == WindowWidthSizeClass.Expanded

    BackHandler(enabled = destination == Destination.SETTINGS) {
        destination = Destination.CALCULATOR
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            // 玻璃风格下由 Scaffold 自己绘制装饰背景，容器色透明以便透出背景
            .then(
                if (visualStyle == VisualStyle.STANDARD) Modifier else Modifier.glassBackdrop()
            ),
        containerColor = if (visualStyle == VisualStyle.STANDARD) {
            MaterialTheme.colorScheme.background
        } else {
            Color.Transparent
        },
        topBar = {
            if (destination == Destination.CALCULATOR) {
                Column {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = stringResource(R.string.app_subtitle),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { destination = Destination.SETTINGS }) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = stringResource(R.string.settings_title)
                                )
                            }
                            IconButton(onClick = { showFormulaSheet = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.Calculate,
                                    contentDescription = stringResource(R.string.action_info)
                                )
                            }
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = stringResource(R.string.action_more)
                                )
                            }
                            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_about)) },
                                    onClick = { menuExpanded = false; showAbout = true }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.action_reset)) },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.RestartAlt, contentDescription = null)
                                    },
                                    onClick = { menuExpanded = false; calculatorState.reset() }
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = if (visualStyle == VisualStyle.STANDARD) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                Color.Transparent
                            },
                            scrolledContainerColor = if (visualStyle == VisualStyle.STANDARD) {
                                MaterialTheme.colorScheme.surfaceContainer
                            } else {
                                Color.Transparent
                            }
                        )
                    )
                    ModeNavigation(
                        selectedMode = calculatorState.mode,
                        onModeChange = { calculatorState.mode = it }
                    )
                }
            }
        }
    ) { padding ->
        when (destination) {
            Destination.CALCULATOR -> CalculatorScreen(
                unit = unit,
                onUnitChange = { newUnit -> scope.launch { preferences.setDistanceUnit(newUnit) } },
                flashProfiles = flashProfiles,
                expandedLayout = expandedLayout,
                contentPadding = padding,
                state = calculatorState
            )
            Destination.SETTINGS -> SettingsScreen(
                themeMode = themeMode,
                dynamicColor = dynamicColor,
                language = language,
                fontSize = fontSize,
                visualStyle = visualStyle,
                colorPreset = colorPreset,
                flashProfiles = flashProfiles,
                onThemeModeChange = { scope.launch { preferences.setThemeMode(it) } },
                onDynamicColorChange = { scope.launch { preferences.setDynamicColor(it) } },
                onLanguageChange = { newLanguage ->
                    scope.launch {
                        preferences.setLanguage(newLanguage)
                        activity?.recreate()
                    }
                },
                onFontSizeChange = { scope.launch { preferences.setFontSize(it) } },
                onVisualStyleChange = { scope.launch { preferences.setVisualStyle(it) } },
                onColorPresetChange = { scope.launch { preferences.setColorPreset(it) } },
                onAddFlashProfile = { name, guideNumber ->
                    scope.launch { preferences.addFlashProfile(name, guideNumber) }
                },
                onUpdateFlashProfile = { profile ->
                    scope.launch { preferences.updateFlashProfile(profile) }
                },
                onDeleteFlashProfile = { profileId ->
                    scope.launch { preferences.deleteFlashProfile(profileId) }
                },
                onBackClick = { destination = Destination.CALCULATOR }
            )
        }
    }

    if (showFormulaSheet) {
        FormulaSheet(onDismiss = { showFormulaSheet = false })
    }
    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModeNavigation(
    selectedMode: SolveMode,
    onModeChange: (SolveMode) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        SolveMode.entries.forEachIndexed { index, mode ->
            SegmentedButton(
                modifier = Modifier.weight(1f),
                selected = selectedMode == mode,
                onClick = { onModeChange(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = SolveMode.entries.size),
                contentPadding = PaddingValues(horizontal = 4.dp),
                label = { Text(stringResource(modeLabelRes(mode))) }
            )
        }
    }
}

private fun modeLabelRes(mode: SolveMode) = when (mode) {
    SolveMode.APERTURE -> R.string.mode_aperture
    SolveMode.DISTANCE -> R.string.mode_distance
    SolveMode.GUIDE_NUMBER -> R.string.mode_gn
    SolveMode.ISO -> R.string.mode_iso
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormulaSheet(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    ModalBottomSheet(onDismissRequest = onDismiss, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.action_info),
                style = MaterialTheme.typography.titleLarge
            )
            FormulaLine(stringResource(R.string.formula_basic))
            FormulaLine(stringResource(R.string.formula_iso_gn))
            FormulaLine(stringResource(R.string.formula_aperture))
            FormulaLine(stringResource(R.string.formula_distance))
            FormulaLine(stringResource(R.string.formula_iso))
            Text(
                text = stringResource(R.string.info_mode_aperture),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.info_mode_distance),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.info_mode_gn),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.info_mode_iso),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun FormulaLine(text: String, modifier: Modifier = Modifier) {
    val visualStyle = com.gncal.app.ui.theme.LocalVisualStyle.current
    val body: @Composable () -> Unit = {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
    if (visualStyle == VisualStyle.STANDARD) {
        androidx.compose.material3.Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.medium
        ) {
            body()
        }
    } else {
        GlassSurface(
            style = visualStyle,
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            body()
        }
    }
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
        title = { Text(stringResource(R.string.about_title)) },
        text = {
            Text(
                text = stringResource(R.string.about_body, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) }
        }
    )
}
