package com.skul.yuriy.composeplayground.feature.metaballEdgesAdvanced

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold10PercentEffect
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold20PercentEffect
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold25PercentEffect
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold30PercentEffect
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold40PercentEffect

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MetaballEdgeAdvancedContent(
    modifier: Modifier = Modifier,
    settings: MetaballEdgeAdvancedSettingsState,
    showGradientEdges: Boolean,
) {
    val text = stringResource(R.string.very_long_mock_text).trimIndent()
    val verticalParam = 16.dp
    val offset = 40.dp
    val edgeAlpha = settings.thresholdPercent / 100f

    val blurModifier = Modifier.metaballEdgeAdvancedGaussianBlur(
        radius = settings.blurRadius,
        topOffset = offset,
        bottomOffset = offset
    )
    val thresholdEffect = when (settings.thresholdPercent) {
        10 -> alphaThreshold10PercentEffect
        20 -> alphaThreshold20PercentEffect
        25 -> alphaThreshold25PercentEffect
        30 -> alphaThreshold30PercentEffect
        40 -> alphaThreshold40PercentEffect
        else -> alphaThreshold30PercentEffect
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
                clip = false
                renderEffect = thresholdEffect
            }
    ) {
        if (showGradientEdges) {
            MetaballEdgeAdvancedGradientEdges(
                edgeAlpha = edgeAlpha,
                verticalParam = verticalParam,
            )
        }

        MetaballEdgeAdvancedTextContent(
            modifier = Modifier
                .fillMaxSize()
                .then(blurModifier)
                .padding(horizontal = 16.dp),
            text = text,
            textSize = settings.textSize,
            lineHeightMultiplier = settings.lineHeightMultiplier,
        )
    }
}

@Composable
private fun BoxScope.MetaballEdgeAdvancedGradientEdges(
    edgeAlpha: Float,
    verticalParam: Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopStart)
            .height(verticalParam)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = edgeAlpha),
                        Color.Black.copy(alpha = 0.0f)
                    )
                )
            )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)
            .height(verticalParam)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.0f),
                        Color.Black.copy(alpha = edgeAlpha),
                    )
                )
            )
    )
}

@Composable
private fun MetaballEdgeAdvancedTextContent(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit,
    lineHeightMultiplier: Float,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(56.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = textSize,
                lineHeight = textSize * lineHeightMultiplier,
            ),
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black,
            text = text,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Justify,
        )
        Spacer(Modifier.height(56.dp))
    }
}
