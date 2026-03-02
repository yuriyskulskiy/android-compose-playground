package com.skul.yuriy.composeplayground.feature.animatedBorderRect

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.BlurredRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.FireShaderDraftRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.MultiLayerRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.RadialLinearDraftRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.ShadowLayerRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.SimpleAgslBorderRectShadowBox
import com.skul.yuriy.composeplayground.util.regularComponents.LabeledSectionWrapper

@Composable
fun ScreenContent(
    isBorderEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LabeledSectionWrapper(
            modifier = Modifier.size(width = 220.dp, height = 120.dp),
            isBorderEnabled = isBorderEnabled,
            text = stringResource(R.string.multi_layer_shadow)
        ) { mod ->
            MultiLayerRectShadowBox(
                modifier = mod,
                color = Color.Green,
                cornerRadius = 24.dp,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 36.dp
            )
        }

        LabeledSectionWrapper(
            modifier = Modifier.size(width = 220.dp, height = 120.dp),
            isBorderEnabled = isBorderEnabled,
            text = stringResource(R.string.paint_with_blurmaskfilter)
        ) { mod ->
            BlurredRectShadowBox(
                modifier = mod,
                color = Color.Green,
                cornerRadius = 24.dp,
                initialBlurRadius = 4.dp,
                pressedBlurRadius = 16.dp,
                initialHaloShadowWidth = 4.dp,
                pressedHaloShadowWidth = 36.dp
            )
        }

        LabeledSectionWrapper(
            modifier = Modifier.size(width = 220.dp, height = 120.dp),
            isBorderEnabled = isBorderEnabled,
            text = stringResource(R.string.outlined_shadow_layer)
        ) { mod ->
            ShadowLayerRectShadowBox(
                modifier = mod,
                color = Color.Red,
                cornerRadius = 24.dp,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 28.dp
            )
        }

        LabeledSectionWrapper(
            modifier = Modifier.size(width = 220.dp, height = 120.dp),
            isBorderEnabled = isBorderEnabled,
            text = stringResource(R.string.radial_linear_draft)
        ) { mod ->
            RadialLinearDraftRectShadowBox(
                modifier = mod,
                color = Color.Yellow,
                cornerRadius = 24.dp,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 36.dp
            )
        }

        LabeledSectionWrapper(
            modifier = Modifier.size(width = 220.dp, height = 120.dp),
            isBorderEnabled = isBorderEnabled,
            text = stringResource(R.string.simple_agsl_border)
        ) { mod ->
            SimpleAgslBorderRectShadowBox(
                modifier = mod,
                cornerRadius = 24.dp,
                initialHaloBorderWidth = 4.dp,
                pressedHaloBorderWidth = 4.dp
            )
        }

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val sandboxModifier = Modifier
            .fillMaxWidth()
            .height(screenWidth)
            .graphicsLayer {
                clip = true
            }
            .then(
                if (isBorderEnabled) {
                    Modifier.border(1.dp, Color.White)
                } else {
                    Modifier
                }
            )

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
