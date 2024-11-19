package com.skul.yuriy.composeplayground.feature.gooey.blurConcept.util

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun BlurFilledTonalIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    blurRadius: Dp = 28.dp,
    iconSize: Dp = 28.dp,
    containerColor: Color = Color.Gray,
    contentColor: Color = Color.White
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .outlineBlur(blurRadius, shape = CircleShape, color = containerColor),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = containerColor,    // Set custom container color
            contentColor = contentColor         // Set custom icon color
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}