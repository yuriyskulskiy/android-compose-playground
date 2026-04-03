package com.skul.yuriy.composeplayground.feature.sensorRotation.statusbar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
internal fun RotationStatusBarTimeWidget() {
    val timeText = rememberCurrentTimeText()
    val timeTextSize = 12.sp
    Text(
        text = timeText,
        color = Color.White,
        style = TextStyle(fontSize = timeTextSize),
    )
}

@Composable
private fun rememberCurrentTimeText(): String {
    val formatter = remember { SimpleDateFormat("H:mm", Locale.getDefault()) }
    var timeText by remember { mutableStateOf(formatter.format(Date())) }

    LaunchedEffect(formatter) {
        while (true) {
            timeText = formatter.format(Date())
            delay(60_000L)
        }
    }

    return timeText
}
