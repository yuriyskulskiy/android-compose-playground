package com.skul.yuriy.composeplayground.feature.animatedBorderRect.items

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.ExampleIndexText
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.RectLabeledSectionWrapper
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.SectionDivider
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurrender.RenderEffectBlurRectShadowBox

@Composable
internal fun RenderEffectBlurExampleItem(
    common: AnimatedBorderRectCommonSpec,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    val shadowBoxModifier = Modifier
        .height(common.shadowBoxHeight)
        .width(common.shadowBoxWidth)

    RectLabeledSectionWrapper(
        modifier = modifier.fillMaxWidth(),
        text = stringResource(R.string.render_effect_blur_scaled_glow)
    ) {
        RenderEffectBlurRectShadowBox(
            modifier = shadowBoxModifier,
            color = Color(0xFFFF6A00),
            cornerRadius = common.cornerRadius,
            initialBlurRadius = 4.dp,
            pressedBlurRadius = 14.dp,
            initialHaloShadowWidth = 4.dp,
            pressedHaloShadowWidth = 16.dp
        ) {
            ExampleIndexText(3)
        }
    }

    if (showDivider) {
        SectionDivider()
    }
}
