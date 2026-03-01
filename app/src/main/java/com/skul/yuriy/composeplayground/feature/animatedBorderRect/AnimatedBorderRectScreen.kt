package com.skul.yuriy.composeplayground.feature.animatedBorderRect

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.util.cornerRedLinearGradient
import com.skul.yuriy.composeplayground.util.fadingTopBottomEdgesDp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedBorderRectScreen() {
    var isBorderEnabled by remember { mutableStateOf(false) }
    val navBackStack = LocalNavBackStack.current
    val scrollState = rememberScrollState()
    val isAtTop by remember { derivedStateOf { scrollState.value == 0 } }
    val isAtBottom by remember { derivedStateOf { scrollState.value == scrollState.maxValue } }
    val topFadeHeight by animateDpAsState(targetValue = if (isAtTop) 0.dp else 24.dp, label = "")
    val bottomFadeHeight by animateDpAsState(
        targetValue = if (isAtBottom) 0.dp else 24.dp,
        label = ""
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = cornerRedLinearGradient(), alpha = 1f)
            .drawBehind {
                drawRect(color = Black.copy(alpha = 0.6f))
            },
        containerColor = White.copy(alpha = 0f),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White.copy(alpha = 0f),
                    navigationIconContentColor = White,
                    titleContentColor = White,
                    actionIconContentColor = White
                ),
                navigationIcon = {
                    IconButton(onClick = { navBackStack.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                title = {
                    Text(text = stringResource(R.string.animated_border_rect))
                },
                actions = {
                    val inactiveColor = White.copy(alpha = 0.62f)
                    val iconColor = if (isBorderEnabled) White else inactiveColor

                    IconButton(onClick = { isBorderEnabled = !isBorderEnabled }) {
                        Icon(
                            imageVector = Icons.Outlined.CropSquare,
                            contentDescription = "Toggle border",
                            tint = iconColor
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ScreenContent(
            isBorderEnabled = isBorderEnabled,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .fadingTopBottomEdgesDp(
                    topFadeHeight = topFadeHeight,
                    bottomFadeHeight = bottomFadeHeight
                )
                .verticalScroll(scrollState)
                .padding(16.dp)
        )
    }
}
