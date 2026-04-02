package com.skul.yuriy.composeplayground.feature.sensorRotation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.feature.sensorRotation.statusbar.FakeRotationStatusBar

internal val RotationHostTopBarHeight = 64.dp

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
