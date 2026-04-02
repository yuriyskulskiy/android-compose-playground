package com.skul.yuriy.composeplayground.feature.sensorRotation.statusbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RotationHostStatusBarStartPadding = 20.dp
private val RotationHostStatusBarEndPadding = 20.dp

@Composable
internal fun rememberStatusBarHeight() = with(LocalContext.current.resources) {
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
internal fun FakeRotationStatusBar(
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
