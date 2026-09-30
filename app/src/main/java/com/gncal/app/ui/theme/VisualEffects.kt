package com.gncal.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.gncal.app.data.VisualStyle

/**
 * Applies visual styling based on the selected preset.
 * Uses translucent surfaces and gradient borders for lightweight GPU-friendly effects.
 */
fun Modifier.visualStyle(
    style: VisualStyle,
    shape: Shape,
    isCard: Boolean = false
): Modifier = composed {
    when (style) {
        VisualStyle.STANDARD -> this
        
        VisualStyle.FROSTED -> {
            val surface = MaterialTheme.colorScheme.surface
            val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
            val outline = MaterialTheme.colorScheme.outline
            
            if (isCard) {
                this
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                surface.copy(alpha = 0.7f),
                                surfaceVariant.copy(alpha = 0.5f)
                            )
                        ),
                        shape = shape
                    )
                    .border(
                        width = 1.dp,
                        color = outline.copy(alpha = 0.2f),
                        shape = shape
                    )
            } else {
                this.background(surface.copy(alpha = 0.85f))
            }
        }
        
        VisualStyle.LIQUID -> {
            val primary = MaterialTheme.colorScheme.primary
            val surface = MaterialTheme.colorScheme.surface
            val outline = MaterialTheme.colorScheme.outline
            
            if (isCard) {
                this
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                surface.copy(alpha = 0.9f),
                                surface.copy(alpha = 0.7f),
                                primary.copy(alpha = 0.05f)
                            ),
                            radius = 1200f
                        ),
                        shape = shape
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primary.copy(alpha = 0.3f),
                                outline.copy(alpha = 0.15f),
                                primary.copy(alpha = 0.2f)
                            )
                        ),
                        shape = shape
                    )
            } else {
                this.background(
                    Brush.verticalGradient(
                        colors = listOf(
                            surface.copy(alpha = 0.95f),
                            surface.copy(alpha = 0.98f)
                        )
                    )
                )
            }
        }
    }
}
