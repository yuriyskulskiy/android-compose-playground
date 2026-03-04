package com.skul.yuriy.composeplayground.feature.animatedBorderRect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun BoxScope.ExampleIndexText(index: Int) {
    Text(
        text = index.toString(),
        color = Color.White,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 44.sp
    )
}

@Composable
internal fun RenderModeRadioOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = Color.White.copy(alpha = 0.55f)
            )
        )
        Text(
            text = label,
            color = Color.White
        )
    }
}

@Composable
internal fun OutlinedLayerStepButton(
    imageVector: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    size: Dp = 36.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val borderColor = if (isPressed) Color.White else Color.White.copy(alpha = 0.7f)

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(size),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isPressed) Color.White.copy(alpha = 0.16f) else Color.Transparent,
            contentColor = if (enabled) Color.White else Color.White.copy(alpha = 0.45f),
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.45f)
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

@Composable
internal fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = Color.White.copy(alpha = 0.18f)
    )
}

internal fun nextLayerCount(current: Int, max: Int): Int {
    if (current < 0) return 0
    if (current >= max) return max

    return when {
        current < 5 -> (current + 1).coerceAtMost(max)
        current < 10 -> minOf(current + 2, 10, max)
        else -> {
            val next = if (current % 5 == 0) current + 5 else ((current / 5) + 1) * 5
            next.coerceAtMost(max)
        }
    }
}

internal fun prevLayerCount(current: Int): Int {
    if (current <= 0) return 0

    return when {
        current <= 5 -> current - 1
        current <= 10 -> maxOf(current - 2, 5)
        else -> {
            val prev = if (current % 5 == 0) current - 5 else (current / 5) * 5
            prev.coerceAtLeast(10)
        }
    }
}
