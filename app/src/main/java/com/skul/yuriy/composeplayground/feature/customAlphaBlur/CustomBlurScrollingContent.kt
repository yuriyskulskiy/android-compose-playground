package com.skul.yuriy.composeplayground.feature.customAlphaBlur

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.material3.Text
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.dynamic.alphaGaussianBlurByHeight
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.dynamic.alphaLinearBlurByHeight
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.kernelTaps.alphaGaussianBlurTest101
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.kernelTaps.alphaGaussianBlurTest61
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.complex.alphaGaussianBlurLocalDynamicThreeZone
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.complex.alphaLinearBlurLocalDynamicThreeZone
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.localThreeZones.alphaGaussianBlurLocalThreeZone
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.localThreeZones.alphaLinearBlurLocalThreeZone
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.static.alphaGaussianBlur
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.effects.static.alphaLinearBlur
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.page.ComplexBlurPage
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.page.KernelQualityPage
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.page.LocalBlurPage
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.page.StaticBlurPage
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.page.VariableBlurPage
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.BlurKernelQuality
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.BlurPageKey
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.CustomBlurMode

@Composable
fun CustomBlurScrollingContent(
    modifier: Modifier = Modifier,
    blurRadius: Dp,
    blurMode: CustomBlurMode,
    selectedPage: BlurPageKey,
    selectedKernelQuality: BlurKernelQuality,
) {
    AnimatedContent(
        targetState = selectedPage,
        transitionSpec = {
            val direction = if (targetState.ordinal > initialState.ordinal) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }
            slideIntoContainer(
                towards = direction,
                animationSpec = tween(durationMillis = 350)
            ) togetherWith slideOutOfContainer(
                towards = direction,
                animationSpec = tween(durationMillis = 350)
            )
        },
        label = "CustomAlphaBlurPageSlide"
    ) { page ->
        if (isNativeModeUnsupportedOnPage(blurMode = blurMode, page = page)) {
            NotSupportedContent(
                modifier = modifier
            )
            return@AnimatedContent
        }

        val pageModifier = modifier.blurModifierForPage(
            selectedPage = page,
            blurMode = blurMode,
            blurRadius = blurRadius,
            selectedKernelQuality = selectedKernelQuality
        )

        when (page) {
            BlurPageKey.Static -> StaticBlurPage(
                modifier = pageModifier,
            )
            BlurPageKey.Local -> LocalBlurPage(
                modifier = pageModifier,
            )
            BlurPageKey.Dynamic -> VariableBlurPage(
                modifier = pageModifier,
            )
            BlurPageKey.KernelQuality -> KernelQualityPage(
                modifier = pageModifier,
            )
            BlurPageKey.Complex -> ComplexBlurPage(
                modifier = pageModifier,
            )
        }
    }
}

// Native Android blur() is supported only for the Static case.
// Local/Dynamic/Complex pages require AGSL-based implementations.
private fun isNativeModeUnsupportedOnPage(
    blurMode: CustomBlurMode,
    page: BlurPageKey,
): Boolean {
    if (blurMode != CustomBlurMode.Native) return false
    return page == BlurPageKey.Local ||
        page == BlurPageKey.Dynamic ||
        page == BlurPageKey.Complex
}

@Composable
private fun NotSupportedContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Not Supported")
    }
}

private fun Modifier.blurModifierForPage(
    selectedPage: BlurPageKey,
    blurMode: CustomBlurMode,
    blurRadius: Dp,
    selectedKernelQuality: BlurKernelQuality,
): Modifier {
    return when (selectedPage) {
        BlurPageKey.Static -> {
            when (blurMode) {
                CustomBlurMode.Native -> blur(blurRadius)
                CustomBlurMode.AglslAlphaLinear -> alphaLinearBlur(radius = blurRadius)
                CustomBlurMode.AgslAlphaGaussian -> alphaGaussianBlur(radius = blurRadius)
            }
        }

        BlurPageKey.Local -> {
            when (blurMode) {
                CustomBlurMode.Native -> blur(blurRadius)
                CustomBlurMode.AglslAlphaLinear -> alphaLinearBlurLocalThreeZone(
                    radius = blurRadius,
                    topOffset = 156.dp,
                    bottomOffset = 156.dp
                )

                CustomBlurMode.AgslAlphaGaussian -> alphaGaussianBlurLocalThreeZone(
                    radius = blurRadius,
                    topOffset = 156.dp,
                    bottomOffset = 156.dp
                )
            }
        }

        BlurPageKey.Dynamic -> {
            when (blurMode) {
                CustomBlurMode.Native -> blur(blurRadius)
                CustomBlurMode.AglslAlphaLinear -> alphaLinearBlurByHeight(radius = blurRadius)
                CustomBlurMode.AgslAlphaGaussian -> alphaGaussianBlurByHeight(radius = blurRadius)
            }
        }

        BlurPageKey.KernelQuality -> {
            when (selectedKernelQuality) {
                BlurKernelQuality.Taps17 -> alphaGaussianBlur(radius = blurRadius)
                BlurKernelQuality.Taps61 -> alphaGaussianBlurTest61(radius = blurRadius)
                BlurKernelQuality.Taps101 -> alphaGaussianBlurTest101(radius = blurRadius)
            }
        }

        BlurPageKey.Complex -> {
            when (blurMode) {
                CustomBlurMode.Native -> blur(blurRadius)
                CustomBlurMode.AglslAlphaLinear -> alphaLinearBlurLocalDynamicThreeZone(
                    radius = blurRadius,
                    topOffset = 250.dp,
                    bottomOffset = 250.dp
                )

                CustomBlurMode.AgslAlphaGaussian -> alphaGaussianBlurLocalDynamicThreeZone(
                    radius = blurRadius,
                    topOffset = 250.dp,
                    bottomOffset = 250.dp
                )
            }
        }
    }
}
