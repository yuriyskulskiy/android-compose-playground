package com.skul.yuriy.composeplayground.feature.animatedBorderRect.items

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
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.shadowlayer.ShadowLayerRectShadowBox

@Composable
internal fun ShadowLayerExampleItem(
    common: AnimatedBorderRectCommonSpec,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    var shadowLayerPasses by remember { mutableIntStateOf(2) }
    val shadowBoxModifier = Modifier
        .height(common.shadowBoxHeight)
        .width(common.shadowBoxWidth)

    RectLabeledSectionWrapper(
        modifier = modifier.fillMaxWidth(),
        text = stringResource(R.string.outlined_shadow_layer),
        aboveTitleContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedLayerStepButton(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease shadow passes",
                    enabled = shadowLayerPasses > 0,
                    onClick = { shadowLayerPasses-- }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Shadow Passes $shadowLayerPasses",
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedLayerStepButton(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase shadow passes",
                    enabled = shadowLayerPasses < 20,
                    onClick = { shadowLayerPasses++ }
                )
            }
        }
    ) {
        ShadowLayerRectShadowBox(
            modifier = shadowBoxModifier,
            color = Color.Red,
            cornerRadius = common.cornerRadius,
            initialHaloBorderWidth = 4.dp,
            pressedHaloBorderWidth = 28.dp,
            passesCount = shadowLayerPasses
        ) {
            ExampleIndexText(4)
        }
    }

    if (showDivider) {
        SectionDivider()
    }
}
