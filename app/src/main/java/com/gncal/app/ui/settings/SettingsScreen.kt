package com.gncal.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gncal.app.R
import com.gncal.app.data.ColorPreset
import com.gncal.app.data.FontSizeMode
import com.gncal.app.data.LanguageMode
import com.gncal.app.data.ThemeMode
import com.gncal.app.data.VisualStyle
import com.gncal.app.model.FlashCalculator
import com.gncal.app.model.FlashProfile
import com.gncal.app.ui.calc.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    dynamicColor: Boolean,
    language: LanguageMode,
    fontSize: FontSizeMode,
    visualStyle: VisualStyle,
    colorPreset: ColorPreset,
    flashProfiles: List<FlashProfile>,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onLanguageChange: (LanguageMode) -> Unit,
    onFontSizeChange: (FontSizeMode) -> Unit,
    onVisualStyleChange: (VisualStyle) -> Unit,
    onColorPresetChange: (ColorPreset) -> Unit,
    onAddFlashProfile: (String, Double) -> Unit,
    onUpdateFlashProfile: (FlashProfile) -> Unit,
    onDeleteFlashProfile: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var addingFlash by remember { mutableStateOf(false) }
    var editingFlash by remember { mutableStateOf<FlashProfile?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language
            SectionCard(title = stringResource(R.string.settings_language)) {
                LanguageMode.entries.forEach { mode ->
                    SettingRow(
                        label = when (mode) {
                            LanguageMode.SYSTEM -> stringResource(R.string.language_system)
                            LanguageMode.CHINESE -> stringResource(R.string.language_chinese)
                            LanguageMode.ENGLISH -> stringResource(R.string.language_english)
                        },
                        selected = language == mode,
                        onClick = { onLanguageChange(mode) }
                    )
                }
            }

            // Font Size
            SectionCard(title = stringResource(R.string.settings_font_size)) {
                FontSizeMode.entries.forEach { mode ->
                    SettingRow(
                        label = when (mode) {
                            FontSizeMode.SYSTEM -> stringResource(R.string.font_size_system)
                            FontSizeMode.COMPACT -> stringResource(R.string.font_size_compact)
                            FontSizeMode.LARGE -> stringResource(R.string.font_size_large)
                        },
                        selected = fontSize == mode,
                        onClick = { onFontSizeChange(mode) }
                    )
                }
            }

            // Theme
            SectionCard(title = stringResource(R.string.settings_theme)) {
                ThemeMode.entries.forEach { mode ->
                    SettingRow(
                        label = when (mode) {
                            ThemeMode.SYSTEM -> stringResource(R.string.pref_theme_system)
                            ThemeMode.LIGHT -> stringResource(R.string.pref_theme_light)
                            ThemeMode.DARK -> stringResource(R.string.pref_theme_dark)
                        },
                        selected = themeMode == mode,
                        onClick = { onThemeModeChange(mode) }
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.pref_dynamic_color),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.pref_dynamic_color_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = dynamicColor, onCheckedChange = onDynamicColorChange)
                }
            }

            // Visual Style
            SectionCard(title = stringResource(R.string.settings_visual_style)) {
                VisualStyle.entries.forEach { style ->
                    SettingRow(
                        label = when (style) {
                            VisualStyle.STANDARD -> stringResource(R.string.visual_standard)
                            VisualStyle.FROSTED -> stringResource(R.string.visual_frosted)
                            VisualStyle.LIQUID -> stringResource(R.string.visual_liquid)
                        },
                        selected = visualStyle == style,
                        onClick = { onVisualStyleChange(style) }
                    )
                }
            }

            // Color Preset
            if (!dynamicColor) {
                SectionCard(title = stringResource(R.string.settings_color_preset)) {
                    ColorPreset.entries.forEach { preset ->
                        SettingRow(
                            label = when (preset) {
                                ColorPreset.AMBER -> stringResource(R.string.preset_amber)
                                ColorPreset.OCEAN -> stringResource(R.string.preset_ocean)
                                ColorPreset.MINT -> stringResource(R.string.preset_mint)
                                ColorPreset.ROSE -> stringResource(R.string.preset_rose)
                            },
                            selected = colorPreset == preset,
                            onClick = { onColorPresetChange(preset) }
                        )
                    }
                }
            }

            SectionCard(title = stringResource(R.string.settings_flash_profiles)) {
                Text(
                    text = stringResource(R.string.settings_flash_profiles_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (flashProfiles.isEmpty()) {
                    Text(
                        text = stringResource(R.string.flash_profiles_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    flashProfiles.forEach { profile ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(profile.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "GN ${FlashCalculator.formatGuideNumber(profile.guideNumber)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { editingFlash = profile }) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = stringResource(R.string.action_edit_flash)
                                )
                            }
                            IconButton(onClick = { onDeleteFlashProfile(profile.id) }) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = stringResource(R.string.action_delete_flash)
                                )
                            }
                        }
                    }
                }
                Button(
                    onClick = { addingFlash = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                    Text(
                        text = stringResource(R.string.action_add_flash),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }

    if (addingFlash) {
        FlashProfileEditorDialog(
            profile = null,
            onDismiss = { addingFlash = false },
            onSave = { name, guideNumber ->
                onAddFlashProfile(name, guideNumber)
                addingFlash = false
            }
        )
    }
    editingFlash?.let { profile ->
        FlashProfileEditorDialog(
            profile = profile,
            onDismiss = { editingFlash = null },
            onSave = { name, guideNumber ->
                onUpdateFlashProfile(profile.copy(name = name, guideNumber = guideNumber))
                editingFlash = null
            }
        )
    }
}

@Composable
private fun SettingRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun FlashProfileEditorDialog(
    profile: FlashProfile?,
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    var name by remember(profile?.id) { mutableStateOf(profile?.name ?: "") }
    var guideNumberText by remember(profile?.id) {
        mutableStateOf(profile?.let { FlashCalculator.formatGuideNumber(it.guideNumber) } ?: "")
    }
    val guideNumber = guideNumberText.toDoubleOrNull()
    val valid = name.trim().isNotEmpty() && guideNumber != null && guideNumber > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (profile == null) R.string.flash_profile_add_title
                    else R.string.flash_profile_edit_title
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.label_flash_name)) },
                    placeholder = { Text(stringResource(R.string.hint_flash_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = guideNumberText,
                    onValueChange = { value ->
                        guideNumberText = value.filter { it.isDigit() || it == '.' }
                            .let { text ->
                                val firstDot = text.indexOf('.')
                                if (firstDot >= 0) {
                                    text.substring(0, firstDot + 1) +
                                        text.substring(firstDot + 1).replace(".", "")
                                } else text
                            }
                    },
                    label = { Text(stringResource(R.string.label_flash_gn)) },
                    placeholder = { Text(stringResource(R.string.hint_flash_gn)) },
                    singleLine = true,
                    isError = guideNumberText.isNotEmpty() && guideNumber == null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                if (!valid && (name.isNotEmpty() || guideNumberText.isNotEmpty())) {
                    Text(
                        text = stringResource(R.string.msg_flash_profile_invalid),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) }
        },
        confirmButton = {
            TextButton(
                enabled = valid,
                onClick = { onSave(name.trim(), guideNumber ?: 0.0) }
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    )
}
