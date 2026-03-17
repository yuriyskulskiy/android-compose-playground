package com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skul.yuriy.composeplayground.feature.metaballEdgeHorizontalScroll.MetaballHorizontalEdgeSection
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold30PercentEffect

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HorizontalTextSection(
    modifier: Modifier = Modifier,
) {
    val effectMagnitude = 30.dp
    val blurRadius = 20.dp
    val alphaThresholdEffect = alphaThreshold30PercentEffect
    val edgeAlpha = 0.3f

    MetaballHorizontalEdgeSection(
        modifier = modifier,
        effectMagnitude = effectMagnitude,
        blurRadius = blurRadius,
        alphaThresholdEffect = alphaThresholdEffect,
        edgeAlpha = edgeAlpha,
    ) {
        Text(
            text = demoHorizontalText,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            style = TextStyle(
                fontSize = HorizontalTextSize,
                color = androidx.compose.ui.graphics.Color.Black,
                fontWeight = FontWeight.Bold,
            )
        )
    }
}

private val HorizontalTextSize = 56.sp

private const val demoHorizontalText =
    "HOME SEARCH PROFILE SETTINGS CART BUILD DONE FAVORITE HOME SEARCH PROFILE SETTINGS CART BUILD DONE FAVORITE"
