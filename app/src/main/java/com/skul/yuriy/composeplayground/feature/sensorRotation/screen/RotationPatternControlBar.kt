package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R

internal val RotationPatternButtonSize = 40.dp
private val SelectedRotationPatternTint = Color.Red
private val DefaultRotationPatternTint = Color.White
private val PressedRotationPatternTint = Color(0xFFF2F2F2)
private val PressedSelectedRotationPatternTint = Color(0xFFFF9A9A)
private val DefaultRotationPatternBackground = Color.Black
private val PressedRotationPatternBackground = Color(0xFF3A3A3A)
private val RotationPatternBorderColor = Color.White.copy(alpha = 0.2f)

internal data class RotationPatternUiItem(
    val state: CalculatorUiState,
    @param:DrawableRes val iconRes: Int,
    val contentDescription: String,
)

@Composable
internal fun rememberRotationPatternItems(): List<RotationPatternUiItem> =
    remember {
        listOf(
            RotationPatternUiItem(
                state = CalculatorUiState.TwoPhaseSlide,
                iconRes = R.drawable.ic_rotation_pattern_two_phase,
                contentDescription = "Two-phase pattern",
            ),
            RotationPatternUiItem(
                state = CalculatorUiState.AspectSlide,
                iconRes = R.drawable.ic_rotation_pattern_aspect,
                contentDescription = "Aspect pattern",
            ),
            RotationPatternUiItem(
                state = CalculatorUiState.MorphingRect,
                iconRes = R.drawable.ic_rotation_pattern_rect,
                contentDescription = "Rectangle pattern",
            ),
            RotationPatternUiItem(
                state = CalculatorUiState.FittedMorphingRect,
                iconRes = R.drawable.ic_rotation_pattern_fitted,
                contentDescription = "Fitted pattern",
            ),
        )
    }

@Composable
internal fun RotationPatternControlBar(
    items: List<RotationPatternUiItem>,
    selectedState: CalculatorUiState,
    onPatternClick: (CalculatorUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            RotationPatternIconButton(
                iconRes = item.iconRes,
                contentDescription = item.contentDescription,
                isSelected = selectedState == item.state,
                onClick = { onPatternClick(item.state) },
            )
        }
    }
}

@Composable
private fun RotationPatternIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val iconTint =
        when {
            isSelected && isPressed -> PressedSelectedRotationPatternTint
            isSelected -> SelectedRotationPatternTint
            isPressed -> PressedRotationPatternTint
            else -> DefaultRotationPatternTint
        }
    val backgroundColor =
        if (isPressed) PressedRotationPatternBackground else DefaultRotationPatternBackground
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .then(modifier)
            .size(RotationPatternButtonSize)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = RotationPatternBorderColor,
                shape = CircleShape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = iconTint,
        )
    }
}
