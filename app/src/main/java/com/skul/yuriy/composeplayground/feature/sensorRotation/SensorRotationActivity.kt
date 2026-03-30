package com.skul.yuriy.composeplayground.feature.sensorRotation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.skul.yuriy.composeplayground.R
import com.skul.yuriy.composeplayground.ui.theme.ComposePlaygroundTheme
import com.skul.yuriy.composeplayground.util.cornerDarkRedLinearGradient2
import com.skul.yuriy.composeplayground.util.regularComponents.CustomTopAppBar

class SensorRotationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            .background(brush = cornerDarkRedLinearGradient2())
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CustomTopAppBar(
            modifier = Modifier.align(Alignment.TopCenter),
            title = stringResource(R.string.sensor_rotation_demo),
            onNavUp = onNavUp,
            containerColor = Color.Transparent
        )

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(R.string.sensor_rotation_demo_placeholder),
            color = Color.White
        )
    }
}
