package com.gncal.app.ui.calc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gncal.app.R
import com.gncal.app.model.Advice
import com.gncal.app.model.CalcResult
import com.gncal.app.model.DistanceUnit
import com.gncal.app.model.FlashCalculator
import com.gncal.app.model.FlashInputs
import com.gncal.app.model.FlashProfile
import com.gncal.app.model.PhotoScales
import com.gncal.app.model.ReferenceRow
import com.gncal.app.model.SolveMode
import com.gncal.app.ui.theme.visualStyle
import java.util.Locale

private val CalculatorStateSaver = mapSaver(
    save = {
        mapOf(
            "mode" to it.mode.ordinal,
            "gn" to it.gnText,
            "aperture" to it.apertureIndex,
            "distance" to it.distanceText,
            "iso" to it.isoIndex,
            "power" to it.powerIndex,
            "comp" to it.compensationIndex,
            "advanced" to if (it.advancedExpanded) 1 else 0
        )
    },
    restore = { map ->
        CalculatorState().apply {
            mode = SolveMode.entries[(map["mode"] as? Int) ?: 0]
            gnText = (map["gn"] as? String) ?: "36"
            apertureIndex = ((map["aperture"] as? Int) ?: PhotoScales.DEFAULT_APERTURE_INDEX)
                .coerceIn(PhotoScales.APERTURES.indices)
            distanceText = (map["distance"] as? String) ?: "3"
            isoIndex = ((map["iso"] as? Int) ?: PhotoScales.DEFAULT_ISO_INDEX)
                .coerceIn(PhotoScales.ISOS.indices)
            powerIndex = ((map["power"] as? Int) ?: 0)
                .coerceIn(PhotoScales.POWER_DENOMINATORS.indices)
            compensationIndex = ((map["comp"] as? Int) ?: PhotoScales.DEFAULT_COMP_INDEX)
                .coerceIn(PhotoScales.COMPENSATION_EV.indices)
            advancedExpanded = (map["advanced"] as? Int) == 1
        }
    }
)

class CalculatorState {
    var mode by mutableStateOf(SolveMode.APERTURE)
    var gnText by mutableStateOf("36")
    var apertureIndex by mutableStateOf(PhotoScales.DEFAULT_APERTURE_INDEX)
    var distanceText by mutableStateOf("3")
    var isoIndex by mutableStateOf(PhotoScales.DEFAULT_ISO_INDEX)
    var powerIndex by mutableStateOf(0)
    var compensationIndex by mutableStateOf(PhotoScales.DEFAULT_COMP_INDEX)
    var advancedExpanded by mutableStateOf(false)

    fun reset() {
        mode = SolveMode.APERTURE
        gnText = "36"
        apertureIndex = PhotoScales.DEFAULT_APERTURE_INDEX
        distanceText = "3"
        isoIndex = PhotoScales.DEFAULT_ISO_INDEX
        powerIndex = 0
        compensationIndex = PhotoScales.DEFAULT_COMP_INDEX
    }
}

@Composable
fun rememberCalculatorState(): CalculatorState =
    rememberSaveable(saver = CalculatorStateSaver) { CalculatorState() }

