package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun GradientHaloFilledIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    haloRadius: Dp = 36.dp,
    iconSize: Dp = 28.dp,
    containerColor: Color = Color.Gray,
    contentColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(size)
            .outlineGradient(
                gradientRadiusOffset = haloRadius,
                color = containerColor
            ) // Gradient halo
            .background(color = containerColor)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, //no ripple
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(iconSize)
        )
    }
}