package com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.circularItemUiItems
import com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.MetaballHorizontalEdgeSection
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold50PercentEffect

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CircularItemsSection(
    modifier: Modifier = Modifier,
) {
    val effectMagnitude = 56.dp
    val blurRadius = 20.dp
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
            CircularItem(icon = item.icon)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun CircularItem(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(34.dp)
        )
    }
}
