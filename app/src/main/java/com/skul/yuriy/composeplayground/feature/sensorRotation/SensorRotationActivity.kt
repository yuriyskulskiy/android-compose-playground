package com.skul.yuriy.composeplayground.feature.sensorRotation

import android.os.Bundle
import android.os.Build
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.skul.yuriy.composeplayground.feature.sensorRotation.screen.SensorRotationScreen
import com.skul.yuriy.composeplayground.ui.theme.ComposePlaygroundTheme

class SensorRotationActivity : ComponentActivity() {
    companion object {
        const val EXTRA_INITIAL_DISPLAY_ROTATION = "initial_display_rotation"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val openingRotation = intent.getIntExtra(
            EXTRA_INITIAL_DISPLAY_ROTATION,
            currentDisplayRotation()
        )
        val openingRotationDegrees = displayRotationDegrees(openingRotation)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                //hide real status bar and use fake one for rotation
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            ComposePlaygroundTheme {
                SensorRotationScreen(
                    initialAngle = openingRotationDegrees,
                    onNavUp = { finish() }
                )
            }
        }
    }

    private fun displayRotationDegrees(rotation: Int): Float =
        when (rotation) {
            Surface.ROTATION_90 -> -90f
            Surface.ROTATION_270 -> 90f
            else -> 0f
        }

    @Suppress("DEPRECATION")
    private fun currentDisplayRotation(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.rotation ?: Surface.ROTATION_0
        } else {
            windowManager.defaultDisplay?.rotation ?: Surface.ROTATION_0
        }
}
