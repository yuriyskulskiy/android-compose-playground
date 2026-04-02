package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.sensorRotation.shape.IRotationShapeCalculator
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.RotationShapeText
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombustext.RhombusText
import com.skul.yuriy.composeplayground.feature.sensorRotation.text.rhombustext.RhombusTextLayoutConfig
import kotlin.math.roundToInt

private val RotationHostTopBarHeight = 64.dp
private val RotationHostHorizontalContentPadding = 14.dp
private val RotationHostStatusBarStartPadding = 20.dp
private val RotationHostStatusBarEndPadding = 20.dp
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
    var calculatorState by rememberSaveable { mutableStateOf(CalculatorUiState.TwoPhaseSlide) }
    val shapeCalculator: IRotationShapeCalculator = remember(calculatorState) {
        calculatorState.createCalculator()
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
                    calculatorState = calculatorState.next()
                }
            }
    ) {
        RotationShapeContainer(
            modifier = Modifier
                .fillMaxSize(),
            inset = 0.dp,
            rotationDegrees = currentAngle,
            shapeCalculator = shapeCalculator,
            rotateContentWithShape = calculatorState.rotateContentWithShape,
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
                        calculatorState = calculatorState,
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
                    selectedState = calculatorState,
                    onPatternClick = { calculatorState = it },
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

@Composable
private fun RotationHeader(
    title: String,
    onNavUp: () -> Unit,
    sourceLabel: String,
    onSourceClick: () -> Unit,
    statusBarHeight: androidx.compose.ui.unit.Dp,
    statusBarStartInset: androidx.compose.ui.unit.Dp,
    statusBarEndInset: androidx.compose.ui.unit.Dp,
    topBarStartInset: androidx.compose.ui.unit.Dp,
    topBarEndInset: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FakeRotationStatusBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(statusBarHeight)
                .padding(start = statusBarStartInset, end = statusBarEndInset),
        )
        SensorRotationTopAppBar(
            title = title,
            onNavUp = onNavUp,
            sourceLabel = sourceLabel,
            onSourceClick = onSourceClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(RotationHostTopBarHeight)
                .padding(top = 1.dp)
                .padding(start = topBarStartInset, end = topBarEndInset),
        )
        HorizontalDivider(color = Color.White)
    }
}

@Composable
private fun RotationTextContent(
    text: String,
    calculatorState: CalculatorUiState,
    textLayoutInfo: RotationShapeTextLayoutInfo,
    statusBarHeight: androidx.compose.ui.unit.Dp,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SensorRotationTopAppBar(
    title: String,
    onNavUp: () -> Unit,
    sourceLabel: String,
    onSourceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White,
        ),
        navigationIcon = {
            IconButton(
                onClick = onNavUp,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go Back",
                )
            }
        },
        title = {
            Text(
                text = title,
                color = Color.White,
            )
        },
        actions = {
            TextButton(onClick = onSourceClick) {
                Text(
                    text = sourceLabel,
                    color = Color.White,
                )
            }
        },
    )
}

@Composable
private fun rememberStatusBarHeight() = with(LocalContext.current.resources) {
    val density = LocalDensity.current
    val statusBarHeightResId = remember {
        getIdentifier("status_bar_height", "dimen", "android")
    }
    if (statusBarHeightResId > 0) {
        with(density) { getDimensionPixelSize(statusBarHeightResId).toDp() }
    } else {
        0.dp
    }
}

@Composable
private fun FakeRotationStatusBar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .padding(
                top = 2.dp,
                start = RotationHostStatusBarStartPadding,
                end = RotationHostStatusBarEndPadding,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "9:41",
            color = Color.White,
            fontSize = 13.sp,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 12.dp, height = 8.dp)
                    .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(2.dp))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.85f))
            )
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )
        }
    }
}
