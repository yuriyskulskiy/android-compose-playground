package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.RotationShapeText
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombus.RhombusText
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombus.RhombusTextLayoutConfig
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar

private val RotationHostTopBarHeight = 64.dp

@Composable
fun SensorRotationScreen(
    onNavUp: () -> Unit
) {
    var smoothingState by rememberSaveable { mutableStateOf(SmoothingUiState.SmoothAlpha) }
    var sourceState by rememberSaveable { mutableStateOf(RotationSourceUiState.RawSensor) }
    val tiltAngle = rememberRotationAngle(
        sourceType = sourceState.sourceType,
        smoothingType = smoothingState
    )
    var calculatorState by rememberSaveable { mutableStateOf(CalculatorUiState.TwoPhaseSlide) }
    val shapeCalculator: IRotationShapeCalculator = remember(calculatorState) {
        calculatorState.createCalculator()
    }
    val baseText = stringResource(R.string.sensor_rotation_demo_text)
    val demoText = "$baseText\n\n$baseText"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures {
                    calculatorState = calculatorState.next()
                }
            }
    ) {
        RotationShapeContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            inset = 16.dp,
            rotationDegrees = tiltAngle,
            shapeCalculator = shapeCalculator,
            rotateContentWithShape = calculatorState.rotateContentWithShape,
        ) { textLayoutInfo ->
            val density = LocalDensity.current
            val topBarStartInset = with(density) {
                val topBarLocalY = RotationHostTopBarHeight.toPx() / 2f
                val shiftAtTopBar =
                    textLayoutInfo.firstLineOffset.toPx() +
                        topBarLocalY * textLayoutInfo.horizontalShiftPerHeight
                maxOf(0f, shiftAtTopBar).toDp()
            }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .height(RotationHostTopBarHeight)
                        .fillMaxWidth()
                        .background(Color.Black)
                ) {
                    CustomTopAppBar(
                        title = "Rotation",
                        onNavUp = onNavUp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = topBarStartInset),
                        enableHorizontalDivider = false,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    if (calculatorState.usesRhombusText) {
                        RhombusText(
                            text = demoText,
                            config = RhombusTextLayoutConfig(
                                lineWidth = textLayoutInfo.lineWidth,
                                firstLineOffset = textLayoutInfo.firstLineOffset,
                                horizontalShiftPerHeight = textLayoutInfo.horizontalShiftPerHeight,
                                contentTopInset = RotationHostTopBarHeight,
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        RotationShapeText(
                            text = demoText,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        DebugRotationFrame(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            inset = 16.dp,
            rotationDegrees = tiltAngle,
            shapeCalculator = shapeCalculator,
            calculatorLabel = calculatorState.label,
            onSwitchCalculator = { calculatorState = calculatorState.next() },
            smoothingLabel = smoothingState.label,
            onSwitchSmoothing = { smoothingState = smoothingState.next() },
            sourceLabel = sourceState.label,
            onSwitchSource = { sourceState = sourceState.next() },
        )

        // dont delete - прост опока он не нужен
    }
}
