package com.skul.yuriy.composeplayground.feature.metaballEdgesAdvanced

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.complex.alphaGaussianBlurLocalDynamicThreeZone
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.complex.alphaLinearBlurLocalDynamicThreeZone
import com.skul.yuriy.composeplayground.util.renderEffect.alphaThreshold20PercentEffect

@Composable
fun MetaballEdgeAdvancedContent(
    modifier: Modifier = Modifier,
    blurRadius: Dp,
    blurMode: MetaballEdgeAdvancedMode,
) {
    val scrollState = rememberScrollState()
    val text = stringResource(R.string.very_long_mock_text).trimIndent()
    val verticalParam = 16.dp
    val offset = 40.dp

    val blurModifier = when (blurMode) {
        MetaballEdgeAdvancedMode.Native -> Modifier.blur(blurRadius)
        MetaballEdgeAdvancedMode.AglslAlphaLiniar -> Modifier.alphaLinearBlurLocalDynamicThreeZone(
            radius = blurRadius,
            topOffset = offset,
            bottomOffset = offset
        )
        MetaballEdgeAdvancedMode.AgslAlphaGaussian -> Modifier.alphaGaussianBlurLocalDynamicThreeZone(
            radius = blurRadius,
            topOffset = offset,
            bottomOffset = offset
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
                clip = false
                renderEffect = alphaThreshold20PercentEffect
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .height(verticalParam)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
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
                            Color.Black.copy(alpha = 0.6f),
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .then(blurModifier)
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(56.dp))
            Text(
                style = MaterialTheme.typography.displayMedium,
                color = Color.Black,
                text = text,
                fontWeight = FontWeight.Normal
            )
            Spacer(Modifier.height(56.dp))
        }
    }
}
