package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.RotationShapeText
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombustext.RhombusText
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombustext.RhombusTextLayoutConfig

@Composable
internal fun RotationTextContent(
    text: String,
    calculatorState: CalculatorUiState,
    textLayoutInfo: RotationShapeTextLayoutInfo,
    statusBarHeight: Dp,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        if (calculatorState.usesRhombusText) {
            RhombusText(
                text = text,
                config = RhombusTextLayoutConfig(
                    lineWidth = textLayoutInfo.lineWidth,
                    firstLineOffset = textLayoutInfo.firstLineOffset,
                    horizontalShiftPerHeight = textLayoutInfo.horizontalShiftPerHeight,
                    contentTopInset = statusBarHeight + RotationHostTopBarHeight,
                ),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            RotationShapeText(
                text = text,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
