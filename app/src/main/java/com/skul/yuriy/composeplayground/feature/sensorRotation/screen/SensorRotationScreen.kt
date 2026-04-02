package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator
import kotlin.math.roundToInt

private val RotationPatternButtonsBottomInset = 4.dp

@Composable
fun SensorRotationScreen(
    initialAngle: Float,
    onNavUp: () -> Unit
) {
    var smoothingState by rememberSaveable { mutableStateOf(SmoothingUiState.AnimateTo) }
    var sourceState by rememberSaveable { mutableStateOf(RotationSourceUiState.AngleListener) }
    val tiltAngle = rememberRotationAngle(
        sourceType = sourceState.sourceType,
        smoothingType = smoothingState
    )
    var viewportPattern by rememberSaveable { mutableStateOf(RotationViewportPattern.TwoPhaseSlide) }
    val shapeCalculator: IRotationShapeCalculator = remember(viewportPattern) {
        viewportPattern.createCalculator()
    }
    val baseText = stringResource(R.string.sensor_rotation_demo_text)
    val demoText = remember(baseText) { "$baseText\n\n$baseText" }
    val currentAngle = tiltAngle ?: initialAngle
    val topBarTitle = "${currentAngle.roundToInt()}°"
    val patternItems = rememberRotationPatternItems()
    val switchSourceMode = {
        sourceState = sourceState.next()
        smoothingState =
            when (sourceState) {
                RotationSourceUiState.RawSensor -> SmoothingUiState.SmoothAlpha
                RotationSourceUiState.AngleListener -> SmoothingUiState.AnimateTo
            }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(2.dp)
            .border(
                width = 2.dp,
                color = Color.Red,
                shape = RoundedCornerShape(
                    topStart = 28.dp,
                    topEnd = 28.dp,
                    bottomStart = 26.dp,
                    bottomEnd = 26.dp
                ),
            )
            .pointerInput(Unit) {
                detectTapGestures {
                    viewportPattern = viewportPattern.next()
                }
            }
    ) {
        RotationHost(
            modifier = Modifier
                .fillMaxSize(),
            inset = 0.dp,
            rotationDegrees = currentAngle,
            shapeCalculator = shapeCalculator,
            rotateContentWithShape = viewportPattern.rotateContentWithShape,
        ) { textLayoutInfo ->
            val density = LocalDensity.current
            val rotationHostStatusBarHeight = rememberStatusBarHeight()
            val topBarStartInset = with(density) {
                val topBarLocalY =
                    rotationHostStatusBarHeight.toPx() + RotationHostTopBarHeight.toPx() / 2f
                val shiftAtTopBar =
                    textLayoutInfo.firstLineOffset.toPx() +
                            topBarLocalY * textLayoutInfo.horizontalShiftPerHeight
                maxOf(0f, shiftAtTopBar).toDp()
            }
            val topBarEndInset = with(density) {
                val topBarLocalY =
                    rotationHostStatusBarHeight.toPx() + RotationHostTopBarHeight.toPx() / 2f
                val leftBoundaryAtTopBar =
                    textLayoutInfo.firstLineOffset.toPx() +
                            topBarLocalY * textLayoutInfo.horizontalShiftPerHeight
                val rightBoundaryAtTopBar = leftBoundaryAtTopBar + textLayoutInfo.lineWidth.toPx()
                (textLayoutInfo.contentWidth.toPx() - rightBoundaryAtTopBar)
                    .coerceAtLeast(0f)
                    .toDp()
            }
            val statusBarStartInset = with(density) {
                val statusBarLocalY = rotationHostStatusBarHeight.toPx() / 2f
                val shiftAtStatusBar =
                    textLayoutInfo.firstLineOffset.toPx() +
                            statusBarLocalY * textLayoutInfo.horizontalShiftPerHeight
                maxOf(0f, shiftAtStatusBar).toDp()
            }
            val statusBarEndInset = with(density) {
                val statusBarLocalY = rotationHostStatusBarHeight.toPx() / 2f
                val leftBoundaryAtStatusBar =
                    textLayoutInfo.firstLineOffset.toPx() +
                            statusBarLocalY * textLayoutInfo.horizontalShiftPerHeight
                val rightBoundaryAtStatusBar =
                    leftBoundaryAtStatusBar + textLayoutInfo.lineWidth.toPx()
                (textLayoutInfo.contentWidth.toPx() - rightBoundaryAtStatusBar)
                    .coerceAtLeast(0f)
                    .toDp()
            }
            val bottomPanelStartInset = with(density) {
                val panelCenterY =
                    textLayoutInfo.contentHeight.toPx() -
                            RotationPatternButtonsBottomInset.toPx() -
                            RotationPatternButtonSize.toPx() / 2f
                val panelStart =
                    textLayoutInfo.firstLineOffset.toPx() +
                            panelCenterY * textLayoutInfo.horizontalShiftPerHeight
                val maxStart =
                    (textLayoutInfo.contentWidth - textLayoutInfo.lineWidth).toPx()
                        .coerceAtLeast(0f)
                panelStart.coerceIn(0f, maxStart).toDp()
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    RotationHeader(
                        title = topBarTitle,
                        onNavUp = onNavUp,
                        sourceLabel = sourceState.label,
                        onSourceClick = switchSourceMode,
                        statusBarHeight = rotationHostStatusBarHeight,
                        statusBarStartInset = statusBarStartInset,
                        statusBarEndInset = statusBarEndInset,
                        topBarStartInset = topBarStartInset,
                        topBarEndInset = topBarEndInset,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                    )

                    RotationTextContent(
                        text = demoText,
                        angleDegrees = currentAngle,
                        viewportPattern = viewportPattern,
                        textLayoutInfo = textLayoutInfo,
                        statusBarHeight = rotationHostStatusBarHeight,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(Color.White),
                    )
                }

                RotationPatternControlBar(
                    items = patternItems,
                    selectedState = viewportPattern,
                    onPatternClick = { viewportPattern = it },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = bottomPanelStartInset,
                            bottom = RotationPatternButtonsBottomInset,
                        )
                        .width(textLayoutInfo.lineWidth),
                )
            }
        }
    }
}
