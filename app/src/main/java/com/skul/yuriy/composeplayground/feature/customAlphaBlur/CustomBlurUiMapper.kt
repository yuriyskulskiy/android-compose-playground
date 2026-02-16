package com.skul.yuriy.composeplayground.feature.customAlphaBlur

import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.BlurKernelQuality
import com.skul.yuriy.composeplayground.feature.customAlphaBlur.state.BlurPageKey

internal fun BlurPageKey.displayName(): String = when (this) {
    BlurPageKey.Static -> "Static Blur"
    BlurPageKey.Local -> "Local Blur"
    BlurPageKey.Dynamic -> "Dynamic Blur"
    BlurPageKey.KernelQuality -> "Kernel Quality"
    BlurPageKey.Complex -> "All-in-One Blur"
}

internal data class KernelQualityLabel(
    val title: String,
    val subtitle: String,
)

internal fun BlurKernelQuality.toUiLabel(): KernelQualityLabel = when (this) {
    BlurKernelQuality.Taps17 -> KernelQualityLabel(
        title = "Fast",
        subtitle = "17 taps"
    )

    BlurKernelQuality.Taps61 -> KernelQualityLabel(
        title = "Balanced",
        subtitle = "61 taps"
    )

    BlurKernelQuality.Taps101 -> KernelQualityLabel(
        title = "Ultra",
        subtitle = "101 taps"
    )
}
