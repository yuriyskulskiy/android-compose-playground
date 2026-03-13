package com.skul.yuriy.composeplayground.feature.metaballEdgesAdvanced

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun MetaballEdgeAdvancedBottomBar(
    settings: MetaballEdgeAdvancedSettingsState,
    onBlurRadiusDecrease: () -> Unit,
    onBlurRadiusIncrease: () -> Unit,
    onThresholdDecrease: () -> Unit,
    onThresholdIncrease: () -> Unit,
    onTextSizeDecrease: () -> Unit,
    onTextSizeIncrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .navigationBarsPadding()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MetaballEdgeAdvancedSettingRow(
            label = "Blur radius",
            value = settings.blurRadius.value.toInt().toString(),
            onDecrease = onBlurRadiusDecrease,
            onIncrease = onBlurRadiusIncrease,
        )
        MetaballEdgeAdvancedDivider()
        MetaballEdgeAdvancedSettingRow(
            label = "Alpha filter",
            value = "${settings.thresholdPercent}%",
            onDecrease = onThresholdDecrease,
            onIncrease = onThresholdIncrease,
        )
        MetaballEdgeAdvancedDivider()
        MetaballEdgeAdvancedSettingRow(
            label = "Text size",
            value = settings.textSize.value.toInt().toString(),
            onDecrease = onTextSizeDecrease,
            onIncrease = onTextSizeIncrease,
        )
    }
}

internal fun Dp.nextBlurStep(): Dp = value.toInt().nextStepIn(BlurRadiusSteps).dp

internal fun Dp.previousBlurStep(): Dp = value.toInt().previousStepIn(BlurRadiusSteps).dp

internal fun TextUnit.nextTextSizeStep(): TextUnit = value.toInt().nextStepIn(TextSizeSteps).sp

internal fun TextUnit.previousTextSizeStep(): TextUnit = value.toInt().previousStepIn(TextSizeSteps).sp

internal fun Int.nextThresholdStep(): Int = nextStepIn(ThresholdSteps)

internal fun Int.previousThresholdStep(): Int = previousStepIn(ThresholdSteps)

private fun Int.nextStepIn(steps: List<Int>): Int {
    val index = steps.indexOf(this)
    val nextIndex = if (index == -1) 0 else (index + 1) % steps.size
    return steps[nextIndex]
}

private fun Int.previousStepIn(steps: List<Int>): Int {
    val index = steps.indexOf(this)
    val previousIndex = if (index == -1) steps.lastIndex else (index - 1 + steps.size) % steps.size
    return steps[previousIndex]
}

private val TextSizeSteps = listOf(16, 18, 20, 22, 24, 26, 28, 30)
private val BlurRadiusSteps = listOf(2, 4, 6, 8, 10, 12, 14, 16, 32, 48)
private val ThresholdSteps = listOf(10, 20, 25, 30, 40)

@Composable
private fun MetaballEdgeAdvancedSettingRow(
    label: String,
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetaballEdgeAdvancedSettingLabel(
            label = label,
            modifier = Modifier.weight(1f),
        )
        MetaballEdgeAdvancedSettingControls(
            value = value,
            onDecrease = onDecrease,
            onIncrease = onIncrease,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MetaballEdgeAdvancedDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.2f))
    )
}

@Composable
private fun MetaballEdgeAdvancedSettingLabel(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        modifier = modifier,
        color = Color.White,
    )
}

@Composable
private fun MetaballEdgeAdvancedSettingControls(
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetaballEdgeAdvancedStepButton(
            symbol = "-",
            onClick = onDecrease,
        )
        Spacer(Modifier.width(8.dp))
        MetaballEdgeAdvancedSettingValue(value = value)
        Spacer(Modifier.width(8.dp))
        MetaballEdgeAdvancedStepButton(
            symbol = "+",
            onClick = onIncrease,
        )
    }
}

@Composable
private fun MetaballEdgeAdvancedSettingValue(value: String) {
    Text(
        text = value,
        color = Color.White,
    )
}

@Composable
private fun MetaballEdgeAdvancedStepButton(
    symbol: String,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(
            text = symbol,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
