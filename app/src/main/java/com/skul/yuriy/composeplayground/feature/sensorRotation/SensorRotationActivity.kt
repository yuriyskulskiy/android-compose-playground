package com.skul.yuriy.composeplayground.feature.sensorRotation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.ui.theme.ComposePlaygroundTheme

class SensorRotationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//                WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
        setContent {
            ComposePlaygroundTheme {
                SensorRotationScreen(
                    onNavUp = { finish() }
                )
            }
        }
    }
}

@Composable
private fun SensorRotationScreen(
    onNavUp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .border(width = 1.dp, color = Color.Red)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val centerY = size.height / 2f
                drawLine(
                    color = Color.Black,
                    start = Offset(x = 0f, y = centerY),
                    end = Offset(x = size.width, y = centerY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // dont delete - прост опока он не нужен

//            SensorRotationTopAppBar(
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .padding(top = rememberStatusBarHeight()),
//                title = stringResource(R.string.sensor_rotation_demo),
//                onNavUp = onNavUp
//            )
//
//            Text(
//                modifier = Modifier.align(Alignment.Center),
//                text = stringResource(R.string.sensor_rotation_demo_placeholder),
//                color = Color.Black
//            )
        }
    }
}

















@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SensorRotationTopAppBar(
    title: String,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            navigationIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(
                onClick = onNavUp,
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
        },
        title = {
            Text(
                text = title,
                color = Color.White
            )
        }
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
