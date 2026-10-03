package com.gncal.app.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.gncal.app.model.DistanceUnit
import com.gncal.app.model.FlashProfile
import com.gncal.app.model.SolveMode
import com.gncal.app.ui.calc.CalculatorScreen
import com.gncal.app.ui.calc.rememberCalculatorState
import com.gncal.app.ui.haptic.HapticEvent
import com.gncal.app.ui.haptic.Haptics
import com.gncal.app.ui.haptic.LocalHaptics
import com.gncal.app.ui.settings.SettingsScreen

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

enum class Destination { CALCULATOR, SETTINGS }

@Composable
fun GnCalApp(
    preferences: UserPreferencesRepository,
    haptics: Haptics = Haptics(null),
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalHaptics provides haptics) {
        GnCalAppContent(preferences = preferences, modifier = modifier)
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun GnCalAppContent(preferences: UserPreferencesRepository, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val haptics = LocalHaptics.current
    val calculatorState = rememberCalculatorState()

    val unit by preferences.distanceUnit.collectAsStateWithLifecycle(DistanceUnit.METER)
    val themeMode by preferences.themeMode.collectAsStateWithLifecycle(ThemeMode.SYSTEM)
    val dynamicColor by preferences.dynamicColor.collectAsStateWithLifecycle(true)
    val language by preferences.language.collectAsStateWithLifecycle(LanguageMode.SYSTEM)
    val fontSize by preferences.fontSize.collectAsStateWithLifecycle(FontSizeMode.SYSTEM)
    val colorPreset by preferences.colorPreset.collectAsStateWithLifecycle(ColorPreset.AMBER)
    val hapticEnabled by preferences.hapticEnabled.collectAsStateWithLifecycle(true)
    val hapticIntensity by preferences.hapticIntensity.collectAsStateWithLifecycle(Haptics.DEFAULT_INTENSITY)
    val flashProfiles by preferences.flashProfiles.collectAsStateWithLifecycle(FlashProfile.defaults)

    // 主页四个求解模式各占一页，可左右滑动切换；与顶部模式按钮双向同步
    val pagerState = rememberPagerState(initialPage = calculatorState.mode.ordinal) {
        SolveMode.entries.size
    }

    LaunchedEffect(hapticEnabled, hapticIntensity) {
        haptics.configure(hapticEnabled, hapticIntensity)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                val pageMode = SolveMode.entries[page]
                if (calculatorState.mode != pageMode) {
                    calculatorState.mode = pageMode
                    haptics.perform(HapticEvent.SELECT)
                }
            }
    }

    LaunchedEffect(calculatorState.mode) {
        val target = calculatorState.mode.ordinal
        if (pagerState.currentPage != target) {
            pagerState.animateScrollToPage(target)
        }
    }

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
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
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
                                    onClick = {
                                        menuExpanded = false
                                        haptics.perform(HapticEvent.CONFIRM)
                                        calculatorState.reset()
                                    }
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                    ModeNavigation(
                        selectedMode = calculatorState.mode,
                        onModeChange = { mode ->
                            if (mode != calculatorState.mode) haptics.perform(HapticEvent.SELECT)
                            calculatorState.mode = mode
                        }
                    )
                    Text(
                        text = stringResource(R.string.hint_swipe_pages),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                }
            }
        }
    ) { padding ->
        when (destination) {
            Destination.CALCULATOR -> HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                CalculatorScreen(
                    unit = unit,
                    onUnitChange = { newUnit ->
                        if (newUnit != unit) haptics.perform(HapticEvent.SELECT)
                        scope.launch { preferences.setDistanceUnit(newUnit) }
                    },
                    flashProfiles = flashProfiles,
                    expandedLayout = expandedLayout,
                    contentPadding = padding,
                    state = calculatorState,
                    mode = SolveMode.entries[page]
                )
            }
            Destination.SETTINGS -> SettingsScreen(
                themeMode = themeMode,
                dynamicColor = dynamicColor,
                language = language,
                fontSize = fontSize,
                colorPreset = colorPreset,
                hapticEnabled = hapticEnabled,
                hapticIntensity = hapticIntensity,
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
                onColorPresetChange = { scope.launch { preferences.setColorPreset(it) } },
                onHapticEnabledChange = { scope.launch { preferences.setHapticEnabled(it) } },
                onHapticIntensityChange = { scope.launch { preferences.setHapticIntensity(it) } },
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
    androidx.compose.material3.Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
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
