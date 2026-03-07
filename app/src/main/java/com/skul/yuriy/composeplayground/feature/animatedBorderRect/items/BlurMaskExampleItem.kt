package com.skul.yuriy.composeplayground.feature.animatedBorderRect.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.ExampleIndexText
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.OutlinedLayerStepButton
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.RectLabeledSectionWrapper
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.SectionDivider
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.blurmask.BlurredRectShadowBox

@Composable
internal fun BlurMaskExampleItem(
    common: AnimatedBorderRectCommonSpec,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    var blurMaskBlurRadiusDp by remember { mutableIntStateOf(16) }
    val shadowBoxModifier = Modifier
        .height(common.shadowBoxHeight)
        .width(common.shadowBoxWidth)

    RectLabeledSectionWrapper(
        modifier = modifier.fillMaxWidth(),
        text = stringResource(R.string.paint_with_blurmaskfilter),
        aboveTitleContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedLayerStepButton(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease blur radius",
                        enabled = blurMaskBlurRadiusDp > 2,
                        onClick = {
                            blurMaskBlurRadiusDp = (blurMaskBlurRadiusDp - 2).coerceAtLeast(2)
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Blur ${blurMaskBlurRadiusDp}dp",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedLayerStepButton(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase blur radius",
                        enabled = blurMaskBlurRadiusDp < 48,
                        onClick = {
                            blurMaskBlurRadiusDp = (blurMaskBlurRadiusDp + 2).coerceAtMost(48)
                        }
                    )
                }
            }
        }
    ) {
        BlurredRectShadowBox(
            modifier = shadowBoxModifier,
            color = Color.Green,
            cornerRadius = common.cornerRadius,
            initialBlurRadius = 4.dp,
            pressedBlurRadius = blurMaskBlurRadiusDp.dp,
            initialHaloShadowWidth = 4.dp,
            pressedHaloShadowWidth = 32.dp
        ) {
            ExampleIndexText(2)
        }
    }

    if (showDivider) {
        SectionDivider()
    }
}
