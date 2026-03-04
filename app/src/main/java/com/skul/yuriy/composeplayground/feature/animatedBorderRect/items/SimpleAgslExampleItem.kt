package com.skul.yuriy.composeplayground.feature.animatedBorderRect.items

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.ExampleIndexText
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.RectLabeledSectionWrapper
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.RenderModeRadioOption
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.SectionDivider
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agslsimple.SimpleAgslBorderRectShadowBox
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agslsimple.SimpleAgslRenderMode

@Composable
internal fun SimpleAgslExampleItem(
    common: AnimatedBorderRectCommonSpec,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    var simpleAgslRenderMode by remember { mutableStateOf(SimpleAgslRenderMode.RenderEffect) }
    val shadowBoxModifier = Modifier
        .height(common.shadowBoxHeight)
        .width(common.shadowBoxWidth)

    RectLabeledSectionWrapper(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { clip = true },
        text = stringResource(R.string.simple_agsl_border),
        aboveTitleContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RenderModeRadioOption(
                    label = "RenderEffect",
                    selected = simpleAgslRenderMode == SimpleAgslRenderMode.RenderEffect,
                    onClick = { simpleAgslRenderMode = SimpleAgslRenderMode.RenderEffect }
                )
                Spacer(modifier = Modifier.width(8.dp))
                RenderModeRadioOption(
                    label = "Canvas Paint",
                    selected = simpleAgslRenderMode == SimpleAgslRenderMode.CanvasPaint,
                    onClick = { simpleAgslRenderMode = SimpleAgslRenderMode.CanvasPaint }
                )
            }
        }
    ) {
        SimpleAgslBorderRectShadowBox(
            modifier = shadowBoxModifier,
            color = Color(red = 0.10f, green = 0.30f, blue = 1.00f, alpha = 1f),
            cornerRadius = common.cornerRadius,
            maxHaloBorderWidth = 32.dp,
            renderMode = simpleAgslRenderMode
        ) {
            ExampleIndexText(6)
        }
    }

    if (showDivider) {
        SectionDivider()
    }
}
