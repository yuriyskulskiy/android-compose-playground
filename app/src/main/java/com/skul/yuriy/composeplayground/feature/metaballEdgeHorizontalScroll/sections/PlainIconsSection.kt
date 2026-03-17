package com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.MetaballHorizontalEdgeSection
import com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.circularItemUiItems
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold50PercentEffect

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PlainIconsSection(
    modifier: Modifier = Modifier,
) {
    val effectMagnitude = 56.dp
    val blurRadius = 30.dp
    val alphaThresholdEffect = alphaThreshold50PercentEffect
    val edgeAlpha = 0.5f

    MetaballHorizontalEdgeSection(
        modifier = modifier,
        effectMagnitude = effectMagnitude,
        blurRadius = blurRadius,
        alphaThresholdEffect = alphaThresholdEffect,
        edgeAlpha = edgeAlpha,
    ) {
        circularItemUiItems.forEach { item ->
            PlainIconItem(icon = item.icon)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun PlainIconItem(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.Black,
        modifier = modifier.size(56.dp)
    )
}
