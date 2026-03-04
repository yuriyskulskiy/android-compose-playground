package com.skul.yuriy.composeplayground.feature.animatedBorderRect

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.items.AnimatedBorderRectCommonSpec
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.items.BlurMaskExampleItem
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.items.FireShaderDraftExampleItem
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.items.GradientExampleItem
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.items.MultiLayerExampleItem
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.items.RenderEffectBlurExampleItem
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.items.ShadowLayerExampleItem
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.items.SimpleAgslExampleItem

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier
) {
    val commonSpec = AnimatedBorderRectCommonSpec(
        cornerRadius = 24.dp,
        shadowBoxWidth = 220.dp,
        shadowBoxHeight = 120.dp
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MultiLayerExampleItem(common = commonSpec)
        BlurMaskExampleItem(common = commonSpec)
        RenderEffectBlurExampleItem(common = commonSpec)
        ShadowLayerExampleItem(common = commonSpec)
        GradientExampleItem(common = commonSpec)
        SimpleAgslExampleItem(common = commonSpec)
        FireShaderDraftExampleItem(common = commonSpec, showDivider = false)
    }
}
