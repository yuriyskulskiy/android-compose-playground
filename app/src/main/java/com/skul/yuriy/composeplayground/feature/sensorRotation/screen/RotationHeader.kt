package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val RotationHostTopBarHeight = 64.dp
private val RotationHostHorizontalContentPadding = 14.dp
private val RotationHostStatusBarStartPadding = 20.dp
private val RotationHostStatusBarEndPadding = 20.dp

@Composable
internal fun RotationHeader(
    title: String,
    onNavUp: () -> Unit,
    sourceLabel: String,
    onSourceClick: () -> Unit,
    statusBarHeight: Dp,
    statusBarStartInset: Dp,
    statusBarEndInset: Dp,
    topBarStartInset: Dp,
    topBarEndInset: Dp,
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
        HorizontalDivider(color = Color.Red, thickness = 2.dp)
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
