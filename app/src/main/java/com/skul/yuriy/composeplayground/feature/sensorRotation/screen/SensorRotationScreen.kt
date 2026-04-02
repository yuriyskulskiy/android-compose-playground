package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.math.roundToInt

private val RotationHostTopBarHeight = 64.dp
private val RotationPatternButtonSize = 40.dp
private val RotationPatternButtonsBottomInset = 4.dp
private val SelectedRotationPatternTint = Color.Red
private val DefaultRotationPatternTint = Color.White
private val PressedRotationPatternTint = Color(0xFFF2F2F2)
private val PressedSelectedRotationPatternTint = Color(0xFFFF9A9A)
private val DefaultRotationPatternBackground = Color.Black
private val PressedRotationPatternBackground = Color(0xFF3A3A3A)
private val RotationPatternBorderColor = Color.White.copy(alpha = 0.2f)

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
    val topBarTitle = "${tiltAngle.roundToInt()}°"
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
            val topBarEndInset = with(density) {
                val topBarLocalY = RotationHostTopBarHeight.toPx() / 2f
                val leftBoundaryAtTopBar =
                    textLayoutInfo.firstLineOffset.toPx() +
                            topBarLocalY * textLayoutInfo.horizontalShiftPerHeight
                val rightBoundaryAtTopBar = leftBoundaryAtTopBar + textLayoutInfo.lineWidth.toPx()
                (textLayoutInfo.contentWidth.toPx() - rightBoundaryAtTopBar)
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
                    Box(
                        modifier = Modifier
                            .height(RotationHostTopBarHeight)
                            .fillMaxWidth()
                            .background(Color.Black)
                    ) {
                        CustomTopAppBar(
                            title = topBarTitle,
                            onNavUp = onNavUp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = topBarStartInset, end = topBarEndInset),
                            enableHorizontalDivider = false,
                            actions = {
                                TextButton(
                                    onClick = switchSourceMode
                                ) {
                                    Text(
                                        text = sourceState.label,
                                        color = Color.White,
                                    )
                                }
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
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

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = bottomPanelStartInset,
                            bottom = RotationPatternButtonsBottomInset,
                        )
                        .width(textLayoutInfo.lineWidth),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RotationPatternIconButton(
                        imageVector = RotationPatternIcons.TwoPhase,
                        contentDescription = "Two-phase pattern",
                        isSelected = calculatorState == CalculatorUiState.TwoPhaseSlide,
                        onClick = { calculatorState = CalculatorUiState.TwoPhaseSlide },
                    )
                    RotationPatternIconButton(
                        imageVector = RotationPatternIcons.Aspect,
                        contentDescription = "Aspect pattern",
                        isSelected = calculatorState == CalculatorUiState.AspectSlide,
                        onClick = { calculatorState = CalculatorUiState.AspectSlide },
                    )
                    RotationPatternIconButton(
                        imageVector = RotationPatternIcons.Fitted,
                        contentDescription = "Rectangle pattern",
                        isSelected = calculatorState == CalculatorUiState.MorphingRect,
                        onClick = { calculatorState = CalculatorUiState.MorphingRect },
                    )
                    RotationPatternIconButton(
                        imageVector = RotationPatternIcons.Morph,
                        contentDescription = "Fitted pattern",
                        isSelected = calculatorState == CalculatorUiState.FittedMorphingRect,
                        onClick = { calculatorState = CalculatorUiState.FittedMorphingRect },
                    )
                }
            }
        }

    }
}

@Composable
private fun RotationPatternIconButton(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val iconTint =
        when {
            isSelected && isPressed -> PressedSelectedRotationPatternTint
            isSelected -> SelectedRotationPatternTint
            isPressed -> PressedRotationPatternTint
            else -> DefaultRotationPatternTint
        }
    val backgroundColor =
        if (isPressed) PressedRotationPatternBackground else DefaultRotationPatternBackground
    Box(
        modifier = Modifier
            .then(modifier)
            .size(RotationPatternButtonSize)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = RotationPatternBorderColor,
                shape = CircleShape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = iconTint,
        )
    }
}
