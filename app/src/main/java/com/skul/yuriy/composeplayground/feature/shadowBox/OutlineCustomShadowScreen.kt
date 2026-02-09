package com.skul.yuriy.composeplayground.feature.shadowBox

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skul.yuriy.composeplayground.LocalNavBackStack
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.navigation.navigateUp
import com.skul.yuriy.composeplayground.util.ScreenBackground


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OutlineShadowBoxRoute() {
    var showBackground by remember { mutableStateOf(true) }

    ScreenBackground(
        showBackground = showBackground,
        modifier = Modifier.fillMaxSize(),
//        imageRes = R.drawable.test2,
        imageRes = R.drawable.gtr_34_1,
        colorFilter = ColorFilter.tint(
            Color.White.copy(alpha = 0.5f),  // Light white tint with 50% default opacity
            blendMode = BlendMode.Lighten    // Blend mode to lighten the background
        ),
    ) {
        OutlineShadowBoxScreen(
            modifier = Modifier.fillMaxSize(),
            onToggleClick = { showBackground = !showBackground },
            showBackground = showBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlineShadowBoxScreen(
    modifier: Modifier,
    onToggleClick: () -> Unit,
    showBackground: Boolean
) {
    val navBackStack = LocalNavBackStack.current

    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = Color.Transparent),
                modifier = Modifier
                    .shadow(elevation = 0.dp, clip = false),
                title = { Text("Shadow box") },
                actions = {
                    IconButton(onClick = onToggleClick) {
                        Icon(
                            painter = painterResource(
                                id = if (showBackground) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24
                            ), contentDescription = "Toggle background visibility"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navBackStack.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
            )

        }
    ) {
        ScreenContent(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(it)
        )
    }
}



