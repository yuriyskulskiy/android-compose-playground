package com.skul.yuriy.composeplayground.feature.metaballEdgeText.conceptSubscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.R
import kotlin.math.roundToInt

private const val MaxBlurRadiusDp = 30f

@Composable
internal fun ConceptControlsBottomBar(
    blurRadiusDp: Float,
    onBlurRadiusChange: (Float) -> Unit,
    blurEnabled: Boolean,
    onBlurEnabledChange: (Boolean) -> Unit,
    alphaFilterPercent: Float,
    onAlphaFilterChange: (Float) -> Unit,
    alphaEnabled: Boolean,
    onAlphaEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerSize = LocalWindowInfo.current.containerSize
    val isLandscape = containerSize.width > containerSize.height

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp
            )
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    SettingSection(
                        label = stringResource(R.string.blur_radius_dp),
                        valueText = blurRadiusDp.roundToInt().toString(),
                        sliderValue = blurRadiusDp,
                        onSliderValueChange = onBlurRadiusChange,
                        sliderRange = 0f..MaxBlurRadiusDp,
                        sliderStartLabel = "0",
                        sliderEndLabel = MaxBlurRadiusDp.roundToInt().toString(),
                        enabled = blurEnabled,
                        onEnabledChange = onBlurEnabledChange,
                        modifier = Modifier.weight(1f)
                    )
                    VerticalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxHeight()
                    )
                    SettingSection(
                        label = stringResource(R.string.alpha_filter),
                        valueText = "${alphaFilterPercent.roundToInt()}%",
                        sliderValue = alphaFilterPercent,
                        onSliderValueChange = onAlphaFilterChange,
                        sliderRange = 0f..100f,
                        sliderStartLabel = "0%",
                        sliderEndLabel = "100%",
                        enabled = alphaEnabled,
                        onEnabledChange = onAlphaEnabledChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                SettingSection(
                    label = stringResource(R.string.blur_radius_dp),
                    valueText = blurRadiusDp.roundToInt().toString(),
                    sliderValue = blurRadiusDp,
                    onSliderValueChange = onBlurRadiusChange,
                    sliderRange = 0f..MaxBlurRadiusDp,
                    sliderStartLabel = "0",
                    sliderEndLabel = MaxBlurRadiusDp.roundToInt().toString(),
                    enabled = blurEnabled,
                    onEnabledChange = onBlurEnabledChange,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )
                SettingSection(
                    label = stringResource(R.string.alpha_filter),
                    valueText = "${alphaFilterPercent.roundToInt()}%",
                    sliderValue = alphaFilterPercent,
                    onSliderValueChange = onAlphaFilterChange,
                    sliderRange = 0f..100f,
                    sliderStartLabel = "0%",
                    sliderEndLabel = "100%",
                    enabled = alphaEnabled,
                    onEnabledChange = onAlphaEnabledChange,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingSection(
    label: String,
    valueText: String,
    sliderValue: Float,
    onSliderValueChange: (Float) -> Unit,
    sliderRange: ClosedFloatingPointRange<Float>,
    sliderStartLabel: String,
    sliderEndLabel: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = valueText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = onSliderValueChange,
            valueRange = sliderRange,
            enabled = enabled
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = sliderStartLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = sliderEndLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )
        }
    }
}
