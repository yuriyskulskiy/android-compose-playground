package com.skul.yuriy.composeplayground.feature.animatedBorderRect

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.BlurredRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.ShadowLayerRectShadowBox
import com.skul.yuriy.composeplayground.util.regularComponents.LabeledSectionWrapper

@Composable
fun ColumnScope.ScreenContent(
    isBorderEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    LabeledSectionWrapper(
        modifier = modifier.size(width = 220.dp, height = 120.dp),
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
            pressedHaloShadowWidth = 24.dp
        )
    }

    LabeledSectionWrapper(
        modifier = modifier.size(width = 220.dp, height = 120.dp),
        isBorderEnabled = isBorderEnabled,
        text = stringResource(R.string.outlined_shadow_layer)
    ) { mod ->
        ShadowLayerRectShadowBox(
            modifier = mod,
            color = Color.Green,
            cornerRadius = 24.dp,
            initialHaloBorderWidth = 4.dp,
            pressedHaloBorderWidth = 32.dp
        )
    }
}
