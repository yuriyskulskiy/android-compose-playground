package com.skul.yuriy.composeplayground.feature.customAlphaBlur.state

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val BlurRadiusSteps = listOf(2, 4, 6, 8, 10, 12, 14, 16, 32, 48)
private const val DefaultBlurRadiusStepIndex = 6

enum class CustomBlurMode {
    Native,
    AglslAlphaLinear,
    AgslAlphaGaussian,
}

enum class BlurPageKey(
) {
    Static,
    KernelQuality,
    Local,
    Dynamic,
    Complex,
}

enum class BlurKernelQuality {
    Taps17,
    Taps61,
    Taps101,
}

data class CustomBlurDataState(
    val selectedPage: BlurPageKey = BlurPageKey.Static,
    val blurMode: CustomBlurMode = CustomBlurMode.Native,
    val blurKernelQuality: BlurKernelQuality = BlurKernelQuality.Taps17,
    val blurRadiusStepIndex: Int = DefaultBlurRadiusStepIndex,
) {
    val isFirstPage: Boolean
        get() = selectedPage.ordinal == 0

    val isLastPage: Boolean
        get() = selectedPage.ordinal == BlurPageKey.entries.lastIndex

    val blurRadiusDp: Dp
        get() = BlurRadiusSteps[blurRadiusStepIndex.coerceIn(0, BlurRadiusSteps.lastIndex)].dp

    companion object {
        val Saver: Saver<CustomBlurDataState, Any> = listSaver(
            save = { state ->
                listOf(
                    state.selectedPage.name,
                    state.blurMode.name,
                    state.blurKernelQuality.name,
                    state.blurRadiusStepIndex
                )
            },
            restore = { raw ->
                CustomBlurDataState(
                    selectedPage = raw.enumOrDefault(0, BlurPageKey.Static),
                    blurMode = raw.enumOrDefault(1, CustomBlurMode.Native),
                    blurKernelQuality = raw.enumOrDefault(2, BlurKernelQuality.Taps17),
                    blurRadiusStepIndex = (raw.getOrNull(3) as? Int)
                        ?.coerceIn(0, BlurRadiusSteps.lastIndex)
                        ?: DefaultBlurRadiusStepIndex
                )
            }
        )
    }
}

sealed interface CustomBlurAction {
    data object PreviousPage : CustomBlurAction
    data object NextPage : CustomBlurAction
    data object ChangeBlurRadius : CustomBlurAction
    data class SelectPage(val page: BlurPageKey) : CustomBlurAction
    data class SelectBlurMode(val mode: CustomBlurMode) : CustomBlurAction
    data class SelectBlurKernelQuality(val quality: BlurKernelQuality) : CustomBlurAction
}

fun CustomBlurDataState.reduce(action: CustomBlurAction): CustomBlurDataState {
    return when (action) {
        CustomBlurAction.PreviousPage -> {
            val previous = (selectedPage.ordinal - 1).coerceAtLeast(0)
            copy(selectedPage = BlurPageKey.entries[previous])
        }

        CustomBlurAction.NextPage -> {
            val next = (selectedPage.ordinal + 1).coerceAtMost(BlurPageKey.entries.lastIndex)
            copy(selectedPage = BlurPageKey.entries[next])
        }

        CustomBlurAction.ChangeBlurRadius -> {
            val nextIndex = (blurRadiusStepIndex + 1) % BlurRadiusSteps.size
            copy(blurRadiusStepIndex = nextIndex)
        }

        is CustomBlurAction.SelectPage -> copy(selectedPage = action.page)
        is CustomBlurAction.SelectBlurMode -> copy(blurMode = action.mode)
        is CustomBlurAction.SelectBlurKernelQuality -> copy(blurKernelQuality = action.quality)
    }
}

private inline fun <reified T : Enum<T>> List<Any>.enumOrDefault(
    index: Int,
    default: T,
): T {
    val raw = getOrNull(index) as? String ?: return default
    return runCatching { enumValueOf<T>(raw) }.getOrDefault(default)
}
