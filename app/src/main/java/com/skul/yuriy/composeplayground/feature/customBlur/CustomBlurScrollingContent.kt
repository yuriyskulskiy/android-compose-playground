package com.skul.yuriy.composeplayground.feature.customBlur

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.customBlur.effects.threeZones.alphaGaussianBlurThreeZone
import com.skul.yuriy.composeplayground.feature.customBlur.effects.threeZones.alphaLinearBlurThreeZone
import com.skul.yuriy.composeplayground.feature.customBlur.test.verticalAlphaFadeTop1Bottom0

@Composable
fun CustomBlurScrollingContent(
    modifier: Modifier = Modifier,
    blurRadius: androidx.compose.ui.unit.Dp,
    blurMode: CustomBlurMode,
) {
    val scrollState = rememberScrollState()
    val text = stringResource(R.string.very_long_mock_text).trimIndent()

    val blurModifier = when (blurMode) {
        CustomBlurMode.Native -> Modifier.blur(blurRadius)

//        CustomBlurMode.AglslAlphaLiniar -> Modifier.alphaLinearBlur(blurRadius)
//        CustomBlurMode.AglslAlphaLiniar -> Modifier.alphaLinearBlurByHeight(blurRadius)
        CustomBlurMode.AglslAlphaLiniar -> Modifier.alphaLinearBlurThreeZone(
            radius = blurRadius,
            topOffset = 72.dp,
            bottomOffset = 72.dp
        )

//        CustomBlurMode.AgslAlphaGaussian -> Modifier.alphaGaussianBlur(blurRadius)
//        CustomBlurMode.AgslAlphaGaussian -> Modifier.alphaGaussianBlurByHeight(blurRadius)
        CustomBlurMode.AgslAlphaGaussian -> Modifier.alphaGaussianBlurThreeZone(
            radius = blurRadius,
            topOffset = 72.dp,
            bottomOffset = 72.dp
        )
    }

    Column(
        modifier = modifier
//            .graphicsLayer {
//                this.renderEffect = alphaThreshold5PercentEffect
//            }
            .then(blurModifier)
            .verticalAlphaFadeTop1Bottom0()
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            text = text,
            fontWeight = FontWeight.Normal
        )
    }
}
