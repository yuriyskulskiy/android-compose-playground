package com.skul.yuriy.composeplayground.feature.animatedBorder

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorder.border.BlurredCircularShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorder.border.GradientCircularShadowBox

@Composable
fun ColumnScope.ScreenContent(
    isBorderEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    // First LabeledSectionWrapper for BlurredCircularShadowBox
    LabeledSectionWrapper(
        modifier = modifier.size(200.dp),
        isBorderEnabled = isBorderEnabled,
        text = stringResource(R.string.paint_with_blurmaskfilter)
    ) { mod ->
        BlurredCircularShadowBox(
            modifier = mod,
            color = Color.Green,
            initialBlurRadius = 4.dp,
            initialHaloShadowWidth = 4.dp,
            pressedHaloShadowWidth = 24.dp,
            pressedBlurRadius = 16.dp,
            innerCircleContentSize = 100.dp,
        )
    }

    // Second LabeledSectionWrapper for GradientCircularShadowBox
    LabeledSectionWrapper(
        modifier = modifier.size(100.dp),
        isBorderEnabled = isBorderEnabled,
        text = stringResource(R.string.regular_radial_gradient_brush)
    ) { mod ->
        GradientCircularShadowBox(
            modifier = mod,
            color = Color.Green,
            initialHaloBorderWidth = 4.dp,
            pressedHaloBorderWidth = 32.dp,
        )
    }
}