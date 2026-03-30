package com.skul.yuriy.composeplayground.feature.sensorRotation

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.AccelerometerRotationAngleSource
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.OrientationEventRotationAngleSource
import com.skul.yuriy.composeplayground.feature.sensorRotation.sensor.RotationAngleSourceType
import com.skul.yuriy.composeplayground.ui.theme.ComposePlaygroundTheme

class SensorRotationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

@Composable
private fun SensorRotationScreen(
    onNavUp: () -> Unit
) {
    val tiltAngle = rememberRotationAngle()
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = tiltAngle
                        transformOrigin = TransformOrigin.Center
                    }
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

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Tilt: ${"%.1f".format(tiltAngle)}°",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
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

@Composable
private fun rememberRotationAngle(
    sourceType: RotationAngleSourceType = DEFAULT_ROTATION_ANGLE_SOURCE             // old
//    sourceType: RotationAngleSourceType = ALTERNATIVE_ROTATION_ANGLE_SOURCE           //new
): Float {
    val context = LocalContext.current
    val angleSource = remember(context, sourceType) {
        when (sourceType) {
            RotationAngleSourceType.Accelerometer -> AccelerometerRotationAngleSource(context)
            RotationAngleSourceType.OrientationEventListener -> {
                OrientationEventRotationAngleSource(context)
            }
        }
    }
    var tiltAngle by remember { mutableFloatStateOf(0f) }
    val currentOnAngleChanged by rememberUpdatedState<(Float) -> Unit> { tiltAngle = it }

    DisposableEffect(angleSource) {
        angleSource.start { angle ->
            currentOnAngleChanged(angle)
        }
        onDispose(angleSource::stop)
    }

    return tiltAngle
}
private val DEFAULT_ROTATION_ANGLE_SOURCE = RotationAngleSourceType.Accelerometer
private val ALTERNATIVE_ROTATION_ANGLE_SOURCE = RotationAngleSourceType.OrientationEventListener

















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
