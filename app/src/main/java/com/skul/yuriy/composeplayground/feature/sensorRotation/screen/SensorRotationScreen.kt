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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.RotationShapeText

@Composable
fun SensorRotationScreen(
    onNavUp: () -> Unit
) {
    val tiltAngle = rememberRotationAngle()
    var calculatorState by rememberSaveable { mutableStateOf(CalculatorUiState.AspectSlide) }
    val shapeCalculator: IRotationShapeCalculator = remember(calculatorState) {
        calculatorState.createCalculator()
    }
    val rotateContentWithShape = calculatorState.rotateContentWithShape
    val baseText = stringResource(R.string.very_long_mock_text).trimIndent().trim()
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
//            rotateContentWithShape = rotateContentWithShape,
            rotateContentWithShape = true,
        ) {
            RotationShapeText(
                text = demoText,
                modifier = Modifier.fillMaxSize()
            )
        }

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
