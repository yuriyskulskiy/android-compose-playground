package com.skul.yuriy.composeplayground.feature.sensorRotation.statusbar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal val RotationHostStatusBarHeight = 28.dp
private val RotationHostStatusBarStartPadding = 22.dp
private val RotationHostStatusBarEndPadding = 20.dp

@Composable
internal fun rememberStatusBarHeight() = RotationHostStatusBarHeight

@Composable
internal fun FakeRotationStatusBar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .padding(
                top = 3.dp,
                start = RotationHostStatusBarStartPadding,
                end = RotationHostStatusBarEndPadding,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RotationStatusBarTimeWidget()
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RotationStatusBarWifiWidget()
            RotationStatusBarBatteryWidget()
        }
    }
}