@Composable
fun CalculatorScreen(
    unit: DistanceUnit,
    onUnitChange: (DistanceUnit) -> Unit,
    flashProfiles: List<FlashProfile>,
    expandedLayout: Boolean,
    contentPadding: PaddingValues,
    state: CalculatorState = rememberCalculatorState(),
    modifier: Modifier = Modifier
) {
    val gn = state.gnText.toDoubleOrNull()?.takeIf { it > 0.0 }
    val distance = state.distanceText.toDoubleOrNull()?.takeIf { it > 0.0 }

    val inputs = FlashInputs(
        mode = state.mode,
        gnIso100 = gn ?: 0.0,
        apertureIndex = state.apertureIndex,
        distance = distance ?: 0.0,
        isoIndex = state.isoIndex,
        powerIndex = state.powerIndex,
        compensationIndex = state.compensationIndex,
        unit = unit
    )
    val result = remember(inputs) { FlashCalculator.solve(inputs) }

    if (expandedLayout) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                InputsPane(
                    state = state,
                    unit = unit,
                    flashProfiles = flashProfiles,
                    onUnitChange = onUnitChange
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ResultsPane(inputs = inputs, result = result, unit = unit)
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InputsPane(
                state = state,
                unit = unit,
                flashProfiles = flashProfiles,
                onUnitChange = onUnitChange
            )
            ResultsPane(inputs = inputs, result = result, unit = unit)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModeSelector(mode: SolveMode, onModeChange: (SolveMode) -> Unit, modifier: Modifier = Modifier) {
    val modes = SolveMode.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        modes.forEachIndexed { index, item ->
            SegmentedButton(
                selected = mode == item,
                onClick = { onModeChange(item) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                label = { Text(stringResource(modeLabelRes(item))) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputsPane(
    state: CalculatorState,
    unit: DistanceUnit,
    flashProfiles: List<FlashProfile>,
    onUnitChange: (DistanceUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    val solvedLabel = stringResource(R.string.label_solved)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionCard(title = stringResource(R.string.section_parameters)) {
            Text(
                text = stringResource(R.string.label_unit),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = unit == DistanceUnit.METER,
                    onClick = { onUnitChange(DistanceUnit.METER) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    label = { Text(stringResource(R.string.unit_meter)) }
                )
                SegmentedButton(
                    selected = unit == DistanceUnit.FEET,
                    onClick = { onUnitChange(DistanceUnit.FEET) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    label = { Text(stringResource(R.string.unit_feet)) }
                )
            }

            // GN
            if (state.mode == SolveMode.GUIDE_NUMBER) {
                SolvedRow(label = stringResource(R.string.label_gn))
            } else {
                FlashProfilePicker(
                    profiles = flashProfiles,
                    selectedGuideNumber = state.gnText.toDoubleOrNull(),
                    onProfileSelected = { state.gnText = FlashCalculator.formatGuideNumber(it.guideNumber) }
                )
                NumberField(
                    label = stringResource(R.string.label_gn),
                    value = state.gnText,
                    onValueChange = { state.gnText = it },
                    placeholder = stringResource(R.string.hint_gn),
                    suffix = unit.symbol,
                    isError = state.gnText.toDoubleOrNull()?.let { it <= 0.0 } ?: true,
                    supportingText = if (state.gnText.toDoubleOrNull() == null) stringResource(R.string.msg_enter_valid) else null
                )
            }

            // 光圈
            SteppedSliderRow(
                label = stringResource(R.string.label_aperture),
                valueLabel = "f/${PhotoScales.APERTURES[state.apertureIndex]}",
                value = state.apertureIndex.toFloat(),
                valueRange = 0f..(PhotoScales.APERTURES.size - 1).toFloat(),
                steps = PhotoScales.APERTURES.size - 2,
                onValueChange = { state.apertureIndex = it.toInt() },
                enabled = state.mode != SolveMode.APERTURE,
                caption = if (state.mode == SolveMode.APERTURE) solvedLabel else null
            )

            // 距离
            if (state.mode == SolveMode.DISTANCE) {
                SolvedRow(label = stringResource(R.string.label_distance))
            } else {
                NumberField(
                    label = stringResource(R.string.label_distance),
                    value = state.distanceText,
                    onValueChange = { state.distanceText = it },
                    placeholder = stringResource(R.string.hint_distance),
                    suffix = unit.symbol,
                    isError = state.distanceText.toDoubleOrNull()?.let { it <= 0.0 } ?: true,
                    supportingText = if (state.distanceText.toDoubleOrNull() == null) stringResource(R.string.msg_enter_valid) else null
                )
                val maxDistance = if (unit == DistanceUnit.METER) 30f else 100f
                val parsed = state.distanceText.toFloatOrNull() ?: 0f
                SteppedSliderRow(
                    label = stringResource(R.string.hint_distance),
                    valueLabel = "${FlashCalculator.formatDistance(parsed.toDouble())} ${unit.symbol}",
                    value = parsed.coerceIn(0f, maxDistance),
                    valueRange = 0f..maxDistance,
                    steps = 0,
                    onValueChange = { state.distanceText = String.format(Locale.US, "%.1f", it) },
                    enabled = true
                )
            }

            // ISO
            SteppedSliderRow(
                label = stringResource(R.string.label_iso),
                valueLabel = PhotoScales.ISOS[state.isoIndex].toString(),
                value = state.isoIndex.toFloat(),
                valueRange = 0f..(PhotoScales.ISOS.size - 1).toFloat(),
                steps = PhotoScales.ISOS.size - 2,
                onValueChange = { state.isoIndex = it.toInt() },
                enabled = state.mode != SolveMode.ISO,
                caption = if (state.mode == SolveMode.ISO) solvedLabel else null
            )
        }

        SectionCard {
            TextButton(
                onClick = { state.advancedExpanded = !state.advancedExpanded },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.section_advanced),
                        style = MaterialTheme.typography.titleMedium
                    )
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = if (state.advancedExpanded) 180f else 0f
                        }
                    )
                }
            }
            AnimatedVisibility(
                visible = state.advancedExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SteppedSliderRow(
                        label = stringResource(R.string.label_power),
                        valueLabel = "1/${PhotoScales.POWER_DENOMINATORS[state.powerIndex]}",
                        value = state.powerIndex.toFloat(),
                        valueRange = 0f..(PhotoScales.POWER_DENOMINATORS.size - 1).toFloat(),
                        steps = PhotoScales.POWER_DENOMINATORS.size - 2,
                        onValueChange = { state.powerIndex = it.toInt() }
                    )
                    SteppedSliderRow(
                        label = stringResource(R.string.label_flash_comp),
                        valueLabel = formatEv(PhotoScales.COMPENSATION_EV[state.compensationIndex]),
                        value = state.compensationIndex.toFloat(),
                        valueRange = 0f..(PhotoScales.COMPENSATION_EV.size - 1).toFloat(),
                        steps = PhotoScales.COMPENSATION_EV.size - 2,
                        onValueChange = { state.compensationIndex = it.toInt() }
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashProfilePicker(
    profiles: List<FlashProfile>,
    selectedGuideNumber: Double?,
    onProfileSelected: (FlashProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selected = profiles.firstOrNull { profile ->
        selectedGuideNumber != null && kotlin.math.abs(profile.guideNumber - selectedGuideNumber) < 0.0001
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.label_flash_profile),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = profiles.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selected?.name ?: stringResource(R.string.flash_profile_manual),
                            textAlign = TextAlign.Start
                        )
                        if (selected != null) {
                            Text(
                                text = "GN ${FlashCalculator.formatGuideNumber(selected.guideNumber)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.ExpandMore,
                        contentDescription = null
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                profiles.forEach { profile ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(profile.name)
                                Text(
                                    text = "GN ${FlashCalculator.formatGuideNumber(profile.guideNumber)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            onProfileSelected(profile)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SolvedRow(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(R.string.label_solved),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ResultsPane(
    inputs: FlashInputs,
    result: CalcResult,
    unit: DistanceUnit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ResultCard(result = result)

        HintCard(
            text = stringResource(modeInfoRes(inputs.mode)),
            level = HintLevel.INFO
        )

        result.advices.forEach { advice ->
            val mapped = mapAdvice(advice, inputs, unit)
            if (mapped != null) {
                HintCard(text = mapped.first, level = mapped.second)
            }
        }

        if (result.rows.isNotEmpty()) {
            ReferenceTable(rows = result.rows, unit = unit)
        }
    }
}

@Composable
private fun ResultCard(result: CalcResult, modifier: Modifier = Modifier) {
    val visualStyle = com.gncal.app.ui.theme.LocalVisualStyle.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (visualStyle == com.gncal.app.data.VisualStyle.STANDARD) Modifier
                else Modifier.visualStyle(visualStyle, MaterialTheme.shapes.extraLarge, isCard = true)
            ),
        color = if (visualStyle == com.gncal.app.data.VisualStyle.STANDARD && result.isValid) {
            MaterialTheme.colorScheme.primaryContainer
        } else if (visualStyle == com.gncal.app.data.VisualStyle.STANDARD) {
            MaterialTheme.colorScheme.errorContainer
        } else if (result.isValid) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
        },
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(resultLabelRes(result.mode)),
                style = MaterialTheme.typography.titleMedium,
                color = if (result.isValid) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onErrorContainer
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                text = result.primary,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = if (result.isValid) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onErrorContainer
                )
                if (result.unitSuffix.isNotEmpty()) {
                    Text(
                        text = " ${result.unitSuffix}",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (result.isValid) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            if (result.detail != null) {
                Text(
                    text = result.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ReferenceTable(rows: List<ReferenceRow>, unit: DistanceUnit, modifier: Modifier = Modifier) {
    SectionCard(title = stringResource(R.string.section_reference), modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.table_header_aperture),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.table_header_distance),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.chunked(3).forEach { group ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        val row = group.getOrNull(index)
                        if (row != null) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = MaterialTheme.shapes.medium,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 11.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "f/${FlashCalculator.formatAperture(row.aperture)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "${FlashCalculator.formatDistance(row.distance)} ${unit.symbol}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        } else {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun mapAdvice(advice: Advice, inputs: FlashInputs, unit: DistanceUnit): Pair<String, HintLevel>? {
    return when (advice) {
        Advice.InvalidInput -> stringResource(R.string.msg_enter_valid) to HintLevel.WARNING
        Advice.SyncSpeedReminder -> stringResource(R.string.msg_sync_speed) to HintLevel.INFO
        Advice.ApertureOutOfRange -> stringResource(R.string.warn_aperture_limit) to HintLevel.WARNING
        is Advice.NearestAperture -> stringResource(
            R.string.msg_nearest_aperture,
            FlashCalculator.formatAperture(advice.standard),
            formatEv(advice.deltaEv)
        ) to HintLevel.TIP
        is Advice.DistanceTooNear -> stringResource(
            R.string.warn_distance_near,
            FlashCalculator.formatDistance(advice.threshold),
            unit.symbol
        ) to HintLevel.WARNING
        is Advice.DistanceTooFar -> stringResource(
            R.string.warn_distance_far,
            FlashCalculator.formatDistance(advice.threshold),
            unit.symbol
        ) to HintLevel.WARNING
        is Advice.IsoTooHigh -> stringResource(R.string.warn_iso_high, advice.iso) to HintLevel.WARNING
        is Advice.IsoTooLow -> stringResource(R.string.warn_iso_low, advice.iso) to HintLevel.TIP
        is Advice.GnTooLarge -> stringResource(R.string.warn_gn_large, advice.threshold) to HintLevel.WARNING
        is Advice.GnTooSmall -> stringResource(R.string.warn_gn_small, advice.threshold) to HintLevel.TIP
        is Advice.EffectiveGn -> {
            if (inputs.powerIndex == 0) {
                stringResource(
                    R.string.result_effective_gn
                ) + "：${FlashCalculator.formatGuideNumber(advice.gn)} ${unit.symbol}" to HintLevel.INFO
            } else {
                stringResource(
                    R.string.msg_power_effect,
                    "1/${PhotoScales.POWER_DENOMINATORS[inputs.powerIndex]}",
                    "${FlashCalculator.formatGuideNumber(advice.gn)} ${unit.symbol}"
                ) to HintLevel.TIP
            }
        }
    }
}

private fun formatEv(value: Double): String =
    if (value >= 0) String.format(Locale.US, "+%.1f EV", value)
    else String.format(Locale.US, "%.1f EV", value)

private fun modeLabelRes(mode: SolveMode) = when (mode) {
    SolveMode.APERTURE -> R.string.mode_aperture
    SolveMode.DISTANCE -> R.string.mode_distance
    SolveMode.GUIDE_NUMBER -> R.string.mode_gn
    SolveMode.ISO -> R.string.mode_iso
}

private fun resultLabelRes(mode: SolveMode) = when (mode) {
    SolveMode.APERTURE -> R.string.result_aperture
    SolveMode.DISTANCE -> R.string.result_distance
    SolveMode.GUIDE_NUMBER -> R.string.result_required_gn
    SolveMode.ISO -> R.string.result_iso
}

private fun modeInfoRes(mode: SolveMode) = when (mode) {
    SolveMode.APERTURE -> R.string.info_mode_aperture
    SolveMode.DISTANCE -> R.string.info_mode_distance
    SolveMode.GUIDE_NUMBER -> R.string.info_mode_gn
    SolveMode.ISO -> R.string.info_mode_iso
}
