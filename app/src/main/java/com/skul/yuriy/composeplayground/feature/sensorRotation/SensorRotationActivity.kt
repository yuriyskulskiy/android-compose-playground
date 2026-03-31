package com.skul.yuriy.composeplayground.feature.sensorRotation

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.skul.yuriy.composeplayground.feature.sensorRotation.screen.SensorRotationScreen
import com.skul.yuriy.composeplayground.ui.theme.ComposePlaygroundTheme

class SensorRotationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Log.wtf("SensorRotationActivity", "onCreate")
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

    override fun onStart() {
        super.onStart()
        Log.wtf("SensorRotationActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.wtf("SensorRotationActivity", "onResume")
    }

    override fun onPause() {
        Log.wtf("SensorRotationActivity", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.wtf("SensorRotationActivity", "onStop")
        super.onStop()
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
