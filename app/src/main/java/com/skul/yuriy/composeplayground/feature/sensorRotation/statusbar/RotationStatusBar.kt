package com.skul.yuriy.composeplayground.feature.sensorRotation.statusbar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.text.format.DateFormat
import java.util.Date
import kotlinx.coroutines.delay

internal val RotationHostStatusBarHeight = 28.dp
private val RotationHostStatusBarStartPadding = 22.dp
private val RotationHostStatusBarEndPadding = 20.dp

@Composable
internal fun rememberStatusBarHeight() = RotationHostStatusBarHeight

@Composable
internal fun FakeRotationStatusBar(
    modifier: Modifier = Modifier,
) {
    val timeText = rememberCurrentTimeText()
    val batteryStatus = rememberBatteryStatus()
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
            text = timeText,
            color = Color.White,
            fontSize = 13.sp,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 12.dp, height = 8.dp)
                        .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .padding(1.dp)
                            .size(
                                width = (10f * (batteryStatus.level / 100f)).coerceIn(1f, 10f).dp,
                                height = 6.dp,
                            )
                            .clip(RoundedCornerShape(1.dp))
                            .background(
                                if (batteryStatus.isCharging) Color(0xFF7CFF8A) else Color.White
                            )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = 6.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                )
            }
            if (batteryStatus.isCharging) {
                Icon(
                    imageVector = Icons.Filled.Bolt,
                    contentDescription = "Charging",
                    tint = Color(0xFF7CFF8A),
                    modifier = Modifier.size(10.dp),
                )
            }
            Text(
                text = "${batteryStatus.level}%",
                color = Color.White,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun rememberCurrentTimeText(): String {
    val context = LocalContext.current
    val formatter = remember(context) { DateFormat.getTimeFormat(context) }
    var timeText by remember { mutableStateOf(formatter.format(Date())) }

    LaunchedEffect(formatter) {
        while (true) {
            timeText = formatter.format(Date())
            delay(60_000L)
        }
    }

    return timeText
}

@Composable
private fun rememberBatteryStatus(): BatteryStatus {
    val context = LocalContext.current
    var batteryStatus by remember { mutableStateOf(readBatteryStatus(context)) }

    DisposableEffect(context) {
        val receiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    batteryStatus = readBatteryStatus(context ?: return)
                }
            }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        onDispose { context.unregisterReceiver(receiver) }
    }

    return batteryStatus
}

private fun readBatteryStatus(context: Context): BatteryStatus {
    val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 100) ?: 100
    val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
    val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        ?: BatteryManager.BATTERY_STATUS_UNKNOWN
    val percentage =
        if (scale > 0) ((level * 100f) / scale).toInt().coerceIn(0, 100) else 100
    val isCharging =
        status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
    return BatteryStatus(
        level = percentage,
        isCharging = isCharging,
    )
}

private data class BatteryStatus(
    val level: Int,
    val isCharging: Boolean,
)
