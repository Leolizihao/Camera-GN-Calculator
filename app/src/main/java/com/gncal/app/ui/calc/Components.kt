package com.gncal.app.ui.calc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gncal.app.R
import com.gncal.app.data.VisualStyle
import com.gncal.app.ui.theme.LocalVisualStyle
import com.gncal.app.ui.theme.visualStyle

/** 提示级别：决定提示卡片的配色 */
enum class HintLevel { INFO, TIP, WARNING }

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val currentVisualStyle = LocalVisualStyle.current
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (currentVisualStyle != VisualStyle.STANDARD) {
                    Modifier.visualStyle(currentVisualStyle, MaterialTheme.shapes.large, isCard = true)
                } else {
                    Modifier
                }
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (currentVisualStyle == VisualStyle.STANDARD) {
                MaterialTheme.colorScheme.surfaceContainerLow
            } else {
                Color.Transparent
            }
        ),
        border = if (currentVisualStyle == VisualStyle.STANDARD) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            )
        } else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}

@Composable
fun NumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    suffix: String? = null,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // 只允许数字与一个小数点
            val filtered = input.filter { it.isDigit() || it == '.' }
                .let { text ->
                    val firstDot = text.indexOf('.')
                    if (firstDot >= 0) {
                        text.substring(0, firstDot + 1) + text.substring(firstDot + 1).replace(".", "")
                    } else text
                }
            onValueChange(filtered)
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        suffix = suffix?.let { { Text(it) } },
        supportingText = supportingText?.let { { Text(it) } },
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = MaterialTheme.shapes.medium
    )
}

/** 带 ± 微调按钮的滑杆，用于标准档位（1/3 档）选择 */
@Composable
fun SteppedSliderRow(
    label: String,
    valueLabel: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    caption: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.titleMedium,
                color = if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onValueChange((value - 1).coerceIn(valueRange)) }, enabled = enabled) {
                Icon(
                    imageVector = Icons.Outlined.Remove,
                    contentDescription = stringResource(R.string.cd_decrease),
                    modifier = Modifier.size(20.dp)
                )
            }
            Slider(
                value = value,
                onValueChange = { onValueChange(it.coerceIn(valueRange)) },
                valueRange = valueRange,
                steps = steps,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onValueChange((value + 1).coerceIn(valueRange)) }, enabled = enabled) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.cd_increase),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        if (caption != null) {
            Text(
                text = caption,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HintCard(text: String, level: HintLevel, modifier: Modifier = Modifier) {
    val visualStyle = LocalVisualStyle.current
    val (container, content, icon) = when (level) {
        HintLevel.INFO -> Triple(
            MaterialTheme.colorScheme.surfaceContainerHighest,
            MaterialTheme.colorScheme.onSurfaceVariant,
            Icons.Outlined.Info
        )
        HintLevel.TIP -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            Icons.Outlined.Bolt
        )
        HintLevel.WARNING -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Outlined.Warning
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (visualStyle == VisualStyle.STANDARD) Modifier
                else Modifier.visualStyle(visualStyle, MaterialTheme.shapes.medium, isCard = true)
            ),
        color = if (visualStyle == VisualStyle.STANDARD) {
            container
        } else {
            container.copy(alpha = 0.68f)
        },
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = content,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                textAlign = TextAlign.Start
            )
        }
    }
}
