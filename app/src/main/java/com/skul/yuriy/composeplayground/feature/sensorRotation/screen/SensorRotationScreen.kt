package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator

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
    var calculatorState by rememberSaveable { mutableStateOf(CalculatorUiState.AspectSlide) }
    val shapeCalculator: IRotationShapeCalculator = remember(calculatorState) {
        calculatorState.createCalculator()
    }
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
            rotateContentWithShape = true,
        ) {
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

//            SensorRotationTopAppBar(
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .padding(top = rememberStatusBarHeight()),
//                title = stringResource(R.string.sensor_rotation_demo)
//                onNavUp = onNavUp
//            )
//
//            Text(
//                modifier = Modifier.align(Alignment.Center),
//                text = stringResource(R.string.sensor_rotation_demo_placeholder),
//                color = Color.Black
//            )
    }
}
