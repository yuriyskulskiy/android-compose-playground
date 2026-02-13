package com.skul.yuriy.composeplayground.feature.customBlur

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
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.customBlur.effects.threeZones.alphaGaussianBlurThreeZone
import com.skul.yuriy.composeplayground.feature.customBlur.effects.threeZones.alphaLinearBlurThreeZone
import com.skul.yuriy.composeplayground.feature.customBlur.util.alphaThreshold20PercentEffect

@Composable
fun CustomBlurScrollingContent(
    modifier: Modifier = Modifier,
    blurRadius: androidx.compose.ui.unit.Dp,
    blurMode: CustomBlurMode,
) {
    val scrollState = rememberScrollState()
    val text = stringResource(R.string.very_long_mock_text).trimIndent()
    val verticalParam = 16.dp
    val offset = 40.dp

    val blurModifier = when (blurMode) {
        CustomBlurMode.Native -> Modifier.blur(blurRadius)

//        CustomBlurMode.AglslAlphaLiniar -> Modifier.alphaLinearBlur(blurRadius)
//        CustomBlurMode.AglslAlphaLiniar -> Modifier.alphaLinearBlurByHeight(blurRadius)
        CustomBlurMode.AglslAlphaLiniar -> Modifier.alphaLinearBlurThreeZone(
            radius = blurRadius,
            topOffset = offset,
            bottomOffset = offset
        )

//        CustomBlurMode.AgslAlphaGaussian -> Modifier.alphaGaussianBlur(blurRadius)
//        CustomBlurMode.AgslAlphaGaussian -> Modifier.alphaGaussianBlurByHeight(blurRadius)
        CustomBlurMode.AgslAlphaGaussian -> Modifier.alphaGaussianBlurThreeZone(
            radius = blurRadius,
            topOffset = offset,
            bottomOffset = offset
        )
//        CustomBlurMode.AgslAlphaGaussian -> Modifier.alphaGaussianBlurThreeZoneWithThreshold(
//            radius = blurRadius,
//            topOffset = offset,
//            bottomOffset = offset,
//            threshold01 = 0.2f
//        )

    }

//    Box(Modifier.fillMaxSize()) {


    Box(
        modifier = modifier

            .fillMaxSize()
//            .fadingTopBottomEdgesDp( 6.dp)
//            .background(color = Color.Red)
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
                clip = false

//                this.renderEffect = alphaThreshold2PercentEffect
//                this.renderEffect = alphaThreshold5PercentEffect
                this.renderEffect = alphaThreshold20PercentEffect
//                this.renderEffect = alphaThreshold30PercentEffect
//                this.renderEffect = alphaThreshold50PercentEffect
//                this.renderEffect = alphaThreshold70PercentEffect


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
//                    style = MaterialTheme.typography.titleLarge,
                style = MaterialTheme.typography.displayMedium,
//                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                text = text,
                fontWeight = FontWeight.Normal
            )
            Spacer(Modifier.height(56.dp))
        }
    }
}
