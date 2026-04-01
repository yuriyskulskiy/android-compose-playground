package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator

@Composable
fun SensorRotationScreen(
    onNavUp: () -> Unit
) {
    val tiltAngle = rememberRotationAngle()
    var calculatorState by rememberSaveable { mutableStateOf(CalculatorUiState.AspectSlide) }
    val shapeCalculator: IRotationShapeCalculator = remember(calculatorState) {
        calculatorState.createCalculator()
    }
    val calculatorLabel = calculatorState.label
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        RotationShapeContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            inset = 16.dp,
            rotationDegrees = tiltAngle,
            shapeCalculator = shapeCalculator
        )

        return
        DebugRotationFrame(
            modifier = Modifier.fillMaxSize(),
            inset = 16.dp,
            rotationDegrees = tiltAngle,
            shapeCalculator = shapeCalculator,
            calculatorLabel = calculatorLabel,
            onSwitchCalculator = { calculatorState = calculatorState.next() }
        )

        // dont delete - прост опока он не нужен

//            SensorRotationTopAppBar(
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .padding(top = rememberStatusBarHeight()),
//                title = stringResource(R.string.sensor_rotation_demo),
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
