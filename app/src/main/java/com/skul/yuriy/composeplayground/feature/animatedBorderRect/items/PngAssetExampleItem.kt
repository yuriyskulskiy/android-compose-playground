package com.skul.yuriy.composeplayground.feature.animatedBorderRect.items

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.ExampleIndexText
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.png.HaloPngGlowingBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.RectLabeledSectionWrapper
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.SectionDivider

@Composable
internal fun PngAssetExampleItem(
    common: AnimatedBorderRectCommonSpec,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    RectLabeledSectionWrapper(
        modifier = modifier,
        text = stringResource(R.string.static_png_glow_border)
    ) {
        HaloPngGlowingBox(
            contentWidth = common.shadowBoxWidth,
            contentHeight = common.shadowBoxHeight,
            cornerRadius = common.cornerRadius,
            idleResId = R.drawable.purple_glow_border_idle_state,
            activeResId = R.drawable.purple_glow_border_active_state
        ) {
            ExampleIndexText(0)
        }
    }

    if (showDivider) {
        SectionDivider()
    }
}
