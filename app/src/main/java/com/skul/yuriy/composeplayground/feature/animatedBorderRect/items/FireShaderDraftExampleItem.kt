package com.skul.yuriy.composeplayground.feature.animatedBorderRect.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.ExampleIndexText
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.RectLabeledSectionWrapper
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.SectionDivider
import com.skul.yuriy.composeplayground.feature.animatedBorderRect.border.agsladvanced.FireShaderDraftRectShadowBox

@Composable
internal fun FireShaderDraftExampleItem(
    common: AnimatedBorderRectCommonSpec,
    modifier: Modifier = Modifier,
    showDivider: Boolean = false
) {
    var showAdvancedAgsl by remember { mutableStateOf(false) }

    RectLabeledSectionWrapper(
        modifier = modifier.fillMaxWidth(),
        text = stringResource(R.string.fire_shader),
        topSpacerHeight = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(268.dp)
        ) {
            OutlinedButton(
                onClick = { showAdvancedAgsl = !showAdvancedAgsl },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 8.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Text(text = if (showAdvancedAgsl) "Stop" else "Start")
            }

            if (showAdvancedAgsl) {
                FireShaderDraftRectShadowBox(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 48.dp)
                        .size(width = 340.dp, height = 220.dp),
                    cornerRadius = common.cornerRadius,
                    bandWidth = 14.dp,
                    contourWidth = common.shadowBoxWidth,
                    contourHeight = common.shadowBoxHeight
                ) {
                    ExampleIndexText(7)
                }
            }
        }
    }

    if (showDivider) {
        SectionDivider()
    }
}
