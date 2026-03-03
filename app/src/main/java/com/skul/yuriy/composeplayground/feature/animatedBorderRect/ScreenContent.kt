package com.skul.yuriy.composeplayground.feature.animatedBorderRect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agslsimple.SimpleAgslBorderRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurmask.BlurredRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.gradient.GradientRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.multilayer.MultiLayerRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.shadowlayer.ShadowLayerRectShadowBox

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier
) {
    val cornerRadius = 24.dp
    val shadowBoxWidth = 220.dp
    val sectionModifier = Modifier.fillMaxWidth()
    val shadowBoxModifier = Modifier.height(120.dp).width(shadowBoxWidth)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RectLabeledSectionWrapper(
            modifier = sectionModifier,
            text = stringResource(R.string.multi_layer_shadow)
        ) {
            MultiLayerRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color.Green,
                cornerRadius = cornerRadius,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 36.dp
            )
        }

        RectLabeledSectionWrapper(
            modifier = sectionModifier,
            text = stringResource(R.string.paint_with_blurmaskfilter)
        ) {
            BlurredRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color.Green,
                cornerRadius = cornerRadius,
                initialBlurRadius = 4.dp,
                pressedBlurRadius = 16.dp,
                initialHaloShadowWidth = 4.dp,
                pressedHaloShadowWidth = 32.dp
            )
        }

        RectLabeledSectionWrapper(
            modifier = sectionModifier,
            text = stringResource(R.string.outlined_shadow_layer)
        ) {
            ShadowLayerRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color.Red,
                cornerRadius = cornerRadius,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 28.dp
            )
        }

        RectLabeledSectionWrapper(
            modifier = sectionModifier,
            text = stringResource(R.string.radial_linear_draft)
        ) {
            GradientRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color.Yellow,
                cornerRadius = cornerRadius,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 36.dp
            )
        }

        RectLabeledSectionWrapper(
            modifier = sectionModifier.background(color = Color.White),
            text = stringResource(R.string.simple_agsl_border)
        ) {
            SimpleAgslBorderRectShadowBox(
                modifier = shadowBoxModifier,
                color = Color(red = 0.10f, green = 0.30f, blue = 1.00f, alpha = 1f),
                cornerRadius = cornerRadius,
                maxHaloBorderWidth = 32.dp
            )
        }

//        Box(
//            modifier = sandboxModifier,
//            contentAlignment = Alignment.Center
//        ) {
//            FireShaderDraftRectShadowBox(
//                modifier = Modifier.size(width = 340.dp, height = 220.dp),
//                cornerRadius = 24.dp,
//                bandWidth = 14.dp,
//                contourWidth = 220.dp,
//                contourHeight = 120.dp
//            )
//        }
//        Spacer(modifier = Modifier.size(24.dp))
//        Text(
//            text = stringResource(R.string.fire_shader_draft),
//            color = Color.White
//        )
    }
}
